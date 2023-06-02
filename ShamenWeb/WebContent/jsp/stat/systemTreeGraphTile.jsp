<%@ include file="/jsp/common/tagdeps.jsp"%>
<bean:page id="requestVar"  property="request"/>
<tiles:useAttribute name="isSysDetail" />
<tiles:useAttribute name="hasChart" />
<div class="row">
	<div class="col">
		<div class="card mb-4 p-3 pb-3">
			<div class="card-header mb-1 border-bottom bg-light">
				<h6 class="m-0">System Hierarchy</h6>
			</div>
			<div class="card-body p-3 pb-3">
				<logic:equal value="false" name="isSysDetail">
					<div class="row border-bottom py-2">
						<div class="col-lg-12">
							<div class="form-group legend-group">
								<label class="legend orginazation">Organization</label>
								<label class="legend system">System</label>
								<label class="legend batchApp">Batch App</label>
								<label class="legend colMbr">Collection Member</label>
								<label class="legend webApp">Web App</label>
							</div>
						</div>
					</div>
				</logic:equal>
				<div class="row border-bottom pb-4">
					<div class="col-lg-12">
				    	<logic:equal value="false" name="hasChart">
							<label>No data</label>
						</logic:equal>
						<logic:equal value="true" name="hasChart">
							<logic:equal value="false" name="isSysDetail">
								<div id="infovis3"></div> 
							</logic:equal>
							<logic:equal value="true" name="isSysDetail">
								<div id="infovis4"></div> 
							</logic:equal> 
						</logic:equal>  
					</div>
				</div>
			</div>
		</div>
	</div>
</div>