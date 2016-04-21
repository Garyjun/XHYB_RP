package com.brainsoon.system.model;

import java.util.Date;
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

import org.springframework.beans.factory.annotation.Autowired;

import com.brainsoon.common.po.BaseHibernateObject;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.system.service.IGroupService;
@Entity
@Table(name = "sys_group")
public class Group extends BaseHibernateObject {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
	@Column
    private String name;
    @Column(name="created_time")
    private Date createdTime;
    @Column(name="modified_time")
    private Date modifiedTime;    
    
    @OneToMany(fetch=FetchType.LAZY,cascade ={CascadeType.ALL},mappedBy="id.groupId" )
    protected List<UserGroup> users;
    
	@OneToMany(fetch=FetchType.LAZY,cascade ={CascadeType.ALL},mappedBy="id.groupId" )
    protected List<GroupRole> roles;
    
    @Transient
    private String userIds;
    @Transient
    private Long[] roleIds;
    
    private IGroupService groupService(){
    	IGroupService groupService = null;
    	try{
    		groupService = (IGroupService) BeanFactoryUtil.getBean("groupService");
    	}catch(Exception e){
    		logger.debug("bean['groupService']尚未装载到容器中！");
    		e.printStackTrace();
    	}
    	return groupService;
    }
    /**
     * 用户组下的角色名称
     */
    @Transient
    private String findGroupRoles;
    
    public void setFindGroupRoles(String findGroupRoles){
    	this.findGroupRoles = findGroupRoles;
    }
    
    public String getFindGroupRoles(){
    	String roleNames = groupService().findRoleNameById(id);
    	return roleNames;
    }
    
    
    /**
     * 用户组下的所有用户
     */
    @Transient
    private String findGroupUsers;
    
    public void setFindGroupUsers(String findGroupUsers){
    	this.findGroupUsers = findGroupUsers;
    }
    
    public String getFindGroupUsers(){
    	String userNames = groupService().findUserNamesById(id);
    	return userNames;
    }
    
	public String getUserIds() {
		return userIds;
	}

	public void setUserIds(String userIds) {
		this.userIds = userIds;
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
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Date getModifiedTime() {
		return modifiedTime;
	}

	public void setModifiedTime(Date modifiedTime) {
		this.modifiedTime = modifiedTime;
	}

	public List<UserGroup> getUsers() {
		return users;
	}

	public void setUsers(List<UserGroup> users) {
		this.users = users;
	}
    public List<GroupRole> getRoles() {
		return roles;
	}

	public void setRoles(List<GroupRole> roles) {
		this.roles = roles;
	}

	public Long[] getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(Long[] roleIds) {
		this.roleIds = roleIds;
	}
}
