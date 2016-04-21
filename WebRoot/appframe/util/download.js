function down4Encrypt(url){
	var msg =	'	<form action="" id="downFilesCommon" class="form-horizontal" method="post">'
				+'	<div class="form-group">'
				+'		<label class="col-sm-4 control-label text-right">加密密钥：</label>'
				+'		<div class="col-sm-8">'
				+'	     <input type="password" name="encryptPwd" id="encryptPwd" class="validate[required]" />'
				+'		</div>'
				+'	</div>'
				+'	</form>';
	$.simpleDialog({
		title:'下载', 
		content:msg, 
		button:[{name:'加密',callback:function(){
					var encryptPwd = top.$('#encryptPwd').val();
					if(typeof(encryptPwd) != 'undefined' && encryptPwd != ''){
						top.$('#downFilesCommon').attr('action',url);
						top.$('#downFilesCommon').submit();
					}else{
						$.alert('请输入加密密钥');
						return false;
					}
				}},
		        {name:'不加密',callback:function(){
		        	top.$('#downFilesCommon').attr('action',url);
		        	top.$('#downFilesCommon').submit();
		        }},
		        {name:'关闭',callback:function(){}}
		]
	});
}