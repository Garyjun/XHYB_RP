package com.brainsoon.rp.support;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * <dl>
 * <dt>Utils.java</dt>
 * <dd>Function: {!!一句话描述功能}</dd>
 * <dd>Description:{详细描述该类的功能}</dd>
 * <dd>Copyright: Copyright (C) 2010</dd>
 * <dd>Company: 北京博云易迅技术有限公司</dd>
 * <dd>CreateUser: xujie</dd>
 * <dd>CreateDate: 2016年3月10日</dd>
 * </dl>
 */
public class Utils {
	private static Utils utils = null;
	private Document configDoc = null;
	private Connection conn = null;
	private String ServiceRootURL = "";
	private String ShowJnlIssueContentURL = "showJnlIssue?id=";
	private String ShowJnlFigureContentURL="showJnlFigure?id=";

	private Utils(){}
	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public String getServiceRootURL() {
		return ServiceRootURL;
	}

	public void setServiceRootURL(String serviceRootURL) {
		ServiceRootURL = serviceRootURL;
	}

	public String getShowJnlIssueContentURL() {
		return ShowJnlIssueContentURL;
	}
	
	public String getShowJnlFigureContentURL() {
		return ShowJnlFigureContentURL;
	}

	public void setShowJnlIssueContentURL(String showJnlIssueContentURL) {
		ShowJnlIssueContentURL = showJnlIssueContentURL;
	}

	private static Utils getInstance() {
		if (utils == null) {
			utils = new Utils();
			try {
				SAXReader sr = new SAXReader();
				Document doc = sr.read(Utils.class.getResource("/").getPath() +"config.xml");
				Element uc = (Element) doc.selectSingleNode("/config/SQLDBConnections/UsingConnection");
				Element c = (Element) doc.selectSingleNode("/config/SQLDBConnections/Connection[@type='" + uc.attributeValue("type") + "' and @name='" + uc.attributeValue("name") + "']");
				Class.forName(c.attributeValue("driver"));
				utils.setConn(DriverManager.getConnection(c.attributeValue("url"), c.attributeValue("user"), c.attributeValue("pwd")));

				utils.setShowJnlIssueContentURL(doc.selectSingleNode("/config/URL/ShowJnlIssueContent").getText());
				utils.setServiceRootURL(doc.selectSingleNode("/config/URL/ServiceRoot").getText());

			} catch (DocumentException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return utils;
	}

	public static String FileMD5(File f) {
		return f.getName() + "[MD5]";
	}

	public static String TextFileContent(InputStream in) {
		String str = "";
		byte b[] = new byte[1024];
		int len = 0;
		int temp = 0; // 所有读取的内容都使用temp接收
		try {
			while ((temp = in.read()) != -1) { // 当没有读取完时，继续读取
				b[len] = (byte) temp;
				len++;
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(new String(b));
		return str;
	}

	public static String TextFileContent(File f) {
		String str = "";
		try {
			InputStreamReader read = new InputStreamReader(new FileInputStream(f), "UTF8");// 考虑到编码格式
			BufferedReader bufferedReader = new BufferedReader(read);
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				str = str + "\n" + line;
			}
			read.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str.trim();
	}

	public static String H2String(String str) {
		return str.replace("'", "''");
	}

	// 获取指定路径文件的字节大小
	public static Long getFileSize(String path) {
		return new File(path).length();
	}

	// 获取指定路径文件的MD5摘要
	public static String getFileMD5(String sTOCXMLPath) {
		// TODO 获取指定路径文件的MD5摘要
		return "[MD5]" + sTOCXMLPath;
	}

	// 格式化文章内容文本，去除首尾空格、回车，将连续回车转换为一个回车
	public static String FormatArtContentText(String cnt) {
		if (cnt == null)
			return "";

		String newCnt = cnt.trim();

		StringBuffer appender = new StringBuffer("");

		if (newCnt != null && newCnt.trim() != "") {
			appender = new StringBuffer(newCnt.length());

			for (int i = 0; i < newCnt.length(); i++) {
				char ch = newCnt.charAt(i);
				if ((ch == 0x9) || (ch == 0xA) || (ch == 0xD) || ((ch >= 0x20) && (ch <= 0xD7FF)) || ((ch >= 0xE000) && (ch <= 0xFFFD)) || ((ch >= 0x10000) && (ch <= 0x10FFFF)))
					appender.append(ch);
			}

			if (newCnt.length() > 0) {
				while (newCnt.subSequence(0, 1).equals("　"))
					newCnt = newCnt.substring(1);
			}
			while (newCnt.indexOf("\n\n") > 0)
				newCnt = newCnt.replace("\n\n", "\n");

		}
		return newCnt;
	}

	public static boolean isNumeric(String str) {
		if (str == null || str.equals(""))
			return false;
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	public static String ReplaceMonthNumber(String str) {
		str = str.replace("一", "1");
		str = str.replace("二", "2");
		str = str.replace("三", "3");
		str = str.replace("四", "4");
		str = str.replace("五", "5");
		str = str.replace("六", "6");
		str = str.replace("七", "7");
		str = str.replace("八", "8");
		str = str.replace("九", "9");
		str = str.replace("十", "10");
		str = str.replace("十一", "11");
		str = str.replace("十二", "12");

		return str;
	}

	public static String ReplaceNumeric(String str) {
		str = str.replace("一", "1");
		str = str.replace("二", "2");
		str = str.replace("三", "3");
		str = str.replace("四", "4");
		str = str.replace("五", "5");
		str = str.replace("六", "6");
		str = str.replace("七", "7");
		str = str.replace("八", "8");
		str = str.replace("九", "9");
		str = str.replace("〇", "0");
		str = str.replace("○", "0");
		str = str.replace("零", "0");

		return str;
	}

	public static String[] getYearMonthFromArtTitle_Batch1(String str) {
		String y = "";
		String m = "";
		String[] ym = { "0", "0" };
		str = Utils.ReplaceNumeric(str);
		while (!Utils.isNumeric(str.substring(0, 1)))
			str = str.substring(1);
		while (Utils.isNumeric(str.substring(0, 1))) {
			y = y + str.substring(0, 1);
			str = str.substring(1);
		}
		ym[0] = y;
		while (!Utils.isNumeric(str.substring(0, 1)))
			str = str.substring(1);
		while (Utils.isNumeric(str.substring(0, 1))) {
			m = m + str.substring(0, 1);
			str = str.substring(1);
		}
		ym[1] = m;
		return ym;
	}

	public static void parseParagraph(String ArtContent) {
		Document SRArtXMLContent = null;

		String XMLStr = "<Content/>";
		try {
			SRArtXMLContent = DocumentHelper.parseText(XMLStr);
		} catch (DocumentException e) {
			System.out.println(XMLStr);
			e.printStackTrace();
		}

		String para[] = ArtContent.split("\n");

		for (int i = 0; i < para.length; i++) {

			Element paraNode = ((Element) SRArtXMLContent.selectSingleNode("/Content")).addElement("Paragraph");
			paraNode.addAttribute("seq", String.valueOf(i));
			paraNode.setText(Utils.FormatArtContentText(para[i]));
			// ArtXMLContent.add(paraNode);

		}

		System.out.println(SRArtXMLContent.asXML());
	}

	/*
	 * 从文章内容（Content）中解析大事辑览条目内容，创建大事辑览条目标准资源， 根据《新华月报》数据，当前暂不提取大事辑览的插图信息
	 */

	private static void parseEventEntry(String art) {
		String ArtContent = art;
		String paragraph[] = ArtContent.split("\n");

		SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd");

		ResourceID parAID = new ResourceID(999l);
		String parTitle = "国内外大事记（一九八六年八月份）";
		Integer y = Integer.valueOf(Utils.getYearMonthFromArtTitle_Batch1(parTitle)[0]);
		Integer m = Integer.valueOf(Utils.getYearMonthFromArtTitle_Batch1(parTitle)[1]) - 1;
		String parAuthor = "作者";
		Integer parPageNum = 107;
		Integer parPDFPageNum = 110;
		String parCategory = "";
		String parSource = "文章来源";
		String parDateDesc = "";
		Date parEventDate = new Date();
		String parEvent = "";
		ResourceID parFigureID = new ResourceID(0l);
		Long IDCounter = 0l;
		boolean international = false;

		for (int i = 0; i < paragraph.length; i++) {

			String p = Utils.FormatArtContentText(paragraph[i]);
			if (p.trim().equals("国际"))
				international = true;
			else if (Utils.isNumeric(p.substring(0, 1)) && p.length() > 10 && p.substring(0, 10).indexOf("日") > 0) {// 以数字开头且前10个字符内包含“日”字的，认为是新的大事记日期条目开始
				if (parEvent.length() > 0) {
					IDCounter++;
					System.out.println("ID:" + IDCounter + ";ArtID:" + parAID.getID() + ";Title" + parTitle + ":;Author:" + parAuthor + ";PageNum:" + parPageNum + ";PDFPageNum:" + parPDFPageNum
							+ ";Category:" + parCategory + ";Source:" + parSource + ";DateDesc:" + parDateDesc + ";EventDate:" + sFormat.format(parEventDate) + ";Event:" + parEvent + ";FigureID:"
							+ parFigureID.getID());
				}

				parDateDesc = p.substring(0, p.indexOf("日") + 1);
				parEvent = p.substring(p.indexOf("日") + 1);

				Calendar c = Calendar.getInstance();
				c.set(y, m, Integer.valueOf(parseDateFromArticleParagraph(parDateDesc)));
				parEventDate = c.getTime();
			} else if (p.length() < 10) { // 如果段落长度少于10个字符，认为该段落为分类
				parCategory = p.trim();
				if (international)
					parCategory = "国际：" + parCategory;
			} else { // 其它情况，段落为新的同期条目开始，则存储之前条目后创建新条目，沿用除内容外的其它信息
				IDCounter++;
				System.out.println("ID:" + IDCounter + ";ArtID:" + parAID.getID() + ";Title" + parTitle + ":;Author:" + parAuthor + ";PageNum:" + parPageNum + ";PDFPageNum:" + parPDFPageNum
						+ ";Category:" + parCategory + ";Source:" + parSource + ";DateDesc:" + parDateDesc + ";EventDate:" + sFormat.format(parEventDate) + ";Event:" + parEvent + ";FigureID:"
						+ parFigureID.getID());
				if (p.substring(0, 1).equals("△"))
					parEvent = p.substring(1);
				else
					parEvent = p.trim();
			}
		}
	}

	private static String parseDateFromArticleParagraph(String p) {
		String str = p;

		String EventDateStr = "";
		while (Utils.isNumeric(str.substring(0, 1))) { // 截取大事记开始时间，即最前端数字代表的日期
			EventDateStr = EventDateStr + str.substring(0, 1);
			str = str.substring(1);
		}
		return EventDateStr;
	}

	public static boolean isJnlPageNum(String s) {
		String a2z = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		return (isNumeric(s) || a2z.indexOf(s) >= 0);
	}

	public static Connection getConnection() {
		return Utils.getInstance().getConn();
	}

	/*
	 * 计算简单数字列表中包含的数字 目前只支持“[0-9]”
	 */
	public static String[] calculateSimpleNumList(String str) {
		if (str.indexOf("[0-9]") >= 0) {
			String[] newStr = new String[10];
			for (int i = 0; i <= 9; i++) {
				newStr[i] = str.substring(0, str.indexOf("[0-9]")) + i + str.substring(str.indexOf("[0-9]") + 5);
			}
			return newStr;
		} else
			return str.split(",");
	}

	public static String StringNull2Empty(String s) {
		if (s == null)
			return "";
		else
			return s;
	}

	public static String getJnlIssueContentURL(ResourceID id) {
		return Utils.getInstance().getServiceRootURL() + Utils.getInstance().getShowJnlIssueContentURL() + id.getID();
	}
	
	public static String getJnlFigureContentURL(ResourceID id) {
		return Utils.getInstance().getServiceRootURL() + Utils.getInstance().getShowJnlFigureContentURL() + id.getID();
	}
	

	public static String getEventEntryMonth(String s) {
		String sMonth = "";
		s = s.substring(0, s.indexOf("月"));
		s =Utils.ReplaceMonthNumber(s);
		while (Utils.isNumeric(s.substring(s.length() - 1))) {
			sMonth = s.substring(s.length() - 1) + sMonth;
			s = s.substring(0, s.length() - 1);
		}
		return sMonth;
	}

	private void buildCLCfromDB() {
		String SQL = "select id, name, code, parent_id from res_category where type = 1";

		Statement stmt;
		try {
			stmt = Utils.getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(SQL);

			FileWriter fw = new FileWriter("CLC.txt", true);
			while (rs.next()) {
				fw.write(rs.getLong("id") + "\t" + rs.getString("name").trim() + "\t" + rs.getString("code").trim() + "\t" + rs.getLong("parent_id") + "\n");
			}
			stmt.close();
			fw.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void buildCCT() {
		BaseCLC clc = BaseCLC.getInstance();
		BaseCCT cct = BaseCCT.getInstance();

		clc.loadFromText(new File("baseData/CLC.txt"));

		String[] FileNames = { "C", "D", "DF", "E", "F", "G" };

		for (int i = 0; i < FileNames.length; i++) {
			System.out.println("Processing " + FileNames[i]);
			File f = new File("baseData/" + FileNames[i] + ".txt");
			if (f.exists()) {
				String line = null;
				try {
					InputStreamReader read = new InputStreamReader(new FileInputStream(f), "GBK");// 考虑到编码格式
					BufferedReader bufferedReader = new BufferedReader(read);

					while ((line = bufferedReader.readLine()) != null) {
						if (line.trim() != "") {
							String fields[] = line.split("\t");
							String clcCode = fields[0].trim();
							String relatedWord = fields[1].trim();
							String subjectWord = "";
							if (fields.length == 3)
								subjectWord = fields[2].trim();

							cct.addThesaurus(subjectWord, relatedWord);
							cct.addCCTItem(clcCode, subjectWord);
							cct.addCCTItem(clcCode, relatedWord);
						}
					}
					read.close();
				} catch (Exception ex) {
					System.out.println("Error Line :" + line);
					ex.printStackTrace();
				}
			}
		}

		cct.export();
	}

	public static String getNoteResourceTypeDesc(int nrt) {
		switch (nrt) {
		case IResourceNoteConstants.NOTE_ON_JOURNAL_ARTICLE:
			return "期刊文章";
		case IResourceNoteConstants.NOTE_ON_JOURNAL_ISSUE:
			return "期刊期次";
		case IResourceNoteConstants.NOTE_ON_JOURNAL_FIGURE:
			return "期刊插图";
		case IResourceNoteConstants.NOTE_ON_PARAGRAPH:
			return "段落";
		case IResourceNoteConstants.NOTE_ON_EVENTENTRY:
			return "大事辑览";
		case IResourceNoteConstants.NOTE_ON_FILE:
			return "文件";
		case IResourceNoteConstants.NOTE_ON_IMAGE:
			return "图片";
		}
		return String.valueOf(nrt);
	}

	public static String getNoteLocationTypeDesc(int nlt) {
		switch (nlt) {
		case IResourceNoteConstants.NOTE_LOCATION_TYPE_TEXT_OFFSET:
			return "定位文本中位置";
		case IResourceNoteConstants.NOTE_LOCATION_TYPE_WHOLE_RESOURCE:
			return "定位整个资源";
		}
		return String.valueOf(nlt);
	}

	public static String getNoteLocationDesc(String nl, int nlt) {
		switch (nlt) {
		case IResourceNoteConstants.NOTE_LOCATION_TYPE_TEXT_OFFSET: {
			String section[] = nl.split(",");
			String field = "";
			switch (Integer.valueOf(section[0].split(":")[1])) {
			case IResourceNoteConstants.NOTE_ON_JNLART_AUTHOR:
				field = "作者";
				break;
			case IResourceNoteConstants.NOTE_ON_JNLART_CONTENT:
				field = "正文";
				break;
			case IResourceNoteConstants.NOTE_ON_JNLART_SORT:
				field = "期刊内分类";
				break;
			case IResourceNoteConstants.NOTE_ON_JNLART_SOURCE:
				field = "来源";
				break;
			case IResourceNoteConstants.NOTE_ON_JNLART_TITLE:
				field = "标题";
				break;
			case IResourceNoteConstants.NOTE_ON_JNLART_WEBSITECAT:
				field = "网站栏目";
				break;
			}
			return "【" + field + "】从第" + (Integer.valueOf(section[1].split(":")[1]) + 1) + "个字到第" + (Integer.valueOf(section[2].split(":")[1]) + 1) + "个字";
		}
		}
		return nl;
	}
}