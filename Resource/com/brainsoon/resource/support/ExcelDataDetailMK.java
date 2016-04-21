package com.brainsoon.resource.support;

public class ExcelDataDetailMK {
	public String name;
	public String nameCN;
	public int isMeta;
	public int order;//顺序，大于0参与分类体系
	public int necessary;//是否必须  0 非必选、1必选
	public int referClass;//0 保存在CommonMetaData对象上，1保存在ExtendMetaData ，其他忽略

	public ExcelDataDetailMK(String name, int isMeta,int necessary,int order, 
			int referClass,String nameCN) {
		super();
		this.name = name;
		this.isMeta = isMeta;
		this.order = order;
		this.necessary = necessary;
		this.referClass = referClass;
		this.nameCN = nameCN;
	}
	public ExcelDataDetailMK(String name) {
		super();
		this.name = name;
	}

	public ExcelDataDetailMK() {
	}
}