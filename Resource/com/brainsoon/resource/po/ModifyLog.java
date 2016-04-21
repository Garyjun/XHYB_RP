package com.brainsoon.resource.po;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.brainsoon.common.po.BaseHibernateObject;

/**
 * Area entity. @author MyEclipse Persistence Tools
 * 区
 */
@Entity
@Table(name = "modify_log")
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class ModifyLog extends BaseHibernateObject implements java.io.Serializable {
	private static final long serialVersionUID = -1056178654494091923L;
	@Id
	@GeneratedValue
	private Integer id;
	@Column(name = "res_id",length=64)
	private String resId;
	@Column(name = "modify_field",length=32)
	private String modifyField;
	@Column(name = "modify_old",length=512)
	private String modifyOld;
	@Column(name = "modify_new",length=512)
	private String modifyNew;
	@Column(name = "modify_time",length=16)
	private Date modifyTime;
	@Column(name = "res_type",length=16)
	private String restype;

	public ModifyLog() {
	}

	
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getResId() {
		return resId;
	}

	public void setResId(String resId) {
		this.resId = resId;
	}
	
	public String getModifyField() {
		return modifyField;
	}

	public void setModifyField(String modifyField) {
		this.modifyField = modifyField;
	}
	
	public String getModifyOld() {
		return modifyOld;
	}

	public void setModifyOld(String modifyOld) {
		this.modifyOld = modifyOld;
	}
	
	public String getModifyNew() {
		return modifyNew;
	}

	public void setModifyNew(String modifyNew) {
		this.modifyNew = modifyNew;
	}
	
	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}


	public String getRestype() {
		return restype;
	}


	public void setRestype(String restype) {
		this.restype = restype;
	}


	@Override
	public String getObjectDescription() {
		return null;
	}

	@Override
	public String getEntityDescription() {
		return null;
	}

	
}