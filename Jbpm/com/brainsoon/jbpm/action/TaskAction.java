package com.brainsoon.jbpm.action;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.query.Operator;
import com.brainsoon.appframe.query.QueryConditionItem;
import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.appframe.util.StringUtil;
import com.brainsoon.bsrcm.search.util.DateUtil;
import com.brainsoon.common.support.Constants;
import com.brainsoon.common.support.GlobalAppCacheMap;
import com.brainsoon.jbpm.constants.ProcessConstants.WFType;
import com.brainsoon.jbpm.po.ViewWaitTask;
import com.brainsoon.jbpm.po.ViewWaitTaskTemp;
import com.brainsoon.jbpm.service.IJbpmTaskViewService;
import com.brainsoon.resrelease.po.ResOrder;
import com.brainsoon.system.model.User;

@Controller
@RequestMapping("/TaskAction/")
public class TaskAction extends BaseAction {
	/** 默认命名空间 **/
	private final String basePath = "/jbpm/";
	@Autowired
	private IJbpmTaskViewService jbpmTaskViewService;

	/**
	 * 跳转到添加页面
	 */
	@RequestMapping("toList")
	public String toTaskList(Model model, ViewWaitTask task,HttpServletRequest request) throws Exception {
		// getRequest().setAttribute("wfTypes", WFType.map) ;
		String taskFlag = request.getParameter("taskFlag");
		model.addAttribute(Constants.COMMAND, task);
		HttpSession session = getSession();
		Map<String, String> map = new TreeMap<String, String>();
	//	int platformId = 0;
		if(LoginUserUtil.getLoginUser()!=null){
//			platformId = LoginUserUtil.getLoginUser().getPlatformId();
//		   if (platformId == 2) {
			map.put(WFType.PUB_ORES_CHECK, WFType.PUB_ORES_CHECK_DESC);
			map.put(WFType.PUB_ORDER_CHECK, WFType.PUB_ORDER_CHECK_DESC);
			map.put(WFType.PUB_SUBJECT_CHECK, WFType.PUB_SUBJECT_CHECK_DESC);
//		   } else {
//			map.put(WFType.ORES_CHECK, WFType.ORES_CHECK_CHECK_DESC);
//			map.put(WFType.BRES_CHECK, WFType.BRES_CHECK_CHECK_DESC);
//			map.put(WFType.PRES_CHECK, WFType.PRES_CHECK_CHECK_DESC);
//			map.put(WFType.ORDER_CHECK, WFType.ORDER_CHECK_DESC);
//		  }
		}
		model.addAttribute("wfTypes", map);
		model.addAttribute("taskFlag", taskFlag);
		return basePath + "taskList";
	}

	/**
	 * 列表查询
	 * 
	 * @param request
	 * @param response
	 * @param params
	 * @return json
	 */
	@RequestMapping(value = "list")
	@ResponseBody
	public PageResult list(HttpServletRequest request, HttpServletResponse response) {
		logger.info("进入查询方法");
		QueryConditionList conditionList = getQueryConditionList();

		/**
		 * 查询任务名称： "1" 资源一审、资源审核 ,"2" 资源二审
		 */
//		String conditionVal = "'资源审核','需求单审核','资源修改','需求单修改','主题库审核'";
//		QueryConditionItem queryItem = new QueryConditionItem("taskName", Operator.IN, conditionVal);
//		conditionList.addCondition(queryItem);
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		String wfPrivis = LoginUserUtil.getWfPrivis();
//		if (StringUtils.isNotBlank(wfPrivis)) {
//			queryItem = new QueryConditionItem("swimName", Operator.IN, wfPrivis);
//			conditionList.addCondition(queryItem);
//		} else {
//			if (!userInfo.isAdmin()) {
//				queryItem = new QueryConditionItem("swimName", Operator.IN, "''");
//				conditionList.addCondition(queryItem);
//			}
//		}
		String publishType = "";
		List<String> listPublishType = new ArrayList<String>();
		if(userInfo.getResTypes()!=null){
			Iterator it = userInfo.getResTypes().entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry) it.next();
				if(StringUtils.isNotBlank(pairs.getKey().toString())&&StringUtils.isNotBlank(pairs.getValue().toString())){
					listPublishType.add(pairs.getKey().toString());
//					publishType = publishType+pairs.getKey().toString()+",";
				}
			}
			if(publishType.endsWith(",")){
				publishType = publishType.substring(0,publishType.length()-1);
			}
//			queryItem = new QueryConditionItem("publishType", Operator.IN, publishType);
//			conditionList.addCondition(queryItem);
		}
		String creatorName = "";
		String userIds = userInfo.getDeptUserIds();
		//判断是不是个人用户授权
		int isPrivate = userInfo.getIsPrivate();
		if (isPrivate == 1) {
			if(StringUtils.isNotBlank(userIds)){
			}else{
				userIds = LoginUserUtil.getLoginUser().getUserId()+"";
			}
		}
//		if (isPrivate == 1) {
//				userIds = LoginUserUtil.getLoginUser().getUserId()+"";
//			}
		
		if (StringUtils.isNotBlank(userIds)) {
			if(userIds.endsWith(",")){
				userIds = userIds.substring(0,userIds.length()-1);
			}
			if(userIds!=null){
			String userIdsArray[] = userIds.split(",");
			for(String userIdsArray1:userIdsArray){
				User user = (User) baseService.getByPk(User.class, Long.parseLong(userIdsArray1));
				creatorName = creatorName+"'"+user.getLoginName()+"'"+",";
			}
			if(creatorName.endsWith(",")){
				creatorName = creatorName.substring(0,creatorName.length()-1);
			}
//				queryItem = new QueryConditionItem("applyUser", Operator.IN, creatorName);
//				conditionList.addCondition(queryItem);
			}
//			hql.append("&creator=" + userIds);
		}else{
//			queryItem = new QueryConditionItem("applyUser", Operator.IN, "-2");
//			conditionList.addCondition(queryItem);
			creatorName = "'-2'";
		}
//		else{
//			hql.append("&creator=-2");
//		}
		String hql = "select new com.brainsoon.jbpm.po.ViewWaitTaskTemp(v.taskId,v.taskName,v.createDate,v.processName,u.userName,v.publishType,v.bizId,v.swimName,v.taskUrl,v.execuId) from User u,ViewWaitTask v where u.loginName=v.applyUser and v.applyUser in ("+creatorName+") and v.swimName in("+wfPrivis+")";
//		String hql ="from ViewWaitTask where applyUser in ("+creatorName+") and swimName in("+wfPrivis+")";
		 List<QueryConditionItem> items = conditionList.getConditionItems();
		 if(items!=null && items.size()>0){
			 for(QueryConditionItem item:items){
				 if(item.getFieldName().equals("taskName")){
					 publishType = item.getValue()+ "";
					 hql+=" and v.taskName like '"+item.getValue()+"'";
				 }else if(item.getFieldName().equals("busiDesc")){
					 hql+=" and v.busiDesc like '"+item.getValue()+"'";
				 }else if(item.getFieldName().equals("applyUser")){
					 hql+=" and u.userName like '"+item.getValue()+"'";
				 }else if(item.getFieldName().equals("processName")){
					 hql+=" and v.processName like '"+item.getValue()+"'";
				 }
			 }
		 }
		 hql+=" order by v.createDate desc";
		List<ViewWaitTaskTemp> listResult = new ArrayList<ViewWaitTaskTemp>();
		int size=0;
//		jbpmTaskViewService.query(hql);
		List<ViewWaitTaskTemp> listObj = jbpmTaskViewService.query(hql);
		for (ViewWaitTaskTemp viewWaitTask : listObj) {
			String types = viewWaitTask.getPublishType();
			String type[] = types.split(",");
			List<String> listResType = new ArrayList<String>();
			for (int j = 0; j < type.length; j++) {
				listResType.add(type[j]);
			}
			if(listPublishType.containsAll(listResType)){
				size++;
				listResult.add(viewWaitTask);
				continue;
			}
		}
		PageResult pageResult = new PageResult();
		int pageSize = StringUtil.obj2Int(request.getParameter("rows"));
		int pageNo = StringUtil.obj2Int(request.getParameter("page"));
		int startIndex = (pageNo - 1) * pageSize > 0 ? (pageNo - 1) * pageSize : 0;
		List<ViewWaitTaskTemp> listPage = new ArrayList<ViewWaitTaskTemp>();
		for (int i=startIndex; i < startIndex+pageSize; i++) {
			if(i<listResult.size()){
				listPage.add(listResult.get(i));
				continue;
			}else{
				break;
			}
		}
		pageResult.setRows(listPage);
		pageResult.setTotal(size);
//		pageResult = jbpmTaskViewService.query4Page(ViewWaitTask.class, conditionList);
		return pageResult;
	}

}
