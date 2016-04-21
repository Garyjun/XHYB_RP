package com.brainsoon.resrelease.support;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @ClassName: SaxWriter
 * @Description: TODO
 * @author xiehewei
 * @date 2015年7月14日 下午4:11:40
 *
 */
public class SaxWriter {

	public static void main(String[] args) {
		createXMLFile();
	}

	/*public static String createXMLFile() {
		Document doc = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder docBuilder = dbf.newDocumentBuilder();
			doc = docBuilder.newDocument();
			Element country = doc.createElement("country");
			doc.appendChild(country);
			country.appendChild(doc.createTextNode("\n    "));
			Element china = doc.createElement("china");
			country.appendChild(china);
			china.appendChild(doc.createTextNode("\n        "));
			Element city = doc.createElement("city");
			city.appendChild(doc.createTextNode("Beijing"));
			china.appendChild(city);
			china.appendChild(doc.createTextNode("\n        "));
			city = doc.createElement("city");
			city.appendChild(doc.createTextNode("Shanghai"));
			china.appendChild(city);
			china.appendChild(doc.createTextNode("\n    "));
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			File file = new File("C:\\Users\\root\\Desktop\\person.xml");
			FileOutputStream out = new FileOutputStream(file);
			StreamResult xmlResult = new StreamResult(out);
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(new DOMSource(doc), xmlResult);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return "success";
	}*/
	
	
	public static String createXMLFile() {
		Document doc = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder docBuilder = dbf.newDocumentBuilder();
			doc = docBuilder.newDocument();
			Element list = doc.createElement("list");
			doc.appendChild(list);
			list.appendChild(doc.createTextNode("\n    "));
			
			Element id = doc.createElement("id");
			id.appendChild(doc.createTextNode("Beijing"));
			list.appendChild(id);
			list.appendChild(doc.createTextNode("\n    "));
			
			
			Element resource = doc.createElement("resource");
			list.appendChild(resource);
			resource.appendChild(doc.createTextNode("\n        "));
			
			Element resId = doc.createElement("id");
			resId.appendChild(doc.createTextNode("Beijing"));
			resource.appendChild(resId);
			resource.appendChild(doc.createTextNode("\n        "));
			Element resName = doc.createElement("name");
			resName.appendChild(doc.createTextNode("Shanghai"));
			resource.appendChild(resName);
			resource.appendChild(doc.createTextNode("\n        "));
			Element metadata = doc.createElement("metadata");
			metadata.appendChild(doc.createTextNode("Shanghai"));
			resource.appendChild(metadata);
			resource.appendChild(doc.createTextNode("\n        "));
			
			Element fileEle = doc.createElement("file");
			resource.appendChild(fileEle);
			resource.appendChild(doc.createTextNode("\n    "));
			fileEle.appendChild(doc.createTextNode("\n        	"));
			Element fileId = doc.createElement("id");
			fileId.appendChild(doc.createTextNode("Beijing"));
			fileEle.appendChild(fileId);
			fileEle.appendChild(doc.createTextNode("\n     		"));
			
			Element filePath = doc.createElement("path");
			filePath.appendChild(doc.createTextNode("Beijing"));
			fileEle.appendChild(filePath);
			fileEle.appendChild(doc.createTextNode("\n     		"));
			
			Element fileMd5Sign = doc.createElement("md5Sign");
			fileMd5Sign.appendChild(doc.createTextNode("Beijing"));
			fileEle.appendChild(fileMd5Sign);
			fileEle.appendChild(doc.createTextNode("\n     	"));
			
			
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			File file = new File("C:\\Users\\root\\Desktop\\person12.xml");
			FileOutputStream out = new FileOutputStream(file);
			StreamResult xmlResult = new StreamResult(out);
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(new DOMSource(doc), xmlResult);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return "success";
	}
}
