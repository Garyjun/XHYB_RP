package com.brainsoon.system.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.common.dao.IBaseDao;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.pagination.PageInfo;
import com.brainsoon.common.po.tree.TreeNode;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.common.support.Constants;
import com.brainsoon.common.support.GlobalAppCacheMap;
import com.brainsoon.system.dao.IRoleDao;
import com.brainsoon.system.model.Group;
import com.brainsoon.system.model.Module;
import com.brainsoon.system.model.Privilege;
import com.brainsoon.system.model.Role;
import com.brainsoon.system.model.RolePrivilege;
import com.brainsoon.system.model.RolePrivilegeId;
import com.brainsoon.system.model.User;
import com.brainsoon.system.service.IModuleService;
import com.brainsoon.system.service.IRoleService;
import com.google.gson.Gson;

@Service
public class RoleService extends BaseService implements IRoleService {
	@Autowired
	private IRoleDao roleDao;
	@Autowired
	private IModuleService moduleService;

	/**
	 * 根据用户id获取用户角色
	 * 
	 * @param userId
	 * @return
	 */
	public List<Role> getRolesByUserId(Long userId) {
		List<Role> roles = new ArrayList<Role>();
		StringBuffer hql = new StringBuffer(
				" from Role r  where r.id in ( ").append("\n");
		hql.append(
				" select ur.id.roleId from UserRole ur where ur.id.userId=:userId ")
				.append("\n");
		hql.append(" ) and r.status ='1' ");
		try {
			roles =roleDao.query(hql.toString(), "userId", userId);
			addUserGroupRoles(userId,roles);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());

		}
		return roles;

	}
	
	private void addUserGroupRoles(Long userId, List<Role> list){
		String hql = "from Group where id in(" + 
				"select ug.id.groupId from UserGroup ug where ug.id.userId ="
				+userId+")";
		List<Group> groups = query(hql);
		for(Group group : groups){
			String roleHql = "from Role where id in(" +
					"select gr.id.roleId from GroupRole gr where gr.id.groupId ="
					+ group.getId()+")";
			List<Role> roles = query(roleHql);
			for(Role role : roles){
				if(!list.contains(role)){
					list.add(role);
				}
			}
		}
	}
	 /**
	    * 根据url,获取拥有访问该url权限的角色
	    * @param url
	    * @return
	 */
	@Override
	public List<Role> getRolesByUrl(String url) {
		List<Role> roles = new ArrayList<Role>();
		StringBuffer hql = new StringBuffer(" from Role r where r.id in ( ").append("\n");
		hql.append(" select rp.id.roleId from RolePrivilege rp where rp.id.privilegeId in (").append("\n");
		hql.append(" select pu.privilegeId from PrivilegeUrlMapping pu where pu.url=:url ").append("\n");
		hql.append(" ) ").append("\n");
		hql.append(" )");
		String platformId = GlobalAppCacheMap.getValue(Constants.USER_CHECKED_PLATFORM_ID) == null?"":GlobalAppCacheMap.getValue(Constants.USER_CHECKED_PLATFORM_ID).toString();
		hql.append(" and r.platformId = " + Long.parseLong(platformId));
		logger.debug("query hql **** ");
		logger.debug(hql.toString());
		try {
			roles = roleDao.query(hql.toString(), "url", url);
			if(!hasAdminRoles(roles))
				addAdminRoleToRoles(roles);
				
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());

		}
		return roles;
	}
	
	private boolean hasAdminRoles(List<Role> roles){
		for(Role role : roles){
			if(role.getRoleKey().equals("ROLE_ADMIN"))
				return true;
		}
		return false;
	}
	
	private void addAdminRoleToRoles(List<Role> roles){
		List<Role> admin = roleDao.query("from Role r where r.roleKey = '" + "ROLE_ADMIN" +"'");
		roles.add(admin.get(0));
	}
	/**
	 * 分页查询角色
	 * @param pageInfo
	 * @param role
	 * @return
	 */
	public PageResult queryRoles(PageInfo pageInfo, Role role) throws ServiceException{
		String hql=" from Role r where 1=1 and platformId = " + LoginUserUtil.getLoginUser().getPlatformId();
    	Map<String, Object> params=new HashMap<String, Object>();
    	if(StringUtils.isNotBlank(role.getRoleName())){
    		try {
				String roleName = URLDecoder.decode(role.getRoleName(),"UTF-8");
				hql=hql+" and r.roleName like :roleName ";
	    		params.put("roleName", "%"+roleName+"%");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
    		
    	}
    	String countHql=" select count(*) "+hql;
    	try {
    		baseDao.query4Page(hql, countHql, pageInfo, params);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
    
    	return pageInfo.getPageResult();
	}
	
	/**
	 * 创建角色
	 * @param role
	 */
	public void doCreateRole(Role role) throws ServiceException{
		try {
			baseDao.create(role);
			role.setRoleKey("ROLE_"+role.getId());
			List<RolePrivilege> privis=new ArrayList<RolePrivilege>();
			String[] privilegeIds=role.getPrivilegeIds().split(",");
			for(String priId: privilegeIds){
				RolePrivilegeId id=new RolePrivilegeId(role.getId(), new Long(priId));
				RolePrivilege rolePri=new RolePrivilege(id);
				rolePri.setPlatformId(LoginUserUtil.getLoginUser().getPlatformId());
				privis.add(rolePri);
			}
			role.setPrivileges(privis);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}
	
	/**
	 * 修改角色
	 * @param role
	 */
	public void doUpdateRole(Role role) throws ServiceException{
		try {
			String executeHql=" delete from RolePrivilege rp where rp.id.roleId=:roleId";
	  		HashMap<String, Object> paras=new HashMap<String, Object>();
	  		paras.put("roleId",role.getId());
	  		baseDao.executeUpdate(executeHql, paras);
			List<RolePrivilege> privis=new ArrayList<RolePrivilege>();
			String[] privilegeIds=role.getPrivilegeIds().split(",");
			for(String priId: privilegeIds){
				RolePrivilegeId id=new RolePrivilegeId(role.getId(), new Long(priId));
				RolePrivilege rolePri=new RolePrivilege(id);
				rolePri.setPlatformId(role.getPlatformId());
				privis.add(rolePri);
			}
			role.setPrivileges(privis);
			baseDao.update(role);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}
	
	/**
	 * 删除角色
	 * 
	 */
	public void doDeleteRole(Long id) throws ServiceException{
		try {
			Role role=(Role)getBaseDao().getByPk(Role.class, id);
			baseDao.delete(role);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}
	
	/**
	 * 根据模块id和角色id,获取该权限树的json串
	 * @param moduleId
	 * @param roleId
	 * @return
	 */
	public String getPrivilegeTreeJson(String moduleIdStr,Long roleId) throws ServiceException {
		String jsonStr="";
		try {
			Long moduleId=new Long(moduleIdStr.substring("m_".length()));
			List<TreeNode> tree=new ArrayList<TreeNode>();
			Module module=null;
			if(moduleId>0){
			  module=(Module)baseDao.getByPk(Module.class,moduleId);
			}
			if(module==null||(module.getDir().equals("1")&&module.getChildren().size()>0)){
				String hql="  from Module m where m.parentId=:parentId order by displayOrder  ";
				List<Module> children=baseDao.query(hql, "parentId", moduleId);
				for(Module child:children){
					TreeNode node=new TreeNode();
			    	node.setId("m_"+child.getId().toString());
			    	node.setName(child.getModuleName());
			    	node.setParent(true);
			    	node.setpId("m_"+moduleId);
			    	boolean checked=false;
			    	if(roleId!=-1){
			    		Role role=(Role)baseDao.getByPk(Role.class, roleId);
			    		String roleKeys=moduleService.getModuleRoles(child.getId());
			    		if(roleKeys.indexOf(role.getRoleKey())!=-1){
			    			checked=true;
			    		}
			    	}
			    	node.setChecked(checked);
			    	node.setOpen(false);
			    	tree.add(node);
				}
			}else{
				String priviHql=" from Privilege p  where p.module.id=:moduleId ";
				List<Privilege> privis=baseDao.query(priviHql, "moduleId", moduleId);
				for(Privilege pri:privis){
					TreeNode node=new TreeNode();
					node.setId(pri.getId().toString());
					node.setName(pri.getPrivilegeName());
					node.setOpen(false);
					node.setpId(moduleIdStr);
					boolean checked=false;
					if(roleId!=-1){
						String checkHql=" from RolePrivilege rp where rp.id.roleId=:roleId and rp.id.privilegeId=:privilegeId ";
						Map<String, Object> paras=new HashMap<String, Object>();
						paras.put("roleId", roleId);
						paras.put("privilegeId", pri.getId());
						List checklist=baseDao.query(checkHql, paras);
						if(checklist.size()>0){
							checked=true;
						}
					}
					node.setChecked(checked);
					node.setParent(false);
					tree.add(node);
					
				}
			}
			Gson gson=new Gson();
			jsonStr=gson.toJson(tree);
			 
		}catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
		return jsonStr;
	}
	/**
	 * 根据角色id获取该角色所对应的权限树
	 * @param roleId
	 * @return
	 * @throws ServiceException
	 */
	public List<TreeNode> getAllPriviTree(Long roleId) throws ServiceException{
		List<TreeNode> tree=new ArrayList<TreeNode>();
		TreeNode root=new TreeNode();
		root.setId("m_-1");
		root.setParent(true);
		getPriviTree(root,roleId,tree);
	
		return tree;
	}
	
	/**
	 * 根据角色id获取该角色所对应的权限树(只取拥有权限的节点)
	 * @param roleId
	 * @return
	 * @throws ServiceException
	 */
	public List<TreeNode> getRolePriviTree(Long roleId) throws ServiceException{
		List<TreeNode> tree=getAllPriviTree(roleId);
		List<TreeNode> roleTree=new ArrayList<TreeNode>();
		for(TreeNode node:tree){
			if(node.isChecked()){
				roleTree.add(node);
			}
			
		}
		
		return roleTree;
	}
	
	
	public List<TreeNode> getPriviTree(TreeNode parentNode,Long roleId,List<TreeNode> tree) throws ServiceException{
		try {
			List<TreeNode> children=getPrivilegeTreeByModule(parentNode.getId(),roleId);
			for(TreeNode node: children){
				tree.add(node);
				if(node.isParent()){
					getPriviTree(node,roleId, tree);
				}
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return tree;
		
	}
	
	
	/**
	 * 根据模块id和角色id,获取模块所对应的权限树
	 * @param moduleId
	 * @param roleId
	 * @return
	 */
	public List<TreeNode> getPrivilegeTreeByModule(String moduleIdStr,Long roleId) throws ServiceException {
		List<TreeNode> tree=new ArrayList<TreeNode>();
		try {
			Long moduleId=new Long(moduleIdStr.substring("m_".length()));
			Module module=null;
			if(moduleId>0){
			  module=(Module)baseDao.getByPk(Module.class,moduleId);
			}
			if(module==null||(module.getDir().equals("1")&&module.getChildren().size()>0)){
				String hql="  from Module m where m.parentModule.id=:parentId and m.platformId = " 
						+ LoginUserUtil.getLoginUser().getPlatformId() + "order by displayOrder  ";
				List<Module> children=baseDao.query(hql, "parentId", moduleId);
				for(Module child:children){
					TreeNode node=new TreeNode();
			    	node.setId("m_"+child.getId().toString());
			    	if(child.getModuleName().indexOf("工作流管理")!=-1){
			    		logger.debug("child.getModuleName()******"+child.getModuleName());
			    	}
			    	node.setName(child.getModuleName());
			    	node.setParent(true);
			    	node.setpId("m_"+moduleId);
			    	boolean checked=false;
			    	if(roleId!=-1){
			    		Role role=(Role)baseDao.getByPk(Role.class, roleId);
//			    		String roleKeys=moduleService.getModuleRoles(child.getId());
//			    		if(roleKeys.indexOf(role.getRoleKey())!=-1){
//			    			checked=true;
//			    		}
			    		if(moduleService.hasPrivlge(role, child.getId())){
			    			checked=true;
			    		}
			    	}
			    	node.setChecked(checked);
			    	node.setOpen(false);
			    	tree.add(node);
				}
			}else{
				String priviHql=" from Privilege p  where p.module.id=:moduleId and p.platformId = "
						+ LoginUserUtil.getLoginUser().getPlatformId();
				List<Privilege> privis=baseDao.query(priviHql, "moduleId", moduleId);
				for(Privilege pri:privis){
					TreeNode node=new TreeNode();
					node.setId(pri.getId().toString());
					node.setName(pri.getPrivilegeName());
					node.setOpen(false);
					node.setpId(moduleIdStr);
					boolean checked=false;
					if(roleId!=-1){
						String checkHql=" from RolePrivilege rp where rp.id.roleId=:roleId and rp.id.privilegeId=:privilegeId and rp.platformId = "
								+ LoginUserUtil.getLoginUser().getPlatformId();
						Map<String, Object> paras=new HashMap<String, Object>();
						paras.put("roleId", roleId);
						paras.put("privilegeId", pri.getId());
						List checklist=baseDao.query(checkHql, paras);
						if(checklist.size()>0){
							checked=true;
						}
					}
					node.setChecked(checked);
					node.setParent(false);
					tree.add(node);
					
				}
			}
			 
		}catch (Exception e) {
			logger.debug("err moduleIdStr "+moduleIdStr);
			logger.debug("err roleId "+roleId);
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
		return tree;
	}

	//禁用用户
	public void doDisabled(String[] ids){
		for(String id : ids){
			try {
				Role role = (Role) getByPk(Role.class, Long.parseLong(id));
				role.setStatus("0");
				baseDao.update(role);
			} catch (Exception e) {
				logger.error(e.getMessage());
				throw new ServiceException(e.getMessage());
			}
		}
	}
	
	//启用用户
	public void doEnabled(String[] ids){
		for(String id : ids){
			try {
				Role role = (Role) getByPk(Role.class, Long.parseLong(id));
				role.setStatus("1");
				baseDao.update(role);
			} catch (Exception e) {
				logger.error(e.getMessage());
				throw new ServiceException(e.getMessage());
			}
		}
	}
}
