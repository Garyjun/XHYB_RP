package com.brainsoon.system.support;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import com.brainsoon.common.po.tree.TreeNode;
import com.brainsoon.common.service.IBaseService;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.system.model.ResCategory;
import com.brainsoon.system.service.IFLTXService;

public class ExportResCategoryUtil {
	private List<ResCategory> resList;
	private IBaseService baseQueryService;
	public ExportResCategoryUtil(List<ResCategory> list){
		resList = list;
		try {
			baseQueryService = (IBaseService) BeanFactoryUtil.getBean("baseService");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public File exportExcel(String name){
		OutputStream out = null;
		File excel = new File("分类体系");
		XSSFWorkbook workbook = new XSSFWorkbook();
		try {
			XSSFSheet sheet= workbook.createSheet("分类体系");
			createTableHead(workbook,sheet,name);
			createTableContent(sheet);
			out = new FileOutputStream(excel);
			workbook.write(out);			
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			if(out!=null){
				try {
					out.flush(); 
					out.close();
				} catch (IOException e2) {
					throw new RuntimeException(e2.getMessage(), e2);
				}
			}			
		}
		return excel;
	}
	
	private void createTableHead(XSSFWorkbook workbook,XSSFSheet sheet,String name){
		XSSFRow row = sheet.createRow(0);
		XSSFCell cell = row.createCell(0);
		XSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		XSSFFont font = workbook.createFont();
		font.setFontName("黑体");
		font.setFontHeightInPoints((short) 18);
		cellStyle.setFont(font);
		cell.setCellValue(name);
		cell.setCellStyle(cellStyle);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));
	}
	
	private void createTableContent(XSSFSheet sheet){
		int leafIndex = 0;
		for(int i=0; i<resList.size(); i++){
			ResCategory rc = resList.get(i);
			if(isLeaf(rc)){
				XSSFRow row = sheet.createRow(leafIndex+2);
				leafIndex++;
				while(rc!=null){
					createCellContent(rc,row);
					rc = getParentNode(rc);
				}
			}
		}
	}
	
	private boolean isLeaf(ResCategory rc){
		List<ResCategory> list = baseQueryService.query("from ResCategory where pid =" + rc.getId());
		if(list!=null&&list.size()>0)
			return false;
		else
			return true;
	}
	
	private ResCategory getParentNode(ResCategory rc){
		ResCategory resCategory = (ResCategory) baseQueryService.getByPk(
				ResCategory.class, rc.getPid());
		return resCategory;
	}
	
	private void createCellContent(ResCategory rc,XSSFRow row){
		String[] array = rc.getPath().split(",");
		int level = array[0].equals("0")?array.length-2:array.length-1;
		row.createCell(level).setCellValue(rc.getName());
	}
}
