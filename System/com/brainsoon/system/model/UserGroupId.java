package com.brainsoon.system.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
@Embeddable
public class UserGroupId implements Serializable {

	@Column(name="user_id")
	private Long userId;
	
	@Column(name="group_id")
	private Long groupId;
	
	public UserGroupId(){
	}
	
	public UserGroupId(Long userId,Long groupId){
		this.userId = userId;
		this.groupId = groupId;
	}
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	
	public int hashCode(){
		return new HashCodeBuilder().append(userId).append(groupId).hashCode();
	}
	
	
	public String toString(){
		return userId + ", " + groupId;
	}
	
	public boolean equals(Object obj){
		if(obj == null){
			return false;
		}
		
		if(!UserGroupId.class.isAssignableFrom(obj.getClass())){
			return false;
		}
		
		UserGroupId o = (UserGroupId)obj;
		
		return new EqualsBuilder().append(userId, o.getUserId()).append(groupId, o.getGroupId()).isEquals();
	}
}
