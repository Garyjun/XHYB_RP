package com.brainsoon.common.util.dofile.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.brainsoon.common.util.dofile.util.DoFileUtils;


/**
 * 
 * @ClassName: XmlFileUtil 
 * @Description:  NCX XML 导航创建类 
 * @author tanghui 
 * @date 2014-6-24 下午3:51:19 
 *
 */
public class XmlFileUtil {

	private static final Logger logger = Logger.getLogger(XmlFileUtil.class);

	/**
	 * 
	 * @Title: createXMLFile
	 * @Description: 创建 NODE XML
	 * @param
	 * @return boolean
	 * @throws
	 */
	public static boolean createXMLFile(String filename, int titalPage,
			List<CatalogDTO> bookCatalogObjs) {
		Document document = DocumentHelper.createDocument();
		Element rootElement = document.addElement("book");
		Element titalPageElement = rootElement.addElement("totalpage");
		titalPageElement.setText(titalPage + "");
		setElement(rootElement, bookCatalogObjs);
		XMLWriter writer = null;
		try {
			OutputFormat format = OutputFormat.createPrettyPrint();
			// format.setEncoding("UTF-8");
			FileOutputStream fos = new FileOutputStream(filename);
			// writer = new XMLWriter(new FileWriter(xmlFile), format);
			writer = new XMLWriter(fos, format);
			writer.write(document);
			writer.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}finally{
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	public static void setElement(Element parentElement,
			List<CatalogDTO> bCatalogObjs) {
		try {
			for (int i = 0; i < bCatalogObjs.size(); i++) {
				Element element = parentElement.addElement("node");
				CatalogDTO bookCatalogObj = bCatalogObjs.get(i);
				element.addAttribute("label", bookCatalogObj.getNavTitel());
				element.addAttribute("navStart", bookCatalogObj.getNavPage());
				element.addAttribute("class", bookCatalogObj.getCss());
				element.addAttribute("level", bookCatalogObj.getLevel());
				setElement(element, bookCatalogObj.getSons());
			}
		} catch (Exception e) {
			// tanghui: handle exception
		}
	}

	public static List<CatalogDTO> ReadXMLFile(String filename, String namespace) {
		List<CatalogDTO> bookCatalogObjs = null;
		try {
			File xmlFile = new File(filename);  
	        FileInputStream fis = new FileInputStream(xmlFile);  
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(fis);
			bookCatalogObjs = ReadXMLDocument(document, namespace);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
		return bookCatalogObjs;
	}

	@SuppressWarnings("unchecked")
	public static List<CatalogDTO> ReadXMLDocument(Document document,String namespace) {
		List<CatalogDTO> bookCatalogObjs = null;
		try {
			Element rootElt = document.getRootElement(); // 获取根节点
			Iterator<Element> iter = rootElt.elementIterator("navMap"); // 获取根节点下的子节点head
			// 遍历head节点
			while (iter.hasNext()) {
				if(bookCatalogObjs == null){
					bookCatalogObjs = new ArrayList<CatalogDTO>();
				}
				Element recordEle = (Element) iter.next();
				List<Element> list1 = recordEle.elements("navPoint");
				bookCatalogObjs = getElement(list1, bookCatalogObjs,"-1");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
		return bookCatalogObjs;
	}

	@SuppressWarnings("unchecked")
	public static List<CatalogDTO> getElement(List<Element> list1,
			List<CatalogDTO> bookCatalogObjs,String pId) {
		for (Element element1 : list1) {
			List<CatalogDTO> childObjs = new ArrayList<CatalogDTO>();
			CatalogDTO bookCatalogObj = new CatalogDTO();
			List<Element> list2 = element1.elements("navLabel");
			Attribute idAttr = element1.attribute("id");
			if(idAttr != null){
				bookCatalogObj.setId(idAttr.getValue());
			}
			
			Attribute classAttr = element1.attribute("class");
			if(classAttr != null){
				bookCatalogObj.setCss(classAttr.getValue());
			}
			
			Attribute levelAttr = element1.attribute("level");
			if(levelAttr != null){
				bookCatalogObj.setLevel(levelAttr.getValue());
			}
			
			bookCatalogObj.setpId(pId);
			
			for (Element element2 : list2) {
				bookCatalogObj.setNavTitel(element2.elementText("text"));
				bookCatalogObj.setName(element2.elementText("text"));
				bookCatalogObj.setNavPage(element2.elementText("navStartPage"));
				bookCatalogObj.setStartPage(element2.elementText("navStartPage"));
				bookCatalogObj.setEndPage(element2.elementText("navEndPage"));
				
			}
			//URL
			Element content = element1.element("content");
			if(content != null){
				String contentTxt = content.attribute("src") != null ? content.attribute("src").getValue() : "";
				bookCatalogObj.setFileUrl(contentTxt);
			}
			
			List<Element> list3 = element1.elements("navPoint");
			List<CatalogDTO> childObjss = null;
			if(list3 != null && list3.size() > 0){
				bookCatalogObj.setParent(true);
				bookCatalogObj.setOpen(true);
				childObjss = getElement(list3, childObjs,bookCatalogObj.getId());
			}else{
				bookCatalogObj.setParent(false);
			}
			
			bookCatalogObj.setSons(childObjss);
			
			bookCatalogObjs.add(bookCatalogObj);
		}
		return bookCatalogObjs;
	}
	
	
	/**
	 * 
	 * @Title: createXMLFile 
	 * @Description: 
	 * @param   
	 * @return void 
	 * @throws
	 */
	public static  void createXMLFile(Document document,String outXmlPath) {
		XMLWriter output = null;
		try {
			//一行一行的写
			OutputFormat outFmt = new OutputFormat("\t", true);
		    outFmt.setEncoding("utf-8");
		    DoFileUtils.mkdir(outXmlPath);
		    output = new XMLWriter(new FileOutputStream(outXmlPath), outFmt);  
		    output.write(document);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(output != null){
					output.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * 
	 * @Title: createXMLFile 
	 * @Description: 
	 * @param   
	 * @return void 
	 * @throws
	 */
	public static  void createXMLFile(String xmlString,String outXmlPath) {
		java.io.Writer output = null;
		try {
			DoFileUtils.mkdir(outXmlPath);
		    output =new java.io.OutputStreamWriter(new java.io.FileOutputStream(outXmlPath),"UTF-8");  
		    output.write(xmlString);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(output != null){
					output.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static List<CatalogDTO> NcxCvtXml(String sourceFile, String destFile,
			int titalPage, String namespace) {
		if(StringUtils.isBlank(namespace)){
			namespace = "http://www.daisy.org/z3986/2005/ncx/";
		}
		List<CatalogDTO> bookCatalogObjs = null;
		try {
			File file = new File(sourceFile);
			if (!file.exists()) {
				return null;
			}
			bookCatalogObjs = ReadXMLFile(sourceFile,namespace);
			if (bookCatalogObjs != null && bookCatalogObjs.size() > 0) {
				createXMLFile(destFile, titalPage, bookCatalogObjs);
			}
		} catch (Exception e) {
			logger.error("NcxCvtXml error:" + e.getMessage());
			return null;
		}
		logger.info("NcxCvtXml successful");
		return bookCatalogObjs;
	}

	public static void main(String[] args) {
		NcxCvtXml("D:/book.ncx", "D:/book.xml", 20,
				"http://www.daisy.org/z3986/2005/ncx/");

	}
}
