package com.brainsoon.common.util.dofile.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.brainsoon.common.util.dofile.view.CatalogDTO;

/**
 * 
 * @ClassName: XmlUtil
 * @Description: XML 解析工具类
 * @author tanghui
 * @date 2014-5-15 上午9:46:16
 * 
 */
public class XmlUtil {

	protected static final Logger logger = Logger.getLogger(DoFileUtils.class);

	/**
	 * 获得Xml 文档对象
	 * 
	 * @param xmlFile
	 *            指向xml 文件的引用
	 * @return xmlDoc 从文件读取xml Document
	 */
	public static Document read(File xmlFile) {
		Document document = null;
		SAXReader saxReader = new SAXReader();
		try {
			document = saxReader.read(xmlFile);
		} catch (DocumentException e) {
			logger.error("通过指向xml文件的文件获得Document对象时出错 !", e);
		}
		return document;
	}

	/**
	 * 通过xml 文件的名字读取Document对象
	 * 
	 * @param xmlFileName
	 * @return Document
	 */
	public static Document read(String xmlFileName) {
		return read(new File(xmlFileName));
	}

	/**
	 * 通过指向xml 文件的URL获得Document对象
	 * 
	 * @param url
	 * @return Document
	 */
	public static Document read(URL url) {
		Document document = null;
		SAXReader saxReader = new SAXReader();
		try {
			document = saxReader.read(url);
		} catch (DocumentException e) {
			logger.error("通过指向xml文件的URL获得Document对象时出错...", e);
		}
		return document;
	}

	/**
	 * 将xml Node 对象转换成 String
	 * 
	 * @param node
	 * @return string
	 */
	public static String NodeToString(Node node) {
		return node.asXML();
	}

	/**
	 * 将字符串解析成Node
	 * 
	 * @param xmlString
	 * @return node
	 */
	public static Node StringToNode(String xmlString) {
		Node node = null;
		try {
			node = DocumentHelper.parseText(xmlString);
		} catch (DocumentException e) {
			logger.error("将字符串解析成doc时出错", e);
		}
		return node;
	}

	/**
	 * 根据给定路径查询给定 xml 元素的子元素
	 * 
	 * @param parent
	 *            父节点元素
	 * @param childPath
	 *            相对与父节点的子节点路径,路径组成部分之间用"/"分割,支持通配符号"*"
	 * @return child 子节点元素
	 */
	public static Element child(Element parent, String childPath) {
		String names[] = childPath.split("/");
		Element child = parent;
		for (String name : names) {
			if (name.equals("*")) {
				child = (Element) child.elements().get(0);
			} else {
				child = child.element(name);
			}
			if (child == null) {
				logger.debug("未找到指定元素[" + name + "],返回null...");
			}
		}
		return child;
	}

	/**
	 * 根据给定路径查询给定 xml 元素的子元素
	 * 
	 * @param parent
	 *            父节点元素
	 * @param childPath
	 *            相对与父节点的子节点路径,路径组成部分之间用"/"分割,支持通配符号"*"
	 * @param index
	 *            子节点在兄弟列表中的位置
	 * @return child 子节点元素
	 */
	public static Element child(Element parent, String childPath, int index) {
		String names[] = childPath.split("/");
		Element child = parent;
		for (String name : names) {
			if (name.equals("*")) {
				child = (Element) child.elements().get(index);
			} else {
				child = child.element(name);
			}
			if (child == null) {
				logger.debug("未找到指定元素[" + name + "],返回null...");
			}
		}
		return child;
	}

	/**
	 * 查询父节点的子节点的属性值
	 * 
	 * @param parent
	 *            父节点
	 * @param attributePath
	 *            子节点属性相对于父节点的路径,路径各组成部分用"/"分割, 属性名称前要添加"@"符号 支持通配符号"*"
	 * @return 子节点的属性值,如果找不到返回null
	 */
	public static String childAttribute(Element parent, String attributePath) {
		if (attributePath.indexOf('@') == -1)
			throw new IllegalArgumentException("属性查询要使用 '@'");
		int slashLoc = attributePath.lastIndexOf('/');
		String childPath = attributePath.substring(0, slashLoc);
		Element child = child(parent, childPath);
		String attributeName = attributePath.substring(slashLoc + 2);
		String attributeValue = child.attributeValue(attributeName);
		if (null == attributeValue) {
			logger.debug("未找到指定属性[" + attributeName + "],返回null...");
		}
		return attributeValue;
	}

	/**
	 * 根据相对于父节点的子节点路径,查询子节点列表
	 * 
	 * @param parent 父节点
	 * @param childrenPath 子节点路径
	 * @return children 子节点列表
	 */
	@SuppressWarnings("unchecked")
	public static List<Element> children(Element parent, String childrenPath) {
		int slashLoc = childrenPath.lastIndexOf('/');
		Element child = child(parent, childrenPath.substring(0, slashLoc));
		if (child == null)
			return Collections.EMPTY_LIST;
		String childrenName = childrenPath.substring(slashLoc + 1);
		List<Element> children;
		if (childrenName.equals("*")) {
			children = child.elements();
		} else {
			children = child.elements(childrenName);
		}
		return children;
	}

	/**
	 * 创建一个xml 元素
	 * 
	 * @param name
	 *            xml 元素的名称
	 * @param attributes
	 *            元素属性
	 * @param ns
	 *            命名空间
	 * @return
	 */
	public static Element createElement(String name,
			Map<String, String> attributes, Namespace ns) {
		Element element = null;
		if (ns == null) {
			element = DocumentHelper.createElement(name);
		} else {
			element = DocumentHelper.createElement(new QName(name, ns));
		}
		for (String key : attributes.keySet()) {
			element.addAttribute(key, attributes.get(key));
		}
		return element;
	}

	/**
	 * 创建xml 文档
	 * 
	 * @param nsArray
	 *            命名空间数组
	 * @param root
	 * @return
	 */
	public static Document createDocument(Namespace[] nsArray, Element root) {
		Document document = DocumentHelper.createDocument();
		if (root != null) {
			document.add(root);
		}
		if (nsArray != null && nsArray.length > 0) {
			for (Namespace ns : nsArray) {
				document.add(ns);
			}
		}
		return document;
	}

	/**
	 * 将document 对象写入指定的文件
	 * 
	 * @param xml
	 * @param fileName
	 */
	public static void dumpToFile(Node xml, String fileName) {
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("UTF-8");
		try {
			XMLWriter writer = new XMLWriter(new FileWriter(fileName), format);
			writer.write(xml);
			writer.close();
		} catch (IOException e) {
			logger.error("将document 对象写入指定的文件时出现IO错误 !", e);
		}
	}

	/**
	 * 通过XML文件名，来解析XML对象，此函数只针对XML中存在一个bean值的情况
	 * 
	 * @paramfileName XML文件名
	 * @param typebean类名
	 * @return 单个bean对象
	 * @throwsException
	 */
	public static Object parserXMLByFileName(String fileName, Class<?> type)
			throws Exception {
		Class<? extends Object> beanClass = Class.forName(type.getName());
		Object bean = beanClass.newInstance();
		File inputXml = new File(fileName);
		SAXReader saxReader = new SAXReader();
		try {
			Document document = saxReader.read(inputXml);
			Element element = document.getRootElement();
			BeanInfo beanInfo = Introspector.getBeanInfo(type);
			PropertyDescriptor[] propertyDescriptors = beanInfo
					.getPropertyDescriptors();
			for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
				String propertyName = propertyDescriptor.getName();
				for (Iterator<?> iterator = element.elementIterator(); iterator
						.hasNext();) {
					Element employeeElement = (Element) iterator.next();
					if (!propertyName.equals("class")) {
						Method readMethod = propertyDescriptor.getWriteMethod();
						String value = employeeElement
								.elementText(propertyName);
						if (value != null) {
							readMethod.invoke(type, value);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bean;
	}

	/**
	 * 通过XML文件名，来解析XML对象，此函数针对XML中存在一个或者多个bean值的情况
	 * 
	 * @paramfileName XML文件名
	 * @param typebean类名
	 * @return 返回一个List<bean>
	 * @throwsException
	 */
	public static List<Object> parserXMLByFileNameList(String fileName,
			Class<?> type) throws Exception {
		Class<? extends Object> beanClass = Class.forName(type.getName());
		List<Object> beanList = new ArrayList<Object>();
		File inputXml = new File(fileName);
		SAXReader saxReader = new SAXReader();
		try {
			Document document = saxReader.read(inputXml);
			Element element = document.getRootElement();
			BeanInfo beanInfo = Introspector.getBeanInfo(type);
			PropertyDescriptor[] propertyDescriptors = beanInfo
					.getPropertyDescriptors();
			for (Iterator<?> iterator = element.elementIterator(); iterator
					.hasNext();) {
				Object bean = beanClass.newInstance();
				Element employeeElement = (Element) iterator.next();
				for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
					String propertyName = propertyDescriptor.getName();
					if (!propertyName.equals("class")) {
						Method readMethod = propertyDescriptor.getWriteMethod();
						String value = employeeElement
								.elementText(propertyName);
						if (value != null) {
							readMethod.invoke(bean, value);

						}
					}
				}
				beanList.add(bean);
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
		return beanList;
	}

	// test
	public static void main(String[] args) {
		try {
			CatalogDTO ot = (CatalogDTO) parserXMLByFileName(
					"D:\\project35\\BSFW\\WebRoot\\tempfile\\viewer\\pdf\\11\\XiaoShuoXinShang.xml",
					CatalogDTO.class);
			System.out.println(ot.getFileUrl());
		} catch (Exception e) {
			// tanghui Auto-generated catch block
			e.printStackTrace();
		}
	}

}
