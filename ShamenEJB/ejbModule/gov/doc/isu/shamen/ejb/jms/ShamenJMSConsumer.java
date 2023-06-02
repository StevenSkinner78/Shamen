/**
 *
 */
package gov.doc.isu.shamen.ejb.jms;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;

import gov.doc.isu.shamen.ejb.beans.view.BatchAppBeanLocal;
import gov.doc.isu.shamen.ejb.beans.view.ControllerBeanLocal;
import gov.doc.isu.shamen.ejb.beans.view.ControllerStatusUpdaterBeanLocal;
import gov.doc.isu.shamen.jms.ControllerMessage;
import gov.doc.isu.shamen.jms.beans.view.JmsManagerBeanLocal;
import gov.doc.isu.shamen.jms.convertor.JmsObjectConvertor;
import gov.doc.isu.shamen.jms.models.JmsRunStatus;
import gov.doc.isu.shamen.jpa.entity.ControllerEntity;
import gov.doc.isu.shamen.jpa.entity.RunStatusEntity;

/**
 * This class used to execute the logic of a message driven consumer bean.
 *
 * @author Shane Duncan JCCC
 */

@MessageDriven(activationConfig = {@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"), @ActivationConfigProperty(propertyName = "destination", propertyValue = "CO.Shamen.REQST"), @ActivationConfigProperty(propertyName = "useJNDI", propertyValue = "true")})
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class ShamenJMSConsumer implements MessageListener {

    private Logger logger = Logger.getLogger("gov.doc.isu.shamen.ejb.jms.ShamenJMSConsumer");

    @EJB
    private ControllerBeanLocal controllerBean;

    private static volatile AtomicBoolean keepGoingCont = new AtomicBoolean();
    @EJB
    private BatchAppBeanLocal bean;
    @EJB
    private JmsManagerBeanLocal jmsManagerBean;

    /**
     * This default constructor must be used to instantiate this class.
     */
    public ShamenJMSConsumer() {
        super();
    }// end constructor

    /**
     * This method is used to launch both the JMS producer and consumer message driven beans.
     * <p>
     * These beans are scheduled to run concurrently and are used to activate the data migration logic for the application.
     *
     * @param message
     *        The message received from the queue.
     */
    @Override
    public void onMessage(Message message) {
        logger.debug("Entering onMessage.");
        logger.debug("This method is used to launch both the JMS producer and consumer message driven beans.");
        logger.debug("***********Received a message**************** On CO.Shamen.REQST");

        try{
            message.acknowledge();

            if(message instanceof ObjectMessage && !message.getJMSRedelivered()){
                Object obj;
                obj = ((ObjectMessage) message).getObject();
                if(obj instanceof ControllerMessage){
                    keepGoingCont.set(true);
                    while(keepGoingCont.get()){
                        logger.debug("Message is a controller message.");
                        processControllerMessage((ControllerMessage) obj, message);
                        keepGoingCont.set(false);
                    }// end while
                }// end if
            }// end if
        }catch(JMSException e){
            logger.error("JMSException occurred while receiving a PTP message. Exception is: " + e.getMessage(), e);
        }catch(Exception e){
            logger.error("Exception occurred while receiving a PTP message. Exception is: " + e.getMessage(), e);
        }// end try/catch
        logger.debug("Exiting onMessage");
    }// end onMessage

    /**
     * This method does all the processing for controller messages.
     *
     * @param controllerMessage
     *        message to process
     * @param message
     *        message
     */
    private void processControllerMessage(ControllerMessage controllerMessage, Message message) {
        logger.debug("Entering processControllerMessage.");
        logger.debug("Parameters are: controllerMessage: " + String.valueOf(controllerMessage) + ", message: " + String.valueOf(message));
        logger.debug("This method does all the processing for controller messages.");
        Boolean replyTo = false;
        try{
            if(controllerMessage != null){
                if(message.getJMSReplyTo() != null || controllerMessage.getReplyTo()){
                    replyTo = true;
                }// end if
                if(ControllerMessage.GIVE_ME_CONTROLLER.equals(controllerMessage.getText())){
                    giveMeController(controllerMessage, message);
                }else if(ControllerMessage.ARE_YOU_ALIVE.equals(controllerMessage.getText())){
                    areYouAlive(controllerMessage, message);
                }else if(ControllerMessage.UPDATE_RUN_STATUS.equals(controllerMessage.getText())){
                    logger.debug("UPDATE_RUN_STATUS requested.");
                    boolean result = updateRunStatus(controllerMessage);
                    if(replyTo && result){
                        logger.debug("Reply message detected.  This message requires confirmation.");
                        replyToMessage(controllerMessage);
                    }// end if
                }// end if
            }// end if
        }catch(JMSException e){
            logger.error("JMSException occurred while processing a PTP message in processController. Exception is: " + e.getMessage(), e);
        }catch(Exception e){
            logger.error("Exception occurred while processing a PTP message in processController. Exception is: " + e.getMessage(), e);
        }// end try-catch
        logger.debug("Exiting processControllerMessage");
    }// end processController

    /**
     * This method processes the 'GIVE_ME_CONTROLLER' message which is a message from the ShamenController requesting a fresh Controller record from the DB.
     *
     * @param controllerMessage
     *        controllerMessage
     * @param message
     *        message
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    private void giveMeController(ControllerMessage controllerMessage, Message message) throws Exception {
        logger.debug("Entering giveMeController.");
        logger.debug("Parameters are: controllerMessage: " + String.valueOf(controllerMessage) + ", message: " + String.valueOf(message));
        logger.debug(" This method processes the 'GIVE_ME_CONTROLLER' message which is a message from the ShamenController requesting a fresh Controller record from the DB.");

        if(controllerMessage.getController() != null){
            logger.info("GIVE_ME_CONTROLLER message request for Controller: " + String.valueOf(controllerMessage.getController()));
            ControllerEntity controller = null;
            try{
                controller = controllerBean.findControllerAddress(controllerMessage.getController().getControllerAddress());
            }catch(Exception e){
                logger.error("Exception occurred while retrieving the controller record for address: " + controllerMessage.getController().getControllerAddress() + ". Exception is: " + e.getMessage(), e);
                controller = null;
            }// end try/catch
            ControllerMessage outgoingMsg = new ControllerMessage();
            outgoingMsg.setController(JmsObjectConvertor.toJms(controller));
            if(controller == null){
                outgoingMsg.setText(ControllerMessage.NO_SUCH_RECORD);
            }else{
                outgoingMsg.setText(ControllerMessage.RECEIVED);
                // if(message.getJMSTimestamp() > controllerStatusBean.getStartTime()){
                controllerBean.setSingleControllerStatus(controller.getControllerRefId(), ControllerStatusUpdaterBeanLocal.CONNECTED);
                // }// end if
            }// end if/else
            logger.debug("Acknowledge the message");
            jmsManagerBean.acknowlegePTPMessage(message, outgoingMsg);
        }// end if
        logger.debug("Exiting giveMeController");
    }// end giveMeController

    /**
     * This method processes the 'ARE_YOU_ALIVE' message which is a message from the ShamenController requesting whether or not ShamenWeb is up and running.
     *
     * @param controllerMessage
     *        controllerMessage
     * @param message
     *        message
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    private void areYouAlive(ControllerMessage controllerMessage, Message message) throws Exception {
        logger.debug("Entering areYouAlive.");
        logger.debug("Parameters are: controllerMessage: " + String.valueOf(controllerMessage) + ", message: " + String.valueOf(message));
        logger.debug("This method processes the 'ARE_YOU_ALIVE' message which is a message from the ShamenController requesting whether or not ShamenWeb is up and running.");

        if(controllerMessage.getController() != null){
            logger.info("ARE_YOU_ALIVE message request for Controller: " + controllerMessage.getController().getControllerAddress());
            logger.debug("Get the controller record");
            ControllerEntity controller = null;
            try{
                controller = controllerBean.findControllerAddress(controllerMessage.getController().getControllerAddress());
            }catch(Exception e){
                logger.error("Exception occurred while retrieving the controller record for address: " + controllerMessage.getController().getControllerAddress() + ". Exception is: " + e.getMessage(), e);
                controller = null;
            }// end try/catch
            ControllerMessage outgoingMsg = new ControllerMessage();
            outgoingMsg.setController(JmsObjectConvertor.toJms(controller));
            outgoingMsg.setText(controller == null ? ControllerMessage.NO_SUCH_RECORD : ControllerMessage.RECEIVED);
            jmsManagerBean.acknowlegePTPMessage(message, outgoingMsg);
        }// end if
        logger.debug("Exiting areYouAlive");
    }// end areYouAlive

    /**
     * This method sends a response back to the controller, notifying of receipt and process of the message.
     *
     * @author <strong>Shane Duncan</strong> JCCC, Oct 27, 2015
     * @param msg
     *        ControllerMessage
     */
    private void replyToMessage(ControllerMessage msg) {
        logger.debug("Entering replyToMessage  Entered with message: " + msg);
        logger.debug("This method sends a response back to the controller, notifying of receipt and process of the message.");
        // JmsManager jms;
        try{
            msg.setText(ControllerMessage.RECEIVED);
            // jms = JmsManager.getInstance();
            logger.debug("Publish the receipt message.");
            jmsManagerBean.publishMessageToController(msg, msg.getController().getControllerAddress());
        }catch(ShamenJMSException e){
            logger.error("Exception occured trying to reply to a message. Exception is: " + e.getMessage(), e);
        }// end if
        logger.debug("Exiting replyToMessage");
    }// end replyToMessage

    /**
     * This method pulls the runStatus out of an incoming message and saves it to the Database.
     *
     * @author <strong>Shane Duncan</strong> JCCC, Oct 27, 2015
     * @param msg
     *        ControllerMessage
     * @return boolean
     */
    private boolean updateRunStatus(ControllerMessage msg) {
        logger.debug("Entering updateRunStatus -- Entered with message: " + msg);
        logger.debug("This method pulls the runStatus out of an incoming message and saves it to the Database.");
        JmsRunStatus rs = msg.getRunStatus();
        RunStatusEntity entity = null;
        boolean result = true;
        if(rs != null){
            logger.debug("Save the runStatus.");
            try{
                entity = JmsObjectConvertor.toShamen(rs);
                bean.mergeRunStatusFromMessage(entity);
            }catch(Exception e){
                logger.error("Error occurred trying to write the RunStatus record. RunStatus is: " + String.valueOf(entity) + ". Exception is: " + e.getMessage(), e);
                result = false;
            }
        }// end if
        logger.debug("Exiting updateRunStatus");
        return result;
    }// end updateRunStatus

}// end class
