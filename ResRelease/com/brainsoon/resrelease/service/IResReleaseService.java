package com.brainsoon.resrelease.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import magick.MagickException;

import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.common.pagination.PageInfo;
import com.brainsoon.common.service.IBaseService;
import com.brainsoon.resrelease.po.ParamsTempEntity;
import com.brainsoon.resrelease.po.ResOrder;
import com.brainsoon.resrelease.po.ResOrderDetail;
import com.brainsoon.resrelease.po.ResRelease;
import com.brainsoon.resrelease.po.ResReleaseDetail;
import com.brainsoon.resrelease.support.ResMsg;
import com.brainsoon.semantic.ontology.model.MetaDataDC;

public interface IResReleaseService extends IBaseService {

	public List<ResRelease> getSysList();
	public void save(ResRelease resReleases);
	public PageResult queryResRelease(PageInfo pageInfo, ResRelease resReleases);
	public ResRelease queryRelReleaseByOrderId(Long orderId);
	public void updateResRelaseStatus(String orderId, String status);
	public Long saveResReleaseProcessTask ( 
			ResOrder order,
			String batchNum,
			String reasonType, 
			String reasonDesc, 
			String description);
	public void doProcess(String processTaskId);
	
	public PageResult queryPublishDetail(PageInfo pageInfo, ResReleaseDetail resReleaseDetail);
	
	public PageResult resSelectQuery(PageInfo pageInfo);
	
	public PageResult getResOrderRecordList(PageInfo pageInfo, ResReleaseDetail resReleaseDetail);
	
	public void downloadBookRes(HttpServletRequest request,HttpServletResponse response, String objectIds, String encryptPwd);
	
	public PageResult queryResReleaseDetail(PageInfo pageInfo, ResReleaseDetail resReleaseDetail, Long id);
	
	public void downloadFile(HttpServletRequest request,HttpServletResponse response, String filePath, boolean isOnLine) throws Exception;
	
	public List<ResReleaseDetail> getResReleaseDetailByCnodition(Long releaseId);
	public ResRelease getResRelease(Long id,String posttype);
	public void processResource(ResOrder resOrder, Long releaseId, String fileRoot, String header, ParamsTempEntity paramsTempEntity) throws NumberFormatException, URISyntaxException, IOException, InterruptedException;
	
	public void addDetail(ResReleaseDetail detail);
	
	public List<MetaDataDC> obtainResRelDeatail(ResOrder resOrder, List<ResOrderDetail> ordDetailList, Long relId, Long publishUserId);
	
	public List<ResOrderDetail> getAllOrderDetail(String orderId);
	
	public void saveToResConverfileTask(Map<String,ResMsg> map);
	
	public List<ResReleaseDetail> getResReleaseDetailByRelIdAndStatus(Long relId, String status);
	
	public ResReleaseDetail getResReleaseDetailByRelIdAndResId(Long relId, String resId);
	
	public void publishOffLine(ResOrder resOrder, String metaInfo, List<ResOrderDetail> detailList, List<String> metaDataList);
	
	public void publishOnLine(ResOrder resOrder, List<ResOrderDetail> detailList, List<String> fileList, String metaInfo);
	public String updateReleaseInfo(String jsons);
	public String getMaterialRes(String startTime,String endTime) throws Exception;
}
