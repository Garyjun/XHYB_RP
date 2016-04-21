package com.brainsoon.common.util.dofile.view;

import java.util.ArrayList;
import java.util.List;

/**
 * 书目对象，用于遍历ncx
 * 
 * @author zuohl
 * 
 */
public class CatalogDTO {
	private String id;
	private String pId;
	private String name;
	private String fileUrl;
	private String css;
	private String startPage;
	private String endPage;
	private boolean parent;
	private boolean open;
	private String navPage;
	private String navTitel;
	private String level;
	
	
	// 子节点
	private List<CatalogDTO> sons = new ArrayList<CatalogDTO>();
	
	public CatalogDTO() {
	}
	
	
	public CatalogDTO(String id, String pId, String name, boolean parent,
			boolean open) {
		super();
		this.id = id;
		this.pId = pId;
		this.name = name;
		this.parent = parent;
		this.open = open;
	}


	public CatalogDTO(String id, String pId, String name, String fileUrl) {
		super();
		this.id = id;
		this.pId = pId;
		this.name = name;
		this.fileUrl = fileUrl;
	}
	public CatalogDTO(String id, String pId, String name, String fileUrl,
			String css,String level) {
		super();
		this.id = id;
		this.pId = pId;
		this.name = name;
		this.fileUrl = fileUrl;
		this.css = css;
		this.level = level;
	}
	
	public CatalogDTO(String id, String pId, String name, String fileUrl,
			String css, String startPage, String endPage,String level) {
		super();
		this.id = id;
		this.pId = pId;
		this.name = name;
		this.fileUrl = fileUrl;
		this.css = css;
		this.startPage = startPage;
		this.endPage = endPage;
		this.level = level;
	}
	
	
	public String getCss() {
		return css;
	}
	public void setCss(String css) {
		this.css = css;
	}
	public String getStartPage() {
		return startPage;
	}
	public void setStartPage(String startPage) {
		this.startPage = startPage;
	}
	public String getEndPage() {
		return endPage;
	}
	public void setEndPage(String endPage) {
		this.endPage = endPage;
	}
	public List<CatalogDTO> getSons() {
		return sons;
	}
	public void setSons(List<CatalogDTO> sons) {
		this.sons = sons;
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


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	public String getFileUrl() {
		return fileUrl;
	}


	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}


	public boolean isParent() {
		return parent;
	}
	public void setParent(boolean parent) {
		this.parent = parent;
	}
	public boolean isOpen() {
		return open;
	}
	public void setOpen(boolean open) {
		this.open = open;
	}


	public String getNavPage() {
		return navPage;
	}


	public void setNavPage(String navPage) {
		this.navPage = navPage;
	}


	public String getNavTitel() {
		return navTitel;
	}


	public void setNavTitel(String navTitel) {
		this.navTitel = navTitel;
	}


	public String getLevel() {
		return level;
	}


	public void setLevel(String level) {
		this.level = level;
	}
	
	

	
}
