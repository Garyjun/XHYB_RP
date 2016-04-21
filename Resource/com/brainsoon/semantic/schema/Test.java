package com.brainsoon.semantic.schema;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellRangeAddress;

import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.resource.po.UploadTaskDetail;
import com.brainsoon.resource.support.ExcelData;
import com.brainsoon.resource.support.ExcelDataCell;
import com.brainsoon.resource.support.ExcelDataDetailMK;
import com.brainsoon.resource.support.ExcelDataRow;
import com.brainsoon.semantic.ontology.model.Asset;
import com.brainsoon.semantic.ontology.model.CommonMetaData;
import com.brainsoon.semantic.ontology.model.ExtendMetaData;
import com.brainsoon.system.support.SystemConstants.ConsumeType;
import com.brainsoon.system.support.SystemConstants.Language;
import com.brainsoon.system.support.SystemConstants.OpeningRate;
import com.brainsoon.system.support.SystemConstants.ResourceMoudle;
import com.google.gson.Gson;

public class Test {
	transient static final Log logger = LogFactory.getLog(Test.class);
	
	public static void main(String[] args) throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		//referClass 0 commonMetaDatas 1 extendMetaDatas
		Gson gson = new Gson();
		//String datas = "{\"@type\":\"customMetaData\",\"name\":\"TBCommonMetaData\",\"nameCN\":\"同步资源通用元数据\",\"customPropertys\":[{\"name\":\"module\",\"nameCN\":\"资源模块\",\"isMeta\":1,\"necessary\":1,\"order\":0},{\"name\":\"version\",\"nameCN\":\"教材版本\",\"isMeta\":1,\"necessary\":1,\"order\":1},{\"name\":\"educational_phase\",\"nameCN\":\"教育阶段\",\"isMeta\":1,\"necessary\":1,\"order\":2},{\"name\":\"subject\",\"nameCN\":\"学科\",\"isMeta\":1,\"necessary\":1,\"order\":3},{\"name\":\"grade\",\"nameCN\":\"适用年级\",\"isMeta\":1,\"necessary\":1,\"order\":4},{\"name\":\"fascicule\",\"nameCN\":\"分册\",\"isMeta\":1,\"necessary\":1,\"order\":5},{\"name\":\"title\",\"nameCN\":\"标题\",\"isMeta\":1,\"necessary\":1,\"order\":-1}]}";
		String datas = "{\"@type\":\"customMetaData\",\"name\":\"TBCommonMetaData\",\"nameCN\":\"同步资源导入元数据模板\",\"customPropertys\":[{\"name\":\"module\",\"nameCN\":\"资源模块\",\"isMeta\":1,\"necessary\":1,\"order\":0,\"referClass\":\"0\"},{\"name\":\"type\",\"nameCN\":\"资源类型\",\"isMeta\":1,\"necessary\":1,\"order\":1,\"referClass\":\"0\"},{\"name\":\"version\",\"nameCN\":\"教材版本\",\"isMeta\":1,\"necessary\":0,\"order\":2,\"referClass\":\"0\"},{\"name\":\"educational_phase\",\"nameCN\":\"教育阶段\",\"isMeta\":1,\"necessary\":1,\"order\":3,\"referClass\":\"0\"},{\"name\":\"subject\",\"nameCN\":\"学科\",\"isMeta\":1,\"necessary\":1,\"order\":4,\"referClass\":\"0\"},{\"name\":\"grade\",\"nameCN\":\"适用年级\",\"isMeta\":1,\"necessary\":1,\"order\":5,\"referClass\":\"0\"},{\"name\":\"fascicule\",\"nameCN\":\"分册\",\"isMeta\":1,\"necessary\":1,\"order\":6,\"referClass\":\"0\"},{\"name\":\"level7\",\"nameCN\":\"章\",\"isMeta\":0,\"necessary\":0,\"order\":7,\"referClass\":\"-1\"},{\"name\":\"level8\",\"nameCN\":\"节\",\"isMeta\":0,\"necessary\":0,\"order\":8,\"referClass\":\"-1\"},{\"name\":\"level9\",\"nameCN\":\"课\",\"isMeta\":0,\"necessary\":0,\"order\":9,\"referClass\":\"-1\"},{\"name\":\"title\",\"nameCN\":\"标题\",\"isMeta\":1,\"necessary\":1,\"order\":-1,\"referClass\":\"0\"},{\"name\":\"keywords\",\"nameCN\":\"关键字\",\"isMeta\":1,\"necessary\":0,\"order\":-1,\"referClass\":\"0\"},{\"name\":\"format\",\"nameCN\":\"格式\",\"isMeta\":1,\"necessary\":0,\"order\":-1,\"referClass\":\"0\"},{\"name\":\"source\",\"nameCN\":\"来源\",\"isMeta\":1,\"necessary\":1,\"order\":-1,\"referClass\":\"0\"},{\"name\":\"description\",\"nameCN\":\"摘要\",\"isMeta\":1,\"necessary\":0,\"order\":-1,\"referClass\":\"0\"},{\"name\":\"down_type\",\"nameCN\":\"下载资源消费类型\",\"isMeta\":1,\"necessary\":0,\"order\":-1,\"referClass\":\"1\"},{\"name\":\"down_value\",\"nameCN\":\"下载资源消费值\",\"isMeta\":1,\"necessary\":0,\"order\":-1,\"referClass\":\"1\"},{\"name\":\"view_type\",\"nameCN\":\"观看资源消费类型\",\"isMeta\":1,\"necessary\":0,\"order\":-1,\"referClass\":\"1\"},{\"name\":\"view_value\",\"nameCN\":\"观看消费值\",\"isMeta\":1,\"necessary\":0,\"order\":-1,\"referClass\":\"1\"},{\"name\":\"language\",\"nameCN\":\"语种\",\"isMeta\":1,\"necessary\":0,\"order\":-1,\"referClass\":\"0\"},{\"name\":\"filePath\",\"nameCN\":\"资源文件路径\",\"isMeta\":0,\"necessary\":0,\"order\":-1,\"referClass\":\"-1\"},{\"name\":\"fileName\",\"nameCN\":\"文件名\",\"isMeta\":0,\"necessary\":0,\"order\":-1,\"referClass\":\"-1\"},{\"name\":\"creator\",\"nameCN\":\"制作者姓名\",\"isMeta\":1,\"necessary\":0,\"order\":-1,\"referClass\":\"0\"},{\"name\":\"email\",\"nameCN\":\"制作者邮箱\",\"isMeta\":1,\"necessary\":0,\"order\":-1,\"referClass\":\"0\"},{\"name\":\"public_or_not\",\"nameCN\":\"资源共享度\",\"isMeta\":1,\"necessary\":0,\"order\":-1,\"referClass\":\"0\"}]}";
		
		
//		CustomMetaData dataObj = gson.fromJson(datas, CustomMetaData.class);
//		System.out.println(dataObj.getNameCN()+"=="+dataObj.getCustomPropertys().get(0).nameCN);
		//createExcel("D:/test.xls", dataObj.getCustomPropertys());
		
//		ExcelData excelData = new ExcelData();
//		fillData("D:/最新数据.xls", excelData);
//		parseData2Asset(excelData, null);
		
//		List<ExcelDataCell> cells = new ArrayList<ExcelDataCell>();
//		ExcelDataCell c = new ExcelDataCell();
//		c.setOrder(6);
//		c.setName("测试6");
//		cells.add(c);
//		
//		cells.get(0).setDataCode("uuu");
//		
//		System.out.println(cells.get(0).getDataCode());
//		c = new ExcelDataCell();
//		c.setOrder(7);
//		c.setName("测试7");
//		cells.add(c);
//		c = new ExcelDataCell();
//		c.setOrder(3);
//		c.setName("测试3");
//		cells.add(c);
//		System.out.println(cells);
//		
//		sortMeta(cells);
//		
//		System.out.println(cells);
		
		String d = "http://10.130.29.14:8090/semantic_index_server/ontologyDetailQuery/queryImportCode?xpathName=%E5%90%8C%E6%AD%A5%E8%B5%84%E6%BA%90,%E5%AD%A6%E6%A1%88,%E4%BA%BA%E6%95%99%E7%89%88,%E5%88%9D%E4%B8%AD,%E8%8B%B1%E8%AF%AD,%E4%B8%83%E5%B9%B4%E7%BA%A7,%E4%B8%8A%E5%86%8C,Unit+1+My+name%27s+Gina4,,&domainType=0";
		HttpClientUtil http = new HttpClientUtil();
		String returnData = http.executeGet(d);
		System.out.println(returnData+"===");
	}
	
	public static void createExcel(String path,List<LomProperty> props){
		File excel = new File(path);
		HSSFWorkbook workbook = null;
        try {
            workbook = new HSSFWorkbook(new FileInputStream(excel));
        } catch (FileNotFoundException e1) {
            logger.error(e1);
        } catch (IOException e1) {
            logger.error(e1);
        }
        HSSFSheet sheet = workbook.getSheetAt(0);
        
        //处理title
        HSSFRow titleRow = sheet.getRow(0);
        HSSFCellStyle s0 = titleRow.getCell(1).getCellStyle();
        HSSFCellStyle s1 = titleRow.getCell(titleRow.getLastCellNum()-1).getCellStyle();
        
        //处理批次号单元格
        HSSFRow batchNumRow = sheet.getRow(1);
        HSSFCellStyle s2 = batchNumRow.getCell(batchNumRow.getLastCellNum()-1).getCellStyle();
        
        HSSFRow rowTemp = sheet.getRow(2);
        
        HSSFCell formatCell = rowTemp.getCell(0);
        
        HSSFRow keyRow = sheet.createRow(3);
        
        //添加新的单元格
        HSSFCell newCell = null;
        int len = props.size() - 1;
        for (int i = 0;i <= len;i ++) {
        	LomProperty prop = props.get(i);
        	newCell = rowTemp.createCell(i);
			newCell.setCellValue(prop.getNameCN());
			newCell.setCellStyle(formatCell.getCellStyle());
			newCell = keyRow.createCell(i);
			newCell.setCellValue(prop.getKey());
		}
        
        //处理title样式
        for(int i = 5;i <= len;i ++){
        	newCell = titleRow.createCell(i);
			newCell.setCellValue("");
			if(i == len){
				newCell.setCellStyle(s1);
			}else{
				newCell.setCellStyle(s0);
			}
			newCell = batchNumRow.createCell(i);
			newCell.setCellValue("");
			newCell.setCellStyle(s2);
        }
        //合并title
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, len)); 
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 4, len)); 
        //sheet.addMergedRegion(new Region(0,(short)0,0,(short)6)); 
        
        
        File outFile = new File("d:/t111.xls");
        OutputStream out = null;
        try {
            out = new FileOutputStream(outFile);
            workbook.write(out);// 写入File
        } catch (Exception e) {
            logger.error(e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.error(e);
                }
            }
        }
	}
	
	public static void fillData(String excelPath,ExcelData data){
		File excel = new File(excelPath);
		HSSFWorkbook workbook = null;
        try {
            workbook = new HSSFWorkbook(new FileInputStream(excel));
	        HSSFSheet sheet = workbook.getSheetAt(0);
	        System.out.println("行号："+sheet.getLastRowNum());
	        HSSFRow batchNumAndNameRow = sheet.getRow(1);
	        
	        data.setqName(batchNumAndNameRow.getCell(1).getStringCellValue());
	        batchNumAndNameRow.getCell(4).setCellType(Cell.CELL_TYPE_STRING);
	        String batchNum = batchNumAndNameRow.getCell(4).getStringCellValue();
	        data.setBatchNum(batchNum);
	        
	        //处理数据
	        /**第一步，先读取标记行*/
	        ExcelDataDetailMK marker = null;
	        HSSFRow fieldsRow = sheet.getRow(2);
	        HSSFRow markerRow = sheet.getRow(3);
	        int lastCell = markerRow.getLastCellNum()-1;
	        HSSFCell tempCell = null;
	        String tempValue = "";
	        String [] tempValueArray;
	        ExcelDataDetailMK [] markers = new ExcelDataDetailMK[lastCell+1];
	        for (int i = 0; i <= lastCell; i++) {
	        	tempCell = markerRow.getCell(i);
	        	tempValue = tempCell.getStringCellValue();
	        	tempValueArray = StringUtils.split(tempValue,",");
	        	marker = new ExcelDataDetailMK(tempValueArray[0], Integer.parseInt(tempValueArray[1]), Integer.parseInt(tempValueArray[2]), Integer.parseInt(tempValueArray[3]), Integer.parseInt(tempValueArray[4]),fieldsRow.getCell(i).getStringCellValue());
	        	markers[i] = marker;
			}
	        data.setMarkers(markers);
	        
	        /**第二步，解析每行数据*/
	        int lastRowNum = sheet.getLastRowNum();
	        HSSFRow tempRow;
	        String [] datas;
	        List<ExcelDataRow> rows = new ArrayList<ExcelDataRow>();
	        ExcelDataRow dataRow = null;
	        for (int i = 4; i <= lastRowNum; i++) {
	        	tempRow = sheet.getRow(i);
	        	datas = new String[lastCell+1];
	        	dataRow = new ExcelDataRow();
	        	for (int j = 0; j <= lastCell; j++) {
	            	tempCell = tempRow.getCell(j);
	            	if(null != tempCell){
	            		tempCell.setCellType(Cell.CELL_TYPE_STRING);
	            		datas[j] = tempCell.getStringCellValue();
	            	}else{
	            		datas[j] = "";
	            	}
	        	}
	        	dataRow.setDatas(datas);
	        	dataRow.setNum(i+1);
	        	rows.add(dataRow);
			}
	        data.setRows(rows);
	        
	        System.out.println(data.getBatchNum());
	        System.out.println(data.getRows());
        } catch (IOException e1) {
            logger.error(e1);
        } finally{
        	excel = null;
        }
        
	}
	/**
	 * 转换数据
	 * @param excelData
	 * @param module 模块code
	 * @param asset
	 */
	public static void parseData2Asset(ExcelData excelData,String module){
		if(null == excelData)
			return;
		Asset asset = null;
		ExcelDataDetailMK[] markers = excelData.getMarkers();
		List<ExcelDataRow> rows = excelData.getRows();
		int maxRow = rows.size();
		String [] datas;
		String currentData;
		int datasLen = 0;
		ExcelDataDetailMK mk;
		String name;
		int order;//顺序，大于0参与分类体系
		int necessary;//是否必须
		int referClass;//0 保存在CommonMetaData对象上，1保存在ExtendMetaData ，其他忽略
		List<ExcelDataCell> cells = new ArrayList<ExcelDataCell>();
		
		String moduleNameCN = ResourceMoudle.getValueByKey(module);
		ExcelDataCell moduleCell = new ExcelDataCell(0,moduleNameCN,"module",-1);
		moduleCell.setDataCode(module);
		ExcelDataCell cell;
		UploadTaskDetail detailInfo;
		Map<String, String> commonMetaDatas;
		Map<String, String> extendMetaDatas;
		for (int i = 0; i < maxRow; i++) {
			ExcelDataRow row = rows.get(i);
			datas = row.getDatas();
			datasLen = datas.length;
			cells.clear();
			cells.add(moduleCell);
			detailInfo = new UploadTaskDetail();
			detailInfo.setCreateTime(new Date());
			detailInfo.setTask(null);
			detailInfo.setExcelNum(row.getNum());
			String error = "第【"+row.getNum()+"】行导入失败";
			
			asset = initAsset();
			commonMetaDatas = asset.getCommonMetaData().getCommonMetaDatas();
//			extendMetaDatas = asset.getExtendMetaData().getExtendMetaDatas();
			
			for (int j = 0; j < datasLen; j++) {
				mk = markers[j];
				currentData = datas[j];
				name = mk.name;
				order = mk.order;
				referClass = mk.referClass;
				necessary = mk.necessary;
				if(necessary == 1 && StringUtils.isBlank(currentData)){
					detailInfo.setRemark(error+"内容不合法，或者空行忽略");
					//create(detail);
					continue;
				}
				if(order >= 0){
					cell = new ExcelDataCell(order,currentData,name,referClass);
					cells.add(cell);
				}else{
					currentData = transformValue(name, currentData)+"";
					if(referClass == 0){
						commonMetaDatas.put(name, currentData);
					}else if(referClass == 1){
//						extendMetaDatas.put(name, currentData);
					}
				}
			}
			//转换分类体系字段,排序后调用接口
			sortMeta(cells);
			transCN2ENCode(cells);
//			fillEnCode(cells, commonMetaDatas, extendMetaDatas);
		}
		System.out.println(asset);
	}
	/**
	 * 初始化
	 * @return
	 */
	public static Asset initAsset(){
		Asset asset = new Asset();
		CommonMetaData commonMeta = asset.getCommonMetaData();
		if(null == commonMeta){
			commonMeta = new CommonMetaData();
			asset.setCommonMetaData(commonMeta);
		}
//		ExtendMetaData extendMeta = asset.getExtendMetaData();
//		if(null == extendMeta){
//			extendMeta = new ExtendMetaData();
//			asset.setExtendMetaData(extendMeta);
//		}
		Map<String, String> commonMetaDatas = commonMeta.getCommonMetaDatas();
		if(null == commonMetaDatas){
			commonMetaDatas = new HashMap<String, String>();
		}
//		Map<String, String> extendMetaDatas = extendMeta.getExtendMetaDatas();
//		if(null == extendMetaDatas){
//			extendMetaDatas = new HashMap<String, String>();
//		}
		return asset;
	}
	public static Object transformValue(String name,String value){
		Class c = dictionaryMapper.get(name);
		if(null != c){
			try {
				return c.getMethod("getValueByDesc", new Class[]{Object.class}).invoke(c, value);
			} catch (Exception e) {
				logger.error(e);
			}
		}
		return value;
	}
	static Map<String,Class> dictionaryMapper = new HashMap<String, Class>();
	static{
		dictionaryMapper.put("down_type", ConsumeType.class);
		dictionaryMapper.put("view_type", ConsumeType.class);
		dictionaryMapper.put("language", Language.class);
		dictionaryMapper.put("public_or_not", OpeningRate.class);
	}
	public static void sortMeta(List<ExcelDataCell> cells){
		Collections.sort(cells);
	}
	/**
	 * 转换分类体系为英文code
	 * @param cells
	 */
	public static void transCN2ENCode(List<ExcelDataCell> cells){
		String version = "";
		String domainType = "1";
		List<String> ar = new ArrayList<String>(cells.size());
		for (ExcelDataCell excelDataCell : cells) {
			if(domainType.equals("1") && StringUtils.equalsIgnoreCase("version", excelDataCell.getName())){
				version = excelDataCell.getData();
				if(StringUtils.isNotBlank(version)){
					domainType = "0";
				}
			}
			try {
				ar.add(URLEncoder.encode(excelDataCell.getData(), "utf-8"));
			} catch (UnsupportedEncodingException e) {
				logger.error("编码出错"+e.getMessage());
			}
		}
		String codes = StringUtils.join(ar,",");
		HttpClientUtil http = new HttpClientUtil();
		String url = "http://10.130.29.14:8090/semantic_index_server/ontologyDetailQuery/queryImportCode";
		String returnData = http.executeGet(url+"?xpathName="+codes+"&domainType="+domainType);
		if(StringUtils.isNotBlank(returnData)){
			Gson gson = new Gson();
			QueryImportCode qCodes = gson.fromJson(returnData, QueryImportCode.class);
			//给code赋值
			String xPath = qCodes.getXpath();
			String [] xPathArray = StringUtils.split(xPath,",");
			for (int i = 0; i < xPathArray.length; i++) {
				cells.get(i).setDataCode(xPathArray[i]);
			}
			//处理单元
			String unitId = qCodes.getUnitId();
			String unitName = qCodes.getUnitName();
			if(StringUtils.isNotBlank(unitId)){
				ExcelDataCell unit = new ExcelDataCell(-1,unitId, "unit", 0);
				unit.setDataCode(unitId);
				ExcelDataCell unitNameCell = new ExcelDataCell(-1,unitName, "unitName", 0);
				unitNameCell.setDataCode(unitName);
				cells.add(unit);
				cells.add(unitNameCell);
			}
		}
	}
	/**
	 * 填充分类体系数据
	 * @param cells
	 * @param commonMetaDatas
	 * @param extendMetaDatas
	 */
	public static void fillEnCode(List<ExcelDataCell> cells,Map<String, String> commonMetaDatas,Map<String, String> extendMetaDatas){
		if(null != cells){
			int referClass;
			String name;
			String code;
			for (ExcelDataCell excelDataCell : cells) {
				referClass = excelDataCell.getReferClass();
				name = excelDataCell.getName();
				code = excelDataCell.getDataCode();
				if(referClass == 0){
					commonMetaDatas.put(name, code);
				}else if(referClass == 1){
					extendMetaDatas.put(name, code);
				}
			}
		}
	}
}
