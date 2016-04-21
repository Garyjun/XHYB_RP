package com.brainsoon.rp.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.common.po.tree.TreeNode;
import com.brainsoon.rp.model.annotation.Annotation;
import com.brainsoon.rp.service.IAnnotatorService;
import com.google.gson.Gson;

/**
 * <dl>
 * <dt>AnnotatorController.java</dt>
 * <dd>Function: 批注</dd>
 * <dd>Description:文本批注 统一API，包括 增删改查 操作</dd>
 * <dd>Copyright: Copyright (C) 2010</dd>
 * <dd>Company: 北京博云易迅技术有限公司</dd>
 * <dd>CreateUser: xujie</dd>
 * <dd>CreateDate: 2016-03-04</dd>
 * </dl>
 */
@Controller
@RequestMapping(value = "/api/annotations/")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class AnnotatorController extends BaseAction {

	@Autowired
	private IAnnotatorService annotatorService;

	@RequestMapping(value = "create", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8")
	@ResponseBody
	public String create(@RequestBody Annotation annotation) {
		annotatorService.create(annotation);
		return new Gson().toJson(annotation);
	}

	@RequestMapping(value = "delete")
	@ResponseBody
	public String delete(Annotation annotation) {
		System.err.println(annotation);
		return "{}";
	}

	@RequestMapping(value = "update")
	@ResponseBody
	public String update(@RequestBody Annotation annotation) {
		System.err.println(annotation);
		return null;
	}

	/**
	 * 网页中批注查询
	 * @param limit  加载的批注数
	 * @param uri    网页URL
	 * @return
	 */
	@RequestMapping(value = "search")
	@ResponseBody
	public String search(@RequestParam int limit,@RequestParam String uri) {
		JSONObject result = new JSONObject();
		List<Annotation> list = annotatorService.findAnnotations(limit,uri);
		result.put("total", list.size());
		result.put("rows", list);
		return result.toString();
	}
}
