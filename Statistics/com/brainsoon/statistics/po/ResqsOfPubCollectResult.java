package com.brainsoon.statistics.po;

import java.io.Serializable;
import java.util.List;

/**
 * 出版资源采集统计
 * @author liming
 *
 */
public class ResqsOfPubCollectResult implements Serializable {
private static final long serialVersionUID = 6022426027760900277L;
	
	private long total;
	private List<ResqsOfPubCollect> rows;
	
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

	public List<ResqsOfPubCollect> getRows() {
		return rows;
	}

	public void setRows(List<ResqsOfPubCollect> rows) {
		this.rows = rows;
	}

	public synchronized long getStatisticsNum() {
		return statisticsNum;
	}

	public synchronized void setStatisticsNum(long statisticsNum) {
		this.statisticsNum = statisticsNum;
	}
	
	

}
