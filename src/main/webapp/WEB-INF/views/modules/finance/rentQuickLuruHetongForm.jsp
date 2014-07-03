<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>包租明细管理</title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.tabiframe{width:100%; height:670px; border:0px;}
	</style>
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
	<form:form id="inputForm" modelAttribute="rent" action="${ctx}/finance/rent/save4quickLuruHetong" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		
		<tags:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">编号:</label>
			<div class="controls">
				<form:input path="business_num" htmlEscape="false" rows="4" maxlength="200" class="required input-xxlarge"/>
				<shiro:hasPermission name="finance:rent:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="包租主信息保存"/>&nbsp;</shiro:hasPermission>
				<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">地址:</label>
			<div class="controls">
				<form:input id="house.name" path="house.name" htmlEscape="false" maxlength="64" class="required input-xlarge" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">楼盘:</label>
			<div class="controls">
				<form:input id="house.houses" path="house.houses" htmlEscape="false" maxlength="64" class=" input-xlarge" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">租进业务员:</label>
			<div class="controls">
			<tags:treeselect id="rentin_person" name="rentin_person.id" notAllowSelectParent="true" value="${rent.rentin_person.id}" labelName="rentin_person.name" labelValue="${rent.rentin_person.name}"
					title="人员" url="/sys/user/treeData" />	
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">承租付款方式:</label>
			<div class="controls">
				<form:select path="rentin_paytype">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('finance_rent_paytype')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">承租时间:</label>
			<div class="controls">
				<input id="rentin_sdate" name="rentin_sdate" type="text"  maxlength="20" class="input-medium Wdate"
				value="<fmt:formatDate value="${rent.rentin_sdate}" pattern="yyyy-MM-dd"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
				至
				<input id="rentin_edate" name="rentin_edate" type="text"  maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${rent.rentin_edate}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">已付月份:</label>
			<div class="controls">
				<input id="rentin_lastpaysdate" name="rentin_lastpaysdate" type="text"  maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${rent.rentin_lastpaysdate}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
				至
				<input id="rentin_lastpayedate" name="rentin_lastpayedate" type="text"  maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${rent.rentin_lastpayedate}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">下次付租时间:</label>
			<div class="controls">
				<input id="rentin_nextpaydate" name="rentin_nextpaydate" type="text"  maxlength="20" class="input-medium Wdate"
				value="<fmt:formatDate value="${rent.rentin_nextpaydate}" pattern="yyyy-MM-dd"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">房东给出的空置期:</label>
			<div class="controls">
				<form:input id="landlord_vacantPeriods" path="landlord_vacantPeriodsTemp" htmlEscape="false" maxlength="64" class=" input-xlarge" />
				<span class="badge badge-warning" title="空置期请用逗号隔开，如'20,20,20'或'2014-06-07,20,30">注</span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">业务员空置期:</label>
			<div class="controls">
				<form:input id="busisaler_vacantPeriods" path="busisaler_vacantPeriodsTemp" htmlEscape="false" maxlength="64" class=" input-xlarge" />
				<span class="badge badge-warning" title="空置期请用逗号隔开，如'20,20,20'或'2014-06-07,20,30">注</span>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">押金:</label>
			<div class="controls">
				<form:input id="rentin_deposit" path="rentin_deposit" htmlEscape="false" maxlength="64" class=" input-xlarge" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">月租金:</label>
			<div class="controls">
				<form:input id="rentin_rentmonth" path="rentin_rentmonth" htmlEscape="false" maxlength="64" class=" input-xlarge" />
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">承租备注:</label>
			<div class="controls">
				<form:textarea id="rentin_remarks" path="rentin_remarks" htmlEscape="false" rows="4" maxlength="200" class="input-xxlarge"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">房东姓名:</label>
			<div class="controls">
				<form:input id="house.landlord.name" path="house.landlord.name" htmlEscape="false" maxlength="64" class=" input-xlarge" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">房东联系方式:</label>
			<div class="controls">
				<form:input id="house.landlord.telephone" path="house.landlord.telephone" htmlEscape="false" maxlength="64" class=" input-xlarge" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">是否为兴业银行:</label>
			<div class="controls">
				<form:select path="house.is_xingyebank">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">转账卡号:</label>
			<div class="controls">
				<form:input id="house.debit_card" path="house.debit_card" htmlEscape="false" maxlength="64" class=" input-xlarge" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">收款户名:</label>
			<div class="controls">
				<form:input id="house.receive_username" path="house.receive_username" htmlEscape="false" maxlength="64" class=" input-xlarge" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">收款银行及营业网点:</label>
			<div class="controls">
				<form:input id="house.receive_bank" path="house.receive_bank" htmlEscape="false" maxlength="64" class=" input-xlarge" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">是否同城:</label>
			<div class="controls">
				<form:select path="house.is_samecity">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">汇入地址:</label>
			<div class="controls">
				<form:input id="house.remit_address" path="house.remit_address" htmlEscape="false" maxlength="64" class=" input-xlarge" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">租户姓名:</label>
			<div class="controls">
				<form:input id="house.tenant.name" path="house.tenant.name" htmlEscape="false" maxlength="64" class=" input-xlarge" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">租户联系方式:</label>
			<div class="controls">
				<form:input id="house.tenant.telephone" path="house.tenant.telephone" htmlEscape="false" maxlength="64" class=" input-xlarge" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">经理:</label>
			<div class="controls">
			<tags:treeselect id="rentin_busi_manager" name="rentin_busi_manager.id" notAllowSelectParent="true" value="${rent.rentin_busi_manager.id}" labelName="rentin_busi_manager.name" labelValue="${rent.rentin_busi_manager.name}"
					title="人员" url="/sys/user/treeData" />					
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">部长:</label>
			<div class="controls">
			<tags:treeselect id="rentin_busi_departleader" name="rentin_busi_departleader.id" notAllowSelectParent="true" value="${rent.rentin_busi_departleader.id}" labelName="rentin_busi_departleader.name" labelValue="${rent.rentin_busi_departleader.name}"
					title="人员" url="/sys/user/treeData" cssClass="required"/>	
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">组长:</label>
			<div class="controls">
			<tags:treeselect id="rentin_busi_teamleader" name="rentin_busi_teamleader.id" notAllowSelectParent="true" value="${rent.rentin_busi_teamleader.id}" labelName="rentin_busi_teamleader.name" labelValue="${rent.rentin_busi_teamleader.name}"
					title="人员" url="/sys/user/treeData" />	
			</div>
		</div>


	</form:form>
</body>
</html>
