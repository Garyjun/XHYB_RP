package com.brainsoon.system.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.brainsoon.common.po.BaseHibernateObject;
import com.brainsoon.system.model.User;
/**
 * BSRCM应用com.brainsoon.bsrcm.dcore.po.SysMetadataType.java 创建时间：2011-12-7 创建者：
 * liusy DcType TODO
 * 
 */
@Entity
@Table(name = "res_target")
public class ResTarget extends BaseHibernateObject {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "targetId", nullable = false)
    private Long id;
    @Column
    private String name;
    @Column
    private Integer status;
    @Column
    private Integer type;
    @ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn (name= "user_id" )
    private User user;
   /* @Column
    private Integer user_id;*/
    @Column
    private String description;
    @Column
    private int source;
    @Column
    private Integer platformId;
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Integer getPlatformId() {
		return platformId;
	}

	public void setPlatformId(Integer platformId) {
		this.platformId = platformId;
	}

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
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

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
