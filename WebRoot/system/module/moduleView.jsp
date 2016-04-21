<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp" %>
<html>
    <head>
        <title>菜单信息</title>
    </head>
     <body>
      	<div class="form-wrap">
      		<form action="" name="form1" method="POST" class="form-horizontal">
        	  <div class="form-group">
		                <label for="moduleName" class="col-sm-4 control-label text-right" >菜单名</label>
		                 <div class="col-sm-5">
		                 <p class="form-control-static">${command.moduleName}</p>
		                 </div>  
			</div>	
			 <div class="form-group">
		                <label for="parentId" class="col-sm-4 control-label text-right" >父菜单</label>
		                   <div class="col-sm-5">
			                    <p class="form-control-static">
					                 ${command.parentName}
				                 </p>
		                    </div>
			</div>	
			
			 <div class="form-group">
		                <label for="displayOrder" class="col-sm-4 control-label text-right">显示顺序</label>
		                 <div class="col-sm-5">
			                 <p class="form-control-static">
			                   ${command.displayOrder}
			                 </p>
		                 </div>  
			</div>	
			 <div class="form-group">
		                <label for="icon" class="col-sm-4 control-label text-right">图标</label>
		                 <div class="col-sm-5">
			                 <p class="form-control-static">
			                   ${command.css}
			                 </p>
		                 </div>  
			</div>	
			
			 <div class="form-group">
		                <label for="url" class="col-sm-4 control-label text-right" >起始url</label>
		                  <div class="col-sm-5">
		                 <textarea rows="3"  name="url" class="form-control" disabled="disabled">${command.url}</textarea>
		                 </div>
		                 
                          <span class="help-block">url示例:/module/list</span>

		                 
			</div>	
			
			<div class="form-group">
			
   					 <div class="col-sm-offset-4 col-sm-5" >
      						<input class="btn btn-primary" type="button" value="关闭 " onclick="javascript:$.closeFromInner();"/>
   		            </div>
  		  </div>
           
        </form>   
        </div>
    </body>
</html>