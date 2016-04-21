package com.brainsoon.common.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class CreateJenaFile {
	public static void main(String[] args) throws Exception {
		try {
			String excelPath = "D:\\图书资源资源模板.xls";
			parseExcel(excelPath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	public static void parseExcel(String excelPath) throws Exception{
		String filePath = "D:/1.txt";
		try {
			RDFFileUtil.creatTxtFile(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		File file = new File(excelPath);
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
		POIFSFileSystem fs = new POIFSFileSystem(in);
		HSSFWorkbook wb = new HSSFWorkbook(fs);
		HSSFSheet sheet = wb.getSheetAt(0);
		int rows = sheet.getLastRowNum();
		for(int i=0;i<rows;i++){
			String uuid = UUID.randomUUID().toString();
			HSSFRow row =sheet.getRow(i);
			HSSFCell cell = row.getCell(0);
			if(cell == null){
				continue;
			}
			int type = cell.getCellType();
			if(HSSFCell.CELL_TYPE_NUMERIC == type){
				continue;
			}
			HSSFCell cell1 = row.getCell(1);
			String value1 = cell1.getStringCellValue();
			value1 = value1.replaceAll("\"", "");
			String value0 = cell.getStringCellValue();
			value0 = value0.replaceAll("\"", "");
			String content = "";
			content+="<http://www.brainsoon.com/resource/word-"+uuid+"> <http://www.brainsoon.com/structure#Domain> \"http://www.brainsoon.com/resource/domain-d72b3bf6-7224-4756-a0c2-a4ebe8140ec1\" <http://www.brainsoon.com/kwldb> .";
			content += "\r\n";
			content+="<http://www.brainsoon.com/resource/word-"+uuid+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.brainsoon.com/structure#Word> <http://www.brainsoon.com/kwldb> .";
			content += "\r\n";
			content+= "<http://www.brainsoon.com/resource/word-"+uuid+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2004/02/skos/core#Concept> <http://www.brainsoon.com/kwldb> .";
			content += "\r\n";
			content += "<http://www.brainsoon.com/resource/word-"+uuid+"> <http://www.w3.org/2004/02/skos/core#prefLabel> \""+ value1+"\"@en <http://www.brainsoon.com/kwldb> .";
			content += "\r\n";
			content += "<http://www.brainsoon.com/resource/word-"+uuid+"> <http://www.w3.org/2004/02/skos/core#prefLabel> \""+value0+"\"@zh <http://www.brainsoon.com/kwldb> .";
			content += "\r\n";
			RDFFileUtil.writeTxtFile(content, filePath);
		}
	}
}
