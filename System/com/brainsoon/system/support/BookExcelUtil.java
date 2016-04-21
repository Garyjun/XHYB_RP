package com.brainsoon.system.support;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
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

import com.brainsoon.common.po.tree.TreeNode;

public class BookExcelUtil {
	private List<TreeNode> nodeList = new ArrayList<TreeNode>();
	private Map<String,TreeNode> map = new HashMap<String, TreeNode>();
	private List<String> indexList = new ArrayList<String>();
	private List<String> peroidList = new ArrayList<String>();
	
	public BookExcelUtil(){
		indexList.add("版本");
		indexList.add("科目");
		indexList.add("年级");
		indexList.add("分册");
		indexList.add("章");
		indexList.add("课");
		indexList.add("节");
		peroidList.add("版本");
		peroidList.add("学段");
		peroidList.add("科目");
		peroidList.add("年级");
		peroidList.add("分册");
		peroidList.add("章");
		peroidList.add("课");
		peroidList.add("节");		
	}
	
	public List<TreeNode> importNode(File excel,String pid){
		Workbook book = null;
		try {
			book = new XSSFWorkbook(new FileInputStream(excel.getAbsolutePath()));
			XSSFSheet sheet = (XSSFSheet) book.getSheetAt(0);
			//总共有多少列,从0开始
			int count = 1;
			int rowNum = sheet.getLastRowNum();
			int colNum = getColumn(sheet);
			ArrayList<Integer> columnList = getHeadIndex(sheet);
			for (int i = 3; i <= rowNum; i++) {
				XSSFRow row = sheet.getRow(i);
				if(row==null)
					continue;
				for(int j=1; j<columnList.size(); j++){
					int index = columnList.get(j);
					String name = getStringCellValue(row.getCell(index)).trim();
					if(!StringUtils.isBlank(name)&&!existNode(name,i,index,sheet,columnList)){
						TreeNode node = new TreeNode();
						node.setName(name);
						node.setId(count+"");
						node.setpId(getLeftNodeId(i,index,sheet,columnList,pid));
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
	
	private String getLeftNodeId(int i,int j, XSSFSheet sheet,ArrayList<Integer> list,String pid){
		if(i==3&&list.indexOf(j)==1)
			return pid;
		XSSFRow row = sheet.getRow(i);
		int index = list.get(list.indexOf(j)-1);
		String name = getStringCellValue(row.getCell(index)).trim();
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
        if (StringUtils.isBlank(strCell)) {
            return "";
        }
        return strCell;
    }
    
    private int getColumn(XSSFSheet sheet){
    	int column = 0;
    	int rowNum = sheet.getLastRowNum();
    	for (int i = 0; i <= rowNum; i++) {
    		if(sheet.getRow(i)!=null){
    			int colNum = sheet.getRow(i).getLastCellNum();
    			if(column<colNum){
    				column = colNum;
    				System.out.println("最长列宽为第"+i+"行");
    			}
    		}
    	}
    	return column;
    }
    
    private boolean existNode(String name,int i, int j, XSSFSheet sheet,ArrayList<Integer> list){
    	if(!hasNodeName(name))
    		return false;
    	XSSFRow row = sheet.getRow(i);
		int index=list.indexOf(j)-1;
		while(index>0){
			String hasName = getStringCellValue(row.getCell(list.get(index))).trim();
			TreeNode hasNode = getNodeByName(hasName);
			TreeNode testNode = getNodeByName(name);
			if(!hasNode.getId().equals(testNode.getpId()))
				return false;
			index--;
			name = hasName;
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
    
    private ArrayList<Integer> getHeadIndex(XSSFSheet sheet){
    	ArrayList<String> heads = new ArrayList<String>();
    	XSSFRow row = sheet.getRow(0);
    	int colNum = getColumn(sheet);
    	for(int j=0; j<colNum; j++){
    		String name = getStringCellValue(row.getCell(j)).trim();
//    		if(!name.equals("版本"))
    			heads.add(name);
    	}
    	ArrayList<Integer> list = new ArrayList<Integer>();
    	List<String> tempList = null;
    	if(heads.indexOf("学段")!=-1)
    		tempList = peroidList;
    	else
    		tempList = indexList;
    	for(int i=0; i<heads.size(); i++){
    		list.add(heads.indexOf(tempList.get(i)));
    	}
    	return list;
    }
	
	public static void main(String[] args) {
		File file = new File("d:\\test\\人教版高中数学.xlsx");
		BookExcelUtil excelUtil = new BookExcelUtil();
		List<TreeNode> list = excelUtil.importNode(file,null);
	}
}
