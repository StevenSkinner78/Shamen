<%@ include file="/jsp/common/tagdeps.jsp"%>
<bean:page id="requestVar"  property="request"/>
<script type="text/javascript">
var systemData = "<bean:write name="statForm" property="systemData"/>";
</script>
<bean:page id="requestVar"  property="request"/>
<div class="page-header row no-gutters py-4 mb-3 border-bottom">
	<div class="col-12 text-center text-sm-left mb-0"> 
		<h3 class="page-title" id="syshierarchytitle" title="<bean:message key="prompt.infographics.syshierarchy.title"/>"><bean:message key="prompt.infographics.syshierarchy.title"/> <i id='source-button-header' class='fa fa-question-circle fa-fw '></i></h3>
	</div>
</div>
<div class="row">
	<div class="col">
		<div class="card mb-4 p-3 pb-3">
			<div class="card-header mb-1 border-bottom bg-light">
				<div class="form-group text-center">
					<label class="badge orginazation">Organization</label>
					<label class="badge system">System</label>
					<label class="badge batchApp">Batch App</label>
					<label class="badge colMbr">Collection Member</label>
					<label class="badge webApp">Web App</label>
				</div>
			</div>
			<div class="card-body p-3 pb-3">
				<div class="row">
					<div class="col-lg-12" id='start' style="display:none;">
						<div><img id="img-spinner" src="<bean:write name="requestVar" property="contextPath"/>/jsp/images/loading_2.gif" alt="Loading"/></div>
					</div>
				</div>
				<div class="row">
					<div class="col-lg-12">
						<logic:equal value="" name="statForm" property="systemData">
							<label>No data selected</label>
						</logic:equal>
				    	<div id="infovis2"></div>    
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
