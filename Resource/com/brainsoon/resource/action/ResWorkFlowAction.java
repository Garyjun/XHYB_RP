package com.brainsoon.resource.action;

import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.crypto.Data;

import jxl.format.Format;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.resource.service.IAreaService;
import com.brainsoon.resource.service.IBaseSemanticSerivce;
import com.brainsoon.resource.service.ICollectResService;
import com.brainsoon.resource.service.IPublishResService;
import com.brainsoon.resource.service.IPubresWorkFlowService;
import com.brainsoon.resource.service.IResWorkFlowService;
import com.brainsoon.resource.service.IResourceService;
import com.brainsoon.resource.support.WorkFlowUtils;
import com.brainsoon.semantic.ontology.model.Asset;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.ResBaseObject;
import com.brainsoon.system.model.SysParameter;
import com.brainsoon.system.service.ISysParameterService;
import com.brainsoon.system.support.SystemConstants;
import com.brainsoon.system.support.SystemConstants.ResourceMoudle;
import com.google.gson.Gson;
import com.brainsoon.common.support.Constants;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.date.DateUtil;

/**
 * 资源工作流Action
 * 
 * @author xujie
 */
@Controller
@RequestMapping("/res/wf/")
public class ResWorkFlowAction extends BaseAction {
	
	@Autowired
	private IResourceService resourceService;
	@Autowired
	private IBaseSemanticSerivce baseSemanticSerivce;
	@Autowired
	private IResWorkFlowService resWorkFlowService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private ICollectResService collectResService;
	@Autowired
	private ISysParameterService sysParameterService;
	@Autowired
	private IPublishResService publishResService;
	@Autowired
	private IPubresWorkFlowService publishResWorkFlowService;
	/**
	 * 上报
	 * 
	 * @param asset
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping("doApply")
	@ResponseBody
	public String doApply(
			@RequestParam("objectId") String objectId, 
			@RequestParam("libType") String libType){
		String msg = "0";
		try{
			resWorkFlowService.doApply(objectId, libType);
		}catch(Exception e){
			msg = e.getMessage();
			logger.debug(e);
		}
		return msg;
	}

	/**
	 * 跳转-审核页面（基于资源详细页面）
	 * 
	 * @param asset
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping("gotoCheck")
	public String gotoCheck(HttpServletRequest request,
			@RequestParam("objectId") String objectId, 
			@RequestParam(value = "execuId", required = false) String execuId,
			@RequestParam(value = "wfTaskId", required = false) String wfTaskId, 
			@RequestParam("operateFrom") String operateFrom, 
			@RequestParam("libType") String libType, 
			ModelMap model)
			throws Exception {
		String goBackTask = request.getParameter("goBackTask");
		model.addAttribute("goBackTask", goBackTask);
		List<SysParameter> listYear = sysParameterService.findParaValue("startYear");
		List<SysParameter> listDays = sysParameterService.findParaValue("days");
		if(listYear!=null && listYear.size()>0 && listDays!=null && listDays.size()>0){
			String statusYear = listYear.get(0).getParaStatus().toString();
			String statusDays = listDays.get(0).getParaStatus().toString();
			if(statusYear.equals("1")&&statusDays.equals("1")){
			String year = listYear.get(0).getParaValue();
			String days = listDays.get(0).getParaValue();
			DateFormat sdf=new SimpleDateFormat("yyyy/MM/dd");
			DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
			Date date=null;
			date = sdf.parse(year);
			String date1 = format1.format(DateUtil.getNextDate(date,  Integer.parseInt(days)));
			model.addAttribute("date1", date1);
			}
		}
		String dest = "";
		String queryType = request.getParameter("queryType");
		model.addAttribute("queryType", queryType);
		model.addAttribute("provinces", areaService.getProvince());
		model.addAttribute("objectId", objectId);
		model.addAttribute("operateFrom", operateFrom);
		model.addAttribute("libType", libType);
		
		if (StringUtils.isNotBlank(objectId)) {
			// 原始资源、标准资源
			if (StringUtils.equals(libType, SystemConstants.LibType.ORES_TYPE) || StringUtils.equals(libType, SystemConstants.LibType.BRES_TYPE)) {
				String objectJson = "";
				ResBaseObject res = null;
				if(objectId.indexOf("book")>0){
					HttpClientUtil http = new HttpClientUtil();
					Gson gson = new Gson();
					objectJson = http.executeGet(WebappConfigUtil.getParameter("CA_DETAIL_URL") + "?id=" + objectId);
					model.addAttribute("resourceDetail", objectJson);
					Ca ca = gson.fromJson(objectJson, Ca.class);
					if(ca.getCommonMetaData()==null){
						return "/error/errorUnRes";
					}
					model.addAttribute("module", ca.getCommonMetaData().getModule());
					model.addAttribute("type", ca.getType());
					model.addAttribute("status", ca.getCommonMetaData().getStatus());
					dest = "/bookRes/bookResDetail";
				}else{
					objectJson = baseSemanticSerivce.getResourceDetailById(objectId);
					model.addAttribute("resourceDetail", objectJson);
					Gson gson = new Gson();
					Asset asset = gson.fromJson(objectJson, Asset.class);
					if(asset.getCommonMetaData()==null){
						return "/error/errorUnRes";
					}
					model.addAttribute("module", asset.getCommonMetaData().getModule());
					model.addAttribute("type", asset.getType());
					model.addAttribute("status", asset.getCommonMetaData().getStatus());
					dest = "/bres/detail";
				}
			} else {
				// 应用资源
				HttpClientUtil http = new HttpClientUtil();
				String resourceDetail = http.executeGet(WebappConfigUtil.getParameter("CA_DETAIL_URL") + "?id=" + objectId);
				Gson gson = new Gson();
				Ca ca = gson.fromJson(resourceDetail, Ca.class);
				if(ca.getCommonMetaData()==null){
					return "/error/errorUnRes";
				}
				model.addAttribute("ca", ca);
				model.addAttribute("resourceDetail", resourceDetail);
				model.addAttribute("module", ca.getCommonMetaData().getModule());
				String region = ca.getCommonMetaData().getCommonMetaDatas().get("region");
				if (!region.equals("")) {
					String regionInfo = areaService.getRegionInfo(region);
					model.addAttribute("regionInfo", regionInfo);
				}
				dest = "/collectRes/collectResDetail";
			}

			if (StringUtils.isNotBlank(operateFrom) && StringUtils.equals(operateFrom, "MANAGE_PAGE")) {
				Map<String, String> map = resWorkFlowService.getWorkFlowInfo(WorkFlowUtils.getExecuId(objectId, libType));
				execuId = map.get("execuId");
				wfTaskId = map.get("wfTaskId");
			}
			model.addAttribute("execuId", execuId);
			model.addAttribute("wfTaskId", wfTaskId);
		}

		return dest;
	}
	
	

	/**
	 * 跳转到编辑页面(驳回编辑)
	 * 
	 * @param asset
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping("gotoEdit")
	public String gotoEdit(
			@RequestParam("objectId") String objectId, 
			@RequestParam("execuId") String execuId, 
			@RequestParam("wfTaskId") String wfTaskId,
			@RequestParam("operateFrom") String operateFrom,
			@RequestParam("libType") String libType,
			ModelMap model) throws Exception {
		String dest = "";
		model.addAttribute("provinces", areaService.getProvince());
		model.addAttribute("objectId", objectId);
		model.addAttribute("execuId", execuId);
		model.addAttribute("wfTaskId", wfTaskId);
		model.addAttribute("operateFrom", operateFrom);
		model.addAttribute("libType", libType);
		if (StringUtils.isNotBlank(objectId)) {
			// 原始资源、标准资源
			if (StringUtils.equals(libType, SystemConstants.LibType.ORES_TYPE) || StringUtils.equals(libType, SystemConstants.LibType.BRES_TYPE)) {
				String objectJson = "";
				ResBaseObject res = null;
				if(objectId.indexOf("book")>0){
					HttpClientUtil http = new HttpClientUtil();
					Gson gson = new Gson();
					objectJson = http.executeGet(WebappConfigUtil.getParameter("CA_DETAIL_URL") + "?id=" + objectId);
					model.addAttribute("resourceDetail", objectJson);
					Ca ca = gson.fromJson(objectJson, Ca.class);
					if(ca.getCommonMetaData()==null){
						return "/error/errorUnRes";
					}
					model.addAttribute("module", ca.getCommonMetaData().getModule());
					model.addAttribute("type", ca.getType());
					model.addAttribute("status", ca.getCommonMetaData().getStatus());
					dest = "/bookRes/bookResEdit";
				}else{
					objectJson = baseSemanticSerivce.getResourceDetailById(objectId);
					model.addAttribute("resourceDetail", objectJson);
					Gson gson = new Gson();
					Asset asset = gson.fromJson(objectJson, Asset.class);
					if(asset.getCommonMetaData()==null){
						return "/error/errorUnRes";
					}
					model.addAttribute("module", asset.getCommonMetaData().getModule());
					model.addAttribute("type", asset.getType());
					model.addAttribute("status", asset.getCommonMetaData().getStatus());
					dest = "/bres/edit";
				}
			}else{
				// 应用资源
				HttpClientUtil http = new HttpClientUtil();
				String resourceDetail = http.executeGet(WebappConfigUtil.getParameter("CA_DETAIL_URL") + "?id=" + objectId);
				Gson gson = new Gson();
				Ca ca =  gson.fromJson(resourceDetail, Ca.class);
				if(ca.getCommonMetaData()==null){
					return "/error/errorUnRes";
				}
				model.addAttribute("resourceDetail", resourceDetail);
				model.addAttribute("module", ca.getCommonMetaData().getModule());
				model.addAttribute("status", ca.getCommonMetaData().getStatus());
				dest = "/collectRes/collectResEdit";
			}
		}
		return dest;
	}

	/**
	 * 审核(通过，驳回)
	 * 
	 * @param asset
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping("doCheck")
	@ResponseBody
	public String doCheck(HttpServletRequest request,
			@RequestParam("objectId") String objectId, 
			@RequestParam("operateFrom") String operateFrom, 
			@RequestParam("decision") String decision,
			@RequestParam(value = "wfTaskId", required = false) String wfTaskId, 
			@RequestParam("libType") String libType) throws Exception {
		
		String goBackTask= request.getParameter("goBackTask");
		String type= request.getParameter("type");
		String module= request.getParameter("module");
		String queryType= request.getParameter("queryType");
		String str = URLDecoder.decode(request.getParameter("checkOpinion"),"UTF-8");
		JSONObject jb=new JSONObject();
		String checkOpinion=(String)jb.fromObject(str).get("checkOpinion"); 
		resWorkFlowService.doCheck(objectId, libType, wfTaskId, decision, URLDecoder.decode(checkOpinion, "UTF-8"));
		if (!goBackTask.equals("")) {
			return "/TaskAction/toList.action";
		} else {
			if(objectId.indexOf("book")>0){
				return "/bookRes/bookResList.jsp?libType=" + libType;
			}
			if (StringUtils.equals(libType, SystemConstants.LibType.ORES_TYPE) || StringUtils.equals(libType, SystemConstants.LibType.BRES_TYPE)) {
				return "/bres/list.jsp?libType=" + libType;
			}else{
				return "/collectRes/collectResList.jsp?libType="+libType+"&type="+type+"&queryType="+queryType+"&module="+module;
			}
		}
	}

	/**
	 * 下线操作
	 * 
	 * @param response
	 * @param objectId
	 */
	@RequestMapping("offlineRes")
	@ResponseBody
	public String offlineRes(
			HttpServletResponse response, 
			@RequestParam("objectId") String objectId) {
		String success = "0";
		resWorkFlowService.updateResStatus(objectId, SystemConstants.ResourceStatus.STATUS4);
		return success;
	}

	/**
	 * 恢复操作(审核通过)
	 * 
	 * @param response
	 * @param objectId
	 */
	@RequestMapping("restoreRes")
	@ResponseBody
	public String restoreRes(
			HttpServletResponse response, 
			@RequestParam("objectId") String objectId) {
		resWorkFlowService.updateResStatus(objectId, SystemConstants.ResourceStatus.STATUS3);
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
	public String doSaveAndSubmit(HttpServletRequest request, HttpServletResponse response,ModelMap model,Ca ca,String uploadFile)
			throws Exception {
		String repeatType = request.getParameter("repeatType");
		String targetNames = request.getParameter("targetNames");
		String publishType = request.getParameter("publishType");
		String creatTime = request.getParameter("creatTime");
		String creator = request.getParameter("creator");
		ca.setCreator(creator);
		ca.setCreateTime(creatTime);
		String objectId = publishResService.savePublishRes(ca, uploadFile,repeatType,publishType,targetNames);
		publishResWorkFlowService.doApply(objectId);
//		List<SysParameter> listYear = sysParameterService.findParaValue("startYear");
//		List<SysParameter> listDays = sysParameterService.findParaValue("days");
//		if(listYear!=null && listYear.size()>0 && listDays!=null && listDays.size()>0){
//			String statusYear = listYear.get(0).getParaStatus().toString();
//			String statusDays = listDays.get(0).getParaStatus().toString();
//			if(statusYear.equals("1")&&statusDays.equals("1")){
//			String year = listYear.get(0).getParaValue();
//			String days = listDays.get(0).getParaValue();
//			DateFormat sdf=new SimpleDateFormat("yyyy/MM/dd");
//			DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
//			Date date=null;
//			date = sdf.parse(year);
//			String date1 = format1.format(DateUtil.getNextDate(date,  Integer.parseInt(days)));
//			model.addAttribute("date1", date1);
//			}
//		}
//		String dest = "";
//		String queryType = request.getParameter("queryType");
//		model.addAttribute("queryType", queryType);
//		model.addAttribute("provinces", areaService.getProvince());
//		model.addAttribute("objectId", objectId);
//		model.addAttribute("operateFrom", operateFrom);
//		model.addAttribute("libType", libType);
		return "redirect:/res/wf/list.action?publishType=1";
//		return "/publishRes/publishResMain?publishType=1";
	}
	@RequestMapping(value = "list")
	public String list(Model model, HttpServletRequest request) {
		logger.debug("to list ");
		String publishType = request.getParameter("publishType");
		model.addAttribute("publishType", 1);
		return "publishRes/publishResMain";
	}
}
