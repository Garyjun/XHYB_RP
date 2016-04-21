package com.brainsoon.semantic.ontology.model;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

public class File extends BaseObject {
	private String caId;
	
	private String isDir;
	
	private String path;  //文件相对目录
	
	private String md5;  //文件MD5
	
	private String fileType;  //文件类型 
	
	private String fileByte;  //文件大小
	
	private String create_time; //创建时间
	
	private String modified_time;  //修改时间
	
	private String creator;  //创建者
	
	private String modifieder; //修改者
 	
	private String aliasName;//文件别名
	
	private String version;//文件版本
	
	private String pid;
	
	private String identifier;
	
	private String coverPath;
	
	private String resName;
	
	private String resObjectId;
	
	private String title;
	
	public String getResName() {
		return resName;
	}

	public void setResName(String resName) {
		this.resName = resName;
	}

	public String getResObjectId() {
		return resObjectId;
	}

	public void setResObjectId(String resObjectId) {
		this.resObjectId = resObjectId;
	}
	
	private Map<String, String> fileMetadataMap = new HashMap<String, String>();
	
	public Map<String, String> getFileMetadataMap() {
		return fileMetadataMap;
	}

	public void setFileMetadataMap(Map<String, String> fileMetadataMap) {
		this.fileMetadataMap = fileMetadataMap;
	}

	public void putFileMetadataMap(String key, String value) {
		fileMetadataMap.put(key, value);	
	}
	
	public String getCoverPath() {
		return coverPath;
	}

	public void setCoverPath(String coverPath) {
		this.coverPath = coverPath;
	}

	@XmlTransient
	public String getFileMetadataMapValue(String key) {
		return fileMetadataMap.get(key);
	}
	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getCaId() {
		return caId;
	}

	public void setCaId(String caId) {
		this.caId = caId;
	}

	public String getIsDir() {
		return isDir;
	}

	public void setIsDir(String isDir) {
		this.isDir = isDir;
	}

	public String getFileByte() {
		return fileByte;	
	}
	
	public void setFileByte(String fileByte) {
		this.fileByte = fileByte;
	}
	
	
	public String getModified_time() {
		return modified_time;	
	}
	
	public void setModified_time(String modified_time) {
		this.modified_time = modified_time;
	}
		
	public String getPath() {
		return path;	
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public String getMd5() {
		return md5;	
	}
	
	public void setMd5(String md5) {
		this.md5 = md5;
	}
	
	
	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getModifieder() {
		return modifieder;
	}

	public void setModifieder(String modifieder) {
		this.modifieder = modifieder;
	}

	public String getAliasName() {
		return aliasName;
	}

	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}

	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getJsonRes(int status) {
		return "";		
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
