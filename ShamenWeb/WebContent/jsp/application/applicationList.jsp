<%@ include file="/jsp/common/tagdeps.jsp"%>
<bean:page id="requestVar" property="request" />
<bean:define id="applicationForm" name="applicationForm" type="gov.doc.isu.shamen.form.ApplicationForm" />
<bean:define id="statusIndList" name="applicationForm" property="statusIndList" />
<bean:define id="confirmedStatusIndList" name="applicationForm" property="confirmedStatusIndList" />
<bean:define id="pocList" name="applicationForm" property="pocList" />
<script type="text/javascript" src="<bean:write name="requestVar" property="contextPath"/>/jsp/scripts/application/applicationList.js"></script>
<script type="text/javascript">
var noRecords = "<bean:write name='applicationForm' property='noRecords'/>";
var goButtonDisable = "<bean:write name='applicationForm' property='goButtonDisable'/>";
var restrict = "<bean:write name='applicationForm' property='restrict'/>";
var msgs = {"commonheadertitle":"<bean:message key="common.header.title" bundle="helpMsgs"/>","jmsconnected":"<bean:message key="common.header.jmsServerConnection" bundle="helpMsgs"/>","jmsnotconnected":"<bean:message key="common.header.jmsServerConnection" bundle="helpMsgs"/>"
        ,"webApplicationtitle":"<bean:message key="webApplication.title" bundle="helpMsgs"/>"};
</script>
<html:form action="/applicationAction.do?method=list&amp;caller=applicationList">
	<div class="page-header row no-gutters py-4 mb-3 border-bottom">
		<div class="col-12 col-sm-4 text-center text-sm-left mb-0">
			<h3 class="page-title" id="webApplicationtitle" title="<bean:message key="prompt.app.list.title"/>">
				<bean:message key="prompt.app.list.title" />
				<a data-toggle="tooltip" data-placement="right" href="<bean:write name="requestVar" property="contextPath"/>/applicationAction.do?method=updateApplicationStatuses" title="Re-establish the Web Apps status."> <i id="refHead"
					class="fa fa-exchange fa-fw"></i></a> <i id='source-button-header' class='fa fa-question-circle fa-fw '></i>
			</h3>
		</div>
	</div>

	<div class="row">
		<div class="col">
			<div class="card mb-4">
				<div class="table-responsive">
					<div class="card-body p-3 pb-3">
						<dwarf:dataTable uid="webApps" sort="list" formName="applicationForm" requestURI="/applicationAction.do?method=updateListPageData" keepStatus="false" className="table table-striped">
							<display:column media="html" style="width: 2%;text-align:center;" class="statusCol">
								<logic:equal name="applicationForm" property="noRecords" value="false">
									<html:hidden name="webApps" property="statusInd" styleId="statusVal" />
									<logic:equal name="webApps" property="statusInd" value="ACT">
										<i id="statusCol<bean:write name="webApps" property="applicationRefId"/>" data-toggle="tooltip" class="fa fa-circle fa-fw text-success defaultCursor" title="Status verified - Application is Active"></i>
									</logic:equal>
									<logic:equal name="webApps" property="statusInd" value="SUP">
										<i id="statusCol<bean:write name="webApps" property="applicationRefId"/>" data-toggle="tooltip" class="fa fa-circle fa-fw text-success defaultCursor" title="Status verified - Application is Suspended"></i>
									</logic:equal>
									<logic:equal name="webApps" property="statusInd" value="INF">
										<i id="statusCol<bean:write name="webApps" property="applicationRefId"/>" data-toggle="tooltip" class="fa fa-circle fa-fw text-success defaultCursor" title="Status verified - Application has Information"></i>
									</logic:equal>
									<logic:equal name="webApps" property="statusInd" value="UNR">
										<i id="statusCol<bean:write name="webApps" property="applicationRefId"/>" data-toggle="tooltip" class="fa fa-circle fa-fw text-danger defaultCursor" title="Status Not Verified - Application is Unresponsive"></i>
									</logic:equal>
									<logic:equal name="webApps" property="statusInd" value="AWR">
										<i id="statusCol<bean:write name="webApps" property="applicationRefId"/>" data-toggle="tooltip" class="fa fa-pulse fa-spinner fa-fw defaultCursor" title="Awaiting response from Application"></i>
									</logic:equal>
								</logic:equal>
							</display:column>
							<display:column>
								<span class="badge badge-pill badge-info" data-toggle="tooltip" title="<bean:write name="webApps" property="instancesDisplay"/>"><bean:write name="webApps" property="instanceCount" /></span>
							</display:column>
							<display:column media="html" style="width: 2%;text-align:center;">
								<c:if test="${webApps.statusInd eq 'ACT' or webApps.statusInd eq 'INF'}">
									<logic:equal name="webApps" property="showApplicationNotification" value="Y">
										<i id="notifyCol<bean:write name="webApps" property="applicationRefId"/>" class="fa fa-bell fa-fw text-white" data-toggle="tooltip" title="Show Notification Message to Client"></i>
									</logic:equal>
									<logic:equal name="webApps" property="showApplicationNotification" value="N">
										<i id="notifyCol<bean:write name="webApps" property="applicationRefId"/>" class="fa fa-bell-slash fa-fw text-reagent-gray" data-toggle="tooltip" title="Do Not Show Notification Message to Client"></i>
									</logic:equal>
								</c:if>
								<c:if test="${webApps.statusInd eq 'AWR' and webApps.showApplicationNotification eq 'Y'}">
									<i id="notifyCol<bean:write name="webApps" property="applicationRefId"/>" class="fa fa-bell fa-fw text-white d-none" data-toggle="tooltip" title="Show Notification Message to Client"></i>
								</c:if>
								<c:if test="${webApps.statusInd eq 'AWR' and webApps.showApplicationNotification eq 'N'}">
									<i id="notifyCol<bean:write name="webApps" property="applicationRefId"/>" class="fa fa-bell-slash fa-fw text-reagent-gray d-none" data-toggle="tooltip" title="Show Notification Message to Client"></i>
								</c:if>
							</display:column>
							<dwarf:searchrow url="applicationAction.do?method=search" useColumnInfo="true" ignoreColumns="0,1,2,7" buttonClass="btn btn-sm btn-primary btn-block" buttonTitle="Filter" buttonLast="true" buttonColSpan="1">
							</dwarf:searchrow>
							<dwarf:columndata />
							<display:column media="html" style="width: 8%">
								<logic:equal name="applicationForm" property="noRecords" value="false">
									<div class="btn-toolbar">
										<div class="btn-group dropdown">
											<button type="button" class="btn btn-sm btn-info dropdown-toggle" data-toggle="dropdown">
												Manage<span class="caret"></span>
											</button>
											<ul class="dropdown-menu dropdown-menu-small" role="menu">
												<li class="dropdown-item"><a
													href="<bean:write name="requestVar" property="contextPath"/>/applicationDetailAction.do?method=editApplication&caller=applicationList&applicationRefId=<bean:write name="webApps" property="applicationRefId"/>">View / Edit</a></li>
												<li class="dropdown-item"><a href="<bean:write name="requestVar" property="contextPath"/>/applicationAction.do?method=refreshSingleApplicationStatus&applicationRefId=<bean:write name="webApps" property="applicationRefId"/>"
													onclick="return changeIcon(<bean:write name="webApps" property="applicationRefId"/>)">Refresh Status</a></li>
												<logic:equal name="applicationForm" property="restrict" value="false">
													<li class="dropdown-item"><a href="<bean:write name="requestVar" property="contextPath"/>/applicationAction.do?method=deleteApplication&applicationRefId=<bean:write name="webApps" property="applicationRefId"/>"
														onclick="return deleteConfirmation()">Delete</a></li>
												</logic:equal>
											</ul>
										</div>
									</div>
								</logic:equal>
							</display:column>
							<display:setProperty name="paging.banner.item_name">Web App</display:setProperty>
							<display:setProperty name="paging.banner.items_name">Web Apps</display:setProperty>
							<display:setProperty name="show.pagesize.dropdown">true</display:setProperty>
							<display:setProperty name="pagesize.dropdown.label.one">Show</display:setProperty>
							<display:setProperty name="pagesize.dropdown.label.two">Rows</display:setProperty>
							<display:setProperty name="pagesize.dropdown.formName">applicationForm</display:setProperty>
							<display:setProperty name="pagesize.dropdown.action">/applicationAction</display:setProperty>
							<display:setProperty name="pagesize.dropdown.type">webApp</display:setProperty>
						</dwarf:dataTable>
					</div>
				</div>
			</div>
		</div>
	</div>
</html:form>