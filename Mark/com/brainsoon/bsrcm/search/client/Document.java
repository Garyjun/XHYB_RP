package com.brainsoon.bsrcm.search.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <dl>
 * <dt>Document</dt>
 * <dd>Description:资源文档</dd>
 * <dd>Copyright: Copyright (c) 2011 博云科技有限公司</dd>
 * <dd>Company: 博云科技有限公司</dd>
 * <dd>CreateDate: Oct 28, 2011</dd>
 * </dl>
 * 
 * @author 张欣
 */

public class Document {
	/**
	 * 文档标识
	 */
	private String id;
	
	/**
	 * 所属资源类型 
	 */
	private String type; 	
	
	/**
	 * 资源分类 
	 */	
	private String resType;
	
	/**
	 * 文档名
	 */
	private String docName;
	
	/**
	 * 关键字
	 */
	private String keywords;
	
	/**
	 * 作者
	 */
	private String author;
	
	/**
	 * 出版社
	 */
	private String publishingHouse;
	
	/**
	 * (匹配的)文本内容 
	 */	
	private final List<String> matchContents = new ArrayList<String>();
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getResType() {
		return resType;
	}

	public void setResType(String resType) {
		this.resType = resType;
	}
	
	public String getDocName() {
		return docName;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}
	
	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	
	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
	
	public String getPublishingHouse() {
		return publishingHouse;
	}

	public void setPublishingHouse(String publishingHouse) {
		this.publishingHouse = publishingHouse;
	}
	
	public List<String> getMatchContents() {
		return matchContents;
	}

	public void addMatchContents(List<String> mcs) {
		matchContents.addAll(mcs);
	}	
	
	public String toString() {	
		//以后再说
		return "";
	}
}
