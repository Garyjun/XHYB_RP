package com.brainsoon.jbpm.service;

import java.util.List;

import org.jbpm.api.ProcessDefinition;
import org.springframework.stereotype.Service;

/**
 * <dl>
 * <dt>IJbpmDefinitionDeployeService</dt>
 * <dd>Description:jbpm流程定义代理服务</dd>
 */

public interface IJbpmDefinitionProxyService {
	/**
	 * 部署jbpm流程定义
	 * @param proDefFileList
	 * @param deployMode
	 */
	public void doDeployJpbmProcessDefinition(List<String> proDefFileList,
			String deployMode);
	
	/**
	 * 根据流程id获取流程定义
	 * @param processDefinitionId
	 * @return
	 */
	public ProcessDefinition getProDefById(String processDefinitionId);
}