package com.brainsoon.system.action;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
import com.brainsoon.common.pagination.PageInfo;
import com.brainsoon.common.po.RemoteResponse;
import com.brainsoon.common.support.Constants;
import com.brainsoon.common.support.SysResTypeCacheMap;
import com.brainsoon.common.util.dofile.util.OSUtil;
import com.brainsoon.common.util.isbn.ISBNChecker;
import com.brainsoon.common.util.md5.MD5Encoder;
import com.brainsoon.common.util.md5.Md5Tool;
import com.brainsoon.jbpm.service.IJbpmTaskViewService;
import com.brainsoon.semantic.ontology.model.Organization;
import com.brainsoon.semantic.schema.ontology.MetadataDefinition;
import com.brainsoon.statistics.service.ISourceNumService;
import com.brainsoon.system.model.Group;
import com.brainsoon.system.model.MetadataDefinitionGroup;
import com.brainsoon.system.model.PrivilegeUrlMapping;
import com.brainsoon.system.model.Role;
import com.brainsoon.system.model.User;
import com.brainsoon.system.model.log.SysOperateLog;
import com.brainsoon.system.service.IDictValueService;
import com.brainsoon.system.service.IGroupService;
import com.brainsoon.system.service.IOrganizationService;
import com.brainsoon.system.service.IRemoteLoginService;
import com.brainsoon.system.service.IRoleService;
import com.brainsoon.system.service.ISysDirService;
import com.brainsoon.system.service.ISysOperateService;
import com.brainsoon.system.service.IUserService;
import com.brainsoon.system.support.SysOperateLogUtils;
import com.brainsoon.system.util.MetadataSupport;
import com.brainsoon.taskprocess.service.ITaskProcessService;

@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class UserAction extends BaseAction {
	@Autowired
	private IUserService userService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IRemoteLoginService remoteLoginService;
	@Autowired
	private IGroupService groupService;
    @Autowired
    private IOrganizationService organizationService;
    @Autowired
    private IDictValueService dictValueService;
    @Autowired
    private ISourceNumService sourceNumService;
    @Autowired
    private ITaskProcessService taskProcessService;
	@Autowired
	private IJbpmTaskViewService jbpmTaskViewService;
	@Autowired
	private ISysOperateService sysOperateService;
	@Autowired
	private ISysDirService sysDirService;
	
	@RequestMapping(value = "/user/list")
	public String list(Model model, User user,HttpServletRequest request) {
		logger.debug("to list ");
		model.addAttribute(Constants.COMMAND, user);
		/*request.setAttribute("record","update");
		request.setAttribute("status", "-1");*/
		return "/system/user/userMain";
	}

	@RequestMapping(value = "/user/query")
	@ResponseBody
	public PageResult query(User user,HttpServletRequest request) throws UnsupportedEncodingException {
		PageInfo pageInfo = getPageInfo();
		return userService.queryUsers(pageInfo, user);
	}

	@RequestMapping(value = "/user/toEdit")
	@Token(save = true)
	public String toEdit(Model model, @RequestParam("id") Long id) {
		logger.info("--------------------------传过来的ID值-------"+id+"--------------------------------------");
		if (!model.containsAttribute(Constants.COMMAND)) {
			if (id > 0) {
				User user = (User) baseService.getByPk(User.class, id);
				logger.info("--------------------------假如ID>0-------"+user.getUserName()+"--------------------------------------");
				List<Role> roles = roleService.getRolesByUserId(user.getId());
				Long[] roleIds = new Long[roles.size()];
				logger.info("--------------------------角色大小-------"+roleIds.length+"--------------------------------------");
				for (int i = 0; i < roles.size(); i++) {
					roleIds[i] = roles.get(i).getId();
					logger.info("--------------------------循环角色ID-------"+roles.get(i).getId()+"--------------------------------------");
				}
				user.setRoleIds(roleIds);
				if(StringUtils.isNotBlank(user.getDataPreRangeArray())&&!"[".equals(user.getDataPreRangeArray())){
					model.addAttribute("dataPreRangeArray", user.getDataPreRangeArray());
				}
				List<Group> groups = groupService.getGroupsByUserId(user.getId());
				logger.info("--------------------------循环角色ID-------"+groups.size()+"--------------------------------------");
				Long[] groupIds = new Long[groups.size()];
				for (int i=0; i < groups.size(); i++){
					groupIds[i] = groups.get(i).getId();
				}
				user.setGroupIds(groupIds);
				
				//huashi
				if(!StringUtils.isBlank(user.getResType())){
					Long[] resTypeIds = new Long[user.getResType().split(",").length];
					for(int i=0; i<resTypeIds.length; i++){
						resTypeIds[i] = Long.parseLong(user.getResType().split(",")[i]);
						logger.info("-------------------------resTypeIds-------"+resTypeIds[i]+"--------------------------------------");
					}
					user.setResTypeIds(resTypeIds);
				}
				model.addAttribute(Constants.COMMAND, user);
				String groupName = "";
				String groupValue = "";
				logger.info("-------------------------判断组ID是否为空-------"+user.getOrgId()+"--------------------------------------");
				if(StringUtils.isNotBlank(user.getOrgId())){
					String goupId[] = user.getOrgId().split(",");
					logger.error("-------------------------组ID大小-------"+ goupId.length+"--------------------------------------");
					List<String> list = new ArrayList<String>();
					for(int i=0;i<goupId.length;i++){
						groupValue = groupValue+goupId[i]+",";
						String hql = "select name from Organization where id="+Long.parseLong(goupId[i]);
						list = groupService.query(hql);
						logger.info("-------------------------组IDlist大小-------"+list.size()+"--------------------------------------");
						if(list.size()>0){
							groupName = groupName + list.get(0)+",";
						}
					}
					logger.info("-------------------------groupName值-------"+groupName+"--------------------------------------");
					if(groupValue.endsWith(",")){
						groupValue = groupValue.substring(0,groupValue.length()-1);
					}
					if(groupName.endsWith(",")){
						groupName = groupName.substring(0,groupName.length()-1);
					}
					model.addAttribute("groupName", groupName);
					model.addAttribute("groupValue", groupValue);
				}
			} else {
				model.addAttribute(Constants.COMMAND, new User());
			}
		}
		model.addAttribute(Constants.ID, id);
		List<Role> roles = userService.query(" from Role ");
//		logger.info("-------------------------roles值大小-------"+roles.size()+"--------------------------------------");
		model.addAttribute("roles", roles);
		List<Group> groups = userService.query(" from Group ");
//		logger.info("-------------------------groups值大小-------"+groups.size()+"--------------------------------------");
		model.addAttribute("groups", groups);
//		logger.info("-------------------------organizationService.getOrganizationJson()值大小-------"+organizationService.getOrganizationJson()+"--------------------------------------");
		model.addAttribute("orgJson",organizationService.getOrganizationJson());
//		logger.info("-------------------------dictValueService.getContentByTableName(publishType)值大小-------"+dictValueService.getContentByTableName("publishType")+"--------------------------------------");
		model.addAttribute("resType", dictValueService.getContentByTableName("publishType"));
//		logger.info("-------------------------getCoreMetadata(id)值大小-------"+getCoreMetadata(id)+"--------------------------------------");
		model.addAttribute("coreMetadata", getCoreMetadata(id));
		return "system/user/userEdit";
	}
	@RequestMapping(value = "/user/view")
	public String view(Model model, @RequestParam("id") Long id) {
		User user = (User) baseService.getByPk(User.class, id);
		List<Role> roles = roleService.getRolesByUserId(user.getId());
		List<Group> groups = groupService.getGroupsByUserId(user.getId());
		Map<String,String> resFieldMap = new HashMap<String,String>();
		//获取该用户拥有的资源库id
		String resType = user.getResType();
		String resTypeName = "";
		JSONArray array = new JSONArray();
		String groupName = "";
		if(StringUtils.isNotBlank(resType)){
			resType = resType.substring(0,resType.length()-1);
			String[] resTypes = resType.split(",");
			if(resTypes!=null && resTypes.length>0){
				for(String oneResType:resTypes){
					JSONObject json = new JSONObject();
					json.put("resTypeId",oneResType);
					//根据资源库类型获取该资源库的元数据的id
					List<String> list = sourceNumService.getListGroupId(Integer.parseInt(oneResType));
					JSONArray groupsArray = new JSONArray();
					for(String groupId : list){
						MetadataDefinitionGroup mdg = (MetadataDefinitionGroup) sourceNumService.getByPk(
								MetadataDefinitionGroup.class, Long.parseLong(groupId));
						JSONObject groupJson = new JSONObject();
						//获取该用户选中的元数据名称
						groupJson.put("groupName", mdg.getFieldZhName());
						groupJson.put("selectField", getSelectMetadata(user,mdg.getId()+""));
						groupsArray.add(groupJson);
					}
					json.put("groupArray", groupsArray.toString());
					json.put("resTypeName", SysResTypeCacheMap.getValue(oneResType));
					array.add(json);
					resTypeName += SysResTypeCacheMap.getValue(oneResType)+",";
				}
				resTypeName = resTypeName.substring(0,resTypeName.length()-1);
			}
		}
		List<String> list1 = new ArrayList<String>();
		String goupId[] = user.getOrgId().split(",");
		for(int i=0;i<goupId.length;i++){
//			groupValue = groupValue+goupId[i]+",";
			String hql = "select name from Organization where id="+Long.parseLong(goupId[i]);
			list1 = groupService.query(hql);
			logger.info("-------------------------组IDlist大小-------"+list1.size()+"--------------------------------------");
			if(list1.size()>0){
				groupName = groupName + list1.get(0)+",";
			}
		}
		if(groupName.endsWith(",")){
			groupName = groupName.substring(0,groupName.length()-1);
		}
		model.addAttribute("groupName", groupName);
		int isPrivate = user.getIsPrivate();
		String isPrivateName = "否";
		if(isPrivate == 1){
			isPrivateName = "是";
		} 
		model.addAttribute(Constants.COMMAND, user);
		model.addAttribute("roles", roles);
		model.addAttribute("groups", groups);
		model.addAttribute("isPrivateName", isPrivateName);
		model.addAttribute("orgJson",organizationService.getOrganizationJson());
		model.addAttribute("resTypeName", resTypeName);
		model.addAttribute("resTypeObj", array.toString());
		model.addAttribute("coreMetadata", getBaseMetadata(user));
		return "system/user/userView";
	}
	@RequestMapping(value = "/user/add")
	@Token(remove = true)
	public void add(Model model, @ModelAttribute("command") User command,HttpServletRequest request, HttpServletResponse response) {
		try {
			UserInfo user = LoginUserUtil.getLoginUser();
			command.setPlatformId(user.getPlatformId());
			userService.doCreateUser(command);
			user.setLoginIp(OSUtil.getIpAddr(request));
			SysOperateLogUtils.addLog("user_add", command.getUserName(), user);
		} catch (Exception e) {
			logger.error(e.getMessage());
			addActionError(e);
		}
	}

	@RequestMapping(value = "/user/update")
	@Token(remove = true)
	public void update(Model model, @ModelAttribute("command") User command, HttpServletResponse response,HttpServletRequest request) {
		try {
			UserInfo user = LoginUserUtil.getLoginUser();
			logger.info("------------------------------进入更新user----"+user.toString()+"------------------------------");
			command.setPlatformId(user.getPlatformId());
			command.setModifiedTime(user.getPasswordLastestModifiedTime());
			userService.doUpdateUser(command);
			
			//如果修改的用户信息的登录名和session中存在的用户是一个人则重置session中的信息
			//fengda 2015年11月14日
			if(command.getLoginName().equals(user.getName())){
				//更新保存登录信息的session的用户的资源库的信息
				UserInfo Newuser = LoginUserUtil.refshUserInfo(command,request);
				SysOperateLogUtils.addLog("user_modify", command.getUserName(), Newuser);
			}else{
				SysOperateLogUtils.addLog("user_modify", command.getUserName(), user);
			}
			
			
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			addActionError("修改异常！");
		}

	}
	
	@RequestMapping(value = "/user/batchDisabled/{ids}")
	@ResponseBody
	public String batchDisabled(@PathVariable String ids){
		logger.debug("*********batchDisabled*********");
		String record = "";
		try {
			String[] idArray = ids.split(",");
			userService.doDisabled(idArray);
			record = "success";
		} catch (Exception e) {
			record = "fail";
			logger.error(e.getMessage());
		}
		return record;
	}
	
	@RequestMapping(value = "/user/batchEnabled/{ids}")
	@ResponseBody
	public String batchEnabled(@PathVariable String ids){
		String record = "";
		logger.debug("*********batchDisabled*********");
		try {
			String[] idArray = ids.split(",");
			userService.doEnabled(idArray);
			record = "success";
		} catch (Exception e) {
			record = "fail";
			logger.error(e.getMessage());
		}
		return record;
	}	

	/**
	 * 删除前校验是否已被分配为加工员
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/user/checkDelete")
	public @ResponseBody String checkDelete(HttpServletRequest request){
		logger.info("进入校验方法");
		String id = request.getParameter("id");
		String ids[] = id.split(",");
		String tag ="0";
		for(int i=0;i<ids.length;i++){
			/*//判断要删除的用户是否被分配为加工员,返回值不为0，则表示不可以被删除
			tag = taskProcessService.canDeleteUser(ids[i]);
			if(!tag.equals("0") && tag !=""){
				return tag;
			}*/
			
			//判断要删除的用户是否有除登陆登出系统之外的操作，有则不允许删除
			User user = (User) userService.getByPk(User.class, Long.parseLong(ids[i]));
			String name = user.getLoginName();
			List<SysOperateLog> logList = new ArrayList<SysOperateLog>();
			logList = userService.query("from SysOperateLog where loginname ='"+name+"'");
			if(logList.size()>0 && logList!=null){
				for (SysOperateLog sysOperateLog : logList) {
					if(!sysOperateLog.getOperateType().equals("user_loginin") && !sysOperateLog.getOperateType().equals("user_loginout")){
						tag = "1";
						return tag;
					}
				}
			}
		}
		return tag;
	}
	@RequestMapping(value = "/user/delete/{id}")
	@ResponseBody
	public String delete(@PathVariable Long id) {
		String record = "";
		try {
			if(id>1){
				UserInfo user = LoginUserUtil.getLoginUser();
				User delUser = (User) userService.getByPk(User.class, id);
				String name = delUser.getUserName();
				userService.delete(User.class, id);
				SysOperateLogUtils.addLog("user_delete", name, user);
			}
			record = "success";
		} catch (Exception e) {
			record = "fail";
			logger.error(e.getMessage());
		}

		return record;
	}

	@RequestMapping(value = "/user/resetPassword")
	@ResponseBody
	public String resetPassword(@RequestParam Long id) {
		String record = "";
		try {
			UserInfo user = LoginUserUtil.getLoginUser();
			User delUser = (User) userService.getByPk(User.class, id);
			userService.doResetPassword(id);
			record = "success";
			SysOperateLogUtils.addLog("user_reset_password", delUser.getUserName(), user);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return record;
	}
	
	@RequestMapping(value = "/user/getRolesDesc", method = { RequestMethod.GET })
	@ResponseBody
	public String getRolesDesc(@RequestParam Long id) {
		String result = "";
		try {
			User user = (User) baseService.getByPk(User.class, id);
			List<Role> roles = roleService.getRolesByUserId(user.getId());
			for(Role r : roles){
				result += r.getRoleName() + "，";
			}
			if(result.length()>0)
				result = result.substring(0, result.length()-1);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return result;
	}

	@RequestMapping(value = "/user/updatePassword")
	@ResponseBody
	public String updatePassword(
			@RequestParam(value = "id") Long id,
			@RequestParam(value = "oldPassword") String oldPassword,
			@RequestParam(value = "newPassword") String newPassword) {
		try {
			UserInfo user = LoginUserUtil.getLoginUser();
			User delUser = (User) userService.getByPk(User.class, id);
			userService.updatePassword(id, oldPassword, newPassword);
			SysOperateLogUtils.addLog("user_modify_password", delUser.getUserName(), user);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return "密码修改成功！";
	}

	@RequestMapping(value = "/user/batchDelete/{ids}")
	@ResponseBody
	public String batchDelete(@PathVariable String ids) {
		logger.debug("*********batchDelete*********");
		String record = "";
		try {
			UserInfo user = LoginUserUtil.getLoginUser();
			String[] idArray = ids.split(",");
			for (int i = 0; i < idArray.length; i++) {
				userService.delete(User.class, new Long(idArray[i]));
			}
			record = "success";
			SysOperateLogUtils.addLog("user_delete", "...", user);
		} catch (Exception e) {
			record = "fail";
			logger.error(e.getMessage());
		}
		return record;
	}

	
	@RequestMapping(value = "/user/gotoAccountSetting")
	public String gotoAccountSetting(Model model) {
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		User user = (User) baseService.getByPk(User.class, userInfo.getUserId());
		model.addAttribute(Constants.COMMAND, user);
		return "system/user/accountSetting";
	}
	
	@RequestMapping(value = "/user/updateAccountInfo")
	@ResponseBody
	public String updateAccountInfo(@ModelAttribute("command") User command){
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		User user = (User) baseService.getByPk(User.class, userInfo.getUserId());
		user.setUserName(command.getUserName());
		user.setMobile(command.getMobile());
		user.setPhone(command.getPhone());
		user.setEmail(command.getEmail());
		try {
			baseService.update(user);
		} catch (Exception e) {
			logger.error(e.getMessage());
			addActionError("修改异常！");
		}
		return "帐户设置成功";
	}
	

	@RequestMapping(value = "/user/checkLoginName")
	@ResponseBody
	public String checkLoginName(HttpServletRequest request) {
		String fieldValue = request.getParameter("fieldValue");
		logger.debug("name ****** " + request.getParameter("name"));
		logger.debug("userId ****** " + request.getParameter("userId"));
		User user = userService.getUserByLoginName(fieldValue);
		if (user != null) {
			return "-1";
		}
		return "0";

	}
	
	@RequestMapping(value = "/user/checkLoginPassword")
	@ResponseBody
	public String checkLoginPassword(HttpServletRequest request) {
		String oldPassword = request.getParameter("fieldValue");
//		String newPassword = request.getParameter("newPassword");
		String password = LoginUserUtil.getLoginUser().getPassword();
		if(!Md5Tool.verifyPassword(oldPassword, password))
			return "{\"jsonValidateReturn\": [\"oldPassword\",false]}";
		else
			return "{\"jsonValidateReturn\": [\"oldPassword\",true]}";
	}
	
	@RequestMapping(value = "/user/checkNewPassword")
	@ResponseBody
	public String checkNewPassword(HttpServletRequest request) {
		String newPassword = request.getParameter("fieldValue");
		String password = LoginUserUtil.getLoginUser().getPassword();
		if(Md5Tool.verifyPassword(newPassword, password))
			return "{\"jsonValidateReturn\": [\"newPassword1\",false]}";
		else
			return "{\"jsonValidateReturn\": [\"newPassword1\",true]}";
	}
	
	@RequestMapping(value = "/user/checkPassword")
	@ResponseBody
	public String checkPassword(HttpServletRequest request) {
		String oldPassword = request.getParameter("oldPassword");
		String newPassword1 = request.getParameter("newPassword1");
		String password = LoginUserUtil.getLoginUser().getPassword();
		if(!Md5Tool.verifyPassword(oldPassword, password))
			return "-1";
		else if(oldPassword.equals(newPassword1))
			return "-2";
		else
			return "0";
	}

//	@RequestMapping(value = "/user/gotoEditPassword")
//	public String gotoEditPassword() {
//		return "system/user/update_password";
//	}
	
	@RequestMapping(value = "/user/getDataPrivilge")
	@ResponseBody
	public String getDataPrivilge(@RequestParam String typeId){
		List<String> list = sourceNumService.getListGroupId(Integer.parseInt(typeId));
		//存放所有被选中的资源库对应的元数据的id和中文名称，和所有的元数据项
		JSONArray array = new JSONArray();
		for(String id : list){
			MetadataDefinitionGroup mdg = (MetadataDefinitionGroup) sourceNumService.getByPk(
					MetadataDefinitionGroup.class, Long.parseLong(id));
			
			//被选中的资源库对应的元数据的id和中文名称，和所有的元数据项
			JSONObject jo = new JSONObject();
			jo.put("id", mdg.getId());
			jo.put("name", mdg.getFieldZhName());
			
			//存放所有的元数据项
			JSONArray fieldArray = new JSONArray();
			
			//根据元数据的id查询所对应的元数据
			List<MetadataDefinition> metadataList = MetadataSupport.
					getMetadataByGroupId(mdg.getId()+"");
			
			for(MetadataDefinition md : metadataList){
				
				//存放与资源库相对应的元数据项的中英文名称
				JSONObject metaJson = new JSONObject();
				metaJson.put("name", md.getFieldZhName());
				metaJson.put("enName", md.getFieldName());
				fieldArray.add(metaJson);
			}
			jo.put("field", fieldArray);
			array.add(jo);
		}
		return array.toString();
	}

	@RequestMapping(value = "/user/getDataRangePreJson")
	@ResponseBody
	public String getDataRangePreJson(@RequestParam Long id){
		User user = (User) baseService.getByPk(User.class, id);
		String result = userService.getDataRangePreJson(user);
		return result;
	}
	
	@RequestMapping(value = "/user/setDataRangePreJson")
	@ResponseBody
	public String setDataRangePreJson(HttpServletRequest request){
		String result = "0";
		String userId = request.getParameter("userId");
		String conditions = request.getParameter("conditions");
		try {
			User user = (User) baseService.getByPk(User.class, Long.parseLong(userId));
			user.setDataPreRangeArray(conditions);
			baseService.update(user);
		} catch (Exception e) {
			result = "-1";
		}
		return result;
	}
	
	private String getCoreMetadata(Long userId){
		logger.info("-------------------------进入getCoreMetadata方法-------"+userId+"--------------------------------------");
		JSONArray array = new JSONArray();
		User user = null;
		String checkedCoreMetadata = "";
		if(userId>0){
			user = (User) baseService.getByPk(User.class, userId);
			if(StringUtils.isNotBlank(user.getDataPreJson())){
				logger.info("-------------------------进入getCoreMetadata方法-------"+user.getDataPreJson()+"--------------------------------------");
			checkedCoreMetadata = user.getDataPreJson();
			}
		}
		logger.info("-------------------------进入getCoreMetadata的MetadataSupport.getCommonMetadatas方法---------------------------------------------");
		List<MetadataDefinition> list = MetadataSupport.getCommonMetadatas();
//		logger.info("-------------------------进入getCoreMetadata方法list大小-------"+list.size()+"--------------------------------------");
		if(list!=null&&list.size()>0){
		for(MetadataDefinition metaData : list){
			JSONObject json = new JSONObject();
			json.put("name", metaData.getFieldZhName());
			json.put("enName", metaData.getFieldName());
			if(!StringUtils.isBlank(checkedCoreMetadata)
					&&checkedCoreMetadata.indexOf(metaData.getFieldName())!=-1){
				json.put("checked", true);
			}else{
				json.put("checked", false);
			}
			array.add(json);
		}
		}
		logger.info("-------------------------进入getCoreMetadata方法array大小-------"+array.toString()+"--------------------------------------");
		return array.toString();
	}
	private String getCoreMetadataDetail(User user){
		String checkedCoreMetadata =  user.getDataPreJson();
		List<MetadataDefinition> list = MetadataSupport.getCommonMetadatas();
		String coreFields = "";
		String dataPreJson = user.getDataPreJson();
		for(MetadataDefinition metaData : list){
			if("[]".equals(dataPreJson)){
				coreFields = coreFields+ metaData.getFieldZhName()+",&nbsp;&nbsp;&nbsp;&nbsp;";
			}else{
				if(!StringUtils.isBlank(checkedCoreMetadata)
						&&checkedCoreMetadata.indexOf(metaData.getFieldName())!=-1){
					coreFields = coreFields+ metaData.getFieldZhName()+",&nbsp;&nbsp;&nbsp;&nbsp;";
				}
			}
		}
		if(coreFields.length()>0){
			coreFields = coreFields.substring(0,coreFields.length()-1);
		}
		return coreFields;
	}
	private String getBaseMetadataDetail(User user,String groupId){
		String checkedCoreMetadata =  user.getDataPreJson();
		List<MetadataDefinition> metadataList = MetadataSupport.
				getMetadataByGroupId(groupId);
		String coreFields = "";
		String dataPreJson = user.getDataPreJson();
		int i=1;
		for(MetadataDefinition metaData : metadataList){
			if("[]".equals(dataPreJson)){
				if(i%5 == 0 ){
					coreFields = coreFields+ metaData.getFieldZhName()+"<br/>";
				}else{
					coreFields = coreFields+ metaData.getFieldZhName()+",&nbsp;&nbsp;&nbsp;&nbsp;";
				}
			}else{
				if(!StringUtils.isBlank(checkedCoreMetadata)
						&&checkedCoreMetadata.indexOf(metaData.getFieldName())!=-1){
					if(i%5 == 0 ){
						coreFields = coreFields+ metaData.getFieldZhName()+"<br/>";
					}else{
						coreFields = coreFields+ metaData.getFieldZhName()+",&nbsp;&nbsp;&nbsp;&nbsp;";
					}
				}
			}
			i++;
		}
		if(coreFields.length()>0){
			coreFields = coreFields.substring(0,coreFields.length()-1);
		}
		return coreFields;
	}
	
	/**
	 * 用于查询当前登陆用户选中的资源库对应的元数据的中文名称
	 * @param user
	 * @param groupId
	 * @return
	 */
	private String getSelectMetadata(User user,String groupId){
		
		//根据元数据的id，查询出对应的所有的元数据
		List<MetadataDefinition> metadataList = MetadataSupport.getMetadataByGroupId(groupId);
		String coreFields = "";
		
		//查询sys_user表的data_pre_json字段（选中的元数据）
		String dataPreJson = user.getDataPreJson();
		int i = 1;
		
		//筛选出所有的元数据与之当前用户所选中的元数据
		for(MetadataDefinition metaData : metadataList){
			if(!"[]".equals(dataPreJson) && dataPreJson.indexOf(metaData.getFieldName())!= -1){
				//用于在页面展示时，使得每五个一行，之后在换行
				if(i%5==0){
					coreFields = coreFields+ metaData.getFieldZhName()+"<br/>";
				}else{
					coreFields = coreFields+ metaData.getFieldZhName()+",&nbsp;&nbsp;";
				}
				i++;
			}
		}
		
		if(coreFields.length()>0){
			coreFields = coreFields.substring(0,coreFields.length()-1);
		}
		return coreFields;
	}
	
	/**
	 * 获取当前用户选中的基本元数据
	 * fengda2015年8月21日
	 * @param user
	 * @return
	 */
	private String getBaseMetadata(User user){
		List<MetadataDefinition> list = MetadataSupport.getCommonMetadatas();
		String coreFields = "";
		String dataPreJson = user.getDataPreJson();
		int i = 1;
		
		for(MetadataDefinition metaData : list){
			if(!"[]".equals(dataPreJson) && dataPreJson.indexOf(metaData.getFieldName()) != -1){
				//用于在页面展示时，使得每五个一行，之后在换行
				if(i%5 == 0){
					coreFields = coreFields+ metaData.getFieldZhName()+"<br/>";
				}else{
					coreFields = coreFields+ metaData.getFieldZhName()+",&nbsp;&nbsp;";
				}
				i++;
			}
		}
		if(coreFields.length()>0){
			coreFields = coreFields.substring(0,coreFields.length()-1);
		}
		return coreFields;
	}
	
	@RequestMapping(value = "/user/getResTypeDescByTypeIds")
	@ResponseBody
	public String getResTypeDescByTypeIds(String publishTypeIds){
		String typeDesc = "[";
		if(StringUtils.isNotEmpty(publishTypeIds)){
			String[] idArr = publishTypeIds.split(",");
			for(String publishType: idArr){
				Object obj = SysResTypeCacheMap.getValue(publishType);
				if(obj!=null){
					String temp = "{" + "\"publishType\":" + "\"" + publishType +"\"" + "," + "\"publishTypeDesc\":";
					typeDesc += temp + "\"" + obj.toString() + "\"},";
				}
			}
			if(StringUtils.isNotEmpty(typeDesc)&&typeDesc.length()>1){
				typeDesc = typeDesc.substring(0, typeDesc.length()-1) + "]";
			}else{
				typeDesc = null;
			}
		}
		return typeDesc;
	}
	
	@RequestMapping(value = "/user/getMetadataDefinitionByName")
	@ResponseBody
	public MetadataDefinition getMetadataDefinitionByName(String enName){
		MetadataDefinition metaData = MetadataSupport.getMetadataDefinitionByName(enName);
		/*int fieldType = -1;
		if(metaData!=null){
			fieldType = metaData.getFieldType();
			metaData.getValueRange();
		}
		return fieldType;*/
		return metaData;
	}
	
	@RequestMapping(value="/user/getResourcesDirectory")
	@ResponseBody
	public String getResourcesDirectory(@RequestParam String typeId){
		JSONArray array = new JSONArray();
		JSONObject jo = new JSONObject();
		try{
			String resTypes = sysDirService.findResourceByResType(typeId);
			if(resTypes.length()>0 && resTypes!= ""){
				String resType[] = resTypes.split(",");
				for (String string : resType) {
					jo.put("resType", string);
					array.add(jo);
				}
			}else{
				array.add("");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return array.toString();
	}
}
