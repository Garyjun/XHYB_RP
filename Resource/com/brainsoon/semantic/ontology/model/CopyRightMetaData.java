package com.brainsoon.semantic.ontology.model;

import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;
@XmlRootElement
@JsonTypeInfo(use = Id.NONE)
public class CopyRightMetaData {
	private String objectId;
	private String ownership;	  	//文字权属所有人
	private String permitSort;	  	//文字授权许可权力
	private String contractCode;	//文字授权合同编码
	private String authEffeDate;	//文字授权生效时间
	private String autherName;	 	//文字授权人
	private String copyright;	  	//音频版权归属
	private String licenStartTime;	//音频版权生效时间
	private String licenEndTime;   	//授权终止时间
	private String language;	  	//授权语种
	private String licensArear;	 	//授权区域
	private String liabilityMode;	//责任方式
	private String authEndDate;	  	//授权终止时间
	private String authStartDate; 	//授权起始时间
	private String licensRange;   	//授权范围
	private String contractTimeline;//合同时限
	private String licensLanguage;  //授权语种
	private String isExclusive;   	//是否独家
	private String isDelegate;   	//有无转授
	private String otherLimited;   	//其他限定
	private String isForSet;   	    //是否供集
	private String isInforTranRight;//有无信息网络传播权
	private String channelAndDivide;//第三方渠道及分成
	private String intoProportion;  //分成比例
	private String tryReadProportion;//试读比例
	private String video;   		//影视
	private String ctsuitTerminal;
	private String identifier;
	private String title;
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	public String getOwnership() {
		return ownership;
	}
	public void setOwnership(String ownership) {
		this.ownership = ownership;
	}
	public String getPermitSort() {
		return permitSort;
	}
	public void setPermitSort(String permitSort) {
		this.permitSort = permitSort;
	}
	public String getContractCode() {
		return contractCode;
	}
	public void setContractCode(String contractCode) {
		this.contractCode = contractCode;
	}
	public String getAuthEffeDate() {
		return authEffeDate;
	}
	public void setAuthEffeDate(String authEffeDate) {
		this.authEffeDate = authEffeDate;
	}
	public String getAutherName() {
		return autherName;
	}
	public void setAutherName(String autherName) {
		this.autherName = autherName;
	}
	public String getCopyright() {
		return copyright;
	}
	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}
	public String getLicenStartTime() {
		return licenStartTime;
	}
	public void setLicenStartTime(String licenStartTime) {
		this.licenStartTime = licenStartTime;
	}
	public String getLicenEndTime() {
		return licenEndTime;
	}
	public void setLicenEndTime(String licenEndTime) {
		this.licenEndTime = licenEndTime;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getLicensArear() {
		return licensArear;
	}
	public void setLicensArear(String licensArear) {
		this.licensArear = licensArear;
	}
	public String getLiabilityMode() {
		return liabilityMode;
	}
	public void setLiabilityMode(String liabilityMode) {
		this.liabilityMode = liabilityMode;
	}
	public String getAuthEndDate() {
		return authEndDate;
	}
	public void setAuthEndDate(String authEndDate) {
		this.authEndDate = authEndDate;
	}
	public String getAuthStartDate() {
		return authStartDate;
	}
	public void setAuthStartDate(String authStartDate) {
		this.authStartDate = authStartDate;
	}
	public String getLicensRange() {
		return licensRange;
	}
	public void setLicensRange(String licensRange) {
		this.licensRange = licensRange;
	}
	public String getContractTimeline() {
		return contractTimeline;
	}
	public void setContractTimeline(String contractTimeline) {
		this.contractTimeline = contractTimeline;
	}
	public String getLicensLanguage() {
		return licensLanguage;
	}
	public void setLicensLanguage(String licensLanguage) {
		this.licensLanguage = licensLanguage;
	}
	public String getIsExclusive() {
		return isExclusive;
	}
	public void setIsExclusive(String isExclusive) {
		this.isExclusive = isExclusive;
	}
	public String getIsDelegate() {
		return isDelegate;
	}
	public void setIsDelegate(String isDelegate) {
		this.isDelegate = isDelegate;
	}
	public String getOtherLimited() {
		return otherLimited;
	}
	public void setOtherLimited(String otherLimited) {
		this.otherLimited = otherLimited;
	}
	public String getIsForSet() {
		return isForSet;
	}
	public void setIsForSet(String isForSet) {
		this.isForSet = isForSet;
	}
	public String getIsInforTranRight() {
		return isInforTranRight;
	}
	public void setIsInforTranRight(String isInforTranRight) {
		this.isInforTranRight = isInforTranRight;
	}
	public String getChannelAndDivide() {
		return channelAndDivide;
	}
	public void setChannelAndDivide(String channelAndDivide) {
		this.channelAndDivide = channelAndDivide;
	}
	public String getIntoProportion() {
		return intoProportion;
	}
	public void setIntoProportion(String intoProportion) {
		this.intoProportion = intoProportion;
	}
	public String getTryReadProportion() {
		return tryReadProportion;
	}
	public void setTryReadProportion(String tryReadProportion) {
		this.tryReadProportion = tryReadProportion;
	}
	public String getVideo() {
		return video;
	}
	public void setVideo(String video) {
		this.video = video;
	}
	public String getCtsuitTerminal() {
		return ctsuitTerminal;
	}
	public void setCtsuitTerminal(String ctsuitTerminal) {
		this.ctsuitTerminal = ctsuitTerminal;
	}
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
}
