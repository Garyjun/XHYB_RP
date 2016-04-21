package com.brainsoon.appframe.support;

import java.io.Serializable;
import java.util.List;


/**
 * 
 * 分页查询结果(特殊情况，有总数)
 * 
 * @author zuo
 *
 */
@SuppressWarnings("rawtypes")
public class PageResultForTNum implements Serializable{
	private static final long serialVersionUID = -4174391815005861734L;
	/**
	 * 结果集
	 */
	private List rows;
	/**
	 * 总结果数
	 */
	private long total;
	
	/**
	 * 统计数
	 */
	private long statisticsNum;
	
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
	public long getStatisticsNum() {
		return statisticsNum;
	}
	public void setStatisticsNum(long statisticsNum) {
		this.statisticsNum = statisticsNum;
	}
}

