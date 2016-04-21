package com.brainsoon.system.model;


import javax.persistence.Column;
import javax.persistence.Entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import com.brainsoon.common.po.BaseHibernateObject;
@Entity
@Table(name = "sys_ResMetadata_Definition_Group")
public class MetadataDefinitionGroup extends BaseHibernateObject {
	private static final long serialVersionUID = 4858001351954128087L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column
    private Long sysResMetadataTypeId;
    @Column
    private String fieldName;
    @Column
    private String fieldZhName;
    @Column
    private int parentId;
    @Column
	private int platformId;
    
   
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSysResMetadataTypeId() {
		return sysResMetadataTypeId;
	}

	public void setSysResMetadataTypeId(Long sysResMetadataTypeId) {
		this.sysResMetadataTypeId = sysResMetadataTypeId;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldZhName() {
		return fieldZhName;
	}

	public void setFieldZhName(String fieldZhName) {
		this.fieldZhName = fieldZhName;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public int getPlatformId() {
		return platformId;
	}

	public void setPlatformId(int platformId) {
		this.platformId = platformId;
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
