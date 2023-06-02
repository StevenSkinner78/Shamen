function checkStatus(data) {    
	var ajaxRequest;
	try {
		ajaxRequest = new XMLHttpRequest(); // Opera 8.0+, Firefox, Safari
	} catch (e) {
		try { // Internet Explorer Browsers
			ajaxRequest = new ActiveXObject("Msxml2.XMLHTTP");
		} catch (e) {
			try {
				ajaxRequest = new ActiveXObject("Microsoft.XMLHTTP");
			} catch (e) { // Something went wrong
				console.error("AJAX not possible");
				return false;
			}
		}
	}

	// Create a function that will receive data sent from the server
	ajaxRequest.onreadystatechange = function() {
		if (ajaxRequest.readyState == 4) {
			self.postMessage(ajaxRequest.responseText);
		}
	}

	ajaxRequest.open("GET", data, true);
	ajaxRequest.send();

}

this.onmessage  =   function(e){
    checkStatus(e.data); // the message comes in just once on pageLoad
};