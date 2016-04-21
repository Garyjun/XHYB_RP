package com.brainsoon.system.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import net.sf.json.JSONArray;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.appframe.util.StringUtil;
import com.brainsoon.common.exception.DaoException;
import com.brainsoon.common.service.IBaseService;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.common.support.Constants;
import com.brainsoon.common.support.GlobalDataCacheMap;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.system.model.DictName;
import com.brainsoon.system.model.DictValue;
import com.brainsoon.system.model.MetaDataModelGroup;
import com.brainsoon.system.service.IDictValueService;
@Service
public class DictValueService extends BaseService implements IDictValueService {

	private static IBaseService baseQueryService = null;
	private JdbcTemplate jdbcTemplate ;
	@Autowired
	public void init(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	@Override
	public void addDictValue(DictValue dictValue,String indexTag) {
		create(dictValue);
	}
	
	@Override
	public List validateDictName(String sql) {
		return jdbcTemplate.queryForList(sql);
	}

	@Override
	public void updDictValue(DictValue dictValue,String indexTag) {
		saveOrUpdate(dictValue);
	}

	@Override
	public void deleteByIds(String ids,String indexTag) {
		if(StringUtils.isNotBlank(ids)){
			delete(DictValue.class, ids);
		}
		
	}
	
	@Override
	public String getIndexTagByName(String name) {
		List<DictValue> list = new ArrayList<DictValue>();
		int platformId = LoginUserUtil.getLoginUser().getPlatformId();
		try {
			baseQueryService = (IBaseService) BeanFactoryUtil.getBean("baseService");
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("name", name);
			parameters.put("platformId", platformId);
			list = baseQueryService.query(" from DictValue dc where dc.name=:name and platformId=:platformId", parameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list.get(0).getIndexTag();
	}
	
	@Override
	public String getNameByIndexTag(String indexTag) {
		List<DictValue> list = new ArrayList<DictValue>();
		int platformId = Integer.parseInt(Constants.EDU_RES_1);
		try {
			baseQueryService = (IBaseService) BeanFactoryUtil.getBean("baseService");
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("indexTag", indexTag);
			parameters.put("platformId", platformId);
			list = baseQueryService.query(" from DictValue dc where dc.indexTag=:indexTag and platformId=:platformId", parameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list.get(0).getName();
	}
	
	@Override
	public String getIndexTagByNameNoPlatFormId(String name) {
		List<DictValue> list = new ArrayList<DictValue>();
		//int platformId = LoginUserUtil.getLoginUser().getPlatformId();
		try {
			baseQueryService = (IBaseService) BeanFactoryUtil.getBean("baseService");
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("name", name);
			list = baseQueryService.query(" from DictValue dc where dc.name=:name", parameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list.get(0).getIndexTag();
	}
	
	public List<MetaDataModelGroup> getContentByTableName(String name){
		Map<String,String> map = new HashMap<String, String>();
		List<MetaDataModelGroup> content = null;
		try {
			content = query("from MetaDataModelGroup order by maxOrder");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}
	
	public String getDictValuesByPId(Long pid){
		List<DictValue> list = new ArrayList<DictValue>();
		String array = "";
		try {
			baseQueryService = (IBaseService) BeanFactoryUtil.getBean("baseService");
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("pid", pid);
			list = baseQueryService.query("from DictValue dc where dc.pid=:pid", parameters);
			for(DictValue dv : list){
				String values = dv.getId() + "=" + dv.getName()+"="+dv.getIndexTag();
				array += values + ",";
			}
			if(list.size()>0){
				array = array.substring(0, array.length()-1);
			}
		} catch (DaoException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return array.toString();
	}
	
	/**
	 * 批量添加数据字典项
	 */
	@Override
	public boolean addDictValueTxt(File file, String status,String pid) {
		boolean result = false;
		try{
			String encoding = "UTF-8";
			if(file.isFile() && file.exists()){  //判断文件是否存在
				//建立一个输入流对象（file为文件路径）
				InputStreamReader read = new InputStreamReader(new FileInputStream(file),encoding);
				//建立一个对象，它把文件内容转成计算机能读懂的语言
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while(StringUtils.isNotBlank(lineTxt  = bufferedReader.readLine())){
					String dValue[] = lineTxt.split(",");
					//参数名称和参数值为必填项
					if(StringUtils.isNotBlank(dValue[0]) && StringUtils.isNotBlank(dValue[2])){
						DictValue dictValue = new DictValue();
						dictValue.setStatus(status);
						dictValue.setPid(Long.parseLong(pid));
						dictValue.setName(dValue[0]);
						dictValue.setShortname(dValue[1]);
						dictValue.setIndexTag(dValue[2]);
						try{
							dictValue.setDescription(dValue[3]);
						}catch(Exception e){
							e.printStackTrace();
						}
						
						create(dictValue);
					}else{
						continue;
					}
					
				}
				read.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * 根据ID查询数据字典名称
	 * @param id
	 * @return
	 */
	@Override
	public String getDictValueById(String id){
		String title = null;
		while(!StringUtils.isBlank(id)){
			List<DictValue> dictList = query("from DictValue where id="+Integer.parseInt(id));
			if(dictList!=null&&dictList.size()>0){
				title = dictList.get(0).getName();
				break;
			}
		}
		return title;
	}
}
