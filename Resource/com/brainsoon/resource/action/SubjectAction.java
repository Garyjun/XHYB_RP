package com.brainsoon.resource.action;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.jbpm.api.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.action.Token;
import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.appframe.util.StringUtil;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.jbpm.service.IJbpmExcutionService;
import com.brainsoon.resource.po.SubjectStore;
import com.brainsoon.resource.service.IResWorkFlowService;
import com.brainsoon.resource.service.ISubjectService;
import com.brainsoon.resource.support.WorkFlowUtils;
import com.brainsoon.resrelease.po.ProdParamsTemplate;
import com.brainsoon.resrelease.po.ResOrderDetail;
import com.brainsoon.resrelease.service.IPublishTempService;
import com.brainsoon.resrelease.service.IResOrderService;
import com.brainsoon.resrelease.service.IResOrderWorkFlowService;
import com.brainsoon.resrelease.support.ResReleaseConstant;
import com.brainsoon.system.model.MetaDataModelGroup;
import com.brainsoon.system.model.User;
import com.brainsoon.system.service.ISysOperateService;
import com.brainsoon.system.support.SysOperateLogUtils;

/**
 * @ClassName: SubjectAction
 * @Description: TODO
 * @author 
 * @date 2015年9月24日 下午4:00:12
 *
 */
@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SubjectAction extends BaseAction{
	/**默认命名空间**/
	private static final String baseUrl = "/subject/";
	@Autowired
	private ISubjectService subjectService;
	@Autowired
	private IResOrderService resOrderService;
	@Autowired
	private IJbpmExcutionService jbpmExcutionService;
	@Autowired
	private IPublishTempService publishTempService;
	@Autowired
	private ISysOperateService sysOperateService;
	@Autowired
	private IResOrderWorkFlowService resOrderWorkFlowService;
	@Autowired
	private IResWorkFlowService resWorkFlowService;
	
	UserInfo userinfo = LoginUserUtil.getLoginUser();
	
	public  static final String FILE_ROOT = StringUtils.replace(WebAppUtils.getWebAppBaseFileDirFR(),"\\", "/");
	public final static String FILE_TEMP = WebAppUtils.getWebRootBaseDir(ConstantsDef.fileTemp);
	public final static String PROD_FILE = WebAppUtils.getWebRootBaseDir(ConstantsDef.prodFile);
	
	//查询该主题库是线上还是线下发布（支持批量）
	@RequestMapping(value=baseUrl+"querywarnings")
	@ResponseBody
	public String querywarnings(HttpServletRequest request,HttpServletResponse response){
		int va = 0;
		String  ids = request.getParameter("ids");
		if(StringUtils.isNotBlank(ids)){
			String id[] = ids.split(",");
			for (int i = 0; i < id.length; i++) {
				SubjectStore store;
				try {
					store = (SubjectStore) subjectService.getByPk(SubjectStore.class, Long.decode(id[i]));
				} catch (Exception e) {
					return "0";
				}
				String pubtype = store.getTemplate().getPublishType();
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

	/**
	 * 通知功能
	 */
	@RequestMapping(value=baseUrl+"warnings")
	@ResponseBody
	public String warnings(HttpServletRequest request,HttpServletResponse response){
		String ids = request.getParameter("ids");
		HttpClientUtil http = new HttpClientUtil();
		logger.info("这是url----->http://app.cciph.com.cn/?app=list&controller=index&action=auto_import_all&id="+ids);
		String results = http.executeGet("http://app.cciph.com.cn/?app=list&controller=index&action=auto_import_all&id="+ids);
		logger.info(" 这是调用接口之后返回的结果------>"+results);
		try {
			JSONObject jsonObject = JSONObject.fromObject(results);
			if(jsonObject.getString("state").equals("0")){
				String id[] = ids.split(",");
				for (String orderid : id) {
				    SubjectStore subjectStore = (SubjectStore) subjectService.getByPk(SubjectStore.class, Long.parseLong(orderid));
					SysOperateLogUtils.addLog("subject_publish", subjectStore.getName(), userinfo);
				}
				return "1";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "0";
	}
	/**
	 * 主题库名称异步验证
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping(value=baseUrl+"validatesubjectNameAdd")
	@ResponseBody
	public String validatesubjectNameAdd(HttpServletRequest request) throws UnsupportedEncodingException{
		request.setCharacterEncoding("utf-8");
		String name = request.getParameter("name");
		String id = request.getParameter("id");
		logger.info("第一次出现的name值----》"+name);
		logger.info("第一次出现的id值----》"+id);
		/*name = new String(name.getBytes("iso8859-1"),"UTF-8");
		logger.info("我是采用iso8859-1转换之后的值----》"+name);*/
		String sql = "from SubjectStore where name='"+name+"' ";
		if(id!=null){
			sql += " and id not in('"+id+"')";
		}
		List<SubjectStore> nameList = subjectService.query(sql);
		if(nameList.size()<1){
			return "{\"jsonValidateReturn\": [\"name\",true]}";
		}
		return "{\"jsonValidateReturn\": [\"name\",false]}";
	}
	/**
	 * 列表list页面查询
	 */
	@RequestMapping(value=baseUrl+"queryList")
	@ResponseBody
	public PageResult querySubject(@RequestParam(value="name",required=false) String name,
			@RequestParam(value="status",required=false) String status,
			@RequestParam(value="storeType",required=false) String storeType,
			@RequestParam(value="subject",required=false) String subject,
			@RequestParam(value="trade",required=false) String trade,
			@RequestParam(value="collectionStart",required=false) String collectionStart,
			@RequestParam(value="collectionEnd",required=false) String collectionEnd,
			HttpServletRequest request,HttpServletResponse response,Model model) throws UnsupportedEncodingException{
		/*QueryConditionList conditionList = getQueryConditionList();
		if (name != null) {
			name= URLDecoder.decode(URLDecoder.decode(name, "utf-8"),"utf-8");
			conditionList.addCondition(new QueryConditionItem("name", Operator.LIKE, "%"+name+"%"));
		}
		if (status != null && !status.equals("")) {
			conditionList.addCondition(new QueryConditionItem("status", Operator.EQUAL, status));
		}
		if (storeType != null && !storeType.equals("")) {
			conditionList.addCondition(new QueryConditionItem("storeType", Operator.EQUAL, storeType));
		}
		if (subject != null && !subject.equals("")) {
			conditionList.addCondition(new QueryConditionItem("subject", Operator.EQUAL, subject));
		}
		if (trade != null && !trade.equals("")) {
			conditionList.addCondition(new QueryConditionItem("trade", Operator.EQUAL, trade));
		}
		if (collectionStart != null && !collectionStart.equals("")) {
			conditionList.addCondition(new QueryConditionItem("collectionStart", Operator.GTE, Integer.parseInt(collectionStart)));
		}
		if (collectionEnd != null && !collectionEnd.equals("")) {
			conditionList.addCondition(new QueryConditionItem("collectionEnd", Operator.LTE, Integer.parseInt(collectionEnd)));
		}
		
		return subjectService.query4Page(SubjectStore.class, conditionList);*/
		String sb = "";
		sb +="from SubjectStore t where 1=1 ";
		if(name != null){
			sb +=" and t.name like '%"+URLDecoder.decode(URLDecoder.decode(name,"UTF-8"),"utf-8")+"%' ";
		}
		if(status != null){
			sb +=" and t.status="+status+" ";
		}
		if (storeType != null) {
			sb +=" and t.storeType ="+storeType+" ";
		}
		if (subject != null) {
			sb +=" and t.subject ='"+subject+"' ";
		}
		if (trade != null) {
			sb +=" and t.trade ="+trade+" ";
		}
		if (collectionStart != null) {
			sb +=" and t.collectionStart >="+collectionStart+" ";
		}
		if (collectionEnd != null) {
			sb +=" and t.collectionEnd <="+collectionEnd+" ";
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
		sb +=" order by t.id desc";
		List<SubjectStore> list = new ArrayList<SubjectStore>();
		PageResult pageResult = new PageResult();
		list=subjectService.query(sb);
		List<SubjectStore> Zlist = new ArrayList<SubjectStore>();
		int size=0;
		if(userInfo!=null){
			Map<String, String> resMap = userInfo.getResTypes();
			Set<String> set = resMap.keySet();
			Iterator<String> it = set.iterator();
			List<String> listtype = new ArrayList<String>();
			while(it.hasNext()){
				listtype.add(it.next());
			}
			for (SubjectStore store : list) {
				String types = store.getTemplate().getType();
				String type[] = types.split(",");
				List<String> listrestype = new ArrayList<String>();
				for (int j = 0; j < type.length; j++) {
					listrestype.add(type[j]);
				}
				if(listtype.containsAll(listrestype)){
					size++;
					Zlist.add(store);
				}
			}
		}
		HttpServletRequest requests = getRequest();
		int pageSize = StringUtil.obj2Int(requests.getParameter("rows"));

		int pageNo = StringUtil.obj2Int(requests.getParameter("page"));
		int startIndex = (pageNo - 1) * pageSize > 0 ? (pageNo - 1) * pageSize : 0;
		List<SubjectStore> stores = new ArrayList<SubjectStore>();
		for (int i=startIndex; i < startIndex+pageSize; i++) {
			if(i<Zlist.size()){
				stores.add(Zlist.get(i));
			}else{
				break;
			}
		}
		
		pageResult.setRows(stores);
		pageResult.setTotal(size);
		
		return pageResult;
	}
	/**
	 * 跳转主题库增加页面
	 */
	@RequestMapping(value=baseUrl+"subjectAdd")
	@Token(save=true)
	public String subjectAdd(@RequestParam(value="id") String id,@RequestParam(value="posttype") String posttype,HttpServletRequest request,HttpServletResponse response,Model model){
		logger.info("------------跳转增加页面--------------");
		List<SubjectStore> lists=subjectService.query("from SubjectStore");
		model.addAttribute("posttype", posttype);
		model.addAttribute("lists", lists);
		return baseUrl+"subjectAdds";
	}
	/**
	 * 跳转修改页面
	 */
	@RequestMapping(value=baseUrl+"subjectEdit")
	@Token(save=true)
	public String subjectEdit(@RequestParam(value="id") String id,@RequestParam(value="posttype") String posttype,HttpServletRequest request,HttpServletResponse response,Model model){
		logger.info("------------修改前根据id查询--------------");
		int platformId = LoginUserUtil.getLoginUser().getPlatformId();
		model.addAttribute("platformId", platformId);
		logger.info("id的值----》"+id);
		SubjectStore subjectStore = (SubjectStore) subjectService.getByPk(SubjectStore.class, Long.decode(id));
		String operateFrom = request.getParameter("operateFrom");
		model.addAttribute("operateFrom", operateFrom);
		model.addAttribute("subjectStore", subjectStore);
		List<SubjectStore> lists=subjectService.query("from SubjectStore t where t.id !="+Long.decode(id));
		model.addAttribute("lists", lists);
		model.addAttribute("posttype", posttype);
		List<ResOrderDetail> resOrderDetailList = resOrderService.getResOrderDetailByOrderIdAndtype(id.toString(), posttype);
		if(resOrderDetailList.size()==0){
			model.addAttribute("flag", "0");
		}else{
			model.addAttribute("flag", "1");
		}
		if(platformId==1){
			List<Task> tasks = jbpmExcutionService.getCurrentTasks(WorkFlowUtils.getExecuId(id.toString(), "sub"));
			if (tasks != null && tasks.size() > 0) {
				model.addAttribute("wfTaskId",tasks.get(0).getId());
			}
			return baseUrl+"subjectEdit";
		}else{
			List<Task> tasks = jbpmExcutionService.getCurrentTasks(WorkFlowUtils.getExecuId(id.toString(), "subject"));
			if (tasks != null && tasks.size() > 0) {
				model.addAttribute("wfTaskId",tasks.get(0).getId());
			}
			return "resRelease/pressOrder/presOrderEdit";
		}
	}
	/**
	 * 弹出模板页面
	 */
	@RequestMapping(value = baseUrl+"modelSelect")
	public String modelSelect(@RequestParam("id") String orderId, @RequestParam("operateFrom") String operateFrom, @RequestParam("posttype") String posttype){
		return "redirect:/subject/modelSelect.jsp?id="+orderId+"&operateFrom="+operateFrom+"&posttype="+posttype;
	}
	
	
	/**
	 * 增加功能
	 */
	@RequestMapping(value=baseUrl+"subjectAddSub")
	@Token(remove=true)
	@ResponseBody
	public String subjectAddSub(Model model,@ModelAttribute(value="command") SubjectStore stores,HttpServletRequest request,HttpServletResponse response){
		logger.info("进入增加功能方法");
		int platformId = userinfo.getPlatformId();
		User user = new User();
		user.setId(userinfo.getUserId());
		String operateDesc = "主题库创建";
		String statusDsc = "未提交";
		try {
			ProdParamsTemplate template = stores.getTemplate();
			stores.setModuleName(template.getName());
			stores.setRestype(template.getType());
			stores.setPlatformId(platformId);
			stores.setCreateTime(new Date());
			stores.setStatus(ResReleaseConstant.OrderStatus.CREATED);
			stores.setCreateUser(user);
			stores=subjectService.save(stores);
			model.addAttribute("stores", stores);
			sysOperateService.saveHistory("subjectCheck."+stores.getId(), stores.getAuditMsg(), statusDsc, operateDesc, new Date(), userinfo.getUserId());
			if(platformId==1){
				SysOperateLogUtils.addLog("subject_add", stores.getName(), userinfo);
			}else{
				SysOperateLogUtils.addLog("presOrder_add", stores.getName(), userinfo);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			addActionError(e);
		}
		return stores.getId().toString();
	}
	/**
	 * 查询资源类型
	 */
	@RequestMapping(value=baseUrl+"subjectRes")
	@ResponseBody
	public Map<String, String> subjectRes(@RequestParam(value="restype") String restype,HttpServletRequest request,HttpServletResponse response,Model model){
		logger.info("查询资源类型-----------------------");
		String type[] = restype.split(",");
		Map<String, String> map = new HashMap<String, String>();
		for(int i=0;i<type.length;i++){
			MetaDataModelGroup metaDataModelGroup=(MetaDataModelGroup) subjectService.getByPk(MetaDataModelGroup.class, Long.decode(type[i]));
			map.put(metaDataModelGroup.getId().toString(), metaDataModelGroup.getTypeName());
		}
		return map;
	}
	/**
	 * 删除主题库（支持批量）
	 * @param ids
	 * @return
	 */
	@RequestMapping(value = baseUrl+"batchDeletesubject")
	@ResponseBody
	public String batchDeletesubject(String ids,String posttype){
		String val = "0";
		try {
			val = subjectService.deleteBatchSubject(ids,posttype);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return val;
	}
	
	/**
	 * 主题库上报
	 */
	@RequestMapping(value = baseUrl+"doApply")
	@ResponseBody
	public String workFlowDes(String objectId,String posttype){
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
				    resOrderWorkFlowService.doApplytoZtk(ordId,posttype);
				}
				result = objIds.length==1?"上报成功!":"批量上报成功!"; 
			}else{
				result = objIds.length==1?"上报失败！":"批量上报的主题库中有没有添加资源的主题库，批量上报失败！";
			}
		} catch (Exception e) {
			result = "上报失败";
		}
		
		return result;
	}
	/**
	 * 保存并提交上报
	 */
	@RequestMapping(value = baseUrl+"resOrderWorkFlow/doSaveAndSubmit")
	@ResponseBody
	public String doSaveAndSubmit(@ModelAttribute(value="command") SubjectStore stores,String wfTaskId,String posttypes,String operateFrom1, Model model){
		try {
			resOrderWorkFlowService.doSaveAndSubmittoSubject(stores,wfTaskId,posttypes);
			model.addAttribute("operateFrom", operateFrom1);
			return "ok";
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}
	/**
	 * 跳转-审核页面
	 * @return
	 */
	@RequestMapping(value = baseUrl+"gotoCheck")
	public String gotoCheck(@RequestParam("objectId") String objectId, 
			@RequestParam(value = "execuId", required = false) String execuId,
			@RequestParam(value = "wfTaskId", required = false) String wfTaskId,
			@RequestParam("operateFrom") String operateFrom,@RequestParam("posttype") String posttype,
			Model model){
		logger.info("进入主题库审核页面！");
		Map<String, String> map = resWorkFlowService.getWorkFlowInfo(WorkFlowUtils.getExecuId(objectId,"sub"));
		execuId = map.get("execuId");
		wfTaskId = map.get("wfTaskId");
		SubjectStore store = (SubjectStore) subjectService.getByPk(SubjectStore.class, Long.valueOf(objectId));
		model.addAttribute("execuId", execuId);
		model.addAttribute("wfTaskId", wfTaskId);
		model.addAttribute("subjectStore",store);
		model.addAttribute("posttype",posttype);
		model.addAttribute("operateFrom", operateFrom);
		List<ResOrderDetail> resOrderDetailList = resOrderService.getResOrderDetailByOrderIdAndtype(objectId, posttype);
		if(resOrderDetailList.size()==0){
			model.addAttribute("flag", "0");
		}else{
			model.addAttribute("flag", "1");
		}
		return "/subject/subjectAudit"; 
	}
	/**
	 * 审核(通过，驳回)
	 * 
	 * @param asset
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping(value = baseUrl+"doCheck")
	@ResponseBody
	public String doCheck(@RequestParam("objectId") String objectId, 
			@RequestParam("operateFrom") String operateFrom, 
			@RequestParam("decision") String decision,
			@RequestParam(value = "wfTaskId", required = false) String wfTaskId, 
			@RequestParam("auditMsg") String auditMsg) throws Exception{
		auditMsg=URLDecoder.decode(auditMsg, "utf-8");
		String status = ResReleaseConstant.OrderStatus.AUDITED;
		if(decision.equals("通过")){
			resOrderWorkFlowService.doChecktoZTK(objectId, status, auditMsg,wfTaskId, decision);
		}else{
			status =  ResReleaseConstant.OrderStatus.AUDIT_REFUSE;
			resOrderWorkFlowService.doChecktoZTK(objectId, status, auditMsg,wfTaskId, decision);
		}
		if (StringUtils.equals(operateFrom, "TASK_LIST_PAGE")) {
			return "/TaskAction/toList.action";
		} else {
			return "/subject/subjectList.jsp";
		}
	}
	/**
	 * 跳转到编辑页面(驳回编辑)
	 * 
	 * @param asset
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping(value = baseUrl+"gotoEdit")
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
		SubjectStore subjectStore = (SubjectStore) subjectService.getByPk(SubjectStore.class, Long.valueOf(objectId));
		model.addAttribute("subjectStore", subjectStore);
		List<ResOrderDetail> resOrderDetailList = resOrderService.getResOrderDetailByOrderIdAndtype(objectId,posttype);
		if(resOrderDetailList.size()==0){
			model.addAttribute("flag", "0");
		}else{
			model.addAttribute("flag", "1");
		}
		if(StringUtils.isNotBlank(objectId)){
			return baseUrl+"subjectEdit";
		}else{
			return baseUrl+"subjectEdit";
		}
	}
	@RequestMapping(value = baseUrl+"canBatchProcess")
	@ResponseBody
	public int canBatchProcess(String orderIds){
		int flag = 0;
		if(StringUtils.isNotEmpty(orderIds)){
			String[] ids = orderIds.split(",");
			int len = ids.length;
			int num = 0;
			for(String id: ids){
				SubjectStore subjectStore = (SubjectStore) subjectService.getByPk(SubjectStore.class, Long.valueOf(id));
				if(ResReleaseConstant.OrderStatus.AUDITED.equals(subjectStore.getStatus()) || ResReleaseConstant.OrderStatus.PROCESSEDWRONG.equals(subjectStore.getStatus())){
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
	 * 查看详细
	 */
	@RequestMapping(value=baseUrl+"subjectQuery")
	public String subjectQuery(@RequestParam(value="id") String id,@RequestParam(value="posttype") String posttype,@RequestParam(value="datatype",required=false)String datatype,HttpServletRequest request,HttpServletResponse response,Model model){
		SubjectStore subjectStore=(SubjectStore) subjectService.getByPk(SubjectStore.class, Long.decode(id));
		SubjectStore subjectStores = (SubjectStore) subjectService.getByPk(SubjectStore.class, Integer.valueOf(subjectStore.getPid()).longValue());
		model.addAttribute("subjectStore", subjectStore);
		model.addAttribute("subjectStores", subjectStores);
		model.addAttribute("posttype", posttype);
		model.addAttribute("datatype", datatype);
		model.addAttribute("execuId",WorkFlowUtils.getExecuId(id.toString(), "sub"));
		return baseUrl+"subjectDetail";
	}
	/**
	 * 修改功能
	 */
	@RequestMapping(value=baseUrl+"subjectUpdSub")
	@ResponseBody
	public String subjectUpdSub(Model model,@ModelAttribute(value="command") SubjectStore stores,HttpServletRequest request,HttpServletResponse response){
		String operateFrom = request.getParameter("operateFrom");
		if(!request.getParameter("id").equals("-1")){
			String flag = request.getParameter("flag");
			SubjectStore subjectStore = (SubjectStore) subjectService.getByPk(SubjectStore.class, stores.getId());
			stores.setStatus(subjectStore.getStatus());
			stores.setAuditor(subjectStore.getAuditor());
			stores.setAuditTime(subjectStore.getAuditTime());
			stores.setAuditMsg(subjectStore.getAuditMsg());
			stores.setCreateUser(subjectStore.getCreateUser());
			stores.setCreateTime(subjectStore.getCreateTime());
			stores.setJsonOkStatus(subjectStore.getJsonOkStatus());
			stores.setExcelOkStatus(subjectStore.getExcelOkStatus());
			stores.setPlatformId(subjectStore.getPlatformId());
			stores.setRemark(subjectStore.getRemark());
			stores.setProcessremark(subjectStore.getProcessremark());
			Long templateId = Long.decode(request.getParameter("templateId"));
			ProdParamsTemplate template = (ProdParamsTemplate) publishTempService.getByPk(ProdParamsTemplate.class, templateId);
			User user = new User();
			if(flag==null){
		        try {
		        	stores.setUpdateTime(new Date());
		        	user.setId(userinfo.getUserId());
		        	stores.setUpdateUser(user);
		        	stores.setTemplate(template);
		        	stores.setModuleName(template.getName());
		        	stores.setRestype(template.getType());
		        	subjectService.saveOrUpdate(stores);
		        	sysOperateService.saveHistory("subjectCheck." + stores.getId(), "", "未提交", "主题库修改", new Date(), userinfo.getUserId());
		        	SysOperateLogUtils.addLog("subject_update", stores.getName(), userinfo);
				} catch (Exception e) {
					    logger.error(e.getMessage());
					    addActionError(e);
				}
	        }else{
	        	subjectStore.setModuleName(template.getName());
	        	subjectStore.setRestype(template.getType());
	        	subjectStore.setUpdateTime(new Date());
	        	user.setId(userinfo.getUserId());
	        	subjectStore.setUpdateUser(user);
	        	subjectStore.setTemplate(template);
	        	subjectService.saveOrUpdate(subjectStore);
	        	sysOperateService.saveHistory("subjectCheck." + stores.getId(), "", "未提交", "主题库修改", new Date(), userinfo.getUserId());
	        	SysOperateLogUtils.addLog("subject_update", stores.getName(), userinfo);
	        }
		}
		if("TASK_LIST_PAGE".equals(operateFrom)){
			return "/TaskAction/toList.action";
		}else{
			return "/subject/subjectList.action";
		}
	}
	@RequestMapping(value=baseUrl+"subjectList")
	public String  subjectlist(){
		return "/subject/subjectList";
	}
	/**
	 * 清空主题库中的资源
	 * @param orderId
	 * @return
	 */
	@RequestMapping(value = baseUrl+"deleteResourceByDeleteType")
	public String deleteResourceByDeleteType(String orderId, String resIds, String deleteType, String operateFrom,String posttype){
		orderId = ""+orderId;
		resOrderService.deleteResList(orderId, deleteType, resIds,posttype);
		return "redirect:/subject/subjectEdit.action?id="+orderId + "&operateFrom=" + operateFrom+"&posttype="+posttype;
	}
	/**
	 * 快速挑选前查询该主题库所拥有的资源类型名称
	 */
	@RequestMapping(value=baseUrl+"ksshaixuan")
	public String resksshaixuan(@RequestParam(value="id")String id,@RequestParam(value="posttype")String posttype,HttpServletRequest request,HttpServletResponse response,Model model){
		SubjectStore subjectStore= (SubjectStore) subjectService.getByPk(SubjectStore.class, Long.decode(id));
		String restype=subjectStore.getTemplate().getType();
		String type[]=restype.split(",");
		List<MetaDataModelGroup> lists = new ArrayList<MetaDataModelGroup>();
		for(int i=0;i<type.length;i++){
			MetaDataModelGroup metaDataModelGroup=(MetaDataModelGroup) subjectService.getByPk(MetaDataModelGroup.class, Long.decode(type[i]));
			lists.add(metaDataModelGroup);
		}
		model.addAttribute("id", id);
		model.addAttribute("posttype", posttype);
		model.addAttribute("lists", lists);
		return "/subject/kstxztk";
	}
	/**
	 * 快速查询页面。查询详细
	 */
	@RequestMapping(value=baseUrl+"tobztx",method = {RequestMethod.POST})
	@ResponseBody
	public PageResult tobzcx(@RequestParam(value="publishType",required=false)String publishTypes,
			@RequestParam(value="name")String ztkname,
			@RequestParam(value="storeType")String storeType,
			@RequestParam(value="subLibclassify")String subLibclassify,
			@RequestParam(value="id")String id,
			HttpServletRequest request,HttpServletResponse response,Model model){
		PageResult result = new PageResult();
		List<SubjectStore> subjectStores = new ArrayList<SubjectStore>();
		Set set = new HashSet();
		//页面初始化。publishTypes==null,直接跳转页面。不查询
		if(!publishTypes.equals("null")){
			String type[] = publishTypes.split(",");
			StringBuffer sql = new StringBuffer();
			sql.append("from SubjectStore t where t.status not in('"+ResReleaseConstant.OrderStatus.CREATED+"','"+ResReleaseConstant.OrderStatus.TO_AUDIT+"','"+ResReleaseConstant.OrderStatus.AUDIT_REFUSE+"') and t.id not in("+Long.decode(id)+")");
			if(StringUtils.isNotBlank(ztkname)){
				try {
					ztkname = URLDecoder.decode(ztkname, "utf-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				sql.append(" and t.name like '%"+ztkname+"%'");
			}
			if(StringUtils.isNotBlank(storeType)){
				sql.append(" and t.storeType = '"+storeType+"'");
			}
			if(StringUtils.isNotBlank(subLibclassify)){
				sql.append(" and t.subLibclassify ='"+subLibclassify+"'");
			}
			//查询除本身之外的状态为审核通过的全部信息
			List<SubjectStore> list=subjectService.query(sql.toString());
			for (int i = 0; i < list.size(); i++) {
				//取其中一个查询出来的值获取资源类型字段并解析
				SubjectStore subjectStore=list.get(i);
				String restype = subjectStore.getTemplate().getType();
				String res[] = restype.split(",");
				for (int j = 0; j < res.length; j++) {
					//查询出来 的实体和前台type循环比较,有一个相同，就放入set
					for(int k=0;k<type.length;k++){
						if(res[j].equals(type[k])){
							//subjectStores.add(subjectStore);
							set.add(subjectStore.getId());
						}
					}
				}
			}
			//set防止ID重复
			Iterator it=set.iterator();
			while (it.hasNext()){
				SubjectStore store=(SubjectStore) subjectService.getByPk(SubjectStore.class, (Long)it.next());
				subjectStores.add(store);
			}
		}
		HttpServletRequest requests = getRequest();
		int pageSize = StringUtil.obj2Int(requests.getParameter("rows"));

		int pageNo = StringUtil.obj2Int(requests.getParameter("page"));
		int startIndex = (pageNo - 1) * pageSize > 0 ? (pageNo - 1) * pageSize : 0;
		List<SubjectStore> stores = new ArrayList<SubjectStore>();
		for (int i=startIndex; i < startIndex+pageSize; i++) {
			if(i<subjectStores.size()){
				stores.add(subjectStores.get(i));
			}else{
				break;
			}
		}
		result.setRows(stores);
		result.setTotal(subjectStores.size());
		return result;
		
	}
	/**
	 * 快速挑选，批量增加资源到子表中
	 */
	@RequestMapping(value=baseUrl+"addsaveres")
	@ResponseBody
	public String addsaveres(@RequestParam(value="resid",required=false) String resid,
			@RequestParam(value="id") String id,
			@RequestParam(value="publishType",required=false) String publishTypes,
			@RequestParam(value="posttype") String posttype,
			HttpServletRequest request,HttpServletResponse response){
		String result = subjectService.addSubjectRes(resid, id, posttype, publishTypes);
		return result;
	}
	/**
	 * loog上传,
	 * @param request
	 * @param response
	 * @param model
	 */
	@RequestMapping(value=baseUrl+"uploadFileToTemp")
	public @ResponseBody HashMap<String, Object> uploadFileToTemp(HttpServletRequest request, HttpServletResponse response,ModelMap model){
		logger.info("---------------------------------进入文件上传路径方法-----------------------------------------------");
		String logo = request.getParameter("logo");
		HashMap<String, Object> rtn = new HashMap<String, Object>();
		int status = 0;
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		MultipartFile multipartFile = null;
		for (Map.Entry<String, MultipartFile> set : fileMap.entrySet()) {
			multipartFile = set.getValue();// 文件名
		}
		String typeStatus = "1";
		String fileName = multipartFile.getOriginalFilename();
		String fileType = fileName.substring(fileName.lastIndexOf(".")+1,fileName.length());
		Long name  = System.currentTimeMillis();
		String uploadFile = PROD_FILE+"ztkLogo"+ "/" + name+"."+fileType;
		if(fileType.toLowerCase().equals("jpg")){
			typeStatus = "0";
		}else if(fileType.toLowerCase().equals("png")){
			typeStatus = "0";
		}
		if (multipartFile.isEmpty()||multipartFile.getSize()<=0) {
			status = 1;
		} else {
			/**使用UUID生成文件名称**/
			File file = new File(PROD_FILE+"ztkLogo");
			if(!file.exists()){
				file.mkdir();
			}
			File restore = new File(uploadFile);
			try {
				multipartFile.transferTo(restore);
			} catch (Exception e) {
				status = -1;
			}
		}
		//判断logo有没有值，如果有值，说明是修改logo，删除原来的logo
		if(StringUtils.isNotBlank(logo)){
			File file = new File(WebAppUtils.getWebAppRoot()+logo);
			if(file.exists()){
				file.delete();
			}
		}
		logger.info("---------------------------------进入文件上传路径uploadFile---"+uploadFile+"--------------------------------------------");
		rtn.put("typeStatus", typeStatus);
		rtn.put("status", status);
		rtn.put("uploadFile", (WebAppUtils.getWebRootRelDir(ConstantsDef.prodFile)+"ztkLogo"+"/"+name+"."+fileType).replaceAll("\\\\", "\\/"));
		return rtn;
		
	}
}
