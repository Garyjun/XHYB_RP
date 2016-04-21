package com.brainsoon.jbpm.po;

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
import org.codehaus.jackson.map.annotate.JsonSerialize;
import com.brainsoon.appframe.support.CustomDateSerializer;
import com.brainsoon.common.po.BaseHibernateObject;
import com.brainsoon.jbpm.constants.ProcessConstants.WFType;

/**
 * <dl>
 * <dt>ViewDoneTask</dt>
 * <dd>Description:工单已办任务视图</dd>
 */
@Entity
@Table(name = "view_done_task")
public class ViewDoneTask extends BaseHibernateObject  {
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
	@Transient
    private String processNameDesc;
   
    public ViewDoneTask() {
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
			taskUrl=taskUrl.replace(";and;", "&");
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

	
}
