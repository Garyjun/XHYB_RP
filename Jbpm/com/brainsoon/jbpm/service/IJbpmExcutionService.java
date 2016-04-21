package com.brainsoon.jbpm.service;
import java.util.List;
import java.util.Map;

import org.jbpm.api.task.Task;

import com.brainsoon.jbpm.exception.JbpmServiceException;
public interface IJbpmExcutionService {
	/**
	 * 创建流程实例
	 * @param processName 流程名称
	 * @param busiId 业务id
	 * @param busiDesc 业务描述
	 * @return 流程实例id
	 */
	public String createProcessInstance(String processName, String busiId, String busiDesc,String publishType) throws JbpmServiceException;
	
	/**
	 * 创建流程实例并设置流程变量。
	 * @param processName 流程名称
	 * @param busiId 业务id
	 * @param busiDesc TODO
	 * @param vars 流程变量
	 * @return 流程实例id
	 */
	public String createProcessInstance(String processName, String busiId, String busiDesc, Map<String, Object> vars,String publishType) throws JbpmServiceException;
	
	/**
	 * 结束任务实例，transitionName,userId可选 多个指向时应指定transitionName
	 * 当任务未被领取直接执行时，设置先领取，比如驳回到该任务时，执行人直接为该人
	 * 
	 * @param taskId
	 * @param transitionName
	 * @param userId
	 * @return
	 */
	public void endTask(String taskId, String transitionName,String userId,String publishType) throws JbpmServiceException ;
	
	
	/**
	 * 通过
	 * @param taskId
	 * @param userId 
	 */
	public void doApprove(String taskId, String userId,String publishType) throws JbpmServiceException;
	
	/**
	 * 驳回
	 * @param taskId
	 * @param userId 
	 */
	public void doReject(String taskId, String userId,String resType) throws JbpmServiceException;
	
	/**
	 * 任务实例是否完成
	 * @param taskId 任务实例id
	 * @return
	 */
	public boolean taskHasEnded(String taskId) throws JbpmServiceException;
	
	/**
	 * 领取任务 
	 * @param taskId
	 * @param userId
	 */
    public void doTakeTask(String taskId,String userId) throws JbpmServiceException;
    
    /**
	 * 结束子流程任务
	 * 注意:该方法应该在结束子流程中最后一个节点任务之前调用，否则会报空异常
	 * @param taskId,子流程中最后一个节点任务id
	 * @param userId
	 */
    public void endSubProcessTask(String taskId) throws JbpmServiceException;
    
    /**
     * 设置业务描述
     * @param processName
     * @param busiId
     * @param busiDesc
     * @throws JbpmServiceException
     */
    public void doSetBusicDesc(String processName, String busiId,String busiDesc ) throws JbpmServiceException;
    
	public List<Task> getCurrentTasks(String excuteId) throws JbpmServiceException;
	
    
	
}
