package com.brainsoon.jbpm.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.api.JbpmException;
import org.jbpm.api.ProcessDefinition;
import org.jbpm.api.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.brainsoon.jbpm.exception.JbpmServiceException;
import com.brainsoon.jbpm.service.IJbpmDefinitionService;


/**
 * @spring.bean id="jbpmDefinitionService"
 * <dl>
 * <dt>JbpmDefinitionService</dt>
 * <dd>Description:流程定义实现类</dd>
 */
@Service
public class JbpmDefinitionService  implements IJbpmDefinitionService {
	protected final Log logger = LogFactory.getLog(getClass());
	@Autowired
	private RepositoryService repositoryService ;
	
	/**
	 * @see com.channelsoft.qframe.workflow.jbpm.service.IJbpmDefinitionService#deployProcessArchive(java.lang.String)
	 */
	public void deployProcessArchive(String zipFileName) {
		try {
			File zipFile=new File(zipFileName);
			String processName=zipFile.getName().substring(0,zipFile.getName().lastIndexOf("."));
			if (existProDef(processName) ){
				throw new JbpmServiceException("该流程定义["+processName+"]已经部署！");
			}
		    ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFileName));
			repositoryService.createDeployment().addResourcesFromZipInputStream(zipInputStream).deploy();
			zipInputStream.close();
			
		} catch (JbpmException e){
			String errInfo = buildErrorInfo("部署流程失败：", e);
			logger.error(errInfo);
			throw new JbpmServiceException(errInfo,e);
		} catch (FileNotFoundException e){
			String errInfo = buildErrorInfo("流程存档文件不存在！", e);
			logger.error(errInfo);
			throw new JbpmServiceException(errInfo,e);
		} catch (IOException e){
			String errInfo = buildErrorInfo("部署流程失败：", e);
			logger.error(errInfo);
			throw new JbpmServiceException(errInfo,e);
		}
	}
	
	/**
	 * @see com.channelsoft.qframe.workflow.jbpm.service.IJbpmDefinitionService#updateProcessArchive(java.lang.String)
	 */
	public void updateProcessArchive(String zipFileName) {
		try {
			    ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFileName));
				repositoryService.createDeployment().addResourcesFromZipInputStream(zipInputStream).deploy();
				zipInputStream.close();
		} catch (JbpmException e){
			String errInfo = buildErrorInfo("部署流程失败：", e);
			logger.error(errInfo);
			throw new JbpmServiceException(errInfo,e);
		} catch (FileNotFoundException e){
			String errInfo = buildErrorInfo("流程存档文件不存在！", e);
			logger.error(errInfo);
			throw new JbpmServiceException(errInfo,e);
		} catch (IOException e){
			String errInfo = buildErrorInfo("部署流程失败：", e);
			logger.error(errInfo);
			throw new JbpmServiceException(errInfo,e);
		}
	}
	
	/**
	 * @see com.channelsoft.qframe.workflow.jbpm.service.IJbpmDefinitionService#getProDefById(long)
	 */
	public ProcessDefinition getProDefById(String processDefinitionId) {
		try {
			ProcessDefinition   processDefinition=null;
			List<ProcessDefinition> processDefines= repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).list();
            if(processDefines.size()>0){
            	processDefinition=processDefines.get(0);
            }
			return processDefinition;
		} catch (JbpmException e){
			String errInfo = buildErrorInfo("查询流程定义失败：", e);
			logger.error(errInfo);
			throw new JbpmServiceException(errInfo,e);
		}
	}
	
	private FileInputStream getProDefFile(String fileName) {
		try {
			return new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			String errInfo = "流程定义文件" + fileName + "不存在！";
			logger.error(errInfo);
			throw new JbpmServiceException(errInfo,e);
		}
	}
	
	/**
	 * 判断是否已存在该流程定义
	 * @return
	 */
	private  boolean existProDef( String processName) {
		boolean has=false;
		List<ProcessDefinition> processDefines=repositoryService.createProcessDefinitionQuery().processDefinitionName(processName).list();
		if(processDefines != null && processDefines.size()>0){
			has=true;
		}
		return has;
	}

	@Override
	public ProcessDefinition getProDefByName(String processName) {
		try {
			ProcessDefinition   processDefinition=null;
			List<ProcessDefinition> processDefines= repositoryService.createProcessDefinitionQuery().processDefinitionName(processName).list();
            if(processDefines.size()>0){
            	processDefinition=processDefines.get(0);
            }
			return processDefinition;
		} catch (JbpmException e){
			String errInfo = buildErrorInfo("查询流程定义失败：", e);
			logger.error(errInfo);
			throw new JbpmServiceException(errInfo,e);
		}
	}
	
	/**
	 * 删除所有流程数据，便于测试
	 */
	public void delAllProcess(){
		  List<ProcessDefinition> delList=repositoryService.createProcessDefinitionQuery().list();
		   for(ProcessDefinition p:delList)
			{
			    logger.debug("del process**** "+p.getName());
				repositoryService.deleteDeploymentCascade(p.getDeploymentId());
			}
		
	}
	
	protected String buildErrorInfo(String msg, Exception e) {
		StringBuffer error = new StringBuffer(500);
		error.append(msg).append(":");
		if (e != null) {
			error.append(e.getMessage());
		}
		return error.toString();
	}
}
