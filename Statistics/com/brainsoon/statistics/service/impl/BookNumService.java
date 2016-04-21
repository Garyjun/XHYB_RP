package com.brainsoon.statistics.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.common.support.OperDbUtils;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.statistics.service.IBookNumService;
import com.brainsoon.system.model.DictValue;

@Service
public class BookNumService extends BaseService implements IBookNumService {

	//图书资源统计
	private static final String PUBLISH_BOOK_URL = WebappConfigUtil.getParameter("BOOK_RES_PUBCOLLECTION");
	//素材资源统计
	private static final String PUBLISH_SUCAI_URL = WebappConfigUtil.getParameter("SUCAI_RES_PUBCOLLECTION");
	
	private JdbcTemplate jdbcTemplate;
	
	 @Autowired
	 public void init(DataSource dataSource) {
	       this.jdbcTemplate = new JdbcTemplate(dataSource);
	 }
	/**
	 * 素材资源统计
	 */
	@Override
	public String querySuCai(String publishType,String queryColum) {
		
		HttpClientUtil http = new HttpClientUtil();
		
		//String url = WebappConfigUtil.getParameter("http://10.130.29.26:8080/semantic_index_server/publish/ontologyListQuery/scProductTypeStatics?publish_type=37");
		//28上   String formList = http.executeGet("http://10.1.1.21:8080/bssks_cciph/publish/ontologyListQuery/scProductTypeStatics?publish_type=37");
		//本地 http://10.130.29.26:8080/semantic_index_server/publish/ontologyListQuery/scProductTypeStatics?publish_type=37
		String formList = http.executeGet(PUBLISH_SUCAI_URL);
		
		//根据数据字典索引查询出该数据字典下的所有项
		List<DictValue> dictList =  OperDbUtils.queryDictValueListByIndexTag("relation_productType");
		
		JSONArray array = new JSONArray();  //记录返回的总结果集
		JSONArray jsonArray = JSONArray.fromObject(formList);
		if(jsonArray != null){
			for (DictValue dictValue : dictList) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("counts",0);
				jsonObject.put("names", dictValue.getName());
				for(int i=0;i<jsonArray.size();i++){
					JSONObject jsonObjecty =  jsonArray.getJSONObject(i);
					Object bookId = jsonObjecty.get("objectId");
					if(bookId != null){
						String objectId = jsonObjecty.getString("objectId");
						if(dictValue.getId().toString().equals(objectId)){
							Object counts = jsonObjecty.get("count");
							if(counts != null){
								jsonObject.put("counts", Integer.valueOf(jsonObjecty.getString("count")));
								break;
							}
							
						}
					}
				}
				array.add(jsonObject);
			}
		}
		return array.toString();
	}

	
	
	
	/**
	 * 图书资源统计
	 */
	@Override
	public String queryBook(String publishType,String queryColum) {
		
		HttpClientUtil http = new HttpClientUtil();
		//28上   http://10.1.1.21:8080/bssks_cciph/publish/ontologyListQuery/resourcePCStatistics
		//本地  http://10.130.29.26:8080/semantic_index_server/publish/ontologyListQuery/resourcePCStatistics
		String formList = http.executeGet(PUBLISH_BOOK_URL);
		
		Map<String,String> bookMap = new HashMap<String, String>();
		
		JSONArray array = new JSONArray();  //记录返回的总结果集
		
		JSONArray sonArry = new JSONArray();  //存放所有的二级目录
		
		
		//将所有的二级目录存放在sonArry中
		try {
			JSONArray jsonParent = JSONArray.fromObject(formList);
			if(jsonParent != null){
				for(int i=0;i<jsonParent.size();i++){
					JSONObject jsonObjectSon = new JSONObject();
					JSONObject jsonObjecty =  jsonParent.getJSONObject(i);
					Object pathObject = jsonObjecty.get("objectId");
					if(pathObject != null){
						String pathId = jsonObjecty.getString("objectId");
						if(pathId.equals("")){
							continue;
						}
						if(pathId.contains("-")){
							continue;
						}
						if(StringUtils.isNotBlank(pathId) && pathId.contains(",")){
							
							jsonObjectSon.put("id", pathId);
							Object countObject = jsonObjecty.get("count");
							if(countObject != null){
								String count = jsonObjecty.getString("count");
								jsonObjectSon.put("count", count);
							}
						}
						else{
							continue;
						}
					}
					sonArry.add(jsonObjectSon);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		//循环所有的二级目录，统计二级目录对应的一级目录下共有多少个资源
		if(sonArry.size()>0 && sonArry != null){
			JSONArray jsonParent = JSONArray.fromObject(sonArry.toString());
			for(int i=0;i<jsonParent.size();i++){
				int parentCount = 0;
				String parentId = "";
				JSONObject jsonObjectSon = new JSONObject();
				JSONObject jsonObjecty =  jsonParent.getJSONObject(0);
				Object pathObject = jsonObjecty.get("id");
				if(pathObject != null){
					String id = jsonObjecty.getString("id");
					parentId = id.substring(0, id.indexOf(","));
					
					for(int j=0;j<jsonParent.size();j++){
						JSONObject sonObjecty =  jsonParent.getJSONObject(j);
						if(sonObjecty != null){
							try {
								Object sonObject = jsonObjecty.get("id");
								if(sonObject != null){
									String sonId = sonObjecty.getString("id");
									String currentSonID = sonId.substring(0, sonId.indexOf(","));
									if(parentId.equals(currentSonID)){
										parentCount = parentCount + Integer.valueOf(sonObjecty.getString("count"));
										jsonParent.remove(j);
										j = -1;
									}
								}
							} catch (Exception e) {
								jsonParent.remove(j);
								e.printStackTrace();
							}
							
						}
					}
				}
				jsonObjectSon.put("number", parentCount);
				jsonObjectSon.put("level", 1);
				jsonObjectSon.put("parent", "");
				jsonObjectSon.put("currentId", parentId);
				array.add(jsonObjectSon);
				i = -1;
			}
		}
		
		
		
		//将json串转换成json对象
		try{
			JSONArray jsonArray = JSONArray.fromObject(formList);
			if(jsonArray != null){
				for(int i=0;i<jsonArray.size();i++){
					JSONObject jsonObjectbook = new JSONObject();
					
					JSONObject jsonObjecty =  jsonArray.getJSONObject(i);
					Object pathObject = jsonObjecty.get("objectId");
					String bookNum = "";	//个数
					String bookClass = "";	//所属级别
					String pId = "";	//上级节点id
					String pathId = "";	//当前节点的path值
					//查询对应名称
					if(pathObject != null){
						pathId = jsonObjecty.getString("objectId");
						if(pathId.equals("")){
							continue;
						}
						if(pathId.contains("-")){
							continue;
						}
						if(StringUtils.isNotBlank(pathId) && pathId.contains(",")){
							bookClass = "2";
							
							//获取父节点的id
							pId = pathId.substring(0,pathId.indexOf(","));
						}else{
							bookClass = "1";
						}
					}
					
					//查询个数
					Object numObject = jsonObjecty.get("count");
					if(numObject != null){
						bookNum = jsonObjecty.getString("count");
					}
					jsonObjectbook.put("number", bookNum);	//当前目录在资源中的个数
					jsonObjectbook.put("level", bookClass);	//当前目录在树中的级别（一级、二级）
					jsonObjectbook.put("parent", pId);		//当前目录的上级目录的id
					jsonObjectbook.put("currentId", pathId);//当前目录的id
					
					array.add(jsonObjectbook);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return array.toString();
	}


}
