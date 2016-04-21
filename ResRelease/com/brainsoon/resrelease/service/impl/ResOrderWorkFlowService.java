package com.brainsoon.resrelease.service.impl;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
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
import com.brainsoon.resource.po.SubjectStore;
import com.brainsoon.resource.service.ISubjectService;
import com.brainsoon.resource.support.WorkFlowUtils;
import com.brainsoon.resrelease.po.ProdParamsTemplate;
import com.brainsoon.resrelease.po.ResFileRelation;
import com.brainsoon.resrelease.po.ResOrder;
import com.brainsoon.resrelease.po.ResOrderDetail;
import com.brainsoon.resrelease.service.IResOrderService;
import com.brainsoon.resrelease.service.IResOrderWorkFlowService;
import com.brainsoon.resrelease.support.ResReleaseConstant;
import com.brainsoon.system.model.User;
import com.brainsoon.system.service.ISysOperateService;
import com.brainsoon.system.service.IUserService;
import com.brainsoon.system.support.SysOperateLogUtils;

@Service
public class ResOrderWorkFlowService extends BaseService implements
		IResOrderWorkFlowService {

	@Autowired
	private IJbpmExcutionService jbpmExcutionService;
	@Autowired
	private ISysOperateService sysOperateService;
	@Autowired
	private IResOrderService resOrderService;
	@Autowired
	private IUserService userService;
	@Autowired
	private ISubjectService subjectService;

	/**
	 * 需求单上报
	 */
	@Override
	public void doApply(String objectId,String posttype) {
		UserInfo user = LoginUserUtil.getLoginUser();
		Long userId = 0L;
		if (user == null) {
			userId = -2L;
		} else {
			userId = user.getUserId();
		}
		ResOrder resOrder = null;
		String[] objectIds = StringUtils.split(objectId, ",");
		String wfType = "";
		String resType = "";
		wfType = WFType.ORDER_CHECK;
		for (String id : objectIds) {
			String status = ResReleaseConstant.OrderStatus.TO_AUDIT;
			resOrder = (ResOrder) resOrderService.getByPk(ResOrder.class, (Long.valueOf(id)));
			List<ResOrderDetail> detailList = resOrderService.getResOrderDetailByOrderIdAndtype(id,posttype);
			if (detailList.size()==0) {
				throw new ServiceException("上报失败：对应的资源不存在！");
			}
			if (!(resOrder.getStatus().equals(ResReleaseConstant.OrderStatus.CREATED)
					||resOrder.getStatus().equals(ResReleaseConstant.OrderStatus.AUDIT_REFUSE))) {
				throw new ServiceException("只有“未提交”和“驳回”状态的需求单才可以提交");
			}
			if(resOrder.getTemplate().getType()!=null){
				resType = resOrder.getTemplate().getType();
			}
			Map<String, String> map = getWorkFlowInfo(WorkFlowUtils.getExecuId(id, "edu"));
			String wfTaskId = map.get("wfTaskId");
			if (StringUtils.isBlank(wfTaskId)) {
				// 创建流程
				jbpmExcutionService.createProcessInstance(wfType, id, resOrder.getChannelName(),resType);
			}else{
				jbpmExcutionService.endTask(wfTaskId,ProcessConstants.SUBMIT, userId.toString(),resType);
			}
			// 创建流程
			//jbpmExcutionService.createProcessInstance(wfType, objectIds, resOrder.getChannelName());
			// 修改状态
			updateResOrderStatus(id, status,"");
			sysOperateService.saveHistory(wfType + "." + id, "", "需求单上报", "上报", new Date(), userId);
			//写日志
			SysOperateLogUtils.addLog("resOrder_apply", resOrder.getChannelName(), user);
		}
	}
	/**
	 * 主题库上报
	 */
	@Override
	public void doApplytoZtk(String objectId,String posttype) {
		UserInfo user = LoginUserUtil.getLoginUser();
		Long userId = 0L;
		if (user == null) {
			userId = -2L;
		} else {
			userId = user.getUserId();
		}
		SubjectStore subjectStore = null;
		String[] objectIds = StringUtils.split(objectId, ",");
		String wfType = "";
		String resType = "";
		wfType = WFType.SUBJECT_CHECK;
		for (String id : objectIds) {
			String status = ResReleaseConstant.OrderStatus.TO_AUDIT;
			subjectStore = (SubjectStore) subjectService.getByPk(SubjectStore.class, (Long.valueOf(id)));
			List<ResOrderDetail> detailList = resOrderService.getResOrderDetailByOrderIdAndtype(id,posttype);
			if (detailList.size()==0) {
				throw new ServiceException("上报失败：对应的资源不存在！");
			}
			if (!(subjectStore.getStatus().equals(ResReleaseConstant.OrderStatus.CREATED)
					||subjectStore.getStatus().equals(ResReleaseConstant.OrderStatus.AUDIT_REFUSE))) {
				throw new ServiceException("只有“未提交”和“驳回”状态的需求单才可以提交");
			}
			if(subjectStore.getTemplate().getType()!=null){
				resType = subjectStore.getTemplate().getType();
			}
			Map<String, String> map = getWorkFlowInfo(WorkFlowUtils.getExecuId(id, "sub"));
			String wfTaskId = map.get("wfTaskId");
			if (StringUtils.isBlank(wfTaskId)) {
				// 创建流程
				jbpmExcutionService.createProcessInstance(wfType, id, subjectStore.getName(),resType);
			}else{
				jbpmExcutionService.endTask(wfTaskId,ProcessConstants.SUBMIT, userId.toString(),resType);
			}
			// 修改状态
			updateSubjectStatus(id, status,"");
			sysOperateService.saveHistory(wfType + "." + id, "", "主题库上报", "上报", new Date(), userId);
			//写日志
			SysOperateLogUtils.addLog("subject_apply", subjectStore.getName(), user);
		}
	}
	
	

	@Override
	public void doCheck(String objectId,String status,String checkOpinion,String wfTaskId,String decision) throws Exception {
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
				//String excuteId = ProcessConstants.WFType.ORDER_CHECK + "." + objectId;
				String excuteId = WorkFlowUtils.getExecuId(objectIds, "edu");
				status = "";
				Task task = getCurrTask(excuteId);
				statusDesc = task.getName();
				
				//根据资源ID,找到对应的流程实例ID
				if (StringUtils.isBlank(wfTaskId)) {
					Map<String, String> map = getWorkFlowInfo(WorkFlowUtils.getExecuId(objectIds, "edu"));
					wfTaskId = map.get("wfTaskId");
					if (StringUtils.isBlank(wfTaskId)) {
						throw new ServiceException("资源[" + objectIds + "]，对应的流程不存在，请确认！");
					}
				}
				String resType = "";
				if(resOrder.getTemplate().getType()!=null){
					resType = resOrder.getTemplate().getType();
				}
				if (decision.equals(ProcessConstants.APPROVE)) {
					jbpmExcutionService.doApprove(wfTaskId,userId.toString(),resType);
					operateDesc = "需求单审核通过";
					// 资源一审，通过状态为 “二审待审核”，其它为，“已通过”
					status = ResReleaseConstant.OrderStatus.AUDITED;
				} else if (decision.equals(ProcessConstants.REJECT)) {
					jbpmExcutionService.doReject(wfTaskId, userId.toString(),resType);
					status = ResReleaseConstant.OrderStatus.AUDIT_REFUSE;
					operateDesc = "需求单审核驳回";
				}
				wfTaskId = null;
				updateResOrderStatus(objectIds, status, checkOpinion);
				//写日志
				SysOperateLogUtils.addLog("resOrder_check", resOrder.getChannelName()+":" + operateDesc, user);
				sysOperateService.saveHistory(excuteId, checkOpinion, statusDesc, operateDesc, new Date(), userId);
				
			}
		} catch (Exception e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public void doChecktoZTK(String objectId,String status,String auditMsg,String wfTaskId,String decision) throws Exception {
		try {
			UserInfo user = LoginUserUtil.getLoginUser();
			Long userId = 0L;
			if (user == null) {
				userId = -2L;
			} else {
				userId = user.getUserId();
			}
			SubjectStore store = null;
			String[] ids = StringUtils.split(objectId, ",");

			for (String objectIds : ids) {
				store = (SubjectStore) subjectService.getByPk(SubjectStore.class, (Long.valueOf(objectIds)));
				if (store == null) {
					throw new ServiceException("操作失败：对应的资源不存在！");
				}
				
				String operateDesc = "";
				String statusDesc = "";
				//String excuteId = ProcessConstants.WFType.ORDER_CHECK + "." + objectId;
				String excuteId = WorkFlowUtils.getExecuId(objectIds, "sub");
				status = "";
				Task task = getCurrTask(excuteId);
				statusDesc = task.getName();
				
				//根据资源ID,找到对应的流程实例ID
				if (StringUtils.isBlank(wfTaskId)) {
					Map<String, String> map = getWorkFlowInfo(WorkFlowUtils.getExecuId(objectIds, "sub"));
					wfTaskId = map.get("wfTaskId");
					if (StringUtils.isBlank(wfTaskId)) {
						throw new ServiceException("资源[" + objectIds + "]，对应的流程不存在，请确认！");
					}
				}
				String resType = "";
				if(store.getTemplate().getType()!=null){
					resType = store.getTemplate().getType();
				}
				if (decision.equals(ProcessConstants.APPROVE)) {
					jbpmExcutionService.doApprove(wfTaskId,userId.toString(),resType);
					operateDesc = "主题库审核通过";
					// 资源一审，通过状态为 “二审待审核”，其它为，“已通过”
					status = ResReleaseConstant.OrderStatus.AUDITED;
				} else if (decision.equals(ProcessConstants.REJECT)) {
					jbpmExcutionService.doReject(wfTaskId, userId.toString(),resType);
					status = ResReleaseConstant.OrderStatus.AUDIT_REFUSE;
					operateDesc = "主题库审核驳回";
				}
				wfTaskId = null;
				updateSubjectStatus(objectIds, status, auditMsg);
				//写日志
				SysOperateLogUtils.addLog("subject_check", store.getName()+":" + operateDesc, user);
				sysOperateService.saveHistory(excuteId, auditMsg, statusDesc, operateDesc, new Date(), userId);
				
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
	public void doSaveAndSubmit(String objectId, String wfTaskId, String channelName, String templateId,
			String description, String orderDate,String posttype) {
		try {
			UserInfo user = LoginUserUtil.getLoginUser();
			Long userId = 0L;
			if (user == null) {
				userId = -2L;
			} else {
				userId = user.getUserId();
			}

			ResOrder resOrder = (ResOrder) resOrderService.getByPk(ResOrder.class, (Long.valueOf(objectId)));
			String wfType = "";
			String resType = "";
			wfType = WFType.ORDER_CHECK;
			String status = ResReleaseConstant.OrderStatus.TO_AUDIT;
			List<ResOrderDetail> list = resOrderService.getResOrderDetailByOrderIdAndtype(objectId, posttype);
			if (list==null) {
				throw new ServiceException("提交失败：需求单资源不存在，无法进行上报！");
			}
			if (!resOrder.getStatus().equals(ResReleaseConstant.OrderStatus.AUDIT_REFUSE)) {
				throw new ServiceException("状态不为“已驳回”的资源不允许执行提交");
			}
			if(resOrder.getTemplate().getType()!=null){
				resType = resOrder.getTemplate().getType();
			}
			jbpmExcutionService.endTask(wfTaskId,ProcessConstants.SUBMIT, userId.toString(),resType);
			//jbpmExcutionService.createProcessInstance(wfType, objectId, resOrder.getChannelName());
			updateResOrderStatusAndChannelName(objectId, status,"", channelName, templateId, description, orderDate);
			SysOperateLogUtils.addLog("resOrder_saveAndSubmit", resOrder.getChannelName(), user);
			sysOperateService.saveHistory(wfType + "." + objectId, "", "需求单编辑提交", "编辑提交", new Date(), userId);
		} catch (Exception e) {
			throw new ServiceException(e.getMessage());
		}
	}
	@Override
	public void doSaveAndSubmittoSubject(SubjectStore stores,String wfTaskId,String posttype) {
		try {
			UserInfo user = LoginUserUtil.getLoginUser();
			Long userId = 0L;
			if (user == null) {
				userId = -2L;
			} else {
				userId = user.getUserId();
			}
			SubjectStore subjectStore = (SubjectStore) subjectService.getByPk(SubjectStore.class, stores.getId());
			String wfType = "";
			String resType = "";
			wfType = WFType.SUBJECT_CHECK;
			String status = ResReleaseConstant.OrderStatus.TO_AUDIT;
			List<ResOrderDetail> list = resOrderService.getResOrderDetailByOrderIdAndtype(stores.getId().toString(), posttype);
			if (list==null) {
				throw new ServiceException("提交失败：主题库资源不存在，无法进行上报！");
			}
			if (!subjectStore.getStatus().equals(ResReleaseConstant.OrderStatus.AUDIT_REFUSE)) {
				throw new ServiceException("状态不为“已驳回”的资源不允许执行提交");
			}
			if(subjectStore.getTemplate().getType()!=null){
				resType = subjectStore.getTemplate().getType();
			}
			jbpmExcutionService.endTask(wfTaskId,ProcessConstants.SUBMIT, userId.toString(),resType);
			//jbpmExcutionService.createProcessInstance(wfType, objectId, resOrder.getChannelName());
			updateSubjectStatusAndChannelName(stores, status);
			SysOperateLogUtils.addLog("subject_saveAndSubmit", stores.getName(), user);
			sysOperateService.saveHistory(wfType + "." + stores.getId(), "", "主题库编辑提交", "编辑提交", new Date(), userId);
		} catch (Exception e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public void updateResOrderStatus(String objectId, String status, String checkOpinion){
		if (StringUtils.isBlank(objectId)) {
			throw new ServiceException("更新需求单：需求单标识（objectId）为空！");
		}
		//处理流程信息
		logger.info("更新需求单状态");
		String[] ids = objectId.split(",");
		ResOrder resOrder = null;
		User user = (User) userService.getByPk(User.class, LoginUserUtil.getLoginUser().getUserId());
		for(String id:ids){
			resOrder = (ResOrder) resOrderService.getByPk(ResOrder.class, (Long.valueOf(id)));
			resOrder.setAuditor(user);
			resOrder.setAuditTime(new Date());
			resOrder.setAuditRemark(checkOpinion);
			resOrder.setStatus(status);
			resOrderService.saveOrUpdate(resOrder);
		}
	}
	@Override
	public void updateSubjectStatus(String objectId, String status, String checkOpinion){
		if (StringUtils.isBlank(objectId)) {
			throw new ServiceException("更新主题库：主题库标识（objectId）为空！");
		}
		//处理流程信息
		logger.info("更新主题库状态");
		String[] ids = objectId.split(",");
		SubjectStore subjectStore = null;
		User user = (User) userService.getByPk(User.class, LoginUserUtil.getLoginUser().getUserId());
		for(String id:ids){
			subjectStore = (SubjectStore) subjectService.getByPk(SubjectStore.class, (Long.valueOf(id)));
			subjectStore.setAuditor(user);
			subjectStore.setAuditTime(new Date());
			subjectStore.setAuditMsg(checkOpinion);
			subjectStore.setStatus(status);
			subjectService.saveOrUpdate(subjectStore);
		}
	}
	
	
	//删除需求单（支持批量）
	@Override
	public String deleteBatchResOrder(String ids,String posttype) {
		String result = "1";
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		String[] idArray = ids.split(",");
		for (int i = 0; i < idArray.length; i++) {
			ResOrder resOrder = (ResOrder) resOrderService.getByPk(ResOrder.class, Long.valueOf(idArray[i]));
			if(resOrder.getStatus().equals(ResReleaseConstant.OrderStatus.CREATED)||resOrder.getStatus().equals(ResReleaseConstant.OrderStatus.AUDIT_REFUSE)){
				if(resOrder.getStatus().equals(ResReleaseConstant.OrderStatus.AUDIT_REFUSE)){
					//暂时先不删除流程数据
					List<Task> tasks = jbpmExcutionService.getCurrentTasks(WorkFlowUtils.getExecuId(idArray[i], "press"));
					jbpmExcutionService.endTask(tasks.get(0).getId(),ProcessConstants.SUBMIT, userInfo.getUserId().toString(),resOrder.getTemplate().getType());
					Map<String, String> map = getWorkFlowInfo(WorkFlowUtils.getExecuId(idArray[i], "press"));
					String wfTaskId = map.get("wfTaskId");
					jbpmExcutionService.doApprove(wfTaskId, userInfo.getUserId().toString(),resOrder.getTemplate().getType());
				}
				//删除需求单明细
				List<ResOrderDetail> detailList = resOrderService.getResOrderDetailByOrderIdAndtype(idArray[i], posttype);
				if(detailList!=null){
					for(ResOrderDetail detail : detailList){
						resOrderService.delete(detail);
					}
				}
				//删除需求单关联表
				List<ResFileRelation> fileList = resOrderService.queryFileByOrdeIdAndposttype(Long.valueOf(idArray[i]),posttype);
				if(fileList!=null){
					for(ResFileRelation file: fileList){
						resOrderService.delete(file);
					}
				}
				
				//删除需求单
				resOrderService.delete(ResOrder.class, new Long(idArray[i]));
				
				//写日志
				SysOperateLogUtils.addLog("resOrder_del", resOrder.getChannelName(), userInfo);
				
			}
		}	
		
		return result;
	}
	
	@Override
	public void updateResOrderStatusAndChannelName(String objectId, String status, 
			String checkOpinion, String channelName, String templateId,
			String description, String orderDate) {
		if (StringUtils.isBlank(objectId)) {
			throw new ServiceException("更新需求单：需求单标识（objectId）为空！");
		}
		logger.info("更新需求单状态");
		String[] ids = objectId.split(",");
		ResOrder resOrder = null;
		User user = (User) userService.getByPk(User.class, LoginUserUtil.getLoginUser().getUserId());
		for(String id:ids){
			resOrder = (ResOrder) resOrderService.getByPk(ResOrder.class, (Long.valueOf(id)));
			resOrder.setAuditor(user);
			resOrder.setAuditTime(new Date());
			resOrder.setAuditRemark(checkOpinion);
			resOrder.setStatus(status);
			resOrder.setChannelName(channelName);
			ProdParamsTemplate template = new ProdParamsTemplate();
			template.setId(Long.valueOf(templateId));
			resOrder.setDescription(description);
			try {
				resOrder.setOrderDate(DateUtils.parseDate(orderDate, "yyyy-MM-dd"));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			resOrder.setTemplate(template);
			resOrderService.saveOrUpdate(resOrder);
		}
	}
	@Override
	public void updateSubjectStatusAndChannelName(SubjectStore store, String status) {
		if (StringUtils.isBlank(store.getId().toString())) {
			throw new ServiceException("更新主题库：主题库标识（id）为空！");
		}
		logger.info("更新主题库状态");
		User user = (User) userService.getByPk(User.class, LoginUserUtil.getLoginUser().getUserId());
		SubjectStore subjectStore = (SubjectStore) subjectService.getByPk(SubjectStore.class, store.getId());
		store.setCreateUser(subjectStore.getCreateUser());
		store.setCreateTime(subjectStore.getCreateTime());
		store.setJsonOkStatus(subjectStore.getJsonOkStatus());
		store.setExcelOkStatus(subjectStore.getExcelOkStatus());
		store.setPlatformId(subjectStore.getPlatformId());
		store.setRemark(subjectStore.getRemark());
			store.setAuditor(user);
			store.setAuditTime(new Date());
			store.setStatus(status);
			ProdParamsTemplate template = (ProdParamsTemplate) subjectService.getByPk(ProdParamsTemplate.class, store.getTemplate().getId());
			store.setTemplate(template);
			store.setModuleName(template.getName());
			store.setRestype(template.getType());
			subjectService.saveOrUpdate(store);
	}
	

	public Map<String, String> getWorkFlowInfo(String execuId) {
		Map<String, String> map = new HashMap<String, String>();
		List<Task> taskList = jbpmExcutionService.getCurrentTasks(execuId);
		Task task = null;
		if (taskList != null && taskList.size() > 0) {
			task = taskList.get(0);
		}
//		if (task == null) {
//			throw new ServiceException("获取审核任务失败：不存在该任务或该任务已经完成！");
//		}
		if (task != null) {
			map.put("execuId", task.getExecutionId());
			map.put("wfTaskId", task.getId());
		}
		//map.put("execuId", task.getExecutionId());
		//map.put("wfTaskId", task.getId());
		return map;
	}
}
