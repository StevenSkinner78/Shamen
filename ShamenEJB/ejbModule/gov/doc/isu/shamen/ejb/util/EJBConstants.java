package gov.doc.isu.shamen.ejb.util;

/**
 * Application Constants class.
 *
 * @author Steven L. Skinner
 */
public class EJBConstants {

    public static final String DEFAULT_PAGE_SIZE = "25";
    public static final String REG_EX_LOC = "^[A-Za-z \\-]*$";
    public static final String REG_EX_NUM = "^[0-9]*$";
    public static final String REG_EX_RECUR_NUM = "^[1-9]*$";
    public static final String REG_EX_NAME = "^[A-Za-z \\-'\\.]*$";
    public static final String REG_EX_DATE = "^\\d{2}/\\d{2}/\\d{4}$";
    public static final String REG_EX_MONTH = "^\\d{2}$";
    public static final String REG_EX_MONTH_YEAR = "^\\d{2}/\\d{4}$";
    public static final String REG_EX_YEAR = "^\\d{4}$";
    public static final String REG_EX_TIME = "^([0-2][0-9]|2[0-3])[:][0-5][0-9]$";
    public static final String[] DAYS_OF_WEEK = {"SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"};
    public static final String[] BATCH_APP_FIELDS = {"Batch App Name", "Start Date", "Start Time", "Frequency", "File Name", "Controller"};
    public static final String[] APPLICATION_FIELDS = {"Verified Status", "Requested Status", "Application Name", "Application Type", "Application Location"};
    // System property for new line
    public static final String DEFAULT_NEW_LINE = System.getProperty("line.separator");
    // Comma
    public static final String COMMA = ",";
    // JMS Constants

    // Message selector property to be used to distinguish messages for controller messages.
    public static final String MESSAGE_SELECTOR = "controller";
    // Message selector property to be used to distinguish messages for controller messages.
    public static final String MESSAGE_SELECTOR_APPLICATION = "application";

    // Name of the connection factory
    public static final String CONNECTION_FACTORY_NAME = "CO_SconT";

    // JMS Constants
    public static final String CONNECTION_FACTORY = "CO_SconT";
    public static final String CONNECTION_FACTORY_VALUE = "CO_SconT";
    public static final String PROVIDER_URL = "THIS_SET_BY_BUILD";// this value is used to be flipped in the build script
    public static final String INITIAL_CONTEXT_FACTORY = "com.ibm.websphere.naming.WsnInitialContextFactory";

    // Name of the topic used by controller.
    public static final String TOPIC = "CO.Control.TOPIC";
    public static final String TOPIC_SHAMEN = "CO.Shamen.TOPIC";
    public static final String SHAMEN_SUBSCRIBER = "SHAMEN_TEST";
    public static final String SHAMEN_REQUEST_Q = "CO.Shamen.REQST";
    public static final String SHAMEN_APPLICATION_REQUEST_Q = "CO.ShamWeb.REQST";
    public static final String SHAMEN_RESPONSE_Q = "SHAMEN_TEST_RESPONSE";
    // Name of the queue used by Shamen web applications.
    public static final String QUEUE_APPLICATION = "CO.WebOut.REQST";

    // Name of the request queue used by applications.
    public static final String APPLICATION_REQUEST_Q = "CO.WebApp.REQST";

    // These are the keys and values used in application messages.
    // Keys
    public static final String ACTION = "ACTION";
    public static final String STATUS = "STATUS";
    public static final String STATUS_REASON = "STATUS_REASON";
    public static final String STATUS_TIME_SECONDS = "STATUS_TIME_SECONDS";
    public static final String REPLY_REQUIRED = "REPLY_REQUIRED";
    public static final String APPLICATION_NAME = "APPLICATION_NAME";
    public static final String APPLICATION_ENVIRONMENT = "APPLICATION_ENVIRONMENT";
    public static final String PASSWORD = "PASSWORD";
    public static final String BATCH_APPS = "BATCH_APPS";
    public static final String REF_ID = "REF_ID";
    public static final String BATCH_APP_ATTR = "BATCH_APP_ATTR";

    // Values (all begin with their corresponding key)
    public static final String ACTION_STATUS_CHANGE = "STATUS_CHANGE";
    public static final String ACTION_GIVE_ME_STATUS = "GIVE_ME_STATUS";
    public static final String ACTION_ESTABLISH_HANDSHAKE = "ESTABLISH_HANDSHAKE";
    public static final String ACTION_MESSAGE_CONFIRMED = "MESSAGE_CONFIRMED";
    public static final String ACTION_GET_APP_BATCH_JOBS = "GET_APP_BATCH_JOBS";
    public static final String ACTION_GIVE_ME_BATCH_JOB = "GIVE_ME_BATCH_JOB";

    public static final String STATUS_SUSPENDED = "SUP";
    public static final String STATUS_ACTIVE = "ACT";

    public static final String DEFAULT_TIMESTAMP = "7799-12-31 00:00:00.000000";
    public static final String BLANK_CODE = "BLA";
    public static final String EMPTY_STRING = "";
    public static final String SDF = "MM/dd/yyyy";
    // private static final String SDFT3 = "MM/dd/yyyy h:mm a";
    public static final String SDFTF = "MM/dd/yyyy HH:mm:ss";
    public static final String SDFT = "MM/dd/yyyy HH:mm";
    public static final String STF = "HH:mm";
    public static final String STFT = "HH:mm:ss";
    public static final String STF3 = "h:mm a";

 // These are the statuses used by the listing screens when last run status from schedule is ascertained.
    public static final String BATCH_STATUS_INACTIVE = "INA";
    public static final String BATCH_STATUS_OFF_SCHEDULE = "OFS";
    public static final String BATCH_STATUS_UNKNOWN = "UNK";
    public static final String BATCH_STATUS_RUNNING = "BLA";
    public static final String BATCH_STATUS_PENDING = "PND";
    public static final String BATCH_STATUS_SUCCESSFUL = "SUC";
    public static final String BATCH_STATUS_UNSUCCESSFUL = "UNS";
    public static final String BATCH_STATUS_NO_SCHEDULE = "NOS";
    // These are the batch app types
    public static final String BATCH_APP_TYPE_CD_COLLECTION = "COL";

    // User Ref Id for client applications
    public static final Long CLIENT_APP_USER_REF_ID = 99L;
}
