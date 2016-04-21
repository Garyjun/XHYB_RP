package com.brainsoon.statistics.po.vo;

import java.io.Serializable;
import java.util.List;

public class ResultList implements Serializable {
	private static final long serialVersionUID = 6022426027760900277L;

	/**
	 * 返回结果集
	 */
	private List list;

	/**
	 * 资源总记录数
	 */
	private int totalSum;

	public synchronized List getList() {
		return list;
	}

	public synchronized void setList(List list) {
		this.list = list;
	}

	public synchronized int getTotalSum() {
		return totalSum;
	}

	public synchronized void setTotalSum(int totalSum) {
		this.totalSum = totalSum;
	}

}
