<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/appframe/common.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>列表</title>
	<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>
	<script type="text/javascript">
		var waitPanel;
		function returnList(){
			location.href = "wordList.jsp";
		}
		
		$(function(){
			$('#form').validationEngine('attach', {
				relative: true,
				overflownDIV:"#divOverflown",
				promptPosition:"bottomLeft",
				maxErrorsPerField:1,
				onValidationComplete:function(form,status){
					if(status){
						waitPanel = $.waitPanel('正在保存...',false);
						$('#form').ajaxSubmit({
							method: 'post',//方式
							success:(function(response){
								callback(response);
			       			})
						});
					}
				}
			});
		});
		
		function callback(data){
			waitPanel.close();
			top.index_frame.freshDataTable('data_div');
			$.closeFromInner();
		}
	</script>
</head>
<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap" style="height: 100%;">
		<div class="form-wrap">
		<form:form action="updWord.action" id="form" modelAttribute="frmWord" method="post" class="form-horizontal" role="form">
			<div class="form-group">
				<label class="col-xs-5 control-label text-right"><span class="required">*</span>敏感词：</label>
				<div class="col-xs-5">
					<form:input path="name"  class="form-control validate[required] text-input"/>
				</div>
			</div>
			<div class="form-group">
				<label class="col-xs-5 control-label text-right">相似词：</label>
				<div class="col-xs-5">
					<form:textarea path="similarWords" rows="5" class="form-control"/>
				</div>
			</div>
			<div class="form-group">
				<label  class="col-xs-5 control-label text-right">状态：</label>
				<div class="col-xs-5">
					<c:if test="${frmWord.id==null}">
						<form:radiobutton path="status" value="1" checked="true"/>可用
					</c:if>
					<c:if test="${frmWord.id!=null}">
						<form:radiobutton path="status" value="1"/>可用
					</c:if>
					<form:radiobutton path="status" value="0"/>禁用
				</div>
			</div>			
			<div class="form-group">
				<label  class="col-xs-5 control-label text-right">等级：</label>
				<div class="col-xs-5">
					<form:radiobutton path="level" value="1" checked="true"/>高
					<form:radiobutton path="level" value="2"/>中
					<form:radiobutton path="level" value="3"/>低
				</div>
			</div>
			<div class="form-group">
				<div class="col-xs-offset-5">
					<div class="form-wrap">
					<form:hidden path="id" />
	           		<input type="hidden" name="token" value="${token}" />
	            	<input id="tijiao" type="submit" value="提交" class="btn btn-primary"/> 
	            	<input class="btn btn-primary" type="button" value="关闭" onclick="javascript:$.closeFromInner();"/>
	            	</div>
	            </div>
			</div>
		</form:form>
		</div>	
	</div>
</body>
</html>