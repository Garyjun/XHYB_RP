package com.brainsoon.resource.action;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.resource.service.IFragmentService;
import com.brainsoon.semantic.ontology.model.Asset;
import com.brainsoon.semantic.ontology.model.AssetList;
import com.brainsoon.semantic.ontology.model.Entry;
import com.google.gson.Gson;

/**
 * @ClassName: Fragment
 * @Description: TODO
 * @author 
 * @date 2016年1月21日 下午3:56:59
 *
 */
@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class FragmentAction extends BaseAction {
	@Autowired
	private IFragmentService fragmentService;
	private static final String SEARCH_ASSET_DETAIL = WebappConfigUtil.getParameter("SEARCH_ASSET_DETAIL");
	@RequestMapping("/fragment/query")
	@ResponseBody
	public String queryfragment(HttpServletRequest request,HttpServletResponse response){
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		Map<String, String> map = new HashMap<String, String>();
		Map<String, String> map1 = new HashMap<String, String>();
		Map<String, String> map2 = new HashMap<String, String>();
		Map<String, String> map3 = new HashMap<String, String>();
		Map<String, String> map4 = new HashMap<String, String>();
		Map<String, String> map5 = new HashMap<String, String>();
		map.put("types", "fragment/image/text.png");
		map1.put("types", "fragment/image/text.png");
		map2.put("types", "fragment/image/text.png");
		map3.put("types", "fragment/image/text.png");
		map4.put("types", "fragment/image/bmp.png");
		map5.put("types", "fragment/image/bmp.png");
		map.put("wenId", "切实把加快转变经济发...");
		map1.put("wenId", "壮大城市社区居民委...");
		map2.put("wenId", "积极完善城市社区党...");
		map3.put("wenId", "不断健全城市社区...");
		map4.put("wenId", "大力加强对城市社区...");
		map5.put("wenId", "切实改善城市社区居...");
		map.put("duanId", "段落");
		map1.put("duanId", "段落1");
		map2.put("duanId", "段落2");
		map3.put("duanId", "段落3");
		map4.put("duanId", "插图1");
		map5.put("duanId", "插图2");
		map.put("neirong", "民公益性服务设施建设纳入城市规划、土地利用规划和社区发展相关专项规划，并与社区卫生、警务指标对建设工程规划设计...");
		map1.put("neirong", "建设工程规划设计方案。城市规划行政主管部门要按照规定的配套建设指标对建设工程规划设计方案进建设工程规划程规划...");
		map2.put("neirong", "民公益性服务设施建设纳入城市规划、土地利用规划和社区发展相关专项规划，并与社区卫生、警务指标对建设工程规划设计...");
		map3.put("neirong", "民公益性服务设施建设纳入城市规划、土地利用规划和社区发展相关专项规划，并与社区卫生、警务指标对建设工程规划设计...");
		map4.put("neirong", "");
		map5.put("neirong", "");
		list.add(map);
		list.add(map1);
		list.add(map2);
		list.add(map3);
		list.add(map4);
		list.add(map5);
		return JSONArray.fromObject(list).toString();
	}
	@RequestMapping("/fragment/queryList")
	@ResponseBody
	public PageResult queryList(@RequestParam(value="resourceName",required=false)String resourceName,
			@RequestParam(value="wordName",required=false)String wordName,
			@RequestParam(value="fragType",required = false)String fragType,@RequestParam(value="treeName",required=false)String treeName){
		AssetList assetList = null;
		if(StringUtils.isNotBlank(treeName)){
			try {
				treeName = URLDecoder.decode(treeName,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		try {
			assetList = fragmentService.queryList(resourceName, wordName, fragType, treeName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		PageResult pageResult = new PageResult();
		pageResult.setTotal(assetList.getTotle());
		pageResult.setRows(assetList.getAssets());
		return pageResult;
	}
	 @RequestMapping("/fragment/toFragmentDetail")
	public String toFragmentDetail(HttpServletRequest request, ModelMap model) {
		String objectId = request.getParameter("objectId");
		HttpClientUtil http = new HttpClientUtil();
		String resourceDetail = http.executeGet(SEARCH_ASSET_DETAIL + "?id=" + objectId);
		Gson gson = new Gson();
		Asset as = gson.fromJson(resourceDetail, Asset.class);
		if(as!=null){
//			model = publishResService.jsonArray(en, objectId, model);
			model.put("publishType", "76");
		}
		model.put("detailFlag", "detail");
//		String execuId = WorkFlowUtils.getExecuId(objectId, "pubresCheck");
//		model.put("execuId", execuId);
		model.put("objectId", objectId);
		model.put("assetAs", as);
		logger.debug(resourceDetail);
		return "fragment/fragmentDetail";
	}
	 @RequestMapping("/fragment/toFragmentImageDetail")
	public String toFragmentImageDetail(HttpServletRequest request, ModelMap model) {
		String objectId = request.getParameter("objectId");
		HttpClientUtil http = new HttpClientUtil();
		String resourceDetail = http.executeGet(SEARCH_ASSET_DETAIL + "?id=" + objectId);
		Gson gson = new Gson();
		Asset as = gson.fromJson(resourceDetail, Asset.class);
		if(as!=null){
//			model = publishResService.jsonArray(en, objectId, model);
			model.put("publishType", "76");
		}
		model.put("detailFlag", "detail");
//		String execuId = WorkFlowUtils.getExecuId(objectId, "pubresCheck");
//		model.put("execuId", execuId);
		model.put("objectId", objectId);
		model.put("assetAs", as);
		logger.debug(resourceDetail);
		return "fragment/fragmentImageDetail";
	}
}
