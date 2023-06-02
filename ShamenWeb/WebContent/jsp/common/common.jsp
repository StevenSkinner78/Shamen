<%--Author:		James Eric Mansfield JEM01#IS
  - Date:		03/05/2012
  - Description: 	Common script files, stylesheets, etc...
--%>
<%@ include file="tagdeps.jsp"%>
<bean:page id="requestVar"  property="request"/>
<link rel="stylesheet"  href="<bean:write name="requestVar" property="contextPath"/>/jsp/assets/css/fullcalendar.min.css"/>
<link rel="stylesheet" type="text/css" href="<bean:write name="requestVar" property="contextPath"/>/jsp/fontawesome/css/all.min.css" media="screen"/>
<link rel="stylesheet" type="text/css" href="<bean:write name="requestVar" property="contextPath"/>/jsp/fontawesome/css/svg-with-js.min.css" media="screen"/>
<link rel="stylesheet" type="text/css" href="<bean:write name="requestVar" property="contextPath"/>/jsp/fontawesome/css/v4-shims.min.css" media="screen"/>
<link rel="stylesheet" type="text/css" href="<bean:write name="requestVar" property="contextPath"/>/jsp/material-design/iconfont/material-icons.css" media="screen"/>
<link rel="stylesheet" type="text/css" href="<bean:write name="requestVar" property="contextPath"/>/jsp/assets/css/bootstrap.min.css" media="screen"/>
<link rel="stylesheet" type="text/css" href="<bean:write name="requestVar" property="contextPath"/>/jsp/assets/css/extras.1.1.0.min.css" media="screen"/>
<link rel="stylesheet"  href="<bean:write name="requestVar" property="contextPath"/>/jsp/assets/css/dygraph.min.css"/>
<link rel="stylesheet"  href="<bean:write name="requestVar" property="contextPath"/>/jsp/assets/css/jquery.datetimepicker.min.css"/>
<link rel="stylesheet" type="text/css" href="<bean:write name="requestVar" property="contextPath"/>/jsp/assets/css/shamen-shards-dashboards.min.css" media="screen"/>

<script type="text/javascript" 	src="<bean:write name="requestVar" property="contextPath"/>/jsp/assets/js/jquery-3.3.1.min.js"></script>
<script type="text/javascript" 	src="<bean:write name="requestVar" property="contextPath"/>/jsp/assets/js/jquery.cookie.js"></script>
<script type="text/javascript" 	src="<bean:write name="requestVar" property="contextPath"/>/jsp/assets/js/popper.min.js"></script>
<script type="text/javascript" 	src="<bean:write name="requestVar" property="contextPath"/>/jsp/assets/js/bootstrap.min.js"></script>
<script type="text/javascript" 	src="<bean:write name="requestVar" property="contextPath"/>/jsp/assets/js/Chart.bundle.min.js"></script>
<script type="text/javascript" 	src="<bean:write name="requestVar" property="contextPath"/>/jsp/assets/js/extras.1.1.0.min.js"></script>
<script type="text/javascript" 	src="<bean:write name="requestVar" property="contextPath"/>/jsp/assets/js/jquery.sharrre.min.js"></script>
<script type="text/javascript" 	src="<bean:write name="requestVar" property="contextPath"/>/jsp/assets/js/shards-dashboards.1.1.0.min.js"></script>
<script type="text/javascript" 	src="<bean:write name="requestVar" property="contextPath"/>/jsp/common/Ajax.js"></script>
<script type="text/javascript" 	src="<bean:write name="requestVar" property="contextPath"/>/jsp/common/json2.js"></script>
<script type="text/javascript" 	src="<bean:write name="requestVar" property="contextPath"/>/jsp/common/popup.js"></script>

<script type="text/javascript" src="<bean:write name="requestVar" property="contextPath"/>/jsp/assets/js/jquery.datetimepicker.min.js"></script>
<script type="text/javascript" src="<bean:write name="requestVar" property="contextPath"/>/jsp/common/common.js?v=6"></script>
<script type="text/javascript" src="<bean:write name="requestVar" property="contextPath"/>/jsp/common/help.js?v=5"></script>
<script type="text/javascript" src="<bean:write name="requestVar" property="contextPath"/>/jsp/assets/js/dygraph.min.js"></script>
<script type="text/javascript" src="<bean:write name="requestVar" property="contextPath"/>/jsp/assets/js/jit.min.js"></script>
<script type="text/javascript">
	var contextPath = '<bean:write name="requestVar" property="contextPath"/>';
	var colapse ='<bean:write name="requestVar" property="contextPath"/>/jsp/images/colapse.svg';
	var expand ='<bean:write name="requestVar" property="contextPath"/>/jsp/images/expand.svg';
	var commonDateErrMsg = "<bean:message key="common.date.formatErrMsg"/>";
	var commonFutureDateErrMsg = "<bean:message key="common.date.futureDate"/>";
	var commonPastDateErrMsg = "<bean:message key="common.date.pastDate"/>";
	var commonDateGreaterEqualMsg = "<bean:message key="common.date.greaterThan"/>";
	var commonDateGreaterMsg  = "<bean:message key="common.date.greaterEqual"/>";
	var commonDateNotEnteredMsg = "<bean:message key="common.date.notEntered"/>";
</script>