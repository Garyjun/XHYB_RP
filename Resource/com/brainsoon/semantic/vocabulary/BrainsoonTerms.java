package com.brainsoon.semantic.vocabulary;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class BrainsoonTerms {
	private static Model m_model = ModelFactory.createDefaultModel();
	
	public static final String BS_NS = "http://www.brainsoon.com/resource#";
	
	public static String getNameSpace() {
		return BS_NS;
	}
	
	protected static final String toURI(String local) {
		return BS_NS + local;
	}
	
	public static String getUriPrefix() {
		return "urn:";
	}
	
	public static final Resource NAMESPACE = m_model.createResource( BS_NS );
	
	public static final Property Domain = m_model.createProperty(toURI("Domain"));
	
	public static final Property ResNode = m_model.createProperty(toURI("ResNode"));
	
	public static final Property Resource = m_model.createProperty(toURI("Resource"));
	
	public static final Property CA = m_model.createProperty(toURI("CA"));
	
	public static final Property MetaData = m_model.createProperty(toURI("MetaData"));
	
	public static final Property Book = m_model.createProperty(toURI("Book"));
	
	public static final Property Organization = m_model.createProperty(toURI("Og"));
	
	public static final Property item = m_model.createProperty(toURI("item"));
	
	public static final Property Asset = m_model.createProperty(toURI("Asset"));
	
	public static final Property CommonMeta = m_model.createProperty(toURI("CommonMeta"));
	
	public static final Property ExtendMeta = m_model.createProperty(toURI("ExtendMeta"));
	
	public static final Property CopyrightMeta = m_model.createProperty(toURI("CopyrightMeta"));
	
	public static final Property File = m_model.createProperty(toURI("File"));
	
	public static final Property broaderCode = m_model.createProperty(toURI("broaderCode"));
	
	public static final Property xPath = m_model.createProperty(toURI("xPath"));
	
	public static final Property broaderName = m_model.createProperty(toURI("broaderName"));
	
	public static final Property broaderNode = m_model.createProperty(toURI("broaderNode"));
	
	public static final Property type = m_model.createProperty(toURI("type"));
	
	public static final Property code = m_model.createProperty(toURI("code"));
	
	public static final Property pid = m_model.createProperty(toURI("pid"));
	
	public static final Property pCode = m_model.createProperty(toURI("pCode"));
	
	public static final Property version = m_model.createProperty(toURI("version"));
	
	public static final Property nodeId = m_model.createProperty(toURI("nodeId"));
	
	public static final Property nodeType = m_model.createProperty(toURI("nodeType"));
	
	public static final Property level = m_model.createProperty(toURI("level"));
	
	public static final Property filePath = m_model.createProperty(toURI("filePath"));
	
	public static final Property md5 = m_model.createProperty(toURI("md5"));
	
	public static final Property name = m_model.createProperty(toURI("name"));
	
	public static final Property path = m_model.createProperty(toURI("path"));
	
	public static final Property alias = m_model.createProperty(toURI("alias"));
	
	public static final Property order = m_model.createProperty(toURI("order"));
	
	public static final Property referClass = m_model.createProperty(toURI("referClass"));
	
	public static final Property BaseCF = m_model.createProperty(toURI("BaseCF"));
	
	public static final Property SecondCF = m_model.createProperty(toURI("SecondCF"));
	
	public static final Property Node = m_model.createProperty(toURI("Node"));
	
	public static final Property CProperty = m_model.createProperty(toURI("CProperty"));
	
	public static final Property li = m_model.createProperty(toURI("li"));
	
	public static final Property importXpath = m_model.createProperty(toURI("importXpath"));
	
	public static final Property importXpathName = m_model.createProperty(toURI("importXpathName"));
	
	public static final Property knowledgeXpath = m_model.createProperty(toURI("knowledgeXpath"));
	
	public static final Property knowledgeXpathName = m_model.createProperty(toURI("knowledgeXpathName"));
	
	public static void main(String[] args) {
			
	}
	
}
