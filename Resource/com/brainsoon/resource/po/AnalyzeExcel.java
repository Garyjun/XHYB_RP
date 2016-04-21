package com.brainsoon.resource.po;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.brainsoon.semantic.ontology.model.Asset;
import com.brainsoon.semantic.ontology.model.Ca;

/**
 * <dl>
 * <dt>ExcelRow</dt>
 * <dd>Description:存储解析Excel数据</dd>
 * <dd>Copyright: Copyright (c) 2011 博云科技有限公司</dd>
 * <dd>Company: 博云科技有限公司</dd>
 * <dd>CreateDate: NOW 10, 2015</dd>
 * </dl>
 * 
 * @author 
 */

public class AnalyzeExcel {	
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
	 * 总共行数 
	 */	
	private String name;
	/**
	 * (匹配的)文档集合
	 * 存放的每一行数据
	 */	
	private List<Map<String,String>> listMap = new ArrayList<Map<String,String>>();
	
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
	
	public List<Map<String, String>> getListMap() {
		return listMap;
	}

	public void setListMap(List<Map<String, String>> listMap) {
		this.listMap = listMap;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
