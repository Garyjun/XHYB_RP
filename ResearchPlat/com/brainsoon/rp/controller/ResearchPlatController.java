package com.brainsoon.rp.controller;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.rp.service.IResearchPlatService;
import com.brainsoon.rp.support.DataTableResult;
import com.brainsoon.rp.support.SearchParam;
import com.brainsoon.rp.support.TreeNode;

/**
 * <dl>
 * <dt>ResearchPlatController</dt>
 * <dd>Function:</dd>
 * <dd>Description:</dd>
 * <dd>Copyright: Copyright (C) 2010</dd>
 * <dd>Company: 北京博云易迅技术有限公司</dd>
 * <dd>CreateUser: xujie</dd>
 * <dd>CreateDate: 2016-03-04</dd>
 * </dl>
 */
@Controller
@RequestMapping(value = "/index/")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ResearchPlatController extends BaseAction {

	@Autowired
	private IResearchPlatService researchPlatService;

	/**
	 * 生成导航树
	 * 
	 * 根据传递的Id构建树，id=-1 获取所有顶级结点
	 * 
	 * @param id
	 *            导航节点
	 * @return
	 */
	@RequestMapping(value = "generateNavTree")
	@ResponseBody
	// todo : json 数据请求
	public String generateNavTree(@RequestParam(required = false) String id, @RequestParam(required = false) String value,@RequestParam(required = false) String pValue) {
		JSONArray array = new JSONArray();
		if (StringUtils.isBlank(id)) {
			id = "-1";
		}
		List<TreeNode> list = researchPlatService.generateTreeNodesFromXML(id,value,pValue);
		if (list != null) {
			for (TreeNode node : list) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("id", node.getId());
				jsonObj.put("pId", node.getPid());
				jsonObj.put("name", node.getLabel());
				jsonObj.put("pValue", node.getpValue());
				jsonObj.put("value", node.getValue());
				jsonObj.put("type", node.getType());
				jsonObj.put("isParent", node.isParent());
				array.add(jsonObj);
			}
		}
		return array.toString();
	}
	
	/**
	 * 
	* @Title: queryResList
	* @Description: 资源列表查询
	* @param request
	* @param response
	* @return    参数
	* @return String    返回类型
	* @throws
	 */
	@RequestMapping(value = "queryResList", method = {RequestMethod.POST })
	@ResponseBody
	public DataTableResult queryResList(HttpServletRequest request, HttpServletResponse response){
		SearchParam searchParam = getDataTableResult();
		
		HashMap<String, String> params = new HashMap<String, String>();
		DataTableResult data = researchPlatService.queryResList(searchParam);
		return data;
	}
}
