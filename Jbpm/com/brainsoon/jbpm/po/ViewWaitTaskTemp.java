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
public class ViewWaitTaskTemp extends BaseHibernateObject  {
	private String taskId;
    private String swimName;
    private String taskName;
    private Date createDate;
    private Date endDate;
    private String processName;
    private Integer platformId;
    private String applyUser;
    private String publishType;
    private String processNameDesc;
    private String processReturnName;
    private String userName;
    private String busiDesc;
    private String bizId;
    private String taskUrl;
    private String execuId;
	private static final Logger logger = Logger.getLogger(ViewWaitTaskTemp.class);
	
	
    public ViewWaitTaskTemp(String taskId,String taskName, Date createDate,
			String processName, String userName, String publishType,String bizId,String swimName,String taskUrl,String execuId) {
		super();
		this.taskId = taskId;
		this.taskName = taskName;
		this.createDate = createDate;
		this.processName = processName;
		this.userName = userName;
		this.publishType = publishType;
		this.bizId = bizId;
		this.swimName = swimName;
		this.taskUrl = taskUrl;
		this.execuId = execuId;
		
	}

	public ViewWaitTaskTemp() {
    }
	
    public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
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

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
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

	public String getBizId() {
		return bizId;
	}

	public void setBizId(String bizId) {
		this.bizId = bizId;
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

	public String getExecuId() {
		return execuId;
	}

	public void setExecuId(String execuId) {
		this.execuId = execuId;
	}
	
}
