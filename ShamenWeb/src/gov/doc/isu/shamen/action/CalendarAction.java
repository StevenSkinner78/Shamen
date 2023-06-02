package gov.doc.isu.shamen.action;

import static gov.doc.isu.dwarf.resources.Constants.FAILURE;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import gov.doc.isu.shamen.business.BatchAppInfo;
import gov.doc.isu.shamen.interfaces.Scheduleable;
import gov.doc.isu.shamen.util.ShamenUtil;

/**
 * Action class that handles all requests for the Schedule Page
 * 
 * @author <strong>Steven Skinner</strong>
 */
public class CalendarAction extends ShamenDispatchAction {
    private static Logger log = Logger.getLogger("gov.doc.isu.shamen.action.CalendarAction");

    /**
     * This method loads the Batch App data to fill the schedule calendar.
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
    public ActionForward showCalendar(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering CalendarAction.showCalendar");
        ActionForward forward = mapping.findForward("showCalendar");

        try{
            setupTabs(request, null, true);
        }catch(Exception e){
            log.error("Exception occurred in CalendarAction.showCalendar. ", e);
            forward = mapping.findForward(FAILURE);
        }// end try
        log.debug("Exiting CalendarAction.showCalendar");
        return forward;
    }

    /**
     * Load the data to be displayed in Calendar on screen.
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
     *         Exception
     */
    public ActionForward loadData(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        log.debug("Entering CalendarAction.loadData");
        String start = "";
        String end = "";
        if(request.getParameter("start") != null){
            start = request.getParameter("start");
            if(start != null && start.length() < 11){
                start += "T00:00:00";
            }
        }// end if
        if(request.getParameter("end") != null){
            end = request.getParameter("end");
            if(end != null && end.length() < 11){
                end += "T00:00:00";
            }
        }// end if
         // Timestamp st = ShamenUtil.getTimeStamp(start);
         // Timestamp ed = ShamenUtil.getTimeStamp(end);
        BatchAppInfo batchInfo = BatchAppInfo.getInstance();
        Calendar fromCal = Calendar.getInstance();
        fromCal.setTime(ShamenUtil.getTimeStamp(start));

        Calendar toCal = Calendar.getInstance();
        toCal.setTime(ShamenUtil.getTimeStamp(end));
        toCal.set(Calendar.HOUR_OF_DAY, 23);
        toCal.set(Calendar.MINUTE, 59);
        Map<Scheduleable, List<Timestamp>> data = batchInfo.getAllBatchRunTimes(new Timestamp(fromCal.getTimeInMillis()), new Timestamp(toCal.getTimeInMillis()));
        response.getWriter().println(batchInfo.getCalendarData(data));
        response.flushBuffer();

        log.debug("Exiting CalendarAction.loadData");
        return null;
    }// end loadWebApps
}
