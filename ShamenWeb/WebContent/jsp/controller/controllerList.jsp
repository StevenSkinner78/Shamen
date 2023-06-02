<%@ include file="/jsp/common/tagdeps.jsp"%>
<bean:page id="requestVar"  property="request"/>
<bean:define id="controllerForm" name="controllerForm" type="gov.doc.isu.shamen.form.ControllerForm"/>
<bean:define id="addType" value="Add Controller"/>
<script type="text/javascript" src="<bean:write name="requestVar" property="contextPath"/>/jsp/scripts/controller/controllerList.js"></script>
<script type="text/javascript">
var restrict = "<bean:write name='controllerForm' property='restrict'/>";
var msgs = {"commonheadertitle":"<bean:message key="common.header.title" bundle="helpMsgs"/>"
        ,"jmsconnected":"<bean:message key="common.header.jmsServerConnection" bundle="helpMsgs"/>"
        ,"jmsnotconnected":"<bean:message key="common.header.jmsServerConnection" bundle="helpMsgs"/>"
        ,"controllertitle":"<bean:message key="controller.title" bundle="helpMsgs"/>"};
</script>
<html:form action="/controllerAction.do?method=list">
<div class="page-header row no-gutters py-4 mb-3 border-bottom">
		<div class="col-12 col-sm-4 text-center text-sm-left mb-0" >
		<h3 class="page-title" id="controllertitle" title="<bean:message key="prompt.controller.list.title"/>"><bean:message key="prompt.controller.list.title"/>
			<a  data-placement="right" data-toggle="tooltip" title="Re-establish all the controller connections." href="<bean:write name="requestVar" property="contextPath"/>/controllerAction.do?method=updateControllerStatuses"><i class="fa fa-exchange fa-fw"></i></a>
			<i id='source-button-header' class='fa fa-question-circle fa-fw '></i>
		</h3>
	</div>
</div>		
<div class="row">
	<div class="col">
		<div class="card mb-4">
		   <div class="table-responsive">
		  <div class="card-body p-3 pb-3">
		 	<display:table name="sessionScope.controllerForm.datalist" uid="controllers" requestURI="/controllerAction.do?method=list"  class="table table-striped" pagesize="25">
				<display:column media="html" style="width: 2%;text-align:center;" class="statusCol">
				<html:hidden name="controllers" property="statusCd" styleId="statusVal"/>
					<logic:equal name="controllers" property="statusCd" value="CON">
						<i id="statusCol<bean:write name="controllers" property="controllerRefId"/>" class="fa fa-circle fa-fw text-success defaultCursor" data-toggle="tooltip"  title="Connected"></i>
					</logic:equal>
					<logic:equal name="controllers" property="statusCd" value="UNR">
						<i id="statusCol<bean:write name="controllers" property="controllerRefId"/>" class="fa fa-circle fa-fw text-danger defaultCursor" data-toggle="tooltip"  title="Unresponsive"></i>
					</logic:equal>
					<logic:equal name="controllers" property="statusCd" value="AWR">
						<i id="statusCol<bean:write name="controllers" property="controllerRefId"/>" class="fa fa-pulse fa-spinner fa-fw defaultCursor" data-toggle="tooltip"  title="Awaiting Response from Controller"></i>
					</logic:equal>
				</display:column>
				<display:column title="Controller Name" property="name" style="width:30%" url="/controllerDetailAction.do?method=editController&caller=controllerList" paramProperty="controllerRefId" paramId="controllerRefId"/>
				<display:column title="Controller Address" property="address" style="width:30%"></display:column>
				<display:column title="Default Batch Location" property="defaultAddress" style="width:20%"></display:column>
				<display:column media="html" style="width: 8%;text-align:center;">
						<div class="btn-toolbar">
				 			<div class="btn-group dropdown">
					 			<button type="button" class="btn btn-sm btn-info dropdown-toggle" data-toggle="dropdown">Manage<span class="caret"></span></button>
								<ul class="dropdown-menu dropdown-menu-small" role="menu">
									<li class="dropdown-item">
										<a href="<bean:write name="requestVar" property="contextPath"/>/controllerDetailAction.do?method=editController&caller=controllerList&controllerRefId=<bean:write name="controllers" property="controllerRefId"/>">View / Edit</a>
									</li>
									<li class="dropdown-item">
										<a href="<bean:write name="requestVar" property="contextPath"/>/controllerAction.do?method=refreshSingleControllerStatus&controllerRefId=<bean:write name="controllers" property="controllerRefId"/>">Refresh Status</a>
									</li>
									<logic:equal name="controllerForm" property="restrict" value="false">
										<li class="dropdown-item">
											<a href="<bean:write name="requestVar" property="contextPath"/>/controllerAction.do?method=deleteController&controllerRefId=<bean:write name="controllers" property="controllerRefId"/>" onclick="return deleteConfirmation()">Delete</a>
										</li>
									</logic:equal>
								</ul>
							</div>
						</div>
				</display:column>
				<display:setProperty name="paging.banner.item_name">Controller</display:setProperty>
				<display:setProperty name="paging.banner.items_name">Controllers</display:setProperty>
				<display:setProperty name="paging.banner.onepage"><span class="pagelinks"></span></display:setProperty>
			</display:table>
			</div>
			</div>
		</div>
	</div>
</div>
</html:form>