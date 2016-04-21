package com.brainsoon.semantic.vocabulary;

import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class CommonDCTerms {
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
	
	//资源标识
	public static final String METADATA_IDENTIFIER = "identifier";	
	//资源名称
	public static final String METADATA_TITLE = "title";	
	//资源类型
	public static final String METADATA_TYPE = "type";	
	//出版资源类型
	public static final String METADATA_PUBLISHTYPE = "publishType";	
	//资源库类型
	public static final String METADATA_LIBTYPE = "libType";
	//资源状态
	public static final String METADATA_STATUS = "status";
	//语种
	public static final String METADATA_LANGUAGE = "language";
	//资源模块
	public static final String METADATA_MODULE = "module";
	//资源模块
	public static final String METADATA_MODULE_NAME = "moduleName";
	//幼儿园、中小学分类目录
	public static final String METADATA_SUBJECT = "subject";
	//幼儿园、中小学分类目录
	public static final String METADATA_SUBJECT_NAME = "subjectName";
	//教育阶段
	public static final String METADATA_EDUCATIONAL_PHASE = "educational_phase";
	//教育阶段
	public static final String METADATA_EDUCATIONAL_PHASE_NAME = "educational_phase_name";
	//适用年级
	public static final String METADATA_GRADE = "grade";
	//适用年级
	public static final String METADATA_GRADE_NAME = "gradeName";
	//分册
	public static final String METADATA_FASCICULE = "fascicule";
	//分册
	public static final String METADATA_FASCICULE_NAME = "fasciculeName";
	//教材版本
	public static final String METADATA_VERSION = "version";
	//教材版本
	public static final String METADATA_VERSION_NAME = "versionName";
	//单元
	public static final String METADATA_UNIT = "unit";
	//单元名称
	public static final String METADATA_UNIT_NAME = "unitName";
	//知识点
	public static final String METADATA_KNOWLEDGE_POINT = "knowledge_point";
	//知识点名称
	public static final String METADATA_KNOWLEDGE_POINT_NAME = "knowledge_point_name";
	//适用对象
	public static final String METADATA_AUDIENCE = "audience";
	//资源描述
	public static final String METADATA_DESCRIPTION = "description";
	//关键词
	public static final String METADATA_KEYWORDS = "keywords";	
	//难易程度
	public static final String METADATA_DIFFICULTY_LEVEL = "difficulty_level";
	//原作者是否公开
	public static final String METADATA_PUBLIC_OR_NOT = "public_or_not";	
	//平台发布时是否公开
	public static final String METADATA_RELEASE_SCOPE = "release_scope";
	//版权所属
	public static final String METADATA_COPYRIGHT = "copyright";	
	//权限管理
	public static final String METADATA_RIGHTS = "rights";	
	//文件格式
	public static final String METADATA_FORMAT = "format";	
	//文件大小
	public static final String METADATA_FILEBYTE = "fileByte";	
	//发布时间
	public static final String METADATA_PUBLISH_TIME = "publish_time";	
	//更新时间
	public static final String METADATA_MODIFIED_TIME = "modified_time";
	//发布者
	public static final String METADATA_PUBLISHER = "publisher";	
	//所在省、市、地区
	public static final String METADATA_REGION = "region";
	//所在学校
	public static final String METADATA_SCHOOL = "school";	
	//学校地址
	public static final String METADATA_ADDRESS = "address";
	//制作者姓名
	public static final String METADATA_CREATOR = "creator";	
	//其他作者
	public static final String METADATA_CONTRIBUTOR = "contributor";
	//性别
	public static final String METADATA_SEX = "sex";	
	//出生日期
	public static final String METADATA_BIRTHDATE = "birthdate";	
	//邮箱地址
	public static final String METADATA_EMAIL = "email";	
	//电话号码
	public static final String METADATA_CELLPHONE_NUMBER = "cellphone_number";	
	//职称
	public static final String METADATA_PROFESSIONAL_TITLE = "professional_title";
	//评价等级
	public static final String METADATA_RATING = "rating";	
	//评价者
	public static final String METADATA_REVIEWER = "reviewer";
	//评价描述
	public static final String METADATA_REVIEW = "review";	
	//评价时间
	public static final String METADATA_EVALUATED_DATE = "evaluated_date";	
	//资源来源
	public static final String METADATA_SOURCE = "source";
	//创建时间:20140929,用于统计
	public static final String METADATA_CREATE_DATE = "create_date";
	//创建时间:2014-09-29 19:34:11:394
	public static final String METADATA_CREATE_TIME = "create_time";
	//图书的根目录   或者是聚合资源的封面路径
	public static final String METADATA_PATH = "path";
	//资源版本  查重时选择选择生成新版本  版本号加1
	public static final String METADATA_RES_VERSION = "res_version";
	static {
		registerProperty(METADATA_IDENTIFIER);
		registerProperty(METADATA_TITLE);
		registerProperty(METADATA_TYPE);
		registerProperty(METADATA_PUBLISHTYPE);
		registerProperty(METADATA_LIBTYPE);
		registerProperty(METADATA_STATUS);
		registerProperty(METADATA_LANGUAGE);
		registerProperty(METADATA_MODULE);
		registerProperty(METADATA_MODULE_NAME);
		registerProperty(METADATA_SUBJECT);
		registerProperty(METADATA_SUBJECT_NAME);
		registerProperty(METADATA_EDUCATIONAL_PHASE);
		registerProperty(METADATA_EDUCATIONAL_PHASE_NAME);
		registerProperty(METADATA_GRADE);
		registerProperty(METADATA_GRADE_NAME);
		registerProperty(METADATA_FASCICULE);
		registerProperty(METADATA_FASCICULE_NAME);
		registerProperty(METADATA_VERSION);
		registerProperty(METADATA_VERSION_NAME);
		registerProperty(METADATA_UNIT);
		registerProperty(METADATA_UNIT_NAME);
		registerProperty(METADATA_KNOWLEDGE_POINT);
		registerProperty(METADATA_KNOWLEDGE_POINT_NAME);
		registerProperty(METADATA_AUDIENCE);
		registerProperty(METADATA_DESCRIPTION);
		registerProperty(METADATA_KEYWORDS);
		registerProperty(METADATA_DIFFICULTY_LEVEL);
		registerProperty(METADATA_PUBLIC_OR_NOT);
		registerProperty(METADATA_RELEASE_SCOPE);
		registerProperty(METADATA_COPYRIGHT);
		registerProperty(METADATA_RIGHTS);
		registerProperty(METADATA_FORMAT);
		registerProperty(METADATA_FILEBYTE);
		registerProperty(METADATA_PUBLISH_TIME);
		registerProperty(METADATA_MODIFIED_TIME);
		registerProperty(METADATA_PUBLISHER);
		registerProperty(METADATA_REGION);
		registerProperty(METADATA_SCHOOL);
		registerProperty(METADATA_ADDRESS);
		registerProperty(METADATA_CREATOR);
		registerProperty(METADATA_CONTRIBUTOR);
		registerProperty(METADATA_SEX);
		registerProperty(METADATA_BIRTHDATE);
		registerProperty(METADATA_EMAIL);
		registerProperty(METADATA_CELLPHONE_NUMBER);
		registerProperty(METADATA_PROFESSIONAL_TITLE);
		registerProperty(METADATA_RATING);
		registerProperty(METADATA_REVIEWER);
		registerProperty(METADATA_REVIEW);
		registerProperty(METADATA_EVALUATED_DATE);
		registerProperty(METADATA_SOURCE);  
		registerProperty(METADATA_CREATE_DATE);
		registerProperty(METADATA_PATH);
		registerProperty(METADATA_RES_VERSION); 
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
}
