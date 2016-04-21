package com.brainsoon.resrelease.po;

import java.util.Date;
import java.util.List;

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

import com.brainsoon.common.po.BaseHibernateObject;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.resource.po.SubjectStore;
import com.brainsoon.resource.service.ISubjectService;
import com.brainsoon.resrelease.service.IResOrderService;
import com.brainsoon.resrelease.service.IResReleaseService;
import com.brainsoon.resrelease.support.FileUtil;
import com.brainsoon.resrelease.support.ResReleaseConstant;
import com.brainsoon.system.model.MetaDataModelGroup;
import com.brainsoon.system.model.User;
@Entity
@Table(name = "res_release")
public class ResRelease extends BaseHibernateObject {

	private static final long serialVersionUID = 3756615920295833048L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id", nullable=false)
	private Long id;
	
	@Column(name="description")
	private String description;
	
	@Column(name="reason_type")
	private String reasonType;
	
	@Column(name="reason_desc")
	private String reasonDesc;
	
	@Column(name="status")
	private String status;
	
	@Column(name="order_id")
	private Long orderId;
	
	@Column(name="channelName")
	private String channelName;
	@Column(name="batch_num")
	private String batchNum;
	
	@ManyToOne(fetch=FetchType.EAGER)   
	@JoinColumn (name= "create_user_id" )
	private User createUser;
	
	@Column(name="create_time")
	private Date createTime;
	
	@ManyToOne(fetch=FetchType.EAGER)   
	@JoinColumn(name="update_user_id")
	private User updateUser;
	
	@Column(name="update_time")
	private Date updateTime;
	
	@Column(name="auditor")
	private String auditor;
	
	@Column(name="audit_time")
	private Date auditTime;
	
	@Column(name="audit_remark")
	private String auditRemark;
	
	@Column(name="process_time")
	private Date processTime;
	
	@Column(name="ftp_url")
	private String ftpUrl;
	
	@Column(name = "platformId")
	private int platformId;
	
	@Column(name = "posttype")
	private String posttype;
	
	@Transient
	private String restypeMessage;
	@Transient
	private String processMessage;
	
	@Transient
	private String publishMessage;
	
	@Transient
	private String publishUrl;
	
	//@Transient
	//private String statusDesc;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getReasonType() {
		return reasonType;
	}

	public void setReasonType(String reasonType) {
		this.reasonType = reasonType;
	}

	public String getReasonDesc() {
		return reasonDesc;
	}

	public void setReasonDesc(String reasonDesc) {
		this.reasonDesc = reasonDesc;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getBatchNum() {
		return batchNum;
	}

	public void setBatchNum(String batchNum) {
		this.batchNum = batchNum;
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

	public String getAuditor() {
		return auditor;
	}

	public void setAuditor(String auditor) {
		this.auditor = auditor;
	}

	public Date getAuditTime() {
		return auditTime;
	}

	public void setAuditTime(Date auditTime) {
		this.auditTime = auditTime;
	}

	public String getAuditRemark() {
		return auditRemark;
	}

	public void setAuditRemark(String auditRemark) {
		this.auditRemark = auditRemark;
	}

	public Date getProcessTime() {
		return processTime;
	}

	public void setProcessTime(Date processTime) {
		this.processTime = processTime;
	}

	public String getFtpUrl() {
		return ftpUrl;
	}

	public void setFtpUrl(String ftpUrl) {
		this.ftpUrl = ftpUrl;
	}

	public String getObjectDescription() {
		return null;
	}

	public String getEntityDescription() {
		return null;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	
	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public int getPlatformId() {
		return platformId;
	}

	public void setPlatformId(int platformId) {
		this.platformId = platformId;
	}
	
	public String getPosttype() {
		return posttype;
	}

	public void setPosttype(String posttype) {
		this.posttype = posttype;
	}

	public String getRestypeMessage() {
		String restypename = "";
		if(posttype.equals("1")){
			ResOrder order = null;
			try {
				order = (ResOrder) getResOrderService().getByPk(ResOrder.class, orderId);
			} catch (Exception e) {
				e.printStackTrace();
			}
			String  restypes = order.getTemplate().getType();
			String restype[] = restypes.split(",");
			for (int i = 0; i < restype.length; i++) {
				MetaDataModelGroup dataModelGroup = (MetaDataModelGroup) getResOrderService().getByPk(MetaDataModelGroup.class, Long.decode(restype[i]));
				restypename += dataModelGroup.getTypeName()+",";
			}
			if(restypename.length()>0){
				restypename = restypename.substring(0, restypename.length()-1);
			}
		}
		if(posttype.equals("2")){
			SubjectStore store = null;
			try {
				store = (SubjectStore) getResOrderService().getByPk(SubjectStore.class, orderId);
			} catch (Exception e) {
				e.printStackTrace();
			}
			String  restypes = store.getTemplate().getType();
			String restype[] = restypes.split(",");
			for (int i = 0; i < restype.length; i++) {
				MetaDataModelGroup dataModelGroup = (MetaDataModelGroup) getResOrderService().getByPk(MetaDataModelGroup.class, Long.decode(restype[i]));
				restypename += dataModelGroup.getTypeName()+",";
			}
			if(restypename.length()>0){
				restypename = restypename.substring(0, restypename.length()-1);
			}
		}
		return restypename;
	}

	public void setRestypeMessage(String restypeMessage) {
		this.restypeMessage = restypeMessage;
	}

	//加工
	public String getProcessMessage(){
		int success = 0;
		int failed = 0;
		int total = 0;
		ResRelease release = (ResRelease) getResReleaseService().getByPk(ResRelease.class, getId());
		String posttype = release.getPosttype();
		List<ResOrderDetail> details = getResOrderService().query("from ResOrderDetail t where t.orderId="+release.getOrderId()+" and t.posttype='"+posttype+"' ");
		List<ResReleaseDetail> resList = getResReleaseService().getResReleaseDetailByCnodition(getId());
		total = details.size();
		if(resList != null && resList.size() > 0){
			for(ResReleaseDetail rd : resList){
				int status = rd.getProcessFileSuccess();
				if(ResReleaseConstant.FileProcessStatus.FILE_PROCESS_SUCCESS == status){//加工成功
					success++;
				}
				if(ResReleaseConstant.FileProcessStatus.FILE_PROCESS_FAILED == status){//加工失败
					failed++;
				}
			}
		}
		return "<font color=\"blue\"><b>" + success+"</b></font>/<font color=\"red\"><b>"+failed+"</b></font>/<font color=\"black\"><b>"+total+"</b></font>";
	}

	public void setProcessMessage(String processMessage) {
		this.processMessage = processMessage;
	}
	
	//发布
	public String getPublishMessage() {
		
		//首先判断状态  如果状态为待发布或发布中 则这些数据为0
		if(ResReleaseConstant.ResReleaseStatus.WAITING_PUBLISH.equals(status) || ResReleaseConstant.ResReleaseStatus.PUBLISHING.equals(status) ){
			return "<font color=\"blue\"><b>0</b></font>/<font color=\"red\"><b>0</b></font>/<font color=\"black\"><b>0</b></font>";
		}
		
		int success = 0;
		int failed = 0;
		int total = 0;
		ResRelease release = (ResRelease) getResReleaseService().getByPk(ResRelease.class, getId());
		String posttype = release.getPosttype();
		List<ResOrderDetail> details = getResOrderService().query("from ResOrderDetail t where t.orderId="+release.getOrderId()+" and t.posttype='"+posttype+"' ");
		List<ResReleaseDetail> resList = getResReleaseService().getResReleaseDetailByCnodition(getId());
		total = details.size();
		if(resList != null && resList.size() > 0){
			for(ResReleaseDetail rd : resList){
				String status = rd.getStatus();
				if(ResReleaseConstant.PublishingStatus.SUCCESS_PUBLISH.equals(status)){//发布成功
					success++;
				}
				if(ResReleaseConstant.PublishingStatus.FAIL_PUBLISH.equals(status)){//发布失败
					failed++;
				}
			}
		}
		return "<font color=\"blue\"><b>" + success+"</b></font>/<font color=\"red\"><b>"+failed+"</b></font>/<font color=\"black\"><b>"+total+"</b></font>";
	}

	public void setPublishMessage(String publishMessage) {
		this.publishMessage = publishMessage;
	}

	public String getPublishUrl() {
		String path = "";
		try {
			String paths = WebAppUtils.getWebRootBaseDir(ConstantsDef.prodFile).replaceAll("\\\\", "\\/");
			if(posttype.equals("1")){
				ResOrder order = (ResOrder) getResOrderService().getByPk(ResOrder.class, orderId);
				if(order.getTemplate().getPublishType().equals("offLine")){
					path = com.brainsoon.resrelease.support.FileUtil.getPublishPath(order);//源文件发布路径
					path = path.replace(paths, "");
					//发布路径= ../fileDir/prodFile/{资源类型}/offLine(onLine)/{当前时间}/{需求单编号} or{{需求单名称}}/
					//只展示     /offLine(onLine)/{当前时间}/{需求单编号} or{{需求单名称}}/
					/*String resType = order.getTemplate().getType();//资源类型ID
					String restype = "";
					//若是用户在数据字典里面配置了{资源类型}  则替换路径
			    	IDictNameService dictNameService = (IDictNameService)BeanFactoryUtil.getBean("dictNameService");
			    	LinkedHashMap<String,String> map = new LinkedHashMap<String, String>();
					map = dictNameService.getDictMapByName("模板导入资源类型目录");
					if(!map.isEmpty() && map.get(resType)!=null){
						restype = map.get(resType);
					}
					int index = path.indexOf("/"+restype+"/");
					path = path.substring(index + restype.length() + 1 );*/
				}
				if(order.getTemplate().getPublishType().equals("onLine")){
					path = FileUtil.getPublishPathsxqd(order);
					path = path.replace(paths, "");
				}
			}else if(posttype.equals("2")){
				SubjectStore store = (SubjectStore) getSubjectService().getByPk(SubjectStore.class, orderId);
				path = FileUtil.getPublishPathsztk(store);
				path = path.replace(paths, "");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return path;
	}

	public void setPublishUrl(String publishUrl) {
		this.publishUrl = publishUrl;
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
	private ISubjectService getSubjectService(){
		ISubjectService subjectService = null;
		try {
			subjectService = (ISubjectService) BeanFactoryUtil.getBean("subjectService");
		} catch (Exception e) {
			logger.debug("bean['subjectService']尚未装载到容器中！");
			e.printStackTrace();
		}
		return subjectService;
	}
}
