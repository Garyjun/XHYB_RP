package com.brainsoon.resrelease.service;

import java.util.Map;

import com.brainsoon.common.service.IBaseService;
import com.brainsoon.resource.po.SubjectStore;

public interface IResOrderWorkFlowService extends IBaseService {

	/**
	 * 需求单上报
	 * 
	 * @param objectId
	 */
	public void doApply(String objectId,String posttype);
	/**
	 * 主题库上报
	 * 
	 * @param objectId
	 */
	public void doApplytoZtk(String objectId,String posttype);

	/**
	 * 审核
	 * 
	 * @param wfTaskId
	 * @param decision
	 * @param checkOpinion
	 * @param wfType 
	 * 
	 * @param
	 */
	public void doCheck(String objectId,String status,String checkOpinion,String wfTaskId,String decision) throws Exception;
	/**
	 * 主题库审核
	 * 
	 * @param wfTaskId
	 * @param decision
	 * @param checkOpinion
	 * @param wfType 
	 * 
	 * @param
	 */
	public void doChecktoZTK(String objectId,String status,String auditMsg,String wfTaskId,String decision) throws Exception;

	/**
	 * 需求单保存并提交
	 * 
	 * @param wfTaskId
	 * @param wfType 流程定义标识
	 * 
	 * @param
	 */
	public void doSaveAndSubmit(String objectId, String wfTaskId, String channelName, String templateId,
			String description, String orderDate,String posttype);
	/**
	 * 主题库保存并提交
	 * 
	 * @param wfTaskId
	 * @param wfType 流程定义标识
	 * 
	 * @param
	 */
	public void doSaveAndSubmittoSubject(SubjectStore stores,String wfTaskId,String posttype);

	/**
	 * 更新资源的状态：下线、恢复（审核通过）
	 * 
	 * @param objectId
	 * @param status
	 * @param checkOpinion
	 */
	public void updateResOrderStatus(String objectId, String status, String checkOpinion);
	/**
	 * 主题库更新资源的状态：下线、恢复（审核通过）
	 * 
	 * @param objectId
	 * @param status
	 * @param checkOpinion
	 */
	public void updateSubjectStatus(String objectId, String status, String checkOpinion);

	/**
	 * 获取工作流程相关的信息
	 * 
	 * @return
	 */
	public Map<String, String> getWorkFlowInfo(String execuId);
	
	public void updateResOrderStatusAndChannelName(String objectId, String status, String checkOpinion, String channelName, String templateId,
			String description, String orderDate);
	public void updateSubjectStatusAndChannelName(SubjectStore store, String status);
	public String deleteBatchResOrder(String ids,String posttype);
}
