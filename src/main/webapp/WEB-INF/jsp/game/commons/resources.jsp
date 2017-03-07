<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/commons/taglibs.jsp" %>
<%
	int _port = request.getServerPort();
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();

//	if (_port == 80) {
		basePath =  request.getScheme() + "://" + request.getServerName();
//	}
	request.setAttribute("ctx", basePath);
%>
<c:set var="resourcesVersion" value="1.0.1" />
<!-- meta -->
<meta charset="UTF-8">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge,Chrome=1" />
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="renderer" content="webkit|ie-comp|ie-stand">

<!-- #icon -->
<link rel="shortcut icon" type="image/x-icon" href="${ctx}/resources/images/angry.png?v=${resourcesVersion}" media="screen" />

<!-- #fonts -->
<link href="https://cdn.bootcss.com/font-awesome/4.6.3/css/font-awesome.min.css" rel="stylesheet">

<!-- #css -->
<link rel="stylesheet" href="${ctx}/resources/plugins/materialize/css/materialize.css">
<!-- 语法高亮 -->
<link rel="stylesheet" href="${ctx}/resources/plugins/prism/prism.css">
<!-- custom -->
<link rel="stylesheet" href="${ctx}/resources/css/style_game.css?v=${resourcesVersion}">

<!-- #js -->
<script src="https://cdn.bootcss.com/jquery/3.1.0/jquery.min.js"></script>

<script src="${ctx}/resources/plugins/materialize/js/materialize.js"></script>
<!-- trianglify -->
<script src="https://cdn.bootcss.com/trianglify/1.0.1/trianglify.min.js"></script>
<!-- jquery.cookie -->
<script src="https://cdn.bootcss.com/jquery-cookie/1.4.1/jquery.cookie.min.js"></script>
<!-- custom -->
<script src="${ctx}/resources/js/common.js"></script>

<!--[if lt IE 9]>

<![endif]-->
<script>
(function (){
	$.baseData = {
		"basePath":"${ctx}",
		"system":{
			"dateTime":new Date(parseInt('${sys_time}')),
		}
	}
})(jQuery);
</script>
<!-- 百度统计代码 -->
<jsp:include page="/WEB-INF/jsp/baidu_hm.jsp" />