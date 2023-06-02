/**
 * 
 */
package gov.doc.isu.shamen.ejb.email;

import java.io.File;
import java.io.Serializable;

import javax.ejb.Local;

/**
 * This is the local business interface for the {@link EmailBean}.
 * 
 * @author Joseph Burris JCCC
 */
@Local
public interface IEmailBeanLocal extends Serializable {

    /**
     * This method is used to construct an e-mail using the incoming parameters.
     * 
     * @param to
     *        The e-mail address(es) to send to.
     * @param cc
     *        The e-mail address(es) to courtesy copy.
     * @param bcc
     *        The e-mail address(es) to blind courtesy copy.
     * @param from
     *        The e-mail address(es) to send from.
     * @param subject
     *        The subject content of the e-mail.
     * @param body
     *        The message content of the e-mail.
     * @param files
     *        The files to attach when sending the e-mail.
     */
    public void send(String to, String cc, String bcc, String from, String subject, String body, File[] files);
}// end interface
