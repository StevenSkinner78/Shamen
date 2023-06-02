$(document).ready(function() {
    
    if(listType == 'batch'){
        var tp = showChart.split(",");
        for (var l = 0; l < tp.length; l++) {
            $('#showChart' + tp[l]).attr('checked', true);
        }
    }else if(listType == "user"){
        var array;
        var lbl = selectedLabels.split(",");
        if(lbl != ""){
            for (var l = 0; l < lbl.length; l++) {
                $('#lbl' + lbl[l]).attr('checked', true);
            }
            init();
        }
    	
    }else if(listType == "system"){
    	initializeSystemChart();        
    }else if(listType == "systemTree"){
        initializeSystemChartTree();        
//    }else if(listType == "system"){
//        //initializeSystemChartTree();        
    }
   
});
var labelType, useGradients, nativeTextSupport, animate;

(function() {
  var ua = navigator.userAgent,
      iStuff = ua.match(/iPhone/i) || ua.match(/iPad/i),
      typeOfCanvas = typeof HTMLCanvasElement,
      nativeCanvasSupport = (typeOfCanvas == 'object' || typeOfCanvas == 'function'),
      textSupport = nativeCanvasSupport 
        && (typeof document.createElement('canvas').getContext('2d').fillText == 'function');
  //I'm setting this based on the fact that ExCanvas provides text support for IE
  //and that as of today iPhone/iPad current text support is lame
  labelType = (!nativeCanvasSupport || (textSupport && !iStuff))? 'Native' : 'HTML';
  nativeTextSupport = labelType == 'Native';
  useGradients = nativeCanvasSupport;
  animate = !(iStuff || !nativeCanvasSupport);
})();

/**
 * This method configures the system chart.  It's a Jit Spacetree
 */
function initializeSystemChart(){
	var test = evaluateStr(systemData);
    array = JSON.parse(test);
    var json = {
            "id":"node00001",
            "name":"DOC App Dev",
            data: {},
            "children": array
                };
    //preprocess subtrees orientation
    var arr = json.children, len = arr.length;
    for(var i=0; i < len; i++) {
        //split half left orientation
      if(i < len / 2) {
            arr[i].data.$orn = 'left';
            $jit.json.each(arr[i], function(n) {
                n.data.$orn = 'left';
            });
        } else {
        //half right
            arr[i].data.$orn = 'right';
            $jit.json.each(arr[i], function(n) {
                n.data.$orn = 'right';
            });
        }
    }
  
	//init Spacetree
    //Create a new ST instance
    var st = new $jit.ST({
        //id of viz container element
        injectInto: 'infovis2',
        //multitree
        multitree: true,
        //set duration for the animation
        levelsToShow: 1,
        duration: 400,
        //set animation transition type
        transition: $jit.Trans.Quart.easeInOut,
        //set distance between node and its children
        levelDistance: 50,
        siblingOffset: 3,
        subtreeOffset: 3,
        //enable panning
        Navigation: {
          enable:true,
          panning: 'avoid nodes'
        },
        //set node and edge styles
        //set overridable=true for styling individual
        //nodes or edges
        Node: {
            autoHeight: true,
            width: 220,
            type: 'rectangle',
            color: '#2D8684',
            overridable: true
        },
        Edge: {
            type: 'bezier',
            lineWidth: 2,
            color:'white',
            overridable: true
        },
        Label: {
            type: 'HTML',
            style: 'bold'
        },
       
        onBeforeCompute: function(node){
            $('#start').toggle();
        },
        
        onAfterCompute: function(){
            $('#start').toggle();
        },
        //This method is called on DOM label creation.
        //Use this method to add event handlers and styles to
        //your node.
        onCreateLabel: function(label, node){
            label.id = node.id;            
            label.innerHTML = node.name;
            label.type = labelType;
            label.overridable= true;
            label.onclick = function(){
                st.setRoot(node.id, 'animate');
            };
            //set label styles
            var style = label.style;
            style.width = '220px';
            style.height = 'auto';            
            style.cursor = 'pointer';
            style.color = '#000';
            style.fontSize = '10px';
            style.textAlign= 'center';
            
        },
        
        //This method is called right before plotting
        //a node. It's useful for changing an individual node
        //style properties before plotting it.
        //The data properties prefixed with a dollar
        //sign will override the global node style properties.
        onBeforePlotNode: function(node){
            //add some color to the nodes in the path between the
            //root node and the selected node.
            if (node.selected) {
                 node.data.$color = "#3CB371";
         
            } else {
                delete node.data.$color;
                //if the node belongs to the last plotted level
                if(!node.anySubnode("exist")) {
                    //count children number
                    var count = 0;
                    node.eachSubnode(function(n) { count++; });
                }
                if(node.id.search("colMbr") >= 0){
                	node.data.$color = '#6495ED';
                }else if(node.id.search("batchApp") >= 0){
                	node.data.$color = '#557EAA';
                	
                }else if(node.id.search("webApp") >= 0){
                    node.data.$type = 'ellipse';
                	node.data.$color = '#83548B';
                	node.data.$autoWidth = true;
                	node.data.$width = 220;
                
                }else if(node.id.search("system") >= 0){
                	node.data.$color = '#EBB056';
                   
                }
            }
        },
       
        
        //This method is called right before plotting
        //an edge. It's useful for changing an individual edge
        //style properties before plotting it.
        //Edge data properties prefixed with a dollar sign will
        //override the Edge global style properties.
        onBeforePlotLine: function(adj){
            if (adj.nodeFrom.selected && adj.nodeTo.selected) {
                adj.data.$color = "#eed";
                adj.data.$lineWidth = 3;                
            }
            else {
                delete adj.data.$color;
                delete adj.data.$lineWidth;
            }
        }
    });
    //load json data
    st.loadJSON(json);
  //compute node positions and layout
    st.compute();
    st.geom.translate(new $jit.Complex(0, -500), 'current');
    st.select(st.root);
	
}

/**
 * This method configures the sytem chart.  It's a Jit Hypertree
 */

function initializeSystemChartTree(){
	var test = evaluateStr(systemData);
    array = JSON.parse(test);
    var json = {
            "id":"node00001",
            "name":"DOC App Dev",
            data: {$color:"#408080"},
            "children": array
                };
    var infovis = document.getElementById('infovis3');
    var w = infovis.offsetWidth - 50, h = infovis.offsetHeight - 50;
    
    //init Hypertree
    var ht = new $jit.Hypertree({
      //id of the visualization container
      injectInto: 'infovis3',
      //canvas width and height
      width: w,
      height: h,
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
function changeGraph(refId){
   var path = contextPath + "/statDetailAction.do?method=loadUserData&listChange='Y'";
   document.forms[0].action = path;
   document.forms[0].submit();
}
function changeChart(){
    var path = contextPath + "/statDetailAction.do?method=showStats";
    document.forms[0].action = path;
    document.forms[0].submit();
 }
function getUserData() {

    $.ajax({
        type : "GET",
        url : contextPath + "/statDetailAction.do?method=loadUserData",
        dataType : "json",
        success : loadArray
    });
}
function loadArray(data){
     array = data;
}

function init(){
    //init data
    var test = evaluateStr(userData);
    array = JSON.parse(test);
    var json = {
            "color":colors.split(","),
            "label":selectedLabels.split(","),
            "values": array
                };
      //init BarChart
      var barChart = new $jit.BarChart({
        //id of the visualization container
        injectInto: 'infovis',
        //whether to add animations
        animate: true,
        //horizontal or vertical barcharts
        orientation: 'vertical',
        //bars separation
        barsOffset: 0.5,
        //visualization offset
        Margin: {
          top: 5,
          left: 5,
          right: 5,
          bottom: 5
        },
        //labels offset position
        labelOffset:5,
        //bars style
        type: 'stacked',
        //whether to show the aggregation of the values
        showAggregates:true,
        //whether to show the labels for the bars
        showLabels:true,
        //label styles
        Label: {
          type: 'HTML', //Native or HTML
          size: 13,
          family: 'Arial',
          color: 'white'
        },
        //tooltip options
        Tips: {
          enable: true,
          onShow: function(tip, elem) {
            tip.innerHTML = "<b>" + elem.name + "</b>: " + elem.value;
          }
        }
      
      });
      //load JSON data.
      barChart.loadJSON(json);
      //end
  }