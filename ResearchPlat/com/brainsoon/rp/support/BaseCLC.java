package com.brainsoon.rp.support;

/*
 * Chinese Library Classification
 * 中国图书馆分类法——中图法
 * Version 待定
 */
public class BaseCLC extends CommonCategory {
	private static BaseCLC clc = null;

	// private CommonCategory category = new CommonCategory();

	public static BaseCLC getInstance() {
		if (clc == null) {
			clc = new BaseCLC();
		}
		return clc;
	}

}
