package com.brainsoon.resource.po;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import com.brainsoon.common.po.BaseHibernateObject;

@Entity
@Table(name = "searMonthTime")
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class SearchEntryMonth extends BaseHibernateObject implements java.io.Serializable{

	private static final long serialVersionUID = 1805827217609256720L;

	private Long id;
	private String entryMonth;
	private String entryYear;
	private String entryDay;
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name = "entryMonth",length=20)
	public String getEntryMonth() {
		return entryMonth;
	}

	public void setEntryMonth(String entryMonth) {
		this.entryMonth = entryMonth;
	}

	@Column(name = "entryYear",length=20)
	public String getEntryYear() {
		return entryYear;
	}

	public void setEntryYear(String entryYear) {
		this.entryYear = entryYear;
	}
	@Column(name = "entryDay",length=20)
	public String getEntryDay() {
		return entryDay;
	}

	public void setEntryDay(String entryDay) {
		this.entryDay = entryDay;
	}
	
	
	@Transient
	@Override
	public String getObjectDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Transient
	@Override
	public String getEntityDescription() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
