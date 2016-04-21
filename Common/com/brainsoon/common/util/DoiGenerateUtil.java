package com.brainsoon.common.util;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;

import com.brainsoon.appframe.util.StringUtil;
import com.brainsoon.common.support.GlobalDataCacheMap;
import com.brainsoon.resource.util.ResUtils;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.schema.ontology.MetadataDefinition;
import com.brainsoon.system.model.SysDoi;
import com.brainsoon.system.util.MetadataSupport;
	/**
	 * DOI生成规则
	 * @author youjw
	 *
	 */

public class DoiGenerateUtil {
	
	/*  td.000103-t.isbn:00000000009877312321-c.010012 */
	public static final Integer FIRSTPARTSYBMOLSIZE=2; //第一部分符号固定长度 ：td的长度
	
	public static final Integer FIRSTPARTVALUESIZE=6; //第一部分值固定长度:如出版社代码 000103的长度   不够ZERO补齐
	
	public static final Integer SECONDPARTSYMBOL1SIZE=1;//第二部分第一个符号固定长度： t的长度
	
	public static final Integer SECONDPARTOPTSHORTSIZE=4;//第二部分用于生成DOI的元数据的简写形式长度：isbn的长度
	
	public static final Integer SECONDPARTOPTVALUESIZE=20;//第二部分用于生成DOI的元数据的简写形式长度：00000000009877312321的长度 不够ZERO补齐
	
	public static final Integer THIRDPARTSYMBOLSIZE=1;//第三部分符号固定长度：c的长度
	
	public static final Integer THIRDPARTVALUE1SIZE=2;//第三部分第一个值固定长度：010012中前两位 01的长度 不够ZERO补齐
	
	public static final Integer THIRDPARTVALUE2SIZE=4;//第三部分第二个值固定长度:010012中后四位0012的长度 不够ZERO补齐
	
	private static final String SEPERATOR="/"; //DOI第二部分中可选值得分隔符 isbn/ssbn ********需要与页面中的分隔符保持一致********
	
	private static final String POINT="."; //DOI中符号和值的点分隔符
	
	private static final String DASH="-"; //DOI中符号和值的横线分隔符：*********建议修改  因为书籍isbn号中 有可能会出现这个符号 会导致不容易解析*******
	
	private static final String ZERO="0"; //DOI用于补全位数的零值:****建议：采用不常用的符号补齐 有可能会出现某个值本身以0开头的 会导致不容易解析;或者限制但凡需要补齐的值 不允许以0开头*****
	
	private static final Logger logger = Logger.getLogger(DoiGenerateUtil.class);
	/**
	 * 调用该方法生成Doi需要的条件:sysResDirectory对象中应该已经设置了类型sysMetadataType的值,且对应配置DOI的各个属性已经存在
	 * @param sysResDirectory
	 * @return 为空或者抛出异常则出错      否则得到正确的DOI值
	 * @throws Exception 
	 */
	public static String generateDoiByResDirectory(Ca ca) throws Exception{
		String doi="";
		logger.info("----------------------------------进入组装sysDoi"+ca.getPublishType()+"-----------------------------------------");
		Long pubType = Long.parseLong(ca.getPublishType());
		SysDoi sysDoi= SysMetaDataUtils.querySysDoiDefinition(pubType);
		logger.info("----------------------------------进入组装sysDoi"+sysDoi+"-----------------------------------------");
		
		String fieldValue="";
		String fieldName="";
		if(sysDoi!=null){
		///////通过以上步骤  
		//连接第一段 firstPartOne:如 td
		if(StringUtils.isNotBlank(sysDoi.getFirstPartOne())){
			fieldValue=sysDoi.getFirstPartOne();
			logger.info("----------------------------------PartOneValue"+fieldValue+"-----------------------------------------");
			doi+=fieldValue;
		}else{
			doi = "";
			return doi;
		}
		if(StringUtils.isNotBlank(sysDoi.getFirstPartTwo())){
			String code = "";
			String impt = "";
			String firstPartValue=sysDoi.getFirstPartTwo();
			if(firstPartValue!=null && !"".equals(firstPartValue)){
				MetadataDefinition metadataDefinition = MetadataSupport.getMetadataDefinitionByName(firstPartValue);
				if(ca.getMetadataMap().get(firstPartValue)!=null){
					if(metadataDefinition!=null && metadataDefinition.getFieldType()==9){
						 boolean zhName = false;
						 if(StringUtils.isNotBlank(ca.getMetadataMap().get(firstPartValue)) && StringUtils.isNotBlank(firstPartValue)){
										if(StringUtils.isNotBlank(ca.getMetadataMap().get(firstPartValue))){
											String arr[] = ca.getMetadataMap().get(firstPartValue).split(",");
											for(int i=0;i<arr.length;i++){
												impt = GlobalDataCacheMap.getChildCodeByIdAndChildValue(metadataDefinition.getValueRange(), arr[i]);
												if(StringUtils.isBlank(impt)){
													impt = GlobalDataCacheMap.getNameValueWithIdByKeyAndChildValue(metadataDefinition.getValueRange(), arr[i]);
												}
												if(StringUtils.isBlank(impt)){
													impt = GlobalDataCacheMap.getChildCodeByCodeAndChildValue(metadataDefinition.getValueRange(), arr[i]);
												}
												if(StringUtils.isNotBlank(impt)){
													code = code + impt+",";
												}else{
													code = "";
													zhName = true;
													break;
												}
											}
											if(zhName){
												return doi = "";
											}
										}
									logger.info("----------------判断数据字典是否符合规范--------------");
							 if(code.endsWith(","))
								 code = code.substring(0,code.length()-1);
						 }
					}else if(metadataDefinition.getFieldType()==1||metadataDefinition.getFieldType()==2||metadataDefinition.getFieldType()==3||metadataDefinition.getFieldType()==4||metadataDefinition.getFieldType()==5){
						String valueRangeArray[] = null;
						if(StringUtils.isNotBlank(metadataDefinition.getValueRange())){
							valueRangeArray = metadataDefinition.getValueRange().split(",");
							for(String val:valueRangeArray){
								if(ca.getMetadataMap().get(firstPartValue).equals(val)){
									code = ca.getMetadataMap().get(firstPartValue);
									break;
								}else{
									code = "";
								}
							}
						}else{
							code = ca.getMetadataMap().get(firstPartValue);
						}
					}
					if(StringUtils.isBlank(code)){
						return doi = "";
					}
					doi+=POINT+code;
				}
//				else{
//					return doi = "";
//				}
				logger.info("----------------------------------firstPartTwo"+firstPartValue+"-------doi----"+doi+"------------------------------");
			}
		}
//		else{
//			doi = "";
//			return doi;
//		}
//		if(StringUtils.isNotBlank(sysDoi.getFirstPartTwo())){
		//连接第一部分3
			String secondPartSymbol=sysDoi.getFirstPartThree();
			if(secondPartSymbol!=null && !"".equals(secondPartSymbol)){
				doi+=sysDoi.getSeparator()+secondPartSymbol;
				logger.info("----------------------------------secondPartSymbol"+secondPartSymbol+"--doi"+doi+"---------------------------------------");
			}
//		}
//		else{
//			doi = "";
//			return doi;
//		}
		//连接第二段 可选值
		fieldValue=chooseSecondPartValue(sysDoi,pubType,ca);
		if(!StringUtil.isEmpty(fieldValue) && fieldValue.startsWith("0")){
			if(fieldValue.length() == 2){
				
			}else{
				logger.info("----------------------------------第二段fieldValue"+fieldValue+"-----------------------------------------");
				doi+=POINT+fieldValue.substring(fieldValue.indexOf("|")+1,fieldValue.length());
			}
		}else{
			doi = "";
			return doi;
		}
		
		//连接第三段 ThirdExtendOne
		if(sysDoi.getThirdExtendOne()!=null && !"".equals(sysDoi.getThirdExtendOne())){
//			SysMetadataDefinition sysMetadataDefinition =SysMetaDataUtils.queryMetadataByFieldEnName(sysDoiDefinition.getThirdPartSymbol(), sysDoiDefinition.getSysMetadataType().getSysMetadataTypeId());
			fieldName = sysDoi.getThirdExtendOne();
			if(!"".equals(fieldName)&&ca.getMetadataMap().get(fieldName)!=null){
				fieldValue=ca.getMetadataMap().get(fieldName);
				logger.info("----------------------------------第三段fieldValueExtendOne  fieldName"+fieldName+"-----------------------------------------");
				logger.info("----------------------------------第三段fieldValueExtendOne"+fieldValue+"-----------------------------------------");
			}
			if(!StringUtil.isEmpty(fieldValue)){
//				if(fieldValue.length()>THIRDPARTVALUE1SIZE){
//					throw new Exception("元数据对象中名称为"+fieldName+"的属性值最长只能为"+THIRDPARTVALUE1SIZE+"位有效位!");
//				}else if(fieldValue.length()<THIRDPARTVALUE1SIZE){
//					fieldValue=fillValueToFixedLength(fieldValue,THIRDPARTVALUE1SIZE);
//					if(StringUtil.isEmpty(fieldValue)){
//						throw new Exception("补齐元数据对象中名称为"+fieldName+"的属性值【"+fieldValue+"为长度"+THIRDPARTVALUE1SIZE+"位失败!");
//					}
//				}
				logger.info("----------------------------------第三段doi"+doi+"----fieldValueExtendOne---"+fieldValue+"----------------------------------");
				doi+=POINT+fieldValue;
			}else{
//				String defaultValue=SysMetaDataUtils.queryMetadataDefauleValueByFieldEnName(sysDoiDefinition.getThirdPartSymbol(), sysDoiDefinition.getSysMetadataType().getSysMetadataTypeId());
//				if(defaultValue!=null && !"".equals(defaultValue)){
//					doi+=POINT+defaultValue;
////				}else{
//				MetadataDefinition metadataDefinition =  MetadataSupport.getMetadataDefinitionByName(fieldName);
//				String fieldZhName = metadataDefinition.getFieldZhName();
//					throw new Exception("定义DOI中名称为【"+fieldZhName+"】的属性值不能为空!");
//				}
				doi = "";
				return doi;
			}
		}
		}else{
			doi = "";
			return doi;
		}
		
		//连接第三段 thirdPartValue1
		if(sysDoi.getThirdExtendTwo()!=null && !"".equals(sysDoi.getThirdExtendTwo())){
//			SysMetadataDefinition sysMetadataDefinition =SysMetaDataUtils.queryMetadataByFieldEnName(sysDoiDefinition.getThirdPartValue1(), sysDoiDefinition.getSysMetadataType().getSysMetadataTypeId());
			fieldName = sysDoi.getThirdExtendTwo();
			if(!"".equals(fieldName)&&ca.getMetadataMap().get(fieldName)!=null){
				fieldValue=ca.getMetadataMap().get(fieldName);
			}
//			else{
//				fieldValue=BeanUtils.getProperty(sysResDirectory, fieldName);
//			}
			if(!StringUtil.isEmpty(fieldValue)){
//				if(fieldValue.length()>THIRDPARTVALUE1SIZE){
//					throw new Exception("元数据对象中名称为"+fieldName+"的属性值最长只能为"+THIRDPARTVALUE1SIZE+"位有效位!");
//				}else if(fieldValue.length()<THIRDPARTVALUE1SIZE){
//					fieldValue=fillValueToFixedLength(fieldValue,THIRDPARTVALUE1SIZE);
//					if(StringUtil.isEmpty(fieldValue)){
//						throw new Exception("补齐元数据对象中名称为"+fieldName+"的属性值【"+fieldValue+"为长度"+THIRDPARTVALUE1SIZE+"位失败!");
//					}
//				}
				doi+=POINT+fieldValue;
				logger.info("----------------------------------第三段ExtendTwo---"+fieldValue+"----------------------------------");
			}else{
//				String defaultValue=SysMetaDataUtils.queryMetadataDefauleValueByFieldEnName(sysDoiDefinition.getThirdPartValue1(), sysDoiDefinition.getSysMetadataType().getSysMetadataTypeId());
//				if(defaultValue!=null && !"".equals(defaultValue)){
//					doi+=POINT+defaultValue;
//				}else{
//				MetadataDefinition metadataDefinition =  MetadataSupport.getMetadataDefinitionByName(fieldName);
//				String fieldZhName = metadataDefinition.getFieldZhName();
//					throw new Exception("定义DOI中名称为【"+fieldZhName+"】的属性值不能为空!");
//				}
				doi = "";
				return doi;
			}
			
		}
	
		//连接第三段 thirdPartValue2:最长为THIRDPARTVALUE2SIZE位 不够则补齐
		if(sysDoi.getThirdExtendThree()!=null && !"".equals(sysDoi.getThirdExtendThree())){
//			SysMetadataDefinition sysMetadataDefinition =SysMetaDataUtils.queryMetadataByFieldEnName(sysDoiDefinition.getThirdPartValue2(), sysDoiDefinition.getSysMetadataType().getSysMetadataTypeId());
			fieldName =sysDoi.getThirdExtendThree();
			if(!"".equals(fieldName)&&ca.getMetadataMap().get(fieldName)!=null){
				fieldValue=ca.getMetadataMap().get(fieldName);
			}
//			else{
//				fieldValue=BeanUtils.getProperty(sysResDirectory, fieldName);
//			}
			if(!StringUtil.isEmpty(fieldValue)){
//				if(fieldValue.length()>THIRDPARTVALUE2SIZE){
//					throw new Exception("元数据对象中名称为"+fieldName+"的属性值最长只能为"+THIRDPARTVALUE2SIZE+"位有效位!");
//				}else if(fieldValue.length()<THIRDPARTVALUE2SIZE){
//					fieldValue=fillValueToFixedLength(fieldValue,THIRDPARTVALUE2SIZE);
//					if(StringUtil.isEmpty(fieldValue)){
//						throw new Exception("补齐元数据对象中名称为"+fieldName+"的属性值【"+fieldValue+"为长度"+THIRDPARTVALUE2SIZE+"位失败!");
//					}
//				}
				doi+=POINT+fieldValue;
			}else{
//				String defaultValue=SysMetaDataUtils.queryMetadataDefauleValueByFieldEnName(sysDoiDefinition.getThirdPartValue2(), sysDoiDefinition.getSysMetadataType().getSysMetadataTypeId());
//				if(defaultValue!=null && !"".equals(defaultValue)){
//					doi+=POINT+defaultValue;
//				}else{
//				MetadataDefinition metadataDefinition =  MetadataSupport.getMetadataDefinitionByName(fieldName);
//				String fieldZhName = metadataDefinition.getFieldZhName();
//					throw new Exception("定义DOI中名称为【"+fieldZhName+"】的属性值不能为空!");
//				}
				doi = "";
				return doi;
			}
		}
		//连接第三段 thirdPartValue3:最长为THIRDPARTVALUE2SIZE位 不够则补齐
				if(sysDoi.getThirdExtendFour()!=null && !"".equals(sysDoi.getThirdExtendFour())){
//					SysMetadataDefinition sysMetadataDefinition =SysMetaDataUtils.queryMetadataByFieldEnName(sysDoiDefinition.getThirdPartValue3(), sysDoiDefinition.getSysMetadataType().getSysMetadataTypeId());
					fieldName = sysDoi.getThirdExtendFour();
					if(!"".equals(fieldName)&&ca.getMetadataMap().get(fieldName)!=null){
						fieldValue=ca.getMetadataMap().get(fieldName);
					}
//					else{
//						fieldValue=BeanUtils.getProperty(sysResDirectory, fieldName);
//					}
					if(!StringUtil.isEmpty(fieldValue)){
//						if(fieldValue.length()>THIRDPARTVALUE2SIZE){
//							throw new Exception("元数据对象中名称为"+fieldName+"的属性值最长只能为"+THIRDPARTVALUE2SIZE+"位有效位!");
//						}else if(fieldValue.length()<THIRDPARTVALUE2SIZE){
//							fieldValue=fillValueToFixedLength(fieldValue,THIRDPARTVALUE2SIZE);
//							if(StringUtil.isEmpty(fieldValue)){
//								throw new Exception("补齐元数据对象中名称为"+fieldName+"的属性值【"+fieldValue+"为长度"+THIRDPARTVALUE2SIZE+"位失败!");
//							}
//						}
						doi+=POINT+fieldValue;
					}else{
//						String defaultValue=SysMetaDataUtils.queryMetadataDefauleValueByFieldEnName(sysDoiDefinition.getThirdPartValue3(), sysDoiDefinition.getSysMetadataType().getSysMetadataTypeId());
//						if(defaultValue!=null && !"".equals(defaultValue)){
//							doi+=POINT+defaultValue;
//						}else{
//						MetadataDefinition metadataDefinition =  MetadataSupport.getMetadataDefinitionByName(fieldName);
//						String fieldZhName = metadataDefinition.getFieldZhName();
//							throw new Exception("定义DOI中名称为【"+fieldZhName+"】的属性值不能为空!");
//						}
						doi = "";
						return doi;
					}
				}
				//连接第三段 thirdPartValue4:最长为THIRDPARTVALUE2SIZE位 不够则补齐
				if(sysDoi.getThirdExtendFive()!=null && !"".equals(sysDoi.getThirdExtendFive())){
					fieldName=sysDoi.getThirdExtendFive();
					if(ca.getMetadataMap().get(fieldName)!=null&&!"".equals(fieldName)){
						fieldValue=ca.getMetadataMap().get(fieldName);
					}
					if(!StringUtil.isEmpty(fieldValue)){
//						if(fieldValue.length()>THIRDPARTVALUE2SIZE){
//							throw new Exception("元数据对象中名称为"+fieldName+"的属性值最长只能为"+THIRDPARTVALUE2SIZE+"位有效位!");
//						}else if(fieldValue.length()<THIRDPARTVALUE2SIZE){
//							fieldValue=fillValueToFixedLength(fieldValue,THIRDPARTVALUE2SIZE);
//							if(StringUtil.isEmpty(fieldValue)){
//								throw new Exception("补齐元数据对象中名称为"+fieldName+"的属性值【"+fieldValue+"为长度"+THIRDPARTVALUE2SIZE+"位失败!");
//							}
//						}
						doi+=POINT+fieldValue;
					}else{
//						String defaultValue=SysMetaDataUtils.queryMetadataDefauleValueByFieldEnName(sysDoiDefinition.getThirdPartValue4(), sysDoiDefinition.getSysMetadataType().getSysMetadataTypeId());
//						if(defaultValue!=null && !"".equals(defaultValue)){
//							doi+=POINT+defaultValue;
//						}else{
//						MetadataDefinition metadataDefinition =  MetadataSupport.getMetadataDefinitionByName(fieldName);
//						String fieldZhName = metadataDefinition.getFieldZhName();
//							throw new Exception("定义DOI中名称为【"+fieldZhName+"】的属性值不能为空!");
//						}
						doi = "";
						return doi;
					}
				}
//				if(sysResDirectory.getResProvided()!=null){
//					doi = doi.substring(0,doi.indexOf("resp")+4)+ SEPERATOR+doi.substring(doi.indexOf("resp")+5,doi.length());
//					doi = doi.replace("resp", sysResDirectory.getResProvided().getPressCode());
					String serialNumber = SysMetaDataUtils.createSerialNumber(ca.getPublishType());
					doi += POINT+serialNumber;
					
//				}
					logger.info("----------------------------------最终doi---"+doi+"----------------------------------");
		return doi;
	}
	/**
	 * 测试DOI定义中第二段的每一个可选值：按名称优先级顺序获得该部分的DOI组成部分
	 * @param sysResDirectory
	 * @param sysDoiDefinition
	 * @return 返回值形式成功     0|isbn.98763542318   ;失败 1|错误信息
	 * @throws Exception 
	 */
	private static String chooseSecondPartValue(SysDoi sysDoi,Long pubType,Ca ca) throws Exception {
		String secondOptString="";
		String errorMsg="";
		//测试第一个可选值：如果有值 则使用第一个可选值
		secondOptString=sysDoi.getSecondOptionalOne();
		if(!StringUtil.isEmpty(secondOptString)){
			secondOptString=getSecondPartValue(secondOptString,pubType,ca);
			if(!StringUtil.isEmpty(secondOptString)){
				return "0|"+secondOptString;
			}else{
				errorMsg+="通过【"+sysDoi.getSecondOptionalOne()+"】不能生成DOI,请检查DOI定义和元数据中生成DOI所需要的值是否符合规范：不为空且不超过最大长度!";
				return "1|"+errorMsg;
			}
			}else{
				
			errorMsg+="DOI主要属性第1个可选值为空!";
			
		}
		
		//测试第二个可选值：如果有值 则使用第二个可选值
		secondOptString=sysDoi.getSecondOptionalTwo();
		if(!StringUtil.isEmpty(secondOptString)){
			secondOptString=getSecondPartValue(secondOptString,pubType,ca);
			if(!StringUtil.isEmpty(secondOptString)){
				return "0|"+secondOptString;
			}else{
				errorMsg+="通过【"+sysDoi.getSecondOptionalTwo()+"】不能生成DOI,请检查DOI定义和元数据中生成DOI所需要的值是否符合规范：不为空且不超过最大长度!";
				return "1|"+errorMsg;
			}
		}else{
			errorMsg+="DOI主要属性第2个可选值为空!";
		}
		
		
		//测试第三个可选值：如果有值 则使用第二个可选值
		secondOptString=sysDoi.getSecondOptionalThree();
		if(!StringUtil.isEmpty(secondOptString)){
			secondOptString=getSecondPartValue(secondOptString,pubType,ca);
			if(!StringUtil.isEmpty(secondOptString)){
				return "0|"+secondOptString;
			}else{
				errorMsg+="通过【"+sysDoi.getSecondOptionalThree()+"】不能生成DOI,请检查DOI定义和元数据中生成DOI所需要的值是否符合规范：不为空且不超过最大长度!";
				return "1|"+errorMsg;
			}
		}else{
			errorMsg+="DOI主要属性第3个可选值为空!";
		}
		
		//测试第四个可选值：如果有值 则使用第二个可选值
		secondOptString=sysDoi.getSecondOptionalFour();
		if(!StringUtil.isEmpty(secondOptString)){
			secondOptString=getSecondPartValue(secondOptString,pubType,ca);
			if(!StringUtil.isEmpty(secondOptString)){
				return "0|"+secondOptString;
			}else{
				errorMsg+="通过【"+sysDoi.getSecondOptionalFour()+"】不能生成DOI,请检查DOI定义和元数据中生成DOI所需要的值是否符合规范：不为空且不超过最大长度!";
				return "1|"+errorMsg;
			}
		}else{
			errorMsg+="DOI主要属性第4个可选值为空!";
		}
		
		return "1|"+errorMsg;
	}
	/**
	 * 获得DOI中的第二段的部分：如 isbn.98763542318 或者cobn.39827363748   
	 * @param secondOptString
	 * @param sysMetadataTypeId
	 * @param sysResDirectory
	 * @return
	 * @throws Exception
	 */
	private static String getSecondPartValue(String secondOptString,Long publishType,Ca ca) throws Exception{
		String partValue="";
		String shortName="";
		String fieldName="";
		String fieldValue="";
		String[] partArray=null;
		if(!StringUtil.isEmpty(secondOptString)){
			if(secondOptString.indexOf(SEPERATOR)!=-1){
				partArray=secondOptString.split(SEPERATOR);
				if(partArray!=null && partArray.length==2){
			//		if(partArray[1].length()==SECONDPARTOPTSHORTSIZE){
						fieldName=partArray[0];
						shortName=partArray[1];
		//			}
				}
			}
		}
		if(!StringUtil.isEmpty(fieldName)){			
//			SysMetadataDefinition sysMetadataDefinition =SysMetaDataUtils.queryMetadataByFieldEnName(fieldName, publishType);
//			fieldName = sysMetadataDefinition.getField();
				if(!"".equals(fieldName)&&ca.getMetadataMap().get(fieldName)!=null){
					fieldValue=ca.getMetadataMap().get(fieldName);
				}
//					else{
//					fieldValue=BeanUtils.getProperty(sysResDirectory, fieldName);
//				}
//				if(!StringUtil.isEmpty(fieldValue)){
//					if(fieldValue.length()>SECONDPARTOPTVALUESIZE){
//						throw new Exception("第二部分可选值不能超过 "+SECONDPARTOPTVALUESIZE+" 位!");
//					}else if(fieldValue.length()<SECONDPARTOPTVALUESIZE){
//						fieldValue=fillValueToFixedLength(fieldValue,SECONDPARTOPTVALUESIZE);
//					}
//				}
		}
		if(!StringUtil.isEmpty(shortName) && !StringUtil.isEmpty(fieldValue)){
			partValue=shortName+POINT+fieldValue;
		}
		return partValue;
	}
	/**
	 * 将valueStr指定的字符串按照length指定的长度扩展  长度不够length的 在其前面补0
	 * @param valueStr
	 * @param length
	 * @return 为空表示发生错误   否则返回正确值(补零之后)
	 */
	private static String fillValueToFixedLength(String valueStr,Integer length){
		String value="";
		int diff=0;
		if(!StringUtil.isEmpty(valueStr) && length!=null){
			diff=length-valueStr.length();
			if(diff>0){
				for(int i=0;i<diff;i++){
					value+=ZERO;
				}
				value+=valueStr;
			}
		}
		return value;
	}
	/**
	 * 验证sysDoiDefinition对象的合法性:定义的Doi的各个部分对于空值的限制
	 * @param sysDoiDefinition
	 * @return 空表示验证通过   否则返回错误信息
	 */
//	private static String validateSysDoiDefinition(SysDoiDefinition sysDoiDefinition){
//		String msg="";
//		//验证第一部分符号：即 td 部分不能为空 且为两位
//		if(!StringUtil.isEmpty(sysDoiDefinition.getFirstPartSymbol())){
////			if(sysDoiDefinition.getFirstPartSymbol().length()!=FIRSTPARTSYBMOLSIZE){
////				return msg="Doi定义的第一部分符号只能为"+FIRSTPARTSYBMOLSIZE+"位!";
////			}
//		}else{
//			return msg="Doi定义的出版社代码不能为空!";
//		}
//		//验证第二部分的合法性：通过该名称能够找到一条系统元数据定义：预防元数据英文名称有过改动却没有同步到DOI定义中
////		if(!StringUtil.isEmpty(sysDoiDefinition.getFirstPartValue())){
////			if(StringUtil.isEmpty(SysMetaDataUtils.queryMetadataFieldByFieldEnName(sysDoiDefinition.getFirstPartValue(),
////					sysDoiDefinition.getSysMetadataType().getSysMetadataTypeId()))){
////				return msg="根据Doi定义的第一部分符号值【"+sysDoiDefinition.getFirstPartValue()+"】不能找到对应的元数据定义,请检查是否在定义DOI规则后修改过元数据英文名称!";
////			}
////		}else{
////			return msg="Doi定义的第一部分值不能为空!";
////		}
//		//验证第二部分符号：即 t 部分不能为空 且为1位
////		if(!StringUtil.isEmpty(sysDoiDefinition.getSecondPartSymbol())){
////			if(sysDoiDefinition.getSecondPartSymbol().length()!=SECONDPARTSYMBOL1SIZE){
////				return msg="Doi定义的第二部分符号1只能为"+SECONDPARTSYMBOL1SIZE+"位!";
////			}
////		}else{
////			return msg="Doi定义的第二部分符号不能为空!";
////		}
//		//验证第二部分4个备选项的合法性:1、不能全部为空   2、可以根据符号 '/'之前的值找到一条系统元数据定义：预防元数据英文名称有过改动却没有同步到DOI定义中
//		if(StringUtil.isEmpty(sysDoiDefinition.getSecondOpt1()) && StringUtil.isEmpty(sysDoiDefinition.getSecondOpt2())
//				&& StringUtil.isEmpty(sysDoiDefinition.getSecondOpt3()) && StringUtil.isEmpty(sysDoiDefinition.getSecondOpt4())){
//			return msg="Doi定义的主要属性可选部分不能全部为空!";
//		}
//		//验证主要属性部分中 ：第一个可选值
//		msg=validateSecondOptValue(sysDoiDefinition.getSecondOpt1(),sysDoiDefinition.getSysMetadataType().getSysMetadataTypeId());
//		if(!StringUtil.isEmpty(msg)){
//			return msg="Doi定义错误:"+msg;
//		}
//		//验证主要属性部分中 ：第二个可选值
//		msg=validateSecondOptValue(sysDoiDefinition.getSecondOpt2(),sysDoiDefinition.getSysMetadataType().getSysMetadataTypeId());
//		if(!StringUtil.isEmpty(msg)){
//			return msg="Doi定义错误:"+msg;
//		}
//		//验证主要属性部分中 ：第三个可选值
//		msg=validateSecondOptValue(sysDoiDefinition.getSecondOpt3(),sysDoiDefinition.getSysMetadataType().getSysMetadataTypeId());
//		if(!StringUtil.isEmpty(msg)){
//			return msg="Doi定义错误:"+msg;
//		}
//		//验证主要属性部分中 ：第四个可选值
//		msg=validateSecondOptValue(sysDoiDefinition.getSecondOpt4(),sysDoiDefinition.getSysMetadataType().getSysMetadataTypeId());
//		if(!StringUtil.isEmpty(msg)){
//			return msg="Doi定义错误:"+msg;
//		}
//		
////		//验证第三部分符号：即 c 部分不能为空 且为1位
////		if(!StringUtil.isEmpty(sysDoiDefinition.getThirdPartSymbol())){
////			if(sysDoiDefinition.getThirdPartSymbol().length()!=THIRDPARTSYMBOLSIZE){
////				return msg="Doi定义的第三部分符号只能为"+THIRDPARTSYMBOLSIZE+"位!";
////			}
////		}else{
////			return msg="Doi定义的第三部分符号不能为空!";
////		}
//		//验证扩展属性1的合法性：1、不能为空  2、通过该名称能够找到一条系统元数据定义：预防元数据英文名称有过改动却没有同步到DOI定义中
//		if(!StringUtil.isEmpty(sysDoiDefinition.getThirdPartSymbol())){
//			if(StringUtil.isEmpty(SysMetaDataUtils.queryMetadataFieldByFieldEnName(sysDoiDefinition.getThirdPartSymbol(),
//					sysDoiDefinition.getSysMetadataType().getSysMetadataTypeId()))){
//				return msg="根据Doi定义的扩展属性1【"+sysDoiDefinition.getThirdPartSymbol()+"】不能找到对应的元数据定义,请检查是否在定义DOI规则后修改过元数据英文名称!";
//			}
//		}
//		//验证扩展属性2的合法性：1、不能为空  2、通过该名称能够找到一条系统元数据定义：预防元数据英文名称有过改动却没有同步到DOI定义中
//		if(!StringUtil.isEmpty(sysDoiDefinition.getThirdPartValue1())){
//			if(StringUtil.isEmpty(SysMetaDataUtils.queryMetadataFieldByFieldEnName(sysDoiDefinition.getThirdPartValue1(),
//					sysDoiDefinition.getSysMetadataType().getSysMetadataTypeId()))){
//				return msg="根据Doi定义的扩展属性2【"+sysDoiDefinition.getThirdPartValue1()+"】不能找到对应的元数据定义,请检查是否在定义DOI规则后修改过元数据英文名称!";
//			}
//		}
//		
//		//验证扩展属性3的合法性：1、不能为空  2、通过该名称能够找到一条系统元数据定义：预防元数据英文名称有过改动却没有同步到DOI定义中
//		if(!StringUtil.isEmpty(sysDoiDefinition.getThirdPartValue2())){
//			if(StringUtil.isEmpty(SysMetaDataUtils.queryMetadataFieldByFieldEnName(sysDoiDefinition.getThirdPartValue2(),
//					sysDoiDefinition.getSysMetadataType().getSysMetadataTypeId()))){
//				return msg="根据Doi定义的扩展属性3【"+sysDoiDefinition.getThirdPartValue2()+"】不能找到对应的元数据定义,请检查是否在定义DOI规则后修改过元数据英文名称!";
//			}
//		}
//		//验证扩展属性4的合法性：1、不能为空  2、通过该名称能够找到一条系统元数据定义：预防元数据英文名称有过改动却没有同步到DOI定义中
//		if(!StringUtil.isEmpty(sysDoiDefinition.getThirdPartValue3())){
//			if(StringUtil.isEmpty(SysMetaDataUtils.queryMetadataFieldByFieldEnName(sysDoiDefinition.getThirdPartValue3(),
//					sysDoiDefinition.getSysMetadataType().getSysMetadataTypeId()))){
//				return msg="根据Doi定义的扩展属性4【"+sysDoiDefinition.getThirdPartValue3()+"】不能找到对应的元数据定义,请检查是否在定义DOI规则后修改过元数据英文名称!";
//			}
//		}
//		//验证扩展属性5的合法性：1、不能为空  2、通过该名称能够找到一条系统元数据定义：预防元数据英文名称有过改动却没有同步到DOI定义中
//		if(!StringUtil.isEmpty(sysDoiDefinition.getThirdPartValue4())){
//			if(StringUtil.isEmpty(SysMetaDataUtils.queryMetadataFieldByFieldEnName(sysDoiDefinition.getThirdPartValue4(),
//					sysDoiDefinition.getSysMetadataType().getSysMetadataTypeId()))){
//				return msg="根据Doi定义的扩展属性5【"+sysDoiDefinition.getThirdPartValue4()+"】不能找到对应的元数据定义,请检查是否在定义DOI规则后修改过元数据英文名称!";
//			}
//		}
//		return msg;
//	}
	/**
	 * 验证第二部分中各个可选值得合法性：可以根据符号 '/'之前的值找到一条系统元数据定义：预防元数据英文名称有过改动却没有同步到DOI定义中
	 * @param optValue
	 * @param sysMetadataTypeId
	 * @return 空表示正确   否则返回出错信息
	 */
//	private static String validateSecondOptValue(String optValue,Long sysMetadataTypeId){
//		String msg="";
//		String[] valueArray=null;
//		if(!StringUtil.isEmpty(optValue)){
//			if(optValue.indexOf(SEPERATOR)!=-1){
//				valueArray=optValue.split(SEPERATOR);
//				if(valueArray.length==2){
//				//	if(valueArray[1].length()==SECONDPARTOPTSHORTSIZE){
//						if(StringUtil.isEmpty(SysMetaDataUtils.queryMetadataFieldByFieldEnName(valueArray[0],sysMetadataTypeId))){
//							msg="根据Doi定义的主要属性部分可选值【"+optValue+"】中分隔符前面的元数据英文名称 "+valueArray[0]+" 不能找到对应的元数据定义,请检查是否在定义DOI规则后修改过元数据英文名称!";
//						}
////					}else{
////						msg="可选值格式错误,分隔符"+SEPERATOR+"后面只能是"+SECONDPARTOPTSHORTSIZE+"位!";
////					}
//				}else{
//					msg="可选值格式错误,格式需要为 xxxx"+SEPERATOR+"yyyy!";
//				}
//			}else{
//				msg="可选值格式错误,格式需要为 xxxx"+SEPERATOR+"yyyy!";
//			}
//		}
//		return msg;
//	}

}
