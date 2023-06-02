/**
 * 
 */
package gov.doc.isu.shamen.ejb.email;

import java.io.File;
import java.util.Date;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;

/**
 * This class used to send e-mails from the application.
 * 
 * @author Joseph Burris JCCC
 */
@Stateless
@Asynchronous
@Local(IEmailBeanLocal.class)
public class EmailBean implements IEmailBeanLocal {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Logger logger = Logger.getLogger("gov.doc.isu.shamen.ejb.email.EmailBean");
    @Resource(name = "mail/session1")
    private Session session;

    /**
     * This default constructor must be used to instantiate this class.
     */
    public EmailBean() {
        super();
    }// end constructor

    /**
     * {@inheritDoc}
     */
    public void send(String to, String cc, String bcc, String from, String subject, String body, File[] files) {
        logger.debug("Entering send. Incoming parameters are: to=" + String.valueOf(to) + ", cc=" + String.valueOf(cc) + ", bcc=" + String.valueOf(bcc) + ", from=" + String.valueOf(from) + ", subject=" + String.valueOf(subject) + ", body=" + String.valueOf(body));
        try{
            MimeMessage msg = new MimeMessage(session);
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
            if(null != cc && !"".equals(cc)){
                msg.addRecipients(Message.RecipientType.CC, InternetAddress.parse(cc, false));
            }// end if
            if(null != bcc && !"".equals(bcc)){
                msg.addRecipients(Message.RecipientType.BCC, InternetAddress.parse(bcc, false));
            }// end if
            msg.setFrom(new InternetAddress(from));
            msg.setSubject(subject);
            msg.setSentDate(new Date());

            MimeBodyPart messageBodyPart = new MimeBodyPart();
            Multipart multipart = new MimeMultipart();
            messageBodyPart.setText(body);
            messageBodyPart.setContent(body, "text/html");
            multipart.addBodyPart(messageBodyPart);

            if(files != null){
                logger.debug("Adding " + files.length + " attachment(s) to the outgoing e-mail");
                MimeBodyPart attachment;
                DataSource source;
                File file;
                for(int i = 0, j = files.length;i < j;i++){
                    file = files[i];
                    if(file != null){
                        // if(logger.isLoggable(Level.FINER)){
                        // logger.finer("Adding a file to the outgoing e-mail");
                        // } // end if
                        attachment = new MimeBodyPart();
                        source = new FileDataSource(file);
                        attachment.setDataHandler(new DataHandler(source));
                        attachment.setFileName(file.getName());
                        multipart.addBodyPart(attachment);
                    }// end if
                }// end for
            } // end if
            msg.setContent(multipart);
            logger.debug("Attempting javax.mail.Transport for mime message.");
            Transport.send(msg);
            logger.debug("Successful call to javax.mail.Transport.");
        }catch(Exception e){
            logger.error("An exception occurred while trying to send an email using the folowing values. to=" + String.valueOf(to) + " from=" + String.valueOf(from) + " subject=" + String.valueOf(subject) + " body=" + String.valueOf(body) + ". Message is " + e.getLocalizedMessage(), e);
        }// end try/catch
        logger.debug("Exiting send");
    }// end send
}// end class
