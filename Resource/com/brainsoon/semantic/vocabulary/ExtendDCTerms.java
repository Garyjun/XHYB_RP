package com.brainsoon.semantic.vocabulary;

import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;


public class ExtendDCTerms {
	private static Model m_model = ModelFactory.createDefaultModel();
	
	private final static Map<String, Property> propertyMap = new HashMap<String, Property>();
	 
	public static final String DC_NS = "http://purl.org/dc/terms/";
	 
	protected static final String toURI(String local) {
		return DC_NS + local;
	}
	
	public static String getNameSpace() {
		return DC_NS;
	}
	
	public static final Resource NAMESPACE = m_model.createResource( DC_NS );
	
	//图片分辨率
	public static final String METADATA_RESOLUTION = "resolution";
	//采样频率
	public static final String METADATA_SAMPLING = "sampling";	
	//采样声道数
	public static final String METADATA_ACOUSTIC_CHANNEL = "acoustic_channel";	
	//播放时间
	public static final String METADATA_DURATION = "duration";
	//规格
	public static final String METADATA_SPECIFICATION = "specification";
	//帧数
	public static final String METADATA_FRAME_COUNT = "frame_count";
	//运行平台
	public static final String METADATA_PLATFORM = "platform";
	//运行要求
	public static final String METADATA_REQUIREMENT = "requirement";
	//软件版本
	public static final String METADATA_SOFTWARE_VERSION = "software_version";
	//教学类型
	public static final String METADATA_TEACHING_TYPE = "teaching_type";
	//音频素材数量
	public static final String METADATA_AUDIO_COUNT = "audio_count";
	//视频素材数量
	public static final String METADATA_VIDEO_COUNT = "video_count";
	//动画文件数量
	public static final String METADATA_ANIMATION_COUNT = "animation_count";
	//页面数量
	public static final String METADATA_PAGE_COUNT = "page_count";
	//课程制作版本
	public static final String METADATA_EDITION = "edition";	
	//评分标准
	public static final String METADATA_CRITERION = "criterion";
	//建议的考试时间
	public static final String METADATA_TYPICAL_TESTING_TIME = "typical_testing_time";	
	//建议的考试得分
	public static final String METADATA_SCORE = "score";	
	//审订人
	public static final String METADATA_AUDITOR = "auditor";	
	//审订日期
	public static final String METADATA_AUDIT_DATE = "audit_date";	
	//实测难度
	public static final String METADATA_DIFFICULTY = "difficulty";
	//实测区分度
	public static final String METADATA_DIFFERENTIATE = "differentiate";
	//访问链接
	public static final String METADATA_LOCATION = "location";
	//阅读消费方式
	public static final String METADATA_VIEW_TYPE = "view_type";	
	//阅读消费价格
	public static final String METADATA_VIEW_VALUE = "view_value";	
	//下载消费方式
	public static final String METADATA_DOWN_TYPE = "down_type";
	//下载消费价格
	public static final String METADATA_DOWN_VALUE = "down_value";
	//购买价格
	public static final String METADATA_PURCHASE_PRICE = "purchase_price";
	
	//图书元数据
	
	//封面
	public static final String METADATA_COVER = "cover";	
	//书名拼音
	public static final String METADATA_ALPHABETTITLE = "alphabetTitle";
	//交替题名
	public static final String METADATA_ALTTITLE = "altTitle";
	//其他题名
	public static final String METADATA_OTHERTITLE = "otherTitle";
	//并列题名
	public static final String METADATA_PARTITLE = "parTitle";
	//丛书名称
	public static final String METADATA_SERIALNAME = "serialname";
	//中图分类号
	public static final String METADATA_CBCLASS = "cbclass";
	//页数
	public static final String METADATA_PAGES = "pages";
	//CIP核字号
	public static final String METADATA_CIP = "cip";
	//纸质图书价格
	public static final String METADATA_PPRICE = "pprice";
	//印次
	public static final String METADATA_PRTCNT = "prtcnt";
	//册号
	public static final String METADATA_VOLNUM = "volnum";
	//版次
	public static final String METADATA_EDICNT = "edicnt";
	//著作方式
	public static final String METADATA_DCTYPE = "dctype";
	//ISBN号
	public static final String METADATA_ISBN = "ISBN";
	//丛书作者姓名
	public static final String METADATA_CLB = "clb";
	//主题词
	public static final String METADATA_DCSUBJECT = "dcsubject";
	//出版社
	public static final String METADATA_DCPUBLISHER = "dcpublisher";
	//出版时间
	public static final String METADATA_PUBLISHDATE = "publishdate";
	
	static {  
		registerProperty(METADATA_RESOLUTION);
		registerProperty(METADATA_SAMPLING);
		registerProperty(METADATA_ACOUSTIC_CHANNEL);
		registerProperty(METADATA_DURATION);
		registerProperty(METADATA_SPECIFICATION);
		registerProperty(METADATA_FRAME_COUNT);
		registerProperty(METADATA_PLATFORM);
		registerProperty(METADATA_REQUIREMENT);
		registerProperty(METADATA_SOFTWARE_VERSION);
		registerProperty(METADATA_TEACHING_TYPE);
		registerProperty(METADATA_AUDIO_COUNT);
		registerProperty(METADATA_VIDEO_COUNT);
		registerProperty(METADATA_ANIMATION_COUNT);
		registerProperty(METADATA_PAGE_COUNT);
		registerProperty(METADATA_EDITION);
		registerProperty(METADATA_CRITERION);
		registerProperty(METADATA_TYPICAL_TESTING_TIME);
		registerProperty(METADATA_SCORE);
		registerProperty(METADATA_AUDITOR);
		registerProperty(METADATA_AUDIT_DATE);
		registerProperty(METADATA_DIFFICULTY);
		registerProperty(METADATA_DIFFERENTIATE);
		registerProperty(METADATA_LOCATION);
		registerProperty(METADATA_VIEW_TYPE);
		registerProperty(METADATA_VIEW_VALUE);
		registerProperty(METADATA_DOWN_TYPE);
		registerProperty(METADATA_DOWN_VALUE);
		registerProperty(METADATA_COVER);
		registerProperty(METADATA_ALPHABETTITLE);
		registerProperty(METADATA_ALTTITLE);
		registerProperty(METADATA_OTHERTITLE);
		registerProperty(METADATA_PARTITLE);
		registerProperty(METADATA_SERIALNAME);
		registerProperty(METADATA_CBCLASS);
		registerProperty(METADATA_PAGES);		
		registerProperty(METADATA_CIP);
		registerProperty(METADATA_PPRICE);
		registerProperty(METADATA_PRTCNT);
		registerProperty(METADATA_VOLNUM);
		registerProperty(METADATA_EDICNT);
		registerProperty(METADATA_DCTYPE);
		registerProperty(METADATA_ISBN);
		registerProperty(METADATA_CLB);
		registerProperty(METADATA_DCSUBJECT);
		registerProperty(METADATA_DCPUBLISHER);
		registerProperty(METADATA_PUBLISHDATE);
		registerProperty(METADATA_PURCHASE_PRICE);
	}
	
	public static Property getProperty(String name) {
		return propertyMap.get(name);
	}
	
	public static boolean existProperty(String name) {
		return propertyMap.containsKey(name);
	}
	
	public static void registerProperty(String name) {
		Property property = m_model.createProperty(toURI(name));
		
		propertyMap.put(name, property);		
	}
	
	public static void main(String[] args) {
		
	}
}
