/**
 * Author: Steven L. Skinner SLS00#IS Date: April 25, 2013 Functions for
 * authorizedUser.jsp
 */

$(document).ready(function() {
	$('#controllerName').focus();
	$('#controllerName').blur(function() {
		changeCaseToUpper(this);
	});
			
	if (controllerRefId == null || controllerRefId == '' || controllerRefId == '0') {
		$('.submenu > li > a').each(function() {
			$(this).addClass('disabled');
			$(this).attr('href',"#");
		});
	}
	if(controllerRefId != null && controllerRefId != '' && controllerRefId != '0'){	
		$('#controllerAddress').attr("disabled", true);
	}
	changeAnimationName();
	$('#cancelButton').click(function() {
		var path = document.forms[0].action;
		path += "&cancel=true";
		document.forms[0].action = path;
		document.forms[0].submit();
		return false;
	});
	     disableAllInputsOnRestrict($('form'));
});

function stopConfirmation(batchName) {
	var msg = "You requested to stop batch application: " + batchName + ".";
	msg += "\nAre you sure?";
	msg += "\n\nTo continue with stop process click \"OK\". ";
	msg += "\nOtherwise click \"Cancel\" \n";

	return confirm(msg);
}
function changeAnimationName() {
	
	if (status == '' || status == 'Unresponsive') {
		$('#statusDisplayCol').toggleClass("label text-danger");
	} else if (status == 'Connected') {
		$('#statusDisplayCol').toggleClass("label text-success");
	} else if (status == 'Awaiting Response') {
		$('#statusDisplayCol').toggleClass("label text-warning");
	} 
}

function addPathParams(url){
	if(controllerRefId != ""){
    	url+= "&controllerRefId=" + controllerRefId;
    }else{
    	url = null;
    }
    return url;
}