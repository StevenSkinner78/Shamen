<%@ include file="/jsp/common/tagdeps.jsp"%>

<bean:page id="requestVar"  property="request"/>
<bean:define id="statForm" name="statForm"
	type="gov.doc.isu.shamen.form.StatForm" />

<script type="text/javascript" 	src="<bean:write name="requestVar" property="contextPath"/>/jsp/scripts/stat/statDetail.js"></script>
<script type="text/javascript">
var caller = "statsInfo";
var listType = "<bean:write name="statForm" property="listType"/>";
var showChart = "<bean:write name="statForm" property="showChartString"/>";
var selectedLabels = "<bean:write name="statForm" property="selectedLabels"/>";
var colors = "<bean:write name="statForm" property="colors"/>";
var chartTitle = "<bean:write name="statForm" property="chartTitle"/>";
var msgs = {"commonheadertitle":"<bean:message key="common.header.title" bundle="helpMsgs"/>",
        "jmsconnected":"<bean:message key="common.header.jmsServerConnection" bundle="helpMsgs"/>",
        "jmsnotconnected":"<bean:message key="common.header.jmsServerConnection" bundle="helpMsgs"/>",
        "infographicstitle":"<bean:message key="infographics.title" bundle="helpMsgs"/>",
        "jobdurationinfo":"<bean:message key="jobduration.info" bundle="helpMsgs"/>",
        "jobdurationtitle":"<bean:message key="jobduration.title" bundle="helpMsgs"/>",
        "appsbyuserinfo":"<bean:message key="appsbyuser.info" bundle="helpMsgs"/>",
        "appsbyusertitle":"<bean:message key="appsbyuser.title" bundle="helpMsgs"/>",
        "syshierarchyinfo":"<bean:message key="syshierarchy.info" bundle="helpMsgs"/>",
        "syshierarchytitle":"<bean:message key="syshierarchy.title" bundle="helpMsgs"/>"};
var chartCsv = "<bean:write name="statForm" property="chartCsv"/>";
var systemData = "<bean:write name="statForm" property="systemData"/>";
var pathToAction = "<bean:write name="requestVar" property="contextPath"/>/statDetailAction.do?method=showStats";
</script>
<html:form action="/statDetailAction.do?method=resetStats">
	<logic:equal value="start" name="statForm" property="listType">
		<div class="page-header row no-gutters py-4 mb-3 border-bottom">
			<div class="col-12 text-center text-sm-left mb-0"> 
				<h3 class="page-title" id="infographicstitle" title="<bean:message key="prompt.infographics.title"/>">
					<bean:message key="prompt.infographics.title"/>
					<i id='source-button-header' class='fa fa-question-circle fa-fw '></i>
				</h3>
			</div>
		</div>
		<tiles:insert page="/jsp/stat/infographics.jsp">
		</tiles:insert>
	</logic:equal>
	<logic:equal value="batch" name="statForm" property="listType">
		<div class="page-header row no-gutters py-4 mb-3 border-bottom">
			<div class="col-12 text-center text-sm-left mb-0"> 
				<h3 class="page-title" id="jobdurationtitle" title="<bean:message key="prompt.infographics.jobduration.title"/>">
					<bean:message key="prompt.infographics.jobduration.title"/>
					<a data-toggle="tooltip" data-placement="right"  href="<bean:write name="requestVar" property="contextPath"/>/statDetailAction.do?method=resetStats" title="Reload Stats Page"><i class="fa fa-refresh fa-fw"></i></a>
					<i id='source-button-header' class='fa fa-question-circle fa-fw '></i>
				</h3>
			</div>
		</div>
		<div class="row">
			<div class="col">
				<div class="card mb-4 p-3 pb-3">
					<tiles:insert page="/jsp/stat/batchPerformance.jsp">
						<tiles:put name="noDataLabel" value="No Data Selected"></tiles:put>
						<logic:equal value="" name="statForm" property="chartCsv">
							<tiles:put name="hasChart" value="false"></tiles:put>
						</logic:equal>
						<logic:notEqual value="" name="statForm" property="chartCsv">
							<tiles:put name="hasChart" value="true"></tiles:put>
						</logic:notEqual>
					</tiles:insert>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col">
				<div class="card mb-4 p-3 pb-3">
					<div class="card-header mb-1 border-bottom bg-light">
						<h6 class="m-0"><bean:message key="prompt.batch.list.title"/></h6>
					</div>
					<tiles:insert page="/jsp/tiles/batchListTile.jsp">
						<tiles:put value="true" name="chartCheck"></tiles:put>
					</tiles:insert>
				</div>
			</div>
		</div>
	</logic:equal>

<logic:equal value="user" name="statForm" property="listType">
	<tiles:insert page="/jsp/stat/userGraphTile.jsp"/>
</logic:equal>
<logic:equal value="system" name="statForm" property="listType">
	<tiles:insert page="/jsp/stat/systemGraphTile.jsp"/>
</logic:equal>
<logic:equal value="systemTree" name="statForm" property="listType">
	<tiles:insert page="/jsp/stat/systemTreeGraphTile.jsp">
		<tiles:put name="isSysDetail" value="false"></tiles:put>
		<logic:equal value="" name="statForm" property="systemData">
			<tiles:put name="hasChart" value="false"></tiles:put>
		</logic:equal>
		<logic:notEqual value="" name="statForm" property="systemData">
			<tiles:put name="hasChart" value="true"></tiles:put>
		</logic:notEqual>
	</tiles:insert>
</logic:equal>
</html:form>