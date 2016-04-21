/*
 * author: jiaoyongjie
 * date:   2009年1月11号
 */
package com.brainsoon.common.util.excel;

import java.io.File;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.List;
import com.brainsoon.common.util.date.DateUtil;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.NumberFormat;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class ExcelCreateUtil {
	
	private static WritableWorkbook book;
	/**
	 * 创建工作薄
	 * @param fileName 工作薄路径名 如 c:\\test.xls
	 * @return
	 * @throws Exception
	 */
	public static void createExcel(String fileName) throws Exception
	{
		//打开文件  
		File file=new File(fileName);
		
	    book=Workbook.createWorkbook(file);
	   

	}
	
	
	/**
	 * 创建工作薄
	 * @throws Exception
	 */
	public static void createExcel(OutputStream os) throws Exception
	{
	
		
	    book=Workbook.createWorkbook(os);
	   

	}
	/**
	 * 保存并关闭工作薄
	 * @throws Exception
	 */
	public static void saveAndcloseExcel() throws Exception
	{
		if(book!=null)
		{
			book.write();
			book.close();
		}
	}
	/**
	 * 创建工作表
	 * @param book 工作薄
	 * @param sheetName 工作表名
	 * @param sheetNum 第几页   
	 * @return
	 * @throws Exception
	 */
	
	public static WritableSheet createExcelSheet(String sheetName,int sheetNum) throws Exception
	{
	
		//	  生成名为sheetName的工作表，参数sheetNum表示这是第几页   
		WritableSheet sheet=book.createSheet(sheetName,sheetNum-1);
		
      return sheet;
	}
	/**
	 * 增加字符形式的单元格
	 * @param sheet 工作表
	 * @param row 行号
	 * @param col 列号
	 * @param content 内容
	 */
	public static void addStringCell(WritableSheet sheet,int row,int col,String content) throws Exception
	{
		Label labelCell  =   new  Label( col-1,  row-1, content ); 
		sheet.addCell(labelCell);
	}
	/**
	 * 增加数字形式的单元格
	 * @param sheet 工作表
	 * @param row  行号
	 * @param col 列号
	 * @param content 内容
	 * @throws Exception
	 */
	public static void addNumberCell(WritableSheet sheet,int row,int col,double content)throws Exception
	{
	   jxl.write.Number numCell=new jxl.write.Number(col-1,row-1,content);
	   sheet.addCell(numCell);
	}
	
	/**
	 * 增加数字形式的单元格
	 * @param sheet 工作表
	 * @param row  行号
	 * @param col 列号
	 * @param content 内容
	 * @throws Exception
	 */
	public static void addNumberCell(WritableSheet sheet,int row,int col,double content,WritableCellFormat dataFormat)throws Exception
	{
	   jxl.write.Number numCell=new jxl.write.Number(col-1,row-1,content,dataFormat);
	   
	   sheet.addCell(numCell);
	}
	/**
	 * 增加日期形式的单元格
	 * @param sheet
	 * @param row
	 * @param col
	 * @param date
	 */
	public static void addDateCell(WritableSheet sheet,int row,int col,String strDate,String datePattern)throws Exception
	{
		
		if(datePattern == null){
			datePattern = DateUtil.datePattern;
		}
	 
		jxl.write.DateFormat df = new jxl.write.DateFormat(datePattern); 
		jxl.write.WritableCellFormat wcfDF = new jxl.write.WritableCellFormat(df); 
		jxl.write.DateTime dateCell = new jxl.write.DateTime(col-1,row-1,Timestamp.valueOf(strDate), wcfDF); 
		
		
		sheet.addCell(dateCell);
		
	}
	
	public void  expExcel(OutputStream os,String[] titles,List result) throws Exception
	{
		
		ExcelCreateUtil.createExcel(os);
		WritableSheet sheet=ExcelCreateUtil.book.createSheet("会员列表", 1);
		for(int i=0;i<titles.length;i++)
		{
			ExcelCreateUtil.addStringCell(sheet, 1,i+1, titles[i]);
		}
		for(int i=0;i<result.size();i++)
		{
			Object[] objs=(Object[])result.get(i);
			for(int j=1;j<objs.length;j++)
			{
				if( objs[j]!=null)
				{
					String cont=objs[j].toString().replace("|","<br>");
					ExcelCreateUtil.addStringCell(sheet, i+2,j, cont);
				}
				
			}
			
		}
		ExcelCreateUtil.saveAndcloseExcel();
		
	}
	
	public static void main(String[] args) {
		try {
		
			ExcelCreateUtil.createExcel("c:\\t3.xls");
			WritableFont dataFont = new WritableFont(WritableFont.TIMES);
			dataFont.setPointSize(10);
			WritableCellFormat data3Format = new WritableCellFormat(dataFont,new NumberFormat("#,##0.0000"));
			
			WritableSheet sheet=ExcelCreateUtil.book.createSheet("first Sheet ", 1);
			ExcelCreateUtil.addStringCell(sheet, 1, 1, "11cell");
			ExcelCreateUtil.addStringCell(sheet, 1, 2, "12cell");
			ExcelCreateUtil.addNumberCell(sheet,2, 1,Double.parseDouble("541636.28569996282958984984"),data3Format);
			ExcelCreateUtil.addNumberCell(sheet,2, 2,541636.28569996282958984984);
		
			//ExcelCreateUtil.addDateCell(sheet,2, 2,"2008-10-06 16:42:11.000000", DateHelper.DEFAULT_DATE_PATTERN);
			ExcelCreateUtil.saveAndcloseExcel();
			System.out.println("success!!!");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}


}
