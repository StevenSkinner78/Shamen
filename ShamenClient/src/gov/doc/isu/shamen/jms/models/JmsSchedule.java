/**
 * @(#)JmsSchedule.java 1.0.0 Copyright (c) 2018 Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit All Rights Reserved. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: -Redistribution of source code must retain the above copyright notice, this list of conditions and the following disclaimer. -Redistribution in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. Neither the name of Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit or the names of contributors may be used to endorse or promote products derived from this software without specific prior written permission. This software is provided "AS IS," without a warranty of any kind. ALL EXPRESS OR IMPLIED CONDITIONS,
 *                      REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. You acknowledge that this
 *                      software is not designed, licensed or intended for use in the design, construction, operation or maintenance of any nuclear facility.
 */
package gov.doc.isu.shamen.jms.models;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;

import gov.doc.isu.shamen.util.ShamenClientUtil;

/**
 * Model object to hold the schedule record for a particular batch application.
 * 
 * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
 * @author <strong>Steven Skinner</strong> JCCC, Oct 01, 2019
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
            }
        }
        return weekNumberList;
    }

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
            }
        }
        return weekDayList;
    }

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
            }
        }
        return monthDayList;
    }

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
     * @return the startTime
     */
    public Time getStartTime() {
        return startTime;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param startTime
     *        the startTime to set
     */
    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @return the frequencyCd
     */
    public String getFrequencyCd() {
        return frequencyCd;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param frequencyCd
     *        the frequencyCd to set
     */
    public void setFrequencyCd(String frequencyCd) {
        this.frequencyCd = frequencyCd;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @return the scheduleStartDt
     */
    public Date getScheduleStartDt() {
        return scheduleStartDt;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param scheduleStartDt
     *        the scheduleStartDt to set
     */
    public void setScheduleStartDt(Date scheduleStartDt) {
        this.scheduleStartDt = scheduleStartDt;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 21, 2015
     * @return the frequencyTypeCd
     */
    public String getFrequencyTypeCd() {
        return frequencyTypeCd;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 21, 2015
     * @param frequencyTypeCd
     *        the frequencyTypeCd to set
     */
    public void setFrequencyTypeCd(String frequencyTypeCd) {
        this.frequencyTypeCd = frequencyTypeCd;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 21, 2015
     * @return the repeatCd
     */
    public String getRepeatCd() {
        return repeatCd;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 21, 2015
     * @param repeatCd
     *        the repeatCd to set
     */
    public void setRepeatCd(String repeatCd) {
        this.repeatCd = repeatCd;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 21, 2015
     * @return the recurNo
     */
    public String getRecurNo() {
        return recurNo;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 21, 2015
     * @param recurNo
     *        the recurNo to set
     */
    public void setRecurNo(String recurNo) {
        this.recurNo = recurNo;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 21, 2015
     * @return the weekdays
     */
    public String getWeekdays() {
        return weekdays;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 21, 2015
     * @param weekdays
     *        the weekdays to set
     */
    public void setWeekdays(String weekdays) {
        this.weekdays = weekdays;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 21, 2015
     * @return the dayNo
     */
    public String getDayNo() {
        return dayNo;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 21, 2015
     * @param dayNo
     *        the dayNo to set
     */
    public void setDayNo(String dayNo) {
        this.dayNo = dayNo;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 21, 2015
     * @return the weekNo
     */
    public String getWeekNo() {
        return weekNo;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 21, 2015
     * @param weekNo
     *        the weekNo to set
     */
    public void setWeekNo(String weekNo) {
        this.weekNo = weekNo;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 22, 2015
     * @return the active
     */
    public String getActive() {
        return active;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 22, 2015
     * @param active
     *        the active to set
     */
    public void setActive(String active) {
        this.active = active;
    }

    /**
     * @return the frequencyCdDesc
     */
    public String getFrequencyCdDesc() {
        return frequencyCdDesc;
    }

    /**
     * @param frequencyCdDesc
     *        the frequencyCdDesc to set
     */
    public void setFrequencyCdDesc(String frequencyCdDesc) {
        this.frequencyCdDesc = frequencyCdDesc;
    }

    /**
     * @return the frequencyTypeCdDesc
     */
    public String getFrequencyTypeCdDesc() {
        return frequencyTypeCdDesc;
    }

    /**
     * @param frequencyTypeCdDesc
     *        the frequencyTypeCdDesc to set
     */
    public void setFrequencyTypeCdDesc(String frequencyTypeCdDesc) {
        this.frequencyTypeCdDesc = frequencyTypeCdDesc;
    }

    /**
     * @return the repeatCdDesc
     */
    public String getRepeatCdDesc() {
        return repeatCdDesc;
    }

    /**
     * @param repeatCdDesc
     *        the repeatCdDesc to set
     */
    public void setRepeatCdDesc(String repeatCdDesc) {
        this.repeatCdDesc = repeatCdDesc;
    }

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
    }
}
