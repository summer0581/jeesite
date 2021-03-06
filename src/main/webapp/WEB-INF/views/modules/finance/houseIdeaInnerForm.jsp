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

	<form:form id="inputForm" modelAttribute="houseIdea" action="${ctx}/finance/houseIdea/innerSave" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<form:hidden path="createBy.id" />
		<form:hidden path="house.id" />
		<form:hidden path="type" value="genjin" />
		<tags:message content="${message}"/>

		<div class="control-group" style="margin-top:20px;">
			<label class="control-label">跟进内容:</label>
			<div class="controls">
				<form:textarea path="content" htmlEscape="false" rows="4" maxlength="1000" class="required" style="width:90%;"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">跟进人:</label>
			<div class="controls">
				<form:input path="createBy.name" htmlEscape="false"  maxlength="200" class="input-small"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">跟进时间:</label>
			<div class="controls">
				<input id="createDate" name="createDate" type="text"  maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${houseIdea.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="finance:houseIdeaInner:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
		</div>
	</form:form>
</body>
</html>
