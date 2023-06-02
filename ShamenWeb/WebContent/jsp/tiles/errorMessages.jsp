<%@ include file="/jsp/common/tagdeps.jsp"%>
<logic:messagesPresent>
<div class="row mt-4">
	<div class="offset-md-3 col-md-6">
		<div class="errorDiv">
			<div class="alert alert-danger rounded" role="alert" id="messags">
				<h4><bean:message key="myform.error.header" /></h4>
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
