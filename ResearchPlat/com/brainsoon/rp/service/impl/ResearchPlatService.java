package com.brainsoon.rp.service.impl;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.pagination.PageInfo;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.crawler.entity.CrawlResult;
import com.brainsoon.crawler.service.ICrawlService;
import com.brainsoon.rp.service.IResearchPlatService;
import com.brainsoon.rp.support.DataTableResult;
import com.brainsoon.rp.support.SearchParam;
import com.brainsoon.rp.support.TreeNode;
import com.brainsoon.rp.support.TreeUtils;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.Entry;
import com.brainsoon.semantic.ontology.model.EntryList;
import com.brainsoon.semantic.ontology.model.Sco;
import com.brainsoon.semantic.ontology.model.ScoList;
import com.brainsoon.semantic.ontology.model.SearchResultCa;
import com.brainsoon.system.support.SystemConstants.ResearchPlatResType;
import com.google.gson.Gson;

@Service
public class ResearchPlatService implements IResearchPlatService {
	private final static String JOURNAL_LIST_URL = WebappConfigUtil.getParameter("JOURNAL_LIST_URL");
	private final static String Article_RES_PUBCOLLECTION = WebappConfigUtil.getParameter("Article_RES_PUBCOLLECTION");
	private final static String SEARCH_ENTRY = WebappConfigUtil.getParameter("SEARCH_ENTRY");

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private ICrawlService crawlService;

	@Deprecated
	@Override
	public List<TreeNode> generateNavTreeFromMysql(String pid) {
		List<TreeNode> list = this.jdbcTemplate.query("select * from rp_nav_tree where pid=" + pid, new RowMapper<TreeNode>() {
			public TreeNode mapRow(ResultSet rs, int rowNum) throws SQLException {
				TreeNode node = new TreeNode();
				node.setId(rs.getString("id"));
				node.setPid(rs.getString("pid"));
				node.setName(rs.getString("name"));
				if (jdbcTemplate.queryForInt("select count(*) from rp_nav_tree where pid=" + rs.getInt("id")) > 0) {
					node.setIsParent(true);
				}
				return node;
			}
		});
		return list;
	}

	@Override
	public List<TreeNode> generateTreeNodesFromXML(String id, String value, String pValue) {
		SAXReader sax = new SAXReader();

		List<TreeNode> list = new ArrayList<TreeNode>();
		try {
			Document doc = sax.read(new File(this.getClass().getResource("/").getPath() + "NavTreeDef.xml"));
			TreeUtils.setDocument(doc);

			List<Element> selectNodes = null;
			if (StringUtils.isBlank(id) || id.equals("-1")) {
				selectNodes = doc.getRootElement().selectNodes("Node");
				for (Element e : selectNodes) {
					list.add(parseNode(e));
				}
			} else {
				TreeNode treeNode = new TreeNode();
				treeNode.setId(id);
				treeNode.setIsParent(true);
				treeNode.setValue(value);
				treeNode.setpValue(pValue);
				list = TreeUtils.populateNodes(treeNode);
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}

		return list;
	}

	/**
	 * 
	 * @param e
	 * @return
	 */
	private TreeNode parseNode(Element e) {
		TreeNode node = new TreeNode();
		node.setId(e.attributeValue("id").trim());
		node.setType(e.attributeValue("type").trim());
		node.setName(e.attributeValue("name").trim());
		node.setPid("-1");
		if (e.attribute("value") != null) {
			node.setValue(e.attributeValue("value").trim());
		}
		if (e.selectNodes("Node").size() > 0 || e.selectNodes("NodeList").size() > 0) {
			node.setIsParent(true);
		}
		node.setLabel(e.attributeValue("label").trim());
		return node;
	}

	@Override
	public DataTableResult queryResList(SearchParam searchParam) {
		DataTableResult dataTableResult = new DataTableResult();
		HttpClientUtil http = new HttpClientUtil();
		Gson gson = new Gson();
		String result = "";

		int page = 1;
		if (searchParam.getStart() >= 0) {
			page = (searchParam.getStart() / searchParam.getLength()) + 1;
		}
		if (StringUtils.isNotBlank(searchParam.getResType()) && searchParam.getResType().equals(ResearchPlatResType.TYPE0)) {
			result = http.executeGet(JOURNAL_LIST_URL + "?page=" + page + "&size=" + searchParam.getLength());
			SearchResultCa caList = gson.fromJson(result, SearchResultCa.class);
			if (caList != null) {
				List<Ca> cas = caList.getRows();
				if (cas != null && cas.size() > 0) {
					dataTableResult.setData(cas);
					dataTableResult.setDraw(searchParam.getDraw());
					dataTableResult.setRecordsTotal(new Long(caList.getTotal()).intValue());
					dataTableResult.setRecordsFiltered(new Long(caList.getTotal()).intValue());
				} else {
					dataTableResult.setData(new ArrayList<Ca>());
					dataTableResult.setDraw(searchParam.getDraw());
					dataTableResult.setRecordsTotal(0);
					dataTableResult.setRecordsFiltered(0);
				}
				return dataTableResult;
			}
		} else if (StringUtils.isNotBlank(searchParam.getResType()) && searchParam.getResType().equals(ResearchPlatResType.TYPE1)) {
			result = http.executeGet(Article_RES_PUBCOLLECTION + "?page=" + searchParam.getStart() + "&sum=" + searchParam.getLength());
			ScoList scolist = gson.fromJson(result, ScoList.class);
			if (scolist != null) {
				List<Sco> scos = scolist.getScos();
				if (scos != null && scos.size() > 0) {
					dataTableResult.setData(scos);
					dataTableResult.setDraw(searchParam.getDraw());
					dataTableResult.setRecordsTotal(new Long(scolist.getTotle()).intValue());
					dataTableResult.setRecordsFiltered(new Long(scolist.getTotle()).intValue());
				} else {
					dataTableResult.setData(new ArrayList<Sco>());
					dataTableResult.setDraw(searchParam.getDraw());
					dataTableResult.setRecordsTotal(0);
					dataTableResult.setRecordsFiltered(0);
				}
				return dataTableResult;
			}
		} else if (StringUtils.isNotBlank(searchParam.getResType()) && searchParam.getResType().equals(ResearchPlatResType.TYPE2)) {
			result = http.executeGet(SEARCH_ENTRY + "?page=" + page + "&pageSize=" + searchParam.getLength());
			EntryList entryList = gson.fromJson(result, EntryList.class);
			if (entryList != null) {
				List<Entry> entries = entryList.getRows();
				if (entries != null && entries.size() > 0) {
					dataTableResult.setData(entries);
					dataTableResult.setDraw(searchParam.getDraw());
					dataTableResult.setRecordsTotal(new Long(entryList.getTotal()).intValue());
					dataTableResult.setRecordsFiltered(new Long(entryList.getTotal()).intValue());
				} else {
					dataTableResult.setData(new ArrayList<Entry>());
					dataTableResult.setDraw(searchParam.getDraw());
					dataTableResult.setRecordsTotal(0);
					dataTableResult.setRecordsFiltered(0);
				}
			}
		} else if (StringUtils.isNotBlank(searchParam.getResType()) && searchParam.getResType().equals(ResearchPlatResType.TYPE4)) {
			PageInfo pageInfo = new PageInfo();
			pageInfo.setPage(page);
			pageInfo.setRows(searchParam.getLength());

			PageResult pageResult = crawlService.query(pageInfo, "", Long.parseLong(searchParam.getChannel()));

			if (pageResult != null) {
				List<CrawlResult> crawlResultList = pageResult.getRows();
				if (crawlResultList != null && crawlResultList.size() > 0) {
					dataTableResult.setData(crawlResultList);
					dataTableResult.setDraw(searchParam.getDraw());
					dataTableResult.setRecordsTotal(new Long(pageResult.getTotal()).intValue());
					dataTableResult.setRecordsFiltered(new Long(pageResult.getTotal()).intValue());
				} else {
					dataTableResult.setData(new ArrayList<CrawlResult>());
					dataTableResult.setDraw(searchParam.getDraw());
					dataTableResult.setRecordsTotal(0);
					dataTableResult.setRecordsFiltered(0);
				}
			}
		}
		return dataTableResult;
	}
}
