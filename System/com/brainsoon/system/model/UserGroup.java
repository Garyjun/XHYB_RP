package com.brainsoon.system.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.brainsoon.common.po.BaseHibernateObject;
@Entity
@Table(name = "sys_user_group")
public class UserGroup extends BaseHibernateObject {
	
	public UserGroupId getId() {
		return id;
	}

	public void setId(UserGroupId id) {
		this.id = id;
	}

	@Id
	private UserGroupId id;
	
	public UserGroup(){
	}
	
	public UserGroup(UserGroupId id){
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
