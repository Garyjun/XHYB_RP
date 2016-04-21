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
import com.brainsoon.system.support.SystemConstants;

@Entity
@Table(name = "file_down_value")
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class FileDownValue extends BaseHibernateObject implements java.io.Serializable{

	private static final long serialVersionUID = 1805827217609256720L;

	private Long id;
	private String resId;
	private FileDownName task;
	private String status;
	private String resName;
	private String ftpPath;
	private String pwd;
	private Date createTime;
	private String totalFileSize;
	private String filePath;
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	@Column(name="res_id",length=32)
	public String getResId() {
		return resId;
	}

	public void setResId(String resId) {
		this.resId = resId;
	}

	@Column(name="res_name",length=32)
	public String getResName() {
		return resName;
	}

	public void setResName(String resName) {
		this.resName = resName;
	}
	
	@Column(length=50)
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name="ftp_Path",length=32)
	public String getFtpPath() {
		return ftpPath;
	}

	public void setFtpPath(String ftpPath) {
		this.ftpPath = ftpPath;
	}

	@Column(name = "pwd",length=50)
	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	
	@Column(name = "create_time",length=50)
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name = "total_file_size",length=256)
	public String getTotalFileSize() {
		return totalFileSize;
	}

	public void setTotalFileSize(String totalFileSize) {
		this.totalFileSize = totalFileSize;
	}
	
	@Column(name = "file_path",length=500)
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
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
	
	@ManyToOne(targetEntity=FileDownName.class, fetch=FetchType.LAZY, cascade={CascadeType.REMOVE})  
    @JoinColumn(name="file_down_id")  
	public FileDownName getTask() {
		return task;
	}

	public void setTask(FileDownName task) {
		this.task = task;
	}

   
}
