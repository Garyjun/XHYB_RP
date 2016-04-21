package com.brainsoon.appframe.query;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class QueryConditionList {
	protected transient final Log logger = LogFactory.getLog(getClass());
	
	private List<QueryConditionItem> conditionItems = new ArrayList<QueryConditionItem>();
	private List<QuerySortItem> sortList = new ArrayList<QuerySortItem>();

	//倒序
	public static final String SORT_MODE_DESC = "desc";

	//正序
	public static final String SORT_MODE_ASC = "asc";
	
	//分页使用的参数，表示当前页的开始行号
	private int startIndex = 0;
	
	//分页使用的参数，表示每页最大行数,缺省20行
	private int pageSize = 20;
		
	public void addCondition(QueryConditionItem condition) {
		conditionItems.add(condition);
	}
	
	public void addSort(QuerySortItem sort){
		sortList.add(sort);
	}

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public List<QueryConditionItem> getConditionItems() {
		return conditionItems;
	}

	public List<QuerySortItem> getSortList() {
		return sortList;
	}
}