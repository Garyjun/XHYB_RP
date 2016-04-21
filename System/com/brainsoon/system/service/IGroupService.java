package com.brainsoon.system.service;

import java.util.List;

import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.common.pagination.PageInfo;
import com.brainsoon.common.service.IBaseService;
import com.brainsoon.system.model.Group;
import com.brainsoon.system.model.Role;

public interface IGroupService extends IBaseService {
	/**
	 * 根据用户id获取用户组
	 * 
	 * @param userId
	 * @return
	 */
	public List<Group> getGroupsByUserId(Long userId);
	
	/**
	 * 根据groupId查询用户
	 * @param groupId
	 */
	public PageResult queryUsersByGroupId(Long groupId,PageInfo pageInfo);
	/**
	 * 添加用户组
	 * @param userIds
	 */
	public void addGroup(Group group,String userIds);
	/**
	 * 编辑用户组
	 * @param userIds
	 */
	public void updateGroup(Group group,String userIds);
	/**
	 * 删除用户组
	 * @param id
	 */
	public void deleteGroupById(Long id);
	/**
	 * 根据groupId查询角色
	 * @param groupId
	 */
	public List<Role> getRolesByGroupId(Long id);
	/**
	 * 根据userIds和group删除组中的指定用户
	 */
	public void deleteGroupUsers(String ids,String groupId);
	
	public void updateUserGroup(String addUserIds, Group group);
	
	/**
	 * 根据用户组id查询该用户组下的角色
	 * @param groupId
	 * @return
	 */
	public String findRoleNameById(Long groupId);
	
	
	/**
	 * 根据用户组id查询该用户组下的所有用户
	 * @param groupId
	 * @return
	 */
	public String findUserNamesById(Long groupId);
}
