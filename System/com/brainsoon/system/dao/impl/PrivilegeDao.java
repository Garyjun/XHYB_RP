package com.brainsoon.system.dao.impl;
import java.util.List;

import org.springframework.stereotype.Repository;
import com.brainsoon.common.dao.hibernate.BaseHibernateDao;
import com.brainsoon.system.dao.IPrivilegeDao;
import com.brainsoon.system.model.Privilege;
import com.brainsoon.system.model.Role;
@Repository
public class PrivilegeDao extends BaseHibernateDao implements IPrivilegeDao {

	/**
	 * 获取角色对应的权限
	 * @param role
	 * @return
	 */
	public List<Privilege> getPrivs(Role role) {
		StringBuffer hql=new StringBuffer(" from Privilege p where p where p.id in (").append("\n");
		hql.append(" select ur.id. from UserRole ur where ur.id.roleId=").append(role.getId());
		
		return null;
	}
	

}
