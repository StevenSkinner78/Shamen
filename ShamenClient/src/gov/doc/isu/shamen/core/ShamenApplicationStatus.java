/**
 * @(#)ShamenApplicationStatus.java 1.0.0 Copyright (c) 2018 Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit All Rights Reserved. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: -Redistribution of source code must retain the above copyright notice, this list of conditions and the following disclaimer. -Redistribution in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. Neither the name of Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit or the names of contributors may be used to endorse or promote products derived from this software without specific prior written permission. This software is provided "AS IS," without a warranty of any kind. ALL EXPRESS OR IMPLIED
 *                                  CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. You
 *                                  acknowledge that this software is not designed, licensed or intended for use in the design, construction, operation or maintenance of any nuclear facility.
 */
package gov.doc.isu.shamen.core;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

/**
 * This class contains the information on this application's status, as provided for by the Shamen JMS application.
 * <p>
 * <font size="3">It is a singleton.</font>
 * </p>
 * 
 * @author <strong>Shane Duncan</strong> JCCC, Mar 28, 2016
 * @author <strong>Steven Skinner</strong> JCCC, 10/01/2019
 */
public class ShamenApplicationStatus implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static ShamenApplicationStatus instance;
    private String action;
    private String status;
    private String statusReason;
    private String statusTimeInSeconds;
    private String replyRequired;
    private HashMap<String, String> messageMap;
    private HashMap<String, Boolean> variableMap;
    private ShamenApplicationCipher sc;

    // These are the keys and values used in application messages.
    // Keys
    public static final String ACTION = "ACTION";
    public static final String STATUS = "STATUS";
    public static final String STATUS_REASON = "STATUS_REASON";
    public static final String STATUS_TIME_SECONDS = "STATUS_TIME_SECONDS";
    public static final String REPLY_REQUIRED = "REPLY_REQUIRED";
    public static final String APPLICATION_NAME = "APPLICATION_NAME";
    public static final String APPLICATION_ENVIRONMENT = "APPLICATION_ENVIRONMENT";
    public static final String APPLICATION_BRANCH = "APPLICATION_BRANCH";
    public static final String APPLICATION_INSTANCE = "APPLICATION_INSTANCE";
    public static final String APPLICATION_VERSION = "APPLICATION_VERSION";
    public static final String APPLICATION_EAR = "APPLICATION_EAR";
    public static final String APPLICATION_ADDNTL_INFO = "APPLICATION_ADDNTL_INFO";
    public static final String PASSWORD = "PASSWORD";
    public static final String BATCH_APPS = "BATCH_APPS";
    public static final String RUN_STATUS_COUNT = "RUN_STATUS_COUNT";
    public static final String BATCH_APP_NM = "BATCH_APP_NM";
    public static final String BATCH_APP_REF_ID = "BATCH_APP_REF_ID";
    public static final String RUN_NUMBER = "RUN_NUMBER";
    public static final String RUN_STATUS_SUMMARIES = "RUN_STATUS_SUMMARIES";
    public static final String RUN_STATUS_LIST = "RUN_STATUS_INFO";
    public static final String FILTER_RUN_STATUS = "FILTER_RUN_STATUS";
    public static final String PAGE_SIZE = "PAGE_SIZE";
    public static final String PAGING_START_ROW = "START_ROW";
    public static final String PAGING_END_ROW = "END_ROW";
    public static final String REF_ID = "REF_ID";
    public static final String BATCH_APP_ATTR = "BATCH_APP_ATTR";
    public static final String USER_REF_ID = "USER_REF_ID";
    public static final String JOB_PARAMETERS = "JOB_PARAMETERS";
    public static final String STATUS_PARAMETERS = "STATUS_PARAMETERS";

    // Values (all begin with their corresponding key)
    public static final String ACTION_ESTABLISH_HANDSHAKE = "ESTABLISH_HANDSHAKE";
    public static final String ACTION_CHANGE_YOUR_STATUS = "STATUS_CHANGE";
    public static final String ACTION_REGISTER = "REGISTER_ME";
    public static final String ACTION_REGISTER_FROM_CLIENT = "REGISTER_ME_ORIGINATING_FROM_CLIENT";
    public static final String ACTION_REPORT_STATUS = "GIVE_ME_STATUS";
    public static final String ACTION_HERE_IS_MY_STATUS = "HERE_IS_MY_STATUS";
    public static final String ACTION_MESSAGE_CONFIRMED = "MESSAGE_CONFIRMED";
    public static final String ACTION_GET_APP_BATCH_JOBS = "GET_APP_BATCH_JOBS";
    public static final String ACTION_GIVE_ME_BATCH_JOB = "GIVE_ME_BATCH_JOB";
    public static final String ACTION_DEATH_NOTIFICATION = "I CROAKED!";
    public static final String ACTION_RUN_BATCH = "RUN_BATCH_JOB";
    public static final String ACTION_GET_APP_NOTIFICATION = "GET_APP_NOTIFICATION";
    public static final String ACTION_GET_RUN_STATUS_SUMMARIES = "GET_RUN_STATUS_SUMMARIES";
    public static final String ACTION_GET_RUN_STATUS_INFO = "GET_RUN_STATUS_INFO";
    public static final String SHOW_APP_NOTIFICATION = "SHOW_APP_NOTIFICATION";
    public static final String APP_NOTIFICATION_DETAIL = "APP_NOTIFICATION_DETAIL";

    public static final String STATUS_SUSPENDED = "SUP";
    public static final String STATUS_ACTIVE = "ACT";
    public static final String STATUS_INFORMATION = "INF";

    // Name of the selector for client applications
    public static final String QUEUE_APPLICATION_SELECTOR_1 = "application";
    public static final String QUEUE_APPLICATION_SELECTOR_2 = "environment";
    public static final String QUEUE_APPLICATION_SELECTOR_3 = "instance";

    /**
     * This method makes sure that this class is a singleton
     * 
     * @return instance
     * @author <strong>Shane Duncan</strong> JCCC, Oct 7, 2015
     */
    public static ShamenApplicationStatus getInstance() {
        if(instance == null){
            instance = new ShamenApplicationStatus();
        }// end if
        return instance;
    }// end getInstance

    /**
     * This is a private constructor created to process this class as a singleton.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Mar 29, 2016
     */
    public ShamenApplicationStatus() {
        super();
        this.variableMap = new HashMap<String, Boolean>();

    }// end constructor

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Mar 28, 2016
     * @return the action
     */
    public String getAction() {
        return action;
    }// end getAction

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Mar 28, 2016
     * @param action
     *        the action to set
     */
    public void setAction(String action) {
        this.action = action;

    }// end setAction

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Mar 28, 2016
     * @return the status
     */
    public String getStatus() {
        return status;
    }// end getStatus

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Mar 28, 2016
     * @param status
     *        the status to set
     */
    public void setStatus(String status) {
        this.status = status;

    }// end setStatus

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Mar 28, 2016
     * @return the statusReason
     */
    public String getStatusReason() {
        return statusReason;
    }// end getStatusReason

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Mar 28, 2016
     * @param statusReason
     *        the statusReason to set
     */
    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;

    }// end setStatusReason

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Mar 28, 2016
     * @return the statusTimeInSeconds
     */
    public String getStatusTimeInSeconds() {
        return statusTimeInSeconds;
    }// end getStatusTimeInSeconds

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Mar 28, 2016
     * @param statusTimeInSeconds
     *        the statusTimeInSeconds to set
     */
    public void setStatusTimeInSeconds(String statusTimeInSeconds) {
        this.statusTimeInSeconds = statusTimeInSeconds;

    }// end setStatusTimeInSeconds

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Mar 28, 2016
     * @return the messageMap
     */
    public HashMap<String, String> getMessageMap() {
        return messageMap;
    }// end getMessageMap

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Mar 28, 2016
     * @param messageMap
     *        the messageMap to set
     */
    public void setMessageMap(HashMap<String, String> messageMap) {
        this.messageMap = messageMap;

    }// end setMessageMap

    /**
     * This loads the messageMap and it's child variables.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Mar 28, 2016
     */
    public void loadMessage(HashMap<String, String> messageMap) {
        setMessageMap(messageMap);
        if(messageMap.containsKey(ACTION)){
            setAction(messageMap.get(ACTION));
        }// end if
        if(messageMap.containsKey(STATUS)){
            setStatus(messageMap.get(STATUS));
        }// end if
        if(messageMap.containsKey(STATUS_REASON)){
            setStatusReason(messageMap.get(STATUS_REASON));
        }// end if
        if(messageMap.containsKey(STATUS_TIME_SECONDS)){
            setStatusTimeInSeconds(messageMap.get(STATUS_TIME_SECONDS));
        }// end if
        if(messageMap.containsKey(REPLY_REQUIRED)){
            setReplyRequired(messageMap.get(REPLY_REQUIRED));
        }// end if

    }// end loadMessage

    /**
     * This method returns the status date as a java.util.Date
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Mar 29, 2016
     * @return date
     */
    public Date getDateAsUtilDate() {
        Date returnDate = new Date();
        if(getStatusTimeInSeconds().isEmpty()){
            returnDate = null;
        }else{
            returnDate.setTime(Long.valueOf(getStatusTimeInSeconds()));
        }// end else-if
        return returnDate;
    }// end getDateAsUtilDate

    /**
     * If the action key = SUSPENDED, return true--else false.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Mar 29, 2016
     * @return
     */
    public Boolean isSuspended() {
        return (STATUS_SUSPENDED.equalsIgnoreCase(status));
    }// end isSuspended

    /**
     * If the action key = INFORMATION, return true--else false.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Mar 29, 2017
     * @return
     */
    public Boolean isInformation() {
        return (STATUS_INFORMATION.equalsIgnoreCase(status));
    }// end isInformation

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Mar 29, 2016
     * @return the replyRequired
     */
    public String getReplyRequired() {
        return replyRequired;
    }// end getReplyRequired

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Mar 29, 2016
     * @param replyRequired
     *        the replyRequired to set
     */
    public void setReplyRequired(String replyRequired) {
        this.replyRequired = replyRequired;

    }// end setReplyRequired

    /**
     * Set the default message for this application.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Mar 30, 2016
     */
    public void setDefaultStatus() {
        HashMap<String, String> messageMap = new HashMap<String, String>();
        messageMap.put(STATUS, STATUS_ACTIVE);
        messageMap.put(APPLICATION_NAME, ShamenConstants.CLIENT_SELECTOR_VALUE);
        messageMap.put(APPLICATION_ENVIRONMENT, ShamenConstants.CLIENT_SELECTOR_VALUE_2);
        setMessageMap(messageMap);

    }// end setDefaultStatus

    /**
     * Return whether or not the handshake has been established for this application. If it has a password, then it has been established.
     * 
     * @return true or false
     */
    public Boolean isHandshakeEstablished() {
        Boolean established = false;
        if(sc != null){
            if(sc.getPassword() != null){
                established = true;
            }// end if
        }// end if
        return established;
    }// end isHandshakeEstablished

    /**
     * @return the sc
     */
    public ShamenApplicationCipher getSc() {
        return sc;
    }// end getSc

    /**
     * @param sc
     *        the sc to set
     */
    public void setSc(ShamenApplicationCipher sc) {
        this.sc = sc;

    }// end setSc

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer builder = new StringBuffer();
        builder.append("ShamenApplicationStatus [action=");
        builder.append(action);
        builder.append(", status=");
        builder.append(status);
        builder.append(", statusReason=");
        builder.append(statusReason);
        builder.append(", statusTimeInSeconds=");
        builder.append(statusTimeInSeconds);
        builder.append(", replyRequired=");
        builder.append(replyRequired);
        builder.append(", messageMap=");
        builder.append(messageMap);
        builder.append(", variableMap=");
        builder.append(variableMap);
        builder.append(", sc=");
        builder.append(sc);
        builder.append("]");
        return builder.toString();
    }// end toString

    /**
     * @return the variableMap
     */
    public HashMap<String, Boolean> getVariableMap() {
        return variableMap;
    }// end getVariableMap

    /**
     * @param variableMap
     *        the variableMap to set
     */
    public void setVariableMap(HashMap<String, Boolean> variableMap) {
        this.variableMap = variableMap;
    }// end setVariableMap

}// end class
