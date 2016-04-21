package com.brainsoon.resource.service;

import java.util.Map;

import com.brainsoon.common.service.IBaseService;

public interface IPubresWorkFlowService extends IBaseService {

	/**
	 * 上报
	 * 
	 * @param objectIds
	 */
	public void doApply(String objectIds);

	/**
	 * 审核
	 * @param wfTaskId
	 * @param decision
	 * @param checkOpinion
	 * @param wfType TODO
	 * 
	 * @param
	 */
	public void doCheck(String objectIds, String wfTaskId, String decision, String checkOpinion,String url) throws Exception;

	/**
	 * 保存并提交
	 * @param wfTaskId
	 * @param wfType 流程定义标识
	 * 
	 * @param
	 */
	public void doSaveAndSubmit(String objectId, String wfTaskId,String publishType);

	/**
	 * 更新资源的状态：下线、恢复（审核通过）
	 * 
	 * @param objectId
	 * @param status
	 */
	public void updateResStatus(String objectId, String status);

	/**
	 * 获取工作流程相关的信息
	 * 
	 * @return
	 */
	public Map<String, String> getWorkFlowInfo(String execuId);

	/**
	 * 状态加解锁
	 */
	public String updateUnLock(String objectId,String status);
}
