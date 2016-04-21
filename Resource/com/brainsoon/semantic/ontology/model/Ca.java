package com.brainsoon.semantic.ontology.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ca extends ResBaseObject {

	private ExtendMetaData extendMetaData;  
	
	private Integer excelNum;// 对应excel的行号，批量导入使用
	
	private String uploadFile;//要上传文件的全路径，2014年7月26日  zuohl add，为批量导入
	
	private String status;
	
	private String publishType;
	
	private List<File> realFiles;
	
	private String createTime;
	
	private String updateTime;
	
	private List<Sco> scos;
	
	public List<Sco> getScos() {
		return scos;
	}

	public void setScos(List<Sco> scos) {
		this.scos = scos;
	}

	private String creator;
	
	private String updater;
	
	private CopyRightMetaData copyRightMetaData ;	
	
	private String convertPath; //图书或出版资源路径，为放到转换表里使用
	
	private String resVersion;
	
	private String rootPath;
	
	private String libType;
	
	private String hasOres;
	
	private String hasBres;
	
	private String hasProd;
	
	private String hasMater;
	
	private String hasCopyright;
	
	private String createName;
	
	private String createTimeFormat;
	
	private String isCompress;//是否压缩
	
	private int num;	//用于doi记数必填字段是否重复
	
	private Map<String, Map<String,String>> fileMetadataFlag = new HashMap<String, Map<String,String>>();
	
	public String getCreateTimeFormat() {
		return createTimeFormat;
	}

	public void setCreateTimeFormat(String createTimeFormat) {
		this.createTimeFormat = createTimeFormat;
	}

	public String getCreateName() {
		return createName;
	}

	public void setCreateName(String createName) {
		this.createName = createName;
	}

	public String getHasCopyright() {
		return hasCopyright;
	}

	public void setHasCopyright(String hasCopyright) {
		this.hasCopyright = hasCopyright;
	}

	public String getLibType() {
		return libType;
	}

	public void setLibType(String libType) {
		this.libType = libType;
	}

	public String getHasOres() {
		return hasOres;
	}

	public void setHasOres(String hasOres) {
		this.hasOres = hasOres;
	}

	public String getHasBres() {
		return hasBres;
	}

	public void setHasBres(String hasBres) {
		this.hasBres = hasBres;
	}

	public String getHasProd() {
		return hasProd;
	}

	public void setHasProd(String hasProd) {
		this.hasProd = hasProd;
	}

	public String getHasMater() {
		return hasMater;
	}

	public void setHasMater(String hasMater) {
		this.hasMater = hasMater;
	}

	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPublishType() {
		return publishType;
	}

	public void setPublishType(String publishType) {
		this.publishType = publishType;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getUpdater() {
		return updater;
	}

	public void setUpdater(String updater) {
		this.updater = updater;
	}

	public String getConvertPath() {
		return convertPath;
	}

	public void setConvertPath(String convertPath) {
		this.convertPath = convertPath;
	}

	public ExtendMetaData getExtendMetaData() {
		return extendMetaData;
	}

	public void setExtendMetaData(ExtendMetaData extendMetaData) {
		this.extendMetaData = extendMetaData;
	}

	public CopyRightMetaData getCopyRightMetaData() {
		return copyRightMetaData;
	}

	public void setCopyRightMetaData(CopyRightMetaData copyRightMetaData) {
		this.copyRightMetaData = copyRightMetaData;
	}

	public Integer getExcelNum() {
		return excelNum;
	}

	public void setExcelNum(Integer excelNum) {
		this.excelNum = excelNum;
	}

	public String getUploadFile() {
		return uploadFile;
	}

	public void setUploadFile(String uploadFile) {
		this.uploadFile = uploadFile;
	}

	public List<File> getRealFiles() {
		return realFiles;
	}

	public void setRealFiles(List<File> realFiles) {
		this.realFiles = realFiles;
	}

	public String getResVersion() {
		return resVersion;
	}

	public void setResVersion(String resVersion) {
		this.resVersion = resVersion;
	}

	public Map<String, Map<String, String>> getFileMetadataFlag() {
		return fileMetadataFlag;
	}

	public void setFileMetadataFlag(
			Map<String, Map<String, String>> fileMetadataFlag) {
		this.fileMetadataFlag = fileMetadataFlag;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getIsCompress() {
		return isCompress;
	}

	public void setIsCompress(String isCompress) {
		this.isCompress = isCompress;
	}
	
}
