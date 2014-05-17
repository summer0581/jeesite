<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>包租月记录管理</title>
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
		<li><a href="${ctx}/finance/rentMonth/?infotype=rentout&rent.id=${rentMonth.rent.id}">出租月记录列表</a></li>
		<li class="active"><a href="${ctx}/finance/rentMonth/rentoutform?id=${rentMonth.id}">出租月记录<shiro:hasPermission name="finance:rentMonth:edit">${not empty rentMonth.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="finance:rentMonth:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="rentMonth" action="${ctx}/finance/rentMonth/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<form:hidden path="rent.id" value=""/>
		<form:hidden path="infotype" value="rentout"/>
		<tags:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">出租业务员:</label>
			<div class="controls">
				<tags:treeselect id="person" name="person.id" notAllowSelectParent="true" value="${rentMonth.person.id}" labelName="person.name" labelValue="${rentMonth.person.name}"
					title="人员" url="/sys/user/treeData" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">付款方式:</label>
			<div class="controls">
				<form:select path="paytype">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('finance_rent_paytype')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">出租时间:</label>
			<div class="controls">
				<input id="sdate" name="sdate" type="text"  maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${rentMonth.sdate}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
				至
				<input id="edate" name="edate" type="text"  maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${rentMonth.edate}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">押金:</label>
			<div class="controls">
				<form:input path="deposit" htmlEscape="false" maxlength="64" class="digits"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">月租金:</label>
			<div class="controls">
				<form:input path="rentmonth" htmlEscape="false" maxlength="64" class="digits"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">已付月份:</label>
			<div class="controls">
				<input id="lastpaysdate" name="lastpaysdate" type="text"  maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${rentMonth.lastpaysdate}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
				至
				<input id="lastpayedate" name="lastpayedate" type="text"  maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${rentMonth.lastpayedate}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">下次付租日期:</label>
			<div class="controls">
				<input id="nextpaydate" name="nextpaydate" type="text"  maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${rentMonth.nextpaydate}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			</div>
		</div>	
		<div class="control-group">
			<label class="control-label">第几期的头月（如果是头月则填，不是头月则为空）:</label>
			<div class="controls">
			<form:input path="firstmonth_num" htmlEscape="false" maxlength="64" class="digits"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">中介费:</label>
			<div class="controls">
			<form:input path="agencyfee" htmlEscape="false" maxlength="64" class="digits"/>			
			</div>
		</div>	
		
	
		<div class="control-group">
			<label class="control-label">已收金额:</label>
			<div class="controls">
				<form:input path="amountreceived" htmlEscape="false" maxlength="64" class="digits"/>
			</div>
		</div>	


		<div class="control-group">
			<label class="control-label">备注:</label>
			<div class="controls">
				<form:textarea path="remarks" htmlEscape="false" rows="4" maxlength="200" class="input-xxlarge"/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="finance:rentMonth:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
