package com.brainsoon.semantic.ontology.model;

import java.util.HashMap;
import java.util.Map;
/**
 * 统计个数
 * @author mengwy
 *
 */
public class ResourceCount {
	private String name;
	private String typeId;
	private String typeName;
	private long num;
	private static Map types = null;
	static{
		types = new HashMap();
		types.put("T01", "教学素材");
		types.put("T02", "教学设计");
		types.put("T03", "教学课件");
		types.put("T04", "试卷习题");
		types.put("T05", "微视频");
		types.put("T06", "数字图书");
		types.put("T07", "数字教材");
		types.put("T08", "教学工具");
		types.put("T09", "教育游戏");
		types.put("T10", "网络课程");
		types.put("T11", "教学案例");
		types.put("T12", "教育网站");
		types.put("T00", "教育资源");
	}
	public ResourceCount() {}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public long getNum() {
		return num;
	}

	public void setNum(long num) {
		this.num = num;
	}
	
//	public Map<String, Long> getFields() {
//		return fields;
//	}
//
//	public void setFields(Map<String, Long> fields) {
//		this.fields = fields;
//	}
}
