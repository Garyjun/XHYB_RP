package com.brainsoon.system.service.impl;

import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.system.model.StopWord;
import com.brainsoon.system.service.IStopWordService;
@Service
public class StopWordService extends BaseService implements IStopWordService {

	private String postUrl = WebappConfigUtil.getParameter("RES_ROOT_URL");
	@Override
	public void addStopWord(StopWord stopWord) {
		// TODO Auto-generated method stub
		create(stopWord);
	}

	@Override
	public void updateStopWord(StopWord stopWord) {
		// TODO Auto-generated method stub
		update(stopWord);
	}

	@Override
	public void deleteByIds(String ids) {
		// TODO Auto-generated method stub
		if(StringUtils.isNotBlank(ids)){
			delete(StopWord.class, ids);
		}
	}

	@Override
	public PageResult listStopWord() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String postStopWord(String name) {
		String url = postUrl + "stopWord/add";
		JSONObject jo = new JSONObject();
		jo.put("stopName", name.substring(0,name.length()-1));
		HttpClientUtil http = new HttpClientUtil();
		String json = http.postJson(url, jo.toString());
		return json;
	}
	
	public String getAllName(){
		String hql = "from StopWord";
		List<StopWord> list = query(hql);
		String name = "";
		if(list!=null&&list.size()>0){
			for(StopWord sw : list){
				name += sw.getName() + ",";
			}
		}
		return name;
	}

}
