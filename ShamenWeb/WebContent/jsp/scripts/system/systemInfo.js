/**
 * Author: Shane Duncan 
 * Functions for systemInfo.jsp
 */

$(document).ready(function() {
	$('#systemName').focus();
	$('#systemName').blur(function() {
		changeCaseToUpper(this);
	});	
	loadStatuses();
	if (systemRefId == null || systemRefId == '' || systemRefId == '0') {
		$('.submenu > li > a').each(function() {
			$(this).addClass('disabled');
			$(this).attr('href',"#");
		});
	}
	if(systemData != null && systemData != ''){
	    initializeSystemChartTree();
	}
	$('#cancelButton').click(function() {
		var path = document.forms[0].action;
		path += "&cancel=true";
		document.forms[0].action = path;
		document.forms[0].submit();
		return false;
	});
	disableAllInputsOnRestrict($('form'));
});



function updateStatus(data) {
    var hasAWR = false;
    if (data.length > 0) {
        // loop through json, build html and append to list
        for(var t = 0; t < data.length; t++) {
            $("#statusCol" + data[t].key).removeClass();
            if (data[t].value == "ACT") {
                $("#statusCol" + data[t].key).addClass(
                        "fa fa-check fa-lg success-color defaultCursor");
                $("#statusCol" + data[t].key).attr(
                "data-original-title","Status verified - Application is Active");
            } else if (data[t].value == "SUP") {
                $("#statusCol" + data[t].key).addClass(
                        "fa fa-check fa-lg success-color defaultCursor");
                $("#statusCol" + data[t].key).attr(
                        "data-original-title","Status verified - Application is Suspended");
            } else if (data[t].value == "INF") {
                $("#statusCol" + data[t].key).addClass(
                        "fa fa-check fa-lg success-color defaultCursor");
                $("#statusCol" + data[t].key).attr(
                        "data-original-title","Status verified - Application has Information");
            } else if (data[t].value == "UNR") {
                $("#statusCol" + data[t].key).addClass(
                        "fa fa-warning fa-lg danger-color defaultCursor");
                $("#statusCol" + data[t].key).attr(
                        "data-original-title","Status Not Verified - Application is Unresponsive");
            } else if (data[t].value == "AWR") {
                hasAWR = true;
                $("#statusCol" + data[t].key).addClass(
                        "fa fa-pulse fa-spinner fa-fw defaultCursor");
                $("#statusCol" + data[t].key).attr(
                        "data-original-title","Awaiting response from application");
            }
        }

    }
    if (hasAWR) {
        setTimeout(loadStatuses, 5000);
    }
}
function loadStatuses() {
    var count = 0;
    $('[id*=statusVal]').each(function(i) {
        if ($(this).val() == 'AWR') {
            count++;
        }
    });
    if (count > 0) {
        $.ajax({
            type : "GET",
            url : contextPath + "/applicationAction.do?method=loadStatuses",
            dataType : "json",
            success : updateStatus
        });
    }
}

function addPathParams(url){
	if(systemRefId != ""){
    	url+= "&systemRefId=" + systemRefId;
    }else{
    	url = null;
    }
    return url;
}


/**
 * This method configures the sytem chart.  It's a Jit Hypertree
 */

function initializeSystemChartTree(){
    var test = evaluateStr(systemData);
    array = JSON.parse(test);
    var json = {
            "id":"node00001",
            "name":$('#systemName').val(),
            data: {$color:"#408080"},
            "children": array
                };
    var infovis = document.getElementById('infovis4');
    var w = infovis.offsetWidth , h = infovis.offsetHeight - 50;
    
    //init Hypertree
    var ht = new $jit.Hypertree({
      //id of the visualization container
      injectInto: 'infovis4',
      //canvas width and height
      width: w,
      height: 650,
      //Change node and edge styles such as
      //color, width and dimensions.
      Node: {
      overridable: true,
      'transform': false,
          dim: 7,
          color: "#f00"
      },
      Edge: {
      overridable: true,
      type:"hyperline",
          lineWidth: 2,
          color: "#1A4C4B"
      },

      
      //Attach event handlers and add text to the
      //labels. This method is only triggered on label
      //creation
      onCreateLabel: function(domElement, node){
      
          domElement.innerHTML = node.name;
          $jit.util.addEvent(domElement, 'click', function () {
              ht.onClick(node.id, {
                  onComplete: function() {
                      ht.controller.onComplete();
                  }
              });
          });
      },
      //Change node styles when labels are placed
      //or moved.
      onPlaceLabel: function(domElement, node){
          var style = domElement.style;
          style.display = '';
          style.cursor = 'pointer';

          var left = parseInt(style.left);
          var w = domElement.offsetWidth;
          style.left = (left - w / 2) + 'px';
          if(node.id.search("colMbr") >= 0){
                node.data.$color = '#6495ED';
                node.data.$type = 'star';
                style.color = "#A9A9A9";
                style.fontSize = "0.6em";
                node.dim = 6;
            }else if(node.id.search("batchApp") >= 0){
                node.data.$color = '#557EAA';
                node.data.$type = 'triangle';
                style.fontSize = "0.7em";
                node.dim = 7;
            }else if(node.id.search("webApp") >= 0){
                node.data.$type = 'circle';
                node.data.$color = '#83548B';
                style.fontSize = "0.7em";
                node.dim = 8;
            }else if(node.id.search("system") >= 0){
                node.data.$color = '#EBB056';
                 node.data.$type = 'square';
                 style.fontSize = "0.8em";
                 node.dim = 10;
            }
      },
    //This method is called right before plotting an
      //edge. This method is useful for adding individual
      //styles to edges.
      onBeforePlotLine: function(adj){
          //Set random lineWidth for edges.
          
             
          if(adj.nodeTo.id.search("colMbr") >= 0){
              adj.data.$lineWidth = .25;
              adj.Edge.color = "#A9A9A9";
          }else if(adj.nodeTo.id.search("batchApp") >= 0){
              adj.data.$lineWidth = .5;
              adj.Edge.color = "#557EAA";
          }else if(adj.nodeTo.id.search("webApp") >= 0){
              adj.data.$lineWidth = .75;
              adj.Edge.color = "#83548B";
          }else if(adj.nodeTo.id.search("system") >= 0){
              adj.data.$lineWidth = 1;
              adj.Edge.color = "#EBB056";
          }
      },
      onComplete: function(){
//          Log.write("done");
          
         
      }
    });
    //load JSON data.
    ht.loadJSON(json);
    //compute positions and plot.
    ht.refresh();
    //end
    ht.controller.onComplete();
    //emulate a click on the root node.
    ht.onClick(ht.root);
}