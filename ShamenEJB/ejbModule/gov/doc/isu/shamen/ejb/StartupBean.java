/**
 *
 */
package gov.doc.isu.shamen.ejb;

import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import gov.doc.isu.shamen.ejb.beans.view.ApplicationStatusUpdaterBeanLocal;
import gov.doc.isu.shamen.ejb.beans.view.ShamenScheduleBeanLocal;

/**
 * This class is loaded on startup and remains alive as long as the application is deployed on the server.
 *
 * @author Shane Duncan JCCC
 */
@Singleton(name = "SystemInitializer")
@Startup
public class StartupBean {
    private Logger logger = Logger.getLogger("gov.doc.isu.shamen.ejb.StartupBean");

    @EJB
    private ShamenScheduleBeanLocal scheduleBean;
    @EJB
    private ApplicationStatusUpdaterBeanLocal updater;
    private static volatile AtomicLong timeInMillis;

    /**
     * This default constructor must be used to instantiate this class.
     */
    public StartupBean() {
        super();
        setTimeInMillis(new AtomicLong(System.currentTimeMillis()));
    }// end constructor

    /**
     * This method is used to initialize any resources required after construction.
     */
    @PostConstruct
    public void initialize() {
        logger.debug("Entering gov.doc.isu.shamen.ejb.StartupBean.initialize()");
        try{
            logger.info("Establish connection to all controllers.");
            scheduleBean.establishAllControllerStatuses();
        }catch(Exception e){
            System.err.println("ERROR initializing Shamen EJB:  " + e.getMessage());
            e.printStackTrace(System.err);
        }// end try/catch
        logger.debug("Exiting gov.doc.isu.shamen.ejb.StartupBean.initialize()");
    }// end initialize

    /**
     * This method is used to destroy any resources required before destroying.
     */
    @PreDestroy
    public void destroy() {
        logger.debug("Entering destroy");
        try{
            logger.info("Attempting to shutdown Shamen EJB");
            logger.info("Attempting to shutdown the log manager.");
            LogManager.shutdown();
            System.out.println("LogManager was successfully shutdown.");
        }catch(Exception e){
            System.err.println("Exception has occurred while trying to shutdown the LogManager. Error message is: " + e.getMessage());
        }// end try...catch
        System.out.println("Exiting destroy");
    }// end destroy

    /**
     * @return the timeInMillis
     */
    public static AtomicLong getTimeInMillis() {
        return timeInMillis;
    }

    /**
     * @param timeInMillis
     *        the timeInMillis to set
     */
    public static void setTimeInMillis(AtomicLong timeInMillis) {
        StartupBean.timeInMillis = timeInMillis;
    }
}// end class
