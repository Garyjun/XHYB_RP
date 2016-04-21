package com.brainsoon.system.dao;

import java.util.List;

import com.brainsoon.common.dao.IBaseDao;
import com.brainsoon.system.model.Privilege;
import com.brainsoon.system.model.Role;

public interface IPrivilegeDao  extends IBaseDao  {
	/**
	 * 获取角色对应的权限
	 * @param role
	 * @return
	 */
    public List<Privilege> getPrivs(Role role);
}
