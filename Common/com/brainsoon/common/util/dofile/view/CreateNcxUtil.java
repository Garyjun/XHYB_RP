package com.brainsoon.common.util.dofile.view;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.brainsoon.common.util.dofile.content.PdfUtil;
import com.brainsoon.common.util.dofile.util.DoFileException;
import com.brainsoon.common.util.dofile.util.DoFileUtils;
import com.brainsoon.common.util.dofile.vo.InitTreeData;
import com.brainsoon.common.util.dofile.vo.NavPointVO;

/**
 * 
 * @ClassName: CreateNcxUtil 
 * @Description: 创建ncx工具类（暂只使用了部分方法） 
 * @author tanghui 
 * @date 2014-5-5 下午1:34:52 
 *
 */
public class CreateNcxUtil {
	
	private static final Logger logger = Logger.getLogger(CreateNcxUtil.class);
	private static final Map<String, String> metaDefineMap = new HashMap<String, String>();
	private static final Map<String, String> opfMetaMap = new HashMap<String, String>();
	private static final Map<String, String> TAG_MAP = new HashMap<String, String>();
	private static final Map<String, String> LEVEL_MAP = new HashMap<String, String>();
	//class 文前的合集
	private static final Map<String, String> PRE_MAP = new HashMap<String, String>();
	//class 文后
	private static final Map<String, String> END_MAP = new HashMap<String, String>();

	
	
	static {

		LEVEL_MAP.put("0", "volume");
		LEVEL_MAP.put("1", "chapter");
		LEVEL_MAP.put("2", "section");
		LEVEL_MAP.put("3", "subsection");
		
		TAG_MAP.put("书名页", "TitlePage");
		TAG_MAP.put("版权页", "Copyright");
		TAG_MAP.put("目录", "Catalog");
		
		PRE_MAP.put("封面", "Cover");
		PRE_MAP.put("书名页", "TitlePage");
		PRE_MAP.put("版权页", "Copyright");
		PRE_MAP.put("目录", "Catalog");
		PRE_MAP.put("前言", "Preface");
		PRE_MAP.put("序言", "Preface");
		PRE_MAP.put("序", "Preface");
		
		END_MAP.put("附录", "Appendix");
		END_MAP.put("参考文献", "Reference");
		END_MAP.put("封底", "BackCover");
		END_MAP.put("书脊", "Spine");
		

		metaDefineMap.put("目录名", "Dirname");
		metaDefineMap.put("版次", "pubserno");
		metaDefineMap.put("版本", "pubcnt");
		metaDefineMap.put("印次", "Prtcnt");
		metaDefineMap.put("纸质图书价格", "PaperPrice");
		metaDefineMap.put("装帧", "Binding");
		metaDefineMap.put("附件", "Attchments");
		metaDefineMap.put("尺寸", "Dimensions");
		metaDefineMap.put("中图分类号", "cbclass");
		metaDefineMap.put("其它题名/副题名", "OtherTitle");
		metaDefineMap.put("并列题名", "ParTitle");
		metaDefineMap.put("交替题名", "AltTitle");
		metaDefineMap.put("题名拼音", "AlphabetTitle");
		metaDefineMap.put("丛书作者姓名", "contributorclb");
		metaDefineMap.put("内容提要", "Description");
		metaDefineMap.put("关键词", "Subject");
		metaDefineMap.put("语种", "Language");
		metaDefineMap.put("ISBN", "isbn");
		metaDefineMap.put("DOI", "identifier");
		metaDefineMap.put("作者", "creator");
		metaDefineMap.put("书名", "title");
		metaDefineMap.put("出版时间", "PublishDate");
		metaDefineMap.put("统一书号", "unbn");
		metaDefineMap.put("书号", "cobn");
		metaDefineMap.put("分册", "volume");
		metaDefineMap.put("科图分类号", "SBClass");
		metaDefineMap.put("丛编次要责任者	", "SerialSubDuty");
		metaDefineMap.put("丛编责任者", "SerialDuty");
		metaDefineMap.put("丛书作者姓名", "SerialAuthorName");
		metaDefineMap.put("分丛书名", "DisSerialName");
		metaDefineMap.put("并列丛书号", "ParSerialNo");
		metaDefineMap.put("并列丛书名", "ParSerialName");
		metaDefineMap.put("丛书号", "SerialNo");
		metaDefineMap.put("丛书名", "SerialName");
		metaDefineMap.put("合订题名", "BouTitle");
		metaDefineMap.put("分册号（图书）", "FasciculeNo");
		metaDefineMap.put("电子文件载体", "EDVector");
		metaDefineMap.put("国际标准电子图书编号", "EISBN");
		metaDefineMap.put("一般性附注", "CommonNote");
		metaDefineMap.put("期刊终止年", "IssueEndYear");
		metaDefineMap.put("期刊起始年", "IssueStartYear");
		metaDefineMap.put("总期号", "TotalIssueNo");
		metaDefineMap.put("期（期刊）", "Issue");
		metaDefineMap.put("卷（期刊）", "Volumes");
		metaDefineMap.put("年（期刊）", "Year");
		metaDefineMap.put("编写方式", "EditForm");
		metaDefineMap.put("作者简介", "AuthorInfo");
		metaDefineMap.put("读者对象", "Reader");
		metaDefineMap.put("版本来源", "PubserSource");
		metaDefineMap.put("版本说明", "PubserDesc");
		metaDefineMap.put("印数", "Printing");
		metaDefineMap.put("字数", "Words");
		metaDefineMap.put("印张", "Sheets");
		metaDefineMap.put("印刷机构", "PriMechanism");
		metaDefineMap.put("发行机构", "Issuers");
		metaDefineMap.put("出版者地址", "PublishPlace");
		metaDefineMap.put("出版社", "Press");
		metaDefineMap.put("原著出版国", "OriCounty");
		metaDefineMap.put("原著语种", "OriLanguage");
		metaDefineMap.put("中间语种", "MidLanguage");
		metaDefineMap.put("责任印制", "PriResponsibility");
		metaDefineMap.put("责任校对", "ResponsibilityPro");
		metaDefineMap.put("版式设计", "LayoutDesign");
		metaDefineMap.put("封面设计", "CoverDesign");
		metaDefineMap.put("编辑助理", "EditorialAssistant");
		metaDefineMap.put("责任编辑", "Editor");
		metaDefineMap.put("次要团体责任者", "SubTeamDuty");
		metaDefineMap.put("团体责任者", "TeamDuty");
		metaDefineMap.put("次要责任者著作责任", "SubWriteDuty");
		metaDefineMap.put("次要责任者", "SubDuty");
		metaDefineMap.put("第一责任者著作责任", "PrimWriteDuty");
		metaDefineMap.put("第一责任者", "PrimDuty");
		metaDefineMap.put("国家代码", "CountryCode");
		metaDefineMap.put("CODEN", "CODEN");
		metaDefineMap.put("ISSN", "ISSN");
		metaDefineMap.put("标准实施时间", "StandardImpTime");
		metaDefineMap.put("标准发布时间", "StandardPublished");
		metaDefineMap.put("标准发布单位", "StandPubser");
		metaDefineMap.put("标准号", "StandNo");
		metaDefineMap.put("卷端题名", "VolumeTitle");
		metaDefineMap.put("书脊题名", "SpineTitle");
		metaDefineMap.put("封面题名", "CoverTitle");

		opfMetaMap.put("AlphabetTitle", "题名拼音");
		opfMetaMap.put("AltTitle", "交替题名");
		opfMetaMap.put("Partitle", "并列题名");
		opfMetaMap.put("OtherTitle", "其它题名/副题名");
		opfMetaMap.put("serialname", "丛书名称");
		opfMetaMap.put("cbclass", "中图分类号");
		opfMetaMap.put("review", "封底书评");
		opfMetaMap.put("Words", "字数");
		opfMetaMap.put("pages", "页数");
		opfMetaMap.put("dimensions", "开本（16开、32开）");
		opfMetaMap.put("Attchments", "附件");
		opfMetaMap.put("Binding", "装帧");
		opfMetaMap.put("cip", "CIP核字号");
		opfMetaMap.put("PaperPrice", "纸质图书价格");
		opfMetaMap.put("Prtcnt", "印次");
		opfMetaMap.put("pubcnt", "版本");
		opfMetaMap.put("specclass", "???");
		opfMetaMap.put("eprice", "???");
	}

	
	
	/**
	 * 生成ncx文件，并封装为对像
	 * 
	 * @param pdfPath
	 * @return
	 * @throws Exception
	 */
	public static InitTreeData loadInitTreeData(String pdfPath) {
		
		try {
			String content = loadNcx(pdfPath ,null);
			
			InitTreeData initTreeData = new InitTreeData();
			initTreeData.setInitTreeData(null);
			
			Document doc = DocumentHelper.parseText(content);
			Element rootElement = doc.getRootElement();
		    Element navMap = rootElement.element("navMap");
		    @SuppressWarnings("unchecked")
	        List<Element> navElements = navMap.elements("navPoint");
	        try {
				if (!decodingNavPoint(initTreeData, navElements)) {
				    return null;
				}
			} catch (Exception e) {
				logger.error(e);
				return null;
			}
	        
	        List<InitTreeData> list= new ArrayList<InitTreeData>(initTreeData.getInitTreeDatas());
	        Collections.sort(list);
	        initTreeData.setInitTreeDatas(new TreeSet<InitTreeData>(list));
	        return initTreeData;
		} catch (Exception e) {
			logger.error(e);
			return null;
		}
	}
	
	/**
	 * 解析ncx的导航的根结点
	 * 
	 * @param initTreeDataParent
	 * @param navElements
	 * @return
	 * @throws Exception
	 */
    private static boolean decodingNavPoint(InitTreeData initTreeDataParent ,
            List<Element> navElements) throws Exception {
    	 for (Element element : navElements) {
             try {
            	 InitTreeData initTreeData = new InitTreeData();
            	 setInitDataElm(initTreeData, element);
            	 initTreeData.setInitTreeData(null);
            	 initTreeData.setpId("0");
            	 initTreeDataParent.getInitTreeDatas().add(initTreeData);
            	 List<InitTreeData> list= new ArrayList<InitTreeData>(initTreeDataParent.getInitTreeDatas());
            	 Collections.sort(list);
            	 initTreeDataParent.setInitTreeDatas(new TreeSet<InitTreeData>(list));
            	 
            	 List moreNav = element.elements("navPoint");
            	 if(moreNav != null && moreNav.size() > 0){
            		 initTreeData.setParent(true);
            	 }
            	 if (!decodingDeepNavPoint(initTreeData, moreNav)) {
                	return false;
            	 }
             } catch (Exception e) {
            	 e.printStackTrace();
                 throw new DoFileException("读取解压后的xml文件内容错误！");
             }
         }
         return true;
    }
    
    /**
     * 设置属性值
     * 
     * @param initTreeData
     * @param element
     */
    private static void setInitDataElm(InitTreeData initTreeData, Element element){
    	initTreeData.setClassValue(element.attribute("class") != null ? element
    			.attribute("class").getValue() : "");
    	initTreeData.setCatalogValue(element.attribute("catalog") != null ? element
                .attribute("catalog").getValue() : "");
    	initTreeData.setType(element.attribute("level") != null ? element
                .attribute("level").getValue() : "");
    	initTreeData.setPlayOrder(element.attribute("playOrder") != null ? Integer
                .parseInt(element.attribute("playOrder").getValue())
                : 0);
		long  playOrderCover = Long.parseLong(initTreeData.getPlayOrder().toString()) ;
		if(playOrderCover==0l){
			playOrderCover = -1l;
		}

		initTreeData.setUnit(1);
		initTreeData.setFileIndexing("#");
		Element label = element.element("navLabel");
		String title = label.element("text") != null ? label.element("text").getText() : "";
		
		if(title.length() > 256){
			throw new DoFileException("ncx文件中 text节点内容过长："+title);
		}
		initTreeData.setTitle(title);
		initTreeData.setStart(label.element("navStartPage") != null ? Integer
                .parseInt(label.element("navStartPage").getText()) : 0);
		initTreeData.setEnd(label.element("navEndPage") != null ? Integer
                .parseInt(label.element("navEndPage").getText()) : 0);
		/*
		Element content = element.element("content");
		if(null != content){
			initTreeData.setFileTempName(content.attribute("src") != null ? content.attribute("src").getValue() : "");
		}*/
    }
    
    /**
     * 设置ncx的子结点信息
     * 
     * @param initTreeDataParent
     * @param navElements
     * @return
     * @throws Exception
     */
    private static boolean decodingDeepNavPoint(InitTreeData initTreeDataParent, List<Element> navElements) throws Exception {
    	 for (Element element : navElements) {
    		 try {
            	InitTreeData initTreeData = new InitTreeData();
            	setInitDataElm(initTreeData, element);
            	initTreeData.setpId(initTreeDataParent.getId()+"");
            	initTreeData.setInitTreeData(initTreeDataParent);
            	initTreeDataParent.getInitTreeDatas().add(initTreeData);
                
            	List<InitTreeData> list= new ArrayList<InitTreeData>(initTreeDataParent.getInitTreeDatas());
     	        Collections.sort(list);
     	        initTreeDataParent.setInitTreeDatas(new TreeSet<InitTreeData>(list));
     	        
                List moreNav = element.elements("navPoint");
                if(moreNav != null && moreNav.size() > 0){
           		 	initTreeData.setParent(true);
                }
                if (!decodingDeepNavPoint(initTreeData, moreNav)) {
                     return false;
                }
             } catch (Exception e) {
            	 e.printStackTrace();
                 throw new DoFileException("读取解压后的xml文件内容错误!");
             }
         }
         return true;
    }
	
    /**
     * 生成ncx文件
     * 
     * @param pdfPath
     * @param params
     * @return
     * @throws Exception
     */
	public static String loadNcx(String pdfPath, Map<String,String> params) throws Exception{
		String content = "";
		File pdfFile = new File(pdfPath);
		if(!pdfFile.exists()){
			throw new Exception("pdf文件不存在！");
		}
		if(params==null){
			params = new HashMap<String,String>();
		}
		List<NavPointVO> navPointList = new ArrayList<NavPointVO>();
		String pdfOutLine = PdfUtil.loadPdfOutLine(pdfPath);
		//System.out.println("======== \n" + pdfOutLine);
		if(StringUtils.isBlank(pdfOutLine)){
			logger.error("【" + pdfPath +"】提取PDF导航失败，可能此文件无没有导航书签！");
		}else{
			navPointList = parseNavPointFromPdfOutline(pdfOutLine);
			
			String fileNameSource = pdfFile.getName();
			
			String fileName = fileNameSource.substring(0,fileNameSource.lastIndexOf("."));
			content = createNCX(navPointList, fileName, params);
		}
		return content;
	}
	
	/**
	 * 生成ncx的相关结点
	 * 
	 * @param navPointList
	 * @param fileName
	 * @param params
	 * @return
	 * @throws DoFileException
	 */
	private static String createNCX(List<NavPointVO> navPointList,String fileName, Map<String,String> params) throws DoFileException {

		Document ncxDocument = DocumentHelper.createDocument();
		Element ncx = ncxDocument.addElement("ncx", "http://www.daisy.org/z3986/2005/ncx/");
		ncx.addAttribute("version", "2005-1");
		Element head = ncx.addElement("head");
		head.addElement("meta").addAttribute("name", "dtb:uid").addAttribute("content",DoFileUtils.Null2Empty(params.get("identifier")));
		head.addElement("meta").addAttribute("name", "dtb:depth").addAttribute("content", "2");
		head.addElement("meta").addAttribute("name", "dtb:generator").addAttribute("content", "");
		head.addElement("meta").addAttribute("name", "dtb:totalPageCount").addAttribute("content", "");
		head.addElement("meta").addAttribute("name", "dtb:maxPageNumber").addAttribute("content", "");
		ncx.addElement("docTitle").addElement("text").setText(DoFileUtils.Null2Empty(params.get("title")));
		ncx.addElement("docAuthor").addElement("text").setText(DoFileUtils.Null2Empty(params.get("creator")));

		try {
			Element navMap = ncx.addElement("navMap");
			int playOrder = 1;
			for (NavPointVO nav : navPointList) {
				playOrder = buildNavpoint(nav, navMap, fileName, playOrder);
			}
			ncxDocument = changeNcxEndPage(ncxDocument);
			return ncxDocument.asXML();
		} catch (Exception ex) {
			logger.error("生成NCX、PDF文件失败！", ex);
			throw new DoFileException(ex.getMessage());
		}
	}
	
	/**
	 * 更新book.ncx中的navEndPage,bookEndPage
	 * 
	 * @param document
	 * @return
	 */
	private static Document changeNcxEndPage(Document document) {
		try {
			String xmlText = document.asXML();
			xmlText = xmlText.replace("xmlns=\"http://www.daisy.org/z3986/2005/ncx/\"", "nameSpace=\"ncx\"");
			document = DocumentHelper.parseText(xmlText);
			List list = document.selectNodes("//navPoint");
			Element navPointNode = null;
			for (int i = 0; i < list.size(); i++) {
				navPointNode = (Element) list.get(i);
				dealNavPoint(navPointNode);
			}
			xmlText = document.asXML();
			xmlText = xmlText.replace("nameSpace=\"ncx\"", "xmlns=\"http://www.daisy.org/z3986/2005/ncx/\"");
			document = DocumentHelper.parseText(xmlText);

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return document;

	}
	
	/**
	 * 设置结点的值
	 * 
	 * @param navPointNode
	 */
	private static void dealNavPoint(Element navPointNode) {
		Element parent = navPointNode.getParent();
		Element node = (Element) navPointNode.selectSingleNode("navLabel");
		Element title = (Element) node.selectSingleNode("text");
		if (title.getText().indexOf("版权页") != -1) {
			navPointNode.addAttribute("catalog", "copyright");
		}
		Element navStartPage = (Element) node.selectSingleNode("navStartPage");
		Element navEndPage = (Element) node.selectSingleNode("navEndPage");
		Element bookEndPage = (Element) node.selectSingleNode("bookEndPage");
		List brothers = parent.selectNodes("navPoint");
		int pos = brothers.indexOf(navPointNode);
		if (pos + 1 < brothers.size()) {
			Element nextBrother = (Element) brothers.get(pos + 1);
			Element nextNode = (Element) nextBrother.selectSingleNode("navLabel");
			Element nextTitle = (Element) nextNode.selectSingleNode("text");
			Element nextStartPage = (Element) nextNode.selectSingleNode("navStartPage");
			int currentStart = Integer.valueOf(navStartPage.getTextTrim());
			int nextStart = Integer.valueOf(nextStartPage.getTextTrim());
			int currentEnd = nextStart;
			if (nextStart > currentStart) {
				currentEnd = nextStart - 1;
			}
			
			if(currentStart>currentEnd){
				currentEnd = currentStart;
			}
			
			navEndPage.setText(String.valueOf(currentEnd));
			bookEndPage.setText(String.valueOf(currentEnd));
		}

	}
	
	/**
	 * 创建 .ncx navPoint节点
	 * 
	 * @param pageInfo
	 * @param ncx
	 * @param fileName
	 * @param playOrder
	 * @return
	 * @throws DoFileException
	 */
	private static int buildNavpoint(NavPointVO pageInfo, Element ncx, String fileName, int playOrder) throws DoFileException {
		Element navPoint = ncx.addElement("navPoint");
		if(pageInfo != null){
			String title = pageInfo.getTitle();

			if (StringUtils.equals(title, "版权页")) {
				navPoint.addAttribute("catalog", "copyright");
			} else {
				navPoint.addAttribute("catalog", "content");
			}
			navPoint.addAttribute("class", pageInfo.getClassValue());
			navPoint.addAttribute("level", pageInfo.getLevelValue());
			navPoint.addAttribute("id", fileName + "-" + playOrder);
			navPoint.addAttribute("playOrder", String.valueOf(playOrder));

			//开始页大于结束页情况下，则结束页等于开始页
			if(pageInfo.getStart()>pageInfo.getEnd()){
				pageInfo.setEnd(pageInfo.getStart());
			}
					
			Element navLabel = navPoint.addElement("navLabel");
			navLabel.addElement("text").setText(pageInfo.getTitle());
			navLabel.addElement("navStartPage").setText(String.valueOf(pageInfo.getStart()));
			navLabel.addElement("navEndPage").setText(String.valueOf(pageInfo.getEnd()));
			navLabel.addElement("bookStartPage").setText(String.valueOf(pageInfo.getStart()));
			navLabel.addElement("bookEndPage").setText(String.valueOf(pageInfo.getEnd()));
			/* 创建content */
			Element content = navPoint.addElement("content");
			content.addAttribute("src", fileName + ".pdf#" + String.valueOf(pageInfo.getStart()));
			playOrder++;
			for (NavPointVO nav : pageInfo.getChildren()) {
				playOrder = buildNavpoint(nav, navPoint, fileName, playOrder);
			}
		}
		return playOrder;
	}
	
	/**
	 * 解析抽取PDF的导航信息，生成NavPointInfo值对象
	 * 
	 * @param pdfOutLine
	 * @return
	 */
	private static List<NavPointVO> parseNavPointFromPdfOutline(String pdfOutLine) throws DoFileException {
		List<NavPointVO> navList = new ArrayList<NavPointVO>();
		Map<String, String> checkMap = new HashMap<String, String>();
		checkMap.put("hasCopyrihtPage", "false");
		checkMap.put("hasCoverPage", "false");
		List<Element> nodes = new ArrayList<Element>();
		try {
			Document doc = DocumentHelper.parseText(pdfOutLine);
			Element root = doc.getRootElement();
			nodes = root.elements("Title");
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new DoFileException("抽取PDF导航信息出错！");
		}
		// 目录层级
		int level;
		if(nodes != null){
			for (Element element : nodes) {
				level = 1;
				NavPointVO navPointVO = recursiveElement(element, checkMap, level);
				if(navPointVO != null){
					navList.add(navPointVO);
				}
			}
		}
		

		return navList;
	}
	
	private static  NavPointVO recursiveElement(Element e, Map<String, String> checkMap, int level) {
		List<Element> elements = e.elements("Title");
		NavPointVO navPoint = createNaviPointInfo(e, checkMap);
		if(navPoint != null){
			if (level < 3) {
				navPoint.setLevelValue(LEVEL_MAP.get(String.valueOf(level)));
			} else {
				navPoint.setLevelValue(LEVEL_MAP.get(String.valueOf(3)));
			}
			if (elements.size() > 0) {
				level++;
				for (Element element : elements) {
					navPoint.getChildren().add(recursiveElement(element, checkMap, level));
				}
			}
		}
		
		return navPoint;
	}
	
	/**
	 * 
	 * @param e
	 * @param checkMap
	 * @return
	 */
	private static NavPointVO createNaviPointInfo(Element e, Map<String, String> checkMap) {
		//判断这三个属性是否同时存在，不存在则不保存
		Attribute attr0 = e.attribute("id");
		Attribute attr = e.attribute("start");
		Attribute attr1 = e.attribute("end");
		boolean b = false;
		if(attr0 != null && attr != null && attr1 != null){
			b = true;
		}
		NavPointVO navTmp = null;
		if(b){
			navTmp = new NavPointVO();
			int id;
			if(attr0 != null){
				String val = attr0.getValue();
				if(StringUtils.isNotBlank(val)){
					id = Integer.parseInt(val);
					navTmp.setId(id);
				}
			}
			
			
			String title = e.getTextTrim();
			if (PRE_MAP.get(title) != null) {
				navTmp.setClassValue("pre");
			} else {
				if (END_MAP.get(title) != null) {
					navTmp.setClassValue("end");
				} else {
					navTmp.setClassValue("body");
				}
			}

			if (StringUtils.startsWith(title, "附录")) {
				navTmp.setClassValue("end");
			}
			
			String textVal = e.getText();
			if(StringUtils.isNotBlank(textVal)){
				if (textVal.equals("版权页")) {
					checkMap.put("hasCopyrihtPage", "true");
					navTmp.setTitle("版权页");
				} else {
					navTmp.setTitle(title);
				}
				
				if (textVal.equals("封面")) {
					checkMap.put("hasCoverPage", "true");
				}
			}
			
			
			
			

			int start;
			if(attr != null){
				String val = attr.getValue();
				if(StringUtils.isNotBlank(val)){
					start = Integer.parseInt(val);
					navTmp.setStart(start);
				}
			}

			int end;
			if(attr1 != null){
				String val = attr1.getValue();
				if(StringUtils.isNotBlank(val)){
					end = Integer.parseInt(val);
					navTmp.setEnd(end);
				}
			}
		}
		return navTmp;
	}
	
	//test
	public static void main(String[] args) {
		try {
			InitTreeData initTreeData = loadInitTreeData("D:\\pdf\\td.0000-t.isbn.9787113152895.c.0001.pdf");
			
			Set<InitTreeData> initTreeDatas = new LinkedHashSet<InitTreeData>();
			
			System.out.println("---1-----:"+initTreeData.getInitTreeDatas().size());
			for (InitTreeData initTreeDataT : initTreeData.getInitTreeDatas()) {
				if (initTreeDataT.getInitTreeData() == null) {
					initTreeDatas.add(initTreeDataT);
				}
			}
			
			for(InitTreeData initTreeDataT0 : initTreeDatas){
				System.out.println("---1-----:"+initTreeDataT0.getTitle());
				
				for(InitTreeData initTreeDataT1 : initTreeDataT0.getInitTreeDatas()){
					System.out.println("---2--------:"+initTreeDataT1.getTitle());
					for(InitTreeData initTreeDataT2 : initTreeDataT1.getInitTreeDatas()){
						System.out.println("---3-----------:"+initTreeDataT2.getTitle());
						
						for(InitTreeData initTreeDataT3 : initTreeDataT2.getInitTreeDatas()){
							System.out.println("---4-------------:"+initTreeDataT3.getTitle());
						}
						
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
