package com.brainsoon.jbpm.service.impl;

import org.jbpm.api.Execution;
import org.jbpm.api.ExecutionService;
import org.jbpm.api.RepositoryService;
import org.jbpm.pvm.internal.model.ProcessDefinitionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brainsoon.jbpm.exception.JbpmServiceException;
import com.brainsoon.jbpm.service.IFreeJumpService;
import com.brainsoon.jbpm.utils.FreeTransUtil;
@Service
public class FreeJumpService implements IFreeJumpService {
	@Autowired
	private ExecutionService executionService ;
	@Autowired
	RepositoryService repositoryService;
	/**
	 * 动态跳转到下一个任务节点
	 * @param executionId
	 * @param nextTask
	 * @throws JbpmServiceException
	 */
    public void doJumpToNextTask(String executionId,String nextTask) throws JbpmServiceException{
    	Execution execution = executionService.findExecutionById(executionId);
		String currentTask = execution.findActiveActivityNames().iterator().next();
		ProcessDefinitionImpl pd =(ProcessDefinitionImpl) repositoryService.createProcessDefinitionQuery()
				.processDefinitionId(execution.getProcessDefinitionId())
				.uniqueResult();
		FreeTransUtil.addOutTransition(pd,currentTask, nextTask);
		executionService.signalExecutionById(executionId, nextTask);
		FreeTransUtil.removeOutTransition(pd,currentTask, nextTask);
   }
}
