package com.brainsoon.crawler.service;

import java.util.List;

import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.pagination.PageInfo;
import com.brainsoon.crawler.entity.CrawlChannel;
import com.brainsoon.crawler.entity.CrawlFilter;
import com.brainsoon.crawler.entity.CrawlResult;

public interface ICrawlService {

	List<CrawlChannel> fetchCrawlChannelTree();

	/**
	 * 分页查询模块
	 * 
	 * @param pageInfo
	 * @param module
	 * @return
	 */
	public PageResult query(PageInfo pageInfo, String title,Long channelId) throws ServiceException;

	/**
	 * 
	 * @param channelId  
	 * @return
	 */
	void start(Long channelId);

	List<CrawlResult> queryCrawlResults(String channelId, String startHour);
	/**
	 * 查询采集过滤
	 * @return
	 */
	public PageResult queryCrawFilter(PageInfo info,String similar);

}
