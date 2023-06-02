<%@ include file="/jsp/common/tagdeps.jsp"%>
<bean:page id="requestVar"  property="request"/>
<bean:define id="systemForm" name="systemForm" type="gov.doc.isu.shamen.form.SystemForm"/>
<bean:define id="addType" value="Add Controller"/>
<script type="text/javascript" src="<bean:write name="requestVar" property="contextPath"/>/jsp/scripts/system/systemList.js"></script>
<script type="text/javascript">
var restrict = "<bean:write name='systemForm' property='restrict'/>";
var msgs = {"commonheadertitle":"<bean:message key="common.header.title" bundle="helpMsgs"/>","jmsconnected":"<bean:message key="common.header.jmsServerConnection" bundle="helpMsgs"/>","jmsnotconnected":"<bean:message key="common.header.jmsServerConnection" bundle="helpMsgs"/>","systemtitle":"<bean:message key="system.title" bundle="helpMsgs"/>"};
</script>
<html:form action="/systemAction.do?method=list">
	<div class="page-header row no-gutters py-4 mb-3 border-bottom">
		<div class="col-12 col-sm-4 text-center text-sm-left mb-0" >
			<h3 class="page-title" id="systemtitle" title="<bean:message key="prompt.system.list.title"/>"><bean:message key="prompt.system.list.title"/> <i id='source-button-header' class='fa fa-question-circle fa-fw '></i></h3>
		</div>
	</div>
	<div class="row">
		<div class="col">
			<div class="card mb-4">
			   <div class="table-responsive">
			  		<div class="card-body p-3 pb-3">
					 	<display:table name="sessionScope.systemForm.datalist" uid="sytems" requestURI="/systemAction.do?method=list"  class="table table-striped" pagesize="25">
							<display:column title="System Name" class="enableWithRestrict" property="name" url="/systemDetailAction.do?method=editSystem&caller=systemList" paramProperty="systemRefId" paramId="systemRefId"/>
							<display:column title="System Desc" property="systemDesc" />
							<display:column title="Point of Contact" property="pointOfContact.fullName" style="width:20%" />
							<display:column media="html" style="width: 8%;">
								<div class="btn-toolbar">
							 		<div class="btn-group dropdown">
							 			<logic:notEqual value="1" name="sytems" property="systemRefId">
							 			<button type="button" class="btn btn-sm btn-info dropdown-toggle" data-toggle="dropdown">Manage<span class="caret"></span></button>
										<ul class="dropdown-menu dropdown-menu-small" role="menu">
											<li  class="dropdown-item">
												<a href="<bean:write name="requestVar" property="contextPath"/>/systemDetailAction.do?method=editSystem&caller=systemList&systemRefId=<bean:write name="sytems" property="systemRefId"/>">View / Edit</a>
											</li>
											<li  class="dropdown-item">
												<a href="<bean:write name="requestVar" property="contextPath"/>/systemAction.do?method=delete&systemRefId=<bean:write name="sytems" property="systemRefId"/>" onclick="return deleteConfirmation()">Delete</a>
											</li>
										</ul>
										</logic:notEqual>
									</div>
								</div>
							</display:column>
							<display:setProperty name="paging.banner.item_name">System</display:setProperty>
							<display:setProperty name="paging.banner.items_name">Systems</display:setProperty>
							<display:setProperty name="paging.banner.onepage"><span class="pagelinks"></span></display:setProperty>
						</display:table>
					</div>
				</div>
			</div>
		</div>
	</div>
</html:form>