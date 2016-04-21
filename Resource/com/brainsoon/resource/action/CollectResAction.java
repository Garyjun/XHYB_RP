package com.brainsoon.resource.action;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.support.Constants;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.resource.service.IAreaService;
import com.brainsoon.resource.service.IBaseSemanticSerivce;
import com.brainsoon.resource.service.ICollectResService;
import com.brainsoon.resrelease.po.ProdParamsTemplate;
import com.brainsoon.resrelease.service.IResOrderService;
import com.brainsoon.semantic.ontology.model.Asset;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.Organization;
import com.brainsoon.semantic.ontology.model.OrganizationItem;
import com.brainsoon.system.model.ResTarget;
import com.brainsoon.system.model.SysParameter;
import com.brainsoon.system.service.IBookService;
import com.brainsoon.system.service.IResTargetService;
import com.brainsoon.system.service.ISysParameterService;
import com.brainsoon.system.support.SysOperateLogUtils;
import com.brainsoon.system.support.SystemConstants.EducationPeriod;
import com.brainsoon.system.support.SystemConstants.NodeType;
import com.brainsoon.system.support.SystemConstants.ResourceType;
import com.google.gson.Gson;
@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)  
public class CollectResAction extends BaseAction {
	 /** 默认命名空间 **/
	 private final String baseUrl = "/collectRes/";
	 private static final String CA_CREATE="ca_save";
	 private static final String CA_UPDATE="ca_update";
	 private static final String CA_DELETE="ca_delete";
	 @Autowired
	 private IAreaService areaService;
	 @Autowired
	 private ICollectResService collectResService;
	 @Autowired
	 private IBookService bookService;
	 @Autowired
	 private IBaseSemanticSerivce baseSemanticSerivce;
	 @Autowired
	 private IResOrderService resOrderService;
	 @Autowired
	 private IResTargetService resTargetService;
	 @Autowired
	 private ISysParameterService sysParameterService;	
	 private List<ResTarget> useResTargets = new LinkedList<ResTarget>();
	 @RequestMapping(value=baseUrl+"list")
 	 public String list(Model model,Asset asset ) {
 	    	 logger.debug("to list ");
 	    	 String authCodes=LoginUserUtil.getAuthResCodes();
 			 logger.debug("authCodes****** "+authCodes);
 			 String authTypes=LoginUserUtil.getAuthResTypes();
 			 logger.debug("authTypes****** "+authTypes);
 	         model.addAttribute(Constants.COMMAND, asset);
 	  	     return baseUrl+"collectResMain";
 	 }
	 /**
	  * 列表查询
	  * @param request
	  * @param response
	  * @return
	  */
	 @RequestMapping(value=baseUrl+"query")
	 @ResponseBody
	 public String  query(HttpServletRequest request, HttpServletResponse response) {
		 QueryConditionList conditionList = getQueryConditionList();
		 String result= collectResService.queryResource4Page(request, conditionList,WebappConfigUtil.getParameter("CA_QUERY_URL"));
		 return result;
	}
	 /**
	  * 列表查询
	  * @param request
	  * @param response
	  * @return
	  */
	 @RequestMapping(value=baseUrl+"collectResList")
	 public String  collectResList(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		 HttpSession session = getSession();
		 String module = request.getParameter("module");
		 model.addAttribute("module", module);
			List<SysParameter> enforceDelete = sysParameterService.findParaValue("enforceDelete");
			if(enforceDelete!=null && enforceDelete.size()>0&&enforceDelete.get(0).getParaStatus().toString().equals("1")){
				session.setAttribute("enforceDelete", enforceDelete.get(0).getParaValue());
			}else{
				session.removeAttribute("enforceDelete");
			}
		 return baseUrl+"collectResList";
	}
		/**
		 * 转换
		 * @param response
		 * @param objectId
		 */
		@RequestMapping(baseUrl+"toChange")
		public void toChange(HttpServletRequest request,HttpServletResponse response,@RequestParam("objectId") String objectId){
			
			
		}
		/**
		 * 模板选择
		 * @return
		 */
		@RequestMapping(baseUrl+"paperModelSelect")
		public String paperModelSelect(HttpServletRequest request,HttpServletResponse response){
			return "redirect:/system/testTemplate/testTempSelected.jsp";
		}
		/**
		 * 模板选择
		 * @param request
		 * @param response
		 * @return
		 */
		@RequestMapping(baseUrl+"orderTemplateList")
		public @ResponseBody PageResult orderPublishTemplateList(HttpServletRequest request,HttpServletResponse response){
			logger.info("查询模板列表");
			QueryConditionList conditionList = getQueryConditionList();
			QueryConditionItem item = new QueryConditionItem("status", Operator.EQUAL, "1");
			conditionList.addCondition(item);
			return bookService.query4Page(ProdParamsTemplate.class, conditionList);
		}
	 /**
		 * 跳转到编辑页面
		 * @param asset
		 * @param model
		 * @throws Exception 
		 */
	@RequestMapping(baseUrl+"edit") 
	public String edit(HttpServletRequest request,@ModelAttribute("frmAsset") Ca ca,@RequestParam("objectId") String objectId,@RequestParam("module") String module,@RequestParam("type") String type,ModelMap model) throws Exception{
			//查询省
			String targetCol = request.getParameter("targetCol");
			String goBackTask = request.getParameter("goBackTask");
			model.addAttribute("goBackTask", goBackTask);
			model.addAttribute("targetCol", targetCol);
			model.addAttribute("provinces", areaService.getProvince());
			model.addAttribute("module", module);
			model.addAttribute("type", type);
			if(!objectId.equals("-1")){
				HttpClientUtil http = new HttpClientUtil();
				String resourceDetail= http.executeGet(WebappConfigUtil.getParameter("CA_DETAIL_URL")+"?id="+objectId);
				Gson gson=new Gson();
				Ca caTemp=gson.fromJson(resourceDetail, Ca.class);
				if(caTemp!=null && caTemp.getCommonMetaData()!=null){
					model.addAttribute("resourceDetail", resourceDetail);
//					List<Organization> organizations = caTemp.getOrganizations();
//					if(organizations!=null && organizations.size()>0){
//						Organization organization = organizations.get(0);
//						if(organization!=null){
//							List<OrganizationItem> organizationItems = organization.getOrganizationItems();
//							String filterIds = "";
//							if(organizationItems!=null && organizationItems.size()>0){
//								for(OrganizationItem organizationItem:organizationItems){
//									List<String> assetObjects = organizationItem.getAssets();
//									if(assetObjects!=null && assetObjects.size()>0){
//										for(String assetObjectId:assetObjects){
//											filterIds += assetObjectId+",";
//										}
//									}
//								}
//								if(!"".equals(filterIds)){
//									filterIds = filterIds.substring(0,filterIds.length()-1);
//									model.addAttribute("filterIds", filterIds);
//								}
//							}
//						}
//					}
				}else{
					return "/error/errorUnRes";
				}
			}
			model.addAttribute("objectId", objectId);
			if(module.equals("SJ")){
				return baseUrl + "paperResEdit";
			}else{
				return baseUrl + "collectResEdit";
			}
	}
	
	 /**
		 * 跳转到阅读页面
		 * @param asset
		 * @param model
		 * @throws Exception 
		 */
	@RequestMapping(baseUrl+"readAllPaper") 
	public String readAllPaper(HttpServletRequest request,@ModelAttribute("frmAsset") Ca ca,@RequestParam("objectId") String objectId,@RequestParam("module") String module,@RequestParam("type") String type,ModelMap model) throws Exception{
			//model.addAttribute("module", module);
			//model.addAttribute("type", type);
			String content_json = "";
			JSONArray jsonArray1 = new JSONArray();
			if(!objectId.equals("-1")){
				HttpClientUtil http = new HttpClientUtil();
				String resourceDetail= http.executeGet(WebappConfigUtil.getParameter("CA_DETAIL_URL")+"?id="+objectId);
				Gson gson=new Gson();
				Ca caTemp=gson.fromJson(resourceDetail, Ca.class);
				if(caTemp!=null && caTemp.getCommonMetaData()!=null){
					model.addAttribute("resourceDetail", resourceDetail);
//					List<Organization> organizations = caTemp.getOrganizations();
//					if(organizations!=null && organizations.size()>0){
//						Organization organization = organizations.get(0);
//						if(organization!=null){
//							List<OrganizationItem> organizationItems = organization.getOrganizationItems();
//							List<OrganizationItem> tempOrganizationItems =organizationItems;
//							if(organizationItems!=null && organizationItems.size()>0){
//								for(int i = 1;i< organizationItems.size();i++){
//									OrganizationItem organizationItem = organizationItems.get(i);
//									if(organizationItem.getAssets() ==null){
//										String questionName = organizationItem.getName();
//										String score = organizationItem.getScore();
//										JSONObject jsonObj = new JSONObject();
//										jsonObj.put("name", questionName);
//										jsonObj.put("score", score);
//										JSONArray jsonArray = new JSONArray();
//										for(OrganizationItem tempOrganizationItem:tempOrganizationItems){
//											if(organizationItem.getNodeId().equals(tempOrganizationItem.getPid()) && tempOrganizationItem.getAssets()!=null){
//												String id = tempOrganizationItem.getAssets().get(0);
//												String resourceJson = baseSemanticSerivce.getResourceDetailById(id);
//												if(!resourceJson.equals("")||resourceJson!=null){
//													Asset asset = gson.fromJson(resourceJson, Asset.class);
//													content_json =asset.getExtendMetaData().getExtendMetaDatas().get("content_json"); 
//													jsonArray.add(content_json);
//													jsonObj.put("data", jsonArray);
//												}
//											}
//										}
//										jsonArray1.add(jsonObj);
//									}
//								}
//							}
//						}
//					}
				}else{
					return "/error/errorUnRes";
				}
			}
			String returnJson = jsonArray1.toString();
			model.addAttribute("returnJson", returnJson);
			return "bres/papers";
	}
	@RequestMapping(baseUrl + "saveRes")
	public void saveRes(HttpServletRequest request, HttpServletResponse response,ModelMap model,@ModelAttribute("frmAsset") Ca ca,@RequestParam("jsonTree") String jsonTree,@RequestParam("nodeAsset") String nodeAsset,@RequestParam("ogId") String ogId) {
		logger.debug("******run at saveRes***********");
		try {
			String repeatType = request.getParameter("repeatType");
			collectResService.saveCollectRes(ca, jsonTree, nodeAsset, ogId,request.getParameter("thumbFile"),repeatType);
			UserInfo user = LoginUserUtil.getLoginUser();
			SysOperateLogUtils.addLog(CA_CREATE, ca.getCommonMetaData().getTitle(), user);
		} catch (Exception e) {
			logger.error(e.getMessage());
		    addActionError(e);
		}
	}
	/**
	 * 检查重复
	 * @param request
	 * @param response
	 * @return Map<String,Object>
	 */
	@RequestMapping(baseUrl + "checkRepeat")
	public @ResponseBody Map<String,Object> checkRepeat(HttpServletRequest request, HttpServletResponse response){
		String source = request.getParameter("source");
		String type = request.getParameter("type");
		String title = request.getParameter("title");
		String creator = request.getParameter("creator");
		String module = request.getParameter("module");
		List<Ca> rs = baseSemanticSerivce.getCaResourceByResVersion(source, type, title, creator,"","",module);
		Map<String,Object> rtn = new HashMap<String, Object>();
		int status = 0;//不重复
		if(rs != null && rs.size() > 0){
			status = 1;
			rtn.put("res", rs);
		}
		rtn.put("status", status);
		return rtn;
	}
	
	@RequestMapping(baseUrl + "updateRes")
	public void updateRes(HttpServletRequest request, HttpServletResponse response,ModelMap model,@ModelAttribute("frmAsset") Ca ca) {
		logger.debug("******run at updateRes***********");
		
		try {
			collectResService.updateCollectRes(ca,request.getParameter("thumbFile"));
			UserInfo user = LoginUserUtil.getLoginUser();
			SysOperateLogUtils.addLog(CA_UPDATE, ca.getCommonMetaData().getTitle(), user);
		} catch (Exception e) {
			logger.error(e.getMessage());
		    addActionError(e);
		}
	}
	
	 @RequestMapping(value=baseUrl+"updateNode")
	 @ResponseBody
	 public String updateNode(@RequestParam("nodeAsset") String nodeAsset,@RequestParam("nodeJson") String nodeJson,@RequestParam("title") String title) {
			 logger.debug("******run at saveRes***********");
			 String objectId="-1";
			 try {
				 objectId=collectResService.updateNode(nodeJson, nodeAsset,"");
				 UserInfo user = LoginUserUtil.getLoginUser();
				 SysOperateLogUtils.addLog(CA_UPDATE, title, user);
				} catch (Exception e) {
					logger.error(e.getMessage());
					objectId="-1";
			    }
			 return objectId;
	 }
	 
	@RequestMapping(value = baseUrl + "deleteNode")
	@ResponseBody
	public String deleteNode(@RequestParam("caId") String caId,
			@RequestParam("nodeId") String nodeId) {
		logger.debug("******run at saveRes***********");
		String succ = "0";
		try {
			collectResService.deleteNode(caId, nodeId,"");
			UserInfo user = LoginUserUtil.getLoginUser();
			SysOperateLogUtils.addLog(CA_UPDATE, "删除节点", user);
		} catch (Exception e) {
			logger.error(e.getMessage());
			succ = "-1";
		}
		return succ;
	}
	 /**
		 * 标签查询
		 * 
		 * @param request
		 * @param response
		 * @param params
		 * 
		 */
		@RequestMapping(value = baseUrl + "collTarget")
		public String collTarget(HttpServletRequest request, HttpServletResponse response,@RequestParam("pMenuId") String pMenuId,Model model) {
			logger.info("进入查询方法");
			UserInfo userInfo = LoginUserUtil.getLoginUser();	
			String hql = "from ResTarget resTarget where resTarget.userid.id="+Long.parseLong(userInfo.getUserId()+"")+" and resTarget.type="+Long.parseLong(pMenuId)+" or resTarget.type=0 and status=1";	
			useResTargets = resTargetService.query(hql);
			model.addAttribute("resTarget",useResTargets);
			
			return baseUrl+"collectResMain";
		}
		/**
		 * 标签信息查询
		 * @param request
		 * @param response
		 * @return
		 */
		 @RequestMapping(value="/collectRes/queryTargetRes")
		 @ResponseBody
		 public String  queryTargetRes(HttpServletRequest request, HttpServletResponse response) {
			 QueryConditionList conditionList = getQueryConditionList();
			 String resTargetId = request.getParameter("resTargetId");
			 String libType = request.getParameter("libType");
			 String type = request.getParameter("type");
			 String module = request.getParameter("module");
			 String result = resTargetService.bresQuery(resTargetId,libType,request,conditionList,module,type);
			 return result;
		} 
	
	 @RequestMapping(value=baseUrl+"toSelRes")
	 public String toSelRes(@RequestParam("nodeId") String nodeId,@RequestParam("objectId") String objectId,ModelMap model) {
	    	 logger.debug("to getKnowledge ");
	    	 model.addAttribute("nodeId", nodeId);
	    	 if(!objectId.equals("-1")){
					HttpClientUtil http = new HttpClientUtil();
					String resourceDetail= http.executeGet(WebappConfigUtil.getParameter("CA_DETAIL_URL")+"?id="+objectId);
					Gson gson=new Gson();
					Ca caTemp=gson.fromJson(resourceDetail, Ca.class);
					if(caTemp!=null && caTemp.getCommonMetaData()!=null){
//						List<Organization> organizations = caTemp.getOrganizations();
//						if(organizations!=null && organizations.size()>0){
//							Organization organization = organizations.get(0);
//							if(organization!=null){
//								List<OrganizationItem> organizationItems = organization.getOrganizationItems();
//								String filterIds = "";
//								if(organizationItems!=null && organizationItems.size()>0){
//									for(OrganizationItem organizationItem:organizationItems){
//										List<String> assetObjects = organizationItem.getAssets();
//										if(assetObjects!=null && assetObjects.size()>0){
//											for(String assetObjectId:assetObjects){
//												filterIds += assetObjectId+",";
//											}
//										}
//									}
//									if(!"".equals(filterIds)){
//										filterIds = filterIds.substring(0,filterIds.length()-1);
//										model.addAttribute("filterIds", filterIds);
//									}
//								}
//							}
//						}
					}else{
						return "/error/errorUnRes";
					}
				}
	  	     return baseUrl+"selectRes";
	 }
	 @RequestMapping(value=baseUrl+"getKnowledge")
	 @ResponseBody
	 public String getKnowledge(@RequestParam("educational_phase") String educational_phase,@RequestParam("subject") String subject) {
	    	 logger.debug("to getKnowledge ");
	    	 if(educational_phase.equals("")&&!subject.equals("")){
	    		 return "";
	    	 }
	  	     return bookService.getKnowledgeByParam(educational_phase+","+subject);
	 }
	 
	/**
	 * 跳转到信息页面
	 * @param ca
	 * @param objectId
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(baseUrl+"detail") 
	public String detail(@RequestParam("objectId") String objectId,ModelMap model) throws Exception{
			//查询省
			HttpClientUtil http = new HttpClientUtil();
			String resourceDetail= http.executeGet(WebappConfigUtil.getParameter("CA_DETAIL_URL")+"?id="+objectId);
			Gson gson=new Gson();
			Ca ca=gson.fromJson(resourceDetail, Ca.class);
			if(ca!=null && ca.getCommonMetaData()!=null){
				model.addAttribute("ca", ca);
				model.addAttribute("module", ca.getCommonMetaData().getModule());
				model.addAttribute("type", ca.getCommonMetaData().getType());
	            model.addAttribute("resourceDetail", resourceDetail);
				model.addAttribute("objectId", objectId);
				String region=ca.getCommonMetaData().getCommonMetaDatas().get("region");
				if(!region.equals("")){
					String regionInfo=areaService.getRegionInfo(region);
					model.addAttribute("regionInfo", regionInfo);
				}
			}else{
				return "/error/errorUnRes";
			}
			return baseUrl + "collectResDetail";
	}
//	/**
//	 * 执行删除操作
//	 * @param response
//	 * @param ids
//	 * @throws IOException
//	 */
//	@RequestMapping(baseUrl + "delRes")
//	public void delRes(HttpServletResponse response,@RequestParam("ids") String ids) throws IOException{
//		collectResService.deleteByIds(ids);
//		outputResult("删除成功");
//	}
	/**
	 * 执行强制删除操作
	 * @param response
	 * @param ids
	 * @throws IOException
	 */
	@RequestMapping(baseUrl + "enforceDelete")
	public @ResponseBody Map<String,Object> enforceDelete(HttpServletResponse response,@RequestParam("ids") String ids) throws IOException{
		Map<String,Object> rtn = new HashMap<String, Object>();
		String orderId = resOrderService.canDelByResId(ids);
		String status = "1";
		if(orderId.equals("")){
			collectResService.deleteByIds(ids);
		}else{
			status = "0";
			rtn.put("status", status);
		}
			rtn.put("status", status);
			return rtn;
	}
	public List<ResTarget> getUseResTargets() {
		return useResTargets;
	}
	public void setUseResTargets(List<ResTarget> useResTargets) {
		this.useResTargets = useResTargets;
	}
}
