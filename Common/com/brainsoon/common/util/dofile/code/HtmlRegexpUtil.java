package com.brainsoon.common.util.dofile.code;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;


/**
 * 替换html标签工具
 * @author zuohl
 *
 */
public class HtmlRegexpUtil {
	private final static String regxpForHtml = "<([^>]*)>"; // 过滤所有以<开头以>结尾的标签

	@SuppressWarnings("unused")
	private final static String regxpForImgTag = "<\\s*img\\s+([^>]*)\\s*>"; // 找出IMG标签
	@SuppressWarnings("unused")
	private final static String regxpForImaTagSrcAttrib = "src=\"([^\"]+)\""; // 找出IMG标签的SRC属性

	/** 
	* 
	*/
	public HtmlRegexpUtil() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * 基本功能：替换标记以正常显示
	 * <p>
	 * 
	 * @param input
	 * @return String
	 */
	public String replaceTag(String input) {
		if (!hasSpecialChars(input)) {
			return input;
		}
		StringBuffer filtered = new StringBuffer(input.length());
		char c;
		for (int i = 0; i <= input.length() - 1; i++) {
			c = input.charAt(i);
			switch (c) {
			case '<':
				filtered.append("&lt;");
				break;
			case '>':
				filtered.append("&gt;");
				break;
			case '"':
				filtered.append("&quot;");
				break;
			case '&':
				filtered.append("&amp;");
				break;
			default:
				filtered.append(c);
			}

		}
		return (filtered.toString());
	}

	/**
	 * 
	 * 基本功能：判断标记是否存在
	 * <p>
	 * 
	 * @param input
	 * @return boolean
	 */
	public boolean hasSpecialChars(String input) {
		boolean flag = false;
		if ((input != null) && (input.length() > 0)) {
			char c;
			for (int i = 0; i <= input.length() - 1; i++) {
				c = input.charAt(i);
				switch (c) {
				case '>':
					flag = true;
					break;
				case '<':
					flag = true;
					break;
				case '"':
					flag = true;
					break;
				case '&':
					flag = true;
					break;
				}
			}
		}
		return flag;
	}

	/**
	 * 
	 * 基本功能：过滤所有以"<"开头以">"结尾的标签
	 * <p>
	 * 
	 * @param str
	 * @return String
	 */
	public static String filterHtml(String str) {
		Pattern pattern = Pattern.compile(regxpForHtml);
		Matcher matcher = pattern.matcher(str);
		StringBuffer sb = new StringBuffer();
		boolean result1 = matcher.find();
		while (result1) {
			matcher.appendReplacement(sb, "");
			result1 = matcher.find();
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	/**
	 * 
	 * 基本功能：过滤指定标签
	 * <p>
	 * 
	 * @param str
	 * @param tag
	 *            指定标签
	 * @return String
	 */
	public static String fiterHtmlTag(String str, String tag) {
		String regxp = "<\\s*" + tag + "\\s+([^>]*)\\s*>";
		Pattern pattern = Pattern.compile(regxp);
		Matcher matcher = pattern.matcher(str);
		StringBuffer sb = new StringBuffer();
		boolean result1 = matcher.find();
		while (result1) {
			matcher.appendReplacement(sb, "");
			result1 = matcher.find();
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	/**
	 * 
	 * 基本功能：替换指定的标签
	 * <p>
	 * @param str
	 * @param beforeTag
	 *            要替换的标签
	 * @param tagAttrib
	 *            要替换的标签属性值
	 * @param startTag
	 *            新标签开始标记
	 * @param endTag
	 *            新标签结束标记
	 * @return String
	 * @如：替换img标签的src属性值为[img]属性值[/img]
	 */
	public static String replaceHtmlTag(String str, String beforeTag,
			String tagAttrib, String startTag, String endTag) {
		String regxpForTag = "<\\s*" + beforeTag + "\\s+([^>]*)\\s*>";
		String regxpForTagAttrib = tagAttrib + "=\"([^\"]+)\"";
		Pattern patternForTag = Pattern.compile(regxpForTag);
		Pattern patternForAttrib = Pattern.compile(regxpForTagAttrib);
		Matcher matcherForTag = patternForTag.matcher(str);
		StringBuffer sb = new StringBuffer();
		boolean result = matcherForTag.find();
		while (result) {
			StringBuffer sbreplace = new StringBuffer();
			Matcher matcherForAttrib = patternForAttrib.matcher(matcherForTag
					.group(1));
			if (matcherForAttrib.find()) {
				matcherForAttrib.appendReplacement(sbreplace, startTag
						+ matcherForAttrib.group(1) + endTag);
			}
			matcherForTag.appendReplacement(sb, sbreplace.toString());
			result = matcherForTag.find();
		}
		matcherForTag.appendTail(sb);
		return sb.toString();
	}
	/**
	 * 替换指定的标签可以指定替换的属性
	 * 注意标签参数不带<>
	 * @param str
	 * @param beforeTag 要替换的标签
	 * @param tagAttrib 标签里的属性
	 * @param startTag 目标开始标签
	 * @param endTag 目标结束标签 一般为 “”
	 * @param targetAttr 要把tagAttrib替换为xxx
	 * @param attrContent 标签里的属性内容替换源内容
	 * @param targetAttrContent 标签里的属性内容替换目标内容
	 * @return String
	 */
	public static String replaceHtmlTag(String str, String beforeTag,
			String tagAttrib, String startTag, String endTag,String targetAttr,String attrContent,String targetAttrContent){
		return replaceHtmlTag(str, beforeTag, tagAttrib, startTag, endTag, targetAttr, attrContent, targetAttrContent, "", "");
	}
	/**
	 * 替换指定的标签可以指定替换的属性
	 * 注意标签参数不带<>
	 * @param str
	 * @param beforeTag 要替换的标签
	 * @param tagAttrib 标签里的属性
	 * @param startTag 目标开始标签
	 * @param endTag 目标结束标签 一般为 “”
	 * @param targetAttr 要把tagAttrib替换为xxx
	 * @param attrContent 标签里的属性内容替换源内容
	 * @param targetAttrContent 标签里的属性内容替换目标内容
	 * @param splitAttr 要拆分的标签里的属性，如 resource的 size标签，要拆分为img的 width 和 height
	 * @param targetAttrContent 被拆分成的属性，必须是逗号分隔的两个属性如 "width,height"
	 * @return String
	 */
	public static String replaceHtmlTag(String str, String beforeTag,
			String tagAttrib, String startTag, String endTag,String targetAttr,String attrContent,String targetAttrContent,String splitAttr,String splitTargetAttr) {
		String regxpForTag = "<\\s*" + beforeTag + "\\s*([^>]*)\\s*>";
		String regxpForTagAttrib = "";
		if(!StringUtils.isEmpty(tagAttrib)){
			regxpForTagAttrib = tagAttrib + "=\"*([^\"|^\\s]+)\"*";
		}
		Pattern patternForTag = Pattern.compile(regxpForTag);
		Pattern patternForAttrib = null;
		if(!StringUtils.isEmpty(regxpForTagAttrib)){
			patternForAttrib = Pattern.compile(regxpForTagAttrib);
		}
		Matcher matcherForTag = patternForTag.matcher(str);
		Pattern patternForSplitAttr = null;//拆分属性
		if(!StringUtils.isEmpty(splitAttr)){
			String regxpForSplitAttr = splitAttr + "=\"*([^\"|^\\s]+)\"*";
			patternForSplitAttr = Pattern.compile(regxpForSplitAttr);
		}
		StringBuffer sb = new StringBuffer();
		boolean result = matcherForTag.find();
		while (result) {
			StringBuffer sbreplace = new StringBuffer();
			sbreplace.append("<"+startTag).append(" ");
			if(null != patternForAttrib){
				Matcher matcherForAttrib = patternForAttrib.matcher(matcherForTag.group(1));
				if (matcherForAttrib.find()) {
					String attrct = matcherForAttrib.group(1);
					if(!StringUtils.isEmpty(attrContent) && !StringUtils.isEmpty(targetAttrContent)){
						attrct = StringUtils.replace(attrct, attrContent, targetAttrContent);
					}
					matcherForAttrib.appendReplacement(sbreplace, targetAttr +"=\"" + attrct +"\"");
				}
				matcherForAttrib.appendTail(sbreplace);
			}else{
				sbreplace.append(matcherForTag.group(1));
			}
			sbreplace.append(endTag+">");
			String currentStr = sbreplace.toString();
			if(null != patternForSplitAttr && !StringUtils.isEmpty(splitTargetAttr)){
				Matcher matcherForSplit = patternForSplitAttr.matcher(currentStr);
				String [] splitTargetAttrs = StringUtils.split(splitTargetAttr,",");
				StringBuffer splitreplace = new StringBuffer();
				while(matcherForSplit.find()){
					String splitAttrContent = matcherForSplit.group(1);
					if(!StringUtils.isEmpty(splitAttrContent)){
						String [] splitAttrContents = StringUtils.split(splitAttrContent,",");
						if(splitAttrContents.length > 1){
							matcherForSplit.appendReplacement(splitreplace, splitTargetAttrs[0]+"=\""+splitAttrContents[0]+"\" "+splitTargetAttrs[1]+"=\""+splitAttrContents[1]+"\"");
						}else{
							matcherForSplit.appendReplacement(splitreplace, splitTargetAttrs[0]+"=\""+splitAttrContents[0]+"\" ");
						}
					}
				}
				matcherForSplit.appendTail(splitreplace);
				currentStr = splitreplace.toString();
			}
			matcherForTag.appendReplacement(sb, currentStr);
			result = matcherForTag.find();
		}
		matcherForTag.appendTail(sb);
		//根据处理结果处理结束标签
		String regxpForEndTag = "</\\s*" + beforeTag + "\\s*([^>]*)\\s*>";
		Pattern patternForEndTag = Pattern.compile(regxpForEndTag);
		Matcher matcherForEndTag = patternForEndTag.matcher(sb.toString());
		StringBuffer rtn = new StringBuffer();
		boolean result1 = matcherForEndTag.find();
		while (result1) {
		    matcherForEndTag.appendReplacement(rtn, "</"+startTag + endTag +">");
		    result1 = matcherForEndTag.find();
		}
		matcherForEndTag.appendTail(rtn);
		return rtn.toString();
	}
	public static void main(String[] args) {
//		String test = "<titleofchapter id=\"123\">目录<br />content</titleofchapterhhhh>ddddddddddddddd<img src=aaa.jpg alt=\"bbb\"/><a href=\"chapter003.xml#1\">乔学后继有人</a>ffffffff";
//		System.out.println(test);
//		String test1 = replaceHtmlTag(test, "img", "src", "resource ", "" ,"jj",".jpg",".png");
//		System.out.println("结果：============"+test1);
////		String test2 = replaceHtmlTag(test, "titleofchapter", "", "h3", "" ,"bb","","");
////		System.out.println("结果：============"+test2);
////		System.out.println("替换a标签");
//		String test3 = replaceHtmlTag(test, "a", "href", "a", "" ,"href",".xml",".html");
//		System.out.println("结果：============"+test3);
		String testa = "<resource class=\"img\" src=\"chapters/backcover2.jpg\" title=\"封底\" size=\"11,222\" pno=\"1\" text=\"穿越夜空的疯狂旅行\" />";
		testa = HtmlRegexpUtil.replaceHtmlTag(testa, "resource", "src", "img", "" ,"src",".xml",".html","size","w,h");
		System.out.println(testa);
		
	}
}
