package com.brainsoon.semantic.ontology.model;

import java.util.ArrayList;
import java.util.List;

import com.brainsoon.semantic.ontology.model.Asset;

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
	private long total;
	
	/**
	 * (匹配的)文档集合
	 */	
	private List<File> rows = new ArrayList<File>();
	
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
	
	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public void setRows(List rows) {
		this.rows = rows;
	}
	
	public List getRows() {
		return rows;
	}
}
