package com.brainsoon.common.service.impl;
import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brainsoon.appframe.query.Operator;
import com.brainsoon.appframe.query.QueryConditionItem;
import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.query.QuerySortItem;
import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.common.dao.IBaseDao;
import com.brainsoon.common.exception.DaoException;
import com.brainsoon.common.pagination.PageInfo;
import com.brainsoon.common.po.BaseHibernateObject;
import com.brainsoon.common.service.IBaseService;

/**
 * 
 * @ClassName: BaseService 
 * @Description:  
 */
@Service
public  class BaseService implements IBaseService {
	protected  Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
    protected IBaseDao baseDao;
	
	/**
	 * 获取session
	 * @return
	 */
	public Session getSession(){
		return baseDao.getSession();
	}
	
	/** 
     * 关闭session连接 
     * */  
    public void closeSession(){
    	baseDao.closeSession();
    }
    
    /**
	 * 持久化
	 * @throws DaoException
	 */
	public void flush() throws DaoException{
		baseDao.flush();
	}

	/**
	 * 创建Hibernate的PO对象，并且持久化
	 * 
	 * @param obj  要持久化的PO对象
	 * @return 持久化后的PO对象，对于利用数据库提供主键生成的PO对象，可以通过返回值获得其主键OID
	 * @throws DaoException
	 */
	public BaseHibernateObject create(BaseHibernateObject obj) throws DaoException{
		return baseDao.create(obj);
	}

	/**
	 * 更新Hibernate的PO对象
	 * 
	 * @param obj
	 * @throws DaoException
	 */
	public void update(BaseHibernateObject obj) throws DaoException{
		baseDao.update(obj);
	}

	/**
	 * 物理删除Hibernate的PO对象
	 * 
	 * @param obj
	 * @throws DaoException
	 */
	public void delete(BaseHibernateObject obj) throws DaoException{
		baseDao.delete(obj);
	}

	/**
	 * 根据主键删除PO对象
	 * 
	 * @param poClass  PO类
	 * @param oid 待删除对象的主键
	 */
	public void delete(Class poClass, Serializable oid) throws DaoException{
		baseDao.delete(poClass, oid);
	}
	/**
	 * 根据以,分割的主键串,批量删除PO对象
	 * @param poClass
	 * @param ids
	 */
    public void delete(Class poClass,String ids) throws DaoException{
    	baseDao.delete(poClass, ids);
    }
	/**
	 * 根据HQL查询语句，返回符合条件的PO对象列表；
	 * 如果使用此方法，请注意保证HQL语法正确！
	 * @param hql  符合HQL规范的查询语句
	 * @return 符合条件的PO对象列表
	 * @throws DaoException
	 */
	public List query(String hql) throws DaoException{
		return baseDao.query(hql);
	}
	
	/**
	 * 根据hql查询对象,一个参数及其值查询对象 
	 * 
	 * @param hql
	 * @param paramName
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public List query(String hql, String paramName, Object value)throws DaoException {
		return baseDao.query(hql, paramName, value);
	}
	
	/**
	 * 带参数的hql查询,参数格式参考方法executeUpdate
	 * @param hql
	 * @param parameters
	 * @return
	 * @throws DaoException
	 */
	public List query(String hql,Map<String, Object> parameters) throws DaoException{
		return baseDao.query(hql, parameters);
	}
	
	/**
	 * 支持参数的hql批量操作,可批量执行update、delete和inserte操作。
	 * @param executeHql hql脚本，参数变量名为规则为":变量名"，如：":newName"
	 * @param parameters 参数集合，元素key为参数变量名，value为参数变量值
	 * @return 操作的记录条数
	 * @throws DaoException
	 */
	public int executeUpdate(String executeHql, Map<String, Object> parameters) throws DaoException{
		return baseDao.executeUpdate(executeHql, parameters);
	}
		
	/**
	 * 加载指定表的所有数据；本方法谨慎使用，建议只对常量表使用！
	 * @param poClass
	 * @return
	 */
	public List loadAll(Class poClass){
		return baseDao.loadAll(poClass);
	}
	/**
	 * 根据数据库表主键，检索唯一的PO对象
	 * @param oid
	 * @return  如果没有符合条件的PO对象，则返回null
	 * @throws DaoException
	 */
	public BaseHibernateObject getByPk(Class poClass,Serializable oid)throws DaoException{
		return baseDao.getByPk(poClass, oid);
	}
	
	/**
	 * 根据数据库表主键，检索唯一的PO对象
	 * @param poClass
	 * @param oid
	 * @return  如果没有符合条件的PO对象，则抛出对象不存在的异常ObjectNotFoundException
	 * @throws DaoException
	 */
	public BaseHibernateObject load(Class poClass,Serializable oid)throws DaoException{
		return baseDao.load(poClass, oid);
	}
	/**
	 * 检查是否存在满足主键条件的PO对象
	 * @param poClass
	 * @param oid
	 * @return
	 * @throws DaoException
	 */
	public boolean exist(Class poClass,Serializable oid) throws DaoException{
		return baseDao.exist(poClass, oid);
	}
	/**
	 * 检查是否满足主键条件的PO对象不存在
	 * @param poClass
	 * @param oid
	 * @return
	 * @throws DaoException
	 */
	public boolean notExist(Class poClass,Serializable oid) throws DaoException{
		return baseDao.notExist(poClass, oid);
	}
	/**
	 * 迫使 session 的缓存与数据库同步
	 * 当缓存中的 对象 与数据库不同步，且 想取得数据库中的数据的时候，可调用此方法
	 * 慎用！！！
	 * @param obj
	 * @throws DaoException
	 */
	public void refresh(BaseHibernateObject obj) throws DaoException{
		baseDao.refresh(obj);
	}
	
	public void saveOrUpdate(BaseHibernateObject o) {
		baseDao.saveOrUpdate(o);
	}
	
	/**
	 * 分页查询
	 * 
	 * @param hql
	 * @param pageBean
	 * @param params
	 * @return
	 */
	public List query4Page(String hql, String countHql, PageInfo pageInfo) {
		return baseDao.query4Page(hql, countHql, pageInfo);
	}
	
	/**
	 * 分页查询
	 * 
	 * @param hql
	 * @param pageInfo
	 * @param params
	 * @return
	 */
	public List query4Page(String hql, String countHql, PageInfo pageInfo,Map<String, Object> params){
		return baseDao.query4Page(hql, countHql, pageInfo, params);
	}
	
	/**
	 * 由sql查询对象
	 * 
	 * @param sql
	 * @return
	 * @throws Exception
	 */

	public List<Object> queryBySql(String sql,Class clas) throws DaoException{
		return baseDao.queryBySql(sql, clas);
	}
	
	/**
	 * 根据数据库SQL语句查询
	 * @param sql	 eg."select a.*,b.NAME from a,b where a.ID = b.ID"
	 * @param returnMaps 输出列和类型的对应Map.key:SQL中输出的字段名称,value:字段对应的实体class或org.hibernate.type.Type eg.HashMap<"a", A>
	 * @return
	 */
	
	public List<Map> queryBySQL(String sql,Map<String, Object> returnMaps)throws DaoException{
		return baseDao.queryBySQL(sql, returnMaps);
	}
	/**
	 * 根据数据库SQL语句查询
	 * 
	 * @param sql
	 *            eg."select a.*,b.NAME from a,b where a.ID = b.ID"
	 * @param returnMap
	 *            输出列和类型的对应Map.key:SQL中输出的字段名称,value:字段对应的实体class或org.hibernate.
	 *            type.Type eg.HashMap<"a", A>
	 * @return
	 */

	public List<Object> queryBySQL(String sql, Map<String, Object> returnMap ,Class voCls)throws DaoException{
		return baseDao.queryBySQL(sql, returnMap, voCls);
	}
	
	/**
	 * 根据数据库SQL语句进行分页查询,返回结果每一条记录对应一个map对象
	 * @param sql
	 * @param pageInfo
	 * @param returnMap
	 * @return 
	 * @throws DaoException
	 */
	public List<Map> query4PageBySql(String sql, PageInfo pageInfo,HashMap<String, Object> returnMap) throws DaoException{
		return baseDao.query4PageBySql(sql, pageInfo, returnMap);
	}
	
	/**
	 * 根据数据库SQL语句进行分页查询,返回结果每一条记录对应一个vo对象
	 * @param sql
	 * @param pageInfo
	 * @param returnMap
	 * @param voCls
	 * @return
	 * @throws DaoException
	 */
	public List<Object> query4PageBySql(String sql,PageInfo pageInfo, HashMap<String, Object> returnMap,Class voCls) throws DaoException{
		return baseDao.query4PageBySql(sql, pageInfo, returnMap,voCls);
	}
	

	/**
	 * 根据数据库SQL语句进行分页查询
	 * @param sql
	 * @param countSql
	 * @param pageInfo
	 * @param returnMap
	 * @return
	 * @throws DaoException
	 */
	public List<Map> query4PageBySql(String sql,String countSql, PageInfo pageInfo,HashMap<String, Object> returnMap) throws DaoException{
		return baseDao.query4PageBySql(sql, countSql, pageInfo, returnMap);
	}
	
	/**
	 * 分页查询，智能添加前台参数
	 * @param poClass
	 * @param conditionList
	 * @return PageResult
	 */
	public PageResult query4Page(Class poClass,QueryConditionList conditionList){
		StringBuffer hql = new StringBuffer();
		
		Map<String, Object> params = null;
		
		params = parseConditions(poClass, conditionList, hql, params);
		return baseDao.query4Page(hql.toString(), conditionList.getStartIndex(), conditionList.getPageSize(), params);
	}
	
	/**
	 * 解析查询条件
	 * @param poClass
	 * @param conditionList
	 * @param hql
	 * @param params
	 * @return
	 */
	public Map<String, Object> parseConditions(Class poClass,
			QueryConditionList conditionList, StringBuffer hql,
			Map<String, Object> params) {
		String poName = getClassName(poClass);
		hql.append(" from ").append(poName);
		//组装查询
		if(null != conditionList){
			params = new HashMap<String, Object>();
			
			List<QueryConditionItem> items = conditionList.getConditionItems();
			
			hql.append(" where 1=1 ");
			for (int i = 0; i < items.size(); i++) {
				QueryConditionItem queryConditionItem = items.get(i);
				String filedName = queryConditionItem.getFieldName();
				if (queryConditionItem.getOperator().equals(Operator.IN)) {
					hql.append(" and ").append(filedName).append(" ").append(queryConditionItem.getOperator().getValue()).append(" (");
					hql.append(queryConditionItem.getValue());
					hql.append(")");
				} else {
					hql.append(" and ").append(filedName).append(" ").append(queryConditionItem.getOperator().getValue()).append(" :");
					filedName = StringUtils.replace(filedName, ".", "_");
					//解决时间查询同一个属性，作为多个条件查询
					hql.append(filedName+"_"+i);
					params.put(filedName+"_"+i, queryConditionItem.getValue());
				}
			}
			//取排序
			List<QuerySortItem> sortList = conditionList.getSortList();
			StringBuffer order = new StringBuffer(sortList.size()*10);
			
			for (QuerySortItem querySortItem : sortList) {
				String filedName = querySortItem.getFieldName();
				if(StringUtils.isNotBlank(filedName)){
					order.append(" ,").append(filedName).append(" ").append(querySortItem.getSortMode());
				}
			}
			if(order.length() > 0){
				order.delete(0, 2);
				hql.append(" order by ").append(order);
			}
		}
		return params;
	}
	
	
	/**
	 * 根据指定的PO的类，返回类名，需要去掉包名
	 * @param poClass
	 * @return String
	 */
	protected String getClassName(Class poClass){
		if (poClass==null) {
			throw new InvalidParameterException("必须指定PO的类");
		}
		
		String className=poClass.getName();
		int index=className.lastIndexOf(".");
		return className.substring(index+1);
	}
	
	public IBaseDao getBaseDao() {
		return baseDao;
	}

	public void setBaseDao(IBaseDao baseDao) {
		this.baseDao = baseDao;
	}
	
}
