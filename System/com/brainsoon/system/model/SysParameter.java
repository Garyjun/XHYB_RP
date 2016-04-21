package com.brainsoon.system.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.brainsoon.common.po.BaseHibernateObject;
import com.brainsoon.system.support.SystemConstants.Status;
import com.brainsoon.system.support.SystemConstants.Types;

/**
 * 
 * @ClassName: SysParameter 
 * @Description:  系统参数实体类
 * @author tanghui 
 * @date 2014-5-7 上午10:33:22 
 *
 */
@Entity
@Table(name = "sys_parameter")
public class SysParameter extends BaseHibernateObject {

	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
	private Long id;
	@Column(name = "para_key")
	private String paraKey;
	@Column(name = "para_value")
	private String paraValue;
	@Column(name = "para_type")
	private Long paraType;
	@Column(name = "para_status")
	private Long paraStatus;
	//说明
	@Column(name = "para_desc")
	private String paraDesc;
	@Transient
	private String paraTypeDesc;
	@Transient
	private String paraStatusDesc;
	@Column(name = "platformId")
	private int platformId;
	// Constructors

	/** default constructor */
	public SysParameter() {
	}

	/** minimal constructor */
	public SysParameter(String paraKey) {
		this.paraKey = paraKey;
	}

	/** full constructor */
	public SysParameter(String paraKey, String paraValue) {
		this.paraKey = paraKey;
		this.paraValue = paraValue;
	}

	public int getPlatformId() {
		return platformId;
	}

	public void setPlatformId(int platformId) {
		this.platformId = platformId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getParaKey() {
		return paraKey;
	}

	public void setParaKey(String paraKey) {
		this.paraKey = paraKey;
	}

	public String getParaValue() {
		return paraValue;
	}

	public void setParaValue(String paraValue) {
		this.paraValue = paraValue;
	}

	public Long getParaType() {
		return paraType;
	}

	public void setParaType(Long paraType) {
		this.paraType = paraType;
	}

	public Long getParaStatus() {
		return paraStatus;
	}

	public void setParaStatus(Long paraStatus) {
		this.paraStatus = paraStatus;
	}

	public String getParaDesc() {
		return paraDesc;
	}

	public void setParaDesc(String paraDesc) {
		this.paraDesc = paraDesc;
	}

	public String getParaTypeDesc() {
		return Types.getValueByKey(Integer.valueOf(getParaType().toString()));
	}

	public void setParaTypeDesc(String paraTypeDesc) {
		this.paraTypeDesc = paraTypeDesc;
	}

	public String getParaStatusDesc() {
		return Status.getValueByKey(getParaStatus().toString());
	}

	public void setParaStatusDesc(String paraStatusDesc) {
		this.paraStatusDesc = paraStatusDesc;
	}

	@Override
	public String getEntityDescription() {
		// tanghui Auto-generated method stub
		return null;
	}
	
	@Override
	public String getObjectDescription() {
		// tanghui Auto-generated method stub
		return null;
	}

}