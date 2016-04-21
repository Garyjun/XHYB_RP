package com.brainsoon.resrelease.action;

import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
import com.brainsoon.resource.service.IResWorkFlowService;
import com.brainsoon.resource.support.WorkFlowUtils;
import com.brainsoon.resrelease.po.ResOrder;
import com.brainsoon.resrelease.po.ResOrderDetail;
import com.brainsoon.resrelease.service.IResOrderService;
import com.brainsoon.resrelease.service.IResOrderWorkFlowService;
import com.brainsoon.resrelease.support.ResReleaseConstant;
import com.brainsoon.system.service.ISysOperateService;
import com.brainsoon.system.support.SysOperateLogUtils;
@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
/**
 * 
 * @ClassName: ResOrderWorkFlowAction 
 * @Description:  需求单流程管理相关的功能
 * @author 谢贺伟 
 * @update author 唐辉
 * @date 2014-7-8 下午3:32:37 
 *
 */
public class ResOrderWorkFlowAction extends BaseAction {

	@Autowired
	private IResOrderWorkFlowService resOrderWorkFlowService;
	@Autowired
	private IResOrderService resOrderService;
	@Autowired
	private IResWorkFlowService resWorkFlowService;
	@Autowired
	private ISysOperateService sysOperateService;
	/**
	 * 上报
	 * @return
	 */
	@RequestMapping(value = "/resOrder/doApply")
	@ResponseBody
	public String doApply(String objectId,HttpServletRequest request){
		String posttype=request.getParameter("posttype");
		logger.debug("to doApply ");
		String result = "";
		try {
			List<ResOrderDetail> list = null;
			int count = 0;
			String[] objIds = objectId.split(",");
			for(String orderId: objIds){
				list = resOrderService.getResOrderDetailByOrderIdAndtype(orderId,posttype);
				if(list.size()==0){
					count++;
					break;
				}
			}
			if(count==0){
				for(String ordId: objIds){
					//String statusDsc = "已提交";
					//String operateDesc = "需求单上报";
				    resOrderWorkFlowService.doApply(ordId,posttype);
				}
				result = objIds.length==1?"上报成功!":"批量上报成功!"; 
			}else{
				result = objIds.length==1?"上报失败！":"批量上报的需求单中有没有添加资源的需求单，批量上报失败！";
			}
		} catch (Exception e) {
			result = "上报失败";
		}
		
		return result;
	}
	
	/**
	 * 跳转-审核页面
	 * @return
	 */
	@RequestMapping(value = "/resOrder/resOrderWorkFlow/gotoCheck")
	public String gotoCheck(@RequestParam("objectId") String objectId, 
			@RequestParam(value = "execuId", required = false) String execuId,
			@RequestParam(value = "wfTaskId", required = false) String wfTaskId,
			@RequestParam("operateFrom") String operateFrom,@RequestParam("posttype") String posttype,
			Model model){
		logger.info("进入需求单审核页面！");
		Map<String, String> map = resWorkFlowService.getWorkFlowInfo(WorkFlowUtils.getExecuId(objectId,"edu"));
		execuId = map.get("execuId");
		wfTaskId = map.get("wfTaskId");
		ResOrder resOrder = (ResOrder) resOrderService.getByPk(ResOrder.class, Long.valueOf(objectId));
		model.addAttribute("objectId", objectId);
		model.addAttribute("execuId", execuId);
		model.addAttribute("wfTaskId", wfTaskId);
		model.addAttribute("resOrder",resOrder);
		model.addAttribute("posttype",posttype);
		model.addAttribute("operateFrom", operateFrom);
		List<ResOrderDetail> resOrderDetailList = resOrderService.getResOrderDetailByOrderIdAndtype(objectId, posttype);
		if(resOrderDetailList.size()==0){
			model.addAttribute("flag", "0");
		}else{
			model.addAttribute("flag", "1");
		}
		return "/resRelease/resOrder/resOrderAudit"; 
	}
	
	/**
	 * 跳转到编辑页面(驳回编辑)
	 * 
	 * @param asset
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping(value = "/resOrder/resOrderWorkFlow/gotoEdit")
	public String gotoEdit(
			@RequestParam("objectId") String objectId, 
			@RequestParam("execuId") String execuId, 
			@RequestParam("wfTaskId") String wfTaskId,
			@RequestParam("operateFrom") String operateFrom,@RequestParam("posttype") String posttype,
			ModelMap model){
		model.addAttribute("objectId", objectId);
		model.addAttribute("execuId", execuId);
		model.addAttribute("wfTaskId", wfTaskId);
		model.addAttribute("posttype", posttype);
		model.addAttribute("operateFrom", operateFrom);
		ResOrder resOrder = (ResOrder) resOrderService.getByPk(ResOrder.class, Long.valueOf(objectId));
		model.addAttribute("resOrder", resOrder);
		List<ResOrderDetail> resOrderDetailList = resOrderService.getResOrderDetailByOrderIdAndtype(objectId,posttype);
		if(resOrderDetailList.size()==0){
			model.addAttribute("flag", "0");
		}else{
			model.addAttribute("flag", "1");
		}
		if(StringUtils.isNotBlank(objectId)){
			return "/resRelease/resOrder/resOrderEdit";
		}else{
			return "/resRelease/resOrder/resOrderEdit";
		}
	}
	
	
	/**
	 * 审核(通过，驳回)
	 * 
	 * @param asset
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping(value = "/resOrder/resOrderWorkFlow/doCheck")
	@ResponseBody
	public String doCheck(@RequestParam("objectId") String objectId, 
			@RequestParam("operateFrom") String operateFrom, 
			@RequestParam("decision") String decision,
			@RequestParam(value = "wfTaskId", required = false) String wfTaskId, 
			@RequestParam("checkOpinion") String checkOpinion) throws Exception{
		checkOpinion=URLDecoder.decode(checkOpinion, "utf-8");
		String status = ResReleaseConstant.OrderStatus.AUDITED;
		if(decision.equals("通过")){
			resOrderWorkFlowService.doCheck(objectId, status, checkOpinion,wfTaskId, decision);
		}else{
			status =  ResReleaseConstant.OrderStatus.AUDIT_REFUSE;
			resOrderWorkFlowService.doCheck(objectId, status, checkOpinion,wfTaskId, decision);
		}
		if (StringUtils.equals(operateFrom, "TASK_LIST_PAGE")) {
			return "/TaskAction/toList.action";
		} else {
			return "/resRelease/resOrder/resOrderList.jsp";
		}
	}
	
	/**
	 * 保存并提交
	 */
	@RequestMapping(value = "/resOrder/resOrderWorkFlow/doSaveAndSubmit")
	@ResponseBody
	public String doSaveAndSubmit(String orderId, String wfTaskId, String operateFrom, String channelName, String templateId,
			String description, String orderDate,String posttype, Model model){
		try {
			resOrderWorkFlowService.doSaveAndSubmit(orderId, wfTaskId, channelName, templateId, description, orderDate,posttype);
			model.addAttribute("operateFrom", operateFrom);
			return "ok";
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}
	
	/**
	 * 发布
	 * @return
	 */
	@RequestMapping(value = "/resOrder/resOrderWorkFlow/publish")
	public String publish(){
		return "";
	}
}
