<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>房屋明细管理</title>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#searchForm").validate();
			$("#btnExport").click(function(){
				top.$.jBox.confirm("确认要导出房屋数据吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/finance/house/export");
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
			$("#searchForm").attr("action","${ctx}/finance/house/");
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
		

	</script>
</head>
<body>
	<div id="importBox" class="hide">
		<form id="importForm" action="${ctx}/finance/house/import" method="post" enctype="multipart/form-data"
			style="padding-left:20px;text-align:center;" class="form-search" onsubmit="loading('正在导入，请稍等...');"><br/>
			<input id="uploadFile" name="file" type="file" style="width:330px"/><br/><br/>　　
			<input id="btnImportSubmit" class="btn btn-primary" type="submit" value="   导    入   "/>
			<a href="${ctx}/finance/house/import/template">下载模板</a>
		</form>
	</div>
	
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/finance/house/">房屋明细列表</a></li>
		<shiro:hasPermission name="finance:house:add"><li><a href="${ctx}/finance/house/form">房屋明细添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="house" action="${ctx}/finance/house/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="showHighSearch" name="showHighSearch" type="hidden" value="${paramMap.showHighSearch}"/>
		<label>地址 ：</label><form:input path="name" htmlEscape="false" maxlength="50" class="input-small"/>
		
		&nbsp;<input id="btnSubmit" class="btn btn-primary" onclick="return page();" type="submit" value="查询"/>
		&nbsp;<input id="btnReset" class="btn btn-primary" onclick="resets()" type="button" value="重置"/>
		&nbsp;<input id="btnShow" class="btn btn-primary" onclick="showOrHidden()" type="button" value="高级查询"/>
		<shiro:hasPermission name="finance:house:view">
			&nbsp;<input id="btnExport" class="btn btn-primary" type="button" value="导出"/>
		</shiro:hasPermission>
		<shiro:hasPermission name="finance:house:edit">
			&nbsp;<input id="btnImport" class="btn btn-primary" type="button" value="导入"/>
		</shiro:hasPermission>
		<div id="pro_search" style="margin-top:10px;${'true' eq paramMap.showHighSearch?'':'display:none;'}">
			<div style="margin-bottom:5px;">
				<label>楼盘：</label><form:input path="houses" htmlEscape="false" maxlength="50" class="input-small"/>
				<label>区域：</label>				
				<form:select path="area" cssClass="input-small">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('house_area')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
				<label>房东姓名 ：</label><form:input path="landlord.name" htmlEscape="false" maxlength="50" class="input-small"/>
				<label>租户姓名：</label><form:input path="tenant.name" htmlEscape="false" maxlength="50" class="input-small"/>
				<label>房东电话 ：</label><form:input path="landlord.telephone" htmlEscape="false" maxlength="50" class="input-small"/>
				<label>租户电话：</label><form:input path="tenant.telephone" htmlEscape="false" maxlength="50" class="input-small"/>
				
			</div>
			<div style="margin-bottom:5px;">
					<label>房屋来源：</label>
					<form:select path="house_source" cssClass="input-small">
						<form:option value="" label="请选择"/>
						<form:options items="${fns:getDictList('finance_house_source')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
					</form:select>
					<label>是否可卖：</label>
					<form:select path="is_cansale" cssClass="input-small">
						<form:option value="" label="请选择"/>
						<form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
					</form:select>
					<label>是否未拿进房源：</label>
					<form:select path="is_norentin" cssClass="input-small">
						<form:option value="" label="请选择"/>
						<form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
					</form:select>
					<label>价格：</label>
					<input id="sale_price_min" name="sale_price_min" type="text" maxlength="10" class="input-small digits" value="${paramMap.sale_price_min}"/>
					至
					<input id="sale_price_max" name="sale_price_max" type="text" maxlength="10" class="input-small digits" value="${paramMap.sale_price_max}"/>
					
					
			</div>
			<div style="margin-bottom:5px;">
					<label>面积：</label>
					<input id="measure_min" name="measure_min" type="text" maxlength="10" class="input-small digits" value="${paramMap.measure_min}"/>
					至
					<input id="measure_max" name="measure_max" type="text" maxlength="10" class="input-small digits" value="${paramMap.measure_max}"/>
					
					<label>朝向：</label><form:input path="direction" htmlEscape="false" maxlength="50" class="input-small"/>
					<label>年代：</label><form:input path="age" htmlEscape="false" maxlength="50" class="input-small"/>
					<label>装修：</label><form:input path="decorate" htmlEscape="false" maxlength="50" class="input-small"/>
					<label>产权证号：</label><form:input path="prop_certno" htmlEscape="false" maxlength="50" class="input-small"/>
					
			</div>
			<div style="margin-bottom:5px;">
				<label>国土证号：</label><form:input path="land_certno" htmlEscape="false" maxlength="50" class="input-small"/>
				<label>家电家具：</label><form:input path="house_elec" htmlEscape="false" maxlength="50" class="input-small"/>
				<label>水：</label>
				<input id="water_num_min" name="water_num_min" type="text" maxlength="10" class="input-small digits" value="${paramMap.water_num_min}"/>
				至
				<input id="water_num_max" name="water_num_max" type="text" maxlength="10" class="input-small digits" value="${paramMap.water_num_max}"/>
				<label>电：</label>
				<input id="elec_num_min" name="elec_num_min" type="text" maxlength="10" class="input-small digits" value="${paramMap.elec_num_min}"/>
				至
				<input id="elec_num_max" name="elec_num_max" type="text" maxlength="10" class="input-small digits" value="${paramMap.elec_num_max}"/>
				
			</div>
			<div style="margin-bottom:5px;">
				<label>户型：</label><form:input path="house_layout" htmlEscape="false" maxlength="50" class="input-small"/>
				<label>物业用途：</label><form:input path="wy_useful" htmlEscape="false" maxlength="50" class="input-small"/>
				<label>付款方式：</label>
					<form:select path="paytype" cssClass="input-small">
						<form:option value="" label="请选择"/>
						<form:options items="${fns:getDictList('finance_rent_paytype')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
					</form:select>
				<label>结构：</label><form:input path="structure" htmlEscape="false" maxlength="50" class="input-small"/>
				<label>周边环境：</label><form:input path="arrond_environ" htmlEscape="false" maxlength="50" class="input-small"/>
			</div>
			<div style="margin-bottom:5px;">
				<label>房屋设施：</label><form:input path="housing_facilities" htmlEscape="false" maxlength="50" class="input-small"/>
				<label>面积描述：</label><form:input path="areadescribe" htmlEscape="false" maxlength="50" class="input-small"/>
				<label>交通情况：</label><form:input path="traffic_condition" htmlEscape="false" maxlength="50" class="input-small"/>
				<label>委托门店：</label><form:input path="entrust_store" htmlEscape="false" maxlength="50" class="input-small"/>
				<label>登记门店：</label><form:input path="regist_store" htmlEscape="false" maxlength="50" class="input-small"/>
			</div>
			<div style="margin-bottom:5px;">
				<label>租进业务员：</label>
				<tags:treeselect id="rentin_userid" name="rentin_userid" notAllowSelectParent="true" cssClass="input-small" value="${paramMap.rentin_user.id}" labelName="rentin_user.name" labelValue="${paramMap.rentin_user.name}"
					title="人员" url="/sys/user/treeData" />
				<label>租出业务员：</label>
				<tags:treeselect id="rentout_userid" name="rentout_userid" notAllowSelectParent="true" cssClass="input-small" value="${paramMap.rentout_user.id}" labelName="rentout_user.name" labelValue="${paramMap.rentout_user.name}"
					title="人员" url="/sys/user/treeData" />
				<label>收款户名：</label><form:input path="receive_username" htmlEscape="false" maxlength="50" class="input-small"/>
				<label>是否可租：</label>
					<form:select path="is_canrent" cssClass="input-small">
						<form:option value="" label="请选择"/>
						<form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
					</form:select>
			</div>
			
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr>
			<th>地址</th>
			<th>楼盘</th>
			<th>房东姓名</th>
			<th>房东联系方式</th>
			
			<th>租户姓名</th>
			<th>租户联系方式</th>
			<th>租进业务员</th>
			<th>租出业务员</th>
			<th>租进时间&nbsp;&nbsp;&nbsp;</th>
			<th>租出时间&nbsp;&nbsp;&nbsp;</th>
			<th>创建者</th>
			<th>备注</th>
			<shiro:hasPermission name="finance:house:edit"><th>操作</th></shiro:hasPermission></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="house">
			<tr>
				<td><a href="${ctx}/finance/house/form?id=${house.id}">${house.name}</a></td>
				<td>${house.houses}</td>
				<td>${allColumnShow or empty house.rentin_user or house.rentin_user eq fns:getUser()?house.landlord.name:''}</td>
				<td>${allColumnShow or empty house.rentin_user or house.rentin_user eq fns:getUser()?house.landlord.telephone:''}</td>
				
				<td>${allColumnShow or empty house.rentout_user or house.rentout_user eq fns:getUser()?house.tenant.name:''}</td>
				<td>${allColumnShow or empty house.rentout_user or house.rentout_user eq fns:getUser()?house.tenant.telephone:''}</td>
				<td>${house.rentin_user.name}</td>
				<td>${house.rentout_user.name}</td>
				<td><fmt:formatDate value="${house.rent.rentinMonths[0].sdate}" pattern="yyyy-MM-dd"/>-<br/><fmt:formatDate value="${house.rent.rentinMonths[0].edate}" pattern="yyyy-MM-dd"/></td>
				<td><fmt:formatDate value="${house.rent.rentoutMonths[0].sdate}" pattern="yyyy-MM-dd"/>-<br/><fmt:formatDate value="${house.rent.rentoutMonths[0].edate}" pattern="yyyy-MM-dd"/></td>
				<td>${house.createBy.name}</td>
				<td>${house.remarks}</td>
				<td>
					<shiro:hasPermission name="finance:house:edit">
    				<a href="${ctx}/finance/house/form?id=${house.id}">修改</a>
    				</shiro:hasPermission>
    				<shiro:hasPermission name="finance:house:delete">
					<a href="${ctx}/finance/house/delete?id=${house.id}" onclick="return confirmx('确认要删除该房屋明细吗？', this.href)">删除</a>
					</shiro:hasPermission>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
