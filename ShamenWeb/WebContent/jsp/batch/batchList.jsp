<%@ include file="/jsp/common/tagdeps.jsp"%>
<bean:page id="requestVar" property="request" />
<bean:define id="batchForm" name="batchForm" type="gov.doc.isu.shamen.form.BatchAppForm" />
<bean:define id="controllerList" name="batchForm" property="controllerList"></bean:define>
<bean:define id="applicationList" name="batchForm" property="applicationList"></bean:define>
<bean:define id="batchTypes" name="batchForm" property="batchTypes"></bean:define>
<bean:define id="systemList" name="batchForm" property="systemList"></bean:define>
<bean:define id="pocList" name="batchForm" property="pocList"></bean:define>
<script type="text/javascript" src="<bean:write name="requestVar" property="contextPath"/>/jsp/scripts/batch/batchList.js"></script>
<script type="text/javascript">
	var noRecords = "<bean:write name='batchForm' property='noRecords'/>";
	var goButtonDisable = "<bean:write name='batchForm' property='goButtonDisable'/>";
	var restrict = "<bean:write name='batchForm' property='restrict'/>";
	var msgs = {
		"commonheadertitle" : "<bean:message key="common.header.title" bundle="helpMsgs"/>",
		"jmsconnected" : "<bean:message key="common.header.jmsServerConnection" bundle="helpMsgs"/>",
		"jmsnotconnected" : "<bean:message key="common.header.jmsServerConnection" bundle="helpMsgs"/>",
		"batchApplicationtitle" : "<bean:message key="batchApplication.title" bundle="helpMsgs"/>"
	};
</script>

<html:form action="/batchAction.do?method=list&amp;caller=batchList">
	<div class="page-header row no-gutters py-4 mb-3 border-bottom">
		<div class="col-md-6 text-left mb-0">
			<h3 class="page-title" id="batchApplicationtitle" title="<bean:message key="prompt.batch.list.title" />">
				<bean:message key="prompt.batch.list.title" />
				<i id='source-button-header' class='fa fa-question-circle fa-fw '></i>
			</h3>
		</div>
		<div class="col-md-6 mb-0">
			<div class="custom-control custom-toggle float-md-right">
				<input type="checkbox" name="showCollectionsInList" value='<bean:write name="batchForm" property="showCollectionsInList"/>' id="viewCollectionsMem" class="custom-control-input enableWithRestrict text-right" onchange="changeList()" /> <label
					class="custom-control-label" for="viewCollectionsMem">Show Collection Members</label>
			</div>
		</div>
	</div>
	<div class="row">
		<div class="col">
			<div class="card mb-4">
				<div class="table-responsive">
					<div class="card-body p-3">
						<dwarf:dataTable uid="batches" sort="list" formName="batchForm" requestURI="/batchAction.do?method=updateListPageData"  className="table table-striped" style="none">
							<display:column media="html" style="width: 2%;text-align:center;">
								<logic:equal name="batchForm" property="noRecords" value="false">
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
								</logic:equal>
							</display:column>
							<display:column media="html" style="width: 2%;text-align:center;">
								<logic:equal name="batchForm" property="noRecords" value="false">
									<html:hidden name="batches" property="controller.statusCd" styleId="statusVal" />
									<logic:equal name="batches" property="controller.statusCd" value="CON">
										<i id="statusCol<bean:write name="batches" property="controller.controllerRefId"/>" class="fa fa-server fa-fw text-success defaultCursor" data-toggle="tooltip" title="Controller is Connected"></i>
									</logic:equal>
									<logic:equal name="batches" property="controller.statusCd" value="UNR">
										<i id="statusCol<bean:write name="batches" property="controller.controllerRefId"/>" class="fa fa-server fa-fw text-danger defaultCursor" data-toggle="tooltip" title="Controller is Unresponsive"></i>
									</logic:equal>
									<logic:equal name="batches" property="controller.statusCd" value="AWR">
										<i id="statusCol<bean:write name="batches" property="controller.controllerRefId"/>" class="fa fa-spin fa-fw fa-spinner defaultCursor" data-toggle="tooltip" title="Awaiting Response from Controller"></i>
									</logic:equal>
								</logic:equal>
							</display:column>
							<display:column media="html" style="width: 2%;text-align:center;">
								<logic:equal name="batchForm" property="noRecords" value="false">
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
										<logic:notEqual name="batches" property="scheduleModel.scheduleRefId" value="0">
											<i class="fa fa-minus-circle fa-fw text-muted defaultCursor" data-toggle="tooltip" title="No Run Result. Schedule is Inactive"></i>
										</logic:notEqual>
									</logic:equal>
									<logic:equal name="batches" property="scheduleModel.lastRunStatus.resultCd" value="UNK">
										<i class="fa fa-question-circle fa-fw text-muted defaultCursor" data-toggle="tooltip" title="Last Run Result - Unable to determine"></i>
									</logic:equal>
									<logic:equal name="batches" property="scheduleModel.lastRunStatus.resultCd" value="UNS">
										<i class="fa fa-ban fa-fw text-danger defaultCursor" data-toggle="tooltip" title="Last Run Result - Unsuccessful"></i>
									</logic:equal>
									<logic:equal name="batches" property="scheduleModel.lastRunStatus.resultCd" value="BLA">
										<i class="fa fa-hourglass-half fa-fw text-success defaultCursor" data-toggle="tooltip" title="Last Run Result - In progress"></i>
									</logic:equal>
								</logic:equal>
							</display:column>
							<display:column media="html" style="width: 2%;text-align:center;">
								<logic:equal name="batchForm" property="noRecords" value="false">
									<logic:equal name="batches" property="partOfCollection" value="Y">
										<i class="fa fa-gears fa-fw text-warning defaultCursor" data-toggle="tooltip" title="Is part of Collection"></i>
									</logic:equal>
								</logic:equal>
							</display:column>
							<dwarf:searchrow url="batchAction.do?method=search" useColumnInfo="true" ignoreColumns="0,1,2,3,9" buttonClass="btn btn-sm btn-primary btn-block" buttonColSpan="1" buttonLast="true" buttonTitle="Filter">
							</dwarf:searchrow>
							<dwarf:columndata />
							<display:column media="html" style="width: 8%">
								<logic:equal name="batchForm" property="noRecords" value="false">
									<div class="btn-toolbar">
										<div class="btn-group dropdown">
											<button type="button" class="btn btn-sm btn-info dropdown-toggle" data-toggle="dropdown">
												Manage<span class="caret"></span>
											</button>
											<ul class="dropdown-menu dropdown-menu-small" role="menu">
												<li class="dropdown-item"><a href="<bean:write name="requestVar" property="contextPath"/>/batchDetailAction.do?method=editBatch&caller=batchList&batchRefId=<bean:write name="batches" property="batchRefId"/>">View / Edit</a></li>
												<li class="dropdown-item"><a href="<bean:write name="requestVar" property="contextPath"/>/batchDetailAction.do?method=duplicateBatch&caller=batchList&batchRefId=<bean:write name="batches" property="batchRefId"/>">Duplicate Batch</a></li>
												<logic:equal name="batches" property="assignedController" value="true">
													<logic:equal name="batches" property="controller.statusCd" value="CON">
														<li class="dropdown-item"><a href="javascript:populateModal('<bean:write name="batches" property="batchRefId"/>','<bean:write name="batches" property="name"/>','BAT')">Launch Now</a></li>
													</logic:equal>
												</logic:equal>
												<logic:equal name="batchForm" property="restrict" value="false">
													<li class="dropdown-item"><a href="<bean:write name="requestVar" property="contextPath"/>/batchAction.do?method=deleteBatch&batchRefId=<bean:write name="batches" property="batchRefId"/>" onclick="return deleteConfirmation()">Delete
															Batch App</a></li>
												</logic:equal>
											</ul>
										</div>
									</div>
								</logic:equal>
							</display:column>
							<display:setProperty name="paging.banner.item_name">Batch App</display:setProperty>
							<display:setProperty name="paging.banner.items_name">Batch Apps</display:setProperty>
							<display:setProperty name="show.pagesize.dropdown">true</display:setProperty>
							<display:setProperty name="pagesize.dropdown.label.one">Show</display:setProperty>
							<display:setProperty name="pagesize.dropdown.label.two">Rows</display:setProperty>
							<display:setProperty name="pagesize.dropdown.formName">batchForm</display:setProperty>
							<display:setProperty name="pagesize.dropdown.action">/batchAction</display:setProperty>
							<display:setProperty name="pagesize.dropdown.type">batchApp</display:setProperty>
							<display:setProperty name="pagesize.dropdown.use.all">false</display:setProperty>
						</dwarf:dataTable>
					</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="/jsp/batch/batchModal.jsp"></jsp:include>
</html:form>