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
var restrict = "<bean:write name='userForm' property='restrict'/>";
var noRecords ="<bean:write name='userForm' property='noRecords'/>";
var msgs = {"commonheadertitle":"<bean:message key="common.header.title" bundle="helpMsgs"/>"
        ,"jmsconnected":"<bean:message key="common.header.jmsServerConnection" bundle="helpMsgs"/>"
        ,"jmsnotconnected":"<bean:message key="common.header.jmsServerConnection" bundle="helpMsgs"/>"
        ,"userAdmintitle":"<bean:message key="userAdmin.title" bundle="helpMsgs"/>"};

</script>
<html:form action="/userAction.do?method=list">
	<div class="page-header row no-gutters py-4 mb-3 border-bottom">
		<div class="col-lg-6 col-sm-4 text-left mb-0">
			<h3 class="page-title" id="userAdmintitle" title="<bean:message key="prompt.authorizedUser.title"/>"><bean:message key="prompt.authorizedUser.title"/> <i id='source-button-header' class='fa fa-question-circle fa-fw'></i></h3>
		</div>
	</div>
<div class="row">
	<div class="col">
		<div class="card mb-4">
			<div class="table-responsive">
			  <div class="card-body p-3 pb-3">
				<dwarf:dataTable sort="list" keepStatus="true" uid="userListing" formName="userForm" requestURI="/userAction.do?method=updateListPageData" excludedParams="method" export="true" tableName="AUTHORIZED USERS" className="table table-striped">
					<dwarf:searchrow url="userAction.do?method=search" useColumnInfo="true" ignoreColumns="5" buttonClass="btn btn-sm btn-primary btn-block" buttonTitle="Filter" buttonColSpan="1" buttonLast="true">
					</dwarf:searchrow>
					<dwarf:columndata />
					<display:column media="html" style="width: 6%">
						<logic:equal name="userForm" property="noRecords" value="false">
							<logic:notEqual name="userListing" property="userRefId" value="99">
								<div class="btn-toolbar">
								<div class="btn-group dropdown">
									<button type="button" class="btn btn-sm btn-info dropdown-toggle" data-toggle="dropdown">Manage<span class="caret"></span></button>
									<ul class="dropdown-menu dropdown-menu-small" role="menu">
									<li class="dropdown-item">
										<a href="<bean:write name="requestVar" property="contextPath"/>/userDetailAction.do?method=editView&userRefId=<bean:write name="userListing" property="userRefId"/>">View / Edit</a>
									</li>
									<li class="dropdown-item">
										<a href="<bean:write name="requestVar" property="contextPath"/>/userAction.do?method=delete&userRefId=<bean:write name="userListing" property="userRefId"/>" onclick="return deleteConfirmation()">Delete</a>
									</li>
								</ul>
							</div>
							</div>
							</logic:notEqual>
						</logic:equal>
					</display:column>
					<display:setProperty name="paging.banner.item_name">User</display:setProperty>
					<display:setProperty name="paging.banner.items_name">Users</display:setProperty>
					<display:setProperty name="show.pagesize.dropdown">true</display:setProperty>
					<display:setProperty name="pagesize.dropdown.label.one">Show</display:setProperty>
					<display:setProperty name="pagesize.dropdown.label.two">Rows</display:setProperty>
					<display:setProperty name="pagesize.dropdown.formName">userForm</display:setProperty>
					<display:setProperty name="pagesize.dropdown.action">/userAction</display:setProperty>
					<display:setProperty name="pagesize.dropdown.type">user</display:setProperty>
				</dwarf:dataTable>
			</div>
			
			</div>
		</div>
	</div>
</div>
</html:form>