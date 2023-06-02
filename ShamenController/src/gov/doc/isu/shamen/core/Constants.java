/**
 * @author <strong>Shane Duncan</strong> JCCC, Aug 19, 2015
 */
package gov.doc.isu.shamen.core;

/**
 * Class to store application constants.
 * 
 * @author <strong>Shane Duncan</strong> JCCC, Aug 19, 2015
 */
public class Constants {
    // Name of the connection factory used by controller.
    public static final String CONNECTION_FACTORY = "CONNECTION_FACTORY";
    public static String CONNECTION_FACTORY_VALUE = "CO_SconT";
    // Name of the Jms provider used by controller.
    public static final String PROVIDER_URL = "THIS_SET_BY_BUILD";
    // Name of the Initial Context Factory
    public static final String INITIAL_CONTEXT_FACTORY = "com.ibm.websphere.naming.WsnInitialContextFactory";
    // Name of the request queue used by controller. This is put in properties
    public static final String REQUEST_Q = "REQUEST_Q";
    public static final String REQUEST_Q_VALUE = "CO.Control.REQST";
    // Name of the response queue used by controller. This is put in properties
    public static final String RESPONSE_Q = "RESPONSE_Q";
    // Name of the request queue used by controller.
    public static final String SHAMEN_REQUEST_Q = "CO.Shamen.REQST";
    // Name of the topic used by controller.
    public static final String TOPIC = "CO.Control.TOPIC";    
    // Message selector property to be used to distinguish messages.
    public static final String MESSAGE_SELECTOR = "controller";
    // Property for controller name in the properties.
    public static final String CONTROLLER_NAME = "CONTROLLER_NAME";
    // Property for serial path in the properties.
    public static final String VB_SHELL_NAME = "Shell.vbs";
    // Property for DB file name.
    public static final String DB_FILE_NAME = "database";
    // Property to be used to kill or keep alive threads
    public static final String CONTROLLER_STAY_ALIVE = "CONTROLLER_STAY_ALIVE";
    // Property to be used to kill or keep alive JMS dependent threads
    public static final String JMS_STAY_ALIVE = "JMS_STAY_ALIVE";
    // Property to be used to to tell the Schedule Manager to refresh the schedule
    public static final String SCHEDULE_REFRESH = "schedule_refresh";
    // System property for new line
    public static final String DEFAULT_NEW_LINE = System.getProperty("line.separator");
    // Default timestamp and date formats.
    public static final String DEFAULT_TIMESTAMP = "7799-12-31 00:00:00.000000";
    public static final String SDFT = "MM/dd/yyyy HH:mm";
    public static final String SDFTF = "yyyy-MM-dd HH:mm:ss";
    public static final String PREPARE_MQ = "prepMQ";
    
    public static final String MQ_USER = "MQ_USER";
    public static final String MQ_PASSWORD = "MQ_PASSWORD";
    public static String MQ_USER_VALUE = "THIS_SET_BY_BUILD";
    public static String MQ_PASSWORD_VALUE = "THIS_SET_BY_BUILD";
    
    public static final String INTERNAL_DB_URL = "internalURL";
    public static final String INTERNAL_DB_INIT_URL = "internalInitURL";
    public static final String INTERNAL_DB_USER = "internalUser";
    public static final String INTERNAL_DB_PASSWORD = "internalPassword";
    
    
    // Vb script guts
    public static final String VBSCRIPT[] = {"Set WshShell = CreateObject(\"WScript.Shell\" ) ",
            "WshShell.Run chr(34) & WScript.Arguments(0) & Chr(34) & WScript.Arguments(1), 0 ", "Set WshShell = Nothing "};
}// end class
