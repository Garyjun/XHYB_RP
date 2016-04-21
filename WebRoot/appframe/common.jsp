<%@ page language="java" import="java.util.*"  pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/appframe-tags.tld" prefix="app" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<c:set var="path" value="<%=path %>" />
<c:set var="basePath" value="<%=basePath %>" />
<link rel="icon" href="${path}/appframe/main/images/favicon.ico"  type="image/x-icon" />
<link rel="shortcut icon" href="${path}/appframe/main/images/favicon.ico" type="image/x-icon" />
<link rel="bookmark" href="${path}/appframe/main/images/favicon.ico" type="image/x-icon" />
<!-- 框架必须 -->
<link rel="stylesheet" type="text/css" href="${path}/appframe/main/css/bootstrap.css"/>
<link rel="stylesheet" type="text/css" href="${path}/appframe/main/css/font-awesome.css"/>
<link rel="stylesheet" type="text/css" href="${path}/appframe/main/css/style-metronic.css"/>
<link rel="stylesheet" type="text/css" href="${path}/appframe/main/css/style.css"/>

<script type="text/javascript" src="${path}/appframe/main/js/libs/jquery.min.js"></script>
<!--[if lt IE 9]>
<script type="text/javascript" src="${path}/appframe/main/js/libs/html5shiv.min.js"></script>
<script type="text/javascript" src="${path}/appframe/main/js/libs/respond.min.js"></script>
<script type="text/javascript" src="${path}/appframe/main/js/placeholder.js"></script>
<![endif]-->

<script type="text/javascript" src="${path}/appframe/main/js/utils.js"></script>
<script type="text/javascript" src="${path}/appframe/main/js/global.js"></script>
<script type="text/javascript" src="${path}/appframe/main/js/libs/bootstrap.js"></script>
<script type="text/javascript" src="${path}/appframe/main/js/dialog.js"></script>
<script type="text/javascript" src="${path}/appframe/main/js/iframe.js"></script>

<script type="text/javascript">
	var _appPath = '${path}';
</script>

<script type="text/javascript" src="${path}/appframe/plugin/ueditor/ueditor.config.js"></script>
<script type="text/javascript" src="${path}/appframe/plugin/ueditor/ueditor.all.js"></script>
<script type="text/javascript" src="${path}/appframe/plugin/ueditor/third-party/codemirror/codemirror.js"></script>
<script type="text/javascript" src="${path}/appframe/plugin/ueditor/third-party/zeroclipboard/ZeroClipboard.js"></script>
<script type="text/javascript" charset="utf-8" src="${path}/appframe/plugin/ueditor/kityformula-plugin/addKityFormulaDialog.js"></script>
<script type="text/javascript" charset="utf-8" src="${path}/appframe/plugin/ueditor/kityformula-plugin/getKfContent.js"></script>
<script type="text/javascript" charset="utf-8" src="${path}/appframe/plugin/ueditor/kityformula-plugin/defaultFilterFix.js"></script>
<script type="text/javascript" charset="utf-8" src="${path}/appframe/plugin/ueditor/lang/zh-cn/zh-cn.js"></script>
<!-- 自动完成功能 -->
<script type="text/javascript" src="${path}/appframe/util/datagridUtil.js"></script>
<script type="text/javascript" src="${path}/appframe/util/appUtils.js"></script>
<script type="text/javascript" src="${path}/appframe/util/imageUtils.js"></script>
<script type="text/javascript" src="${path}/appframe/util/uuid.js"></script>
<script type="text/javascript" src="${path}/appframe/util/characterUtil.js"></script>
<script type="text/javascript" src="${path}/appframe/util/filterWordsResult.js"></script>

<script type="text/javascript" src="${path}/appframe/plugin/jquery/jquery.form.js"></script>
<script type="text/javascript" src="${path}/appframe/plugin/jquery/jquery.pulsate.js"></script>

<!-- 前端验证 -->
<link rel="stylesheet" type="text/css" href="${path}/appframe/plugin/jQueryValidationEngine/css/validationEngine.jquery.css"/>

<script type="text/javascript" src="${path}/appframe/plugin/jQueryValidationEngine/js/languages/jquery.validationEngine-zh_CN.js" charset="utf-8"></script>
<script type="text/javascript" src="${path}/appframe/plugin/jQueryValidationEngine/js/jquery.validationEngine.js" charset="utf-8"></script>


<!-- 表格组件 -->
<link rel="stylesheet" type="text/css" href="${path}/appframe/plugin/EasyUI/themes/default/easyui.css"/>
<link rel="stylesheet" type="text/css" href="${path}/appframe/plugin/EasyUI/themes/icon.css"/>
<script type="text/javascript" src="${path}/appframe/plugin/EasyUI/jquery.easyui.min.js"></script>
<script type="text/javascript" src="${path}/appframe/plugin/EasyUI/easyui-lang-zh_CN.js"></script>

<!-- 计算剩余高度 -->
<script type="text/javascript" src="${path}/appframe/util/accountHeight.js"></script>


<!-- 在线阅读JS start -->
<script type="text/javascript" src="${path}/docviewer/js/fileviewer.js"></script>
<script type="text/javascript" src="${path}/docviewer/js/hy2py.js"></script>
<link rel="stylesheet" type="text/css" href="${path}/docviewer/css/docviewer.css"/>
<script type="text/javascript" src="${path}/appframe/plugin/fancyBox/lib/jquery.mousewheel-3.0.6.pack.js"></script>
<!-- Add fancyBox main JS and CSS files -->
<script type="text/javascript" src="${path}/appframe/plugin/fancyBox/source/jquery.fancybox.js?v=2.1.5"></script>
<link rel="stylesheet" type="text/css" href="${path}/appframe/plugin/fancyBox/source/jquery.fancybox.css?v=2.1.5" media="screen" />
<!-- Add Button helper (this is optional) -->
<link rel="stylesheet" type="text/css" href="${path}/appframe/plugin/fancyBox/source/helpers/jquery.fancybox-buttons.css?v=1.0.5" />
<script type="text/javascript" src="${path}/appframe/plugin/fancyBox/source/helpers/jquery.fancybox-buttons.js?v=1.0.5"></script>
<!-- Add Thumbnail helper (this is optional) -->
<link rel="stylesheet" type="text/css" href="${path}/appframe/plugin/fancyBox/source/helpers/jquery.fancybox-thumbs.css?v=1.0.7" />
<script type="text/javascript" src="${path}/appframe/plugin/fancyBox/source/helpers/jquery.fancybox-thumbs.js?v=1.0.7"></script>
<!-- Add Media helper (this is optional) -->
<script type="text/javascript" src="${path}/appframe/plugin/fancyBox/source/helpers/jquery.fancybox-media.js?v=1.0.6"></script>
<!-- 在线阅读JS end -->

<!-- 使用respond.js来为IE(6-8)添加Media Query支持-->
<script type="text/javascript" src="${path}/appframe/plugin/Respond/dest/respond.src.js"></script>

<!-- 使用placeholder.js来为IE(6-8)添加placeholder支持-->
<script type="text/javascript" src="${path}/appframe/plugin/placeholder/jquery.placeholder.js"></script>
<script>jQuery(function($){$('input, textarea').placeholder();});</script>

<!-- 遮罩层 -->
<link rel="stylesheet" type="text/css" href="${path}/appframe/plugin/loadmask/jquery.loadmask.css"/>
<script type="text/javascript" src="${path}/appframe/plugin/loadmask/jquery.loadmask.min.js"></script>
<script type="text/javascript" src="${path}/appframe/plugin/blockui-master/jquery.blockUI.js"></script>
<!-- 日期 -->
<script type="text/javascript" charset="utf-8" src="${path}/appframe/plugin/My97DatePicker/WdatePicker.js"></script>
<!-- layer插件  -->
<script type="text/javascript" src="${path}/appframe/plugin/layer/layer/layer.js"></script>
<script type="text/javascript" src="${path}/appframe/plugin/jxmenu/js/jquery-powerFloat-min.js"></script>
<script type="text/javascript" src="${path}/appframe/plugin/jxmenu/js/jquery-xmenu.js"></script>
<link type="text/css" rel="stylesheet" 	href="${path}/appframe/plugin/jxmenu/css/powerFloat.css" />
<link type="text/css" rel="stylesheet" 	href="${path}/appframe/plugin/jxmenu/css/xmenu.css" />
