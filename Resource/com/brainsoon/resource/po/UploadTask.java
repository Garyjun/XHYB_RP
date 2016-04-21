package com.brainsoon.resource.po;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.brainsoon.common.po.BaseHibernateObject;
import com.brainsoon.system.support.SystemConstants.ImportStatus;

@Entity
@Table(name = "upload_task")
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class UploadTask extends BaseHibernateObject implements java.io.Serializable{

	private static final long serialVersionUID = 1805827217609256720L;

	private Long id;
	private String excelPath;//	文件模板Excel路径/批次路径
	private String batchNum;
	private String name;
	private String remark;
	private String libType;
	private Date createTime;
	private int status;
	private String statusDesc;
	private String module;
	private String type;
	private int platformId;
	private Date finishTime;
	private int failNum;
	private int succNum;
	private int allNum;
	private String failExcelPath;
	private String fileNotExistPath;
	private String excelName;
	private String excelDir;
	private String uuid;
	private int numIng;
	private int filetype;
	private String repeatType;//重复策略
	private String createId;//创建人
	
	@Column(name = "repeatType",length=32)
	public String getRepeatType() {
		return repeatType;
	}

	public void setRepeatType(String repeatType) {
		this.repeatType = repeatType;
	}
	@Column(name = "createId",length=32)
	public String getCreateId() {
		return createId;
	}

	public void setCreateId(String createId) {
		this.createId = createId;
	}

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	@Column(name = "excel_name",length=256)
	public String getExcelName() {
		return excelName;
	}

	public void setExcelName(String excelName) {
		this.excelName = excelName;
	}

	@Column(name = "excel_path",length=256)
	public String getExcelPath() {
		return excelPath;
	}

	public void setExcelPath(String excelPath) {
		this.excelPath = excelPath;
	}
	
	@Column(name = "file_not_exist_path",length=256)
	public String getFileNotExistPath() {
		return fileNotExistPath;
	}

	public void setFileNotExistPath(String fileNotExistPath) {
		this.fileNotExistPath = fileNotExistPath;
	}

	@Column(name = "fail_excel_path",length=256)
	public String getFailExcelPath() {
		return failExcelPath;
	}

	public void setFailExcelPath(String failExcelPath) {
		this.failExcelPath = failExcelPath;
	}

	@Column(name = "batch_num",length=50)
	public String getBatchNum() {
		return batchNum;
	}

	public void setBatchNum(String batchNum) {
		this.batchNum = batchNum;
	}

	@Column(name = "name",length=50)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time", length = 23)
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	@Column(length=32)
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Transient
	@Override
	public String getObjectDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Transient
	@Override
	public String getEntityDescription() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Column(name = "remark",length=100)
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Transient
	public String getStatusDesc() {
		return ImportStatus.getValueByKey(status);
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}
	
	@Column(name = "lib_type",length=2)
	public String getLibType() {
		return libType;
	}

	public void setLibType(String libType) {
		this.libType = libType;
	}
	@Column(name = "module",length=8)
	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}
	@Column(name = "type",length=32)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	@Column(name = "platformId",length=2)
	public int getPlatformId() {
		return platformId;
	}

	public void setPlatformId(int platformId) {
		this.platformId = platformId;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "finish_time", length = 23)
	public Date getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(Date finishTime) {
		this.finishTime = finishTime;
	}
	@Column(name = "failNum",length=11)
	public int getFailNum() {
		return failNum;
	}

	public void setFailNum(int failNum) {
		this.failNum = failNum;
	}
	@Column(name = "succNum",length=11)
	public int getSuccNum() {
		return succNum;
	}

	public void setSuccNum(int succNum) {
		this.succNum = succNum;
	}
	@Column(name = "allNum",length=11)
	public int getAllNum() {
		return allNum;
	}

	public void setAllNum(int allNum) {
		this.allNum = allNum;
	}

	@Transient
	public String getExcelDir() {
		return excelDir;
	}

	public void setExcelDir(String excelDir) {
		this.excelDir = excelDir;
	}
	@Transient
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	@Column(length=20)
	public int getNumIng() {
		return numIng;
	}

	public void setNumIng(int numIng) {
		this.numIng = numIng;
	}
	@Column(name = "file_type",length=2)
	public int getFiletype() {
		return filetype;
	}

	public void setFiletype(int filetype) {
		this.filetype = filetype;
	}
	
}
