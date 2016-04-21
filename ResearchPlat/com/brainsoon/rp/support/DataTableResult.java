package com.brainsoon.rp.support;

import java.util.List;

public class DataTableResult {
	private int recordsTotal;
	private int recordsFiltered;
	private int draw;
	private List data;
	
	public DataTableResult(int recordsTotal, int recordsFiltered,
			int draw, List data) {
		super();
		this.recordsTotal = recordsTotal;
		this.recordsFiltered = recordsFiltered;
		this.draw = draw;
		this.data = data;
	}
	
	public DataTableResult() {
		super();
	}
	
	public int getRecordsTotal() {
		return recordsTotal;
	}
	
	public void setRecordsTotal(int recordsTotal) {
		this.recordsTotal = recordsTotal;
	}
	
	public int getRecordsFiltered() {
		return recordsFiltered;
	}
	
	public void setRecordsFiltered(int recordsFiltered) {
		this.recordsFiltered = recordsFiltered;
	}
	
	public int getDraw() {
		return draw;
	}
	
	public void setDraw(int draw) {
		this.draw = draw;
	}
	
	public List getData() {
		return data;
	}
	
	public void setData(List data) {
		this.data = data;
	}
	
}
