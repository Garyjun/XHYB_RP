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

/**
 * <dl>
 * <dt>SysOperateLog</dt>
 * <dd>Description:系统操作日志记录</dd>
 * <dd>Copyright: Copyright (C) 2009</dd>
 * <dd>Company: 博云易迅</dd>
 * <dd>CreateDate:2014-05-16</dd>
 * </dl>
 * 
 * @author xujie
 */
@Entity
@Table(name = "sys_operate_log")
public class SysOperateLog extends BaseHibernateObject {
	
	private static final long serialVersionUID = 5152122200841306840L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "log_type")
	private String logType;
	
	@Column(name = "platformId")
	private int platformId;

	@Column(name = "operator")
	private String operator;

	@Column(name = "operate_type_key")
	private String operateType;

	@Column(name = "operate_desc")
	private String operateDesc;

	@Column(name = "operate_time")
	private Date operateTime;

	@Column(name = "user_ip")
	private String userIp;

	@Column
	private String loginname;
	
	public String getLoginname() {
		return loginname;
	}

	public void setLoginname(String loginname) {
		this.loginname = loginname;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "operate_type_id")
	private SysOperateType sysOperateType;

	@Override
	public String getObjectDescription() {
		return null;
	}

	@Override
	public String getEntityDescription() {
		return null;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getPlatformId() {
		return platformId;
	}

	public void setPlatformId(int platformId) {
		this.platformId = platformId;
	}

	public String getLogType() {
		return logType;
	}

	public void setLogType(String logType) {
		this.logType = logType;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getOperateType() {
		return operateType;
	}

	public void setOperateType(String operateType) {
		this.operateType = operateType;
	}

	public String getOperateDesc() {
		return operateDesc;
	}

	public void setOperateDesc(String operateDesc) {
		this.operateDesc = operateDesc;
	}

	public Date getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}

	public String getUserIp() {
		return userIp;
	}

	public void setUserIp(String userIp) {
		this.userIp = userIp;
	}

	public SysOperateType getSysOperateType() {
		return sysOperateType;
	}

	public void setSysOperateType(SysOperateType sysOperateType) {
		this.sysOperateType = sysOperateType;
	}

}
