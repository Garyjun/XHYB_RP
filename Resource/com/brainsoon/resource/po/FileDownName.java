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
import com.brainsoon.system.support.SystemConstants;
import com.brainsoon.system.support.SystemConstants.ImportStatus;

@Entity
@Table(name = "file_down_name")
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class FileDownName extends BaseHibernateObject implements java.io.Serializable{

	private static final long serialVersionUID = 1805827217609256720L;

	private Long id;
	private String resName;
	private String fileType;
	private String fileSize;
	private String status;
	private String isComplete;
	private String downloadType;
	private int downloadNum;
	private String ftpPath;
	private int downloadTime;
	private String pwd;
	private Date createTime;
	private String downloadUser;
	private String loginUser;
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	@Column(name = "res_name",length=256)
	public String getResName() {
		return resName;
	}

	public void setResName(String resName) {
		this.resName = resName;
	}

	@Column(name = "file_type",length=256)
	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	
	@Column(name = "file_size",length=256)
	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	
	
	@Column(name = "is_complete",length=256)
	public String getIsComplete() {
		return isComplete;
	}

	public void setIsComplete(String isComplete) {
		this.isComplete = isComplete;
	}

	@Column(name = "download_type",length=50)
	public String getDownloadType() {
		return downloadType;
	}

	public void setDownloadType(String downloadType) {
		this.downloadType = downloadType;
	}

	@Column(name = "download_num",length=50)
	public int getDownloadNum() {
		return downloadNum;
	}

	public void setDownloadNum(int downloadNum) {
		this.downloadNum = downloadNum;
	}

	@Column(name = "ftp_path",length=50)
	public String getFtpPath() {
		return ftpPath;
	}

	public void setFtpPath(String ftpPath) {
		this.ftpPath = ftpPath;
	}
	
	@Column(length=50)
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	@Column(name = "download_time",length=50)
	public int getDownloadTime() {
		return downloadTime;
	}

	public void setDownloadTime(int downloadTime) {
		this.downloadTime = downloadTime;
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
	@Column(name = "download_user",length=20)
	public String getDownloadUser() {
		return downloadUser;
	}

	public void setDownloadUser(String downloadUser) {
		this.downloadUser = downloadUser;
	}
	@Column(name = "login_user",length=20)
	public String getLoginUser() {
		return loginUser;
	}

	public void setLoginUser(String loginUser) {
		this.loginUser = loginUser;
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
	
}
