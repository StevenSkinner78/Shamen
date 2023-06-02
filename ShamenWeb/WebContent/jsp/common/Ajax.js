var xmlHttp = null;
/**
This function initialize the xmlHttp object by checking 
cross browser compatibility.
It supports Firefox, Opera 8.0+, Safari, IE 6.0+ and IE 5.5+ browsers.
*/
function init(){
	if(window.ActiveXObject){
		try{
			//Internet Explorer 6.0+
		    xmlHttp=new ActiveXObject("Msxml2.XMLHTTP");
		    }
		catch (e){
		    try{
		    	//Internet Explorer 5.5+
		      	xmlHttp=new ActiveXObject("Microsoft.XMLHTTP");
		      	}
		    catch (e){
		      	alert("Your browser does not support AJAX!");
		      	return false;
		      }
		    }
		}else if(window.XMLHttpRequest){
			try{
				//Firefox, Opera 8.0+, Safari
				xmlHttp=new XMLHttpRequest();
				}
			catch (e){
				alert("Your browser does not support AJAX!");
		      	return false;
				  }
     		}
 	}
/**
 * To update the target combo object based on source combo value.
 * Internally used JSONArry and JSONObject to get the values
 * @param sourceObj
 * @param targetObj
 * @param url
 */
function updateCombo(sourceObj, targetObj, url){
	init();
	xmlHttp.onreadystatechange=function(){
      if(xmlHttp.readyState==4){
        $(document).trigger("resetIdleTimer");
  		var the_object = eval( "(" + xmlHttp.responseText + ")" );
  		updateComboObjects(targetObj,the_object);
        }
      };
      xmlHttp.open("POST",url,true);
      xmlHttp.send(null);
}
function updateComboObjects(targetObj, data){
	 removeAllOptions(targetObj);
     var j;
     for (j=0;j < data.length;j++){
		var optn = document.createElement("OPTION");
		optn.value = data[j].key;
		optn.text = data[j].val;
		targetObj.options.add(optn);
		}
 }
/**
 * Removes all values from combo box before updating with new values
 * @param selectbox
 * @return
 */
function removeAllOptions(selectbox){
	var i;
    for(i=selectbox.options.length-1;i>=0;i--){
     selectbox.remove(i);
     }
	} 
 
/**
 * JQuery Ajax wrapper 
 * Fires off the AJAX object with user defined parameters information.
 * @param {Object} params    - AJAX data options to bind to the jQuery object
 */
ajaxCall = function (params) {
     // If dataType wasn't specified in the params, default to 'html'
     var dataType = (params.dataType !== undefined) ? params.dataType : 'html';
     // If type wasn't specified in the params, default to 'POST' 
     var type = (params.type !== undefined) ? params.type : 'POST';
     // jQuery AJAX object
     $.ajax({
         // Normal properties
         dataType: dataType,
         type: type,
         async: params.async,
         url: params.url,
         data: params.data,
         cache: params.cache,
         contentType: params.contentType,
         context: params.context,
         jsonp: params.jsonp,
         password: params.password,
         processData: params.processData,
         scriptCharset: params.scriptCharset,
         timeout: params.timeout,
         traditional: params.traditional,
         username: params.username,
         // Global beforeSend wrapper with user defined function 
         beforeSend: function () {
             if (typeof params.beforeSend === 'function'){
                 params.beforeSend();
             }
         },
         // Global success wrapper with user defined function
         success: function (data) {
        	 $(document).trigger("resetIdleTimer");
             if (typeof params.success === 'function') {
                 params.success(data);
             }
         },
         // Global complete wrapper with user defined function 
         complete:function () {
        	 if (typeof params.complete === 'function') {
            	 params.complete();
             }
         },
         // Global error wrapper with user defined function
         error : function (xhr, error) {
        	 if (typeof params.error === 'function') {
            	 params.error(xhr, error);
             }
         }
     });
};