package com.brainsoon.crawler.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.brainsoon.common.po.BaseHibernateObject;

/**
 * 采集页结果集(CRAWL_RESULT)
 * 
 * @author xujie
 * @version 1.0.0 2016-01-13
 */
@Entity
@Table(name = "crawl_result")
public class CrawlResult extends BaseHibernateObject {
	/** 版本号 */
	private static final long serialVersionUID = 7085595093661774056L;

	/** 唯一标识 */
	@Id
	@GeneratedValue
	@Column(name = "ID", unique = true, nullable = false, length = 32)
	private Long id;

	/** 编号 */
	@Column(name = "SN", nullable = true, length = 32)
	private String sn;

	/** 频道ID */
	@ManyToOne
	private CrawlChannel channel;

	/** 页面地址 */
	@Column(name = "URL", nullable = true, length = 128)
	private String url;

	/** 文章标题 */
	@Column(name = "TITLE", nullable = true, length = 256)
	private String title;

	/** 文章作者 */
	@Column(name = "AUTHOR", nullable = true, length = 32)
	private String author;

	/** 文章内容 */
	@Column(name = "CONTENT", nullable = true, length = 65535)
	private String content;

	/** 发布时间 */

	@Column(name = "POSTIME", nullable = true)
	private String postime;
	
	/** 文章来源 */
	@Column(name = "SOURCE", nullable = true, length = 32)
	private String source;

	/** 点击数 */
	@Column(name = "CLICKS", nullable = true, length = 10)
	private Integer clicks;

	/** 回复数 */
	@Column(name = "REPLYS", nullable = true, length = 10)
	private Integer replys;

	/** 物理文件路径 */
	@Column(name = "FILE_PATH", nullable = true, length = 128)
	private String filePath;

	/** 状态 */
	@Column(name = "STATUS", nullable = true, length = 2)
	private String status;

	/** 创建时间 */

	@Column(name = "CREATE_TIME", nullable = true)
	private Date createTime;

	/** 更新时间 */

	@Column(name = "UPDATE_TIME", nullable = true)
	private Date updateTime;

	/**
	 * 获取唯一标识
	 * 
	 * @return 唯一标识
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * 设置唯一标识
	 * 
	 * @param id
	 *            唯一标识
	 */
	public void setId(Long id) {
		this.id = id;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	/**
	 * 获取页面地址
	 * 
	 * @return 页面地址
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * 设置页面地址
	 * 
	 * @param url
	 *            页面地址
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
	
	/**
	 * 获取文章标题
	 * 
	 * @return 文章标题
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * 设置文章标题
	 * 
	 * @param title
	 *            文章标题
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * 获取文章作者
	 * 
	 * @return 文章作者
	 */
	public String getAuthor() {
		return this.author;
	}

	/**
	 * 设置文章作者
	 * 
	 * @param author
	 *            文章作者
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * 获取文章内容
	 * 
	 * @return 文章内容
	 */
	public String getContent() {
		return this.content;
	}

	/**
	 * 设置文章内容
	 * 
	 * @param content
	 *            文章内容
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * 获取发布时间
	 * 
	 * @return 发布时间
	 */
	public String getPostime() {
		return this.postime;
	}

	/**
	 * 设置发布时间
	 * 
	 * @param postime
	 *            发布时间
	 */
	public void setPostime(String postime) {
		this.postime = postime;
	}

	/**
	 * 获取点击数
	 * 
	 * @return 点击数
	 */
	public Integer getClicks() {
		return this.clicks;
	}

	/**
	 * 设置点击数
	 * 
	 * @param clicks
	 *            点击数
	 */
	public void setClicks(Integer clicks) {
		this.clicks = clicks;
	}

	/**
	 * 获取回复数
	 * 
	 * @return 回复数
	 */
	public Integer getReplys() {
		return this.replys;
	}

	/**
	 * 设置回复数
	 * 
	 * @param replys
	 *            回复数
	 */
	public void setReplys(Integer replys) {
		this.replys = replys;
	}

	/**
	 * 获取物理文件路径
	 * 
	 * @return 物理文件路径
	 */
	public String getFilePath() {
		return this.filePath;
	}

	/**
	 * 设置物理文件路径
	 * 
	 * @param filePath
	 *            物理文件路径
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	/**
	 * 获取状态
	 * 
	 * @return 状态
	 */
	public String getStatus() {
		return this.status;
	}

	/**
	 * 设置状态
	 * 
	 * @param status
	 *            状态
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * 获取创建时间
	 * 
	 * @return 创建时间
	 */
	public Date getCreateTime() {
		return this.createTime;
	}

	/**
	 * 设置创建时间
	 * 
	 * @param createTime
	 *            创建时间
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public CrawlChannel getChannel() {
		return channel;
	}

	public void setChannel(CrawlChannel channel) {
		this.channel = channel;
	}

	/**
	 * 获取更新时间
	 * 
	 * @return 更新时间
	 */
	public Date getUpdateTime() {
		return this.updateTime;
	}

	/**
	 * 设置更新时间
	 * 
	 * @param updateTime
	 *            更新时间
	 */
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public String getObjectDescription() {
		return "采集结果集：" + getTitle();
	}

	@Override
	public String getEntityDescription() {
		return "采集结果集";
	}
}