package com.brainsoon.system.model;

import com.brainsoon.common.po.BaseHibernateObject;


public class SysDirSupport extends BaseHibernateObject {

	private static final long serialVersionUID = 369058824608280576L;
	private SysDir sysdir;
	private String[] fileType;
	
	public SysDir getSysdir() {
		return sysdir;
	}

	public void setSysdir(SysDir sysdir) {
		this.sysdir = sysdir;
	}

	public String[] getFileType() {
		return fileType;
	}

	public void setFileType(String[] fileType) {
		this.fileType = fileType;
	}
	
	public SysDirSupport(SysDir sysdir, String[] fileType) {
		super();
		this.sysdir = sysdir;
		this.fileType = fileType;
	}
	
	public SysDirSupport() {
		super();
	}

	@Override
	public String getObjectDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEntityDescription() {
		// TODO Auto-generated method stub
		return null;
	}


	
}
