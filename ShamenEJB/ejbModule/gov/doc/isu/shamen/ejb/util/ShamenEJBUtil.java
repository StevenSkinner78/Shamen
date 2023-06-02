package gov.doc.isu.shamen.ejb.util;

import static gov.doc.isu.shamen.ejb.util.EJBConstants.EMPTY_STRING;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Utility class to handle utility method for Shamen EJB .
 *
 * @author Steven L. Skinner sls000is
 */
public class ShamenEJBUtil {

    private static Logger log = Logger.getLogger("gov.doc.isu.shamen.ejb.util.ShamenEJBUtil");
    public static final String DEFAULT_TIMESTAMP = "12/31/7799 00:00:00";
    private static final String STF = "HH:mm";
    private static final String SDF = "MM/dd/yyyy";
    private static final String SDFTF = "MM/dd/yyyy HH:mm:ss";
    public static final String SDFT = "MM/dd/yyyy HH:mm";
    public static final String DEFAULT_DATE = "12/31/7799";
    public static final String DEFAULT_TIME = "00:00";
    public static final String DEFAULT_DATE_TIME = "00/00/0000 00:00";

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
                log.error("Exception occurred trying to convert string to sql date.  The string is: " + dateStr + ", e=" + e1);
            }// end try/catch
        }// end if

        log.debug("Return value is: result=" + String.valueOf(result));
        log.debug("Exiting getSQLDate");
        return result;
    }// end getSQLDate

    /**
     * Converts the hh:mm a Time value to java.sql.Time
     *
     * @param v
     *        timesString
     * @return java.sql.Time
     */
    public static Time getSQLTime(String v) {
        log.debug("Entering getSQLTime");
        log.debug("Converts the hh:mm a Time value to java.sql.Time");
        log.debug("Entry parameters are: v=" + String.valueOf(v));
        Time result = null;
        if((v != null) && !v.equals("") && (v.indexOf(":") != -1)){
            StringBuffer sb = new StringBuffer();
            if(v.indexOf("p") != -1){
                int hold = Integer.valueOf(v.substring(0, v.indexOf(":")));
                hold = hold + 12;
                sb.append(hold).append(":").append(v.substring(v.indexOf(":") + 1, v.indexOf(":") + 3));
                v = sb.toString();
            }else if(v.indexOf("a") != -1 && v.substring(0, 2).equalsIgnoreCase("12")){
                sb.append("00").append(":").append(v.substring(v.indexOf(":") + 1, v.indexOf(":") + 3));
                v = sb.toString();
            }// end if/else
            try{
                result = new Time(new SimpleDateFormat(STF).parse(v.trim()).getTime());
            }catch(ParseException e){
                log.error("Input String cannot be parsed in to time value.. Returning null");
            }// end catch
        }// end if
        log.debug("Return value is: result=" + String.valueOf(result));
        log.debug("Exiting getSQLTime");
        return result;
    }// end method

    /**
     * This method will return a comma separated String for elements in list. It is used for building email to: string.
     *
     * @param list
     *        List<String> of email addresses
     * @return String
     */
    public static String collapseEmailList(Collection<String> list) {
        log.debug("Entering collapseEmailList");
        log.debug("This method will return a comma separated String for elements in list. It is used for building email to: string.");
        log.debug("Entry parameters are: list=" + String.valueOf(list));
        String result = EMPTY_STRING;
        StringBuffer sb = new StringBuffer();
        if(list != null && !list.isEmpty()){
            for(Iterator<String> it = list.iterator();it.hasNext();){
                sb.append(it.next());
                if(it.hasNext()){
                    sb.append(",");
                }// end if
            }// end for
        }else{
            sb.append("");

        }// end if/else
        result = sb.toString();
        log.debug("Return value is: result=" + String.valueOf(result));
        log.debug("Exiting collapseEmailList");
        return result;
    }// end collapseEmailList

    /**
     * This method used to get 12/31/7799 00:00:00 as a Timestamp value
     *
     * @return Timestamp
     */
    public static Timestamp getDefaultTimeStamp() {
        log.debug("Entering getDefaultTimeStamp");
        log.debug("This method used to get 12/31/7799 00:00:00 as a Timestamp value");
        java.util.Date date = null;
        Timestamp result = null;
        try{
            date = new SimpleDateFormat().parse(DEFAULT_TIMESTAMP);
            result = new Timestamp(date.getTime());
        }catch(ParseException e){
            log.debug("ParseException occurred while trying to parse Default Timestamp. Using Calendar Object to establish Default Timestamp.");
            Calendar cal = GregorianCalendar.getInstance();
            cal.set(7799, 11, 31, 0, 0, 0);
            cal.set(Calendar.MILLISECOND, 0);
            result = new Timestamp(cal.getTimeInMillis());
        }// end try/catch
        log.debug("Return value is: result=" + String.valueOf(result));
        log.debug("Exiting getDefaultTimeStamp");
        return result;
    }// end getDefaultTimeStamp

    /**
     * This method used to a timestamp based on value passed in
     *
     * @param value
     *        the amount to figure timestamp by
     * @return Timestamp
     */
    public static Timestamp getTimestampByValue(int value) {
        log.debug("Entering getTimestampByValue");
        log.debug("This method used to a timestamp based on value passed in");
        log.debug("Entry parameters are: value=" + String.valueOf(value));
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.add(Calendar.DAY_OF_MONTH, value);
        Timestamp result = new java.sql.Timestamp(cal.getTimeInMillis());
        log.debug("Return value is: result=" + String.valueOf(result));
        log.debug("Exiting getTimestampByValue");
        return result;
    }// end getTimestampByValue

    /**
     * This method used to a timestamp based on value passed in
     *
     * @param time
     *        the time in Long value
     * @param value
     *        the amount to figure timestamp by
     * @return Timestamp
     */
    public static Boolean compareSystemTimeToTimeByValue(Long time, int value) {
        log.debug("Entering compareSystemTimeToTimeByValue");
        log.debug("This method used to a timestamp based on value passed in");
        log.debug("Entry parameters are: time=" + String.valueOf(time) + ", value=" + String.valueOf(value));
        boolean result = false;
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTimeInMillis(time);
        cal.add(Calendar.MINUTE, value);
        if(System.currentTimeMillis() > cal.getTimeInMillis()){
            result = true;
        }// end if
        log.debug("Return value is: result=" + String.valueOf(result));
        log.debug("Exiting compareSystemTimeToTimeByValue");
        return result;
    }// end compareSystemTimeToTimeByValue

    /**
     * This method used to a timestamp based on long value passed in
     *
     * @param value
     *        the amount to figure timestamp by
     * @return Timestamp
     */
    public static java.sql.Date getDateFromLong(Long value) {
        log.debug("Entering getDateFromLong");
        log.debug("This method used to a timestamp based on long value passed in");
        log.debug("Entry parameters are: value=" + String.valueOf(value));
        java.sql.Date result = new java.sql.Date(value);
        log.debug("Return value is: result=" + String.valueOf(result));
        log.debug("Exiting getDateFromLong");
        return result;
    }// end getDateFromLong

    /**
     * Gets the current date in sql format
     *
     * @return java.sql.Date
     */
    public static java.sql.Timestamp getCurrentTimestamp() {
        log.debug("Entering getCurrentTimestamp");
        log.debug("Gets the current date in sql format");
        java.sql.Timestamp result = new java.sql.Timestamp(System.currentTimeMillis());
        log.debug("Return value is: result=" + String.valueOf(result));
        log.debug("Exiting getCurrentTimestamp");
        return result;
    }// end getCurrentTimestamp

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
        log.debug(" This method will return the system timestamp as a custom formatted string  Pattern: MM/dd/yyyy HH:mm:ss.SSS");
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
     * Calculate time it to in seconds minutes and hours format
     *
     * @param start
     *        start time
     * @return String
     */
    public static String getTimeTookInSecMinHours(long start) {

        log.debug("Entering getTimeTookInSecMinHours");
        log.debug("Calculate time it to in seconds minutes and hours format");
        log.debug("Entry parameters are: start=" + String.valueOf(start));
        long finish = System.currentTimeMillis() - start;

        long seconds = finish / 1000L;
        long hours = seconds / 3600L;
        seconds -= hours * 3600L;
        long minutes = seconds / 60L;
        seconds -= minutes * 60L;
        String result = "Process took " + hours + " hours " + minutes + " minutes and " + seconds + " seconds";
        log.debug("Return value is: result=" + String.valueOf(result));
        log.debug("Exiting getTimeTookInSecMinHours");
        return result;
    }// end getTimeTookInSecMinHours

    /**
     * This method is a null safe trim utility method.
     *
     * @param inString
     *        The String to be trimmed
     * @return trimmed value
     */
    public static String trim(String inString) {
        log.debug("Entering trim");
        log.debug("This method is a null safe trim utility method.");
        log.debug("Entry parameters are: inString=" + String.valueOf(inString));
        String returnString = null;
        if(inString != null){
            returnString = inString.trim();
        }// end if
        log.debug("Return value is: returnString=" + String.valueOf(returnString));
        log.debug("Exiting trim");
        return returnString;
    }// end trim

    /***
     * This method return true if the given string is null or empty else returns false
     *
     * @param str
     *        str
     * @return boolean
     */
    public static boolean isNullOrEmpty(String str) {
        log.debug("Entering isNullOrEmpty");
        log.debug("This method return true if the given string is null or empty else returns false");
        log.debug("Entry parameters are: str=" + String.valueOf(str));
        boolean result = (str == null || str.trim().equals(EMPTY_STRING)) ? true : false;
        log.debug("Return value is: result=" + String.valueOf(result));
        log.debug("Exiting isNullOrEmpty");
        return result;
    }// end isNullOrEmpty

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
        log.debug("Entering swapStrings");
        log.debug("Replaces what it finds in the string with something else.");
        log.debug("Entry parameters are: inString=" + String.valueOf(inString) + ", from=" + String.valueOf(from) + ", to=" + String.valueOf(to));
        if(inString != null){
            int index = -1;
            index = inString.indexOf(from);
            while(index != -1){
                inString = inString.substring(0, index) + to + inString.substring(index + from.length());
                index = inString.indexOf(from, index + to.length());
            }// end while
        }// end if
        log.debug("Return value is: inString=" + String.valueOf(inString));
        log.debug("Exiting swapStrings");
        return inString;
    }// end method

    /**
     * Replaces all newlines (\n) or CR-newlines (\r\n) with html <br>
     * 's
     *
     * @param inString
     *        inString
     * @return String
     */
    public static String textBreak2HtmlBreak(String inString) {
        log.debug("Entering textBreak2HtmlBreak");
        log.debug("Replaces all newlines (\\n) or CR-newlines (\\r\\n) with html  's");
        log.debug("Entry parameters are: inString=" + String.valueOf(inString));
        inString = swapStrings(inString, "\r\n", "<br/>");
        inString = swapStrings(inString, "\n", "<br/>");
        log.debug("Return value is: inString=" + String.valueOf(inString));
        log.debug("Exiting textBreak2HtmlBreak");
        return inString;
    }// end method

    /**
     * This method turns a string into a String array by splitting on delimiter. If no delimiter then default is comma.
     *
     * @param inString
     *        the string to split
     * @param delimiter
     *        the delimiter to split on.
     * @return String[]
     */
    public static String[] toArrayFromString(String inString, String delimiter) {
        log.debug("Entering toArrayFromString");
        log.debug("This method turns a string into a String array by splitting on delimiter. If no delimiter then default is comma.");
        log.debug("Entry parameters are: inString=" + String.valueOf(inString) + ", delimiter=" + String.valueOf(delimiter));
        String[] result = null;
        if(!isNullOrEmpty(inString)){
            if(isNullOrEmpty(delimiter)){
                delimiter = ",";
            }// end if
            result = inString.split(delimiter);

        }// end if
        log.debug("Return value is: result=" + Arrays.toString(result));
        log.debug("Exiting toArrayFromString");
        return result;
    }// end toArrayFromString

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
     * Returns the time difference between two dates in Minutes
     *
     * @param start
     *        The start date
     * @param stop
     *        The stop date
     * @return String
     */
    public static String getTimeDifferenceInMinutes(String start, String stop) {
        log.debug("Entering getTimeDifferenceInMinutes");
        log.debug("Returns the time difference between two dates in Minutes");
        log.debug("Entry parameters are: start=" + String.valueOf(start) + ", stop=" + String.valueOf(stop));
        double seconds;
        double minutes = 0;
        String result;
        if(!isNullOrEmpty(start) && !isNullOrEmpty(stop)){
            seconds = (getDate(stop).getTime() - getDate(start).getTime()) / 1000;
            minutes = seconds / 60;
        }// end if
        result = String.valueOf(minutes);
        log.debug("Return value is: ljGetTimeDifferenceInMinutes=" + String.valueOf(result));
        log.debug("Exiting getTimeDifferenceInMinutes");
        return result;
    }// end getTimeDifference

    /**
     * This method us to convert a string date into a java.util.date.
     *
     * @param dateStr
     *        date
     * @return java.util.Date
     */
    public static Date getDate(String dateStr) {
        log.debug("Entering getDate");
        log.debug("This method us to convert a string date into a java.util.date.");
        log.debug("Entry parameters are: dateStr=" + String.valueOf(dateStr));
        Date result = null;
        try{
            result = new SimpleDateFormat(SDFTF).parse(dateStr);
        }catch(Exception e){
            log.error("Exception occurred trying to convert string to date.  The string is: " + dateStr, e);
        }// end try/catch
        log.debug("Return value is: result=" + String.valueOf(result));
        log.debug("Exiting getDate");
        return result;
    }// end getDate

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
        if(!isNullOrEmpty(start) && !isNullOrEmpty(stop)){

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
     * Returns the time difference between two dates in Hours : Minutes : Seconds format.
     *
     * @param start
     *        The start date
     * @param stop
     *        The stop date
     * @return String
     */
    public static String getTimeDifference(Timestamp start, Timestamp stop) {
        log.debug("Entering getTimeDifference");
        log.debug("Returns the time difference between two dates in Hours : Minutes : Seconds format.");
        log.debug("Entry parameters are: start=" + String.valueOf(start) + ", stop=" + String.valueOf(stop));
        String result = EMPTY_STRING;
        StringBuffer sb = new StringBuffer();
        if(start != null && stop != null){

            long hours, minutes, seconds;
            seconds = stop.getTime() - start.getTime() / 1000;

            hours = seconds / 3600;
            sb.append(hours < 10 ? "0" + hours : hours);
            sb.append(":");
            seconds = seconds - (hours * 3600);

            minutes = seconds / 60;
            sb.append(minutes < 10 ? "0" + minutes : minutes);
            sb.append(":");

            seconds = (seconds - (minutes * 60));
            sb.append(seconds < 10 ? "0" + seconds : seconds);

        }// end if
        result = sb.toString();
        log.debug("Return value is: result=" + String.valueOf(result));
        log.debug("Exiting getTimeDifference");
        return result;
    }// end getTimeDifference

    /**
     * To check for null or zero long
     *
     * @param value
     *        value
     * @return boolean
     */
    public static boolean isNotNullAndZero(Long value) {
        log.debug("Entering isNotNullAndZero");
        log.debug("To check for null or zero long");
        log.debug("Entry parameters are: value=" + String.valueOf(value));
        boolean result = (value != null && value > 0L) ? true : false;
        log.debug("Return value is: result=" + String.valueOf(result));
        log.debug("Exiting isNotNullAndZero");
        return result;
    }// end isNotNullAndZero

    /**
     * To check for null or empty collection
     *
     * @param col
     *        col
     * @return boolean
     */
    public static boolean isEmpty(Collection<?> col) {
        log.debug("Entering isEmpty");
        log.debug("To check for null or empty collection");
        log.debug("Entry parameters are: col=" + String.valueOf(col));
        boolean result = col == null || col.size() == 0 ? true : false;
        log.debug("Return value is: result=" + String.valueOf(result));
        log.debug("Exiting isEmpty");
        return result;
    }// end isEmpty

    /**
     * To check for null or empty Map
     *
     * @param col
     *        col
     * @return boolean
     */
    public static boolean isEmpty(Map<?, ?> col) {
        log.debug("Entering isEmpty");
        log.debug("To check for null or empty Map");
        log.debug("Entry parameters are: col=" + String.valueOf(col));
        boolean result = col == null || col.size() == 0 ? true : false;
        log.debug("Return value is: result=" + String.valueOf(result));
        log.debug("Exiting isEmpty");
        return result;
    }// end isEmpty

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
     * @param date
     *        [Optional] The date in sql format.
     * @param timeStr
     *        [Optional] The time in string format.
     * @param dateTimeFormat
     *        [Optional] The time in string format.
     * @return Timestamp or null if there was a parse exception
     */
    public static Timestamp getSqlTSFromDateTimeString(Date date, String timeStr, String dateTimeFormat) {
        log.debug("Entering getSqlTSFromDateTimeString");
        log.debug("This method returns timestamp from Date (MM/dd/yyyy) and Time(HH:mm) strings. Returns null in case of a parse exception.  If null is passed in for the dateTimeFormat parameter a default format is used. The Default format is: MM/dd/yyyy HH:mm.   NOTE: This method was intended to be called for formatting a date and time for a display table column. ");
        log.debug("Entry parameters are: date=" + String.valueOf(date) + ", timeStr=" + String.valueOf(timeStr) + ", dateTimeFormat=" + String.valueOf(dateTimeFormat));
        java.sql.Timestamp result = null;
        String dateStr = null;
        try{
            dateStr = getDateAsString(date);
            if(!isNullOrEmpty(dateStr) && !isNullOrEmpty(timeStr)){
                StringBuffer sb = new StringBuffer();
                sb.append(dateStr.trim());
                sb.append(" ").append(timeStr.trim());
                String dateTimeSDF = null;
                if(isNullOrEmpty(dateTimeFormat)){
                    dateTimeSDF = SDFT;
                }else{
                    dateTimeSDF = dateTimeFormat;
                } // end if-else
                java.util.Date date2 = new SimpleDateFormat(dateTimeSDF).parse(sb.toString());
                result = new java.sql.Timestamp(date2.getTime());
            } // end if
        }catch(ParseException e){
            log.warn("Parse exception was encountered while trying to parse: " + dateStr + " " + timeStr + " to format: " + dateTimeFormat);
        } // end catch
        log.debug("Return value is: result=" + String.valueOf(result));
        log.debug("Exiting getSqlTSFromDateTimeString");
        return result;
    }// end getSqlTSFromDateTimeString

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
        log.debug("Entering getSqlTSFromDateTimeString");
        log.debug("This method returns timestamp from Date (MM/dd/yyyy) and Time(HH:mm) strings. Returns null in case of a parse exception.  If null is passed in for the dateTimeFormat parameter a default format is used. The Default format is: MM/dd/yyyy HH:mm.   NOTE: This method was intended to be called for formatting a date and time for a display table column. ");
        log.debug("Entry parameters are: dateStr=" + String.valueOf(dateStr) + ", timeStr=" + String.valueOf(timeStr) + ", dateTimeFormat=" + String.valueOf(dateTimeFormat));
        java.sql.Timestamp result = null;
        try{
            if(!isNullOrEmpty(dateStr) && !isNullOrEmpty(timeStr)){
                StringBuffer sb = new StringBuffer();
                sb.append(dateStr.trim());
                sb.append(" ").append(timeStr.trim());
                String dateTimeSDF = null;
                if(isNullOrEmpty(dateTimeFormat)){
                    dateTimeSDF = SDFT;
                }else{
                    dateTimeSDF = dateTimeFormat;
                } // end if-else
                java.util.Date date2 = new SimpleDateFormat(dateTimeSDF).parse(sb.toString());
                result = new java.sql.Timestamp(date2.getTime());
            } // end if
        }catch(ParseException e){
            log.warn("Parse exception was encountered while trying to parse: " + dateStr + " " + timeStr + " to format: " + dateTimeFormat);
        } // end catch
        log.debug("Return value is: result=" + String.valueOf(result));
        log.debug("Exiting getSqlTSFromDateTimeString");
        return result;
    }// end getSqlTSFromDateTimeString

    /**
     * This method returns the string value of the current date in MM/dd/yyyy format.
     *
     * @return String current date
     */
    public static String getDate() {
        log.debug("Entering getDate");
        log.debug("This method returns the string value of the current date in MM/dd/yyyy format.");
        String result = new SimpleDateFormat(SDF).format(new Date());
        log.debug("Return value is: result=" + String.valueOf(result));
        log.debug("Exiting getDate");
        return result;
    }// end getDate

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
        log.debug("Entering getDateAsString");
        log.debug("Returns the String representation of input date in MM/dd/yyyy format. Returns empty string if date is null.");
        log.debug("Entry parameters are: date=" + String.valueOf(date));
        String result = EMPTY_STRING;
        if(date != null){
            result = new SimpleDateFormat(SDF).format(date);
        }// end if
        log.debug("Return value is: result=" + String.valueOf(result));
        log.debug("Exiting getDateAsString");
        return result;
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
        log.debug("Entering clearSQLTimeFields");
        log.debug("This method clears out the time portion of a date object for more accurate date comparisons.");
        log.debug("Entry parameters are: inputDate=" + String.valueOf(inputDate));
        // Prevent null pointer exceptions
        if(inputDate == null){
            log.debug("Return value is: null");
            log.debug("Exiting clearSQLTimeFields");
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
        log.debug("Return value is: returnDate=" + String.valueOf(returnDate));
        log.debug("Exiting clearSQLTimeFields");
        return returnDate;
    }// end clearSQLTimeFields

    /**
     * This method clears out the time portion of a date object for more accurate date comparisons.
     *
     * @param cal
     *        a Calendar object
     * @return the date value with all of the time fields set to zero
     * @author Shane Duncan
     */
    public static long clearCalendarTimeFields(Calendar cal) {
        log.debug("Entering clearCalendarTimeFields");
        log.debug("This method clears out the time portion of a date object for more accurate date comparisons.");
        log.debug("Entry parameters are: cal=" + String.valueOf(cal));
        long returnVal = 0;
        // Prevent null pointer exceptions
        if(cal != null){
            // Clear out the time portion
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            returnVal = cal.getTimeInMillis();
        }
        log.debug("Return value is: returnVal=" + String.valueOf(returnVal));
        log.debug("Exiting clearCalendarTimeFields");
        return returnVal;
    }// end clearCalendarTimeFields

    /**
     * This method clears out the time portion of a date object for more accurate date comparisons.
     *
     * @param cal
     *        a Calendar object
     * @return the Calendar value with all of the time fields set to zero
     * @author Shane Duncan
     */
    public static Calendar clearCalendarTimeFieldsForCal(Calendar cal) {
        log.debug("Entering clearCalendarTimeFieldsForCal");
        log.debug("This method clears out the time portion of a date object for more accurate date comparisons.");
        log.debug("Entry parameters are: cal=" + String.valueOf(cal));
        Calendar result = null;
        // Prevent null pointer exceptions
        if(cal != null){
            result = cal;
            // Clear out the time portion
            result.set(Calendar.HOUR_OF_DAY, 0);
            result.set(Calendar.MINUTE, 0);
            result.set(Calendar.SECOND, 0);
            result.set(Calendar.MILLISECOND, 0);
        }
        log.debug("Return value is: result=" + String.valueOf(result));
        log.debug("Exiting clearCalendarTimeFieldsForCal");
        return result;
    }// end clearCalendarTimeFieldsForCal

    /**
     * Gets the current java.sql.Timestamp
     *
     * @return java.sql.Timestamp
     */
    public static java.sql.Timestamp getSQLTimestamp() {
        log.debug("Entering getSQLTimestamp");
        log.debug("Gets the current java.sql.Timestamp");
        java.sql.Timestamp result = new java.sql.Timestamp(new java.util.Date().getTime());
        log.debug("Return value is: result=" + String.valueOf(result));
        log.debug("Exiting getSQLTimestamp");
        return result;
    }// end method

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
        java.sql.Timestamp ts = null;
        if(!isNullOrEmpty(timestampString)){
            java.util.Date utilDate = getDateWithTime(timestampString);
            ts = new java.sql.Timestamp(utilDate.getTime());
        }
        log.debug("Return value is: ts=" + String.valueOf(ts));
        log.debug("Exiting getSqlTimestamp");
        return ts;
    }// end getSqlTimestamp

    /**
     * Parses input date in "MM/dd/yyyy HH:mm" format.
     *
     * @param dateWithTime
     *        dateWithTime
     * @return Date object for the input date
     */
    public static Date getDateWithTime(String dateWithTime) {
        log.debug("Entering getDateWithTime");
        log.debug("Parses input date in \"MM/dd/yyyy HH:mm\" format.");
        log.debug("Entry parameters are: dateWithTime=" + String.valueOf(dateWithTime));
        Date result = null;
        if(dateWithTime != null && dateWithTime.trim().length() > 0){
            if(dateWithTime.equals(DEFAULT_DATE)){
                dateWithTime = DEFAULT_DATE_TIME;
            }else if(dateWithTime.trim().length() == 10){
                StringBuffer sb = new StringBuffer();
                sb.append(dateWithTime).append("").append(DEFAULT_TIME);
                dateWithTime = sb.toString();
            }// end else
            try{
                result = new SimpleDateFormat(SDFT).parse(dateWithTime);
            }catch(ParseException e){
                log.error("getDateWithTime - Exception parsing date: " + dateWithTime);
            }// end try
        }// end if

        log.debug("Return value is: result=" + String.valueOf(result));
        log.debug("Exiting getDateWithTime");
        return result;
    }// end method

}// end class
