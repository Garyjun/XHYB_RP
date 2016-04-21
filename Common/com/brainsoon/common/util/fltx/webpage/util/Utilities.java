package com.brainsoon.common.util.fltx.webpage.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 封装了一些字符串、数组的常用的方法。
 * 
 */
public class Utilities {
	
	/**
	 * 得到指定代码中指定序号的时间。
	 * 
	 * @param string 需要查找时间的字符串。
	 * @param fromIndex 指定从第几个时间开始查找，第一个时间是1。
	 * @return 查找的时间。
	 */
	public static String getTime(String string,int fromIndex){
		if(string!=null){
			//采集时间正则表达式
			String regex="((\\d{1,2} ?[点:] ?\\d{1,2} ?)([分:] ?\\d{1,2} ?秒?)?)";
			Matcher matcher = Pattern.compile(regex).matcher(string.replace("&nbsp;", ";"));
			
			for(int count=1;matcher.find();count++){
				if(count==fromIndex && matcher.groupCount()>0){
					return matcher.group(1).trim();
				}
			}
		}
		
		return null;
	}
	
	/**
	 * 因网页中可能有零到多个日期或日期时间；所以这里获得指定第几个日期或日期时间。
	 * 
	 * @param string 需要查找时间的字符串。
	 * @param fromIndex 指定从第几个日期时间开始查找，第一个日期时间是1。
	 * @return 日期或日期时间字符串。
	 */
	public static String getDateTime(String string,int fromIndex){
		if(string!=null){
			//采集日期或日期时间正则表达式
			String regex="(((\\d{4}|\\d{2}) ?[/年-] ?)?\\d{1,2} ?[/月-] ?\\d{1,2} ?[日 ]?( ?\\d{1,2}( ?[点:] ?\\d{1,2}( ?[分:] ?\\d{1,2} ?秒?)?)?)?)";
			Matcher matcher = Pattern.compile(regex).matcher(string.replace("&nbsp;", " "));
			//开始查找日期或日期时间
			for(int count=1;matcher.find();count++){
				if(count==fromIndex && matcher.groupCount()>0){
					return matcher.group(1).trim();
				}
			}
		}
		
		return null;
	}
	
	/**
	 * 判断断一个数组是没有初始话，还是为空，开始部分没有初始化。
	 * 
	 * @param object 被检查的数组。
	 * @return  1 表示 数组未初始化。
	 * 			2 表示 数组已初始化，但数组长度为零。
	 * 			3 表示 数组已初始化，但所有元素都初始化，且长度不为零。
	 * 			4 表示 数组已初始化，但所有元素未初始化，且长度不为零。
	 * 			5 表示 数组已初始化，但部分元素未初始化，且长度不为零。
	 */
	public static int checkArray (Object[] object){
		if(object==null){
			return 1;//数组未初始化；
		}
		if(object.length==0){
			return 2;//数组长度为零；
		}
		
		int flag=0;
		for (int i = 0; i < object.length; i++) {
			if(object[i]==null){
				flag++;
			}
		}
		
		if(flag==0){
			return 3;//数组所有元素都初始化，且长度不为零；
		}
		else if(flag==object.length){
			return 4;//数组所有元素未初始化，且长度不为零；
		}
		else{
			return 5;//数组部分元素未初始化，且长度不为零；
		}
	}
	
	
	/**
	 * 得到指定标记在指定字符串中出现的次数。
	 * 
	 * @param string 指定的字符串。
	 * @param flag 指定的标记。
	 */
	public static int getFlagCount(String string, String flag) {
		if(string==null || flag==null){
			throw new NullPointerException("输入的参数“string”或“flag”为空");
		}
		
		if(string.equals("") || flag.equals("")){
			return 0;
		}
		
		int count = 0;
		int index = -1;
		while ((index=string.indexOf(flag,index))>0) {
			count++;
			index=flag.length()+index;
		}
		
		return count;
	}

	/**
	 * 得到指定某种模式的标记在指定字符串中出现的次数。
	 * 
	 * @param string 指定的字符串。
	 * @param regexp 指定某种模式的标记，正则表达式。
	 */
	public static int getRegexpFlagCount(String string, String regexp) {
		if(string==null || regexp==null){
			throw new NullPointerException("输入的参数“string”或“regexp”为空");
		}
		
		if(string.equals("") || regexp.equals("")){
			return 0;
		}
		
		Matcher m=Pattern.compile(regexp).matcher(string);
		
		int count=0; while(m.find()) count++;
	
		return count;
	}
	
	/**
	 * 删除字符串两端所有以 flag 开始子字符串。
	 * 
	 * @param string 需要被删除子字符串的字符串。
	 * @param flag 需要去除的子字符串。
	 * @return 删除两端子字符串的字符串。
	 */
	public static String trimFlag(String string,String flag){
		if(string==null || flag==null){
			throw new NullPointerException("输入的参数“string”或“flag”为空");
		}
		
		if(string.equals("") || flag.equals("")){
			return string;
		}
		
		int index1=string.indexOf(flag);
		while(index1==0){
			string=string.substring(flag.length());
			index1=string.indexOf(flag);
		}
		
		int index2=string.lastIndexOf(flag);
		while(index2+flag.length()==string.length() && index2>=0){
			string=string.substring(0, index2);
			index2=string.lastIndexOf(flag);
		}
		
		return string;
	}
	
	/**
	 * 删除字符串左端所有以 leftFlag 开始子字符串
	 * 
	 * @param string 需要被删除子字符串的字符串。
	 * @param leftFlag 需要去除的子字符串。
	 * @return 删除左端子字符串的字符串。
	 */
	public static String trimLeftFlag(String string,String leftFlag){
		if(string==null || leftFlag==null){
			throw new NullPointerException("输入的参数“string”或“leftFlag”为空");
		}
		
		if(string.equals("") || leftFlag.equals("")){
			return string;
		}
		
		int index=string.indexOf(leftFlag);
		while(index==0){
			string=string.substring(index);
			index=string.indexOf(leftFlag);
		}
		
		return string;
	}
	
	/**
	 * 删除字符串右端所有以 rightFlag 开始子字符串。
	 * 
	 * @param string 需要被删除子字符串的字符串。
	 * @param rightFlag 需要去除的子字符串。
	 * @return 删除右端子字符串的字符串。
	 */
	public static String trimRaightFlag(String string,String rightFlag){
		if(string==null || rightFlag==null){
			throw new NullPointerException("输入的参数“string”或“rightFlag”为空");
		}
		
		if(string.equals("") || rightFlag.equals("")){
			return string;
		}
		
		int index=string.lastIndexOf(rightFlag);
		while(index+rightFlag.length()==string.length() && index>=0){
			string=string.substring(0, index);
			index=string.lastIndexOf(rightFlag);
		}
		
		return string;
	}

	public static String replaceNewline(String string){
		if(string!=null){
			// 注意不要写大于0，方式开始是"\r"、"\n"字符
			if(string.indexOf("\r")>-1){
				string=string.replace("\r", "");
			}
			if(string.indexOf("\n")>-1){
				string=string.replace("\n", "");
			}
		}
		
		return string;
	}
	
	
	/**
	 * 返回指定子字符串在此字符串中第一次出现处的索引，从指定的索引开始，不区分大小。
	 * 
	 * @param subject 被查找字符串。
	 * @param search 要查找的子字符串。
	 * @return 指定子字符串在此字符串中第一次出现处的索引，从指定的索引开始。
	 */
	public static int ignoreCaseIndexOf(String subject, String search) {
		return ignoreCaseIndexOf(subject, search,-1);
	}
	
	/**
	 * 返回指定子字符串在此字符串中第一次出现处的索引，从指定的索引开始，不区分大小。
	 * 
	 * @param subject 被查找字符串。
	 * @param search 要查找的子字符串。
	 * @param fromIndex 开始查找的索引位置。其值没有限制，如果它为负，则与它为 0 的效果同样：将查找整个字符串。
	 * 			如果它大于此字符串的长度，则与它等于此字符串长度的效果相同：返回 -1。
	 * @return 指定子字符串在此字符串中第一次出现处的索引，从指定的索引开始。
	 */
	public static int ignoreCaseIndexOf(String subject, String search,
			int fromIndex) {
		
		//当被查找字符串或查找子字符串为空时，抛出空指针异常。
		if (subject == null || search == null) {
			throw new NullPointerException("输入的参数“subject”或“search”为空");
		}
		
		fromIndex = fromIndex < 0 ? 0 : fromIndex;

		if (search.equals("")) {
			return fromIndex >= subject.length() ? subject.length() : fromIndex;
		}

		int index1 = fromIndex;
		int index2 = 0;

		char c1;
		char c2;

		loop1: while (true) {

			if (index1 < subject.length()) {
				c1 = subject.charAt(index1);
				c2 = search.charAt(index2);
				
			} else {
				break loop1;
			}

			while (true) {
				if (isEqual(c1, c2)) {

					if (index1 < subject.length() - 1
							&& index2 < search.length() - 1) {
						
						c1 = subject.charAt(++index1);
						c2 = search.charAt(++index2);
					} else if (index2 == search.length() - 1) {
					
						return fromIndex;
					} else {
						
						break loop1;
					}
					
				} else {
					
					index2 = 0;
					break;
				}
			}
			//重新查找子字符串的位置
			index1 = ++fromIndex;
		}

		return -1;
	}
	
	/**
	 * 返回指定子字符串在此字符串中最右边出现处的索引。
	 * 
	 * @param subject 被查找字符串。 
	 * @param search 要查找的子字符。
	 * @return 在此对象表示的字符序列中最后一次出现该字符的索引；如果在该点之前未出现该字符，则返回 -1
	 */
	public static int ignoreCaseLastIndexOf(String subject, String search){
		return ignoreCaseLastIndexOf(subject,search,subject.length());
	}
	
	/**
	 * 返回指定字符在此字符串中最后一次出现处的索引，从指定的索引处开始进行反向查找。
	 * @param subject 被查找字符串 。
	 * @param search 要查找的子字符串。
	 * @param fromIndex 开始查找的索引。fromIndex 的值没有限制。如果它大于等于此字符串的长度，则与它小于此字符串长度减 1 的效果相同：将查找整个字符串。
	 * 			如果它为负，则与它为 -1 的效果相同：返回 -1。 
	 * @return 在此对象表示的字符序列（小于等于 fromIndex）中最后一次出现该字符的索引；
	 * 			如果在该点之前未出现该字符，则返回 -1
	 */
	public static int ignoreCaseLastIndexOf(String subject, String search,
			int fromIndex) {
		
		//当被查找字符串或查找子字符串为空时，抛出空指针异常。
		if (subject == null || search == null) {
			throw new NullPointerException("输入的参数“subject”或“search”为空");
		}

		if (search.equals("")) {
			return fromIndex >= subject.length() ? subject.length() : fromIndex;
		}
		
		fromIndex = fromIndex >= subject.length() ? subject.length() - 1 : fromIndex;

		int index1 = fromIndex;
		int index2 = 0;

		char c1;
		char c2;

		loop1: while (true) {

			if (index1 >= 0) {
				c1 = subject.charAt(index1);
				c2 = search.charAt(index2);
			} else {
				break loop1;
			}

			while (true) {
				//判断两个字符是否相等
				if (isEqual(c1, c2)) {
					if (index1 < subject.length() - 1
							&& index2 < search.length() - 1) {
						
						c1 = subject.charAt(++index1);
						c2 = search.charAt(++index2);
					} else if (index2 == search.length() - 1) {
					
						return fromIndex;
					} else {
						
						break loop1;
					}
				} else {
					//在比较时，发现查找子字符串中某个字符不匹配，则重新开始查找子字符串
					index2 = 0;
					break;
				}
			}
			//重新查找子字符串的位置
			index1 = --fromIndex;
		}

		return -1;
	}
	
	/**
	 * 判断两个字符是否相等。
	 * @param c1 字符1
	 * @param c2 字符2
	 * @return 若是英文字母，不区分大小写，相等true，不等返回false；
	 * 			若不是则区分，相等返回true，不等返回false。
	 */
	private static boolean isEqual(char c1,char c2){
			//  字母小写                   字母大写
		if(((97<=c1 && c1<=122) || (65<=c1 && c1<=90))
				&& ((97<=c2 && c2<=122) || (65<=c2 && c2<=90))
				&& ((c1-c2==32) || (c2-c1==32))){
			
			return true;
		}
		else if(c1==c2){
			return true;
		}
		
		return false;
	}
	
	/**
	 * 返回指定子字符串在此字符串中第一次出现处的索引，从指定的索引开始，不区分大小。
	 * 
	 * @param subject 被查找字符串缓冲区。
	 * @param search 要查找的子字符串。
	 * @return 指定子字符串在此字符串中第一次出现处的索引，从指定的索引开始。
	 */
	public static int ignoreCaseIndexOf(StringBuffer subject, String search) {
		return ignoreCaseIndexOf(subject, search,-1);
	}
	
	/**
	 * 返回指定子字符串在此字符串中第一次出现处的索引，从指定的索引开始，不区分大小。
	 * 
	 * @param subject 被查找字符串缓冲区。
	 * @param search 要查找的子字符串。
	 * @param fromIndex 开始查找的索引位置。其值没有限制，如果它为负，则与它为 0 的效果同样：将查找整个字符串。
	 * 			如果它大于此字符串的长度，则与它等于此字符串长度的效果相同：返回 -1。
	 * @return 指定子字符串在此字符串中第一次出现处的索引，从指定的索引开始。
	 */
	public static int ignoreCaseIndexOf(StringBuffer subject, String search,
			int fromIndex) {
		
		//当被查找字符串或查找子字符串为空时，抛出空指针异常。
		if (subject == null || search == null) {
			throw new NullPointerException("输入的参数“subject”或“search”为空");
		}
		
		fromIndex = fromIndex < 0 ? 0 : fromIndex;

		if (search.equals("")) {
			return fromIndex >= subject.length() ? subject.length() : fromIndex;
		}

		int index1 = fromIndex;
		int index2 = 0;

		char c1;
		char c2;

		loop1: while (true) {

			if (index1 < subject.length()) {
				
				c1 = subject.charAt(index1);
				c2 = search.charAt(index2);
				
			} else {
				
				break loop1;
			}

			while (true) {
				if (isEqual(c1, c2)) {

					if (index1 < subject.length() - 1
							&& index2 < search.length() - 1) {
						
						c1 = subject.charAt(++index1);
						c2 = search.charAt(++index2);
					
					} else if (index2 == search.length() - 1) {
					
						return fromIndex;
						
					} else {
						
						break loop1;
					}
					
				} else {
					
					index2 = 0;
					break;
				}
			}
			//重新查找子字符串的位置
			index1 = ++fromIndex;
		}

		return -1;
	}
	
	/**
	 * 返回指定子字符串在此字符串中最右边出现处的索引。
	 * 
	 * @param subject 被查找字符串缓冲区。 
	 * @param search 要查找的子字符。
	 * @return 在此对象表示的字符序列中最后一次出现该字符的索引；如果在该点之前未出现该字符，则返回 -1
	 */
	public static int ignoreCaseLastIndexOf(StringBuffer subject, String search){
		return ignoreCaseLastIndexOf(subject,search,subject.length());
	}
	
	/**
	 * 返回指定字符在此字符串中最后一次出现处的索引，从指定的索引处开始进行反向查找。
	 * @param subject 被查找字符串缓冲区。 
	 * @param search 要查找的子字符串。
	 * @param fromIndex 开始查找的索引。fromIndex 的值没有限制。如果它大于等于此字符串的长度，则与它小于此字符串长度减 1 的效果相同：将查找整个字符串。
	 * 			如果它为负，则与它为 -1 的效果相同：返回 -1。 
	 * @return 在此对象表示的字符序列（小于等于 fromIndex）中最后一次出现该字符的索引；
	 * 			如果在该点之前未出现该字符，则返回 -1
	 */
	public static int ignoreCaseLastIndexOf(StringBuffer subject, String search,
			int fromIndex) {
		
		//当被查找字符串或查找子字符串为空时，抛出空指针异常。
		if (subject == null || search == null) {
			throw new NullPointerException("输入的参数“subject”或“search”为空");
		}

		if (search.equals("")) {
			return fromIndex >= subject.length() ? subject.length() : fromIndex;
		}
		
		fromIndex = fromIndex >= subject.length() ? subject.length() - 1 : fromIndex;

		int index1 = fromIndex;
		int index2 = 0;

		char c1;
		char c2;

		loop1: while (true) {

			if (index1 >= 0) {
				c1 = subject.charAt(index1);
				c2 = search.charAt(index2);
			} else {
				break loop1;
			}

			while (true) {
				//判断两个字符是否相等
				if (isEqual(c1, c2)) {
					
					if (index1 < subject.length() - 1
							&& index2 < search.length() - 1) {
						
						c1 = subject.charAt(++index1);
						c2 = search.charAt(++index2);
					
					} else if (index2 == search.length() - 1) {
					
						return fromIndex;
					
					} else {
						
						break loop1;
					}
				} else {
					//在比较时，发现查找子字符串中某个字符不匹配，则重新开始查找子字符串
					index2 = 0;
					break;
				}
			}
			//重新查找子字符串的位置
			index1 = --fromIndex;
		}

		return -1;
	}
	
	public static void main(String[] args) {
		StringBuffer sb=new StringBuffer("huahuahuahua");
		sb.insert(3, "xia",1,2);
		System.out.println(sb.toString());
		
//		System.out.println(sb.lastIndexOf(""));
//		System.out.println(ignoreCaseLastIndexOf(sb, ""));
		
//		String h="huahuahuahua";
//		int i1=h.lastIndexOf("hua");
//		int i2=h.lastIndexOf("hua",i1-1);
//		System.out.println(h.substring(i2,i1));
		
		
//		System.out.println("bABC".indexOf("ABC",0));
//		System.out.println(ignoreCaseIndexOf("bABC", "abc",0));
//		
//		System.out.println("134".lastIndexOf("34"));
//		System.out.println(ignoreCaseLastIndexOf("134", "34"));
//		
//		System.out.println("134".lastIndexOf("34",1));
//		System.out.println(ignoreCaseLastIndexOf("134", "34",1));
		
//		int flag=ignoreCaseIndexOf("<li><A首 HRef=\"http://www.CSDN.net/\" target=\"_blank\">首页</A>|</li>","CsDn",2);
//		System.out.println(flag);
//		
//		int flag1=ignoreCaseLastIndexOf("<li><A首 HRef=\"http://www.csdn.net/\" target=\"_blank\">首页</A>|</li>","<A首",1000);
//		System.out.println(flag1);
		
//		System.out.println("<li><A首 HRef=\"http://www.csdn.net/\" target=\"_blank\">首页</A>|</li>".indexOf("<A首",0));
//		System.out.println(ignoreCaseIndexOf("<li><A首 HRef=\"http://www.csdn.net/\" target=\"_blank\">首页</A>|</li>","<A首",0));
//		
//		long l1=System.currentTimeMillis();
//		for (int i = 0; i < 10000000; i++) {
//			ignoreCaseIndexOf("<li><A首 HRef=\"http://www.csdn.net/\" target=\"_blank\">首页</A>|</li>","<A首",0);
//		}
//		long l2=System.currentTimeMillis();
//		
//		System.out.println("l2-l1="+(l2-l1));
//		
//		long l3=System.currentTimeMillis();
//		for (int i = 0; i < 10000000; i++) {
//			"<li><A首 HRef=\"http://www.csdn.net/\" target=\"_blank\">首页</A>|</li>".indexOf("<A首",0);
//		}
//		long l4=System.currentTimeMillis();
//		
//		System.out.println("l3-l4="+(l4-l3));
//		
//		System.out.println(ignoreCaseLastIndexOf("<li><A首 HRef=\"http://www.csdn.net/\" target=\"_blank\">首页</A>|</li>","<A首",1000));
//		System.out.println("<li><A首 HRef=\"http://www.csdn.net/\" target=\"_blank\">首页</A>|</li>".lastIndexOf("<A首",1000));
////		
//		long l5=System.currentTimeMillis();
//		for (int i = 0; i < 10000000; i++) {
//			ignoreCaseLastIndexOf("<li><A首 HRef=\"http://www.csdn.net/\" target=\"_blank\">首页</A>|</li>","<A首",1000);
//		}
//		long l6=System.currentTimeMillis();
//		
//		System.out.println("l6-l5="+(l6-l5));
//		
//		long l7=System.currentTimeMillis();
//		for (int i = 0; i < 10000000; i++) {
//			"<li><A首 HRef=\"http://www.csdn.net/\" target=\"_blank\">首页</A>|</li>".lastIndexOf("<A首",1000);
//		}
//		long l8=System.currentTimeMillis();
//		
//		System.out.println("l8-l7="+(l8-l7));
		
//		System.out.println("11".indexOf("",1));
//		System.out.println("".lastIndexOf("",3));
//		System.out.println(isEqual('日','日'));
//		System.out.println((new Character('a').equals('A')));
//		System.out.println((int)'日');
		
//		System.out.println("a:"+(int)'a');
//		System.out.println("z:"+(int)'z');
//		System.out.println("A:"+(int)'A');
//		System.out.println("Z:"+(int)'Z');
//		
//		System.out.println('a'-'z');
		
//		String s="\r\taaa\r\t2010年2月5日&nbsp;17点13分bb\r\t";
//		
//		System.out.println(getTime(s, 1));
//		System.out.println(getDateTime(s, 1));
//		System.out.println(replaceNewline(s));
//		System.out.println(replace(s, "\r",""));
		
//		String s="bbb<a>aaaa<b>aaaa<a>bbb";
//		System.out.println(trimLeftFlag(s, "<a>"));
//		System.out.println(trimRightFlag(s, "<a>"));
//		String s="bb";
//		System.out.println(trimFlag(s, "b"));
	}
}