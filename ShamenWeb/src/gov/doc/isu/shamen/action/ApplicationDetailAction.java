/**
 * @author <strong>Brian Hicks</strong> JCCC, Mar 7, 2016
 */
package gov.doc.isu.shamen.action;

import static gov.doc.isu.dwarf.resources.Constants.CANCEL;
import static gov.doc.isu.dwarf.resources.Constants.FAILURE;
import static gov.doc.isu.dwarf.resources.Constants.REFRESH;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import gov.doc.isu.dwarf.model.CodeModel;
import gov.doc.isu.dwarf.util.AppUtil;
import gov.doc.isu.shamen.business.ApplicationInfo;
import gov.doc.isu.shamen.business.AuthorizedUserInfo;
import gov.doc.isu.shamen.business.CodeInfo;
import gov.doc.isu.shamen.business.SystemInfo;
import gov.doc.isu.shamen.form.ApplicationForm;
import gov.doc.isu.shamen.jpa.entity.ApplicationStatusCodeEntity;
import gov.doc.isu.shamen.models.ApplicationInstanceModel;
import gov.doc.isu.shamen.models.ApplicationModel;
import gov.doc.isu.shamen.models.AuthorizedUserModel;
import gov.doc.isu.shamen.models.SystemModel;

/**
 * Action class that handles all request for the Application details and editing
 *
 * @author <strong>Brian Hicks</strong> JCCC, Mar 7, 2016
 */
public class ApplicationDetailAction extends ShamenDispatchAction {
    private static final String MY_CLASS_NAME = "gov.doc.isu.shamen.action.ApplicationDetailAction";
    private static Logger log = Logger.getLogger(MY_CLASS_NAME);

    /**
     * Used to update all applications listed on the screen by changing their application status.
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
        log.debug("Entering ApplicationDetailAction.updateAll(). Parameters: " + new Object[]{mapping, form, request, response});
        log.debug("Used to update all applications listed on the screen by changing their application status.");
        ActionForward forward = mapping.findForward(REFRESH);
        ApplicationInfo appInfo = ApplicationInfo.getInstance();
        CodeInfo codeInfo = CodeInfo.getInstance();
        ApplicationStatusCodeEntity statusInd = null;
        try{
            if(request.getParameter("statusInd") != null){
                statusInd = codeInfo.getStatusEntityByCode(request.getParameter("statusInd"));
            }// end if
            appInfo.updateAllApps(statusInd, getAppUser(request).getUserRefId());
            setupTabs(request, null, true);
        }catch(Exception e){
            log.error("Exception occurred in ApplicationDetailAction.updateAll(). Message is: " + e.getMessage(), e);
            forward = mapping.findForward(FAILURE);
        }// end try
        log.debug("Exiting ApplicationDetailAction.updateAll().");
        return forward;
    }// end updateAll

    /**
     * Used to get the application for editing.
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
    public ActionForward editApplication(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering ApplicationDetailAction.editApplication(). Parameters: " + new Object[]{mapping, form, request, response});
        log.debug("Used to get the application for editing.");
        ApplicationForm theForm = (ApplicationForm) form;
        ActionForward forward = mapping.findForward("editApplication");
        ApplicationInfo appInfo = ApplicationInfo.getInstance();
        ApplicationModel app = new ApplicationModel();
        try{
            setCaller(theForm, request);
            if(null != request.getParameter("applicationRefId")){
                app = appInfo.getApplication(Long.valueOf(request.getParameter("applicationRefId")));
            }// end if
            theForm.setApplication(app);
            request.getSession().setAttribute("sessionBatchList", theForm.getApplication().getBatchApps());
            getReferenceData(theForm, request);
            setUserRefIds(theForm.getApplication(), request);
            setupTabs(request, "ApplicationInfoLink", true);
        }catch(Exception e){
            log.error("Exception occurred in ApplicationDetailAction.editApplication. Message is: " + e.getMessage(), e);
            forward = mapping.findForward(FAILURE);
        }// end try
        log.debug("Exiting ApplicationDetailAction.editApplication().");
        return forward;
    }// end editApplication

    /**
     * Used to save a new application record or updates to an existing application.
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
    public ActionForward saveApplication(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering ApplicationDetailAction.saveApplication(). Parameters: " + new Object[]{mapping, form, request, response});
        log.debug("Used to save a new application record or updates to an existing application.");
        ApplicationForm theForm = (ApplicationForm) form;
        ActionForward forward = mapping.findForward(REFRESH);
        ActionMessages errors = new ActionMessages();
        ActionMessages errors2 = new ActionMessages();
        ApplicationInfo appInfo = ApplicationInfo.getInstance();
        ApplicationModel app = theForm.getApplication();
        try{
            if(theForm.getCaller() != null){
                String path = mapping.findForward(theForm.getCaller()).getPath();
                if("system".equalsIgnoreCase(theForm.getCaller())){
                    path += "&systemRefId=" + theForm.getApplication().getSystem().getSystemRefId();
                }else if("authorizedUserInfo".equalsIgnoreCase(theForm.getCaller())){
                    path += "&userRefId=" + theForm.getApplication().getPointOfContact().getUserRefId();
                }else if("applicationInfo".equalsIgnoreCase(theForm.getCaller())){
                    path += "&applicationRefId=" + theForm.getApplication().getApplicationRefId();
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
            errors2 = theForm.validateAppData(getResources(request));
            if(!errors2.isEmpty()){
                if(!errors.isEmpty()){
                    errors.add(errors2);
                }else{
                    errors = errors2;
                }// end if
            }// end if
            if(!errors.isEmpty()){
                this.saveErrors(request, errors);
                setupTabs(request, "ApplicationInfoLink", true);
                return mapping.getInputForward();
            }else{
                if(!AppUtil.isNotNullAndZero(app.getApplicationRefId())){
                    appInfo.saveApplication(app);
                }else{
                    appInfo.updateApplication(app);
                }// end if/else
            }// end if/else
            setupTabs(request, null, true);
        }catch(

        Exception e){
            log.error("Exception occurred in ApplicationDetailAction.saveApplication(). Message is: " + e.getMessage(), e);
            forward = mapping.findForward(FAILURE);
        }// end try/catch
        log.debug("Exiting ApplicationDetailAction.saveApplication().");
        return forward;
    }// end saveApplication

    /**
     * Used to add a new application to the DB.
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
    public ActionForward addApplication(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering ApplicationDetailAction.addApplication(). Parameters: " + new Object[]{mapping, form, request, response});
        log.debug("Used to add a new application to the DB.");
        ApplicationForm theForm = (ApplicationForm) form;
        ActionForward forward = mapping.findForward("editApplication");
        try{
            setCaller(theForm, request);
            theForm.setApplication(new ApplicationModel());
            theForm.getApplication().setSystem(new SystemModel());
            theForm.getApplication().setPointOfContact(new AuthorizedUserModel());
            theForm.getApplication().setAppInstances(new ArrayList<ApplicationInstanceModel>());
            if(request.getParameter("systemRefId") != null){
                theForm.getApplication().getSystem().setSystemRefId((Long.valueOf(request.getParameter("systemRefId"))));
            }// end if
            getReferenceData(theForm, request);
            setUserRefIds(theForm.getApplication(), request);
            setupTabs(request, "ApplicationInfoLink", true);
        }catch(Exception e){
            log.error("Exception occurred in ApplicationDetailAction.addApplication(). Message is: " + e.getMessage(), e);
            forward = mapping.findForward(FAILURE);
        }// end try/catch
        log.debug("Exiting ApplicationDetailAction.addApplication().");
        return forward;
    }// end addApplication

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
        CodeInfo codeInfo = CodeInfo.getInstance();
        AuthorizedUserInfo userInfo = AuthorizedUserInfo.getInstance();

        // log.debug("loading app type list. list.size=" + (AppUtil.isEmpty(theForm.getAppTypeList()) ? "null/empty" : theForm.getAppTypeList().size()));
        // theForm.setAppTypeList(codeInfo.getApplicationTypeCodes());
        log.debug("loading confirmed status list. list.size=" + (AppUtil.isEmpty(theForm.getStatusIndList()) ? "null/empty" : theForm.getStatusIndList().size()));
        theForm.setStatusIndList(codeInfo.getApplicationStatusCodes());
        log.debug("loading requested status list. list.size=" + (AppUtil.isEmpty(theForm.getStatusIndList()) ? "null/empty" : theForm.getStatusIndList().size()));
        theForm.setConfirmedStatusIndList(codeInfo.getApplicationStatusCodes());
        theForm.getConfirmedStatusIndList().add(new CodeModel("UNR", "Unresponsive"));
        log.debug("loading system list. list.size=" + (AppUtil.isEmpty(theForm.getSystemList()) ? "null/empty" : theForm.getSystemList().size()));
        theForm.setSystemList(SystemInfo.getInstance().getSystemListAsCodeModel());
        // set the point of contact code list
        theForm.setPocList(userInfo.getUsersListAsCode());

        log.debug("Exiting ApplicationAction - method getReferenceData()");
    }// end getReferenceData

    /**
     * Set the caller parameter into the form
     *
     * @param theForm
     *        action form
     * @param request
     *        HTTP servlet request
     */
    private void setCaller(ApplicationForm theForm, HttpServletRequest request) {
        log.debug("Entering setCaller");
        log.debug("Set the caller parameter into the form");
        if(request.getParameter("caller") != null && !"applicationList".equalsIgnoreCase(request.getParameter("caller"))){
            theForm.setCaller(request.getParameter("caller"));
        }else{
            theForm.setCaller(null);
        }// end if/else
        log.debug("Exiting setCaller");
    }// end setCaller
}// end ApplicationDetailAction
