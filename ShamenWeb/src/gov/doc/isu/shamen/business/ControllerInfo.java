package gov.doc.isu.shamen.business;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import gov.doc.isu.dwarf.exception.BaseException;
import gov.doc.isu.dwarf.model.CodeModel;
import gov.doc.isu.dwarf.util.AppUtil;
import gov.doc.isu.shamen.convertor.ObjectConvertor;
import gov.doc.isu.shamen.ejb.beans.view.BatchAppBeanLocal;
import gov.doc.isu.shamen.ejb.beans.view.ControllerBeanLocal;
import gov.doc.isu.shamen.ejb.beans.view.ShamenScheduleBeanLocal;
import gov.doc.isu.shamen.jpa.entity.ControllerEntity;
import gov.doc.isu.shamen.models.BatchAppModel;
import gov.doc.isu.shamen.models.ControllerModel;

/**
 * This is the processor class for ControllerInfo.
 *
 * @author <strong>Steven L. Skinner</strong>
 */
public class ControllerInfo implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static Logger log = Logger.getLogger("gov.doc.isu.shamen.business.ControllerInfo");
    private static final ControllerInfo INSTANCE = new ControllerInfo();

    /**
     * This constructor made private to block instantiating this class.
     */
    private ControllerInfo() {
        super();
    }// end constructor

    /**
     * This method returns the private static final instance variable to enforce the Singleton pattern.
     *
     * @return An instance of this class.
     */
    public static ControllerInfo getInstance() {
        log.debug("Returning the static instance of the ControllerInfo class.");
        return INSTANCE;
    }// end getInstance

    /**
     * This method is called to use a JNDI lookup for accessing the {@link ControllerBeanLocal}.
     *
     * @return An instance of the <code>ControllerBeanLocal</code>.
     * @throws BaseException
     *         An exception if the <code>ControllerBeanLocal</code> class cannot be accessed.
     */
    public ControllerBeanLocal getBean() throws BaseException {
        log.debug("Entering ControllerInfo.getBean().");
        log.debug("This method is called to use a JNDI lookup for accessing the ControllerBeanLocal.");
        ControllerBeanLocal bean;
        try{
            Context context = new InitialContext();
            bean = (ControllerBeanLocal) context.lookup("java:global/ShamenEAR/ShamenEJB/ControllerBean!gov.doc.isu.shamen.ejb.beans.view.ControllerBeanLocal");
        }catch(NamingException e){
            log.error("An exception occurred while attempting to access the ControllerBean from the ShamenServiceBean. Message is: " + e.getMessage());
            throw new BaseException(e.getMessage());
        }// end try/catch
        log.debug("Exiting ControllerInfo.getBean. Return value is: " + String.valueOf(bean));
        return bean;
    }// end getBean

    /**
     * Loads a {@link ControllerEntity} without converting to model
     *
     * @param refId
     *        the ref id of the controller
     * @return controller
     * @throws BaseException
     *         if an exception occurred
     */
    public ControllerEntity getControllerEntity(Long refId) throws BaseException {
        log.debug("Entering ControllerInfo.getControllerEntity(). Incoming parameter is: " + String.valueOf(refId));
        log.debug("This method is used to load an ControllerEntity without converting to model.");
        ControllerEntity entity = null;
        try{
            entity = (ControllerEntity) getBean().findControllerByRefId(refId);
        }catch(Exception e){
            log.error("An exception occurred while getting controller entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Exiting ControllerInfo.getControllerEntity");
        return entity;
    }// end getControllerEntity

    /**
     * Loads a List of {@link ControllerModel} for display.
     *
     * @return list of controllers
     * @throws BaseException
     *         if an exception occurred
     */
    public List<ControllerModel> getControllerList() throws BaseException {
        log.debug("Entering ControllerInfo.getControllerList().");
        log.debug("This method is used to load a List of ControllerModel for display.");
        List<ControllerModel> list = new ArrayList<ControllerModel>();
        try{
            list = ObjectConvertor.toControllerListBusinessFromObj(getBean().getControllerList());
        }catch(Exception e){
            log.error("An exception occurred while getting controller list entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
         // try{
         // if(null != entities && !entities.isEmpty()){
         // ControllerModel model;
         // BatchAppModel batch;
         // BatchAppEntity ent;
         // for(int i = 0, j = entities.size();i < j;i++){
         // model = ObjectConvertor.toControllerBusiness(entities.get(i));
         // // model.setStatus(getControllerStatusBean().getSingleControllerStatus(model.getControllerRefId()));
         // model.setBatchApps(new ArrayList<BatchAppModel>());
         // if(!AppUtil.isEmpty(entities.get(i).getBatchApps())){
         // for(Iterator<BatchAppEntity> iter = entities.get(i).getBatchApps().iterator();iter.hasNext();){
         // ent = iter.next();
         // batch = new BatchAppModel(ent.getBatchAppRefId());
         // batch.setName(ent.getBatchName());
         // batch.setType(ent.getBatchType().getBatchTypeCd());
         // batch.setSchedule(ent.getSchedule() == null ? new ScheduleModel() : new ScheduleModel(ent.getSchedule().getScheduleRefId(), ent.getSchedule().getActiveInd()));
         // model.addBatchApp(batch);
         // }// end for
         // }// end if
         // list.add(model);
         // }// end for
         // }else{
         // log.warn("The controller list in the database is null or empty. An empty list of controller models will be returned.");
         // }// end if/else
         // }catch(Exception e){
         // log.error("An exception occurred while getting controller list entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
         // throw new BaseException(e.getMessage(), e);
         // }// end try/catch
        log.debug("Exiting ControllerInfo.getControllerList controllerList.size=" + (AppUtil.isEmpty(list) ? "empty" : list.size()));
        return list;
    } // end getControllerList
      //
      // /**
      // * Loads a List of {@link ControllerModel} for display.
      // *
      // * @return list of controllers
      // * @throws BaseException
      // * if an exception occurred
      // */
      // public List<ControllerModel> getControllerList() throws BaseException {
      // log.debug("Entering ControllerInfo.getControllerList().");
      // log.debug("This method is used to load a List of ControllerModel for display.");
      // List<ControllerModel> controllerList = new ArrayList<ControllerModel>();
      // try{
      // controllerList = ObjectConvertor.toRunStatusListBusinessFromObj(getBean().getControllerList());
      // }catch(Exception e){
      // log.error("An exception occurred while getting run status list entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
      // throw new BaseException(e.getMessage(), e);
      // }// end try/catch
      // log.debug("Exiting ControllerInfo.getControllerList runStatusList.size=" + (AppUtil.isEmpty(runList) ? "empty" : runList.size()));
      // return runList;
      // } // end getControllerList

    /**
     * This method is called to use a JNDI lookup for accessing the {@link BatchAppBeanLocal}.
     *
     * @return An instance of the <code>BatchAppBeanLocal</code>.
     * @throws BaseException
     *         An exception if the <code>BatchAppBeanLocal</code> class cannot be accessed.
     */
    public BatchAppBeanLocal getBatchBean() throws BaseException {
        log.debug("Entering ControllerInfo.getBean().");
        log.debug("This method is called to use a JNDI lookup for accessing the BatchAppBeanLocal.");
        BatchAppBeanLocal bean;
        try{
            Context context = new InitialContext();
            bean = (BatchAppBeanLocal) context.lookup("java:global/ShamenEAR/ShamenEJB/BatchAppBean!gov.doc.isu.shamen.ejb.beans.view.BatchAppBeanLocal");
        }catch(NamingException e){
            log.error("An exception occurred while attempting to access the BatchAppBean from the ShamenServiceBean. Message is: " + e.getMessage());
            throw new BaseException(e.getMessage());
        }// end try/catch
        log.debug("Exiting ControllerInfo.getBean(). Return value is: " + String.valueOf(bean));
        return bean;
    }// end getBean

    /**
     * Loads a {@link ControllerModel} for display.
     *
     * @param refId
     *        the ref id of the controller
     * @return controller
     * @throws BaseException
     *         if an exception occurred
     */
    public ControllerModel getController(Long refId) throws BaseException {
        log.debug("Entering ControllerInfo.getController(). Incoming parameter is: " + String.valueOf(refId));
        log.debug("This method is used to load an ControllerModel for edit.");
        ControllerEntity entity = null;
        ControllerModel model = new ControllerModel();
        List<BatchAppModel> batchAppList = new ArrayList<BatchAppModel>();
        try{
            entity = (ControllerEntity) getBean().findControllerByRefId(refId);
        }catch(Exception e){
            log.error("An exception occurred while getting controller entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        try{
            if(null != entity){
                entity.setBatchApps(getBatchBean().checkOnSchedule(entity.getBatchApps()));
                model = ObjectConvertor.toControllerBusiness(entity);
                batchAppList = ObjectConvertor.toBatchAppListBusiness(entity.getBatchApps());
                model.setBatchApps(batchAppList);
            }else{
                log.warn("The controller entity in the database is null or empty. An new instatnce of controller model will be returned.");
            }// end if/else
        }catch(Exception e){
            log.error("An exception occurred while converting controller model. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Exiting ControllerInfo.getController controller=" + String.valueOf(model));
        return model;
    }// end getController

    /**
     * Loads a List of {@link CodeModel} for display.
     *
     * @param isListScreen
     *        boolean check for if this is a list screen. Will add default value for if this is a list screen.
     * @return list of controllers
     * @throws BaseException
     *         if an exception occurred
     */
    public List<ControllerModel> getControllerListAsCodeModel(boolean isListScreen) throws BaseException {
        log.debug("Entering ControllerInfo.getControllerListAsCodeModel(). ");
        log.debug("This method is used to load a List of Controllers as CodeModel for select box.");
        List<ControllerModel> list = null;
        try{
            list = ObjectConvertor.convertControllerToCodeModel(getBean().getControllerListForCode());
            if(!AppUtil.isEmpty(list) && isListScreen){
                list.add(new ControllerModel("UNASSIGNED", "UNASSIGNED"));
            }// end if
        }catch(Exception e){
            log.error("An exception occurred while getting controller list entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch

        log.debug("Exiting ControllerInfo.getControllerListAsCodeModel list.size=" + (AppUtil.isEmpty(list) ? "empty" : list.size()));
        return list;
    } // end getControllerListAsCodeModel

    /**
     * Returns the Default Batch Location for a Controller ref id
     *
     * @param list
     *        list of controllers
     * @param controllerRefId
     *        the ref id for the controller selected
     * @return String
     * @throws BaseException
     *         if an exception occurred
     */
    public String getDefaultLocation(List<ControllerModel> list, Long controllerRefId) throws BaseException {
        log.debug("Entering ControllerInfo.getDefaultLocation(). Incoming parameter is: " + String.valueOf(controllerRefId));
        log.debug("This method is used to return the Default Batch Location for a Controller ref id");
        String result = "";
        try{
            ControllerModel model = null;
            Iterator<ControllerModel> it = list.iterator();
            while(it.hasNext()){
                model = (ControllerModel) it.next();
                if(Long.valueOf(model.getCode()).equals(controllerRefId)){
                    result = model.getDefaultAddress();
                    break;
                }// end if
            }// end while
        }catch(Exception e){
            log.error("An exception occurred while getting controller list entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch

        log.debug("Exiting ControllerInfo.getDefaultLocation result=" + result);
        return result;
    } // end getDefaultLoaction

    /**
     * Loads a List of {@link CodeModel} for display.
     *
     * @param refId
     *        the ref id of the controller
     * @return List<CodeModel>
     * @throws BaseException
     *         if an exception occurred
     */
    public List<CodeModel> getBatchAppListAsCodeModel(Long refId) throws BaseException {
        log.debug("Entering ControllerInfo.getBatchAppListAsCodeModel().");
        log.debug("This method is used to load a List of Controllers as CodeModel for select box.");
        List<CodeModel> list = new ArrayList<CodeModel>();
        try{
            ControllerEntity entity = getControllerEntity(refId);
            list = ObjectConvertor.convertBatchToCodeModel(entity.getBatchApps());
        }catch(Exception e){
            log.error("An exception occurred while getBatchAppListAsCodeModel. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Exiting ControllerInfo.getBatchAppListAsCodeModel list.size=" + (AppUtil.isEmpty(list) ? "empty" : list.size()));
        return list;
    } // end getBatchAppListAsCodeModel

    /**
     * Saves new controller
     *
     * @param model
     *        the controller model to save
     * @throws BaseException
     *         if an exception occurred
     */
    public void saveController(ControllerModel model) throws BaseException {
        log.debug("Entering ControllerInfo.saveController(). Incoming parameter is: " + (model == null ? "null" : model.toString()));
        log.debug("This method is used to add an ControllerModel.");
        model.setDeleteIndicator("N");
        ControllerEntity entity = ObjectConvertor.toControllerPersist(model);
        try{
            getBean().create(entity);
            // getControllerStatusBean().addControllerToMap(entity);
        }catch(Exception e){
            log.error("An exception occurred while saving an controller entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Exiting ControllerInfo.saveController");
    } // end saveController

    /**
     * Updates a existing controller record
     *
     * @param model
     *        the controller model to save
     * @throws BaseException
     *         if an exception occurred
     */
    public void updateController(ControllerModel model) throws BaseException {
        log.debug("Entering ControllerInfo.updateController(). Incoming parameter is: " + String.valueOf(model));
        log.debug("This method is used to Update an existing Controller record.");
        ControllerEntity entity = ObjectConvertor.toControllerPersist(model);
        try{
            getBean().update(entity);
        }catch(Exception e){
            log.error("An exception occurred while updating an controller entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Exiting ControllerInfo.updateController");
    } // end updateController

    /**
     * Delete a existing controller record
     *
     * @param controllerRefId
     *        the refId of controller to delete.
     * @param updateUserRefId
     *        the refid of user deleteing record
     * @throws BaseException
     *         if an exception occurred
     */
    public void deleteController(Long controllerRefId, Long updateUserRefId) throws BaseException {
        log.debug("Entering ControllerInfo.deleteController(). Incoming parameters are: controllerRefId=" + String.valueOf(controllerRefId) + ", updateUserRefId=" + String.valueOf(updateUserRefId));
        log.debug("This method is used to delete an Controller record.");
        try{
            getBean().deleteController(controllerRefId, updateUserRefId);
            // getControllerStatusBean().removeControllerFromMap(controllerRefId);
        }catch(Exception e){
            log.error("An exception occurred while deleting an controller entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Exiting ControllerInfo.deleteController");
    } // end deleteController

    /**
     * Used to kick off the asynchronous status update.
     *
     * @throws BaseException
     *         if an exception occurred
     */
    public void updateControllerStatuses() throws BaseException {
        log.debug("Entering ApplicationInfo.updateApplicationStatuses().");
        log.debug("Used to kick off the asynchronous status update.");
        try{
            getScheduleBean().establishAllControllerStatuses();
        }catch(Exception e){
            log.error("An exception occurred while updating application statuses. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Entering ControllerInfo.updateControllerStatuses().");
    }// end updateApplicationStatuses

    /**
     * Used to kick off the asynchronous status update for a single controller.
     *
     * @param controllerRefId
     *        the refid of controller
     * @throws BaseException
     *         if an exception occurred
     */
    public void updateSingleControllerStatus(Long controllerRefId) throws BaseException {
        log.debug("Entering ApplicationInfo.updateSingleControllerStatus(). Incoming parameters are: controllerRefId=" + String.valueOf(controllerRefId));
        log.debug("Used to kick off the asynchronous status update for a single controller.");
        try{
            getScheduleBean().establishSingleControllerStatus(controllerRefId);
        }catch(Exception e){
            log.error("An exception occurred while updating application status. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Entering ControllerInfo.updateSingleControllerStatus().");
    }// end updateApplicationStatuses

    /**
     * This method is called to use a JNDI lookup for accessing the <code>ShamenScheduleBean</code>
     *
     * @return An instance of the <code>ShamenScheduleBean</code>.
     * @throws BaseException
     *         An exception if the <code>ShamenScheduleBean</code> class cannot be accessed.
     */
    public ShamenScheduleBeanLocal getScheduleBean() throws BaseException {
        log.debug("Entering ApplicationInfo.getScheduleBean().");
        log.debug("This method is called to use a JNDI lookup for accessing the ShamenScheduleBean.");
        ShamenScheduleBeanLocal bean;
        try{
            Context context = new InitialContext();
            bean = (ShamenScheduleBeanLocal) context.lookup("java:global/ShamenEAR/ShamenEJB/ShamenScheduleBean!gov.doc.isu.shamen.ejb.beans.view.ShamenScheduleBeanLocal");
        }catch(NamingException e){
            log.error("An exception occurred while attempting to access the ShamenScheduleBean from the ShamenServiceBean. Message is: " + e.getMessage());
            throw new BaseException(e.getMessage());
        }// end try/catch
        log.debug("Exiting ApplicationInfo.getScheduleBean(). Return: bean=" + (null != bean ? String.valueOf(bean) : null));
        return bean;
    }// end getBean

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

} // end ControllerInfo
