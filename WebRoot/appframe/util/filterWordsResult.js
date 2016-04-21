/**
 * 敏感词过滤结果页面
 */

function filterWord(path,basePath,fileName){
//	,"epub"
	var array = ["pdf", "txt", "ncx", "doc", "docx", "xml"];
	var name = path.substring(path.lastIndexOf(".")+1,path.length);
	if($.inArray(name,array)==-1){
		$.alert("不支持" + name + "格式的文件过滤敏感词!\n" + "支持格式为pdf,txt,doc,docx,xml的文件。");
		return;
	}
	$("#levelList").remove();
	var checkboxDiv = $('<div class="modal fade" id="levelList" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true"></div>');
	checkboxDiv.appendTo($("body"));
	var checkboxDialog = $('<div class="modal-dialog modal-sm"></div>');
	checkboxDialog.appendTo(checkboxDiv);
	var checkboxContent = $('<div class="modal-content"></div>');
	checkboxContent.appendTo(checkboxDialog);
	var checkboxHeader = $('<div class="modal-header"><h4 class="modal-title" id="myModalLabel">选择过滤等级</h4></div>');
	checkboxHeader.appendTo(checkboxContent);
	var checkboxList = $('<div class="form-group"><label class="checkbox-inline col-xs-offset-1"><input type="checkbox" id="level1" value="1"> 高</label><label class="checkbox-inline"><input type="checkbox" id="level2" value="2"> 中</label><label class="checkbox-inline"><input type="checkbox" id="level3" value="3"> 低</label></div>');
	checkboxList.appendTo(checkboxContent);
	var button = $('<button class="btn btn-primary col-xs-offset-1">确定</button>');
	button.appendTo(checkboxList);
	$("#levelList").modal('show');
	button.click(function(){
		var level = "";
		if($("#level1").is(":checked")){
			level += "1,";
		}
		if($("#level2").is(":checked")){
			level += "2,";
		}
		if($("#level3").is(":checked")){
			level += "3";
		}
		if(level==""){
			$.alert("请选择级别");
			return;
		}
		$("#levelList").mask("过滤中...");
		$.get(basePath+"/DFA/testFile.action?path="+path+"&level="+level+"&fileName="+fileName,function(data){
			$("#levelList").modal('hide');
			$("#levelList").unmask();
			var result = JSON.parse(data);
			if("error" in result){
				$.alert(result["error"]);
				return;
			}
			var modalFade = $('<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true"></div>');
			modalFade.appendTo($("body"));
			var modalDialog = $('<div class="modal-dialog"></div>');
			modalDialog.appendTo(modalFade);
			var modalContent = $('<div class="modal-content"></div>');
			modalContent.appendTo(modalDialog);
			var modalHeader = $('<div id="modalHeader" class="modal-header"></div>');
			modalHeader.appendTo(modalContent);
			var button = $('<button type="button" id="flag" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>');
			button.appendTo(modalHeader);
			var header = $('<span class="modal-title" id="myModalLabelHeader"></span>');
			header.insertAfter(button);
			var modalBody = $('<div class="modal-body"></div>');
			modalBody.insertAfter(modalHeader);
			var ul = $("<ul id=\"wordResult\"></ul>");
			ul.appendTo(modalBody);
			$("#myModalLabelHeader").text(result.head);
			if(result["pdf"].length==0){
				$("#wordResult").empty();
				var nothing = $("<li></li>");
				nothing.text("该文件中不含有敏感词。");
				nothing.appendTo($("#wordResult"));
			}else{
				$("#wordResult").empty();
				for(var key in result["pdf"]){
					var li = $("<li></li>");
					li.text(result["pdf"][key]);
					li.appendTo($("#wordResult"));
				}
			}
			$('#myModal').modal('show');
		});
	});
	
}