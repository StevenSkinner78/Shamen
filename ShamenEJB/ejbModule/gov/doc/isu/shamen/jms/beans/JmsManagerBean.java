package gov.doc.isu.shamen.jms.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
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

import org.apache.log4j.Logger;

import gov.doc.isu.shamen.ejb.beans.view.BatchAppBeanLocal;
import gov.doc.isu.shamen.ejb.jms.ShamenJMSException;
import gov.doc.isu.shamen.ejb.util.EJBConstants;
import gov.doc.isu.shamen.ejb.util.ShamenEJBUtil;
import gov.doc.isu.shamen.jms.ControllerMessage;
import gov.doc.isu.shamen.jms.beans.view.JmsManagerBeanLocal;
import gov.doc.isu.shamen.jms.beans.view.JmsManagerBeanRemote;
import gov.doc.isu.shamen.jms.convertor.JmsObjectConvertor;
import gov.doc.isu.shamen.jms.models.JmsController;
import gov.doc.isu.shamen.jms.models.JmsRunStatus;
import gov.doc.isu.shamen.jpa.entity.BatchAppEntity;
import gov.doc.isu.shamen.jpa.entity.CommonEntity;
import gov.doc.isu.shamen.jpa.entity.ControllerEntity;
import gov.doc.isu.shamen.jpa.entity.ResultCodeEntity;
import gov.doc.isu.shamen.jpa.entity.RunStatusEntity;
import gov.doc.isu.shamen.jpa.entity.StatusCodeEntity;

/**
 * This class contains all the necessary methods to receive/send/listen for jms.
 * <p>
 * JmsManagerBean is a Stateless Session Bean implementation class JmsManagerBeanLocal
 *
 * @author <strong>Shane Duncan</strong> JCCC, Aug 17, 2015
 * @author <strong>Steven Skinner</strong> JCCC, Jun 07, 2016
 */
@Stateless
@Local(JmsManagerBeanLocal.class)
@Remote(JmsManagerBeanRemote.class)
public class JmsManagerBean implements JmsManagerBeanLocal, JmsManagerBeanRemote {

    private static final long serialVersionUID = 1L;
    private Logger logger = Logger.getLogger("gov.doc.isu.shamen.jms.beans.JmsManagerBean");

    @EJB
    private BatchAppBeanLocal batchAppBean;

    @Resource(name = "CO_SconT", type = ConnectionFactory.class)
    private ConnectionFactory factory;

    @Resource(name = "CO.Control.TOPIC", type = Destination.class)
    private Destination topicDestination;

    /**
     * This method is called after the bean has been constructed.
     *
     * @throws Exception
     */
    @PostConstruct
    private void initialize() {
        logger.debug("Entering initialize. This method is called after the bean has been constructed.");
        Connection connection = null;

        try{
            logger.debug("Creating connection through ConnectionFactory.");
            connection = createAndStartConnection();
            logger.debug("Starting and closing connection to insure everything is up and running.");
            connection.close();
            logger.debug("Exiting: createAndStartConnection()");
        }catch(Exception e){
            logger.error("Exception occurred initializing JmsMangerBean.", e);
        }// end try-catch

        logger.debug("Exiting initialize.");
    }// end initialize

    /**
     * This method sends a Point to Point message(synchronous) and does not wait on response
     *
     * @param q
     *        Destination(required)
     * @param msg
     *        ControllerMessage(required)
     * @author <strong>Shane Duncan</strong> JCCC, Aug 17, 2015
     */
    public void sendPTP(Destination q, ControllerMessage msg) {
        String methodName = "sendPTP";
        logger.debug("Entering: " + methodName);
        logger.debug("This method sends a Point to Point message(synchronous) and does not wait on response.");
        Session session = null;
        MessageProducer producer = null;
        Connection connection = null;
        try{
            logger.debug("Setting up JMS session.");
            connection = createAndStartConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            logger.debug("Creating JMS producer.");
            producer = session.createProducer(q);
            logger.debug("Creating Object Message");
            ObjectMessage message = session.createObjectMessage(msg);
            message.setJMSCorrelationID(msg.getCorrelationID());
            logger.debug("Sending message: " + msg);
            producer.send(message, DeliveryMode.NON_PERSISTENT, Message.DEFAULT_PRIORITY, 150000);
        }catch(JMSException e){
            logger.error("A JMSException exception was caught while trying to set up or send PTP message. Exception is: " + e.getMessage(), e);
        }finally{
            closeJmsResources(connection, session, null, producer);
        }// end try-catch
        logger.debug("Exiting: " + methodName);
    }// end sendPTP

    /**
     * This method sends a Point to Point message(synchronous) and does not wait on response
     *
     * @param q
     *        Destination(required)
     * @param msg
     *        ControllerMessage(required)
     * @param inMessage
     *        Message(required) It's the message to respond to.
     * @author <strong>Shane Duncan</strong> JCCC, Aug 17, 2015
     */
    public void sendPTPWithHashMap(Destination q, HashMap<String, String> msg, Message inMessage) {
        String methodName = "sendPTPWithHashMap";
        logger.debug("Entering: " + methodName);
        logger.debug("This method sends a Point to Point message(synchronous) and does not wait on response.");
        Session session = null;
        MessageProducer producer = null;
        Connection connection = null;
        try{
            logger.debug("Setting up JMS session.");
            connection = createAndStartConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            logger.debug("Creating JMS producer.");
            producer = session.createProducer(q);
            logger.debug("Creating Object Message");
            ObjectMessage message = session.createObjectMessage(msg);
            message.setJMSCorrelationID(inMessage.getJMSCorrelationID());
            logger.debug("Sending message: " + msg);
            producer.send(message, DeliveryMode.NON_PERSISTENT, Message.DEFAULT_PRIORITY, 150000);
        }catch(JMSException e){
            logger.error("A JMSException exception was caught while trying to set up or send PTP message. Exception is: " + e.getMessage(), e);
        }finally{
            closeJmsResources(connection, session, null, producer);
        }// end try-catch-finally
        logger.debug("Exiting: " + methodName);
    }// end sendPTPWithHashMap

    /**
     * This method sends a Point to Point message(synchronous) and does not wait on response
     *
     * @param q
     *        Destination(required)
     * @param msg
     *        ControllerMessage(required)
     * @param inMessage
     *        Message(required) It's the message to respond to.
     * @author <strong>Shane Duncan</strong> JCCC, Aug 17, 2015
     */
    public void sendPTPWithObjectHashMap(Destination q, HashMap<String, Object> msg, Message inMessage) {
        String methodName = "sendPTPWithHashMap";
        logger.debug("Entering: " + methodName);
        logger.debug("This method sends a Point to Point message(synchronous) and does not wait on response.");
        Session session = null;
        MessageProducer producer = null;
        Connection connection = null;
        try{
            logger.debug("Setting up JMS session.");
            connection = createAndStartConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            logger.debug("Creating JMS producer.");
            producer = session.createProducer(q);
            logger.debug("Creating Object Message");
            ObjectMessage message = session.createObjectMessage(msg);
            message.setJMSCorrelationID(inMessage.getJMSCorrelationID());
            logger.debug("Sending message: " + msg);
            producer.send(message, DeliveryMode.NON_PERSISTENT, Message.DEFAULT_PRIORITY, 150000);
        }catch(JMSException e){
            logger.error("A JMSException exception was caught while trying to set up or send PTP message. Exception is: " + e.getMessage(), e);
        }finally{
            closeJmsResources(connection, session, null, producer);
        }// end try-catch-finally
        logger.debug("Exiting: " + methodName);
    }// end sendPTPWithHashMap

    /**
     * This method acknowledges a previously sent PTP message. All information for acknowledgement must be in the message.
     *
     * @param message
     *        (required)
     * @param outgoingMessage
     *        outgoingMessage
     * @author <strong>Shane Duncan</strong> JCCC, Aug 17, 2015
     */
    public void acknowlegePTPMessage(Message message, ControllerMessage outgoingMessage) {

        logger.debug("Entering JmsManager.acknowlegePTPMessage with " + new Object[]{message, outgoingMessage});
        logger.debug("This method acknowledges a previously sent PTP message.  All information for acknowledgement must be in the message.");
        if(message instanceof ObjectMessage){
            logger.debug("Attempting to acknowledge message.");
            Object obj;
            try{
                obj = ((ObjectMessage) message).getObject();
                if(obj instanceof ControllerMessage){
                    outgoingMessage.setCorrelationID(message.getJMSCorrelationID());
                    logger.debug("Acknowledging message.");
                    sendPTP(message.getJMSReplyTo(), outgoingMessage);
                }// end if
            }catch(JMSException e){
                logger.error("A JMSException exception was caught while trying to acknowledge PTP message. Exception is: " + e.getMessage(), e);

            }// end try-catch
        }// end if
        logger.debug("Exiting JmsManager.acknowlegePTPMessage");
    }// end acknowlegePTPMessage

    /**
     * This method acknowledges a previously sent PTP message. All information for acknowledgement must be in the message.
     *
     * @param message
     *        (required)
     * @param outgoingMessage
     *        outgoingMessage
     * @author <strong>Shane Duncan</strong> JCCC, Aug 17, 2015
     */
    public void acknowlegePTPMessageWithHashMap(Message message, HashMap<String, String> outgoingMessage) {

        logger.debug("Entering JmsManager.acknowlegePTPMessageWithHashMap with " + new Object[]{message, outgoingMessage});
        logger.debug("This method acknowledges a previously sent PTP message.  All information for acknowledgement must be in the message.");
        if(message instanceof ObjectMessage){
            logger.debug("Attempting to acknowledge message.");
            Object obj;
            try{
                obj = ((ObjectMessage) message).getObject();
                if(obj instanceof HashMap){
                    logger.debug("Acknowledging message.");
                    sendPTPWithHashMap(message.getJMSReplyTo(), outgoingMessage, message);
                }// end if
            }catch(JMSException e){
                logger.error("A JMSException exception was caught while trying to acknowledge PTP message. Exception is: " + e.getMessage(), e);

            }// end try-catch
        }// end if
        logger.debug("Exiting JmsManager.acknowlegePTPMessageWithHashMap");
    }// end acknowlegePTPMessage

    /**
     * This method acknowledges a previously sent PTP message. All information for acknowledgement must be in the message.
     *
     * @param message
     *        (required)
     * @param outgoingMessage
     *        outgoingMessage
     * @author <strong>Shane Duncan</strong> JCCC, Aug 17, 2015
     */
    public void acknowlegePTPMessageWithObjectHashMap(Message message, HashMap<String, Object> outgoingMessage) {

        logger.debug("Entering JmsManager.acknowlegePTPMessageWithHashMap with " + new Object[]{message, outgoingMessage});
        logger.debug("This method acknowledges a previously sent PTP message.  All information for acknowledgement must be in the message.");
        if(message instanceof ObjectMessage){
            logger.debug("Attempting to acknowledge message.");
            Object obj;
            try{
                obj = ((ObjectMessage) message).getObject();
                if(obj instanceof HashMap){
                    logger.debug("Acknowledging message.");
                    sendPTPWithObjectHashMap(message.getJMSReplyTo(), outgoingMessage, message);
                }// end if
            }catch(JMSException e){
                logger.error("A JMSException exception was caught while trying to acknowledge PTP message. Exception is: " + e.getMessage(), e);

            }// end try-catch
        }// end if
        logger.debug("Exiting JmsManager.acknowlegePTPMessageWithHashMap");
    }// end acknowlegePTPMessage

    /**
     * Receive a HashMap message from a destination. This is done only one time as it is assumed that it will be using a temporary queue only.
     *
     * @author <strong>Shane Duncan</strong> JCCC, Aug 17, 2015
     * @param q
     *        (required)
     * @return Message
     * @throws ShamenJMSException
     *         if an exception occurred
     */
    @SuppressWarnings("unchecked")
    public HashMap<String, String> receiveFromDestination(Destination q) throws ShamenJMSException {
        String methodName = "receiveFromDestination";
        logger.debug("Entering: " + methodName);
        logger.debug("Receive a HashMap message from a destination.  This is done only one time as it is assumed that it will be using a temporary queue only.");
        Connection connection = null;
        HashMap<String, String> messageMap = null;
        Session session = null;
        MessageConsumer consumer = null;
        try{
            connection = createAndStartConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            consumer = session.createConsumer(q);
            // connection.start();
            Message message = consumer.receive(15000);
            if(message instanceof ObjectMessage){
                Object obj = null;
                obj = ((ObjectMessage) message).getObject();
                if(obj instanceof HashMap){
                    messageMap = (HashMap<String, String>) obj;
                    logger.debug("*******************Received message** from destination: " + String.valueOf(q));
                    Set<String> keys = messageMap.keySet();
                    for(Iterator<String> iterator = keys.iterator();iterator.hasNext();){
                        String key = (String) iterator.next();
                        logger.debug("Message key " + key + " contains value: " + messageMap.get(key));
                    }// end for
                }// end if
            }// end if
        }catch(Exception e){
            throw new ShamenJMSException("Exception occurred receivinf message from destination.", e);
        }finally{
            closeJmsResources(connection, session, consumer, null);
        }// end try-catch-finally
        logger.debug("Exiting: " + methodName + " with: " + messageMap);
        return messageMap;
    }// end receiveFromDestination

    /**
     * This method will start a batch application
     *
     * @param runStatus
     *        (required) This holds the create user ref id of the run.
     * @param batchTypeCd
     *        batch Type code
     * @param userId
     *        the users id
     * @param jobParameters
     *        job parameters if passed in
     * @author <strong>Shane Duncan</strong> JCCC, Sep 23, 2015
     * @throws ShamenJMSException
     *         if an exception occurred
     */
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public void startBatchApp(RunStatusEntity runStatus, String batchTypeCd, String userId, String jobParameters) throws ShamenJMSException {
        String methodName = "startBatchApp";
        logger.debug("Entering: " + methodName);
        logger.debug("This method will start a batch application.");

        ControllerEntity controller = runStatus.getBatchApp().getController();
        ControllerMessage msg = new ControllerMessage();
        // if the userId was sent in, then set it in misc paramters.
        if(userId != null){
            msg.setMiscParameters(new ArrayList<String>());
            msg.getMiscParameters().add(userId);
        }// end if
        msg.setJobParameters(jobParameters);
        msg.setText("RUN BAT");
        try{
            msg.setRunStatus(JmsObjectConvertor.toJms(runStatus));
            msg.setCollectionIndicator((batchTypeCd == null ? false : (batchTypeCd.equals(EJBConstants.BATCH_APP_TYPE_CD_COLLECTION) ? true : false)));
        }catch(Exception e){
            logger.error("Exception occurred while converting a runStatus to JmsRunStatus. Exception is: " + e.getMessage(), e);
            throw new ShamenJMSException("Exception occurred while converting a runStatus to JmsRunStatus.", e);
        }
        msg.setBatchAppRefId(runStatus.getBatchApp().getBatchAppRefId());
        msg.setCorrelationID(null);
        publishMessageToController(msg, controller.getControllerAddress());
        logger.debug("Exiting: " + methodName);
    }

    /**
     * This method will start a batch application from remote ejb call.
     *
     * @param batchRefId
     *        (required) This holds the batch ref id.
     * @param userID
     *        the users id
     * @param jobParameters
     *        job parameters if passed in
     * @author <strong>Steven Skinner</strong> JCCC, Oct 10, 2019
     * @throws Exception
     *         if an exception occurred
     */
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public void startBatchApp(Long batchRefId, String userID, String jobParameters) throws Exception {
        String methodName = "startBatchApp";
        logger.debug("Entering: " + methodName);
        logger.debug("This method will start a batch application from remote ejb call.");

        BatchAppEntity entity = batchAppBean.findBatchByRefId(batchRefId);
        String runNumberString = String.valueOf(System.currentTimeMillis());
        RunStatusEntity runStatusEntity = new RunStatusEntity();
        runStatusEntity.setRunNumber(Long.valueOf(runNumberString.substring(8)));
        runStatusEntity.setStatus(new StatusCodeEntity(JmsRunStatus.STATUS_STARTING, ""));
        runStatusEntity.setResult(new ResultCodeEntity(JmsRunStatus.RESULTS_PENDING, ""));
        runStatusEntity.setStartTime(ShamenEJBUtil.getCurrentTimestamp());
        runStatusEntity.setStopTime(ShamenEJBUtil.getDefaultTimeStamp());
        runStatusEntity.setCommon(new CommonEntity());
        runStatusEntity.getCommon().setCreateTime(runStatusEntity.getStartTime());
        runStatusEntity.getCommon().setCreateUserRefId(Long.valueOf(EJBConstants.CLIENT_APP_USER_REF_ID));
        runStatusEntity.getCommon().setUpdateUserRefId(Long.valueOf(EJBConstants.CLIENT_APP_USER_REF_ID));
        runStatusEntity.getCommon().setUpdateTime(ShamenEJBUtil.getDefaultTimeStamp());
        runStatusEntity.getCommon().setDeleteIndicator("N");
        runStatusEntity.setBatchApp(entity);
        startBatchApp(runStatusEntity, entity.getBatchType().getBatchTypeCd(), userID, jobParameters);

        logger.debug("Exiting: " + methodName);
    }

    /**
     * This method is to verify that a remote EJB call is available.
     * 
     * @return Boolean
     * @throws Exception
     *         if an exception occurred
     */
    public Boolean testEJB() throws Exception {
        String methodName = "startBatchApp";
        logger.debug("Entering: " + methodName);
        logger.debug("This method is to verify that a remote EJB call is available.");
        Boolean result = true;
        logger.debug("Exiting: " + methodName);
        return result;
    }

    /**
     * This method will publish a message that tells all controllers to update their schedules.
     *
     * @author <strong>Shane Duncan</strong> JCCC, Sep 23, 2015
     * @throws ShamenJMSException
     *         if an exception occurred
     */
    public void updateAllSchedules() throws ShamenJMSException {
        String methodName = "updateAllSchedules";
        logger.debug("Entering: " + methodName);
        logger.debug("This method will publish a message that tells all controllers to update their schedules. ");

        ControllerMessage msg = new ControllerMessage();

        msg.setText(ControllerMessage.REFRESH_SCHEDULE);
        msg.setCorrelationID(null);
        ControllerEntity controllerModel = new ControllerEntity();
        controllerModel.setControllerAddress("ALL");
        publishMessageToController(msg, controllerModel.getControllerAddress());
        logger.debug("Exiting: " + methodName);
    }

    /**
     * This method will publish a message that tells a controller to update its schedules.
     *
     * @param controller
     *        (required)
     * @author <strong>Shane Duncan</strong> JCCC, Sep 23, 2015
     * @throws ShamenJMSException
     *         if an exception occurred
     */
    public void updateControllerSchedules(ControllerEntity controller) throws ShamenJMSException {
        String methodName = "updateControllerSchedules";
        logger.debug("Entering: " + methodName);
        logger.debug("This method will publish a message that tells all controllers to update their schedules. ");

        ControllerMessage msg = new ControllerMessage();
        if(controller == null){
            controller = new ControllerEntity();
            controller.setControllerAddress("ALL");
        }
        msg.setController(JmsObjectConvertor.toJms(controller));
        msg.setText(ControllerMessage.REFRESH_SCHEDULE);
        msg.setCorrelationID(null);

        publishMessageToController(msg, controller.getControllerAddress());
        logger.debug("Exiting: " + methodName);
    }

    /**
     * This method will publish a message provides a controller with the data it needs to refresh itself.
     *
     * @param controller
     *        (required)
     * @author <strong>Shane Duncan</strong> JCCC, Sep 23, 2015
     * @throws ShamenJMSException
     *         if an exception occurred
     */
    public void resetControllerSchedules(JmsController controller) throws ShamenJMSException {
        String methodName = "resetControllerSchedules";
        logger.debug("Entering: " + methodName);
        logger.debug("This method will publish a message provides a controller with the data it needs to refresh itself.");

        ControllerMessage msg = new ControllerMessage();
        msg.setController(controller);
        msg.setText(ControllerMessage.REFRESH_SCHEDULE);
        msg.setCorrelationID(null);

        publishMessageToController(msg, controller.getControllerAddress());
        logger.debug("Exiting: " + methodName);
    }

    /**
     * This method will publish a message with the proper selector so that the correct controller will receive it.
     *
     * @param msg
     *        ControllerMessage
     * @param controllerAddress
     *        address
     * @author <strong>Shane Duncan</strong> JCCC, Sep 23, 2015
     * @throws ShamenJMSException
     *         if an exception occurred
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void publishMessageToController(ControllerMessage msg, String controllerAddress) throws ShamenJMSException {
        String methodName = "publishMessage";
        logger.debug("Entering: " + methodName);
        logger.debug("This method will publish a message with the proper selector so that the correct controller will receive it.");
        Session session = null;
        MessageProducer producer = null;
        Connection connection = null;
        try{
            connection = createAndStartConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            logger.debug("Creating JMS producer.");
            producer = session.createProducer(topicDestination);
            logger.debug("Creating Object Message");
            msg.setSelectorName(EJBConstants.MESSAGE_SELECTOR);
            msg.setSelector(controllerAddress.toUpperCase());
            ObjectMessage message = session.createObjectMessage(msg);
            message.setStringProperty(EJBConstants.MESSAGE_SELECTOR, controllerAddress.toUpperCase());
            logger.debug("Sending message: " + msg);
            producer.send(message, DeliveryMode.PERSISTENT, Message.DEFAULT_PRIORITY, 0);
            logger.debug("Message was successfully sent.");
        }catch(Exception e){
            throw new ShamenJMSException("Exception caught trying to publish message to controller.  Exception is: " + e.getMessage(), e);
        }finally{
            closeJmsResources(connection, session, null, producer);
        }// end try-catch-finally
        logger.debug("Exiting: " + methodName);
    }

    /**
     * This method receives Point to Point message(synchronous) with a forced acknowledgment. This is only for use with Temporary queues
     *
     * @param q
     *        queue name
     * @param correlationId
     *        correlationId to
     * @return message
     * @author <strong>Shane Duncan</strong> JCCC, Aug 17, 2015
     */
    public Message receiveAcknowledgement(Queue q, String correlationId) {
        String methodName = "receiveAcknowledgement";
        logger.debug("Entering: " + methodName);
        logger.debug("This method receives Point to Point message(synchronous) with a forced acknowledgment. This is only for use with Temporary queues.");
        Session session = null;
        MessageConsumer consumer = null;
        Message text = null;
        Connection connection = null;
        try{
            connection = createAndStartConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            consumer = session.createConsumer(q);
            Message message = consumer.receive(10000);
            if(message instanceof ObjectMessage){
                Object obj = null;
                obj = ((ObjectMessage) message).getObject();
                if(obj instanceof HashMap){
                    text = message;
                    logger.debug("Received message:" + obj);
                    if(correlationId.equals(message.getJMSCorrelationID())){
                        logger.debug("Acknowledging message");
                    }// end if
                }// end if
            }// end if

        }catch(JMSException e){
            logger.error("Exception occurred while acknowledging message.  Exception is: " + e.getMessage(), e);
        }finally{
            closeJmsResources(connection, session, consumer, null);
        }// end try-catch-finally
        logger.debug("Exiting: " + methodName + " with " + text);
        return text;
    }// end receiveAcknowledgement

    /**
     * This method will close the jms resources that are sent to it. It has full null checks.
     * 
     * @param connection
     *        connection to be closed
     * @param ses
     *        session to be closed
     * @param consumer
     *        consumer to be closed
     * @param producer
     *        producer to be closed
     */
    public void closeJmsResources(Connection connection, Session ses, MessageConsumer consumer, MessageProducer producer) {
        logger.debug("Entering: closeJmsResources with session, connection, consumer, producer: " + new Object[]{ses, consumer, producer});
        logger.debug("This method will close the jms resources that are sent to it.  It has full null checks.");
        try{
            logger.debug("Close the JMS resources.");
            if(consumer != null){
                consumer.close();
            }// end if
            if(producer != null){
                producer.close();
            }// end if
            if(ses != null){
                ses.close();
            }// end if
            if(connection != null){
                connection.close();
            }// end if
        }catch(JMSException e){
            logger.error("Exception occurred trying to close a resource. Exception is: " + e.getMessage(), e);
        }// end try-catch
        logger.debug("Exiting: closeJmsResources");
    }// end closeJmsResources

    /**
     * This method creates and starts a jms connection
     * 
     * @return connection
     * @throws JMSException
     *         if an exception occurred
     */
    private Connection createAndStartConnection() throws JMSException {
        logger.debug("Entering: createAndStartConnection");
        logger.debug("This method creates and starts a jms connection");
        Connection connection = null;
        try{
            logger.debug("Create and start the connection.");
            connection = factory.createConnection();
            connection.start();
        }catch(JMSException e){
            logger.error("Exception occurred trying to create and start a connection. Exception is: " + e.getMessage(), e);
            throw (e);
        }// end try-catch
        logger.debug("Exiting: closeJmsResources with connection = " + String.valueOf(connection));
        return connection;
    }// end createAndStartConnection

}
