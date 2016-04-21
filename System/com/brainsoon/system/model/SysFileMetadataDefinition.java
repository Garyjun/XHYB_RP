package com.brainsoon.system.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;

import com.brainsoon.common.po.BaseHibernateObject;
import com.brainsoon.semantic.schema.common.SysMetadataDefinitionConstants;
@Entity
@Table(name = "sys_FileMetadata_Definition")
public class SysFileMetadataDefinition extends BaseHibernateObject {
	private static final long serialVersionUID = 4858001351954128087L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false) 
    private Long id;
    @Column
    private Long sysFileMetadataTypeId;
    @Column
    // 元数据类别 1:基础元数据 2:继承元数据
    private int type;
    
    // 字段英文名称
    @Column
    private String fieldName;

    // 字段中文名称
    @Column
    private String fieldZhName;

    // 字段类型 1:text 文本、2:select 选择框、3:checkBox 复选框、4:radio 单选框 5：textarea 文本域
    // 6:byte[] 图片附件 7 date 日期 8 分类lookup 9url
    @Column
    private Integer fieldType;

    // 是否允许空值 默认1 允许为空 0 不允许为空
    @Column
    private Integer allowNull;

    // 值格式效验模式 默认1:无 2:数字 3:字母 4:数字及字母 5:汉字 6:邮箱
    @Column
    private Integer validateModel;

    // 值范围 已,分割多个值 只有当字段类型为2:select 选择框、3:checkBox 复选框、4:radio 单选框 时有效
    // 如果为分类查找类型则为获取分类的url
    @Column
    private String valueRange;

    // 值长度范围 以双闭区间限定取值范围 如 0,10表示长度在0和10之间 如为5则长度必须大于5 如为,10表示长度必须小于10
    @Column
    private String valueLength;


    // 排序值
    @Column
    private Integer orderNum;

    // 显示优先级 默认1:仅详细页展示 2:精简信息展示 3：列表页展示
    @Column
    private Integer viewPriority;
    @Column
    private Integer   identifier;
    @Column
    private String exportLevel;
    //是否只读 0否，1是
    @Column
    private String readOnly;
    @Column
    private String defaultValue;  //默认值   
 
    //是否用于查重  1代表是查重字段  
    @Column
    private String duplicateCheck;
    @Column
    private String relatedWords; //多个用半角,分隔
    @Column
    private String openAutoComple;//是否打开自动完成1为打开
    @Column
    private String createIndex; //是否创建索引 1为创建索引
    @Column
    private String showField;//是否显示  1为不显示
    @Column
    private String attriType; //属性分类
    @Column
    private String lomAttri;
    @Column
    private String openQuery;
    
	public String getLomAttri() {
		return lomAttri;
	}
	public void setLomAttri(String lomAttri) {
		this.lomAttri = lomAttri;
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

    public String getFieldName() {
        return fieldName;
    }
  
	public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
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

    
    public Integer getValidateModel() {
        return validateModel;
    }

    
    public String getValidateModelDesc() {
        return SysMetadataDefinitionConstants.ValidateModel
                .getValueByKey(getValidateModel().toString());
    }

    
    public String getValidatePattern() {
        return SysMetadataDefinitionConstants.ValidatePattern
                .getValueByKey(getValidateModel().toString());
    }

    public void setValidateModel(Integer validateModel) {
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

    
    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public Integer getViewPriority() {
        return viewPriority;
    }

    
    public String getViewPriorityDesc() {
        return SysMetadataDefinitionConstants.ViewPriority
                .getValueByKey(getViewPriority().toString());
    }

    public void setViewPriority(Integer viewPriority) {
        this.viewPriority = viewPriority;
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
    
    
    public boolean isIdentifier(){
        if(null!=getIdentifier()&&1==getIdentifier()){
            return true;
        }
        return false;
    }
    
	@Override
	public String getObjectDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEntityDescription() {
		// TODO Auto-generated method stub
		return null;
	}
}
