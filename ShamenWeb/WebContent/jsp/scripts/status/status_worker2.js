

//function timedCount(i) {
//    postMessage(i);
//
//}
//
//this.onmessage = function(event) {
//	timedCount(event.data);
//};

var i = 0;

function timedCount() {
    postMessage(i);
    if(i < 100){
    	i = i + 100;
    	setTimeout("timedCount()",500);
    }else{
    	i = 0;
    	setTimeout("timedCount()",500);
    }
}

timedCount(); 