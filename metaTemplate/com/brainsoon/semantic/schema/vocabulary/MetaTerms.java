package com.brainsoon.semantic.schema.vocabulary;

import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class MetaTerms {
	private static Model m_model = ModelFactory.createDefaultModel();
	public static final String Meta_NS = "http://www.brainsoon.com/meta/";
	private final static Map<String, Resource> resourceMap = new HashMap<String, Resource>();
	private final static Map<String, Property> propertyMap = new HashMap<String, Property>();
	
	public static String getNameSpace() {
		return Meta_NS;
	}
	protected static final String toURI(String local) {
		return Meta_NS + local;
	}
	public static final Property uri = m_model.createProperty(toURI("url"));
	
	public static final Property fieldName = m_model.createProperty(toURI("fieldName"));
	
	public static final Property fieldZhName = m_model.createProperty(toURI("fieldZhName"));
	
	public static final Property fieldType = m_model.createProperty(toURI("fieldType"));
	
	public static final Property allowNull = m_model.createProperty(toURI("allowNull"));
	
	public static final Property validateModel = m_model.createProperty(toURI("validateModel"));
	
	public static final Property valueRange = m_model.createProperty(toURI("valueRange"));
	
	public static final Property valueLength = m_model.createProperty(toURI("valueLength"));
	
	public static final Property validateFName = m_model.createProperty(toURI("validateFName"));
	
	public static final Property orderNum = m_model.createProperty(toURI("orderNum"));
	
	public static final Property viewPriority = m_model.createProperty(toURI("viewPriority"));
	
	public static final Property queryModel = m_model.createProperty(toURI("queryModel"));
	
	public static final Property identifier = m_model.createProperty(toURI("identifier"));
	
	public static final Property exportLevel = m_model.createProperty(toURI("exportLevel"));
	
	public static final Property description = m_model.createProperty(toURI("description"));
	
	public static final Property openBlur = m_model.createProperty(toURI("openBlur"));
	
	public static final Property readOnly = m_model.createProperty(toURI("readOnly"));
	
	public static final Property defaultValue = m_model.createProperty(toURI("defaultValue"));
	
	public static final Property groupId = m_model.createProperty(toURI("groupId"));
	
	public static final Property resType = m_model.createProperty(toURI("resType"));
	
	public static final Property duplicateCheck = m_model.createProperty(toURI("duplicateCheck"));
	
	public static final Property relatedWords = m_model.createProperty(toURI("relatedWords"));
	
	public static final Property openAutoComple = m_model.createProperty(toURI("openAutoComple"));
	
	public static final Property createIndex = m_model.createProperty(toURI("createIndex"));
	
	public static final Property openQuery = m_model.createProperty(toURI("openQuery"));
	
	public static final Property showField = m_model.createProperty(toURI("showField"));
	
	public static final Property allowAdvancedQuery = m_model.createProperty(toURI("allowAdvancedQuery"));
	
	public static final Property secondSearch = m_model.createProperty(toURI("secondSearch"));
	
	public static final Resource BaseCF = m_model.createResource( toURI("BaseCF") );
	
	public static final Resource CF = m_model.createResource( toURI("CF") );
	
	public static final Resource FileCF = m_model.createResource( toURI("FileCF") );
	
	public static final Resource MetaData = m_model.createResource( toURI("MetaData") );
	
	public static final Resource MetaDataChild = m_model.createResource( toURI("MetaDataChild") );
	

	static {
		registerResource();
	}


	public static void registerProperty() {
		propertyMap.put("fieldName", fieldName);		
		propertyMap.put("fieldZhName", fieldZhName);	
		propertyMap.put("fieldType", fieldType);		
	}
	
	public static void registerResource() {
		resourceMap.put("BaseCF", BaseCF);		
		resourceMap.put("CF", CF);	
		resourceMap.put("MetaData", MetaData);
		resourceMap.put("FileCF", FileCF);
	}
	public static Resource getResource(String name) {
		return resourceMap.get(name);
	}




}
