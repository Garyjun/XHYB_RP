/**
 * 
 */
package com.brainsoon.semantic.schema.ontology;

import java.io.Serializable;

import javax.persistence.Column;

import org.apache.commons.lang3.StringUtils;

import com.brainsoon.common.support.MetadataDefinitionGroupCacheMap;
import com.brainsoon.common.support.SysResTypeCacheMap;
import com.brainsoon.semantic.schema.common.SysMetadataDefinitionConstants;


public class MetadataDefinition implements Comparable<MetadataDefinition>,Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 2172130261724030063L;
	// 元数据类别 1:基础元数据 2:继承元数据
    private int type;
 // 元数据标示
    private String uri;
    //所属分类
    private String domain;
    
    // 字段英文名称
    private String fieldName;

    // 字段中文名称
    private String fieldZhName;

    // 字段类型 1:text 文本、2:select 选择框、3:checkBox 复选框、4:radio 单选框 5：textarea 文本域
    //  7 date 日期 6 分类lookup 9url
    private Integer fieldType;

    // 是否允许空值 默认1 允许为空 0 不允许为空
    private Integer allowNull;

    // 值格式效验模式 默认1:无 2:数字 3:字母 4:数字及字母 5:汉字 6:邮箱
    private String validateModel;

    // 值范围 已,分割多个值 只有当字段类型为2:select 选择框、3:checkBox 复选框、4:radio 单选框 时有效
    // 如果为分类查找类型则为获取分类的url
    private String valueRange;

    // 值长度范围 以双闭区间限定取值范围 如 0,10表示长度在0和10之间 如为5则长度必须大于5 如为,10表示长度必须小于10
    private String valueLength;

    // 验证函数名
    private String validateFName;

    // 排序值
    private Integer orderNum;

    // 显示优先级 默认1:仅详细页展示 3编辑页展示 2：列表页展示
    private String viewPriority;

    // 查询模式 默认1：不用于查询 2：完全匹配查询 3：模糊匹配查询 4：区间查询
    private Integer queryModel  ;
    //标签:10 doi:5
    private Integer   identifier;
    
    private String exportLevel;
    
    /*新加字段  对元数据定义的描述  最大长度512位*/
    private String description;

    //是否开启模糊查询功能 0不开启，1开启
    private Integer openBlur;
    //是否只读 0否，1是
    private String readOnly;
    private String defaultValue;  //默认值   
    private String groupId;
    private String groupName;
    //是否用于查重  1代表是查重字段  
    private String duplicateCheck;
    
    private String superProperty;
    
    private String relatedWords; //多个用半角,分隔
    
    private String openAutoComple;//是否打开自动完成1为打开
    
    private String createIndex; //是否创建索引 1为创建索引
    
    private String showField;//是否显示  1为不显示
    
    private String resType; //资源分类 图书 漫画等
    
    private String attriType; //属性分类
    
    private String lomAttri;
    
    private String allowAdvancedQuery;

    private String resLifeCycle;
    
    private String openQuery;
    
    private String isCom; //2代表普通元数据 1代表核心元数据 3 文件元数据
    
    private String secondSearch;	//二次查询
    
	public String getIsCom() {
		return isCom;
	}
	public void setIsCom(String isCom) {
		this.isCom = isCom;
	}
	public String getGroupName() {
		String tempGroupName = "";
		if(StringUtils.isNotBlank(groupId)){
			tempGroupName = (String) MetadataDefinitionGroupCacheMap.getValue(groupId);
		}
		return tempGroupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getLomAttri() {
		return lomAttri;
	}
	public void setLomAttri(String lomAttri) {
		this.lomAttri = lomAttri;
	}
	public String getAllowAdvancedQuery() {
		return allowAdvancedQuery;
	}
	public void setAllowAdvancedQuery(String allowAdvancedQuery) {
		this.allowAdvancedQuery = allowAdvancedQuery;
	}
	public String getResLifeCycle() {
		return resLifeCycle;
	}
	public void setResLifeCycle(String resLifeCycle) {
		this.resLifeCycle = resLifeCycle;
	}
	public String getOpenQuery() {
		return openQuery;
	}
	public void setOpenQuery(String openQuery) {
		this.openQuery = openQuery;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getAttriType() {
		return attriType;
	}
	public void setAttriType(String attriType) {
		this.attriType = attriType;
	}
	public String getShowField() {
		return showField;
	}
	public void setShowField(String showField) {
		this.showField = showField;
	}
	public String getResType() {
		return resType;
	}
	public void setResType(String resType) {
		this.resType = resType;
	}
	public String getCreateIndex() {
		return createIndex;
	}
	public void setCreateIndex(String createIndex) {
		this.createIndex = createIndex;
	}
	public String getOpenAutoComple() {
		return openAutoComple;
	}
	public void setOpenAutoComple(String openAutoComple) {
		this.openAutoComple = openAutoComple;
	}
	public String getRelatedWords() {
		return relatedWords;
	}
	public void setRelatedWords(String relatedWords) {
		this.relatedWords = relatedWords;
	}
	public String getDuplicateCheck() {
		return duplicateCheck;
	}
	public void setDuplicateCheck(String duplicateCheck) {
		this.duplicateCheck = duplicateCheck;
	}
	public String getUri() {
    	return uri;
    }
    public void setUri(String uri)  {
    	this.uri = uri;
    }
    public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
    public String getReadOnly() {
		return readOnly;
	}
    
    public Boolean isReadOnlyMode() {
		return StringUtils.equals("1", getReadOnly());
	}

	public void setReadOnly(String readOnly) {
		this.readOnly = readOnly;
	}

	public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupId(){
       return groupId;
    }
    
    public String getFieldZhName() {
        return fieldZhName;
    }

    public void setFieldZhName(String fieldZhName) {
        this.fieldZhName = fieldZhName;
    }
    
    public String getExportLevel() {
        return exportLevel;
    }

    public void setExportLevel(String exportLevel) {
        this.exportLevel = exportLevel;
    }

    public Integer getType() {
        return type;
    }
    
    public String getTypeDesc() {
        return SysMetadataDefinitionConstants.Type.getValueByKey(getType()
                .toString());
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDomain() {
        return domain;
    }
    public void setDomain(String domain) {
        this.domain = domain;
    }
    
    public String getFieldName() {
        return fieldName;
    }
  
	public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
	
    public Integer getOpenBlur() {
		return openBlur;
	}

	public void setOpenBlur(Integer openBlur) {
		this.openBlur = openBlur;
	}


	public Integer getFieldType() {
        return fieldType;
    }

    
    public String getFieldTypeDesc() {
        return SysMetadataDefinitionConstants.FieldType
                .getValueByKey(getFieldType().toString());
    }

    public void setFieldType(Integer fieldType) {
        this.fieldType = fieldType;
    }

    public Integer getAllowNull() {
        return allowNull;
    }

    
    public String getAllowNullDesc() {
        return SysMetadataDefinitionConstants.AllowNull.getValueByKey(String
                .valueOf(getAllowNull()));
    }

    public void setAllowNull(Integer allowNull) {
        this.allowNull = allowNull;
    }

    
    
    public String getValidateModelDesc() {
        return SysMetadataDefinitionConstants.ValidateModel
                .getValueByKey(getValidateModel().toString());
    }

    
    public String getValidatePattern() {
        return SysMetadataDefinitionConstants.ValidatePattern
                .getValueByKey(getValidateModel().toString());
    }

    public String getValidateModel() {
		return validateModel;
	}
	public void setValidateModel(String validateModel) {
		this.validateModel = validateModel;
	}
	public String getValueRange() {
        return valueRange;
    }

    public void setValueRange(String valueRange) {
        this.valueRange = valueRange;
    }

    
    public String getValueLength() {
        return valueLength;
    }

    public void setValueLength(String valueLength) {
        this.valueLength = valueLength;
    }

 public String getValidateFName() {
        return validateFName;
    }

    public void setValidateFName(String validateFName) {
        this.validateFName = validateFName;
    }

    
    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public String getViewPriorityDesc() {
        return SysMetadataDefinitionConstants.ViewPriority
                .getValueByKey(getViewPriority());
    }


    
    public String getViewPriority() {
		return viewPriority;
	}
	public void setViewPriority(String viewPriority) {
		this.viewPriority = viewPriority;
	}
	public Integer getQueryModel() {
        return queryModel;
    }

    
    public String getQueryModelDesc() {
        return SysMetadataDefinitionConstants.QueryModel
                .getValueByKey(getQueryModel().toString());
    }

    public void setQueryModel(Integer queryModel) {
        this.queryModel = queryModel;
    }

    
    public String getIdentifierDesc() {
        return SysMetadataDefinitionConstants.Identifier.getValueByKey(String
                .valueOf(getIdentifier()));
    }

    
    public Integer getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Integer identifier) {
        this.identifier = identifier;
    }
    
    
//    public boolean isIdentifier(){
//        if(null!=getIdentifier()&&1==getIdentifier()){
//            return true;
//        }
//        return false;
//    }
    
    
    public String getSuperProperty() {
    	return superProperty;
    }
    
    public void setSuperProperty(String superProperty) {
    	this.superProperty = superProperty;	    	
      }
    
    public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getSecondSearch() {
		return secondSearch;
	}
	public void setSecondSearch(String secondSearch) {
		this.secondSearch = secondSearch;
	}
	@Override
	public int compareTo(MetadataDefinition arg0) {
		// TODO Auto-generated method stub
		if(arg0 == null || arg0.getOrderNum() == null || this.getOrderNum() == null){
			return 0;
		}
		return this.getOrderNum().compareTo(arg0.getOrderNum());
	}

}
