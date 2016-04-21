package com.brainsoon.resrelease.service;

import java.util.Map;

import com.brainsoon.common.service.IBaseService;

/**
 * @ClassName: IPressOrderWorkFlowService
 * @Description: 出版库流程service层接口
 * @author xiehewei
 * @date 2014年9月11日 下午3:43:40
 *
 */
public interface IPressOrderWorkFlowService extends IBaseService {

	/**
	 * 上报
	 * @param objectId
	 */
	public void doApply(String objectId);

	/**
	 * 审核
	 * @param wfTaskId
	 * @param decision
	 * @param checkOpinion
	 * @param wfType 
	 * @param
	 */
	public void doCheck(String objectId, String wfTaskId, String decision, String checkOpinion) throws Exception;

	/**
	 * 保存并提交
	 * @param wfTaskId
	 * @param wfType 流程定义标识
	 * @param
	 */
	public void doSaveAndSubmit(String objectId, String wfTaskId);

	/**
	 * 更新资源的状态
	 * @param objectId
	 * @param status
	 * @param checkOpinion
	 */
	public void updateResOrderStatus(String objectId, String status, String checkOpinion);

	/**
	 * 获取工作流程相关的信息
	 * @return
	 */
	public Map<String, String> getWorkFlowInfo(String execuId);
}
