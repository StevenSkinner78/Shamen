/**
 * @author <strong>Shane Duncan</strong> JCCC, Aug 21, 2015
 */
package gov.doc.isu.shamen.thread;

import gov.doc.isu.gtv.managers.PropertiesMgr;
import gov.doc.isu.shamen.core.Constants;
import gov.doc.isu.shamen.jms.ControllerMessage;
import gov.doc.isu.shamen.jms.JmsManager;
import gov.doc.isu.shamen.models.Properties;
import gov.doc.isu.shamen.processors.ShamenThreadSafeProcessor;

import javax.jms.Message;
import javax.jms.ObjectMessage;

/**
 * This class was does not handle any messages. This is used to link to the JMS server's main controller queue. When it cannot link to the queue, it means that the JMS server is down. That is its primary function.
 * 
 * @author <strong>Shane Duncan</strong> JCCC, Aug 21, 2015
 */
public class PtpListener extends ControllerThread {
    private static final String MY_CLASS_NAME = "gov.doc.isu.shamen.thread.PtpListener";
    private ShamenThreadSafeProcessor shamenProcessor;

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Aug 31, 2015
     */
    public PtpListener(String threadName) {
        super(threadName);
    }// end PtpListener

    /*
     * (non-Javadoc) This is an overridden method to kick off the PTPListener to listen for point to point messages.
     * @see gov.doc.isu.shamen.thread.ControllerThread#run()
     */
    @Override
    public void run() {
        String methodName = "run";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest(methodName, "This is an overridden method to kick off the PTPListener to listen for point to point messages.");
        Message msg = null;
        Properties properties;
        try{
            myLogger.finer(methodName, "Create a JMS manager.");
            JmsManager jms = new JmsManager(myLogger.getChild());
            Boolean keepGoing = true;
            while(keepGoing){
                // This log message was intentionally put to finest to keep the log from filling up.
                myLogger.finest(methodName, "Listen for next message.");
                ControllerMessage controllerMessage = null;
                msg = jms.receivePTP(PropertiesMgr.getProperties().getProperty(Constants.REQUEST_Q));
                if(msg instanceof ObjectMessage){
                    Object obj;
                    obj = ((ObjectMessage) msg).getObject();
                    if(obj instanceof ControllerMessage){
                        controllerMessage = (ControllerMessage) obj;
                    }// end if
                }// end if
                if(controllerMessage != null){
                    if(ControllerMessage.ARE_YOU_ALIVE.equals(controllerMessage.getText())){
                        myLogger.finer(methodName, "Is alive confirmation was requested.  Acknowledging alive status.");
                        // jms.acknowlegePTPMessage(msg, ControllerMessage.RECEIVED);
                    }// end if
                }// end if
                 // This log message was intentionally put to finest to keep the log from filling up.
                myLogger.finest(methodName, "Get stayAlive property.  If value false or not in the properties, kill thread.");
                properties = getProperties();
                // This is set to finest to insure that the logs don't fill up with crud.
                myLogger.finest(methodName, "Get stayAlive property.  If value false or not in the properties, kill thread.");
                if(!properties.isControllerStayAlive()){
                    keepGoing = false;
                    myLogger.fine(methodName, "Termination request detected. Killing thread.");
                }
                // This log message was intentionally put to finest to keep the log from filling up.
                myLogger.finest(methodName, "Get jmsStayAlive property.  If value false or not in the properties, kill thread.");
                if(!properties.isJmsStayAlive()){
                    myLogger.warning(methodName, "JMS connection failure detected, killing thread until JMS comes back up.");
                    keepGoing = false;
                }// end if
            }// end while
            jms.cleanUp();
        }catch(Exception e){
            myLogger.getChild().severe("Exception caught while receiving PTP Message.  Message is: " + e.getMessage());
        }// end try-catch
        killMyself();
    }// end run

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
        // getProcessor().cleanUp();
        myLogger.exiting(MY_CLASS_NAME, methodName, p);
        return p;
    }// end getProperties

    /**
     * This method sets the controllerStayAlive property for use by other threads.
     * 
     * @param value
     * @author <strong>Shane Duncan</strong> JCCC, Oct 6, 2015
     */
    private void setControllerStayAlive(String value) {
        String methodName = "setControllerStayAlive";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest(methodName, "This method sets the controllerStayAlive property for use by other threads.");
        Properties p = new Properties();
        p.setControllerStayAlive(value);
        getProcessor().updateProperties(p);
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end setControllerStayAlive

    /**
     * This method controls the instance variable for the processor.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Oct 6, 2015
     */
    private ShamenThreadSafeProcessor getProcessor() {
        if(shamenProcessor == null){
            shamenProcessor = new ShamenThreadSafeProcessor(myLogger.getChild());
        }
        return shamenProcessor;
    }
}// end class
