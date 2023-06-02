/**
 * 
 */
package gov.doc.isu.shamen.ejb.beans;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.Singleton;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.apache.log4j.Logger;

import gov.doc.isu.shamen.ejb.beans.view.JmsStatusBeanLocal;

/**
 * This class is used to check and store JMS status.
 * 
 * @author Shane Duncan JCCC
 */
@Singleton
@Local(JmsStatusBeanLocal.class)
public class JmsStatusBean implements JmsStatusBeanLocal {

    /**
     * 
     */
    private static final long serialVersionUID = -9054491981054519451L;
    private Logger logger = Logger.getLogger("gov.doc.isu.shamen.ejb.beans.JmsStatusBean");
    private Boolean status;
    @Resource(name = "CO_SconT", type = ConnectionFactory.class)
    private ConnectionFactory factory;

    /**
     * This default constructor must be used to instantiate this class.
     */
    public JmsStatusBean() {
        super();
    }// end constructor

    /**
     * This method is called after the bean has been constructed.
     */
    @PostConstruct
    private void initialize() {
        logger.debug("Entering initialize. This method is called after the bean has been constructed.");
        initializeJms();
        logger.debug("Exiting initialize");
    }// end initialize

    /**
     * This method is called to check and see if the JMS environment is set up and usable.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Sep 3, 2015
     */
   // @Schedule(minute = "30", hour = "*", persistent=false)
    private void initializeJms() {
        logger.debug("Entering initializeJms. This method is called to check and see if the JMS environment is set up and usable.  It's run from the EJB schedule annotations.");
        Boolean cool = true;
        Connection con = null;
        try{
            con = factory.createConnection();
        }catch(JMSException ex){
            cool = false;
            logger.warn("JMSException encountered trying to check status of the JMS server.", ex);
        }catch(Exception ex){
            cool = false;
            logger.warn("Exception encountered trying to check status of the JMS server.", ex);
        }finally{
            try{
                if(con != null){
                    con.close();
                    con = null;
                }// end if
            }catch(Exception ex){
                logger.error("Exception encountered trying to close the resources used to check the status of the JMS server.", ex);
            }// end try-catch
        }// end finally

        status = cool;
        logger.debug("Exiting initializeJms after setting connection status = " + String.valueOf(status));
    }// end initializeJms

    /**
     * @return the status
     */
    public Boolean getStatus() {
        return status;
    }

    /**
     * @param status
     *        the status to set
     */
    public void setStatus(Boolean status) {
        this.status = status;
    }

}// end class
