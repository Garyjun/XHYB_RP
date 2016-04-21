package com.brainsoon.system.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import com.brainsoon.common.po.BaseHibernateObject;
/**
 * BSRCM应用com.brainsoon.bsrcm.dcore.po.SysMetadataType.java 创建时间：2011-12-7 创建者：
 * liusy DcType TODO
 * 
 */
@Entity
@Table(name = "sys_doi_definition")
public class SysDoi extends BaseHibernateObject {
    /**
     * Doi类
     */
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column
    private Long publishType;
    //前缀部分1
    @Column(name = "first_part_prefix1")
    private String firstPartOne;
    //前缀部分2
    @Column(name = "first_part_prefix2")
    private String firstPartTwo;
    //前缀部分3
    @Column(name = "first_part_prefix3")
    private String firstPartThree;
    //可选值1
    @Column(name = "second_opt1")
    private String secondOptionalOne;
    //可选值2
    @Column(name = "second_opt2")
    private String secondOptionalTwo;
    //可选值3
    @Column(name = "second_opt3")
    private String secondOptionalThree;
    //可选值4
    @Column(name = "second_opt4")
    private String secondOptionalFour;
    //扩展属性1
    @Column(name = "third_part_extend1")
    private String thirdExtendOne;
    //扩展属性2
    @Column(name = "third_part_extend2")
    private String thirdExtendTwo;
    //扩展属性3
    @Column(name = "third_part_extend3")
    private String thirdExtendThree;
    //扩展属性4
    @Column(name = "third_part_extend4")
    private String thirdExtendFour;
    //扩展属性5
    @Column(name = "third_part_extend5")
    private String thirdExtendFive;
    @Column(name = "doi_separator")
    private String separator;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public Long getPublishType() {
		return publishType;
	}

	public void setPublishType(Long publishType) {
		this.publishType = publishType;
	}

	public String getFirstPartOne() {
		return firstPartOne;
	}

	public void setFirstPartOne(String firstPartOne) {
		this.firstPartOne = firstPartOne;
	}

	public String getFirstPartTwo() {
		return firstPartTwo;
	}

	public void setFirstPartTwo(String firstPartTwo) {
		this.firstPartTwo = firstPartTwo;
	}

	public String getFirstPartThree() {
		return firstPartThree;
	}

	public void setFirstPartThree(String firstPartThree) {
		this.firstPartThree = firstPartThree;
	}

	public String getSecondOptionalOne() {
		return secondOptionalOne;
	}

	public void setSecondOptionalOne(String secondOptionalOne) {
		this.secondOptionalOne = secondOptionalOne;
	}

	public String getSecondOptionalTwo() {
		return secondOptionalTwo;
	}

	public void setSecondOptionalTwo(String secondOptionalTwo) {
		this.secondOptionalTwo = secondOptionalTwo;
	}

	public String getSecondOptionalThree() {
		return secondOptionalThree;
	}

	public void setSecondOptionalThree(String secondOptionalThree) {
		this.secondOptionalThree = secondOptionalThree;
	}

	public String getSecondOptionalFour() {
		return secondOptionalFour;
	}

	public void setSecondOptionalFour(String secondOptionalFour) {
		this.secondOptionalFour = secondOptionalFour;
	}

	public String getThirdExtendOne() {
		return thirdExtendOne;
	}

	public void setThirdExtendOne(String thirdExtendOne) {
		this.thirdExtendOne = thirdExtendOne;
	}

	public String getThirdExtendTwo() {
		return thirdExtendTwo;
	}

	public void setThirdExtendTwo(String thirdExtendTwo) {
		this.thirdExtendTwo = thirdExtendTwo;
	}

	public String getThirdExtendThree() {
		return thirdExtendThree;
	}

	public void setThirdExtendThree(String thirdExtendThree) {
		this.thirdExtendThree = thirdExtendThree;
	}

	public String getThirdExtendFour() {
		return thirdExtendFour;
	}

	public void setThirdExtendFour(String thirdExtendFour) {
		this.thirdExtendFour = thirdExtendFour;
	}

	public String getThirdExtendFive() {
		return thirdExtendFive;
	}

	public void setThirdExtendFive(String thirdExtendFive) {
		this.thirdExtendFive = thirdExtendFive;
	}

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
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
