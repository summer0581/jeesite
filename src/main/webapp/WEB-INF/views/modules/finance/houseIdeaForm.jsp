<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>房屋跟进意见管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/finance/houseIdea/">房屋跟进意见列表</a></li>
		<li class="active"><a href="${ctx}/finance/houseIdea/form?id=${houseIdea.id}">房屋跟进意见<shiro:hasPermission name="finance:houseIdea:edit">${not empty houseIdea.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="finance:houseIdea:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="houseIdea" action="${ctx}/finance/houseIdea/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<tags:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">跟进内容:</label>
			<div class="controls">
				<form:textarea path="content" htmlEscape="false" rows="4" maxlength="1000" class="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">跟进人:</label>
			<div class="controls">
				<form:input path="createBy" htmlEscape="false"  maxlength="200" class="input-small"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">跟进时间:</label>
			<div class="controls">
				<form:input path="createDate" htmlEscape="false"  maxlength="200" class="input-small"/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="finance:houseIdea:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
