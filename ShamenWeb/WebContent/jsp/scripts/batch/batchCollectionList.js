/**
 * Author: Steven L. Skinner SLS00#IS Date: April 25, 2013 Functions for
 * authorizedUser.jsp
 */

$(document).ready(function() {
	loadStatuses();
	$('[type=search]').each( function() {
		$(this).attr("placeholder", "Search...");
	});
	
	if (goButtonDisable != '') {
		$('#collections tbody tr:eq(0)').hide();
	}
	disableAllInputsOnRestrict($('form'));
});

function updateStatus(data) {
	var hasAWR = false;
	if (data.length > 0) {
		// loop through json, build html and append to list
		for(var t = 0; t < data.length; t++) {
			if (data[t].value == "CON") {
				$('[id=statusCol' + data[t].key + ']').each(function(i) {
				$(this).removeClass();
				$(this).addClass(
						"fa fa-server fa-fw success-color defaultCursor");
			});
			} else if (data[t].value == "UNR") {
				$('[id=statusCol' + data[t].key + ']').each(function(i) {
				$(this).removeClass();
				$(this).addClass(
						"fa fa-server fa-fw danger-color defaultCursor");
				});
			} else if (data[t].value == "AWR") {
				hasAWR = true;
				$('[id=statusCol' + data[t].key + ']').each(function(i) {
				$(this).removeClass();
				$(this).addClass(
						"fa fa-pulse fa-spinner fa-fw defaultCursor");
				});
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