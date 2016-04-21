package com.brainsoon.statistics.po;

import java.io.Serializable;
import java.util.List;

/**
 * 资源采集 统计
 * 
 * @author Administrator
 * 
 */
public class ResqsOfCollectResult implements Serializable {
	private static final long serialVersionUID = 6022426027760900277L;
	
	private long total;
	private List<ResqsOfCollect> rows;
	
	/**
	 * 统计数
	 */
	private long statisticsNum;
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	public List<ResqsOfCollect> getRows() {
		return rows;
	}
	public void setRows(List<ResqsOfCollect> rows) {
		this.rows = rows;
	}
	public synchronized long getStatisticsNum() {
		return statisticsNum;
	}
	public synchronized void setStatisticsNum(long statisticsNum) {
		this.statisticsNum = statisticsNum;
	}
	
	
}
