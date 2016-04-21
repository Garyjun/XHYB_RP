package com.brainsoon.system.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
@Embeddable
public class UserRoleId implements java.io.Serializable {

	@Column(name="user_id")
	private Long userId;
	@Column(name="role_id")
	private Long roleId;

	/**
	 * 
	 */
	public UserRoleId() {
		super();
	}

	/**
	 * @param userId
	 * @param roleId
	 */
	public UserRoleId(Long userId, Long roleId) {
		super();
		this.userId = userId;
		this.roleId = roleId;
	}

	/**
	 * @return the userId
	 */
	public Long getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
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

	
	public int hashCode(){
		return new HashCodeBuilder().append(userId).append(roleId).hashCode();
	}
	
	public String toString(){
		return userId + ", " + roleId;
	}
	
	public boolean equals(Object obj){
		if(obj == null){
			return false;
		}
		
		if(!UserRoleId.class.isAssignableFrom(obj.getClass())){
			return false;
		}
		
		UserRoleId o = (UserRoleId)obj;
		
		return new EqualsBuilder().append(userId, o.getUserId()).append(roleId, o.getRoleId()).isEquals();
	}

}
