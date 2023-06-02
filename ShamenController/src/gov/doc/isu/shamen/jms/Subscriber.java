/**
 * @author <strong>Shane Duncan</strong> JCCC, Aug 19, 2015
 */
package gov.doc.isu.shamen.jms;

import java.util.logging.Level;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import gov.doc.isu.gtv.managers.PropertiesMgr;
import gov.doc.isu.shamen.core.Constants;
import gov.doc.isu.shamen.processors.ShamenThreadSafeProcessor;
import gov.doc.isu.shamen.thread.ControllerThread;

/**
 * This is the abstract class from which all subscribers will be built upon
 * 
 * @author <strong>Shane Duncan</strong> JCCC, Aug 19, 2015
 */
public abstract class Subscriber extends ControllerThread implements MessageListener, ExceptionListener {
    // The reference to the JNDI Context
    private InitialContext ctx = null;
    // Private static names for the Administered JMS Objects
    private static String connectionFactoryName = null;
    private static String topicName = null;
    private ConnectionFactory tcf = null;
    private TopicSubscriber subscriber = null;
    private Connection topicConnection = null;
    private Topic topic = null;
    private Session ses = null;
    private static final String MY_CLASS_NAME = "gov.doc.isu.shamen.jms.Subscriber";

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Aug 19, 2015
     */
    public Subscriber() {
        super();
        loadProperties();
    }// end constructor

    /**
     * Overloaded constructor.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Aug 19, 2015
     */
    public Subscriber(String threadName) {
        super(threadName);
        loadProperties();
    }// end constructor

    /**
     * This is the method that must be implemented from the MessageListener interface. This method will be called when a message has arrived at the Topic and the container calls this method and passes the Message.
     */
    @Override
    abstract public void onMessage(Message arg0);

    /**
     * The run method is necessary because this method implements the Runnable interface to keep the thread alive and waiting for messages. Otherwise, this would would not stay alive and would not be able to listen for messages asyncronously.
     */
    public void run() {
        while(true){
            synchronized(this){
                try{
                    wait();
                }catch(InterruptedException ex){
                    myLogger.getChild().log(Level.SEVERE, "A InterruptedException has been caught while waiting for subscription msg. Message is: " + ex.getMessage(), ex);
                }// end try-catch
            }// end inner method
        }// end while
    }// end run

    // Private Accessors for Connection Factory Name
    private static String getConnectionFactoryName() {
        return connectionFactoryName;
    }// end method

    // Private mutator for the Connection factory Name
    private static void setConnectionFactoryName(String name) {
        connectionFactoryName = name;
    }// end method

    // Private Accessors for the Topic Name
    private static String getTopicName() {
        return topicName;
    }// end method

    // Private mutator for Topic Name
    private static void setTopicName(String name) {
        topicName = name;
    }// end method

    /**
     * This method is called to set up and initialize the necessary Connection and Session references.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 3, 2015
     * @param msgSelector
     *        String(required)
     * @throws JMSException
     * @throws NamingException
     */
    public void init() throws JMSException, NamingException {
        String methodName = "init";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest(methodName, "This method is called to set up and initialize the necessary Connection and Session references. ");
        // set up the message selector so this controller only gets the messages it should
        String msgSelector = "controller = " + "'" + PropertiesMgr.getProperties().getProperty(Constants.MESSAGE_SELECTOR) + "' OR controller = 'ALL'";
        try{
            myLogger.finer(methodName, "Set up all the JMS connection properties.");
            myLogger.finer(methodName, "Get a connection to the QueueConnectionFactory");
            tcf = (ConnectionFactory) ctx.lookup(getConnectionFactoryName());
            myLogger.finer(methodName, "Create a connection.");
            topicConnection = tcf.createConnection(PropertiesMgr.getProperties().getProperty(Constants.MQ_USER),PropertiesMgr.getProperties().getProperty(Constants.MQ_PASSWORD));
            topicConnection.setClientID(PropertiesMgr.getProperties().getProperty(Constants.MESSAGE_SELECTOR));
            myLogger.finer(methodName, "Create a session that is non-transacted and is notified automatically.");
            ses = topicConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            myLogger.finer(methodName, "Look up a destination.");
            topic = (Topic) ctx.lookup(getTopicName());
            myLogger.finer(methodName, "Create the receiver with a msgSelector. The msgSelector may be null. The no Local parameter is set so that this subscriber will not receive copies of its own messages.");
            subscriber = ses.createDurableSubscriber(topic, PropertiesMgr.getProperties().getProperty(Constants.MESSAGE_SELECTOR), msgSelector, false);
        }catch(NamingException ex){
            myLogger.getChild().severe("A naming exception has been caught while trying to set up the subscriber. Message is: " + ex.getMessage());

            myLogger.warning(methodName, "Telling all the JMS dependent Threads to terminate.");
            setJmsAlive("false");
            throw (ex);
        }catch(JMSException ex){
            myLogger.getChild().severe("An exception has been caught while trying to set up the subscriber. Message is: " + ex.getMessage());

            myLogger.warning(methodName, "Telling all the JMS dependent Threads to terminate.");
            setJmsAlive("false");
            throw (ex);
        }catch(Exception ex){
            myLogger.getChild().severe("An exception has been caught while trying to set up the subscriber. Message is: " + ex.getMessage());

            myLogger.warning(methodName, "Telling all the JMS dependent Threads to terminate.");
            setJmsAlive("false");

        }finally{
            try{
                myLogger.finer(methodName, "Close up the JNDI connection since we have found what we needed.");
                ctx.close();
            }catch(Exception ex){
                myLogger.finer(methodName, "Unable to close JNDI connection.");
            }// end try-catch
        }// end finally
        myLogger.finer(methodName, "Inform the received that the callbacks should be sent to this instance.");
        subscriber.setMessageListener(this);
        myLogger.finer(methodName, "Start listening.");
        topicConnection.start();
        myLogger.finer(methodName, "Listening on topic " + topic.getTopicName());
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end init

    /**
     * This method sets the JmsAlive property for use by other threads.
     * 
     * @param value
     * @author <strong>Shane Duncan</strong> JCCC, Oct 6, 2015
     */
    private void setJmsAlive(String value) {
        String methodName = "updateProperties";
        myLogger.entering(MY_CLASS_NAME, methodName, value);
        myLogger.finest(methodName, "This method sets the JmsAlive property for use by other threads.");
        gov.doc.isu.shamen.models.Properties p = new gov.doc.isu.shamen.models.Properties();
        p.setJmsStayAlive(value);
        ShamenThreadSafeProcessor processor = new ShamenThreadSafeProcessor(myLogger.getChild());
        processor.updateProperties(p);
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end setJmsAlive

    /**
     * This method is called to load the JMS resource properties
     * 
     * @throws NamingException
     */
    private void loadProperties() {
        String methodName = "loadProperties";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest(methodName, "This method is called to load the JMS resource properties.");
        try{
            myLogger.finer(methodName, "Use the custom properties to load the JNDI stuff.");
            ctx = new InitialContext(PropertiesMgr.getProperties());
        }catch(NamingException e){
            myLogger.getChild().severe("A naming exception has been caught while trying to load context for the subscriber. Message is: " + e.getMessage());
        }// end try-catch
        myLogger.finer(methodName, "Set the JMS Administered values for this instance.");
        setConnectionFactoryName(Constants.CONNECTION_FACTORY_VALUE);
        setTopicName(Constants.TOPIC);
    }// end loadProperties

    /**
     * This method closes all the JMS connections so that a new one may be opened by another thread on reset.
     */
    public void closeAllConnections() {
        String methodName = "closeAllConnections";
        try{
            topicConnection.close();
            subscriber.close();
            ses.close();
            topic = null;
        }catch(JMSException ex){
            myLogger.getChild().severe("An exception has been caught while trying to close all JMS resources. Message is: " + ex.getMessage());
        }catch(Exception ex){
            myLogger.getChild().severe("An exception has been caught while trying to close all JMS resources. Message is: " + ex.getMessage());
        }finally{
            try{
                myLogger.finer(methodName, "Close up the JNDI connection since we have found what we needed.");
                ctx.close();
            }catch(Exception ex){
                myLogger.finer(methodName, "Unable to close JNDI connection.");
            }// end try-catch
        }// end finally
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Aug 25, 2015
     * @return the ses
     */
    public Session getSes() {
        return ses;
    }// end getSes

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Aug 25, 2015
     * @param ses
     *        the ses to set
     */
    public void setSes(Session ses) {
        this.ses = ses;
    }// end setSes
}// end class
