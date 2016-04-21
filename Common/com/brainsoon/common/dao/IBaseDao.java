package com.brainsoon.common.dao;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.common.exception.DaoException;
import com.brainsoon.common.pagination.PageInfo;
import com.brainsoon.common.po.BaseHibernateObject;
/**
 * 
 * @ClassName: IBaseDao 
 * @Description:  BaseDao 基类接口类
 */
public interface IBaseDao {
	/**
	 * 创建Hibernate的PO对象，并且持久化
	 * 
	 * @param obj  要持久化的PO对象
	 * @return 持久化后的PO对象，对于利用数据库提供主键生成的PO对象，可以通过返回值获得其主键OID
	 * @throws DaoException
	 */
	public BaseHibernateObject create(BaseHibernateObject obj) throws DaoException;

	/**
	 * 更新Hibernate的PO对象
	 * 
	 * @param obj
	 * @throws DaoException
	 */
	public void update(BaseHibernateObject obj) throws DaoException;

	/**
	 * 物理删除Hibernate的PO对象
	 * 
	 * @param obj
	 * @throws DaoException
	 */
	public void delete(BaseHibernateObject obj) throws DaoException;

	/**
	 * 根据主键删除PO对象
	 * 
	 * @param poClass  PO类
	 * @param oid 待删除对象的主键
	 */
	public void delete(Class poClass, Serializable oid) throws DaoException;
	
	/**
	 * 根据以,分割的主键串,批量删除PO对象
	 * @param poClass
	 * @param ids
	 */
	
    public void delete(Class poClass,String ids) throws DaoException;
    
	/**
	 * 根据HQL查询语句，返回符合条件的PO对象列表；
	 * 如果使用此方法，请注意保证HQL语法正确！
	 * @param hql  符合HQL规范的查询语句
	 * @return 符合条件的PO对象列表
	 * @throws DaoException
	 */
	public List query(String hql) throws DaoException;
	public List query(String hql, int first, int maxValue)
			throws DaoException;
	
	/**
	 * 根据hql查询对象,一个参数及其值查询对象 
	 * 
	 * @param hql
	 * @param paramName
	 * @param value
	 * @return
	 * @throws Exception
	 */

	public List query(String hql, String paramName, Object value);
	
	/**
	 * 带参数的hql查询
	 * @param hql
	 * @param parameters
	 * @return
	 * @throws DaoException
	 */
	public List query(String hql,Map<String, Object> parameters) throws DaoException;
	
	/**
	 * 支持参数的hql批量操作,可批量执行update、delete和inserte操作。
	 * @param executeHql hql脚本，参数变量名为规则为":变量名"，如：":newName"
	 * @param parameters 参数集合，元素key为参数变量名，value为参数变量值
	 * @return 操作的记录条数
	 * @throws DaoException
	 */
	public int executeUpdate(String executeHql, Map<String, Object> parameters) throws DaoException;
		
	/**
	 * 加载指定表的所有数据；本方法谨慎使用，建议只对常量表使用！
	 * @param poClass
	 * @return
	 */
	public List loadAll(Class poClass);
	
	/**
	 * 根据数据库表主键，检索唯一的PO对象
	 * @param oid
	 * @return  如果没有符合条件的PO对象，则返回null
	 * @throws DaoException
	 */
	public BaseHibernateObject getByPk(Class poClass,Serializable oid)throws DaoException;
	
	/**
	 * 根据数据库表主键，检索唯一的PO对象
	 * @param poClass
	 * @param oid
	 * @return  如果没有符合条件的PO对象，则抛出对象不存在的异常ObjectNotFoundException
	 * @throws DaoException
	 */
	public BaseHibernateObject load(Class poClass,Serializable oid)throws DaoException;
	/**
	 * 检查是否存在满足主键条件的PO对象
	 * @param poClass
	 * @param oid
	 * @return
	 * @throws DaoException
	 */
	public boolean exist(Class poClass,Serializable oid) throws DaoException;
	/**
	 * 检查是否满足主键条件的PO对象不存在
	 * @param poClass
	 * @param oid
	 * @return
	 * @throws DaoException
	 */
	public boolean notExist(Class poClass,Serializable oid) throws DaoException;
	/**
	 * 迫使 session 的缓存与数据库同步
	 * 当缓存中的 对象 与数据库不同步，且 想取得数据库中的数据的时候，可调用此方法
	 * 慎用！！！
	 * @param obj
	 * @throws DaoException
	 */
	public void refresh(BaseHibernateObject obj) throws DaoException;
	
	public void saveOrUpdate(BaseHibernateObject o) ;
	/**
	 * 分页查询
	 * 
	 * @param hql
	 * @param pageBean
	 * @param params
	 * @return
	 */
	public List query4Page(String hql, String countHql, PageInfo pageInfo) throws DaoException ;
	
	/**
	 * 分页查询
	 * 
	 * @param hql
	 * @param pageInfo
	 * @param params
	 * @return
	 */
	public List query4Page(String hql, String countHql, PageInfo pageInfo,Map<String, Object> params) throws DaoException;
	
	/**
	 * 分页查询
	 * @param hql
	 * @param pageInfo
	 * @param params
	 * @return
	 */
	public List query4Page(String hql, PageInfo pageInfo,Map<String, Object> params) throws DaoException;
	
	/**
	 * 由sql查询对象
	 * 
	 * @param sql
	 * @return
	 * @throws Exception
	 */

	public List<Object> queryBySql(String sql,Class clas) throws DaoException;
	
	/**
	 * 根据数据库SQL语句查询
	 * @param sql	 eg."select a.*,b.NAME from a,b where a.ID = b.ID"
	 * @param returnMap 输出列和类型的对应Map.key:SQL中输出的字段名称,value:字段对应的实体class或org.hibernate.type.Type eg.HashMap<"a", A>
	 * @return
	 */
	
	public List<Map> queryBySQL(String sql,Map<String, Object> returnMap)throws DaoException;
	
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

	public List<Object> queryBySQL(String sql, Map<String, Object> returnMap ,Class voCls)throws DaoException;
	
	/**
	 * 根据数据库SQL语句进行分页查询,返回结果每一条记录对应一个map对象
	 * @param sql
	 * @param pageInfo
	 * @param returnMap
	 * @return 
	 * @throws DaoException
	 */
	public List<Map> query4PageBySql(String sql, PageInfo pageInfo,HashMap<String, Object> returnMap) throws DaoException;

	/**
	 * 根据数据库SQL语句进行分页查询,返回结果每一条记录对应一个map对象
	 * @param sql
	 * @param countSql
	 * @param pageInfo
	 * @param returnMap
	 * @return
	 * @throws DaoException
	 */
	public List<Map> query4PageBySql(String sql,String countSql, PageInfo pageInfo,HashMap<String, Object> returnMap) throws DaoException;
	
	/**
	 * 根据数据库SQL语句进行分页查询,返回结果每一条记录对应一个vo对象
	 * @param sql
	 * @param pageInfo
	 * @param returnMap
	 * @param voCls
	 * @return
	 * @throws DaoException
	 */
	public List<Object> query4PageBySql(String sql,PageInfo pageInfo, HashMap<String, Object> returnMap,Class voCls) throws DaoException;
	
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
	public List<Object> query4PageBySql(String sql, String countSql,PageInfo pageInfo, HashMap<String, Object> returnMap,Class voCls) throws DaoException;
	
	/**
	 * 分页查询  zuohl add
	 * @param hql where中按位占位
	 * @param startIndex
	 * @param pageSize
	 * @param params 参数
	 * @return PageResult
	 */
	public PageResult query4Page(String hql,int startIndex,int pageSize,Map<String, Object> params);
	
	/**
	 * 获取session
	 * @return
	 */
	public Session getSession();
	
	/** 
     * 关闭session连接 
     * */  
    public void closeSession();
    
    /**
	 * 创建Hibernate的PO对象，并且持久化。
	 * @throws DaoException
	 */
	public void flush() throws DaoException;
	 /**
		 * 更新PO对象，并且持久化。
		 * @throws DaoException
		 */
	public boolean updateWithHql(String hql) throws DaoException ;
	/**
	 * 分页查询  zuohl add
	 * @param hql where中按位占位
	 * @param startIndex
	 * @param pageSize
	 * @param params 参数
	 * @return PageResult
	 */
	public PageResult queryBeachImportDetaillPage(String hql,int startIndex,int pageSize,Map<String, Object> params);
}
