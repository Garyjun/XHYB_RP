package com.brainsoon.system.action;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.action.Token;
import com.brainsoon.appframe.query.Operator;
import com.brainsoon.appframe.query.QueryConditionItem;
import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.common.pagination.PageInfo;
import com.brainsoon.common.support.Constants;
import com.brainsoon.system.model.Group;
import com.brainsoon.system.model.Role;
import com.brainsoon.system.model.User;
import com.brainsoon.system.model.UserGroup;
import com.brainsoon.system.model.Word;
import com.brainsoon.system.service.IGroupService;
import com.brainsoon.system.service.IUserService;
@Controller
public class GroupAction extends BaseAction {
	/**默认命名空间**/
	private static final String baseUrl = "/system/group/";
	@Autowired
	private IGroupService groupService;
	@Autowired
	private IUserService userService;
	
	@RequestMapping(baseUrl + "list")
	public @ResponseBody PageResult list(HttpServletRequest request) throws UnsupportedEncodingException{
		logger.info("查询用户组列表");
		QueryConditionList conditionList = getQueryConditionList();
		String name = request.getParameter("name");
		if(!StringUtils.isBlank(name)){
			name = URLDecoder.decode(request.getParameter("name"), "UTF-8");
			conditionList.addCondition(new QueryConditionItem("name", Operator.LIKE,"%"+name+"%"));
		}
		return groupService.query4Page(Group.class, conditionList);
	}
	
	@RequestMapping(baseUrl + "listUser")
	public @ResponseBody PageResult listUser(@RequestParam Long groupId,@RequestParam String userIds){
		logger.info("查询用户组列表");
		PageInfo pageInfo = getPageInfo();
		if(groupId!=null){
			return groupService.queryUsersByGroupId(groupId,pageInfo);
		}else{
			int size = pageInfo.getRows();
			int page = pageInfo.getPage();
			int startIndex = (page-1)*size;
			List<User> users = new ArrayList<User>();
			List<User> resultUsers = new ArrayList<User>();
			if(StringUtils.isNotBlank(userIds)){
				for(String userId : userIds.split(",")){
					User user = (User) groupService.getByPk(User.class, 
							Long.parseLong(userId));
					users.add(user);
				}
				int total = users.size();
				int endIndex = 0;
				if(total<size*page){
					endIndex = startIndex+total-(page-1)*size;
				}else{
					endIndex = startIndex+size;
				}
				for(int i=0;i<users.size();i++){
					if(i >= startIndex && i< endIndex){
						resultUsers.add(users.get(i));
					}
				}
			}
			PageResult result = new PageResult();
			result.setRows(resultUsers);
			result.setTotal(users.size());
			return result;
		}
	}
	
	@RequestMapping(baseUrl + "toEdit")
	public String toEdit(Model model, @RequestParam Long id) {
		if (id > 0) {
			Group group = (Group) groupService.getByPk(Group.class, id);
			group.setUserIds(setGroupUserIds(group));
			List<Role> roles = groupService.getRolesByGroupId(id);
			Long[] roleIds = new Long[roles.size()];
			for (int i = 0; i < roles.size(); i++) {
				roleIds[i] = roles.get(i).getId();
			}
			group.setRoleIds(roleIds);
			model.addAttribute("group", group);
		} else {
			model.addAttribute(new Group());
		}
		List<Role> allRoles = groupService.query("from Role");
		model.addAttribute("roles", allRoles);
		model.addAttribute(Constants.ID, id);
		return "system/group/groupEdit";
	}
	
	@RequestMapping(baseUrl + "view")
	public String view(Model model, @RequestParam Long id) {
		Group group = (Group) groupService.getByPk(Group.class, id);
		group.setUserIds(setGroupUserIds(group));
		List<Role> roles = groupService.getRolesByGroupId(id);
		StringBuilder roleNames = new StringBuilder();
		JSONArray ob1 = new JSONArray();
		JSONObject ob2 = new JSONObject();
		for(Role r : roles){
			ob2.put("name", r.getRoleName());
			ob1.add(ob2);
//			roleNames.append(r.getRoleName()+",");
		}
		model.addAttribute("group", group);
		String roleNameStr = roleNames.toString();
		if(!StringUtils.isBlank(roleNameStr)){
			roleNameStr = roleNameStr.substring(0,roleNameStr.length()-1);
		}
		model.addAttribute("roleNames", ob1);
		return "system/group/groupDetail";
	}
	
	private String setGroupUserIds(Group group){
		StringBuilder result = new StringBuilder();
		if(group.getUsers()==null || group.getUsers().size()==0)
			return "";
		for(UserGroup ug : group.getUsers()){
			result.append(ug.getId().getUserId()+",");
		}
		return result.toString();
	}
	
	@RequestMapping(baseUrl + "update")
	public String update(@ModelAttribute("group") Group group,HttpServletRequest request){
		String addUserIds = request.getParameter("addUserIds");
		if(group.getId()==null){
			groupService.addGroup(group, addUserIds);
		}else{
			groupService.updateGroup(group, addUserIds);
		}
		return "system/group/groupList";
	}
	
	@RequestMapping(baseUrl + "addUser")
	@ResponseBody
	public String addUser(@RequestParam String userIds,@RequestParam Long groupId){
		String result = "0";
		try {
			Group group = (Group) groupService.getByPk(Group.class, groupId);
			groupService.updateUserGroup(userIds, group);
		} catch (Exception e) {
			e.printStackTrace();
			result = "-1";
		}
		return result;
	}
	
	@RequestMapping(baseUrl + "delete")
	public @ResponseBody String delete(@RequestParam Long id){
		String result = "0";
		try {
			groupService.deleteGroupById(id);
		} catch (Exception e) {
			result = "-1";
		}
		return result;
	}
	
	@RequestMapping(baseUrl + "batchDelete")
	public @ResponseBody String batchDelete(@RequestParam String ids){
		String result = "0";
		if(!StringUtils.isBlank(ids)){
			try {
				String[] idArray = ids.split(",");
				for(String id : idArray){
					groupService.deleteGroupById(Long.parseLong(id));
				}
			} catch (Exception e) {
				result = "-1";
			}
		}
		return result;
	}
	
	@RequestMapping(baseUrl + "delUser")
	@ResponseBody
	public String delUser(@RequestParam String ids,@RequestParam String groupId){
		String result = "0";
		try {
			groupService.deleteGroupUsers(ids,groupId);
		} catch (Exception e) {
			e.printStackTrace();
			result = "-1";
		}
		return result;
	}
	
	@RequestMapping(baseUrl + "test")
	@ResponseBody
	public String test(@RequestParam Long id){
		User user = (User) userService.getByPk(User.class, id);
		JSONObject result = userService.getDataPrivilegeByUser(user);
		return result.toString();
	}
}
