package com.brainsoon.system.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.brainsoon.common.po.BaseHibernateObject;
import com.brainsoon.system.support.SystemConstants.Status;

@Entity
@Table(name = "sys_role")
public class Role extends BaseHibernateObject {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
	private Long id;
	@Column(name="role_name")
	private String roleName;
	@Column(name="role_key")
	private String roleKey;
	@Column(length=1000)
	private String description;
	@Column
	private String status="1";
	@OneToMany(fetch=FetchType.LAZY,cascade ={CascadeType.ALL},mappedBy="id.roleId" )
	protected List<RolePrivilege> privileges;
	@Transient
	private String privilegeIds;
	@Transient
	private String statusDesc;
	@Column(name="res_ids")
	private String resIds;
	@Column(name="res_codes")
	private String resCodes;
	@Column(name="res_types")
	private String resTypes;
	@Column(name="private")
	private int isPrivate;
	@Column
    private int platformId;		//平台ID	
	@Transient
	private String[] resTypesArray;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleKey() {
		return roleKey;
	}

	public void setRoleKey(String roleKey) {
		this.roleKey = roleKey;
	}

	public String getPrivilegeIds() {
		return privilegeIds;
	}

	public void setPrivilegeIds(String privilegeIds) {
		this.privilegeIds = privilegeIds;
	}
	@JsonIgnore 
	public List<RolePrivilege> getPrivileges() {
		return privileges;
	}

	public void setPrivileges(List<RolePrivilege> privileges) {
		this.privileges = privileges;
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
	public String getStatusDesc() {
		return Status.getValueByKey(getStatus());
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}

	public String getResIds() {
		return resIds;
	}

	public void setResIds(String resIds) {
		this.resIds = resIds;
	}

	public String getResCodes() {
		return resCodes;
	}

	public void setResCodes(String resCodes) {
		this.resCodes = resCodes;
	}

	public String getResTypes() {
		return resTypes;
	}

	public void setResTypes(String resTypes) {
		this.resTypes = resTypes;
	}
	@JsonIgnore 
	public String[] getResTypesArray() {
		return resTypesArray;
	}

	public void setResTypesArray(String[] resTypesArray) {
		this.resTypesArray = resTypesArray;
	}
	public int getPlatformId() {
		return platformId;
	}

	public void setPlatformId(int platformId) {
		this.platformId = platformId;
	}
    public int getIsPrivate() {
		return isPrivate;
	}

	public void setIsPrivate(int isPrivate) {
		this.isPrivate = isPrivate;
	}
}
