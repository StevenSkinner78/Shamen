/**
 * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
 */
package gov.doc.isu.shamen.processors;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import gov.doc.isu.gtv.logging.ApplicationLogger;
import gov.doc.isu.shamen.interfaces.Scheduleable;
import gov.doc.isu.shamen.jms.models.JmsSchedule;
import gov.doc.isu.shamen.models.ScheduleEntry;

/**
 * Contains all processor methods for the schedule calculations
 * 
 * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2017
 * @author <strong>Steven Skinner</strong> JCCC, Aug 17, 2020
 * @author <strong>Gary Campbell</strong> JCCC, Jan 12, 2021
 */
public class ShamenScheduleProcessor {
    private static final String MY_CLASS_NAME = "gov.doc.isu.shamen.processors.ShamenScheduleProcessor";
    private static ApplicationLogger appLogger = null;
    private static final String SDFTF = "yyyy-MM-dd HH:mm:ss";
    // please note that this logger is not static in order for all the threads to log properly.
    private Logger myLogger = null;
    private Boolean isThread = false;
    private ShamenThreadSafeProcessor shamenProcessor;

    /**
     * Default constructor with logger instantiation
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
     */
    public ShamenScheduleProcessor() {
        // set standard logger to default.
        myLogger = Logger.getLogger(MY_CLASS_NAME);
        // set up the application logger
        setUpAppLogger();
    }// end ShamenProcessor

    /**
     * Default constructor with applogger instantiation and standard logger instantiation. This is used when this processor is used by one of the threads.
     * 
     * @param inLogger
     *        (required)
     * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
     */
    public ShamenScheduleProcessor(Logger inLogger) {
        isThread = true;
        // set the standard logger
        myLogger = inLogger;
        // set up the application logger
        setUpAppLogger();
    }// end ShamenProcessor

    /**
     * This method sets up the application logger for this class.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 29, 2015
     */
    private void setUpAppLogger() {
        String methodName = "setUpAppLogger";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method sets up the application logger for this class.");
        // Initialize an instance of ApplicationLogger for this class.

        appLogger = ApplicationLogger.getInstance();

        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end setUpAppLogger

    public String getFormattedSchedule() {
        List<Scheduleable> batchAppList = getProcessor().getBatchAppsForController();
        batchAppList.addAll(getProcessor().getBatchCollectionsForController());
        List schedules = getScheduledJobsForController(new Timestamp(System.currentTimeMillis()), batchAppList);
        return formatSchedule(schedules);
    }// end getFormattedSchedule

    private String formatSchedule(List schedules) {
        StringBuffer sb = new StringBuffer();
        sb.append("<table border='1' cellpadding='5'>");
        sb.append("<caption><h1>Schedule Information</h1></caption>");
        if(schedules != null && !schedules.isEmpty()){
            Collections.sort(schedules);
            sb.append("<thead>");
            sb.append("<tr bgcolor='lightgray'>");
            sb.append("<td>");
            sb.append("<strong>Start Time</strong>");
            sb.append("</td>");
            sb.append("<td>");
            sb.append("<strong>Job Name</strong>");
            sb.append("</td>");
            sb.append("</tr>");
            sb.append("</thead>");
            for(int i = 0, j = schedules.size();i < j;i++){
                ScheduleEntry se = (ScheduleEntry) schedules.get(i);
                sb.append("<tr>");
                sb.append("<td>");
                sb.append(toDateFromTimestamp(se.getStartTs()));
                sb.append("</td>");
                sb.append("<td>");
                sb.append(se.getScheduleable().getName());
                sb.append("</td>");
                sb.append("</tr>");
            }// end for
        }else{
            sb.append("<tr>");
            sb.append("No jobs are scheduled to run within 24 hours.");
            sb.append("</tr>");
        }// end if
        sb.append("</table>");
        return sb.toString();
    }// end formatSchedule

    /**
     * This method converts a timestamp to a string date in the "MM/dd/yyyy HH:MM am/pm" format.
     *
     * @param timestamp
     * @return
     */
    public String toDateFromTimestamp(java.sql.Timestamp timestamp) {
        String methodName = "toDateFromTimestamp";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method gets an SQL date and converts it to a string MM/DD/YYYY date.");
        if(timestamp == null){
            return null;
        }// end if
        StringBuffer sb = new StringBuffer();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp.getTime());
        sb.append(cal.get(Calendar.MONTH) + 1);
        sb.append("/");
        sb.append(cal.get(Calendar.DAY_OF_MONTH));
        sb.append("/");
        sb.append(cal.get(Calendar.YEAR));
        sb.append(" ");
        if(cal.get(Calendar.HOUR) == 0){
            sb.append("12");
        }else{
            sb.append(String.valueOf(cal.get(Calendar.HOUR)).length() == 1 ? "0" + cal.get(Calendar.HOUR) : cal.get(Calendar.HOUR));
        }// end if/else
        sb.append(":");
        sb.append(String.valueOf(cal.get(Calendar.MINUTE)).length() == 1 ? "0" + cal.get(Calendar.MINUTE) : cal.get(Calendar.MINUTE));
        sb.append(" ");
        sb.append(cal.get(Calendar.AM_PM) == 0 ? "am" : "pm");
        myLogger.exiting(MY_CLASS_NAME, methodName);
        return sb.toString();
    }// end toDateFromTimestamp

    /**
     * This method controls the instance variable for the processor.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Oct 6, 2015
     */
    private ShamenThreadSafeProcessor getProcessor() {
        if(shamenProcessor == null){
            shamenProcessor = new ShamenThreadSafeProcessor();
        }// end if
        return shamenProcessor;
    }// end getProcessor

    /**
     * This method gets all the scheduled runs starting on the provided start timestamp and extending for 24 hours.
     * 
     * @param startTs
     * @param batchAppList
     * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
     * @return List of BatchApps
     */
    public List getScheduledJobsForController(Timestamp fromTs, List<Scheduleable> batchAppList) {
        String methodName = "getScheduledJobsForController";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method loads all the batch job schedules and returns them as a map");
        // calculate 24 hour range
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(fromTs.getTime());
        cal.add(Calendar.HOUR_OF_DAY, 24);
        Timestamp toTs = new Timestamp(cal.getTimeInMillis());
        List<ScheduleEntry> schedules = new ArrayList<ScheduleEntry>();
        for(int i = 0, j = batchAppList.size();i < j;i++){
            Scheduleable s = batchAppList.get(i);
            if(s != null && s.getName() != null){
                List<Timestamp> schedule = calculateRunTimes(s, fromTs, toTs);
                if(schedule != null && !schedule.isEmpty()){
                    for(int k = 0, l = schedule.size();k < l;k++){
                        if(schedule.get(k).after(fromTs) && schedule.get(k).before(toTs)){
                            ScheduleEntry scheduleEntry = new ScheduleEntry();
                            scheduleEntry.setScheduleable(s);
                            scheduleEntry.setStartTs(schedule.get(k));
                            schedules.add(scheduleEntry);
                        }// end if
                    }// end for
                }// end if
            }// end if
        }// end for
        myLogger.exiting(MY_CLASS_NAME, methodName);
        return schedules;
    }// end getScheduledJobsForController

    /**
     * This method calculates all the run times for a given app within a date range. The date range is mandatory.
     * 
     * @param app
     *        The scheduled app
     * @param fromTs
     *        From Timestamp used in date range.
     * @param toTs
     *        To Timestamp used in date range.
     * @return list of run Timestamps
     * @throws BaseException
     *         if an exception occurred
     */
    private List<Timestamp> calculateRunTimes(Scheduleable app, Timestamp fromTs, Timestamp toTs) {
        String methodName = "calculateRunTimes";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method calculates all the run times for a given app within a date range. The date range is mandatory.");
        List<Timestamp> runTimes = new ArrayList<Timestamp>();
        List<JmsSchedule> schedule = app.getSchedule();
        for(JmsSchedule jmsSchedule : schedule){
            if(jmsSchedule != null && "Y".equals(jmsSchedule.getActive())){
                if(JmsSchedule.FREQUENCY_ONE_TIME.equals(jmsSchedule.getFrequencyCd())){
                    runTimes = getOneTimeRunTimes(jmsSchedule, fromTs, toTs);
                }else if(JmsSchedule.FREQUENCY_DAILY.equals(jmsSchedule.getFrequencyCd())){
                    runTimes = getDailyRunTimes(jmsSchedule, fromTs, toTs);
                }else if(JmsSchedule.FREQUENCY_WEEKLY.equals(jmsSchedule.getFrequencyCd())){
                    runTimes = getWeeklyRunTimes(jmsSchedule, fromTs, toTs);
                }else if(JmsSchedule.FREQUENCY_MONTHLY.equals(jmsSchedule.getFrequencyCd())){
                    runTimes = getMonthlyRunTimes(jmsSchedule, fromTs, toTs);
                }// end if/else
            }// end if
        }// end for
        myLogger.exiting(MY_CLASS_NAME, methodName, runTimes);
        return runTimes;
    }// end calculateRunTimes

    /**
     * This method gets the next run time for one-time jobs for a given range. If none are found, it will just return an empty list.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param schedule
     *        (required)
     * @param fromTs
     *        from range (required)
     * @param toTs
     *        to range (required)
     * @return List<Timestamp>
     */
    private List<Timestamp> getOneTimeRunTimes(JmsSchedule schedule, Timestamp fromTs, Timestamp toTs) {
        String methodName = "getOneTimeRunTimes";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method gets the next run time for one-time jobs for a given range. If none are found, it will just return an empty list.");
        List<Timestamp> runTimes = new ArrayList<Timestamp>();
        Timestamp startTime = null;
        // Load the run time
        startTime = getSqlTSFromDateTimeString(schedule.getScheduleStartDt().toString(), schedule.getStartTime().toString(), SDFTF);
        // for one time only job, if the one time is not in the date range, do not proceed.
        if(startTime.after(fromTs) && startTime.before(toTs)){
            // Check the repeat value and handle accordingly.
            runTimes = handleMultipleRepeats(schedule, startTime);
        }// end if
        myLogger.exiting(MY_CLASS_NAME, methodName, runTimes);
        return runTimes;
    }// end getOneTimeRunTimes

    /**
     * This method gets the next run time for daily jobs for a given range. If none are found, it will just return an empty list.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param schedule
     *        (required)
     * @param fromTs
     *        from range (required)
     * @param toTs
     *        to range (required)
     * @return List<Timestamp>
     */
    private List<Timestamp> getDailyRunTimes(JmsSchedule schedule, Timestamp fromTs, Timestamp toTs) {
        String methodName = "getDailyRunTimes";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method gets the next run time for daily jobs for a given range. If none are found, it will just return an empty list.");
        List<Timestamp> runTimes = new ArrayList<Timestamp>();
        Timestamp startTime = null;
        // Load the run time
        startTime = getSqlTSFromDateTimeString(schedule.getScheduleStartDt().toString(), schedule.getStartTime().toString(), SDFTF);
        // Get all the possible days to run within the specified range.
        List<Timestamp> runDays = handleMultipleRecurs(schedule, startTime, fromTs, toTs);
        // loop through all the possible days and find all the times for each one.
        for(int i = 0, j = runDays.size();i < j;i++){
            // Check the repeat value and handle accordingly.
            runTimes.addAll(handleMultipleRepeats(schedule, runDays.get(i)));
        }// end for
        myLogger.exiting(MY_CLASS_NAME, methodName, runTimes);
        return runTimes;
    }// end getDailyRunTimes

    /**
     * This method gets the next run time for weekly jobs for a given range. If none are found, it will just return an empty list.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param schedule
     *        (required)
     * @param fromTs
     *        from range (required)
     * @param toTs
     *        to range (required)
     * @return list of timestamps
     */
    private List<Timestamp> getWeeklyRunTimes(JmsSchedule schedule, Timestamp fromTs, Timestamp toTs) {
        String methodName = "getWeeklyRunTimes";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method gets the next run time for weekly jobs for a given range. If none are found, it will just return an empty list.");
        List<Timestamp> runTimes = new ArrayList<Timestamp>();
        // Load the run time
        Timestamp startTs = getSqlTSFromDateTimeString(schedule.getScheduleStartDt().toString(), schedule.getStartTime().toString(), SDFTF);
        // Get all the pertinent weekdays to use for calculations.
        List<Timestamp> runDays = calculateMultipleWeekdays(schedule, startTs, fromTs, toTs);
        // loop through all the possible days and find all the times for each one.
        for(int i = 0, j = runDays.size();i < j;i++){
            // Check the repeat value and handle accordingly.
            runTimes.addAll(handleMultipleRepeats(schedule, runDays.get(i)));
        }// end for
        myLogger.exiting(MY_CLASS_NAME, methodName, runTimes);
        return runTimes;
    }// end getWeeklyRunTimes

    /**
     * This method gets the next run time for monthly jobs for a given range. If none are found, it will just return an empty list.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param schedule
     *        (required)
     * @param fromTs
     *        from range (required)
     * @param toTs
     *        to range (required)
     * @return list of timestamps
     */
    private List<Timestamp> getMonthlyRunTimes(JmsSchedule schedule, Timestamp fromTs, Timestamp toTs) {
        String methodName = "getMonthlyRunTimes";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method gets the next run time for weekly jobs for a given range. If none are found, it will just return an empty list.");
        List<Timestamp> runTimes = new ArrayList<Timestamp>();
        // Load the run time
        Timestamp startTs = getSqlTSFromDateTimeString(schedule.getScheduleStartDt().toString(), schedule.getStartTime().toString(), SDFTF);
        // Get all the pertinent weekdays to use for calculations.
        List<Timestamp> runDays = calculateMultipleMonthdays(schedule, startTs, fromTs, toTs);
        // loop through all the possible days and find all the times for each one.
        for(int i = 0, j = runDays.size();i < j;i++){
            // Check the repeat value and handle accordingly.
            runTimes.addAll(handleMultipleRepeats(schedule, runDays.get(i)));
        }// end for
        myLogger.exiting(MY_CLASS_NAME, methodName, runTimes);
        return runTimes;
    }// end getWeeklyRunTimes

    /**
     * This method calculates all the jobs repeats for a given day starting at a certain time.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2017
     * @param schedule
     *        (required)
     * @param runTs
     *        (required)
     * @return List<Timestamp>
     */
    private List<Timestamp> handleMultipleRepeats(JmsSchedule schedule, Timestamp runTs) {
        String methodName = "handleMultipleRepeats";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method calculates all the jobs repeats for a given day.");
        Timestamp repeatRun = runTs;
        List<Timestamp> runTimes = new ArrayList<Timestamp>();

        Calendar calculationDay = Calendar.getInstance();
        calculationDay.setTimeInMillis(runTs.getTime());
        // must get rid of the seconds in order to account for thread lag.
        calculationDay.set(Calendar.SECOND, 0);
        calculationDay.set(Calendar.MILLISECOND, 0);

        // Set up the time increments by the repeat code value.
        Calendar nextRunCalendar = Calendar.getInstance();
        nextRunCalendar.setTimeInMillis(runTs.getTime());
        // If the original fits, add the original time to it
        if(nextRunCalendar.after(calculationDay) || nextRunCalendar.equals(calculationDay)){
            runTimes.add((Timestamp) runTs.clone());
        }// end if
         // only perform calculations if necessary
        if(schedule.getRepeatCd() != null && !"BLA".equals(schedule.getRepeatCd())){
            Integer increment = 0;
            Integer incrementType = 0;
            if(JmsSchedule.REPEAT_TYPE_30_MINUTE.equals(schedule.getRepeatCd())){
                increment = 30;
                incrementType = Calendar.MINUTE;
            }else if(JmsSchedule.REPEAT_TYPE_15_MINUTE.equals(schedule.getRepeatCd())){
                increment = 15;
                incrementType = Calendar.MINUTE;
            }else if(JmsSchedule.REPEAT_TYPE_10_MINUTE.equals(schedule.getRepeatCd())){
                increment = 10;
                incrementType = Calendar.MINUTE;
            }else if(JmsSchedule.REPEAT_TYPE_05_MINUTE.equals(schedule.getRepeatCd())){
                increment = 05;
                incrementType = Calendar.MINUTE;
            }else if(JmsSchedule.REPEAT_TYPE_01_MINUTE.equals(schedule.getRepeatCd())){
                increment = 01;
                incrementType = Calendar.MINUTE;
            }else if(JmsSchedule.REPEAT_TYPE_1_HOUR.equals(schedule.getRepeatCd())){
                increment = 1;
                incrementType = Calendar.HOUR;
            }else if(JmsSchedule.REPEAT_TYPE_2_HOUR.equals(schedule.getRepeatCd())){
                increment = 2;
                incrementType = Calendar.HOUR;
            }else if(JmsSchedule.REPEAT_TYPE_3_HOUR.equals(schedule.getRepeatCd())){
                increment = 3;
                incrementType = Calendar.HOUR;
            }else if(JmsSchedule.REPEAT_TYPE_4_HOUR.equals(schedule.getRepeatCd())){
                increment = 4;
                incrementType = Calendar.HOUR;
            }// end else-if
             // Take the base start time and add the proper increments until the time is greater than current time or it moves the day that was set by the repeat function.
            int i = 0;
            while(true){
                nextRunCalendar.add(incrementType, increment);
                if(nextRunCalendar.get(Calendar.DAY_OF_YEAR) > calculationDay.get(Calendar.DAY_OF_YEAR)){
                    break;
                }// end if
                if(nextRunCalendar.after(calculationDay) || nextRunCalendar.equals(calculationDay)){
                    repeatRun.setTime(nextRunCalendar.getTimeInMillis());
                    runTimes.add((Timestamp) repeatRun.clone());
                }// end if
                i = i + 1;
            }// end while
        }// end if
        myLogger.exiting(MY_CLASS_NAME, methodName, runTimes);
        return runTimes;
    }// end handleMultipleRepeats

    /**
     * This method loads a list of all the days this job should run within a specified range. This is for Weekly type jobs.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2017
     * @param schedule
     *        (required)
     * @param startTs
     *        schedule start (required)
     * @param fromTs
     *        from range (required)
     * @param toTs
     *        to range (required)
     * @return List<Timestamp>
     */
    private List<Timestamp> calculateMultipleWeekdays(JmsSchedule schedule, Timestamp startTs, Timestamp fromTs, Timestamp toTs) {
        String methodName = "calculateMultipleWeekdays";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method loads a list of all the days this job should run within a specified range.  This is for Weekly type jobs.");
        List<Timestamp> returnTimes = new ArrayList<Timestamp>();
        // Insure that the weekdays has a value
        if(schedule.getWeekdays() != null){
            // make sure that schedule is not set to start after the date range.
            if(!startTs.after(toTs)){
                Calendar fromCalendar = Calendar.getInstance();
                fromCalendar.setTimeInMillis(fromTs.getTime());
                // must get rid of the seconds for calculation purposes.
                fromCalendar.set(Calendar.SECOND, 0);
                fromCalendar.set(Calendar.MILLISECOND, 0);
                // Take the base start time and add the proper increments until it's in range
                Calendar nextRunCalendar = Calendar.getInstance();
                nextRunCalendar.setTimeInMillis(startTs.getTime());
                Integer increment = 1;
                Integer incrementType = Calendar.DAY_OF_YEAR;
                Date nextRunDate = new Date(nextRunCalendar.getTimeInMillis());
                Date fromDate = clearSQLTimeFields(new Date(fromCalendar.getTimeInMillis()));
                ArrayList<Integer> weekDayArray = schedule.getWeekdaysAsArrayList();
                // add the proper increments until the time is future.
                while(true){

                    Integer runDay = nextRunCalendar.get(Calendar.DAY_OF_WEEK);
                    nextRunDate = clearSQLTimeFields(new Date(nextRunCalendar.getTimeInMillis()));
                    // if calculated next run date is in date range, use it.
                    if((nextRunDate.after(fromDate) || nextRunDate.equals(fromDate)) && nextRunDate.before(toTs)){
                        if(weekDayArray.contains(runDay)){
                            returnTimes.add(new Timestamp(nextRunCalendar.getTimeInMillis()));
                        }// end if
                    }// end if-else
                     // if the calculated next run date is past the date range, leave.
                    if(nextRunDate.after(toTs)){
                        break;
                    }// end if
                    nextRunCalendar.add(incrementType, increment);
                }// end while
            }// end if
        }// end if
        myLogger.exiting(MY_CLASS_NAME, methodName, returnTimes);
        return returnTimes;
    }// end calculateMultipleWeekdays

    /**
     * This method loads a list of all the days this job should run within a specified range. This is for Monthly type jobs.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2017
     * @param schedule
     *        (required)
     * @param startTs
     *        schedule start (required)
     * @param fromTs
     *        from range (required)
     * @param toTs
     *        to range (required)
     * @return List<Timestamp>
     */
    private List<Timestamp> calculateMultipleMonthdays(JmsSchedule schedule, Timestamp startTs, Timestamp fromTs, Timestamp toTs) {
        String methodName = "calculateMultipleMonthdays";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method loads a list of all the days this job should run within a specified range.  This is for Monthly type jobs.");
        List<Timestamp> returnTimes = new ArrayList<Timestamp>();
        // make sure that schedule is not set to start after the date range.
        if(!startTs.after(toTs)){
            // set up calendar with from range
            Calendar fromCalendar = Calendar.getInstance();
            fromCalendar.setTimeInMillis(fromTs.getTime());
            fromCalendar = clearCalendarTimeFieldsForCal(fromCalendar);
            // set up calendar with to range
            Calendar toCalendar = Calendar.getInstance();
            toCalendar.setTimeInMillis(toTs.getTime());
            toCalendar = clearCalendarTimeFieldsForCal(toCalendar);
            Calendar calculationDate = Calendar.getInstance();
            calculationDate.setTimeInMillis(startTs.getTime());
            Integer increment = 1;
            Integer incrementType = Calendar.DAY_OF_YEAR;
            Integer calculationDay;
            Integer weekNumber;
            if(JmsSchedule.FREQUENCY_TYPE_MONTH_WITH_WEEKDAY.equals(schedule.getFrequencyTypeCd())){
                Integer lastWeekOfMonth;
                ArrayList<Integer> weekNumberArray = schedule.getWeekNumbersAsArrayList();
                ArrayList<Integer> weekDayArray = schedule.getWeekdaysAsArrayList();
                while(true){
                    calculationDay = calculationDate.get(Calendar.DAY_OF_WEEK);
                    weekNumber = calculationDate.get(Calendar.DAY_OF_WEEK_IN_MONTH);
                    lastWeekOfMonth = calculationDate.getActualMaximum(Calendar.DAY_OF_WEEK_IN_MONTH);
                    // first check to see if it fits the date range
                    if(calculationDate.after(fromCalendar) && calculationDate.before(toCalendar)){
                        // then check if it is the specified week of the month or it's last week of month and that was specified AND if it fits the date range then check for load
                        if(weekNumberArray.contains(weekNumber) || (weekNumber.equals(lastWeekOfMonth) && weekNumberArray.contains(Integer.valueOf(5)))){
                            // then check the day
                            if(weekDayArray.contains(calculationDay)){
                                returnTimes.add(new Timestamp(calculationDate.getTimeInMillis()));
                            }// end if
                        }// end if
                    }// end if
                    calculationDate.add(incrementType, increment);
                    // if the calculated next run date is past the date range, leave.
                    if(calculationDate.after(toCalendar)){
                        break;
                    }// end if
                }// end while
            }else if(JmsSchedule.FREQUENCY_TYPE_DAY_OF_MONTH.equals(schedule.getFrequencyTypeCd())){
                // This type of schedule is when the user picks certain days of of the month
                while(true){
                    calculationDay = calculationDate.get(Calendar.DAY_OF_MONTH);
                    Integer lastDayOfMonth = calculationDate.getMaximum(Calendar.DAY_OF_MONTH);
                    ArrayList<Integer> monthDayArray = schedule.getMonthDaysAsArrayList();
                    // first check to see if it fits the date range
                    if(calculationDate.after(fromCalendar) && calculationDate.before(toCalendar)){
                        // then check if it is one of the days the user chose
                        if(monthDayArray.contains(calculationDay) || (calculationDay.equals(lastDayOfMonth) && monthDayArray.contains(Integer.valueOf(32)))){
                            returnTimes.add(new Timestamp(calculationDate.getTimeInMillis()));
                        }// end if
                    }// end if
                    calculationDate.add(incrementType, increment);
                    // if the calculated next run date is past the date range, leave.
                    if(calculationDate.after(toCalendar)){
                        break;
                    }// end if
                }// end while
            }// end if-else
        }// end if
        myLogger.exiting(MY_CLASS_NAME, methodName, returnTimes);
        return returnTimes;
    }// end calculateMultipleMonthdays

    /**
     * This method loads a list of all the days this job should run within a specified range.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2017
     * @param schedule
     *        (required)
     * @param startTs
     *        schedule start (required)
     * @param fromTs
     *        from range (required)
     * @param toTs
     *        to range (required)
     * @return List<Timestamp>
     */
    private List<Timestamp> handleMultipleRecurs(JmsSchedule schedule, Timestamp startTs, Timestamp fromTs, Timestamp toTs) {
        String methodName = "handleMultipleRepeats";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method loads a list of all the days this job should run within a specified range.");
        List<Timestamp> returnTimes = new ArrayList<Timestamp>();
        // Date startDate = clearSQLTimeFields(getSQLDate(schedule.getScheduleStartDt()));
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTimeInMillis(fromTs.getTime());
        // must get rid of the seconds for calculation purposes.
        fromCalendar.set(Calendar.SECOND, 0);
        fromCalendar.set(Calendar.MILLISECOND, 0);
        // Take the base start time and add the proper increments until the time is greater than current time.
        Calendar nextRunCalendar = Calendar.getInstance();
        nextRunCalendar.setTimeInMillis(startTs.getTime());
        // Insure that the recur is set
        if(schedule.getRecurNo() != null){
            // make sure that schedule is not set to start after the date range.
            if(!startTs.after(toTs)){
                Integer increment = Integer.valueOf(String.valueOf(schedule.getRecurNo()));
                Integer incrementType = null;
                if(JmsSchedule.FREQUENCY_DAILY.equals(schedule.getFrequencyCd())){
                    incrementType = Calendar.DAY_OF_YEAR;
                }else if(JmsSchedule.FREQUENCY_WEEKLY.equals(schedule.getFrequencyCd())){
                    incrementType = Calendar.WEEK_OF_MONTH;
                }// end if-else
                Date nextRunDate = new Date(nextRunCalendar.getTimeInMillis());
                Date currentDate = null;
                nextRunDate = clearSQLTimeFields(new Date(nextRunCalendar.getTimeInMillis()));
                currentDate = clearSQLTimeFields(new Date(fromCalendar.getTimeInMillis()));
                // if original date is in date range, use it.
                if((nextRunDate.after(currentDate) || nextRunDate.equals(currentDate)) && nextRunDate.before(toTs)){
                    returnTimes.add(new Timestamp(nextRunCalendar.getTimeInMillis()));
                }// end if-else
                 // add the proper increments until the time is not in range.
                if(increment != 0){
                    while(true){
                        nextRunCalendar.add(incrementType, increment);
                        nextRunDate = clearSQLTimeFields(new Date(nextRunCalendar.getTimeInMillis()));
                        currentDate = clearSQLTimeFields(new Date(fromCalendar.getTimeInMillis()));
                        // if calculated next run date is in date range, use it.
                        if((nextRunDate.after(currentDate) || nextRunDate.equals(currentDate)) && nextRunDate.before(toTs)){
                            returnTimes.add(new Timestamp(nextRunCalendar.getTimeInMillis()));
                        }// end if-else
                         // if the calculated next run date is past the date range, leave.
                        if(nextRunDate.after(toTs)){
                            break;
                        }// end if
                    }// end while
                }// end if
            }// end if
        }// end if
        myLogger.exiting(MY_CLASS_NAME, methodName, returnTimes);
        return returnTimes;
    }// end handleMultipleRecurs

    /**
     * This method clears out the time portion of a date object for more accurate date comparisons.
     * 
     * @param inputDate
     *        a java.sql.Date object
     * @return the inputDate value with all of the time fields set to zero
     * @author Shane Duncan
     */
    public java.sql.Date clearSQLTimeFields(java.sql.Date inputDate) {
        String methodName = "clearSQLTimeFields";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method clears out the time portion of a date object for more accurate date comparisons.");
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
        myLogger.exiting(MY_CLASS_NAME, methodName, returnDate);
        return returnDate;
    }// end clearSQLTimeFields

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
    public Timestamp getSqlTSFromDateTimeString(String dateStr, String timeStr, String dateTimeFormat) {
        String methodName = "getSqlTSFromDateTimeString";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finer("Parameters: " + new Object[]{dateStr, timeStr, dateTimeFormat});
        myLogger.finest("This method returns timestamp from Date (MM/dd/yyyy) and Time(HH:mm) strings. Returns null in case of a parse exception.");
        java.sql.Timestamp result = null;
        try{
            if(!isNullOrEmpty(dateStr) && !isNullOrEmpty(timeStr)){
                StringBuffer sb = new StringBuffer();
                sb.append(dateStr.trim());
                sb.append(" ").append(timeStr.trim());
                String dateTimeSDF = null;
                if(isNullOrEmpty(dateTimeFormat)){
                    dateTimeSDF = "MM/dd/yyyy HH:mm";
                }else{
                    dateTimeSDF = dateTimeFormat;
                } // end if-else
                java.util.Date date = new SimpleDateFormat(dateTimeSDF).parse(sb.toString());
                result = new java.sql.Timestamp(date.getTime());
            } // end if
        }catch(ParseException e){
            myLogger.severe("Parse exception was encountered while trying to parse: " + dateStr + " " + timeStr + " to format: " + dateTimeFormat);
        } // end catch
        myLogger.exiting(MY_CLASS_NAME, methodName, result);
        return result;
    }// end getSqlTSFromDateTimeString

    /**
     * This method return true if the given {@code String} is null or empty else returns false.
     *
     * @param s
     *        The {@code String} to check.
     * @return True if null or empty, false otherwise.
     */
    public boolean isNullOrEmpty(String s) {
        boolean result = (s == null || s.trim().equals("")) ? true : false;
        return result;
    }// end isNullOrEmpty

    /**
     * This method clears out the time portion of a date object for more accurate date comparisons.
     * 
     * @param inputDate
     *        a Calendar object
     * @return the inputDate value with all of the time fields set to zero
     * @author Shane Duncan
     */
    public Calendar clearCalendarTimeFieldsForCal(Calendar cal) {
        String methodName = "clearCalendarTimeFieldsForCal";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method clears out the time portion of a date object for more accurate date comparisons.");

        // Prevent null pointer exceptions
        if(cal == null){
            return null;
        }// end if
         // Clear out the time portion
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        myLogger.exiting(MY_CLASS_NAME, methodName, cal);
        return cal;
    }// end clearCalendarTimeFieldsForCal

}// end class
