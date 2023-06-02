package gov.doc.isu.shamen.action;

import static gov.doc.isu.dwarf.resources.Constants.FAILURE;
import static gov.doc.isu.dwarf.resources.Constants.KEY;
import static gov.doc.isu.dwarf.resources.Constants.LIST;
import static gov.doc.isu.dwarf.resources.Constants.VAL;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import gov.doc.isu.dwarf.resources.Constants;
import gov.doc.isu.dwarf.util.AppUtil;
import gov.doc.isu.shamen.business.ControllerInfo;
import gov.doc.isu.shamen.form.ControllerForm;
import gov.doc.isu.shamen.models.ControllerModel;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Action class that handles all request for the Controller List Screen.
 * 
 * @author <strong>Steven Skinner</strong>
 */
public class ControllerAction extends ShamenDispatchAction {
    private static Logger log = Logger.getLogger("gov.doc.isu.shamen.action.ControllerAction");

    /**
     * This method gets the list of Controllers.
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
        log.debug("Entering ControllerAction.list");
        ActionForward forward = mapping.findForward(LIST);
        try{
            ControllerForm theForm = (ControllerForm) form;
            ControllerInfo controllerInfo = ControllerInfo.getInstance();
            // This needed for export of PDF view
            Constants.LIST_NAME = "SHAMEN CONTROLLER LIST";
            theForm.semiFlush();
            List<ControllerModel> controllerList = new ArrayList<ControllerModel>();
            log.debug("loading the controller list for display.");
            controllerList = controllerInfo.getControllerList();
            theForm.setDatalist(controllerList);
            setupTabs(request, null, true);
        }catch(Exception e){
            log.error("Exception occurred in ControllerAction.list. ", e);
            forward = mapping.findForward(FAILURE);
        }// end try
        log.debug("Exiting ControllerAction.list");
        return forward;
    }// end list

    /**
     * This method deletes a controller based on controller ref id.
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
    public ActionForward deleteController(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering ControllerAction.deleteController");
        ControllerForm theForm = (ControllerForm) form;
        ControllerInfo controllerInfo = ControllerInfo.getInstance();
        // This needed for export of PDF view
        Constants.LIST_NAME = "SHAMEN CONTROLLER LIST";
        ActionMessages errors = new ActionMessages();
        Long controllerRefId = 0L;
        try{
            if(request.getParameter("controllerRefId") != null){
                controllerRefId = Long.valueOf(request.getParameter("controllerRefId"));
            }// end if
            ControllerModel checkController = controllerInfo.getController(controllerRefId);
            if(!AppUtil.isEmpty(checkController.getBatchApps())){
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.delete.controller", checkController.getName(), checkController.getAddress(), checkController.getBatchApps().size()));
                this.saveErrors(request, errors);
                setupTabs(request, null, true);
                return mapping.getInputForward();
            }// end if
            controllerInfo.deleteController(controllerRefId, getAppUser(request).getUserRefId());
            theForm.setControllerList(controllerInfo.getControllerList());
            setupTabs(request, null, true);
        }catch(Exception e){
            log.error("Exception occurred in ControllerAction.deleteController. ", e);
            return mapping.findForward(FAILURE);
        }// end try
        log.debug("Exiting ControllerAction.deleteController");
        return list(mapping, theForm, request, response);
    }// end deleteController

    /**
     * Used to start the asynchronous controller status update.
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
    public ActionForward updateControllerStatuses(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering ControllerAction.updateControllerStatuses(). Parameters: " + new Object[]{mapping, form, request, response});
        log.debug("This method is Used to start the asynchronous controller status update.");
        ControllerInfo controllerInfo = ControllerInfo.getInstance();
        try{
            controllerInfo.updateControllerStatuses();
            setupTabs(request, null, true);
        }catch(Exception e){
            log.error("Exception occurred in ControllerAction.updateControllerStatuses(). Message is: " + e.getMessage(), e);
            return mapping.findForward(FAILURE);
        }// end try/catch
        log.debug("Exiting ControllerAction.updateControllerStatuses(). Return: forward= ControllerAction.list()");
        return list(mapping, form, request, response);
    }// end updateApplicationStatuses

    /**
     * Used to start the asynchronous controller status update.
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
    public ActionForward refreshSingleControllerStatus(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering ControllerAction.refreshSingleControllerStatus(). Parameters: " + new Object[]{mapping, form, request, response});
        log.debug("This method is Used to start the asynchronous controller status update.");
        ControllerInfo controllerInfo = ControllerInfo.getInstance();
        Long controllerRefId = 0L;
        try{
            if(request.getParameter("controllerRefId") != null){
                controllerRefId = Long.valueOf(request.getParameter("controllerRefId"));
            }// end if
            controllerInfo.updateSingleControllerStatus(controllerRefId);
            setupTabs(request, null, true);
        }catch(Exception e){
            log.error("Exception occurred in ControllerAction.updateControllerStatuses(). Message is: " + e.getMessage(), e);
            return mapping.findForward(FAILURE);
        }// end try/catch
        log.debug("Exiting ControllerAction.refreshSingleControllerStatus(). Return: forward= ControllerAction.list()");
        return list(mapping, form, request, response);
    }// end refreshSingleControllerStatus

    /**
     * Ajax call to load the statuses of controllers.
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
        log.debug("Entering ControllerAction.loadStatuses");
        ControllerInfo controllerInfo = ControllerInfo.getInstance();
        List<ControllerModel> controllerList = controllerInfo.getControllerList();
        JSONArray jsnArray = new JSONArray();
        if(AppUtil.isEmpty(controllerList)){
            controllerList = new ArrayList<ControllerModel>();
        }// end if
        for(int i = 0, j = controllerList.size();i < j;i++){
            JSONObject jo = new JSONObject();
            jo.put(KEY, controllerList.get(i).getControllerRefId());
            jo.put(VAL, controllerList.get(i).getStatusCd());
            jsnArray.add(jo);
        }// end for
        response.getWriter().println(jsnArray);
        response.flushBuffer();
        log.debug("Exiting ControllerAction.loadStatuses");
        return null;
    }// end loadStatuses
}// end ControllerAction
