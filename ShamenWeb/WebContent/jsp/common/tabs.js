/**	Author:	James Eric Mansfield JEM01#IS
 *	Date:	April 9, 2013
 *	Functions for Struts-Menu tabs.jsp tile 
 */

if (!document.getElementById) {
    document.getElementById = function() { return null; }
}

function addParams(obj) {
	var url = addPathParams(obj.href);
	if(url== null){
		url = obj.href;
		obj.setAttribute("class", "disabled")
	}
	obj.setAttribute("href", url);
	return true;
}

function initMenu() {
	
    var uls = document.getElementsByTagName("ul");
    for (i = 0; i < uls.length; i++) {
        if (uls[i].className == "nav flex-column") {
            decorateMenu(uls[i]);
        }
    }
}

function decorateMenu(menu) {
    var items = menu.getElementsByTagName("li");
    for (var i=0; i < items.length; i++) {
        items[i].firstChild.myIndex = i;
        // retain any existing onclick handlers from menu-config.xml
        if (items[i].firstChild.onclick) {
            items[i].firstChild.onclick=function() { 
                eval(items[this.myIndex].firstChild.getAttribute("onclick"));
                setCookie("menuSelected", this.myIndex); 
                };
        } else {
            items[i].firstChild.onclick=function() { 
                setCookie("menuSelected", this.myIndex); 
            };
        }
    }
    activateMenu(items);
}

function activateMenu(items) {
	var overview = "overviewAction";
	var userAction = "userAction";
	var userDetailAction = "userDetailAction";
	var systemAction = "systemAction";
	var systemDetailAction = "systemDetailAction";
	var listAction = "controllerAction";
	var detailAction = "controllerDetailAction";
	var batchAction = "batchAction";
	var batchCollectionAction = "batchCollectionAction";
	var applicationAction = "applicationAction";
	var statusAction = "statusAction";
	var calendarAction = "calendarAction";
	var statAction = "statDetailAction";
	
	// sub menu
	var addBatch = "batchDetailAction";
	var addBatchCollection = "batchCollectionDetailAction";
	var updateAll = "=updateAll";
	var updateAllBatch = "=updateAllBatch";
	var addApplication = "applicationDetailAction";
	var loadPage = "=loadPage";
	var resetStats = "=resetStats";
	var showStats = "=showStats";
	var loadUserData = "=loadUserData";
	var loadSystemData = "=loadSystemData";
	var loadSystemStatTree = "=loadSystemStatTree";
	var batchRunAverages = "=batchRunAverages";
		
    var activeMenu;
    var found = 0;
    
    for (var i=0; i < items.length; i++) {
        var url = items[i].firstChild.getAttribute("href");
        var current = document.location.toString();
        if (current.indexOf(url) != -1) {
            found++;
        }
    }
//     
    // more than one found, use cookies
//    if (found > 1) {  
//        var menuSelected = getCookie("menuSelected"); 
//        if (items[menuSelected].parentNode.className == "submenu") {
//            items[menuSelected].firstChild.className="active";
//            items[menuSelected].parentNode.parentNode.className="active";
//        } else {            
//            items[menuSelected].className+="active";
//        }
//    } else {
        // only one found, match on URL
        for (var i=0; i < items.length; i++) {
            var url = items[i].firstChild.getAttribute("href");
            if(url == null){
            	url = "";
            }
            var current = document.location.toString();
            if ((current.indexOf(addBatch) != -1 && url.indexOf("#") != -1)
    				|| (current.indexOf(addBatchCollection) != -1 && url.indexOf("#") != -1) 
    				|| (current.indexOf(userDetailAction) != -1 && url.indexOf("#") != -1) 
    				|| (current.indexOf(addApplication) != -1 && url.indexOf("#") != -1)
    				|| (current.indexOf(addApplication) != -1 && url.indexOf("javascript:void(0)") != -1)
    				|| (current.indexOf(resetStats) != -1 && url.indexOf(resetStats) != -1)
    				|| (current.indexOf(showStats) != -1 && url.indexOf(resetStats) != -1)
    				|| (current.indexOf(loadUserData) != -1 && url.indexOf(loadUserData) != -1) 
                    || (current.indexOf(loadSystemData) != -1 && url.indexOf(loadSystemData) != -1)) {
                if (items[i].parentNode.className == "submenu") {
                    items[i].firstChild.className+=" active";
                    items[i].className+=" active";
                    items[i].parentNode.parentNode.className+=" active";
                } else {            
                    items[i].className+=" active";
                }
            }else{
            	if((current.indexOf(overview) != -1 && url.indexOf(overview) != -1)
            	    || (current.indexOf(calendarAction) != -1 && url.indexOf(calendarAction) != -1)
            		|| (current.indexOf(userAction) != -1 && url.indexOf(userAction) != -1) 
            		|| (current.indexOf(listAction) != -1 && url.indexOf(listAction) != -1)
            		|| (current.indexOf(detailAction) != -1 && url.indexOf("javascript:void(0)") != -1)
            		|| (current.indexOf(batchAction) != -1 && url.indexOf(batchAction) != -1)
            		|| (current.indexOf(batchCollectionAction) != -1 && url.indexOf(batchCollectionAction) != -1)
            		|| (current.indexOf(applicationAction) != -1 && url.indexOf(applicationAction) != -1)
            		|| (current.indexOf(systemAction) != -1 && url.indexOf(systemAction) != -1)
            		|| (current.indexOf(systemDetailAction) != -1 && url.indexOf("javascript:void(0)") != -1)
            		|| (current.indexOf(statusAction) != -1 && url.indexOf(statusAction) != -1)
            		|| (current.indexOf(loadPage) != -1 && url.indexOf(loadPage) != -1)){
            		
            		 items[i].className+=" active";
            	}
            }
//        }
    	}
}

// Select the menu that matches the URL when the page loads
window.onload=initMenu;

// =========================================================================
//                          Cookie functions 
// =========================================================================
/* This function is used to set cookies */
//function setCookie(name,value,expires,path,domain,secure) {
//  document.cookie = name + "=" + escape (value) +
//    ((expires) ? "; expires=" + expires.toGMTString() : "") +
//    ((path) ? "; path=" + path : "") +
//    ((domain) ? "; domain=" + domain : "") + ((secure) ? "; secure" : "");
//}
//
///* This function is used to get cookies */
//function getCookie(name) {
//	var prefix = name + "=" 
//	var start = document.cookie.indexOf(prefix) 
//
//	if (start==-1) {
//		return null;
//	}
//	
//	var end = document.cookie.indexOf(";", start+prefix.length) 
//	if (end==-1) {
//		end=document.cookie.length;
//	}
//
//	var value=document.cookie.substring(start+prefix.length, end) 
//	return unescape(value);
//}
//
///* This function is used to delete cookies */
//function deleteCookie(name,path,domain) {
//  if (getCookie(name)) {
//    document.cookie = name + "=" +
//      ((path) ? "; path=" + path : "") +
//      ((domain) ? "; domain=" + domain : "") +
//      "; expires=Thu, 01-Jan-70 00:00:01 GMT";
//  }
//}