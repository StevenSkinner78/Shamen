<%@ include file="/jsp/common/tagdeps.jsp"%>
<bean:page id="requestVar"  property="request"/>
<bean:define id="batchCollectionForm" name="batchCollectionForm" type="gov.doc.isu.shamen.form.BatchAppCollectionForm"/>
<bean:define id="controllerList" name="batchCollectionForm" property="controllerList"></bean:define>
<bean:define id="applicationList" name="batchCollectionForm" property="applicationList"></bean:define>
<bean:define id="systemList" name="batchCollectionForm" property="systemList"></bean:define>
<bean:define id="pocList" name="batchCollectionForm" property="pocList"></bean:define>
<bean:define id="batchAsCodeList" name="batchCollectionForm" property="batchAsCodeList" toScope="page"></bean:define>
<script type="text/javascript" src="<bean:write name="requestVar" property="contextPath"/>/jsp/scripts/batch/batchCollections.js"></script>
<script type="text/javascript">
var shwAvaBtch = "<bean:write name='batchCollectionForm' property='showBatchApps'/>"
var addBtchErr = "<bean:write name='batchCollectionForm' property='addBatchAppsError'/>"
var caller = "<bean:write name='batchCollectionForm' property='caller'/>";
var restrict = "<bean:write name='batchCollectionForm' property='restrict'/>";
var chartCsv = "<bean:write name="batchCollectionForm" property="chartCsv"/>";
var chartTitle = "";
var msgs =  {"commonheadertitle":"<bean:message key="common.header.title" bundle="helpMsgs"/>"
        ,"jmsconnected":"<bean:message key="common.header.jmsServerConnection" bundle="helpMsgs"/>"
        ,"jmsnotconnected":"<bean:message key="common.header.jmsServerConnection" bundle="helpMsgs"/>"
		,"batchApplicationCollectionInfotitle":"<bean:message key="batchApplicationCollectionInfo.title" bundle="helpMsgs"/>"
		,"scheduleInfotitle":"<bean:message key="scheduleInfo.title" bundle="helpMsgs"/>"
		,"scheduleInfoactivate":"<bean:message key="scheduleInfo.activate" bundle="helpMsgs"/>"
		,"scheduleInfostartDate":"<bean:message key="scheduleInfo.startDate" bundle="helpMsgs"/>"
		,"scheduleInfostartTime":"<bean:message key="scheduleInfo.startTime" bundle="helpMsgs"/>"
		,"scheduleInfofrequency":"<bean:message key="scheduleInfo.frequency" bundle="helpMsgs"/>"
		,"batchApplicationjobdurationexecution" : "<bean:message key="batchApplication.jobduration.execution" bundle="helpMsgs"/>"};
</script>
<html:form action="/batchCollectionDetailAction.do?method=saveBatchApp">
	<div class="page-header row no-gutters py-4 mb-3 border-bottom">
		<div class="col-12 text-center text-sm-left mb-0"> 
			<html:hidden name="batchCollectionForm" property="scheduleChangeFlag" styleId="scheduleChangeFlag" value="false"/>
				<h3 class="page-title" id="batchApplicationCollectionInfotitle" title="<bean:message key="prompt.batch.collection.info.title"/>">
					<logic:equal value="0" property="batchApp.batchRefId" name="batchCollectionForm">New Batch Collection</logic:equal>
					<logic:notEqual value="0" property="batchApp.batchRefId" name="batchCollectionForm">
						<bean:write name="batchCollectionForm" property="batchApp.name"/>
					</logic:notEqual> Info <i id='source-button-header' class='fa fa-question-circle fa-fw '></i>
				</h3>
			</div>
		</div>
	<div class="row">
		<div class="col">
			<div class="card mb-4 p-3 pb-3">
				<div id="batchInfoDiv">
					<div class="row">
						<div class="col-lg-6">
							<div class="form-group">
								<label><bean:message key="prompt.batch.collection.name"/></label>
								<html:text property="batchApp.name" styleClass="form-control" styleId="batchName" maxlength="45" errorStyleClass="form-control is-invalid" errorKey="org.apache.struts.action.ERROR"/>
							</div>
						</div>
						<div class="col-lg-6">
							<div class="form-group">
								<label><bean:message key="prompt.batch.collection.description"/></label>
								<html:textarea property="batchApp.description" styleClass="form-control" styleId="description" cols="45" rows="2" errorStyleClass="form-control is-invalid" errorKey="org.apache.struts.action.ERROR"/>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-lg-6">
							<div class="form-group">
								<label><bean:message key="prompt.system.name"/></label>
								<html:select property="batchApp.system.systemRefId" styleClass="custom-select" styleId="systemRefId" errorStyleClass="custom-select is-invalid" errorKey="org.apache.struts.action.ERROR" >
									<html:options collection="systemList" property="code" labelProperty="description"/>
								</html:select>
							</div>
						</div>
						<div class="col-lg-6">
							<div class="form-group">
								<label><bean:message key="prompt.app.name"/></label>
								<html:select property="batchApp.application.applicationRefId" styleClass="custom-select" styleId="applicationRefId" errorStyleClass="custom-select is-invalid" errorKey="org.apache.struts.action.ERROR" >
									<html:options collection="applicationList" property="code" labelProperty="description"/>
								</html:select>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-lg-6">
							<div class="form-group">
								<label><bean:message key="prompt.controller.name"/></label>
								<html:select property="batchApp.controller.controllerRefId" styleClass="custom-select" styleId="controllerRefId" errorStyleClass="custom-select is-invalid" errorKey="org.apache.struts.action.ERROR" >
									<html:options collection="controllerList" property="code" labelProperty="description"/>
								</html:select>
							</div>
						</div>
						<div class="col-lg-6">
							<div class="form-group">
								<label><bean:message key="common.label.poc"/></label>
								<html:select property="batchApp.pointOfContact.userRefId" styleClass="custom-select" styleId="pointOfContact" errorStyleClass="custom-select is-invalid" errorKey="org.apache.struts.action.ERROR" >
									<html:options collection="pocList" property="code" labelProperty="description"/>
								</html:select>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-lg-1">
							<div class="form-group">
								<label><bean:message key="prompt.batch.collection.apps.title"/></label>
							</div>
						</div>
						<isu:multiTransferBox  className="multi custom-select" listName="batchAsCodeList" secondSelectId="secondSelectId" firstSelectId="firstSelectId" selectedProperty="selectedValues"/>
					</div>
				</div>
				<tiles:insert page="/jsp/batch/batchScheduleInfoCollection.jsp" flush="true"/>
				<div class="card-footer border-top">
					<div class="row">
						<div class="col">
							<tiles:insert page="/jsp/tiles/buttons.jsp" flush="true"/>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="row">
		<div class="col">
			<div class="card mb-4 p-3 pb-3">
				<div class="card-header mb-1 border-bottom bg-light">
					<h6 class="sub-header bs-component" id="batchApplicationjobdurationexecution" title="<bean:message key="prompt.infographics.jobduration.title"/>"><bean:message key="prompt.infographics.jobduration.title"/></h6>
				</div>
				<tiles:insert page="/jsp/stat/batchPerformance.jsp">
					<tiles:put name="noDataLabel" value="No Data"></tiles:put>
					<logic:equal value="" name="batchCollectionForm" property="chartCsv">
						<tiles:put name="hasChart" value="false"></tiles:put>
					</logic:equal>
					<logic:notEqual value="" name="batchCollectionForm" property="chartCsv">
						<tiles:put name="hasChart" value="true"></tiles:put>
					</logic:notEqual>
				</tiles:insert>
			</div>
		</div>
	</div>
	<tiles:insert page="/jsp/batch/batchStatusInfoCollection.jsp" flush="true"/>
</html:form>