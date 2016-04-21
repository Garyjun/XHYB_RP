package com.brainsoon.search.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

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
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.common.util.dofile.zip.ZipUtil;
import com.brainsoon.resource.action.BresAction;
import com.brainsoon.resource.service.IBatchImportResService;
import com.brainsoon.resource.support.ExcelDataDetailMK;
import com.brainsoon.search.service.ISearchPubresService;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.CommonMetaData;
import com.brainsoon.semantic.ontology.model.ExtendMetaData;
import com.brainsoon.semantic.ontology.model.MetaDataDC;
import com.brainsoon.semantic.ontology.model.SearchResultCa;
import com.brainsoon.semantic.schema.LomProperty;
import com.google.gson.Gson;

@Service
public class SearchPubresService implements ISearchPubresService {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	private static final String ADVANCE_SEARCH_RESOURCE_DIC = WebappConfigUtil.getParameter("ADVANCE_SEARCH_RESOURCE_DIC");
	private static final String ADVANCE_SEARCH_RESOURCE_LIST = WebappConfigUtil.getParameter("ADVANCE_SEARCH_RESOURCE_LIST");
	public static final String FILE_TEMP = StringUtils.replace(WebAppUtils.getTempDir(), "\\", "/");
	private final static SimpleDateFormat dateformat2 = new SimpleDateFormat("yyyyMMddHHmmssSSS");

	@Autowired
	private IBatchImportResService batchImportResService;

	@Override
	public String getAdvaceSearchQueryConditionDic(String eduPhase, String subject, String type) {
		JSONArray jsonArr = new JSONArray();
		try {
			String url = ADVANCE_SEARCH_RESOURCE_DIC;
			HttpClientUtil http = new HttpClientUtil();
			String json = "";
			/**
			 * type:null， 学段 type:1，学科 type:2,知识点
			 */
			if (StringUtils.equals(type, "1")) {
				url = url + "?educational_phase=" + eduPhase + "&type=" + type;
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

	@Override
	public File exportRes(String queryCondition) {
		HttpClientUtil http = new HttpClientUtil();
		String jsonStr = http.executeGet(WebappConfigUtil.getParameter("PUBLISH_QUERY_URL") + "?" + queryCondition);
		SearchResultCa resultList = new Gson().fromJson(jsonStr, SearchResultCa.class);
		List<Ca> caList = resultList.getRows();
		String zipName = "";
		if (caList != null || caList.size() > 0) {
			String title = "资源";
			String[][] paths = new String[caList.size()][2];
			int i = 0;
			for (Ca ca : caList) {
				title = ca.getCommonMetaData().getTitle();
				String path = ca.getCommonMetaData().getPath();
				paths[i][0] = StringUtils.replace(WebAppUtils.getWebAppBaseFileDirFR(), "\\", "/") + path;
				paths[i][1] = title + "V" + ca.getCommonMetaData().getResVersion() + ".0";
				i++;
			}
			zipName = BresAction.FILE_TEMP + "资源包.zip";
			try {
				ZipUtil.zipFileOrFolder(paths, zipName, null);
			} catch (Exception e) {
				logger.error("压缩出现问题" + e.getMessage());
			}
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
		SearchPubresService s = new SearchPubresService();
		System.err.println(s.getAdvaceSearchQueryConditionDic(null, null, null));
		System.err.println(s.getAdvaceSearchQueryConditionDic("P", null, "1"));
		System.err.println(s.getAdvaceSearchQueryConditionDic("P", "S02", "2"));
	}
}
