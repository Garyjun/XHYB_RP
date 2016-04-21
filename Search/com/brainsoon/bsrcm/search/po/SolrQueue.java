package com.brainsoon.bsrcm.search.po;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.brainsoon.common.po.BaseHibernateObject;

@Entity
@Table(name = "solr_queue")
public class SolrQueue extends BaseHibernateObject {

	private static final long serialVersionUID = -2384568126530869332L;
	
	 //主键
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
	private Long id;
	 //资源标识
    @Column(name = "res_id")
	private String resId;
	 //索引动作(0创建索引，1删除索引)
    @Column
	private String actions;
	 //资源对象索引状态(0索引未创建,1索引已创建,2创建索引失败)
    @Column
	private String status;
	 //创建时间
    @Column(name = "create_Time")
	private Date createTime;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getResId() {
		return resId;
	}

	public void setResId(String resId) {
		this.resId = resId;
	}

	public String getStatus() {
		return status;
	}

	public String getActions() {
		return actions;
	}

	public void setActions(String actions) {
		this.actions = actions;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}


	public Serializable getObjectID() {
		return getId();
	}

	@Override
	public String getObjectDescription() {
		return "全文索引队列表";
	}

	@Override
	public String getEntityDescription() {
		return "全文索引队列表";
	}

}
