/**
 * @author <strong>Shane Duncan</strong> JCCC, Aug 19, 2015
 */
package gov.doc.isu.shamen.jms;

import java.util.logging.Logger;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import gov.doc.isu.gtv.logging.ApplicationLogger;
import gov.doc.isu.gtv.managers.PropertiesMgr;
import gov.doc.isu.shamen.core.Constants;

/**
 * This is the abstract class from which all publishers will be built upon
 * 
 * @author <strong>Shane Duncan</strong> JCCC, Aug 19, 2015
 * @author <strong>Steven Skinner</strong> JCCC, Oct 01, 2019
 */
public abstract class Publisher {
    // The reference to the JNDI Context
    protected InitialContext ctx = null;
    // Private static names for the Administered JMS Objects
    private static String connectionFactoryName = null;
    private static String topicName = null;
    // Private instance references
    protected ConnectionFactory tcf = null;
    protected Connection connection = null;
    protected Session ses = null;
    protected Topic topic = null;
    private MessageProducer publisher = null;
    protected ApplicationLogger appLogger = null;
    private static final String MY_CLASS_NAME = "gov.doc.isu.shamen.jms.Publisher";
    private static Logger myLogger = Logger.getLogger(MY_CLASS_NAME);

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Aug 19, 2015
     */
    public Publisher() {
        super();
        // Initialize an instance of ApplicationLogger for this class.

        appLogger = ApplicationLogger.getInstance();

        loadProperties();
    }

    // Private Accessors for Connection Factory Name
    protected static String getConnectionFactoryName() {
        return connectionFactoryName;
    }

    // Private mutator for the Connection factory Name
    private static void setConnectionFactoryName(String name) {
        connectionFactoryName = name;
    }

    // Private Accessors for the Topic Name
    protected static String getTopicName() {
        return topicName;
    }

    // Private mutator for Topic Name
    private static void setTopicName(String name) {
        topicName = name;
    }

    /**
     * This method is called to set up and initialize the necessary Connection and Session references.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 3, 2015
     * @throws JMSException
     * @throws NamingException
     */
    public void init() throws JMSException, NamingException {
        String methodName = "init";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method is called to set up and initialize the necessary Connection and Session references.");
        try{
            myLogger.finer("Get a connection to the QueueConnectionFactory.");
            tcf = (ConnectionFactory) ctx.lookup(getConnectionFactoryName());
            myLogger.finer("Create a connection.");
            connection = tcf.createConnection(PropertiesMgr.getProperties().getProperty("USER"),PropertiesMgr.getProperties().getProperty("PASSWORD"));
            myLogger.finer("Create a session that is non-transacted and is notified automatically.");
            ses = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            myLogger.finer("Look up a destination.");
            topic = (Topic) ctx.lookup(getTopicName());
            myLogger.finer("Create the publisher.");
            setPublisher(ses.createProducer(topic));
        }catch(NamingException ex){
            appLogger.getChild().severe("A naming exception has been caught while trying to set up the publisher. Message is: " + ex.getMessage());
            System.exit(-1);
        }finally{
            try{
                // Close up the JNDI connection since we have found what we needed
                ctx.close();
            }catch(Exception ex){
                appLogger.bold("An exception has been caught while trying to JNDI context after setting up the publisher. Message is: " + ex.getMessage());
            }// end try catch
        }// end finally
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }// end init

    /**
     * This method is called to load the JMS resource properties *
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 3, 2015
     */
    private void loadProperties() {
        String methodName = "loadProperties";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest("This method is called to load the JMS resource properties.");
        myLogger.finer("Use the custom properties to load the JNDI stuff.");
        try{
            ctx = new InitialContext(PropertiesMgr.getProperties());
        }catch(NamingException e){
            appLogger.getChild().severe("A NamingException exception has been caught while trying to load properties for a publisher. Message is: " + e.getMessage());
            System.exit(-1);
        }
        myLogger.finer("Set the JMS Administered values for this instance.");
        setConnectionFactoryName(Constants.CONNECTION_FACTORY_VALUE);
        setTopicName(Constants.TOPIC);
        myLogger.exiting(MY_CLASS_NAME, methodName);
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Aug 19, 2015
     * @return the publisher
     */
    public MessageProducer getPublisher() {
        return publisher;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Aug 19, 2015
     * @param publisher
     *        the publisher to set
     */
    public void setPublisher(MessageProducer publisher) {
        this.publisher = publisher;
    }
}
