package com.brainsoon.bsrcm.search.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.AbstractUpdateRequest;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrException;
import org.dom4j.DocumentException;

import com.brainsoon.bsrcm.search.client.util.FileUtils;
import com.brainsoon.bsrcm.search.client.util.OSCommandUtil;
import com.brainsoon.bsrcm.search.client.util.XmlParser;
import com.brainsoon.appframe.util.WebappConfigUtil;

/**
 * <dl>
 * <dt>DocumentSearchClient</dt>
 * <dd>Description:���ĵ������ͻ���</dd>
 * <dd>Copyright: Copyright (c) 2011 ���ƿƼ����޹�˾</dd>
 * <dd>Company: ���ƿƼ����޹�˾</dd>
 * <dd>CreateDate: Oct 28, 2011</dd>
 * </dl>
 * 
 * @author ����
 */

/**
 * @author zhangxin
 *
 */
public class DocumentSearchClient {
    private static Logger logger = Logger.getLogger(DocumentSearchClient.class
		  .getName());
  
    private final static Map<String, String> fieldNameTable = new HashMap<String, String>();
  
    private static String solrUrl;     
    
	private static class DocumentSearchClientHolder {
		static DocumentSearchClient instance = new DocumentSearchClient();
	}

	public static DocumentSearchClient buildSingleton() {
		return DocumentSearchClientHolder.instance;
	}
	
	public void setSolrUrl(String solrUrl) {
		DocumentSearchClient.solrUrl = solrUrl;
	}
	
	public static String getSolrUrl() {
		return solrUrl;
	}
		
    public void initialize() {
	    solrUrl = WebappConfigUtil.getParameter("solrUrl");
	    
    	fieldNameTable.put("docId", "id");
	    fieldNameTable.put("bookName", "title");
	    fieldNameTable.put("chapterId", "chapterId");
	    fieldNameTable.put("chapterName", "chapterName");
	    fieldNameTable.put("parentId", "parentId");
	    fieldNameTable.put("author", "author");
	    fieldNameTable.put("publisher", "category");
	    fieldNameTable.put("content", "files");	 
	    fieldNameTable.put("all", "");	
    }
    
    public void createIndex(String filePath) {
    	File file = new File(filePath);
    	if(file.isDirectory())
    	   createMultiFileIndex(filePath);
    	else
    	   createSingleFileIndex(filePath);
    }
    
    public void createMultiFileIndex(String docPath) {
	    List<String> docFiles = FileUtils.getFileListBySubfix(docPath, "pdf");  
	    for(String docFile : docFiles) {
	    	createSingleFileIndex(docFile);
	    }
    }
	
    public void createSingleFileIndex(String docFile) {
//	    String docFileName = FileUtils.getFileName(docFile, ".pdf");
	    String docConfigFile = docFile + ".xml";			  
	    try {
		    org.dom4j.Document doc = XmlParser.loadFile(new FileInputStream(new File(docConfigFile)));			
		    DocumentConfig docConfig = new DocumentConfig(doc);
		    if(docConfig == null)
		       return;				 
		    String docId = docConfig.getResPk();  
		    String bookName = docConfig.getBookName();
		    String author = docConfig.getAuthor();
		    String resType = docConfig.getResType();
		    String type = docConfig.getType();
		    String publishingHouse = docConfig.getPublishingHouse();
		    try {
			  createIndex(docFile, docId, bookName, author, type
					    , resType, publishingHouse);
			  logger.info("���������ɹ����ļ���[" + docFile + "]");
		    } catch (SearchException e) {
			  logger.error("��������ʧ�ܣ��ļ���[" + docFile + "]���쳣[" + e.getMessage() + "]");
		    }				 
	    } catch (FileNotFoundException e) {
		    logger.error("�ĵ������ļ�[" + docConfigFile + "]��ʧ��");
	    } catch (DocumentException e) {
		    logger.error("�ĵ������ļ�[" + docConfigFile +"]��ȡ����");
	    }
    }
    
    public void createSingleChapterIndex(String docFile, Properties properties) {				 
	    String docId = properties.getProperty("bookObjectId");  
	    String bookName = properties.getProperty("bookName");
	    String chapterId = properties.getProperty("chapterObjectId");
	    String chapterName = properties.getProperty("chapterName");
	    String parentId = properties.getProperty("parentId");
	    String author = properties.getProperty("author");
	    String publisher = properties.getProperty("publisher");		    
	    
	    try {
		  createIndex(docFile, docId, bookName, chapterId
				    , chapterName, parentId, author, publisher);
		  logger.info("���������ɹ����ļ���[" + docFile + "]");
	    } catch (SearchException e) {
		  logger.error("��������ʧ�ܣ��ļ���[" + docFile + "]���쳣[" + e.getMessage() + "]");
	    }				 

    }
    
    private void createIndex(String docFile, String docId, String bookName, String chapterId
		    , String chapterName, String parentId, String author, String publisher) throws SearchException {	  			    
        SolrServer solr = new HttpSolrServer(solrUrl);
    
        ContentStreamUpdateRequest up = new ContentStreamUpdateRequest("/update");	      
    
	    try {
			up.addFile(new File(docFile), "application/xml");
		} catch (IOException e) {
			throw new SearchException("���������쳣���ļ���ȡ�쳣���쳣[" + e.getMessage() + "]");
		}
	    
	    up.setParam("literal.id", docId);
	    up.setParam("literal.title", bookName);
	    up.setParam("literal.chapterId", chapterId);
	    up.setParam("literal.chapterName", chapterName);
	    up.setParam("literal.parentId", parentId);
	    up.setParam("literal.author", author);
	    up.setParam("literal.category", publisher);
	    up.setParam("fmap.content", "ikmaxword");
	    
	    up.setAction(AbstractUpdateRequest.ACTION.COMMIT, true, true);
	    
	    try {
			solr.request(up);
		} catch (SolrServerException e) {
			throw new SearchException("���������쳣�����������쳣���쳣[" + e.getMessage() + "]");
		} catch (IOException e) {
			throw new SearchException("���������쳣��ͨѶ�쳣���쳣[" + e.getMessage() + "]");
		}	
    }	
    
    public void createIndex(String docFile, String docId, String bookName, String author, String type
		    , String resType, String publishingHouse) throws SearchException {	  			    
        SolrServer solr = new HttpSolrServer(solrUrl);

		System.out.println("update createIndex................................................................."); 
//        ContentStreamUpdateRequest up = new ContentStreamUpdateRequest("/update/extract"); 
		ContentStreamUpdateRequest up = new ContentStreamUpdateRequest("/update");
        System.out.println("update createIndex2.................................................................");
	    try {
			up.addFile(new File(docFile), "application/xml");
//			upSolrFile(docFile);
		} catch (Exception e) {
			throw new SearchException("���������쳣���ļ���ȡ�쳣���쳣[" + e.getMessage() + "]");
		}
	    
//	    up.setParam("literal.uuid", docId);
//	    up.setParam("literal.title", bookName);
//	    up.setParam("literal.author", author);
//	    up.setParam("literal.publishing", publishingHouse);
//	    up.setParam("literal.filetype", String.valueOf(type));	    
//	    up.setParam("literal.restype", String.valueOf(resType));
//	    up.setParam("fmap.content", "ikmaxword");
	    
	    up.setAction(AbstractUpdateRequest.ACTION.COMMIT, true, true);
	    
	    try {
			solr.request(up);
			
	    } catch (SolrException e) {
	    	e.printStackTrace();
		} catch (SolrServerException e) {
			e.printStackTrace();
			throw new SearchException("���������쳣�����������쳣���쳣[" + e.getMessage() + "]");
		} catch (IOException e) {
			e.printStackTrace();
			throw new SearchException("���������쳣��ͨѶ�쳣���쳣[" + e.getMessage() + "]");
		}	
    }	
	  
    public SearchResult query(String content, String field, int start, int maxrow) throws SearchException {	
    	if (StringUtils.isBlank(content) 
    			|| StringUtils.isBlank(field))
    		return null;
    		
	    SolrServer server = new HttpSolrServer(solrUrl);
	    
		SolrQuery query = new SolrQuery();

		String solrFieldName = getSolrFieldName(field);
		if(StringUtils.isNotBlank(solrFieldName))
		   content = solrFieldName + ":" + content;		
		System.out.println("content====================================" + content);
		String hlField = "*";
		if(!field.equals("all"))
			hlField = getSolrFieldName(field);
		
		query.setQuery(content);			
		query.setHighlight(true);
		query.setParam("fl", "uuid,title,author");
		query.setParam("hl.fl", hlField);
		query.setParam("start", String.valueOf(start));
		query.setParam("rows", String.valueOf(maxrow));		
		
		System.out.println(query.toString());
		QueryResponse res;
		try {
			res = server.query(query);
		} catch (SolrServerException e) {
			throw new SearchException("�����쳣�����������쳣���쳣[" + e.getMessage() + "]");
		}
		
		SearchResult searchResult = parseQueryResponse(res);

        return searchResult;
    }
	
    private SearchResult parseQueryResponse(QueryResponse res) {
		SolrDocumentList docList = res.getResults();
		long totleRow = docList.getNumFound();
		long startRow = docList.getStart();
        long maxRow = docList.size(); 
		
        SearchResult searchResult = new SearchResult();
        searchResult.setTotleRow(totleRow);
        searchResult.setStartRow(startRow);
        searchResult.setMaxRow(maxRow);
        
        Map<String,Map<String,List<String>>> highlightings = res.getHighlighting();
        
        for(SolrDocument solrDoc : docList)
        {
        	 Document doc = new Document();
        	 //solrDoc.get����ֵ������String�����п�����List
        	 String id = (String)solrDoc.get("uuid");	        	 
        	 List<String> docNames = (List<String>)solrDoc.get("title");
        	 List<String> authors = (List<String>)solrDoc.get("author");
        	 List<String> publishingHouses = (List<String>)solrDoc.get("category");
        	 List<String> types = (List<String>)solrDoc.get("subject");
        	 List<String> resTypes = (List<String>)solrDoc.get("links");
        	 Map<String,List<String>> matchList =  highlightings.get(id);
        	 
        	 if(matchList == null)
        		continue;
        	 
        	 String docName = "";
        	 String author = "";
        	 String publishingHouse = "";
        	 String type = "";
        	 String resType = "";
        	 if(docNames != null && docNames.size() > 0) {
        		docName =  docNames.get(0);              
        		if(docNames.size() > 1)
        		   docName =  docNames.get(1);  
        		if(matchList.containsKey("title"))
        		   docName =  matchList.get("title").get(0);
        	 }
        			        	 
        	 if(authors != null && authors.size() > 0) {
        		author =  authors.get(0);
        		if(authors.size() > 1)
        		   author =  authors.get(1);
        		if(matchList.containsKey("author"))
        		   author =  matchList.get("author").get(0);	
        	 }
        		
        	 if(publishingHouses != null && publishingHouses.size() > 0) {
        		publishingHouse = publishingHouses.get(0);
        		if(publishingHouses.size() > 1)
        		   publishingHouse = publishingHouses.get(1);
        		if(matchList.containsKey("category"))
        		   publishingHouse =  matchList.get("category").get(0);	
        	 }
        		
        	 if(types != null && types.size() > 0) {
        		type =  types.get(0);
        		if(types.size() > 1)
        		   type =  types.get(1); 
        	 }
        		
        	 if(resTypes != null && resTypes.size() > 0) {
        		 resType =  resTypes.get(0);
        		 if(resTypes.size() > 1)
        			resType =  resTypes.get(1);  
        	 }                  		         	 
        	  
        	 doc.setId(id);
        	 doc.setDocName(docName);
        	 doc.setAuthor(author);	
        	 doc.setPublishingHouse(publishingHouse);
        	 doc.setType(type);
        	 doc.setResType(resType);        	 
        	       	 
        	 List<String> matchContents = new ArrayList<String>();
        	 if(matchList.containsKey("files"))
        		 matchContents.addAll(matchList.get("files")); 
        	 
        	 doc.addMatchContents(matchContents);   
        	 
        	 searchResult.addDocument(doc);
         }
         return searchResult;
    }
    
    private void upSolrFile(String book) throws IOException, InterruptedException {
    	OSCommandUtil.copyFile(book, "D:/apache-tomcat-6.0.37/book/book.xml");
    } 
    private String getSolrFieldName(String localName) {
    	return fieldNameTable.get(localName);
    }
    
    public static void main(String[] args) {		  
	    try {	      
	      DocumentSearchClient d = DocumentSearchClient.buildSingleton();
	      d.initialize();
	      d.setSolrUrl("http://10.130.29.26:11009/solr/core5");
//	      d.setSolrUrl("http://localhost:8080/solr/core5");
//	      d.setSolrUrl("http://10.130.29.14:8080/solr");
	      //d.createMultiFileIndex("E:\\ͼ��\\upload");
	      Properties properties = new Properties();
	      properties.put("bookObjectId", "1");
	      properties.put("bookName", "�������ʺ");
//	      properties.put("chapterObjectId", "urn:book-d0166f87-914b-448a-9c97-6afc3719d362.chapter-1e7ce06a-83da-4318-8dbc-52f51e93b29d");
//	      properties.put("chapterName", "�峯");
//	      properties.put("parentId", "urn:book-d0166f87-914b-448a-9c97-6afc3719d362");
	      properties.put("author", "����");
	      properties.put("publisher", "����������");
//	      d.createSingleChapterIndex("D:/zx/��ý��epub����ؿ���/������/��Դ�ļ�/xxxxx/pdf/pages/page00010.txt", properties);
//	      String book = "E:/temp/���������һ��_Mark.xml";
	      String book = "D:/xsm-source-multimedia-java-maven-new/semantic_knowledge_server/temp/��������·�������.xml";
	      d.createIndex(book, null, null, null, null
	  		    , null, null);
	      
	      System.out.println("here.................................................................");
//	      String field = "files";
	      String field = "all";
	      String content = "�Ƴ�";
	      int start = 0;
	      int totle = 10;
//	      SearchResult sr = d.query(content, field, start, totle);
//	      
//	      List<Document> lds = sr.getDocuments();	      
//	      for(Document doc : lds) {
//	    	  System.out.println("1=============================" + doc.getMatchContents());
//	      }	      
	    } catch (Exception ex) {
	      System.out.println(ex.toString());
	    }
    }
}
