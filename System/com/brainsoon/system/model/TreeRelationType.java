package com.brainsoon.system.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.brainsoon.common.po.BaseHibernateObject;
import com.brainsoon.taskprocess.model.TaskProcess;

@Entity
@Table(name = "res_treeRelation_type")
public class TreeRelationType extends BaseHibernateObject{
	private static final long serialVersionUID = 4858001351954128087L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false) 
    private Long id;
    @Column
    private int centerKey;
    @Column
    private int relationKey;
    @Column
    private int centerType;
    @Column
    private int relationType;
   
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getCenterKey() {
		return centerKey;
	}

	public void setCenterKey(int centerKey) {
		this.centerKey = centerKey;
	}

	public int getRelationKey() {
		return relationKey;
	}

	public void setRelationKey(int relationKey) {
		this.relationKey = relationKey;
	}

	public int getCenterType() {
		return centerType;
	}

	public void setCenterType(int centerType) {
		this.centerType = centerType;
	}

	public int getRelationType() {
		return relationType;
	}

	public void setRelationType(int relationType) {
		this.relationType = relationType;
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
