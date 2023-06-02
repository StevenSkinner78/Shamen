<%@ include file="/jsp/common/tagdeps.jsp"%>
<bean:page id="requestVar"  property="request"/>
<div class="row">
	<div class="col-lg-4 mb-4">
		<div class="card card-small card-post bg-light">
			<div class="card-post__image bs-component" style="background-image: url('jsp/svg/jobDurration.svg');" id="jobdurationinfo" title="<bean:message key="label.tabs.stat.performance.tooltip"/>"></div>
			<div class="card-footer text-muted border-top py-3">
              <h5 class="card-title">
               	<a href="<bean:write name="requestVar" property="contextPath"/>/statDetailAction.do?method=resetStats"><bean:message key="label.tabs.stat.performance.tooltip"/></a>
              </h5>
            </div>
		</div>
	</div>
	<div class="col-lg-4 mb-4">
		<div class="card card-small card-post bg-light">
			<div class="card-post__image bs-component" style="background-image: url('jsp/svg/systemByUser.svg');" id="appsbyuserinfo" title="<bean:message key="label.tabs.stat.user.tooltip"/>"></div>
			<div class="card-footer text-muted border-top py-3">
              <h5 class="card-title">
                <a href="<bean:write name="requestVar" property="contextPath"/>/statDetailAction.do?method=loadUserData"><bean:message key="label.tabs.stat.user.tooltip"/></a>
              </h5>
            </div>
		</div>
	</div>
	<div class="col-lg-4 mb-4">
		<div class="card card-small card-post bg-light">
			<div class="card-post__image bs-component" style="background-image: url('jsp/svg/systemHierarchy.svg');" id="syshierarchyinfo" title="<bean:message key="label.tabs.stat.system.tooltip"/>"></div>
			<div class="card-footer text-muted border-top py-3">
              <h5 class="card-title">
                <a href="<bean:write name="requestVar" property="contextPath"/>/statDetailAction.do?method=loadSystemData"><bean:message key="label.tabs.stat.system.tooltip"/></a>
              </h5>
            </div>
		</div>
	</div>
</div>
