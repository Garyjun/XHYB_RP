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

public class ItemAnalyzeExcel {	
	/**
	 * (匹配的)文档集合
	 */	
	private List<AnalyzeExcel> listMap;
	
	public void addAnalyzeExcel(AnalyzeExcel analyzeExcel ){
		if(listMap ==null){
			listMap = new ArrayList<AnalyzeExcel>();
		}
		listMap.add(analyzeExcel);
	}
	public List<AnalyzeExcel> getListMap() {
		return listMap;
	}

	public void setListMap(List<AnalyzeExcel> listMap) {
		this.listMap = listMap;
	}
}
