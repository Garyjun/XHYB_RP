package com.brainsoon.common.util.fltx.webpage.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 网页（嵌套）表格解析对象。
 */
public class TableUtil {
	//判断是否第一取下一行
	private boolean isFirst=false;
	
	private HtmlUtil htmlUtil=new HtmlUtil();
	
	/**
	 * 初始化一个新的表格解析对象。
	 */
	public TableUtil() {
		super();
	}
	
	/**
	 * 使用表格代码初始化一个新的表格解析对象。
	 * @param tabCode 表格代码。
	 */
	public TableUtil(String tabCode){
		this.setTabCode(tabCode);
	}
	
	/**
	 * 使用网页链接、网页编码、开始标记、结尾标记初始化一个新表格对象。
	 *  
	 * @param link 需要采集表格代码。
	 * @param encoding 网页编码。
	 * @param begin 字符串开始标记。
	 * @param end 字符串结尾标记。
	 */
	public TableUtil(String link, String encoding, String begin, String end)
			throws MalformedURLException, UnsupportedEncodingException,
			IOException, NullPointerException {
		HtmlPage p = new HtmlPage(link, encoding);
		htmlUtil.setHtmlCode(p.getHtmlCode());
		this.setTabCode(htmlUtil.firstStringMatche(begin, end));
	}
	
	public HtmlUtil getHtmlUtil() {
		return htmlUtil;
	}

	public void setHtmlUtil(HtmlUtil htmlUtil) {
		this.htmlUtil = htmlUtil;
	}

	/**
	 * 设置表格代码；如果传入的代码为空，则抛出控制异常。
	 * @param tabCode 表格代码。
	 * @exception 抛出 传入的参数“tabCode”为空 异常。
	 */
	public void setTabCode(String tabCode){
		if(tabCode==null){
			throw new NullPointerException("传入的参数“tabCode”为空");
		}
		
		//不区分大小写替换th的表格
		if(Utilities.ignoreCaseIndexOf(tabCode,"<th")>0){
			tabCode=tabCode.replaceAll("(?i)<th", "<td");
		}
		if(Utilities.ignoreCaseIndexOf(tabCode,"</th")>0){
			tabCode=tabCode.replaceAll("(?i)</th", "</td");
		}
		
		htmlUtil.setHtmlCode(Utilities.replaceNewline(tabCode));
	}
		
	/**
	 * 获取表格第一行列标签代码代码。
	 * @return 表格第一行列标签代码代码。
	 */
	public String[] getFirstRow(){
		return getFirstRow((int[])null);
	}
	
	/**
	 * 获取表格第一行中指定列号的列标签代码代码。
	 * @param cols 指定的列号数组。
	 * @return 表格第一行中指定列号的列标签代码代码。
	 */
	public String[] getFirstRow(int cols[]){
		this.isFirst=false;
		return getNextRow(cols);
	}
	
	/**
	 * 获取表格下一行中指定列号的列标签代码代码。
	 * @param cols 指定的列号数组。
	 * @return 表格下一行中指定列号的列标签代码代码。
	 */
	public String[] getNextRow(int cols[]){
		String trCode=null;
		
		if(isFirst==false){
			isFirst=true;
			trCode=htmlUtil.firstPairTag("tr");
		}
		else{
			trCode=htmlUtil.nextPairTag();
		}
		
		if(trCode==null){
			return null;
		}
		htmlUtil.setHtmlCode(trCode);

		//保存一行中的列代码
		String tds[]=null;
		//获取一个表格的代码
		List<String> list=htmlUtil.getPairTagList("td");
		
		if(list!=null){
			//当列号不为
			if (cols != null && cols.length >= list.size()) {
				tds=new String[list.size()];
			}
			else if(cols != null && cols.length < list.size()){
				tds=new String[cols.length];
			}
			else{
				tds=new String[list.size()];
			}
			
			for (int i = 0; i < tds.length; i++) {
				tds[i]=list.get(i);
			}
		}
		
		return tds;
	}

	
	/**
	 * 获取表格每行中所有列标签的代码。
	 * 
	 * @return 成功返回每行中列标签代码组成数组，代码中包含要提取的数据，失败返回null。
	 */
	public String[][] getTabCell(){
		List<String> trList=htmlUtil.getPairTagList("tr");
		if(trList==null){
			return null;
		}
		
		HtmlUtil util=new HtmlUtil();
		String[][] tds=new String[trList.size()][];
		
		for (int i = 0; i < tds.length; i++) {
			String trStr = trList.get(i);
			util.setHtmlCode(trStr);
			
			List<String> tdList=util.getPairTagList("td");
			if(tdList!=null){
				tds[i]=new String[tdList.size()];
				for (int j = 0; j < tds[i].length; j++) {
					tds[i][j]=tdList.get(j);
				}
			}
		}
		
		return tds;
	}
	
	/**
	 * 获取指定行号所有表格列标签代码，当传入的行号大于采集到表格行数时，则被忽略改传入的行号。
	 * 
	 * @return 成功返回指定行号(小于等于采集到表格行数)的所有列标签代码，代码中包含要提取的数据，失败返回null。
	 * 
	 * @exception 抛出 传入的参数“rowIds”为空 异常。
	 */
	public String[][] getRowTabCell(int rowIds[]){
		if (rowIds == null) {
			throw new NullPointerException("传入的参数“rowIds”为空");
		}

		String[][] selectData = null;
		
		String[][] tmpData = getTabCell();
		if (tmpData != null) {

			selectData = new String[rowIds.length][];

			for (int i = 0; i < rowIds.length; i++) {
				// 判断行号的值是否大于表格行数，或小于0。
				if (rowIds[i] > tmpData.length - 1 || rowIds[i] < 0) {
					continue;
				}

				selectData[i] = tmpData[rowIds[i]];
			}
		}

		return selectData;
	}
	
	/**
	 * 获取所有行中指定列号的表格列标签代码，当传入列号大于采集到表格列数时，则被忽略改传入的列号。
	 * 
	 * @return 成功返回指定列号(小于等于采集到表格列数)的列标签的代码，代码中包含要提取的数据，失败返回null。
	 * 
	 * @exception 抛出 传入的参数“colIds”为空 异常。
	 */
	public String[][] getColTabCell(int colIds[]){
		if (colIds == null) {
			throw new NullPointerException("传入的参数“colIds”为空");
		}

		String[][] selectData = null;

		String[][] tmpData = getTabCell();
		if (tmpData != null) {

			selectData = new String[tmpData.length][colIds.length];
			
			for (int i = 0; i < selectData.length; i++) {
				for (int j = 0; j < colIds.length; j++) {
					// 判断列号的值是否大于表格最大列数，或小于0。
					if (colIds[j] > tmpData[i].length - 1 || colIds[j] < 0) {
						continue;
					}
					
					selectData[i][j] = tmpData[i][colIds[j]];
				}
			}
		}

		return selectData;
	}
	
	/**
	 * 获取指定行列坐标号的表格列代码。注：返回的结果，每一行中，列数可能不一样。
	 * @param rowCols 指定的行列坐标号。
	 * @return 表格列代码
	 * 
	 * @exception 抛出 传入的参数“rowCols”为空 异常
	 */
	public String[][] getSelectTabCell(int rowCols[][]){
		if (rowCols == null) {
			throw new NullPointerException("传入的参数“rowCols”为空");
		}
		
		String selectData[][] = null;

		String[][] tmpData = getTabCell();
		if (tmpData != null) {

			List<List<String>> rowColList=new ArrayList<List<String>>();
			
			Map<Integer,List<Integer>> rowColMap=array2Map(rowCols);
			if(rowColMap!=null && !rowColMap.isEmpty()){
				
				Iterator<Integer> iter=rowColMap.keySet().iterator();
				while(iter.hasNext()) {

					int rowid=iter.next();
					//当行号大于表格行数或当前行中列号大于表格行中的列号则跳过。
					if(rowid <= tmpData.length-1 || rowid >= 0){

						List<String> rowList=new ArrayList<String>();
						
						List<Integer> rowIdList=rowColMap.get(rowid);
						for (Integer colid : rowIdList) {
	
							if(colid <= tmpData[rowid].length-1 || colid >= 0){
								rowList.add(tmpData[rowid][colid]);
							}						
						}
						
						rowColList.add(rowList);
					}
				}
				
				selectData=new String[rowColList.size()][];
				for (int i = 0; i < rowColList.size(); i++) {
					
					List<String> rowList=rowColList.get(i);

					String td[]=new String[rowList.size()];
					
					for (int j=0;j<rowList.size();j++) {
						
						td[j]=rowList.get(j);
					}
					
					selectData[i]=td;
				}
			}
		}

		return selectData;
	}
	
	/** 过滤相同的行号 */
	private Map<Integer,List<Integer>> array2Map(int rowCols[][]){
		Map<Integer,List<Integer>> rowColMap=new LinkedHashMap<Integer, List<Integer>>();
		if(rowCols!=null){
		
			// 过滤相同的行号
			for (int i = 0; i < rowCols.length; i++) {
				
				List<Integer> colList=new ArrayList<Integer>();
				// 判断行号是否已经存在
				if(!rowColMap.containsKey(rowCols[i][0])){
					// 不存在则加入进去
					rowColMap.put(rowCols[i][0],colList);
				}
			}
			
			// 把相同的列号放在一行中
			for (int i = 0; i < rowCols.length; i++) {

				List<Integer> colList=rowColMap.get(rowCols[i][0]);
				
				if(!colList.contains(rowCols[i][1])){
					colList.add(rowCols[i][1]);
				}
				
				rowColMap.put(rowCols[i][0],colList);
			}
		}
		
		return rowColMap;
	}
	
	/**
	 * 增加指定标签名。
	 * @param tagName 标签名。
	 * @return 是否增加成功，成功为true，失败为false。
	 */
	public boolean addTagName(String tagName){
		return htmlUtil.addTagName(tagName);
	}
	
	/**
	 * 删除指定标签名。
	 * @param tagName 标签名。
	 * @return 是否删除成功，成功为true，失败为false。
	 */
	public boolean delTagName(String tagName){
		return htmlUtil.delTagName(tagName);
	}
	/**
	 * 是否存在指定的标签名。 
	 * @param tagName 标签名。
	 * @return 存在返回true，不在返回false。
	 */
	public boolean existTagName(String tagName){
		return htmlUtil.existTagName(tagName);
	}
		
	/**
	 * 获取一个列标签代码中的数据。
	 * 
	 * @param tdCode 一个列标签代码
	 * @return 列标签代码中的数据。
	 */
	public String getData(String tdCode) {
		if (tdCode == null || tdCode.equals("")) {
			return null;
		}

		tdCode = tdCode.replace("&nbsp;", " ");
		tdCode = tdCode.replace("&quot;", "\"");
		tdCode = tdCode.replace("&apos;", "\'");

		// 删除HTML标签
		tdCode = htmlUtil.delHtmlTag(tdCode);

		tdCode = tdCode.replace("&lt;", "<");
		tdCode = tdCode.replace("&gt;", ">");

		return tdCode.trim();
	}
	
	public static void main(String args[]) throws Exception{
		try {
			String link="http://quote.eastmoney.com/RankList.aspx?StockType=10&SortType=1&RuleType=-1&x=32&y=9";
			String encode="gb2312";
			HtmlPage page=new HtmlPage(link,encode);
			HtmlUtil htmlUtil=new HtmlUtil(page.getHtmlCode());
			String s="<table width=\"944\" border=\"0\" cellpadding=\"2\" cellspacing=\"0\" align=\"center\">";
			String e="</table>";
			
			TableUtil tableUtil=new TableUtil(htmlUtil.firstStringMatche(s, e));
			tableUtil.addTagName("nobr");

			String [][]str=tableUtil.getSelectTabCell(new int[][]{{1,1},{2,1},{3,1}});
			for (int i = 0; str!=null && i <str.length; i++) {
				for (int j = 0; str[i]!=null && j < str[i].length; j++) {
					str[i][j]=tableUtil.getData(str[i][j]);
					
					System.out.print(str[i][j]+"\t");
				}
				
				System.out.println();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
//		try {
//			String link="http://quote.eastmoney.com/RankList.aspx?StockType=10&SortType=1&RuleType=-1&x=32&y=9";
//			String encode="gb2312";
//			HtmlPage page=new HtmlPage(link,encode);
//			HtmlUtil htmlUtil=new HtmlUtil(page.getHtmlCode());
//			String s="<table width=\"944\" border=\"0\" cellpadding=\"2\" cellspacing=\"0\" align=\"center\">";
//			String e="</table>";
//			
//			TableUtil tableUtil=new TableUtil(htmlUtil.firstStringMatche(s, e));
//			tableUtil.addTagName("nobr");
//
//			String [][]str=tableUtil.getColTabCell(new int[]{0,1,3,4,5,6,7,8,9,10,11,12,13,14});
//			for (int i = 0; str!=null && i <str.length; i++) {
//				for (int j = 0; str[i]!=null && j < str[i].length; j++) {
//					str[i][j]=tableUtil.getData(str[i][j]);
//					System.out.print(str[i][j]+"\t");
//				}
//				System.out.println();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		
//		String t="A<TABLE><TR><TD><TABLE><TR><TD>hualong</TD></TR></TABLE></TD><TD><TABLE><TR><TD>xiapeng</TD></TR></TABLE></TD></TR><TR><TD><TABLE><TR><TD>hualong</TD></TR></TABLE></TD><TD><TABLE><TR><TD>xiapeng</TD></TR></TABLE></TD></TR></TABLE>";
//		TableUtil tu=new TableUtil(t);
//		String td[][]=tu.getTabCell();
//		for (int i = 0; i < td.length; i++) {
//			for (int j = 0; j < td[i].length; j++) {
//				td[i][j]=tu.getData(td[i][j]);
//				System.out.println(td[i][j]);
//			}
//		}
		
//		String t="A<TABLE><TR><TD><TABLE><TR><TD>hualong</TD></TR></TABLE></TD><TD><TABLE><TR><TD>xiapeng</TD></TR></TABLE></TD></TR><TR><TD><TABLE><TR><TD>hualong</TD></TR></TABLE></TD><TD><TABLE><TR><TD>xiapeng</TD></TR></TABLE></TD></TR></TABLE>";
//		TableUtil tu=new TableUtil(t);
//		String[] tab=tu.getFirstRow();
//		for (int i = 0; tab!=null && i < tab.length; i++) {
//			System.out.println(tab[i]);
//		}
	}
}
