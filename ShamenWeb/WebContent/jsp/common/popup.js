function popup(width, height, url, top, left, returnResult) {
	// create a window name with _ in place of any non-word characters
	var windowName = url.replace(/\W/g, "_");
	return popupWithName(width, height, url, top, left, returnResult,windowName);
}

function popupWithName(width, height, url, top, left, returnResult,winName){
	$(document).trigger("resetIdleTimer");
	if (width > screen.availWidth) {
		width = (screen.availWidth - 100);
	}
	if (height > screen.availHeight) {
		height = (screen.availHeight - 100);
	}
	if (top == null) top = (screen.availHeight - height) / 2;
	if (left == null) left = (screen.availWidth - width) / 2;

	var args = "scrollbars=yes,toolbar=no,directories=no,menubar=no,resizable=yes,status=no,width=" + width + ",height=" + height + ",top=" + top + ",left=" + left;
	
	var popWindow = window.open(url, winName, args);
	try {
		popWindow.focus();
	} catch (err) {
		// ie gives an error if window is "inline pdf" and same already exists; ignore it
	}
	// if we always return this, some browsers show a page with the text: [object]
	if (returnResult == true){
		return popWindow;
	}
}
/***
 *This variable is used by openEmptyWindow and should not
 *be user by any other methods 
 */


/***
 * This method post the data provided in the params array
 * to the provided url.
 * @author nkr000is
 * @param name (REQUIRED) Name of the window
 * @param url (REQUIRED) URL to post the data
 * @param params (REQUIRED) key value parameters
 * @param width (REQUIRED) width of the window
 * @param height (REQUIRED) height of the window
 * @param top (OPTIONAL) top position of the window
 * @param left (OPTIONAL) left position of the window
 * @return the reference to the pop-up window
 */
function popupWithPost(name, url, params, width, height,top, left){
	//Constucting the form element
	var popupEmptyWindowRef = null;
    var form = document.createElement("form");
    form.setAttribute("method", "post");
    form.setAttribute("action", url);
    // setting the target
    form.setAttribute("target", name);
    //onSubmit calls the openPopupWithPost
    form.setAttribute("onsubmit", popupEmptyWindowRef = openEmptyWindow('about:blank',width, height,top, left,name));
    for (var i in params) {
        if (params.hasOwnProperty(i)) {
            var input = document.createElement('input');
            input.type = 'hidden';
            input.name = i;
            input.value = params[i];
            form.appendChild(input);
        }
    }
    document.body.appendChild(form);
    //Submit the form to the specified form target
    form.submit();
    //remove the form from the parent window
    document.body.removeChild(form);
    return popupEmptyWindowRef;
}
 
 /***
  * This method post the data provided in the form 
  * to the provided url.
  * @author nkr000is
  * @param formId (REQUIRED) Id of the form
  * @param url (REQUIRED) URL to post the data
  * @param width (REQUIRED) width of the window
  * @param height (REQUIRED) height of the window
  * @param top (OPTIONAL) top position of the window
  * @param left (OPTIONAL) left position of the window
  * @return the reference to the pop-up window
  */ 
function popupWithFormSubmit(fromId, url, width, height,top, left){
	var popupEmptyWindowRef = null;
	var form = document.getElementById(fromId);
	var onsubmit = form.getAttribute("onsubmit");
	var action = form.getAttribute("action");
	if(action == null){
		action= "";
	}
	if(onsubmit == null){
		onsubmit= "";
	}
	form.setAttribute("method", "post");
    form.setAttribute("action", url);
    // setting the target
    form.setAttribute("target", fromId);
    
    //onSubmit calls the openPopupWithPost
    form.setAttribute("onsubmit", popupEmptyWindowRef = openEmptyWindow('about:blank',width, height,top, left,fromId));
    form.submit();
    form.setAttribute("target", "");
    form.setAttribute("action", action);
    form.setAttribute("onsubmit", onsubmit);
    return popupEmptyWindowRef;
}
  
  /***
   * This method post the data provided in the form 
   * to the provided url.  It's just like the popupWithFormSubmit, but it uses an intermediary jsp(loading.jsp) that 
   * tells the user the report is loading.
   * @author sbd00#is
   * @param formId (REQUIRED) Id of the form
   * @param url (REQUIRED) URL to post the data
   * @param width (REQUIRED) width of the window
   * @param height (REQUIRED) height of the window
   * @param top (OPTIONAL) top position of the window
   * @param left (OPTIONAL) left position of the window
   * @return the reference to the pop-up window
   */ 
 function popupReportWithFormSubmit(fromId, url, width, height,top, left){
 	var popupEmptyWindowRef = null;
 	var form = document.getElementById(fromId);
 	var onsubmit = form.getAttribute("onsubmit");
 	var action = form.getAttribute("action");
 	if(action == null){
 		action= "";
 	}
 	if(onsubmit == null){
 		onsubmit= "";
 	}
 	form.setAttribute("method", "post");
     form.setAttribute("action", url);
     // setting the target
     form.setAttribute("target", fromId);
     
     //onSubmit calls the openPopupWithPost
     form.setAttribute("onsubmit", popupEmptyWindowRef = openEmptyWindow(contextPath+'/jsp/common/loading.jsp',width, height,top, left,fromId));
     form.submit();
     form.setAttribute("target", "");
     form.setAttribute("action", action);
     form.setAttribute("onsubmit", onsubmit);
     return popupEmptyWindowRef;
 }

function openEmptyWindow(url,width, height,top, left,winName){
	return popupWithName(width, height, url, top, left, true,winName);
}

function showReport(url) {
	var newWin = popup(620, 600, url, null, null, true);
	newWin.focus();
	return newWin;
}

function popScreenPrint() {
	var tTitle = "<title>"+document.title+"</title>\n";

	var tStyle = "";
	var docStyles = document.styleSheets;
	for (var i=0;i<docStyles.length; i+=1) {
		tStyle += "<link href=\""+docStyles[i].href;
		tStyle += "\" type=\""+docStyles[i].type;
		tStyle += "\" rel=\"stylesheet\"/>\n";
	}
	var tScript = "";
	var docScripts = document.getElementsByTagName("script");
	for (var i=0;i<docScripts.length;i+=1) {
		var sAttr = docScripts[i].attributes;
		var tTmp = "<script"; 
		for(var n=0; n<sAttr.length; n+=1) {
			var tVal = sAttr[n].value;
			if (tVal == "null") continue;
			if (tVal == 0) continue;
			tTmp += " " + sAttr[n].name + "=\"" + tVal + "\"";
		}
		tTmp += "></script>";
		if (tTmp.indexOf("src") > 0) {
			tScript += tTmp;
		}
	}

	var tBody = "<body onload=\"popScreenFormat()\">"+document.body.innerHTML+"</body>";

	var tHtml = "<html><head>"+tTitle+tStyle+tScript+"</head>"+tBody+"</html>";

	var top = 0;
	var left = 0;
	if (window.screenY) {
		top = window.screenY;
		left = window.screenX;
	} else {
		top = window.screenTop;
		left = window.screenLeft;
	}
	var args = "scrollbars=no,toolbar=no,directories=no,menubar=no,resizable=no,status=no,width=100,height=100";
	args += ",top=" + top + ",left=" + left;
	var prtWin = window.open("", prtWin, args);
	prtWin.document.open();
	prtWin.document.write(tHtml);
	prtWin.document.close();

	window.focus();
}

function popScreenFormat() {
	var srcForms = window.opener.document.forms;
	var prtForms = document.forms;
	for (var i=0; i<srcForms.length; i+=1) {
		var srcNode = srcForms[i].elements;
		var prtNode = prtForms[i].elements;
		for (var j=0; j<srcNode.length; j+=1) {
			if (srcNode[j].style.display == "none") {
				continue;
			}
			if (srcNode[j].type == "button" || srcNode[j].type == "submit" || srcNode[j].type == "reset") {
				prtNode[j].style.display = "none";
			} else if (srcNode[j].checked) {
				prtNode[j].checked = true;
			} else if (srcNode[j].type == "textarea") {
				var newNode = document.createElement("span");
				newNode.setAttribute("id", "newNode"+i+j);
				var newText = document.createTextNode(srcNode[j].value);
				newNode.appendChild(newText);
				var pNode = prtNode[j].parentNode;
				pNode.insertBefore(newNode, prtNode[j]);
				prtNode[j].style.display = "none";
			} else {
				prtNode[j].value = srcNode[j].value;
			}
		}
	}

	window.print();
	window.close();
}

/**
* THIS IS THE METHOD THAT SHOULD BE USED FOR ALL REPORT CSV BUTTONS
* This method doesn't bother with the erroneous window that's used everywhere else.  CSV's don't
* open in the window anyway.  This method simply submits the form to a dummy page that will
* cause the CSV to open in the user's default CSV viewer.
* @author sbd00#is
* @param formId (REQUIRED) Id of the form
* @param width - window width
* @param height - window height
* @param url - path
* @param top - location of popup from top of screen
* @param left - location of popup from left of screen
* @param returnResult - result to return
* @return popup
*/
function CSVReportWithFormSubmit(formId,width, height, url, top, left){
	$(document).trigger("resetIdleTimer");
	if (width > screen.availWidth) {
		width = (screen.availWidth - 100);
	}
	if (height > screen.availHeight) {
		height = (screen.availHeight - 100);
	}
	//var values = $('#fromId').serialize();
	if (top == null) top = (screen.availHeight - height) / 2;
	if (left == null) left = (screen.availWidth - width) / 2;
	var form = document.getElementById(formId);
	var onsubmit = form.getAttribute("onsubmit");
	var action = form.getAttribute("action");
	if(action == null){
		action= "";
	}
	if(onsubmit == null){
		onsubmit= "";
	}
	form.setAttribute("method", "post");
    form.setAttribute("action", url);
    // setting the target
    form.setAttribute("target", "");

	var args = "scrollbars=yes,toolbar=no,directories=no,menubar=yes,resizable=yes,status=no,width=" + width + ",height=" + height + ",top=" + top + ",left=" + left;

	form.submit();
    form.setAttribute("target", "");
    form.setAttribute("action", action);
    form.setAttribute("onsubmit", onsubmit);
	
    return null;
	
}
