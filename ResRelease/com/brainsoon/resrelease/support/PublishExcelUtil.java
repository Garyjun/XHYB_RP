package com.brainsoon.resrelease.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hssf.util.Region;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.exception.DaoException;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.date.DateUtil;
import com.brainsoon.common.util.dofile.content.ExcelUtil;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.FileToolkit;
import com.brainsoon.common.util.dofile.util.PropertiesReader;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.resrelease.po.ResOrder;
import com.brainsoon.resrelease.service.IResOrderService;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.CommonMetaData;
import com.brainsoon.semantic.ontology.model.ExtendMetaData;
import com.brainsoon.semantic.ontology.model.MetaDataDC;
import com.brainsoon.system.service.ICodeService;
import com.brainsoon.system.service.IDictNameService;
import com.brainsoon.system.support.SystemConstants;
import com.google.gson.JsonSyntaxException;

/**
 * @ClassName: PublishExcelUtil
 * @Description: 发布读写Excel工具类
 * @author xiehewei
 * @date 2014年9月12日 上午9:50:13
 *
 */
public class PublishExcelUtil {
	
	private static Logger log = Logger.getLogger(PublishExcelUtil.class);

	/**
	 * 生成元数据Excel
	 * @param orderId 需求单Id
	 * @param resId 资源Id
	 */
	public static void createMetadataExcel(String orderId, String resId){
		HSSFWorkbook book = new HSSFWorkbook();
		
		File file = new File("C:/Users/root/Desktop/metadata_course.xls");
		HttpClientUtil http = new HttpClientUtil();
		String metadataItem = http.executeGet("http://10.130.29.26:8080/semantic_index_server/ontologyListQuery/customMetaData?name=YD");
		JSONObject object = JSONObject.fromObject(metadataItem);
		JSONArray array = object.getJSONArray("customPropertys");
		int len = array.size();
		HSSFSheet sheet = book.createSheet();
		int rowNum = 0;
		HSSFRow row = sheet.createRow(rowNum++);
		
		
		HSSFRow row0 = sheet.createRow(rowNum++);
		short necNum = 0;
		short notNecNum = 0;
		HSSFCell currentCell;
		int cellNum = 0;
		String supplierName = (String) object.get("nameCN");
		for(int i=0;i<len;i++){
			currentCell = row.createCell(cellNum);
			currentCell.setCellValue(supplierName);
			currentCell.setCellStyle(getFirstTitleStyle(book,true,0));
			sheet.setColumnWidth(cellNum, (short) 35.7 * 10 * 18);
			row.setHeightInPoints((float) 18.5);
			cellNum++;
		}
		sheet.addMergedRegion(new Region(0, (short)0, 0, (short) (len-1)));
		cellNum = 0;
		for(int i=0;i<len;i++){
			JSONObject arrObj = (JSONObject) array.get(i);
			currentCell = row0.createCell(cellNum);
			if(arrObj.get("refer").toString().equals("2")){
				currentCell.setCellValue("必须数据元素");
				necNum++;
			}else{
				currentCell.setCellValue("可选数据元素");
				notNecNum++;
			}
//			currentCell.setCellStyle(getFirstTitleStyle(book,true,1));
			sheet.setColumnWidth(cellNum, (short) 35.7 * 10 * 18);
			row0.setHeightInPoints((float) 18.5);
			cellNum++;
		}
		// 四个参数分别是：起始行，起始列，结束行，结束列
		sheet.addMergedRegion(new Region(1, (short)0, 1, (short) (necNum-1)));
		sheet.addMergedRegion(new Region(1, (short)(necNum), 1, (short)(necNum+notNecNum-1)));
		cellNum = 0;
		HSSFRow row1 = sheet.createRow(rowNum++);
		HSSFRow row2 = sheet.createRow(rowNum++);
		for (int i=0;i<len;i++) {
			JSONObject arrObj = (JSONObject) array.get(i);
			currentCell = row1.createCell(cellNum);
			if(Long.valueOf(arrObj.get("necessary")+"")==1){
				currentCell.setCellStyle(getListHeaderStyle(book,1));
			}else{
				currentCell.setCellStyle(getListHeaderStyle(book,0));
			}
			currentCell.setCellValue(arrObj.get("nameCN")+"");
			sheet.setColumnWidth(cellNum, (short) 35.7 * 10 * 18);
			row1.setHeightInPoints((float) 18.5);
			currentCell = row2.createCell(cellNum);
			currentCell.setCellValue(arrObj.get("alias").toString());
			row2.setZeroHeight(true);//隐藏行
			cellNum++;
		}
		try {
			book.write(new FileOutputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 针对中国移动定制路径
	 * 根据字段名获得字段值
	 * @param fieldName 字段名
	 * @param refer 取值参数
	 * @param orderId 需求单id
	 * @param ca ca
	 * @param asset asset
	 * 
	 */
	private static Object getFieldValueByName(String fieldName, String refer, String orderId, MetaDataDC dc) {
		//CommonMetaData commMetaData = ca!=null?ca.getCommonMetaData():asset.getCommonMetaData();
		//ExtendMetaData extendMetaData = ca!=null?ca.getExtendMetaData():asset.getExtendMetaData();
		CommonMetaData commMetaData = dc.getCommonMetaData();
		ExtendMetaData extendMetaData = dc.getExtendMetaData();
		String val = "";
		ICodeService codeService = null;
		IDictNameService dictNameService = null;
		try {
			dictNameService = (IDictNameService) BeanFactoryUtil.getBean("dictNameService");
			codeService = (ICodeService) BeanFactoryUtil.getBean("codeService");
		} catch (Exception e) {
			e.printStackTrace();
		}
		//try {
			if(refer.equals("2")||refer.equals("3")){
				val = commMetaData.getCommonMetaValue(fieldName);
				
				String englishCode = dictNameService.getValueKeyByIndex("英语", "subject");
				String peopleACode = dictNameService.getValueKeyByIndex("数学A版", "subject");
				String peopleBCode = dictNameService.getValueKeyByIndex("数学B版", "subject");
				//对学科、年级、分册进行编码
				if(fieldName.equals("subject")){
					if(peopleACode.equals(val)||peopleBCode.equals(val)){//对数学A版 、数学B版进行处理
						val = val.substring(0, val.length()-1);
					}
					val = codeService.selectCode("02", val.toString());
				}else if(fieldName.equals("grade")){
					String educationalPhaseName = commMetaData.getCommonMetaValue("educational_phase_name");
					String prefix = dictNameService.getValueKeyByIndex(educationalPhaseName,"peroid");
					val = codeService.selectCode("03", prefix+val.toString());
				}else if(fieldName.equals("fascicule")){
					String grade = commMetaData.getCommonMetaValue("grade");
					String gradeDictVal = dictNameService.getValueKeyByIndex("通用", "NJ");
					if(grade.equals(gradeDictVal)){ //针对高中
						String fasciculeName = commMetaData.getCommonMetaValue("fasciculeName");
						val = codeService.selectCodeByName("01", fasciculeName);
					}else{
						val = codeService.selectCode("01", val.toString());
					}
			    }else if(fieldName.equals("language")){
					if("ZH".equals(val)){
						val = "zh-CN";
					}else if("EN".equals(val)){
						val = "en-US";
					}
				}else if(fieldName.equals("fileByte")){
					val = getResourceSize(dc);
				}else if(fieldName.equals("module")){
					val = SystemConstants.ResourceMoudle.getValueByKey(val);
				}else if(fieldName.equals("type")){
					//对素材进行特别处理
					if(val.equals("T01")){
						String sufix = dc.getCommonMetaData().getFormat().toLowerCase();
						String documentFormat = PropertiesReader.getInstance().getProperty(ConstantsDef.documentFormat);  //文本
						String pictureFormat = PropertiesReader.getInstance().getProperty(ConstantsDef.pictureFormat); //图形/图像
						String audioFormat = PropertiesReader.getInstance().getProperty(ConstantsDef.audioFormat); //音频
						String videoFormat = PropertiesReader.getInstance().getProperty(ConstantsDef.videoFormat);  //视频
						String animaFormat = PropertiesReader.getInstance().getProperty(ConstantsDef.animaFormat); //动画
						if(documentFormat.contains(sufix)){
							val = "T";
						}else if(pictureFormat.contains(sufix)){
							val = "P";
						}else if(audioFormat.contains(sufix)){
							val = "A";
						}else if(videoFormat.contains(sufix)){
							val = "V";
						}else if(animaFormat.contains(sufix)){
							val = "N";
						}
					}else{
						val = codeService.selectCode("00", val.toString());
					}
				}else if(fieldName.equals("version")){
					//val = commMetaData.getCommonMetaValue("versionName");
					String subject = commMetaData.getCommonMetaValue("subject");
					String educationalPhaseName = commMetaData.getCommonMetaValue("educational_phase_name");
					String gradeCode = dictNameService.getValueKeyByIndex(educationalPhaseName,"peroid");
					String priCode = dictNameService.getValueKeyByIndex("小学","peroid");
					String midCode = dictNameService.getValueKeyByIndex("初中","peroid");
					if(englishCode.equals(subject)){
						if(gradeCode.equals(priCode)){//小学英语
							val = "03";
						}else if(gradeCode.equals(midCode)){//初中英语
							val = "07";
						}else{//高中英语
							val = "01";
						}
					}else if(peopleACode.equals(subject)){//人教A版
						val = "08";
					}else if(peopleBCode.equals(subject)){//人教B版
						val = "09";
					}else{
						val = "01";
					}
				}else if(fieldName.equals("publishVersion")){
					val = "v1.0"; 
				}else if(fieldName.equals("creator")){
					val = "华师京城";
				}else if(fieldName.equals("educational_phase")){ //对学段进行处理
					val = commMetaData.getCommonMetaValue("educational_phase_name");
					val = dictNameService.getValueKeyByIndex(val, "peroid");
					val = codeService.selectCode("04", val);
				}else if(fieldName.equals("description")){  //对描述为空进行处理     标题+关键字       如果关键字为空  标题+资源类型
					String str = "科技让教育更精彩";
					//val = dc.getCommonMetaData().getDescription();
					String keywords = commMetaData.getCommonMetaValue("keywords");
					if(StringUtils.isEmpty(val)){
						if(StringUtils.isEmpty(keywords)){
							val = commMetaData.getCommonMetaValue("title") + "," + 
									SystemConstants.ResourceType.getValueByKey(commMetaData.getCommonMetaValue("type"));
						}else{
							val = commMetaData.getCommonMetaValue("title") 
									+ "," + keywords;
						}
					}else {
						if(val.contains(str)){
							val = val.replaceAll(str, "");
							if(StringUtils.isEmpty(val)){
								if(StringUtils.isEmpty(keywords)){
									val = commMetaData.getCommonMetaValue("title") + "," + 
											SystemConstants.ResourceType.getValueByKey(commMetaData.getCommonMetaValue("type"));
								}else{
									val = commMetaData.getCommonMetaValue("title") 
											+ "," + keywords;
								}
								
							}else{
								val = val.trim();
							}
						}else{
							val = val.trim();
						}
					}
				}else if(fieldName.equals("audience")){  //适用对象
					//val = dc.getCommonMetaData().getAudience();
					val = "";
				}else if(fieldName.equals("keywords")){ //对关键字为空进行处理     标题、资源类型
					if(StringUtils.isEmpty(val)){
						val = commMetaData.getCommonMetaValue("title") + "," + 
								commMetaData.getCommonMetaValue("type");
					}
				}
			}else if(refer.equals("4")){
				val = extendMetaData.getExtendMetaValue(fieldName);
			}
			if(fieldName.equals("bookNum")){ //对课本进行处理
				String[] importXpathNames = dc.getImportXpathName().split(",");
				String name = "";
				for(int i=0;i<=4;i++){
					name += importXpathNames[i]+",";
				}
				String grade = commMetaData.getCommonMetaValue("grade");
				String gradeDictVal = dictNameService.getValueKeyByIndex("通用", "NJ");
				if(grade.equals(gradeDictVal)){ //针对高中
					val = dictNameService.getValueKeyByIndex(name.substring(0, name.length()-1),"ISBN");
				}else{
					val = dictNameService.getValueKeyByIndex(name.substring(0, name.length()-1), "bookNum");
				}
			}
			if(StringUtils.isEmpty(val)){
				val = "";
			}
		//} catch (Exception e) { 
		//	e.getMessage(); 
		//}
		return val; 
	}
	 
	/**
	 * 一级标题字体
	 * 
	 * @param wb
	 * @return
	 */
	private static  HSSFFont getFirstHdrFont(HSSFWorkbook wb,boolean isNeed) {
		HSSFFont fontStyle = wb.createFont();
		fontStyle.setFontName("微软雅黑");
		fontStyle.setFontHeightInPoints((short) 13);
		fontStyle.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
		if(isNeed){
			fontStyle.setColor(IndexedColors.BLACK.getIndex());
		}
		return fontStyle;
	}
	
	/**
	 * 一级标题的格式
	 */
	private  static HSSFCellStyle getFirstTitleStyle(HSSFWorkbook wb,boolean isNeed, int rowNum) {
		HSSFCellStyle cellStyle = wb.createCellStyle();
		HSSFDataFormat format = wb.createDataFormat();  
		cellStyle.setDataFormat(format.getFormat("@"));  
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		cellStyle.setFont(getFirstHdrFont(wb,isNeed));
		cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		if(rowNum==0){
			cellStyle.setFillForegroundColor(getCustomerColor(IndexedColors.LIGHT_GREEN.getIndex(), wb));
		}else{
			cellStyle.setFillForegroundColor(getCustomerColor(IndexedColors.GOLD.getIndex(), wb));
		}
		
		cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		return cellStyle;
	}
	
	/**
	 * 二级标题的字体
	 */
	public static HSSFCellStyle getListHeaderStyle(HSSFWorkbook wb,int necessary) {
		HSSFFont fontStyle = wb.createFont();
		fontStyle.setFontName("微软雅黑");
		fontStyle.setFontHeightInPoints((short) 11);
		HSSFCellStyle cellStyle = wb.createCellStyle();
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cellStyle.setFont(fontStyle);
		cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		if(necessary==1){
			cellStyle.setFillForegroundColor(getCustomerColor(IndexedColors.RED.getIndex(), wb));
		}else{
			cellStyle.setFillForegroundColor(getCustomerColor(IndexedColors.BLUE.getIndex(), wb));
		}
		cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		return cellStyle;
	}
	
	/**
	 * 二级标题的格式
	 */
	public static short getCustomerColor(short index, HSSFWorkbook wb) {
		HSSFPalette palette = wb.getCustomPalette();
		// 197, 190, 151
		palette.setColorAtIndex(HSSFColor.LIGHT_ORANGE.index, (byte) 197, (byte) 190, (byte) 151);
		// 221, 217, 195
		palette.setColorAtIndex(HSSFColor.BROWN.index, (byte) 221, (byte) 217, (byte) 195);
		// 153, 204, 255
		palette.setColorAtIndex(HSSFColor.BLUE.index, (byte) 153, (byte) 204, (byte) 255);
		// 252, 125, 0
		palette.setColorAtIndex(HSSFColor.YELLOW.index, (byte) 153, (byte) 204, (byte) 255);
		return index;

	}
	
	
	public static String getResourceSize(MetaDataDC dc){
		Long size = 0L;
//		if(ca!=null){
//			List<Organization> orgs = ca.getOrganizations();
//			for(Organization org:orgs){
//				List<OrganizationItem> items = org.getOrganizationItems();
//				for(OrganizationItem item:items){
//					List<com.brainsoon.semantic.ontology.model.File> files = item.getFiles();
//					for(com.brainsoon.semantic.ontology.model.File file:files){
//						if(file!=null){
//							String fileByte = file.getFileByte()==null?"0":(file.getFileByte()==""?"0":file.getFileByte());
//							size += Long.valueOf(fileByte);
//						}
//					}
//				}
//			}
//		}else{
			//List<com.brainsoon.semantic.ontology.model.File> files = asset.getFiles();
			//for(com.brainsoon.semantic.ontology.model.File file:files){
				//String fileByte = file.getFileByte();
				String fileByte = dc.getCommonMetaData().getCommonMetaValue("fileByte");
				size += Long.valueOf(fileByte);
			//}
//		}
		return size.toString();
	}
	
	/**
	 * 针对中国移动定制路径
	 * 根据移动模板向Excel写元数据
	 * @param orderId 需求单Id
	 * @param resIds 资源id
	 * @throws IOException 
	 */
	public static void writeMetaDataValToExcel(String orderId, List<MetaDataDC> metaDataDCList) {
		try {
			HttpClientUtil http = new HttpClientUtil();
			String metadataUrl = WebappConfigUtil.getParameter("SYS_HAVEMETA_URL");
			String metadataItem = http.executeGet(metadataUrl);
			JSONObject obj = JSONObject.fromObject(metadataItem);
			IResOrderService resOrderService = (IResOrderService) BeanFactoryUtil.getBean("resOrderService");
			ResOrder resOrder = (ResOrder) resOrderService.getByPk(ResOrder.class, Long.valueOf(orderId));
			JSONArray array = obj.getJSONArray("customPropertys");
			String rootPath = WebAppUtils.getWebRootBaseDir(ConstantsDef.prodFile).replaceAll("\\\\", "\\/");
			String webClassPath = WebAppUtils.getWebAppClassesPath().replaceAll("\\\\", "\\/");
			String xlspath = webClassPath+"resource_metadata_list.xls";
			
			//String templName = "移动学习平台学科资源批量导入模板.xls";
			String templName = "resource_metadata_list.xls";
			//templName = new String(templName.getBytes(),"utf-8");
			String excelPath = xlspath;
			File file = new File(excelPath);
			String path = rootPath+DateUtil.convertDateTimeToString(resOrder.getCreateTime()).replaceAll(":", "").replaceAll(" ", "")+
					"/"+resOrder.getOrderId();
			FileUtils.copyFileToDirectory(file, new File(path));
			try {
				FileInputStream fis = new FileInputStream(path+"/"+templName);
				HSSFWorkbook wb = new HSSFWorkbook(fis);
				HSSFSheet sheet = wb.getSheetAt(0);
				//int rowNum = sheet.getPhysicalNumberOfRows();//获得行数
				HSSFRow rows = sheet.getRow(2);
				Map<Integer, String> map = new LinkedHashMap<Integer, String>();
				for (int i = 0; i < rows.getPhysicalNumberOfCells(); i++) {
					String cellVal = rows.getCell(i).getStringCellValue();
					getEnNameByCnName(array, map, i, cellVal);
				}
				int rowNum = 3;
				for(MetaDataDC dc:metaDataDCList){
					HSSFRow row7 = sheet.createRow(rowNum++);
					log.debug(rowNum-1+"_excel+++++++++++");
//					String url = "";
//					if(platformId==1){
//						url = WebappConfigUtil.getParameter("RES_DETAIL_URL")+"?id="+resId;
//					}else{
//						url = WebappConfigUtil.getParameter("PUBLISH_DETAIL_URL")+"?id="+resId;
//					}
//					String resDetail = http.executeGet(url);
//					JSONObject object = JSONObject.fromObject(resDetail);
//					String type = object.get("type").toString();
//					Asset asset = null;
//					Ca ca = null;
//					if("CA".equals(type)){
//						ca = new Gson().fromJson(resDetail, Ca.class);
//					}else{
//						asset = new Gson().fromJson(resDetail, Asset.class);
//					}
					Set<Integer> set = map.keySet();
					Iterator<Integer> it = set.iterator();
					String refer = "";
					String lessonVal = "";
					while(it.hasNext()){
						Integer pos = it.next();
						HSSFCell cell = row7.createCell(pos);
						String fieldName = map.get(pos);
						for(int j=0;j<array.size();j++){
							JSONObject jsonObj = JSONObject.fromObject(array.get(j));
							String enName = (String) jsonObj.get("name");
							if(fieldName.equals(enName)){
								refer = (String) jsonObj.get("refer");
							}
						}
						String cellValue = getFieldValueByName(fieldName, refer, "", dc)+"";
						lessonVal = (String) getFieldValueByName("lesson_name", "3", orderId, dc);
						//如果课不存在，将节的值赋值给课，将节的赋值为空字符串
						if("".equals(lessonVal)){
							if(fieldName.equals("node_name")){
								cellValue = "";
							}
							if(fieldName.equals("lesson_name")){
								cellValue = (String) getFieldValueByName("node_name", "3", orderId, dc);
							}
						}
						if(fieldName.equals("source")){
							cellValue = "华师京城";
						}
						cell.setCellValue(cellValue);
					}
				}
				FileOutputStream fos = new FileOutputStream(path+"/"+templName);
				wb.write(fos);
				fos.close();
				fis.close();
				File fileExl = new File(path+"/"+templName);
				fileExl.renameTo(new File(path+"/"+"resource_new.xls"));
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (DaoException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 针对中国移动定制路径
	 * 根据Excel中文标题获得英文和位置
	 */
	private static void getEnNameByCnName(JSONArray array,
			Map<Integer, String> map, int i, String cellVal) {
		for(int j=0;j<array.size();j++){
			JSONObject jsonObj = JSONObject.fromObject(array.get(j));
			String cnName = (String) jsonObj.get("nameCN");
			String enName = (String) jsonObj.get("name");
			log.debug("cnName: "+cnName+"  enName: "+enName);
			if(cellVal.equals(cnName)){
				map.put(i, enName);
				break;
			}
			if(cellVal.startsWith("大小")){
				if(cnName.startsWith("大小")){
					map.put(i, enName);
					break;
				}
			}
			if(cellVal.equals("版本")){
				if(cnName.endsWith("版本号")){
					map.put(i, enName);
					break;
				}
			}
			if(cellVal.equals("资源来源")){
				map.put(i, "source");
				break;
			}
			
			if(cellVal.equals("单元")){
				map.put(i, "chapter_name");
				break;
			}
			if(cellVal.equals("课")){
				map.put(i, "lesson_name");
				break;
			}
			if(cellVal.equals("节")){
				map.put(i, "node_name");
				break;
			}
			if(cellVal.equals("课本")){
				map.put(i, "bookNum");
				break;
			}
		}
	}
	
	//针对华师京城平台资源清单标题中英文对照
	private Map<String, String> getTitleMap(){
		Map<String, String> excelMap = new LinkedHashMap<String, String>();
		excelMap.put("标识", "identifier");
		excelMap.put("标题" ,"title");
		excelMap.put("语种", "language");
		excelMap.put("描述", "description");
		excelMap.put("关键字", "keywords");
		excelMap.put("供献者", "creator");
		excelMap.put("格式 ", "format");
		excelMap.put("资源类型", "type");
		excelMap.put("资源来源", "source");
		excelMap.put("版本", "publishVersion");
		excelMap.put("大小", "totalFileSize");//包括ca asset
		excelMap.put("适用对象","fascicule");
		excelMap.put("学科", "subjectName");
		excelMap.put("学段", "educational_phase_name");
		excelMap.put("年级", "gradeName");
		excelMap.put("分册", "fasciculeName");
		excelMap.put("教材版本", "versionName");
		excelMap.put("资源模块", "module");
		return excelMap;
	}
	
	/**
	 * 针对华师京城
	 * 根据字段名获得字段值
	 * @param fieldName 字段名
	 * @param orderId 需求单id
	 * @param dc 元数据
	 * 
	 */
	private static Object getFieldValueByHsjcName(String fieldName, String orderId, MetaDataDC dc) {
			CommonMetaData commMetaData = dc.getCommonMetaData();
			String val = commMetaData.getCommonMetaValue(fieldName);
			try {
				ICodeService codeService = (ICodeService) BeanFactoryUtil.getBean("codeService");
				IDictNameService dictNameService = (IDictNameService) BeanFactoryUtil.getBean("dictNameService");
				IResOrderService resOrderService = (IResOrderService) BeanFactoryUtil.getBean("resOrderService");
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(val==null){
				val = "";
			}
			return val;
	}
	
	/**
	 * 针对华师京城生成资源发布清单
	 * 根据字段名获得字段值
	 * @param fieldName 字段名
	 * @param orderId 需求单id
	 * @param dc 元数据
	 * 
	 */
	public void writePublishOrderSourceList(String orderId, List<MetaDataDC> metaDataDCList){
		try {
			HttpClientUtil http = new HttpClientUtil();
			String metadataUrl = WebappConfigUtil.getParameter("SYS_HAVEMETA_URL");
			String metadataItem = http.executeGet(metadataUrl);
			JSONObject obj = JSONObject.fromObject(metadataItem);
			IResOrderService resOrderService = (IResOrderService) BeanFactoryUtil.getBean("resOrderService");
			ResOrder resOrder = (ResOrder) resOrderService.getByPk(ResOrder.class, Long.valueOf(orderId));
			JSONArray array = obj.getJSONArray("customPropertys");
			String rootPath = WebAppUtils.getWebRootBaseDir(ConstantsDef.prodFile).replaceAll("\\\\", "\\/");
			String webClassPath = WebAppUtils.getWebAppClassesPath().replaceAll("\\\\", "\\/");
			String xlspath = webClassPath+"order_publish_list.xls";
			
			String templName = "order_publish_list.xls";
			String excelPath = xlspath;
			File file = new File(excelPath);
			String path = rootPath+DateUtil.convertDateTimeToString(resOrder.getCreateTime()).replaceAll(":", "").replaceAll(" ", "")+
					"/"+resOrder.getOrderId();
			FileUtils.copyFileToDirectory(file, new File(path));
			try {
				FileInputStream fis = new FileInputStream(path+"/"+templName);
				HSSFWorkbook wb = new HSSFWorkbook(fis);
				HSSFSheet sheet = wb.getSheetAt(0);
				//int rowNum = sheet.getPhysicalNumberOfRows();//获得行数
				HSSFRow rows = sheet.getRow(2);
				Map<Integer, String> map = new LinkedHashMap<Integer, String>();
				for (int i = 0; i < rows.getPhysicalNumberOfCells(); i++) {
					String cellVal = rows.getCell(i).getStringCellValue();
					getEnNameByCnName(array, map, i, cellVal);
				}
				int rowNum = 3;
				for(MetaDataDC dc:metaDataDCList){
					HSSFRow row7 = sheet.createRow(rowNum++);
					log.debug(rowNum-1+"_excel+++++++++++");
					Set<Integer> set = map.keySet();
					Iterator<Integer> it = set.iterator();
					String refer = "";
					String lessonVal = "";
					while(it.hasNext()){
						Integer pos = it.next();
						HSSFCell cell = row7.createCell(pos);
						String fieldName = map.get(pos);
						for(int j=0;j<array.size();j++){
							JSONObject jsonObj = JSONObject.fromObject(array.get(j));
							String enName = (String) jsonObj.get("name");
							if(fieldName.equals(enName)){
								refer = (String) jsonObj.get("refer");
							}
						}
						String cellValue = getFieldValueByName(fieldName, refer, "", dc)+"";
						lessonVal = (String) getFieldValueByName("lesson_name", "3", orderId, dc);
						//如果课不存在，将节的值赋值给课，将节的赋值为空字符串
						if("".equals(lessonVal)){
							if(fieldName.equals("node_name")){
								cellValue = "";
							}
							if(fieldName.equals("lesson_name")){
								cellValue = (String) getFieldValueByName("node_name", "3", orderId, dc);
							}
						}
						if(fieldName.equals("source")){
							cellValue = "华师京城";
						}
						cell.setCellValue(cellValue);
					}
				}
				FileOutputStream fos = new FileOutputStream(path+"/"+templName);
				wb.write(fos);
				fos.close();
				fis.close();
				File fileExl = new File(path+"/"+templName);
				fileExl.renameTo(new File(path+"/"+"resource_new.xls"));
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (DaoException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 按类型统计资源个数及总个数
	 */
	public static void writeCaculateExcel(List<Object[]> list, ResOrder resOrder){
		String publishRoot = WebAppUtils.getWebRootBaseDir(ConstantsDef.prodFile).replaceAll("\\\\", "\\/");
		String webClassPath = WebAppUtils.getWebAppClassesPath().replaceAll("\\\\", "\\/");
//		String tempName = "resource_caculate_list.xls";
		String tempName = "resource_caculate_list.xlsx";
		//String xlspath = "C:/Users/root/Desktop/resource_caculate_list.xlsx";
		String xlspath = webClassPath + tempName;
		String path = publishRoot + DateUtil.convertDateTimeToString(resOrder.getCreateTime()).replaceAll(":", "").replaceAll(" ", "")+
				"/" + resOrder.getOrderId();
		java.io.File file = new java.io.File(path);
		if(!file.exists()){
			file.mkdirs();
		}
		FileOutputStream fos = null;
		XSSFRow row = null;
		XSSFCell cell = null;
		Long total = 0L;
		try {
			FileToolkit.copyFile(xlspath, path);
			String resPath = path + "/" + tempName;
			FileInputStream fis = new FileInputStream(new File(resPath));
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			XSSFSheet sheet = wb.getSheetAt(0);
			row = sheet.createRow(1);
			cell = row.createCell(1);
			cell.setCellValue("客户名称为:"+resOrder.getChannelName()+"的需求单资源统计信息");
			int num = 3;
			for(Object[] arr: list){
				row = sheet.createRow(num);
				int cellNum = 0;
				for(Object o : arr){
					cell = row.createCell(cellNum);
					if(o!=null&&o.toString().trim().length()>0){
						if(cellNum==7){
							cell.setCellValue(Integer.valueOf(o.toString()));
							total += Integer.valueOf(o.toString());
						}else{
							cell.setCellValue(o.toString());
						}
					}else{
						cell.setCellValue("");
					}
					cellNum++;
				}
				num++;
			}
			row = sheet.createRow(num);
			cell = row.createCell(7);
			cell.setCellValue(total);
			fos = new FileOutputStream(new File(resPath));
			wb.write(fos);
			fos.flush();
			fos.close();
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeMetaDataExcel(ResOrder resOrder, List<Ca> listCa, List<String> metaDataList){
		FileOutputStream fos = null;
		FileInputStream fis = null;
		String publishRoot = WebAppUtils.getWebRootBaseDir(ConstantsDef.prodFile).replaceAll("\\\\", "\\/");
		String timeStr = DateUtil.convertDateTimeToString(resOrder.getCreateTime()).replaceAll(":", "").replaceAll(" ", "");
		String path = publishRoot + timeStr +"/" + resOrder.getOrderId();
		String excelName = "resource_caculate_list.xlsx";
		File file = new File(path);
		if(!file.exists()){
			file.mkdir();
		}
		createExcelByMetaData(metaDataList, path);
		try {
			XSSFRow row = null;
			XSSFCell cell = null;
			String excelPath = path + "/" + excelName;
			
			fis = new FileInputStream(new File(excelPath));
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			XSSFSheet sheet = wb.getSheetAt(0);
			row = sheet.createRow(1);
			cell = row.createCell(1);
			cell.setCellValue("客户名称为:" + resOrder.getChannelName() + "的需求单资源元数据");
			int num = 3;
			for(Ca ca : listCa){
				Map<String, String> map = ca.getMetadataMap();
				row = sheet.createRow(num);
				int cellNum = 0;
				for(String str: metaDataList){
					cell = row.createCell(cellNum);
					String value = map.get(str);
					if(StringUtils.isNotBlank(value)){
						cell.setCellValue(str);
					}else{
						cell.setCellValue("");
					}
					cellNum++;
				}
				num++;
			}
			fos = new FileOutputStream(new File(excelPath));
			wb.write(fos);
			fos.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(fos != null){
					fos.close();
				}
				if(fis != null){
					fis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static String createExcelByMetaData(List<String> metadata, String path){
		try {
			File file = new File(path);
			InputStream fis = new FileInputStream(file);
			Workbook book = ExcelUtil.createWorkBook(fis);
			Sheet sheet = ExcelUtil.getSheetFirst(book);
			int num = 0;
			Row row = sheet.getRow(0);
			for(String data: metadata){
				Cell cell = row.getCell(num);
				cell.setCellValue(data);
				num++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}  
		return path;
	}
	
	public static void main(String[] args) {
		
	}
}
