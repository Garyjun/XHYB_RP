package com.brainsoon.resource.po;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.brainsoon.common.po.BaseHibernateObject;
import com.brainsoon.common.support.OperDbUtils;

/**
 * 版权预警
 */
@Entity
@Table(name = "copyright_repeat")
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class CopyrightRepeat extends BaseHibernateObject implements java.io.Serializable {

	// Fields

	private static final long serialVersionUID = -1056178654494091926L;
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
	private Integer id;
	@Column(name = "source",length=128)
	private String source;
	@Column(name = "type",length=32)
	private String type;
	@Column(name = "title",length=128)
	private String title;
	@Column(name = "creator",length=128)
	private String creator;
	@Column(name = "crtType",length=128)
	private String crtType;
	@Column(name = "crtPerson",length=128)
	private String crtPerson;
	@Column(name = "authorizer",length=128)
	private String authorizer;
	@Column(name = "authArea",length=128)
	private String authArea;
	@Column(name = "authChannel",length=128)
	private String authChannel;
	@Column(name = "authTimeLimit",length=128)
	private String authTimeLimit;
	@Column(name = "authStartDate",length=128)
	private String authStartDate;
	@Column(name = "authEndDate",length=128)
	private String authEndDate;
	@Column(name = "authLanguage",length=128)
	private String authLanguage;
	@Column(name = "permitRight",length=128)
	private String permitRight;
	@Column(name = "collaPattern",length=128)
	private String collaPattern;
	@Column(name = "contractCode",length=128)
	private String contractCode;
	// Constructors

	/** default constructor */
	public CopyrightRepeat() {
	}

	/** minimal constructor */
	public CopyrightRepeat(Integer id) {
		this.id = id;
	}


	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	
	

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public String getCrtType() {
		return crtType;
	}
	public String getCrtTypeDesc() {
		if(crtType!=null && !"".equals(crtType)){
			return OperDbUtils.queryNameByIndexAndKey("crtType", getCrtType());
		}else{
			return "";
		}
	}
	public void setCrtType(String crtType) {
		this.crtType = crtType;
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

	public String getAuthArea() {
		return authArea;
	}
	public String getAuthAreaDesc() {
		if(authArea!=null && !"".equals(authArea)){
			return OperDbUtils.queryNameByIndexAndKey("authArea", authArea);
		}else{
			return "";
		}
	}
	public void setAuthArea(String authArea) {
		this.authArea = authArea;
	}

	public String getAuthChannel() {
		return authChannel;
	}
	public String getAuthChannelDesc() {
		if(authChannel!=null && !"".equals(authChannel)){
			return OperDbUtils.queryNameByIndexAndKey("authChannel", authChannel);
		}else{
			return "";
		}
	}
	public void setAuthChannel(String authChannel) {
		this.authChannel = authChannel;
	}

	public String getAuthTimeLimit() {
		return authTimeLimit;
	}

	public void setAuthTimeLimit(String authTimeLimit) {
		this.authTimeLimit = authTimeLimit;
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

	public String getAuthLanguage() {
		return authLanguage;
	}
	public String getAuthLanguageDesc() {
		if(authLanguage!=null && !"".equals(authLanguage)){
			return OperDbUtils.queryNameByIndexAndKey("authLanguage", authLanguage);
		}else{
			return "";
		}
	}
	public void setAuthLanguage(String authLanguage) {
		this.authLanguage = authLanguage;
	}

	public String getPermitRight() {
		return permitRight;
	}
	public String getPermitRightDesc() {
		if(permitRight!=null && !"".equals(permitRight)){
			return OperDbUtils.queryNameByIndexAndKey("permitRight", permitRight);
		}else{
			return "";
		}
	}
	public void setPermitRight(String permitRight) {
		this.permitRight = permitRight;
	}

	public String getCollaPattern() {
		return collaPattern;
	}
	public String getCollaPatternDesc() {
		if(collaPattern!=null && !"".equals(collaPattern)){
			return OperDbUtils.queryNameByIndexAndKey("collaPattern", collaPattern);
		}else{
			return "";
		}
	}
	public void setCollaPattern(String collaPattern) {
		this.collaPattern = collaPattern;
	}

	public String getContractCode() {
		return contractCode;
	}

	public void setContractCode(String contractCode) {
		this.contractCode = contractCode;
	}

	@Override
	public String getObjectDescription() {
		// TODO Auto-generated method stub
		return "版权导入重复";
	}
	@Override
	public String getEntityDescription() {
		// TODO Auto-generated method stub
		return "版权导入重复";
	}
	
}