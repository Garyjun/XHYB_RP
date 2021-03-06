package com.brainsoon.search.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.common.util.dofile.zip.ZipUtil;
import com.brainsoon.resource.service.IBatchImportResService;
import com.brainsoon.resource.support.ExcelDataDetailMK;
import com.brainsoon.search.service.ISearchService;
import com.brainsoon.semantic.ontology.model.CommonMetaData;
import com.brainsoon.semantic.ontology.model.ExtendMetaData;
import com.brainsoon.semantic.ontology.model.FilePath;
import com.brainsoon.semantic.ontology.model.MetaDataDC;
import com.brainsoon.semantic.schema.LomProperty;
import com.brainsoon.semantic.schema.common.SysMetadataDefinitionConstants;
import com.brainsoon.semantic.schema.ontology.MetadataDefinition;
import com.brainsoon.system.service.IDictNameService;
import com.brainsoon.system.service.IDictValueService;
import com.brainsoon.system.util.MetadataSupport;

@Service
public class SearchService extends BaseService implements ISearchService {

	protected Logger logger = LoggerFactory.getLogger(SearchService.class);

	private static final String ADVANCE_SEARCH_RESOURCE_DIC = WebappConfigUtil.getParameter("ADVANCE_SEARCH_RESOURCE_DIC");
	private static final String ADVANCE_SEARCH_RESOURCE_LIST = WebappConfigUtil.getParameter("ADVANCE_SEARCH_RESOURCE_LIST");
	private static final String ADVANCE_SEARCH_RESOURCE_Sub = WebappConfigUtil.getParameter("ADVANCE_SEARCH_RESOURCE_Sub");
	public static final String FILE_TEMP = StringUtils.replace(WebAppUtils.getTempDir(), "\\", "/");
	private final static SimpleDateFormat dateformat2 = new SimpleDateFormat("yyyyMMddHHmmssSSS");

	@Autowired
	private IBatchImportResService batchImportResService;
	
	@Autowired
	private IDictNameService dictNameService;
	
	@Autowired
	private IDictValueService dictValueService;

	@Override
	public String getAdvaceSearchQueryConditionDic(String eduPhase, String subject, String type,String xPath) {
		JSONArray jsonArr = new JSONArray();
		try {
			String url = ADVANCE_SEARCH_RESOURCE_DIC;
			String hUrl = ADVANCE_SEARCH_RESOURCE_Sub;
			HttpClientUtil http = new HttpClientUtil();
			String json = "";
			/**
			 * type:null， 学段 type:1，学科 type:2,知识点
			 */
			if (StringUtils.equals(type, "1")) {
					//url = url + "?educational_phase=" + eduPhase + "&type=" + type;
				url = hUrl + "?type=" + type+"&xPath="+xPath+","+eduPhase;
			} else if (StringUtils.equals(type, "2")) {
				url = url + "?educational_phase=" + eduPhase + "&subject=" + subject + "&type=" + type;
			}
			json = http.executeGet(url);
			JSONObject jsonObject = JSONObject.fromObject(json);
			JSONArray domains = jsonObject.getJSONArray("domains");
			if (domains.size() > 0) {
				for (int i = 0; i < domains.size(); i++) {
					JSONObject domain = domains.getJSONObject(i);
					JSONObject jsonObj = new JSONObject();
					if (StringUtils.equals(type, "2") && subject != null && eduPhase != null) {
						jsonObj.put("nodeId", domain.getString("nodeId"));
						jsonObj.put("pid", domain.getString("pid"));
						jsonObj.put("name", domain.getString("label"));
						jsonObj.put("objectId", domain.getString("objectId"));
						jsonObj.put("code", domain.getString("code"));
					} else {
						jsonObj.put("code", domain.getString("code"));
						jsonObj.put("name", domain.getString("label"));
					}
					jsonArr.add(jsonObj);
				}
			}
		} catch (Exception e) {
			logger.debug("高级查询出错，请检查服务接口" + ADVANCE_SEARCH_RESOURCE_DIC + "是否已开启");
		}

		return jsonArr.toString();
	}

	public File exportRes(List<MetaDataDC> dcs, String downType) {
		if (StringUtils.equals("metadata", downType)) {
			return generateMetadataExcel(dcs);
		}

		String resMainDir = SearchService.FILE_TEMP + UUID.randomUUID().toString() + File.separator;
		String zipName = SearchService.FILE_TEMP + dateformat2.format(new Date()) + ".zip";
		try {
			for (MetaDataDC metaDataDC : dcs) {
				List<FilePath> data = metaDataDC.getFileAndPaths();
				for (FilePath filePath : data) {
					String dir = filePath.getDir();
					String path = filePath.getPath();
					if (StringUtils.isNotBlank(dir)) {
						File resFile = new File(WebAppUtils.getWebAppBaseFileDirFR() + File.separator + path);
						if (resFile.exists()) {
							File destDir = new File(resMainDir + dir);
							if (!destDir.exists()) {
								destDir.mkdirs();
							}
							FileUtils.copyFileToDirectory(resFile, destDir);
						}
					}
				}
			}
		} catch (IOException e) {
			logger.error("文件错误：" + e.getMessage());
			e.printStackTrace();
		}

		/**
		 * 如果导出元数据+文件，将元数据excel文件复制到主目录下
		 */
		if (StringUtils.equals("metadata_file", downType)) {
			try {
				FileUtils.copyFileToDirectory(generateMetadataExcel(dcs), new File(resMainDir));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * 打包
		 */
		try {
			ZipUtil.zipFileOrFolder(resMainDir, zipName, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new File(zipName);
	}

	/**
	 * @param dcs
	 */
	public File generateMetadataExcel(List<MetaDataDC> dcs) {
		// 首先生成模板
		File templeteFile = null;

		List<LomProperty> props = batchImportResService.getPropertyByModule("EX");
		String path = this.getClass().getResource("/").getPath() + "resExportTemplete.xls";

		// 生成五个excel sheet
		templeteFile = createExcel(path, props, templeteFile, "同步资源");
		templeteFile = createExcel(path, props, templeteFile, "主题资源");
		templeteFile = createExcel(path, props, templeteFile, "知识点资源");
		templeteFile = createExcel(path, props, templeteFile, "拓展资源");
		templeteFile = createExcel(path, props, templeteFile, "教师专业发展");

		// 读取模板标识
		HSSFWorkbook workbook = null;
		try {
			workbook = new HSSFWorkbook(new FileInputStream(templeteFile));
		} catch (IOException e1) {
			logger.error(e1.getMessage());
		}
		HSSFSheet sheetTB = workbook.getSheet("同步资源");
		ExcelDataDetailMK[] markersTB = getMK(sheetTB);
		int rowNumTB = 2;

		HSSFSheet sheetZT = workbook.getSheet("主题资源");
		ExcelDataDetailMK[] markersZT = getMK(sheetZT);
		int rowNumZT = 2;

		HSSFSheet sheetZS = workbook.getSheet("知识点资源");
		ExcelDataDetailMK[] markersZS = getMK(sheetZS);
		int rowNumZS = 2;

		HSSFSheet sheetTZ = workbook.getSheet("拓展资源");
		ExcelDataDetailMK[] markersTZ = getMK(sheetTZ);
		int rowNumTZ = 2;

		HSSFSheet sheetJS = workbook.getSheet("教师专业发展");
		ExcelDataDetailMK[] markersJS = getMK(sheetJS);
		int rowNumJS = 2;

		// 写数据
		for (MetaDataDC metaDataDC : dcs) {
			CommonMetaData commonMeta = metaDataDC.getCommonMetaData();
			String module = "";
			if (null != commonMeta) {
				module = commonMeta.getModule();
			} else {
				continue;
			}
			ExtendMetaData extendMeta = metaDataDC.getExtendMetaData();
			if (module.equals("同步资源")) {
				rowNumTB = parseDataToExcel(sheetTB, markersTB, rowNumTB, metaDataDC, commonMeta, extendMeta);
			} else if (module.equals("主题资源")) {
				rowNumZT = parseDataToExcel(sheetZT, markersZT, rowNumZT, metaDataDC, commonMeta, extendMeta);
			} else if (module.equals("知识点资源")) {
				rowNumZS = parseDataToExcel(sheetZS, markersZS, rowNumZS, metaDataDC, commonMeta, extendMeta);
			} else if (module.equals("拓展资源")) {
				rowNumTZ = parseDataToExcel(sheetTZ, markersTZ, rowNumTZ, metaDataDC, commonMeta, extendMeta);
			} else if (module.equals("教师专业发展")) {
				rowNumJS = parseDataToExcel(sheetJS, markersJS, rowNumJS, metaDataDC, commonMeta, extendMeta);
			}
		}
		setAutoSize(sheetTB);
		setAutoSize(sheetZT);
		setAutoSize(sheetZS);
		setAutoSize(sheetTZ);
		setAutoSize(sheetJS);

		OutputStream out = null;
		try {
			out = new FileOutputStream(templeteFile);
			workbook.write(out);// 写入File
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}
		}

		return templeteFile;
	}

	protected void setAutoSize(HSSFSheet sheet) {
		HSSFRow row = sheet.getRow(0);
		int lastCell = row.getLastCellNum() - 1;
		for (int j = 0; j < lastCell; j++) {
			sheet.autoSizeColumn(j);
		}
	}

	protected int parseDataToExcel(HSSFSheet sheetTB, ExcelDataDetailMK[] markersTB, int rowNumTB, MetaDataDC metaDataDC, CommonMetaData commonMeta, ExtendMetaData extendMeta) {
		HSSFRow cRow = sheetTB.createRow(rowNumTB++);
		String name = "";
		int referClass = -1;
		String currentValue = "";
		int cellNum = 0;
		HSSFCell cCell;
		for (ExcelDataDetailMK excelDataDetailMK : markersTB) {
			name = excelDataDetailMK.name;
			referClass = excelDataDetailMK.referClass;
			if (referClass == 0) {
				currentValue = commonMeta.getCommonMetaValue(name);
			} else if (referClass == 1) {
				currentValue = extendMeta.getExtendMetaValue(name);
			} else if (name.equalsIgnoreCase("importXpathName")) {
				currentValue = metaDataDC.getImportXpathName();
			} else if (name.equalsIgnoreCase("knowledgeXpathName")) {
				currentValue = metaDataDC.getKnowledgeXpathName();
			}
			if (StringUtils.isNotBlank(currentValue)) {
				currentValue = batchImportResService.transformKeyForDesc(name, currentValue) + "";
				cCell = cRow.createCell(cellNum);
				cCell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cCell.setCellValue(currentValue);
			}

			cellNum++;
		}
		return rowNumTB;
	}

	protected ExcelDataDetailMK[] getMK(HSSFSheet sheet) {
		HSSFRow fieldsRow = sheet.getRow(0);
		HSSFRow markerRow = sheet.getRow(1);
		int lastCell = markerRow.getLastCellNum() - 1;
		HSSFCell tempCell = null;
		String tempValue = "";
		String[] tempValueArray;
		ExcelDataDetailMK marker;
		ExcelDataDetailMK[] markers = new ExcelDataDetailMK[lastCell + 1];
		for (int i = 0; i <= lastCell; i++) {
			tempCell = markerRow.getCell(i);
			tempValue = tempCell.getStringCellValue();
			tempValueArray = StringUtils.split(tempValue, ",");
			marker = new ExcelDataDetailMK(tempValueArray[0], Integer.parseInt(tempValueArray[1]), Integer.parseInt(tempValueArray[2]), Integer.parseInt(tempValueArray[3]),
					Integer.parseInt(tempValueArray[4]), fieldsRow.getCell(i).getStringCellValue());
			markers[i] = marker;
		}
		return markers;
	}

	/**
	 * 创建excel导出模板
	 * 
	 * @param path 最原始模板地址
	 * @param props
	 * @param outFile 要输出的excel文件
	 * @param sheetName 填充sheet名称
	 */
	public File createExcel(String path, List<LomProperty> props, File outFile, String sheetName) {
		File excel = new File(path);
		if (null != outFile) {
			excel = outFile;
		}
		HSSFWorkbook workbook = null;
		try {
			workbook = new HSSFWorkbook(new FileInputStream(excel));
		} catch (IOException e1) {
			logger.error(e1.getMessage());
		}
		HSSFSheet sheet = workbook.getSheet(sheetName);
		HSSFRow titleRow = sheet.getRow(0);

		HSSFCellStyle style = titleRow.getCell(0).getCellStyle();

		HSSFRow keyRow = sheet.createRow(1);

		// 添加新的单元格
		HSSFCell newCell = null;
		int len = props.size() - 1;
		LomProperty prop;
		String cellName = "";
		for (int i = 0; i <= len; i++) {
			prop = props.get(i);
			newCell = titleRow.createCell(i);
			cellName = prop.getNameCN();
			newCell.setCellValue(cellName);
			newCell.setCellStyle(style);
			newCell = keyRow.createCell(i);
			newCell.setCellValue(prop.getKey());
		}
		keyRow.setZeroHeight(true);

		if (null == outFile)
			outFile = new File(FILE_TEMP + File.separator + dateformat2.format(new Date()) + ".xls");
		OutputStream out = null;
		try {
			out = new FileOutputStream(outFile);
			workbook.write(out);// 写入File
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}
		}
		return outFile;
	}

	public static void main(String[] args) {
		SearchService s = new SearchService();
		System.err.println(s.getAdvaceSearchQueryConditionDic(null, null, null,null));
		System.err.println(s.getAdvaceSearchQueryConditionDic("P", null, "1","TB"));
		System.err.println(s.getAdvaceSearchQueryConditionDic("P", "S02", "2","TB"));
	}

	@Override
	public String getAllDefineMetaData(String resType, String flag) {
		JSONArray array = new JSONArray();
		if("all".equals(resType)){
			resType = null;
		}
		List<MetadataDefinition> metaDataList = new ArrayList<MetadataDefinition>();
		
		if("getAcrossStoreCondition".equals(flag)){
			metaDataList = MetadataSupport.getCommonMetadatas();
		}else{
			metaDataList = MetadataSupport.getMetadateDefinesByResType(resType);
			//metaDataList = null;
		}
		if(metaDataList!=null){
			for(MetadataDefinition metaData : metaDataList){
				 Map<String, Object> map = new HashMap<String, Object>();
				 int fieldType = metaData.getFieldType();
	    		 String fieldEnName = metaData.getFieldName();
	    		 String fieldZhName = metaData.getFieldZhName();
	    		 String groupId = metaData.getGroupId();  //分组id
	    		 String pattern = SysMetadataDefinitionConstants.FieldType.getValueByKey(fieldType); //数据格式
	    		 int queryModel = metaData.getQueryModel();
	    		 String fieldValues = metaData.getValueRange();
	    		 String valueLen = metaData.getValueLength();  //数据字典项时允许选择的最多项数 
	    		 String groupName = metaData.getGroupName();
	    		 String dateType = metaData.getValueRange();
	    		 map.put("fieldType", fieldType);
	    		 map.put("fieldEnName", fieldEnName);
	    		 map.put("fieldZhName", fieldZhName);
	    		 map.put("groupId", groupId);
	    		 map.put("pattern", pattern);
	    		 map.put("queryModel", queryModel);
	    		 map.put("fieldValues", fieldValues);
	    		 map.put("groupName", groupName);
	    		 map.put("valueLen", valueLen);
	    		 map.put("dateType", dateType);
	    		 array.add(map);
			}
		}
    	return array.toString();
	}

	@Override
	public String getMetaDataValueByEnName(String enName) {
		MetadataDefinition definition = MetadataSupport.getMetadataDefinitionByName(enName);
		String value = "";
		/*if(definition!=null){
			value = definition.getValueRange();
		}*/
		Integer fieldType = definition.getFieldType();
		String valueRange = definition.getValueRange();
		if(fieldType==9){
			Long indexPk = dictNameService.getDictNamePKByIndex(valueRange);//根据数据库数据字典获得主键
			value = dictValueService.getDictValuesByPId(indexPk);//通过主键查询dict_value中pid对应的值
		}else if(fieldType==2||fieldType==3||fieldType==4){
			value = valueRange;
		}
		return value;
	}

	@Override
	public String getDefineMetaDataByResType(String resType) {
		JSONArray array = new JSONArray();
		List<MetadataDefinition> metaDataList = new ArrayList<MetadataDefinition>();
		if("common".equals(resType)){
			metaDataList = MetadataSupport.getCommonMetadatas();
		}else{
			//metaDataList = MetadataSupport.getMetadateDefinesByResType(resType);
			metaDataList = MetadataSupport.getPrivilegeMetadateDefineList(resType);
		}
		if(metaDataList!=null){
			for(MetadataDefinition metaData : metaDataList){
				 Map<String, Object> map = new HashMap<String, Object>();
				 int fieldType = metaData.getFieldType();
	    		 String fieldEnName = metaData.getFieldName();
	    		 String fieldZhName = metaData.getFieldZhName();
	    		 String groupId = metaData.getGroupId();  //分组id
	    		 String pattern = SysMetadataDefinitionConstants.FieldType.getValueByKey(fieldType); //数据格式
	    		 int queryModel = metaData.getQueryModel();
	    		 String fieldValues = metaData.getValueRange();
	    		 //String valueLen = metaData.getValueLength();
	    		 String groupName = metaData.getGroupName();
	    		 map.put("resType", resType);
	    		 map.put("fieldType", fieldType);
	    		 map.put("fieldEnName", fieldEnName);
	    		 map.put("fieldZhName", fieldZhName);
	    		 map.put("groupId", groupId);
	    		 map.put("pattern", pattern);
	    		 map.put("queryModel", queryModel);
	    		 map.put("fieldValues", fieldValues);
	    		 map.put("groupName", groupName);
	    		 array.add(map);
			}
		}
    	return array.toString();
	}
}
