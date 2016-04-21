package com.brainsoon.semantic.ontology.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 为业务平台提供元数据
 * @author zhangpeng
 *
 */
public class MetaDataDC extends ResBaseObject {	
	private String coverPath;  //封面路径
	private ExtendMetaData extendMetaData;
	private List<FilePath> fileAndPaths;
	private CopyRightMetaData copyRightMetaData ; //版权元数据
	public ExtendMetaData getExtendMetaData() {
		return extendMetaData;
	}

	public void setExtendMetaData(ExtendMetaData extendMetaData) {
		this.extendMetaData = extendMetaData;
	}

	public String getCoverPath() {
		return coverPath;
	}

	public void setCoverPath(String coverPath) {
		this.coverPath = coverPath;
	}

	public CopyRightMetaData getCopyRightMetaData() {
		return copyRightMetaData;
	}

	public void setCopyRightMetaData(CopyRightMetaData copyRightMetaData) {
		this.copyRightMetaData = copyRightMetaData;
	}

	public List<FilePath> getFileAndPaths() {
		return fileAndPaths;
	}

	public void setFileAndPaths(List<FilePath> fileAndPaths) {
		this.fileAndPaths = fileAndPaths;
	}

}
