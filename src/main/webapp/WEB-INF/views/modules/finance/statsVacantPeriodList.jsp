<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>空置期提成管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
		});
		function page(n,s){
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
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/finance/stats/vacantPeriod">空置期提成</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="vacantPeriod" action="${ctx}/finance/stats/vacantPeriod" method="post" class="breadcrumb form-search">
	<input id="orderBy" name="orderBy" type="hidden" value="${paramMap.orderBy}"/>
	<input id="showHighSearch" name="showHighSearch" type="hidden" value="${paramMap.showHighSearch}"/>
	<div><label>房屋地址：</label><input name="name" type="text" maxlength="50" class="input-small" value="${paramMap.name}"/>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
		&nbsp;<input id="btnReset" class="btn btn-primary" onclick="resets()" type="button" value="重置"/>
		&nbsp;<input id="btnShow" class="btn btn-primary" onclick="showOrHidden()" type="button" value="高级查询"/>
	</div>
	<div id="pro_search" style="margin-top:10px;${'true' eq paramMap.showHighSearch?'':'display:none;'}">
		<label>承租开始时间：</label>
		<input id="rentin_sdate_begin" name="rentin_sdate_begin" type="text"  maxlength="20" class="input-small Wdate"
				value="${paramMap.rentin_sdate_begin}" onclick="WdatePicker({maxDate:'#F{$dp.$D(\'rentin_sdate_end\')}',dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
		至<input id="rentin_sdate_end" name="rentin_sdate_end" type="text"   maxlength="20" class="input-small Wdate"
				value="${paramMap.rentin_sdate_end}" onclick="WdatePicker({minDate:'#F{$dp.$D(\'rentin_sdate_begin\')}',dateFmt:'yyyy-MM-dd',isShowClear:false});"/>&nbsp;&nbsp;	
		<label>出租开始时间：</label>
		<input id="rentout_sdate_begin" name="rentout_sdate_begin" type="text"  maxlength="20" class="input-small Wdate"
				value="${paramMap.rentout_sdate_begin}" onclick="WdatePicker({maxDate:'#F{$dp.$D(\'rentout_sdate_end\')}',dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
		至<input id="rentout_sdate_end" name="rentout_sdate_end" type="text"   maxlength="20" class="input-small Wdate"
				value="${paramMap.rentout_sdate_end}" onclick="WdatePicker({minDate:'#F{$dp.$D(\'rentout_sdate_begin\')}',dateFmt:'yyyy-MM-dd',isShowClear:false});"/>&nbsp;&nbsp;			
	</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>地址</th>
				<th>租进业务员</th>
				<th>承租情况</th>
				<th>出租情况</th>
				<th>租金</th>
				<th>租出业务员</th>
				<th>组长</th>
				<th>空置期天数</th>
				<th>租进业务员提成</th>
				<th>租出业务员提成</th>
				<th>部长提成</th>
				<th>部门经理提成</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${list}" var="rent">
			<tr>
				<td>${rent.house.name}</td>
				<td>${rent.rentin_person }</td>
				<td><fmt:formatDate value="${rent.rentin_sdate}" pattern="yyyy-MM-dd"/>-<br/><fmt:formatDate value="${rent.rentin_edate}" pattern="yyyy-MM-dd"/></td>
				<td><fmt:formatDate value="${rent.rentout_sdate}" pattern="yyyy-MM-dd"/>-<br/><fmt:formatDate value="${rent.rentout_edate}" pattern="yyyy-MM-dd"/></td>
				<td>${rent.rentout_rentmonth }</td>
				<td>${rent.rentout_person }</td>
				<td>${rent.house.team_leader.name }</td>
				<td>${rent.vacantperiod }</td>
				<td>${rent.rentin_cut }</td>
				<td>${rent.rentout_cut }</td>
				<td>${rent.teamleader_cut }</td>
				<td>${rent.manager_cut }</td>
			</tr>
		</c:forEach>
		<tr>
			<td colspan="8" class="total_td">合计</td>
			<td>${total.rentin_cut_total }</td>
			<td>${total.rentout_cut_total }</td>
			<td>${total.teamleader_cut_total }</td>
			<td>${total.manager_cut_total }</td>
		</tr>
		</tbody>
	</table>
	
</body>
</html>
