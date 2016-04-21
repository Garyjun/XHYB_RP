package com.brainsoon.crawler.job;

import java.util.List;

import com.brainsoon.common.service.IBaseService;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.crawler.core.pipeline.MysqlPipeline;
import com.brainsoon.crawler.core.processor.XHWPageProcessor;
import com.brainsoon.crawler.core.support.DepthScheduler;
import com.brainsoon.crawler.entity.CrawlChannel;

import us.codecraft.webmagic.Spider;

public class CrawlScheduleJob {

	/**
	 * 定时执行所有的爬取任务
	 * 
	 * @throws Exception
	 */
	public void excuteTaskList() throws Exception {
		IBaseService baseService = (IBaseService) BeanFactoryUtil.getBean("baseService");
		List<CrawlChannel> channelList = baseService.query("from CrawlChannel");
		for (CrawlChannel channel : channelList) {
			if (channel.getStatus().equals("0")) {
				Spider spider = Spider
						.create(new XHWPageProcessor().setChannel(channel))
						.scheduler(new DepthScheduler())
						.addUrl(channel.getDomain())
						.addPipeline(new MysqlPipeline())
						.thread(channel.getCrawlerThreadNum())
						.setExitWhenComplete(true);
				spider.start();
			}
		}
	}
}
