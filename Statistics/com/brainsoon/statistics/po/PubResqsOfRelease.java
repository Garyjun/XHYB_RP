package com.brainsoon.statistics.po;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.brainsoon.common.support.OperDbUtils;
import com.brainsoon.system.support.SystemConstants.ResourceMoudle;
import com.brainsoon.system.support.SystemConstants.ResourceType;

@Entity
@Table(name = "resqs_of_pubrelease")
public class PubResqsOfRelease  implements Serializable {
	private static final long serialVersionUID = 6022426027760900277L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false)
	private Long id;
	
	@Column(name = "channelName")
	private String channelName;
	
	@Column(name = "pubResType")
	private String pubResType;
	
	@Column(name = "filingDate")
	private Date filingDate;
	
	@Column(name = "countNum")
	private int countNum;
	
	@Column(name = "platformId")
	private int platformId;
	
	public int getPlatformId() {
		return platformId;
	}

	public void setPlatformId(int platformId) {
		this.platformId = platformId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public String getPubResType() {
		return OperDbUtils.queryNameByIndexAndKey("publishType",pubResType);
	}

	public void setPubResType(String pubResType) {
		this.pubResType = pubResType;
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

}
