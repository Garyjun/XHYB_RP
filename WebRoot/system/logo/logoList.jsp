<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/appframe/common.jsp" %>

<html>
	<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
	<title>Logo管理</title>
	<link rel="stylesheet" type="text/css" href="${path}/appframe/plugin/uploadify/uploadify.css" />
	<script type="text/javascript" src="${path}/appframe/plugin/uploadify/jquery.uploadify.js"></script>
    <script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>


	<script type="text/javascript">
	
	      //表单验证通过,开始上传
		 function uploadFile(){
			var files = $("#fileData").data('uploadify').queueData.files;
			var fileId = '';
			console.info(files);
			for (var n in files) {
				fileId = files.id;
			}
			if(fileId != ''){
				$('#fileData').uploadify('upload','*')
			}else{
				$.alert('请选择上传的文件！');
			}
		} 
		
		$(function(){
			//定义一datagrid
 	   		var _divId = 'data_div';  //datagrid展示的位置
 	   		var _url = '${path}/logo/query.action';
 	   		var _pk = 'id';
 	   		var _conditions = ['logoName'];
 	   		var _sortName = 'id';
 	   		var _sortOrder = 'desc';
 	   		var _check=false;
 			var _colums = [ 
 						    { title:'编号', field:'id' ,width:100, align:'center'},
 						    { title:'状态', field:'status' ,width:100, align:'center',hidden:'true'},
 							{ title:'Logo图片', field:'logoName' ,width:100, align:'center' ,formatter:$logo},
 							{title:'操作',field:'opt1',width:fillsize(0.12),align:'center',formatter:$operate}
 						];
 			accountHeight();
 			$grid = $.datagrid(_divId,_url,_pk,_colums,_conditions,_sortName,_sortOrder,_check);
		    
 			//文件上传
 			$('#fileData').uploadify({
		    	//‘uploadify.swf’	uploadify.swf 文件的相对路径
		        'swf'      : '${path}/appframe/plugin/uploadify/uploadify.swf',
		        //后台处理程序的相对路径
		        'uploader' : '${path}/logo/upload.action',
		        //如果设置为True则表示启用SWFUpload的调试模式
		        'debug' : false,
		        //是否自动上传
		        'auto':false,
		        //设置为true时，则可以上传多个文件
		        'multi':false,
		      	//队列最多显示的任务数量，如果选择的文件数量超出此限制，将会出发onSelectError事件。
				//注意此项并非最大文件上传数量，如果要限制最大上传文件数量，应设置uploadLimit。
		        'queueSizeLimit':1,
		      	//浏览按钮的文本
		        'buttonText':'选择文件', 
		        //是否自动将已完成任务从队列中删除，如果设置为false则会一直保留此任务显示
		        'removeCompleted':false,
// 		        'fileDesc': "请选择zip或rar文件",
		        'fileTypeExts':'*.jpg;*.png',
		      	//当文件即将开始上传时立即触发
		        'onUploadStart': function (file) { 
		        	$("#file_upload").uploadify("settings", "formData", {});
            	},
            	 //返回一个错误，选择文件的时候触发
		        'onSelectError':function(file, errorCode, errorMsg){
		            switch(errorCode) {
		                case -100:
		                    $.alert("上传的文件数量已经超出系统限制的"+$('#fileData').uploadify('settings','queueSizeLimit')+"个文件！");
		                    break;
		                case -110:
		                    $.alert("文件 ["+file.name+"] 大小超出系统限制的"+$('#fileData').uploadify('settings','fileSizeLimit')+"大小！");
		                    break;
		                case -120:
		                    $.alert("文件 ["+file.name+"] 大小异常！");
		                    break;
		                case -130:
		                    $.alert("文件 ["+file.name+"] 类型不正确,系统只允许jpg和png格式的文件！");
		                    break;
		            }
		        },
		      	//当文件上传成功时触发
		        'onUploadSuccess': function(file, data, response) {  
		        	//$.alert(data);
		        	//重新刷新页面
		        	$grid.query();
            	}
		    });
		});
		
		
 		//启用Logo图片
		function detail(id){
			$.ajax({
				url : "${path}/logo/qiyong.action?id="+id,
				type: 'post',
				success:function(data){
					 var data = JSON.parse(data);
	            	 var name;
	            	 for(var i=0;i<data.length;i++){
	 					var listSon = data[i];
	 					name = listSon.success;
	 				}
					if(name == ''){
						//刷新整个父页面
						parent.location = "${path}/index.jsp"
					}else{
						alert("启用失败");
					}
				}
			});
		}
		
 		//删除已上传的logo
 		function del(id){
 			var ss = id;
 			$.confirm('确定要删除该Logo吗？', function(){
	 			$.ajax({
	 				url : "${path}/logo/del.action?id="+id,
					type: 'post',
					dataType: "text",
					success:function(data){
						if(data == "success"){
							$grid.query();
						}else{
							$.alert('操作失败');
						}
					}
	 			});
 			});
 		}
 		
 		function disabled(id){
 			$.ajax({
				url : "${path}/logo/doDisabled.action?id="+id,
				type: 'post',
				dataType: "text",
				success:function(data){
					if(data == "success"){
						//刷新整个父页面
						parent.location = "${path}/index.jsp"
					}else{
						$.alert("禁用失败");
					}
				}
			});
 		}
		/***操作列***/
 		$operate = function(value,rec){
			var status = rec.status;
			var opt1 = "";
			var enabled= "<a class=\"btn hover-red\" href=\"javascript:detail("+rec.id+")\" ><i class=\"fa fa-edit\"></i> 启用</a>";
			var disabled = "<a class=\"btn hover-red\" href=\"javascript:disabled("+rec.id+")\" ><i class=\"fa fa-edit\"></i>禁用</a>";
			var del = "<a class=\"btn hover-red\" href=\"javascript:del("+rec.id+")\" ><i class=\"fa fa-edit\"></i>删除</a>";
			if(status == 1){
				if(rec.logoName== 'pubLogo3.png'){
					opt1 = "";
				}else{
					opt1 = disabled;
				}
			}else{
				if(rec.logoName== 'pubLogo3.png'){
					opt1 = enabled;
				}else{
					 opt1 = enabled+del;
				}
			}
			return opt1;
 		}; 
 		
 		$logo = function(value,rec){
 			var opt = "";
 			var url = "${path}/fileDir/sysUpLoadFile/logo/" + value;
 		    var img = new Image();    
 			img.src = url;  
 			var width = 200;
 			var height = 40;
 			/*
 			 try{
 				var iwidth = img.width;
 	 			if(iwidth <= 200){
 	 				width = iwidth;
 	 			}
 	 			
 	 			var iheight = img.height;
 				if(iheight <= 40){
 					height  = iheight;
 	 			}
 				//alert(width + "-----" + height + "----" + url);
 		    }catch(e){
 		    	
 		    }finally{
 		    	
 		    } */
 			opt += "<img alt=\"logo\" width=\"" + width +"\" height=\"" + height +"\"  id=\"logo\" src=\"" + url + "\"/>";
 			return opt;
 		};
	</script>
	
</head>


<body>
	<div id="fakeFrame" class="container-fluid by-frame-wrap">
		<div class="panel panel-default">
			<div class="panel-heading" id="div_head_t">
				<ul class="breadcrumb">
			        <li>
			            	系统管理
			        </li>
			        <li>
			            	Logo管理
			        </li>
				</ul>
			</div>
			 <div class="portlet portlet-border">
		        <div class="portlet-title">
		            <div class="caption">Logo上传</div>
		        </div>
		        <div class="form-wrap">
		        <div class="row form-wrap">
					<label class="col-sm-4 control-label text-right">选择Logo图片：</label>
					<div class="col-sm-8">
						<input type="hidden" id="format" name="commonMetaDatas['format']" />
							<div style="white-space:nowrap;">
								<input type="file" name="fileData" id="fileData" class="validate[required] input"/> 
							</div>
							<div>
								<input type="submit" class="btn blue" value="上传" onclick="uploadFile();"/>&nbsp;&nbsp;(建议：图片宽高为 320*40像素，且背景透明)
							</div>
						</div>
					</div>
				</div>	
	         </div> 
	         <div class="portlet portlet-border">
		        <div class="portlet-title">
		            <div class="caption">Logo列表（注：系统会把大图缩小显示）</div>
		        </div>
		        <div class="form-wrap">
		        	<div id="data_div" class="data_div height_remain" style="height:100%;width:98%;" align="center"></div>
		        </div>
			 </div>
		</div>
	</div>
</body>
</html>