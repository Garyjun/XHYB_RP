package com.brainsoon.system.support;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.brainsoon.appframe.util.StringUtil;
import com.brainsoon.common.po.tree.TreeNode;

public class ExcelUtil {
	private List<TreeNode> nodeList = new ArrayList<TreeNode>();
	private Map<String,TreeNode> map = new HashMap<String, TreeNode>();
	public List<TreeNode> importNode(File excel){
		Workbook book = null;
		try {
			book = new XSSFWorkbook(new FileInputStream(excel.getAbsolutePath()));
			XSSFSheet sheet = (XSSFSheet) book.getSheetAt(0);
			//总共有多少列,从0开始
			int count = 1;
			int rowNum = sheet.getLastRowNum();
			int colNum = getColumn(sheet);
			for (int i = 1; i <= rowNum; i++) {
				XSSFRow row = sheet.getRow(i);
				for(int j=0; j<colNum; j++){
					String name = getStringCellValue(row.getCell(j)).trim();
					if(!StringUtils.isBlank(name)&&!existNode(name,i,j,sheet)){
						TreeNode node = new TreeNode();
						node.setName(name);
						node.setId(count+"");
						node.setpId(getLeftNodeId(i,j,count,sheet,nodeList));
						count ++;
						System.out.println("name="+node.getName()+" id="+node.getId()+" pid="+node.getpId());
						nodeList.add(node);
						map.put(i*10+j+"", node);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nodeList;
	}
	
	private String getLeftNodeId(int i,int j,int count, XSSFSheet sheet,List<TreeNode> nodeList){
		if(i==1&&j==0)
			return "0";
		XSSFRow row = sheet.getRow(i);
		String name = getStringCellValue(row.getCell(j-1)).trim();
		return getNodeByName(name).getId();
	}

	
    /**
     * 获取单元格数据内容为字符串类型的数据
     * 
     * @param cell Excel单元格
     * @return String 单元格数据内容
     */
    private String getStringCellValue(XSSFCell cell) {
    	if(cell==null)
    		return "";
        String strCell = "";
        switch (cell.getCellType()) {
        case XSSFCell.CELL_TYPE_STRING:
            strCell = cell.getStringCellValue();
            break;
        case XSSFCell.CELL_TYPE_NUMERIC:
            strCell = String.valueOf(cell.getNumericCellValue());
            break;
        case XSSFCell.CELL_TYPE_BOOLEAN:
            strCell = String.valueOf(cell.getBooleanCellValue());
            break;
        case XSSFCell.CELL_TYPE_BLANK:
            strCell = "";
            break;
        default:
            strCell = "";
            break;
        }
        if (strCell.equals("") || strCell == null) {
            return "";
        }
        return strCell;
    }
    
    private int getColumn(XSSFSheet sheet){
    	int column = 0;
    	int rowNum = sheet.getLastRowNum();
    	for (int i = 0; i <= rowNum; i++) {
    		int colNum = sheet.getRow(i).getLastCellNum();
    		if(column<colNum)
    			column = colNum;
    	}
    	return column;
    }
    
    private boolean existNode(String name,int rowNum,int colNum,XSSFSheet sheet){
    	if(!hasNodeName(name))
    		return false;
    	XSSFRow row = sheet.getRow(rowNum);
    	colNum--;
    	while(colNum>0){
    		String hasName = getStringCellValue(row.getCell(colNum)).trim();
			TreeNode hasNode = getNodeByName(hasName);
			TreeNode testNode = getNodeByName(name);
			if(!hasNode.getId().equals(testNode.getpId()))
				return false;
			name = hasName;
			colNum--;
    	}
    	return true;
    }
    
    private boolean hasNodeName(String name){
    	for(TreeNode node : nodeList){
    		if(name.equals(node.getName()))
    			return true;
    	}
    	return false;
    }
    
    private TreeNode getNodeByName(String name){
    	for(int i=nodeList.size()-1; i>=0; i--){
    		if(name.equals(nodeList.get(i).getName()))
    			return nodeList.get(i);
    	}
    	return null;
    }
	
	public static void main(String[] args) {
		File file = new File("d:\\test\\知识点树 - 历史.xlsx");
		ExcelUtil excelUtil = new ExcelUtil();
		List<TreeNode> list = excelUtil.importNode(file);
	}
}
