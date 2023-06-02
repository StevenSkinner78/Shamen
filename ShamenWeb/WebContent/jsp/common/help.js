/**
 * 
 */
$(document).ready(function() {

var $button = $("<i id='source-button' class='fa fa-question-circle fa-fw text-primary pointerCursor'></i>").click(function(){
    $("#source-modal h3").empty();
    $("#source-modal pre").empty();
    var ele = $(this).parent().attr('id');
    var label ='';
        if(!$(this).parent().hasClass('navbar-brand') && !$(this).parent().hasClass('navbar-text')){
              label = $(".page-title").attr('title');
        }
        if($(this).parent().is('H3') == false){
            if(label != ''){
                label+= "<br/>";
            }
            label+= $(this).parent().attr('title');
        }
        $("#source-modal h3").html(label);
        var text = evaluateStr(msgs[ele]);
        $("#source-modal pre").html(text);
        
        $("#source-modal").modal();
      });

 $(".bs-component").hover(function(){
    $(this).append($button);
     $button.show();
   
  }, function(){
     $button.hide();
  });
   
 $("#source-button-header").click(function(){
	 $("#source-modal h3").empty();
	    $("#source-modal pre").empty();
	    var ele = $(this).parent().attr('id');
	    var label ='';
	        if(!$(this).parent().hasClass('navbar-brand') && !$(this).parent().hasClass('navbar-text')){
	              label = $(".page-title").attr('title');
	        }
	        if($(this).parent().is('H3') == false){
	            if(label != ''){
	                label+= "<br/>";
	            }
	            label+= $(this).parent().attr('title');
	        }
	        $("#source-modal h3").html(label);
	        var text = evaluateStr(msgs[ele]);
	        $("#source-modal pre").html(text);
	        
	        $("#source-modal").modal();
	      });
});