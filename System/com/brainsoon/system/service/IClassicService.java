package com.brainsoon.system.service;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.brainsoon.common.service.IBaseService;

public interface IClassicService extends IBaseService {
	public String getMoudleTree(String code);
	public String getPrivilegeTree(String code);
	
	//教材改版信息
	public String getChangeVersionInfo(String objectId);
	//教材改版
	public JSONObject changeVersion(String objectId, JSONArray domains);
}
