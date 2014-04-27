
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
