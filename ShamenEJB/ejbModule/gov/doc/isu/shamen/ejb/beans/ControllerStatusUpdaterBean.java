/**
 *
 */
package gov.doc.isu.shamen.ejb.beans;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TemporaryQueue;

import org.apache.log4j.Logger;

import gov.doc.isu.shamen.ejb.beans.view.ControllerBeanLocal;
import gov.doc.isu.shamen.ejb.beans.view.ControllerStatusUpdaterBeanLocal;
import gov.doc.isu.shamen.ejb.util.EJBConstants;
import gov.doc.isu.shamen.jms.ControllerMessage;
import gov.doc.isu.shamen.jms.convertor.JmsObjectConvertor;
import gov.doc.isu.shamen.jms.models.JmsController;
import gov.doc.isu.shamen.jpa.entity.ControllerEntity;

/**
 * This class is used as the muscle to request status updates from Controllers. It is meant to run asynchronously.
 *
 * @author sbd000is
 */
@Stateless
@Local(ControllerStatusUpdaterBeanLocal.class)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class ControllerStatusUpdaterBean implements ControllerStatusUpdaterBeanLocal {

    private static final long serialVersionUID = -7759398261754327450L;
    private Logger logger = Logger.getLogger("gov.doc.isu.shamen.ejb.beans.ControllerStatusUpdaterBean");

    @Resource(name = "CO_SconT", type = ConnectionFactory.class)
    private ConnectionFactory factory;

    @Resource(name = "CO.Control.TOPIC", type = Destination.class)
    private Destination destination;

    @EJB
    private ControllerBeanLocal controllerBean;

    /**
     * {@inheritDoc}
     */
    @Override
    @javax.ejb.Asynchronous
    public void establishControllerStatus(ControllerEntity controller) {
        logger.debug("Entering: establishAllControllerStatuses");
        logger.debug("This method will publish a message to a controller to check for a response. If a response returns then the controller is deemed connected.");
        MessageConsumer consumer = null;
        Message inMessage = null;
        Session session = null;
        MessageProducer producer = null;
        Connection connection = null;
        // setSingleControllerStatus(controller, AWAITING_RESPONSE);
        try{
            connection = factory.createConnection();
            logger.debug("Create the JMS Session.");
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            logger.debug("Create the JMS Producer.");
            producer = session.createProducer(destination);
            producer.setTimeToLive(5000);
            logger.debug("Create the temporary queue to be used in the response.");
            TemporaryQueue tempQ = session.createTemporaryQueue();
            logger.debug("Create the JMS Consumer.");
            consumer = session.createConsumer(tempQ);
            connection.start();
            String correlationId = String.valueOf(System.currentTimeMillis());
            logger.debug("Create the message");
            ControllerMessage msg = new ControllerMessage();
            msg.setController(JmsObjectConvertor.toJms(controller));
            msg.setSelectorName(EJBConstants.MESSAGE_SELECTOR);
            msg.setSelector(controller.getControllerAddress().toUpperCase());
            msg.setCorrelationID(null);
            msg.setText(ControllerMessage.ARE_YOU_ALIVE);
            ObjectMessage message = session.createObjectMessage(msg);
            message.setStringProperty(EJBConstants.MESSAGE_SELECTOR, controller.getControllerAddress().toUpperCase());
            message.setJMSReplyTo(tempQ);
            message.setJMSCorrelationID(correlationId);
            logger.debug("Sending message: " + String.valueOf(message));
            producer.send(message);
            inMessage = consumer.receive(5000);
            if(inMessage instanceof ObjectMessage){
                Object obj = null;
                try{
                    obj = ((ObjectMessage) message).getObject();
                    logger.debug("Insure that this message is a ControllerMessage, if not ignore it.");
                    if(obj instanceof ControllerMessage){
                        setSingleControllerStatus(controller, CONNECTED);
                    }
                }catch(JMSException e){
                    logger.error("A JMS exception has been caught while trying to cast a published message to an ObjectMessage. Message is: " + e.getMessage());
                }// end try-catch
            }else{
                setSingleControllerStatus(controller, null);
            }// end if/else

        }catch(JMSException e){
            logger.error("JMSException occurred while establish statuses of all controllers.  Exception is: " + e.getMessage());
        }catch(Exception e){
            logger.error("Exception occurred while establish statuses of all controllers.  Exception is: " + e.getMessage());
        }finally{
            closeJmsResources(connection, session, consumer, producer);
        }// end try-catch-finally
        logger.debug("Exiting: establishAllControllerStatuses");
    }// end isControllerConnected

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSingleControllerStatus(ControllerEntity controller, String status) {
        logger.debug("Entering setSingleControllerStatus.");
        logger.debug("Parameter are: controller: " + String.valueOf(controller));
        logger.debug("This method sets the the status for a single controller and loads it into the controller map.");
        logger.debug("Set the status for controller: " + String.valueOf(controller));
        if(status == null){
            logger.debug("Status came in as null, so defaulting it to Unresponsive.");
            status = UNRESPONSIVE;
        }// end if
        JmsController app = JmsObjectConvertor.toJms(controller);
        app.setStatus(status);
        logger.debug("Put the controller in the controllers HashMap.");
        try{
            controllerBean.setSingleControllerStatus(controller.getControllerRefId(), status);
        }catch(Exception e){
            logger.error("Exception occurred updating controller status.  Controller is: " + String.valueOf(controller), e);
        }
        logger.debug("Exiting setSingleControllerStatus");
    }// end setSingleControllerStatus

    /**
     * This method closes the connection, session, consumer, and producer that were used to establish a controller's status.
     *
     * @param connection
     *        Connection
     * @param session
     *        Session
     * @param consumer
     *        MessageConsumer
     * @param producer
     *        MessageProducer
     */
    private void closeJmsResources(Connection connection, Session session, MessageConsumer consumer, MessageProducer producer) {
        logger.debug("Entering: closeJmsResources()");
        logger.debug("This method closes the connection, session, consumer, and producer that were used to establish a controller's status.");
        try{
            if(producer != null){
                logger.debug("Closing producer.");
                producer.close();
            }// end if
            if(consumer != null){
                logger.debug("Closing consumer.");
                consumer.close();
            }// end if
            if(session != null){
                logger.debug("Closing session.");
                connection.close();
            }// end if
            if(connection != null){
                logger.debug("Closing connection.");
                connection.close();
            }// end if
        }catch(JMSException e){
            logger.error("Exception occurred while trying to close JMS resources.  Error is: " + e);
        }finally{
            producer = null;
            consumer = null;
            session = null;
            connection = null;

        }// end try/catch
        logger.debug("Exiting: closeJmsResources()");
    }// end closeConnection
}// end class
