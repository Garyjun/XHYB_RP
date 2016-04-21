package com.brainsoon.system.util;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.lang.StringUtils;

import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.util.date.DateUtil;

/**
 * 提供给Struts用来做数据类型转换的工具类
 * This class is converts a java.util.Date to a String
 * and a String to a java.util.Date. It is used by
 * BeanUtils when copying properties.  Registered
 * for use in BaseAction. 
 * @author liwei
 */
public class DateConverter implements Converter {
    public Object convert(Class type, Object value) {
        if (value == null) {
            return null;
        } else if (type == Date.class) {
            return convertToDate(type, value);
        } else if (type == String.class) {
            return convertToString(type, value);
        } else {
        	// 李咏：
        	// 未注册Converter的Class都会调用 String 的Converter, 也就是调用这个类，所以必须处理
        	// 参见 org.apache.commons.beanutils.ConvertUtilsBean.convert()
        	// 
        	
        	// 这里给出的convert方式是从org.apache.commons.beanutils.converters.StringConverter
        	// 中移过来
            return (value.toString());
        } 
    }

    protected Object convertToDate(Class type, Object value) {
        if (value instanceof String) {
            try {
                if (StringUtils.isEmpty(value.toString())) {
                    return null;
                }
                (new SimpleDateFormat(DateUtil.getDatePattern())).parse((String) value);
            } catch (Exception pe) {
                throw new ConversionException("Error converting String to Date");
            }
        }
        else if (value instanceof Date) {
        	return value;
        }        

        throw new ConversionException("Could not convert " +
                                      value.getClass().getName() + " to " +
                                      type.getName());
    }

    protected Object convertToString(Class type, Object value) {
        if (value instanceof Date) {
            try {
                return (new SimpleDateFormat(DateUtil.getDatePattern())).format(value);
            } catch (Exception e) {
                throw new ConversionException("Error converting Date to String");
            }
        }

        return value.toString();
    }
    
    
    /**
     * 
     * @Title: dataFormat 
     * @Description: 对日期格式化处理，出错会抛出异常
     * 注：仅能处理：yyyy / yyyy-MM / yyyy-MM-dd 对 带时间的不能处理  
     * @param   
     * @return String 
     * @throws
     */
    public static String dataFormat(String date) throws ServiceException{
    	String formatDate = "";
    	if(StringUtils.isNotBlank(date)){
			if(date.contains("/")){
				formatDate = date.replaceAll("\\/", "-");
			}
			if(date.contains(".")){
				formatDate = date.replaceAll("\\.", "-");
			}
			//长度为8位并且为数字
			if(date.length() == 8 && date.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$")){
				formatDate = date.substring(0,4)+"-"+date.substring(4,6)+"-"+date.substring(6,8);
			}
			if(StringUtils.isBlank(formatDate)){
				formatDate = date;
			}
			String[] dateStr = formatDate.split("[-]");
			int len = dateStr.length;
			//校验每部分是否为数字
			for (int i = 0; i < len; i++) {
				if(!dateStr[i].matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$")){
					throw new ServiceException("日期不合法!【含特殊字符】");
				}
			}
			
			//判断年份是否为4位
			String yearDate = dateStr[0];
			if(yearDate.length() != 4){
				throw new ServiceException("日期不合法!【年份位数不够】");
			}else{
				if(yearDate.equals("0000")){
					throw new ServiceException("日期不合法!【年份不能为0000】");
				}
			}
			
			if(len == 1){//年份
				formatDate = yearDate;
			}else if(len == 2){//月份
				String month = dateStr[1];
				if(date.equals("2014-00")){
					System.out.println("");
				}
				if(StringUtils.isNotBlank(month)){
					if(month.length() > 2 || month.length() <= 0){
						throw new ServiceException("日期不合法!【月份位数不对】");
					}else if(month.length() == 1){//不够2位补0
						if(month.equals("0")){
							throw new ServiceException("日期不合法!【月份不能为0】");
						}else{
							month = "0" + month;
						}
					}else if(month.length() == 2){//
						if(month.equals("00")){
							throw new ServiceException("日期不合法!【月份不能为00】");
						}
					}
					if(StringUtils.isNotBlank(month)){
						formatDate = yearDate + "-" + month;
					}else{
						formatDate = yearDate;
					}
				}else{
					formatDate = yearDate;
				}
			}else if(len == 3){//日
				String month = dateStr[1];
				if(StringUtils.isNotBlank(month)){
					if(month.length() > 2 || month.length() <= 0){
						throw new ServiceException("日期不合法!【月份位数不对】");
					}else if(month.length() == 1){//不够2位补0
						if(month.equals("0")){//出现为0的情况
							throw new ServiceException("日期不合法!【月份不能为0】");
						}else{
							month = "0" + month;
						}
					}else if(month.length() == 2){//
						if(month.equals("00")){
							throw new ServiceException("日期不合法!【月份不能为00】");
						}
					}
					String riqi = dateStr[2];
					if(StringUtils.isNotBlank(riqi)){
						if(riqi.length() > 2 || riqi.length() <= 0){
							throw new ServiceException("日期不合法!【日位数不对】");
						}else if(riqi.length() == 1){//不够2位补0
							if(riqi.equals("0")){//出现为0的情况
								throw new ServiceException("日期不合法!【日不能为0】");
							}else{
								riqi = "0" + riqi;
							}
						}else if(riqi.length() == 2){//
							if(riqi.equals("00")){
								throw new ServiceException("日期不合法!【日不能为00】");
							}
						}
						if(StringUtils.isNotBlank(riqi)){
							formatDate = yearDate + "-" + month  + "-" + riqi;
						}else{
							formatDate = yearDate + "-" + month;
						}
					}else{
						formatDate = yearDate + "-" + month;
					}
				}else{
					throw new ServiceException("日期不合法!【月份填写不对】");
				}
			  }
			}
    	return formatDate;
   }
    /**
     * 判断日期格式化处理
     */
    public static String formatTo(String dateBefor) throws ServiceException{
    	String formatDate = "";
//    	  Date date = new Date();          //当前系统日期
    	  SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			String[] dateStr = dateBefor.split("[-]");
			if(dateBefor.length()<=10&&dateBefor.contains("-")&&dateStr.length==3){
			int len = dateStr.length;
			//校验每部分是否为数字
			for (int i = 0; i < len; i++) {
					if(!dateStr[i].matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$")){
						throw new ServiceException("日期不合法!【含特殊字符】");
					}
				}
			}else{
				Date time = format.parse(dateBefor);
			}
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ServiceException("日期不合法!【含特殊字符】");
		} 
    	
    	return formatDate;
    }
    /**
     * 
     * @Title: dataFormat 
     * @Description: 对日期格式化处理，并会做相应的处理
     * @param   
     * @return String 
     * @throws
     */
    public static String dataFormatTo(String date) throws ServiceException{
    	String formatDate = "";
    	if(StringUtils.isNotBlank(date)){
			if(date.contains("/")){
				formatDate = date.replaceAll("\\/", "-");
			}
			if(date.contains(".")){
				formatDate = date.replaceAll("\\.", "-");
			}
			//长度为8位并且为数字
			if(date.length() == 8 && date.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$")){
				formatDate = date.substring(0,4)+"-"+date.substring(4,6)+"-"+date.substring(6,8);
			}
			if(StringUtils.isBlank(formatDate)){
				formatDate = date;
			}
			String[] dateStr = formatDate.split("[-]");
			int len = dateStr.length;
			//校验每部分是否为数字
			for (int i = 0; i < len; i++) {
				if(!dateStr[i].matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$")){
					throw new ServiceException("日期不合法!【含特殊字符】");
				}
			}
			
			//判断年份是否为4位
			String yearDate = dateStr[0];
			if(yearDate.length() != 4){
				throw new ServiceException("日期不合法!【年份位数不够】");
			}else{
				if(yearDate.equals("0000")){
					throw new ServiceException("日期不合法!【年份不能为0000】");
				}
			}
			
			if(len == 1){//年份
				formatDate = yearDate;
			}else if(len == 2){//月份
				String month = dateStr[1];
				if(date.equals("2014-00")){
					System.out.println("");
				}
				if(StringUtils.isNotBlank(month)){
					if(month.length() > 2 || month.length() <= 0){
						throw new ServiceException("日期不合法!【月份位数不对】");
					}else if(month.length() == 1){//不够2位补0
						if(month.equals("0")){
							month = "";
						}else{
							month = "0" + month;
						}
					}else if(month.length() == 2){//
						if(month.equals("00")){
							month = "";
						}
					}
					if(StringUtils.isNotBlank(month)){
						formatDate = yearDate + "-" + month;
					}else{
						formatDate = yearDate;
					}
				}else{
					formatDate = yearDate;
				}
			}else if(len == 3){//日
				String month = dateStr[1];
				if(StringUtils.isNotBlank(month)){
					if(month.length() > 2 || month.length() <= 0){
						throw new ServiceException("日期不合法!【月份位数不对】");
					}else if(month.length() == 1){//不够2位补0
						if(month.equals("0")){//出现为0的情况
							throw new ServiceException("日期不合法!【月份填写不对】");
						}else{
							month = "0" + month;
						}
					}else if(month.length() == 2){//
						if(month.equals("00")){
							throw new ServiceException("日期不合法!【月份不能为00】");
						}
					}
					String riqi = dateStr[2];
					if(StringUtils.isNotBlank(riqi)){
						if(riqi.length() > 2 || riqi.length() <= 0){
							throw new ServiceException("日期不合法!【日位数不对】");
						}else if(riqi.length() == 1){//不够2位补0
							if(riqi.equals("0")){//出现为0的情况
								riqi= "";
							}else{
								riqi = "0" + riqi;
							}
						}else if(riqi.length() == 2){//
							if(riqi.equals("00")){
								riqi= "";
							}
						}
						if(StringUtils.isNotBlank(riqi)){
							formatDate = yearDate + "-" + month  + "-" + riqi;
						}else{
							formatDate = yearDate + "-" + month;
						}
					}else{
						formatDate = yearDate + "-" + month;
					}
				}else{
					throw new ServiceException("日期不合法!【月份填写不对】");
				}
			  }
			}
    	return formatDate;
   }
    
    
    public static void main(String[] args) {
		try {
			dataFormat("2201-00");
//			dataFormat("20你-12.12");
//			dataFormat("嘻嘻嘻");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
