/*
 * author: jiaoyongjie
 * date:   2009年1月11号
 */
package com.brainsoon.common.util.excel;
import java.text.ParseException;
import org.apache.commons.validator.GenericValidator;
import com.brainsoon.common.util.date.DateUtil;
import com.brainsoon.common.util.excel.exception.*;
import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;
import jxl.NumberCell;
import jxl.Sheet;

/**
 * JXL组件包装类
 * 所有的行号、列号均从1开始
 * @author jiaoyongjie
 *
 */
public class JXLUtils {
	
	/**
	 * 
	 * @param sheet 工作薄
	 * @param row 行号
	 * @param col 列号
	 * @return 工作薄中指定行号、列号的字符串对象
	 */
	public static String getString(Sheet sheet, int row, int col){
		if(sheet == null){
			return null;
		}
		String s = null;
		Cell cell = null;
		if(col <= sheet.getColumns() && row <= sheet.getRows()){
			cell = sheet.getCell(col-1, row-1);
		}
		if(cell != null && !GenericValidator.isBlankOrNull(cell.getContents().trim())){
			s = cell.getContents().trim();
		}
		return s;
	}
	
	/**
	 * 
	 * @param sheet 工作薄
	 * @param row 行号
	 * @param col 列号
	 * @param datePattern 日期格式
	 * @return 工作薄中指定行号、列号的日期对象
	 * @throws DateFormatException 日期格式错误的异常
	 * @throws ParseException 
	 */
	public static java.util.Date getDate(Sheet sheet, int row, int col, String datePattern) throws DateFormatException, ParseException {
		if(sheet == null){
			return null;
		}
		if(datePattern == null){
			datePattern = DateUtil.datePattern;
		}
		java.util.Date date = null;
		Cell cell = null;
		if(col <= sheet.getColumns() && row <= sheet.getRows()){
			cell = sheet.getCell(col-1, row-1);
		}
		if(cell != null && !GenericValidator.isBlankOrNull(cell.getContents().trim())){
			if(cell.getType() == CellType.DATE){
				DateCell dateCell = (DateCell)cell;
				date = dateCell.getDate();
			}else if(GenericValidator.isDate(cell.getContents().trim(), datePattern, false)){
				date = DateUtil.convertStringToDate(cell.getContents().trim(), datePattern);
			}else{
				throw new DateFormatException();
			}
		}
		return date;
	}
	
	/**
	 * 
	 * @param sheet 工作薄
	 * @param row 行号
	 * @param col 列号
	 * @return 工作薄中指定行号、列号的Double对象
	 * @throws DoubleFormatException Double格式异常
	 */
	public static Double getDouble(Sheet sheet, int row, int col) throws DoubleFormatException {
		if(sheet == null){
			return null;
		}
		Double d = null;
		Cell cell = null;
		if(col <= sheet.getColumns() && row <= sheet.getRows()){
			cell = sheet.getCell(col-1, row-1);
		}
		if(cell != null && !GenericValidator.isBlankOrNull(cell.getContents().trim())){
			if(cell.getType() == CellType.NUMBER || cell.getType() == CellType.NUMBER_FORMULA){
				NumberCell numberCell = (NumberCell)cell;
				d = new Double(numberCell.getValue());
			}else if(GenericValidator.isDouble(cell.getContents().trim())){
				d = new Double(cell.getContents().trim());
			}else{
				throw new DoubleFormatException();
			}
		}
		return d;
	}
	
	/**
	 * 
	 * @param sheet 工作薄
	 * @param row 行号
	 * @param col 列号
	 * @return 工作薄中指定行号、列号的Integer对象
	 * @throws IntegerFormatException Integer格式异常
	 */
	public static Integer getInteger(Sheet sheet, int row, int col) throws IntegerFormatException {
		if(sheet == null){
			return null;
		}
		Integer i = null;
		Cell cell = null;
		if(col <= sheet.getColumns() && row <= sheet.getRows()){
			cell = sheet.getCell(col-1, row-1);
		}
		if(cell != null && !GenericValidator.isBlankOrNull(cell.getContents().trim())){
			if(GenericValidator.isInt(cell.getContents().trim())){
				i = new Integer(cell.getContents().trim());
			}else{
				throw new IntegerFormatException();
			}
		}
	
		return i;
	}
	
	/**
	 * 
	 * @param sheet 工作薄
	 * @param row 行号
	 * @param col 列号
	 * @return 工作薄中指定行号、列号的Long对象
	 * @throws LongFormatException 
	 */
	public static Long getLong(Sheet sheet, int row, int col) throws LongFormatException {
		if(sheet == null){
			return null;
		}
		Long i = null;
		Cell cell = null;
		if(col <= sheet.getColumns() && row <= sheet.getRows()){
			cell = sheet.getCell(col-1, row-1);
		}
		if(cell != null && !GenericValidator.isBlankOrNull(cell.getContents().trim())){
			if(GenericValidator.isLong(cell.getContents().trim())){
				i = new Long(cell.getContents().trim());
			}else{
				throw new LongFormatException();
			}
		}
	
		return i;
	}
}



