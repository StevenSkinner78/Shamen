/**
 * @(#)ApplicationPublisher.java 1.0.0 Copyright (c) 2018 Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit All Rights Reserved. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: -Redistribution of source code must retain the above copyright notice, this list of conditions and the following disclaimer. -Redistribution in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. Neither the name of Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit or the names of contributors may be used to endorse or promote products derived from this software without specific prior written permission. This software is provided "AS IS," without a warranty of any kind. ALL EXPRESS OR IMPLIED
 *                               CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. You
 *                               acknowledge that this software is not designed, licensed or intended for use in the design, construction, operation or maintenance of any nuclear facility.
 */
package gov.doc.isu.shamen.jms;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TopicConnectionFactory;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author <strong>Shane Duncan</strong> JCCC, Aug 19, 2015
 * @author <strong>Steven Skinner</strong> JCCC, Oct 01, 2019
 */
public class ApplicationPublisher extends Publisher {
    private static final String MY_CLASS_NAME = "gov.doc.isu.shamen.jms.Publisher";
    private static Log log = LogFactory.getLog(MY_CLASS_NAME);

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Aug 19, 2015
     */
    public ApplicationPublisher() {
        super();
    }

    /**
     * This method will publish a message to the topic with the proper selector specified.
     * 
     * @param destination
     *        (required)
     * @param message
     *        (required)
     * @author <strong>Shane Duncan</strong> JCCC, Sep 23, 2015
     * @throws NamingException
     *         exeption that can be thrown by this method
     * @throws JMSException
     *         exeption that can be thrown by this method
     */
    public void publishToTopic(Destination destination, Message message) throws NamingException, JMSException {
        log.debug("Entering: publishToTopic with destination, message: " + new Object[]{destination, message});
        // Local reference to a TopicPublisher
        MessageProducer publisher = null;

        log.trace("Get a connection to the QueueConnectionFactory.");
        tcf = (TopicConnectionFactory) ctx.lookup(getConnectionFactoryName());

        log.trace("Create a connection.");
        topicConnection = tcf.createTopicConnection();

        log.trace("Create a session that is non-transacted and is notified automatically.");
        ses = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);

        log.trace("Create the publisher.");
        publisher = ses.createProducer(destination);

        log.trace("Publish the message. Message: " + String.valueOf(message));
        publisher.send(message);

        log.trace("Close the open resources.");
        topicConnection.close();
        ctx.close();
        log.trace("Exiting: publishToTopic");
    }// end publishToTopic
}// end class
