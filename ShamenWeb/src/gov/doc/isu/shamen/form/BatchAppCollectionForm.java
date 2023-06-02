package gov.doc.isu.shamen.form;

import static gov.doc.isu.dwarf.resources.Constants.DOT;
import static gov.doc.isu.dwarf.resources.Constants.NEW_LINE;
import static gov.doc.isu.shamen.resources.AppConstants.BAT_FILE;
import static gov.doc.isu.shamen.resources.AppConstants.DAILY_FREQUENCY_CODE;
import static gov.doc.isu.shamen.resources.AppConstants.DAY_OF_MONTH_FREQ_TYPE_CODE;
import static gov.doc.isu.shamen.resources.AppConstants.MONTHLY_FREQUENCY_CODE;
import static gov.doc.isu.shamen.resources.AppConstants.REG_EX_DATE;
import static gov.doc.isu.shamen.resources.AppConstants.REG_EX_MONTH;
import static gov.doc.isu.shamen.resources.AppConstants.REG_EX_MONTH_YEAR;
import static gov.doc.isu.shamen.resources.AppConstants.REG_EX_NAME;
import static gov.doc.isu.shamen.resources.AppConstants.REG_EX_NUM;
import static gov.doc.isu.shamen.resources.AppConstants.REG_EX_RECUR_NUM;
import static gov.doc.isu.shamen.resources.AppConstants.REG_EX_TIME;
import static gov.doc.isu.shamen.resources.AppConstants.REG_EX_YEAR;
import static gov.doc.isu.shamen.resources.AppConstants.WEEKLY_FREQUENCY_CODE;
import static gov.doc.isu.shamen.resources.AppConstants.WEEK_OF_MONTH_FREQ_TYPE_CODE;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.util.MessageResources;

import gov.doc.isu.dwarf.core.AbstractForm;
import gov.doc.isu.dwarf.model.CodeModel;
import gov.doc.isu.dwarf.model.ColumnModel;
import gov.doc.isu.dwarf.util.AppUtil;
import gov.doc.isu.dwarf.util.StringUtil;
import gov.doc.isu.shamen.models.BatchAppCollectionModel;
import gov.doc.isu.shamen.models.BatchAppModel;
import gov.doc.isu.shamen.models.ControllerModel;
import gov.doc.isu.shamen.models.ScheduleModel;
import gov.doc.isu.shamen.util.ShamenUtil;

/**
 * Form bean for the Shamen BatchApp Information for the Shamen Web Interface application.
 *
 * @author Steven L. Skinner
 */
public class BatchAppCollectionForm extends AbstractForm {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static Logger log = Logger.getLogger("gov.doc.isu.shamen.form.BatchAppCollectionForm");
    private String caller;
    private BatchAppModel batchApp;
    private ScheduleModel scheduleModel;
    private List<BatchAppModel> batchList;
    private List<CodeModel> batchAsCodeList;
    private String[] selectedValues;
    private List<ControllerModel> controllerList;
    private List<CodeModel> applicationList;
    private List<CodeModel> systemList;
    private List<CodeModel> frequencyCodes;
    private List<CodeModel> repeatCodes;
    private List<CodeModel> weekDays;
    private List<CodeModel> selectType;
    private List<CodeModel> batchTypes;
    private List<CodeModel> pocList;
    private String[] selectedDays;
    private String[] selectedType;
    private String[] selectedWeekdayWeeks;
    private String[] selectedWeekdayMonth;
    private List<CodeModel> daysInMonth;
    private String monthSelect;
    private boolean showSchedule;
    private boolean showStatus;
    private boolean showBatchApps;
    private boolean addBatchAppsError;
    private boolean batchAppCollectionScreen;
    private Map<Long, BatchAppCollectionModel> batchAppCollectionMap;
    private String pageLoadTime;
    private boolean scheduleChangeFlag;
    private boolean scheduleAddFlag;
    private String chartCsv;
    private int listCounter;

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        super.reset(mapping, request);
        if(batchApp != null){
            if(getScheduleModel() != null){
                getScheduleModel().setFrequencyCd("");
                getScheduleModel().setFrequencyTypeCd("");
                getScheduleModel().setWeekNumber(null);
                getScheduleModel().setActiveInd("N");
            }// end if
        }// end if
        selectedDays = null;
        selectedType = null;
        selectedWeekdayWeeks = null;
        selectedWeekdayMonth = null;
        selectedValues = null;
        addBatchAppsError = false;
        scheduleAddFlag = false;
        listCounter = 0;
    }// end reset

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
     * @return the batchList
     */
    public List<BatchAppModel> getBatchList() {
        return batchList;
    }// end getBatchList

    /**
     * @param batchList
     *        the batchList to set
     */
    public void setBatchList(List<BatchAppModel> batchList) {
        this.batchList = batchList;
    }// end setBatchList

    /**
     * @return the batchAsCodeList
     */
    public List<CodeModel> getBatchAsCodeList() {
        return batchAsCodeList;
    }// end getBatchAsCodeList

    /**
     * @param batchAsCodeList
     *        the batchAsCodeList to set
     */
    public void setBatchAsCodeList(List<CodeModel> batchAsCodeList) {
        this.batchAsCodeList = batchAsCodeList;
    }// end setBatchAsCodeList

    /**
     * @return the selectedValues
     */
    public String[] getSelectedValues() {
        return selectedValues;
    }// end getSelectedValues

    /**
     * @param selectedValues
     *        the selectedValues to set
     */
    public void setSelectedValues(String[] selectedValues) {
        this.selectedValues = selectedValues;
    }// end setSelectedValues

    /**
     * @return the controllerList
     */
    public List<ControllerModel> getControllerList() {
        return controllerList;
    }// end getControllerList

    /**
     * @param controllerList
     *        the controllerList to set
     */
    public void setControllerList(List<ControllerModel> controllerList) {
        this.controllerList = controllerList;
    }// end setControllerList

    /**
     * @return the applicationList
     */
    public List<CodeModel> getApplicationList() {
        return applicationList;
    }// end getApplicationList

    /**
     * @param applicationList
     *        the applicationList to set
     */
    public void setApplicationList(List<CodeModel> applicationList) {
        this.applicationList = applicationList;
    }// end setApplicationList

    /**
     * @return the frequencyCodes
     */
    public List<CodeModel> getFrequencyCodes() {
        return frequencyCodes;
    }// end getFrequencyCodes

    /**
     * @param frequencyCodes
     *        the frequencyCodes to set
     */
    public void setFrequencyCodes(List<CodeModel> frequencyCodes) {
        this.frequencyCodes = frequencyCodes;
    }// end setFrequencyCodes

    /**
     * @return the repeatCodes
     */
    public List<CodeModel> getRepeatCodes() {
        return repeatCodes;
    }// end getRepeatCodes

    /**
     * @param repeatCodes
     *        the repeatCodes to set
     */
    public void setRepeatCodes(List<CodeModel> repeatCodes) {
        this.repeatCodes = repeatCodes;
    }// end setRepeatCodes

    /**
     * @return the weekDays
     */
    public List<CodeModel> getWeekDays() {
        return weekDays;
    }// end getWeekDays

    /**
     * @param weekDays
     *        the weekDays to set
     */
    public void setWeekDays(List<CodeModel> weekDays) {
        this.weekDays = weekDays;
    }// end setWeekDays

    /**
     * @return the selectType
     */
    public List<CodeModel> getSelectType() {
        return selectType;
    }// end getSelectType

    /**
     * @param selectType
     *        the selectType to set
     */
    public void setSelectType(List<CodeModel> selectType) {
        this.selectType = selectType;
    }// end setSelectType

    /**
     * @return the selectedDays
     */
    public String[] getSelectedDays() {
        return selectedDays;
    }// end getSelectedDays

    /**
     * @param selectedDays
     *        the selectedDays to set
     */
    public void setSelectedDays(String[] selectedDays) {
        this.selectedDays = selectedDays;
    }// end setSelectedDays

    /**
     * @return the selectedType
     */
    public String[] getSelectedType() {
        return selectedType;
    }// end getSelectedType

    /**
     * @param selectedType
     *        the selectedType to set
     */
    public void setSelectedType(String[] selectedType) {
        this.selectedType = selectedType;
    }// end setSelectedType

    /**
     * @return the selectedWeekdayWeeks
     */
    public String[] getSelectedWeekdayWeeks() {
        return selectedWeekdayWeeks;
    }// end getSelectedWeekdayWeeks

    /**
     * @param selectedWeekdayWeeks
     *        the selectedWeekdayWeeks to set
     */
    public void setSelectedWeekdayWeeks(String[] selectedWeekdayWeeks) {
        this.selectedWeekdayWeeks = selectedWeekdayWeeks;
    }// end setSelectedWeekdayWeeks

    /**
     * @return the selectedWeekdayMonth
     */
    public String[] getSelectedWeekdayMonth() {
        return selectedWeekdayMonth;
    }// end getSelectedWeekdayMonth

    /**
     * @param selectedWeekdayMonth
     *        the selectedWeekdayMonth to set
     */
    public void setSelectedWeekdayMonth(String[] selectedWeekdayMonth) {
        this.selectedWeekdayMonth = selectedWeekdayMonth;
    }// end setSelectedWeekdayMonth

    /**
     * @return the daysInMonth
     */
    public List<CodeModel> getDaysInMonth() {
        return daysInMonth;
    }// end getDaysInMonth

    /**
     * @param daysInMonth
     *        the daysInMonth to set
     */
    public void setDaysInMonth(List<CodeModel> daysInMonth) {
        this.daysInMonth = daysInMonth;
    }// end setDaysInMonth

    /**
     * @return the monthSelect
     */
    public String getMonthSelect() {
        return monthSelect;
    }// end getMonthSelect

    /**
     * @param monthSelect
     *        the monthSelect to set
     */
    public void setMonthSelect(String monthSelect) {
        this.monthSelect = monthSelect;
    }// end setMonthSelect

    /**
     * @return the showSchedule
     */
    public boolean isShowSchedule() {
        return showSchedule;
    }// end isShowSchedule

    /**
     * @param showSchedule
     *        the showSchedule to set
     */
    public void setShowSchedule(boolean showSchedule) {
        this.showSchedule = showSchedule;
    }// end setShowSchedule

    /**
     * @return the showStatus
     */
    public boolean isShowStatus() {
        return showStatus;
    }// end isShowStatus

    /**
     * @param showStatus
     *        the showStatus to set
     */
    public void setShowStatus(boolean showStatus) {
        this.showStatus = showStatus;
    }// end setShowStatus

    /**
     * @return the showBatchApps
     */
    public boolean isShowBatchApps() {
        return showBatchApps;
    }// end isShowBatchApps

    /**
     * @param showBatchApps
     *        the showBatchApps to set
     */
    public void setShowBatchApps(boolean showBatchApps) {
        this.showBatchApps = showBatchApps;
    }// end setShowBatchApps

    /**
     * @return the addBatchAppsError
     */
    public boolean isAddBatchAppsError() {
        return addBatchAppsError;
    }// end isAddBatchAppsError

    /**
     * @param addBatchAppsError
     *        the addBatchAppsError to set
     */
    public void setAddBatchAppsError(boolean addBatchAppsError) {
        this.addBatchAppsError = addBatchAppsError;
    }// end setAddBatchAppsError

    /**
     * @return the batchTypes
     */
    public List<CodeModel> getBatchTypes() {
        return batchTypes;
    }// end getBatchTypes

    /**
     * @param batchTypes
     *        the batchTypes to set
     */
    public void setBatchTypes(List<CodeModel> batchTypes) {
        this.batchTypes = batchTypes;
    }// end setBatchTypes

    /**
     * @return the pocList
     */
    public List<CodeModel> getPocList() {
        return pocList;
    }// end getPocList

    /**
     * @param pocList
     *        the pocList to set
     */
    public void setPocList(List<CodeModel> pocList) {
        this.pocList = pocList;
    }// end setPocList

    /**
     * @return the batchAppCollectionMap
     */
    public Map<Long, BatchAppCollectionModel> getBatchAppCollectionMap() {
        return batchAppCollectionMap;
    }// end getBatchAppCollectionMap

    /**
     * @param batchAppCollectionMap
     *        the batchAppCollectionMap to set
     */
    public void setBatchAppCollectionMap(Map<Long, BatchAppCollectionModel> batchAppCollectionMap) {
        this.batchAppCollectionMap = batchAppCollectionMap;
    }// end setBatchAppCollectionMap

    /**
     * @return the batchAppCollectionScreen
     */
    public boolean isBatchAppCollectionScreen() {
        return batchAppCollectionScreen;
    }// end isBatchAppCollectionScreen

    /**
     * @param batchAppCollectionScreen
     *        the batchAppCollectionScreen to set
     */
    public void setBatchAppCollectionScreen(boolean batchAppCollectionScreen) {
        this.batchAppCollectionScreen = batchAppCollectionScreen;
    }// end setBatchAppCollectionScreen

    /**
     * @return the caller
     */
    public String getCaller() {
        return caller;
    }// end getCaller

    /**
     * @param caller
     *        the caller to set
     */
    public void setCaller(String caller) {
        this.caller = caller;
    }// end setCaller

    /**
     * @return the systemList
     */
    public List<CodeModel> getSystemList() {
        return systemList;
    }// end getSystemList

    /**
     * @param systemList
     *        the systemList to set
     */
    public void setSystemList(List<CodeModel> systemList) {
        this.systemList = systemList;
    }// end setSystemList

    /**
     * @return the pageLoadTime
     */
    public String getPageLoadTime() {
        return pageLoadTime;
    }// end getPageLoadTime

    /**
     * @param pageLoadTime
     *        the pageLoadTime to set
     */
    public void setPageLoadTime(String pageLoadTime) {
        this.pageLoadTime = pageLoadTime;
    }// end setPageLoadTime

    /**
     * @return the scheduleChangeFlag
     */
    public boolean isScheduleChangeFlag() {
        return scheduleChangeFlag;
    }// end isScheduleChangeFlag

    /**
     * @param scheduleChangeFlag
     *        the scheduleChangeFlag to set
     */
    public void setScheduleChangeFlag(boolean scheduleChangeFlag) {
        this.scheduleChangeFlag = scheduleChangeFlag;
    }// end setScheduleChangeFlag

    /**
     * @return the chartCsv
     */
    public String getChartCsv() {
        return chartCsv;
    }// end getChartCsv

    /**
     * @param chartCsv
     *        the chartCsv to set
     */
    public void setChartCsv(String chartCsv) {
        this.chartCsv = chartCsv;
    }// end setChartCsv

    /**
     * @return the scheduleModel
     */
    public ScheduleModel getScheduleModel() {
        return scheduleModel;
    }// end getScheduleModel

    /**
     * @param scheduleModel
     *        the scheduleModel to set
     */
    public void setScheduleModel(ScheduleModel scheduleModel) {
        this.scheduleModel = scheduleModel;
    }// end setScheduleModel

    /**
     * @return the listCounter
     */
    public int getListCounter() {
        return listCounter;
    }// end getListCounter

    /**
     * @param listCounter
     *        the listCounter to set
     */
    public void setListCounter(int listCounter) {
        this.listCounter = listCounter;
    }// end setListCounter

    /**
     * @return the scheduleAddFlag
     */
    public boolean isScheduleAddFlag() {
        return scheduleAddFlag;
    }// end isScheduleAddFlag

    /**
     * @param scheduleAddFlag
     *        the scheduleAddFlag to set
     */
    public void setScheduleAddFlag(boolean scheduleAddFlag) {
        this.scheduleAddFlag = scheduleAddFlag;
    }// end setScheduleAddFlag

    /**
     * Used to validate fields on batchAppInfo.jsp.
     *
     * @param messageResources
     *        {@link MessageResources}
     * @return {@link ActionMessages}
     */
    public ActionMessages validateBatchInfo(MessageResources messageResources) {
        log.debug("Entering gov.doc.isu.shamen.form.BatchAppCollectionForm.validateBatchInfo().");
        ActionMessages errors = new ActionMessages();
        if(batchApp != null){
            if(!StringUtil.isNullOrEmpty(batchApp.getFileName())){
                int len = batchApp.getFileName().length();
                int extn = batchApp.getFileName().lastIndexOf(DOT);
                String type = batchApp.getFileName().substring((extn + 1), len);
                if(!BAT_FILE.equalsIgnoreCase(type)){
                    errors.add("batchApp.fileName", new ActionMessage("errors.invalidFileType", type));
                }// end if
            }// end if
        }// end if
        log.debug("Exiting gov.doc.isu.shamen.form.BatchAppCollectionForm.validateBatchInfo().");
        return errors;
    }// end validateBatchInfo

    /**
     * Used to validate fields on batchAppInfo.jsp.
     *
     * @param messageResources
     *        {@link MessageResources}
     * @return {@link ActionMessages}
     */
    public ActionMessages validateBatchScheduleInfo(MessageResources messageResources) {
        log.debug("Entering gov.doc.isu.shamen.form.BatchAppCollectionForm.validateBatchScheduleInfo().");
        ActionMessages errors = new ActionMessages();
        if(batchApp != null){
            if(getScheduleModel() != null){
                if(StringUtil.isNullOrEmpty(getScheduleModel().getFrequencyCd())){
                    errors.add("scheduleModel.frequencyCd", new ActionMessage("errors.required", messageResources.getMessage("prompt.schedule.frequency")));
                }// end if
                if(StringUtil.isNullOrEmpty(getScheduleModel().getSchedultStartDt())){
                    errors.add("scheduleModel.schedultStartDt", new ActionMessage("errors.required", messageResources.getMessage("prompt.schedule.startDate")));
                }else{
                    if(scheduleChangeFlag){
                        if(!ShamenUtil.isDateGtOrEqualToCurrent(AppUtil.getDate(getScheduleModel().getSchedultStartDt()))){
                            errors.add("scheduleModel.schedultStartDt", new ActionMessage("errors.date.greater.equal.current", messageResources.getMessage("prompt.schedule.startDate")));
                        }// end if
                    }// end if
                }// end if/else
                if(StringUtil.isNullOrEmpty(getScheduleModel().getStartTime())){
                    errors.add("scheduleModel.startTime", new ActionMessage("errors.required", messageResources.getMessage("prompt.schedule.startTime")));
                }// end if

                if(DAILY_FREQUENCY_CODE.equalsIgnoreCase(getScheduleModel().getFrequencyCd())){
                    if(!getScheduleModel().getRecur().toString().matches(REG_EX_RECUR_NUM)){
                        errors.add("scheduleModel.recur", new ActionMessage("errors.recur.number", messageResources.getMessage("prompt.schedule.recur")));
                    }// end if
                }// end if
                if(WEEKLY_FREQUENCY_CODE.equalsIgnoreCase(getScheduleModel().getFrequencyCd())){
                    if(selectedWeekdayWeeks == null || selectedWeekdayWeeks.length <= 0){
                        errors.add("selectedWeekdayWeeks", new ActionMessage("errors.weekday"));
                    }// end if
                }// end if
                if(MONTHLY_FREQUENCY_CODE.equalsIgnoreCase(getScheduleModel().getFrequencyCd())){
                    if(StringUtil.isNullOrEmpty(getScheduleModel().getFrequencyTypeCd())){
                        errors.add("getScheduleModel().getFrequencyCd()", new ActionMessage("errors.required", messageResources.getMessage("prompt.schedule.recur")));
                    }else{
                        if(DAY_OF_MONTH_FREQ_TYPE_CODE.equalsIgnoreCase(getScheduleModel().getFrequencyTypeCd())){
                            if(StringUtil.isNullOrEmptyStringArray(getScheduleModel().getDayNumber())){
                                errors.add("scheduleModel.dayNumber", new ActionMessage("errors.daynumber"));
                            }// end if
                        }// end if
                        if(WEEK_OF_MONTH_FREQ_TYPE_CODE.equalsIgnoreCase(getScheduleModel().getFrequencyTypeCd())){
                            if(StringUtil.isNullOrEmptyStringArray(getScheduleModel().getWeekNumber())){
                                errors.add("scheduleModel.weekNumber", new ActionMessage("errors.weeknumber"));
                            }// end if
                            if(StringUtil.isNullOrEmptyStringArray(selectedWeekdayMonth)){
                                errors.add("selectedWeekdayMonth", new ActionMessage("errors.weekday"));
                            }// end if
                        }// end if
                    }// end if/else
                }// end if

            }// end if
        }// end if
        log.debug("Exiting gov.doc.isu.shamen.form.BatchAppCollectionForm.validateBatchScheduleInfo().");
        return errors;
    }// end validateBatchScheduleInfo

    /**
     * Used to validate fields on batchAppList.jsp.
     *
     * @param messageResources
     *        {@link MessageResources}
     * @return {@link ActionMessages}
     */
    public ActionMessages validateSearchFilters(MessageResources messageResources) {
        log.debug("Entering BatchAppCollectionForm.validateSearchFilters().");
        ActionMessages errors = new ActionMessages();
        if(!AppUtil.isEmpty(getFilterSearchObject())){
            Iterator<ColumnModel> it = getFilterSearchObject().iterator();
            int i = 0;
            while(it.hasNext()){
                ColumnModel mod = it.next();
                if(!StringUtil.isNullOrEmpty(mod.getColumnValue().toString())){
                    if(mod.getColumnName().equalsIgnoreCase("Batch App Name")){
                        log.debug("validating " + mod.getColumnName() + ". value = " + mod.getColumnValue().toString().trim());
                        if(!mod.getColumnValue().toString().trim().matches(REG_EX_NAME)){
                            errors.add("filterSearchObject[" + i + "].columnValue", new ActionMessage("errors.name", mod.getColumnName()));
                        }// end if
                    }// end if
                    if(mod.getColumnName().equalsIgnoreCase("Execution Count")){
                        log.debug("validating " + mod.getColumnName() + ". value = " + mod.getColumnValue().toString().trim());
                        if(!mod.getColumnValue().toString().trim().matches(REG_EX_NUM)){
                            errors.add("filterSearchObject[" + i + "].columnValue", new ActionMessage("errors.number", mod.getColumnName()));
                        }// end if
                    }// end if
                    if(mod.getColumnName().equalsIgnoreCase("Start Date")){
                        String date = mod.getColumnValue().toString();
                        log.debug("validating " + mod.getColumnName() + ". value = " + date);
                        validateDateFilter(date, mod.getColumnName(), errors, "filterSearchObject[" + i + "].columnValue");
                    }else if(mod.getColumnName().equalsIgnoreCase("Start Time")){
                        log.debug("validating " + mod.getColumnName() + ". value = " + mod.getColumnValue().toString().trim());
                        if(!mod.getColumnValue().toString().trim().matches(REG_EX_TIME)){
                            errors.add("filterSearchObject[" + i + "].columnValue", new ActionMessage("errors.time", mod.getColumnName()));
                        }// end if
                    }// end else...if
                }// end if
                i++;
            }// end if
        }// end if
        log.debug("Exiting BatchAppCollectionForm.validateSearchFilters().");
        return errors;
    }// end validateSearchFilters

    /**
     * Used to validate the date search fields.
     *
     * @param date
     *        The String user input to validate.
     * @param label
     *        The String label of the field to add to the error message.
     * @param errors
     *        {@link ActionMessages}
     * @param errorField
     *        The String property to add the error background to.
     */
    public void validateDateFilter(String date, String label, ActionMessages errors, String errorField) {
        log.debug("Entering BatchAppCollectionForm.validateDateFilter().");
        if(!date.matches(REG_EX_MONTH) && !date.matches(REG_EX_MONTH_YEAR) && !date.matches(REG_EX_YEAR) && !date.matches(REG_EX_DATE)){
            errors.add(errorField, new ActionMessage("errors.search_date", label));
        }else{
            String err = "", year = "";
            switch(date.length()){
                case 2:// check valid MM
                    err = (!ShamenUtil.validateInt(date, 1, 12) ? label + " month" : "");
                    break;
                case 4:// check valid CCYY
                    err = (!ShamenUtil.validateYear(date) ? label + " year" : "");
                    break;
                case 7:// check valid MM/CCYY
                    date = date.substring(0, date.indexOf("/")) + "/01" + date.substring(date.indexOf("/"));
                    year = date.substring(date.lastIndexOf("/") + 1);
                    err = (!ShamenUtil.validateDate(date) || !ShamenUtil.validateYear(year) ? label + " month/year" : "");
                    break;
                case 10:// check valid MM/DD/CCYY
                    year = date.substring(date.lastIndexOf("/") + 1);
                    err = (!ShamenUtil.validateDate(date) || !ShamenUtil.validateYear(year) ? label : "");
                    break;
                default:
                    err = "";
            }// end switch
            if(!StringUtil.isNullOrEmpty(err)){
                errors.add(errorField, new ActionMessage("errors.invalid", err));
            }// end if
        }// end if/else
        log.debug("Exiting BatchAppCollectionForm.validateDateFilter().");
    }// end validateDateFilter

    /**
     * Used to validate fields on batchAppInfo.jsp.
     *
     * @param messageResources
     *        {@link MessageResources}
     * @return {@link ActionMessages}
     */
    public ActionMessages validateBatchCollection(MessageResources messageResources) {
        log.debug("Entering gov.doc.isu.shamen.form.BatchAppCollectionForm.validateBatchCollection().");
        ActionMessages errors = new ActionMessages();
        log.debug("validating batch collection data.");
        if(batchApp != null){
            if(StringUtil.isNullOrEmpty(batchApp.getName())){
                errors.add("batchApp.name", new ActionMessage("errors.required", messageResources.getMessage("prompt.batch.collection.name")));
            }// end if
            if(!StringUtil.isNullOrEmpty(batchApp.getName()) && !batchApp.getName().trim().matches(REG_EX_NAME)){
                errors.add("batchApp.name", new ActionMessage("errors.invalid", messageResources.getMessage("prompt.batch.collection.name")));
            }// end if
            if(!StringUtil.isNullOrEmpty(batchApp.getName()) && batchApp.getName().trim().length() > 45){
                errors.add("batchApp.name", new ActionMessage("errors.maxlength", messageResources.getMessage("prompt.batch.collection.name"), 45));
            }// end if
            if(StringUtil.isNullOrEmpty(batchApp.getDescription())){
                errors.add("batchApp.description", new ActionMessage("errors.required", messageResources.getMessage("prompt.batch.collection.description")));
            }// end if
            if(!StringUtil.isNullOrEmpty(batchApp.getDescription()) && batchApp.getDescription().trim().length() > 255){
                errors.add("batchApp.description", new ActionMessage("errors.maxlength", messageResources.getMessage("prompt.batch.collection.description"), 255));
            }// end if
            if(!AppUtil.isNotNullAndZero(batchApp.getSystem().getSystemRefId())){
                errors.add("batchApp.system.systemRefId", new ActionMessage("errors.required", messageResources.getMessage("prompt.system.name")));
            }// end if
            if(!AppUtil.isNotNullAndZero(Long.valueOf(batchApp.getController().getControllerRefId()))){
                errors.add("batchApp.controller.controllerRefId", new ActionMessage("errors.required", messageResources.getMessage("prompt.controller.name")));
            }else{
                if(StringUtil.isNullOrEmptyStringArray(selectedValues) || selectedValues.length < 2){
                    if(AppUtil.isEmpty(batchAsCodeList)){
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.collection.noBatches"));
                    }else{
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.collection"));
                        setAddBatchAppsError(true);
                    }// end if/else
                }// end if
            }// end if/else
            if(!AppUtil.isNotNullAndZero(batchApp.getPointOfContact().getUserRefId())){
                errors.add("batchApp.pointOfContact.userRefId", new ActionMessage("errors.required", messageResources.getMessage("common.label.poc")));
            }// end if
        }// end if
        log.debug("Exiting gov.doc.isu.shamen.form.BatchAppCollectionForm.validateBatchCollection().");
        return errors;
    }// end validateBatchCollection

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer builder = new StringBuffer();
        builder.append("BatchAppCollectionForm [batchApp=");
        builder.append(batchApp).append(NEW_LINE);
        builder.append("batchList=");
        builder.append(batchList).append(NEW_LINE);
        builder.append("controllerList=");
        builder.append(controllerList).append(NEW_LINE);
        builder.append("applicationList=");
        builder.append(applicationList).append(NEW_LINE);
        builder.append("frequencyCodes=");
        builder.append(frequencyCodes).append(NEW_LINE);
        builder.append("repeatCodes=");
        builder.append(repeatCodes).append(NEW_LINE);
        builder.append("weekDays=");
        builder.append(weekDays).append(NEW_LINE);
        builder.append("selectType=");
        builder.append(selectType).append(NEW_LINE);
        builder.append("batchTypes=");
        builder.append(batchTypes).append(NEW_LINE);
        builder.append("selectedDays=");
        builder.append(Arrays.toString(selectedDays)).append(NEW_LINE);
        builder.append("selectedType=");
        builder.append(Arrays.toString(selectedType)).append(NEW_LINE);
        builder.append("selectedWeekdayWeeks=");
        builder.append(Arrays.toString(selectedWeekdayWeeks)).append(NEW_LINE);
        builder.append("selectedWeekdayMonth=");
        builder.append(Arrays.toString(selectedWeekdayMonth)).append(NEW_LINE);
        builder.append("daysInMonth=");
        builder.append(daysInMonth).append(NEW_LINE);
        builder.append("monthSelect=");
        builder.append(monthSelect).append(NEW_LINE);
        builder.append("showSchedule=");
        builder.append(showSchedule).append(NEW_LINE);
        builder.append("showStatus=");
        builder.append(showStatus).append(NEW_LINE);
        builder.append(super.toString());
        builder.append("]");
        return builder.toString();
    }// end to String

}// end BatchAppCollectionForm
