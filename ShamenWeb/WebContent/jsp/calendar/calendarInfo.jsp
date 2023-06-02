<%@ include file="/jsp/common/tagdeps.jsp"%>
<bean:page id="requestVar" property="request" />
<bean:define id="calendarForm" name="calendarForm" type="gov.doc.isu.shamen.form.CalendarForm"/>
<tiles:importAttribute/>
<script type="text/javascript" 	src="<bean:write name="requestVar" property="contextPath"/>/jsp/assets/js/moment.min.js"></script>
<script type="text/javascript" 	src="<bean:write name="requestVar" property="contextPath"/>/jsp/assets/js/fullcalendar.min.js"></script>
<script type="text/javascript" src="<bean:write name="requestVar" property="contextPath"/>/jsp/scripts/calendar/calendarInfo.js"></script>
<script type="text/javascript">
var msgs = {"commonheadertitle":"<bean:message key="common.header.title" bundle="helpMsgs"/>","jmsconnected":"<bean:message key="common.header.jmsServerConnection" bundle="helpMsgs"/>","jmsnotconnected":"<bean:message key="common.header.jmsServerConnection" bundle="helpMsgs"/>","calendartitle":"<bean:message key="calendar.title" bundle="helpMsgs"/>"};

</script>
<html:form action="/calendarAction.do?method=showCalendar">
<html:hidden name="calendarForm" property="schedule" styleId="schedule"/>
<div class="page-header row no-gutters py-4 mb-3 border-bottom">
	<div class="col-12 col-sm-4 text-center text-sm-left mb-0" >
		<h3 class="page-title" id="calendartitle" title ="<bean:message key="prompt.calendar.title"/>"><bean:message key="prompt.calendar.title"/> <i id='source-button-header' class='fa fa-question-circle fa-fw '></i></h3>
	</div>
</div>
<div class="row">
	<div class="col">
		<div class="card mb-4">
			<div class="card-body p-3 pb-3">
				<div id="calendar"></div>
				<div id='loading' style="">
					<div><img id="img-spinner" src="<bean:write name="requestVar" property="contextPath"/>/jsp/images/loading_calendar.gif" alt="Loading"/></div>
				</div>
			</div>
		</div>
	</div>
</div>
</html:form>

