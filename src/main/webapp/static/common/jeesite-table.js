
(function($){
	$.initSortTable = function(tablename,searchFun){
		// 表格排序
		var orderBy = $("#orderBy").val().split(" ");
		$("#"+tablename+" th.sort").each(function(){
			if ($(this).hasClass(orderBy[0])){
				orderBy[1] = orderBy[1]&&orderBy[1].toUpperCase()=="DESC"?"down":"up";
				$(this).html($(this).html()+" <i class=\"icon icon-arrow-"+orderBy[1]+"\"></i>");
			}
		});
		$("#"+tablename+" th.sort").click(function(){
			var order = $(this).attr("class").split(" ");
			var sort = $("#orderBy").val().split(" ");
			for(var i=0; i<order.length; i++){
				if (order[i] == "sort"){order = order[i+1]; break;}
			}
			if (order == sort[0]){
				sort = (sort[1]&&sort[1].toUpperCase()=="DESC"?"ASC":"DESC");
				$("#orderBy").val(order+" DESC"!=order+" "+sort?"":order+" "+sort);
			}else{
				$("#orderBy").val(order+" ASC");
			}
			if("function" ==  typeof searchFun)
				searchFun();
		});
	}

})(jQuery)

String.prototype.toDate = function(){
	return new Date(Date.parse(this.replace(/-/g,  "/"))); 
}

Date.prototype.dateAddYears = function(years){
	var dt1 = this;
	var dt2 = new Date(dt1.setFullYear( dt1.getFullYear() + new Number(years) ));
	return dt2;
}

Date.prototype.dateAddDays = function(days) {
	var a = this;
    a = a.valueOf();
    a = a + days * 24 * 60 * 60 * 1000;
    a = new Date(a);
    return a;
}

Date.prototype.formate = function(format){ 
	var o = { 
	"M+" : this.getMonth()+1, //month 
	"d+" : this.getDate(), //day 
	"h+" : this.getHours(), //hour 
	"m+" : this.getMinutes(), //minute 
	"s+" : this.getSeconds(), //second 
	"q+" : Math.floor((this.getMonth()+3)/3), //quarter 
	"S" : this.getMilliseconds() //millisecond 
	} 

	if(/(y+)/.test(format)) { 
	format = format.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length)); 
	} 

	for(var k in o) { 
	if(new RegExp("("+ k +")").test(format)) { 
	format = format.replace(RegExp.$1, RegExp.$1.length==1 ? o[k] : ("00"+ o[k]).substr((""+ o[k]).length)); 
	} 
	} 
	return format; 
} 
