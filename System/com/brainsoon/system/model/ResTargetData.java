package com.brainsoon.system.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.brainsoon.common.po.BaseHibernateObject;
import com.brainsoon.common.support.MetadataDefineCacheMap;
import com.brainsoon.common.support.SysResTypeCacheMap;
import com.brainsoon.resource.util.ResUtils;
/**
 * BSRCM应用com.brainsoon.bsrcm.dcore.po.SysMetadataType.java 创建时间：2011-12-7 创建者：
 * liusy DcType TODO
 * 
 */
@Entity
@Table(name = "res_target_data")
public class ResTargetData extends BaseHibernateObject {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column
    private String pid;
    @Column
    private Long targetId;
    @Column
    private String objectId;
    @Column
    private Long libType;
    @Column
    private Long productId;
    @Column
    private Long platformId;
    @Column
    private String module;
    @Column
    private String type;
    @Column
    private String targetName;
    @Column
    private Long targetNum;
    @Column(name = "user_id")
    private Long userId;
    @Column
    private Integer status;
    @Column
    private Integer targetStatus;
    @Column
    private String description;
    @Column
    private String targetType;
    @Column
    private String xpath;
	@Transient
	private String moduleType;
	@Transient
	private String targetCb;
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public Long getTargetId() {
		return targetId;
	}

	public void setTargetId(Long targetId) {
		this.targetId = targetId;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public Long getLibType() {
		return libType;
	}

	public void setLibType(Long libType) {
		this.libType = libType;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Long getPlatformId() {
		return platformId;
	}

	public void setPlatformId(Long platformId) {
		this.platformId = platformId;
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

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	
	public Long getTargetNum() {
		return targetNum;
	}

	public void setTargetNum(Long targetNum) {
		this.targetNum = targetNum;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTargetType() {
		return targetType;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

	public String getXpath() {
		return xpath;
	}

	public void setXpath(String xpath) {
		this.xpath = xpath;
	}

	public String getModuleType() {
		return (String) SysResTypeCacheMap.getValue(getModule()+"");
	}

	public void setModuleType(String moduleType) {
		this.moduleType = moduleType;
	}

	public String getTargetCb() {
		return (String)ResUtils.targetCb(getTargetType());
	}

	public void setTargetCb(String targetCb) {
		this.targetCb = targetCb;
	}

	public Integer getTargetStatus() {
		return targetStatus;
	}

	public void setTargetStatus(Integer targetStatus) {
		this.targetStatus = targetStatus;
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
