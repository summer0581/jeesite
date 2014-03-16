<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>包租明细管理</title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
	.table thead th.th_center{
		text-align: center;
		vertical-align: middle;
	}
	</style>
	
	<script type="text/javascript">
		$(document).ready(function() {
			
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
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/finance/rent/">包租明细列表</a></li>
		<shiro:hasPermission name="finance:rent:edit"><li><a href="${ctx}/finance/rent/form">包租明细添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="rent" action="${ctx}/finance/rent/rentList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<div><label>房屋地址：</label><form:input path="name" htmlEscape="false" maxlength="50" class="input-small"/>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
		&nbsp;<input id="btnReset" class="btn btn-primary" onclick="resets()" type="button" value="重置"/>
		</div>
		<div style="margin-top:10px;">
		<label>承租下次付租时间：</label>
		<input id="rentin_nextpaysdate" name="rentin_nextpaysdate" type="text"  maxlength="20" class="input-small Wdate"
				value="${paramMap.rentin_nextpaysdate}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
		至
		<input id="rentin_nextpayedate" name="rentin_nextpayedate" type="text"  maxlength="20" class="input-small Wdate"
				value="${paramMap.rentin_nextpayedate}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
		<label>出租下次付租时间：</label>
				<input id="rentout_nextpaysdate" name="rentout_nextpaysdate" type="text"  maxlength="20" class="input-small Wdate"
				value="${paramMap.rentout_nextpaysdate}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
		至
		<input id="rentout_nextpayedate" name="rentout_nextpayedate" type="text"  maxlength="20" class="input-small Wdate"
				value="${paramMap.rentout_nextpayedate}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr>
			<th rowspan="2" class="th_center">房屋地址</th>
			<th colspan="7" class="th_center">承租情况</th>
			<th colspan="8" class="th_center">出租情况</th>
			<th rowspan="2" class="th_center">每月利润</th>
			<shiro:hasPermission name="finance:rent:edit"><th rowspan="2" class="th_center">操作</th></shiro:hasPermission>
		</tr>
		<tr>
			<th>业务员</th>
			<th>付款方式</th>
			<th>承租时间</th>
			<th>押金</th>
			<th>月租金</th>
			<th>已付月份</th>
			<th>下次付租时间</th>
			
			<th>业务员</th>
			<th>付款方式</th>
			<th>出租时间</th>
			<th>押金</th>
			<th>月租金</th>
			<th>已付月份</th>
			<th>已收金额</th>
			<th>下次付租时间</th>
		</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="rent">
			<tr>
				<td><a href="${ctx}/finance/rent/form?id=${rent.id}">${rent.name}</a></td>
				<td>${rent.rentin_person}</td>
				<td>${rent.rentin_paytype}</td>
				<td><fmt:formatDate value="${rent.rentin_sdate}" pattern="yyyy-MM-dd"/>-<br/><fmt:formatDate value="${rent.rentin_edate}" pattern="yyyy-MM-dd"/></td>
				<td>${rent.rentin_deposit}</td>
				<td>${rent.rentin_rentmonth}</td>
				<td><fmt:formatDate value="${rent.rentin_lastpaysdate}" pattern="yyyy-MM-dd"/>-<br/><fmt:formatDate value="${rent.rentin_lastpayedate}" pattern="yyyy-MM-dd"/></td>
				<td><fmt:formatDate value="${rent.rentin_nextpaydate}" pattern="yyyy-MM-dd"/></td>
				<td>${rent.rentout_person}</td>
				<td>${rent.rentout_paytype}</td>
				<td><fmt:formatDate value="${rent.rentout_lastpaysdate}" pattern="yyyy-MM-dd"/>-<br/><fmt:formatDate value="${rent.rentout_lastpayedate}" pattern="yyyy-MM-dd"/></td>
				<td>${rent.rentout_rentmonth}</td>
				<td>${rent.rentout_amountreceived}</td>
				<td><fmt:formatDate value="${rent.rentout_nextpaydate}" pattern="yyyy-MM-dd"/></td>
				<td>${rent.rentout_deposit}</td>
				<td><fmt:formatDate value="${rent.rentout_sdate}" pattern="yyyy-MM-dd"/>-<br/><fmt:formatDate value="${rent.rentout_edate}" pattern="yyyy-MM-dd"/></td>
				<td>${rent.rentout_profitmonth}</td>
				<shiro:hasPermission name="finance:rent:edit"><td>
    				<a href="${ctx}/finance/rent/form?id=${rent.id}">修改</a>
    				<br/>
					<a href="${ctx}/finance/rent/delete?id=${rent.id}" onclick="return confirmx('确认要删除该包租明细吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
