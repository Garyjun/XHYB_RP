package com.brainsoon.resrelease.po;

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
import com.brainsoon.common.support.SysResTypeCacheMap;
import com.brainsoon.resrelease.support.ResReleaseConstant;
import com.brainsoon.system.model.User;
@Entity
@Table(name = "res_order")
public class ResOrder extends BaseHibernateObject {

	private static final long serialVersionUID = 5531033399989108488L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "order_id" , nullable=false)
	private Long orderId;
	
	@Column(name = "order_date")
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date orderDate;
	
	@Column(name = "channel_name")
	private String channelName;
	
	@Column(name = "STATUS")
	private String status;
	
	@ManyToOne(fetch=FetchType.EAGER) 
	@JoinColumn (name= "template_id" )
	private ProdParamsTemplate template;
	
	@Column(name = "description")
	private String description;
	
	@ManyToOne(fetch=FetchType.EAGER)   
	@JoinColumn (name= "create_user" )
	private User createUser;
	
	@Column(name = "create_time")
	//@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date createTime;
	
	@ManyToOne(fetch=FetchType.EAGER)   
	@JoinColumn (name= "update_user" )
	private User updateUser;
	
	@Column(name = "update_time")
	//@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
	
	@ManyToOne(fetch=FetchType.EAGER)   
	@JoinColumn (name= "auditor" )
	private User auditor;
	
	@Column(name = "audit_time")
	//@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date auditTime;
	
	@Column(name = "audit_remark")
	private String auditRemark;
	
	@Column(name = "batch_num")
	private String batchNum;
	
	@Column(name = "platformId")
	private int platformId;
	
	@Column(name = "moduleName")
	private String moduleName;
	
	@Column(name = "resType")
	private String resType;
	
	@Transient
	private String resTypeDesc;
	public String getResTypeDesc() {
		String map="";
		String type[] = template.getType().split(",");
		for (int i = 0; i < type.length; i++) {
			String types = type[i];
			map += (String) SysResTypeCacheMap.getValue(types)+",";			
		}
		return map.substring(0, map.length()-1);
	}

	@Transient
	private String statusDesc;
	
	/*@OneToMany(targetEntity=ResOrderDetail.class,cascade=CascadeType.ALL)
    //@Fetch(FetchMode.JOIN)
    @JoinColumn(name="order_id",updatable=false)
	private List<ResOrderDetail> resOrderDetail;*/
	
	//生成元数据excel是否成功：0:否 1:是
	@Column(name = "excel_ok_status")
	private Integer excelOkStatus = 0;
	
	//生成元数据json是否成功：0:否 1:是
	@Column(name = "json_ok_status")
	private Integer jsonOkStatus = 0;
	
	//生成元数据及文件xml是否成功：0:否 1:是
	@Column(name = "xml_ok_status")
	private Integer xmlOkStatus = 0;
	
	//备注（是否成功创建元数据Excel、资源清单Xml）
	@Column(name = "remark")
	private String remark;
	@Column(name = "processRemark")
	private String processRemark;
	
	
	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public User getAuditor() {
		return auditor;
	}

	public void setAuditor(User auditor) {
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

	public String getBatchNum() {
		return batchNum;
	}

	public void setBatchNum(String batchNum) {
		this.batchNum = batchNum;
	}
	
	public String getCreateName(){
		if(createUser==null){
			return "";
		}
		return createUser.getLoginName();
	}
	
	public String getUpdateUserName(){
		if(updateUser==null){
			return "";
		}
		return updateUser.getLoginName();
	}
	
	public String getAuditorName(){
		if(auditor==null){
			return "";
		}
		return auditor.getLoginName();
	}
	
	public String getStatusDesc() {
		return ResReleaseConstant.OrderStatus.getValueByKey(status);
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}

	/*public List<ResOrderDetail> getResOrderDetail() {
		return resOrderDetail;
	}

	public void setResOrderDetail(List<ResOrderDetail> resOrderDetail) {
		this.resOrderDetail = resOrderDetail;
	}*/

	public int getPlatformId() {
		return platformId;
	}

	public void setPlatformId(int platformId) {
		this.platformId = platformId;
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

	@Override
	public String getObjectDescription() {
		return null;
	}

	@Override
	public String getEntityDescription() {
		return null;
	}

	public Integer getExcelOkStatus() {
		return excelOkStatus;
	}

	public void setExcelOkStatus(Integer excelOkStatus) {
		this.excelOkStatus = excelOkStatus;
	}

	public Integer getXmlOkStatus() {
		return xmlOkStatus;
	}

	public void setXmlOkStatus(Integer xmlOkStatus) {
		this.xmlOkStatus = xmlOkStatus;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getJsonOkStatus() {
		return jsonOkStatus;
	}

	public void setJsonOkStatus(Integer jsonOkStatus) {
		this.jsonOkStatus = jsonOkStatus;
	}

	public String getProcessRemark() {
		return processRemark;
	}

	public void setProcessRemark(String processRemark) {
		this.processRemark = processRemark;
	}
	
	

}
