package com.brainsoon.system.model;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.brainsoon.common.po.BaseHibernateObject;

@Entity
@Table(name = "sys_user_role")
public class UserRole extends BaseHibernateObject {
	@Id
	private UserRoleId id;
    @Column
    private int platformId;		//平台ID	
	
	public int getPlatformId() {
		return platformId;
	}
	public void setPlatformId(int platformId) {
		this.platformId = platformId;
	}
	public UserRole() {
		super();
	}
	public UserRole(UserRoleId id) {
		this.id=id;
	}
	public UserRoleId getId() {
		return id;
	}
	public void setId(UserRoleId id) {
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
