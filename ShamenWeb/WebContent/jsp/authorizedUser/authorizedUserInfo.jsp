<%--Author:			James Eric Mansfield JEM01#IS
  - Date:		03/14/2013
  - Description: 	Detail and Listing for Authorized Users
  - JavaScript:	Event handlers in the external file's ready() function.
--%>

<%@ include file="/jsp/common/tagdeps.jsp"%>
<bean:page id="requestVar"  property="request"/>
<bean:define id="userForm" name="userForm" type="gov.doc.isu.shamen.form.AuthorizedUserForm" />
<bean:define id="emailIndList" name="userForm" property="emailIndList"></bean:define>
<bean:define id="authorityList" name="userForm" property="authorityList"></bean:define>
<script type="text/javascript" src="<bean:write name="requestVar" property="contextPath"/>/jsp/scripts/authorizedUser/authorizedUser.js"></script>
<script type="text/javascript">
var menuLinkId = "Admin";
var caller = "authorizedUserInfo";
var userRefId = "<bean:write name='userForm' property='authUser.userRefId'/>";
var restrict = "<bean:write name='userForm' property='restrict'/>";
var noRecords ="<bean:write name='userForm' property='noRecords'/>";
var msgs = {"commonheadertitle":"<bean:message key="common.header.title" bundle="helpMsgs"/>","jmsconnected":"<bean:message key="common.header.jmsServerConnection" bundle="helpMsgs"/>","jmsnotconnected":"<bean:message key="common.header.jmsServerConnection" bundle="helpMsgs"/>","userInfotitle":"<bean:message key="userInfo.title" bundle="helpMsgs"/>"};
</script>
<html:form action="/userDetailAction.do?method=addSave">
<html:hidden name="userForm" property="authUser.emailInd" styleId="activeVal"/>
	<div class="page-header row no-gutters py-4 mb-3 border-bottom">
		<div class="col-12 text-center text-sm-left mb-0"> 
			<h3 class="page-title" id="userInfotitle" title="<bean:message key="prompt.authorizedUser.info.title"/>"><bean:message key="prompt.authorizedUser.info.title"/> <i id='source-button-header' class='fa fa-question-circle fa-fw '></i></h3>
		</div>
	</div>
	<div class="row">
		<div class="col">
			<div class="card mb-4 p-3 pb-3">
				<div id="userDetailRow">
					<isu:authorizedUser formName="userForm" levelSelect="1"/>
				</div>
				<div class="card-footer border-top">
					<div class="row">
						<div class="col">
							<tiles:insert page="/jsp/tiles/buttons.jsp" flush="true"/>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<logic:greaterThan value="0" name="userForm" property="authUser.userRefId">
		<div class="row">
			<div class="col">
				<div class="card mb-4 p-3 pb-3">
					<div class="card-header mb-1 border-bottom bg-light">
						<h6 class="m-0">User Systems</h6>
					</div>
		 			<div class="table-responsive">
		 				<div class="card-body p-3 pb-3">
							<display:table name="sessionScope.userForm.authUser.systems" uid="system" class="table table-striped" export="false" pagesize="">
								<display:column title="System Name"  property="name"/>
								<display:column title="System Description"  property="systemDesc"/>
							</display:table>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col">
				<div class="card mb-4 p-3 pb-3">
					<div class="card-header mb-1 border-bottom bg-light">
						<h6 class="m-0">User Web Applications</h6>
					</div>
						<tiles:insert page="/jsp/tiles/applicationListTile.jsp" />
				</div>	
		 	</div>
		</div>
		<div class="row">
			<div class="col">
				<div class="card mb-4 p-3 pb-3">
					<div class="card-header mb-1 border-bottom bg-light">
						<h6 class="m-0">User Batch Applications</h6>
					</div>
				 	<tiles:insert page="/jsp/tiles/batchListTile.jsp" />
				</div>
			</div>
		</div>
	</logic:greaterThan>
</html:form>