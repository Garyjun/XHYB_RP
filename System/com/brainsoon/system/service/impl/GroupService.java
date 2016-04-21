package com.brainsoon.system.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.pagination.PageInfo;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.system.model.Group;
import com.brainsoon.system.model.GroupRole;
import com.brainsoon.system.model.GroupRoleId;
import com.brainsoon.system.model.Role;
import com.brainsoon.system.model.User;
import com.brainsoon.system.model.UserGroup;
import com.brainsoon.system.model.UserGroupId;
import com.brainsoon.system.service.IGroupService;
import com.brainsoon.system.support.SysOperateLogUtils;
@Service
public class GroupService extends BaseService implements IGroupService {

	private JdbcTemplate jdbcTemplate;
	@Autowired
	public void init(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public List<Group> getGroupsByUserId(Long userId) {
		List<Group> groups = new ArrayList<Group>();
		StringBuffer hql = new StringBuffer();
		hql.append("from Group g where g.id in ( ");
		hql.append("select ug.id.groupId from UserGroup ug where ug.id.userId = :userId");
		hql.append(" )");
		try {
			groups = query(hql.toString(),"userId", userId);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
		return groups;
	}

	@Override
	public PageResult queryUsersByGroupId(Long groupId,PageInfo pageInfo) {
		List<User> users = new ArrayList<User>();
		StringBuffer hql = new StringBuffer();
		hql.append("from User u where u.id in ( ");
		hql.append("select ug.id.userId from UserGroup ug where ug.id.groupId = :groupId");
		hql.append(" )");
		try {
			users = query(hql.toString(),"groupId", groupId);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
		int total = users.size();
		int size = pageInfo.getRows();
		PageResult result = new PageResult();
		int start = (pageInfo.getPage()-1)*pageInfo.getRows();
		int end = 0;
		if(start>=total){
			start = 0;
			end = start + pageInfo.getRows();
			if(end>total){
				end = total;
			}
		}else{
			end = start + pageInfo.getRows();
			if(end>total){
				end = start+total-(pageInfo.getPage()-1)*size;
			}
		}
		List<User> currentUsers = users.subList(start, end);
		result.setRows(currentUsers);
		result.setTotal(users.size());
		return result;
	}

	@Override
	public void addGroup(Group group, String addUserIds) {
		group.setCreatedTime(new Date());
		group.setModifiedTime(new Date());
		create(group);
		setGroupRole(group,"add");
		setUserGroup(addUserIds, group);
		update(group);
		SysOperateLogUtils.addLog("add_group", group.getName(), LoginUserUtil.getLoginUser());
	}
	
	private void setUserGroup(String addUserIds, Group group) {
		String[] ids = getAddUserIds(addUserIds,group);
		List<UserGroup> users = new ArrayList<UserGroup>();
		for(String id : ids){
			if(!StringUtils.isBlank(id)){
				UserGroupId ugid = new UserGroupId(Long.parseLong(id),group.getId());
				UserGroup ug = new UserGroup(ugid);
				users.add(ug);
			}
		}
		group.setUsers(users);
	}
	
	public void deleteGroupUsers(String ids,String groupId){
		for(String userId : ids.split(",")){
			String executeHql = "delete from UserGroup ug where "
					+ "ug.id.userId = :userId and ug.id.groupId = :groupId";
			HashMap<String, Object> paras = new HashMap<String, Object>();
			paras.put("groupId", Long.parseLong(groupId));
			paras.put("userId", Long.parseLong(userId));
			baseDao.executeUpdate(executeHql, paras);
		}
	}
	
	private void setGroupRole(Group group, String type){
		if("update".equals(type)){
			String executeHql = " delete from GroupRole gr where gr.id.groupId=:groupId";
			HashMap<String, Object> paras = new HashMap<String, Object>();
			paras.put("groupId", group.getId());
			baseDao.executeUpdate(executeHql, paras);			
		}
		List<GroupRole> roles = new ArrayList<GroupRole>();
		Long[] roleIds = group.getRoleIds();
		for (Long roleId : roleIds) {
			GroupRoleId id = new GroupRoleId(group.getId(), roleId);
			GroupRole gr = new GroupRole(id);
			roles.add(gr);
		}
		group.setRoles(roles);
	}

	
	public void updateUserGroup(String addUserIds, Group group) {
		String[] ids = getAddUserIds(addUserIds,group);
		List<UserGroup> users = new ArrayList<UserGroup>();
		for(String id : ids){
			if(!StringUtils.isBlank(id)){
				UserGroupId ugid = new UserGroupId(Long.parseLong(id),group.getId());
				UserGroup ug = new UserGroup(ugid);
				users.add(ug);
			}
		}
		group.setUsers(users);
		update(group);
	}
	
	private String[] getAddUserIds(String addUserIds, Group group){
		String[] ids = addUserIds.split(",");
		if(StringUtils.isBlank(group.getUserIds()))
			return ids;
		List<String> idList = new ArrayList<String>();
		for(String id : ids){
			if(group.getUserIds().indexOf(id)==-1)
				idList.add(id);
		}
		String[] result = new String[idList.size()];
		for(int i=0; i<result.length; i++){
			result[i] = idList.get(i);
		}
		return result;
	}

	@Override
	public void updateGroup(Group group,String userIds) {
		group.setModifiedTime(new Date());
//		deleteOldGroupUser(group);
//		setUserGroup(userIds, group);
		setGroupRole(group,"update");
		SysOperateLogUtils.addLog("upd_group", group.getName(), LoginUserUtil.getLoginUser());
		update(group);
	}

	private void deleteOldGroupUser(Group group) {
		String executeHql = " delete from UserGroup ug where ug.id.groupId=:groupId";
		HashMap<String, Object> paras = new HashMap<String, Object>();
		paras.put("groupId", group.getId());
		baseDao.executeUpdate(executeHql, paras);
	}

	@Override
	public void deleteGroupById(Long id) {
		Group group = (Group) getByPk(Group.class, id);
		delete(group);
		SysOperateLogUtils.addLog("del_group", group.getName(), LoginUserUtil.getLoginUser());
	}

	@Override
	public List<Role> getRolesByGroupId(Long id) {
		List<Role> roles = new ArrayList<Role>();
		StringBuffer hql = new StringBuffer();
		hql.append("from Role r where r.id in ( ");
		hql.append("select gr.id.roleId from GroupRole gr where gr.id.groupId = :groupId");
		hql.append(" )");
		try {
			roles = query(hql.toString(),"groupId", id);
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
		return roles;
	}

	/**
	 * 查询用户组下所包含的角色
	 */
	@Override
	public String findRoleNameById(Long groupId) {
		String sql ="select r.role_name from sys_group g,sys_group_role p,sys_role r where p.group_id = g.Id and p.role_id = r.id and g.Id = "+groupId;
		List<String> result = null;
		try{
			result = jdbcTemplate.queryForList(sql, String.class);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return StringUtils.substringBetween(result.toString(), "[", "]");
	}

	/**
	 * 根据用户组id查询该用户组下的所有用户
	 * @param groupId
	 * @return
	 */
	@Override
	public String findUserNamesById(Long groupId) {
		String sql ="select u.login_name from sys_user u,sys_group g,sys_user_group z where z.group_id = g.Id and z.user_id = u.id and g.id = "+ groupId;
		List<String> userList = null;
		try{
			userList = jdbcTemplate.queryForList(sql, String.class);
		}catch(Exception e){
			e.printStackTrace();
		}
		return StringUtils.substringBetween(userList.toString(), "[", "]");
	}
}
