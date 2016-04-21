package com.brainsoon.system.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.brainsoon.common.po.BaseHibernateObject;

@Entity
@Table(name = "in_definit")
public class InDefinition extends BaseHibernateObject {
	private static final long serialVersionUID = 4858001351954128087L;
	    @Id
	    @GeneratedValue(strategy = GenerationType.AUTO)
	    @Column(name = "id", nullable = false)
	    private Long id;
		@Column
	    private String name;
		@Column
	    private String nameAbbr;
	    @Column
	    private String status;
	    @Column
	    private String url;
	    @Column
		private int platformId;
		/*@OneToMany(targetEntity=DictValue.class,cascade=CascadeType.ALL)
	    @Fetch(FetchMode.JOIN)
	    @JoinColumn(name="pid",updatable=false)*/
	    
		@Override
		public String getObjectDescription() {
			// TODO Auto-generated method stub
			return null;
		}

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

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getNameAbbr() {
			return nameAbbr;
		}

		public void setNameAbbr(String nameAbbr) {
			this.nameAbbr = nameAbbr;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		@Override
		public String getEntityDescription() {
			// TODO Auto-generated method stub
			return null;
		}
}
