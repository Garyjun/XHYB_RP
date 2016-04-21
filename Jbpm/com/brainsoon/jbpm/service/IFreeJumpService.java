package com.brainsoon.jbpm.service;

import com.brainsoon.jbpm.exception.JbpmServiceException;

public interface IFreeJumpService {
	/**
	 * 动态跳转到下一个任务节点
	 * @param executionId
	 * @param nextTask
	 * @throws JbpmServiceException
	 */
    public void doJumpToNextTask(String executionId,String nextTask) throws JbpmServiceException;
}
