package com.brainsoon.system.support;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.brainsoon.common.po.tree.TreeNode;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.common.util.dofile.zip.ZipUtil;
import com.brainsoon.resource.action.BresAction;

public class ExportExcelUtil {
	private List<File> subjectFileList = new ArrayList<File>();
	private final static SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	String fileTemp = WebAppUtils.getWebRootBaseDir(ConstantsDef.fileTemp);
	private List<TreeNode> versionTree;
	public ExportExcelUtil(List<TreeNode> versionTree){
		this.versionTree = versionTree;
	}
	
	public File exportVersionExcel(){
		List<TreeNode> subjectList = new ArrayList<TreeNode>();
		String filePath = fileTemp + dateformat.format(new Date());
		new File(filePath).mkdir();
		for(TreeNode node : versionTree){
			if(!node.getNodeType().equals("3"))
				continue;
			subjectList.add(node);
		}
		for(TreeNode node : subjectList){
			File subjectFile = writeSubjectExcel(node,filePath);
			subjectFileList.add(subjectFile);
		}
		String fileName = subjectFileList.get(0).getParentFile().getAbsolutePath();
		return zipSubjectFiles(fileName);
	}
	
	private File writeSubjectExcel(TreeNode subjectNode,String filePath){
		String fileName = createFileName(subjectNode,filePath);
		return writeExcel(fileName,subjectNode);
	}
	
	private File writeExcel(String fileName,TreeNode subjectNode){
		OutputStream out = null;
		File excel = new File(fileName);
		XSSFWorkbook workbook = new XSSFWorkbook(); 
		try {
			XSSFSheet sheet= workbook.createSheet(subjectNode.getName());
			createTableHead(sheet);
			createTableContent(sheet,subjectNode);
			out = new FileOutputStream(excel);
			workbook.write(out);
		} catch (Exception e) {
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
	
	private void createTableHead(XSSFSheet sheet){
		XSSFRow row = sheet.createRow(0);
		row.createCell(0).setCellValue("版本");
		row.createCell(1).setCellValue("科目");
		row.createCell(2).setCellValue("年级");
		row.createCell(3).setCellValue("分册");
		row.createCell(4).setCellValue("章");
		row.createCell(5).setCellValue("课");
		row.createCell(6).setCellValue("节");
		
		XSSFRow row2 = sheet.createRow(1);
		row2.createCell(0).setCellValue("version");
		row2.createCell(1).setCellValue("subject");
		row2.createCell(2).setCellValue("grade");
		row2.createCell(3).setCellValue("volume");
		row2.createCell(4).setCellValue("chapter");
		row2.createCell(5).setCellValue("lesson");
		row2.createCell(6).setCellValue("node");
	}
	
	private void createTableContent(XSSFSheet sheet,TreeNode subjectNode){
		int leafIndex = 0;
		for(int i=0; i<versionTree.size(); i++){
			TreeNode node = versionTree.get(i);
			if(isLeaf(node)&&isRightSubject(node,subjectNode)){
				leafIndex ++;
				XSSFRow row = sheet.createRow(leafIndex+2);
				while(node!=null&&isValid(node)){
					createCellContent(node,row,subjectNode);
					node = getParentNode(node);
				}
			}
		}
	}
	
	private void createCellContent(TreeNode node,XSSFRow row,TreeNode subjectNode){
//		String nodeType = node.getNodeType();
		int level = node.getLevel();
//		if(nodeType.equals("0")){
//			row.createCell(0).setCellValue(versionTree.get(0).getName());
//		}else if(nodeType.equals("1")){
//			row.createCell(1).setCellValue(subjectNode.getName());
//		}else {
//			if(level == 1)
//				row.createCell(level + 2).setCellValue(node.getName());
//			else
//				row.createCell(level).setCellValue(node.getName());
//		}
		if(level == 1)
			row.createCell(1).setCellValue(node.getName());
		else
			row.createCell(level-2).setCellValue(node.getName());
	}
	
	private TreeNode getParentNode(TreeNode node){
		for(TreeNode tn : versionTree){
			if(tn.getId().equals(node.getpId()))
				return tn;
		}
		return null;
	}
	
	private boolean isLeaf(TreeNode node){
		if(node.getNodeType().equals("6"))
			return true;
		else
			return false;
	}
	
	private String createFileName(TreeNode subjectNode,String filePath){
		String subject = subjectNode.getName();
		subjectNode = getParentNode(subjectNode);
		String peroid = subjectNode.getName();
		subjectNode = getParentNode(subjectNode);
		String version = subjectNode.getName();
		String fileName = version + "_" + peroid + "_" + subject + ".xlsx";
		return filePath + File.separator + fileName;
	}
	
	private File zipSubjectFiles(String parentPath){
		String zipName = fileTemp + dateformat.format(new Date()) + ".zip";
		try {
			ZipUtil.zipFileOrFolder(parentPath, zipName, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new File(zipName);
	}
	
	private boolean isValid(TreeNode node){
		while(getParentNode(node)!=null)
			node = getParentNode(node);
		if(node.getNodeType().equals("0"))
			return true;
		else
			return false;
	}
	
	private boolean isRightSubject(TreeNode node, TreeNode subjectNode){
		if(node.getNodeType().equals("0")||node.getNodeType().equals("1"))
			return true;
		while(node!=null){
			if(node.getId().equals(subjectNode.getId()))
				return true;
			node = getParentNode(node);
		}
		return false;
	}
	
	public List<TreeNode> getVersionTree() {
		return versionTree;
	}

	public void setVersionTree(List<TreeNode> versionTree) {
		this.versionTree = versionTree;
	}

}
