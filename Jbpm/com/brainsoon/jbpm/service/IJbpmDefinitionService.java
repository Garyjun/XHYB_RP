package com.brainsoon.jbpm.service;

import org.jbpm.api.ProcessDefinition;


/**
 * <dl>
 * <dt>IJbpmDefinitionService</dt>
 * <dd>Description:Jbpm流程定义相关的处理接口</dd>

 */
public interface IJbpmDefinitionService {
	
	/**
	 * 使用流程存档文件，部署新的流程定义。
	 * @param zipFileName
	 */
	public void deployProcessArchive(String zipFileName);
	
	/**
	 * 使用流程存档文件，创建一个新版本的流程定义。
	 * @param zipFileName
	 */
	public void updateProcessArchive(String zipFileName);
	
	/**
	 * 根据流程名称获取流程定义
	 * @param processName
	 * @return
	 */
	public ProcessDefinition getProDefByName(String processName);
	
	/**
	 * 根据流程id获取流程定义
	 * @param processDefinitionId
	 * @return
	 */
	public ProcessDefinition getProDefById(String processDefinitionId);
	
	/**
	 * 删除所有流程数据，便于测试
	 */
	public void delAllProcess();
	
	

}
