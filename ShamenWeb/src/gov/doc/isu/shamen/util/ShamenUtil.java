package gov.doc.isu.shamen.util;

import static gov.doc.isu.dwarf.resources.Constants.DEFAULT_DATE;
import static gov.doc.isu.dwarf.resources.Constants.EMPTY_SPACE;
import static gov.doc.isu.dwarf.resources.Constants.EMPTY_STRING;
import static gov.doc.isu.dwarf.resources.Constants.KEY;
import static gov.doc.isu.dwarf.resources.Constants.UNDER_SCORE;
import static gov.doc.isu.dwarf.resources.Constants.VAL;
import static gov.doc.isu.dwarf.taglib.displaytag.util.TagConstants.DATE_SORT;
import static gov.doc.isu.dwarf.taglib.displaytag.util.TagConstants.DATE_TIME_SORT;
import static gov.doc.isu.shamen.resources.AppConstants.SHAMEN_DEFAULT_LABEL;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import gov.doc.isu.dwarf.model.CodeModel;
import gov.doc.isu.dwarf.model.ColumnModel;
import gov.doc.isu.dwarf.util.AppUtil;
import gov.doc.isu.dwarf.util.StringUtil;
import gov.doc.isu.shamen.models.ScheduleModel;
import gov.doc.isu.shamen.resources.AppConstants;

/**
 * Utility class to handle utility method for Shamen Web.
 *
 * @author <strong>Steven Skinner</strong> JCCC, Sep 10, 2015
 */
public class ShamenUtil {

    private static Logger log = Logger.getLogger("gov.doc.isu.shamen.util.ShamenUtil");
    private static final String NO_RECORDS_DISPLAY_STRING = "No Records found for your search string.";
    private static final String NO_RECORDS_FOUND_DISPLAY_STRING = "No Records found to display.";
    // private static final String STF = "HH:mm";
    private static final String STFT = "HH:mm";
    private static final String STF3 = "h:mm a";
    // private static final String YMD = "yyyyMMdd";
    private static final String SDF = "MM/dd/yyyy";
    // private static final String SDFT3 = "MM/dd/yyyy h:mm a";
    private static final String SDFTF = "MM/dd/yyyy HH:mm:ss";
    // private static final String SDFT3 = "MM/dd/yyyy h:mm a";
    private static final String SDFTF4 = "yyyy-MM-dd HH:mm:ss";
    private static final String FORWARD_SLASH = "/";
    public static final String DEFAULT_TIMESTAMP = "12/31/7799 00:00:00";

    /**
     * This method is a utility check to see if activate/deactivate tabs should be shown.
     * 
     * @param scheduleList
     *        the list of Schedule Models to check
     * @return Boolean
     */
    public static Boolean showActivateTab(List<ScheduleModel> scheduleList) {
        log.debug("Entering showActivateTab");
        log.debug("This method is a utility check to see if activate/deactivate tabs should be shown.");
        log.debug("Entry parameters are: scheduleList=" + String.valueOf(scheduleList));
        Boolean result = false;
        if(!AppUtil.isEmpty(scheduleList)){
            result = true;
            for(int i = 0, j = scheduleList.size();i < j;i++){
                if(scheduleList.get(i).isEdit() || !AppUtil.isNotNullAndZero(scheduleList.get(i).getScheduleRefId())){
                    result = false;
                    break;
                }// end if
            }// end for
        }// end if
        log.debug("Return value is: result=" + String.valueOf(result));
        log.debug("Exiting showActivateTab");
        return result;
    }// end showActivateTab

    /**
     * Returns a java.sql.Date from a java.util.Date
     *
     * @param date
     *        date
     * @return java.sql.Date
     */
    public static java.sql.Date getSQLDate(java.util.Date date) {
        log.debug("Entering getSQLDate");
        log.debug("Returns a java.sql.Date from a java.util.Date");
        log.debug("Entry parameters are: date=" + String.valueOf(date));
        java.sql.Date result = null;
        if(null != date){
            result = new java.sql.Date(date.getTime());
        }// end if
        log.debug("Return value is: result=" + String.valueOf(result));
        log.debug("Exiting getSQLDate");
        return result;
    }// end getSQLDate

    /**
     * This method us to convert a string date into a java.util.date.
     *
     * @param dateStr
     *        date
     * @return java.util.Date
     */
    public static Date getDate(String dateStr) {
        log.debug("Entering ShamenUtil.getDate().  Incoming parameter is: " + String.valueOf(dateStr));
        log.debug("This method us to convert a string date into a java.util.date.");
        Date result = null;
        try{
            result = new SimpleDateFormat(SDFTF).parse(dateStr);
        }catch(Exception e){
            log.error("Exception occurred trying to convert string to date.  The string is: " + dateStr, e);
        }// end try/catch
        log.debug("Exiting ShamenUtil.getDate()");
        return result;
    }// end getDate

    /**
     * Returns the String representation of input date in MM/dd/yyyy format. Returns empty string if date is null.
     *
     * @param date
     *        String
     * @return date as string
     */
    public static String convertStringDate(String date) {
        log.debug("Entering ShamenUtil.convertStringDate()");
        log.debug("Parameters: date=" + (date == null ? "null" : String.valueOf(date)));
        String result = EMPTY_STRING;
        if(date != null && !DEFAULT_DATE.equals(date)){
            result = new SimpleDateFormat(SDF).format(date);
        }// end if
        log.debug("Exiting ShamenUtil.convertStringDate()");
        return result;
    }// end methos

    /**
     * Returns the time difference between two dates in Hours : Minutes : Seconds format.
     *
     * @param start
     *        The start date
     * @param stop
     *        The stop date
     * @return String
     */
    public static String getTimeDifference(String start, String stop) {
        log.debug("Entering ShamenUtil.getTimeDifference().  Incoming parameters are start: " + String.valueOf(start) + ", stop: " + String.valueOf(stop));
        log.debug("Returns the time difference between two dates in Hours : Minutes : Seconds format.");
        StringBuffer result = new StringBuffer();
        if(!StringUtil.isNullOrEmpty(start) && !StringUtil.isNullOrEmpty(stop)){

            long hours, minutes, seconds;
            seconds = (getDate(stop).getTime() - getDate(start).getTime()) / 1000;

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
        log.debug("Exiting ShamenUtil.getTimeDifference()");
        return result.toString();
    }// end getTimeDifference

    /**
     * Returns the time difference between two dates in Minutes
     *
     * @param start
     *        The start date
     * @param stop
     *        The stop date
     * @return String
     */
    public static String getTimeDifferenceInMinutes(String start, String stop) {
        log.debug("Entering ShamenUtil.getTimeDifferenceInMinutes().  Incoming parameters are start: " + String.valueOf(start) + ", stop: " + String.valueOf(stop));
        log.debug("Returns the time difference between two dates in Minutes.");
        double seconds;
        double minutes = 0;
        if(!StringUtil.isNullOrEmpty(start) && !StringUtil.isNullOrEmpty(stop)){
            seconds = (getDate(stop).getTime() - getDate(start).getTime()) / 1000;
            minutes = seconds / 60;
        }// end if
        log.debug("Exiting ShamenUtil.getTimeDifference()");
        return String.valueOf(minutes);
    }// end getTimeDifference

    /**
     * Converts string from MM/dd/yyyy to yyyy-MM-dd. Returns null in case of a parse exception.
     *
     * @param dateStr
     *        date
     * @author Shane Duncan JCCC--02/14/2014, Changed to not reference the simple date format statically. When multiple users are hitting this at one time, it creates a java.lang.NumberFormatException: multiple points exception. To remedy this, it now creates a new simple date format every time.
     * @return java.sql.Date
     */
    public static java.sql.Date getSQLDate(String dateStr) {
        log.debug("Entering getSQLDate");
        log.debug("Converts string from MM/dd/yyyy to yyyy-MM-dd. Returns null in case of a parse exception.");
        log.debug("Entry parameters are: dateStr=" + String.valueOf(dateStr));
        java.sql.Date result = null;
        if((dateStr != null) && !dateStr.equals("")){
            try{
                Date date = new SimpleDateFormat(SDF).parse(dateStr);
                result = new java.sql.Date(date.getTime());
            }catch(Exception e1){
                log.error("Exception occurred trying to convert string to sql date.  The string is: " + dateStr, e1);
            }// end try/catch
        }// end if
        log.debug("Return value is: result=" + String.valueOf(result));
        log.debug("Exiting getSQLDate");
        return result;
    }// end getSQLDate

    /**
     * Converts a timestamp to a String in the format YYYY-MM-DD HH:MM
     *
     * @param timeStamp
     *        Timestamp
     * @author Shane Duncan
     * @return String
     */
    public static String getTimeStampForChart(Timestamp timeStamp) {
        log.debug("Entering getTimeStampForChart");
        log.debug("Converts a timestamp to a String in the format YYYY-MM-DD HH:MM");
        log.debug("Entry parameters are: timeStamp=" + String.valueOf(timeStamp));
        String result = "";
        if(timeStamp != null){
            result = new SimpleDateFormat(SDFTF4).format(timeStamp);
            if(DEFAULT_TIMESTAMP.equalsIgnoreCase(result)){
                result = EMPTY_STRING;
            }// end if
        }// end if
        log.debug("Return value is: result=" + String.valueOf(result));
        log.debug("Exiting getTimeStampForChart");
        return result;
    }// end getSQLDate

    /**
     * Get the sql Timestamp for string value
     *
     * @param timestampString
     *        the time stamp in string value
     * @return java.sql.Timestamp
     */
    public static java.sql.Timestamp getSqlTimestamp(String timestampString) {
        log.debug("Entering getSqlTimestamp");
        log.debug("Get the sql Timestamp for string value");
        log.debug("Entry parameters are: timestampString=" + String.valueOf(timestampString));
        if(timestampString == null){
            log.debug("Return value is: null");
            log.debug("Exiting getSqlTimestamp");
            return null;
        }// end if
        java.sql.Timestamp ts = null;
        java.util.Date utilDate = AppUtil.getDateWithTime(timestampString);
        ts = new java.sql.Timestamp(utilDate.getTime());
        log.debug("Return value is: ts=" + String.valueOf(ts));
        log.debug("Exiting getSqlTimestamp");
        return ts;
    }// end getSqlTimestamp

    /**
     * Parses input date in "MM/dd/yyyy HH:mm" format.
     *
     * @param timestampString
     *        the time stamp in string value
     * @return java.sql.Timestamp
     */
    public static java.sql.Timestamp getTimeStamp(String timestampString) {
        log.debug("Entering getTimeStamp");
        log.debug("Parses input date in \"MM/dd/yyyy HH:mm\" format.");
        log.debug("Entry parameters are: timestampString=" + String.valueOf(timestampString));
        if(timestampString == null){
            log.debug("Return value is: null");
            log.debug("Exiting getTimeStamp");
            return null;
        }// end if
        java.sql.Timestamp ts = null;
        try{
            java.util.Date utilDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(timestampString);
            ts = new java.sql.Timestamp(utilDate.getTime());
        }catch(ParseException e){
            log.error("getTimeStamp - Exception parsing date: " + timestampString);
        }// end try
        log.debug("Return value is: ts=" + String.valueOf(ts));
        log.debug("Exiting getTimeStamp");
        return ts;
    }// end getTimeStamp

    /**
     * <p>
     * This method will return the system timestamp as a custom formatted string
     * </p>
     * <b>Pattern: </b><code>MM/dd/yyyy HH:mm:ss.SSS</code>
     *
     * @return date - custom formatted system timestamp string
     */
    public static String getCurrentTimestampString() {
        log.debug("Entering getCurrentTimestampString");
        log.debug("This method will return the system timestamp as a custom formatted string  Pattern: MM/dd/yyyy HH:mm:ss.SSS");
        SimpleDateFormat sd = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
        String date = "";
        try{
            Date dt = new Date();
            date = sd.format(dt);
        }catch(Exception e){
            log.error("Exception getting system timestamp as a string: " + e.getMessage());
        }// end try/catch
        log.debug("Return value is: date=" + String.valueOf(date));
        log.debug("Exiting getCurrentTimestampString");
        return date;
    }// end getCurrentTimestampString

    /**
     * Formats the Timestamp
     *
     * @param sqlTime
     *        the passed in Timestamp
     * @return either the formatted Timestamp or null
     */
    public static String getFormattedDateTimeAsString(Timestamp sqlTime) {
        log.debug("Entering getFormattedDateTimeAsString");
        log.debug("Formats the Timestamp");
        log.debug("Entry parameters are: sqlTime=" + String.valueOf(sqlTime));
        String result = null;
        if(sqlTime != null){
            result = new SimpleDateFormat(SDFTF).format(sqlTime);
            if(DEFAULT_TIMESTAMP.equalsIgnoreCase(result)){
                result = EMPTY_STRING;
            }// end if
        }// end if
        log.debug("Return value is: result=" + String.valueOf(result));
        log.debug("Exiting getFormattedDateTimeAsString");
        return result;
    }// end getFormattedDateTimeAsString

    /**
     * This method compares the filter object list to the object list. Returns true of they match.
     *
     * @param filterObj
     *        The list of objects to use as search parameters
     * @param objList
     *        The list to compare to
     * @return <boolean> true if match
     * @throws ParseException
     *         ParseException
     */
    public static boolean filter(List<ColumnModel> filterObj, List<ColumnModel> objList) throws ParseException {
        log.debug("Entering gov.doc.isu.shamen.util.ShamenUtil - method filter");
        log.debug("Parameters: filterObj=" + (filterObj == null ? "null" : filterObj.toString()) + " ,objList=" + (objList == null ? "null" : objList.toString()));
        boolean matches = false;
        String fldName, filterFldName, fldType;
        Object fldValue, filterFldValue;
        boolean isSelect = false;
        boolean multiple = false;
        for(int i = 0;i < filterObj.size();i++){
            filterFldName = filterObj.get(i).get(KEY).toString();
            fldName = objList.get(i).get(KEY).toString();
            isSelect = objList.get(i).get("isSelect") != null && Boolean.valueOf(objList.get(i).get("isSelect").toString()) ? true : false;
            multiple = objList.get(i).get("multiple") != null && !StringUtil.isNullOrEmpty(objList.get(i).get("multiple").toString()) ? true : false;
            if(filterFldName.trim().equalsIgnoreCase(fldName.trim())){
                log.debug("Field Name to check = " + filterFldName);
                if(!multiple){
                    filterFldValue = (String) filterObj.get(i).get(VAL);
                }else{
                    filterFldValue = (String[]) filterObj.get(i).get(VAL);
                }// end if/else
                log.debug("Value of field in search object = " + filterFldValue);
                if((multiple && (checkForNotDefaultValue((String[]) filterFldValue)) && !StringUtil.isNullOrEmptyStringArray((String[]) filterFldValue)) || (!multiple && !StringUtil.isNullOrEmpty((String) filterFldValue)) && (!multiple && !SHAMEN_DEFAULT_LABEL.equalsIgnoreCase((String) filterFldValue))){
                    fldType = objList.get(i).getType();
                    fldValue = String.valueOf(objList.get(i).get(VAL));
                    if(fldName.endsWith("Phone") && ((String) filterFldValue).length() == 13){
                        fldValue = StringUtil.phoneToDB((String) fldValue);
                        filterFldValue = StringUtil.phoneToDB((String) filterFldValue);
                    }// end if
                    log.debug("Value of field to compare = " + fldValue);
                    if(DATE_SORT.equals(fldType) || DATE_TIME_SORT.equals(fldType)){
                        // compare field value against filter field value
                        if(!StringUtil.isNullOrEmpty((String) fldValue) && compareDates((String) fldValue, (String) filterFldValue)){
                            matches = true;
                        }else{
                            return false;
                        }// end if/else
                    }else if(isSelect){
                        if(multiple){
                            if(!StringUtil.isNullOrEmpty((String) fldValue) && StringUtil.arrayContains((String) fldValue, (String[]) filterFldValue)){
                                matches = true;
                            }else{
                                return false;
                            }// end if/else
                        }else{
                            if(((String) fldValue).equalsIgnoreCase((String) filterFldValue)){
                                matches = true;
                            }else{
                                return false;
                            }// end if/else
                        }// end if/else
                    }else if(((String) fldValue).toLowerCase().trim().contains(((String) filterFldValue).toLowerCase().trim())){
                        matches = true;
                    }else{
                        return false;
                    }// end if/else
                }else{
                    log.debug("Search field value is empty");
                }// end else
            }// end if
        }// end for
        log.debug("Exiting gov.doc.isu.shamen.util.ShamenUtil - method filter");
        return matches;
    }// end method

    /**
     * This method checks if string array is deafult value or if the first element of a 1 element array is <code>DEFAULT_DROPDOWN_LABEL</code>
     *
     * @param array
     *        the string array to check
     * @return boolean true|false
     */
    public static boolean checkForNotDefaultValue(String[] array) {
        log.debug("Entering checkForNotDefaultValue");
        log.debug("This method checks if string array is deafult value or if the first element of a 1 element array is DEFAULT_DROPDOWN_LABEL");
        log.debug("Entry parameters are: array=" + Arrays.toString(array));
        boolean result = true;
        if(!StringUtil.isNullOrEmptyStringArray(array)){
            if(array.length == 1){
                if(array[0] == null || SHAMEN_DEFAULT_LABEL.equalsIgnoreCase(array[0])){
                    result = false;
                }// end if
            }// end if
            if(array.length == 100 && array[0] == null){
                result = false;
            }// end if
        }// end if
        log.debug("Return value is: result=" + String.valueOf(result));
        log.debug("Exiting checkForNotDefaultValue");
        return result;
    }// end checkForNotDefaultValue

    /**
     * Used to compare two String dates to see if the month, year, month/year, or month/day/year values match. This method parsed the String objects to retrieve dates for comparison.
     *
     * @param value1
     *        The value to be matched.
     * @param value2
     *        User input trying to match.
     * @return true if values match; false otherwise
     */
    public static boolean compareDates(String value1, String value2) {
        log.debug("Entering gov.doc.isu.shamen.util.ShamenUtil - method compareDates. Incoming parameters: value1=" + String.valueOf(value1) + ", value2=" + String.valueOf(value2));
        boolean matches = false;
        if(value1 == null || value2 == null){
            log.debug("Incoming parameter value is null. Returning false.");
            return matches;
        }// end if
        try{
            SimpleDateFormat format = null;
            ParsePosition pos = new ParsePosition(0);
            Calendar fldTime = Calendar.getInstance(), filterFldTime = Calendar.getInstance();
            log.debug("Running switch statement for value: " + value2.length());
            switch(value2.length()){
                case 2:// check MM format
                    log.debug("Parsing value " + String.valueOf(value1) + " to month format.");
                    format = new SimpleDateFormat("MM");
                    fldTime.setTimeInMillis(format.parse(value1, pos).getTime());
                    filterFldTime.setTimeInMillis(format.parse(value2).getTime());
                    matches = (fldTime.get(Calendar.MONTH) == filterFldTime.get(Calendar.MONTH));
                    log.debug("Month values " + ((!matches) ? "do not match." : "match"));
                    break;
                case 4:// check CCYY format
                    log.debug("Parsing value " + String.valueOf(value1) + " to year format.");
                    format = new SimpleDateFormat("yyyy");
                    pos.setIndex(6);
                    fldTime.setTimeInMillis(format.parse(value1, pos).getTime());
                    filterFldTime.setTimeInMillis(format.parse(value2).getTime());
                    matches = (fldTime.get(Calendar.YEAR) == filterFldTime.get(Calendar.YEAR));
                    log.debug("Year values " + ((!matches) ? "do not match." : "match"));
                    break;
                case 7:// check MM/CCYY format
                    log.debug("Parsing value " + String.valueOf(value1) + " to month/year format.");
                    format = new SimpleDateFormat("MM/yyyy");
                    // substring the month/year from the value (day interferes with comparison)
                    String val = value1.substring(0, value1.indexOf(FORWARD_SLASH) + 1) + value1.substring(value1.lastIndexOf(FORWARD_SLASH) + 1);
                    fldTime.setTimeInMillis(format.parse(val, pos).getTime());
                    filterFldTime.setTimeInMillis(format.parse(value2).getTime());
                    matches = (fldTime.get(Calendar.MONTH) == filterFldTime.get(Calendar.MONTH)) && (fldTime.get(Calendar.YEAR) == filterFldTime.get(Calendar.YEAR));
                    log.debug("Month/year values " + ((!matches) ? "do not match." : "match"));
                    break;
                case 10:// check MM/DD/CCYY format
                    log.debug("Parsing value " + String.valueOf(value1) + " to month/day/year format.");
                    format = new SimpleDateFormat(SDF);
                    fldTime.setTimeInMillis(format.parse(value1, pos).getTime());
                    filterFldTime.setTimeInMillis(format.parse(value2).getTime());
                    matches = (fldTime.get(Calendar.MONTH) == filterFldTime.get(Calendar.MONTH)) && (fldTime.get(Calendar.DAY_OF_MONTH) == filterFldTime.get(Calendar.DAY_OF_MONTH)) && (fldTime.get(Calendar.YEAR) == filterFldTime.get(Calendar.YEAR));
                    log.debug("Month/day/year values " + ((!matches) ? "do not match." : "match"));
                    break;
                default:
                    matches = false;
            }// end switch
        }catch(ParseException e){
            log.error("ParseException while comparing dates. Compare values are: value=" + String.valueOf(value1) + ", filter value=" + String.valueOf(value2));
        }// end try/catch
        log.debug("Exiting gov.doc.isu.shamen.util.ShamenUtil - method compareDates. Return value is: " + String.valueOf(matches));
        return matches;
    }// end compareDates

    /**
     * This method builds a blank object to display in data table. This is a temporary fix to keep the filter object input boxes in table when no records match.
     *
     * @param displayNames
     *        The Map of display names
     * @param fieldNames
     *        The array of field name values
     * @return <List>columnVO
     */
    public static List<ColumnModel> getNoRecordsFoundForSearch(Map<String, String> displayNames, String[] fieldNames) {
        log.debug("Entering gov.doc.isu.shamen.util.ShamenUtil - method getNoRecordsFoundForSearch");
        log.debug("Parameters: displayNames.size=" + (displayNames == null ? "null" : displayNames.size()) + " ,objList.fieldNames=" + (fieldNames == null ? "null" : fieldNames.length));
        List<ColumnModel> f = new ArrayList<ColumnModel>();
        for(int x = 0, y = fieldNames.length;x < y;x++){
            ColumnModel fd = new ColumnModel();
            fd.setColumnName(displayNames == null ? fieldNames[x] : displayNames.get(fieldNames[x]));
            if(x == 0){
                fd.put(VAL, NO_RECORDS_DISPLAY_STRING);
            }else{
                fd.put(VAL, EMPTY_STRING);
            }// end else
            f.add(fd);
        }// end for
        log.debug("Exiting gov.doc.isu.shamen.util.ShamenUtil - method getNoRecordsFoundForSearch");
        return f;
    }// end method

    /**
     * This method builds a blank object to display in data table. This is a temporary fix to keep the filter object input boxes in table when no records match for Authorized Users.
     *
     * @return <List>columnVO
     */
    public static List<ColumnModel> getBatchAppNoRecordsFound() {
        log.debug("Entering gov.doc.isu.shamen.util.ShamenUtil - method getBatchAppNoRecordsFound");
        List<ColumnModel> f = new ArrayList<ColumnModel>();
        ColumnModel fd = new ColumnModel();
        fd.put(KEY, "Batch App Name");
        fd.put(VAL, NO_RECORDS_DISPLAY_STRING);
        f.add(fd);
        fd = new ColumnModel();
        fd.put(KEY, "Start Date");
        fd.put(VAL, EMPTY_STRING);
        f.add(fd);
        fd = new ColumnModel();
        fd.put(KEY, "Start Time");
        fd.put(VAL, EMPTY_STRING);
        f.add(fd);
        fd = new ColumnModel();
        fd.put(KEY, "Frequency");
        fd.put(VAL, EMPTY_STRING);
        f.add(fd);
        fd = new ColumnModel();
        fd.put(KEY, "File Name");
        fd.put(VAL, EMPTY_STRING);
        f.add(fd);
        fd = new ColumnModel();
        fd.put(KEY, "Controller");
        fd.put(VAL, EMPTY_STRING);
        f.add(fd);
        log.debug("Exiting gov.doc.isu.shamen.util.ShamenUtil - method getBatchAppNoRecordsFound");
        return f;
    }// end getBatchAppNoRecordsFound

    /**
     * This method builds a blank object to display in data table. This is a temporary fix to keep the filter object input boxes in table when no records return from query.
     *
     * @param displayNames
     *        The Map of display names
     * @param fieldNames
     *        The array of field name values
     * @return <List>columnVO
     */
    public static List<ColumnModel> getNoRecordsFoundForDisplay(Map<String, String> displayNames, String[] fieldNames) {
        log.debug("Entering gov.doc.isu.shamen.util.ShamenUtil - method getNoRecordsFoundForDisplay");
        log.debug("Parameters: displayNames.size=" + (displayNames == null ? "null" : displayNames.size()) + " ,objList.fieldNames=" + (fieldNames == null ? "null" : fieldNames.length));
        List<ColumnModel> f = new ArrayList<ColumnModel>();
        for(int x = 0, y = fieldNames.length;x < y;x++){
            ColumnModel fd = new ColumnModel();
            fd.setColumnName(displayNames == null ? fieldNames[x] : displayNames.get(fieldNames[x]));
            if(x == 0){
                fd.put(VAL, NO_RECORDS_FOUND_DISPLAY_STRING);
                fd.put("disabled", "disabled");
            }else{
                fd.put(VAL, EMPTY_STRING);
            }// end else
            f.add(fd);
        }// end for
        log.debug("Exiting gov.doc.isu.shamen.util.ShamenUtil - method getNoRecordsFoundForDisplay");
        return f;
    }// end method

    /**
     * Checks to see that value is between min and max
     *
     * @param value
     *        Value to check
     * @param min
     *        Minimum value
     * @param max
     *        Maximun value
     * @return true if value >= min and value <= max; false otherwise
     */
    public static boolean validateRange(int value, int min, int max) {
        log.debug("Entering validateRange");
        log.debug("Checks to see that value is between min and max");
        log.debug("Entry parameters are: value=" + String.valueOf(value) + ", min=" + String.valueOf(min) + ", max=" + String.valueOf(max));
        boolean result = (value >= min && value <= max);
        log.debug("Return value is: result=" + String.valueOf(result));
        log.debug("Exiting validateRange");
        return result;
    }// end validateRange

    /**
     * Validate that the input value is of type java.util.Date Method follows the MM/dd/yyyy pattern for validation
     *
     * @param value
     *        String object to check
     * @return true if java.util.Date; false otherwise
     */
    public static boolean validateDate(String value) {
        log.debug("Entering validateDate");
        log.debug("Validate that the input value is of type java.util.Date Method follows the MM/dd/yyyy pattern for validation");
        log.debug("Entry parameters are: value=" + String.valueOf(value));
        boolean isValid = false;
        SimpleDateFormat sf = new SimpleDateFormat(SDF);
        sf.setLenient(false);
        try{
            sf.parse(value);
            isValid = true;
        }catch(Exception e){
            isValid = false;
        }// try/catch
        log.debug("Return value is: isValid=" + String.valueOf(isValid));
        log.debug("Exiting validateDate");
        return isValid;
    }// validateDate

    /**
     * Validate that the input for Integer field is valid and within the range.
     *
     * @param value
     *        String object to check
     * @param min
     *        Minimum value
     * @param max
     *        Maximun value
     * @return true if valid value; false otherwise
     */
    public static boolean validateInt(String value, int min, int max) {
        log.debug("Entering validateInt");
        log.debug("Validate that the input for Integer field is valid and within the range.");
        log.debug("Entry parameters are: value=" + String.valueOf(value) + ", min=" + String.valueOf(min) + ", max=" + String.valueOf(max));
        boolean result = false;
        if(value != null){
            if(validateInt(value)){
                int n = Integer.valueOf(value).intValue();
                result = validateRange(n, min, max);
            }// end if
        }// end if
        log.debug("Return value is: result=" + String.valueOf(result));
        log.debug("Exiting validateInt");
        return result;
    }// validateInt

    /**
     * Validate that the input for year is valid.
     *
     * @param value
     *        String object to check
     * @return true if valid year; false otherwise
     */
    public static boolean validateYear(String value) {
        log.debug("Entering validateYear");
        log.debug("Validate that the input for year is valid.");
        log.debug("Entry parameters are: value=" + String.valueOf(value));
        boolean result = false;
        if(value != null){
            if(validateInt(value)){
                int n = Integer.valueOf(value).intValue();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                int year = calendar.get(Calendar.YEAR);
                result = validateRange(n, 1900, year);
            }// end if
        }// end if
        log.debug("Return value is: result=" + String.valueOf(result));
        log.debug("Exiting validateYear");
        return result;
    }// validateYear

    /**
     * Validate that the input value is of type int
     *
     * @param value
     *        String object to check
     * @return true if int; false otherwise
     */
    public static boolean validateInt(String value) {
        log.debug("Entering validateInt");
        log.debug("Validate that the input value is of type int");
        log.debug("Entry parameters are: value=" + String.valueOf(value));
        boolean isValid = false;
        try{
            Integer.parseInt(value);
            isValid = true;
        }catch(Exception e){
            isValid = false;
        }// end try/catch
        log.debug("Return value is: isValid=" + String.valueOf(isValid));
        log.debug("Exiting validateInt");
        return isValid;
    }// end validateInt

    /**
     * Returns the SQl Time as a hh:mm AM/PM String.
     *
     * @param sqlTime
     *        sqlTime
     * @return String
     */
    public static String getFormatted12HrsTimeAsString(Time sqlTime) {
        log.debug("Entering ShamenUtil.getFormatted12HrsTimeAsString.");
        log.debug("Parameters are: sqlTime=" + String.valueOf(sqlTime));
        String result = null;
        if(sqlTime != null){
            result = new SimpleDateFormat(STF3).format(sqlTime);
        }// end if
        log.debug("Exiting ShamenUtil.getFormatted12HrsTimeAsString. result=" + String.valueOf(result));
        return result;
    }// end method

    /**
     * Returns the SQl Time as a hh:mm:ss String.
     *
     * @param sqlTime
     *        sqlTime
     * @return String
     */
    public static String getTimeAsString(Time sqlTime) {
        log.debug("Entering ShamenUtil.getTimeAsString.");
        log.debug("Parameters are: sqlTime=" + String.valueOf(sqlTime));
        String result = null;
        if(sqlTime != null){
            result = new SimpleDateFormat(STFT).format(sqlTime);
        }// end if
        log.debug("Exiting ShamenUtil.getTimeAsString. result=" + String.valueOf(result));
        return result;
    }// end method

    /**
     * Converts the hh:mm a Time value to java.sql.Time
     *
     * @param v
     *        timesString
     * @return java.sql.Time
     */
    public static Time getSQLTime(String v) {
        log.debug("Entering ShamenUtil.getSQLTime.");
        log.debug("Parameters are: v=" + String.valueOf(v));

        Time result = null;
        StringBuffer sb = new StringBuffer();
        if((v != null) && !v.equals("") && (v.indexOf(":") != -1)){
            if(v.indexOf("p") != -1){
                int hold = Integer.valueOf(v.substring(0, v.indexOf(":")));
                hold = hold + 12;
                sb.append(hold).append(":").append(v.substring(v.indexOf(":") + 1, v.indexOf(":") + 3));
                v = sb.toString();
            }else if(v.indexOf("a") != -1 && v.substring(0, 2).equalsIgnoreCase("12")){
                sb.append("00").append(":").append(v.substring(v.indexOf(":") + 1, v.indexOf(":") + 3));
                v = sb.toString();
            }else{
                if(v.trim().length() == 5){
                    sb.append(v.trim()).append(":").append("00");
                }// end if
                v = sb.toString();
            }// end if/else
            try{
                result = new Time(new SimpleDateFormat(STFT).parse(v.trim()).getTime());
            }catch(ParseException e){
                log.warn("Input String cannot be parsed in to time value.. Returning null");
            }// end catch
        }// end if
        log.debug("Exiting ShamenUtil.getSQLTime. result=" + String.valueOf(result));
        return result;
    }// end method

    /**
     * Returns the String representation of input date in MM/dd/yyyy format. Returns empty string if date is null.
     *
     * @param date
     *        java.sql.Date
     * @return date as string
     * @throws ParseException
     *         if an exception occurred
     */
    public static String getDateAsString(final Date date) throws ParseException {
        log.debug("Entering ShamenUtil - method getDateAsString");
        log.debug("Parameters: date=" + (date == null ? "null" : String.valueOf(date)));
        String result = EMPTY_STRING;
        if(date != null){
            result = new SimpleDateFormat(SDF).format(date);
        }// end if
        log.debug("Exiting ShamenUtil - method getDateAsString");
        return result;
    }// end method

    /**
     * This method was added for use in the Visitation Module. Receives a java.sql.Date and returns a java.sql.Date of the first day in the month.
     *
     * @param inputDate
     *        a java.sql.Date object
     * @return java.sql.Date
     * @author Shane Duncan
     */
    public static java.sql.Date getFirstDayOfMonth(java.sql.Date inputDate) {
        log.debug("Entering getFirstDayOfMonth");
        log.debug("This method was added for use in the Visitation Module. Receives a java.sql.Date and returns a java.sql.Date of the first day in the month.");
        log.debug("Entry parameters are: inputDate=" + String.valueOf(inputDate));
        // Prevent null pointer exceptions
        if(inputDate == null){
            log.debug("Return value is: null");
            log.debug("Exiting getFirstDayOfMonth");
            return null;
        }// end if
        Calendar workDate = Calendar.getInstance();
        workDate.setTime(inputDate);
        workDate.set(Calendar.DAY_OF_MONTH, workDate.getActualMinimum(Calendar.DAY_OF_MONTH));
        java.sql.Date sqlDate = getSQLDate(workDate.getTime());
        log.debug("Return value is: sqlDate=" + String.valueOf(sqlDate));
        log.debug("Exiting getFirstDayOfMonth");
        return sqlDate;

    }// end getFirstDayOfMonth

    /**
     * This method was added for use in the Visitation Module. Receives a java.sql.Date and returns a java.sql.Date of the last day in the month.
     *
     * @param inputDate
     *        a java.sql.Date object
     * @return java.sql.Date
     * @author Shane Duncan
     */
    public static java.sql.Date getLastDayOfMonth(java.sql.Date inputDate) {
        log.debug("Entering getLastDayOfMonth");
        log.debug("This method was added for use in the Visitation Module. Receives a java.sql.Date and returns a java.sql.Date of the last day in the month.");
        log.debug("Entry parameters are: inputDate=" + String.valueOf(inputDate));
        // Prevent null pointer exceptions
        if(inputDate == null){
            log.debug("Return value is: null");
            log.debug("Exiting getLastDayOfMonth");
            return null;
        }// end if
        Calendar workDate = Calendar.getInstance();
        workDate.setTime(inputDate);
        workDate.set(Calendar.DAY_OF_MONTH, workDate.getActualMaximum(Calendar.DAY_OF_MONTH));
        java.sql.Date sqlDate = getSQLDate(workDate.getTime());
        log.debug("Return value is: sqlDate=" + String.valueOf(sqlDate));
        log.debug("Exiting getLastDayOfMonth");
        return sqlDate;
    }// end getLastDayOfMonth

    /**
     * Returns the month name from a month int value.
     *
     * @param month
     *        month number
     * @return String
     */
    public static String getMonthFromInt(int month) {
        log.debug("Entering getMonthFromInt");
        log.debug("Returns the month name from a month int value.");
        log.debug("Entry parameters are: month=" + String.valueOf(month));
        String result = new DateFormatSymbols().getMonths()[month - 1];
        log.debug("Return value is: result=" + String.valueOf(result));
        log.debug("Exiting getMonthFromInt");
        return result;
    }// end getMonthFromInt

    /**
     * Returns an CodeModel list with the integer value as the key and month name as the value
     *
     * @return List<CodeModel> - CodeModel list with the integer value as the key and month name as the value
     */
    public static List<CodeModel> getMonthsOptionList() {
        log.debug("Entering getMonthsOptionList");
        log.debug("Returns an CodeModel list with the integer value as the key and month name as the value");
        String[] months = new DateFormatSymbols().getMonths();
        List<CodeModel> monthsList = new ArrayList<CodeModel>();
        monthsList.add(new CodeModel(String.valueOf(0), SHAMEN_DEFAULT_LABEL));
        for(int i = 0;i < (months.length - 1);i++){
            monthsList.add(new CodeModel(Integer.toString(i + 1), months[i]));
        }// end for
        log.debug("Return value is: monthsList=" + String.valueOf(monthsList));
        log.debug("Exiting getMonthsOptionList");
        return monthsList;
    }// end getMonthsOptionList

    /**
     * Returns an CodeModel list with the integer value as the key and weekday name as the value
     *
     * @return List<CodeModel> - CodeModel list with the integer value as the key and weekday name as the value
     */
    public static List<CodeModel> getWeekDaysOptionList() {
        log.debug("Entering getWeekDaysOptionList");
        log.debug("Returns an CodeModel list with the integer value as the key and weekday name as the value");
        String[] weekDays = new DateFormatSymbols().getWeekdays();
        List<CodeModel> weekDayList = new ArrayList<CodeModel>();
        // weekDayList.add(new CodeModel(String.valueOf(0), DEFAULT_LABEL));
        for(int i = 1;i < weekDays.length;i++){
            weekDayList.add(new CodeModel(Integer.toString(i), weekDays[i]));
        }// end for
        log.debug("Return value is: weekDayList=" + String.valueOf(weekDayList));
        log.debug("Exiting getWeekDaysOptionList");
        return weekDayList;
    }// end getWeekDaysOptionList

    /**
     * Gets the week number list
     *
     * @return List<CodeModel>
     */
    public static List<CodeModel> getWeekNumberList() {
        log.debug("Entering getWeekNumberList");
        log.debug("Gets the week number list");
        List<CodeModel> selectType = new ArrayList<CodeModel>();
        selectType.add(new CodeModel("1", "First"));
        selectType.add(new CodeModel("2", "Second"));
        selectType.add(new CodeModel("3", "Third"));
        selectType.add(new CodeModel("4", "Fourth"));
        selectType.add(new CodeModel("5", "Last"));
        log.debug("Return value is: selectType=" + String.valueOf(selectType));
        log.debug("Exiting getWeekNumberList");
        return selectType;
    }// end getWeekNumberList

    /**
     * Converts a string array of week numbers to a comma separated string of week number names
     *
     * @param sarray
     *        the string array
     * @return String
     */
    public static String convertWeekNumberArray(String[] sarray) {
        log.debug("Entering convertWeekNumberArray");
        log.debug("Converts a string array of week numbers to a comma separated string of week number names");
        log.debug("Entry parameters are: sarray=" + Arrays.toString(sarray));
        String returnResult = EMPTY_STRING;
        if(sarray != null){
            String[] result = new String[sarray.length];
            for(int i = 0;i < sarray.length;i++){
                if(Integer.parseInt(sarray[i]) == 1){
                    result[i] = "First";
                }else if(Integer.parseInt(sarray[i]) == 2){
                    result[i] = "Second";
                }else if(Integer.parseInt(sarray[i]) == 3){
                    result[i] = "Third";
                }else if(Integer.parseInt(sarray[i]) == 4){
                    result[i] = "Fourth";
                }else if(Integer.parseInt(sarray[i]) == 5){
                    result[i] = "Last";
                }// end if/else

            }// end for
            returnResult = StringUtil.collapseArray(result);
        }// end if
        log.debug("Return value is: returnResult=" + String.valueOf(returnResult));
        log.debug("Exiting convertWeekNumberArray");
        return returnResult;
    }// end convertWeekdayArray

    /**
     * Returns the days of month as a List<CodeModel>
     *
     * @return List<CodeModel>
     */
    public static List<CodeModel> getDaysOfMonthList() {
        log.debug("Entering getDaysOfMonthList");
        log.debug("Returns the days of month as a ListCodeModel");
        List<CodeModel> result = new ArrayList<CodeModel>();
        for(int i = 1;i < 32;i++){
            result.add(new CodeModel(String.valueOf(i), String.valueOf(i)));
        }// end for
        result.add(new CodeModel("32", "Last"));
        log.debug("Return value is: result=" + String.valueOf(result));
        log.debug("Exiting getDaysOfMonthList");
        return result;
    }// end getDaysOfMonthList

    /**
     * Returns the week day name from a day int value.
     *
     * @param day
     *        int
     * @return String
     */
    public static String getDayFromInt(int day) {
        log.debug("Entering getDayFromInt");
        log.debug("Returns the week day name from a day int value.");
        log.debug("Entry parameters are: day=" + String.valueOf(day));
        String result = new DateFormatSymbols().getWeekdays()[day];
        log.debug("Return value is: result=" + String.valueOf(result));
        log.debug("Exiting getDayFromInt");
        return result;
    }// end getDayFromInt

    /**
     * Returns the week days in String []
     *
     * @return String []
     */
    public static String[] getWeekDays() {
        log.debug("Entering getWeekDays");
        log.debug("Returns the week days in String []");
        String[] result = new DateFormatSymbols().getWeekdays();
        log.debug("Return value is: result=" + Arrays.toString(result));
        log.debug("Exiting getWeekDays");
        return result;
    }// end getWeekDays

    /**
     * Converts a string array of weekday numbers to a comma separated string of week day names
     *
     * @param sarray
     *        the string array
     * @return String
     */
    public static String convertWeekdayArray(String[] sarray) {
        log.debug("Entering convertWeekdayArray");
        log.debug("Converts a string array of weekday numbers to a comma separated string of week day names");
        log.debug("Entry parameters are: sarray=" + Arrays.toString(sarray));
        String returnResult = EMPTY_STRING;
        if(sarray != null){
            String[] result = new String[sarray.length];
            for(int i = 0;i < sarray.length;i++){
                result[i] = getDayFromInt(Integer.parseInt(sarray[i]));
            }// end for
            returnResult = StringUtil.collapseArray(result);
        }// end if
        log.debug("Return value is: returnResult=" + String.valueOf(returnResult));
        log.debug("Exiting convertWeekdayArray");
        return returnResult;
    }// end convertWeekdayArray

    /**
     * Converts a string array of month numbers to a comma separated string of month names
     *
     * @param sarray
     *        the string array
     * @return String
     */
    public static String convertMonthArray(String[] sarray) {
        log.debug("Entering convertMonthArray");
        log.debug("Converts a string array of month numbers to a comma separated string of month names");
        log.debug("Entry parameters are: sarray=" + Arrays.toString(sarray));
        String returnResult = EMPTY_STRING;
        if(sarray != null){
            String[] result = new String[sarray.length];
            for(int i = 0;i < sarray.length;i++){
                result[i] = getMonthFromInt(Integer.parseInt(sarray[i]));
            }// end for
            returnResult = StringUtil.collapseArray(result);
        }// end if
        log.debug("Return value is: returnResult=" + String.valueOf(returnResult));
        log.debug("Exiting convertMonthArray");
        return returnResult;
    }// end convertMonthArray

    /**
     * Converts an array of Strings to a comma separated line
     *
     * @param values
     *        array of Longs to collapse to convert
     * @return String object
     */
    public static String collapseLongArray(Long[] values) {
        log.debug("Entering ShamenUtil - method collapseLongArray()");
        log.debug("Parameters are: array.length=" + (values == null ? "null" : values.length));
        String retStr = EMPTY_STRING;
        if(values != null && values.length != 0){
            StringBuffer sb = new StringBuffer();
            for(int i = 0;i < values.length;i++){
                sb.append(values[i]);
                if(i + 1 < values.length){
                    sb.append(",");
                }// end if
            }// end for
            retStr = sb.toString();
        }// end if
        log.debug("Exiting ShamenUtill - method collapseLongArray()");
        return retStr;
    }// end method

    /**
     * This method is used to check for a <code>null</code> or empty collection.
     *
     * @param col
     *        The <code>Collection&lt;?&gt;</code> to check
     * @return True if <code>null</code> or empty, false otherwise.
     */
    public static boolean isEmpty(Collection<?> col) {
        log.debug("Entering isEmpty");
        log.debug("This method is used to check for a null or empty collection.");
        log.debug("Entry parameters are: col=" + String.valueOf(col));
        boolean result = (col == null || col.size() == 0) ? true : false;
        log.debug("Return value is: result=" + String.valueOf(result));
        log.debug("Exiting isEmpty");
        return result;
    }// end isEmpty

    /**
     * @author Ram Vangala
     * @date 11/18/2009
     * @param sarray
     *        String[]
     * @return Integer[]
     */
    public static Integer[] convertStringArraytoIntegerArray(String[] sarray) {
        log.debug("Entering convertStringArraytoIntegerArray");
        log.debug("Entry parameters are: sarray=" + Arrays.toString(sarray));
        if(sarray != null){
            Integer[] longarray = new Integer[sarray.length];
            for(int i = 0;i < sarray.length;i++){
                longarray[i] = Integer.parseInt(sarray[i]);
            }// end for
            log.debug("Return value is: longarray=" + Arrays.toString(longarray));
            log.debug("Exiting convertStringArraytoIntegerArray");
            return longarray;
        }// end if
        log.debug("Return value is: null");
        log.debug("Exiting convertStringArraytoIntegerArray");
        return null;
    }// end convertStringArraytoIntegerArray

    /**
     * This method formats the export file name.
     *
     * @param startDateTime
     *        the start date and time
     * @param batchName
     *        the name of the batch
     * @param type
     *        the type of batch
     * @return String
     */
    public static String formatFileName(final String startDateTime, String batchName, String type) {
        log.debug("Entering gov.doc.isu.shamen.util.ShamenUtil - method formatFileName");
        log.debug("Parameters: startDateTime=" + String.valueOf(startDateTime) + ", batchName=" + String.valueOf(batchName) + ", type=" + String.valueOf(type));
        StringBuffer returnString = new StringBuffer();
        returnString.append(batchName.replaceAll(EMPTY_SPACE, UNDER_SCORE));
        returnString.append(UNDER_SCORE).append(type);
        if(!StringUtil.isNullOrEmpty(startDateTime)){
            if(startDateTime.indexOf(EMPTY_SPACE) > 0){
                String arr = startDateTime.replaceAll(EMPTY_SPACE, UNDER_SCORE);
                arr = arr.replaceAll("/", EMPTY_STRING);
                arr = arr.replaceAll(":", EMPTY_STRING);
                returnString.append(UNDER_SCORE).append(arr);
            }// end if
        }// end if
        log.debug("Exiting gov.doc.isu.shamen.util.ShamenUtil - method formatFileName result=" + String.valueOf(returnString));
        return returnString.toString();
    }// end formatFileName

    /**
     * This method is a null safe trim utility method.
     *
     * @param inString
     *        The String to be trimmed
     * @return trimmed value
     */
    public static String trim(String inString) {
        log.debug("Entering gov.doc.isu.shamen.util.ShamenUtil - method trim - with parameter: " + String.valueOf(inString));
        String returnString = null;
        if(inString != null){
            returnString = inString.trim();
        }// end if
        log.debug("Exiting gov.doc.isu.shamen.util.ShamenUtil - method trim - with return value: " + String.valueOf(returnString));
        return returnString;
    }// end trim

    /**
     * Replaces what it finds in the string with something else.
     *
     * @param inString
     *        inString
     * @param from
     *        from this
     * @param to
     *        to this
     * @return String
     */
    public static String swapStrings(String inString, String from, String to) {
        log.debug("Entering gov.doc.isu.shamen.util.ShamenUtil - method swapStrings.");
        log.debug("Parameters are: inString=" + String.valueOf(inString) + ", from=" + String.valueOf(from) + ", to=" + String.valueOf(to));
        if(inString != null){
            int index = -1;
            index = inString.indexOf(from);
            while(index != -1){
                inString = inString.substring(0, index) + to + inString.substring(index + from.length());
                index = inString.indexOf(from, index + to.length());
            }// end while
        }// end if
        log.debug("Exiting gov.doc.isu.shamen.util.ShamenUtil - method swapStrings. result=" + String.valueOf(inString));
        return inString;
    }// end method

    /**
     * Replaces all newlines (<code>\n</code>) or CR-newlines (<code>\r\n</code>) with html <code><br></code> 's
     *
     * @param inString
     *        inString
     * @return String
     */
    public static String textBreak2HtmlBreak(String inString) {
        log.debug("Entering gov.doc.isu.shamen.util.ShamenUtil - method textBreak2HtmlBreak.");
        log.debug("Parameters are: inString=" + String.valueOf(inString));
        inString = swapStrings(inString, "\r\n", "<br/>");
        inString = swapStrings(inString, "\n", "<br/>");
        log.debug("Exiting gov.doc.isu.shamen.util.ShamenUtil - method textBreak2HtmlBreak. result=" + String.valueOf(inString));
        return inString;
    }// end method

    /**
     * Replaces all newlines (<code><br></code>) with (<code>\n</code>)
     *
     * @param inString
     *        inString
     * @return String
     */
    public static String htmlBreak2TextBreak(String inString) {
        log.debug("Entering gov.doc.isu.shamen.util.ShamenUtil - method htmlBreak2TextBreak.");
        log.debug("Parameters are: inString=" + String.valueOf(inString));
        inString = swapStrings(inString, "<br/>", "\r\n");
        log.debug("Exiting gov.doc.isu.shamen.util.ShamenUtil - method htmlBreak2TextBreak. result=" + String.valueOf(inString));
        return inString;
    }// end method

    /**
     * Replaces all newlines (<code>\n</code>) or CR-newlines (<code>\r\n</code>) with html <code><br></code> 's
     *
     * @param inString
     *        inString
     * @return String
     */
    public static String textBreakRemoveBreak(String inString) {
        log.debug("Entering gov.doc.isu.shamen.util.ShamenUtil - method textBreakRemoveBreak.");
        log.debug("Parameters are: inString=" + String.valueOf(inString));
        inString = swapStrings(inString, "\r\n", " ");
        inString = swapStrings(inString, "\n", " ");
        log.debug("Exiting gov.doc.isu.shamen.util.ShamenUtil - method textBreakRemoveBreak. result=" + String.valueOf(inString));
        return inString;
    }// end method

    /**
     * This method clears out the time portion of a date object for more accurate date comparisons.
     *
     * @param inputDate
     *        a java.sql.Date object
     * @return the inputDate value with all of the time fields set to zero
     * @author Shane Duncan
     */
    public static java.sql.Date clearSQLTimeFields(java.sql.Date inputDate) {
        log.debug("Entering gov.doc.isu.shamen.util.ShamenUtil - method clearSQLTimeFields.");
        log.debug("This method clears out the time portion of a date object for more accurate date comparisons.");
        // Prevent null pointer exceptions
        if(inputDate == null){
            return null;
        }// end if
         // Create Calendar object representing the date
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(inputDate.getTime());
        // Clear out the time portion
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        // Return the new date value
        java.sql.Date returnDate = new java.sql.Date(cal.getTimeInMillis());
        cal = null;
        log.debug("Exiting gov.doc.isu.shamen.util.ShamenUtil - method clearSQLTimeFields. result=" + String.valueOf(returnDate));
        return returnDate;
    }// end clearSQLTimeFields

    /**
     * This method clears out the time portion of a date object for more accurate date comparisons.
     *
     * @param cal
     *        a Calendar object
     * @return the inputDate value with all of the time fields set to zero
     * @author Shane Duncan
     */
    public static long clearCalendarTimeFields(Calendar cal) {
        log.debug("Entering ShamenUtil method clearCalendarTimeFields.");
        log.debug("This method clears out the time portion of a date object for more accurate date comparisons.");
        long returnVal = 0;
        // Prevent null pointer exceptions
        if(cal == null){
            return returnVal;
        }// end if
         // Clear out the time portion
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        returnVal = cal.getTimeInMillis();
        log.debug("Exiting ShamenUtil method clearCalendarTimeFields returning: " + String.valueOf(returnVal));
        return returnVal;
    }// end clearCalendarTimeFields

    /**
     * This method clears out the time portion of a date object for more accurate date comparisons.
     *
     * @param cal
     *        a Calendar object
     * @return the inputDate value with all of the time fields set to zero
     * @author Shane Duncan
     */
    public static Calendar clearCalendarTimeFieldsForCal(Calendar cal) {
        log.debug("Entering ShamenUtil method clearCalendarTimeFieldsForCal.");
        log.debug("This method clears out the time portion of a date object for more accurate date comparisons.");

        // Prevent null pointer exceptions
        if(cal == null){
            return null;
        }// end if
         // Clear out the time portion
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        log.debug("Exiting ShamenUtil method clearCalendarTimeFieldsForCal returning: " + String.valueOf(cal));
        return cal;
    }// end clearCalendarTimeFieldsForCal

    /**
     * This method returns timestamp from Date (MM/dd/yyyy) and Time(HH:mm) strings. Returns null in case of a parse exception.
     * <p>
     * If null is passed in for the <code>dateTimeFormat</code> parameter a default format is used. The Default format is: <code>MM/dd/yyyy HH:mm</code>.
     * </p>
     * <p>
     * <b>NOTE: This method was intended to be called for formatting a date and time for a display table column.</b>
     * </p>
     *
     * @author rts000is added this method for returning timestamp values for display table columns that need to use it
     * @param dateStr
     *        [Optional] The date in string format.
     * @param timeStr
     *        [Optional] The time in string format.
     * @param dateTimeFormat
     *        [Optional] The time in string format.
     * @return Timestamp or null if there was a parse exception
     */
    public static Timestamp getSqlTSFromDateTimeString(String dateStr, String timeStr, String dateTimeFormat) {
        log.debug("Entering gov.doc.isu.shamen.util.ShamenUtil - method getSqlTSFromDateTimeString.");
        log.debug("Parameters: " + new Object[]{dateStr, timeStr, dateTimeFormat});
        log.debug("This method returns timestamp from Date (MM/dd/yyyy) and Time(HH:mm) strings. Returns null in case of a parse exception.");
        java.sql.Timestamp result = null;
        try{
            if(!isNullOrEmpty(dateStr) && !isNullOrEmpty(timeStr)){
                StringBuffer sb = new StringBuffer();
                sb.append(dateStr.trim());
                sb.append(" ").append(timeStr.trim());
                String dateTimeSDF = null;
                if(isNullOrEmpty(dateTimeFormat)){
                    dateTimeSDF = AppConstants.SDFT;
                }else{
                    dateTimeSDF = dateTimeFormat;
                } // end if-else
                java.util.Date date = new SimpleDateFormat(dateTimeSDF).parse(sb.toString());
                result = new java.sql.Timestamp(date.getTime());
            } // end if
        }catch(ParseException e){
            log.warn("Parse exception was encountered while trying to parse: " + dateStr + " " + timeStr + " to format: " + dateTimeFormat);
        } // end catch
        log.debug("Exiting gov.doc.isu.shamen.util.ShamenUtil - method getSqlTSFromDateTimeString. result=" + String.valueOf(result));
        return result;
    }// end getSqlTSFromDateTimeString

    /**
     * This method return true if the given {@code String} is null or empty else returns false.
     *
     * @param string
     *        The {@code String} to check.
     * @return True if null or empty, false otherwise.
     */
    public static boolean isNullOrEmpty(String string) {
        log.debug("Entering gov.doc.isu.shamen.util.ShamenUtil - method isNullOrEmpty.");
        log.debug("Parameter: " + String.valueOf(string));
        boolean result = (string == null || string.trim().equals(EMPTY_STRING)) ? true : false;
        log.debug("Exiting gov.doc.isu.shamen.util.ShamenUtil - method isNullOrEmpty. result=" + String.valueOf(result));
        return result;
    }// end isNullOrEmpty

    /**
     * This method returns the string value of the current date in MM/dd/yyyy format.
     *
     * @return String current date
     */
    public static String getDate() {
        log.debug("Entering gov.doc.isu.shamen.util.ShamenUtil - method getDate.");
        log.debug("This method returns the string value of the current date in MM/dd/yyyy format.");
        String result = new SimpleDateFormat(SDF).format(new Date());
        log.debug("Exiting gov.doc.isu.shamen.util.ShamenUtil - method getDate. result=" + String.valueOf(result));
        return result;
    }// end getDate

    /**
     * Gets the current java.util.Date in MM/dd/yyyy format.
     *
     * @return the current date
     * @author Steven Skinner
     */
    public static Date getCurrentDate() {
        log.debug("Entering gov.doc.isu.shamen.util.ShamenUtil - method getCurrentDate.");
        log.debug("This method returns the current java.util.Date in MM/dd/yyyy format.");
        Date currentDate = AppUtil.getDate(new SimpleDateFormat(SDF).format(new Date()));
        log.debug("Exiting gov.doc.isu.shamen.util.ShamenUtil - method getDate. currentDate=" + String.valueOf(currentDate));
        return currentDate;
    }// end getCurrentDate

    /**
     * Check to see if the given date is equal to or greater than current date.
     *
     * @param date
     *        the date
     * @return true if the given date is equal to or greater than the current date false otherwise.
     * @author Steven Skinner
     */
    public static Boolean isDateGtOrEqualToCurrent(Date date) {
        log.debug("Entering gov.doc.isu.shamen.util.ShamenUtil - method isDateGtOrEqualToCurrent.");
        log.debug("This method checks to see if the given date is equal to or greater than current date.");
        boolean result = false;
        if(date.equals(getCurrentDate()) || date.after(getCurrentDate())){
            result = true;
        }// end if
        log.debug("Exiting gov.doc.isu.shamen.util.ShamenUtil - method isDateGtOrEqualToCurrent. result=" + String.valueOf(result));
        return result;
    }// end isDateGtOrEqualToCurrent

    /**
     * Check to see if the given date is equal to or greater than current date.
     *
     * @param date
     *        the date
     * @return true if the given date is equal to or greater than the current date false otherwise.
     * @author Steven Skinner
     */
    public static Boolean isDateEqualToCurrent(Date date) {
        log.debug("Entering gov.doc.isu.shamen.util.ShamenUtil - method isDateGtOrEqualToCurrent.");
        log.debug("This method checks to see if the given date is equal to or greater than current date.");
        boolean result = false;
        if(date.equals(getCurrentDate())){
            result = true;
        }// end if
        log.debug("Exiting gov.doc.isu.shamen.util.ShamenUtil - method isDateGtOrEqualToCurrent. result=" + String.valueOf(result));
        return result;
    }// end isDateGtOrEqualToCurrent

    /**
     * This method checks id time is one hour greater the the compare time.
     *
     * @param inDate
     *        the time
     * @param compareDate
     *        the time to compare
     * @return Boolean
     */
    public static Boolean isTimeOneHourGreater(Date inDate, Date compareDate) {
        log.debug("Entering isTimeOneHourGreater");
        log.debug("This method checks id time is one hour greater the the compare time.");
        log.debug("Entry parameters are: inDate=" + String.valueOf(inDate) + ", compareDate=" + String.valueOf(compareDate));
        Calendar cal = GregorianCalendar.getInstance();
        Calendar loadCal = GregorianCalendar.getInstance();
        boolean result = true;
        cal.setTime(inDate);
        loadCal.setTime(compareDate);
        loadCal.add(Calendar.HOUR, 1);
        if(cal.getTimeInMillis() == loadCal.getTimeInMillis() || cal.getTimeInMillis() < loadCal.getTimeInMillis()){
            result = false;
        }// end if
        log.debug("Return value is: result=" + String.valueOf(result));
        log.debug("Exiting isTimeOneHourGreater");
        return result;
    }// end isTimeOneHourGreater

    /**
     * This method builds the CSV row for statistical analysis.
     *
     * @param row
     *        the row
     * @param separator
     *        the separator
     * @return String
     */
    public static String buildCSVRow(List<String> row, String separator) {
        log.debug("Entering buildCSVRow");
        log.debug("This method builds the CSV row for statistical analysis.");
        log.debug("Entry parameters are: row=" + String.valueOf(row) + ", separator=" + String.valueOf(separator));
        StringBuffer csv = new StringBuffer();
        for(int i = 0, j = row.size();i < j;i++){
            if(!StringUtil.isNullOrEmpty(csv.toString())){
                csv.append(separator);
            }// endif
            csv.append(row.get(i));
        }// end if
        csv.append("\\");
        csv.append("n");
        String result = csv.toString();
        log.debug("Return value is: result=" + String.valueOf(result));
        log.debug("Exiting buildCSVRow");
        return result;
    }// end buildCSVRow

}// end class
