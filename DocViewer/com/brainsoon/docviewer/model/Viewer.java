package com.brainsoon.docviewer.model;

/**
 * 
 * @ClassName: Viewer 
 * @Description:  在线预览文件参数类
 * @author tanghui 
 * @date 2014-5-12 下午5:20:13 
 *
 */
public class Viewer {

	private String id;  //文件id
	private String filePath;  //文件路径
	private String fileType;  //文件类型
	private String width;  //定义窗口的宽度
	private String height;  //定义窗口的高度
	private String tarPath;  //目标文件路径
	private String srcFileName;  //不带扩展名的文件名
	private String tempNcxPath;  //NCX文件临时路径
	private String tempNodeXmlPath;  //带node节点的xml文件临时路径
	private String thisIndex;  //开始页
	private String maxPages;  //pdf最大页数
	
	
	
	public Viewer() {
		super();
	}
	
	
	public Viewer(String filePath) {
		super();
		this.filePath = filePath;
	}
	
	
	public Viewer(String id, String fileType) {
		super();
		this.id = id;
		this.fileType = fileType;
	}


	public Viewer(String filePath, String tarPath,
			String srcFileName, String tempNcxPath, String tempNodeXmlPath,
			String thisIndex, String maxPages) {
		super();
		this.filePath = filePath;
		this.tarPath = tarPath;
		this.srcFileName = srcFileName;
		this.tempNcxPath = tempNcxPath;
		this.tempNodeXmlPath = tempNodeXmlPath;
		this.thisIndex = thisIndex;
		this.maxPages = maxPages;
	}



	public Viewer(String id, String filePath, String fileType, String width,
			String height, String tarPath, String srcFileName,
			String tempNcxPath, String tempNodeXmlPath, String thisIndex,
			String maxPages) {
		super();
		this.id = id;
		this.filePath = filePath;
		this.fileType = fileType;
		this.width = width;
		this.height = height;
		this.tarPath = tarPath;
		this.srcFileName = srcFileName;
		this.tempNcxPath = tempNcxPath;
		this.tempNodeXmlPath = tempNodeXmlPath;
		this.thisIndex = thisIndex;
		this.maxPages = maxPages;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getWidth() {
		return width;
	}
	public void setWidth(String width) {
		this.width = width;
	}
	public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	public String getTarPath() {
		return tarPath;
	}
	public void setTarPath(String tarPath) {
		this.tarPath = tarPath;
	}
	public String getSrcFileName() {
		return srcFileName;
	}
	public void setSrcFileName(String srcFileName) {
		this.srcFileName = srcFileName;
	}
	public String getTempNcxPath() {
		return tempNcxPath;
	}
	public void setTempNcxPath(String tempNcxPath) {
		this.tempNcxPath = tempNcxPath;
	}
	public String getTempNodeXmlPath() {
		return tempNodeXmlPath;
	}
	public void setTempNodeXmlPath(String tempNodeXmlPath) {
		this.tempNodeXmlPath = tempNodeXmlPath;
	}
	public String getThisIndex() {
		return thisIndex;
	}
	public void setThisIndex(String thisIndex) {
		this.thisIndex = thisIndex;
	}
	public String getMaxPages() {
		return maxPages;
	}
	public void setMaxPages(String maxPages) {
		this.maxPages = maxPages;
	}
	
	
	
}
