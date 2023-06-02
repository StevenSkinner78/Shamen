/**
 * @author <strong>Shane Duncan</strong> JCCC, Aug 21, 2015
 */
package gov.doc.isu.shamen.thread;

import gov.doc.isu.gtv.util.ApplicationUtil;
import gov.doc.isu.shamen.core.Constants;
import gov.doc.isu.shamen.interfaces.Scheduleable;
import gov.doc.isu.shamen.jms.models.JmsBatchApp;
import gov.doc.isu.shamen.jms.models.JmsBatchAppCollection;
import gov.doc.isu.shamen.jms.models.JmsController;
import gov.doc.isu.shamen.jms.models.JmsSchedule;
import gov.doc.isu.shamen.models.Properties;
import gov.doc.isu.shamen.processors.ShamenThreadSafeProcessor;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class is the thread that processes scheduled batch runs.<br>
 * This class modified to handle multiple schedule objects for a batch application
 * 
 * @author <strong>Shane Duncan</strong> JCCC, Aug 21, 2015
 * @author <strong>Steven Skinner</strong> JCCC, Aug 17, 2020
 * @author <strong>Gary Campbell</strong> JCCC, Jan 12, 2021
 */
public class ScheduleManager extends ControllerThread {
    private static final String MY_CLASS_NAME = "gov.doc.isu.shamen.thread.ScheduleManager";
    private JmsController controller;
    private List<Scheduleable> batchAppList;
    private Map<Long, Timestamp> lastRuns = new HashMap<Long, Timestamp>();
    private ShamenThreadSafeProcessor shamenProcessor;
    private String vbsPath;
    private Timestamp currentTs;

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Aug 31, 2015
     */
    public ScheduleManager(String threadName) {
        super(threadName);
    }// end constructor

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Aug 31, 2015
     */
    public ScheduleManager(String threadName, JmsController controller, String vbsPath) {
        super(threadName);
        this.controller = controller;
        this.vbsPath = vbsPath;
    }// end constructor

    /*
     * (non-Javadoc) This is an overridden method to kick off the PTPListener to listen for point to point messages.
     * @see gov.doc.isu.shamen.thread.ControllerThread#run()
     */
    @Override
    public void run() {
        String methodName = "run";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest(methodName, "This is an overridden method to kick off the ScheduleManager to manage scheduled runs of batch jobs.");
        if(controller != null){
            refreshBatchApps();
        }// end if
        myLogger.finer(methodName, "Initiate the wait loop.");
        Boolean keepGoing = true;
        Properties currentProps;
        try{
            while(keepGoing){
                currentProps = getProperties();
                // Get refresh schedule property. If value true, get new schedule records.
                if(currentProps != null && currentProps.isScheduleRefresh()){
                    myLogger.finer(methodName, "Schedule refresh detected by the schedule manager.");
                    refreshBatchApps();
                    myLogger.finer(methodName, "Set the refresh value back to false.");
                    Properties p = new Properties();
                    p.setScheduleRefresh("false");
                    getProcessor().updateProperties(p);
                }// end if
                 // Do the dirty work of seeing what needs runnin.
                if(controller != null){
                    checkStatus();
                }else{
                    shamenProcessor = getProcessor();
                    controller = shamenProcessor.getController();
                    // getProcessor().cleanUp();
                }// end if-else
                try{
                    // sleep for 2 seconds
                    Thread.sleep(2000);
                }catch(InterruptedException e){
                    myLogger.getChild().severe("InterruptedException caught while trying to sleep in the scheduleManager.  Message is: " + e.getMessage());
                }// end try-catch
                 // Get stayAlive property. If value false or not in the properties, kill thread.
                if(currentProps != null && !currentProps.isControllerStayAlive()){
                    myLogger.finer(methodName, "Killing thread.");
                    keepGoing = false;
                }// end if
            }// end while
        }catch(Exception e){
            myLogger.severe(methodName, "An unexpected exception encountered during normal processing: " + e);
        }// end try-catch
        killMyself();
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end run

    /**
     * This method gets the properties from the DB.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Oct 6, 2015
     * @return
     */
    private Properties getProperties() {
        String methodName = "getProperties";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest(methodName, "This method gets the properties from the DB.");
        Properties p = getProcessor().getProperties();
        // getProcessor().cleanUp();
        myLogger.exiting(MY_CLASS_NAME, methodName, p);
        return p;
    }// end getProperties

    /**
     * This method refreshes the batch app records
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     */
    private void refreshBatchApps() {
        String methodName = "refreshBatchApps";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest(methodName, "This method refreshes the batch app records.");
        shamenProcessor = getProcessor();
        // refresh the controller record
        controller = shamenProcessor.getController();
        myLogger.finer(methodName, "Get all batch apps associated to controller.");
        if(controller != null){
            myLogger.finest(methodName, "Load the batch apps.");
            List<JmsBatchApp> localBatchAppList = shamenProcessor.getBatchAppsForController();
            controller.setBatchApps(localBatchAppList);
            batchAppList = new ArrayList<Scheduleable>();
            batchAppList.addAll(localBatchAppList);
            myLogger.finest(methodName, "Load the batch app collections.");
            List<JmsBatchAppCollection> localBatchAppCollectionList = shamenProcessor.getBatchCollectionsForController();
            myLogger.finest(methodName, "Add the batch app collections to the internal list.");
            batchAppList.addAll(localBatchAppCollectionList);
            controller.setJmsBatchAppCollections(localBatchAppCollectionList);
        }// end if
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end refreshBatchApps

    /**
     * This method returns an instance of the ShamenThreadSafeProcessor
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 29, 2015
     * @return
     */
    private ShamenThreadSafeProcessor getProcessor() {
        String methodName = "getProcessor";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest(methodName, "This method returns an instance of the ShamenThreadSafeProcessor.");
        if(shamenProcessor == null){
            shamenProcessor = new ShamenThreadSafeProcessor(myLogger.getChild());
        }// end if
        myLogger.exiting(MY_CLASS_NAME, methodName, shamenProcessor);
        return shamenProcessor;
    }// end if

    /**
     * This method checks to see if any batch jobs should be run, if it should, then it kicks off the job. <br>
     * -- Modified to handle multiple schedules
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @author <strong>Steven Skinner</strong> JCCC, Aug 17, 2020
     * @author <strong>Gary Campbell</strong> JCCC, Jan 12, 2021
     */
    private void checkStatus() {
        String methodName = "checkStatus";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest(methodName, "This method checks to see if any batch jobs should be run, if it should, then it kicks off the job.");
        currentTs = new Timestamp(System.currentTimeMillis());
        myLogger.finer(methodName, "Loop through all the batch apps and see if they need to be run.");
        if(batchAppList != null){
            for(int i = 0, j = batchAppList.size();i < j;i++){
                for(int k = 0, l = batchAppList.get(i).getSchedule().size();k < l;k++){
                    if(shouldBatchAppBeRun(batchAppList.get(i).getSchedule().get(k))){
                        runBatchApp(batchAppList.get(i), batchAppList.get(i).getSchedule().get(k).getScheduleRefId());
                    }// end if

                }
            }// end for
        }// end if
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end checkStatus

    /**
     * This method receives a batch job object, checks its schedule record and then compares it to the current state of the universe to see if it should be run or not.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @author <strong>Steven Skinner</strong> JCCC, Aug 17, 2020
     * @author --Modified by<strong>Gary Campbell</strong> JCCC, Jan 12, 2021
     * @param batchApp
     *        (required)
     * @return
     */
    private Boolean shouldBatchAppBeRun(JmsSchedule jmsSchedule) {
        String methodName = "shouldBatchAppBeRun";
        myLogger.entering(MY_CLASS_NAME, methodName, jmsSchedule);
        myLogger.finest(methodName, "This method receives a batch job object, checks its schedule record and then compares it to the current state of the universe to see if it should be run or not. ");
        Boolean shouldRun = false;
        // Check for to make sure the schedule is active
        if("Y".equals(jmsSchedule.getActive())){
            Timestamp nextRunTimeTs = getNextRunTimeIfToday(jmsSchedule);
            if(nextRunTimeTs != null){
                Timestamp plusTenSecondsTimeTs = new Timestamp(nextRunTimeTs.getTime() + 10000);
                // Check to see if time to run is in the past but only run if the time is less than ten seconds from the schedule time.
                if(currentTs.after(nextRunTimeTs) && currentTs.before(plusTenSecondsTimeTs)){
                    // Make sure it wasn't already run
                    if(!wasAlreadyRan(jmsSchedule, nextRunTimeTs)){
                        shouldRun = true;
                    }// end if
                }// end if
            }// end if
        }// end if
        myLogger.exiting(MY_CLASS_NAME, methodName, shouldRun);
        return shouldRun;
    }// end shouldBatchAppBeRun

    /**
     * This method checks to see if the batch job has already been run. It does so by checking the last run to see if was within 3 minutes of the proposed run.
     * 
     * @param schedule
     *        schedule object
     * @param proposedRun
     *        The timestamp of the proposed run.
     * @author <strong>Shane Duncan</strong> JCCC, Oct 1, 2015
     */
    private Boolean wasAlreadyRan(JmsSchedule schedule, Timestamp proposedRun) {
        String methodName = "wasAlreadyRan";
        myLogger.entering(MY_CLASS_NAME, methodName, new Object[]{schedule, proposedRun});
        myLogger.finest(methodName, "This method checks to see if the batch job has already been run. It does so by checking the last run to see if was within 3 minutes of the proposed run.");
        Boolean alreadyRan = false;
        Timestamp lastRun;
        // Set the last run timestamp
        if(lastRuns.containsKey(schedule.getScheduleRefId())){
            lastRun = lastRuns.get(schedule.getScheduleRefId());
        }else{
            // If it has never been run, set the date to default.
            lastRun = getSqlTSFromDateTimeString("01/01/1970", "00:01", null);
        }// end if
        if(lastRun != null && proposedRun != null){
            Long difference = lastRun.getTime() - proposedRun.getTime();
            if((difference > 0 && difference < 180000) || (difference < 0 && difference > -180000)){
                alreadyRan = true;
            }// end if
        }// end if
        myLogger.exiting(MY_CLASS_NAME, methodName, alreadyRan);
        return alreadyRan;
    }// wasAlreadyRan

    /**
     * This method creates a BatchAppRunner thread which kicks off a batch job.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param batchApp
     *        (required)
     * @return
     */
    private void runBatchApp(Scheduleable batchApp, Long scheduleRefId) {
        String methodName = "runBatchApp";
        myLogger.entering(MY_CLASS_NAME, methodName, batchApp);
        myLogger.finest(methodName, "This method creates a BatchAppRunner thread which kicks off a batch job.");
        myLogger.info(methodName, "Running batch: " + batchApp.getName());
        ExecutorService executor = Executors.newSingleThreadExecutor();
        // batchApp.getSchedule().setLastRunTs(currentTs);
        lastRuns.put(scheduleRefId, currentTs);
        String runNumberString = String.valueOf(System.currentTimeMillis());
        executor.execute(new Thread(new BatchAppRunner(batchApp.getName() + "_", batchApp, true, scheduleRefId, vbsPath, Long.valueOf(runNumberString.substring(0, 10)), null)));
        // executor.execute(new Thread(new BatchAppRunner(batchApp.getName() + "_", batchApp, true, vbsPath, Long.valueOf(runNumberString))));
        executor.shutdown();
        executor = null;
        batchApp = null;
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end runBatchApp

    /**
     * This method gets the next run time for the current day and the schedule if the job should be run today. Otherwise it will just return null.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param schedule
     *        (required)
     * @return timestamp
     */
    public Timestamp getNextRunTimeIfToday(JmsSchedule schedule) {
        String methodName = "getNextRunTimeIfToday";
        myLogger.entering(MY_CLASS_NAME, methodName, schedule);
        myLogger.finest(methodName, "This method gets the next run time for the current day and the schedule if the job should be run today. Otherwise it will just return null.");
        Timestamp ts = null;
        if(JmsSchedule.FREQUENCY_ONE_TIME.equals(schedule.getFrequencyCd())){
            ts = getNextOneTimeRunTime(schedule);
        }else if(JmsSchedule.FREQUENCY_DAILY.equals(schedule.getFrequencyCd())){
            ts = getNextDailyRunTime(schedule);
        }else if(JmsSchedule.FREQUENCY_WEEKLY.equals(schedule.getFrequencyCd())){
            ts = getNextWeeklyRunTimeIfToday(schedule);
        }else if(JmsSchedule.FREQUENCY_MONTHLY.equals(schedule.getFrequencyCd())){
            ts = getNextMonthlyRunTimeIfToday(schedule);
        }
        myLogger.exiting(MY_CLASS_NAME, methodName, ts);
        return ts;
    }

    /**
     * This method gets the next run time for one-time jobs if the job should be run today. Otherwise it will just return null.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param schedule
     *        (required)
     * @return timestamp
     */
    private Timestamp getNextOneTimeRunTime(JmsSchedule schedule) {
        String methodName = "getNextOneTimeRunTime";
        myLogger.entering(MY_CLASS_NAME, methodName, schedule);
        myLogger.finest(methodName, "This method gets the next run time for one-time jobs if the job should be run today. Otherwise it will just return null.");
        Timestamp ts = null;
        // if the day isn't today, skip the rest of processing.
        if(clearSQLTimeFields(schedule.getScheduleStartDt()).equals(clearSQLTimeFields(new java.sql.Date(currentTs.getTime())))){
            // Load the run time
            ts = getSqlTSFromDateTimeString(schedule.getScheduleStartDt().toString(), schedule.getStartTime().toString(), Constants.SDFTF);
            // Check the repeat value and handle accordingly.
            ts = handleRepeats(schedule, ts);
        }// end if
        myLogger.exiting(MY_CLASS_NAME, methodName, ts);
        return ts;
    }// end getNextOneTimeRunTime

    /**
     * This method gets the next run time for Daily jobs if the job should be run today. Otherwise it will just return null.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param schedule
     *        (required)
     * @return timestamp
     */
    private Timestamp getNextDailyRunTime(JmsSchedule schedule) {
        String methodName = "getNextDailyRunTime";
        myLogger.entering(MY_CLASS_NAME, methodName, schedule);
        myLogger.finest(methodName, "This method gets the next run time for Daily jobs if the job should be run today. Otherwise it will just return null.");
        // Load the run time as if it were today.
        Timestamp runTs = getSqlTSFromDateTimeString(schedule.getScheduleStartDt().toString(), schedule.getStartTime().toString(), Constants.SDFTF);
        // Check the recur and handle next run.
        runTs = handleRecurs(schedule, runTs);
        // If today is a valid run day, check the repeats and handle them as well.
        if(clearSQLTimeFields(new java.sql.Date(runTs.getTime())).equals(clearSQLTimeFields(new java.sql.Date(currentTs.getTime())))){
            runTs = handleRepeats(schedule, runTs);
        }// end if
        myLogger.exiting(MY_CLASS_NAME, methodName, runTs);
        return runTs;
    }// end getNextDailyRunTime

    /**
     * This method determines if the next run time should be adjusted by checking the repeat value. If it does, then it will add the specified increment to it using the last run as its basis.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param schedule
     *        (required)
     * @param runTs
     *        (required)
     * @return timestamp
     */
    private Timestamp handleRecurs(JmsSchedule schedule, Timestamp runTs) {
        String methodName = "handleRecurs";
        myLogger.entering(MY_CLASS_NAME, methodName, schedule);
        myLogger.finest(methodName, "This method determines if the next run time should be adjusted by checking the repeat value. If it does, then it will add the specified increment to it using the last run as its basis.");
        Timestamp recurRun = runTs;
        Date currentDateTest = clearSQLTimeFields(new Date(System.currentTimeMillis()));
        Date startDate = clearSQLTimeFields(schedule.getScheduleStartDt());
        // Insure that the recur is set and that the start date is not today's date.
        if(schedule.getRecurNo() != null && currentDateTest.after(startDate)){
            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.setTimeInMillis(currentTs.getTime());
            // must get rid of the seconds in order to account for thread lag.
            currentCalendar.set(Calendar.SECOND, 0);
            currentCalendar.set(Calendar.MILLISECOND, 0);
            if(!runTs.after(currentTs)){
                // Take the base start time and add the proper increments until the time is greater than current time.
                Calendar nextRunCalendar = Calendar.getInstance();
                nextRunCalendar.setTimeInMillis(runTs.getTime());
                Integer increment = Integer.valueOf(schedule.getRecurNo());
                Integer incrementType = null;
                if(JmsSchedule.FREQUENCY_DAILY.equals(schedule.getFrequencyCd())){
                    incrementType = Calendar.DAY_OF_YEAR;
                }else if(JmsSchedule.FREQUENCY_WEEKLY.equals(schedule.getFrequencyCd())){
                    incrementType = Calendar.WEEK_OF_MONTH;
                }// end if-else
                 // add the proper increments until the time is future.
                while(true){
                    nextRunCalendar.add(incrementType, increment);
                    Date nextRunDate = new Date(nextRunCalendar.getTimeInMillis());
                    Date currentDate = new Date(currentCalendar.getTimeInMillis());
                    // if calculated next run date is after current date or calculated next run date equals the current date, it's the one to
                    // use.
                    if(nextRunDate.after(clearSQLTimeFields(currentDate)) || nextRunDate.equals(clearSQLTimeFields(currentDate))){
                        recurRun.setTime(nextRunCalendar.getTimeInMillis());
                        break;
                    }// end if
                }// end for
            }// end if
        }// end if
        myLogger.exiting(MY_CLASS_NAME, methodName, recurRun);
        return recurRun;
    }// end handleRecurs

    /**
     * This method determines if the next run time should be adjusted by checking the repeat value. If it does, then it will add the specified increment to it using the last run as its basis.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param schedule
     *        (required)
     * @param runTs
     *        (required)
     * @return timestamp
     */
    private Timestamp handleRepeats(JmsSchedule schedule, Timestamp runTs) {
        String methodName = "handleRepeats";
        myLogger.entering(MY_CLASS_NAME, methodName, schedule);
        myLogger.finest(methodName, "This method determines if the next run time should be adjusted by checking the repeat value. If it does, then it will add the specified increment to it using the last run as its basis.");
        Timestamp repeatRun = runTs;
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTimeInMillis(currentTs.getTime());
        // must get rid of the seconds in order to account for thread lag.
        currentCalendar.set(Calendar.SECOND, 0);
        currentCalendar.set(Calendar.MILLISECOND, 0);
        // Check to see if the currently set run time is already runnable. If so, skip processing.
        Timestamp plusOneMinuteTimeTs = new Timestamp(runTs.getTime() + 60000);
        if(!(runTs.after(currentTs)) && !(currentTs.after(runTs) && currentTs.before(plusOneMinuteTimeTs)) && schedule.getRepeatCd() != null){
            // Set up the time increments by the repeat code value.
            Calendar nextRunCalendar = Calendar.getInstance();
            nextRunCalendar.setTimeInMillis(runTs.getTime());
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
             // Take the base start time and add the proper increments until the time is greater than current time.
            for(int i = 0;i < 500;i++){
                nextRunCalendar.add(incrementType, increment);
                if(nextRunCalendar.after(currentCalendar) || nextRunCalendar.equals(currentCalendar)){
                    repeatRun.setTime(nextRunCalendar.getTimeInMillis());
                    break;
                }// end if
            }// end for
        }// end if
        myLogger.exiting(MY_CLASS_NAME, methodName, repeatRun);
        return repeatRun;
    }// end handleRepeats

    /**
     * This method gets the next run time for weekly jobs if today is found as a valid run day.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param schedule
     *        (required)
     * @return timestamp
     */
    private Timestamp getNextWeeklyRunTimeIfToday(JmsSchedule schedule) {
        String methodName = "getNextWeeklyRunTimeIfToday";
        myLogger.entering(MY_CLASS_NAME, methodName, schedule);
        myLogger.finest(methodName, "This method gets the next run time for weekly jobs if today is found as a valid run day.");
        Timestamp ts = null;
        Calendar cal = Calendar.getInstance();
        // Do check for week with day.
        if(JmsSchedule.FREQUENCY_TYPE_WEEK_WITH_DAY.equals(schedule.getFrequencyTypeCd())){
            // Determine if today is a day that requires running.
            Integer today = cal.get(Calendar.DAY_OF_WEEK);
            ArrayList<Integer> weekDayArray = schedule.getWeekdaysAsArrayList();
            if(weekDayArray.contains(today)){
                ts = getSqlTSFromDateTimeString(new java.sql.Date(currentTs.getTime()).toString(), schedule.getStartTime().toString(), Constants.SDFTF);
                // If today is a valid run day, check the repeats and handle them as well.
                if(clearSQLTimeFields(new java.sql.Date(ts.getTime())).equals(clearSQLTimeFields(new java.sql.Date(currentTs.getTime())))){
                    ts = handleRepeats(schedule, ts);
                }// end if
            }// end if
        }// end if
        myLogger.exiting(MY_CLASS_NAME, methodName, ts);
        return ts;
    }// end getNextWeeklyRunTimeIfToday

    /**
     * This method gets the next run time for monthly jobs if the job should be run today. Otherwise it will just return null.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param schedule
     *        (required)
     * @return timestamp
     */
    private Timestamp getNextMonthlyRunTimeIfToday(JmsSchedule schedule) {
        String methodName = "getNextMonthlyRunTimeIfToday";
        myLogger.entering(MY_CLASS_NAME, methodName, schedule);
        myLogger.finest(methodName, "This method gets the next run time for monthly jobs if the job should be run today. Otherwise it will just return null.");
        Timestamp ts = null;
        Calendar cal = Calendar.getInstance();
        // Do check for days of month style schedule.
        if(JmsSchedule.FREQUENCY_TYPE_DAY_OF_MONTH.equals(schedule.getFrequencyTypeCd())){
            // Determine if today is a day that requires running.
            Integer today = cal.get(Calendar.DAY_OF_MONTH);
            Integer lastDayOfMonth = cal.getMaximum(Calendar.DAY_OF_MONTH);
            ArrayList<Integer> monthDayArray = schedule.getMonthDaysAsArrayList();
            // if today is one of the days in the month or if last day of the month was chosen(day = 32), then check to see if today is the last
            // day of the month
            if(monthDayArray.contains(today) || (today.equals(lastDayOfMonth) && monthDayArray.contains(Integer.valueOf(32)))){
                ts = getSqlTSFromDateTimeString(new java.sql.Date(currentTs.getTime()).toString(), schedule.getStartTime().toString(), Constants.SDFTF);
            }// end if
             // Do check for month with Week numbers and days.
        }else if(JmsSchedule.FREQUENCY_TYPE_MONTH_WITH_WEEKDAY.equals(schedule.getFrequencyTypeCd())){
            // Determine if today is a day that requires running.
            Integer today = cal.get(Calendar.DAY_OF_WEEK);
            Integer weekNumber = cal.get(Calendar.DAY_OF_WEEK_IN_MONTH);
            Integer lastWeekOfMonth = cal.getActualMaximum(Calendar.DAY_OF_WEEK_IN_MONTH);
            ArrayList<Integer> weekNumberArray = schedule.getWeekNumbersAsArrayList();
            ArrayList<Integer> weekDayArray = schedule.getWeekdaysAsArrayList();
            // If it is the specified week of the month or it's last week of month and specified
            if(weekNumberArray.contains(weekNumber) || (weekNumber.equals(lastWeekOfMonth) && weekNumberArray.contains(Integer.valueOf(5)))){
                // If the day of the week matches, run that booger
                if(weekDayArray.contains(today)){
                    ts = getSqlTSFromDateTimeString(new java.sql.Date(currentTs.getTime()).toString(), schedule.getStartTime().toString(), Constants.SDFTF);
                }// end if
            }// end if
        }// end else-if
         // If today is a valid run day, check the repeats and handle them as well.
        if(ts != null){
            if(clearSQLTimeFields(new java.sql.Date(ts.getTime())).equals(clearSQLTimeFields(new java.sql.Date(currentTs.getTime())))){
                ts = handleRepeats(schedule, ts);
            }// end if
        }// end if
        myLogger.exiting(MY_CLASS_NAME, methodName, ts);
        return ts;
    }

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
        myLogger.entering(MY_CLASS_NAME, methodName, new Object[]{dateStr, timeStr, dateTimeFormat});
        myLogger.finest(methodName, "This method returns timestamp from Date (MM/dd/yyyy) and Time(HH:mm) strings. Returns null in case of a parse exception.");
        java.sql.Timestamp result = null;
        try{
            if(!ApplicationUtil.isNullOrEmpty(dateStr) && !ApplicationUtil.isNullOrEmpty(timeStr)){
                StringBuffer sb = new StringBuffer();
                sb.append(dateStr.trim());
                sb.append(" ").append(timeStr.trim());
                String dateTimeSDF = null;
                if(ApplicationUtil.isNullOrEmpty(dateTimeFormat)){
                    dateTimeSDF = Constants.SDFT;
                }else{
                    dateTimeSDF = dateTimeFormat;
                } // end if-else
                java.util.Date date = new SimpleDateFormat(dateTimeSDF).parse(sb.toString());
                result = new java.sql.Timestamp(date.getTime());
            } // end if
        }catch(ParseException e){
            myLogger.warning(methodName, "Parse exception was encountered while trying to parse: " + dateStr + " " + timeStr + " to format: " + dateTimeFormat);
        } // end catch
        myLogger.exiting(MY_CLASS_NAME, methodName, result);
        return result;
    }// end getSqlTSFromDateTimeString

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
        // myLogger.entering(MY_CLASS_NAME, methodName, inputDate);
        // myLogger.finest(methodName, "This method clears out the time portion of a date object for more accurate date comparisons.");
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
        // myLogger.exiting(MY_CLASS_NAME, methodName, returnDate);
        return returnDate;
    }// end clearSQLTimeFields

    /**
     * This method clears out the time portion of a timestamp object for more accurate date comparisons.
     * 
     * @param inputDate
     *        a java.sql.Date object
     * @return the inputDate value with all of the time fields set to zero
     * @author Shane Duncan
     */
    public java.sql.Date clearSQLTimestampFields(java.sql.Timestamp inputDate) {
        String methodName = "clearSQLTimestampFields";
        myLogger.entering(MY_CLASS_NAME, methodName, inputDate);
        myLogger.finest(methodName, "This method clears out the time portion of a timestamp object for more accurate date comparisons.");
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
    }// end clearSQLTimestampFields
}// end class
