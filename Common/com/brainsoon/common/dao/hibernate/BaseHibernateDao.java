package com.brainsoon.common.dao.hibernate;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.Id;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.common.dao.IBaseDao;
import com.brainsoon.common.exception.DaoException;
import com.brainsoon.common.pagination.PageInfo;
import com.brainsoon.common.po.BaseHibernateObject;
import com.brainsoon.resource.po.UploadTaskDetail;

/**
 * 
 * @ClassName: BaseHibernateDao
 * @Description: BaseHibernateDao 基类
 */
@Repository("baseDao")
public class BaseHibernateDao implements IBaseDao {
	protected final Logger logger = LoggerFactory.getLogger(BaseHibernateDao.class);

	@Autowired
	@Qualifier("sessionFactory")
	private SessionFactory sessionFactory;

	public Session getSession() {
		// 事务必须是开启的(Required)，否则获取不到
		return sessionFactory.getCurrentSession();
	}

	
	/** 
     * 关闭session连接 
     * */  
    public void closeSession(){  
        if(getSession()!=null)  
        	getSession().close();  
    }  
    
	
	/**
	 * 创建Hibernate的PO对象，并且持久化。
	 * 
	 * @param obj 要持久化的PO对象
	 * @throws DaoException
	 */
	public void flush() throws DaoException {
		try {
			getSession().flush();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DaoException(e.getMessage(), e);
		}
	}
	
	
	
	
	/**
	 * 创建Hibernate的PO对象，并且持久化。
	 * 
	 * @param obj
	 *            要持久化的PO对象
	 * @return 持久化后的PO对象，对于利用数据库提供主键生成的PO对象，可以通过返回值获得其主键OID
	 * @throws DaoException
	 */
	public BaseHibernateObject create(BaseHibernateObject obj)
			throws DaoException {
		try {
			getSession().save(obj);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DaoException(e.getMessage(), e);
		}
		return obj;
	}

	/**
	 * 更新Hibernate的PO对象
	 * 
	 * @param obj
	 */
	public void update(BaseHibernateObject obj) throws DaoException {
		try {
			getSession().update(obj);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DaoException(e.getMessage(), e);
		}

	}

	/**
	 * 删除Hibernate的PO对象
	 * 
	 * @param obj
	 */
	public void delete(BaseHibernateObject obj) throws DaoException {
		try {
			getSession().delete(obj);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DaoException(e.getMessage(), e);
		}

	}

	/**
	 * 根据主键删除PO对象
	 * 
	 * @param poClass
	 *            PO类
	 * @param oid
	 *            待删除对象的主键
	 */
	public void delete(Class poClass, Serializable oid) throws DaoException {
		getSession().delete(getByPk(poClass, oid));

	}

	/**
	 * 根据以,分割的主键串,批量删除PO对象
	 * 
	 * @param poClass
	 * @param ids
	 */

	public void delete(Class poClass, String ids) throws DaoException {
		String pkName = "";
		Field[] fields = poClass.getDeclaredFields();
		for (Field f : fields) {
			if (f.isAnnotationPresent(Id.class)) {
				pkName = f.getName();
				break;
			}
		}
		char[] chars = new char[1];  
		//属性上找不到,去get方法
		if(StringUtils.isBlank(pkName)){
			Method[] methods = poClass.getDeclaredMethods();
			for (Method method : methods) {
				if(method.isAnnotationPresent(Id.class)){
					String currentName = method.getName();
					//去掉get,然后首字母转小写
					currentName = StringUtils.substringAfter(currentName, "get");
			        chars[0] = currentName.charAt(0);  
			        if(chars[0] >= 'A'  &&  chars[0] <= 'Z'){  
			        	String temp = new String(chars);  
			        	currentName = currentName.replaceFirst(temp,temp.toLowerCase());
			        }
			        pkName = currentName;
			        break;
				}
			}
		}
		if(StringUtils.isNotBlank(pkName)){
			String sql = " delete from " + poClass.getSimpleName() + " where "
					+ pkName + " in (" + ids + ")";
			Map<String, Object> paras = new HashMap<String, Object>();
			executeUpdate(sql, paras);
		}
	}

	/**
	 * 根据HQL查询语句，返回符合条件的PO对象列表； 注意：如果能够通过PO对象关联拼写查询条件，请直接使用query(Class
	 * poClass,QueryConditionList conditions)方法； 如果使用此方法，请注意保证HQL语法正确！
	 * 
	 * @param hql
	 *            符合HQL规范的查询语句
	 * @return 符合条件的PO对象列表
	 * @throws DaoException
	 */
	public List query(String hql) throws DaoException {
		List result = new ArrayList();
		try {
			result = (List) getSession().createQuery(hql).list();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DaoException(e.getMessage(), e);
		}
		return result;
	}
	/**
	 * 根据HQL查询语句，返回符合条件的PO对象列表； 注意：如果能够通过PO对象关联拼写查询条件，请直接使用query(Class
	 * poClass,QueryConditionList conditions)方法； 如果使用此方法，请注意保证HQL语法正确！
	 * 
	 * @param hql
	 *            符合HQL规范的查询语句
	 * @return 符合条件的PO对象列表
	 * @throws DaoException
	 */
	public boolean updateWithHql(String hql) throws DaoException {
		boolean updateSucc = false;
		try {
			int result  = getSession().createQuery(hql).executeUpdate();
			if(result != 0){
				updateSucc = true;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DaoException(e.getMessage(), e);
		}
		return updateSucc;
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
	public List query(String hql, String paramName, Object value)
			throws DaoException {
		List result = new ArrayList();
		try {
			Query query = getSession().createQuery(hql);
			query.setParameter(paramName, value);
			result = query.list();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DaoException(e.getMessage(), e);
		}
		return result;
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
	public List query(String hql, int first, int maxValue)
			throws DaoException {
		List result = new ArrayList();
		try {
			Query query = getSession().createQuery(hql);
			query.setMaxResults(maxValue);
			query.setFirstResult(first);
			result = query.list();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DaoException(e.getMessage(), e);
		}
		return result;
	}
	/**
	 * 带参数的hql查询
	 * 
	 * @param hql
	 * @param parameters
	 * @return
	 * @throws DaoException
	 */
	public List query(String hql, Map<String, Object> params)
			throws DaoException {
		List result = new ArrayList();
		try {
			Query query = getSession().createQuery(hql);
			String[] paramNames = new String[params.size()];
			params.keySet().toArray(paramNames);
			for (int i = 0; i < paramNames.length; i++) {
				query.setParameter(paramNames[i], params.get(paramNames[i]));
			}
			result = query.list();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DaoException(e.getMessage(), e);
		}
		return result;
	}

	/**
	 * 支持参数的hql批量操作,可批量执行update、delete和inserte操作。
	 * 
	 * @param executeHql
	 *            hql脚本，参数变量名为规则为":变量名"，如：":newName"
	 * @param parameters
	 *            参数集合，元素key为参数变量名，value为参数变量值
	 * @return 操作的记录条数
	 * @throws DaoException
	 * 
	 */
	public int executeUpdate(String executeHql, Map<String, Object> parameters)
			throws DaoException {
		try {
			Query query = getSession().createQuery(executeHql);
			if (parameters != null && !parameters.isEmpty()) {
				for (Iterator iterator = parameters.entrySet().iterator(); iterator
						.hasNext();) {
					Map.Entry<String, Object> parameter = (Map.Entry<String, Object>) iterator
							.next();
					if (parameter.getValue() instanceof Collection) {
						query.setParameterList(parameter.getKey(),
								(Collection) parameter.getValue());
					} else {
						query.setParameter(parameter.getKey(),
								parameter.getValue());
					}
				}
			}

			return query.executeUpdate();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DaoException(e.getMessage(), e);
		}

	}

	/**
	 * 查询PO的所有数据
	 * 
	 * @param poClass
	 */
	public List loadAll(Class poClass) {
		String hql = " from " + poClass.getSimpleName();
		return getSession().createQuery(hql).list();
	}

	/**
	 * 根据数据库表主键，检索唯一的PO对象
	 * 
	 * @param poClass
	 * @param oid
	 * @throws DaoException
	 */
	public BaseHibernateObject getByPk(Class poClass, Serializable oid)
			throws DaoException {
		try {
			return (BaseHibernateObject) getSession().get(poClass, oid);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DaoException(e.getMessage(), e);
		}

	}

	/**
	 * 根据数据库表主键，检索唯一的PO对象
	 * 
	 * @param poClass
	 * @param oid
	 * @return 如果没有符合条件的PO对象，则抛出对象不存在的异常ObjectNotFoundException
	 * @throws DaoException
	 */
	public BaseHibernateObject load(Class poClass, Serializable oid)
			throws DaoException {
		try {
			return (BaseHibernateObject) getSession().load(poClass, oid);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DaoException(e.getMessage(), e);
		}
	}

	/**
	 * 检查是否存在满足主键条件的PO对象
	 * 
	 * @param poClass
	 * @param oid
	 * @return
	 * @throws DaoException
	 */
	public boolean exist(Class poClass, Serializable oid) throws DaoException {

		return getByPk(poClass, oid) != null;
	}

	/**
	 * 检查是否满足主键条件的PO对象不存在
	 * 
	 * @param poClass
	 * @param oid
	 * @return
	 * @throws DaoException
	 */
	public boolean notExist(Class poClass, Serializable oid)
			throws DaoException {
		return getByPk(poClass, oid) == null;
	}

	/**
	 * 迫使 session 的缓存与数据库同步 当缓存中的 对象 与数据库不同步，且 想取得数据库中的数据的时候，可调用此方法 慎用！！！
	 * 
	 * @param obj
	 * @throws DaoException
	 * @author: rui.zh time: 2006-7-26 上午10:29:56
	 */
	public void refresh(BaseHibernateObject obj) throws DaoException {
		try {
			getSession().refresh(obj);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DaoException(e.getMessage(), e);
		}
	}

	/**
	 * 保存或修改PO对象
	 * 
	 * @param o
	 */
	public void saveOrUpdate(BaseHibernateObject o) {
		try {
			getSession().merge(o);
		} catch (Exception e) {
			logger.warn(e.getMessage());
			throw new DaoException(e.getMessage(), e);
		}

	}

	/**
	 * 分页处理逻辑
	 * 
	 * @param countHql
	 * @param query
	 * @param countQuery
	 * @param pageInfo
	 */
	private void dealPage(String countHql, Query query, Query countQuery,
			PageInfo pageInfo) throws Exception {
		if (countHql.toUpperCase().indexOf("COUNT(*)") != -1) {
			pageInfo.setTotal(new Integer(countQuery.uniqueResult().toString()).intValue());
		} else {
			pageInfo.setTotal(countQuery.list().size());
		}
		int pageNo = pageInfo.getPage();
		int pageRowNum = pageInfo.getRows();
		query.setMaxResults(pageRowNum);
		query.setFirstResult((pageNo - 1) * pageRowNum > 0 ? (pageNo - 1)
				* pageRowNum : 0);
		pageInfo.setItems(query.list());
	}

	/**
	 * 分页查询
	 * 
	 * @param hql
	 * @param pageBean
	 * @param params
	 * @return
	 */
	public List query4Page(String hql, String countHql, PageInfo pageInfo)
			throws DaoException {
		List result = new ArrayList();
		try {
			Session session = getSession();
			Query countQuery = session.createQuery(countHql);
			Query query = session.createQuery(hql);
			dealPage(countHql, query, countQuery, pageInfo);
			result = pageInfo.getItems();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DaoException(e.getMessage(), e);
		}
		return result;
	}

	/**
	 * 分页查询
	 * 
	 * @param hql
	 * @param pageInfo
	 * @param params
	 * @return
	 */
	public List query4Page(String hql, String countHql, PageInfo pageInfo,
			Map<String, Object> params) throws DaoException {
		List result = new ArrayList();
		try {
			Session session = getSession();
			Query countQuery = session.createQuery(countHql);
			if(pageInfo.getOrder()!=null&&pageInfo.getSort()!=null){
				hql=hql+" order by "+pageInfo.getSort()+" "+pageInfo.getOrder();
			}
			Query query = session.createQuery(hql);
			String[] paramNames = new String[params.size()];
			params.keySet().toArray(paramNames);
			Object[] values = new Object[params.size()];
			for (int i = 0; i < paramNames.length; i++) {
				values[i] = params.get(paramNames[i]);
			}
			if (paramNames != null && paramNames.length != 0) {
				for (int i = 0; i < paramNames.length; i++) {
					query.setParameter(paramNames[i], values[i]);
					countQuery.setParameter(paramNames[i], values[i]);
				}
			}
			dealPage(countHql, query, countQuery, pageInfo);
			result = pageInfo.getItems();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DaoException(e.getMessage(), e);
		}
		return result;
	}
	
	/**
	 * 分页查询
	 * @param hql
	 * @param pageInfo
	 * @param params
	 * @return
	 */
	public List query4Page(String hql, PageInfo pageInfo,Map<String, Object> params) throws DaoException {
		List result=new ArrayList();
		try {
			String countHql="select count(*)  "+hql.substring(hql.toLowerCase().indexOf("from"));
			result=query4Page(hql, countHql, pageInfo, params);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DaoException(e.getMessage(), e);
		}
		return result;
	}

	/**
	 * 由sql查询对象
	 * 
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public List<Object> queryBySql(String sql, Class clas) throws DaoException {
		Session session = getSession();
		List<Object> result = new ArrayList();
		try {
			result = session.createSQLQuery(sql).addEntity(clas).list();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DaoException(e.getMessage(), e);
		}
		return result;
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

	public List<Map> queryBySQL(String sql, Map<String, Object> returnMap)throws DaoException {
		Session session = getSession();
		List<Map> result = new ArrayList<Map>();
		try {
			SQLQuery query = session.createSQLQuery(sql);
			String[] paramNames = new String[returnMap.size()];
			returnMap.keySet().toArray(paramNames);
			for (int i = 0; i < paramNames.length; i++) {
				Object valObj = returnMap.get(paramNames[i]);
				if (valObj instanceof Class) {
					Class className = (Class) valObj;
					query.addEntity(paramNames[i], className);
				} else {
					query.addScalar(paramNames[i],
							(org.hibernate.type.Type) valObj);
				}
				
			}
			query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			result = query.list();
		} catch (DataAccessException e) {
			logger.error(e.getMessage());
			throw new DaoException(e.getMessage(), e);
		}
		return result;
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

	public List<Object> queryBySQL(String sql, Map<String, Object> returnMap ,Class voCls)throws DaoException {
		Session session = getSession();
		List<Object> result = new ArrayList();
		try {
			SQLQuery query = session.createSQLQuery(sql);
			String[] paramNames = new String[returnMap.size()];
			returnMap.keySet().toArray(paramNames);
			for (int i = 0; i < paramNames.length; i++) {
				Object valObj = returnMap.get(paramNames[i]);
				if (valObj instanceof Class) {
					Class className = (Class) valObj;
					query.addEntity(paramNames[i], className);
				} else {
					query.addScalar(paramNames[i],
							(org.hibernate.type.Type) valObj);
				}
				
			}
			query.setResultTransformer(Transformers.aliasToBean(voCls));
			result = query.list();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DaoException(e.getMessage(), e);
		}
		
		return result;
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
		String countSql="select count(*)  "+sql.substring(sql.toLowerCase().indexOf("from"));
		List<Map> result = new ArrayList<Map>();
		try {
			result=query4PageBySql(sql, countSql, pageInfo, returnMap);
		} catch (DataAccessException e) {
			logger.error(e.getMessage());
			throw new DaoException(e.getMessage(), e);
		}	
		return result;
	}
	
	/**
	 * 根据数据库SQL语句进行分页查询,返回结果每一条记录对应一个map对象
	 * 
	 * @param sql
	 * @param countSql
	 * @param pageInfo
	 * @param returnMap
	 * @return
	 * @throws DaoException
	 */
	public List<Map> query4PageBySql(String sql, String countSql,PageInfo pageInfo, HashMap<String, Object> returnMap)
			throws DaoException {
		Session session = getSession();
		List<Map> result = new ArrayList<Map>();
		try {
			SQLQuery query = session.createSQLQuery(sql);
			String[] paramNames = new String[returnMap.size()];
			returnMap.keySet().toArray(paramNames);
			for (int i = 0; i < paramNames.length; i++) {
				Object valObj = returnMap.get(paramNames[i]);
				if (valObj instanceof Class) {
					Class className = (Class) valObj;
					query.addEntity(paramNames[i], className);
				} else {
					query.addScalar(paramNames[i],
							(org.hibernate.type.Type) valObj);
				}
				
			}
			query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			SQLQuery countQuery = session.createSQLQuery(countSql);
			if (countSql.toUpperCase().indexOf("COUNT(*)") != -1) {
				pageInfo.setTotal(new Integer(countQuery.uniqueResult()
						.toString()).intValue());
			} else {
				pageInfo.setTotal(countQuery.list().size());
			}
			int pageNo = pageInfo.getPage();
			int pageRowNum = pageInfo.getRows();
			query.setMaxResults(pageRowNum);
			query.setFirstResult((pageNo - 1) * pageRowNum > 0 ? (pageNo - 1)
					* pageRowNum : 0);
			result = query.list();
			pageInfo.setItems(result);
		} catch (DataAccessException e) {
			logger.error(e.getMessage());
			throw new DaoException(e.getMessage(), e);
		}
		return result;
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
	public List<Object> query4PageBySql(String sql,PageInfo pageInfo, HashMap<String, Object> returnMap,Class voCls)throws DaoException {
		String countSql="select count(*)  "+sql.substring(sql.toLowerCase().indexOf("from"));
		List<Object> objResult=new ArrayList<Object>();
		try {
			objResult=query4PageBySql(sql, countSql, pageInfo, returnMap, voCls);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DaoException(e.getMessage(), e);
		}
		return objResult;
	}
	
	/**
	 * 根据数据库SQL语句进行分页查询,返回结果每一条记录对应一个vo对象
	 * 
	 * @param sql
	 * @param countSql
	 * @param pageInfo
	 * @param returnMap
	 * @param voCls
	 * @return
	 * @throws DaoException
	 */
	public List<Object> query4PageBySql(String sql, String countSql,PageInfo pageInfo, HashMap<String, Object> returnMap,Class voCls) throws DaoException {
		Session session = getSession();
		List<Object> result = new ArrayList<Object>();
		try {
			SQLQuery query = session.createSQLQuery(sql);
			String[] paramNames = new String[returnMap.size()];
			returnMap.keySet().toArray(paramNames);
			for (int i = 0; i < paramNames.length; i++) {
				Object valObj = returnMap.get(paramNames[i]);
				if (valObj instanceof Class) {
					Class className = (Class) valObj;
					query.addEntity(paramNames[i], className);
				} else {
					query.addScalar(paramNames[i],
							(org.hibernate.type.Type) valObj);
				}
				
			}
			query.setResultTransformer(Transformers.aliasToBean(voCls));
			SQLQuery countQuery = session.createSQLQuery(countSql);
			if (countSql.toUpperCase().indexOf("COUNT(*)") != -1) {
				pageInfo.setTotal(new Integer(countQuery.uniqueResult()
						.toString()).intValue());
			} else {
				pageInfo.setTotal(countQuery.list().size());
			}
			int pageNo = pageInfo.getPage();
			int pageRowNum = pageInfo.getRows();
			query.setMaxResults(pageRowNum);
			query.setFirstResult((pageNo - 1) * pageRowNum > 0 ? (pageNo - 1)
					* pageRowNum : 0);
			result = query.list();
			pageInfo.setItems(result);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DaoException(e.getMessage(), e);
		}
		return result;
	}
	
	
	/**
	 * 分页查询
	 * @param hql where中按位占位
	 * @param startIndex
	 * @param pageSize
	 * @param params 参数
	 * @return PageResult
	 */
	public PageResult query4Page(String hql,int startIndex,int pageSize,Map<String, Object> params){
		PageResult result = new PageResult();
		try {
			String countHql = " select count(*) " + hql;
			
			Session session = getSession();
			Query countQuery = session.createQuery(countHql);
			Query query = session.createQuery(hql);
			
			if(null != params && params.size() > 0){
				String[] paramNames = new String[params.size()];
				params.keySet().toArray(paramNames);
				Object[] values = new Object[params.size()];
				for (int i = 0; i < paramNames.length; i++) {
					values[i] = params.get(paramNames[i]);
				}
				if (paramNames != null && paramNames.length != 0) {
					for (int i = 0; i < paramNames.length; i++) {
						query.setParameter(paramNames[i], values[i]);
						countQuery.setParameter(paramNames[i], values[i]);
					}
				}
			}
			
			result.setTotal(new Integer(countQuery.uniqueResult().toString()).intValue());
			
			query.setMaxResults(pageSize);
			query.setFirstResult(startIndex);
			
			result.setRows(query.list());
			
			
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DaoException(e.getMessage(), e);
		}
		return result;
	}
	/**
	 * 批量导入详细列表分页查询
	 * @param hql where中按位占位
	 * @param startIndex
	 * @param pageSize
	 * @param params 参数
	 * @return PageResult
	 */
	@SuppressWarnings("unchecked")
	public PageResult queryBeachImportDetaillPage(String hql,int startIndex,int pageSize,Map<String, Object> params){
		PageResult result = new PageResult();
		try {
			Session session = getSession();
			Query query = session.createQuery(hql);
			boolean status = false;
			if(null != params && params.size() > 0){
				String[] paramNames = new String[params.size()];
				params.keySet().toArray(paramNames);
				Object[] values = new Object[params.size()];
				for (int i = 0; i < paramNames.length; i++) {
					if(paramNames[i].indexOf("status") != -1){
					   status = true;
					}
					values[i] = params.get(paramNames[i]);
				}
				if (paramNames != null && paramNames.length != 0) {
					for (int i = 0; i < paramNames.length; i++) {
						query.setParameter(paramNames[i], values[i]);
					}
				}
			}
			List<UploadTaskDetail> list = query.list();
			List<UploadTaskDetail> orderResult = new ArrayList<UploadTaskDetail>();
			int num = 0;
			if(list.size()>0){
					for(int i=0;i<list.size();i++){
//						num = i+1;
//						if(num<list.size() && list.size()-num!=1){
//							if(list.get(i+1).getExcelNum()-list.get(i).getExcelNum()==1){
//								orderResult.add(list.get(i));
//							}else{
//								break;
//							}
//						}else{
								orderResult.add(list.get(i));
//						}
				}
			}
			List<UploadTaskDetail> tempList =null;
			int lastIndex = startIndex+pageSize;
			tempList = new ArrayList<UploadTaskDetail>();
			if(!status){
			result.setTotal(orderResult.size());
			for(int i=startIndex;i<lastIndex;i++){
				if(i==orderResult.size()){
					break;
				}else{
					UploadTaskDetail tempDetail = orderResult.get(i);
					tempList.add(tempDetail);
				}
			}
				result.setRows(tempList);
			}else{
				for(int i=startIndex;i<lastIndex;i++){
					if(i==list.size()){
						break;
					}else{
						tempList.add(list.get(i));
					}
				}
				result.setRows(tempList);
				result.setTotal(list.size());
			}
			query.setMaxResults(pageSize);
			query.setFirstResult(startIndex);
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DaoException(e.getMessage(), e);
		}
		return result;
	}
}
