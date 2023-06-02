/**
 * Author: Steven L. Skinner SLS00#IS Date: April 25, 2013 Functions for
 * authorizedUser.jsp
 */

$(document).ready( function() {


	if($('#activeVal').val() == "Y"){
        $('#emailInd').attr('checked',true);
    }else{
        $('#emailInd').attr('checked',false);
    }
	showAuthLevelDesc();
	
	$('#userID').focus();	 
	
	$('[type=search]').each( function() {
		$(this).attr("placeholder", "Search...");
	});
	$('#userID').blur( function() {
		changeCaseToUpper(this);
	});
	$('#lastName').blur( function() {
		changeCaseToUpper(this);
	});
	$('#firstName').blur( function() {
		changeCaseToUpper(this);
	});
	
	$('#deleteView')
	.click(function(){
		return deleteConfirmation();
	});
	$('form').submit(function() {
        if($('#emailInd').is(':checked')){
            $('#activeVal').val("Y");
        }else{
            $('#activeVal').val("N");
        }
    });
	disableAllInputsOnRestrict($('form'));
});

function showAuthLevelDesc() {
    var value = $('#authority').val();
    if (value == "VIEW") {
        $('#viewDesc').show();
        $('#userDesc').hide();
        $('#admnDesc').hide();
    } else if (value == "USER") {
        $('#viewDesc').hide();
        $('#userDesc').show();
        $('#admnDesc').hide();
    } else if (value == "ADMN") {
        $('#viewDesc').hide();
        $('#userDesc').hide();
        $('#admnDesc').show();
    } else {
        $('#viewDesc').hide();
        $('#userDesc').hide();
        $('#admnDesc').hide();
    }
}


/**	CLEAR Button onclick event
 *	Reset the form in either addView or editView
 */
function resetTheForm() {
	var path = contextPath + "/userAction.do?method=";
	if (userRefId == "" || userRefId == 0) {
		path += "list";
		document.forms[0].action = path;
	} else {
		path += "editView&userRefId=" + userRefId;
		document.forms[0].action = path;
	}
	document.forms[0].submit();
}
