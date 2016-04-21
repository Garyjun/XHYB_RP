package com.brainsoon.common.po.tree;

public class TreeNode implements java.io.Serializable{
	private String id;// id
	private String name;// 名称
	private String pId;// 父id
	private boolean checked;// 是否选中
	private boolean open;// 是否打开
	private boolean isParent;
	private String code;
	private String nodeType;
	private String type;
	private int level = 0;
	private String xpath;
	private String objectId;

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getXpath() {
		return xpath;
	}

	public void setXpath(String xpath) {
		this.xpath = xpath;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getName() {
		return name;
	}

	public TreeNode setName(String name) {
		this.name = name;
		return this;
	}

	public boolean isChecked() {
		return checked;
	}

	public TreeNode setChecked(boolean checked) {
		this.checked = checked;
		return this;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public boolean isParent() {
		return isParent;
	}

	public void setParent(boolean isParent) {
		this.isParent = isParent;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getpId() {
		return pId;
	}

	public void setpId(String pId) {
		this.pId = pId;
	}
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
