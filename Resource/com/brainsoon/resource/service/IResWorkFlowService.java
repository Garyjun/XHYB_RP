package com.brainsoon.resource.service;

import java.util.Map;

import com.brainsoon.common.service.IBaseService;
import com.brainsoon.semantic.ontology.model.Asset;

public interface IResWorkFlowService extends IBaseService {

	/**
	 * 上报
	 * 
	 * @param objectIds
	 */
	public void doApply(String objectIds, String libType);

	/**
	 * 审核
	 * 
	 * @param wfTaskId
	 * @param decision
	 * @param checkOpinion
	 * @param wfType TODO
	 * 
	 * @param
	 */
	public void doCheck(String objectIds, String libType, String wfTaskId, String decision, String checkOpinion) throws Exception;

	/**
	 * 保存并提交
	 * 
	 * @param wfTaskId
	 * @param wfType 流程定义标识
	 * 
	 * @param
	 */
	public void doSaveAndSubmit(String objectId, String libType, String wfTaskId);

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

}
