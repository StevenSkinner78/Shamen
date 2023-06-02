package gov.doc.isu.shamen.business;

import java.io.ObjectStreamException;
import java.io.Serializable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import gov.doc.isu.dwarf.exception.BaseException;
import gov.doc.isu.dwarf.util.StringUtil;
import gov.doc.isu.shamen.ejb.beans.view.JmsStatusBeanLocal;
import gov.doc.isu.shamen.models.JmsModel;

/**
 * This is the processor class for JmsStatus.
 * 
 * @author <strong>Shane B. Duncan</strong>
 */
public class JmsStatusInfo implements Serializable {

    private static final long serialVersionUID = -7135204850568105900L;
    private static Logger log = Logger.getLogger("gov.doc.isu.shamen.business.JmsStatusInfo");
    private static final JmsStatusInfo INSTANCE = new JmsStatusInfo();

    /**
     * This constructor made private to block instantiating this class.
     */
    private JmsStatusInfo() {
        super();
    }// end constructor

    /**
     * This method returns the private static final instance variable to enforce the Singleton pattern.
     * 
     * @return An instance of this class.
     */
    public static JmsStatusInfo getInstance() {
        log.debug("Returning the static instance of the AuthorizedUserInfo class.");
        return INSTANCE;
    }// end getInstance

    /**
     * This method is called to use a JNDI lookup for accessing the {@link JmsStatusBeanLocal}.
     * 
     * @return An instance of the <code>JmsStatusBeanLocal</code>.
     * @throws BaseException
     *         An exception if the <code>JmsStatusBeanLocal</code> class cannot be accessed.
     */
    public JmsStatusBeanLocal getBean() throws BaseException {
        log.debug("Entering JmsStatusInfo.getBean().");
        log.debug("This method is called to use a JNDI lookup for accessing the JmsStatusBeanLocal.");
        JmsStatusBeanLocal bean;
        try{
            Context context = new InitialContext();
            bean = (JmsStatusBeanLocal) context.lookup("java:global/ShamenEAR/ShamenEJB/JmsStatusBean!gov.doc.isu.shamen.ejb.beans.view.JmsStatusBeanLocal");
        }catch(NamingException e){
            log.error("An exception occurred while attempting to access the AuthorizedUserBean from the ShamenServiceBean. Message is: " + e.getMessage());
            throw new BaseException(e.getMessage());
        }// end try/catch
        log.debug("Exiting getBean. Return value is: " + String.valueOf(bean));
        return bean;
    }// end getBean

    /**
     * This method is used to retrieve the jmsStatus
     *
     * @return JmsModel
     * @throws BaseException
     *         to demonstrate error occurred.
     */
    public JmsModel getStatus() throws BaseException {
        log.debug("Entering JmsStatusInfo.getStatus()");
        log.debug("This method is used to search for a Authorized user record by userID to see if they are an Authorized User to this system.");
        JmsModel jmsModel = new JmsModel();
        try{
            jmsModel.setStatus(getBean().getStatus());
        }catch(Exception e){
            log.error("An exception occurred while retrieving an authorized user entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch

        log.debug("Exiting JmsStatusInfo.getStatus jmsModel=" + StringUtil.isNull(jmsModel));
        return jmsModel;
    } // end getStatus

    /**
     * This method is used to replace the use of a compiler-generated <code>readResolve()</code> method which is called from the <code>readObject()</code> method within the serialization machinery.
     * <p>
     * The <code>readObject()</code> method will create a new instance of the serialized class and call the <code>readResolve()</code> thereafter. By providing this method, the singleton pattern will not be violated with an additional instance of the class since this method returns the singleton instance.
     * 
     * @return The singleton instance of this class
     * @throws ObjectStreamException
     *         An exception if the serialization machinery fails on this method.
     */
    private Object readResolve() throws ObjectStreamException {
        return INSTANCE;
    }// end readResolve
} // end class
