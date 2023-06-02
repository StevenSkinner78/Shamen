/**
 * @(#)ShamenAppSubscriptionSpawner.java 1.0.0 Copyright (c) 2018 Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit All Rights Reserved. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: -Redistribution of source code must retain the above copyright notice, this list of conditions and the following disclaimer. -Redistribution in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. Neither the name of Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit or the names of contributors may be used to endorse or promote products derived from this software without specific prior written permission. This software is provided "AS IS," without a warranty of any kind. ALL EXPRESS OR
 *                                       IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH
 *                                       DAMAGES. You acknowledge that this software is not designed, licensed or intended for use in the design, construction, operation or maintenance of any nuclear facility.
 */
package gov.doc.isu.shamen.core;

import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.properties.EncryptableProperties;

import gov.doc.isu.shamen.jms.AppTopicListener;
import gov.doc.isu.shamen.jms.ApplicationJmsManager;
import gov.doc.isu.shamen.jms.ShamenUncaughtExceptionHandler;

/**
 * This class is a singleton that keeps the subscription threads running.
 * 
 * @author <strong>Shane Duncan</strong> JCCC, Oct 7, 2015
 * @author <strong>Steven Skinner</strong> JCCC, Aug 17, 2020
 */
public class ShamenAppSubscriptionSpawner implements Runnable {

    private static ShamenAppSubscriptionSpawner instance;
    private static ExecutorService executor;
    private static final String MY_CLASS_NAME = "gov.doc.isu.shamen.core.ShamenAppSubscriptionSpawner";
    private static Log log = LogFactory.getLog(MY_CLASS_NAME);
    private static volatile AtomicBoolean keepGoing = new AtomicBoolean();
    private static Properties properties;
    private static StandardPBEStringEncryptor encryptor;

    /**
     * This method makes sure that this class is a singleton
     * 
     * @return instance
     * @author <strong>Shane Duncan</strong> JCCC, Oct 7, 2015
     */
    public static ShamenAppSubscriptionSpawner getInstance() throws Exception {
        if(instance == null){
            // Use this method to decrypt the passwords for use in the application.
            encryptor = new StandardPBEStringEncryptor();
            // This is the encryption / decryption password key
            encryptor.setPassword(getEncryptionKey());
            properties = new EncryptableProperties(encryptor);
            properties.put(InitialContext.INITIAL_CONTEXT_FACTORY, ShamenConstants.INITIAL_CONTEXT_FACTORY);
            properties.put(InitialContext.PROVIDER_URL, ShamenConstants.PROVIDER_URL);
            properties.put(ShamenConstants.CONNECTION_FACTORY, ShamenConstants.CONNECTION_FACTORY_VALUE);

            properties.put(ShamenConstants.MQ_USER, ShamenConstants.MQ_USER_VALUE);
            properties.put(ShamenConstants.MQ_PASSWORD, ShamenConstants.MQ_PASSWORD_VALUE);

            properties.getProperty(ShamenConstants.MQ_USER);
            properties.getProperty(ShamenConstants.MQ_PASSWORD);
            instance = new ShamenAppSubscriptionSpawner();
        }// end if
        return instance;
    }// end getInstance

    /**
     * This method will spawn the subscription
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Oct 7, 2015
     */
    private void spawn() throws Exception {
        log.debug("Entering ShamenAppSubscriptionSpawner.spawn");
        executor = Executors.newFixedThreadPool(1);
        // register this instance
        if(register()){
            Thread t = null;
            AppTopicListener appTopicListener = new AppTopicListener();
            t = new Thread(appTopicListener, ShamenConstants.TOPIC_NAME);
            t.setUncaughtExceptionHandler(new ShamenUncaughtExceptionHandler(log));
            executor.execute(t);
            executor.shutdown();
        }else{
            executor = null;
        }// end if-else
        log.debug("Exiting ShamenAppSubscriptionSpawner.spawn");
    }// end spawn

    /**
     * This method registers this instance with the Shamen application.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Dec 6, 2017
     */
    @SuppressWarnings("unchecked")
    private Boolean register() {
        log.debug("Entering ShamenAppSubscriptionSpawner.register");
        log.trace("This method registers this instance with the Shamen application. ");

        log.info("Register instance with Shamen. This instance is: " + ShamenConstants.CLIENT_SELECTOR_VALUE_3);
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
        log.debug("Encrypt the registration with default encryption.");
        ShamenApplicationCipher sc;
        try{
            sc = new ShamenApplicationCipher();
            messageMap = sc.encryptApplicationMap(messageMap);
            messageMap.put(ShamenApplicationStatus.ACTION, ShamenApplicationStatus.ACTION_REGISTER_FROM_CLIENT);
            log.info("Send message to ShamenWeb to register.");
            returnMap = ApplicationJmsManager.getInstance().sendPTPWithAcknowledge(ShamenConstants.SHAMEN_REQUEST_Q, messageMap, 15000);
            if(returnMap != null){
                registered = true;
                log.info("Successfully registered instance: " + String.valueOf(ShamenConstants.CLIENT_SELECTOR_VALUE_3) + " with ShamenWeb.");
            }else{
                registered = false;
                log.info("Did NOT successfully register instance: " + String.valueOf(ShamenConstants.CLIENT_SELECTOR_VALUE_3) + " with ShamenWeb.");
            }// end if-else
        }catch(Exception e){
            log.error("Error was encountered trying to register instance with Shamen. Error is: " + e.getMessage(), e);
        }// end try-catch
        log.debug("Exiting ShamenAppSubscriptionSpawner.register with: " + registered);
        return registered;
    }// end register

    /**
     * This method is called to check and see if the JMS environment is set up and usable.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 3, 2015
     */
    @SuppressWarnings("unused")
    private Boolean initializeJms() {
        log.debug("Entering ShamenAppSubscriptionSpawner.initializeJms");
        Boolean cool = true;
        log.trace("This method is called to set up and initialize the necessary Connection and Session references. ");
        // set up the message selector so this controller only gets the messages it should

        InitialContext ctx = null;
        Session ses = null;
        Connection con = null;
        try{
            ctx = new InitialContext(properties);

            log.debug("Set up all the JMS connection properties.");
            log.debug("Get a connection to the ConnectionFactory");
            ConnectionFactory conFactory = (ConnectionFactory) ctx.lookup(ShamenConstants.CONNECTION_FACTORY_VALUE);
            log.debug("Create a connection.");
            con = conFactory.createConnection(properties.getProperty(ShamenConstants.MQ_USER), properties.getProperty(ShamenConstants.MQ_PASSWORD));
            log.debug("Create a session that is non-transacted and is notified automatically.");
            ses = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
            log.debug("Look up the destination to insure it is there.");
            Destination destination = (Destination) ctx.lookup(ShamenConstants.TOPIC_NAME);
        }catch(NamingException ex){
            log.error("A naming exception has been caught while trying to initialize JMS in the subscriptionSpawner. This may be due to the JMS server being down. Properties = " + (properties != null ? properties.toString() : "null") + " Message is: " + ex.getMessage());
            cool = false;
        }catch(Exception ex){
            log.error("An exception has been caught while trying to initialize JMS in the subscriptionSpawner.  This may be due to the JMS server being down. Properties = " + (properties != null ? properties.toString() : "null") + " Message is: " + ex.getMessage());
            cool = false;
        }finally{
            try{
                if(ses != null){
                    ses.close();
                }// end if
                if(con != null){
                    con.close();
                }// end if
                log.debug("Close up the JNDI connection since we have found what we needed.");
                ctx.close();
            }catch(Exception ex){
                log.warn("Unable to close JNDI connection.");
            }// end try-catch
        }// end finally
        log.debug("Exiting ShamenAppSubscriptionSpawner.initializeJms");
        return cool;
    }// end initializeJms

    @Override
    public void run() {
        log.debug("Entering ShamenAppSubscriptionSpawner.run");
        keepGoing.set(true);
        while(keepGoing.get()){
            // if the executor is not currently running, try to reset it.
            if((executor == null) || (executor != null ? executor.isTerminated() : true)){
                // if((executor == null)){
                try{
                    // Only spawn the listener if the jms environment is good
                    if(initializeJms()){
                        spawn();
                    }// end if
                }catch(Exception e){
                    log.error("Error occurred spawning the JMS listener.  Message is: " + e.getMessage());
                }// end try-catch
            }// end if
            try{
                TimeUnit.SECONDS.sleep(60);
            }catch(InterruptedException e){
                log.error("Error occurred sleeping during the jms spawning test.  Message is: " + e.getMessage());
            }// end try-catch
        }// end while
        log.debug("Exiting ShamenAppSubscriptionSpawner.run");
    }// end run

    /**
     * This method is called from the ShamenContextListener and will terminate the JMS threads so that messaging resources are killed.
     */
    public void killYourself() {
        log.debug("Entering ShamenAppSubscriptionSpawner.killYourself");
        log.debug("Signal the topic listener to terminate.");
        AppTopicListener.setKeepGoing(false);
        log.debug("Signal this thread to terminate.");
        ShamenAppSubscriptionSpawner.keepGoing.set(false);
        executor.shutdownNow();
        executor.shutdown(); // Disable new tasks from being submitted
        sendDeathNotification();
        try{
            log.debug("Wait a while for existing tasks to terminate.");
            if(!executor.awaitTermination(60, TimeUnit.SECONDS)){
                executor.shutdownNow(); // Cancel currently executing tasks
                if(!executor.awaitTermination(60, TimeUnit.SECONDS)){
                    System.err.println("executor did not terminate");
                }// end if
            }// end if
        }catch(InterruptedException e){
            log.debug("(Re-)Cancel if current thread also interrupted.");
            executor.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
            log.error("An InterruptedException has been caught while trying to shut down the thread executor normally.  ShutdownNow had to be used. Message is: " + e.getMessage(), e);
        }// end try-catch
        log.debug("Exiting ShamenAppSubscriptionSpawner.killYourself");
    }// end killYourself

    /**
     * This method sends the notification that this instance has died of natural causes.
     */
    private void sendDeathNotification() {
        log.debug("Entering ShamenAppSubscriptionSpawner.sendDeathNotification");
        log.info("Send Death notification.");
        HashMap<String, String> messageMap = new HashMap<String, String>();
        messageMap.put(ShamenApplicationStatus.REPLY_REQUIRED, "false");

        messageMap.put(ShamenApplicationStatus.APPLICATION_ADDNTL_INFO, ShamenConstants.CLIENT_ADDNTL_INFO);
        messageMap.put(ShamenApplicationStatus.APPLICATION_BRANCH, ShamenConstants.CLIENT_BRANCH);
        messageMap.put(ShamenApplicationStatus.APPLICATION_EAR, ShamenConstants.CLIENT_EAR);
        messageMap.put(ShamenApplicationStatus.APPLICATION_VERSION, ShamenConstants.CLIENT_VERSION);
        log.debug("Encrypt the death message with default encryption.");
        ShamenApplicationCipher sc = ShamenApplicationStatus.getInstance().getSc();
        messageMap = sc.encryptApplicationMap(messageMap);
        messageMap.put(ShamenApplicationStatus.APPLICATION_NAME, ShamenConstants.CLIENT_SELECTOR_VALUE);
        messageMap.put(ShamenApplicationStatus.APPLICATION_ENVIRONMENT, ShamenConstants.CLIENT_SELECTOR_VALUE_2);
        messageMap.put(ShamenApplicationStatus.APPLICATION_INSTANCE, ShamenConstants.CLIENT_SELECTOR_VALUE_3);
        messageMap.put(ShamenApplicationStatus.ACTION, ShamenApplicationStatus.ACTION_DEATH_NOTIFICATION);
        log.info("Send death notification to ShamenWeb.");
        try{
            ApplicationJmsManager.getInstance().sendToDestination(ShamenConstants.SHAMEN_REQUEST_Q, messageMap, "1");
        }catch(Exception e){
            log.error("Unable to send death notification message.  Exception is: " + e);
        }// end try-catch
        log.debug("Exiting ShamenAppSubscriptionSpawner.sendDeathNotification");
    }// end sendDeathNotification

    public static String getEncryptionKey() {
        return "docsecretpassword";
    }
}// end class
