package com.brainsoon.article.service.impl;



import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.article.service.IArticleService;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.semantic.ontology.model.AssetList;
import com.brainsoon.semantic.ontology.model.Sco;
import com.google.gson.Gson;

@Service
public class ArticleService extends BaseService implements IArticleService {

	private static String Article_RES_PUBCOLLECTION = WebappConfigUtil.getParameter("Article_RES_PUBCOLLECTION");
	
	/**
	 * 查询文章列表
	 * preType	所属期刊分类
	 * page		起始值
	 * sum		当前页显示的条数
	 * joulName 所属期刊名称
	 */
	@Override
	public AssetList queryList(String preType,int page,int sum,String joulName) {
		AssetList assetList = null;
		HttpClientUtil http = new HttpClientUtil();
		String URL = "?fragType=0&page="+page+"&sum="+sum;
		if(StringUtils.isNotBlank(preType)){
			URL += "&treeId="+preType;
		}
		if(StringUtils.isNotBlank(joulName)){
			URL += "&name="+joulName;
		}
		String result = http.executeGet(Article_RES_PUBCOLLECTION+URL);
		Gson gson = new Gson();
		if(result != null){
			assetList = gson.fromJson(result, AssetList.class);
		}
		return assetList;
	}
	
	/**
	 * 修改文章信息
	 */
	@Override
	public String updateSco(Sco sco, String publishType) {
		HttpClientUtil http = new HttpClientUtil();
		String resourceDetail = http.executeGet(WebappConfigUtil.getParameter("Article_DETAIL_CONTENT") + "?id=" + sco.getObjectId());
		Gson gson = new Gson();
		Sco oldSco = gson.fromJson(resourceDetail, Sco.class);
		Map<String,String> scoMetadaMap = sco.getMetadataMap();			//保存修改完成后的sco的元数据信息
		Map<String,String> oldScoMetadaMap = oldSco.getMetadataMap();	//保存未修改的sco的元数据信息
		//(Map.Entry<String, String> newEntry : metadataMap.entrySet()
		
		for(Map.Entry<String, String> oldEntry : oldScoMetadaMap.entrySet()){
			String scoKey = oldEntry.getKey();
			if(!StringUtils.isNotBlank(scoMetadaMap.get(scoKey))){
				scoMetadaMap.remove(scoKey);
			}
		}
		sco.setMetadataMap(scoMetadaMap);
		String updateJson = gson.toJson(sco);
		HttpClientUtil http2 = new HttpClientUtil();
		String result = http2.postJson(WebappConfigUtil.getParameter("Article_UPDATE_CONTENT"), updateJson);
		return result;
	}

	
}
