	/**
	 * 获取改版教材Code
	 */
	function getVersionCode(code, nodeId) {
		if (code.charAt(0) == "V") {
			var versionNum = parseInt($("#lastVersionNum", parent.document).val());
			if (versionNum < 8) {
				code = "V0" + (versionNum + 1);
			} else {
				code = "V" + (versionNum + 1);
			}
		}
		$("#lastVersionNum", parent.document).val(code);
		return code;
	}
	
	//教材改版保留节点
	function getChangeVersionNodes(nodes){
		var versions = [];
		for(var i=0; i<nodes.length; i++){
			var version = {};
			version.nodeId = nodes[i].nodeId;
			version.pid = nodes[i].pid;
			version.nodeType = nodes[i].nodeType;
			version.label = nodes[i].label;
			version.code = getVersionCode(nodes[i].code,nodes[i].nodeId);
			version.level = nodes[i].level + 2;
			versions.push(version);
		}
		versions[0].label = $("#newVersionName").val();
		return versions;
	}
	
	//获取BroaderNames
	function getBroaderNames(parentNode,name){
		var names = [];
		names.push(name);
		while(parentNode){
			if(parentNode.nodeType==0){
				names.push(parentNode.label);
				break;
			}else{
				names.push(parentNode.label);
			}
			parentNode = parentNode.getParentNode();
		}
		return names.reverse().join(",");
	}
	
	//获取NodeType
	function getNodeType(treeNode){
		var nodeType = 0;
		if(treeNode.nodeType==8&&treeNode.code=="T00"){
			nodeType = 0;
		}else if(treeNode.nodeType==8&&treeNode.code=="T06"){
			nodeType = 1;
		}else if(treeNode.nodeType==8&&treeNode.code=="T12"){
			nodeType = 6;
		}else if(treeNode.nodeType==0){
			nodeType = 1;
		}else if(treeNode.nodeType==1){
			nodeType = 3;
		}else if(treeNode.nodeType==3){
			nodeType = 2;
		}else if(treeNode.nodeType==2&&treeNode.code.length==3){
			nodeType = 4;
		}else if(treeNode.nodeType==2&&treeNode.code.length==2){
			nodeType = 6;
		}
		return nodeType;
	}
	
	//节点是否可编辑名称
	function isText(node){
		if(node.code=="T12"||node.nodeType=="6"||node.code=="XB"
				||node.code=="ZB"||node.code=="DB")
			return true;
		else
			return false;
	}
	
	//是否属于教材版本节点
	function isBookVersion(node){
		var result = false;
		if(node.label=="教育资源")
			return true;
		while(node&&node.level>=0){
			if(node.nodeType!=0)
				node = node.getParentNode();
			else{
				result = true;
				break;				
			}
		}
		return result;
	}
	
	//生成单个节点json数据
	function getNodeJson(node,domainType) {
		var books = {
			"@type" : "domainList",
			"domainType" : 0
		};
		var domains = [];
		var book = {
			"label" : node.name,
			"nodeId" : node.nodeId,
			"nodeType" : node.nodeType,
			"pid" : node.pid,
			"code" : node.code,
			"level" : node.level,
			"domainType":domainType
		};
		if (node.objectId)		book["objectId"] = node.objectId;
		if (node.xpath)			book["xpath"] = node.xpath;
		if (node.pcode)			book["pcode"] = node.pcode;
		if (node.version)		book["version"] = node.version;	
		if (node.broaderNames)	book["broaderNames"] = node.broaderNames;
		if (node.broaders)		book["broaders"] = node.broaders;
		domains.push(book);
		var domainStr = JSON.stringify(domains);
		books["domains"] = domainStr;
		return books;
	}
	
	//获取推理状态
	function checkJobStatus(){
		var status = $.ajax({url:_appPath+"/system/book/checkJobStatus.action",async:false});
		return status.responseText;
	}