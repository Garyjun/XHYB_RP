package com.brainsoon.resrelease.action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.jbpm.api.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
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
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.appframe.util.StringUtil;
import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.exception.DaoException;
import com.brainsoon.common.pagination.PageInfo;
import com.brainsoon.common.service.IBaseService;
import com.brainsoon.common.support.Constants;
import com.brainsoon.common.support.SysResTypeCacheMap;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.date.DateUtil;
import com.brainsoon.jbpm.service.IJbpmExcutionService;
import com.brainsoon.resource.support.WorkFlowUtils;
import com.brainsoon.resrelease.po.ParamsTempEntity;
import com.brainsoon.resrelease.po.ProdParamsTemplate;
import com.brainsoon.resrelease.po.ResFileRelation;
import com.brainsoon.resrelease.po.ResOrder;
import com.brainsoon.resrelease.po.ResOrderDetail;
import com.brainsoon.resrelease.service.IPublishTempService;
import com.brainsoon.resrelease.service.IResOrderService;
import com.brainsoon.resrelease.service.IResOrderWorkFlowService;
import com.brainsoon.resrelease.support.ResReleaseConstant;
import com.brainsoon.resrelease.support.SysParamsTemplateConstants;
import com.brainsoon.search.service.ISearchIndexService;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.Domain;
import com.brainsoon.semantic.ontology.model.File;
import com.brainsoon.semantic.schema.ontology.MetadataDefinition;
import com.brainsoon.system.model.MetaDataModelGroup;
import com.brainsoon.system.model.SysDir;
import com.brainsoon.system.model.User;
import com.brainsoon.system.service.IClassicService;
import com.brainsoon.system.service.IDictNameService;
import com.brainsoon.system.service.ISysDirService;
import com.brainsoon.system.service.ISysOperateService;
import com.brainsoon.system.service.IUserService;
import com.brainsoon.system.support.SysOperateLogUtils;
import com.brainsoon.system.util.MetadataSupport;
import com.google.gson.Gson;
@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
/**
 * 
 * @ClassName: ResOrderAction 
 * @Description: 需求单管理相关功能 
 * @author 谢贺伟 
 * @date 2014-7-8 下午3:32:55 
 *
 */
public class ResOrderAction extends BaseAction {
	
	@Autowired
	private IResOrderService resOrderService;
	@Autowired
	private ISysOperateService sysOperateService;
	@Autowired
	private IPublishTempService publishTempService;
	@Autowired
	private IBaseService inDefinitionService;
	@Autowired
	private IJbpmExcutionService jbpmExcutionService;
	@Autowired
	private IResOrderWorkFlowService resOrderWorkFlowService;
	@Autowired
	private ISysDirService sysDirService;
	@Autowired
	private IClassicService classicService;
	@Autowired
	private IUserService userService;
	@Autowired
	private ISearchIndexService searchIndexService;
	private User user = new User();
	@Autowired
	private IDictNameService dictNameService;
	
	@RequestMapping(value = "/resOrder/list")
	public String list(Model model, ResOrder resOrder) {
		logger.debug("to list ");
		model.addAttribute("platformId", LoginUserUtil.getLoginUser().getPlatformId());
		model.addAttribute(Constants.COMMAND, resOrder);
		return "resRelease/resOrder/resOrderList";
	}
	//查询该需求单是线上还是线下发布
	@RequestMapping(value="/resOrder/querywarnings")
	@ResponseBody
	public String querywarnings(HttpServletRequest request,HttpServletResponse response){
		int va = 0;
		String  ids = request.getParameter("ids");
		if(StringUtils.isNotBlank(ids)){
			String id[] = ids.split(",");
			for (int i = 0; i < id.length; i++) {
				ResOrder order;
				try {
					order = (ResOrder) resOrderService.getByPk(ResOrder.class, Long.decode(id[i]));
				} catch (Exception e) {
					return "0";
				}
				String pubtype=order.getTemplate().getPublishType();
				if(pubtype.equals("onLine")){
					va++;
				}
			}
			if(id.length==va){
				return "1";
			}
		}
		return "0";
	}
	//通知功能
	@RequestMapping(value="/resOrder/warnings")
	@ResponseBody
	public String warnings(HttpServletRequest request,HttpServletResponse response){
		String ids = request.getParameter("ids");
		HttpClientUtil http = new HttpClientUtil();
		logger.info("这是url---->http://app.cciph.com.cn/?app=book&controller=import&action=auto_import_all&id="+ids);
		http.executeGet("http://app.cciph.com.cn/?app=book&controller=import&action=auto_import_all&id="+ids);
		//logger.info(" 这是调用接口之后返回的结果------>"+results);
		//if(StringUtils.isBlank(results)){
			String id[] = ids.split(",");
			UserInfo userInfo= LoginUserUtil.getLoginUser();
			for (String orderid : id) {
				ResOrder resOrder = (ResOrder) resOrderService.getByPk(ResOrder.class, Long.parseLong(orderid));
				SysOperateLogUtils.addLog("resOrder_publish", resOrder.getChannelName(), userInfo);
			}
			return "1";
		/*}
		return "0";*/
	}
	
	/**
	 * 创建/修改 资源列表查询
	 * 
	 * @param request
	 * @param response
	 * @param params
	 * @return json
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	@RequestMapping(value = "/resOrder/resourceList")
	public @ResponseBody String list(HttpServletRequest request, HttpServletResponse response,Model model)
			throws JsonParseException, JsonMappingException, IOException {
		logger.info("进入查询方法");
		String orderId = request.getParameter("orderId");
		String restype = request.getParameter("restype");
		String posttype = request.getParameter("posttype");
		String formList = "";
		if(StringUtils.isNotBlank(orderId)&&StringUtils.isNotBlank(restype)&&StringUtils.isNotBlank(posttype)){
			List<ResOrderDetail> detailList = resOrderService.getResOrderDetailByOrderIds(orderId,posttype,restype);
			String resIds = "";
			if(detailList!=null && detailList.size() > 0){
				int page = Integer.parseInt(request.getParameter("page"));
				int size = Integer.parseInt(request.getParameter("rows"));
				//起始条数
				int startIndex = (page - 1) * size > 0 ? (page - 1) * size : 0;
				for (int i = startIndex; i < startIndex+size; i++) {
					if(i<detailList.size()){
						resIds += detailList.get(i).getResId() + ",";
					}
				}
				logger.info("资源大小--------------------"+detailList.size());
				int len = resIds.length();
				if(len>1){
					resIds = resIds.substring(0, len-1);
				}
				formList = resOrderService.queryResourceLists(resIds, "1", String.valueOf(size));
				if(StringUtils.isNotBlank(formList)){
					JSONObject jsonObject = JSONObject.fromObject(formList);
					jsonObject.put("total", detailList.size());
					formList = jsonObject.toString();
					logger.info("字符串------------》"+formList);
				}
			}
		}
		
		if(StringUtils.isBlank(formList)){
			formList = new Gson().toJson(new PageInfo());
		}
		return formList;
	}
	
	/**
	 * 教育库资源选择列表查询
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "resOrder/resSelectList")
	public void resSelectList(HttpServletRequest request, HttpServletResponse response){
		logger.info("进入查询方法");
		String libType = request.getParameter("libType");
		String module = request.getParameter("module");
		String type = request.getParameter("type");
		String orderId = request.getParameter("id");
		String page = request.getParameter("page");
		String size = request.getParameter("rows");
		String modifiedStartTime = request.getParameter("modifiedStartTime");
		String modifiedEndTime = request.getParameter("modifiedEndTime");
		String  creator = request.getParameter("creator");
		String title = request.getParameter("title");
		String keywords = request.getParameter("keywords");
		String description = request.getParameter("description");
		String resIds = request.getParameter("resIds");
		String knowledge_point = request.getParameter("knowledge_point");  //知识点
		String batchNum = request.getParameter("batchNum");  //批次号
		String unitName = request.getParameter("unitName"); 
		String eduPhase = "";  //学段
		String version = "";  //版本
		String subject = "";  //学科
		String grade = "";  //年级
		String fascicule = "";  //分册
		//处理特殊的字段属性
		Iterator iter = request.getParameterMap().entrySet().iterator(); 
		QueryConditionItem item = null;
		while(iter.hasNext()){
			Map.Entry<String,String[]> entry = (Map.Entry)iter.next();
			String key = entry.getKey();
			if(StringUtils.isNotBlank(key)){
				if(key.equals("commonMetaData.commonMetaDatas['educational_phase']")){//学段
					String[] value = entry.getValue();
					if(value != null && value.length > 0){
						eduPhase = value[0];
					}
				}
				
				if(key.equals("commonMetaData.commonMetaDatas['version']")){//版本
					String[] value = entry.getValue();
					if(value != null && value.length > 0){
						version = value[0];
					}
				}
				
				if(key.equals("commonMetaData.commonMetaDatas['subject']")){//学科
					String[] value = entry.getValue();
					if(value != null && value.length > 0){
						subject = value[0];
					}
				}
				
				if(key.equals("commonMetaData.commonMetaDatas['grade']")){//年纪
					String[] value = entry.getValue();
					if(value != null && value.length > 0){
						grade = value[0];
					}
				}
				
				if(key.equals("commonMetaData.commonMetaDatas['fascicule']")){//分册
					String[] value = entry.getValue();
					if(value != null && value.length > 0){
						fascicule = value[0];
					}
				}
			}
		}
		String templateType = ((ResOrder)resOrderService.getByPk(ResOrder.class, Long.valueOf(orderId))).getTemplate().getType();
		List<ResOrderDetail> list = resOrderService.getResOrderDetailByOrderId(orderId);
		String objectId = resIds;
		if(list.size()>0){
				for(ResOrderDetail detail :list){
					String resId = detail.getResId();
					objectId += resId +",";
				}
				objectId += "," + objectId.substring(0, objectId.length()-1);
		}
		HttpClientUtil http = new HttpClientUtil();
		page = page!=null?page:"1";
		size = size!=null?size:"100000";
		try {
			String resourceUrl = "?page="+page+"&size="+size+"&sort=modified_time&order=desc&queryType=0&status=0"+"&libType="+(libType==null?"":libType)+//"&module="+(module==null?"":module)+
					"&creator="+(creator!=null?creator:"")+"&keywords="+(keywords!=null?keywords:"")+"&modifiedStartTime="+
					(modifiedStartTime!=null?modifiedStartTime:"")+"&modifiedEndTime="+(modifiedEndTime!=null?modifiedEndTime:"")+"&description="+(description!=null?URLEncoder.encode(description,"utf-8"):"")+
					"&title="+(title!=null?title:"")+"&eduPhase="+(eduPhase!=null?URLEncoder.encode(eduPhase,"utf-8"):"")+
					"&subject="+(subject!=null?URLEncoder.encode(subject,"utf-8"):"")+"&knowledge_point="+(knowledge_point!=null?URLEncoder.encode(knowledge_point,"utf-8"):"")+
					"&version="+(version!=null?version:"")+"&grade="+(grade!=null?grade:"")+"&fascicule="+(fascicule!=null?fascicule:"")+
					"&batchNum="+(batchNum!=null?batchNum:"");
					//+"&filterType=exe,pps,ASF,asf,zip,rar";
			
			String resourceUrl0 = "?page="+page+"&size="+size+"&sort=modified_time&order=desc&queryType=0&status=0"+"&libType="+(libType==null?"":libType)+"&module="+(module==null?"":module)+
					"&creator="+(creator!=null?creator:"")+"&keywords="+(keywords!=null?keywords:"")+"&modifiedStartTime="+
					(modifiedStartTime!=null?modifiedStartTime:"")+"&modifiedEndTime="+(modifiedEndTime!=null?modifiedEndTime:"")+"&description="+(description!=null?URLEncoder.encode(description,"utf-8"):"")+
					"&title="+(title!=null?title:"")+"&eduPhase="+(eduPhase!=null?URLEncoder.encode(eduPhase,"utf-8"):"")+
					"&subject="+(subject!=null?URLEncoder.encode(subject,"utf-8"):"")+"&knowledge_point="+(knowledge_point!=null?URLEncoder.encode(knowledge_point,"utf-8"):"")+
					"&version="+(version!=null?version:"")+"&grade="+(grade!=null?grade:"")+"&fascicule="+(fascicule!=null?fascicule:"")+
					"&batchNum="+(batchNum!=null?batchNum:"");
					//+"&filterType=exe,pps,ASF,asf,zip,rar";
			if(templateType.equals("book")){
				resourceUrl = WebappConfigUtil.getParameter("BOOK_QUERY_URL")+resourceUrl+"&type=T06";
				outputResult(http.executeGet(resourceUrl));//+"&status=4"
			}else{
				resourceUrl = WebappConfigUtil.getParameter("PUBLISH_QUERY_URL")+resourceUrl+"&type="+(type!=null?type:"");
				//resourceUrl0 = WebappConfigUtil.getParameter("ADVANCE_SEARCH_RESOURCE_LIST")+resourceUrl0+"&type="+(type==null?"T01,T02,T03,T04,T05,T07,T08,T09,T12,T13":(type.equals("T06")?"T110":type));
				//resourceUrl0 = WebappConfigUtil.getParameter("ASSET_LIST_URL")+resourceUrl0+"&type="+(type!=null?type:"");
				outputResult(http.executeGet(resourceUrl));//+"&status=4"
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}	
	}
	
	@RequestMapping(value = "/resOrder/query")
	@ResponseBody
	public PageResult query(
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam(value = "channelName", required = false) String channelName,
			@RequestParam(value = "status", required = false) String status,
			@RequestParam(value = "createUser", required = false) String createUser) throws UnsupportedEncodingException, ParseException {
		/*QueryConditionList conditionList = getQueryConditionList();
		if (channelName != null) {
			conditionList.addCondition(new QueryConditionItem("channelName", Operator.LIKE, "%"+URLDecoder.decode(channelName,"UTF-8")+"%"));
		}
		if (status != null) {
			conditionList.addCondition(new QueryConditionItem("status", Operator.EQUAL, status));
		}
		if (createUser != null) {
			conditionList.addCondition(new QueryConditionItem("createUser.userName", Operator.LIKE, "%"+URLDecoder.decode(createUser,"UTF-8")+"%"));
		}
		if (startDate != null) {
			conditionList.addCondition(new QueryConditionItem("createTime", Operator.GT, DateUtil.convertStringToDate("yyyy-MM-dd HH:mm:ss", URLDecoder.decode(startDate,"UTF-8"))));
		}
		if (endDate != null) {
			conditionList.addCondition(new QueryConditionItem("createTime", Operator.LT, DateUtil.convertStringToDate("yyyy-MM-dd HH:mm:ss", URLDecoder.decode(endDate,"UTF-8"))));
		}
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		if(userInfo!=null){
			Map<String, String> resMap = userInfo.getResTypes();
			if(resMap!=null){
				Set<String> set = resMap.keySet();
				Iterator<String> it = set.iterator();
				String resTypes = "";
				while(it.hasNext()){
					resTypes += it.next() + ",";
				}
				if(StringUtils.isNotEmpty(resTypes)){
					resTypes = resTypes.substring(0, resTypes.length()-1);
					conditionList.addCondition(new QueryConditionItem("resType", Operator.IN, resTypes));
				}
				
				int isPrivate = userInfo.getIsPrivate();
				String userIds = userInfo.getDeptUserIds();
				if(1==isPrivate){
					if(StringUtils.isNotBlank(userIds)){
						conditionList.addCondition(new QueryConditionItem("createUser.id", Operator.IN, userIds.substring(0, userIds.length()-1)));
					}else{
						conditionList.addCondition(new QueryConditionItem("createUser.id", Operator.EQUAL, userInfo.getUserId()));
					}
				}else{
					
					if(StringUtils.isNotEmpty(userIds)){
						conditionList.addCondition(new QueryConditionItem("createUser.id", Operator.IN, userIds.substring(0, userIds.length()-1)));
					}else{
						conditionList.addCondition(new QueryConditionItem("createUser.id", Operator.IN, "-2"));
					}
				}
			}
		}*/
		/*PageResult pageResult = new PageResult();
		pageResult = resOrderService.query4Page(ResOrder.class, conditionList);
		List<ResOrder> lists=pageResult.getRows();
		List<ResOrder> Zlist = new ArrayList<ResOrder>();
		int size = 0;
		if(userInfo!=null){
			Map<String, String> resMap = userInfo.getResTypes();
			Set<String> set = resMap.keySet();
			Iterator<String> it = set.iterator();
			List<String> listtype = new ArrayList<String>();
			while(it.hasNext()){
				listtype.add(it.next());
			}
			for (ResOrder resOrder : lists) {
				String types = resOrder.getTemplate().getType();
				String type[] = types.split(",");
				List<String> listrestype = new ArrayList<String>();
				for (int j = 0; j < type.length; j++) {
					listrestype.add(type[j]);
				}
				if(listtype.containsAll(listrestype)){
					size++;
					Zlist.add(resOrder);
				}
			}
		}
		PageResult pageResults = new PageResult();
		pageResults.setRows(Zlist);
		pageResults.setTotal(size);*/
		
		
		String sb = "";
		sb +="from ResOrder t where 1=1 ";
		if(channelName != null){
			sb +=" and t.channelName like '%"+URLDecoder.decode(channelName,"UTF-8")+"%' ";
		}
		if(status != null){
			sb +=" and t.status="+status+" ";
		}
		if (createUser != null) {
			sb +=" and t.createUser.userName like '%"+URLDecoder.decode(createUser,"UTF-8")+"%' ";
		}
		if (startDate != null) {
			sb +=" and t.createTime >'"+ URLDecoder.decode(startDate,"UTF-8")+"' ";
		}
		if (endDate != null) {
			sb +=" and t.createTime <'"+ URLDecoder.decode(startDate,"UTF-8")+"'";
		}
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		if(userInfo!=null){
			Map<String, String> resMap = userInfo.getResTypes();
			if(resMap!=null){
				int isPrivate = userInfo.getIsPrivate();
				String userIds = userInfo.getDeptUserIds();
				if(1==isPrivate){
					if(StringUtils.isNotBlank(userIds)){
						sb +=" and t.createUser.id in("+userIds.substring(0, userIds.length()-1)+")";
					}else{
						sb +=" and t.createUser.id ="+userInfo.getUserId();
					}
				}else{
					if(StringUtils.isNotEmpty(userIds)){
						sb +=" and t.createUser.id in("+userIds.substring(0, userIds.length()-1)+")";
					}else{
						sb +=" and t.createUser.id in (-2)";
					}
				}
				
			}
		}
		sb +=" order by t.orderId desc";
		List<ResOrder> list = new ArrayList<ResOrder>();
		PageResult pageResult = new PageResult();
		list=resOrderService.query(sb);
		List<ResOrder> Zlist = new ArrayList<ResOrder>();
		int size=0;
		if(userInfo!=null){
			Map<String, String> resMap = userInfo.getResTypes();
			Set<String> set = resMap.keySet();
			Iterator<String> it = set.iterator();
			List<String> listtype = new ArrayList<String>();
			while(it.hasNext()){
				listtype.add(it.next());
			}
			for (ResOrder resOrder : list) {
				String types = resOrder.getTemplate().getType();
				String type[] = types.split(",");
				List<String> listrestype = new ArrayList<String>();
				for (int j = 0; j < type.length; j++) {
					listrestype.add(type[j]);
				}
				if(listtype.containsAll(listrestype)){
					size++;
					Zlist.add(resOrder);
				}
			}
		}
		HttpServletRequest request = getRequest();
		int pageSize = StringUtil.obj2Int(request.getParameter("rows"));

		int pageNo = StringUtil.obj2Int(request.getParameter("page"));
		int startIndex = (pageNo - 1) * pageSize > 0 ? (pageNo - 1) * pageSize : 0;
		List<ResOrder> orders = new ArrayList<ResOrder>();
		for (int i=startIndex; i < startIndex+pageSize; i++) {
			if(i<Zlist.size()){
				orders.add(Zlist.get(i));
			}else{
				break;
			}
		}
		
		pageResult.setRows(orders);
		pageResult.setTotal(size);
		
		//pageResult=resOrderService.query4Page(ResOrder.class, conditionList);
		return pageResult;
	}
	/**
	 * 需求单上报
	 * @param objectId
	 * @return
	 */
	@RequestMapping(value = "/resOrder/workFlowDes")
	public String workFlowDes(String objectId,String posttype){
		int platformId = LoginUserUtil.getLoginUser().getPlatformId();
		//if(platformId==1){
			return "redirect:/resOrder/doApply.action?objectId="+objectId+"&posttype="+posttype;
		/*}else{
			return "redirect:/pubresOrder/pubresOrderWorkFlow/doApply.action?objectId="+objectId;
		}*/
	}
	
	@RequestMapping(value = "/resOrder/toEdit")
	@Token(save = true)
	public String toEdit(Model model, Long id,String posttype, HttpServletRequest request, HttpServletResponse response) throws ParseException {
		logger.info("进入添加需求单页面");
		int platformId = LoginUserUtil.getLoginUser().getPlatformId();
		model.addAttribute("platformId", platformId);
		if(id==-1){
			ResOrder resOrder = new ResOrder();
			model.addAttribute("resOrder", resOrder);
			model.addAttribute("posttype", posttype);
			return "resRelease/resOrder/resOrderCreate";
		}else{
			ResOrder resOrder = (ResOrder) resOrderService.getByPk(ResOrder.class, id);
			String operateFrom = request.getParameter("operateFrom");
			model.addAttribute("operateFrom", operateFrom);
			model.addAttribute("resOrder", resOrder);
			model.addAttribute("status", ResReleaseConstant.OrderStatus.ORDERADD);
			String posttypes=resOrder.getTemplate().getPosttype();
			if(StringUtils.isBlank(posttype)){
				posttype = posttypes;
			}
			model.addAttribute("posttype", posttypes);
			/*String types=resOrder.getTemplate().getType();
			String type[] = types.split(",");
			List<MetaDataModelGroup> MetaDataModelGroupList = new ArrayList<MetaDataModelGroup>();
			Map<String, String> map = new HashMap<String, String>();
			for (int i = 0; i < type.length; i++) {
				MetaDataModelGroup MetaDataModelGroup=(com.brainsoon.system.model.MetaDataModelGroup) resOrderService.getByPk(MetaDataModelGroup.class, Long.decode(type[i]));
				map.put(MetaDataModelGroup.getId().toString(),MetaDataModelGroup.getTypeName());
			}
			model.addAttribute("map", map);*/
			List<ResOrderDetail> resOrderDetailList = resOrderService.getResOrderDetailByOrderIdAndtype(id.toString(),posttype);
			if(resOrderDetailList.isEmpty()){
				model.addAttribute("flag", "0");
			}else{
				model.addAttribute("flag", "1");
			}
			if(platformId==1){
				UserInfo userInfo = new UserInfo();
				List<Task> tasks = jbpmExcutionService.getCurrentTasks(WorkFlowUtils.getExecuId(id.toString(), "edu"));
				if (tasks != null && tasks.size() > 0) {
					model.addAttribute("wfTaskId",tasks.get(0).getId());
				}
				return "resRelease/resOrder/resOrderEdit";
			}else{
				List<Task> tasks = jbpmExcutionService.getCurrentTasks(WorkFlowUtils.getExecuId(id.toString(), "press"));
				if (tasks != null && tasks.size() > 0) {
					model.addAttribute("wfTaskId",tasks.get(0).getId());
				}
				return "resRelease/pressOrder/presOrderEdit";
			}
				
		}
	}
	/**
	 * 查询资源类型-------------------------在这---------------查类型-------------
	 */
	@RequestMapping(value="/resOrder/queryrestype")
	@ResponseBody
	public Map<String, String> subjectRes(@RequestParam(value="orderId") String orderId,HttpServletRequest request,HttpServletResponse response,Model model){
		logger.info("查询资源类型-----------------------");
		ResOrder resOrder=(ResOrder) resOrderService.getByPk(ResOrder.class, Long.decode(orderId));
		String restype=resOrder.getTemplate().getType();
		String type[] = restype.split(",");
		Map<String, String> map = new HashMap<String, String>();
		for(int i=0;i<type.length;i++){
			MetaDataModelGroup metaDataModelGroup=(MetaDataModelGroup) resOrderService.getByPk(MetaDataModelGroup.class, Long.decode(type[i]));
			map.put(metaDataModelGroup.getId().toString(), metaDataModelGroup.getTypeName());
		}
		return map;
	}
	@RequestMapping(value="/resOrder/querypublishtype")
	@ResponseBody
	public String querypublishtype(HttpServletRequest request,HttpServletResponse response){
		String orderId=request.getParameter("objectId");
		logger.info("查询发布途径-----------------------");
		ResOrder resOrder=(ResOrder) resOrderService.getByPk(ResOrder.class, Long.decode(orderId));
		String restype=resOrder.getTemplate().getPublishType();
		return restype;
	}
	@RequestMapping(value = "/resOrder/add")
	@Token(remove = true)
	public String add(Model model,String posttype,@ModelAttribute("resOrder") ResOrder resOrder, HttpServletRequest request, HttpServletResponse response) {
		logger.info("进入保存需求单方法");
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		int platformId = userInfo.getPlatformId();
		user.setId(userInfo.getUserId());
		String operateDesc = "需求单创建";
		String statusDsc = "未提交";
		try {
			ProdParamsTemplate template = resOrder.getTemplate();
			resOrder.setModuleName(template.getName());
			resOrder.setResType(template.getType());
			resOrder.setPlatformId(platformId);
			resOrder.setCreateTime(new Date());
			resOrder.setStatus(ResReleaseConstant.OrderStatus.CREATED);
			resOrder.setCreateUser(user);
			resOrder = resOrderService.save(resOrder);
			model.addAttribute("resOrder", resOrder);
			model.addAttribute("orderId", resOrder.getOrderId());
			sysOperateService.saveHistory("orderCheck."+resOrder.getOrderId(), resOrder.getAuditRemark(), statusDsc, operateDesc, new Date(), userInfo.getUserId());
			if(platformId==1){
				SysOperateLogUtils.addLog("resOrder_add", resOrder.getChannelName(), userInfo);
			}else{
				SysOperateLogUtils.addLog("presOrder_add", resOrder.getChannelName(), userInfo);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			addActionError(e);
		}
		return "redirect:/resOrder/toEdit.action?id="+resOrder.getOrderId()+"&posttype="+posttype;
	}
	
	@RequestMapping(value = "/resOrder/update")
    public String update(HttpServletRequest request, HttpServletResponse response) {
		String operateFrom = request.getParameter("operateFrom");
		if(!request.getParameter("orderId").equals("-1")){
			Long orderId = Long.valueOf(request.getParameter("orderId"));
			String channelName = request.getParameter("channelName");
			if(StringUtils.isNotBlank(channelName)){
				try {
					channelName = URLDecoder.decode(channelName,"utf-8");
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
			}
			
			String description = request.getParameter("description");
			String flag = request.getParameter("flag");
			Long templateId = Long.valueOf(request.getParameter("templateId"));
			ProdParamsTemplate template = (ProdParamsTemplate) publishTempService.getByPk(ProdParamsTemplate.class, templateId);
			ResOrder resOrder = (ResOrder) resOrderService.getByPk(ResOrder.class, orderId);
			UserInfo userInfo = LoginUserUtil.getLoginUser();
			int platformId = userInfo.getPlatformId();
			if(flag==null){
		        try {
		        	String date = request.getParameter("orderDate");
		        	Date orderDate = DateUtil.convertStringToDate(date);
		        	user.setId(userInfo.getUserId());
		        	resOrder.setUpdateTime(new Date());
		        	resOrder.setOrderDate(orderDate);
		        	resOrder.setUpdateUser(user);
		        	resOrder.setChannelName(channelName);
		        	resOrder.setOrderId(orderId);
		        	resOrder.setTemplate(template);
		        	resOrder.setModuleName(template.getName());
		        	resOrder.setResType(template.getType());
		        	resOrder.setDescription(description);
		        	resOrderService.update(resOrder);
		        	sysOperateService.saveHistory("orderCheck." + resOrder.getOrderId(), "", "未提交", "需求单修改", new Date(), userInfo.getUserId());
		        	if(platformId==1){
		        		SysOperateLogUtils.addLog("resOrder_update", resOrder.getChannelName(), userInfo);
		        	}else{
		        		SysOperateLogUtils.addLog("presOrder_update", resOrder.getChannelName(), userInfo);
		        	}
				} catch (Exception e) {
					    logger.error(e.getMessage());
					    addActionError(e);
				}
	        }else{
	        	resOrder.setModuleName(template.getName());
	        	resOrder.setResType(template.getType());
	        	resOrder.setUpdateTime(new Date());
	        	user.setId(userInfo.getUserId());
	        	resOrder.setUpdateUser(user);
	        	resOrder.setTemplate(template);
	        	resOrderService.update(resOrder);
	        	sysOperateService.saveHistory("orderCheck." + resOrder.getOrderId(), "", "未提交", "需求单修改", new Date(), userInfo.getUserId());
	        	if(platformId==1){
	        		SysOperateLogUtils.addLog("resOrder_update", resOrder.getChannelName(), userInfo);
	        	}else{
	        		SysOperateLogUtils.addLog("presOrder_update", resOrder.getChannelName(), userInfo);
	        	}
	        }
		}
		if("TASK_LIST_PAGE".equals(operateFrom)){
			return "redirect:/TaskAction/toList.action";
		}else{
			return "redirect:/resOrder/list.action";
		}
    }
	
	@RequestMapping(value = "/resOrder/view")
	public String view(Model model, @RequestParam("id") Long id, @RequestParam("posttype") Long posttype,HttpServletRequest request,HttpServletResponse response) {
		int platformId = LoginUserUtil.getLoginUser().getPlatformId();
		ResOrder resOrder = (ResOrder) baseService.getByPk(ResOrder.class, id);
		model.addAttribute("resOrder", resOrder);
		model.addAttribute("posttype", posttype);
		if(platformId==1){
			model.addAttribute("execuId",WorkFlowUtils.getExecuId(id.toString(), "edu"));
			return "resRelease/resOrder/resOrderDetail";
		}
		else{
			model.addAttribute("execuId",WorkFlowUtils.getExecuId(id.toString(), "press"));
			return "resRelease/pressOrder/resOrderDetail";
		}
	}
	
	@RequestMapping(value = "/resOrder/operateHistory")
	public PageResult queryOperateHistory(ResOrder resOrder){
		QueryConditionList conditionList = getQueryConditionList();
		return sysOperateService.query4Page(ResOrder.class, conditionList);
	}
	
	/**
	 * 模板查询
	 * fengda 2015年8月15日修改
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/resOrder/orderPublishTemplateList")
	public @ResponseBody PageResult orderPublishTemplateList(HttpServletRequest request,HttpServletResponse response){
		logger.info("查询资源发布模板列表");
		
		//前台查询条件会自动加载到conditionList中
		QueryConditionList conditionList = getQueryConditionList();
		
		/*//获取当前用户的资源库id
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
					conditionList.addCondition(new QueryConditionItem("type", Operator.IN, resTypes));
				}
			}
			
			//判断当前登录人是否授权，1表示已经授权
			int isPrivate = userInfo.getIsPrivate();
			if(1==isPrivate){
				conditionList.addCondition(new QueryConditionItem("createUser.id", Operator.EQUAL, userInfo.getUserId()));
			}else{
				String userIds = userInfo.getDeptUserIds();
				if(StringUtils.isNotEmpty(userIds)){
					conditionList.addCondition(new QueryConditionItem("createUser.id", Operator.IN, userIds.substring(0, userIds.length()-1)));
				}
			}
		}*/
		
		//添加状态限制 只能选择启用的模板
		conditionList.addCondition(new QueryConditionItem("status", Operator.EQUAL, "1"));
		PageResult pageResult = new PageResult();
		pageResult = publishTempService.query4Page(ProdParamsTemplate.class, conditionList);
		List<ProdParamsTemplate> list=pageResult.getRows();
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		List<ProdParamsTemplate> Zlist = new ArrayList<ProdParamsTemplate>();
		int size = 0;
		//获取当前登录账户的资源类型
			Map<String, String> resMap = userInfo.getResTypes();
			Set<String> set = resMap.keySet();
			Iterator<String> it = set.iterator();
			List<String> listtype = new ArrayList<String>();
			while(it.hasNext()){
				listtype.add(it.next());
			}
			//循环模板的资源类型
			for (ProdParamsTemplate prodParamsTemplate : list) {
				String types = prodParamsTemplate.getType();
				String type[] = types.split(",");
				List<String> listrestype = new ArrayList<String>();
				for (int j = 0; j < type.length; j++) {
					listrestype.add(type[j]);
				}
				if(listtype.containsAll(listrestype)){
					size++;
					Zlist.add(prodParamsTemplate);
				}
			}
			HttpServletRequest requests = getRequest();
			int pageSize = StringUtil.obj2Int(requests.getParameter("rows"));

			int pageNo = StringUtil.obj2Int(requests.getParameter("page"));
			int startIndex = (pageNo - 1) * pageSize > 0 ? (pageNo - 1) * pageSize : 0;
			List<ProdParamsTemplate> paramsTemplates = new ArrayList<ProdParamsTemplate>();
			for (int i=startIndex; i < startIndex+pageSize; i++) {
				if(i<Zlist.size()){
					paramsTemplates.add(Zlist.get(i));
				}else{
					break;
				}
			}	
			
		PageResult pageResults = new PageResult();
		pageResults.setRows(paramsTemplates);
		pageResults.setTotal(size);
		
		return pageResults;
	}

	@RequestMapping(value = "/resRelease/select")
	public String modelSelect(ModelMap model, HttpServletRequest request, HttpServletResponse response){
		logger.info("查看资源发布模板");
		String id = request.getParameter("id");
		String opFrom = request.getParameter("opFrom");
		ParamsTempEntity paramsTempEntity = new ParamsTempEntity();
		if(id!=null){
			ProdParamsTemplate prodParamsTemplate = (ProdParamsTemplate) publishTempService.getByPk(
					ProdParamsTemplate.class, Long.parseLong(id));
			paramsTempEntity = publishTempService.convertEntity(prodParamsTemplate);
		}
		//String supplierName = ((InDefinition)inDefinitionService.getByPk(InDefinition.class, Long.valueOf(paramsTempEntity.getSupplier()))).getName();
		//model.addAttribute("supplierName", supplierName);
		Map<Object, Object> resTypeMap = SysResTypeCacheMap.getMapValue(); 
		String resTypeDesc = (String) resTypeMap.get(paramsTempEntity.getResourceType());
		model.addAttribute("resTypeDesc", resTypeDesc);
		model.addAttribute("paramsTempEntity", paramsTempEntity);
		model.addAttribute("opFrom", opFrom);
		String waterMarkFileType = paramsTempEntity.getWaterMarkFileType();
		if(StringUtils.isNotEmpty(waterMarkFileType)){
			String[] types= StringUtil.strToArray(waterMarkFileType, ",");
			for(String type:types){
				if(type.equals("image")){
					model.addAttribute("image","image");
				}else if(type.equals("video")){
					model.addAttribute("video","video");
				}else{
					model.addAttribute("text","text");
				}
			}
		}
		String metaInfo = paramsTempEntity.getMetaInfo();
		if(metaInfo.indexOf("元数据")>-1){
			model.addAttribute("metaInofFlag", 1);
			String metaDatasCode = paramsTempEntity.getMetaDatasCode();
			if(StringUtils.isNotEmpty(metaDatasCode)){
				String[] codes =  metaDatasCode.split(",");
				//String resType = paramsTempEntity.getResourceType();
				String metadatasDesc = "";
				for(String code : codes){
					MetadataDefinition define = MetadataSupport.getMetadateDefineByUri(code);
					if(define!=null){
						String zhName = define.getFieldZhName();
						if(zhName!=null){
							metadatasDesc += zhName + ",";
						}
					}
				}
				if(metadatasDesc.length()>1){
					metadatasDesc = metadatasDesc.substring(0, metadatasDesc.length()-1);
				}
				model.addAttribute("metadatasDesc", metadatasDesc);
			}
		}
		
		return "resRelease/resOrder/modelDetail";
	}
	
	@RequestMapping(value = "/resOrder/batchDelete")
	public String delete(String ids){
		try {
			UserInfo userInfo = LoginUserUtil.getLoginUser();
			int platformId = userInfo.getPlatformId();
			String[] idArray = ids.split(",");
			for (int i = 0; i < idArray.length; i++) {
				ResOrderDetail resOrderDetail = (ResOrderDetail) resOrderService.getByPk(ResOrderDetail.class, new Long(idArray[i]));
				resOrderService.delete(ResOrderDetail.class, new Long(idArray[i]));
				if(platformId==1){
					SysOperateLogUtils.addLog("resOrder_delResource", resOrderDetail.getResId(), userInfo);
				}else{
					SysOperateLogUtils.addLog("presOrder_delResource", resOrderDetail.getResId(), userInfo);
				}
				
			}
		} catch (DaoException e) {
			logger.error(e.getMessage());
		}
		return "resRelease/resOrder/resOrdersCreateList";
	}
	
	/**
	 * 删除需求单（支持批量）
	 * @param ids
	 * @return
	 */
	@RequestMapping(value = "/resOrder/batchDeleteResOrder")
	@ResponseBody
	public String batchDeleteResOrder(String ids,String posttype){
		String val = "0";
		try {
			val = resOrderWorkFlowService.deleteBatchResOrder(ids,posttype);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return val;
	}
	
	
	@RequestMapping(value = "/resOrder/modelSelect")
	public String modelSelect(@RequestParam("orderId") String orderId, @RequestParam("operateFrom") String operateFrom){
		return "redirect:/resRelease/resOrder/modelSelect.jsp?orderId="+orderId+"&operateFrom="+operateFrom+"&posttype=1";
	}
	
	/**
	 * 保存选择的资源
	 * @param resIds
	 * @param orderId
	 * @param resTypeId
	 * @return
	 */
	@RequestMapping(value = "/resOrder/saveSelectedResource")
	public String saveSelectedResource(String resIds, String orderId,String posttype,String restype){
		if("".equals(resIds)){
			return "redirect:/resOrder/toEdit.action?id="+orderId;
		}else{
			resOrderService.saveResource(resIds, orderId,posttype,restype);
			return "redirect:/resOrder/toEdit.action?id="+orderId;
		}
	}
	
	@RequestMapping(value = "resOrder/saveSelectedResourceByQuery")
	public String saveSelectedResourceByQuery(String cas){
		JSONArray json = JSONArray.fromObject(cas);
		return "";
	}
	
	/**
	 * 清空需求单中的资源
	 * @param orderId
	 * @return
	 */
	@RequestMapping(value = "resOrder/deleteResourceByDeleteType")
	public String deleteResourceByDeleteType(String orderId, String resIds, String deleteType, String operateFrom,String posttype){
		orderId = ""+orderId;
		resOrderService.deleteResList(orderId, deleteType, resIds,posttype);
		return "redirect:/resOrder/toEdit.action?id="+orderId + "&operateFrom=" + operateFrom;
	}
	
	/**
	 * 添加资源
	 * @param orderId
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "resOrder/addResource")
	public String addResource(String orderId, String type, String operateFrom,String posttype, Model model){
		model.addAttribute("orderId", orderId);
		//model.addAttribute("type", type);
		model.addAttribute("posttype", posttype);
		model.addAttribute("operateFrom", operateFrom);
		//String filTree = resOrderService.getFileTree(null, null, 1, 5);
		//if(filTree==null){
		//	model.addAttribute("fileTree", filTree);
		//}
		//String dirTree = resOrderService.getDirTree();
		//model.addAttribute("fileTree", filTree);
		//model.addAttribute("dirTree", dirTree);
		String types[] = type.split(",");
		List<MetaDataModelGroup> lists = new ArrayList<MetaDataModelGroup>();
		for (int i = 0; i < types.length; i++) {
			String restype = types[i];
			MetaDataModelGroup metaDataModelGroup= (MetaDataModelGroup) resOrderService.getByPk(MetaDataModelGroup.class, Long.decode(restype));
			lists.add(metaDataModelGroup);
		}
		model.addAttribute("lists", lists);
		return "resRelease/resOrder/resSelect";
		
	}
	/**
	 * 线上添加资源
	 * @param orderId
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "resOrder/addResourceonline")
	public String addResourceonline(String orderId, String type, String operateFrom,String posttype, Model model){
		model.addAttribute("orderId", orderId);
		model.addAttribute("posttype", posttype);
		model.addAttribute("operateFrom", operateFrom);
		String types[] = type.split(",");
		List<MetaDataModelGroup> lists = new ArrayList<MetaDataModelGroup>();
		for (int i = 0; i < types.length; i++) {
			String restype = types[i];
			MetaDataModelGroup metaDataModelGroup= (MetaDataModelGroup) resOrderService.getByPk(MetaDataModelGroup.class, Long.decode(restype));
			lists.add(metaDataModelGroup);
		}
		model.addAttribute("lists", lists);
		return "resRelease/resOrder/resSelectOnline";
		
	}
	
	@RequestMapping(value = "resOrder/getFileTreeJson")
	@ResponseBody
	public String getFileTreeJson(int page, int size, String param, Long orderId,String restype){
		
		String fileTree = resOrderService.getFileTree(null, null, page, size, param, orderId,restype);
		return fileTree;
	}
	
	@RequestMapping(value = "resOrder/getDirTreeJson")
	@ResponseBody
	public String getDirTreeJson(Long orderId,String restype){
		String dirTree = resOrderService.getDirTree(orderId,restype);
		return dirTree;
	}
	/**
	 * 上报
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "resOrder/canApply")
	@ResponseBody
	public String canApply(HttpServletRequest request,HttpServletResponse response){
		String orderId = request.getParameter("orderId");
		String posttype = request.getParameter("posttype");
		List<List<ResOrderDetail>> lists = new ArrayList<List<ResOrderDetail>>();
		List<ResOrderDetail> list = null;
		String[] ids = orderId.split(",");
		for(String id:ids){
			list = resOrderService.getResOrderDetailByOrderIdAndtype(id,posttype);
			if(list!=null&&list.size()>0){
				lists.add(list);
			}
			
		}
		String result = "";
		if (lists.size() == ids.length) {
			result = "Y";
		} else {
			result = "N";
		}
		return result;
	}
	
	@RequestMapping(value = "/resOrder/publishLog")
	public String publishLog(Model model, @RequestParam("id") Long id) {
		ResOrder resOrder = (ResOrder) baseService.getByPk(ResOrder.class, id);
		model.addAttribute("resOrder", resOrder);
		return "resRelease/resOrder/resOrderPublishLog";
	}
	
	@RequestMapping(value = "resOrder/publishLogQuery")
	@ResponseBody
	public PageResult publishLogQuery(Model model, @RequestParam("orderId") Long orderId, ResOrderDetail resOrderDetail){
		logger.info("进入发布日志记录页面");
		ResOrder resOrder = (ResOrder) resOrderService.getByPk(ResOrder.class, orderId);
		model.addAttribute("resOrder", resOrder);
		resOrderDetail.setOrderId(orderId);
		resOrderDetail.setPlatformId(LoginUserUtil.getLoginUser().getPlatformId());
		PageInfo pageInfo = getPageInfo();
		PageResult pr = resOrderService.queryResOrderDetail(pageInfo, resOrderDetail);
		return resOrderService.queryResOrderDetail(pageInfo, resOrderDetail);
//		return "resRelease/resOrder/resOrderPublishLog";
	}
	
	@RequestMapping("resOrder/caculateResource")
	public void caculateResource(@RequestParam("orderIds") String orderIds){
		resOrderService.caculateResource(orderIds);
	}
	
	@RequestMapping(value = "resOrder/showDirTree")
	@ResponseBody
	public void createModelTree(Model model){
		JSONArray array = new JSONArray();
		if(LoginUserUtil.getLoginUser() != null){
			JSONObject json = new JSONObject();
			json.put("id", 0);
			json.put("name", "资源目录");
			json.put("open", true);
			json.put("pId", -1);
			array.add(json);
			int platformId = LoginUserUtil.getLoginUser().getPlatformId();
			String hql = " from SysDir where platformId=" + platformId;
			List<SysDir> dirList = sysDirService.query(hql);
			for(SysDir dir: dirList) {
				String types = dir.getFileTypes();
				String name = dir.getDirCnName();
				Long id = dir.getId();
				json.put("id", id);
				json.put("name", name);
				json.put("open", true);
				json.put("pId", 0);
				json.put("flag", "_dir");
				array.add(json);
				if(StringUtils.isNotEmpty(types)) {
					String[] typeArr = types.split(",");
					int count = 1;
					for(String type : typeArr) {
						json.put("id", count);
						json.put("name", type);
						json.put("pId", id);
						json.put("open", true);
						json.put("flag", "_file");
						count++;
						array.add(json);
					}
				}
			}
		}
		model.addAttribute("dirTree", array.toString());
	}
	
	@RequestMapping(value = "resOrder/queryResforTreePage")
	@ResponseBody
	public String queryResforTreePage(HttpServletRequest request, Model model){
		//resOrderService.getFileTree(null, null, 1, 1, "", 0);
		String page = request.getParameter("page");
		String size = request.getParameter("count");
		/*JSONArray array = new JSONArray();
		if(LoginUserUtil.getLoginUser() != null){
			JSONObject json = new JSONObject();
			json.put("id", 0);
			json.put("name", "资源文件");
			json.put("open", true);
			json.put("pId", -1);
			array.add(json);
			int platformId = LoginUserUtil.getLoginUser().getPlatformId();
			String hql = " from SysDir where platformId=" + platformId;
			Query query = sysDirService.getSession().createQuery(hql);  
			int maxCount = 10;
			int firstResult = 1;
			query.setMaxResults(maxCount);   //返回最大记录行
			query.setFirstResult(firstResult);   //开始检索的位置
			List<SysDir> dirList = query.list();
			for(SysDir dir: dirList) {
				String types = dir.getFileTypes();
				String name = dir.getDirCnName();
				Long id = dir.getId();
				json.put("id", id);
				json.put("name", name);
				json.put("open", true);
				json.put("pId", 0);
				array.add(json);
				if(StringUtils.isNotEmpty(types)) {
					String[] typeArr = types.split(",");
					int count = 1;
					for(String type : typeArr) {
						json.put("id", count);
						json.put("name", type);
						json.put("pId", id);
						json.put("open", true);
						count++;
						array.add(json);
					}
					
				}
			}
		}
		model.addAttribute("fileTree", array.toString());*/
		String filTree = resOrderService.queryResforTreePage(Integer.valueOf(page).intValue(), Integer.valueOf(size).intValue());
		return filTree;
	}
	
	@RequestMapping(value = "resOrder/queryResDetail")
	@ResponseBody
	public String queryResDetail(String id) {
		String resDetail = "";
		HttpClientUtil http = new HttpClientUtil();
		String url = WebappConfigUtil.getParameter("PUBLISH_DETAIL_URL") + "?id=" + id;
		resDetail = http.executeGet(url);
		Gson gson = new Gson();
		Ca ca = gson.fromJson(resDetail, Ca.class);
		List<File> fileList = ca.getRealFiles();
		JSONArray array = new JSONArray();
		for(File f: fileList){
			JSONObject json = new JSONObject();
			//String path = f.getPath();
			String objId = f.getId();
			String pId = f.getPid();
			String name = f.getName();
			json.put("id", objId);
			if(pId.equals("-1")){
				String path = f.getPath();
				String tempName = path.substring(path.lastIndexOf("/")+1,path.length());
				json.put("name", tempName);	
			}else{
				json.put("name", name);
			}
			
			json.put("pId", pId);
			json.put("open", true);
			array.add(json);
		}
		return array.toString();
	}
	
	@RequestMapping(value = "resOrder/getClassfacationTree")
	@ResponseBody
	public String getClassfacationTree(String moudleName){
		String result = classicService.getMoudleTree(moudleName);
		//gson.fromJson(result, Domain.class);
		JSONArray json = JSONArray.fromObject(result);
		JSONArray tree = new JSONArray();
		for(int i=0;i<json.size();i++){
			Domain domain = (Domain) json.get(i);
			String pid = domain.getPid();
			String name = domain.getLabel();
			String id = domain.getId();
			Map<String, String> map = new HashMap<String, String>();
			map.put("id", id);
			map.put("name", name);
			map.put("pid", pid);
			tree.add(map);
		}
		
		return tree.toString();
	}
	
	@RequestMapping(value = "resOrder/saveAllResource")
	public String saveAllResource(String orderId, String conditions, String dirs,String restype,String posttype){
		if(dirs.startsWith("ext")){
			dirs = dirs.substring(4);
			resOrderService.saveAllResourceByExt(conditions, dirs, orderId,restype,posttype);
			return "redirect:/resOrder/toEdit.action?id="+orderId;
		}
		resOrderService.saveAllResource(conditions, dirs, orderId,restype,posttype);
		return "redirect:/resOrder/toEdit.action?id="+orderId;
	}
	
	@RequestMapping(value = "resOrder/resourceDetail")
	@ResponseBody
	public String resourceDetail(Long orderId, String resId,String posttype){
		List<ResFileRelation> list = resOrderService.queryFileDetail(orderId, resId,posttype);
		String fileIds = "";
		if(list != null){
			for(ResFileRelation rfr : list){
				String fileId = rfr.getFileId();
				fileIds += fileId + ",";
			}
			if(fileIds.length()>1){
				fileIds = fileIds.substring(0, fileIds.length()-1);
			}
		}
		HttpClientUtil http = new HttpClientUtil();
		String url = WebappConfigUtil.getParameter("PUBLISH_FILELIST_URL") + "?page=1&size=1000" + "&objectIds=" + fileIds;
		String resource = http.executeGet(url);
		return resource;
	}
	
	@RequestMapping(value = "resOrder/getResOrderStatus")
	public String getResOrderStatus(Long orderId){
		ResOrder resOrder = (ResOrder) resOrderService.getByPk(ResOrder.class, orderId);
		return resOrder.getStatus();
	}
	
	@RequestMapping(value = "resOrder/getResType")
	@ResponseBody
	public String getResType(Long orderId){
		ResOrder resOrder = (ResOrder) resOrderService.getByPk(ResOrder.class, orderId);
		String resType = resOrder.getTemplate().getType();
		return resType;
	}
	
	@RequestMapping(value = "resOrder/updateStatus")
	@ResponseBody
	public void updateStatus(String orderIds){
		if(StringUtils.isNotEmpty(orderIds)){
			String[] arrs = orderIds.split(",");
			for(String arr : arrs){
				ResOrder resOrder = (ResOrder) resOrderService.getByPk(ResOrder.class, Long.valueOf(arr));
				String publishType = resOrder.getTemplate().getPublishType();
				if(SysParamsTemplateConstants.PublishType.OFF_LINE_DESC.equals(publishType)){
					resOrder.setStatus(ResReleaseConstant.OrderStatus.PUBLISHED);
				}
			}
		}
	}
	
	@RequestMapping(value = "resOrder/getPublishType")
	@ResponseBody
	public String getPublishType(Long orderId){
		ResOrder resOrder = (ResOrder) resOrderService.getByPk(ResOrder.class, orderId);
		return resOrder.getTemplate().getPublishType();
	}
	
	@RequestMapping(value = "resOrder/getMetaInfo")
	@ResponseBody
	public String getMetaInfo(Long orderId){
		String flag = "0";
		ResOrder resOrder = (ResOrder) resOrderService.getByPk(ResOrder.class, orderId);
		String metaInfo = resOrder.getTemplate().getMetaInfo();
		if(StringUtils.isNotEmpty(metaInfo)){
			if(metaInfo.contains("资源文件")){
				flag = "1";
			}
		}
		return flag;
	}
	
	@RequestMapping(value = "resOrder/getFileTreeJsonByQueryCondition")
	@ResponseBody
	public String getFileTreeJsonByQueryCondition(int page, int size, String param, Long orderId){
		
		String fileTree = resOrderService.getFileTreeJsonByQueryCondition(page, size, param, orderId);
		return fileTree;
	}
	
	@RequestMapping(value = "resOrder/getCreatorName")
	@ResponseBody
	public String getCreatorName(Long creatorId){
		User user = (User) userService.getByPk(User.class, creatorId);
		if(user!=null){
			String userName = user.getUserName();
			return userName;
		}
		return "";
	}

	@Override
	@InitBinder
	protected void initBinder(HttpServletRequest request,
            ServletRequestDataBinder binder) throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	    CustomDateEditor dateEditor = new CustomDateEditor(format, true);
	    binder.registerCustomEditor(Date.class, dateEditor);
	    super.initBinder(request, binder);
	}
	
	
	@RequestMapping(value = "resOrder/getExtensionName")
	@ResponseBody
	public String getExtensionName(HttpServletRequest request,HttpServletResponse response){
		logger.info("****资源文件扩展名****");
		JSONArray array = new JSONArray();
		int id = 1;//扩展名的ID
		int count = 1;//扩展名目录的ID
		
		LinkedHashMap<String, String> metaInfoMap = dictNameService.getDictMapByName("资源文件扩展名");
		
		JSONObject json = new JSONObject();
		json.put("id", 0);
		json.put("name", "扩展名目录");
		json.put("open", true);
		json.put("pId", -1);
		json.put("flag", "extensionNameDirs");
		array.add(json);
		
		for (Map.Entry<String, String> entry : metaInfoMap.entrySet()) {
			//result += entry.getValue()+":"+entry.getKey()+";";
			json.put("id", id);
			json.put("name", entry.getValue());
			json.put("open", true);
			json.put("pId", 0);
			json.put("flag", "extensionNameDir");
			array.add(json);
			id++;
			
			String[] types = entry.getKey().split(",");
			for (int i = 0; i < types.length; i++) {
				if (StringUtils.isNotEmpty(types[i])) {
					json.put("id", id);
					json.put("name", types[i]);
					json.put("pId", count);
					json.put("open", true);
					json.put("flag", "extensionName");
					id++;
					array.add(json);
				}
			}
			count=id;
		}
		
		/*if (result.endsWith(";")) {
			result=result.substring(0, result.length()-1);
		}
		logger.info("资源文件扩展名："+result);*/
		
		return array.toString();
	}
	/**
	 * 增加资源
	 */
	@RequestMapping(value = "resOrder/addres")
	@ResponseBody
	public String addres(HttpServletRequest request,HttpServletResponse response){
		String resId = request.getParameter("resId");
		String posttype = request.getParameter("posttype");
		String restype = request.getParameter("restype");
		String orderId = request.getParameter("orderId");
		String resule = resOrderService.addResResource(resId, orderId, posttype, restype);
		return resule;
	}
	@RequestMapping(value="resOrder/addAllres")
	public String addAllres(HttpServletRequest request,HttpServletResponse response){
		String metadataMap = request.getParameter("params");
		try {
			metadataMap = URLEncoder.encode(metadataMap.trim(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String total = request.getParameter("total");
		String orderId = request.getParameter("orderId");
		String posttype =  request.getParameter("posttype");
		String restype = request.getParameter("restype");
		String formList = searchIndexService.queryFormList(metadataMap,restype,"1",total,"1",null);
		System.out.println(formList);
		String objectid="";
		JSONObject json = JSONObject.fromObject(formList);
		JSONArray jsonarray = json.getJSONArray("rows");
		if(jsonarray!=null){
			for (int i = 0; i < jsonarray.size(); i++) {
				JSONObject ob= (JSONObject) jsonarray.get(i);
				objectid += ob.getString("objectId")+",";
			}
		}
		objectid = objectid.substring(0, objectid.length()-1);
		return "redirect:/resOrder/addres.action?resId="+objectid+"&posttype="+posttype+"&restype="+restype+"&orderId="+orderId;
	}
}
