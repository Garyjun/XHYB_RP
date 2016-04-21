package com.brainsoon.system.model;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.brainsoon.common.po.BaseHibernateObject;
/**
 * Logo实体类
 * @author fengda
 *
 */
@Entity
@Table(name="sys_logo")
public class Logo extends BaseHibernateObject{
	
	//id
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false)
    private Integer id;
	
	//logo的宽度
	@Column
	private Integer width;
	
	//logo的高度
	@Column
	private Integer height;
	
	//logo的状态
	@Column
	private Integer status; //1启用 ， 0 不启用
	
	//创建时间
	@Column
	private Date createTime;
	
	//修改时间
	@Column
	private Date updateTime;
	
	//创建人id
	@Column
	private Long createUserId;
	
	//修改人id
	@Column
	private Long updateUserId;
	
	//logo的名称
	@Column 
	private String logoName;
	@Override
	public String getObjectDescription() {
		return null;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Long getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(Long createUserId) {
		this.createUserId = createUserId;
	}

	public Long getUpdateUserId() {
		return updateUserId;
	}

	public void setUpdateUserId(Long updateUserId) {
		this.updateUserId = updateUserId;
	}

	public String getLogoName() {
		return logoName;
	}

	public void setLogeName(String logoName) {
		this.logoName = logoName;
	}

	@Override
	public String getEntityDescription() {
		return null;
	}

}
