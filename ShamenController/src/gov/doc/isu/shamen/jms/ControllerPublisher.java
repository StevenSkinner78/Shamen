/**
 * @author <strong>Shane Duncan</strong> JCCC, Aug 19, 2015
 */
package gov.doc.isu.shamen.jms;

import java.util.logging.Logger;

import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.NamingException;

import gov.doc.isu.gtv.managers.PropertiesMgr;

/**
 * @author <strong>Shane Duncan</strong> JCCC, Aug 19, 2015
 */
public class ControllerPublisher extends Publisher {
    private static final String MY_CLASS_NAME = "gov.doc.isu.shamen.jms.ControllerPublisher";
    private static Logger myLogger = Logger.getLogger(MY_CLASS_NAME);

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Aug 19, 2015
     */
    public ControllerPublisher() {
        super();
    }

    /**
     * This method publishes a durable message to a topic.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 3, 2015
     * @param topicName
     *        String(required)
     * @param message
     *        ControllerMessage(required)
     */
    public void publishToTopic(String topicName, ControllerMessage message) {
        String methodName = "publishToTopic";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method publishes a durable message to a topic. In parms are: topicName = " + topicName + " and message = " + message);
        // Local reference to a TopicPublisher
        MessageProducer producer = null;
        try{
            myLogger.finer("Get a connection to the QueueConnectionFactory.");
            tcf = (ConnectionFactory) ctx.lookup(getConnectionFactoryName());
            myLogger.finer("Create a connection.");
            connection = tcf.createConnection(PropertiesMgr.getProperties().getProperty("USER"),PropertiesMgr.getProperties().getProperty("PASSWORD"));
            myLogger.finer("Create a session that is non-transacted and is notified automatically.");
            ses = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            myLogger.finer("Lookup a destination.");
            topic = (Topic) ctx.lookup(topicName);
            myLogger.finer("Create the publisher.");
            producer = ses.createProducer(topic);
            myLogger.finer("Load the message.");
            ObjectMessage msg = ses.createObjectMessage(message);
            myLogger.finer("Set the property that will be used by the message selector.");
            msg.setJMSCorrelationID(message.getCorrelationID());
            if(message.getReplyTo()){
                msg.setJMSReplyTo(topic);
            }
            myLogger.finer("Publish the message.");
            producer.send(msg, DeliveryMode.PERSISTENT, Message.DEFAULT_PRIORITY, 14400000);
            myLogger.finer("Close the openresources.");
            connection.close();
        }catch(NamingException ex){
            appLogger.getChild().severe("A naming exception has been caught while trying to set up the publisher. Message is: " + ex.getMessage());
            myLogger.severe("A naming exception has been caught while trying to set up the publisher. Message is: " + ex.getMessage());
        }catch(JMSException ex){
            appLogger.getChild().severe("A JMSException exception has been caught while trying to publish a message. Message is: " + ex.getMessage());
            myLogger.severe("A JMSException exception has been caught while trying to publish a message. Message is: " + ex.getMessage());
            // It's a good idea to always put a finally block to ensure the
            // context is closed
        }finally{
            try{
                // Close up the JNDI connection since we have found what we needed
                ctx.close();
            }catch(Exception ex){
                appLogger.bold("An exception has been caught while trying to JNDI context after setting up the publisher. Message is: " + ex.getMessage());
                myLogger.severe("An exception has been caught while trying to JNDI context after setting up the publisher. Message is: " + ex.getMessage());
            }// end try catch
        }// end finally
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end publishToTopic
}// end class
