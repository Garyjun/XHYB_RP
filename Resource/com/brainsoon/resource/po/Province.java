package com.brainsoon.resource.po;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * 省
 */
@Entity
@Table(name = "province")
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class Province implements java.io.Serializable {

	// Fields

	private static final long serialVersionUID = 8006039214313413237L;
	private Integer id;
	private String code;
	private String name;
	private Integer displayno;

	// Constructors

	/** default constructor */
	public Province() {
	}

	/** minimal constructor */
	public Province(Integer id) {
		this.id = id;
	}
	public Province(String code,String name) {
		super();
		this.name = name;
		this.code = code;
	}
	/** full constructor */
	public Province(Integer id, String code, String name, Integer displayno) {
		this.id = id;
		this.code = code;
		this.name = name;
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

	@Column(name = "name",length=50)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "displayno")
	public Integer getDisplayno() {
		return this.displayno;
	}

	public void setDisplayno(Integer displayno) {
		this.displayno = displayno;
	}

}