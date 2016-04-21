package com.brainsoon.crawler.entity;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.brainsoon.common.po.BaseHibernateObject;

/**
 * 站点频道(CRAWL_CHANNEL)
 * 
 * @version 1.0.0 2016-01-12
 */
@Entity
@Table(name = "crawl_channel")
public class CrawlChannel extends BaseHibernateObject implements java.io.Serializable {
    /** 版本号 */
    private static final long serialVersionUID = 3355647187823510547L;
    
    /** 频道id */
    @Id
    @GeneratedValue
    @Column(name = "ID", unique = true, nullable = false, length = 11)
    private Long id;
    
    /** 父频道id */
    @Column(name = "PID", nullable = true, length = 11)
    private Long pid;
    
    /** 频道名称 */
    @Column(name = "NAME", nullable = true, length = 32)
    private String name;
    
    /** 站点域名 */
    @Column(name = "DOMAIN", nullable = true, length = 64)
    private String domain;
    
    /** 频道地址 */
    @Column(name = "REGEX_SITE", nullable = true, length = 64)
    private String regexSite;
    
    /** 文章标题正则匹配 */
    @Column(name = "RULE_TITLE", nullable = true, length = 64)
    private String ruleTitle;
    
    /** 文章内容正则匹配 */
    @Column(name = "RULE_CONTENT", nullable = true, length = 64)
    private String ruleContent;
    
    /** 文章作者正则匹配 */
    @Column(name = "RULE_AUTHOR", nullable = true, length = 32)
    private String ruleAuthor;
    
    /** 文章发布时间 */
    @Column(name = "RULE_POSTIME", nullable = true, length = 16)
    private String rulePostime;
    
    /** 文章发布时间 */
    @Column(name = "RULE_SOURCE", nullable = true, length = 32)
    private String ruleSource;
    
    /** 爬虫字符集 */
    @Column(name = "CRAWLER_CHARSET", nullable = true, length = 8)
    private String crawlerCharset;
    
    /** 爬虫代理主机地址 */
    @Column(name = "CRAWLER_PROXY_HOST", nullable = true, length = 16)
    private String crawlerProxyHost;
    
    /** 爬虫代理端口号 */
    @Column(name = "CRAWLER_PROXY_PORT", nullable = false, length = 8)
    private String crawlerProxyPort;
    
    /** 爬虫代理账户 */
    @Column(name = "CRAWLER_PROXY_USER", nullable = true, length = 16)
    private String crawlerProxyUser;
    
    /** 爬虫代理密码 */
    @Column(name = "CRAWLER_PROXY_PASS", nullable = true, length = 32)
    private String crawlerProxyPass;
    
    /** 爬虫下载间隔时间 */
    @Column(name = "CRAWLER_DOWN_INTERVAL", nullable = true, length = 16)
    private String crawlerDownInterval;
    
    /** 爬虫UserAgent头信息 */
    @Column(name = "CRAWLER_HTTP_HEADERS", nullable = true, length = 256)
    private String crawlerHttpHeaders;
    
    /** 爬虫cookies键值对，用","分隔：${id}=${value},${id}=${value} */
    @Column(name = "CRAWLER_HTTP_COOKIES", nullable = true, length = 256)
    private String crawlerHttpCookies;
    
    /** 爬虫URL参数键值对，用","分隔：${name}=${value},${name}=${value} */
    @Column(name = "CRAWLER_HTTP_PARAS", nullable = true, length = 256)
    private String crawlerHttpParas;
    
    /** 爬虫重新尝试下载次数 */
    @Column(name = "CRAWLER_RETRY_DOWN_TIMES", nullable = true, length = 2)
    private String crawlerRetryDownTimes;
    
    /** 爬虫URL尝试抓取次数 */
    @Column(name = "CRAWLER_RETRY_URL_TIMES", nullable = true, length = 2)
    private String crawlerRetryUrlTimes;
    
    /** 爬虫线程数 */
    @Column(name = "CRAWLER_THREAD_NUM", nullable = false, length = 3)
    private Integer crawlerThreadNum;
    
    /** 爬取总页数 */
    @Column(name = "TOTAL_PAGES", nullable = true, length = 10)
    private Integer totalPages;
    
    /** 爬取错误页数 */
    @Column(name = "ERROR_PAGES", nullable = true, length = 10)
    private Integer errorPages;
    
    /** 频道状态：0-未启动 1-正在运行 2-异常停止 */
    @Column(name = "STATUS", nullable = true, length = 1)
    private String status;
    
    /** 创建时间 */
    
    @Column(name = "CREATE_TIME", nullable = true)
    private Date createTime;
    
    /** 更新时间 */
    
    @Column(name = "UPDATE_TIME", nullable = true)
    private Date updateTime;
    
    /** 创建人 */
    @Column(name = "CREATE_USER", nullable = true, length = 16)
    private String createUser;
    
    /** 修改人 */
    @Column(name = "UPDATE_USER", nullable = true, length = 16)
    private String updateUser;
    
    /**
     * 获取频道id
     * 
     * @return 频道id
     */
    public Long getId() {
        return this.id;
    }
     
    /**
     * 设置频道id
     * 
     * @param id
     *          频道id
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * 获取父频道id
     * 
     * @return 父频道id
     */
    public Long getPid() {
        return this.pid;
    }
     
    /**
     * 设置父频道id
     * 
     * @param pid
     *          父频道id
     */
    public void setPid(Long pid) {
        this.pid = pid;
    }
    
    /**
     * 获取频道名称
     * 
     * @return 频道名称
     */
    public String getName() {
        return this.name;
    }
     
    /**
     * 设置频道名称
     * 
     * @param name
     *          频道名称
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * 获取站点域名
     * 
     * @return 站点域名
     */
    public String getDomain() {
        return this.domain;
    }
     
    /**
     * 设置站点域名
     * 
     * @param domain
     *          站点域名
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }
    
    /**
     * 获取频道地址
     * 
     * @return 频道地址
     */
    public String getRegexSite() {
        return this.regexSite;
    }
     
    /**
     * 设置频道地址
     * 
     * @param site
     *          频道地址
     */
    public void setRegexSite(String regexSite) {
        this.regexSite = regexSite;
    }
    
    public String getRuleTitle() {
		return ruleTitle;
	}

	public void setRuleTitle(String ruleTitle) {
		this.ruleTitle = ruleTitle;
	}

	public String getRuleContent() {
		return ruleContent;
	}

	public void setRuleContent(String ruleContent) {
		this.ruleContent = ruleContent;
	}

	public String getRuleAuthor() {
		return ruleAuthor;
	}

	public void setRuleAuthor(String ruleAuthor) {
		this.ruleAuthor = ruleAuthor;
	}

	public String getRulePostime() {
		return rulePostime;
	}

	public void setRulePostime(String rulePostime) {
		this.rulePostime = rulePostime;
	}

	public String getRuleSource() {
		return ruleSource;
	}

	public void setRuleSource(String ruleSource) {
		this.ruleSource = ruleSource;
	}

	/**
     * 获取爬虫字符集
     * 
     * @return 爬虫字符集
     */
    public String getCrawlerCharset() {
        return this.crawlerCharset;
    }
     
    /**
     * 设置爬虫字符集
     * 
     * @param crawlerCharset
     *          爬虫字符集
     */
    public void setCrawlerCharset(String crawlerCharset) {
        this.crawlerCharset = crawlerCharset;
    }
    
    /**
     * 获取爬虫代理主机地址
     * 
     * @return 爬虫代理主机地址
     */
    public String getCrawlerProxyHost() {
        return this.crawlerProxyHost;
    }
     
    /**
     * 设置爬虫代理主机地址
     * 
     * @param crawlerProxyHost
     *          爬虫代理主机地址
     */
    public void setCrawlerProxyHost(String crawlerProxyHost) {
        this.crawlerProxyHost = crawlerProxyHost;
    }
    
    /**
     * 获取爬虫代理端口号
     * 
     * @return 爬虫代理端口号
     */
    public String getCrawlerProxyPort() {
        return this.crawlerProxyPort;
    }
     
    /**
     * 设置爬虫代理端口号
     * 
     * @param crawlerProxyPort
     *          爬虫代理端口号
     */
    public void setCrawlerProxyPort(String crawlerProxyPort) {
        this.crawlerProxyPort = crawlerProxyPort;
    }
    
    /**
     * 获取爬虫代理账户
     * 
     * @return 爬虫代理账户
     */
    public String getCrawlerProxyUser() {
        return this.crawlerProxyUser;
    }
     
    /**
     * 设置爬虫代理账户
     * 
     * @param crawlerProxyUser
     *          爬虫代理账户
     */
    public void setCrawlerProxyUser(String crawlerProxyUser) {
        this.crawlerProxyUser = crawlerProxyUser;
    }
    
    /**
     * 获取爬虫代理密码
     * 
     * @return 爬虫代理密码
     */
    public String getCrawlerProxyPass() {
        return this.crawlerProxyPass;
    }
     
    /**
     * 设置爬虫代理密码
     * 
     * @param crawlerProxyPass
     *          爬虫代理密码
     */
    public void setCrawlerProxyPass(String crawlerProxyPass) {
        this.crawlerProxyPass = crawlerProxyPass;
    }
    
    /**
     * 获取爬虫下载间隔时间
     * 
     * @return 爬虫下载间隔时间
     */
    public String getCrawlerDownInterval() {
        return this.crawlerDownInterval;
    }
     
    /**
     * 设置爬虫下载间隔时间
     * 
     * @param crawlerDownInterval
     *          爬虫下载间隔时间
     */
    public void setCrawlerDownInterval(String crawlerDownInterval) {
        this.crawlerDownInterval = crawlerDownInterval;
    }
    
    /**
     * 获取爬虫UserAgent头信息
     * 
     * @return 爬虫UserAgent头信息
     */
    public String getCrawlerHttpHeaders() {
        return this.crawlerHttpHeaders;
    }
     
    /**
     * 设置爬虫UserAgent头信息
     * 
     * @param crawlerHttpHeaders
     *          爬虫UserAgent头信息
     */
    public void setCrawlerHttpHeaders(String crawlerHttpHeaders) {
        this.crawlerHttpHeaders = crawlerHttpHeaders;
    }
    
    /**
     * 获取爬虫cookies键值对，用","分隔：${id}=${value},${id}=${value}
     * 
     * @return 爬虫cookies键值对
     */
    public String getCrawlerHttpCookies() {
        return this.crawlerHttpCookies;
    }
     
    /**
     * 设置爬虫cookies键值对，用","分隔：${id}=${value},${id}=${value}
     * 
     * @param crawlerHttpCookies
     *          爬虫cookies键值对，用","分隔：${id}=${value},${id}=${value}
     */
    public void setCrawlerHttpCookies(String crawlerHttpCookies) {
        this.crawlerHttpCookies = crawlerHttpCookies;
    }
    
    /**
     * 获取爬虫URL参数键值对，用","分隔：${name}=${value},${name}=${value}
     * 
     * @return 爬虫URL参数键值对
     */
    public String getCrawlerHttpParas() {
        return this.crawlerHttpParas;
    }
     
    /**
     * 设置爬虫URL参数键值对，用","分隔：${name}=${value},${name}=${value}
     * 
     * @param crawlerHttpParas
     *          爬虫URL参数键值对，用","分隔：${name}=${value},${name}=${value}
     */
    public void setCrawlerHttpParas(String crawlerHttpParas) {
        this.crawlerHttpParas = crawlerHttpParas;
    }
    
    /**
     * 获取爬虫重新尝试下载次数
     * 
     * @return 爬虫重新尝试下载次数
     */
    public String getCrawlerRetryDownTimes() {
        return this.crawlerRetryDownTimes;
    }
     
    /**
     * 设置爬虫重新尝试下载次数
     * 
     * @param crawlerRetryDownTimes
     *          爬虫重新尝试下载次数
     */
    public void setCrawlerRetryDownTimes(String crawlerRetryDownTimes) {
        this.crawlerRetryDownTimes = crawlerRetryDownTimes;
    }
    
    /**
     * 获取爬虫URL尝试抓取次数
     * 
     * @return 爬虫URL尝试抓取次数
     */
    public String getCrawlerRetryUrlTimes() {
        return this.crawlerRetryUrlTimes;
    }
     
    /**
     * 设置爬虫URL尝试抓取次数
     * 
     * @param crawlerRetryUrlTimes
     *          爬虫URL尝试抓取次数
     */
    public void setCrawlerRetryUrlTimes(String crawlerRetryUrlTimes) {
        this.crawlerRetryUrlTimes = crawlerRetryUrlTimes;
    }
    
    /**
     * 获取爬虫线程数
     * 
     * @return 爬虫线程数
     */
    public Integer getCrawlerThreadNum() {
        return this.crawlerThreadNum;
    }
     
    /**
     * 设置爬虫线程数
     * 
     * @param crawlerThreadNum
     *          爬虫线程数
     */
    public void setCrawlerThreadNum(Integer crawlerThreadNum) {
        this.crawlerThreadNum = crawlerThreadNum;
    }
    
    /**
     * 获取爬取总页数
     * 
     * @return 爬取总页数
     */
    public Integer getTotalPages() {
        return this.totalPages;
    }
     
    /**
     * 设置爬取总页数
     * 
     * @param totalPages
     *          爬取总页数
     */
    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }
    
    /**
     * 获取爬取错误页数
     * 
     * @return 爬取错误页数
     */
    public Integer getErrorPages() {
        return this.errorPages;
    }
     
    /**
     * 设置爬取错误页数
     * 
     * @param errorPages
     *          爬取错误页数
     */
    public void setErrorPages(Integer errorPages) {
        this.errorPages = errorPages;
    }
    
    /**
     * 获取频道状态：0-未启动 1-正在运行 2-异常停止
     * 
     * @return 频道状态
     */
    public String getStatus() {
        return this.status;
    }
     
    /**
     * 设置频道状态：0-未启动 1-正在运行 2-异常停止
     * 
     * @param status
     *          频道状态：0-未启动 1-正在运行 2-异常停止
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
     *          创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
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
     *          更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
    
    /**
     * 获取创建人
     * 
     * @return 创建人
     */
    public String getCreateUser() {
        return this.createUser;
    }
     
    /**
     * 设置创建人
     * 
     * @param createUser
     *          创建人
     */
    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }
    
    /**
     * 获取修改人
     * 
     * @return 修改人
     */
    public String getUpdateUser() {
        return this.updateUser;
    }
     
    /**
     * 设置修改人
     * 
     * @param updateUser
     *          修改人
     */
    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
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