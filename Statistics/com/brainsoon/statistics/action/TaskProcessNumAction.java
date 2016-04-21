package com.brainsoon.statistics.action;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.action.Token;
import com.brainsoon.appframe.query.Operator;
import com.brainsoon.appframe.query.QueryConditionItem;
import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;

import org.apache.commons.io.FileUtils;

import com.brainsoon.common.util.date.DateUtil;
import com.brainsoon.statistics.service.ITaskProcessNumService;
import com.brainsoon.system.model.MetaDataFileModelGroup;
import com.brainsoon.system.model.User;
import com.brainsoon.system.support.SysOperateLogUtils;
import com.brainsoon.taskprocess.model.TaskDetail;
import com.brainsoon.taskprocess.model.TaskProcess;
import com.brainsoon.taskprocess.model.TaskResRelation;
import com.brainsoon.taskprocess.service.ITaskProcessService;



@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class TaskProcessNumAction extends BaseAction {
	/** 默认命名空间 **/
	private final String baseUrl = "/statistics/taskProcessNum/";
	UserInfo userInfo =  LoginUserUtil.getLoginUser();
	private static  List<TaskProcess> allRecords = new ArrayList<TaskProcess>();
	private static Long total = 0L;
	@Autowired
	private ITaskProcessNumService taskProcessNumService;
	@Autowired
	private ITaskProcessService taskProcessService;
	/**
	 * 获取元数据信息
	 * @param order
	 * @param model
	 * @throws Exception 
	 */
	@RequestMapping(baseUrl+"queryList") 
	@Token(save=true)
	public @ResponseBody PageResult  queryList(HttpServletRequest request,HttpServletResponse response) throws Exception{
		QueryConditionList conditionList = getQueryConditionList();
		
		//当前登录人已经被授权，则只能显示自己添加的任务
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		String userIds = userInfo.getDeptUserIds();
		if(userInfo.getIsPrivate() == 1){
			if(StringUtils.isNotBlank(userIds)){
				if(StringUtils.isNotEmpty(userIds)){
//					String[] ids = userIds.substring(0, userIds.length()-1).split(",");
//					Long[] idArr = new Long[ids.length];
//					for(int i=0;i<ids.length;i++){
//						idArr[i] = Long.valueOf(ids[i]);
//					}
		    		conditionList.addCondition(new QueryConditionItem("createUser.id", Operator.IN, userIds.substring(0, userIds.length()-1)));
				}
			}else{
				conditionList.addCondition(new QueryConditionItem("createUser.id", Operator.EQUAL,userInfo.getUserId()));
			}
		}else{
			if(StringUtils.isNotEmpty(userIds)){
//				String[] ids = userIds.substring(0, userIds.length()-1).split(",");
//				Long[] idArr = new Long[ids.length];
//				for(int i=0;i<ids.length;i++){
//					idArr[i] = Long.valueOf(ids[i]);
//				}
	    		conditionList.addCondition(new QueryConditionItem("createUser.id", Operator.IN, userIds.substring(0, userIds.length()-1)));
			}else{
				conditionList.addCondition(new QueryConditionItem("createUser.id", Operator.IN, "-2"));
			}
		}
		
		String taskName = request.getParameter("taskName");
		if(StringUtils.isNotBlank(taskName)){
			taskName = URLDecoder.decode(request.getParameter("taskName"), "UTF-8");
		}
		/*String status = request.getParameter("status");
		String processName = request.getParameter("processName");*/
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		if (StringUtils.isNotBlank(taskName)) {
			conditionList.addCondition(new QueryConditionItem("taskName", Operator.LIKE,"%"+taskName+"%"));
		}
		if (StringUtils.isNotBlank(startTime)) {
			conditionList.addCondition(new QueryConditionItem("startTime", Operator.GT,DateUtil.convertStringToDate(startTime)));
		}
		if (StringUtils.isNotBlank(endTime)) {
			conditionList.addCondition(new QueryConditionItem("endTime", Operator.LT,DateUtil.getNextDate(DateUtil.convertStringToDate(endTime))));
		}
		/*if (StringUtils.isNotBlank(processName)) {
			conditionList.addCondition(new QueryConditionItem("user.userName", Operator.LIKE,"%"+processName+"%"));
		}
		if (StringUtils.isNotBlank(status)) {
			conditionList.addCondition(new QueryConditionItem("status", Operator.EQUAL,Integer.parseInt(status)));
		}*/
		PageResult pageResult = taskProcessNumService.query4Page(TaskProcess.class, conditionList);
		
		//allRecords表示总结果集
		allRecords=pageResult.getRows();
		//表示总结果数
		total = pageResult.getTotal();
		
		return pageResult;
	}
	
	/**
	 * 任务加工统计导出下载
	 * 
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = baseUrl + "exportRes")
	public ResponseEntity<byte[]> exportRes(@RequestParam("ids") String ids) throws IOException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		String filename = URLEncoder.encode("任务加工统计.xls", "UTF-8");
		headers.setContentDispositionFormData("attachment", filename);
		List datas = new ArrayList();
		if (StringUtils.isNotBlank(ids)) {
			String[] idArr = StringUtils.split(ids,",");
			if(allRecords!=null && total>=idArr.length){
				for (String id : idArr) {
					Long id1 = Long.parseLong(id); 
					TaskProcess taskProcess = (TaskProcess)taskProcessNumService.getByPk(TaskProcess.class, id1);
					datas.add(taskProcess);
					SysOperateLogUtils.addLog("taskNum_exportRes",taskProcess.getTaskName(), userInfo);
				}
			}
		} else {
			for(int i=0;i<allRecords.size();i++){
				datas.add(allRecords.get(i));
			}
		}
		return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(taskProcessNumService.exportRes(datas)), headers, HttpStatus.OK);
	}
	
	
	/**
	 * 获取要导出的全部id
	 * @param order
	 * @param model
	 * @throws Exception 
	 */
	@RequestMapping(baseUrl+"queryId") 
	@Token(save=true)
	public @ResponseBody String  queryId(HttpServletRequest request,HttpServletResponse response) throws Exception{
		List<TaskProcess> idList = taskProcessNumService
				.dotaskId();
		JSONArray array = new JSONArray();
		for (TaskProcess taskProcess : idList) {
			JSONObject json = new JSONObject();
			json.put("id", taskProcess.getId());
			array.add(json);
		}
		return array.toString();
	}
}
