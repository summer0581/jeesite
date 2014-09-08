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
			$("#btnExport").click(function(){
				top.$.jBox.confirm("确认要导出房屋包租业绩提成统计数据吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/finance/stats/export/businessCut");
						$("#searchForm").submit();
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});

		});
		function page(n,s){
			$("#searchForm").attr("action","${ctx}/finance/stats/businessCut");
			$("#searchForm").submit();
        	return false;
        }
		
		function resets(){
			$("input[type=text]","#searchForm").val("");
			page();
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
		&nbsp;<input id="btnSubmit" class="btn btn-primary" onclick="return page();" type="submit" value="查询"/>
		&nbsp;<input id="btnReset" class="btn btn-primary" onclick="resets()" type="button" value="重置"/>
		&nbsp;<input id="btnShow" class="btn btn-primary" onclick="showOrHidden()" type="button" value="高级查询"/>
		&nbsp;<input id="btnExport" class="btn btn-primary" type="button" value="导出"/>
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
		<table id="contentTable" class="table table-striped table-bordered table-condensed"style="overflow-x:auto;overflow-y:hidden;">
			<thead>
				<tr>
					<th rowspan="2">编号</th>
					<th rowspan="2">物业名称</th>
					<th rowspan="2">租进业务员</th>
					<th rowspan="2">组长</th>
					<th rowspan="2">部长</th>
					<th rowspan="2">租进时间</th>
					<th rowspan="2">租出业务员</th>
					<th rowspan="2">租出时间</th>
					<th colspan="2">中介费</th>
					<th rowspan="2">租进业务员提成</th>
					<th rowspan="2">租出业务员提成</th>
					<th rowspan="2">组长提成</th>
					<th rowspan="2">部长提成</th>
					<th rowspan="2">经理提成</th>
				</tr>
				<tr>
					<th>进</th>
					<th>出</th>
				</tr>
			</thead>
			<tbody>
			<c:forEach items="${resultList}" var="var">
				<tr>
					<td>${var.rentinmonth.rent.business_num}</td>
					<td><a href="#" onclick="openWindow('${ctx}/finance/rent/form?id=${var.rentinmonth.rent.id}','查看[${var.rentinmonth.rent.house.name}]房屋包租明细')">${var.rentinmonth.rent.house.name}</a></td>
					<td><a href="#" onclick="openWindow('${ctx}/finance/stats/businessCutDetail4Person?personid=${var.rentinmonth.person.id}&rentout_sdate_begin=${paramMap.rentout_sdate_begin}&rentout_sdate_end=${paramMap.rentout_sdate_end}','查看[${var.rentinmonth.person.name}]个人业绩提成明细')">${var.rentinmonth.person.name }</a></td>
					<td><a href="#" onclick="openWindow('${ctx}/finance/stats/businessCutDetail4Person?personid=${var.rentinmonth.busi_teamleader.id}&rentout_sdate_begin=${paramMap.rentout_sdate_begin}&rentout_sdate_end=${paramMap.rentout_sdate_end}','查看[${var.rentinmonth.busi_teamleader.name}]个人业绩提成明细')">${var.rentinmonth.busi_teamleader.name}</a></td>
					<td><a href="#" onclick="openWindow('${ctx}/finance/stats/businessCutDetail4Person?personid=${var.rentinmonth.busi_departleader.id}&rentout_sdate_begin=${paramMap.rentout_sdate_begin}&rentout_sdate_end=${paramMap.rentout_sdate_end}','查看[${var.rentinmonth.busi_departleader.name}]个人业绩提成明细')">${var.rentinmonth.busi_departleader.name}</a></td>
					<td><fmt:formatDate value="${var.rentinmonth.sdate}" pattern="yyyy-MM-dd"/>-<br/><fmt:formatDate value="${var.rentinmonth.edate}" pattern="yyyy-MM-dd"/></td>
					<td><a href="#" onclick="openWindow('${ctx}/finance/stats/businessCutDetail4Person?personid=${var.rentoutmonth.person.id}&rentout_sdate_begin=${paramMap.rentout_sdate_begin}&rentout_sdate_end=${paramMap.rentout_sdate_end}','查看[${var.rentoutmonth.person.name}]个人业绩提成明细')">${var.rentoutmonth.person.name }</a></td>
					<td><fmt:formatDate value="${var.rentoutmonth.sdate}" pattern="yyyy-MM-dd"/>-<br/><fmt:formatDate value="${var.rentoutmonth.edate}" pattern="yyyy-MM-dd"/></td>
					<td>${var.rentinmonth.agencyfee}</td>
					<td>${var.rentoutmonth.agencyfee}</td>
					<td>${var.rentin_cut}</td>
					<td>${var.rentout_cut}</td>
					<td>${var.teamleader_cut}</td>
					<td>${var.departleader_cut}</td>
					<td>${var.manager_cut}</td>
				</tr> 
			</c:forEach>
			<tr>
				<td colspan="10">合计</td>
				<td>${total.rentin_cut_total}</td>
				<td>${total.rentout_cut_total}</td>
				<td>${total.teamleader_cut_total}</td>
				<td>${total.departleader_cut_total}</td>
				<td>${total.manager_cut_total}</td>
			</tr>
			
	
			</tbody>
		</table>

</body>
</html>
