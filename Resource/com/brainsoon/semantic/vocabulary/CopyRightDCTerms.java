package com.brainsoon.semantic.vocabulary;

import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;


public class CopyRightDCTerms {
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
	
	//版权类型
	public static final String METADATA_CRTTYPE = "crtType";	
	//版权人
	public static final String METADATA_CRTPERSON = "crtPerson";
	//授权人
	public static final String METADATA_AUTHORIZER = "authorizer";	
	//产品类型
	public static final String METADATA_PRODTYPE = "prodType";
	//授权地区
	public static final String METADATA_AUTHAREA = "authArea";
	//授权渠道
	public static final String METADATA_AUTHCHANNEL = "authChannel";
	//授权时限
	public static final String METADATA_AUTHTIMELIMIT = "authTimeLimit";
	//授权开始时间
	public static final String METADATA_AUTHSTARTDATE = "authStartDate";
	//授权结束时间
	public static final String METADATA_AUTHENDDATE = "authEndDate";
	//授权语言
	public static final String METADATA_AUTHLANGUAGE = "authLanguage";
	//许可权利
	public static final String METADATA_PERMITRIGHT = "permitRight";
	//合作模式
	public static final String METADATA_COLLAPATTERN = "collaPattern";
	//合同编号
	public static final String METADATA_CONTRACTCODE = "contractCode";
	
	static {  
		registerProperty(METADATA_CRTTYPE);
		registerProperty(METADATA_CRTPERSON);
		registerProperty(METADATA_AUTHORIZER);
		registerProperty(METADATA_PRODTYPE);
		registerProperty(METADATA_AUTHAREA);
		registerProperty(METADATA_AUTHCHANNEL);		
		registerProperty(METADATA_AUTHTIMELIMIT);
		registerProperty(METADATA_AUTHSTARTDATE);
		registerProperty(METADATA_AUTHENDDATE);
		registerProperty(METADATA_AUTHLANGUAGE);
		registerProperty(METADATA_PERMITRIGHT);
		registerProperty(METADATA_COLLAPATTERN);
		registerProperty(METADATA_CONTRACTCODE);
	}
	
	public static boolean existProperty(String name) {
		return propertyMap.containsKey(name);
	}
	
	public static Property getProperty(String name) {
		return propertyMap.get(name);
	}
	
	public static void registerProperty(String name) {
		Property property = m_model.createProperty(toURI(name));		
		propertyMap.put(name, property);		
	}
	
	public static void main(String[] args) {
		
	}
}
