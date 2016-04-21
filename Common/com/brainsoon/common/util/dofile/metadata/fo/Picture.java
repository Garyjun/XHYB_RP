package com.brainsoon.common.util.dofile.metadata.fo;

/**
 * 
 * @ClassName: Picture 
 * @Description:  图片
 * @author tanghui 
 * @date 2014-4-17 上午11:22:16 
 *
 */
public class Picture extends FileObject{
	
	//图片分辨率---指明图形/图像的长度、宽度，以像素为单位计，格式如：800*600
	private String resolution;

	public Picture() {
		super();
		// tanghui Auto-generated constructor stub
	}
	
	

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}



	@Override
	public String toString() {
		return "Picture [resolution=" + resolution + ", getFileName()="
				+ getFileName() + ", isExists()=" + isExists()
				+ ", getPath()=" + getPath()
				+ ", getAbsolutePath()=" + getAbsolutePath() + ", isCanRead()="
				+ isCanRead() + ", isCanWrite()=" + isCanWrite()
				+ ", getParentPath()=" + getParentPath() + ", getLength()="
				+ getLength() + ", getLastModified()=" + getLastModified()
				+ ", isFile()=" + isFile() + ", isDirectory()=" + isDirectory()
				+ ", getFormat()=" + getFormat() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
	
	
	
	
	
	
	
}
