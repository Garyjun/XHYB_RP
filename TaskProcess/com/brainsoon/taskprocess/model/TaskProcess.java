package com.brainsoon.taskprocess.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

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

import org.apache.commons.lang.StringUtils;

import com.brainsoon.common.po.BaseHibernateObject;
import com.brainsoon.common.support.OperDbUtils;
import com.brainsoon.common.support.SysResTypeCacheMap;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.system.model.User;
import com.brainsoon.taskprocess.service.ITaskProcessService;

/**
 *
 * @ClassName: TaskProcess
 * @Description:
 * @author: tanghui
 * @date:2015-3-30 下午4:55:49
 */
@Entity
@Table(name = "task_process")
public class TaskProcess extends BaseHibernateObject{

	// Fields
	private static final long serialVersionUID = -1099178424294199126L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
	private Long id;

	@Column(name="taskName",length=200)
	private String taskName;     //任务名称

	@Column(name="batch_number",length=100)
	private String batchNumber;    //加工批次

	@Column(name="process_number")
	private Integer processNumber;  //加工数量

	@Column(name="person_number")
	private Integer personNumber;  //建议加工人数

	@Column(name="description")
	private String description;  //任务说明:基本要求,特殊要求,加工说明

	@Column(name="status")
	private Integer status = 1;  //任务状态:1-新建、2-待审核、3-加工中

	@Column(name="start_time")
	private Date startTime;  //预计开始时间
	@Column
	private String startTimeDesc;

	@Column(name="end_time")
	private Date endTime;  //要求完成时间
	@Column
	private String endTimeDesc;

	public String getStartTimeDesc() {
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
	}

	@Column(name="priority")
	private Integer priority = 2;  //1-高、2-中、3-低

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="create_user")
	private User createUser;//创建人

	@Column(name="create_time")
	private Date createTime;//创建时间

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="update_user")
	private User updateUser;//修改人

	@Column(name="update_time")
	private Date updateTime;//修改时间

	@Column(name="platformId")
	private int platformId;  //平台id：1-教育 2-出版

	@Column(name="resType")
	private String resType; // 资源类型
	
	@Transient
	private String processorNameDesc;//加工员名称
	
	@Transient
	private int resNumDesc;//加工数量
	
	@Transient
	private String processStatusDesc;//加工状态

	// Constructors

	/** default constructor */
	public TaskProcess() {}


	public Long getId() {
		return this.id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getTaskName() {
		return this.taskName;
	}


	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}


	public String getBatchNumber() {
		return this.batchNumber;
	}


	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}


	public Integer getProcessNumber() {
		return this.processNumber;
	}


	public void setProcessNumber(Integer processNumber) {
		this.processNumber = processNumber;
	}


	public Integer getPersonNumber() {
		return this.personNumber;
	}


	public void setPersonNumber(Integer personNumber) {
		this.personNumber = personNumber;
	}


	public String getDescription() {
		return this.description;
	}


	public void setDescription(String description) {
		this.description = description;
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


	public Integer getPriority() {
		return this.priority;
	}


	public void setPriority(Integer priority) {
		this.priority = priority;
	}



	public Date getCreateTime() {
		return this.createTime;
	}


	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}



	public Date getUpdateTime() {
		return this.updateTime;
	}


	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	public User getCreateUser() {
		return this.createUser;
	}


	public void setCreateUser(User createUser) {
		this.createUser = createUser;
	}


	public User getUpdateUser() {
		return this.updateUser;
	}


	public void setUpdateUser(User updateUser) {
		this.updateUser = updateUser;
	}


	public int getPlatformId() {
		return this.platformId;
	}


	public void setPlatformId(int platformId) {
		this.platformId = platformId;
	}

	
	public String getResType() {
		return resType;
	}


	public void setResType(String resType) {
		this.resType = resType;
	}


	public String getStatusDesc() {
		return OperDbUtils.queryValueByKey("taskProcessStatus").get(getStatus()+"");
	}


	public String getPriorityDesc() {
		return  OperDbUtils.queryValueByKey("priority").get(getPriority()+"");
	}

	public String getResTypeDesc() {
		Map<Object, Object> resTypeMap = SysResTypeCacheMap.getMapValue(); 
		String resTypeDesc = (String) resTypeMap.get(getResType());
		return resTypeDesc;
	}
	
	public String getProcessorNameDesc() {
		List<TaskProcessorRelation>  processorList = getTaskProcessService().getTaskProcessorRelationByTaskId(id);
		String names = "";
		if(processorList!=null){
			for(TaskProcessorRelation processor : processorList){
				names += processor.getProcessor().getUserName() + ",";
			}
			if(StringUtils.isNotEmpty(names)){
				names = names.substring(0, names.length()-1);
			}
		}
		return names;
	}

	public int getResNumDesc() {
		List<TaskDetail> taskDetailList = getTaskProcessService().getTaskDetailByTaskIdAndStatus(id, null);
		int resNum = 0;
		if(taskDetailList!=null){
			resNum = taskDetailList.size();
		}
		return resNum;
	}

	
	public String getProcessStatusDesc() {
		logger.info("-----------------进入getProcessStatusDesc方法---------------");
		List<TaskDetail> allList = getTaskProcessService().getTaskDetailByTaskIdAndStatus(id, null);
		logger.info("-----------------打印allList--"+allList.size()+"-------------");
		List<TaskDetail> processedList = getTaskProcessService().getTaskDetailByTaskIdAndStatus(id, 2);
		logger.info("-----------------打印processedList--"+processedList.size()+"-------------");
		//String allProcess = "全部完成";
		//String partProcess = "部分完成";
		//String noProcess = "未加工";
		String processResultDesc = "";
		if(allList!=null){
			if(processedList==null){
				logger.info("-----------------判断if1---------------");
				processResultDesc = "未加工";
			}else{
				logger.info("-----------------判断if2---------------");
				if(allList.size()>processedList.size()){
					processResultDesc = "部分完成";
				}else{
					processResultDesc = "全部完成";
				}
			}
		}
		logger.info("-----------------打印processResultDesc--"+processResultDesc+"-------------");
		return processResultDesc;
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

	private ITaskProcessService getTaskProcessService(){
		ITaskProcessService taskProcessService = null;
		try {
			taskProcessService = (ITaskProcessService) BeanFactoryUtil.getBean("taskProcessService");
		} catch (Exception e) {
			logger.debug("bean['taskProcessService']尚未装载到容器中！");
			e.printStackTrace();
		}
		return taskProcessService;
	}

}