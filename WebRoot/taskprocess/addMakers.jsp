<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>列表</title>
	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
	<script type="text/javascript">
		var waitPanel;
		$(document).ready(function(){
		});
		
		function callback(data){
			$.closeFromInner();
		}
		
		function savaMakers(){
			var makersId = getMakers();
			if(makersId!=null&&makersId!=""){
				$.post("${path}/taskProcess/saveMaker.action",{
					makers:			makersId,
					taskProcessId:	"${taskProcessId}"
				},function(data){					
					if(data!=-1){
						$.alert("添加成功！");
						var parentWin =  top.index_frame.work_main;
						
						parentWin.freshDataTable('data_div_user');
						$.closeFromInner();
					}
					else
						$.alert("添加失败！");
				});	
			}else{
				$.alert("请选择要添加的加工员！");
			}
		}
		
		function getMakers(){
			var makers = "";
			 $("input[name='makers']").each(function(){
				 if($(this).attr("checked") == "checked"){
					 makers += $(this).val() + ",";
				 }
			 });
			 return makers;
		}
	</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap" style="height: 100%;">
			<div class="form-group">
				<label class="col-xs-4 control-label text-right">请选择加工员：</label>
				<div class="col-xs-8">
		        	<c:forEach items="${makers}" var="maker" varStatus="status">
		        		<label class="checkbox-inline">
		        			<input type="checkbox" name="makers" value="${maker.id}"/>${maker.userName}
		        		</label>
		        		<c:if test="${status.index !=0 && status.index % 4 == 0}">
		        			<br />
		        			<br />
		        		</c:if>
		        		<c:if test="${status.isLast()}">
		        			<br />
		        			<br />
		        		</c:if>
		        	</c:forEach>					
				</div>
			</div>		

			<div class="form-group">
				<div class="col-xs-offset-5">
					<div class="form-wrap">
	           		<input type="hidden" name="token" value="${token}" />
	            	<input type="button" value="提交" class="btn btn-primary" onclick="savaMakers();"/> 
	            	<input class="btn btn-primary" type="button" value="关闭" onclick="javascript:$.closeFromInner();"/>
	            	</div>
	            </div>
			</div>
	</div>
</body>
</html>