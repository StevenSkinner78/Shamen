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
import gov.doc.isu.shamen.business.BatchAppInfo;
import gov.doc.isu.shamen.business.ControllerInfo;
import gov.doc.isu.shamen.form.ControllerForm;
import gov.doc.isu.shamen.models.ControllerModel;

/**
 * Action class that handles all request for the Controller Edit Screen.
 *
 * @author <strong>Steven Skinner</strong>
 */
public class ControllerDetailAction extends ShamenDispatchAction {
    private static Logger log = Logger.getLogger("gov.doc.isu.shamen.action.ControllerDetailAction");

    /**
     * This method adds a new controller.
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
    public ActionForward addController(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering ControllerDetailAction.addController");
        ControllerForm theForm = (ControllerForm) form;
        ActionForward forward = mapping.findForward("editController");
        // This needed for export of PDF view
        Constants.LIST_NAME = "SHAMEN CONTROLLER INFORMATION";
        theForm.semiFlush();
        try{
            theForm.setController(new ControllerModel());
            setUserRefIds(theForm.getController(), request);
            setupTabs(request, "ControllerInfoLink", true);
        }catch(Exception e){
            log.error("Exception occurred in ControllerDetailAction.addController. ", e);
            forward = mapping.findForward(FAILURE);
        }// end try
        log.debug("Exiting ControllerDetailAction.addController");
        return forward;
    }// end addController

    /**
     * This method gets the controller for edit.
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
    public ActionForward editController(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering ControllerDetailAction.editController");
        ControllerForm theForm = (ControllerForm) form;
        ActionForward forward = mapping.findForward("editController");
        ControllerInfo controllerInfo = ControllerInfo.getInstance();
        // This needed for export of PDF view
        Constants.LIST_NAME = "SHAMEN CONTROLLER INFORMATION";
        theForm.semiFlush();
        theForm.setNoRecords(true);
        ControllerModel controller = new ControllerModel();
        setCaller(theForm, request);
        try{
            log.debug("loading the controller list for display.");
            if(request.getParameter("controllerRefId") != null){
                controller = controllerInfo.getController(Long.valueOf(request.getParameter("controllerRefId")));
            }// end if
            theForm.setController(controller);
            setUserRefIds(theForm.getController(), request);
            setupTabs(request, "ControllerInfoLink", true);
        }catch(Exception e){
            log.error("Exception occurred in ControllerDetailAction.editController. ", e);
            forward = mapping.findForward(FAILURE);
        }// end try
        log.debug("Exiting ControllerDetailAction.editController");
        return forward;
    }// end editController

    /**
     * This method saves the controller.
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
    public ActionForward saveController(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering ControllerDetailAction.saveController");
        ControllerForm theForm = (ControllerForm) form;
        ActionForward forward = mapping.findForward(Constants.REFRESH);
        ActionMessages errors = new ActionMessages();
        ControllerInfo controllerInfo = ControllerInfo.getInstance();
        ControllerModel controller = theForm.getController();
        setCaller(theForm, request);
        try{
            if(request.getParameter("cancel") != null){
                return mapping.findForward(CANCEL);
            }// end if
            errors = theForm.validate(mapping, request);
            if(!errors.isEmpty()){
                this.saveErrors(request, errors);
                setupTabs(request, "ControllerInfoLink", true);
                return mapping.getInputForward();
            }else{
                // ActiveDirectoryValidator.verifyComputer(theForm.getController().getAddress());
                if(!AppUtil.isNotNullAndZero(controller.getControllerRefId())){
                    controllerInfo.saveController(controller);
                }else{
                    controllerInfo.updateController(controller);
                }// end if/else
            }// end if/else
            setupTabs(request, null, true);
            // }catch(ActiveDirectoryValidationException e){
            // errors.add("controller.address", new ActionMessage("errors.controller.addressNotValid", theForm.getController().getAddress()));
            // log.error("ActiveDirectoryValidationException occured in ControllerDetailAction.saveController. e=" + e.getMessage());
        }catch(Exception e){
            log.error("Exception occurred in ControllerDetailAction.saveController. ", e);
            forward = mapping.findForward(FAILURE);
        }finally{
            if(!errors.isEmpty()){
                this.saveErrors(request, errors);
                forward = mapping.getInputForward();
            } // end if
        }// end try
        log.debug("Exiting ControllerDetailAction.saveController");
        return forward;
    }// end saveController

    /**
     * This method updates batch app record by setting active indicator to 'Y' or 'N' depending on request parameter.
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
        log.debug("Entering ControllerDetailAction.updateBatchAppActivate");
        ControllerForm theForm = (ControllerForm) form;
        ActionForward forward = mapping.findForward("editController");
        ControllerInfo controllerInfo = ControllerInfo.getInstance();
        BatchAppInfo batchInfo = BatchAppInfo.getInstance();
        // This needed for export of PDF view
        Constants.LIST_NAME = "SHAMEN CONTROLLER INFORMATION";
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
            ControllerModel controller = controllerInfo.getController(theForm.getController().getControllerRefId());
            theForm.setController(controller);
            setUserRefIds(theForm.getController(), request);
            setupTabs(request, "ControllerInfoLink", true);
        }catch(Exception e){
            log.error("Exception occurred in ControllerDetailAction.updateBatchAppActivate. ", e);
            forward = mapping.findForward(FAILURE);
        }// end try
        log.debug("Exiting ControllerDetailAction.updateBatchAppActivate");
        return forward;
    }// end updateBatchAppActivate

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
    public ActionForward updateAllBatch(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering ControllerDetailAction.updateAllBatch");
        ControllerForm theForm = (ControllerForm) form;
        ActionForward forward = mapping.findForward("editController");
        ControllerInfo controllerInfo = ControllerInfo.getInstance();
        BatchAppInfo batchInfo = BatchAppInfo.getInstance();
        // This needed for export of PDF view
        Constants.LIST_NAME = "SHAMEN CONTROLLER INFORMATION";
        theForm.semiFlush();
        theForm.setNoRecords(true);
        String activate = "";
        Long controllerRefId = 0L;
        try{
            if(request.getParameter("activate") != null){
                activate = request.getParameter("activate");
            }// end if
            if(request.getParameter("controllerRefId") != null){
                controllerRefId = Long.valueOf(request.getParameter("controllerRefId"));
            }// end if
            batchInfo.updateControllerBatchApps(activate, controllerRefId, getAppUser(request).getUserRefId());
            if(request.getParameter("list") != null){
                return mapping.findForward(Constants.REFRESH);
            }// end if
            ControllerModel controller = controllerInfo.getController(controllerRefId);
            theForm.setController(controller);
            setUserRefIds(theForm.getController(), request);
            setupTabs(request, "ControllerInfoLink", true);
        }catch(Exception e){
            log.error("Exception occurred in ControllerDetailAction.updateAllBatch. ", e);
            forward = mapping.findForward(FAILURE);
        }// end try
        log.debug("Exiting ControllerDetailAction.updateAllBatch");
        return forward;
    }// end updateAllBatch

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
        setupTabs(request, "ControllerInfoLink", true);
        return new ActionForward(mapping.getInput());
    }// end updateListPageData

    /**
     * Set the caller parameter into the form
     *
     * @param theForm
     *        action form
     * @param request
     *        HTTP servlet request
     */
    private void setCaller(ControllerForm theForm, HttpServletRequest request) {
        log.debug("Entering setCaller");
        log.debug("Set the caller parameter into the form");
        if(request.getParameter("caller") != null){
            theForm.setCaller(request.getParameter("caller"));
        }// end if
        log.debug("Exiting setCaller");
    }// end setCaller

}// end ControllerDetailAction
