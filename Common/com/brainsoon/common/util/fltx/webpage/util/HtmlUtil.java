package com.brainsoon.common.util.fltx.webpage.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 本类用来对网页代码进行解析。获取数据方式：字符串标记截取，正则表达式标记截取，和一些辅助功能等。
 * 
 * @作者 华龙
 * 
 * @时间 2008-06-26
 */
public class HtmlUtil {
	// 网页的代码内容
	private String htmlCode = "";

	private int searchBegin = 0;// 查找HTML代码的开始范围
	private int searchEnd = 0;// 查找HTML代码的结束范围
	
	/**字符串标记截取的开始索引*/
	private int besBeginIndex = 0;
	/**正则表达式提取的开始索引*/
	private int regBeginIndex = 0;
	/**正则表达式标记截取的开始索引*/
	private int berBeginIndex = 0;
	/**字符串标记截取对应双标签的开始索引*/
	private int ptBeginIndex = 0;
	/**字符串标记截取一对双标签的开始索引*/
	private int btBeginIndex = 0;
	
	
	/**字符串标记截取的开始标记*/
	private String besBeginString = null;// 开始标记
	/**字符串标记截取的结尾标记*/
	private String besEndString = null;// 结尾标记
	
	
	/**用于正则表达式提取数据的匹配器*/
	private Matcher matcher = null;
	
	
	/** 用于正则表达式标记截取数据的开始匹配匹配器*/
	private Matcher berBeginMatcher = null;// 开始匹配器
	/** 用于正则表达式标记截取数据的结尾匹配匹配器*/
	private Matcher berEndMatcher = null;// 结尾匹配器

	
	/**字符串标记截取对应双标签的开始标记*/
	private String ptBeginString = null;// 开始标记
	/**字符串标记截取对应双标签的结尾标记*/
	private String ptEndString = null;// 结尾标记
	/**字符串标记截取一对双标签的开始标记*/
	private String btBeginString = null;// 开始标记
	/**字符串标记截取一对双标签的结尾标记*/
	private String btEndString = null;// 结尾标记
	
	
	/** 正则表达提取 字符串标记截取的代码块中数据的模式对象*/
	private Pattern strMtchPattern = null;
	/** 正则表达提取 正则表达式标记截取的代码块中数据的模式对象*/
	private Pattern regMtchPattern = null;
	
	
	// 判断查找到结尾标记时，是否再次反向查找开始标记位置
	private boolean isSearchBack = true;
	// 判断截取结果是否包含开始结尾标记
	private boolean isContainSelf = false;
	
	// 保存已加载的HTML标签名
	private List<String> htmlTagList = new ArrayList<String>();
	// 日志操作对象
	private static Logger logger = LogFactory.getLogger(HtmlUtil.class);

	/**
	 * 创建一个网页解析对象。
	 */
	public HtmlUtil() {
		super();
	}
	
	/**
	 * 使用网页代码创建一个网页解析对象。
	 * 
	 * @param htmlCode 网页代码。
	 */
	public HtmlUtil(String htmlCode) {
		this.setHtmlCode(htmlCode);
	}
	
	/**
	 * 返回用于解析的网页代码。
	 * @return 用于解析的网页代码。
	 */
	public String getHtmlCode() {
		return htmlCode;
	}

	/**
	 * 设置用于解析的网页代码。
	 * 
	 * @param htmlCode 网页代码。
	 */
	public void setHtmlCode(String htmlCode) {
		this.htmlCode = htmlCode;
		this.searchBegin = 0;
		this.searchEnd = htmlCode.length();
	}
	
	/**
	 * 获取网页代码的开始查找位置。
	 * 
	 * @return 网页代码的开始查找位置。
	 */
	public int getSearchBegin() {
		return searchBegin;
	}
	
	/**
	 * 设置网页代码的开始查找位置。
	 * @param searchBegin 网页代码的开始查找位置。
	 */
	public void setSearchBegin(int searchBegin) {
		this.searchBegin = searchBegin;
	}
	
	/**
	 * 获取网页代码的结尾查找位置。
	 * @return 网页代码的结尾查找位置。
	 */
	public int getSearchEnd() {
		return searchEnd;
	}
	
	/**
	 * 设置网页代码的结尾查找位置。
	 * @param searchEnd 网页代码的结尾查找位置。
	 */
	public void setSearchEnd(int searchEnd) {
		this.searchEnd = searchEnd;
	}

	/**
	 * 获取查找到结尾标记时，是否需要从结尾标记开始处向前查找开始标记的开始位置。
	 * @return 是否要向前查找，要设置true，不要设置false。
	 */
	public boolean isSearchBack() {
		return isSearchBack;
	}

	/**
	 * 设置查找到结尾标记时，是否需要从结尾标记开始处向前查找开始标记的开始位置。
	 * 
	 * 注：此方法会影响所有使用截取方式得到数据的方法。
	 * 
	 * @param isSearchBack 是否要向前查找，要设置true，不要设置false，默认是true。
	 */
	public void setSearchBack(boolean isSearchBack) {
		this.isSearchBack = isSearchBack;
	}
	
	/**
	 * 获取使用标记截取时，是否要包含标记本身。
	 * @return 截取时是否要包含标记本身，要返回true，不要返回false。
	 */
	public boolean isContainSelf() {
		return isContainSelf;
	}

	/**
	 * 设置使用标记截取时，是否要包含标记本身。
	 * 
	 * 注：此方法会影响所有使用截取方式得到数据的方法。
	 * 
	 * @param isContainSelf 截取时是否要包含标记本身，要返回true，不要返回false，默认是false。
	 */
	public void setContainSelf(boolean isContainSelf) {
		this.isContainSelf = isContainSelf;
	}

	/**
	 * 使用指定的开始、结尾查找位置来设置查找范围，即设置网页代码开始、结尾查找位置。
	 * 
	 * @param searchBegin 开始查找位置。
	 * @param searchEnd 结尾查找位置。
	 */
	public void setSearchRange(int searchBegin, int searchEnd) {
		if(searchBegin<0){
			this.searchBegin = 0;
		}
		else{
			this.searchBegin = searchBegin;
		}
		
		if(htmlCode!=null && searchEnd>htmlCode.length()){
			this.searchEnd = htmlCode.length();
		}
		else{
			this.searchEnd = searchEnd;
		}
	}

	/**
	 * 使用指定的字符串开始、结尾标记来设置查找范围，即设置网页代码开始、结尾查找位置。
	 * 
	 * 注：当字符串开始、结尾标记有一个以上不在网页代码中，则开始位置为0；结尾位置为网页代码长度。
	 * 
	 * @param begin 字符串开始标记。
	 * @param end 字符串结尾标记。
	 */
	public void setSearchRange(String begin, String end) {
		int indexBegin = this.htmlCode.indexOf(begin);
		int indexEnd = this.htmlCode.indexOf(end, indexBegin + begin.length());

		if (indexBegin >= 0 && indexEnd > indexBegin) {
			if (this.isContainSelf) {
				// 包含开始结尾标记
				indexEnd = indexEnd + end.length();
			} else {
				// 不含开始结尾标记
				indexBegin = indexBegin + begin.length();
			}
		} else {
			// 当不存在开始或结尾标记时，查找范围则是开始和结束
			indexBegin = 0;
			indexEnd = this.htmlCode.length();
		}

		// 查找的开始位置
		this.searchBegin = indexBegin;
		// 查找的结束位置
		this.searchEnd = indexEnd;
	}

	/**
	 * 获取使用字符串标记截取的网页代码中第一个子字符串。
	 * 
	 * @param begin 字符串开始标记。
	 * @param end 字符串结尾标记。
	 * @return 使用字符串标记截取的网页代码中第一个子字符串。
	 */
	public String firstStringMatche(String begin, String end) {
		if(begin==null || end==null){
			return null;
		}
		
		this.besBeginString = begin;
		this.besEndString = end;

		this.besBeginIndex = this.searchBegin;

		return nextStringMatche();
	}

	/**
	 * 获取使用字符串标记截取的网页代码中下一个子字符串。
	 * 
	 * @return 使用字符串标记截取的网页代码中下一个子字符串。
	 */
	public String nextStringMatche() {
		if (searchEnd - besBeginIndex < besBeginString.length() + besEndString.length()) {
			return null;
		}

		int indexBegin = -1;
		int indexEnd = -1;

		indexBegin = htmlCode.indexOf(besBeginString, besBeginIndex);
		if (indexBegin == -1 || indexBegin >= searchEnd) {
			return null;
		}

		indexEnd = htmlCode.indexOf(besEndString, indexBegin + besBeginString.length());
		if (indexEnd == -1) {
			return null;
		}

		// 这步需要注意：为了确定 开始结尾标记之间 还有没有 开始标记;
		// 这里indexEnd-1是很重要，避免开始和结尾标记相同时截取不到
		if (isSearchBack) {
			indexBegin = htmlCode.lastIndexOf(besBeginString, indexEnd - 1);
		}

		besBeginIndex = indexEnd + besEndString.length();

		// 判断截取的时候是否要包含标记本身
		if (isContainSelf) {
			return htmlCode.substring(indexBegin, indexEnd + besEndString.length());
		} else {
			return htmlCode.substring(indexBegin + besBeginString.length(),indexEnd);
		}
	}

	/**
	 * 获取网页代码中所有使用字符串标记截取的子字符串。
	 * @param begin 字符串开始标记。
	 * @param end 字符串结尾标记。
	 * @return 网页代码中所有使用字符串标记截取的子字符串。
	 */
	public List<String> getStringMatcheList(String begin, String end) {
		ArrayList<String> list = new ArrayList<String>();

		String result = firstStringMatche(begin, end);
		while (result != null) {
			list.add(result);
			result = nextStringMatche();
		}

		return list;
	}

	/**
	 * 获取网页代码中所有使用字符串标记截取的，且经过containKey单个关键字过滤的子字符串。
	 * 
	 * 注：containKey存在。
	 * 
	 * @param begin 字符串开始标记。
	 * @param end 字符串结尾标记。
	 * @param containKey 包含的关键字。
	 * @return 网页代码中所有使用字符串标记截取的，且经过containKey单个关键字过滤的子字符串。
	 */
	public List<String> getStringMatcheList(String begin, String end,
			String containKey) {
		
		String containKeys[] = { containKey };
		return getStringMatcheList(begin, end, containKeys);
	}

	/**
	 * 获取网页代码中所有使用字符串标记截取的，且经过containKeys里多个关键字过滤的子字符串。
	 * 
	 * 注：containKeys同时存在。
	 * 
	 * @param begin 字符串开始标记。
	 * @param end 字符串结尾标记。
	 * @param containKeys 截取的结果中包含的关键字数组。
	 * @return 网页代码中所有使用字符串标记截取的，且经过containKeys里多个关键字过滤的子字符串。
	 */
	public List<String> getStringMatcheList(String begin, String end,
			String containKeys[]) {
		
		return this.getStringMatcheList(begin, end, containKeys, null);
	}

	/**
	 * 获取网页代码中所有使用字符串标记截取的，且经过containKeys里和exceptKeys里多个关键字过滤的子字符串。
	 * 
	 * 注：containKeys同时存在，exceptKeys同时不存在。
	 * 
	 * @param begin 字符串开始标记。
	 * @param end 字符串结尾标记。
	 * @param containKeys 截取的结果中包含的关键字数组。
	 * @param exceptKeys 截取的结果中不含的关键字数组。
	 * @return 网页代码中所有使用字符串标记截取的，且经过containKeys里和exceptKeys里多个关键字过滤的子字符串。
	 */
	public List<String> getStringMatcheList(String begin, String end,
			String containKeys[], String exceptKeys[]) {
		
		return this.getStringMatcheList(begin, end, containKeys, true,
				exceptKeys, true);
	}

	/**
	 * 获取网页代码中所有使用字符串标记截取的，且经过containKeys里和exceptKeys里多个关键字过滤的子字符串。
	 * @param begin 字符串开始标记。
	 * @param end 字符串结尾标记。
	 * @param containKeys 截取的结果中包含的关键字数组。
	 * @param isContainAll 截取的结果中是否全部包含containKeys，是为true，否为false。
	 * @param exceptKeys 截取的结果中不含的关键字数组。
	 * @param isExceptAll 截取的结果中是否全部不含exceptKeys，是为true，否为false。
	 * @return 网页代码中所有使用字符串标记截取的，且经过containKeys里和exceptKeys里多个关键字过滤的子字符串。
	 */
	public List<String> getStringMatcheList(String begin, String end,
			String containKeys[], boolean isContainAll, String exceptKeys[],
			boolean isExceptAll) {
		
		List<String> filterList = null;

		List<String> list = getStringMatcheList(begin, end);
		if (list != null) {

			filterList = new ArrayList<String>();//用于保存符合条件的结果

			if (containKeys == null || containKeys.length==0) {
				if (exceptKeys == null || exceptKeys.length==0) {
					
					filterList = list;//不用过滤
					
				} else {
					// 全部包含就去除
					if (isExceptAll) {
						for (String result : list) {
							
							int containCount = 0;
							for (int i = 0; i < exceptKeys.length; i++) {
								if (result.contains(exceptKeys[i])) {
									containCount++;
								}
							}
							if (containCount != exceptKeys.length) {
								filterList.add(result);
							}
						}
					}
					// 部分包含就去除
					else {
						for (String result : list) {
							int containCount = 0;
							for (int i = 0; i < exceptKeys.length; i++) {
								if (result.contains(exceptKeys[i])) {
									containCount++;
								}
							}
							if (containCount == 0) {
								filterList.add(result);
							}
						}
					}
				}
			} else {
				// 全部包含就提取
				if (isContainAll) {
					for (String result : list) {
						int containCount = 0;
						for (int i = 0; i < containKeys.length; i++) {
							if (result.contains(containKeys[i])) {
								containCount++;
							}
						}
						if (containCount == containKeys.length) {
							filterList.add(result);
						}
					}
					if (exceptKeys != null && exceptKeys.length>0) {
						// 全部包含就去除
						if (isExceptAll) {
							for (int i = 0; i < filterList.size(); i++) {
								String result = filterList.get(i);

								int containCount = 0;
								for (int j = 0; j < exceptKeys.length; j++) {
									if (result.contains(exceptKeys[j])) {
										containCount++;
									}
								}
								if (containCount == exceptKeys.length) {
									filterList.remove(i);
									i--;
								}
							}
						}
						// 部分包含就去除
						else {
							for (int i = 0; i < filterList.size(); i++) {
								String result = filterList.get(i);
								int containCount = 0;
								for (int j = 0; j < exceptKeys.length; j++) {
									if (result.contains(exceptKeys[j])) {
										containCount++;
									}
								}
								if (containCount > 0) {
									filterList.remove(i);
									i--;
								}
							}
						}
					}
				}
				// 部分包含就提取
				else {
					for (String result : list) {
						int containCount = 0;
						for (int i = 0; i < containKeys.length; i++) {
							if (result.contains(containKeys[i])) {
								containCount++;
							}
						}
						if (containCount > 0) {
							filterList.add(result);
						}
					}
					if (exceptKeys != null && exceptKeys.length>0) {
						// 全部包含就去除
						if (isExceptAll) {
							for (int i = 0; i < filterList.size(); i++) {
								String result = filterList.get(i);
								int containCount = 0;
								for (int j = 0; j < exceptKeys.length; j++) {
									if (result.contains(exceptKeys[j])) {
										containCount++;
									}
								}
								if (containCount == exceptKeys.length) {
									filterList.remove(i);
									i--;
								}
							}
						}
						// 部分包含就去除
						else {
							for (int i = 0; i < filterList.size(); i++) {
								String result = filterList.get(i);
								int containCount = 0;
								for (int j = 0; j < exceptKeys.length; j++) {
									if (result.contains(exceptKeys[j])) {
										containCount++;
									}
								}
								if (containCount > 0) {
									filterList.remove(i);
									i--;
								}
							}
						}
					}
				}
			}
		}

		return filterList;
	}
	
	/**
	 * 获得完整双标签之间的内容。
	 * 
	 * @param tag 双标签的完整代码。
	 * @return 标签之间的内容。
	 */
	public String getTagText(String tag) {
		String text = null;

		if (tag != null && !tag.trim().equals("")) {
			
			// 判断标签的“<”在非零位置，则从“<”处开始截取完整双标签代码
			if (tag.indexOf("<") >= 0) {
				
				//判断是否为标签
				char ch = tag.charAt(tag.indexOf("<") + 1);
				if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
					//截取处完整的双标签。
					tag = tag.substring(tag.indexOf("<"));
					if(tag.lastIndexOf(">")>0){
						tag = tag.substring(0, tag.lastIndexOf(">") + 1);
					}
				}
			}

			// 删除标签
			text = delBetweenTag(tag, "<", ">", false);

			if (text != null) {
				// 替换HTML实体
				text = replaceEntity(text).trim();
			}
		}

		return text;
	}

	/**
	 * 得到标签代码中，第一个包含关键字的单标签或开始标签。
	 * 
	 * @param code 含一个或多个完整双标签代码。
	 * @param tagName 标签名。
	 * @param key 标签中包含的关键字。
	 * @return @return 标签代码中，第一个包含关键字的单标签或开始标签，失败返回 null。
	 */
	public static String getSingleTag(String code, String tagName, String key) {
		if (code == null || tagName == null || key == null) {
			return null;
		}
		
		//查找标签中关键字的位置
		int index = code.indexOf(key);
		if (index < 0) {
			return null;
		}
		
		//从关键字开始处向前不区分大小写的查找标签名		
		int index1 = Utilities.ignoreCaseLastIndexOf(code,"<" + tagName, index);
		if (index1 < 0) {
			return null;
		}
		
		//从关键字开始处向后查找“>”			
		int index2 = code.indexOf(">", index1);
		if (index2 < 0 || index2 < index) {
			return null;
		}
		
		return code.substring(index1, index2 + 1);
	}

	/**
	 * 得到在含一个或多个完整双标签代码中，第一个包含关键字的双标签。
	 * 
	 * @param code 含一个或多个完整双标签代码。
	 * @param tagName 标签名。
	 * @param key 标签中包含的关键字。
	 * @return 成功返回第一个存在关键字的双标签，失败返回 null。
	 */
	public static String getDoubleTag(String code, String tagName, String key) {
		if (code == null || tagName == null || key == null) {
			return null;
		}
		
		//查找首次出现的关键字
		int index = code.indexOf(key);
		if (index < 0) {
			return null;
		}
		
		//从关键字开始处向前查找开始标签名
		int index1 = Utilities.ignoreCaseLastIndexOf(code, "<" + tagName, index);
		if (index1 < 0) {
			return null;
		}
		
		//从开始标签处向后查找结束标签名
		int index2 = Utilities.ignoreCaseIndexOf(code, "</" + tagName + ">", index1);
		if (index2 < 0 || index2 <= index) {
			return null;
		}
		return code.substring(index1, index2 + tagName.length() + 3);
	}

	/**
	 * 获取标签中指定属性名（不区分大小写）的属性值。
	 * 
	 * @param tag 开始标签或单标签。
	 * @param attrName 指定的属性名，不区分大小写。
	 * @return 查找成功返回属性值，否则返回 null。
	 */
	public static String getAttributeValue(String tag, String attrName) {
		// 当标签为空或不存在时，属性名为空时，返回空
		if (tag == null || tag.trim().equals("") || tag.indexOf("=") < 0
				|| attrName == null || attrName.trim().equals("")) {

			return null;
		}

		// 属性值结尾位置
		int attrValueEndIndex = -1;

		// 标签的开始位置，防止标签前有非标签内字符
		int index = tag.indexOf("<", -1);

		while (index < tag.length()) {
			int equalIndex = tag.indexOf("=", index);
			if (equalIndex < 0) {
				break;
			}
			
			// “=”右边的位置
			int equalLeftIndex = equalIndex - 1;
			
			// 跳过“=”右边的空格、制表符、全角空格；“=”右边位置要大于上一个属性值的结尾位置
			for (; (tag.charAt(equalLeftIndex) == ' ' 
					|| tag.charAt(equalLeftIndex) == '\t' 
					|| tag.charAt(equalLeftIndex) == '　')
					&& equalLeftIndex > attrValueEndIndex; equalLeftIndex--);

			// 保存需要比较的位置
			int compareIndex[] = new int[5];

			// 获取“=”左边的“"”的值
			compareIndex[0] = tag.lastIndexOf("\"", equalLeftIndex);
			if (compareIndex[0] > 0 && compareIndex[0] > attrValueEndIndex) {
				// 判断是否为一个转义的“"”
				if (tag.charAt(compareIndex[0] - 1) == '\\') {
					compareIndex[0] = -1;
				}
			}
			// 获取“=”左边的“'”的值
			compareIndex[1] = tag.lastIndexOf("\'", equalLeftIndex);
			if (compareIndex[1] > 0 && compareIndex[1] > attrValueEndIndex) {
				// 判断是否为一个转义的“'”
				if (tag.charAt(compareIndex[1] - 1) == '\\') {
					compareIndex[1] = -1;
				}
			}
			// 获取“=”左边的属性名前半角空格的值
			compareIndex[2] = tag.lastIndexOf(" ", equalLeftIndex);
			// 获取“=”左边的属性名前制表符的值
			compareIndex[3] = tag.lastIndexOf("\t", equalLeftIndex);
			// 获取“=”左边的属性名前全角空格的值
			compareIndex[4] = tag.lastIndexOf("　", equalLeftIndex);
			
			// 对这些位置进行升序排序
			Arrays.sort(compareIndex);

			
			// 判断截取的字符串是否为指定的属性名
			boolean equalsAttrName = false;

			// 取最大的位置，作为属性名的开始位置
			int attrNameBeginIndex = compareIndex[4] + 1;
			if (attrNameBeginIndex > 0 && attrNameBeginIndex >= attrValueEndIndex) {
				// String attrName=tag.substring(attrNameBeginIndex,equalIndex);
				// 对比属性名是否相同
				equalsAttrName = attrName.equalsIgnoreCase(tag.substring(
						attrNameBeginIndex, equalIndex).trim());
			} else {
				
				break;
			}

			/////////////////////////////////////////////

			// 开始计算“=”的右边，即计算属性值的结束位置
			int equalRightIndex = equalIndex + 1;
			// 跳过“=”右边的半角空格、制表符、全角空格
			for (; (tag.charAt(equalRightIndex) == ' '
					|| tag.charAt(equalRightIndex) == '\t' 
					|| tag.charAt(equalLeftIndex) == '　')
					&& equalRightIndex < tag.length() - 1; equalRightIndex++);

			// 获取“=”右边属性值的开始字符
			char nextChar = tag.charAt(equalRightIndex++);
			// 判断为“"”时，属性值使用双引号扩住
			if (nextChar == '"') {
				for (; equalRightIndex < tag.length() - 1; equalRightIndex++) {
					if (tag.charAt(equalRightIndex) == '"') {
						// 判断是否为一个转义的“"”
						if (tag.charAt(equalRightIndex - 1) != '\\') {
							break;
						}
					}
				}
			}
			// 判断为“'”时,属性值使用单引号扩住
			else if (nextChar == '\'') {
				for (; equalRightIndex < tag.length() - 1; equalRightIndex++) {
					if (tag.charAt(equalRightIndex) == '\'') {
						// 判断是否为一个转义的“'”
						if (tag.charAt(equalRightIndex - 1) != '\\') {
							break;
						}
					}
				}
			}
			// 判断为空字符，属性值结尾使用空格分开
			else {
				for (; (tag.charAt(equalRightIndex) != ' '
							&& tag.charAt(equalRightIndex) != '\t' 
							&& tag.charAt(equalLeftIndex) != '　'
							&& tag.charAt(equalLeftIndex) != '>')
						&& equalRightIndex < tag.length() - 1; equalRightIndex++);
			}

			// 保存这次查找的“=”右边属性值结尾位置后一位
			index = equalRightIndex + 1;
			// 保存上一个属性值的结束位置
			attrValueEndIndex = equalRightIndex;

			// 判断本次查找的属性名是否为指定的属性名
			if (equalsAttrName==true) {
				// 截取属性值
				String attrValue = tag.substring(equalIndex + 1,attrValueEndIndex + 1);
				if (attrValue != null) {

					// 去除属性值前后半角空格
					attrValue = attrValue.trim();
					
					// 判断属性值是否被半角单、双引号括住
					if ((attrValue.endsWith("'") && attrValue.startsWith("'"))
							|| (attrValue.endsWith("\"") && attrValue.startsWith("\""))) {
						
						attrValue = attrValue.substring(1,attrValue.length() - 1);
					}
					// 判断属性值后一个字符是标签的“>”
					else if (attrValue.endsWith(">")) {
						
						attrValue = attrValue.substring(0,attrValue.length() - 1);
					}
					
					return attrValue;
				}
			}
		}

		return null;
	}
	
	/**
	 * 获取第一个符合正则表达式的子字符串。
	 * @param regexp 带捕获组的正则表达式。
	 * @return 第一个符合正则表达式的子字符串。
	 */
	public String firstMatche(String regexp){
		this.regBeginIndex=this.searchBegin;
		
		matcher=Pattern.compile(regexp).matcher(this.htmlCode);
		
		return nextMatche();
	}
	
	/**
	 * 获取下一个符合正则表达式的子字符串。
	 * @return 下一个符合正则表达式的子字符串。
	 */
	public String nextMatche(){
		String result=null;
		
		if (searchEnd <= regBeginIndex) {
			return result;
		}
		
		// 从指定位置查找下一个“匹配的数据”
		if(matcher.find(this.regBeginIndex)){
			// “匹配的数据”的开始位置
//			int sIndex=matcher.start();
			// “匹配的数据”的结尾位置
			int eIndex=matcher.end();
			
			// 当开始正则表达式中含有捕获组时，开始标记不一定就是正则表达式匹配的全部字符串
			if(matcher.groupCount()>0){
				// 获取“匹配的数据”的开始位置
//				sIndex=beginMatcher.start(1);
				// 获取“匹配的数据”的结束位置
				eIndex=matcher.end(1);
				
				// 提取匹配的开始标记
				result=matcher.group(1);
			}
			else{
				result=matcher.group(0);
			}
			
			// 设置下一个开始匹配“开始标记”的位置
			this.regBeginIndex=eIndex;
		}
		
		return result;
	}
	
	/**
	 * 获取所有符合正则表达式的子字符串。
	 * @param regexp 带捕获组的正则表达式。
	 * @return 所有符合正则表达式的子字符串。
	 */
	public List<String> getMatcheList(String regexp){
		List<String> matcheList = new ArrayList<String>();
		
		String result = firstMatche(regexp);
		while (result != null) {
			matcheList.add(result);
			result = nextMatche();
		}

		return matcheList;
	}
	
	/**
	 * 获取所有符合正则表达式的，且经过containKey关键字过滤的子字符串。
	 * @param regexp 带捕获组的正则表达式。
	 * @param containKey 必含的关键字。
	 * @return 所有符合正则表达式的，且经过一个关键过滤的子字符串。
	 */
	public List<String> getMatcheList(String regexp, String containKey) {
		return getMatcheList(regexp, new String[] { containKey });
	}

	/**
	 * 获取所有符合正则表达式的，且经过containKeys里的多个关键字过滤的子字符串。
	 * @param regexp 带捕获组的正则表达式。
	 * @param containKeys 全部必含的一组关键字。
	 * @return 所有符合正则表达式的，且经过多个关键字过滤的子字符串。
	 */
	public List<String> getMatcheList(String regexp, String containKeys[]) {
		return getMatcheList(regexp, containKeys, null);
	}

	/**
	 * 获取所有符合正则表达式的，且经过containKeys里和exceptKeys里的多个关键字过滤的子字符串。
	 * @param regexp 带捕获组的正则表达式。
	 * @param containKeys 全部包含的一组关键字。
	 * @param exceptKeys 全部不含的一组关键字。
	 * @return 所有符合正则表达式的，且经过多个关键字过滤的子字符串。
	 */
	public List<String> getMatcheList(String regexp, String containKeys[],
			String exceptKeys[]) {
		return getMatcheList(regexp, containKeys, true, exceptKeys, true);
	}

	/**
	 * 获取所有符合正则表达式的，且经过containKeys里和exceptKeys里的多个关键字过滤的子字符串。
	 * @param regexp 带捕获组的正则表达式。
	 * @param containKeys 必含的一组关键字。
	 * @param isContainAll 子字符串是否全部包含containKeys里的关键字，是为true，不是为false。
	 * @param exceptKeys 不含的一组关键字。
	 * @param isExceptAll 子字符串是否全部包含exceptKeys里的关键字，是为true，不是为false。
	 * @return 所有符合正则表达式的，且经过多个关键字过滤的子字符串。
	 */
	public List<String> getMatcheList(String regexp, String containKeys[],
			boolean isContainAll, String exceptKeys[], boolean isExceptAll) {
		List<String> filterList = null;
		
		List<String> list = getMatcheList(regexp);
		if (list != null) {

			filterList = new ArrayList<String>();

			if (containKeys == null || containKeys.length==0) {
				if (exceptKeys == null || exceptKeys.length==0) {
					
					filterList = list;//直接赋值
					
				} else {
					// 全部包含就去除
					if (isExceptAll) {
						for (String result : list) {
							
							int containCount = 0;
							for (int i = 0; i < exceptKeys.length; i++) {
								if (result.contains(exceptKeys[i])) {
									containCount++;
								}
							}
							if (containCount != exceptKeys.length) {
								filterList.add(result);
							}
						}
					}
					// 部分包含就去除
					else {
						for (String result : list) {
							int containCount = 0;
							for (int i = 0; i < exceptKeys.length; i++) {
								if (result.contains(exceptKeys[i])) {
									containCount++;
								}
							}
							if (containCount == 0) {
								filterList.add(result);
							}
						}
					}
				}
			} else {
				// 全部包含就提取
				if (isContainAll) {
					for (String result : list) {
						
						int containCount = 0;
						for (int i = 0; i < containKeys.length; i++) {
							if (result.contains(containKeys[i])) {
								containCount++;
							}
						}
						if (containCount == containKeys.length) {
							filterList.add(result);
						}
					}
					if (exceptKeys != null && exceptKeys.length>0) {
						// 全部包含就去除
						if (isExceptAll) {
							for (int i = 0; i < filterList.size(); i++) {
								String result = filterList.get(i);
								
								int containCount = 0;
								for (int j = 0; j < exceptKeys.length; j++) {
									if (result.contains(exceptKeys[j])) {
										containCount++;
									}
								}
								if (containCount == exceptKeys.length) {
									filterList.remove(i);
									i--;
								}
							}
						}
						// 部分包含就去除
						else {
							for (int i = 0; i < filterList.size(); i++) {
								String result = filterList.get(i);
								
								int containCount = 0;
								for (int j = 0; j < exceptKeys.length; j++) {
									if (result.contains(exceptKeys[j])) {
										containCount++;
									}
								}
								if (containCount > 0) {
									filterList.remove(i);
									i--;
								}
							}
						}
					}
				}
				// 部分包含就提取
				else {
					for (String result : list) {
						int containCount = 0;
						for (int i = 0; i < containKeys.length; i++) {
							if (result.contains(containKeys[i])) {
								containCount++;
							}
						}
						if (containCount > 0) {
							filterList.add(result);
						}
					}
					if (exceptKeys != null && exceptKeys.length>0) {
						// 全部包含就去除
						if (isExceptAll) {
							for (int i = 0; i < filterList.size(); i++) {
								String result = filterList.get(i);
								
								int containCount = 0;
								for (int j = 0; j < exceptKeys.length; j++) {
									if (result.contains(exceptKeys[j])) {
										containCount++;
									}
								}
								if (containCount == exceptKeys.length) {
									filterList.remove(i);
									i--;
								}
							}
						}
						// 部分包含就去除
						else {
							for (int i = 0; i < filterList.size(); i++) {
								String result = filterList.get(i);
								
								int containCount = 0;
								for (int j = 0; j < exceptKeys.length; j++) {
									if (result.contains(exceptKeys[j])) {
										containCount++;
									}
								}
								if (containCount > 0) {
									filterList.remove(i);
									i--;
								}
							}
						}
					}
				}
			}
		}

		return filterList;
	}
	/**
	 * 获取使用正则表达式标记截取的网页代码中第一个子字符串字符串。
	 * 
	 * @param beginRegexp 开始正则表达式标记，如果有捕获组，则会以第一捕获组获取的字符串作开始标记。
	 * @param endRegexp 结尾正则表达式标记，如果有捕获组，则会以第一捕获组获取的字符串作结尾标记。
	 * @return 使用正则表达式开始、结尾标记截取的网页代码中第一个子字符串字符串。
	 */
	public String firstRegexpMatche(String beginRegexp, String endRegexp) {
		if(beginRegexp==null || endRegexp==null){
			return null;
		}
		
		this.berBeginIndex = this.searchBegin;

		this.berBeginMatcher = Pattern.compile(beginRegexp).matcher(htmlCode);
		this.berEndMatcher = Pattern.compile(endRegexp).matcher(htmlCode);

		return nextRegexpMatche();
	}
	
	/**
	 * 获取正则表达式使用标记截取的网页代码中下一个子字符串字符串。
	 * 
	 * @return 使用正则表达式开始、结尾正则表达式标记截取的网页代码中下一个子字符串字符串。
	 */
	public String nextRegexpMatche(){
		if (searchEnd <= berBeginIndex) {
			return null;
		}
		
		// 从指定位置查找下一个“开始标记”
		if(berBeginMatcher.find(this.berBeginIndex)){
			
			// “开始标记”的开始位置
			int sIndexBegin=berBeginMatcher.start();
			// “开始标记”的结尾位置
			int eIndexBegin=berBeginMatcher.end();
			
			// 当开始正则表达式中含有捕获组时，开始标记不一定就是正则表达式匹配的全部字符串
			if(berBeginMatcher.groupCount()>0){
				// 提取匹配的开始标记
				// String begin=beginMatcher.group(1);
				
				// 获取“开始标记”的开始位置
				sIndexBegin=berBeginMatcher.start(1);
				// 获取“开始标记”的结束位置
				eIndexBegin=berBeginMatcher.end(1);
			}
			
			// 从指定位置查找下一个“结尾标记
			if(berEndMatcher.find(eIndexBegin)){
				
				// “结尾标记”的开始位置
				int sIndexEnd=berEndMatcher.start();
				// “结尾标记”的结束位置
				int eIndexEnd=berEndMatcher.end();
				
				// 当结尾正则表达式中含有捕获组时，结尾标记不一定就是正则表达式匹配的全部字符串
				if (berEndMatcher.groupCount() > 0) {
					// 提取匹配的结尾标记
//					 String end=berEndMatcher.group(0);
					
					// 获取“结尾标记”的开始位置
					sIndexEnd = berEndMatcher.start(1);
					// 获取“结尾标记”的结束位置
					eIndexEnd = berEndMatcher.end(1);
				}
				
				//判断为需要向后查找，即查找到“结尾标记”时，反向查找“开始标记”，避免首次查找的“开始标记”与“结尾标记”之间还有“开始标记”
				if (isSearchBack) {
					
					//这里不能像字符串标记向后查找那样，这里使用的循环递进，当查找到“开始标记”与“结尾标记”之间没有“开始标记”则不再查找
					while (berBeginMatcher.find(eIndexBegin)) {
						// “开始标记”的开始位置
						int sIndexBeginTmp = berBeginMatcher.start();
						// “开始标记”的结尾位置
						int eIndexBeginTmp = berBeginMatcher.end();
						
						// 当开始正则表达式中含有捕获组时，开始标记不一定就是正则表达式匹配的全部字符串
						if (berBeginMatcher.groupCount() > 0) {
							// 提取匹配的开始标记
							// String begin=beginMatcher.group(1);
							
							// 获取“开始标记”的开始位置
							sIndexBeginTmp = berBeginMatcher.start(1);
							// 获取“开始标记”的结束位置
							eIndexBeginTmp = berBeginMatcher.end(1);
						}
						
						//当“开始标记”的结尾位置大于“结尾标记”的开始位置则不再查找
						if(eIndexBeginTmp>sIndexEnd){
							break;
						}
						
						//给“开始标记”的开始位置赋值
						sIndexBegin=sIndexBeginTmp;
						//给“开始标记”的结尾位置赋值
						eIndexBegin=eIndexBeginTmp;
					}
				}
				
				// 设置下一个开始匹配“开始标记”的位置
				this.berBeginIndex=eIndexEnd;
				
				// 判断截取时是否包含标记本身
				if(!this.isContainSelf){
					// 判断截取时不包含标记本身
					return this.htmlCode.substring(eIndexBegin, sIndexEnd);
				}
				else{
					// 判断截取时是包含标记本身
					return this.htmlCode.substring(sIndexBegin, eIndexEnd);
				}
			}
		}
		
		return null;
	}

	/**
	 * 获取网页代码中所有使用正则表达式标记截取的子字符串。
	  @param beginRegexp 开始正则表达式标记，如果有捕获组，则会以第一捕获组获取的字符串作开始标记。
	 * @param endRegexp 结尾正则表达式标记，如果有捕获组，则会以第一捕获组获取的字符串作结尾标记。
	 * @return 网页中所有使用正则表达式开始、结尾标记截取的子字符串。
	 */
	public List<String> getRegexpMatcheList(String beginRegexp,
			String endRegexp) {
		List<String> matcheList = new ArrayList<String>();
		
		String result = firstRegexpMatche(beginRegexp, endRegexp);
		while (result != null) {
			matcheList.add(result);
			result = nextRegexpMatche();
		}

		return matcheList;
	}

	/**
	 * 获取网页代码中所有使用正则表达式开始、结尾标记截取的，且经过containKey关键字过滤的子字符串。
	 * 
	 * 注：containKey存在。
	 * 
	 @param beginRegexp 开始正则表达式标记，如果有捕获组，则会以第一捕获组获取的字符串作开始标记。
	 * @param endRegexp 结尾正则表达式标记，如果有捕获组，则会以第一捕获组获取的字符串作结尾标记。
	 * @param containKey 包含的关键字。
	 * @return 网页中所有使用正则表达式开始、结尾标记截取的，且经过containKey关键字过滤的子字符串
	 */
	public List<String> getRegexpMatcheList(String beginRegexp,
			String endRegexp, String containKey) {
		
		return this.getRegexpMatcheList(beginRegexp, endRegexp,
								new String[] { containKey });
	}

	/**
	 * 获取网页代码中所有使用正则表达式开始、结尾标记截取的，且经过containKeys里多个关键字过滤的子字符串。
	 * 
	 * 注：containKeys同时存在。
	 * 
	 * @param beginRegexp 开始正则表达式标记，如果有捕获组，则会以第一捕获组获取的字符串作开始标记。
	 * @param endRegexp 结尾正则表达式标记，如果有捕获组，则会以第一捕获组获取的字符串作结尾标记。
	 * @param containKeys 截取的结果中包含的关键字数组。
	 * @return 网页中所有使用正则表达式开始、结尾标记截取的，且经过containKeys里多个关键字过滤的子字符串。
	 */
	public List<String> getRegexpMatcheList(String beginRegexp,
			String endRegexp, String containKeys[]) {
		
		return this.getRegexpMatcheList(beginRegexp, endRegexp, containKeys);
	}

	/**
	 * 获取网页代码中所有使用正则表达式标记截取的，且经过containKeys里和exceptKeys里多个关键字过滤的结果。
	 * 
	 * 注：containKeys同时存在，exceptKeys同时不存在。
	 * 
	 * @param beginRegexp 开始正则表达式标记，如果有捕获组，则会以第一捕获组获取的字符串作开始标记。
	 * @param endRegexp 结尾正则表达式标记，如果有捕获组，则会以第一捕获组获取的字符串作结尾标记。
	 * @param containKeys 截取的结果中包含的关键字数组。
	 * @param exceptKeys 截取的结果中不含的关键字数组。
	 * @return 所有使用正则表达式标记截取的，且经过containKeys里和exceptKeys里多个关键字过滤的结果。
	 */
	public List<String> getRegexpMatcheList(String beginRegexp,
			String endRegexp, String containKeys[], String exceptKeys[]) {
		
		return this.getRegexpMatcheList(beginRegexp, endRegexp, containKeys,
				true, exceptKeys, true);
	}

	/**
	 * 获取网页代码中所有使用正则表达式标记截取的，且经过containKeys里和exceptKeys里多个关键字过滤的结果。
	 * 
	 * @param beginRegexp 开始正则表达式标记，如果有捕获组，则会以第一捕获组获取的字符串作开始标记。
	 * @param endRegexp 结尾正则表达式标记，如果有捕获组，则会以第一捕获组获取的字符串作结尾标记。
	 * @param containKeys 截取的结果中包含的关键字数组。
	 * @param isContainAll 截取的结果中是否全部包含containKeys，是为true，否为false。
	 * @param exceptKeys 截取的结果中不含的关键字数组。
	 * @param isExceptAll 截取的结果中是否全部不含exceptKeys，是为true，否为false。
	 * @return 所有使用正则表达式标记截取的，且经过containKeys里和exceptKeys里多个关键字过滤的结果。
	 */
	public List<String> getRegexpMatcheList(String beginRegexp,
			String endRegexp, String containKeys[], boolean isContainAll,
			String exceptKeys[], boolean isExceptAll) {
		
		List<String> filterMatcheList = null;

		List<String> matcheList = getRegexpMatcheList(beginRegexp, endRegexp);
		if (matcheList != null) {
			
			filterMatcheList = new ArrayList<String>();
			
			if (containKeys == null || containKeys.length==0) {
				if (exceptKeys == null || exceptKeys.length==0) {
					
					filterMatcheList = matcheList;//直接赋值
				
				} else {
					// 全部包含就去除
					if (isExceptAll) {
						for (String result : matcheList) {
							int containCount = 0;
							for (int i = 0; i < exceptKeys.length; i++) {
								if (result.contains(exceptKeys[i])) {
									containCount++;
								}
							}
							if (containCount != exceptKeys.length) {
								filterMatcheList.add(result);
							}
						}
					}
					// 部分包含就去除
					else {
						for (String result : matcheList) {
							int containCount = 0;
							for (int i = 0; i < exceptKeys.length; i++) {
								if (result.contains(exceptKeys[i])) {
									containCount++;
								}
							}
							if (containCount == 0) {
								filterMatcheList.add(result);
							}
						}
					}
				}
			} else {
				// 全部包含就提取
				if (isContainAll) {
					for (String result : matcheList) {
						int containCount = 0;
						for (int i = 0; i < containKeys.length; i++) {
							if (result.contains(containKeys[i])) {
								containCount++;
							}
						}
						if (containCount == containKeys.length) {
							filterMatcheList.add(result);
						}
					}
					if (exceptKeys != null && exceptKeys.length>0) {
						// 全部包含就去除
						if (isExceptAll) {
							for (int i = 0; i < filterMatcheList.size(); i++) {
								String result = filterMatcheList.get(i);

								int containCount = 0;
								for (int j = 0; j < exceptKeys.length; j++) {
									if (result.contains(exceptKeys[j])) {
										containCount++;
									}
								}
								if (containCount == exceptKeys.length) {
									filterMatcheList.remove(i);
									i--;
								}
							}
						} 
						// 部分包含就去除
						else {
							for (int i = 0; i < filterMatcheList.size(); i++) {
								String result = filterMatcheList.get(i);

								int containCount = 0;
								for (int j = 0; j < exceptKeys.length; j++) {
									if (result.contains(exceptKeys[j])) {
										containCount++;
									}
								}
								if (containCount > 0) {
									filterMatcheList.remove(i);
									i--;
								}
							}
						}
					}
				} 
				// 部分包含就提取
				else {
					for (String result : matcheList) {
						int containCount = 0;
						for (int i = 0; i < containKeys.length; i++) {
							if (result.contains(containKeys[i])) {
								containCount++;
							}
						}
						if (containCount > 0) {
							filterMatcheList.add(result);
						}
					}
					if (exceptKeys != null && exceptKeys.length>0) {
						// 全部包含就去除
						if (isExceptAll) {
							for (int i = 0; i < filterMatcheList.size(); i++) {
								String result = filterMatcheList.get(i);

								int containCount = 0;
								for (int j = 0; j < exceptKeys.length; j++) {
									if (result.contains(exceptKeys[j])) {
										containCount++;
									}
								}
								if (containCount>0 && containCount == exceptKeys.length) {
									filterMatcheList.remove(i);
									i--;
								}
							}
						} 
						// 部分包含就去除
						else {
							for (int i = 0; i < filterMatcheList.size(); i++) {
								int containCount = 0;
								String result = filterMatcheList.get(i);

								for (int j = 0; j < exceptKeys.length; j++) {
									if (result.contains(exceptKeys[j])) {
										containCount++;
									}
								}
								if (containCount > 0) {
									filterMatcheList.remove(i);
									i--;
								}
							}
						}
					}
				}
			}
		}

		return filterMatcheList;
	}
	
	private String tagName=null;
	
	
	
	// 使用栈结构保存开始和结尾标记的开始位置
	private Stack<Integer> stack=new Stack<Integer>();
	
	/**
	 * 获取网页代码中使用指定标签名组成的字符串标记截取的第一组对应的标签，截取的结果可能包含相同标签名的嵌套标签，因为这组标签是对应的。
	 * 
	 * @param tagName 指定标签名,大小无关。
	 * @return 网页代码中使用指定标签名组成的字符串标记截取第一组对应的标签。
	 */
	public String firstPairTag(String tagName){
		if (tagName==null) {
			return null;
		}
		
		//需要查找的标签名
		this.tagName=tagName;
		//用开始标签的开始部分作为开始标记
		this.ptBeginString="<"+tagName;
		//用结尾标签的开始部分作为结尾标记
		this.ptEndString="</"+tagName;
		
		//设置开始查找的位置
		this.ptBeginIndex = this.searchBegin;
		
		return nextPairTag();
	}
	
	/**
	 * 获取网页代码中使用指定标签名组成的字符串标记截取的下一组对应的标签，截取的结果可能包含相同标签名的嵌套标签，因为这组标签是对应的。
	 * 
	 * @return 网页代码中使用指定标签名组成的字符串标记截取下一组对应的标签。
	 */
	public String nextPairTag(){
		String result=null;// 保存结果
		
		if (searchEnd - ptBeginIndex < ptBeginString.length() + ptEndString.length()) {
			return result;
		}
		stack.clear();// 清空上前次调用时保存的数据。
		
		
		int bBeginIndex = -1;//开始标记的开始位置
		int eEndIndex = -1;//结尾标记的结尾位置
		
		
		//判断是否是首次查找开始标记，默认是“否”
		boolean isBeginFirst = false;
		//判断是否是首次查找结尾标记，默认是“否”
		boolean isEndFirst = false;
		
		
		//用于记录每个开始标记的开始位置
		int bPartBeginIndex =ptBeginIndex;
		
		while (true) {
			//下面的循环是用于查找开始部分标记的开始位置。
			
			// 查找开始标签的开始位置
			int bBeginIndexTmp = -1;
			int eBeginIndexTmp = -1;
			
			//用于循环的标量，保存每个标记的开始位置。
			int bPartBeginIndexTmp=bPartBeginIndex;
			
			do{
				bPartBeginIndexTmp=Utilities.ignoreCaseIndexOf(this.htmlCode,this.ptBeginString,bPartBeginIndexTmp);
				if (bPartBeginIndexTmp < 0) {
					break;
				}
				
				int ePartBeginIndexTmp=this.htmlCode.indexOf(">",bPartBeginIndexTmp);
				if (ePartBeginIndexTmp < 0) {
					break;
				}
				
//				String tag=this.htmlCode.substring(bPartBeginIndexTmp,ePartBeginIndexTmp+1);
				String tagNameTmp=getTagName(this.htmlCode.substring(bPartBeginIndexTmp,ePartBeginIndexTmp+1));
				if(tagName != null && tagName.equalsIgnoreCase(tagNameTmp)){
					if(isBeginFirst==false){
						isBeginFirst=true;
						// 开始标记的开始位置
						bBeginIndex=bPartBeginIndexTmp;
					}
					
					bBeginIndexTmp=bPartBeginIndexTmp;
					eBeginIndexTmp=ePartBeginIndexTmp;
					
					break;
				}
				
				bPartBeginIndexTmp=ePartBeginIndexTmp+1;
			
			} while (true);
			
			
			//下面的循环是用于查找结尾的部分标记的开始位置。
			
			//查找结尾标记的开始位置。
			int bEndIndexTmp = -1;
			int eEndIndexTmp = -1;
			
			//用于循环的标量，保存每个标记的开始位置。
			int bPartEndIndexTmp=-1;
			
			if(isEndFirst==false){
				isEndFirst=true;
				bPartEndIndexTmp=bPartBeginIndexTmp;
			}
			else{
				bPartEndIndexTmp=bPartBeginIndex;
			}
			
			do{
				bPartEndIndexTmp=Utilities.ignoreCaseIndexOf(this.htmlCode,this.ptEndString, bPartEndIndexTmp);
				if (bPartEndIndexTmp < 0) {
					break;
				}
				
				int ePartEndIndexTmp=this.htmlCode.indexOf(">",bPartEndIndexTmp);
				if (ePartEndIndexTmp < 0) {
					break;
				}
				
//				String tag=this.htmlCode.substring(bPartEndIndexTmp,ePartEndIndexTmp+1);
				String tagNameTmp=getTagName(this.htmlCode.substring(bPartEndIndexTmp,ePartEndIndexTmp+1));
				if(tagName!=null && tagName.equalsIgnoreCase(tagNameTmp)){
					// 结尾标记的结尾位置
					eEndIndex=ePartEndIndexTmp;
					
					bEndIndexTmp=bPartEndIndexTmp;
					eEndIndexTmp=ePartEndIndexTmp;
					
					break;
				}
				
				bPartEndIndexTmp=ePartEndIndexTmp+1;
				
			} while (true);
						
			// 当开始和结尾标记都存在时
			if (bBeginIndexTmp >= 0 && bEndIndexTmp > 0) {
				
				// 开始标签的开始位置小于结尾标签的开始位置
				if (bBeginIndexTmp < bEndIndexTmp) {
					
					stack.push(bPartBeginIndex);// 进栈
					
					// eBeginIndexTmp值是“>”位置，所以加 1
					bPartBeginIndex = eBeginIndexTmp+1;
				} 
				else {
					if(stack.isEmpty()){
						break;
					}
					else{
						// 出栈
						stack.pop();
						// eEndIndexTmp值是“>”位置，所以加 1
						bPartBeginIndex = eEndIndexTmp+1;
					}
				}
			}
			// 当某次查找不存在开始标签，但存在结尾标签时 
			else if (bBeginIndexTmp < 0 && bEndIndexTmp > 0) {
				if(stack.isEmpty()){
					break;
				}
				else{
					// 出栈
					stack.pop();
					// eEndIndexTmp值是“>”位置，所以加 1
					bPartBeginIndex = eEndIndexTmp+1;
				}
			} 
			//当开始标签存在结尾标签不存在时，或者两者都不存在时
			else {
				break;
			}
			
			if(stack.isEmpty()){
				break;
			}
		}
		
		if(bBeginIndex >= 0 && eEndIndex >= bBeginIndex){
			this.ptBeginIndex=eEndIndex+1;
			// 截取结果
			result = this.htmlCode.substring(bBeginIndex, eEndIndex+1);
		}
		
		return result;
	}
	
	/**
	 * 获取网页代码中使用指定标签名组成的字符串标记截取的所有对应的标签，截取的结果可能包含相同标签名的嵌套标签，因为这组标签是对应的。
	 * 
	 * 注：截取的结果可能包含相同标签名的嵌套标签，因为这组标签是对应的。
	 * 
	 * @param tagName 指定标签名,大小无关。
	 * @return 网页代码中使用指定标签名组成的字符串标记截取的所有对应的标签。
	 */
	public List<String> getPairTagList(String tagName){
		List<String> tagList = new ArrayList<String>();

		String result = firstPairTag(tagName);
		while (result != null) {
			
			tagList.add(result);
			result = nextPairTag();
		}

		return tagList;
	}
	
	/**
	 * 获取网页代码中使用指定标签名组成的字符串标记截取的，且经过containKey关键字过滤的所有对应的标签。
	 * 
	 * 注：截取的结果可能包含相同标签名的嵌套标签，因为这组标签是对应的；containtKey必须存在。
	 * 
	 * @param tagName  指定标签名,大小无关。
	 * @param containKey 截取的结果中包含的关键字。
	 * @return 网页代码中使用指定标签名组成的字符串标记截取的所有对应的标签。
	 */
	public List<String> getPairTagList(String tagName,String containKey){
		return getPairTagList(tagName, new String[]{containKey});
	}
	
	/**
	 * 获取网页代码中使用指定标签名组成的字符串标记截取的，且经过containKey关键字过滤的所有对应的标签。
	 * 
	 * 注：截取的结果可能包含相同标签名的嵌套标签，因为这组标签是对应的；containKeys同时存在。
	 * 
	 * @param tagName 指定标签名,大小无关。
	 * @param containKeys 截取的结果中包含的关键字数组。 
	 * @return 网页代码中使用指定标签名组成的字符串标记截取的所有对应的标签。
	 */
	public List<String> getPairTagList(String tagName,String containKeys[]){
		return getPairTagList(tagName, containKeys, null);
	}
	
	/**
	 * 获取网页代码中使用指定的标签名组成的字符串标记截取的，且经过containKeys里和exceptKeys里的多个关键字过滤的所有对应的标签。
	 * 
	 * 注：截取的结果可能包含相同标签名的嵌套标签，因为这组标签是对应的； containKeys同时存在，exceptKeys同时不存在。
	 * 
	 * @param tagName 指定标签名,大小无关。
	 * @param containKeys 截取的结果中包含的关键字数组。
	 * @param exceptKeys 截取的结果中不含的关键字数组。 
	 * @return 网页代码中使用指定标签名组成的字符串标记截取的所有对应的标签。
	 */
	public List<String> getPairTagList(String tagName,String containKeys[],String exceptKeys[]){
		return getPairTagList(tagName, containKeys, true, exceptKeys, true);
	}
	
	/**
	 * 获取网页代码中使用指定的标签名组成的字符串标记截取的，且经过containKeys里和exceptKeys里的多个关键字过滤的所有对应的标签。
	 * 
	 * 注：截取的结果可能包含相同标签名的嵌套标签，因为这组标签是对应的。
	 * 
	 * @param tagName 指定标签名,大小无关。
	 * @param containKeys 截取的结果中包含的关键字数组。
	 * @param isContainAll 截取的结果中是否全部包含containKeys，是为true，否为false。
	 * @param exceptKeys 截取的结果中不含的关键字数组。
	 * @param isExceptAll 截取的结果中是否全部不含exceptKeys，是为true，否为false。
	 * @return 网页代码中使用指定标签名组成的字符串标记截取的所有对应的标签。
	 */
	public List<String> getPairTagList(String tagName,String containKeys[],boolean isContainAll,String exceptKeys[],boolean isExceptAll){
		List<String> filterMatcheList = null;

		List<String> tagList = getPairTagList(tagName);
		if (tagList != null) {
			
			filterMatcheList = new ArrayList<String>();
			
			if (containKeys == null || containKeys.length==0) {
				if (exceptKeys == null || exceptKeys.length==0) {
					
					filterMatcheList = tagList;
				
				} else {
					// 全部包含就去除
					if (isExceptAll) {
						for (String result : tagList) {
							int containCount = 0;
							for (int i = 0; i < exceptKeys.length; i++) {
								if (result.contains(exceptKeys[i])) {
									containCount++;
								}
							}
							if (containCount != exceptKeys.length) {
								filterMatcheList.add(result);
							}
						}
					}
					// 部分包含就去除
					else {
						for (String result : tagList) {
							int containCount = 0;
							for (int i = 0; i < exceptKeys.length; i++) {
								if (result.contains(exceptKeys[i])) {
									containCount++;
								}
							}
							if (containCount == 0) {
								filterMatcheList.add(result);
							}
						}
					}
				}
			} else {
				// 全部包含就提取
				if (isContainAll) {
					for (String result : tagList) {
						
						int containCount = 0;
						for (int i = 0; i < containKeys.length; i++) {
							if (result.contains(containKeys[i])) {
								containCount++;
							}
						}
						if (containCount == containKeys.length) {
							filterMatcheList.add(result);
						}
					}
					if (exceptKeys != null && exceptKeys.length>0) {
						// 全部包含就去除
						if (isExceptAll) {
							for (int i = 0; i < filterMatcheList.size(); i++) {
								String result = filterMatcheList.get(i);
								
								int containCount = 0;
								for (int j = 0; j < exceptKeys.length; j++) {
									if (result.contains(exceptKeys[j])) {
										containCount++;
									}
								}
								if (containCount == exceptKeys.length) {
									filterMatcheList.remove(i);
									i--;
								}
							}
						} 
						// 部分包含就去除
						else {
							for (int i = 0; i < filterMatcheList.size(); i++) {
								int containCount = 0;
								String result = filterMatcheList.get(i);

								for (int j = 0; j < exceptKeys.length; j++) {
									if (result.contains(exceptKeys[j])) {
										containCount++;
									}
								}
								if (containCount > 0) {
									filterMatcheList.remove(i);
									i--;
								}
							}
						}
					}
				}
				// 部分包含就提取
				else {
					for (String result : tagList) {
						int containCount = 0;
						for (int i = 0; i < containKeys.length; i++) {
							if (result.contains(containKeys[i])) {
								containCount++;
							}
						}
						if (containCount > 0) {
							filterMatcheList.add(result);
						}
					}
					if (exceptKeys != null && exceptKeys.length>0) {
						if (isExceptAll) {
							for (int i = 0; i < filterMatcheList.size(); i++) {
								int containCount = 0;
								String result = filterMatcheList.get(i);

								for (int j = 0; j < exceptKeys.length; j++) {
									if (result.contains(exceptKeys[j])) {
										containCount++;
									}
								}
								if (containCount == exceptKeys.length) {
									filterMatcheList.remove(i);
									i--;
								}
							}
						} else {
							for (int i = 0; i < filterMatcheList.size(); i++) {
								int containCount = 0;
								String result = filterMatcheList.get(i);

								for (int j = 0; j < exceptKeys.length; j++) {
									if (result.contains(exceptKeys[j])) {
										containCount++;
									}
								}
								if (containCount > 0) {
									filterMatcheList.remove(i);
									i--;
								}
							}
						}
					}
				}
			}
		}

		return filterMatcheList;
	}
	
	/**
	 * 获取网页代码中使用指定的标签名组成的字符串标记截取的第一组双标签。
	 * 注： 获取的是最内层标签，相同的标签名，不会存在嵌套。
	 * 
	 * @param tagName 指定标签名,大小无关。
	 * @return 指定的标签名组成的字符串标记截取的第一组双标签。
	 */
	public String firstBetweenTag(String tagName) {
		if(tagName==null) {
			return null;
		}
		
		//设置查找的标签名
		this.tagName=tagName;
		
		this.btBeginString = "<" + tagName;
		this.btEndString = "</" + tagName;
		
		//设置开始查找的位置
		this.btBeginIndex = this.searchBegin;
		
		return nextBetweenTag();
	}
	
	/**
	 * 获取网页代码中使用指定的标签名组成的字符串标记截取的下一组双标签。
	 * 注：获取的是最内层标签，相同的标签名，不会存在嵌套。
	 * 
	 * @return 指定的标签名组成的字符串标记截取的下一组双标签。
	 */
	public String nextBetweenTag() {
		if (searchEnd - btBeginIndex < btBeginString.length() + btEndString.length()) {
			return null;
		}
		
		int bIndexBegin = -1;
		int eIndexBegin = -1;
		
		int loopBeginIndex=btBeginIndex;
		while(true){
			
			int bIndexBeginTmp = Utilities.ignoreCaseIndexOf(htmlCode,btBeginString, loopBeginIndex);
			if (bIndexBeginTmp < 0) {
				return null;
			}
			
			int eIndexBeginTmp=htmlCode.indexOf(">",bIndexBeginTmp+btBeginString.length());
			if(eIndexBeginTmp < 0){
				return null;
			}
			
//			String tag=htmlCode.substring(bIndexBeginTmp,eIndexBeginTmp+1);
			String tagName=getTagName(htmlCode.substring(bIndexBeginTmp,eIndexBeginTmp+1));
			if(tagName!=null && tagName.equalsIgnoreCase(this.tagName)){
//				bIndexBegin=bIndexBeginTmp;
				eIndexBegin=eIndexBeginTmp+1;
				break;
			}
			
			loopBeginIndex=eIndexBeginTmp+1;
		}
		
		int bIndexEnd = -1;
		int eIndexEnd = -1;
		
		int loopEndIndex=eIndexBegin;
		while(true){
			int bIndexEndTmp = Utilities.ignoreCaseIndexOf(htmlCode,btEndString, loopEndIndex);
			if (bIndexEndTmp < 0) {
				return null;
			}
			
			int eIndexEndTmp=htmlCode.indexOf(">",bIndexEndTmp+btEndString.length());
			if(eIndexEndTmp < 0){
				return null;
			}
			
//			String tag=htmlCode.substring(bIndexEndTmp,eIndexEndTmp+1);
			String tagName=getTagName(htmlCode.substring(bIndexEndTmp,eIndexEndTmp+1));
			if(tagName!=null && tagName.equalsIgnoreCase(this.tagName)){
				bIndexEnd=bIndexEndTmp;
				eIndexEnd=eIndexEndTmp+1;
				break;
			}
			
			loopEndIndex=eIndexEndTmp+1;
		}
		
		loopBeginIndex=bIndexEnd-1;
		while(true){
			
			// 这步需要注意：为了确定 开始、结尾标记之间 没有 开始标记。
			int bIndexBeginTmp = Utilities.ignoreCaseLastIndexOf(htmlCode,btBeginString, loopBeginIndex);
			if (bIndexBeginTmp < 0) {
				return null;
			}
			
			int eIndexBeginTmp=htmlCode.indexOf(">",bIndexBeginTmp+btBeginString.length());
			if(eIndexBeginTmp < 0){
				return null;
			}
			
//			String tag=htmlCode.substring(bIndexBeginTmp,eIndexBeginTmp+1);
			String tagName=getTagName(htmlCode.substring(bIndexBeginTmp,eIndexBeginTmp+1));
			if(tagName!=null && tagName.equalsIgnoreCase(this.tagName)){
				bIndexBegin=bIndexBeginTmp;
//				eIndexBegin=eIndexBeginTmp+1;
				break;
			}
			
			loopBeginIndex=bIndexBeginTmp-1;
		}

		btBeginIndex = eIndexEnd;

		return htmlCode.substring(bIndexBegin, eIndexEnd);
	}
	
	/**
	 * 获取网页代码中所有使用指定的标签名组成的字符串标记截取的双标签。
	 * @param tagName 指定标签名,大小无关。
	 * @return 网页代码中所有使用指定的标签名组成的字符串标记截取的双标签。
	 */
	public List<String> getBetweenTagList(String tagName) {
		List<String> tagList = new ArrayList<String>();

		String reslut = firstBetweenTag(tagName);
		while (reslut != null) {
			
			tagList.add(reslut);
			reslut = nextBetweenTag();
		}

		return tagList;
	}

	/**
	 * 获取网页代码中所有使用指定的标签名组成的字符串标记截取的，且经过containKey关键字过滤的双标签。
	 * 
	 * 注：containKey存在。
	 * 
	 * @param tagName 指定标签名,大小无关。
	 * @param containKey 截取的结果中包含的关键字。
	 * @return 网页代码中所有使用指定的标签名组成的字符串标记截取的，且经过containKey关键字过滤的双标签。
	 */
	public List<String> getBetweenTagList(String tagName, String containKey) {
		String containKeys[] = { containKey };
		return getBetweenTagList(tagName, containKeys);
	}

	/**
	 * 获取网页代码中所有使用指定的标签名组成的字符串标记截取的，且经过containKeys里的多个关键字过滤的双标签。
	 * 
	 * 注：containKeys同时存在。
	 * 
	 * @param tagName 指定标签名,大小无关。
	 * @param containKeys 截取的结果中包含的关键字数组。
	 * @return 网页代码中所有使用指定的标签名组成的字符串标记截取的，且经过containKeys里多个关键字过滤的双标签。
	 */
	public List<String> getBetweenTagList(String tagName, String containKeys[]) {
		return getBetweenTagList(tagName, containKeys, null);
	}

	/**
	 * 获取网页代码中所有使用指定的标签名组成的字符串标记截取的，且经过containKeys里和exceptKeys里的多个关键字过滤的双标签。
	 * 
	 * 注：containKeys同时存在，exceptKeys同时不存在。
	 * 
	 * @param tagName 指定标签名,大小无关。
	 * @param containKeys 截取的结果中包含的关键字数组。
	 * @param isContainAll 截取的结果中是否全部包含containKeys，是为true，否为false。
	 * @param exceptKeys 截取的结果中不含的关键字数组。
	 * @param isExceptAll 截取的结果中是否全部不含exceptKeys，是为true，否为false。
	 * @return 网页代码中所有使用指定的标签名组成的字符串标记截取的，且经过containKeys里和exceptKeys里的多个关键字过滤的双标签。
	 */
	public List<String> getBetweenTagList(String tagName, String containKeys[],
			String exceptKeys[]) {
		return this.getBetweenTagList(tagName, containKeys, true, exceptKeys, true);
	}

	/**
	 * 获取网页代码中所有使用指定的标签名组成的字符串标记截取的，且经过containKeys里和exceptKeys里的多个关键字过滤的双标签。
	 * 
	 * @param tagName 指定标签名,大小无关。
	 * @param containKeys 截取的结果中包含的关键字数组。
	 * @param isContainAll 截取的结果中是否全部包含containKeys，是为true，否为false。
	 * @param exceptKeys 截取的结果中不含的关键字数组。
	 * @param isExceptAll 截取的结果中是否全部不含exceptKeys，是为true，否为false。
	 * @return 网页代码中所有使用指定的标签名组成的字符串标记截取的，且经过containKeys里和exceptKeys里的多个关键字过滤的双标签。
	 */
	public List<String> getBetweenTagList(String tagName, String containKeys[],
			boolean isContainAll, String exceptKeys[], boolean isExceptAll) {
		
		List<String> filterTagList = null;

		List<String> tagList = getBetweenTagList(tagName);
		if (tagList != null) {
			
			filterTagList = new ArrayList<String>();
			if (containKeys == null || containKeys.length==0) {
				if (exceptKeys == null || exceptKeys.length==0) {
					
					filterTagList = tagList;
					
				} else {
					// 全部包含就去除
					if (isExceptAll) {
						
						for (String result : tagList) {
							int containCount = 0;

							for (int i = 0; i < exceptKeys.length; i++) {
								if (result.contains(exceptKeys[i])) {
									containCount++;
								}
							}
							if (containCount != exceptKeys.length) {
								filterTagList.add(result);
							}
						}
					}
					// 部分包含就去除
					else {
						for (String result : tagList) {
							int containCount = 0;

							for (int i = 0; i < exceptKeys.length; i++) {
								if (result.contains(exceptKeys[i])) {
									containCount++;
								}
							}
							if (containCount == 0) {
								filterTagList.add(result);
							}
						}
					}
				}
			} else {
				// 全部包含就提取
				if (isContainAll) {
					for (String result : tagList) {
						int containCount = 0;

						for (int i = 0; i < containKeys.length; i++) {
							if (result.contains(containKeys[i])) {
								containCount++;
							}
						}
						if (containCount == containKeys.length) {
							filterTagList.add(result);
						}
					}
					if (exceptKeys != null && exceptKeys.length>0) {
						// 全部包含就提取
						if (isExceptAll) {
							for (int i = 0; i < filterTagList.size(); i++) {
								String result = filterTagList.get(i);
								int containCount = 0;

								for (int j = 0; j < exceptKeys.length; j++) {
									if (result.contains(exceptKeys[j])) {
										containCount++;
									}
								}
								if (containCount == exceptKeys.length) {
									filterTagList.remove(i);
									i--;
								}
							}
						} 
						// 部分包含就提取
						else {
							for (int i = 0; i < filterTagList.size(); i++) {
								String result = filterTagList.get(i);
								int containCount = 0;

								for (int j = 0; j < exceptKeys.length; j++) {
									if (result.contains(exceptKeys[j])) {
										containCount++;
									}
								}
								if (containCount > 0) {
									filterTagList.remove(i);
									i--;
								}
							}
						}
					}
				} 
				// 部分包含就提取
				else {
					for (String result : tagList) {
						int containCount = 0;

						for (int i = 0; i < containKeys.length; i++) {
							if (result.contains(containKeys[i])) {
								containCount++;
							}
						}
						if (containCount > 0) {
							filterTagList.add(result);
						}
					}
					if (exceptKeys != null && exceptKeys.length>0) {
						// 全部包含就提取
						if (isExceptAll) {
							for (int i = 0; i < filterTagList.size(); i++) {
								String result = filterTagList.get(i);
								int containCount = 0;

								for (int j = 0; j < exceptKeys.length; j++) {
									if (result.contains(exceptKeys[j])) {
										containCount++;
									}
								}
								if (containCount == exceptKeys.length) {
									filterTagList.remove(i);
									i--;
								}
							}
						}
						// 部分包含就提取
						else {
							for (int i = 0; i < filterTagList.size(); i++) {
								String result = filterTagList.get(i);
								int containCount = 0;

								for (int j = 0; j < exceptKeys.length; j++) {
									if (result.contains(exceptKeys[j])) {
										containCount++;
									}
								}
								if (containCount > 0) {
									filterTagList.remove(i);
									i--;
								}
							}
						}
					}
				}
			}
		}
		return filterTagList;
	}
	
	/**
	 * 从使用字符串标记截取网页代码中第一个子字符串中，提取匹配指定带捕获组的正则表达式的子字符串数组。
	 * 
	 * 注：截取的结果可能包含相同标签名的嵌套标签，因为这组标签是对应的。
	 * 
	 * @param begin 字符串开始标记。
	 * @param end 字符串结尾标记。
	 * @param regexp 带捕获组的正则表达式。
	 * @return 提取第一个截取结果用指定带捕获组的正则表达式匹配的子字符串。
	 */
	public String[] firstStringMatches(String begin, String end, String regexp) {
		if(begin==null || end==null || regexp==null){
			return null;
		}
		
		this.besBeginString = begin;
		this.besEndString = end;

		this.besBeginIndex = this.searchBegin;

		this.strMtchPattern = Pattern.compile(regexp);

		return nextStringMatches();
	}

	/**
	 * 从使用字符串标记截取网页代码中下一个子字符串中，提取匹配指定带捕获组的正则表达式的子字符串数组。
	 * 
	 * 注：截取的结果可能包含相同标签名的嵌套标签，因为这组标签是对应的。
	 * 
	 * @return 提取下一个截取结果用指定带捕获组的正则表达式匹配的子字符串数组。
	 */
	public String[] nextStringMatches() {
		// 获取下一组用字符串标记截取的结果
		String result = nextStringMatche();

		if (result != null) {
			Matcher matcher = strMtchPattern.matcher(result);
			if (matcher.find()) {
				String[] array = new String[matcher.groupCount()];
				for (int i = 0; i < array.length; ++i) {
					// 获取正则表达式中每个捕获组匹配的值
					array[i] = matcher.group(i + 1);
				}
				return array;
			}
		}

		return null;
	}

	/**
	 * 从使用字符串标记截取网页代码中所有子字符串中，提取匹配指定带捕获组的正则表达式的子字符串数组。
	 * 
	 * 注：截取的结果可能包含相同标签名的嵌套标签，因为这组标签是对应的。
	 * 
	 * @param begin 字符串开始标记。
	 * @param end 字符串结尾标记。
	 * @param regexp 带捕获组的正则表达式。
	 * @return  提取所有截取结果用指定带捕获组的正则表达式匹配的子字符串数组。
	 */
	public List<String[]> getStringMatchesList(String begin, String end,
			String regexp) {
		
		List<String[]> matcheList = new ArrayList<String[]>();

		String results[] = firstStringMatches(begin, end, regexp);
		while (results != null) {
			matcheList.add(results);
			results = nextStringMatches();
		}

		return matcheList;
	}

	/**
	 * 从使用字符串标记截取网页代码中所有子字符串中，提取匹配指定带捕获组的正则表达式的，且经过containKey单个关键字过滤的子字符串后数组。
	 * 
	 * 注：截取的结果可能包含相同标签名的嵌套标签，因为这组标签是对应的；containKey存在。
	 * 
	 * @param begin 字符串开始标记。
	 * @param end 字符串结尾标记。
	 * @param regexp 带捕获组的正则表达式。
	 * @param containKey 包含的关键字。
	 * @return 提取所有截取结果用指定带捕获组的正则表达式匹配的子字符串数组。
	 */
	public List<String[]> getStringMatchesList(String begin, String end,
			String regexp, String containKey) {
		
		return this.getStringMatchesList(begin, end, regexp,
				new String[] { containKey }, null);
	}

	/**
	 * 从使用字符串标记截取网页代码中所有子字符串中，提取匹配指定带捕获组的正则表达式的，且经过containKeys里的多个关键字过滤的子字符串后数组。
	 * 
	 * 注：截取的结果可能包含相同标签名的嵌套标签，因为这组标签是对应的；containKeys同时存在。
	 * 
	 * @param begin 字符串开始标记。
	 * @param end 字符串结尾标记。
	 * @param regexp 带捕获组的正则表达式。
	 * @param containKeys 包含的关键字。
	 * @return 提取所有截取结果用指定带捕获组的正则表达式匹配的子字符串数组。
	 */
	public List<String[]> getStringMatchesList(String begin, String end,
			String regexp, String[] containKeys) {
		
		return this.getStringMatchesList(begin, end, regexp, containKeys, null);
	}

	/**
	 * 从使用字符串标记截取网页代码中所有子字符串中，提取匹配指定带捕获组的正则表达式的，且经过containKeys里和exceptKeys里的多个关键字过滤的子字符串后数组。
	 * 
	 * 注：截取的结果可能包含相同标签名的嵌套标签，因为这组标签是对应的；containKeys同时存在，exceptKeys同时不存在。
	 *
	 * @param begin 字符串开始标记。
	 * @param end 字符串结尾标记。
	 * @param regexp 带捕获组的正则表达式。
	 * @param containKeys 匹配的结果中包含的关键字数组。
	 * @param exceptKeys 匹配的结果中不含的关键字数组。
	 * @return 提取所有截取结果用指定带捕获组的正则表达式匹配的子字符串数组。
	 */
	public List<String[]> getStringMatchesList(String begin, String end,
			String regexp, String[] containKeys, String[] exceptKeys) {
		
		return this.getStringMatchesList(begin, end, regexp, containKeys, true,
				exceptKeys, true);
	}

	/**
	 * 从使用字符串标记截取网页代码中所有子字符串中，提取匹配指定带捕获组的正则表达式的，且经过containKeys里和exceptKeys里的多个关键字过滤的子字符串后数组。
	 * 
	 * 注：截取的结果可能包含相同标签名的嵌套标签，因为这组标签是对应的。
	 * 
	 * @param begin 字符串开始标记。
	 * @param end 字符串结尾标记。
	 * @param regexp 带捕获组的正则表达式。
	 * @param containKeys 匹配的结果中包含的关键字数组。
	 * @param isContainAll 匹配的结果中是否全部包含containKeys，是为true，否为false。
	 * @param exceptKeys 匹配的结果中不含的关键字数组。
	 * @param isExceptAll 匹配的结果中是否全部不含exceptKeys，是为true，否为false。
	 * @return 提取所有截取结果用指定带捕获组的正则表达式匹配的子字符串数组。
	 */
	public List<String[]> getStringMatchesList(String begin, String end,
			String regexp, String[] containKeys, boolean isContainAll,
			String[] exceptKeys, boolean isExceptAll) {
		
		List<String[]> filterMatchesList = null;
		
		List<String[]> matchesList = getStringMatchesList(begin, end,regexp);
		if (matchesList != null) {
			
			filterMatchesList = new ArrayList<String[]>();

			if (containKeys == null || containKeys.length==0) {
				if (exceptKeys == null || exceptKeys.length==0) {
					
					filterMatchesList = matchesList;
				} 
				else {
					// 全部包含就去除
					if (isExceptAll) {
						for (String[] results : matchesList) {

							ArrayList<String> resultList = new ArrayList<String>();
							for (int i = 0; i < results.length; i++) {
								int containCount = 0;
								for (int j = 0; j < exceptKeys.length; j++) {
									if (results[i].contains(exceptKeys[j])) {
										containCount++;
									}
								}
								if (containCount != exceptKeys.length) {
									resultList.add(results[i]);
								}
							}

							filterMatchesList.add(toArray(resultList));
						}
					} 
					// 部分包含就去除
					else {
						for (String[] results : matchesList) {

							ArrayList<String> resultList = new ArrayList<String>();
							for (int i = 0; i < results.length; i++) {
								int containCount = 0;
								for (int j = 0; j < exceptKeys.length; j++) {
									if (results[i].contains(exceptKeys[j])) {
										containCount++;
									}
								}
								if (containCount == 0) {
									resultList.add(results[i]);
								}
							}

							filterMatchesList.add(toArray(resultList));
						}
					}
				}
			} 
			else {
				// 全部包含就提取
				if (isContainAll) {
					for (String[] results : matchesList) {

						ArrayList<String> resultList = new ArrayList<String>();
						for (int i = 0; i < results.length; i++) {
							int containCount = 0;
							for (int j = 0; j < containKeys.length; j++) {
								if (results[i].contains(containKeys[j])) {
									containCount++;
								}
							}
							if (containCount == containKeys.length) {
								resultList.add(results[i]);
							}
						}

						filterMatchesList.add(toArray(resultList));
					}
					if (exceptKeys != null && exceptKeys.length>0) {
						// 全部包含就去除
						if (isExceptAll) {
							for (int i = 0; i < filterMatchesList.size(); i++) {
								String[] results = filterMatchesList.get(i);

								ArrayList<String> resultList = new ArrayList<String>();
								for (int j = 0; j < results.length; j++) {
									int containCount = 0;
									for (int k = 0; k < exceptKeys.length; k++) {
										if (results[j].contains(exceptKeys[k])) {
											containCount++;
										}
									}
									if (containCount != exceptKeys.length) {
										resultList.add(results[j]);
									}
								}

								filterMatchesList.set(i, toArray(resultList));
							}
						} 
						// 部分包含就去除
						else {
							for (int i = 0; i < filterMatchesList.size(); i++) {
								String[] results = filterMatchesList.get(i);

								ArrayList<String> resultList = new ArrayList<String>();
								for (int j = 0; j < results.length; j++) {
									int containCount = 0;
									for (int k = 0; k < exceptKeys.length; k++) {
										if (results[j].contains(exceptKeys[k])) {
											containCount++;
										}
									}
									if (containCount == 0) {
										resultList.add(results[j]);
									}
								}
								filterMatchesList.set(i, toArray(resultList));
							}
						}
					}
				}
				// 部分包含就提取
				else {
					for (String[] results : matchesList) {

						ArrayList<String> resultList = new ArrayList<String>();
						for (int i = 0; i < results.length; i++) {
							int containCount = 0;
							for (int j = 0; j < containKeys.length; j++) {
								if (results[i].contains(containKeys[j])) {
									containCount++;
								}
							}
							if (containCount > 0) {
								resultList.add(results[i]);
							}
						}

						filterMatchesList.add(toArray(resultList));
					}
					if (exceptKeys != null && exceptKeys.length>0) {
						// 全部包含就去除
						if (isExceptAll) {
							for (int i = 0; i < filterMatchesList.size(); i++) {
								String[] results = filterMatchesList.get(i);

								ArrayList<String> resultList = new ArrayList<String>();
								for (int j = 0; j < results.length; j++) {
									int containCount = 0;
									for (int k = 0; k < exceptKeys.length; k++) {
										if (results[j].contains(exceptKeys[k])) {
											containCount++;
										}
									}
									if (containCount != exceptKeys.length) {
										resultList.add(results[j]);
									}
								}

								filterMatchesList.set(i, toArray(resultList));
							}
						}
						// 部分包含就去除
						else {
							for (int i = 0; i < filterMatchesList.size(); i++) {
								String[] results = filterMatchesList.get(i);

								ArrayList<String> resultList = new ArrayList<String>();
								for (int j = 0; j < results.length; j++) {
									
									int containCount = 0;
									for (int k = 0; k < exceptKeys.length; k++) {
										if (results[j].contains(exceptKeys[k])) {
											containCount++;
										}
									}
									if (containCount == 0) {
										resultList.add(results[j]);
									}
								}

								filterMatchesList.set(i, toArray(resultList));
							}
						}
					}
				}
			}
		}
		
		return filterMatchesList;
	}

	/**
	 * 从使用正则表达式标记截取网页代码中第一个子字符串中，提取匹配指定带捕获组的正则表达式的子字符串字符串数组。
	 * 
	 * 注：截取的结果可能包含相同标签名的嵌套标签，因为这组标签是对应的。
	 * 
	 * @param beginRegexp 开始正则表达式标记，如果有捕获组，则会以第一捕获组获取的字符串作开始标记。
	 * @param endRegexp 结尾正则表达式标记，如果有捕获组，则会以第一捕获组获取的字符串作结尾标记。
	 * @param regexp 带有零到多个捕获组的正则表达式。
	 * @return 提取匹配指定带捕获组的正则表达式的子字符串数组。
	 */
	public String[] firstRegexpMatches(String beginRegexp, String endRegexp,
			String regexp) {
		if(beginRegexp==null || endRegexp==null || regexp==null){
			return null;
		}
		
		this.berBeginIndex = this.searchBegin;
		
		this.regMtchPattern = Pattern.compile(regexp);
		
		this.berBeginMatcher = Pattern.compile(beginRegexp).matcher(htmlCode);
		this.berEndMatcher = Pattern.compile(endRegexp).matcher(htmlCode);

		return nextRegexpMatches();
	}

	/**
	 * 从使用正则表达式标记截取网页代码中下一个子字符串中，提取匹配指定带捕获组的正则表达式的子字符串数组。
	 * 
	 * 注：截取的结果可能包含相同标签名的嵌套标签，因为这组标签是对应的。
	 * 
	 * @param beginRegexp 开始正则表达式标记，如果有捕获组，则会以第一捕获组获取的字符串作开始标记。
	 * @param endRegexp 结尾正则表达式标记，如果有捕获组，则会以第一捕获组获取的字符串作结尾标记。
	 * @param regexp 带有零到多个捕获组的正则表达式。
	 * @return 提取匹配指定带捕获组的正则表达式的子字符串数组。
	 */
	public String[] nextRegexpMatches() {
		
		// 获取下一组用正则表达式标记截取的结果
		String result = nextRegexpMatche();

		if (result != null) {
			Matcher matcher = regMtchPattern.matcher(result);
			if (matcher.find()) {
				
				String[] array = new String[matcher.groupCount()];
				for (int i = 0; i < array.length; ++i) {

					// 获取正则表达式中每个捕获组匹配的值
					array[i] = matcher.group(i + 1);
				}
				
				return array;
			}
		}

		return null;
	}

	/**
	 * 从使用正则表达式标记截取网页代码中下一个子字符串中，提取匹配指定带捕获组的正则表达式的子字符串数组。
	 * 
	 * 注：截取的结果可能包含相同标签名的嵌套标签，因为这组标签是对应的。
	 * 
	 * @param beginRegexp 开始正则表达式标记，如果有捕获组，则会以第一捕获组获取的字符串作开始标记。
	 * @param endRegexp 结尾正则表达式标记，如果有捕获组，则会以第一捕获组获取的字符串作结尾标记。
	 * @param regex 带有零到多个捕获组的正则表达式。
	 * @return 提取匹配指定带捕获组的正则表达式的子字符串数组。
	 */
	public List<String[]> getRegexpMatchesList(String beginRegexp,
			String endRegexp, String regex) {
		List<String[]> matcheList = new ArrayList<String[]>();

		String results[] = firstStringMatches(beginRegexp, endRegexp, regex);
		while (results != null) {
			matcheList.add(results);
			results = nextStringMatches();
		}

		return matcheList;
	}

	/**
	 * 从使用正则表达式标记截取网页代码中下一个子字符串中，提取匹配指定带捕获组的正则表达式的，且经过containKey单个关键字过滤的子字符串数组。
	 * 
	 * 注：截取的结果可能包含相同标签名的嵌套标签，因为这组标签是对应的。
	 * 
	 * @param beginRegexp 开始正则表达式标记，如果有捕获组，则会以第一捕获组获取的字符串作开始标记。
	 * @param endRegexp 结尾正则表达式标记，如果有捕获组，则会以第一捕获组获取的字符串作结尾标记。
	 * @param regex 带有零到多个捕获组的正则表达式。
	 * @param containKey 截取的结果中包含的关键字。
	 * @return 提取匹配指定带捕获组的正则表达式的子字符串数组。
	 */
	public List<String[]> getRegexpMatchesList(String beginRegexp,
			String endRegexp, String regexp, String containKey) {

		return this.getRegexpMatchesList(beginRegexp, endRegexp, regexp,
				new String[] { containKey }, null);
	}

	/**
	 * 从使用正则表达式标记截取网页代码中下一个子字符串中，提取匹配指定带捕获组的正则表达式的，且经过containKeys里的多个关键字过滤的子字符串数组。
	 * 
	 * 注：截取的结果可能包含相同标签名的嵌套标签，因为这组标签是对应的。
	 * 
	 * @param beginRegexp 开始正则表达式标记，如果有捕获组，则会以第一捕获组获取的字符串作开始标记。
	 * @param endRegexp 结尾正则表达式标记，如果有捕获组，则会以第一捕获组获取的字符串作结尾标记。
	 * @param regex 带有零到多个捕获组的正则表达式。
	 * @param containKeys 截取的结果中包含的关键字数组。
	 * @return 提取匹配指定带捕获组的正则表达式的子字符串数组。
	 */
	public List<String[]> getRegexpMatchesList(String beginRegexp,
			String endRegexp, String regexp, String[] containKeys) {

		return this.getRegexpMatchesList(beginRegexp, endRegexp, regexp,
				containKeys, null);
	}

	/**
	 * 从使用正则表达式标记截取网页代码中下一个子字符串中，提取匹配指定带捕获组的正则表达式的，且经过containKeys里和exceptKeys里的多个关键字过滤的子字符串数组。
	 * 
	 * 注：截取的结果可能包含相同标签名的嵌套标签，因为这组标签是对应的。
	 * 
	 * @param beginRegexp 开始正则表达式标记，如果有捕获组，则会以第一捕获组获取的字符串作开始标记。
	 * @param endRegexp 结尾正则表达式标记，如果有捕获组，则会以第一捕获组获取的字符串作结尾标记。
	 * @param regexp 带有零到多个捕获组的正则表达式。
	 * @param containKeys 截取的结果中包含的关键字数组。
	 * @param exceptKeys 截取的结果中不含的关键字数组。
	 * @return 提取匹配指定带捕获组的正则表达式的子字符串数组。
	 */
	public List<String[]> getRegexpMatchesList(String begin, String end,
			String regexp, String[] containKeys, String[] exceptKeys) {

		return this.getRegexpMatchesList(begin, end, regexp, containKeys, true,
				exceptKeys, true);
	}

	/**
	 * 从使用正则表达式标记截取网页代码中下一个子字符串中，提取匹配指定带捕获组的正则表达式的，且经过containKeys里和exceptKeys里的多个关键字过滤的子字符串数组。
	 * 
	 * 注：截取的结果可能包含相同标签名的嵌套标签，因为这组标签是对应的。
	 * 
	 * @param beginRegexp 开始正则表达式标记，如果有捕获组，则会以第一捕获组获取的字符串作开始标记。
	 * @param endRegexp 结尾正则表达式标记，如果有捕获组，则会以第一捕获组获取的字符串作结尾标记。
	 * @param regexp 带有零到多个捕获组的正则表达式。
	 * @param containKeys 截取的结果中包含的关键字数组。
	 * @param isContainAll 截取的结果中是否全部包含containKeys，是为true，否为false。
	 * @param exceptKeys 截取的结果中不含的关键字数组。
	 * @param isExceptAll 截取的结果中是否全部不含exceptKeys，是为true，否为false。
	 * @return 提取匹配指定带捕获组的正则表达式的子字符串数组。
	 */
	public List<String[]> getRegexpMatchesList(String beginRegexp,
			String endRegexp, String regexp, String[] containKeys,
			boolean isContainAll, String[] exceptKeys, boolean isExceptAll) {

		List<String[]> filterMatchesList = null;

		List<String[]> matchesList = getRegexpMatchesList(beginRegexp, endRegexp,regexp);
		if (matchesList != null) {
			filterMatchesList = new ArrayList<String[]>();

			if (containKeys == null || containKeys.length==0) {
				if (exceptKeys == null || exceptKeys.length==0) {
					
					filterMatchesList = matchesList;
				
				} else {
					// 全部包含就去除
					if (isExceptAll) {
						for (String[] results : matchesList) {
							ArrayList<String> resultList = new ArrayList<String>();

							for (int i = 0; i < results.length; i++) {
								int containCount = 0;
								for (int j = 0; j < exceptKeys.length; j++) {
									if (results[i].contains(exceptKeys[j])) {
										containCount++;
									}
								}
								if (containCount != exceptKeys.length) {
									resultList.add(results[i]);
								}
							}

							filterMatchesList.add(toArray(resultList));
						}
					} 
					// 部分包含就去除
					else {
						for (String[] results : matchesList) {
							ArrayList<String> resultList = new ArrayList<String>();

							for (int i = 0; i < results.length; i++) {
								int containCount = 0;
								for (int j = 0; j < exceptKeys.length; j++) {
									if (results[i].contains(exceptKeys[j])) {
										containCount++;
									}
								}
								if (containCount == 0) {
									resultList.add(results[i]);
								}
							}

							filterMatchesList.add(toArray(resultList));
						}
					}
				}
			} else {
				// 全部包含就提取
				if (isContainAll) {
					for (String[] results : matchesList) {
						ArrayList<String> resultList = new ArrayList<String>();

						for (int i = 0; i < results.length; i++) {
							int containCount = 0;
							for (int j = 0; j < containKeys.length; j++) {
								if (results[i].contains(containKeys[j])) {
									containCount++;
								}
							}
							if (containCount == containKeys.length) {
								resultList.add(results[i]);
							}
						}

						filterMatchesList.add(toArray(resultList));
					}
					if (exceptKeys != null && exceptKeys.length>0) {
						// 全部包含就去除
						if (isExceptAll) {
							for (int i = 0; i < filterMatchesList.size(); i++) {
								String[] results = filterMatchesList.get(i);

								ArrayList<String> resultList = new ArrayList<String>();
								for (int j = 0; j < results.length; j++) {
									int containCount = 0;
									for (int k = 0; k < exceptKeys.length; k++) {
										if (results[j].contains(exceptKeys[k])) {
											containCount++;
										}
									}
									if (containCount != exceptKeys.length) {
										resultList.add(results[j]);
									}
								}

								filterMatchesList.set(i, toArray(resultList));
							}
						} 
						// 部分包含就去除
						else {
							for (int i = 0; i < filterMatchesList.size(); i++) {
								String[] results = filterMatchesList.get(i);

								ArrayList<String> resultList = new ArrayList<String>();
								for (int j = 0; j < results.length; j++) {
									int containCount = 0;
									for (int k = 0; k < exceptKeys.length; k++) {
										if (results[j].contains(exceptKeys[k])) {
											containCount++;
										}
									}
									if (containCount == 0) {
										resultList.add(results[j]);
									}
								}
								filterMatchesList.set(i, toArray(resultList));
							}
						}
					}
				}
				// 部分包含就提取
				else {
					for (String[] results : matchesList) {

						ArrayList<String> resultList = new ArrayList<String>();
						for (int i = 0; i < results.length; i++) {
							int containCount = 0;
							for (int j = 0; j < containKeys.length; j++) {
								if (results[i].contains(containKeys[j])) {
									containCount++;
								}
							}
							if (containCount > 0) {
								resultList.add(results[i]);
							}
						}

						filterMatchesList.add(toArray(resultList));
					}
					if (exceptKeys != null && exceptKeys.length>0) {
						// 全部包含就去除
						if (isExceptAll) {
							for (int i = 0; i < filterMatchesList.size(); i++) {
								String[] results = filterMatchesList.get(i);

								ArrayList<String> resultList = new ArrayList<String>();
								for (int j = 0; j < results.length; j++) {
									int containCount = 0;
									for (int k = 0; k < exceptKeys.length; k++) {
										if (results[j].contains(exceptKeys[k])) {
											containCount++;
										}
									}
									if (containCount != exceptKeys.length) {
										resultList.add(results[j]);
									}
								}

								filterMatchesList.set(i, toArray(resultList));
							}
						} 
						// 部分包含就去除
						else {
							for (int i = 0; i < filterMatchesList.size(); i++) {
								String[] results = filterMatchesList.get(i);

								ArrayList<String> resultList = new ArrayList<String>();
								for (int j = 0; j < results.length; j++) {
									int containCount = 0;
									for (int k = 0; k < exceptKeys.length; k++) {
										if (results[j].contains(exceptKeys[k])) {
											containCount++;
										}
									}
									if (containCount == 0) {
										resultList.add(results[j]);
									}
								}

								filterMatchesList.set(i, toArray(resultList));
							}
						}
					}
				}
			}
		}
		return filterMatchesList;
	}

	/**
	 * 把列表转换成数组。
	 * 
	 * @param list 需转换的列表。
	 * @return 列表转换成的数组。
	 */
	private String[] toArray(ArrayList<String> list) {
		String[] array = null;
		if (list != null) {
			array = new String[list.size()];

			list.toArray(array);
		}
		return array;
	}

	private boolean loadHtmlTag=true;
	
	/**
	 * 返回是否要加载HTML标签，默认为 true。
	 * @return 是否要加载HTML标签，要为true，否为false
	 */
	public boolean isLoadHtmlTag() {
		return loadHtmlTag;
	}
	
	/**
	 * 设置是否要加载HTML标签，默认为 true。
	 * @param loadHtmlTag 是否要加载HTML标签，要为true，否为false
	 */
	public void setLoadHtmlTag(boolean loadHtmlTag) {
		this.loadHtmlTag = loadHtmlTag;
	}

	/**
	 * 加载文件中标准的HTML标签名。
	 */
	private void loadHtmlTag() {
		BufferedReader reader = null;
		try {
			
			//判断是否要加载HTML表签名
			if(loadHtmlTag==true){
				reader = new BufferedReader(new InputStreamReader(
						HtmlUtil.class.getResourceAsStream("htmltag.cfg")));
				
				String line = reader.readLine();
				while (line != null) {
					htmlTagList.add(line.trim());
					line = reader.readLine();
				}
			}
			loadHtmlTag=false; //加载成功
			
		} catch (Exception e) {
			loadHtmlTag=true; //加载失败
			logger.log(Level.WARNING, "读取html标签配置异常", e);
		} 
		finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e) {
					logger.log(Level.WARNING, "关闭读取html标签配置的流异常", e);
				}
			}
		}
	}

	/**
	 * 增加指定标签名，如果已经存在该标签名也则返回true。
	 * 
	 * @param tagName 标签名。
	 * @return 是否增加成功，成功为true，失败为false。
	 */
	public boolean addTagName(String tagName) {
		this.loadHtmlTag();
		
		if(htmlTagList.contains(tagName)){
			return true;
		}
		
		return htmlTagList.add(tagName.toLowerCase());
	}

	/**
	 * 删除指定标签名。
	 * 
	 * @param tagName 标签名。
	 * @return 是否删除成功，成功为true，失败为false。
	 */
	public boolean delTagName(String tagName) {
		this.loadHtmlTag();
		return htmlTagList.remove(tagName.toLowerCase());
	}

	/**
	 * 清空所有标签名。
	 */
	public void clearTagName() {
		htmlTagList.clear();
	}
	
	/**
	 * 是否存在指定的标签名。
	 * 
	 * @param tagName 标签名。
	 * @return 存在返回true，不在返回false。
	 */
	public boolean existTagName(String tagName){
		// 加载HTML标签名
		this.loadHtmlTag();
		// 链表保存的是小写HTML标签名
		return htmlTagList.contains(tagName.toLowerCase());
	}
	
	/**
	 * 获取指定开始标签、结尾标签或单标签的标签名。
	 * 
	 * @param tag 指定的开始标签、结尾标签或单标签。
	 * @return 成功返回标签名，失败返回null。
	 */
	public static String getTagName(String tag) {
		if(tag==null){
			return null;
		}
		
		// 替换结束标签的“/” ，以便提取标签名，并且转换成小写。
		tag = tag.replace("/", "").toLowerCase();

		int index1 = tag.indexOf("<", 0);
		if (index1 < 0) {
			return null;
		}
		
		// 获取注释标签名
		if(tag.matches("<!--[^>]*?-->")){
			return "!--";
		}
		
		// 查找标签名的结束位置
		int index2 = tag.indexOf(" ", index1 + 1);
		if (index2 < 0) {
			index2 = tag.indexOf(">", index1 + 1);
		}
		if (index2 < 0) {
			index2 = tag.indexOf("\t", index1 + 1);
		}
		if (index2 < 0) {
			return null;
		}
		
		// 截取标签名
		return tag.substring(index1 + 1, index2);
	}

	//用于代替字符相加减
	private StringBuffer buffer=new StringBuffer();
	
	/**
	 * 把指定的HTML代码中所有标签的标签名和其属性名转换成小写。
	 * 
	 * @param code 指定的HTML代码。
	 * @return 转后的HTML代码。
	 */
	public String toLowerCase(String code) {
		if(code==null || code.trim().equals("")){
			return code;
		}
		
		//判断是否要加载HTML表签名
		if(htmlTagList.size()<=0){
			loadHtmlTag();
		}
		
		//使用buffer保存字符串。
		if(buffer.length()!=0){
			buffer.delete(0, buffer.length());
		}
		buffer.append(code);
		
		//查找开始标签开始“<”
		int tagBeginIndex = buffer.indexOf("<", -1);
		while (tagBeginIndex >= 0 && tagBeginIndex < buffer.length()) {

			char c = buffer.charAt(tagBeginIndex + 1);

			if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '/'
					|| c == '!') {

				int tagEndIndex = buffer.indexOf(">", tagBeginIndex + 1);
				if (tagEndIndex < 0) {
					break;
				}

				// 截取单个标签
				String tag = buffer.substring(tagBeginIndex, tagEndIndex + 1);
				
				// 截取单个标签名
				String tagName=getTagName(tag);
				//判断标签名是否为空，是否为HMTL标签
				if (tagName != null && existTagName(tagName)) {
					//删除截取的标签字符串
					buffer.delete(tagBeginIndex, tagEndIndex + 1);
					
					// 最小化标签的名称和属性
					tag = tagToLowerCase(tag);
					
					//插入最小化后的标签字符串
					buffer.insert(tagBeginIndex, tag);
					
					// 从标签结尾处下一个字符开始查找“<”
					tagBeginIndex = buffer.indexOf("<", tagEndIndex + 1);
				} else {
					
					// 从“<”的下一个字符开始查找“<”
					tagBeginIndex = buffer.indexOf("<", tagBeginIndex + 1);
				}

			} else {
				// 从“<”的下一个字符开始查找“<”
				tagBeginIndex = buffer.indexOf("<", tagBeginIndex + 1);
			}
		}

		return buffer.toString();
	}

	/**
	 * 把指定的HTML代码中指定标签名的标签的标签名和其属性名转换成小写。
	 * 
	 * @param code 指定的HTML代码。
	 * @param tagName 指定的标签名称。
	 * @return 转后的HTML代码。
	 * @exception 抛出 传入的参数“tagName”为空 异常。
	 */
	public String toLowerCase(String code, String tagName) {
		if(code==null || code.trim().equals("")){
			return code;
		}
		
		if(tagName==null){
			throw new NullPointerException("传入的参数“tagName”为空");
		}
		
		//判断是否要加载HTML表签名
		if(htmlTagList.size()<=0){
			loadHtmlTag();
		}
		
		//使用buffer保存字符串。
		if(buffer.length()!=0){
			buffer.delete(0, buffer.length());
		}
		buffer.append(code);
		
		//查找开始标签开始“<”
		int tagBeginIndex = buffer.indexOf("<", -1);
		while (tagBeginIndex >= 0 && tagBeginIndex < buffer.length()) {

			char c = buffer.charAt(tagBeginIndex + 1);
			
			if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '/'
					|| c == '!') {

				int tagEndIndex = buffer.indexOf(">", tagBeginIndex + 1);
				if (tagEndIndex < 0) {
					break;
				}

				// 截取单个标签
				String tag = buffer.substring(tagBeginIndex, tagEndIndex + 1);
				
				// 截取单个标签名
				String tagNameTmp=getTagName(tag);
				//判断标签名是否为空，是否为HMTL标签，否和指定的标签名相等
				if (tagNameTmp != null && existTagName(tagNameTmp) && tagName.equalsIgnoreCase(tagNameTmp)) {

					//删除截取的标签字符串
					buffer.delete(tagBeginIndex, tagEndIndex + 1);
					
					// 最小化标签的名称和属性
					tag = tagToLowerCase(tag);

					//插入最小化后的标签字符串
					buffer.insert(tagBeginIndex, tag);
					
					// 从标签结尾处下一个字符开始查找“<”
					tagBeginIndex = code.indexOf("<", tagEndIndex + 1);
				} else {
					// 从“<”的下一个字符开始查找“<”
					tagBeginIndex = code.indexOf("<", tagBeginIndex + 1);
				}

			} else {
				// 从“<”的下一个字符开始查找“<”
				tagBeginIndex = code.indexOf("<", tagBeginIndex + 1);
			}
		}

		return buffer.toString();
	}

	/**
	 * 把指定的HTML开始标签、结尾标签或单标签的标签名和其属性名转换成小写。
	 * 
	 * @param tag 开始标签、结尾标签或单标签。
	 * @return 转换后的标签。
	 * @exception 抛出 传入的参数“tag”为空 异常。
	 */
	public String tagToLowerCase(String tag) {
		if(tag==null){
			throw new NullPointerException("传入的参数“tag”为空");
		}
		
		// 判断为结尾标签
		if (tag.startsWith("</")) {
			return tag.toLowerCase();
		}
		
		// 判断为开始标签，且没有属性
		if (!tag.contains("=")) {
			return tag.toLowerCase();
		}

		// 获取标签名，并且变成小写
		int index = tag.indexOf(" ", -1);
		if (index < 0) {
			index = tag.indexOf("\t", -1);
		}
		if (index < 0) {
			index = tag.indexOf(">");
		}
		if (index > 0) {
			tag = tag.substring(0, index).toLowerCase() + tag.substring(index);
		} else {
			// 没有标签名
			return tag;
		}

		// 属性值结尾位置
		int attrValueEndIndex = -1;

		while (index < tag.length()) {
			int equalIndex = tag.indexOf("=", index);
			if (equalIndex < 0) {
				break;
			}
			// “=”右边的位置
			int equalLeftIndex = equalIndex - 1;
			// 跳过“=”右边的空格、制表符、全角空格；“=”右边位置要大于上一个属性值的结尾位置
			for (; (tag.charAt(equalLeftIndex) == ' '
					|| tag.charAt(equalLeftIndex) == '\t' || tag
					.charAt(equalLeftIndex) == '　')
					&& equalLeftIndex > attrValueEndIndex; equalLeftIndex--)
				;

			// 保存需要比较的位置
			int compareIndex[] = new int[5];

			// 获取“=”左边的“"”的值
			compareIndex[0] = tag.lastIndexOf("\"", equalLeftIndex);
			if (compareIndex[0] > 0 && compareIndex[0] > attrValueEndIndex) {
				// 判断是否为一个转义的“"”
				if (tag.charAt(compareIndex[0] - 1) == '\\') {
					compareIndex[0] = -1;
				}
			}
			// 获取“=”左边的“'”的值
			compareIndex[1] = tag.lastIndexOf("\'", equalLeftIndex);
			if (compareIndex[1] > 0 && compareIndex[1] > attrValueEndIndex) {
				// 判断是否为一个转义的“'”
				if (tag.charAt(compareIndex[1] - 1) == '\\') {
					compareIndex[1] = -1;
				}
			}
			// 获取“=”左边的属性名前半角空格的值
			compareIndex[2] = tag.lastIndexOf(" ", equalLeftIndex);
			// 获取“=”左边的属性名前制表符的值
			compareIndex[3] = tag.lastIndexOf("\t", equalLeftIndex);
			// 获取“=”左边的属性名前全角空格的值
			compareIndex[4] = tag.lastIndexOf("　", equalLeftIndex);
			// 对这些位置进行升序排序
			Arrays.sort(compareIndex);

			// 取最大的位置，作为属性名的开始位置
			int attrNameBeginIndex = compareIndex[4] + 1;
			if (attrNameBeginIndex > 0
					&& attrNameBeginIndex >= attrValueEndIndex) {
				tag = tag.substring(0, attrNameBeginIndex)
						+ tag.substring(attrNameBeginIndex, equalIndex)
								.toLowerCase() + tag.substring(equalIndex);
			} else {
				break;
			}

			// /////////////////////////////////////////

			// 开始计算“=”的右边，即计算属性值的结束位置
			int equalRightIndex = equalIndex + 1;
			// 跳过“=”右边的半角空格、制表符、全角空格
			for (; (tag.charAt(equalRightIndex) == ' '
					|| tag.charAt(equalRightIndex) == '\t' || tag
					.charAt(equalLeftIndex) == '　')
					&& equalRightIndex < tag.length() - 1; equalRightIndex++)
				;

			// 获取“=”右边属性值的开始字符
			char nextChar = tag.charAt(equalRightIndex++);
			// 判断为“"”时，属性值使用双引号扩住
			if (nextChar == '"') {
				for (; equalRightIndex < tag.length() - 1; equalRightIndex++) {
					if (tag.charAt(equalRightIndex) == '"') {
						// 判断是否为一个转义的“"”
						if (tag.charAt(equalRightIndex - 1) != '\\') {
							break;
						}
					}
				}
			}
			// 判断为“'”时,属性值使用单引号扩住
			else if (nextChar == '\'') {
				for (; equalRightIndex < tag.length() - 1; equalRightIndex++) {
					if (tag.charAt(equalRightIndex) == '\'') {
						// 判断是否为一个转义的“'”
						if (tag.charAt(equalRightIndex - 1) != '\\') {
							break;
						}
					}
				}
			}
			// 判断为空字符，属性值结尾使用空格分开
			else {
				for (; (tag.charAt(equalRightIndex) != ' '
						&& tag.charAt(equalRightIndex) != '\t' && tag
						.charAt(equalLeftIndex) != '　')
						&& equalRightIndex < tag.length() - 1; equalRightIndex++)
					;
			}

			// 保存这次查找的“=”右边属性值结尾位置后一位
			index = equalRightIndex + 1;

			// 保存上一个属性值的结束位置
			attrValueEndIndex = equalRightIndex;
		}

		return tag;
	}
	
	/**
	 * 删除指定网页代码中所有HTML标签。
	 * 
	 * @param code 网页代码。
	 * @return 删除所有HTML标签的网页代码中。
	 */
	public String delHtmlTag(String code) {
		if (code == null || code.trim().equals("")) {
			return code;
		}
		
		this.loadHtmlTag();// 加载HTML标签
		
		if(buffer.length()!=0){
			buffer.delete(0, buffer.length());
		}
		buffer.append(code);
		
		
		int index1 = buffer.indexOf("<", -1);
		while (index1 >= 0 && index1 < buffer.length()) {

			int index2 = buffer.indexOf(">", index1 + 1);
			if (index2 < 0) {
				break;
			}

			index1 = buffer.lastIndexOf("<", index2);
			// 截取标签
			String tag = buffer.substring(index1, index2 + 1);
			// 截取标签名
			String tagName = getTagName(tag);
			// 判断标签名是否为HTML标签
			if (tagName != null && existTagName(tagName)) {
				
				buffer.delete(index1, index2 + 1);
				index1 = buffer.indexOf("<", index1);
			} 
			else {
				index1 = buffer.indexOf("<", index2);
			}
		}

		return buffer.toString();
	}

	
	
	/**
	 * 删除指定网页代码中所有以开始标记和结尾标记之间的子字符串，可能删除标记本身。
	 * 
	 * @param code 网页代码。
	 * @param beginTag 字符串开始标记。
	 * @param endTag 字符串结尾标记。
	 * @param isSearchBack 当查找到结尾标记时，是否从结尾标记的开始位置再向前查找开始标记的开始位置。
	 * @return 删除指定网页代码中所有以开始标记和结尾标记之间的子字符串。
	 */
	public String delBetweenTag(String code, String beginTag, String endTag, boolean isSearchBack) {
		if (code == null || beginTag == null || endTag == null) {
			return code;
		}
		
		if(code.equals("") || beginTag.equals("") || endTag.equals("")){
			return code;
		}
		
		if(buffer.length()!=0){
			buffer.delete(0, buffer.length());
		}
		buffer.append(code);
		
		while (true) {
			// 查找开始标记
			int index1 = buffer.indexOf(beginTag);
			if (index1 < 0) {
				break;
			}
			
			// 从开始标记的结束位置查找结尾标记
			int index2 = buffer.indexOf(endTag, index1 + beginTag.length());
			if (index2 < 0) {
				break;
			}
			
			// 判断是否向后查找
			if (isSearchBack) {
				index1 = buffer.lastIndexOf(beginTag, index2);
			}
			
			// 删除开始和结尾标记之间的字符串
			buffer.delete(index1, index2+endTag.length());
		}
		
		return buffer.toString();
	}
	
	/**
	 * 获取指定网页代码中以指定开始标记和与之配对结尾标记之间的子字符串，可能包含标记本身。
	 * 
	 * @param code 网页代码。
	 * @param beginTag 用于字符串开始标记的开始标签代码。
	 * @param endTag 用于字符串结尾标记的结尾标签代码。
	 * @param isContainSelf 截取时是否要包含开始、结尾标记本身，包含为true，不含为false。
	 * @return 获取指定开始标签和与之配对结尾标签之间的代码。
	 */
	public static String getPairTag(String code,String beginTag, String endTag,boolean isContainSelf) {
		String result=null;
		
		if (code==null || beginTag == null || endTag == null) {
			return result;
		}
		
		//保存开始结尾前的位置，用于它们配对。
		Stack<Integer> stack=new Stack<Integer>();
		
		// 获取标签名
		String tagName = getTagName(beginTag);
		if (tagName == null) {
			return result;
		}
		
		// 组合开始标签
		String partBeginTag = "<" + tagName;
		
		//开始标记的开始、结尾位置
		int bBeginIndex = -1;
		int eBeginIndex = -1;
		
		
		//结尾标记的开始、结尾位置
		int bEndIndex = -1;
		int eEndIndex = -1;
		
		//判断是否是首次查找开始标记，默认是“否”
		boolean isBeginFirst = false;
		boolean isEndFirst = false;
		
		
		//用于记录每个开始标记的开始位置
		int bPartBeginIndex =-1;
		
		while (true) {
			//下面的循环是用于查找开始部分标记的开始位置。
			
			// 查找开始标签的开始位置
			int bBeginIndexTmp = -1;
			int eBeginIndexTmp = -1;
			
			//用于循环的标量，保存每个标记的开始位置。
			int bPartBeginIndexTmp=bPartBeginIndex;
			
			do{
				bPartBeginIndexTmp=Utilities.ignoreCaseIndexOf(code,partBeginTag,bPartBeginIndexTmp);
				if (bPartBeginIndexTmp < 0) {
					break;
				}
				
				int ePartBeginIndexTmp=code.indexOf(">",bPartBeginIndexTmp);
				if (ePartBeginIndexTmp < 0) {
					break;
				}
				
//				String tag=code.substring(bPartBeginIndexTmp,ePartBeginIndexTmp+1);
				String tagNameTmp=getTagName(code.substring(bPartBeginIndexTmp,ePartBeginIndexTmp+1));
				if(tagName!=null && tagName.equalsIgnoreCase(tagNameTmp)){
					if(isBeginFirst==false){
						isBeginFirst=true;
						bBeginIndex=bPartBeginIndexTmp;
						eBeginIndex=ePartBeginIndexTmp;
					}
					
					bBeginIndexTmp=bPartBeginIndexTmp;
					eBeginIndexTmp=ePartBeginIndexTmp;
					
					break;
				}
				
				bPartBeginIndexTmp=ePartBeginIndexTmp+1;
			
			} while(true);
			
			
			//下面是用于查找结尾标记的开始位置。
			
			
			//查找结尾标记的开始位置。
			int bEndIndexTmp = -1;
			if(isEndFirst==false){
				isEndFirst = true;
				bEndIndexTmp = Utilities.ignoreCaseIndexOf(code,endTag, bPartBeginIndexTmp);
			}
			else{
				bEndIndexTmp = Utilities.ignoreCaseIndexOf(code,endTag, bPartBeginIndex);
			}
			
			bEndIndex=bEndIndexTmp;//结尾标记开始位置
			eEndIndex=bEndIndexTmp+endTag.length();//结尾标记的结尾位置
			
			// 当开始和结尾标记都存在时
			if (bBeginIndexTmp >= 0 && bEndIndexTmp > 0) {
				
				// 开始标签的开始位置小于结尾标签的开始位置
				if (bBeginIndexTmp < bEndIndexTmp) {
					
					stack.push(bPartBeginIndex);// 进栈
					
					bPartBeginIndex = eBeginIndexTmp+1;
				} 
				else {
					if(stack.isEmpty()){
						break;
					}

					stack.pop();// 出栈
					bPartBeginIndex = eEndIndex;
				}
			}
			// 当不存在开始标签，但存在结尾标签时 
			else if (bBeginIndexTmp < 0 && bEndIndexTmp > 0) {
				if(stack.isEmpty()){
					break;
				}
				
				stack.pop();// 出栈
				bPartBeginIndex = eEndIndex;
			} 
			//当开始标签存在结尾标签不存在时，或者两者都不存在时
			else {
				break;
			}
			
			if(stack.isEmpty()){
				break;
			}
		}
		
		if(bBeginIndex >=0 && bEndIndex >= bBeginIndex){
			if (isContainSelf) {
				result = code.substring(bBeginIndex, eEndIndex);
			} 
			else {
				result = code.substring(eBeginIndex+1, bEndIndex);
			}
		}
		
		return result;
	}
	
	/**
	 * 删除指定网页代码中以指定开始标记和与之配对结尾标记作之间的子字符串，并且删除标记本身。
	 * 
	 * @param code 网页代码。
	 * @param beginTag 用于字符串开始标记的开始标签代码。
	 * @param endTag 用于字符串结尾标记的结尾标签代码。
	 * @return 删除开始标记和结尾标记直间的子字符串后的代码。
	 */
	public static String delPairTag(String code, String beginTag,String endTag) {
		String str = getPairTag(code, beginTag,endTag,true);
		
		if (str == null || str.equals("")) {
			return code;
		}
		
		return code.replace(str, "");
	}

	/**
	 * 替换指定网页代码中的常见HTML实体。
	 * 
	 * @param code 需替换的网页代码。
	 * @return 替换后的网页代码。
	 */
	public static String replaceEntity(String code) {
		if (code != null) {
			// 替换HTML实体名称
			code = code.replace("&lt;", "<");
			code = code.replace("&gt;", ">");
			code = code.replace("&amp;", "&");
			code = code.replace("&nbsp;", " ");
			code = code.replace("&apos;", "\'");
			code = code.replace("&quot;", "\"");
			code = code.replace("&middot;", "·");

			// 替换HTML实体编号
			code = code.replace("&#60;", "<");
			code = code.replace("&#62;", ">");
			code = code.replace("&#38;", "&");
			code = code.replace("&#35;", "#");
			code = code.replace("&#160", " ");
			code = code.replace("&#39;", "\'");
			code = code.replace("&#34;", "\"");
			code = code.replace("&#183;", "·");
		}

		return code;
	}

	
	public static void main(String[] args) throws Exception{
		
		
//		String s="</tr><tr><tb><tr></tr></tb></tr><tr></tr>";
//		String r=HtmlUtil.getPairTag(s, "<tr>", "</tr>", true);
//		System.out.println(r);
//		
//		HtmlUtil util=new HtmlUtil(s);
//		List<String> list=util.getPairTagList("tr");
//		for (String sr : list) {
//			System.out.println(sr);
//		}
		
		
		
//		HtmlPage page=new HtmlPage("http://stock.hexun.com/real/index.html");
//		HtmlUtil util=new HtmlUtil(page.getHtmlCode());
//		
//		String s=util.firstStringMatche("<div class=\"temp01\">", "<div class=\"listdh\">");
//		util.setHtmlCode(s);
		
		
//		HtmlUtil util=new HtmlUtil("<li>1<li>2<li>3<li>4</ul>");
//		List<String> list=util.getRegexpMatcheList("<li>", "<li>|</ul>");
//		HtmlUtil util=new HtmlUtil("a1ab2b");
//		List<String> list=util.getRegexpMatcheList("<li>", "(</a>)[^<>]*?(<li>|</ul>)");
//		System.out.println(list.size());
//		
//		for (String link : list) {
//			System.out.println(Utilities.replaceNewline(link));
//		}
		
		
//		String tag="<!doctype>";
//		System.out.println(getTagName(tag));
		
		
//		String t="<a>aa</a><a>bb</a><a>cc</a>";
//		HtmlUtil hu=new HtmlUtil(t);
//		List<String> list=hu.getMatcheList("<a>..</a>");
//		for (String s : list) {
//			System.out.println(s);
//			System.out.println(hu.firstPairTag("a"));
//		}
		
		
//		String s="<a>bb</a>";
//		HtmlUtil hu=new HtmlUtil(s);
//		System.out.println(hu.delHtmlTag(s));
		
		
//		String s="<dl>\r\n\t\t\t\t<dt><a class=ulink href='/html/qihuo/index.html'>期货</a><a class=ulink href='/html/qihuo/index.html'>期货</a><a href=\"/html/qihuo/201003/03-35230.html\">中期协:前两月全国期市成交期货合约3.8亿手</a>&nbsp;发表于：<span>2010-03-03</span></dt>\r\n\t\t\t\t\r\n\t\t\t</dl>";
//		HtmlUtil hu=new HtmlUtil(s);
//		List<String>list=hu.getPairTagList("a");
//		for (String tag : list) {
//			System.out.println(tag);
//		}
//		
//		List<String>list1=hu.getTwainTagList("a");
//		for (String tag : list1) {
//			System.out.println(tag);
//		}
//		
//		hu.setContainSelf(true);
//		List<String>list2=hu.getStringMatcheList("<a", "</a>");
//		for (String tag : list2) {
//			System.out.println(tag);
//		}
//		
//		hu.setContainSelf(true);
//		List<String>list3=hu.getRegexpMatcheList("<a", "</a>");
//		for (String tag : list3) {
//			System.out.println(tag);
//		}
		
//		String link="http://www.21cbh.com/html/economy/1.html";
//		HtmlPage hm=new HtmlPage(link,"utf-8");
//		HtmlUtil hu=new HtmlUtil();
//		hu.setHtmlCode(hm.getHtmlCode());
//		List< String > teg=hu.getPairTagList("a");
//		for (String t : teg) {
//			String bt=HtmlUtil.getSingleTag(t, "a", "href");
//			String href=HtmlUtil.getAttributeValue(bt, "href");
//			
//			System.out.println(hm.repairLink(href));
////			System.out.println(t);
//		}
		
		
//		String tag="<a href=2010-03/16/content_1072848.htm >";
//		String href=HtmlUtil.getAttributeValue(tag, "href");
//		System.out.println(href);
		
		
//		String s="<BODY><A HREF    =   \"HTTP://WWW.b\"aidu.COM\"  title='aaaaa'>BAIDU</A><BODY>";
//		System.out.println(toLowerCase(s));
//		System.out.println(toLowerCase(s,"a"));
		
//		String s="</script><asp><a><";
//		
//		String r=delHtmlTag(s);
//		System.out.println(r);
//		
//		r=delPairTag(s, "<", ">", true);
//		System.out.println(r);
		
//		StringBuffer sb=new StringBuffer();
//		sb.append("0123456789123456789");
//		sb.delete(0, sb.length());
//		System.out.println(sb.length());
//		System.out.println(sb.toString());

//		String s="11<a><a><asp>夏朋</asp></a></a>";
//		HtmlUtilTest h=new HtmlUtilTest();
//		h.setHtmlCode(s);
//		h.setContainSelf(true);
//		System.out.println(h.firstTwainTag("a"));
//		System.out.println(h.nextTwainTag());
		
//		String r=getTwainTag(s, "1","1",true);
//		System.out.println(r);
//		System.out.println(delTwainTag(s, "<a>","</a>"));
		
//		HtmlUtilTest h=new HtmlUtilTest();
		
//		h.setHtmlCode("11");
//		h.setContainSelf(true);
//		System.out.println(h.firstRegexpMatche("1", "1"));
//		System.out.println(h.nextRegexpMatche());
//		System.out.println(h.nextRegexpMatche());
//		System.out.println(h.nextRegexpMatche());
//		System.out.println(h.nextRegexpMatche());
		
//		h.setHtmlCode("11");
//		h.setContainSelf(true);
//		System.out.println(h.firstStringMatche("1", "1"));
//		System.out.println(h.nextStringMatche());
//		System.out.println(h.nextStringMatche());
//		System.out.println(h.nextStringMatche());
//		System.out.println(h.nextStringMatche());
		
		
		
//		h.setHtmlCode("<b><a>11</a ></b ><b><a>12</a ></b ><b><a>13</a ></b ><b><a>14</a ></b ><b><a>15</a ></b ><b><a>16</a ></b >");
//		System.out.println(h.firstStringMatcheTag("a"));
//		System.out.println(h.nextStringMatcheTag());
//		System.out.println(h.nextStringMatcheTag());
//		System.out.println(h.nextStringMatcheTag());
//		System.out.println(h.nextStringMatcheTag());
//		System.out.println(h.nextStringMatcheTag());
//		System.out.println(h.nextStringMatcheTag());
	}
}
