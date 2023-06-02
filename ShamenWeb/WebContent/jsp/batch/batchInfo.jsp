<%@ include file="/jsp/common/tagdeps.jsp"%>
<bean:page id="requestVar"  property="request"/>
<bean:define id="batchForm" name="batchForm" type="gov.doc.isu.shamen.form.BatchAppForm"/>
<bean:define id="applicationList" name="batchForm" property="applicationList"></bean:define>
<bean:define id="controllerList" name="batchForm" property="controllerList"></bean:define>
<bean:define id="systemList" name="batchForm" property="systemList"></bean:define>
<bean:define id="batchTypes" name="batchForm" property="batchTypes"></bean:define>
<bean:define id="pocList" name="batchForm" property="pocList"></bean:define>
<script type="text/javascript" src="<bean:write name="requestVar" property="contextPath"/>/jsp/scripts/batch/batchInfo.js"></script>
<script type="text/javascript">
var caller = "<bean:write name='batchForm' property='caller'/>";
var msgs = {"commonheadertitle":"<bean:message key="common.header.title" bundle="helpMsgs"/>"
        ,"jmsconnected":"<bean:message key="common.header.jmsServerConnection" bundle="helpMsgs"/>"
        ,"jmsnotconnected":"<bean:message key="common.header.jmsServerConnection" bundle="helpMsgs"/>"
        ,"batchApplicationInfotitle":"<bean:message key="batchApplicationInfo.title" bundle="helpMsgs"/>"
		,"batchApplicationInfotype":"<bean:message key="batchApplicationInfo.type" bundle="helpMsgs"/>"
		,"batchApplicationInfofileLocation":"<bean:message key="batchApplicationInfo.fileLocation" bundle="helpMsgs"/>"
		,"batchApplicationInfofileName":"<bean:message key="batchApplicationInfo.fileName" bundle="helpMsgs"/>"
		,"scheduleInfotitle":"<bean:message key="scheduleInfo.title" bundle="helpMsgs"/>"
		,"scheduleInfoactivate":"<bean:message key="scheduleInfo.activate" bundle="helpMsgs"/>"
		,"scheduleInfostartDate":"<bean:message key="scheduleInfo.startDate" bundle="helpMsgs"/>"
		,"scheduleInfostartTime":"<bean:message key="scheduleInfo.startTime" bundle="helpMsgs"/>"
		,"scheduleInfofrequency":"<bean:message key="scheduleInfo.frequency" bundle="helpMsgs"/>"
		,"batchApplicationjobdurationexecution" : "<bean:message key="batchApplication.jobduration.execution" bundle="helpMsgs"/>"};
var partOfCollection = "<bean:write name='batchForm' property='batchApp.partOfCollection'/>";
var restrict = "<bean:write name='batchForm' property='restrict'/>";
var chartCsv = "<bean:write name="batchForm" property="chartCsv"/>";
var chartTitle = "";
</script>
<html:form action="/batchDetailAction.do?method=saveBatchApp">
	<div class="page-header row no-gutters py-4 mb-3 border-bottom">
		<div class="col-12 text-center text-sm-left mb-0"> 
			<html:hidden name="batchForm" property="scheduleChangeFlag" styleId="scheduleChangeFlag" value="false"/>
			<h3 class="page-title" id="batchApplicationInfotitle" title="<bean:message key="prompt.batch.info.title"/>"><logic:equal value="0" property="batchApp.batchRefId" name="batchForm">New Batch App</logic:equal><logic:notEqual value="0" property="batchApp.batchRefId" name="batchForm"><bean:write name="batchForm" property="batchApp.name"/></logic:notEqual> Info <i id='source-button-header' class='fa fa-question-circle fa-fw '></i></h3>
		</div>
	</div>
	<div class="row">
		<div class="col">
			<div class="card mb-4 p-3 pb-3">
				<div id="batchInfoDiv">
					<div class="row">
						<div class="col-lg-6">
							<div class="form-group">
								<label><bean:message key="prompt.batch.name"/></label>
								<html:text property="batchApp.name" styleClass="form-control" styleId="batchName" maxlength="45" errorStyleClass="form-control is-invalid" errorKey="org.apache.struts.action.ERROR"/>
							</div>
						</div>
						<div class="col-lg-6">
							<div class="form-group">
								<label><bean:message key="prompt.batch.description"/></label>
								<html:textarea property="batchApp.description" styleClass="form-control" styleId="description" cols="45" rows="2" errorStyleClass="form-control is-invalid" errorKey="org.apache.struts.action.ERROR"/>
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
						<div class="col-lg-6">
							<div class="form-group">
								<label class="bs-component" id="batchApplicationInfofileLocation" title="<bean:message key="prompt.batch.location"/>"><bean:message key="prompt.batch.location"/></label>
								<html:text property="batchApp.fileLocation" styleClass="form-control" styleId="fileLoc" errorStyleClass="form-control is-invalid" errorKey="org.apache.struts.action.ERROR"/>
							</div>
						</div>
						<div class="col-lg-6">
							<div class="form-group">
								<label class="bs-component" id="batchApplicationInfofileName" title="<bean:message key="prompt.batch.fileName"/>"><bean:message key="prompt.batch.fileName"/></label>
								<html:text property="batchApp.fileName" styleClass="form-control" styleId="fileName"  errorStyleClass="form-control is-invalid" errorKey="org.apache.struts.action.ERROR"/>
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
								<label class="bs-component" id="batchApplicationInfotype" title="<bean:message key="prompt.batch.type"/>"><bean:message key="prompt.batch.type"/></label>
								<html:select property="batchApp.type" styleClass="custom-select" styleId="type" errorStyleClass="custom-select is-invalid" errorKey="org.apache.struts.action.ERROR" >
									<html:options collection="batchTypes" property="code" labelProperty="description"/>
								</html:select>
							</div>
						</div>
						<logic:equal value="Y" name="batchForm" property="batchApp.partOfCollection">
							<div class="col-lg-6">
								<div class="form-group">
									<label>Notification</label>
									<h5 style="margin-top: 5px;">
										<label style="display: block;" class="label text-warning">Batch App is currently apart of a Batch Collection </label>
									</h5>
								</div>
							</div>
						</logic:equal>
					</div>
				</div>
				<logic:equal value="N" name="batchForm" property="batchApp.partOfCollection">
					<tiles:insert page="/jsp/batch/batchScheduleInfo.jsp" flush="true"/>
				</logic:equal>
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
					<h6 class="m-0 bs-component" id="batchApplicationjobdurationexecution" title="<bean:message key="prompt.infographics.jobduration.title"/>"><bean:message key="prompt.infographics.jobduration.title"/></h6>
				</div>
				<tiles:insert page="/jsp/stat/batchPerformance.jsp">
					<tiles:put name="noDataLabel" value="No Data"></tiles:put>
					<logic:equal value="" name="batchForm" property="chartCsv">
						<tiles:put name="hasChart" value="false"></tiles:put>
					</logic:equal>
					<logic:notEqual value="" name="batchForm" property="chartCsv">
						<tiles:put name="hasChart" value="true"></tiles:put>
					</logic:notEqual>
				</tiles:insert>
			</div>
		</div>
	</div>
	<tiles:insert page="/jsp/batch/batchStatusInfo.jsp" flush="true"/>
</html:form>