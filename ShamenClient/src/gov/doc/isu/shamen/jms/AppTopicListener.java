/**
 * @(#)AppTopicListener.java 1.0.0 Copyright (c) 2018 Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit All Rights Reserved. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: -Redistribution of source code must retain the above copyright notice, this list of conditions and the following disclaimer. -Redistribution in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. Neither the name of Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit or the names of contributors may be used to endorse or promote products derived from this software without specific prior written permission. This software is provided "AS IS," without a warranty of any kind. ALL EXPRESS OR IMPLIED
 *                           CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. You
 *                           acknowledge that this software is not designed, licensed or intended for use in the design, construction, operation or maintenance of any nuclear facility.
 */
package gov.doc.isu.shamen.jms;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.doc.isu.shamen.core.ShamenApplicationCipher;
import gov.doc.isu.shamen.core.ShamenApplicationStatus;
import gov.doc.isu.shamen.core.ShamenConstants;

/**
 * @author <strong>Shane Duncan</strong> JCCC, Aug 21, 2015
 * @author <strong>Steven Skinner</strong> JCCC, Oct 01, 2019
 */
public class AppTopicListener implements Runnable {
    private static final String MY_CLASS_NAME = "gov.doc.isu.shamen.jms.AppTopicListener";
    private static Log myLogger = LogFactory.getLog(MY_CLASS_NAME);
    private static volatile AtomicBoolean keepGoing = new AtomicBoolean();

    /*
     * (non-Javadoc) This is an overridden method to kick off the AppTopicListener to listen for Topic Consumption.
     */
    @Override
    public void run() {

        myLogger.debug("Entering AppTopicListener.run");
        myLogger.trace("This is an overridden method to kick off the AppTopicListener to listen for topic messages.");
        Message msg = null;
        HashMap<String, String> messageMap = null;
        try{
            keepGoing.set(true);

            while(keepGoing.get()){
                myLogger.trace("Create a JMS manager.");
                ApplicationJmsManager jms = ApplicationJmsManager.getInstance();
                // This log message was intentionally put to trace to keep the log from filling up.
                myLogger.trace("Listen for next message.");
                msg = jms.consumeMessageFromDestination(ShamenConstants.TOPIC_NAME, buildDiscriminator());
                messageMap = null;
                if(msg != null){
                    if(msg instanceof ObjectMessage){
                        Object obj;
                        obj = ((ObjectMessage) msg).getObject();
                        if(obj instanceof HashMap){
                            messageMap = (HashMap<String, String>) obj;
                            // if the action is visible, it's not an encrypted message and should be handled differently.
                            if(messageMap.containsKey(ShamenApplicationStatus.ACTION)){
                                if(ShamenApplicationStatus.ACTION_REGISTER.equals(messageMap.get(ShamenApplicationStatus.ACTION))){
                                    myLogger.info("Process the message as a registration request.");
                                    register();
                                }else if(ShamenApplicationStatus.ACTION_ESTABLISH_HANDSHAKE.equals(messageMap.get(ShamenApplicationStatus.ACTION))){
                                    myLogger.info("Process the message as a handshake.");
                                    processHandshake(messageMap, msg);
                                }else{
                                    myLogger.error("!!!!!DANGER!!!!!  Someone tried to send an unrecognized message to this application.  This is a security breach.");
                                }
                            }else{
                                if(ShamenApplicationStatus.getInstance().isHandshakeEstablished()){
                                    messageMap = ShamenApplicationStatus.getInstance().getSc().decryptApplicationMap(messageMap);
                                    Set<String> keys = messageMap.keySet();
                                    for(Iterator<String> iterator = keys.iterator();iterator.hasNext();){
                                        String key = (String) iterator.next();
                                        myLogger.debug("Message key " + key + " contains value: " + messageMap.get(key));
                                    }// end for
                                    processMessage(messageMap, msg);
                                }else{
                                    myLogger.warn("A message was received but handshake was not established.  Message will be ignored.");
                                }// end else
                            }// end else
                        }// end if
                    }// end if
                }// end if

            }// end while
        }catch(Exception e){
            myLogger.error("Exception caught while receiving Topic Message with acknowledge.  Exception is: " + e + " Message received is: " + String.valueOf(msg));
            keepGoing.set(false);
        }// end try-catch
        myLogger.debug("Exiting AppTopicListener.run");
    }// end run

    /**
     * This method builds the discriminator string for the message consumer. It will cause it to get messages for:
     * <ul>
     * <li>ALL applications</li>
     * <li>This application/environment/node</li>
     * <li>This application/environment and '' node</li>
     * </ul>
     * 
     * @return discriminatorString
     */
    private String buildDiscriminator() {
        StringBuffer sb = new StringBuffer();
        sb.append("(");
        sb.append(ShamenConstants.CLIENT_SELECTOR);
        sb.append("'");
        sb.append(ShamenConstants.CLIENT_SELECTOR_VALUE);
        sb.append("' AND (");
        sb.append(ShamenConstants.CLIENT_SELECTOR_2);
        sb.append("'");
        sb.append(ShamenConstants.CLIENT_SELECTOR_VALUE_2);
        sb.append("' OR ");
        sb.append(ShamenConstants.CLIENT_SELECTOR_2);
        sb.append("'ALL')");
        sb.append(" AND (");
        sb.append(ShamenConstants.CLIENT_SELECTOR_3);
        sb.append("'ALL'");
        sb.append(" OR ");
        sb.append(ShamenConstants.CLIENT_SELECTOR_3);
        sb.append("'");
        sb.append(ShamenConstants.CLIENT_SELECTOR_VALUE_3);
        sb.append("')");
        sb.append(" OR ");
        sb.append(ShamenConstants.CLIENT_SELECTOR);
        sb.append("'ALL')");
        return sb.toString();
    }// end buildDiscriminator

    /**
     * This method registers this instance with the Shamen application.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Dec 6, 2017
     */
    @SuppressWarnings("unchecked")
    private Boolean register() {
        myLogger.debug("Entering ShamenAppSubscriptionSpawner.register");
        myLogger.trace("This method registers this instance with the Shamen application. ");

        myLogger.info("Register instance with Shamen. This instance is: " + ShamenConstants.CLIENT_SELECTOR_VALUE_3);
        Boolean registered = false;
        HashMap<String, String> messageMap = new HashMap<String, String>();
        HashMap<String, String> returnMap = new HashMap<String, String>();
        messageMap.put(ShamenApplicationStatus.REPLY_REQUIRED, "false");
        messageMap.put(ShamenApplicationStatus.APPLICATION_NAME, ShamenConstants.CLIENT_SELECTOR_VALUE);
        messageMap.put(ShamenApplicationStatus.APPLICATION_ENVIRONMENT, ShamenConstants.CLIENT_SELECTOR_VALUE_2);
        messageMap.put(ShamenApplicationStatus.APPLICATION_ADDNTL_INFO, ShamenConstants.CLIENT_ADDNTL_INFO);
        messageMap.put(ShamenApplicationStatus.APPLICATION_BRANCH, ShamenConstants.CLIENT_BRANCH);
        messageMap.put(ShamenApplicationStatus.APPLICATION_INSTANCE, ShamenConstants.CLIENT_SELECTOR_VALUE_3);
        messageMap.put(ShamenApplicationStatus.APPLICATION_EAR, ShamenConstants.CLIENT_EAR);
        messageMap.put(ShamenApplicationStatus.APPLICATION_VERSION, ShamenConstants.CLIENT_VERSION);
        myLogger.debug("Encrypt the registration with default encryption.");
        ShamenApplicationCipher sc;
        try{
            sc = new ShamenApplicationCipher();
            messageMap = sc.encryptApplicationMap(messageMap);
            messageMap.put(ShamenApplicationStatus.ACTION, ShamenApplicationStatus.ACTION_REGISTER);
            myLogger.info("Send message to ShamenWeb to register.");
            returnMap = ApplicationJmsManager.getInstance().sendPTPWithAcknowledge(ShamenConstants.SHAMEN_REQUEST_Q, messageMap, 15000);
            if(returnMap != null){
                registered = true;
                myLogger.info("Successfully registered instance: " + String.valueOf(ShamenConstants.CLIENT_SELECTOR_VALUE_3) + " with ShamenWeb.");
            }// end else
        }catch(Exception e){
            myLogger.error("Error was encountered trying to register instance with Shamen. Error is: " + e.getMessage(), e);
        }// end try-catch
        myLogger.debug("Exiting ShamenAppSubscriptionSpawner.register with: " + registered);
        return registered;
    }// end register

    /**
     * This method processes the message and could do the following:
     * <ul>
     * <li>load the application status singleton.</li>
     * <li>return a status update to Shamen</li
     * </ul>
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Mar 29, 2016
     * @param messageMap
     *        Message HashMap
     * @param msg
     *        Message that was received
     */
    private void processMessage(HashMap<String, String> messageMap, Message msg) {
        myLogger.debug("Entering AppTopicListener.processMessage");
        myLogger.trace(" This method processes the message and could do the following: load the application status singleton, return a status update to Shamen");

        if(messageMap.containsKey(ShamenApplicationStatus.ACTION)){
            if(ShamenApplicationStatus.ACTION_CHANGE_YOUR_STATUS.equals(messageMap.get(ShamenApplicationStatus.ACTION))){
                myLogger.info("Changing status for: " + getKey());
                myLogger.info("Load new status for the application.  Status is: " + messageMap);
                ShamenApplicationStatus.getInstance().loadMessage(messageMap);
                myLogger.info("Confirm the status for this application back to Shamen.  Status is: " + messageMap);
                HashMap<String, String> responseMessageMap = new HashMap<String, String>();
                responseMessageMap.put(ShamenApplicationStatus.ACTION, ShamenApplicationStatus.ACTION_HERE_IS_MY_STATUS);
                responseMessageMap.put(ShamenApplicationStatus.STATUS, ShamenApplicationStatus.getInstance().getStatus());
                responseMessageMap.put(ShamenApplicationStatus.REPLY_REQUIRED, "false");
                responseMessageMap.put(ShamenApplicationStatus.APPLICATION_NAME, ShamenConstants.CLIENT_SELECTOR_VALUE);
                responseMessageMap.put(ShamenApplicationStatus.APPLICATION_ENVIRONMENT, ShamenConstants.CLIENT_SELECTOR_VALUE_2);
                responseMessageMap.put(ShamenApplicationStatus.APPLICATION_ADDNTL_INFO, ShamenConstants.CLIENT_ADDNTL_INFO);
                responseMessageMap.put(ShamenApplicationStatus.APPLICATION_BRANCH, ShamenConstants.CLIENT_BRANCH);
                responseMessageMap.put(ShamenApplicationStatus.APPLICATION_EAR, ShamenConstants.CLIENT_EAR);
                responseMessageMap.put(ShamenApplicationStatus.APPLICATION_VERSION, ShamenConstants.CLIENT_VERSION);
                responseMessageMap = ShamenApplicationStatus.getInstance().getSc().encryptApplicationMap(responseMessageMap);
                try{
                    ApplicationJmsManager.getInstance().sendToDestination(msg.getJMSReplyTo(), responseMessageMap, msg.getJMSCorrelationID());
                    myLogger.info("Successfully changed and confirmed the status for the application to: " + ShamenApplicationStatus.getInstance().getStatus());
                }catch(Exception e){
                    myLogger.error("Exception occurred confirming status update to Shamen.  Exception is: " + e.getMessage(), e);
                }// end try-catch

            }else if(ShamenApplicationStatus.ACTION_REPORT_STATUS.equals(messageMap.get(ShamenApplicationStatus.ACTION))){
                myLogger.info("Reporting status for: " + getKey());
                myLogger.info("Report on the status for the application.  Status is: " + messageMap);
                HashMap<String, String> responseMessageMap = new HashMap<String, String>();
                responseMessageMap.put(ShamenApplicationStatus.ACTION, ShamenApplicationStatus.ACTION_HERE_IS_MY_STATUS);
                responseMessageMap.put(ShamenApplicationStatus.STATUS, ShamenApplicationStatus.getInstance().getStatus());
                responseMessageMap.put(ShamenApplicationStatus.REPLY_REQUIRED, "false");
                responseMessageMap.put(ShamenApplicationStatus.APPLICATION_NAME, ShamenConstants.CLIENT_SELECTOR_VALUE);
                responseMessageMap.put(ShamenApplicationStatus.APPLICATION_ENVIRONMENT, ShamenConstants.CLIENT_SELECTOR_VALUE_2);
                responseMessageMap.put(ShamenApplicationStatus.APPLICATION_ADDNTL_INFO, ShamenConstants.CLIENT_ADDNTL_INFO);
                responseMessageMap.put(ShamenApplicationStatus.APPLICATION_BRANCH, ShamenConstants.CLIENT_BRANCH);
                responseMessageMap.put(ShamenApplicationStatus.APPLICATION_EAR, ShamenConstants.CLIENT_EAR);
                responseMessageMap.put(ShamenApplicationStatus.APPLICATION_VERSION, ShamenConstants.CLIENT_VERSION);
                responseMessageMap = ShamenApplicationStatus.getInstance().getSc().encryptApplicationMap(responseMessageMap);
                try{
                    ApplicationJmsManager.getInstance().sendToDestination(msg.getJMSReplyTo(), responseMessageMap, msg.getJMSCorrelationID());
                    myLogger.info("Successfully reported the status for the application as: " + ShamenApplicationStatus.getInstance().getStatus());
                }catch(Exception e){
                    myLogger.error("Exception occurred giving Shamen a status update.  Exception is: " + e.getMessage(), e);
                }// end try-catch

            }// end else-if
        }// end if
        myLogger.debug("Exiting AppTopicListener.processMessage");
    }// end processMessage

    /**
     * This method processes a handshake message from the Shamen application. This means that it will set up the encryption password and store it locally in singleton.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Mar 30, 2016
     */
    private void processHandshake(HashMap<String, String> messageMap, Message msg) {
        myLogger.debug("Entering AppTopicListener.processHandshake");
        myLogger.trace("This method processes a handshake message from the Shamen application. This means that it will set up the encryption password and store it locally in singleton.");
        myLogger.info("Process the handshake from Shamen.");

        myLogger.info("Establishing the handshake for: " + getKey());
        // Set the cache to reflect this node setting up handshake
        ShamenApplicationCipher sc;
        try{
            sc = new ShamenApplicationCipher();
            myLogger.debug("Setting up the cipher.");
            // remove the non encrypted entry from the map
            messageMap.remove(ShamenApplicationStatus.ACTION);
            messageMap = sc.decryptApplicationMap(messageMap);
            if(messageMap.containsKey(ShamenApplicationStatus.PASSWORD)){
                myLogger.info("Storing password in the cache for this handshake.");
                ShamenApplicationStatus.getInstance().setSc(new ShamenApplicationCipher(messageMap.get(ShamenApplicationStatus.PASSWORD)));
                ShamenApplicationStatus.getInstance().setStatus(messageMap.get(ShamenApplicationStatus.STATUS));
                myLogger.info("Report on the status for the application.  Status is: " + messageMap);
                HashMap<String, String> responseMessageMap = new HashMap<String, String>();
                responseMessageMap.put(ShamenApplicationStatus.ACTION, ShamenApplicationStatus.ACTION_MESSAGE_CONFIRMED);
                responseMessageMap.put(ShamenApplicationStatus.STATUS, ShamenApplicationStatus.getInstance().getStatus());
                responseMessageMap.put(ShamenApplicationStatus.REPLY_REQUIRED, "false");
                responseMessageMap.put(ShamenApplicationStatus.APPLICATION_NAME, ShamenConstants.CLIENT_SELECTOR_VALUE);
                responseMessageMap.put(ShamenApplicationStatus.APPLICATION_ENVIRONMENT, ShamenConstants.CLIENT_SELECTOR_VALUE_2);
                responseMessageMap.put(ShamenApplicationStatus.APPLICATION_ADDNTL_INFO, ShamenConstants.CLIENT_ADDNTL_INFO);
                responseMessageMap.put(ShamenApplicationStatus.APPLICATION_BRANCH, ShamenConstants.CLIENT_BRANCH);
                responseMessageMap.put(ShamenApplicationStatus.APPLICATION_EAR, ShamenConstants.CLIENT_EAR);
                responseMessageMap.put(ShamenApplicationStatus.APPLICATION_VERSION, ShamenConstants.CLIENT_VERSION);
                responseMessageMap = ShamenApplicationStatus.getInstance().getSc().encryptApplicationMap(responseMessageMap);
                try{
                    myLogger.info("Send a message back to the tempQ to confirm the successful establishment of the handshake.");
                    ApplicationJmsManager.getInstance().sendToDestination(msg.getJMSReplyTo(), responseMessageMap, msg.getJMSCorrelationID());
                }catch(Exception e){
                    myLogger.error("Exception occurred confirming handshake to Shamen.  Exception is: " + e.getMessage(), e);
                }// end try-catch

            }// end if
        }catch(Exception e){
            myLogger.error("Error was encountered trying to establish handshake with Shamen. The application will default to active. Error is: " + e.getMessage(), e);
            ShamenApplicationStatus.getInstance().setSc(null);
        }// end try-catch
        myLogger.debug("Exiting AppTopicListener.processHandshake");
    }// end processHandshake

    /**
     * @return the keepGoing
     */
    public static AtomicBoolean getKeepGoing() {
        return keepGoing;
    }

    /**
     * @param keepGoing
     *        the keepGoing to set
     */
    public static void setKeepGoing(Boolean keepGoing) {
        AppTopicListener.keepGoing.set(keepGoing);
    }// end setKeepGoing

    /**
     * This method builds the key for this application to be used in checking values from the distributed map.
     * 
     * @return
     */
    private String getKey() {
        return ShamenConstants.CLIENT_SELECTOR_VALUE + "-" + ShamenConstants.CLIENT_SELECTOR_VALUE_2 + "-" + ShamenConstants.CLIENT_SELECTOR_VALUE_3;
    }// end getKey

}// end class
