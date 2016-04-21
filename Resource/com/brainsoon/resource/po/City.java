package com.brainsoon.resource.po;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * 市
 */
@Entity
@Table(name = "city")
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class City implements java.io.Serializable {

	// Fields

	private static final long serialVersionUID = -2683469084068070334L;
	private Integer id;
	private String code;
	private String name;
	private String provincecode;
	private Integer displayno;

	// Constructors

	/** default constructor */
	public City() {
	}

	/** minimal constructor */
	public City(Integer id) {
		this.id = id;
	}
	public City(String code,String name) {
		super();
		this.name = name;
		this.code = code;
	}
	/** full constructor */
	public City(Integer id, String code, String name, String provincecode,
			Integer displayno) {
		this.id = id;
		this.code = code;
		this.name = name;
		this.provincecode = provincecode;
		this.displayno = displayno;
	}

	// Property accessors
	@Id
	@GeneratedValue
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "code", length = 10)
	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name = "name",length=30)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "provincecode", length = 10)
	public String getProvincecode() {
		return this.provincecode;
	}

	public void setProvincecode(String provincecode) {
		this.provincecode = provincecode;
	}

	@Column(name = "displayno")
	public Integer getDisplayno() {
		return this.displayno;
	}

	public void setDisplayno(Integer displayno) {
		this.displayno = displayno;
	}

}