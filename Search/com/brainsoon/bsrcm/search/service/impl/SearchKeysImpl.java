package com.brainsoon.bsrcm.search.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.brainsoon.bsrcm.search.po.SearchKeys;
import com.brainsoon.bsrcm.search.po.SolrQueue;
import com.brainsoon.bsrcm.search.service.ISearchKeysService;
import com.brainsoon.bsrcm.search.service.ISolrQueueFacede;
import com.brainsoon.common.service.impl.BaseService;
@Service
public class SearchKeysImpl extends BaseService implements
 ISearchKeysService {
	/**
	 * 返回searchKeys列表名称
	 * @return
	 */
	@Override
	public String getSearchKeyName() {
		String hql = "from SearchKeys  ORDER BY searchNum DESC";
		List<SearchKeys> searchList = baseDao.query(hql,0,15);
		JSONArray jsArray = new JSONArray();
		if(searchList!=null && !searchList.isEmpty()){
			for(SearchKeys searchKey:searchList){
				JSONObject jo = new JSONObject();
				jo.put("name", searchKey.getSearchName());
				jo.put("num", searchKey.getSearchNum());
				jsArray.add(jo);
			}
		}
		return jsArray.toString();
	}

}
