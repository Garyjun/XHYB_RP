package test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.brainsoon.semantic.schema.ontology.CustomMetaData;
import com.brainsoon.semantic.schema.ontology.MetadataDefinition;
import com.brainsoon.semantic.schema.store.jena.OntologyTemplate;
import com.brainsoon.semantic.schema.store.jena.service.MetaService;
import com.hp.hpl.jena.ontology.OntModel;

public class OntologyTestT {

	public static void main(String... argv) {
		OntModel ontModel = OntologyTemplate.getInstance().getOntModel();
		CustomMetaData cm = new CustomMetaData();
		cm.setName("CommonMetaData");
		cm.setNameCN("通用元数据");
		cm.setClassType("BaseCF");
		MetadataDefinition meta = new MetadataDefinition();
		meta = new MetadataDefinition();
		meta.setFieldName("title");
		meta.setFieldType(1);
		meta.setAllowNull(1);
		meta.setShowField("0");
	//	meta.setValidateModel(3);
		meta.setQueryModel(2);
		//meta.setValueLength(",5");
		meta.setOrderNum(1);
		meta.setGroupId("17");
		meta.setFieldZhName("标题") ;
		meta.setDuplicateCheck("1");
		cm.addCustomProperty(meta);
		
	    meta = new MetadataDefinition();
	    meta.setFieldName("isbn");
		meta.setFieldType(1);
		meta.setAllowNull(1);
		meta.setShowField("0");
	//	meta.setValidateModel(1);
		meta.setValueLength(",15");
		meta.setQueryModel(2);
		meta.setOrderNum(1);
		meta.setFieldZhName("Doi");
		meta.setDuplicateCheck("1");
		meta.setIdentifier(5);
		cm.setName("CommonMetaData");
		cm.setNameCN("通用元数据");
		cm.setClassType("BaseCF");
		cm.addCustomProperty(meta);
		
//	    meta = new MetadataDefinition();
//	    meta.setFieldName("target");
//		meta.setFieldType(1);
//		meta.setAllowNull(1);
//		meta.setShowField("0");
//		meta.setValidateModel(1);
//		meta.setValueLength(",15");
//		meta.setQueryModel(2);
//		meta.setOrderNum(1);
//		meta.setFieldZhName("标签") ;
//		meta.setDuplicateCheck("1");
//		meta.setIdentifier(10);
		
//	    meta = new MetadataDefinition();
//	    meta.setFieldName("doi");
//		meta.setFieldType(1);
//		meta.setAllowNull(1);
//		meta.setShowField("0");
//		meta.setValidateModel(1);
//		meta.setValueLength(",15");
//		meta.setQueryModel(2);
//		meta.setOrderNum(1);
//		meta.setFieldZhName("Doi") ;
//		meta.setDuplicateCheck("1");
//		meta.setIdentifier(10);
			
		  meta = new MetadataDefinition();
		    meta.setFieldName("res_type");
			meta.setFieldType(2);
			meta.setAllowNull(1);
	//		meta.setValidateModel(1);
			meta.setShowField("1");
			meta.setQueryModel(2);
			meta.setOrderNum(1);
			meta.setValueRange("中文,英文,其他");
			meta.setFieldZhName("语种") ;
			cm.setName("CommonMetaData");
			cm.setNameCN("通用元数据");
			cm.setClassType("BaseCF");
			cm.addCustomProperty(meta);
			
			meta = new MetadataDefinition();
		    meta.setFieldName("publishdate");
			meta.setFieldType(7);
			meta.setAllowNull(1);
		//	meta.setValidateModel(1);
			meta.setQueryModel(4);
			meta.setOrderNum(1);
			meta.setGroupId("16");
			meta.setFieldZhName("出版日期") ;
			cm.setName("CommonMetaData");
			cm.setNameCN("通用元数据");
			cm.setClassType("BaseCF");
			cm.addCustomProperty(meta);
			
			meta = new MetadataDefinition();
		    meta.setFieldName("source");
			meta.setFieldType(3);
			meta.setAllowNull(1);
		//	meta.setValidateModel(1);
			meta.setQueryModel(2);
			meta.setOrderNum(1);
			meta.setGroupId("17");
			meta.setValueRange("flv,mp4,rmvb");
			meta.setFieldZhName("格式") ;
			cm.setName("CommonMetaData");
			cm.setNameCN("通用元数据");
			cm.setClassType("BaseCF");
			cm.addCustomProperty(meta);
			
			meta = new MetadataDefinition();
		    meta.setFieldName("cbclass");
			meta.setFieldType(6);
			meta.setAllowNull(1);
	//		meta.setValidateModel(1);
			meta.setQueryModel(2);
			meta.setOrderNum(1);
			meta.setValueRange("1");
			meta.setGroupId("16");
			meta.setFieldZhName("中图分类") ;
			cm.setName("CommonMetaData");
			cm.setNameCN("通用元数据");
			cm.setClassType("BaseCF");
			cm.addCustomProperty(meta);
			
			CustomMetaData cm1 = new CustomMetaData();
			meta = new MetadataDefinition();
		    meta.setFieldName("review");
			meta.setFieldType(1);
			meta.setAllowNull(1);
	//		meta.setValidateModel(1);
			meta.setQueryModel(2);
			meta.setOrderNum(1);
			meta.setIdentifier(10);
			meta.setValueRange("1");
			meta.setGroupId("16");
			meta.setFieldZhName("标签") ;
			cm1.setName("BaseMetaData");
			cm1.setNameCN("基本元数据");
			cm1.setClassType("CF");
			cm1.addCustomProperty(meta);
			meta = new MetadataDefinition();
		    meta.setFieldName("rating");
			meta.setFieldType(1);
			meta.setAllowNull(1);
	//		meta.setValidateModel(1);
			meta.setQueryModel(2);
			meta.setOrderNum(1);
			meta.setValueRange("1");
			meta.setGroupId("16");
			meta.setFieldZhName("评价等级") ;
			cm1.setName("BaseMetaData");
			cm1.setNameCN("基本元数据");
			cm1.setClassType("CF");
			cm1.addCustomProperty(meta);
		List<CustomMetaData> customMetaDatas = new ArrayList<CustomMetaData>();
		customMetaDatas.add(cm1);
		customMetaDatas.add(cm);
 		MetaService.saveBaseMetaSchemas(customMetaDatas);
		CustomMetaData cm3 = MetaService.getBaseMetaSchemas("BaseMetaData");
		if(cm3!=null){
			System.out.println(cm3.getNameCN());
		}
		CustomMetaData cm4 = MetaService.getBaseMetaSchemas("CommonMetaData");
		if(cm4!=null){
			System.out.println(cm4.getNameCN());
		}
	}
}
