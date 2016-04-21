package com.brainsoon.system.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.brainsoon.common.po.BaseHibernateObject;

/**
 * 人员表
 * @author lenovo
 *
 */
@Entity
@Table(name = "sys_staff")
public class Staff extends BaseHibernateObject{
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name ="id", nullable = false)
	private Long id;
	
	@Column
	private String name;  //姓名
	
	@Column
	private String age;  //年龄
	
	@Column
	private String sex; //性别
	
	@Column
	private String birthday;  //出生日期
	
	@Column
	private String address;  //住址
	
	@Column(name = "card_number")
	private String cardNumber;  //身份证号
	
	@Column
	private String code;   //邮政编码
	
	@Column
	private String identity; //职位
	
	@Column
	private String telephone;  //联系电话
	
	@Column(name = "user_type")
	private String userType = "作者";  //人员类型
	
	@Column(name = "work_address")  //作者单位地址
	private String workAddress;
	
	@Column(name = "work_name")
	private String workName;   //作者单位名称
	
	@Column
	private String email;   //电子邮件
	

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

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	
	public String getWorkAddress() {
		return workAddress;
	}

	public void setWorkAddress(String workAddress) {
		this.workAddress = workAddress;
	}

	public String getWorkName() {
		return workName;
	}

	public void setWorkName(String workName) {
		this.workName = workName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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
