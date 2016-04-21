package com.brainsoon.system.dao.impl;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import com.brainsoon.common.dao.hibernate.BaseHibernateDao;
import com.brainsoon.system.dao.IPrivilegeUrlDao;
import com.brainsoon.system.model.PrivilegeUrlAndRoleBo;
import com.brainsoon.system.model.PrivilegeUrlMapping;
import com.brainsoon.system.model.Role;
@Repository
public class PrivilegeUrlDao extends BaseHibernateDao implements IPrivilegeUrlDao {

	/**
	 * 获取角色对应的权限url
	 * @param role
	 * @return
	 */
	public List<PrivilegeUrlMapping> getPrivUrls(Role role) {
		StringBuffer hql=new StringBuffer(" from PrivilegeUrlMapping p where p.privilegeId in (").append("\n");
		hql.append(" select rp.id.privilegeId from RolePrivilege rp where rp.id.roleId=").append(role.getId());
		hql.append(" )");
		return this.query(hql.toString());
	}
	
	
	/**
	 * 根据平台id获取角色对应的权限及URL
	 * @param platformId
	 * @return List
	 */
	public List getPrivUrlsAndRoles(String platformId){
		StringBuffer sql = new StringBuffer("");
		sql.append("SELECT t.`id`,");
		sql.append("t.`url` AS url,");
//		sql.append("t.`privilege_id` AS privilegeId,");
		sql.append("GROUP_CONCAT(t2.`role_key`) AS roleKey,");
		sql.append("GROUP_CONCAT(t2.`role_name`) AS roleName");
		sql.append(" FROM `sys_privilege_url_mapping` t LEFT JOIN `sys_role_privilege` t1");
		sql.append(" ON t.`privilege_id`= t1.`privilege_id`");
		sql.append(" AND t.`platformId` = t1.`platformId`");
		sql.append(" LEFT JOIN `sys_role` t2 ON t1.`role_id` = t2.`id`");
		sql.append(" AND t1.`platformId` = t2.`platformId`");
		sql.append(" WHERE t.`platformId` = " + platformId + " GROUP BY t.`url`");
		//List list  = this.getSession().createSQLQuery(sql.toString()).addEntity(PrivilegeUrlMapping.class).list();
		List list  = this.getSession().createSQLQuery(sql.toString()).setResultTransformer(Transformers.aliasToBean(PrivilegeUrlAndRoleBo.class)).list();
		return list;
	}
	

	/**
	 * 根据平台id获取角色对应的权限
	 * @param platformId
	 * @return List
	 */
	public List<PrivilegeUrlMapping> getPrivUrlsByPlatformId(String platformId){
		StringBuffer hql=new StringBuffer(" from PrivilegeUrlMapping p where p.platformId =" + Long.parseLong(platformId));
		List priUrls=this.query(hql.toString());
		
		return priUrls;
	}
	

}
