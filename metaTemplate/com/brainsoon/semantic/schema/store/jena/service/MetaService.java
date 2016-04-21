package com.brainsoon.semantic.schema.store.jena.service;

import java.util.List;

import com.brainsoon.semantic.schema.ontology.CustomMetaData;
import com.brainsoon.semantic.schema.ontology.MetadataDefinition;
import com.brainsoon.semantic.schema.store.jena.OntologyTemplate;
import com.brainsoon.semantic.schema.vocabulary.MetaTerms;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;

public class MetaService {

	public static void saveBaseMetaSchemas(List<CustomMetaData> customMetaDatas) {
		OntModel ontModel = getOntologyTemplate().getOntModel();
		try {
			boolean metadataNull = true;
			for(CustomMetaData customMetaData:customMetaDatas){
				String clName = customMetaData.getName();
				String clsName = customMetaData.getSName();
				String label = customMetaData.getNameCN();
				String classType = customMetaData.getClassType();		
				OntClass ontClass = getOntologyTemplate().createClass(ontModel, clName, label, MetaTerms.getResource(classType));
				List<MetadataDefinition> metadatas = customMetaData.getCustomPropertys();
				if(metadatas!=null && metadatas.size()>0 ){
					for(MetadataDefinition metadata : metadatas) {
						getOntologyTemplate().createProperty(ontModel, ontClass, clsName, metadata);
					}
					metadataNull = false;
				}
			}
			ontModel.write(System.out, "RDF/XML-ABBREV");
			if(!metadataNull){
				getOntologyTemplate().save(ontModel);
			}
		} finally {
			ontModel.close();
	    }
		
	 }
	
	public static CustomMetaData getBaseMetaSchemas(String className) {
		OntModel ontModel = getOntologyTemplate().getOntModel();
		getOntologyTemplate().load(ontModel);
		CustomMetaData cm;
		try {
			cm = getOntologyTemplate().getClass(ontModel, className);			
		} finally {
			ontModel.close();
	    }
		return cm;
	 }
	
	private static OntologyTemplate getOntologyTemplate() {
		return OntologyTemplate.getInstance();
	}


}
