package com.brainsoon.resource.po;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.brainsoon.common.po.BaseHibernateObject;
import com.brainsoon.common.support.GlobalDataCacheMap;
import com.brainsoon.resrelease.po.ProdParamsTemplate;
import com.brainsoon.resrelease.support.ResReleaseConstant;
import com.brainsoon.system.model.User;
/**
 * @ClassName: SubjectStore
 * @Description: TODO
 * @author
 * @date 2015年9月24日 上午11:09:04
 *
 */
@Entity
@Table(name="subject_store")
public class SubjectStore extends BaseHibernateObject implements java.io.Serializable{
	private static final long serialVersionUID = 8068568950238260320L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id",nullable=false)
	private Long id;
	@Column(name="name")
	private String name;
	@Column(name="name_en")
	private String nameEn;
	@Column
	private String logo;
	@Column(name="trade")
	private String trade;
	@Column
	private String subject;
	@Column
	private String bookman;
	@Column
	private String audience;
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn (name= "template_id" )
	private ProdParamsTemplate template;
	@Column
	private String moduleName;
	@Column(name = "platformId")
	private int platformId;
	@Column(name="store_type")
	private String storeType;
	@Column
	private String keyword;
	@Column(name="collection_start")
	private int collectionStart;
	@Column(name="collection_end")
	private int collectionEnd;
	@Column(name="language")
	private String language;
	@Column
	private String synopsis;
	@Column
	private String subLibclassify;
	@Column
	private String status;
	
	@ManyToOne(fetch=FetchType.EAGER)   
	@JoinColumn (name= "auditor" )
	private User auditor;
	
	@Column(name = "audit_time")
	private Date auditTime;
	
	@Column(name="audit_msg")
	private String auditMsg;
	@Column(name="create_time")
	private Date createTime;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="create_user")
	private User createUser;//创建人
	
	@Column(name="update_time")
	private Date updateTime;
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="update_user")
	private User updateUser;//修改人
	@Column
	private int pid;
	@Column
	private String restype;
	//生成元数据excel是否成功：0:否 1:是
	@Column(name = "excel_ok_status")
	private Integer excelOkStatus = 0;
	@Column(name = "json_ok_status")
	private Integer jsonOkStatus = 0;
	//备注（是否成功创建元数据Excel、资源清单Xml）
	@Column(name = "remark")
	private String remark;
	@Column(name = "processremark")
	private String processremark;
	@Transient
	private String tradeDesc;
	@Transient
	private String statusDesc;
	@Transient
	private String languageDesc;
	@Transient
	private String subjectDesc;
	@Transient
	private String bookmanDesc;
	@Transient
	private String storetypeDesc;
	@Transient
	private String audienceDesc;
	@Transient
	private String subLibclassifyDesc;
	//行业转换
	public String getTradeDesc() throws Throwable {
		String map =GlobalDataCacheMap.getNameValueWithNameByKeyAndChildKey("trade", getTrade());
		return map;
	}
	//状态转换
	public String getStatusDesc() {
		return ResReleaseConstant.OrderStatus.getValueByKey(status);
	}
	//语言转换
	public String getLanguageDesc() throws Exception{
		String map =GlobalDataCacheMap.getNameValueWithNameByKeyAndChildKey("relation_language", getLanguage());
		/*IDictNameService dictNameService = (IDictNameService)BeanFactoryUtil.getBean("dictNameService");
		String map = dictNameService.getDictMapByName("授权语言").get(getLanguage());*/
		return map;
	}
	//学科转换
	public String getSubjectDesc() throws Exception{
		/*IDictNameService dictNameService = (IDictNameService)BeanFactoryUtil.getBean("dictNameService");
		String map = dictNameService.getDictMapByName("学科").get(getSubject());*/
		String map =GlobalDataCacheMap.getNameValueWithNameByKeyAndChildKey("subject", getSubject());
		return map;
	}
	//出版商转换
	public String getBookmanDesc() throws Exception{
		/*IDictNameService dictNameService = (IDictNameService)BeanFactoryUtil.getBean("dictNameService");
		String map = dictNameService.getDictMapByName("出版社").get(getBookman());*/
		String map =GlobalDataCacheMap.getNameValueWithNameByKeyAndChildKey("presshose", getBookman());
		return map;
	}
	//资源类别转换
	public String getStoretypeDesc() throws Exception{
		/*IDictNameService dictNameService = (IDictNameService)BeanFactoryUtil.getBean("dictNameService");
		String map = dictNameService.getDictMapByName("库类别").get(getStoreType());*/
		String map =GlobalDataCacheMap.getNameValueWithNameByKeyAndChildKey("kuCategory", getStoreType());
		return map;
	}
	//受众类别转换
	public String getAudienceDesc() throws Exception{
		/*IDictNameService dictNameService = (IDictNameService)BeanFactoryUtil.getBean("dictNameService");
		String map = dictNameService.getDictMapByName("受众类别").get(getAudience());*/
		String map =GlobalDataCacheMap.getNameValueWithNameByKeyAndChildKey("audience", getAudience());
		return map;
	}
	//主题库类别
	public String getSubLibclassifyDesc() throws Exception{
		String map =GlobalDataCacheMap.getNameValueWithNameByKeyAndChildKey("ZTKtypes", getSubLibclassify());
		return map;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	
	public String getSubLibclassify() {
		return subLibclassify;
	}
	public void setSubLibclassify(String subLibclassify) {
		this.subLibclassify = subLibclassify;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNameEn() {
		return nameEn;
	}
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	
	
	public String getAudience() {
		return audience;
	}
	public void setAudience(String audience) {
		this.audience = audience;
	}
	public String getTrade() {
		return trade;
	}
	public void setTrade(String trade) {
		this.trade = trade;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public void setBookman(String bookman) {
		this.bookman = bookman;
	}
	
	public String getBookman() {
		return bookman;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public int getCollectionStart() {
		return collectionStart;
	}
	public void setCollectionStart(int collectionStart) {
		this.collectionStart = collectionStart;
	}
	public int getCollectionEnd() {
		return collectionEnd;
	}
	public void setCollectionEnd(int collectionEnd) {
		this.collectionEnd = collectionEnd;
	}
	
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getSynopsis() {
		return synopsis;
	}
	public void setSynopsis(String synopsis) {
		this.synopsis = synopsis;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getAuditMsg() {
		return auditMsg;
	}
	public void setAuditMsg(String auditMsg) {
		this.auditMsg = auditMsg;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public void setNameEn(String nameEn) {
		this.nameEn = nameEn;
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
	public User getCreateUser() {
		return createUser;
	}
	public void setCreateUser(User createUser) {
		this.createUser = createUser;
	}
	public User getUpdateUser() {
		return updateUser;
	}
	public void setUpdateUser(User updateUser) {
		this.updateUser = updateUser;
	}
	public String getStoreType() {
		return storeType;
	}
	public void setStoreType(String storeType) {
		this.storeType = storeType;
	}
	public String getRestype() {
		return restype;
	}
	public void setRestype(String restype) {
		this.restype = restype;
	}
	public ProdParamsTemplate getTemplate() {
		return template;
	}
	public void setTemplate(ProdParamsTemplate template) {
		this.template = template;
	}
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public int getPlatformId() {
		return platformId;
	}
	public void setPlatformId(int platformId) {
		this.platformId = platformId;
	}
	public Integer getExcelOkStatus() {
		return excelOkStatus;
	}
	public void setExcelOkStatus(Integer excelOkStatus) {
		this.excelOkStatus = excelOkStatus;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Integer getJsonOkStatus() {
		return jsonOkStatus;
	}
	public void setJsonOkStatus(Integer jsonOkStatus) {
		this.jsonOkStatus = jsonOkStatus;
	}
	public User getAuditor() {
		return auditor;
	}
	public void setAuditor(User auditor) {
		this.auditor = auditor;
	}
	public Date getAuditTime() {
		return auditTime;
	}
	public void setAuditTime(Date auditTime) {
		this.auditTime = auditTime;
	}
	public String getProcessremark() {
		return processremark;
	}
	public void setProcessremark(String processremark) {
		this.processremark = processremark;
	}
	
}
