package com.brainsoon.system.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.brainsoon.appframe.query.Operator;
import com.brainsoon.appframe.query.QueryConditionItem;
import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.common.exception.DaoException;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.pagination.PageInfo;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.common.support.Constants;
import com.brainsoon.common.support.GlobalAppCacheMap;
import com.brainsoon.system.model.Module;
import com.brainsoon.system.model.Privilege;
import com.brainsoon.system.model.Role;
import com.brainsoon.system.model.RolePrivilege;
import com.brainsoon.system.model.log.SysOperateType;
import com.brainsoon.system.service.IModuleService;

@Service
public class ModuleService extends BaseService implements IModuleService {
	/**
	 * 根据父id,获取子模块
	 * 
	 * @param parentId
	 * @return
	 * @throws ServiceException
	 */
	public List<Module> getModulesByParentId(Long parentId)
			throws ServiceException {
		String hql = " from Module m where m.parentModule.id=:parentId and m.status=1 and platformId = 1 order by displayOrder  ";
		List<Module> modules = new ArrayList<Module>();
		List<Module> showModules = new ArrayList<Module>();
		try {
			modules = baseDao.query(hql, "parentId", parentId);
			for (Module m : modules) {
				String roles = "";
				if (m.getDir().equals("1")) {
					List<Module> newChildren = new ArrayList<Module>();
					for (Module child : m.getChildren()) {
						child.setRoles(getModuleRoles(child.getId()));
						roles = roles + child.getRoles() + ",";
						newChildren.add(child);
					}
					m.setChildren(newChildren);
				} else {
					roles = getModuleRoles(m.getId());
				}
				// if (roles.length() > 1&&roles.indexOf(",")!=-1) {
				// roles = roles.substring(0, roles.length() - 1);
				// }
				m.setRoles(roles);
				showModules.add(m);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
		return showModules;
	}

	/**
	 * 根据父id,获取子模块选项
	 * 
	 * @param parentId
	 * @return
	 * @throws ServiceException
	 */
	public List<Module> getModulesOptions(Long parentId)
			throws ServiceException {
		String hql = " select new com.brainsoon.system.model.Module(m.id,m.moduleName) from Module m where m.parentModule.id = "
				+ parentId
				+ " and platformId = "
				+ LoginUserUtil.getLoginUser().getPlatformId()
				+ " order by displayOrder  ";
		List<Module> modules = new ArrayList<Module>();
		try {
			modules = baseDao.query(hql);

		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
		return modules;
	}

	/**
	 * 获取所有模块选项
	 * 
	 * @return
	 */
	public List<Module> getAllModulesOptions() throws ServiceException {
		String hql = " select new com.brainsoon.system.model.Module(m.id,m.moduleName) from Module m where m.id!=-1 and platformId= "
				+ LoginUserUtil.getLoginUser().getPlatformId()
				+ " order  by m.moduleName,displayOrder  ";
		List<Module> modules = new ArrayList<Module>();
		try {
			modules = baseDao.query(hql);

		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
		return modules;
	}

	/**
	 * 根据模块id,获取模块所属角色
	 * 
	 * @param moduleId
	 * @return
	 * @throws ServiceException
	 */

	public String getModuleRoles(Long moduleId) throws ServiceException {
		Module module = (Module) baseDao.getByPk(Module.class, moduleId);
		String moduleIds = "";
		if (module.getDir().equals("1") && module.getChildren().size() > 0) {
			String queryChildHql = "from Module m where m.xpath like '"
					+ module.getXpath() + ",%'";
			List<Module> children = baseDao.query(queryChildHql);
			for (Module m : children) {
				moduleIds = moduleIds + m.getId() + ",";
			}
			if (moduleIds.length() > 1) {
				moduleIds = moduleIds.substring(0, moduleIds.length() - 1);
			}

		} else {
			moduleIds = moduleId.toString();
		}
		String roles = "";
		StringBuffer hql = new StringBuffer(
				" select distinct r.roleKey from Role r where r.id in (")
				.append("\n");
		hql.append(
				" select rp.id.roleId from RolePrivilege rp where rp.id.privilegeId in (")
				.append("\n");
		hql.append(" select p.id from Privilege p where p.module.id in (")
				.append(moduleIds).append(")\n");
		hql.append(")\n");
		hql.append(")\n");
		try {
			List<String> list = baseDao.query(hql.toString());
			for (String roleKey : list) {
				roles = roles + roleKey + ",";
			}
			if (roles.indexOf("ROLE_ADMIN") == -1)
				roles += "ROLE_ADMIN,";
			if (roles.length() > 1) {
				roles = roles.substring(0, roles.length() - 1);
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}

		return roles;

	}

	/**
	 * 
	 */
	public boolean hasPrivlge(Role role, Long moudleId) {
		String hql = "from Privilege p where p.module.id = " + moudleId;
		try {
			List<Privilege> list = baseDao.query(hql);
			for (Privilege p : list) {
				if (roleHasPrivilege(p, role)) {
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean roleHasPrivilege(Privilege p, Role r) {
		for (RolePrivilege rp : r.getPrivileges()) {
			if (rp.getId().getPrivilegeId() == p.getId())
				return true;
		}
		return false;
	}

	/**
	 * 分页查询模块
	 * 
	 * @param pageInfo
	 * @param module
	 * @return
	 */
	public PageResult query(PageInfo pageInfo, Module module)
			throws ServiceException {
		String hql = " from Module m where m.id!=-1 and platformId = "
				+ LoginUserUtil.getLoginUser().getPlatformId();
		Map<String, Object> params = new HashMap<String, Object>();
		if (StringUtils.isNotBlank(module.getModuleName())) {
			hql = hql + " and m.moduleName like :moduleName";
			try {
				String moduleName = URLDecoder.decode(module.getModuleName(),
						"UTF-8");
				params.put("moduleName", "%" + moduleName + "%");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

		}
		hql = hql + " order by id desc ";
		try {
			baseDao.query4Page(hql, pageInfo, params);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}

		return pageInfo.getPageResult();
	}

	public void deleteModule(Long id) throws ServiceException {
		try {
			baseDao.delete(Module.class, id);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public String getOperateTypeJsonByModuleId(Long moduleId) {
		StringBuffer json = new StringBuffer();
		// 模块操作类型
		String queryHql = "from SysOperateType p where p.module.id=" + moduleId;
		List<SysOperateType> sysOperateTypes = getBaseDao().query(queryHql);

		for (SysOperateType op : sysOperateTypes) {
			json.append("{id:").append(op.getId()).append(",operateKey:'")
					.append(op.getOperateKey()).append("'")
					.append(",operateName:'").append(op.getOperateName())
					.append("'").append(",moduleId:")
					.append(op.getModule().getId());
			json.append("},");
		}
		int jsonLen = json.length();
		if (jsonLen > 0) {
			json = json.deleteCharAt(jsonLen - 1);
		}
		logger.debug("[" + json.toString() + "]");
		return "[" + json.toString() + "]";
	}

	@Override
	public Map<Long, String> getModuleList(int platformId) {
		if (logger.isDebugEnabled()) {
			logger.debug("进入ModuleService.getModuleList()...");
		}
		Map<Long, String> moduleMap = new LinkedHashMap<Long, String>();
		try {
			String hql = "from Module where parentModule.id != -1  and platformId ="
					+ platformId + " order by parentModule.moduleName asc";
			List<Module> moduleList = query(hql);
			if (moduleList != null || moduleList.size() != 0) {
				for (Module module : moduleList) {
					Long id = module.getId();
					String name = module.getParentModule().getModuleName()
							+ "-->" + module.getModuleName();
					if (null == id || null == name) {
						continue;
					}
					if (id != 53 && id != 66 && id != 150 && id != 153
							&& id != 59 && id != 58) {
						moduleMap.put(id, name);
					}
				}
			}

		} catch (DaoException e) {
			if (logger.isErrorEnabled()) {
				logger.error("查询权限模块出现异常", e);
			}
			throw new ServiceException("查询权限模块出现异常");
		}
		return moduleMap;
	}

}
