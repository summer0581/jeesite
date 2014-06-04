<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>空置期个人提成明细</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#btnExport").click(function(){
				top.$.jBox.confirm("确认要导出房屋包租空置期(个人明细）数据吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/finance/stats/export/vacantPeriodDetail4Person");
						$("#searchForm").submit();
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
		});

	</script>
</head>
<body>
	<form:form id="searchForm" modelAttribute="vacantPeriod" action="${ctx}/finance/stats/vacantPeriodDetail4Person" method="post" class="breadcrumb form-search">
	<div>
		<input id="personid" name="personid" type="hidden" value="${paramMap.personid}"/>
		&nbsp;<input id="btnExport" class="btn btn-primary" type="button" value="导出"/>
	</div>
	</form:form>

	<h3>承租空置期提成汇总</h3>
	<table id="rentinRentMonths" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>编号</th>
				<th>地址</th>
				<th>租进业务员</th>
				<th>承租情况</th>
				<th>出租情况</th>
				<th>租金</th>
				<th>租出业务员</th>
				<th>部长</th>
				<th>空置期设置</th>
				<th>空置期天数</th>
				<th>租进业务员提成</th>
				<th>组长提成</th>
				<th>部长提成</th>
				<th>经理提成</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${rentinRentMonths}" var="var">
			<tr>
				<td>${var.rentinmonth.rent.business_num}</td> 
				<td>${var.rentinmonth.rent.house.name}</td>
				<td>${var.rentinmonth.person.name }</td>
				<td><fmt:formatDate value="${var.rentinmonth.sdate}" pattern="yyyy-MM-dd"/>-<br/><fmt:formatDate value="${var.rentinmonth.edate}" pattern="yyyy-MM-dd"/></td>
				<td><fmt:formatDate value="${var.rentmonth.sdate}" pattern="yyyy-MM-dd"/>-<br/><fmt:formatDate value="${var.rentmonth.edate}" pattern="yyyy-MM-dd"/></td>
				<td>${var.rentmonth.rentmonth }</td>
				<td>${var.rentmonth.person.name }</td>
				<td>${var.rentinmonth.busi_departleader.name }</td>
				<td>${var.vacantperiodconfig}</td>
				<td>${var.vacantperiod }</td>
				<td>${var.rentin_cut }</td>
				<td>${var.teamleader_cut }</td>
				<td>${var.departleader_cut }</td>
				<td>${var.manager_cut }</td>
			</tr>
		</c:forEach>
			<tr>
				<td colspan="10">合计</td>
				<td >${totalMap.rentin_cut_total }</td>
				<td >${totalMap.teamleader_cut_total }</td>
				<td >${totalMap.departleader_cut_total }</td>
				<td >${totalMap.manager_cut_total }</td>
			</tr>
		</tbody>
	</table>
	<h3>出租空置期提成汇总</h3>
	<table id="rentoutRentMonths" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>编号</th>
				<th>地址</th>
				<th>租进业务员</th>
				<th>承租情况</th>
				<th>出租情况</th>
				<th>租金</th>
				<th>租出业务员</th>
				<th>部长</th>
				<th>空置期设置</th>
				<th>空置期天数</th>
				<th>租出业务员提成</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${rentoutRentMonths}" var="var">
			<tr>
				<td>${var.rentinmonth.rent.business_num}</td>
				<td>${var.rentinmonth.rent.house.name}</td>
				<td>${var.rentinmonth.person.name }</td> 
				<td><fmt:formatDate value="${var.rentinmonth.sdate}" pattern="yyyy-MM-dd"/>-<br/><fmt:formatDate value="${var.rentinmonth.edate}" pattern="yyyy-MM-dd"/></td>
				<td><fmt:formatDate value="${var.rentmonth.sdate}" pattern="yyyy-MM-dd"/>-<br/><fmt:formatDate value="${var.rentmonth.edate}" pattern="yyyy-MM-dd"/></td>
				<td>${var.rentmonth.rentmonth }</td>
				<td>${var.rentmonth.person.name }</td>
				<td>${var.rentinmonth.busi_departleader.name }</td>
				<td>${var.vacantperiodconfig}</td>
				<td>${var.vacantperiod }</td>
				<td>${var.rentout_cut }</td>
			</tr>		
		</c:forEach>
			<tr>
				<td colspan="10">合计</td>
				<td >${totalMap.rentout_cut_total }</td>
			</tr>
		</tbody>
	</table>	
</body>
</html>
