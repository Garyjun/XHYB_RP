package com.brainsoon.statistics.po;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.brainsoon.system.support.SystemConstants.*;

/**
 * 资源采集 统计
 * 
 * @author Administrator
 * 
 */
public class ResqsOfCollect implements Serializable {

	private static final long serialVersionUID = 6022426027760900277L;

	private Long objectId;

	private String sum;

	private String type;

	private String libType;

	private String module;

	private String count;

	private String source;

	private String status;

	public String getSum() {
		return sum;
	}

	public void setSum(String sum) {
		this.sum = sum;
	}

	public String getType() {
		if(StringUtils.equals(type, "T10")||StringUtils.equals(type, "T11")){
			return ResourceCaType.getValueByKey(type);
		}else{
			return ResourceType.getValueByKey(type);
		}
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLibType() {
		return LibType.getValueByKey(libType);
	}

	public void setLibType(String libType) {
		this.libType = libType;
	}

	public String getModule() {
		return ResourceMoudle.getValueByKey(module);
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getStatus() {
		return ResourceStatus.getValueByKey(status);
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getObjectId() {
		return objectId;
	}

	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}
}
