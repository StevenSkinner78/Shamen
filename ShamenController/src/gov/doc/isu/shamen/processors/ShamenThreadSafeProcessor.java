/**
 * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
 */
package gov.doc.isu.shamen.processors;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.doc.isu.gtv.util.ApplicationUtil;
import gov.doc.isu.shamen.core.Constants;
import gov.doc.isu.shamen.dao.ShamenThreadSafeDAO;
import gov.doc.isu.shamen.interfaces.Scheduleable;
import gov.doc.isu.shamen.jms.ControllerMessage;
import gov.doc.isu.shamen.jms.JmsManager;
import gov.doc.isu.shamen.jms.models.JmsBatchApp;
import gov.doc.isu.shamen.jms.models.JmsBatchAppCollection;
import gov.doc.isu.shamen.jms.models.JmsController;
import gov.doc.isu.shamen.jms.models.JmsRunStatus;
import gov.doc.isu.shamen.jms.models.JmsSchedule;
import gov.doc.isu.shamen.models.Message;
import gov.doc.isu.shamen.models.Properties;

/**
 * Contains all processor methods for the Shamen Controller
 * 
 * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
 * @author <strong>Steven Skinner</strong> JCCC, Aug 17, 2020
 * @author <strong>Gary Campbell</strong> JCCC, Jan 12, 2021
 */
public class ShamenThreadSafeProcessor {
    private static final String MY_CLASS_NAME = "gov.doc.isu.shamen.processors.ShamenThreadSafeProcessor";
    // please note that this logger is not static in order for all the threads to log properly.
    private Logger myLogger = null;
    private Boolean isThread = false;
    private ShamenThreadSafeDAO shamenDAO = null;

    /**
     * Default constructor with logger instantiation
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
     */
    public ShamenThreadSafeProcessor() {
        // set standard logger to default.
        myLogger = Logger.getLogger(MY_CLASS_NAME);
    }// end ShamenProcessor

    /**
     * Default constructor with applogger instantiation and standard logger instantiation. This is used when this processor is used by one of the threads.
     * 
     * @param inLogger
     *        (required)
     * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
     */
    public ShamenThreadSafeProcessor(Logger inLogger) {
        isThread = true;
        // set the standard logger
        // myLogger = inLogger;
        // This is temporary to see if it makes a difference on logging locks.
        myLogger = Logger.getLogger(MY_CLASS_NAME);
    }// end ShamenProcessor

    // /**
    // * This method sets up the application logger for this class.
    // *
    // * @author <strong>Shane Duncan</strong> JCCC, Sep 29, 2015
    // */
    // private void setUpAppLogger() {
    // String methodName = "setUpAppLogger";
    // myLogger.entering(MY_CLASS_NAME, methodName);
    // myLogger.finest("This method sets up the application logger for this class.");
    // myLogger.exiting(MY_CLASS_NAME, methodName);
    // }// end setUpAppLogger

    /**
     * This method returns a properly configured DAO instance.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 29, 2015
     * @return
     */
    private ShamenThreadSafeDAO getDAO() {
        String methodName = "getDAO";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method returns a properly configured DAO instance.");
        // if already retrieved, skip. Otherwise get a new one.
        if(shamenDAO == null){
            if(isThread){
                // if this is a thread, set it up with the proper thread logger.
                shamenDAO = ShamenThreadSafeDAO.getInstance(myLogger);
            }else{
                shamenDAO = ShamenThreadSafeDAO.getInstance();
            }// end else-if
        }// end if
        myLogger.exiting(MY_CLASS_NAME, methodName, shamenDAO);
        return shamenDAO;
    }// end getDAO

    /**
     * Cleanup all the DB stuff.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 29, 2015
     * @return
     */
    public void cleanUp() {
        getDAO().cleanUp();
        shamenDAO = null;
    }// end cleanUp

    /**
     * This method gets the controller record for this controller. It also loads the queue for this controller.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
     */
    public JmsController getController() {
        String methodName = "getController";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method gets the controller record for this controller. It also loads the queue for this controller.");
        ShamenThreadSafeDAO dao = getDAO();
        myLogger.finer("Get the controller record.");
        JmsController controller = dao.getController(getProperties().getControllerName());
        // if(controller != null){
        // myLogger.finer("Get the queue record");
        // controller.setQueue(dao.getQueue(controller.getQueueRefId()));
        // }// end if
        myLogger.finest("Returning with controller: " + (null != controller ? controller : "null"));
        myLogger.exiting(MY_CLASS_NAME, methodName);
        return controller;
    }// end getController

    /**
     * This method gets the controller record for named controller. It also loads the queue for this controller.
     * 
     * @param controllerName
     *        (required)
     * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
     */
    public JmsController getControllerByName(String controllerName) {
        String methodName = "getControllerByName";
        myLogger.entering(MY_CLASS_NAME, methodName, controllerName);
        myLogger.finest("This method gets the controller record for this controller. It also loads the queue for this controller.");
        ShamenThreadSafeDAO dao = getDAO();
        myLogger.finer("Get the controller record.");
        JmsController controller = dao.getController(controllerName);
        myLogger.finer("Get the queue record");
        // controller.setQueue(dao.getQueue(controller.getQueueRefId()));
        myLogger.exiting(MY_CLASS_NAME, methodName, controller);
        return controller;
    }// end getControllerByName

    /**
     * This method gets all the batch apps for a particular controller.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
     * @return List of BatchApps
     */
    public List getBatchAppsForController() {
        String methodName = "getBatchAppsForController";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method gets all the batch apps for a particular controller.");
        ShamenThreadSafeDAO dao = getDAO();
        myLogger.finer("Get the batch app records.");
        List<JmsBatchApp> batchAppList = dao.getBatchAppsForController();
        myLogger.finer("Get the schedule for each batch app.");
        for(int i = 0, j = batchAppList.size();i < j;i++){
            batchAppList.get(i).setSchedule(dao.getScheduleForBatchApp(batchAppList.get(i).getBatchAppRefId()));
        }// end for
        if(myLogger.isLoggable(Level.FINEST)){
            myLogger.finest("Returning with batchAppList: " + (null != batchAppList ? batchAppList : "null"));
        }// end if
        myLogger.exiting(MY_CLASS_NAME, methodName, (batchAppList != null ? "Number of BatchApps: " + batchAppList.size() : "0"));
        return batchAppList;
    }// end getBatchAppsForController

    /**
     * This method gets all the batch Collections for a particular controller.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
     * @return List of BatchCollections
     */
    public List getBatchCollectionsForController() {
        String methodName = "getBatchAppsForController";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method gets all the batch collections for a particular controller.");
        ShamenThreadSafeDAO dao = getDAO();
        myLogger.finer("Get the batch app records.");
        List<JmsBatchAppCollection> batchCollectionList = dao.getBatchCollectionsForController();
        myLogger.finer("Get the schedule for each batch collection.");
        for(int i = 0, j = batchCollectionList.size();i < j;i++){
            batchCollectionList.get(i).setSchedule(dao.getScheduleForBatchApp(batchCollectionList.get(i).getMainBatchAppRefId()));
        }// end for
        myLogger.finer("Get the child batch jobs for each batch collection.");
        for(int i = 0, j = batchCollectionList.size();i < j;i++){
            batchCollectionList.get(i).setBatchApps(dao.getBatchAppsForCollection(batchCollectionList.get(i).getMainBatchAppRefId()));
        }// end for
        if(myLogger.isLoggable(Level.FINEST)){
            myLogger.finest("Returning with BatchCollections: " + (null != batchCollectionList ? batchCollectionList : "null"));
        }// end if
        myLogger.exiting(MY_CLASS_NAME, methodName, (batchCollectionList != null ? "Number of BatchCollections: " + batchCollectionList.size() : "0"));
        return batchCollectionList;
    }// end getBatchCollectionsForController

    /**
     * This method gets the batch Collection by batchAppRefId.
     * 
     * @param batchAppRefId
     *        refId of the batch collection
     * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
     * @return Scheduleable the batch collection
     */
    public Scheduleable getBatchCollectionByRefId(Long batchAppRefId) {
        String methodName = "getBatchCollectionByRefId";
        myLogger.entering(MY_CLASS_NAME, methodName, batchAppRefId);
        myLogger.finest("This method gets the batch Collection by batchAppRefId.");
        ShamenThreadSafeDAO dao = getDAO();
        myLogger.finer("Get the batch app records.");
        JmsBatchAppCollection batchCollection = dao.getBatchCollectionByRefId(batchAppRefId);
        myLogger.finer("Get the schedule for each batch collection.");
        if(batchCollection != null){
            batchCollection.setSchedule(dao.getScheduleForBatchApp(batchCollection.getMainBatchAppRefId()));
            myLogger.finer("Get the child batch jobs for each batch collection.");
            batchCollection.setBatchApps(dao.getBatchAppsForCollection(batchCollection.getMainBatchAppRefId()));
        }// end if

        if(myLogger.isLoggable(Level.FINEST)){
            myLogger.finest("Returning with BatchCollection: " + (null != batchCollection ? batchCollection : "null"));
        }// end if
        myLogger.exiting(MY_CLASS_NAME, methodName, (batchCollection != null ? "BatchCollection: " + batchCollection : "null"));
        return batchCollection;
    }// end getBatchCollectionByRefId

    /**
     * This method gets a batch apps by the batchAppRefId
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
     * @param batchAppRefId
     *        (required)
     * @return BatchApp
     */
    public JmsBatchApp getBatchAppByBatchAppRefId(Long batchAppRefId) {
        String methodName = "getBatchAppByBatchAppRefId";
        myLogger.entering(MY_CLASS_NAME, methodName, batchAppRefId);
        myLogger.finest("This method gets a batch apps by the batchAppRefId.");
        ShamenThreadSafeDAO dao = getDAO();
        myLogger.finer("Get the batch app record.");
        JmsBatchApp batchApp = dao.getBatchAppByBatchAppRefId(batchAppRefId);
        if(batchApp != null){
            batchApp.setSchedule(dao.getScheduleForBatchApp(batchAppRefId));
        }// end if
        myLogger.exiting(MY_CLASS_NAME, methodName, batchApp);
        return batchApp;
    }// end getBatchAppByBatchAppRefId

    /**
     * This method gets the most recent run status for a schedule. It's basically the last scheduled run.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param scheduleRefId
     *        (required)
     * @return Schedule
     */
    public JmsRunStatus getMostRecentRunStatusForSchedule(Long scheduleRefId) {
        String methodName = "getMostRecentRunStatusForSchedule";
        myLogger.entering(MY_CLASS_NAME, methodName, scheduleRefId);
        myLogger.finest("This method gets the most recent run status for a schedule.  It's basically the last scheduled run. ");
        ShamenThreadSafeDAO dao = getDAO();
        myLogger.finer("Get the run status record.");
        JmsRunStatus runStatus = dao.getMostRecentRunStatusForSchedule(scheduleRefId);
        myLogger.exiting(MY_CLASS_NAME, methodName, runStatus);
        return runStatus;
    }// end getMostRecentRunStatusForSchedule

    /**
     * This method inserts or updates a record in the run status table.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param runStatus
     *        (required)
     */
    public JmsRunStatus saveRunStatus(JmsRunStatus runStatus) {
        String methodName = "insertRunStatus";
        myLogger.entering(MY_CLASS_NAME, methodName, runStatus);
        myLogger.finest("This method inserts or updates a record in the run status table.");
        ShamenThreadSafeDAO dao = getDAO();
        if(runStatus.getRunStatusRefId() == null){
            myLogger.finer("Insert the runStatus record.");
            runStatus = dao.insertRunStatus(runStatus);
        }else{
            myLogger.finer("Update the runStatus record.");
            runStatus = dao.updateRunStatus(runStatus);
        }// end if/else
        myLogger.exiting(MY_CLASS_NAME, methodName, runStatus);
        return runStatus;
    }// end saveRunStatus

    /**
     * This method initializes the local DB.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
     * @return Controller
     * @throws Exception
     */
    public void initializeInternalDB() {
        String methodName = "initializeDB";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method initializes the local DB.");
        ShamenThreadSafeDAO dao = getDAO();
        dao.initializeInternalDB();
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end initializeInternalDB

    /**
     * This method creates the local DB.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
     * @return Controller
     * @throws SQLException
     * @throws Exception
     */
    public void createDB() throws SQLException {
        String methodName = "createDB";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method initializes the local DB.");
        ShamenThreadSafeDAO dao = getDAO();
        dao.createDB();
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end createDB

    /**
     * Used to insert a Controller and its children to the local db.
     * 
     * @param controllerModel
     *        the ControllerModel to add to the DB.
     * @throws WDUException
     *         if an exception occurred
     */
    public void insertController(JmsController controller) {
        String methodName = "insertController";
        if(controller != null){
            myLogger.entering(MY_CLASS_NAME, methodName, controller);
            myLogger.finest("Used to insert a Controller record and its children to the local db.");
            ShamenThreadSafeDAO dao = getDAO();
            dao.insertController(controller);
            // Loop through and write the batch apps
            if(controller.getBatchApps() != null){
                for(int i = 0, j = controller.getBatchApps().size();i < j;i++){
                    JmsBatchApp batchApp = controller.getBatchApps().get(i);
                    insertBatchApp(batchApp);
                    // Write the schedules
                    if(batchApp.getSchedule() != null){
                        for(JmsSchedule schedule : batchApp.getSchedule()){
                            insertSchedule(schedule);
                        }// end for
                    }// end if
                }// end for
            }// end if
             // Loop through and write the batch app collections.
            if(controller.getJmsBatchAppCollections() != null){
                for(int i = 0, j = controller.getJmsBatchAppCollections().size();i < j;i++){
                    JmsBatchAppCollection batchAppCollection = controller.getJmsBatchAppCollections().get(i);
                    // Set up the main record
                    JmsBatchApp batchApp = ShamenObjectConverter.collectionToMainBatch(batchAppCollection);
                    insertBatchApp(batchApp);
                    for(int k = 0, l = batchAppCollection.getBatchApps().size();k < l;k++){
                        JmsBatchApp batchAppColMbr = batchAppCollection.getBatchApps().get(k);
                        // insert the xref
                        insertBatchAppXref(batchApp.getBatchAppRefId(), batchAppColMbr.getBatchAppRefId(), batchAppColMbr.getRunSequenceNbr());
                        // insert the batch app collection member
                        insertBatchApp(batchAppColMbr);
                    }// end for

                    // Write the schedules
                    if(batchApp.getSchedule() != null){
                        for(JmsSchedule schedule : batchApp.getSchedule()){
                            insertSchedule(schedule);
                        }// end for
                    }// end if
                }// end for
            }// end if
             // Write the queue
             // if(controller.getQueue() != null){
             // insertQueue(controller.getQueue());
             // }// end if
        }// end if
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end insertController

    // /**
    // * Used to insert a queue record to the local db.
    // *
    // * @param queue
    // * the queue to add to the DB.
    // */
    // public void insertQueue(JmsQueue queue) {
    // String methodName = "insertQueue";
    // myLogger.entering(MY_CLASS_NAME, methodName, queue);
    // myLogger.finest("Used to insert a queue record to the local db.");
    // ShamenThreadSafeDAO dao = getDAO();
    // dao.insertQueue(queue);
    // myLogger.exiting(MY_CLASS_NAME, methodName);
    // }// end insertQueue

    /**
     * Used to insert a BatchAppXref record to the local db.
     * 
     * @param collectionRefId
     *        collectionRefId
     * @param associatedRefId
     *        associatedRefId
     * @param appRunSeq
     *        appRunSeq
     */
    public void insertBatchAppXref(Long collectionRefId, Long associatedRefId, Long appRunSeq) {
        String methodName = "insertBatchApp";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("Used to insert a BatchApp record to the local db.");
        ShamenThreadSafeDAO dao = getDAO();
        dao.insertBatchAppXref(collectionRefId, associatedRefId, appRunSeq);
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end insertBatchAppXref

    /**
     * Used to insert a BatchApp record to the local db.
     * 
     * @param batchApp
     *        the batchApp to add to the DB.
     */
    public void insertBatchApp(JmsBatchApp batchApp) {
        String methodName = "insertBatchApp";
        myLogger.entering(MY_CLASS_NAME, methodName, batchApp);
        myLogger.finest("Used to insert a BatchApp record to the local db.");
        ShamenThreadSafeDAO dao = getDAO();
        dao.insertBatchApp(batchApp);
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end insertBatchApp

    /**
     * Used to insert a Schedule record to the local db.
     * 
     * @param schedule
     *        the schedule to add to the DB.
     */
    public void insertSchedule(JmsSchedule schedule) {
        String methodName = "insertSchedule";
        myLogger.entering(MY_CLASS_NAME, methodName, schedule);
        myLogger.finest("Used to insert a Schedule record to the local db.");
        ShamenThreadSafeDAO dao = getDAO();
        dao.insertSchedule(schedule);
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end insertSchedule

    /**
     * Used to insert a runStatus record to the local db.
     * 
     * @param runStatus
     *        the runStatus to add to the DB.
     */
    public void insertRunStatus(JmsRunStatus runStatus) {
        String methodName = "insertRunStatus";
        myLogger.entering(MY_CLASS_NAME, methodName, runStatus);
        myLogger.finest("Used to insert a runStatus record to the local db.");
        ShamenThreadSafeDAO dao = getDAO();
        dao.insertRunStatus(runStatus);
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end insertRunStatus

    /**
     * Used to delete a message record from the local db.
     * 
     * @param controllerMessage
     *        the ControllerMessage to delete from the DB.
     */
    public void deleteMessage(ControllerMessage controllerMessage) {
        String methodName = "deleteMessage";
        myLogger.entering(MY_CLASS_NAME, methodName, controllerMessage);
        myLogger.finest("Used to delete a message record from the local db.");
        myLogger.fine("Convert the controllerMessage to a message object");
        Message message = new Message();
        message.setId(controllerMessage.getCorrelationID());
        ShamenThreadSafeDAO dao = getDAO();
        dao.deleteMessage(message);
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end deleteMessage

    /**
     * Used to insert a message to the local DB. This is done by first serializing the message and then putting it into a blob in the DB.
     * 
     * @param message
     *        the ControllerMessage to add to the DB.
     */
    public void insertMessage(ControllerMessage message) {
        String methodName = "insertMessage";
        myLogger.entering(MY_CLASS_NAME, methodName, message);
        myLogger.finest("Used to insert a message to the local DB.  This is done by first serializing the message and then putting it into a blob in the DB.");
        try{
            // writeFileNoAppend(serialPath, "");
            myLogger.fine("Create the output stream for serialization.");
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(out);
            myLogger.fine("Serialize the message object.");
            oos.writeObject(message);
            // myLogger.fine("Pull the serialization in as a string.");
            // List<String> s = FileUtil.readFile(serialPath);
            // StringBuffer sb = new StringBuffer();
            // for (int i = 0, j = s.size(); i < j; i++ ) {
            // sb.append(s.get(i));
            // }//end if
            Message m = new Message();
            m.setMessageBytes(out.toByteArray());
            m.setId(message.getCorrelationID());
            // m.setMessage(readFileFull(serialPath));
            m.setType(message.getText());
            ShamenThreadSafeDAO dao = getDAO();
            dao.insertMessage(m);
        }catch(FileNotFoundException e){
            myLogger.log(Level.SEVERE, "Exception encountered while tyring to serialize message. Message is: " + e.getMessage(), e);
        }catch(IOException e){
            myLogger.log(Level.SEVERE, "Exception encountered while tyring to serialize message. Message is: " + e.getMessage(), e);
        }// end try-catch
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end insertMessage

    /**
     * This method gets the message by the message's id
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param id
     *        (required)
     * @return ControllerMessage
     */
    public ControllerMessage getMessageById(String id) {
        String methodName = "getMessageById";
        myLogger.entering(MY_CLASS_NAME, methodName, id);
        myLogger.finest("This method gets the message by the message's id.");
        ShamenThreadSafeDAO dao = getDAO();
        myLogger.finer("Get the Message record.");
        Message message = dao.getMessageById(id);
        ControllerMessage returnMessage = null;
        if(message != null){
            if(message.getMessageBytes() != null){
                // String serialPath = PropertiesMgr.getProperties().getProperty(Constants.SERIAL_PATH);
                try{
                    // writeFileNoAppend(serialPath, "");
                    // FileUtil.writeFile(serialPath, message.getMessage());
                    myLogger.fine("Create the output stream for serialization.");
                    ByteArrayInputStream in = new ByteArrayInputStream(message.getMessageBytes());
                    ObjectInputStream ois = new ObjectInputStream(in);
                    myLogger.fine("Serialize the message object.");
                    myLogger.fine("De-Serialize the message object.");
                    returnMessage = (ControllerMessage) ois.readObject();
                }catch(FileNotFoundException e){
                    myLogger.log(Level.SEVERE, "Exception encountered while tyring to de-serialize message. Message is: " + e.getMessage(), e);
                }catch(IOException e){
                    myLogger.log(Level.SEVERE, "Exception encountered while tyring to de-serialize message. Message is: " + e.getMessage(), e);
                }catch(ClassNotFoundException e){
                    myLogger.log(Level.SEVERE, "Exception encountered while tyring to de-serialize message. Message is: " + e.getMessage(), e);
                }// end try-catch
            }// end if
        }// end if
        myLogger.exiting(MY_CLASS_NAME, methodName, message);
        return returnMessage;
    }// end getMessageById

    /**
     * This method gets all the saved messages.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param id
     *        (required)
     * @return messageList list of all messages
     */
    public List<Message> getAllMessages() {
        String methodName = "getAllMessages";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method gets all the saved messages.");
        ShamenThreadSafeDAO dao = getDAO();
        myLogger.fine("Get all the Messages.");
        List<Message> messageList = dao.getAllMessages();
        myLogger.exiting(MY_CLASS_NAME, methodName, messageList);
        return messageList;
    }// end getAllMessages

    /**
     * This gets all archived messages and sends them. This is to insure that Shamen is up to date with anything this controller may have been doing while jms was down.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     */
    public List<Message> processArchivedMessages() {
        String methodName = "processArchivedMessages";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This gets all archived messages and sends them.  This is to insure that Shamen is up to date with anything this controller may have been doing while jms was down.");
        ShamenThreadSafeDAO dao = getDAO();
        myLogger.fine("Get all the Messages.");
        List<Message> messageList = dao.getAllMessages();
        myLogger.fine("Send each message.");
        JmsManager jms = null;
        try{
            jms = new JmsManager(myLogger);
            for(int i = 0, j = messageList.size();i < j;i++){
                Message m = messageList.get(i);
                ControllerMessage msg = getMessageById(m.getId());
                if(msg != null){
                    jms.sendPTP(msg, Constants.SHAMEN_REQUEST_Q);
                }// end if
            }// end for
        }catch(Exception e){
            myLogger.log(Level.SEVERE, "An Exception has been caught while trying to send the message to shamen to update the runStatus record. Message is: " + e.getMessage(), e);
        }finally{
            if(jms != null){
                jms.cleanUp();
            }// end if
        } // end try-catch
        myLogger.exiting(MY_CLASS_NAME, methodName, messageList);
        return messageList;
    }// end processArchivedMessages

    /**
     * This method shuts down and compacts the DB
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2017
     * @return Controller
     */
    public void shutdownAndCompactDB() {
        myLogger.entering(MY_CLASS_NAME, "shutdownAndCompactDB");
        myLogger.finest("This method shuts down and compacts the DB.");
        getDAO().shutdownAndCompactDB();
        myLogger.exiting(MY_CLASS_NAME, "shutdownAndCompactDB");
    }// end shutdownAndCompactDB

    /**
     * This method initializes the properties table.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
     * @return Controller
     * @throws Exception
     */
    public void initializePropertiesAndRunStatus() {
        String methodName = "initializeProperties";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method initializes the properties table.");
        ShamenThreadSafeDAO dao = getDAO();
        dao.initializePropertiesAndRunStatus();
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end initializeProperties

    /**
     * Used to get the properties record in the local db.
     * 
     * @return Properties
     */
    public Properties getProperties() {
        String methodName = "getProperties";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("Used to get the properties record in the local db.");
        ShamenThreadSafeDAO dao = getDAO();
        Properties p = null;
        // it is possible for this to try and retrieve properties at exactly the same time as it is being deleted by another thread. If so, keep trying.
        for(int i = 0;i < 5;i++){
            p = dao.getProperties();
            if(p != null){
                break;
            }// end if
             // sleep for 1/2 second
            try{
                Thread.sleep(500);
            }catch(InterruptedException e){
                myLogger.warning("Unable to sleep because: " + e.getMessage());
            }// end try-catch
        }// end for
        if(p == null){
            myLogger.severe("Unable to retrieve properties, even after 5 attempts. This should never happen so something bad is going on.");
        }// end if
        myLogger.exiting(MY_CLASS_NAME, methodName, p);
        return p;
    }// end getProperities

    /**
     * Used to update the properties record in the local db.
     * 
     * @param inProperties
     *        the inProperties to UPDATE to the DB.
     */
    public void updateProperties(Properties inProperties) {
        String methodName = "updateProperties";
        myLogger.entering(MY_CLASS_NAME, methodName, inProperties);
        myLogger.finest("Used to update the properties record in the local db.");
        ShamenThreadSafeDAO dao = getDAO();
        dao.updateProperties(inProperties);
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end updateProperties

    /**
     * This method is a convenience method used to write to an existing file by appending a line to the end of the file.
     * 
     * @param path
     *        The <code>String</code> value of the path to the file being written to.
     * @param line
     *        The <code>String</code> to append to the end of the file.
     */
    public void writeFileNoAppend(String path, String line) {
        myLogger.entering(MY_CLASS_NAME, "writeFile");
        File f = new File(path);
        BufferedWriter writer = null;
        try{
            writer = new BufferedWriter(new FileWriter(f, false));
            myLogger.finer("Writing line to file: " + f.getName());
            writer.append(line);
            writer.flush(); // flush to write out to file...
        }catch(IOException e){
            myLogger.log(Level.SEVERE, "IOException was caught while trying to write to the file. Message is: " + e.getMessage(), e);
        }finally{
            try{
                myLogger.finer("See if the writer needs to be closed or not...if so close it");
                if(writer != null){
                    writer.close();
                }// end if
            }catch(IOException e){
                myLogger.log(Level.SEVERE, "IOException was caught while trying to close the Buffered Writer. Message is: " + e.getMessage(), e);
            }// end try/catch
        }// end try/catch/finally
        myLogger.exiting(MY_CLASS_NAME, "writeFile");
    }// end writeFile

    /**
     * This method is a convenience method used to read a file and return a <code>List&lt;String&gt;</code>.
     * 
     * @param path
     *        The {@link java.io.File} to read and add to a <code>List&lt;String&gt;</code>.
     * @return The file contents in a <code>List&lt;String&gt;</code>.
     */
    public String readFileFull(String path) {
        myLogger.entering(MY_CLASS_NAME, "readFile");
        File f = new File(path);
        BufferedReader reader = null;
        StringBuffer sb = new StringBuffer();
        try{
            String line = null;
            reader = new BufferedReader(new FileReader(f));
            while((line = reader.readLine()) != null){
                if(!ApplicationUtil.isNullOrEmpty(line)){
                    sb.append(line);
                }// end if
            } // end while
        }catch(FileNotFoundException e){
            myLogger.log(Level.SEVERE, "FileNotFoundException was caught while trying to initialize the Buffered Reader. Message is: " + e.getMessage(), e);
        }catch(IOException e){
            myLogger.log(Level.SEVERE, "IOException was caught while trying to read the file. Message is: " + e.getMessage(), e);
        }finally{
            try{
                myLogger.finer("See if the reader needs to be closed or not...if so close it");
                if(reader != null){
                    reader.close();
                }// end if
            }catch(IOException e){
                myLogger.log(Level.SEVERE, "IOException was caught while trying to close the Buffered Reader. Message is: " + e.getMessage(), e);
            }// end try/catch
        }// end try/catch/finally
        myLogger.exiting(MY_CLASS_NAME, "readFile");
        return sb.toString();
    }// end readFile
}// end class
