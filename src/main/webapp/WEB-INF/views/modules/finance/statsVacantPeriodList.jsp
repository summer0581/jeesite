<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>空置期提成管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#periodtotaltab").click(function(){
				$(this).attr("href","${ctx}/finance/stats/vacantPeriod4Person?rentout_sdate_begin="+$("#rentout_sdate_begin").val()+"&rentout_sdate_end="+$("#rentout_sdate_end").val());
			})
			$("#btnExport").click(function(){
				top.$.jBox.confirm("确认要导出房屋包租空置期统计数据吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/finance/stats/export/vacantPeriod");
						$("#searchForm").submit();
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
		});
		function page(n,s){
			$("#searchForm").attr("action","${ctx}/finance/stats/vacantPeriod");
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
					$(".jbox", top.document).css("top","10px");
				}
			});
		}
	</script>
</head>
<body>
	<!-- Modal -->
	<div id="remarkModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	  <div class="modal-header">
	    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
	    <h3 id="myModalLabel">空置期计算说明</h3>
	  </div>
	  <div class="modal-body">
	    <h4>
	    <span style="color:red;">注意一:</span>要计算空置期的包租月记录，必须设置承租和出租的【头期】这个值，否则系统无法识别为出租第一个月。
		<br>
		<br>
	    <span style="color:red;">注意二:</span>头期设置为1的包租月记录，计算空置期时，必须要设置好租进月记录；<br>
		头期不为1的，也就是后面续租或者重新出租的月记录，必须要设置好上一次租出记录，否则查不出空置期</h4> 
	  </div>
	  <div class="modal-footer">
	    <button class="btn" data-dismiss="modal" aria-hidden="true">关闭</button>
	  </div>
	</div>

	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/finance/stats/vacantPeriod">空置期提成</a></li>
		<li ><a href="#" id="periodtotaltab">个人总计</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="vacantPeriod" action="${ctx}/finance/stats/vacantPeriod" method="post" class="breadcrumb form-search">
	<input id="orderBy" name="orderBy" type="hidden" value="${paramMap.orderBy}"/>
	<input id="showHighSearch" name="showHighSearch" type="hidden" value="${paramMap.showHighSearch}"/>
	<div><label>房屋地址：</label><input name="name" type="text" maxlength="50" class="input-small" value="${paramMap.name}"/>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" onclick="return page();" type="submit" value="查询"/>
		&nbsp;<input id="btnReset" class="btn btn-primary" onclick="resets()" type="button" value="重置"/>
		&nbsp;<input id="btnShow" class="btn btn-primary" onclick="showOrHidden()" type="button" value="高级查询"/>
		&nbsp;<input id="btnExport" class="btn btn-primary" type="button" value="导出"/>
		<a href="#remarkModal" role="button"  data-toggle="modal"><span class="badge badge-warning">空置期说明</span></a>
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
				<th>编号</th>
				<th>地址</th>
				<th>租进业务员</th>
				<th>承租情况</th>
				<th>出租情况</th>
				<th>租金</th>
				<th>租出业务员</th>
				<th>组长</th>
				<th>部长</th>
				<th>空置期天数设置</th>
				<th>空置期天数</th>
				<th>租进业务员提成</th>
				<th>租出业务员提成</th>
				<th>组长提成</th>
				<th>部长提成</th>
				<th>经理提成</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${list}" var="var">
			<tr>
				<td>${var.rentinmonth.rent.business_num }</td>
				<td><a href="#" onclick="openWindow('${ctx}/finance/rent/form?id=${var.rentinmonth.rent.id}','查看[${var.rentinmonth.rent.house.name}]房屋包租明细')">${var.rentinmonth.rent.house.name}</a></td>
				<td><a href="#" onclick="openWindow('${ctx}/finance/stats/vacantPeriodDetail4Person?personid=${var.rentinmonth.person.id}&rentout_sdate_begin=${paramMap.rentout_sdate_begin}&rentout_sdate_end=${paramMap.rentout_sdate_end}','查看[${var.rentinmonth.person.name}]个人空置期明细')">${var.rentinmonth.person.name }</a></td>
				<td><fmt:formatDate value="${var.rentinmonth.sdate}" pattern="yyyy-MM-dd"/>-<br/><fmt:formatDate value="${var.rentinmonth.edate}" pattern="yyyy-MM-dd"/></td>
				<td><fmt:formatDate value="${var.rentmonth.sdate}" pattern="yyyy-MM-dd"/>-<br/><fmt:formatDate value="${var.rentmonth.edate}" pattern="yyyy-MM-dd"/></td>
				<td>${var.rentmonth.rentmonth }</td>
				<td><a href="#" onclick="openWindow('${ctx}/finance/stats/vacantPeriodDetail4Person?personid=${var.rentmonth.person.id}&rentout_sdate_begin=${paramMap.rentout_sdate_begin}&rentout_sdate_end=${paramMap.rentout_sdate_end}','查看[${var.rentmonth.person.name}]个人空置期明细')">${var.rentmonth.person.name }</a></td>
				<td><a href="#" onclick="openWindow('${ctx}/finance/stats/vacantPeriodDetail4Person?personid=${var.rentinmonth.busi_teamleader.id}&rentout_sdate_begin=${paramMap.rentout_sdate_begin}&rentout_sdate_end=${paramMap.rentout_sdate_end}','查看[${var.rentinmonth.busi_teamleader.name}]个人空置期明细')">${var.rentinmonth.busi_teamleader.name }</a></td>
				<td><a href="#" onclick="openWindow('${ctx}/finance/stats/vacantPeriodDetail4Person?personid=${var.rentinmonth.busi_departleader.id}&rentout_sdate_begin=${paramMap.rentout_sdate_begin}&rentout_sdate_end=${paramMap.rentout_sdate_end}','查看[${var.rentinmonth.busi_departleader.name}]个人空置期明细')">${var.rentinmonth.busi_departleader.name }</a></td>
				<td>${var.vacantperiodconfig}</td>
				<td>${var.vacantperiod }</td>
				<td>${var.rentin_cut }</td>
				<td>${var.rentout_cut }</td>
				<td>${var.teamleader_cut }</td>
				<td>${var.departleader_cut }</td>
				<td>${var.manager_cut }</td>
			</tr>
		</c:forEach>
		<tr>
			<td colspan="11" class="total_td">合计</td>
			<td>${total.rentin_cut_total }</td>
			<td>${total.rentout_cut_total }</td>
			<td>${total.teamleader_cut_total }</td>
			<td>${total.departleader_cut_total }</td>
			<td>${total.manager_cut_total }</td>
		</tr>
		</tbody>
	</table>
	
</body>
</html>
