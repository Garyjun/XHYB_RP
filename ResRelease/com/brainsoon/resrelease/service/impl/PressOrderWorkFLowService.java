package com.brainsoon.resrelease.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jbpm.api.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.jbpm.constants.ProcessConstants;
import com.brainsoon.jbpm.constants.ProcessConstants.WFType;
import com.brainsoon.jbpm.service.IJbpmExcutionService;
import com.brainsoon.resource.support.WorkFlowUtils;
import com.brainsoon.resrelease.po.ResOrder;
import com.brainsoon.resrelease.po.ResOrderDetail;
import com.brainsoon.resrelease.service.IPressOrderWorkFlowService;
import com.brainsoon.resrelease.service.IResOrderService;
import com.brainsoon.resrelease.support.ResReleaseConstant;
import com.brainsoon.system.service.ISysOperateService;
import com.brainsoon.system.support.SysOperateLogUtils;
import com.brainsoon.system.support.SystemConstants;

/**
 * @ClassName: PressOrderWorkFLowService
 * @Description: 出版库流程service层实现类
 * @author xiehewei
 * @date 2014年9月11日 下午3:48:34
 *
 */
@Service
public class PressOrderWorkFLowService extends BaseService implements
		IPressOrderWorkFlowService {

	@Autowired
	private IJbpmExcutionService jbpmExcutionService;
	@Autowired
	private ISysOperateService sysOperateService;
	@Autowired
	private IResOrderService resOrderService;
	
	@Override
	public void doApply(String objectId) {
		UserInfo user = LoginUserUtil.getLoginUser();
		Long userId = 0L;
		if (user == null) {
			userId = -2L;
		} else {
			userId = user.getUserId();
		}
		ResOrder resOrder = null;
		String[] ids = StringUtils.split(objectId, ",");
		for (String objectIds : ids) {
			String wfType = WFType.PUB_ORDER_CHECK;
			String status = ResReleaseConstant.OrderStatus.TO_AUDIT;
			resOrder = (ResOrder) resOrderService.getByPk(ResOrder.class, (Long.valueOf(objectIds)));
			List<ResOrderDetail> detailList = resOrderService.getResOrderDetailByOrderId(objectIds);
			if (detailList.size()==0) {
				throw new ServiceException("上报失败：对应的资源不存在！");
			}
			if (!(resOrder.getStatus().equals(ResReleaseConstant.OrderStatus.CREATED)
					||resOrder.getStatus().equals(ResReleaseConstant.OrderStatus.AUDIT_REFUSE))) {
				throw new ServiceException("只有“未提交”和“驳回”状态的需求单才可以提交");
			}
			Map<String, String> map = getWorkFlowInfo(WorkFlowUtils.getExecuId(objectIds, "press"));
			String wfTaskId = map.get("wfTaskId");
			if (StringUtils.isBlank(wfTaskId)) {
				// 创建流程
				jbpmExcutionService.createProcessInstance(wfType, objectIds, resOrder.getChannelName(), resOrder.getResType());
			}else{
				jbpmExcutionService.endTask(wfTaskId, wfType,userId.toString(),resOrder.getResType());
			}
			// 修改状态
			updateResOrderStatus(objectIds, status,"");
			SysOperateLogUtils.addLog("presOrder_apply", resOrder.getChannelName(), user);
			//sysOperateService.saveHistory("orderCheck."+ordId, "", statusDsc, operateDesc, new Date(), LoginUserUtil.getLoginUser().getUserId());
			sysOperateService.saveHistory(wfType + "." + objectIds, "", "需求单", "上报", new Date(), userId);
		}
	}

	@Override
	public void doCheck(String objectId, String wfTaskId, String decision,
			String checkOpinion) throws Exception {
		try {
			UserInfo user = LoginUserUtil.getLoginUser();
			Long userId = 0L;
			if (user == null) {
				userId = -2L;
			} else {
				userId = user.getUserId();
			}
			ResOrder resOrder = null;
			String[] ids = StringUtils.split(objectId, ",");

			for (String objectIds : ids) {
				resOrder = (ResOrder) resOrderService.getByPk(ResOrder.class, (Long.valueOf(objectIds)));
				if (resOrder == null) {
					throw new ServiceException("操作失败：对应的资源不存在！");
				}
				
				String operateDesc = "";
				String statusDesc = "";
				String excuteId = WorkFlowUtils.getExecuId(objectIds, "press");
				String status = "";
				Task task = getCurrTask(excuteId);
				statusDesc = task.getName();
				
				//根据资源ID,找到对应的流程实例ID
				if (StringUtils.isBlank(wfTaskId)) {
					Map<String, String> map = getWorkFlowInfo(WorkFlowUtils.getExecuId(objectIds, "press"));
					wfTaskId = map.get("wfTaskId");
					if (StringUtils.isBlank(wfTaskId)) {
						throw new ServiceException("资源[" + objectIds + "]，对应的流程不存在，请确认！");
					}
				}
				
				if (decision.equals(ProcessConstants.APPROVE)) {
					jbpmExcutionService.doApprove(wfTaskId, userId.toString(),resOrder.getResType());
					operateDesc = "通过";
					status = ResReleaseConstant.OrderStatus.AUDITED;
				} else if (decision.equals(ProcessConstants.REJECT)) {
					jbpmExcutionService.doReject(wfTaskId, userId.toString(),resOrder.getResType());
					status = ResReleaseConstant.OrderStatus.AUDIT_REFUSE;
					operateDesc = "驳回";
				}
				wfTaskId = null;
				updateResOrderStatus(objectIds, status, checkOpinion);
				SysOperateLogUtils.addLog("presOrder_check", resOrder.getChannelName(), user);
				sysOperateService.saveHistory(excuteId, checkOpinion, statusDesc, operateDesc, new Date(), userId);
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
	public void doSaveAndSubmit(String objectId, String wfTaskId) {
		try {
			UserInfo user = LoginUserUtil.getLoginUser();
			Long userId = 0L;
			if (user == null) {
				userId = -2L;
			} else {
				userId = user.getUserId();
			}

			ResOrder resOrder = (ResOrder) resOrderService.getByPk(ResOrder.class, (Long.valueOf(objectId)));
			String wfType = WFType.PUB_ORDER_CHECK;
			String status = ResReleaseConstant.OrderStatus.TO_AUDIT;
			List<ResOrderDetail> list = resOrderService.getResOrderDetailByOrderId(objectId);
			if (list == null) {
				throw new ServiceException("提交失败：需求单资源不存在，无法进行上报！");
			}
			if (!resOrder.getStatus().equals(ResReleaseConstant.OrderStatus.AUDIT_REFUSE)) {
				throw new ServiceException("状态不为“已驳回”的资源不允许执行提交");
			}
			jbpmExcutionService.endTask(wfTaskId,wfType, userId.toString(),resOrder.getResType());
			updateResOrderStatus(objectId, status,"");
			SysOperateLogUtils.addLog("presOrder_saveAndSubmit", resOrder.getChannelName(), user);
			sysOperateService.saveHistory(wfType + "." + objectId, "", "需求单编辑提交", "编辑提交", new Date(), userId);
		} catch (Exception e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public void updateResOrderStatus(String objectId, String status, String checkOpinion) {
		if (StringUtils.isBlank(objectId)) {
			throw new ServiceException("更新需求单：需求单标识（objectId）为空！");
		}
		logger.info("更新需求单状态");
		String[] ids = objectId.split(",");
		ResOrder resOrder = null;
		for(String id:ids){
			resOrder = (ResOrder) resOrderService.getByPk(ResOrder.class, (Long.valueOf(id)));
			resOrder.setStatus(status);
			resOrderService.saveOrUpdate(resOrder);
		}

	}

	@Override
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
