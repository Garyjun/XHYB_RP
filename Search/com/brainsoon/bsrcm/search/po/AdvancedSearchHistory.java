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

/**
 * @ClassName: AdvancedSearchSql
 * @Description: TODO
 * @author xiehewei
 * @date 2015年5月11日 上午11:43:37
 *
 */
@Entity
@Table(name = "advanced_search_history")
public class AdvancedSearchHistory extends BaseHibernateObject implements
		Serializable {

	private static final long serialVersionUID = -8166593637122730060L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id" , nullable=false)
	private Long id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "create_time")
	private Date createTime;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "query_sql")
	private String querySql;

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

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getQuerySql() {
		return querySql;
	}

	public void setQuerySql(String querySql) {
		this.querySql = querySql;
	}

	@Override
	public String getObjectDescription() {
		return null;
	}

	@Override
	public String getEntityDescription() {
		return null;
	}
}
