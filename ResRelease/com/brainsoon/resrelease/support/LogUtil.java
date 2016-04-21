package com.brainsoon.resrelease.support;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.brainsoon.common.util.date.DateUtil;
import com.brainsoon.resrelease.po.ResOrder;
import com.brainsoon.semantic.ontology.model.Ca;

/**
 * @ClassName: LogUtil
 * @Description: 发布日志
 * @author xiehewei
 * @date 2015年4月17日 下午3:52:39
 *
 */
public class LogUtil {

	public static void fileMissLog(String publishRoot, ResOrder resOrder, Ca ca){
		try {
			String time2Str = DateUtil.convertDateTimeToString(resOrder.getCreateTime()).replaceAll(":", "").replaceAll(" ", ""); 
			String path = publishRoot + time2Str +"/"+ resOrder.getOrderId()
					+ "/log/miss.txt";
			File logFile = new File(path);
			if(!logFile.exists()){
				logFile.createNewFile();
			}
			FileWriter fileWriter = new FileWriter(logFile, true);
			fileWriter.append("title="+"resId,"+"\r\n");
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
