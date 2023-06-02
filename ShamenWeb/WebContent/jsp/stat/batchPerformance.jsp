<%@ include file="/jsp/common/tagdeps.jsp"%>
<bean:page id="requestVar" property="request" />
<script type="text/javascript" src="<bean:write name="requestVar" property="contextPath"/>/jsp/assets/js/moment.min.js"></script>
<tiles:useAttribute name="hasChart" />
<tiles:useAttribute name="noDataLabel" />
<div class="card-body bg-light p-3 pb-3">
	<div class="row">
		<div class="col-lg-12">
			<logic:equal value="false" name="hasChart">
				<label class="text-dark"><bean:write name="noDataLabel" /></label>
			</logic:equal>
			<logic:equal value="true" name="hasChart">
				<div id="graphdiv"></div>
			</logic:equal>
		</div>
	</div>
</div>
<script type="text/javascript">
	if (chartCsv != "") {
		g = new Dygraph(document.getElementById("graphdiv"), chartCsv,
				{
					legend : 'always',
					animatedZooms : true,
					labelsSeparateLines : true,
					fillGraph : true,
					axes : {
						x : {
							valueFormatter : function(ms) {
								return moment(new Date(ms)).format(
										'MM/DD/YY HH:MM:SS');
							}
						}
					}
				});
	}
</script>