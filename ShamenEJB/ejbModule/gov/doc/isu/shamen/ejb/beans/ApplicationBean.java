/**
 *
 */
package gov.doc.isu.shamen.ejb.beans;

import java.sql.Timestamp;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;

import gov.doc.isu.shamen.ejb.beans.view.ApplicationBeanLocal;
import gov.doc.isu.shamen.ejb.util.ShamenEJBUtil;
import gov.doc.isu.shamen.jpa.entity.ApplicationEntity;
import gov.doc.isu.shamen.jpa.entity.ApplicationInstanceEntity;
import gov.doc.isu.shamen.jpa.entity.ApplicationStatusCodeEntity;

/**
 * This class is used to access the database via an entity manager to perform CRUD operations with the applications table.
 *
 * @author Steven Skinner JCCC
 */
@Stateless
@Local(ApplicationBeanLocal.class)
public class ApplicationBean implements ApplicationBeanLocal {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Logger logger = Logger.getLogger("gov.doc.isu.shamen.ejb.beans.ApplicationBean");
    @PersistenceContext(unitName = "ShamenJPA")
    private EntityManager em;

    /**
     * This default constructor must be used to instantiate this class.
     */
    public ApplicationBean() {
        super();
    }// end constructor

    /**
     * This method is called after the bean has been constructed.
     */
    @PostConstruct
    private void initialize() {
        logger.debug("Entering initialize. This method is called after the bean has been constructed.");
        if(em == null){
            logger.error("Entity manager has not been created in the post construct method. Check the server logs for deployment errors.");
        }// end if
        logger.debug("Exiting initialize.");
    }// end initialize

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ApplicationEntity> getApplicationList() throws Exception {
        logger.debug("Entering getApplicationList");
        logger.debug("This method is used to return a list of ApplicationEntitys.");
        // NOTE: This query is different because it requires fresh data all the time due to the volatility of the
        // application instances, thus the BYPASS cache logic is used.
        TypedQuery<ApplicationEntity> q = em.createNamedQuery("ApplicationEntity.findAll", ApplicationEntity.class);
        q.setHint("javax.persistence.cache.retrieveMode", "BYPASS");
        List<ApplicationEntity> list = q.getResultList();
        logger.debug("Return value is: list.size=" + (ShamenEJBUtil.isEmpty(list) ? "0" : list.size()));
        logger.debug("Exiting getApplicationList");
        return list;
    }// end getApplicationList

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Object> getApplicationListForCode() throws Exception {
        logger.debug("Entering getApplicationListForCode");
        logger.debug("This method is used to return a list of Objects with application name and address.");
        List<Object> list = em.createNativeQuery("select application_ref_id, Application_Nm, Application_Addr from trans.Applications where delete_ind = 'N' order by application_Nm, application_addr").getResultList();
        logger.debug("Return value is: list.size=" + (ShamenEJBUtil.isEmpty(list) ? "0" : list.size()));
        logger.debug("Exiting getApplicationListForCode");
        return list;
    }// end getApplicationListForCode

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Object> getApplicationListBySystemForCode(Long systemRefId) throws Exception {
        logger.debug("Entering getApplicationListBySystemForCode");
        logger.debug("This method is used to return a list of Objects with application name and address.");
        logger.debug("Entry parameters are: systemRefId=" + String.valueOf(systemRefId));
        List<Object> list = em.createNativeQuery("select application_ref_id, Application_Nm, Application_Addr from trans.Applications where System_Ref_Id = ? and delete_ind = 'N' order by application_Nm, application_addr").setParameter(1, systemRefId).getResultList();
        logger.debug("Return value is: list.size=" + (ShamenEJBUtil.isEmpty(list) ? "0" : list.size()));
        logger.debug("Exiting getApplicationListBySystemForCode");
        return list;
    }// end getApplicationListForCode

    /**
     * {@inheritDoc}
     */
    @Override
    public ApplicationEntity findByNameAndAddress(String applicationName, String applicationAddress) throws Exception {
        logger.debug("Entering findByNameAndAddress");
        logger.debug("This method is used to return an instance of an object for viewing or for update.");
        logger.debug("Entry parameters are: applicationName=" + String.valueOf(applicationName) + ", applicationAddress=" + String.valueOf(applicationAddress));
        ApplicationEntity entity = em.createNamedQuery("ApplicationEntity.findByNameAndAddress", ApplicationEntity.class).setParameter("applicationName", applicationName).setParameter("applicationAddress", applicationAddress).getSingleResult();
        // em.refresh(entity);
        logger.debug("Return value is: entity=" + String.valueOf(entity));
        logger.debug("Exiting findByNameAndAddress");
        return entity;
    }// end findByNameAndAddress

    /**
     * {@inheritDoc}
     */
    @Override
    public ApplicationInstanceEntity findInstanceByApplicationAddressName(String applicationName, String applicationAddress, String instanceName) throws Exception {
        logger.debug("Entering findInstanceByApplicationAddressName");
        logger.debug("This method is used to return an instance of an object for viewing or for update.");
        logger.debug("Entry parameters are: applicationName=" + String.valueOf(applicationName) + ", applicationAddress=" + String.valueOf(applicationAddress) + ", instanceName=" + String.valueOf(instanceName));
        ApplicationInstanceEntity entity = null;
        try{
            if(applicationName != null && applicationAddress != null && instanceName != null){
                entity = em.createNamedQuery("ApplicationInstanceEntity.getInstanceByApplicationAddressName", ApplicationInstanceEntity.class).setParameter("applicationName", applicationName).setParameter("applicationAddress", applicationAddress).setParameter("instanceName", applicationName).getSingleResult();
                // Refresh because this data must be fresh
                // em.refresh(entity);
            }// end if
        }catch(NoResultException e){
            logger.debug("No application instance was found.");
            // This is not actually an exception.
            // SWALLOW IT
        }// end try-catch
        logger.debug("Return value is: entity=" + String.valueOf(entity));
        logger.debug("Exiting findInstanceByApplicationAddressName");
        return entity;
    }// end findInstanceByApplicationAddressName

    /**
     * {@inheritDoc}
     */
    @Override
    public ApplicationEntity findApplicationByRefId(Long applicationRefId) throws Exception {
        logger.debug("Entering findApplicationByRefId");
        logger.debug("This method is used to return an instance of an object for viewing or for update.");
        logger.debug("Entry parameters are: applicationRefId=" + String.valueOf(applicationRefId));
        ApplicationEntity entity = em.find(ApplicationEntity.class, applicationRefId);
        // em.refresh(entity);
        logger.debug("Return value is: entity=" + String.valueOf(entity));
        logger.debug("Exiting findApplicationByRefId");
        return entity;
    }// end findApplicationByRefId

    /**
     * {@inheritDoc}
     */
    @Override
    public ApplicationInstanceEntity findApplicationInstanceByRefId(Long applicationInstanceRefId) throws Exception {
        logger.debug("Entering findApplicationInstanceByRefId");
        logger.debug("This method is used to return an instance of an object for viewing or for update.");
        logger.debug("Entry parameters are: applicationInstanceRefId=" + String.valueOf(applicationInstanceRefId));
        ApplicationInstanceEntity entity = em.find(ApplicationInstanceEntity.class, applicationInstanceRefId);
        // em.refresh(entity);
        logger.debug("Return value is: entity=" + String.valueOf(entity));
        logger.debug("Exiting findApplicationInstanceByRefId");
        return entity;
    }// end findApplicationInstanceByRefId

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(ApplicationEntity entity) throws Exception {
        logger.debug("Entering update");
        logger.debug("This method is used to update an instance of an object with the database.");
        logger.debug("Entry parameters are: entity=" + String.valueOf(entity));
        entity.setBatchApps(em.find(ApplicationEntity.class, entity.getApplicationRefId()).getBatchApps());
        em.merge(entity);
        // force DB write
        em.flush();
        logger.debug("Exiting update");
    }// end merge

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(ApplicationInstanceEntity entity) throws Exception {
        logger.debug("Entering update");
        logger.debug("This method is used to update an instance of an object with the database.");
        logger.debug("Entry parameters are: entity=" + String.valueOf(entity));
        // insure that the update junk is not null. This happens because of JPA caching
        if(entity.getCommon().getUpdateUserRefId() == null){
            entity.getCommon().setUpdateUserRefId(0L);
            entity.getCommon().setUpdateTime(new Timestamp(System.currentTimeMillis()));
        }// end if
        if(entity.getTimeOfDeathTs() == null){
            entity.setTimeOfDeathTs(ShamenEJBUtil.getDefaultTimeStamp());
        }
        em.merge(entity);
        em.flush();
        logger.debug("Exiting update");
    }// end merge

    /**
     * {@inheritDoc}
     */
    @Override
    public ApplicationEntity create(ApplicationEntity entity) throws Exception {
        logger.debug("Entering create");
        logger.debug("This method is used to create an instance of an object with the database.");
        logger.debug("Entry parameters are: entity=" + String.valueOf(entity));
        entity = em.merge(entity);
        logger.debug("Return value is: entity=" + String.valueOf(entity));
        logger.debug("Exiting create");
        return entity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApplicationInstanceEntity create(ApplicationInstanceEntity entity) throws Exception {
        logger.debug("Entering create");
        logger.debug("This method is used to create an instance of an object with the database.");
        logger.debug("Entry parameters are: entity=" + String.valueOf(entity));
        if(entity.getTimeOfDeathTs() == null){
            entity.setTimeOfDeathTs(ShamenEJBUtil.getDefaultTimeStamp());
        }// end if
        em.merge(entity);
        em.flush();
        logger.debug("Return value is: entity=" + String.valueOf(entity));
        logger.debug("Exiting create");
        return entity;
    }// end create

    /**
     * {@inheritDoc}
     */
    @Override
    public ApplicationEntity deleteApplication(Long applicationRefId, Long updateUserRefId) throws Exception {
        logger.debug("Entering deleteApplication");
        logger.debug("This method is used to 'soft' delete an instance of an object in the database.");
        logger.debug("Entry parameters are: applicationRefId=" + String.valueOf(applicationRefId) + ", updateUserRefId=" + String.valueOf(updateUserRefId));
        ApplicationEntity appEntity = findApplicationByRefId(applicationRefId);
        if(null != appEntity){
            if(null != appEntity.getCommon()){
                // Set the delete indicator and update user reference ID/timestamp before merging.
                appEntity.getCommon().setDeleteIndicator("Y");
                appEntity.getCommon().setUpdateUserRefId(updateUserRefId);
                appEntity.getCommon().setUpdateTime(new java.sql.Timestamp(System.currentTimeMillis()));
                appEntity = em.merge(appEntity);
            }else{
                logger.warn("The embedded common entity model is null in the application entity. The delete fails!");
            }// end if
        }else{
            logger.warn("Unable to find the application model with the entity manager! Record will not be deleted. Record primary key is " + String.valueOf(applicationRefId) + ", and the update user reference ID is " + String.valueOf(updateUserRefId));
        }// end if/else
        logger.debug("Return value is: appEntity=" + String.valueOf(appEntity));
        logger.debug("Exiting deleteApplication");
        return appEntity;
    }// end deleteApplication

    /**
     * {@inheritDoc}
     */
    @Override
    public ApplicationInstanceEntity deleteApplicationInstance(Long applicationInstanceRefId, Long updateUserRefId) throws Exception {
        logger.debug("Entering deleteApplicationInstance");
        logger.debug("This method is used to 'soft' delete an instance of an object in the database.");
        logger.debug("Entry parameters are: applicationInstanceRefId=" + String.valueOf(applicationInstanceRefId) + ", updateUserRefId=" + String.valueOf(updateUserRefId));
        ApplicationInstanceEntity appEntity = findApplicationInstanceByRefId(applicationInstanceRefId);
        if(null != appEntity){
            if(null != appEntity.getCommon()){
                // Set the delete indicator and update user reference ID/timestamp before merging.
                appEntity.getCommon().setDeleteIndicator("Y");
                appEntity.getCommon().setUpdateUserRefId(updateUserRefId);
                appEntity.getCommon().setUpdateTime(new java.sql.Timestamp(System.currentTimeMillis()));
                // This is being hard-deleted temporarily.
                em.remove(appEntity);
            }else{
                logger.warn("The embedded common entity model is null in the application entity. The delete fails!");
            }// end if
        }else{
            logger.warn("Unable to find the application model with the entity manager! Record will not be deleted. Record primary key is " + String.valueOf(applicationInstanceRefId) + ", and the update user reference ID is " + String.valueOf(updateUserRefId));
        }// end if/else
        logger.debug("Return value is: appEntity=" + String.valueOf(appEntity));
        logger.debug("Exiting deleteApplicationInstance");
        return appEntity;
    }// end deleteApplicationInstance

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateAllApps(ApplicationStatusCodeEntity statusInd, Long updateUserRefId) throws Exception {
        logger.debug("Entering updateAllApps");
        logger.debug("Used to update all applicationEntities by setting stausInd to passed in status.");
        logger.debug("Entry parameters are: statusInd=" + String.valueOf(statusInd) + ", updateUserRefId=" + String.valueOf(updateUserRefId));
        String defaultMessage = "";
        // These can be changed to a default message to show up
        // if someone uses the activate all or suspend all function
        if(statusInd.getApplicationStatusCd().equals("ACT")){
            defaultMessage = "(default) activation message.";
        }else if(statusInd.getApplicationStatusCd().equals("SUP")){
            defaultMessage = "(default) suspension message.";
        }// end if/else
        em.createQuery("UPDATE ApplicationEntity a SET a.common.updateUserRefId = ?1, a.common.updateTime = ?2, a.applicationStatus = ?3, a.statusComment = ?4 WHERE a.common.deleteIndicator = 'N'").setParameter(1, updateUserRefId).setParameter(2, new java.sql.Timestamp(System.currentTimeMillis())).setParameter(3, statusInd).setParameter(4, defaultMessage).executeUpdate();
        logger.debug("Exiting updateAllApps");
    }// end updateAllApps

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteOldApplicationInstances(ApplicationEntity application) throws Exception {
        logger.debug("Entering deleteOldApplicationInstances");
        logger.debug("Used to delete all application instances for a given application");
        logger.debug("Entry parameters are: application=" + String.valueOf(application));
        em.createNativeQuery("DELETE FROM Trans.Application_Instances WHERE (Application_Ref_Id = ?1) ").setParameter(1, application.getApplicationRefId()).executeUpdate();
        logger.debug("Exiting deleteOldApplicationInstances");
    }// end deleteOldApplicationInstances

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteOldApplicationInstancesAll() throws Exception {
        logger.debug("Entering deleteOldApplicationInstancesAll");
        logger.debug("Used to delete all application instances for a all applications");
        em.createNativeQuery("DELETE FROM Trans.Application_Instances").executeUpdate();
        logger.debug("Exiting deleteOldApplicationInstancesAll");
    }// end deleteOldApplicationInstancesAll

    /**
     * {@inheritDoc}
     */
    @Override
    public ApplicationInstanceEntity getApplicationInstance(ApplicationEntity application, String node) throws Exception {
        logger.debug("Entering getApplicationInstance");
        logger.debug("This method gets the application instance for a given application by node.");
        logger.debug("Entry parameters are: application=" + String.valueOf(application) + ", node=" + String.valueOf(node));
        ApplicationInstanceEntity entity = null;
        try{
            entity = em.createNamedQuery("ApplicationInstanceEntity.getInstanceByKey", ApplicationInstanceEntity.class).setParameter("applicationRefId", application.getApplicationRefId()).setParameter("node", node).getSingleResult();
        }catch(NoResultException e){
            logger.debug("No application instance was found.");
            // This is not actually an exception.
            // SWALLOW IT
        }// end try-catch

        logger.debug("Return value is: entity=" + String.valueOf(entity));
        logger.debug("Exiting getApplicationInstance");
        return entity;
    }// end getApplicationInstancePrimary

}// end class
