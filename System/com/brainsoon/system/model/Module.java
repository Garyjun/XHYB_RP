package com.brainsoon.system.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Where;

import com.brainsoon.common.po.BaseHibernateObject;
@Entity
@Table(name = "sys_module")
public class Module extends BaseHibernateObject  {
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
	private Long id;
	@Column(name="module_name")
	private String moduleName;
	@Column(name="display_order")
	private Integer displayOrder;
	@Column
	private String dir;//是否是目录节点,1表示是，0表示非
	@OneToMany(targetEntity = Module.class, cascade = { CascadeType.ALL }, mappedBy = "parentModule")
	@Where(clause="status='1'")
	@OrderBy(value = "displayOrder ASC") 
	protected List<Module> children;
	@Column(name="url")
	private String url;//模块url
	@Transient
	private String roles;
	@Transient
	private String parentName;
	@Transient
	private String fullName;
	@ManyToOne(fetch = FetchType.EAGER,optional=true)  
    @JoinColumn(name = "parent_id") 
	private Module parentModule;
	@Column
	private String status="1";
	@Transient
	private String[] lastMenu;//上级菜单
	@Column(name="xpath")
	private String xpath;//模块路径
	@Column(name="css")
	private String css="fa fa-tasks";//按钮样式
    @Column
    private int platformId;		//平台ID
	public Module(){
		
	}
	public Module(Long id,String moduleName){
		this.id=id;
		this.moduleName=moduleName;
	}
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}


	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
	@JsonIgnore 
	public List<Module> getChildren() {
		return children;
	}

	public void setChildren(List<Module> children) {
		this.children = children;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}
	public String getParentName() {
		return parentName;
	}
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public Module getParentModule() {
		return parentModule;
	}
	public void setParentModule(Module parentModule) {
		this.parentModule = parentModule;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String[] getLastMenu() {
		return lastMenu;
	}
	public void setLastMenu(String[] lastMenu) {
		this.lastMenu = lastMenu;
	}
	public String getXpath() {
		return xpath;
	}
	public void setXpath(String xpath) {
		this.xpath = xpath;
	}
	public String getCss() {
		return css;
	}
	public void setCss(String css) {
		this.css = css;
	}
	public int getPlatformId() {
		return platformId;
	}
	public void setPlatformId(int platformId) {
		this.platformId = platformId;
	}
}
