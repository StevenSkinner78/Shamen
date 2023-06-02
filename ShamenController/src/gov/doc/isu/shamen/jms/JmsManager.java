/**
 * This class contains all the necessary methods to receive/send/listen for jms.
 * 
 * @author <strong>Shane Duncan</strong> JCCC, Aug 17, 2015
 */
package gov.doc.isu.shamen.jms;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TemporaryQueue;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import gov.doc.isu.gtv.managers.PropertiesMgr;
import gov.doc.isu.gtv.model.CustomProperties;
import gov.doc.isu.shamen.core.Constants;
import gov.doc.isu.shamen.models.Properties;
import gov.doc.isu.shamen.processors.ShamenThreadSafeProcessor;

/**
 * This class contains all the necessary methods to receive/send/listen for jms.
 *
 * @author <strong>Shane Duncan</strong> JCCC, Aug 17, 2015
 * @author <strong>Steven Skinner</strong> JCCC, Jun 07, 2016
 */
public class JmsManager {
    private static CustomProperties customProperties;
    // private JmsManager jmsManager;
    private Context context = null;
    private Connection connection = null;
    private ConnectionFactory factory = null;
    private Queue controllerRequestQueue = null;
    private Queue controllerResponseQueue = null;
    private static final String MY_CLASS_NAME = "gov.doc.isu.shamen.jms.JmsManager";
    private Logger myLogger = null;

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Aug 17, 2015
     * @throws NamingException
     * @throws JMSException
     */
    public JmsManager(Logger inLogger) throws Exception {
        // Initialize the logging.
        if(inLogger == null){
            myLogger = Logger.getLogger(MY_CLASS_NAME);
        }else{
            myLogger = inLogger;
        }// end if-else
        myLogger.finer("Initializing the JmsManager.");
        setCustomProperties(PropertiesMgr.getProperties());
        // Use the custom properties to load the JNDI stuff.
        myLogger.finer("Load the initial context.");
        context = new InitialContext(getCustomProperties());
        myLogger.finer("Context is: " + String.valueOf(context));
        myLogger.finer("Load the Connection Factory.");
        try{
            factory = (ConnectionFactory) context.lookup(Constants.CONNECTION_FACTORY_VALUE);
        }catch(Exception e){
            myLogger.severe(e.getMessage());
            throw (e);
        }// end if
        myLogger.finer("Create the connection.");

        connection = factory.createConnection(PropertiesMgr.getProperties().getProperty(Constants.MQ_USER), PropertiesMgr.getProperties().getProperty(Constants.MQ_PASSWORD));
        // connection.setClientID(InetAddress.getLocalHost().getHostName().toUpperCase() + "_" + System.currentTimeMillis());
        myLogger.finer("Start the connection.");
        connection.start();
        myLogger.finer("JmsManager intialized successfully.");
        myLogger.exiting(MY_CLASS_NAME, "JmsManager");
    }// end constructor

    /**
     * This method sends a Point to Point message(synchronous) and does not wait on response
     * 
     * @param q
     *        String(required)
     * @param msg
     *        ControllerMessage(required)
     * @author <strong>Shane Duncan</strong> JCCC, Aug 17, 2015
     */
    public void sendPTP(String q, ControllerMessage msg) {
        String methodName = "sendPTP";
        myLogger.entering(MY_CLASS_NAME, methodName, new Object[]{q, msg});
        myLogger.finest("This method sends a Point to Point message(synchronous) and does not wait on response.");
        try{
            myLogger.finer("Setting up JMS session.");
            Destination destination = (Destination) context.lookup(q);
            Session session = getSession();
            myLogger.finer("Creating JMS producer.");
            MessageProducer producer = session.createProducer(destination);
            myLogger.finer("Creating Object Message");
            ObjectMessage message = session.createObjectMessage(msg);
            message.setJMSCorrelationID(msg.getCorrelationID());
            myLogger.finer("Sending message: " + msg);
            producer.send(message);
        }catch(JMSException e){
            myLogger.log(Level.SEVERE, "A JMSException exception was caught while trying to set up or send PTP message. Message is: " + e.getMessage(), e);
            myLogger.warning("Telling all the JMS dependent Threads to terminate.");
            setJmsAlive("false");
        }catch(NamingException e){
            myLogger.log(Level.SEVERE, "A NamingException was caught while trying to set up or send PTP message. Message is: " + e.getMessage(), e);
            myLogger.warning("Telling all the JMS dependent Threads to terminate.");
            setJmsAlive("false");
        }catch(Exception e){
            myLogger.log(Level.SEVERE, "An Exception was caught while trying to set up or send PTP message. Message is: " + e.getMessage(), e);
            myLogger.warning("Telling all the JMS dependent Threads to terminate.");
            setJmsAlive("false");
        }// end try-catch
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end sendPTP

    /**
     * Gets the session. It does full null checks and recreates whatever needs recreating.
     *
     * @return the session
     * @throws Exception
     *         the exception
     */
    private Session getSession() throws Exception {
        Session session = null;
        try{
            if(connection == null){
                factory = (ConnectionFactory) context.lookup(Constants.CONNECTION_FACTORY_VALUE);
                myLogger.finer("Create the connection.");
                connection = factory.createConnection(PropertiesMgr.getProperties().getProperty(Constants.MQ_USER), PropertiesMgr.getProperties().getProperty(Constants.MQ_PASSWORD));
            }// end if
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        }catch(JMSException e){
            myLogger.log(Level.SEVERE, "A JMSException exception was caught while trying to set up or send PTP message. Message is: " + e.getMessage(), e);
            throw (e);
        }catch(Exception e){
            myLogger.severe(e.getMessage());
            throw (e);
        }// end try-catch
        return session;
    }// end getSession

    /**
     * This method sets the JmsAlive property for use by other threads.
     * 
     * @param value
     * @author <strong>Shane Duncan</strong> JCCC, Oct 6, 2015
     */
    private void setJmsAlive(String value) {
        String methodName = "setJmsAlive";
        myLogger.entering(MY_CLASS_NAME, methodName, value);
        myLogger.finest("This method sets the JmsAlive property for use by other threads.");
        Properties p = new Properties();
        p.setJmsStayAlive(value);
        ShamenThreadSafeProcessor processor = new ShamenThreadSafeProcessor(myLogger);
        processor.updateProperties(p);
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end setJmsAlive

    /**
     * This method acknowledges a Are You Alive request.
     * 
     * @param message
     *        (required)
     * @param receiptText
     * @author <strong>Shane Duncan</strong> JCCC, Aug 17, 2015
     */
    public void acknowlegeAreYouAliveMessage(Message message) {
        String methodName = "acknowlegeAreYouAliveMessage";
        myLogger.entering(MY_CLASS_NAME, methodName, new Object[]{message});
        myLogger.finest("This method acknowledges a previously sent PTP message.  All information for acknowledgement must be in the message.");
        if(message instanceof ObjectMessage){
            myLogger.finer("Attempting to acknowledge message.");
            Object obj;
            try{
                obj = ((ObjectMessage) message).getObject();
                if(obj instanceof ControllerMessage){
                    ControllerMessage msg = new ControllerMessage();
                    msg.setText(ControllerMessage.RECEIVED);
                    msg.setCorrelationID(message.getJMSCorrelationID());
                    Queue responseQ = (Queue) message.getJMSReplyTo();
                    myLogger.finer("Acknowledging message.");
                    Session session = getSession();
                    myLogger.finer("Creating JMS producer.");
                    MessageProducer producer = session.createProducer(responseQ);
                    myLogger.finer("Creating Object Message");
                    ObjectMessage responseMessage = session.createObjectMessage(msg);
                    responseMessage.setJMSCorrelationID(msg.getCorrelationID());
                    myLogger.finer("Sending message: " + responseMessage);
                    producer.send(responseMessage);
                }// end if
            }catch(JMSException e){
                myLogger.log(Level.SEVERE, "A JMSException exception was caught while trying to acknowledge Are You Alive request message. Message is: " + e.getMessage(), e);
                myLogger.warning("Telling all the JMS dependent Threads to terminate.");
                setJmsAlive("false");
            }catch(Exception e){
                myLogger.log(Level.SEVERE, "An Exception exception was caught while trying to acknowledge Are You Alive message. Message is: " + e.getMessage(), e);
                myLogger.warning("Telling all the JMS dependent Threads to terminate.");
                setJmsAlive("false");
            }// end try-catch
        }// end if
    }// end acknowlegeAreYouAliveMessage

    /**
     * This method sends Point to Point message(synchronous) to the desired queue and then wait for response.
     * 
     * @param q
     *        String(required)
     * @param msg
     *        ControllerMessage(required)
     * @param waitTime
     *        Amount of time to wait on acknowledgment. If forever, set to 0. (required)
     * @author <strong>Shane Duncan</strong> JCCC, Aug 17, 2015
     */
    public ControllerMessage sendPTPWithAcknowledge(String q, ControllerMessage msg, Integer waitTime) {
        String methodName = "sendPTPWithAcknowledge";
        myLogger.entering(MY_CLASS_NAME, methodName, new Object[]{q, msg});
        myLogger.finest("This method sends Point to Point message(synchronous) to the desired queue and then wait for response.");
        ControllerMessage receivedMessage = null;
        try{
            myLogger.finer("Setting up JMS session.");
            Destination destination = (Destination) context.lookup(q);
            Session session = getSession();
            TemporaryQueue tempQ = session.createTemporaryQueue();
            myLogger.finer("Creating JMS producer.");
            MessageProducer producer = session.createProducer(destination);
            myLogger.finer("Creating Object Message");
            ObjectMessage message = session.createObjectMessage(msg);
            message.setJMSReplyTo(tempQ);
            message.setJMSCorrelationID(msg.getCorrelationID());
            myLogger.finer("Sending message: " + msg);
            producer.send(message);
            Message receivedJmsMessage = receiveAcknowledgement(tempQ, msg.getCorrelationID(), waitTime);
            myLogger.finer("Received message: " + receivedJmsMessage);
            if(receivedJmsMessage instanceof ObjectMessage){
                Object obj;
                obj = ((ObjectMessage) receivedJmsMessage).getObject();
                if(obj instanceof ControllerMessage){
                    receivedMessage = (ControllerMessage) obj;
                }// end if
            }// end if
             // myLogger.finer("Deleting temporary Queue: " + tempQ);
             // tempQ.delete();
        }catch(JMSException e){
            myLogger.log(Level.SEVERE, "A JMSException exception was caught while trying to set up or send PTP message with acknowledge. Message is: " + e.getMessage(), e);
            myLogger.warning("Telling all the JMS dependent Threads to terminate.");
            setJmsAlive("false");
        }catch(NamingException e){
            myLogger.log(Level.SEVERE, "A NamingException was caught while trying to set up or send PTP message with acknowledge. Message is: " + e.getMessage(), e);
            setJmsAlive("false");
        }catch(Exception e){
            myLogger.log(Level.SEVERE, "An Exception was caught while trying to set up or send PTP message with acknowledge. Message is: " + e.getMessage(), e);
            setJmsAlive("false");
        }// end try-catch
        return receivedMessage;
    }// end sendPTPWithAcknowledge

    /**
     * This method sends Point to Point message(synchronous) to the desired queue and then wait for response.
     * 
     * @param q
     *        String(required)
     * @param msg
     *        ControllerMessage(required)
     * @param waitTime
     *        Amount of time to wait on acknowledgment. If forever, set to 0. (required)
     * @author <strong>Shane Duncan</strong> JCCC, Aug 17, 2015
     */
    public ControllerMessage sendNonPersistentPTPWithAcknowledge(String q, ControllerMessage msg, Integer waitTime) {
        String methodName = "sendNonPersistentPTPWithAcknowledge";
        myLogger.entering(MY_CLASS_NAME, methodName, new Object[]{q, msg});
        myLogger.finest("This method sends Point to Point message(synchronous) to the desired queue and then wait for response.");
        ControllerMessage receivedMessage = null;
        try{
            myLogger.finer("Setting up JMS session.");
            Destination destination = (Destination) context.lookup(q);
            Session session = getSession();
            TemporaryQueue tempQ = session.createTemporaryQueue();
            myLogger.finer("Creating JMS producer.");
            MessageProducer producer = session.createProducer(destination);
            myLogger.finer("Creating Object Message");
            ObjectMessage message = session.createObjectMessage(msg);
            message.setJMSReplyTo(tempQ);
            message.setJMSCorrelationID(msg.getCorrelationID());
            myLogger.finer("Sending message: " + msg);
            producer.send(message, DeliveryMode.NON_PERSISTENT, Message.DEFAULT_PRIORITY, 0);
            Message receivedJmsMessage = receiveAcknowledgement(tempQ, msg.getCorrelationID(), waitTime);
            myLogger.finer("Received message: " + receivedJmsMessage);
            if(receivedJmsMessage instanceof ObjectMessage){
                Object obj;
                obj = ((ObjectMessage) receivedJmsMessage).getObject();
                if(obj instanceof ControllerMessage){
                    receivedMessage = (ControllerMessage) obj;
                }// end if
            }// end if

        }catch(JMSException e){
            myLogger.log(Level.SEVERE, "A JMSException exception was caught while trying to set up or send PTP message with acknowledge. Message is: " + e.getMessage(), e);
            myLogger.warning("Telling all the JMS dependent Threads to terminate.");
            setJmsAlive("false");
        }catch(NamingException e){
            myLogger.log(Level.SEVERE, "A NamingException was caught while trying to set up or send PTP message with acknowledge. Message is: " + e.getMessage(), e);
            setJmsAlive("false");
        }catch(Exception e){
            myLogger.log(Level.SEVERE, "An Exception was caught while trying to set up or send PTP message with acknowledge. Message is: " + e.getMessage(), e);
            setJmsAlive("false");
        }// end try-catch
        return receivedMessage;
    }// end sendNonPersistentPTPWithAcknowledge

    /**
     * Receive Point to Point message(synchronous) as an acknowledgment to a previous sent message. This should be used sparingly.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Oct 15, 2015
     * @param q
     *        queue to receive from
     * @param correlationId
     *        the correlationId of the message to acknowledge.
     * @param waitTime
     *        The amount of time to wait on t he acknowledgment. If forever, send 0.
     * @return Message
     */
    public Message receiveAcknowledgement(Queue q, String correlationId, Integer waitTime) {
        String methodName = "receiveAcknowledgement";
        myLogger.entering(MY_CLASS_NAME, methodName, new Object[]{q, correlationId});
        myLogger.finest("Receive Point to Point message(synchronous) as an acknowledgment to a previous sent message. This should be used sparingly.");
        Message receiptMessage = null;
        try{
            Session session = getSession();
            MessageConsumer consumer = session.createConsumer(q);
            connection.start();
            Message message = consumer.receive(waitTime);
            if(message instanceof ObjectMessage){
                Object obj = null;
                obj = ((ObjectMessage) message).getObject();
                if(obj instanceof ControllerMessage){
                    receiptMessage = message;
                    myLogger.finer("Received message:" + obj);
                    if(correlationId.equals(message.getJMSCorrelationID())){
                        myLogger.finer("The correlation id matches.  The previous sent message is thusly confirmed.");
                    }else{
                        myLogger.finer("The correlation id DOES NOT match.");
                    }// end if-else
                }// end if
            }else{
                if(message != null){
                    myLogger.warning("A non object message was received during message acknowledgment.  This should never happen.  The message is: " + message);
                }// end if
            }// end if-else
        }catch(JMSException e){
            myLogger.log(Level.SEVERE, "A JMSException exception was caught while trying to acknowledge a ptp message. Message is: " + e.getMessage(), e);
            myLogger.warning("Telling all the JMS dependent Threads to terminate.");
            setJmsAlive("false");
        }catch(Exception e){
            myLogger.log(Level.SEVERE, "An Exception exception was caught while trying to acknowledge a ptp message. Message is: " + e.getMessage(), e);
            myLogger.warning("Telling all the JMS dependent Threads to terminate.");
            setJmsAlive("false");
        }// end try-catch
        myLogger.exiting(MY_CLASS_NAME, methodName, receiptMessage);
        return receiptMessage;
    }// end receiveAcknowledgement

    /**
     * This method acknowledges a previously sent PTP message. All information for acknowledgement must be in the message.
     * 
     * @param message
     *        (required)
     * @param receiptText
     * @author <strong>Shane Duncan</strong> JCCC, Aug 17, 2015
     */
    public void acknowlegePTPMessage(Message message, String receiptText) {
        String methodName = "acknowlegePTPMessage";
        myLogger.entering(MY_CLASS_NAME, methodName, new Object[]{message, receiptText});
        myLogger.finest("This method acknowledges a previously sent PTP message.  All information for acknowledgement must be in the message.");
        if(message instanceof ObjectMessage){
            myLogger.finer("Attempting to acknowledge message.");
            Object obj;
            try{
                obj = ((ObjectMessage) message).getObject();
                if(obj instanceof ControllerMessage){
                    ControllerMessage msg = new ControllerMessage();
                    msg.setText(receiptText);
                    msg.setCorrelationID(message.getJMSCorrelationID());
                    Queue responseQ = (Queue) message.getJMSReplyTo();
                    myLogger.finer("Acknowledging message.");
                    sendPTP(responseQ.getQueueName(), msg);
                }// end if
            }catch(JMSException e){
                myLogger.log(Level.SEVERE, "A JMSException exception was caught while trying to acknowledge PTP message. Message is: " + e.getMessage(), e);
                myLogger.warning("Telling all the JMS dependent Threads to terminate.");
                setJmsAlive("false");
            }catch(Exception e){
                myLogger.log(Level.SEVERE, "An Exception exception was caught while trying to acknowledge PTP message. Message is: " + e.getMessage(), e);
                myLogger.warning("Telling all the JMS dependent Threads to terminate.");
                setJmsAlive("false");
            }// end try-catch
        }// end if
    }// end acknowlegePTPMessage

    /**
     * This method receives Point to Point message(synchronous)
     * 
     * @param q
     *        String(required)
     * @author <strong>Shane Duncan</strong> JCCC, Aug 17, 2015
     */
    public Message receivePTP(String q) {
        String methodName = "receivePTP";
        myLogger.entering(MY_CLASS_NAME, methodName, q);
        myLogger.finest("This method receives Point to Point message(synchronous). ");
        Message message = null;
        try{
            myLogger.finer("Setting up JMS connection properties.");
            Destination destination = (Destination) context.lookup(q);
            Session session = getSession();
            MessageConsumer consumer = session.createConsumer(destination);
            myLogger.finer("Starting JMS connection.");
            connection.start();
            myLogger.finer("Waiting for message for 10 seconds.");
            message = consumer.receive(10000);
            session.close();
            consumer = null;
            destination = null;
            if(message instanceof ObjectMessage){
                myLogger.finer("Some type of message received");
                Object obj = null;
                obj = ((ObjectMessage) message).getObject();
                if(obj instanceof ControllerMessage){
                    myLogger.finer("Received message:" + obj);
                }// end if
            }else{
                myLogger.finer("Message was not received within time limit.");
            }// end if-else
        }catch(JMSException e){
            myLogger.log(Level.SEVERE, "A JMSException exception was caught while trying to set up or receive PTP message. Message is: " + e.getMessage(), e);
            myLogger.warning("Telling all the JMS dependent Threads to terminate.");
            setJmsAlive("false");
        }catch(NamingException e){
            myLogger.log(Level.SEVERE, "A NamingException was caught while trying to set up or receive PTP. Message is: " + e.getMessage(), e);
            myLogger.warning("Telling all the JMS dependent Threads to terminate.");
            setJmsAlive("false");
        }catch(Exception e){
            myLogger.log(Level.SEVERE, "An Exception was caught while trying to set up or receive PTP. Message is: " + e.getMessage(), e);
            myLogger.warning("Telling all the JMS dependent Threads to terminate.");
            setJmsAlive("false");
        }// end try-catch
        myLogger.exiting(MY_CLASS_NAME, methodName);
        return message;
    }// end receivePTP

    /**
     * This method publishes a message to a topic.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Aug 19, 2015
     */
    public void publish(ControllerMessage msg, String topic) {
        String methodName = "publish";
        myLogger.entering(MY_CLASS_NAME, methodName, msg);
        myLogger.finest("This method publishes a message to a topic.");
        myLogger.finer("Creating a ControllerPublisher.");
        ControllerPublisher publisher = null;
        myLogger.finer("Create an instance of ControllerPublisher.");
        publisher = new ControllerPublisher();
        myLogger.finer("Publish the message to the Topic.");
        publisher.publishToTopic(topic, msg);
        publisher = null;
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end publish

    /**
     * This method sends Point to Point message(synchronous) to the desired queue and then wait for response.
     * 
     * @param msg
     *        ControllerMessage(required)
     * @param q
     *        String(required)
     * @author <strong>Shane Duncan</strong> JCCC, Aug 17, 2015
     * @throws Exception
     */
    public void sendPTP(ControllerMessage msg, String q) {
        String methodName = "sendPTP";
        myLogger.entering(MY_CLASS_NAME, methodName, new Object[]{q, msg});
        myLogger.finest("This method sends Point to Point message(synchronous) to the desired queue.");
        Session session = null;
        try{
            myLogger.finer("Setting up JMS session.");
            Destination destination = (Destination) context.lookup(q);
            session = getSession();
            myLogger.finer("Creating JMS producer.");
            MessageProducer producer = session.createProducer(destination);
            myLogger.finer("Creating Object Message");
            ObjectMessage message = session.createObjectMessage(msg);
            message.setJMSCorrelationID(msg.getCorrelationID());
            myLogger.finer("Sending message: " + msg);
            producer.send(message);
        }catch(JMSException e){
            myLogger.log(Level.SEVERE, "A JMSException exception was caught while trying to set up or send PTP message. Message is: " + e.getMessage(), e);
            // myLogger.warning("Telling all the JMS dependent Threads to terminate.");
            // setJmsAlive("false");
            // throw(e);
        }catch(NamingException e){
            myLogger.log(Level.SEVERE, "A NamingException was caught while trying to set up or send PTP message. Message is: " + e.getMessage(), e);
            myLogger.warning("Telling all the JMS dependent Threads to terminate.");
            setJmsAlive("false");
            // throw(e);
        }catch(Exception e){
            myLogger.log(Level.SEVERE, "An Exception was caught while trying to set up or send PTP message. Message is: " + e.getMessage(), e);
            // setJmsAlive("false");
            // throw(e);
        }finally{
            if(session != null){
                try{
                    session.close();
                }catch(JMSException e){
                    myLogger.log(Level.SEVERE, "An Exception was caught while trying to set up or send PTP message. Message is: " + e.getMessage(), e);
                }
            }
        }
        // end try-catch
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end sendPTP

    /**
     * This method cleans up the shared JMS resources. This should only be called in instances where a new JmsManager is created, used and then discarded.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Aug 19, 2015
     */
    public void cleanUp() {
        String methodName = "cleanUp";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method cleans up the shared JMS resources.");
        myLogger.finer("Close the connection.");
        context = null;
        try{
            connection.close();
        }catch(JMSException e){
            myLogger.warning("Unable to JMS close connection.  This may or may not be an issue.  Reason is: " + e.getMessage());
        }// end try-catch
        connection = null;
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end cleanUp

    /**
     * This method clears all messages from a Queue by receiving them all with auto acknowledge.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Aug 17, 2015
     */
    public Message clearQ(String q) {
        String methodName = "clearQ";
        myLogger.entering(MY_CLASS_NAME, methodName, q);
        myLogger.finest("This method clears all messages from a Queue by receiving them all with auto acknowledge.");
        TextMessage text = null;
        try{
            myLogger.finer("Setting up JMS connection properties.");
            Destination destination = (Destination) context.lookup(q);
            Session session = getSession();
            MessageConsumer consumer = session.createConsumer(destination);
            myLogger.finer("Starting JMS connection.");
            connection.start();
            while(true){
                Message message = consumer.receive(1);
                if(message instanceof ObjectMessage){
                    Object obj = null;
                    obj = ((ObjectMessage) message).getObject();
                    if(obj instanceof ControllerMessage){
                        myLogger.finer("Cleared message:" + obj);
                    }// end if
                }else{
                    myLogger.finer("Queue: " + q + " has been emptied");
                    break;
                }// end if-else
            }// end while
        }catch(JMSException e){
            myLogger.log(Level.SEVERE, "A JMSException exception was caught while trying to clear a queue. Message is: " + e.getMessage(), e);
            myLogger.warning("Telling all the JMS dependent Threads to terminate.");
            setJmsAlive("false");
        }catch(NamingException e){
            myLogger.log(Level.SEVERE, "A NamingException was caught while trying to clear a queue. Message is: " + e.getMessage(), e);
            myLogger.warning("Telling all the JMS dependent Threads to terminate.");
            setJmsAlive("false");
        }catch(Exception e){
            myLogger.log(Level.SEVERE, "An Exception was caught while trying to clear a queue. Message is: " + e.getMessage(), e);
            myLogger.warning("Telling all the JMS dependent Threads to terminate.");
            setJmsAlive("false");
        }// end try-catch
        return text;
    }// end clearQ

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Aug 17, 2015
     * @return the customProperties
     */
    public CustomProperties getCustomProperties() {
        return customProperties;
    }// end getCustomProperties

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Aug 17, 2015
     * @param customProperties
     *        the customProperties to set
     */
    public void setCustomProperties(CustomProperties customProperties) {
        myLogger.info("Setting up connection to jms with the following properties: " + String.valueOf(customProperties));
        // customProperties.put(Context.SECURITY_PRINCIPAL, "isu02#is");
        // customProperties.put(Context.SECURITY_CREDENTIALS, "itsd");
        this.customProperties = customProperties;
    }// end setCustomProperties

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Aug 26, 2015
     * @return the controllerRequestQueue
     */
    public Queue getControllerRequestQueue() {
        return controllerRequestQueue;
    }// end getControllerRequestQueue

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Aug 26, 2015
     * @param controllerRequestQueue
     *        the controllerRequestQueue to set
     */
    public void setControllerRequestQueue(Queue controllerRequestQueue) {
        this.controllerRequestQueue = controllerRequestQueue;
    }// end setControllerRequestQueue

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Aug 26, 2015
     * @return the controllerResponseQueue
     */
    public Queue getControllerResponseQueue() {
        return controllerResponseQueue;
    }// end getControllerResponseQueue

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Aug 26, 2015
     * @param controllerResponseQueue
     *        the controllerResponseQueue to set
     */
    public void setControllerResponseQueue(Queue controllerResponseQueue) {
        this.controllerResponseQueue = controllerResponseQueue;
    }// end setControllerResponseQueue
}
