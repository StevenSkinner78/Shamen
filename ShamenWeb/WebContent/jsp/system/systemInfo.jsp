<%@ include file="/jsp/common/tagdeps.jsp"%>
<bean:page id="requestVar"  property="request"/>
<bean:define id="systemForm" name="systemForm" type="gov.doc.isu.shamen.form.SystemForm" />
<bean:define id="pocList" name="systemForm" property="pocList"></bean:define>
<script type="text/javascript" src="<bean:write name="requestVar" property="contextPath"/>/jsp/scripts/system/systemInfo.js"></script>
<script type="text/javascript">
var systemRefId = "<bean:write name='systemForm' property='system.systemRefId'/>";
var caller = "system";
var restrict = "<bean:write name='systemForm' property='restrict'/>";
var systemData = "<bean:write name="systemForm" property="systemData"/>";
var msgs = {"commonheadertitle":"<bean:message key="common.header.title" bundle="helpMsgs"/>","jmsconnected":"<bean:message key="common.header.jmsServerConnection" bundle="helpMsgs"/>","jmsnotconnected":"<bean:message key="common.header.jmsServerConnection" bundle="helpMsgs"/>"
        ,"systemInfotitle":"<bean:message key="systemInfo.title" bundle="helpMsgs"/>"};
</script>
<html:form action="/systemDetailAction.do?method=saveSystem" >
	<div class="page-header row no-gutters py-4 mb-3 border-bottom">
		<div class="col-12 col-sm-4 text-center text-sm-left mb-0" >
			<h3 class="page-title" id="systemInfotitle" title="<bean:message key="prompt.system.info.title"/>"><bean:message key="prompt.system.info.title"/> <i id='source-button-header' class='fa fa-question-circle fa-fw '></i></h3>
		</div>
	</div>
	<div class="row">
		<div class="col">
			<div class="card mb-4 p-3 pb-3">
				<div class="row">
					<div class="col-lg-6">
						<div class="form-group">
							<label for="systemName"><bean:message key="prompt.system.name"/></label>
							<html:text property="system.name" styleClass="form-control" styleId="systemName" maxlength="75" errorStyleClass="form-control is-invalid" errorKey="org.apache.struts.action.ERROR"/>
						</div>
					</div>
					<div class="col-lg-6">
						<div class="form-group">
							<label class="" for="pointOfContact"><bean:message key="common.label.poc"/></label>
							<html:select property="system.pointOfContact.userRefId" styleClass="custom-select enableWithRestrict" styleId="pointOfContact" errorStyleClass="custom-select is-invalid enableWithRestrictr" errorKey="org.apache.struts.action.ERROR" >
								<html:options collection="pocList" property="code" labelProperty="description"/>
							</html:select>
						</div>
					</div>					
				</div>
				<div class="row">
					<div class="col-lg-6">
						<div class="form-group">
							<label for="systemDesc"><bean:message key="prompt.system.description"/></label>
							<html:textarea property="system.systemDesc" styleClass="form-control" styleId="systemDesc" cols="45" rows="2" errorStyleClass="form-control is-invalid" errorKey="org.apache.struts.action.ERROR"/>
						</div>
					</div>
				</div>
				<tiles:insert page="/jsp/tiles/buttons.jsp" flush="true"/>
			</div>
		</div>
	</div>
	<logic:greaterThan value="0" name="systemForm" property="system.systemRefId">
		<tiles:insert page="/jsp/stat/systemTreeGraphTile.jsp">
			<tiles:put name="isSysDetail" value="true"></tiles:put>
			<logic:equal value="" name="systemForm" property="systemData">
				<tiles:put name="hasChart" value="false"></tiles:put>
			</logic:equal>
			<logic:notEqual value="" name="systemForm" property="systemData">
				<tiles:put name="hasChart" value="true"></tiles:put>
			</logic:notEqual>
		</tiles:insert>
		<div class="row">
			<div class="col">
				<div class="card mb-4 p-3 pb-3">
					<div class="card-header mb-1 border-bottom bg-light">
						<h6 class="m-0"><bean:message key="prompt.system.web.app.list.title"/></h6>
					</div>
					<tiles:insert page="/jsp/tiles/applicationListTile.jsp" />
				</div>
		 	</div>
		</div>
		<div class="row">
			<div class="col">
				<div class="card mb-4 p-3 pb-3">
					<div class="card-header mb-1 border-bottom bg-light">
						<h6 class="m-0"><bean:message key="prompt.system.batch.app.list.title"/></h6>
					</div>
				 	<tiles:insert page="/jsp/tiles/batchListTile.jsp"/>
				</div>
			</div>
		</div>
	</logic:greaterThan>
</html:form>