<%@ include file="/jsp/common/tagdeps.jsp"%>

<bean:page id="requestVar"  property="request"/>

<tiles:importAttribute/>
<div class="table-responsive">
	<div class="card-body p-3 pb-3">
		<display:table name="sessionScope.sessionBatchList" uid="batches" class="table table-striped" export="false" pagesize="">
			<logic:present name="displayActiveCol" scope="page" >
				<display:column media="html" style="width: 2%;text-align:center;" class="activeCol">
					<html:hidden property="batchRefId" name="batches" styleId="refIdHold"/>
					<logic:notEqual name="batches" property="type" value="GE2">
						<logic:notEqual name="batches" property="type" value="COL">
							<i title="No Errors Detected" data-toggle="tooltip" class="fa fa-check fa-lg text-success defaultCursor"></i>
						</logic:notEqual>
					</logic:notEqual>
					<logic:equal name="batches" property="type" value="GE2">
						<logic:notEqual name="batches" property="appState" value="false">
							<i title="No Errors Detected" data-toggle="tooltip" class="fa fa-check fa-lg text-success defaultCursor"></i>
						</logic:notEqual>
						<logic:equal name="batches" property="appState" value="false">
							<i title="Error Detected" data-toggle="tooltip" class="fa fa-warning fa-lg text-danger defaultCursor"></i>
						</logic:equal>
					</logic:equal>
					<logic:equal name="batches" property="type" value="COL">
						<logic:notEqual name="batches" property="appState" value="false">
							<i title="No Errors Detected" data-toggle="tooltip" class="fa fa-check fa-lg text-success defaultCursor"></i>
						</logic:notEqual>
						<logic:equal name="batches" property="appState" value="false">
							<i title="Error Detected" data-toggle="tooltip" class="fa fa-warning fa-lg text-danger defaultCursor"></i>
						</logic:equal>
					</logic:equal>
				</display:column>
			</logic:present>
			<display:column title="Batch Name" class="enableWithRestrict">
				<logic:equal value="COL" name="batches" property="type">
					<c:set var="url" value="/batchCollectionDetailAction.do?method=editBatch"/>
				</logic:equal>
				<logic:notEqual value="COL" name="batches" property="type">
					<c:set var="url" value="/batchDetailAction.do?method=editBatch"/>
				</logic:notEqual>
				<a href="javascript: handleForward('<bean:write name='url'/>&batchRefId=<bean:write name="batches" property="batchRefId"/>')">
					<bean:write name="batches" property="name"/>
				</a>
				
			</display:column>
			<display:column title="Controller" style="width: 15%" property="controller.name"/>
			<display:column title="Frequency" style="width: 15%" property="scheduleModel.frequencyCd"/>
			<display:column title="Type" style="width: 15%" property="typeDesc"/>
			<logic:present name="chartCheck" scope="page" >
				<logic:equal value="statsInfo" name="statForm" property="caller">
					<display:column title="Show Chart" media="html" style="width:10%;" >
						<div class="custom-control custom-checkbox">
							<input type="checkbox" name="showChart" value="<bean:write name="batches" property="batchRefId"/>" onclick="javascript: changeChart(<bean:write name="batches" property="batchRefId"/>)" id="showChart<bean:write name="batches" property="batchRefId"/>" class="custom-control-input enableWithRestrict">
							<label class="custom-control-label" for="showChart<bean:write name="batches" property="batchRefId"/>"></label>
						</div>		 		 
					</display:column>
					<display:setProperty name="paging.banner.all_items_found" value=""/>
				</logic:equal>
			</logic:present>
			<logic:empty name="sessionBatchList" scope="session">
				<display:setProperty name="paging.banner.all_items_found"><div class="col-lg-6"><div class="form-inline form-group mtm mbn">{0} of {0} {2}.</div></div></display:setProperty>
			</logic:empty>
		</display:table>
	</div>
</div>