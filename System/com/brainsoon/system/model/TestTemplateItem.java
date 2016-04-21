package com.brainsoon.system.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.brainsoon.common.po.BaseHibernateObject;

@Entity
@Table(name = "t_templateitem")
public class TestTemplateItem  extends BaseHibernateObject {
	private static final long serialVersionUID = 4858001351954128087L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private int id;
    @ManyToOne(targetEntity=TestTemplate.class,cascade=CascadeType.MERGE,fetch=FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name="pid")
    private TestTemplate testTemplate;
    @Column
    private String testTypeKey;
    @Column
    private String testType;
    @Column
    private int count;
    @Column
	private int platformId;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public TestTemplate getTestTemplate() {
		return testTemplate;
	}
	public void setTestTemplate(TestTemplate testTemplate) {
		this.testTemplate = testTemplate;
	}
	public String getTestTypeKey() {
		return testTypeKey;
	}
	public void setTestTypeKey(String testTypeKey) {
		this.testTypeKey = testTypeKey;
	}
	public String getTestType() {
		return testType;
	}
	public void setTestType(String testType) {
		this.testType = testType;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
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
