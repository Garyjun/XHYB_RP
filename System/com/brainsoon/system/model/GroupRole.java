package com.brainsoon.system.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.brainsoon.common.po.BaseHibernateObject;
@Entity
@Table(name = "sys_group_role")
public class GroupRole extends BaseHibernateObject {

	@Id
	private GroupRoleId id;
	
	public GroupRoleId getId() {
		return id;
	}

	public void setId(GroupRoleId id) {
		this.id = id;
	}

	public GroupRole(){
	}
	
	public GroupRole(GroupRoleId id){
		this.id = id;
	}
	@Override
	public String getObjectDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEntityDescription() {
		// TODO Auto-generated method stub
		return null;
	}

}
