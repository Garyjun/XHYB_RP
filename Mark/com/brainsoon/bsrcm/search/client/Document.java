package com.brainsoon.bsrcm.search.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <dl>
 * <dt>Document</dt>
 * <dd>Description:��Դ�ĵ�</dd>
 * <dd>Copyright: Copyright (c) 2011 ���ƿƼ����޹�˾</dd>
 * <dd>Company: ���ƿƼ����޹�˾</dd>
 * <dd>CreateDate: Oct 28, 2011</dd>
 * </dl>
 * 
 * @author ����
 */

public class Document {
	/**
	 * �ĵ���ʶ
	 */
	private String id;
	
	/**
	 * ������Դ���� 
	 */
	private String type; 	
	
	/**
	 * ��Դ���� 
	 */	
	private String resType;
	
	/**
	 * �ĵ���
	 */
	private String docName;
	
	/**
	 * �ؼ���
	 */
	private String keywords;
	
	/**
	 * ����
	 */
	private String author;
	
	/**
	 * ������
	 */
	private String publishingHouse;
	
	/**
	 * (ƥ���)�ı����� 
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
		//�Ժ���˵
		return "";
	}
}
