package com.brainsoon.statistics.service.impl;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.date.DateUtil;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.dfa.service.IWordsService;
import com.brainsoon.dfa.util.WordsUtil;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.File;
import com.brainsoon.semantic.ontology.model.SearchParamCa;
import com.brainsoon.semantic.ontology.model.SearchResultCa;
import com.brainsoon.statistics.po.RespsOfResourceWord;
import com.brainsoon.statistics.po.RespsOfResourceWordFile;
import com.brainsoon.statistics.service.IResourceOfWordService;
import com.brainsoon.system.model.DictValue;
import com.brainsoon.system.service.IMetaDataModelService;
import com.brainsoon.system.service.ISysDirService;
import com.ctc.wstx.util.DataUtil;
import com.google.gson.Gson;

@Service(value="resourceOfWordService")
public class ResourceOfWord extends BaseService implements IResourceOfWordService {
	
	@Autowired
	private IMetaDataModelService metaDataModelService;
	
	private String[] fileType = {"pdf", "txt", "doc", "docx"};
	
	//敏感词等级为中级的敏感词
	private String level = "3,";
	
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private ISysDirService sysDirService;
	
	@Autowired
	 public void init(DataSource dataSource) {
	       this.jdbcTemplate = new JdbcTemplate(dataSource);
	 }
	
	/**
	 * 比较系统中配置的敏感词和数据库中存在的敏感词是否发生变化
	 * @return
	 */
	public boolean checkWord(){
		boolean compare = false;
		try {
			//数据库中查询出中级敏感词(敏感词表)
			IWordsService wordsService = (IWordsService) BeanFactoryUtil.getBean("wordsService");
			List<String> levelWords = wordsService.getWordsByLevel(level.split(","));
			
			//从数据字典中查询出上一次过滤记录的敏感词
			String wordList = sysDirService.findWords();
			String[] wordGroup = wordList.split(",");
			
			if(levelWords.size() != wordGroup.length){
				compare = true;
				return compare;
			}else{
				for (String groupWord : wordGroup) {
					for (String levalWord : levelWords) {
						if(!groupWord.equals(levalWord)){
							compare = true;
							return compare;
						}
					}
				}
			}
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return compare;
	}
	
	
	/**
	 * 当敏感词表中的敏感词有更新，需要将项目中记录敏感词的数据字典进行更新
	 */
	public void resetWord(){
		
		//数据库中查询出中级敏感词(敏感词表)
		IWordsService wordsService;
		try {
			String sql = "delete from dict_value where pid = 225";
			jdbcTemplate.execute(sql);
			
			wordsService = (IWordsService) BeanFactoryUtil.getBean("wordsService");
			List<String> levelWords = wordsService.getWordsByLevel(level.split(","));
			if(levelWords.size()>0){
				for (String string : levelWords) {
					DictValue dicrEntity = new DictValue();
					dicrEntity.setName(string);
					dicrEntity.setIndexTag(string);
					dicrEntity.setStatus("1");
					dicrEntity.setPid(Long.parseLong("225"));
					dicrEntity.setPlatformId(1);
					sysDirService.create(dicrEntity);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	//过滤资源下的文件的敏感词
	@Override
	public void doResourceOfWord() {
		
		//比较系统中配置的敏感词和数据库中存在的敏感词是否发生变化
		boolean compare = checkWord();
		if(compare){
			try{
				//重新设置系统中数据字典的敏感词
				resetWord();
				
				
				String sql1 = "delete from sys_resource_word";
				String sql2 = "delete from sys_resource_word_file";
				jdbcTemplate.execute(sql1);
				jdbcTemplate.execute(sql2);
				String rsql1 = "truncate table sys_resource_word";
				String rsql2 = "truncate table sys_resource_word_file";
				jdbcTemplate.execute(rsql1);
				jdbcTemplate.execute(rsql2);
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		
		
		//查询所有资源
		
		String resSql = "select id from sys_ResMetadata_Type";
		List<String> ResIdList = null;
		String resMetadaIds = null;    	//记录所有的资源id
		String userSql = "select id from sys_user";
		List<String> userIdList = null;
		String userIds = null;    		//记录所有的用户id
		try{
			ResIdList = jdbcTemplate.queryForList(resSql, String.class);
			userIdList = jdbcTemplate.queryForList(userSql, String.class);
			if(ResIdList.size() > 0){
				resMetadaIds = StringUtils.substringBetween(ResIdList.toString(), "[","]");
			}
			if(userIdList.size()>0){
				userIds = StringUtils.substringBetween(userIdList.toString(), "[","]");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		Gson gson = new Gson();
		HttpClientUtil http = new HttpClientUtil();
		SearchParamCa searchParamCa = new SearchParamCa();
		//searchParamCa.setInterfaceQuery("resource");
		searchParamCa.setCreator(userIds);
		searchParamCa.setPublishType(resMetadaIds);
		searchParamCa.setSorts("create_time");
		searchParamCa.setOrders("desc");
		searchParamCa.setQueryType(0);
		searchParamCa.setPage("1");
		searchParamCa.setSize(50000);
		String paramStrs = gson.toJson(searchParamCa);
		String result = http.postJson(WebappConfigUtil.getParameter("PUBLISH_QUERYBYPOST_URL"), paramStrs);
		
		try{
			//解析资源和资源文件
			SearchResultCa caList = gson.fromJson(result, SearchResultCa.class);
			if(caList!=null && !caList.getRows().isEmpty()){
				List<Ca> cas = caList.getRows();
				//获取存放资源的list
				if(cas!=null && cas.size()>0){
					for(int i=0;i<cas.size();i++){
						boolean haveWord = false;
						String word = null;
						//资源的实体类 
						RespsOfResourceWord resourceEntity = null;
						
						//获取保存每个资源的实体类
						Ca ca = cas.get(i);
						logger.info("==================="+ca+"-------*********-*----*-*-*-*-*/-");
						//获取资源的id
						String resourceId = ca.getObjectId();
						
						String resourceWord = "";
						/**
						 * 根据资源id查询数据库，
						 * 1.若只有一个那么对此实体进行更新保存。
						 * 2.若大于一个则说明出现错误全部删掉，重新创建，并且删除资源下对应的文件
						 * 3.若没查询出来，说明是新增加的，则New
						 */
						String hql = "from RespsOfResourceWord where resourceId = '"+resourceId+"'";
						List<RespsOfResourceWord> resLists = getBaseDao().query(hql);
						if(resLists.size()>1){
							String resIds = "";
							for(int p=0; p<resLists.size(); p++){
								//删除资源下的文件
								String sql = "delete from sys_resource_word_file where resource_id = '"+resLists.get(p).getResourceId()+"'";
								jdbcTemplate.execute(sql);
								resIds+= ",";
								resIds+=resLists.get(p).getId();		
							}
							if(resIds.length()>0){
								try{
									resIds = resIds.substring(1, resIds.length());
								}catch(Exception e){
									e.printStackTrace();
								}
								//删除资源
								getBaseDao().delete(RespsOfResourceWord.class, resIds);
							}
							resourceEntity = new RespsOfResourceWord();
						}else if(resLists.size() ==1){
							resourceEntity = resLists.get(0);
							word = resourceEntity.getHaveWord();
						}else{
							resourceEntity = new RespsOfResourceWord();
						}
						
						
						//获取每个资源的名称
						Map<String,String> metadataMap = ca.getMetadataMap();
						String resourceName = metadataMap.get("title");
						
						//所属的资源类型
						String resourceTypeId = ca.getPublishType();
						resourceEntity.setResourceName(resourceName);
						resourceEntity.setResourceId(resourceId);
						resourceEntity.setResourceTypeId(resourceTypeId);
						resourceEntity.setStatus("已过滤");
						
						
						
						/**
						 * 文件的getIsDir为2时进行是否过滤操作
						 * 2表示是文件，1.表示是文件夹
						 * 
						 * 核查查询到的文件和数据库中存在的文件的最后更新时间
						 * 1.如果从实体中获得的更新时间为空，那么说明该文件是第一次保存，或者是新上传的文件，数据库中还不存在
						 * 则要进行对敏感词的过滤
						 * 2.如果从实体中获取的最后更新时间不为空，并且和接口中查询出来的文件的最后更新时间不等，
						 * 则说明该文件在最后过滤之后有更改，要进行过滤
						 * 3.如果从实体中获取的最后更新时间不为空，并且和接口中查询出来的文件的最后更新时间相等
						 * 则不用再重复对敏感词过滤
						 */
						//处理每个资源下的文件
						String resultFile=  http.executeGet(WebappConfigUtil// + objectId
								.getParameter("PUBLISH_DETAIL_URL") + "?id="+resourceId);
						
						try{
							Ca bookCa = gson.fromJson(resultFile, Ca.class);
							if(bookCa.getRealFiles()!= null && bookCa.getRealFiles().size()>0){
								//获取资源下的每个文件
								List<File> resourceFiles = bookCa.getRealFiles();
								for (File file : resourceFiles) {
									logger.info("++++++++++++++*********"+file+"@@@@@@@@@@@######$$$$$$$$$");
									//资源下文件的实体类
									RespsOfResourceWordFile resourceFileEntity = null;
									
									//控制文件是否过滤
									boolean filtered = false;
									
									//获取每个资源的路径
									String filePath = file.getPath();
									
									//文件的绝对路径和文件别名
									String fileRealPath = WebAppUtils.getWebAppBaseFileDirFR()+
											filePath;
									
									fileRealPath = fileRealPath.replaceAll("\\\\", "/");
									//根据路径获取文件的扩展名
									String name = fileRealPath.substring(fileRealPath.lastIndexOf(".") + 1,
											fileRealPath.length());
									logger.info("-----------*********"+name+"%%%%%%%%%%%%%%$$$$$$$$$4$$$$$");
									//判断当前文件是文件还是文件夹,只有是文件时才进行过滤
									////判断筛选敏感词的文件是否包含该文件的扩展名
									if(file.getIsDir().equals("2") && Arrays.asList(fileType).contains(name)){
										String fileSql = "from RespsOfResourceWordFile where fileId ='"+file.getObjectId()+"'";
										List<RespsOfResourceWordFile> fileLists = getBaseDao().query(fileSql);
										if(fileLists.size()>0){
											resourceFileEntity = fileLists.get(0);
											//获取文件最后更新时间
											String updateTime = DateUtil.convertLongToString(file.getModified_time());
											//判断接口中的最后更新时间和数据库中存在的最后更新时间不等重新出过滤
											if(!DateUtil.convertLongToString(resourceFileEntity.getUpdateTime()).equals(updateTime)){
												filtered = true;
											}
										}else{
											resourceFileEntity = new RespsOfResourceWordFile();
											filtered = true;
										}
										
										
										//判断文件的更新时间，是否还需要对文件进行过滤,true重新过滤，false不用过滤
										//保存资源对应的文件表中，如果资源中的文件不包含敏感词，那么就不对该文件进行保存
										/**
										 * filtered:true  表示文件有过更改需要重新更新
										 * compare :true  表示敏感词个数或者内容有变化，需要对文件重新更新
										 */
										if(filtered){
											try {
												//筛选出该文件中包含的敏感词
												Map<Integer, String[]> checkMaps = WordsUtil
														.checkWordsByPage(fileRealPath, level.split(","));
												if(checkMaps!=null && checkMaps.size()>0){
													//返回值同一敏感词出现多次，那么此敏感词返回多个
													for (Integer pageKey : checkMaps.keySet()) {
														String[] wordList = checkMaps.get(pageKey);
														//去重，去除重复的敏感词
														if(wordList.length>0){
															Set set = new TreeSet();
															for (String string : wordList) {
																if(StringUtils.isNotBlank(string)){
																	set.add(string);
																}
															}
															wordList =  (String[]) set.toArray(new String[0]);
															//获取文件中包含的敏感词，保存
															if(wordList.length>0){
																String fileWords = "";
																for(int k=0;k<wordList.length;k++){
																	fileWords += ",";
																	fileWords +=wordList[k];
																}
																if(StringUtils.isNotBlank(fileWords)){
																	fileWords = fileWords.substring(1, fileWords.length());
																	resourceWord += fileWords+",";
																	//文件所包含的敏感词
																	resourceFileEntity.setWord(fileWords);
																	haveWord = true;
																	//设置文件所属的资源id
																	resourceFileEntity.setResourceId(resourceId);
																	//设置文件的扩展名 
																	resourceFileEntity.setFileName(name);
																	//设置文件的路径
																	resourceFileEntity.setFilePath(file.getId());
																	//设置文件的id
																	resourceFileEntity.setFileId(file.getObjectId());
																	//设置文件的更新时间
																	resourceFileEntity.setUpdateTime(file.getModified_time());
																	resourceFileEntity.setFileRealPath(filePath);
																	getBaseDao().saveOrUpdate(resourceFileEntity);
																	
																}else{
																	continue;
																}
																
															}else{
																continue;
															}
														}
													}
												}
												
											} catch (Exception e) {
												e.printStackTrace();
											}
											
											
												
										}else{
											continue;
										}
								  }
								}
							}
						}catch(Exception e){
							e.printStackTrace();
						}
						
						if(haveWord){
							resourceEntity.setHaveWord("包含");
							if(StringUtils.isNotBlank(resourceWord)){
								resourceWord = resourceWord.substring(0,resourceWord.length()-1);
								resourceEntity.setResourceWord(resourceWord);
							}
							
						}else if(StringUtils.isNotBlank(word)){
							resourceEntity.setHaveWord(word);
						}else{
							resourceEntity.setHaveWord("不包含");
						}
						//保存或更新资源的实体类
						getBaseDao().saveOrUpdate(resourceEntity);
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	
	/**
	 * List页面显示查询资源下文件是否包含敏感词
	 */
	@Override
	public String findWord(String resourceId) {
		String sql = "select word from sys_resource_word_file where resource_id = '"+resourceId+"'";
		List<String> resFiles = null;
		String result = "不包含";
		try{
			resFiles = jdbcTemplate.queryForList(sql, String.class);
			if(resFiles.size()>0){
				for (String string : resFiles) {
					if(StringUtils.isNotBlank(string)){
						result = "包含";
						break;
					}
				}
			}else{
				result = "不包含";
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}

	
}
