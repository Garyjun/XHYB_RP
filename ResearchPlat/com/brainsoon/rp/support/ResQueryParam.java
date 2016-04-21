package com.brainsoon.rp.support;

import net.sf.json.JSONArray;


/**
 * 
* @ClassName: ResQueryParam
* @Description: datatable返回参数封装
* @author huagnjun
* @date 2016年3月23日
*
 */
public class ResQueryParam {
	
	private String resType;//资源类型
	private JSONArray columns;
	private JSONArray conditions;
	
	public ResQueryParam(String resType, JSONArray columns, JSONArray conditions) {
		super();
		this.resType = resType;
		this.columns = columns;
		this.conditions = conditions;
	}
	
	public ResQueryParam() {
		super();
	}
	public String getResType() {
		return resType;
	}
	public void setResType(String resType) {
		this.resType = resType;
	}
	public JSONArray getColumns() {
		return columns;
	}
	public void setColumns(JSONArray columns) {
		this.columns = columns;
	}
	public JSONArray getConditions() {
		return conditions;
	}
	public void setConditions(JSONArray conditions) {
		this.conditions = conditions;
	}
	
}
