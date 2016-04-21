package com.brainsoon.bsrcm.search.client;

import java.util.ArrayList;
import java.util.List;

/**
 * <dl>
 * <dt>SearchResult</dt>
 * <dd>Description:�������(�ĵ��б�)</dd>
 * <dd>Copyright: Copyright (c) 2011 ���ƿƼ����޹�˾</dd>
 * <dd>Company: ���ƿƼ����޹�˾</dd>
 * <dd>CreateDate: Oct 28, 2011</dd>
 * </dl>
 * 
 * @author ����
 */

public class SearchResult {	
	/**
	 * ��ʼ�к�
	 */
	private long startRow;
	
	/**
	 * �������
	 */
	private long maxRow;
	
	/**
	 * �ܹ����� 
	 */	
	private long totleRow;
	
	/**
	 * (ƥ���)�ĵ�����
	 */	
	private final List<Document> documents = new ArrayList<Document>();
	
	public long getStartRow() {
		return startRow;
	}

	public void setStartRow(long startRow) {
		this.startRow = startRow;
	}
	
	public long getMaxRow() {
		return maxRow;
	}

	public void setMaxRow(long maxRow) {
		this.maxRow = maxRow;
	}
	
	public long getTotleRow() {
		return totleRow;
	}

	public void setTotleRow(long totleRow) {
		this.totleRow = totleRow;
	}
	
	public void addDocument(Document document) {
		documents.add(document);
	}

	public List<Document> getDocuments() {
		return documents;
	}	
}
