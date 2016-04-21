package com.brainsoon.resource.support;

import java.util.List;
/**
 * excel清单文件
 * @author zuo
 *
 */
public class ExcelData {
	
	private String qName;
	
	private String batchNum;
	
	private int totalNum; //数据总行数
	
	private ExcelDataDetailMK [] markers;
	
	private List<ExcelDataRow> rows;
	
	public String getqName() {
		return qName;
	}
	public void setqName(String qName) {
		this.qName = qName;
	}
	public String getBatchNum() {
		return batchNum;
	}
	public void setBatchNum(String batchNum) {
		this.batchNum = batchNum;
	}
	public ExcelDataDetailMK[] getMarkers() {
		return markers;
	}
	public void setMarkers(ExcelDataDetailMK[] markers) {
		this.markers = markers;
	}
	public List<ExcelDataRow> getRows() {
		return rows;
	}
	public void setRows(List<ExcelDataRow> rows) {
		this.rows = rows;
	}
	public int getTotalNum() {
		return totalNum;
	}
	public void setTotalNum(int totalNum) {
		this.totalNum = totalNum;
	}
}
