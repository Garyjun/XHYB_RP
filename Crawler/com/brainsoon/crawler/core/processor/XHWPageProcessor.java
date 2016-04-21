package com.brainsoon.crawler.core.processor;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.brainsoon.crawler.entity.CrawlChannel;
import com.brainsoon.crawler.utils.DateUtils;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.SmartContentSelector;

/**
 * 新华网
 * 
 * @author xujie
 */
public class XHWPageProcessor implements PageProcessor {

	Logger logger = Logger.getLogger(XHWPageProcessor.class);
	
	private Site site = Site.me()
			.setRetryTimes(3)
			.setSleepTime(3000)
			.setUserAgent(
			"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");;

	private CrawlChannel channel;

	public void process(Page page) {
		logger.debug("fetch url >>>：" + page.getUrl());
		
		String matchUrlRegex = getMatchUrlRegex(channel.getRegexSite());
		String title = page.getHtml().xpath(channel.getRuleTitle()).get();
		String posTime = page.getHtml().xpath(channel.getRulePostime()).get();
		String source = page.getHtml().xpath(channel.getRuleSource()).get();
		page.putField("title", title);
		page.putField("postime", posTime);
		page.putField("source", source);
		String content = new SmartContentSelector().select(page.getRawText());
		if (isSkip(page, matchUrlRegex, title, content)) {
			page.setSkip(true);
		}
		page.putField("content", content);
		page.putField("channel", channel);
		page.addTargetRequests(page.getHtml().links().regex(matchUrlRegex).all());
		
	}
	/**
	 * 页面过滤：标题为空、url不匹配、内容为空
	 * @param page
	 * @param matchUrlRegex
	 * @param title
	 * @param content
	 * @return
	 */
	private boolean isSkip(Page page, String matchUrlRegex, String title, String content) {
		return !page.getUrl().regex(matchUrlRegex).match()||StringUtils.isBlank(content)||StringUtils.isBlank(title);
	}

	private String getMatchUrlRegex(String regexStr) {
		regexStr = StringUtils.replace(regexStr, "${year}", DateUtils.getYearByString());
		regexStr = StringUtils.replace(regexStr, "${month}", DateUtils.getMonthByString());
		regexStr = StringUtils.replace(regexStr, "${day}", DateUtils.getDayByString());
		return regexStr;
	}

	@Override
	public Site getSite() {
		return this.site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	public CrawlChannel getChannel() {
		return channel;
	}

	public XHWPageProcessor setChannel(CrawlChannel channel) {
		this.channel = channel;
		return this;
	}

}
