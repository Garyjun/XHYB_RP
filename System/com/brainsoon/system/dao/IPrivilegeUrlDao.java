package com.brainsoon.system.dao;

import java.util.List;

import com.brainsoon.common.dao.IBaseDao;
import com.brainsoon.system.model.Privilege;
import com.brainsoon.system.model.PrivilegeUrlAndRoleBo;
import com.brainsoon.system.model.PrivilegeUrlMapping;
import com.brainsoon.system.model.Role;

public interface IPrivilegeUrlDao  extends IBaseDao  {
	/**
	 * 获取角色可以访问的资源
	 * @param role
	 * @return
	 */
    public List<PrivilegeUrlMapping> getPrivUrls(Role role);
    
    

	/**
	 * 根据平台id获取角色对应的权限
	 * @param platformId
	 * @return List
	 */
	public List<PrivilegeUrlMapping> getPrivUrlsByPlatformId(String platformId);
	
	
	/**
	 * 根据平台id获取角色对应的权限及URL
	 * @param platformId
	 * @return List
	 */
	public List getPrivUrlsAndRoles(String platformId);
	
}
