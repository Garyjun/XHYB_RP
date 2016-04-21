package com.brainsoon.resrelease.processThread;

import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.resource.po.SubjectStore;
import com.brainsoon.resrelease.po.ResOrder;

/**
 * <dl>
 * <dt>TaskInfo</dt>
 * <dd>Description:加工任务信息</dd>
 * <dd>CreateDate: 2015年1月15日 下午5:29:32</dd>
 * </dl>
 * 
 * @author xiehewei
 */

public class TaskInfo {
	private ResOrder resOrder;
	private UserInfo userInfo;
	private String objectId;
	private String posttype;
	public ResOrder getResOrder() {
		return resOrder;
	}

	public void setResOrder(ResOrder resOrder) {
		this.resOrder = resOrder;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getPosttype() {
		return posttype;
	}

	public void setPosttype(String posttype) {
		this.posttype = posttype;
	}
	
	

}
