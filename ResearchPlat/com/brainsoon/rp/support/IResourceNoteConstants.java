package com.brainsoon.rp.support;

/**
 * <dl>
 * <dt>IResourceNoteConstants.java</dt>
 * <dd>Function: {!!一句话描述功能}</dd>
 * <dd>Description:{详细描述该类的功能}</dd>
 * <dd>Copyright: Copyright (C) 2010</dd>
 * <dd>Company: 北京博云易迅技术有限公司</dd>
 * <dd>CreateUser: xujie</dd>
 * <dd>CreateDate: 2016年3月10日</dd>
 * </dl>
 */
public interface IResourceNoteConstants {
	public int NOTE_TYPE_TEXT = 1;

	public int NOTE_ON_FILE = 0;
	public int NOTE_ON_IMAGE = 1;
	public int NOTE_ON_PARAGRAPH = 2;
	public int NOTE_ON_JOURNAL_ISSUE = 10;
	public int NOTE_ON_JOURNAL_ARTICLE = 11;
	public int NOTE_ON_JOURNAL_FIGURE = 12;
	public int NOTE_ON_EVENTENTRY = 20;

	public int NOTE_ON_JNLART_TITLE = 1101;
	public int NOTE_ON_JNLART_AUTHOR = 1102;
	public int NOTE_ON_JNLART_SORT = 1103;
	public int NOTE_ON_JNLART_WEBSITECAT = 1104;
	public int NOTE_ON_JNLART_SOURCE = 1105;
	public int NOTE_ON_JNLART_CONTENT = 1106;

	public int NOTE_ON_EVENTENTRY_CATEGORY = 2001;
	public int NOTE_ON_EVENTENTRY_DATEDESC = 2002;
	public int NOTE_ON_EVENTENTRY_SOURCE = 2003;
	public int NOTE_ON_EVENTENTRY_EVENT = 2004;

	public int NOTE_LOCATION_TYPE_WHOLE_RESOURCE = 0;
	public int NOTE_LOCATION_TYPE_TEXT_OFFSET = 1;
}
