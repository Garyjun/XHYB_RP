package com.brainsoon.taskprocess.web.controller;


import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
import com.brainsoon.common.exception.DaoException;
import com.brainsoon.common.support.Constants;
import com.brainsoon.common.support.OperDbUtils;
import com.brainsoon.common.support.SysResTypeCacheMap;
import com.brainsoon.system.model.User;
import com.brainsoon.system.service.IUserService;
import com.brainsoon.system.support.SysOperateLogUtils;
import com.brainsoon.taskprocess.model.TaskDetail;
import com.brainsoon.taskprocess.model.TaskProcess;
import com.brainsoon.taskprocess.model.TaskProcessorRelation;
import com.brainsoon.taskprocess.model.TaskResRelation;
import com.brainsoon.taskprocess.service.ITaskProcessService;

/**
 *
 * @ClassName: TaskProcessController
 * @Description:   加工任务单管理Controller
 * @author: tanghui
 * @date:2015-3-31 上午10:50:33
 */
@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class TaskProcessController extends BaseAction {
	protected static final Logger logger = Logger.getLogger(TaskProcessController.class);

	/**默认命名空间**/
	private static final String baseUrl = "/taskProcess/";
	@Autowired
    @Qualifier("taskProcessService")
	private ITaskProcessService taskProcessService;
	@Autowired
	private IUserService userService;

	UserInfo userinfo = LoginUserUtil.getLoginUser();
	
	/**
	 *
	 * @Title: index
	 * @Description: 进入main页面
	 * @param @param model
	 * @param @param taskProcess
	 * @param @return
	 * @return String
	 * @throws
	 */
	@RequestMapping(value = baseUrl + "index", method = {RequestMethod.GET})
	public String index(Model model, TaskProcess taskProcess) {
		logger.debug("to index ");
//		model.addAttribute(Constants.PLATFORM_ID,userinfo.getPlatformId());
		return "taskprocess/taskprocessMain";
	}

	/**
	 *
	 * @Title: list
	 * @Description: 进入list页面
	 * @param @param model
	 * @param @param taskProcess
	 * @param @return
	 * @return String
	 * @throws
	 */
	@RequestMapping(value = baseUrl + "list", method = {RequestMethod.GET})
	public String list(Model model, TaskProcess taskProcess) {
		logger.debug("to list ");
		model.addAttribute(Constants.COMMAND, taskProcess);
		setModelCommonValue(model);
		model.addAttribute("makers", userService.getMakerList());
		return "taskprocess/taskprocesslist";
	}

	/**
	 * @throws UnsupportedEncodingException 
	 *
	 * @Title: query
	 * @Description:  查询list列表数据集
	 * @param @param taskName
	 * @param @param createUser
	 * @param @return
	 * @return PageResult
	 * @throws
	 */
	@RequestMapping(value = baseUrl + "query")
	@ResponseBody
	public PageResult query(@RequestParam(value = "taskName", required = false) String taskName,
			@RequestParam(value = "createUser", required = false) String createUser) throws ParseException, UnsupportedEncodingException {
		logger.info("+++++++++++++++++++++++++++------------------进入加工查询列表---------------++++++++++++++++++++++++++++++++====");
		//PageInfo pageInfo = getPageInfo();
		//taskProcess.setPlatformId(userinfo.getPlatformId());
		//return taskProcessService.query(pageInfo, taskProcess);
		QueryConditionList conditionList = getQueryConditionList();
		if (taskName != null) {
			conditionList.addCondition(new QueryConditionItem("taskName", Operator.LIKE, "%"+URLDecoder.decode(taskName,"UTF-8")+"%"));
		}
		if (createUser != null) {
			conditionList.addCondition(new QueryConditionItem("createUser.loginName", Operator.LIKE, "%"+createUser+"%"));
		}
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		if(userInfo!=null){
			Map<String, String> resMap = userInfo.getResTypes();
			if(resMap!=null){
				Set<String> set = resMap.keySet();
				Iterator<String> it = set.iterator();
				String resTypes = "";
				while(it.hasNext()){
					resTypes += "'" + it.next() + "'" + ",";
				}
				if(StringUtils.isNotEmpty(resTypes)){
					resTypes = resTypes.substring(0, resTypes.length()-1);
					conditionList.addCondition(new QueryConditionItem("resType", Operator.IN, resTypes));
				}
				
			}
			String userIds = userInfo.getDeptUserIds();
			int isPrivate = userInfo.getIsPrivate();
			if(1==isPrivate){
				if(StringUtils.isNotBlank(userIds)){
					logger.info("+++++++++++++++++++++++++++进入加工查询列表++++++"+userIds+"++++++++++++++++++++++++++++++++====");
					if(StringUtils.isNotEmpty(userIds)){
						logger.info("+++++++++++++++++++++++++++进入if++++++"+userIds+"++++++++++++++++++++++++++++++++====");
			    		conditionList.addCondition(new QueryConditionItem("createUser.id", Operator.IN, userIds.substring(0, userIds.length()-1)));
					}
				}else{
					conditionList.addCondition(new QueryConditionItem("createUser.id", Operator.EQUAL, userInfo.getUserId()));
				}
			}else{
				logger.info("+++++++++++++++++++++++++++进入加工查询列表++++++"+userIds+"++++++++++++++++++++++++++++++++====");
				if(StringUtils.isNotEmpty(userIds)){
					logger.info("+++++++++++++++++++++++++++进入if++++++"+userIds+"++++++++++++++++++++++++++++++++====");
		    		conditionList.addCondition(new QueryConditionItem("createUser.id", Operator.IN, userIds.substring(0, userIds.length()-1)));
				}else{
					conditionList.addCondition(new QueryConditionItem("createUser.id", Operator.IN, "-2"));
				}
			}
		}
		return taskProcessService.query4Page(TaskProcess.class, conditionList);
	}
	
	/**
	 * 查询加工清单
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping(baseUrl + "queryTaskResource")
	@ResponseBody
	public PageResult queryTaskResource(HttpServletRequest request,HttpServletResponse response) throws ParseException, UnsupportedEncodingException{
		logger.info("加工资源列表查询");
		QueryConditionList conditionList = getQueryConditionList();
		String taskId = request.getParameter("taskId");
		String resStatus = request.getParameter("status");
		String taskProcessName = request.getParameter("taskName");
		String userName = request.getParameter("userName");
		String resName = request.getParameter("resName");
		String publishType = request.getParameter("publishType");
		if(StringUtils.isNotBlank(taskId)){
			QueryConditionItem item = new QueryConditionItem();
			item.setFieldName("taskDetail.taskProcess.id");
			item.setOperator(Operator.EQUAL);
			item.setValue(Long.valueOf(taskId));
			conditionList.addCondition(item);
		}
		if(StringUtils.isNotBlank(resStatus)){
			QueryConditionItem item = new QueryConditionItem();
			item.setFieldName("status");
			item.setOperator(Operator.EQUAL);
			if("allRes".equals(resStatus)){
				
			}else if("distributed".equals(resStatus)){
				resStatus = "1";
				item.setValue(Integer.valueOf(resStatus));
				conditionList.addCondition(item);
			}else if("finished".equals(resStatus)){
				resStatus = "2";
				item.setValue(Integer.valueOf(resStatus));
				conditionList.addCondition(item);
			}else {
				resStatus = "0";
				item.setValue(Integer.valueOf(resStatus));
				conditionList.addCondition(item);
			}
		}
		//加工单名称
		if(StringUtils.isNotBlank(taskProcessName)){
			QueryConditionItem item = new QueryConditionItem();
			item.setFieldName("taskDetail.taskProcess.taskName");
			item.setOperator(Operator.LIKE);
			taskProcessName = URLDecoder.decode(taskProcessName, "UTF-8");
			item.setValue("%"+taskProcessName+"%");
			conditionList.addCondition(item);
		}
		
		//加工员
		if(StringUtils.isNotBlank(userName)){
			QueryConditionItem item = new QueryConditionItem();
			item.setFieldName("taskDetail.user.userName");
			item.setOperator(Operator.LIKE);
			userName = URLDecoder.decode(userName, "UTF-8");
			item.setValue("%"+userName+"%");
			conditionList.addCondition(item);			
		}
		
		//资源名称
		if(StringUtils.isNotBlank(resName)){
			QueryConditionItem item = new QueryConditionItem();
			item.setFieldName("resName");
			item.setOperator(Operator.LIKE);
			resName = URLDecoder.decode(resName, "UTF-8");
			item.setValue("%"+resName+"%");
			conditionList.addCondition(item);			
		}
		if(StringUtils.isNotBlank(publishType)){
			QueryConditionItem item = new QueryConditionItem();
			item.setFieldName("publishType");
			item.setOperator(Operator.EQUAL);
			item.setValue(publishType);
			conditionList.addCondition(item);			
		}
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		if(userInfo!=null){
			String userIds = userInfo.getDeptUserIds();
			if(StringUtils.isNotEmpty(userIds)){
	    		conditionList.addCondition(new QueryConditionItem("taskDetail.taskProcess.createUser.id", Operator.IN, userIds.substring(0, userIds.length()-1)));
			}else{
				conditionList.addCondition(new QueryConditionItem("taskDetail.taskProcess.createUser.id", Operator.IN, "-2"));
			}
		}
		return taskProcessService.query4Page(TaskResRelation.class, conditionList);
	}
	
	/**
	 *
	 * @Title: delete
	 * @Description: 删除单个加工任务单
	 * @param @param id
	 * @param @return
	 * @return String
	 * @throws
	 */
	@RequestMapping(value =  baseUrl + "delete", method = {RequestMethod.GET})
	public String delete(@RequestParam("id") String id) {
		try {
			TaskProcess taskProcess = (TaskProcess)taskProcessService.getByPk(TaskProcess.class,Long.parseLong(id));
			SysOperateLogUtils.addLog("taskProcess_delete", taskProcess.getTaskName(), userinfo);
			taskProcessService.deleteRelativeResource(id);
			taskProcessService.deleteRelativeProcessor(id);
			taskProcessService.delete(TaskProcess.class, id);
		} catch (DaoException e) {
			logger.error(e.getMessage());
		}
		return "redirect:/taskProcess/list.action";
	}

	/**
	 *
	 * @Title: batchDelete
	 * @Description: 批量删除加工任务单
	 * @param @param ids
	 * @param @return
	 * @return String
	 * @throws
	 */
	@RequestMapping(value =  baseUrl + "batchDelete/{ids}", method = {RequestMethod.GET})
	public String batchDelete(@PathVariable String ids) {
		try {
			String[] idArray = ids.split(",");
			for (int i = 0; i < idArray.length; i++) {
				TaskProcess taskProcess = (TaskProcess)taskProcessService.getByPk(TaskProcess.class,Long.parseLong(idArray[i]));
				SysOperateLogUtils.addLog("taskProcess_delete", taskProcess.getTaskName(), userinfo);
				taskProcessService.deleteRelativeResource(idArray[i]);
				taskProcessService.deleteRelativeProcessor(idArray[i]);
				taskProcessService.delete(TaskProcess.class, new Long(idArray[i]));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return "redirect:/taskProcess/list.action";
	}



	/**
	 *
	 * @Title: toEdit
	 * @Description: 进入添加/编辑加工任务单页面
	 * @param @param model
	 * @param @param id
	 * @param @return
	 * @return String
	 * @throws
	 */
	@RequestMapping(value = baseUrl + "toEdit", method = {RequestMethod.GET})
	@Token(save = true)
	public String toEdit(Model model, @RequestParam("id") Long id) {
		logger.info("进入修改/添加系统参数页面");
		TaskProcess taskProcess= null;
		if(id > -1){
			model.addAttribute(Constants.ID,id);
			taskProcess = (TaskProcess) taskProcessService.getByPk(TaskProcess.class, id);
		}else{
			taskProcess = new TaskProcess();
		}
		setModelCommonValue(model);
		model.addAttribute("taskProcess", taskProcess);
		return "taskprocess/taskprocessEdit";
	}

	/**
	 *
	 * @Title: add
	 * @Description: 保存加工任务单
	 * @param @param model
	 * @param @param taskProcess
	 * @param @param response
	 * @return void
	 * @throws
	 */
	@RequestMapping(value = baseUrl + "add")
	@Token(remove=true)
	public void add(Model model, @ModelAttribute("command") TaskProcess taskProcess, HttpServletResponse response) {
		try {
			taskProcess.setPlatformId(userinfo.getPlatformId());
			taskProcess.setCreateTime(new Date());
			User user = new User();
			user.setId(userinfo.getUserId());
			taskProcess.setCreateUser(user);
			taskProcessService.save(taskProcess);
			SysOperateLogUtils.addLog("taskProcess_add", taskProcess.getTaskName(), userinfo);
		} catch (Exception e) {
			logger.error(e.getMessage());
			addActionError(e);
		}
	}


	/**
	 *
	 * @Title: update
	 * @Description:更新加工任务单
	 * @param @param model
	 * @param @param sysParameter
	 * @param @param response
	 * @return void
	 * @throws
	 */
   @RequestMapping(value = baseUrl + "update")
   @Token(remove=true)
   public void update(Model model, @ModelAttribute("command") TaskProcess taskProcess, HttpServletResponse response) {
		try {
			taskProcess.setPlatformId(userinfo.getPlatformId());
			taskProcess.setUpdateTime(new Date());
			User user = new User();
			user.setId(userinfo.getUserId());
			taskProcess.setUpdateUser(user);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String startTimeDesc = sdf.format(taskProcess.getStartTime());
			String endTimeDesc = sdf.format(taskProcess.getEndTime());
			taskProcess.setStartTimeDesc(startTimeDesc);
			taskProcess.setEndTimeDesc(endTimeDesc);
			taskProcessService.update(taskProcess);
			SysOperateLogUtils.addLog("taskProcess_update", taskProcess.getTaskName(), userinfo);
		} catch (Exception e) {
		    logger.error(e.getMessage());
		    addActionError(e);
		}
   }

	/**
	 *
	 * @Title: view
	 * @Description: 显示加工任务单明细
	 * @param @param model
	 * @param @param id
	 * @param @return
	 * @return String
	 * @throws
	 */
	@RequestMapping(value = baseUrl + "view")
	public String view(Model model, @RequestParam("id") Long id) {
		TaskProcess taskProcess = (TaskProcess) baseService.getByPk(TaskProcess.class, id);
		model.addAttribute("taskProcess", taskProcess);
		return "taskprocess/taskprocessDetail";
	}

	/**
	 *
	 * @Title: setModel
	 * @Description:  给Model设置值
	 * @param @param model
	 * @param @return
	 * @return Model
	 * @throws
	 */
	public void setModelCommonValue(Model model){
		model.addAttribute("priority2Desc",OperDbUtils.queryValueByKey("priority"));
//		model.addAttribute("status2Desc",OperDbUtils.queryValueByKey("taskProcessStatus"));
	}



	@RequestMapping(value = baseUrl + "validateTaskProcessName")
	public @ResponseBody String validateTaskProcessName(HttpServletRequest request){
		logger.info("进入查询方法");
		int platformId = userinfo.getPlatformId();
		String id = null;
		String paraKey = request.getParameter("fieldValue");
		return "{\"jsonValidateReturn\": [\"taskName\",true]}";
	}

	
	@RequestMapping(value = baseUrl + "addResource")
	@Token(save = true)	
	public String addResource(Model model, @RequestParam("id") Long id) {
		logger.info("进入修改/添加系统参数页面");
		model.addAttribute("taskProcessId", id);
		TaskProcess taskProcess = (TaskProcess) baseService.getByPk(TaskProcess.class, id);
		model.addAttribute("taskProcess", taskProcess);
		SysOperateLogUtils.addLog("taskProcess_addResAndProcessor", taskProcess.getTaskName(), userinfo);
//		model.addAttribute("makers", userService.getMakerList());
		return "taskprocess/taskDistributionEdit";
		//return "taskprocess/addResource";
	}

	@RequestMapping(value = baseUrl + "addMakers")
	public String addMakers(Model model,@RequestParam("id") Long id){
		logger.info("进入添加加工员页面");
		model.addAttribute("taskProcessId", id);
		model.addAttribute("makers", userService.getMakerList());
		return "taskprocess/addMakers";
	}
	
	@RequestMapping(value = baseUrl + "saveResource")
	@ResponseBody
	@Token(save = true)
	public synchronized  String saveResource(HttpServletRequest request){
//		String makerIds = request.getParameter("makers");
		String resourceIds = request.getParameter("resources");
		String taskProcessId = request.getParameter("taskProcessId");
		String result = "0";
		try {
			if(StringUtils.isNoneBlank(taskProcessId)){
				Long taskProcessIdForInt = Long.parseLong(taskProcessId);
				TaskProcess taskProcess = taskProcessService.getTaskProcessInfo(taskProcessIdForInt);
				List<String> resIdList = taskProcessService.getAllSysResDirectoryList(taskProcessIdForInt);
				taskProcessService.addResource(resourceIds,resIdList, taskProcess);
			}
		} catch (Exception e) {
			result = "-1";
			e.printStackTrace();
		}
		return result;
	}
	
	@RequestMapping(value = baseUrl + "saveMaker")
	@ResponseBody	
	public String saveMaker(HttpServletRequest request){
		String result = "0";
		String makerIds = request.getParameter("makers");
		String taskProcessId = request.getParameter("taskProcessId");
		try {
			taskProcessService.addMakers(makerIds, taskProcessId);
		} catch (Exception e) {
			result = "-1";
			e.printStackTrace();
		}		
		return result;
	}
	
	@RequestMapping(baseUrl + "deleteTaskResource")
	@ResponseBody	
	public String deleteTaskResource(@RequestParam("id") Long id){
		String result = "0";
		try {
			deleteTaskResourceById(id);
		} catch (Exception e) {
			e.printStackTrace();
			result = "-1";
		}
		return result;
	}

	private void deleteTaskResourceById(Long id) {
		TaskResRelation taskResRelation = (TaskResRelation) taskProcessService.getByPk(
				TaskResRelation.class, id);
		TaskDetail taskDetail = (TaskDetail) taskProcessService.getByPk(
				TaskDetail.class, taskResRelation.getTaskDetail().getId());
		taskProcessService.delete(taskDetail);
		taskProcessService.delete(taskResRelation);
	}
	
	@RequestMapping(baseUrl + "batchDeleteTaskResource")
	@ResponseBody	
	public String batchDeleteTaskResource(@RequestParam("ids") String ids){
		String result = "0";
		try{
			for(String id : ids.split(",")){
				deleteTaskResourceById(Long.parseLong(id));
			}
		}catch(Exception e){
			result = "-1";
		}
		return result;
	}
	
	@RequestMapping(baseUrl + "getMetaDataIds")
	@ResponseBody	
	public String getMetaDataIds(@RequestParam("ids") String ids, @RequestParam("publishType") String publishType,@RequestParam("status") String status){
		
		if (!"0".equals(status) && !"1".equals(status) && !"2".equals(status)) {
			status = "";
		}
		String metaDataIds = taskProcessService.getResIdsByIdAndPublishType(ids, publishType,status);
		
		/*		for(String id : ids.split(",")){
			TaskResRelation trr = (TaskResRelation) taskProcessService.getByPk(
					TaskResRelation.class, Long.parseLong(id));
			String resType = trr.getPublishType();
			if(StringUtils.isNotEmpty(publishType)){
				if(publishType.equals(resType)){
					metaDataIds.append(trr.getSysResDirectoryId() + ",");
				}
			}
		}*/
		//SysOperateLogUtils.addLog("taskProcessDetail_exportMetadata", userinfo.getUsername(), userinfo);
		return metaDataIds;
	}
	@RequestMapping(baseUrl + "getMetaDataIdsByPage")
	@ResponseBody	
	public String getMetaDataIdsByPage(@RequestParam("page") String page , @RequestParam("page1") String page1 ,
			@RequestParam("pageSize") String pageSize , @RequestParam("resName") String resName , 
			@RequestParam("userName") String userName , @RequestParam("taskName") String taskName , 
			@RequestParam("publishType") String publishType){
		
		String metaDataIds = taskProcessService.getResIdsByPage(page, page1, pageSize, resName, userName, taskName, publishType);
		return metaDataIds;
	}
	
	@RequestMapping(value = baseUrl + "resourceDetail")
	@ResponseBody
	public String resourceDetail(String resId){
		logger.info("resId+++++++++++++++++ " + resId);
		if(StringUtils.isEmpty(resId)){
			return "";
		}else{
			return taskProcessService.resourceDetail(resId);
		}
	}
	
	@RequestMapping(value = baseUrl + "queryResourcesByTaskIdAndProcessor")
	@ResponseBody
	public String queryResourcesByTaskIdAndProcessor(HttpServletRequest request, HttpServletResponse response){
		String page = (String)request.getParameter("page");
		String size = (String)request.getParameter("rows");
		String taskId = (String)request.getParameter("taskId");
		String processorId = (String)request.getParameter("userId");
		return taskProcessService.queryResourcesByTaskIdAndProcessor(taskId, processorId, page, size);
	}
	
	@RequestMapping(value = baseUrl + "deleteResourceByTaskIdAndResDetailId")
	@ResponseBody
	public void deleteResourceByTaskIdAndResDetailId(Long taskId, String resDetailIds) {
		if(taskId!=null&&StringUtils.isNotEmpty(resDetailIds)){
			taskProcessService.deleteResourceByTaskIdAndResDetailId(taskId, resDetailIds);
		}
	}
	
	@RequestMapping(value = baseUrl + "batchDeleteResourceByTaskIdAndResDetailIds")
	@ResponseBody
	public void batchDeleteResourceByTaskIdAndResDetailIds(Long taskId, String resDetailIds) {
		if(taskId!=null&&StringUtils.isNotEmpty(resDetailIds)){
			String[] arrs = resDetailIds.split(",");
			for(String id : arrs){
				taskProcessService.deleteResourceByTaskIdAndResDetailId(taskId, id);
			}
		}
	}
	
	@RequestMapping(value = baseUrl + "deleteAllResByTaskId")
	@ResponseBody
	public void deleteAllResByTaskId(Long taskId){
		List<TaskDetail> detailList = taskProcessService.getTaskDetailByTaskIdAndStatus(taskId, 0);
		if(detailList!=null){
			for(TaskDetail detail: detailList){
				Long detailId = detail.getId();
				if(detailId!=null){
					deleteResourceByTaskIdAndResDetailId(taskId, detailId.toString());
				}
			}
		}
	}
	
	@RequestMapping(value = baseUrl + "queryDistributedProcessors")
	@ResponseBody
	public PageResult queryDistributedProcessors(Long taskId) {
		QueryConditionList conditionList = getQueryConditionList();
		if(taskId!=null){
			QueryConditionItem item = new QueryConditionItem();
			item.setFieldName("taskProcess.id");
			item.setOperator(Operator.EQUAL);
			item.setValue(taskId);
			conditionList.addCondition(item);			
		}
		return taskProcessService.query4Page(TaskProcessorRelation.class, conditionList);
	}
	
	@RequestMapping(value = baseUrl + "delProcessor")
	@ResponseBody
	public void delProcessor(Long taskId, String processorIds){
		taskProcessService.delProcessor(taskId, processorIds);
	}
	
	@RequestMapping(value = baseUrl + "averageDist")
	@ResponseBody
	public String averageDist(Long taskId , String processorIds){
		/*String flag = "success";
		List<TaskDetail> taskDetailList = taskProcessService.getTaskDetailByTaskIdAndStatus(taskId, null);
		List<TaskProcessorRelation> taskProcessorList = taskProcessService.getTaskProcessorRelationByTaskId(taskId);
		int len = taskDetailList.size();
		if(taskProcessorList!=null){
			int proNum = taskProcessorList.size();
			int avgNum = len/proNum;
			int temp = 0;
			int userCounter = 0;
			for(int counter=0; counter<len; counter++){
				if(temp>=avgNum){
					userCounter++;
					temp = 0;
				}
				if(userCounter>=proNum){
					userCounter = proNum-1;
				}
				TaskProcessorRelation tp = taskProcessorList.get(userCounter);
				User user = tp.getProcessor();
				TaskDetail taskDetail = taskDetailList.get(counter);
				taskDetail.setUser(user);
				taskDetail.setStatus(Integer.valueOf(1));
				taskProcessService.saveOrUpdate(taskDetail);
				TaskResRelation tr = taskProcessService.getResListByDetailId(taskDetail.getId());
				tr.setStatus(Integer.valueOf(1));
				taskProcessService.saveOrUpdate(tr);
				temp++;
			}
		}
		return flag;*/
		String flag = "success";
		//该任务单中未分配的资源
		List<TaskDetail> taskList = taskProcessService.getTaskDetailByTaskIdAndStatus(taskId, 0);
		//
		//List<TaskProcessorRelation> processorList = taskProcessService.getTaskProcessorRelationByTaskId(taskId);
		List<TaskProcessorRelation> processorList = taskProcessService.getRelationByTaskIdAndProcessorIds(taskId,processorIds);
		taskProcessService.saveAverageDist(taskList, processorList);
		return flag;
	}
	
	public void writeLogFile(String pathName, String logDir, User user){
		FileWriter fileWriter = null;
		java.io.File logFile = new java.io.File(logDir);
		if(!logFile.exists()){
			logFile.mkdirs();
		}
		try {
			fileWriter = new FileWriter(pathName, true);
			fileWriter.write(user.getLoginName() + "\r\n");
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = baseUrl + "getProcessorNum")
	@ResponseBody
	public int getProcessorNum(Long taskId){
		List<TaskProcessorRelation> taskProcessorList = taskProcessService.getTaskProcessorRelationByTaskId(taskId);
		if(taskProcessorList!=null){
			return taskProcessorList.size();
		}else{
			return 0;
		}
	}
	
	@RequestMapping(value = baseUrl + "distributedResToProcessor")
	@ResponseBody
	public String distributedResToProcessor(Long taskId, Long processorId, String taskDetailIds){
		if(StringUtils.isNotEmpty(taskDetailIds)){
			String[] detailIds = taskDetailIds.split(",");
			for(String id: detailIds){
				Long detailId = Long.valueOf(id);
				TaskDetail taskDetail = (TaskDetail) taskProcessService.getByPk(TaskDetail.class, detailId);
				User processor = new User();
				processor.setId(processorId);
				taskDetail.setUser(processor);
				taskDetail.setStatus(1);
				TaskResRelation taskResRelation = taskProcessService.getResListByDetailId(detailId);
				taskResRelation.setStatus(1);
				taskProcessService.saveOrUpdate(taskDetail);
				taskProcessService.saveOrUpdate(taskResRelation);
			}
		}
		return "success";
	}
	
	@RequestMapping(value = baseUrl + "distributedResToProcessorByNum")
	@ResponseBody
	public String distributedResToProcessorByNum(Long taskId, Long processorId, Long resNum){
		if(resNum!=null&&resNum>0){
			List<TaskDetail> taskDetailList = taskProcessService.getTaskDetailByTaskIdAndStatus(taskId, Integer.valueOf(0));
			if(taskDetailList!=null){
				//for(TaskDetail taskDetail: taskDetailList){
				for(int i=0;i<resNum;i++){
					TaskDetail taskDetail = taskDetailList.get(i);
					Long taskDetailId = taskDetail.getId();
					User processor = new User();
					processor.setId(processorId);
					taskDetail.setUser(processor);
					taskDetail.setStatus(1);
					TaskResRelation taskResRelation = taskProcessService.getResListByDetailId(taskDetailId);
					taskResRelation.setStatus(1);
					taskProcessService.saveOrUpdate(taskDetail);
					taskProcessService.saveOrUpdate(taskResRelation);
				}
			}
		}
		return "success";
	}
	
	@RequestMapping(value = baseUrl + "getTaskResPkByTaskIdAndProcessorIds")
	@ResponseBody
	public String getTaskResPkByTaskIdAndProcessorIds(Long taskId,
			String processorIds) {
		String taskResPks = taskProcessService.getTaskResPkByTaskIdAndProcessorIds(taskId, processorIds);
		return taskResPks;
	}
	
	@RequestMapping(value = baseUrl + "copyProcessFiles")
	@ResponseBody
	public String copyProcessFiles(Long taskId, String processorIds){
		taskProcessService.copyProcessFiles(taskId, processorIds);
		return "success";
	}
	
	@RequestMapping(value = baseUrl + "saveAllResByCondition")
	@ResponseBody
	public String saveAllResByCondition(Long taskId, String conditions){
		taskProcessService.saveAllResByCondition(taskId, conditions);
		return "sucess";
	}
	
	@RequestMapping(value = baseUrl + "getPublishTypeDescByPublishType")
	@ResponseBody
	public String getPublishTypeDescByPublishType(String publishType){
		String typeDesc = "";
		Object obj = SysResTypeCacheMap.getValue(publishType);
		if(obj!=null){
			typeDesc = obj.toString();
		}
		return typeDesc;
	}
	
	
	@RequestMapping(value = baseUrl + "queryRelationByTaskIdAndProcessor")
	@ResponseBody
	public PageResult queryRelationByTaskIdAndProcessor(HttpServletRequest request, HttpServletResponse response){
		
		QueryConditionList conditionList = getQueryConditionList();
		
		String taskId = (String)request.getParameter("taskId");
		String processorId = (String)request.getParameter("processorId");
		
		if (StringUtils.isNoneBlank(taskId)) {
			conditionList.addCondition(new QueryConditionItem("taskDetail.taskProcess.id", Operator.EQUAL, Long.valueOf(taskId)));
		}
		if (StringUtils.isNoneBlank(processorId)) {
			conditionList.addCondition(new QueryConditionItem("taskDetail.user.id", Operator.EQUAL, Long.valueOf(processorId)));
		}
		
		PageResult pageResult = taskProcessService.query4Page(TaskResRelation.class, conditionList);
		
		return pageResult;
	}
	
	
	@RequestMapping(value = baseUrl + "removeResToProcessor")
	@ResponseBody
	public String removeResToProcessor(HttpServletRequest request, HttpServletResponse response){
		String taskId = request.getParameter("taskId");
		String processorId = (String)request.getParameter("processorId");
		String resIds = (String)request.getParameter("resIds");
		String result = taskProcessService.removeResToProcessor(taskId,processorId,resIds);
		return result;
	}
}
