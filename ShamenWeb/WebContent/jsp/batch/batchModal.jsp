<div class="row">
	<div class="col-md-6">
		<div id="batch-modal" class="modal fade top" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" data-backdrop="true">
			<div class="modal-dialog modal-top modal-notify modal-primary" role="document">
				<div class="modal-content">
					<div class="modal-header">
						<h3 class="modal-title">Launch Batch Application</h3>
					</div>
					<div class="modal-body">
						<div class="row d-flex">
							<label class="text-white font-weight-bold">Batch Name:</label>
							<label class="text-white pl-2" id="batch-name"></label> 
						</div>
						<div class="row d-block">
							<label class="text-white font-weight-bold">Batch Parameters</label><br>
							<label class="text-white d-block pl-2">comma separated key=value pairs</label>
							<label class="text-white d-block pl-2">leave blank if there are no parameters</label>
							<input type="text" class="form-control d-block enableWithRestrict" id="job-parameters">
							<input type="hidden" id="batch-ref-id">
						</div>
						<div class="row d-flex pt-2">
							<div class="form-group">
								<a id="launchBatchBtn" role="button" class="btn btn-light" onclick="">Launch</a>
								<a role="button" class="btn btn-light" data-dismiss="modal">Cancel</a>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>