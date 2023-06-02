/**
 * 
 */
$(document).ready(function() {

    $('#calendar').fullCalendar({
        themeSystem :'shamenTheme',
        header : {
            left : 'prev,next today',
            center : 'title',
            right : 'month,agendaWeek,agendaDay,listDay'
        },
        views : {
            listDay : {
                buttonText : 'agenda'
            }
        },
        navLinks : true,
        editable : false,
        eventLimit : true, 
        eventTimeFormat: "HH:mm",
        slotLabelFormat: "HH:mm",
        defaultView : 'agendaWeek',
        allDaySlot: false,
        scrollTime: '00:00:00',
        defaultTimedEventDuration : '00:30:00',
        loading: function(bool) {
            $('#loading').toggle(bool);
        },
        events : function(start, end,timezone,loadArray) {
            cache: true,
            $.ajax({
                type : "GET",
                url : contextPath + "/calendarAction.do?method=loadData",
                dataType : "json",
                data: {
                    start: start.format(),
                    end: end.format()
                },
                success :  function(doc) {
                    
                    $.each(doc, function (key, val) {
                        val.url = contextPath + val.url;
                   });
                    loadArray(doc);
                },
                error: function (xhr, err) {
                    alert("Error retrieving calendar data");
                }
            });
        }
        
    });
    
    $('form').submit(function() {
       return false;
    });
    
});

function loadArray(data){
    var array = JSON.parse(data);
   
    return array;
}

