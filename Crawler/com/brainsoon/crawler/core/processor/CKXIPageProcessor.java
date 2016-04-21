package com.brainsoon.crawler.core.processor;

import java.util.List;

import javax.management.JMException;

import com.brainsoon.crawler.core.pipeline.MysqlPipeline;
import com.brainsoon.crawler.core.support.DepthScheduler;
import com.brainsoon.crawler.entity.CrawlChannel;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.SmartContentSelector;

/**
 * Created by Administrator on 2015/10/16.
 */
public class CKXIPageProcessor implements PageProcessor {

	private Site site = Site.me().setRetryTimes(3).setSleepTime(1000).setUserAgent(
			"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");;

	private CrawlChannel channel;

	public void process(Page page) {
		System.out.println("抓取链接 >>>：" + page.getUrl());

		page.putField("title", page.getHtml().xpath("//html/head/title/text()").get());
		// page.putField("content", page.getHtml().get());
		page.putField("content", new SmartContentSelector().select(page.getRawText()));
		page.addTargetRequests(page.getHtml().links().regex("http://www.cankaoxiaoxi.com/.*").all());
	}

	@SuppressWarnings("unused")
	private void iterPrintUrl(List<String> all) {
		System.out.println("页面链接总数：" + all.size());
		for (String s : all) {
			System.out.println("URL:>>> " + s);
		}
	}

	public CrawlChannel getChannel() {
		return channel;
	}

	public void setChannel(CrawlChannel channel) {
		this.channel = channel;
	}

	public Site getSite() {
		return this.site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	public static void main(String[] args) throws JMException {
		Spider CKXISpider = Spider.create(new CKXIPageProcessor())
				.scheduler(new DepthScheduler())
				.addUrl("http://www.cankaoxiaoxi.com")
				.addPipeline(new MysqlPipeline())
				.thread(10);
		//SpiderMonitor.instance().register(CKXISpider);
		CKXISpider.run();
	}

}
