//tabs新建选项卡
function openTabs(resId,resType,title,url){
	var tabs = $("#center_area").tabs();
	var ool = false;
	var nodePath =  $("#center_area ul li a");
	//循环已经打开的选项卡，，判断是否存在
	$.each(nodePath,function(i,n) {
		if(n.attributes.href.nodeValue=='#tabs-'+resId){
			tabs.tabs("select",i);
			ool = true;
		}
	});
	if(ool==false){
		ajaxResText(resId,resType,title,url);
	}
}
//按键事件关闭当前窗口
function onkeyclosetabs(){
	document.onkeydown = function(e){
		var keyCode = e.keyCode || e.which || e.charCode;
	    var altKey = e.altKey;
		if(altKey && keyCode==81){
			chionkeyclosetabs();
		}
	}
}
function chionkeyclosetabs(){
	//循环已经打开的选项卡，，判断是否存在
	var selected = $("#center_area").tabs().tabs('option', 'selected');
	$("#center_area").tabs().tabs("remove",selected);
	setLiWidth();
	$("#center_area").tabs().tabs( "refresh" );
}

//选项卡关闭方法
function tabsclose(){
	var tabs=$("#center_area").tabs();
	tabs.delegate( "span.ui-icon-close", "click", function() {
	      var panelId = $( this ).closest( "li" ).attr( "aria-controls" );
	      var nodePath =  $("#center_area ul li a");
			//循环已经打开的选项卡，，判断是否存在
			$.each(nodePath,function(i,n) {
				if(n.attributes.href.nodeValue=="#"+panelId){
					tabs.tabs("remove",i);
					setLiWidth();
				}
			});
	      tabs.tabs( "refresh" );
	});
}
//tabs中li导航栏平均分配大小
function setLiWidth() {
	if($(".tab_wrap").find("ul.tab_list").children("li").length>4){
		$(".tab_wrap ul.tab_list li").css({
			"width": (100/($(".tab_wrap").find("ul.tab_list").children("li").length))-1 + "%",
			"display":"block"
		});
	}
	if(($(".tab_wrap ul.tab_list li")[0].offsetWidth-25)>11){
		$(".tab_wrap ul.tab_list li a").css({
			"width":  (($(".tab_wrap ul.tab_list li")[0].offsetWidth-25)/($(".tab_wrap ul.tab_list li")[0].offsetWidth)*100)+"%",
			"overflow":"hidden","text-overflow":"ellipsis","white-space":"nowrap",
			"display":"block"
		});
	}else{
		$(".tab_wrap ul.tab_list li a").css({
			//"width":  "0px","overflow":"hidden","text-overflow":"ellipsis","white-space":"nowrap"
			"display":"none"
		});
	}
}
/*
	异步加载数据
*/
function ajaxResText(resId,resType,title,url){
	var tabs = $("#center_area").tabs();
	var tabsul = $('#tabsul');
	var tabsdiv = $('#tabsdiv');
	var tabsTitle = "<li style='width:20%;'><a href=\"#tabs-"+resId+"\">"+title+"</a><span style='float:right;' class='ui-icon ui-icon-close' role='presentation'>Remove Tab</span></li>";
	var tabsText = "<div id=\"tabs-"+resId+"\" style='height:100%;'><iframe width='100%' frameborder='0' height='100%' src='tabsCenterDetail.jsp?resId="+resId+"&resType="+resType+"&url="+url+"'></iframe></div>";
	tabsul.append(tabsTitle);
	tabsdiv.append(tabsText);
	setLiWidth();
	tabs.tabs("refresh");
	var idx = $("#center_area a[href=\"#tabs-"+resId+"]").parent().index();
	tabs.tabs( "option", "active" ,idx);
}