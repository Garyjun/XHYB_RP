package com.brainsoon.common.util.dofile.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * 导航目录信息VO (为兼容资源管理平台CDP规范)
 * 
 * @author Administrator
 */
public class NavPointVO {

	private int id;
	// 标题
	private String title;
	// 开始页数
	private int start;
	// 开始页数
	private int end;
	// 层级(卷，章，节，子节)
	private String levelValue;
	// 分类(per,body,end)
	private String classValue;
	// 类别(title,copyright,content)
	private String catalogValue;
	// 子节点
	List<NavPointVO> children = new ArrayList<NavPointVO>();
	//节点导航路径：chapters/chapter005.xml#id5
	private String src;
	//该导航对应的标签：书名页、版权页、目录、文前、序言....
	private String tag; 
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLevelValue() {
		return levelValue;
	}

	public void setLevelValue(String levelValue) {
		this.levelValue = levelValue;
	}

	public String getClassValue() {
		return classValue;
	}

	public void setClassValue(String classValue) {
		this.classValue = classValue;
	}

	public String getCatalogValue() {
		return catalogValue;
	}

	public void setCatalogValue(String catalogValue) {
		this.catalogValue = catalogValue;
	}

	public List<NavPointVO> getChildren() {
		return children;
	}

	public void setChildren(List<NavPointVO> children) {
		this.children = children;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

}
