package com.brainsoon.resrelease.po;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.po.BaseHibernateObject;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.resrelease.service.IResOrderService;
import com.brainsoon.resrelease.service.IResReleaseService;
import com.brainsoon.resrelease.support.ResReleaseConstant;
import com.brainsoon.semantic.ontology.model.File;
import com.brainsoon.semantic.ontology.model.SearchResult;
import com.google.gson.Gson;

/**
 * @ClassName: ResFileRelation
 * @Description: 资源文件关系表
 * @author xiehewei
 * @date 2015年4月9日 上午9:40:59
 *
 */
@Entity
@Table(name = "res_file_relation")
public class ResFileRelation extends BaseHibernateObject {

	private static final long serialVersionUID = 2791549557803953451L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id" , nullable=false)
	private Long id;
	
	@Column(name="res_id")
	private String resId;
	
	@Column(name="file_id")
	private String fileId;
	
	@Column(name="order_id")
	private Long orderId;
	
	//-1：待加工，1：加工成功， 0 ：加工失败
	@Column(name="process_status")
	private int processStatus = -1;
	
	@Column(name="process_remark")
	private String processRemark;
	
	
	@Column(name="publish_remark")
	private String publishRemark;

	
	//-1：待发布，1：发布成功， 0 ：发布失败
	@Column(name="publish_status")
	private int publishStatus = -1;
	
	
	//-1：待加工，1：加工成功， 0 ：加工失败
	@Column(name="process_watermark_status")
	private int processWatermarkStatus = -1;
		
		
	@Column(name="process_watermark_remark")
	private String processWatermarkRemark;
	
	@Column(name="posttype")
	private String posttype;
	
	@Column(name="restype")
	private String restype;
	
	@Transient
	private String processStatusDesc;
	
	@Transient
	private String publishStatusDesc;
	
	@Transient
	private String fileName;
	
	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getResId() {
		return resId;
	}

	public void setResId(String resId) {
		this.resId = resId;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public int getProcessStatus() {
		return processStatus;
	}

	public void setProcessStatus(int processStatus) {
		this.processStatus = processStatus;
	}
	
	public int getPublishStatus() {
		return publishStatus;
	}

	public void setPublishStatus(int publishStatus) {
		this.publishStatus = publishStatus;
	}

	public String getProcessStatusDesc() {
		int status = getProcessStatus();
		if(status == -1){
			return "待加工";
		}else{
			return ResReleaseConstant.FileProcessStatus.getValueByKey(status);
		}
	}

	public void setProcessStatusDesc(String processStatusDesc) {
		this.processStatusDesc = processStatusDesc;
	}
	
	
	public String getPublishStatusDesc() {
		if(getPublishStatus() == -1){
			return "待发布";
		}else{
			return ResReleaseConstant.ResPublishStatus.getValueByKey(getPublishStatus());
		}
	}

	public String getProcessRemark() {
		String desc = "";
		if(StringUtils.isNotBlank(processRemark)){
			desc = "<font color=\"blue\">" + processRemark + "</font>";
		}
		if(StringUtils.isNotBlank(processWatermarkRemark)){
			desc +=  "</br><font color=\"green\">" + processWatermarkRemark + "</font>";
		}
		return desc;
	}

	public void setProcessRemark(String processRemark) {
		this.processRemark = processRemark;
	}

	public String getPublishRemark() {
		return publishRemark;
	}

	public void setPublishRemark(String publishRemark) {
		this.publishRemark = publishRemark;
	}

	public void setPublishStatusDesc(String publishStatusDesc) {
		this.publishStatusDesc = publishStatusDesc;
	}

	public String getFileName() {
		HttpClientUtil http = new HttpClientUtil();
		String url = WebappConfigUtil.getParameter("PUBLISH_FILELIST_URL") + "?objectIds=" + getFileId();
		String resource = http.executeGet(url);
		Gson gson = new Gson();
		SearchResult searchResult = (SearchResult) gson.fromJson(resource, SearchResult.class);
		String name = "";
		if(searchResult!=null){
			List<File> list = searchResult.getRows();
			if(list!=null){
				System.out.println(list.get(0));
				File file = (File) list.get(0);
				name = file.getName();
				if(StringUtils.isEmpty(name)){
					name = "";
				}
			}
		}
		return name;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public String getObjectDescription() {
		return null;
	}

	@Override
	public String getEntityDescription() {
		return null;
	}
	
	public int getProcessWatermarkStatus() {
		return processWatermarkStatus;
	}

	public void setProcessWatermarkStatus(int processWatermarkStatus) {
		this.processWatermarkStatus = processWatermarkStatus;
	}

	public String getProcessWatermarkRemark() {
		return processWatermarkRemark;
	}

	public void setProcessWatermarkRemark(String processWatermarkRemark) {
		this.processWatermarkRemark = processWatermarkRemark;
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

	public String getPosttype() {
		return posttype;
	}

	public void setPosttype(String posttype) {
		this.posttype = posttype;
	}

	public String getRestype() {
		return restype;
	}

	public void setRestype(String restype) {
		this.restype = restype;
	}
	
}
