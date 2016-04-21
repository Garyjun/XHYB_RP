package com.brainsoon.crawler.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.action.Token;
import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.common.support.Constants;
import com.brainsoon.crawler.entity.CrawlChannel;
import com.brainsoon.crawler.entity.CrawlFilter;
import com.brainsoon.crawler.entity.CrawlResult;
import com.brainsoon.crawler.service.ICrawlService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CrawlerController extends BaseAction {

	private static final String baseUrl = "/crawler/";

	@Autowired
	private ICrawlService crawlService;

	/**
	 * Get crawler channelTree value
	 * 
	 * @return xujie
	 * @throws Exception
	 */
	@RequestMapping(baseUrl + "channelTree")
	@Token(save = true)
	public @ResponseBody String channelTree() throws Exception {
		JSONArray array = new JSONArray();
		List<CrawlChannel> list = crawlService.fetchCrawlChannelTree();
		if (list != null) {
			for (CrawlChannel channel : list) {
				JSONObject jsonChild = new JSONObject();
				jsonChild.put("id", channel.getId());
				jsonChild.put("pId", channel.getPid());
				jsonChild.put("name", channel.getName());
				array.add(jsonChild);
			}
		}
		return array.toString();
	}

	@RequestMapping(baseUrl + "task_edit")
	@Token(save = true)
	public String toEdit(Model model, @RequestParam String id) {
		// CrawlTask task = null;
		if (!model.containsAttribute(Constants.COMMAND)) {
			if (org.apache.commons.lang3.StringUtils.isBlank(id)) {
				// task = new CrawlTask();
			} else {
				// task = crawlService.getCrawlTaskById(id);
			}
		}
		model.addAttribute(Constants.ID, id);
		// model.addAttribute(Constants.COMMAND, task);
		return "crawler/task_edit";
	}

	@RequestMapping(baseUrl + "query")
	@ResponseBody
	public PageResult query(@RequestParam(required = false) String title, @RequestParam(required = false) Long channelId) {
		return crawlService.query(getPageInfo(), title, channelId);
	}

	/**
	 * start crawler task
	 * 
	 * @return xujie
	 * @throws Exception
	 */
	@RequestMapping(baseUrl + "start")
	@Token(save = true)
	public @ResponseBody String start(@RequestParam String channelId) throws Exception {
		JSONArray array = new JSONArray();
		crawlService.start(Long.parseLong(channelId));
		return array.toString();
	}

	/**
	 * 
	 * @param channelId
	 *            频道ID
	 * @param startHour
	 *            开始小时 如 “9” 表示只查询 9-10点之间的爬取结果
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(baseUrl + "getCrawlResults")
	@Token(save = true)
	public @ResponseBody String getCrawlResults(String channelId, String startHour) throws Exception {
		JSONArray array = new JSONArray();
		List<CrawlResult> list = crawlService.queryCrawlResults(channelId, startHour);
		if (list != null) {
			for (CrawlResult result : list) {
				JSONObject jsonChild = new JSONObject();
				jsonChild.put("domain", result.getChannel().getDomain());
				jsonChild.put("channel_id", result.getChannel().getId());
				jsonChild.put("title", result.getTitle());
				jsonChild.put("file_path", result.getFilePath());
				jsonChild.put("post_time", result.getPostime());
				jsonChild.put("author", result.getAuthor());
				jsonChild.put("create_time", result.getCreateTime());
				array.add(jsonChild);
			}
		}
		return array.toString();
	}
	@RequestMapping(baseUrl + "queryCrawFilter")
	@ResponseBody
	public PageResult queryCrawFilter(HttpServletRequest request,@RequestParam(required = false) String similar) {
		return crawlService.queryCrawFilter(getPageInfo(), similar);
	}
}
