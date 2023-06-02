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
			<display:table name="sessionScope.batchCollectionForm.paginatedList" uid="status" pagesize="20" requestURI="/batchCollectionDetailAction.do?method=updateListPageData" toggle="collapsed" toggleAll="collapsed" class="table table-striped">
				<display:column title="Status" style="width: 8%;vertical-align:middle;" property="statusDesc"/>
				<display:column title="Start Time" style="width: 20%;vertical-align:middle;" property="start"/>
				<display:column title="Stop Time" style="width: 20%;vertical-align:middle;" property="stop"/>
				<display:column title="Duration" style="width: 8%;text-align:center;vertical-align:middle;" property="duration"/>
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
				<logic:notEmpty name="batchCollectionForm" property="batchApp.runStatuses" scope="session">
					<logic:notEmpty name="status" property="collectionMembers">
						<display:subRow id="collectionStatus" positionBelow="true">
							<display:table class="sublist" name="pageScope.status.collectionMembers"  uid="childStatus" >
									<display:column media="html" style="width: 4%" >
										<logic:notEqual name="childStatus" property="stop" value="">
											 <div class="dropdown" title="View Run Status Report" data-toggle="tooltip">							
												<a class="text-white" data-toggle="dropdown" href="#" >
													<i class="fa fa-file-o fa-fw text-white" ></i>
												</a>
												<ul class="dropdown-menu down-menu-small" role="menu">
													<li class="dropdown-item" title="View Run Status Report As Text Document" data-toggle="tooltip" data-placement="right"><a href="<bean:write name="requestVar" property="contextPath"/>/statusAction.do?method=viewResultDetail&runStatusRefId=<bean:write name='childStatus' property='runStatusRefId'/>&type=txt" target="_blank"><i class="fa fa-file-text-o fa-fw"></i>TEXT</a></li>
													<li class="dropdown-item" title="View Run Status Report As PDF" data-toggle="tooltip" data-placement="right"><a href="<bean:write name="requestVar" property="contextPath"/>/statusAction.do?method=viewResultDetail&runStatusRefId=<bean:write name='childStatus' property='runStatusRefId'/>&type=pdf" target="_blank"><i class="fa fa-file-pdf-o fa-fw"></i>PDF</a></li>
												</ul>
											</div> 
										</logic:notEqual>
									</display:column>
								<logic:greaterThan value="0" name="childStatus" property="mainBatchAppRefId">
									<display:column title="Collection Member Name" style="width: 28%;" class="font-large font-weight-bold">
										<logic:notEqual value="" name="childStatus" property="stop">
											<a href="<bean:write name="requestVar" property="contextPath"/>/batchDetailAction.do?method=editBatch&batchRefId=<bean:write name="childStatus" property="batchApp.batchRefId"/>">
												<bean:write name="childStatus" property="batchApp.name"/>
											</a>
										</logic:notEqual>
										<logic:equal value="" name="childStatus" property="stop">
												<span class="font-weight-normal pl-2"><bean:write name="childStatus" property="batchApp.name"/></span>
										</logic:equal>
									</display:column>
									<display:column title="Start Time" style="width: 17%" property="start"/>
									<display:column title="Stop Time" style="width: 17%" property="stop"/>
									<display:column title="Duration" style="width: 8%" property="duration"/>
									<display:column title="Status" style="width: 8%" property="statusDesc" />
									<display:column title="Details" style="width: 18%" >
										<logic:equal name="childStatus" property="description" value="">
											<bean:write name="childStatus" property="resultDesc"/>
										</logic:equal>
										<logic:notEqual name="childStatus" property="description" value="">
											<bean:write name="childStatus" property="description"/>
										</logic:notEqual>
									</display:column>		
								</logic:greaterThan>
								<logic:lessThan value="1" name="childStatus" property="mainBatchAppRefId">
									<display:column class="font-large font-weight-bold text-info">
										<bean:write name="childStatus" property="batchApp.name"/>
									</display:column>
									<display:column title="Start Time" style="width: 17%" class="font-large font-weight-bold text-info"  property="start"/>
									<display:column title="Stop Time" style="width: 17%" class="font-large font-weight-bold text-info"  property="stop"/>
									<display:column title="Duration" style="width: 8%" class="font-large font-weight-bold text-info"  property="duration"/>
									<display:column title="Status" style="width: 8%" class="font-large font-weight-bold text-info"  property="statusDesc"/>
								</logic:lessThan>
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
