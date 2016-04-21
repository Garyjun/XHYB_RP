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
import javax.persistence.Table;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.brainsoon.common.po.BaseHibernateObject;
@Entity
@Table(name = "sys_privilege")
public class Privilege  extends BaseHibernateObject  {
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
	private Long id;
	@Column(name="privilege_name")
	private String privilegeName;
	@Column(name="display_order")
	private Integer displayOrder;
	@Column  
	private String status="1";
    @ManyToOne(fetch=FetchType.EAGER)   
	@JoinColumn (name= "module_id" )     
	private Module module;
    //对于一对多，因为要在一的这一端维持关系，所以删除了mappedBy配置
	@OneToMany(fetch=FetchType.LAZY,cascade ={CascadeType.ALL},mappedBy="privilegeId" )    
	protected List<PrivilegeUrlMapping> privilegeUrls;
	@Transient
	private String statusDesc="";
	@Transient
	private String urls="";
	@Transient
	private String[] menu;
    @Column
    private int platformId;		//平台ID
	public Privilege(){
		
	}
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public Module getModule() {
		return module;
	}

	public void setModule(Module module) {
		this.module = module;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	@JsonIgnore 
	public List<PrivilegeUrlMapping> getPrivilegeUrls() {
		return privilegeUrls;
	}

	public void setPrivilegeUrls(List<PrivilegeUrlMapping> privilegeUrls) {
		this.privilegeUrls = privilegeUrls;
	}

	public String getPrivilegeName() {
		return privilegeName;
	}

	public void setPrivilegeName(String privilegeName) {
		this.privilegeName = privilegeName;
	}

	public String getStatusDesc() {
		return statusDesc;
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}
	public String getUrls() {
		return urls;
	}
	public void setUrls(String urls) {
		this.urls = urls;
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
	public String[] getMenu() {
		return menu;
	}
	public void setMenu(String[] menu) {
		this.menu = menu;
	}
	public int getPlatformId() {
		return platformId;
	}

	public void setPlatformId(int platformId) {
		this.platformId = platformId;
	}
}
