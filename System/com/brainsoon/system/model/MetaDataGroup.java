package com.brainsoon.system.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.brainsoon.common.po.BaseHibernateObject;

@Entity
@Table(name = "metaDataGroup")
public class MetaDataGroup extends BaseHibernateObject {
	 private static final long serialVersionUID = 4858001351954128087L;
	    @Id
	    @GeneratedValue(strategy = GenerationType.AUTO)
	    @Column(name = "id", nullable = false)
	    private Long id;
	    @ManyToOne(targetEntity=InDefinition.class,cascade=CascadeType.MERGE,fetch=FetchType.EAGER)
	    @Fetch(FetchMode.JOIN)
	    @JoinColumn(name="pid")
	    private InDefinition definition;
	    public InDefinition getDefinition() {
			return definition;
		}
		public void setDefinition(InDefinition definition) {
			this.definition = definition;
		}
		@Column
	    private Long version;
		@Column
		private String nameMust;
		@Column
		private String nameMay;
		@Column
		private String nameMustCN;
		@Column
		private String nameMayCN;
        @Column
	    private String nameExpand;
        @Column
		private int platformId;
        
		public int getPlatformId() {
			return platformId;
		}
		public void setPlatformId(int platformId) {
			this.platformId = platformId;
		}
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getNameMustCN() {
			return nameMustCN;
		}
		public void setNameMustCN(String nameMustCN) {
			this.nameMustCN = nameMustCN;
		}
		public String getNameMayCN() {
			return nameMayCN;
		}
		public void setNameMayCN(String nameMayCN) {
			this.nameMayCN = nameMayCN;
		}
		public Long getVersion() {
			return version;
		}
		public void setVersion(Long version) {
			this.version = version;
		}
		public String getNameMust() {
			return nameMust;
		}
		public void setNameMust(String nameMust) {
			this.nameMust = nameMust;
		}
		public String getNameMay() {
			return nameMay;
		}
		public void setNameMay(String nameMay) {
			this.nameMay = nameMay;
		}
		public String getNameExpand() {
			return nameExpand;
		}
		public void setNameExpand(String nameExpand) {
			this.nameExpand = nameExpand;
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
