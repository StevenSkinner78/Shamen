/**
 * 
 */

$(document).ready(function() {
	
	$("#appInfo-div").hide();
	
	if(applicationRefId != null && applicationRefId != '' && applicationRefId != '0'){	
		$('#applicationName').attr("disabled", true);
		$('#appLocation').attr("disabled", true);
	}
	if($('#showAppInfoVal').val() == "Y"){
        $('#showApplicationInfo').attr('checked','checked');
        $("#appInfo-div").show();
    }
	$('#showApplicationInfo').click(function(){
		if($('#showApplicationInfo').is(':checked')){
			 $("#appInfo-div").slideDown(1000);
		}else{
			 $("#appInfo-div").slideUp(1000);
		}
	});
	
	$('#cancelButton').click(function() {
		var path = document.forms[0].action;
		path += "&cancel=true";
		document.forms[0].action = path;
		document.forms[0].submit();
		return false;
	});
	$('form').submit(function() {
		if($('#showApplicationInfo').is(':checked')){
	        $('#showAppInfoVal').val('Y');
	    }else{
	        $('#showAppInfoVal').val('N');
	        $('#applicationInfo').val(' ');
	    }
	});

	disableAllInputsOnRestrict($('form'));
});
