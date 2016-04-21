package com.brainsoon.semantic.ontology.model;

import java.util.ArrayList;
import java.util.List;

public class StatisticsList {
	/**
	 * 总共行数 
	 */	
	private long total;
	
	private long statisticsNum;
	
	/**
	 * (匹配的)文档集合
	 */	
	private List<Statistics> rows = new ArrayList<Statistics>();
	
	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}
	
	public void addRow(Statistics statistics) {
		rows.add(statistics);
	}

	public void setRows(List<Statistics> rows) {
		this.rows = rows;
	}
	
	public List<Statistics> getRows() {
		return rows;
	}

	public long getStatisticsNum() {
		return statisticsNum;
	}

	public void setStatisticsNum(long statisticsNum) {
		this.statisticsNum = statisticsNum;
	}
	
}
