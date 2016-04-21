package com.brainsoon.resrelease.processThread;

import com.brainsoon.resrelease.po.ResOrder;

/**
 * <dl>
 * <dt>FileTaskInfo</dt>
 * <dd>Description:加工文件任务信息</dd>
 * <dd>CreateDate: 2015年1月15日 下午5:29:32</dd>
 * </dl>
 * 
 * @author xiehewei
 */

public class FileTaskInfo {
	
	private ResOrder resOrder;
	private String fileId;
	private String resId;

	public ResOrder getResOrder() {
		return resOrder;
	}

	public void setResOrder(ResOrder resOrder) {
		this.resOrder = resOrder;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getResId() {
		return resId;
	}

	public void setResId(String resId) {
		this.resId = resId;
	}

}
