package com.brainsoon.resrelease.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.brainsoon.common.dao.IBaseDao;

@Repository
public interface IResOrderDao extends IBaseDao {

	public Map<String,List<Object[]>> caculateResource(String orderIds);
}
