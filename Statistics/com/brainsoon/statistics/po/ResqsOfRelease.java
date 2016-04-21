package com.brainsoon.statistics.po;

import java.io.Serializable;
import java.util.Date;

import com.brainsoon.common.support.GlobalDataCacheMap;

/**
 * 资源发布 统计
 * 
 * @author Administrator
 * 
 */
public class ResqsOfRelease implements Serializable {
	private static final long serialVersionUID = 6022426027760900277L;
	
	private Long id;
	private Long releaseId;
	private String channelName;
	private String fileType;
	private String posttype;
	private String posttypeDesc;
	private Date filingDate;
	private int countNum;


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getReleaseId() {
		return releaseId;
	}

	public void setReleaseId(Long releaseId) {
		this.releaseId = releaseId;
	}
	

	public String getPosttypeDesc() {
		return posttypeDesc;
	}

	public void setPosttypeDesc(String posttypeDesc) {
		this.posttypeDesc = posttypeDesc;
	}

	public String getPosttype() {
		return posttype;
	}

	public void setPosttype(String posttype) {
		this.posttype = posttype;
	}

	public Date getFilingDate() {
		return filingDate;
	}

	public void setFilingDate(Date filingDate) {
		this.filingDate = filingDate;
	}

	public int getCountNum() {
		return countNum;
	}

	public void setCountNum(int countNum) {
		this.countNum = countNum;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

}
