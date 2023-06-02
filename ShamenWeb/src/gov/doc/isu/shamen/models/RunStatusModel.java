/**
 * @author <strong>Steven Skinner</strong> JCCC, Sep 10, 2015
 */
package gov.doc.isu.shamen.models;

import static gov.doc.isu.dwarf.resources.Constants.NEW_LINE;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import gov.doc.isu.dwarf.model.CommonModel;

/**
 * Model object to hold the run status record for a particular batch application schedule record.
 *
 * @author <strong>Steven Skinner</strong> JCCC, Sep 10, 2015
 */
public class RunStatusModel extends CommonModel implements Cloneable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Long runStatusRefId;
    private Long runNumber;
    private BatchAppModel batchApp;
    private Long scheduleRefId;
    private Long mainBatchAppRefId;
    private String start;
    private Timestamp startTs;
    private String stop;
    private String statusCd;
    private String statusDesc;
    private String resultCd;
    private String resultDesc;
    private String resultDetail;
    private String description;
    private boolean fromScheduleInd;
    private String userName;
    private String duration;
    private String durationInMinutes;
    private List<RunStatusModel> collectionMembers = new ArrayList<RunStatusModel>();

    /**
     * Default Constructor
     */
    public RunStatusModel() {
        super();
    }

    /**
     * Constructor
     * 
     * @param resultCd
     *        the result code to initialize with
     */
    public RunStatusModel(String resultCd) {
        super();
        this.resultCd = resultCd;
    }

    /**
     * @return the runStatusRefId
     */
    public Long getRunStatusRefId() {
        return runStatusRefId;
    }

    /**
     * @param runStatusRefId
     *        the runStatusRefId to set
     */
    public void setRunStatusRefId(Long runStatusRefId) {
        this.runStatusRefId = runStatusRefId;
    }

    /**
     * @return the batchApp
     */
    public BatchAppModel getBatchApp() {
        return batchApp;
    }

    /**
     * @param batchApp
     *        the batchApp to set
     */
    public void setBatchApp(BatchAppModel batchApp) {
        this.batchApp = batchApp;
    }

    /**
     * @return the scheduleRefId
     */
    public Long getScheduleRefId() {
        return scheduleRefId;
    }

    /**
     * @param scheduleRefId
     *        the scheduleRefId to set
     */
    public void setScheduleRefId(Long scheduleRefId) {
        this.scheduleRefId = scheduleRefId;
    }

    /**
     * @return the start
     */
    public String getStart() {
        return start;
    }

    /**
     * @param start
     *        the start to set
     */
    public void setStart(String start) {
        this.start = start;
    }

    /**
     * @return the stop
     */
    public String getStop() {
        return stop;
    }

    /**
     * @param stop
     *        the stop to set
     */
    public void setStop(String stop) {
        this.stop = stop;
    }

    /**
     * @return the statusCd
     */
    public String getStatusCd() {
        return statusCd;
    }

    /**
     * @param statusCd
     *        the statusCd to set
     */
    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }

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
     * @return the resultCd
     */
    public String getResultCd() {
        return resultCd;
    }

    /**
     * @param resultCd
     *        the resultCd to set
     */
    public void setResultCd(String resultCd) {
        this.resultCd = resultCd;
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
     * @return the resultDetail
     */
    public String getResultDetail() {
        return resultDetail;
    }

    /**
     * @param resultDetail
     *        the resultDetail to set
     */
    public void setResultDetail(String resultDetail) {
        this.resultDetail = resultDetail;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     *        the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the fromScheduleInd
     */
    public boolean isFromScheduleInd() {
        return fromScheduleInd;
    }

    /**
     * @param fromScheduleInd
     *        the fromScheduleInd to set
     */
    public void setFromScheduleInd(boolean fromScheduleInd) {
        this.fromScheduleInd = fromScheduleInd;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName
     *        the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the duration
     */
    public String getDuration() {
        return duration;
    }

    /**
     * @param duration
     *        the duration to set
     */
    public void setDuration(String duration) {
        this.duration = duration;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer builder = new StringBuffer();
        builder.append("RunStatusModel [runStatusRefId=");
        builder.append(runStatusRefId).append(NEW_LINE);
        builder.append("runNumber=");
        builder.append(runNumber).append(NEW_LINE);
        builder.append("batchApp=");
        builder.append(batchApp == null ? "null" : batchApp.getName()).append(NEW_LINE);
        builder.append("scheduleRefId=");
        builder.append(scheduleRefId).append(NEW_LINE);
        builder.append("mainBatchAppRefId=");
        builder.append(mainBatchAppRefId).append(NEW_LINE);
        builder.append("start=");
        builder.append(start).append(NEW_LINE);
        builder.append("stop=");
        builder.append(stop).append(NEW_LINE);
        builder.append("statusCd=");
        builder.append(statusCd).append(NEW_LINE);
        builder.append("statusDesc=");
        builder.append(statusDesc).append(NEW_LINE);
        builder.append("resultCd=");
        builder.append(resultCd).append(NEW_LINE);
        builder.append("resultDesc=");
        builder.append(resultDesc).append(NEW_LINE);
        builder.append("resultDetail=");
        builder.append(resultDetail).append(NEW_LINE);
        builder.append("description=");
        builder.append(description).append(NEW_LINE);
        builder.append(super.toString());
        builder.append("]");
        return builder.toString();
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
     * @return the collectionMembers
     */
    public List<RunStatusModel> getCollectionMembers() {
        return collectionMembers;
    }

    /**
     * @param collectionMembers
     *        the collectionMembers to set
     */
    public void setCollectionMembers(List<RunStatusModel> collectionMembers) {
        this.collectionMembers = collectionMembers;
    }

    /**
     * @return the startTs
     */
    public Timestamp getStartTs() {
        return startTs;
    }

    /**
     * @param startTs
     *        the startTs to set
     */
    public void setStartTs(Timestamp startTs) {
        this.startTs = startTs;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * @return the durationInMinutes
     */
    public String getDurationInMinutes() {
        return durationInMinutes;
    }

    /**
     * @param durationInMinutes
     *        the durationInMinutes to set
     */
    public void setDurationInMinutes(String durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }

}
