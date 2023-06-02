package gov.doc.isu.shamen.action;

import static gov.doc.isu.dwarf.resources.Constants.FAILURE;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import gov.doc.isu.shamen.business.SystemInfo;
import gov.doc.isu.shamen.form.OverviewForm;
import gov.doc.isu.shamen.models.SystemModel;

/**
 * Action class that handles all requests for the CoverPage
 *
 * @author <strong>Shane Duncan</strong>
 */
public class OverviewAction extends ShamenDispatchAction {
    private static Logger log = Logger.getLogger("gov.doc.isu.shamen.action.OverviewAction");

    /**
     * This method resets the cover page.
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
    public ActionForward resetCover(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        OverviewForm theForm = (OverviewForm) form;
        theForm.setRadio(null);
        return cover(mapping, form, request, response);
    }

    /**
     * This method gets the stats for display on the cover page
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
    public ActionForward cover(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering OverviewAction.cover");
        ActionForward forward = mapping.findForward("Overview");
        try{
            OverviewForm theForm = (OverviewForm) form;
            Long systemRefId = 0L;
            if(theForm.getRadio() != null){
                systemRefId = Long.valueOf(theForm.getRadio());
            }// end if

            log.debug("loading the System list for display.");
            List<SystemModel> systemList = SystemInfo.getInstance().getSystemListWithStats();
            theForm.setDatalist(systemList);
            if(!systemRefId.equals(0L)){
                for(int i = 0, j = systemList.size();i < j;i++){
                    if(systemRefId.equals(systemList.get(i).getSystemRefId())){
                        theForm.setSystem(systemList.get(i));
                    }// end if
                }// end for
            }else{
                theForm.setSystem(SystemInfo.getInstance().calculateAllSystemsStats(systemList));
            }// end if-else

            setupTabs(request, null, true);
        }catch(Exception e){
            log.error("Exception occurred in OverviewAction.cover. ", e);
            forward = mapping.findForward(FAILURE);
        }// end try
        log.debug("Exiting OverviewAction.cover");
        return forward;
    }// end list

}// end OverviewAction
