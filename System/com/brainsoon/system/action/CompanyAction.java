package com.brainsoon.system.action;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
import com.brainsoon.common.support.Constants;
import com.brainsoon.common.support.GlobalDataCacheMap;
import com.brainsoon.system.model.Company;
import com.brainsoon.system.model.User;
import com.brainsoon.system.service.ICompanyService;
import com.brainsoon.system.support.SysOperateLogUtils;

@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CompanyAction extends BaseAction{
	
	@Autowired
	private ICompanyService companyService;
	UserInfo userinfo = LoginUserUtil.getLoginUser();
	
	/**
	 * 查询
	 * @param companyType
	 * @param name
	 * @return
	 * @throws ParseException
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value ="/company/query")
	@ResponseBody
	public PageResult query(@RequestParam(value = "companyType", required = false) String companyType,
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "address", required = false) String address) throws ParseException, UnsupportedEncodingException {
		logger.info("+++++++++++++++++++++++++++------------------进入单位查询列表---------------++++++++++++++++++++++++++++++++====");
		QueryConditionList conditionList = getQueryConditionList();
		if (StringUtils.isNotBlank(companyType)) {
			companyType = URLDecoder.decode(companyType, "UTF-8");
			conditionList.addCondition(new QueryConditionItem("companyType", Operator.LIKE, "%"+companyType+"%"));
		}
		if(StringUtils.isNotBlank(name)){
			name = URLDecoder.decode(name,"UTF-8");
			conditionList.addCondition(new QueryConditionItem("name", Operator.LIKE, "%"+name+"%"));
		}
		if(StringUtils.isNotBlank(address)){
			address = URLDecoder.decode(address,"UTF-8");
			conditionList.addCondition(new QueryConditionItem("address", Operator.LIKE, "%"+address+"%"));
		}
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		return companyService.query4Page(Company.class, conditionList);
	}
	
	
	/**
	 * 修改跳转
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value ="/company/toEdit", method = {RequestMethod.GET})
	@Token(save = true)
	public String toEdit(Model model, @RequestParam("id") Long id,HttpServletRequest request) {
		logger.info("进入修改/添加单位页面");
		
		String fromPeopleUnit = request.getParameter("fromPeopleUnit");
		Company company = null;
		if(id > -1){
			model.addAttribute(Constants.ID,id);
			company = (Company) companyService.getByPk(Company.class, id);
		}else{
			company = new Company();
		}
		model.addAttribute("company", company);
		model.addAttribute("fromPeopleUnit", fromPeopleUnit);
		return "system/dataManagement/company/companyEdit";
	}
	
	/**
	 * 更新数据
	 * @param model
	 * @param company
	 * @param response
	 */
	@RequestMapping(value = "/company/update")
	   @Token(remove=true)
	   public void update(Model model, @ModelAttribute("command") Company company, HttpServletResponse response) {
			try {
				company.setModifiedTime(new Date());
				company.setModifieder(userinfo.getUsername());
				companyService.update(company);
				//刷新人员单位缓存
				GlobalDataCacheMap.refreshPeopleUnit();
				SysOperateLogUtils.addLog("company_addCompany",userinfo.getUsername() , userinfo);
			} catch (Exception e) {
			    logger.error(e.getMessage());
			    addActionError(e);
			}
	   }
	
	/**
	 * 增加
	 * @param model
	 * @param company
	 * @param response
	 */
	@RequestMapping(value ="/company/add")
	@Token(remove=true)
	public void add(Model model, @ModelAttribute("command") Company company, HttpServletResponse response) {
		try {
			company.setCreatedTime(new Date());;
			User user = new User();
			company.setCreateder(userinfo.getUsername());;
			companyService.create(company);
			//刷新人员单位缓存
			GlobalDataCacheMap.refreshPeopleUnit();
			SysOperateLogUtils.addLog("company_addCompany",userinfo.getUsername() , userinfo);
		}catch(Exception e){
			logger.error(e.getMessage());
			addActionError(e);
		}
	}
	
	/**
	 * 删除
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/company/delete")
	@ResponseBody
	public String delete(@RequestParam("id") Long id){
		String result = "ok";
		Company company =  (Company) companyService.getByPk(Company.class, id);
		try{
			companyService.delete(company);
			SysOperateLogUtils.addLog("company_del",company.getName(), userinfo);
		}catch(Exception e){
			result = "no";
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 详细
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value ="/company/view")
	public String view(Model model, @RequestParam("id") Long id) {
		Company company = (Company) baseService.getByPk(Company.class, id);
		model.addAttribute("company", company);
		return "system/dataManagement/company/companyDetail";
	}
	
	/**
	 * 批量删除 
	 * @param ids
	 * @return
	 */
	@RequestMapping(value="/company/batchDelete")
	@ResponseBody
	public String batchDelete(@RequestParam(value = "ids", required = false) String ids){
		String result = "ok";
		try{
			if(StringUtils.isNotBlank(ids)){
				String idArray[] = ids.split(",");
				for (String string : idArray) {
					Company company = (Company) baseService.getByPk(Company.class, Long.parseLong(string));
					companyService.delete(company);
					SysOperateLogUtils.addLog("company_del",company.getName(), userinfo);
				}
			}
		}catch(Exception e){
			result = "no";
			e.printStackTrace();
		}
		return result;
	}
	
	
	
	/**
	 * 处理出版社，根据出版社的名称和地址查询数据库中是否存在与之相同的出版社
	 * 若有返回已经保存的出版社的id
	 * 若没有创建一个出版社保存后返回出版社的id
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/company/doSaveOrUpdate")
	@ResponseBody
	public String doSaveOrUpdate(HttpServletRequest request){
		String companyName = request.getParameter("companyName");
		String companyAddress = request.getParameter("companyAddress");
		List<Company> companyList = null;
		boolean record = false;
		String companyId = null;
		String hql = "from Company where name = '" + companyName + "'";
		try{
			companyList = companyService.query(hql);
			if(companyList.size()>0){
				for (Company company : companyList) {
					if(company.getAddress().equals(companyAddress)){
						record = true;
						companyId = company.getId().toString();
						break;
					}
				}
			}
			
			if(!record){
				Company company = new Company();
				company.setName(companyName);
				company.setAddress(companyAddress);
				companyService.create(company);
				companyId = company.getId().toString();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return companyId;
	}
	/**
	 * 根据资源ids查询名称
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/company/searchName")
	public @ResponseBody String searchName(HttpServletRequest request, HttpServletResponse response) {
		String ids = request.getParameter("ids");
		String names = companyService.searchName(ids);
		return names;
	}
}
