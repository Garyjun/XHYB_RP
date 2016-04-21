package com.brainsoon.system.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.resource.service.IAreaService;
import com.brainsoon.resource.service.IBaseSemanticSerivce;
import com.brainsoon.resource.service.IResWorkFlowService;
import com.brainsoon.resource.support.WorkFlowUtils;
import com.brainsoon.semantic.ontology.model.Asset;
import com.brainsoon.semantic.ontology.model.CommonMetaData;
import com.brainsoon.system.service.IOrphanResourceService;
import com.brainsoon.system.support.SystemConstants;
import com.google.gson.Gson;
@Controller
public class OrphanResourceAction extends BaseAction {
	/**默认命名空间**/
	private static final String baseUrl = "/orphanResource/";	
	@Autowired
	private IOrphanResourceService orphanResourceService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IResWorkFlowService resWorkFlowService;
	
	//孤儿资源列表
	@RequestMapping(baseUrl + "list")
	public void list(HttpServletRequest request, HttpServletResponse response) {
		logger.info("进入查询方法");
		QueryConditionList conditionList = getQueryConditionList();
		outputResult(orphanResourceService.query4Page(request, conditionList));
	}
	
	//孤儿资源挂节点
	@RequestMapping(baseUrl + "mountResource")
	public @ResponseBody String mountResource(HttpServletRequest request, HttpServletResponse response) {
		logger.info("孤儿资源挂载节点");
		String objectId = request.getParameter("resourceId");
		String version = request.getParameter("version");
		String peroid = request.getParameter("peroid");
		String subject = request.getParameter("subject");
		String grade = request.getParameter("grade");
		String volume = request.getParameter("volume");
		String unit = request.getParameter("unit");
		String unitName = request.getParameter("unitName");
		Asset res = orphanResourceService.getResourceById(objectId);
		CommonMetaData cmd = res.getCommonMetaData();
		CommonMetaData newCmd = new CommonMetaData();
		newCmd.setObjectId(cmd.getObjectId());
		newCmd.setVersion(version);
		newCmd.setEducational_phase(peroid);
		newCmd.setSubject(subject);
		newCmd.setGrade(grade);
		newCmd.setFascicule(volume);
		newCmd.setUnit(unit);
		newCmd.setUnitName(unitName);
		newCmd.setResource(objectId);
		
		Gson gson = new Gson();
		String result = orphanResourceService.mountResource(gson.toJson(newCmd));
		return result;
	}
	
	//删除孤儿资源
	@RequestMapping(baseUrl + "delete")
	public void deleteResource(HttpServletRequest request, HttpServletResponse response) {
		logger.info("删除孤儿资源");
		String ids = request.getParameter("ids");
		orphanResourceService.deleteByIds(ids);
		outputResult("删除成功");
	}
	
	/**
	 * 详细
	 * @param asset
	 * @param model
	 * @throws Exception 
	 */
	@RequestMapping(baseUrl+"detail") 
	public String detail(@RequestParam("objectId") String objectId,ModelMap model) throws Exception{
		//查询省
		model.addAttribute("provinces", areaService.getProvince());
		model.addAttribute("objectId", objectId);
		if(StringUtils.isNotBlank(objectId)){
			Asset res = orphanResourceService.getResourceById(objectId);
			if(null != res){
				model.addAttribute("module", res.getCommonMetaData().getModule());
				model.addAttribute("type", res.getCommonMetaData().getType());
				String libType = res.getCommonMetaData().getLibType();
				model.addAttribute("libType", libType);
				Gson gson = new Gson();
				model.addAttribute("resourceDetail", gson.toJson(res));
				model.addAttribute("execuId",WorkFlowUtils.getExecuId(objectId, libType));
			}
		}
		return baseUrl + "detail";
	}
	
	//资源下线
	@RequestMapping(baseUrl+"offlineResource") 
	@ResponseBody
	public String offlineResource(HttpServletRequest request, HttpServletResponse response) {
		logger.info("孤儿资源下线");
		String objectId = request.getParameter("resourceId");
		resWorkFlowService.updateResStatus(objectId, SystemConstants.ResourceStatus.STATUS4);
		return "下线成功！";
	}
}
