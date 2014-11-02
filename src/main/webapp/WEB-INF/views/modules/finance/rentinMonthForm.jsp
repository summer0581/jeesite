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
					$("#rentin_sdate",parent.document).val($("#sdate").val());//每次保存租进月记录后都要更新父页面的rentin_sdate字段，让空置期设置能正常
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
		
		function autoSetUserLeaders(){
			if("" == $("#personId").val()){
				alert("请先选择业务员！");
				return;
			}
			$.getJSON("${ctx}/sys/user/getUserLeaders",{userid:$("#personId").val()},function(data){
				var msg = new Array();
				msg.push("成功获取当前业务员的 经理："+data.manager.name);
				if(undefined != data.departleader){
					msg.push(",部长："+data.departleader.name);
				}
				if(undefined != data.teamleader){
					msg.push(",组长："+data.teamleader.name);
				}
				alert(msg.join(""));
				$("#busi_managerId").val(data.manager.id);
				$("#busi_managerName").val(data.manager.name);
				if(undefined != data.departleader){
					$("#busi_departleaderId").val(data.departleader.id);
					$("#busi_departleaderName").val(data.departleader.name);
				}
				if(undefined != data.teamleader){
					$("#busi_teamleaderId").val(data.teamleader.id);
					$("#busi_teamleaderName").val(data.teamleader.name);
				}
			});
		}
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
		<input type="hidden" id="viewtype" name="viewtype" value="${paramMap.viewtype}"/>
		<form:hidden path="infotype" value="rentin"/>
		<tags:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">承租业务员:</label>
			<div class="controls">
				<tags:treeselect id="person" name="person.id" notAllowSelectParent="true" value="${rentMonth.person.id}" labelName="person.name" labelValue="${rentMonth.person.name}"
					title="人员" url="/sys/user/treeData" allowClear="true"/>
				<shiro:hasPermission name="finance:rentMonth:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="承租月记录保存"/>&nbsp;</shiro:hasPermission>
				<input id="btnAutoSetLeaders" class="btn btn-primary" type="button" onclick="autoSetUserLeaders()" value="自动设置业务员领导"/>
				<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
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
			<form:input path="nextshouldamount" htmlEscape="false" maxlength="64" class="number"/>			
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">下次应付备注:</label>
			<div class="controls">
			<form:textarea path="nextshouldremark" htmlEscape="false" rows="4" maxlength="2000" class="input-xxlarge"/>	
			</div>
		</div>
		<!-- <div class="control-group">
			<label class="control-label">第几期的头月（如果是头月则填，不是头月则为空）:</label>
			<div class="controls">
			<form:input path="firstmonth_num" htmlEscape="false" maxlength="64" class="digits"/>
			</div>
		</div> -->
		<div class="control-group">
			<label class="control-label">中介费:</label>
			<div class="controls">
			<form:input path="agencyfee" htmlEscape="false" maxlength="64" class="digits"/>			
			</div>
		</div>	
		<div class="control-group">
			<label class="control-label">承租业务员业绩提成固定值:</label>
			<div class="controls">
				<form:input path="person_fixedcut" htmlEscape="false" maxlength="64" class="input-small digits"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">业务经理:</label>
			<div class="controls">
			<tags:treeselect id="busi_manager" name="busi_manager.id" notAllowSelectParent="true" value="${rentMonth.busi_manager.id}" labelName="busi_manager.name" labelValue="${rentMonth.busi_manager.name}"
					title="人员" url="/sys/user/treeData" allowClear="true"/>
			业务提成固定值:
				<form:input path="manager_fixedcut" htmlEscape="false" maxlength="64" class="input-small digits"/>	
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">业务部长:</label>
			<div class="controls">
			<tags:treeselect id="busi_departleader" name="busi_departleader.id" notAllowSelectParent="true" value="${rentMonth.busi_departleader.id}" labelName="busi_departleader.name" labelValue="${rentMonth.busi_departleader.name}"
					title="人员" url="/sys/user/treeData" allowClear="true"/>	
			业务提成固定值:
				<form:input path="departer_fixedcut" htmlEscape="false" maxlength="64" class="input-small digits"/>		
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">业务组长:</label>
			<div class="controls">
			<tags:treeselect id="busi_teamleader" name="busi_teamleader.id" notAllowSelectParent="true" value="${rentMonth.busi_teamleader.id}" labelName="busi_teamleader.name" labelValue="${rentMonth.busi_teamleader.name}"
					title="人员" url="/sys/user/treeData" allowClear="true"/>		
			业务提成固定值:
				<form:input path="teamleader_fixedcut" htmlEscape="false" maxlength="64" class="input-small digits"/>		
			</div>
		</div>


		<div class="control-group">
			<label class="control-label">备注:</label>
			<div class="controls">
				<form:textarea path="remarks" htmlEscape="false" rows="4" maxlength="200" class="input-xxlarge"/>
			</div>
		</div>
	</form:form>
</body>
</html>
