package com.brainsoon.crawler.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.brainsoon.common.po.BaseHibernateObject;

/**
 * (crawl_filterLog)
 * 
 * @version 1.0.0 2016-01-12
 */
@Entity
@Table(name = "crawl_filterLog")
public class CrawlFilter extends BaseHibernateObject implements java.io.Serializable {
    /** 版本号 */
    private static final long serialVersionUID = 3355647187823510547L;
    
    /** 频道id */
    @Id
    @GeneratedValue
    @Column(name = "id", unique = true, nullable = false, length = 11)
    private Long id;
    
    /** wp1 */
    @Column(name = "wp1", nullable = true, length = 11)
    private String wp1;
    
    /** wp2 */
    @Column(name = "wp2", nullable = true, length = 11)
    private String wp2;
    
    /** 描述1 */
    @Column(name = "description1", nullable = true, length = 32)
    private String description1;
    
    /** 描述2 */
    @Column(name = "description2", nullable = true, length = 32)
    private String description2;
    
    /** 站点域名 */
    @Column(name = "url1", nullable = true, length = 64)
    private String url1;
    
    /** 站点域名 */
    @Column(name = "url2", nullable = true, length = 64)
    private String url2;
    
    /** 名称1 */
    @Column(name = "siteName1", nullable = true, length = 64)
    private String siteName1;
    
    /** 名称2 */
    @Column(name = "siteName2", nullable = true, length = 64)
    private String siteName2;
    
    /** 频道1*/
    @Column(name = "channelName1", nullable = true, length = 64)
    private String channelName1;
    
    /** 频道2*/
    @Column(name = "channelName2", nullable = true, length = 64)
    private String channelName2;
    
    /** 文章内容正则匹配 */
    @Column(name = "time", nullable = true, length = 64)
    private int time;
    
    /** 相似度 */
    @Column(name = "similar", nullable = true, length = 32)
    private String similar;
   
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getWp1() {
		return wp1;
	}

	public void setWp1(String wp1) {
		this.wp1 = wp1;
	}

	public String getWp2() {
		return wp2;
	}

	public void setWp2(String wp2) {
		this.wp2 = wp2;
	}

	public String getDescription1() {
		return description1;
	}

	public void setDescription1(String description1) {
		this.description1 = description1;
	}

	public String getDescription2() {
		return description2;
	}

	public void setDescription2(String description2) {
		this.description2 = description2;
	}

	public String getUrl1() {
		return url1;
	}

	public void setUrl1(String url1) {
		this.url1 = url1;
	}

	public String getUrl2() {
		return url2;
	}

	public void setUrl2(String url2) {
		this.url2 = url2;
	}

	public String getSiteName1() {
		return siteName1;
	}

	public void setSiteName1(String siteName1) {
		this.siteName1 = siteName1;
	}

	public String getSiteName2() {
		return siteName2;
	}

	public void setSiteName2(String siteName2) {
		this.siteName2 = siteName2;
	}

	public String getChannelName1() {
		return channelName1;
	}

	public void setChannelName1(String channelName1) {
		this.channelName1 = channelName1;
	}

	public String getChannelName2() {
		return channelName2;
	}

	public void setChannelName2(String channelName2) {
		this.channelName2 = channelName2;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public String getSimilar() {
		return similar;
	}

	public void setSimilar(String similar) {
		this.similar = similar;
	}

	@Override
	public String getObjectDescription() {
		return null;
	}

	@Override
	public String getEntityDescription() {
		return null;
	}
}