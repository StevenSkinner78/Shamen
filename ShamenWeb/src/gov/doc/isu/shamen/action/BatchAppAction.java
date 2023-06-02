package gov.doc.isu.shamen.action;

import static gov.doc.isu.dwarf.resources.Constants.FAILURE;
import static gov.doc.isu.dwarf.resources.Constants.LIST;
import static gov.doc.isu.shamen.resources.AppConstants.BATCH_APP_FIELDS;
import static gov.doc.isu.shamen.resources.AppConstants.DEFAULT_PAGE_SIZE;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import gov.doc.isu.dwarf.model.ColumnModel;
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
import gov.doc.isu.shamen.models.BatchAppModel;
import gov.doc.isu.shamen.models.ControllerModel;
import gov.doc.isu.shamen.models.RunStatusModel;
import gov.doc.isu.shamen.models.ScheduleModel;
import gov.doc.isu.shamen.util.ShamenUtil;

/**
 * Action class that handles all request for the Batch App and Batch Collections.
 * 
 * @author <strong>Steven Skinner</strong>
 */
public class BatchAppAction extends ShamenDispatchAction {
    private static Logger log = Logger.getLogger("gov.doc.isu.shamen.action.BatchAppAction");

    /**
     * This method gets the list of batch apps.
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
    public ActionForward list(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering BatchAppAction.list");
        ActionForward forward = mapping.findForward(LIST);
        try{
            BatchAppForm theForm = (BatchAppForm) form;
            BatchAppInfo batchInfo = BatchAppInfo.getInstance();
            ApplicationInfo applicationInfo = ApplicationInfo.getInstance();
            ControllerInfo controllerInfo = ControllerInfo.getInstance();
            CodeInfo codeInfo = CodeInfo.getInstance();
            AuthorizedUserInfo userInfo = AuthorizedUserInfo.getInstance();
            // set the application code list
            theForm.setApplicationList(applicationInfo.getApplicationListAsCodeModel());
            // set the controller code list
            theForm.setControllerList(controllerInfo.getControllerListAsCodeModel(true));

            theForm.setSystemList(SystemInfo.getInstance().getSystemListAsCodeModel());

            // set the point of contact code list
            theForm.setPocList(userInfo.getUsersListAsCode());

            theForm.setBatchTypes(codeInfo.getBatchTypeCodes());
            if(request.getParameter("toggleColMem") != null || theForm.getFilterSearchObject() != null && !checkFilterSearchObject(theForm.getFilterSearchObject())){
                theForm.setSessionList(batchInfo.getBatchList());
                return search(mapping, theForm, request, response);
            }// end if
             // This needed for export of PDF view
            Constants.LIST_NAME = "BATCH APP LIST";
            theForm.semiFlush();
            theForm.setNoRecords(false);
            theForm.setGoButtonDisable("");
            List<BatchAppModel> batchList = new ArrayList<BatchAppModel>();
            log.debug("setting search object.");
            setFilterSearchObject(theForm, BATCH_APP_FIELDS);
            log.debug("loading the batch list for display.");
            batchList = batchInfo.getBatchList();
            if(AppUtil.isEmpty(batchList)){
                log.debug("No records were returned for batch app.");
                batchList = new ArrayList<BatchAppModel>();
                theForm.setNoRecords(true);
                theForm.setGoButtonDisable("disabled");
                BatchAppModel t = new BatchAppModel();
                t.setColumnData(ShamenUtil.getNoRecordsFoundForDisplay(null, BATCH_APP_FIELDS));
                batchList.add(t);
            }// end if
            theForm.setDatalist(batchInfo.removeCollectionMembers(batchList));
            theForm.setSessionList(batchList);
            theForm.setColumnInfo(batchList.get(0).getColumnData());
            if(StringUtil.isNullOrEmpty(theForm.getPageSize())){
                theForm.setPageSize(DEFAULT_PAGE_SIZE);
            }// end if
            setupTabs(request, null, true);
        }catch(Exception e){
            log.error("Exception occurred in BatchAppAction.list. ", e);
            forward = mapping.findForward(FAILURE);
        }// end try
        log.debug("Exiting BatchAppAction.list");
        return forward;
    }// end list

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
        log.debug("Entering BatchAppAction.deleteBatch");
        BatchAppForm theForm = (BatchAppForm) form;
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
            log.error("Exception occurred in BatchAppAction.deleteBatch. ", e);
            return mapping.findForward(FAILURE);
        }// end try
        log.debug("Exiting BatchAppAction.deleteBatch");
        return list(mapping, theForm, request, response);
    }// end deleteBatch

    /**
     * This method starts the batch app via jms message.
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
    public ActionForward startBatch(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering BatchAppAction.startBatch");
        ActionForward forward = mapping.findForward("viewStatusList");
        BatchAppForm theForm = (BatchAppForm) form;
        BatchAppInfo batchInfo = BatchAppInfo.getInstance();
        // ControllerInfo controllerInfo = ControllerInfo.getInstance();
        // This needed for export of PDF view
        Constants.LIST_NAME = "SHAMEN BATCH APP";
        theForm.semiFlush();
        theForm.setNoRecords(false);
        Long batchRefId = 0L;
        String jobParameters = " ";
        try{
            if(request.getParameter("batchRefId") != null){
                batchRefId = Long.valueOf(request.getParameter("batchRefId"));
            }// end if
            if(request.getParameter("jobParameters") != null){
                jobParameters = request.getParameter("jobParameters");
            }// end if
            RunStatusModel model = new RunStatusModel();
            setUserRefIds(model, request);
            batchInfo.startBatchApp(batchRefId, model, jobParameters);
            setupTabs(request, null, true);
        }catch(Exception e){
            log.error("Exception occurred in BatchAppAction.startBatch. ", e);
            forward = mapping.findForward(FAILURE);
        }// end try
        log.debug("Exiting BatchAppAction.startBatch");
        return forward;
    }// end startBatch

    /**
     * This method uses the filter object to search sessionList on form for matching records.
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
     * @throws Exception
     *         - if an Exception occurred
     */
    @SuppressWarnings("unchecked")
    public ActionForward search(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.debug("Entering BatchAppAction - method search()");
        ActionForward forward = mapping.findForward(LIST);
        ActionMessages errors = new ActionMessages();
        try{
            BatchAppForm theForm = (BatchAppForm) form;
            errors = theForm.validateSearchFilters(getResources(request));
            if(!errors.isEmpty()){
                this.saveErrors(request, errors);
                return forward;
            }// end if
            theForm.setNoRecords(false);
            theForm.setGoButtonDisable("");
            List<BatchAppModel> batchList = new ArrayList<BatchAppModel>();
            List<ColumnModel> fieldList = theForm.getFilterSearchObject();
            if(!AppUtil.isEmpty(fieldList) || checkFilterSearchObject(fieldList)){
                List<BatchAppModel> list = (ArrayList<BatchAppModel>) theForm.getSessionList();
                for(int i = 0, j = list.size();i < j;i++){

                    if(ShamenUtil.filter(fieldList, list.get(i).getColumnData())){
                        batchList.add(list.get(i));
                    }// end if
                }// end for
                theForm.setFilterSearchObject(fieldList);
            }// end if
            log.debug("setting batchList based on search object. batchList.size=" + (AppUtil.isEmpty(batchList) ? "null/empty" : batchList.size()));
            if(AppUtil.isEmpty(batchList)){
                // check to see if session list is empty
                if(AppUtil.isEmpty(theForm.getSessionList())){
                    theForm.setNoRecords(true);
                    theForm.setGoButtonDisable("disabled");
                    batchList = (ArrayList<BatchAppModel>) theForm.getDatalist();
                    // check to see if all input boxes are empty. If so the reset list to full list.
                }else if(checkFilterSearchObject(fieldList)){
                    batchList = (ArrayList<BatchAppModel>) theForm.getSessionList();
                    if(!theForm.isShowCollectionsInList()){
                        batchList = BatchAppInfo.getInstance().removeCollectionMembers(batchList);
                    }// end if
                }else{
                    theForm.setNoRecords(true);
                    BatchAppModel t = new BatchAppModel();
                    t.setController(new ControllerModel());
                    t.setScheduleList(new ArrayList<ScheduleModel>());
                    t.setColumnData(ShamenUtil.getNoRecordsFoundForSearch(null, BATCH_APP_FIELDS));
                    batchList.add(t);
                }// end else
            }else{
                if(!theForm.isShowCollectionsInList()){
                    batchList = BatchAppInfo.getInstance().removeCollectionMembers(batchList);
                }// end if
            }// end else
            theForm.setDatalist(batchList);
            theForm.setSubType(null);
            setupTabs(request, null, true);
        }catch(Exception e){
            log.error("Exception caught in BatchAppAction method search.", e);
            forward = mapping.findForward(FAILURE);
        }// end catch
        log.debug("Exiting BatchAppAction - method search()");
        return forward;
    }// end search

}// end BatchAppAction
