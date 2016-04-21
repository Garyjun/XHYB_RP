package com.brainsoon.common.util.dofile.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/** 
 * @获取汉语文字的拼音
 * @author:tanghui
 * 一般用法
 
pinyin4j的使用很方便，一般转换只需要使用PinyinHelper类的静态工具方法即可：
 
String[] pinyin = PinyinHelper.toHanyuPinyinStringArray(‘刘’);
 
//该类还有其他的拼音转换形式，但是基本上用不到，就不介绍了
 
返回的数组即是该字符的拼音，如上例就是pinyin[0]=liu2，后面的数字代表声调，声调为5表示轻读，无声调。之所谓返回数组，是因为被判定的汉字有可能有多个读音。如果输入的参数不是汉字，则返回null。
 
拼音格式化
 
如果对于拼音转换后的结果有一些特定的格式要求目前pinyin4j支持：
 
l 声调格式化。例如：“刘”字的格式化后为“liu2”或“liu”或“liú”
 
l 对特殊拼音ü的的显示格式。例如“u:”或“v”或“ü”
 
l 大小写的转换。例如：“liu2”或“LIU2”
 
以上这些格式可以混合使用，下面就来介绍具体的使用方法，首先需要创建格式化对象HanyuPinyinOutputFormat，例如：
 
HanyuPinyinOutputFormat outputFormat = new HanyuPinyinOutputFormat();
 
然后分别调用outputFormat的set方法设置上述一些格式要求：
 
设置声调格式：
 
outputFormat.setToneType(HanyuPinyinToneType);
 
方法参数HanyuPinyinToneType有以下常量对象：
 
HanyuPinyinToneType.WITH_TONE_NUMBER 用数字表示声调，例如：liu2
 
HanyuPinyinToneType.WITHOUT_TONE 无声调表示，例如：liu
 
HanyuPinyinToneType.WITH_TONE_MARK 用声调符号表示，例如：liú
 
设置特殊拼音ü的显示格式：
 
outputFormat.setVCharType(HanyuPinyinVCharType);
 
方法参数HanyuPinyinVCharType有以下常量对象：
 
HanyuPinyinVCharType.WITH_U_AND_COLON 以U和一个冒号表示该拼音，例如：lu:
 
HanyuPinyinVCharType.WITH_V 以V表示该字符，例如：lv
 
HanyuPinyinVCharType.WITH_U_UNICODE 以ü表示
 
设置大小写格式
 
outputFormat.setCaseType(HanyuPinyinCaseType);
 
HanyuPinyinCaseType.LOWERCASE 转换后以全小写方式输出
 
HanyuPinyinCaseType.UPPERCASE 转换后以全大写方式输出
 
设置好格式对象后还是利用上述的工具类方法进行拼音转换，只不过需要将格式化对象当成方法参数传入转换方法，告知要转换的格式要求：

String[] pinyin = PinyinHelper.toHanyuPinyinStringArray(‘刘’, outputFormat);

但该方法会有异常抛出，注意处理。

 */

public class PinyingUtil {

		/** 汉语拼音格式化工具类 */
		private static HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();

		/**
		 * 获取字符串内的所有汉字的汉语拼音
		 * @param src
		 * @return
		 */
		public static String spell(String src) {
			format.setCaseType(HanyuPinyinCaseType.LOWERCASE); // 小写拼音字母
			format.setToneType(HanyuPinyinToneType.WITHOUT_TONE); // 不加语调标识
			format.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE); // u:的声母
			
			StringBuffer sb = new StringBuffer();
			int strLength = src.length();
			try {
				for (int i = 0; i < strLength; i++) {
					// 对英文字母的处理：小写字母转换为大写，大写的直接返回
					char ch = src.charAt(i);
					if (ch >= 'a' && ch <= 'z')
						sb.append((char) (ch - 'a' + 'A'));
					if (ch >= 'A' && ch <= 'Z')
						sb.append(ch);
					// 对汉语的处理
					String[] arr = PinyinHelper.toHanyuPinyinStringArray(ch, format);
					if (arr != null && arr.length > 0)
						sb.append(arr[0]).append(" ");
				}
			} catch (BadHanyuPinyinOutputFormatCombination e) {
				e.printStackTrace();
			}
			return sb.toString();
		}

		/**
		 * 获取字符串内的所有汉字的汉语拼音并大写每个字的首字母
		 * @param src
		 * @return
		 */
		public static String spellWithTone(String src) {
			format.setCaseType(HanyuPinyinCaseType.LOWERCASE);// 小写
			format.setToneType(HanyuPinyinToneType.WITH_TONE_MARK);// 标声调
			format.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);// u:的声母
			
			if (src == null) {
				return null;
			}
			try {
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < src.length(); i++) {
					// 对英文字母的处理：小写字母转换为大写，大写的直接返回
					char ch = src.charAt(i);
					if (ch >= 'a' && ch <= 'z')
						sb.append((char) (ch - 'a' + 'A'));
					if (ch >= 'A' && ch <= 'Z')
						sb.append(ch);
					// 对汉语的处理
					String[] arr = PinyinHelper.toHanyuPinyinStringArray(ch, format);
					if (arr == null || arr.length == 0) {
						continue;
					}
					String s = arr[0];// 不管多音字,只取第一个
					char c = s.charAt(0);// 大写第一个字母
					String pinyin = String.valueOf(c).toUpperCase().concat(s.substring(1));
					sb.append(pinyin).append(" ");
				}
				return sb.toString();
			} catch (BadHanyuPinyinOutputFormatCombination e) {
				e.printStackTrace();
			}
			return null;
		}
		
		/**
		 * 获取字符串内的所有汉字的汉语拼音并大写每个字的首字母
		 * @param src
		 * @return
		 */
		public static String spellNoneTone(String src) {
			format.setCaseType(HanyuPinyinCaseType.LOWERCASE);// 小写
			format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);// 标声调
			format.setVCharType(HanyuPinyinVCharType.WITH_V);// uv的声母
			
			if (src == null) {
				return null;
			}
			try {
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < src.length(); i++) {
					// 对英文字母的处理：小写字母转换为大写，大写的直接返回
					char ch = src.charAt(i);
					if (ch >= 'a' && ch <= 'z')
						sb.append((char) (ch - 'a' + 'A'));
					if (ch >= 'A' && ch <= 'Z')
						sb.append(ch);
					if ( Character.isDigit(ch))
						sb.append(ch);
					if (ch == '-' || ch == '[' || ch == ']' || ch == '【' || ch == '】' || ch =='_' || ch=='*')
						sb.append(ch);
					// 对汉语的处理
					String[] arr = PinyinHelper.toHanyuPinyinStringArray(ch, format);
					if (arr == null || arr.length == 0) {
						continue;
					}
					String s = arr[0];// 不管多音字,只取第一个
					char c = s.charAt(0);// 大写第一个字母
					String pinyin = String.valueOf(c).toUpperCase().concat(s.substring(1));
					sb.append(pinyin).append("");
				}
				return sb.toString();
			} catch (BadHanyuPinyinOutputFormatCombination e) {
				e.printStackTrace();
			}
			return null;
		}

		/**
		 * 获取汉语第一个字的首英文字母
		 * @param src
		 * @return
		 */
		public static String getTerm(String src){
			String res = spell(src);
			if(res!=null&&res.length()>0){
				return res.toUpperCase().charAt(0)+"";
			}else{
				return "OT";
			}
		}
		
	    
		    /**
		     * 判断字符串是否包含有中文   
		     * @param str
		     * @return
		     */
		    public static boolean isChinese(String str) {    
		         String regex = "[\\u4e00-\\u9fa5]";    
		         Pattern pattern = Pattern.compile(regex);    
		         Matcher matcher = pattern.matcher(str);    
		         return matcher.find();    
		       } 
		
		/**
		 * @param args
		 */
		public static void main(String[] args) {
			
//			System.out.println("---------------------spellWithTone");
//			System.out.println(spellWithTone("English"));
//			System.out.println(spellWithTone("有志者事竟成，阿斯顿佛"));
//			System.out.println(spellWithTone("中华人民共和国"));
//			System.out.println(spellWithTone("单丽丽"));
//			
//			System.out.println("-----------------------------spell");
//			System.out.println(spell("English"));
//			System.out.println(spell("有志者事竟成，阿斯顿佛"));
//			System.out.println(spell("中华人民共和国"));
//			System.out.println(spell("单丽丽"));
//			
//			System.out.println("----------------------spellNoneTone");
//			System.out.println(spellNoneTone("English"));
//			System.out.println(spellNoneTone("有志者事竟成，窈窕你是谁"));
//			System.out.println(spellNoneTone("中华人民共和国"));
//			System.out.println(spellNoneTone("单丽丽"));
//			
//			System.out.println("---------------------------getTerm");
//			System.out.println(getTerm("English"));
//			System.out.println(getTerm("有志者事竟成，窈窕你是谁"));
//			System.out.println(getTerm("中华人民共和国"));
//			System.out.println(getTerm("单丽丽"));

		}
		




}
