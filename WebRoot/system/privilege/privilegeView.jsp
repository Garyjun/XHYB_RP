<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp" %>
<html>
    <head>
        <title>查看</title>
    </head>
     <body>
       <div class="form-wrap">
      	<div>
      		<form action="" name="form1" method="POST" class="form-horizontal">
        	<div class="form-group">
		                <label for="privilegeName" class="col-sm-4 control-label text-right" >权限名</label>
		                 <div class="col-sm-5">
		                  <p class="form-control-static">${command.privilegeName}</p>
		                 </div>  
			</div>	
			 <div class="form-group">
		                <label for="command.fullName" class="col-sm-4 control-label text-right" >所属菜单</label>
		                   <div class="col-sm-5">
			                   <p class="form-control-static">${command.module.moduleName}</p>
		                    </div>
			</div>	
			
			 <div class="form-group">
		                <label for="displayOrder" class="col-sm-4 control-label text-right">显示顺序</label>
		                 <div class="col-sm-5">
		                  <p class="form-control-static">${command.displayOrder}</p>
		                 </div>  
			</div>	
			
			 <div class="form-group">
		                <label for="urls" class="col-sm-4 control-label text-right" >url</label>
		                  <div class="col-sm-5">
		                 <textarea rows="5"  disabled="disabled" name="urls" class="form-control">${command.urls}</textarea>
		                 </div>
			</div>	
			
			<div class="form-group">
   					 <div class="col-sm-offset-4 col-sm-5" align="center">
      					<input class="btn btn-primary" type="button" value="关闭 " onclick="javascript:$.closeFromInner();"/>
   					 </div>
  		  </div>
        	
        </form>   
      	</div>
        
        </div>
    </body>
</html>