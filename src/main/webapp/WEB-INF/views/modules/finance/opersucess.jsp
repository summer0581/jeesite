<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>操作成功</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		
	</script>
</head>
<body>
	<tags:message content="${not empty message?message : '操作成功!'}"/>
	<div>请点击关闭按钮进行当前窗口关闭！</div>
</body>
</html>
