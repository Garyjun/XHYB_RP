package com.brainsoon.resource.support;

/**
 * 记录每个单元格的详细信息，但只记录order大于0的数据，这些数据参与分类体系
 * @author zuo
 *
 */
public class ExcelDataCell implements Comparable<ExcelDataCell>{
	private Integer order;
	private String data;
	private String name;
	private Integer referClass;//0 保存在CommonMetaData对象上，1保存在ExtendMetaData ，其他忽略
	private String dataCode;
	public ExcelDataCell() {
		super();
	}
	public Integer getOrder() {
		return order;
	}
	public ExcelDataCell(Integer order, String data, String name,Integer referClass) {
		super();
		this.order = order;
		this.data = data;
		this.name = name;
		this.referClass = referClass;
	}
	public void setOrder(Integer order) {
		this.order = order;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public int compareTo(ExcelDataCell o) {
		return this.getOrder().compareTo(o.getOrder());
	}
	public Integer getReferClass() {
		return referClass;
	}
	public void setReferClass(Integer referClass) {
		this.referClass = referClass;
	}
	public String getDataCode() {
		return dataCode;
	}
	public void setDataCode(String dataCode) {
		this.dataCode = dataCode;
	}
}
