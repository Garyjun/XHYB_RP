package com.brainsoon.resource.po;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.brainsoon.common.po.BaseHibernateObject;
import com.brainsoon.system.support.SystemConstants.ImportStatus;

/**
 * 批量导入结果
 */
@Entity
@Table(name = "copyright_import_result")
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class CopyrightImportResult extends BaseHibernateObject implements java.io.Serializable {

	// Fields

	private static final long serialVersionUID = -1056178654494091926L;
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
	private Integer id;
	@Column(name = "excelName",length=128)
	private String excelName;
	@Column(name = "status",length=2)
	private Integer status;
	@Column(name = "type",length=2)
	private String type;
	@Column(name = "virtualName",length=64)
	private String virtualName;
	@Column(name="importTime")
	private Date importTime;
	// Constructors

	/** default constructor */
	public CopyrightImportResult() {
	}

	/** minimal constructor */
	public CopyrightImportResult(Integer id) {
		this.id = id;
	}


	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getExcelName() {
		return excelName;
	}

	public void setExcelName(String excelName) {
		this.excelName = excelName;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getVirtualName() {
		return virtualName;
	}

	public void setVirtualName(String virtualName) {
		this.virtualName = virtualName;
	}

	public Date getImportTime() {
		return importTime;
	}

	public void setImportTime(Date importTime) {
		this.importTime = importTime;
	}
	
	public String getStatusDesc() {
		return ImportStatus.getValueByKey(status);
	}

	@Override
	public String getObjectDescription() {
		// TODO Auto-generated method stub
		return "版权批量导入日志";
	}
	@Override
	public String getEntityDescription() {
		// TODO Auto-generated method stub
		return "版权批量导入日志";
	}
	
}