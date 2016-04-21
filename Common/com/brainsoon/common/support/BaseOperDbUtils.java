package com.brainsoon.common.support;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.system.model.SysParameter;
import com.brainsoon.system.service.IDictNameService;
import com.hp.hpl.jena.sparql.function.library.print;



/**
 * 
 * @ClassName: BaseOperDbUtils 
 * @Description: 操作数据库工具类
 * @author tanghui 
 * @date 2013-5-2 下午4:07:35 
 *
 */
public class BaseOperDbUtils {
	public static final Logger logger = Logger.getLogger(BaseOperDbUtils.class);
	private static  IDictNameService dictNameService = null;
	
	
	/**
	 * 根据typeId查询select标签options  且为有效的
	 * @param typeId
	 * @return
	 */
//	@SuppressWarnings("unchecked")
//	public static List<SysSelectOpts> querySelectOptsByTpyeId(String typeId){
//		dictNameService = (IDictNameService)BeanFactoryUtil.getBean("dictNameService");
//		String hql = "from SysSelectOpts s where s.sysSelectType.id = '" + typeId + "' and s.sysSelectType.status=1";
//		return baseQueryService.query(hql);
//	}
	
	
	/**
	 * 根据IndexTag查询select标签options 且为有效的
	 * @param indexTag
	 * @return
	 */
//	@SuppressWarnings("unchecked")
//	public static List<SysSelectOpts> querySelectOptsByIndexTag(String indexTag){
//		String hql = "from SysSelectOpts s where s.sysSelectType.indexTag = '" + indexTag + "' and s.sysSelectType.status=1";
//		return baseQueryService.query(hql);
//	}
	
	
	/**
	 * 查询所有的select,且为有效的
	 * @return
	 */
//	@SuppressWarnings("unchecked")
//	public static List<SysSelectType> querySelectTypesList(){
//		return baseQueryService.query("from SysSelectType s where s.status=1");
//	}
	
	/**
	 * 根据key查询参数value
	 * @param indexTag
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String queryParamValueByKey(String key){
		try {
			dictNameService = (IDictNameService)BeanFactoryUtil.getBean("dictNameService");
		} catch (Exception e) {
			e.printStackTrace();
		}
		String hql = "from SysParameter s where s.key = '" + key + "' and s.status= 1";
		List<SysParameter> sysParameters = dictNameService.query(hql);
		if(sysParameters != null && sysParameters.size() > 0){
			return sysParameters.get(0).getParaValue();
		}else{
			return null;
		}
	}
	
	/**
	 * 
	 * @Title: queryResCategoryIdByCode 
	 * @Description: 查询自定义分类主键Id
	 * @param  code 
	 * @param  type 
	 * @return Long
	 * @throws
	 */
//	@SuppressWarnings({ "unchecked"})
//	public static Long queryResCategoryIdByCode(String code,String type){
//		Long id = null;
//		if(StringUtils.isNotEmpty(code) && StringUtils.isNotEmpty(type)){
//			if(code.indexOf(".") != -1){
//				code = code.substring(0,code.lastIndexOf("."));
//			}
//			List<ResCategory> resCategorys = null;
//			//判断是否是中图分类
//			if(type.equals("1")){
//				resCategorys =  baseQueryService.query(" from ResCategory s where s.code  like '%"  + code.trim() +  "*%' and s.type =" + type);
//			}else{
//				resCategorys =  baseQueryService.query(" from ResCategory s where s.code  like '%"  + code.trim() +  "%' and s.type =" + type);
//			}
//			
//			if(resCategorys != null && resCategorys.size() > 0){
//				//如果只查询到了一条数据，那么说明准确的定位到了分类
//				if(resCategorys.size() == 1){
//					id = resCategorys.get(0).getId();
//				}
//			  }
//		  }
//		return id;
//	}
	
	
	//test
//	public static void main(String[] args){
//		BaseOperDbUtils m = new BaseOperDbUtils();
//		List<SysSelectOpts> ss = querySelectOptsByTpyeId("1");
//		for (SysSelectOpts sysSelectOpts : ss) {
//			System.out.println(sysSelectOpts.toString());
//		}
//		
//	}
	
	
}
