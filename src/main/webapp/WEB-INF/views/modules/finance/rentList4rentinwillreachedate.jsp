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
			$("#searchForm").validate();
			$.initSortTable("contentTable",page);
			
			$("#btnExport").click(function(){
				exportExcel4base();
			});

		});
		
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").attr("action","${ctx}/finance/rent/rentList");
			$("#searchForm").submit();
        	return false;
        }
		function resets(){
			$("input[type=text],input[type=hidden],select","#searchForm").not("#pageNo,#pageSize").val("");
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
			top.$.jBox.open("iframe:"+url, "查看["+personname+"]房屋明细",$(top.document).width()-220,$(top.document).height()-80,{
				buttons:{"确定":true}, loaded:function(h){
					$(".jbox-content", top.document).css("overflow-y","hidden");
					$(".jbox", top.document).css("top","10px");
				}
			});
		}
		
		
	</script>
</head>
<body>

	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/finance/rent/">包租明细【租进房租即将到期】列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="rent" action="${ctx}/finance/rent/rentList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<input id="notcancelrentonly" name="notcancelrentonly" type="hidden" value="${paramMap.notcancelrentonly}"/>
		<input id="viewtype" name="viewtype" type="hidden" value="${paramMap.viewtype}"/>
		<input id="order" name="order" type="hidden" value="${paramMap.order}"/>
		<input id="showHighSearch" name="showHighSearch" type="hidden" value="${paramMap.showHighSearch}"/>
		<div><label>房屋地址：</label>
		<form:input path="name" htmlEscape="false" maxlength="50" class="input-small" value="${paramMap.name}"/>
		<label>编号：</label>
		<input name="business_num" maxlength="50" type="text" class="input-small digits" value="${paramMap.business_num}"/>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" onclick="return page();" type="submit" value="查询"/>
		&nbsp;<input id="btnReset" class="btn btn-primary" onclick="resets()" type="button" value="重置"/>
		&nbsp;<input id="btnShow" class="btn btn-primary" onclick="showOrHidden()" type="button" value="高级查询"/>	
		</div>
		<div id="pro_search" style="margin-top:10px;${'true' eq paramMap.showHighSearch?'':'display:none;'}">
		<div style="margin-bottom:5px;">
			<label>承租租金：</label>
			<input id="rentin_rentmonthmin" name="rentin_rentmonthmin" type="text" maxlength="10" class="input-small digits" value="${paramMap.rentin_rentmonthmin}"/>
			至
			<input id="rentin_rentmonthmax" name="rentin_rentmonthmax" type="text" maxlength="10" class="input-small digits" value="${paramMap.rentin_rentmonthmax}"/>
			<label>出租租金：</label>
			<input id="rentout_rentmonthmin" name="rentout_rentmonthmin" type="text" maxlength="10" class="input-small digits" value="${paramMap.rentout_rentmonthmin}"/>
			至
			<input id="rentout_rentmonthmax" name="rentout_rentmonthmax" type="text" maxlength="10" class="input-small digits" value="${paramMap.rentout_rentmonthmax}"/>
			
				
		</div>
		<div style="margin-bottom:5px;">
			<label>承租开始时间：</label>
			<input id="rentin_sdatesdate" name="rentin_sdatesdate" type="text"  maxlength="20" class="input-small Wdate"
					value="${paramMap.rentin_sdatesdate}" onclick="WdatePicker({maxDate:'#F{$dp.$D(\'rentin_sdateedate\')}',dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			至
			<input id="rentin_sdateedate" name="rentin_sdateedate" type="text"  maxlength="20" class="input-small Wdate"
					value="${paramMap.rentin_sdateedate}" onclick="WdatePicker({minDate:'#F{$dp.$D(\'rentin_sdatesdate\')}',dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			<label>承租结束时间：</label>
					<input id="rentin_edatesdate" name="rentin_edatesdate" type="text"  maxlength="20" class="input-small Wdate"
					value="${paramMap.rentin_edatesdate}" onclick="WdatePicker({maxDate:'#F{$dp.$D(\'rentin_edateedate\')}',dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			至
			<input id="rentin_edateedate" name="rentin_edateedate" type="text"  maxlength="20" class="input-small Wdate"
					value="${paramMap.rentin_edateedate}" onclick="WdatePicker({minDate:'#F{$dp.$D(\'rentin_edatesdate\')}',dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
		</div>
		<div style="margin-bottom:5px;">
			<label>出租开始时间：</label>
			<input id="rentout_sdatesdate" name="rentout_sdatesdate" type="text"  maxlength="20" class="input-small Wdate"
					value="${paramMap.rentout_sdatesdate}" onclick="WdatePicker({maxDate:'#F{$dp.$D(\'rentout_sdateedate\')}',dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			至
			<input id="rentout_sdateedate" name="rentout_sdateedate" type="text"  maxlength="20" class="input-small Wdate"
					value="${paramMap.rentout_sdateedate}" onclick="WdatePicker({minDate:'#F{$dp.$D(\'rentout_sdatesdate\')}',dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			<label>出租结束时间：</label>
					<input id="rentout_edatesdate" name="rentout_edatesdate" type="text"  maxlength="20" class="input-small Wdate"
					value="${paramMap.rentout_edatesdate}" onclick="WdatePicker({maxDate:'#F{$dp.$D(\'rentout_edateedate\')}',dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			至
			<input id="rentout_edateedate" name="rentout_edateedate" type="text"  maxlength="20" class="input-small Wdate"
					value="${paramMap.rentout_edateedate}" onclick="WdatePicker({minDate:'#F{$dp.$D(\'rentout_edatesdate\')}',dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
		</div>
		<div>
			<label>承租下次付租时间：</label>
			<input id="rentin_nextpaysdate" name="rentin_nextpaysdate" type="text"  maxlength="20" class="input-small Wdate"
					value="${paramMap.rentin_nextpaysdate}" onclick="WdatePicker({maxDate:'#F{$dp.$D(\'rentin_nextpayedate\')}',dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			至
			<input id="rentin_nextpayedate" name="rentin_nextpayedate" type="text"  maxlength="20" class="input-small Wdate"
					value="${paramMap.rentin_nextpayedate}" onclick="WdatePicker({minDate:'#F{$dp.$D(\'rentin_nextpaysdate\')}',dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			<label>出租下次收租时间：</label>
					<input id="rentout_nextpaysdate" name="rentout_nextpaysdate" type="text"  maxlength="20" class="input-small Wdate"
					value="${paramMap.rentout_nextpaysdate}" onclick="WdatePicker({maxDate:'#F{$dp.$D(\'rentout_nextpayedate\')}',dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			至
			<input id="rentout_nextpayedate" name="rentout_nextpayedate" type="text"  maxlength="20" class="input-small Wdate"
					value="${paramMap.rentout_nextpayedate}" onclick="WdatePicker({minDate:'#F{$dp.$D(\'rentout_nextpaysdate\')}',dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
		</div> 
		<div>
			<label>承租业务员：</label>
				<tags:treeselect id="rentinperson_id" name="rentinperson_id" notAllowSelectParent="true" cssClass="input-small" value="${paramMap.rentinperson.id}" labelName="rentinperson.name" labelValue="${paramMap.rentinperson.name}"
					title="人员" url="/sys/user/treeData" />
			<label>承租组长：</label>
				<tags:treeselect id="rentin_teamleader_id" name="rentin_teamleader_id" notAllowSelectParent="true" cssClass="input-small" value="${paramMap.rentin_teamleader.id}" labelName="rentin_teamleader.name" labelValue="${paramMap.rentin_teamleader.name}"
					title="人员" url="/sys/user/treeData" />
			<label>出租部长：</label>
				<tags:treeselect id="rentin_departleader_id" name="rentin_departleader_id" notAllowSelectParent="true" cssClass="input-small" value="${paramMap.rentin_departleader.id}" labelName="rentin_departleader.name" labelValue="${paramMap.rentin_departleader.name}"
					title="人员" url="/sys/user/treeData" />
		</div>
		<div>
			<label>出租业务员：</label>
				<tags:treeselect id="rentoutperson_id" name="rentoutperson_id" notAllowSelectParent="true" cssClass="input-small" value="${paramMap.rentoutperson.id}" labelName="rentoutperson.name" labelValue="${paramMap.rentoutperson.name}"
					title="人员" url="/sys/user/treeData" />
			<label>出租组长：</label>
				<tags:treeselect id="rentout_teamleader_id" name="rentout_teamleader_id" notAllowSelectParent="true" cssClass="input-small" value="${paramMap.rentout_teamleader.id}" labelName="rentout_teamleader.name" labelValue="${paramMap.rentout_teamleader.name}"
					title="人员" url="/sys/user/treeData" />
			<label>出租部长：</label>
				<tags:treeselect id="rentout_departleader_id" name="rentout_departleader_id" notAllowSelectParent="true" cssClass="input-small" value="${paramMap.rentout_departleader.id}" labelName="rentout_departleader.name" labelValue="${paramMap.rentout_departleader.name}"
					title="人员" url="/sys/user/treeData" />
		</div>
		<div>
			<label>承租付款方式：</label> 
				<select id="rentin_paytype" name="rentin_paytype" class="input-small" >
					<option value="" label="请选择"/>
					<c:forEach items="${fns:getDictList('finance_rent_paytype')}" var="dict">
						<c:choose >
							<c:when test="${dict.value == paramMap.rentin_paytype }">
								<option value="${dict.value}" selected="selected" label="">${dict.label}</option>
							</c:when>
							<c:otherwise>
								<option value="${dict.value}" label="">${dict.label}</option>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</select>
			<label>出租付款方式：</label>
				<select id="rentout_paytype" name="rentout_paytype" class="input-small" >
					<option value="" label="请选择"/>
					<c:forEach items="${fns:getDictList('finance_rent_paytype')}" var="dict">
						<c:choose >
							<c:when test="${dict.value == paramMap.rentout_paytype }">
								<option value="${dict.value}" selected="selected" label="">${dict.label}</option>
							</c:when>
							<c:otherwise>
								<option value="${dict.value}" label="">${dict.label}</option>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</select>
		</div>
		<div style="margin-bottom:5px;">
			<label>承租备注：</label>
			<input id="rentin_remark" name="rentin_remark" type="text" maxlength="10" class="input-medium" value="${paramMap.rentin_remark}"/>
			<label>出租备注：</label>
			<input id="rentout_remark" name="rentout_remark" type="text" maxlength="10" class="input-medium" value="${paramMap.rentout_remark}"/>
			<label>提前退租备注：</label>
			<input id="rentout_cancelrentremark" name="rentout_cancelrentremark" type="text" maxlength="10" class="input-medium" value="${paramMap.rentout_cancelrentremark}"/>
		</div>
		<div>
			<label>出租提前退租时间：</label>
			<input id="rentout_cancelrentsdate" name="rentout_cancelrentsdate" type="text"  maxlength="20" class="input-small Wdate"
					value="${paramMap.rentout_cancelrentsdate}" onclick="WdatePicker({maxDate:'#F{$dp.$D(\'rentout_cancelrentedate\')}',dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			至
			<input id="rentout_cancelrentedate" name="rentout_cancelrentedate" type="text"  maxlength="20" class="input-small Wdate"
					value="${paramMap.rentout_cancelrentedate}" onclick="WdatePicker({minDate:'#F{$dp.$D(\'rentout_cancelrentsdate\')}',dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			<label>是否租客转租</label>
			<select id="is_terentrentout" name="is_terentrentout" class="input-small" >
					<option value="" label="请选择"/>
					<c:forEach items="${fns:getDictList('yes_no')}" var="dict">
						<c:choose >
							<c:when test="${dict.value == paramMap.is_terentrentout }">
								<option value="${dict.value}" selected="selected" label="">${dict.label}</option>
							</c:when>
							<c:otherwise>
								<option value="${dict.value}" label="">${dict.label}</option>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</select>
			<label>是否未租出房屋</label>
			<select id="is_norentout" name="is_norentout" class="input-small" >
					<option value="" label="请选择"/>
					<c:forEach items="${fns:getDictList('yes_no')}" var="dict">
						<c:choose >
							<c:when test="${dict.value == paramMap.is_norentout }">
								<option value="${dict.value}" selected="selected" label="">${dict.label}</option>
							</c:when>
							<c:otherwise>
								<option value="${dict.value}" label="">${dict.label}</option>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</select>
		</div>
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr>
			<th>编号</th>
			<th>房屋地址</th>
			<th>承租时间</th>
			<th>承租月租金</th>
			<th>出租时间</th>
			<th>租进业务员</th>
			<th>房东姓名</th>
			<th>房东电话</th>
		</tr>
		
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="rent">
			<tr>
				<td>${rent.business_num}</td>
				<td><a href="#" onclick="openWindow('${ctx}/finance/house/form?id=${rent.house.id}','${rent.house.name}')" title="${rent.house.name}">${fns:abbr(fns:replaceHtml(rent.house.name),25)}</a></td>
				<td><fmt:formatDate value="${rent.rentinMonths[0].sdate}" pattern="yyyy-MM-dd"/>-<br/><fmt:formatDate value="${rent.rentinMonths[0].edate}" pattern="yyyy-MM-dd"/></td>
				<td>${rent.rentinMonths[0].rentmonth}</td>
				<td><fmt:formatDate value="${rent.rentoutMonths[0].sdate}" pattern="yyyy-MM-dd"/>-<br/><fmt:formatDate value="${rent.rentoutMonths[0].edate}" pattern="yyyy-MM-dd"/></td>
				<td>${rent.rentinMonths[0].person.name}</td>
				<td>${rent.house.landlord.name}</td>
				<td>${rent.house.landlord.telephone}</td>
			</tr>
		</c:forEach>		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
