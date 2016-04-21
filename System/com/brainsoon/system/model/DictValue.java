package com.brainsoon.system.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.brainsoon.common.po.BaseHibernateObject;
import com.brainsoon.system.support.SystemConstants;
@Entity
@Table(name = "dict_value")
public class DictValue extends BaseHibernateObject {

	private static final long serialVersionUID = 2983788754877605163L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
	@Column(name = "platformId")
	private int platformId;
	public int getPlatformId() {
		return platformId;
	}

	public void setPlatformId(int platformId) {
		this.platformId = platformId;
	}

	@Column
	private String shortname;
	@Column
    private String name;
    @Column
    private String indexTag;
    @Column
    private String status="1";
    @Column
    private String description;
    @Column
    private Long pid;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getShortname() {
		return shortname;
	}

	public void setShortname(String shortname) {
		this.shortname = shortname;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIndexTag() {
		return indexTag;
	}

	public void setIndexTag(String indexTag) {
		this.indexTag = indexTag;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
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
