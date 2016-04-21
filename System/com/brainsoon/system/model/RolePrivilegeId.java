package com.brainsoon.system.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Embeddable
public class RolePrivilegeId  implements java.io.Serializable {
	@Column(name="role_id")
	private Long roleId;
	@Column(name="privilege_id")
	private Long privilegeId;

	/**
	 * 
	 */
	public RolePrivilegeId() {
		super();
	}

	/**
	 * @param roleId
	 * @param privilegeId
	 */
	public RolePrivilegeId(Long roleId, Long privilegeId) {
		super();
		this.roleId = roleId;
		this.privilegeId = privilegeId;
	}

	/**
	 * @return the roleId
	 */
	public Long getRoleId() {
		return roleId;
	}

	/**
	 * @param roleId the roleId to set
	 */
	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	/**
	 * @return the privilegeId
	 */
	public Long getPrivilegeId() {
		return privilegeId;
	}

	/**
	 * @param privilegeId the privilegeId to set
	 */
	public void setPrivilegeId(Long privilegeId) {
		this.privilegeId = privilegeId;
	}

	public int hashCode(){
		return new HashCodeBuilder().append(privilegeId).append(roleId).hashCode();
	}
	
	public String toString(){
		return privilegeId + ", " + roleId;
	}
	
	public boolean equals(Object obj){
		if(obj == null){
			return false;
		}
		
		if(!RolePrivilege.class.isAssignableFrom(obj.getClass())){
			return false;
		}
		
		RolePrivilegeId o = (RolePrivilegeId)obj;
		
		return new EqualsBuilder().append(privilegeId, o.getPrivilegeId()).append(roleId, o.getRoleId()).isEquals();
	}


}
