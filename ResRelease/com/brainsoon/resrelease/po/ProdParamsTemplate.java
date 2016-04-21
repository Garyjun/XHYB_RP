package com.brainsoon.resrelease.po;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.brainsoon.common.po.BaseHibernateObject;
import com.brainsoon.common.support.GlobalDataCacheMap;
import com.brainsoon.common.support.SysResTypeCacheMap;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.system.model.User;
import com.brainsoon.system.service.IDictNameService;
@Entity
@Table(name = "order_params_template")
public class ProdParamsTemplate extends BaseHibernateObject {

	private static final long serialVersionUID = -5335831794602243464L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
	@Column
	private String name;
	@Column
	private String type;
	@Column
	private String status;
	@Column
	private String remark;
	@Column (name = "create_time")
	private Date creatTime;
	@ManyToOne(targetEntity=User.class)
	@Fetch(FetchMode.JOIN)
	@JoinColumn(name="create_user",updatable=false)
	private User createUser;
	@Column (name = "params_json")
	private String paramsJson;
	@Column
	private String supplier;
	@Column
	private String metaInfo;
	@Column
	private int platformId;
	@Column
	private String metaDatasCode;
	@Column
	private String url;
	@Column
	private String publishType;
	@Column
	private String posttype;
	@Transient
	private String typeDesc;
	@Transient
	private String posttypeDesc;
	
	public String getTypeDesc() {
		String types[]=type.split(",");
		String ty="";
		for (int i = 0; i < types.length; i++) {
			ty +=(String) SysResTypeCacheMap.getValue(types[i])+",";
		}
		ty = ty.substring(0, ty.length()-1);
		return ty;
	}
	
	public String getPosttypeDesc() throws Exception{
		/*IDictNameService dictNameService = (IDictNameService)BeanFactoryUtil.getBean("dictNameService");
		String map = dictNameService.getDictMapByName("发布途径").get(getPosttype());*/
		String map =GlobalDataCacheMap.getNameValueWithNameByKeyAndChildKey("XqdZtk", getPosttype());
		return map;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getCreatTime() {
		return creatTime;
	}

	public void setCreatTime(Date creatTime) {
		this.creatTime = creatTime;
	}

	public User getCreateUser() {
		return createUser;
	}

	public void setCreateUser(User createUser) {
		this.createUser = createUser;
	}

	public String getParamsJson() {
		return paramsJson;
	}

	public void setParamsJson(String paramsJson) {
		this.paramsJson = paramsJson;
	}
	
	public int getPlatformId() {
		return platformId;
	}

	public void setPlatformId(int platformId) {
		this.platformId = platformId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
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
	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public String getMetaInfo() {
		return metaInfo;
	}

	public void setMetaInfo(String metaInfo) {
		this.metaInfo = metaInfo;
	}

	public String getMetaDatasCode() {
		return metaDatasCode;
	}

	public void setMetaDatasCode(String metaDatasCode) {
		this.metaDatasCode = metaDatasCode;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPublishType() {
		return publishType;
	}

	public void setPublishType(String publishType) {
		this.publishType = publishType;
	}

	public String getPosttype() {
		return posttype;
	}

	public void setPosttype(String posttype) {
		this.posttype = posttype;
	}
	

}
