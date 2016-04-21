package com.brainsoon.crawler.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

	/**
	 * <dt>G 年代标志符</dt>
	 * <dt>y 年</dt>
	 * <dt>M 月</dt>
	 * <dt>d 日</dt>
	 * <dt>h 时 在上午或下午 (1~12)</dt>
	 * <dt>H 时 在一天中 (0~23)</dt>
	 * <dt>m 分</dt>
	 * <dt>s 秒</dt>
	 * <dt>S 毫秒</dt>
	 * <dt>E 星期</dt>
	 * <dt>D 一年中的第几天</dt>
	 * <dt>F 一月中第几个星期几</dt>
	 * <dt>w 一年中第几个星期</dt>
	 * <dt>W 一月中第几个星期</dt>
	 * <dt>a 上午 / 下午 标记符</dt>
	 * <dt>k 时 在一天中 (1~24)</dt>
	 * <dt>K 时 在上午或下午 (0~11)</dt>
	 * <dt>z 时区</dt>
	 */

	public static String YYYY_MM_DD = "yyyy-MM-dd";

	public static Calendar calendar = Calendar.getInstance();

	public static String getDayByString() {
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		if (day < 10) {
			return "0" + day;
		}
		return day + "";
	}

	public static int getDayByInt() {
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	public static String getYearByString() {
		return calendar.get(Calendar.YEAR) + "";
	}

	public static int getYearByInt() {
		return calendar.get(Calendar.YEAR);
	}

	public static String getMonthByString() {
		int month = calendar.get(Calendar.MONTH) + 1;
		if (month < 10) {
			return "0" + month;
		}
		return month + "";
	}

	public static int getMonthByInt() {
		return calendar.get(Calendar.MONTH) + 1;
	}

	/**
	 * 
	 * 获取时间戳 精确到 毫秒级
	 * 
	 * @return
	 */
	public static String getTimestampStr() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		return formatter.format(new Date());
	}

	public static void main(String[] args) {
		System.err.println(getTimestampStr());
	}
}
