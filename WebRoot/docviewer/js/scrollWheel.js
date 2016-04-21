/*注册事件*/
var scrollFunc=function(e){ 
    e=e || window.event; 
    var value = "";
//     var t1=document.getElementById("wheelDelta"); 
//     var t2=document.getElementById("detail"); 
    if(e.currentTarget==window){
    	return;
    }
    if(e.wheelDelta){//IE/Opera/Chrome 
//         t1.value=e.wheelDelta; 
        value=e.wheelDelta; 
    }else if(e.detail){//Firefox 
//         t2.value=e.detail; 
        value=e.detail;
    } 
    var zTree = $.fn.zTree.getZTreeObj("tree");
    var nextNode = "";
    if(value=="-120" || value=="3"){
		 var nodes = zTree.getSelectedNodes();
			var treeNode = nodes[0];
// 			var getParentNode = nodes[0].getParentNode();
			//是节点取下一个循环，不是节点则取下一个节点
			if(treeNode.isParent == false){
				nextNode = nodes[0].getNextNode();
			}else{
				nextNode = treeNode;
			}
			if(nextNode!=null && nextNode.isParent == true && nextNode.children!=null && nextNode.children!=undefined){
				for(var y = 0;y<nextNode.children.length;y++){
					var fileUrl = nextNode.children[y].fileUrl;
			 		if(fileUrl != null && fileUrl !=""){
					zTree.selectNode(nextNode.children[y]);
					beforeClickZtree("", nextNode.children[y]);
//		 			$('#byPageContent').attr("src","${basePath}" + url);
					}
					break;
				}
				//第二节点
			}else if(nextNode==null && treeNode.isParent ==true && treeNode.children!=undefined){
				for(var y = 0;y<treeNode.children.length;y++){
					var fileUrl = treeNode.children[y].fileUrl;
			 		if(fileUrl != null && fileUrl !=""){
					zTree.selectNode(treeNode.children[y]);
					beforeClickZtree("", treeNode.children[y]);
//		 			$('#byPageContent').attr("src","${basePath}" + url);
					}
					break;
				}
				//循环所有节点的子节点，
			}else if(nextNode==null && treeNode.isParent ==false ){
				//上级节点
				nextNode = treeNode.getParentNode();
				nextNode = nextNode.getNextNode();
				//如果二节点上级节点为空
				if(nextNode==null){
					nextNode = treeNode.getParentNode();
					nextNode = nextNode.getParentNode();
					nextNode = nextNode.getNextNode();
				}
				//循环一直找到上级节点的下一节点不为空
				if(nextNode==null){
					nextNode = treeNode.getParentNode();
					for(var i=0;i<16;i++){
						nextNode = nextNode.getParentNode();
						if(nextNode==null){
							continue;
						}
						if(nextNode.getNextNode()==null){
							continue;
						}else{
							nextNode = nextNode.getNextNode();
							break;
						}
					}
				}
				if(nextNode!=null && nextNode.isParent == true && nextNode.children!=null && nextNode.children!=undefined){
					for(var y = 0;y<nextNode.children.length;y++){
						var fileUrl = nextNode.children[y].fileUrl;
				 		if(fileUrl != null && fileUrl !=""){
						zTree.selectNode(nextNode.children[y]);
						beforeClickZtree("", nextNode.children[y]);
//			 			$('#byPageContent').attr("src","${basePath}" + url);
						}
						break;
					}
					
				}else{
					zTree.selectNode(nextNode);
					beforeClickZtree("", nextNode);
				}
				//循环第二节点都不为空
			}else if(nextNode!=null && treeNode.isParent ==true ){
				for(var y = 0;y<nextNode.children.length;y++){
					var fileUrl = nextNode.children[y].fileUrl;
			 		if(fileUrl != null && fileUrl !=""){
					zTree.selectNode(nextNode.children[y]);
					beforeClickZtree("", nextNode.children[y]);
//		 			$('#byPageContent').attr("src","${basePath}" + url);
					}
					break;
				}
				
			}else{
				zTree.selectNode(nextNode);
				beforeClickZtree("", nextNode);
			}
    	
    }else if(value=="+120" ||  value=="-3"){
		 var nodes = zTree.getSelectedNodes();
			var treeNode = nodes[0];
//			var getParentNode = nodes[0].getParentNode();
			//是节点取下一个循环，不是节点则取下一个节点
			if(treeNode.isParent == false){
				nextNode = nodes[0].getPreNode();
			}else{
				nextNode = treeNode;
			}
			if(nextNode!=null && nextNode.isParent == true && nextNode.children!=null && nextNode.children!=undefined){
				for(var y =nextNode.children.length-1 ;y>=0;y--){
					var fileUrl = nextNode.children[y].fileUrl;
			 		if(fileUrl != null && fileUrl !=""){
					zTree.selectNode(nextNode.children[y]);
					beforeClickZtree("", nextNode.children[y]);
//		 			$('#byPageContent').attr("src","${basePath}" + url);
					}
					break;
				}
				//第二节点
			}else if(nextNode==null && treeNode.isParent ==true && treeNode.children!=undefined){
				for(var y = 0;y<treeNode.children.length;y++){
					var fileUrl = treeNode.children[y].fileUrl;
			 		if(fileUrl != null && fileUrl !=""){
					zTree.selectNode(treeNode.children[y]);
					beforeClickZtree("", treeNode.children[y]);
//		 			$('#byPageContent').attr("src","${basePath}" + url);
					}
					break;
				}
				//循环所有节点的子节点，
			}else if(nextNode==null && treeNode.isParent ==false ){
				//循环一直找到最末节点不为空
				if(nextNode==null){
					nextNode = treeNode.getParentNode();
					for(var i=0;i<16;i++){
						nextNode = nextNode.getNextNode();
						if(nextNode==null){
							nextNode = treeNode.getNextNode();
							break;
						}
						if(nextNode.isLastNode==false){
							continue;
						}else{
							nextNode = nextNode.getPreNode();
							break;
						}
					}
				}
				
				//上级节点
				nextNode = treeNode.getParentNode();
				nextNode = nextNode.getPreNode();
				//如果二节点上级节点为空
				var parentNode = "";
				if(nextNode==null){
					nextNode = treeNode.getParentNode();
					nextNode = nextNode.getParentNode();
					//用于承接最后一次上级节点
					parentNode = nextNode;
					nextNode = nextNode.getPreNode();
				}
				//用于一直循环到有下一个节点为止
				if(nextNode==null){
					for(var i=0;i<16;i++){
						nextNode = parentNode.getParentNode();
						parentNode = nextNode;
						nextNode = nextNode.getPreNode();
						if(nextNode==null){
							continue;
						}else if(nextNode!=null){
							break;
						}
					}
				}
				if(nextNode!=null && nextNode.isParent == true && nextNode.children!=null && nextNode.children!=undefined){
					for(var y = nextNode.children.length-1;y>=0;y--){
						var fileUrl = nextNode.children[y].fileUrl;
				 		if(fileUrl != null && fileUrl !=""){
						zTree.selectNode(nextNode.children[y]);
						beforeClickZtree("", nextNode.children[y]);
//			 			$('#byPageContent').attr("src","${basePath}" + url);
						}
						break;
					}
					
				}else{
					zTree.selectNode(nextNode);
					beforeClickZtree("", nextNode);
				}
				//循环第二节点都不为空
			}else if(nextNode!=null && treeNode.isParent ==true ){
				for(var y = 0;y<nextNode.children.length;y++){
					var fileUrl = nextNode.children[y].fileUrl;
			 		if(fileUrl != null && fileUrl !=""){
					zTree.selectNode(nextNode.children[y]);
					beforeClickZtree("", nextNode.children[y]);
//		 			$('#byPageContent').attr("src","${basePath}" + url);
					}
					break;
				}
				
			}else{
				zTree.selectNode(nextNode);
				beforeClickZtree("", nextNode);
			}
    }
} 
/*注册事件*/ 
if(document.addEventListener){ 
    document.addEventListener('DOMMouseScroll',scrollFunc,false); 
}//W3C 
window.onmousewheel=document.onmousewheel=scrollFunc;//IE/Opera/Chrome 