package com.brainsoon.semantic.ontology.model;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.brainsoon.semantic.vocabulary.ExtendDCTerms;

public class ExtendMetaData {
	private String objectId;
	
	private String resource;
	
	protected Map<String, String> extendMetaDatas = new HashMap<String, String>();
	
	public ExtendMetaData() {
		extendMetaDatas.put("mapName", "extendMetaDatas");		
	}
	
	public String getObjectId() {
		return objectId;	
	}
	
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}	
	
	@XmlTransient
	public String getSampling() {
		return extendMetaDatas.get(ExtendDCTerms.METADATA_SAMPLING);	
	}
	
	public void setSampling(String sampling) {
		extendMetaDatas.put(ExtendDCTerms.METADATA_SAMPLING, sampling);
	}
	@XmlTransient
	public String getAcoustic_channel() {
		return extendMetaDatas.get(ExtendDCTerms.METADATA_ACOUSTIC_CHANNEL);	
	}
	
	public void setAcoustic_channel(String acoustic_channel) {
		extendMetaDatas.put(ExtendDCTerms.METADATA_ACOUSTIC_CHANNEL, acoustic_channel);
	}
	@XmlTransient
	public String getDuration() {
		return extendMetaDatas.get(ExtendDCTerms.METADATA_DURATION);	
	}
	
	public void setDuration(String duration) {
		extendMetaDatas.put(ExtendDCTerms.METADATA_DURATION, duration);
	}
	@XmlTransient
	public String getFrame_count() {
		return extendMetaDatas.get(ExtendDCTerms.METADATA_FRAME_COUNT);	
	}
	
	public void setFrame_count(String frame_count) {
		extendMetaDatas.put(ExtendDCTerms.METADATA_FRAME_COUNT, frame_count);
	}
	@XmlTransient
	public String getSpecification() {
		return extendMetaDatas.get(ExtendDCTerms.METADATA_SPECIFICATION);	
	}
	
	public void setSpecification(String specification) {
		extendMetaDatas.put(ExtendDCTerms.METADATA_SPECIFICATION, specification);
	}
	@XmlTransient
	public String getPlatform() {
		return extendMetaDatas.get(ExtendDCTerms.METADATA_PLATFORM);	
	}
	
	public void setPlatform(String platform) {
		extendMetaDatas.put(ExtendDCTerms.METADATA_PLATFORM, platform);
	}
	@XmlTransient
	public String getRequirement() {
		return extendMetaDatas.get(ExtendDCTerms.METADATA_REQUIREMENT);	
	}
	
	public void setRequirement(String requirement) {
		extendMetaDatas.put(ExtendDCTerms.METADATA_REQUIREMENT, requirement);
	}
	@XmlTransient
	public String getSoftware_version() {
		return extendMetaDatas.get(ExtendDCTerms.METADATA_SOFTWARE_VERSION);	
	}
	
	public void setSoftware_version(String software_version) {
		extendMetaDatas.put(ExtendDCTerms.METADATA_SOFTWARE_VERSION, software_version);
	}
	@XmlTransient
	public String getTeaching_type() {
		return extendMetaDatas.get(ExtendDCTerms.METADATA_TEACHING_TYPE);	
	}
	
	public void setTeaching_type(String teaching_type) {
		extendMetaDatas.put(ExtendDCTerms.METADATA_TEACHING_TYPE, teaching_type);
	}
	@XmlTransient
	public String getResolution() {
		return extendMetaDatas.get(ExtendDCTerms.METADATA_RESOLUTION);	
	}
	
	public void setResolution(String resolution) {
		extendMetaDatas.put(ExtendDCTerms.METADATA_RESOLUTION, resolution);
	}
	@XmlTransient
	public String getCriterion() {
		return extendMetaDatas.get(ExtendDCTerms.METADATA_CRITERION);	
	}
	
	public void setCriterion(String criterion) {
		extendMetaDatas.put(ExtendDCTerms.METADATA_CRITERION, criterion);
	}
	@XmlTransient
	public String getTypical_testing_time() {
		return extendMetaDatas.get(ExtendDCTerms.METADATA_TYPICAL_TESTING_TIME);	
	}
	
	public void setTypical_testing_time(String typical_testing_time) {
		extendMetaDatas.put(ExtendDCTerms.METADATA_TYPICAL_TESTING_TIME, typical_testing_time);
	}
	@XmlTransient
	public String getScore() {
		return extendMetaDatas.get(ExtendDCTerms.METADATA_SCORE);	
	}
	
	public void setScore(String score) {
		extendMetaDatas.put(ExtendDCTerms.METADATA_SCORE, score);
	}
	@XmlTransient
	public String getAuditor() {
		return extendMetaDatas.get(ExtendDCTerms.METADATA_AUDITOR);	
	}
	
	public void setAuditor(String auditor) {
		extendMetaDatas.put(ExtendDCTerms.METADATA_AUDITOR, auditor);
	}
	@XmlTransient
	public String getAudit_date() {
		return extendMetaDatas.get(ExtendDCTerms.METADATA_AUDIT_DATE);	
	}
	
	public void setAudit_date(String audit_date) {
		extendMetaDatas.put(ExtendDCTerms.METADATA_AUDIT_DATE, audit_date);
	}
	@XmlTransient
	public String getDifficulty() {
		return extendMetaDatas.get(ExtendDCTerms.METADATA_DIFFICULTY);	
	}
	
	public void setDifficulty(String difficulty) {
		extendMetaDatas.put(ExtendDCTerms.METADATA_DIFFICULTY, difficulty);
	}
	@XmlTransient
	public String getDifferentiate() {
		return extendMetaDatas.get(ExtendDCTerms.METADATA_DIFFERENTIATE);	
	}
	
	public void setDifferentiate(String differentiate) {
		extendMetaDatas.put(ExtendDCTerms.METADATA_DIFFERENTIATE, differentiate);
	}
	
	@XmlTransient
	public String getAlphabetTitle() {
		return extendMetaDatas.get(ExtendDCTerms.METADATA_ALPHABETTITLE);	
	}
	
	public void setAlphabetTitle(String alphabetTitle) {
		extendMetaDatas.put(ExtendDCTerms.METADATA_ALPHABETTITLE, alphabetTitle);
	}
	@XmlTransient
	public String getAltTitle() {
		return extendMetaDatas.get(ExtendDCTerms.METADATA_ALTTITLE);	
	}
	
	public void setAltTitle(String altTitle) {
		extendMetaDatas.put(ExtendDCTerms.METADATA_ALTTITLE, altTitle);
	}
	@XmlTransient
	public String getOtherTitle() {
		return extendMetaDatas.get(ExtendDCTerms.METADATA_OTHERTITLE);	
	}
	
	public void setOtherTitle(String otherTitle) {
		extendMetaDatas.put(ExtendDCTerms.METADATA_OTHERTITLE, otherTitle);
	}
	@XmlTransient
	public String getParTitle() {
		return extendMetaDatas.get(ExtendDCTerms.METADATA_PARTITLE);	
	}
	
	public void setParTitle(String parTitle) {
		extendMetaDatas.put(ExtendDCTerms.METADATA_PARTITLE, parTitle);
	}
	@XmlTransient
	public String getSerialname() {
		return extendMetaDatas.get(ExtendDCTerms.METADATA_SERIALNAME);	
	}
	
	public void setSerialname(String serialname) {
		extendMetaDatas.put(ExtendDCTerms.METADATA_SERIALNAME, serialname);
	}
	@XmlTransient
	public String getCbclass() {
		return extendMetaDatas.get(ExtendDCTerms.METADATA_OTHERTITLE);	
	}
	
	public void setCbclass(String cbclass) {
		extendMetaDatas.put(ExtendDCTerms.METADATA_CBCLASS, cbclass);
	}
	@XmlTransient
	public String getPages() {
		return extendMetaDatas.get(ExtendDCTerms.METADATA_PAGES);	
	}
	
	public void setPages(String pages) {
		extendMetaDatas.put(ExtendDCTerms.METADATA_PAGES, pages);
	}
	@XmlTransient
	public String getCip() {
		return extendMetaDatas.get(ExtendDCTerms.METADATA_CIP);	
	}
	
	public void setCip(String cip) {
		extendMetaDatas.put(ExtendDCTerms.METADATA_CIP, cip);
	}
	@XmlTransient
	public String getPprice() {
		return extendMetaDatas.get(ExtendDCTerms.METADATA_PPRICE);	
	}
	
	public void setPprice(String pprice) {
		extendMetaDatas.put(ExtendDCTerms.METADATA_PPRICE, pprice);
	}
	@XmlTransient
	public String getPrtcnt() {
		return extendMetaDatas.get(ExtendDCTerms.METADATA_PRTCNT);	
	}
	
	public void setPrtcnt(String prtcnt) {
		extendMetaDatas.put(ExtendDCTerms.METADATA_PRTCNT, prtcnt);
	}
	@XmlTransient
	public String getVolnum() {
		return extendMetaDatas.get(ExtendDCTerms.METADATA_VOLNUM);	
	}
	
	public void setVolnum(String volnum) {
		extendMetaDatas.put(ExtendDCTerms.METADATA_VOLNUM, volnum);
	}
	@XmlTransient
	public String getEdicnt() {
		return extendMetaDatas.get(ExtendDCTerms.METADATA_EDICNT);	
	}
	
	public void setEdicnt(String edicnt) {
		extendMetaDatas.put(ExtendDCTerms.METADATA_EDICNT, edicnt);
	}
	@XmlTransient
	public String getDctype() {
		return extendMetaDatas.get(ExtendDCTerms.METADATA_DCTYPE);	
	}
	
	public void setDctype(String dctype) {
		extendMetaDatas.put(ExtendDCTerms.METADATA_DCTYPE, dctype);
	}
	@XmlTransient
	public String getISBN() {
		return extendMetaDatas.get(ExtendDCTerms.METADATA_ISBN);	
	}
	
	public void setISBN(String ISBN) {
		extendMetaDatas.put(ExtendDCTerms.METADATA_ISBN, ISBN);
	}
	@XmlTransient
	public String getClb() {
		return extendMetaDatas.get(ExtendDCTerms.METADATA_CLB);	
	}
	
	public void setClb(String clb) {
		extendMetaDatas.put(ExtendDCTerms.METADATA_CLB, clb);
	}
	@XmlTransient
	public String getDcsubject() {
		return extendMetaDatas.get(ExtendDCTerms.METADATA_DCSUBJECT);	
	}
	
	public void setDcsubject(String dcsubject) {
		extendMetaDatas.put(ExtendDCTerms.METADATA_DCSUBJECT, dcsubject);
	}
	@XmlTransient
	public String getDcpublisher() {
		return extendMetaDatas.get(ExtendDCTerms.METADATA_DCPUBLISHER);	
	}
	
	public void setDcpublisher(String dcpublisher) {
		extendMetaDatas.put(ExtendDCTerms.METADATA_DCPUBLISHER, dcpublisher);
	}
	@XmlTransient
	public String getPublishdate() {
		return extendMetaDatas.get(ExtendDCTerms.METADATA_PUBLISHDATE);	
	}
	
	public void setPublishdate(String publishdate) {
		extendMetaDatas.put(ExtendDCTerms.METADATA_PUBLISHDATE, publishdate);
	}
	
	@XmlTransient
	public String getView_type() {
		return extendMetaDatas.get(ExtendDCTerms.METADATA_VIEW_TYPE);	
	}
	
	public void setView_type(String view_type) {
		extendMetaDatas.put(ExtendDCTerms.METADATA_VIEW_TYPE, view_type);
	}	
	@XmlTransient
	public String getView_value() {
		return extendMetaDatas.get(ExtendDCTerms.METADATA_VIEW_VALUE);	
	}
	
	public void setView_value(String view_value) {
		extendMetaDatas.put(ExtendDCTerms.METADATA_VIEW_VALUE, view_value);
	}
	@XmlTransient
	public String getDown_type() {
		return extendMetaDatas.get(ExtendDCTerms.METADATA_DOWN_TYPE);	
	}
	
	public void setDown_type(String down_type) {
		extendMetaDatas.put(ExtendDCTerms.METADATA_DOWN_TYPE, down_type);
	}
	@XmlTransient
	public String getDown_value() {
		return extendMetaDatas.get(ExtendDCTerms.METADATA_DOWN_VALUE);	
	}
	
	public void setDown_value(String down_value) {
		extendMetaDatas.put(ExtendDCTerms.METADATA_DOWN_VALUE, down_value);
	}
	
	public Map<String, String> getExtendMetaDatas() {
		return extendMetaDatas;	
	}
	
	public void putExtendMetaData(String key, String value) {
		extendMetaDatas.put(key, value);	
	}
	
	public void setExtendMetaDatas(Map<String, String> extendMetaDatas) {
		this.extendMetaDatas = extendMetaDatas;	
	}
	
	@XmlTransient
	public String getExtendMetaValue(String key) {
		return extendMetaDatas.get(key);
	}
	
	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

}
