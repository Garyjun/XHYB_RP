package com.brainsoon.common.util.dofile.content;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.text.DecimalFormat;
import java.text.Format;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


/**
 * 
 * @ClassName: ExcelUtil
 * @Description: POI 操作excel ,支持03xls 和07xlsx，通用工具类
 * @author tanghui
 * @date 2014-10-30 上午9:07:39
 * 
 */
public class ExcelUtil {

	/**
	 * 创建Workbook
	 * 
	 * @param in
	 * @return Workbook
	 * @throws IOException
	 * @throws InvalidFormatException
	 */
	public static Workbook createWorkBook(InputStream in) throws IOException,
			InvalidFormatException {
		if (!in.markSupported()) {
			in = new PushbackInputStream(in, 8);
		}
		if (POIFSFileSystem.hasPOIFSHeader(in)) {
			return new HSSFWorkbook(in);
		}
		if (POIXMLDocument.hasOOXMLHeader(in)) {
			return new XSSFWorkbook(OPCPackage.open(in));
		}
		throw new IllegalArgumentException("你的excel版本目前poi解析不了");
	}

	/**
	 * 得到工作表
	 * 
	 * @param book
	 * @return
	 */
	public static Sheet getSheetFirst(Workbook book) {
		return book.getSheetAt(0);
	}

	/**
	 * 得到工作表
	 * 
	 * @param book
	 * @return
	 */
	public static Sheet getSheetFirst(InputStream in) throws IOException,
			InvalidFormatException {
		Sheet sheet = null;
		try {
			if (!in.markSupported()) {
				in = new PushbackInputStream(in, 8);
			}
			if (POIFSFileSystem.hasPOIFSHeader(in)) {
				sheet = new HSSFWorkbook(in).getSheetAt(0);
			}
			if (POIXMLDocument.hasOOXMLHeader(in)) {
				sheet = new XSSFWorkbook(OPCPackage.open(in)).getSheetAt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return sheet;
	}

	/**
	 * 表头的名称第一位
	 * 
	 * @param sheet
	 * @return
	 */
	public static String getHeadFirst(Sheet sheet) {
		Row row = sheet.getRow(0);
		if (row != null) {
			return row.getCell(0).getRichStringCellValue().getString();
		}
		return null;
	}

	/**
	 * 获取单元格数据
	 * 
	 * @param cell
	 * @return String
	 * @author
	 */
	public static Object getCellValue(Cell cell) {
		if (cell == null) {
			return "";
		}
		Object obj = new Object();
		try {
			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_STRING:// 字符串型
				obj = cell.getStringCellValue();
				break;
			case Cell.CELL_TYPE_NUMERIC:// 数值型
				if (DateUtil.isCellDateFormatted(cell)) {// 日期
					obj = cell.getDateCellValue();
				} else {
					Format format = new DecimalFormat("000");// 003类型的
					obj = format.format(cell.getNumericCellValue());
				}
				break;
			case Cell.CELL_TYPE_BOOLEAN:// 布尔型
				obj = String.valueOf(cell.getBooleanCellValue());
				break;
			case Cell.CELL_TYPE_FORMULA:// 公式型
				obj = String.valueOf(cell.getCellFormula());
				break;
			case Cell.CELL_TYPE_ERROR:// 错误
				obj = cell.getErrorCellValue();
				break;
			case Cell.CELL_TYPE_BLANK:// 空值
				obj = "";
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}

	/**
	 * 格式化数值按类型
	 * 
	 * @param cell
	 * @return object
	 */
	public static String convNUMERIC(Cell cell, String type) {
		if (cell == null) {
			return "";
		}
		if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
			Format format = new DecimalFormat(type);
			return format.format(cell.getNumericCellValue());
		} else {
			return "";
		}
	}
}
