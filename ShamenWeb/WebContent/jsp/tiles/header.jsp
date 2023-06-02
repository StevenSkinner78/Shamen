<%--Author:		James Eric Mansfield JEM01#IS
  - Date:		03/05/2013
  - Description: 	Tiles header
  - JavaScript:	Event handlers in the external file's ready() function.
--%>

<%@ include file="/jsp/common/tagdeps.jsp"%>
<bean:page id="requestVar"  property="request"/>
<script>
var environment_name = "<bean:message key='application.environment.name'/>";

function openStats() {
	var win = window.open('<bean:write name="requestVar" property="contextPath"/>/jsp/common/stats.jsp', 'stats', 'toolbars=no,menubar=no,height=800,width=800,resizable=yes,scrollbars=yes')
	win.focus();
}
</script>
 <div id="header" class="main-navbar fixed-top">
	<!-- Main Navbar -->
	<nav class="navbar align-items-stretch flex-md-nowrap p-0" >
	<div class="w-100 d-none d-md-flex d-lg-flex"></div>
		<ul class="navbar-nav border-left flex-row ">
			<li id="app-env-link2" class="nav-item border-right" style="background-color:<bean:message key='application.environment.color'/>">
				<label class="text-white font-weight-bold p-3"><bean:message key='application.environment.name'/></label>
			</li>
			<li class="nav-item border-right dropdown enableWithRestrict notifications">
				<a class="nav-link nav-link-icon text-center" href="#" role="button" id="dropdownMenuLink" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
					<span class="nav-link-icon__wrapper">
						<i class="material-icons text-white">&#xE7F4;</i>
						<logic:equal value="true" name="JmsModel" property="status">
							<span class="badge badge-pill badge-success text-dark">1</span>
						</logic:equal>
						<logic:notEqual value="true" name="JmsModel" property="status">
							<span class="badge badge-pill badge-danger">1</span>
						</logic:notEqual>
					</span>
				</a>
				<div class="dropdown-menu bg-primary dropdown-menu-small" aria-labelledby="dropdownMenuLink">
					<a class="dropdown-item" href="#"> <logic:equal value="true" name="JmsModel" property="status">
							<i class="fa fa-thumbs-o-up text-success"></i>
							<span class="text-success bs-component" id="jmsconnected" title="JMS Server Status"> JMS Server Connected </span>
						</logic:equal> <logic:notEqual value="true" name="JmsModel" property="status">
							<i class="fa fa-thumbs-o-down text-danger"></i>
							<span class="text-danger bs-component" id="jmsnotconnected" title="JMS Server Status"> JMS Server NOT Connected </span>
						</logic:notEqual>
					</a>
				</div>
			</li>
			<li class="nav-item dropdown notifications">
				<a class="nav-link nav-link-icon text-nowrap px-3" data-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false">
					<span class="nav-link-icon__wrapper">
						<i class="material-icons text-white">&#xe7fd;</i> <span class="d-md-inline-block"><bean:write name="appUser" property="fullName" scope="session" /></span>
					</span>
				</a>
				<div class="dropdown-menu dropdown-menu-small">
					<a class="dropdown-item" href="javascript:openStats()">
						<i class="fa fa-list-alt fa-fw"></i> View Log
					</a>
					<isu:creditPage path="credits/credits.html" anchorClass="dropdown-item"/>
					<a class="dropdown-item" href="<bean:write name="requestVar" property="contextPath"/>/logonAction.do?method=logOff">
						 <i class="material-icons">&#xE879;</i> Logout
					</a>
				</div>
			</li>
		</ul>
		<nav class="nav">
			<a href="#" class="nav-link nav-link-icon toggle-sidebar d-md-inline d-lg-none text-center border-left" data-toggle="collapse" data-target=".header-navbar" aria-expanded="false" aria-controls="header-navbar"> <i class="material-icons">&#xE5D2;</i>
			</a>
		</nav>
	</nav>
</div>