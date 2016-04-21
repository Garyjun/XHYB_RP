package com.brainsoon.common.util.dofile.metadata.fo;

import java.io.Serializable;

/**
 * 
 * @ClassName: FileObject 
 * @Description:  文件抽象类
 * @author tanghui 
 * @date 2014-4-17 上午11:41:23 
 *
 */
@SuppressWarnings("serial")
public abstract class FileObject implements Serializable {

	private String fileName;  //文件名称
    private boolean exists;   //文件是否存在
    private String path; //文件相对路径
    private String absolutePath; //文件绝对路径
    private boolean canRead;  //文件是否可以读取
    private boolean canWrite;  //文件是否可以写入
    private String parentPath;  //文件上级路径
    private long length;  //文件大小
    private String lastModified;  //文件最后修改时间
    private boolean isFile;     //是否是文件类型
    private boolean isDirectory;  //是否是文件夹类型
    private String format;    //文件扩展名
    
    
	public FileObject() {
		super();
		// tanghui Auto-generated constructor stub
	}


	public String getFileName() {
		return fileName;
	}


	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	public boolean isExists() {
		return exists;
	}


	public void setExists(boolean exists) {
		this.exists = exists;
	}


	public String getPath() {
		return path;
	}


	public void setPath(String path) {
		this.path = path;
	}


	public String getAbsolutePath() {
		return absolutePath;
	}


	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}


	public boolean isCanRead() {
		return canRead;
	}


	public void setCanRead(boolean canRead) {
		this.canRead = canRead;
	}


	public boolean isCanWrite() {
		return canWrite;
	}


	public void setCanWrite(boolean canWrite) {
		this.canWrite = canWrite;
	}


	public String getParentPath() {
		return parentPath;
	}


	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
	}


	public long getLength() {
		return length;
	}


	public void setLength(long length) {
		this.length = length;
	}


	public String getLastModified() {
		return lastModified;
	}


	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}


	public boolean isFile() {
		return isFile;
	}


	public void setFile(boolean isFile) {
		this.isFile = isFile;
	}


	public boolean isDirectory() {
		return isDirectory;
	}


	public void setDirectory(boolean isDirectory) {
		this.isDirectory = isDirectory;
	}


	public String getFormat() {
		return format;
	}


	public void setFormat(String format) {
		this.format = format;
	}

    
}
