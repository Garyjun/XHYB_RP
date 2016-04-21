package com.brainsoon.resource.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jbpm.api.TaskService;
import org.jbpm.api.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.bsrcm.search.service.ISolrQueueFacede;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.jbpm.constants.ProcessConstants;
import com.brainsoon.jbpm.constants.ProcessConstants.WFType;
import com.brainsoon.jbpm.service.IJbpmExcutionService;
import com.brainsoon.resource.service.IBaseSemanticSerivce;
import com.brainsoon.resource.service.IPubresWorkFlowService;
import com.brainsoon.resource.support.DeleteFileTaskQueue;
import com.brainsoon.resource.support.DoCheckCopyFileQueue;
import com.brainsoon.resource.support.DoCheckCopyFileThread;
import com.brainsoon.resource.support.WorkFlowUtils;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.ResBaseObject;
import com.brainsoon.statistics.service.IEffectNumService;
import com.brainsoon.system.service.ISysOperateService;
import com.brainsoon.system.service.ISysParameterService;
import com.brainsoon.system.support.SysOperateLogUtils;
import com.brainsoon.system.support.SystemConstants;
import com.brainsoon.system.support.SystemConstants.OperatType;
import com.brainsoon.system.util.MetadataSupport;
import com.google.gson.Gson;

@Service
public class PubresWorkFlowService extends BaseService implements IPubresWorkFlowService {
	@Autowired
	private IJbpmExcutionService jbpmExcutionService;
	@Autowired
	private ISysOperateService sysOperateService;
	@Autowired
	private IBaseSemanticSerivce baseSemanticSerivce;
	@Autowired
	private TaskService taskService;
	@Autowired
	private IEffectNumService iEffectNumService;
	@Autowired
	private ISysParameterService sysParameterService;

	private final static SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

	@Override
	public void doApply(String objectIds) {
		logger.debug("进入doApply()方法");

		if (StringUtils.isBlank(objectIds)) {
			logger.debug("objectIds：为空值，无法上报！");
			throw new ServiceException("上报失败：资源为空！");
		}

		Long userId = 0L;
		UserInfo user = LoginUserUtil.getLoginUser();
		if (user == null) {
			userId = -2L;
		} else {
			userId = user.getUserId();
		}

		String[] ids = StringUtils.split(objectIds, ",");
		for (String objectId : ids) {
			Ca res = getPubres(objectId);
			logger.info("传过来的id"+objectId+"返回资源resId"+res.getObjectId());
			String wfType = WFType.PUB_ORES_CHECK;
			String status = SystemConstants.ResourceStatus.STATUS1;
			if (res == null) {
				throw new ServiceException("上报失败：对应的资源不存在！");
			}
//			if (res.getMetadataMap() == null) {
//				throw new ServiceException("上报失败：对应的资源不存在！");
//			}
			if (!(res.getStatus().equals(SystemConstants.ResourceStatus.STATUS5) || res.getStatus().equals(SystemConstants.ResourceStatus.STATUS0))) {
				throw new ServiceException("只有“已驳回”、“草稿”状态的资源才可以提交");
			}
			
			Map<String, String> map = getWorkFlowInfo(ProcessConstants.WFType.PUB_ORES_CHECK + "." + objectId);
			logger.info("处理完map"+map);
			String wfTaskId = map.get("wfTaskId");
			logger.info("wfTaskId"+wfTaskId);
			if (StringUtils.isBlank(wfTaskId)) {
				logger.info("wfTaskId为空");
				jbpmExcutionService.createProcessInstance(wfType, objectId, MetadataSupport.getTitle(res),res.getPublishType());
			} else {
				logger.info("wfTaskId不为空");
				jbpmExcutionService.endTask(wfTaskId, ProcessConstants.SUBMIT,userId+"",res.getPublishType());
			}
			updateResStatus(objectId, status);
			sysOperateService.saveHistory(wfType + "." + objectId, "", "资源草稿", "上报", new Date(), userId);
			SysOperateLogUtils.addLog("pub_res_apply", MetadataSupport.getTitle(res), user);
		}

	}

	/**
	 * @param objectId
	 * @return
	 */
	public Ca getPubres(String objectId) {
		Ca res;
		HttpClientUtil http = new HttpClientUtil();
		String resourceDetail = http.executeGet(WebappConfigUtil.getParameter("PUBLISH_DETAIL_URL") + "?id=" + objectId);
		res = new Gson().fromJson(resourceDetail, Ca.class);
		return res;
	}

	@Override
	public void doSaveAndSubmit(String objectId, String wfTaskId,String publishType) {
		try {
			Long userId = 0L;
			UserInfo user = LoginUserUtil.getLoginUser();
			if (user == null) {
				userId = -2L;
			} else {
				userId = user.getUserId();
			}

			String wfType = WFType.PUB_ORES_CHECK;
			String status = SystemConstants.ResourceStatus.STATUS1;
			Ca res = getPubres(objectId);

			if (res == null) {
				throw new ServiceException("提交失败：对应的资源不存在！");
			}

			if (!res.getStatus().equals(SystemConstants.ResourceStatus.STATUS5)) {
				throw new ServiceException("状态不为“已驳回”的资源不允许执行提交");
			}
			Map<String, String> map = getWorkFlowInfo(ProcessConstants.WFType.PUB_ORES_CHECK + "." + objectId);
			wfTaskId = map.get("wfTaskId");
			jbpmExcutionService.endTask(wfTaskId,ProcessConstants.SUBMIT, String.valueOf(userId),publishType);
			updateResStatus(objectId, status);
			sysOperateService.saveHistory(wfType + "." + objectId, "", "资源编辑", "编辑提交", new Date(), userId);
			SysOperateLogUtils.addLog("pub_res_apply", MetadataSupport.getTitle(res), user);
		} catch (Exception e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public void doCheck(String objectIds, String wfTaskId, String decision, String checkOpinion,String url) throws Exception {
		Long start1 = System.currentTimeMillis();
		//fileDir目录绝对路径
		String FILE_ROOT = StringUtils.replace(WebAppUtils.getWebAppBaseFileDirFR(),"\\", "/");
		//待抽取txt文件的根目录-绝对路径
		String filesolr = StringUtils.replace(WebAppUtils.getWebRootBaseDir("filesolr"),"\\", "/");
		try {
			UserInfo user = LoginUserUtil.getLoginUser();
			Long userId = 0L;
			if (user == null) {
				userId = -2L;
			} else {
				userId = user.getUserId();
			}

			if (StringUtils.isBlank(objectIds)) {
				logger.debug("objectIds：为空值，无法上报！");
				throw new ServiceException("上报失败：资源为空！");
			}
			String[] ids = StringUtils.split(objectIds, ",");
			HttpClientUtil http = new HttpClientUtil();
			for (String objectId : ids) {
				Ca res = getPubres(objectId);
				if (res == null) {
					throw new ServiceException("操作失败：对应的资源不存在！");
				}
				String operateDesc = "";
				String statusDesc = "";
				String excuteId = ProcessConstants.WFType.PUB_ORES_CHECK + "." + objectId;
				String status = "";
				Task task = getCurrTask(excuteId);
				statusDesc = task.getName();
				String operateType = "";

				// 根据资源ID,找到对应的流程实例ID
				if (StringUtils.isBlank(wfTaskId)) {
					Map<String, String> map = getWorkFlowInfo(excuteId);
					wfTaskId = map.get("wfTaskId");
					if (StringUtils.isBlank(wfTaskId)) {
						throw new ServiceException("资源[" + objectIds + "]，对应的流程不存在，请确认！");
					}
				}
				if (decision.equals(ProcessConstants.APPROVE)) {
					jbpmExcutionService.doApprove(wfTaskId, userId.toString(),res.getPublishType());
					status = SystemConstants.ResourceStatus.STATUS3;
					operateType = OperatType.FIRST_CHECK_APPROVE;
					
					logger.debug("审核 获取流程实例用时1：" + (System.currentTimeMillis() - start1));
					Long start2 = System.currentTimeMillis();
					
					ISolrQueueFacede solrQueueFacede = (ISolrQueueFacede) BeanFactoryUtil.getBean("solrQueueFacede");
					url = url + "/changeSolrQueneStatus/updAction.action?objectId="+objectId;
					String msg = solrQueueFacede.addSolrQueue(objectId,url);
					logger.info("审核 抽文本写表----"+msg);
					
					logger.debug("审核 抽文本写表用时2：" + (System.currentTimeMillis() - start2));
					Long start3 = System.currentTimeMillis();
					
					//new Thread(new DoCheckCopyFileThread(objectId)).start();
					DoCheckCopyFileQueue.getInst().addMessage(objectId);
					/*
					String uid = objectId.substring(4, objectId.length());
					String combinePath = filesolr + "noconvert/" + uid;
					File combinePathDir = new File(combinePath);
			        if (!combinePathDir.exists()) {
			        	combinePathDir.mkdirs();
			        }
					List<com.brainsoon.semantic.ontology.model.File> realFiles = res
							.getRealFiles();
					if (realFiles != null && realFiles.size() > 0) {
						for (com.brainsoon.semantic.ontology.model.File realFile : realFiles) {
							if ("2".equals(realFile.getIsDir())) {
								String fileType = realFile.getFileType()
										.toLowerCase();
								String path = realFile.getPath();
								path = path.replaceAll("\\\\", "/");
								String absPath = FILE_ROOT + path;
								File absFile = new File(absPath);
								if (absFile.exists()) {
									String newPath = combinePath + "/"
											+ realFile.getAliasName();
									if (StringUtils.equals("pdf", fileType)) {
										if (!SolrUtil.isCanPdfToTXT(absPath)) {
											continue;
										}
										if (SolrUtil.pdfTextStripper(absPath,
												newPath)) {
											FileUtils.copyFile(absFile, new File(
													newPath));
										}
									} else if (StringUtils.equals("doc", fileType)
											|| StringUtils.equals("docx", fileType)) {
										FileUtils.copyFile(absFile, new File(
												newPath));

									} else if (StringUtils.equals("xml", fileType)) {
										if (SolrUtil.xml2Text(absPath, newPath)) {
											FileUtils.copyFile(absFile, new File(
													newPath));
										}
									} else if (StringUtils.equals("txt", fileType)) {
										FileUtils.copyFile(absFile, new File(
												newPath));
									} else {
										logger.debug("不支持该类型文件：" + fileType);
										continue;
									}
								}

							}
						}
					}*/
					
					logger.debug("审核 拷贝文件用时3：" + (System.currentTimeMillis() - start3));
					Long start4 = System.currentTimeMillis();
					
					/*//回调更新solr队列表状态url	modify 2015-12-16 09:27:39 huangjun 回调路径存在表中  转换服务扫表处理程序 不须调用fcs
					url = url + "/changeSolrQueneStatus/updAction.action?objectId="+objectId;
					String changeToTextUrl = "";
					try {
						String urlEn = URLEncoder.encode(url, "UTF-8");
					    changeToTextUrl = WebappConfigUtil.getParameter("PUBLISH_CHANGE_TO_TEXT");
						changeToTextUrl+="?url="+urlEn;
						http.executeGet(changeToTextUrl);
					} catch (Exception e) {
						logger.debug("调用抽取文本URL失败： + " + changeToTextUrl);
					}*/
					operateDesc = "通过";
					SysOperateLogUtils.addLog("pub_res_access",MetadataSupport.getTitle(res), user);
					// iEffectNumService.doPiecework(userId, operateType,
					// libType,res.getCommonMetaData().getRating(), 1);
					logger.debug("审核 流程审核用时4：" + (System.currentTimeMillis() - start4));
				} else if (decision.equals(ProcessConstants.REJECT)) {
					jbpmExcutionService.doReject(wfTaskId, userId.toString(),res.getPublishType());
					operateType = OperatType.FIRST_CHECK_REJECT;
					status = SystemConstants.ResourceStatus.STATUS5;
					operateDesc = "驳回";
					// iEffectNumService.doPiecework(userId, operateType,
					// libType, res.getCommonMetaData().getRating(), 1);
					SysOperateLogUtils.addLog("pub_res_check", MetadataSupport.getTitle(res), user);
				}
				url = WebappConfigUtil.getParameter("UPDATE_PUBLISH_STATUS_URL") + "?resourceId=" + objectId + "&status=" + status;
				String result = http.executeGet(url);
				wfTaskId = null;
////		    updateResStatus(objectId, status);
				sysOperateService.saveHistory(excuteId, checkOpinion, statusDesc, operateDesc, new Date(), userId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}

	}

	/**
	 * @param excuteId
	 * @param task
	 * @return
	 */
	public Task getCurrTask(String excuteId) {
		List<Task> tasks = jbpmExcutionService.getCurrentTasks(excuteId);
		Task task = null;
		if (tasks != null && tasks.size() > 0) {
			task = tasks.get(0);
		} else {
			throw new ServiceException("流程操作错误：任务不存在！");
		}
		return task;
	}

	@Override
	public void updateResStatus(String objectId, String status) {
		logger.info("进入updateResStatus方法");
		if (StringUtils.isBlank(objectId)) {
			throw new ServiceException("更新资源：资源标识（objectId）为空！");
		}
		Long userId = LoginUserUtil.getLoginUser().getUserId();
		String url = WebappConfigUtil.getParameter("UPDATE_PUBLISH_STATUS_URL") + "?resourceId=" + objectId + "&status=" + status;
		HttpClientUtil http = new HttpClientUtil();
		Gson gson = new Gson();
		String result = http.executeGet(url);
		String resourceDetail = http
				.executeGet(WebappConfigUtil
						.getParameter("PUBLISH_DETAIL_URL")
						+ "?id="
						+ objectId);
		Ca ca = gson.fromJson(resourceDetail, Ca.class);
		String title = MetadataSupport.getTitle(ca);
		String checkOpinion = "";
		if("4".equals(status)){
			sysOperateService.saveHistory(
					WorkFlowUtils.getExecuId(objectId, "pubresCheck"),
					checkOpinion,"资源下线" , "下线", new Date(), userId);
			SysOperateLogUtils.addLog("offline_operate",title, LoginUserUtil.getLoginUser());
			
		}
		if("3".equals(status)){
			sysOperateService.saveHistory(
					WorkFlowUtils.getExecuId(objectId, "pubresCheck"),
					checkOpinion,"资源恢复" , "恢复", new Date(), userId);
			SysOperateLogUtils.addLog("offline_reset", title, LoginUserUtil.getLoginUser());
		}
		if (StringUtils.equals(result, "-1")) {
			throw new ServiceException("更新资源：失败，对应的资源不存在！");
		}
	}

	public Map<String, String> getWorkFlowInfo(String execuId) {
		logger.info("进入处理jbpm表getWorkFlowInfo方法");
		Map<String, String> map = new HashMap<String, String>();
		List<Task> taskList = jbpmExcutionService.getCurrentTasks(execuId);
		Task task = null;
		if (taskList != null && taskList.size() > 0) {
			task = taskList.get(0);
		}
		if (task != null) {
			map.put("execuId", task.getExecutionId());
			map.put("wfTaskId", task.getId());
			logger.info("进入处理jbpm表返回wfTaskId"+task.getId());
			logger.info("进入处理jbpm表返回execuId"+task.getExecutionId());
		}

		return map;
	}

	@Override
	public String updateUnLock(String objectId,String status) {
		String stat = "0";
		try{
			String ids[]=objectId.split(",");
			String url = "";
			String title = "";
			UserInfo user = LoginUserUtil.getLoginUser();
			Long userId = 0L;
		if (user!= null) {
				userId = user.getUserId();
			}
		logger.debug("进入updateResStatus方法");
		String checkOpinion = "";
		if(status.equals("3")){
			status = "8";
			HttpClientUtil http = new HttpClientUtil();
			for(int i=0;i<ids.length;i++){
				ResBaseObject res = getPubres(ids[i]);
				url = WebappConfigUtil.getParameter("UPDATE_PUBLISH_STATUS_URL") + "?resourceId=" + ids[i]+ "&status=" + status;
				http.executeGet(url);
				title = res.getMetadataMap().get("title");
				
				SysOperateLogUtils.addLog("un_lock",
						title, user);
				sysOperateService.saveHistory(
						WorkFlowUtils.getExecuId(ids[i], "pubresCheck"),
						checkOpinion,"资源解锁" , "解锁", new Date(), userId);
			}
			stat = "1";
		}else if(status.equals("8")){
			status = "3";
			HttpClientUtil http = new HttpClientUtil();
			for(int j=0;j<ids.length;j++){
				ResBaseObject res = getPubres(ids[j]);
				url = WebappConfigUtil.getParameter("UPDATE_PUBLISH_STATUS_URL") + "?resourceId=" + ids[j] + "&status=" + status;
				http.executeGet(url);
				title = res.getMetadataMap().get("title");
				SysOperateLogUtils.addLog("go_lock",
						title, user);
				sysOperateService.saveHistory(
						WorkFlowUtils.getExecuId(ids[j], "pubresCheck"),
						checkOpinion,"资源加锁" , "加锁", new Date(), userId);
			}
			stat = "1";
		}
		logger.debug("更新："+url);
		} catch (Exception e) {
			throw new ServiceException(e.getMessage());
		}
		return stat;
	}
	
	/*public static void main (String []args){
		String url ="http://10.130.29.67:8080/bsfw20150320/changeSolrQueneStatus/updAction.action?objectId=urn:publish-113ae090-63bd-49c0-a810-9fc06096105e";
		try {
			url = URLEncoder.encode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		HttpClientUtil http = new HttpClientUtil();
		String changeToTextUrl = "http://10.130.29.124:8099/fileService/convertTxt";
		//String changeToTextUrl = WebappConfigUtil.getParameter("PUBLISH_CHANGE_TO_TEXT");
		changeToTextUrl+="?url="+url;
		http.executeGet(changeToTextUrl);
		
	}*/

}
