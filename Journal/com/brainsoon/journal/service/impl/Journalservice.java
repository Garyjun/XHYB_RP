package com.brainsoon.journal.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brainsoon.appframe.query.QueryConditionItem;
import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.common.pagination.PageInfo;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.date.DateUtil;
import com.brainsoon.journal.service.IJournalservice;
import com.brainsoon.resource.util.ResUtils;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.Sco;
import com.brainsoon.semantic.ontology.model.SearchParamCa;
import com.brainsoon.semantic.ontology.model.SearchResult;
import com.brainsoon.semantic.ontology.model.SearchResultCa;
import com.brainsoon.system.model.ResCategory;
import com.brainsoon.system.service.IDictNameService;
import com.google.gson.Gson;
@Service
public class Journalservice extends BaseService implements IJournalservice{
	@Autowired
	private IDictNameService dictNameService;

	/**
	 * 
	* @Title: getTimeTree
	* @Description: 期刊管理左侧页面时间树 0某期刊 1某年代 2某年 3某期
	* @param type
	* @return    参数
	* @return String    返回类型
	* @throws
	 */
	public String getTimeTree(Long type, String nodeId, String level, String name, String xcode){
		
		JSONArray array = new JSONArray();
		int nowYear = DateUtil.getYearOf(new Date());//当前年
		if ("2".equals(level)) {//点击的节点为年份 点击年份展开的是当前年份下的所有期数
			int num = getNumByYear(name);
			for (int i = num; i >= 1; i--) {
				DecimalFormat df=new DecimalFormat("00");
			    String count = df.format(i);
				JSONObject jo = new JSONObject();
				jo.put("isParent", false);
				jo.put("level", "3");//期
				jo.put("id", count);
				jo.put("name", "第"+count+"期");
				jo.put("code", count);
				jo.put("pid", nodeId);
				jo.put("xcode", xcode+","+count);
				jo.put("type", type);
				array.add(jo);
			}
		}else {//点击的节点是年代
			List<ResCategory> resList = new ArrayList<ResCategory>();
			resList = query("from ResCategory where type = " + type + " and pid =" + nodeId + " order by id desc");
			for(ResCategory rc : resList){
				JSONObject jo = new JSONObject();
				jo.put("isParent", true);
				if ("0".equals(rc.getPid())) {
					jo.put("level", "1");//年代
				}else {
					jo.put("level", "2");//年份
				}
				jo.put("id", rc.getId());
				jo.put("name", rc.getName());
				jo.put("code", rc.getCode());
				jo.put("pid", rc.getPid());
				jo.put("xcode", rc.getXcode());
				jo.put("type", type);
				array.add(jo);
			}
			
			if ("0".equals(nodeId)) {
				JSONObject root = new JSONObject();
				root.put("level", "0");//刊物
				root.put("isParent", true);
				root.put("id", 0);
				root.put("name", "新华月报");
				root.put("code", "");
				root.put("pid", "-1");
				root.put("xpath", "");
				root.put("type", type);
				root.put("open", true);
				array.add(root);
			}
		}
		
		
		return array.toString();
	}
	
	/**
	 * 
	* @Title: queryJournalList
	* @Description:查询期刊列表
	* @param request
	* @param queryUrl
	* @return    参数
	* @return String    返回类型
	* @throws
	 */
	public String queryJournalList(HttpServletRequest request, String queryUrl) {
		String level = request.getParameter("level");
		String xcode = request.getParameter("xcode");
		String magazineYear = request.getParameter("magazineYear");
		String magazineNum = request.getParameter("magazineNum");
		String numOfYear = request.getParameter("numOfYear");
		String publishType = request.getParameter("publishType");
		int page = Integer.parseInt(request.getParameter("page"));
		int size = Integer.parseInt(request.getParameter("rows"));
		String sort = request.getParameter("sort");
		String order = request.getParameter("order");
		
		if (page < 1) {
			page = 1;
		}
		HttpClientUtil http = new HttpClientUtil();

		String result = http.executeGet(queryUrl+"?page="+page+"&size="+size+"&sort="+sort+"&order="+order
				+"&publishType="+publishType +"&magazineYear="+magazineYear +"&magazineNum="+magazineNum
				+"&numOfYear="+numOfYear );
		return result;
	}
	
	/**
	 * 
	* @Title: getJournalDetail
	* @Description: 期刊详情
	* @param level
	* @param code
	* @return    参数
	* @return String    返回类型
	* @throws
	 */
	public String getJournalDetail(String level, String code){
		
		return "";
	}
	
	/**
	 * 
	* @Title: getJournalList
	* @Description: 文章列表
	* @param keyWord
	* @param articleTitle
	* @param magazineNum
	* @return    参数
	* @return String    返回类型
	* @throws
	 */
	public String getArticleList(String keyWord, String articleTitle, String magazineNum){
		
		String result = "";
		//SearchResultCa caList = new SearchResultCa();
		SearchResult articleList = new SearchResult();
		List<Sco> rows = new ArrayList<Sco>();
		for(int i=0;i<10;i++){
			Sco sco = new Sco();
			sco.setCaId("222"+i);
			sco.setId("6666"+i);
			sco.setContent("<![CDATA[　　11月16日至18日，2010中国国际矿业大会在天津举行。本次中国国际矿业大会是第十二届大会，会议的主题是“合作、责任、发展”。11月16日，2010中国国际矿业大会开幕式举行，开幕式由国土资源部部长徐绍史主持。来自50多个国家和地区的政府官员、专家学者、企业界代表近3000人参加开幕式。中共中央政治局常委、国务院副总理李克强在天津出席2010中国国际矿业大会开幕式并致辞。他强调，中国将按照坚持科学发展、加快转变经济发展方式的要求，把立足国内开发与加强国际合作结合起来，充分利用国内外两个市场两种资源，不断增强经济社会发展的能源资源保障能力。中共中央政治局委员、天津市委书记张高丽参加了开幕式。李克强在致辞中说，当前世界经济和国际矿业正处在一个调整恢复时期，矿产资源贸易日渐活跃，但一些矿产品价格攀升过快，贸易保护有所抬头。矿产资源全球分布的不均衡，决定了世界上没有任何一个国家可以完全依靠本国的资源满足发展需要，加强国际合作是必由之路。经济与矿业相辅相成，矿业是经济的支撑，经济发展是矿业繁荣的前提。国际社会应当以理性和长远的眼光，承担起推动各国共同发展、促进世界经济持续增长、维护矿业市场稳定运行的责任，深化务实合作，保持资源价格合理稳定，建立和维护有利于矿业持续繁荣的市场秩序，积极发展绿色矿业，增加资源有效供给。李克强指出，中国正处于工业化、城镇化加快推进的时期，资源环境的制约日益突出。促进经济长期平稳较快发展与社会和谐进步，必须加快建设资源节约型、环境友好型社会。中国将加大资源节约力度，通过产业结构优化、企业技术改造、绿色消费行为等促进资源节约，大力发展循环经济，全面推动节能节材节矿，努力实现可持续发展。李克强说，中国作为一个发展中大国，增强资源保障能力，必须立足国内。我国地质找矿潜力大，要加强规划和政策引导，推进体制机制和科技创新，突出重点矿种和重点成矿区带，强化地质调查和资源勘探，加快形成能源和矿产资源战略接续区，高水平开发利用资源，夯实国内保障基础。李克强指出，矿产资源市场是中国最早开放的领域之一。我们将进一步改善投资环境，继续鼓励外商投资矿业，更好地引进先进技术和管理经验。同时，支持国内有实力、有信誉的企业到境外开展矿业合作，实现互利共赢。中国国际矿业大会是世界四大矿业会议之一，是全球矿业界对话、交流、合作的重要平台。这个平台为中外矿业界了解中国矿业投资环境、掌握全球矿业最新动态、寻找商务合作机会、交流管理与发展经验等，发挥了重要作用。]]></Content>");
			sco.getMetadataMap().put("title", "2010中国国际矿业大会在天津举行　李克强出席开幕式并致辞");
			sco.getMetadataMap().put("author", "张三"+(i+1));
			sco.getMetadataMap().put("page", (i*10)+"-"+((i+1)*10));
			rows.add(sco);
		}
		articleList.setRows(rows);
		Gson gson=new Gson();
//		 SearchResultCa caList = gson.fromJson(result, SearchResultCa.class);
		result = gson.toJson(articleList);
		return result;
	}
	
	public int getNumByYear(String year){
		int num = 24;//默认24期
		try {
			if (year.endsWith("年")) {
				year = year.substring(0,year.length()-1);
			}
			int nowYear = DateUtil.getYearOf(new Date());//当前年
			int nowMonth = DateUtil.getMonthOf(new Date())+1;//当前月
			int nowDay = DateUtil.getDayOf(new Date());//当前日
			int clickYear = Integer.parseInt(year);//前台点击的年份
			
			String publicationCycle = dictNameService.getDictIndexByName("出版周期",year);//1是月刊 0是半月刊
			if (StringUtils.isNotBlank(publicationCycle)) {
				if ("0".equals(publicationCycle)) {
					if (nowYear == clickYear) {
						if (nowDay<15) {
							num = nowMonth*2-1;
						}else {
							num = nowMonth*2;
						}
					}else {
						num = 24;
					}
				}else {
					if (nowYear == clickYear) {
						num = nowMonth;
					}else {
						num = 12;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("---【Journalservice】 获取当前年份的期数异常---");
		}
		
		
		return num;
	}
	
	public static void main(String[] args) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("2016", "0");
		map.put("2015", "0");
		map.put("2014", "0");
		map.put("2013", "0");
		map.put("2012", "0");
		map.put("2011", "0");
		map.put("2010", "0");
		
		System.out.println(map.get("2016"));
	}
}
