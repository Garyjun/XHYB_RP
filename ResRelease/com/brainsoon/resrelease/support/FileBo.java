package com.brainsoon.resrelease.support;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 文件业务对象
 * @author 唐辉
 *
 */
public class FileBo implements Serializable {

	private static final long serialVersionUID = 7109757417281192114L;
	protected static final Log logger = LogFactory.getLog(FileBo.class);

	private String fileName;
	private String fileRealPath; //文件相对路径
	private String fileSize;
	private String fileId;
	private String fileAbsolutePath;//文件绝对路径
	private String resId; //资源id
	private String resName; //资源名称
	private String id; //id
	private String md5; //md5
	private String targetPath; //处理后的文件绝对路径
	
	
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileRealPath() {
		return fileRealPath;
	}
	public void setFileRealPath(String fileRealPath) {
		this.fileRealPath = fileRealPath;
	}
	public String getFileSize() {
		return fileSize;
	}
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public String getFileAbsolutePath() {
		return fileAbsolutePath;
	}
	public void setFileAbsolutePath(String fileAbsolutePath) {
		this.fileAbsolutePath = fileAbsolutePath;
	}
	public String getResId() {
		return resId;
	}
	public void setResId(String resId) {
		this.resId = resId;
	}
	public String getResName() {
		return resName;
	}
	public void setResName(String resName) {
		this.resName = resName;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public FileBo() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getMd5() {
		return md5;
	}
	public void setMd5(String md5) {
		this.md5 = md5;
	}
	public String getTargetPath() {
		return targetPath;
	}
	public void setTargetPath(String targetPath) {
		this.targetPath = targetPath;
	}
	

	
	
	
	
}
