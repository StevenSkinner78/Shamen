/**
 * @(#)JmsRunStatus.java 1.0.0 Copyright (c) 2018 Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit All Rights Reserved. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: -Redistribution of source code must retain the above copyright notice, this list of conditions and the following disclaimer. -Redistribution in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. Neither the name of Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit or the names of contributors may be used to endorse or promote products derived from this software without specific prior written permission. This software is provided "AS IS," without a warranty of any kind. ALL EXPRESS OR IMPLIED CONDITIONS,
 *                       REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. You acknowledge that this
 *                       software is not designed, licensed or intended for use in the design, construction, operation or maintenance of any nuclear facility.
 */
package gov.doc.isu.shamen.jms.models;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Model object to hold the run status record for a batch application.
 * 
 * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
 */
public class JmsRunStatus extends JmsBaseObject {
    /**
     * 
     */
    private static final long serialVersionUID = -1748357596272106859L;
    private Long runStatusRefId;
    private Long scheduleRefId;
    private Long batchAppRefId;
    private Long mainBatchAppRefId;// This is the collection's refId
    private Timestamp startTs;
    private Timestamp stopTs;
    private String statusCd;
    private String statusDesc;
    private String resultCd;
    private String resultDesc;
    private String resultDetail;
    private String description;
    private Long runNumber;
    private List<JmsRunStatus> collectionMembers = new ArrayList<JmsRunStatus>();
    private JmsBatchApp batchApp;
    // Properties to be used for statuses
    public static final String STATUS_STARTED = "STD";
    public static final String STATUS_STARTING = "STR";
    public static final String STATUS_FAILURE_TO_LAUNCH = "FTL";
    public static final String STATUS_DONE = "DON";
    public static final String STATUS_PROCESSING = "PRO";
    // Properties to be used for results
    public static final String RESULTS_SUCCESSFUL = "SUC";
    public static final String RESULTS_UNKNOWN = "UNK";
    public static final String RESULTS_UNSUCCESSFUL = "UNS";
    public static final String RESULTS_PENDING = "BLA";

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("runStatusRefId = ").append(runStatusRefId).append(DEFAULT_NEW_LINE);
        sb.append("runNumber = ").append(String.valueOf(runNumber)).append(DEFAULT_NEW_LINE);
        sb.append("scheduleRefId = ").append(scheduleRefId).append(DEFAULT_NEW_LINE);
        sb.append("batchAppRefId = ").append(batchAppRefId).append(DEFAULT_NEW_LINE);
        sb.append("startTs = ").append(startTs).append(DEFAULT_NEW_LINE);
        sb.append("stopTs = ").append(stopTs).append(DEFAULT_NEW_LINE);
        sb.append("statusDesc = ").append(statusDesc).append(DEFAULT_NEW_LINE);
        sb.append("resultDesc = ").append(resultDesc).append(DEFAULT_NEW_LINE);
        sb.append("resultDetail = ").append(resultDetail).append(DEFAULT_NEW_LINE);
        sb.append("description = ").append(description).append(DEFAULT_NEW_LINE);
        sb.append("batchApp = ").append(batchApp);
        sb.append(super.toString());
        return sb.toString();
    }// end toString

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @return the scheduleRefId
     */
    public Long getScheduleRefId() {
        return scheduleRefId;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param scheduleRefId
     *        the scheduleRefId to set
     */
    public void setScheduleRefId(Long scheduleRefId) {
        this.scheduleRefId = scheduleRefId;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @return the batchAppRefId
     */
    public Long getBatchAppRefId() {
        return batchAppRefId;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param batchAppRefId
     *        the batchAppRefId to set
     */
    public void setBatchAppRefId(Long batchAppRefId) {
        this.batchAppRefId = batchAppRefId;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @return the runStatusRefId
     */
    public Long getRunStatusRefId() {
        return runStatusRefId;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param runStatusRefId
     *        the runStatusRefId to set
     */
    public void setRunStatusRefId(Long runStatusRefId) {
        this.runStatusRefId = runStatusRefId;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @return the startTs
     */
    public Timestamp getStartTs() {
        return startTs;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param startTs
     *        the startTs to set
     */
    public void setStartTs(Timestamp startTs) {
        this.startTs = startTs;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @return the stopTs
     */
    public Timestamp getStopTs() {
        return stopTs;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param stopTs
     *        the stopTs to set
     */
    public void setStopTs(Timestamp stopTs) {
        this.stopTs = stopTs;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @return the statusCd
     */
    public String getStatusCd() {
        return statusCd;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param statusCd
     *        the statusCd to set
     */
    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @return the resultCd
     */
    public String getResultCd() {
        return resultCd;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param resultCd
     *        the resultCd to set
     */
    public void setResultCd(String resultCd) {
        this.resultCd = resultCd;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param description
     *        the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @return the resultDetail
     */
    public String getResultDetail() {
        return resultDetail;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param resultDetail
     *        the resultDetail to set
     */
    public void setResultDetail(String resultDetail) {
        this.resultDetail = resultDetail;
    }

    /**
     * @return the runNumber
     */
    public Long getRunNumber() {
        return runNumber;
    }

    /**
     * @param runNumber
     *        the runNumber to set
     */
    public void setRunNumber(Long runNumber) {
        this.runNumber = runNumber;
    }

    /**
     * @return the mainBatchAppRefId
     */
    public Long getMainBatchAppRefId() {
        return mainBatchAppRefId;
    }

    /**
     * @param mainBatchAppRefId
     *        the mainBatchAppRefId to set
     */
    public void setMainBatchAppRefId(Long mainBatchAppRefId) {
        this.mainBatchAppRefId = mainBatchAppRefId;
    }

    /**
     * Returns the time difference between two dates in Hours : Minutes : Seconds format.
     * 
     * @param start
     *        The start date
     * @param stop
     *        The stop date
     * @return String
     */
    public String getTimeDifference() {
        StringBuffer result = new StringBuffer();
        if(startTs != null && stopTs != null){

            long hours, minutes, seconds;
            seconds = (stopTs.getTime() - startTs.getTime()) / 1000;

            hours = seconds / 3600;
            result.append(hours < 10 ? "0" + hours : hours);
            result.append(":");
            seconds = seconds - (hours * 3600);

            minutes = seconds / 60;
            result.append(minutes < 10 ? "0" + minutes : minutes);
            result.append(":");

            seconds = (seconds - (minutes * 60));
            result.append(seconds < 10 ? "0" + seconds : seconds);

        }// end if
        return result.toString();
    }// end getTimeDifference

    /**
     * @return the statusDesc
     */
    public String getStatusDesc() {
        return statusDesc;
    }

    /**
     * @param statusDesc
     *        the statusDesc to set
     */
    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    /**
     * @return the resultDesc
     */
    public String getResultDesc() {
        return resultDesc;
    }

    /**
     * @param resultDesc
     *        the resultDesc to set
     */
    public void setResultDesc(String resultDesc) {
        this.resultDesc = resultDesc;
    }

    /**
     * @return the collectionMembers
     */
    public List<JmsRunStatus> getCollectionMembers() {
        return collectionMembers;
    }

    /**
     * @param collectionMembers
     *        the collectionMembers to set
     */
    public void setCollectionMembers(List<JmsRunStatus> collectionMembers) {
        this.collectionMembers = collectionMembers;
    }

    /**
     * @return the batchApp
     */
    public JmsBatchApp getBatchApp() {
        return batchApp;
    }

    /**
     * @param batchApp
     *        the batchApp to set
     */
    public void setBatchApp(JmsBatchApp batchApp) {
        this.batchApp = batchApp;
    }
}
