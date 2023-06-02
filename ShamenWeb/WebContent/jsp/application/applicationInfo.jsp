<%@ include file="/jsp/common/tagdeps.jsp"%>
<bean:page id="requestVar"  property="request"/>
<bean:define id="applicationForm" name="applicationForm" type="gov.doc.isu.shamen.form.ApplicationForm"/>
<bean:define id="statusIndList" name="applicationForm" property="statusIndList"/>
<bean:define id="systemList" name="applicationForm" property="systemList"></bean:define>
<bean:define id="pocList" name="applicationForm" property="pocList"/>
<script type="text/javascript" src="<bean:write name="requestVar" property="contextPath"/>/jsp/scripts/application/applicationInfo.js"></script>
<script type="text/javascript">
var verStatus = "<bean:write name='applicationForm' property='application.statusInd'/>";
var applicationRefId = "<bean:write name='applicationForm' property='application.applicationRefId'/>";
var caller = "applicationInfo";
var restrict = "<bean:write name='applicationForm' property='restrict'/>";
var msgs = {"commonheadertitle":"<bean:message key="common.header.title" bundle="helpMsgs"/>","jmsconnected":"<bean:message key="common.header.jmsServerConnection" bundle="helpMsgs"/>"
        ,"jmsnotconnected":"<bean:message key="common.header.jmsServerConnection" bundle="helpMsgs"/>"
        ,"webApplicationInfotitle":"<bean:message key="webApplicationInfo.title" bundle="helpMsgs"/>"
        ,"webApplicationInforequestedStatus":"<bean:message key="webApplicationInfo.requestedStatus" bundle="helpMsgs"/>"
        ,"webApplicationInfoconfirmedStatus":"<bean:message key="webApplicationInfo.confirmedStatus" bundle="helpMsgs"/>"
        ,"webApplicationInforequestedStatusReason":"<bean:message key="webApplicationInfo.requestedStatusReason" bundle="helpMsgs"/>"
        ,"webApplicationInfoverified":"<bean:message key="webApplicationInfo.verified" bundle="helpMsgs"/>"
        ,"webApplicationInfoinstances":"<bean:message key="webApplicationInfo.instances" bundle="helpMsgs"/>"
        ,"webApplicationInfoshowAppInfo":"<bean:message key="webApplicationInfo.showAppInfo" bundle="helpMsgs"/>"
        ,"webApplicationInfoappInfo":"<bean:message key="webApplicationInfo.appInfo" bundle="helpMsgs"/>"};
</script>
<html:form action="/applicationDetailAction.do?method=saveApplication">
	<div class="page-header row no-gutters py-4 mb-3 border-bottom">
		<div class="col-12 col-sm-4 text-center text-sm-left mb-0"> 
			<h3 class="page-title" id="webApplicationInfotitle" title="<bean:message key="prompt.app.info.title"/>"><bean:message key="prompt.app.info.title"/> <i id='source-button-header' class='fa fa-question-circle fa-fw '></i></h3>
		</div>
	</div>
	<div class="row">
		<div class="col">
			<div class="card mb-4 p-3 pb-3">
					<div class="row">
						<div class="col-lg-6">
							<div class="form-group">
								<label><bean:message key="prompt.app.name"/></label>
								<html:text property="application.applicationName" styleClass="form-control" styleId="applicationName" maxlength="75" errorStyleClass="form-control is-invalid" errorKey="org.apache.struts.action.ERROR"/>
							</div>
						</div>
						<div class="col-lg-6">
							<div class="form-group">
								<label><bean:message key="prompt.app.location"/></label>
								<html:text property="application.applicationAddress" styleClass="form-control" styleId="appLocation" maxlength="75" errorStyleClass="form-control is-invalid" errorKey="org.apache.struts.action.ERROR"/>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-lg-6">
							<div class="form-group">
								<label><bean:message key="prompt.system.name"/></label>
								<html:select property="application.system.systemRefId" styleClass="custom-select" styleId="systemRefId" errorStyleClass="custom-select is-invalid" errorKey="org.apache.struts.action.ERROR" >
									<html:options collection="systemList" property="code" labelProperty="description"/>
								</html:select>
							</div>
						</div>
						<div class="col-lg-6">
							<div class="form-group">
								<label><bean:message key="common.label.poc"/></label>
								<html:select property="application.pointOfContact.userRefId" styleClass="custom-select" styleId="pointOfContact" errorStyleClass="custom-select is-invalid" errorKey="org.apache.struts.action.ERROR" >
									<html:options collection="pocList" property="code" labelProperty="description"/>
								</html:select>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-lg-6">
							<div class="form-group">
								<label class="bs-component" id="webApplicationInfoshowAppInfo" title="<bean:message key="prompt.app.show.notificationDesc"/>"><bean:message key="prompt.app.show.notificationDesc"/></label>
								<div class="custom-control custom-toggle">
								<html:hidden property="application.showApplicationNotification" styleId="showAppInfoVal"/>
									<input type="checkbox" id="showApplicationInfo"  value='<bean:write name="applicationForm" property="application.showApplicationNotification"/>' class="custom-control-input enableWithRestrict">
									<label for="showApplicationInfo" class="custom-control-label bs-component" id="showApplicationInfoactivate" title="Show Application Notification"></label>
								</div>
							</div>
						</div>
						<div class="col-lg-6" id="appInfo-div">
							<div class="form-group">
								<label class="bs-component" id="webApplicationInfoappInfo" title="<bean:message key="prompt.app.notificationDesc"/>"><bean:message key="prompt.app.notificationDesc"/></label>
								<html:textarea styleClass="form-control" property="application.applicationNotificationDesc" styleId="applicationInfo" cols="45" rows="5" errorStyleClass="form-control is-invalid" errorKey="org.apache.struts.action.ERROR"></html:textarea>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-lg-6">
							<div class="form-group">
								<label class="bs-component" id="webApplicationInforequestedStatus" title="<bean:message key="prompt.app.status.request"/>"><bean:message key="prompt.app.status.request"/></label>
								<html:select property="application.requestStatusInd" styleClass="custom-select" styleId="appStatReq" errorStyleClass="custom-select is-invalid" errorKey="org.apache.struts.action.ERROR" >
									<html:options collection="statusIndList" property="code" labelProperty="description"/>
								</html:select>
							</div>
						</div>
						<div class="col-lg-6">
							<div class="form-group">
								<label class="bs-component" id="webApplicationInforequestedStatusReason" title="<bean:message key="prompt.app.status.comment"/>"><bean:message key="prompt.app.status.comment"/></label>
								<html:textarea styleClass="form-control" property="application.statusComment" cols="45" rows="5" errorStyleClass="form-control is-invalid"></html:textarea>
							</div>
						</div>
					</div>
					<tiles:insert page="/jsp/tiles/buttons.jsp" flush="true"/>
				</div>
			</div>
		</div>
		<logic:greaterThan value="0" name="applicationForm" property="application.applicationRefId">
		<div class="row">
			<div class="col">
				<div class="card mb-4 p-3 pb-3">
					<div class="card-header mb-1 border-bottom bg-light">
						<h6 class="bs-component m-0" id="webApplicationInfoinstances" title="<bean:message key="prompt.app.instances.title"/>"><bean:message key="prompt.app.instances.title"/></h6>
					</div>
					<div class="table-responsive">
						<div class="card-body p-3 pb-3">
							<display:table name="sessionScope.applicationForm.application.appInstances" uid="instance" toggle="collapsed"  class="table table-striped mb-0">
								<display:column media="html" style="width: 2%;text-align:center;" class="statusCol" >
									<logic:equal name="instance" property="status" value="ACT">
										<i data-toggle="tooltip" class="fa fa-circle fa-fw text-success defaultCursor" title="Status verified - Application is Active"></i>
									</logic:equal>
									<logic:equal name="instance" property="status" value="SUP">
										<i data-toggle="tooltip" class="fa fa-circle fa-fw text-success defaultCursor" title="Status verified - Application is Suspended"></i>
									</logic:equal>
									<logic:equal name="instance" property="status" value="INF">
										<i data-toggle="tooltip" class="fa fa-circle fa-fw text-success defaultCursor" title="Status verified - Application has Information"></i>
									</logic:equal>
									<logic:equal name="instance" property="status" value="UNR">
										<i data-toggle="tooltip" class="fa fa-circle fa-fw text-danger defaultCursor" title="Status Not Verified - Application is Unresponsive"></i>
									</logic:equal>
									<logic:equal name="instance" property="status" value="AWR">
										<i data-toggle="tooltip" class="fa fa-pulse fa-spinner fa-fw defaultCursor" title="Awaiting response from application"></i>
									</logic:equal>
								</display:column>
								<display:column property="applicationInstanceName" title="Instance Name"/>
								<display:column property="instantiationTs" title="Instantiation Time"/>
								<display:column title="Ear Name" property="verifiedEarNm"/>
								<display:subRow id="subRow" positionBelow="true">
									<div class="col-lg-offset-0">
										<table class="sublist ">
											<tbody>
												<tr class="even">
													<td class="col-lg-2"><label>Branch</label></td>
													<td class="col-lg-9"><bean:write name="instance" property="verifiedBuildNm"/></td>
												</tr>
												<tr>
													<td class="col-lg-2"><label>Version</label></td>
													<td class="col-lg-9"><bean:write name="instance" property="verifiedVersionNm"/></td>
												</tr>
												<tr class="even">
													<td class="col-lg-2"><label>Additional Info</label></td>
													<td class="col-lg-9"><bean:write name="instance" property="verifiedAddtnlInfo" filter="false"/></td>
												</tr>
											</tbody>
										</table>
								</div>
								</display:subRow>
								<display:setProperty name="paging.banner.all_items_found" value=""></display:setProperty>
							</display:table>
						</div>
					</div>
				</div>
			</div>
		</div>
	</logic:greaterThan>
	<logic:greaterThan value="0" name="applicationForm" property="application.applicationRefId">
	<div class="row">
		<div class="col">
			<div class="card mb-4 p-3 pb-3">
				<div class="card-header mb-1 border-bottom bg-light">
					<h6 class="m-0">Application Batch Apps</h6>
				</div>
					<tiles:insert page="/jsp/tiles/batchListTile.jsp"/>
			</div>
		</div>
	</div>
	</logic:greaterThan>
</html:form>