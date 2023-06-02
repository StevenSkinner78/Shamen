<%@ include file="/jsp/common/tagdeps.jsp"%>
<bean:page id="requestVar"  property="request"/>
<div class="row">
	<div class="col">
		<div class="card mb-4 p-3 pb-3">
			<div class="card-header mb-1 border-bottom bg-light">
				<h6 class="m-0"><bean:message key="prompt.batch.status.title"/></h6>
			</div>
			<div class="table-responsive" id="statusTable">
				<div class="card-body p-3 pb-3">
					<display:table name="sessionScope.batchForm.paginatedList" uid="status" sort="external" requestURI="/batchDetailAction.do?method=updateListPageData" toggle="collapsed" toggleAll="collapsed"  class="table table-striped">
						<display:column media="html" style="width: 2%;text-align:center;vertical-align:middle;">
							<logic:equal name="status" property="statusDesc" value="Done">
								<div class="dropdown">							
									<a class="text-white" data-toggle="dropdown" href="#" >
										<i class="fa fa-file-o fa-fw text-white" ></i>
									</a>
									<ul class="dropdown-menu down-menu-small" role="menu">
										<li class="dropdown-item" title="View Run Status Report As Text Document" data-toggle="tooltip" data-placement="right"><a href="<bean:write name="requestVar" property="contextPath"/>/statusAction.do?method=viewResultDetail&runStatusRefId=<bean:write name='status' property='runStatusRefId'/>&type=txt" target="_blank">TEXT</a></li>
										<li class="dropdown-item" title="View Run Status Report As PDF" data-toggle="tooltip" data-placement="right"><a href="<bean:write name="requestVar" property="contextPath"/>/statusAction.do?method=viewResultDetail&runStatusRefId=<bean:write name='status' property='runStatusRefId'/>&type=pdf" target="_blank">PDF</a></li>
									</ul>
								</div>
							</logic:equal>
						</display:column>
						<display:column title="Status" style="width: 8%;vertical-align:middle;" property="statusDesc"/>
						<display:column title="Start Time" style="width: 20%;vertical-align:middle;" property="start"/>
						<display:column title="Stop Time" style="width: 20%;vertical-align:middle;" property="stop"/>
						<display:column title="Duration" style="width: 8%;vertical-align:middle;" property="duration"/>
						<display:column title="Details" style="width: 20%;vertical-align:middle;">
							<logic:equal name="status" property="description" value="">
								<bean:write name="status" property="resultDesc"/>
							</logic:equal>
							<logic:notEqual name="status" property="description" value="">
								<bean:write name="status" property="description"/>
							</logic:notEqual>
						</display:column>
						<display:column title="From" style="vertical-align:middle;">
							<logic:equal name="status" property="fromScheduleInd" value="true">
								Schedule
							</logic:equal>
							<logic:equal name="status" property="fromScheduleInd" value="false">
								<bean:write name="status" property="userName"/>
							</logic:equal>
						</display:column>
						<logic:notEmpty name="batchForm" property="batchApp.runStatuses" scope="session">
								<logic:notEmpty name="status" property="collectionMembers">
									<display:subRow id="collectionStatus" positionBelow="true">
										<display:table class="sublist" name="pageScope.status.collectionMembers"  uid="childStatus" >
											<display:column title="Start Time" style="width: 17%" property="start"/>
											<display:column title="Status" style="width: 8%" property="statusDesc" />
										
											<display:column title="Details" style="width: 18%">
												<logic:equal name="childStatus" property="description" value="">
													<bean:write name="childStatus" property="resultDesc"/>
												</logic:equal>
												<logic:notEqual name="childStatus" property="description" value="">
													<bean:write name="childStatus" property="description"/>
												</logic:notEqual>
											</display:column>
											<display:column title="From" style="width: 12%">
												<bean:write name="childStatus" property="resultDetail"/>
											</display:column>
											<display:setProperty name="paging.banner.all_items_found" value=""></display:setProperty>
										</display:table>
									</display:subRow>
								</logic:notEmpty>
							</logic:notEmpty>
						<display:setProperty name="paging.banner.group_size">10</display:setProperty>
						<display:setProperty name="paging.banner.item_name">Run Status</display:setProperty>
						<display:setProperty name="paging.banner.items_name">Run Statuses</display:setProperty>
					</display:table>
				</div>
			</div>
		</div>
	</div>
</div>
