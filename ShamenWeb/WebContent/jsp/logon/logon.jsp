<%--Author:		James Eric Mansfield JEM01#IS
  - Date:		03/05/2012
  - Description: 	User Logon
--%>

<%@ include file="/jsp/common/tagdeps.jsp"%>
<bean:page id="requestVar"  property="request"/>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="icon" href="<bean:write name="requestVar" property="contextPath"/>/jsp/images/shamen2.ico" />
<jsp:include page="/jsp/common/common.jsp" flush="true" />
<title><bean:message key="label.login"/></title>
</head>
<body>
<script type="text/javascript">
$(document).ready(function(){
	deleteCookie('ie_notice_cookie');
	if(browser == 'ie'){
		$('#ie_notice').removeClass('d-none');
		$('#ie_notice').addClass('show');
	}
	$("#ie_notice_button").click(function(e){
		setCookie("ie_notice_cookie",true,7, contextPath, '', true);
	});
$('[type=password]').keypress(function(e) {
    var $password = $(this),
        tooltipVisible = $('.tooltip').is(':visible'),
        s = String.fromCharCode(e.which);
    
    // check if capslock is on.
    if ( s.toUpperCase() === s && s.toLowerCase() !== s && !e.shiftKey ) {
      if (!tooltipVisible)
          $password.tooltip('show');
    } else {
      if (tooltipVisible)
          $password.tooltip('hide');
    }
    
    // hide the tooltip when moving away from the password field
    $password.blur(function(e) {
    	$password.tooltip('hide');
    });
});
});
</script>
<html:form action="/logonAction.do?method=logOn">
<div class="container-fluid">
	<div class="alert alert-warning alert-dismissable fade d-none mb-0 mt-1" role="alert" id="ie_notice">
		<button type="button" class="close" data-dismiss="alert" aria-label="Close" id="ie_notice_button">
			<span aria-hidden="true">x</span>
		</button>
		<p class="m-0"><img class="flex-self-start flex-shrink-0 pt-1 mr-2 hide-sm" src="<bean:write name="requestVar" property="contextPath"/>/jsp/images/ie_not_supported.svg" style="width: 24px;">
		Please note that Shamen Web does not fully support Internet Explorer. As such, not all elements will function properly.
		We recommend upgrading to the latest <a href="https://www.microsoft.com/en-us/windows/microsoft-edge">Microsoft Edge</a>, <a href="https://chrome.google.com">Google Chrome</a>, or <a href="https://mozilla.org/firefox/">Firefox</a>.</p>
	</div>
	<div class="d-flex align-items-center auth auth-bg-1 theme-one">
		<div class="row w-100">
			<div class="col-lg-6 mx-auto">
				<div class="auto-form-wrapper">
					<logic:messagesPresent>
						<div class="row">
							<div class="col-lg-9 mt-2">
								<div class="errorDiv">
										<div class="alert alert-danger" role="alert">
											<ul>
												<html:messages id="msg">
													<li><bean:write name="msg" filter="false"/></li>
												</html:messages>
											</ul>
										</div>
								</div>
							</div>
						</div>
					</logic:messagesPresent>
					<div class="row form-group">
						<div class="col-lg-12 mt-3">
							<h3 class="">Welcome to</h3>
							<h1 class="">Shamen Web</h1>
						</div>
					</div>
					<div class="input-group mb-3">
						<div class="input-group input-group-seamless">
							<input name="userId" type="text" id="userId" class="form-control" placeholder="User Name" autofocus>
							<span class="input-group-append">
								<span class="input-group-text">
									<i class="material-icons">person</i>
								</span>
							</span>
						</div>
					</div>
					<div class="input-group mb-3">
						<div class="input-group input-group-seamless">
							<input type="password" name="password" id="password" class="form-control" placeholder="Password"  data-placement="top" data-trigger="manual" data-title="Caps lock is on!"/>
							<span class="input-group-append">
								<span class="input-group-text">
									<i class="material-icons">lock</i>
								</span>
							</span>
						</div>
					</div>
					<div class="form-group">
						<html:submit property="subType" value="Log in" styleId="logonButton" styleClass="btn btn-primary submit-btn btn-block" title="Log On"></html:submit>
					</div>
					<div class="row">
						<div class="col">
						<h3>Notice</h3>
							<p>This site is provided as a service to the Missouri Department of Corrections and its employees. Any information displayed on this site is the property of the Missouri Department of Corrections and
								is to be used for departmental purposes only.  Unauthorized entry to this site or misuse of data 
								is strictly prohibited!</p>
							<p>Sign-in as your username@fulldomain, for example: <font class="text-warning font-weight-bold">username@ads.state.mo.us.</font><br/>
								Your Password is the same as your Windows Logon Password.
							</p>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
</html:form>
</body>
</html>