package com.brainsoon.system.service;

import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.pagination.PageInfo;
import com.brainsoon.common.service.IBaseService;
import com.brainsoon.system.model.Privilege;

public interface IPrivilegeService extends IBaseService{
	/**
	 * 分页查询权限
	 * @param pageInfo
	 * @param privilege
	 * @return
	 */
	public PageResult query(PageInfo pageInfo, Privilege privilege) throws ServiceException;
	/**
	 * 增加权限
	 * @param privilege
	 * @throws ServiceException
	 */
	public void doCreatePrivilege(Privilege privilege)throws ServiceException;
	
	/**修改权限
	 * @param privilege
	 * @throws ServiceException
	 */
	public void doUpdatePrivilege(Privilege privilege)throws ServiceException;
	
	/**
	 * 删除权限
	 * @param id
	 * @throws ServiceException
	 */
	public void doDeletePrivilege(Long id)throws ServiceException;
	
	/**
	 * 批量删除权限
	 * @param ids
	 * @throws ServiceException
	 */
	public void doBatchDeletePrivilege(String ids)throws ServiceException;
	/**
	 * 获取权限url串
	 * @param privilegeId
	 * @return
	 * @throws ServiceException
	 */
	public String getPriviUrls(Long  privilegeId)throws ServiceException;
}
