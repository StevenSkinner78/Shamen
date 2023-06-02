<%@ include file="/jsp/common/tagdeps.jsp"%>
<bean:page id="requestVar"  property="request"/>
<tiles:importAttribute/>
<div class="table-responsive">
	<div class="card-body p-3 pb-3">
		<display:table name="sessionScope.sessionAppList"  uid="webApp" class="table table-striped" export="false" pagesize="">
			<display:column media="html" style="width: 2%;text-align:center;" class="statusCol">
			<html:hidden name="webApp" property="statusInd" styleId="statusVal"/>
				<logic:equal name="webApp" property="statusInd" value="ACT">
					<i id="statusCol<bean:write name="webApp" property="applicationRefId"/>" data-toggle="tooltip" class="fa fa-check fa-lg text-success defaultCursor" title="Status verified - Application is Active"></i>
				</logic:equal>
				<logic:equal name="webApp" property="statusInd" value="SUP">
					<i id="statusCol<bean:write name="webApp" property="applicationRefId"/>" data-toggle="tooltip" class="fa fa-check fa-lg text-success defaultCursor" title="Status verified - Application is Suspended"></i>
				</logic:equal>
				<logic:equal name="webApp" property="statusInd" value="INF">
					<i id="statusCol<bean:write name="webApp" property="applicationRefId"/>" data-toggle="tooltip" class="fa fa-check fa-lg text-success defaultCursor" title="Status verified - Application has Information"></i>
				</logic:equal>
				<logic:equal name="webApp" property="statusInd" value="UNR">
					<i id="statusCol<bean:write name="webApp" property="applicationRefId"/>" data-toggle="tooltip" class="fa fa-warning fa-lg text-danger defaultCursor" title="Status Not Verified - Application is Unresponsive"></i>
				</logic:equal>
				<logic:equal name="webApp" property="statusInd" value="AWR">
					<i id="statusCol<bean:write name="webApp" property="applicationRefId"/>" data-toggle="tooltip" class="fa fa-pulse fa-spinner fa-fw defaultCursor" title="Awaiting response from application"></i>
				</logic:equal>
			</display:column>
			<display:column title="Requested Status"style="width: 15%" property="requestStatusIndDesc"/>
			<display:column title="Name"style="width: 15%" class="enableWithRestrict">
				<a href="javascript: handleForward('/applicationDetailAction.do?method=editApplication&applicationRefId=<bean:write name="webApp" property="applicationRefId"/>')">
					<bean:write name="webApp" property="applicationName"/>
				</a>
			</display:column>
			<display:column title="Type"style="width: 15%" property="applicationTypeDesc"/>
			<display:column title="Envrionment"style="width: 15%" property="applicationAddress"/>
			<logic:empty name="sessionAppList" scope="session">
				<display:setProperty name="paging.banner.all_items_found"><div class="col-lg-6"><div class="form-inline form-group mtm mbn">{0} of {0} {2}.</div></div></display:setProperty>
			</logic:empty>				
		</display:table>
	</div>
</div>