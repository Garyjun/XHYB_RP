package com.brainsoon.system.vo;

public class RemoteUser {
	private Long id;//用户id
	private String loginName;//登录名
	private String userName;//用户名
	private String phone;//电话
	private String mobile;//手机
    private String email;//邮箱
	private String resAuthCodes;//资源授权码,形如:ZS,K,SL1;ZS,P
	private String resTypes;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getResAuthCodes() {
		return resAuthCodes;
	}

	public void setResAuthCodes(String resAuthCodes) {
		this.resAuthCodes = resAuthCodes;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getResTypes() {
		return resTypes;
	}

	public void setResTypes(String resTypes) {
		this.resTypes = resTypes;
	}
}
