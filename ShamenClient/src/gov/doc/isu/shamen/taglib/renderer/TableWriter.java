/**
 * @(#)TableWriter.java 1.0.0 Copyright (c) 2018 Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit All Rights Reserved. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: -Redistribution of source code must retain the above copyright notice, this list of conditions and the following disclaimer. -Redistribution in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. Neither the name of Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit or the names of contributors may be used to endorse or promote products derived from this software without specific prior written permission. This software is provided "AS IS," without a warranty of any kind. ALL EXPRESS OR IMPLIED CONDITIONS,
 *                      REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. You acknowledge that this
 *                      software is not designed, licensed or intended for use in the design, construction, operation or maintenance of any nuclear facility.
 */
package gov.doc.isu.shamen.taglib.renderer;

import static gov.doc.isu.shamen.core.ShamenConstants.CONTROLLER_STATUS_CD_CONNECTED;
import static gov.doc.isu.shamen.core.ShamenConstants.CONTROLLER_STATUS_CD_WAITING;
import static gov.doc.isu.shamen.core.ShamenConstants.ICON_CONTROLLER_CONNECTED_TITLE;
import static gov.doc.isu.shamen.core.ShamenConstants.ICON_CONTROLLER_DISCONNECTED_TITLE;
import static gov.doc.isu.shamen.core.ShamenConstants.ICON_CONTROLLER_WAITING_TITLE;
import static gov.doc.isu.shamen.core.ShamenConstants.ICON_LASTRUN_BAD_TITLE;
import static gov.doc.isu.shamen.core.ShamenConstants.ICON_LASTRUN_GOOD_TITLE;
import static gov.doc.isu.shamen.core.ShamenConstants.ICON_LASTRUN_INACTIVE_TITLE;
import static gov.doc.isu.shamen.core.ShamenConstants.ICON_LASTRUN_INPROGRESS_TITLE;
import static gov.doc.isu.shamen.core.ShamenConstants.ICON_LASTRUN_MULTIPLE_TITLE;
import static gov.doc.isu.shamen.core.ShamenConstants.ICON_LASTRUN_OFFSCHEDULE_TITLE;
import static gov.doc.isu.shamen.core.ShamenConstants.ICON_LASTRUN_PENDING_TITLE;
import static gov.doc.isu.shamen.core.ShamenConstants.ICON_LASTRUN_UNKNOWN_TITLE;
import static gov.doc.isu.shamen.core.ShamenConstants.ICON_SCHEDULE_ACTIVE_TITLE;
import static gov.doc.isu.shamen.core.ShamenConstants.ICON_SCHEDULE_INACTIVE_TITLE;
import static gov.doc.isu.shamen.core.ShamenConstants.ICON_SCHEDULE_NO_SCHEDULE_TITLE;
import static gov.doc.isu.shamen.core.ShamenConstants.LASTRUN_STATUS_CD_BAD;
import static gov.doc.isu.shamen.core.ShamenConstants.LASTRUN_STATUS_CD_GOOD;
import static gov.doc.isu.shamen.core.ShamenConstants.LASTRUN_STATUS_CD_INACTIVE;
import static gov.doc.isu.shamen.core.ShamenConstants.LASTRUN_STATUS_CD_INPROGRESS;
import static gov.doc.isu.shamen.core.ShamenConstants.LASTRUN_STATUS_CD_MULTIPLE;
import static gov.doc.isu.shamen.core.ShamenConstants.LASTRUN_STATUS_CD_OFFSCHEDULE;
import static gov.doc.isu.shamen.core.ShamenConstants.LASTRUN_STATUS_CD_PENDING;
import static gov.doc.isu.shamen.core.ShamenConstants.PARAMETER_PAGE;
import static gov.doc.isu.shamen.core.ShamenConstants.TAG_PARENT_URL;
import static gov.doc.isu.shamen.core.ShamenConstants.TAG_RUN_JOB;
import static gov.doc.isu.shamen.core.ShamenConstants.TAG_SHOW_DETAIL;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.doc.isu.shamen.jms.models.JmsBatchApp;
import gov.doc.isu.shamen.jms.models.JmsRunStatus;
import gov.doc.isu.shamen.taglib.BatchAppHandlerTagSvgIcons;
import gov.doc.isu.shamen.taglib.models.BatchAppModel;
import gov.doc.isu.shamen.taglib.models.RunStatusModel;
import gov.doc.isu.shamen.taglib.models.TableModel;
import gov.doc.isu.shamen.taglib.models.TableRow;
import gov.doc.isu.shamen.taglib.pagination.SmartListHelper;
import gov.doc.isu.shamen.taglib.properties.TableProperties;
import gov.doc.isu.shamen.taglib.util.Href;
import gov.doc.isu.shamen.util.ShamenClientUtil;

/**
 * This class is used to allow exporting table data in HTML format.
 * <p>
 * Modified: Added logic to match filter value w/ 'equals' rather that w/ 'contains' when useEquals flag is provided. Added legend property check and rendering of legend to the IMP table display.
 * </p>
 * 
 * @author Joseph Burris JCCC, jsb000is
 * @author <strong>Brian Hicks</strong>, JCCC - Apr 03, 2019 - modification author
 * @author <strong>Steven Skinner</strong> JCCC, Oct 01, 2019
 */
public class TableWriter {
    private static Log log = LogFactory.getLog("gov.doc.isu.shamen.taglib.renderer.TableWriter");
    private TableModel tableModel;
    private JspWriter writer;
    private TableProperties properties;
    private PageContext pagecontext;
    private SmartListHelper listHelper;
    private String homeURL;
    private static final String SAFE_PARM = "%26";
    private static final String TABLE_START = "<table";
    private static final String TABLE_END = "</table>";
    private static final String TH_START = "<th>";
    private static final String TH_END = "</th>";
    private static final String TR_START = "<tr>";
    private static final String TR_END = "</tr>";
    private static final String TD_START = "<td>";
    private static final String TD_END = "</td>";
    private static final String IMG_SVG_START = "<acronym title=\"";
    private static final String IMG_SVG_1_END = "\">";
    private static final String IMG_SVG_2_END = "</acronym>";
    private static final String DIV_END = "</div>";
    private static final String BUTTON_START = "<button type=\"button\"";
    private static final String BUTTON_END = "</button>";
    private static final String NO_MATCHING_BATCH_PROVIDED = "<tr><td colspan=\"8\">No Matching Batch Jobs</td></tr>";
    private static final String NO_RUN_STATUS_HISTORY = "<tr><td colspan=\"6\">No Run Status History</td></tr>";
    private static final String[] LIST_TABLE_HEADERS = {"", "", "", "BATCH APP NAME", "TYPE", "EXECUTION COUNT", ""};
    private static final String[] RUN_STATUS_TABLE_HEADERS = {"STATUS", "START TIME", "STOP TIME", "DURATION", "RESULT", "FROM", ""};

    /**
     * This overloaded constructor is used to instantiate this class.
     *
     * @param tableModel
     *        The backing model for the table of data.
     * @param pagecontext
     *        A {@link PageContext} to use in building table.
     * @param writer
     *        The {@link JspWriter} to use for exporting data.
     * @param homeURL
     *        The homeURL to use for directing page.
     */
    public TableWriter(TableModel tableModel, PageContext pagecontext, JspWriter writer, String homeURL, SmartListHelper listHelper) {
        super();
        this.tableModel = tableModel;
        this.pagecontext = pagecontext;
        this.writer = writer;
        this.homeURL = homeURL;
        this.listHelper = listHelper;
        this.properties = tableModel.getProperties();
    }// end constructor

    /**
     * This method is used to write the HTML table.
     *
     * @throws JspException
     *         An exception if the method logic fails.
     */
    public void writeTable() throws JspException {
        try{
            writeTableOpener();
        }catch(Exception e){
            log.error("An exception occurred while writing the table opener. Message is: " + e.getMessage(), e);
        }// end try/catch
        try{
            writeTableHeader();
        }catch(Exception e){
            log.error("An exception occurred while writing the table header. Message is: " + e.getMessage(), e);
        }// end try/catch
        try{
            writeTableBodyOpener();
        }catch(Exception e){
            log.error("An exception occurred while writing the table body opener. Message is: " + e.getMessage(), e);
        }// end try/catch
        try{
            if(tableModel.isListPage()){
                writeTableBody();
            }else{
                writeTableDetailBody();

            }// end if/else
        }catch(Exception e){
            log.error("An exception occurred while writing the table body. Message is: " + e.getMessage(), e);
        }// end try/catch
        try{
            writeTableBodyCloser();
        }catch(Exception e){
            log.error("An exception occurred while writing the table body closer. Message is: " + e.getMessage(), e);
        }// end try/catch
        try{
            writeTableCloser();
        }catch(Exception e){
            log.error("An exception occurred while writing the table closer. Message is: " + e.getMessage(), e);
        }// end try/catch
        try{
            writeBottomBanner();
        }catch(Exception e){
            log.error("An exception occurred while writing the bottom banner. Message is: " + e.getMessage(), e);
        }// end try/catch

    }// end writeTable

    /**
     * This method is used to write the HTML table opener.
     */
    @SuppressWarnings("unchecked")
    private void writeTableOpener() {
        if(log.isDebugEnabled()){
            log.debug("[" + tableModel.getTableId() + "] writeTableOpener called for table [" + tableModel.getTableId() + "]");
        }// end if
        StringBuffer buffer = new StringBuffer();
        Map<Object, Object> attributes = (Map<Object, Object>) tableModel.getAttributes().clone();
        attributes.put("id", tableModel.getTableId());
        buffer.append(TABLE_START).append(attributes).append(">");
        write(buffer.toString());
        if(log.isDebugEnabled()){
            log.debug("[" + tableModel.getTableId() + "] writeTableOpener end");
        }// end if
    }// end writeTableOpener

    /**
     * This method is used to write the HTML table head opener.
     */
    private void writeTableHeadOpener() {
        if(log.isDebugEnabled()){
            log.debug("[" + tableModel.getTableId() + "] writeTableHeadOpener called for table [" + tableModel.getTableId() + "]");
        }// end if
        write("<thead>");
        if(log.isDebugEnabled()){
            log.debug("[" + tableModel.getTableId() + "] writeTableHeadOpener end");
        }// end if
    }// end writeTableHeadOpener

    /**
     * This method is used to write the HTML table head closer.
     */
    private void writeTableHeadCloser() {
        if(log.isDebugEnabled()){
            log.debug("[" + tableModel.getTableId() + "] writeTableHeadCloser called for table [" + tableModel.getTableId() + "]");
        }// end if
        write("</thead>");
        if(log.isDebugEnabled()){
            log.debug("[" + tableModel.getTableId() + "] writeTableHeadCloser end");
        }// end if
    }// end writeTableHeadCloser

    /**
     * This method is used to write the HTML table row opener.
     */
    private void writeTableRowOpener() {
        if(log.isDebugEnabled()){
            log.debug("[" + tableModel.getTableId() + "] writeTableRowOpener called for table [" + tableModel.getTableId() + "]");
        }// end if
        write("<tr>");
        if(log.isDebugEnabled()){
            log.debug("[" + tableModel.getTableId() + "] writeTableRowOpener end");
        }// end if
    }// end writeTableRowOpener

    /**
     * This method is used to write the HTML table row closer.
     */
    private void writeTableRowCloser() {
        if(log.isDebugEnabled()){
            log.debug("[" + tableModel.getTableId() + "] writeTableRowCloser called for table [" + tableModel.getTableId() + "]");
        }// end if
        write("</tr>");
        if(log.isDebugEnabled()){
            log.debug("[" + tableModel.getTableId() + "] writeTableRowCloser end");
        }// end if
    }// end writeTableRowCloser

    /**
     * This method is used to write the HTML table headers.
     */
    private void writeTableHeader() {
        if(log.isDebugEnabled()){
            log.debug("[" + tableModel.getTableId() + "] writeTableHeader called for table [" + tableModel.getTableId() + "]");
        }// end if
        writeTableHeadOpener();
        writeTableRowOpener();
        if(tableModel.isListPage()){
            for(int i = 0, j = LIST_TABLE_HEADERS.length;i < j;i++){
                write(TH_START);
                write(LIST_TABLE_HEADERS[i]);
                write(TH_END);
            }// end while
        }else{
            for(int i = 0, j = RUN_STATUS_TABLE_HEADERS.length;i < j;i++){
                write(TH_START);
                write(RUN_STATUS_TABLE_HEADERS[i]);
                write(TH_END);
            }// end while
        }// end if/else
        writeTableRowCloser();
        writeTableHeadCloser();
        if(log.isDebugEnabled()){
            log.debug("[" + tableModel.getTableId() + "] writeTableHeader end");
        }// end if
    }// end writeTableHeader

    /**
     * This method is used to write the HTML table body opener.
     */
    private void writeTableBodyOpener() {
        if(log.isDebugEnabled()){
            log.debug("[" + tableModel.getTableId() + "] writeTableBodyOpener called for table [" + tableModel.getTableId() + "]");
        }// end if
        write("<tbody>");
        if(log.isDebugEnabled()){
            log.debug("[" + tableModel.getTableId() + "] writeTableBodyOpener end");
        }// end if
    }// end writeTableBodyOpener

    /**
     * This method is used to write the HTML table body.
     *
     * @throws Exception
     *         An exception if the method logic fails.
     */
    private void writeTableBody() throws Exception {
        if(log.isDebugEnabled()){
            log.debug("[" + tableModel.getTableId() + "] writeTableBody called for table [" + tableModel.getTableId() + "]");
        }// end if
        if(null != tableModel.getRowListPage() || !tableModel.getRowListPage().isEmpty()){

            HttpServletRequest request = (HttpServletRequest) pagecontext.getRequest();
            String contextPath = request.getContextPath();
            StringBuffer sb = new StringBuffer();
            BatchAppModel batchModel = null;
            JmsBatchApp batchApp = null;
            boolean hasActiveSchedule = false;
            Iterator<TableRow> rowiter = tableModel.getRowListPage().iterator();
            while(rowiter.hasNext()){
                hasActiveSchedule = false;
                batchModel = (BatchAppModel) rowiter.next();
                batchApp = batchModel.getJmsBatchApp();
                sb.append(TR_START);
                // schedule indicator
                for(int i = 0, j = batchApp.getSchedule().size();i < j;i++){
                    if("Y".equals(batchApp.getSchedule().get(i).getActive())){
                        hasActiveSchedule = true;
                        break;
                    }// end if
                }// end for
                sb.append(TD_START);
                sb.append(IMG_SVG_START);
                if(!ShamenClientUtil.isEmpty(batchApp.getSchedule()) && null != batchApp.getSchedule().get(0).getScheduleRefId()){
                    if(hasActiveSchedule){
                        sb.append(ICON_SCHEDULE_ACTIVE_TITLE);
                        sb.append(IMG_SVG_1_END);
                        sb.append(BatchAppHandlerTagSvgIcons.CALENDAR_CHECK_ICON);
                    }else{
                        sb.append(ICON_SCHEDULE_INACTIVE_TITLE);
                        sb.append(IMG_SVG_1_END);
                        sb.append(BatchAppHandlerTagSvgIcons.CALENDAR_TIMES_ICON);
                    }// end if/else
                }else{
                    sb.append(ICON_SCHEDULE_NO_SCHEDULE_TITLE);
                    sb.append(IMG_SVG_1_END);
                    sb.append(BatchAppHandlerTagSvgIcons.CALENDAR_MINUS_ICON);
                }// end if/else
                sb.append(IMG_SVG_2_END);
                sb.append(TD_END);
                // controller indicator
                sb.append(TD_START);
                sb.append(IMG_SVG_START);
                if(CONTROLLER_STATUS_CD_CONNECTED.equals(batchApp.getControllerStatus())){
                    sb.append(ICON_CONTROLLER_CONNECTED_TITLE);
                    sb.append(IMG_SVG_1_END);
                    sb.append(BatchAppHandlerTagSvgIcons.SERVER_GREEN_ICON);
                }else if(CONTROLLER_STATUS_CD_WAITING.equals(batchApp.getControllerStatus())){
                    sb.append(ICON_CONTROLLER_WAITING_TITLE);
                    sb.append(IMG_SVG_1_END);
                    sb.append(BatchAppHandlerTagSvgIcons.HOURGLASS_ICON);
                }else{
                    sb.append(ICON_CONTROLLER_DISCONNECTED_TITLE);
                    sb.append(IMG_SVG_1_END);
                    sb.append(BatchAppHandlerTagSvgIcons.SERVER_RED_ICON);
                }// end if
                sb.append(IMG_SVG_2_END);
                sb.append(TD_END);
                // last run indicator
                sb.append(TD_START);
                if(!ShamenClientUtil.isEmpty(batchApp.getSchedule()) && null != batchApp.getSchedule().get(0).getScheduleRefId()){
                    sb.append(IMG_SVG_START);
                    if(LASTRUN_STATUS_CD_MULTIPLE.equals(batchApp.getLastRunStatusCd())){
                        sb.append(ICON_LASTRUN_MULTIPLE_TITLE);
                        sb.append(IMG_SVG_1_END);
                        sb.append(BatchAppHandlerTagSvgIcons.UNKNOWN_ICON);
                    }else if(LASTRUN_STATUS_CD_GOOD.equals(batchApp.getLastRunStatusCd())){
                        sb.append(ICON_LASTRUN_GOOD_TITLE);
                        sb.append(IMG_SVG_1_END);
                        sb.append(BatchAppHandlerTagSvgIcons.SUCCESSFUL_ICON);
                    }else if(LASTRUN_STATUS_CD_INPROGRESS.equals(batchApp.getLastRunStatusCd())){
                        sb.append(ICON_LASTRUN_INPROGRESS_TITLE);
                        sb.append(IMG_SVG_1_END);
                        sb.append(BatchAppHandlerTagSvgIcons.HOURGLASS_ICON);
                    }else if(LASTRUN_STATUS_CD_OFFSCHEDULE.equals(batchApp.getLastRunStatusCd())){
                        sb.append(ICON_LASTRUN_OFFSCHEDULE_TITLE);
                        sb.append(IMG_SVG_1_END);
                        sb.append(BatchAppHandlerTagSvgIcons.OFFSCHEDULE_ICON);
                    }else if(LASTRUN_STATUS_CD_BAD.equals(batchApp.getLastRunStatusCd())){
                        sb.append(ICON_LASTRUN_BAD_TITLE);
                        sb.append(IMG_SVG_1_END);
                        sb.append(BatchAppHandlerTagSvgIcons.UNSUCCESSFUL_ICON);
                    }else if(LASTRUN_STATUS_CD_PENDING.equals(batchApp.getLastRunStatusCd())){
                        sb.append(ICON_LASTRUN_PENDING_TITLE);
                        sb.append(IMG_SVG_1_END);
                        sb.append(BatchAppHandlerTagSvgIcons.PENDING_ICON);
                    }else if(LASTRUN_STATUS_CD_INACTIVE.equals(batchApp.getLastRunStatusCd())){
                        sb.append(ICON_LASTRUN_INACTIVE_TITLE);
                        sb.append(IMG_SVG_1_END);
                        sb.append(BatchAppHandlerTagSvgIcons.INACTIVE_ICON);
                    }else{
                        sb.append(ICON_LASTRUN_UNKNOWN_TITLE);
                        sb.append(IMG_SVG_1_END);
                        sb.append(BatchAppHandlerTagSvgIcons.UNKNOWN_ICON);
                    }// end if
                }// end if
                sb.append(IMG_SVG_2_END);
                sb.append(TD_END);
                // batch app name
                sb.append(TD_START);
                // Set the link to the detail
                sb.append("<a href=\"");
                sb.append(contextPath);
                sb.append("/BatchDetail.shamen?batchAppRefId=");
                sb.append(batchApp.getBatchAppRefId());
                sb.append("&");
                sb.append(TAG_PARENT_URL);
                sb.append("=");
                // sb.append("'");
                sb.append(getFormattedUrlWithParms(TAG_SHOW_DETAIL));
                // sb.append("'");
                sb.append("\">");
                sb.append(ShamenClientUtil.isNullOrEmpty(batchModel.getDisplayName()) ? batchApp.getName() : batchModel.getDisplayName());
                sb.append("</a>");
                sb.append(TD_END);
                // type
                sb.append(TD_START);
                if("COL".equals(batchApp.getType())){
                    sb.append(batchApp.getTypeDescription());
                }else{
                    sb.append("Standard");
                }// end if-else
                sb.append(TD_END);
                // execution count
                sb.append(TD_START);
                sb.append(batchApp.getExecutionCount());
                sb.append(TD_END);
                if("Y".equalsIgnoreCase(batchModel.getAllowRun())){
                    // Run icon
                    sb.append(TD_START);
                    // set button
                    sb.append(BUTTON_START);
                    sb.append(" id=\"id-");
                    sb.append(batchApp.getBatchAppRefId());
                    sb.append("\" class=\"shamen-btn shamen-btn-info");
                    if(CONTROLLER_STATUS_CD_CONNECTED.equals(batchApp.getControllerStatus()) || CONTROLLER_STATUS_CD_WAITING.equals(batchApp.getControllerStatus())){
                        sb.append("\" title=\"Launch Batch Job\"");
                    }else{
                        sb.append(" disabled\" disabled=\"disabled\" title=\"Launch not available. Controller is not connected.\"");
                    }// end if/else
                    sb.append(" onclick=\"openLightBox('");
                    sb.append(batchApp.getBatchAppRefId());
                    sb.append("')\">Launch");
                    sb.append(BUTTON_END);
                    sb.append(TD_END);
                    write(buildParameterBox(batchModel));

                }else{
                    sb.append(TD_START);
                    sb.append(TD_END);
                }// end if/else
                sb.append(TR_END);

            }// end while
            write(sb.toString());
        }else{
            write(NO_MATCHING_BATCH_PROVIDED);
        }// end if/else
        if(log.isDebugEnabled()){
            log.debug("[" + tableModel.getTableId() + "] writeTableBody end");
        }// end if
    }// end writeTableBody

    /**
     * This method formats the run status list into html
     */
    public void writeRunStatusTable() {
        if(log.isDebugEnabled()){
            log.debug("[" + tableModel.getTableId() + "] writeRunStatusTable called for table [" + tableModel.getTableId() + "]");
        }// end if
        StringBuffer sb = new StringBuffer();
        // Do the rows
        Iterator<TableRow> rowiter = tableModel.getRowListPage().iterator();
        int counter = 0;
        while(rowiter.hasNext()){

            RunStatusModel runStatusModel = (RunStatusModel) rowiter.next();

            JmsRunStatus rs = runStatusModel.getJmsRunStatus();
            String rowClass = counter % 2 == 0 ? "even" : "odd";
            counter++;
            sb.append("<tr class=\"").append(rowClass).append("\">");
            String duration = "";
            // only do duration math if the job is complete
            if(!rs.getStopTs().equals(ShamenClientUtil.getSqlTimestamp(ShamenClientUtil.DEFAULT_TIMESTAMP))){
                duration = rs.getTimeDifference();
            }// end if
             // if description is blank, then it's a completion status, otherwise it's a regular run
            if(rs.getDescription() == null || "".equals(rs.getDescription())){
                sb.append(putInCols(rs.getStatusDesc(), ShamenClientUtil.getFormattedDateTimeAsString(rs.getStartTs()), ShamenClientUtil.getFormattedDateTimeAsString(rs.getStopTs()), duration, rs.getResultDesc(), (rs.getScheduleRefId() != null ? "Schedule" : "User")));
            }else{
                sb.append(putInCols(rs.getStatusDesc(), ShamenClientUtil.getFormattedDateTimeAsString(rs.getStartTs()), ShamenClientUtil.getFormattedDateTimeAsString(rs.getStopTs()), duration, rs.getResultDetail(), (rs.getScheduleRefId() != null ? "Schedule" : "User")));
            }// end if
            sb.append("<td>");
            if(rs.getCollectionMembers() != null && !rs.getCollectionMembers().isEmpty()){
                sb.append(IMG_SVG_START);
                sb.append("Show Run Details\" class=\"\" onclick=\"toggleSubrow(this)\" id=\"colapseIcon-");
                sb.append(rs.getRunNumber());
                sb.append(IMG_SVG_1_END);
                sb.append(BatchAppHandlerTagSvgIcons.COLAPSE_ICON);
                sb.append(IMG_SVG_2_END);
                sb.append(IMG_SVG_START);
                sb.append("Hide Run Details\" class=\"hidden\" onclick=\"toggleSubrow(this)\" id=\"expandIcon-");
                sb.append(rs.getRunNumber());
                sb.append(IMG_SVG_1_END);
                sb.append(BatchAppHandlerTagSvgIcons.EXPAND_ICON);
                sb.append(IMG_SVG_2_END);
            }// end if
            sb.append(TD_END);
            sb.append(TR_END);
            // if the collection member list has data, then display it as a subrow
            if(rs.getCollectionMembers() != null && !rs.getCollectionMembers().isEmpty()){
                sb.append("<tr id=\"subrow");
                sb.append(rs.getRunNumber());
                sb.append("\" class=\"hidden\"");
                sb.append(">");
                sb.append("<td colspan=\"7\">");
                // add table
                sb.append("<table class=\"sublist\">");
                // add table headings
                sb.append("<thead><tr>");
                // if the batch app is standard, do standard sub table, otherwise do collection subtable
                if(!runStatusModel.getJmsBatchApp().getFromCollection()){
                    sb = formatBatchSubTableHeadings(sb);
                }else{
                    sb = formatBatchCollectionSubTableHeadings(sb);
                }// end if-else
                sb.append("</tr></thead><tbody>");
                for(int k = 0, l = rs.getCollectionMembers().size();k < l;k++){
                    JmsRunStatus subRs = rs.getCollectionMembers().get(k);
                    // if the batch app is standard, do standard sub table, otherwise do collection subtable
                    if(!runStatusModel.getJmsBatchApp().getFromCollection()){
                        if("".equals(subRs.getResultDetail())){
                            sb.append(putInRowAndCols(ShamenClientUtil.getFormattedDateTimeAsString(subRs.getStartTs()), subRs.getStatusDesc(), subRs.getDescription()));
                        }else{
                            sb.append(putInRowAndCols(ShamenClientUtil.getFormattedDateTimeAsString(subRs.getStartTs()), subRs.getStatusDesc(), subRs.getResultDetail()));
                        }// end if-else
                    }else{
                        // If it's the completion message of the subjob, then increase the font, otherwise display as normal but indent 1st column
                        if("DON".equals(subRs.getStatusCd()) && !"".equals(ShamenClientUtil.getFormattedDateTimeAsString(subRs.getStopTs()))){
                            sb.append("<tr class=\"runStatusCollectionCompletionTableRow\">");
                            sb.append(TD_START);
                        }else{
                            sb.append(TR_START);
                            sb.append("<td class=\"runStatusCollectionChildTableRow\">");
                        }// end if-else
                        sb.append(subRs.getBatchApp().getName());
                        sb.append(TD_END);
                        if(subRs.getDescription() == null || "".equals(subRs.getDescription())){
                            if("".equals(subRs.getResultDetail())){
                                sb.append(putInCols(ShamenClientUtil.getFormattedDateTimeAsString(subRs.getStartTs()), ShamenClientUtil.getFormattedDateTimeAsString(subRs.getStopTs()), subRs.getStatusDesc(), subRs.getResultDesc()));
                            }else{
                                sb.append(putInCols(ShamenClientUtil.getFormattedDateTimeAsString(subRs.getStartTs()), ShamenClientUtil.getFormattedDateTimeAsString(subRs.getStopTs()), subRs.getStatusDesc(), subRs.getResultDetail()));
                            }// end if/else
                        }else{
                            sb.append(putInCols(ShamenClientUtil.getFormattedDateTimeAsString(subRs.getStartTs()), ShamenClientUtil.getFormattedDateTimeAsString(subRs.getStopTs()), subRs.getStatusDesc(), subRs.getDescription()));
                        }// end if-else
                        sb.append(TR_END);
                    }// end if-else
                }// end for
                sb.append("</tbody>").append(TABLE_END);
                sb.append("</td></tr>");
            }// end if
        }// end for
        write(sb.toString());
        if(log.isDebugEnabled()){
            log.debug("[" + tableModel.getTableId() + "] writeRunStatusTable end");
        }// end if
    }// end formatRunStatusList

    /**
     * This method is used to write the HTML table body.
     *
     * @throws Exception
     *         An exception if the method logic fails.
     */
    private void writeTableDetailBody() throws Exception {
        if(log.isDebugEnabled()){
            log.debug("[" + tableModel.getTableId() + "] writeTableDetailBody called for table [" + tableModel.getTableId() + "]");
        }// end if
        if(null != tableModel.getRowListPage() && !tableModel.getRowListPage().isEmpty()){
            writeRunStatusTable();
        }else{
            write(NO_RUN_STATUS_HISTORY);
        }// end if/else
        if(log.isDebugEnabled()){
            log.debug("[" + tableModel.getTableId() + "] writeTableDetailBody end");
        }// end if
    }// end writeTableDetailBody

    /**
     * This method is used to write the HTML table body closer.
     */
    private void writeTableBodyCloser() {
        if(log.isDebugEnabled()){
            log.debug("[" + tableModel.getTableId() + "] writeTableBodyCloser called for table [" + tableModel.getTableId() + "]");
        }// end if
        write("</tbody>");
        if(log.isDebugEnabled()){
            log.debug("[" + tableModel.getTableId() + "] writeTableBodyCloser end");
        }// end if
    }// end writeTableBodyCloser

    /**
     * This method is used to write the HTML table closer.
     */
    private void writeTableCloser() {
        if(log.isDebugEnabled()){
            log.debug("[" + tableModel.getTableId() + "] writeTableCloser called for table [" + tableModel.getTableId() + "]");
        }// end if
        write(TABLE_END);
        if(log.isDebugEnabled()){
            log.debug("[" + tableModel.getTableId() + "] writeTableCloser end");
        }// end if
    }// end writeTableCloser

    /**
     * This method is used to write the HTML table bottom banner.
     */
    private void writeBottomBanner() {
        if(log.isDebugEnabled()){
            log.debug("[" + tableModel.getTableId() + "] writeBottomBanner called for table [" + tableModel.getTableId() + "]");
        }// end if
        if(properties.getAddPagingBannerBottom()){
            writeSearchResultAndNavigation();
        }// end if
        if(log.isDebugEnabled()){
            log.debug("[" + tableModel.getTableId() + "] writeBottomBanner end");
        }// end if
    }// end writeBottomBanner

    /**
     * This method builds the parameter pop up box for running batch job.
     * 
     * @param batchModel
     *        the Batch Model associated
     * @return String
     * @throws JspException
     *         if an exception occurred
     */
    public String buildParameterBox(BatchAppModel batchModel) throws JspException {
        if(log.isDebugEnabled()){
            log.debug("[" + tableModel.getTableId() + "] buildParameterBox called for table [" + tableModel.getTableId() + "]");
        }// end i
        StringBuffer sb = new StringBuffer();
        HttpServletRequest request = (HttpServletRequest) pagecontext.getRequest();
        String contextPath = request.getContextPath();
        JmsBatchApp batch = batchModel.getJmsBatchApp();
        sb.append("<div class=\"shamen-modal top\" tabindex=\"-1\" role=\"dialog\" aria-labelledby=\"myModalLabel\" aria-hidden=\"true\" data-backdrop=\"true\" id=\"lightbox-").append(batch.getBatchAppRefId()).append("\">");
        sb.append("<div class=\"shamen-modal-dialog shamen-modal-top shamen-modal-notify shamen-modal-info\">");
        sb.append("<div class=\"shamen-modal-content\">\r\n");
        sb.append("<div class=\"shamen-modal-header\">\r\n");
        sb.append("<h3 class=\"shamen-modal-title\">Launch Batch Application</h3>\r\n");
        sb.append(DIV_END).append("\r\n");
        sb.append("<div class=\"shamen-modal-body\">\r\n");
        sb.append("<label class=\"font-weight-bold\">Batch Name:</label>\r\n");
        sb.append("<label class=\"info-text\" id=\"batch-name\">");
        sb.append(ShamenClientUtil.isNullOrEmpty(batchModel.getDisplayName()) ? batch.getName() : batchModel.getDisplayName());
        sb.append("</label>\r\n");
        if("Y".equalsIgnoreCase(batchModel.getUseParameterInput())){
            sb.append("<label class=\"font-weight-bold\">Batch Parameters</label>\r\n");
            if("Y".equalsIgnoreCase(batchModel.getShowDefaultMessage())){
                sb.append("<label class=\"info-text\">comma separated key=value pairs</label>\r\n");
                sb.append("<label class=\"info-text\">leave blank if there are no parameters</label>\r\n");
            }// end if
            if(!ShamenClientUtil.isNullOrEmpty(batchModel.getCustomMessage())){
                sb.append("<label class=\"custom-message\">");
                sb.append(batchModel.getCustomMessage());
                sb.append("</label>\r\n");
            }// end if
            if(!ShamenClientUtil.isNullOrEmpty(batchModel.getBodyContent())){
                sb.append(batchModel.getBodyContent());
            }// end if
        }// end if
        sb.append(DIV_END).append("\r\n");
        sb.append("<div class=\"shamen-modal-footer\">");
        sb.append(BUTTON_START);
        sb.append(" class=\"shamen-btn shamen-btn-info\"  onclick=\"launchJob(");
        sb.append("'").append(batch.getBatchAppRefId()).append("',");
        sb.append("'").append(batchModel.getUseParameterInput()).append("',");
        sb.append("'");
        sb.append(contextPath);
        sb.append("/BatchDetail.shamen?batchAppRefId=");
        sb.append(batch.getBatchAppRefId());
        sb.append("&");
        sb.append(TAG_PARENT_URL);
        sb.append("=");
        sb.append(getFormattedUrlWithParms(TAG_RUN_JOB));
        if(!ShamenClientUtil.isNullOrEmpty(batchModel.getDefaultParameter())){
            sb.append("%26defaultParameter=");
            sb.append(batchModel.getDefaultParameter());
        }// end if
        sb.append("')\">Launch");
        sb.append(BUTTON_END);
        sb.append("&nbsp;&nbsp;");
        sb.append(BUTTON_START);
        sb.append(" class=\"shamen-btn shamen-btn-info\" onclick=\"dismissLightBox('");
        sb.append(batch.getBatchAppRefId());
        sb.append("')\">Cancel");
        sb.append(BUTTON_END);
        sb.append(DIV_END).append("\r\n");
        sb.append(DIV_END).append("\r\n");
        sb.append(DIV_END).append("\r\n");
        sb.append(DIV_END).append("\r\n");
        if(log.isDebugEnabled()){
            log.debug("[" + tableModel.getTableId() + "] writeBottomBanner end");
        }// end if
        return sb.toString();
    }// end buildParameterBox

    /**
     * This method is used to write the search results summary and pagination bar for the HTML table.
     */
    private void writeSearchResultAndNavigation() {
        if(log.isDebugEnabled()){
            log.debug("[" + tableModel.getTableId() + "] writeSearchResultAndNavigation called for table [" + tableModel.getTableId() + "]");
        }// end if
        if(tableModel.getPagesize() != 0){
            Href clonedHref = (Href) tableModel.getHref().clone();
            write("<div class=\"bottom-banner\">");
            write(listHelper.getSearchResultsSummary());
            write(listHelper.getPageNavigationBar(clonedHref, PARAMETER_PAGE));
            write("</div>");
        }// end if
        if(log.isDebugEnabled()){
            log.debug("[" + tableModel.getTableId() + "] writeSearchResultAndNavigation end");
        }// end if
    }// end writeSearchResultAndNavigation

    /**
     * This method gets the URL with all the necessary parameters for what is required to forward to the detail page.
     * 
     * @param function
     *        function to redirect to.
     * @return url
     */
    public String getFormattedUrlWithParms(String function) {
        HttpServletResponse response = (HttpServletResponse) pagecontext.getResponse();
        StringBuffer url = new StringBuffer();
        String contextPath = ((HttpServletRequest) pagecontext.getRequest()).getContextPath();

        // prepend the context path if any.
        // actually checks if context path is already there for people which manually add it
        if(contextPath != null && homeURL != null && homeURL.startsWith("/") && !homeURL.startsWith(contextPath)){
            url.append(contextPath);
        }// end if
        url.append(homeURL);
        // replace all the &'s to parameter safe hex value.
        url = new StringBuffer(url.toString().replace("&", "%26"));

        // Add request variable that indicates the page should only show the detail portion.
        url.append(SAFE_PARM).append(function).append("=").append("Y");

        return response.encodeURL(url.toString());

    }// end getFormattedUrlWithParms

    /**
     * This method takes any number of data variables and put them into separate columns within a table row.
     * 
     * @param data
     * @return table row with columns
     */
    public String putInRowAndCols(String... data) {
        StringBuffer sb = new StringBuffer();
        sb.append(TR_START);
        for(int i = 0, j = data.length;i < j;i++){
            sb.append(TD_START);
            sb.append(data[i]);
            sb.append(TD_END);
        }// end for
        sb.append(TR_END);
        return sb.toString();
    }// end putInRowAndCols

    /**
     * This method will put all the junk from the data array into table columns. It does not have a "tr" definition
     * 
     * @param data
     * @return formatted data
     */
    public String putInCols(String... data) {
        StringBuffer sb = new StringBuffer();
        for(int i = 0, j = data.length;i < j;i++){
            sb.append(TD_START);
            sb.append(data[i]);
            sb.append(TD_END);
        }// end for
        return sb.toString();
    }// end putInCols

    /**
     * This method formats the sub table headings for a standard batch job.
     * 
     * @param sb
     * @return
     */
    private StringBuffer formatBatchSubTableHeadings(StringBuffer sb) {
        sb.append("<th>Start Time</th>");
        sb.append("<th>Status</th>");
        sb.append("<th>Details</th>");
        return sb;
    }// end formatBatchSubTableHeadings

    /**
     * This method formats the sub table headings for a collection batch job.
     * 
     * @param sb
     * @return
     */
    private StringBuffer formatBatchCollectionSubTableHeadings(StringBuffer sb) {
        sb.append("<th>Collection Member Name</th>");
        sb.append("<th>Start Time</th>");
        sb.append("<th>Stop Time</th>");
        sb.append("<th>Status</th>");
        sb.append("<th>Details</th>");
        return sb;
    }// end formatBatchCollectionSubTableHeadings

    /**
     * This method is used to write the parameter {@code str} to the {@link JspWriter}.
     *
     * @param str
     *        The {@code String} to write.
     */
    public void write(String str) {
        if(null != str){
            try{
                writer.write(str);
            }catch(IOException e){
                throw new RuntimeException("gov.doc.isu.shamen.taglib.renderers.HTMLView", e.getCause());
            }// end try/catch
        }// end if
    }// end write
}// end class
