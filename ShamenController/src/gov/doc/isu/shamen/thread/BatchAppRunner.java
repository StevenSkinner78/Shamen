/**
 * @author <strong>Shane Duncan</strong> JCCC, Aug 21, 2015
 */
package gov.doc.isu.shamen.thread;

import gov.doc.isu.gtv.logging.status.StatusMessages;
import gov.doc.isu.gtv.model.StatusMessage;
import gov.doc.isu.gtv.util.ApplicationConstants;
import gov.doc.isu.shamen.core.Constants;
import gov.doc.isu.shamen.interfaces.Scheduleable;
import gov.doc.isu.shamen.jms.ControllerMessage;
import gov.doc.isu.shamen.jms.JmsManager;
import gov.doc.isu.shamen.jms.models.JmsBatchApp;
import gov.doc.isu.shamen.jms.models.JmsBatchAppCollection;
import gov.doc.isu.shamen.jms.models.JmsRunStatus;
import gov.doc.isu.shamen.jms.models.JmsSchedule;
import gov.doc.isu.shamen.models.Properties;
import gov.doc.isu.shamen.models.Status;
import gov.doc.isu.shamen.processors.ShamenThreadSafeProcessor;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * This class will handle invocation of a .bat.
 * 
 * @author <strong>Shane Duncan</strong> JCCC, Aug 21, 2015
 * @author <strong>Steven Skinner</strong> JCCC, Aug 17, 2020
 * @author <strong>Gary Campbell</strong> JCCC, Jan 12, 2021
 */
public class BatchAppRunner extends ControllerThread {
    private Scheduleable batchApp = null;// no getter or setter created intentionally as this thread should not be tampered with post run.
    private Boolean fromSchedule = null;// no getter or setter created intentionally as this thread should not be tampered with post run.
    private JmsRunStatus runStatus = null;// no getter or setter created intentionally as this thread should not be tampered with post run.
    private Timestamp originalStartTs = null;// no getter or setter created intentionally as this thread should not be tampered with post run.
    private Long scheduleRefId = null;// no getter or setter created intentionally as this thread should not be tampered with post run.
    private String vbsPath = null;// no getter or setter created intentionally as this thread should not be tampered with post run.
    private String result = null;// no getter or setter created intentionally as this thread should not be tampered with post run.
    private String resultDetail = null;// no getter or setter created intentionally as this thread should not be tampered with post run.
    private Long runNumber = null;// no getter or setter created intentionally as this thread should not be tampered with post run.
    private String userId = null;// no getter or setter created intentionally as this thread should not be tampered with post run.
    private String jobParameters = null;// no getter or setter created intentionally as this thread should not be tampered with post run.
    private ShamenThreadSafeProcessor shamenProcessor = null;
    private static final String MY_CLASS_NAME = "gov.doc.isu.shamen.thread.BatchAppRunner";
    private Map<Timestamp, StatusMessage> sentStatuses = new TreeMap<Timestamp, StatusMessage>();
    private Timestamp globalJobStart;

    /**
     * Constructor with threadName, bat, and location
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 3, 2015
     * @param threadName
     *        (required)
     * @param batchApp
     *        (required)
     * @param fromSchedule
     *        (required)
     * @param scheduleRefId
     *        (required)
     * @param vbsPath
     *        Path of the vb script helper file.
     */
    public BatchAppRunner(String threadName, Scheduleable batchApp, Boolean fromSchedule, Long scheduleRefId, String vbsPath) {
        super("BATCH_APP_" + threadName);
        myLogger.finer("BatchAppRunner", "Thread started.");
        this.batchApp = batchApp;
        this.fromSchedule = fromSchedule;
        this.scheduleRefId = scheduleRefId;
        this.vbsPath = vbsPath;
    }// end BatchAppRunner

    /**
     * Constructor with threadName, bat, and location
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 3, 2015
     * @param threadName
     *        (required)
     * @param batchApp
     *        (required)
     * @param fromSchedule
     *        (required)
     * @param scheduleRefId
     *        (required)
     * @param vbsPath
     *        Path of the vb script helper file.
     * @param runNumber
     *        run number for this job
     * @param userId
     *        the user who ran the job if it came from a client application
     */
    public BatchAppRunner(String threadName, Scheduleable batchApp, Boolean fromSchedule, Long scheduleRefId, String vbsPath, Long runNumber, String userId) {
        super("BATCH_APP_" + threadName);
        myLogger.finer("BatchAppRunner", "Thread started.");
        this.batchApp = batchApp;
        this.fromSchedule = fromSchedule;
        this.scheduleRefId = scheduleRefId;
        this.vbsPath = vbsPath;
        this.runNumber = runNumber;
        this.userId = userId;
    }// end BatchAppRunner

    /**
     * Constructor with threadName, bat, and location
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 3, 2015
     * @param threadName
     *        (required)
     * @param batchApp
     *        (required)
     * @param fromSchedule
     *        (required)
     * @param scheduleRefId
     *        (required)
     * @param vbsPath
     *        Path of the vb script helper file.
     * @param runNumber
     *        run number for this job
     * @param userId
     *        the user who ran the job if it came from a client application
     * @param jobParameters
     *        if there are job parameters passed in
     */
    public BatchAppRunner(String threadName, Scheduleable batchApp, Boolean fromSchedule, Long scheduleRefId, String vbsPath, Long runNumber, String userId, String jobParameters) {
        super("BATCH_APP_" + threadName);
        myLogger.finer("BatchAppRunner", "Thread started.");
        this.batchApp = batchApp;
        this.fromSchedule = fromSchedule;
        this.scheduleRefId = scheduleRefId;
        this.vbsPath = vbsPath;
        this.runNumber = runNumber;
        this.userId = userId;
        this.jobParameters = jobParameters;
    }// end BatchAppRunner

    /**
     * This method inserts the Run_Status record into the database
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Aug 28, 2015
     * @param batchApp
     *        the one being dealt with
     * @param status
     *        (required)
     *        <ul>
     *        <li>"STD"</li>
     *        <li>"PRO"</li>
     *        <li>"DON"</li>
     *        </ul>
     * @param stopTs
     *        when the job stopped
     * @param description
     *        to accompany the status
     * @param soloJob
     *        is this a stand alone batch
     */
    private void updateRunStatus(JmsBatchApp batchApp, String status, Timestamp stopTs, String description, Boolean soloJob, Timestamp startTs) {
        String methodName = "updateRunStatus";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest(methodName, "This method updates the Run_Status record in the database.");
        // if the run status has not been initialized yet, do so
        if(runStatus == null){
            runStatus = new JmsRunStatus();
            myLogger.finer(methodName, "Set up the Run Status record.");
            runStatus.setBatchAppRefId(batchApp.getBatchAppRefId());
            if((batchApp.getUpdateUserRefId() != null ? batchApp.getUpdateUserRefId() : 0) != 0){
                runStatus.setCreateUserRefId(batchApp.getUpdateUserRefId());
            }else{
                runStatus.setCreateUserRefId(batchApp.getCreateUserRefId());
            }// end if
            if(fromSchedule){
                if(batchApp.getSchedule() != null){
                    runStatus.setScheduleRefId(scheduleRefId);
                }// end if
            }// end if
        }// end if
        runStatus.setStartTs(startTs);
        if(stopTs != null){
            runStatus.setStopTs(stopTs);
        }// end if
        runStatus.setStatusCd(status);
        runStatus.setDescription(description);
        runStatus.setBatchAppRefId(batchApp.getBatchAppRefId());
        runStatus.setUpdateTs(new Timestamp(System.currentTimeMillis()));
        runStatus.setRunStatusRefId(null);
        // Set mainBatchRefId to null to insure that it's dealt with as a solo job.
        if(soloJob){
            runStatus.setMainBatchAppRefId(null);
        }// end if
         // Set the run number for this job. It could be sent in from ShamenWeb, or generated locally.
        runStatus.setRunNumber(Long.valueOf(runNumber));

        if(null == result || "".equals(result)){
            runStatus.setResultCd(JmsRunStatus.RESULTS_PENDING);
        }else{
            runStatus.setResultCd(result);
        }// end if
         // if the userId was sent in and the status is STR, then set the userId
        if(userId != null && JmsRunStatus.STATUS_STARTED.equals(status)){
            runStatus.setResultDetail(userId);
        }else{
            runStatus.setResultDetail(resultDetail);
        }// end if/else
        runStatus.setCreateTs(new Timestamp(System.currentTimeMillis()));
        runStatus.setUpdateTs(null);
        runStatus.setUpdateUserRefId(null);
        myLogger.finer(methodName, "Save the run status record");
        ControllerMessage msg = new ControllerMessage();
        msg.setText(ControllerMessage.UPDATE_RUN_STATUS);
        msg.setRunStatus(runStatus);
        msg.setCorrelationID(String.valueOf(System.currentTimeMillis()));
        msg.setController(getProcessor().getController());
        msg.setReplyTo(true);
        myLogger.finer(methodName, "Write the record to the local db so it is not lost.");
        getProcessor().insertMessage(msg);
        Properties p = getProcessor().getProperties();
        if("true".equals((p.getJmsStayAlive() != null ? p.getJmsStayAlive().trim() : "false"))){
            try{
                myLogger.finer(methodName, "The jms components are working, send the message.");
                JmsManager jms = new JmsManager(myLogger.getChild());
                jms.sendPTP(msg, Constants.SHAMEN_REQUEST_Q);
                jms.cleanUp();
                msg = null;
                jms = null;
            }catch(Exception e){
                myLogger.getChild().log(Level.SEVERE, "An Exception has been caught while trying to send the message to shamen to update the runStatus record. Message is: " + e.getMessage(), e);
            }// end try-catch
        }// end if
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end updateRunStatus

    /**
     * This method inserts the Run_Status record into the database
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Aug 28, 2015
     * @param batchApp
     *        the one being dealt with
     * @param status
     *        (required)
     *        <ul>
     *        <li>"STD"</li>
     *        <li>"PRO"</li>
     *        <li>"DON"</li>
     *        </ul>
     * @param stopTs
     *        when the job stopped
     * @param description
     *        to accompany the status
     */
    private void updateCollectionRunStatus(JmsBatchAppCollection batchApp, String status, Timestamp stopTs, String description, Timestamp startTs) {
        String methodName = "updateCollectionRunStatus";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest(methodName, "This method updates the Run_Status record in the database.");
        // if the run status has not been initialized yet, do so
        if(runStatus == null){
            runStatus = new JmsRunStatus();
            myLogger.finer(methodName, "Set up the Run Status record.");
            runStatus.setBatchAppRefId(batchApp.getMainBatchAppRefId());
            if((batchApp.getUpdateUserRefId() != null ? batchApp.getUpdateUserRefId() : 0) != 0){
                runStatus.setCreateUserRefId(batchApp.getUpdateUserRefId());
            }else{
                runStatus.setCreateUserRefId(batchApp.getCreateUserRefId());
            }// end if
            if(fromSchedule){
                if(batchApp.getSchedule() != null){
                    runStatus.setScheduleRefId(scheduleRefId);
                }// end if
            }// end if
        }// end if
        runStatus.setStartTs(startTs);
        if(stopTs != null){
            runStatus.setStopTs(stopTs);
        }// end if
        runStatus.setStatusCd(status);
        runStatus.setBatchAppRefId(batchApp.getMainBatchAppRefId());
        runStatus.setMainBatchAppRefId(null);
        runStatus.setDescription(description);
        runStatus.setUpdateTs(new Timestamp(System.currentTimeMillis()));
        runStatus.setRunStatusRefId(null);
        // Set the run number for this job. It could be sent in from ShamenWeb, or generated locally.
        runStatus.setRunNumber(Long.valueOf(runNumber));
        if(null == result || "".equals(result)){
            runStatus.setResultCd(JmsRunStatus.RESULTS_PENDING);
        }else{
            runStatus.setResultCd(result);
        }// end if
         // if the userId was sent in and the status is STR, then set the userId
        if(userId != null && JmsRunStatus.STATUS_STARTED.equals(status)){
            runStatus.setResultDetail(userId);
        }else{
            runStatus.setResultDetail(resultDetail);
        }// end if/else
        runStatus.setCreateTs(new Timestamp(System.currentTimeMillis()));
        runStatus.setUpdateTs(null);
        runStatus.setUpdateUserRefId(null);
        myLogger.finer(methodName, "Save the run status record");
        ControllerMessage msg = new ControllerMessage();
        msg.setText(ControllerMessage.UPDATE_RUN_STATUS);
        msg.setRunStatus(runStatus);
        msg.setCorrelationID(String.valueOf(System.currentTimeMillis()));
        msg.setController(getProcessor().getController());
        msg.setReplyTo(true);
        myLogger.finer(methodName, "Write the record to the local db so it is not lost.");
        getProcessor().insertMessage(msg);
        Properties p = getProcessor().getProperties();
        if("true".equals((p.getJmsStayAlive() != null ? p.getJmsStayAlive().trim() : "false"))){
            try{
                myLogger.finer(methodName, "The jms components are working, send the message.");
                JmsManager jms = new JmsManager(myLogger.getChild());
                jms.sendPTP(msg, Constants.SHAMEN_REQUEST_Q);
                jms.cleanUp();
                msg = null;
                jms = null;
            }catch(Exception e){
                myLogger.getChild().log(Level.SEVERE, "An Exception has been caught while trying to send the message to shamen to update the runStatus record. Message is: " + e.getMessage(), e);
            }// end try-catch
        }// end if
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end updateCollectionRunStatus

    /**
     * This method controls the instance variable for the processor.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Oct 6, 2015
     */
    private ShamenThreadSafeProcessor getProcessor() {
        if(shamenProcessor == null){
            shamenProcessor = new ShamenThreadSafeProcessor(myLogger.getChild());
        }// end if
        return shamenProcessor;
    }// end getProcessor

    /*
     * (non-Javadoc)
     * @see gov.doc.isu.shamen.thread.ControllerThread#run() This is an overridden method to start the BatchAppRunner processing.
     */
    @Override
    public void run() {
        String methodName = "run";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest(methodName, "This is an overridden method to start the BatchAppRunner processing.");
        if(batchApp instanceof JmsBatchApp){
            runBatchApp((JmsBatchApp) batchApp);
        }else{
            runBatchAppCollection((JmsBatchAppCollection) batchApp);
        }// end if-else
        myLogger.fine(methodName, "Clean up the DB Connection resources.");
        getProcessor().cleanUp();
        shamenProcessor = null;
        myLogger.exiting(MY_CLASS_NAME, methodName);
        killMyself();

    }// end run

    /**
     * This method runs the .bat via RunTime.getRunTime().exec which may contain any type of processing.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Aug 28, 2015
     * @throws IOException
     * @throws InterruptedException
     */
    private void runBatchApp(JmsBatchApp batchApp) {
        String methodName = "runBatchApp";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest(methodName, "This method runs the .bat via RunTime.getRunTime().exec which may contain any type of processing. The BatchApp is: " + batchApp);
        updateRunStatus(batchApp, JmsRunStatus.STATUS_STARTED, null, null, true, new Timestamp(System.currentTimeMillis()));
        Timestamp holdStartTs = runStatus.getStartTs();
        globalJobStart = holdStartTs;
        String commandString = null;
        Boolean smartyPants = false;
        // Set indicator if not a dumb app.
        if(!JmsBatchApp.TYPE_DUMB.equals(batchApp.getType())){
            smartyPants = true;
        }// end if
         // set job parameters to empty space if it is null;
        if(null == jobParameters){
            jobParameters = " ";
        }// end if

        commandString = "cmd /Q /C CSCRIPT \"" + vbsPath + "\" \"" + batchApp.getFileLocation().trim() + "\\" + batchApp.getFileNm().trim() + "\" \"" + jobParameters + "\"";
        File f = new File(batchApp.getFileLocation());
        myLogger.info(methodName, "Running the Bat via a VB Script: " + commandString);
        Process p;
        Status doneStatus = new Status();
        try{
            p = Runtime.getRuntime().exec(commandString, null, new File(f.getCanonicalPath()));

            // Wait for job to finish if it's smart
            if(smartyPants){
                while(true){
                    TimeUnit.SECONDS.sleep(20);
                    handleStatus(f.getCanonicalPath(), batchApp);
                    doneStatus = checkIfDone(f.getCanonicalPath());
                    if(doneStatus.getDone()){
                        break;
                    }// end if
                }// end while
            }// end if
            p = null;
            // If not smart george, then show unknown results
            if(!smartyPants){
                result = JmsRunStatus.RESULTS_UNKNOWN;
                resultDetail = null;
            }else{
                result = getResult(f.getCanonicalPath());
                resultDetail = "";
            }// end if-else
            runStatus.setStartTs(holdStartTs);
        }catch(IOException e){
            myLogger.getChild().log(Level.SEVERE, "An IOException has been caught while trying to run the batch app. The BatchApp is: " + batchApp + " Message is: " + e.getMessage(), e);
            result = JmsRunStatus.RESULTS_UNSUCCESSFUL;
        }catch(Exception e){
            myLogger.getChild().log(Level.SEVERE, "An Exception has been caught while trying to run the batch app. The BatchApp is: " + batchApp + " Message is: " + e.getMessage(), e);
            result = JmsRunStatus.RESULTS_UNSUCCESSFUL;
        }finally{
            updateRunStatus(batchApp, JmsRunStatus.STATUS_DONE, doneStatus.getTs(), null, true, holdStartTs);
        }// end try-catch
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end runBatchApp

    /**
     * This method pulls status messages from Smart George and publishes them to ShamenWeb.
     * 
     * @param path
     *        path of the batch
     * @param batchApp
     *        batchApp currently being run
     */
    private void handleStatus(String path, JmsBatchApp batchApp) {
        String methodName = "getResult";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest(methodName, "This method pulls status messages from Smart George and publishes them to ShamenWeb. The path is: " + path);
        StatusMessages messages = new StatusMessages(path);

        if(messages != null){
            if(!messages.isEmpty()){
                for(int i = 0, j = messages.size();i < j;i++){
                    // Make sure that the message is not a done and that it's after the job start
                    if(!sentStatuses.containsKey(messages.get(i).getTimestamp()) && !"DONE".equals(messages.get(i).getStatus()) && messages.get(i).getTimestamp().after(globalJobStart)){
                        updateRunStatus(batchApp, JmsRunStatus.STATUS_PROCESSING, null, messages.get(i).getMessage(), false, messages.get(i).getTimestamp());
                        sentStatuses.put(messages.get(i).getTimestamp(), messages.get(i));
                    }// end if
                }// end for
            }// end if
        }// end if
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end handleStatus

    /**
     * This method will check to see if the job is done using George 2.0 statusLogger.
     * 
     * @param path
     *        path of the batch
     * @return result
     */
    private Status checkIfDone(String path) {
        String methodName = "checkIfDone";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest(methodName, "This method will check to see if the job is done. The path is: " + path);
        StatusMessages messages = new StatusMessages(path);
        Status status = new Status();
        status.setDone(false);
        // If the messages is empty, then something sinister and unseemly has happened. Report that the job is done.
        if(messages != null){
            if(!messages.isEmpty()){
                StatusMessage m = messages.getLast();
                if(ApplicationConstants.STATUS_DONE.equals(m.getStatus()) && m.getTimestamp().after(globalJobStart)){
                    status.setDone(true);
                    status.setTs(m.getTimestamp());
                }// end if
            }else{
                status.setDone(true);
                status.setTs(new Timestamp(System.currentTimeMillis()));
            }// end else-if
        }// end if
        myLogger.exiting(MY_CLASS_NAME, methodName, result);
        return status;
    }// end checkIfDone

    /**
     * This method will get the result of the batch job run and return it accordingly.
     * 
     * @param path
     *        path of the batch
     * @return result
     */
    private String getResult(String path) {
        String methodName = "getResult";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest(methodName, "This method will get the result of the batch job run and return it accordingly. The path is: " + path);
        StatusMessages messages = new StatusMessages(path);
        String result = JmsRunStatus.RESULTS_UNKNOWN;
        Boolean freshRunDetected = false;
        if(messages != null){
            if(!messages.isEmpty()){
                StatusMessage m = null;
                // loop through all statuses, looking for any errors. If none, then it was
                // successful
                for(int i = 0, j = messages.size();i < j;i++){
                    m = messages.get(i);
                    if(m.getTimestamp().after(globalJobStart)){
                        freshRunDetected = true;
                        if(ApplicationConstants.STATUS_DONE.equals(m.getStatus())){
                            myLogger.info(methodName, "Successful completion of the job was detected.");
                            result = JmsRunStatus.RESULTS_SUCCESSFUL;
                        }else if(ApplicationConstants.STATUS_ERROR.equals(m.getStatus())){
                            myLogger.info(methodName, "Unsuccessful completion of the job was detected.");
                            result = JmsRunStatus.RESULTS_UNSUCCESSFUL;
                            break;
                        }// end if-else
                    }// end if
                }// end for
            }else{// if there are no messages then something bad happened
                myLogger.info(methodName, "No statuses were found so therefore it is an unsuccessful completion of the job.");
                result = JmsRunStatus.RESULTS_UNSUCCESSFUL;
            }// end if-else
        }// end if
         // if no fresh statuses were found, the job was not successful
        if(!freshRunDetected){
            myLogger.info(methodName, "No fresh statuses were found so therefore it is an unsuccessful completion of the job.");
            result = JmsRunStatus.RESULTS_UNSUCCESSFUL;
        }// end if
        myLogger.exiting(MY_CLASS_NAME, methodName, result);
        return result;
    }// end getResult

    // /**
    // * This method will get the log of the recently finished job.
    // *
    // * @param path
    // * path of the batch
    // * @return log
    // */
    // private String getLog(String path) {
    // String methodName = "getLogPath";
    // myLogger.entering(MY_CLASS_NAME, methodName);
    // myLogger.finest(methodName, "This method will get the log path for the batch job. The path is: " + path);
    // StatusMessages messages = new StatusMessages(path);
    // String logPath = null;
    // StringBuffer sb = new StringBuffer();
    // if(messages != null){
    // if(!messages.isEmpty()){
    // StatusMessage m = null;
    // // loop through all statuses, looking for the done message which contains the log address.
    // for(int i = 0, j = messages.size();i < j;i++){
    // m = messages.get(i);
    // if(ApplicationConstants.STATUS_DONE.equals(m.getStatus())){
    // logPath = m.getMessage();
    // myLogger.finer(methodName, "Done message found. Log Path is: " + logPath);
    // break;
    // }// end if-else
    // }
    // }// end if
    // // if the log path was found, then get the log and format it.
    // if(logPath != null){
    // List<String> log = FileUtil.readFile(logPath);
    // for(int i = 0, j = log.size();i < j;i++){
    // sb.append(log.get(i)).append(Constants.DEFAULT_NEW_LINE);
    // }// end for
    // }// end if
    // }// end if
    // myLogger.exiting(MY_CLASS_NAME, methodName, result);
    // return sb.toString();
    // }// end getLog

    /**
     * This method runs the .bat via RunTime.getRunTime().exec which may contain any type of processing. It does so in a loop and runs the entire collection of batch applications.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Aug 28, 2015
     * @throws IOException
     * @throws InterruptedException
     */
    private void runBatchAppCollection(JmsBatchAppCollection batchAppCollection) {
        String methodName = "runBatchAppCollection";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest(methodName, "This method runs the .bat via RunTime.getRunTime().exec which may contain any type of processing.  It does so in a loop and runs the entire collection of batch applications.. The BatchAppCollection is: " + batchAppCollection);
        JmsBatchApp batchApp = null;
        originalStartTs = new Timestamp(System.currentTimeMillis());
        updateCollectionRunStatus(batchAppCollection, JmsRunStatus.STATUS_STARTED, null, null, originalStartTs);
        String holdResult = null;
        Status doneStatus = null;
        // set job parameters to empty space if it is null;
        if(null == jobParameters){
            jobParameters = " ";
        }// end if
        for(int i = 0, j = batchAppCollection.getBatchApps().size();i < j;i++){
            sentStatuses.clear();
            // reset the start time for this job
            runStatus.setStartTs(new Timestamp(System.currentTimeMillis()));
            Timestamp holdStartTs = runStatus.getStartTs();
            globalJobStart = holdStartTs;
            // make sure the parent collection's refId is set.
            runStatus.setMainBatchAppRefId(batchAppCollection.getMainBatchAppRefId());
            batchApp = batchAppCollection.getBatchApps().get(i);
            updateRunStatus(batchApp, JmsRunStatus.STATUS_STARTED, null, null, false, new Timestamp(System.currentTimeMillis()));
            String commandString = "cmd /Q /C CSCRIPT \"" + vbsPath + "\" \"" + batchApp.getFileLocation().trim() + "\\" + batchApp.getFileNm().trim() + "\" \"" + jobParameters + "\"";
            File f = new File(batchApp.getFileLocation());
            myLogger.info(methodName, "Running the Bat via a VbScript: " + commandString);
            Process p;
            doneStatus = new Status();
            try{
                p = Runtime.getRuntime().exec(commandString, null, new File(f.getCanonicalPath()));
                // Wait for job to finish
                while(true){
                    TimeUnit.SECONDS.sleep(20);
                    handleStatus(f.getCanonicalPath(), batchApp);
                    doneStatus = checkIfDone(f.getCanonicalPath());
                    if(doneStatus.getDone()){
                        break;
                    }// end if
                }// end while

                p = null;
                // Set the smart result
                result = getResult(f.getCanonicalPath());
                resultDetail = "";
                runStatus.setStartTs(holdStartTs);
            }catch(IOException e){
                myLogger.getChild().log(Level.SEVERE, "An IOException has been caught while trying to run the batch app. The BatchApp is: " + batchApp + " Message is: " + e.getMessage(), e);
                result = JmsRunStatus.RESULTS_UNSUCCESSFUL;
            }catch(Exception e){
                myLogger.getChild().log(Level.SEVERE, "An Exception has been caught while trying to run the batch app. The BatchApp is: " + batchApp + " Message is: " + e.getMessage(), e);
                result = JmsRunStatus.RESULTS_UNSUCCESSFUL;
            }finally{
                updateRunStatus(batchApp, JmsRunStatus.STATUS_DONE, doneStatus.getTs(), null, false, holdStartTs);

            }// end try-catch
            if(!JmsRunStatus.RESULTS_SUCCESSFUL.equals(result)){
                myLogger.getChild().log(Level.SEVERE, "The batch collection member did not complete successfully. Collection run is aborted.  The BatchApp is: " + batchApp);
                holdResult = result;
                break;
            }// end if
            runStatus.setStopTs(null);
            holdResult = result;
            result = null;

        }// end for

        resultDetail = "See component jobs' logs.";
        result = holdResult;
        updateCollectionRunStatus(batchAppCollection, JmsRunStatus.STATUS_DONE, doneStatus.getTs(), null, originalStartTs);
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end runBatchAppCollection
}// end class
