package com.brainsoon.crawler.core.pipeline;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.annotation.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brainsoon.crawler.utils.DateUtils;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.utils.FilePersistentBase;

/**
 * 保存到本地 按照 年/月/日    目录
 *
 */
@ThreadSafe
public class FilePipeline extends FilePersistentBase implements Pipeline {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public FilePipeline(){}

    public FilePipeline(String path) {
        setPath(path);
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
    	// domain/year/month/day/
        String path = this.path 
        		+ PATH_SEPERATOR 
        		+ task.getUUID() 
        		+ PATH_SEPERATOR 
        		+ DateUtils.getYearByInt()
        		+ PATH_SEPERATOR 
        		+ DateUtils.getMonthByString()
        		+ PATH_SEPERATOR 
        		+ DateUtils.getDayByString()
        		+ PATH_SEPERATOR;
        try {
            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(
            		new FileOutputStream(
            				getFile(path + DigestUtils.md5Hex(resultItems.getRequest().getUrl())
            					+ ".html"))
            		,"UTF-8"));
            printWriter.println("url:\t" + resultItems.getRequest().getUrl());
            for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
                if (entry.getValue() instanceof Iterable) {
                    Iterable value = (Iterable) entry.getValue();
                    printWriter.println(entry.getKey() + ":");
                    for (Object o : value) {
                        printWriter.println(o);
                    }
                } else {
                    printWriter.println(entry.getKey() + ":\t" + entry.getValue());
                }
            }
            printWriter.close();
        } catch (IOException e) {
            logger.warn("write file error", e);
        }
    }
}
