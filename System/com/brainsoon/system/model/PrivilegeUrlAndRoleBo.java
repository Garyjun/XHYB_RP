package com.brainsoon.system.model;


public class PrivilegeUrlAndRoleBo{

	private Integer id;
	private Long privilegeId;
	private String url;
	private String roleKey;
	private String roleName;
	
	public PrivilegeUrlAndRoleBo() {
		super();
	}
	
	public PrivilegeUrlAndRoleBo(Integer id,String url, Long privilegeId,
			String roleKey, String roleName) {
		super();
		this.id = id;
		this.url = url;
		this.privilegeId = privilegeId;
		this.roleKey = roleKey;
		this.roleName = roleName;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Long getPrivilegeId() {
		return privilegeId;
	}
	public void setPrivilegeId(Long privilegeId) {
		this.privilegeId = privilegeId;
	}
	public String getRoleKey() {
		return roleKey;
	}
	public void setRoleKey(String roleKey) {
		this.roleKey = roleKey;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	
	
	
	
}
