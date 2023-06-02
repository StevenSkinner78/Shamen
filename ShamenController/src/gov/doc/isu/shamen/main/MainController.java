/**
 * 
 */
package gov.doc.isu.shamen.main;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;

import gov.doc.isu.gtv.core.AbstractApplication;
import gov.doc.isu.gtv.core.UserInterface;
import gov.doc.isu.gtv.core.ifc.IDBConnectivity;
import gov.doc.isu.gtv.core.ifc.IEmailable;
import gov.doc.isu.gtv.email.EmailSender;
import gov.doc.isu.gtv.exception.EmailException;
import gov.doc.isu.gtv.exception.PrepareException;
import gov.doc.isu.gtv.logging.ApplicationLogger;
import gov.doc.isu.gtv.managers.PropertiesMgr;
import gov.doc.isu.gtv.model.CustomProperties;
import gov.doc.isu.gtv.util.ApplicationConstants;
import gov.doc.isu.gtv.util.DateUtil;
import gov.doc.isu.gtv.util.FileUtil;
import gov.doc.isu.shamen.core.Constants;
import gov.doc.isu.shamen.jms.ControllerMessage;
import gov.doc.isu.shamen.jms.JmsManager;
import gov.doc.isu.shamen.jms.models.JmsController;
import gov.doc.isu.shamen.models.Properties;
import gov.doc.isu.shamen.processors.ShamenScheduleProcessor;
import gov.doc.isu.shamen.processors.ShamenThreadSafeProcessor;
import gov.doc.isu.shamen.thread.MessageMaid;
import gov.doc.isu.shamen.thread.PtpListener;
import gov.doc.isu.shamen.thread.ScheduleManager;
import gov.doc.isu.shamen.thread.TopicListener;

/**
 * This is the main controller class for the Shamen application Controller module. This class handles all interactions between the JMS Server and the Batch applications, as well as the interactions from the batch applications back to the JMS Server.
 * 
 * @author <strong>Shane Duncan</strong> JCCC
 * @author <strong>Steven Skinner</strong> JCCC
 */
public class MainController extends AbstractApplication implements IDBConnectivity, IEmailable {
    private static final long serialVersionUID = 1L;
    private static ApplicationLogger applicationLogger;
    private static final String MY_CLASS_NAME = "gov.doc.isu.shamen.main.MainController";
    private static Logger myLogger = Logger.getLogger(MY_CLASS_NAME);
    private ExecutorService executor;
    private Boolean jmsActive = false;
    private ShamenThreadSafeProcessor shamenProcessor;
    private JmsController controller;
    private String vbsPath;

    /**
     * This constructor is required and is used to call the super class which in turn is used to enable the George Framework.
     * 
     * @param args
     *        The <code>String[]</code> of command line arguments.
     * @throws PrepareException
     *         An exception if the command line argument includes the 'PREPARE' argument. The George Framework detects this and will create the external configuration files and force an application exit.
     */
    public MainController(String[] args) throws PrepareException {
        super(args);
    }// end constructor

    /**
     * This method is used for starting a Java application.
     * 
     * @param args
     *        The <code>String[]</code> of command line arguments.
     */
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try{

            // There is no need to call the run method of the class after this as the George Framework will execute it once enabled.
            new MainController(args);
            // Use of the loggers may begin until after the constructor is called.
            myLogger.info("George Framework has been enabled. Calling the controlling class for the application.");
            // Your code should have completed here.
            String end = DateUtil.asTime(System.currentTimeMillis() - start);
            myLogger.info("Controlling class for the application has completed. Running time is " + end);
        }catch(Exception e){
            System.err.println("Exception caught in main! Message is: " + e.getMessage());
            e.printStackTrace(System.out);
            myLogger.severe("Exception caught in main! Message is: " + e.getMessage());
            System.exit(1);
        }// end try/catch
    }// end main
     // public static final OutputStream DEV_NULL = new OutputStream() {
     // public void write(int b) {}
     // };

    /**
     * This method is here to give the PROCRUN something to stop the service.
     * 
     * @param ars
     */
    public static void stop(String[] ars) {
        try{
            myLogger.info("Stopping process");
            stopprocess();
        }catch(Exception e){
            myLogger.info("Exception in stop: " + e.getMessage());
            e.printStackTrace();
        }// end try-catch
    }// end stop

    /**
     * This method stops processing.
     * 
     * @throws Exception
     */
    protected static void stopprocess() throws Exception {
        // Connection con = DBConnection.getConnection();
        // if(con != null){
        // con.close();
        // }// end if
        System.out.println("stopprocess");
        System.exit(0);
    }// end stopprocess

    /*
     * (non-Javadoc)
     * @see gov.doc.isu.gtv.core.AbstractApplication#preprocess()
     */
    @Override
    protected void preprocess() throws Exception {
        // System.setProperty("derby.stream.error.method","MainController.disableDerbyLogFile");
        // System.setProperty("derby.stream.error.field","MainController.DEV_NULL");
        System.setProperty("derby.stream.error.file", "ShamenController/logs/ShamenController/derby_error.log");

        // check for prepMQ to extract need jars
        if(getArguments().length > 0 && Constants.PREPARE_MQ.equalsIgnoreCase(getArguments()[0])){
            prepForMQEnvironment();
        }// end if
        System.out.println("Shamen Controller is initializing...");
        // Include this line to set up the George Framework.
        File[] jars = new File("./mqlib").listFiles();
        if(null == jars || jars.length == 0){
            System.err.println("MQ library was not found in classpath. Application will exit.");
            System.exit(1);
        }// end if
        jars = null;
    }// end preprocess

    /*
     * (non-Javadoc)
     * @see gov.doc.isu.gtv.core.AbstractApplication#process()
     */
    @Override
    protected void process() throws Exception {
        System.out.println("process");

        applicationLogger = ApplicationLogger.getInstance();

    }// end process

    /**
     * This method extracts jars from application to be external for the J2SE application to communicate with the the WepSphere server. It exits from the application after completion.
     */
    private static void prepForMQEnvironment() {
        try{
            System.out.println("Extracting WebSphere and MQ jars.");
            JarExtractor ext = new JarExtractor(true, ".jar");
            ext.extractInternalFilesToExternalDirectory(new File("./mqlib"));
        }catch(Exception e){
            System.err.println("Exception caught while trying to extract WebSphere and MQ jars! Message is: " + e.getMessage());
            e.printStackTrace(System.out);
        }// end try/catch
        System.exit(1);
    }// end prepForMQEnvironment

    /**
     * This method sets up a default controller record to be used internally. It also attempts to set up the Jms stuff. If Jms is not available, it will wait until such a time that it comes on line.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Oct 5, 2015
     * @throws SQLException
     */
    private void initializeController() throws SQLException {
        String methodName = "setDefaultController";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method sets up a default controller record to be used internally. It also attempts to set up the Jms stuff. If Jms is not available, it will wait until such a time that it comes on line.");
        // clear the local DB
        clearDB();
        // create a new DB
        getProcessor().createDB();
        // Initialize the internal properties for dynamic jms connection detection. This is not what George usually does but is required.
        PropertiesMgr.getProperties().setProperty(Constants.JMS_STAY_ALIVE, "true");
        PropertiesMgr.getProperties().setProperty(Constants.CONTROLLER_STAY_ALIVE, "true");
        java.util.Properties prop = System.getProperties();
        // initialize the properties for later use.
        getProcessor().initializePropertiesAndRunStatus();
        // if the property is set for this to run independent of the main DB, then set up the internal db.
        myLogger.finer("Set up the internal db.");
        getProcessor().initializeInternalDB();
        JmsController c = new JmsController();
        c.setControllerAddress(getProperties().getControllerName());
        c.setControllerName("DEFAULT");
        c.setControllerRefId(Long.valueOf(0));
        c.setQueueRefId(Long.valueOf(0));
        getProcessor().insertController(c);
        PropertiesMgr.getProperties().setProperty(Constants.MESSAGE_SELECTOR, c.getControllerAddress().toUpperCase());
        myLogger.finer("Set up Jms.");
        while(!(jmsActive != null ? jmsActive : false)){
            setUpJMS();
            if(!jmsActive){
                myLogger.finer("Jms is not available for initialization.  Waiting and will try again.");
            }else{
                myLogger.finer("Jms initialized successfully.  We are ready to rock.");
                break;
            }// end if
            try{
                Thread.sleep(30000);
            }catch(InterruptedException e){
                myLogger.severe("Exception occurred trying to put the jms activator loop temporarily to sleep.  Message is: " + e.getMessage());
            }// end try-catch
        }// end while
         // Finally, request the controller record from Shamen
        if(!getControllerFromShamen()){
            myLogger.severe("Unable to get the controller record from Shamen.  Controller is ending.");
            closeLoggers();
            System.exit(-1);
        }// end if
        myLogger.fine("Set up the Visual Basic Script file to handle dummy type batch invocations.");
        setUpVbScript();
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end setDefaultController

    /**
     * This method gets controller record from the Shamen Application. It will wait indefinitely for the answer from Shamen as it cannot function without this information. If Shamen does not find a record, it will answer as such, causing the controller to shutdown.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Oct 6, 2015
     * @return
     */
    private Boolean getControllerFromShamen() {
        String methodName = "getControllerFromShamen";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method gets controller record from the Shamen Application.  It will wait indefinitely for the answer from Shamen as it cannot function without this information.  If Shamen does not find a record, it will answer as such, causing the controller to shutdown.");
        Boolean gotController = false;
        while(!gotController){
            try{
                JmsManager jms = new JmsManager(myLogger);
                ControllerMessage msg = new ControllerMessage();
                msg.setText(ControllerMessage.GIVE_ME_CONTROLLER);
                msg.setCorrelationID(String.valueOf(System.currentTimeMillis()));
                msg.setController(controller);
                ControllerMessage receivedMsg = jms.sendPTPWithAcknowledge(Constants.SHAMEN_REQUEST_Q, msg, 30000);

                jms.cleanUp();
                if(receivedMsg != null){
                    // If the message was received but no record was found, force into wait loop.
                    if(!ControllerMessage.NO_SUCH_RECORD.equals(receivedMsg.getText())){
                        if(receivedMsg.getController() != null){
                            myLogger.finer("Got the controller and am ready to roll.");
                            getProcessor().initializeInternalDB();
                            getProcessor().insertController(receivedMsg.getController());
                            controller = receivedMsg.getController();
                            PropertiesMgr.getProperties().setProperty(Constants.MESSAGE_SELECTOR, controller.getControllerAddress().toUpperCase());
                            gotController = true;
                            break;
                        }// end if
                    }else{
                        myLogger.info("No record was returned from ShamenWeb, will try again in 30 seconds.");
                    }// end if
                }// end if
            }catch(Exception e){
                myLogger.severe("Exception occurred while trying to get the Controller record from Shamen.  Message is: " + e.getMessage());
            }// end try-catch
            try{
                myLogger.finer("Going to sleep for 30 seconds.");
                // Thread.sleep(120000);
                TimeUnit.SECONDS.sleep(30);// Sleep for 30 seconds at a time.
            }catch(InterruptedException e){
                myLogger.severe("Exception occurred while trying to sleep during get loop for the Controller record from Shamen.  Message is: " + e.getMessage());
            }// end try-catch
        }// end while
        myLogger.exiting(MY_CLASS_NAME, methodName, gotController);
        return gotController;
    }// end getControllerFromShamen

    /**
     * This method gets the properties from the DB.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Oct 6, 2015
     * @return Properties
     */
    private Properties getProperties() {
        String methodName = "getProperties";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method gets the properties from the DB.");
        Properties p = getProcessor().getProperties();
        // Check to see if the properties file values were changed. If they were make the internal DB reflect the
        // change and reset them to defaults.
        String stayAlive = PropertiesMgr.getProperties().getProperty(Constants.CONTROLLER_STAY_ALIVE);
        String jmsStayAlive = PropertiesMgr.getProperties().getProperty(Constants.JMS_STAY_ALIVE);
        if(!"true".equals(stayAlive) || !"true".equals(jmsStayAlive)){
            if(!"true".equals(stayAlive)){
                p.setControllerStayAlive("false");
                PropertiesMgr.getProperties().setProperty(Constants.CONTROLLER_STAY_ALIVE, "true");
            }// end if
            if(!"true".equals(jmsStayAlive)){
                p.setJmsStayAlive("false");
                PropertiesMgr.getProperties().setProperty(Constants.JMS_STAY_ALIVE, "true");
            }// end if
            getProcessor().updateProperties(p);
        }// end if
        myLogger.exiting(MY_CLASS_NAME, methodName, p);
        return p;
    }// end getProperties

    /**
     * This method sets a up a VB script file that will run any .bat without any object locking. It is used for dummy type batch application.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Oct 6, 2015
     */
    private void setUpVbScript() {
        String methodName = "setUpVbScript";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method sets a up a VB script file that will run any .bat without any object locking.  It is used for dummy type batch application.");
        myLogger.fine("Create the VB Script file.");
        try{
            File f = new File(ApplicationConstants.PARENT_DIR + Constants.VB_SHELL_NAME);
            if(f.createNewFile()){
                // load to a list to send to the file utility.
                ArrayList<String> lines = new ArrayList<String>();
                for(int i = 0, j = Constants.VBSCRIPT.length;i < j;i++){
                    lines.add(Constants.VBSCRIPT[i]);
                }// end for
                FileUtil.writeFile(ApplicationConstants.PARENT_DIR + Constants.VB_SHELL_NAME, lines);
            }// end if
            vbsPath = f.getCanonicalPath();
        }catch(IOException e){
            myLogger.severe("Exception occured while initially creating the Vb Script file.  Message is: " + e.getMessage());
            applicationLogger.getChild().severe("Exception occured while initially creating the Vb Script file.  Message is: " + e.getMessage());
        }// end try-catch
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end setUpVbScript

    /**
     * This method cleans up the files associated with the DB. This will delete them ALL. If any files are locked, it will go into a wait cycle for 30 seconds and try again. This will be repeated 10 times. If it still cannot delete the files, it will warn through the logs and attempt to run anyway.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, July 6, 2017
     */
    private void clearDB() {
        String methodName = "cleanDB";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method cleans up the files associated with the DB.  This will delete them ALL.");
        myLogger.fine("Clear DB.");
        Boolean done = false;
        int count = 0;
        while(true){
            try{
                File f = new File(ApplicationConstants.PARENT_DIR + Constants.DB_FILE_NAME);
                if(f != null && f.exists()){
                    java.io.File[] files = f.listFiles();
                    if(files != null){
                        for(int i = 0, j = files.length;i < j;i++){
                            rm(files[i]);
                            files[i].delete();
                        }
                    }
                    f.delete();
                }
                done = true;
            }catch(Exception e){
                myLogger.severe("Exception occured while clearing the localDb files.  Message is: " + e.getMessage());
            }
            // if successfully deleted, leave. Otherwise initiate the wait cycle and attempt again
            if(done){
                break;
            }else if(count < 10){
                try{
                    // Sleep for 30 seconds at a time.
                    TimeUnit.SECONDS.sleep(30);
                }catch(InterruptedException e){
                    myLogger.severe("Exception occured while in the wait state during DB clear.  Message is: " + e.getMessage());
                    applicationLogger.getChild().severe("Exception occured while in the wait state during DB clear.  Message is: " + e.getMessage());
                }// end-if
                count = count + 1;
            }else{
                done = true;
            }// end if
        }// end while
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end clearDB

    private void rm(java.io.File file) throws Exception {
        if(file.list() != null && file.list().length > 0){
            java.io.File[] files = file.listFiles();
            for(int i = 0, j = files.length;i < j;i++){
                if(files[i].isDirectory()){
                    if(files[i].list() != null && files[i].list().length > 0){
                        for(int k = 0, l = files.length;k < l;k++){
                            rm(files[k]);
                        }
                    }
                }
                rm(files[i]);
            }
        }else{
            file.delete();
        }

    }

    /*
     * (non-Javadoc)
     * @see gov.doc.isu.gtv.core.AbstractApplication#run()
     */
    @Override
    public void run() {
        String methodName = "run";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method is an overridden method of AbstractApplication.  It does the meat of the ShamenController.");
        applicationLogger.info("Running Shamen Controller!");
        Long connectivityLossTime = null;
        Boolean initialEmailSent = false;
        // Set up the internal DB and jms stuff.
        try{
            initializeController();
        }catch(SQLException e1){
            myLogger.log(Level.SEVERE, "Unable to initialize controller. Message is: " + e1.getMessage(), e1);
        }
        Boolean holdJmsActive = new Boolean(jmsActive);
        executor = Executors.newFixedThreadPool(4);
        if(jmsActive){
            myLogger.info("Create the PTP listener thread.");
            executor.execute(setUpPtpListener());
            myLogger.info("Create the Subscriber listener thread.");
            executor.execute(setUpTopicListener());
            myLogger.info("Create the MessageMaid thread.");
            executor.execute(setUpMessageMaid());
        }// end if
        myLogger.info("Create the schedule manager thread to run any scheduled batch apps.");
        Thread t3 = null;
        ScheduleManager scheduleManager = new ScheduleManager("SCHEDULE_MANAGER_", controller, vbsPath);
        t3 = new Thread(scheduleManager, "SCHEDULE_MANAGER");
        // t3.setUncaughtExceptionHandler(new ShamenUncaughtExceptionHandler("SCHEDULE_MANAGER_"));
        executor.execute(t3);
        myLogger.finer("Wait until all threads are finished or until system user manually shuts down.");
        Properties properties;
        while(!executor.isTerminated()){
            try{
                TimeUnit.SECONDS.sleep(30);// Sleep for 30 seconds at a time.
            }catch(InterruptedException e){
                myLogger.log(Level.SEVERE, "InterruptedException - Driver thread interrupted for while sleeping. Message is: " + e.getMessage(), e);
            }// end try/catch
            properties = getProperties();
            if(!properties.isControllerStayAlive()){
                myLogger.info("Termination was detected.  Shutting down controller and all threads.");
                shutdownAndAwaitTermination(executor);
            }// end if
            if(!properties.isJmsStayAlive()){
                myLogger.fine("JMS connection failure detected, setting jmsActive back to false.");
                jmsActive = false;
                if(!jmsActive.equals(holdJmsActive)){
                    connectivityLossTime = System.currentTimeMillis();
                    holdJmsActive = new Boolean(jmsActive);
                }
            }// end if
             // If the Jms component is not set up for whatever reason, attempt to set it up.
            if(!jmsActive){
                try{
                    TimeUnit.SECONDS.sleep(30);// Sleep for 30 seconds at a time.
                }catch(InterruptedException e){
                    myLogger.log(Level.SEVERE, "InterruptedException - Driver thread interrupted for while sleeping. Message is: " + e.getMessage(), e);
                }// end try/catch
                setUpJMS();
                // if successful JMS initialization, then start the JMS dependent threads.
                if(jmsActive){
                    holdJmsActive = new Boolean(jmsActive);
                    // if the loss of jms connectivity email was sent and connectivity has been restored, then notify admin of connection restoration.
                    if(initialEmailSent){
                        sendJmsConnectivityRestorationEmail();
                    }
                    connectivityLossTime = null;
                    initialEmailSent = false;
                    myLogger.info("Create the PTP listener thread.");
                    executor.execute(setUpPtpListener());
                    myLogger.info("Create the Topic listener thread.");
                    executor.execute(setUpTopicListener());
                    myLogger.info("Create the MessageMaid thread.");
                    executor.execute(setUpMessageMaid());
                    // Since work could have been done while the JMS components were down, catch them up.
                    getProcessor().processArchivedMessages();

                }else{
                    if(connectivityLossTime != null){
                        Long diff = System.currentTimeMillis() - connectivityLossTime;
                        // if connectivity has lasted longer than an hour and no notification has been sent
                        // or connectivity has lasted longer than 24 hours, send an additional email.
                        if((diff > 3600000 && !initialEmailSent) || (diff > 86400000 && initialEmailSent)){
                            // if((diff > 120000 && !initialEmailSent) || (diff > 120000 && initialEmailSent)){
                            // if the initial email has already been sent, then reset the time calculation start so that it goes another 24 hours.
                            if(initialEmailSent){
                                connectivityLossTime = System.currentTimeMillis();
                            }// end if
                            sendJmsConnectivityEmail();
                            initialEmailSent = true;
                        }// end if
                    }// end if
                }// end if-else
            }// end if
        }// end while
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end run

    /**
     * This method sends an email to notify Shamen admin that jms connectivity has been down for over an hour.
     */
    private void sendJmsConnectivityEmail() {
        String methodName = "sendJmsConnectivityEmail";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method sends an email to notify Shamen admin that jms connectivity has been down for over an hour.");
        myLogger.info("Send notification to admin that jms connectivity has been down for over an hour.");
        controller = getProcessor().getController();
        String controllerName = "";
        String providerUrl = PropertiesMgr.getProperties().getProperty(InitialContext.PROVIDER_URL);
        if(controller != null){
            controllerName = controller.getControllerAddress().toUpperCase();
        }// end if
        StringBuffer sb = new StringBuffer();
        sb.append("Be advised that ShamenController: ");
        sb.append(controllerName);
        sb.append(" has been unable to establish a connection to ");
        sb.append(providerUrl);
        sb.append(" for over an hour. ");
        sb.append("</br>");
        sb.append("</br>");
        sb.append("The controller will still run the scheduled jobs but no updates will be sent to ShamenWeb until JMS connectivity is restored.  You will receive an additional email when the JMS Connection is restored.  If the connection is not restored in 24 hours, you will receive another email with the next 24 hours of schedule information.  The Controller's current schedule for the next 24 hours is: ");
        sb.append("</br>");
        sb.append("</br>");

        sb.append(new ShamenScheduleProcessor().getFormattedSchedule());
        try{
            EmailSender.send(sb.toString());
        }catch(EmailException e){
            myLogger.severe("Unable to send the jms connectivity email to notifiy admin of loss of jms connectivity for over an hour.");
        }// end try-catch
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end sendJmsConnectivityEmail

    /**
     * This method sends an email to notify Shamen admin that jms connectivity has been restored.
     */
    private void sendJmsConnectivityRestorationEmail() {
        String methodName = "sendJmsConnectivityRestorationEmail";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method sends an email to notify Shamen admin that jms connectivity has been restored.");
        myLogger.info("Send notification to admin that jms connectivity has been restored.");
        controller = getProcessor().getController();
        String controllerName = "";
        String providerUrl = PropertiesMgr.getProperties().getProperty(InitialContext.PROVIDER_URL);
        if(controller != null){
            controllerName = controller.getControllerAddress().toUpperCase();
        }// end if
        StringBuffer sb = new StringBuffer();
        sb.append("Be advised that ShamenController: ");
        sb.append(controllerName);
        sb.append(" has reestablished a connection to ");
        sb.append(providerUrl);
        sb.append(". ");
        sb.append("</br>");
        sb.append("</br>");
        sb.append("The controller will update ShamenWeb with all job run information that occurred during the JMS connection outage. This may take several minutes depending upon the duration of the outage and amount of job run information. ");
        sb.append("</br>");
        sb.append("</br>");
        sb.append("The controller will now function normally.");
        try{
            EmailSender.send(sb.toString());
        }catch(EmailException e){
            myLogger.severe("Unable to send the jms connectivity email to notifiy admin of loss of jms connectivity for over an hour.");
        }// end try-catch
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end sendJmsConnectivityEmail

    /**
     * This method shuts down all the threads and waits for actual termination.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 23, 2015
     * @param pool
     *        (required)
     */
    private void shutdownAndAwaitTermination(ExecutorService pool) {
        String methodName = "shutdownAndAwaitTermination";
        myLogger.entering(MY_CLASS_NAME, methodName, pool);
        myLogger.finest("This method shuts down all the threads and waits for actual termination.");
        pool.shutdown(); // Disable new tasks from being submitted
        try{
            // Wait a while for existing tasks to terminate
            if(!pool.awaitTermination(60, TimeUnit.SECONDS)){
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if(!pool.awaitTermination(60, TimeUnit.SECONDS)) System.err.println("Pool did not terminate");
            }// end if
        }catch(InterruptedException e){
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
            myLogger.log(Level.SEVERE, "An InterruptedException has been caught while trying to shut down the thread pool normally.  ShutdownNow had to be used. Message is: " + e.getMessage(), e);
            applicationLogger.getChild().log(Level.SEVERE, "An InterruptedException has been caught while trying to shut down the thread pool normally.  ShutdownNow had to be used. Message is: " + e.getMessage(), e);
        }// end try-catch
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end shutdownAndAwaitTermination

    /**
     * This method sets up the PtpListener thread
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Aug 26, 2015
     */
    private Thread setUpPtpListener() {
        String methodName = "setUpPtpListener";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method sets up the PtpListener thread.");
        Thread t = null;
        PtpListener ptp = new PtpListener("PTP_LISTENER_");
        t = new Thread(ptp, "PTP_LISTENER");
        // t.setUncaughtExceptionHandler(new ShamenUncaughtExceptionHandler("PTP_LISTENER_"));
        myLogger.exiting(MY_CLASS_NAME, methodName, t);
        return t;
    }

    /**
     * This method sets up the PtpListener thread
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Aug 26, 2015
     */
    private Thread setUpTopicListener() {
        String methodName = "setUpTopicListener";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method sets up the PtpListener thread.");
        Thread t = null;
        TopicListener topicListener = new TopicListener("TOPIC_LISTENER_", vbsPath);
        t = new Thread(topicListener, "TOPIC_LISTENER");
        // t.setUncaughtExceptionHandler(new ShamenUncaughtExceptionHandler("TOPIC_LISTENER_"));
        myLogger.exiting(MY_CLASS_NAME, methodName, t);
        return t;
    }

    /**
     * This method sets up the MessageMaid thread that does period message maintenance to keep controller and shamen in sync.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Aug 26, 2015
     */
    private Thread setUpMessageMaid() {
        String methodName = "setUpMessageMaid";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method sets up the MessageMaid thread that does period message maintenance to keep controller and shamen in sync.");
        Thread t = null;
        MessageMaid m = new MessageMaid("MESSAGE_MAID_");
        t = new Thread(m, "MESSAGE_MAID");
        // t.setUncaughtExceptionHandler(new ShamenUncaughtExceptionHandler("MESSAGE_MAID_"));
        myLogger.exiting(MY_CLASS_NAME, methodName, t);
        return t;
    }// end setUpMessageMaid

    /**
     * This method insures that all connections can be established and sets all the properties accordingly.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Aug 26, 2015
     */
    private void setUpJMS() {
        String methodName = "setUpJMS";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method insures that all connections can be established and sets all the properties accordingly.");
        jmsActive = false;
        // If controller record has not been obtained, go get that sucker.
        if(controller == null){
            controller = getProcessor().getController();
            if(controller != null){
                PropertiesMgr.getProperties().setProperty(Constants.MESSAGE_SELECTOR, controller.getControllerAddress().toUpperCase());
            }// end if
        }// end if
         // if jms has not been set up then attempt to set it up.
        if(!jmsActive && controller != null){
            try{
                // Try and set up the JMS component.
                JmsManager jms = new JmsManager(myLogger);
                jmsActive = true;
                jms.cleanUp();
                // Set the property to let all threads know that JMS is in business.
                Properties p = new Properties();
                p.setJmsStayAlive("true");
                getProcessor().updateProperties(p);
            }catch(Exception e){
                myLogger.log(Level.WARNING, "An Exception has been caught while trying to set up the JmsManager. Since it can not be properly initialized, this controller will be functioning independently from JMS. If the situation is fixed, it will automatically register and continue working.  Message is: " + e.getMessage(), e);
                jmsActive = false;
            }// end try-catch
        }// end if
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end setUpJms

    /*
     * (non-Javadoc)
     * @see gov.doc.isu.gtv.core.AbstractApplication#postprocess()
     */
    @Override
    protected void postprocess() throws Exception {
        closeLoggers();
        cleanupLogs();
        System.out.println("postprocess");
        System.exit(0);
    }// end postprocess

    /*
     * (non-Javadoc)
     * @see gov.doc.isu.gtv.core.AbstractApplication#getAdditionalApplicationProperties()
     */
    @Override
    public CustomProperties getAdditionalApplicationProperties() {
        CustomProperties properties = new CustomProperties();
        properties.put(InitialContext.INITIAL_CONTEXT_FACTORY, Constants.INITIAL_CONTEXT_FACTORY, "DO NOT CHANGE!.  This is the JMS provider's context factory");
        properties.put(InitialContext.PROVIDER_URL, Constants.PROVIDER_URL);
        properties.put(Constants.CONNECTION_FACTORY, Constants.CONNECTION_FACTORY_VALUE, "DO NOT CHANGE!.  This is the connection factory literal used to set up JMS through JNDI.");
        properties.put(Constants.REQUEST_Q, Constants.REQUEST_Q_VALUE);
        properties.put(Constants.MQ_USER, Constants.MQ_USER_VALUE);
        properties.put(Constants.MQ_PASSWORD, Constants.MQ_PASSWORD_VALUE);
        properties.put("java.naming.security.principal", "system", "DO NOT CHANGE!.  The user ID used to connect to the JMS provider.");
        properties.put("java.naming.security.credentials", "manager", "DO NOT CHANGE!. The password used to connect to the JMS provider.");
        properties.put(Constants.CONTROLLER_STAY_ALIVE, "true", "Continue running property.  If you change this, the controller will terminate!  Also, don't forget to change this back to true when you restart.");
        properties.put(Constants.JMS_STAY_ALIVE, "true", "Reconnection order property. If you change this to false, the controller will end messaging threads and attempt to restart them.  Be patient and give it at least 2 minutes to finish. Also, don't forget to change this back to true when you restart.");
        properties.put(Constants.INTERNAL_DB_URL, "jdbc:derby:ShamenController/resources/database");
        properties.put(Constants.INTERNAL_DB_INIT_URL, "jdbc:derby:ShamenController/resources/database;create=true");
        properties.put(Constants.INTERNAL_DB_USER, "sa");
        properties.put(Constants.INTERNAL_DB_PASSWORD, "sa");
        return properties;
    }// end getAdditionalApplicationProperties

    /**
     * This method controls the instance variable for the processor.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Oct 6, 2015
     */
    private ShamenThreadSafeProcessor getProcessor() {
        if(shamenProcessor == null){
            shamenProcessor = new ShamenThreadSafeProcessor();
        }
        return shamenProcessor;
    }

    /*
     * (non-Javadoc)
     * @see gov.doc.isu.gtv.core.AbstractApplication#getAdditionalLoggingProperties()
     */
    @Override
    public CustomProperties getAdditionalLoggingProperties() {
        CustomProperties properties = new CustomProperties();
        properties.put("gov.doc.isu.shamen.core.level", "INFO");
        properties.put("gov.doc.isu.shamen.dao.level", "INFO");
        properties.put("gov.doc.isu.shamen.jms.level", "INFO");
        properties.put("gov.doc.isu.shamen.main.level", "INFO");
        properties.put("gov.doc.isu.shamen.thread.level", "INFO");
        properties.put("gov.doc.isu.shamen.processors.level", "INFO");
        return properties;
    }// end getAdditionalLoggingProperties

    /*
     * (non-Javadoc)
     * @see gov.doc.isu.gtv.core.AbstractApplication#getMonitorTime()
     */
    protected long getMonitorTime() {
        return 30L;
    }// end getMonitorTime

    /*
     * (non-Javadoc)
     * @see gov.doc.isu.gtv.core.AbstractApplication#getCheckPeriod()
     */
    @Override
    protected long getCheckPeriod() {
        return 10L;
    }// end getCheckPeriod

    /*
     * (non-Javadoc)
     * @see gov.doc.isu.gtv.core.AbstractApplication#getCleanLogDirectory()
     */
    @Override
    protected boolean getCleanLogDirectory() {
        return true;
    }// end getCleanLogDirectory

    /*
     * (non-Javadoc)
     * @see gov.doc.isu.gtv.core.ifc.IEmailable#getEmailBcc()
     */
    @Override
    public String getEmailBcc() {
        return null;
    }// end getEmailBcc

    /*
     * (non-Javadoc)
     * @see gov.doc.isu.gtv.core.ifc.IEmailable#getEmailCc()
     */
    @Override
    public String getEmailCc() {
        return null;
    }// end getEmailCc

    /*
     * (non-Javadoc)
     * @see gov.doc.isu.gtv.core.ifc.IEmailable#getEmailFrom()
     */
    @Override
    public String getEmailFrom() {
        return "ShamenController@docapp.mo.gov";
    }// end getEmailFrom

    /*
     * (non-Javadoc)
     * @see gov.doc.isu.gtv.core.ifc.IEmailable#getEmailHost()
     */
    @Override
    public String getEmailHost() {
        return "zimbra.isu.net";
    }// end getEmailHost

    /*
     * (non-Javadoc)
     * @see gov.doc.isu.gtv.core.ifc.IEmailable#getEmailStoreProtocol()
     */
    @Override
    public String getEmailStoreProtocol() {
        return "pop3";
    }// end getEmailStoreProtocol

    @Override
    public String getEmailSubject() {
        return "Shamen Application";
    }// end getEmailSubject

    /*
     * (non-Javadoc)
     * @see gov.doc.isu.gtv.core.ifc.IEmailable#getEmailTo()
     */
    @Override
    public String getEmailTo() {
        return "sls000is@isu.net";
    }// end getEmailTo

    /*
     * (non-Javadoc)
     * @see gov.doc.isu.gtv.core.ifc.IEmailable#getEmailTransportProtocol()
     */
    @Override
    public String getEmailTransportProtocol() {
        return "smtp";
    }// end getEmailTransportProtocol

    /*
     * (non-Javadoc)
     * @see gov.doc.isu.gtv.core.ifc.IDBConnectivity#getDatabaseDriver()
     */
    @Override
    public String getDatabaseDriver() {
        return "org.apache.derby.jdbc.EmbeddedDriver";
    }

    /*
     * (non-Javadoc)
     * @see gov.doc.isu.gtv.core.ifc.IDBConnectivity#getDatabaseUrl()
     */
    @Override
    public String getDatabaseUrl() {
        return "jdbc:derby:ShamenController/resources/database";
    }

    /*
     * (non-Javadoc)
     * @see gov.doc.isu.gtv.core.ifc.IDBConnectivity#getDatabasePassword()
     */
    @Override
    public String getDatabasePassword() {
        return "sa";
    }

    /*
     * (non-Javadoc)
     * @see gov.doc.isu.gtv.core.ifc.IDBConnectivity#getDatabaseUserId()
     */
    @Override
    public String getDatabaseUserId() {
        return "sa";
    }

    @Override
    public String getApplicationName() {
        return "ShamenController";
    }

    @Override
    protected String getEncryptionKey() {
        return "docsecretpassword";
    }

    @Override
    protected UserInterface getUserInterface() {
        // TODO Auto-generated method stub
        return null;
    }

}// end class
