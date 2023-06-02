
self.onconnect = function(e) {
	var port = e.ports[0];
	port.onmessage = function() {
		port.postmessage(1 / x);
	};
};