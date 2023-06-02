package gov.doc.isu.shamen.util;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This is a utility class for common methods.
 * 
 * @author <strong>Shane Duncan</strong> JCCC, 08/8/2017
 * @author <strong>Steven Skinner</strong> JCCC, Oct 01, 2019
 */
public class ShamenClientUtil {

    private static Log log = LogFactory.getLog("gov.doc.isu.shamen.util.ShamenClientUtil");
    public static final String DEFAULT_TIMESTAMP = "12/31/7799 00:00:00";
    public static final String DEFAULT_DATE = "12/31/7799";
    public static final String DEFAULT_TIME = "00:00";
    private static final String SDFTF = "MM/dd/yyyy HH:mm:ss";
    public static final String SDFT = "MM/dd/yyyy HH:mm";
    public static final String EMPTY_STRING = "";
    private static final String SDF = "MM/dd/yyyy";
    public static final String DEFAULT_DATE_TIME = "00/00/0000 00:00";

    /* The following methods were copied from the commons API ArrayUtils class. */
    public static final String[] EMPTY_STRING_ARRAY = new String[0];

    /**
     * Formats the Timestamp
     * 
     * @param sqlTime
     *        the passed in Timestamp
     * @return either the formatted Timestamp or null
     */
    public static String getFormattedDateTimeAsString(Timestamp sqlTime) {
        log.debug("Entering getFormattedDateTimeAsString()");
        log.trace("Parameters: sqlTime=" + (sqlTime == null ? "null" : String.valueOf(sqlTime)));
        String result = null;
        if(sqlTime != null){
            result = new SimpleDateFormat(SDFTF).format(sqlTime);
            if(DEFAULT_TIMESTAMP.equalsIgnoreCase(result)){
                result = EMPTY_STRING;
            }// end if
        }// end if
        log.debug("Exiting getFormattedTimeAsString(). The result is: " + (result != null ? result : "null"));
        return result;
    }// end getFormattedDateTimeAsString

    /**
     * Returns a java.sql.Date from a java.util.Date
     * 
     * @param date
     *        date
     * @return java.sql.Date
     */
    public static java.sql.Date getSQLDate(java.util.Date date) {
        return new java.sql.Date(date.getTime());
    }// end getSQLDate

    /**
     * Returns the String representation of input date in MM/dd/yyyy format. Returns empty string if date is null.
     * 
     * @param date
     *        java.sql.Date
     * @return date as string
     * @throws ParseException
     */
    public static String getSqlDateAsString(Date date) {
        log.debug("Entering getDateAsString()");
        log.trace("Parameters: date=" + (date == null ? "null" : String.valueOf(date)));
        String result = EMPTY_STRING;
        if(date != null){
            result = new SimpleDateFormat(SDF).format(date);
        }// end if
        log.debug("Exiting getDateAsString()");
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
        if(timestampString == null){
            return null;
        }// end if
        java.sql.Timestamp ts = null;
        java.util.Date utilDate = getDateWithTime(timestampString);
        ts = new java.sql.Timestamp(utilDate.getTime());
        return ts;
    }// end getSqlTimestamp

    /**
     * Parses input date in "MM/dd/yyyy HH:mm" format.
     * 
     * @param dateWithTime
     *        dateWithTime
     * @return Date object for the input date
     */
    public static java.util.Date getDateWithTime(String dateWithTime) {
        log.debug("Entering getDateWithTime()");
        log.trace("Parameters: dateWithTime=" + (isNullOrEmpty(dateWithTime) ? "null" : String.valueOf(dateWithTime)));
        java.util.Date result = null;
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

        log.debug("Exiting getDateWithTime() result=" + String.valueOf(result));
        return result;
    }// end method

    /**
     * To check for null or empty collection
     *
     * @param col
     *        col
     * @return boolean
     */
    public static boolean isEmpty(Collection<?> col) {
        log.debug("Entering isEmpty");
        log.trace("To check for null or empty collection");
        log.debug("Entry parameters are: col=" + String.valueOf(col));
        boolean result = col == null || col.size() == 0 ? true : false;
        log.debug("Return value is: result=" + String.valueOf(result));
        log.debug("Exiting isEmpty");
        return result;
    }// end isEmpty

    /***
     * This method return true if the given string is null or empty else returns false
     * 
     * @param str
     *        str
     * @return boolean
     */
    public static boolean isNullOrEmpty(String str) {
        log.debug("Entering isNullOrEmpty()");
        log.trace("Parameters are: string=" + String.valueOf(str));
        boolean result = (str == null || str.trim().equals(EMPTY_STRING)) ? true : false;
        log.debug("Exiting isNullOrEmpty()");
        return result;
    }

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
        log.debug("Entering swapStrings.");
        log.debug("Parameters are: inString=" + String.valueOf(inString) + ", from=" + String.valueOf(from) + ", to=" + String.valueOf(to));
        if(inString != null){
            int index = -1;
            index = inString.indexOf(from);
            while(index != -1){
                inString = inString.substring(0, index) + to + inString.substring(index + from.length());
                index = inString.indexOf(from, index + to.length());
            }// end while
        }// end if
        log.debug("Exiting swapStrings. result=" + String.valueOf(inString));
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
        log.debug("Entering textBreak2HtmlBreak.");
        log.debug("Parameters are: inString=" + String.valueOf(inString));
        inString = swapStrings(inString, "\r\n", "<br/>");
        inString = swapStrings(inString, "\n", "<br/>");
        log.debug("Exiting textBreak2HtmlBreak. result=" + String.valueOf(inString));
        return inString;
    }// end method

    /* The following methods were copied from the commons API StringUtils class. */
    /**
     * This method is used to test if the parameter {@code str} is {@code null} or blank.
     *
     * @param str
     *        The {@code String} to test.
     * @return A {@code boolean} flag indicating if the parameter is {@code null} or blank.
     */
    public static boolean isBlank(String str) {
        if((str == null) || ((str.length()) == 0)){
            return true;
        }// end if
        for(int i = 0, j = str.length();i < j; ++i){
            if(!(Character.isWhitespace(str.charAt(i)))){
                return false;
            }// end if
        }// end for
        return true;
    }// end isBlank

    /**
     * This method is used to test if the parameter {@code str} is not {@code null} or blank.
     *
     * @param str
     *        The {@code String} to test.
     * @return A {@code boolean} flag indicating if the parameter is not {@code null} or blank.
     */
    public static boolean isNotBlank(String str) {
        return (!(isBlank(str)));
    }// end isNotBlank

    /**
     * This method is used to split a {@code String} into a {@code String[]}.
     *
     * @param str
     *        The {@code String} to be split.
     * @return The split {@code String}.
     */
    public static String[] split(String str) {
        if(str == null){
            return null;
        }// end if
        int len = str.length();
        if(len == 0){
            return EMPTY_STRING_ARRAY;
        }// end if
        List<String> list = new ArrayList<String>();
        int sizePlus1 = 1;
        int i = 0;
        int start = 0;
        boolean match = false;
        boolean lastMatch = false;
        while(i < len){
            if(Character.isWhitespace(str.charAt(i))){
                if(match){
                    lastMatch = true;
                    if(sizePlus1++ == -1){
                        i = len;
                        lastMatch = false;
                    }// end if
                    list.add(str.substring(start, i));
                    match = false;
                }// end if
                i++;
                start = i;
            }else{
                lastMatch = false;
                match = true;
                i++;
            }// end if/else
        }// end while
        if((match) || (lastMatch)){
            list.add(str.substring(start, i));
        }// end if
        return list.toArray(new String[list.size()]);
    }// end split

    /**
     * Returns the week day name from a day int value.
     *
     * @param day
     *        int
     * @return String
     */
    public static String getDayFromInt(int day) {
        log.debug("Entering getDayFromInt");
        log.trace("Returns the week day name from a day int value.");
        log.debug("Entry parameters are: day=" + String.valueOf(day));
        String result = new DateFormatSymbols().getWeekdays()[day];
        log.debug("Return value is: result=" + String.valueOf(result));
        log.debug("Exiting getDayFromInt");
        return result;
    }// end getDayFromInt

    /**
     * This method gets the day of the week in text for from the Calendar day value.
     * 
     * @param day
     * @return String
     */
    public static String getDayOfWeek(Integer day) {
        log.trace("Entering getDayOfWeek");
        String dayOfWeek = "";
        if(day != null){
            log.debug("Inside if condition with expression: day != null");
            if(Calendar.SUNDAY == day){
                log.debug("Inside if condition with expression: Calendar.SUNDAY == day");
                dayOfWeek = "Sunday";
            }else if(Calendar.MONDAY == day){
                log.debug("Inside if condition with expression: Calendar.MONDAY == day");
                dayOfWeek = "Monday";
            }else if(Calendar.TUESDAY == day){
                log.debug("Inside if condition with expression: Calendar.TUESDAY == day");
                dayOfWeek = "Tuesday";
            }else if(Calendar.WEDNESDAY == day){
                log.debug("Inside if condition with expression: Calendar.WEDNESDAY == day");
                dayOfWeek = "Wednesday";
            }else if(Calendar.THURSDAY == day){
                log.debug("Inside if condition with expression: Calendar.THURSDAY == day");
                dayOfWeek = "Thursday";
            }else if(Calendar.FRIDAY == day){
                log.debug("Inside if condition with expression: Calendar.FRIDAY == day");
                dayOfWeek = "Friday";
            }else if(Calendar.SATURDAY == day){
                log.debug("Inside if condition with expression: Calendar.SATURDAY == day");
                dayOfWeek = "Saturday";
            }// end if
        }// end if
        log.trace("Exiting getDayOfWeek");
        return dayOfWeek;
    }// end getDayOfWeek

    /**
     * Converts a string array of week numbers to a comma separated string of week number names
     *
     * @param sarray
     *        the string array
     * @return String
     */
    public static String convertWeekNumberArray(String[] sarray) {
        log.debug("Entering convertWeekNumberArray");
        log.trace("Converts a string array of week numbers to a comma separated string of week number names");
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
                }

            }// end for
            returnResult = collapseArray(result, false);
        }// end if
        log.debug("Return value is: returnResult=" + String.valueOf(returnResult));
        log.debug("Exiting convertWeekNumberArray");
        return returnResult;
    }// end convertWeekdayArray

    /**
     * Converts an array of Strings to a comma separated line
     * 
     * @param str
     *        array of Strings to convert
     * @param enquote
     *        if true: each String will be encapsulated in single quotes
     * @return String object
     */
    public static String collapseArray(final String[] str, final boolean enquote) {
        log.debug("Entering gov.doc.isu.dwarf.util.StringUtil - method collapseArray()");
        log.debug("Parameters are: array.length=" + (str == null ? "null" : str.length) + " ,enquote=" + String.valueOf(enquote));
        String retStr = EMPTY_STRING;
        if(str != null && str.length != 0){
            StringBuffer sb = new StringBuffer();
            if(enquote){
                sb.append("'");
            }// end if
            for(int i = 0;i < str.length;i++){
                sb.append(str[i]);
                if(i + 1 < str.length){
                    if(enquote){
                        sb.append("','");
                    }else{
                        sb.append(",");
                    }// end else
                }// end if
            }// end for
            if(enquote){
                sb.append("'");
            }// end if
            retStr = sb.toString();
        }// end if
        log.debug("Exiting gov.doc.isu.dwarf.util.StringUtil - method collapseArray()");
        return retStr;
    }// end method

    /**
     * Converts a string array of weekday numbers to a comma separated string of week day names
     *
     * @param sarray
     *        the string array
     * @return String
     */
    public static String convertWeekdayArray(String[] sarray) {
        log.debug("Entering convertWeekdayArray");
        log.trace("Converts a string array of weekday numbers to a comma separated string of week day names");
        log.debug("Entry parameters are: sarray=" + Arrays.toString(sarray));
        String returnResult = EMPTY_STRING;
        if(sarray != null){
            String[] result = new String[sarray.length];
            for(int i = 0;i < sarray.length;i++){
                result[i] = getDayFromInt(Integer.parseInt(sarray[i]));
            }// end for
            returnResult = collapseArray(result, false);
        }// end if
        log.debug("Return value is: returnResult=" + String.valueOf(returnResult));
        log.debug("Exiting convertWeekdayArray");
        return returnResult;
    }// end convertWeekdayArray

}// end class
