<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<script type="text/javascript">
	function getExtentMeta(){
		//获取扩展元数据
		$.ajax({
			type:"post",
			async:false,
			url:"${path}/getExtendMetaHtml.action?objectId=${objectId}&type=T06&ext="+cFileType,
			success:function(data){
				data = eval('('+data+')');
				$('#tab_1_1_3').html(data.outputResult);
			}
		});
	}
	
	function setCity(){
		var provinceCode = $('#provinces').val();
		//获取市列表
		$.ajax({
			type:"post",
			async:false,
			url:"${path}/getPlaceList.action?type=2&code="+provinceCode,
			success:function(data){
				$("#city").empty();
				$("#city").append(createOption(data));
			}
		});
		setArea();
	}
	function setArea(){
		var cityCode = $('#city').val();
		//获取市列表
		$.ajax({
			type:"post",
			async:false,
			url:"${path}/getPlaceList.action?type=3&code="+cityCode,
			success:function(data){
				$("#area").empty();
				$("#area").append(createOption(data));
			}
		});
	}
	function createOption(data){
		var options = '';
		data = eval('('+data+')');
		var lt = data.length;
		for(var i = 0;i <lt;i ++){
			options += '<option value="'+data[i].code+'">'+data[i].name+'</option>';
		}
		return options;
	}
	var isCopyright = '${param.isCopyright}';
	$(function(){
		if(isCopyright == 1){
			$("#copyrightTab").addClass('active');
			$("#coreTab").removeClass('active');
			$("#tab_1_1_4").addClass('active');
			$("#tab_1_1_1").removeClass('active');
		}
		//纠正验证框架位置
		$('#meta_tab a').click(function (e) {
            e.preventDefault();
            setTimeout('$("#coreData").validationEngine("updatePromptsPosition");',50);
        });
		var objectId = $("#objectId").val();
		if(objectId!='' && objectId!='-1'){
			$('#source').attr("readonly","readonly").addClass('no_border');
			$('#creator').attr("readonly","readonly").addClass('no_border');
			$('#title').attr("readonly","readonly").addClass('no_border');
			$('#ISBN').attr("readonly","readonly").addClass('no_border');
		}
		
	});
</script>
<ul class="nav nav-tabs nav-justified" id="meta_tab">
    <li class="active" id ="coreTab"><a href="#tab_1_1_1" data-toggle="tab">核心元数据</a></li>
<!--     <li><a href="#tab_1_1_2" data-toggle="tab">通用元数据</a></li> -->
    <li><a href="#tab_1_1_3" data-toggle="tab">扩展元数据</a></li>
    <li id="copyrightTab"><a href="#tab_1_1_4" data-toggle="tab">版权元数据</a></li>
</ul>
<div class="tab-content">
    <div class="tab-pane active" id="tab_1_1_1">
            <div class="portlet">
                <div class="portlet-title">
                    <div class="caption">资源信息 <a href="javascript:;" onclick="togglePortlet(this)"><i
                            class="fa fa-angle-up"></i></a></div>
                </div>
                <div class="portlet-body">
                    <div class="container-fluid">
                        <div class="row">
                        	<%--以下内容不需要动态判断显示逻辑 --%>
                        	<div class="col-md-6">
                                <div class="form-group">
                                    <label class="control-label col-md-4"><i class="must">*</i>书名：</label>
                                    <div class="col-md-8">
                                        <input type="text" name="commonMetaData.commonMetaDatas['title']" id="title" class="form-control validate[required]"/>
                                    </div>
                                </div>
                            </div>
                        	<div class="col-md-6">
                                <div class="form-group">
                                    <label class="control-label col-md-4"><i class="must">*</i>ISBN：</label>

                                    <div class="col-md-8">
<!--                                     validate[required,custom[checkISBN] -->
                                    	<input type="text" name="extendMetaData.extendMetaDatas['ISBN']" id="ISBN" class="form-control ]"/>
                                    </div>
                                </div>
                            </div>
                             <div class="col-md-6">
                                <div class="form-group">
                                    <label class="control-label col-md-4"><i class="must">*</i>作者：</label>

                                    <div class="col-md-8">
                                    	<input type="text" name="commonMetaData.commonMetaDatas['creator']" id="creator" class="form-control validate[required]" onblur="topCheckRepeat();"/>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label class="control-label col-md-4"><i class="must">*</i>来源：</label>

                                    <div class="col-md-8">
                                    	<input type="text" name="commonMetaData.commonMetaDatas['source']" id="source" class="form-control validate[required]" onblur="topCheckRepeat();"/>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label class="control-label col-md-4"><i class="must">*</i>关键字：</label>

                                    <div class="col-md-8">
                                    	<input type="text" name="commonMetaData.commonMetaDatas['keywords']" id="keywords" class="form-control validate[required]"/>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label class="control-label col-md-4"><i class="must">*</i>适用对象：</label>

                                    <div class="col-md-8">
                                    	<app:constants id="audience" name="commonMetaData.commonMetaDatas['audience']" 
                                        	repository="com.brainsoon.system.support.SystemConstants" 
                                        	className="Audience" 
                                        	inputType="select" cssType="form-control validate[required]" headerValue="请选择"></app:constants>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-12">
                                <div class="form-group">
                                    <label class="control-label col-md-2"><i class="must">*</i>摘要：</label>

                                    <div class="col-md-10">
                                        <textarea name="commonMetaData.commonMetaDatas['description']" id="description" class="form-control validate[required]"></textarea>
                                    </div>
                                </div>
                            </div>
                             <div class="col-md-6">
                                <div class="form-group">
                                    <label class="control-label col-md-4"><i class="must">*</i>语种：</label>

                                    <div class="col-md-8">
                                    	<app:constants id="language" name="commonMetaData.commonMetaDatas['language']" 
                                        	repository="com.brainsoon.system.support.SystemConstants" 
                                        	className="Language" 
                                        	inputType="select" cssType="form-control validate[required]" headerValue="请选择"></app:constants>
                                    </div>
                                </div>
                            </div>
                             <div class="col-md-6">
                                <div class="form-group">
                                    <label class="control-label col-md-4">批次号：</label>

                                    <div class="col-md-8">
                                    	<input type="text" name="batchNum" id="batchNum" class="form-control"/>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-12">
                                <div class="form-group">
                                    <label class="control-label col-md-2"><i class="must">*</i>原作者是否公开：</label>

                                    <div class="col-md-10">
			    <app:constants id="public_or_not" name="commonMetaData.commonMetaDatas['public_or_not']" 
                                        	repository="com.brainsoon.system.support.SystemConstants" 
                                        	className="OpeningRate" 
                                        	inputType="radio"  selected="QJ"></app:constants>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <div class="col-md-12">
                                        <fieldset>
                                            <legend>阅读消费方式设置</legend>
                                            <div class="col-md-7">
                                                <label class="control-label col-md-6">消费类型：</label>

                                                <div class="col-md-6">
                                                    <app:constants id="view_type" name="extendMetaData.extendMetaDatas['view_type']" 
                                        				repository="com.brainsoon.system.support.SystemConstants" 
                                        				className="ConsumeType" 
                                        				inputType="select" cssType="form-control" headerValue="请选择"></app:constants>
                                                </div>
                                            </div>
                                            <div class="col-md-5">
                                                <label class="control-label col-md-6">消费值：</label>

                                                <div class="col-md-6">
                                                    <input type="text" id="view_value" name="extendMetaData.extendMetaDatas['view_value']" class="form-control" placeholder="0"/>
                                                </div>
                                            </div>
                                        </fieldset>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group">
                                    <div class="col-md-12">
                                        <fieldset>
                                            <legend>下载消费方式设置</legend>
                                            <div class="col-md-7">
                                                <label class="control-label col-md-6">消费类型：</label>

                                                <div class="col-md-6">
                                                    <app:constants id="down_type" name="extendMetaData.extendMetaDatas['down_type']" 
                                        				repository="com.brainsoon.system.support.SystemConstants" 
                                        				className="ConsumeType" 
                                        				inputType="select" cssType="form-control" headerValue="请选择"></app:constants>
                                                </div>
                                            </div>
                                            <div class="col-md-5">
                                                <label class="control-label col-md-6">消费值：</label>

                                                <div class="col-md-6">
                                                    <input type="text" id="down_value" name="extendMetaData.extendMetaDatas['down_value']" class="form-control" placeholder="0"/>
                                                </div>
                                            </div>
                                        </fieldset>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="portlet">
                <div class="portlet-title">
                    <div class="caption">制作者信息 <a href="javascript:;" onclick="togglePortlet(this)"><i
                            class="fa fa-angle-up"></i></a></div>
                </div>
                <div class="portlet-body">
                    <div class="container-fluid">
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label class="control-label col-md-4">所在地区：</label>

                                    <div class="col-md-8">
				<select id="provinces" class="form-control" onchange="setCity()" style="width: 33%;display:inline;">
					<option value="">请选择</option>
					<c:forEach items="${provinces}" var="item">
						<option value="${item.code }">${item.name }</option>
					</c:forEach>
				</select>
				<select class="form-control" id="city" name="city" onchange="setArea()" style="min-width: 80px;width: 32%;display:inline;">
					<option value="">请选择</option>
				</select>
				<input type="hidden" id="region" name="commonMetaData.commonMetaDatas['region']" /><%--存储省市县代码 --%>
				<select class="form-control" id="area" name="area" style="min-width: 80px;width: 33%;display:inline;"><option value="">请选择</option></select>
			</div>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label class="control-label col-md-4">性别：</label>

                                    <div class="col-md-8">
                                    	<app:constants name="commonMetaData.commonMetaDatas['sex']" 
                                        	repository="com.brainsoon.system.support.SystemConstants" 
                                        	className="Gender" 
                                        	inputType="radio"  selected="M"></app:constants>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label class="control-label col-md-4">所在学校：</label>

                                    <div class="col-md-8">
                                    	<input type="text" name="commonMetaData.commonMetaDatas['school']" id="school" class="form-control"/>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label class="control-label col-md-4">学校地址：</label>

                                    <div class="col-md-8">
                                        <input type="text" name="commonMetaData.commonMetaDatas['address']" id="address" class="form-control"/>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="row">
<!--                             <div class="col-md-6"> -->
<!--                                 <div class="form-group"> -->
<!--                                     <label class="control-label col-md-4"><i class="must">*</i>作者：</label> -->

<!--                                     <div class="col-md-8"> -->
<!--                                     	<input type="text" name="commonMetaData.commonMetaDatas['creator']" id="creator" class="form-control validate[required]"/> -->
<!--                                     </div> -->
<!--                                 </div> -->
<!--                             </div> -->
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label class="control-label col-md-4">其他作者姓名：</label>

                                    <div class="col-md-8">
                                        <input type="text" name="commonMetaData.commonMetaDatas['contributor']" id="contributor" class="form-control"/>
                                    </div>
                                </div>
                            </div>
                             <div class="col-md-6">
                                <div class="form-group">
                                    <label class="control-label col-md-4">出生日期：</label>

                                    <div class="col-md-8">
                                    	<input type="text" name="commonMetaData.commonMetaDatas['birthdate']" id="birthdate" class="form-control Wdate" onClick="WdatePicker({readOnly:true})"/>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                             <div class="col-md-6">
                                <div class="form-group">
                                    <label class="control-label col-md-4">Email：</label>

                                    <div class="col-md-8">
                                        <input type="text" name="commonMetaData.commonMetaDatas['email']" id="email" class="form-control validate[custom[email]]"/>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label class="control-label col-md-4">手机：</label>

                                    <div class="col-md-8">
                                    	<input type="text" name="commonMetaData.commonMetaDatas['cellphone_number']" id="cellphone_number" class="form-control validate[custom[mobile]]"/>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label class="control-label col-md-4">职称：</label>

                                    <div class="col-md-8">
                                        <input type="text" name="commonMetaData.commonMetaDatas['professional_title']" id="professional_title" class="form-control"/>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="portlet">
                <div class="portlet-title">
                    <div class="caption">修改信息 <a href="javascript:;" onclick="togglePortlet(this)"><i
                            class="fa fa-angle-up"></i></a></div>
                </div>
                <div class="portlet-body">
                	<div class="container-fluid">
                        <div class="row">
			                <div class="col-md-12">
			                    <div class="form-group">
			                        <label class="control-label col-md-2">修改内容：</label>
			
			                        <div class="col-md-10">
			                            <textarea  readonly="readonly" id="modifyContent" class="form-control" rows="8"></textarea>
			                        </div>
			                    </div>
			                </div>
			             </div>
			          </div>      
	                ，     </div>
            </div>
    </div>
    <%-- 选项卡2 --%>
    <div class="tab-pane" id="tab_1_1_2">
    	<div class="portlet">
            <div class="portlet-title">
                <div class="caption">资源信息 <a href="javascript:;" onclick="togglePortlet(this)"><i
                        class="fa fa-angle-up"></i></a></div>
            </div>
            <div class="portlet-body">
                <div class="container-fluid">
                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="control-label col-md-4">难易程度：</label>

                                <div class="col-md-8">
                                    <input type="text" id="difficulty_level" name="commonMetaData.commonMetaDatas['difficulty_level']" class="form-control validate[custom[onlyNumberOne]]" style="width: 70px;"/>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="control-label col-md-4"><i class="must">*</i>版权所属：</label>

                                <div class="col-md-8">
                                    <input type="text" id="copyright" name="commonMetaData.commonMetaDatas['copyright']" class="form-control"/>
                                </div>
                            </div>
                        </div>
<!--                         <div class="col-md-6" id="knowledge_pointDiv"> -->
<!--                             <div class="form-group"> -->
<!--                                 <label class="control-label col-md-4">知识点：</label> -->

<!--                                 <div class="col-md-8"> -->
<!--                                    	<div class="input-group"> -->
<!--                                    		<input type="text" class="form-control" name="commonMetaData.commonMetaDatas['knowledge_point_name']" id="knowledge_point_name" readonly="readonly"/> -->
<!--                                    		<input type="hidden" name="commonMetaData.commonMetaDatas['knowledge_point']" id="knowledge_point"/> -->
<!-- 										<span class="input-group-btn"> -->
<!-- 											<a id="menuBtn2" onclick="showKnowledge(); return false;" href="###" class="btn btn-primary" role="button">选择</a> -->
<!-- 											<a onclick="clearKnowledge();return false;" href="###" class="btn btn-primary" role="button" style="margin-left: 3px;">清除</a> -->
<!-- 										</span> -->
<!--                                    	</div> -->
<!-- 								</div> -->
<!--                             </div> -->
<!--                         </div> -->
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="control-label col-md-4">购买价格：</label>

                                <div class="col-md-8">
                                    <input type="text" id="purchase_price" name="extendMetaData.extendMetaDatas['purchase_price']" class="form-control"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="portlet">
            <div class="portlet-title">
                <div class="caption">评价与反馈 <a href="javascript:;" onclick="togglePortlet(this)"><i
                        class="fa fa-angle-up"></i></a></div>
            </div>
            <div class="portlet-body">
                <div class="container-fluid">
                    <div class="row">
                        <div class="col-md-12">
                            <div class="form-group">
                                <label class="control-label col-md-2">评价等级：</label>
                                <div class="col-md-10">
                                    <input type="number" name="commonMetaData.commonMetaDatas['rating']" id="rating" class="rating"/>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-12">
                            <div class="form-group">
                                <label class="control-label col-md-2">评价者：</label>

                                <div class="col-md-10">
                                    <input type="text" name="commonMetaData.commonMetaDatas['reviewer']" id="reviewer" class="form-control" style="width: 40%;"/>
                                     </div>
                                 </div>
                             </div>
                         </div>
                         <div class="row">
                             <div class="col-md-12">
                                 <div class="form-group">
                                     <label class="control-label col-md-2">评价描述：</label>

                                     <div class="col-md-10">
                                     	<textarea name="commonMetaData.commonMetaDatas['review']" class="form-control" rows="3"></textarea>
                                     </div>
                                 </div>
                             </div>
                         </div>
                     </div>
                 </div>
             </div>
         </div>
         <div class="tab-pane" id="tab_1_1_3">
         	
	</div>
		<div class="tab-pane" id="tab_1_1_4">
			<%@ include file="/bres/copyright.jsp" %>
		</div>
</div>