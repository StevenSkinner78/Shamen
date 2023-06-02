/**
 *
 */
package gov.doc.isu.shamen.ejb.beans;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;

import gov.doc.isu.shamen.ejb.beans.view.ApplicationBeanLocal;
import gov.doc.isu.shamen.ejb.beans.view.ApplicationStatusUpdaterBeanLocal;
import gov.doc.isu.shamen.ejb.beans.view.BatchAppBeanLocal;
import gov.doc.isu.shamen.ejb.beans.view.ControllerBeanLocal;
import gov.doc.isu.shamen.ejb.beans.view.ControllerStatusUpdaterBeanLocal;
import gov.doc.isu.shamen.ejb.beans.view.ShamenScheduleBeanLocal;
import gov.doc.isu.shamen.ejb.email.IEmailBeanLocal;
import gov.doc.isu.shamen.jpa.entity.ControllerEntity;

/**
 * This Bean maintains all Application Schedules for Shamen.
 *
 * @author Steven L. Skinner
 */
@Singleton
@Local(ShamenScheduleBeanLocal.class)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class ShamenScheduleBean implements ShamenScheduleBeanLocal {
    /**
     *
     */
    private static final long serialVersionUID = -2248129051203768991L;

    private Logger log = Logger.getLogger("gov.doc.isu.shamen.ejb.beans.ShamenScheduleBean");

    @PersistenceContext(unitName = "ShamenJPA")
    private EntityManager em;

    @EJB(beanInterface = IEmailBeanLocal.class)
    private IEmailBeanLocal email;

    @EJB
    private ApplicationBeanLocal bean;
    @EJB
    private ControllerBeanLocal controllerBean;
    @EJB
    private ApplicationStatusUpdaterBeanLocal updater;
    @EJB
    private ControllerStatusUpdaterBeanLocal controllerStatusUpdaterBean;
    @EJB
    private BatchAppBeanLocal batchAppBean;
    @Resource(name = "CO_SconT", type = ConnectionFactory.class)
    private ConnectionFactory factory;
    @Resource(name = "CO.WebApp.TOPIC", type = Destination.class)
    private Destination destination;

    /**
     * This method deletes all Run Status records from the database that are two weeks old or greater from the current date of schedule run.
     * <p>
     * It is scheduled to run every Friday at midnight.
     * </p>
     */
    // @Schedule(hour = "03", minute = "45", persistent=false)
    // @Schedule(hour="*",minute = "*/30", persistent=false)
    public void deletePastRunStatuses() {
        log.debug("Entering deletePastRunStatuses.");
        log.debug("This method is used to delete all run statuses that are two weeks old or greater.");
        int result = 0;
        try{
            result = batchAppBean.deleteOldRunStatus();
        }catch(Exception e){
            log.error("Exception occurred while executing scheduled deletion of run status records.", e);
        }// end try-catch
        log.debug("Exiting deletePastRunStatuses. Number deleted = " + String.valueOf(result));
    }// end deletePastRunStatuses

    /**
     * This method loads a List<String> of all the administrators who should receive email's concerning Shamen.
     *
     * @return List<String>
     */
    public List<String> getAdminEmailList() {
        log.debug("Entering getAdminEmailList.");
        log.debug("This method loads a List<String> of all the administrators who should receive email's concerning Shamen.");
        List<String> result = em.createNamedQuery("AuthorizedUserEntity.FIND_FOR_EMAIL", String.class).getResultList();
        log.debug("Exiting getAdminEmailList.");
        return result;
    }// end getAdminEmailList

    /**
     * {@inheritDoc}
     */
    @Override
    // @Schedule(hour = "*/2", minute = "10", persistent=false)
    public void establishAllControllerStatuses() {
        log.debug("Entering: establishAllControllerStatuses");
        log.debug("This method will publish a message to a controller to check for a response. If a response returns then the controller is deemed connected.");
        List<ControllerEntity> controllerList;
        try{
            controllerList = controllerBean.getControllerListForUpdater();
            Iterator<ControllerEntity> it = controllerList.iterator();
            while(it.hasNext()){
                ControllerEntity entity = controllerBean.findControllerByRefId(it.next().getControllerRefId());
                controllerStatusUpdaterBean.setSingleControllerStatus(entity, ControllerStatusUpdaterBeanLocal.AWAITING_RESPONSE);
                controllerStatusUpdaterBean.establishControllerStatus(entity);
            }// end while
        }catch(Exception e){
            log.error("Error occurred trying to establish all controller statuses.", e);
        }// end try/catch
        log.debug("Exiting: establishAllControllerStatuses");
    }// end isControllerConnected

    /**
     * {@inheritDoc}
     */
    @Override
    public void establishSingleControllerStatus(Long refId) {
        log.debug("Entering: establishSingleControllerStatus");
        log.debug("This method will publish a message to a controller to check for a response. If a response returns then the controller is deemed connected.");
        try{
            ControllerEntity entity = controllerBean.findControllerByRefId(refId);
            controllerStatusUpdaterBean.establishControllerStatus(entity);
        }catch(Exception e){
            log.error("Error occurred trying to establish controller status.", e);
        }// end try/catch
        log.debug("Exiting: establishSingleControllerStatus");
    }// end establishSingleControllerStatus

    /**
     * This is scheduled to insure fresh application connection statuses.
     */
    // @Schedule(hour = "*", minute = "*/15", persistent=false)
    public void resetAllApplicationStatuses() {
        log.debug("Entering resetAllApplicationStatuses");
        log.debug("This is scheduled to insure fresh application connection statuses.");
        updater.setAllApplicationInstanceStatus(ApplicationStatusUpdaterBeanLocal.STATUS_AWAITING_RESPONSE);
        updater.resetAllApplicationStatusesAsync();
        log.debug("Exiting resetAllApplicationStatuses");
    }// end resetAllApplicationStatuses

}// end class
