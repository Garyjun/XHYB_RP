package com.brainsoon.resource.support;

import java.util.List;
/**
 * excel清单文件
 * @author zuo
 *
 */
public class ImportData {
	
	private String jsonDate;
	
	private String repeatType;
	
	private String syncJsonFileName;
	
	private String sjName;
	
	private int allNum;
	
	private String publishType;
	
	public String getSyncJsonFileName() {
		return syncJsonFileName;
	}

	public void setSyncJsonFileName(String syncJsonFileName) {
		this.syncJsonFileName = syncJsonFileName;
	}

	public String getJsonDate() {
		return jsonDate;
	}

	public void setJsonDate(String jsonDate) {
		this.jsonDate = jsonDate;
	}

	public String getRepeatType() {
		return repeatType;
	}

	public void setRepeatType(String repeatType) {
		this.repeatType = repeatType;
	}

	public String getSjName() {
		return sjName;
	}

	public void setSjName(String sjName) {
		this.sjName = sjName;
	}

	public int getAllNum() {
		return allNum;
	}

	public void setAllNum(int allNum) {
		this.allNum = allNum;
	}

	public String getPublishType() {
		return publishType;
	}

	public void setPublishType(String publishType) {
		this.publishType = publishType;
	}
	
	
	
	


}
