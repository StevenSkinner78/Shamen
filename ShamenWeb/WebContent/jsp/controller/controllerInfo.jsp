<%@ include file="/jsp/common/tagdeps.jsp"%>
<bean:page id="requestVar"  property="request"/>
<bean:define id="controllerForm" name="controllerForm" type="gov.doc.isu.shamen.form.ControllerForm"/>
<script type="text/javascript" src="<bean:write name="requestVar" property="contextPath"/>/jsp/scripts/controller/controllerInfo.js"></script>
<script type="text/javascript">
var status = "<bean:write name='controllerForm' property='controller.status'/>"
var controllerRefId = "<bean:write name='controllerForm' property='controller.controllerRefId'/>";
var restrict = "<bean:write name='controllerForm' property='restrict'/>";
var msgs = {"commonheadertitle":"<bean:message key="common.header.title" bundle="helpMsgs"/>"
        ,"jmsconnected":"<bean:message key="common.header.jmsServerConnection" bundle="helpMsgs"/>"
        ,"jmsnotconnected":"<bean:message key="common.header.jmsServerConnection" bundle="helpMsgs"/>"
        ,"controllerInfotitle":"<bean:message key="controllerInfo.title" bundle="helpMsgs"/>"
		,"controllerInfocontrollerName":"<bean:message key="controllerInfo.controllerName" bundle="helpMsgs"/>"
		,"controllerInfocontrollerAddress":"<bean:message key="controllerInfo.controllerAddress" bundle="helpMsgs"/>"
		,"controllerInfodefaultBatchLocation":"<bean:message key="controllerInfo.defaultBatchLocation" bundle="helpMsgs"/>"
		,"controllerInfocontrollerStatus":"<bean:message key="controllerInfo.controllerStatus" bundle="helpMsgs"/>"};
</script>
<html:form action="/controllerDetailAction.do?method=saveController">
	<div class="page-header row no-gutters py-4 mb-3 border-bottom">
		<div class="col-12 col-sm-4 text-center text-sm-left mb-0"> 
			<h3 class="page-title" id="controllerInfotitle" title="<bean:message key="prompt.controller.info.title"/>"><bean:message key="prompt.controller.info.title"/> <i id='source-button-header' class='fa fa-question-circle fa-fw '></i></h3>
		</div>
	</div>
	<div class="row">
		<div class="col">
			<div class="card mb-4 p-3 pb-3">
				<div class="row">
					<div class="col-lg-6">
						<div class="form-group">
							<label class="bs-component" id="controllerInfocontrollerName" title="<bean:message key="prompt.controller.name"/>"><bean:message key="prompt.controller.name"/></label>
							<html:text property="controller.name" styleClass="form-control" styleId="controllerName" maxlength="35" errorStyleClass="form-control is-invalid" errorKey="org.apache.struts.action.ERROR"/>
						</div>
					</div>
					<div class="col-lg-6">
						<div class="form-group">
							<label class="bs-component" id="controllerInfocontrollerAddress"  title="<bean:message key="prompt.controller.address"/>"><bean:message key="prompt.controller.address"/></label>
							<html:text property="controller.address" styleClass="form-control" styleId="controllerAddress" maxlength="18"  errorStyleClass="form-control is-invalid" errorKey="org.apache.struts.action.ERROR"/>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-lg-6">
						<div class="form-group">
							<label class="bs-component" id="controllerInfodefaultBatchLocation" title="<bean:message key="prompt.controller.default.address"/>"><bean:message key="prompt.controller.default.address"/></label>
							<html:text property="controller.defaultAddress" styleClass="form-control" styleId="defaultAdress" maxlength="35" errorStyleClass="form-control is-invalid" errorKey="org.apache.struts.action.ERROR"/>
						</div>
					</div>	
					<logic:greaterThan name="controllerForm" property="controller.controllerRefId" value="0">
							<div class="col-lg-6">
								<div class="form-group">
									<label class="bs-component" id="controllerInfocontrollerStatus" title="<bean:message key="prompt.controller.status"/>"><bean:message key="prompt.controller.status"/></label>
									<h4 style="margin-top: 5px;">
										<label id="statusDisplayCol" style="display: block;"><bean:write name="controllerForm" property="controller.status"/></label>
									</h4>
								</div>
							</div>
					</logic:greaterThan>
				</div>
				<tiles:insert page="/jsp/tiles/buttons.jsp" flush="true"/>
			</div>
		</div>
	</div>
<logic:greaterThan value="0" name="controllerForm" property="controller.controllerRefId">
	<div class="row">
		<div class="col">
			<div class="card mb-4 p-3 pb-3">
				<div class="card-header mb-1 border-bottom bg-light">
					<h5 class="m-0"><bean:message key="prompt.batch.list.title"/></h5>
				</div>
				<div class="table-responsive">
					<div class="card-body p-3 pb-3">
						<display:table name="sessionScope.controllerForm.controller.batchApps" requestURI="/controllerDetailAction.do?method=updateListPageData" uid="batches" class="table table-striped" export="false" pagesize="25">
							<display:column media="html" style="width: 2%;text-align:center;" class="activeCol">
								<logic:equal name="batches" property="scheduleModel.activeInd" value="Y">
									<i class="fa fa-calendar-check-o fa-fw text-success defaultCursor" data-toggle="tooltip" title="One or More Active Batch Schedules"></i>
								</logic:equal>
								<logic:equal name="batches" property="scheduleModel.activeInd" value="N">
									<logic:equal name="batches" property="scheduleModel.scheduleRefId" value="0">
										<i class="fa fa-calendar-minus-o fa-fw text-muted defaultCursor" data-toggle="tooltip" title="No Schedules Exist for Batch"></i>
									</logic:equal>
									<logic:notEqual name="batches" property="scheduleModel.scheduleRefId" value="0">
										<i class="fa fa-calendar-times-o fa-fw text-muted defaultCursor" data-toggle="tooltip" title="No Active Batch Schedules"></i>
									</logic:notEqual>
								</logic:equal>
							</display:column>
							<display:column media="html" style="width: 2%;text-align:center;" class="activeCol">
								<logic:equal name="batches" property="scheduleModel.lastRunStatus.resultCd" value="MUL">
									<i class="fa fa-question-circle fa-fw text-muted defaultCursor" data-toggle="tooltip" title="Unable to determine - Multiple Schedules Detected"></i>
								</logic:equal>
								<logic:equal name="batches" property="scheduleModel.lastRunStatus.resultCd" value="PND">
									<i class="fa fa-clock-o fa-fw text-muted defaultCursor" data-toggle="tooltip" title="Last Run Result - Pending"></i>
								</logic:equal>
								<logic:equal name="batches" property="scheduleModel.lastRunStatus.resultCd" value="OFS">
									<i class="fa fa-exclamation-triangle fa-fw text-danger defaultCursor" data-toggle="tooltip" title="Last Run Result - Off Schedule"></i>
								</logic:equal>
								<logic:equal name="batches" property="scheduleModel.lastRunStatus.resultCd" value="SUC">
									<i class="fa fa-check fa-fw text-success defaultCursor" data-toggle="tooltip" title="Last Run Result - Completed Successfully"></i>
								</logic:equal>
								<logic:equal name="batches" property="scheduleModel.lastRunStatus.resultCd" value="INA">
									<i class="fa fa-minus-circle fa-fw text-muted defaultCursor" data-toggle="tooltip" title="No Run Result. Schedule is Inactive"></i>
								</logic:equal>
								<logic:equal name="batches" property="scheduleModel.lastRunStatus.resultCd" value="UNK">
									<i class="fa fa-question-circle fa-fw text-muted defaultCursor" data-toggle="tooltip" title="Last Run Result - Unable to determine"></i>
								</logic:equal>
								<logic:equal name="batches" property="scheduleModel.lastRunStatus.resultCd" value="UNS">
									<i class="fa fa-ban fa-fw text-danger defaultCursor" data-toggle="tooltip" title="Last Run Result - Unsuccessful"></i>
								</logic:equal>
								<logic:equal name="batches" property="scheduleModel.lastRunStatus.resultCd" value="BLA">
									<i class="fa fa-hourglass-half fa-fw text-success-light defaultCursor" data-toggle="tooltip" title="Last Run Result - In progress"></i>
								</logic:equal>
							</display:column>
							<display:column title="Batch Name" class="enableWithRestrict">
								<logic:equal value="COL" name="batches" property="type">
									<c:set var="url" value="/batchCollectionDetailAction.do?method=editBatch&caller=controllerInfo"/>
								</logic:equal>
								<logic:notEqual value="COL" name="batches" property="type">
									<c:set var="url" value="/batchDetailAction.do?method=editBatch&caller=controllerInfo"/>
								</logic:notEqual>
								<a href="<bean:write name="requestVar" property="contextPath"/><bean:write name='url'/>&batchRefId=<bean:write name="batches" property="batchRefId"/>">
									<bean:write name="batches" property="name"/>
								</a>
							</display:column>
							<display:column title="Frequency"style="width: 15%" property="scheduleModel.frequencyDesc"/>
							<display:column title="Type"style="width: 15%" property="typeDesc"/>
							<display:column media="html" style="width: 10%;text-align:center;">
								<div class="btn-toolbar">
					 				<div class="btn-group dropdown">
					 					<button type="button" class="btn btn-sm btn-info dropdown-toggle enableWithRestrict" data-toggle="dropdown">Manage<span class="caret"></span></button>
										<ul class="dropdown-menu dropdown-menu-small" role="menu">
											<logic:equal value="COL" name="batches" property="type">
												<c:set var="url" value="/batchCollectionDetailAction.do?method=editBatch&caller=controllerInfo"/>
											</logic:equal>
											<logic:notEqual value="COL" name="batches" property="type">
												<c:set var="url" value="/batchDetailAction.do?method=editBatch&caller=controllerInfo"/>
											</logic:notEqual>
											<li class="dropdown-item">
												<a href="<bean:write name="requestVar" property="contextPath"/><bean:write name='url'/>&batchRefId=<bean:write name="batches" property="batchRefId"/>">View / Edit</a>
											</li>
											<logic:equal name="controllerForm" property="controller.status" value="Connected">
												<li class="dropdown-item">
													<a href="javascript:populateModal('<bean:write name="batches" property="batchRefId"/>','<bean:write name="batches" property="name"/>','<bean:write name="batches" property="type"/>')">Launch Now</a>
												</li>
											</logic:equal>
										</ul>
									</div>
								</div>
							</display:column>
							<display:setProperty name="paging.banner.item_name">Batch App</display:setProperty>
							<display:setProperty name="paging.banner.items_name">Batch Apps</display:setProperty>
						</display:table>
					</div>
				</div>
			</div>
		</div>
	</div>
</logic:greaterThan>
	<jsp:include page="/jsp/batch/batchModal.jsp"></jsp:include>
</html:form>