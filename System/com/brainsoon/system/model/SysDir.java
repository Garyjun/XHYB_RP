package com.brainsoon.system.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.brainsoon.common.po.BaseHibernateObject;
import com.brainsoon.common.support.OperDbUtils;
import com.brainsoon.common.support.SysResTypeCacheMap;
import com.brainsoon.system.support.SystemConstants;
import com.brainsoon.system.support.SystemConstants.Status;

/**
 * @ClassName: SysDir
 * @Description:  元数据
 * @author shea
 *
 */
@Entity
@Table(name = "sys_dir")
public class SysDir extends BaseHibernateObject {

	private static final long serialVersionUID = 8355336208496189306L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false)
	private Long id;
	@Column
	private String dirEnName;
	@Column
	private String dirCnName;
	@Column
	private String fileTypes;
	@Column
	private String resType;
	@Transient
	private String resTypeNum;
	@Column
	private String status;
	@Column
	private String dirDesc;
	@Column(name = "platformId")
	private int platformId;
	@Transient
	private String statusDesc;
	@Transient
	private String resTypeDesc;
	@Column
	private String lifeCycle;
	@Transient
	private String lifeCycleDesc;
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDirEnName() {
		return dirEnName;
	}

	public int getPlatformId() {
		return platformId;
	}

	public void setPlatformId(int platformId) {
		this.platformId = platformId;
	}

	public void setDirEnName(String dirEnName) {
		this.dirEnName = dirEnName;
	}

	public String getDirCnName() {
		return dirCnName;
	}

	public void setDirCnName(String dirCnName) {
		this.dirCnName = dirCnName;
	}

	public String getFileTypes() {
		return fileTypes;
	}

	public void setFileTypes(String fileTypes) {
		this.fileTypes = fileTypes;
	}

	public String getResType() {
		return resType;
	}

	public void setResType(String resType) {
		this.resType = resType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDirDesc() {
		return dirDesc;
	}

	public void setDirDesc(String dirDesc) {
		this.dirDesc = dirDesc;
	}

	public String getStatusDesc() {
		return Status.getValueByKey(getStatus());
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}

	/*public String getResTypeDesc() {
		return OperDbUtils.queryNameByIndexAndKey("publishType", getResType());
	}

	public void setResTypeDesc(String resTypeDesc) {
		this.resTypeDesc = resTypeDesc;
	}*/
	public String getLifeCycleDesc() {
		return SystemConstants.LifeCycle.getValueByKey(getLifeCycle());
	}

	public void setLifeCycleDesc(String lifeCycleDesc) {
		this.lifeCycleDesc = lifeCycleDesc;
	}
	public String getLifeCycle() {
		return lifeCycle;
	}

	public void setLifeCycle(String lifeCycle) {
		this.lifeCycle = lifeCycle;
	}

	public String getResTypeNum() {
		return (String) SysResTypeCacheMap.getValue(getResType());
	}

	public void setResTypeNum(String resTypeNum) {
		this.resTypeNum = resTypeNum;
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
