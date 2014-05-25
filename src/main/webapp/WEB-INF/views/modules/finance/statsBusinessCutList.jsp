<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>业绩提成管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#periodtotaltab").click(function(){
				$(this).attr("href","${ctx}/finance/stats/businessCut4Person?rentout_sdate_begin="+$("#rentout_sdate_begin").val()+"&rentout_sdate_end="+$("#rentout_sdate_end").val());
			})
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
		
		function openWindow(url,title){
			top.$.jBox.open("iframe:"+url, title,$(top.document).width()-220,$(top.document).height()-80,{
				buttons:{"确定":true}, loaded:function(h){
					$(".jbox-content", top.document).css("overflow-y","hidden");
				}
			});
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/finance/stats/businessCut">业绩提成</a></li>
		<li ><a href="#" id="periodtotaltab">个人总计</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="businessCut" action="${ctx}/finance/stats/businessCut" method="post" class="breadcrumb form-search">
	<input id="orderBy" name="orderBy" type="hidden" value="${paramMap.orderBy}"/>
	<input id="showHighSearch" name="showHighSearch" type="hidden" value="${paramMap.showHighSearch}"/>
	<div><label>房屋地址：</label><input name="name" type="text" maxlength="50" class="input-small" value="${paramMap.name}"/>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
		&nbsp;<input id="btnReset" class="btn btn-primary" onclick="resets()" type="button" value="重置"/>
		&nbsp;<input id="btnShow" class="btn btn-primary" onclick="showOrHidden()" type="button" value="高级查询"/>
	</div>
	<div id="pro_search" style="margin-top:10px;${'true' eq paramMap.showHighSearch?'':'display:none;'}">
		<label>提成时间段：</label>
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
				<th rowspan="2">物业名称</th>
				<th rowspan="2">租进业务员</th>
				<th rowspan="2">部长</th>
				<th rowspan="2">租进时间</th>
				<th rowspan="2">租出业务员</th>
				<th rowspan="2">租出时间</th>
				<th colspan="2">中介费</th>
				<c:forEach items="${showUserList}" var="user">
					<th rowspan="2">${user.name }</th>
				</c:forEach>
			</tr>
			<tr>
				<th>进</th>
				<th>出</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${resultList}" var="var">
			<tr>
				<td><a href="#" onclick="openWindow('${ctx}/finance/rent/form?id=${var.rentinmonth.rent.id}','查看[${var.rentinmonth.rent.house.name}]房屋包租明细')">${var.rentinmonth.rent.house.name}</a></td>
				<td><a href="#" onclick="openWindow('${ctx}/finance/stats/businessCutDetail4Person?personid=${var.rentinmonth.person.id}&rentout_sdate_begin=${paramMap.rentout_sdate_begin}&rentout_sdate_end=${paramMap.rentout_sdate_end}','查看[${var.rentinmonth.person.name}]个人业绩提成明细')">${var.rentinmonth.person.name }</a></td>
				<td><a href="#" onclick="openWindow('${ctx}/finance/stats/businessCutDetail4Person?personid=${var.rentinmonth.busi_departleader.id}&rentout_sdate_begin=${paramMap.rentout_sdate_begin}&rentout_sdate_end=${paramMap.rentout_sdate_end}','查看[${var.rentinmonth.busi_departleader.name}]个人业绩提成明细')">${var.rentinmonth.busi_departleader.name}</a></td>
				<td><fmt:formatDate value="${var.rentinmonth.sdate}" pattern="yyyy-MM-dd"/>-<br/><fmt:formatDate value="${var.rentinmonth.edate}" pattern="yyyy-MM-dd"/></td>
				<td><a href="#" onclick="openWindow('${ctx}/finance/stats/businessCutDetail4Person?personid=${var.rentmonth.person.id}&rentout_sdate_begin=${paramMap.rentout_sdate_begin}&rentout_sdate_end=${paramMap.rentout_sdate_end}','查看[${var.rentmonth.person.name}]个人业绩提成明细')">${var.rentmonth.person.name }</a></td>
				<td><fmt:formatDate value="${var.rentmonth.sdate}" pattern="yyyy-MM-dd"/>-<br/><fmt:formatDate value="${var.rentmonth.edate}" pattern="yyyy-MM-dd"/></td>
				<td>${var.rentinmonth.agencyfee}</td>
				<td>${var.rentmonth.agencyfee}</td>
				<c:forEach items="${var.resultUserCutList}" var="usercut">
					<td>${usercut}</td>
				</c:forEach>
			</tr> 
		</c:forEach>

		</tbody>
	</table>
	
</body>
</html>
