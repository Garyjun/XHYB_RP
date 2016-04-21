package com.brainsoon.resrelease.action;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
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
import com.brainsoon.common.util.date.DateUtil;
import com.brainsoon.resource.po.SubjectStore;
import com.brainsoon.resource.service.IBaseSemanticSerivce;
import com.brainsoon.resource.service.ICollectResService;
import com.brainsoon.resource.service.ISubjectService;
import com.brainsoon.resrelease.po.ResFileRelation;
import com.brainsoon.resrelease.po.ResOrder;
import com.brainsoon.resrelease.po.ResOrderDetail;
import com.brainsoon.resrelease.po.ResRelease;
import com.brainsoon.resrelease.po.ResReleaseDetail;
import com.brainsoon.resrelease.processThread.PublishThread;
import com.brainsoon.resrelease.service.IPublishTempService;
import com.brainsoon.resrelease.service.IResOrderService;
import com.brainsoon.resrelease.service.IResReleaseService;
import com.brainsoon.resrelease.support.ResReleaseConstant;
import com.brainsoon.resrelease.support.ResReleaseConstant.ResReleaseDetailStatus;
import com.brainsoon.system.model.MetaDataModelGroup;
import com.brainsoon.system.model.User;
import com.brainsoon.system.service.IDictValueService;
import com.brainsoon.system.service.IInDefinitionService;
import com.brainsoon.system.service.ISysOperateService;
import com.brainsoon.system.support.SysOperateLogUtils;

@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
/**
 * 
 * @ClassName: ResReleaseAction 
 * @Description:  资源发布相关功能
 * @author 谢贺伟
 * @date 2014-7-8 下午3:33:25 
 *
 */
public class ResReleaseAction extends BaseAction {
	@Autowired
	private IResOrderService resOrderService;
	@Autowired
	private IResReleaseService resReleaseService;
	@Autowired
	private IBaseSemanticSerivce baseSemanticSerivce;
	@Autowired
	private IPublishTempService publishTempService;
	@Autowired
	private ICollectResService collectResService;
	@Autowired
	private ISysOperateService sysOperateService;
	@Autowired
	private IDictValueService dictValueService;
	@Autowired
	private IInDefinitionService inDefinitionService;
	@Autowired
	private ISubjectService subjectService;
	
	
	/**
	 * 资源发布
	 * @return
	 */
	@RequestMapping(value = "resRelease/publish")
	@ResponseBody
	public String publishResRelease(String orderId) {
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		//根据任务单id查询任务单
		ResOrder resOrder = (ResOrder) resOrderService.getByPk(ResOrder.class, Long.valueOf(orderId));
		String batchNum = "zbjt-" + getName(resOrder.getOrderId().intValue());
		//根据需求单id查询唯一的加工单
		ResRelease resRelease = resReleaseService.queryRelReleaseByOrderId(Long.valueOf(orderId));
		//写日志
		SysOperateLogUtils.addLog("resOrder_publish", resOrder.getChannelName(), userInfo);
		
		//fengda 2015年8月7日解决授权问题，新添加了userInfo.getUserId()参数
		PublishThread publishThread = new PublishThread(resOrder, resRelease,userInfo.getUserId());
		new Thread(publishThread).start();
		return "redirect:/resOrder/list.action";
	}
	
	
	
	/**
	 * 资源单条或批量加工
	 * @return
	 */
	@RequestMapping(value = "resRelease/batchProcess")
	@ResponseBody
	public String batchProcess(String orderIds,String posttype) { 
		//TaskQueue queue = TaskQueue.getInst();
		String[] ids = orderIds.split(",");
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		User user = new User();
		user.setId(userInfo.getUserId());
		String result = "";
		for(String id: ids){
			if(posttype.equals("1")){
				ResOrder resOrder = (ResOrder) resOrderService.getByPk(ResOrder.class, Long.valueOf(id));
				if(resOrder.getStatus().equals(ResReleaseConstant.OrderStatus.ORDERADD)){
					ResRelease resRelease = resReleaseService.getResRelease(Long.parseLong(id), posttype);
					List<ResReleaseDetail> resReleaseDetails = resReleaseService.getResReleaseDetailByCnodition(resRelease.getId());
					List<ResOrderDetail> details = resOrderService.getResOrderDetailByOrderIdAndtype(id, posttype);
					if(details.size()==resReleaseDetails.size()){
						result += "单据：【"+resOrder.getChannelName()+"】追加资源为空,";
						continue;
					}
				}
				resOrder.setStatus(ResReleaseConstant.OrderStatus.WAITING_PROCESS);
				resOrder.setAuditor(user);
				resOrderService.update(resOrder);
				SysOperateLogUtils.addLog("resOrder_process", resOrder.getChannelName(), userInfo);
			}
			if(posttype.equals("2")){
				SubjectStore subjectStore = (SubjectStore) subjectService.getByPk(SubjectStore.class, Long.valueOf(id));
				if(subjectStore.getStatus().equals(ResReleaseConstant.OrderStatus.ORDERADD)){
					ResRelease resRelease = resReleaseService.getResRelease(Long.parseLong(id), posttype);
					List<ResReleaseDetail> resReleaseDetails = resReleaseService.getResReleaseDetailByCnodition(resRelease.getId());
					List<ResOrderDetail> details = resOrderService.getResOrderDetailByOrderIdAndtype(id, posttype);
					if(details.size()==resReleaseDetails.size()){
						result += "单据：【"+subjectStore.getName()+"】追加资源为空,";
						continue;
					}
				}
				subjectStore.setStatus(ResReleaseConstant.OrderStatus.WAITING_PROCESS);
				subjectStore.setAuditor(user);
				subjectService.update(subjectStore);
				SysOperateLogUtils.addLog("subject_process", subjectStore.getName(), userInfo);
			}
		}
		if(StringUtils.isNotBlank(result)){
			result = result.substring(0, result.length()-1);
			result += "不能加工发布!其他单据正在加工！";
		}else {
			result = "资源加工已经触发，请稍后！";
		}
		//new Thread(new BatchProcessThread()).start();
		return result;
	}
	
	@RequestMapping(value = "resRelease/canBatchProcess")
	@ResponseBody
	public int canBatchProcess(String orderIds){
		int flag = 0;
		if(StringUtils.isNotEmpty(orderIds)){
			String[] ids = orderIds.split(",");
			int len = ids.length;
			int num = 0;
			for(String id: ids){
				ResOrder resOrder = (ResOrder) resOrderService.getByPk(ResOrder.class, Long.valueOf(id));
				if(ResReleaseConstant.OrderStatus.AUDITED.equals(resOrder.getStatus()) || ResReleaseConstant.OrderStatus.PROCESSEDWRONG.equals(resOrder.getStatus())){
					num++;
				}
			}
			if(len==num){
				flag = 1;
			}
		}
		return flag;
	}
	
	/**
	 * 列表查询
	 * 
	 * @param request
	 * @param response
	 * @param params
	 * @return json
	 */
	@RequestMapping(value = "/resRelease/list")
	public @ResponseBody PageResult list(HttpServletRequest request, HttpServletResponse response) {
		logger.info("进入查询方法");
		QueryConditionList conditionList = getQueryConditionList();
		return resReleaseService.query4Page(ResRelease.class, conditionList);
	}
	
	/**
	 * http下载
	 * @param connectionUrl
	 * @param encoding
	 * @param url
	 */
	@RequestMapping(value = "resRelease/httpDownload")
	public void httpDownload(HttpServletRequest request,HttpServletResponse response, Long id){
		ResRelease resRelease = (ResRelease) resReleaseService.getByPk(ResRelease.class, id);
		String connectionUrl = resRelease.getFtpUrl();
		try {
			UserInfo userInfo = LoginUserUtil.getLoginUser();
			resReleaseService.downloadFile(request, response, connectionUrl, false);
			SysOperateLogUtils.addLog("httpDownload", userInfo.getUsername(), userInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 发布记录列表
	 * @return
	 */
	@RequestMapping(value = "resRelease/resOrderRecordList")
	@ResponseBody
	public PageResult resOrderRecordList(
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam(value = "status", required = false) String status,
			@RequestParam(value = "createUser", required = false) String createUser,
			@RequestParam(value = "channelName", required = false) String channelName,
			@RequestParam(value = "posttype", required = false) String posttype) throws UnsupportedEncodingException, ParseException {
		QueryConditionList conditionList = getQueryConditionList();
		System.out.println(posttype);
		if (status != null) {
			conditionList.addCondition(new QueryConditionItem("status", Operator.EQUAL, status));
		}
		if (channelName != null) {
			conditionList.addCondition(new QueryConditionItem("channelName", Operator.LIKE, "%"+URLDecoder.decode(URLDecoder.decode(channelName,"utf-8"),"utf-8")+"%"));
		}
		if (createUser != null) {
			conditionList.addCondition(new QueryConditionItem("createUser.userName", Operator.LIKE, "%"+createUser+"%"));
		}
		if (startDate != null) {
			conditionList.addCondition(new QueryConditionItem("createTime", Operator.GT, DateUtil.convertStringToDate("yyyy-MM-dd HH:mm:ss", URLDecoder.decode(startDate,"UTF-8"))));
		}
		if (endDate != null) {
			conditionList.addCondition(new QueryConditionItem("createTime", Operator.LT, DateUtil.convertStringToDate("yyyy-MM-dd HH:mm:ss", URLDecoder.decode(endDate,"UTF-8"))));
		}
		if (posttype != null) {
			conditionList.addCondition(new QueryConditionItem("posttype", Operator.EQUAL, posttype));
		}
		/*UserInfo userInfo = LoginUserUtil.getLoginUser();
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
					conditionList.addCondition(new QueryConditionItem("resOrder.resType", Operator.IN, resTypes));
				}
			}
			String userIds = userInfo.getDeptUserIds();
			int isPrivate = userInfo.getIsPrivate();
			if(1==isPrivate){
				if(StringUtils.isNotBlank(userIds)){
					conditionList.addCondition(new QueryConditionItem("resOrder.createUser.id", Operator.IN, userIds.substring(0, userIds.length()-1)));
				}else{
					conditionList.addCondition(new QueryConditionItem("resOrder.createUser.id", Operator.EQUAL, userInfo.getUserId()));
				}
			}else{
				if(StringUtils.isNotEmpty(userIds)){
					conditionList.addCondition(new QueryConditionItem("resOrder.createUser.id", Operator.IN, userIds.substring(0, userIds.length()-1)));
				}else{
					conditionList.addCondition(new QueryConditionItem("resOrder.createUser.id", Operator.IN, "-2"));
				}
			}
		}*/
		return resReleaseService.query4Page(ResRelease.class, conditionList);
	}

	@RequestMapping(value = "resRelease/resReleaseDetailQuery")
	@ResponseBody
	public PageResult resReleaseDetailQuery(Model model, @RequestParam("id") Long id,@RequestParam(value="restype") String restype){
		QueryConditionList conditionList = getQueryConditionList();
		conditionList.addCondition(new QueryConditionItem("releaseId", Operator.EQUAL, id));
		conditionList.addCondition(new QueryConditionItem("resType", Operator.EQUAL, restype));
		PageResult result = resReleaseService.query4Page(ResReleaseDetail.class, conditionList);
		return result;
	}
	
	/**
	 * 查看资源发布的详细信息
	 * @param model
	 * @param id
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "resRelease/view")
	public String view(Model model, @RequestParam("id") Long id,@RequestParam(value="datetype",required=false) String datetype, HttpServletRequest request,HttpServletResponse response) {
		ResRelease resRelease = (ResRelease) baseService.getByPk(ResRelease.class, id);
		Map<String, String> map = new HashMap<String, String>();
		if(resRelease.getPosttype().equals("1")){
			//需求单
			ResOrder order = (ResOrder) resOrderService.getByPk(ResOrder.class, resRelease.getOrderId());
			String restypes = order.getTemplate().getType();
			String restype[] = restypes.split(",");
			for (String type : restype) {
				MetaDataModelGroup metaDataModelGroup=(MetaDataModelGroup) resOrderService.getByPk(MetaDataModelGroup.class, Long.decode(type));
				map.put(metaDataModelGroup.getId().toString(), metaDataModelGroup.getTypeName());
			}
		}
		if(resRelease.getPosttype().equals("2")){
			//需求单
			SubjectStore store = (SubjectStore) subjectService.getByPk(SubjectStore.class, resRelease.getOrderId());
			String restypes = store.getTemplate().getType();
			String restype[] = restypes.split(",");
			for (String type : restype) {
				MetaDataModelGroup metaDataModelGroup=(MetaDataModelGroup) subjectService.getByPk(MetaDataModelGroup.class, Long.decode(type));
				map.put(metaDataModelGroup.getId().toString(), metaDataModelGroup.getTypeName());
			}
		}
		System.out.println(JSONObject.fromObject(map).toString());
		model.addAttribute("map", JSONObject.fromObject(map).toString());
		model.addAttribute("resRelease", resRelease);
		model.addAttribute("datetype", datetype);
		return "resRelease/resRecord/resRecordDetail";
	}
	
	@RequestMapping(value = "resRelease/updateResReleaseStatus")
	public String updateResReleaseStatus(@RequestParam("relId") Long relId){
		ResRelease resRelease = (ResRelease) resReleaseService.getByPk(ResRelease.class, relId);
		resRelease.setStatus("1");
		resReleaseService.update(resRelease);
		return "resRelease/resRecord/resRecordList";
	}
	
	@RequestMapping(value = "resRelease/publishResFileDetailQuery")
	@ResponseBody
	public PageResult publishResFileDetailQuery(Long orderId, String resId,String posttype){
		QueryConditionList conditionList = getQueryConditionList();
		conditionList.addCondition(new QueryConditionItem("resId", Operator.EQUAL, resId));
		conditionList.addCondition(new QueryConditionItem("orderId", Operator.EQUAL, orderId));
		conditionList.addCondition(new QueryConditionItem("posttype", Operator.EQUAL, posttype));
		PageResult result = resReleaseService.query4Page(ResFileRelation.class, conditionList);
		return result;
	}
	
	@RequestMapping(value = "resRelease/queryDetailByProAndPubStatus")
	@ResponseBody
	public PageResult queryDetailByProAndPubStatus(Long relId, String processStatus, String status,String restype){
		QueryConditionList conditionList = getQueryConditionList();
		if(StringUtils.isNotEmpty(processStatus)){
			if("proSuccess".equals(processStatus)){
				conditionList.addCondition(new QueryConditionItem("processFileFailed", Operator.EQUAL, 0));
			}else{
				conditionList.addCondition(new QueryConditionItem("processFileFailed", Operator.NOTEQUAL, 0));
			}
		}
		if(StringUtils.isNotEmpty(status)){
			if("pubSuccess".equals(status)){
				conditionList.addCondition(new QueryConditionItem("status", Operator.EQUAL, "2"));
			}else{
				conditionList.addCondition(new QueryConditionItem("status", Operator.NOTEQUAL, "2"));
			}
		}
		if(relId != null){
			conditionList.addCondition(new QueryConditionItem("releaseId", Operator.EQUAL, relId));
		}
		conditionList.addCondition(new QueryConditionItem("resType", Operator.EQUAL, restype));
		PageResult result = resReleaseService.query4Page(ResReleaseDetail.class, conditionList);
		return result;
	}
	
	private static String getName(int n) {
		String name = String.valueOf(n);
		int len = 5 - name.length();
		for (int i = 0; i < len; i++) {
			name = "0" + name;
		}
		return name;
	}
	
	@RequestMapping(value = "resRelease/refreshList")
	public String refreshList(){
		return "resRelease/resRecord/resRecordList";
	}
}
