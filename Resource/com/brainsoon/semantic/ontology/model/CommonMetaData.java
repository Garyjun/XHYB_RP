package com.brainsoon.semantic.ontology.model;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;

import com.brainsoon.semantic.vocabulary.CommonDCTerms;
import com.brainsoon.system.support.SystemConstants.ResourceStatus;

@XmlRootElement
@JsonTypeInfo(use = Id.NONE)
public class CommonMetaData {
	private String objectId;
	
	private String resource;
	protected Map<String, String> commonMetaDatas = new HashMap<String, String>();
	
	public CommonMetaData() {
		commonMetaDatas.put("mapName", "commonMetaDatas");		
	}
	
	public String getObjectId() {
		return objectId;	
	}
	
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}	
	
	@XmlTransient
	public String getIdentifier() {
		return commonMetaDatas.get(CommonDCTerms.METADATA_IDENTIFIER); 	
	}
	
	public void setIdentifier(String identifier) {
		commonMetaDatas.put(CommonDCTerms.METADATA_IDENTIFIER, identifier);
	}
	@XmlTransient
	public String getPublishType() {
		return commonMetaDatas.get(CommonDCTerms.METADATA_PUBLISHTYPE);
	}

	public void setPublishType(String publishType) {
		commonMetaDatas.put(CommonDCTerms.METADATA_PUBLISHTYPE, publishType);
	}
	@XmlTransient
	public String getTitle() {
		return commonMetaDatas.get(CommonDCTerms.METADATA_TITLE);	
	}
	
	public void setTitle(String title) {
		commonMetaDatas.put(CommonDCTerms.METADATA_TITLE, title);
	}
	
	@XmlTransient
	public String getResVersion() {
		return commonMetaDatas.get(CommonDCTerms.METADATA_RES_VERSION);	
	}
	
	public void setResVersion(String resVersion) {
		commonMetaDatas.put(CommonDCTerms.METADATA_RES_VERSION, resVersion);
	}
	@XmlTransient
	public String getPath() {
		return commonMetaDatas.get(CommonDCTerms.METADATA_PATH);	
	}
	
	public void setPath(String path) {
		commonMetaDatas.put(CommonDCTerms.METADATA_PATH, path);
	}
	@XmlTransient	
	public String getType() {
		return commonMetaDatas.get(CommonDCTerms.METADATA_TYPE);
	}
	
	public void setType(String type) {
		commonMetaDatas.put(CommonDCTerms.METADATA_TYPE, type);
	}
	
	@XmlTransient
	public String getLibType() {
		return commonMetaDatas.get(CommonDCTerms.METADATA_LIBTYPE);	
	}
	
	public void setLibType(String libType) {
		commonMetaDatas.put(CommonDCTerms.METADATA_LIBTYPE, libType);
	}
	
	@XmlTransient	
	public String getLanguage() {
		return commonMetaDatas.get(CommonDCTerms.METADATA_LANGUAGE); 	
	}
	
	public void setLanguage(String language) {
		commonMetaDatas.put(CommonDCTerms.METADATA_MODULE, language);
	}
	
	@XmlTransient	
	public String getModule() {
		return commonMetaDatas.get(CommonDCTerms.METADATA_MODULE); 	
	}
		
	public void setModule(String module) {
		commonMetaDatas.put(CommonDCTerms.METADATA_MODULE, module); 
	}
	
	@XmlTransient
	public String getSubject() {
		return commonMetaDatas.get(CommonDCTerms.METADATA_SUBJECT); 	
	}
	
	public void setSubject(String subject) {
		commonMetaDatas.put(CommonDCTerms.METADATA_SUBJECT, subject);
	}
	
	@XmlTransient
	public String getEducational_phase() {
		return commonMetaDatas.get(CommonDCTerms.METADATA_EDUCATIONAL_PHASE); 	
	}
	
	public void setEducational_phase(String educational_phase) {
		commonMetaDatas.put(CommonDCTerms.METADATA_EDUCATIONAL_PHASE, educational_phase);
	}
	
	@XmlTransient
	public String getGrade() {
		return commonMetaDatas.get(CommonDCTerms.METADATA_GRADE); 	
	}
	
	public void setGrade(String grade) {
		commonMetaDatas.put(CommonDCTerms.METADATA_GRADE, grade);
	}
	
	@XmlTransient
	public String getFascicule() {
		return commonMetaDatas.get(CommonDCTerms.METADATA_FASCICULE); 	
	}
	
	public void setFascicule(String fascicule) {
		commonMetaDatas.put(CommonDCTerms.METADATA_FASCICULE, fascicule);
	}
	
	@XmlTransient
	public String getVersion() {
		return commonMetaDatas.get(CommonDCTerms.METADATA_VERSION); 
	}
	
	public void setVersion(String version) {
		commonMetaDatas.put(CommonDCTerms.METADATA_VERSION, version);
	}
	
	@XmlTransient
	public String getUnit() {
		return commonMetaDatas.get(CommonDCTerms.METADATA_UNIT);		
	}
	
	public void setUnit(String unit) {
		commonMetaDatas.put(CommonDCTerms.METADATA_UNIT, unit);
	}
	
	@XmlTransient
	public String getUnitName() {
		return commonMetaDatas.get(CommonDCTerms.METADATA_UNIT_NAME);		
	}
	
	public void setUnitName(String unitName) {
		commonMetaDatas.put(CommonDCTerms.METADATA_UNIT_NAME, unitName);
	}
	
	@XmlTransient
	public String getKnowledge_point() {
		return commonMetaDatas.get(CommonDCTerms.METADATA_KNOWLEDGE_POINT);		
	}
	
	public void setKnowledge_point(String knowledge_point) {
		commonMetaDatas.put(CommonDCTerms.METADATA_KNOWLEDGE_POINT, knowledge_point);
	}
	
	@XmlTransient
	public String getAudience() {
		return commonMetaDatas.get(CommonDCTerms.METADATA_AUDIENCE);	
	}
	
	public void setAudience(String audience) {
		commonMetaDatas.put(CommonDCTerms.METADATA_AUDIENCE, audience);
	}
	
	@XmlTransient
	public String getDescription() {
		return commonMetaDatas.get(CommonDCTerms.METADATA_DESCRIPTION);	
	}
	
	public void setDescription(String description) {
		commonMetaDatas.put(CommonDCTerms.METADATA_DESCRIPTION, description);
	}
	
	@XmlTransient
	public String getKeywords() {
		return commonMetaDatas.get(CommonDCTerms.METADATA_KEYWORDS);	
	}
	
	public void setKeywords(String keywords) {
		commonMetaDatas.put(CommonDCTerms.METADATA_KEYWORDS, keywords);
	}
	
	@XmlTransient
	public String getDifficulty_level() {
		return commonMetaDatas.get(CommonDCTerms.METADATA_DIFFICULTY_LEVEL);	
	}
	
	public void setDifficulty_level(String difficulty_level) {
		commonMetaDatas.put(CommonDCTerms.METADATA_DIFFICULTY_LEVEL, difficulty_level);
	}
	
	@XmlTransient
	public String getPublic_or_not() {
		return commonMetaDatas.get(CommonDCTerms.METADATA_PUBLIC_OR_NOT);	
	}
	
	public void setPublic_or_not(String public_or_not) {
		commonMetaDatas.put(CommonDCTerms.METADATA_PUBLIC_OR_NOT, public_or_not);
	}
	
	@XmlTransient
	public String getRelease_scope() {
		return commonMetaDatas.get(CommonDCTerms.METADATA_RELEASE_SCOPE);	
	}
	
	public void setRelease_scope(String release_scope) {
		commonMetaDatas.put(CommonDCTerms.METADATA_RELEASE_SCOPE, release_scope);
	}
	@XmlTransient
	public String getCopyright() {
		return commonMetaDatas.get(CommonDCTerms.METADATA_COPYRIGHT);	
	}
	
	public void setCopyright(String copyright) {
		commonMetaDatas.put(CommonDCTerms.METADATA_COPYRIGHT, copyright);
	}
	
	@XmlTransient
	public String getRights() {
		return commonMetaDatas.get(CommonDCTerms.METADATA_RIGHTS);	
	}
	
	public void setRights(String rights) {
		commonMetaDatas.put(CommonDCTerms.METADATA_RIGHTS, rights);
	}
	
	@XmlTransient
	public String getFormat() {
		return commonMetaDatas.get(CommonDCTerms.METADATA_FORMAT);	
	}
	
	public void setFormat(String format) {
		commonMetaDatas.put(CommonDCTerms.METADATA_FORMAT, format);
	}
	
	@XmlTransient
	public String getFileByte() {
		return commonMetaDatas.get(CommonDCTerms.METADATA_FILEBYTE);	
	}
	
	public void setFileByte(String fileByte) {
		commonMetaDatas.put(CommonDCTerms.METADATA_FILEBYTE, fileByte);
	}
	
	@XmlTransient
	public String getPublish_time() {
		return commonMetaDatas.get(CommonDCTerms.METADATA_PUBLISH_TIME);	
	}
	
	public void setPublish_time(String publish_time) {
		commonMetaDatas.put(CommonDCTerms.METADATA_PUBLISH_TIME, publish_time);
	}
	
	@XmlTransient
	public String getModified_time() {
		return commonMetaDatas.get(CommonDCTerms.METADATA_MODIFIED_TIME);	
	}
	
	public void setModified_time(String modified_time) {
		commonMetaDatas.put(CommonDCTerms.METADATA_MODIFIED_TIME, modified_time);
	}
	
	@XmlTransient
	public String getPublisher() {
		return commonMetaDatas.get(CommonDCTerms.METADATA_PUBLISHER);	
	}
	
	public void setPublisher(String publisher) {
		commonMetaDatas.put(CommonDCTerms.METADATA_PUBLISHER, publisher);
	}
	
	@XmlTransient
	public String getRegion() {
		return commonMetaDatas.get(CommonDCTerms.METADATA_REGION);	
	}
	
	public void setRegion(String region) {
		commonMetaDatas.put(CommonDCTerms.METADATA_REGION, region);
	}
	
	@XmlTransient
	public String getSchool() {
		return commonMetaDatas.get(CommonDCTerms.METADATA_SCHOOL);	
	}
	
	public void setSchool(String school) {
		commonMetaDatas.put(CommonDCTerms.METADATA_SCHOOL, school);
	}
	
	@XmlTransient
	public String getAddress() {
		return commonMetaDatas.get(CommonDCTerms.METADATA_ADDRESS);	
	}
	
	public void setAddress(String address) {
		commonMetaDatas.put(CommonDCTerms.METADATA_ADDRESS, address);
	}
	
	@XmlTransient
	public String getCreator() {
		return commonMetaDatas.get(CommonDCTerms.METADATA_CREATOR);	
	}
	
	public void setCreator(String creator) {
		commonMetaDatas.put(CommonDCTerms.METADATA_CREATOR, creator);
	}	
	
	@XmlTransient
	public String getContributor() {
		return commonMetaDatas.get(CommonDCTerms.METADATA_CONTRIBUTOR);	
	}
	
	public void setContributor(String contributor) {
		commonMetaDatas.put(CommonDCTerms.METADATA_CONTRIBUTOR, contributor);
	}
	
	@XmlTransient
	public String getSex() {
		return commonMetaDatas.get(CommonDCTerms.METADATA_SEX);	
	}
	
	public void setSex(String sex) {
		commonMetaDatas.put(CommonDCTerms.METADATA_SEX, sex);
	}
	
	@XmlTransient
	public String getBirthdate() {
		return commonMetaDatas.get(CommonDCTerms.METADATA_BIRTHDATE);	
	}
	
	public void setBirthdate(String birthdate) {
		commonMetaDatas.put(CommonDCTerms.METADATA_BIRTHDATE, birthdate);
	}
	
	@XmlTransient
	public String getEmail() {
		return commonMetaDatas.get(CommonDCTerms.METADATA_EMAIL);	
	}
	
	public void setEmail(String email) {
		commonMetaDatas.put(CommonDCTerms.METADATA_EMAIL, email);
	}
	
	@XmlTransient
	public String getCellphone_number() {
		return commonMetaDatas.get(CommonDCTerms.METADATA_CELLPHONE_NUMBER);	
	}
	
	public void setCellphone_number(String cellphone_number) {
		commonMetaDatas.put(CommonDCTerms.METADATA_CELLPHONE_NUMBER, cellphone_number);
	}
	
	@XmlTransient
	public String getProfessional_title() {
		return commonMetaDatas.get(CommonDCTerms.METADATA_PROFESSIONAL_TITLE);	
	}
	
	public void setProfessional_title(String professional_title) {
		commonMetaDatas.put(CommonDCTerms.METADATA_PROFESSIONAL_TITLE, professional_title);
	}
	
	@XmlTransient
	public String getRating() {
		return commonMetaDatas.get(CommonDCTerms.METADATA_RATING);	
	}
	
	public void setRating(String rating) {
		commonMetaDatas.put(CommonDCTerms.METADATA_RATING, rating);
	}
	
	@XmlTransient
	public String getReviewer() {
		return commonMetaDatas.get(CommonDCTerms.METADATA_REVIEWER);	
	}
	
	public void setReviewer(String reviewer) {
		commonMetaDatas.put(CommonDCTerms.METADATA_REVIEWER, reviewer);
	}
	
	@XmlTransient
	public String getReview() {
		return commonMetaDatas.get(CommonDCTerms.METADATA_REVIEW);	
	}
	
	public void setReview(String review) {
		commonMetaDatas.put(CommonDCTerms.METADATA_REVIEW, review);
	}
	
	@XmlTransient
	public String getEvaluated_date() {
		return commonMetaDatas.get(CommonDCTerms.METADATA_EVALUATED_DATE);	
	}
	
	public void setEvaluated_date(String evaluated_date) {
		commonMetaDatas.put(CommonDCTerms.METADATA_EVALUATED_DATE, evaluated_date);
	} 
	
	@XmlTransient
	public String getStatus() {
		return commonMetaDatas.get(CommonDCTerms.METADATA_STATUS);	
	}
	
	public void setStatus(String status) {
		commonMetaDatas.put(CommonDCTerms.METADATA_STATUS, status);
	}
	@XmlTransient
	public String getSource() {
		return commonMetaDatas.get(CommonDCTerms.METADATA_SOURCE);	
	}
	
	public void setSource(String source) {
		commonMetaDatas.put(CommonDCTerms.METADATA_SOURCE, source);
	}
	public Map<String, String> getCommonMetaDatas() {
		return commonMetaDatas;	
	}
	
	public void setCommonMetaDatas(Map<String, String> commonMetaDatas) {
		this.commonMetaDatas = commonMetaDatas;	
	}
	
	public void putCommonMetaData(String key, String value) {
		commonMetaDatas.put(key, value);	
	}
	
	@XmlTransient
	public String getCommonMetaValue(String key) {
		return commonMetaDatas.get(key);
	}
	
	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}
	
	@XmlTransient
	public String getStatusDesc(){
		return ResourceStatus.getValueByKey(getStatus());
	}
}
