package com.brainsoon.resrelease.action;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.resource.service.IResWorkFlowService;
import com.brainsoon.resource.support.WorkFlowUtils;
import com.brainsoon.resrelease.po.ResOrder;
import com.brainsoon.resrelease.po.ResOrderDetail;
import com.brainsoon.resrelease.service.IPressOrderWorkFlowService;
import com.brainsoon.resrelease.service.IResOrderService;
import com.brainsoon.system.service.ISysOperateService;

/**
 * @ClassName: PressOrderWorkFlowAction
 * @Description: 出版库流程
 * @author xiehewei
 * @date 2014年9月11日 上午10:44:49
 *
 */
@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class PressOrderWorkFlowAction extends BaseAction {

	@Autowired
	private IPressOrderWorkFlowService pressOrderWorkFlowService;
	@Autowired
	private IResOrderService resOrderService;
	@Autowired
	private IResWorkFlowService resWorkFlowService;
	@Autowired
	private ISysOperateService sysOperateService;
	/**
	 * 上报
	 */
	@RequestMapping(value = "/pubresOrder/pubresOrderWorkFlow/doApply")
	@ResponseBody
	public String doApply(String objectId){
		logger.debug("to doApply ");
		List<ResOrderDetail> list = null;
		int count = 0;
		String result = "";
		String[] objIds = objectId.split(",");
		for(String orderId: objIds){
			list = resOrderService.getResOrderDetailByOrderId(orderId);
			if(list.size()==0){
				count++;
				break;
			}
		}
		if(count==0){
			for(String ordId: objIds){
				String statusDsc = "已提交";
				String operateDesc = "需求单上报";
				pressOrderWorkFlowService.doApply(ordId);
				UserInfo userInfo = LoginUserUtil.getLoginUser();
				//SysOperateLogUtils.addLog("resOrder_apply", userInfo.getUsername(), userInfo);
				//sysOperateService.saveHistory("orderCheck."+ordId, "", statusDsc, operateDesc, new Date(), LoginUserUtil.getLoginUser().getUserId());
			}
			result = objIds.length==1?"上报成功!":"批量上报成功!";
		}else{
			result = objIds.length==1?"上报失败！":"批量上报的需求单中有没有添加资源的需求单，批量上报失败！";
		}
		return result;
	}
	
	/**
	 * 跳转-审核页面
	 * @return
	 */
	@RequestMapping(value = "/pubresOrder/pubresOrderWorkFlow/gotoCheck")
	public String gotoCheck(@RequestParam("objectId") String objectId, 
			@RequestParam(value = "execuId", required = false) String execuId,
			@RequestParam(value = "wfTaskId", required = false) String wfTaskId,
			@RequestParam("operateFrom") String operateFrom,
			Model model){
		logger.info("进入需求单审核页面！");
		Map<String, String> map = resWorkFlowService.getWorkFlowInfo(WorkFlowUtils.getExecuId(objectId,"press"));
		execuId = map.get("execuId");
		wfTaskId = map.get("wfTaskId");
		ResOrder resOrder = (ResOrder) resOrderService.getByPk(ResOrder.class, Long.valueOf(objectId));
		model.addAttribute("objectId", objectId);
		model.addAttribute("execuId", execuId);
		model.addAttribute("wfTaskId", wfTaskId);
		model.addAttribute("resOrder",resOrder);
		model.addAttribute("operateFrom", operateFrom);
		List<ResOrderDetail> resOrderDetailList = resOrderService.getResOrderDetailByOrderId(objectId);
		if(resOrderDetailList.size()==0){
			model.addAttribute("flag", "0");
		}else{
			model.addAttribute("flag", "1");
		}
		return "/resRelease/pressOrder/resOrderAudit"; 
	}
	
	/**
	 * 跳转到编辑页面(驳回编辑)
	 * 
	 * @param asset
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping(value = "/pubresOrder/pubresOrderWorkFlow/gotoEdit")
	public String gotoEdit(
			@RequestParam("objectId") String objectId, 
			@RequestParam("execuId") String execuId, 
			@RequestParam("wfTaskId") String wfTaskId,
			@RequestParam("operateFrom") String operateFrom,
			ModelMap model){
		model.addAttribute("objectId", objectId);
		model.addAttribute("execuId", execuId);
		model.addAttribute("wfTaskId", wfTaskId);
		model.addAttribute("operateFrom", operateFrom);
		ResOrder resOrder = (ResOrder) resOrderService.getByPk(ResOrder.class, Long.valueOf(objectId));
		model.addAttribute("resOrder", resOrder);
		List<ResOrderDetail> resOrderDetailList = resOrderService.getResOrderDetailByOrderId(objectId);
		if(resOrderDetailList.size()==0){
			model.addAttribute("flag", "0");
		}else{
			model.addAttribute("flag", "1");
		}
		if(StringUtils.isNotBlank(objectId)){
			return "/resRelease/pressOrder/presOrderEdit";
		}else{
			return "/resRelease/pressOrder/presOrderEdit";
		}
	}
	
	/**
	 * 审核(通过，驳回)
	 * 
	 * @param asset
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping(value = "/resOrderWorkFlow/pressResOrderWorkFlow/doCheck")
	@ResponseBody
	public String doCheck(@RequestParam("objectId") String objectId, 
			@RequestParam("operateFrom") String operateFrom, 
			@RequestParam("decision") String decision,
			@RequestParam(value = "wfTaskId", required = false) String wfTaskId, 
			@RequestParam("checkOpinion") String checkOpinion) throws Exception{
		String status = "2";
		String statusDsc = "";
		String operateDesc = "需求单审核";
		if(decision.equals("通过")){
			statusDsc = "通过";
		}else{
			status = "3";
			statusDsc = "驳回";
		}
		pressOrderWorkFlowService.updateResOrderStatus(objectId, status, checkOpinion);
		pressOrderWorkFlowService.doCheck(objectId, wfTaskId, decision, checkOpinion);
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		//SysOperateLogUtils.addLog("resOrder_check", userInfo.getUsername(), userInfo);
		//sysOperateService.saveHistory("orderCheck."+objectId, "", statusDsc, operateDesc, new Date(), LoginUserUtil.getLoginUser().getUserId());
		if (StringUtils.equals(operateFrom, "TASK_LIST_PAGE")) {
			return "/TaskAction/toList.action";
		} else {
			return "/resRelease/resOrder/resOrderList.jsp";
		}
	}
	
	/**
	 * 保存并提交
	 */
	@RequestMapping(value = "/resOrderWorkFlow/pressResOrderWorkFlow/doSaveAndSubmit")
	@ResponseBody
	public String todoCheck(String orderId, String wfTaskId, String operateFrom, Model model){
		//pressOrderWorkFlowService.updateResOrderStatus(orderId, status,"");
		//return "/resRelease/resOrder/resOrderList";
		pressOrderWorkFlowService.doSaveAndSubmit(orderId, wfTaskId);
		model.addAttribute("operateFrom", operateFrom);
		return "ok";
	}
}
