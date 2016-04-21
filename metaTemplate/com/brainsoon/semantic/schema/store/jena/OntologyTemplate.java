package com.brainsoon.semantic.schema.store.jena;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.semantic.schema.ontology.CustomMetaData;
import com.brainsoon.semantic.schema.ontology.MetadataDefinition;
import com.brainsoon.semantic.schema.vocabulary.LOMTerms;
import com.brainsoon.semantic.schema.vocabulary.MetaTerms;
import com.brainsoon.semantic.vocabulary.BrainsoonTerms;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

public class OntologyTemplate {
	private static Logger logger = Logger.getLogger(OntologyTemplate.class.getName());
	
	private static class OntologyTemplateHolder {
		static OntologyTemplate instance = new OntologyTemplate();
	}

	public static OntologyTemplate getInstance() {
		return OntologyTemplateHolder.instance;
	}

	public OntModel getOntModel() {
		OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);		
		return ontModel;
	}	
	
	public OntClass createClass(OntModel ontModel, String className, String label, Resource classType) {
		OntClass cls = ontModel.createClass(LOMTerms.getNameSpace()+className);	
		cls.addLabel(label, "zh");
		cls.addRDFType(OWL.Class);
		cls.addRDFType(classType);
		return cls;
	}	
	//根据名称获得定义的元数据
	public CustomMetaData getClass(OntModel ontModel, String className) {
 		CustomMetaData cm = new CustomMetaData();
		OntClass cls = ontModel.getOntClass(LOMTerms.getNameSpace()+className);
		if(cls == null)
			return null;
		cm.setNameCN(cls.getLabel("zh"));
		cm.setName(className);
		if("CommonMetaData".equals(className)){
			cm.setClassType("BaseCF");
		}else if("FileMetaData".equals(className)){
			cm.setClassType("FileCF");
		}else{
			cm.setClassType("CF");
		}
	//	cm.setClassType(cls.getRDFType().getLocalName());
		ResIterator rs = ontModel.listResourcesWithProperty(RDFS.domain, cls);	
		Statement statement = null;
		while (rs.hasNext()) {
			com.hp.hpl.jena.rdf.model.Resource p = rs.nextResource();
			MetadataDefinition metadata = new MetadataDefinition();
			OntProperty ontProperty = ontModel.getOntProperty(p.toString());
			statement = ontProperty.getProperty(MetaTerms.fieldType);
			if(statement!=null){
				metadata.setFieldType(statement.getObject().asLiteral().getInt());
			}
			statement = ontProperty.getProperty(MetaTerms.allowNull);
			if(statement!=null){
				metadata.setAllowNull(statement.getObject().asLiteral().getInt());
			}
			statement = ontProperty.getProperty(MetaTerms.valueRange);
			if(statement!=null){
				metadata.setValueRange(statement.getObject().asLiteral().getString());
			}
			statement = ontProperty.getProperty(MetaTerms.valueLength);
			if(statement!=null){
				metadata.setValueLength(statement.getObject().asLiteral().getString());
			}
			statement = ontProperty.getProperty(MetaTerms.queryModel);
			if(statement!=null){
				metadata.setQueryModel(statement.getObject().asLiteral().getInt());
			}
			statement = ontProperty.getProperty(MetaTerms.orderNum);
			if(statement!=null){
				metadata.setOrderNum(statement.getObject().asLiteral().getInt());
			}
			statement = ontProperty.getProperty(MetaTerms.validateModel);
			if(statement!=null){
				metadata.setValidateModel(statement.getObject().asLiteral().getString());
			}
			statement = ontProperty.getProperty(MetaTerms.groupId);
			if(statement!=null){
				metadata.setGroupId(statement.getObject().asLiteral().getString());
			}
			statement = ontProperty.getProperty(MetaTerms.duplicateCheck);
			if(statement!=null){
				metadata.setDuplicateCheck(statement.getObject().asLiteral().getString());
			}
			statement = ontProperty.getProperty(MetaTerms.createIndex);
			if(statement!=null){
				metadata.setCreateIndex(statement.getObject().asLiteral().getString());
			}
			statement = ontProperty.getProperty(MetaTerms.openAutoComple);
			if(statement!=null){
				metadata.setOpenAutoComple(statement.getObject().asLiteral().getString());
			}
			statement = ontProperty.getProperty(MetaTerms.resType);
			if(statement!=null){
				metadata.setResType(statement.getObject().asLiteral().getString());
			}
			statement = ontProperty.getProperty(MetaTerms.showField);
			if(statement!=null){
				metadata.setShowField(statement.getObject().asLiteral().getString());
			}
			statement = ontProperty.getProperty(MetaTerms.exportLevel);
			if(statement!=null){
				metadata.setExportLevel(statement.getObject().asLiteral().getString());
			}
			statement = ontProperty.getProperty(MetaTerms.identifier);
			if(statement!=null){
				metadata.setIdentifier(statement.getObject().asLiteral().getInt());
			}
			statement = ontProperty.getProperty(MetaTerms.viewPriority);
			if(statement!=null){
				metadata.setViewPriority(statement.getObject().asLiteral().getString());
			}
			statement = ontProperty.getProperty(MetaTerms.allowAdvancedQuery);
			if(statement!=null){
				metadata.setAllowAdvancedQuery(statement.getObject().asLiteral().getString());
			}
			statement = ontProperty.getProperty(MetaTerms.readOnly);
			if(statement!=null){
				metadata.setReadOnly(statement.getObject().asLiteral().getString());
			}
			statement = ontProperty.getProperty(MetaTerms.uri);
			if(statement!=null){
				metadata.setUri(statement.getObject().asLiteral().getString());
			}
			statement = ontProperty.getProperty(MetaTerms.openQuery);
			if(statement!=null){
				metadata.setOpenQuery(statement.getObject().asLiteral().getString());
			}
			statement = ontProperty.getProperty(MetaTerms.defaultValue);
			if(statement!=null){
				metadata.setDefaultValue(statement.getObject().asLiteral().getString());
			}
//			statement = ontProperty.getProperty(MetaTerms.defaultValue);
//			if(statement!=null){
//				metadata.setDefaultValue(statement.getObject().asLiteral().getString());
//			}
			statement = ontProperty.getProperty(MetaTerms.secondSearch);
			if(statement!=null){
				metadata.setSecondSearch(statement.getObject().asLiteral().getString());
			}
			statement = ontProperty.getProperty(MetaTerms.description);
			if(statement!=null){
				metadata.setDescription(statement.getObject().asLiteral().getString());
			}
			statement = ontProperty.getProperty(MetaTerms.relatedWords);
			if(statement!=null){
				metadata.setRelatedWords(statement.getObject().asLiteral().getString());
			}
		//	metadata.setUri(ontProperty.getURI());
			metadata.setDomain(className);
			OntProperty propertySuper = ontProperty.getSuperProperty();
			if(propertySuper != null) {
				 
			}
			metadata.setFieldZhName(ontProperty.getLabel("zh"));
			metadata.setFieldName(ontProperty.getLabel("en"));
			logger.debug(metadata.getFieldZhName());
			cm.addCustomProperty(metadata);
		}
		
		return cm;
	}	
	public Property createProperty(OntModel ontModel, OntClass cls, String clsName, MetadataDefinition metadata) {		
		OntProperty p = ontModel.createOntProperty( LOMTerms.getNameSpace() + clsName + "/"  + metadata.getFieldName());
		if(metadata.getSuperProperty() != null) {			
			OntProperty p1 = ontModel.getOntProperty(metadata.getSuperProperty());
			if(p1 != null) {
				p.addProperty(RDF.type, MetaTerms.MetaDataChild);
				p.addSuperProperty(p1);
			} else {
				p.addProperty(RDF.type, MetaTerms.MetaData);
			}
			
		} else {
			p.addProperty(RDF.type, MetaTerms.MetaData);
		}
		p.addProperty(RDFS.domain, cls);		
		createPropertyDetail(ontModel, p, metadata);
		
		return p;
	}
	public Property getPropertyDetail(OntProperty p, MetadataDefinition metadata) {

			p.listProperties();

		
		return p;
	}
	public Property createPropertyDetail(OntModel ontModel, OntProperty p, MetadataDefinition metadata) {
		if(metadata.getFieldName()!=null)
			p.addLiteral(RDFS.label, ontModel.createLiteral(metadata.getFieldName(), "en"));	
		if(metadata.getFieldZhName()!=null) 
			p.addLiteral(RDFS.label, ontModel.createLiteral(metadata.getFieldZhName(), "zh"));	
		if(metadata.getFieldType()!=null && metadata.getFieldType() > 0)
			p.addLiteral(MetaTerms.fieldType, metadata.getFieldType());
		if(metadata.getAllowNull()!=null) 
		p.addLiteral(MetaTerms.allowNull, metadata.getAllowNull());
		if(metadata.getValidateModel()!=null)
			p.addLiteral(MetaTerms.validateModel, metadata.getValidateModel());
		if(metadata.getValueRange()!=null)
			p.addLiteral(MetaTerms.valueRange, metadata.getValueRange());
		if(metadata.getValueLength()!=null)
			p.addLiteral(MetaTerms.valueLength, metadata.getValueLength());
		if(metadata.getValidateFName()!=null)
			p.addLiteral(MetaTerms.validateFName, metadata.getValidateFName());
		if(metadata.getOrderNum()!=null && metadata.getOrderNum() > 0)
			p.addLiteral(MetaTerms.orderNum, metadata.getOrderNum());
		if(metadata.getViewPriority()!=null)
			p.addLiteral(MetaTerms.viewPriority, metadata.getViewPriority());
		if(metadata.getQueryModel()!=null && metadata.getQueryModel() > 0)
			p.addLiteral(MetaTerms.queryModel, metadata.getQueryModel());
		if(metadata.getIdentifier()!=null && metadata.getIdentifier() > 0)
			p.addLiteral(MetaTerms.identifier, metadata.getIdentifier());
		if(metadata.getExportLevel()!=null)
			p.addLiteral(MetaTerms.exportLevel, metadata.getExportLevel());
		if(metadata.getDescription()!=null)
			p.addLiteral(MetaTerms.description, metadata.getDescription());
		if(metadata.getGroupId()!=null)
			p.addLiteral(MetaTerms.groupId, metadata.getGroupId());
		if(metadata.getDuplicateCheck()!=null)
			p.addLiteral(MetaTerms.duplicateCheck, metadata.getDuplicateCheck());
		if(metadata.getResType()!=null)
			p.addLiteral(MetaTerms.resType, metadata.getResType());
		if(metadata.getShowField()!=null)
			p.addLiteral(MetaTerms.showField, metadata.getShowField());
		if(metadata.getReadOnly()!=null)
			p.addLiteral(MetaTerms.readOnly, metadata.getReadOnly());
		if(metadata.getAllowAdvancedQuery()!=null)
			p.addLiteral(MetaTerms.allowAdvancedQuery, metadata.getAllowAdvancedQuery());
		if(metadata.getOpenQuery() !=null)
			p.addLiteral(MetaTerms.openQuery, metadata.getOpenQuery());
		if(metadata.getUri()!=null)
			p.addLiteral(MetaTerms.uri, metadata.getUri());
		if(metadata.getDefaultValue()!=null)
			p.addLiteral(MetaTerms.defaultValue, metadata.getDefaultValue());
		if(metadata.getSecondSearch()!=null)
			p.addLiteral(MetaTerms.secondSearch, metadata.getSecondSearch());
		if(metadata.getRelatedWords()!=null)
			p.addLiteral(MetaTerms.relatedWords, metadata.getRelatedWords());
		return p;
	}
	
	public void save(OntModel ontModel) {
		OutputStream modelFile = null;
		try {
			modelFile = getModelOutput();
			ontModel.write(modelFile, "RDF/XML-ABBREV");
		} catch (FileNotFoundException e) {
			
		} finally {
			if(modelFile != null) {
				try {
					modelFile.close();
				} catch (IOException e) {

				}
			}
		}
	}
	
	public void load(OntModel ontModel) {
		File modelFile = new File(getModelFileName());
		logger.info("从服务器加载元数据到内存");
		if(modelFile.exists())
			ontModel.read("file:"+getModelFileName());		
	}
	
	public static OutputStream getModelOutput() throws FileNotFoundException {
		OutputStream output = new FileOutputStream(getModelFileName());
		return output;
	}

	public static String getModelFileName() {
		String fileName = WebAppUtils.getWebAppRoot() + "template/metaTemplate.rdf";	
		logger.info("rrrrrrrrrrrrrrrrrrrrrrrrrrrr============"+fileName);
//		String fileName = "E:/template/metaTemplate.rdf";
		return fileName;
	}
	
}
