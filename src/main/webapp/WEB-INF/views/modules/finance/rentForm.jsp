<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>包租明细管理</title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.tabiframe{width:100%; height:670px; border:0px;}
	</style>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
			if("" == $("#id").val()){
				$("#myTab li").eq(0).removeClass("active").hide();
				$("#myTab li").eq(1).hide();
				$("#myTab li").eq(2).hide();
				$("#myTab li").eq(3).addClass("active")
				$("#rentin").removeClass("in active")
				$("#remark").addClass("in active");
			}
			$("select[name^='salesman_vacantperiods']").find("option:eq(1)").remove();
			//alert($("select[name='salesman_vacantperiods[4].type'] option[text='房东指定的空置期']").text())
			
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/finance/rent/rentList">包租明细列表</a></li>
		<li class="active"><a href="${ctx}/finance/rent/form?id=${rent.id}">包租明细<shiro:hasPermission name="finance:rent:edit">${not empty rent.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="finance:rent:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="rent" action="${ctx}/finance/rent/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		
		<tags:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">房屋名称:</label>
			<div class="controls">
				<form:hidden id="houseid" path="house.id" htmlEscape="false" maxlength="64" class="input-xlarge"/>
				<form:input id="name" path="name" htmlEscape="false" maxlength="64" class="input-xlarge" readonly="true"/>
				<a id="relationButton" href="javascript:" class="btn">选择房屋</a>
				<shiro:hasPermission name="finance:rent:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
				<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
				<script type="text/javascript">
					var houseSelect = [];
					function houseSelectAddOrDel(id,title){
						houseSelect = []
						houseSelect.push([id,title]);
						houseSelectRefresh();
					}
					function houseSelectRefresh(){
						$("#houseid").val("");
						$("#name").val("");
						if(houseSelect.length > 0){
							$("#name").val(houseSelect[houseSelect.length-1][1]);
							$("#houseid").val(houseSelect[houseSelect.length-1][0]);
						}
					}
					$("#relationButton").click(function(){
						top.$.jBox.open("iframe:${ctx}/finance/house/selectList?pageSize=8", "添加房屋",$(top.document).width()-220,$(top.document).height()-180,{
							buttons:{"确定":true}, loaded:function(h){
								$(".jbox-content", top.document).css("overflow-y","hidden");
							}
						});
					});
				</script>
			</div>
		</div>
		    <div class="control-group">
				<label class="control-label">编号:</label>
				<div class="controls">
					<form:input path="business_num" htmlEscape="false" rows="4" maxlength="200" class="input-xxlarge"/>
				</div>
			</div>
		
		<div id="rentMonthTab">
		<hr />
		<ul id="myTab" class="nav nav-tabs">
              <li class="active"><a href="#rentin" data-toggle="tab">承租月明细</a></li>
              <li><a href="#rentout" data-toggle="tab">出租月明细</a></li>
              <li><a href="#vacantperiod" data-toggle="tab">空置期设置</a></li>
              <li><a href="#remark" data-toggle="tab">备注</a></li>
            </ul>
            <div id="myTabContent" class="tab-content">
              <div class="tab-pane fade in active" id="rentin">
					<iframe src="${ctx}/finance/rentMonth/?infotype=rentin&rent.id=${rent.id}" class="tabiframe"></iframe>
              </div>
              <div class="tab-pane fade" id="rentout">
		           <iframe src="${ctx}/finance/rentMonth/?infotype=rentout&rent.id=${rent.id}" class="tabiframe"></iframe>
              </div>
			<div class="tab-pane fade" id="vacantperiod"> 		
				<ul id="myTab1" class="nav nav-tabs">
		              <li class="active"><a href="#home1" data-toggle="tab">房东空置期设置</a></li>
		              <li><a href="#profile1" data-toggle="tab">业务员控制期设置</a></li>
		            </ul>
		            <div id="myTabContent1" class="tab-content">
		              <div class="tab-pane fade in active" id="home1">
		
									<table class="table"> 
									 	<tr>
									 		<th>时段一：</th>
									 		<td>
									 		<input type="hidden" name="landlord_vacantperiods[0].id" value="${empty rent.landlord_vacantperiods?'':rent.landlord_vacantperiods[0].id }"/>
									 		<input type="hidden" name="landlord_vacantperiods[0].sn" value="2"/>
									 		<input id="vacantPeriod_sdate1" name="landlord_vacantperiods[0].sdate" type="text"  maxlength="20" class="input-medium Wdate"
										value="<fmt:formatDate value="${empty rent.landlord_vacantperiods?'':rent.landlord_vacantperiods[0].sdate}" pattern="yyyy-MM-dd"/>"
										onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/> 
									至
									<input id="vacantPeriod_edate1" name="landlord_vacantperiods[0].edate" type="text"  maxlength="20" class="input-medium Wdate"
										value="<fmt:formatDate value="${empty rent.landlord_vacantperiods?'':rent.landlord_vacantperiods[0].edate}" pattern="yyyy-MM-dd"/>"
										onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/></td>
										<td>
											<input type="hidden" name="landlord_vacantperiods[0].type" value="2"/>
										</td>
									 	</tr>
									 	<tr>
									 		<th>时段二：</th>
									 		<td>
									 		<input type="hidden" name="landlord_vacantperiods[1].id" value="${empty rent.landlord_vacantperiods?'':rent.landlord_vacantperiods[1].id }"/>
									 		<input type="hidden" name="landlord_vacantperiods[1].sn" value="2"/>
									 		<input id="vacantPeriod_sdate2" name="landlord_vacantperiods[1].sdate" type="text"  maxlength="20" class="input-medium Wdate"
										value="<fmt:formatDate value="${empty rent.landlord_vacantperiods?'':rent.landlord_vacantperiods[1].sdate}" pattern="yyyy-MM-dd"/>"
										onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
									至
									<input id="vacantPeriod_edate2" name="landlord_vacantperiods[1].edate" type="text"  maxlength="20" class="input-medium Wdate"
										value="<fmt:formatDate value="${empty rent.landlord_vacantperiods?'':rent.landlord_vacantperiods[1].edate}" pattern="yyyy-MM-dd"/>"
										onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/></td>
										<td><input type="hidden" name="landlord_vacantperiods[1].type" value="2"/></td>
									 	</tr>
									 	<tr>
									 		<th>时段三：</th>
									 		<td>
									 		<input type="hidden" name="landlord_vacantperiods[2].id" value="${empty rent.landlord_vacantperiods?'':rent.landlord_vacantperiods[2].id }"/>
									 		<input type="hidden" name="landlord_vacantperiods[2].sn" value="3"/>
									 		<input id="vacantPeriod_sdate3" name="landlord_vacantperiods[2].sdate" type="text"  maxlength="20" class="input-medium Wdate"
										value="<fmt:formatDate value="${empty rent.landlord_vacantperiods?'':rent.landlord_vacantperiods[2].sdate}" pattern="yyyy-MM-dd"/>"
										onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
									至
									<input id="vacantPeriod_edate3" name="landlord_vacantperiods[2].edate" type="text"  maxlength="20" class="input-medium Wdate"
										value="<fmt:formatDate value="${empty rent.landlord_vacantperiods?'':rent.landlord_vacantperiods[2].edate}" pattern="yyyy-MM-dd"/>"
										onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/></td>
										<td><input type="hidden" name="landlord_vacantperiods[2].type" value="2"/></td>
									 	</tr>
									 	<tr>
									 		<th>时段四：</th>
									 		<td>
									 		<input type="hidden" name="landlord_vacantperiods[3].id" value="${empty rent.landlord_vacantperiods?'':rent.landlord_vacantperiods[3].id }"/>
									 		<input type="hidden" name="landlord_vacantperiods[3].sn" value="4"/>
									 		<input id="vacantPeriod_sdate4" name="landlord_vacantperiods[3].sdate" type="text"  maxlength="20" class="input-medium Wdate"
										value="<fmt:formatDate value="${empty rent.landlord_vacantperiods?'':rent.landlord_vacantperiods[3].sdate}" pattern="yyyy-MM-dd"/>"
										onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
									至
									<input id="vacantPeriod_edate4" name="landlord_vacantperiods[3].edate" type="text"  maxlength="20" class="input-medium Wdate"
										value="<fmt:formatDate value="${empty rent.landlord_vacantperiods?'':rent.landlord_vacantperiods[3].edate}" pattern="yyyy-MM-dd"/>"
										onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/></td>
										<td><input type="hidden" name="landlord_vacantperiods[3].type" value="2"/></td>
									 	</tr>
									 	<tr>
									 		<th>时段五：</th>
									 		<td>
									 		<input type="hidden" name="landlord_vacantperiods[4].id" value="${empty rent.landlord_vacantperiods?'':rent.landlord_vacantperiods[4].id }"/>
									 		<input type="hidden" name="landlord_vacantperiods[4].sn" value="5"/>
									 		<input id="vacantPeriod_sdate5" name="landlord_vacantperiods[4].sdate" type="text"  maxlength="20" class="input-medium Wdate"
										value="<fmt:formatDate value="${empty rent.landlord_vacantperiods?'':rent.landlord_vacantperiods[4].sdate}" pattern="yyyy-MM-dd"/>"
										onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
									至
									<input id="vacantPeriod_edate5" name="landlord_vacantperiods[4].edate" type="text"  maxlength="20" class="input-medium Wdate"
										value="<fmt:formatDate value="${empty rent.landlord_vacantperiods?'':rent.landlord_vacantperiods[4].edate}" pattern="yyyy-MM-dd"/>"
										onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/></td>
										<td><input type="hidden" name="landlord_vacantperiods[4].type" value="2"/></td>
									 	</tr>
		
									</table>
		              </div>
		              <div class="tab-pane fade" id="profile1">
				                
									<table class="table">
									 	<tr>
									 		<th>时段一：</th>
									 		<td>
									 		<input type="hidden" name="salesman_vacantperiods[0].id" value="${empty rent.salesman_vacantperiods?'':rent.salesman_vacantperiods[0].id }"/>
									 		<input type="hidden" name="salesman_vacantperiods[0].sn" value="2"/>
									 		<input id="salesman_vacantperiods_sdate1" name="salesman_vacantperiods[0].sdate" type="text"  maxlength="20" class="input-medium Wdate"
										value="<fmt:formatDate value="${empty rent.salesman_vacantperiods?'':rent.salesman_vacantperiods[0].sdate}" pattern="yyyy-MM-dd"/>"
										onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/> 
									至
									<input id="salesman_vacantperiods_edate1" name="salesman_vacantperiods[0].edate" type="text"  maxlength="20" class="input-medium Wdate"
										value="<fmt:formatDate value="${empty rent.salesman_vacantperiods?'':rent.salesman_vacantperiods[0].edate}" pattern="yyyy-MM-dd"/>"
										onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/></td>
										<td><form:select path="salesman_vacantperiods[0].type">
											<form:options items="${fns:getDictList('finance_vacnatperiod_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
										</form:select></td>
									 	</tr>
									 	<tr>
									 		<th>时段二：</th>
									 		<td>
									 		<input type="hidden" name="salesman_vacantperiods[1].id" value="${empty rent.salesman_vacantperiods?'':rent.salesman_vacantperiods[1].id }"/>
									 		<input type="hidden" name="salesman_vacantperiods[1].sn" value="2"/>
									 		<input id="salesman_vacantperiods_sdate2" name="salesman_vacantperiods[1].sdate" type="text"  maxlength="20" class="input-medium Wdate"
										value="<fmt:formatDate value="${empty rent.salesman_vacantperiods?'':rent.salesman_vacantperiods[1].sdate}" pattern="yyyy-MM-dd"/>"
										onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
									至
									<input id="salesman_vacantperiods_edate2" name="salesman_vacantperiods[1].edate" type="text"  maxlength="20" class="input-medium Wdate"
										value="<fmt:formatDate value="${empty rent.salesman_vacantperiods?'':rent.salesman_vacantperiods[1].edate}" pattern="yyyy-MM-dd"/>"
										onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/></td>
										<td><form:select path="salesman_vacantperiods[1].type">
											<form:options items="${fns:getDictList('finance_vacnatperiod_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
										</form:select></td>
									 	</tr>
									 	<tr>
									 		<th>时段三：</th>
									 		<td>
									 		<input type="hidden" name="salesman_vacantperiods[2].id" value="${empty rent.salesman_vacantperiods?'':rent.salesman_vacantperiods[2].id }"/>
									 		<input type="hidden" name="salesman_vacantperiods[2].sn" value="3"/>
									 		<input id="salesman_vacantperiods_sdate3" name="salesman_vacantperiods[2].sdate" type="text"  maxlength="20" class="input-medium Wdate"
										value="<fmt:formatDate value="${empty rent.salesman_vacantperiods?'':rent.salesman_vacantperiods[2].sdate}" pattern="yyyy-MM-dd"/>"
										onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
									至
									<input id="salesman_vacantperiods_edate3" name="salesman_vacantperiods[2].edate" type="text"  maxlength="20" class="input-medium Wdate"
										value="<fmt:formatDate value="${empty rent.salesman_vacantperiods?'':rent.salesman_vacantperiods[2].edate}" pattern="yyyy-MM-dd"/>"
										onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/></td>
										<td><form:select path="salesman_vacantperiods[2].type">
											<form:options items="${fns:getDictList('finance_vacnatperiod_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
										</form:select></td>
									 	</tr>
									 	<tr>
									 		<th>时段四：</th>
									 		<td>
									 		<input type="hidden" name="salesman_vacantperiods[3].id" value="${empty rent.salesman_vacantperiods?'':rent.salesman_vacantperiods[3].id }"/>
									 		<input type="hidden" name="salesman_vacantperiods[3].sn" value="4"/>
									 		<input id="salesman_vacantperiods_sdate4" name="salesman_vacantperiods[3].sdate" type="text"  maxlength="20" class="input-medium Wdate"
										value="<fmt:formatDate value="${empty rent.salesman_vacantperiods?'':rent.salesman_vacantperiods[3].sdate}" pattern="yyyy-MM-dd"/>"
										onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
									至
									<input id="salesman_vacantperiods_edate4" name="salesman_vacantperiods[3].edate" type="text"  maxlength="20" class="input-medium Wdate"
										value="<fmt:formatDate value="${empty rent.salesman_vacantperiods?'':rent.salesman_vacantperiods[3].edate}" pattern="yyyy-MM-dd"/>"
										onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/></td>
										<td><form:select path="salesman_vacantperiods[3].type">
											<form:options items="${fns:getDictList('finance_vacnatperiod_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
										</form:select></td>
									 	</tr>
									 	<tr>
									 		<th>时段五：</th>
									 		<td>
									 		<input type="hidden" name="salesman_vacantperiods[4].id" value="${empty rent.salesman_vacantperiods?'':rent.salesman_vacantperiods[4].id }"/>
									 		<input type="hidden" name="salesman_vacantperiods[4].sn" value="5"/>
									 		<input id="salesman_vacantperiods_sdate5" name="salesman_vacantperiods[4].sdate" type="text"  maxlength="20" class="input-medium Wdate"
										value="<fmt:formatDate value="${empty rent.salesman_vacantperiods?'':rent.salesman_vacantperiods[4].sdate}" pattern="yyyy-MM-dd"/>"
										onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
									至
									<input id="salesman_vacantperiods_edate5" name="salesman_vacantperiods[4].edate" type="text"  maxlength="20" class="input-medium Wdate"
										value="<fmt:formatDate value="${empty rent.salesman_vacantperiods?'':rent.salesman_vacantperiods[4].edate}" pattern="yyyy-MM-dd"/>"
										onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/></td>
										<td><form:select path="salesman_vacantperiods[4].type" >
											<form:options items="${fns:getDictList('finance_vacnatperiod_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
										</form:select></td>
									 	</tr>
									 	<tr>
									 		<th>时段六：</th>
									 		<td>
									 		<input type="hidden" name="salesman_vacantperiods[5].id" value="${empty rent.salesman_vacantperiods?'':rent.salesman_vacantperiods[5].id }"/>
									 		<input type="hidden" name="salesman_vacantperiods[5].sn" value="6"/>
									 		<input id="salesman_vacantperiods_sdate5" name="salesman_vacantperiods[5].sdate" type="text"  maxlength="20" class="input-medium Wdate"
										value="<fmt:formatDate value="${empty rent.salesman_vacantperiods?'':rent.salesman_vacantperiods[5].sdate}" pattern="yyyy-MM-dd"/>"
										onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
									至
									<input id="salesman_vacantperiods_edate6" name="salesman_vacantperiods[5].edate" type="text"  maxlength="20" class="input-medium Wdate"
										value="<fmt:formatDate value="${empty rent.salesman_vacantperiods?'':rent.salesman_vacantperiods[5].edate}" pattern="yyyy-MM-dd"/>"
										onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/></td>
										<td><form:select path="salesman_vacantperiods[5].type" >
											<form:options items="${fns:getDictList('finance_vacnatperiod_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
										</form:select></td>
									 	</tr>
									</table>
							
		              </div>
		              
		            </div>
              </div>
              <div class="tab-pane fade" id="remark">
              	<div class="control-group">
					<label class="control-label">备注:</label>
					<div class="controls">
						<form:textarea path="remarks" htmlEscape="false" rows="4" maxlength="200" class="input-xxlarge"/>
					</div>
				</div>
              	
              </div>
            </div>
        </div>
		

	</form:form>
</body>
</html>
