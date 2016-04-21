package com.brainsoon.resource.action;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.date.DateUtil;
import com.brainsoon.jbpm.constants.ProcessConstants;
import com.brainsoon.resource.po.ModifyLog;
import com.brainsoon.resource.service.IAreaService;
import com.brainsoon.resource.service.IBaseSemanticSerivce;
import com.brainsoon.resource.service.ICollectResService;
import com.brainsoon.resource.service.IPublishResService;
import com.brainsoon.resource.service.IPubresWorkFlowService;
import com.brainsoon.resource.service.IResWorkFlowService;
import com.brainsoon.resource.service.IResourceService;
import com.brainsoon.resource.service.impl.PubresWorkFlowService;
import com.brainsoon.resource.support.WorkFlowUtils;
import com.brainsoon.semantic.ontology.model.Asset;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.SearchParamCa;
import com.brainsoon.statistics.service.IEffectNumService;
import com.brainsoon.system.model.SysParameter;
import com.brainsoon.system.service.ISysParameterService;
import com.brainsoon.system.support.SysOperateLogUtils;
import com.brainsoon.system.support.SystemConstants;
import com.brainsoon.system.util.MetadataSupport;
import com.google.gson.Gson;

/**
 * 资源工作流Action
 * 
 * @author xujie
 */
@Controller
@RequestMapping("/pubres/wf/")
public class PubresWorkFlowAction extends BaseAction {

	@Autowired
	private IBaseSemanticSerivce baseSemanticSerivce;
	@Autowired
	private IPubresWorkFlowService publishResWorkFlowService;
	@Autowired
	private IPublishResService publishResService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private ICollectResService collectResService;
	@Autowired
	private ISysParameterService sysParameterService;
	private static final String BOOK_CREATE = "publish_save";
	private static final String BOOK_UPDATE = "publish_update";
	private final static String CONVERT_TXT_SUCCESSURL = WebappConfigUtil.getParameter("CONVERT_TXT_SUCCESSURL");

	/**
	 * 上报
	 * 
	 * @param asset
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping(value = "goDoApply", method = {RequestMethod.POST })
	@ResponseBody
	public String goDoApply(@RequestParam("doApply") String doApply) throws Exception {
		Gson gson = new Gson();
		SearchParamCa spc = gson.fromJson(doApply, SearchParamCa.class);
		String objectId = "";
		if(spc!=null){
			objectId = spc.getIds();
		}
		publishResWorkFlowService.doApply(objectId);
		return "上报成功！";
	}

	/**
	 * 跳转-审核页面（基于资源详细页面）
	 * 
	 * @param asset
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping("gotoCheck")
	public String gotoCheck(
			HttpServletRequest request,
			@RequestParam("objectId") String objectId,
			@RequestParam(value = "execuId", required = false) String execuId,
			@RequestParam(value = "wfTaskId", required = false) String wfTaskId,
			@RequestParam("operateFrom") String operateFrom, ModelMap model)
			throws Exception {
		if (StringUtils.isNotBlank(objectId)) {
			HttpClientUtil http = new HttpClientUtil();
			String goBackTask = request.getParameter("goBackTask");
			String status = request.getParameter("status");
			model.addAttribute("status", status);
			model.addAttribute("goBackTask", goBackTask);
			String resourceDetail = http.executeGet(WebappConfigUtil
					.getParameter("PUBLISH_DETAIL_URL") + "?id=" + objectId);
			Gson gson = new Gson();
			Ca bookCa = gson.fromJson(resourceDetail, Ca.class);
			model.put("detailFlag", "detail");
//			List<SysParameter> listYear = sysParameterService
//					.findParaValue("startYear");
//			List<SysParameter> listDays = sysParameterService
//					.findParaValue("days");
//			if (listYear != null && listYear.size() > 0 && listDays != null
//					&& listDays.size() > 0) {
//				String statusYear = listYear.get(0).getParaStatus().toString();
//				String statusDays = listDays.get(0).getParaStatus().toString();
//				if (statusYear.equals("1") && statusDays.equals("1")) {
//					String year = listYear.get(0).getParaValue();
//					String days = listDays.get(0).getParaValue();
//					DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
//					DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
//					Date date = null;
//					date = sdf.parse(year);
//					String date1 = format1.format(DateUtil.getNextDate(date,
//							Integer.parseInt(days)));
//					model.addAttribute("date1", date1);
//				}
//			}
			if(bookCa!=null){
				model = publishResService.jsonArray(bookCa, objectId, model);
			}
			model.addAttribute("bookCa", bookCa);
			model.addAttribute("objectId", objectId);
			if (bookCa.getMetadataMap().size() == 0) {
				return "/error/errorUnRes";
			}
			model.addAttribute("publishType", bookCa.getPublishType());

			if (StringUtils.isNotBlank(operateFrom)
					&& StringUtils.equals(operateFrom, "MANAGE_PAGE")) {
				Map<String, String> map = publishResWorkFlowService
						.getWorkFlowInfo(ProcessConstants.WFType.PUB_ORES_CHECK
								+ "." + objectId);
				execuId = map.get("execuId");
				wfTaskId = map.get("wfTaskId");
			}
			model.addAttribute("execuId", execuId);
			model.addAttribute("wfTaskId", wfTaskId);
		}
		model.addAttribute("operateFrom", operateFrom);
		return "/publishRes/createDetail";
	}

	/**
	 * 跳转到编辑页面(驳回编辑)
	 * 
	 * @param asset
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping("gotoEdit")
	public String gotoEdit(@RequestParam("objectId") String objectId, @RequestParam("execuId") String execuId, @RequestParam("wfTaskId") String wfTaskId,
			@RequestParam("operateFrom") String operateFrom, ModelMap model,HttpServletRequest request) throws Exception {
		model.addAttribute("objectId", objectId);
		model.addAttribute("execuId", execuId);
		model.addAttribute("wfTaskId", wfTaskId);
		model.addAttribute("operateFrom", operateFrom);
		String taskFlag = request.getParameter("taskFlag");
		model.addAttribute("taskFlag", taskFlag);
		if (StringUtils.isNotBlank(objectId)) {
			HttpClientUtil http = new HttpClientUtil();
			String resourceDetail = "";
			String targetNames ="";
			resourceDetail = http.executeGet(WebappConfigUtil
						.getParameter("PUBLISH_DETAIL_URL") + "?id=" + objectId);
			Gson gson = new Gson();
			Ca bookCa = gson.fromJson(resourceDetail, Ca.class);
			if (bookCa != null) {
				publishResService.jsonArray(bookCa, objectId, model);
				model.put("publishType", bookCa.getPublishType());
				String rootPath = bookCa.getRootPath();
				if(StringUtils.isNotBlank(rootPath)){
					rootPath = rootPath.replaceAll("\\\\", "/");
					model.put("rootPath", rootPath);
				}
				model.put("rootPath", rootPath);
				model.put("status", bookCa.getStatus());
				model.put("publishType", bookCa.getPublishType());
				model.put("taskFlagAddFile", "");
			}else{
				return "/error/errorUnRes";
			}
			if (bookCa.getMetadataMap()==null|| bookCa.getMetadataMap().size() == 0) {
				return "/error/errorUnRes";
			}
			model.put("bookCa", bookCa);
			model.put("targetNames", targetNames);
			model.put("objectId", objectId);
			model.put("modiFlag", "modiFlag");
		}
		return "/publishRes/create";
	}

	/**
	 * 审核(通过，驳回)
	 * 
	 * @param asset
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping(value = "doCheck", method = {RequestMethod.POST })
	@ResponseBody
	public String doCheck(@RequestParam("doCheck") String doCheck,@RequestParam(value = "wfTaskId", required = false) String wfTaskId) throws Exception {
		HttpServletRequest request = getRequest(); 
		Gson gson = new Gson();
		SearchParamCa spc = gson.fromJson(doCheck, SearchParamCa.class);
		String objectId = "";
		String decision ="";
		String checkOpinion = "";
		String operateFrom = "";
		if(spc != null){
			objectId = spc.getIds();
			decision = spc.getDecision();
			checkOpinion = spc.getCheckOpinion();
			operateFrom = spc.getOperateFrom();
		}
		String url = CONVERT_TXT_SUCCESSURL;
		publishResWorkFlowService.doCheck(objectId, wfTaskId, decision, checkOpinion,url);
		if (StringUtils.equals(operateFrom, "TASK_LIST_PAGE")) {
			return "/TaskAction/toList.action";
		} else {
			return "1";
		//	return "/publishRes/publishResList.action?publishType=" + publishType+"&status="+status;
		}
	}

	/**
	 * 下线操作
	 * 
	 * @param response
	 * @param objectId
	 */
	@RequestMapping("PubOfflineRes")
	@ResponseBody
	public String offlineRes(HttpServletResponse response, @RequestParam("objectId") String objectId) {
		String success = "0";
		publishResWorkFlowService.updateResStatus(objectId, SystemConstants.ResourceStatus.STATUS4);
		return success;
	}

	/**
	 * 恢复操作(恢复成功)
	 * 
	 * @param response
	 * @param objectId
	 */
	@RequestMapping("restoreRes")
	@ResponseBody
	public String restoreRes(HttpServletResponse response, @RequestParam("objectId") String objectId) {
		publishResWorkFlowService.updateResStatus(objectId, SystemConstants.ResourceStatus.STATUS3);
		return "恢复成功！";
	}

	/**
	 * 资源状态字典Map集合
	 * 
	 * @param status
	 * @return json
	 * @throws Exception
	 */
	@RequestMapping("getStatusMap")
	@ResponseBody
	public Map getStatusDesc() throws Exception {
		return SystemConstants.ResourceStatus.getMap();
	}

	/**
	 * 保存并提交
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @param asset
	 * @param uploadFile
	 */
	@RequestMapping("doSaveAndSubmit")
	@ResponseBody
	public String doSaveAndSubmit(HttpServletRequest request, HttpServletResponse response, 
			ModelMap model, 
			@RequestParam(value="operateFrom",required=false) String operateFrom,
			@ModelAttribute("frmAsset") Ca ca, 
			@RequestParam("wfTaskId") String wfTaskId) {
		try {
			operateFrom = request.getParameter("operateFrom");
			publishResService.updateCollectRes(ca, null);
			publishResWorkFlowService.doSaveAndSubmit(ca.getObjectId(), wfTaskId,ca.getPublishType());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			addActionError(e);
		}
		if (StringUtils.equals(operateFrom, "TASK_LIST_PAGE")) {
			return "/TaskAction/toList.action";
		} else {
			return "";
	//		return "/publishRes/publishResList.jsp?publishType=" + ca.getCommonMetaData().getPublishType();
		}
	}
	/**
	 * 已通过状态加解锁
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @param asset
	 * @param uploadFile
	 */
	 @RequestMapping(value = "gotoUnLock", method = {RequestMethod.POST })
	public @ResponseBody String gotoUnLock(HttpServletRequest request, HttpServletResponse response, 
			ModelMap model,@RequestParam("batchGoto") String batchGoto){
		 	Gson gson = new Gson();
			SearchParamCa spc = gson.fromJson(batchGoto, SearchParamCa.class);
			String id = "";
			String status = "";
			if(spc!=null){
				id = spc.getIds();
				status = spc.getStatus();
			}
		String stat = publishResWorkFlowService.updateUnLock(id,status);
		return stat;
	}
}
