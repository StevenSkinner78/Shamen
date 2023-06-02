<%@ include file="/jsp/common/tagdeps.jsp"%>
<bean:page id="requestVar"  property="request"/>
<bean:define id="statusForm" name="statusForm" type="gov.doc.isu.shamen.form.RunStatusForm"/>
<script type="text/javascript" src="<bean:write name="requestVar" property="contextPath"/>/jsp/scripts/status/statusList.js"></script>
<script type="text/javascript">
var msgs = {"commonheadertitle":"<bean:message key="common.header.title" bundle="helpMsgs"/>"
        ,"jmsconnected":"<bean:message key="common.header.jmsServerConnection" bundle="helpMsgs"/>"
        ,"jmsnotconnected":"<bean:message key="common.header.jmsServerConnection" bundle="helpMsgs"/>"
        ,"recentBatchRunstitle":"<bean:message key="recentBatchRuns.title" bundle="helpMsgs"/>"};
</script>
<html:form action="/statusAction.do?method=list">
	<div class="page-header row no-gutters py-4 mb-3 border-bottom">
		<div class="col-lg-6 col-sm-4 text-left mb-0">
		<h3 class="page-title" id="recentBatchRunstitle" title="<bean:message key="label.tabs.status.tooltip"/>">
			<bean:message key="label.tabs.status.tooltip"/>
			<a data-toggle="tooltip" data-placement="right"  href="<bean:write name="requestVar" property="contextPath"/>/statusAction.do?method=list" title="Reload Run Status Page"><i class="fa fa-refresh fa-fw"></i></a>
			<i id='source-button-header' class='fa fa-question-circle fa-fw '></i>
		</h3>
	</div>
</div>		
	<div class="row">
	<div class="col">
		<div class="card mb-4">
			<div class="table-responsive">
			  <div class="card-body p-3 pb-3">
			<display:table name="sessionScope.statusForm.runStatuses" uid="status" requestURI="/statusAction.do?method=updateListPageData" toggle="expanded" toggleAll="expanded" class="table table-striped">
				<display:column media="html" style="width: 4%;text-align:center;vertical-align:middle;">
					<logic:equal name="status" property="statusDesc" value="Done">
						<logic:notEqual value="COL" name="status" property="batchApp.type">
							<div class="dropdown">							
								<a class="text-white" data-toggle="dropdown" href="#" >
									<i class="fa fa-file-o fa-fw text-white" ></i>
								</a>
								<ul class="dropdown-menu down-menu-small" role="menu">
									<li class="dropdown-item" title="View Run Status Report As Text Document" data-toggle="tooltip" data-placement="right"><a href="<bean:write name="requestVar" property="contextPath"/>/statusAction.do?method=viewResultDetail&runStatusRefId=<bean:write name='status' property='runStatusRefId'/>&type=txt" target="_blank"><i class="fa fa-file-text-o fa-fw"></i> TEXT</a></li>
									<li class="dropdown-item" title="View Run Status Report As PDF" data-toggle="tooltip" data-placement="right"><a href="<bean:write name="requestVar" property="contextPath"/>/statusAction.do?method=viewResultDetail&runStatusRefId=<bean:write name='status' property='runStatusRefId'/>&type=pdf" target="_blank"><i class="fa fa-file-pdf-o fa-fw"></i> PDF</a></li>
								</ul>
							</div>
						</logic:notEqual>
					</logic:equal>
				</display:column>
				<display:column title="Batch/Collection Name" >
						<logic:equal value="COL" name="status" property="batchApp.type">
							<c:set var="url" value="/batchCollectionDetailAction.do?method=editBatch&caller=runStatusList"/>
						</logic:equal>
						<logic:notEqual value="COL" name="status" property="batchApp.type">
							<c:set var="url" value="/batchDetailAction.do?method=editBatch&caller=runStatusList"/>
						</logic:notEqual>
						<a href="<bean:write name="requestVar" property="contextPath"/><bean:write name='url'/>&batchRefId=<bean:write name="status" property="batchApp.batchRefId"/>" >
							<bean:write name="status" property="batchApp.name"/>
						</a>
				</display:column>
				<display:column title="Start Time" style="width: 14%" property="start"/>
				<display:column title="Stop Time" style="width: 14%" property="stop"/>
				<display:column title="Duration" style="width: 7%" property="duration"/>
				<display:column title="Status" style="width: 10%">
					<bean:write name="status" property="statusDesc"/>
<%-- 					<logic:notEqual name="status" property="description" value=""> --%>
<%-- 						<a class="p-2" data-toggle="tooltip" href="<bean:write name="requestVar" property="contextPath"/>/batchAction.do?method=stopBatch&batchRefId=<bean:write name="status" property="batchApp.batchRefId"/>" title="Stop Batch Application"> --%>
<!-- 							<i class="fa fa-stop-circle text-danger"></i> -->
<!-- 						</a> -->
<%-- 					</logic:notEqual> --%>
				</display:column>
				<display:column title="Details" style="width: 17%">
					<logic:equal name="status" property="description" value="">
						<bean:write name="status" property="resultDesc"/>
					</logic:equal>
					<logic:notEqual name="status" property="description" value="">
						<bean:write name="status" property="description"/>
					</logic:notEqual>
				</display:column>	
				<display:column title="From" style="width: 15%">
						<logic:equal name="status" property="fromScheduleInd" value="true">
							Schedule
						</logic:equal>
						<logic:equal name="status" property="fromScheduleInd" value="false">
							<bean:write name="status" property="userName"/>
						</logic:equal>
				</display:column>
				<logic:notEmpty name="statusForm" property="runStatuses" scope="session">
					<logic:notEmpty name="status" property="collectionMembers">
						<display:subRow id="collectionStatus" positionBelow="true">
							<display:table class="sublist" name="pageScope.status.collectionMembers"  uid="childStatus" >
								<display:column media="html" style="width: 4%" >
									<logic:equal name="childStatus" property="statusDesc" value="Done">
										<div class="dropdown" title="View Run Status Report" data-toggle="tooltip">							
											<a class="text-white" data-toggle="dropdown" href="#" >
												<i class="fa fa-file-o fa-fw text-white" ></i>
											</a>
											<ul class="dropdown-menu dropdown-menu-small" role="menu">
												<li class="dropdown-item"  title="View Run Status Report As Text Document" data-toggle="tooltip" data-placement="right"><a href="<bean:write name="requestVar" property="contextPath"/>/statusAction.do?method=viewResultDetail&runStatusRefId=<bean:write name='childStatus' property='runStatusRefId'/>&type=txt" target="_blank"><i class="fa fa-file-text-o fa-fw"></i>TEXT</a></li>
												<li class="dropdown-item"  title="View Run Status Report As PDF" data-toggle="tooltip" data-placement="right"><a href="<bean:write name="requestVar" property="contextPath"/>/statusAction.do?method=viewResultDetail&runStatusRefId=<bean:write name='childStatus' property='runStatusRefId'/>&type=pdf" target="_blank"><i class="fa fa-file-pdf-o fa-fw"></i>PDF</a></li>
											</ul>
										</div>
									</logic:equal>
								</display:column>
								<display:column title="Collection Member Name" style="width: 28%;">
									<logic:equal value="COL" name="childStatus" property="batchApp.type">
										<c:set var="url" value="/batchCollectionDetailAction.do?method=editBatch&caller=runStatusList"/>
									</logic:equal>
									<logic:notEqual value="COL" name="childStatus" property="batchApp.type">
										<c:set var="url" value="/batchDetailAction.do?method=editBatch&caller=runStatusList"/>
									</logic:notEqual>
									<a href="<bean:write name="requestVar" property="contextPath"/><bean:write name='url'/>&batchRefId=<bean:write name="childStatus" property="batchApp.batchRefId"/>">
										<bean:write name="childStatus" property="batchApp.name"/>
									</a>
								</display:column>
								<display:column title="Start Time" style="width: 17%" property="start"/>
								<display:column title="Stop Time" style="width: 17%" property="stop"/>
								<display:column title="Duration" style="width: 8%" property="duration"/>
								<display:column title="Status" style="width: 8%" property="statusDesc" />
							
								<display:column title="Details" style="width: 18%">
									<logic:equal name="childStatus" property="description" value="">
										<bean:write name="childStatus" property="resultDesc"/>
									</logic:equal>
									<logic:notEqual name="childStatus" property="description" value="">
										<bean:write name="childStatus" property="description"/>
									</logic:notEqual>
								</display:column>
								<display:setProperty name="paging.banner.all_items_found" value=""></display:setProperty>
							</display:table>
						</display:subRow>
					</logic:notEmpty>
				</logic:notEmpty>
				<display:setProperty name="paging.banner.all_items_found" value=""></display:setProperty>			
			</display:table>
		</div>
	</div>
</div>
</div>
</div>
</html:form>