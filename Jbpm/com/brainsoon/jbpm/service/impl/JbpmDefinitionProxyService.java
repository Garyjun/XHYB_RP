package com.brainsoon.jbpm.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.api.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.jbpm.exception.JbpmServiceException;
import com.brainsoon.jbpm.service.IJbpmDefinitionProxyService;
import com.brainsoon.jbpm.service.IJbpmDefinitionService;


/**
 * @spring.bean id="jbpmDefinitionProxyService"
 * <dl>
 * <dt>JbpmDefinitionDeployeService</dt>
 * <dd>Description:jbpm流程定义代理服务</dd>
 */
@Service
public class JbpmDefinitionProxyService  implements IJbpmDefinitionProxyService {
	protected final Log logger = LogFactory.getLog(getClass());
	@Autowired
	private IJbpmDefinitionService jbpmDefinitionService;
	public void doDeployJpbmProcessDefinition(List<String> proDefFileList,
			String deployMode) {
		try {
			if (StringUtils.equalsIgnoreCase("delete", deployMode)) {
				jbpmDefinitionService.delAllProcess();
			}else{
				for (String proDefFile : proDefFileList) {
					if (StringUtils.equalsIgnoreCase("create", deployMode)) {
						jbpmDefinitionService.deployProcessArchive(proDefFile);
					} else if (StringUtils.equalsIgnoreCase("update", deployMode)) {
						jbpmDefinitionService.updateProcessArchive(proDefFile);
					} else {
						logger.error("不支持流程定义部署方式[" + deployMode + "]!");
						return;
					}

					logger.info("部署流程定义" + proDefFile + "成功!");
				}
			}
		} catch (JbpmServiceException e) {
			logger.error("JBPM流程定义部署失败：",e);
			throw new ServiceException( e);
		} 
	}
	
	public ProcessDefinition getProDefById(String processDefinitionId){
		try {
			ProcessDefinition processDefinition = jbpmDefinitionService.getProDefById(processDefinitionId);
			return processDefinition;
		} catch (JbpmServiceException e) {
			logger.error("查询流程定义失败：",e);
			throw new ServiceException(e);
		} 
	}
}