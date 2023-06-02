<%@ include file="/jsp/common/tagdeps.jsp"%>
<bean:page id="requestVar" property="request" />
<tiles:importAttribute/>
<bean:define id="repeatCodes" name="batchCollectionForm" property="repeatCodes"></bean:define>
<bean:define id="selectType" name="batchCollectionForm" property="selectType"></bean:define>
<bean:define id="weekDays" name="batchCollectionForm" property="weekDays"></bean:define>
<div class="row">
	<div class="col">
		<div class="card bg-dark mb-4 p-3 pb-3">
			<div class="card-header m-1 p-2 border-bottom">
				<h5 class="bs-component m-0 text-white" id="scheduleInfotitle" title="<bean:message key="prompt.batch.schedule.title"/>"><bean:message key="prompt.batch.schedule.title"/></h5>
					<logic:equal value="true" name="batchCollectionForm" property="scheduleAddFlag">
				<h6 class="m-0 text-warning" id="scheduleAddNotice"><bean:message key="common.message.schedule.change"/></h6>
				</logic:equal>
			</div>
			<div class="col-md-11 align-self-center">
				<display:table name="sessionScope.batchCollectionForm.batchApp.scheduleList" uid="schedules" class="table table-sm table-striped table-bordered table-dark" export="false" pagesize="" defaultsort="4">
					<display:column media="html" style="width: 3%;text-align:center;">
						<logic:equal name="schedules" property="activeInd" value="Y">
							<i class="fa fa-calendar-check-o fa-fw text-success defaultCursor" data-toggle="tooltip" title="Batch Schedule is Active"></i>
						</logic:equal>
						<logic:equal name="schedules" property="activeInd" value="N">
								<i class="fa fa-calendar-times-o fa-fw text-muted defaultCursor" data-toggle="tooltip" title="Batch Schedule is Inactive"></i>
						</logic:equal>
					</display:column>
					<display:column media="html" style="width: 3%;text-align:center;">
						<logic:equal name="schedules" property="lastRunStatus.resultCd" value="PND">
							<i class="fa fa-clock-o fa-fw text-muted defaultCursor" data-toggle="tooltip" title="Last Run Result - Pending"></i>
						</logic:equal>
						<logic:equal name="schedules" property="lastRunStatus.resultCd" value="OFS">
							<i class="fa fa-exclamation-triangle fa-fw text-danger defaultCursor" data-toggle="tooltip" title="Last Run Result - Off Schedule"></i>
						</logic:equal>
						<logic:equal name="schedules" property="lastRunStatus.resultCd" value="SUC">
							<i class="fa fa-check fa-fw text-success defaultCursor" data-toggle="tooltip" title="Last Run Result - Completed Successfully"></i>
						</logic:equal>
						<logic:equal name="schedules" property="lastRunStatus.resultCd" value="INA">
							<i class="fa fa-minus-circle fa-fw text-muted defaultCursor" data-toggle="tooltip" title="No Run Result. Schedule is Inactive"></i>
						</logic:equal>
						<logic:equal name="schedules" property="lastRunStatus.resultCd" value="UNK">
							<i class="fa fa-question-circle fa-fw text-muted defaultCursor" data-toggle="tooltip" title="Last Run Result - Unable to determine"></i>
						</logic:equal>
						<logic:equal name="schedules" property="lastRunStatus.resultCd" value="UNS">
							<i class="fa fa-ban fa-fw text-danger defaultCursor" data-toggle="tooltip" title="Last Run Result - Unsuccessful"></i>
						</logic:equal>
						<logic:equal name="schedules" property="lastRunStatus.resultCd" value="BLA">
							<i class="fa fa-hourglass-half fa-fw text-success defaultCursor" data-toggle="tooltip" title="Last Run Result - In progress"></i>
						</logic:equal>
						<logic:equal name="schedules" property="lastRunStatus.resultCd" value="EDT">
							<i class="fa fa-exclamation-circle fa-fw text-warning defaultCursor" data-toggle="tooltip" title="Schedule Edit - Must save to complete changes."></i>
						</logic:equal>
						<logic:equal name="schedules" property="lastRunStatus.resultCd" value="NEW">
							<i class="fa fa-flag-o fa-fw text-warning defaultCursor" data-toggle="tooltip" title="New Schedule - Must save to complete changes."></i>
						</logic:equal>
					</display:column>
					<display:column title="Start Date" style="width: 10%" property="schedultStartDt"/>
					<display:column title="Start Time"style="width: 10%" property="startTime"/>
					<display:column title="Frequency" style="width: 10%" property="frequencyDesc"/>
					<display:column title="Schedule Details" property="scheduleDetailDescription"/>
					<display:column media="html" style="width: 8%;">
						<div class="btn-toolbar justify-content-center">
							<div class="btn-group dropdown">
								<button type="button" class="btn btn-sm btn-light dropdown-toggle" data-toggle="dropdown">Manage<span class="caret"></span></button>
								<ul class="dropdown-menu dropdown-menu-small" role="menu">
									<logic:equal value="0" name="schedules" property="scheduleRefId">
										<li  class="dropdown-item">
											<a href="<bean:write name="requestVar" property="contextPath"/>/batchCollectionDetailAction.do?method=editSchedule&scheduleListNumber=<bean:write name="schedules" property="listNumber"/>">View / Edit</a>
										</li>
										<li  class="dropdown-item">
											<a href="<bean:write name="requestVar" property="contextPath"/>/batchCollectionDetailAction.do?method=deleteSchedule&scheduleListNumber=<bean:write name="schedules" property="listNumber"/>" onclick="return deleteConfirmation()">Delete</a>
										</li>
									</logic:equal>
									<logic:notEqual value="0" name="schedules" property="scheduleRefId">
										<li  class="dropdown-item">
											<a href="<bean:write name="requestVar" property="contextPath"/>/batchCollectionDetailAction.do?method=editSchedule&scheduleRefId=<bean:write name="schedules" property="scheduleRefId"/>">View / Edit</a>
										</li>
										<li  class="dropdown-item">
											<a href="<bean:write name="requestVar" property="contextPath"/>/batchCollectionDetailAction.do?method=deleteSchedule&scheduleRefId=<bean:write name="schedules" property="scheduleRefId"/>" onclick="return deleteConfirmation()">Delete</a>
										</li>
										<logic:notEqual value="EDT" name="schedules" property="lastRunStatus.resultCd">
										<logic:equal name="schedules" property="activeInd" value="Y">
											<li class="dropdown-item">
												<a href="<bean:write name="requestVar" property="contextPath"/>/batchCollectionDetailAction.do?method=updateBatchAppActivate&scheduleRefId=<bean:write name="schedules" property="scheduleRefId"/>&activate=N" onclick="return deactivateConfirmation()" title="Deactivate Batch App Schedule">Deactivate Schedule</a>
											</li>
										</logic:equal>
										<logic:notEqual name="schedules" property="activeInd" value="Y">
											<li class="dropdown-item">
												<a href="<bean:write name="requestVar" property="contextPath"/>/batchCollectionDetailAction.do?method=updateBatchAppActivate&scheduleRefId=<bean:write name="schedules" property="scheduleRefId"/>&activate=Y" onclick="return activateConfirmation()" title="Activate Batch App Schedule">Activate Schedule</a>
											</li>
											</logic:notEqual>
										</logic:notEqual>
									</logic:notEqual>
								</ul>
							</div>
						</div>
					</display:column>
					<display:setProperty name="paging.banner.all_items_found">&nbsp;</display:setProperty>
				</display:table>
				<div class="card bg-dark mt-5 mb-4 p-3 border-top">
				<div id="batchScheduleDiv" class="card-body">
					<html:hidden property="scheduleModel.scheduleRefId" styleId="scheduleRefId"/>
					<html:hidden property="scheduleModel.listNumber" styleId="listNumber"/>
						<div class="row col-lg-12">
							<div class="col-lg-2">
								<html:hidden property="scheduleModel.activeInd" styleId="activeVal"/>
								<div class="custom-control custom-toggle">
									<input type="checkbox" id="activeInd" name="activeHold" class="custom-control-input enableWithRestrict">
									<label for="activeInd" class="custom-control-label bs-component" id="scheduleInfoactivate" title="<bean:message key="prompt.schedule.activate"/>"><bean:message key="prompt.schedule.activate"/></label>
								</div>
							</div>
							<div class=" col-lg-2">
								<label class="float-lg-right  bs-component" id="scheduleInfostartDate" title="<bean:message key="prompt.schedule.startDate"/>"><bean:message key="prompt.schedule.startDate"/></label>
							</div>
							<div class="col-lg-3">
								<div class="input-group input-group-seamless mb-3">
									<html:text property="scheduleModel.schedultStartDt" styleClass="form-control enableWithRestrict" readonly="true" styleId="startDate"  errorStyleClass="form-control is-invalid enableWithRestrict" errorKey="org.apache.struts.action.ERROR"/>
									<span class="input-group-append"><span class="input-group-text"><i class="material-icons" id="_startDate">ic_event</i></span></span>
								</div>
								<div id="startDateCal"></div>
							</div>
							<div class=" col-lg-2">
								<label class="float-lg-right bs-component" id="scheduleInfostartTime" title="<bean:message key="prompt.schedule.startTime"/>"><bean:message key="prompt.schedule.startTime"/></label>
							</div>
							<div class=" col-lg-3">
								<div class="input-group input-group-seamless mb-3">
									<html:text property="scheduleModel.startTime" styleClass="form-control enableWithRestrict" readonly="true" styleId="startTime"  errorStyleClass="form-control is-invalid enableWithRestrict" errorKey="org.apache.struts.action.ERROR" />
									<span class="input-group-append">
											<span class="input-group-text">
											<i class="material-icons" id="_startTime">av_timer</i>
										</span>
									</span>
								</div>
								<div id="startTimeCal"></div>
							</div>
						</div>
						<div id="freqRow" class="row col-lg-12">
							<div class="col-lg-2">
								<div class="form-group">
									<label class="bs-component" id="scheduleInfofrequency" title="<bean:message key="prompt.schedule.frequency"/>"><bean:message key="prompt.schedule.frequency"/></label>
									<i id="clearFreq" data-toggle="tooltip" data-placement="right" title="Clear Frequency selection." class="fa fa-remove fa-fw pointerCursor text-danger"></i>
									<html:hidden property="scheduleModel.frequencyCd" styleId="freqVal"/>
								</div>
							</div>
							<div class="col-lg-2">
								<logic:iterate id="freqCodes" name="batchCollectionForm" property="frequencyCodes" indexId="count" type="gov.doc.isu.dwarf.model.CodeModel">
									<div class="custom-control custom-radio mb-1">
										<input class="custom-control-input enableWithRestrict" type="radio" name="freqHold" value="<%=freqCodes.getCode()%>" id="frequency<%=count.intValue()%>">
										<label class="custom-control-label" for="frequency<%=count.intValue()%>"><bean:write name="freqCodes" property="description"/></label>
									</div>
								</logic:iterate>
							</div>
							<div id="dailyRow" class="form-row col-lg-3">
								<div class="form-group col-lg-3">
									<label class="float-lg-right"><bean:message key="prompt.schedule.recur.label"/></label>
								</div>
								<div class="form-group col-lg-5">
									<html:text property="scheduleModel.recur" styleClass="form-control enableWithRestrict" styleId="recur"  maxlength="2" errorStyleClass="form-control is-invalid enableWithRestrict" errorKey="org.apache.struts.action.ERROR" />
								</div>
								<div class="form-group col-lg-3">
									<label>day(s)</label>
								</div>
							</div>
							<div id="weeklyRow" class="form-row col-lg-3">
								<div class="form-group">
									<label><bean:message key="prompt.schedule.weekday"/></label>
									<logic:iterate name="batchCollectionForm" property="weekDays" id="day">
										<div class="custom-control custom-checkbox">
											<html:multibox property="selectedWeekdayWeeks" styleClass="custom-control-input enableWithRestrict" errorStyleClass="custom-control-input is-invalid enableWithRestrict" errorKey="org.apache.struts.action.ERROR" styleId="weeks" >
												<bean:write name="day" property="code" />
											</html:multibox>
											<label class="custom-control-label" for="weeks<bean:write name="day" property="code" />" >
												<bean:write name="day" property="description"/>
											</label>
										</div>
									</logic:iterate>
								</div>
							</div>
							<div id="monthlyRow" class="form-row col-lg-3">
								<div class="form-group">
									<div class="custom-control custom-radio mb-1">
										<html:radio property="scheduleModel.frequencyTypeCd" value="DOM" styleId="monSelect" styleClass="custom-control-input enableWithRestrict"/>
										<label class="custom-control-label" for="monSelect">Days of Month</label>
									</div>
									<div class="custom-control custom-radio mb-1">
										<html:radio property="scheduleModel.frequencyTypeCd" value="MWD" styleId="monSelect2" styleClass="custom-control-input enableWithRestrict"/>
										<label class="custom-control-label" for="monSelect2">Weekdays</label>
									</div>
								</div>
							</div>
							<div id="daysCol" class="form-row col-lg-3 custom-control-inline">
								<logic:iterate name="batchCollectionForm" property="daysInMonth" id="dayInMonth" indexId="count">
									<div class="custom-control custom-checkbox">
										<html:multibox property="scheduleModel.dayNumber" styleClass="custom-control-input enableWithRestrict" errorStyleClass="custom-control-input is-invalid enableWithRestrict" errorKey="org.apache.struts.action.ERROR" styleId="daysCheck">
											<bean:write name="dayInMonth" property="code"/>
										</html:multibox>
										<label class="custom-control-label pr-1" for="daysCheck<bean:write name="dayInMonth" property="code"/>"><bean:write name="dayInMonth" property="description"/></label>
									</div>
								</logic:iterate>
							</div>
							<div id="repeatRow" class="form-row col-lg-5">
								<div class=" col-lg-3">
									<div class="form-group">
										<label>Repeat task every</label>
									</div>
								</div>
								<div class="col-lg-7">
									<div class="form-group">
										<html:select property="scheduleModel.repeatCd" styleClass="custom-select enableWithRestrict" styleId="repeat" errorStyleClass="custom-select is-invalid enableWithRestrict" errorKey="org.apache.struts.action.ERROR" >
											<html:options collection="repeatCodes" property="code" labelProperty="description"/>
										</html:select>
									</div>
								</div>
							</div>
							<div id="onCol" class="form-row col-lg-2">
								<div class="form-group">
									<label class="top-vertical"><bean:message key="prompt.schedule.weeknumber"/></label>
									<html:select property="scheduleModel.weekNumber" styleClass="custom-select enableWithRestrict" size="5" multiple="multiple"  styleId="weekNumber" errorStyleClass="custom-select is-invalid enableWithRestrict" errorKey="org.apache.struts.action.ERROR" >
										<html:options collection="selectType" property="code" labelProperty="description"/>
									</html:select>
								</div>
							</div>
							<div id="onCol2" class="form-row col-lg-2">
								<div class="form-group">
									<label class="top-vertical"><bean:message key="prompt.schedule.weekday"/></label>
									<html:select property="selectedWeekdayMonth" styleClass="custom-select enableWithRestrict" multiple="multiple"  size="5" styleId="selectedWeekdayMonth" errorStyleClass="custom-select is-invalid enableWithRestrict" errorKey="org.apache.struts.action.ERROR" >
										<html:options collection="weekDays" property="code" labelProperty="description"/>
									</html:select>
									</div>
								</div>
							</div>
						</div>
						<div class="card-footer border-top">
							<html:button property="subType" title="Add Schedule" value="Add Schedule" styleClass="btn btn-light enableWithRestrict" styleId="addScheduleButton"/>
							<html:button property="subType" title="Clear Schedule" value="Clear Schedule" styleClass="btn btn-light enableWithRestrict" styleId="cancelScheduleButton"/>
						</div>
					</div>
				</div>
			</div>
		</div>
</div>