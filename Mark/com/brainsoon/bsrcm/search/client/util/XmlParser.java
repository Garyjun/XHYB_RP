package com.brainsoon.bsrcm.search.client.util;

import java.io.InputStream;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class XmlParser {

	public static Document loadFile(InputStream input) throws DocumentException
	{
		if(input == null)
		{
			throw new NullPointerException("InputStream is NULL.");
		}
		
		SAXReader reader = new SAXReader();
		return reader.read(input);
	}
	
	public static String queryString(Document document, String xpath)
	{
		if(document == null)
		{
			throw new NullPointerException("Document is NULL.");
		}
		
		Node node = document.selectSingleNode(xpath);
		if(node == null)
		{
			return StringUtils.EMPTY;
		}
		return node.getText();
	}
	
	public static void main(String[] args) {
		String config = "E:\\Õº È\\11008409.xml";	

	}
}
