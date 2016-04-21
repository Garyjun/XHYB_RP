function MetaData(fieldEnName, fieldZhName, fieldType, fieldValues, groupId){ 
	this.fieldEnName = fieldEnName; 
	this.fieldZhName = fieldZhName; 
	this.fieldType = fieldType; 
	this.fieldValues = fieldValues; 
	this.groupId = groupId; 
	//this.domain = domain;
	
	this.getFieldEnName = getFieldEnName;
	this.setFieldEnName = setFieldEnName;
	
	this.getFieldZhName = getFieldZhName;
	this.setFieldZhName = setFieldZhName;
	
	this.getFieldType = getFieldType;
	this.setFieldType = setFieldType;
	
	this.getFieldValues = getFieldValues;
	this.setFieldValues = setFieldValues;
	
	this.getGroupId = getGroupId;
	this.setGroupId = setGroupId;
	
	this.toString = toString;
} 

//fieldEnName set和get方法
function getFieldEnName(){ 
    return this.fieldEnName; 
}; 
function setFieldEnName(fieldEnName){ 
    this.fieldEnName = fieldEnName; 
}; 

//fieldZhName set和get方法
function getFieldZhName(){ 
    return this.fieldZhName; 
}; 
function setFieldZhName(fieldZhName){ 
    this.fieldZhName = fieldZhName; 
}; 

//fieldType set和get方法
function getFieldType(){ 
    return this.fieldType; 
}; 
function setFieldType(fieldType){ 
    this.fieldType = fieldType; 
}; 

//fieldEnName set和get方法
function getFieldValues(){ 
    return this.fieldValues; 
}; 
function setFieldValues(fieldValues){ 
    this.fieldValues = fieldValues; 
}; 

//groupId set和get方法
function getGroupId(){ 
    return this.groupId; 
}; 
function setGroupId(groupId){ 
    this.groupId = groupId; 
}; 

//重写toString方法
function toString(){   
    var s = "{";   
    s += this.fieldEnName + "," + this.fieldZhName + "," + this.fieldType + "," + this.fieldValues + "," + this.groupId;
    s += "}";   
    return s;   
};