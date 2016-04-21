package com.brainsoon.statistics.action;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.query.Operator;
import com.brainsoon.appframe.query.QueryConditionItem;
import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.statistics.po.RespsOfResourceWord;
import com.brainsoon.statistics.po.RespsOfResourceWordFile;
import com.brainsoon.statistics.service.IResourceOfWordService;
import com.brainsoon.system.model.MetaDataModelGroup;
import com.brainsoon.taskprocess.model.TaskResRelation;

@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ResourceNumAction extends BaseAction{
	
	@Autowired
	private IResourceOfWordService resourceOfWord;
	
	/**
	 * 过滤敏感词
	 * @return
	 */
	@RequestMapping(value="/resource/queryResource")
	@ResponseBody
	public String queryResource(){
		String result = "ok";
		try{
			resourceOfWord.doResourceOfWord();
		}catch(Exception e){
			result = "error";
			e.printStackTrace();
		}
		return result;
	}
	
	
	/**
	 * 加载列表页面
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/resource/queryList")
	@ResponseBody
	public PageResult queryList(HttpServletRequest request){
		QueryConditionList conditionList = getQueryConditionList();
		String resType = request.getParameter("publishType");
		String word = request.getParameter("word_type");
		String wordName = request.getParameter("wordName");
		String resourceName = request.getParameter("resourceName");
		if(StringUtils.isNotBlank(resType)){
			conditionList.addCondition(new QueryConditionItem("resourceTypeId", Operator.EQUAL,resType));
		}
		if(StringUtils.isNotBlank(wordName)){
			try {
				wordName = URLDecoder.decode(wordName,"UTF-8");
				conditionList.addCondition(new QueryConditionItem("resourceWord", Operator.LIKE,"%"+wordName+"%"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		
		}
		if(StringUtils.isNotBlank(resourceName)){
			try {
				resourceName = URLDecoder.decode(resourceName,"UTF-8");
				conditionList.addCondition(new QueryConditionItem("resourceName", Operator.LIKE,"%"+resourceName+"%"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		if(StringUtils.isNotBlank(word)){
			try {
				word = URLDecoder.decode(word,"UTF-8");
				conditionList.addCondition(new QueryConditionItem("haveWord", Operator.EQUAL,word));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
		}
		PageResult pageResult = resourceOfWord.query4Page(RespsOfResourceWord.class, conditionList);
		return pageResult;
	}
	
	
	/**
	 * 详细页面跳转
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping(value ="/resource/view")
	public String view(@RequestParam String id,HttpServletRequest request){
		if(StringUtils.isNotBlank(id)){
			request.setAttribute("resourId", id);
		}
		return "statistics/wordNum/wordNumDetail";
	}
	
	
	/**
	 * 敏感词详细展示
	 * @param req
	 * @return
	 */
	@RequestMapping(value ="/resource/queryDetail")
	@ResponseBody
	public PageResult queryDetail(HttpServletRequest req){
		String resId = req.getParameter("resource");
		QueryConditionList conditionList = getQueryConditionList();
		if(StringUtils.isNotBlank(resId)){
			conditionList.addCondition(new QueryConditionItem("resourceId", Operator.EQUAL,resId));
		}
		PageResult pageResult = resourceOfWord.query4Page(RespsOfResourceWordFile.class, conditionList);
		return pageResult;
	}
}
