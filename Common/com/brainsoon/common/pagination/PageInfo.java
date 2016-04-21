package com.brainsoon.common.pagination;

import java.util.List;

import com.brainsoon.appframe.support.PageResult;

public class PageInfo {
	
    private int page=1;	//当前页
    
	//分页使用的参数，表示每页最大行数,缺省10行
	private int rows = 10;
	
	private String order;
	
	private String sort="desc";
	
	private List items;// 当前页包含的记录列表
	
	private long total;
	
	//增加该属性是由于easyUi中rows充当页面显示记录数和结果集合两个角色
	private PageResult pageResult;

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public List getItems() {
		return items;
	}

	public void setItems(List items) {
		this.items = items;
	}

	public PageResult getPageResult() {
		pageResult=new PageResult();
		pageResult.setRows(getItems());
		pageResult.setTotal(getTotal());
		return pageResult;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

}
