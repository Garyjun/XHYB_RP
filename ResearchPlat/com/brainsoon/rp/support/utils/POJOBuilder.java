package com.brainsoon.rp.support.utils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

/**
 * <dl>
 * <dt>POJOBuilder.java</dt>
 * <dd>Function: {!!一句话描述功能}</dd>
 * <dd>Description:{详细描述该类的功能}</dd>
 * <dd>Copyright: Copyright (C) 2010</dd>
 * <dd>Company: 北京博云易迅技术有限公司</dd>
 * <dd>CreateUser: xujie</dd>
 * <dd>CreateDate: 2016年3月6日</dd>
 * </dl>
 */
public class POJOBuilder {
	
	public static Object fromJSONStr(String jsonStr) {
		String json = "";
		if (!JSONUtils.mayBeJSON(json)) {
			System.err.println("不是合法的JSON");
		}
		if (jsonStr.startsWith("[")) {
			parseArray(JSONArray.fromObject(json));
		} else {
			parseObject(JSONObject.fromObject(json));
		}
		return null;
	}

	/**
	 * 处理JSON 对象
	 * @param fromObject
	 * @param string
	 */
	private static void parseObject(JSONObject fromObject) {
		
	}

	/**
	 * 处理JSON 数组
	 * @param fromObject
	 * @param string
	 */
	private static void parseArray(JSONArray fromObject) {
		
	}
}
