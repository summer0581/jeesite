<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>空置期个人提成</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#vacantPeriodTab").click(function(){
				$(this).attr("href","${ctx}/finance/stats/vacantPeriod?rentout_sdate_begin="+$("#rentout_sdate_begin").val()+"&rentout_sdate_end="+$("#rentout_sdate_end").val());
			})
			$("#btnExport").click(function(){
				top.$.jBox.confirm("确认要导出房屋包租空置期(个人总计）数据吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/finance/stats/export/vacantPeriod4Person");
						$("#searchForm").submit();
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});

		});
		function page(n,s){
			$("#searchForm").attr("action","${ctx}/finance/stats/vacantPeriod4Person");
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
		function openWindow(url,personname){
			top.$.jBox.open("iframe:"+url, "查看["+personname+"]个人空置期明细",$(top.document).width()-220,$(top.document).height()-80,{
				buttons:{"确定":true}, loaded:function(h){
					$(".jbox-content", top.document).css("overflow-y","hidden");
				}
			});
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li ><a href="#" id=vacantPeriodTab>空置期提成</a></li>
		<li class="active"><a href="${ctx}/finance/stats/vacantPeriod4Person">个人合计</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="vacantPeriod" action="${ctx}/finance/stats/vacantPeriod4Person" method="post" class="breadcrumb form-search">
	<input id="orderBy" name="orderBy" type="hidden" value="${paramMap.orderBy}"/>
	<input id="showHighSearch" name="showHighSearch" type="hidden" value="${paramMap.showHighSearch}"/>
	<div><label>房屋地址：</label><input name="name" type="text" maxlength="50" class="input-small" value="${paramMap.name}"/>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" onclick="return page();" type="submit" value="查询"/>
		&nbsp;<input id="btnReset" class="btn btn-primary" onclick="resets()" type="button" value="重置"/>
		&nbsp;<input id="btnShow" class="btn btn-primary" onclick="showOrHidden()" type="button" value="高级查询"/>
		&nbsp;<input id="btnExport" class="btn btn-primary" type="button" value="导出"/>
	</div>
	<div id="pro_search" style="margin-top:10px;${'true' eq paramMap.showHighSearch?'':'display:none;'}">
		<label>空置期时间段：</label>
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
				<th>业务员</th>
				<th>租进提成</th>
				<th>租出提成</th>
				<th>合计</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${list}" var="var">
			<tr>
				<td><a href="#" onclick="openWindow('${ctx}/finance/stats/vacantPeriodDetail4Person?personid=${var.person.id}&rentout_sdate_begin=${paramMap.rentout_sdate_begin}&rentout_sdate_end=${paramMap.rentout_sdate_end}','${var.person.name}')">${var.person.name}</a></td>
				<td>${var.rentinPeriodTotal }</td>
				<td>${var.rentoutPeriodTotal }</td>
				<td>${var.periodTotal }</td>
			</tr>
		</c:forEach>
		<tr>
			<td>合计</td>
			<td>${total.rentin_cut_total }</td>
			<td>${total.rentout_cut_total }</td>
			<td>${total.cut_total }</td>
		</tr>
		</tbody>
	</table>
	
</body>
</html>
