package gov.doc.isu.shamen.action;

import static gov.doc.isu.dwarf.resources.Constants.FAILURE;
import static gov.doc.isu.dwarf.resources.Constants.LIST;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import gov.doc.isu.shamen.business.SystemInfo;
import gov.doc.isu.shamen.form.SystemForm;

/**
 * Action class that handles all request for the System List Screen.
 * 
 * @author <strong>Steven Skinner</strong>
 */
public class SystemAction extends ShamenDispatchAction {
    private static Logger log = Logger.getLogger("gov.doc.isu.shamen.action.SystemAction");

    /**
     * This method gets the list of Systems.
     * 
     * @param mapping
     *        (REQUIRED)- Information that the System, RequestProcessor, knows about mapping the request to the instance of this Action class.
     * @param form
     *        (REQUIRED) - A JavaBean associated with mapping. It will have its properties initialized from the request parameters. Subsequently, properties of this bean have been populated
     * @param request
     *        (REQUIRED) - Provides data including parameter name and values, attributes, and an input stream
     * @param response
     *        (OPTIONAL) - Assist in sending a response to the client
     * @return ActionForward - Describes where and how control should be forwarded
     */
    public ActionForward list(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering SystemAction.list");
        ActionForward forward = mapping.findForward(LIST);
        try{
            SystemForm theForm = (SystemForm) form;
            log.debug("loading the System list for display.");
            theForm.setDatalist(SystemInfo.getInstance().getSystemList());
            setupTabs(request, null, true);
        }catch(Exception e){
            log.error("Exception occurred in SystemAction.list. ", e);
            forward = mapping.findForward(FAILURE);
        }// end try
        log.debug("Exiting SystemAction.list");
        return forward;
    }// end list

    /**
     * This method deletes the system by ref id.
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
    public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering SystemAction.delete");
        SystemForm theForm = (SystemForm) form;

        theForm.semiFlush();
        theForm.setNoRecords(false);
        Long systemRefId = 0L;
        try{
            if(request.getParameter("systemRefId") != null){
                systemRefId = Long.valueOf(request.getParameter("systemRefId"));
            }// end if
            SystemInfo.getInstance().deleteSystem(systemRefId, getAppUser(request).getUserRefId());
            theForm.setDatalist(SystemInfo.getInstance().getSystemList());
            setupTabs(request, null, true);
        }catch(Exception e){
            log.error("Exception occurred in SystemAction.delete. ", e);
            return mapping.findForward(FAILURE);
        }// end try
        log.debug("Exiting SystemAction.delete");
        return list(mapping, theForm, request, response);
    }// end deleteBatch
}// end SystemAction
