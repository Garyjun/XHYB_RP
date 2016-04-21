package com.brainsoon.rp.support;

import java.util.HashMap;
import java.util.Map;

import net.minidev.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 
* @ClassName: SearchParam
* @Description: datatable返回参数封装
* @author huagnjun
* @date 2016年3月23日
*
 */
public class SearchParam {
	//datatable返回参数
	private int draw;//请求次数
	private int start;//开始索引
	private int length;//每页显示条数
	private String searchValue;//搜索词
	private String sortColumn;//排序字段
	private String order;//正序倒序
	
	private String resType;//资源类型
	private String decade;//年代
	private String year;//年份
	private String month;//月份
	private String week;//周
	private String day;//天
	private String treeId;//分类Id
	private String xpath;//分类Xpath
	private String site;//站点
	private String channel;//频道
	private JSONArray columns;
	
	public SearchParam(int draw, int start, int length, String searchValue,
			String sortColumn, String order, String resType, String decade,
			String year, String month, String week, String day, String treeId,
			String xpath, String site, String channel) {
		super();
		this.draw = draw;
		this.start = start;
		this.length = length;
		this.searchValue = searchValue;
		this.sortColumn = sortColumn;
		this.order = order;
		this.resType = resType;
		this.decade = decade;
		this.year = year;
		this.month = month;
		this.week = week;
		this.day = day;
		this.treeId = treeId;
		this.xpath = xpath;
		this.site = site;
		this.channel = channel;
	}
	public SearchParam() {
		super();
	}
	public int getDraw() {
		return draw;
	}
	public void setDraw(int draw) {
		this.draw = draw;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public String getSearchValue() {
		return searchValue;
	}
	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}
	public String getSortColumn() {
		return sortColumn;
	}
	public void setSortColumn(String sortColumn) {
		this.sortColumn = sortColumn;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	public String getResType() {
		return resType;
	}
	public void setResType(String resType) {
		this.resType = resType;
	}
	public String getDecade() {
		return decade;
	}
	public void setDecade(String decade) {
		this.decade = decade;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getWeek() {
		return week;
	}
	public void setWeek(String week) {
		this.week = week;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public String getTreeId() {
		return treeId;
	}
	public void setTreeId(String treeId) {
		this.treeId = treeId;
	}
	public String getXpath() {
		return xpath;
	}
	public void setXpath(String xpath) {
		this.xpath = xpath;
	}
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public JSONArray getColumns() {
		return columns;
	}
	public void setColumns(JSONArray columns) {
		this.columns = columns;
	}
	
}
