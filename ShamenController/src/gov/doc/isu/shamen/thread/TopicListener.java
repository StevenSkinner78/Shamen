/**
 * @author <strong>Shane Duncan</strong> JCCC, Aug 21, 2015
 */
package gov.doc.isu.shamen.thread;

import gov.doc.isu.shamen.interfaces.Scheduleable;
import gov.doc.isu.shamen.jms.ControllerMessage;
import gov.doc.isu.shamen.jms.JmsManager;
import gov.doc.isu.shamen.jms.Subscriber;
import gov.doc.isu.shamen.jms.models.JmsController;
import gov.doc.isu.shamen.models.Properties;
import gov.doc.isu.shamen.processors.ShamenThreadSafeProcessor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.naming.NamingException;

/**
 * This class is intended to listen to a topic on a JMS server.
 * <p>
 * The types of messages it listens for are:
 * </p>
 * <ul>
 * <li>Termination</li>
 * <li>Run a Batch Application</li>
 * <li>Update a schedule</li>
 * <li>Alive?</li>
 * </ul>
 * 
 * @author <strong>Shane Duncan</strong> JCCC, Aug 21, 2015
 */
public class TopicListener extends Subscriber {
    private static final String MY_CLASS_NAME = "gov.doc.isu.shamen.thread.TopicListener";
    private ShamenThreadSafeProcessor shamenProcessor;
    private String vbsPath;

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 3, 2015
     * @param threadName
     *        The name for the thread
     * @param vbsPath
     *        The path of the Vbs shell.
     */
    public TopicListener(String threadName, String vbsPath) {
        super(threadName);
        this.vbsPath = vbsPath;
    }

    /*
     * (non-Javadoc) This method is an overridden method that starts the TopicListener.
     * @see gov.doc.isu.shamen.thread.ControllerThread#run()
     */
    @Override
    public void run() {
        String methodName = "run";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest(methodName, "This method is an overridden method that starts the TopicListener.");
        myLogger.finer(methodName, "Set up the subscription.");
        try{
            init();
        }catch(JMSException e){
            myLogger.getChild().log(Level.SEVERE, "A JMS exception has been caught while trying to set up the subscription. Message is: " + e.getMessage(), e);
        }catch(NamingException e){
            myLogger.getChild().log(Level.SEVERE, "A naming exception has been caught while trying to set up the subscription. Message is: " + e.getMessage(), e);
        }
        Boolean keepGoing = true;
        Properties properties;
        while(keepGoing){
            // This is set to finest to insure that the logs don't fill up with junk.
            myLogger.finest(methodName, "Listen for next message.");
            try{
                TimeUnit.SECONDS.sleep(5);
            }catch(InterruptedException e){
                myLogger.getChild().log(Level.SEVERE, "A InterruptedException has been caught while trying to sleep in the run method. Message is: " + e.getMessage(), e);
            }
            properties = getProperties();
            // This is set to finest to insure that the logs don't fill up with crud.
            myLogger.finest(methodName, "Get stayAlive property.  If value false or not in the properties, kill thread.");
            if(!properties.isControllerStayAlive()){
                keepGoing = false;
                myLogger.fine(methodName, "Termination request detected. Killing thread.");
            }
            // This log message was intentionally put to finest to keep the log from filling up.
            myLogger.finest(methodName, "Get jmsStayAlive property.  If value false or not in the properties, kill thread.");
            if(!properties.isJmsStayAlive()){
                myLogger.warning(methodName, "JMS connection failure detected, killing thread until JMS comes back up.");
                keepGoing = false;
            }// end if
        }// end while
        closeAllConnections();
        killMyself();
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

    /*
     * (non-Javadoc) This method listens for a message for a certain topic. It responds to the message accordingly.
     * @see gov.doc.isu.shamen.jms.Subscriber#onMessage(javax.jms.Message)
     */
    public void onMessage(Message msg) {
        String methodName = "onMessage";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest(methodName, "This method listens for a message for a certain topic.  It responds to the message accordingly.");
        if(msg instanceof ObjectMessage){
            Object obj = null;
            try{
                obj = ((ObjectMessage) msg).getObject();
            }catch(JMSException e){
                myLogger.getChild().log(Level.SEVERE, "A JMS exception has been caught while trying to cast a published message to an ObjectMessage. Message is: " + e.getMessage(), e);
            }
            myLogger.finer(methodName, "Insure that this message is a ControllerMessage, if not ignore it.");
            if(obj instanceof ControllerMessage){
                ControllerMessage controllerMessage = (ControllerMessage) obj;
                myLogger.finer(methodName, "Message received is: " + controllerMessage);
                if(ControllerMessage.TERMINATE.equals(controllerMessage.getText())){
                    myLogger.info(methodName, "####################### MESSAGE ####################### Termination requested.");
                    myLogger.fine(methodName, "Make controller kill itself.");
                    setControllerStayAlive("false");
                }else if(ControllerMessage.RUN_BAT.equals(controllerMessage.getText())){
                    myLogger.info(methodName, "####################### MESSAGE ####################### Run Batch requested.");
                    myLogger.fine(methodName, "Kick off a batch app.");
                    runBatchApp(controllerMessage);
                }else if(ControllerMessage.REFRESH_SCHEDULE.equals(controllerMessage.getText())){
                    myLogger.info(methodName, "####################### MESSAGE ####################### Update schedule requested.");
                    myLogger.fine(methodName, "Tell the scheduler thread to update itself.");
                    // if controller was sent, redo the internal DB.
                    initializeControllerDb(controllerMessage.getController());
                    setScheduleRefresh("true");
                }else if(ControllerMessage.RECEIVED.equals(controllerMessage.getText())){
                    myLogger.info(methodName, "####################### MESSAGE ####################### Message confirmation received.");
                    myLogger.fine(methodName, "This is the confirmation of a received message.");
                    deleteStoredMessage(controllerMessage);
                }else if(ControllerMessage.ARE_YOU_ALIVE.equals(controllerMessage.getText())){
                    myLogger.info(methodName, "####################### MESSAGE ####################### Are Your Alive? requested.");
                    myLogger.finer(methodName, "Ask the controller for alive confirmation.");
                    acknowledgeControllerAlive((ObjectMessage) msg);

                }else if(ControllerMessage.RESTART_THREADS.equals(controllerMessage.getText())){
                    myLogger.info(methodName, "####################### MESSAGE ####################### Restart Threads requested.");
                    myLogger.finer(methodName, "Order the controller to restart its threads.");
                    setJmsAlive("false");

                }// end else if
            }// end if
        }// end if
    }// end onMessage

    @Override
    public void onException(JMSException exception) {
        // This is where you put any logging of messaging exceptions and possibly code to terminate the thread.
        myLogger.getChild().severe("JMS Exception occurred in TopicListener.  It was: " + exception);
    }// end onException

    /**
     * This method sets the controllerStayAlive property for use by other threads.
     * 
     * @param value
     * @author <strong>Shane Duncan</strong> JCCC, Oct 6, 2015
     */
    private void setControllerStayAlive(String value) {
        String methodName = "setControllerStayAlive";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest(methodName, "This method sets the controllerStayAlive property for use by other threads.");
        Properties p = new Properties();
        p.setControllerStayAlive(value);
        getProcessor().updateProperties(p);
        // getProcessor().cleanUp();
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end setControllerStayAlive

    /**
     * This method sets the JmsAlive property for use by other threads.
     * 
     * @param value
     * @author <strong>Shane Duncan</strong> JCCC, Oct 6, 2015
     */
    private void setJmsAlive(String value) {
        String methodName = "setJmsAlive";
        myLogger.entering(MY_CLASS_NAME, methodName, value);
        myLogger.finest(methodName, "This method sets the JmsAlive property for use by other threads.");
        Properties p = new Properties();
        p.setJmsStayAlive(value);
        ShamenThreadSafeProcessor processor = new ShamenThreadSafeProcessor(myLogger.getChild());
        processor.updateProperties(p);
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end setJmsAlive

    /**
     * This method sets the scheduleRefresh property for use by other threads.
     * 
     * @param value
     * @author <strong>Shane Duncan</strong> JCCC, Oct 6, 2015
     */
    private void setScheduleRefresh(String value) {
        String methodName = "setScheduleRefresh";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest(methodName, "This method sets the scheduleRefresh property for use by other threads.");
        Properties p = new Properties();
        p.setScheduleRefresh(value);
        getProcessor().updateProperties(p);
        // getProcessor().cleanUp();
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end setScheduleRefresh

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

    /**
     * This method creates a BatchAppRunner thread which kicks off a batch job.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param controllerMessage
     *        (required)
     * @return
     */
    private void runBatchApp(ControllerMessage controllerMessage) {
        String methodName = "runBatchApp";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest(methodName, "This method creates a BatchAppRunner thread which kicks off a batch job.");
        Scheduleable batchApp = null;
        String userId = null;
        String jobParamaters = " ";
        if(!controllerMessage.getCollectionIndicator()){
            batchApp = getProcessor().getBatchAppByBatchAppRefId(controllerMessage.getBatchAppRefId());
        }else{
            batchApp = getProcessor().getBatchCollectionByRefId(controllerMessage.getBatchAppRefId());
        }// end if-else

        // If this batch app was run from a client web application, then get the user Id from the misc parameters
        if(controllerMessage.getMiscParameters() != null && !controllerMessage.getMiscParameters().isEmpty()){
            userId = controllerMessage.getMiscParameters().get(0);
        }

        // If this batch app was has job parameters
        if(controllerMessage.getJobParameters() != null && !controllerMessage.getJobParameters().isEmpty()){
            jobParamaters = controllerMessage.getJobParameters();
        }

        // Since this is being run manually, insure that the scheduleRefId is null
        if(batchApp != null){
            batchApp.setSchedule(null);
            if(controllerMessage.getRunStatus() != null){
                batchApp.setCreateUserRefId(controllerMessage.getRunStatus().getCreateUserRefId());
                batchApp.setUpdateUserRefId(controllerMessage.getRunStatus().getUpdateUserRefId());
            }else{
                batchApp.setCreateUserRefId(1L);
                batchApp.setUpdateUserRefId(null);
            }
            ExecutorService executor = Executors.newSingleThreadExecutor();
            String runNumberString = String.valueOf(System.currentTimeMillis());
            executor.execute(new Thread(new BatchAppRunner(batchApp.getName() + "_", batchApp, Boolean.valueOf(true), null, vbsPath, Long.valueOf(runNumberString.substring(0, 10)), userId, jobParamaters)));
            executor.shutdown();
            executor = null;
        }else{
            myLogger.fine(methodName, "Unable to run manual batch job.  Not found in the local DB for batchAppRefId: " + controllerMessage.getBatchAppRefId());
        }
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end runBatchApp

    /**
     * This method calls the JmsManager and acknowledges the Alive? request.
     *
     * @author <strong>Steven Skinner</strong> JCCC, July 13, 2016
     * @param message
     *        the message recieved through the topic
     */
    private void acknowledgeControllerAlive(ObjectMessage message) {
        String methodName = "acknowledgeControllerAlive";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest(methodName, "This method calls the JmsManager and acknowledges the Alive? request");
        try{
            JmsManager jms = new JmsManager(myLogger.getChild());
            jms.acknowlegeAreYouAliveMessage(message);
            jms.cleanUp();
        }catch(JMSException e){
            onException(e);
        }catch(Exception e){
            myLogger.getChild().severe("Exception occurred in TopicListener.  It was: " + e);
        }
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }

    /**
     * This method initializes the controller database and sets up all the records.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param controller
     *        (required)
     */
    private void initializeControllerDb(JmsController controller) {
        String methodName = "initializeControllerDb";
        myLogger.entering(MY_CLASS_NAME, methodName, controller);
        myLogger.finest(methodName, "This method initializes the controller database and sets up all the records.  ");
        if(controller != null){
            getProcessor().initializeInternalDB();
            // insert the controller and all of its children
            getProcessor().insertController(controller);
        }// end if
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end initializeControllerDb

    /**
     * This method deletes the stored message. The idea is that important messages sent to Shamen must be confirmed. If confirmation doesn't occur, the message will be resent.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param message
     *        ControllerMessage to be deleted (required)
     */
    private void deleteStoredMessage(ControllerMessage message) {
        String methodName = "deleteStoredMessage";
        myLogger.entering(MY_CLASS_NAME, methodName, message);
        myLogger.finest(methodName, "This method deletes the stored message.  The idea is that important messages sent to Shamen must be confirmed.  If confirmation doesn't occur, the message will be resent.");
        myLogger.exiting(MY_CLASS_NAME, methodName);
        myLogger.fine(methodName, "This message has been confirmed, so delete it.");
        getProcessor().deleteMessage(message);
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end deleteStoredMessage
}// end class
