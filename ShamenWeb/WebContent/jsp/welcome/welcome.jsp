<%@ include file="/jsp/common/tagdeps.jsp"%>
<bean:page id="requestVar" property="request" />
<tiles:importAttribute/>
<script type="text/javascript">
var caller = "welcome";
var msgs = {"commonheadertitle":"<bean:message key="common.header.title" bundle="helpMsgs"/>","jmsconnected":"<bean:message key="common.header.jmsServerConnection" bundle="helpMsgs"/>","jmsnotconnected":"<bean:message key="common.header.jmsServerConnection" bundle="helpMsgs"/>","overviewtitle":"<bean:message key="overview.title" bundle="helpMsgs"/>","overviewdonutall": "<bean:message key="overview.donut.all" bundle="helpMsgs"/>","overviewdonutbatch":"<bean:message key="overview.donut.batch" bundle="helpMsgs"/>","overviewdonutwebapps":"<bean:message key="overview.donut.webapps" bundle="helpMsgs"/>"};
</script>

<div class="page-header row no-gutters py-4 mb-3 border-bottom">
	<div class="col-12 col-sm-4 text-center text-sm-left mb-0" >
		<h3 class="page-title" id="welcome" title="Welcome">Welcome</h3>
	</div>
</div>
<div class="row">
	<div class="col">
		<div class="col-lg-6">
		<div class="card card-small card-post p-1">
				<h1 class="text-white">Welcome to Shamen Web</h1>
			</div>
		</div>
	</div>
</div>