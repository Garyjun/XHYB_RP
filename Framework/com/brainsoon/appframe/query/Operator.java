package com.brainsoon.appframe.query;

import java.util.HashMap;
import java.util.Map;

public enum Operator {

	LIKE("like"), IN("in"), EQUAL("="),NOTEQUAL("!="), LT("<"), GT(">"),LTE("<="), GTE(">="), BETWEEN("between");

	// 成员变量
	private String value;

	// 构造方法
	private Operator(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public static Map<String, Operator> getAllMaps() {
		Map<String, Operator> all = new HashMap<String, Operator>();
		Operator[] opts = Operator.values();
		for (Operator operator : opts) {
			all.put(operator.getValue(), operator);
		}
		return all;
	}
}
