package gov.doc.isu.shamen.action;

import static gov.doc.isu.dwarf.resources.Constants.CANCEL;
import static gov.doc.isu.dwarf.resources.Constants.FAILURE;
import static gov.doc.isu.dwarf.resources.Constants.KEY;
import static gov.doc.isu.dwarf.resources.Constants.LIST;
import static gov.doc.isu.dwarf.resources.Constants.REFRESH;
import static gov.doc.isu.dwarf.resources.Constants.VAL;
import static gov.doc.isu.shamen.resources.AppConstants.BLANK_CODE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import gov.doc.isu.dwarf.exception.BaseException;
import gov.doc.isu.dwarf.model.CodeModel;
import gov.doc.isu.dwarf.resources.Constants;
import gov.doc.isu.dwarf.util.AppUtil;
import gov.doc.isu.dwarf.util.StringUtil;
import gov.doc.isu.shamen.business.ApplicationInfo;
import gov.doc.isu.shamen.business.AuthorizedUserInfo;
import gov.doc.isu.shamen.business.BatchAppInfo;
import gov.doc.isu.shamen.business.CodeInfo;
import gov.doc.isu.shamen.business.ControllerInfo;
import gov.doc.isu.shamen.business.SystemInfo;
import gov.doc.isu.shamen.form.BatchAppForm;
import gov.doc.isu.shamen.models.ApplicationModel;
import gov.doc.isu.shamen.models.AuthorizedUserModel;
import gov.doc.isu.shamen.models.BatchAppCollectionModel;
import gov.doc.isu.shamen.models.BatchAppModel;
import gov.doc.isu.shamen.models.ControllerModel;
import gov.doc.isu.shamen.models.RunStatusModel;
import gov.doc.isu.shamen.models.ScheduleModel;
import gov.doc.isu.shamen.models.SystemModel;
import gov.doc.isu.shamen.util.ShamenPaginatedList;
import gov.doc.isu.shamen.util.ShamenUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Action class that handles all request for the Batch App Edit and Batch Collection Edit.
 *
 * @author <strong>Steven Skinner</strong>
 */
public class BatchAppDetailAction extends ShamenDispatchAction {
    private static Logger log = Logger.getLogger("gov.doc.isu.shamen.action.BatchAppDetailAction");

    /**
     * This method forwards to the batch app edit screen for a new batch app record.
     *
     * @param mapping
     *        (REQUIRED)- Information that the controller, RequestProcessor, knows about mapping the request to the instance of this Action class.
     * @param form
     *        (REQUIRED) - A JavaBean associated with mapping. It will have its properties initialized from the request parameters. Subsequently, properties of this bean have been populated
     * @param request
     *        (REQUIRED) - Provides data including parameter name and values, attributes, and an input stream
     * @param response
     *        (OPTIONAL) - Assist in sending a response to the client
     * @return ActionForward - Describes where and how control should be forwarded
     */
    public ActionForward addBatch(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering BatchAppDetailAction.addBatch");
        BatchAppForm theForm = (BatchAppForm) form;
        ActionForward forward = mapping.findForward("editBatch");
        // This needed for export of PDF view
        Constants.LIST_NAME = "SHAMEN BATCH APP INFORMATION";
        theForm.semiFlush();
        try{
            Long controllerRefId = 0L;
            setCaller(theForm, request);
            theForm.setBatchApp(new BatchAppModel());
            theForm.getBatchApp().setController(new ControllerModel());
            theForm.getBatchApp().setApplication(new ApplicationModel());
            theForm.getBatchApp().setSystem(new SystemModel());
            theForm.getBatchApp().setPointOfContact(new AuthorizedUserModel());
            if(request.getParameter("controllerRefId") != null){
                theForm.getBatchApp().getController().setControllerRefId((Long.valueOf(request.getParameter("controllerRefId"))));
                controllerRefId = Long.valueOf(request.getParameter("controllerRefId"));
            }// end if
            if(request.getParameter("systemRefId") != null){
                theForm.getBatchApp().getSystem().setSystemRefId((Long.valueOf(request.getParameter("systemRefId"))));
            }// end if
            theForm.getBatchApp().setScheduleList(new ArrayList<>());
            theForm.setScheduleModel(new ScheduleModel());
            theForm.setShowSchedule(true);
            theForm.setShowStatus(false);
            theForm.setListCounter(0);
            theForm.setChartCsv(null);
            getReferenceData(theForm, request);
            if(0L != controllerRefId){
                theForm.getBatchApp().setFileLocation(ControllerInfo.getInstance().getDefaultLocation(theForm.getControllerList(), controllerRefId));
            }// end if
            setUserRefIds(theForm.getBatchApp(), request);
            setUserRefIds(theForm.getScheduleModel(), request);
            setupTabs(request, "BatchInfoLink", false);
        }catch(Exception e){
            log.error("Exception occurred in BatchAppDetailAction.addBatch. message: " + e.getMessage(), e);
            forward = mapping.findForward(FAILURE);
        }// end try
        log.debug("Exiting BatchAppDetailAction.addBatch");
        return forward;
    }// end addBatch

    /**
     * This method duplicates a batch.
     *
     * @param mapping
     *        (REQUIRED)- Information that the controller, RequestProcessor, knows about mapping the request to the instance of this Action class.
     * @param form
     *        (REQUIRED) - A JavaBean associated with mapping. It will have its properties initialized from the request parameters. Subsequently, properties of this bean have been populated
     * @param request
     *        (REQUIRED) - Provides data including parameter name and values, attributes, and an input stream
     * @param response
     *        (OPTIONAL) - Assist in sending a response to the client
     * @return ActionForward - Describes where and how control should be forwarded
     */
    public ActionForward duplicateBatch(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering BatchAppDetailAction.duplicateBatch");
        BatchAppForm theForm = (BatchAppForm) form;
        ActionForward forward = mapping.findForward("editBatch");
        BatchAppInfo batchInfo = BatchAppInfo.getInstance();
        // This needed for export of PDF view
        Constants.LIST_NAME = "SHAMEN BATCH APP INFORMATION";
        theForm.semiFlush();
        BatchAppModel batch = new BatchAppModel();
        Long batchAppRefId = 0L;
        try{
            setCaller(theForm, request);
            if(request.getParameter("batchRefId") != null){
                batchAppRefId = Long.valueOf(request.getParameter("batchRefId"));
            }// end if
            batch = batchInfo.getBatchForDuplicate(batchAppRefId, getAppUser(request).getUserRefId());
            theForm.setBatchApp(batch);
            theForm.setShowSchedule(true);
            theForm.setShowStatus(false);
            theForm.setScheduleModel(new ScheduleModel());
            theForm.setListCounter(0);
            setUserRefIds(theForm.getBatchApp(), request);
            setUserRefIds(theForm.getScheduleModel(), request);
            theForm.setChartCsv(null);
            getReferenceData(theForm, request);
            setupTabs(request, "BatchInfoLink", ShamenUtil.showActivateTab(theForm.getBatchApp().getScheduleList()));
        }catch(Exception e){
            log.error("Exception occurred in BatchAppDetailAction.duplicateBatch. message: " + e.getMessage(), e);
            forward = mapping.findForward(FAILURE);
        }// end try
        log.debug("Exiting BatchAppDetailAction.duplicateBatch");
        return forward;
    }// end duplicateBatch
    
    /**
     * This method gets the batch app for edit.
     *
     * @param mapping
     *        (REQUIRED)- Information that the controller, RequestProcessor, knows about mapping the request to the instance of this Action class.
     * @param form
     *        (REQUIRED) - A JavaBean associated with mapping. It will have its properties initialized from the request parameters. Subsequently, properties of this bean have been populated
     * @param request
     *        (REQUIRED) - Provides data including parameter name and values, attributes, and an input stream
     * @param response
     *        (OPTIONAL) - Assist in sending a response to the client
     * @return ActionForward - Describes where and how control should be forwarded
     */
    public ActionForward editBatch(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering BatchAppDetailAction.editBatch");
        BatchAppForm theForm = (BatchAppForm) form;
        ActionForward forward = mapping.findForward("editBatch");
        BatchAppInfo batchInfo = BatchAppInfo.getInstance();
        // This needed for export of PDF view
        Constants.LIST_NAME = "SHAMEN BATCH APP INFORMATION";
        theForm.semiFlush();
        theForm.setNoRecords(true);
        BatchAppModel batch = new BatchAppModel();
        Long batchAppRefId = 0L;
        try{
            setCaller(theForm, request);
            if(request.getParameter("batchRefId") != null){
                batchAppRefId = Long.valueOf(request.getParameter("batchRefId"));
            }// end if
            batch = batchInfo.getBatchForDetail(batchAppRefId);
            ShamenPaginatedList pl = new ShamenPaginatedList(request, 0);
            pl.setTotalNumberOfRows(batchInfo.getRunStatusCount(batchAppRefId));
            batch.setRunStatuses(batchInfo.getRunStatusList(batchAppRefId, Long.valueOf(pl.getFirstRecordIndex()), Long.valueOf(pl.getLastRecordIndex())));
            pl.setList(batch.getRunStatuses());
            theForm.setPaginatedList(pl);
            theForm.setBatchApp(batch);
            theForm.setShowSchedule(true);
            theForm.setShowStatus(true);
            theForm.setScheduleModel(new ScheduleModel());
            theForm.setListCounter(0);
            setUserRefIds(theForm.getBatchApp(), request);
            setUserRefIds(theForm.getScheduleModel(), request);
            getReferenceData(theForm, request);
            theForm.setChartCsv(batchInfo.buildStatCSV(batch));
            setupTabs(request, "BatchInfoLink", ShamenUtil.showActivateTab(theForm.getBatchApp().getScheduleList()));
        }catch(Exception e){
            log.error("Exception occurred in BatchAppDetailAction.editBatch. message: " + e.getMessage(), e);
            forward = mapping.findForward(FAILURE);
        }// end try
        log.debug("Exiting BatchAppDetailAction.editBatch");
        return forward;
    }// end editBatch

    /**
     * This method gets the schedule for edit.
     *
     * @param mapping
     *        (REQUIRED)- Information that the controller, RequestProcessor, knows about mapping the request to the instance of this Action class.
     * @param form
     *        (REQUIRED) - A JavaBean associated with mapping. It will have its properties initialized from the request parameters. Subsequently, properties of this bean have been populated
     * @param request
     *        (REQUIRED) - Provides data including parameter name and values, attributes, and an input stream
     * @param response
     *        (OPTIONAL) - Assist in sending a response to the client
     * @return ActionForward - Describes where and how control should be forwarded
     */
    public ActionForward editSchedule(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering BatchAppDetailAction.editSchedule");
        BatchAppForm theForm = (BatchAppForm) form;
        ActionForward forward = mapping.findForward("editBatch");
        BatchAppInfo batchInfo = BatchAppInfo.getInstance();
        // This needed for export of PDF view
        Constants.LIST_NAME = "SHAMEN BATCH APP INFORMATION";
        theForm.semiFlush();
        theForm.setNoRecords(true);
        ScheduleModel schedule = new ScheduleModel();
        Long scheduleRefId = 0L;
        try{
            setCaller(theForm, request);
            if(request.getParameter("scheduleRefId") != null){
                scheduleRefId = Long.valueOf(request.getParameter("scheduleRefId"));
            }else if(request.getParameter("scheduleListNumber") != null){
                int listNumber = Integer.valueOf(request.getParameter("scheduleListNumber"));
                schedule = theForm.getBatchApp().getScheduleByListNumber(listNumber);
                theForm.getBatchApp().setScheduleList(batchInfo.removeScheduleInList(theForm.getBatchApp().getScheduleList(), listNumber));
            }
            if(scheduleRefId > 0){
                schedule = batchInfo.getBatchAppSchedule(scheduleRefId);
            }
            theForm.setScheduleModel(schedule);
            ShamenPaginatedList pl = new ShamenPaginatedList(request, 0);
            pl.setTotalNumberOfRows(batchInfo.getRunStatusCount(theForm.getBatchApp().getBatchRefId()));
            theForm.getBatchApp().setRunStatuses(batchInfo.getRunStatusList(theForm.getBatchApp().getBatchRefId(), Long.valueOf(pl.getFirstRecordIndex()), Long.valueOf(pl.getLastRecordIndex())));
            pl.setList(theForm.getBatchApp().getRunStatuses());
            theForm.setPaginatedList(pl);
            copyData(theForm, schedule, true);
            setupTabs(request, "BatchInfoLink", ShamenUtil.showActivateTab(theForm.getBatchApp().getScheduleList()));
        }catch(Exception e){
            log.error("Exception occurred in BatchAppDetailAction.editSchedule. message: " + e.getMessage(), e);
            forward = mapping.findForward(FAILURE);
        }// end try
        log.debug("Exiting BatchAppDetailAction.editSchedule");
        return forward;
    }// end editSchedule

    /**
     * This method gets the schedule for edit.
     *
     * @param mapping
     *        (REQUIRED)- Information that the controller, RequestProcessor, knows about mapping the request to the instance of this Action class.
     * @param form
     *        (REQUIRED) - A JavaBean associated with mapping. It will have its properties initialized from the request parameters. Subsequently, properties of this bean have been populated
     * @param request
     *        (REQUIRED) - Provides data including parameter name and values, attributes, and an input stream
     * @param response
     *        (OPTIONAL) - Assist in sending a response to the client
     * @return ActionForward - Describes where and how control should be forwarded
     */
    public ActionForward clearSchedule(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering BatchAppDetailAction.clearSchedule");
        BatchAppForm theForm = (BatchAppForm) form;
        ActionForward forward = mapping.findForward("editBatch");
        // This needed for export of PDF view
        Constants.LIST_NAME = "SHAMEN BATCH APP INFORMATION";
        try{
            theForm.setScheduleModel(new ScheduleModel());
            theForm.getScheduleModel().setLastRunStatus(new RunStatusModel());
            theForm.setScheduleAddFlag(theForm.getBatchApp().isEdit());
            setupTabs(request, "BatchInfoLink", ShamenUtil.showActivateTab(theForm.getBatchApp().getScheduleList()));
        }catch(Exception e){
            log.error("Exception occurred in BatchAppDetailAction.editSchedule. message: " + e.getMessage(), e);
            forward = mapping.findForward(FAILURE);
        }// end try
        log.debug("Exiting BatchAppDetailAction.editSchedule");
        return forward;
    }// end editSchedule

    /**
     * This method gets the schedule for edit.
     *
     * @param mapping
     *        (REQUIRED)- Information that the controller, RequestProcessor, knows about mapping the request to the instance of this Action class.
     * @param form
     *        (REQUIRED) - A JavaBean associated with mapping. It will have its properties initialized from the request parameters. Subsequently, properties of this bean have been populated
     * @param request
     *        (REQUIRED) - Provides data including parameter name and values, attributes, and an input stream
     * @param response
     *        (OPTIONAL) - Assist in sending a response to the client
     * @return ActionForward - Describes where and how control should be forwarded
     */
    public ActionForward addSchedule(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering BatchAppDetailAction.addSchedule");
        BatchAppForm theForm = (BatchAppForm) form;
        ActionMessages errors = new ActionMessages();
        ActionForward forward = mapping.findForward("editBatch");
        BatchAppInfo batchInfo = BatchAppInfo.getInstance();
        CodeInfo codeInfo = CodeInfo.getInstance();
        // This needed for export of PDF view
        Constants.LIST_NAME = "SHAMEN BATCH APP INFORMATION";
        try{
            errors.add(theForm.validateBatchScheduleInfo(getResources(request)));
            if(!errors.isEmpty()){
                this.saveErrors(request, errors);
            }else{
                copyData(theForm, theForm.getScheduleModel(), false);
                if(theForm.getScheduleModel().getScheduleRefId() > 0L){
                    theForm.getBatchApp().setScheduleList(batchInfo.updateScheduleInList(theForm.getBatchApp().getScheduleList(), theForm.getScheduleModel()));
                }else{
                    int listNumber = theForm.getListCounter() + 1;
                    theForm.getScheduleModel().setListNumber(listNumber);
                    theForm.setListCounter(listNumber);
                    theForm.getScheduleModel().setLastRunStatus(new RunStatusModel("NEW"));
                    theForm.getBatchApp().getScheduleList().add(codeInfo.fillCodeDesc(theForm.getScheduleModel()));
                }
                theForm.setScheduleModel(new ScheduleModel());
                theForm.getScheduleModel().setLastRunStatus(new RunStatusModel());
                theForm.setScheduleAddFlag(true);
                setUserRefIds(theForm.getScheduleModel(), request);
            }// end if/else
            ShamenPaginatedList pl = new ShamenPaginatedList(request, 0);
            pl.setTotalNumberOfRows(batchInfo.getRunStatusCount(theForm.getBatchApp().getBatchRefId()));
            theForm.getBatchApp().setRunStatuses(batchInfo.getRunStatusList(theForm.getBatchApp().getBatchRefId(), Long.valueOf(pl.getFirstRecordIndex()), Long.valueOf(pl.getLastRecordIndex())));
            pl.setList(theForm.getBatchApp().getRunStatuses());
            theForm.setPaginatedList(pl);
            setupTabs(request, "BatchInfoLink", ShamenUtil.showActivateTab(theForm.getBatchApp().getScheduleList()));
        }catch(Exception e){
            log.error("Exception occurred in BatchAppDetailAction.addSchedule. message: " + e.getMessage(), e);
            forward = mapping.findForward(FAILURE);
        }// end try
        log.debug("Exiting BatchAppDetailAction.addSchedule");
        return forward;
    }// end editSchedule

    /**
     * This method deletes the schedule associated with a batch.
     *
     * @param mapping
     *        (REQUIRED)- Information that the controller, RequestProcessor, knows about mapping the request to the instance of this Action class.
     * @param form
     *        (REQUIRED) - A JavaBean associated with mapping. It will have its properties initialized from the request parameters. Subsequently, properties of this bean have been populated
     * @param request
     *        (REQUIRED) - Provides data including parameter name and values, attributes, and an input stream
     * @param response
     *        (OPTIONAL) - Assist in sending a response to the client
     * @return ActionForward - Describes where and how control should be forwarded
     */
    public ActionForward deleteSchedule(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering BatchAppDetailAction.deleteSchedule");
        BatchAppForm theForm = (BatchAppForm) form;
        ActionForward forward = mapping.findForward("editBatch");
        BatchAppInfo batchInfo = BatchAppInfo.getInstance();
        try{
            if(request.getParameter("scheduleRefId") != null){
                Long scheduleRefId = Long.valueOf(request.getParameter("scheduleRefId"));
                BatchAppModel batch = batchInfo.getBatchForDetail(theForm.getBatchApp().getBatchRefId());
                List<ScheduleModel> scheduleList = batchInfo.deleteBatchAppSchedule(batch.getScheduleList(), scheduleRefId, getAppUser(request).getUserRefId());
                batch.setScheduleList(scheduleList);
                batchInfo.updateBatchApp(batch);
                String path = mapping.findForward("batchInfo").getPath();
                if(!StringUtil.isNullOrEmpty(theForm.getCaller())){
                    path += "&caller=" + theForm.getCaller();
                }// end if/else
                path += "&batchRefId=" + theForm.getBatchApp().getBatchRefId();
                forward = new ActionForward(path, true);
            }else if(request.getParameter("scheduleListNumber") != null){
                int listNumber = Integer.valueOf(request.getParameter("scheduleListNumber"));
                theForm.getBatchApp().setScheduleList(batchInfo.removeScheduleInList(theForm.getBatchApp().getScheduleList(), listNumber));
                theForm.setScheduleAddFlag(theForm.getBatchApp().isEdit());
            }
            setupTabs(request, "BatchInfoLink", ShamenUtil.showActivateTab(theForm.getBatchApp().getScheduleList()));
        }catch(Exception e){
            log.error("Exception occurred in BatchAppDetailAction.deleteSchedule. message: " + e.getMessage(), e);
            forward = mapping.findForward(FAILURE);
        }// end try
        log.debug("Exiting BatchAppDetailAction.deleteSchedule");
        return forward;
    }// end deleteSchedule

    /**
     * Set the caller parameter into the form
     *
     * @param theForm
     *        action form
     * @param request
     *        HTTP servlet request
     */
    private void setCaller(BatchAppForm theForm, HttpServletRequest request) {
        log.debug("Entering setCaller");
        log.debug("Set the caller parameter into the form");
        if(request.getParameter("caller") != null){
            theForm.setCaller(request.getParameter("caller"));
        }// end if
        log.debug("Exiting setCaller");
    }// end setCaller

    /**
     * This method clears all Run Statuses associated with a Batch App.
     *
     * @param mapping
     *        (REQUIRED)- Information that the controller, RequestProcessor, knows about mapping the request to the instance of this Action class.
     * @param form
     *        (REQUIRED) - A JavaBean associated with mapping. It will have its properties initialized from the request parameters. Subsequently, properties of this bean have been populated
     * @param request
     *        (REQUIRED) - Provides data including parameter name and values, attributes, and an input stream
     * @param response
     *        (OPTIONAL) - Assist in sending a response to the client
     * @return ActionForward - Describes where and how control should be forwarded
     */
    public ActionForward clearBatchRunStatuses(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering BatchAppDetailAction.clearBatchRunStatuses");
        BatchAppForm theForm = (BatchAppForm) form;
        ActionForward forward = mapping.findForward("editBatch");
        BatchAppInfo batchInfo = BatchAppInfo.getInstance();
        // This needed for export of PDF view
        Constants.LIST_NAME = "SHAMEN BATCH APP INFORMATION";
        theForm.semiFlush();
        theForm.setNoRecords(true);
        try{
            BatchAppModel batch = batchInfo.clearRunStatuses(theForm.getBatchApp().getBatchRefId(), false);
            theForm.setBatchApp(batch);
            theForm.setShowSchedule(true);
            theForm.setShowStatus(false);
            setUserRefIds(theForm.getBatchApp(), request);
            setUserRefIds(theForm.getScheduleModel(), request);
            theForm.setChartCsv("");
            // copyData(theForm, theForm.getBatchApp(), true);
            getReferenceData(theForm, request);
            setupTabs(request, "BatchInfoLink", true);
        }catch(Exception e){
            log.error("Exception occurred in BatchAppDetailAction.clearBatchRunStatuses. message: " + e.getMessage(), e);
            forward = mapping.findForward(FAILURE);
        }// end try
        log.debug("Exiting BatchAppDetailAction.clearBatchRunStatuses");
        return forward;
    }// end clearBatchRunStatuses

    /**
     * This method deletes the batch app by ref id.
     *
     * @param mapping
     *        (REQUIRED)- Information that the controller, RequestProcessor, knows about mapping the request to the instance of this Action class.
     * @param form
     *        (REQUIRED) - A JavaBean associated with mapping. It will have its properties initialized from the request parameters. Subsequently, properties of this bean have been populated
     * @param request
     *        (REQUIRED) - Provides data including parameter name and values, attributes, and an input stream
     * @param response
     *        (OPTIONAL) - Assist in sending a response to the client
     * @return ActionForward - Describes where and how control should be forwarded
     */
    public ActionForward deleteBatch(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering BatchAppDetailAction.deleteBatch");
        BatchAppForm theForm = (BatchAppForm) form;
        ActionForward forward = mapping.findForward(LIST);
        BatchAppInfo batchInfo = BatchAppInfo.getInstance();
        // This needed for export of PDF view
        Constants.LIST_NAME = "SHAMEN BATCH APP";
        theForm.semiFlush();
        theForm.setNoRecords(false);
        Long batchRefId = 0L;
        try{
            if(request.getParameter("batchRefId") != null){
                batchRefId = Long.valueOf(request.getParameter("batchRefId"));
            }// end if
            batchInfo.deleteBatchApp(batchRefId, getAppUser(request).getUserRefId(), false);
            theForm.setBatchList(batchInfo.getBatchList());
            setupTabs(request, null, true);
        }catch(Exception e){
            log.error("Exception occurred in BatchAppDetailAction.deleteBatch. message: " + e.getMessage(), e);
            forward = mapping.findForward(FAILURE);
        }// end try
        log.debug("Exiting BatchAppDetailAction.deleteBatch");
        return forward;
    }// end deleteBatch

    /**
     * This method updates all batches by setting active indicator to 'Y' or 'N' depending on request parameter.
     *
     * @param mapping
     *        (REQUIRED)- Information that the controller, RequestProcessor, knows about mapping the request to the instance of this Action class.
     * @param form
     *        (REQUIRED) - A JavaBean associated with mapping. It will have its properties initialized from the request parameters. Subsequently, properties of this bean have been populated
     * @param request
     *        (REQUIRED) - Provides data including parameter name and values, attributes, and an input stream
     * @param response
     *        (OPTIONAL) - Assist in sending a response to the client
     * @return ActionForward - Describes where and how control should be forwarded
     */
    public ActionForward updateAll(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering BatchAppDetailAction.updateAll");
        BatchAppForm theForm = (BatchAppForm) form;
        ActionForward forward = mapping.findForward("editBatch");
        BatchAppInfo batchInfo = BatchAppInfo.getInstance();
        // This needed for export of PDF view
        Constants.LIST_NAME = "SHAMEN BATCH APP INFORMATION";
        String activate = "";
        BatchAppModel batch = new BatchAppModel();
        try{
            if(request.getParameter("activate") != null){
                activate = request.getParameter("activate");
            }// end if

            batchInfo.updateBatchActiveInd(activate, theForm.getBatchApp().getBatchRefId(), getAppUser(request).getUserRefId());
            batch = batchInfo.getBatchForDetail(theForm.getBatchApp().getBatchRefId());
            ShamenPaginatedList pl = new ShamenPaginatedList(request, 0);
            pl.setTotalNumberOfRows(batchInfo.getRunStatusCount(batch.getBatchRefId()));
            batch.setRunStatuses(batchInfo.getRunStatusList(batch.getBatchRefId(), Long.valueOf(pl.getFirstRecordIndex()), Long.valueOf(pl.getLastRecordIndex())));
            pl.setList(batch.getRunStatuses());
            theForm.setPaginatedList(pl);
            theForm.setBatchApp(batch);
            theForm.setShowSchedule(true);
            theForm.setShowStatus(true);
            theForm.setScheduleModel(new ScheduleModel());
            setUserRefIds(theForm.getBatchApp(), request);
            setUserRefIds(theForm.getScheduleModel(), request);
            getReferenceData(theForm, request);
            theForm.setChartCsv(batchInfo.buildStatCSV(batch));
            setupTabs(request, "BatchInfoLink", true);
        }catch(Exception e){
            log.error("Exception occurred in BatchAppDetailAction.updateAll. message: " + e.getMessage(), e);
            forward = mapping.findForward(FAILURE);
        }// end try
        log.debug("Exiting BatchAppDetailAction.updateAll");
        return forward;
    }// end updateAll

    /**
     * This method updates all batches by setting active indicator to 'Y' or 'N' depending on request parameter.
     *
     * @param mapping
     *        (REQUIRED)- Information that the controller, RequestProcessor, knows about mapping the request to the instance of this Action class.
     * @param form
     *        (REQUIRED) - A JavaBean associated with mapping. It will have its properties initialized from the request parameters. Subsequently, properties of this bean have been populated
     * @param request
     *        (REQUIRED) - Provides data including parameter name and values, attributes, and an input stream
     * @param response
     *        (OPTIONAL) - Assist in sending a response to the client
     * @return ActionForward - Describes where and how control should be forwarded
     */
    public ActionForward updateBatchAppActivate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering BatchAppDetailAction.updateBatchAppActivate");
        BatchAppForm theForm = (BatchAppForm) form;
        ActionForward forward = mapping.findForward("editBatch");
        BatchAppInfo batchInfo = BatchAppInfo.getInstance();
        // This needed for export of PDF view
        Constants.LIST_NAME = "SHAMEN BATCH APP INFORMATION";
        String activate = "";
        Long scheduleRefId = 0L;
        BatchAppModel batch = new BatchAppModel();
        try{
            if(request.getParameter("activate") != null){
                activate = request.getParameter("activate");
            }// end if
            if(request.getParameter("scheduleRefId") != null){
                scheduleRefId = Long.valueOf(request.getParameter("scheduleRefId"));
            }// end if
            batchInfo.updateBatchActiveInd(activate, theForm.getBatchApp().getBatchRefId(), scheduleRefId, getAppUser(request).getUserRefId());
            batch = batchInfo.getBatchForDetail(theForm.getBatchApp().getBatchRefId());
            ShamenPaginatedList pl = new ShamenPaginatedList(request, 0);
            pl.setTotalNumberOfRows(batchInfo.getRunStatusCount(batch.getBatchRefId()));
            batch.setRunStatuses(batchInfo.getRunStatusList(batch.getBatchRefId(), Long.valueOf(pl.getFirstRecordIndex()), Long.valueOf(pl.getLastRecordIndex())));
            pl.setList(batch.getRunStatuses());
            theForm.setPaginatedList(pl);
            theForm.setBatchApp(batch);
            theForm.setShowSchedule(true);
            theForm.setShowStatus(true);
            theForm.setScheduleModel(new ScheduleModel());
            setUserRefIds(theForm.getBatchApp(), request);
            setUserRefIds(theForm.getScheduleModel(), request);
            getReferenceData(theForm, request);
            theForm.setChartCsv(batchInfo.buildStatCSV(batch));
            setupTabs(request, "BatchInfoLink", true);
        }catch(Exception e){
            log.error("Exception occurred in BatchAppDetailAction.updateBatchAppActivate. message: " + e.getMessage(), e);
            forward = mapping.findForward(FAILURE);
        }// end try
        log.debug("Exiting BatchAppDetailAction.updateBatchAppActivate");
        return forward;
    }// end updateBatchAppActivate

    /**
     * This method saves a batch.
     *
     * @param mapping
     *        (REQUIRED)- Information that the controller, RequestProcessor, knows about mapping the request to the instance of this Action class.
     * @param form
     *        (REQUIRED) - A JavaBean associated with mapping. It will have its properties initialized from the request parameters. Subsequently, properties of this bean have been populated
     * @param request
     *        (REQUIRED) - Provides data including parameter name and values, attributes, and an input stream
     * @param response
     *        (OPTIONAL) - Assist in sending a response to the client
     * @return ActionForward - Describes where and how control should be forwarded
     */
    public ActionForward saveBatchApp(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering BatchAppDetailAction.saveBatchApp");
        BatchAppForm theForm = (BatchAppForm) form;
        ActionForward forward = mapping.findForward(REFRESH);
        ActionMessages errors = new ActionMessages();
        ActionMessages errors2 = new ActionMessages();
        BatchAppInfo batchInfo = BatchAppInfo.getInstance();
        BatchAppModel batchApp = theForm.getBatchApp();
        try{
            if(theForm.getCaller() != null){
                String path = mapping.findForward(theForm.getCaller()).getPath();
                if("controllerInfo".equalsIgnoreCase(theForm.getCaller())){
                    path += "&controllerRefId=" + theForm.getBatchApp().getController().getControllerRefId();
                }else if("applicationInfo".equalsIgnoreCase(theForm.getCaller())){
                    path += "&applicationRefId=" + theForm.getBatchApp().getApplication().getApplicationRefId();
                }else if("system".equalsIgnoreCase(theForm.getCaller())){
                    path += "&systemRefId=" + theForm.getBatchApp().getSystem().getSystemRefId();
                }else if("authorizedUserInfo".equalsIgnoreCase(theForm.getCaller())){
                    path += "&userRefId=" + theForm.getBatchApp().getPointOfContact().getUserRefId();
                }// end if/else
                forward = new ActionForward(path, true);
            }// end if
            if(request.getParameter("cancel") != null){
                if(theForm.getCaller() == null){
                    forward = mapping.findForward(CANCEL);
                }// end if
                return forward;
            }// end if
            errors = theForm.validate(mapping, request);
            errors2 = theForm.validateBatchInfo(getResources(request));
            if(!errors2.isEmpty()){
                if(!errors.isEmpty()){
                    errors.add(errors2);
                }else{
                    errors = errors2;
                }// end if
            }// end if
            if(!errors.isEmpty()){
                this.saveErrors(request, errors);
                setupTabs(request, "BatchInfoLink", ShamenUtil.showActivateTab(theForm.getBatchApp().getScheduleList()));
                return mapping.getInputForward();
            }else{
                // copyData(theForm, theForm.getBatchApp(), false);
                if(!AppUtil.isNotNullAndZero(batchApp.getBatchRefId())){
                    batchInfo.saveBatchApp(batchApp);
                }else{
                    batchInfo.updateBatchApp(batchApp);
                }// end if/else
            }// end if/else
            setupTabs(request, null, true);
        }catch(Exception e){
            log.error("Exception occurred in BatchAppDetailAction.saveBatchApp. message: " + e.getMessage(), e);
            forward = mapping.findForward(FAILURE);
        }// end try
        log.debug("Exiting BatchAppDetailAction.saveBatchApp");
        return forward;
    }// end saveBatchApp

    /**
     * Load batch apps based on controller ref id..
     *
     * @param mapping
     *        mapping
     * @param form
     *        form
     * @param request
     *        request
     * @param response
     *        response
     * @return null null
     * @throws Exception
     *         Exception
     */
    public ActionForward loadWebApps(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        log.debug("Entering BatchAppDetailAction.loadWebApps");
        BatchAppForm theForm = (BatchAppForm) form;
        ApplicationInfo appInfo = ApplicationInfo.getInstance();
        Long systemRefId = 0L;
        if(request.getParameter("systemRefId") != null){
            systemRefId = Long.valueOf(request.getParameter("systemRefId"));
        }// end if
        List<CodeModel> webApps = (ArrayList<CodeModel>) appInfo.getApplicationListForSystemAsCodeModel(systemRefId);
        if(AppUtil.isEmpty(webApps)){
            webApps = new ArrayList<CodeModel>();
        }// end if
        theForm.setApplicationList(webApps);
        JSONArray jsnArray = createJSONArray(webApps.isEmpty() ? null : webApps);
        response.getWriter().println(jsnArray);
        response.flushBuffer();

        log.debug("Exiting BatchAppDetailAction.loadWebApps");
        return null;
    }// end loadWebApps

    /**
     * Load default batch location based on controller ref id..
     *
     * @param mapping
     *        mapping
     * @param form
     *        form
     * @param request
     *        request
     * @param response
     *        response
     * @return null null
     * @throws Exception
     *         Exception
     */
    public ActionForward loadDefaultLocation(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        log.debug("Entering BatchAppDetailAction.loadDefaultLocation");
        BatchAppForm theForm = (BatchAppForm) form;
        ControllerInfo contInfo = ControllerInfo.getInstance();
        Long controllerRefId = 0L;
        if(request.getParameter("controllerRefId") != null){
            controllerRefId = Long.valueOf(request.getParameter("controllerRefId"));
        }// end if
        String result = contInfo.getDefaultLocation(theForm.getControllerList(), controllerRefId);

        JSONObject jo = new JSONObject();
        jo.put(KEY, "defaultLocation");
        jo.put(VAL, result);
        response.getWriter().println(jo);
        response.flushBuffer();

        log.debug("Exiting BatchAppDetailAction.loadDefaultLocation");
        return null;
    }// end loadDefaultLocation

    /**
     * Gets the reference data (dropdown data) required for the view.
     *
     * @param form
     *        action form
     * @param request
     *        HTTP servlet request
     * @throws Exception
     *         if an exception occurred
     */
    protected void getReferenceData(ActionForm form, HttpServletRequest request) throws Exception {
        log.debug("Entering BatchAppDetailAction - method getReferenceData()");
        BatchAppForm theForm = (BatchAppForm) form;
        ControllerInfo controllerInfo = ControllerInfo.getInstance();
        ApplicationInfo applicationInfo = ApplicationInfo.getInstance();
        CodeInfo codeInfo = CodeInfo.getInstance();
        AuthorizedUserInfo userInfo = AuthorizedUserInfo.getInstance();

        theForm.setControllerList(controllerInfo.getControllerListAsCodeModel(false));
        log.debug("loading controller list. list.size=" + (AppUtil.isEmpty(theForm.getControllerList()) ? "null/empty" : theForm.getControllerList().size()));
        theForm.setApplicationList(applicationInfo.getApplicationListForSystemAsCodeModel((theForm.getBatchApp().getSystem() == null || theForm.getBatchApp().getSystem().getSystemRefId() == null) ? 0L : theForm.getBatchApp().getSystem().getSystemRefId()));
        log.debug("loading application list. list.size=" + (AppUtil.isEmpty(theForm.getApplicationList()) ? "null/empty" : theForm.getApplicationList().size()));
        theForm.setSystemList(SystemInfo.getInstance().getSystemListAsCodeModel());
        log.debug("loading system list. list.size=" + (AppUtil.isEmpty(theForm.getSystemList()) ? "null/empty" : theForm.getSystemList().size()));
        theForm.setFrequencyCodes(codeInfo.getFrequencyCodes(false));
        log.debug("loading frequency list. list.size=" + (AppUtil.isEmpty(theForm.getFrequencyCodes()) ? "null/empty" : theForm.getFrequencyCodes().size()));
        theForm.setRepeatCodes(codeInfo.getRepeatCodes());
        log.debug("loading repeat list. list.size=" + (AppUtil.isEmpty(theForm.getRepeatCodes()) ? "null/empty" : theForm.getRepeatCodes().size()));
        theForm.setWeekDays(ShamenUtil.getWeekDaysOptionList());
        log.debug("loading weekdays list. list.size=" + (AppUtil.isEmpty(theForm.getWeekDays()) ? "null/empty" : theForm.getWeekDays().size()));
        theForm.setBatchTypes(codeInfo.getBatchTypeCodes());
        log.debug("loading batch type codes list. list.size=" + (AppUtil.isEmpty(theForm.getBatchTypes()) ? "null/empty" : theForm.getBatchTypes().size()));
        theForm.setSelectType(ShamenUtil.getWeekNumberList());
        theForm.setDaysInMonth(ShamenUtil.getDaysOfMonthList());

        // set the point of contact code list
        theForm.setPocList(userInfo.getUsersListAsCode());

        log.debug("Exiting BatchAppDetailAction - method getReferenceData()");
    }// end getReferenceData

    /**
     * Sets object data to form for the view.
     *
     * @param theForm
     *        BatchAppForm action form
     * @param schedule
     *        ScheduleModel
     * @param toForm
     *        boolean true|false
     * @throws Exception
     *         if an exception occurred
     */
    protected void copyData(BatchAppForm theForm, ScheduleModel schedule, boolean toForm) throws Exception {
        log.debug("Entering BatchAppDetailAction - method copyData()");
        if(toForm){
            if(schedule != null){
                if("WKY".equalsIgnoreCase(schedule.getFrequencyCd())){
                    theForm.setSelectedWeekdayWeeks(schedule.getWeekdays());
                }else if("MTY".equalsIgnoreCase(schedule.getFrequencyCd())){
                    if("MWD".equalsIgnoreCase(schedule.getFrequencyTypeCd())){
                        theForm.setSelectedWeekdayMonth(schedule.getWeekdays());
                    }// end if
                }// end if/else
            }// end if
        }else{
            if("DLY".equalsIgnoreCase(schedule.getFrequencyCd())){
                schedule.setDayNumber(null);
                schedule.setFrequencyTypeCd(BLANK_CODE);
                schedule.setWeekdays(null);
                schedule.setWeekNumber(null);
            }else if("WKY".equalsIgnoreCase(schedule.getFrequencyCd())){
                schedule.setDayNumber(null);
                schedule.setWeekNumber(null);
                schedule.setFrequencyTypeCd("WWD");
                schedule.setRecur(0L);
                schedule.setWeekdays(theForm.getSelectedWeekdayWeeks());
                theForm.setSelectedWeekdayWeeks(null);
            }else if("MTY".equalsIgnoreCase(schedule.getFrequencyCd())){
                schedule.setRecur(0L);
                if("MWD".equalsIgnoreCase(schedule.getFrequencyTypeCd())){
                    schedule.setDayNumber(null);
                    schedule.setWeekdays(theForm.getSelectedWeekdayMonth());
                    theForm.setSelectedWeekdayMonth(null);
                }else if("DOM".equalsIgnoreCase(schedule.getFrequencyTypeCd())){
                    schedule.setWeekNumber(null);
                    schedule.setWeekdays(null);
                }
            }// end if/else...if
        }// end if/else
        log.debug("Exiting BatchAppDetailAction - method copyData()");
    }// end copyData

    /**
     * This method converts a list of BatchAppCollectionModel to an String [] and sets the retrieved to the a map on the form for future check.
     *
     * @param selectedList
     *        List<BatchAppCollectionModel>
     * @param theForm
     *        BatchAppForm
     * @return String[]
     */
    protected String[] convertSelectedToArray(List<BatchAppCollectionModel> selectedList, BatchAppForm theForm) {
        log.debug("Entering BatchAppDetailAction - method convertSelectedToArray()");
        String[] selected = null;
        if(!selectedList.isEmpty()){
            theForm.setBatchAppCollectionMap(new HashMap<Long, BatchAppCollectionModel>());
            selected = new String[selectedList.size()];
            for(int i = 0, j = selectedList.size();i < j;i++){
                selected[i] = String.valueOf(selectedList.get(i).getAssocBatchApp().getBatchRefId());
                theForm.getBatchAppCollectionMap().put(selectedList.get(i).getAssocBatchApp().getBatchRefId(), selectedList.get(i));
            }// end for
        }// end if
        log.debug("Exiting BatchAppDetailAction - method convertSelectedToArray()");
        return selected;
    }// end convertSelectedToArray

    /**
     * This method converts the selected values array from the form to a List<BatchAppCollectionModel>
     *
     * @param theForm
     *        BatchAppForm
     * @return List<BatchAppCollectionModel>
     * @throws BaseException
     *         if an exception occurred
     */
    protected List<BatchAppCollectionModel> convertSelected(BatchAppForm theForm) throws BaseException {
        log.debug("Entering BatchAppDetailAction - method convertSelected()");
        String[] selected = theForm.getSelectedValues();
        List<BatchAppCollectionModel> result = null;

        if(!StringUtil.isNullOrEmptyStringArray(selected)){
            result = new ArrayList<BatchAppCollectionModel>();
            Long collectionRefId = 0L;
            BatchAppCollectionModel model = null;
            for(int i = 0, j = selected.length;i < j;i++){
                collectionRefId = (AppUtil.isEmpty(theForm.getBatchAppCollectionMap()) || theForm.getBatchAppCollectionMap().get(Long.valueOf(selected[i])) == null) ? 0L : theForm.getBatchAppCollectionMap().get(Long.valueOf(selected[i])).getCollectionRefId();
                model = new BatchAppCollectionModel(collectionRefId, new BatchAppModel(theForm.getBatchApp().getBatchRefId()), new BatchAppModel(Long.valueOf(selected[i])), Long.valueOf(i));
                result.add(model);
            }// end for
            if(!AppUtil.isEmpty(theForm.getBatchAppCollectionMap())){
                Set<Long> keys = theForm.getBatchAppCollectionMap().keySet();
                for(Iterator<Long> it = keys.iterator();it.hasNext();){
                    Long refId = it.next();
                    if(!StringUtil.arrayContains(String.valueOf(refId), selected)){
                        model = theForm.getBatchAppCollectionMap().get(refId);
                        model.setDeleteIndicator("Y");
                        result.add(model);
                    }// end if

                }// end for
            }// end if
        }// end if
        log.debug("Exiting BatchAppDetailAction - method convertSelected()");
        return result;
    }// end convertSelected

    /**
     * This method captures any data that is submitted from any of the list page This is required for enhanced touch list tag library
     *
     * @param mapping
     *        (REQUIRED)- Information that the controller, RequestProcessor, knows about mapping the request to the instance of this Action class.
     * @param actionForm
     *        (REQUIRED) - A JavaBean associated with mapping. It will have its properties initialized from the request parameters. Subsequently, properties of this bean have been populated
     * @param request
     *        (REQUIRED) - Provides data including parameter name and values, attributes, and an input stream
     * @param response
     *        (OPTIONAL) - Assist in sending a response to the client
     * @return ActionForward - Describes where and how control should be forwarded
     */
    public ActionForward updateListPageData(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) {
        log.debug("updateListPageData for pagination");
        ActionForward forward = new ActionForward(mapping.getInput());
        BatchAppForm theForm = (BatchAppForm) actionForm;
        BatchAppInfo batchInfo = BatchAppInfo.getInstance();
        try{
            ShamenPaginatedList pl = new ShamenPaginatedList(request, 0);
            pl.setTotalNumberOfRows(theForm.getPaginatedList().getFullListSize());
            pl.setList(batchInfo.getRunStatusList(theForm.getBatchApp().getBatchRefId(), Long.valueOf(pl.getFirstRecordIndex()) + 1, Long.valueOf(pl.getLastRecordIndex())));
            theForm.setPaginatedList(pl);
        }catch(BaseException e){
            log.error("Exception occurred in BatchAppDetailAction.updateListPageData. message: " + e.getMessage(), e);
            forward = mapping.findForward(FAILURE);
        }// end try/catch
        setupTabs(request, "BatchInfoLink", ShamenUtil.showActivateTab(theForm.getBatchApp().getScheduleList()));
        return forward;
    }// end updateListPageData
}// end class
