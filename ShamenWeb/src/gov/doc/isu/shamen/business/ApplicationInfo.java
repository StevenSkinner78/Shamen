package gov.doc.isu.shamen.business;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import gov.doc.isu.dwarf.exception.BaseException;
import gov.doc.isu.dwarf.model.CodeModel;
import gov.doc.isu.dwarf.util.AppUtil;
import gov.doc.isu.shamen.convertor.ObjectConvertor;
import gov.doc.isu.shamen.ejb.beans.view.ApplicationBeanLocal;
import gov.doc.isu.shamen.ejb.beans.view.ApplicationStatusUpdaterBeanLocal;
import gov.doc.isu.shamen.ejb.beans.view.ShamenScheduleBeanLocal;
import gov.doc.isu.shamen.jpa.entity.ApplicationEntity;
import gov.doc.isu.shamen.jpa.entity.ApplicationStatusCodeEntity;
import gov.doc.isu.shamen.models.ApplicationModel;
import gov.doc.isu.shamen.models.AuthorizedUserModel;
import gov.doc.isu.shamen.models.SystemModel;

/**
 * This is the processor class for ApplicationInfo.
 *
 * @author <strong>Brian Hicks</strong> JCCC, Mar 7, 2016
 * @author <strong>Steven Skinner</strong> JCCC, Aug 16, 2016
 */
public class ApplicationInfo implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static Logger log = Logger.getLogger("gov.doc.isu.shamen.business.ApplicationInfo");
    private static final ApplicationInfo INSTANCE = new ApplicationInfo();

    /**
     * This constructor made private to block instantiating this class.
     */
    private ApplicationInfo() {
        super();
    }// end constructor

    /**
     * This method returns the private static final instance variable to enforce the Singleton pattern.
     *
     * @return An instance of this class.
     */
    public static ApplicationInfo getInstance() {
        log.debug("Returning the static instance of the ApplicationInfo class.");
        return INSTANCE;
    }// end getInstance

    /**
     * This method is called to use a JNDI lookup for accessing the <code>ApplicationBean</code>
     *
     * @return An instance of the <code>ApplicationBean</code>.
     * @throws BaseException
     *         An exception if the <code>ApplicationBean</code> class cannot be accessed.
     */
    public ApplicationBeanLocal getBean() throws BaseException {
        log.debug("Entering ApplicationInfo.getBean().");
        log.debug("This method is called to use a JNDI lookup for accessing the ApplicationBean.");
        ApplicationBeanLocal bean;
        try{
            Context context = new InitialContext();
            bean = (ApplicationBeanLocal) context.lookup("java:global/ShamenEAR/ShamenEJB/ApplicationBean!gov.doc.isu.shamen.ejb.beans.view.ApplicationBeanLocal");
        }catch(NamingException e){
            log.error("An exception occurred while attempting to access the ApplicationBean from the ShamenServiceBean. Message is: " + e.getMessage());
            throw new BaseException(e.getMessage());
        }// end try/catch
        log.debug("Exiting ApplicationInfo.getBean(). Return: bean=" + (null != bean ? String.valueOf(bean) : null));
        return bean;
    }// end getBean

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
     * This method is called to use a JNDI lookup for accessing the <code>ApplicationStatusUpdaterBeanLocal</code>
     *
     * @return An instance of the <code>ApplicationStatusUpdaterBeanLocal</code>.
     * @throws BaseException
     *         An exception if the <code>ApplicationStatusUpdaterBeanLocal</code> class cannot be accessed.
     */
    public ApplicationStatusUpdaterBeanLocal getStatusUpdaterBean() throws BaseException {
        log.debug("Entering ApplicationInfo.getScheduleBean().");
        log.debug("This method is called to use a JNDI lookup for accessing the ApplicationStatusUpdaterBeanLocal.");
        ApplicationStatusUpdaterBeanLocal bean;
        try{
            Context context = new InitialContext();
            bean = (ApplicationStatusUpdaterBeanLocal) context.lookup("java:global/ShamenEAR/ShamenEJB/ApplicationStatusUpdaterBean!gov.doc.isu.shamen.ejb.beans.view.ApplicationStatusUpdaterBeanLocal");
        }catch(NamingException e){
            log.error("An exception occurred while attempting to access the ApplicationStatusUpdaterBeanLocal from the ShamenServiceBean. Message is: " + e.getMessage());
            throw new BaseException(e.getMessage());
        }// end try/catch
        log.debug("Exiting ApplicationInfo.getScheduleBean(). Return: bean=" + (null != bean ? String.valueOf(bean) : null));
        return bean;
    }// end getStatusUpdaterBean

    /**
     * Used to load a {@link ApplicationModel} for display.
     *
     * @param appRefId
     *        The refId of the application
     * @return ApplicationModel
     * @throws BaseException
     *         if an exception occurred
     */
    public ApplicationModel getApplication(Long appRefId) throws BaseException {
        log.debug("Entering ApplicationInfo.getApplication(). Parameter: appRefId=" + (null != appRefId ? String.valueOf(appRefId) : null));
        log.debug("Used to load a ApplicationModel for display.");
        ApplicationEntity entity = null;
        ApplicationModel model = new ApplicationModel();
        try{
            entity = (ApplicationEntity) getBean().findApplicationByRefId(appRefId);
        }catch(Exception e){
            log.error("An exception occurred while getting application entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        try{
            if(null != entity){
                model = ObjectConvertor.toApplicationBusiness(entity);
                model.setSystem(entity.getSystem() == null ? new SystemModel() : ObjectConvertor.toSystemBusiness(entity.getSystem()));
                model.setPointOfContact(entity.getPointOfContact() == null ? new AuthorizedUserModel() : ObjectConvertor.toAuthorizedUserBusiness(entity.getPointOfContact()));
                model.setBatchApps(ObjectConvertor.toBatchAppListBusiness(entity.getBatchApps()));
            }else{
                log.warn("The application entity in the database is null or empty. An new instatnce of an application model will be returned.");
            }// end if/else
        }catch(Exception e){
            log.error("An exception occurred while converting the application model. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Exiting ApplicationInfo.getApplication(). Return: model=" + (null != model ? String.valueOf(model) : null));
        return model;
    }// end getApplication

    /**
     * Loads a List of {@link ApplicationModel} for display.
     *
     * @return appList list of ApplicationModel
     * @throws BaseException
     *         if an exception occurred
     */
    public List<ApplicationModel> getApplicationList() throws BaseException {
        log.debug("Entering ApplicationInfo.getApplicationList().");
        log.debug("This method is used to load a List of ApplicationModel for display.");
        List<ApplicationModel> appList = new ArrayList<ApplicationModel>();
        List<ApplicationEntity> entities = null;
        try{
            entities = (List<ApplicationEntity>) getBean().getApplicationList();
        }catch(Exception e){
            log.error("An exception occurred while getting application list entities. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        try{
            if(null != entities && !entities.isEmpty()){
                appList = ObjectConvertor.convertForApplicationList(entities);
            }else{
                log.warn("The application list in the database is null or empty. An empty list of application models will be returned.");
            }// end if/else
        }catch(Exception e){
            log.error("An exception occurred while getting application list entities. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Exiting ApplicationInfo.getApplicationList(). Return: appList. appList.size()=" + (AppUtil.isEmpty(appList) ? "empty" : appList.size()));
        return appList;
    } // end getApplicationList

    /**
     * Used to save a new {@link ApplicationModel}.
     *
     * @param model
     *        The ApplicationModel to save.
     * @throws BaseException
     *         if an exception occurred
     */
    public void saveApplication(ApplicationModel model) throws BaseException {
        log.debug("Entering ApplicationInfo.saveApplication(). Parameter: model=" + (null != model ? String.valueOf(model) : null));
        log.debug("Used to save a new ApplicationModel.");
        ApplicationEntity entity = null;
        try{
            if(null != model){
                model.setDeleteIndicator("N");
                entity = ObjectConvertor.toApplicationPersist(model);
                entity = getBean().create(entity);
                sendStatusUpdateToApplication(entity);
            }// end if
        }catch(Exception e){
            log.error("An exception occurred while saving a new application entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Exiting ApplicationInfo.saveApplication().");
    }// end saveApplication

    /**
     * Used to update an existing {@link ApplicationModel} record.
     *
     * @param model
     *        The ApplicationModel to update.
     * @throws BaseException
     *         if an exception occurred
     */
    public void updateApplication(ApplicationModel model) throws BaseException {
        log.debug("Entering ApplicationInfo.updateApplication(). Parameter: model=" + (null != model ? String.valueOf(model) : null));
        log.debug("Used to update an existing ApplicationModel record.");
        ApplicationEntity entity = null;
        try{
            if(null != model){
                model.setDeleteIndicator("N");
                entity = ObjectConvertor.toApplicationPersist(model);
                getBean().update(entity);
                sendStatusUpdateToApplication(entity);
            }// end if
        }catch(Exception e){
            log.error("An exception occurred while updating an application entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Exiting ApplicationInfo.updateApplication().");
    }// end updateApplication

    /**
     * This method sends a status update to the application via the ApplicationStatusUpdaterBean
     *
     * @param entity
     *        ApplicationEntity
     */
    private void sendStatusUpdateToApplication(ApplicationEntity entity) {
        log.debug("Entering ApplicationInfo.sendStatusUpdateToApplication(). Parameter: entity=" + (null != entity ? String.valueOf(entity) : null));
        log.debug("This method sends a status update to the application via the ApplicationStatusUpdaterBean.");
        try{
            getStatusUpdaterBean().resetApplicationStatusAsync(entity);
            // Rest for 3 seconds to give the apps time to register their instances. This is to insure that the screen will reflect reconnection status and go into auto update.
            TimeUnit.SECONDS.sleep(3);
        }catch(BaseException e){
            log.error("Exception encountered trying to send a status update to application: " + String.valueOf(entity), e);
        }catch(Exception e){
            log.error("Exception encountered trying to send a status update to application: " + String.valueOf(entity), e);
        }// end try/catch
        log.debug("Exiting ApplicationInfo.sendStatusUpdateToApplication().");
    }// end sendStatusUpdateToApplication

    /**
     * Used to delete an existing application record.
     *
     * @param applicationRefId
     *        the refid of app to delete
     * @param userRefId
     *        the refid of user deleting record
     * @throws BaseException
     *         if an exception occurred
     */
    public void deleteApplication(Long applicationRefId, Long userRefId) throws BaseException {
        log.debug("Entering ApplicationInfo.deleteApplication(). Parameters: " + new Object[]{applicationRefId, userRefId});
        log.debug("Used to delete an existing application record.");
        try{
            getBean().deleteApplication(applicationRefId, userRefId);
        }catch(Exception e){
            log.error("An exception occurred while deleting an application entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Entering ApplicationInfo.deleteApplication().");
    }// end deleteApplication

    /**
     * Used to kick off the asynchronous status update.
     *
     * @throws BaseException
     *         if an exception occurred
     */
    public void updateApplicationStatuses() throws BaseException {
        log.debug("Entering ApplicationInfo.updateApplicationStatuses().");
        log.debug("Used to kick off the asynchronous status update.");
        try{
            getStatusUpdaterBean().setAllApplicationInstanceStatus(ApplicationStatusUpdaterBeanLocal.STATUS_AWAITING_RESPONSE);
            getStatusUpdaterBean().resetAllApplicationStatusesAsync();
            // Rest for 3 seconds to give the apps time to register their instances. This is to insure that the screen will reflect reconnection status and go into auto update.
            TimeUnit.SECONDS.sleep(3);
        }catch(Exception e){
            log.error("An exception occurred while updating application statuses. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Entering ApplicationInfo.updateApplicationStatuses().");
    }// end updateApplicationStatuses

    /**
     * Used to kick off the asynchronous status update for a single application.
     *
     * @param applicationRefId
     *        the refid of app to delete
     * @throws BaseException
     *         if an exception occurred
     */
    public void refreshSingleApplicationStatus(Long applicationRefId) throws BaseException {
        log.debug("Entering ApplicationInfo.refreshSingleApplicationStatus().");
        log.debug("Used to kick off the asynchronous status update.");
        try{
            sendStatusUpdateToApplication(getBean().findApplicationByRefId(applicationRefId));
        }catch(Exception e){
            log.error("An exception occurred while updating application statuses. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Entering ApplicationInfo.refreshSingleApplicationStatus().");
    }// end refreshSingleApplicationStatus

    /**
     * Used to update all applications by setting stausInd to passed in status.
     *
     * @param statusInd
     *        the status indicator value
     * @param userRefId
     *        the refId of user updating records
     * @throws BaseException
     *         if an exception occurred
     */
    public void updateAllApps(ApplicationStatusCodeEntity statusInd, Long userRefId) throws BaseException {
        log.debug("Entering ApplicationInfo.updateAllApps(). Parameters: " + new Object[]{statusInd, userRefId});
        log.debug("Used to update all applications by setting stausInd to passed in status.");
        try{
            getBean().updateAllApps(statusInd, userRefId);
        }catch(Exception e){
            log.error("An exception occurred while activating/suspending all applications. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Exiting ApplicationInfo.updateAllApps().");
    }// end updateAllApps

    /**
     * Loads a List of {@link CodeModel} for display.
     *
     * @return list of controllers
     * @throws BaseException
     *         if an exception occurred
     */
    public List<CodeModel> getApplicationListAsCodeModel() throws BaseException {
        log.debug("Entering ApplicationInfo.getApplicationListAsCodeModel().");
        log.debug("This method is used to load a List of Applications as CodeModel for select box.");
        List<CodeModel> list = null;
        try{

            list = ObjectConvertor.convertApplicationToCodeModel(getBean().getApplicationListForCode());
            if(!AppUtil.isEmpty(list)){
                list.add(new CodeModel("UNASSIGNED", "UNASSIGNED"));
            }// end if
        }catch(Exception e){
            log.error("An exception occurred while getting application list entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Exiting ApplicationInfo.getApplicationListAsCodeModel list.size=" + (AppUtil.isEmpty(list) ? "empty" : list.size()));
        return list;
    } // end getApplicationListAsCodeModel

    /**
     * Loads a List of {@link CodeModel} for display.
     *
     * @param systemRefId
     *        the ref id of the System
     * @return list of controllers
     * @throws BaseException
     *         if an exception occurred
     */
    public List<CodeModel> getApplicationListForSystemAsCodeModel(Long systemRefId) throws BaseException {
        log.debug("Entering ApplicationInfo.getApplicationListForSystemAsCodeModel(). Parameters: " + new Object[]{systemRefId});
        log.debug("This method is used to load a List of Applications as CodeModel for select box.");
        List<CodeModel> list = null;
        try{
            list = ObjectConvertor.convertApplicationToCodeModel(getBean().getApplicationListBySystemForCode(systemRefId));
        }catch(Exception e){
            log.error("An exception occurred while getting application list entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Exiting ApplicationInfo.getApplicationListForSystemAsCodeModel list.size=" + (AppUtil.isEmpty(list) ? "empty" : list.size()));
        return list;
    } // end getApplicationListAsCodeModel

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
}// end ApplicationInfo
