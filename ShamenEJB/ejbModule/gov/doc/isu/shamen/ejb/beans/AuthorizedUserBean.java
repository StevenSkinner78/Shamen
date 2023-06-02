/**
 *
 */
package gov.doc.isu.shamen.ejb.beans;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;

import gov.doc.isu.shamen.ejb.beans.view.AuthorizedUserBeanLocal;
import gov.doc.isu.shamen.ejb.util.ShamenEJBUtil;
import gov.doc.isu.shamen.jpa.entity.ApplicationEntity;
import gov.doc.isu.shamen.jpa.entity.AuthorizedUserEntity;
import gov.doc.isu.shamen.jpa.entity.BatchAppEntity;
import gov.doc.isu.shamen.jpa.entity.SystemEntity;

/**
 * This class is used to access the database via an entity manager to perform CRUD operations with the authorized user table.
 *
 * @author Joseph Burris JCCC
 */
@Stateless
@Local(AuthorizedUserBeanLocal.class)
public class AuthorizedUserBean implements AuthorizedUserBeanLocal {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Logger logger = Logger.getLogger("gov.doc.isu.shamen.ejb.beans.AuthorizedUserBean");
    @PersistenceContext(unitName = "ShamenJPA")
    private EntityManager em;

    /**
     * This default constructor must be used to instantiate this class.
     */
    public AuthorizedUserBean() {
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
        logger.debug("Exiting initialize");
    }// end initialize

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AuthorizedUserEntity> getUsersList() throws Exception {
        logger.debug("Entering getUsersList. This method is used to return a list of AuthorizedUserEntitys.");
        List<AuthorizedUserEntity> list = em.createNamedQuery("AuthorizedUserEntity.FIND_ALL", AuthorizedUserEntity.class).getResultList();
        logger.debug("Return value is: list.size=" + (ShamenEJBUtil.isEmpty(list) ? "0" : list.size()));
        logger.debug("Exiting getUsersList.");
        return list;
    }// end getUsersList

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getEmailList() throws Exception {
        logger.debug("Entering getUsersList. This method is used to return a list of AuthorizedUserEntitys.");
        List<String> list = em.createNamedQuery("AuthorizedUserEntity.FIND_FOR_EMAIL", String.class).getResultList();
        logger.debug("Return value is: list.size=" + (ShamenEJBUtil.isEmpty(list) ? "0" : list.size()));
        logger.debug("Exiting getUsersList.");
        return list;
    }// end getUsersList

    /**
     * {@inheritDoc}
     */
    @Override
    public AuthorizedUserEntity findAuthorizedUserByUserRefId(Long authorizedUserRefId) throws Exception {
        logger.debug("Entering findAuthorizedUserByUserRefId. This method is used to return an instance of an object for viewing or for update. Incoming parameter is: " + String.valueOf(authorizedUserRefId));
        AuthorizedUserEntity model = em.find(AuthorizedUserEntity.class, authorizedUserRefId);
        logger.debug("Return value is: entity=" + String.valueOf(model));
        logger.debug("Exiting findAuthorizedUserByUserRefId.");
        return model;
    }// end findAuthorizedUserByUserRefId

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<BatchAppEntity> getBatchListForUser(Long userRefId) throws Exception {
        logger.debug("Entering getBatchListForUser. This method is used to return a list of objects filled with batch app info.");
        List<BatchAppEntity> list = em.createNamedQuery("BatchAppEntity.FOR_USER", BatchAppEntity.class).setParameter("userID", userRefId).getResultList();
        logger.debug("Return value is: list.size=" + (ShamenEJBUtil.isEmpty(list) ? "0" : list.size()));
        logger.debug("Exiting getBatchListForUser.");
        return list;
    }// end getBatchListForUser

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<ApplicationEntity> getWebAppListForUser(Long userRefId) throws Exception {
        logger.debug("Entering getWebAppListForUser. This method is used to return a list of objects filled with web app info.");
        List<ApplicationEntity> list = em.createNamedQuery("ApplicationEntity.FOR_USER", ApplicationEntity.class).setParameter("userID", userRefId).getResultList();
        logger.debug("Return value is: list.size=" + (ShamenEJBUtil.isEmpty(list) ? "0" : list.size()));
        logger.debug("Exiting getWebAppListForUser.");
        return list;
    }// end getWebAppListForUser

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<SystemEntity> getSystemListForUser(Long userRefId) throws Exception {
        logger.debug("Entering getSystemListForUser. This method is used to return a list of objects filled with system app info.");
        List<SystemEntity> list = em.createNamedQuery("SystemEntity.FOR_USER", SystemEntity.class).setParameter("userID", userRefId).getResultList();
        logger.debug("Return value is: list.size=" + (ShamenEJBUtil.isEmpty(list) ? "0" : list.size()));
        logger.debug("Exiting getSystemListForUser.");
        return list;
    }// end getSystemListForUser

    /**
     * {@inheritDoc}
     */
    @Override
    public AuthorizedUserEntity findAuthorizedUserByUserId(String userSignOn) throws Exception {
        logger.debug("Entering findAuthorizedUserByUserId. This method is used to return an instance of an object for viewing or for update. Incoming parameter is: " + userSignOn);
        AuthorizedUserEntity model = null;
        try{
            model = em.createNamedQuery("AuthorizedUserEntity.FIND_BY_USER_ID", AuthorizedUserEntity.class).setParameter("userID", userSignOn).getSingleResult();
        }catch(NoResultException e){
            logger.debug("No result returned for query.");
        }catch(Exception e){
            throw e;
        }// end try/catch
        logger.debug("Return value is: entity=" + String.valueOf(model));
        logger.debug("Exiting findAuthorizedUserByUserId.");
        return model;
    }// end findAuthorizedUserByUserId

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Integer countBatchAppsAndCollectionsByUser(Long userRefId) throws Exception {
        logger.debug("Entering getAllScheduledBatchAppsAndCollections. This method is used to return a list of objects filled with batch app info.");
        Integer result = 0;
        StringBuffer query = new StringBuffer();
        query.append("SELECT b.Batch_App_Ref_Id,c.Controller_Nm, b.Batch_Nm, s.Schedule_Start_Dt, s.Start_Time, a.Application_Nm, a.Application_Addr,bt.Batch_Type_Desc, s.Active_Ind,cc.Controller_Status_Desc,f.frequency_desc,sy.System_Nm,c.controller_ref_id,c.controller_status_cd,s.Frequency_Cd,s.Recur_No,s.Repeat_Cd,s.Week_No,s.Weekdays,s.Day_No,s.Frequency_Type_Cd, b.System_Ref_Id, 'N',s.schedule_ref_id,us.User_First_Nm,us.User_Last_Nm");
        query.append(" FROM Trans.Batch_Apps b left join trans.Schedules s on s.Batch_App_Ref_Id = b.Batch_App_Ref_Id left join trans.Controllers c on c.Controller_Ref_Id = b.Controller_Ref_Id left join trans.Applications a on a.Application_Ref_Id = b.Application_Ref_Id left join code.Batch_Type_Codes bt on bt.Batch_Type_Cd = b.Batch_Type_Cd left join code.Controller_Status_Codes cc on cc.Controller_Status_Cd = c.Controller_Status_Cd left join code.Frequency_Codes f on f.Frequency_Cd = s.Frequency_Cd left join trans.Systems sy on sy.System_Ref_Id = b.System_Ref_Id left join trans.Authorized_Users us on us.User_Ref_Id = b.Responsible_Staff_User_Ref_Id and us.delete_ind = 'N'");
        query.append(" WHERE us.User_Ref_Id = ? and b.Delete_Ind = 'N' and Schedule_Start_Dt is not null and s.Delete_Ind = 'N' order by b.Batch_Nm");
        List<Object> list = em.createNativeQuery(query.toString()).setParameter(1, userRefId).getResultList();
        if(null != list){
            result = list.size();
        }// end if
        logger.debug("Return value is: result=" + result);
        logger.debug("Exiting getAllScheduledBatchAppsAndCollections.");
        return result;
    }// end getAllScheduledBatchAppsAndCollections

    /**
     * {@inheritDoc}
     */
    @Override
    public void merge(AuthorizedUserEntity entity) throws Exception {
        logger.debug("Entering merge. This method is used to merge an instance of an object with the database. Incoming parameter is: " + String.valueOf(entity));
        em.merge(entity);
        logger.debug("Exiting merge");
    }// end merge

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAuthorizedUser(Long userRefId, Long updateUserRefId) throws Exception {
        logger.debug("Entering deleteAuthorizedUser. This method is used to 'soft' delete an instance of an object in the database. Incoming parameters are: userRefId=" + String.valueOf(userRefId) + ", updateUserRefId=" + String.valueOf(updateUserRefId));
        AuthorizedUserEntity model = findAuthorizedUserByUserRefId(userRefId);
        if(null != model){
            AuthorizedUserEntity defaultSys = em.find(SystemEntity.class, 1L).getPointOfContact();
            if(null != defaultSys){
                List<SystemEntity> systems = getSystemListForUser(userRefId);
                if(!ShamenEJBUtil.isEmpty(systems)){
                    for(SystemEntity systemEntity : systems){
                        systemEntity.setPointOfContact(defaultSys);
                        em.merge(systemEntity);
                    }
                }
                List<BatchAppEntity> batches = getBatchListForUser(userRefId);
                if(!ShamenEJBUtil.isEmpty(batches)){
                    for(BatchAppEntity batch : batches){
                        batch.setPointOfContact(defaultSys);
                        em.merge(batch);
                    }
                }
                List<ApplicationEntity> webApps = getWebAppListForUser(userRefId);
                if(!ShamenEJBUtil.isEmpty(webApps)){
                    for(ApplicationEntity app : webApps){
                        app.setPointOfContact(defaultSys);
                        em.merge(app);
                    }
                }
            }
            if(null != model.getCommon()){
                // Set the delete indicator and update user reference ID/timestamp before merging.
                model.getCommon().setDeleteIndicator("Y");
                model.getCommon().setUpdateUserRefId(updateUserRefId);
                model.getCommon().setUpdateTime(new java.sql.Timestamp(System.currentTimeMillis()));
                em.merge(model);
            }else{
                logger.warn("The embedded common entity model is null in the authorized user entity. The delete fails!");
            }// end if
        }else{
            logger.warn("Unable to find the authorized user model with the entity manager! Record will not be deleted. Record primary key is " + String.valueOf(userRefId) + ", and the update user reference ID is " + String.valueOf(updateUserRefId));
        }// end if/else
        logger.debug("Exiting deleteAuthorizedUser");
    }// end deleteAuthorizedUser

}// end class
