<%--Author:		James Eric Mansfield JEM01#IS
  - Date:		03/05/2013
  - Description: 	Tiles header
  - JavaScript:	Event handlers in the external file's ready() function.
--%>

<%@ include file="/jsp/common/tagdeps.jsp"%>
<div class="row" id="button-row">
	<div class="col-lg-6">
		<div class="form-group">
			<html:submit property="subType" title="Save" value="Save" styleClass="btn btn-primary" styleId="saveButton"/>
			<html:cancel title="Cancel" value="Cancel" styleClass="btn btn-primary enableWithRestrict" styleId="cancelButton"/>
		</div>
	</div>
</div>
