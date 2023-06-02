<!DOCTYPE HTML>
<html>
<head>
<meta charset="utf-8"/>
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<meta name="viewport" content="width=device-width, initial-scale=1"/>
<%@ include file="/jsp/common/tagdeps.jsp"%>
<bean:page id="requestVar"  property="request"/>
<link rel="icon" href="<bean:write name="requestVar" property="contextPath"/>/jsp/images/shamen2.ico" />
<jsp:include page="/jsp/common/common.jsp" flush="true" />
<title><tiles:getAsString name="title"/></title>
</head>
<body class="h-100">

	<div class="container-fluid">
		
		<div class="row">
			<!-- Main Sidebar -->
			<aside class="main-sidebar col-12 col-md-3 col-lg-2 px-0">
				<div class="main-navbar" style="background-color:<bean:message key='application.environment.color'/>">
					<nav class="navbar align-items-stretch flex-md-nowrap border-bottom p-0">
						<a class="navbar-brand w-100 mr-0" href="#" style="line-height: 25px;">
							<div class="d-table m-auto">
								<span class="d-md-inline ml-1 bs-component" id="commonheadertitle" title="Shamen Web">Shamen Web</span>
							</div>
						</a> <a class="toggle-sidebar d-sm-inline d-md-none d-lg-none"> <i class="material-icons">&#xE5C4;</i>
						</a>
					</nav>
				</div>
				<div id="app-env-link1" class="border-right d-sm-flex d-md-none d-lg-none" style="background-color:<bean:message key='application.environment.color'/>">
					<label class="w-100 text-white font-weight-bold p-2 text-center"><bean:message key='application.environment.name'/></label>
				</div>
				<div class="nav-wrapper">
					<tiles:insert attribute="tabs" />
				</div>
			</aside>
			<main class="main-content col-lg-10 col-md-9 col-sm-12 p-0 offset-lg-2 offset-md-3">
			
				<tiles:insert attribute="header" />
				<div class="alert alert-warning alert-dismissable d-none fade mb-0 mt-1" role="alert" id="ie_notice">
					<button type="button" class="close" data-dismiss="alert" aria-label="Close" id="ie_notice_button">
						<span aria-hidden="true">x</span>
					</button>
					<p class="m-0"><img class="flex-self-start flex-shrink-0 pt-1 mr-2 hide-sm" src="<bean:write name="requestVar" property="contextPath"/>/jsp/images/ie_not_supported.svg" style="width: 24px;">
					Please note that Shamen Web does not fully support Internet Explorer. As such, not all elements will function properly.
					We recommend upgrading to the latest <a href="https://www.microsoft.com/en-us/windows/microsoft-edge">Microsoft Edge</a>, <a href="https://chrome.google.com">Google Chrome</a>, or <a href="https://mozilla.org/firefox/">Firefox</a>.</p>
				</div>
				<div class="main-content-container container-fluid px-4" id="main-section">
					<tiles:insert attribute="errors" />
					<tiles:insert attribute="body" />
				</div>
			</main>
			</div>
				<div class="row">
					<div class="col-md-12">
						<div id="source-modal" class="modal fade right" tabindex="-1" role="dialog"
                                aria-labelledby="myModalLabel" aria-hidden="true" data-backdrop="true">
							<div class="modal-dialog modal-side modal-top-right modal-lg modal-notify modal-primary">
								<div class="modal-content">
									<div class="modal-header">
										<h3 class="modal-title"></h3>
										<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
									</div>
									<div class="modal-body">
										<pre class="help-content"></pre>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
						</div>
</body>
</html>