package gov.doc.isu.shamen.action;

import static gov.doc.isu.dwarf.resources.Constants.CANCEL;
import static gov.doc.isu.dwarf.resources.Constants.FAILURE;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import gov.doc.isu.dwarf.resources.Constants;
import gov.doc.isu.dwarf.util.AppUtil;
import gov.doc.isu.shamen.business.AuthorizedUserInfo;
import gov.doc.isu.shamen.business.BatchAppInfo;
import gov.doc.isu.shamen.business.SystemInfo;
import gov.doc.isu.shamen.form.SystemForm;
import gov.doc.isu.shamen.models.AuthorizedUserModel;
import gov.doc.isu.shamen.models.SystemModel;

/**
 * Action class that handles all request for the System Edit Screen.
 *
 * @author <strong>Shane Duncan</strong>
 */
public class SystemDetailAction extends ShamenDispatchAction {
    private static Logger log = Logger.getLogger("gov.doc.isu.shamen.action.SystemDetailAction");

    /**
     * This method adds a new system.
     *
     * @param mapping
     *        (REQUIRED)- Information that the system, RequestProcessor, knows about mapping the request to the instance of this Action class.
     * @param form
     *        (REQUIRED) - A JavaBean associated with mapping. It will have its properties initialized from the request parameters. Subsequently, properties of this bean have been populated
     * @param request
     *        (REQUIRED) - Provides data including parameter name and values, attributes, and an input stream
     * @param response
     *        (OPTIONAL) - Assist in sending a response to the client
     * @return ActionForward - Describes where and how control should be forwarded
     */
    public ActionForward addSystem(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering SystemDetailAction.addSystem");
        SystemForm theForm = (SystemForm) form;
        ActionForward forward = mapping.findForward("editSystem");
        // This needed for export of PDF view
        Constants.LIST_NAME = "SHAMEN SYSTEM INFORMATION";
        theForm.semiFlush();
        try{
            theForm.setSystem(new SystemModel());
            theForm.getSystem().setPointOfContact(new AuthorizedUserModel());
            setUserRefIds(theForm.getSystem(), request);
            AuthorizedUserInfo userInfo = AuthorizedUserInfo.getInstance();
            // set the point of contact code list
            theForm.setPocList(userInfo.getUsersListAsCode());
            setupTabs(request, "SystemInfoLink", true);
        }catch(Exception e){
            log.error("Exception occurred in SystemDetailAction.addSystem. ", e);
            forward = mapping.findForward(FAILURE);
        }// end try
        log.debug("Exiting SystemDetailAction.addSystem");
        return forward;
    }// end addSystem

    /**
     * This method gets the system for edit.
     *
     * @param mapping
     *        (REQUIRED)- Information that the system, RequestProcessor, knows about mapping the request to the instance of this Action class.
     * @param form
     *        (REQUIRED) - A JavaBean associated with mapping. It will have its properties initialized from the request parameters. Subsequently, properties of this bean have been populated
     * @param request
     *        (REQUIRED) - Provides data including parameter name and values, attributes, and an input stream
     * @param response
     *        (OPTIONAL) - Assist in sending a response to the client
     * @return ActionForward - Describes where and how control should be forwarded
     */
    public ActionForward editSystem(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering SystemDetailAction.editSystem");
        SystemForm theForm = (SystemForm) form;
        ActionForward forward = mapping.findForward("editSystem");
        SystemInfo systemInfo = SystemInfo.getInstance();
        // This needed for export of PDF view
        Constants.LIST_NAME = "SHAMEN SYSTEM INFORMATION";
        theForm.semiFlush();
        theForm.setNoRecords(true);
        SystemModel system = new SystemModel();
        setCaller(theForm, request);
        try{
            AuthorizedUserInfo userInfo = AuthorizedUserInfo.getInstance();
            Long sysRef = 0L;
            // set the point of contact code list
            theForm.setPocList(userInfo.getUsersListAsCode());
            log.debug("loading the system list for display.");
            if(request.getParameter("systemRefId") != null){
                sysRef = Long.valueOf(request.getParameter("systemRefId"));
            }// end if
            system = systemInfo.getSystem(sysRef);
            if(sysRef == 1L){
                theForm.setRestrict(true);
            }// end if
            theForm.setSystem(system);
            request.getSession().setAttribute("sessionBatchList", theForm.getSystem().getBatchApps());
            request.getSession().setAttribute("sessionAppList", theForm.getSystem().getApplications());
            setUserRefIds(theForm.getSystem(), request);
            theForm.setSystemData(SystemInfo.getInstance().getSystemChart(system));
            setupTabs(request, "SystemInfoLink", true);
        }catch(Exception e){
            log.error("Exception occurred in SystemDetailAction.editSystem. ", e);
            forward = mapping.findForward(FAILURE);
        }// end try
        log.debug("Exiting SystemDetailAction.editSystem");
        return forward;
    }// end editSystem

    /**
     * This method saves the system.
     *
     * @param mapping
     *        (REQUIRED)- Information that the system, RequestProcessor, knows about mapping the request to the instance of this Action class.
     * @param form
     *        (REQUIRED) - A JavaBean associated with mapping. It will have its properties initialized from the request parameters. Subsequently, properties of this bean have been populated
     * @param request
     *        (REQUIRED) - Provides data including parameter name and values, attributes, and an input stream
     * @param response
     *        (OPTIONAL) - Assist in sending a response to the client
     * @return ActionForward - Describes where and how control should be forwarded
     */
    public ActionForward saveSystem(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering SystemDetailAction.saveSystem");
        SystemForm theForm = (SystemForm) form;
        ActionForward forward = mapping.findForward(Constants.REFRESH);
        ActionMessages errors = new ActionMessages();
        SystemInfo systemInfo = SystemInfo.getInstance();
        SystemModel system = theForm.getSystem();
        setCaller(theForm, request);
        try{
            if(theForm.getCaller() != null){
                forward = new ActionForward(mapping.findForward(theForm.getCaller()).getPath(), true);
            }// end if
            if(request.getParameter("cancel") != null){
                if(theForm.getCaller() == null){
                    forward = mapping.findForward(CANCEL);
                }// end if
                return forward;
            }// end if
            errors = theForm.validate(mapping, request);
            if(!errors.isEmpty()){
                this.saveErrors(request, errors);
                setupTabs(request, "SystemInfoLink", true);
                return mapping.getInputForward();
            }else{
                if(!AppUtil.isNotNullAndZero(system.getSystemRefId())){
                    systemInfo.saveSystem(system);
                }else{
                    system.setUpdateTime(AppUtil.getSQLTimestamp());
                    systemInfo.updateSystem(system);
                }// end if/else
            }// end if/else
            setupTabs(request, null, true);

        }catch(Exception e){
            log.error("Exception occurred in SystemDetailAction.saveSystem. ", e);
            forward = mapping.findForward(FAILURE);
        }finally{
            if(!errors.isEmpty()){
                this.saveErrors(request, errors);
                forward = mapping.getInputForward();
            } // end if
        }// end try
        log.debug("Exiting SystemDetailAction.saveSystem");
        return forward;
    }// end saveSystem

    /**
     * This method updates batch app record by setting active indicator to 'Y' or 'N' depending on request parameter.
     *
     * @param mapping
     *        (REQUIRED)- Information that the system, RequestProcessor, knows about mapping the request to the instance of this Action class.
     * @param form
     *        (REQUIRED) - A JavaBean associated with mapping. It will have its properties initialized from the request parameters. Subsequently, properties of this bean have been populated
     * @param request
     *        (REQUIRED) - Provides data including parameter name and values, attributes, and an input stream
     * @param response
     *        (OPTIONAL) - Assist in sending a response to the client
     * @return ActionForward - Describes where and how control should be forwarded
     */
    public ActionForward updateBatchAppActivate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering SystemDetailAction.updateBatchAppActivate");
        SystemForm theForm = (SystemForm) form;
        ActionForward forward = mapping.findForward("editSystem");
        SystemInfo systemInfo = SystemInfo.getInstance();
        BatchAppInfo batchInfo = BatchAppInfo.getInstance();
        // This needed for export of PDF view
        Constants.LIST_NAME = "SHAMEN SYSTEM INFORMATION";
        theForm.semiFlush();
        theForm.setNoRecords(true);
        String activate = "";
        Long batchRefId = 0L;
        try{
            if(request.getParameter("activate") != null){
                activate = request.getParameter("activate");
            }// end if
            if(request.getParameter("batchRefId") != null){
                batchRefId = Long.valueOf(request.getParameter("batchRefId"));
            }// end if
            batchInfo.updateBatchActiveInd(activate, batchRefId, getAppUser(request).getUserRefId());
            SystemModel system = systemInfo.getSystem(theForm.getSystem().getSystemRefId());
            theForm.setSystem(system);
            setUserRefIds(theForm.getSystem(), request);
            setupTabs(request, "SystemInfoLink", true);
        }catch(Exception e){
            log.error("Exception occurred in SystemDetailAction.updateBatchAppActivate. ", e);
            forward = mapping.findForward(FAILURE);
        }// end try
        log.debug("Exiting SystemDetailAction.updateBatchAppActivate");
        return forward;
    }// end updateBatchAppActivate

    /**
     * This method updates all batches by setting active indicator to 'Y' or 'N' depending on request parameter.
     *
     * @param mapping
     *        (REQUIRED)- Information that the system, RequestProcessor, knows about mapping the request to the instance of this Action class.
     * @param form
     *        (REQUIRED) - A JavaBean associated with mapping. It will have its properties initialized from the request parameters. Subsequently, properties of this bean have been populated
     * @param request
     *        (REQUIRED) - Provides data including parameter name and values, attributes, and an input stream
     * @param response
     *        (OPTIONAL) - Assist in sending a response to the client
     * @return ActionForward - Describes where and how control should be forwarded
     */
    public ActionForward updateAllBatch(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering SystemDetailAction.updateAllBatch");
        SystemForm theForm = (SystemForm) form;
        ActionForward forward = mapping.findForward("editSystem");
        SystemInfo systemInfo = SystemInfo.getInstance();
        // This needed for export of PDF view
        Constants.LIST_NAME = "SHAMEN SYSTEM INFORMATION";
        theForm.semiFlush();
        theForm.setNoRecords(true);
        // String activate = "";
        Long systemRefId = 0L;
        try{
            // if(request.getParameter("activate") != null){
            // activate = request.getParameter("activate");
            // }// end if
            if(request.getParameter("systemRefId") != null){
                systemRefId = Long.valueOf(request.getParameter("systemRefId"));
            }// end if
             // batchInfo.updateSystemBatchApps(activate, systemRefId, getAppUser(request).getUserRefId());
            if(request.getParameter("list") != null){
                return mapping.findForward(Constants.REFRESH);
            }// end if
            SystemModel system = systemInfo.getSystem(systemRefId);
            theForm.setSystem(system);
            setUserRefIds(theForm.getSystem(), request);
            setupTabs(request, "SystemInfoLink", true);
        }catch(Exception e){
            log.error("Exception occurred in SystemDetailAction.updateAllBatch. ", e);
            forward = mapping.findForward(FAILURE);
        }// end try
        log.debug("Exiting SystemDetailAction.updateAllBatch");
        return forward;
    }// end updateAllBatch

    /**
     * Set the caller parameter into the form
     *
     * @param theForm
     *        action form
     * @param request
     *        HTTP servlet request
     */
    private void setCaller(SystemForm theForm, HttpServletRequest request) {
        log.debug("Entering setCaller");
        log.debug("Set the caller parameter into the form");
        if(request.getParameter("caller") != null){
            theForm.setCaller(request.getParameter("caller"));
        }// end if
        log.debug("Exiting setCaller");
    }// end setCaller

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
        setupTabs(request, "SystemInfoLink", true);
        return new ActionForward(mapping.getInput());
    }// end updateListPageData
}// end SystemDetailAction
