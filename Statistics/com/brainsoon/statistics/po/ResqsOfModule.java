package com.brainsoon.statistics.po;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.brainsoon.common.po.BaseHibernateObject;
import com.brainsoon.system.support.SystemConstants.LibType;
import com.brainsoon.system.support.SystemConstants.ResourceCaType;
import com.brainsoon.system.support.SystemConstants.ResourceMoudle;
import com.brainsoon.system.support.SystemConstants.ResourceType;


@Entity
@Table(name = "resqs_of_module")
public class ResqsOfModule extends BaseHibernateObject{
	private static final long serialVersionUID = 6022426027760900277L;
	private Long id;
	//成熟度名称 原始 2、标准 1、应用 3
	private int maturityName;
	//资源模块代码
	private String moduleName;
	//资源类型代码
	private String resType;
	//教材版本
	private String tmVersion;
	//学科
	private String tmSubject;
	//学段
	private String educationalPhase;
	//年级
	private String tmGrade;
	//归档日期
	private Date filingDate;
	private int platformId;
	
	//数量
	private int num;
	@SuppressWarnings("unused")
	private String moduleNameDesc;
	@SuppressWarnings("unused")
	private String resTypeDesc;
	@SuppressWarnings("unused")
	private String maturityNameDesc;
	
	private String filingDate2;
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name = "maturityName", length = 1)
	public int getMaturityName() {
		return maturityName;
	}
	public void setMaturityName(int maturityName) {
		this.maturityName = maturityName;
	}
	@Column(name = "moduleName", length = 3)
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	@Column(name = "resType", length = 3)
	public String getResType() {
		return resType;
	}
	public void setResType(String resType) {
		this.resType = resType;
	}
	@Column(name = "tmVersion", length = 10)
	public String getTmVersion() {
		return tmVersion;
	}
	public void setTmVersion(String tmVersion) {
		this.tmVersion = tmVersion;
	}
	@Column(name = "tmSubject", length = 10)
	public String getTmSubject() {
		return tmSubject;
	}
	public void setTmSubject(String tmSubject) {
		this.tmSubject = tmSubject;
	}
	@Column(name = "tmGrade", length = 11)
	public String getTmGrade() {
		return tmGrade;
	}
	public void setTmGrade(String tmGrade) {
		this.tmGrade = tmGrade;
	}
	@Column(name = "educationalPhase", length = 10)
	public String getEducationalPhase() {
		return educationalPhase;
	}
	public void setEducationalPhase(String educationalPhase) {
		this.educationalPhase = educationalPhase;
	}
	@Column(name = "filingDate", length = 10)
	public Date getFilingDate() {
		return filingDate;
	}
	public void setFilingDate(Date filingDate) {
		this.filingDate = filingDate;
	}
	@Column(name = "num", length = 11)
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	
	@Column(name = "platformId", length = 11)
	public int getPlatformId() {
		return platformId;
	}
	public void setPlatformId(int platformId) {
		this.platformId = platformId;
	}
	
	@Transient
	public String getModuleNameDesc() {
		return ResourceMoudle.getValueByKey(moduleName);
	}
	public void setModuleNameDesc(String moduleNameDesc) {
		this.moduleNameDesc = moduleNameDesc;
	}
	@Transient
	public String getResTypeDesc() {
		if(StringUtils.equals(resType, "T10")||StringUtils.equals(resType, "T11")){
			return ResourceCaType.getValueByKey(resType);
		}else{
			return ResourceType.getValueByKey(resType);
		}
	}
	public void setResTypeDesc(String resTypeDesc) {
		this.resTypeDesc = resTypeDesc;
	}
	@Transient
	public String getMaturityNameDesc() {
		return LibType.getValueByKey(maturityName+"");
	}
	public void setMaturityNameDesc(String maturityNameDesc) {
		this.maturityNameDesc = maturityNameDesc;
	}
	
	@Transient
	public String getObjectDescription() {
		// TODO Auto-generated method stub
		return null;
	}
	@Transient
	public String getEntityDescription() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Transient
	public String getFilingDate2() {
		return filingDate2;
	}
	public void setFilingDate2(String filingDate2) {
		this.filingDate2 = filingDate2;
	}
	
}
