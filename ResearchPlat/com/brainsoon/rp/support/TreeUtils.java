package com.brainsoon.rp.support;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

/**
 * <dl>
 * <dt>TreeUtils.java</dt>
 * <dd>Function: {!!一句话描述功能}</dd>
 * <dd>Description:{详细描述该类的功能}</dd>
 * <dd>Copyright: Copyright (C) 2010</dd>
 * <dd>Company: 北京博云易迅技术有限公司</dd>
 * <dd>CreateUser: xujie</dd>
 * <dd>CreateDate: 2016年3月10日</dd>
 * </dl>
 */
public class TreeUtils {

	private static Node document = null;

	/**
	 * 根据不同的节点类型展开节点
	 * 
	 * @return
	 */
	public static List<TreeNode> populateNodes(TreeNode treeNode) {
		List<TreeNode> treeNodeList = new ArrayList<TreeNode>();
		if (treeNode.isParent()) {
			Element elParent = (Element) document.selectSingleNode("//Node[@id='" + StringUtils.substringBeforeLast(treeNode.getId(), "-") + "']");
			if (!elParent.selectNodes("Node").isEmpty()) { // "Node"节点下有"Node"，则生成这些节点
				ArrayList<?> nodeList = (ArrayList<?>) elParent.selectNodes("Node");// 根据父节点中存储的id，取得对应配置节点，然后确定需要生成哪些子节点
				for (int i = 0; i < nodeList.size(); i++) {
					Element e = (Element) nodeList.get(i);
					if (e.attributeValue("type").equals("ManageNode_Label")) {// 生成简单标签节点
						populateLabelNode(treeNodeList, treeNode, e);
					} else if (e.attributeValue("type").equals("ManageNode_Enum") || e.attributeValue("type").equals("ManageNode_SimpleNumList")) {// 生成枚举节点和简单数字列表节点（目前，后者涵盖了前者）
						populateSimpleNumListNode(treeNodeList, treeNode, e);
					} else if (e.attributeValue("type").equals("ManageNode_RecentDateList")) {// 生成最近日期列表节点
						populateRecentDateListNode(treeNodeList, treeNode, e);
					} else if (e.attributeValue("type").equals("ManageNode_SimpleSQLList")) {// 生成简单关系数据库查询列表节点
						populateSimpleSQLListNode(treeNodeList, treeNode, e);
					}
				}
			}
			if (!elParent.selectNodes("ApplyNodeTemplate").isEmpty()) { // 如果生成的子节点包括了可复用的“ApplyNodeTemplate”，则将NodeList需要的参数（包括静态值、父节点取值和祖父节点取值）传递下去
				ArrayList<?> nll = (ArrayList<?>) elParent.selectNodes("ApplyNodeTemplate");
				for (int i = 0; i < nll.size(); i++) {
					Element el = (Element) nll.get(i);
					String par = "";
					if (el.attributeValue("par") != null) {
						par = el.attributeValue("par");
						if (par.indexOf("../../@value") >= 0)
							par = par.replace("../../@value", (String) (treeNode.getpValue()));
						if (par.indexOf("../@value") >= 0)
							par = par.replace("../@value", (String) (treeNode.getValue()));
					}
					populateNodeList(treeNodeList, treeNode, el, par);
				}
			}
		}
		return treeNodeList;
	}

	/**
	 * 展开可复用的节点列表NodeList
	 * 
	 * @param treeNodeList
	 * 
	 * @param tiParent
	 * @param eNL
	 * @param par
	 */
	private static void populateNodeList(List<TreeNode> treeNodeList, TreeNode tiParent, Element eNL, String par) {
		Element elNLD = (Element) document.selectSingleNode("//NodeTemplate[@name='" + eNL.attributeValue("name") + "']");
		if (elNLD != null && !elNLD.selectNodes("Node").isEmpty()) {
			ArrayList<?> nl = (ArrayList<?>) elNLD.selectNodes("Node");
			for (int i = 0; i < nl.size(); i++) {
				Element el = (Element) nl.get(i);

				if (el.attributeValue("type").equals("ManageNode_SimpleSQLList")) {
					String SQL = el.attributeValue("sql").replace("@par", par);
					String[] value_type = el.attributeValue("value_type").split(",");
					Connection conn = Utils.getConnection();
					try {
						PreparedStatement stmt = conn.prepareStatement(SQL);
						ResultSet rs = stmt.executeQuery();
						while (rs.next()) {
							TreeNode ti = new TreeNode().setParent(tiParent);

							ti.setLabel(el.attributeValue("label"));
							ti.setName(el.attributeValue("name"));
							ti.setValue(el.attributeValue("value"));
							ti.setRsType(Utils.StringNull2Empty(el.attributeValue("resource_type")));// 记录资源类型
							ti.setRsId(Utils.StringNull2Empty(el.attributeValue("resource_id")));// 记录资源标识

							for (int j = 0; j < value_type.length; j++) {
								String value = "";
								if (value_type[j].equalsIgnoreCase("Long"))
									value = String.valueOf(rs.getLong(j + 1));
								else if (value_type[j].equalsIgnoreCase("String"))
									value = String.valueOf(rs.getString(j + 1));
								else if (value_type[j].equalsIgnoreCase("Int"))
									value = String.valueOf(rs.getInt(j + 1));
								ti.setLabel(ti.getLabel().replace("@value[" + (j + 1) + "]", value));
								ti.setName(((String) ti.getName()).replace("@value[" + (j + 1) + "]", value));
								ti.setValue(((String) ti.getValue()).replace("@value[" + (j + 1) + "]", value));
								ti.setRsId(((String) ti.getRsId()).replace("@value[" + (j + 1) + "]", value));
							}
							ti.setId(el.attributeValue("id"));
							ti.setType(el.attributeValue("type"));
							if (el.selectNodes("Node").size() > 0 || el.selectNodes("ApplyNodeTemplate").size() > 0) {
								ti.setIsParent(true);
							}
							treeNodeList.add(ti);
						}
						stmt.close();

					} catch (SQLException e) {
						System.err.println("SQL：" + SQL);
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * 展开简单的关系数据库SQL查询节点，即属性“type”是“ManageNode_SimpleSQLList”的节点
	 * 
	 * @param treeNodeList
	 */
	private static void populateSimpleSQLListNode(List<TreeNode> treeNodeList, TreeNode tiParent, Element el) {
		String SQL = el.attributeValue("sql");

		if (SQL.indexOf("../../@value") >= 0)
			SQL = SQL.replace("../../@value", (String) (tiParent.getParent().getValue()));
		if (SQL.indexOf("../@value") >= 0)
			SQL = SQL.replace("../@value", (String) (tiParent.getValue()));

		String[] value_type = el.attributeValue("value_type").split(",");
		Connection conn = Utils.getConnection();
		try {
			PreparedStatement stmt = conn.prepareStatement(SQL);
			ResultSet rs = stmt.executeQuery();
			int num = 1;
			while (rs.next()) {
				TreeNode node = new TreeNode().setParent(tiParent);
				node.setLabel(el.attributeValue("label"));
				node.setName(el.attributeValue("name"));
				node.setValue(el.attributeValue("value"));
				node.setRsType(Utils.StringNull2Empty(el.attributeValue("resource_type")));// 记录资源类型
				node.setRsId(Utils.StringNull2Empty(el.attributeValue("resource_id")));// 记录资源标识

				for (int j = 0; j < value_type.length; j++) {
					String value = "";
					if (value_type[j].equalsIgnoreCase("Long"))
						value = String.valueOf(rs.getLong(j + 1));
					else if (value_type[j].equalsIgnoreCase("String"))
						value = String.valueOf(rs.getString(j + 1));
					else if (value_type[j].equalsIgnoreCase("Int"))
						value = String.valueOf(rs.getInt(j + 1));
					node.setLabel(node.getLabel().replace("@value[" + (j + 1) + "]", value));
					node.setName(((String) node.getName()).replace("@value[" + (j + 1) + "]", value));
					node.setValue(((String) node.getValue()).replace("@value[" + (j + 1) + "]", value));
					node.setRsId(((String) node.getRsId()).replace("@value[" + (j + 1) + "]", value));

				}
				node.setId(el.attributeValue("id").trim() + "-" + num++);
				node.setType(el.attributeValue("type"));
				node.setPid(tiParent.getId());
				node.setParent(tiParent);
				if (el.selectNodes("Node").size() > 0 || el.selectNodes("ApplyNodeTemplate").size() > 0) {
					node.setIsParent(true);
				}
				treeNodeList.add(node);
			}
			stmt.close();
		} catch (SQLException e) {
			System.err.println("SQL：" + SQL);
			e.printStackTrace();
		}
	}

	/**
	 * 展开最近日期列表节点，即属性“type”是“ManageNode_RecentDateList”的节点
	 * 
	 * @param treeNodeList
	 */
	private static void populateRecentDateListNode(List<TreeNode> treeNodeList, TreeNode tiParent, Element el) {
		Integer value = Integer.valueOf(el.attributeValue("value")); // 从“今天”向前数几天

		String[] values = new String[value];// 生成对应天数的列表
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		for (int i = 0; i < value; i++) { // 从“今天”向前倒数，设置列表中的每项，如果定义了日期格式（属性“format”），则为列表中的每项设定格式
			c.add(Calendar.DAY_OF_YEAR, -1);
			SimpleDateFormat fmt = null;
			if (el.attributeValue("format") != null) {
				fmt = new SimpleDateFormat(el.attributeValue("format"));
				values[i] = fmt.format(c.getTime());
			} else
				values[i] = c.toString();
		}
		for (int j = 0; j < values.length; j++) {// 逐个生成列表中的日期对应的节点
			TreeNode node = new TreeNode().setParent(tiParent);

			node.setLabel(el.attributeValue("label").replace("@format", values[j])); // 将标签（“@label”）中对格式化日期的引用（“@format”）替换成格式化后的日期
			if (node.getLabel().indexOf("../../@value") >= 0) // 替换标签中可能有的对父节点和祖父节点的引用
				node.setLabel(node.getLabel().replace("../../@value", (String) (tiParent.getParent().getValue())));
			if (node.getLabel().indexOf("../@value") >= 0)
				node.setLabel(node.getLabel().replace("../@value", (String) (tiParent.getValue())));
			// increment id
			node.setId(el.attributeValue("id").trim() + "-" + j);
			// add the pid prop for web client
			node.setPid(el.getParent().attributeValue("id").trim());
			node.setType(el.attributeValue("type"));
			// 将名称（“@name”）中对取值的引用（“@value”）替换成实际值
			node.setName(el.attributeValue("name").replace("@value", values[j]));
			node.setValue(values[j]);
			if (el.selectNodes("Node").size() > 0 || el.selectNodes("ApplyNodeTemplate").size() > 0) {
				node.setIsParent(true);
			}
			treeNodeList.add(node);
		}
	}

	/**
	 * 展开最简单的标签节点，即属性“type”是“ManageNode_Label”的节点， 此类节点只做显示，用于组织树形视图的静态结构
	 */
	private static void populateLabelNode(List<TreeNode> treeNodeList, TreeNode parentNode, Element e) {
		TreeNode node = new TreeNode().setParent(parentNode);
		node.setId(e.attributeValue("id").trim());
		node.setType(e.attributeValue("type").trim());
		node.setName(e.attributeValue("name").trim());
		node.setLabel(e.attributeValue("label").trim());
		node.setPid(parentNode.getId());
		if (e.attribute("value") != null) {
			node.setValue(e.attributeValue("value").trim());
		}

		if (e.selectNodes("Node").size() > 0 || e.selectNodes("ApplyNodeTemplate").size() > 0) {
			node.setParent(parentNode).setIsParent(true);
		}
		treeNodeList.add(node);
	}

	/**
	 * 展开简单的数字列表节点，即属性“type”是“ManageNode_SimpleNumList”的节点
	 * 
	 * @param treeNodeList
	 */
	private static void populateSimpleNumListNode(List<TreeNode> treeNodeList, TreeNode tiParent, Element el) {
		// 计算列表中有哪些数字
		String[] values = Utils.calculateSimpleNumList(el.attributeValue("value"));
		// 为列表中的每个数字替换其取值（“value”属性）中可能包含的父节点和祖父节点的值（“value”属性）
		for (int j = 0; j < values.length; j++) {
			if (values[j].indexOf("../../@value") >= 0)
				values[j] = values[j].replace("../../@value", (String) (tiParent.getParent().getValue()));
			if (values[j].indexOf("../@value") >= 0)
				values[j] = values[j].replace("../@value", (String) (tiParent.getValue()));
		}

		for (int j = 0; j < values.length; j++) { // 逐个生成列表中的数字对应的节点
			TreeNode node = new TreeNode().setParent(tiParent);
			node.setLabel(el.attributeValue("label").replace("@value", values[j])); // 将标签（“@label”）中对取值的引用（“@value”）替换成实际值
			node.setId(el.attributeValue("id") + "-" + j);// 记录id
			node.setType(el.attributeValue("type"));// 记录类型
			node.setName(el.attributeValue("name").replace("@value", values[j]));// 将名称（“@name”）中对取值的引用（“@value”）替换成实际值
			node.setValue(values[j]);// 记录实际值
			node.setParent(tiParent);
			if (el.selectNodes("Node").size() > 0 || el.selectNodes("ApplyNodeTemplate").size() > 0) {
				node.setIsParent(true);
			}
			treeNodeList.add(node);
		}
	}

	public Node getNodeTreeRoot() {
		return document;
	}

	public void setNodeTreeRoot(Node nodeTreeRoot) {
		document = nodeTreeRoot;
	}

	/**
	 * @param doc
	 */
	public static void setDocument(Document doc) {
		document = doc;
	}
}
