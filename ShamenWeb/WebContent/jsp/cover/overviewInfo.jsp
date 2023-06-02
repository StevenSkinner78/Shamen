<%@ include file="/jsp/common/tagdeps.jsp"%>
<%@page import="java.util.Iterator"%>
<%@page import="gov.doc.isu.shamen.models.SystemModel"%>
<bean:page id="requestVar" property="request" />
<bean:define id="overviewForm" name="overviewForm"
	type="gov.doc.isu.shamen.form.OverviewForm" />
	<tiles:importAttribute/>
	<script type="text/javascript" src="<bean:write name="requestVar" property="contextPath"/>/jsp/scripts/overview/overviewInfo.js"></script>
<script type="text/javascript">
var chart1Text = "<bean:write name="overviewForm" property="system.name" /> System<logic:equal name="overviewForm" property="system.name" value="All">s</logic:equal>";
var chart1Bad = "<bean:write name="overviewForm" property="system.stat.allStats.naughtyApps"/>";
var chart1Good = "<bean:write name="overviewForm" property="system.stat.allStats.niceApps"/>";
var chart1UnreportableText = "Unreportable";
var chart1Unreportable = "<bean:write name="overviewForm" property="system.stat.allStats.unreportableApps"/>";


var chart2Text = "<bean:write name="overviewForm" property="system.name" /> Batch Applications";
var chart2BadText = "In Error";
var chart2Bad = "<bean:write name="overviewForm" property="system.stat.batchStats.naughtyApps"/>";
var chart2GoodText = "Thriving";
var chart2Good = "<bean:write name="overviewForm" property="system.stat.batchStats.niceApps"/>";
var chart2UnreportableText = "Unreportable";
var chart2Unreportable = "<bean:write name="overviewForm" property="system.stat.batchStats.unreportableApps"/>";
var chart2PendingText = "Pending";
var chart2Pending = "<bean:write name="overviewForm" property="system.stat.batchStats.pendingApps"/>";
var chart2RunningText = "In Progress";
var chart2Running = "<bean:write name="overviewForm" property="system.stat.batchStats.runningApps"/>";
var chart2OffScheduleText = "Did Not Run";
var chart2OffSchedule = "<bean:write name="overviewForm" property="system.stat.batchStats.offScheduleApps"/>";
var chart2InactiveText = "Deactivated";
var chart2Inactive = "<bean:write name="overviewForm" property="system.stat.batchStats.inActiveApps"/>";

var chart3Text = "<bean:write name="overviewForm" property="system.name" /> Web Applications";
var chart3BadText = "Unresponsive";
var chart3Bad = "<bean:write name="overviewForm" property="system.stat.webAppStats.naughtyApps"/>";
var chart3InfoText = "Information";
var chart3Info = "<bean:write name="overviewForm" property="system.stat.webAppStats.infoApps"/>";
var chart3SuspendedText = "Suspended";
var chart3Suspended = "<bean:write name="overviewForm" property="system.stat.webAppStats.suspendedApps"/>";
var chart3ActiveText = "Active";
var chart3Active = "<bean:write name="overviewForm" property="system.stat.webAppStats.activeApps"/>";
var chart3UnreportableText = "Unreportable";
var chart3Unreportable = "<bean:write name="overviewForm" property="system.stat.webAppStats.unreportableApps"/>";
var msgs = {"commonheadertitle":"<bean:message key="common.header.title" bundle="helpMsgs"/>","jmsconnected":"<bean:message key="common.header.jmsServerConnection" bundle="helpMsgs"/>","jmsnotconnected":"<bean:message key="common.header.jmsServerConnection" bundle="helpMsgs"/>","overviewtitle":"<bean:message key="overview.title" bundle="helpMsgs"/>","overviewdonutall": "<bean:message key="overview.donut.all" bundle="helpMsgs"/>","overviewdonutbatch":"<bean:message key="overview.donut.batch" bundle="helpMsgs"/>","overviewdonutwebapps":"<bean:message key="overview.donut.webapps" bundle="helpMsgs"/>"};
</script>

<html:form action="/overviewAction.do?method=cover">
	<div class="page-header row no-gutters py-4 mb-3 border-bottom">
		<div class="col-12 col-sm-4 text-center text-sm-left mb-0" >
			<h3 class="page-title" id="overviewtitle" title="<bean:message key="prompt.overview.title"/>"><bean:message key="prompt.overview.title" />
			<a data-toggle="tooltip" data-placement="right" href="<bean:write name="requestVar" property="contextPath"/>/overviewAction.do?method=resetCover" title="Reset Overview"><i class="fa fa-repeat fa-fw"></i></a>
			<i id='source-button-header' class='fa fa-question-circle fa-fw' title="Overview Help"></i>
			</h3>
		</div>
	</div>
<div class="row placeholders">
<tiles:insert page="/jsp/tiles/donutTile.jsp" /> 
</div>
<div class="row">
	<div class="col">
		<div class="card mb-4">
			<div class="table-responsive">
				<div class="card-body p-3 pb-3">
					<%Iterator it = overviewForm.getDatalist().iterator();%>
	 				<display:table name="sessionScope.overviewForm.datalist" uid="sytems" requestURI="/overviewAction.do?method=cover"  class="table table-striped">
						<%
							String refId = "0";
							if(it.hasNext()){
								SystemModel system = (SystemModel)it.next();
								refId = String.valueOf(system.getSystemRefId());
							}
						%>
						<display:column media="html" style="width: 2%;" class="lightCol">
						<logic:equal name="sytems" property="stat.allStats.naughtyApps" value="0">
							<i data-toggle="tooltip" class="fa fa-check text-success defaultCursor" title="No Errors Detected"></i>
						</logic:equal>
						<logic:notEqual name="sytems" property="stat.allStats.naughtyApps" value="0">
							<i data-toggle="tooltip" class="fa fa-warning text-danger defaultCursor" title="Error Detected"></i>
						</logic:notEqual>
						</display:column>
						<display:column title="System Name" property="name" style="width:20%" url="/systemDetailAction.do?method=editSystem&caller=overviewList" paramProperty="systemRefId" paramId="systemRefId"/>
						<display:column title="System Desc" property="systemDesc" style="width:50%" />
						<display:column title="Batch Applications" property="nbrBatchApps" style="width:10%" />
						<display:column title="Web Applications" property="nbrApplications" style="width:10%" />
						<display:column title="Show Charts" media="html" style="width:20%" >
						 	<div class="custom-control custom-radio">
								<html:radio styleClass="custom-control-input" styleId="showPie<%=String.valueOf(refId) %>" name="overviewForm" property="radio" value="<%=String.valueOf(refId) %>" onclick="changePie()" >
								</html:radio>
								<label class="custom-control-label" for="showPie<%=String.valueOf(refId) %>"></label>
							</div>
						</display:column>
						<display:setProperty name="paging.banner.all_items_found" value=""/>
					</display:table>
				</div>
			</div>
		</div>
	</div>
</div>
</html:form>