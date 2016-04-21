package com.brainsoon.search.action;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.query.QueryConditionItem;
import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.bsrcm.search.po.SearchKeys;
import com.brainsoon.bsrcm.search.service.ISearchKeysService;
import com.brainsoon.common.dao.IBaseDao;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.resource.service.IBaseSemanticSerivce;
import com.brainsoon.system.dao.IUserDao;
import com.brainsoon.system.model.User;
import com.brainsoon.system.service.IUserService;

/**
 * 全文检索
 * 
 * @author 唐辉
 *
 */
@Controller
@RequestMapping("/search/pubres/fullsearch")
public class FullSearchAction extends BaseAction {

	@Autowired
	private IBaseSemanticSerivce baseSemanticSerivce;
	@Autowired
	private IUserService userService;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
    protected IBaseDao baseDao;
	@Autowired
	private ISearchKeysService searchKeysService;

	@RequestMapping("gotoMain")
	public String gotoMain(Model model) {
		return "/search/pubres/fullSearch";
	}

	@RequestMapping("list")
	public void list(HttpServletRequest request, HttpServletResponse response) {
		logger.info("进入查询方法");
		String keyword = request.getParameter("keyword");
		QueryConditionList conditionList = getQueryConditionList();
		String hql= baseSemanticSerivce.parseCondition(request, conditionList);
		//String jsonStr = baseSemanticSerivce.query4PageByPubRes(hql,WebappConfigUtil.getParameter("PUBLISH_FULL_SEARCH"));
		String url = WebappConfigUtil.getParameter("PUBLISH_FULL_SEARCH_YW") + "?" + hql;
		//String url = "http://10.1.1.202:9098/solrservice/searchBookContent.action" + "?" + hql;
		HttpClientUtil http = new HttpClientUtil();
		String jsonStr = http.executeGet(url);
		logger.info("全文检索url:"+url);
		logger.info("全文检索返回数据:"+jsonStr);
		
		List<QueryConditionItem> items = conditionList.getConditionItems();
		String keyWords = "";
		for (QueryConditionItem queryConditionItem : items) {
			keyWords = queryConditionItem.getValue().toString();
		}
		if(StringUtils.isNotBlank(keyWords)){
			hql = "from SearchKeys where searchName='"+keyWords+"'";
			List<SearchKeys> searchList = userService.query(hql);
			if(searchList!=null && !searchList.isEmpty()){
				Long searchNum = searchList.get(0).getSearchNum();
				searchNum = searchNum+1;
				hql = "update SearchKeys set searchNum="+searchNum+" where searchName='"+keyWords+"'";
				baseDao.updateWithHql(hql);
			}else{
				Long num =1L;
				SearchKeys searchKeys = new SearchKeys();
				searchKeys.setSearchName(keyWords);
				searchKeys.setSearchNum(num);
				userService.create(searchKeys);
			}
		}
//		if(StringUtils.isNotBlank(str))
		// 将创建者的Id换成登录名
		JSONObject jsonobject = JSONObject.fromObject(jsonStr);
		
		// 获取一个json数组
		JSONArray array = jsonobject.getJSONArray("items");
		JSONArray retArray = new JSONArray();
		for (Object object : array) {
			JSONObject file = JSONObject.fromObject(object);
			
			if (!file.isEmpty()) {
				
				// 作者
				try {
					String authorname = file.getString("authorname");
					if (StringUtils.isNotBlank(authorname)) {
						file.put("authorname", authorname);
					}else {
						file.put("authorname", "暂无");
					}
				} catch (Exception e) {
					file.put("authorname", "暂无");
					logger.info("-----全文检索---异常");
					e.printStackTrace();
				}
				
				//封面
				try {
					String genre = file.getString("genre");
				} catch (Exception e) {
					file.put("genre", "");
					logger.info("-----全文检索---异常");
					e.printStackTrace();
				}

				// title判断
				try {
					String title = file.getString("title");
				} catch (Exception e) {
					file.put("title", "");
					logger.info("-----全文检索---异常");
					e.printStackTrace();
				}
				
				// matchContents判断
				try {
					String matchContents = file.getString("matchContents");
					if (StringUtils.isNotBlank(keyword) || keyword==null || "null".equals(keyword)) {
						String description = file.getString("description");
						file.put("matchContents", description);
					}
				} catch (Exception e) {
					file.put("matchContents", "");
					logger.info("-----全文检索---异常");
					e.printStackTrace();
				}

				retArray.add(file);
			}
			jsonobject.put("rows", retArray);
			jsonobject.put("total", jsonobject.get("maxPageIndexNumber"));

			outputResult(jsonobject.toString());
			// outputResult(jsonStr);
		}
	}
	/**
	 * ajax查询searchKeys表，返回前台
	 */
	@RequestMapping("searchKeys")
	public @ResponseBody String searchKeys(){
		String json = searchKeysService.getSearchKeyName();
		return json;
	}
}
