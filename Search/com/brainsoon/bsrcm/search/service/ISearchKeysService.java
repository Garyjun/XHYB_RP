package com.brainsoon.bsrcm.search.service;

import java.util.List;

import com.brainsoon.bsrcm.search.po.SolrQueue;
import com.brainsoon.common.service.IBaseService;

/**
 * Content Desc:<p><p>
 */
public interface ISearchKeysService extends IBaseService {
	
	
	/**
	 * 返回searchKeys列表名称
	 * @return
	 */
	public String getSearchKeyName();

}
