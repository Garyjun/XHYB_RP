package com.brainsoon.taskprocess.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

import com.brainsoon.common.po.BaseHibernateObject;
import com.brainsoon.system.model.User;

/**
 *
 * @ClassName: TaskDetail
 * @Description:
 * @author: tanghui
 * @date:2015-3-30 下午5:10:52
 */
@Entity
@Table(name = "task_detail")
public class TaskDetail extends BaseHibernateObject{

	// Fields
	private static final long serialVersionUID = -1099178424234199120L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
	private Long id;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn (name= "task_process_id" ) //加工任务id
	private TaskProcess taskProcess;  //加工任务对象

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn (name= "processor_id" )
	private User user;  //需从系统中选择加工员:sys_user
	
	@Transient
	private String makerName;

	public String getMakerName() {
		if(user!=null)
			return user.getUserName();
		else
			return "";
	}

	public void setMakerName(String name) {
		if(user!=null)
			makerName = user.getUserName();
		else
			makerName = "";
	}
	@Column
	private String description;  //直接从以上的任务信息的'任务说明'字段中带过来

	@Column
	private String machining;  //如：全工序、版面分析识别->纵向校对->横向校对->文字审核->生成pdf->xml标注

	@Column
	private Integer status; 
	
	private String statusDesc;//1-未领取、2-加工中、3-加工完成
	/*@Column
	private String startTimeDesc;
	@Column
	private String endTimeDesc;*/

	/*public String getStartTimeDesc() {
		return startTimeDesc;
	}

	public void setStartTimeDesc(String startTimeDesc) {
		this.startTimeDesc = startTimeDesc;
	}

	public String getEndTimeDesc() {
		return endTimeDesc;
	}

	public void setEndTimeDesc(String endTimeDesc) {
		this.endTimeDesc = endTimeDesc;
	}*/

	public String getStatusDesc() {
		return statusDesc;
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}
	@Column(name="start_time")
	private Date startTime;  //任务开始时间

	@Column(name="end_time")
	private Date endTime;  //加工结束时间

	@Column
	private Integer way;  //1-线上选择、2-线下分配

	@Column
	private int platformId=1;  //平台id：1-教育 2-出版


	// Constructors

	/** default constructor */
	public TaskDetail() {}



	public Long getId() {
		return this.id;
	}



	public void setId(Long id) {
		this.id = id;
	}



	public TaskProcess getTaskProcess() {
		return this.taskProcess;
	}



	public void setTaskProcess(TaskProcess taskProcess) {
		this.taskProcess = taskProcess;
	}



	public User getUser() {
		return this.user;
	}



	public void setUser(User user) {
		this.user = user;
	}



	public String getDescription() {
		return this.description;
	}



	public void setDescription(String description) {
		this.description = description;
	}



	public String getMachining() {
		return this.machining;
	}



	public void setMachining(String machining) {
		this.machining = machining;
	}



	public Integer getStatus() {
		return this.status;
	}



	public void setStatus(Integer status) {
		this.status = status;
	}



	public Date getStartTime() {
		return this.startTime;
	}



	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}



	public Date getEndTime() {
		return this.endTime;
	}



	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}



	public Integer getWay() {
		return this.way;
	}



	public void setWay(Integer way) {
		this.way = way;
	}



	public int getPlatformId() {
		return this.platformId;
	}



	public void setPlatformId(int platformId) {
		this.platformId = platformId;
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