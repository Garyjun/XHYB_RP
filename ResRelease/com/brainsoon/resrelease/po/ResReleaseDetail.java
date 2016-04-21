package com.brainsoon.resrelease.po;

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

import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.po.BaseHibernateObject;
import com.brainsoon.common.support.OperDbUtils;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.resrelease.service.IResOrderService;
import com.brainsoon.resrelease.service.IResReleaseService;
import com.brainsoon.resrelease.support.ResReleaseConstant;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.system.model.User;
import com.brainsoon.system.util.MetadataSupport;
import com.google.gson.Gson;
@Entity
@Table(name = "res_release_detail")
public class ResReleaseDetail extends BaseHibernateObject  implements Comparable {

	private static final long serialVersionUID = -6517287934822834857L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="detail_id", nullable=false)
	private Long detailId;
	
	@Column(name="status")
	private String status;
	
//	@ManyToOne(fetch=FetchType.EAGER) 
	@Column (name= "release_id" )
	private Long releaseId;
	
	@ManyToOne(fetch=FetchType.EAGER) 
	@JoinColumn (name= "template_id" )
	private ProdParamsTemplate template;
	
	@Column(name="res_id")
	private String resId;
	
	@Column(name="file_type")
	private String fileType;
	
	@Column(name="remark")
	private String remark;
	
	@ManyToOne(fetch=FetchType.EAGER)   
	@JoinColumn(name="create_user_id")
	private User createUser;
	
	@Column(name="create_time")
	private Date createTime;
	
	@ManyToOne(fetch=FetchType.EAGER)   
	@JoinColumn(name="update_user_id")
	private User updateUser;
	
	@Column(name="update_time")
	private Date updateTime;
	
	@Column(name="moduleName")
	private String moduleName;
	
	@Column(name="resType")
	private String resType;
	
	@Column(name="channel_name")
	private String channelName;
	
	@Column(name="res_title")
	private String resTitle;
	
	@Column(name="process_times")
	private Long processTimes;
	
	@Column(name = "platformId")
	private int platformId;
	
	@Column(name = "pubResType")
	private String pubResType;
	
	@Column(name = "version")
	private int version;
	@Column
	private String msg;
	
	@Transient
	private String statusDesc;
	
	@Transient
	private String resTypeDesc;
	
	@Transient
	private String pubResTypeDesc;
	
	@Transient
	private String processMessage;
	
	/*@Transient
	private String publishMessage;*/
	
	@Transient
	private String resName;
	
	@Column(name = "publish_file_success")
	private int publishFileSuccess;
	
	@Column(name = "publish_file_failed")
	private int publishFileFailed;
	
	@Column(name = "process_file_success")
	private int processFileSuccess;
	
	@Column(name = "process_file_failed")
	private int processFileFailed;
	
	public Long getDetailId() {
		return detailId;
	}

	public void setDetailId(Long detailId) {
		this.detailId = detailId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ProdParamsTemplate getTemplate() {
		return template;
	}

	public void setTemplate(ProdParamsTemplate template) {
		this.template = template;
	}

	public String getResId() {
		return resId;
	}

	public void setResId(String resId) {
		this.resId = resId;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public User getCreateUser() {
		return createUser;
	}

	public void setCreateUser(User createUser) {
		this.createUser = createUser;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public User getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(User updateUser) {
		this.updateUser = updateUser;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public String getObjectDescription() {
		return null;
	}

	@Override
	public String getEntityDescription() {
		return null;
	}

	public Long getReleaseId() {
		return releaseId;
	}

	public void setReleaseId(Long releaseId) {
		this.releaseId = releaseId;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getResType() {
		return resType;
	}

	public void setResType(String resType) {
		this.resType = resType;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
	
	public String getResTitle() {
		return resTitle;
	}

	public void setResTitle(String resTitle) {
		this.resTitle = resTitle;
	}

	public Long getProcessTimes() {
		return processTimes;
	}

	public void setProcessTimes(Long processTimes) {
		this.processTimes = processTimes;
	}

	public String getStatusDesc() {
		return ResReleaseConstant.ResReleaseDetailStatus.getValueByKey(getStatus());
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}

	public String getResTypeDesc() {
		return ResReleaseConstant.ResTypeDesc.getValueByKey(getResType());
	}

	public void setResTypeDesc(String resTypeDesc) {
		this.resTypeDesc = resTypeDesc;
	}

	public int getPlatformId() {
		return platformId;
	}

	public void setPlatformId(int platformId) {
		this.platformId = platformId;
	}

	public String getPubResType() {
		return pubResType;
	}

	public void setPubResType(String pubResType) {
		this.pubResType = pubResType;
	}

	public String getPubResTypeDesc() {
		return OperDbUtils.queryNameByIndexAndKey("publishType", getPubResType());
	}

	public void setPubResTypeDesc(String pubResTypeDesc) {
		this.pubResTypeDesc = pubResTypeDesc;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	
	//加工
	public String getProcessMessage(){
		int success = 0;
		int failed = 0;
		int total = 0;
		Long releaseId = getReleaseId();
		ResRelease resRelease = (ResRelease) getResReleaseService().getByPk(ResRelease.class, releaseId);
		String resId = getResId();
		List<ResFileRelation> fileList = (List<ResFileRelation>) getResOrderService().queryFileDetail(resRelease.getOrderId(), resId,resRelease.getPosttype());
		if(fileList !=null){
			total = fileList.size();
			for(ResFileRelation rf : fileList){
				int status = rf.getProcessStatus();
				if(status == ResReleaseConstant.FileProcessStatus.FILE_PROCESS_SUCCESS){//加工成功
					success++;
				}
				if(status == ResReleaseConstant.FileProcessStatus.FILE_PROCESS_FAILED){//加工失败
					failed++;
				}
			}
		}
		return "<font color=\"blue\"><b>" + success+"</b></font>/<font color=\"red\"><b>"+failed+"</b></font>/<font color=\"black\"><b>"+total+"</b></font>";
	}
		
	//发布
	/*public String getPublishMessage() {
		int success = 0;
		int failed = 0;
		int total = 0;
		ResRelease resRelease = (ResRelease) getResReleaseService().getByPk(ResRelease.class, getReleaseId());
		//首先判断状态  如果状态为待发布或发布中 则这些数据为0
		if(ResReleaseConstant.ResReleaseStatus.WAITING_PUBLISH.equals(resRelease.getStatus()) || ResReleaseConstant.ResReleaseStatus.PUBLISHING.equals(resRelease.getStatus()) ){
			return "<font color=\"blue\"><b>0</b></font>/<font color=\"red\"><b>0</b></font>/<font color=\"black\"><b>0</b></font>";
		}
		List<ResFileRelation> fileList = (List<ResFileRelation>) getResOrderService().queryFileDetail(resRelease.getOrderId(), getResId(),resRelease.getPosttype());
		if(fileList != null){
			total = fileList.size();
			for(ResFileRelation rd : fileList){
				int status = rd.getPublishStatus();
				if(ResReleaseConstant.ResPublishStatus.RES_PUBLISH_SUCCESS == status){//发布成功
					success++;
				}
				if(ResReleaseConstant.ResPublishStatus.RES_PUBLISH_FAILED == status){//发布失败
					failed++;
				}
			}
		}
		return "<font color=\"blue\"><b>" + success+"</b></font>/<font color=\"red\"><b>"+failed+"</b></font>/<font color=\"black\"><b>"+total+"</b></font>";
	}*/
		
	public int getPublishFileSuccess() {
		return publishFileSuccess;
	}

	public void setPublishFileSuccess(int publishFileSuccess) {
		this.publishFileSuccess = publishFileSuccess;
	}

	public int getPublishFileFailed() {
		return publishFileFailed;
	}

	public void setPublishFileFailed(int publishFileFailed) {
		this.publishFileFailed = publishFileFailed;
	}

	public String getResName() {
		HttpClientUtil http = new HttpClientUtil();
		String url = WebappConfigUtil.getParameter("PUBLISH_DETAIL_URL") + "?id=" + getResId();
		String resource = http.executeGet(url);
		Gson gson = new Gson();
		Ca ca = (Ca) gson.fromJson(resource, Ca.class);
		String title = "";
		if(ca!=null){
			Map metadateMap = ca.getMetadataMap();
			String titleField = MetadataSupport.getTitleFieldName();
			if(metadateMap!=null && StringUtils.isNotBlank(titleField)){
				title = (String) metadateMap.get(titleField);
				if(StringUtils.isEmpty(title)){
					title = "";
				}
			}
		}
		return title;
	}

	public void setResName(String resName) {
		this.resName = resName;
	}

	public int getProcessFileSuccess() {
		return processFileSuccess;
	}

	public void setProcessFileSuccess(int processFileSuccess) {
		this.processFileSuccess = processFileSuccess;
	}

	public int getProcessFileFailed() {
		return processFileFailed;
	}

	public void setProcessFileFailed(int processFileFailed) {
		this.processFileFailed = processFileFailed;
	}

	private IResReleaseService getResReleaseService(){
		IResReleaseService resReleaseService = null;
		try {
			resReleaseService = (IResReleaseService) BeanFactoryUtil.getBean("resReleaseService");
		} catch (Exception e) {
			logger.debug("bean['resReleaseService']尚未装载到容器中！");
			e.printStackTrace();
		}
		return resReleaseService;
	}
	
	private IResOrderService getResOrderService(){
		IResOrderService resOrderService = null;
		try {
			resOrderService = (IResOrderService) BeanFactoryUtil.getBean("resOrderService");
		} catch (Exception e) {
			logger.debug("bean['resOrderService']尚未装载到容器中！");
			e.printStackTrace();
		}
		return resOrderService;
	}
	
	@Override
	public int compareTo(Object obj) {
		ResReleaseDetail resConverfileTask = (ResReleaseDetail)obj;
		return this.detailId.compareTo(resConverfileTask.getDetailId());
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
}
