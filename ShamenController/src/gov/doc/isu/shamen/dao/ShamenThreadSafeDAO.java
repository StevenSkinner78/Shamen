/**
 * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
 */
package gov.doc.isu.shamen.dao;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.junderground.jdbc.DebugLevel;
import com.junderground.jdbc.StatementFactory;

import gov.doc.isu.gtv.database.DBConnection;
import gov.doc.isu.gtv.managers.PropertiesMgr;
import gov.doc.isu.shamen.core.Constants;
import gov.doc.isu.shamen.jms.models.JmsBatchApp;
import gov.doc.isu.shamen.jms.models.JmsBatchAppCollection;
import gov.doc.isu.shamen.jms.models.JmsController;
import gov.doc.isu.shamen.jms.models.JmsRunStatus;
import gov.doc.isu.shamen.jms.models.JmsSchedule;
import gov.doc.isu.shamen.models.Message;
import gov.doc.isu.shamen.models.Properties;

/**
 * This class handles all data access for the Shamen Controller. NOTE: This class is not like a standard DAO in that it cannot keep a static instance of itself. This is due to the multiple threads needing to log to the thread logger. Thus they all get get new instances. However, the connection is still static.
 * 
 * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
 * @author <strong>Steven Skinner</strong> JCCC, Aug 17, 2020
 * @author <strong>Gary Campbell</strong> JCCC, Jan 12, 2021
 */
public class ShamenThreadSafeDAO {
    private static final String SQL_GET_CONTROLLER_BY_ADDRESS = "SELECT * FROM TRANS.Controllers where Controller_Address = ? and Delete_Ind = 'N'";
    private static final String SQL_GET_BATCH_APPS = "SELECT * FROM TRANS.Batch_Apps where FILE_NM <> '' and Delete_Ind = 'N'";
    private static final String SQL_GET_COLLECTION_BATCH_APPS = "SELECT b.* FROM TRANS.Batch_Apps b join TRANS.BATCH_APPS_COLLECTION_XREF x on x.Assoc_Batch_App_Ref_Id = b.Batch_App_Ref_Id where x.Main_Batch_App_Ref_Id = ? order by Batch_App_Run_Seq";
    private static final String SQL_GET_BATCH_COLLECTIONS = "SELECT * FROM TRANS.Batch_Apps where FILE_NM is null and Delete_Ind = 'N'";
    private static final String SQL_GET_BATCH_COLLECTION_BY_REF_ID = "SELECT * FROM TRANS.Batch_Apps where FILE_NM is null and Delete_Ind = 'N'  and Batch_App_Ref_Id = ?";
    private static final String SQL_GET_BATCH_APPS_BY_BATCH_APP_REF_ID = "SELECT * FROM TRANS.Batch_Apps where Batch_App_Ref_Id = ? and Delete_Ind = 'N'";
    private static final String SQL_GET_SCHEDULE_BY_BATCH_APP_REF_ID = "SELECT * FROM TRANS.Schedules where Batch_App_Ref_Id = ? and Delete_Ind = 'N'";
    private static final String SQL_GET_MESSAGE_BY_ID = "SELECT * FROM MASTER.Messages where MESSAGE_ID = ?";
    private static final String SQL_GET_ALL_MESSAGES = "SELECT * FROM MASTER.Messages order by CREATE_TS";
    private static final String SQL_GET_MOST_RECENT_RUN_STATUS_FOR_SCHEDULE = "SELECT f_Id,Create_Ts,Update_User_Ref_Id,Update_Ts,Delete_Ind FROM MASTER.Run_Status where Schedule_Ref_Id = ? and Delete_Ind = 'N' and Run_Status_Ref_Id = (select MAX(run_status_ref_id) from Trans.Run_Status where Schedule_Ref_Id = ?)";
    private static final String SQL_GET_PROPERTIES = "SELECT * FROM MASTER.PROPERTIES";
    private static final String SQL_INSERT_RUN_STATUS = "INSERT into MASTER.Run_Status (Batch_App_Ref_Id,Schedule_Ref_Id,Start_Ts,Stop_Ts,Status_Cd,Result_Cd,Result_Detail,Description,Create_User_Ref_Id,CREATE_TS) VALUES(?,?,?,?,?,?,?,?,?,?)";
    private static final String SQL_UPDATE_RUN_STATUS = "UPDATE MASTER.Run_Status set Batch_App_Ref_Id=?,Schedule_Ref_Id =?,Start_Ts=?,Stop_Ts=?,Status_Cd=?,Result_Cd=?,Result_Detail=?,Description=?,Create_User_Ref_Id=?,create_ts = ? WHERE Run_Status_Ref_Id = ?";
    private static final String SQL_CREATE_CONTROLLER_TABLE = "CREATE TABLE TRANS.CONTROLLERS(CONTROLLER_REF_ID int, CONTROLLER_NM VARCHAR(30), QUEUE_REF_ID int, CONTROLLER_ADDRESS VARCHAR(40), CREATE_USER_REF_ID int, CREATE_TS timestamp, UPDATE_USER_REF_ID int, UPDATE_TS timestamp, DELETE_IND char(1))";
    private static final String SQL_DROP_CONTROLLER_TABLE = "DROP TABLE TRANS.CONTROLLERS";
    private static final String SQL_CREATE_BATCH_APPS_TABLE = "CREATE TABLE TRANS.BATCH_APPS(BATCH_APP_REF_ID int, CONTROLLER_REF_ID int, BATCH_NM VARCHAR(45), FILE_NM VARCHAR(100), FILE_LOCATION VARCHAR(150), DESCRIPTION VARCHAR(300), BATCH_TYPE_CD CHAR(3), CREATE_USER_REF_ID int, CREATE_TS timestamp, UPDATE_USER_REF_ID int, UPDATE_TS timestamp, DELETE_IND char(1))";
    private static final String SQL_DROP_BATCH_APPS_TABLE = "DROP TABLE TRANS.BATCH_APPS";
    private static final String SQL_CREATE_BATCH_APP_XREF_TABLE = "CREATE TABLE TRANS.BATCH_APPS_COLLECTION_XREF(Main_Batch_App_Ref_Id int, Assoc_Batch_App_Ref_Id int, Batch_App_Run_Seq int)";
    private static final String SQL_DROP_BATCH_APP_XREF_TABLE = "DROP TABLE TRANS.BATCH_APPS_COLLECTION_XREF";
    private static final String SQL_CREATE_QUEUE_TABLE = "CREATE TABLE TRANS.QUEUE(QUEUE_REF_ID int, QUEUE_NM VARCHAR(18), CREATE_USER_REF_ID int, CREATE_TS timestamp, UPDATE_USER_REF_ID int, UPDATE_TS timestamp, DELETE_IND char(1))";
    private static final String SQL_DROP_QUEUE_TABLE = "DROP TABLE TRANS.QUEUE";
    private static final String SQL_CREATE_RUN_STATUS_TABLE = "CREATE TABLE MASTER.RUN_STATUS(RUN_STATUS_REF_ID int, BATCH_APP_REF_ID int, SCHEDULE_REF_ID int, START_TS timestamp, STOP_TS timestamp, STATUS_CD CHAR(3), RESULT_CD CHAR(3), RESULT_DETAIL VARCHAR(500), DESCRIPTION VARCHAR(100),CREATE_USER_REF_ID int, CREATE_TS timestamp, UPDATE_USER_REF_ID int, UPDATE_TS timestamp, DELETE_IND char(1))";
    private static final String SQL_DROP_RUN_STATUS_TABLE = "DROP TABLE MASTER.RUN_STATUS";
    private static final String SQL_CREATE_SCHEDULES_TABLE = "CREATE TABLE TRANS.SCHEDULES(SCHEDULE_REF_ID int, BATCH_APP_REF_ID int, START_TIME TIME, FREQUENCY_CD CHAR(3), FREQUENCY_TYPE_CD CHAR(3), REPEAT_CD CHAR(3), SCHEDULE_START_DT DATE, RECUR_NO INT, WEEKDAYS VARCHAR(15),DAY_NO VARCHAR(90), WEEK_NO VARCHAR(10), ACTIVE_IND CHAR(1), CREATE_USER_REF_ID int, CREATE_TS timestamp, UPDATE_USER_REF_ID int, UPDATE_TS timestamp, DELETE_IND char(1))";
    private static final String SQL_DROP_SCHEDULES_TABLE = "DROP TABLE TRANS.SCHEDULES";
    private static final String SQL_CREATE_PROPERTIES_TABLE = "CREATE TABLE MASTER.PROPERTIES(CONTROLLER_NAME varchar(40), CONTROLLER_STAY_ALIVE char(5), JMS_STAY_ALIVE char(5), schedule_refresh CHAR(5))";
    private static final String SQL_DROP_PROPERTIES_TABLE = "DROP TABLE MASTER.PROPERTIES";
    private static final String SQL_CREATE_MESSAGES_TABLE = "CREATE TABLE MASTER.MESSAGES(MESSAGE_ID varchar(30), TYPE varchar(30), CREATE_TS timestamp DEFAULT CURRENT_TIMESTAMP, MESSAGE BLOB )";
    private static final String SQL_DROP_MESSAGES_TABLE = "DROP TABLE MASTER.MESSAGES";
    private static final String SQL_CREATE_SCHEMA = "CREATE SCHEMA TRANS";
    private static final String SQL_CREATE_SCHEMA_MASTER = "CREATE SCHEMA MASTER";
    private static final String SQL_DELETE_MESSAGE_BY_ID = "DELETE FROM MASTER.Messages where MESSAGE_ID = ?";
    private static final String SQL_DROP_SCHEMA = "DROP SCHEMA TRANS RESTRICT";
    private static final String SQL_DROP_SCHEMA_MASTER = "DROP SCHEMA MASTER CASCADE";
    private static final String SQL_DROP_PROPERTIES = "DROP TABLE MASTER.PROPERTIES";
    private static final String SQL_DELETE_PROPERTIES = "Delete from MASTER.PROPERTIES";
    protected static final String SQL_INSERT_CONTROLLER = "insert into TRANS.Controllers(controller_ref_id, queue_ref_id,controller_address,controller_Nm,delete_ind)  values(?,?,?,?,'N')";
    protected static final String SQL_INSERT_QUEUE = "insert into TRANS.QUEUE(QUEUE_REF_ID,QUEUE_NM,delete_ind)  values(?,?,'N')";
    protected static final String SQL_INSERT_PROPERTIES = "insert into MASTER.PROPERTIES(CONTROLLER_NAME,CONTROLLER_STAY_ALIVE,JMS_STAY_ALIVE,schedule_refresh)  values(?,?,?,?)";
    protected static final String SQL_INSERT_MESSAGES = "insert into MASTER.MESSAGES(MESSAGE_ID,TYPE,MESSAGE)  values(?,?,?)";
    protected static final String SQL_INSERT_BATCH_APP = "insert into TRANS.BATCH_APPS(batch_app_ref_id, controller_ref_id, batch_nm, file_nm, file_location, description, batch_type_cd, delete_ind)  values(?,?,?,?,?,?,?,'N')";
    protected static final String SQL_INSERT_BATCH_APP_XREF = "insert into TRANS.Batch_Apps_Collection_Xref(Main_Batch_App_Ref_Id, Assoc_Batch_App_Ref_Id, Batch_App_Run_Seq)  values(?,?,?)";
    protected static final String SQL_INSERT_SCHEDULE = "insert into TRANS.SCHEDULES(schedule_ref_id, batch_app_ref_id, start_time, frequency_cd, frequency_type_cd, repeat_cd, schedule_start_dt,recur_no, weekdays, day_no, week_no, active_ind, delete_ind)  values(?,?,?,?,?,?,?,?,?,?,?,?,'N')";
    protected static final String SQL_SHUTDOWN_AND_COMPACT = "SHUTDOWN COMPACT";
    private static final String CREATE_USER_REF_ID = "Create_User_Ref_Id";
    private static final String UPDATE_USER_REF_ID = "Update_User_Ref_Id";
    private static final String CREATE_TS = "Create_Ts";
    private static final String UPDATE_TS = "Update_Ts";
    private static final String DELETE_IND = "Delete_Ind";
    private static final String MY_CLASS_NAME = "gov.doc.isu.shamen.dao.ShamenThreadSafeDAO";
    private Logger myLogger = Logger.getLogger(MY_CLASS_NAME);
    public Connection conn;
    public Connection internalConn;

    /**
     * This method instantiates this class and returns and instance. It cannot be static as this has to be used by the threads, which require special logging concessions.
     * 
     * @return shamenDAO A static instance of this class.
     */
    public static synchronized ShamenThreadSafeDAO getInstance() {
        ShamenThreadSafeDAO shamenDAO = new ShamenThreadSafeDAO(Logger.getLogger(MY_CLASS_NAME));
        return shamenDAO;
    }// end getInstance

    /**
     * This method instantiates this class and returns and instance. This has to be used by the threads, which require special logging concessions.
     * 
     * @param inLogger
     * @return shamenDAO A static instance of this class.
     */
    public static synchronized ShamenThreadSafeDAO getInstance(Logger inLogger) {
        ShamenThreadSafeDAO shamenDAO = new ShamenThreadSafeDAO(inLogger);
        return shamenDAO;
    }// end getInstance

    /**
     * This special constructor provided to insure that threads are able to pick up the logging from this class.
     * 
     * @param inLogger
     * @author <strong>Shane Duncan</strong> JCCC, Sep 29, 2015
     */
    public ShamenThreadSafeDAO(Logger inLogger) {
        super();
        if(inLogger == null){
            myLogger = Logger.getLogger(MY_CLASS_NAME);
        }else{
            // myLogger = inLogger;
            // this commented out temporarily for logging lock tests.
            myLogger = Logger.getLogger(MY_CLASS_NAME);
        }

    }// end constructor

    /**
     * This method establishes the DB connection via George, It only uses the internal database
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 29, 2015
     * @return connection
     * @throws SQLException
     */
    public Connection getDBConnection() throws SQLException {
        String methodName = "getDBConnection";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method establishes the DB connection via George, It only uses the internal database.");
        if(conn == null){
            conn = DBConnection.getConnection();
        }else if(!conn.isValid(0)){
            conn = DBConnection.getConnection();
        }// end if
        myLogger.exiting(MY_CLASS_NAME, methodName, conn);
        return conn;
    }// end getDBConnection

    /**
     * This method cleans up all the DB resources.
     */
    public void cleanUp() {
        myLogger.entering(MY_CLASS_NAME, "cleanUp");
        myLogger.finest("This method cleans up all the DB resources.");
        if(conn != null){
            try{
                myLogger.fine("Close the DB connection.");
                getDBConnection().close();
                conn = null;
            }catch(SQLException e){
                myLogger.warning("Exception occurred while trying to close the DB Connection.  Exception is: " + e.getMessage());
            }// end try-catch
        }// end if
        myLogger.exiting(MY_CLASS_NAME, "cleanUp");
    }// end cleanUp

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

        try{
            myLogger.info("Drop the TRANS schema tables.");
            dropTableStmt(SQL_DROP_CONTROLLER_TABLE);
            dropTableStmt(SQL_DROP_BATCH_APPS_TABLE);
            dropTableStmt(SQL_DROP_QUEUE_TABLE);
            dropTableStmt(SQL_DROP_SCHEDULES_TABLE);
            dropTableStmt(SQL_DROP_BATCH_APP_XREF_TABLE);
            dropTableStmt(SQL_DROP_SCHEMA);
            myLogger.info("Create the TRANS schema and tables.");
            createTableStmt(SQL_CREATE_SCHEMA);
            createTableStmt(SQL_CREATE_CONTROLLER_TABLE);
            createTableStmt(SQL_CREATE_BATCH_APPS_TABLE);
            createTableStmt(SQL_CREATE_QUEUE_TABLE);
            createTableStmt(SQL_CREATE_SCHEDULES_TABLE);
            createTableStmt(SQL_CREATE_BATCH_APP_XREF_TABLE);
            myLogger.info("Successfully initialized internal DB.");
        }catch(Exception e){
            myLogger.log(Level.SEVERE, "A SQLException was caught while trying to initialize the internal DB. Message is: " + e.getMessage(), e);
        }// end try/catch
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
        myLogger.finest("This method createDB the local DB.");
        myLogger.info("Create the local DB.");
        try{
            DBConnection.getConnection(PropertiesMgr.getProperties().getProperty(Constants.INTERNAL_DB_INIT_URL), PropertiesMgr.getProperties().getProperty(Constants.INTERNAL_DB_USER), PropertiesMgr.getProperties().getProperty(Constants.INTERNAL_DB_PASSWORD));
        }catch(SQLException e){
            myLogger.severe("Database not created. The message is: " + e.getMessage());
            throw (e);
        }// end try-catch
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end createDB

    /**
     * This method shuts down and compacts the DB
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2017
     * @return Controller
     */
    public void shutdownAndCompactDB() {
        myLogger.entering(MY_CLASS_NAME, "shutdownAndCompactDB");
        myLogger.finest("This method shuts down and compacts the DB.");
        PreparedStatement ps = null;
        try{
            ps = StatementFactory.getStatement(getDBConnection(), SQL_SHUTDOWN_AND_COMPACT, DebugLevel.ON);
            ps.execute();
            conn = null;
        }catch(SQLException e){
            myLogger.warning("Database did not shutdown and compact because: " + e.getMessage());
        }// Leave debug on for logging.
        myLogger.exiting(MY_CLASS_NAME, "shutdownAndCompactDB");
    }

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
        // Create and initialize the internal properties table.
        try{
            // dropTableStmt(SQL_DROP_SCHEMA_MASTER);
            dropTableStmt(SQL_DROP_MESSAGES_TABLE);
            dropTableStmt(SQL_DROP_RUN_STATUS_TABLE);
            dropTableStmt(SQL_DROP_PROPERTIES);

            createTableStmt(SQL_CREATE_SCHEMA_MASTER);
            createTableStmt(SQL_CREATE_PROPERTIES_TABLE);
            createTableStmt(SQL_CREATE_RUN_STATUS_TABLE);
            createTableStmt(SQL_CREATE_MESSAGES_TABLE);
            Properties p = new Properties();
            p.setControllerName(InetAddress.getLocalHost().getHostName().toUpperCase());
            p.setControllerStayAlive("true");
            p.setJmsStayAlive("true");
            p.setScheduleRefresh("false");
            insertProperties(p);
        }catch(Exception e){
            myLogger.log(Level.SEVERE, "A SQLException was caught while trying to initialize Properties and RunStatus tables. Message is: " + e.getMessage(), e);
        }// end try-catch
    }// end initializePropertiesAndRunStatus

    /**
     * Used to drop a table for the local memory database.
     * 
     * @param stmt
     *        The sql statement to drop/create a table.
     * @throws LocalMemoryDBException
     *         {@link LocalMemoryDBException}
     */
    private void dropTableStmt(String stmt) {
        myLogger.entering(MY_CLASS_NAME, "dropTableStmt", new Object[]{stmt});
        myLogger.finest("Used to drop a table for the local memory database..");
        myLogger.finer("     Running the following query : " + stmt);
        PreparedStatement ps = null;
        try{
            ps = StatementFactory.getStatement(getDBConnection(), stmt, DebugLevel.ON);
            ps.execute();
        }catch(Exception e){
            // Schema did not exist, ignore exception--but warn just in case.
            myLogger.warning("Object not dropped.  This is probably due to it not existing and is therefore not an issue. The message is: " + e.getMessage());
        }// end try-catch
        myLogger.exiting(MY_CLASS_NAME, "dropTableStmt");
    }// end runTableStmt

    /**
     * Used to create a table for the local memory database.
     * 
     * @param stmt
     *        The sql statement to drop/create a table.
     * @throws Exception
     */
    private void createTableStmt(String stmt) throws Exception {
        myLogger.entering(MY_CLASS_NAME, "createTableStmt", new Object[]{stmt});
        myLogger.finest("Used to create a table for the local memory database.");
        myLogger.finer("     Running the following query : " + stmt);
        PreparedStatement ps = null;
        ps = StatementFactory.getStatement(getDBConnection(), stmt, DebugLevel.ON);// Leave debug on for logging.
        ps.execute();
        myLogger.exiting(MY_CLASS_NAME, "runCreateTableStmt");
    } // end runTableStmt

    /**
     * Used to insert a Controller record to the local db.
     * 
     * @param controllerModel
     *        the ControllerModel to add to the DB.
     */
    public void insertController(JmsController controllerModel) {
        String methodName = "insertController";
        myLogger.entering(MY_CLASS_NAME, methodName, controllerModel);
        myLogger.finest("Used to insert a Controller record to the local db.");
        PreparedStatement ps = null;
        try{
            ps = StatementFactory.getStatement(getDBConnection(), SQL_INSERT_CONTROLLER, DebugLevel.ON);
            ps.setInt(1, Integer.parseInt(controllerModel.getControllerRefId().toString()));
            ps.setInt(2, (controllerModel.getQueueRefId() != null ? Integer.parseInt(controllerModel.getQueueRefId().toString()) : 0));
            ps.setString(3, controllerModel.getControllerAddress().trim());
            ps.setString(4, controllerModel.getControllerName().trim());
            myLogger.finer("prepared statement=" + String.valueOf(ps));
            ps.execute();
        }catch(Exception e){
            myLogger.log(Level.SEVERE, "A SQLException was caught while trying to insert the controller record. Message is: " + e.getMessage(), e);
        }// end try-catch
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end insertController

    // /**
    // * Used to insert a Queue record to the local db.
    // *
    // * @param queue
    // * the queue to add to the DB.
    // */
    // public void insertQueue(JmsQueue queue) {
    // String methodName = "insertQueue";
    // myLogger.entering(MY_CLASS_NAME, methodName, queue);
    // myLogger.finest("Used to insert a Queue record to the local db.");
    // PreparedStatement ps = null;
    // try{
    // ps = StatementFactory.getStatement(getDBConnection(), SQL_INSERT_QUEUE, DebugLevel.ON);
    // myLogger.finer("prepared statement=" + String.valueOf(ps));
    // ps.setInt(1, Integer.parseInt(queue.getQueueRefId().toString()));
    // ps.setString(2, queue.getQueueName() != null ? queue.getQueueName().trim() : "");
    // ps.execute();
    // }catch(Exception e){
    // myLogger.log(Level.SEVERE, "A SQLException was caught while trying to insert the queue record. Message is: " + e.getMessage(), e);
    // appLogger.getChild().log(Level.SEVERE, "A SQLException was caught while trying to insert the queue record. Message is: " + e.getMessage(), e);
    // }// end try-catch
    // myLogger.exiting(MY_CLASS_NAME, methodName);
    // }// end insertQueue

    /**
     * Used to insert a properties record to the local db.
     * 
     * @param properties
     *        the properties to add to the DB.
     */
    public void insertProperties(Properties properties) {
        String methodName = "insertProperties";
        myLogger.entering(MY_CLASS_NAME, methodName, properties);
        myLogger.finest("Used to insert a properties record to the local db.");
        PreparedStatement ps = null;
        try{
            ps = StatementFactory.getStatement(getDBConnection(), SQL_INSERT_PROPERTIES, DebugLevel.ON);
            myLogger.finer("prepared statement=" + String.valueOf(ps));
            ps.setString(1, properties.getControllerName());
            ps.setString(2, properties.getControllerStayAlive());
            ps.setString(3, properties.getJmsStayAlive());
            ps.setString(4, properties.getScheduleRefresh());
            ps.execute();
        }catch(Exception e){
            myLogger.log(Level.SEVERE, "A SQLException was caught while trying to insert the properties record. Message is: " + e.getMessage(), e);
        }// end try-catch
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end insertProperties

    /**
     * Used to get a message record from the local db.
     * 
     * @param id
     *        the id
     */
    public Message getMessageById(String id) {
        String methodName = "getMessageById";
        myLogger.entering(MY_CLASS_NAME, methodName, id);
        myLogger.finest("Used to get a message record from the local db.");
        PreparedStatement ps = null;
        ResultSet rs = null;
        Message m = new Message();
        try{
            ps = StatementFactory.getStatement(getDBConnection(), SQL_GET_MESSAGE_BY_ID, DebugLevel.ON);
            myLogger.finer("prepared statement=" + String.valueOf(ps));
            ps.setString(1, id);
            myLogger.finer("prepared statement=" + String.valueOf(ps));
            rs = ps.executeQuery();
        }catch(Exception e){
            myLogger.log(Level.SEVERE, "A SQLException was caught while trying to select the message record. Message is: " + e.getMessage(), e);
        }// end try-catch
        try{
            if(rs.next()){
                m.setId(rs.getString("MESSAGE_ID"));
                m.setType(rs.getString("TYPE"));
                m.setMessageBytes(rs.getBytes("MESSAGE"));
                m.setCreateTs(rs.getTimestamp("CREATE_TS"));
            }// end if
        }catch(SQLException e){
            myLogger.log(Level.SEVERE, "A SQLException was caught while trying to get the message record. Message is: " + e.getMessage(), e);
        }finally{// kill the reference to the result set to free memory
            rs = null;
        }// end try-catch
        myLogger.exiting(MY_CLASS_NAME, methodName, m);
        return m;
    }// end getMessageById

    /**
     * This method is used to get all message records from the local db.
     * 
     * @return list of messages
     */
    public List<Message> getAllMessages() {
        String methodName = "getAllMessages";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method is used to get all message records from the local db.");
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<Message> messageList = new ArrayList<Message>();
        try{
            ps = StatementFactory.getStatement(getDBConnection(), SQL_GET_ALL_MESSAGES, DebugLevel.ON);
            myLogger.finer("prepared statement=" + String.valueOf(ps));
            rs = ps.executeQuery();
        }catch(Exception e){
            myLogger.log(Level.SEVERE, "A SQLException was caught while trying to select all the message records. Message is: " + e.getMessage(), e);
        }// end try-catch
        try{
            while(rs.next()){
                Message m = new Message();
                m.setId(rs.getString("MESSAGE_ID"));
                m.setType(rs.getString("TYPE"));
                m.setMessageBytes(rs.getBytes("MESSAGE"));
                m.setCreateTs(rs.getTimestamp("CREATE_TS"));
                messageList.add(m);
            }// end while
        }catch(SQLException e){
            myLogger.log(Level.SEVERE, "A SQLException was caught while trying to get all the message records. Message is: " + e.getMessage(), e);
        }finally{// kill the reference to the result set to free memory
            rs = null;
        }// end try-catch
        myLogger.fine("Number of messages found in the DB: " + (null != messageList ? messageList.size() : "0"));
        myLogger.exiting(MY_CLASS_NAME, methodName, messageList);
        return messageList;
    }// end getAllMessages

    /**
     * This method deletes a record in the Messages table.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param runStatus
     *        (required)
     */
    public void deleteMessage(Message message) {
        String methodName = "deleteMessage";
        myLogger.entering(MY_CLASS_NAME, methodName, message);
        myLogger.finest("This method deletes a record in the Messages table.");
        PreparedStatement ps = null;
        String sql = SQL_DELETE_MESSAGE_BY_ID;
        try{
            myLogger.fine("     Running the following query : " + sql);
            ps = getDBConnection().prepareStatement(sql);
            ps.setString(1, message.getId());
            ps.execute();
            myLogger.fine("          Successfully executed query ");
        }catch(SQLException e){
            myLogger.log(Level.SEVERE, "A SQLException was caught while trying to delete a message record. Message is: " + e.getMessage(), e);
        }// end try/catch
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end deleteMessage

    /**
     * Used to insert a message record to the local db.
     * 
     * @param m
     *        the Message to add to the DB.
     */
    public void insertMessage(Message m) {
        String methodName = "insertMessages";
        myLogger.entering(MY_CLASS_NAME, methodName, m);
        myLogger.finest("Used to insert a message record to the local db.");
        PreparedStatement ps = null;
        try{
            ps = StatementFactory.getStatement(getDBConnection(), SQL_INSERT_MESSAGES, DebugLevel.ON);
            myLogger.finer("prepared statement=" + String.valueOf(ps));
            ps.setString(1, m.getId());
            ps.setString(2, m.getType());
            ps.setBytes(3, m.getMessageBytes());
            ps.execute();
            myLogger.fine("          Successfully executed query ");
        }catch(Exception e){
            myLogger.log(Level.SEVERE, "A SQLException was caught while trying to insert the message record. Message is: " + e.getMessage(), e);
        }// end try-catch
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end insertProperties

    /**
     * Used to get the properties record in the local db.
     * 
     * @return Properties
     */
    public Properties getProperties() {
        String methodName = "getProperties";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("Used to get the properties record in the local db.");
        PreparedStatement ps = null;
        ResultSet rs = null;
        Properties p = new Properties();
        try{
            ps = StatementFactory.getStatement(getDBConnection(), SQL_GET_PROPERTIES, DebugLevel.ON);
            myLogger.finer("prepared statement=" + String.valueOf(ps));
            rs = ps.executeQuery();
            myLogger.finer("          Successfully executed query ");
        }catch(Exception e){
            myLogger.log(Level.SEVERE, "A SQLException was caught while trying to get the properties record. Message is: " + e.getMessage(), e);
        }// end try-catch
        try{
            if(rs != null){
                if(rs.next()){
                    p.setControllerName(rs.getString("CONTROLLER_NAME"));
                    p.setControllerStayAlive(rs.getString("CONTROLLER_STAY_ALIVE"));
                    p.setJmsStayAlive(rs.getString("JMS_STAY_ALIVE"));
                    p.setScheduleRefresh(rs.getString("schedule_refresh"));
                }// end if
            }// end if
        }catch(SQLException e){
            myLogger.log(Level.SEVERE, "A SQLException was caught while trying to get the properties record. Message is: " + e.getMessage(), e);
        }finally{// kill the reference to the result set to free memory
            rs = null;
        }// end try-catch
        myLogger.exiting(MY_CLASS_NAME, methodName, p);
        return p;
    }// end getProperties

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
        // get the current properties record so no values are lost
        PreparedStatement ps = null;
        Properties dbProperties = getProperties();
        try{
            // delete all the properties
            dropTableStmt(SQL_DELETE_PROPERTIES);
            // recreate the properties
            // createTableStmt(SQL_CREATE_PROPERTIES_TABLE);
            // merge the existing properties into the new version.
            if(inProperties.getControllerName() != null){
                dbProperties.setControllerName(inProperties.getControllerName());
            }// end if
            if(inProperties.getControllerStayAlive() != null){
                dbProperties.setControllerStayAlive(inProperties.getControllerStayAlive());
            }// end if
            if(inProperties.getJmsStayAlive() != null){
                dbProperties.setJmsStayAlive(inProperties.getJmsStayAlive());
            }// end if
            if(inProperties.getScheduleRefresh() != null){
                dbProperties.setScheduleRefresh(inProperties.getScheduleRefresh());
            }// end if
            insertProperties(dbProperties);
        }catch(Exception e){
            myLogger.log(Level.SEVERE, "A SQLException was caught while trying to update the properites record. Message is: " + e.getMessage(), e);
        }// end try-catch
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end updateProperties

    /**
     * Used to insert a Batch App record to the local db.
     * 
     * @param batchApp
     *        the batchApp to add to the DB.
     */
    public void insertBatchApp(JmsBatchApp batchApp) {
        String methodName = "insertBatchApp";
        myLogger.entering(MY_CLASS_NAME, methodName, batchApp);
        myLogger.finest("Used to insert a Batch App record to the local db.");
        // First, check to see if this already exists in the local DB. This could happen if the controller can
        // run the batch job independently and as part of a collection.
        if(getBatchAppByBatchAppRefId(batchApp.getBatchAppRefId()) == null){
            PreparedStatement ps = null;
            try{
                ps = StatementFactory.getStatement(getDBConnection(), SQL_INSERT_BATCH_APP, DebugLevel.ON);
                myLogger.finer("prepared statement=" + String.valueOf(ps));
                ps.setInt(1, Integer.parseInt(batchApp.getBatchAppRefId().toString()));
                ps.setInt(2, Integer.parseInt(batchApp.getControllerRefId() != null ? batchApp.getControllerRefId().toString() : "0"));
                ps.setString(3, batchApp.getName().trim());
                ps.setString(4, batchApp.getFileNm());
                ps.setString(5, batchApp.getFileLocation());
                ps.setString(6, batchApp.getDescription());
                ps.setString(7, batchApp.getType());
                ps.execute();
                myLogger.fine("          Successfully executed query ");
            }catch(Exception e){
                myLogger.log(Level.SEVERE, "A SQLException was caught while trying to insert the batchApp record. Message is: " + e.getMessage(), e);
            }// end try-catch
        }else{
            myLogger.finer("The batch app was already in the DB, skipping this insert.");
        }// end if-else
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end insertBatchApp

    /**
     * Used to insert a Batch App record to the local db.
     * 
     * @param batchApp
     *        the batchApp to add to the DB.
     */
    public void insertBatchAppXref(Long collectionRefId, Long associatedRefId, Long appRunSeq) {
        String methodName = "insertBatchApp";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("Used to insert a Batch App record to the local db.");
        PreparedStatement ps = null;
        try{
            ps = StatementFactory.getStatement(getDBConnection(), SQL_INSERT_BATCH_APP_XREF, DebugLevel.ON);
            myLogger.finer("prepared statement=" + String.valueOf(ps));
            ps.setInt(1, collectionRefId.intValue());
            ps.setInt(2, associatedRefId.intValue());
            ps.setInt(3, appRunSeq.intValue());
            ps.execute();
            myLogger.fine("          Successfully executed query ");
        }catch(Exception e){
            myLogger.log(Level.SEVERE, "A SQLException was caught while trying to insert the batchApp record. Message is: " + e.getMessage(), e);
        }// end try-catch
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
        PreparedStatement ps = null;
        try{
            ps = StatementFactory.getStatement(getDBConnection(), SQL_INSERT_SCHEDULE, DebugLevel.ON);
            myLogger.finer("prepared statement=" + String.valueOf(ps));
            ps.setInt(1, Integer.parseInt(schedule.getScheduleRefId().toString()));
            ps.setInt(2, Integer.parseInt(schedule.getBatchAppRefId().toString()));
            ps.setTime(3, schedule.getStartTime());
            ps.setString(4, schedule.getFrequencyCd());
            ps.setString(5, schedule.getFrequencyTypeCd());
            ps.setString(6, schedule.getRepeatCd());
            ps.setDate(7, schedule.getScheduleStartDt());
            ps.setString(8, schedule.getRecurNo());
            ps.setString(9, schedule.getWeekdays());
            ps.setString(10, schedule.getDayNo());
            ps.setString(11, schedule.getWeekNo());
            ps.setString(12, schedule.getActive());
            ps.execute();
            myLogger.fine("          Successfully executed query ");
        }catch(Exception e){
            myLogger.log(Level.SEVERE, "A SQLException was caught while trying to insert the schedule record. Message is: " + e.getMessage(), e);
        }// end try-catch
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end insertSchedule

    /**
     * This method gets the controller record entered controller name.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
     * @return Controller
     */
    public JmsController getController(String controllerName) {
        String methodName = "getController";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method gets the controller record for this controller. It does so by the controller name found in the properties.");
        PreparedStatement ps = null;
        ResultSet rs = null;
        JmsController controller = null;
        try{
            ps = StatementFactory.getStatement(getDBConnection(), SQL_GET_CONTROLLER_BY_ADDRESS, DebugLevel.ON);
            ps.setString(1, controllerName);
            myLogger.finer("prepared statement=" + String.valueOf(ps));
            rs = ps.executeQuery();
            myLogger.fine("          Successfully executed query ");
        }catch(SQLException e){
            myLogger.log(Level.SEVERE, "A SQLException was caught while trying to get the controller record. Message is: " + e.getMessage(), e);
        }// end try/catch
        try{
            if(rs.next()){
                controller = new JmsController();
                controller.setControllerAddress((null != rs.getString("Controller_Address") ? rs.getString("Controller_Address").trim() : ""));
                controller.setControllerName(rs.getString("Controller_Nm"));
                controller.setControllerRefId(rs.getLong("Controller_Ref_Id"));
                controller.setQueueRefId(rs.getLong("Queue_Ref_Id"));
                controller.setDeleteInd(rs.getString(DELETE_IND));
                controller.setCreateTs(rs.getTimestamp(CREATE_TS));
                controller.setCreateUserRefId(rs.getLong(CREATE_USER_REF_ID));
                controller.setUpdateTs(rs.getTimestamp(UPDATE_TS));
                controller.setUpdateUserRefId(rs.getLong(UPDATE_USER_REF_ID));
            }// end if
        }catch(SQLException e){
            myLogger.log(Level.SEVERE, "A SQLException was caught while trying to get the controller record. Message is: " + e.getMessage(), e);
        }finally{// kill the reference to the result set to free memory
            rs = null;
        }// end try-catch
        myLogger.exiting(MY_CLASS_NAME, methodName);
        return controller;
    }// end getController

    /**
     * This method gets all the batch apps for this controller.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
     * @return List of BatchApps
     */
    public List getBatchAppsForController() {
        String methodName = "getBatchAppsForController";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method gets all the batch apps for a particular controller. ");
        PreparedStatement ps = null;
        ResultSet rs = null;
        JmsBatchApp batchApp = null;
        ArrayList<JmsBatchApp> batchAppList = new ArrayList<JmsBatchApp>();
        String sql = SQL_GET_BATCH_APPS;
        try{
            myLogger.finer("     Running the following query : " + sql);
            ps = getDBConnection().prepareStatement(sql);
            rs = ps.executeQuery();
            myLogger.fine("          Successfully executed query ");
        }catch(SQLException e){
            myLogger.log(Level.SEVERE, "A SQLException was caught while trying to get the batch apps for a controller record. Message is: " + e.getMessage(), e);
        }// end try/catch
        try{
            while(rs.next()){
                batchApp = new JmsBatchApp();
                batchApp.setName((null != rs.getString("Batch_Nm") ? rs.getString("Batch_Nm").trim() : ""));
                batchApp.setBatchAppRefId(rs.getLong("Batch_App_Ref_Id"));
                batchApp.setControllerRefId(rs.getLong("Controller_Ref_Id"));
                batchApp.setFileLocation(rs.getString("File_Location"));
                batchApp.setFileNm(rs.getString("File_Nm"));
                batchApp.setDescription(rs.getString("Description"));
                batchApp.setType(rs.getString("Batch_Type_Cd"));
                batchApp.setDeleteInd(rs.getString(DELETE_IND));
                batchApp.setCreateTs(rs.getTimestamp(CREATE_TS));
                batchApp.setCreateUserRefId(rs.getLong(CREATE_USER_REF_ID));
                batchApp.setUpdateTs(rs.getTimestamp(UPDATE_TS));
                batchApp.setUpdateUserRefId(rs.getLong(UPDATE_USER_REF_ID));
                batchAppList.add(batchApp);
            }// end if
        }catch(SQLException e){
            myLogger.log(Level.SEVERE, "A SQLException was caught while trying to get the batch apps for a controller record. Message is: " + e.getMessage(), e);
        }finally{// kill the reference to the result set to free memory
            rs = null;
        }// end try-catch
        myLogger.exiting(MY_CLASS_NAME, methodName);
        return batchAppList;
    }// end getBatchAppsForController

    /**
     * This method gets all the batch apps for a given collection.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
     * @return List of BatchApps
     */
    public List getBatchAppsForCollection(Long collectionRefId) {
        String methodName = "getBatchAppsForCollection";
        myLogger.entering(MY_CLASS_NAME, methodName, collectionRefId);
        myLogger.finest("This method gets all the batch apps for a particular controller. ");
        PreparedStatement ps = null;
        ResultSet rs = null;
        JmsBatchApp batchApp = null;
        ArrayList<JmsBatchApp> batchAppList = new ArrayList<JmsBatchApp>();
        String sql = SQL_GET_COLLECTION_BATCH_APPS;
        try{
            myLogger.finer("     Running the following query : " + sql);
            ps = getDBConnection().prepareStatement(sql);
            ps.setLong(1, collectionRefId);
            rs = ps.executeQuery();
            myLogger.fine("          Successfully executed query ");
        }catch(SQLException e){
            myLogger.log(Level.SEVERE, "A SQLException was caught while trying to get the batch apps for a controller record. Message is: " + e.getMessage(), e);
        }// end try/catch
        try{
            while(rs.next()){
                batchApp = new JmsBatchApp();
                batchApp.setName((null != rs.getString("Batch_Nm") ? rs.getString("Batch_Nm").trim() : ""));
                batchApp.setBatchAppRefId(rs.getLong("Batch_App_Ref_Id"));
                batchApp.setControllerRefId(rs.getLong("Controller_Ref_Id"));
                batchApp.setFileLocation(rs.getString("File_Location"));
                batchApp.setFileNm(rs.getString("File_Nm"));
                batchApp.setDescription(rs.getString("Description"));
                batchApp.setType(rs.getString("Batch_Type_Cd"));
                batchApp.setDeleteInd(rs.getString(DELETE_IND));
                batchApp.setCreateTs(rs.getTimestamp(CREATE_TS));
                batchApp.setCreateUserRefId(rs.getLong(CREATE_USER_REF_ID));
                batchApp.setUpdateTs(rs.getTimestamp(UPDATE_TS));
                batchApp.setUpdateUserRefId(rs.getLong(UPDATE_USER_REF_ID));
                batchAppList.add(batchApp);
            }// end if
        }catch(SQLException e){
            myLogger.log(Level.SEVERE, "A SQLException was caught while trying to get the batch apps for a controller record. Message is: " + e.getMessage(), e);
        }finally{// kill the reference to the result set to free memory
            rs = null;
        }// end try-catch
        myLogger.exiting(MY_CLASS_NAME, methodName);
        return batchAppList;
    }// end getBatchAppsForController

    /**
     * This method gets all the batch Collections for this controller.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
     * @return List of BatchApps
     */
    public List getBatchCollectionsForController() {
        String methodName = "getBatchCollectionsForController";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method gets all the batch collections for a this controller. ");
        PreparedStatement ps = null;
        ResultSet rs = null;
        JmsBatchAppCollection batchAppCollection = null;
        ArrayList<JmsBatchAppCollection> batchAppList = new ArrayList<JmsBatchAppCollection>();
        String sql = SQL_GET_BATCH_COLLECTIONS;
        try{
            myLogger.finer("     Running the following query : " + sql);
            ps = getDBConnection().prepareStatement(sql);
            rs = ps.executeQuery();
            myLogger.fine("          Successfully executed query ");
        }catch(SQLException e){
            myLogger.log(Level.SEVERE, "A SQLException was caught while trying to get the batch collections for a controller record. Message is: " + e.getMessage(), e);
        }// end try/catch
        try{
            while(rs.next()){
                batchAppCollection = new JmsBatchAppCollection();
                batchAppCollection.setName((null != rs.getString("Batch_Nm") ? rs.getString("Batch_Nm").trim() : ""));
                batchAppCollection.setMainBatchApp(rs.getLong("Batch_App_Ref_Id"));
                batchAppCollection.setDescription(rs.getString("Description"));
                batchAppCollection.setDeleteInd(rs.getString(DELETE_IND));
                batchAppCollection.setCreateTs(rs.getTimestamp(CREATE_TS));
                batchAppCollection.setCreateUserRefId(rs.getLong(CREATE_USER_REF_ID));
                batchAppCollection.setUpdateTs(rs.getTimestamp(UPDATE_TS));
                batchAppCollection.setUpdateUserRefId(rs.getLong(UPDATE_USER_REF_ID));
                batchAppList.add(batchAppCollection);
            }// end if
        }catch(SQLException e){
            myLogger.log(Level.SEVERE, "A SQLException was caught while trying to get the batch apps for a controller record. Message is: " + e.getMessage(), e);
        }finally{// kill the reference to the result set to free memory
            rs = null;
        }// end try-catch
        myLogger.exiting(MY_CLASS_NAME, methodName);
        return batchAppList;
    }// end getBatchAppsForController

    /**
     * This method gets a batch Collection by refId.
     * 
     * @param batchAppRefId
     *        refId for the main batch application
     * @author <strong>Shane Duncan</strong> JCCC, Jun 10, 2017
     * @return JmsBatchAppCollection
     */
    public JmsBatchAppCollection getBatchCollectionByRefId(Long batchAppRefId) {
        String methodName = "getBatchCollectionByRefId";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method gets a batch Collection by refId.");
        PreparedStatement ps = null;
        ResultSet rs = null;
        JmsBatchAppCollection batchAppCollection = null;
        String sql = SQL_GET_BATCH_COLLECTION_BY_REF_ID;
        try{
            myLogger.finer("     Running the following query : " + sql);
            ps = getDBConnection().prepareStatement(sql);
            ps.setLong(1, batchAppRefId);
            rs = ps.executeQuery();
            myLogger.fine("          Successfully executed query ");
        }catch(SQLException e){
            myLogger.log(Level.SEVERE, "A SQLException was caught while trying to get the batch collections for a controller record. Message is: " + e.getMessage(), e);
        }// end try/catch
        try{
            if(rs.next()){
                batchAppCollection = new JmsBatchAppCollection();
                batchAppCollection.setName((null != rs.getString("Batch_Nm") ? rs.getString("Batch_Nm").trim() : ""));
                batchAppCollection.setMainBatchApp(rs.getLong("Batch_App_Ref_Id"));
                batchAppCollection.setDescription(rs.getString("Description"));
                batchAppCollection.setDeleteInd(rs.getString(DELETE_IND));
                batchAppCollection.setCreateTs(rs.getTimestamp(CREATE_TS));
                batchAppCollection.setCreateUserRefId(rs.getLong(CREATE_USER_REF_ID));
                batchAppCollection.setUpdateTs(rs.getTimestamp(UPDATE_TS));
                batchAppCollection.setUpdateUserRefId(rs.getLong(UPDATE_USER_REF_ID));
            }// end if
        }catch(SQLException e){
            myLogger.log(Level.SEVERE, "A SQLException was caught while trying to get the batch apps for a controller record. Message is: " + e.getMessage(), e);
        }finally{// kill the reference to the result set to free memory
            rs = null;
        }// end try-catch
        myLogger.exiting(MY_CLASS_NAME, methodName);
        return batchAppCollection;
    }// end getBatchAppsForController

    /**
     * This method get a batch apps by the batchAppRefId
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
     * @param batchAppRefId
     *        (required)
     * @return BatchApp
     */
    public JmsBatchApp getBatchAppByBatchAppRefId(Long batchAppRefId) {
        String methodName = "getBatchAppByBatchAppRefId";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method get a batch apps by the batchAppRefId. Entered with batchAppRefId: " + batchAppRefId);
        PreparedStatement ps = null;
        ResultSet rs = null;
        JmsBatchApp batchApp = null;
        String sql = SQL_GET_BATCH_APPS_BY_BATCH_APP_REF_ID;
        try{
            myLogger.finer("     Running the following query : " + sql);
            ps = getDBConnection().prepareStatement(sql);
            ps.setLong(1, batchAppRefId);
            myLogger.finer("     Using batchAppRefId : " + batchAppRefId);
            rs = ps.executeQuery();
            myLogger.fine("          Successfully executed query ");
        }catch(SQLException e){
            myLogger.log(Level.SEVERE, "A SQLException was caught while trying to get the batch apps record. Message is: " + e.getMessage(), e);
        }// end try/catch
        try{
            if(rs.next()){
                batchApp = new JmsBatchApp();
                batchApp.setName((null != rs.getString("Batch_Nm") ? rs.getString("Batch_Nm").trim() : ""));
                batchApp.setBatchAppRefId(rs.getLong("Batch_App_Ref_Id"));
                batchApp.setControllerRefId(rs.getLong("Controller_Ref_Id"));
                batchApp.setFileLocation(rs.getString("File_Location"));
                batchApp.setFileNm(rs.getString("File_Nm"));
                batchApp.setDescription(rs.getString("Description"));
                batchApp.setType(rs.getString("Batch_Type_Cd"));
                batchApp.setDeleteInd(rs.getString(DELETE_IND));
                batchApp.setCreateTs(rs.getTimestamp(CREATE_TS));
                batchApp.setCreateUserRefId(rs.getLong(CREATE_USER_REF_ID));
                batchApp.setUpdateTs(rs.getTimestamp(UPDATE_TS));
                batchApp.setUpdateUserRefId(rs.getLong(UPDATE_USER_REF_ID));
            }// end if
        }catch(SQLException e){
            myLogger.log(Level.SEVERE, "A SQLException was caught while trying to get the batchapps record. Message is: " + e.getMessage(), e);
        }finally{// kill the reference to the result set to free memory
            rs = null;
        }// end try-catch
        myLogger.finest("Exiting with BatchApp: " + (null != batchApp ? batchApp : "null"));
        myLogger.exiting(MY_CLASS_NAME, methodName);
        return batchApp;
    }// end getBatchAppsForController

    /**
     * This method gets a schedule record by the provided batchAppRefId
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param batchAppRefId
     *        (required)
     * @return Schedule
     */
    public List<JmsSchedule> getScheduleForBatchApp(Long batchAppRefId) {
        String methodName = "getScheduleForBatchApp";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method gets a schedule record by the provided batchAppRefId. Entered with batchAppRefId: " + batchAppRefId);
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<JmsSchedule> scheduleList = new ArrayList<JmsSchedule>();
        JmsSchedule schedule = null;
        String sql = SQL_GET_SCHEDULE_BY_BATCH_APP_REF_ID;
        try{
            myLogger.finer("     Running the following query : " + sql);
            ps = getDBConnection().prepareStatement(sql);
            ps.setLong(1, batchAppRefId);
            myLogger.finer("     Using batchAppRefId : " + batchAppRefId);
            rs = ps.executeQuery();
            myLogger.fine("          Successfully executed query ");
        }catch(SQLException e){
            myLogger.log(Level.SEVERE, "A SQLException was caught while trying to get the Schedule for a BatchApp record. Message is: " + e.getMessage(), e);
        }// end try/catch
        try{
            while(rs.next()){
                schedule = new JmsSchedule();
                schedule.setBatchAppRefId(rs.getLong("Batch_App_Ref_Id"));
                schedule.setScheduleRefId(rs.getLong("Schedule_Ref_Id"));
                schedule.setStartTime(rs.getTime("Start_Time"));
                schedule.setFrequencyCd(rs.getString("Frequency_Cd"));
                schedule.setScheduleStartDt(rs.getDate("Schedule_Start_Dt"));
                schedule.setFrequencyTypeCd(rs.getString("Frequency_Type_Cd"));
                schedule.setRepeatCd(rs.getString("Repeat_Cd"));
                schedule.setRecurNo(rs.getString("Recur_No"));
                schedule.setWeekdays(null != rs.getString("Weekdays") ? rs.getString("Weekdays") : "");
                schedule.setDayNo(rs.getString("Day_No"));
                schedule.setWeekNo(rs.getString("Week_No"));
                schedule.setActive(rs.getString("Active_Ind"));
                schedule.setDeleteInd(rs.getString(DELETE_IND));
                schedule.setCreateTs(rs.getTimestamp(CREATE_TS));
                schedule.setCreateUserRefId(rs.getLong(CREATE_USER_REF_ID));
                schedule.setUpdateTs(rs.getTimestamp(UPDATE_TS));
                schedule.setUpdateUserRefId(rs.getLong(UPDATE_USER_REF_ID));
                scheduleList.add(schedule);
            }// end if
        }catch(SQLException e){
            myLogger.log(Level.SEVERE, "A SQLException was caught while trying to get the Schedule for a BatchApp record. Message is: " + e.getMessage(), e);
        }finally{// kill the reference to the result set to free memory
            rs = null;
        }// end try-catch
        myLogger.exiting(MY_CLASS_NAME, methodName);
        return scheduleList;
    }// end getScheduleForBatchApp

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
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method gets the most recent run status for a schedule.  It's basically the last scheduled run. Entered with scheduleRefId: " + scheduleRefId);
        PreparedStatement ps = null;
        ResultSet rs = null;
        JmsRunStatus runStatus = null;
        String sql = SQL_GET_MOST_RECENT_RUN_STATUS_FOR_SCHEDULE;
        try{
            myLogger.finer("     Running the following query : " + sql);
            ps = getDBConnection().prepareStatement(sql);
            ps.setLong(1, scheduleRefId);
            ps.setLong(2, scheduleRefId);
            myLogger.finer("     Using scheduleRefId : " + scheduleRefId);
            rs = ps.executeQuery();
            myLogger.fine("          Successfully executed query ");
        }catch(SQLException e){
            myLogger.log(Level.SEVERE, "A SQLException was caught while trying to get the most recent runStatus for a schedule record. Message is: " + e.getMessage(), e);
        }// end try/catch
        try{
            if(rs.next()){
                runStatus = new JmsRunStatus();
                runStatus.setBatchAppRefId(rs.getLong("Batch_App_Ref_Id"));
                runStatus.setScheduleRefId(rs.getLong("Schedule_Ref_Id"));
                runStatus.setRunStatusRefId(rs.getLong("Run_Status_Ref_Id"));
                runStatus.setStartTs(rs.getTimestamp("Start_Ts"));
                runStatus.setStopTs(rs.getTimestamp("Stop_Ts"));
                runStatus.setResultCd(rs.getString("Result_Cd"));
                runStatus.setResultDetail(rs.getString("Result_Detail"));
                runStatus.setDescription(rs.getString("Description"));
                runStatus.setDeleteInd(rs.getString(DELETE_IND));
                runStatus.setCreateTs(rs.getTimestamp(CREATE_TS));
                runStatus.setCreateUserRefId(rs.getLong(CREATE_USER_REF_ID));
                runStatus.setUpdateTs(rs.getTimestamp(UPDATE_TS));
                runStatus.setUpdateUserRefId(rs.getLong(UPDATE_USER_REF_ID));
            }// end if
        }catch(SQLException e){
            myLogger.log(Level.SEVERE, "A SQLException was caught while trying to get the most recent runStatus for a schedule record. Message is: " + e.getMessage(), e);
        }finally{// kill the reference to the result set to free memory
            rs = null;
        }// end try-catch
        myLogger.exiting(MY_CLASS_NAME, methodName);
        return runStatus;
    }// end getScheduleForBatchApp

    /**
     * This method inserts a record into the run status table.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param runStatus
     *        (required)
     */
    public JmsRunStatus insertRunStatus(JmsRunStatus runStatus) {
        String methodName = "insertRunStatus";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method inserts a record into the run status table. Entered with runStatus: " + runStatus);
        PreparedStatement ps = null;

        String sql = SQL_INSERT_RUN_STATUS;
        try{
            myLogger.finer("     Running the following query : " + sql);
            ps = getDBConnection().prepareStatement(sql);
            ps.setLong(1, runStatus.getBatchAppRefId());
            ps.setString(2, (runStatus.getScheduleRefId() != null ? runStatus.getScheduleRefId().toString() : null));
            ps.setTimestamp(3, runStatus.getStartTs());
            if(runStatus.getStopTs() == null){
                ps.setString(4, Constants.DEFAULT_TIMESTAMP);
            }else{
                ps.setTimestamp(4, runStatus.getStopTs());
            }// end else if
            ps.setString(5, (runStatus.getStatusCd() != null ? runStatus.getStatusCd() : ""));
            ps.setString(6, (runStatus.getResultCd() != null ? runStatus.getResultCd() : ""));
            ps.setString(7, (runStatus.getResultDetail() != null ? runStatus.getResultDetail() : ""));
            ps.setString(8, (runStatus.getDescription() != null ? runStatus.getDescription() : ""));
            ps.setLong(9, runStatus.getCreateUserRefId());
            ps.setTimestamp(10, runStatus.getCreateTs());
            myLogger.finer("     Using runStatus : " + runStatus);
            ps.execute();
            myLogger.fine("          Successfully executed query ");
        }catch(SQLException e){
            myLogger.log(Level.SEVERE, "A SQLException was caught while trying to insert the runStatus record. Message is: " + e.getMessage(), e);
        }// end try/catch
        myLogger.exiting(MY_CLASS_NAME, methodName, runStatus);
        return runStatus;
    }// end insertRunStatus

    /**
     * This method updates a record in the run status table.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param runStatus
     *        (required)
     */
    public JmsRunStatus updateRunStatus(JmsRunStatus runStatus) {
        String methodName = "updateRunStatus";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method updates a record in the run status table. Entered with runStatus: " + runStatus);
        PreparedStatement ps = null;

        String sql = SQL_UPDATE_RUN_STATUS;
        try{
            myLogger.finer("     Running the following query : " + sql);
            ps = getDBConnection().prepareStatement(sql);
            ps.setLong(1, runStatus.getBatchAppRefId());
            ps.setString(2, (runStatus.getScheduleRefId() != null ? runStatus.getScheduleRefId().toString() : null));
            ps.setTimestamp(3, runStatus.getStartTs());
            if(runStatus.getStopTs() == null){
                ps.setString(4, Constants.DEFAULT_TIMESTAMP);
            }else{
                ps.setTimestamp(4, runStatus.getStopTs());
            }// end else if
            ps.setString(5, (runStatus.getStatusCd() != null ? runStatus.getStatusCd() : ""));
            ps.setString(6, (runStatus.getResultCd() != null ? runStatus.getResultCd() : ""));
            ps.setString(7, (runStatus.getResultDetail() != null ? runStatus.getResultDetail() : ""));
            ps.setString(8, (runStatus.getDescription() != null ? runStatus.getDescription() : ""));
            ps.setLong(9, runStatus.getCreateUserRefId());
            ps.setTimestamp(10, runStatus.getCreateTs());
            ps.setLong(11, runStatus.getRunStatusRefId());
            myLogger.finer("     Using runStatus : " + runStatus);
            ps.execute();
            myLogger.fine("          Successfully executed query ");
        }catch(SQLException e){
            myLogger.log(Level.SEVERE, "A SQLException was caught while trying to get the update the runStatus record. Message is: " + e.getMessage(), e);
        }// end try-catch
        myLogger.exiting(MY_CLASS_NAME, methodName, runStatus);
        return runStatus;
    }// end updateRunStatus
}// end class
