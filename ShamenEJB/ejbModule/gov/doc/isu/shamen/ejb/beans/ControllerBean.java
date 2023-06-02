/**
 *
 */
package gov.doc.isu.shamen.ejb.beans;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;

import gov.doc.isu.shamen.ejb.beans.view.ControllerBeanLocal;
import gov.doc.isu.shamen.jpa.entity.ControllerEntity;
import gov.doc.isu.shamen.jpa.entity.ControllerStatusCodeEntity;

/**
 * This class is used to access the database via an entity manager to perform CRUD operations with the controller table.
 *
 * @author Steven Skinner JCCC
 */
@Stateless
@Local(ControllerBeanLocal.class)
public class ControllerBean implements ControllerBeanLocal {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Logger logger = Logger.getLogger("gov.doc.isu.shamen.ejb.beans.ControllerBean");

    @PersistenceContext(unitName = "ShamenJPA")
    private EntityManager em;

    /**
     * This default constructor must be used to instantiate this class.
     */
    public ControllerBean() {
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
    public List<Object> getControllerList() throws Exception {
        logger.debug("Entering getControllerList. This method is used to return a list of ControllerEntity.");
        List<Object> list = em.createNativeQuery("SELECT Controller_Ref_Id,Controller_Address,Controller_Nm,c.Controller_Status_Cd,cd.Controller_Status_Desc,Default_Address FROM Trans.Controllers c join Code.Controller_Status_Codes cd on c.Controller_Status_Cd = cd.Controller_Status_Cd where c.Delete_Ind='N' order by c.Controller_Nm").getResultList();
        logger.debug("Exiting getControllerList. Return value is: " + String.valueOf(list));
        return list;
    }// end getControllerList

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Object> getControllerListForCode() throws Exception {
        logger.debug("Entering getControllerListForCode. This method is used to return a list of Objects.");
        List<Object> list = em.createNativeQuery("select controller_ref_id, controller_Nm, default_address from trans.controllers where delete_ind = 'N' order by controller_Nm").getResultList();
        logger.debug("Exiting getControllerListForCode. Return value is: " + String.valueOf(list));
        return list;
    }// end getControllerListForCode

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<ControllerEntity> getControllerListForUpdater() throws Exception {
        logger.debug("Entering getControllerListForUpdater. This method is used to return a list of controllers with only their refId's.");
        List<Object> list = em.createNativeQuery("select controller_Ref_Id,' ' from trans.controllers where delete_ind = 'N'").getResultList();

        List<ControllerEntity> returnList = new ArrayList<ControllerEntity>();
        for(int i = 0, j = list.size();i < j;i++){
            Object[] element = (Object[]) list.get(i);
            returnList.add(new ControllerEntity(Long.valueOf(element[0].toString())));
        }
        logger.debug("Exiting getControllerListForUpdater. Return value is: " + String.valueOf(returnList));
        return returnList;
    }// end getControllerListForUpdater

    /**
     * {@inheritDoc}
     */
    @Override
    public ControllerEntity findControllerByRefId(Long controllerRefId) throws Exception {
        logger.debug("Entering findControllerByRefId. This method is used to return an instance of an object for viewing or for update. Incoming parameter is: " + String.valueOf(controllerRefId));
        // em.clear();
        ControllerEntity model = em.find(ControllerEntity.class, controllerRefId);
//        em.refresh(model);
        // in order to insure that all refreshes cascade, do them manually. This was added because collection member xref was not refreshing from the controller level refresh
//        if(model.getBatchApps() != null){
//            for(int i = 0, j = model.getBatchApps().size();i < j;i++){
//                em.refresh(model.getBatchApps().get(i));
//            }// end for
//        }// end if
        logger.debug("Exiting findControllerByRefId. Return value is: " + String.valueOf(model));
        return model;
    }// end findControllerByRefId

    /**
     * {@inheritDoc}
     */
    @Override
    public ControllerEntity findControllerAddress(String address) throws Exception {
        logger.debug("Entering findControllerAddress. This method is used to return an instance of an object for viewing or for update. Incoming parameter is: " + String.valueOf(address));
        ControllerEntity model = em.createNamedQuery("ControllerEntity.FIND_BY_ADDRESS", ControllerEntity.class).setParameter("address", address).getSingleResult();
//        em.refresh(model);
        logger.debug("Exiting findControllerAddress. Return value is: " + String.valueOf(model));
        return model;
    }// end findControllerAddress

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ControllerEntity> getControllersAndChildren() throws Exception {
        logger.debug("Entering getControllersAndChildren. This method is used to load a list of controllers with all their child associations");
        List<ControllerEntity> controllers = em.createNamedQuery("ControllerEntity.LOAD_CONTROLLER_LIST", ControllerEntity.class).getResultList();
        logger.debug("Exiting getControllersAndChildren. Found " + (controllers != null ? controllers.size() : "0") + " controllers.");
        return controllers;
    }// end getControllersAndChildren

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(ControllerEntity entity) throws Exception {
        logger.debug("Entering update. This method is used to update an instance of an object with the database. Incoming parameter is: " + String.valueOf(entity));
        ControllerEntity controller = em.find(ControllerEntity.class, entity.getControllerRefId());
        controller.setControllerAddress(entity.getControllerAddress());
        controller.setControllerName(entity.getControllerName());
        controller.setDefaultAddress(entity.getDefaultAddress());
        controller.setCommon(entity.getCommon());
        em.merge(controller);
        logger.debug("Exiting update");
    }// end update

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSingleControllerStatus(Long controllerRefId, String status) throws Exception {
        logger.debug("Entering update. This method is used to update a single controller's status with the database. Incoming parameter is: " + String.valueOf(controllerRefId));
        ControllerEntity controller = em.find(ControllerEntity.class, controllerRefId);
        controller.setControllerStatusCodeEntity(new ControllerStatusCodeEntity(status, null));
        em.merge(controller);
        logger.debug("Exiting setSingleControllerStatus");
    }// end setSingleControllerStatus

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(ControllerEntity entity) throws Exception {
        logger.debug("Entering create. This method is used to create an instance of an object with the database. Incoming parameter is: " + String.valueOf(entity));
        em.merge(entity);
        logger.debug("Exiting create");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteController(Long controllerRefId, Long updateUserRefId) throws Exception {
        logger.debug("Entering deleteController. This method is used to 'soft' delete an instance of an object in the database. Incoming parameters are: controllerRefId=" + String.valueOf(controllerRefId) + ", updateUserRefId=" + String.valueOf(updateUserRefId));
        ControllerEntity model = findControllerByRefId(controllerRefId);
        if(null != model){
            if(null != model.getCommon()){
                // Set the delete indicator and update user reference ID/timestamp before merging.
                model.getCommon().setDeleteIndicator("Y");
                model.getCommon().setUpdateUserRefId(updateUserRefId);
                model.getCommon().setUpdateTime(new java.sql.Timestamp(System.currentTimeMillis()));
                em.merge(model);
            }else{
                logger.warn("The embedded common entity model is null in the controller entity. The delete fails!");
            }// end if
        }else{
            logger.warn("Unable to find the controller model  with the entity manager! Record will not be deleted. Record primary key is " + String.valueOf(controllerRefId) + ", and the update user reference ID is " + String.valueOf(updateUserRefId));
        }// end if/else
        logger.debug("Exiting deleteController");
    }// end deleteBatchApp

}// end class
