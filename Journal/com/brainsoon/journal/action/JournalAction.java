package com.brainsoon.journal.action;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CalcChainDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.query.QueryConditionItem;
import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.support.GlobalDataCacheMap;
import com.brainsoon.common.support.OperDbUtils;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.journal.service.IJournalservice;
import com.brainsoon.resource.service.IPublishResService;
import com.brainsoon.resource.support.WorkFlowUtils;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.CaList;
import com.brainsoon.semantic.ontology.model.SearchResultCa;
import com.brainsoon.semantic.schema.ontology.MetadataDefinition;
import com.brainsoon.system.service.IFLTXService;
import com.brainsoon.system.service.IZTFLService;
import com.brainsoon.system.util.MetadataSupport;
import com.google.gson.Gson;
@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class JournalAction extends BaseAction {
	/** 默认命名空间 **/
	private final String baseUrl = "/journal/";
	
	@Autowired
	private IJournalservice journalservice;
	@Autowired
	private IPublishResService publishResService;
	@Autowired
	private IZTFLService zTFLService;
	@Autowired
	private IFLTXService fltxService;
	/**
	 * 
	* @Title: getTimeTree
	* @Description: 期刊管理左侧页面时间树
	* @param request
	* @param response
	* @return    参数
	* @return String    返回类型
	* @throws
	 */
	@RequestMapping(value=baseUrl+"getTimeTree")
	public @ResponseBody String getTimeTree(HttpServletRequest request,HttpServletResponse response){
		String type = request.getParameter("type");
		String nodeId = request.getParameter("nodeId");
		String level = request.getParameter("level");
		String name = request.getParameter("name");
		String xcode = request.getParameter("xcode");
		try {
			if (StringUtils.isNotBlank(name)) {
				name = URLDecoder.decode(name,"UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return journalservice.getTimeTree(Long.parseLong(type), nodeId, level, name, xcode);
	}
	
	/**
	 * 
	 * @Title: tolist
	 * @Description: 期刊列表页面
	 * @param request
	 * @param response
	 * @return    参数
	 * @return String    返回类型
	 * @throws
	 */
	@RequestMapping(value=baseUrl+"journalList")
	public @ResponseBody PageResult JournalList(HttpServletRequest request,HttpServletResponse response){
		String level = request.getParameter("level");
		String xcode = request.getParameter("xcode");
		String magazineNum = request.getParameter("magazineNum");
		String numOfYear = request.getParameter("numOfYear");
		String publishType = request.getParameter("publishType");
		
		 String result= journalservice.queryJournalList(request, WebappConfigUtil.getParameter("JOURNAL_LIST_URL"));
		 logger.info("【JournalAction】JournalList--->>>期刊列表--->>>返回result:" + result);
		 Gson gson=new Gson();
		 CaList caList = gson.fromJson(result, CaList.class);
		 if(caList!=null){
			 List<Ca> cas = caList.getCas();
			 if(cas!=null && cas.size()>0){
				 for(int i=0;i<cas.size();i++){
					 Ca ca = cas.get(i);
					 if(StringUtils.isNotBlank(ca.getCreator())){
						ca.setCreateName(OperDbUtils.getUserNameByloginName(ca.getCreator()));
					 }
					 if(StringUtils.isNotBlank(ca.getCreateTime())){
						 SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				    		String date = format.format(Long.parseLong(ca.getCreateTime()));
				    		ca.setCreateTime(date);
					 }
				 }
			 }
		 }
		 result = gson.toJson(caList);
		PageResult pageResult = new PageResult();
		pageResult.setTotal(caList.getTotle());
		pageResult.setRows(caList.getCas());
		return pageResult;
	}
	
	/**
	 * 
	* @Title: tolist
	* @Description: 期刊列表页面
	* @param request
	* @param response
	* @return    参数
	* @return String    返回类型
	* @throws
	 */
	@RequestMapping(value=baseUrl+"toDetail")
	public String toDetail(HttpServletRequest request,HttpServletResponse response, ModelMap model){
		String objectId = request.getParameter("objectId");
		//String searchFlag = request.getParameter("searchFlag");
		String publishType = request.getParameter("publishType");
		//String flagSta = request.getParameter("flagSta");
		String status = request.getParameter("status");
		//String targetField = request.getParameter("targetField");
		//String targetNames = request.getParameter("targetNames");
		//String cbclassFieldValue = request.getParameter("cbclassFieldValue");
		//String cbclassField = request.getParameter("cbclassField");
		String copyrightWaring = request.getParameter("copyrightWaring");
		HttpClientUtil http = new HttpClientUtil();
		String resourceDetail = http.executeGet(WebappConfigUtil.getParameter("JOURNAL_DETAIL_URL") + "?id=" + objectId);
		Gson gson = new Gson();
		Ca bookCa = gson.fromJson(resourceDetail, Ca.class);
		
		if(bookCa!=null){
			model = publishResService.jsonArray(bookCa, objectId, model);
			model.put("publishType", bookCa.getPublishType());
		}
		model.put("detailFlag", "detail");
		String execuId = WorkFlowUtils.getExecuId(objectId, "pubresCheck");
		model.put("execuId", execuId);
		model.put("objectId", objectId);
		model.put("bookCa", bookCa);
		model.put("flagSta", null);
		//model.put("searchFlag", searchFlag);
		model.put("status", "0");
		model.put("publishType", publishType);
//		model.put("status", status);
		//model.put("targetField", targetField);
		//model.put("targetNames", targetNames);
		//model.put("cbclassFieldValue", cbclassFieldValue);
		//model.put("cbclassField", cbclassField);
		if(StringUtils.isNotBlank(copyrightWaring)){
			model.put("copyrightWaring", copyrightWaring);
		}else{
			model.put("copyrightWaring", "");
		}
		//logger.debug(resourceDetail);
		return baseUrl + "detail";
		//return journalservice.getJournalDetail(level,code);
	}
	
	/**
	 * 
	 * @Title: tolist
	 * @Description: 期刊-文章列表页面
	 * @param request
	 * @param response
	 * @return    参数
	 * @return String    返回类型
	 * @throws
	 */
	@RequestMapping(value=baseUrl+"articleList")
	public @ResponseBody String articleList(HttpServletRequest request,HttpServletResponse response){
		String keyWord = request.getParameter("keyWord");
		String articleTitle = request.getParameter("articleTitle");
		String magazineNum = request.getParameter("magazineNum");
		return journalservice.getArticleList(keyWord, articleTitle, magazineNum);
	}
}
