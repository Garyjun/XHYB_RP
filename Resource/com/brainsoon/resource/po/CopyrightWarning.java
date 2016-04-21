package com.brainsoon.resource.po;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.brainsoon.common.po.BaseHibernateObject;
import com.brainsoon.system.util.MetadataSupport;

/**
 * 版权预警
 */
@Entity
@Table(name = "copyright_warning")
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class CopyrightWarning extends BaseHibernateObject{
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
	private Integer id;
	@Column(name = "objectId",length=128)
	private String objectId;
	@Column(name = "crtPerson",length=256)
	private String crtPerson;
	@Column(name = "authorizer",length=256)
	private String authorizer;
	@Column(name = "authStartDate",length=32)
	private String authStartDate;
	@Column(name = "authEndDate",length=32)
	private String authEndDate;
	@Column(name = "module",length=16)
	private String module;
	@Column(name = "type",length=16)
	private String type;
	@Column(name = "publishType",length=16)
	private String publishType;
	@Column(name = "contractCode",length=128)
	private String contractCode;
	@Column(name = "title",length=256)
	private String title;
    @Column
    private Integer platformId;
    @Transient
    private String temPublishType;
	// Constructors

	/** default constructor */
	public CopyrightWarning() {
	}

	/** minimal constructor */
	public CopyrightWarning(Integer id) {
		this.id = id;
	}

	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	
	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	
	public String getCrtPerson() {
		return crtPerson;
	}

	public void setCrtPerson(String crtPerson) {
		this.crtPerson = crtPerson;
	}
	
	public String getAuthorizer() {
		return authorizer;
	}

	public void setAuthorizer(String authorizer) {
		this.authorizer = authorizer;
	}
	
	public String getAuthStartDate() {
		return authStartDate;
	}

	public void setAuthStartDate(String authStartDate) {
		this.authStartDate = authStartDate;
	}
	
	public String getAuthEndDate() {
		return authEndDate;
	}

	public void setAuthEndDate(String authEndDate) {
		this.authEndDate = authEndDate;
	}

	public String getContractCode() {
		return contractCode;
	}

	public void setContractCode(String contractCode) {
		this.contractCode = contractCode;
	}
	
	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPublishType() {
		return publishType;
	}

	public void setPublishType(String publishType) {
		this.publishType = publishType;
	}

	public Integer getPlatformId() {
		return platformId;
	}

	public void setPlatformId(Integer platformId) {
		this.platformId = platformId;
	}
	
	public String getTemPublishType() {
		return temPublishType = MetadataSupport.getTitleByPublishType(getPublishType());
	}

	public void setTemPublishType(String temPublishType) {
		this.temPublishType = temPublishType;
	}

	@Override
	public String getObjectDescription() {
		// TODO Auto-generated method stub
		return "版权预警";
	}
	@Override
	public String getEntityDescription() {
		// TODO Auto-generated method stub
		return "版权预警";
	}
	
}