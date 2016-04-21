package com.brainsoon.rp.support;

import java.util.ArrayList;
import java.util.List;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

public class TreeNode {

	private String id;
	private String pid;
	private String name;
	private String value;
	private String pValue;
	private String data;
	private String path;
	private String nodeType;
	private String label;
	private TreeNode parent;
	private boolean isParent;
	private String rsType;
	private String rsId;

	public TreeNode() {
	}

	public String getName() {
		return name;
	}

	public TreeNode setName(String name) {
		this.name = name;
		return this;
	}

	public String getPath() {
		return path;
	}

	public TreeNode setPath(String path) {
		this.path = path;
		return this;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getType() {
		return nodeType;
	}

	public TreeNode setType(String type) {
		this.nodeType = type;
		return this;
	}

	public String getData() {
		return data;
	}

	public TreeNode setData(String data) {
		this.data = data;
		return this;
	}

	public String getLabel() {
		return label;
	}

	public TreeNode setLabel(String label) {
		this.label = label;
		return this;
	}

	public boolean isParent() {
		return isParent;
	}

	public TreeNode setIsParent(boolean isParent) {
		this.isParent = isParent;
		return this;
	}

	public String getId() {
		return id;
	}

	public TreeNode setId(String id) {
		this.id = id;
		return this;
	}

	public String getPid() {
		return pid;
	}

	public TreeNode setPid(String pid) {
		this.pid = pid;
		return this;
	}

	public TreeNode getParent() {
		return parent;
	}

	public TreeNode setParent(TreeNode parent) {
		this.parent = parent;
		return this;
	}

	public String getRsType() {
		return rsType;
	}

	public void setRsType(String rsType) {
		this.rsType = rsType;
	}

	public String getRsId() {
		return rsId;
	}

	public void setRsId(String rsId) {
		this.rsId = rsId;
	}

	public String getpValue() {
		return pValue;
	}

	public void setpValue(String pValue) {
		this.pValue = pValue;
	}
}
