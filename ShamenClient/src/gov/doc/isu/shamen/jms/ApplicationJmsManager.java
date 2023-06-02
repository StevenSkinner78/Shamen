/**
 * @(#)ApplicationJmsManager.java 1.0.0 Copyright (c) 2018 Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit All Rights Reserved. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: -Redistribution of source code must retain the above copyright notice, this list of conditions and the following disclaimer. -Redistribution in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. Neither the name of Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit or the names of contributors may be used to endorse or promote products derived from this software without specific prior written permission. This software is provided "AS IS," without a warranty of any kind. ALL EXPRESS OR IMPLIED
 *                                CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. You
 *                                acknowledge that this software is not designed, licensed or intended for use in the design, construction, operation or maintenance of any nuclear facility.
 */
package gov.doc.isu.shamen.jms;

import java.util.HashMap;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TemporaryQueue;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.properties.EncryptableProperties;

import gov.doc.isu.shamen.core.ShamenConstants;

/**
 * This class contains all the necessary methods to receive/send/listen for jms.
 * 
 * @author <strong>Shane Duncan</strong> JCCC, Aug 17, 2015
 * @author <strong>Steven Skinner</strong> JCCC, Oct 01, 2019
 */
public class ApplicationJmsManager {

    private ConnectionFactory factory = null;
    private Context context = null;
    private Connection connection = null;
    private static final String MY_CLASS_NAME = "gov.doc.isu.shamen.jms.ApplicationJmsManager";
    private static Log log = LogFactory.getLog(MY_CLASS_NAME);
    private static StandardPBEStringEncryptor encryptor;

    protected static ApplicationJmsManager INSTANCE = null;

    /**
     * This method returns the private static final instance variable to enforce the Singleton pattern. o
     * 
     * @return An instance of this class.
     * @throws Exception
     */
    public static ApplicationJmsManager getInstance() throws Exception {
        // if (INSTANCE == null) {
        try{
            // Use this method to decrypt the passwords for use in the application.
            encryptor = new StandardPBEStringEncryptor();
            // This is the encryption / decryption password key
            encryptor.setPassword(getEncryptionKey());
            INSTANCE = new ApplicationJmsManager();
        }catch(Exception e){
            log.error("Exception occurred trying to create a new instance of the ApplicationJmsManager.", e);
            throw new Exception(e.getMessage());
        }
        // }
        return INSTANCE;
    }// end getInstance

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Aug 17, 2015
     * @throws NamingException
     * @throws JMSException
     */
    private ApplicationJmsManager() throws Exception {
        log.debug("Entering: ApplicationJmsManager constructor");
        Properties properties = new EncryptableProperties(encryptor);
        properties.put(InitialContext.INITIAL_CONTEXT_FACTORY, ShamenConstants.INITIAL_CONTEXT_FACTORY);
        properties.put(InitialContext.PROVIDER_URL, ShamenConstants.PROVIDER_URL);
        properties.put(ShamenConstants.CONNECTION_FACTORY, ShamenConstants.CONNECTION_FACTORY_VALUE);
        properties.put(ShamenConstants.MQ_USER, ShamenConstants.MQ_USER_VALUE);
        properties.put(ShamenConstants.MQ_PASSWORD, ShamenConstants.MQ_PASSWORD_VALUE);

        // Use the custom properties to load the JNDI stuff.
        context = new InitialContext(properties);
        factory = (ConnectionFactory) context.lookup(ShamenConstants.CONNECTION_FACTORY);
        connection = factory.createConnection(properties.getProperty(ShamenConstants.MQ_USER), properties.getProperty(ShamenConstants.MQ_PASSWORD));
        // connection.start();
        log.debug("Exiting: ApplicationJmsManager constructor");
    }

    public Boolean isConnected() {
        Boolean connected = false;
        if(connection != null){
            connected = true;
        }
        return connected;
    }

    /**
     * Consume a message from a given destination
     * 
     * @param destinationName
     *        (required)
     * @return message
     * @author <strong>Shane Duncan</strong> JCCC, Aug 17, 2015
     */
    public Message consumeMessageFromDestination(String destinationName, String selector) throws Exception {
        String methodName = "consumeMessageFromDestination";
        log.debug("Entering: " + methodName + " with destination: " + String.valueOf(destinationName) + ", selector: " + String.valueOf(selector));
        log.trace("This method will receive a message(synchronous).");

        Message text = null;
        Session session = null;
        Destination destination = null;
        MessageConsumer consumer = null;
        try{
            destination = (Destination) context.lookup(destinationName);
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            log.trace("Session successfully created.");
            consumer = session.createConsumer(destination, selector);
            log.trace("Consumer successfully created.");
            connection.start();
            log.trace("Connection successfully started.");
            Message message = consumer.receive(300000);
            if(message != null){
                log.info("***********Received a message**************** On " + String.valueOf(destinationName));
                if(message instanceof ObjectMessage){
                    Object obj = null;

                    obj = ((ObjectMessage) message).getObject();

                    if(obj instanceof HashMap){
                        text = message;
                        log.info("Message is HashMap message");
                    }// end if
                }// end if
            }else{
                log.trace("No message was received within the specified timeframe.");
            }// end-else
        }catch(Exception e){
            log.error("Exception occurred receiving message.", e);
            throw e;
        }finally{
            closeJmsResources(session, connection, consumer, null);
        }// end try-catch-finally
        log.debug("Exiting: " + methodName);
        return text;
    }// end consumeMessageFromDestination

    /**
     * This method will close the jms resources that are sent to it. It has full null checks.
     * 
     * @param ses
     *        session to be closed
     * @param con
     *        connection to be closed
     * @param consumer
     *        consumer to be closed
     * @param producer
     *        producer to be closed
     */
    public void closeJmsResources(Session ses, Connection con, MessageConsumer consumer, MessageProducer producer) {
        log.debug("Entering: closeJmsResources with session, connection, consumer, producer: " + new Object[]{ses, con, consumer, producer});
        log.trace("This method will close the jms resources that are sent to it.  It has full null checks.");
        try{
            // This set at debug level because it is so highly traffic'd
            log.debug("Close the JMS resources.");
            if(consumer != null){
                consumer.close();
            }// end if
            if(producer != null){
                producer.close();
            }// end if
            if(ses != null){
                ses.close();
            }// end if
            if(con != null){
                con.close();
            }// end if
        }catch(JMSException e){
            log.error("Exception occurred trying to close a resource.", e);
        }// end try-catch
        log.debug("Exiting: closeJmsResources");
    }// end closeJmsResources

    /**
     * This method sends Point to Point message(synchronous) to the desired queue and then wait for response.
     * 
     * @param destinationString
     *        String(required)
     * @param msg
     *        ControllerMessage(required)
     * @param waitTime
     *        Amount of time to wait on acknowledgment. If forever, set to 0. (required)
     * @author <strong>Shane Duncan</strong> JCCC, Aug 17, 2015
     */
    public HashMap sendPTPWithAcknowledge(String destinationString, HashMap<String, String> messageMap, Integer waitTime) {
        log.debug("Entering sendPTPWithAcknowledge with " + new Object[]{destinationString, messageMap, waitTime});
        log.trace("This method sends Point to Point message(synchronous) to the desired queue and then wait for response.");
        HashMap<String, String> receivedMessage = null;
        try{
            log.debug("Setting up JMS session.");
            Destination destination = (Destination) context.lookup(destinationString);
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            connection.start();
            TemporaryQueue tempQ = session.createTemporaryQueue();
            log.debug("Creating JMS producer.");
            MessageProducer producer = session.createProducer(destination);
            log.debug("Creating Object Message");
            ObjectMessage message = session.createObjectMessage(messageMap);
            String correlationId = String.valueOf(System.currentTimeMillis());
            message.setJMSReplyTo(tempQ);
            message.setJMSCorrelationID(correlationId);
            log.info("Sending message: " + messageMap);
            producer.send(message);
            Message receivedJmsMessage = receiveAcknowledgement(tempQ, correlationId, waitTime);
            log.debug("Received message: " + receivedJmsMessage);
            if(receivedJmsMessage instanceof ObjectMessage){
                // shouldn't need to delete tempQ receiveAcknowledgement calls closeJmsResources which should delete tempQ
                if(null != tempQ){
                    try{
                        tempQ.delete();
                    }catch(JMSException e){
                        log.warn("A JMSException exception was caught while trying to delete a tempQ after the message was acknowledged. Message is: " + e.getMessage(), e);
                    }catch(Exception e){
                        log.warn("An Exception was caught while trying to delete a tempQ after the message was acknowledged. Message is: " + e.getMessage(), e);
                    }// end try-catch
                }
                closeJmsResources(session, connection, null, producer);
                Object obj;
                obj = ((ObjectMessage) receivedJmsMessage).getObject();
                if(obj instanceof HashMap){
                    receivedMessage = (HashMap) obj;
                }// end if
            }// end if
        }catch(JMSException e){
            log.error("A JMSException exception was caught while trying to set up or send PTP message with acknowledge. Message is: " + e.getMessage(), e);

        }catch(NamingException e){
            log.error("A NamingException was caught while trying to set up or send PTP message with acknowledge. Message is: " + e.getMessage(), e);

        }catch(Exception e){
            log.error("An Exception was caught while trying to set up or send PTP message with acknowledge. Message is: " + e.getMessage(), e);

        }// end try-catch
        log.debug("Exiting sendPTPWithAcknowledge with message: " + receivedMessage);
        return receivedMessage;
    }// end sendPTPWithAcknowledge

    /**
     * This method sends Point to Point object message(synchronous) to the desired destination.
     * 
     * @param q
     *        String(required)
     * @param messageMap
     *        HashMap(required)
     * @param correlationId
     *        (not required)
     * @author <strong>Shane Duncan</strong> JCCC, Aug 17, 2015
     */
    public void sendToDestination(Destination q, HashMap<String, String> messageMap, String correlationId) {
        log.debug("Entering sendToDestination with q = " + String.valueOf(q) + ", messageMap = " + String.valueOf(messageMap) + ", correlationId = " + String.valueOf(correlationId));
        log.trace("This method sends Point to Point message(synchronous) to the desired destination.");
        MessageProducer producer = null;
        Session session = null;
        try{
            log.debug("Setting up JMS session.");
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            connection.start();
            log.debug("Creating JMS producer.");
            producer = session.createProducer(q);
            producer.setTimeToLive(20000);
            log.debug("Creating Object Message");
            ObjectMessage message = session.createObjectMessage(messageMap);
            if(correlationId != null){
                message.setJMSCorrelationID(correlationId);
            }// end if
            log.debug("Sending message: " + messageMap);
            producer.send(message);

        }catch(JMSException e){
            log.error("A JMSException exception was caught while trying to set up or send PTP message with acknowledge. Message is: " + e.getMessage(), e);
        }catch(Exception e){
            log.error("An Exception was caught while trying to set up or send PTP message with acknowledge. Message is: " + e.getMessage(), e);
        }finally{
            closeJmsResources(session, connection, null, producer);
        }// end try-catch-finally
        log.debug("Exiting sendToDestination");
    }// end sendPTP

    /**
     * This method sends Point to Point object message(synchronous) to the desired destination.
     * 
     * @param q
     *        String(required)
     * @param messageMap
     *        HashMap(required)
     * @param correlationId
     *        (not required)
     * @author <strong>Shane Duncan</strong> JCCC, Aug 17, 2015
     */
    public void sendToDestination(String destinationString, HashMap<String, String> messageMap, String correlationId) {
        log.debug("Entering sendToDestination with q = " + String.valueOf(destinationString) + ", messageMap = " + String.valueOf(messageMap) + ", correlationId = " + String.valueOf(correlationId));
        log.trace("This method sends Point to Point message(synchronous) to the desired destination.");
        MessageProducer producer = null;
        Session session = null;
        try{
            log.debug("Setting up JMS session.");
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            connection.start();
            log.debug("Creating JMS producer.");
            Destination destination = (Destination) context.lookup(destinationString);
            producer = session.createProducer(destination);
            producer.setTimeToLive(20000);
            log.debug("Creating Object Message");
            ObjectMessage message = session.createObjectMessage(messageMap);
            if(correlationId != null){
                message.setJMSCorrelationID(correlationId);
            }// end if
            log.debug("Sending message: " + messageMap);
            producer.send(message);

        }catch(JMSException e){
            log.error("A JMSException exception was caught while trying to set up or send PTP message with acknowledge. Message is: " + e.getMessage(), e);
        }catch(Exception e){
            log.error("An Exception was caught while trying to set up or send PTP message with acknowledge. Message is: " + e.getMessage(), e);
        }finally{
            closeJmsResources(session, connection, null, producer);
        }// end try-catch-finally
        log.debug("Exiting sendToDestination");
    }// end sendToDestination

    /**
     * Receive Point to Point message(synchronous) as an acknowledgment to a previous sent message. This should be used sparingly. This will wait the desired milliseconds before it gives up.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Oct 15, 2015
     * @param q
     *        queue to receive from
     * @param correlationId
     *        the correlationId of the message to acknowledge.
     * @param waitTime
     *        The amount of time(in milliseconds) to wait on the acknowledgment. If forever, send 0.
     * @return Message
     */
    public Message receiveAcknowledgement(Queue q, String correlationId, Integer waitTime) {
        String methodName = "receiveAcknowledgement";
        log.debug("Entering: " + methodName);
        log.trace("Receive Point to Point message(synchronous) as an acknowledgment to a previous sent message. This should be used sparingly. This will wait the desired milliseconds before it gives up.");
        log.info("Wait " + String.valueOf(waitTime) + " milliseconds for a response.");
        Message receiptMessage = null;
        Session session = null;
        MessageConsumer consumer = null;
        try{
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            consumer = session.createConsumer(q);
            connection.start();
            Message message = consumer.receive(waitTime);
            if(message != null){
                log.info("***********Received a message**************** On " + String.valueOf(q));
                if(message instanceof ObjectMessage){
                    Object obj = null;
                    obj = ((ObjectMessage) message).getObject();
                    if(obj instanceof HashMap){
                        receiptMessage = message;
                        log.debug("Received is hashmap message:" + obj);
                        if(correlationId.equals(message.getJMSCorrelationID())){
                            log.debug("The correlation id matches.  The previous sent message is thusly confirmed.");
                        }else{
                            log.debug("The correlation id DOES NOT match. This message is being discarded.");
                        }// end if-else
                    }// end if
                }else{
                    if(message != null){
                        log.info("A non object message was received during message acknowledgment.  This should never happen.  The message is: " + message);
                    }// end if
                }// end if-else
            }// end if
        }catch(JMSException e){
            log.error("A JMSException exception was caught while trying to acknowledge a ptp message. Message is: " + e.getMessage(), e);

        }catch(Exception e){
            log.error("An Exception exception was caught while trying to acknowledge a ptp message. Message is: " + e.getMessage(), e);

        }finally{
            closeJmsResources(session, connection, consumer, null);
        }// end try-catch-finally
        log.debug("Exiting: " + methodName + " with: " + receiptMessage);
        return receiptMessage;
    }// end receiveAcknowledgement

    public static String getEncryptionKey() {
        return "docsecretpassword";
    }
}
