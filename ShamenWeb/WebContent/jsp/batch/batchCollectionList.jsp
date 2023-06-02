<%@ include file="/jsp/common/tagdeps.jsp"%>
<bean:page id="requestVar"  property="request"/>
<bean:define id="batchCollectionForm" name="batchCollectionForm" type="gov.doc.isu.shamen.form.BatchAppCollectionForm"/>
<bean:define id="controllerList" name="batchCollectionForm" property="controllerList"></bean:define>
<bean:define id="applicationList" name="batchCollectionForm" property="applicationList"></bean:define>
<bean:define id="systemList" name="batchCollectionForm" property="systemList"></bean:define>
<bean:define id="pocList" name="batchCollectionForm" property="pocList"></bean:define>
<script type="text/javascript" src="<bean:write name="requestVar" property="contextPath"/>/jsp/scripts/batch/batchCollectionList.js"></script>
<script type="text/javascript">
var noRecords = "<bean:write name='batchCollectionForm' property='noRecords'/>";
var restrict = "<bean:write name='batchCollectionForm' property='restrict'/>";
var goButtonDisable = "<bean:write name='batchCollectionForm' property='goButtonDisable'/>";
var msgs = {"commonheadertitle":"<bean:message key="common.header.title" bundle="helpMsgs"/>"
        ,"jmsconnected":"<bean:message key="common.header.jmsServerConnection" bundle="helpMsgs"/>"
        ,"jmsnotconnected":"<bean:message key="common.header.jmsServerConnection" bundle="helpMsgs"/>"
        ,"batchCollectionApplicationtitle":"<bean:message key="batchCollectionApplication.title" bundle="helpMsgs"/>"};

</script>
<html:form action="/batchCollectionAction.do?method=list&amp;caller=batchCollectionList">
	<div class="page-header row no-gutters py-4 mb-3 border-bottom">
		<div class="col-lg-12 col-sm-4 text-left mb-0">
			<h3 class="page-title" title="<bean:message key="prompt.batch.collection.list.title"/>" id="batchCollectionApplicationtitle"><bean:message key="prompt.batch.collection.list.title"/> <i id='source-button-header' class='fa fa-question-circle fa-fw '></i></h3>
		</div>
	</div>
<div class="row">
	<div class="col">
		<div class="card mb-4">
			<div class="table-responsive">
			  <div class="card-body p-3 pb-3">
				<dwarf:dataTable uid="collections" sort="list" formName="batchCollectionForm" requestURI="/batchCollectionAction.do?method=updateListPageData" className="table table-striped">			
					<display:column media="html" style="width: 2%;text-align:center;" class="activeCol">
						<logic:equal name="batchCollectionForm" property="noRecords" value="false">
									<logic:equal name="collections" property="scheduleModel.activeInd" value="Y">
										<i class="fa fa-calendar-check-o fa-fw text-success defaultCursor" data-toggle="tooltip" title="One or More Active Batch Schedules"></i>
									</logic:equal>
									<logic:equal name="collections" property="scheduleModel.activeInd" value="N">
										<logic:equal name="collections" property="scheduleModel.scheduleRefId" value="0">
											<i class="fa fa-calendar-minus-o fa-fw text-muted defaultCursor" data-toggle="tooltip" title="No Schedules Exist for Batch"></i>
										</logic:equal>
										<logic:notEqual name="collections" property="scheduleModel.scheduleRefId" value="0">
											<i class="fa fa-calendar-times-o fa-fw text-muted defaultCursor" data-toggle="tooltip" title="No Active Batch Schedules"></i>
										</logic:notEqual>
									</logic:equal>
								</logic:equal>
					</display:column>
					
					<display:column media="html" style="width: 2%;text-align:center;" class="activeCol">
						<logic:equal name="batchCollectionForm" property="noRecords" value="false">
							<html:hidden name="collections" property="controller.statusCd" styleId="statusVal"/>
							<logic:equal name="collections" property="controller.statusCd" value="CON">
								<i id="statusCol<bean:write name="collections" property="controller.controllerRefId"/>" class="fa fa-server fa-fw text-success defaultCursor" data-toggle="tooltip" title="Controller is Connected"></i>
							</logic:equal>
							<logic:equal name="collections" property="controller.statusCd" value="UNR">
								<i id="statusCol<bean:write name="collections" property="controller.controllerRefId"/>" class="fa fa-server fa-fw text-danger defaultCursor" data-toggle="tooltip" title="Controller is Unresponsive"></i>
							</logic:equal>
							<logic:equal name="collections" property="controller.statusCd" value="AWR">
										<i id="statusCol<bean:write name="collections" property="controller.controllerRefId"/>" class="fa fa-spin fa-fw fa-spinner defaultCursor" data-toggle="tooltip" title="Awaiting Response from Controller"></i>
							</logic:equal>
						</logic:equal>
					</display:column>
					<display:column media="html" style="width: 2%;text-align:center;">
						<logic:equal name="batchCollectionForm" property="noRecords" value="false">
							<logic:equal name="collections" property="scheduleModel.lastRunStatus.resultCd" value="MUL">
								<i class="fa fa-question-circle fa-fw text-muted defaultCursor" data-toggle="tooltip" title="Unable to determine - Multiple Schedules Detected"></i>
							</logic:equal>
							<logic:equal name="collections" property="scheduleModel.lastRunStatus.resultCd" value="PND">
								<i class="fa fa-clock-o fa-fw text-muted defaultCursor" data-toggle="tooltip" title="Last Run Result - Pending"></i>
							</logic:equal>
							<logic:equal name="collections" property="scheduleModel.lastRunStatus.resultCd" value="OFS">
								<i class="fa fa-exclamation-triangle fa-fw text-danger defaultCursor" data-toggle="tooltip" title="Last Run Result - Off Schedule"></i>
							</logic:equal>
							<logic:equal name="collections" property="scheduleModel.lastRunStatus.resultCd" value="SUC">
								<i class="fa fa-check fa-fw text-success defaultCursor" data-toggle="tooltip" title="Last Run Result - Completed Successfully"></i>
							</logic:equal>
							<logic:equal name="collections" property="scheduleModel.lastRunStatus.resultCd" value="INA">
								<logic:notEqual name="collections" property="scheduleModel.scheduleRefId" value="0">
									<i class="fa fa-minus-circle fa-fw text-muted defaultCursor" data-toggle="tooltip" title="No Run Result. Schedule is Inactive"></i>
								</logic:notEqual>
							</logic:equal>
							<logic:equal name="collections" property="scheduleModel.lastRunStatus.resultCd" value="UNK">
								<i class="fa fa-question-circle fa-fw text-muted defaultCursor" data-toggle="tooltip" title="Last Run Result - Unable to determine"></i>
							</logic:equal>
							<logic:equal name="collections" property="scheduleModel.lastRunStatus.resultCd" value="UNS">
								<i class="fa fa-ban fa-fw text-danger defaultCursor" data-toggle="tooltip" title="Last Run Result - Unsuccessful"></i>
							</logic:equal>
							<logic:equal name="collections" property="scheduleModel.lastRunStatus.resultCd" value="BLA">
								<i class="fa fa-hourglass-half fa-fw text-success defaultCursor" data-toggle="tooltip" title="Last Run Result - In progress"></i>
							</logic:equal>
						</logic:equal>
					</display:column>
					<dwarf:searchrow url="batchCollectionAction.do?method=search" useColumnInfo="true" ignoreColumns="0,1,2,5" buttonClass="btn btn-sm btn-primary btn-block" buttonTitle="Filter" buttonColSpan="1" buttonLast="true">
					</dwarf:searchrow>
					<dwarf:columndata  />
					<display:column media="html" style="width: 10%">
					<logic:equal name="batchCollectionForm" property="noRecords" value="false">
						<div class="btn-toolbar">
					 		<div class="btn-group dropdown">
					 			<button type="button" class="btn btn-sm btn-info dropdown-toggle" data-toggle="dropdown">Manage<span class="caret"></span></button>
								<ul class="dropdown-menu dropdown-menu-small" role="menu">
									<li class="dropdown-item">
										<a href="<bean:write name="requestVar" property="contextPath"/>/batchCollectionDetailAction.do?method=editBatch&caller=batchCollectionList&batchRefId=<bean:write name="collections" property="batchRefId"/>">View / Edit</a>
									</li>
									<li class="dropdown-item">
										<a href="<bean:write name="requestVar" property="contextPath"/>/batchCollectionDetailAction.do?method=duplicateBatch&caller=batchCollectionList&batchRefId=<bean:write name="collections" property="batchRefId"/>">Duplicate</a>
									</li>
									<logic:equal name="collections" property="assignedController" value="true">
										<logic:equal name="collections" property="controller.statusCd" value="CON">
														<li class="dropdown-item"><a href="javascript:populateModal('<bean:write name="collections" property="batchRefId"/>','<bean:write name="collections" property="name"/>','COL')">Launch Now</a></li>
										</logic:equal>
									</logic:equal>
									<logic:equal name="batchCollectionForm" property="restrict" value="false">
										<li class="dropdown-item">
											<a href="<bean:write name="requestVar" property="contextPath"/>/batchCollectionAction.do?method=deleteBatch&batchRefId=<bean:write name="collections" property="batchRefId"/>" onclick="return deleteConfirmation()">Delete</a>
										</li>
									</logic:equal>
								</ul>
							</div>
						</div>
					</logic:equal>
					</display:column>
					<display:setProperty name="paging.banner.item_name">Collections</display:setProperty>
					<display:setProperty name="paging.banner.items_name">Collections</display:setProperty>
					<display:setProperty name="show.pagesize.dropdown">true</display:setProperty>
					<display:setProperty name="pagesize.dropdown.label.one">Show</display:setProperty>
					<display:setProperty name="pagesize.dropdown.label.two">Rows</display:setProperty>
					<display:setProperty name="pagesize.dropdown.formName">batchCollectionForm</display:setProperty>
					<display:setProperty name="pagesize.dropdown.action">/batchCollectionAction</display:setProperty>
					<display:setProperty name="pagesize.dropdown.type">batchCollection</display:setProperty>
					<display:setProperty name="pagesize.dropdown.use.all">false</display:setProperty>
				</dwarf:dataTable>
			</div>
			</div>
		</div>
	</div>
</div>
	<jsp:include page="/jsp/batch/batchModal.jsp"></jsp:include>
</html:form>