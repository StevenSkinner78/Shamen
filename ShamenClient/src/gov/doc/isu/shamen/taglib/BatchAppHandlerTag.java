/**
 * @(#)BatchAppHandlerTag.java 1.0.0 Copyright (c) 2018 Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit All Rights Reserved. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: -Redistribution of source code must retain the above copyright notice, this list of conditions and the following disclaimer. -Redistribution in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. Neither the name of Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit or the names of contributors may be used to endorse or promote products derived from this software without specific prior written permission. This software is provided "AS IS," without a warranty of any kind. ALL EXPRESS OR IMPLIED
 *                             CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. You
 *                             acknowledge that this software is not designed, licensed or intended for use in the design, construction, operation or maintenance of any nuclear facility.
 */
package gov.doc.isu.shamen.taglib;

import static gov.doc.isu.shamen.core.ShamenConstants.PARAMETER_PAGE;
import static gov.doc.isu.shamen.core.ShamenConstants.SVG_END;
import static gov.doc.isu.shamen.core.ShamenConstants.SVG_START;
import static gov.doc.isu.shamen.core.ShamenConstants.TAG_PARENT_URL;
import static gov.doc.isu.shamen.core.ShamenConstants.TAG_RUN_JOB;
import static gov.doc.isu.shamen.core.ShamenConstants.TAG_RUN_JOB_DEFAULT_PARAMETERS;
import static gov.doc.isu.shamen.core.ShamenConstants.TAG_RUN_JOB_PARAMETERS;
import static gov.doc.isu.shamen.core.ShamenConstants.TAG_SHOW_DETAIL;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.doc.isu.shamen.core.ShamenApplicationStatus;
import gov.doc.isu.shamen.core.ShamenConstants;
import gov.doc.isu.shamen.jms.ApplicationJmsManager;
import gov.doc.isu.shamen.jms.models.JmsBatchApp;
import gov.doc.isu.shamen.jms.models.JmsRunStatus;
import gov.doc.isu.shamen.jms.models.JmsSchedule;
import gov.doc.isu.shamen.taglib.models.RunStatusModel;
import gov.doc.isu.shamen.taglib.models.ScheduleModel;
import gov.doc.isu.shamen.taglib.models.TableModel;
import gov.doc.isu.shamen.taglib.models.TableRow;
import gov.doc.isu.shamen.taglib.pagination.PaginatedListSmartListHelper;
import gov.doc.isu.shamen.taglib.pagination.SmartListHelper;
import gov.doc.isu.shamen.taglib.properties.TableProperties;
import gov.doc.isu.shamen.taglib.renderer.TableRenderer;
import gov.doc.isu.shamen.taglib.renderer.TableWriter;
import gov.doc.isu.shamen.taglib.util.Href;
import gov.doc.isu.shamen.util.ShamenClientUtil;

/**
 * This class builds the SHAMEN Batch Application Admin page.
 * 
 * @author <strong>Shane Duncan</strong> JCCC, 08/08/2017
 * @author <strong>Steven Skinner</strong> JCCC, 10/01/2019
 */
public class BatchAppHandlerTag extends BodyTagSupport {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static Log log = LogFactory.getLog("gov.doc.isu.shamen.taglib.BatchAppHandlerTag");
    private String homeURL;
    private String webApp;
    private String environment;
    private String hasRunnableJobs = "Y";
    private String userId;
    private String tableId;
    private int listPageSize;
    private int runStatusListPageSize;
    private int listSize;
    private static List<JmsBatchApp> batchList;
    private List<TableRow> scheduleRowList;
    private boolean listPage;
    private boolean runJobRequest;
    private TableModel tableModel;
    private TableModel scheduleTableModel;
    private TableProperties properties;
    private SmartListHelper listHelper;
    private int pageNumber;
    private String filterByCode;
    private static StringBuffer stringBuffer;
    private static final String SHAMEN_DEFAULT = "<div class=\"shamen-container\"><div class=\"shamen-div\">";
    private static final String SHAMEN_HEADER = "<div class=\"shamen-header\"><h1>SHAMEN Batch Application Admin</h1></div>";
    private static final String NOT_CONNECTED = "<h1>Not connected to Shamen</h1>";
    private static final String DIV_END = "</div>";
    private static final String BUTTON_START_PARTIAL = "<button type=\"button\" class=\"shamen-btn shamen-btn-info\"";
    private static final String BUTTON_END = "</button>";
    private static final String LABEL_START = "<label>";
    private static final String LABEL_END = "</label>";
    private static final String TR_START = "<tr>";
    private static final String TR_END = "</tr>";
    private static final String TD_START = "<td>";
    private static final String TD_END = "</td>";
    private static final String SAFE_PARM = "%26";

    /**
     * This tag creates batch app listing and detail pages
     * 
     * @author Shane Duncan
     */
    public int doStartTag() throws JspException {
        log.trace("Entering doStartTag");
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        setStringBuffer(new StringBuffer());
        if(ShamenConstants.USE_SHAMEN){
            log.debug("Inside if condition with expression: ShamenConstants.USE_SHAMEN");
            if(webApp == null){
                log.debug("Inside if condition with expression: webApp == null");
                webApp = ShamenConstants.CLIENT_SELECTOR_VALUE;
            }// end if
            if(environment == null){
                log.debug("Inside if condition with expression: environment == null");
                environment = ShamenConstants.CLIENT_SELECTOR_VALUE_2;
            }// end if
            if(userId != null){
                log.debug("Inside if condition with expression: userId != null");
                if(userId.startsWith("pageScope.") || userId.startsWith("requestScope.") || userId.startsWith("sessionScope.") || userId.startsWith("applicationScope.")){
                    log.debug("Inside if condition with expression: userId.startsWith(\"pageScope.\") || userId.startsWith(\"requestScope.\") || userId.startsWith(\"sessionScope.\")|| userId.startsWith(\"applicationScope.\")");
                    userId = (String) evaluateExpression(userId);
                }// end if/else
            }else{
                log.debug("Inside else condition.");
                userId = "Unknown";
            }// end if
            properties = TableProperties.getInstance(request);
            // check jms connection status
            try{
                if(ApplicationJmsManager.getInstance().isConnected()){
                    log.debug("Inside if condition with expression: ApplicationJmsManager.getInstance().isConnected()");
                    if("Y".equals((request.getParameter(TAG_SHOW_DETAIL) != null ? request.getParameter(TAG_SHOW_DETAIL) : "N"))){
                        log.debug("Inside if condition with expression: \"Y\".equals((request.getParameter(TAG_SHOW_DETAIL) != null ? request.getParameter(TAG_SHOW_DETAIL) : \"N\"))");
                        log.debug("Method call to setListPage(false)");
                        setListPage(false);
                        log.debug("Method call to setRunJobRequest(false)");
                        setRunJobRequest(false);
                        log.debug("Instantiating variable with identifier: href");
                        Href href = new Href(getFormattedUrlWithParms(TAG_SHOW_DETAIL));
                        log.debug("Instantiating variable with identifier: batchRefId");
                        String batchRefId = request.getParameter(ShamenConstants.TAG_BATCH_APP_REF_ID);
                        filterByCode = ShamenClientUtil.isNullOrEmpty(request.getParameter("filterValue")) ? "" : request.getParameter("filterValue");
                        log.debug("Instantiating variable with identifier: pl");
                        ShamenClientPaginatedList pl = new ShamenClientPaginatedList(request, runStatusListPageSize);
                        log.debug("Instantiating variable with identifier: batchApp");
                        JmsBatchApp batchApp = getBatchAppFromShamen(batchRefId, filterByCode, pl);

                        if(!ShamenClientUtil.isEmpty(batchApp.getSchedule())){
                            scheduleRowList = new ArrayList<TableRow>();
                            for(JmsSchedule sch : batchApp.getSchedule()){
                                ScheduleModel schedule = new ScheduleModel(sch);
                                schedule.setJmsBatchApp(batchApp);
                                scheduleRowList.add(schedule);
                            }// end for
                        }// end if
                        log.debug("Method call to pl.setTotalNumberOfRows(listSize)");
                        pl.setTotalNumberOfRows(listSize);
                        log.debug("Method call to properties.setProperty(TableProperties.PROPERTY_STRING_PAGING_ITEM_NAME,\"Batch Run\")");
                        properties.setProperty(TableProperties.PROPERTY_STRING_PAGING_ITEM_NAME, "Batch Run");
                        log.debug("Method call to properties.setProperty(TableProperties.PROPERTY_STRING_PAGING_ITEMS_NAME,\"Batch Runs\")");
                        properties.setProperty(TableProperties.PROPERTY_STRING_PAGING_ITEMS_NAME, "Batch Runs");
                        tableModel = new TableModel(webApp, environment, "run-status-table", false, properties, pageContext);
                        log.debug("Method call to tableModel.setJmsBatchApp(batchApp)");
                        tableModel.setJmsBatchApp(batchApp);
                        log.debug("Method call to tableModel.setListSize(listSize)");
                        tableModel.setListSize(listSize);
                        log.debug("Method call to tableModel.getAttributes().put(\"class\",\"shamen-table\")");
                        tableModel.getAttributes().put("class", "shamen-table");
                        log.debug("Method call to tableModel.setTableId(\"run-status-table\")");
                        tableModel.setTableId("run-status-table");
                        for(JmsRunStatus runstatus : batchApp.getRunStatusList()){
                            log.debug("Inside for statement with expression: JmsRunStatus runstatus : batchApp.getRunStatusList()");
                            log.debug("Instantiating variable with identifier: model");
                            RunStatusModel model = new RunStatusModel(runstatus);
                            log.debug("Method call to model.setJmsBatchApp(batchApp)");
                            model.setJmsBatchApp(batchApp);
                            log.debug("Method call to tableModel.addRow(model)");
                            tableModel.addRow(model);
                        }// end for
                        log.debug("Method call to pl.setList(tableModel.getRowListFull())");
                        pl.setList(tableModel.getRowListFull());
                        log.debug("Method call to tableModel.setPaginatedList(pl)");
                        tableModel.setPaginatedList(pl);
                        log.debug("Method call to href.addParameter(ShamenConstants.TAG_BATCH_APP_REF_ID,batchRefId)");
                        href.addParameter(ShamenConstants.TAG_BATCH_APP_REF_ID, batchRefId);
                        log.debug("Method call to href.addParameter(\"filterValue\",filterByCode)");
                        href.addParameter("filterValue", filterByCode);
                        if(request.getParameter(PARAMETER_PAGE) != null){
                            log.debug("Inside if condition with expression: request.getParameter(PARAMETER_PAGE) != null");
                            log.debug("Method call to href.addParameter(PARAMETER_PAGE,request.getParameter(PARAMETER_PAGE))");
                            href.addParameter(PARAMETER_PAGE, request.getParameter(PARAMETER_PAGE));
                            pageNumber = Integer.valueOf(request.getParameter(PARAMETER_PAGE));
                        }// end if
                        log.debug("Method call to tableModel.setHref(href)");
                        tableModel.setHref(href);
                    }else if("Y".equals((request.getParameter(TAG_RUN_JOB) != null ? request.getParameter(TAG_RUN_JOB) : "N"))){
                        log.debug("Inside if condition with expression: \"Y\".equals((request.getParameter(TAG_RUN_JOB) != null ? request.getParameter(TAG_RUN_JOB) : \"N\"))");
                        log.debug("Method call to setRunJobRequest(true)");
                        // run the batch job.
                        setRunJobRequest(true);
                        log.debug("Method call to setListPage(false)");
                        setListPage(false);
                        log.debug("Method call to runBatchApp()");
                        runBatchApp();
                        log.debug("Method call to stringBuffer.append(formatRefreshMetaTag(getFormattedUrlWithParms(TAG_SHOW_DETAIL).replace(\"%26\",\"&\") + \"&\" + ShamenConstants.TAG_BATCH_APP_REF_ID+ \"=\"+ request.getParameter(ShamenConstants.TAG_BATCH_APP_REF_ID)))");
                        stringBuffer.append(formatRefreshMetaTag(getFormattedUrlWithParms(TAG_SHOW_DETAIL).replace("%26", "&") + "&" + ShamenConstants.TAG_BATCH_APP_REF_ID + "=" + request.getParameter(ShamenConstants.TAG_BATCH_APP_REF_ID)));
                        log.debug("Method call to stringBuffer.append(\"<html></html>\")");
                        stringBuffer.append("<html></html>");
                    }else{
                        log.debug("Inside else condition.");
                        log.debug("Method call to setListPage(true)");
                        setListPage(true);
                        log.debug("Method call to setRunJobRequest(false)");
                        setRunJobRequest(false);
                        log.debug("Method call to getBatchListFromShamen(webApp,environment)");
                        getBatchListFromShamen(webApp, environment);
                        log.debug("Instantiating variable with identifier: href");
                        Href href = new Href(getFormattedUrlWithParms(null));
                        tableModel = new TableModel(webApp, environment, tableId, true, properties, pageContext);
                        log.debug("Method call to tableModel.setHomeURL(homeURL)");
                        tableModel.setHomeURL(homeURL);
                        log.debug("Method call to tableModel.setHasRunnableJobs(hasRunnableJobs)");
                        tableModel.setHasRunnableJobs(hasRunnableJobs);
                        log.debug("Method call to tableModel.setUserId(userId)");
                        tableModel.setUserId(userId);
                        if(listPageSize > 0){
                            log.debug("Inside if condition with expression: listPageSize > 0");
                            log.debug("Method call to tableModel.setPagesize(listPageSize)");
                            tableModel.setPagesize(listPageSize);
                        }// end if
                        log.debug("Method call to tableModel.setAllBatchList(batchList)");
                        tableModel.setAllBatchList(batchList);
                        log.debug("Method call to tableModel.getAttributes().put(\"class\",\"shamen-table\")");
                        tableModel.getAttributes().put("class", "shamen-table");
                        if(request.getParameter(PARAMETER_PAGE) != null){
                            log.debug("Inside if condition with expression: request.getParameter(PARAMETER_PAGE) != null");
                            log.debug("Method call to href.addParameter(PARAMETER_PAGE,request.getParameter(PARAMETER_PAGE))");
                            href.addParameter(PARAMETER_PAGE, request.getParameter(PARAMETER_PAGE));
                            pageNumber = Integer.valueOf(request.getParameter(PARAMETER_PAGE));
                        }// end if
                        log.debug("Method call to tableModel.setHref(href)");
                        tableModel.setHref(href);
                    }// if-else
                }else{
                    log.debug("Inside else condition.");
                    log.debug("Method call to doNotConnected()");
                    doNotConnected();
                }// end if/else
            }catch(Exception e){
                log.error("Error While constructing the BatchAppList. Exception is: " + e.getMessage(), e);
                throw new JspException(e.getMessage());
            }// end try/catch
        }else{
            log.debug("Inside else condition.");
            try{
                doNotConnected();
            }catch(Exception e){
                log.error("Error While constructing the BatchAppList. Exception is: " + e.getMessage(), e);
                throw new JspException(e.getMessage());
            }// end try/catch
        }// end if/else
        log.trace("Exiting doStartTag");
        return 2;
    }// end doStartTag

    /**
     * Save any body content of this tag, which will generally be the option(s) representing the values displayed to the user.
     * 
     * @exception JspException
     *            if a JSP exception has occurred
     */
    public int doAfterBody() throws JspException {
        log.trace("Entering doAfterBody");
        log.trace("Exiting doAfterBody");
        return SKIP_BODY;
    }// end doAfterBody

    /*
     * (non-Javadoc)
     * @see javax.servlet.jsp.tagext.BodyTagSupport#doEndTag()
     */
    public int doEndTag() throws JspException {
        log.trace("Entering doEndTag");
        try{
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            String contextPath = request.getContextPath();
            if(!ShamenConstants.USE_SHAMEN){
                pageContext.getOut().print(stringBuffer.toString());
            }else{
                if(isListPage()){
                    log.debug("Inside if condition with expression: isListPage()");
                    log.debug("Method call to doStart()");
                    doStart();
                    log.debug("Method call to pageContext.getOut().print(stringBuffer.toString())");
                    pageContext.getOut().print(stringBuffer.toString());
                    log.debug("Method call to setupViewableData()");
                    setupViewableData();
                    log.debug("Method call to writeHTMLData()");
                    writeHTMLData();
                    log.debug("Method call to pageContext.getOut().print(DIV_END + DIV_END)");
                    pageContext.getOut().print(DIV_END + DIV_END);
                }else if(isRunJobRequest()){
                    log.debug("Inside if condition with expression: isRunJobRequest()");
                    log.debug("Method call to pageContext.getOut().print(stringBuffer.toString())");
                    pageContext.getOut().print(stringBuffer.toString());
                }else{
                    log.debug("Inside else condition.");
                    log.debug("Method call to doStart()");
                    doStart();
                    log.debug("Method call to doDetail()");
                    doDetail();
                    log.debug("Method call to pageContext.getOut().print(stringBuffer.toString())");
                    pageContext.getOut().print(stringBuffer.toString());
                    log.debug("Method call to setupDetailViewableData()");
                    setupDetailViewableData();
                    log.debug("Method call to writeHTMLData()");
                    writeHTMLData();
                    log.debug("Instantiating variable with identifier: sb");
                    StringBuffer sb = new StringBuffer();
                    log.debug("Method call to sb.append(\"<div class=\\\"button-div\\\"><a class=\\\"shamen-btn shamen-btn-info\\\" href=\\\"\")");
                    sb.append("<div class=\"button-div\"><a class=\"shamen-btn shamen-btn-info\" href=\"");
                    log.debug("Method call to sb.append(contextPath)");
                    sb.append(contextPath);
                    log.debug("Method call to sb.append(homeURL)");
                    sb.append(homeURL);
                    log.debug("Method call to sb.append(\"\\\">Back</a></div>\")");
                    sb.append("\">Back</a></div>");
                    log.debug("Method call to sb.append(DIV_END).append(DIV_END).append(DIV_END).append(DIV_END)");
                    sb.append(DIV_END).append(DIV_END).append(DIV_END).append(DIV_END);
                    log.debug("Method call to pageContext.getOut().print(sb.toString())");
                    pageContext.getOut().print(sb.toString());
                }// end if/else
            }// end if/else
        }catch(Exception e){
            log.error("doEndTag Error While constructing the end of batch table ", e);
            throw new JspException(e.getMessage());
        }// end try/catch
        release();
        log.trace("Exiting doEndTag");
        return EVAL_PAGE;

    }// end doEndTag

    /**
     * This method is used to set the viewable data for the rendering class.
     */
    @SuppressWarnings("unchecked")
    private void setupViewableData() {
        log.trace("Entering setupViewableData");
        List<TableRow> fullList = (List<TableRow>) tableModel.getRowListFull();
        int pageOffset = 0;
        listHelper = new SmartListHelper(fullList, fullList.size(), tableModel.getPagesize(), properties, false);
        listHelper.setCurrentPage(pageNumber);
        pageOffset = listHelper.getFirstIndexForCurrentPage();
        fullList = (List<TableRow>) listHelper.getListForCurrentPage();
        tableModel.setRowListPage(fullList);
        tableModel.setPageOffset(pageOffset);
        log.trace("Exiting setupViewableData");
    }// end setupViewableData

    /**
     * This method is used to set the viewable data for the rendering class.
     */
    @SuppressWarnings("unchecked")
    private void setupDetailViewableData() {
        log.trace("Entering setupDetailViewableData");
        List<TableRow> fullList = (List<TableRow>) tableModel.getRowListFull();
        int pageOffset = 0;
        listHelper = new PaginatedListSmartListHelper(tableModel.getPaginatedList(), tableModel.getProperties());
        listHelper.setCurrentPage(tableModel.getPaginatedList().getPageNumber());
        pageOffset = listHelper.getFirstIndexForCurrentPage();
        fullList = (List<TableRow>) listHelper.getListForCurrentPage();
        tableModel.setRowListPage(fullList);
        tableModel.setPageOffset(pageOffset);
        log.trace("Exiting setupDetailViewableData");
    }// end setupViewableData

    /**
     * This method is used to write the data in the HTML format.
     *
     * @throws JspException
     *         An exception if the method logic fails.
     */
    private void writeHTMLData() throws JspException {
        log.trace("Entering writeHTMLData");
        JspWriter writer = pageContext.getOut();

        (new TableWriter(tableModel, pageContext, writer, homeURL, listHelper)).writeTable();
        log.trace("Exiting writeHTMLData");
    }// end writeHTMLData

    /**
     * This method sets the head tag with a refresh url. This is used so that F5 does not cause things like running a batch job to be run again when they are only trying to refresh the page.
     * 
     * @param url
     *        thr current url
     * @return String
     */
    public String formatRefreshMetaTag(String url) {
        log.trace("Entering formatRefreshMetaTag");
        stringBuffer.append("<head>");
        stringBuffer.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
        stringBuffer.append("<meta http-equiv=\"refresh\" content=\"0;url=");
        stringBuffer.append(url);
        stringBuffer.append("\">");
        stringBuffer.append("</head>");
        log.trace("Exiting formatRefreshMetaTag");
        return stringBuffer.toString();
    }// end formatRefreshMetaTag

    /**
     * This constructed if there is no connection to Shamen Web and messages cannot be sent or recieved. Returns a formatted html string.
     * 
     * @return StringBuffer
     */
    public void doNotConnected() {
        log.trace("Entering doNotConnected");
        // stringBuffer.append(loadStyleSheet());
        stringBuffer.append(SHAMEN_DEFAULT);
        stringBuffer.append(SHAMEN_HEADER);
        stringBuffer.append(NOT_CONNECTED);
        stringBuffer.append(DIV_END);
        stringBuffer.append(DIV_END);
        log.trace("Exiting doNotConnected");
    }// end doNotConnected

    /**
     * This method gets a list of batch applications from ShamenWeb, formats them into html, and returns the string.
     * 
     * @return StringBuffer
     * @throws JspException
     */
    public void doStart() throws JspException {
        log.trace("Entering doStart");
        loadScript();
        if(isListPage()){
            log.debug("Inside if condition with expression: isListPage()");
            log.debug("Method call to stringBuffer.append(SHAMEN_DEFAULT)");
            stringBuffer.append(SHAMEN_DEFAULT);
            log.debug("Method call to stringBuffer.append(SHAMEN_HEADER)");
            stringBuffer.append(SHAMEN_HEADER);
        }else{
            log.debug("Inside else condition.");
            log.debug("Method call to stringBuffer.append(SHAMEN_DEFAULT)");
            stringBuffer.append(SHAMEN_DEFAULT);
            log.debug("Method call to stringBuffer.append(SHAMEN_HEADER)");
            stringBuffer.append(SHAMEN_HEADER);
        }// end if/else
        log.debug("Method call to loadSVGIcons()");
        loadSVGIcons();
        log.trace("Exiting doStart");
    }// end doList

    /**
     * This method loads all the SVG icons that will be used for the page.
     * 
     * @param sb
     * @return StringBuffer with icons
     */
    private void loadSVGIcons() {
        log.trace("Entering loadSVGIcons");
        stringBuffer.append(SVG_START);
        stringBuffer.append(BatchAppHandlerTagSvgIcons.CALENDAR_CHECK_ICON_DEF);
        stringBuffer.append(BatchAppHandlerTagSvgIcons.CALENDAR_TIMES_ICON_DEF);
        stringBuffer.append(BatchAppHandlerTagSvgIcons.CALENDAR_MINUS_ICON_DEF);
        stringBuffer.append(BatchAppHandlerTagSvgIcons.HOURGLASS_ICON_DEF);
        stringBuffer.append(BatchAppHandlerTagSvgIcons.OFFSCHEDULE_ICON_DEF);
        stringBuffer.append(BatchAppHandlerTagSvgIcons.UNKNOWN_ICON_DEF);
        stringBuffer.append(BatchAppHandlerTagSvgIcons.PENDING_ICON_DEF);
        stringBuffer.append(BatchAppHandlerTagSvgIcons.SERVER_GREEN_ICON_DEF);
        stringBuffer.append(BatchAppHandlerTagSvgIcons.SERVER_RED_ICON_DEF);
        stringBuffer.append(BatchAppHandlerTagSvgIcons.SUCCESSFUL_ICON_DEF);
        stringBuffer.append(BatchAppHandlerTagSvgIcons.UNSUCCESSFUL_ICON_DEF);
        stringBuffer.append(BatchAppHandlerTagSvgIcons.INACTIVE_ICON_DEF);
        stringBuffer.append(BatchAppHandlerTagSvgIcons.COLAPSE_ICON_DEF);
        stringBuffer.append(BatchAppHandlerTagSvgIcons.EXPAND_ICON_DEF);
        stringBuffer.append(SVG_END);
        log.trace("Exiting loadSVGIcons");
    }// end loadSVGIcons

    /**
     * This method gets the batch application from Shamen, renders it to html and returns a string buffer containing it.
     * 
     * @return
     * @throws JspException
     */
    public void doDetail() throws Exception {
        log.trace("Entering doDetail");
        JmsBatchApp batchApp = tableModel.getJmsBatchApp();
        stringBuffer.append("<div class=\"batch-table\">");
        if(batchApp == null){
            log.debug("Inside if condition with expression: batchApp == null");
            log.debug("Method call to stringBuffer.append(NOT_CONNECTED)");
            stringBuffer.append(NOT_CONNECTED);
        }else{
            log.debug("Inside else condition.");
            if(batchApp != null){
                log.debug("Inside if condition with expression: batchApp != null");
                log.debug("Method call to stringBuffer.append(\"<div class=\\\"batch-detail-info\\\">\")");
                stringBuffer.append("<div class=\"batch-detail-info\">");
                log.debug("Method call to stringBuffer.append(putInDiv(LABEL_START,\"Batch Name: \",LABEL_END,\"<span>\",batchApp.getName(),\"</span>\"))");
                stringBuffer.append(putInDiv(LABEL_START, "Batch Name: ", LABEL_END, "<span>", batchApp.getName(), "</span>"));
                log.debug("Method call to stringBuffer.append(putInDiv(LABEL_START,\"Batch Description: \",LABEL_END,\"<span>\",batchApp.getDescription(),\"</span>\"))");
                stringBuffer.append(putInDiv(LABEL_START, "Batch Description: ", LABEL_END, "<span>", batchApp.getDescription(), "</span>"));
                log.debug("Method call to stringBuffer.append(putInDiv(LABEL_START,\"Batch Type: \",LABEL_END,\"<span>\",batchApp.getTypeDescription(),\"</span>\"))");
                stringBuffer.append(putInDiv(LABEL_START, "Batch Type: ", LABEL_END, "<span>", batchApp.getTypeDescription(), "</span>"));
                log.debug("Method call to doScheduleDetail(batchApp.getSchedule())");
                // Load the schedule info
                doScheduleDetail();
                log.debug("Method call to stringBuffer.append(DIV_END)");
                stringBuffer.append(DIV_END);
                log.debug("Method call to stringBuffer.append(\"<div class=\\\"batch-detail-info\\\"> <div><h2>Run Status Information</h2></div>\")");
                stringBuffer.append("<div class=\"batch-detail-info\"> <div><h2>Run Status Information</h2></div>");
                log.debug("Method call to doRunStatusFilterOption()");
                doRunStatusFilterOption();
            }// end if
        }// end if/else
        log.trace("Exiting doDetail");
    }// end doDetail

    /**
     * This method loads a default style sheet or an import statement for a custom one sent in by the tag parameters.
     *
     * @return styleSheet html
     */
    private void loadScript() {
        log.trace("Entering loadScript");
        stringBuffer.append("<script>");
        stringBuffer.append("function launchJob(batchRefId,useParam,obj) {\r\n");
        stringBuffer.append(" if(useParam == 'Y'){\r\n");
        stringBuffer.append(" var jobParams = document.getElementById(\"job-parameters\" + batchRefId).value;\r\n");
        stringBuffer.append(" obj += \"%26jobParameters=\" + jobParams ;\r\n");
        stringBuffer.append(" }\r\n");
        stringBuffer.append(" window.location.assign(obj);\r\n");
        stringBuffer.append("}\r\n");
        stringBuffer.append("\r\n");
        stringBuffer.append("function openLightBox(batchRefId) {\r\n");
        stringBuffer.append("var lightbox = document.getElementById(\"lightbox-\" + batchRefId),\r\n");
        stringBuffer.append("dimmer = document.createElement(\"div\");\r\n");
        stringBuffer.append("dimmer.style.width = window.innerWidth + 'px';\r\n");
        stringBuffer.append("dimmer.style.height = window.innerHeight + 'px';\r\n");
        stringBuffer.append("dimmer.className = 'shamen-modal-backdrop fade show';\r\n");
        stringBuffer.append("dimmer.id = \"dimmer\";\r\n");
        stringBuffer.append("dimmer.onclick = function() {\r\n");
        stringBuffer.append("document.body.removeChild(this);\r\n");
        stringBuffer.append("lightbox.style.display = 'none';\r\n");
        stringBuffer.append("document.body.className = '';\r\n");
        stringBuffer.append("}\r\n");
        stringBuffer.append("document.body.appendChild(dimmer);\r\n");
        stringBuffer.append("document.body.className = 'shamen-modal-open';\r\n");
        stringBuffer.append("lightbox.style.display = 'block';\r\n");
        stringBuffer.append("lightbox.style.top = window.innerHeight / 3 + 'px';\r\n");
        stringBuffer.append("return false;\r\n");
        stringBuffer.append("}\r\n");
        stringBuffer.append("\r\n");
        stringBuffer.append("function dismissLightBox(batchRefId) {\r\n");
        stringBuffer.append("var lightbox = document.getElementById(\"lightbox-\" + batchRefId), dimmer = document.getElementById(\"dimmer\");\r\n");
        stringBuffer.append("document.body.removeChild(dimmer);\r\n");
        stringBuffer.append("lightbox.style.display = 'none';\r\n");
        stringBuffer.append("document.body.className = '';\r\n");
        stringBuffer.append("return false;\r\n");
        stringBuffer.append("}\r\n");
        stringBuffer.append("\r\n");
        stringBuffer.append("function toggleSubrow(obj) {\r\n");
        stringBuffer.append("var id = obj.id;\r\n");
        stringBuffer.append("var pos = id.indexOf(\"-\");\r\n");
        stringBuffer.append("var name = id.substring(0, pos);\r\n");
        stringBuffer.append("var num = id.substring(pos +1);\r\n");
        stringBuffer.append("if (name == 'colapseIcon') {\r\n");
        stringBuffer.append("document.getElementById(\"colapseIcon-\" + num).className = 'hidden';\r\n");
        stringBuffer.append("document.getElementById(\"expandIcon-\" + num).className = '';\r\n");
        stringBuffer.append("document.getElementById(\"subrow\" + num).className = '';\r\n");
        stringBuffer.append("} else {\r\n");
        stringBuffer.append("document.getElementById(\"colapseIcon-\" + num).className = '';\r\n");
        stringBuffer.append("document.getElementById(\"expandIcon-\" + num).className = 'hidden';\r\n");
        stringBuffer.append("document.getElementById(\"subrow\" + num).className = 'hidden';\r\n");
        stringBuffer.append("}\r\n");
        stringBuffer.append("}\r\n");
        stringBuffer.append("\r\n");
        stringBuffer.append("function filterTable(obj) {\r\n");
        stringBuffer.append("var detailFilter = document.getElementById(\"detailFilter\").value;\r\n");
        stringBuffer.append("obj += \"%26filterValue=\" + detailFilter;\r\n");
        stringBuffer.append("window.location.assign(obj);\r\n");
        stringBuffer.append("}\r\n");
        stringBuffer.append("</script>");
        log.trace("Exiting loadScript");
    }// end loadScript

    /**
     * This method formats the schedule detail record into html and returns it as a string.
     * 
     * @param schedule
     * @return String
     */
    private void doScheduleDetail() throws Exception {
        log.trace("Entering doScheduleDetail");
        stringBuffer.append("<h2>Schedule Information</h2>");
        if(ShamenClientUtil.isEmpty(scheduleRowList)){
            scheduleRowList = new ArrayList<TableRow>();
        }// end if
        TableRenderer scheduleTable = new TableRenderer(scheduleRowList, "schedule-table");
        stringBuffer.append(scheduleTable.writeTable().toString());
        log.trace("Exiting doScheduleDetail");
    }// end formatScheduleDetail

    /**
     * This method builds the filter option for the run status table.
     */
    private void doRunStatusFilterOption() {
        log.trace("Entering doRunStatusFilterOption");
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        String contextPath = request.getContextPath();
        stringBuffer.append("<div class=\"shamen-right\"><label>Filter By Result:</label>");
        stringBuffer.append("<select class=\"shamen-select\" id=\"detailFilter\">");
        stringBuffer.append("<option value=\"\">All</option>");
        stringBuffer.append("<option value=\"SUC\"");
        if(!ShamenClientUtil.isNullOrEmpty(filterByCode) && "SUC".equalsIgnoreCase(filterByCode)){
            log.debug("Inside if condition with expression: !ShamenClientUtil.isNullOrEmpty(filterByCode) && \"SUC\".equalsIgnoreCase(filterByCode)");
            log.debug("Method call to stringBuffer.append(\" selected=\\\"selected\\\"\")");
            stringBuffer.append(" selected=\"selected\"");
        }// end if
        stringBuffer.append(">Completed Successfully</option>");
        stringBuffer.append("<option value=\"UNS\"");
        if(!ShamenClientUtil.isNullOrEmpty(filterByCode) && "UNS".equalsIgnoreCase(filterByCode)){
            log.debug("Inside if condition with expression: !ShamenClientUtil.isNullOrEmpty(filterByCode) && \"UNS\".equalsIgnoreCase(filterByCode)");
            log.debug("Method call to stringBuffer.append(\" selected=\\\"selected\\\"\")");
            stringBuffer.append(" selected=\"selected\"");
        }// end if
        stringBuffer.append(">Unsuccessful</option>");
        stringBuffer.append("</select>");
        stringBuffer.append("&nbsp;&nbsp;");
        stringBuffer.append(BUTTON_START_PARTIAL);
        stringBuffer.append(" onclick=\"filterTable('");
        stringBuffer.append(contextPath);
        stringBuffer.append("/BatchDetail.shamen?batchAppRefId=");
        stringBuffer.append(tableModel.getJmsBatchApp().getBatchAppRefId());
        stringBuffer.append("&");
        stringBuffer.append(TAG_PARENT_URL);
        stringBuffer.append("=");
        stringBuffer.append(getFormattedUrlWithParms(TAG_SHOW_DETAIL));
        stringBuffer.append("')\">Filter").append(BUTTON_END);
        stringBuffer.append(DIV_END);
        log.trace("Exiting doRunStatusFilterOption");
    }// end doRunStatusFilterOption

    /**
     * This method takes any number of data variables and put them into one column within a table row.
     * 
     * @param data
     * @return table row with columns
     */
    public String putInDiv(String... data) {
        log.trace("Entering putInDiv");
        StringBuffer sb = new StringBuffer();
        for(int i = 0, j = data.length;i < j;i++){
            log.debug("Inside for statement with expression: i < j");
            log.debug("Method call to sb.append(data[i])");
            sb.append(data[i]);
        }// end for
        log.trace("Exiting putInDiv");
        return sb.toString();
    }// end putInRowAndCol

    /**
     * This method takes any number of data variables and put them into one column within a table row.
     * 
     * @param data
     * @return table row with columns
     */
    public String putInRowAndCol(String... data) {
        log.trace("Entering putInRowAndCol");
        StringBuffer sb = new StringBuffer();
        sb.append(TR_START);
        sb.append(TD_START);
        for(int i = 0, j = data.length;i < j;i++){
            log.debug("Inside for statement with expression: i < j");
            log.debug("Method call to sb.append(data[i])");
            sb.append(data[i]);
        }// end for
        sb.append(TD_END);
        sb.append(TR_END);
        log.trace("Exiting putInRowAndCol");
        return sb.toString();
    }// end putInRowAndCol

    /**
     * This method takes any number of data variables and put them into separate columns within a table row.
     * 
     * @param data
     * @return table row with columns
     */
    public String putInRowAndCols(String... data) {
        log.trace("Entering putInRowAndCols");
        StringBuffer sb = new StringBuffer();
        sb.append(TR_START);
        for(int i = 0, j = data.length;i < j;i++){
            log.debug("Inside for statement with expression: i < j");
            log.debug("Method call to sb.append(TD_START)");
            sb.append(TD_START);
            log.debug("Method call to sb.append(data[i])");
            sb.append(data[i]);
            log.debug("Method call to sb.append(TD_END)");
            sb.append(TD_END);
        }// end for
        sb.append(TR_END);
        log.trace("Exiting putInRowAndCols");
        return sb.toString();
    }// end putInRowAndCols

    /**
     * This method will put all the junk from the data array into table columns. It does not have a "tr" definition
     * 
     * @param data
     * @return formatted data
     */
    public String putInCols(String... data) {
        log.trace("Entering putInCols");
        StringBuffer sb = new StringBuffer();
        for(int i = 0, j = data.length;i < j;i++){
            log.debug("Inside for statement with expression: i < j");
            log.debug("Method call to sb.append(TD_START)");
            sb.append(TD_START);
            log.debug("Method call to sb.append(data[i])");
            sb.append(data[i]);
            log.debug("Method call to sb.append(TD_END)");
            sb.append(TD_END);
        }// end for
        log.trace("Exiting putInCols");
        return sb.toString();
    }// end putInCols

    /**
     * This method gets the URL with all the necessary parameters for what is required to forward to the detail page.
     * 
     * @param function
     *        function to redirect to.
     * @return url
     */
    public String getFormattedUrlWithParms(String function) {
        log.trace("Entering getFormattedUrlWithParms");
        HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
        StringBuffer url = new StringBuffer();
        String contextPath = ((HttpServletRequest) this.pageContext.getRequest()).getContextPath();

        // prepend the context path if any.
        // actually checks if context path is already there for people which manually add it
        if(contextPath != null && homeURL != null && homeURL.startsWith("/") && !homeURL.startsWith(contextPath)){
            log.debug("Inside if condition with expression: contextPath != null && homeURL != null && homeURL.startsWith(\"/\") && !homeURL.startsWith(contextPath)");
            log.debug("Method call to url.append(contextPath)");
            url.append(contextPath);
        }// end if
        url.append(homeURL);
        // replace all the &'s to parameter safe hex value.
        url = new StringBuffer(url.toString().replace("&", "%26"));

        // Add request variable that indicates the page should only show the detail portion.
        if(!ShamenClientUtil.isNullOrEmpty(function)){
            log.debug("Inside if condition with expression: !ShamenClientUtil.isNullOrEmpty(function)");
            log.debug("Method call to url.append(SAFE_PARM).append(function).append(\"=\").append(\"Y\")");
            url.append(SAFE_PARM).append(function).append("=").append("Y");
        }// end if

        log.trace("Exiting getFormattedUrlWithParms");
        return response.encodeURL(url.toString());

    }// end getFormattedUrlWithParms

    /**
     * This method gets the list of batch applications assigned to the web application.
     * 
     * @param webApp
     * @param environment
     * @return
     */
    @SuppressWarnings("unchecked")
    public void getBatchListFromShamen(String webApp, String environment) {
        log.trace("Entering getBatchListFromShamen");
        HashMap<String, String> messageMap = new HashMap<String, String>();
        HashMap<String, Object> receivedMessageMap = new HashMap<String, Object>();
        batchList = new ArrayList<JmsBatchApp>();
        messageMap.put(ShamenApplicationStatus.STATUS, ShamenApplicationStatus.getInstance().getStatus());
        messageMap.put(ShamenApplicationStatus.REPLY_REQUIRED, "true");
        messageMap = ShamenApplicationStatus.getInstance().getSc().encryptApplicationMap(messageMap);
        messageMap.put(ShamenApplicationStatus.ACTION, ShamenApplicationStatus.ACTION_GET_APP_BATCH_JOBS);
        messageMap.put(ShamenApplicationStatus.APPLICATION_NAME, ShamenConstants.CLIENT_SELECTOR_VALUE);
        messageMap.put(ShamenApplicationStatus.APPLICATION_ENVIRONMENT, ShamenConstants.CLIENT_SELECTOR_VALUE_2);
        messageMap.put(ShamenApplicationStatus.APPLICATION_INSTANCE, ShamenConstants.CLIENT_SELECTOR_VALUE_3);
        try{
            log.info("Attempt to get Batch App List from ShamenWeb.");
            receivedMessageMap = ApplicationJmsManager.getInstance().sendPTPWithAcknowledge(ShamenConstants.SHAMEN_REQUEST_Q, messageMap, 30000);
            if(receivedMessageMap != null){
                log.debug("Inside if condition with expression: receivedMessageMap != null");
                log.info("Received a response from ShamenWeb.");
                receivedMessageMap = ShamenApplicationStatus.getInstance().getSc().decryptApplicationObjectMap(receivedMessageMap);
                batchList = (ArrayList<JmsBatchApp>) receivedMessageMap.get(ShamenApplicationStatus.BATCH_APPS);
            }// end-if
        }catch(Exception e){
            log.error("Exception occurred confirming status update to Shamen.  Exception is: " + e.getMessage(), e);
        }// end try-catch
        log.trace("Exiting getBatchListFromShamen");
    }// end getBatchListFromShamen

    /**
     * This method builds the key for this application to be used in checking values from the distributed map.
     * 
     * @return
     */
    @SuppressWarnings("unused")
    private String getKey() {
        return ShamenConstants.CLIENT_SELECTOR_VALUE + ShamenConstants.CLIENT_SELECTOR_VALUE_2;
    }// end getKey

    /**
     * This method gets a single batch application from ShamenWeb.
     * 
     * @param batchRefId
     * @return
     */
    @SuppressWarnings("unchecked")
    public JmsBatchApp getBatchAppFromShamen(String batchRefId, String filterCode, ShamenClientPaginatedList pl) {
        log.trace("Entering getBatchAppFromShamen");
        HashMap<String, String> messageMap = new HashMap<String, String>();
        HashMap<String, Object> receivedMessageMap = new HashMap<String, Object>();
        JmsBatchApp batchApp = null;
        messageMap.put(ShamenApplicationStatus.STATUS, ShamenApplicationStatus.getInstance().getStatus());
        messageMap.put(ShamenApplicationStatus.REPLY_REQUIRED, "true");
        messageMap.put(ShamenApplicationStatus.REF_ID, batchRefId);
        messageMap.put(ShamenApplicationStatus.FILTER_RUN_STATUS, filterCode);
        messageMap.put(ShamenApplicationStatus.PAGE_SIZE, String.valueOf(pl.getPageSize()));
        messageMap.put(ShamenApplicationStatus.PAGING_START_ROW, String.valueOf(pl.getFirstRecordIndex()));
        messageMap.put(ShamenApplicationStatus.PAGING_END_ROW, String.valueOf(pl.getLastRecordIndex()));
        messageMap = ShamenApplicationStatus.getInstance().getSc().encryptApplicationMap(messageMap);
        messageMap.put(ShamenApplicationStatus.ACTION, ShamenApplicationStatus.ACTION_GIVE_ME_BATCH_JOB);
        messageMap.put(ShamenApplicationStatus.APPLICATION_NAME, ShamenConstants.CLIENT_SELECTOR_VALUE);
        messageMap.put(ShamenApplicationStatus.APPLICATION_ENVIRONMENT, ShamenConstants.CLIENT_SELECTOR_VALUE_2);
        messageMap.put(ShamenApplicationStatus.APPLICATION_INSTANCE, ShamenConstants.CLIENT_SELECTOR_VALUE_3);
        try{
            log.info("Attempt to get Batch App from ShamenWeb.");
            receivedMessageMap = ApplicationJmsManager.getInstance().sendPTPWithAcknowledge(ShamenConstants.SHAMEN_REQUEST_Q, messageMap, 30000);
            if(receivedMessageMap != null){
                log.debug("Inside if condition with expression: receivedMessageMap != null");
                log.info("Received a response from ShamenWeb.");
                receivedMessageMap = ShamenApplicationStatus.getInstance().getSc().decryptApplicationObjectMap(receivedMessageMap);
                batchApp = (JmsBatchApp) receivedMessageMap.get(ShamenApplicationStatus.BATCH_APPS);
                listSize = (Integer) receivedMessageMap.get(ShamenApplicationStatus.RUN_STATUS_COUNT);
                // listSize = batchApp.getRunStatusList().size();
            }// end-if
        }catch(Exception e){
            log.error("Exception occurred confirming status update to Shamen.  Exception is: " + e.getMessage(), e);
        }// end try-catch
        log.trace("Exiting getBatchAppFromShamen");
        return batchApp;
    }// end getBatchAppFromShamen

    /**
     * This method sends a message to ShamenWeb to run a batch job.
     * 
     * @param batchRefId
     * @param userRefId
     * @return
     */
    @SuppressWarnings("unchecked")
    public Boolean runBatchApp() {
        log.trace("Entering runBatchApp");
        HashMap<String, String> messageMap = new HashMap<String, String>();
        HashMap<String, Object> receivedMessageMap = new HashMap<String, Object>();
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        String batchRefId = request.getParameter(ShamenConstants.TAG_BATCH_APP_REF_ID);
        String jobParameters = request.getParameter(TAG_RUN_JOB_PARAMETERS);
        if(!ShamenClientUtil.isNullOrEmpty(request.getParameter(TAG_RUN_JOB_DEFAULT_PARAMETERS))){
            log.debug("Inside if condition with expression: !ShamenClientUtil.isNullOrEmpty(request.getParameter(TAG_RUN_JOB_DEFAULT_PARAMETERS))");
            log.debug("Instantiating variable with identifier: defaultParam");
            String defaultParam = request.getParameter(TAG_RUN_JOB_DEFAULT_PARAMETERS);
            jobParameters = ShamenClientUtil.isNullOrEmpty(jobParameters) ? defaultParam : jobParameters + "," + defaultParam;
        }// end if
        Boolean confirmed = false;
        messageMap.put(ShamenApplicationStatus.STATUS, ShamenApplicationStatus.getInstance().getStatus());
        messageMap.put(ShamenApplicationStatus.REPLY_REQUIRED, "true");
        messageMap.put(ShamenApplicationStatus.REF_ID, batchRefId);
        messageMap.put(ShamenApplicationStatus.USER_REF_ID, userId);
        messageMap.put(ShamenApplicationStatus.JOB_PARAMETERS, jobParameters);
        messageMap = ShamenApplicationStatus.getInstance().getSc().encryptApplicationMap(messageMap);
        messageMap.put(ShamenApplicationStatus.ACTION, ShamenApplicationStatus.ACTION_RUN_BATCH);
        messageMap.put(ShamenApplicationStatus.APPLICATION_NAME, ShamenConstants.CLIENT_SELECTOR_VALUE);
        messageMap.put(ShamenApplicationStatus.APPLICATION_ENVIRONMENT, ShamenConstants.CLIENT_SELECTOR_VALUE_2);
        messageMap.put(ShamenApplicationStatus.APPLICATION_INSTANCE, ShamenConstants.CLIENT_SELECTOR_VALUE_3);
        try{
            log.info("Attempt to run Batch App from ShamenWeb.");
            receivedMessageMap = ApplicationJmsManager.getInstance().sendPTPWithAcknowledge(ShamenConstants.SHAMEN_REQUEST_Q, messageMap, 30000);
            if(receivedMessageMap != null){
                log.debug("Inside if condition with expression: receivedMessageMap != null");
                log.info("Received a response from ShamenWeb.");
                receivedMessageMap = ShamenApplicationStatus.getInstance().getSc().decryptApplicationObjectMap(receivedMessageMap);
                confirmed = true;
            }// end-if
        }catch(Exception e){
            log.error("Exception occurred confirming status update to Shamen.  Exception is: " + e.getMessage(), e);
        }// end try-catch
        log.trace("Exiting runBatchApp");
        return confirmed;
    }// end runBatchApp

    /**
     * @return the webApp
     */
    public String getWebApp() {
        return webApp;
    }// end getWebApp

    /**
     * @param webApp
     *        the webApp to set
     */
    public void setWebApp(String webApp) {
        this.webApp = webApp;
    }// end setWebApp

    /**
     * @return the environment
     */
    public String getEnvironment() {
        return environment;
    }// end getEnvironment

    /**
     * @param environment
     *        the environment to set
     */
    public void setEnvironment(String environment) {
        this.environment = environment;
    }// end setEnvironment

    /**
     * @return the homeURL
     */
    public String getHomeURL() {
        return homeURL;
    }// end getHomeURL

    /**
     * @param homeURL
     *        the homeURL to set
     */
    public void setHomeURL(String homeURL) {
        this.homeURL = homeURL;
    }// end setHomeURL

    /**
     * @return the hasRunnableJobs
     */
    public String getHasRunnableJobs() {
        return hasRunnableJobs;
    }// end getHasRunnableJobs

    /**
     * @param hasRunnableJobs
     *        the hasRunnableJobs to set
     */
    public void setHasRunnableJobs(String hasRunnableJobs) {
        this.hasRunnableJobs = hasRunnableJobs;
    }// end setHasRunnableJobs

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }// end getUserId

    /**
     * @param userId
     *        the userId to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }// end setUserId

    /**
     * @return the listSize
     */
    public int getListSize() {
        return listSize;
    }// end getListSize

    /**
     * @param listSize
     *        the listSize to set
     */
    public void setListSize(int listSize) {
        this.listSize = listSize;
    }// end setListSize

    /**
     * @return the listPage
     */
    public boolean isListPage() {
        return listPage;
    }// end isListPage

    /**
     * @param listPage
     *        the listPage to set
     */
    public void setListPage(boolean listPage) {
        this.listPage = listPage;
    }// end setListPage

    /**
     * @return the tableId
     */
    public String getTableId() {
        return tableId;
    }// end getTableId

    /**
     * @param tableId
     *        the tableId to set
     */
    public void setTableId(String tableId) {
        this.tableId = tableId;
    }// end setTableId

    /**
     * @return the stringBuffer
     */
    public StringBuffer getStringBuffer() {
        return stringBuffer;
    }// end getStringBuffer

    /**
     * @param stringBuffer
     *        the stringBuffer to set
     */
    public void setStringBuffer(StringBuffer stringBuffer) {
        BatchAppHandlerTag.stringBuffer = stringBuffer;
    }// end setStringBuffer

    /**
     * @return the tableModel
     */
    public TableModel getTableModel() {
        return tableModel;
    }// end getTableModel

    /**
     * @param tableModel
     *        the tableModel to set
     */
    public void setTableModel(TableModel tableModel) {
        this.tableModel = tableModel;
    }// end setTableModel

    /**
     * @return the properties
     */
    public TableProperties getProperties() {
        return properties;
    }// end getProperties

    /**
     * @param properties
     *        the properties to set
     */
    public void setProperties(TableProperties properties) {
        this.properties = properties;
    }// end setProperties

    /**
     * @return the listHelper
     */
    public SmartListHelper getListHelper() {
        return listHelper;
    }// end getListHelper

    /**
     * @param listHelper
     *        the listHelper to set
     */
    public void setListHelper(SmartListHelper listHelper) {
        this.listHelper = listHelper;
    }// end setListHelper

    /**
     * @return the pageNumber
     */
    public int getPageNumber() {
        return pageNumber;
    }// end getPageNumber

    /**
     * @param pageNumber
     *        the pageNumber to set
     */
    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }// end setPageNumber

    /**
     * @return the runJobRequest
     */
    public boolean isRunJobRequest() {
        return runJobRequest;
    }// end isRunJobRequest

    /**
     * @param runJobRequest
     *        the runJobRequest to set
     */
    public void setRunJobRequest(boolean runJobRequest) {
        this.runJobRequest = runJobRequest;
    }// end setRunJobRequest

    /**
     * @return the filterByCode
     */
    public String getFilterByCode() {
        return filterByCode;
    }// end getFilterByCode

    /**
     * @param filterByCode
     *        the filterByCode to set
     */
    public void setFilterByCode(String filterByCode) {
        this.filterByCode = filterByCode;
    }// end setFilterByCode

    /**
     * @return the listPageSize
     */
    public int getListPageSize() {
        return listPageSize;
    }// end getListPageSize

    /**
     * @param listPageSize
     *        the listPageSize to set
     */
    public void setListPageSize(int listPageSize) {
        this.listPageSize = listPageSize;
    }// end setListPageSize

    /**
     * @return the runStatusListPageSize
     */
    public int getRunStatusListPageSize() {
        return runStatusListPageSize;
    }// end getRunStatusListPageSize

    /**
     * @param runStatusListPageSize
     *        the runStatusListPageSize to set
     */
    public void setRunStatusListPageSize(int runStatusListPageSize) {
        this.runStatusListPageSize = runStatusListPageSize;
    }// end setRunStatusListPageSize

    /**
     * @return the scheduleTableModel
     */
    public TableModel getScheduleTableModel() {
        return scheduleTableModel;
    }// end getScheduleTableModel

    /**
     * @param scheduleTableModel
     *        the scheduleTableModel to set
     */
    public void setScheduleTableModel(TableModel scheduleTableModel) {
        this.scheduleTableModel = scheduleTableModel;
    }// end setScheduleTableModel

    /**
     * <p>
     * evaluate an expression in a way similar to LE in jstl.
     * </p>
     * <p>
     * the first token is supposed to be an object in the page scope (default scope) or one of the following:
     * </p>
     * <ul>
     * <li>pageScope</li>
     * <li>requestScope</li>
     * <li>sessionScope</li>
     * <li>applicationScope</li>
     * </ul>
     * <p>
     * Tokens after the object name are interpreted as javabean properties (accessed through getters), mapped or indexed properties, using the jakarta common-beans library
     * </p>
     * 
     * @param expression
     *        expression to evaluate
     * @return Object result
     * @throws ObjectLookupException
     *         if unable to get a bean using the given expression
     */
    protected Object evaluateExpression(String expression) {
        String expressionWithoutScope = expression;

        // default scope = request
        // this is for compatibility with the previous version, probably default should be PAGE
        int scope = PageContext.REQUEST_SCOPE;

        if(expression.startsWith("pageScope.")){
            scope = PageContext.PAGE_SCOPE;
            expressionWithoutScope = expressionWithoutScope.substring(expressionWithoutScope.indexOf('.') + 1);
        }else if(expression.startsWith("requestScope.")){
            scope = PageContext.REQUEST_SCOPE;
            expressionWithoutScope = expressionWithoutScope.substring(expressionWithoutScope.indexOf('.') + 1);

        }else if(expression.startsWith("sessionScope.")){
            scope = PageContext.SESSION_SCOPE;
            expressionWithoutScope = expressionWithoutScope.substring(expressionWithoutScope.indexOf('.') + 1);

        }else if(expression.startsWith("applicationScope.")){
            scope = PageContext.APPLICATION_SCOPE;
            expressionWithoutScope = expressionWithoutScope.substring(expressionWithoutScope.indexOf('.') + 1);

        }// end if/else
        if(expressionWithoutScope.indexOf('.') != -1){
            try{
                // complex: property from a bean
                String objectName = StringUtils.substringBefore(expressionWithoutScope, ".");
                String beanProperty = StringUtils.substringAfter(expressionWithoutScope, ".");
                Object beanObject;

                // get the bean
                beanObject = pageContext.getAttribute(objectName, scope);

                // if null return
                if(beanObject == null){
                    return "Unknown";
                }// end if

                return PropertyUtils.getSimpleProperty(beanObject, beanProperty);

            }catch(IllegalAccessException e){
                return "Unknown";
            }catch(InvocationTargetException e){
                return "Unknown";
            }catch(NoSuchMethodException e){
                return "Unknown";
            }// end try/catch
        }// end if

        return pageContext.getAttribute(expressionWithoutScope, scope);

    }// end evaluateExpression

}// end class
