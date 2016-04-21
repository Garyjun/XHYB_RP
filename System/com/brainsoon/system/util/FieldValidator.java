package com.brainsoon.system.util;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.brainsoon.common.util.isbn.ISBNChecker;
import com.brainsoon.semantic.schema.common.SysMetadataDefinitionConstants;
import com.brainsoon.semantic.schema.ontology.MetadataDefinition;

public class FieldValidator {
	/**
	 * excel批量导入对元数据单个字段的校验
	 * @param metadataDefinitionsMap
	 * @param fieldName
	 * @param fieldValue
	 * @return
	 */
	public static String checkFieldHasError(Map<String,MetadataDefinition> metadataDefinitionsMap,String fieldName,String fieldValue){
		String resultValue = "";
		MetadataDefinition metadataDefinition = metadataDefinitionsMap.get(fieldName);
		//metadataDefinition为空说明不是自定义元数据，不需要校验
		if(metadataDefinition !=null){
			if(metadataDefinition.getAllowNull() == 0){
				if(fieldValue == null || "".equals(fieldValue) || StringUtils.isBlank(fieldValue)){
					resultValue += metadataDefinition.getFieldZhName()+"不允许为空;";
				}else{
					resultValue += checkFieldWithValue(metadataDefinition,fieldName,fieldValue,resultValue);
					resultValue += checkValueFormat(metadataDefinition,fieldName,fieldValue);//处理值格式校验
				}
			}else{
				resultValue += checkFieldWithValue(metadataDefinition,fieldName,fieldValue,resultValue);
				resultValue += checkValueFormat(metadataDefinition,fieldName,fieldValue);//处理值格式校验
			}
		}
		return resultValue;
	}
	public static String checkFieldWithValue(MetadataDefinition metadataDefinition,String fieldName,String fieldValue,String resultValue){
		int fieldType = metadataDefinition.getFieldType();  //字段类型
		switch (fieldType) {
		case 1:
			resultValue += checkInputOrTextarea(metadataDefinition,fieldName,fieldValue,resultValue);
			break;
		case 2://前面已做校验
			//resultValue += checkSelectOrRadio(metadataDefinition,fieldName,fieldValue,resultValue);
			break;
		case 3://前面已做校验
			//resultValue += checkCheckbox(metadataDefinition,fieldName,fieldValue,resultValue);
			break;
		case 4://前面已做校验
			//resultValue += checkSelectOrRadio(metadataDefinition,fieldName,fieldValue,resultValue);
			break;
		case 5:
			resultValue += checkInputOrTextarea(metadataDefinition,fieldName,fieldValue,resultValue);
			break;
		case 6://前面已做校验
			// sb = MetadataUtil.createLookup(sb,
			// metadataDefinition,object,true,basePath);
			break;
		case 7:
			resultValue += checkDateTime(metadataDefinition,fieldName,fieldValue,resultValue);
			break;
		case 8:
			resultValue += checkURL(metadataDefinition,fieldName,fieldValue,resultValue);
			break;
		case 9://前面已做校验
			break;
		case 10://前面已做校验
			break;
		case 11://前面已做校验
			break;
		}
		return resultValue;
	}
	
	public static String checkSelectOrCheckbox(MetadataDefinition metadataDefinition,String fieldName,String fieldValue,String resultValue){
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
				resultValue+="["+metadataDefinition.getFieldZhName()+"]的值只能是["+metadataDefinition.getValueRange()+"]中的数据项;";
			}
		}
		return resultValue;
	}
	
	public static String checkCheckbox(MetadataDefinition metadataDefinition,String fieldName,String fieldValue,String resultValue){
		String valueRange = metadataDefinition.getValueRange();
		if(!"".equals(fieldValue)){
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
				resultValue+="["+metadataDefinition.getFieldZhName()+"]的值只能是["+metadataDefinition.getValueRange()+"]中的数据项;";
			}
		}
		}else{
			resultValue = "";
		}
		return resultValue;
	}
	
	public static String checkSelectOrRadio(MetadataDefinition metadataDefinition,String fieldName,String fieldValue,String resultValue){
		String valueRange = metadataDefinition.getValueRange();
		if(!"".equals(fieldValue)){
		if(valueRange == null || "".equals(valueRange)){
			resultValue+=metadataDefinition.getFieldZhName()+"未定义值范围;";
		}else{
			if(StringUtils.isNotBlank(fieldValue)){
			String[] valueRanges = valueRange.split(",");
			boolean exist = false;
			for(String valueRangeOne:valueRanges){
				if(fieldValue.equals(valueRangeOne)){
					exist = true;
					break;
				}
			}
			if(!exist){
				resultValue+="["+metadataDefinition.getFieldZhName()+"]的值只能是["+metadataDefinition.getValueRange()+"]中的一个数据项;";
			}
			}
		}
		}else{
			resultValue = "";
		}
		return resultValue;
	}
	
	public static String checkDateTime(MetadataDefinition metadataDefinition,String fieldName,String fieldValue,String resultValue){
		if(!"".equals(fieldValue)){
    	try{
    		DateConverter.formatTo(fieldValue);
    	}catch(Exception e){
    		e.getStackTrace();
    		resultValue += "["+metadataDefinition.getFieldZhName()+"]不是合法的日期类型;";
    	}
		}else{
			resultValue = "";
		}
		return resultValue;
	}
	public static String checkURL(MetadataDefinition metadataDefinition,String fieldName,String fieldValue,String resultValue){
		if(!"".equals(fieldValue)){
		if(!fieldValue.startsWith("http://") || !fieldValue.startsWith("https://")){
			resultValue += metadataDefinition.getFieldZhName()+"不是合法的URL;";
		}
		}else{
			resultValue = "";
		}
		return resultValue;
	}
	public static String checkInputOrTextarea(MetadataDefinition metadataDefinition,String fieldName,String fieldValue,String resultValue){
		if(!"".equals(fieldValue)){
			int fieldType = metadataDefinition.getFieldType();
			String valueLength = metadataDefinition.getValueLength();//模板中定义的长度
//			String vLength = "64";
//			int value =0;
			int lengthMax=64;
			int lengthMin=0;
			/**
			 * 取值范围处理
			 * valueLength	数组		数组长度	描述
			 * 		10	   	[10]	1		小于10	情况一
					5,		[5]		1		大于5		情况二
					,10		[, 10]	2		小于10	情况三
				   5,10		[5, 10]	2		大于5小于10	情况四
			 */
			long len = DataValidator.howLength(fieldValue);//填入字符的长度
			if (fieldType ==5) {
				lengthMax =50000; 
			}else if(StringUtils.isNotBlank(valueLength)){
				if(valueLength.indexOf(",")>=0){
					String LengthMaxMin[] = valueLength.split(",");
					if (LengthMaxMin.length==2) {
						if (LengthMaxMin[0].equals("")) {//情况三
							lengthMax = Integer.parseInt(LengthMaxMin[1]);
							lengthMin = 0;
						}else {//情况四
							lengthMax = Integer.parseInt(LengthMaxMin[1]);
							lengthMin = Integer.parseInt(LengthMaxMin[0]);
						}
					}else {//情况二
						lengthMin = Integer.parseInt(valueLength);
					}
				}else {//情况一
					lengthMax = Integer.parseInt(valueLength);
				}
				
			}
			if (len<lengthMin || len>lengthMax) {
				resultValue += "["+metadataDefinition.getFieldZhName()+"]"+"取值范围为：【" + lengthMin +"-"+ lengthMax + "】位;";
			}
				
		    
			/*//取值范围
		    if(StringUtils.isNotBlank(valueLength)){
		    	if(valueLength.indexOf(",")>=0){
		    		String vL[] = valueLength.split(",");
		    		if(StringUtils.isNotBlank(vL[0])){
		    			vLength = valueLength.substring(2,valueLength.length());
		    			value = Integer.parseInt(vLength);
		    		}else{
		    			
//		    			vLength = valueLength.substring(1,valueLength.length());
		    			value = Integer.parseInt(vLength);
		    		}
			    	valueLength = "取值范围为：【" + "0-" + vLength + "】位;";
		    	}else{
		    		vLength = valueLength;
			    	valueLength = "取值范围为：【" +  vLength + "】位;";
			    }
		    }else{
		    	valueLength = "取值范围为：【64】位;";
		    }
		    long len = DataValidator.howLength(fieldValue);
		    fieldName = metadataDefinition.getFieldZhName();
		    String validateModel = "";
			if(DataValidator.vsLength(len+"",value+"")){
				resultValue += "["+metadataDefinition.getFieldZhName()+"]"+valueLength;
			}
			*/
		}else {
			resultValue = "";
		}

		return resultValue;
	}
	
	
	
	public static String checkValueFormat(MetadataDefinition metadataDefinition,String fieldName,String fieldValue){
		String resultValue = "";
		String validateModel = metadataDefinition.getValidateModel();
		fieldName = metadataDefinition.getFieldZhName();
		if("2".equals(validateModel)){
			if(!DataValidator.isNum(fieldValue)){
					resultValue+="["+fieldName+"]"+"必须为"+SysMetadataDefinitionConstants.ValidateModel.getValueByKey(validateModel+"");
    		}
		}else if("3".equals(validateModel)){
			if(!DataValidator.isLetter(fieldValue)){
					resultValue+="["+fieldName+"]"+"必须为"+SysMetadataDefinitionConstants.ValidateModel.getValueByKey(validateModel+"");
    		}
		}else if("4".equals(validateModel)){
			if(!DataValidator.isLetterAndNum(fieldValue)){
					resultValue+="["+fieldName+"]"+"必须为"+SysMetadataDefinitionConstants.ValidateModel.getValueByKey(validateModel+"");
    		}
		}else if("5".equals(validateModel)){
			if(!DataValidator.isChinese(fieldValue)){
					resultValue+="["+fieldName+"]"+"必须为"+SysMetadataDefinitionConstants.ValidateModel.getValueByKey(validateModel+"");
    		}
		}else if("6".equals(validateModel)){
			if(!DataValidator.mailbox(fieldValue)){
				resultValue+="["+fieldName+"]"+"必须为"+SysMetadataDefinitionConstants.ValidateModel.getValueByKey(validateModel+"");
    		}
		}else if("8".equals(validateModel)){
			try {
				ISBNChecker good = new ISBNChecker(fieldValue);
			} catch (Exception e) {
				resultValue+="ISBN输入值【"+fieldValue+"】不符合规范 错误信息："+e.getMessage()+",";
			}
		}
		return resultValue;
	}
	public static void main(String[] args) {

	}

}
