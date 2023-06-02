/**
 * @(#)Publisher.java 1.0.0 Copyright (c) 2018 Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit All Rights Reserved. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: -Redistribution of source code must retain the above copyright notice, this list of conditions and the following disclaimer. -Redistribution in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. Neither the name of Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit or the names of contributors may be used to endorse or promote products derived from this software without specific prior written permission. This software is provided "AS IS," without a warranty of any kind. ALL EXPRESS OR IMPLIED CONDITIONS,
 *                    REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. You acknowledge that this
 *                    software is not designed, licensed or intended for use in the design, construction, operation or maintenance of any nuclear facility.
 */
package gov.doc.isu.shamen.jms;

import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.doc.isu.shamen.core.ShamenConstants;

/**
 * This is the abstract class from which all publishers will be built upon
 * 
 * @author <strong>Shane Duncan</strong> JCCC, Aug 19, 2015
 * @author <strong>Steven Skinner</strong> JCCC, Oct 01, 2019
 */
public abstract class Publisher {
    private static final String MY_CLASS_NAME = "gov.doc.isu.shamen.jms.AppTopicListener";
    private static Log myLogger = LogFactory.getLog(MY_CLASS_NAME);
    // The reference to the JNDI Context
    protected InitialContext ctx;
    // Private static names for the Administered JMS Objects
    private static String connectionFactoryName;
    private static String topicName;
    // Private instance references
    protected TopicConnectionFactory tcf;
    protected TopicConnection topicConnection;
    protected TopicSession ses;
    protected Topic topic;

    private TopicPublisher publisher;

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Aug 19, 2015
     */
    public Publisher() {
        super();
        loadProperties();
    }

    /**
     * Private Accessors for Connection Factory Name
     * 
     * @return connectionFactoryName
     */
    protected static String getConnectionFactoryName() {
        return connectionFactoryName;
    }

    /**
     * Private mutator for the Connection factory Name
     * 
     * @param name
     *        name
     */
    private static void setConnectionFactoryName(String name) {
        connectionFactoryName = name;
    }

    /**
     * Private Accessors for the Topic Name
     * 
     * @return topicName
     */
    protected static String getTopicName() {
        return topicName;
    }

    /**
     * Private mutator for Topic Name
     * 
     * @param name
     *        name
     */
    private static void setTopicName(String name) {
        topicName = name;
    }

    /**
     * This method is called to set up and initialize the necessary Connection and Session references.
     * 
     * @throws JMSException
     *         exeption that can be thrown by this method
     * @throws NamingException
     *         exeption that can be thrown by this method
     */
    public void init() throws JMSException, NamingException {
        myLogger.debug("Entering init()");
        myLogger.trace("This method is called to set up and initialize the necessary Connection and Session references.");
        try{

            // Get a connection to the QueueConnectionFactory
            tcf = (TopicConnectionFactory) ctx.lookup(getConnectionFactoryName());

            // Create a connection
            topicConnection = tcf.createTopicConnection();

            // Create a session that is non-transacted and is notified automatically
            TopicSession session = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);

            // Look up a destination
            topic = (Topic) ctx.lookup(getTopicName());

            // Create the publisher
            setPublisher(session.createPublisher(topic));

            // It's a good idea to always put a finally block so that the
            // context is closed
        }catch(NamingException ex){
            myLogger.error("NamingException occurred trying to initialize Publisher.", ex);
            System.exit(-1);
        }finally{
            try{
                // Close up the JNDI connection since we have found what we needed
                ctx.close();
            }catch(Exception ex){
                myLogger.error("Exception occurred trying to close the InitialContext.", ex);
            }
        }
        myLogger.debug("Exiting init()");
    }

    /**
     * This method is called to load the JMS resource properties
     * 
     * @throws NamingException
     */
    private void loadProperties() {
        myLogger.debug("Entering loadProperties()");
        myLogger.trace("This method is called to load the JMS resource properties.");
        // Use the custom properties to load the JNDI stuff.
        try{
            Properties properties = new Properties();
            properties.put(InitialContext.INITIAL_CONTEXT_FACTORY, ShamenConstants.INITIAL_CONTEXT_FACTORY);
            properties.put(InitialContext.PROVIDER_URL, ShamenConstants.PROVIDER_URL);
            properties.put(ShamenConstants.CONNECTION_FACTORY, ShamenConstants.CONNECTION_FACTORY_VALUE);

            // Use the custom properties to load the JNDI stuff.
            ctx = new InitialContext(properties);

        }catch(NamingException e){
            myLogger.error("Naming Exception trying to load properties.", e);
        }

        // Set the JMS Administered values for this instance
        setConnectionFactoryName(ShamenConstants.CONNECTION_FACTORY);
        setTopicName(ShamenConstants.TOPIC_NAME);
        myLogger.debug("Exiting loadProperties()");
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Aug 19, 2015
     * @return the publisher
     */
    public TopicPublisher getPublisher() {
        return publisher;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Aug 19, 2015
     * @param publisher
     *        the publisher to set
     */
    public void setPublisher(TopicPublisher publisher) {
        this.publisher = publisher;
    }

}
