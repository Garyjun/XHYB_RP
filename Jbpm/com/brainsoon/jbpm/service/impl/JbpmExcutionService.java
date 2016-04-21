package com.brainsoon.jbpm.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.api.ExecutionService;
import org.jbpm.api.JbpmException;
import org.jbpm.api.ProcessDefinition;
import org.jbpm.api.ProcessInstance;
import org.jbpm.api.RepositoryService;
import org.jbpm.api.TaskService;
import org.jbpm.api.task.Task;
import org.jbpm.pvm.internal.task.TaskImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.jbpm.constants.ProcessConstants;
import com.brainsoon.jbpm.exception.JbpmServiceException;
import com.brainsoon.jbpm.service.IJbpmExcutionService;
import com.mysql.jdbc.log.LogUtils;

/**
 * jbpm执行服务类
 */
@Service
public class JbpmExcutionService implements IJbpmExcutionService {
	protected final Log logger = LogFactory.getLog(getClass());
	@Autowired
	public ExecutionService executionService;
	@Autowired
	public TaskService taskService;
	@Autowired
	public RepositoryService repositoryService;

	protected String buildErrorInfo(String msg, Exception e) {
		StringBuffer error = new StringBuffer(500);
		error.append(msg).append(":");
		if (e != null) {
			error.append(e.getMessage());
		}
		return error.toString();
	}

	/**
	 * 创建流程实例
	 * 
	 * @param processName 流程名称
	 * @param busiId 业务id
	 * @return 流程实例id
	 */
	public String createProcessInstance(String processName, String busiId, String busiDesc,String publishType) throws JbpmServiceException {
		logger.debug("进入 JbpmExcutionService.createProcessInstance()");
		return createProcessInstance(processName, busiId, busiDesc, new HashMap<String, Object>(),publishType);
	}

	/**
	 * 创建流程实例并设置流程变量。
	 * 
	 * @param processName 流程名称
	 * @param busiId 业务id
	 * @param vars 流程变量
	 * @return 流程实例id
	 */
	public String createProcessInstance(String processName, String busiId, String busiDesc, Map<String, Object> vars,String publishType) throws JbpmServiceException {
		logger.debug("进入 JbpmExcutionService.createProcessInstance()");

		if (StringUtils.isBlank(processName) || StringUtils.isBlank(busiId) || StringUtils.isBlank(busiDesc)) {
			throw new JbpmServiceException("参数错误！");
		}
		try {
			List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery().processDefinitionName(processName).list();
			logger.debug("processDefinitions"+processDefinitions);
			if (processDefinitions.size() == 0) {
				throw new JbpmServiceException("流程定义名[" + processName + "]尚未部署！");
			}
			// BUSI_DESC,添加提交人信息
			UserInfo loginUser = LoginUserUtil.getLoginUser();
			logger.debug("loginUser"+loginUser);
			vars.put(ProcessConstants.BUSI_DESC, busiDesc + "_" + loginUser.getPlatformId() + "_" + loginUser.getName()+"_"+publishType);
			ProcessInstance pi = executionService.startProcessInstanceByKey(processName, vars, busiId);
			return pi.getId();
		} catch (JbpmException e) {
			String errInfo = buildErrorInfo("创建流程实例失败：", e);
			logger.error(errInfo);
			throw new JbpmServiceException(errInfo, e);
		}
	}

	/**
	 * 完成某个任务实例，并转移到流程的缺省节点。
	 * 
	 * @param taskId 任务实例id
	 * @param userId 执行人id
	 */
	public void endTask(String taskId, String userId, String publishType) throws JbpmServiceException {
		endTask(taskId, ProcessConstants.SUBMIT, userId,publishType);
	}

	/**
	 * 结束任务实例，transitionName,userId可选 多个指向时应指定transitionName 当任务未被领取直接执行时，设置先领取，比如驳回到该任务时，执行人直接为该人
	 * 
	 * @param taskId
	 * @param transitionName
	 * @param userId
	 * @return
	 */
	public void endTask(String taskId, String transitionName, String userId, String publishType) throws JbpmServiceException {
		try {
			Task taskInst = taskService.getTask(taskId);
			if(taskInst!=null){
				if (taskInst.getAssignee() == null && StringUtils.isNotBlank(userId)) {
					taskService.takeTask(taskId, userId);
				}
				UserInfo loginUser = LoginUserUtil.getLoginUser();
	//			Map<String, Object> vars = new HashMap<String, Object>();
	//			vars.put(ProcessConstants.BUSI_DESC, ProcessConstants.BUSI_DESC + "_" + loginUser.getPlatformId() + "_" + loginUser.getName()+"_"+publishType);
	//			ProcessInstance pi = executionService.startProcessInstanceByKey(transitionName, vars, busiId);
	//			
	//			
	//			Map<String, String> map = getWorkFlowInfo(ProcessConstants.WFType.PUB_ORES_CHECK + "." + objectId);
	//			String wfTaskId = map.get("wfTaskId");
				String beforeValue = executionService.getVariable(taskInst.getExecutionId(), ProcessConstants.BUSI_DESC).toString();
				executionService.setVariable(taskInst.getExecutionId(), ProcessConstants.BUSI_DESC, StringUtils.substringBeforeLast(beforeValue, "_") + "_"+publishType);
				taskService.completeTask(taskId, transitionName);
			}
			
		} catch (JbpmException e) {
			String errInfo = buildErrorInfo("完成任务实例失败：", e);
			logger.error(errInfo);
			throw new JbpmServiceException(errInfo, e);
		}
	}

	/**
	 * 任务实例是否完成
	 * 
	 * @param taskId 任务实例id
	 * @return
	 */
	public boolean taskHasEnded(String taskId) throws JbpmServiceException {
		try {
			TaskImpl task = (TaskImpl) taskService.getTask(taskId);
			return task.isCompleted();

		} catch (JbpmException e) {
			String errInfo = buildErrorInfo("判断任务实例是否完成失败：", e);
			logger.error(errInfo);
			throw new JbpmServiceException(errInfo, e);
		}
	}

	/**
	 * 审核通过
	 * 
	 * @param taskId
	 * @param userId
	 */
	public void doApprove(String taskId, String userId,String resType) throws JbpmServiceException {
		endTask(taskId, ProcessConstants.APPROVE_DESC,userId,resType);
	}

	/**
	 * 审核驳回
	 * 
	 * @param taskId
	 */
	public void doReject(String taskId, String userId,String resType) throws JbpmServiceException {
		endTask(taskId, ProcessConstants.REJECT_DESC, userId,resType);
	}

	/**
	 * 领取任务
	 * 
	 * @param taskId
	 * @param userId
	 */
	public void doTakeTask(String taskId, String userId) throws JbpmServiceException {
		taskService.takeTask(taskId, userId);
	}

	/**
	 * 结束子流程任务, 注意:该方法应该在结束子流程中最后一个节点任务之前调用，否则会报空异常
	 * 
	 * @param taskId,子流程中最后一个节点任务id
	 * @param userId
	 */
	public void endSubProcessTask(String taskId) throws JbpmServiceException {
		String subTaskId = (String) taskService.getVariable(taskId, ProcessConstants.SUB_TASKID);
		String mainTaskId = (String) taskService.getVariable(taskId, ProcessConstants.MAIN_TASKID);
		taskService.completeTask(subTaskId);
		if (taskService.getSubTasks(mainTaskId).size() == 0) {
			taskService.completeTask(mainTaskId);
		}
	}

	/**
	 * 设置业务描述
	 * 
	 * @param processName
	 * @param busiId
	 * @param busiDesc
	 * @throws JbpmServiceException
	 */
	public void doSetBusicDesc(String processName, String busiId, String busiDesc) throws JbpmServiceException {
		executionService.setVariable(processName + "." + busiId, ProcessConstants.BUSI_DESC, busiDesc);
	}

	/**
	 * 根据流程实例ID,获取当前任务
	 * 
	 * @param execuId 流程实例ID
	 * @return
	 */
	public List<Task> getCurrentTasks(String execuId) throws JbpmServiceException {
		List<Task> tasks = taskService.createTaskQuery().executionId(execuId).list();
		return tasks;
	}
}
