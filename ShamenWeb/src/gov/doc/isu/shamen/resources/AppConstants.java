package gov.doc.isu.shamen.resources;

import gov.doc.isu.dwarf.resources.Constants;

/**
 * Application Constants class.
 *
 * @author <strong>Steven Skinner</strong> JCCC, Sep 10, 2015
 */
public class AppConstants extends Constants {

    public static final String DEFAULT_PAGE_SIZE = "25";
    public static final String REG_EX_LOC = "^[A-Za-z \\-]*$";
    public static final String REG_EX_NUM = "^[0-9]*$";
    public static final String REG_EX_RECUR_NUM = "^[1-9]*$";
    public static final String REG_EX_NAME = "^[0-9A-Za-z \\-'\\.]*$";
    public static final String REG_EX_DATE = "^\\d{2}/\\d{2}/\\d{4}$";
    public static final String REG_EX_MONTH = "^\\d{2}$";
    public static final String REG_EX_MONTH_YEAR = "^\\d{2}/\\d{4}$";
    public static final String REG_EX_YEAR = "^\\d{4}$";
    public static final String REG_EX_TIME = "^([0-2][0-9]|2[0-3])[:][0-5][0-9]$";
    public static final String REG_EX_TIME_HOUR = "^([0-2][0-9]|2[0-3])[:]$";
    public static final String[] DAYS_OF_WEEK = {"SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"};
    public static final String[] CONTROLLER_FIELDS = {"Controller Name", "Address", "Default Batch Location"};
    public static final String[] BATCH_APP_FIELDS = {"Batch App Name", "Execution Count", "Type", "System", "Point of Contact"};
    public static final String[] BATCH_APP_COLLECTION_FIELDS = {"Collection Name", "Execution Count", "System", "Point of Contact"};
    public static final String[] APPLICATION_FIELDS = {"Web App Name", "Environment", "Requested Status", "Point of Contact"};
    public static final String[] USER_MANAGEMENT_FIELDS = {"Last Name", "First Name", "User Id", "Email", "Authority"};
    // System property for new line
    public static final String DEFAULT_NEW_LINE = System.getProperty("line.separator");
    // Comma
    public static final String COMMA = ",";
    public static final String DEFAULT_TIMESTAMP = "7799-12-31 00:00:00.000000";
    public static final String BLANK_CODE = "BLA";
    public static final String SHAMEN_DEFAULT_LABEL = "--Choose--";
    public static final String PARAM_PAGESIZE = "pagesize";
    public static final String JMETER_USER_NAME = "JMETER000@ISU.NET";
    public static final String JMETER_PASSWORD = "Jmeter000";
    public static final String CLIENT_SELECTOR_VALUE = "Shamen Web";
    public static final String CLIENT_SELECTOR_VALUE_2 = "PREPROD1";
    public static final String SDFTF = "yyyy-MM-dd HH:mm:ss";
    public static final String SDFT = "MM/dd/yyyy HH:mm";
    public static final String PDF_TYPE = "pdf";
    public static final String TXT_TYPE = "txt";
    public static final String XML_TYPE = "xml";

    // These are the values used by the batch app screens.
    public static final String BAT_FILE = "BAT";
    public static final String DAILY_FREQUENCY_CODE = "DLY";
    public static final String WEEKLY_FREQUENCY_CODE = "WKY";
    public static final String MONTHLY_FREQUENCY_CODE = "MTY";
    public static final String DAY_OF_MONTH_FREQ_TYPE_CODE = "DOM";
    public static final String WEEK_OF_MONTH_FREQ_TYPE_CODE = "MWD";

    // These are the batch app types
    public static final String BATCH_APP_TYPE_CD_COLLECTION = "COL";

    // This is for use in the Stats display
    public static final String HEADER = "HEADER";
    public static final String[] USER_STATS_LABELS = {"System", "BatchApps", "WebApps"};

}
