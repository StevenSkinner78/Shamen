$(document).ready(function() {
	loadStatuses();
	$('[type=search]').each( function() {
		$(this).attr("placeholder", "Search...");
	});
	
	
	if (goButtonDisable != '') {
		$('#application tbody tr:eq(0)').hide();
	
	}
	$("#refHead").click(function(){
		$("#refHead").removeClass();
	    $("#refHead").addClass("fa fa-pulse fa-spinner fa-fw defaultCursor");
	});
	disableAllInputsOnRestrict($('form'));
});


function changeIcon(refId){
    $("#statusCol" + refId).removeClass();
    $("#statusCol" + refId).addClass("fa fa-pulse fa-spinner fa-fw defaultCursor");
    $("#statusCol" + refId).attr("data-original-title","Awaiting response from application")
    return true;
}

function updateStatus(data) {
	var hasAWR = false;
	if (data.length > 0) {
		// loop through json, build html and append to list
		for(var t = 0; t < data.length; t++) {
			$("#statusCol" + data[t].key).removeClass();
			if (data[t].value == "ACT") {
				$("#statusCol" + data[t].key).addClass("fa fa-circle fa-fw text-success defaultCursor");
				$("#statusCol" + data[t].key).attr("data-original-title", "Status verified - Application is Active");
				$("#notifyCol" + data[t].key).removeClass("d-none");
			} else if (data[t].value == "SUP") {
				$("#statusCol" + data[t].key).addClass("fa fa-circle fa-fw text-success defaultCursor");
				$("#statusCol" + data[t].key).attr("data-original-title", "Status verified - Application is Suspended");
				$("#notifyCol" + data[t].key).addClass("d-none");
			} else if (data[t].value == "INF") {
				$("#statusCol" + data[t].key).addClass("fa fa-circle fa-fw text-success defaultCursor");
				$("#statusCol" + data[t].key).attr("data-original-title", "Status verified - Application has Information");
				$("#notifyCol" + data[t].key).removeClass("d-none");
			} else if (data[t].value == "UNR") {
				$("#statusCol" + data[t].key).addClass("fa fa-circle fa-fw text-danger defaultCursor");
				$("#statusCol" + data[t].key).attr("data-original-title", "Status Not Verified - Application is Unresponsive");
				$("#notifyCol" + data[t].key).addClass("d-none");
			} else if (data[t].value == "AWR") {
				hasAWR = true;
				$("#statusCol" + data[t].key).addClass("fa fa-pulse fa-spinner fa-fw defaultCursor");
				$("#statusCol" + data[t].key).attr("data-original-title", "Awaiting response from application");
				$("#notifyCol" + data[t].key).addClass("d-none");
			}
		}

	}
	if (hasAWR) {
		setTimeout(loadStatuses, 5000);
	}
}
function loadStatuses() {
	var count = 0;
	$('[id*=statusVal]').each(function(i) {
		if ($(this).val() == 'AWR') {
			count++;
		}
	});
	if (count > 0) {
		$.ajax({
			type : "GET",
			url : contextPath + "/applicationAction.do?method=loadStatuses",
			dataType : "json",
			success : updateStatus
		});
	}
}
