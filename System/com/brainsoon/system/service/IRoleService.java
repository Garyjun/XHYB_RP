package com.brainsoon.system.service;

import java.util.List;

import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.pagination.PageInfo;
import com.brainsoon.common.po.tree.TreeNode;
import com.brainsoon.common.service.IBaseService;
import com.brainsoon.system.model.Role;

public interface IRoleService extends IBaseService{
   /**
    * 根据用户id获取用户角色
    * @param userId
    * @return
    */
   public List<Role> getRolesByUserId(Long userId);
   /**
    * 根据url,获取拥有访问该url权限的角色
    * @param url
    * @return
    */
   public List<Role> getRolesByUrl(String url);
   
   /**
	 * 分页查询角色
	 * @param pageInfo
	 * @param role
	 * @return
	 */
	public PageResult queryRoles(PageInfo pageInfo, Role role) throws ServiceException;
	
	/**
	 * 创建角色
	 * @param role
	 */
	public void doCreateRole(Role role) throws ServiceException;
	
	/**
	 * 修改角色
	 * @param role
	 */
	public void doUpdateRole(Role role) throws ServiceException;
	
	/**
	 * 根据模块id和角色id,获取该权限树的json串
	 * @param moduleIdStr
	 * @param roleId
	 * @return
	 */
	public String getPrivilegeTreeJson(String moduleIdStr,Long roleId) throws ServiceException;
	/**
	 * 根据角色id获取该角色所对应的权限树
	 * @param roleId
	 * @return
	 * @throws ServiceException
	 */
	public List<TreeNode> getAllPriviTree(Long roleId) throws ServiceException;
	
	/**
	 * 根据角色id获取该角色所对应的权限树(只取拥有权限的节点)
	 * @param roleId
	 * @return
	 * @throws ServiceException
	 */
	public List<TreeNode> getRolePriviTree(Long roleId) throws ServiceException;
	
	/**
	 * 禁用角色
	 * @param userId
	 */
	public void doDisabled(String[] ids);
	/**
	 * 启用角色
	 * @param userId
	 */
	public void doEnabled(String[] ids);	
	
	
}
