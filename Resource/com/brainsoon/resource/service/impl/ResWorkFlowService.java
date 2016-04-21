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
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.jbpm.constants.ProcessConstants;
import com.brainsoon.jbpm.constants.ProcessConstants.WFType;
import com.brainsoon.jbpm.service.IJbpmExcutionService;
import com.brainsoon.resource.service.IBaseSemanticSerivce;
import com.brainsoon.resource.service.IResWorkFlowService;
import com.brainsoon.resource.support.WorkFlowUtils;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.ResBaseObject;
import com.brainsoon.statistics.service.IEffectNumService;
import com.brainsoon.system.model.User;
import com.brainsoon.system.service.ISysOperateService;
import com.brainsoon.system.support.SysOperateLogUtils;
import com.brainsoon.system.support.SystemConstants;
import com.brainsoon.system.support.SystemConstants.OperatType;
import com.google.gson.Gson;

@Service
public class ResWorkFlowService extends BaseService implements IResWorkFlowService {
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

	private final static SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

	@Override
	public void doApply(String objectIds, String libType) {
		logger.debug("进入doApply()方法");
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
		for (String objectId : ids) {
			ResBaseObject res = null;
			String wfType = "";
			String status = "";
			String operateKey = "";
			if (StringUtils.equals(libType, SystemConstants.LibType.ORES_TYPE)) {
				wfType = WFType.ORES_CHECK;
				if(objectId.indexOf("book")>0){
					HttpClientUtil http = new HttpClientUtil();
					Gson gson = new Gson();
					res = gson.fromJson(http.executeGet(WebappConfigUtil.getParameter("CA_DETAIL_URL") + "?id=" + objectId), Ca.class);
				}else{
					res = baseSemanticSerivce.getResourceById(objectId);
				}
				operateKey = "edu_ores_apply";
				// "待审核"
				status = SystemConstants.ResourceStatus.STATUS1;
			} else if (StringUtils.equals(libType, SystemConstants.LibType.BRES_TYPE)) {
				operateKey = "edu_bres_apply";
				wfType = WFType.BRES_CHECK;
				if(objectId.indexOf("book")>0){
					HttpClientUtil http = new HttpClientUtil();
					Gson gson = new Gson();
					res = gson.fromJson(http.executeGet(WebappConfigUtil.getParameter("CA_DETAIL_URL") + "?id=" + objectId), Ca.class);
				}else{
					res = baseSemanticSerivce.getResourceById(objectId);
				}
				// "一审待审核"
				status = SystemConstants.ResourceStatus.STATUS2;
			} else if (StringUtils.equals(libType, SystemConstants.LibType.PRES_TYPE)) {
				HttpClientUtil http = new HttpClientUtil();
				Gson gson = new Gson();
				res = gson.fromJson(http.executeGet(WebappConfigUtil.getParameter("CA_DETAIL_URL") + "?id=" + objectId), Ca.class);
				wfType = WFType.PRES_CHECK;
				operateKey = "edu_pres_apply";
				// "一审待审核"
				status = SystemConstants.ResourceStatus.STATUS2;
			}
			if (res == null) {
				throw new ServiceException("上报失败：对应的资源不存在！");
			}
			if (!(res.getCommonMetaData().getStatus().equals(SystemConstants.ResourceStatus.STATUS5) || res.getCommonMetaData().getStatus().equals(SystemConstants.ResourceStatus.STATUS0))) {
				throw new ServiceException("只有“已驳回”、“草稿”状态的资源才可以提交");
			}
			Map<String, String> map = getWorkFlowInfo(WorkFlowUtils.getExecuId(objectId, libType));
			String wfTaskId = map.get("wfTaskId");
			logger.debug("创建流程");
			if (StringUtils.isBlank(wfTaskId)) {
				// 创建流程
				jbpmExcutionService.createProcessInstance(wfType, objectId, res.getCommonMetaData().getTitle(),res.getResType());
			}else{
				jbpmExcutionService.endTask(wfTaskId,ProcessConstants.SUBMIT,userId.toString(),res.getResType());
			}
			// 修改状态
			updateResStatus(objectId, status);
			sysOperateService.saveHistory(wfType + "." + objectId, "", "资源草稿", "上报", new Date(), userId);
			SysOperateLogUtils.addLog(operateKey, res.getCommonMetaData().getTitle(), user);
		}

	}

	@Override
	public void doCheck(String objectIds, String libType, String wfTaskId, String decision, String checkOpinion) throws Exception {
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

			for (String objectId : ids) {
				ResBaseObject res = null;
				String operateKey = "";
				if (StringUtils.equals(libType, SystemConstants.LibType.ORES_TYPE)) {
					if(objectId.indexOf("book")>0){
						HttpClientUtil http = new HttpClientUtil();
						Gson gson = new Gson();
						res = gson.fromJson(http.executeGet(WebappConfigUtil.getParameter("CA_DETAIL_URL") + "?id=" + objectId), Ca.class);
					}else{
						res = baseSemanticSerivce.getResourceById(objectId);
					}
					operateKey = "edu_ores_check";
				} else if (StringUtils.equals(libType, SystemConstants.LibType.BRES_TYPE)) {
					if(objectId.indexOf("book")>0){
						HttpClientUtil http = new HttpClientUtil();
						Gson gson = new Gson();
						res = gson.fromJson(http.executeGet(WebappConfigUtil.getParameter("CA_DETAIL_URL") + "?id=" + objectId), Ca.class);
					}else{
						res = baseSemanticSerivce.getResourceById(objectId);
					}
					operateKey = "edu_bres_check";
				} else if (StringUtils.equals(libType, SystemConstants.LibType.PRES_TYPE)) {
					operateKey = "edu_pres_check";
					HttpClientUtil http = new HttpClientUtil();
					Gson gson = new Gson();
					res = gson.fromJson(http.executeGet(WebappConfigUtil.getParameter("CA_DETAIL_URL") + "?id=" + objectId), Ca.class);
				}
				if (res == null) {
					throw new ServiceException("操作失败：对应的资源不存在！");
				}

				String operateDesc = "";
				String statusDesc = "";
				String excuteId = WorkFlowUtils.getExecuId(objectId, libType);
				String status = "";
				Task task = getCurrTask(excuteId);
				statusDesc = task.getName();
				String operateType = "";

				//根据资源ID,找到对应的流程实例ID
				if (StringUtils.isBlank(wfTaskId)) {
					Map<String, String> map = getWorkFlowInfo(WorkFlowUtils.getExecuId(objectId, libType));
					wfTaskId = map.get("wfTaskId");
					if (StringUtils.isBlank(wfTaskId)) {
						throw new ServiceException("资源[" + objectIds + "]，对应的流程不存在，请确认！");
					}
				}
				
				if (decision.equals(ProcessConstants.APPROVE)) {
					jbpmExcutionService.doApprove(wfTaskId, userId.toString(),res.getResType());
					operateDesc = "通过";
					/**
					 * 1 原始资源，审核通过，置状态"已通过" 2 标准、聚合资源，审核通过，置状态"二审待审核"
					 */
					if (StringUtils.equals(libType, SystemConstants.LibType.ORES_TYPE)) {
						status = SystemConstants.ResourceStatus.STATUS3;
						operateType = OperatType.FIRST_CHECK_APPROVE;
					} else {
						if (StringUtils.equals(task.getName(), "资源一审")) {
							status = SystemConstants.ResourceStatus.STATUS6;
							operateType = OperatType.FIRST_CHECK_APPROVE;
						} else {
							status = SystemConstants.ResourceStatus.STATUS3;
							operateType = OperatType.SECOND_CHECK_APPROVE;
						}
					}

					iEffectNumService.doPiecework(userId, operateType, libType, res.getCommonMetaData().getRating(), 1);
					SysOperateLogUtils.addLog("edu_ores_access_"+libType, res.getCommonMetaData().getTitle(), user);
				} else if (decision.equals(ProcessConstants.REJECT)) {
					
					jbpmExcutionService.doReject(wfTaskId, userId.toString(),res.getResType());

					/**
					 * 1 原始资源，审核通过，置状态"已通过" 2 标准、聚合资源，审核通过，置状态"二审待审核"
					 */
					if (StringUtils.equals(libType, SystemConstants.LibType.ORES_TYPE)) {
						operateType = OperatType.FIRST_CHECK_REJECT;
					} else {
						if (StringUtils.equals(task.getName(), "资源一审")) {
							operateType = OperatType.FIRST_CHECK_REJECT;
						} else {
							operateType = OperatType.SECOND_CHECK_REJECT;
						}
					}

					status = SystemConstants.ResourceStatus.STATUS5;
					operateDesc = "驳回";
					iEffectNumService.doPiecework(userId, operateType, libType, res.getCommonMetaData().getRating(), 1);
				}
				wfTaskId = null;
				updateResStatus(objectId, status);
				sysOperateService.saveHistory(excuteId, checkOpinion, statusDesc, operateDesc, new Date(), userId);
				SysOperateLogUtils.addLog(operateKey, res.getCommonMetaData().getTitle(), user);
			}
		} catch (Exception e) {
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
	public void doSaveAndSubmit(String objectId, String libType, String wfTaskId) {
		try {
			UserInfo user = LoginUserUtil.getLoginUser();
			Long userId = 0L;
			if (user == null) {
				userId = -2L;
			} else {
				userId = user.getUserId();
			}

			ResBaseObject res = null;
			String wfType = "";
			String status = "";
			String operateKey = "";
			if (StringUtils.equals(libType, SystemConstants.LibType.ORES_TYPE)) {
				operateKey = "edu_ores_apply";
				wfType = WFType.ORES_CHECK;
				if(objectId.indexOf("book")>0){
					HttpClientUtil http = new HttpClientUtil();
					Gson gson = new Gson();
					res = gson.fromJson(http.executeGet(WebappConfigUtil.getParameter("CA_DETAIL_URL") + "?id=" + objectId), Ca.class);
				}else{
					res = baseSemanticSerivce.getResourceById(objectId);
				}
				status = SystemConstants.ResourceStatus.STATUS1;
			} else if (StringUtils.equals(libType, SystemConstants.LibType.BRES_TYPE)) {
				operateKey = "edu_bres_apply";
				wfType = WFType.BRES_CHECK;
				if(objectId.indexOf("book")>0){
					HttpClientUtil http = new HttpClientUtil();
					Gson gson = new Gson();
					res = gson.fromJson(http.executeGet(WebappConfigUtil.getParameter("CA_DETAIL_URL") + "?id=" + objectId), Ca.class);
				}else{
					res = baseSemanticSerivce.getResourceById(objectId);
				}
				status = SystemConstants.ResourceStatus.STATUS2;
			} else if (StringUtils.equals(libType, SystemConstants.LibType.PRES_TYPE)) {
				operateKey = "edu_pres_apply";
				HttpClientUtil http = new HttpClientUtil();
				Gson gson = new Gson();
				res = gson.fromJson(http.executeGet(WebappConfigUtil.getParameter("CA_DETAIL_URL") + "?id=" + objectId), Ca.class);
				wfType = WFType.PRES_CHECK;
				status = SystemConstants.ResourceStatus.STATUS2;
			}
			if (res == null) {
				throw new ServiceException("提交失败：对应的资源不存在！");
			}
			if (!res.getCommonMetaData().getStatus().equals(SystemConstants.ResourceStatus.STATUS5)) {
				throw new ServiceException("状态不为“已驳回”的资源不允许执行提交");
			}
			jbpmExcutionService.endTask(wfTaskId,ProcessConstants.SUBMIT, userId.toString(),res.getResType());
			updateResStatus(objectId, status);

			sysOperateService.saveHistory(wfType + "." + objectId, "", "资源编辑", "编辑提交", new Date(), userId);
			SysOperateLogUtils.addLog(operateKey, res.getCommonMetaData().getTitle(), user);
		} catch (Exception e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public void updateResStatus(String objectId, String status) {
		logger.debug("进入updateResStatus方法");
		if (StringUtils.isBlank(objectId)) {
			throw new ServiceException("更新资源：资源标识（objectId）为空！");
		}
		String url = WebappConfigUtil.getParameter("UPDATE_RESOURCE_STATUS_URL") + "?resourceId=" + objectId + "&status=" + status;
		logger.debug("更新："+url);
		HttpClientUtil http = new HttpClientUtil();
		String result = http.executeGet(url);
		if (StringUtils.equals(result, "-1")) {
			throw new ServiceException("更新资源：失败，对应的资源不存在！");
		}
	}

	public Map<String, String> getWorkFlowInfo(String execuId) {
		Map<String, String> map = new HashMap<String, String>();
		List<Task> taskList = jbpmExcutionService.getCurrentTasks(execuId);
		Task task = null;
		if (taskList != null && taskList.size() > 0) {
			task = taskList.get(0);
		}
		if (task != null) {
			map.put("execuId", task.getExecutionId());
			map.put("wfTaskId", task.getId());
		}
		
		return map;
	}

}
