package com.brainsoon.system.action;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
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
import com.brainsoon.common.pagination.PageInfo;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.system.model.InDefinition;
import com.brainsoon.system.model.MetaDataGroup;
import com.brainsoon.system.service.IInDefinitionService;
import com.brainsoon.system.support.SysOperateLogUtils;

@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class InDefinitionAction extends BaseAction {
	/**默认命名空间**/
	private static final String baseUrl = "/system/inDefinition/";
	@Autowired
	private IInDefinitionService inDefinitionService;
	UserInfo userInfo =  LoginUserUtil.getLoginUser();
	
	/**
	 * 跳转到添加页面
	 * @param order
	 * @param model
	 * @throws Exception 
	 */
	@RequestMapping(baseUrl+"add") 
	@Token(save=true)
	public String add(@ModelAttribute("frmDefinition")InDefinition inDefinition ,ModelMap model) throws Exception{
		return baseUrl + "inDefinitionAdd";
	}
	
	/**
	 * 跳转到详细页面
	 * @param order
	 * @param model
	 * @throws Exception 
	 */
	@RequestMapping(baseUrl+"detail") 
	@Token(save=true)
	public String detail(@RequestParam("id") Long id,HttpServletResponse response,ModelMap model) throws Exception{
		logger.debug("进入查看页面");
		InDefinition inDefinition = (InDefinition)inDefinitionService.getByPk(InDefinition.class, id);
		model.addAttribute("frmDefinition", inDefinition);
		return baseUrl + "inDefinitionDetail";
	}
	
	/**
	 * 跳转到修改页面
	 * @param order
	 * @param model
	 * @throws Exception 
	 */
	@RequestMapping(baseUrl+"up") 
	@Token(save=true)
	public String upData(@RequestParam("id") Long id,HttpServletResponse response,ModelMap model) throws Exception{
		logger.debug("进入查看页面");
		InDefinition inDefinition = (InDefinition)inDefinitionService.getByPk(InDefinition.class, id);
		model.addAttribute("frmDefinition", inDefinition);
		return baseUrl + "inDefinitionEdit";
	}
	
	/**
	 * 跳转到列表页面
	 * @param order
	 * @param model
	 * @throws Exception 
	 */
	@RequestMapping(baseUrl+"query") 
	@Token(save=true)
	public @ResponseBody String list(HttpServletRequest request,HttpServletResponse response) throws Exception{
		List<InDefinition> list = inDefinitionService.doList();
		JSONArray array = new JSONArray();
		for(InDefinition inDefinition : list){
			JSONObject json = new JSONObject();
			json.put("name", inDefinition.getName());
			json.put("id", inDefinition.getId());
			array.add(json);
		}
		return array.toString();
	}
	
	/**
	 * 执行添加操作
	 * @param request
	 * @param response 
	 */
	@RequestMapping(baseUrl + "addAction")
	@Token(remove=true)
	public String addAction(HttpServletRequest request,HttpServletResponse response,@ModelAttribute("frmDefinition") InDefinition inDefinition,Model model){
		logger.debug("进入添加方法");
		try {
			inDefinition.setPlatformId(userInfo.getPlatformId());
			inDefinitionService.addInDefinition(inDefinition);
			SysOperateLogUtils.addLog("indefinition_add", inDefinition.getName(), userInfo);
		} catch (Exception e) {
			addActionError(e);
		}
		return baseUrl + "inDefinition";
	}
	
	/**
	 * 执行删除操作
	 * @param response
	 * @param ids
	 * @throws IOException
	 */
	@RequestMapping(baseUrl + "delAction")
	public String delAction(HttpServletResponse response,@RequestParam("id") String id) throws IOException{
		logger.debug("进入删除方法");
		inDefinitionService.deleteById(id);
		return baseUrl + "inDefinition";
	}
	
	/**
	 * 执行更新操作
	 * @param request
	 * @param response
	 * @param order
	 * @param model
	 */
	@RequestMapping(baseUrl + "updAction")
	@Token(remove=true)
	public String updAction(HttpServletRequest request,HttpServletResponse response,@ModelAttribute("frmDefinition") InDefinition inDefinition,ModelMap model){
		logger.debug("进入修改方法");
		try {
			inDefinition.setPlatformId(userInfo.getPlatformId());
			inDefinitionService.updInDefinition(inDefinition);
			SysOperateLogUtils.addLog("indefinition_up", inDefinition.getName(), userInfo);
		} catch (Exception e) {
			addActionError(e);
		}
		return baseUrl + "inDefinition";
	}
	
	/**
	 * 跳转到元数据页面
	 * @param order
	 * @param model
	 * @throws Exception 
	 */
	@RequestMapping(baseUrl+"metaDataList") 
	@Token(save=true)
	public String metaData(@RequestParam("pid") String pid,ModelMap model) throws Exception{
		Long pidd = Long.valueOf(pid);
		model.addAttribute("pid", pidd);
		return baseUrl + "metaDataList";
	}
	
	/**
	 * 获取元数据信息
	 * @param order
	 * @param model
	 * @throws Exception 
	 */
	@RequestMapping(baseUrl+"queryMetaData") 
	@Token(save=true)
	public @ResponseBody PageResult  queryMetaData() throws Exception{
		QueryConditionList conditionList = getQueryConditionList();
		return inDefinitionService.query4Page(MetaDataGroup.class, conditionList);
	}
	
	/**
	 * 跳转到元数据定制页面
	 * @param order
	 * @param model
	 * @throws Exception 
	 */
	@RequestMapping(baseUrl+"metaDataTerm") 
	@Token(save=true)
	public String metaDataTerm(@RequestParam("pid") String pid,@ModelAttribute("frmMetaDataTerm") MetaDataGroup metaDataGroup) throws Exception{
		InDefinition inDefinition = new InDefinition();
		Long id = Long.parseLong(pid); 
		inDefinition.setId(id);
		metaDataGroup.setDefinition(inDefinition);
		return baseUrl + "metaDataTerm";
	}
	
	/**
	 * 获取元数据项
	 * @param order
	 * @param model
	 * @throws Exception 
	 */
	@RequestMapping(baseUrl+"haveMetaData") 
	@Token(save=true)
	public @ResponseBody String haveMetaData(HttpServletRequest request,HttpServletResponse response) throws Exception{
		String json = inDefinitionService.HaveMetaData();
		return json;
	}
	
	/**
	 * 执行添加操作
	 * @param request
	 * @param response 
	 */
	@RequestMapping(baseUrl + "addMetaAction")
	@Token(save=true)
	public @ResponseBody String addMetaAction(HttpServletRequest request,HttpServletResponse response,@ModelAttribute("frmMetaDataTerm") MetaDataGroup metaDataGroup,Model model){
		logger.debug("进入添加方法");
		Long ppid = 0L;
		try {
			String nameMust = (String)request.getParameter("nameMust");
			String nameMay = (String)request.getParameter("nameMay");
			String nameMustCN = (String)request.getParameter("nameMustCN");
			String nameMayCN = (String)request.getParameter("nameMayCN");
			String pid = (String)request.getParameter("pid");
			ppid= Long.parseLong(pid); 
			metaDataGroup.setPlatformId(userInfo.getPlatformId());
			metaDataGroup.setNameMay(nameMay);
			metaDataGroup.setNameMust(nameMust);
			metaDataGroup.setNameMustCN(nameMustCN);
			metaDataGroup.setNameMayCN(nameMayCN);
			InDefinition inDefinition = new InDefinition();
			inDefinition.setId(ppid);
			metaDataGroup.setDefinition(inDefinition);
			inDefinitionService.addMetaDataTerm(metaDataGroup);
			SysOperateLogUtils.addLog("metaDataTerm_add", userInfo.getUsername(), userInfo);
		} catch (Exception e) {
			addActionError(e);
		}
		return "0";
	}
	
	/**
	 * 详细元数据项
	 * @param order
	 * @param model
	 * @throws Exception 
	 */
	@RequestMapping(baseUrl+"metaDataTermdetail") 
	@Token(save=true)
	public String metaDataTermdetail(@RequestParam("id") String id,HttpServletRequest request,HttpServletResponse response,Model model) throws Exception{
		Long id1 = Long.parseLong(id); 
		MetaDataGroup metaDataGroup = (MetaDataGroup)inDefinitionService.getByPk(MetaDataGroup.class, id1);
		model.addAttribute(metaDataGroup);
		return baseUrl + "metaDataTermDetail";
	}
	
	/**
	 * 执行删除操作
	 * @param response
	 * @param ids
	 * @throws IOException
	 */
	@RequestMapping(baseUrl + "delmetaDataTerm")
	public String delmetaDataTerm(HttpServletResponse response,@RequestParam("id") String id,@RequestParam("pid") String pid,ModelMap model) throws IOException{
		Long pidd = Long.valueOf(pid);
		model.addAttribute("pid", pidd);
		inDefinitionService.deleteMetaGroupById(id);
		return baseUrl + "metaDataList";
	}
	/**
	 * 跳转到修改页面
	 * @param order
	 * @param model
	 * @throws Exception 
	 */
	@RequestMapping(baseUrl+"upmetaDataTerm") 
	@Token(save=true)
	public String upmetaDataTerm(@RequestParam("id") String id,HttpServletRequest request,HttpServletResponse response,ModelMap model) throws Exception{
		Long id1 = Long.parseLong(id); 
		MetaDataGroup metaDataGroup = (MetaDataGroup)inDefinitionService.getByPk(MetaDataGroup.class, id1);
		model.addAttribute("frmMetaDataTerm", metaDataGroup);
		return baseUrl + "metaDataTermEdit";
	}
	
	/**
	 * 执行修改操作
	 * @param request
	 * @param response 
	 */
	@RequestMapping(baseUrl + "upMetaAction")
	@Token(save=true)
	public @ResponseBody String upMetaAction(HttpServletRequest request,HttpServletResponse response,@ModelAttribute("frmMetaDataTerm") MetaDataGroup metaDataGroup,Model model){
		logger.debug("进入添加方法");
		Long ppid = 0L;
		Long idd = 0L;
		try {
			String nameMust = (String)request.getParameter("nameMust");
			String nameMay = (String)request.getParameter("nameMay");
			String nameMustCN = (String)request.getParameter("nameMustCN");
			String nameMayCN = (String)request.getParameter("nameMayCN");
			String id = (String)request.getParameter("id");
			String pid = (String)request.getParameter("pid");
			ppid= Long.parseLong(pid); 
			idd = Long.parseLong(id); 
			metaDataGroup = (MetaDataGroup)inDefinitionService.getByPk(MetaDataGroup.class, idd);
			metaDataGroup.setNameMay(nameMay);
			metaDataGroup.setPlatformId(userInfo.getPlatformId());
			metaDataGroup.setNameMust(nameMust);
			metaDataGroup.setNameMustCN(nameMustCN);
			metaDataGroup.setNameMayCN(nameMayCN);
			/*InDefinition inDefinition = new InDefinition();
			inDefinition.setId(ppid);
			metaDataGroup.setDefinition(inDefinition);*/
			inDefinitionService.upMetaDataTerm(metaDataGroup);
			SysOperateLogUtils.addLog("metaDataTerm_up", userInfo.getUsername(), userInfo);
		} catch (Exception e) {
			addActionError(e);
		}
		return "0";
	}
}
