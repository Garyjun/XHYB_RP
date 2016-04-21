package com.brainsoon.statistics.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.statistics.po.vo.ResBookVo;
import com.brainsoon.statistics.service.IBookNumService;

@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class BookNumAction extends BaseAction{

	private final String baseUrl = "/statistics/bookNum/";
	
	@Autowired
	private IBookNumService bookService;
	/**
	 * 素材资源统计
	 */
	@RequestMapping(value=baseUrl+"sucaiNum")
	@ResponseBody
	public String sucaiNum(HttpServletRequest requset){
		String rusult = bookService.querySuCai("","");
		return rusult;
	}
	
	
	/**
	 * 素材资源表格样式统计
	 */
	@RequestMapping(value=baseUrl+"sucaiNumTab")
	@ResponseBody
	public String sucaiNumTab(HttpServletRequest requset){
		StringBuffer sb = new StringBuffer();
		sb.append("<table width=\"350\" height=\"200\" border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
		sb.append("		<tr align=\"center\">");
		sb.append("			<td>名称</td>");
		sb.append("			<td>数量</td>");
		sb.append("		</tr>");

		String rusult = bookService.querySuCai("","");
		JSONArray arr = JSONArray.fromObject(rusult);
		if(arr != null){
			for(int i=0;i<arr.size();i++){
				JSONObject jsonObjecty = arr.getJSONObject(i);
				Object nameObject = jsonObjecty.get("names");
				if(nameObject != null){
					String name = jsonObjecty.getString("names");
					sb.append("	<tr align=\"center\">");
					sb.append("		<td>"+name+"</td>");		
				}
				Object countObject = jsonObjecty.get("counts");
				if(countObject != null){
					String count = jsonObjecty.getString("counts");
					sb.append("		<td>"+count+"</td>");
					sb.append("	</tr>");
				}
			}
		}
		return sb.toString();
	}
	
	
	/**
	 * 图书资源统计
	 * @param request
	 * @return
	 */
	@RequestMapping(value=baseUrl+"bookNum")
	@ResponseBody
	public String bookNum(HttpServletRequest request){
		
		//查询资源在库中对应的目录的资源个数
		String rusult = bookService.queryBook("","");
		JSONArray arr = JSONArray.fromObject(rusult);
		
		StringBuffer sb = new StringBuffer(); 
		
		//存放着一级名称对应的所有二级元素
		//格式map<"parentId,parentName",List<二级实体>
		Map<String,List<ResBookVo>> resAll = new HashMap<String, List<ResBookVo>>();
		
		try {
			//查询出所有的一级节点
			String hql = "from ResBookVo r where r.parentId = 0 and r.type = 4";
			List<ResBookVo>  resParent = bookService.query(hql);
			
			//查询出所有的二级节点
			hql = "from ResBookVo r where r.parentId != 0 and r.type = 4";
			List<ResBookVo> resSon = bookService.query(hql);
			
			
			//将一级对应的所有二级放在list中，存放在map中
			for(int i=0;i<resParent.size();i++){
				List<ResBookVo> sonList = new ArrayList<ResBookVo>();
				for(int j=0;j<resSon.size();j++){
					if(resParent.get(i).getId() == resSon.get(j).getParentId()){
						sonList.add(resSon.get(j));
					}
				}
				resAll.put(resParent.get(i).getId()+","+resParent.get(i).getName(), sonList);
				//System.out.println(resAll.get(resParent.get(i).getId()+","+resParent.get(i).getName()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//获取map集合中的所有键，存入到Set集合中，  
	 	Set<Entry<String, List<ResBookVo>>> entry = resAll.entrySet();  
        //通过迭代器取出map中的键值关系，迭代器接收的泛型参数应和Set接收的一致  
        Iterator<Entry<String, List<ResBookVo>>> it = entry.iterator();
        
        
       // sb.append("	<div id=\"fakeFrame\" class=\"container-fluid by-frame-wrap\" style=\"width: 85%\">");
        sb.append("		<div class=\"panel panel-default\">");
        sb.append("			<div class=\"panel-heading\" id=\"div_head_t\">");
        sb.append("				<ol class=\"breadcrumb\">");
        sb.append("					<li><a href=\"javascript:;\">图书分类统计</a></li>");
        sb.append("				</ol>");
        sb.append("			</div>");
        sb.append("			<div align=\"center\" style=\"margin-top: 40px;\">");
        sb.append("				<table width=\"650\" border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
        sb.append("					<tr align=\"center\">");
        sb.append("						<td height=\"50px\">一级分类</td>");
        sb.append("						<td>分类数量</td>");
        sb.append("						<td>二级分类</td>");
        sb.append("						<td>分类数量</td>");
        sb.append("					</tr>");
        //循环map和查询库中存放资源的json串进行比对拼接
        while (it.hasNext()){  
            //将键值关系取出存入Map.Entry这个映射关系集合接口中  
            Entry<String, List<ResBookVo>>  me = it.next();
            //使用Map.Entry中的方法获取键和值  
            String key = me.getKey();
            List<ResBookVo> sonMap = me.getValue();
            String id = key.substring(0, key.indexOf(","));   //一级节点id
            String name = key.substring(key.indexOf(",")+1, key.length()); //一级节点名称
           // String value = me.getValue();  
            
            
            
            
            //循环目录下资源的个数
            if(arr != null){
            	int record = 0;  //0  表示还没有生成第一个tr
            	for(int i=0;i<arr.size();i++){
            		//说明一级、二级已经生成，所以直接跳出
            		//针对于的数据
            		//{"count":4,"objectId":"48160,48171","sum":0.0},
            		//{"count":4,"objectId":"48168","sum":0.0},
            		//{"count":4,"objectId":"48161,48163","sum":0.0},
            		//{"count":2,"objectId":"48161,48162","sum":0.0}]
            		if(record == 1){	
            			break;
            		}
            		JSONObject jsonObjecty =  arr.getJSONObject(i);
            		if(jsonObjecty != null){
            			String level = jsonObjecty.getString("level");
            			String currentId = jsonObjecty.getString("currentId"); //获取单签目录id
            			String number = jsonObjecty.getString("number");	//获取当前目录下有多少个资源
            			if(level.equals("1")){
            				if(currentId.equals(id)){
            					record = 1;		//表示已经生成了第一个tr
            					sb.append("<tr align=\"center\">");
            					sb.append("		<td rowspan="+sonMap.size()+">"+name+"</td>");
            					sb.append("		<td style=\"color:red\" rowspan="+sonMap.size()+">"+number+"</td>");
            				}
            			}
            			
            			//说明返回的json数据中没有此一级目录的资源
            			if(record ==0 && i==arr.size()-1){
            				record = 1;		//表示已经生成了第一个tr
        					sb.append("<tr align=\"center\">");
        					sb.append("		<td rowspan="+sonMap.size()+">"+name+"</td>");
        					sb.append("		<td rowspan="+sonMap.size()+" style=\"color:red\">0</td>");
            			}
            			
            			if(record == 1){
            				
            				int sonFirst = 0;	//0 表示二级的第一个还未生成    1表示二级的第一个已生成
            				for(int m=0;m<sonMap.size();m++){
            					ResBookVo book = sonMap.get(m);
            					
            					for(int k=0;k<arr.size();k++){
            						int sonRecord = 0;		//0表示返回的数据中没有此资源   1表示返回的数据中有此资源
            						JSONObject jsonBook =  arr.getJSONObject(k);
            						if(jsonBook != null){
            	            			String levelSon = jsonBook.getString("level");
            	            			String currentIdSon = jsonBook.getString("currentId"); //获取单签目录id
            	            			String numberSon = jsonBook.getString("number");	//获取当前目录下有多少个资源
            	            			String parentId = jsonBook.getString("parent"); 	//父级目录id
            	            			if(levelSon.equals("2")){
            	            				//生成的是第一个二级目录
            	            				if(Integer.valueOf(parentId) == book.getParentId() && currentIdSon.equals(book.getPath()) && sonFirst ==0){
            	            					sonRecord = 1;
            	            					sonFirst = 1;
            	            					sb.append("	<td height=\"35px\">"+book.getName()+"</td>");
            	            					sb.append("	<td style=\"color:orange\">"+numberSon+"</td>");
            	            					sb.append("</tr>");
            	            					break;
            	            				}
            	            				//生成的不是第一个二级目录
            	            				if(Integer.valueOf(parentId) == book.getParentId() && currentIdSon.equals(book.getPath()) && sonFirst== 1){
            	            					sonRecord = 1;
            	            					sb.append("<tr align=\"center\">");
                	            				sb.append("		<td height=\"35px\">"+book.getName()+"</td>");
                	            				sb.append("		<td style=\"color:orange\">"+numberSon+"</td>");
            	            					sb.append("</tr>");
            	            					break;
            	            				}
            	            				
            	            			}
            	            			
            	            			//生成的是第一个二级目录
            	            			if(sonFirst==0 && sonRecord == 0 && k == (arr.size()-1)){
            	            					sonFirst = 1;
            	            					sonRecord = 1;
            	            					sb.append("	<td height=\"35px\">"+book.getName()+"</td>");
            	            					sb.append("	<td style=\"color:orange\">0</td>");
            	            					sb.append("</tr>");
            	            			}
            	            			
            	            			
            	            			//生成的不是第一个二级目录
            	            			if(sonFirst == 1 && k == (arr.size()-1) && sonRecord == 0){
            	            				sb.append("<tr align=\"center\">");
            	            				sb.append("		<td height=\"35px\">"+book.getName()+"</td>");
            	            				sb.append("		<td style=\"color:orange\">0</td>");
        	            					sb.append("</tr>");
            	            			}
            	            		}
                				}
            				}
            				if(sonMap.isEmpty()){
            					
            					sb.append("	<td height=\"35px\"></td>");
            					sb.append("	<td style=\"color:red\"></td>");
            					sb.append("</tr>");
            				}
            			}
            		}
            	}
            }
        }  
        
        sb.append("			</table>");
        sb.append("		</div>");
        sb.append("	</div>");
		return sb.toString();
	}
}
