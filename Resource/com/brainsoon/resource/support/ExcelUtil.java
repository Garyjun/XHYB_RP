package com.brainsoon.resource.support;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;

import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.support.GlobalDataCacheMap;
import com.brainsoon.common.support.OperDbUtils;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.common.util.date.DateUtil;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.resource.service.impl.BatchImportResService;
import com.brainsoon.resrelease.po.ResOrder;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.CaList;
import com.brainsoon.semantic.schema.ontology.CustomMetaData;
import com.brainsoon.semantic.schema.ontology.MetadataDefinition;
import com.brainsoon.semantic.schema.store.jena.service.MetaService;
import com.brainsoon.statistics.service.ISourceNumService;
import com.brainsoon.system.model.MetaDataModelGroup;
import com.brainsoon.system.service.IMetaDataModelService;
import com.brainsoon.system.service.ISysParameterService;
import com.brainsoon.system.service.IZTFLService;
import com.brainsoon.system.util.FieldExcelValidator;
import com.brainsoon.system.util.MetadataSupport;
import com.google.gson.Gson;

public class ExcelUtil {
	private static Logger logger = Logger.getLogger(ExcelUtil.class);
	private final static String FTP_LOCAL_MAPPING = WebappConfigUtil
			.getParameter("FTP_LOCAL_MAPPING");
	private static ISourceNumService sourceNumService = null;
	public static void main(String[] args){
//		String excelPath = "E:/华师京城资源/excel日志/51806.xls";
//		String excelDir = "E:/华师京城资源/excel日志/";
//		ExcelUtil.createFileNotExistLog(excelPath,excelDir);
		try {
			System.out.println(URLEncoder.encode("15 说\"屏\"", "utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 导出 元数据并生成excel
	 * 
	 * @param cas
	 * @throws Exception
	 */
	public static File createExcelByRes(List<Ca> cas, String level,
			String publishType) throws Exception {
		IZTFLService zTFLService = null;
		try {
			zTFLService = (IZTFLService) BeanFactoryUtil.getBean("ZTFLService");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		List<MetadataDefinition> metadataDefinitions = MetadataSupport
				.getMetadateDefines(publishType);
//		metadataDefinitions = ListUtils.retainAll(metadataDefinitions, list2);
		HSSFSheet oresTempleteSheet = null;
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFCellStyle style = workbook.createCellStyle();
		style.setFillForegroundColor(HSSFColor.AQUA.index);// 设置背景色
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 水平
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN); // 下边框
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);// 左边框
		style.setBorderTop(HSSFCellStyle.BORDER_THIN);// 上边框
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);// 右边框
		// 生成一个字体
		HSSFFont font = workbook.createFont();
		font.setFontName("宋体");
		font.setFontHeightInPoints((short) 13);
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); // 字体增粗
		// 把字体应用到当前的样式
		style.setFont(font);
		HSSFSheet sheet = null;
		HSSFCell cell = null;
		int i = 0;
		int y = 1;
		List<MetadataDefinition> levelMetadataDefinitions =  null;
		for (Ca ca : cas) {
			if (i % 5001 == 0) {
				sheet = workbook.createSheet("资源元数据" + y);
				y++;
				HSSFRow titlerow0 = sheet.createRow(0);
				HSSFCell cell0  = titlerow0.createCell(0);
				cell0.setCellValue("文件路径");
				cell0.setCellStyle(style);
				HSSFRow titlerow1 = sheet.createRow(1);
				titlerow1.setZeroHeight(true);
				HSSFCell cell1  = titlerow1.createCell(0);
				cell1.setCellValue("filePath");
				sheet.setColumnWidth(0, 20 * 256);
				int num1=0;
				//用于记录导出级别
//				String fileName = "";
				for(int j =0;j<metadataDefinitions.size();j++){
					if(!StringUtils.isNotBlank(level)){
					num1++;
					cell1  = titlerow1.createCell(j+1);
					cell1.setCellValue(metadataDefinitions.get(j).getFieldName());
					sheet.setColumnWidth(j+1, 20 * 256);
					
					}else{
						String levelTemp = metadataDefinitions.get(j).getExportLevel();
						String levels[] = level.split(",");
						for(String levelOne:levels){
							if(levelTemp.indexOf(levelOne)>=0){
								num1++;
								sheet.setColumnWidth(num1+1, 20 * 256);
								cell1 = titlerow1.createCell((short) num1);
								cell1.setCellValue(metadataDefinitions.get(j).getFieldName());
								break;
							}
						}
					}
				}
				cell1 = titlerow1.createCell((short) num1+1);
				cell1.setCellValue("objectId");
				sheet.setColumnHidden(num1+1,true);
				int num = 1;
				levelMetadataDefinitions = new ArrayList<MetadataDefinition>();
				for(MetadataDefinition metadataDefinition:metadataDefinitions){
					String levelTemp = metadataDefinition.getExportLevel();
					if(StringUtils.isNotBlank(level)){
						String levels[] = level.split(",");
					for(String levelOne:levels){
						if(levelTemp.indexOf(levelOne)>=0){
							sheet.setColumnWidth(num, 20 * 256);
							cell = titlerow0.createCell((short) num);
							cell.setCellStyle(style);
							cell.setCellValue(metadataDefinition.getFieldZhName());
							levelMetadataDefinitions.add(metadataDefinition);
							num++;
							break;
						}
					}
					}else{
						sheet.setColumnWidth(num, 20 * 256);
						cell = titlerow0.createCell((short) num);
						cell.setCellStyle(style);
						cell.setCellValue(metadataDefinition.getFieldZhName());
						levelMetadataDefinitions.add(metadataDefinition);
						num++;
					}
				}
				cell = titlerow0.createCell((short) num);
				sheet.setColumnWidth(num, 25 * 256);
				if(!StringUtils.isNotBlank(level)){
				cell.setCellStyle(style);
				cell.setCellValue("唯一标识符");
				}
				num++;
				i=1;
				i++;
			}
			HSSFRow row = sheet.createRow(i);
			Map<String, String> metadataMap = ca.getMetadataMap();
			for(int k = 0;k<levelMetadataDefinitions.size();k++){
				MetadataDefinition metadataDefinitionTemp = levelMetadataDefinitions.get(k);
				cell = row.createCell(k+1);
				String value = "";
				if (metadataDefinitionTemp.getFieldType() == 6) { // 分类
					String cbValue = metadataMap.get(metadataDefinitionTemp
							.getFieldName());
					if (StringUtils.isNotBlank(cbValue)) {
						value = zTFLService.queryCatagoryCnName(cbValue);
					}
				} else if(metadataDefinitionTemp.getFieldType()==2 || metadataDefinitionTemp.getFieldType()==3 || metadataDefinitionTemp.getFieldType()==4){
					//根据元数据的英文名从metadataMap取出对应值，例language=3593,
					String dicValue = metadataMap.get(metadataDefinitionTemp.getFieldName());
					if(StringUtils.isNotBlank(dicValue)){
						try {
							value = zTFLService.queryDictValue(dicValue);
						} catch (Exception e) {
							value = dicValue;
							e.printStackTrace();
						}
					}
					/*String values = "";
					String dicValue = metadataMap.get(metadataDefinitionTemp.getFieldName());
					logger.info("----------------------------原值"+dicValue+"----------------------------------------");
					if(StringUtils.isNotBlank(dicValue)){
						String div[] = dicValue.split(",");
						for(int j=0;j<div.length;j++){
							value = OperDbUtils.queryNameById(div[j]);
							values = values+value+",";
							logger.info("----------------------------values值"+values+"----------------------------------------");
						}
						value = values.substring(0,values.length()-1);
					}*/
					//人员
				}else if(metadataDefinitionTemp.getFieldType()==10){
					String dicValue = metadataMap.get(metadataDefinitionTemp.getFieldName());
					if(StringUtils.isNotBlank(dicValue)){
						String categoryName = "";
						 try {
							 String arrayStaff[] = dicValue.split(",");
							 for(String sta:arrayStaff){
								 categoryName = categoryName + GlobalDataCacheMap.getNameStaffWithNameByKeyAndChildKey(sta+"staff",dicValue)+",";
							 }
							 if(categoryName.endsWith(",")){
								 value = categoryName.substring(0,categoryName.length()-1);
							 }
							 
						} catch (Exception e) {
							value = dicValue;
						}
					}
					
					//单位
				}else if(metadataDefinitionTemp.getFieldType()==11){
					String dicValue = metadataMap.get(metadataDefinitionTemp.getFieldName());
					if(StringUtils.isNotBlank(dicValue)){
						String categoryName = "";
						 try {
							 String arrayCompany[] = dicValue.split(",");
							 for(String com:arrayCompany){
								 categoryName = categoryName + GlobalDataCacheMap.getNameCompanyWithNameByKeyAndChildKey(com+"company",dicValue)+",";
							 }
							 if(categoryName.endsWith(",")){
								 value = categoryName.substring(0,categoryName.length()-1);
							 }
							 
						} catch (Exception e) {
							value = dicValue;
						}
					}
				}else if(metadataDefinitionTemp.getFieldType()==7){
					String format = metadataDefinitionTemp.getValueRange();
					String dicValue = metadataMap.get(metadataDefinitionTemp.getFieldName());
					if (StringUtils.isNotBlank(dicValue)) {
						try {
							Date dateOld = new Date(Long.parseLong(dicValue)); // 根据long类型的毫秒数生命一个date类型的时间
							value = new SimpleDateFormat(format).format(dateOld);
						} catch (Exception e) {
							value = dicValue;
						}
					}
				}else{
					value = metadataMap.get(metadataDefinitionTemp.getFieldName());
				}
				cell.setCellValue(value);
			}
//			if(!StringUtils.isNotBlank(level)){
			cell = row.createCell(levelMetadataDefinitions.size()+1);
			cell.setCellValue(ca.getObjectId());
//			}
			i++;
		}
		File outFile = new File(BatchImportResService.FILE_TEMP
				+ File.separator + UUID.randomUUID().toString() + ".xls");
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
	public static boolean isImage(String fileType){
		boolean image = false;
		if("jpg".equalsIgnoreCase(fileType)){
			image = true;
		}else if("png".equalsIgnoreCase(fileType)){
			image = true;
		}else if("bmp".equalsIgnoreCase(fileType)){
			image = true;
		}else if("tif".equalsIgnoreCase(fileType)){
			image = true;
		}else if("tiff".equalsIgnoreCase(fileType)){
			image = true;
		}else if("jpeg".equalsIgnoreCase(fileType)){
			image = true;
		}else if("gif".equalsIgnoreCase(fileType)){
			image = true;
		}
		return image;
	}
	 /**
     * 功能：Java读取txt文件的内容
     * 步骤：1：先获得文件句柄
     * 2：获得文件句柄当做是输入一个字节码流，需要对这个输入流进行读取
     * 3：读取到输入流后，需要读取生成字节流
     * 4：一行一行的输出。readline()。
     * 备注：需要考虑的是异常情况
     * @param filePath
     */
	public static List<String> readTxtFile(String filePath) {
		List<String> context = null;
		try {
			context = new ArrayList<String>();
			String encoding = "UTF-8";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					context.add(lineTxt);
				}
				read.close();
			} else {
				logger.info("找不到指定的文件");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return context;
	}
	public static int parseExcelFile(String excelPath,Map<Integer,String> resultLog,Map<Integer,String> fileNotExistLog){
		int allRows = 0;
		try {  
            FileInputStream is = new FileInputStream(excelPath);  
            HSSFWorkbook workbook = new HSSFWorkbook(is);  
            HSSFSheet sheet = workbook.getSheetAt(0); 
            allRows = sheet.getLastRowNum();
			HSSFRow titleRow = sheet.getRow(2);
			short total = titleRow.getLastCellNum();
			for(int i=4;i<=allRows;i++){
				 HSSFRow oneRow = sheet.getRow(i);
				 logger.info("解析--------------------------"+i+"行");
				 HSSFCell cell = oneRow.getCell(total-1);
				 HSSFCell filaPathCell = oneRow.getCell(20);
				 HSSFCell fileNameCell = oneRow.getCell(21);
				 String filePath = "";
				 if(filaPathCell!=null && fileNameCell!=null){
					 filePath = FTP_LOCAL_MAPPING
								+ filaPathCell.getStringCellValue()
								+ File.separator
								+ fileNameCell.getStringCellValue();
					 filePath = filePath.replaceAll("\\\\", "/");
					 if (!new File(filePath).exists()) {
						fileNotExistLog.put(i+1,  "资源文件不存在，路径【"
								+ filePath + "】");
						continue;
					 }else{
						 if(cell == null){
							 continue;
						 }
						 String value = cell.getStringCellValue();
						 resultLog.put(i+1, value);
					 }
				 }else{
					 if(cell == null){
						 continue;
					 }
					 String value = cell.getStringCellValue();
					 resultLog.put(i+1, value);
				 }
            }
            is.close();  
        } catch (Exception e) {   
            e.printStackTrace();  
        }  
		return allRows;
	}
	public static void createFileNotExistLog(String excelPath,String excelDir){
		try {  
            FileInputStream is = new FileInputStream(excelPath);  
            HSSFWorkbook workbook = new HSSFWorkbook(is);  
            HSSFSheet sheet = workbook.getSheetAt(0); 
            int allRows = sheet.getLastRowNum();
			HSSFRow titleRow = sheet.getRow(2);
			short total = titleRow.getLastCellNum();
			for(int i=4;i<=allRows;i++){
				 HSSFRow oneRow = sheet.getRow(i);
				 HSSFCell cell = oneRow.getCell(total-1);
				 if(cell == null){
					 continue;
				 }
				 String value = cell.getStringCellValue();
				 if(value.indexOf("资源文件不存在，路径")<0){
					 HSSFRow removingRow=sheet.getRow(i);
	                 sheet.removeRow(removingRow);
				 }
            }
            String uuid = "";
			uuid = UUID.randomUUID().toString();
			String tempExcelPath = excelDir+uuid+".xls";
            FileOutputStream os = new FileOutputStream(tempExcelPath);  
            workbook.write(os); 
            is.close();  
            os.close();  
//            String fileNotExistPath = excelDir+"文件不存在日志.xls";
//            removeBlankRow(tempExcelPath,fileNotExistPath);
        } catch (Exception e) {   
            e.printStackTrace();  
        }  
	}
	
	public static void createFailLog(String excelPath,String excelDir){
		try {  
            FileInputStream is = new FileInputStream(excelPath);  
            HSSFWorkbook workbook = new HSSFWorkbook(is);  
            HSSFSheet sheet = workbook.getSheetAt(0); 
            int allRows = sheet.getLastRowNum();
			HSSFRow titleRow = sheet.getRow(2);
			short total = titleRow.getLastCellNum();
			for(int i=4;i<=allRows;i++){
				 HSSFRow oneRow = sheet.getRow(i);
				 HSSFCell cell = oneRow.getCell(total-1);
				 if(cell == null){
					 continue;
				 }
				 String value = cell.getStringCellValue();
				 if(value.indexOf("资源文件不存在，路径")>=0){
					 HSSFRow removingRow=sheet.getRow(i);
	                 sheet.removeRow(removingRow);
				 }
            }
            String uuid = "";
			uuid = UUID.randomUUID().toString();
			String tempExcelPath = excelDir+uuid+".xls";
            FileOutputStream os = new FileOutputStream(tempExcelPath);  
            workbook.write(os); 
            is.close();  
            os.close();  
            String fileNotExistPath = excelDir+"失败日志(不包含文件不存在).xls";
            removeBlankRow(tempExcelPath,fileNotExistPath);
        } catch (Exception e) {   
            e.printStackTrace();  
        }  
	}
	public static void insertFailLog(String excelPath,String newPath,Map<Integer,String> resultLog,int allTotal){
		try {  
            FileInputStream is = new FileInputStream(excelPath);  
            HSSFWorkbook workbook = new HSSFWorkbook(is);  
            HSSFSheet sheet = workbook.getSheetAt(0); 
            boolean isNeedAddCol = true;
            HSSFRow titleRow = sheet.getRow(1);
            short total = titleRow.getLastCellNum();
            HSSFCell cell = titleRow.getCell(total-1);
            String cellValue = cell.getStringCellValue();
            if(cellValue!=null && !"".equals(cellValue) && "失败原因".equals(cellValue)){
            	isNeedAddCol = false;
            }else{
            	HSSFCell newCell = titleRow.createCell(total);
            	newCell.setCellValue("失败原因");
            	newCell.setCellStyle(getTitleStyle(workbook));
            	sheet.setColumnWidth(total, (short) 50 * 25 * 12);
            }
            HSSFCell cellTemp = null;
            //在相应的行列添写失败信息
            List<Integer> needNotDelRow = new ArrayList<Integer>();
            for(Map.Entry<Integer, String> entry:resultLog.entrySet()){ 
            	
            	HSSFRow  row = sheet.getRow(entry.getKey()-1);
            	if(row==null){
            		row = sheet.getRow(entry.getKey());
            	}
            	if(isNeedAddCol){
            		cellTemp = row.createCell(total);
            	}else{
            		cellTemp = row.getCell(total-1);
            	}
            	if(cellTemp!=null){
            		cellTemp.setCellValue(entry.getValue());
                	needNotDelRow.add(entry.getKey());
                	logger.info("fffffffff======="+entry.getKey());
            	}
            }
            for(int i=4;i<allTotal+4;i++){
            	if(!needNotDelRow.contains(i)){
            		HSSFRow removingRow=sheet.getRow(i-1);
            		if(removingRow !=null){
            			sheet.removeRow(removingRow);
            		}
            	}
            }
//            HSSFRow titleRow1 = sheet.getRow(1);
//            titleRow1.setZeroHeight(true);
            FileOutputStream os = new FileOutputStream(newPath);  
            workbook.write(os); 
            is.close();  
            os.close();  
//            resultLog.clear();
//            resultLog = null;
        } catch (Exception e) {   
            e.printStackTrace();  
        }  
	}
	
	public static void insertFileNotExistLog(String excelPath,String newPath,Map<Integer,String> resultLog,int allTotal){
		try {  
            FileInputStream is = new FileInputStream(excelPath);  
            HSSFWorkbook workbook = new HSSFWorkbook(is);  
            HSSFSheet sheet = workbook.getSheetAt(0); 
            boolean isNeedAddCol = true;
            HSSFRow titleRow = sheet.getRow(1);
            short total = titleRow.getLastCellNum();
            HSSFCell cell = titleRow.getCell(total-1);
            String cellValue = cell.getStringCellValue();
            if(cellValue!=null && !"".equals(cellValue) && "失败原因".equals(cellValue)){
            	isNeedAddCol = false;
            }else{
            	HSSFCell newCell = titleRow.createCell(total);
            	newCell.setCellValue("失败原因");
            	newCell.setCellStyle(getTitleStyle(workbook));
            	sheet.setColumnWidth(total, (short) 50 * 25 * 12);
            	titleRow.setHeight((short) 280);
            }
            HSSFCell cellTemp = null;
            List<Integer> needNotDelRow = new ArrayList<Integer>();
            for(Map.Entry<Integer, String> entry:resultLog.entrySet()){
            	HSSFRow row = sheet.getRow(entry.getKey()-1);  
            	if(isNeedAddCol){
            		cellTemp = row.createCell(total);
            	}else{
            		cellTemp = row.getCell(total-1);
            	}
            	if(cellTemp!=null){
            		cellTemp.setCellValue(entry.getValue());
                	needNotDelRow.add(entry.getKey());
                	logger.info("fffffffff======="+entry.getKey());
            	}
            } 
            for(int i=4;i<allTotal+4;i++){
            	if(!needNotDelRow.contains(i)){
            		HSSFRow removingRow=sheet.getRow(i-1);
            		if(removingRow !=null){
            			sheet.removeRow(removingRow);
            		}
            	}
            }
            HSSFRow titleRow1 = sheet.getRow(1);
            titleRow1.setZeroHeight(true);
//            File file = new File(newPath);
//            if(!file.exists()){
//            	file.mkdirs();
//            }
            FileOutputStream os = new FileOutputStream(newPath);
            workbook.write(os);
            os.flush();
            os.close(); 
            is.close();  
//            resultLog.clear();
//            resultLog = null;
        } catch (Exception e) {
            e.printStackTrace();  
        }
	}
	public static void removeSuccRow(String excelPath,String newPath,List<Integer> succRowNums){
		try {  
            FileInputStream is = new FileInputStream(excelPath);  
            HSSFWorkbook workbook = new HSSFWorkbook(is);
            HSSFSheet sheet = workbook.getSheetAt(0); 
            for(int succRowNum:succRowNums){
            	HSSFRow removingRow=sheet.getRow(succRowNum-1);
            	sheet.removeRow(removingRow);
            }
            FileOutputStream os = new FileOutputStream(newPath);
            workbook.write(os);  
            is.close();  
            os.close();  
        } catch (Exception e) {   
            e.printStackTrace();  
        }  
	}
	/**
	 * 删除空白行
	 * @param excelPath
	 * @param newPath
	 */
	public static void removeBlankRow(String excelPath,String newPath){
		try {  
            FileInputStream is = new FileInputStream(excelPath);  
            HSSFWorkbook workbook = new HSSFWorkbook(is);  
            HSSFSheet sheet = workbook.getSheetAt(0); 
            HSSFRow tempRow;
            int i = sheet.getLastRowNum();
			while (i > 0) {
				i--;
				tempRow = sheet.getRow(i);
				if ((tempRow == null) || (tempRow != null && tempRow.getCell(0) ==null && tempRow.getCell(1) ==null)) {
					sheet.shiftRows(i + 1, sheet.getLastRowNum(), -1);
				}
			}
            FileOutputStream os = new FileOutputStream(newPath);  
            workbook.write(os);  
            is.close();
            os.close();
        } catch (Exception e) {   
            e.printStackTrace();  
        }  
	}
	public static void removeRow(HSSFSheet sheet, int rowIndex) {
	    int lastRowNum=sheet.getLastRowNum();
	    if(rowIndex>=0&&rowIndex<lastRowNum)
	        sheet.shiftRows(rowIndex+1,lastRowNum,-1);//将行号为rowIndex+1一直到行号为lastRowNum的单元格全部上移一行，以便删除rowIndex行
	    if(rowIndex==lastRowNum){
	        HSSFRow removingRow=sheet.getRow(rowIndex);
	        if(removingRow!=null)
	            sheet.removeRow(removingRow);
	    }
	}
	/**
	 * 标题的格式
	 */
	private static  HSSFCellStyle getTitleStyle(HSSFWorkbook wb) {
		HSSFCellStyle cellStyle = wb.createCellStyle();
		HSSFDataFormat format = wb.createDataFormat();  
		cellStyle.setDataFormat(format.getFormat("@"));  
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		cellStyle.setFont(getFirstHdrFont(wb));
		cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		cellStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
		cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		return cellStyle;
	}
	/**
	 * 主标题字体
	 * 
	 * @param wb
	 * @return
	 */
	private static  HSSFFont getFirstHdrFont(HSSFWorkbook wb) {
		HSSFFont fontStyle = wb.createFont();
		fontStyle.setFontName("微软雅黑");
		fontStyle.setFontHeightInPoints((short) 15);
		fontStyle.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		return fontStyle;
	}
	/**
	 * 导出 元数据并生成excel
	 * 
	 * @param cas
	 * @throws Exception
	 */
	public static File createExcelBeachByRes(CaList caList, List<MetadataDefinition> list2,
			String publishType,String level,String publishDir) throws Exception {
		IZTFLService zTFLService = null;
		try {
			zTFLService = (IZTFLService) BeanFactoryUtil.getBean("ZTFLService");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		List<MetadataDefinition> metadataDefinitions = MetadataSupport.getMetadateDefines(publishType);
		if(list2.size()>0){
			metadataDefinitions = ListUtils.retainAll(metadataDefinitions, list2);
		}
		HSSFSheet oresTempleteSheet = null;
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFCellStyle style = workbook.createCellStyle();
		style.setFillForegroundColor(HSSFColor.AQUA.index);// 设置背景色
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 水平
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN); // 下边框
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);// 左边框
		style.setBorderTop(HSSFCellStyle.BORDER_THIN);// 上边框
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);// 右边框
		// 生成一个字体
		HSSFFont font = workbook.createFont();
		font.setFontName("宋体");
		font.setFontHeightInPoints((short) 13);
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); // 字体增粗
		// 把字体应用到当前的样式
		style.setFont(font);
//		HSSFCellStyle style1 = workbook.createCellStyle();
//		HSSFFont font1 = workbook.createFont();
//		font1.setFontHeightInPoints((short) 9);
//		style1.setFillForegroundColor(HSSFColor.AQUA.index);// 设置背景色
//		style1.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
//		style1.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 水平
//		style1.setBorderBottom(HSSFCellStyle.BORDER_THIN); // 下边框
//		style1.setBorderLeft(HSSFCellStyle.BORDER_THIN);// 左边框
//		style1.setBorderTop(HSSFCellStyle.BORDER_THIN);// 上边框
//		style1.setBorderRight(HSSFCellStyle.BORDER_THIN);// 右边框
//		style1.setFont(font1);
		HSSFSheet sheet = null;
		HSSFCell cell = null;
		int i = 0;
		int y = 1;
		List<MetadataDefinition> levelMetadataDefinitions =  null;
		List<Ca> calListStr = caList.getCas();
		if(calListStr != null && calListStr.size() > 0){
			 for(Ca ca : calListStr){
				if (i % 5001 == 0) {
					sheet = workbook.createSheet("资源元数据" + y);
	//				HSSFRow titleRow = sheet.createRow(0);
	//				HSSFRow titleRow1 = sheet.createRow(1);
					y++;
					HSSFRow titlerow0 = sheet.createRow(0);
	//				HSSFCell cell0  = titlerow0.createCell(0);
	//				cell0.setCellValue("文件路径");
	//				cell0.setCellStyle(style);
					HSSFRow titlerow1 = sheet.createRow(1);
					titlerow1.setZeroHeight(true);
					HSSFCell cell1  = titlerow1.createCell(0);
	//				cell1.setCellValue("filePath");
					sheet.setColumnWidth(0, 20 * 256);
					int num1=0;
					String fileName = "";
					for(int j =0;j<metadataDefinitions.size();j++){
						num1++;
						if(!StringUtils.isNotBlank(level)){
						cell1  = titlerow1.createCell(j+1);
						cell1.setCellValue(metadataDefinitions.get(j).getFieldName());
						sheet.setColumnWidth(j+1, 20 * 256);
						
						}else{
							String levelTemp = metadataDefinitions.get(j).getExportLevel();
							String levels[] = level.split(",");
							for(String levelOne:levels){
								if(levelTemp.indexOf(levelOne)>=0){
									sheet.setColumnWidth(j+1, 20 * 256);
									cell1 = titlerow1.createCell((short) j+1);
									cell1.setCellValue(metadataDefinitions.get(j).getFieldName());
									break;
								}
							}
						}
					}
	//				cell1 = titlerow1.createCell((short) num1+1);
	//				cell1.setCellValue("objectId");
					int num = 0;
					levelMetadataDefinitions = new ArrayList<MetadataDefinition>();
					for(MetadataDefinition metadataDefinition:metadataDefinitions){
						String levelTemp = metadataDefinition.getExportLevel();
						if(StringUtils.isNotBlank(level)){
							String levels[] = level.split(",");
						for(String levelOne:levels){
							if(levelTemp.indexOf(levelOne)>=0){
								sheet.setColumnWidth(num, 20 * 256);
								cell = titlerow0.createCell((short) num);
								cell.setCellStyle(style);
								cell.setCellValue(metadataDefinition.getFieldZhName());
								levelMetadataDefinitions.add(metadataDefinition);
								num++;
								break;
							}
						}
						}else{
							sheet.setColumnWidth(num, 20 * 256);
							cell = titlerow0.createCell((short) num);
							cell.setCellStyle(style);
							cell.setCellValue(metadataDefinition.getFieldZhName());
							levelMetadataDefinitions.add(metadataDefinition);
							num++;
						}
					}
	//				cell = titlerow0.createCell((short) num);
					sheet.setColumnWidth(num, 25 * 256);
	//				if(!StringUtils.isNotBlank(level)){
	//				cell.setCellStyle(style);
	//				cell.setCellValue("唯一标识符");
	//				}
					num++;
					i=1;
					i++;
				}
				HSSFRow row = sheet.createRow(i);
				Map<String, String> metadataMap = ca.getMetadataMap();
				for(int k = 0;k<levelMetadataDefinitions.size();k++){
					MetadataDefinition metadataDefinitionTemp = levelMetadataDefinitions.get(k);
					cell = row.createCell(k);
					String value = "";
					if (metadataDefinitionTemp.getFieldType() == 6) { // 分类
						String cbValue = metadataMap.get(metadataDefinitionTemp
								.getFieldName());
						if (StringUtils.isNotBlank(cbValue)) {
							value = zTFLService.queryCatagoryCnName(cbValue);
						}
					} else {
						if(metadataDefinitionTemp.getFieldType()==9){
							String values = "";
							String dicValue = metadataMap.get(metadataDefinitionTemp.getFieldName());
							logger.info("----------------------------原值"+dicValue+"----------------------------------------");
							if(StringUtils.isNotBlank(dicValue)){
							String div[] = dicValue.split(",");
							for(int j=0;j<div.length;j++){
								value = OperDbUtils.queryNameById(div[j]);
								values = values+value+",";
								logger.info("----------------------------values值"+values+"----------------------------------------");
							}
							value = values.substring(0,values.length()-1);
							}
						}else{
							value = metadataMap.get(metadataDefinitionTemp
								.getFieldName());
						}
					}
					cell.setCellValue(value);
				}
	//			if(!StringUtils.isNotBlank(level)){
	//			cell = row.createCell(levelMetadataDefinitions.size()+1);
	//			cell.setCellValue(ca.getObjectId());
	//			}
				i++;
			}
		}
//		String publishRoot = WebAppUtils.getWebRootBaseDir(ConstantsDef.prodFile).replaceAll("\\\\", "\\/");
//		String time2Str = DateUtil.convertDateTimeToString(resOrder.getCreateTime()).replace(":", "").replace(" ", "");
//		String publishDir = publishRoot + time2Str + "/"+ resOrder.getOrderId() + "/";//源文件发布路径
//		File dirFile = new File(publishDir);
//		if(!dirFile.exists()){
//			dirFile.mkdirs();
//		}
		MetaDataModelGroup dataModelGroup = (MetaDataModelGroup) zTFLService.getByPk(MetaDataModelGroup.class, Long.decode(publishType));
		File outFile = new File(publishDir +dataModelGroup.getTypeName()+"元数据.xls");
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
}
