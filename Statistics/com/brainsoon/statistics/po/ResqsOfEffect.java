package com.brainsoon.statistics.po;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.brainsoon.common.po.BaseHibernateObject;
import com.brainsoon.system.support.SystemConstants.LibType;
import com.brainsoon.system.support.SystemConstants.ResourceMoudle;
import com.brainsoon.system.support.SystemConstants.ResourceType;

/**
 * 绩效统计
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "resqs_of_effect")
public class ResqsOfEffect extends BaseHibernateObject {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false)
	private Long id;

	// 用户名称
	@Column(name = "userId")
	private Long userId;

	@Transient
	private String userName;
	// 操作类型
	@Column(name = "operateType")
	private String operateType;

	// 成熟度
	@Column(name = "maturityName")
	private String maturityName;

	// 评价星级
	@Column(name = "starRating")
	private String starRating;

	// 归档日期
	@Column(name = "filingDate")
	private Date filingDate;

	// 统计数量
	@Column(name = "countNum")
	private int countNum;

	public String getStarRating() {
		return starRating;
	}

	public void setStarRating(String starRating) {
		this.starRating = starRating;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMaturityName() {
		return maturityName;
	}

	public void setMaturityName(String maturityName) {
		this.maturityName = maturityName;
	}

	public Date getFilingDate() {
		return filingDate;
	}

	public void setFilingDate(Date filingDate) {
		this.filingDate = filingDate;
	}

	public int getCountNum() {
		return countNum;
	}

	public void setCountNum(int countNum) {
		this.countNum = countNum;
	}

	public String getOperateType() {
		return operateType;
	}

	public void setOperateType(String operateType) {
		this.operateType = operateType;
	}

	@Override
	public String getObjectDescription() {
		return "绩效计件对象";
	}

	@Override
	public String getEntityDescription() {
		return "绩效计件";
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}