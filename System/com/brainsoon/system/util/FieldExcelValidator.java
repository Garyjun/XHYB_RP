package com.brainsoon.system.util;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.semantic.schema.common.SysMetadataDefinitionConstants;
import com.brainsoon.semantic.schema.ontology.MetadataDefinition;
import com.brainsoon.system.model.DictName;
import com.brainsoon.system.model.DictValue;
import com.brainsoon.system.service.IDictNameService;

public class FieldExcelValidator {
	/**
	 * excel批量导入对元数据单个字段的校验
	 * @param metadataDefinitionsMap
	 * @param fieldName
	 * @param fieldValue
	 * @return
	 */
	public static String checkFieldHasPoint(Map<String,MetadataDefinition> metadataDefinitionsMap,String fieldName){
		String resultValue = "";
		MetadataDefinition metadataDefinition = metadataDefinitionsMap.get(fieldName);
		//metadataDefinition为空说明不是自定义元数据，不需要校验
		if(metadataDefinition !=null){  
			if(metadataDefinition.getAllowNull() == 0){
					resultValue += metadataDefinition.getFieldZhName()+"不允许为空;";
			}else{
				resultValue = checkFieldWithValue(metadataDefinition,resultValue);
			}
		}
		return resultValue;
	}
	public static String checkFieldWithValue(MetadataDefinition metadataDefinition,String resultValue){
		int fieldType = metadataDefinition.getFieldType();  //字段类型
		switch (fieldType) {
		case 1:
			resultValue = checkInputOrTextarea(metadataDefinition,resultValue);
			break;
		case 2:
			resultValue = checkSelectOrRadio(metadataDefinition,resultValue);
			break;
		case 3:
			resultValue = checkCheckbox(metadataDefinition,resultValue);
			break;
		case 4:
			resultValue = checkSelectOrRadio(metadataDefinition,resultValue);
			break;
		case 5:
			resultValue = checkInputOrTextarea(metadataDefinition,resultValue);
			break;
		case 6:
			// sb = MetadataUtil.createLookup(sb,
			// metadataDefinition,object,true,basePath);
			break;
		case 7:
			resultValue = checkDateTime(metadataDefinition,resultValue);
			break;
		case 8:
			resultValue = checkURL(metadataDefinition,resultValue);
			break;
		case 9:
			resultValue = checkDict(metadataDefinition,resultValue);
			break;
		}
		return resultValue;
	}
	
	public static String checkSelectOrCheckbox(MetadataDefinition metadataDefinition,String fieldValue,String resultValue){
		String valueRange = metadataDefinition.getValueRange();
		if(valueRange == null || "".equals(valueRange)){
			resultValue+=metadataDefinition.getFieldZhName()+"未定义值范围;";
		}else{
			String[] valueRanges = valueRange.split(",");
			String[] values = fieldValue.split(",");
			int i = 0;
			for(String value:values){
				for(String valueRangeOne:valueRanges){
					if(value.equals(valueRangeOne)){
						i++;
						break;
					}
				}
			}
			if(i!=values.length){
				resultValue+="("+metadataDefinition.getFieldZhName()+")的值只能是["+metadataDefinition.getValueRange()+"]中的数据项;";
			}
		}
		return resultValue;
	}
	
	public static String checkCheckbox(MetadataDefinition metadataDefinition,String resultValue){
				resultValue+="("+metadataDefinition.getFieldZhName()+")的值只能是["+metadataDefinition.getValueRange()+"]中的数据项;";
		return resultValue;
	}
	
	public static String checkSelectOrRadio(MetadataDefinition metadataDefinition,String resultValue){
				resultValue+="("+metadataDefinition.getFieldZhName()+")的值只能是["+metadataDefinition.getValueRange()+"]中的一个数据项;";
		return resultValue;
	}
	
	public static String checkDateTime(MetadataDefinition metadataDefinition,String resultValue){
    		resultValue += "(日期格式）只能是年月日形式如2015/4/16";
		return resultValue;
	}
	public static String checkURL(MetadataDefinition metadataDefinition,String resultValue){
			resultValue += "URL地址只能是以http://开头或https://开头的如http://www.boyunyixun.com;";
		return resultValue;
	}
	public static String checkInputOrTextarea(MetadataDefinition metadataDefinition,String resultValue){
		int fieldType = metadataDefinition.getFieldType();
		String valueLength = metadataDefinition.getValueLength();
		String vLength = "64";
	    if(fieldType ==5){
	    	vLength ="50000";
	    }
	    //取值范围
	    if(StringUtils.isNotBlank(valueLength)){
	    	if(valueLength.indexOf(",")>=0){
	    		vLength = valueLength.substring(1,valueLength.length());
		    	valueLength = "取值范围为：【" + "0-" + vLength + "】位;";
	    	}else{
	    		vLength = valueLength;
		    	valueLength = "取值范围为：【" +  vLength + "】位;";
		    }
	    }else{
	    	valueLength = "取值范围为：【64】位;";
	    }
	    resultValue = valueLength;
	    //long len = DataValidator.howLength(fieldValue);
//		if(DataValidator.vsLength(len+"",vLength)){
//			resultValue += metadataDefinition.getFieldZhName()+valueLength;
//		}else{
			String validateModel = metadataDefinition.getValidateModel();
			if("2".equals(validateModel)){
					resultValue+="必须为"+SysMetadataDefinitionConstants.ValidateModel.getValueByKey(validateModel+"");
			}else if("3".equals(validateModel)){
					resultValue+="必须为"+SysMetadataDefinitionConstants.ValidateModel.getValueByKey(validateModel+"");
			}else if("4".equals(validateModel)){
					resultValue+="必须为"+SysMetadataDefinitionConstants.ValidateModel.getValueByKey(validateModel+"");
			}else if("5".equals(validateModel)){
					resultValue+="必须为"+SysMetadataDefinitionConstants.ValidateModel.getValueByKey(validateModel+"");
			}else if("6".equals(validateModel)){
					resultValue+="必须为"+SysMetadataDefinitionConstants.ValidateModel.getValueByKey(validateModel+"");
			}else if("8".equals(validateModel)){
					resultValue+="必须为合法的ISBN";
			}
//		  }
		return resultValue;
	}
	public static String checkDict(MetadataDefinition metadataDefinition,String resultValue){
		try {
			IDictNameService dictNameService = (IDictNameService)BeanFactoryUtil.getBean("dictNameService");
			String hql = "";
			if(metadataDefinition.getValueRange()!=null){
				hql = "from DictName where indexTag ='"+metadataDefinition.getValueRange()+"'";
			}
			List<DictName> dict = dictNameService.query(hql);
			String dictName = "";
			if(dict.size()>0 && dict.get(0).getValueList().size()>0){
				List<DictValue> dv = dict.get(0).getValueList();
				for(int i=0;i<dv.size();i++){
					if(dv.get(i).getName()!=null){
						dictName = dictName + dv.get(i).getName()+",";
					}
				}
			}
			if(dictName.endsWith(",")){
				dictName = dictName.substring(0,dictName.length()-1);
			}
			resultValue+="("+metadataDefinition.getFieldZhName()+")的值只能是["+dictName+"]中的一个数据项;";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultValue;
		}
	public static void main(String[] args) {

	}

}
