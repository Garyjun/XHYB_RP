/*
SQLyog Ultimate v11.11 (64 bit)
MySQL - 5.0.67-community-nt : Database - bsrcm_xhyb
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`bsrcm_xhyb` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `bsrcm_xhyb`;

/*Table structure for table `crawl_channel` */

CREATE TABLE `crawl_channel` (
  `id` int(11) NOT NULL auto_increment COMMENT '频道id',
  `pid` int(11) default NULL COMMENT '父频道id',
  `name` varchar(32) default NULL COMMENT '频道名称',
  `domain` varchar(64) default NULL COMMENT '站点域名',
  `regex_site` varchar(128) default NULL COMMENT '频道地址',
  `regex_title` varchar(64) default NULL COMMENT '文章标题正则匹配',
  `regex_content` varchar(64) default NULL COMMENT '文章内容正则匹配',
  `regex_author` varchar(32) default NULL COMMENT '文章作者正则匹配',
  `regex_postime` varchar(16) default NULL COMMENT '文章发布时间',
  `crawler_charset` varchar(8) default NULL COMMENT '爬虫字符集',
  `crawler_proxy_host` varchar(16) default NULL COMMENT '爬虫代理主机地址',
  `crawler_proxy_port` varchar(8) default NULL COMMENT '爬虫代理端口号',
  `crawler_proxy_user` varchar(16) default NULL COMMENT '爬虫代理账户',
  `crawler_proxy_pass` varchar(32) default NULL COMMENT '爬虫代理密码',
  `crawler_down_interval` varchar(16) default NULL COMMENT '爬虫下载间隔时间',
  `crawler_http_headers` varchar(256) default NULL COMMENT '爬虫UserAgent头信息',
  `crawler_http_cookies` varchar(256) default NULL COMMENT '爬虫cookies键值对，用","分隔：${id}=${value},${id}=${value}',
  `crawler_http_paras` varchar(256) default NULL COMMENT '爬虫URL参数键值对，用","分隔：${name}=${value},${name}=${value}',
  `crawler_retry_down_times` varchar(2) default NULL COMMENT '爬虫重新尝试下载次数',
  `crawler_retry_url_times` varchar(2) default NULL COMMENT '爬虫URL尝试抓取次数',
  `crawler_thread_num` tinyint(4) NOT NULL default '3' COMMENT '爬虫线程数',
  `total_pages` int(11) default NULL COMMENT '爬取总页数',
  `error_pages` int(11) default NULL COMMENT '爬取错误页数',
  `status` varchar(1) default NULL COMMENT '频道状态：0-未启动 1-正在运行 2-异常停止',
  `create_time` datetime default NULL COMMENT '创建时间',
  `update_time` datetime default NULL COMMENT '更新时间',
  `create_user` varchar(16) default NULL COMMENT '创建人',
  `update_user` varchar(16) default NULL COMMENT '修改人',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8 COMMENT='站点频道';

/*Table structure for table `crawl_result` */

CREATE TABLE `crawl_result` (
  `id` int(32) NOT NULL auto_increment COMMENT '唯一标识',
  `sn` varchar(32) default NULL COMMENT '编号',
  `channel_id` int(11) default NULL COMMENT '频道ID',
  `url` varchar(128) default NULL COMMENT '页面地址',
  `title` varchar(256) default NULL COMMENT '文章标题',
  `author` varchar(32) default NULL COMMENT '文章作者',
  `content` text COMMENT '文章内容',
  `postime` datetime default NULL COMMENT '发布时间',
  `clicks` int(11) default NULL COMMENT '点击数',
  `replys` int(11) default NULL COMMENT '回复数',
  `file_path` varchar(128) default NULL COMMENT '物理文件路径',
  `status` varchar(2) default NULL COMMENT '状态',
  `create_time` datetime default NULL COMMENT '创建时间',
  `update_time` datetime default NULL COMMENT '更新时间',
  PRIMARY KEY  (`id`),
  KEY `channel_id` (`channel_id`),
  CONSTRAINT `crawl_result_ibfk_1` FOREIGN KEY (`channel_id`) REFERENCES `crawl_channel` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='采集结果集';

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
