/**
 * Author: Steven L. Skinner SLS00#IS Date: April 25, 2013 Functions for
 * authorizedUser.jsp
 */

$(document).ready(function() {
  	$('#batchName').focus();
	var pageDate = new Date().getHours() + ":" + new Date().getMinutes();
	
	if($('#activeVal').val() == "Y"){
        $('#activeInd').attr('checked',true);
        toggleFrequencyAst($(this).val());
    }else{
        $('#activeInd').attr('checked',false);
    }
    $('[id*=weeks]').each(function(i) {
        var id = "weeks" + $(this).val();
        $(this).attr('id',id);
    });
    $('[id*=daysCheck]').each(function(i) {
        var id = "daysCheck" + $(this).val();
         $(this).attr('id',id);
     });
   $("#controllerRefId").change(function() {
       populateDefaultLocation();
   });
    $('#clearFreq').click(function () {
        if ($('#freqVal').val() != '') {
            $('[id*=frequency]').each(function (i) {
                    $(this).attr('checked',false);
            });
        }
      
        checkFrequency();
    });
	
   
	if($('#freqVal').val() != ''){
	    $('[id*=frequency]').each(function(i) {
		    if ($(this).val() == $('#freqVal').val()) {
			$(this).attr('checked','checked');
		    }
		});
	}
	checkFrequency();
		
    $('[id*=frequency]').click(function() {
		checkFrequency();
		$('#scheduleChangeFlag').val(true);
		
	});
	$("#systemRefId").change(function() {
		populateWebApps();
	});

	$('#monSelect').click(function() {
		$('#onCol').hide();
		$('#onCol2').hide();
		$('#daysCol').fadeIn();
		$('#scheduleChangeFlag').val(true);
	});
	$('#monSelect2').click(function() {
		$('#daysCol').hide();
		$('#onCol').fadeIn();
		$('#onCol2').fadeIn();
		$('#scheduleChangeFlag').val(true);
	});
	$('[id=recur]').change(function(i) {
        $('#scheduleChangeFlag').val(true);
    });
	$('[id*=daysCheck]').click(function(i) {
        $('#scheduleChangeFlag').val(true);
    });
    $('[id=weekNumber]').change(function(i) {
        $('#scheduleChangeFlag').val(true);
    });
    $('[id=selectedWeekDayMonth]').change(function(i) {
        $('#scheduleChangeFlag').val(true);
    });
    
    if(partOfCollection == 'N'){
        $('#startDateCal').datetimepicker({
            date : $('#startDate').val() == "" ? new Date() : new Date($('#startDate').val()),
            startDate: new Date(new Date().getFullYear(), new Date().getMonth(), new Date().getDate(), 0, 0, 0),
            viewMode : 'YMD',
            onDateChange : function() {
                $('#startDate').val(this.getText('MM/dd/yyyy'));
                $('#startDateCal').hide();
                $('#scheduleChangeFlag').val(true);
            },
            onClear : function() {
                $('#startDate').val("");
                $('#scheduleChangeFlag').val(true);
            },
            onToday : function() {
                $('#startDate').val(this.getText('MM/dd/yyyy'));
                $('#startDateCal').hide();
                $('#scheduleChangeFlag').val(true);
            },
            onOk : function() {
                $('#startDate').val(this.getText('MM/dd/yyyy'));
                $('#startDateCal').hide();
                $('#scheduleChangeFlag').val(true);
            }
            
        });
        $('#startDateCal').hide();
        var current = new Date();
        if($('#startTime').val() != ""){
            current.setHours($('#startTime').val().substring(0,2));
            current.setMinutes($('#startTime').val().substring(3,5));
        }
        $('#startTimeCal').datetimepicker({
            date : current,
            viewMode : 'HM',
            onDateChange : function() {
                $("#startTime").val(this.getText('HH:mm'));
                $('#scheduleChangeFlag').val(true);
            },
            onClear : function() {
                $('#startTime').val("");
                $('#scheduleChangeFlag').val(true);
            },
            onToday : function() {
                $('#startTimeCal').hide();
                $('#scheduleChangeFlag').val(true);
            },
            onOk : function() {
                $('#startTime').val(this.getText('HH:mm'));
                $('#startTimeCal').hide();
                $('#scheduleChangeFlag').val(true);
            }
        });
        $('#startTimeCal').hide();
    	$('#startDate').click(function(){
    		$('#startDateCal').toggle();
    		
    	});
    
    	$('#startTime').click(function() {
    		$('#startTimeCal').toggle();
    		
    	});
	}
    $('#addScheduleButton').click(function() {
    	if($('#activeInd').is(':checked')){
		    $('#activeVal').val("Y");
		}else{
		    $('#activeVal').val("N");
		}
        var path = contextPath +'/batchDetailAction.do?method=addSchedule';
        document.forms[0].action = path;
        document.forms[0].submit();
        return false;
    });
    $('#cancelScheduleButton').click(function() {
    	clearSchedule();
        return false;
    });
	$('#cancelButton').click(function() {
        var path = document.forms[0].action;
        path += "&cancel=true";
        document.forms[0].action = path;
        document.forms[0].submit();
        return false;
    });
	$('form').submit(function() {
		enableAllInputs();
	});
	$('#saveButton').addClass('enableWithRestrict');
	disableAllInputsOnRestrict($('form'));
});

function populateDefaultLocation() {
    $('#fileLoc').val('');
    var controllerRefId = $("#controllerRefId").val();

    $.ajax({
        type : "GET",
        url : contextPath + "/batchDetailAction.do?method=loadDefaultLocation",
        data : "controllerRefId=" + controllerRefId,
        dataType : "json",
        success : updateDefaultLocation
    });
}

function updateDefaultLocation(data){
    $('#fileLoc').val(data.value);
}
function populateWebApps() {
	$('#applicationRefId').empty();
	var system = $("#systemRefId").val();

	$.ajax({
		type : "GET",
		url : contextPath + "/batchDetailAction.do?method=loadWebApps",
		data : "systemRefId=" + system,
		dataType : "json",
		success : updateWebApps,
		complete : function() {
			if (system == '0' || $("#systemRefId").val() == '') {
				$("#applicationRefId").trigger("change");
			}
		}
	});
}

function updateWebApps(data){
	var options = '';
	if (data.length > 0) {
		// loop through json, build html and append to list
		$.each(data, function(ctr, obj) {
			options += '<option value="' + obj.key + '">' + obj.value
					+ '</option>';
		});

		$('#applicationRefId').append($(options));
	}
}
/**
 * Toggle Link onClick event Show/Hide the selectorDiv to display the checkBoxes
 * and column labels.
 */
function toggleFrequencyAst(val) {
	if (val == 'Y') {
		$('#freqAst').show();
	} else {
		$('#freqAst').hide();
	}
}


function checkFrequency() {
	var freq = null;
	$('[id*=frequency]').each(function(i) {
	    if ($(this).is(':checked')) {
		freq = $(this).val();
		$('#freqVal').val(freq);
	    }
	});
	if (freq != null) {
		if (freq == 'ONT') {
			$('#weeklyRow').hide();
			$('#monthlyRow').hide();
			$('#daysCol').hide();
			$('#onCol').hide();
			$('#onCol2').hide();
			$('#dailyRow').hide();
			$('#monSelect').attr('checked',false);
			$('#monSelect2').attr('checked',false);
			$('#recur').val('');
			$('#repeatRow').fadeIn();
		} else if (freq == 'DLY') {
			$('#weeklyRow').hide();
			$('#monthlyRow').hide();
			$('#daysCol').hide();
			$('#onCol').hide();
			$('#onCol2').hide();
			$('#monSelect').attr('checked',false);
			$('#monSelect2').attr('checked',false);
			$('#dailyRow').fadeIn();
			$('#repeatRow').fadeIn();
			
		} else if (freq == 'WKY') {
			$('#dailyRow').hide();
			$('#monthlyRow').hide();
			$('#daysCol').hide();
			$('#onCol').hide();
			$('#onCol2').hide();
			$('#repeatRow').hide();
			$('#recur').val('');
			$('#repeat').val('BLA');
			$("#repeat").trigger("change");
			$('#monSelect').attr('checked',false);
			$('#monSelect2').attr('checked',false);
			$('#weeklyRow').fadeIn();
		} else if (freq == 'MTY') {
			$('#dailyRow').hide();
			$('#weeklyRow').hide();
			$('#repeatRow').hide();
			$('#recur').val('');
			$('#repeat').val('BLA');
            $("#repeat").trigger("change");
			$('#monthlyRow').fadeIn();
			if ($('#monSelect').is(':checked')) {
				$('#onCol').hide();
				$('#onCol2').hide();
				$('#daysCol').fadeIn();
			} else if ($('#monSelect2').is(':checked')) {
				$('#daysCol').hide();
				$('#onCol').fadeIn();
				$('#onCol2').fadeIn();
			} else {
				$('#daysCol').hide();
				$('#onCol').hide();
				$('#onCol2').hide();
			}
		} else {
			$('#dailyRow').hide();
			$('#weeklyRow').hide();
			$('#monthlyRow').hide();
			$('#daysCol').hide();
			$('#repeatRow').hide();
			$('#onCol').hide();
			$('#onCol2').hide();
			$('#recur').val('');
			$('#repeat').val('BLA');
            $("#repeat").trigger("change");
			$('#monSelect').attr('checked',false);
			$('#monSelect2').attr('checked',false);
			$('#freqVal').val('');
		}
	} else {
		$('#dailyRow').hide();
		$('#weeklyRow').hide();
		$('#monthlyRow').hide();
		$('#daysCol').hide();
		$('#onCol').hide();
		$('#onCol2').hide();
		$('#repeatRow').hide();
		$('#recur').val('');
		$('#repeat').val('BLA');
        $("#repeat").trigger("change");
		$('#monSelect').attr('checked',false);
		$('#monSelect2').attr('checked',false);
		$('#freqVal').val('');
	}

}
/**
 * This function clears the schedule
 * @returns
 */
function clearSchedule() {
	$('[id*=frequency]').each(function(i) {
		$(this).attr('checked', false);
	});
	$('#scheduleRefId').val('');
	$('#listNumber').val('');
	$('#activeInd').attr('checked',false);
	$('#activeVal').val('');
	$('#startDate').val('');
	$('#startTime').val('');
	$('#dailyRow').hide();
	$('#weeklyRow').hide();
	$('#monthlyRow').hide();
	$('#daysCol').hide();
	$('#onCol').hide();
	$('#onCol2').hide();
	$('#repeatRow').hide();
	$('#recur').val('');
	$('#repeat').val('BLA');
	$("#repeat").trigger("change");
	$('#monSelect').attr('checked', false);
	$('#monSelect2').attr('checked', false);
	$('#freqVal').val('');
}

/**
 * Toggle Link onClick event Show/Hide the selectorDiv to display the checkBoxes
 * and column labels.
 */
function toggleInfoDetailDiv() {
    $('#batchInfoDiv').toggle('slow');
    $('#toggleLinkInfo').toggleClass("fa-toggle-down");
    $('#toggleLinkInfo').toggleClass("fa-toggle-up");
    
}

/**
 * Toggle Link onClick event Show/Hide the selectorDiv to display the checkBoxes
 * and column labels.
 */
function toggleScheduleDetailDiv() {
    $('#batchScheduleDiv').toggle('slow');
    $('#button-row').toggle('slow');
    $('#toggleLinkSchedule').toggleClass("fa-toggle-down");
    $('#toggleLinkSchedule').toggleClass("fa-toggle-up");
    
}

function stopConfirmation(batchName) {
	var msg = "You requested to stop batch application: " + batchName + ".";
	msg += "\nAre you sure?";
	msg += "\n\nTo continue with stop process click \"OK\". ";
	msg += "\nOtherwise click \"Cancel\" \n";

	return confirm(msg);
}

/**
 * ---------------------------------------------------------- ASKS THE USER IF
 * THEY ARE SURE THEY WANT TO DELETE THE ITEM FROM LIST
 * -----------------------------------------------------------
 */
function clearConfirmation(batchName) {
	var msg = "You are about to delete all Batch Run Status Information for";
	msg += "\nBatch App: \"" + batchName + "\". Are you sure?";
	msg += "\n\nTo continue with delete click \"OK\". ";
	msg += "\nOtherwise click \"Cancel\" \n";

	return confirm(msg);
}
