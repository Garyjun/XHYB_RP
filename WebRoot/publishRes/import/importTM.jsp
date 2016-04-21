<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/appframe/common.jsp" %>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
	<title>编辑资源</title>
	<script type="text/javascript" src="${path}/appframe/main/js/libs/bootstrap-rating-input.min.js"></script>
	<link rel="stylesheet" href="${path}/appframe/plugin/zTree/css/zTreeStyle/zTreeStyle.css"/>
	<script type="text/javascript" src="${path}/appframe/plugin/zTree/js/jquery.ztree.all-3.5.js"></script>
<!-- 	<style>
		/* 该功能保证批次目录下的资源目录显示的也是文件夹图标，而不是文件图标 */
		ul.ztree li span.button.ico_docu{
			margin-right:2px; 
			margin-right:2px; 
			background-position: -147px 0; 
			vertical-align:top; 
			*vertical-align:middle
		}

	</style> -->
	<script type="text/javascript">
		var setting = {  
            data: {
        		simpleData: {
        			enable: true,
        			idKey: "id",
        			pIdKey: "pId",
        			rootPId: 0,
        		}
        	},
			check: {
				enable: true,
				chkStyle: "checkbox"
			}
        }; 
		
		var zTree;
		$(function(){  
            $.ajax({
				type : "post",
				url : "${path}/publishRes/getTMJson.action",
				success : function(data) {
					zNodes = eval('('+data+')');
                	zTree = $.fn.zTree.init($("#tree"), setting, zNodes);
				}
			});
        }); 
		
		function save(){
			var index = top.layer.msg('数据处理中，请稍候！', {icon: 4});
			var paths="";
			var nodes=zTree.getCheckedNodes(true);
			for(var i=0;i<nodes.length;i++){
				var nodePath = nodes[i].path;
				var nodeType = nodes[i].type;
				if(nodeType == "file"){
					if(nodePath!="" && typeof(nodePath)!="undefined"){
						paths +=nodePath + ",";
					}
				}
			}
			if(paths==""){
				top.layer.msg('请选择需要导入的批次！');
				return;
			}
			
			$.post("${path}/publishRes/saveResByTM.action",
				{
				paths: encodeURI(paths),
				publishType: $('#publishType').val()
				},
				function(data){
					if(data=='SUCCESS'){
						top.layer.close(index);
						top.layer.msg('文件上传成功!',{icon: 1});
						top.index_frame.work_main.freshDataTable('data_div');
						top.layer.closeAll("iframe"); //关闭信息框
					}else{
						layer.close(index);
						top.layer.msg('文件上传出错。请检查导入目录的文件！', {icon: 2});
					}
		    });
		}
	</script>
</head>
<body>
<div class="form-wrap">
	<input type="hidden" id="publishType" name="publishType" value="${param.publishType}"/>
	<form action="#" id="coreData" class="form-horizontal">
		<div class="form-group">
			<label class="col-sm-3 control-label text-right">文件目录：<span class="required">*</span></label>
			<div class="col-sm-9">
			<div style="overflow-y:auto; width:100%; height:100%;">
				<ul id="tree" class="ztree form-wrap"></ul>
			</div>
			</div>
		</div>
		<div class="form-group">
			<div class="col-sm-offset-3 col-sm-9">
               	<input id="tijiao" type="button" value="提交" class="btn btn-primary" onclick="save();"/> 
               	<input class="btn btn-primary" type="button" value="关闭 " onclick="parent.parent.layer.closeAll();"/>
            </div>
        </div>
	</form>
</div>
</body>
</html>