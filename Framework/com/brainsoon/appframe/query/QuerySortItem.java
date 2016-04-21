package com.brainsoon.appframe.query;

public class QuerySortItem {

	private String fieldName;
	
	private String sortMode;
	
	public QuerySortItem(){}
	public QuerySortItem(String fieldName, String sortMode) {
		super();
		setFieldName(fieldName);
		setSortMode(sortMode);
	}


	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getSortMode() {
		return sortMode;
	}

	public void setSortMode(String sortMode) {
		this.sortMode = sortMode;
	}
	public static void main(String[] args) {
		System.out.println(Operator.getAllMaps());
	}
}
