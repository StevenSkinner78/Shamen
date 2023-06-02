/**
 * @author <strong>Brian Hicks</strong> JCCC, Mar 7, 2016
 */
package gov.doc.isu.shamen.action;

import static gov.doc.isu.dwarf.resources.Constants.FAILURE;
import static gov.doc.isu.dwarf.resources.Constants.KEY;
import static gov.doc.isu.dwarf.resources.Constants.LIST;
import static gov.doc.isu.dwarf.resources.Constants.VAL;
import static gov.doc.isu.shamen.resources.AppConstants.APPLICATION_FIELDS;
import static gov.doc.isu.shamen.resources.AppConstants.DEFAULT_PAGE_SIZE;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import gov.doc.isu.dwarf.model.CodeModel;
import gov.doc.isu.dwarf.model.ColumnModel;
import gov.doc.isu.dwarf.resources.Constants;
import gov.doc.isu.dwarf.util.AppUtil;
import gov.doc.isu.dwarf.util.StringUtil;
import gov.doc.isu.shamen.business.ApplicationInfo;
import gov.doc.isu.shamen.business.AuthorizedUserInfo;
import gov.doc.isu.shamen.business.CodeInfo;
import gov.doc.isu.shamen.business.SystemInfo;
import gov.doc.isu.shamen.form.ApplicationForm;
import gov.doc.isu.shamen.models.ApplicationModel;
import gov.doc.isu.shamen.util.ShamenUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * <p>
 * Action class that handles all request for Web Application List screen in Shamen.
 *
 * @author <strong>Brian Hicks</strong> JCCC, Mar 7, 2016
 * @author <strong>Steven Skinner</strong> JCCC, Aug 7, 2017
 */
public class ApplicationAction extends ShamenDispatchAction {
    private static Logger log = Logger.getLogger("gov.doc.isu.shamen.action.ApplicationAction");

    /**
     * This method gets the list of applications from the DB.
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
        log.debug("Entering ApplicationAction.list() Parameters: " + new Object[]{mapping, form, request, response});
        log.debug("This method gets the list of applications from the DB.");
        ActionForward forward = mapping.findForward(LIST);
        try{
            ApplicationForm theForm = (ApplicationForm) form;
            log.debug("Getting a list of the application types for display in select box on ApplicationForm.");
            getReferenceData(theForm, request);
            if(theForm.getFilterSearchObject() != null && !checkFilterSearchObject(theForm.getFilterSearchObject())){
                theForm.setSessionList(ApplicationInfo.getInstance().getApplicationList());
                return search(mapping, theForm, request, response);
            }// end if
            Constants.LIST_NAME = "WEB APP LIST";
            theForm.semiFlush();
            theForm.setNoRecords(false);
            theForm.setGoButtonDisable("");
            log.debug("Setting the filter search object.");
            setFilterSearchObject(theForm, APPLICATION_FIELDS);
            log.debug("Loading the application list for display.");
            List<ApplicationModel> appList = ApplicationInfo.getInstance().getApplicationList();
            if(AppUtil.isEmpty(appList)){
                log.debug("No records were returned for application list.");
                appList = new ArrayList<ApplicationModel>();
                theForm.setNoRecords(true);
                theForm.setGoButtonDisable("disabled");
                ApplicationModel app = new ApplicationModel();
                app.setColumnData(ShamenUtil.getNoRecordsFoundForDisplay(null, APPLICATION_FIELDS));
                app.setStatusInd("NONE");
                appList.add(app);
            }// end if
            theForm.setDatalist(appList);
            theForm.setSessionList(appList);
            theForm.setColumnInfo(appList.get(0).getColumnData());
            if(StringUtil.isNullOrEmpty(theForm.getPageSize())){
                theForm.setPageSize(DEFAULT_PAGE_SIZE);
            }// end if
            setupTabs(request, null, true);
        }catch(Exception e){
            log.error("Exception occurred in ApplicationAction.list(). Message is: " + e.getMessage(), e);
            forward = mapping.findForward(FAILURE);
        }// end try/catch
        log.debug("Exiting ApplicationAction.list()");
        return forward;
    }// end list

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
     */
    @SuppressWarnings("unchecked")
    public ActionForward search(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering ApplicationAction.search() Parameters: " + new Object[]{mapping, form, request, response});
        log.debug("This method uses the filter object to search sessionList on form for matching records.");
        ActionForward forward = mapping.findForward(LIST);
        try{
            ApplicationForm theForm = (ApplicationForm) form;
            theForm.setNoRecords(false);
            theForm.setGoButtonDisable("");
            List<ApplicationModel> appList = new ArrayList<ApplicationModel>();
            List<ColumnModel> fieldList = theForm.getFilterSearchObject();
            if(!AppUtil.isEmpty(fieldList) || checkFilterSearchObject(fieldList)){
                List<ApplicationModel> list = (ArrayList<ApplicationModel>) theForm.getSessionList();
                for(int i = 0, j = list.size();i < j;i++){
                    if(ShamenUtil.filter(fieldList, list.get(i).getColumnData())){
                        appList.add(list.get(i));
                    }// end if
                }// end for
                theForm.setFilterSearchObject(fieldList);
            }// end if
            log.debug("Setting appList based on search object. appList.size() =" + (AppUtil.isEmpty(appList) ? "null/empty" : appList.size()));
            if(AppUtil.isEmpty(appList)){
                log.debug("Checking if sessionList is empty.");
                if(AppUtil.isEmpty(theForm.getSessionList())){
                    theForm.setNoRecords(true);
                    theForm.setGoButtonDisable("disabled");
                    appList = (List<ApplicationModel>) theForm.getDatalist();
                    log.debug("Checking if all input boxes are empty.  If true, list reset to full list.");
                }else if(checkFilterSearchObject(fieldList)){
                    appList = (List<ApplicationModel>) theForm.getSessionList();
                }else{
                    theForm.setNoRecords(true);
                    ApplicationModel app = new ApplicationModel();
                    app.setColumnData(ShamenUtil.getNoRecordsFoundForSearch(null, APPLICATION_FIELDS));
                    appList.add(app);
                }// end if/else
            }// end if
            theForm.setDatalist(appList);
            theForm.setSubType(null);
            setupTabs(request, null, true);
        }catch(Exception e){
            log.error("Exception caught in ApplicationAction.search(). Message is: " + e.getMessage(), e);
            forward = mapping.findForward(FAILURE);
        }// end try/catch
        log.debug("Exiting ApplicationAction.search()");
        return forward;
    }// end search

    /**
     * Used to (soft) delete an application record.
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
    public ActionForward deleteApplication(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering ApplicationAction.deleteApplication(). Parameters: " + new Object[]{mapping, form, request, response});
        log.debug("Used to (soft) delete an application record.");
        ApplicationForm theForm = (ApplicationForm) form;
        theForm.semiFlush();
        theForm.setNoRecords(false);
        Long applicationRefId = 0L;
        try{
            if(request.getParameter("applicationRefId") != null){
                applicationRefId = Long.valueOf(request.getParameter("applicationRefId"));
            }// end if
            ApplicationInfo.getInstance().deleteApplication(applicationRefId, getAppUser(request).getUserRefId());
            theForm.setAppList(ApplicationInfo.getInstance().getApplicationList());
            setupTabs(request, null, true);
        }catch(Exception e){
            log.error("Exception occurred in BatchAppAction.deleteApplication(). Message is: " + e.getMessage(), e);
            return mapping.findForward(FAILURE);
        }// end try/catch
        log.debug("Exiting ApplicationAction.deleteApplication()");
        return list(mapping, form, request, response);
    }// end deleteApplication

    /**
     * Used to start the asynchronous application status update.
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
    public ActionForward updateApplicationStatuses(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering ApplicationAction.updateApplicationStatuses(). Parameters: " + new Object[]{mapping, form, request, response});
        log.debug("This method is Used to start the asynchronous application status update.");
        try{
            ApplicationInfo.getInstance().updateApplicationStatuses();
            setupTabs(request, null, true);
        }catch(Exception e){
            log.error("Exception occurred in ApplicationAction.updateApplicationStatuses(). Message is: " + e.getMessage(), e);
            return mapping.findForward(FAILURE);
        }// end try/catch
        log.debug("Exiting ApplicationAction.updateApplicationStatuses()");
        return list(mapping, form, request, response);
    }// end updateApplicationStatuses

    /**
     * Used to start the asynchronous application status update for a single application.
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
    public ActionForward refreshSingleApplicationStatus(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering ApplicationAction.refreshSingleApplicationStatus(). Parameters: " + new Object[]{mapping, form, request, response});
        log.debug("This method is Used to start the asynchronous application status update.");
        try{
            Long applicationRefId = 0L;
            if(request.getParameter("applicationRefId") != null){
                applicationRefId = Long.valueOf(request.getParameter("applicationRefId"));
            }// end if
            ApplicationInfo.getInstance().refreshSingleApplicationStatus(applicationRefId);
            setupTabs(request, null, true);
        }catch(Exception e){
            log.error("Exception occurred in ApplicationAction.refreshSingleApplicationStatus(). Message is: " + e.getMessage(), e);
            return mapping.findForward(FAILURE);
        }// end try/catch
        log.debug("Exiting ApplicationAction.refreshSingleApplicationStatus()");
        return list(mapping, form, request, response);
    }// end refreshSingleApplicationStatus

    /**
     * Ajax call to load the statuses of web applications.
     *
     * @param mapping
     *        (REQUIRED)- Information that the controller, RequestProcessor, knows about mapping the request to the instance of this Action class.
     * @param form
     *        (REQUIRED) - A JavaBean associated with mapping. It will have its properties initialized from the request parameters. Subsequently, properties of this bean have been populated
     * @param request
     *        (REQUIRED) - Provides data including parameter name and values, attributes, and an input stream
     * @param response
     *        (OPTIONAL) - Assist in sending a response to the client
     * @return null null
     * @throws Exception
     *         Exception
     */
    public ActionForward loadStatuses(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        log.debug("Entering BatchAppAction.loadStatuses");
        List<ApplicationModel> appList = ApplicationInfo.getInstance().getApplicationList();
        JSONArray jsnArray = new JSONArray();
        if(AppUtil.isEmpty(appList)){
            appList = new ArrayList<ApplicationModel>();
        }// end if
        for(int i = 0, j = appList.size();i < j;i++){
            JSONObject jo = new JSONObject();
            jo.put(KEY, appList.get(i).getApplicationRefId());
            jo.put(VAL, appList.get(i).getStatusInd());
            jsnArray.add(jo);
        }// end for
        response.getWriter().println(jsnArray);
        response.flushBuffer();
        log.debug("Exiting BatchAppAction.loadStatuses");
        return null;
    }// end loadStatuses

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
        log.debug("Entering ApplicationAction - method getReferenceData()");

        ApplicationForm theForm = (ApplicationForm) form;
        AuthorizedUserInfo userInfo = AuthorizedUserInfo.getInstance();
        // log.debug("loading app type list. list.size=" + (AppUtil.isEmpty(theForm.getAppTypeList()) ? "null/empty" : theForm.getAppTypeList().size()));
        // theForm.setAppTypeList(CodeInfo.getInstance().getApplicationTypeCodes());
        log.debug("loading confirmed status list. list.size=" + (AppUtil.isEmpty(theForm.getStatusIndList()) ? "null/empty" : theForm.getStatusIndList().size()));
        theForm.setStatusIndList(CodeInfo.getInstance().getApplicationStatusCodes());
        log.debug("loading requested status list. list.size=" + (AppUtil.isEmpty(theForm.getStatusIndList()) ? "null/empty" : theForm.getStatusIndList().size()));
        theForm.setConfirmedStatusIndList(CodeInfo.getInstance().getApplicationStatusCodes());
        theForm.getConfirmedStatusIndList().add(new CodeModel("UNR", "Unresponsive"));
        log.debug("loading system list. list.size=" + (AppUtil.isEmpty(theForm.getSystemList()) ? "null/empty" : theForm.getSystemList().size()));
        theForm.setSystemList(SystemInfo.getInstance().getSystemListAsCodeModel());
        // set the point of contact code list
        theForm.setPocList(userInfo.getUsersListAsCode());
        log.debug("Exiting ApplicationAction - method getReferenceData()");
    }// end getReferenceData

}// end ApplicationAction
