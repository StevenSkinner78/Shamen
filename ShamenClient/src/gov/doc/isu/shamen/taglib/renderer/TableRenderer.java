/**
 * @(#)TableWriter.java 1.0.0 Copyright (c) 2018 Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit All Rights Reserved. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: -Redistribution of source code must retain the above copyright notice, this list of conditions and the following disclaimer. -Redistribution in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. Neither the name of Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit or the names of contributors may be used to endorse or promote products derived from this software without specific prior written permission. This software is provided "AS IS," without a warranty of any kind. ALL EXPRESS OR IMPLIED CONDITIONS,
 *                      REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. You acknowledge that this
 *                      software is not designed, licensed or intended for use in the design, construction, operation or maintenance of any nuclear facility.
 */
package gov.doc.isu.shamen.taglib.renderer;

import static gov.doc.isu.shamen.core.ShamenConstants.ICON_LASTRUN_BAD_TITLE;
import static gov.doc.isu.shamen.core.ShamenConstants.ICON_LASTRUN_GOOD_TITLE;
import static gov.doc.isu.shamen.core.ShamenConstants.ICON_LASTRUN_INACTIVE_TITLE;
import static gov.doc.isu.shamen.core.ShamenConstants.ICON_LASTRUN_INPROGRESS_TITLE;
import static gov.doc.isu.shamen.core.ShamenConstants.ICON_LASTRUN_OFFSCHEDULE_TITLE;
import static gov.doc.isu.shamen.core.ShamenConstants.ICON_LASTRUN_PENDING_TITLE;
import static gov.doc.isu.shamen.core.ShamenConstants.ICON_LASTRUN_UNKNOWN_TITLE;
import static gov.doc.isu.shamen.core.ShamenConstants.ICON_SCHEDULE_ACTIVE_TITLE;
import static gov.doc.isu.shamen.core.ShamenConstants.ICON_SCHEDULE_INACTIVE_TITLE;
import static gov.doc.isu.shamen.core.ShamenConstants.LASTRUN_STATUS_CD_BAD;
import static gov.doc.isu.shamen.core.ShamenConstants.LASTRUN_STATUS_CD_GOOD;
import static gov.doc.isu.shamen.core.ShamenConstants.LASTRUN_STATUS_CD_INPROGRESS;
import static gov.doc.isu.shamen.core.ShamenConstants.LASTRUN_STATUS_CD_OFFSCHEDULE;
import static gov.doc.isu.shamen.core.ShamenConstants.LASTRUN_STATUS_CD_PENDING;

import java.util.Iterator;
import java.util.List;

import javax.servlet.jsp.JspException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.doc.isu.shamen.jms.models.JmsSchedule;
import gov.doc.isu.shamen.taglib.BatchAppHandlerTagSvgIcons;
import gov.doc.isu.shamen.taglib.models.ScheduleModel;
import gov.doc.isu.shamen.taglib.models.TableRow;
import gov.doc.isu.shamen.util.ShamenClientUtil;

/**
 * This class is used to allow exporting table data in HTML format.
 * <p>
 * Modified: Added logic to match filter value w/ 'equals' rather that w/ 'contains' when useEquals flag is provided. Added legend property check and rendering of legend to the IMP table display.
 * </p>
 * 
 * @author Joseph Burris JCCC, jsb000is
 * @author <strong>Brian Hicks</strong>, JCCC - Apr 03, 2019 - modification author
 * @author <strong>Steven Skinner</strong> JCCC, Jan 14, 2021
 */
public class TableRenderer {
    private static Log log = LogFactory.getLog("gov.doc.isu.shamen.taglib.renderer.TableRenderer");
    private List<TableRow> rowList;
    private StringBuffer writer;
    private String tableId;
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
    private static final String NO_SCHEDULE_AVAILABLE = "<tr><td colspan=\"6\">No Schedule(s) Available For This Batch Job</td></tr>";
    private static final String[] SCHEDULE_TABLE_HEADERS = {"", "", "START DATE", "START TIME", "FREQUENCY", "SCHEDULE DETAILS"};

    /**
     * Default Constructor
     */
    public TableRenderer() {
        super();
    }// end constructor

    /**
     * This overloaded constructor is used to instantiate this class.
     *
     * @param rowList
     *        The backing model for the table of data.
     * @param writer
     *        The {@link StringBuilder} to use for exporting data.
     */
    public TableRenderer(List<TableRow> rowList, String tableId) {
        super();
        this.rowList = rowList;
        this.tableId = tableId;
    }// end constructor

    /**
     * This method is used to write the HTML table.
     *
     * @throws JspException
     *         An exception if the method logic fails.
     */
    public StringBuffer writeTable() throws JspException {
        if(writer == null){
            writer = new StringBuffer();
        }// end if
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
            writeTableSchedulelBody();
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

        return writer;

    }// end writeTable

    /**
     * This method is used to write the HTML table opener.
     */
    private void writeTableOpener() {
        if(log.isDebugEnabled()){
            log.debug("[" + tableId + "] writeTableOpener called for table [" + tableId + "]");
        }// end if
        writer.append(TABLE_START).append(" class=\"shamen-table\" id=\"").append(tableId).append("\">");
        if(log.isDebugEnabled()){
            log.debug("[" + tableId + "] writeTableOpener end");
        }// end if
    }// end writeTableOpener

    /**
     * This method is used to write the HTML table head opener.
     */
    private void writeTableHeadOpener() {
        if(log.isDebugEnabled()){
            log.debug("[" + tableId + "] writeTableHeadOpener called for table [" + tableId + "]");
        }// end if
        writer.append("<thead>");
        if(log.isDebugEnabled()){
            log.debug("[" + tableId + "] writeTableHeadOpener end");
        }// end if
    }// end writeTableHeadOpener

    /**
     * This method is used to write the HTML table head closer.
     */
    private void writeTableHeadCloser() {
        if(log.isDebugEnabled()){
            log.debug("[" + tableId + "] writeTableHeadCloser called for table [" + tableId + "]");
        }// end if
        writer.append("</thead>");
        if(log.isDebugEnabled()){
            log.debug("[" + tableId + "] writeTableHeadCloser end");
        }// end if
    }// end writeTableHeadCloser

    /**
     * This method is used to write the HTML table row opener.
     */
    private void writeTableRowOpener() {
        if(log.isDebugEnabled()){
            log.debug("[" + tableId + "] writeTableRowOpener called for table [" + tableId + "]");
        }// end if
        writer.append("<tr>");
        if(log.isDebugEnabled()){
            log.debug("[" + tableId + "] writeTableRowOpener end");
        }// end if
    }// end writeTableRowOpener

    /**
     * This method is used to write the HTML table row closer.
     */
    private void writeTableRowCloser() {
        if(log.isDebugEnabled()){
            log.debug("[" + tableId + "] writeTableRowCloser called for table [" + tableId + "]");
        }// end if
        writer.append("</tr>");
        if(log.isDebugEnabled()){
            log.debug("[" + tableId + "] writeTableRowCloser end");
        }// end if
    }// end writeTableRowCloser

    /**
     * This method is used to write the HTML table headers.
     */
    private void writeTableHeader() {
        if(log.isDebugEnabled()){
            log.debug("[" + tableId + "] writeTableHeader called for table [" + tableId + "]");
        }// end if
        writeTableHeadOpener();
        writeTableRowOpener();
        for(int i = 0, j = SCHEDULE_TABLE_HEADERS.length;i < j;i++){
            writer.append(TH_START);
            writer.append(SCHEDULE_TABLE_HEADERS[i]);
            writer.append(TH_END);
        }// end for
        writeTableRowCloser();
        writeTableHeadCloser();
        if(log.isDebugEnabled()){
            log.debug("[" + tableId + "] writeTableHeader end");
        }// end if
    }// end writeTableHeader

    /**
     * This method is used to write the HTML table body opener.
     */
    private void writeTableBodyOpener() {
        if(log.isDebugEnabled()){
            log.debug("[" + tableId + "] writeTableBodyOpener called for table [" + tableId + "]");
        }// end if
        writer.append("<tbody>");
        if(log.isDebugEnabled()){
            log.debug("[" + tableId + "] writeTableBodyOpener end");
        }// end if
    }// end writeTableBodyOpener

    /**
     * This method formats the Schedule list into html table
     */
    public void writeScheduleTable() {
        if(log.isDebugEnabled()){
            log.debug("[" + tableId + "] writeScheduleTable called for table [" + tableId + "]");
        }// end if
        StringBuffer sb = new StringBuffer();
        // Do the rows
        Iterator<TableRow> rowiter = rowList.iterator();
        int counter = 0;
        while(rowiter.hasNext()){

            ScheduleModel scheduleModel = (ScheduleModel) rowiter.next();

            JmsSchedule sch = scheduleModel.getJmsSchedule();
            String rowClass = counter % 2 == 0 ? "even" : "odd";
            counter++;
            sb.append("<tr class=\"").append(rowClass).append("\">");
            // schedule indicator
            sb.append(TD_START);
            sb.append(IMG_SVG_START);
            if("Y".equals(sch.getActive())){
                sb.append(ICON_SCHEDULE_ACTIVE_TITLE);
                sb.append(IMG_SVG_1_END);
                sb.append(BatchAppHandlerTagSvgIcons.CALENDAR_CHECK_ICON);
            }else{
                sb.append(ICON_SCHEDULE_INACTIVE_TITLE);
                sb.append(IMG_SVG_1_END);
                sb.append(BatchAppHandlerTagSvgIcons.CALENDAR_TIMES_ICON);
            }// end if-else
            sb.append(IMG_SVG_2_END);
            sb.append(TD_END);
            // last run indicator
            sb.append(TD_START);
            sb.append(IMG_SVG_START);
            if("N".equals(sch.getActive())){
                sb.append(ICON_LASTRUN_INACTIVE_TITLE);
                sb.append(IMG_SVG_1_END);
                sb.append(BatchAppHandlerTagSvgIcons.INACTIVE_ICON);
            }else{
                if(LASTRUN_STATUS_CD_GOOD.equals(sch.getLastRunStatusCd())){
                    sb.append(ICON_LASTRUN_GOOD_TITLE);
                    sb.append(IMG_SVG_1_END);
                    sb.append(BatchAppHandlerTagSvgIcons.SUCCESSFUL_ICON);
                }else if(LASTRUN_STATUS_CD_INPROGRESS.equals(sch.getLastRunStatusCd())){
                    sb.append(ICON_LASTRUN_INPROGRESS_TITLE);
                    sb.append(IMG_SVG_1_END);
                    sb.append(BatchAppHandlerTagSvgIcons.HOURGLASS_ICON);
                }else if(LASTRUN_STATUS_CD_OFFSCHEDULE.equals(sch.getLastRunStatusCd())){
                    sb.append(ICON_LASTRUN_OFFSCHEDULE_TITLE);
                    sb.append(IMG_SVG_1_END);
                    sb.append(BatchAppHandlerTagSvgIcons.OFFSCHEDULE_ICON);
                }else if(LASTRUN_STATUS_CD_BAD.equals(sch.getLastRunStatusCd())){
                    sb.append(ICON_LASTRUN_BAD_TITLE);
                    sb.append(IMG_SVG_1_END);
                    sb.append(BatchAppHandlerTagSvgIcons.UNSUCCESSFUL_ICON);
                }else if(LASTRUN_STATUS_CD_PENDING.equals(sch.getLastRunStatusCd())){
                    sb.append(ICON_LASTRUN_PENDING_TITLE);
                    sb.append(IMG_SVG_1_END);
                    sb.append(BatchAppHandlerTagSvgIcons.PENDING_ICON);
                }else{
                    sb.append(ICON_LASTRUN_UNKNOWN_TITLE);
                    sb.append(IMG_SVG_1_END);
                    sb.append(BatchAppHandlerTagSvgIcons.UNKNOWN_ICON);
                }// end if
            }// end if/else
            sb.append(IMG_SVG_2_END);
            sb.append(TD_END);
            // Start Date
            sb.append(TD_START);
            sb.append(ShamenClientUtil.getSqlDateAsString(sch.getScheduleStartDt()));
            sb.append(TD_END);
            // Start Time
            sb.append(TD_START);
            sb.append(sch.getStartTime().toString());
            sb.append(TD_END);
            // Frequency
            sb.append(TD_START);
            sb.append(sch.getFrequencyCdDesc());
            sb.append(TD_END);
            // Schedule Details
            sb.append(TD_START);
            sb.append(sch.getScheduleDetails());
            sb.append(TD_END);
            sb.append(TR_END);
        }// end while
        writer.append(sb.toString());
        if(log.isDebugEnabled()){
            log.debug("[" + tableId + "] writeRunStatusTable end");
        }// end if
    }// end writeScheduleTable

    /**
     * This method is used to write the HTML table body.
     *
     * @throws Exception
     *         An exception if the method logic fails.
     */
    private void writeTableSchedulelBody() throws Exception {
        if(log.isDebugEnabled()){
            log.debug("[" + tableId + "] writeTableDetailBody called for table [" + tableId + "]");
        }// end if
        if(!ShamenClientUtil.isEmpty(rowList)){
            writeScheduleTable();
        }else{
            writer.append(NO_SCHEDULE_AVAILABLE);
        }// end if/else
        if(log.isDebugEnabled()){
            log.debug("[" + tableId + "] writeTableDetailBody end");
        }// end if
    }// end writeTableSchedulelBody

    /**
     * This method is used to write the HTML table body closer.
     */
    private void writeTableBodyCloser() {
        if(log.isDebugEnabled()){
            log.debug("[" + tableId + "] writeTableBodyCloser called for table [" + tableId + "]");
        }// end if
        writer.append("</tbody>");
        if(log.isDebugEnabled()){
            log.debug("[" + tableId + "] writeTableBodyCloser end");
        }// end if
    }// end writeTableBodyCloser

    /**
     * This method is used to write the HTML table closer.
     */
    private void writeTableCloser() {
        if(log.isDebugEnabled()){
            log.debug("[" + tableId + "] writeTableCloser called for table [" + tableId + "]");
        }// end if
        writer.append(TABLE_END);
        if(log.isDebugEnabled()){
            log.debug("[" + tableId + "] writeTableCloser end");
        }// end if
    }// end writeTableCloser

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

}// end class
