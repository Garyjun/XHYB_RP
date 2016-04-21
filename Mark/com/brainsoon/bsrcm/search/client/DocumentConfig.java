package com.brainsoon.bsrcm.search.client;

import org.dom4j.Document;

import com.brainsoon.bsrcm.search.client.util.XmlParser;


public class DocumentConfig {
	private String resPk;
	private String type;
	private String resType;
	private String bookName;
	private String author;
	private String publishingHouse;	
	
	public DocumentConfig(Document doc)
	{		
    	this.resPk = XmlParser.queryString(doc, "/res/resPk");
    	this.type = XmlParser.queryString(doc, "/res/type");
    	this.resType = XmlParser.queryString(doc, "/res/resType");
    	this.bookName = XmlParser.queryString(doc, "/res/bookName");
    	this.author = XmlParser.queryString(doc, "/res/author");
    	this.publishingHouse = XmlParser.queryString(doc, "/res/publishingHouse");
	}

	public String getResPk()
	{
		return resPk;
	}
	
	public String getType()
	{
		return type;
	}
	
	public String getResType()
	{
		return resType;
	}

	public String getBookName()
	{
		return bookName;
	}

	public String getAuthor()
	{
		return author;
	}

	public String getPublishingHouse()
	{
		return publishingHouse;
	}	
}
