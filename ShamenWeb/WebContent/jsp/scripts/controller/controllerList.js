/**
 * Author: Steven L. Skinner SLS00#IS Date: April 25, 2013 Functions for
 * authorizedUser.jsp
 */

$(document).ready( function() {
	loadStatuses();
});

function updateStatus(data) {
	var hasAWR = false;
	if (data.length > 0) {
		// loop through json, build html and append to list
		for(var t = 0; t < data.length; t++) {
			$("#statusCol" + data[t].key).removeClass();
			if (data[t].value == "CON") {
				$("#statusCol" + data[t].key).addClass(
						"fa fa-circle fa-fw text-success defaultCursor");
			} else if (data[t].value == "UNR") {
				$("#statusCol" + data[t].key).addClass(
						"fa fa-circle fa-fw text-danger defaultCursor");
			} else if (data[t].value == "AWR") {
				hasAWR = true;
				$("#statusCol" + data[t].key).addClass(
						"fa fa-pulse fa-spinner fa-fw defaultCursor");
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
			url : contextPath + "/controllerAction.do?method=loadStatuses",
			dataType : "json",
			success : updateStatus
		});
	}
}


