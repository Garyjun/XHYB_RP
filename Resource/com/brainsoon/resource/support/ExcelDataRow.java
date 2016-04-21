package com.brainsoon.resource.support;
/**
 * 保存excel的每行数据
 * @author zuo
 *
 */
public class ExcelDataRow {
	
	private int num;//行号
	
	private String [] datas;

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String[] getDatas() {
		return datas;
	}

	public void setDatas(String[] datas) {
		this.datas = datas;
	}

}
