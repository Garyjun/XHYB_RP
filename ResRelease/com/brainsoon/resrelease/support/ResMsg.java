package com.brainsoon.resrelease.support;


public class ResMsg{

	private String name;
	private String resId;
	private String resPath;
	private boolean qfm; //缺封面
	private boolean qyf; //缺源文件
	private boolean qcf; //缺转换后的文件
	private String zy; //摘要
	
	
	public ResMsg() {
		super();
	}
	
	
	public ResMsg(String name, String resId, String resPath, boolean qfm,
			boolean qyf, boolean qcf,String zy) {
		super();
		this.name = name;
		this.resId = resId;
		this.resPath = resPath;
		this.qfm = qfm;
		this.qyf = qyf;
		this.qcf = qcf;
		this.zy = zy;
	}
	
	
	
	public ResMsg(String name, String resId, String resPath, boolean qfm,
			boolean qyf, boolean qcf) {
		super();
		this.name = name;
		this.resId = resId;
		this.resPath = resPath;
		this.qfm = qfm;
		this.qyf = qyf;
		this.qcf = qcf;
	}


	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getResId() {
		return resId;
	}
	public void setResId(String resId) {
		this.resId = resId;
	}
	public String getResPath() {
		return resPath;
	}
	public void setResPath(String resPath) {
		this.resPath = resPath;
	}
	public boolean getQfm() {
		return qfm;
	}
	public void setQfm(boolean qfm) {
		this.qfm = qfm;
	}
	public boolean getQyf() {
		return qyf;
	}
	public void setQyf(boolean qyf) {
		this.qyf = qyf;
	}
	public boolean getQcf() {
		return qcf;
	}
	public void setQcf(boolean qcf) {
		this.qcf = qcf;
	}


	public String getZy() {
		return zy;
	}


	public void setZy(String zy) {
		this.zy = zy;
	}


	@Override
	public String toString() {
		return "ResMsg [name=" + name + ", resId=" + resId + ", resPath="
				+ resPath + ", qfm=" + qfm + ", qyf=" + qyf + ", qcf=" + qcf
				+ ", zy=" + zy + "]";
	}
	
	
}
