package com.brainsoon.statistics.support;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.WebAppUtils;

/**
 * 导出工具类
 * @author zuo
 *
 */
public class StatisticsExcelUtils {
	transient static final Log logger = LogFactory.getLog(StatisticsExcelUtils.class);
	private static DateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	private static String fileTemp = WebAppUtils.getWebRootBaseDir(ConstantsDef.fileTemp);
	
	public static File getExcelFile(String templete,List<Object> datas) {
		String timeString = simpleDateFormat.format(new Date());
        HSSFWorkbook workbook = null;
        try {
            workbook = new HSSFWorkbook(StatisticsExcelUtils.class.getResourceAsStream("/"+templete));
        } catch (FileNotFoundException e1) {
            logger.error(e1);
        } catch (IOException e1) {
            logger.error(e1);
        }
        HSSFSheet sheet = workbook.getSheetAt(0);
       
        if(null != datas){
        	HSSFRow rowTemp = sheet.getRow(2);
            List<HSSFCell> cellList = new LinkedList<HSSFCell>();
            int lastCell = rowTemp.getLastCellNum();
            for (int i = 0; i < lastCell; i++) {
            	cellList.add(rowTemp.getCell(i));
			}
            int rowNum = 2;
        	for (Object data : datas) {
        		HSSFRow row = sheet.createRow(rowNum++);
				coverValueFromObject(cellList, row, data);
			}
        }
        File file = new File(fileTemp + timeString +".xls");
        // 创建一个File 拿来当缓存用.也就是先将内存中的excel写入File中.然后再将File转换成输出流
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
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
        return file;
    }
	
	private static void coverValueFromObject(List<HSSFCell> cellList,
            HSSFRow row, Object data) {
        int i = 0;
        for (HSSFCell cell : cellList) {
            HSSFCell cellNew = row.createCell(i++);
            cellNew.setCellStyle(cell.getCellStyle());
            cellNew.setCellValue(ognlToValue(cell.getStringCellValue(),data));
        }
    }
	/**
     * @param stringCellValue
     * @param data
     * @return
     */
    private static String ognlToValue(String stringCellValue,
    		Object data) {
        Object propertyValue = null;
        try {
        	propertyValue = BeanUtils.getProperty(data,stringCellValue);
        } catch (IllegalAccessException e) {
            logger.error(e);
        } catch (InvocationTargetException e) {
            logger.error(e);
        } catch (NoSuchMethodException e) {
            logger.error(e);
        } catch (Exception e) {
            logger.error(e);
        }
        if (propertyValue instanceof Date) {
            propertyValue = formatter.format((Date) propertyValue);
        }
        return propertyValue != null ? String.valueOf(propertyValue) : "";
    }
}
