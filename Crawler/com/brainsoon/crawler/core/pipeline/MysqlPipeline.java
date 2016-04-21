package com.brainsoon.crawler.core.pipeline;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Date;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import com.brainsoon.common.service.IBaseService;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.crawler.entity.CrawlResult;
import com.brainsoon.crawler.utils.DateUtils;
import com.brainsoon.crawler.utils.ReflectionUtils;

import sun.misc.Launcher;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

public class MysqlPipeline implements Pipeline {

	private Logger logger = LoggerFactory.getLogger(getClass());
	private static IBaseService baseService;
	private static JdbcTemplate jdbcTemplate;
	static{
		try {
			jdbcTemplate = (JdbcTemplate)BeanFactoryUtil.getBean("jdbcTemplate");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void process(ResultItems resultItems, Task task) {
		
		String urlCode = DigestUtils.md5Hex(resultItems.getRequest().getUrl()) + ".html";
		if(hasUrlCode(urlCode)){
			return;
		}
		CrawlResult result = new CrawlResult();
		result.setUrl(resultItems.getRequest().getUrl());

		Map<String, Object> map = resultItems.getAll();
		Field[] fields = result.getClass().getDeclaredFields();
		try {
			for (Field field : fields) {
				if (map.get(field.getName()) != null) {
					Method m = (Method) result.getClass()
							.getMethod("set" + ReflectionUtils.getMethodName(field.getName()), field.getType());
					m.invoke(result, map.get(field.getName()));
				}
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			baseService = (IBaseService) BeanFactoryUtil.getBean("baseService");
			result.setSn(DateUtils.getTimestampStr());
			// result.setChannel(channel);
			result.setCreateTime(new Date());
			result.setUpdateTime(new Date());
			/* 存放路径：${root}/year/month/day
			String path = task.getSite().getDomain() + File.separator + DateUtils.getYearByInt() + File.separator
					+ DateUtils.getMonthByString() + File.separator + DateUtils.getDayByString();
			*/
			result.setFilePath(urlCode);
		} catch (Exception e) {
			logger.debug("获取baseService Bean 错误！", e);
		}
		
		baseService.create(result);
	}

	private boolean hasUrlCode(String urlCode) {
		int count = jdbcTemplate.queryForInt("SELECT count(*) FROM crawl_result WHERE file_path ='" + urlCode + "'");
		return count > 0 ? true : false;
	}

	public static void main(String[] args) {
		URL[] urls = Launcher.getBootstrapClassPath().getURLs();
		for (URL url : urls) {
			System.err.println(url.getPath());
		}
	}
}
