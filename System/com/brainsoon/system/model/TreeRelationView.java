package com.brainsoon.system.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.brainsoon.common.po.BaseHibernateObject;

@Entity
@Table(name = "view_tree_relation")
public class TreeRelationView  extends BaseHibernateObject {
	private static final long serialVersionUID = 4858001351954128087L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false) 
	private int id;
	@Column
	private int centerId;
	@Column
    private int relativeId;
    @Column
    private String centerName;
    @Column
    private String centerTypeName;
    @Column
    private String relativeName;
    @Column
    private String relativeType;
    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public int getCenterId() {
		return centerId;
	}

	public void setCenterId(int centerId) {
		this.centerId = centerId;
	}

	public int getRelativeId() {
		return relativeId;
	}

	public void setRelativeId(int relativeId) {
		this.relativeId = relativeId;
	}

	public String getCenterName() {
		return centerName;
	}

	public void setCenterName(String centerName) {
		this.centerName = centerName;
	}

	public String getCenterTypeName() {
		return centerTypeName;
	}

	public void setCenterTypeName(String centerTypeName) {
		this.centerTypeName = centerTypeName;
	}

	public String getRelativeName() {
		return relativeName;
	}

	public void setRelativeName(String relativeName) {
		this.relativeName = relativeName;
	}

	public String getRelativeType() {
		return relativeType;
	}

	public void setRelativeType(String relativeType) {
		this.relativeType = relativeType;
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

}
