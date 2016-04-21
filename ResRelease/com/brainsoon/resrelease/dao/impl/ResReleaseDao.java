package com.brainsoon.resrelease.dao.impl;


import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.brainsoon.common.dao.hibernate.BaseHibernateDao;
import com.brainsoon.resrelease.dao.IResReleaseDao;
import com.brainsoon.resrelease.po.ResFileRelation;
import com.brainsoon.resrelease.po.ResReleaseDetail;
@Repository
public class ResReleaseDao extends BaseHibernateDao implements IResReleaseDao {

	@Override
	public List<ResReleaseDetail> getResReleaseDetailByRelIdAndStatus(Long relId,
			String status) {
		List<ResReleaseDetail> list = new ArrayList<ResReleaseDetail>();
		String hql = " from ResReleaseDetail where "
				+ "releaseId="+relId+" and status='"+status+"'";
		Query query = getSession().createQuery(hql);
		list = query.list();
		return list;
	}

	@Override
	public ResReleaseDetail getResReleaseDetailByRelIdAndResId(Long relId,
			String resId) {
		ResReleaseDetail detail = null;
		String hql = " from ResReleaseDetail where "
				+ "releaseId="+relId+" and resId='"+resId+"'";
		Query query = getSession().createQuery(hql);
		detail = (ResReleaseDetail) query.uniqueResult();
		return detail;
	}

	@Override
	public List<ResFileRelation> getFileListByOrderId(Long orderId){
		String hql = " from ResFileRelation where orderId=" + orderId;
		List<ResFileRelation> list = query(hql);
		return list;
	}
}
