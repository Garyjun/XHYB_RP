package com.brainsoon.statistics.po;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.brainsoon.common.po.BaseHibernateObject;
/**
 * 资源下文件的实体类
 * @author 冯达
 * 
 *
 */
@Entity
@Table(name = "sys_resource_word_file")
public class RespsOfResourceWordFile extends BaseHibernateObject{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false)
	private Long id;
	
	@Column(name = "file_id")
	private String fileId;    //文件id
	
	@Column(name = "resource_id")
	private String resourceId;   //资源id
	
	@Column(name ="file_path")
	private String filePath;   //资源下对应的文件名称（加上路径显示）
	
	@Column
	private String word;    //包含的敏感词
	
	@Column(name = "file_name")
	private String fileName;   //文件类型的扩展名
	
	@Column(name="update_time")
	private String updateTime;  //文件最后过滤时间
	
	@Column(name="file_real_path")
	private String fileRealPath;  //文件相对路径
	
	public String getFileRealPath() {
		return fileRealPath;
	}

	public void setFileRealPath(String fileRealPath) {
		this.fileRealPath = fileRealPath;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
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
