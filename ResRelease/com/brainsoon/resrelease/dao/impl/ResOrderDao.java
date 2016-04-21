package com.brainsoon.resrelease.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.brainsoon.common.dao.hibernate.BaseHibernateDao;
import com.brainsoon.resrelease.dao.IResOrderDao;
import com.brainsoon.resrelease.po.ResFileRelation;
import com.brainsoon.semantic.ontology.model.Ca;
@Repository
public class ResOrderDao extends BaseHibernateDao implements IResOrderDao {

	@SuppressWarnings("unchecked")
	@Override
	public Map<String,List<Object[]>> caculateResource(String orderIds) {
		Map<String,List<Object[]>> map = new HashMap<String,List<Object[]>>();
		List<Object[]> list = new ArrayList<Object[]>();
		String str = " module, type, versionName,"
				+ " educational_phase_name, subjectName, gradeName, fasciculeName ";
		for(String orderId : orderIds.split(",")){
			String hql = " from res_order_detail where orderId=" + Long.valueOf(orderId) + " group by "+str+" order by c";
			String counthql = "select "+str+",  count(objectId) c "+hql;
			list = getSession().createQuery(counthql).list();
			map.put(orderId, list);
		}
		return map;
	}
	
}
