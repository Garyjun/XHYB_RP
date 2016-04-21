package com.brainsoon.resource.po;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.Type;

import com.brainsoon.appframe.support.CustomDateSerializer;
import com.brainsoon.common.po.BaseHibernateObject;
import com.brainsoon.system.support.SystemConstants.BatchImportDetaillType;

@Entity
@Table(name = "upload_task_detail")
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class UploadTaskDetail extends BaseHibernateObject implements java.io.Serializable{

	private static final long serialVersionUID = 1805827217609256720L;

	private Long id;
	private UploadTask task;
	private String body;
	private int status;
	private int excelNum;
	private String remark;
	private Date createTime;
	private String checkRepeatField;
	private String statusZh;
	private int platformId;
	private String importStatus;
	private String paths;
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
	
	@ManyToOne(targetEntity=UploadTask.class, fetch=FetchType.LAZY, cascade={CascadeType.REMOVE})  
    @JoinColumn(name="task_id")  
	public UploadTask getTask() {
		return task;
	}

	public void setTask(UploadTask task) {
		this.task = task;
	}

    @Lob 
    @Basic(fetch = FetchType.LAZY) 
    @Type(type="text")
    @Column(name="body", nullable=true) 
	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	@Column(name="excel_num",length=32)
	public int getExcelNum() {
		return excelNum;
	}

	public void setExcelNum(int excelNum) {
		this.excelNum = excelNum;
	}

	@Column(length=250)
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	@JsonSerialize(using = CustomDateSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time", length = 23)
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	@Transient
	public String getStatusZh() {
		return BatchImportDetaillType.getValueByKey(getStatus());
	}

	public void setStatusZh(String statusZh) {
		this.statusZh = statusZh;
	}
	@Column(length=2)
	public String getCheckRepeatField() {
		return checkRepeatField;
	}

	public void setCheckRepeatField(String checkRepeatField) {
		this.checkRepeatField = checkRepeatField;
	}

	@Column(name = "platformId",length=2)
	public int getPlatformId() {
		return platformId;
	}

	public void setPlatformId(int platformId) {
		this.platformId = platformId;
	}
	@Column
	public String getImportStatus() {
		return importStatus;
	}

	public void setImportStatus(String importStatus) {
		this.importStatus = importStatus;
	}
	@Column(name = "paths",length=100)
	public String getPaths() {
		return paths;
	}

	public void setPaths(String paths) {
		this.paths = paths;
	}
	
}
