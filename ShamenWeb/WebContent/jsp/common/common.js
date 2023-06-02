/*******************************************************************************
 * *** GLOBAL VARIABLES ***
 ******************************************************************************/
var productionMode = true;
// added saurabhg
var values = new Array();
var dirtyFlag = 0;
var appName = navigator.appName;
var appVersion = navigator.appVersion;
var browser = "unknown";
var restrictFields = 'false';
var version = new Number(navigator.appVersion.substring(0, 1));
var ieNotice = getCookie("ie_notice_cookie");

var browser = (function(){
	  var test = function(regexp) { return regexp.test(window.navigator.userAgent);}
	  switch(true){
		case test(/edge/i): return "edge";
		case test(/opr/i) && (!!window.opr || !!window.opera): return "opera";
		case test(/chrome/i) && !!window.chrome: return "chrome";
		case test(/trident/i) : return "ie";
		case test(/firefox/i) : return "firefox";
		case test(/safari/i): return "safari";
		default: return "other";
	  }
	})();
/* This function is used to set cookies */
function setCookie(name, value, expiresP, pathP, domainP, secureP) {
	$.cookie(name, value, {
		expires : expiresP,
		path : pathP,
		domain : domainP,
		secure : secureP
	});
}

/* This function is used to get cookies */
function getCookie(name) {
	return $.cookie(name);
}
function deleteCookie(name) {
	$.cookie(name, null);
}

function handleForward(action){
    var path = contextPath + action;
    if (caller != null && caller != "") {
        path+= "&caller=" + caller;
    }
    document.forms[0].action = path;
    document.forms[0].submit();
}

function populateModal(refId,batchName,type){
	let startFunc = 'startBatch()';
	if(type == 'COL'){
		startFunc = 'startBatchCollection()';
	}// end if
	$('#launchBatchBtn').attr('onclick', startFunc);
	$('#batch-name').empty();
	$('#batch-name').html(batchName);
	$("#batch-ref-id").val(refId);
	$("#batch-modal").modal();
}

function startBatch(){
	var url = contextPath + "/batchAction.do?method=startBatch&batchRefId=" + $("#batch-ref-id").val();
	url+= "&jobParameters=" + $("#job-parameters").val();
	document.forms[0].action = url,
	document.forms[0].submit();
}

function startBatchCollection(){
	var url = contextPath + "/batchCollectionAction.do?method=startBatch&batchRefId=" + $("#batch-ref-id").val();
	url+= "&jobParameters=" + $("#job-parameters").val();
	document.forms[0].action = url,
	document.forms[0].submit();
}


function runConfirmation() {
	var msg = "You requested to run the batch application.";
	msg += "\nAre you sure?";
	msg += "\n\nTo continue with run process click \"OK\". ";
	msg += "\nOtherwise click \"Cancel\" \n";

	return confirm(msg);
}

function handleSaveButton(saveButton, disable) {
	saveButton.disabled = disable;
}
/**
 * This a onclick confirmation for activating schedules
 */
function activateConfirmation() {
    var msg = "You requested to activate the batch schedule.";
    msg += "\nAre you sure?";
    msg += "\n\nTo continue with process click \"OK\". ";
    msg += "\nOtherwise click \"Cancel\" \n";

    return confirm(msg);
}
/**
 * This a onclick confirmation for de-activating schedules
 */
function deactivateConfirmation() {
    var msg = "You requested to deactivate the batch schedule.";
    msg += "\nAre you sure?";
    msg += "\n\nTo continue with process click \"OK\". ";
    msg += "\nOtherwise click \"Cancel\" \n";

    return confirm(msg);
}

/**
 * @author NKR000IS
 * @param {Object}
 *            Id the common part of the ID should be passsed. This method is
 *            used by display tag and it can also be used by other pages which
 *            needs expand all and collapse all.
 */
function expandAll(tableId) {
	$("img[id ^= img" + tableId + " ]").each(function() {
		$(this).attr('src', expand);
	});
	
	$("tr[id ^= '" + tableId + "']").show();
	$("tr[id ^= '" + tableId + "']").removeClass('hidden');
}

function collapseAll(tableId) {
	$("img[id ^= img" + tableId + " ]").each(function() {
		$(this).attr('src', colapse);
	});
	$("tr[id ^= '" + tableId + "']").hide();
	$("tr[id ^= '" + tableId + "']").addClass('hidden');
}

function toggleHandler(cnt) {
	var currentSrc = $("#img" + cnt).attr('src');
	if (currentSrc == colapse) {
		$("#img" + cnt).attr('src', expand);
		$("#" + cnt).removeClass('hidden');
		$("#" + cnt).show();

	} else {
		$("#img" + cnt).attr('src', colapse);
		$("#" + cnt).addClass('hidden');
		$("#" + cnt).hide();
	}
}

/**
 * @author Ajay This method toggles HTML content with a specified className.
 * @param showHideFlag
 *            (boolean value)
 * @return
 */
function toggleByClassName(showHideFlag, className) {
	if (showHideFlag == true) {
		$('.' + className).removeClass('hidden');
	} else if (showHideFlag == false) {
		$('.' + className).addClass('hidden');
	}
}

/**
 * ---------------------------------------------------------- ASKS THE USER IF
 * THEY ARE SURE THEY WANT TO DELETE THE ITEM FROM LIST
 * -----------------------------------------------------------
 */
function deleteConfirmation() {
	var msg = "About to delete, are you sure?";
	msg += "\n\nTo continue with delete click \"OK\". ";
	msg += "\nOtherwise click \"Cancel\" \n";

	return confirm(msg);
}

/**
 * Function used to submit form data and refresh page
 */
function refreshPage(url) {
	document.forms[0].action = url;
	document.forms[0].submit();
}


function evaluateStr(str){
    str = "" + str;
    str = str.replace(/&#39;/g,"\'");
    str = str.replace(/&quot;/g,"\"");
    str = str.replace(/&amp;/g,"\&");
    str = str.replace(/&lt;/g,"\<");
    str = str.replace(/&gt;/g,"\>");
    str = str.replace(/&#010;/g,"\n");
    str = str.replace(/&#013;/g,"\r");
     
    return str;
}
 function htmlEncode(str){
     str = str.replace(/&/g,"&amp;");
     str = str.replace(/</g,"&lt;");
     str = str.replace(/>/g,"&gt;");
     str = str.replace(/\"/g,"&quot;");
     
     return str;
 }
/**
 * WhiteSpace manipulation in a string
 * 
 */
// whitespace characters
var whitespace = " \t\n\r";
// Check whether string s is empty.
function isEmpty(s) {
	return ((s == null) || (s.length == 0) ? true :false);
}

// Returns true if string s is empty or
// whitespace characters only.
function isWhitespace(s) {
	var i;

	// Is s empty?
	if (isEmpty(s))
		return true;

	// Search through string's characters one by one
	// until we find a non-whitespace character.
	// When we do, return false; if we don't, return true.
	for (i = 0; i < s.length; i++) {
		// Check that current character isn't whitespace.
		var c = s.charAt(i);
		if (whitespace.indexOf(c) == -1)
			return false;
	}
	// All characters are whitespace.
	return true;
}

/**
 * autoTab Tabs to the next field filtering the special keys and sets the focus.
 * This function accepts three parameters
 * 
 * @param 1:
 *            object of current field.
 * @param 2:
 *            window key event.
 * @param 3:
 *            object of next field. Created by Sarat Kasa on 06/23/2010 Modified
 *            by Ajay Kumar on 06/23/2010 to use JQuery inArray function
 */
function autoTab(obj, e, nextobj) {
	var len = obj.maxLength;
	var isNN = (navigator.appName.indexOf("Netscape") != -1);
	var keyCode = (isNN) ? e.which : e.keyCode;
	var filter = (isNN) ? [ 0, 8, 9 ] : [ 0, 8, 9, 16, 17, 18, 37, 38, 39, 40, 46 ];
	if (obj.value.length >= len && $.inArray(keyCode, filter) == -1) {
		// only advance to the next field it the cursor is on the end of the
		// textbox
		if (determineCursorPosition(obj) == obj.maxLength) {
			obj.value = obj.value.slice(0, len);
			$('#' + nextobj).focus();
			$('#' + nextobj).select();
		}
	}
}

/**
 * Function used to change the case of the value in the textObject to UpperCase.
 */
function changeCaseToUpper(textObject) {
	textObject.value = textObject.value.toUpperCase();
}

/**
 * This method will restrict the user from entering Numbers or Alphabets or
 * AlphaNumeric depending on the value you send and returns true or false for
 * the event handler "onkeypress".
 */
function FilterAlphaNumericInput(e, requestorId) {
	var key;
	var keychar;
	var requestId = requestorId;
	if (window.event)
		key = window.event.keyCode;
	else if (e)
		key = e.which;
	else
		return true;
	keychar = String.fromCharCode(key);
	keychar = keychar.toLowerCase();
	// control keys
	if ((key == null) || (key == 0) || (key == 8) || (key == 9) || (key == 13) || (key == 27) || (key == 32))
		return true;
	// Alphabets
	else if (requestId == "alpha") {
		if ((("abcdefghijklmnopqrstuvwxyz").indexOf(keychar) > -1))
			return true;
		else
			return false;
	} else if (requestId == "lastName") {
		if ((("abcdefghijklmnopqrstuvwxyz'").indexOf(keychar) > -1))
			return true;
		else
			return false;
	}
	// Numbers
	else if (requestId == "num") {
		if ((("0123456789").indexOf(keychar) > -1))
			return true;
		else
			return false;
	}
	// Shift Indicator, specifically for WDU Custody Staff Application
	else if (requestId == "shf") {
		if ((("123").indexOf(keychar) > -1))
			return true;
		else
			return false;
	}
	// Numbers with a dash
	else if (requestId == "phn") {
		if ((("0123456789-()").indexOf(keychar) > -1))
			return true;
		else
			return false;
	}
	else if (requestId == "zip") {
		if ((("0123456789-").indexOf(keychar) > -1))
			return true;
		else
			return false;
	}
	// Alphabets and Numbers
	else if ((("abcdefghijklmnopqrstuvwxyz0123456789").indexOf(keychar) > -1))
		return true;
	else
		return false;
}

function disableAllInputsOnRestrict () {

    if (restrict == 'true') {
        var formElements = $('input,select,textarea,button');
        for (var i = 0; i < formElements.length; i++) {
            if (formElements[i].id != 'goButton' 
                && formElements[i].name != 'pagesize' 
                && !$(formElements[i]).hasClass('sortable')
                 && !$(formElements[i]).hasClass('select2-input')
                && formElements[i].id != 'cancelButton'
                && !$(formElements[i]).hasClass("dropdown-toggle")
                 && !$(formElements[i]).hasClass("close")
                && !$(formElements[i]).hasClass("enableWithRestrict")) {
                $(formElements[i]).attr("disabled", "disabled");
            }
        }
        var allLinks = $('a');
        for (var i = 0; i < allLinks.length; i++) {
            if (allLinks[i].href != null && !allLinks[i].href.match("window.print") && !allLinks[i].href.match("showHelp")
                    && !$(allLinks[i]).hasClass("enableWithRestrict") 
                     && !$(allLinks[i]).parent().hasClass("enableWithRestrict")
                     && allLinks[i].parentNode.tagName != 'LI') {
                // If the anchor is apart of display tag bottom row, don't
                // disable the link.
                if (!$(allLinks[i]).parent().hasClass("exportlinks") 
                    && !$(allLinks[i]).parent().hasClass("pagination-plain")) {
                   $(allLinks[i]).attr("disabled", "disabled");
                }
            }
        }
        var items = $('li');
        for (var i = 0; i < items.length; i++) {
            if (!$(items[i]).parent().hasClass("nav flex-column")
                    && !$(items[i]).parent().hasClass("pagination-plain")
                    && !$(items[i]).parent().hasClass("submenu")
                    && !$(items[i]).parent().hasClass("dropdown-menu")
                    && !$(items[i]).hasClass("dropdown")
                    && !$(items[i]).hasClass("enableWithRestrict")) {
                disableAnchor(items[i].firstChild,true);
            }
        }
    }
}
function enableAllInputs(){
    var formElements = $('input,select,textarea,button');
    for (var i = 0; i < formElements.length; i++) {
        if (formElements[i].id != 'goButton' 
            && formElements[i].name != 'pagesize' 
            && !$(formElements[i]).hasClass('sortable')
            && !$(formElements[i]).hasClass('select2-input')
            && formElements[i].id != 'cancelButton'
            && !$(formElements[i]).hasClass("dropdown-toggle")
            && !$(formElements[i]).hasClass("close")
            && !$(formElements[i]).hasClass("enableWithRestrict")) 
        {
            
            formElements[i].removeAttribute("disabled");
        }
    }
    var allLinks = $('a');
    for (var i = 0; i < allLinks.length; i++) {
        if (allLinks[i].href != null && !allLinks[i].href.match("window.print") && !allLinks[i].href.match("showHelp")
                && !$(allLinks[i]).hasClass("enableWithRestrict") && allLinks[i].parentNode.tagName != 'LI') {
            // If the anchor is apart of display tag bottom row, don't
            // disable the link.
            if (!$(allLinks[i]).parent().hasClass("exportlinks") 
                && !$(allLinks[i]).parent().hasClass("pagination-plain")) 
            {
                allLinks[i].removeAttribute("disabled");
            }
        }
    }
    var items = $('li');
    for (var i = 0; i < items.length; i++) {
        if (!$(items[i]).parent().hasClass("nav flex-column")
                && !$(items[i]).parent().hasClass("pagination-plain")
                && !$(items[i]).parent().hasClass("submenu")
                && !$(items[i]).parent().hasClass("dropdown-menu")
                && !$(items[i]).hasClass("dropdown")
                && !$(items[i]).hasClass("enableWithRestrict")) {
            disableAnchor(items[i].firstChild,false);
        }
    }
}
/**
 * This generic function add/remove the Anchor tag link and onClick event
 * 
 * @author r9g000is This method disables the anchor tag. Modified by Anurag on
 *         09/22/2010
 */
function disableAnchor(obj, disable) {
	var href = $(obj).attr("href");
	var onclick = $(obj).attr("onclick");
	var href_bak = $(obj).attr("href_bak");
	var onclick_bak = $(obj).attr("onclick_bak");

	if (disable) {
		if (href_bak == null) {
//			href_bak = document.createAttribute('href_bak');
			obj.setAttribute('href_bak','');
		}
		if (onclick_bak == null) {
//			onclick_bak = document.createAttribute('onclick_bak');
			obj.setAttribute('onclick_bak','');
		}
		if (href && href != "") {
		    $(obj).attr('href_bak', href);
		}
		if (onclick && onclick != "") {
		    $(obj).attr('onclick_bak', onclick);
		}
		obj.removeAttribute('href');
		obj.setAttribute('onclick', '');
		obj.style.color = "gray";
	} else {
		if (obj.attributes['href_bak'] != null && obj.attributes['href_bak'].nodeValue != '') {
			obj.setAttribute('href', obj.attributes['href_bak'].nodeValue);
		}
		if (obj.attributes['onclick_bak'] != null && obj.attributes['onclick_bak'].nodeValue != '') {
			obj.setAttribute('onclick', obj.attributes['onclick_bak'].nodeValue);
		}
		obj.style.color = "blue";
	}
}

/**
 * This method is used to submit the data on click of the pagination. the data
 * is submitted to the updateListPageData method in the otrackDispatchAction
 * class.
 * 
 * Include this JavaScript when you have HTML controls (like text box or check
 * box etc on your listing page)
 * 
 * 
 */
function submitListPageData(actionPath) {

	var formElements = document.forms[0].elements;

	for (var i = 0; i < formElements.length; i++) {
		if (formElements[i].tagName == 'INPUT' || formElements[i].tagName == 'SELECT' || formElements[i].tagName == 'TEXTAREA') {

			formElements[i].disabled = false;
		}
	}

	document.forms[0].action = actionPath;
	document.forms[0].submit();
}

function checkAppEnv(){
	if(environment_name ==  null || environment_name == ''){
		$('#app-env-link1').removeClass();
		$('#app-env-link1').addClass("d-none");
		$('#app-env-link2').addClass("d-none");
	}
}

function winResize(){
		$('#ie_notice').css('top', $('#header').outerHeight(true));
		$('#main-section').css('margin-top', $('#header').outerHeight(true));
}

$(window).resize(winResize);

$(document).ready(function() {
	$('[data-toggle="tooltip"]').tooltip();
	
	if(browser == 'ie' && (ieNotice == null || ieNotice != "hide")){
		$('#ie_notice').removeClass('d-none');
		$('#ie_notice').addClass('show');
	}else{
		$('#ie_notice').removeClass('show');
		$('#ie_notice').addClass('d-none');
	}
	
	$("#ie_notice_button").click(function(e){
		setCookie("ie_notice_cookie","hide");
	});
	checkAppEnv();
	winResize();
});