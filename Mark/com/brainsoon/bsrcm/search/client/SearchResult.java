package com.brainsoon.bsrcm.search.client;

import java.util.ArrayList;
import java.util.List;

/**
 * <dl>
 * <dt>SearchResult</dt>
 * <dd>Description:搜索结果(文档列表)</dd>
 * <dd>Copyright: Copyright (c) 2011 博云科技有限公司</dd>
 * <dd>Company: 博云科技有限公司</dd>
 * <dd>CreateDate: Oct 28, 2011</dd>
 * </dl>
 * 
 * @author 张欣
 */

public class SearchResult {	
	/**
	 * 开始行号
	 */
	private long startRow;
	
	/**
	 * 最大行数
	 */
	private long maxRow;
	
	/**
	 * 总共行数 
	 */	
	private long totleRow;
	
	/**
	 * (匹配的)文档集合
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
