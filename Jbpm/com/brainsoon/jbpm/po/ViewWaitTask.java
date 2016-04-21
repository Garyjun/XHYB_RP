package com.brainsoon.jbpm.po;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.brainsoon.appframe.support.CustomDateSerializer;
import com.brainsoon.common.po.BaseHibernateObject;
import com.brainsoon.jbpm.constants.ProcessConstants.WFType;
import com.brainsoon.resource.util.ResUtils;
import com.brainsoon.system.util.MetadataSupport;

/**
 * <dl>
 * <dt>ViewWaitTask</dt>
 * <dd>Description:工单待办任务视图</dd>
 */
@Entity
@Table(name = "view_wait_task")
public class ViewWaitTask extends BaseHibernateObject  {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="task_id",nullable = false)
    private String taskId;
	@Column(name="actor_id")
    private String actorId;
	@Column(name="swim_Name")
    private String swimName;
	@Column(name="task_name")
    private String taskName;
	@Column(name="execu_id")
    private String execuId;
	@Column(name="create_date")
    private Date createDate;
	@Column(name="end_date")
    private Date endDate;
	@Column(name="biz_id")
    private String bizId;
	@Column(name="task_url")
    private String taskUrl;
	@Column(name="process_name")
    private String processName;
	@Column(name="busi_desc")
    private String busiDesc;
	@Column(name="platform_id")
    private Integer platformId;
	@Column(name="apply_user")
    private String applyUser;
	@Column(name="publishType")
    private String publishType;
	@Transient
    private String processNameDesc;
	@Transient
    private String processReturnName;
	@Transient
    private String userName;
	private static final Logger logger = Logger.getLogger(ViewWaitTask.class);
    public ViewWaitTask() {
    }
	
    public String getTaskId() {
        return taskId;
    }
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
    public String getActorId() {
        return this.actorId;
    }
    public void setActorId(String actorId) {
        this.actorId = actorId;
    }
    public String getTaskName() {
        return this.taskName;
    }
    public void setTaskName(String taskName) {
    	
        this.taskName = taskName;
    }
    @JsonSerialize(using = CustomDateSerializer.class)
    public Date getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
    
    public Date getEndDate() {
        return this.endDate;
    }
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
    public String toString() {
		return new ToStringBuilder(this).append("taskId", getTaskId()).append(
				"actorId", getActorId()).toString();
	}
	public boolean equals(Object other) {
		if (!(other instanceof ViewWaitTask))
			return false;
		ViewWaitTask castOther = (ViewWaitTask) other;
		return new EqualsBuilder().append(this.getTaskId(),
				castOther.getTaskId()).append(this.getActorId(),
				castOther.getActorId()).append(this.getSwimName(),
				castOther.getSwimName()).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(getTaskId()).append(getActorId())
				.append(getSwimName()).toHashCode();
	}

	@Override
	public String getEntityDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getObjectDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSwimName() {
		return swimName;
	}

	public void setSwimName(String swimName) {
		this.swimName = swimName;
	}

	public String getTaskUrl() {
		if(StringUtils.isNotBlank(taskUrl)){
			taskUrl=taskUrl.replaceAll(";and;", "&");
			taskUrl=taskUrl.replace("${bizId}", bizId);
		}
		return taskUrl;
	}
	public void setTaskUrl(String taskUrl) {
		this.taskUrl = taskUrl;
	}

	public String getBizId() {
		return bizId;
	}

	public void setBizId(String bizId) {
		this.bizId = bizId;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String getExecuId() {
		return execuId;
	}

	public void setExecuId(String execuId) {
		this.execuId = execuId;
	}

	public String getProcessNameDesc() {
		processNameDesc=WFType.getValueByKey(getProcessName());
		return processNameDesc;
	}

	public void setProcessNameDesc(String processNameDesc) {
		this.processNameDesc = processNameDesc;
	}

	public String getBusiDesc() {
		return busiDesc;
	}

	public void setBusiDesc(String busiDesc) {
		this.busiDesc = busiDesc;
	}

	public String getDesc() {
		return StringUtils.substringBeforeLast(StringUtils.substringBeforeLast(getBusiDesc(), "_"), "_");
	}



	public String getProcessReturnName() {
		if(getSwimName().equals("WF_pubresCheck")||getSwimName().equals("WF_pubresModify")){
			processReturnName = MetadataSupport.getTitle(ResUtils.returnCa(getBizId()+""));
		}else if(getSwimName().equals("WF_subjectCheck")||getSwimName().equals("WF_subjectModify")){
			processReturnName = MetadataSupport.getTitleBySubject(getBizId()+"");
		}else{
			processReturnName = MetadataSupport.getTitleByOrder(getBizId()+"");
		}
		
		logger.info("-------------------------------"+processReturnName+"------"+getBizId()+"-------------------------------------");
		return processReturnName;
	}

	public void setProcessReturnName(String processReturnName) {
		this.processReturnName = processReturnName;
	}

	public void setApplyUser(String applyUser) {
		this.applyUser = applyUser;
	}

	public String getApplyUser() {
		return applyUser;
	}

	public Integer getPlatformId() {
		return platformId;
	}

	public void setPlatformId(Integer platformId) {
		this.platformId = platformId;
	}

	public String getPublishType() {
		return publishType;
	}

	public void setPublishType(String publishType) {
		this.publishType = publishType;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}
