package com.brainsoon.resrelease.po;

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
import javax.persistence.Transient;

import com.brainsoon.common.po.BaseHibernateObject;
import com.brainsoon.resrelease.support.ResReleaseConstant;
import com.brainsoon.system.model.User;
@Entity
@Table(name = "res_order_detail")
public class ResOrderDetail extends BaseHibernateObject {
	
	private static final long serialVersionUID = -75214435801335115L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id", nullable=false)
	private Long id;
	
	@Column(name="res_id")
	private String resId;
	
	@Column(name="order_id")
	private Long orderId;
	
	@Transient
	private String resTypeDesc;
	
	@Column(name="STATUS")
	private String status;
	
	@Transient
	private String statusDesc;
	
	@ManyToOne(fetch=FetchType.EAGER)   
	@JoinColumn(name="create_user")
	private User createUser;

	@Column(name="create_time")
	private Date createTime;
	
	@ManyToOne(fetch=FetchType.EAGER)   
	@JoinColumn(name="update_user")
	private User updateUser; 
	
	@Column(name="update_time")
	private Date updateTime;
	
	@Column(name="memo")
	private String memo;
	
	@Column(name = "platformId")
	private int platformId;
	
	@Column(name = "module_name")
	private String module;
	
	@Column(name="res_typeId")
	private String resTypeId;
	
	@Column(name = "version_name")
	private String version;
	
	@Column(name = "educational_phase_name")
	private String educationalPhase;
	
	@Column(name = "subject_name")
	private String subject;
	
	@Column(name = "grade_name")
	private String grade;
	
	@Column(name = "fascicule_name")
	private String fascicule;
	
	@Column(name = "unit_name")
	private String unit;
	
	@Column(name = "node_name")
	private String node;
	
	@Column(name = "title")
	private String title;
	
	@Column(name = "creator")
	private String creator;
	
	@Column(name = "keywords")
	private String keywords;
	
	@Column(name = "format_name")
	private String format;
	
	@Column(name = "res_modified_time")
	private String modifiedTime;
	
	@Column(name = "res_type")
	private String resType;
	
	@Column(name = "remark")
	private String remark;
	
	@Column(name = "posttype")
	private String posttype;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getResId() {
		return resId;
	}

	public void setResId(String resId) {
		this.resId = resId;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public String getResTypeId() {
		return resTypeId;
	}

	public void setResTypeId(String resTypeId) {
		this.resTypeId = resTypeId;
	}

	public String getResTypeDesc(){
		return ResReleaseConstant.ResourceType.getValueByKey(getResTypeId());
	}
	
	public void setResTypeDesc(String resTypeDesc) {
		this.resTypeDesc = resTypeDesc;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatusDesc() {
		return ResReleaseConstant.ProcessStatus.getValueByKey(getStatus());
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}

	public User getCreateUser() {
		return createUser;
	}

	public void setCreateUser(User createUser) {
		this.createUser = createUser;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public User getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(User updateUser) {
		this.updateUser = updateUser;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public int getPlatformId() {
		return platformId;
	}

	public void setPlatformId(int platformId) {
		this.platformId = platformId;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getEducationalPhase() {
		return educationalPhase;
	}

	public void setEducationalPhase(String educationalPhase) {
		this.educationalPhase = educationalPhase;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getFascicule() {
		return fascicule;
	}

	public void setFascicule(String fascicule) {
		this.fascicule = fascicule;
	}
	
	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
	
	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getModifiedTime() {
		return modifiedTime;
	}

	public void setModifiedTime(String modifiedTime) {
		this.modifiedTime = modifiedTime;
	}

	public String getResType() {
		return resType;
	}

	public void setResType(String resType) {
		this.resType = resType;
	}

	@Override
	public String getObjectDescription() {
		return null;
	}

	@Override
	public String getEntityDescription() {
		return null;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getPosttype() {
		return posttype;
	}

	public void setPosttype(String posttype) {
		this.posttype = posttype;
	}
	
	

}
