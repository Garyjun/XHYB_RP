package com.brainsoon.statistics.action;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.action.Token;
import com.brainsoon.appframe.query.Operator;
import com.brainsoon.appframe.query.QueryConditionItem;
import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.appframe.support.PageResultForTNum;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.common.util.date.DateUtil;
import com.brainsoon.semantic.ontology.model.Statistics;
import com.brainsoon.statistics.service.ISourceProcessNumService;
import com.brainsoon.statistics.service.ITaskProcessNumService;
import com.brainsoon.system.model.MetaDataGroup;
import com.brainsoon.system.model.MetaDataModelGroup;
import com.brainsoon.system.support.SysOperateLogUtils;
import com.brainsoon.taskprocess.model.TaskDetail;
import com.brainsoon.taskprocess.model.TaskResRelation;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SourceProcessNumAction extends BaseAction {
	/** 默认命名空间 **/
	private final String baseUrl = "/statistics/sourceProcessNum/";
	UserInfo userInfo =  LoginUserUtil.getLoginUser();
	private static  List<TaskResRelation> allRecords = new ArrayList<TaskResRelation>();
	private static int total = 0;
	@Autowired
	private ISourceProcessNumService sourceProcessNumService;
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
		
		String taskName = request.getParameter("taskName");
		String resName = request.getParameter("resName");
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		String processName = request.getParameter("processName");
		String status = request.getParameter("status");
		if(StringUtils.isNotBlank(resName)){
			resName = URLDecoder.decode(request.getParameter("resName"), "UTF-8");
			conditionList.addCondition(new QueryConditionItem("resName", Operator.LIKE,"%"+resName+"%"));
		}
		if(StringUtils.isNotBlank(taskName)){
			taskName = URLDecoder.decode(request.getParameter("taskName"), "UTF-8");
			conditionList.addCondition(new QueryConditionItem("taskDetail.taskProcess.taskName", Operator.LIKE,"%"+URLDecoder.decode(taskName, "UTF-8")+"%"));
		}
		if(StringUtils.isNotBlank(status)){
			status = URLDecoder.decode(request.getParameter("status"), "UTF-8");
			if("0".equals(status)){
				conditionList.addCondition(new QueryConditionItem("taskDetail.status", Operator.NOTEQUAL,2));
			}else{
				conditionList.addCondition(new QueryConditionItem("taskDetail.status", Operator.EQUAL,Integer.valueOf(status)));
			}
			
		}
		if(StringUtils.isNotBlank(processName)){
			processName = URLDecoder.decode(request.getParameter("processName"), "UTF-8");
			conditionList.addCondition(new QueryConditionItem("taskDetail.user.userName", Operator.LIKE,"%"+processName+"%"));
		}
		if (StringUtils.isNotBlank(startTime)) {
			conditionList.addCondition(new QueryConditionItem("taskDetail.startTime",  Operator.GT, DateUtil.convertStringToDate("yyyy-MM-dd", startTime)));
		}
		if (StringUtils.isNotBlank(endTime)) {
			conditionList.addCondition(new QueryConditionItem("taskDetail.endTime",Operator.LT, DateUtil.convertStringToDate("yyyy-MM-dd", endTime)));
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
					conditionList.addCondition(new QueryConditionItem("publishType", Operator.IN, resTypes));
				}
				
			}
			String userIds = userInfo.getDeptUserIds();
			int isPrivate = userInfo.getIsPrivate();
			if(1==isPrivate){
				if(StringUtils.isNotBlank(userIds)){
					if(StringUtils.isNotEmpty(userIds)){
						String[] ids = userIds.substring(0, userIds.length()-1).split(",");
						Long[] idArr = new Long[ids.length];
						for(int i=0;i<ids.length;i++){
							idArr[i] = Long.valueOf(ids[i]);
						}
			    		conditionList.addCondition(new QueryConditionItem("taskDetail.taskProcess.createUser.id", Operator.IN, userIds.substring(0, userIds.length()-1)));
					}
				}else{
					conditionList.addCondition(new QueryConditionItem("taskDetail.taskProcess.createUser.id", Operator.EQUAL, userInfo.getUserId()));
				}
			}else{
				if(StringUtils.isNotBlank(userIds)){
					conditionList.addCondition(new QueryConditionItem("taskDetail.taskProcess.createUser.id", Operator.IN, userIds.substring(0, userIds.length()-1)));
				}else{
					conditionList.addCondition(new QueryConditionItem("taskDetail.taskProcess.createUser.id", Operator.IN, "-2"));
				}
			}
		}
		
		PageResult pageResult = sourceProcessNumService.query4Page(TaskResRelation.class, conditionList);
		allRecords = pageResult.getRows();
		total = allRecords.size();
		return pageResult;
	}
	
	/**
	 * 资源加工统计导出下载
	 * 
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = baseUrl + "exportRes")
	@ResponseBody
	public String exportRes(@RequestParam(value="ids",required=false) String ids) throws IOException {
		List<TaskResRelation> datas = new ArrayList<TaskResRelation>();
		if (StringUtils.isNotBlank(ids)) {
			String[] idArr = StringUtils.split(ids,",");
			System.out.println(idArr);
				for (String id : idArr) {
					Long id1 = Long.parseLong(id); 
					TaskResRelation taskResRelation = (TaskResRelation)sourceProcessNumService.getByPk(TaskResRelation.class, id1);
					int statusFlag = taskResRelation.getTaskDetail().getStatus();
					if(0==statusFlag) {
						taskResRelation.setStatusDesc("未加工");
					}else if(1==statusFlag) {
						taskResRelation.setStatusDesc("未加工");
					}else if(2==statusFlag) {
						taskResRelation.setStatusDesc("已完成");
					}
					datas.add(taskResRelation);
					SysOperateLogUtils.addLog("sourceProcess_exportRes",taskResRelation.getResName(), userInfo);
				}
			}
		return sourceProcessNumService.exportRes(datas).getAbsolutePath();
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
		List<TaskResRelation> idList = sourceProcessNumService.dotaskId();
		JSONArray array = new JSONArray();
		for (TaskResRelation taskResRelation : idList) {
			JSONObject json = new JSONObject();
			json.put("id", taskResRelation.getId());
			array.add(json);
		}
		return array.toString();
	}
	
	/**
	 * 查询出相对应的数据并创建好excel
	 * @param resName
	 * @param status
	 * @param taskName
	 * @param processName
	 * @param startTime
	 * @param endTime
	 * @param pageSize
	 * @param page
	 * @param page1
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(baseUrl+"getExportByPage")
	@ResponseBody
	public String getExportByPage(@RequestParam(value="resName", required = false) String resName,
			@RequestParam(value="status", required = false) String status,@RequestParam(value="taskName", required = false) String taskName,
			@RequestParam(value="processName", required = false) String processName,@RequestParam(value="startTime", required = false) String startTime,
			@RequestParam(value="endTime", required = false) String endTime,@RequestParam(value="pageSize", required = false) String pageSize,
			@RequestParam(value="page", required = false) String page,@RequestParam(value="page1", required = false) String page1) throws Exception{
		List<TaskResRelation> taskResList = new ArrayList<TaskResRelation>();
		try{
			 taskResList = sourceProcessNumService.findProcessByPage(resName, status, taskName, processName, startTime, endTime, pageSize, page, page1);
		}catch(Exception e){
			e.printStackTrace();
		}
		if(taskResList.size()>0 && taskResList!=null){
			for (TaskResRelation taskResRelation : taskResList) {
				Long id = taskResRelation.getId();
				TaskResRelation taskResRelations = (TaskResRelation)sourceProcessNumService.getByPk(TaskResRelation.class, id);
				int statusFlag = taskResRelations.getTaskDetail().getStatus();
				if(0==statusFlag) {
					taskResRelations.setStatusDesc("未加工");
				}else if(1==statusFlag) {
					taskResRelations.setStatusDesc("未加工");
				}else if(2==statusFlag) {
					taskResRelations.setStatusDesc("已完成");
				}
				SysOperateLogUtils.addLog("sourceProcess_exportRes",taskResRelations.getResName(), userInfo);
			}
		}
		return  sourceProcessNumService.exportRes(taskResList).getAbsolutePath();
	}
	
	/**
	 * 对已创建好的excel进行导出
	 * 
	 * @return
	 * @throws Exception 
	 */
    @RequestMapping(baseUrl + "getExportExcelDown")
	@ResponseBody
	public ResponseEntity<byte[]> getExportExcelDown(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String excelFilePath = request.getParameter("excelFilePath");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		String filename = URLEncoder.encode("资源加工统计.xls", "UTF-8");
		headers.setContentDispositionFormData("attachment", filename);
		File excelFile = new File(excelFilePath);
		return new ResponseEntity<byte[]>(
				FileUtils.readFileToByteArray(excelFile), headers,
				HttpStatus.OK);
	}

}
