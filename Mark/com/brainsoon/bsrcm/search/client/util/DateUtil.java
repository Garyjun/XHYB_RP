/**
 * FileName: DateUtil.java
 */
package com.brainsoon.bsrcm.search.client.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	private static String defaultTimePattern = "yyyy-MM-dd HH:mm:ss:SSS";
	
	public static String getDateTime(Date date) {
		return getDateTime(date, defaultTimePattern);
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
    	Date currentDate=new Date();
    	System.out.println("current Date="+DateUtil.getDateTime(currentDate, "yyyyMMdd"));    	
    }
}
