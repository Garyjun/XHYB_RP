package com.brainsoon.article.action;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.article.service.IArticleService;
import com.brainsoon.common.pagination.PageInfo;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.resource.service.IPublishResService;
import com.brainsoon.resource.support.WorkFlowUtils;
import com.brainsoon.semantic.ontology.model.AssetList;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.Sco;
import com.google.gson.Gson;

@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ArticleAction extends BaseAction{

	@Autowired
	private IArticleService articlrService;
	
	@Autowired
	private IPublishResService publishResService;
	/**
	 * 文章列表查询
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping("/article/queryList")
	@ResponseBody
	public PageResult queryList(HttpServletRequest req,@RequestParam(value="treeId",required=false) String preType,@RequestParam(required=false) String joulName) throws UnsupportedEncodingException{
		//String preType = req.getParameter("treeId");
		if(preType != null && preType != ""){
			preType=URLDecoder.decode(URLDecoder.decode(preType,"UTF-8"),"utf-8");
		}
		if(StringUtils.isNotBlank(joulName)){
			joulName = URLDecoder.decode(joulName,"UTF-8");
		}
		PageInfo pageInfo = getPageInfo();
		int page = pageInfo.getPage();	//当前页数
		int sum = pageInfo.getRows();	//每页显示的条数
		
		int start = -1;
		if(page == 1){
			start = 1;
		}else if(page > 1){
			start = (page - 1) * sum + 1; 
		}
		AssetList assetList = null;
		try {
			assetList = articlrService.queryList(preType,start,sum,joulName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		PageResult pageResult = new PageResult();
		pageResult.setTotal(assetList.getTotle());
		pageResult.setRows(assetList.getAssets());
		return pageResult;
	}
	
	
	/**
	 * 查询文章资源详细
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/article/Detail")
	public String toDetail(HttpServletRequest request,HttpServletResponse response, ModelMap model){
		String objectId = request.getParameter("objectId");
		HttpClientUtil http = new HttpClientUtil();
		String resourceDetail = http.executeGet(WebappConfigUtil.getParameter("Article_DETAIL_CONTENT") + "?id=" + objectId);
		Gson gson = new Gson();
		Sco scoEntity = gson.fromJson(resourceDetail, Sco.class);
		model.put("publishType","38");
		model.put("detailFlag", "detail");
		model.put("objectId", objectId);
		model.put("scoEntity", scoEntity);
		return "/article/articleDetail";
	}
	
	/**
	 * 文章修改
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/article/Edit")
	public String toEdit(HttpServletRequest request,HttpServletResponse response, ModelMap model){
		String objectId = request.getParameter("objectId");
		HttpClientUtil http = new HttpClientUtil();
		String resourceDetail = http.executeGet(WebappConfigUtil.getParameter("Article_DETAIL_CONTENT") + "?id=" + objectId);
		Gson gson = new Gson();
		Sco scoEntity = gson.fromJson(resourceDetail, Sco.class);
		model.put("publishType","38");
		model.put("objectId", objectId);
		model.put("scoEntity", scoEntity);
		return "/article/articleEdit";
	}
	
	
	/**
	 * 对文章信息进行修改
	 */
	@RequestMapping(value="/article/toUpdate")
	@ResponseBody
	public String toUpdate(HttpServletRequest request,Sco sco){
		String result = "success";
		String objectId = request.getParameter("objectId");
		if(StringUtils.isNotBlank(objectId)){
			sco.setObjectId(objectId);
		}
		try {
			String publishType = "38";
			String respurces = articlrService.updateSco(sco, publishType);
		} catch (Exception e) {
			result = "fail";
			e.printStackTrace();
			logger.error(e.getMessage());
		    addActionError(e);
		}
		return result;
	}
}
