<%@ include file="/jsp/common/tagdeps.jsp"%>
<bean:page id="requestVar" property="request" />
<script type="text/javascript">
var doughnutChartOptions;
var noAppsData;
var noAppsTooltips;	
var hasAppsTooltips;	
</script>
<tiles:importAttribute/>
<div class="col-lg-4 col-md-6 col-sm-12 mb-4">
	<div class="card card-small h-100">
		<div class="card-body d-flex">
			<canvas id="donut-1"></canvas>
		</div>
	</div>
</div>
<div class="col-lg-4 col-md-6 col-sm-12 mb-4">
	<div class="card card-small h-100">
		<div class="card-body d-flex">
			<canvas id="donut-2"></canvas>
		</div>
	</div>
</div>
<div class="col-lg-4 col-md-6 col-sm-12 mb-4">
	<div class="card card-small h-100">
		<div class="card-body d-flex">
			<canvas id="donut-3"></canvas>
		</div>
	</div>
</div>