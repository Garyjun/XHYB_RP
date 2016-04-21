package com.brainsoon.system.model.log;

import java.io.Serializable;
import java.util.Date;

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
import com.brainsoon.system.model.User;

/**
 * <dl>
 * <dt>SysOperateHistory</dt>
 * <dd>Description:工作流操作历史记录</dd>
 * <dd>Copyright: Copyright (C) 2009</dd>
 * <dd>Company: 博云易迅</dd>
 * <dd>CreateDate:2014-05-16</dd>
 * </dl>
 * 
 * @author xujie
 */
@Entity
@Table(name = "sys_operate_history")
public class SysOperateHistory extends BaseHibernateObject {

	private static final long serialVersionUID = 7982454731202669499L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false)
	private long id;
	/** 实体代号+实体主键 */
	@Column(name = "bean_id")
	private String beanId;

	/** 实体最新状态 */
	@Column(name = "bean_status")
	private String beanStatus;

	/** 备注信息 */
	@Column(name = "remark")
	private String remark;

	/** 操作者 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id")
	private User operator;

	/** 操作时间 */
	@Column(name = "operate_time")
	private Date operateTime;

	/** 更新到该状态的描述 */
	@Column(name = "operate_desc")
	private String operateDesc;

	@Override
	public String getEntityDescription() {
		return "流程操作历史" + id + beanId;
	}

	@Override
	public String getObjectDescription() {
		return "流程操作历史";
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getBeanId() {
		return beanId;
	}

	public void setBeanId(String beanId) {
		this.beanId = beanId;
	}

	public String getBeanStatus() {
		return beanStatus;
	}

	public void setBeanStatus(String beanStatus) {
		this.beanStatus = beanStatus;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public User getOperator() {
		return operator;
	}

	public void setOperator(User operator) {
		this.operator = operator;
	}

	public Date getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}

	public String getOperateDesc() {
		return operateDesc;
	}

	public void setOperateDesc(String operateDesc) {
		this.operateDesc = operateDesc;
	}

}
