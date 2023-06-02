package gov.doc.isu.shamen.jms.beans.view;

import java.io.Serializable;
import java.util.HashMap;

import javax.ejb.Local;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.Queue;

import gov.doc.isu.shamen.ejb.jms.ShamenJMSException;
import gov.doc.isu.shamen.jms.ControllerMessage;
import gov.doc.isu.shamen.jms.models.JmsController;
import gov.doc.isu.shamen.jpa.entity.ControllerEntity;
import gov.doc.isu.shamen.jpa.entity.RunStatusEntity;

/**
 * This is the local business interface for the {@link gov.doc.isu.shamen.jms.beans.JmsManagerBean}.
 * 
 * @author Steven Skinner JCCC
 */
@Local
public interface JmsManagerBeanLocal extends Serializable {

    /**
     * This method sends a Point to Point message(synchronous) and does not wait on response
     * 
     * @param q
     *        Destination(required)
     * @param msg
     *        ControllerMessage(required)
     * @author <strong>Shane Duncan</strong> JCCC, Aug 17, 2015
     */
    public void sendPTP(Destination q, ControllerMessage msg);

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
    public void sendPTPWithHashMap(Destination q, HashMap<String, String> msg, Message inMessage);

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
    public void sendPTPWithObjectHashMap(Destination q, HashMap<String, Object> msg, Message inMessage);

    /**
     * This method acknowledges a previously sent PTP message. All information for acknowledgement must be in the message.
     * 
     * @param message
     *        (required)
     * @param outgoingMessage
     *        outgoingMessage
     * @author <strong>Shane Duncan</strong> JCCC, Aug 17, 2015
     */
    public void acknowlegePTPMessage(Message message, ControllerMessage outgoingMessage);

    /**
     * This method acknowledges a previously sent PTP message. All information for acknowledgement must be in the message.
     * 
     * @param message
     *        (required)
     * @param outgoingMessage
     *        outgoingMessage
     * @author <strong>Shane Duncan</strong> JCCC, Aug 17, 2015
     */
    public void acknowlegePTPMessageWithHashMap(Message message, HashMap<String, String> outgoingMessage);

    /**
     * This method acknowledges a previously sent PTP message. All information for acknowledgement must be in the message.
     * 
     * @param message
     *        (required)
     * @param outgoingMessage
     *        outgoingMessage
     * @author <strong>Shane Duncan</strong> JCCC, Aug 17, 2015
     */
    public void acknowlegePTPMessageWithObjectHashMap(Message message, HashMap<String, Object> outgoingMessage);

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
    public HashMap<String, String> receiveFromDestination(Destination q) throws ShamenJMSException;

    /**
     * This method will start a batch application
     * 
     * @param runStatus
     *        (required) This holds the create user ref id of the run.
     * @param batchTypeCd
     *        batch type code
     * @param userId
     *        client user that kicked off job(not required)
     * @param jobParameters
     *        job parameters if passed in
     * @author <strong>Shane Duncan</strong> JCCC, Sep 23, 2015
     * @throws ShamenJMSException
     *         if an exception occurred
     */
    public void startBatchApp(RunStatusEntity runStatus, String batchTypeCd, String userId, String jobParameters) throws ShamenJMSException;

    /**
     * This method will publish a message that tells all controllers to update their schedules.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 23, 2015
     * @throws ShamenJMSException
     *         if an exception occurred
     */
    public void updateAllSchedules() throws ShamenJMSException;

    /**
     * This method will publish a message that tells a controller to update its schedules.
     * 
     * @param controller
     *        (required)
     * @author <strong>Shane Duncan</strong> JCCC, Sep 23, 2015
     * @throws ShamenJMSException
     *         if an exception occurred
     */
    public void updateControllerSchedules(ControllerEntity controller) throws ShamenJMSException;

    /**
     * This method will publish a message provides a controller with the data it needs to refresh itself.
     * 
     * @param controller
     *        (required)
     * @author <strong>Shane Duncan</strong> JCCC, Sep 23, 2015
     * @throws ShamenJMSException
     *         if an exception occurred
     */
    public void resetControllerSchedules(JmsController controller) throws ShamenJMSException;

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
    public void publishMessageToController(ControllerMessage msg, String controllerAddress) throws ShamenJMSException;

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
    public Message receiveAcknowledgement(Queue q, String correlationId);

}
