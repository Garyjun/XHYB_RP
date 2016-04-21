package com.brainsoon.taskprocess.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.brainsoon.common.po.BaseHibernateObject;

/**
 *
 * @ClassName: TaskResRelation
 * @Description:
 * @author: tanghui
 * @date:2015-3-30 下午5:15:40
 */
@Entity
@Table(name = "task_res_relation")
public class TaskResRelation extends BaseHibernateObject{

	// Fields
	private static final long serialVersionUID = -1000078424294199126L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
	private Long id;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn (name= "task_detail_id" )
	private TaskDetail taskDetail;    //任务分配id

	@Column(name="sys_res_directory_id")
	private String sysResDirectoryId;  //元数据id(不是必填项)

	@Column(name="res_name")
	private String resName;  //注：如果是从系统中选择的资源则直接显示系统中的资源名称，否则显示自己添加的名称(不是必填项)

	@Column
	private Integer status;  //1-未加工、2-已加工

	@Column
	private String description;  //描述信息

	@Column
	private int platformId=1;  //平台id：1-教育 2-出版

	@Column
	private String statusDesc;//1-未领取、2-加工中、3-加工完成
	
	@Column	
	private String publishType;//资源类型

	public String getPublishType() {
		return publishType;
	}

	public void setPublishType(String publishType) {
		this.publishType = publishType;
	}

	public String getStatusDesc() {
		return statusDesc;
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}
	// Constructors

	/** default constructor */
	public TaskResRelation() {}


	public Long getId() {
		return this.id;
	}


	public void setId(Long id) {
		this.id = id;
	}



	public String getResName() {
		return this.resName;
	}


	public void setResName(String resName) {
		this.resName = resName;
	}


	public int getPlatformId() {
		return this.platformId;
	}


	public void setPlatformId(int platformId) {
		this.platformId = platformId;
	}


	public TaskDetail getTaskDetail() {
		return taskDetail;
	}


	public void setTaskDetail(TaskDetail taskDetail) {
		this.taskDetail = taskDetail;
	}


	public String getSysResDirectoryId() {
		return this.sysResDirectoryId;
	}


	public void setSysResDirectoryId(String sysResDirectoryId) {
		this.sysResDirectoryId = sysResDirectoryId;
	}


	public Integer getStatus() {
		return this.status;
	}


	public void setStatus(Integer status) {
		this.status = status;
	}


	public String getescription() {
		return this.description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	/**
	 * @return
	 * @see com.brainsoon.common.po.BaseHibernateObject#getObjectDescription()
	 */
	@Override
	public String getObjectDescription() {
		// tanghui Auto-generated method stub
		return null;
	}


	/**
	 * @return
	 * @see com.brainsoon.common.po.BaseHibernateObject#getEntityDescription()
	 */
	@Override
	public String getEntityDescription() {
		// tanghui Auto-generated method stub
		return null;
	}

}