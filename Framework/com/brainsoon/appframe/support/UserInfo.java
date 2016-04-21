package com.brainsoon.appframe.support;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.brainsoon.semantic.schema.ontology.MetadataDefinition;



public class UserInfo implements Serializable{
	
	private static final long serialVersionUID = -6354013732451218874L;
	
	/**
	 * 用户ID
	 */
	private Long userId;
	private String username;
	private String password;
    private Date loginTime;
    private String loginIp;
    private boolean enabled;
    private Date passwordLastestModifiedTime;
    private String name;//登录姓名
    private String organizationName;//组织机构名称
    private int platformId;
    private boolean isAdmin;
    private int isPrivate;
    private String orgId;
    private String dataPreRangeArray;   
    
    public String getDataPreRangeArray() {
		return dataPreRangeArray;
	}
	public void setDataPreRangeArray(String dataPreRangeArray) {
		this.dataPreRangeArray = dataPreRangeArray;
	}
	public int getIsPrivate() {
		return isPrivate;
	}
	public void setIsPrivate(int isPrivate) {
		this.isPrivate = isPrivate;
	}
	private String deptUserIds;
    public String getDeptUserIds() {
		return deptUserIds;
	}
	public void setDeptUserIds(String deptUserIds) {
		this.deptUserIds = deptUserIds;
	}
	private Map<String,String> resTypes;
	private List<MetadataDefinition> metadataList;
    
	public Map<String, String> getResTypes() {
		return resTypes;
	}
	public void setResTypes(Map<String, String> resTypes) {
		this.resTypes = resTypes;
	}
	
	public boolean isAdmin() {
		if(name.equals("admin"))
			return true;
		else
			return false;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Date getLoginTime() {
		return loginTime;
	}
	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}
	public String getLoginIp() {
		return loginIp;
	}
	public void setLoginIp(String loginIp) {
		this.loginIp = loginIp;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public Date getPasswordLastestModifiedTime() {
		return passwordLastestModifiedTime;
	}
	public void setPasswordLastestModifiedTime(Date passwordLastestModifiedTime) {
		this.passwordLastestModifiedTime = passwordLastestModifiedTime;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOrganizationName() {
		return organizationName;
	}
	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}
	public int getPlatformId() {
		return platformId;
	}
	public void setPlatformId(int platformId) {
		this.platformId = platformId;
	}
    public List<MetadataDefinition> getMetadataList() {
		return metadataList;
	}
	public void setMetadataList(List<MetadataDefinition> metadataList) {
		this.metadataList = metadataList;
	}
	
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	@Override
	public String toString() {
		return "UserInfo ["
				+ (userId != null ? "userId=" + userId + ", " : "")
				+ (username != null ? "username=" + username + ", " : "")
				+ (password != null ? "password=" + password + ", " : "")
				+ (loginTime != null ? "loginTime=" + loginTime + ", " : "")
				+ (loginIp != null ? "loginIp=" + loginIp + ", " : "")
				+ "enabled="
				+ enabled
				+ ", "
				+ (passwordLastestModifiedTime != null ? "passwordLastestModifiedTime="
						+ passwordLastestModifiedTime + ", "
						: "")
				+ (name != null ? "name=" + name + ", " : "")
				+ (organizationName != null ? "organizationName="
						+ organizationName + ", " : "")
				+ "platformId="
				+ platformId
				+ ", isAdmin="
				+ isAdmin
				+ ", isPrivate="
				+ isPrivate
				+ ", "
				+ (orgId != null ? "orgId=" + orgId + ", " : "")
				+ (dataPreRangeArray != null ? "dataPreRangeArray="
						+ dataPreRangeArray + ", " : "")
				+ (deptUserIds != null ? "deptUserIds=" + deptUserIds + ", "
						: "")
				+ (resTypes != null ? "resTypes=" + resTypes + ", " : "")
				+ (metadataList != null ? "metadataList=" + metadataList : "")
				+ "]";
	}
	
}
