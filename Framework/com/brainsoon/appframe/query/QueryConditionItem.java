package com.brainsoon.appframe.query;

import net.sf.json.JSONArray;

import com.google.gson.Gson;

public class QueryConditionItem {
	
	private String fieldName;

	private Operator operator;

	private Object value;
	
	private String isMetadata =  "0";//是否是自定义元数据  "1"代表是自定义元数据
	
	private String resType;
	
	public String getResType() {
		return resType;
	}

	public void setResType(String resType) {
		this.resType = resType;
	}

	public String getIsMetadata() {
		return isMetadata;
	}

	public void setIsMetadata(String isMetadata) {
		this.isMetadata = isMetadata;
	}

	public QueryConditionItem() {
		super();
	}

	public QueryConditionItem(String fieldName, Operator operator, Object value) {
		super();
		setFieldName(fieldName);
		setOperator(operator);
		setValue(value);
	}
	
	public QueryConditionItem(String resType, String fieldName, Operator operator, Object value) {
		super();
		setResType(resType);
		setFieldName(fieldName);
		setOperator(operator);
		setValue(value);
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String toString() {
		return getFieldName() + getOperator().getValue() + getValue();
	}

	public boolean isEmptyValue() {
		return getValue() == null || getValue().toString().trim().length() == 0;
	}
	public static void main(String[] args) {
//		Gson gson = new Gson();
//		String json = "[{\"resType\":\"1\",\"fieldName\":\"doi\",\"operator\":\"LIKE\", \"value\":\"value\"},{\"resType\":\"12\",\"fieldName\":\"doi2\",\"operator\":\"IN\", \"value\":\"value2\"}]";
//		System.out.println(json);
//		JSONArray array = JSONArray.fromObject(json);
//		for(int i=0;i<array.size();i++){
//			String jsonObj = array.get(i).toString();
//			QueryConditionItem item = gson.fromJson(jsonObj, QueryConditionItem.class);
//			System.out.println(item.getFieldName()+ "   " + item.getOperator().toString());
//		}
//		
		
	}
}