package com.brainsoon.common.util.dofile.util;

import java.util.Date;

import org.apache.log4j.Logger;

import com.brainsoon.common.util.date.DateUtil;

/**
 * 
 * @ClassName: DateTools 
 * @Description:  时间处理工具类
 * @author tanghui 
 * @date 2013-3-14 下午4:23:49 
 *
 */
public class DateTools {
	public static final Logger logger = Logger.getLogger(DateTools.class);
	
	//当前开始时间（返回秒数）
	public static long getStartTime(){
	    Date dt = new Date();  
	    logger.info("开始处理时间:[" + DateUtil.convertDateTimeToString(dt) + "]");
		return dt.getTime();
	}
	
	//结束时间差（总时间差-总秒数）
	public static String getTotaltime(long startTime){
        Date endDate = new Date();  			
        long endTime = endDate.getTime();
        long timecha = (endTime - startTime);  			
        String totalTime = sumTime(timecha);  
        logger.info("处理完成时间：[" +  DateUtil.convertDateTimeToString(endDate) + "] 共用:{" + totalTime + "}");
        return totalTime;
	}
	
	
	
	/**
	 * 计算转换的总时间
	 * 
	 * @param ms
	 * @return
	 */
	public static String sumTime(long ms) {
		int ss = 1000;
		long mi = ss * 60;
		long hh = mi * 60;
		long dd = hh * 24;
		long day = ms / dd;
		long hour = (ms - day * dd) / hh;
		long minute = (ms - day * dd - hour * hh) / mi;
		long second = (ms - day * dd - hour * hh - minute * mi) / ss;
		long milliSecond = ms - day * dd - hour * hh - minute * mi - second
				* ss;
		String strDay = day < 10 ? "0" + day + "天" : "" + day + "天";
		String strHour = hour < 10 ? "0" + hour + "小时" : "" + hour + "小时";
		String strMinute = minute < 10 ? "0" + minute + "分" : "" + minute + "分";
		String strSecond = second < 10 ? "0" + second + "秒" : "" + second + "秒";
		String strMilliSecond = milliSecond < 10 ? "0" + milliSecond : ""
				+ milliSecond;
		strMilliSecond = milliSecond < 100 ? "0" + strMilliSecond + "毫秒" : ""
				+ strMilliSecond + " 毫秒";
		return strDay + " " + strHour + ":" + strMinute + ":" + strSecond + " "
				+ strMilliSecond;
	}
	
	
	/**
	 * 根据时间返回总秒数 形如：（00:12:12）
	 * 
	 * @param timeStr
	 * @return
	 */
	public static String getSplitStr(String timeStr) {
		String secStr = "0";// 返回秒
		if (timeStr != null && !timeStr.equals("")) {
			String[] str = timeStr.split(":");
			int subInt0 = Integer.parseInt(str[0]);
			int subInt1 = Integer.parseInt(str[1]);
			String str2s = "";
			if (str[2].length() > 2 && str[2].indexOf(".") > 0) {
				str2s = str[2].substring(0, str[2].indexOf("."));
			} else {
				str2s = str[2];
			}
			int subInt2 = Integer.parseInt(str2s);
			Long countNum = subInt0 * 3600L + subInt1 * 60 + subInt2;
			secStr = countNum + "";
		}
		return secStr;
	}

	/**
	 * 计算两个字符串时间相减 如：("00:22:22")
	 * 
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static String calTime(String time1, String time2) {
		Long time1Long = Long.parseLong(time1);
		Long time2Long = Long.parseLong(time2);
		Long timeLong = time2Long - time1Long;
		StringBuffer sbuffer = null;
		if (timeLong > 0) {
			int hour = (int) (timeLong / 3600);
			int minute = (int) ((timeLong - hour * 3600) / 60);
			int second = (int) ((timeLong - hour * 3600 - minute * 60) % 60);
			sbuffer = new StringBuffer();
			if (hour < 10) {
				sbuffer.append("0");
			}
			sbuffer.append(Integer.toString(hour));
			sbuffer.append(":");
			if (minute < 10) {
				sbuffer.append("0");
			}
			sbuffer.append(Integer.toString(minute));
			sbuffer.append(":");
			if (second < 10) {
				sbuffer.append("0");
			}
			sbuffer.append(Integer.toString(second));
			return sbuffer.toString();
		} else {
			logger.error("时间不能为负数！可能原因是传入的时间位置不对!");
			return "";
		}
	}
	
	
	//test
	public static void main(String[] args){
		long ss = getStartTime();
		for (long i = 0; i <2000066000; i++) {
			
		}
		getTotaltime(ss);
	}
	
}
