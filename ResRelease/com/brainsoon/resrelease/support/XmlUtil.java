package com.brainsoon.resrelease.support;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.brainsoon.common.util.date.DateUtil;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.resrelease.po.ResFileRelation;
import com.brainsoon.resrelease.po.ResOrder;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.CaList;


public class XmlUtil {
	
	private static Logger log = Logger.getLogger(XmlUtil.class);
	
	/**
	 * 生成发布的xml文件
	 * @param resOrder
	 * @param caList
	 * @param publishDir
	 * @return
	 */
	public static String createResourceListXMLFile(ResOrder resOrder, CaList caList,String publishDir,Map<String, List<String>> map,Map<String,String> fileMap) {
		log.info("开始生成需求单资源清单xml");
		Document doc = null;
		FileOutputStream out = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder docBuilder = dbf.newDocumentBuilder();
			doc = docBuilder.newDocument();
			Element list = doc.createElement("list");
			doc.appendChild(list);
			list.appendChild(doc.createTextNode("\n    "));
			
			Element id = doc.createElement("id");
			Long resOrderId = resOrder.getOrderId();
			id.appendChild(doc.createTextNode("" + resOrderId)); //需求单id
			list.appendChild(id);
			list.appendChild(doc.createTextNode("\n    "));
			List<Ca> calListStr = caList.getCas();
			if(calListStr != null && calListStr.size() > 0){
				for(Ca ca: calListStr){
					Element resource = doc.createElement("resource");
					list.appendChild(resource);
					resource.appendChild(doc.createTextNode("\n        "));
					
					Element resId = doc.createElement("id");
					String resIdDesc = ca.getObjectId();
					resId.appendChild(doc.createTextNode("" + resIdDesc));
					resource.appendChild(resId);
					resource.appendChild(doc.createTextNode("\n        "));
					Element resName = doc.createElement("name");
					String resNameDesc = ca.getMetadataMap().get("title");
					resName.appendChild(doc.createTextNode("" + resNameDesc));
					resource.appendChild(resName);
					resource.appendChild(doc.createTextNode("\n        "));
					Element metadata = doc.createElement("metadata");
					metadata.appendChild(doc.createTextNode("metadata"));
					resource.appendChild(metadata);
					resource.appendChild(doc.createTextNode("\n        "));
					List<FileBo> fileList = ProcessUtil.getResIdList(ca, map);
					if(fileList != null && fileList.size() > 0){
						for (FileBo fileBo : fileList) {
							String fileIdDesc = fileBo.getFileId();
							String publishBaseDir = (fileBo.getId()).replaceAll("\\\\", "\\/");
						   //如果加过水印，则使用fileMap中的文件路径去找文件
						   if(fileMap != null && fileMap.size() > 0 && fileMap.get(fileBo.getFileId()) != null){
								//获取拷贝后的文件绝对地址
								String publishFilePath = fileMap.get(fileBo.getFileId()).replaceAll("\\\\", "\\/");
								String srcPath = (publishDir + fileBo.getResName() + "/").replaceAll("\\\\", "\\/"); //源文件绝对路径
								publishBaseDir = publishFilePath.substring(srcPath.length(), publishFilePath.length());
							}
							Element fileEle = doc.createElement("file");
							resource.appendChild(fileEle);
							resource.appendChild(doc.createTextNode("\n    "));
							fileEle.appendChild(doc.createTextNode("\n        	"));
							Element fileId = doc.createElement("id");
							fileId.appendChild(doc.createTextNode("" + fileIdDesc));
							fileEle.appendChild(fileId);
							fileEle.appendChild(doc.createTextNode("\n     		"));
							
							Element filePath = doc.createElement("path");
							filePath.appendChild(doc.createTextNode("" + publishBaseDir));
							fileEle.appendChild(filePath);
							fileEle.appendChild(doc.createTextNode("\n     		"));
							
							String md5Sign = fileBo.getMd5();
							if(StringUtils.isNotEmpty(md5Sign)){
								Element fileMd5Sign = doc.createElement("md5Sign");
								fileMd5Sign.appendChild(doc.createTextNode("" + md5Sign));
								fileEle.appendChild(fileMd5Sign);
							}
							fileEle.appendChild(doc.createTextNode("\n     	"));
						}
					}
				}
			}
			
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			File fileDir = new File(publishDir);
			if(!fileDir.exists()){
				fileDir.mkdirs();
			}
			File file = new File(publishDir + "资源文件清单.xml");
			out = new FileOutputStream(file);
			StreamResult xmlResult = new StreamResult(out);
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(new DOMSource(doc), xmlResult);
		} catch (ParserConfigurationException e) {
			log.info("开始生成需求单资源清单xml失败");
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			log.info("开始生成需求单资源清单xml失败");
			e.printStackTrace();
		} catch (TransformerException e) {
			log.info("开始生成需求单资源清单xml失败");
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			log.info("开始生成需求单资源清单xml失败");
			e.printStackTrace();
		} finally{
			if(out != null){
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return "success";
	}
	
    public static void main(String[] args) {    
    	/*List<WraperObj> list = new ArrayList<WraperObj>();
    	WraperObj ojb = new WraperObj();
    	ojb.setId("id111");
    	Obj o = new Obj();
    	o.setId("ww");
    	o.setMetadata("metadata");
    	o.setName("name");
    	List<File> fileList = new ArrayList<File>();
    	File file = new File();
    	//file.setMd5("md5Sign");
    	file.setPath("path");
    	file.setId("fileid");
    	File file1 = new File();
    	file1.setMd5("md5Sign1");
    	file1.setPath("path1");
    	file1.setId("fileid1");
    	fileList.add(file);
    	fileList.add(file1);
    	o.setFile(fileList);
    	
    	Obj o1 = new Obj();
    	o1.setId("ww");
    	o1.setMetadata("metadata");
    	o1.setName("name");
    	List<File> fileList1 = new ArrayList<File>();
    	File file2 = new File();
    	file2.setMd5("md5Sign");
    	file2.setPath("path");
    	file2.setId("fileid");
    	File file3 = new File();
    	file3.setMd5("md5Sign1");
    	file3.setPath("path1");
    	file3.setId("fileid1");
    	fileList1.add(file2);
    	fileList1.add(file3);
    	o1.setFile(fileList1);
    	
    	List<Obj> objList = new ArrayList<Obj>();
    	
    	objList.add(o);
    	objList.add(o1);
    	ojb.setResource(objList);
    	list.add(ojb);
    	createResultXml("C:\\Users\\root\\Desktop\\test\\we\\df1.xml", list, "需求单id");*/
    }    
}
