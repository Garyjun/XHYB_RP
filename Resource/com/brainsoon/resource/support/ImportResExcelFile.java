package com.brainsoon.resource.support;

import java.io.File;

import com.brainsoon.appframe.support.UserInfo;

public class ImportResExcelFile {
	private File excel;
	private String name;
	private String module;
	private String repeatType; // 1 新建 2 覆盖，3 忽略
	private String repeatRelevanceType; //重复自定关联，为 1 时关联
	private UserInfo userInfo;
	private String remark;
	private String libType; //1 标准资源 2 原始资源
	private Long userId;
	private String username;
	private int platformId;
	private String loginIp;
	private String fileDir; //存放失败资源的excel是需要的路径 
	private String processTask;//是否来自加工
	
	public String getFileDir() {
		return fileDir;
	}
	public void setFileDir(String fileDir) {
		this.fileDir = fileDir;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLoginIp() {
		return loginIp;
	}
	public void setLoginIp(String loginIp) {
		this.loginIp = loginIp;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public int getPlatformId() {
		return platformId;
	}
	public void setPlatformId(int platformId) {
		this.platformId = platformId;
	}
	public File getExcel() {
		return excel;
	}
	public void setExcel(File excel) {
		this.excel = excel;
	}
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
	}
	public String getRepeatType() {
		return repeatType;
	}
	public void setRepeatType(String repeatType) {
		this.repeatType = repeatType;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getRepeatRelevanceType() {
		return repeatRelevanceType;
	}
	public void setRepeatRelevanceType(String repeatRelevanceType) {
		this.repeatRelevanceType = repeatRelevanceType;
	}
	public String getLibType() {
		return libType;
	}
	public void setLibType(String libType) {
		this.libType = libType;
	}
	public UserInfo getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
	public String getProcessTask() {
		return processTask;
	}
	public void setProcessTask(String processTask) {
		this.processTask = processTask;
	}
	
}
