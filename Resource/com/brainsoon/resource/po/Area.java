package com.brainsoon.resource.po;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Area entity. @author MyEclipse Persistence Tools
 * 区
 */
@Entity
@Table(name = "area")
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class Area implements java.io.Serializable {

	// Fields

	private static final long serialVersionUID = -1056178654494091926L;
	private Integer id;
	private String name;
	private String code;
	private String citycode;
	private Integer displayno;

	// Constructors

	/** default constructor */
	public Area() {
	}

	/** minimal constructor */
	public Area(Integer id) {
		this.id = id;
	}

	public Area(String code,String name) {
		super();
		this.name = name;
		this.code = code;
	}
	public Area(String name) {
		super();
		this.name = name;
	}

	/** full constructor */
	public Area(Integer id, String name, String code, String citycode,
			Integer displayno) {
		this.id = id;
		this.name = name;
		this.code = code;
		this.citycode = citycode;
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

	@Column(name = "name",length=30)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "code", length = 10)
	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name = "citycode", length = 10)
	public String getCitycode() {
		return this.citycode;
	}

	public void setCitycode(String citycode) {
		this.citycode = citycode;
	}

	@Column(name = "displayno")
	public Integer getDisplayno() {
		return this.displayno;
	}

	public void setDisplayno(Integer displayno) {
		this.displayno = displayno;
	}
}