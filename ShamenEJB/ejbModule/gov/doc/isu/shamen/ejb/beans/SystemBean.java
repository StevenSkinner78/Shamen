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
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;

import gov.doc.isu.shamen.ejb.beans.view.SystemBeanLocal;
import gov.doc.isu.shamen.jpa.entity.SystemEntity;

/**
 * This class is used to access the database via an entity manager to perform CRUD operations with the system table.
 *
 * @author Shane Duncan JCCC
 */
@Stateless
@Local(SystemBeanLocal.class)
public class SystemBean implements SystemBeanLocal {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Logger logger = Logger.getLogger("gov.doc.isu.shamen.ejb.beans.SystemBean");

    @PersistenceContext(unitName = "ShamenJPA")
    private EntityManager em;

    /**
     * This default constructor must be used to instantiate this class.
     */
    public SystemBean() {
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
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<Object> getSystemList() throws Exception {
        logger.debug("Entering getSystemList. This method is used to return a list of SystemEntity.");
        List<Object> list = em.createNativeQuery("SELECT s.System_Ref_Id,System_Nm,System_Desc,apps.appCount,batches.batchCount,users.user_first_nm,users.user_last_nm FROM Trans.Systems s left outer join (select s1.system_ref_id,COUNT(*) as appCount from Trans.Applications s1 where s1.Delete_Ind = 'N' group by s1.system_ref_id) apps on apps.system_ref_id = s.system_ref_id left outer join (select s2.system_ref_id,COUNT(*) as batchCount from Trans.Batch_Apps s2 where s2.Delete_Ind = 'N' group by s2.system_ref_id) batches on batches.system_ref_id = s.system_ref_id  left outer join trans.Authorized_users users on users.User_Ref_Id = s.Responsible_Staff_User_Ref_Id and users.Delete_Ind = 'N' where s.Delete_Ind='N' order by s.system_Nm ").getResultList();
        logger.debug("Exiting getSystemList. Return value is: " + String.valueOf(list));
        return list;
    }// end getSystemList

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<SystemEntity> getSystemListWithChildren() throws Exception {
        logger.debug("Entering getSystemList. This method is used to return a list of SystemEntity.");
        List<SystemEntity> list = em.createNamedQuery("SystemEntity.LOAD_SYSTEM_LIST", SystemEntity.class).getResultList();
        logger.debug("Exiting getControllersAndChildren. Found " + (list != null ? list.size() : "0") + " controllers.");
        return list;
    }// end getSystemList

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(SystemEntity entity) throws Exception {
        logger.debug("Entering create. This method is used to create an instance of an object with the database. Incoming parameter is: " + String.valueOf(entity));
        em.merge(entity);
        logger.debug("Exiting create");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(SystemEntity entity) throws Exception {
        logger.debug("Entering update. This method is used to update an instance of an object with the database. Incoming parameter is: " + String.valueOf(entity));
        SystemEntity system = em.find(SystemEntity.class, entity.getSystemRefId());
        system.setSystemName(entity.getSystemName());
        system.setSystemDesc(entity.getSystemDesc());
        system.setPointOfContact(entity.getPointOfContact());
        system.setCommon(entity.getCommon());
        em.merge(system);
        logger.debug("Exiting update");
    }// end update

    /**
     * {@inheritDoc}
     */
    @Override
    public SystemEntity findSystemByRefId(Long systemRefId) throws Exception {
        logger.debug("Entering findSystemByRefId. This method is used to return an instance of an object for viewing or for update. Incoming parameter is: " + String.valueOf(systemRefId));
        SystemEntity model = em.find(SystemEntity.class, systemRefId);
        // em.refresh(model);
        logger.debug("Exiting findSystemByRefId. Return value is: " + String.valueOf(model));
        return model;
    }// end findSystemByRefId

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteSystem(Long systemRefId, Long updateUserRefId) throws Exception {
        logger.debug("Entering deleteSystem. This method is used to 'soft' delete an instance of an object in the database. Incoming parameters are: systemRefId=" + String.valueOf(systemRefId) + ", updateUserRefId=" + String.valueOf(updateUserRefId));
        SystemEntity systemEntity = findSystemByRefId(systemRefId);
        SystemEntity defaultEntity = findSystemByRefId(1L);
        if(null != systemEntity){
            if(null != systemEntity.getCommon()){
                if(defaultEntity != null){
                    // change associated batch apps to default system
                    em.createNativeQuery("UPDATE Trans.Batch_Apps SET System_Ref_Id = ?, Update_User_Ref_Id = ?, Update_Ts = ? WHERE System_Ref_Id = ? AND Delete_Ind = 'N'").setParameter(1, 1L).setParameter(2, updateUserRefId).setParameter(3, new java.sql.Timestamp(System.currentTimeMillis())).setParameter(4, systemRefId).executeUpdate();
                    // change associated web apps to default system
                    em.createNativeQuery("UPDATE Trans.Applications SET System_Ref_Id = ?, Update_User_Ref_Id = ?, Update_Ts = ? WHERE System_Ref_Id = ? AND Delete_Ind = 'N'").setParameter(1, 1L).setParameter(2, updateUserRefId).setParameter(3, new java.sql.Timestamp(System.currentTimeMillis())).setParameter(4, systemRefId).executeUpdate();
                    // Set the delete indicator and update user reference ID/timestamp before merging.
                }
                systemEntity.getApps().clear();
                systemEntity.getBatchApps().clear();
                systemEntity.getCommon().setDeleteIndicator("Y");
                systemEntity.getCommon().setUpdateUserRefId(updateUserRefId);
                systemEntity.getCommon().setUpdateTime(new java.sql.Timestamp(System.currentTimeMillis()));
                em.merge(systemEntity);
            }else{
                logger.warn("The embedded common entity model is null in the system entity. The delete fails!");
            }// end if
        }else{
            logger.warn("Unable to find the systemEntity with the entity manager! Record will not be deleted. Record primary key is " + String.valueOf(systemRefId) + ", and the update user reference ID is " + String.valueOf(updateUserRefId));
        }// end if/else
        logger.debug("Exiting deleteSystem.");

    }// end deleteSystem
}// end class
