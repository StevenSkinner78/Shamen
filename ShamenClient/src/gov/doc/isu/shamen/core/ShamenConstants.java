/**
 * @(#)ShamenConstants.java 1.0.0 Copyright (c) 2018 Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit All Rights Reserved. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: -Redistribution of source code must retain the above copyright notice, this list of conditions and the following disclaimer. -Redistribution in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. Neither the name of Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit or the names of contributors may be used to endorse or promote products derived from this software without specific prior written permission. This software is provided "AS IS," without a warranty of any kind. ALL EXPRESS OR IMPLIED
 *                          CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. You
 *                          acknowledge that this software is not designed, licensed or intended for use in the design, construction, operation or maintenance of any nuclear facility.
 */
package gov.doc.isu.shamen.core;

/**
 * Shamen Application Constants class. These constants are used by the Shamen Application Controller portion of this project.
 * 
 * @author Shane B. Duncan
 * @author Steven Skinner
 */
public class ShamenConstants {

    // JMS Constants
    public static final String CONNECTION_FACTORY = "CO_SconT";
    public static final String CONNECTION_FACTORY_VALUE = "CO_SconT";
    public static String PROVIDER_URL = "THIS_IS_SET_AT_RUNTIME";
    public static final String INITIAL_CONTEXT_FACTORY = "com.ibm.websphere.naming.WsnInitialContextFactory";

    // Name of the topic used by Shamen.
    public static final String TOPIC_NAME = "CO.WebApp.TOPIC";

    // Selector used to only get messages from the topic for this application
    public static final String CLIENT_SELECTOR = "application = ";
    public static final String CLIENT_SELECTOR_2 = "environment = ";
    public static final String CLIENT_SELECTOR_3 = "instance = ";

    // This application's unique name.
    public static String CLIENT_SELECTOR_VALUE = "THIS_IS_SET_AT_RUNTIME";
    // This application's unique environment characteristics. This is set at runtime from values in the ShamenApplicationProperties file!!!!
    public static String CLIENT_SELECTOR_VALUE_2 = "THIS_IS_SET_AT_RUNTIME";
    public static String CLIENT_SELECTOR_VALUE_3 = "THIS_IS_SET_AT_RUNTIME";
    public static String CLIENT_EAR = "THIS_IS_SET_AT_RUNTIME";
    public static String CLIENT_VERSION = "THIS_IS_SET_AT_RUNTIME";
    public static String CLIENT_BRANCH = "THIS_IS_SET_AT_RUNTIME";
    public static String CLIENT_ADDNTL_INFO = "THIS_IS_SET_AT_RUNTIME";
    public static boolean USE_SHAMEN = false;

    public static final String MQ_USER = "MQ_USER";
    public static final String MQ_PASSWORD = "MQ_PASSWORD";

    public static String MQ_USER_VALUE = "THIS_IS_SET_AT_RUNTIME";
    public static String MQ_PASSWORD_VALUE = "THIS_IS_SET_AT_RUNTIME";

    // These are the application's name variables in the resource file.
    public static final String CLIENT_PROPERTY_NAME = "applicationName";
    public static final String CLIENT_PROPERTY_ENV = "applicationEnvironment";
    public static final String CLIENT_PROPERTY_PROVIDER_URL = "providerURL";
    public static final String CLIENT_PROPERTY_EAR = "ear";
    public static final String CLIENT_PROPERTY_VERSION = "version";
    public static final String CLIENT_PROPERTY_BRANCH = "branch";
    public static final String CLIENT_PROPERTY_ADDNTL_INFO = "additional";
    public static final String CLIENT_PROPERTY_MQ_USER = "mqUser";
    public static final String CLIENT_PROPERTY_MQ_PASSWORD = "mqPassword";

    // Background image for suspend page
    public static final String BACKGROUND_IMAGE = "ShamenHeaderSvg.txt";

    // Background image for suspend page
    public static final String HEADER_DATA = "ShamenHeaderHtml.txt";

    // Name of the request queue used by controller.
    public static final String SHAMEN_REQUEST_Q = "CO.ShamWeb.REQST";

    // Name of the parameter used in ListBatchTagFilter to indicate only show the detail.
    public static final String TAG_BATCH_APP_REF_ID = "batchAppRefId";
    public static final String TAG_PARENT_URL = "parentURL";

    public static final String SVG_END = "</svg>";
    public static final String SVG_START = "<svg xmlns=\"http://www.w3.org/2000/svg\" display=\"none\">";
    public static final String HEADER_ICON = "<svg xmlns=\"http://www.w3.org/2000/svg\" height=\"150px\" width=\"960px\"><use xmlns:xlink=\"http://www.w3.org/1999/xlink\" xlink:href=\"#shamen_clients_head\" /></svg>";
    public static final String ICON_SCHEDULE_NO_SCHEDULE_TITLE = "No Schedules Exist";
    public static final String ICON_SCHEDULES_ACTIVE_TITLE = "One or More Active Batch Schedules";
    public static final String ICON_SCHEDULES_INACTIVE_TITLE = "No Active Batch Schedules";
    public static final String ICON_SCHEDULE_ACTIVE_TITLE = "Batch Schedule is ACTIVE";
    public static final String ICON_SCHEDULE_INACTIVE_TITLE = "Batch Schedule is INACTIVE";
    public static final String ICON_CONTROLLER_CONNECTED_TITLE = "Controller is Connected";
    public static final String ICON_CONTROLLER_DISCONNECTED_TITLE = "Controller is Unresponsive";
    public static final String ICON_CONTROLLER_WAITING_TITLE = "Controller is Awaiting Response";
    public static final String ICON_LASTRUN_GOOD_TITLE = "Schedule Last Run Result - Successful";
    public static final String ICON_LASTRUN_BAD_TITLE = "Schedule Last Run Result - Unsuccessful";
    public static final String ICON_LASTRUN_UNKNOWN_TITLE = "Schedule Last Run Result - Unknown";
    public static final String ICON_LASTRUN_MULTIPLE_TITLE = "Schedule Last Run Result - Unknown, Multiple Schedules Detected";
    public static final String ICON_LASTRUN_INPROGRESS_TITLE = "Schedule Last Run Result - In Progress";
    public static final String ICON_LASTRUN_OFFSCHEDULE_TITLE = "Schedule Last Run Result - Off Schedule";
    public static final String ICON_LASTRUN_PENDING_TITLE = "Schedule Last Run Result - Pending";
    public static final String ICON_LASTRUN_INACTIVE_TITLE = "Schedule Last Run Result - Inactive";
    public static final String ICON_LASTRUN_NO_SCHEDULE_TITLE = "Schedule Last Run Result - No Schedule";
    public static final String CONTROLLER_STATUS_CD_CONNECTED = "CON";
    public static final String CONTROLLER_STATUS_CD_WAITING = "AWR";
    public static final String LASTRUN_STATUS_CD_GOOD = "SUC";
    public static final String LASTRUN_STATUS_CD_BAD = "UNS";
    public static final String LASTRUN_STATUS_CD_INPROGRESS = "BLA";
    public static final String LASTRUN_STATUS_CD_OFFSCHEDULE = "OFS";
    public static final String LASTRUN_STATUS_CD_PENDING = "PND";
    public static final String LASTRUN_STATUS_CD_INACTIVE = "INA";
    public static final String LASTRUN_STATUS_CD_MULTIPLE = "MUL";

    // Name of the parameter used in ListBatchTagFilter to indicate only show the detail.
    public static final String TAG_RUN_JOB = "runJob";
    public static final String TAG_SHOW_DETAIL = "showDetail";
    public static final String TAG_FILTER_TABLE = "filterTable";
    public static final String TAG_RUN_JOB_PARAMETERS = "jobParameters";
    public static final String TAG_RUN_JOB_DEFAULT_PARAMETERS = "defaultParameter";
    public static final String PARAMETER_PAGE = "p";
}// end class
