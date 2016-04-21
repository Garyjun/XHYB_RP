package com.brainsoon.bsrcm.search.po;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.brainsoon.common.po.BaseHibernateObject;

@Entity
@Table(name = "search_keys")
public class SearchKeys extends BaseHibernateObject {

	private static final long serialVersionUID = -2384568126530869332L;
	
	 //主键
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
	private Long id;
	 //名称
    @Column(name = "search_name")
	private String searchName;
	 //索引描述
    @Column(name = "search_desc")
	private String searchDesc;
	 //创建搜索URL
    @Column(name = "search_url")
	private String searchUrl;
	 //创建搜索次数
    @Column(name = "search_num")
	private Long searchNum;
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getSearchName() {
		return searchName;
	}
	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}
	public String getSearchDesc() {
		return searchDesc;
	}
	public void setSearchDesc(String searchDesc) {
		this.searchDesc = searchDesc;
	}
	public String getSearchUrl() {
		return searchUrl;
	}
	public void setSearchUrl(String searchUrl) {
		this.searchUrl = searchUrl;
	}
	public Long getSearchNum() {
		return searchNum;
	}
	public void setSearchNum(Long searchNum) {
		this.searchNum = searchNum;
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
