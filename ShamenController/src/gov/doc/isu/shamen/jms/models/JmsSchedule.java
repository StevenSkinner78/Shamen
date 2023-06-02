/**
 * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
 */
package gov.doc.isu.shamen.jms.models;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;

/**
 * Model object to hold the schedule record for a particular batch application.
 * 
 * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
 */
public class JmsSchedule extends JmsBaseObject {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Long scheduleRefId;
    private Long batchAppRefId;
    private Time startTime;
    private Date scheduleStartDt;
    private String frequencyCd;
    private String frequencyCdDesc;
    private String frequencyTypeCd;
    private String frequencyTypeCdDesc;
    private String repeatCd;
    private String repeatCdDesc;
    private String recurNo;
    private String weekdays;
    private String dayNo;
    private String weekNo;
    private String active;
    private String lastRunStatusCd;
    private String scheduleDetails;
    // Properties to be used for run frequencyCd
    public static final String FREQUENCY_ONE_TIME = "ONT";
    public static final String FREQUENCY_DAILY = "DLY";
    public static final String FREQUENCY_MONTHLY = "MTY";
    public static final String FREQUENCY_WEEKLY = "WKY";
    // Properties to be used for run frequencyTypeCd
    public static final String FREQUENCY_TYPE_WEEK_WITH_DAY = "WWD";
    public static final String FREQUENCY_TYPE_DAY_OF_MONTH = "DOM";
    public static final String FREQUENCY_TYPE_MONTH_WITH_WEEKDAY = "MWD";
    // Properties to be used for run repeat Cd
    public static final String REPEAT_TYPE_1_HOUR = "HR1";
    public static final String REPEAT_TYPE_2_HOUR = "HR2";
    public static final String REPEAT_TYPE_3_HOUR = "HR3";
    public static final String REPEAT_TYPE_4_HOUR = "HR4";
    public static final String REPEAT_TYPE_30_MINUTE = "M30";
    public static final String REPEAT_TYPE_15_MINUTE = "M15";
    public static final String REPEAT_TYPE_10_MINUTE = "M10";
    public static final String REPEAT_TYPE_05_MINUTE = "M05";
    public static final String REPEAT_TYPE_01_MINUTE = "M01";

    

    /**
     * Get the week numbers as a string array. Each element corresponds to a different week of the month. 1-5 (5=last)
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 21, 2015
     * @return
     */
    public ArrayList<Integer> getWeekNumbersAsArrayList() {
        String[] weekNumberArray = null;
        ArrayList<Integer> weekNumberList = new ArrayList<Integer>();
        if(weekNo != null){
            weekNumberArray = weekNo.split(",");
            for(int i = 0, j = weekNumberArray.length;i < j;i++){
                weekNumberList.add(Integer.valueOf(weekNumberArray[i]));
            }// end for
        }// end if
        return weekNumberList;
    }// end getWeekNumbersAsArrayList

    /**
     * Get the weekdays as a string array. Each element corresponds to a different day of the week. 1-7
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 21, 2015
     * @return
     */
    public ArrayList<Integer> getWeekdaysAsArrayList() {
        String[] weekdayArray = null;
        ArrayList<Integer> weekDayList = new ArrayList<Integer>();
        if(weekdays != null){
            weekdayArray = weekdays.split(",");
            for(int i = 0, j = weekdayArray.length;i < j;i++){
                weekDayList.add(Integer.valueOf(weekdayArray[i]));
            }// end for
        }// end if
        return weekDayList;
    }// end getWeekdaysAsArrayList

    /**
     * Get the month days as a string array. Each element corresponds to a different day of the month. 1-32, (32 = last)
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 21, 2015
     * @return
     */
    public ArrayList<Integer> getMonthDaysAsArrayList() {
        String[] monthdayArray = null;
        ArrayList<Integer> monthDayList = new ArrayList<Integer>();
        if(dayNo != null){
            monthdayArray = dayNo.split(",");
            for(int i = 0, j = monthdayArray.length;i < j;i++){
                monthDayList.add(Integer.valueOf(monthdayArray[i]));
            }// end for
        }// end if
        return monthDayList;
    }// end getMonthDaysAsArrayList

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @return the scheduleRefId
     */
    public Long getScheduleRefId() {
        return scheduleRefId;
    }// end getScheduleRefId

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param scheduleRefId
     *        the scheduleRefId to set
     */
    public void setScheduleRefId(Long scheduleRefId) {
        this.scheduleRefId = scheduleRefId;
    }// end setScheduleRefId

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @return the batchAppRefId
     */
    public Long getBatchAppRefId() {
        return batchAppRefId;
    }// end getBatchAppRefId

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param batchAppRefId
     *        the batchAppRefId to set
     */
    public void setBatchAppRefId(Long batchAppRefId) {
        this.batchAppRefId = batchAppRefId;
    }// end setBatchAppRefId

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @return the startTime
     */
    public Time getStartTime() {
        return startTime;
    }// end getStartTime

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param startTime
     *        the startTime to set
     */
    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }// end setStartTime

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @return the frequencyCd
     */
    public String getFrequencyCd() {
        return frequencyCd;
    }// end getFrequencyCd

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param frequencyCd
     *        the frequencyCd to set
     */
    public void setFrequencyCd(String frequencyCd) {
        this.frequencyCd = frequencyCd;
    }// end setFrequencyCd

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @return the scheduleStartDt
     */
    public Date getScheduleStartDt() {
        return scheduleStartDt;
    }// end getScheduleStartDt

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param scheduleStartDt
     *        the scheduleStartDt to set
     */
    public void setScheduleStartDt(Date scheduleStartDt) {
        this.scheduleStartDt = scheduleStartDt;
    }// end setScheduleStartDt

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 21, 2015
     * @return the frequencyTypeCd
     */
    public String getFrequencyTypeCd() {
        return frequencyTypeCd;
    }// end getFrequencyTypeCd

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 21, 2015
     * @param frequencyTypeCd
     *        the frequencyTypeCd to set
     */
    public void setFrequencyTypeCd(String frequencyTypeCd) {
        this.frequencyTypeCd = frequencyTypeCd;
    }// end setFrequencyTypeCd

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 21, 2015
     * @return the repeatCd
     */
    public String getRepeatCd() {
        return repeatCd;
    }// end getRepeatCd

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 21, 2015
     * @param repeatCd
     *        the repeatCd to set
     */
    public void setRepeatCd(String repeatCd) {
        this.repeatCd = repeatCd;
    }// end setRepeatCd

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 21, 2015
     * @return the recurNo
     */
    public String getRecurNo() {
        return recurNo;
    }// end getRecurNo

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 21, 2015
     * @param recurNo
     *        the recurNo to set
     */
    public void setRecurNo(String recurNo) {
        this.recurNo = recurNo;
    }// end setRecurNo

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 21, 2015
     * @return the weekdays
     */
    public String getWeekdays() {
        return weekdays;
    }// end getWeekdays

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 21, 2015
     * @param weekdays
     *        the weekdays to set
     */
    public void setWeekdays(String weekdays) {
        this.weekdays = weekdays;
    }// end setWeekdays

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 21, 2015
     * @return the dayNo
     */
    public String getDayNo() {
        return dayNo;
    }// end getDayNo

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 21, 2015
     * @param dayNo
     *        the dayNo to set
     */
    public void setDayNo(String dayNo) {
        this.dayNo = dayNo;
    }// end setDayNo

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 21, 2015
     * @return the weekNo
     */
    public String getWeekNo() {
        return weekNo;
    }// end getWeekNo

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 21, 2015
     * @param weekNo
     *        the weekNo to set
     */
    public void setWeekNo(String weekNo) {
        this.weekNo = weekNo;
    }// end setWeekNo

    
    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 22, 2015
     * @return the active
     */
    public String getActive() {
        return active;
    }// end getActive

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 22, 2015
     * @param active
     *        the active to set
     */
    public void setActive(String active) {
        this.active = active;
    }// end setActive

    /**
     * @return the frequencyCdDesc
     */
    public String getFrequencyCdDesc() {
        return frequencyCdDesc;
    }// end getFrequencyCdDesc

    /**
     * @param frequencyCdDesc the frequencyCdDesc to set
     */
    public void setFrequencyCdDesc(String frequencyCdDesc) {
        this.frequencyCdDesc = frequencyCdDesc;
    }// end setFrequencyCdDesc

    /**
     * @return the frequencyTypeCdDesc
     */
    public String getFrequencyTypeCdDesc() {
        return frequencyTypeCdDesc;
    }// end getFrequencyTypeCdDesc

    /**
     * @param frequencyTypeCdDesc the frequencyTypeCdDesc to set
     */
    public void setFrequencyTypeCdDesc(String frequencyTypeCdDesc) {
        this.frequencyTypeCdDesc = frequencyTypeCdDesc;
    }// end setFrequencyTypeCdDesc

   
    /**
     * @return the repeatCdDesc
     */
    public String getRepeatCdDesc() {
        return repeatCdDesc;
    }// end getRepeatCdDesc

    /**
     * @param repeatCdDesc the repeatCdDesc to set
     */
    public void setRepeatCdDesc(String repeatCdDesc) {
        this.repeatCdDesc = repeatCdDesc;
    }// end setRepeatCdDesc

    /**
     * @return the lastRunStatusCd
     */
    public String getLastRunStatusCd() {
        return lastRunStatusCd;
    }

    /**
     * @param lastRunStatusCd
     *        the lastRunStatusCd to set
     */
    public void setLastRunStatusCd(String lastRunStatusCd) {
        this.lastRunStatusCd = lastRunStatusCd;
    }

    /**
     * @return the scheduleDetails
     */
    public String getScheduleDetails() {
        return scheduleDetails;
    }

    /**
     * @param scheduleDetails
     *        the scheduleDetails to set
     */
    public void setScheduleDetails(String scheduleDetails) {
        this.scheduleDetails = scheduleDetails;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer builder = new StringBuffer();
        builder.append("JmsSchedule [scheduleRefId=");
        builder.append(scheduleRefId);
        builder.append(", batchAppRefId=");
        builder.append(batchAppRefId);
        builder.append(", startTime=");
        builder.append(startTime);
        builder.append(", scheduleStartDt=");
        builder.append(scheduleStartDt);
        builder.append(", frequencyCd=");
        builder.append(frequencyCd);
        builder.append(", frequencyCdDesc=");
        builder.append(frequencyCdDesc);
        builder.append(", frequencyTypeCd=");
        builder.append(frequencyTypeCd);
        builder.append(", frequencyTypeCdDesc=");
        builder.append(frequencyTypeCdDesc);
        builder.append(", repeatCd=");
        builder.append(repeatCd);
        builder.append(", repeatCdDesc=");
        builder.append(repeatCdDesc);
        builder.append(", recurNo=");
        builder.append(recurNo);
        builder.append(", weekdays=");
        builder.append(weekdays);
        builder.append(", dayNo=");
        builder.append(dayNo);
        builder.append(", weekNo=");
        builder.append(weekNo);
        builder.append(", active=");
        builder.append(active);
        builder.append(", lastRunStatusCd=");
        builder.append(lastRunStatusCd);
        builder.append("]");
        return builder.toString();
    }// end toString
}// end class
