<%@ include file="/jsp/common/tagdeps.jsp"%>
<bean:page id="requestVar"  property="request"/>
<script type="text/javascript">
var userData = "<bean:write name="statForm" property="userData"/>";
var selectedLabels = "<bean:write name="statForm" property="selectedLabelsString"/>";
</script>
<bean:page id="requestVar"  property="request"/>
<div class="page-header row no-gutters py-4 mb-3 border-bottom">
	<div class="col-12 text-center text-sm-left mb-0"> 
		<h3 class="page-title" id="appsbyusertitle" title="<bean:message key="label.tabs.stat.user.tooltip"/>"><bean:message key="label.tabs.stat.user.tooltip"/> <i id='source-button-header' class='fa fa-question-circle fa-fw '></i></h3>
	</div>
</div>
<div class="row">
	<div class="col">
		<div class="card mb-4 p-3 pb-3">
			<div class="row">
				<div class="col-lg-12">
					<div class="form-group custom-control-inline">
						<div class="custom-control custom-checkbox">
							<input type="checkbox" name="selectedLabels" onclick="changeGraph()" value="System" id="lblSystem" class="custom-control-input">
							<label class="custom-control-label badge system" for="lblSystem">System</label>
						</div>
					</div>
					<div class="form-group custom-control-inline">
						<div class="custom-control custom-checkbox">
							<input type="checkbox" name="selectedLabels" onclick="changeGraph()" value="BatchApps" id="lblBatchApps" class="custom-control-input">
							<label class="custom-control-label badge batchApp" for="lblBatchApps">Batch Apps</label>
						</div>
					</div>
					<div class="form-group custom-control-inline">
						<div class="custom-control custom-checkbox">
							<input  type="checkbox" name="selectedLabels" onclick="changeGraph()" value="WebApps" id="lblWebApps" class="custom-control-input">
							<label class="custom-control-label badge webApp" for="lblWebApps">Web Apps</label>
						</div>
					</div>
				</div>
			</div>
			<div class="card-body p-3 pb-3">
				<div class="row">
					<div class="col-lg-12">
						<logic:equal value="" name="statForm" property="selectedLabelsString">
							<label>No data selected</label>
						</logic:equal>
				    	<div id="infovis"></div>    
					</div>
				</div>
			</div>
		</div>
	</div>
</div>