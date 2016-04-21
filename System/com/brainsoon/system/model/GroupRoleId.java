package com.brainsoon.system.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class GroupRoleId implements java.io.Serializable {
	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	@Column(name="group_id")
	private Long groupId;
	@Column(name="role_id")
	private Long roleId;
	
	public GroupRoleId(){
	}
	
	public GroupRoleId(Long groupId,Long roleId){
		this.groupId = groupId;
		this.roleId = roleId;
	}
}
