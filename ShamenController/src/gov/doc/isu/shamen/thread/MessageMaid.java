/**
 * @author <strong>Shane Duncan</strong> JCCC, Aug 21, 2015
 */
package gov.doc.isu.shamen.thread;

import gov.doc.isu.shamen.core.Constants;
import gov.doc.isu.shamen.jms.ControllerMessage;
import gov.doc.isu.shamen.jms.JmsManager;
import gov.doc.isu.shamen.jms.models.JmsController;
import gov.doc.isu.shamen.models.Message;
import gov.doc.isu.shamen.models.Properties;
import gov.doc.isu.shamen.processors.ShamenThreadSafeProcessor;

import java.util.List;
import java.util.logging.Level;

/**
 * This class is used to maintain message integrity with Shamen. It will look in the database for messages that are more than an hour old. If Shamen and JMS are up, it will resend the messages.
 * 
 * @author <strong>Shane Duncan</strong> JCCC, Aug 21, 2015
 */
public class MessageMaid extends ControllerThread {
    private static final String MY_CLASS_NAME = "gov.doc.isu.shamen.thread.MessageMaid";
    private JmsController controller;
    private ShamenThreadSafeProcessor shamenProcessor;
    private Properties currentProps;
    private JmsManager jms;

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Aug 31, 2015
     * @throws Exception
     */
    public MessageMaid(String threadName) {
        super(threadName);
        try{
            jms = new JmsManager(myLogger.getChild());
        }catch(Exception e){
            myLogger.severe("Constructor", "Exception caught while trying to do start jms manager.  Message is: " + e.getMessage());
        }
    }// end constructor

    /*
     * (non-Javadoc) This is an overridden method to kick off the MessageMaid to maintain message integrity with Shamen.
     * @see gov.doc.isu.shamen.thread.ControllerThread#run()
     */
    @Override
    public void run() {
        String methodName = "run";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest(methodName, "This is an overridden method to kick off the MessageMaid to maintain message integrity with Shamen.");
        myLogger.finer(methodName, "Initiate the wait loop.");
        Boolean keepGoing = true;
        while(keepGoing){
            try{
                // sleep for 30 seconds
                Thread.sleep(30000);
            }catch(InterruptedException e){
                myLogger.severe(methodName, "InterruptedException caught while trying to sleep in the MessageMaid.  Message is: " + e.getMessage());
            }// end try-catch
            currentProps = getProperties();
            // This is set to finest to insure that the logs don't fill up with crud.
            myLogger.finest(methodName, "Get stayAlive property.  If value false or not in the properties, kill thread.");
            if(!currentProps.isControllerStayAlive()){
                keepGoing = false;
                myLogger.fine(methodName, "Termination request detected. Killing thread.");
                break;
            }
            // This log message was intentionally put to finest to keep the log from filling up.
            myLogger.finest(methodName, "Get jmsStayAlive property.  If value false or not in the properties, kill thread.");
            if(!currentProps.isJmsStayAlive()){
                myLogger.warning(methodName, "JMS connection failure detected, killing thread until JMS comes back up.");
                keepGoing = false;
                break;
            }// end if
             // See if any messages need to be resent to Shamen.
            if(controller != null){
                try{
                    dustForMessages();
                }catch(Exception e){
                    myLogger.severe(methodName, "Exception caught while trying to do message maintenance.  Message is: " + e.getMessage());
                }
            }else{
                controller = getProcessor().getController();
            }// end if-else
        }// end while
        myLogger.exiting(MY_CLASS_NAME, methodName);
        killMyself();
    }// end run

    /**
     * Get all unconfirmed messages from the internal database. If messages exist, then check the status of both JMS and Shamen. If they are both up, resend the messages if they are over an hour old.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Oct 26, 2015
     * @throws Exception
     */
    private void dustForMessages() throws Exception {
        String methodName = "dustForMessages";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest(methodName, "Get all unconfirmed messages from the internal database. If messages exist, then check the status of both JMS and Shamen. If they are both up, resend the messages if they are over 30 minutes old.");
        List<Message> messageList = getProcessor().getAllMessages();
        ControllerMessage msg;
        // only process if there are archived messages.
        if(messageList != null ? !messageList.isEmpty() : false){
            // check for viability of JMS
            if(currentProps.isJmsStayAlive()){
                // check for viability of Shamen
                if(isShamenAlive()){
                    JmsManager jms = new JmsManager(myLogger.getChild());
                    myLogger.finer(methodName, "Loop through all the archived messages and re-send them if more than 30 minutes old.");
                    for(int i = 0, j = messageList.size();i < j;i++){
                        Message m = messageList.get(i);
                        if(System.currentTimeMillis() - m.getCreateTs().getTime() > 1800000){
                            msg = getProcessor().getMessageById(m.getId());
                            if(msg != null){
                                myLogger.info(methodName, "Attempting to send " + msg.getText() + " message.");
                                myLogger.finest(methodName, "Message is: " + msg);
                                try{
                                    jms.sendPTP(msg, Constants.SHAMEN_REQUEST_Q);
                                }catch(Exception e){
                                    myLogger.getChild().log(Level.SEVERE, "An Exception has been caught while trying to send the message to shamen to update the runStatus record. Message is: " + e.getMessage(), e);
                                }// end try-catch
                            }// end if
                        }// end if
                    }// end for
                    if(jms != null){
                        jms.cleanUp();
                    }
                }// end if
            }// end if
        }// end if
        messageList = null;
        msg = null;
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end dustForMessages

    /**
     * This method sends a PTP message to Shamen with the request/reply pattern forcing Shamen to reply with its status.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Oct 6, 2015
     * @return boolean
     */
    private Boolean isShamenAlive() {
        String methodName = "isShamenAlive";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest(methodName, "This method sends a PTP message to Shamen with the request/reply pattern forcing Shamen to reply with its status.");
        Boolean shameAlive = false;
        try{
            JmsManager jms = new JmsManager(myLogger.getChild());
            ControllerMessage msg = new ControllerMessage();
            msg.setText(ControllerMessage.ARE_YOU_ALIVE);
            msg.setCorrelationID(controller.getControllerAddress() + System.currentTimeMillis());
            msg.setController(controller);
            ControllerMessage receivedMsg = jms.sendNonPersistentPTPWithAcknowledge(Constants.SHAMEN_REQUEST_Q, msg, 120000);
            if(receivedMsg != null){
                shameAlive = true;
            }// end if
            msg = null;
            jms.cleanUp();
        }catch(Exception e){
            myLogger.severe(methodName, "Exception occurred while trying to get check the status of Shamen.  Message is: " + e.getMessage());
        }// end try-catch
        myLogger.exiting(MY_CLASS_NAME, methodName, shameAlive);
        return shameAlive;
    }// end isShamenAlive

    /**
     * This method gets the properties from the DB.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Oct 6, 2015
     * @return
     */
    private Properties getProperties() {
        String methodName = "getProperties";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest(methodName, "This method gets the properties from the DB.");
        Properties p = getProcessor().getProperties();
        myLogger.exiting(MY_CLASS_NAME, methodName, p);
        return p;
    }// end getProperties

    /**
     * This method returns an instance of the ShamenThreadSafeProcessor
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 29, 2015
     * @return
     */
    private ShamenThreadSafeProcessor getProcessor() {
        String methodName = "getProcessor";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest(methodName, "This method returns an instance of the ShamenThreadSafeProcessor.");
        if(shamenProcessor == null){
            shamenProcessor = new ShamenThreadSafeProcessor(myLogger.getChild());
        }// end if
        myLogger.exiting(MY_CLASS_NAME, methodName, shamenProcessor);
        return shamenProcessor;
    }// end if
}// end class
