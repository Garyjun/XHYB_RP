package com.brainsoon.system.service;

import java.util.List;
import java.util.Map;

import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.pagination.PageInfo;
import com.brainsoon.common.service.IBaseService;
import com.brainsoon.system.model.Module;
import com.brainsoon.system.model.Role;

public interface IModuleService extends IBaseService {
	/**
	 * 根据父id,获取子模块
	 * 
	 * @param parentId
	 * @return
	 * @throws ServiceException
	 */
	public List<Module> getModulesByParentId(Long parentId) throws ServiceException;

	/**
	 * 分页查询模块
	 * 
	 * @param pageInfo
	 * @param module
	 * @return
	 */
	public PageResult query(PageInfo pageInfo, Module module) throws ServiceException;

	/**
	 * 根据父id,获取子模块选项
	 * 
	 * @param parentId
	 * @return
	 * @throws ServiceException
	 */
	public List<Module> getModulesOptions(Long parentId) throws ServiceException;

	/**
	 * 获取所有模块选项
	 * 
	 * @return
	 */
	public List<Module> getAllModulesOptions() throws ServiceException;

	/**
	 * 根据模块id,获取模块所属角色
	 * 
	 * @param moduleId
	 * @return
	 * @throws ServiceException
	 */

	public String getModuleRoles(Long moduleId) throws ServiceException;

	/**
	 * 根据模块id，获取该模块下所有的操作类型（SysOperateType）
	 * 
	 * @param moduleId
	 * @return JSON 串
	 */
	public String getOperateTypeJsonByModuleId(Long moduleId);
	
	public Map<Long,String> getModuleList(int platformId);
	
	public boolean hasPrivlge(Role role,Long moudleId);
}
