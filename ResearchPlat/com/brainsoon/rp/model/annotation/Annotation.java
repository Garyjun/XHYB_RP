package com.brainsoon.rp.model.annotation;

import java.util.Date;
import java.util.List;

public class Annotation {

	private Integer id;

	// 创建时间
	private Date created;
	// 更新时间
	private Date updated;
	// 批注
	private String text;
	// 批注对应的文本内容
	private String quote;
	// 所批注的页面URL
	private String uri;
	// 批注对应的文本内容 范围 包含开始，结束及偏移量
	private List<Range> ranges;
	// 用户
	private String user;

	// 资源id标识
	private String resId;
	// 资源类型
	private String resType;


	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getQuote() {
		return quote;
	}

	public void setQuote(String quote) {
		this.quote = quote;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public List<Range> getRanges() {
		return ranges;
	}

	public void setRanges(List<Range> ranges) {
		this.ranges = ranges;
	}


	public String getResId() {
		return resId;
	}

	public void setResId(String resId) {
		this.resId = resId;
	}

	public String getResType() {
		return resType;
	}

	public void setResType(String resType) {
		this.resType = resType;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}
