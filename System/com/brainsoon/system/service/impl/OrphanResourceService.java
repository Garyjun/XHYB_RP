package com.brainsoon.system.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.brainsoon.appframe.query.QueryConditionItem;
import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.semantic.ontology.model.Asset;
import com.brainsoon.semantic.ontology.model.AssetList;
import com.brainsoon.system.service.IOrphanResourceService;
import com.google.gson.Gson;
@Service
public class OrphanResourceService extends BaseService implements
		IOrphanResourceService {

	private String postUrl = WebappConfigUtil.getParameter("RES_ROOT_URL");
	private static final String ASSET_DETAIL_URL = WebappConfigUtil.getParameter("ASSET_DETAIL_URL");
	private static final String ORPHANRESOURCE_MOUNT_URL = WebappConfigUtil.getParameter("ORPHANRESOURCE_MOUNT_URL");
	private static final String ASSET_DELETE_URL = WebappConfigUtil.getParameter("ASSET_DELETE_URL");
	@Override
	/**
	 * 分页查询，智能添加前台参数，读取rdf库
	 * @param request
	 * @param conditionList
	 * @return String json
	 */
	public String query4Page(HttpServletRequest request,QueryConditionList conditionList){
		String hql = parseCondition(request, conditionList);
		HttpClientUtil http = new HttpClientUtil();
		String url = postUrl + "ontologyListQuery/orphanResources";
		String result = http.executeGet(url + "?"+hql);
		JSONObject json = JSONObject.fromObject(result);
		JSONArray array = json.getJSONArray("metaDataDCs");
		return array.toString();
	}

	public String parseCondition(HttpServletRequest request,
			QueryConditionList conditionList) {
		StringBuffer hql = new StringBuffer();
		hql.append("page=").append(request.getParameter("page")).append("&size=").append(conditionList.getPageSize());
		hql.append("&sort=").append(request.getParameter("sort")).append("&order=").append(request.getParameter("order"));
		hql.append("&type=").append(request.getParameter("type"));
		
		//组装查询
		if(null != conditionList){
			
			List<QueryConditionItem> items = conditionList.getConditionItems();
			
			for (QueryConditionItem queryConditionItem : items) {
				String filedName = queryConditionItem.getFieldName();
				try {
					hql.append("&").append(filedName).append("=").append(URLEncoder.encode(queryConditionItem.getValue()+"","utf-8"));
				} catch (UnsupportedEncodingException e) {
					logger.error(e.getMessage());
				}
			}
			
		}
		return hql.toString();
	}

	@Override
	public Asset getResourceById(String objectId) {
		String str = getResourceDetailById(objectId);
		if(StringUtils.indexOf(str, "commonMetaData") > -1){
			Gson gson = new Gson();
			return gson.fromJson(str, Asset.class);
		}else{
			return null;
		}
	}
	
	public String getResourceDetailById(String objectId){
		HttpClientUtil http = new HttpClientUtil();
		String res = http.executeGet(ASSET_DETAIL_URL+"?id="+objectId);
		//转译单引号
		res = StringUtils.replace(res, "\'", "\\'");
		return res;
	}

	@Override
	public String mountResource(String json) {
		HttpClientUtil http = new HttpClientUtil();
		return http.postJson(ORPHANRESOURCE_MOUNT_URL,json);
	}

	public void deleteResourceById(String objectId) {
		// TODO Auto-generated method stub
		HttpClientUtil http = new HttpClientUtil();
		http.executeGet(ASSET_DELETE_URL+"?id="+objectId);
	}

	public void deleteByIds(String ids) {
		if(StringUtils.isNotBlank(ids)){
			String [] idsArr = StringUtils.split(ids, ",");
			for (String id : idsArr) {
				deleteResourceById(StringUtils.trim(id));
			}
		}
	}
	
}
