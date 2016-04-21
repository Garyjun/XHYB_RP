package com.brainsoon.statistics.po;

import java.io.Serializable;

import com.brainsoon.common.support.OperDbUtils;
import com.brainsoon.system.support.SystemConstants.ResourceStatus;

/**
 * 资源采集统计
 * @author liming
 *
 */
public class ResqsOfPubCollect implements Serializable {
	private static final long serialVersionUID = 6022426027760900277L;

	private Long objectId;

	private String sum;

	private String type;
	
	private String count;

	private String source;

	private String status;
	
	public Long getObjectId() {
		return objectId;
	}

	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}

	public String getSum() {
		return sum;
	}

	public void setSum(String sum) {
		this.sum = sum;
	}


	public String getType() {
		return OperDbUtils.queryNameByIndexAndKey("publishType",type);
	}

	public void setType(String type) {
		this.type = type;
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

}
