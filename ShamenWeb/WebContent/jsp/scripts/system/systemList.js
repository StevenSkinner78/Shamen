/**
 * Author: Shane B. Duncan Date: April 25, 2017 Functions for systemList.jsp
 */

$(document).ready(function() {

	$('[id*=table]').each(function() {
		var num = $(this).attr('id').indexOf("e") + 1;
		var tableNum = $(this).attr('id').substr(num);
		if (tableNum % 2 != 0) {
			$(this).removeClass('systemTable');
			$(this).addClass('systemTableEven');
		}
	});
	disableAllInputsOnRestrict($('form'));
	 if (restrict == 'true') {
	     var formElements = $('button');
	        for (var i = 0; i < formElements.length; i++) {
	            if ($(formElements[i]).hasClass("dropdown-toggle")) {
	                $(formElements[i]).attr("disabled", "disabled");
	            }
	        }
	 }
});
