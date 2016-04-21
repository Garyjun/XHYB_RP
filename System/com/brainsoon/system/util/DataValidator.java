package com.brainsoon.system.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @ClassName: DataValidator 
 * @Description:  数据验证类
 * @author tanghui 
 * @date 2013-6-13 下午2:49:46 
 *
 */
public class DataValidator {
	
   /**
	* 是否是整数
	* @param value
	*/
	public static boolean isIntege(String value){
		boolean b=true;
		if(StringUtils.isNotBlank(value)){
			Pattern p=null;//正则表达式
			Matcher m=null;//操作符表达式
			p=p.compile("^-?[1-9]\\d*$");
			m=p.matcher(value);
			b=m.matches();
		}
		return b;
	}
	
	/**
	* 是否是正整数
	* @param value
	*/
	public static boolean isIntege1(String value){
		boolean b=true;
		if(StringUtils.isNotBlank(value)){
			Pattern p=null;//正则表达式
			Matcher m=null;//操作符表达式
			p=p.compile("^[1-9]\\d*$");
			m=p.matcher(value);
			b=m.matches();
		}
		return b;
	}
	
	/**
	* 是否是负整数
	* @param value
	*/
	public static boolean isIntege2(String value){
		boolean b=true;
		if(StringUtils.isNotBlank(value)){
			Pattern p=null;//正则表达式
			Matcher m=null;//操作符表达式
			p=p.compile("^-[1-9]\\d*$");
			m=p.matcher(value);
			b=m.matches();
		}
		return b;	
	}
	
	/**
	* 是否是数字
	* @param value
	*/
	public static boolean isNum(String value){
		boolean b=true;
		if(StringUtils.isNotBlank(value)){
			Pattern p=null;//正则表达式
			Matcher m=null;//操作符表达式
			p=p.compile("^([+-]?)\\d*\\.?\\d+$");
			m=p.matcher(value);
			b=m.matches();
		}
		
		return b;
	}
	
	/**
	* 是否是正数（正整数 + 0）
	* @param value
	*/
	public static boolean isNum1(String value){
		boolean b=true;
		if(StringUtils.isNotBlank(value)){
			Pattern p=null;//正则表达式
			Matcher m=null;//操作符表达式
			p=p.compile("^[1-9]\\d*|0$");
			m=p.matcher(value);
			b=m.matches();
		}
		return b;
	}
	
	/**
	* 是否是负数（负整数 + 0）
	* @param value
	*/
	public static boolean isNum2(String value){
		boolean b=true;
		if(StringUtils.isNotBlank(value)){
			Pattern p=null;//正则表达式
			Matcher m=null;//操作符表达式
			p=p.compile("^-[1-9]\\d*|0$");
			m=p.matcher(value);
			b=m.matches();
		}
		return b;
	}
	
	/**
	* 是否是浮点数
	* @param value
	*/
	public static boolean isDecmal(String value){
		boolean b=true;
		if(StringUtils.isNotBlank(value)){
			Pattern p=null;//正则表达式
			Matcher m=null;//操作符表达式
			p=p.compile("^([+-]?)\\d*\\.\\d+$");
			m=p.matcher(value);
			b=m.matches();
		}
		return b;
	}
	
	/**
	* 是否是正浮点数
	* @param value
	*/
	public static boolean isDecmal1(String value){
		boolean b=true;
		if(StringUtils.isNotBlank(value)){
			Pattern p=null;//正则表达式
			Matcher m=null;//操作符表达式
			p=p.compile("^[1-9]\\d*.\\d*|0.\\d*[1-9]\\d*$");
			m=p.matcher(value);
			b=m.matches();
		}
		return b;
	}
	
	/**
	* 是否是负浮点数
	* @param value
	*/
	public static boolean isDecmal2(String value){
		boolean b=true;
		if(StringUtils.isNotBlank(value)){
			Pattern p=null;//正则表达式
			Matcher m=null;//操作符表达式
			p=p.compile("^-([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*)$");
			m=p.matcher(value);
			b=m.matches();
		}
		return b;
	}
	
	/**
	* 是否是浮点数
	* @param value
	*/
	public static boolean isDecmal3(String value){
		boolean b=true;
		if(StringUtils.isNotBlank(value)){
			Pattern p=null;//正则表达式
			Matcher m=null;//操作符表达式
			p=p.compile("^-?([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*|0?.0+|0)$");
			m=p.matcher(value);
			b=m.matches();
		}
		return b;
	}
	
	/**
	* 是否是非负浮点数（正浮点数 + 0）
	* @param value
	*/
	public static boolean isDecmal4(String value){
		boolean b=true;
		if(StringUtils.isNotBlank(value)){
			Pattern p=null;//正则表达式
			Matcher m=null;//操作符表达式
			p=p.compile("^[1-9]\\d*.\\d*|0.\\d*[1-9]\\d*|0?.0+|0$");
			m=p.matcher(value);
			b=m.matches();
		}
		return b;
	}
	
	/**
	* 是否是非正浮点数（负浮点数 + 0）
	* @param value
	*/
	public static boolean isDecmal5(String value){
		boolean b=true;
		if(StringUtils.isNotBlank(value)){
			Pattern p=null;//正则表达式
			Matcher m=null;//操作符表达式
			p=p.compile("^(-([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*))|0?.0+|0$");
			m=p.matcher(value);
			b=m.matches();
		}
		return b;
	}
	
	/**
	* 是否是邮件
	* @param value
	*/
	public static boolean isEmail(String value){
		boolean b=true;
		if(StringUtils.isNotBlank(value)){
			Pattern p=null;//正则表达式
			Matcher m=null;//操作符表达式
			p=p.compile("^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$");
			m=p.matcher(value);
			b=m.matches();
		}
		return b;
	}
	
	/**
	* 是否是颜色
	* @param value
	*/
	public static boolean isColor(String value){
		boolean b=true;
		if(StringUtils.isNotBlank(value)){
			Pattern p=null;//正则表达式
			Matcher m=null;//操作符表达式
			p=p.compile("^[a-fA-F0-9]{6}$");
			m=p.matcher(value);
			b=m.matches();
		}
		return b;
	}
	
	/**
	* 是否是url
	* @param value
	*/
	public static boolean isUrl(String value){
		boolean b=true;
		if(StringUtils.isNotBlank(value)){
			Pattern p=null;//正则表达式
			Matcher m=null;//操作符表达式
			p=p.compile("^http[s]?:\\/\\/([\\w-]+\\.)+[\\w-]+([\\w-./?%&=]*)?$");
			m=p.matcher(value);
			b=m.matches();
		}
		return b;
	}
	
	/**
	* 是否是中文
	* @param value
	*/
	public static boolean isChinese(String value){
		boolean b=true;
		if(StringUtils.isNotBlank(value)){
			Pattern p=null;//正则表达式
			Matcher m=null;//操作符表达式
			p=p.compile("^[\\u4E00-\\u9FA5\\uF900-\\uFA2D]+$");
			m=p.matcher(value);
			b=m.matches();
		}
		return b;
	}
	
	/**
	* 是否是ACSII字符
	* @param value
	*/
	public static boolean isAscii(String value){
		boolean b=true;
		if(StringUtils.isNotBlank(value)){
			Pattern p=null;//正则表达式
			Matcher m=null;//操作符表达式
			p=p.compile("^[\\x00-\\xFF]+$");
			m=p.matcher(value);
			b=m.matches();
		}
		return b;
	}
	
	/**
	* 是否是邮编
	* @param value
	*/
	public static boolean isZipcode(String value){
		boolean b=true;
		if(StringUtils.isNotBlank(value)){
			Pattern p=null;//正则表达式
			Matcher m=null;//操作符表达式
			p=p.compile("^\\d{6}$");
			m=p.matcher(value);
			b=m.matches();
		}
		return b;
	}
	/**
	* 是否是邮箱
	* @param value
	*/
	public static boolean mailbox(String value){
		boolean b=true;
		if(StringUtils.isNotBlank(value)){
			Pattern p=null;//正则表达式
			Matcher m=null;//操作符表达式
			p=p.compile("^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$");
			m=p.matcher(value);
			b=m.matches();
		}
		return b;
	}
	/**
	* 是否是手机
	* @param value
	*/
	public static boolean isMobile(String value){
		boolean b=true;
		if(StringUtils.isNotBlank(value)){
			Pattern p=null;//正则表达式
			Matcher m=null;//操作符表达式
			p=p.compile("^(13|15)[0-9]{9}$");
			m=p.matcher(value);
			b=m.matches();
		}
		return b;
	}
	
	/**
	* 是否是ip地址
	* @param value
	*/
	public static boolean isIp(String value){
		boolean b=true;
		if(StringUtils.isNotBlank(value)){
			Pattern p=null;//正则表达式
			Matcher m=null;//操作符表达式
			p=p.compile("^(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)$");
			m=p.matcher(value);
			b=m.matches();
		}
		return b;
	}
	
	/**
	* 是否是非空
	* @param value
	*/
	public static boolean isNotempty(String value){
		boolean b=true;
		if(StringUtils.isNotBlank(value)){
			Pattern p=null;//正则表达式
			Matcher m=null;//操作符表达式
			p=p.compile("^\\S+$");
			m=p.matcher(value);
			b=m.matches();
		}
		return b;
	}
	
	/**
	* 是否是图片
	* @param value
	*/
	public static boolean isPicture(String value){
		boolean b=true;
		if(StringUtils.isNotBlank(value)){
			Pattern p=null;//正则表达式
			Matcher m=null;//操作符表达式
			p=p.compile("(.*)\\.(jpg|bmp|gif|ico|pcx|jpeg|tif|png|raw|tga)$");
			m=p.matcher(value);
			b=m.matches();
		}
		return b;
	}
	
	/**
	* 是否是压缩文件
	* @param value
	*/
	public static boolean isRar(String value){
		boolean b=true;
		if(StringUtils.isNotBlank(value)){
			Pattern p=null;//正则表达式
			Matcher m=null;//操作符表达式
			p=p.compile("(.*)\\.(rar|zip|7zip|tgz)$");
			m=p.matcher(value);
			b=m.matches();
		}
		return b;
	}
	
	/**
	* 是否是日期
	* @param value
	*/
	public static boolean isDate(String value){
		boolean b=true;
		if(StringUtils.isNotBlank(value)){
			Pattern p=null;//正则表达式
			Matcher m=null;//操作符表达式
			p=p.compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
			m=p.matcher(value);
			b=m.matches();
		}
		return b;
	}
	
	/**
	* 是否是QQ号码
	* @param value
	*/
	public static boolean isQq(String value){
		boolean b=true;
		if(StringUtils.isNotBlank(value)){
			Pattern p=null;//正则表达式
			Matcher m=null;//操作符表达式
			p=p.compile("^[1-9]*[1-9][0-9]*$");
			m=p.matcher(value);
			b=m.matches();
		}
		return b;
	}
	
	/**
	* 是否是电话号码的函数(包括验证国内区号,国际区号,分机号)
	* @param value
	*/
	public static boolean isTel(String value){
		boolean b=true;
		if(StringUtils.isNotBlank(value)){
			Pattern p=null;//正则表达式
			Matcher m=null;//操作符表达式
			p=p.compile("^(([0\\+]\\d{2,3}-)?(0\\d{2,3})-)?(\\d{7,8})(-(\\d{3,}))?$");
			m=p.matcher(value);
			b=m.matches();
		}
		return b;
	}
	
	/**
	* 用来用户注册。匹配由数字、26个英文字母或者下划线组成的字符串
	* @param value
	*/
	public static boolean isUsername(String value){
		boolean b=true;
		if(StringUtils.isNotBlank(value)){
			Pattern p=null;//正则表达式
			Matcher m=null;//操作符表达式
			p=p.compile("^\\w+$");
			m=p.matcher(value);
			b=m.matches();
		}
		return b;
	}
	
	/**
	* 是否是字母
	* @param value
	*/
	public static boolean isLetter(String value){
		boolean b=true;
		if(StringUtils.isNotBlank(value)){
			Pattern p=null;//正则表达式
			Matcher m=null;//操作符表达式
			p=p.compile("^[A-Za-z]+$");
			m=p.matcher(value);
			b=m.matches();
		}
		return b;
	}
	/**
	* 数字及字母
	* @param value
	*/
	public static boolean isLetterAndNum(String value){
		boolean b=true;
		if(StringUtils.isNotBlank(value)){
			Pattern p=null;//正则表达式
			Matcher m=null;//操作符表达式
			p=p.compile("^[A-Za-z0-9]+$");
			m=p.matcher(value);
			b=m.matches();
		}
		return b;
	}
	/**
	* 是否是大写字母
	* @param value
	*/
	public static boolean isLetter_u(String value){
		boolean b=true;
		if(StringUtils.isNotBlank(value)){
			Pattern p=null;//正则表达式
			Matcher m=null;//操作符表达式
			p=p.compile("^[A-Z]+$");
			m=p.matcher(value);
			b=m.matches();
		}
		return b;
	}
	
	
	/**
	 * 判断指定的日期格式是否正确
	 * @param str
	 * @return
	 */
	public static boolean isDateFormat(String str,String format){
	    try{
	    	DateFormat formatter = new SimpleDateFormat(format);
	        Date date = (Date)formatter.parse(str);
	        return str.equals(formatter.format(date));
	    }catch(Exception e){
	       return false;
	    }
	}
	
	/**
	* 是否是大写字母
	* @param value
	*/
	public static boolean isLetter_l(String value){
		boolean b=true;
		if(StringUtils.isNotBlank(value)){
			Pattern p=null;//正则表达式
			Matcher m=null;//操作符表达式
			p=p.compile("^[a-z]+$");
			m=p.matcher(value);
			b=m.matches();
		}
		return b;
	}
	
	/**
	* 是否是价格
	* @param value
	*/
	public static boolean isPrice(String value){
		boolean b=true;
		if(StringUtils.isNotBlank(value)){
			Pattern p=null;//正则表达式
			Matcher m=null;//操作符表达式
			p=p.compile("^([1-9]{1}[0-9]{0,}(\\.[0-9]{0,2})?|0(\\.[0-9]{0,2})?|\\.[0-9]{1,2})$");
			m=p.matcher(value);
			b=m.matches();
		}
		return b;
	}
	
	
	/**
	* 字符长度
	* @param value
	*/
	public static long howLength(String value){
		long len = 0;
		if(StringUtils.isNotBlank(value)){
			len = (value+"").length();
		}
		return len;
	}
	
	
	/**
	* 比较字符长度
	* @param value1
	* @param value2
	*/
	public static boolean vsLength(String value1,String value2){
		if(StringUtils.isNotBlank(value1) && StringUtils.isNotBlank(value2)){
			if(Integer.parseInt(value1) > Integer.parseInt(value2)){
				return true;
			}else{
				return false;
			}
		}else if(StringUtils.isNotBlank(value1) && StringUtils.isBlank(value2)){
			return true;
		}else if(StringUtils.isBlank(value1) && StringUtils.isBlank(value2)){
			return false;
		}else{
			return true;
		}
	}
	/**
	* 单选框或单选按钮的验证
	* @param value1（输入的）
	* @param value2(所有的)
	*/
	public static boolean existValue(String value1,String value2){
		if(StringUtils.isNotBlank(value1) && StringUtils.isNotBlank(value2)){
			String[] values = value2.split(",");
	    	int count = 0;
	    	for(String value:values){
	    		if(!value.trim().equals(value1)){
	    			count++;
	    		}else{
	    			break;
	    		}
	    	}
	    	if(count == values.length){
	    		return false;
	    	}
		}
    	return true;
	}	
	/**
	* 多选框的验证
	* @param value1（输入的）
	* @param value2(所有的)
	*/
	public static boolean existCheckValue(String value1,String value2){
		boolean isExist = true;
		if(StringUtils.isNotBlank(value1) && StringUtils.isNotBlank(value2)){
			if(value1.indexOf(",")>0){
				String[] newValue = value1.split(",");
				String[] newValue1 = value2.split(",");
				int count =0;
				for(String value:newValue){
					int count1 = 0;
					for(String value3:newValue1){
						if(value.equals(value3)){
							break;
						}else{
							count1++;
						}
					}
					if(count1 == newValue1.length){
						isExist = false;
						break;
					}else{
						count++;
					}
				}
				if(count != newValue.length){
					isExist = false;
				}
			}else{
				isExist = DataValidator.existValue(value1, value2);
			}
		}
    	return isExist;
	}	
	public static void main(String[] args) {
	//	System.out.println(DataValidator.existCheckValue("a,d", "a,b,c"));
//		boolean b=isPrice("25.67");
//		b = isNum("2222 2");
//		System.out.println("结果是什么？  " + b);
	}

}

