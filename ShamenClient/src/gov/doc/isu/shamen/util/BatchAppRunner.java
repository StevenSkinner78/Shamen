/**
 * @(#)BatchAppTag.java 1.0.0 Copyright (c) 2018 Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit All Rights Reserved. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: -Redistribution of source code must retain the above copyright notice, this list of conditions and the following disclaimer. -Redistribution in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. Neither the name of Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit or the names of contributors may be used to endorse or promote products derived from this software without specific prior written permission. This software is provided "AS IS," without a warranty of any kind. ALL EXPRESS OR IMPLIED CONDITIONS,
 *                      REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. You acknowledge that this
 *                      software is not designed, licensed or intended for use in the design, construction, operation or maintenance of any nuclear facility.
 */
package gov.doc.isu.shamen.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.doc.isu.shamen.core.ShamenApplicationStatus;
import gov.doc.isu.shamen.core.ShamenConstants;
import gov.doc.isu.shamen.jms.ApplicationJmsManager;
import gov.doc.isu.shamen.jms.models.JmsBatchApp;
import gov.doc.isu.shamen.jms.models.JmsRunStatus;
import gov.doc.isu.shamen.jms.models.JmsRunStatusSummary;

/**
 * This class retrieves batch app information from Shamen Web and executes a run of that job.
 * 
 * @author <strong>Steven Skinner</strong> JCCC, 12/21/2020
 * @author <strong>Zac Lisle</strong> JCCC, 08/27/2021 - Added getRunStatusSummaries(String batchAppName, String pastDate)
 */
public class BatchAppRunner {

    private final Log log = LogFactory.getLog("gov.doc.isu.shamen.util.BatchAppRunner");

    /**
     * This method sends a message to ShamenWeb to run a batch job.
     * 
     * @param batchAppName
     *        the name registered in Shamen of the batch app to run
     * @param jobParameters
     *        any parameters passed to the batch application
     * @param the
     *        id of the user requesting the batch application to be run
     * @return Boolean
     */
    @SuppressWarnings("unchecked")
    public Boolean runBatchApp(String batchAppName, String jobParameters, String userId) throws Exception {
        log.trace("Entering runBatchApp");
        Boolean confirmed = false;
        if(ShamenConstants.USE_SHAMEN){
            try{
                if(ApplicationJmsManager.getInstance().isConnected()){
                    JmsBatchApp batchApp = getBatchFromShamen(batchAppName);
                    if(batchApp != null){
                        HashMap<String, String> messageMap = new HashMap<String, String>();
                        HashMap<String, Object> receivedMessageMap = new HashMap<String, Object>();
                        messageMap.put(ShamenApplicationStatus.STATUS, ShamenApplicationStatus.getInstance().getStatus());
                        messageMap.put(ShamenApplicationStatus.REPLY_REQUIRED, "true");
                        messageMap.put(ShamenApplicationStatus.REF_ID, String.valueOf(batchApp.getBatchAppRefId()));
                        messageMap.put(ShamenApplicationStatus.USER_REF_ID, userId);
                        messageMap.put(ShamenApplicationStatus.JOB_PARAMETERS, jobParameters);
                        messageMap = ShamenApplicationStatus.getInstance().getSc().encryptApplicationMap(messageMap);
                        messageMap.put(ShamenApplicationStatus.ACTION, ShamenApplicationStatus.ACTION_RUN_BATCH);
                        messageMap.put(ShamenApplicationStatus.APPLICATION_NAME, ShamenConstants.CLIENT_SELECTOR_VALUE);
                        messageMap.put(ShamenApplicationStatus.APPLICATION_ENVIRONMENT, ShamenConstants.CLIENT_SELECTOR_VALUE_2);
                        messageMap.put(ShamenApplicationStatus.APPLICATION_INSTANCE, ShamenConstants.CLIENT_SELECTOR_VALUE_3);
                        log.info("Attempt to run Batch App from ShamenWeb.");
                        receivedMessageMap = ApplicationJmsManager.getInstance().sendPTPWithAcknowledge(ShamenConstants.SHAMEN_REQUEST_Q, messageMap, 5000);
                        if(receivedMessageMap != null){
                            log.debug("Inside if condition with expression: receivedMessageMap != null");
                            log.info("Received a response from ShamenWeb.");
                            receivedMessageMap = ShamenApplicationStatus.getInstance().getSc().decryptApplicationObjectMap(receivedMessageMap);
                            confirmed = true;
                        }// end-if
                    }else{
                        throw new Exception("No Batch App returned from Shamen for batch app named: " + batchAppName);
                    }// end if/else
                }// end if
            }catch(Exception e){
                throw new Exception("Error while trying to run batch app. Message is:" + e.getMessage(), e);
            }// end try/catch
        }// end if
        log.trace("Exiting runBatchApp");
        return confirmed;
    }// end runBatchApp

    /**
     * This method gets the list of batch applications assigned to the web application and returns the requested batch application information.
     * 
     * @param batchName
     *        the name of the batch application
     * @return JmsBatchApp the batch application requested
     */
    @SuppressWarnings("unchecked")
    private JmsBatchApp getBatchFromShamen(String batchName) throws Exception {
        log.trace("Entering getBatchFromShamen");
        HashMap<String, String> messageMap = new HashMap<String, String>();
        HashMap<String, Object> receivedMessageMap = new HashMap<String, Object>();
        List<JmsBatchApp> batchList = new ArrayList<JmsBatchApp>();
        JmsBatchApp batchApp = null;
        messageMap.put(ShamenApplicationStatus.STATUS, ShamenApplicationStatus.getInstance().getStatus());
        messageMap.put(ShamenApplicationStatus.REPLY_REQUIRED, "true");
        messageMap = ShamenApplicationStatus.getInstance().getSc().encryptApplicationMap(messageMap);
        messageMap.put(ShamenApplicationStatus.ACTION, ShamenApplicationStatus.ACTION_GET_APP_BATCH_JOBS);
        messageMap.put(ShamenApplicationStatus.APPLICATION_NAME, ShamenConstants.CLIENT_SELECTOR_VALUE);
        messageMap.put(ShamenApplicationStatus.APPLICATION_ENVIRONMENT, ShamenConstants.CLIENT_SELECTOR_VALUE_2);
        messageMap.put(ShamenApplicationStatus.APPLICATION_INSTANCE, ShamenConstants.CLIENT_SELECTOR_VALUE_3);
        try{
            log.info("Attempt to get Batch App List from ShamenWeb.");
            receivedMessageMap = ApplicationJmsManager.getInstance().sendPTPWithAcknowledge(ShamenConstants.SHAMEN_REQUEST_Q, messageMap, 5000);
            if(receivedMessageMap != null){
                log.debug("Inside if condition with expression: receivedMessageMap != null");
                log.info("Received a response from ShamenWeb.");
                receivedMessageMap = ShamenApplicationStatus.getInstance().getSc().decryptApplicationObjectMap(receivedMessageMap);
                batchList = (ArrayList<JmsBatchApp>) receivedMessageMap.get(ShamenApplicationStatus.BATCH_APPS);
            }// end-if
            if(batchList != null && !batchList.isEmpty()){
                for(JmsBatchApp batchAppHold : batchList){
                    if(batchAppHold.getName().equalsIgnoreCase(batchName)){
                        batchApp = batchAppHold;
                    }// end if
                }// end for
            }// end if

        }catch(Exception e){
            throw new Exception("Exception occurred retrieving batch app from Shamen. Exception is: " + e.getMessage(), e);
        }// end try-catch
        log.trace("Exiting getBatchFromShamen");
        return batchApp;
    }// end getBatchFromShamen

    /**
     * This method retrieves a list of run status summaries for a specific batch application within a specified date range.
     * <p>
     * <i>Date range is calculated starting from todays day back to the pastDate provided.</i>
     * <p>
     * <b>At most the top 50 Run Status Summaries will be retrieved, regardless of the date range provided.</b>
     * 
     * @param batchAppName
     *        the name registered in Shamen of the batch app to run
     * @param pastDate
     *        <b> Required Date format: yyyy-MM-dd </b> the latest date to search back run status summaries for
     * @return <code> List</code> of {@link JmsRunStatusSummary}
     */
    @SuppressWarnings("unchecked")
    public List<JmsRunStatusSummary> getRunStatusSummaries(String batchAppName, String pastDate) throws Exception {
        log.trace("Entering getRunStatusSummaries");
        List<JmsRunStatusSummary> summaryList = new ArrayList<JmsRunStatusSummary>();
        if(StringUtils.isNotBlank(pastDate)){
            try{
                LocalDate.parse(pastDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }catch(Exception e){
                throw new Exception("Incorrect date format for pastDate. Format must match the prescribed pattern. (yyyy-MM-dd)");
            }// end try/catch
        }else{
            throw new Exception("Null or Empty value was found for parameter pastDate.");
        }// end if/else
        if(ShamenConstants.USE_SHAMEN){
            try{
                if(ApplicationJmsManager.getInstance().isConnected()){
                    HashMap<String, String> messageMap = new HashMap<String, String>();
                    HashMap<String, Object> receivedMessageMap = new HashMap<String, Object>();
                    messageMap.put(ShamenApplicationStatus.STATUS, ShamenApplicationStatus.getInstance().getStatus());
                    messageMap.put(ShamenApplicationStatus.REPLY_REQUIRED, "true");
                    messageMap.put(ShamenApplicationStatus.BATCH_APP_NM, batchAppName);
                    messageMap.put(ShamenApplicationStatus.STATUS_PARAMETERS, pastDate.toString());
                    messageMap = ShamenApplicationStatus.getInstance().getSc().encryptApplicationMap(messageMap);
                    messageMap.put(ShamenApplicationStatus.ACTION, ShamenApplicationStatus.ACTION_GET_RUN_STATUS_SUMMARIES);
                    messageMap.put(ShamenApplicationStatus.APPLICATION_NAME, ShamenConstants.CLIENT_SELECTOR_VALUE);
                    messageMap.put(ShamenApplicationStatus.APPLICATION_ENVIRONMENT, ShamenConstants.CLIENT_SELECTOR_VALUE_2);
                    messageMap.put(ShamenApplicationStatus.APPLICATION_INSTANCE, ShamenConstants.CLIENT_SELECTOR_VALUE_3);
                    log.info("Attempt to get Run Status Summary List from ShamenWeb.");
                    receivedMessageMap = ApplicationJmsManager.getInstance().sendPTPWithAcknowledge(ShamenConstants.SHAMEN_REQUEST_Q, messageMap, 5000);
                    if(receivedMessageMap != null){
                        log.debug("Inside if condition with expression: receivedMessageMap != null");
                        log.info("Received a response from ShamenWeb.");
                        receivedMessageMap = ShamenApplicationStatus.getInstance().getSc().decryptApplicationObjectMap(receivedMessageMap);
                        summaryList = (ArrayList<JmsRunStatusSummary>) receivedMessageMap.get(ShamenApplicationStatus.RUN_STATUS_SUMMARIES);
                    }// end-if
                }// end if
            }catch(Exception e){
                throw new Exception("Error while trying to retrieve Batch App Run Status Summary list. Message is:" + e.getMessage(), e);
            }// end try/catch
        }// end if
        log.trace("Exiting getRunStatusSummaries");
        return summaryList;
    }// end getRunStatusSummaries

    /**
     * This method retrieves a list of Run Status information for a specific batch run based on the Run Number provided.
     * 
     * @param runNumber
     *        the run number
     * @return <code> List</code> of {@link JmsRunStatus}
     */
    @SuppressWarnings("unchecked")
    public List<JmsRunStatus> getRunStatusInfo(String batchAppRefId, String runNumber) throws Exception {
        log.trace("Entering getRunStatusInfo");
        List<JmsRunStatus> summaryList = new ArrayList<JmsRunStatus>();
        if(ShamenConstants.USE_SHAMEN){
            try{
                if(ApplicationJmsManager.getInstance().isConnected()){
                    HashMap<String, String> messageMap = new HashMap<String, String>();
                    HashMap<String, Object> receivedMessageMap = new HashMap<String, Object>();
                    messageMap.put(ShamenApplicationStatus.STATUS, ShamenApplicationStatus.getInstance().getStatus());
                    messageMap.put(ShamenApplicationStatus.REPLY_REQUIRED, "true");
                    messageMap.put(ShamenApplicationStatus.BATCH_APP_REF_ID, batchAppRefId);
                    messageMap.put(ShamenApplicationStatus.RUN_NUMBER, runNumber);
                    messageMap = ShamenApplicationStatus.getInstance().getSc().encryptApplicationMap(messageMap);
                    messageMap.put(ShamenApplicationStatus.ACTION, ShamenApplicationStatus.ACTION_GET_RUN_STATUS_INFO);
                    messageMap.put(ShamenApplicationStatus.APPLICATION_NAME, ShamenConstants.CLIENT_SELECTOR_VALUE);
                    messageMap.put(ShamenApplicationStatus.APPLICATION_ENVIRONMENT, ShamenConstants.CLIENT_SELECTOR_VALUE_2);
                    messageMap.put(ShamenApplicationStatus.APPLICATION_INSTANCE, ShamenConstants.CLIENT_SELECTOR_VALUE_3);
                    log.info("Attempt to get Run Status Info from ShamenWeb.");
                    receivedMessageMap = ApplicationJmsManager.getInstance().sendPTPWithAcknowledge(ShamenConstants.SHAMEN_REQUEST_Q, messageMap, 5000);
                    if(receivedMessageMap != null){
                        log.debug("Inside if condition with expression: receivedMessageMap != null");
                        log.info("Received a response from ShamenWeb.");
                        receivedMessageMap = ShamenApplicationStatus.getInstance().getSc().decryptApplicationObjectMap(receivedMessageMap);
                        summaryList = (ArrayList<JmsRunStatus>) receivedMessageMap.get(ShamenApplicationStatus.RUN_STATUS_LIST);
                    }// end-if
                }// end if
            }catch(Exception e){
                throw new Exception("Error while trying to retrieve Batch App Run Status Info. Message is:" + e.getMessage(), e);
            }// end try/catch
        }// end if
        log.trace("Exiting getRunStatusInfo");
        return summaryList;
    }// end getRunStatusInfo
}// end class
