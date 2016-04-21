package com.brainsoon.appframe.util;

import java.io.IOException;
import java.sql.Time;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

/**
 * 字符串工具类
 * 定义 apache 下 StringUtils里没有的方法
 * zuohl
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class StringUtil {
	private static int MINIMUMFRACTIONDIGITS = 2;//float类型小数点后最小位数
	private static int MAXIMUMFRACTIONDIGITS = 2;//float类型小数点后最大位数
	
	/**
	 * 判断集合对象是否为空
	 * @param c 集合对象
	 * @return
	 */
	public static boolean isEmpty(Collection c){
		if( c == null || c.size() == 0 ){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * 判断数组是否为空
	 * @param array
	 * @return
	 */
	public static boolean isEmpty(String[] array){
		if( array == null || array.length == 0 ){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * 判断数组是否不为空
	 * @param array
	 * @return
	 */
	public static boolean isNotEmpty(String[] array){
		return !isEmpty(array);
	}
	
	/**
	 * 判断对象是否为空
	 * @param obj
	 * @return
	 */
	public static boolean isEmpty( Object obj ){
		if( obj == null ){
			return true;
		}else if(StringUtils.isBlank(obj+"")){
			return true;
		}
		return false;
	}
	
	/**
	 * 获取替换字符串
	 * @param str
	 * @param rpstr
	 * @return
	 * @throws Exception
	 */
	public final static String nvl(String str, String rpstr){
		if(str == null && rpstr ==null)
			return "";
		if (isEmpty(str))
			return rpstr;
		return str;
	}
	
	/**
	 * 获取字符串
	 * @param str
	 * @param rpstr
	 * @return
	 * @throws Exception
	 */
	public final static String nvl(Object str , String rpstr){
		if ( str == null )
			return rpstr;		
		return str.toString();		
	}

	/**
	 * 替换空字符串
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public final static String nvl(String str){
		if( str==null || str.equals("null")){
			return "";
		}
		return nvl(str, "");
	}
	/**
	 * 获取空字符串
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public final static String nvl(Object str){
		if( str == null || str.equals("null")){
			return "";
		}
		return nvl(str, "");
	}
	/**
	 * 获取HashMap里面的内容
	 * @param hm
	 * @param hname
	 * @return
	 * @throws Exception
	 */
	public final static String nvl(HashMap hm, String hname) throws Exception {
		if (hm == null) {
			return "";
		} else {
			if ((hm.get(hname) == null) || ("".equals(hm.get(hname)))) {
				return "";
			} else {
				return (String) hm.get(hname);
			}
		}
	}
	
	/**
	 * 将对象转换成整形
	 * @param str
	 * @param defaultint
	 * @return
	 */
	public static int obj2Int(Object str, int defaultint) {
		if (str == null)
			return defaultint;
		try {
			int a = Integer.parseInt((String) str);
			return a;
		} catch (NumberFormatException e) {
			// logger.warn("", e);
			return defaultint;
		}
	}
	/**
	 * 将对象转换成Byte（返回表示指定 byte 值的一个 Byte 实例。如果不需要新的 Byte 实例，则通常应优先使用此方法，而不是构造方法 Byte(byte)，因为该方法有可能通过缓存经常请求的值来显著提高空间和时间性能。）
	 * @param str
	 * @return
	 */
	public static Byte objToByte(Object str){
		//如果为空则直接返回0
		if( isEmpty( str ) ){
			return 0;
		}else{
			try {
				return Byte.valueOf(str+"");
			} catch (Exception e) {
				// TODO: handle exception
				return 0;
			}
		}
	}
	/**
	 * 将对象转换成整型变量
	 * @param obj
	 * @return
	 */
	public static int obj2Int(Object obj){
		//如果为空则直接返回0
		if( isEmpty( obj ) ){
			return 0;
		}
		//将对象转换成整型
		try {
			return Integer.parseInt( nvl(obj,"0") );
		} catch (Exception e) {
			return 0;
		}
	}
	
	/**
	 * 将对象转换成浮点型
	 * @param obj
	 * @return 返回浮点数
	 */
	public static float obj2Float(Object obj){
		//如果为空则直接返回0
		if( isEmpty( obj ) ){
			return 0.00f;
		}
		//将对象转换成整型
		try {
			return Float.parseFloat( nvl(obj,"0") );
		} catch (Exception e) {
			// TODO: handle exception
			return 0.00f;
		}
	}
	/**
	 * 将对象转换成浮点型
	 * @param obj
	 * @param defaultfloat 默认值
	 * @return
	 */
	public static float obj2Float( Object obj , float defaultfloat){
		//如果为空则直接返回0
		if( isEmpty( obj ) ){
			return defaultfloat;
		}
		//将对象转换成整型
		try {
			return Float.parseFloat( nvl(obj,"0") );
		} catch (Exception e) {
			// TODO: handle exception
			return defaultfloat;
		}
	}
	/**
	 * 将对象转换成浮点型
	 * @param obj
	 * @return
	 */
	public static double obj2Double(Object obj){
		//如果为空则直接返回0
		if( isEmpty( obj ) ){
			return 0;
		}
		//将对象转换成整型
		try {
			return Double.parseDouble(nvl(obj,"0") );
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
	}
	/**
	 * 将对象转换成浮点型
	 * @param obj
	 * @param defaultfloat 默认值
	 * @return
	 */
	public static double obj2Double( Object obj , float defaultfloat){
		//如果为空则直接返回0
		if( isEmpty( obj ) ){
			return defaultfloat;
		}
		//将对象转换成整型
		try {
			return Double.parseDouble( nvl(obj,"0") );
		} catch (Exception e) {
			return defaultfloat;
		}
	}
	
	/**
	 * 将对象转换成长整型
	 * @param obj 待转换的对象
	 * @param defaultlong 默认值
	 * @return
	 */
	public static long obj2Long(Object obj , long defaultlong){
		if( isEmpty(obj) ){//如果为空则返回默认值
			return defaultlong;
		}
		try {
			//将对象转换成长整型
			return Long.parseLong(nvl(obj,"0"));
		} catch (Exception e) {
			return defaultlong;
		}
	}
	
	/**
	 * 将对象转换成长整型
	 * @param obj 待转换的对象
	 * @return
	 */
	public static long obj2Long(Object obj){
		return obj2Long(obj,0);
	}
	/**
	 * 将对象转换成 byte 类型（将 string 参数解析为有符号的十进制 byte。除了第一个字符可以是表示负值的 ASCII 负号 '-' ('\u002D') 之外，该字符串中的字符必须都是十进制数字。返回得到的 byte 值与以该 string 参数和基数 10 为参数的 ）
	 * @param obj
	 * @return byte 如果为空或者出现异常返回 -1
	 */
	public static byte obj2Byte(Object obj){
		//如果为空则直接返回-1
		if( isEmpty( obj ) ){
			return Byte.parseByte(-1+"");
		}
		//将对象转换成整型
		try {
			return Byte.parseByte(nvl(obj,"-1"));
		} catch (Exception e) {
			return Byte.parseByte(-1+"");
		}
	}
	/**
	 * 将对象转换为short类型
	 * @param obj
	 * @return short 如果为空或者出现异常返回 -1
	 */
	public static short obj2Short(Object obj){
		//如果为空则直接返回-1
		if( isEmpty( obj ) ){
			return Short.parseShort(-1+"");
		}
		//将对象转换成整型
		try {
			return Short.parseShort(nvl(obj,"-1"));
		} catch (Exception e) {
			return Short.parseShort(-1+"");
		}
		
	}
	/**
	 * 格式化双精度类型数据
	 * @param paramDouble 待格式化值
	 * @param paramString 格式化字符串
	 * @return
	 */
	public static String formatNumber(double paramDouble,String paramString) {
		DecimalFormat localDecimalFormat = new DecimalFormat(paramString);
		return localDecimalFormat.format(paramDouble);
	}
	/**
	 * 格式化双精度类型数据(#.##)
	 * @param paramDouble 待格式化值
	 * @return
	 */
	public static String formatNumber(double paramDouble){
		DecimalFormat localDecimalFormat = new DecimalFormat("0.00");
		return localDecimalFormat.format(paramDouble);
	}
	/**
	 * 格式化长整型数据
	 * @param paramLong 待格式化数据
	 * @param paramString 格式化字符串
	 * @return
	 */
	public static String formatNumber(long paramLong,String paramString) {
		DecimalFormat localDecimalFormat = new DecimalFormat(paramString);
		return localDecimalFormat.format(paramLong);
	}
	
	/**
	 * 获取工程目录
	 * @return
	 */
	public static String getCurrentPath() {
		String str = "";
		try {
			str = StringUtil.class.getProtectionDomain().getCodeSource()
					.toString();
			str = str.substring(str.indexOf("/"), str.lastIndexOf("/"));
			str = str.substring(1, str.indexOf("WEB-INF") + 8);
		} catch (Exception ex) {
			return null;
		}
		return str;
	}
	/**
	 * 获取集合里面的对象数 
	 * @param c Collection对象
	 * @return
	 */
	public static int getCollectionSize( Collection c ){
		if( c == null ){
			return 0 ;
		}else{
			return c.size();
		}
			
	}
	
//	/**
//	 * 加密字符串
//	 * @param str
//	 * @return
//	 * @throws IOException
//	 */
//	public static String encodeString(String str) throws IOException{
//        sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
//        String encodedStr = new String(encoder.encodeBuffer(str.getBytes()));
//        return encodedStr.trim();
//    }
//	/**
//	 * 解密字符串
//	 * @param str
//	 * @return
//	 * @throws IOException
//	 */
//	public static String decodeString(String str) throws IOException{
//        sun.misc.BASE64Decoder dec = new sun.misc.BASE64Decoder();
//        String value = new String(dec.decodeBuffer(str));
//        return value;
//    }
	
	/**
	 * 将半角字符串转换成全角字符串
	 * @param input 待转换的字符串
	 * @return
	 */
	public static String ToSBC(String input) {
		StringBuffer lastStr = new StringBuffer();
		int k = 1; // 记录单引号。如果为单数：左单引号。复数：右单引号
		if (StringUtils.isNotEmpty(input) && input.indexOf("<") != -1 ) {			
			for (int i = 0; i < input.length(); i++) {
				char strs = input.charAt(i);
				char c1 = '\'';
				char c2 = '(';
				char c3 = ')';
				char c4 = '<';
				char c5 = '>';
				char c6 = '/';
				char c7 = ',';
				char c8 = '.';
				// 英文单引号
				if (strs == c1) {
					if (k % 2 == 1) {
						strs = '‘';
					} else {
						strs = '’';
					}
					k++;
				}
				// 英文左括号
				if (strs == c2) {
					strs = '（';
				}
				// 英文右括号
				if (strs == c3) {
					strs = '）';
				}
				if (strs == c4) {
					strs = '＜';
				}
				if (strs == c5) {
					strs = '＞';
				}
				if (strs == c6) {
					strs = '/';
				}
				if (strs == c7) {
					strs = '，';
				}
				if (strs == c8) {
					strs = '.';
				}
				lastStr.append(strs);
			}
		} else {
			lastStr.append( input );
		}
		return lastStr.toString();
    } 
	
	
	/**
	 * 将字符串转换成数组
	 * @param str 源字符串
	 * @param delim 字符串分隔符
	 * @return
	 */
	public static String[] strToArray(String str,String delim){
		String[] array = null;//结果数组
		/**判断数据源和分隔符是否为空，只有不空的情况下才去做转换操作
		 */
		if( StringUtils.isNotEmpty( str ) && StringUtils.isNotEmpty(delim) ){
			try{
				//转换数据
				StringTokenizer st = new StringTokenizer(str,delim);
				//初使化数组长度
				array = new String[st.countTokens()];
				int i = 0 ;
				while( st.hasMoreElements() ){
					String _str = nvl(st.nextElement(),"");
					if( isEmpty(_str) )
						continue;
					//将数值放入到数组路
					array[i]=_str;
					//下标添加1
					i ++;
				}
			}catch (Exception e) {
				return null;
			}
		}
		return array;
	}
	
	/**
	 * 将HashMap转换成Option
	 * @param hp
	 * @param deString
	 * @return
	 */
	public static String hpToSelectOption(Map hp,String deString){
		StringBuffer strBuffer = new StringBuffer();
		try {
			if( !isEmpty(hp) ){
				Iterator it = hp.entrySet().iterator();
				while( it.hasNext() ){
					Entry en = (Entry) it.next();
					if( StringUtils.isNotEmpty(deString) ){
						if( nvl(en.getKey(),"").equals(deString) ){
							strBuffer.append("<option value='"+en.getKey()+"' selected>").append(en.getValue()).append("</option>");
						}else{
							strBuffer.append("<option value='"+en.getKey()+"'>").append(en.getValue()).append("</option>");
						}
					}else{
						strBuffer.append("<option value='"+en.getKey()+"'>").append(en.getValue()).append("</option>");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strBuffer.toString();
	}
	/**
	 * 将string类型的数字数组字符串转换为int数组
	 * @param strs 如"1,2,3,10"
	 * @return Integer []
	 */
	public static Integer [] strArrayToInteger(String strs){
		if(!StringUtil.isEmpty(strs)){
			String [] strsArr = strs.split(",");
			int ls = strsArr.length; //数组长度
			Integer [] ins = new Integer[ls];
			for(int i=0;i<ls;i++){
				ins[i] = Integer.parseInt(strsArr[i]);
			}
			return ins;
		}
		return null;
	}
	/**
	 * 字符串数组转换为整型数组
	 * @param strsArr
	 * @return
	 */
	public static Integer [] strArrayToInteger(String [] strsArr){
		if(null != strsArr && strsArr.length > 0){
			int ls = strsArr.length; //数组长度
			Integer [] ins = new Integer[ls];
			for(int i=0;i<ls;i++){
				ins[i] = Integer.parseInt(strsArr[i]);
			}
			return ins;
		}
		return null;
	}
	/**
	 * 将String类型的数字数组字符串转换为int类型的List
	 * @param strs 如"1,2,3,10"
	 * @return List
	 */
	public static List strArrayToList(String strs){
		List rtn = new ArrayList();
		if(!StringUtil.isEmpty(strs)){
			String [] strsArr = strs.split(",");
			int ls = strsArr.length; //数组长度
			for(int i=0;i<ls;i++){
				rtn.add(Integer.parseInt(strsArr[i]));
			}
		}
		return rtn;
	}
	/**
	 * 将String类型的数字字符串转换为Long的List
	 * @param strs
	 * @return List
	 */
	public static List strArrayToLongList(String strs){
		List rtn = new ArrayList();
		if(!StringUtil.isEmpty(strs)){
			String [] strsArr = strs.split(",");
			int ls = strsArr.length; //数组长度
			for(int i=0;i<ls;i++){
				rtn.add(Long.parseLong(strsArr[i]));
			}
		}
		return rtn;
	}
	public static List strArrayToList(String [] strsArr){
		List rtn = new ArrayList();
		if(null != strsArr){
			int ls = strsArr.length; //数组长度
			for(int i=0;i<ls;i++){
				rtn.add(Integer.parseInt(strsArr[i]));
			}
		}
		return rtn;
	}
	/**
	 * 将字符串转换为date类型
	 * @param str
	 * @return java.util.date 有异常返回null
	 */
	public static Date stringToDate(String str){
		SimpleDateFormat dateFormat = null;
		if(str.length() == 10){
			dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		}else{
			dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
		try {
			return (Date) dateFormat.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 将字符串转换为date类型
	 * @param str
	 * @param format "yyyy-MM-dd HH:mm:ss"  "HH:mm:ss"
	 * @return java.util.date 有异常返回null
	 */
	public static Date stringToDate(String str,String format){
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		try {
			return (Date) dateFormat.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 将date类型转换为字符串
	 * @param str
	 * @return String 有异常返回""
	 */
	public static String DateToString(Date date){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(date);
	}
	/**
	 * 将date类型转换为字符串
	 * @param str
	 * @param format "yyyy-MM-dd HH:mm:ss"  "HH:mm:ss"
	 * @return String 有异常返回""
	 */
	public static String DateToString(Date date,String format){
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(date);
	}
	/**
	 * 将float 转换为 字符串 ，把小数点后的0 去掉
	 * @param fl 浮点数
	 * @return 返回数字字符串，默认两位小数
	 */
	public static String floatToString(float fl){
		NumberFormat nf = NumberFormat.getInstance();
		nf.setGroupingUsed(false);//去0
		nf.setMinimumFractionDigits(MINIMUMFRACTIONDIGITS);//最小位数
		nf.setMaximumFractionDigits(MAXIMUMFRACTIONDIGITS);//最大位数
		return nf.format(fl);//支持转换long double float
	}
	/**
	 * 获取当前时间字符串
	 * @param format 字符串格式 如 yyyyMMddHHmm ， yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	@SuppressWarnings("unused")
	public static String getCurrentTime(String format){
		Date date=new Date();
		SimpleDateFormat dd=new SimpleDateFormat(format);
		return dd.format(new Date());
	}
	/**
	 * 字符串转换为 sql 中的time类型
	 * @param obj
	 * @return
	 */
	public static Time objToSqlTime(String obj){
		return Time.valueOf(obj); 
	}
	/**
	 * 将float 类型转换为 int
	 * @param fl
	 * @param defaultInt
	 * @return
	 */
	public static int float2Int(Float fl,int defaultInt){
		if( isEmpty(fl) ){//如果为空则返回默认值
			return defaultInt;
		}
		try {
			return fl.intValue();
		} catch (Exception e) {
			return defaultInt;
		}
		
	}
	/**
	 * 将float 类型转换为 int
	 * @param fl
	 * @return int
	 */
	public static int float2Int(Float fl){
		return float2Int(fl,0);
	}
	/**
	 * 两个整数相除，取百分比
	 * @param i1
	 * @param i2
	 * @return (如 1/2 返回 50)
	 */
	public static Long divInt(Integer i1,Integer i2){
		if( isEmpty(i1) || isEmpty(i2) ){//如果为空则返回默认值
			return (long)0;
		}
		Long rs = Math.round(i1 * 0.1 / (i2*0.1) * 100);
		return rs;
	}
	/**
	 * 将对象转换为字符串，null转为“”
	 * @param obj
	 * @return
	 */
	public static String obj2String(Object obj){
		if(null == obj){
			return "";
		}
		return String.valueOf(obj);
	}
	/**
	 * 将字符串转换为date类型
	 * 如果为 yyyy-MM-dd 自动补成 yyyy-MM-dd HH:mm:ss格式
	 * @param str
	 * @return java.util.date 有异常返回null
	 */
	public static Date string2Date(String str){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			str = StringUtils.trim(str);
			if(str.length() == 10){
				str += " 00:00:00";
			}
			return (Date) dateFormat.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static void main(String[] args) {
		System.out.println(stringToDate("2012-11-22 00:00:00"));
	}
}
