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

import org.codehaus.jackson.annotate.JsonIgnore;

import com.brainsoon.common.po.BaseHibernateObject;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.system.service.IOrganizationService;
import com.brainsoon.system.service.IUserService;

@Entity
@Table(name = "sys_user")
public class User extends BaseHibernateObject {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name="login_name")
    private String loginName;
    @Column(name="user_name")
    private String userName;
    @Column
    private String password;
    @Column
    private String phone;
    @Column
    private String mobile;
    @Column
    private String email;
    @Column(name="created_by")
    private String createdBy;
    @Column(name="modified_by")
    private String modifiedBy;
    @Column(name="created_time")
    private Date createdTime;
    @Column(name="modified_time")
    private Date modifiedTime;
    @Column
    private int status;
    @Column(name="data_pre_json")
    private String dataPreJson;
    @Column(name="data_pre_range_array")
    private String dataPreRangeArray;    
	@Column
    private String description;
    @Column(name="tenant_id")
    private Long tenantId;
    @Column
    private String free="1";//空闲状态
    @Column
    private String online="0";//在线状态
    @Column
    private int platformId;		//平台ID
	@Column
    private String organization;
    @Column
    private String resType;
    @Transient
    private Long[] resTypeIds;
    @Column(name="private")
    private int isPrivate;
    
    //授权的资源目录
    @Column(name="resource_data_json")
    private String resourceDataJson;

	/**
	 * 用户类型。2:USP上创建的租户管理员，1：USP上创建的管理员，0：加工平台创建的一般用户
	 */
    @Column(name="user_type")
    private String userType = "0";
    @Column(name="relation_id")
    private Long relationId;  
    @Column(name="org_id")
    private String orgId;
    @Column(name="keystone_user_id")
    private String keystoneUserId;
    @Transient
    private Long[] roleIds;
    @Transient
    private Long[] groupIds;
	@Transient
    private String rolesDesc;
    
	
	
	
	
	//组织部门
	@Transient
	private String orgnameByOrgId;
	
	//用户组
	@Transient
	private String useridByGroup;
	
    public void setOrgnameByOrgId(String orgnameByOrgId) {
		this.orgnameByOrgId = orgnameByOrgId;
	}
    
    public String getOrganizeName(){
    	return orgnameByOrgId;
    }
    
    public String getGroupName(){
    	return useridByGroup;
    }
    /**
     * 查询用户所在的组织机构
     * @return
     */
    public String getOrgnameByOrgId(){
    	String name = "";
    	if(orgId!=null && orgId!=""){
	    	String orgid[] = orgId.split(",");
	    	for (String string : orgid) {
	    		long orgId = Long.parseLong(string);
	    		try{
		    		Organization organization = (Organization) getOrganizationService().getByPk(Organization.class, orgId);
		    		name += organization.getName();
		    		name += ",";
	    		}catch(Exception e){
	    			logger.error("根据组织机构ID为"+string+"查询的组织机构已不存在");
	    			e.printStackTrace();
	    		}
			}
    	}
    	if(name.length()>0 && name!=null){
    		name = name.substring(0,name.length()-1);
    	}
    		return name;
    	}
    
    private IOrganizationService getOrganizationService(){
    	IOrganizationService organizationService = null;
    	try{
    		organizationService = (IOrganizationService) BeanFactoryUtil.getBean("organizationService");
    	}catch(Exception e){
    		logger.debug("bean['organizationService']尚未装载到容器中！");
    		e.printStackTrace();
    	}
    	return organizationService;
    }
    
    
    /**
     * 查询当前用户所在的用户组
     * @return
     */
    public void setUseridByGroup(String useridByGroup){
    	this.useridByGroup = useridByGroup;
    }
    public String getUseridByGroup(){
    	String names = getUserService().findidByGroupName(id);
    	return names;
    }	
    
    private IUserService getUserService(){
    	IUserService userService = null;
    	try{
    		userService = (IUserService) BeanFactoryUtil.getBean("userService");
    	}catch(Exception e){
    		logger.debug("bean['UserService']尚未装载到容器中！");
    		e.printStackTrace();
    	}
    	return userService;
    }
    
    
    public String getRolesDesc(){
    	return rolesDesc;
    }
    public void setRolesDesc(String rolesDesc){
    	this.rolesDesc = rolesDesc;
    }
   
    @OneToMany(fetch=FetchType.LAZY,cascade ={CascadeType.ALL},mappedBy="id.userId" )
    protected List<UserRole> roles;
    
    @OneToMany(fetch=FetchType.LAZY,cascade ={CascadeType.ALL},mappedBy="id.userId" )
    protected List<UserGroup> groups;

	
	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
	 * 用户类型。2:USP上创建的租户管理员，1：USP上创建的管理员，0：加工平台创建的一般用户
	 */
    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Long getRelationId() {
        return relationId;
    }

    public void setRelationId(Long relationId) {
        this.relationId = relationId;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }


	public String getKeystoneUserId() {
		return keystoneUserId;
	}

	public void setKeystoneUserId(String keystoneUserId) {
		this.keystoneUserId = keystoneUserId;
	}

	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}
	public String getOnline() {
		return online;
	}

	public void setOnline(String online) {
		this.online = online;
	}
	
	public String getFree() {
		return free;
	}

	public void setFree(String free) {
		this.free = free;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	@JsonIgnore 
	public List<UserRole> getRoles() {
		return roles;
	}

	public void setRoles(List<UserRole> roles) {
		this.roles = roles;
	}
	@JsonIgnore
    public List<UserGroup> getGroups() {
		return groups;
	}
	public void setGroups(List<UserGroup> groups) {
		this.groups = groups;
	}

	public Long[] getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(Long[] roleIds) {
		this.roleIds = roleIds;
	}

    public Long[] getGroupIds() {
		return groupIds;
	}
	public void setGroupIds(Long[] groupIds) {
		this.groupIds = groupIds;
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
    public int getPlatformId() {
		return platformId;
	}
	public void setPlatformId(int platformId) {
		this.platformId = platformId;
	}
    public String getOrganization() {
		return organization;
	}
	public void setOrganization(String organization) {
		this.organization = organization;
	}
	public String getResType() {
		return resType;
	}
	public void setResType(String resType) {
		this.resType = resType;
	}
	public int getIsPrivate() {
		return isPrivate;
	}
	public void setIsPrivate(int isPrivate) {
		this.isPrivate = isPrivate;
	}
    public Long[] getResTypeIds() {
		return resTypeIds;
	}
	public void setResTypeIds(Long[] resTypeIds) {
		this.resTypeIds = resTypeIds;
	}
    public String getDataPreJson() {
		return dataPreJson;
	}
	public void setDataPreJson(String dataPreJson) {
		this.dataPreJson = dataPreJson;
	}
	public String getDataPreRangeArray() {
		return dataPreRangeArray;
	}
	public void setDataPreRangeArray(String dataPreRangeArray) {
		this.dataPreRangeArray = dataPreRangeArray;
	}
	
	/**
	 * 授权的资源目录 
	 * @return
	 */
	public String getResourceDataJson() {
		return resourceDataJson;
	}

	public void setResourceDataJson(String resourceDataJson) {
		this.resourceDataJson = resourceDataJson;
	}
}
