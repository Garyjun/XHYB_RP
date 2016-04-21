package com.brainsoon.resrelease.support;

import java.util.List;

import com.brainsoon.appframe.support.UserInfo;

/**
 * @ClassName: PublishResOrder
 * @Description: 发布任务
 * @author xiehewei
 * @date 2014年12月16日 下午2:58:53
 *
 */
public class PublishResOrder {

	private Long orderId;
	private UserInfo userInfo;
	private String remark;
	private Long userId;
	private String username;
	private int platformId;
	private String loginIp;
	private String metaInfo;
	private List<String> metadataList;

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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

	public String getLoginIp() {
		return loginIp;
	}

	public void setLoginIp(String loginIp) {
		this.loginIp = loginIp;
	}

	public String getMetaInfo() {
		return metaInfo;
	}

	public void setMetaInfo(String metaInfo) {
		this.metaInfo = metaInfo;
	}

	public List<String> getMetadataList() {
		return metadataList;
	}

	public void setMetadataList(List<String> metadataList) {
		this.metadataList = metadataList;
	}
	
}
