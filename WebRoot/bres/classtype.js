var domainType = -1; // 0 版权树 1分类树 选择学段后值改变
var type = '';
var module = '';
//专题资源，拓展资源，教师专业 不需要学段
function hideEducational(){
	if(module == 'ZT'|| module == 'JS' ||  module == 'TZ'){
		$('#educational_phase1').hide();
	}
}

//改变学段
function changeEducational(){
	module = $('#module').val();
	type = $('#type').val();
	if(module == 'ST'||module == 'SJ'){
		module = 'TB';
	}
	changeDomainType();
	//获取下一级
	var nextNodes = getDomainNode(getCode(1));
	var opts = createDomainOptions(nextNodes);
	if(type == 'T06'){
		domainType = 1;
	}
	if(domainType == 0){
		//更新教材版本
		$('#version').empty();
		$('#version').append('<option value="">请选择</option>');
		$('#version').append(opts);
		emptySelect('subject');
	}else{
		//更新学科
		$('#subject').empty();
		$('#subject').append('<option value="">请选择</option>');
		$('#subject').append(opts);
	}
	//下级全部重置
	emptySelect('grade');
	emptySelect('fascicule');
	clearUnit();
}

//改变版本
function changeVersion(){
	//更新学科
	updateOpt(2,'subject');
	emptySelect('grade');
	emptySelect('fascicule');
	clearUnit();
}
//改变学科
function changeSubject(){
	//年级
	updateOpt(3,'grade');
	emptySelect('fascicule');
	clearUnit();
}
//改变年级
function changeGrade(){
	//分册
	updateOpt(4,'fascicule');
	clearUnit();
}

//清空select
function emptySelect(id){
	var $obj = $('#'+id);
	$obj.empty();
	$obj.append('<option value="">请选择</option>');
}
function updateOpt(dj,id){
	var nextNodes = getDomainNode(getCode(dj));
	var opts = createDomainOptions(nextNodes);
	var $obj = $('#'+id);
	$obj.empty();
	$obj.append('<option value="">请选择</option>');
	$obj.append(opts);
}
//动态显示隐藏表单，显示规则参考资源分类体系
function showOrHideDiv(){
	module = $('#module').val();
	type = $('#type').val();
	if(module == 'ST'||module == 'SJ'){
		module = 'TB';
	}
	var edu = getEducational();
	//控制除学段外的其他动态显示字段，先隐藏所有(除T12外，其他都有单元)，然后选择性显示
	$('#versionDiv').hide();
	$('#subjectDiv').hide();
	$('#gradeDiv').hide();
	$('#fasciculeDiv').hide();
	if(type == 'T12' || type == 'T06'){
		$('#unitDiv').hide();
		$('#unitDivContent').hide();
	}else{
		$('#unitDiv').show();
		$('#unitDivContent').show();
	}
	
	if(module == 'TB' && (type != 'T06' &&  type != 'T12' )){
		$('#subjectDiv').show();
		$('#gradeDiv').show();
		if(edu != '' && edu != 'K'){   //学前
			$('#versionDiv').show();
			$('#fasciculeDiv').show();
		}
	}
	if(module == 'ZS'){
		$('#subjectDiv').show();
		$('#knowledge_pointDiv').hide();
	}else{
		$('#knowledge_pointDiv').show();
	}
	if(module == 'TB' && type == 'T06'){
		$('#subjectDiv').show();
		$('#gradeDiv').show();
		$('#knowledge_pointDiv').hide();
	}else if((module == 'ZT' || module == 'ZS'|| module == 'JS' ||  module == 'TZ') && type == 'T06'){
		$('#subjectDiv').hide();
		$('#knowledge_pointDiv').hide();
	}
	var type = $('#type').val();
}
//控制学段显示
function educationalPhaseShow(){
	var type = $('#type').val();
	var module = $('#module').val();
	if(module == 'ST'||module == 'SJ'){
		module = 'TB';
	}
	if((module == 'TB' || module == 'ZS') && type != 'T12'){
		//获取学段
		$.ajax({
			type:'post',
			async:false,
			url:_appPath+'/bres/getEducationalPhaseOptions.action?module='+module,
			success:function(data){
				$('#educational_phase').empty();
				$('#educational_phase').append(data);
			}
		});
		if(module == 'ZS' && type == 'T06'){
			$('#educational_phaseDiv').hide();
		}else{
			$('#educational_phaseDiv').show();
		}
		
	}else{
		$('#educational_phase').empty();
		$('#educational_phaseDiv').hide();
	}
}
//改变要读取树的类型
function changeDomainType(){
	module = $('#module').val();
	var type = $('#type').val();
	if(module == 'ST'||module == 'SJ'){
		module = 'TB';
	}
	var educational_phase = getEducational();
	$.ajax({
		type:"post",
		async:false,
		url:_appPath+"/getDomainType.action?module="+module+'&educational_phase='+educational_phase,
		success:function(data){
			domainType = data;
		}
	});
	showOrHideDiv();//类型改变后，动态显示隐藏表单
}
//获取上级编码 1，学段之前 2，版本 3，学科 4，适用年级 5，分册
function getCode(dj){
	var codes = '';
	if(dj == 1){
		var module = $('#module').val();
		var type = $('#type').val();
		codes = module + ',' + type;
		codes += ','+ getEducational();
		return codes;
	} 
	if(domainType == 0 && dj == 2){
		codes = getCode(1) + ',' + $('#version').val();
		return codes;
	}else if(domainType == 1 && dj == 2){
		return getCode(1);
	}
	if(dj == 3){
		codes = getCode(2) + ',' + $('#subject').val();
		return codes;
	}
	if(dj == 4){
		codes = getCode(3) + ',' + $('#grade').val();
		return codes;
	}
	if(domainType == 0 && dj == 5){
		codes = getCode(4) + ',' + $('#fascicule').val();
		return codes;
	}else if(domainType == 1 && dj == 5){
		//分册没有
		return getCode(4);
	}
	return '';
}

//遍历节点
function createDomainOptions(nextNodes){
	nextNodes = eval('('+nextNodes+')');
	var domains = eval(nextNodes.domains);
	var opts = '';
	for(var i = 0;i < domains.length;i ++){
		opts += '<option value="'+domains[i].code+'" xpath="'+domains[i].xpath+'" >'+domains[i].label+'</option>';
	}
	return opts;
}
//获得当前的学段
function getEducational(){
	//判断 educational_phaseDiv 是否隐藏，要区别于不可见，不可见元素的display不一定为none
	var dis = $('#educational_phaseDiv').css("display");
	if(dis == 'none'){
		return "";
	}else{
		return $('#educational_phase').val();
	}
}

//获取下一级节点
function getDomainNode(codes){
	var nodeData = "";
	$.ajax({
		type:"post",
		async:false,
		url:_appPath+"/getDomainNode.action?domainType="+domainType+"&codes="+codes,
		success:function(data){
			nodeData = data;
		}
	});
	return nodeData;
}