package gov.doc.isu.shamen.jpa.entity;

import static gov.doc.isu.shamen.ejb.util.EJBConstants.DEFAULT_NEW_LINE;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.AdditionalCriteria;

import gov.doc.isu.shamen.ejb.util.ShamenEJBUtil;

/**
 * The persistent class for the Schedules database table.
 */
@Entity
@Table(name = "Schedules", schema = "Trans")
@AdditionalCriteria("this.common.deleteIndicator = 'N'")
public class ScheduleEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Schedule_Ref_Id", unique = true)
    private Long scheduleRefId;

    @Column(name = "Active_Ind")
    private String activeInd;

    @Column(name = "Day_No")
    private String dayNo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Frequency_Cd", columnDefinition = "IS NOT NULL")
    private FrequencyCodeEntity frequency;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Frequency_Type_Cd")
    private FrequencyTypeCodeEntity frequencyType;

    @Column(name = "Recur_No")
    private Long recurNo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Repeat_Cd")
    private RepeatCodeEntity repeat;

    @Column(name = "Schedule_Start_Dt")
    private Date scheduleStartDt;

    @Column(name = "Start_Time")
    private Time startTime;

    @Column(name = "Week_No")
    private String weekNumber;

    @Column(name = "Weekdays")
    private String weekdays;

    // bi-directional many-to-one association to BatchAppEntity
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "Batch_App_Ref_Id")
    private BatchAppEntity batchApp;

    private transient RunStatusEntity lastRunStatus;

    @Embedded
    private CommonEntity common;

    /**
     *
     */
    public ScheduleEntity() {}// end constructor

    /**
     * @param scheduleRefId
     *        scheduleRefId
     */
    public ScheduleEntity(Long scheduleRefId) {
        super();
        this.scheduleRefId = scheduleRefId;
    }// end constructor

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
     * @return the frequency
     */
    public FrequencyCodeEntity getFrequency() {
        return frequency;
    }// end getFrequency

    /**
     * @param frequency
     *        the frequency to set
     */
    public void setFrequency(FrequencyCodeEntity frequency) {
        this.frequency = frequency;
    }// end setFrequency

    /**
     * @return the frequencyType
     */
    public FrequencyTypeCodeEntity getFrequencyType() {
        return frequencyType;
    }// end getFrequencyType

    /**
     * @param frequencyType
     *        the frequencyType to set
     */
    public void setFrequencyType(FrequencyTypeCodeEntity frequencyType) {
        this.frequencyType = frequencyType;
    }// end setFrequencyType

    /**
     * @return the repeat
     */
    public RepeatCodeEntity getRepeat() {
        return repeat;
    }// end getRepeat

    /**
     * @param repeat
     *        the repeat to set
     */
    public void setRepeat(RepeatCodeEntity repeat) {
        this.repeat = repeat;
    }// end setRepeat

    /**
     * @return the scheduleRefId
     */
    public Long getScheduleRefId() {
        return scheduleRefId;
    }// end getScheduleRefId

    /**
     * @param scheduleRefId
     *        the scheduleRefId to set
     */
    public void setScheduleRefId(Long scheduleRefId) {
        this.scheduleRefId = scheduleRefId;
    }// end setScheduleRefId

    /**
     * @return the activeInd
     */
    public String getActiveInd() {
        return activeInd;
    }// end getActiveInd

    /**
     * @param activeInd
     *        the activeInd to set
     */
    public void setActiveInd(String activeInd) {
        this.activeInd = activeInd;
    }// end setActiveInd

    /**
     * @return the dayNo
     */
    public String getDayNo() {
        return dayNo;
    }// end getDayNo

    /**
     * @param dayNo
     *        the dayNo to set
     */
    public void setDayNo(String dayNo) {
        this.dayNo = dayNo;
    }// end setDayNo

    /**
     * @return the recurNo
     */
    public Long getRecurNo() {
        return recurNo;
    }// end getRecurNo

    /**
     * @param recurNo
     *        the recurNo to set
     */
    public void setRecurNo(Long recurNo) {
        this.recurNo = recurNo;
    }// end setRecurNo

    /**
     * @return the scheduleStartDt
     */
    public Date getScheduleStartDt() {
        return scheduleStartDt;
    }// end getScheduleStartDt

    /**
     * @param scheduleStartDt
     *        the scheduleStartDt to set
     */
    public void setScheduleStartDt(Date scheduleStartDt) {
        this.scheduleStartDt = scheduleStartDt;
    }// end setScheduleStartDt

    /**
     * @return the startTime
     */
    public Time getStartTime() {
        return startTime;
    }// end getStartTime

    /**
     * @param startTime
     *        the startTime to set
     */
    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }// end setStartTime

    /**
     * @return the weekNumber
     */
    public String getWeekNumber() {
        return weekNumber;
    }// end getWeekNumber

    /**
     * @param weekNumber
     *        the weekNumber to set
     */
    public void setWeekNumber(String weekNumber) {
        this.weekNumber = weekNumber;
    }// end setWeekNumber

    /**
     * @return the weekdays
     */
    public String getWeekdays() {
        return weekdays;
    }// end getWeekdays

    /**
     * @param weekdays
     *        the weekdays to set
     */
    public void setWeekdays(String weekdays) {
        this.weekdays = weekdays;
    }// end setWeekdays

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
     * @return the lastRunStatus
     */
    public RunStatusEntity getLastRunStatus() {
        return lastRunStatus;
    }

    /**
     * @param lastRunStatus
     *        the lastRunStatus to set
     */
    public void setLastRunStatus(RunStatusEntity lastRunStatus) {
        this.lastRunStatus = lastRunStatus;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer builder = new StringBuffer();
        builder.append("ScheduleEntity [scheduleRefId=");
        builder.append(scheduleRefId).append(DEFAULT_NEW_LINE);
        builder.append(", activeInd=");
        builder.append(activeInd).append(DEFAULT_NEW_LINE);
        builder.append(", dayNo=");
        builder.append(dayNo).append(DEFAULT_NEW_LINE);
        builder.append(", frequency=");
        builder.append(frequency).append(DEFAULT_NEW_LINE);
        builder.append(", frequencyType=");
        builder.append(frequencyType).append(DEFAULT_NEW_LINE);
        builder.append(", recurNo=");
        builder.append(recurNo).append(DEFAULT_NEW_LINE);
        builder.append(", repeat=");
        builder.append(repeat).append(DEFAULT_NEW_LINE);
        builder.append(", scheduleStartDt=");
        builder.append(scheduleStartDt).append(DEFAULT_NEW_LINE);
        builder.append(", startTime=");
        builder.append(startTime).append(DEFAULT_NEW_LINE);
        builder.append(", weekNumber=");
        builder.append(weekNumber).append(DEFAULT_NEW_LINE);
        builder.append(", weekdays=");
        builder.append(weekdays).append(DEFAULT_NEW_LINE);
        builder.append(", common=");
        builder.append(common);
        builder.append("]");
        return builder.toString();
    }// end toString

    /**
     * This method returns the days in a String array for easy calculation purposes.
     *
     * @return String[]
     */
    public String[] getDayNumberArray() {
        return ShamenEJBUtil.toArrayFromString(getDayNo(), null);
    }// end getDayNumberArray

    /**
     * This method returns the weekdays in a String array for easy calculation purposes.
     *
     * @return String[]
     */
    public String[] getWeekDaysArray() {
        return ShamenEJBUtil.toArrayFromString(getWeekdays(), null);
    }// end getWeekDaysArray

    /**
     * This method returns the week numbers in a String array for easy calculation purposes.
     *
     * @return String[]
     */
    public String[] getWeekNumberArray() {
        return ShamenEJBUtil.toArrayFromString(getWeekNumber(), null);
    }// end getWeekNumberArray
}// end class
