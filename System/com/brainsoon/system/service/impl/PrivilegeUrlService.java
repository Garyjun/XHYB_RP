package com.brainsoon.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.brainsoon.common.dao.IBaseDao;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.common.support.Constants;
import com.brainsoon.common.support.GlobalAppCacheMap;
import com.brainsoon.system.dao.IPrivilegeUrlDao;
import com.brainsoon.system.model.PrivilegeUrlMapping;
import com.brainsoon.system.model.Role;
import com.brainsoon.system.service.IPrivilegeUrlService;
@Service
public class PrivilegeUrlService extends BaseService implements IPrivilegeUrlService{
	@Autowired
	private IPrivilegeUrlDao privilegeUrlDao;
	

	@Override
	/**
	 *  获取角色对应的权限url
	 * @param role
	 * @return
	 */
	public List<PrivilegeUrlMapping> getPrivUrls(Role role) {
		// TODO Auto-generated method stub
		return privilegeUrlDao.getPrivUrls(role);
	}
	
	

	/**
	 * 根据平台id获取角色对应的权限
	 * @param platformId
	 * @return List
	 */
	public List<PrivilegeUrlMapping> getPrivUrlsByPlatformId(String platformId){
		return privilegeUrlDao.getPrivUrlsByPlatformId(platformId);
	}
	
	
	/**
	 * 根据平台id获取角色对应的权限及URL
	 * @param platformId
	 * @return List
	 */
	public List getPrivUrlsAndRoles(String platformId){
		return privilegeUrlDao.getPrivUrlsAndRoles(platformId);
	}
	
}
