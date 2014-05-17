<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>包租明细管理</title>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<meta name="decorator" content="default"/>
	<style type="text/css">
	.table thead th.th_center{
		text-align: center;
		vertical-align: middle;
	}
	</style>
	
	<script type="text/javascript">
		$(document).ready(function() {
			$.initSortTable("contentTable",page);
			
			$("#btnExport").click(function(){
				top.$.jBox.confirm("确认要导出房屋包租数据吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/finance/rent/export");
						$("#searchForm").submit();
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			$("#btnImport").click(function(){
				$.jBox($("#importBox").html(), {title:"导入数据", buttons:{"关闭":true}, 
					bottomText:"导入文件不能超过5M，仅允许导入“xls”或“xlsx”格式文件！"});
			});
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
		function resets(){
			$("input[type=text]","#searchForm").val("");
			$("#searchForm").submit();
		}
		
		function showOrHidden(){
			$("#pro_search").fadeToggle("fast");
			if("true" != $("#showHighSearch").val()){
				$("#showHighSearch").val("true");
			}else{
				$("#showHighSearch").val("false");
			}
			
		}
		
		
	</script>
</head>
<body>
	<div id="importBox" class="hide">
		<form id="importForm" action="${ctx}/finance/rent/import" method="post" enctype="multipart/form-data"
			style="padding-left:20px;text-align:center;" class="form-search" onsubmit="loading('正在导入，请稍等...');"><br/>
			<input id="uploadFile" name="file" type="file" style="width:330px"/><br/><br/>　　
			<input id="btnImportSubmit" class="btn btn-primary" type="submit" value="   导    入   "/>
			<a href="${ctx}/finance/rent/import/template">下载模板</a>
		</form>
	</div>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/finance/rent/">包租明细列表</a></li>
		<shiro:hasPermission name="finance:rent:edit"><li><a href="${ctx}/finance/rent/form">包租明细添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="rent" action="${ctx}/finance/rent/rentList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<input id="showHighSearch" name="showHighSearch" type="hidden" value="${paramMap.showHighSearch}"/>
		<div><label>房屋地址：</label>
		<form:input path="name" htmlEscape="false" maxlength="50" class="input-small" value="${paramMap.name}"/>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
		&nbsp;<input id="btnReset" class="btn btn-primary" onclick="resets()" type="button" value="重置"/>
		&nbsp;<input id="btnShow" class="btn btn-primary" onclick="showOrHidden()" type="button" value="高级查询"/>
		<shiro:hasPermission name="finance:house:view">
			&nbsp;<input id="btnExport" class="btn btn-primary" type="button" value="导出"/>
		</shiro:hasPermission>
		<shiro:hasPermission name="finance:house:edit">
			&nbsp;<input id="btnImport" class="btn btn-primary" type="button" value="导入"/>
		</shiro:hasPermission>		</div>
		<div id="pro_search" style="margin-top:10px;${'true' eq paramMap.showHighSearch?'':'display:none;'}">
		<label>承租下次付租时间：</label>
		<input id="rentin_nextpaysdate" name="rentin_nextpaysdate" type="text"  maxlength="20" class="input-small Wdate"
				value="${paramMap.rentin_nextpaysdate}" onclick="WdatePicker({maxDate:'#F{$dp.$D(\'rentin_nextpayedate\')}',dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
		至
		<input id="rentin_nextpayedate" name="rentin_nextpayedate" type="text"  maxlength="20" class="input-small Wdate"
				value="${paramMap.rentin_nextpayedate}" onclick="WdatePicker({minDate:'#F{$dp.$D(\'rentin_nextpaysdate\')}',dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
		<label>出租下次付租时间：</label>
				<input id="rentout_nextpaysdate" name="rentout_nextpaysdate" type="text"  maxlength="20" class="input-small Wdate"
				value="${paramMap.rentout_nextpaysdate}" onclick="WdatePicker({maxDate:'#F{$dp.$D(\'rentout_nextpayedate\')}',dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
		至
		<input id="rentout_nextpayedate" name="rentout_nextpayedate" type="text"  maxlength="20" class="input-small Wdate"
				value="${paramMap.rentout_nextpayedate}" onclick="WdatePicker({minDate:'#F{$dp.$D(\'rentout_nextpaysdate\')}',dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr>
			<th rowspan="2" class="th_center">房屋地址</th>
			<th colspan="6" class="th_center">承租情况</th>
			<th colspan="7" class="th_center">出租情况</th>
			<th rowspan="2" class="th_center">每月利润</th>
			<shiro:hasPermission name="finance:rent:edit"><th rowspan="2" class="th_center">操作</th></shiro:hasPermission>
		</tr>
		<tr>
			<th>业务员</th>
			<th>付款方式</th>
			<th>承租时间</th>
			<th>月租金</th>
			<th>已付月份</th>
			<th >下次付租时间</th>
			
			<th>业务员</th>
			<th>付款方式</th>
			<th>出租时间</th>
			<th>月租金</th>
			<th>已付月份</th>
			<th>已收金额</th>
			<th >下次收租时间</th>
		</tr>
		
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="rent">
			<tr>
				<td><a href="${ctx}/finance/rent/form?id=${rent.id}">${rent.name}</a></td>
				<td>${rent.rentinMonths[0].person.name}</td>
				<td>${fns:getDictLabel(rent.rentinMonths[0].paytype, 'finance_rent_paytype', '')}</td>
				<td><fmt:formatDate value="${rent.rentinMonths[0].sdate}" pattern="yyyy-MM-dd"/>-<br/><fmt:formatDate value="${rent.rentinMonths[0].edate}" pattern="yyyy-MM-dd"/></td>
				<td>${rent.rentinMonths[0].rentmonth}</td>
				<td><fmt:formatDate value="${rent.rentinMonths[0].lastpaysdate}" pattern="yyyy-MM-dd"/>-<br/><fmt:formatDate value="${rent.rentinMonths[0].lastpayedate}" pattern="yyyy-MM-dd"/></td>
				<td><fmt:formatDate value="${rent.rentinMonths[0].nextpaydate}"  pattern="yyyy-MM-dd" /></td>
				<td>${rent.rentoutMonths[0].person.name}</td>
				<td>${fns:getDictLabel(rent.rentoutMonths[0].paytype, 'finance_rent_paytype', '')}</td>
				<td><fmt:formatDate value="${rent.rentoutMonths[0].sdate}" pattern="yyyy-MM-dd"/>-<br/><fmt:formatDate value="${rent.rentoutMonths[0].edate}" pattern="yyyy-MM-dd"/></td>
				<td>${rent.rentoutMonths[0].rentmonth}</td>
				<td><fmt:formatDate value="${rent.rentoutMonths[0].lastpaysdate}" pattern="yyyy-MM-dd"/>-<br/><fmt:formatDate value="${rent.rentoutMonths[0].lastpayedate}" pattern="yyyy-MM-dd"/></td>
				<td>${rent.rentoutMonths[0].amountreceived}</td>
				<td><fmt:formatDate value="${rent.rentoutMonths[0].nextpaydate}"  pattern="yyyy-MM-dd" /></td>
				<td>${rent.rentoutMonths[0].rentmonth-rent.rentinMonths[0].rentmonth}</td>
				<shiro:hasPermission name="finance:rent:edit"><td>
    				<a href="${ctx}/finance/rent/form?id=${rent.id}">修改</a>
    				<br/>
					<a href="${ctx}/finance/rent/delete?id=${rent.id}" onclick="return confirmx('确认要删除该包租明细吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
