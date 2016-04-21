package com.brainsoon.resource.po;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.brainsoon.common.po.BaseHibernateObject;

/**
 * 服务器文件计数
 */
@Entity
@Table(name = "file_datainfo")
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class FileDataInfo extends BaseHibernateObject implements java.io.Serializable {

	private static final long serialVersionUID = 8333137724039229273L;
	// Fields

	private Integer id;
	private String resModule;
	private String resType;
	private String fileFolder;
	private Integer fileNum;
	private String note;
	// Constructors

	/** default constructor */
	public FileDataInfo() {
	}

	/** minimal constructor */
	public FileDataInfo(Integer id) {
		this.id = id;
	}

	// Property accessors
	@Id
	@GeneratedValue
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "res_module",length=3)
	public String getResModule() {
		return resModule;
	}

	public void setResModule(String resModule) {
		this.resModule = resModule;
	}

	@Column(name = "res_type",length=32)
	public String getResType() {
		return resType;
	}
	
	public void setResType(String resType) {
		this.resType = resType;
	}
	
	@Column(name = "file_folder",length=3)
	public String getFileFolder() {
		return fileFolder;
	}

	public void setFileFolder(String fileFolder) {
		this.fileFolder = fileFolder;
	}
	
	@Column(name = "file_num",length=11)
	public Integer getFileNum() {
		return fileNum;
	}

	public void setFileNum(Integer fileNum) {
		this.fileNum = fileNum;
	}
	
	@Column(name = "note",length=50)
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Transient
	@Override
	public String getObjectDescription() {
		return null;
	}
	
	@Transient
	@Override
	public String getEntityDescription() {
		return null;
	}
}