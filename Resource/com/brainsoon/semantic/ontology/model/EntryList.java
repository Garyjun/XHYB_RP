package com.brainsoon.semantic.ontology.model;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class EntryList extends BaseObjectList {
	private List<Entry> rows = new ArrayList<Entry>();
	/**
	 * 最大行数
	 */
	private long maxRow;
	/**
	 * 开始行号
	 */
	protected Long total;
	
	private long startRow;
	public void addEntry(Entry entry) {
		if(rows == null) {
			rows = new ArrayList<Entry>();
		}
		rows.add(entry);
	}

	public List<Entry> getRows() {
		return rows;
	}

	public void setRows(List<Entry> rows) {
		this.rows = rows;
	}

	public long getMaxRow() {
		return maxRow;
	}

	public void setMaxRow(long maxRow) {
		this.maxRow = maxRow;
	}

	public long getStartRow() {
		return startRow;
	}

	public void setStartRow(long startRow) {
		this.startRow = startRow;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

}
