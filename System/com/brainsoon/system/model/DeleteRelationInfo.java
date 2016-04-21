package com.brainsoon.system.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.brainsoon.common.po.BaseHibernateObject;
@Entity
@Table(name = "delete_relation_info")
public class DeleteRelationInfo extends BaseHibernateObject {

	private static final long serialVersionUID = -1516960289526271752L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
	@Column
	private String objectId1;
	@Column
	private String objectId2;
	@Column(name="create_time")
	private Date createTime;
	

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

	public String getObjectId1() {
		return objectId1;
	}

	public void setObjectId1(String objectId1) {
		this.objectId1 = objectId1;
	}

	public String getObjectId2() {
		return objectId2;
	}

	public void setObjectId2(String objectId2) {
		this.objectId2 = objectId2;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
