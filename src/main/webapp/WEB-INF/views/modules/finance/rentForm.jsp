<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>包租明细管理</title>
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
		<li><a href="${ctx}/finance/rent/">包租明细列表</a></li>
		<li class="active"><a href="${ctx}/finance/rent/form?id=${rent.id}">包租明细<shiro:hasPermission name="finance:rent:edit">${not empty rent.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="finance:rent:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="rent" action="${ctx}/finance/rent/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<tags:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">房屋名称:</label>
			<div class="controls">
				<form:hidden id="houseid" path="house.id" htmlEscape="false" maxlength="64" class="input-xlarge"/>
				<form:input id="name" path="name" htmlEscape="false" maxlength="64" class="input-xlarge" readonly="true"/>
				<a id="relationButton" href="javascript:" class="btn">选择房屋</a>
				<script type="text/javascript">
					var houseSelect = [];
					function houseSelectAddOrDel(id,title){
						houseSelect = []
						houseSelect.push([id,title]);
						houseSelectRefresh();
					}
					function houseSelectRefresh(){
						$("#houseid").val("");
						$("#name").val("");
						for (var i=0; i<houseSelect.length; i++){
							$("#name").val(houseSelect[i][1]);
							$("#houseid").val(houseSelect[i][0]);
						}
					}
					$.getJSON("${ctx}/finance/house/findByIds",{ids:$("#houseid").val()},function(data){
						for (var i=0; i<data.length; i++){
							houseSelect.push([data[i][1],data[i][2]]);
						}
						//houseSelectRefresh();
					});
					$("#relationButton").click(function(){
						top.$.jBox.open("iframe:${ctx}/finance/house/selectList?pageSize=8", "添加房屋",$(top.document).width()-220,$(top.document).height()-180,{
							buttons:{"确定":true}, loaded:function(h){
								$(".jbox-content", top.document).css("overflow-y","hidden");
							}
						});
					});
				</script>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">承租业务员:</label>
			<div class="controls">
			<tags:treeselect id="rentin_person" name="rentin_person.id" notAllowSelectParent="true" value="${rent.rentin_person.id}" labelName="rentin_person.name" labelValue="${rent.rentin_person.name}"
					title="人员" url="/sys/user/treeData" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">承租付款方式:</label>
			<div class="controls">
				<form:select path="rentin_paytype">
					<form:options items="${fns:getDictList('finance_rent_paytype')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">承租时间:</label>
			<div class="controls">
				<input id="rentin_sdate" name="rentin_sdate" type="text"  maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${rent.rentin_sdate}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
				至
				<input id="rentin_edate" name="rentin_edate" type="text"  maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${rent.rentin_edate}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">承租押金:</label>
			<div class="controls">
				<form:input path="rentin_deposit" htmlEscape="false" maxlength="64" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">承租月租金:</label>
			<div class="controls">
				<form:input path="rentin_rentmonth" htmlEscape="false" maxlength="64" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">承租已付月份:</label>
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
			<label class="control-label">承租下次付租日期:</label>
			<div class="controls">
				<input id="rentin_nextpaydate" name="rentin_nextpaydate" type="text"  maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${rent.rentin_nextpaydate}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">出租时间:</label>
			<div class="controls">
				<input id="rentout_sdate" name="rentout_sdate" type="text"  maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${rent.rentout_sdate}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
				至
				<input id="rentout_edate" name="rentout_edate" type="text"  maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${rent.rentout_edate}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">出租押金:</label>
			<div class="controls">
				<form:input path="rentout_deposit" htmlEscape="false" maxlength="64" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">出租月租金:</label>
			<div class="controls">
				<form:input path="rentout_rentmonth" htmlEscape="false" maxlength="64" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">出租已付月份:</label>
			<div class="controls">
				<input id="rentout_lastpaysdate" name="rentout_lastpaysdate" type="text"  maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${rent.rentout_lastpaysdate}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">出租上次付款结束日期:</label>
			<div class="controls">
				<input id="rentout_lastpayedate" name="rentout_lastpayedate" type="text"  maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${rent.rentout_lastpayedate}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">出租已收金额:</label>
			<div class="controls">
				<form:input path="rentout_amountreceived" htmlEscape="false" maxlength="64" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">出租下次付租日期:</label>
			<div class="controls">
				<input id="rentout_nextpaydate" name="rentout_nextpaydate" type="text"  maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${rent.rentout_nextpaydate}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">出租付款方式:</label>
			<div class="controls">
				<form:select path="rentout_paytype">
					<form:options items="${fns:getDictList('finance_rent_paytype')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">出租业务员:</label>
			<div class="controls">
				<tags:treeselect id="rentout_person" name="rentout_person.id" notAllowSelectParent="true" value="${rent.rentout_person.id}" labelName="rentout_person.name" labelValue="${rent.rentout_person.name}"
					title="人员" url="/sys/user/treeData" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">出租每月利润:</label>
			<div class="controls">
				<form:input path="rentout_profitmonth" htmlEscape="false" maxlength="64" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">备注:</label>
			<div class="controls">
				<form:textarea path="remarks" htmlEscape="false" rows="4" maxlength="200" class="input-xxlarge"/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="finance:rent:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
