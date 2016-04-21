package com.brainsoon.resource.service.impl;

import org.springframework.stereotype.Service;

import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.resource.service.IFragmentService;
import com.brainsoon.semantic.ontology.model.AssetList;
import com.google.gson.Gson;

/**
 * @ClassName: FragmentService
 * @Description: TODO
 * @author 
 * @date 2016年2月1日 上午9:36:34
 *
 */
@Service
public class FragmentService extends BaseService implements IFragmentService {
	private static String Asset_RES_PUBCOLLECTION = WebappConfigUtil.getParameter("Asset_RES_PUBCOLLECTION");

	@Override
	public AssetList queryList(String name, String wordName, String fragType,String treeName) {
		AssetList assetList = null;
		HttpClientUtil http = new HttpClientUtil();
		String result = http.executeGet(Asset_RES_PUBCOLLECTION+"?fragType="+fragType+"&name="+name+"&wordName="+wordName+"&treeName="+treeName);
		Gson gson = new Gson();
		assetList = gson.fromJson(result, AssetList.class);
		return assetList;
	}
}
