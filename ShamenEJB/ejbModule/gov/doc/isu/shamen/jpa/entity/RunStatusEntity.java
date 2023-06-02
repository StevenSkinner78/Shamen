package gov.doc.isu.shamen.jpa.entity;

import static gov.doc.isu.shamen.ejb.util.EJBConstants.DEFAULT_NEW_LINE;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.AdditionalCriteria;
import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.config.CacheIsolationType;

/**
 * The persistent class for the Run_Status database table.
 */
@Entity
@Cache(isolation = CacheIsolationType.ISOLATED)
@Table(name = "Run_Status", schema = "Trans")
@NamedQueries({@NamedQuery(name = "RunStatusEntity.FIND_BY_EXAMPLE", query = "SELECT r FROM RunStatusEntity r where r.batchApp.batchAppRefId = :batchAppRefId and r.schedule.scheduleRefId = :scheduleRefId and r.startTime = :startTime and r.stopTime = :stopTime and r.status.statusCd = :statusCd and r.result.resultCd = :resultCd and r.description = :description and r.common.createUserRefId = :createUserRefId and r.common.createTime = :createTime and r.common.deleteIndicator = 'N'")})
@AdditionalCriteria("this.common.deleteIndicator = 'N'")
public class RunStatusEntity implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Run_Status_Ref_Id", unique = true)
    private Long runStatusRefId;

    @Column(name = "Main_Batch_App_Ref_Id")
    private Long mainBatchAppRefId;

    @Column(name = "Description")
    private String description;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Result_Cd")
    private ResultCodeEntity result;

    @Column(name = "Result_Detail")
    private String resultDetail;

    @Column(name = "Start_Ts")
    private Timestamp startTime;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Status_Cd")
    private StatusCodeEntity status;

    @Column(name = "Stop_Ts")
    private Timestamp stopTime;

    @Column(name = "Run_Nbr")
    private Long runNumber;

    // bi-directional many-to-one association to BatchAppEntity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Batch_App_Ref_Id", columnDefinition = "IS NOT NULL")
    private BatchAppEntity batchApp;

    // bi-directional one-to-one association to ScheduleEntity
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Schedule_Ref_Id", columnDefinition = "IS NOT NULL")
    private ScheduleEntity schedule;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Create_User_Ref_Id", insertable = false, updatable = false, columnDefinition = "IS NOT NULL")
    private AuthorizedUserEntity createdBy;

    @Embedded
    private CommonEntity common;

    private transient boolean fromSchedule;
    private transient String user;
    private transient List<RunStatusEntity> collectionMembers = new ArrayList<RunStatusEntity>();

    /**
     *
     */
    public RunStatusEntity() {}// end constructor

    /**
     * @return the runStatusRefId
     */
    public Long getRunStatusRefId() {
        return runStatusRefId;
    }// end getRunStatusRefId

    /**
     * @param runStatusRefId
     *        the runStatusRefId to set
     */
    public void setRunStatusRefId(Long runStatusRefId) {
        this.runStatusRefId = runStatusRefId;
    }// end setRunStatusRefId

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }// end getDescription

    /**
     * @param description
     *        the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }// end setDescription

    /**
     * @return the resultDetail
     */
    public String getResultDetail() {
        return resultDetail;
    }// end getResultDetail

    /**
     * @param resultDetail
     *        the resultDetail to set
     */
    public void setResultDetail(String resultDetail) {
        this.resultDetail = resultDetail;
    }// end setResultDetail

    /**
     * @return the startTime
     */
    public Timestamp getStartTime() {
        return startTime;
    }// end getStartTime

    /**
     * @param startTime
     *        the startTime to set
     */
    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }// end setStartTime

    /**
     * @return the stopTime
     */
    public Timestamp getStopTime() {
        return stopTime;
    }// end getStopTime

    /**
     * @param stopTime
     *        the stopTime to set
     */
    public void setStopTime(Timestamp stopTime) {
        this.stopTime = stopTime;
    }// end setStopTime

    /**
     * @return the batchApp
     */
    public BatchAppEntity getBatchApp() {
        return batchApp;
    }// end getBatchApp

    /**
     * @param batchApp
     *        the batchApp to set
     */
    public void setBatchApp(BatchAppEntity batchApp) {
        this.batchApp = batchApp;
    }// end setBatchApp

    /**
     * @return the schedule
     */
    public ScheduleEntity getSchedule() {
        return schedule;
    }// end getSchedule

    /**
     * @param schedule
     *        the schedule to set
     */
    public void setSchedule(ScheduleEntity schedule) {
        this.schedule = schedule;
    }// end setSchedule

    /**
     * @return the result
     */
    public ResultCodeEntity getResult() {
        return result;
    }// end getResult

    /**
     * @param result
     *        the result to set
     */
    public void setResult(ResultCodeEntity result) {
        this.result = result;
    }// end setResult

    /**
     * @return the status
     */
    public StatusCodeEntity getStatus() {
        return status;
    }// end getStatus

    /**
     * @param status
     *        the status to set
     */
    public void setStatus(StatusCodeEntity status) {
        this.status = status;
    }// end setStatus

    /**
     * @return the createdBy
     */
    public AuthorizedUserEntity getCreatedBy() {
        return createdBy;
    }// end getCreatedBy

    /**
     * @param createdBy
     *        the createdBy to set
     */
    public void setCreatedBy(AuthorizedUserEntity createdBy) {
        this.createdBy = createdBy;
    }// end setCreatedBy

    /**
     * @return the common
     */
    public CommonEntity getCommon() {
        return common;
    }// end getCommon

    /**
     * @param common
     *        the common to set
     */
    public void setCommon(CommonEntity common) {
        this.common = common;
    }// end setCommon

    /**
     * @return the fromSchedule
     */
    public boolean isFromSchedule() {
        return fromSchedule;
    }// end isFromSchedule

    /**
     * @param fromSchedule
     *        the fromSchedule to set
     */
    public void setFromSchedule(boolean fromSchedule) {
        this.fromSchedule = fromSchedule;
    }// end setFromSchedule

    /**
     * This method sets the transient values after entity is loaded
     */
    @PostLoad
    public void setTransientValues() {
        setFromSchedule(schedule == null ? false : true);
    }// end setTransientValues

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer builder = new StringBuffer();
        builder.append("RunStatusEntity [runStatusRefId=");
        builder.append(runStatusRefId).append(DEFAULT_NEW_LINE);
        builder.append(", mainBatchAppRefId=");
        builder.append(mainBatchAppRefId).append(DEFAULT_NEW_LINE);
        builder.append(", description=");
        builder.append(description).append(DEFAULT_NEW_LINE);
        builder.append(", result=");
        builder.append(result).append(DEFAULT_NEW_LINE);
        // builder.append(", resultDetail=");
        // builder.append(resultDetail).append(DEFAULT_NEW_LINE);
        builder.append(", startTime=");
        builder.append(startTime).append(DEFAULT_NEW_LINE);
        builder.append(", status=");
        builder.append(status).append(DEFAULT_NEW_LINE);
        builder.append(", stopTime=");
        builder.append(stopTime).append(DEFAULT_NEW_LINE);
        builder.append(", batchAppRefId=");
        builder.append(batchApp == null ? "null" : batchApp.getBatchAppRefId()).append(DEFAULT_NEW_LINE);
        builder.append(", scheduleRefId=");
        builder.append(schedule == null ? "null" : schedule.getScheduleRefId()).append(DEFAULT_NEW_LINE);
        builder.append(", fromSchedule=");
        builder.append(fromSchedule).append(DEFAULT_NEW_LINE);
        builder.append(", common=");
        builder.append(common);
        builder.append("]");
        return builder.toString();
    }// end toString

    /**
     * @return the runNumber
     */
    public Long getRunNumber() {
        return runNumber;
    }// end getRunNumber

    /**
     * @param runNumber
     *        the runNumber to set
     */
    public void setRunNumber(Long runNumber) {
        this.runNumber = runNumber;
    }// end setRunNumber

    /**
     * @return the mainBatchAppRefId
     */
    public Long getMainBatchAppRefId() {
        return mainBatchAppRefId;
    }// end getMainBatchAppRefId

    /**
     * @param mainBatchAppRefId
     *        the mainBatchAppRefId to set
     */
    public void setMainBatchAppRefId(Long mainBatchAppRefId) {
        this.mainBatchAppRefId = mainBatchAppRefId;
    }// end setMainBatchAppRefId

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object otherObject) {
        boolean equal = true;
        // a quick test to see if the objects are identical
        if(this == otherObject){
            return true;
        }// end if
         // must return false if the explicit parameter is null
        if(otherObject == null){
            return false;
        }// end if
         // if the classes don't match, they can't be equal
        if(getClass() != otherObject.getClass()){
            return false;
        }// end if
         // now we know otherObject is a non-null RunStatusEntity
        RunStatusEntity other = (RunStatusEntity) otherObject;
        if(!(this.getDescription() != null ? this.getDescription() : "").equals((other.getDescription() != null ? other.getDescription() : ""))){
            equal = false;
        }// end if
        if(equal){
            if(!(this.getMainBatchAppRefId() != null ? this.getMainBatchAppRefId() : Long.valueOf(0)).equals((other.getMainBatchAppRefId() != null ? other.getMainBatchAppRefId() : Long.valueOf(0)))){
                equal = false;
            }// end if
        }// end if
        if(equal){
            if(!(this.getResult() != null ? this.getResult() : "").equals((other.getResult() != null ? other.getResult() : ""))){
                equal = false;
            }// end if
        }// end if
        if(equal){
            if(!(this.getResultDetail() != null ? this.getResultDetail() : "").equals((other.getResultDetail() != null ? other.getResultDetail() : ""))){
                equal = false;
            }// end if
        }// end if
        if(equal){
            if(!(this.getRunNumber() != null ? this.getRunNumber() : Long.valueOf(0)).equals((other.getRunNumber() != null ? other.getRunNumber() : Long.valueOf(0)))){
                equal = false;
            }// end if
        }// end if
        if(equal){
            if(!(this.getStartTime() != null ? this.getStartTime() : "").equals((other.getStartTime() != null ? other.getStartTime() : ""))){
                equal = false;
            }// end if
        }// end if
        if(equal){
            if(!(this.getStatus() != null ? this.getStatus() : "").equals((other.getStatus() != null ? other.getStatus() : ""))){
                equal = false;
            }// end if
        }// end if
        if(equal){
            if(!(this.getStopTime() != null ? this.getStopTime() : "").equals((other.getStopTime() != null ? other.getStopTime() : ""))){
                equal = false;
            }// end if
        }// end if
        return equal;
    }// end equals

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }// end hashCode

    /**
     * @return the collectionMembers
     */
    public List<RunStatusEntity> getCollectionMembers() {
        return collectionMembers;
    }// end getCollectionMembers

    /**
     * @param collectionMembers
     *        the collectionMembers to set
     */
    public void setCollectionMembers(List<RunStatusEntity> collectionMembers) {
        this.collectionMembers = collectionMembers;
    }// end setCollectionMembers

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }// end getUser

    /**
     * @param user
     *        the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }// end setUser

    /*
     * (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }// end clone

}// end class
