package com.brainsoon.system.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.brainsoon.common.po.BaseHibernateObject;

@Entity
@Table(name = "sys_company")
public class Company extends BaseHibernateObject{
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name ="id", nullable = false)
	private Long id;
	
	@Column
	private String name;  //公司名称
	
	@Column(name = "short_name")
	private String shortName; //公司简称
	
	@Column
	private String address;  //公司地址
	
	@Column
	private String code;  //邮编
	
	@Column(name = "belongs_city")
	private String city;  //所属城市
	
	@Column(name = "contact_first")
	private String contactFirst;  //联系人1
	
	@Column(name = "telephone_first")
	private String telephoneFirst; //联系人1联系电话
	
	@Column(name= "contact_second")
	private String contactSecond;   //联系人2
	
	@Column(name = "telephone_second")
	private String telephoneSecond;   //联系人2联系电话
	
	@Column
	private String email;   //电子邮件
	
	@Column
	private String description;  // 简介
	
	@Column(name = "company_type")
	private String companyType;   //单位类型
	
	@Column(name = "created_by")
	private String createder;     //创建人
	
	@Column(name = "modified_by")
	private String modifieder;    //修改人
	
	@Column(name = "created_time")
	private Date createdTime;    //创建时间
	
	@Column(name = "modified_time")
	private Date modifiedTime;   //修改时间
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getContactFirst() {
		return contactFirst;
	}

	public void setContactFirst(String contactFirst) {
		this.contactFirst = contactFirst;
	}

	public String getTelephoneFirst() {
		return telephoneFirst;
	}

	public void setTelephoneFirst(String telephoneFirst) {
		this.telephoneFirst = telephoneFirst;
	}

	public String getContactSecond() {
		return contactSecond;
	}

	public void setContactSecond(String contactSecond) {
		this.contactSecond = contactSecond;
	}

	public String getTelephoneSecond() {
		return telephoneSecond;
	}

	public void setTelephoneSecond(String telephoneSecond) {
		this.telephoneSecond = telephoneSecond;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCompanyType() {
		return companyType;
	}

	public void setCompanyType(String companyType) {
		this.companyType = companyType;
	}

	public String getCreateder() {
		return createder;
	}

	public void setCreateder(String createder) {
		this.createder = createder;
	}

	public String getModifieder() {
		return modifieder;
	}

	public void setModifieder(String modifieder) {
		this.modifieder = modifieder;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Date getModifiedTime() {
		return modifiedTime;
	}

	public void setModifiedTime(Date modifiedTime) {
		this.modifiedTime = modifiedTime;
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
