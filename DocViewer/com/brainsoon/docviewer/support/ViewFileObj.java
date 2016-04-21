package com.brainsoon.docviewer.support;

public class ViewFileObj {

	int num; //状态参数
	String desc; //说明
	boolean status = false; //状态
	
	
	public ViewFileObj() {
		super();
		// tanghui Auto-generated constructor stub
	}
	public ViewFileObj(int num, String desc, boolean status) {
		super();
		this.num = num;
		this.desc = desc;
		this.status = status;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	
	
}
