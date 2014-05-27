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
		<li><a href="${ctx}/finance/rentMonth/?infotype=rentin&rent.id=${rentMonth.rent.id}">承租月记录列表</a></li>
		<li class="active"><a href="${ctx}/finance/rentMonth/rentinform?id=${rentMonth.id}">承租月记录<shiro:hasPermission name="finance:rentMonth:edit">${not empty rentMonth.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="finance:rentMonth:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="rentMonth" action="${ctx}/finance/rentMonth/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<form:hidden path="rent.id" value=""/>
		<form:hidden path="infotype" value="rentin"/>
		<tags:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">承租业务员:</label>
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
			<label class="control-label">承租时间:</label>
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
			<label class="control-label">下次应付金额:</label>
			<div class="controls">
			<form:input path="nextshouldamount" htmlEscape="false" maxlength="64" class="digits"/>			
			</div>
		</div>		
		<div class="control-group">
			<label class="control-label">中介费:</label>
			<div class="controls">
			<form:input path="agencyfee" htmlEscape="false" maxlength="64" class="digits"/>			
			</div>
		</div>	
		<div class="control-group">
			<label class="control-label">业务经理:</label>
			<div class="controls">
			<tags:treeselect id="busi_manager" name="busi_manager.id" notAllowSelectParent="true" value="${rentMonth.busi_manager.id}" labelName="busi_manager.name" labelValue="${rentMonth.busi_manager.name}"
					title="人员" url="/sys/user/treeData" />		
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">业务部长:</label>
			<div class="controls">
			<tags:treeselect id="busi_departleader" name="busi_departleader.id" notAllowSelectParent="true" value="${rentMonth.busi_departleader.id}" labelName="busi_departleader.name" labelValue="${rentMonth.busi_departleader.name}"
					title="人员" url="/sys/user/treeData" />		
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">业务组长:</label>
			<div class="controls">
			<tags:treeselect id="busi_teamleader" name="busi_teamleader.id" notAllowSelectParent="true" value="${rentMonth.busi_teamleader.id}" labelName="busi_teamleader.name" labelValue="${rentMonth.busi_teamleader.name}"
					title="人员" url="/sys/user/treeData" />			
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">空置期提成方案:</label>
			<div class="controls">
			<form:select id="cut_vacantperiodtype" path="cut_vacantperiodtype" class="">
			<form:options items="${vacantPeriodCutconfigs}" itemLabel="name" itemValue="cut_code" htmlEscape="false"/></form:select>	
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">业绩提成方案:</label>
			<div class="controls">
			<form:select id="cut_businesssaletype" path="cut_businesssaletype" class="">
			<form:options items="${businessSaleCutconfigs}" itemLabel="name" itemValue="cut_code" htmlEscape="false"/></form:select>	
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
