package com.brainsoon.system.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import com.brainsoon.common.po.BaseHibernateObject;
/**
 * BSRCM应用com.brainsoon.bsrcm.dcore.po.SysMetadataType.java 创建时间：2011-12-7 创建者：
 * liusy DcType TODO
 * 
 */
@Entity
@Table(name = "codeData")
public class Code extends BaseHibernateObject {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "codeId", nullable = false)
    private Long codeId;
    //编码名称
    @Column
    private String codeName;
    //编码状态
    @Column
    private String codeStatus;
    //接口方名称
    @Column
    private String adapterName;
    //接口方类型
    @Column
    private String adapterType;
    //适配编码
    @Column
    private String adapterCode;
    @Column
    private String adapterVer;
    @Column
    private Integer platformId;
    //默认编码
    @Column
    private String codeDefault;
    @Column
    private String codeStage;
    @Column
    private String codeGrade;
    @Column
    private String name;

    
	public Long getCodeId() {
		return codeId;
	}

	public void setCodeId(Long codeId) {
		this.codeId = codeId;
	}

	public Integer getPlatformId() {
		return platformId;
	}

	public void setPlatformId(Integer platformId) {
		this.platformId = platformId;
	}

	public String getCodeName() {
		return codeName;
	}

	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}

	public String getCodeStatus() {
		return codeStatus;
	}

	public void setCodeStatus(String codeStatus) {
		this.codeStatus = codeStatus;
	}

	public String getAdapterType() {
		return adapterType;
	}

	public void setAdapterType(String adapterType) {
		this.adapterType = adapterType;
	}

	public String getAdapterCode() {
		return adapterCode;
	}

	public void setAdapterCode(String adapterCode) {
		this.adapterCode = adapterCode;
	}

	public String getAdapterVer() {
		return adapterVer;
	}

	public void setAdapterVer(String adapterVer) {
		this.adapterVer = adapterVer;
	}

	public String getCodeDefault() {
		return codeDefault;
	}

	public void setCodeDefault(String codeDefault) {
		this.codeDefault = codeDefault;
	}

	public String getAdapterName() {
		return adapterName;
	}

	public void setAdapterName(String adapterName) {
		this.adapterName = adapterName;
	}

	public String getCodeStage() {
		return codeStage;
	}

	public void setCodeStage(String codeStage) {
		this.codeStage = codeStage;
	}

	public String getCodeGrade() {
		return codeGrade;
	}

	public void setCodeGrade(String codeGrade) {
		this.codeGrade = codeGrade;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
