package com.brainsoon.system.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.resource.po.SearchEntryMonth;
import com.brainsoon.system.model.DictName;
import com.brainsoon.system.model.MetadataDefinitionGroup;
import com.brainsoon.system.model.ResCategory;
import com.brainsoon.system.model.ResCategoryType;
import com.brainsoon.system.model.TreeRelationType;
import com.brainsoon.system.service.IFLTXService;
import com.brainsoon.system.support.ExportResCategoryUtil;
@Service
public class FLTXService extends BaseService implements IFLTXService {
    private JdbcTemplate jdbcTemplate ;
	
	@Autowired
	public void init(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	} 

	@Override
	public String getFLTXMenu() {
		List<ResCategoryType> resTypeList = query("from ResCategoryType");
		JSONArray array = new JSONArray();
		for(ResCategoryType rct : resTypeList){
			JSONObject jo = new JSONObject();
			jo.put("id", rct.getId());
			jo.put("name", rct.getName());
			jo.put("indexTag", rct.getIndexTag());
			array.add(jo);
		}
		return array.toString();
	}

	@Override
	public String getFLTXContent(Long type) {
		List<ResCategory> resList = query("from ResCategory where type = " + type);
		JSONArray array = new JSONArray();
		for(ResCategory rc : resList){
			JSONObject jo = new JSONObject();
			jo.put("id", rc.getId());
			String name = StringUtils.isBlank(rc.getCode())?"[无]":"["+rc.getCode()+"]";
			name += rc.getName();
			jo.put("name", name);
			jo.put("code", rc.getCode());
			jo.put("pid", rc.getPid());
			jo.put("type", type);
			jo.put("xpath", rc.getPath());
			array.add(jo);
		}
		return array.toString();
	}
	
	public String getFLTXContent(Long type,String path) {
		List<ResCategory> resList = new ArrayList<ResCategory>();
		if(path.equals("0")){
			if(type!=1){
				resList = query("from ResCategory where type = "
						+ type + " and pid = " + path +" order by id desc");
			}else{
				resList = query("from ResCategory where type = "
						+ type + " and pid = " + path);
			}
			
		}else{
			if(type!=1){
				resList = query("from ResCategory where type = "
						+ type + " and pid = " + path + " order by id desc");
			}else{
				resList = query("from ResCategory where type = "
						+ type + " and pid = " + path);
			}
		}
		JSONArray array = new JSONArray();
		for(ResCategory rc : resList){
			JSONObject jo = new JSONObject();
			List<ResCategory> childrenList = query("from ResCategory where type = "
						+ type + " and pid = " + rc.getId());
			if(childrenList!=null&&childrenList.size()>0){
				jo.put("isParent", true);
				jo.put("classDate", "tmTimeClass");
			}else{
				jo.put("isParent", false);
				jo.put("classDate", "tmdate1");
			}
			
			jo.put("id", rc.getId());
			String name = "";
			if(type==1){
				name = StringUtils.isBlank(rc.getCode())?"[无]":"["+rc.getCode()+"]";
				name += rc.getName();
			}else{
				name = rc.getName();
			}
			jo.put("name", name);
			jo.put("code", rc.getCode());
			jo.put("pid", rc.getPid());
			jo.put("type", type);
			jo.put("xpath", rc.getPath());
			if(type==6){
				jo.put("xcode", rc.getXcode());
			}
			array.add(jo);
		}
		return array.toString();		
	}
	@Override
	public ResCategoryType addFLTXMenu(String name, String indexTag) {
		ResCategoryType rct = new ResCategoryType();
		rct.setName(name);
		rct.setIndexTag(indexTag);
		rct.setCreatedTime(new Date());
		rct.setModifiedTime(new Date());
		rct = (ResCategoryType) create(rct);
		return rct;
	}
	@Override
	public void addRelationType(TreeRelationType treeRalationType) {
		create(treeRalationType);
	}
	public File exportExcel(String typeId,String typeName){
		List<ResCategory> resList = query("from ResCategory where type = " + typeId);
		ExportResCategoryUtil ercu = new ExportResCategoryUtil(resList);
		File file = ercu.exportExcel(typeName);
		return file;
	}

	@Override
	public void addFLTXContent(JSONObject node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateFLTXMenu(JSONObject node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateFLTXContent(JSONObject node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteFLTXMenu(Long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteFLTXContent(Long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getFLTXNodeByCode(Long type, String codes) {
		StringBuilder result = new StringBuilder();
		for(String code : codes.split(",")){//正常逻辑
			ResCategory res = getResCategoryByCode(type, code);
			if(res!=null){
				result.append(",");
				result.append(res.getPath());
				result.append(",");
				result.append("-");
			}
		}
		
		String paths = result.toString();
		if(StringUtils.isNotBlank(paths)){
			paths = paths.substring(0, paths.length()-1);
		}else{
			paths ="0";
		}
		return paths;
	}
	
	/**
	 * 根据分类path 查询分类的名字
	 * @param path
	 * @return
	 */
	public String queryCatagoryCnName(String path) {
		String sql = "";
		String name = "";
		if(StringUtils.isNotBlank(path)){
			if(path.indexOf("-")>=0){
				String[] paths = path.split("-"); //后台存储的数据格式为,22,334,32,-,55,23,为了中图分类号能匹配
				sql = "from ResCategory where ";
				for(String pathTemp:paths){
					pathTemp = pathTemp.substring(1,pathTemp.length()-1);
					sql+="path='"+ pathTemp +"' or ";
				}
				sql = sql.substring(0,sql.length()-3);
			}else{
				if(path.length()>1){
					path = path.substring(1,path.length()-1);
				}
				sql = "from ResCategory where path ='"+ path +"'";
			}
			List<ResCategory> list = query(sql);
			if(list.size()>0){
				for(ResCategory node : list){
					String code = "["+node.getCode()+"]";
					name += code +node.getName()+",";
				}
				if(name.length()>1){
					name = name.substring(0,name.length()-1);
				}
			}
		}
		return name;
	}
	/**
	 * 根据分类path 查询分类的编码
	 * @param path
	 * @return
	 */
	public String queryCatagoryCode(String path) {
		String sql = "";
		String code = "";
		if(StringUtils.isNotBlank(path)){
			if(path.indexOf("-")>=0){
				String[] paths = path.split("-"); //后台存储的数据格式为,22,334,32,-,55,23,为了中图分类号能匹配
				sql = "from ResCategory where ";
				for(String pathTemp:paths){
					pathTemp = pathTemp.substring(1,pathTemp.length()-1);
					sql+="path='"+ pathTemp +"' or ";
				}
				sql = sql.substring(0,sql.length()-3);
			}else{
				if(path.length()>1){
					path = path.substring(1,path.length()-1);
				}
				sql = "from ResCategory where path ='"+ path +"'";
			}
			List<ResCategory> list = query(sql);
			if(list.size()>0){
				for(ResCategory node : list){
					code += node.getCode()+",";
				}
				if(code.length()>1){
					code = code.substring(0,code.length()-1);
				}
			}
		}
		return code;
	}
	private ResCategory getResCategoryByCode(Long type,String code){
		ResCategory res = null;
		while(!StringUtils.isBlank(code)){
			List<ResCategory> resList = query("from ResCategory where type = "
					+ type + " and code = '" + code + "'");
			if(resList!=null&&resList.size()>0){
				res = resList.get(0);
				break;
			}
			code = code.substring(0, code.length()-1);
		}
		return res;
	}

	@Override
	public String getFLTXNodeByName(Long type, String names) {
		StringBuilder result = new StringBuilder();
		for(String name : names.split(",")){
			result.append(",");
			ResCategory res = getResCategoryByName(type, name);
			if(res!=null && res.getPath()!=null){
				result.append(res.getPath());
				result.append(",");
				result.append("-");
			}else{
				return "";
			}
		}
		String paths = result.toString();
		paths = paths.substring(0, paths.length()-1);
		return paths;
	}
	
	private ResCategory getResCategoryByName(Long type,String name){
		ResCategory res = null;
		while(!StringUtils.isBlank(name)){
			List<ResCategory> resList = query("from ResCategory where type = "
					+ type + " and name = '" + name + "'");
			if(resList!=null&&resList.size()>0){
				res = resList.get(0);
				break;
			}
			name = name.substring(0, name.length()-1);
		}
		return res;		
	}
	@Override
	public void delRelation(Long id){
		TreeRelationType treeRelationType = (TreeRelationType)getByPk(TreeRelationType.class,id);
		delete(treeRelationType);
	}
	@Override
	public List queryNameAndPath(String id) {
		List<ResCategory> listMeta = new ArrayList<ResCategory>();
		String arrayId[] = id.split(",");
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT NAME ,path,TYPE FROM res_category WHERE id IN (SELECT relationKey FROM res_treeRelation_type WHERE centerKey in (");
		for(int i=0;i<arrayId.length;i++) {
			sql.append(Integer.parseInt(arrayId[i]));
			sql.append(",");
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append("))");
		try {
			List list = jdbcTemplate.queryForList(sql.toString());
			Iterator it = list.iterator();    
			while(it.hasNext()) {    
			    Map map = (Map) it.next();
			    ResCategory resCategory = new ResCategory();
			    resCategory.setName(map.get("name").toString());
			    resCategory.setPath(map.get("path").toString());
			    resCategory.setType(Long.parseLong(map.get("type").toString()));
			    listMeta.add(resCategory);
			} 
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
		
		return listMeta;
	}
	public String queryCheckType(String centerType) {
		List<ResCategoryType> resTypeList = query("from ResCategoryType");
		List<TreeRelationType> listMeta = new ArrayList<TreeRelationType>();
		JSONArray array = new JSONArray();
		int type = Integer.parseInt(centerType);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT relationType,centerType  FROM res_treeRelation_type WHERE centerType = '"+type+"' or relationType = '"+type+"'");
		try {
			List list = jdbcTemplate.queryForList(sql.toString());
			Iterator it = list.iterator();    
			while(it.hasNext()) {    
			    Map map = (Map) it.next();
			    TreeRelationType treeRelationType = new TreeRelationType();
			    treeRelationType.setRelationType(Integer.parseInt(map.get("relationType").toString()));
			    treeRelationType.setCenterType(Integer.parseInt(map.get("centerType").toString()));
			    listMeta.add(treeRelationType);
			} 
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
		if(listMeta.size()==0){
			for(ResCategoryType rct : resTypeList){
				JSONObject jo = new JSONObject();
				long relationType1 = Long.parseLong(centerType);
				long idNew1 = rct.getId();
				if(idNew1!=relationType1) {
					jo.put("id", rct.getId());
					jo.put("name", rct.getName());
					jo.put("indexTag", rct.getIndexTag());
					array.add(jo);
				}
			}
		}/*else {
			String arrayAll[] = new String[resTypeList.size()];
			String typeStr="";
			for(int j=0;j<resTypeList.size();j++){
				String idNewSec = resTypeList.get(j).getId()+"";
				arrayAll[j] = idNewSec;
			}
			for(int k=0;k<listMeta.size();k++) {
				String allType = listMeta.get(k).getType();
				typeStr+=allType+",";
			}
			String arrayChild[] = typeStr.split(",");
			for(int i=0;i<arrayChild.length;i++){
				for(int g=i+1;g<arrayChild.length;g++) {
					if(arrayChild[i].equals(arrayChild[g])) {
						arrayChild[g]=null;
					}
				}
			}
			
		}*/
		return array.toString();
	}
	@Override
	public String checkRelation(String centerId,String relationId) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT relationKey  FROM res_treeRelation_type WHERE centerKey = '"+centerId+"' and relationKey= '"+relationId+"'");
		List list = jdbcTemplate.queryForList(sql.toString());
		if(list.size()>0) {
			return "1";
		}
		return "0";
	}
	/**
	 * 返回月份
	 */
	@Override
	public String monthJson(long parseLong, String yearPath,String year,String monthField) {
		
		JSONArray array = new JSONArray();
		String hql = "from SearchEntryMonth";
		List<SearchEntryMonth> seList = query(hql);
		String mysqlYear = "";
		int month = 0;
		if(seList.size()>0){
			mysqlYear = seList.get(0).getEntryYear();
			month = Integer.parseInt(seList.get(0).getEntryMonth());
		}
		//获得当前年是否是数据库最新的年，如果是...则循环到当前年的几月，如果不是则直接从12个月循环
		if(year.equals(mysqlYear)){
			for(int i=month;i>0;i--){
				JSONObject jo = new JSONObject();
				jo.put("name", i+"月");
				jo.put("id", year);
				jo.put("classDate", monthField);
				jo.put("pid", yearPath);
				jo.put("isParent", true);
	//			jo.put("xpath", path);
				array.add(jo);
			}
		}else{
			for(int i=12;i>0;i--){
				JSONObject jo = new JSONObject();
				jo.put("name", i+"月");
				jo.put("id", year);
				jo.put("classDate", monthField);
				jo.put("pid", yearPath);
				jo.put("isParent", true);
	//			jo.put("xpath", path);
				array.add(jo);
			}
		}
		return array.toString();
	}
	/**
	 * 返回天数
	 */
	@Override
	public String dayJson(long parseLong, String year, String month,
			String dayField) {
		JSONArray array = new JSONArray();
		String hql = "from SearchEntryMonth";
		List<SearchEntryMonth> seList = query(hql);
		String mysqlYDay = "";
		String mysqlYear = "";
		int day = 0;
		if(seList.size()>0){
			mysqlYear = seList.get(0).getEntryYear();
			mysqlYDay = seList.get(0).getEntryDay();
			day = Integer.parseInt(mysqlYDay);
		}
		int monthValue = Integer.parseInt(month);
		int yearValue = Integer.parseInt(year);
		 //获取当前时间  
		Calendar a = Calendar.getInstance();  
        a.set(Calendar.YEAR, yearValue);
        a.set(Calendar.MONTH, monthValue - 1); 
        a.set(Calendar.DATE, 1);  
        a.roll(Calendar.DATE, -1);  
        int maxDate = a.get(Calendar.DATE);  
//        JSONArray array = new JSONArray();
        if(day!=0 && year.equals(mysqlYear)){
        	for(int i=day;i>0;i--){
            	JSONObject jo = new JSONObject();
    			jo.put("name", i+"日");
    			jo.put("id", dayField);
    			jo.put("classDate", dayField);
    			jo.put("pid", year);
    			jo.put("month", month);
    			jo.put("isParent", false);
    			array.add(jo);
            }
        }else{
            for(int i=maxDate;i>0;i--){
            	JSONObject jo = new JSONObject();
    			jo.put("name", i+"日");
    			jo.put("id", dayField);
    			jo.put("classDate", dayField);
    			jo.put("pid", year);
    			jo.put("month", month);
    			jo.put("isParent", false);
    			array.add(jo);
            }
        }
		return array.toString();
	}

	@Override
	public String getEntryMainTime(Long type,String path) {
		List<ResCategory> resList = new ArrayList<ResCategory>();
		if(path.equals("0")){
			if(type!=1){
				resList = query("from ResCategory where type = "
						+ type + " and pid = " + path +" order by id desc");
			}else{
				resList = query("from ResCategory where type = "
						+ type + " and pid = " + path);
			}
			
		}else{
			if(type!=1){
				resList = query("from ResCategory where type = "
						+ type + " and pid = " + path + " order by id desc");
			}else{
				resList = query("from ResCategory where type = "
						+ type + " and pid = " + path);
			}
		}
		JSONArray array = new JSONArray();
		String hql = "from SearchEntryMonth";
		List<SearchEntryMonth> seList = query(hql);
		for(ResCategory rc : resList){
			JSONObject jo = new JSONObject();
			List<ResCategory> childrenList = query("from ResCategory where type = "
						+ type + " and pid = " + rc.getId());
			if(childrenList!=null&&childrenList.size()>0){
				jo.put("isParent", true);
				jo.put("classDate", "tmTimeClass");
			}else{
				jo.put("isParent", true);
				jo.put("classDate", "tmdate1");
			}
			
			jo.put("id", rc.getId());
			String name = "";
			if(type==1){
				name = StringUtils.isBlank(rc.getCode())?"[无]":"["+rc.getCode()+"]";
				name += rc.getName();
			}else{
				name = rc.getName();
			}
			jo.put("name", name);
			jo.put("code", rc.getCode());
			jo.put("pid", rc.getPid());
			jo.put("type", type);
			jo.put("xpath", rc.getPath());
			array.add(jo);
		}
		return array.toString();	
	}
}
