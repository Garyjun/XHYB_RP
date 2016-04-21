/**
 * FileName: DateUtil.java
 */
package com.brainsoon.bsrcm.search.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	private static String defaultTimePattern = "yyyy-MM-dd HH:mm:ss:SSS";
	public static String dateTimePattern = "yyyy-MM-dd HH:mm:ss";
	public static String simpleTimePattern = "yyyy-MM-dd";
	public static String getDateTime(Date date) {
		return getDateTime(date, defaultTimePattern);
	}
	
	public static Date parseTime(String time) throws ParseException {
		return parseTime(time, defaultTimePattern);
	}
	public static Date parseTimes(String time) throws ParseException {
		return parseTime(time, dateTimePattern);
	}
	public static Date simpleDateTimes(String time) throws ParseException {
		return parseTime(time, simpleTimePattern);
	}
	public static String getDateBy8Hours(Date date){
		SimpleDateFormat df=new SimpleDateFormat(dateTimePattern);   
		String newDate = null;
		long time = date.getTime();
		long hours = 8*60*60*1000;
		long newTime = time-hours;
		newDate = df.format(new Date(newTime));
		return newDate;
	}
    public static final String getDateTime(Date date, String aMask) {
        SimpleDateFormat df = null;
        String returnValue = "";

        if (date != null) {
            df = new SimpleDateFormat(aMask);
            returnValue = df.format(date);
        }

        return (returnValue);
    }
    
    public static final Date parseTime(String time, String aMask) throws ParseException {
        SimpleDateFormat df = null;
        Date returnValue = null;

        if (time != null) {
            df = new SimpleDateFormat(aMask);
			returnValue = df.parse(time);
        }

        return (returnValue);
    }
    
	public static Date add(Date date, int type, int val) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(type, val);
		
		return calendar.getTime();
	}
    
    public static void main(String args[]) {
		String officePath = "D:/temp/《除数是一位数的除法》教学反思.doc";
		String targetPdfPath = "D:/temp/《除数是一位数的除法》教学反思.pdf";
		try {
			com.brainsoon.common.util.dofile.conver.OfficeToPdfUtils.convertToPdf(officePath, targetPdfPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	
    }
}
