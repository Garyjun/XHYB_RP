package com.brainsoon.appframe.support;

import java.io.Serializable;
import java.util.List;


/**
 * 
 * 分页查询结果
 * 
 * @author zuo
 *
 */
@SuppressWarnings("rawtypes")
public class PageResult implements Serializable{
	private static final long serialVersionUID = -4174391815005861734L;
	/**
	 * 结果集
	 */
	private List rows;
	/**
	 * 总结果数
	 */
	private long total;
	
	public List getRows() {
		return rows;
	}
	public void setRows(List rows) {
		this.rows = rows;
	}
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
}

