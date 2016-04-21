package com.brainsoon.rp.model.annotation;

/**
 * 
 * <dl>
 * <dt>Range.java</dt>
 * <dd>Function: 批注文本区域信息</dd>
 * <dd>Description:{详细描述该类的功能}</dd>
 * <dd>Copyright: Copyright (C) 2010</dd>
 * <dd>Company: 北京博云易迅技术有限公司</dd>
 * <dd>CreateUser: xujie</dd>
 * <dd>CreateDate: 2016年3月6日</dd>
 * </dl>
 */
public class Range {

	// 批注文本的开始标记（xpath）
	private String start;
	// 批注文本的开始标记（xpath）
	private String end;
	// 开始偏移量
	private int startOffset;
	// 结束偏移量
	private int endOffset;

	public void setStart(String start) {
		this.start = start;
	}

	public String getStart() {
		return this.start;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public String getEnd() {
		return this.end;
	}

	public void setStartOffset(int startOffset) {
		this.startOffset = startOffset;
	}

	public int getStartOffset() {
		return this.startOffset;
	}

	public void setEndOffset(int endOffset) {
		this.endOffset = endOffset;
	}

	public int getEndOffset() {
		return this.endOffset;
	}

}