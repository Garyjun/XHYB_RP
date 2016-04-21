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
		<form action="updateMany.action" method="post" id="form" class="form-horizontal">
			<div class="form-group">
				<label  class="col-xs-5 control-label text-right">状态：</label>
				<div class="col-xs-5">
					<input type="radio" value="1" name="status" checked="checked"/>可用
					<input type="radio" value="0" name="status"/>禁用
				</div>
			</div>			
			<div class="form-group">
				<label  class="col-xs-5 control-label text-right">等级：</label>
				<div class="col-xs-5">
				    <input type="radio" value="1" name="level" checked="checked"/>高
					<input type="radio" value="2" name="level"/>中
					<input type="radio" value="3" name="level"/>低
				</div>
			</div>
			<div class="form-group">
				<div class="col-xs-offset-5">
					<div class="form-wrap">
					<input name="words" value="${ids}" type="hidden"/>
	           		<input type="hidden" name="token" value="${token}" />
	            	<input id="tijiao" type="submit" value="提交" class="btn btn-primary"/> 
	            	<input class="btn btn-primary" type="button" value="关闭" onclick="javascript:$.closeFromInner();"/>
	            	</div>
	            </div>
			</div>
		</form>
		</div>	
	</div>
</body>
</html>