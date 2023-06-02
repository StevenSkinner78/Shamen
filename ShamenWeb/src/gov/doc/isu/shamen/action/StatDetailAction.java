package gov.doc.isu.shamen.action;

import static gov.doc.isu.dwarf.resources.Constants.FAILURE;
import static gov.doc.isu.shamen.resources.AppConstants.HEADER;
import static gov.doc.isu.shamen.resources.AppConstants.USER_STATS_LABELS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import gov.doc.isu.dwarf.util.StringUtil;
import gov.doc.isu.shamen.business.AuthorizedUserInfo;
import gov.doc.isu.shamen.business.BatchAppInfo;
import gov.doc.isu.shamen.business.SystemInfo;
import gov.doc.isu.shamen.form.StatForm;
import gov.doc.isu.shamen.models.BatchAppModel;
import gov.doc.isu.shamen.models.RunStatusModel;
import gov.doc.isu.shamen.util.ShamenUtil;

/**
 * Action class that handles all request for the Run Status List Screen
 *
 * @author <strong>Shane Duncan</strong>
 */
public class StatDetailAction extends ShamenDispatchAction {
    private static Logger log = Logger.getLogger("gov.doc.isu.shamen.action.StatDetailAction");

    /**
     * This method loads the initial Info graphics page.
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
    public ActionForward loadPage(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering StatDetailAction.loadPage");
        StatForm theForm = (StatForm) form;
        theForm.setListType("start");
        setupTabs(request, null, true);
        log.debug("Exiting StatDetailAction.loadPage");
        return mapping.findForward("Detail");
    }// end loadPage

    /**
     * This method resetStats the miscellaneous stats.
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
    public ActionForward resetStats(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering StatDetailAction.resetStats");
        StatForm theForm = (StatForm) form;
        theForm.setChartCsv(null);
        theForm.setChartMap(null);
        theForm.setChartTitle(null);
        log.debug("Exiting StatDetailAction.resetStats");
        return showStats(mapping, theForm, request, response);
    }// end resetStats

    /**
     * This method gets the miscellaneous stats.
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
    public ActionForward showStats(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering StatDetailAction.showStats");
        ActionForward forward = mapping.findForward("Detail");
        StatForm theForm = (StatForm) form;
        try{
            BatchAppInfo batchAppInfo = BatchAppInfo.getInstance();
            List<BatchAppModel> batchList = batchAppInfo.getAllScheduledBatchAppsAndCollections();
            request.getSession().setAttribute("sessionBatchList", batchList);
            // Get the current batch app

            if(theForm.getShowChart() != null && theForm.getShowChart().length > 0){
                theForm.setChartCsv(null);
                theForm.setChartMap(null);
                theForm.setChartTitle(null);
                theForm.setShowChartString(null);
                for(int i = 0, j = theForm.getShowChart().length;i < j;i++){
                    loadStats(form, Long.valueOf(theForm.getShowChart()[i]));
                    theForm.setShowChartString(StringUtil.collapseArray(theForm.getShowChart()));
                }// end for
            }else{
                theForm.setChartCsv(null);
                theForm.setChartMap(null);
                theForm.setChartTitle(null);
                theForm.setShowChartString(null);
                theForm.setShowChart(null);
            }// end if

            theForm.setCaller("statsInfo");
            theForm.setListType("batch");
            setupTabs(request, null, true);
        }catch(Exception e){
            log.error("Exception occurred in StatDetailAction.showStats. ", e);
            forward = mapping.findForward(FAILURE);
        }// end try
        log.debug("Exiting StatDetailAction.showStats");
        return forward;
    }// end showStats

    /**
     * This method gets the stat information for a batch app, formats it, and loads it to the form.
     *
     * @param form
     *        (REQUIRED) - A JavaBean associated with mapping. It will have its properties initialized from the request parameters. Subsequently, properties of this bean have been populated
     * @param batchAppRefId
     *        (REQUIRED) - The ref id of the batch app whose stats are to be loaded.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void loadStats(ActionForm form, Long batchAppRefId) {
        log.debug("Entering StatDetailAction.loadStats");
        try{
            StatForm theForm = (StatForm) form;

            // Get the batch app processor
            BatchAppInfo batchAppInfo = BatchAppInfo.getInstance();

            BatchAppModel batchApp = batchAppInfo.getBatchForGraph(batchAppRefId);
            // Pull out the run statuses
            List<RunStatusModel> runStatusList = batchApp.getRunStatuses();
            List<String> details = new ArrayList<String>();
            String title = "";
            Map runMap;
            if(theForm.getChartMap() == null){
                runMap = new HashMap<String, List<String>>();
            }else{
                runMap = theForm.getChartMap();
                title = theForm.getChartTitle();
            }// end if-else
            int numberOfApps = 0;
            // Build the statistic CSV
            StringBuffer csv = new StringBuffer();
            // if fresh, then set up the header
            if(runMap.isEmpty()){
                details.add("Date");
            }else{
                details = (List<String>) runMap.get(HEADER);
                numberOfApps = details.size() - 1;
                title = title.trim() + ", ";
            }// end if
            details.add(batchApp.getName());
            runMap.put(HEADER, details);
            title = title + batchApp.getName();

            // Loop through and load to map taking into account already loaded runs
            for(int i = 0, j = runStatusList.size();i < j;i++){
                if("DON".equals(runStatusList.get(i).getStatusCd()) && "SUC".equals(runStatusList.get(i).getResultCd())){
                    String runTime = ShamenUtil.getTimeStampForChart(runStatusList.get(i).getStartTs());
                    // Check to see if run time is already in the map.
                    List<String> mapList = (List<String>) runMap.get(runTime);
                    // run time in map already, add second run time to it.
                    if(mapList != null){
                        mapList.add(runStatusList.get(i).getDurationInMinutes());
                    }else{
                        // run time not in map, create new list and add run time to the correct element. Default others to blank
                        mapList = new ArrayList<String>();
                        for(int k = 0;k < numberOfApps;k++){
                            mapList.add("");
                        }// end for
                        mapList.add(runStatusList.get(i).getDurationInMinutes());
                    }// end if-else
                    runMap.put(runTime, mapList);
                }// end if
            }// end for
             // set up the header first.
            details = (List<String>) runMap.get(HEADER);
            csv.append(ShamenUtil.buildCSVRow(details, ","));
            // Loop through the map and load it into a CSV
            List keySet = new ArrayList(runMap.keySet());
            Collections.sort(keySet);
            for(Iterator iterator = keySet.iterator();iterator.hasNext();){
                String key = (String) iterator.next();
                if(!key.equals(HEADER)){
                    List durationList = (List) runMap.get(key);
                    details = new ArrayList<String>();
                    details.add(key);
                    // You must insure that each duration list is the same size. this catches all the runs that had a time for previous app, but not this one.
                    if(durationList.size() < (numberOfApps + 1)){
                        durationList.add("");
                    }// end if
                    for(int i = 0, j = durationList.size();i < j;i++){
                        details.add((String) durationList.get(i));
                    }// end for
                    csv.append(ShamenUtil.buildCSVRow(details, ","));
                }// end if
            }// end for
            theForm.setChartCsv(csv.toString());
            theForm.setBatchApp(batchApp);
            theForm.setChartMap(runMap);
            theForm.setChartTitle(title);
        }catch(Exception e){
            log.error("Exception occurred in StatDetailAction.loadStats. ", e);
        }// end try
        log.debug("Exiting StatDetailAction.loadStats");
    }// end loadStats

    /**
     * This method gets the system data stats.
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
    public ActionForward loadSystemData(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        log.debug("Entering StatDetailAction.loadSystemData");
        ActionForward forward = mapping.findForward("Detail");
        StatForm theForm = (StatForm) form;
        try{
            theForm.setSystemData(SystemInfo.getInstance().getSystemsListForChart());
            theForm.setListType("system");
            setupTabs(request, null, true);
        }catch(Exception e){
            log.error("Exception occurred in StatDetailAction.loadSystemData. ", e);
            forward = mapping.findForward(FAILURE);
        }// end try
        log.debug("Exiting StatDetailAction.loadSystemData");
        return forward;
    }// end loadSystemData

    /**
     * This method gets the system tree data stats.
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
    public ActionForward loadSystemStatTree(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        log.debug("Entering StatDetailAction.loadSystemStatTree");
        ActionForward forward = mapping.findForward("Detail");
        StatForm theForm = (StatForm) form;
        try{
            theForm.setSystemData(SystemInfo.getInstance().getSystemsListForChart());
            theForm.setListType("systemTree");
            setupTabs(request, null, true);
        }catch(Exception e){
            log.error("Exception occurred in StatDetailAction.loadSystemStatTree. ", e);
            forward = mapping.findForward(FAILURE);
        }// end try
        log.debug("Exiting StatDetailAction.loadSystemStatTree");
        return forward;
    }// end loadSystemStatTree

    /**
     * This method gets the batch stats for full timeline averages.
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
    public ActionForward batchRunAverages(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        log.debug("Entering StatDetailAction.batchRunAverages");
        ActionForward forward = mapping.findForward("Detail");
        StatForm theForm = (StatForm) form;
        try{
            theForm.setListType("batchRunAvg");
            setupTabs(request, null, true);
        }catch(Exception e){
            log.error("Exception occurred in StatDetailAction.batchRunAverages. ", e);
            forward = mapping.findForward(FAILURE);
        }// end try
        log.debug("Exiting StatDetailAction.batchRunAverages");
        return forward;
    }// end batchRunAverages

    /**
     * This method gets the user data stats.
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
    public ActionForward loadUserData(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        log.debug("Entering StatDetailAction.loadUserData");
        ActionForward forward = mapping.findForward("Detail");
        StatForm theForm = (StatForm) form;
        try{
            theForm.setUserLabels(USER_STATS_LABELS);
            String change = request.getParameter("listChange");
            if(change != null){
                if(null != theForm.getSelectedLabels()){
                    theForm.setUserData(AuthorizedUserInfo.getInstance().getUsersListForChart(theForm.getSelectedLabels()));
                    theForm.setSelectedLabelsString(StringUtil.collapseArray(theForm.getSelectedLabels()));
                }else{
                    theForm.setUserData(null);
                    theForm.setSelectedLabelsString(null);
                }
            }else{
                theForm.setUserData(AuthorizedUserInfo.getInstance().getUsersListForChart(USER_STATS_LABELS));
                theForm.setSelectedLabels(USER_STATS_LABELS);
                theForm.setSelectedLabelsString(StringUtil.collapseArray(USER_STATS_LABELS));
            }// end if
            if(null != theForm.getSelectedLabels()){
                setColorString(theForm);
            }
            theForm.setListType("user");
            setupTabs(request, null, true);
        }catch(Exception e){
            log.error("Exception occurred in StatDetailAction.loadUserData. ", e);
            forward = mapping.findForward(FAILURE);
        }// end try
        log.debug("Exiting StatDetailAction.loadUserData");
        return forward;
    }// end loadUserData

    /**
     * This method sets the colors for the various displays.
     *
     * @param theForm
     *        A JavaBean associated with mapping. It will have its properties initialized from the request parameters. Subsequently, properties of this bean have been populated
     */
    public void setColorString(StatForm theForm) {
        log.debug("Entering setColorString");
        log.debug("This method sets the colors for the various displays.");
        log.debug("Entry parameters are: theForm=" + String.valueOf(theForm));
        String[] labels = theForm.getSelectedLabels();
        String bb = "";
        if(StringUtil.arrayContains("System", labels)){
            bb += "#EBB056";
        }
        if(StringUtil.arrayContains("BatchApps", labels)){
            if(!StringUtil.isNullOrEmpty(bb)){
                bb += ",";
            }
            bb += "#557EAA";
        }
        if(StringUtil.arrayContains("WebApps", labels)){
            if(!StringUtil.isNullOrEmpty(bb)){
                bb += ",";
            }
            bb += "#83548B";
        }
        theForm.setColors(bb);
        log.debug("Exiting setColorString");
    }
}// end StatDetailAction
