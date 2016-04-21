package com.brainsoon.crawler.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import us.codecraft.webmagic.Spider;

import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.pagination.PageInfo;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.crawler.core.pipeline.MysqlPipeline;
import com.brainsoon.crawler.core.processor.XHWPageProcessor;
import com.brainsoon.crawler.core.support.DepthScheduler;
import com.brainsoon.crawler.entity.CrawlChannel;
import com.brainsoon.crawler.entity.CrawlFilter;
import com.brainsoon.crawler.entity.CrawlResult;
import com.brainsoon.crawler.service.ICrawlService;
import com.google.gson.Gson;

@Service
public class CrawlService extends BaseService implements ICrawlService {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public List<CrawlChannel> fetchCrawlChannelTree() {
		List<CrawlChannel> list = this.query("from CrawlChannel");
		return list;
	}

	/**
	 * 分页查询模块
	 * 
	 * @param pageInfo
	 * @param result
	 * @return
	 */
	public PageResult query(PageInfo pageInfo, String title, Long channelId) throws ServiceException {
		String hql = " from CrawlResult where 1=1";
		if (StringUtils.isNotBlank(title)) {
			hql += " and title like =%" + title + "%";
		}

		if (channelId != null) {
			hql += " and channel.id =" + channelId;
		}

		Map<String, Object> params = new HashMap<String, Object>();
		hql = hql + " order by id desc ";
		try {
			baseDao.query4Page(hql, pageInfo, params);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
		return pageInfo.getPageResult();
	}

	@Override
	public void start(Long channelId) {
		CrawlChannel channel = (CrawlChannel) this.getByPk(CrawlChannel.class, channelId);
		Spider spider = Spider
				.create(new XHWPageProcessor().setChannel(channel))
				.scheduler(new DepthScheduler())
				.addUrl(channel.getDomain())
				.addPipeline(new MysqlPipeline())
				.thread(channel.getCrawlerThreadNum())
				.setExitWhenComplete(true);
		spider.start();
	}
	

	@Override
	public List<CrawlResult> queryCrawlResults(String channelId, String startHour) {
		String hql = " from CrawlResult where 1=1";
		if (StringUtils.isNotBlank(startHour)) {
			hql += " and createTime";
		}

		if (channelId != null) {
			hql += " and channel.id =" + channelId;
		}
	
		return null;
	}

	@Override
	public PageResult queryCrawFilter(PageInfo pageInfo,String similar) {
		String hql = "from CrawlFilter where 1=1";
		
		if(StringUtils.isNotBlank(similar)){
			hql += " and similar>"+similar;
		}
		Map<String, Object> params = new HashMap<String, Object>();
		hql = hql + " order by id desc ";
		baseDao.query4Page(hql, pageInfo, params);
		return pageInfo.getPageResult();
	}
}
