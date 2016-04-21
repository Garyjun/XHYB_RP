package com.brainsoon.common.dao;

import com.brainsoon.common.exception.DaoException;
import com.brainsoon.resrelease.po.ResReleaseDetail;

/**
 * @ClassName: IBaseJdbcDao
 * @Description: 
 * @author xiehewei
 * @date 2014年11月25日 下午2:21:52
 *
 */
public interface IBaseJdbcDao {

	/**
	 * 执行更新SQL语句
	 * @param sql
	 * @return    返回受影响的数据行数
	 * @throws DaoException
	 */
	public int update(ResReleaseDetail detail,int count,int total) throws DaoException;
}
