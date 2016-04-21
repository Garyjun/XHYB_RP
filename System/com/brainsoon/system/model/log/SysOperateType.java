package com.brainsoon.system.model.log;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.brainsoon.common.po.BaseHibernateObject;
import com.brainsoon.system.model.Module;

/**
 * <dl>
 * <dt>SysOperateType</dt>
 * <dd>Description: 系统操作类型</dd>
 * <dd>Copyright: Copyright (C) 2009</dd>
 * <dd>Company: 博云易迅</dd>
 * <dd>CreateDate:2014-05-16</dd>
 * </dl>
 * 
 * @author xujie
 */
@Entity
@Table(name = "sys_operate_type")
public class SysOperateType extends BaseHibernateObject {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false)
	private Long id;

	// 操作标识
	@Column(name = "operate_key")
	private String operateKey;

	// 操作名称
	@Column(name = "operate_name")
	private String operateName;

	// 操作对应模块
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "module_id")
	private Module module;

	public Long getId() {
		return id;
	}

	public Module getModule() {
		return module;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setModule(Module module) {
		this.module = module;
	}

	public String getOperateKey() {
		return operateKey;
	}

	public String getOperateName() {
		return operateName;
	}

	public void setOperateKey(String operateKey) {
		this.operateKey = operateKey;
	}

	public void setOperateName(String operateName) {
		this.operateName = operateName;
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
