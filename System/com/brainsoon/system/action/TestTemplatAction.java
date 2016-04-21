package com.brainsoon.system.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.brainsoon.system.model.DictName;
import com.brainsoon.system.model.TestTemplate;
import com.brainsoon.system.model.TestTemplateItem;
import com.brainsoon.system.service.ITestTemplatService;
import com.brainsoon.system.support.SysOperateLogUtils;

@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class TestTemplatAction  extends BaseAction {
	/**默认命名空间**/
	private static final String baseUrl = "/system/testTemplate/";
	@Autowired
	private ITestTemplatService testTemplatServiceService;
	UserInfo userInfo =  LoginUserUtil.getLoginUser();
	@RequestMapping(baseUrl + "list")
	public @ResponseBody PageResult list(HttpServletRequest request,HttpServletResponse response){
		logger.info("进入查询方法");
		QueryConditionList conditionList = getQueryConditionList();
		return testTemplatServiceService.query4Page(TestTemplate.class, conditionList);
	}
	
	@RequestMapping(baseUrl + "testTemplateItemList")
	public @ResponseBody PageResult testTemplateItemList(HttpServletRequest request,HttpServletResponse response){
		logger.info("进入查询方法");
		QueryConditionList conditionList = getQueryConditionList();
		String pidStr = request.getParameter("pid");
		int pid = Integer.parseInt(pidStr);
		conditionList.addCondition(new QueryConditionItem("pid", Operator.EQUAL, pid));
		return testTemplatServiceService.query4Page(TestTemplateItem.class, conditionList);
	}
	
	/**
	 * 跳转到添加页面
	 * @param order
	 * @param model
	 * @throws Exception 
	 */
	@RequestMapping(baseUrl+"addtestTemplateItem") 
	@Token(save=true)
	public String add(@RequestParam("pid") int pid,HttpServletResponse response,ModelMap model) throws Exception{
		TestTemplateItem testTemplateItem = new TestTemplateItem();
		TestTemplate testTemplate = new TestTemplate();
		testTemplate.setId(pid);
		testTemplateItem.setTestTemplate(testTemplate);
		model.addAttribute("frmTestTemplateItem", testTemplateItem);
		return baseUrl + "testTemplate";
	}
	
	/**
	 * 执行添加操作
	 * @param request
	 * @param response 
	 */
	@RequestMapping(baseUrl + "addAction")
	@Token(save=true)
	public @ResponseBody String addAction(HttpServletRequest request,HttpServletResponse response,Model model){
		logger.debug("进入添加方法");
		try {
			String testTypeKeyArray = request.getParameter("testTypeKey");
			String testTypeArray = request.getParameter("testType");
			String pidStr = request.getParameter("pid");
			int pid = Integer.parseInt(pidStr);
			String testTypeKey[] = testTypeKeyArray.split(",");
			String testType[] = testTypeArray.split(",");
			int count = testTemplatServiceService.doMaxCount(pid);
			for(int i=0;i<testType.length;i++) {
				TestTemplateItem testTemplateIt = new TestTemplateItem();
				testTemplateIt.setPlatformId(userInfo.getPlatformId());
				testTemplateIt.setTestType(testType[i]);
				testTemplateIt.setTestTypeKey(testTypeKey[i]);
				if(count==0) {
					testTemplateIt.setCount(i+1);
				} else{
					testTemplateIt.setCount(i+1+count);
				}
				TestTemplate testTemplate = new TestTemplate();
				testTemplate.setId(pid);
				testTemplateIt.setTestTemplate(testTemplate);
			    testTemplatServiceService.addTestTemplatItem(testTemplateIt);
			}
		} catch (Exception e) {
			addActionError(e);
		}
		return "0";
	}
	
	/**
	 * 跳转到添加页面
	 * @param order
	 * @param model
	 * @throws Exception 
	 */
	@RequestMapping(baseUrl+"add") 
	@Token(save=true)
	public String add(@ModelAttribute("frmtestTemplate") TestTemplate testTemplate,ModelMap model) throws Exception{
		return baseUrl + "testTemplateAdd";
	}

	/**
	 * 执行添加操作
	 * @param request
	 * @param response
	 */
	@RequestMapping(baseUrl + "addTemplateAction")
	@Token(save=true)
	public @ResponseBody int addTemplateAction(HttpServletRequest request,HttpServletResponse response,@ModelAttribute("frmtestTemplate") TestTemplate testTemplate,Model model){
		logger.info("进入添加方法");
		try {
			int platformId = userInfo.getPlatformId();
			testTemplate.setPlatformId(userInfo.getPlatformId());
			testTemplatServiceService.addTemplate(testTemplate);
			if(platformId==1) {
				SysOperateLogUtils.addLog("testTemplate_add", testTemplate.getName(), userInfo);
			}else{
				SysOperateLogUtils.addLog("pud_testTemplate_add", testTemplate.getName(), userInfo);
			}
			
		} catch (Exception e) {
			addActionError(e);
		}
		int id = testTemplate.getId();
		return id;
	}
	
	/**
	 * 跳转到更新页面
	 * @param id
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(baseUrl + "upd")
	@Token(save=true)
	public String upd(@RequestParam("id") int id,HttpServletResponse response,ModelMap model){
		logger.info("进入修改页面");
		TestTemplate testTemplate = (TestTemplate)testTemplatServiceService.getByPk(TestTemplate.class, id);
		model.addAttribute("frmtestTemplate", testTemplate);
		return baseUrl + "testTemplateEdit";
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
	public String updAction(HttpServletRequest request,HttpServletResponse response,@ModelAttribute("frmtestTemplate")TestTemplate testTemplate,ModelMap model){
		logger.info("进入修改方法");
		try {
			int platformId = userInfo.getPlatformId();
			testTemplate.setPlatformId(userInfo.getPlatformId());
			testTemplatServiceService.updTestTemplatService(testTemplate);
		} catch (Exception e) {
			addActionError(e);
		}
		return baseUrl + "testTemplateMain";
	}
	
	/**
	 * 执行删除操作
	 * @param response
	 * @param ids
	 * @throws IOException
	 */
	@RequestMapping(baseUrl + "delAction")
	public void delAction(HttpServletResponse response,@RequestParam("ids") String ids) throws IOException{
		logger.debug("进入删除方法");
		testTemplatServiceService.deleteByIds(ids);
		outputResult("删除成功");
	}
	
	@RequestMapping(baseUrl + "testTemplateDetail")
	@Token(save=true)
	public String testTemplateDetail(@RequestParam("id") int id,HttpServletResponse response,ModelMap model){
		logger.info("进入查看页面");
		TestTemplate testTemplate = (TestTemplate)testTemplatServiceService.getByPk(TestTemplate.class, id);
		model.addAttribute("frmtestTemplate", testTemplate);
		return baseUrl + "testTemplateDetail";
	}
	
	@RequestMapping(baseUrl + "testTemplateItemDetail")
	@Token(save=true)
	public String testTemplateItemDetail(@RequestParam("id") int id,HttpServletResponse response,ModelMap model){
		logger.info("进入查看页面");
		TestTemplateItem testTemplateItem  = (TestTemplateItem)testTemplatServiceService.getByPk(TestTemplateItem.class, id);
		model.addAttribute("frmtestTemplateItem", testTemplateItem);
		return baseUrl + "testTemplateItemDetail";
	}
	
	/**
	 * 执行删除操作
	 * @param response
	 * @param ids
	 * @throws IOException
	 */
	@RequestMapping(baseUrl + "delTestTempItemAction")
	public void delTestTempItemAction(HttpServletResponse response,@RequestParam("ids") String ids) throws IOException{
		logger.debug("进入删除方法");
		testTemplatServiceService.deleteItemByIds(ids);
		outputResult("删除成功");
	}

	/**
	 * 返回模板集合
	 * @param response
	 * @param ids
	 * @throws IOException
	 */
	@RequestMapping(baseUrl + "paperArray")
	public @ResponseBody Map<String, Object> paperArray(HttpServletResponse response,@RequestParam("paperId") String id) throws IOException{
		logger.debug("进入查询模板方法");
		Map<String, Object> rtn = new HashMap<String, Object>();
		String itemList = testTemplatServiceService.doItemList(id);
		if(!itemList.equals("")){
			rtn.put("itemList", itemList);
		}
		return rtn;
	}
	/**
	 * 返回模板集合
	 * @param response
	 * @param ids
	 * @throws IOException
	 */
	@RequestMapping(baseUrl + "getTemplatName")
	public @ResponseBody Map<String, Object> getTemplatName(HttpServletResponse response,@RequestParam("templet_id") String id) throws IOException{
		logger.debug("进入查询模板方法");
		Map<String, Object> rtn = new HashMap<String, Object>();
		String itemList = testTemplatServiceService.getTemplatName(id);
		if(!itemList.equals("")){
			rtn.put("itemList", itemList);
		}
		return rtn;
	}
}
