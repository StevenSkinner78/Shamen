/**
 * @author <strong>Steven Skinner</strong> JCCC, Sep 10, 2015
 */
package gov.doc.isu.shamen.models;

import static gov.doc.isu.dwarf.resources.Constants.NEW_LINE;
import static gov.doc.isu.shamen.resources.AppConstants.BLANK_CODE;

import java.util.Arrays;

import gov.doc.isu.dwarf.model.CommonModel;
import gov.doc.isu.dwarf.util.StringUtil;
import gov.doc.isu.shamen.util.ShamenUtil;

/**
 * Model object to hold the schedule record for a particular batch application.
 * 
 * @author <strong>Steven Skinner</strong> JCCC, Sep 10, 2015
 */
public class ScheduleModel extends CommonModel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Long scheduleRefId;
    private BatchAppModel batchApp;
    private String startTime;
    private String frequencyCd;
    private String frequencyDesc;
    private String schedultStartDt;
    private String[] weekdays;
    private String[] weekNumber;
    private String[] dayNumber;
    private String frequencyTypeCd = BLANK_CODE;
    private String frequencyTypeDesc;
    private Long recur;
    private String repeatCd;
    private String repeatDesc;
    private String activeInd = "N";
    private int listNumber;
    private boolean isEdit;
    private RunStatusModel lastRunStatus = new RunStatusModel();

    /**
     * Default Constructor
     */
    public ScheduleModel() {
        super();
    }// end constructor

    /**
     * Constructor
     * 
     * @param scheduleRefId
     *        scheduleRefId
     * @param activeInd
     *        activeInd
     */
    public ScheduleModel(Long scheduleRefId, String activeInd) {
        super();
        this.scheduleRefId = scheduleRefId;
        this.activeInd = activeInd;
    }// end constructor

    /**
     * Constructor
     * 
     * @param scheduleRefId
     *        scheduleRefId
     * @param startTime
     *        startTime
     * @param frequencyDesc
     *        frequencyDesc
     * @param schedultStartDt
     *        schedultStartDt
     * @param activeInd
     *        activeInd
     */
    public ScheduleModel(Long scheduleRefId, String startTime, String frequencyDesc, String schedultStartDt, String activeInd) {
        super();
        this.scheduleRefId = scheduleRefId;
        this.startTime = startTime;
        this.frequencyDesc = frequencyDesc;
        this.schedultStartDt = schedultStartDt;
        this.activeInd = activeInd;
    }// end constructor

    /**
     * This method formats the description of the schedule to display in table
     * 
     * @return String
     */
    public String getScheduleDetailDescription() {
        StringBuilder result = new StringBuilder();
        if(getFrequencyCd().equalsIgnoreCase("ONT")){
            result.append("Execute task at schedule start time on start date ");
            if(!"BLA".equalsIgnoreCase(getRepeatCd())){
                result.append(" and repeat every ").append(getRepeatDesc());
            }// end if
        }else if(getFrequencyCd().equalsIgnoreCase("DLY")){
            result.append("Execute task every ");
            if(!"BLA".equalsIgnoreCase(getRepeatCd())){
                result.append(getRepeatDesc()).append(" every ");
            }// end if
            if(getRecur() > 1){
                result.append(getRecur()).append(" day(s)");
            }else{
                result.append("day");
            }// end if/else
        }else if(getFrequencyCd().equalsIgnoreCase("WKY")){
            result.append("Execute task every ").append(getFullWeekDayDesc());
        }else if(getFrequencyCd().equalsIgnoreCase("MTY")){
            if(getFrequencyTypeCd().equalsIgnoreCase("MWD")){
                result.append("Execute task on the ").append(getFullWeekNumberDesc()).append(" ").append(getFullWeekDayDesc()).append(" of the month");
            }else if(getFrequencyTypeCd().equalsIgnoreCase("DOM")){
                result.append("Execute task on the ").append(getFullDayNumberDesc()).append(" day(s) of the month");
            }// end if/else
        }// end if/else
        return result.toString();
    }// end getScheduleDetailDescription

    /**
     * Returns the Week Day Name as comma separated String
     * 
     * @return String
     */
    public String getFullWeekDayDesc() {
        return ShamenUtil.convertWeekdayArray(weekdays);
    }// end getFullWeekDayDesc

    /**
     * Returns the Week Number Name as comma separated String
     * 
     * @return String
     */
    public String getFullWeekNumberDesc() {
        return ShamenUtil.convertWeekNumberArray(weekNumber);
    }// end getFullWeekNumberDesc

    /**
     * Returns the Week Number Name as comma separated String
     * 
     * @return String
     */
    public String getFullDayNumberDesc() {
        String[] dayNumberArray = new String[dayNumber.length];
        if(StringUtil.arrayContains("32", dayNumber)){
            for(int i = 0, j = dayNumber.length;i < j;i++){
                if(dayNumber[i].equalsIgnoreCase("32")){
                    dayNumberArray[i] = "Last";
                }else{
                    dayNumberArray[i] = dayNumber[i];
                }// end if/else
            }// end for
        }else{
            dayNumberArray = dayNumber;
        }
        return StringUtil.collapseArray(dayNumberArray);
    }// end getFullDayNumberDesc

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
     * @return the batchApp
     */
    public BatchAppModel getBatchApp() {
        return batchApp;
    }// end getBatchApp

    /**
     * @param batchApp
     *        the batchApp to set
     */
    public void setBatchApp(BatchAppModel batchApp) {
        this.batchApp = batchApp;
    }// end setBatchApp

    /**
     * @return the startTime
     */
    public String getStartTime() {
        return startTime;
    }// end getStartTime

    /**
     * @param startTime
     *        the startTime to set
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }// end setStartTime

    /**
     * @return the frequencyCd
     */
    public String getFrequencyCd() {
        return frequencyCd;
    }// end getFrequencyCd

    /**
     * @param frequencyCd
     *        the frequencyCd to set
     */
    public void setFrequencyCd(String frequencyCd) {
        this.frequencyCd = frequencyCd;
    }// end setFrequencyCd

    /**
     * @return the frequencyDesc
     */
    public String getFrequencyDesc() {
        return frequencyDesc;
    }// end getFrequencyDesc

    /**
     * @param frequencyDesc
     *        the frequencyDesc to set
     */
    public void setFrequencyDesc(String frequencyDesc) {
        this.frequencyDesc = frequencyDesc;
    }// end setFrequencyDesc

    /**
     * @return the schedultStartDt
     */
    public String getSchedultStartDt() {
        return schedultStartDt;
    }// end getSchedultStartDt

    /**
     * @param schedultStartDt
     *        the schedultStartDt to set
     */
    public void setSchedultStartDt(String schedultStartDt) {
        this.schedultStartDt = schedultStartDt;
    }// end setSchedultStartDt

    /**
     * @return the weekdays
     */
    public String[] getWeekdays() {
        return weekdays;
    }// end getWeekdays

    /**
     * @param weekdays
     *        the weekdays to set
     */
    public void setWeekdays(String[] weekdays) {
        this.weekdays = weekdays;
    }// end setWeekdays

    /**
     * @return the weekNumber
     */
    public String[] getWeekNumber() {
        return weekNumber;
    }// end getWeekNumber

    /**
     * @param weekNumber
     *        the weekNumber to set
     */
    public void setWeekNumber(String[] weekNumber) {
        this.weekNumber = weekNumber;
    }// end setWeekNumber

    /**
     * @return the dayNumber
     */
    public String[] getDayNumber() {
        return dayNumber;
    }// end getDayNumber

    /**
     * @param dayNumber
     *        the dayNumber to set
     */
    public void setDayNumber(String[] dayNumber) {
        this.dayNumber = dayNumber;
    }// end setDayNumber

    /**
     * @return the frequencyTypeCd
     */
    public String getFrequencyTypeCd() {
        return frequencyTypeCd;
    }// end getFrequencyTypeCd

    /**
     * @param frequencyTypeCd
     *        the frequencyTypeCd to set
     */
    public void setFrequencyTypeCd(String frequencyTypeCd) {
        this.frequencyTypeCd = frequencyTypeCd;
    }// end setFrequencyTypeCd

    /**
     * @return the frequencyTypeDesc
     */
    public String getFrequencyTypeDesc() {
        return frequencyTypeDesc;
    }// end getFrequencyTypeDesc

    /**
     * @param frequencyTypeDesc
     *        the frequencyTypeDesc to set
     */
    public void setFrequencyTypeDesc(String frequencyTypeDesc) {
        this.frequencyTypeDesc = frequencyTypeDesc;
    }// end setFrequencyTypeDesc

    /**
     * @return the recur
     */
    public Long getRecur() {
        return recur;
    }// end getRecur

    /**
     * @param recur
     *        the recur to set
     */
    public void setRecur(Long recur) {
        this.recur = recur;
    }// end setRecur

    /**
     * @return the repeatCd
     */
    public String getRepeatCd() {
        return repeatCd;
    }// end getRepeatCd

    /**
     * @param repeatCd
     *        the repeatCd to set
     */
    public void setRepeatCd(String repeatCd) {
        this.repeatCd = repeatCd;
    }// end setRepeatCd

    /**
     * @return the repeatDesc
     */
    public String getRepeatDesc() {
        return repeatDesc;
    }// end getRepeatDesc

    /**
     * @param repeatDesc
     *        the repeatDesc to set
     */
    public void setRepeatDesc(String repeatDesc) {
        this.repeatDesc = repeatDesc;
    }// end setRepeatDesc

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
     * @return the listNumber
     */
    public int getListNumber() {
        return listNumber;
    }// end getListNumber

    /**
     * @param listNumber
     *        the listNumber to set
     */
    public void setListNumber(int listNumber) {
        this.listNumber = listNumber;
    }// end setListNumber

    /**
     * @return the isEdit
     */
    public boolean isEdit() {
        return isEdit;
    }// end isEdit

    /**
     * @param isEdit
     *        the isEdit to set
     */
    public void setEdit(boolean isEdit) {
        this.isEdit = isEdit;
    }// end setEdit

    /**
     * @return the lastRunStatus
     */
    public RunStatusModel getLastRunStatus() {
        return lastRunStatus;
    }// end getLastRunStatus

    /**
     * @param lastRunStatus
     *        the lastRunStatus to set
     */
    public void setLastRunStatus(RunStatusModel lastRunStatus) {
        this.lastRunStatus = lastRunStatus;
    }// end setLastRunStatus

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer builder = new StringBuffer();
        builder.append("ScheduleModel [scheduleRefId=");
        builder.append(scheduleRefId).append(NEW_LINE);
        builder.append("batchApp=");
        builder.append(batchApp == null ? "null" : batchApp.getName()).append(NEW_LINE);
        builder.append("startTime=");
        builder.append(startTime).append(NEW_LINE);
        builder.append("frequencyCd=");
        builder.append(frequencyCd).append(NEW_LINE);
        builder.append("frequencyDesc=");
        builder.append(frequencyDesc).append(NEW_LINE);
        builder.append("schedultStartDt=");
        builder.append(schedultStartDt).append(NEW_LINE);
        builder.append("weekdays=");
        builder.append(Arrays.toString(weekdays)).append(NEW_LINE);
        builder.append("weekNumber=");
        builder.append(Arrays.toString(weekNumber)).append(NEW_LINE);
        builder.append("dayNumber=");
        builder.append(Arrays.toString(dayNumber)).append(NEW_LINE);
        builder.append("frequencyTypeCd=");
        builder.append(frequencyTypeCd).append(NEW_LINE);
        builder.append("frequencyTypeDesc=");
        builder.append(frequencyTypeDesc).append(NEW_LINE);
        builder.append("recur=");
        builder.append(recur).append(NEW_LINE);
        builder.append("repeatCd=");
        builder.append(repeatCd).append(NEW_LINE);
        builder.append("repeatDesc=");
        builder.append(repeatDesc).append(NEW_LINE);
        builder.append("activeInd=");
        builder.append(activeInd).append(NEW_LINE);
        builder.append(super.toString());
        builder.append("]");
        return builder.toString();
    }// end toString

}// end class
