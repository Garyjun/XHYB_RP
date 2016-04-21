package com.brainsoon.system.action;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
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
import com.brainsoon.system.model.Staff;
import com.brainsoon.system.service.IStaffService;
import com.brainsoon.system.support.SysOperateLogUtils;

@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class StaffAction extends BaseAction{

	@Autowired
	private IStaffService staffService;
	UserInfo userInfo = LoginUserUtil.getLoginUser();
	/**
	 * 列表页面加载
	 * @param staffType
	 * @param name
	 * @return
	 * @throws ParseException
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value ="/staff/query")
	@ResponseBody
	public PageResult query(@RequestParam(value = "staff_type", required = false) String staffType,
			@RequestParam(value = "name", required = false) String name,@RequestParam(value = "address", required = false) String address) throws ParseException, UnsupportedEncodingException {
		logger.info("+++++++++++++++++++++++++++------------------进入人员查询列表---------------++++++++++++++++++++++++++++++++====");
		QueryConditionList conditionList = getQueryConditionList();
		if (StringUtils.isNotBlank(staffType)) {
			staffType = URLDecoder.decode(staffType, "UTF-8");
			conditionList.addCondition(new QueryConditionItem("userType", Operator.LIKE, "%"+staffType+"%"));
		}
		if(StringUtils.isNotBlank(name)){
			name = URLDecoder.decode(name,"UTF-8");
			conditionList.addCondition(new QueryConditionItem("name", Operator.LIKE, "%"+name+"%"));
		}
		if(StringUtils.isNotBlank(address)){
			address = URLDecoder.decode(address,"UTF-8");
			conditionList.addCondition(new QueryConditionItem("address", Operator.LIKE, "%"+address+"%"));
		}
		return staffService.query4Page(Staff.class, conditionList);
	}
	
	
	/**
	 * 编辑添加页面跳转
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value ="/staff/toEdit", method = {RequestMethod.GET})
	@Token(save = true)
	public String toEdit(Model model, @RequestParam("id") Long id,HttpServletRequest request) {
		logger.info("进入修改/添加人员页面"+id);
		Staff staff = null;
		String fromPeopleUnit = request.getParameter("fromPeopleUnit");
		if(id > -1){
			logger.info("进111111111111111111111"+id);
			model.addAttribute(Constants.ID,id);
			staff = (Staff) staffService.getByPk(Staff.class, id);
			logger.info("进222222222222222"+id);
		}else{
			staff = new Staff();
		}
		logger.info("进333333333333333"+id);
		if(StringUtils.isNotBlank(fromPeopleUnit)){
			model.addAttribute("fromPeopleUnit", fromPeopleUnit);
		}else{
			model.addAttribute("fromPeopleUnit", "");
		}
		model.addAttribute("staff", staff);
		return "system/dataManagement/staff/staffEdit";
	}
	
	
	/**
	 * 更新数据
	 * @param model
	 * @param staff
	 * @param response
	 */
	   @RequestMapping(value = "/staff/update")
	   @Token(remove=true)
	   public void update(Model model, @ModelAttribute("command") Staff staff, HttpServletResponse response) {
			try {
				staffService.update(staff);
				//刷新人员单位缓存
				GlobalDataCacheMap.refreshPeopleUnit();
				SysOperateLogUtils.addLog("staff_upd", staff.getName(), userInfo);
			} catch (Exception e) {
			    logger.error(e.getMessage());
			    addActionError(e);
			}
	   }
   
	   
	   /**
	    * 保存数据
	    * @param model
	    * @param staff
	    * @param response
	    */
	    @RequestMapping(value ="/staff/add")
		@Token(remove=true)
		public void add(Model model, @ModelAttribute("command") Staff staff, HttpServletResponse response) {
			try {
				staffService.create(staff);
				//刷新人员单位缓存
				GlobalDataCacheMap.refreshPeopleUnit();
				SysOperateLogUtils.addLog("staff_add", staff.getName(), userInfo);
			}catch(Exception e){
				logger.error(e.getMessage());
				addActionError(e);
			}
		}  
	    
	    
	    @RequestMapping(value = "/staff/delete")
		@ResponseBody
		public String delete(@RequestParam("id") Long id){
			String result = "ok";
			Staff staff =  (Staff) staffService.getByPk(Staff.class, id);
			try{
				staffService.delete(staff);
				SysOperateLogUtils.addLog("staff_del", staff.getName(), userInfo);
			}catch(Exception e){
				result = "no";
				e.printStackTrace();
			}
			return result;
		}
	    
	    
	    /**
	     * 详细页面跳转
	     * @param model
	     * @param id
	     * @return
	     */
	    @RequestMapping(value ="/staff/view")
		public String view(Model model, @RequestParam("id") Long id) {
	    	Staff staff = (Staff) baseService.getByPk(Staff.class, id);
			model.addAttribute("staff", staff);
			return "system/dataManagement/staff/staffDetail";
		}
	    
	    
	    /**
	     * 批量删除
	     * @param ids
	     * @return
	     */
	    @RequestMapping(value="/staff/batchDelete")
		@ResponseBody
		public String batchDelete(@RequestParam(value = "ids", required = false) String ids){
			String result = "ok";
			try{
				if(StringUtils.isNotBlank(ids)){
					String idArray[] = ids.split(",");
					for (String string : idArray) {
						Staff staff = (Staff) baseService.getByPk(Staff.class, Long.parseLong(string));
						staffService.delete(staff);
						SysOperateLogUtils.addLog("staff_del", staff.getName(), userInfo);
					}
				}
			}catch(Exception e){
				result = "no";
				e.printStackTrace();
			}
			return result;
		}
	    
	    
	    
	    /**
	     * 根据姓名和地址查询员工表中，该人是否存在
	     * 存在返回此人的id，不存在创建，返回创建后此人的id
	     * @param request
	     * @return
	     */
	    @RequestMapping(value = "/staff/doSaveOrUpdate")
		@ResponseBody
		public String doSaveOrUpdate(HttpServletRequest request){
	    	String name = request.getParameter("name");
	    	String address = request.getParameter("address");
	    	String hql = "from Staff where name = '" + name + "'" ;
	    	List<Staff> staffList = null;  //记录查询出来的员工实体类列表
	    	String staffId = null;         //记录返回值员工的id
	    	boolean record = false;        //标识数据库中是否存在该人员的信息
	    	try{
	    		staffList = staffService.query(hql);
	    		if(staffList.size()>0){
	    			for (Staff staff : staffList) {
						if(address.equals(staff.getAddress())){
							staffId = staff.getId().toString();
							record = true ;
							break;
						}
					}
	    		}
	    		
	    		if(!record){
	    			Staff staff = new Staff();
	    			staff.setName(name);
	    			staff.setAddress(address);
	    			staffService.create(staff);
	    			staffId = staff.getId().toString();
	    		}
	    		
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}
	    	return staffId;
	    }
	/**
	 * 根据资源ids查询名称
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/staff/searchName")
	public @ResponseBody String searchName(HttpServletRequest request, HttpServletResponse response) {
		String ids = request.getParameter("ids");
		String names = staffService.searchName(ids);
		return names;
	}
}
