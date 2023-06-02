package gov.doc.isu.shamen.business;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import gov.doc.isu.dwarf.exception.BaseException;
import gov.doc.isu.dwarf.model.CommonModel;
import gov.doc.isu.dwarf.util.AppUtil;
import gov.doc.isu.dwarf.util.StringUtil;
import gov.doc.isu.shamen.convertor.ObjectConvertor;
import gov.doc.isu.shamen.ejb.beans.view.AuthorizedUserBeanLocal;
import gov.doc.isu.shamen.ejb.beans.view.BatchAppBeanLocal;
import gov.doc.isu.shamen.ejb.beans.view.CodeBeanLocal;
import gov.doc.isu.shamen.ejb.beans.view.ControllerBeanLocal;
import gov.doc.isu.shamen.ejb.beans.view.ShamenScheduleBeanLocal;
import gov.doc.isu.shamen.interfaces.Scheduleable;
import gov.doc.isu.shamen.jms.beans.view.JmsManagerBeanLocal;
import gov.doc.isu.shamen.jms.convertor.JmsObjectConvertor;
import gov.doc.isu.shamen.jms.models.JmsController;
import gov.doc.isu.shamen.jms.models.JmsRunStatus;
import gov.doc.isu.shamen.jpa.entity.BatchAppEntity;
import gov.doc.isu.shamen.jpa.entity.ControllerEntity;
import gov.doc.isu.shamen.jpa.entity.RunStatusEntity;
import gov.doc.isu.shamen.jpa.entity.ScheduleEntity;
import gov.doc.isu.shamen.models.ApplicationModel;
import gov.doc.isu.shamen.models.AuthorizedUserModel;
import gov.doc.isu.shamen.models.BatchAppCollectionModel;
import gov.doc.isu.shamen.models.BatchAppModel;
import gov.doc.isu.shamen.models.ControllerModel;
import gov.doc.isu.shamen.models.RunStatusModel;
import gov.doc.isu.shamen.models.ScheduleModel;
import gov.doc.isu.shamen.models.SystemModel;
import gov.doc.isu.shamen.resources.AppConstants;
import gov.doc.isu.shamen.util.ShamenUtil;
import net.sf.json.JSONArray;

/**
 * This is the processor class for BatchAppInfo.
 *
 * @author <strong>Steven L. Skinner</strong>
 */
public class BatchAppInfo implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static Logger log = Logger.getLogger("gov.doc.isu.shamen.business.BatchAppInfo");
    private static final BatchAppInfo INSTANCE = new BatchAppInfo();

    /**
     * This constructor made private to block instantiating this class.
     */
    private BatchAppInfo() {
        super();
    }// end constructor

    /**
     * This method returns the private static final instance variable to enforce the Singleton pattern.
     *
     * @return An instance of this class.
     */
    public static BatchAppInfo getInstance() {
        log.debug("Returning the static instance of the BatchAppInfo class.");
        return INSTANCE;
    }// end getInstance

    /**
     * This method is called to use a JNDI lookup for accessing the {@link BatchAppBeanLocal}.
     *
     * @return An instance of the <code>BatchAppBeanLocal</code>.
     * @throws BaseException
     *         An exception if the <code>BatchAppBeanLocal</code> class cannot be accessed.
     */
    public BatchAppBeanLocal getBean() throws BaseException {
        log.debug("Entering BatchAppInfo.getBean().");
        log.debug("This method is called to use a JNDI lookup for accessing the BatchAppBeanLocal.");
        BatchAppBeanLocal bean;
        try{
            Context context = new InitialContext();
            bean = (BatchAppBeanLocal) context.lookup("java:global/ShamenEAR/ShamenEJB/BatchAppBean!gov.doc.isu.shamen.ejb.beans.view.BatchAppBeanLocal");
        }catch(NamingException e){
            log.error("An exception occurred while attempting to access the BatchAppBean from the ShamenServiceBean. Message is: " + e.getMessage());
            throw new BaseException(e.getMessage());
        }// end try/catch
        log.debug("Exiting BatchAppInfo.getBean(). Return value is: " + String.valueOf(bean));
        return bean;
    }// end getBean

    /**
     * This method is called to use a JNDI lookup for accessing the {@link AuthorizedUserBeanLocal}.
     *
     * @return An instance of the <code>AuthorizedUserBeanLocal</code>.
     * @throws BaseException
     *         An exception if the <code>AuthorizedUserBeanLocal</code> class cannot be accessed.
     */
    public AuthorizedUserBeanLocal getAuthorizedUserBean() throws BaseException {
        log.debug("Entering BatchAppInfo.getAuthorizedUserBean().");
        log.debug("This method is called to use a JNDI lookup for accessing the AuthorizedUserBeanLocal.");
        AuthorizedUserBeanLocal authBean;
        try{
            Context context = new InitialContext();
            authBean = (AuthorizedUserBeanLocal) context.lookup("java:global/ShamenEAR/ShamenEJB/AuthorizedUserBean!gov.doc.isu.shamen.ejb.beans.view.AuthorizedUserBeanLocal");
        }catch(NamingException e){
            log.error("An exception occurred while attempting to access the AuthorizedUserBean from the ShamenServiceBean. Message is: " + e.getMessage());
            throw new BaseException(e.getMessage());
        }// end try/catch
        log.debug("Exiting BatchAppInfo.getAuthorizedUserBean. Return value is: " + String.valueOf(authBean));
        return authBean;
    }// end getAuthorizedUserBean

    /**
     * This method is called to use a JNDI lookup for accessing the {@link ShamenScheduleBeanLocal}.
     *
     * @return An instance of the <code>ShamenScheduleBeanLocal</code>.
     * @throws BaseException
     *         An exception if the <code>ShamenScheduleBeanLocal</code> class cannot be accessed.
     */
    public ShamenScheduleBeanLocal getScheduleBean() throws BaseException {
        log.debug("Entering BatchAppInfo.getBean().");
        log.debug("This method is called to use a JNDI lookup for accessing the BatchAppBeanLocal.");
        ShamenScheduleBeanLocal bean;
        try{
            Context context = new InitialContext();
            bean = (ShamenScheduleBeanLocal) context.lookup("java:global/ShamenEAR/ShamenEJB/ShamenScheduleBean!gov.doc.isu.shamen.ejb.beans.view.ShamenScheduleBeanLocal");
        }catch(NamingException e){
            log.error("An exception occurred while attempting to access the BatchAppBean from the ShamenServiceBean. Message is: " + e.getMessage());
            throw new BaseException(e.getMessage());
        }// end try/catch
        log.debug("Exiting BatchAppInfo.getBean(). Return value is: " + String.valueOf(bean));
        return bean;

    }// end getBean

    /**
     * This method is called to use a JNDI lookup for accessing the {@link ControllerBeanLocal}.
     *
     * @return An instance of the <code>ControllerBeanLocal</code>.
     * @throws BaseException
     *         An exception if the <code>ControllerBeanLocal</code> class cannot be accessed.
     */
    public ControllerBeanLocal getControllerBean() throws BaseException {
        log.debug("Entering BatchAppInfo.getControllerBean().");
        log.debug("This method is called to use a JNDI lookup for accessing the ControllerBeanLocal.");
        ControllerBeanLocal bean;
        try{
            Context context = new InitialContext();
            bean = (ControllerBeanLocal) context.lookup("java:global/ShamenEAR/ShamenEJB/ControllerBean!gov.doc.isu.shamen.ejb.beans.view.ControllerBeanLocal");
        }catch(NamingException e){
            log.error("An exception occurred while attempting to access the BatchAppBean from the ShamenServiceBean. Message is: " + e.getMessage());
            throw new BaseException(e.getMessage());
        }// end try/catch
        log.debug("Exiting BatchAppInfo.getControllerBean(). Return value is: " + String.valueOf(bean));
        return bean;
    }// end getControllerBean

    /**
     * This method is called to use a JNDI lookup for accessing the {@link CodeBeanLocal}.
     * 
     * @return An instance of the <code>CodeBeanLocal</code>.
     * @throws BaseException
     *         An exception if the <code>CodeBeanLocal</code> class cannot be accessed.
     */
    public CodeBeanLocal getCodeBean() throws BaseException {
        log.debug("Entering BatchAppInfo.getCodeBean().");
        log.debug("This method is called to use a JNDI lookup for accessing the CodeBeanLocal.");
        CodeBeanLocal bean;
        try{
            Context context = new InitialContext();
            bean = (CodeBeanLocal) context.lookup("java:global/ShamenEAR/ShamenEJB/CodeBean!gov.doc.isu.shamen.ejb.beans.view.CodeBeanLocal");
        }catch(NamingException e){
            log.error("An exception occurred while attempting to access the CodeBean from the ShamenServiceBean. Message is: " + e.getMessage());
            throw new BaseException(e.getMessage());
        }// end try/catch
        log.debug("Exiting getCodeBean. Return value is: " + String.valueOf(bean));
        return bean;
    }// end getCodeBean

    /**
     * This method is called to use a JNDI lookup for accessing the {@link JmsManagerBeanLocal}.
     *
     * @return An instance of the <code>JmsManagerBeanLocal</code>.
     * @throws BaseException
     *         An exception if the <code>JmsManagerBeanLocal</code> class cannot be accessed.
     */
    public JmsManagerBeanLocal getJmsManager() throws BaseException {
        log.debug("Entering BatchAppInfo.getJmsManager().");
        log.debug("This method is called to use a JNDI lookup for accessing the BatchAppBeanLocal.");
        JmsManagerBeanLocal bean;
        try{
            Context context = new InitialContext();
            bean = (JmsManagerBeanLocal) context.lookup("java:global/ShamenEAR/ShamenEJB/JmsManagerBean!gov.doc.isu.shamen.jms.beans.view.JmsManagerBeanLocal");
        }catch(NamingException e){
            log.error("An exception occurred while attempting to access the JmsManagerBean from the ShamenServiceBean. Message is: " + e.getMessage());
            throw new BaseException(e.getMessage());
        }// end try/catch
        log.debug("Exiting BatchAppInfo.getJmsManager(). Return value is: " + String.valueOf(bean));
        return bean;
    }// end getJmsManager

    /**
     * This method gets the stat information for a batch app, formats it into CSV, and returns it as String.
     *
     * @param batchApp
     *        batchAppRefId
     * @return csvString
     * @throws BaseException
     *         if an exception occurred
     */
    public String buildStatCSV(BatchAppModel batchApp) throws BaseException {
        log.debug("Entering BatchAppInfo.buildStatCSV");
        // Pull out the run statuses
        List<RunStatusModel> runStatusList = getRunStatusListForGraphByBatchAppRefId(batchApp.getBatchRefId());

        // Build the statistic CSV
        StringBuffer csv = new StringBuffer();
        if(runStatusList != null && !runStatusList.isEmpty()){
            csv.append("Date,");
            csv.append(batchApp.getName());
            csv.append("\\");
            csv.append("n");
            ArrayList<String> details = new ArrayList<String>();
            // Loop through the map and load it from the
            for(int i = 0, j = runStatusList.size();i < j;i++){
                if("DON".equals(runStatusList.get(i).getStatusCd())){
                    details.clear();
                    details.add(ShamenUtil.getTimeStampForChart(runStatusList.get(i).getStartTs()));
                    details.add(runStatusList.get(i).getDurationInMinutes());
                    csv.append(ShamenUtil.buildCSVRow(details, ","));
                }// end if
            }// end for
        }// end for
        log.debug("Exiting BatchAppInfo.buildStatCSV");
        return csv.toString();
    }// end buildStatCSV

    /**
     * Loads a {@link BatchAppModel} for duplication.
     *
     * @param refId
     *        the ref id of the batch
     * @return BatchAppModel
     * @throws BaseException
     *         if an exception occurred
     */
    public BatchAppModel getBatchForDuplicate(Long refId, Long userRefId) throws BaseException {
        log.debug("Entering BatchAppInfo.getBatchForDuplicate(). Incoming parameters are refId = " + String.valueOf(refId) + ", userRefId = " + String.valueOf(userRefId));
        log.debug("This method is used to load an BatchAppModel for duplication.");
        BatchAppModel model = getBatchForDetail(refId);
        model.setBatchRefId(0L);
        model.setRunStatuses(null);
        model.setPartOfCollection("N");
        resetCommonModel(model, userRefId);
        List<ScheduleModel> scheduleList = model.getScheduleList();
        if(!AppUtil.isEmpty(scheduleList)){
            resetScheduleValues(scheduleList, userRefId);
        }// end if
        log.debug("Exiting BatchAppInfo.getBatchForDuplicate batch=" + String.valueOf(model));
        return model;
    }// end getBatchForDuplicate

    /**
     * This method handles resetting values on the CommonModel.
     * 
     * @param model
     *        - CommonModel to be reset
     * @param userRefId
     *        - the userRefId
     */
    private void resetCommonModel(CommonModel model, Long userRefId) {
        log.debug("Entering resetCommonModel");
        log.debug("Entry Parameters: model = " + String.valueOf(model) + ", userRefId = " + String.valueOf(userRefId));
        log.trace("This method handles resetting values on the CommonModel.");
        model.setCreateUserRefId(userRefId);
        model.setCreateTime(null);
        model.setUpdateUserRefId(0L);
        model.setUpdateTime(null);
        log.debug("Exiting resetCommonModel");
    }// end resetCommonModel

    /**
     * This method handles resetting the Schedule values.
     * 
     * @param scheduleList
     *        - the ScheduleModel list to be reset
     * @param userRefId
     *        - the user ref id.
     */
    private void resetScheduleValues(List<ScheduleModel> scheduleList, Long userRefId) {
        log.debug("Entering resetScheduleValues");
        log.debug("Entry Parameters: scheduleList.size = " + String.valueOf(scheduleList.size()) + ", userRefId = " + String.valueOf(userRefId));
        log.trace("This method handles resetting the Schedule values.");
        ScheduleModel scheduleModel = null;
        for(int i = 0, j = scheduleList.size();i < j;i++){
            scheduleModel = scheduleList.get(i);
            scheduleModel.setScheduleRefId(0L);
            scheduleModel.getBatchApp().setBatchRefId(0L);
            resetCommonModel(scheduleModel, userRefId);
        }// end for
        log.debug("Exiting resetScheduledValues");
    }// end resetScheduleValues

    /**
     * Loads a {@link BatchAppModel} for display.
     *
     * @param refId
     *        the ref id of the batch
     * @return BatchAppModel
     * @throws BaseException
     *         if an exception occurred
     */
    public BatchAppModel getBatchForDetail(Long refId) throws BaseException {
        log.debug("Entering BatchAppInfo.getBatch(). Incoming parameter is batchAppRefId = " + String.valueOf(refId));
        log.debug("This method is used to load an BatchAppModel for edit.");
        BatchAppEntity entity = null;
        BatchAppModel model = new BatchAppModel();
        try{
            entity = (BatchAppEntity) getBean().findBatchByRefId(refId);
        }catch(Exception e){
            log.error("An exception occurred while getting batch app entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        try{
            if(null != entity){
                model = ObjectConvertor.toBatchAppBusiness(entity);
                model.setController(ObjectConvertor.toControllerBusiness(entity.getController()));
                model.setScheduleList(new ArrayList<ScheduleModel>());
                if(!AppUtil.isEmpty(entity.getSchedule())){
                    getBean().checkOnScheduleForBatchDetail(entity.getSchedule(), model.getBatchRefId());
                    for(ScheduleEntity schedule : entity.getSchedule()){
                        model.getScheduleList().add(ObjectConvertor.toScheduleBusiness(schedule));
                    }// end for
                }// end if
                model.setApplication(entity.getApplication() == null ? new ApplicationModel() : ObjectConvertor.toApplicationBusiness(entity.getApplication()));
                model.setSystem(entity.getSystem() == null ? new SystemModel() : ObjectConvertor.toSystemBusiness(entity.getSystem()));
                model.setPointOfContact(entity.getPointOfContact() == null ? new AuthorizedUserModel() : ObjectConvertor.toAuthorizedUserBusiness(entity.getPointOfContact()));
                model.setPartOfCollection(getPartOfCollection(refId));
            }else{
                log.warn("The batch app entity in the database is null or empty. An new instatnce of batch app model will be returned.");
            }// end if/else
        }catch(Exception e){
            log.error("An exception occurred while converting batch app model. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Exiting BatchAppInfo.getBatch batch=" + String.valueOf(model));
        return model;
    }// end getBatch

    /**
     * This method is used to load a List of RunStatusModel for display on batch app detail screen.
     * 
     * @param batchAppRefId
     *        the batch app ref id
     * @param startRow
     *        the first row to display
     * @param endRow
     *        the last row to display
     * @return List<RunStatusModel>
     * @throws BaseException
     *         if an exception occurred
     */
    public List<RunStatusModel> getRunStatusList(Long batchAppRefId, Long startRow, Long endRow) throws BaseException {
        log.debug("Entering getRunStatusList");
        log.debug("This method is used to load a List of RunStatusModel for display on batch app detail screen.");
        log.debug("Entry parameters are: batchAppRefId=" + String.valueOf(batchAppRefId));
        List<RunStatusModel> runList = new ArrayList<RunStatusModel>();
        try{
            runList = ObjectConvertor.toRunStatusListBusinessFromObjForDetail(getBean().getRunStatusListByPageAndResult(batchAppRefId, startRow, endRow, null));
        }catch(Exception e){
            log.error("An exception occurred while getting run status list entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Return value is: runStatusList=" + String.valueOf(runList));
        log.debug("Exiting getRunStatusList");
        return runList;
    }// end getRunStatusList

    /**
     * This method gets the row count for run status records associated with a batch application.
     * 
     * @param batchAppRefId
     *        the batch app ref id
     * @return Integer
     * @throws BaseException
     *         if an exception occurred
     */
    public Integer getRunStatusCount(Long batchAppRefId) throws BaseException {
        log.debug("Entering getRunStatusCount");
        log.debug("This method gets the row count for run status records associated with a batch application.");
        log.debug("Entry parameters are: batchAppRefId=" + String.valueOf(batchAppRefId));
        int result = 0;
        try{
            result = getBean().countRunStatus(batchAppRefId, null);
        }catch(Exception e){
            log.error("An exception occurred while getting the batch app run status count. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Return value is: result=" + String.valueOf(result));
        log.debug("Exiting getRunStatusCount");
        return result;
    }// end getRunStatusCount

    /**
     * This method is used to load a List of RunStatusModel for display on batch app collection detail screen.
     * 
     * @param batchAppRefId
     *        the batch app ref id
     * @param startRow
     *        the first row to select for display
     * @param endRow
     *        the last roe to select for display
     * @return List<RunStatusModel>
     * @throws BaseException
     *         if an exception occurred
     */
    public List<RunStatusModel> getCollectionRunStatusList(Long batchAppRefId, Long startRow, Long endRow) throws BaseException {
        log.debug("Entering getCollectionRunStatusList");
        log.debug("This method is used to load a List of RunStatusModel for display on batch app collection detail screen.");
        log.debug("Entry parameters are: batchAppRefId=" + String.valueOf(batchAppRefId));
        List<RunStatusModel> runList = new ArrayList<RunStatusModel>();
        try{
            runList = ObjectConvertor.toRunStatusListBusinessFromObjForCollectionDetail(getBean().getCollectionRunStatusListByPage(batchAppRefId, startRow, endRow, null));
        }catch(Exception e){
            log.error("An exception occurred while getting collection run status list entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Return value is: runStatusList=" + String.valueOf(runList));
        log.debug("Exiting getCollectionRunStatusList");
        return runList;
    }// end getRunStatusList

    /**
     * This method checks to see if a batch app is part of a collection.
     *
     * @param refId
     *        the ref id of the batch app
     * @return String Y/N
     */
    public String getPartOfCollection(Long refId) {
        log.debug("Entering getPartOfCollection");
        log.debug("This method checks to see if a batch app is part of a collection.");
        log.debug("Entry parameters are: refId=" + String.valueOf(refId));
        String result = "N";
        try{
            result = getBean().partOfCollection(refId);
        }catch(BaseException e){
            log.error("An BaseException occurred while checking if batch app is part of collection. Message is: " + e.getMessage(), e);
        }catch(Exception e){
            log.error("An BaseException occurred while checking if batch app is part of collection. Message is: " + e.getMessage(), e);
        }// end try/catch
        log.debug("Return value is: result=" + String.valueOf(result));
        log.debug("Exiting getPartOfCollection");
        return result;

    }// end getPartOfCollection

    /**
     * Loads a {@link BatchAppModel} (Collection) for duplication.
     *
     * @param refId
     *        the ref id of the batch
     * @return BatchAppModel
     * @throws BaseException
     *         if an exception occurred
     */
    public BatchAppModel getBatchCollectionForDuplicate(Long refId, Long userRefId) throws BaseException {
        log.debug("Entering BatchAppInfo.getBatchCollectionForDuplicate(). Incoming parameters are refId = " + String.valueOf(refId) + ", userRefId = " + String.valueOf(userRefId));
        log.debug("This method is used to load a BatchApp Model (Collection) for duplication.");
        BatchAppModel model = getBatchCollection(refId);
        model.setBatchRefId(0L);
        model.setRunStatuses(null);
        resetCommonModel(model, userRefId);
        List<BatchAppCollectionModel> batchCollection = model.getBatchAppCollection();
        if(!AppUtil.isEmpty(batchCollection)){
            log.info("Resetting Associated Batch Apps in Collection");
            BatchAppCollectionModel collModel = null;
            for(int i = 0, j = batchCollection.size();i < j;i++){
                collModel = batchCollection.get(i);
                collModel.setCollectionRefId(0L);
//                collModel.getMainBatchApp().setBatchRefId(0L);
                resetCommonModel(collModel, userRefId);
            }// end for
        }// end if
        List<ScheduleModel> scheduleList = model.getScheduleList();
        if(!AppUtil.isEmpty(scheduleList)){
            log.info("Resetting Schedule values");
            resetScheduleValues(scheduleList, userRefId);
        }// end if
        log.debug("Exiting BatchAppInfo.getBatchCollectionForDuplicate batch=" + String.valueOf(model));
        return model;
    }// end getBatchCollectionForDuplicate

    /**
     * Loads a {@link BatchAppModel} for display.
     *
     * @param refId
     *        the ref id of the batch
     * @return BatchAppModel
     * @throws BaseException
     *         if an exception occurred
     */
    public BatchAppModel getBatchCollection(Long refId) throws BaseException {
        log.debug("Entering BatchAppInfo.getBatchCollection(). Incoming parameter is batchAppRefId = " + String.valueOf(refId));
        log.debug("This method is used to load an BatchAppModel for edit.");
        BatchAppEntity entity = null;
        BatchAppModel model = new BatchAppModel();
        try{
            entity = (BatchAppEntity) getBean().findBatchByRefId(refId);
        }catch(Exception e){
            log.error("An exception occurred while getting batch app entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        try{
            if(null != entity){
                model = ObjectConvertor.toBatchCollectionBusiness(entity);
                model.setController(ObjectConvertor.toControllerBusiness(entity.getController()));
                model.setScheduleList(new ArrayList<ScheduleModel>());
                if(!AppUtil.isEmpty(entity.getSchedule())){
                    getBean().checkOnScheduleForBatchDetail(entity.getSchedule(), model.getBatchRefId());
                    for(ScheduleEntity schedule : entity.getSchedule()){
                        model.getScheduleList().add(ObjectConvertor.toScheduleBusiness(schedule));
                    }// end for
                }// end if
                model.setApplication(entity.getApplication() == null ? new ApplicationModel() : ObjectConvertor.toApplicationBusiness(entity.getApplication()));
                model.setSystem(entity.getSystem() == null ? new SystemModel() : ObjectConvertor.toSystemBusiness(entity.getSystem()));
                model.setPointOfContact(entity.getPointOfContact() == null ? new AuthorizedUserModel() : ObjectConvertor.toAuthorizedUserBusiness(entity.getPointOfContact()));
                // model.setRunStatuses(ObjectConvertor.toRunStatusListBusinessFromObjForCollectionDetail(getBean().getCollectionRunStatusList(entity.getBatchAppRefId())));
            }else{
                log.warn("The batch app entity in the database is null or empty. An new instatnce of batch app model will be returned.");
            }// end if/else
        }catch(Exception e){
            log.error("An exception occurred while converting batch app model. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Exiting BatchAppInfo.getBatchCollection batch=" + String.valueOf(model));
        return model;
    }// end getBatchCollection

    /**
     * Loads a {@link BatchAppModel} for display.
     *
     * @param refId
     *        the ref id of the batch
     * @param collection
     *        collection indicator
     * @return BatchAppModel
     * @throws BaseException
     *         if an exception occurred
     */
    public BatchAppModel clearRunStatuses(Long refId, Boolean collection) throws BaseException {
        log.debug("Entering BatchAppInfo.clearRunStatuses(). Incoming parameter is batchAppRefId = " + String.valueOf(refId));
        log.debug("This method is used to clear all run statuses for a batch app and return load an BatchAppModel for edit.");
        BatchAppEntity entity = null;
        BatchAppModel model = new BatchAppModel();
        try{
            entity = (BatchAppEntity) getBean().deleteRunStatusByBatchAppRefId(refId, collection);
        }catch(Exception e){
            log.error("An exception occurred while deleting run statuses for a batch app entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        try{
            if(null != entity){
                if(AppConstants.BATCH_APP_TYPE_CD_COLLECTION.equalsIgnoreCase(entity.getBatchType().getBatchTypeCd())){
                    model = ObjectConvertor.toBatchCollectionBusiness(entity);
                }else{
                    model = ObjectConvertor.toBatchAppBusiness(entity);
                }// end if/else
                model.setController(ObjectConvertor.toControllerBusiness(entity.getController()));
                // model.setSchedule(ObjectConvertor.toScheduleBusiness(entity.getSchedule()));
                model.setScheduleList(new ArrayList<ScheduleModel>());
                if(!AppUtil.isEmpty(entity.getSchedule())){
                    for(ScheduleEntity schedule : entity.getSchedule()){
                        model.getScheduleList().add(ObjectConvertor.toScheduleBusiness(schedule));
                    }// end for
                }// end if
                model.setRunStatuses(new ArrayList<RunStatusModel>());
                model.setApplication(entity.getApplication() == null ? new ApplicationModel() : ObjectConvertor.toApplicationBusiness(entity.getApplication()));
                model.setSystem(entity.getSystem() == null ? new SystemModel() : ObjectConvertor.toSystemBusiness(entity.getSystem()));
                model.setPointOfContact(entity.getPointOfContact() == null ? new AuthorizedUserModel() : ObjectConvertor.toAuthorizedUserBusiness(entity.getPointOfContact()));
                model.setPartOfCollection(getPartOfCollection(refId));
            }else{
                log.warn("The batch app entity in the database is null or empty. An new instatnce of batch app model will be returned.");
            }// end if/else
        }catch(Exception e){
            log.error("An exception occurred while converting batch app model. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Exiting BatchAppInfo.clearRunStatuses batch=" + String.valueOf(model));
        return model;
    } // end getclearRunStatusesBatch

    /**
     * Loads a {@link getBatchEntity} without converting to BatchAppModel.
     *
     * @param refId
     *        the ref id of the batch
     * @return BatchEntity
     * @throws BaseException
     *         if an exception occurred
     */
    public BatchAppEntity getBatchEntity(Long refId) throws BaseException {
        log.debug("Entering BatchAppInfo.getBatchEntity(). Incoming parameter is batchAppRefId = " + String.valueOf(refId));
        log.trace("This method is used to load an Batc App hEntity.");
        BatchAppEntity entity = null;
        try{
            entity = (BatchAppEntity) getBean().findBatchByRefId(refId);
        }catch(Exception e){
            log.error("An exception occurred while getting batch app entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Exiting BatchAppInfo.getBatch. Return value: entity = " + String.valueOf(entity));
        return entity;
    } // end getBatchEntity

    /**
     * Saves new Batch App
     *
     * @param model
     *        the batch app model to save
     * @throws BaseException
     *         if an exception occurred
     */
    public void saveBatchApp(BatchAppModel model) throws BaseException {
        log.debug("Entering BatchAppInfo.saveBatchApp(). Incoming parameter is: " + (model == null ? "null" : model.toString()));
        log.debug("This method is used to add an Batch App.");
        BatchAppEntity entity = null;
        try{
            if(null != model){
                model.setDeleteIndicator("N");
                entity = ObjectConvertor.toBatchAppPersist(model);
                entity.setSchedule(new ArrayList<ScheduleEntity>());
                if(!AppUtil.isEmpty(model.getScheduleList())){
                    for(ScheduleModel scheduleModel : model.getScheduleList()){
                        ScheduleEntity scheduleEntity = ObjectConvertor.toSchedulePersist(scheduleModel);
                        if(null != scheduleEntity){
                            scheduleEntity.setBatchApp(entity);
                            entity.getSchedule().add(scheduleEntity);
                        }// end if
                    }// end for
                }// end if
                entity = getBean().create(entity);
            }// end if/else
        }catch(Exception e){
            log.error("An exception occurred while saving a new batch app entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        publishControllerUpdate(entity.getController().getControllerRefId());
        log.debug("Exiting BatchAppInfo.saveBatchApp");
    }// end saveBatchApp

    /**
     * Saves new Batch App Collection.
     *
     * @param model
     *        the batch app model to save
     * @throws BaseException
     *         if an exception occurred
     */
    public void saveBatchCollection(BatchAppModel model) throws BaseException {
        log.debug("Entering BatchAppInfo.saveBatchCollection(). Incoming parameter is: " + (model == null ? "null" : model.toString()));
        log.debug("This method is used to add an Batch App Collection.");
        BatchAppEntity entity = null;
        try{
            if(null != model){
                model.setDeleteIndicator("N");
                entity = ObjectConvertor.toBatchCollectionPersist(model);
                entity.setSchedule(new ArrayList<ScheduleEntity>());
                if(!AppUtil.isEmpty(model.getScheduleList())){
                    for(ScheduleModel scheduleModel : model.getScheduleList()){
                        ScheduleEntity scheduleEntity = ObjectConvertor.toSchedulePersist(scheduleModel);
                        if(null != scheduleEntity){
                            scheduleEntity.setBatchApp(entity);
                            entity.getSchedule().add(scheduleEntity);
                        }// end if
                    }// end for
                }// end if
                 // entity.setSchedule(ObjectConvertor.toSchedulePersist(model.getSchedule()));
                 // if(null != entity.getSchedule()){
                 // entity.getSchedule().setBatchApp(entity);
                 // }// end if
                entity = getBean().create(entity);
            }// end if/else
        }catch(Exception e){
            log.error("An exception occurred while saving a new batch app collection. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        publishControllerUpdate(entity.getController().getControllerRefId());
        log.debug("Exiting BatchAppInfo.saveBatchCollection");
    }// end saveBatchCollection

    /**
     * Updates a existing batch app collection record
     *
     * @param model
     *        the batch app collection model to update
     * @throws BaseException
     *         if an exception occurred
     */
    public BatchAppModel updateBatchApp(BatchAppModel model) throws BaseException {
        log.debug("Entering BatchAppInfo.updateBatchApp().  Incoming parameter is: " + String.valueOf(model));
        log.debug("This method is used to Update an existing Batch App record.");
        BatchAppEntity entity = null;
        try{
            if(null != model){
                model.setDeleteIndicator("N");
                entity = ObjectConvertor.toBatchAppPersist(model);
                entity.setSchedule(new ArrayList<ScheduleEntity>());
                if(!AppUtil.isEmpty(model.getScheduleList())){
                    for(ScheduleModel scheduleModel : model.getScheduleList()){
                        ScheduleEntity scheduleEntity = ObjectConvertor.toSchedulePersist(scheduleModel);
                        if(null != scheduleEntity){
                            scheduleEntity.setBatchApp(entity);
                            entity.getSchedule().add(scheduleEntity);
                        }// end if
                    }// end for
                }// end if
                 // entity.setSchedule(ObjectConvertor.toSchedulePersist(model.getSchedule()));
                 // if(null != entity.getSchedule()){
                 // entity.getSchedule().getBatchApp().setBatchAppRefId(entity.getBatchAppRefId());
                 // }// end if
                entity = getBean().update(entity);
                if(null != entity){
                    model = new BatchAppModel();
                    model = ObjectConvertor.toBatchAppBusiness(entity);
                    model.setController(ObjectConvertor.toControllerBusiness(entity.getController()));
                    model.setScheduleList(new ArrayList<ScheduleModel>());
                    if(!AppUtil.isEmpty(entity.getSchedule())){
                        for(ScheduleEntity schedule : entity.getSchedule()){
                            model.getScheduleList().add(ObjectConvertor.toScheduleBusiness(schedule));
                        }// end for
                    }// end if
                    model.setApplication(entity.getApplication() == null ? new ApplicationModel() : ObjectConvertor.toApplicationBusiness(entity.getApplication()));
                    model.setSystem(entity.getSystem() == null ? new SystemModel() : ObjectConvertor.toSystemBusiness(entity.getSystem()));
                    model.setPointOfContact(entity.getPointOfContact() == null ? new AuthorizedUserModel() : ObjectConvertor.toAuthorizedUserBusiness(entity.getPointOfContact()));
                    model.setPartOfCollection(getPartOfCollection(entity.getBatchAppRefId()));
                }// end if
            }// end if/else
        }catch(Exception e){
            log.error("An exception occurred while updating an batch app entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        publishControllerUpdate(entity.getController().getControllerRefId());
        log.debug("Exiting BatchAppInfo.updateBatchApp");
        return model;
    } // end updateBatchApp

    /**
     * Updates a existing batch app collection record
     *
     * @param model
     *        the batch app collection model to update
     * @throws BaseException
     *         if an exception occurred
     */
    public void updateBatchAppCollection(BatchAppModel model) throws BaseException {
        log.debug("Entering BatchAppInfo.updateBatchAppCollection().  Incoming parameter is: " + String.valueOf(model));
        log.debug("This method is used to Update an existing Batch App Collection record.");
        BatchAppEntity entity = null;
        try{
            if(null != model){
                model.setDeleteIndicator("N");
                entity = ObjectConvertor.toBatchCollectionPersist(model);
                entity.setSchedule(new ArrayList<ScheduleEntity>());
                if(!AppUtil.isEmpty(model.getScheduleList())){
                    for(ScheduleModel scheduleModel : model.getScheduleList()){
                        ScheduleEntity scheduleEntity = ObjectConvertor.toSchedulePersist(scheduleModel);
                        if(null != scheduleEntity){
                            scheduleEntity.setBatchApp(entity);
                            entity.getSchedule().add(scheduleEntity);
                        }// end if
                    }// end for
                }// end if
                 // entity.setSchedule(ObjectConvertor.toSchedulePersist(model.getSchedule()));
                 // if(null != entity.getSchedule()){
                 // entity.getSchedule().getBatchApp().setBatchAppRefId(entity.getBatchAppRefId());
                 // }// end if
                entity = getBean().update(entity);
            }// end if/else
        }catch(Exception e){
            log.error("An exception occurred while updating an batch app collection. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        publishControllerUpdate(entity.getController().getControllerRefId());
        log.debug("Exiting BatchAppInfo.updateBatchAppCollection");
    } // end updateBatchAppCollection

    /**
     * Loads a {@link ScheduleModel} for display.
     *
     * @param refId
     *        the ref id of the schedule
     * @return ScheduleModel
     * @throws BaseException
     *         if an exception occurred
     */
    public ScheduleModel getBatchAppSchedule(Long refId) throws BaseException {
        log.debug("Entering BatchAppInfo.getBatchAppSchedule(). Incoming parameter is scheduleRefId = " + String.valueOf(refId));
        log.debug("This method is used to load an ScheduleModel for edit.");
        ScheduleEntity entity = null;
        ScheduleModel model = new ScheduleModel();
        try{
            entity = (ScheduleEntity) getBean().findScheduleByRefId(refId);
        }catch(Exception e){
            log.error("An exception occurred while getting schedule entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        try{
            if(null != entity){
                model = ObjectConvertor.toScheduleBusiness(entity);
            }else{
                log.warn("The schedule entity in the database is null or empty. An new instatnce of schedule model will be returned.");
            }// end if/else
        }catch(Exception e){
            log.error("An exception occurred while converting schedule model. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Exiting BatchAppInfo.getBatchAppSchedule schedule=" + String.valueOf(model));
        return model;
    }// end getBatchAppSchedule

    /**
     * This method is used to delete an Batch App Schedule record.
     *
     * @param scheduleRefId
     *        the refid of schedule associated with the batch app
     * @param updateUserRefId
     *        the refid of user deleting record
     * @throws BaseException
     *         if an exception occurred
     */
    public List<ScheduleModel> deleteBatchAppSchedule(List<ScheduleModel> scheduleList, Long scheduleRefId, Long updateUserRefId) throws BaseException {
        log.debug("Entering BatchAppInfo.deleteBatchAppSchedule(). Incoming parameters are: scheduleRefId=" + String.valueOf(scheduleRefId) + ", updateUserRefId=" + String.valueOf(updateUserRefId));
        log.debug("This method is used to delete an Batch App Schedule record.");
        try{
            for(ScheduleModel scheduleModel : scheduleList){
                if(scheduleModel.getScheduleRefId().equals(scheduleRefId)){
                    scheduleModel.setDeleteIndicator("Y");
                    scheduleModel.setUpdateUserRefId(updateUserRefId);
                }// end if
            }// end for
        }catch(Exception e){
            log.error("An exception occurred while delete an batch app schedule. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Exiting BatchAppInfo.deleteBatchAppSchedule");
        return scheduleList;
    } // end deleteBatchAppSchedule

    /**
     * Deletes a existing batch app record
     *
     * @param batchAppRefId
     *        the refid of batch app to delete
     * @param updateUserRefId
     *        the refid of user deleting record
     * @param isCollection
     *        true if the a Batch App Collection
     * @throws BaseException
     *         if an exception occurred
     */
    public void deleteBatchApp(Long batchAppRefId, Long updateUserRefId, boolean isCollection) throws BaseException {
        log.debug("Entering BatchAppInfo.deleteBatchApp(). Incoming parameters are: batchAppRefId=" + String.valueOf(batchAppRefId) + ", updateUserRefId=" + String.valueOf(updateUserRefId));
        log.debug("This method is used to delete an Batch App record.");
        try{
            getBean().deleteRunStatusByBatchAppRefId(batchAppRefId, isCollection);
            Long controllerRefId = getBean().deleteBatchApp(batchAppRefId, updateUserRefId);
            publishControllerUpdate(controllerRefId);
        }catch(Exception e){
            log.error("An exception occurred while deleting an batch app entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Exiting BatchAppInfo.deleteBatchApp");
    } // end deleteBatchApp

    /**
     * Updates all batch apps by setting active ind to 'Y' or 'N'
     *
     * @param activeInd
     *        the active indicator value
     * @param updateUserRefId
     *        the refid of user updating records
     * @throws BaseException
     *         if an exception occurred
     */
    public void updateAllBatchApps(String activeInd, Long updateUserRefId) throws BaseException {
        log.debug("Entering BatchAppInfo.updateAllBatchApps(). Incoming parameters are: activeInd=" + String.valueOf(activeInd) + ", updateUserRefId=" + String.valueOf(updateUserRefId));
        log.debug("This method is used to activate/deactivate all Batch App records.");
        try{
            getBean().updateAllBatchApp(activeInd, updateUserRefId);
            // Get all controllers and publish refreshes to them.
            List<ControllerModel> controllerList = ObjectConvertor.toControllerListBusinessFromObj(getControllerBean().getControllerList());
            for(int i = 0, j = controllerList.size();i < j;i++){
                publishControllerUpdate(controllerList.get(i).getControllerRefId());
            }// end for
        }catch(Exception e){
            log.error("An exception occurred while activating/deactivating all batch apps. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Exiting BatchAppInfo.updateAllBatchApps");
    } // end updateAllBatchApps

    /**
     * Updates all batch apps collections by setting active ind to 'Y' or 'N'
     *
     * @param activeInd
     *        the active indicator value
     * @param updateUserRefId
     *        the refid of user updating records
     * @throws BaseException
     *         if an exception occurred
     */
    public void updateAllBatchAppsCollections(String activeInd, Long updateUserRefId) throws BaseException {
        log.debug("Entering BatchAppInfo.updateAllBatchAppsCollections(). Incoming parameters are: activeInd=" + String.valueOf(activeInd) + ", updateUserRefId=" + String.valueOf(updateUserRefId));
        log.debug("This method is used to activate/deactivate all Batch App records.");
        try{
            getBean().updateAllBatchAppCollections(activeInd, updateUserRefId);
            // Get all controllers and publish refreshes to them.
            List<ControllerModel> controllerList = ObjectConvertor.toControllerListBusinessFromObj(getControllerBean().getControllerList());
            for(int i = 0, j = controllerList.size();i < j;i++){
                publishControllerUpdate(controllerList.get(i).getControllerRefId());
            }// end for
        }catch(Exception e){
            log.error("An exception occurred while activating/deactivating all batch apps collections. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Exiting BatchAppInfo.updateAllBatchAppsCollections");
    } // end updateAllBatchApps

    /**
     * Updates all batch apps for a controller by setting active ind to 'Y' or 'N'
     *
     * @param activeInd
     *        the active indicator value
     * @param controllerRefId
     *        the controller the batch apps are associated with
     * @param updateUserRefId
     *        the refid of user updating records
     * @throws BaseException
     *         if an exception occurred
     */
    public void updateControllerBatchApps(String activeInd, Long controllerRefId, Long updateUserRefId) throws BaseException {
        log.debug("Entering BatchAppInfo.updateControllerBatchApps(). Incoming parameters are: activeInd=" + String.valueOf(activeInd) + ", controllerRefId=" + String.valueOf(controllerRefId) + ", updateUserRefId=" + String.valueOf(updateUserRefId));
        log.debug("This method is used to activate/deactivate all Batch App records associated with a specific controller record.");
        try{
            getBean().updateAllControllerBatchApp(activeInd, controllerRefId, updateUserRefId);
            publishControllerUpdate(controllerRefId);
        }catch(Exception e){
            log.error("An exception occurred while activating/deactivating all batch apps associated with a specific controller. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Exiting BatchAppInfo.updateControllerBatchApps");
    } // end updateControllerBatchApps

    /**
     * This method updates the active ind
     *
     * @param activeInd
     *        the active indicator value
     * @param batchRefId
     *        the ref id of the batch app
     * @param updateUserRefId
     *        the refid of user updating records
     * @throws BaseException
     *         if an exception occurred
     */
    public void updateBatchActiveInd(String activeInd, Long batchRefId, Long updateUserRefId) throws BaseException {
        log.debug("Entering BatchAppInfo.updateBatchActiveInd(). Incoming parameters are: activeInd=" + String.valueOf(activeInd) + ", batchRefId=" + String.valueOf(batchRefId) + ", updateUserRefId=" + String.valueOf(updateUserRefId));
        log.debug("This method is used to activate/deactivate a Batch App record.");
        Long controllerRefId = null;
        try{
            controllerRefId = getBean().updateBatchAppActiveInd(activeInd, batchRefId, updateUserRefId);
            // Publish a message to tell controllers to update their schedules
            publishControllerUpdate(controllerRefId);
        }catch(Exception e){
            log.error("An exception occurred while activating/deactivating a batch app entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Exiting BatchAppInfo.updateBatchActiveInd");
    } // end updateBatchActiveInd

    /**
     * This method updates the active ind
     *
     * @param activeInd
     *        the active indicator value
     * @param batchRefId
     *        the ref id of the batch app
     * @param scheduleRefId
     *        the ref id of the batch apps schedule record
     * @param updateUserRefId
     *        the refid of user updating records
     * @throws BaseException
     *         if an exception occurred
     */
    public void updateBatchActiveInd(String activeInd, Long batchRefId, Long scheduleRefId, Long updateUserRefId) throws BaseException {
        log.debug("Entering BatchAppInfo.updateBatchActiveInd(). Incoming parameters are: activeInd=" + String.valueOf(activeInd) + ", batchRefId=" + String.valueOf(batchRefId) + ", scheduleRefId=" + String.valueOf(scheduleRefId) + ", updateUserRefId=" + String.valueOf(updateUserRefId));
        log.debug("This method is used to activate/deactivate a Batch App record.");
        Long controllerRefId = null;
        try{
            controllerRefId = getBean().updateBatchAppActiveInd(activeInd, batchRefId, scheduleRefId, updateUserRefId);
            // Publish a message to tell controllers to update their schedules
            publishControllerUpdate(controllerRefId);
        }catch(Exception e){
            log.error("An exception occurred while activating/deactivating a batch app entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Exiting BatchAppInfo.updateBatchActiveInd");
    } // end updateBatchActiveInd

    /**
     * Loads a {@link BatchAppModel} for display.
     *
     * @param refId
     *        the ref id of the batch
     * @return BatchAppModel
     * @throws BaseException
     *         if an exception occurred
     */
    public BatchAppModel getBatchForGraph(Long refId) throws BaseException {
        log.debug("Entering BatchAppInfo.getBatchForGraph(). Incoming parameter is batchAppRefId = " + String.valueOf(refId));
        log.debug("This method is used to load an BatchAppModel for display in graph.");
        String name = null;
        BatchAppModel model = new BatchAppModel();
        try{
            name = getBean().getBatchAppName(refId);
        }catch(Exception e){
            log.error("An exception occurred while getting batch app name. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        try{
            if(null != name){
                model.setName(name);
                model.setRunStatuses(getRunStatusListForGraphByBatchAppRefId(refId));
            }else{
                log.warn("The batch app entity in the database is null or empty. An new instatnce of batch app model will be returned.");
            }// end if/else
        }catch(BaseException e){
            throw e;
        }// end try/catch
        log.debug("Exiting BatchAppInfo.getBatchForGraph batch=" + String.valueOf(model));
        return model;
    }// end getBatchForGraph

    /**
     * Loads a List of {@link RunStatusModel} for display.
     *
     * @return list of RunStatusModel
     * @throws BaseException
     *         if an exception occurred
     */
    public List<RunStatusModel> getRunStatusListForGraphByBatchAppRefId(Long batchAppRefId) throws BaseException {
        log.debug("Entering BatchAppInfo.getRunStatusListForGraphByBatchAppRefId().");
        log.debug("This method is used to load a List of RunStatusModel for display.");
        List<RunStatusModel> runList = new ArrayList<RunStatusModel>();
        try{
            runList = ObjectConvertor.toRunStatusListForGraph(getBean().getRunStatusListForGraph(batchAppRefId));
        }catch(Exception e){
            log.error("An exception occurred while getting run status list entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Exiting BatchAppInfo.getRunStatusListForGraphByBatchAppRefId runStatusList.size=" + (AppUtil.isEmpty(runList) ? "empty" : runList.size()));
        return runList;
    } // end getRunStatusListForGraphByBatchAppRefId

    /**
     * Loads a List of {@link RunStatusModel} for display.
     *
     * @return list of RunStatusModel
     * @throws BaseException
     *         if an exception occurred
     */
    public List<RunStatusModel> getRunStatusList() throws BaseException {
        log.debug("Entering BatchAppInfo.getRunStatusList().");
        log.debug("This method is used to load a List of RunStatusModel for display.");
        List<RunStatusModel> runList = new ArrayList<RunStatusModel>();
        try{
            runList = ObjectConvertor.toRunStatusListBusinessFromObj(getBean().getRunStatusList());
        }catch(Exception e){
            log.error("An exception occurred while getting run status list entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Exiting BatchAppInfo.getRunStatusList runStatusList.size=" + (AppUtil.isEmpty(runList) ? "empty" : runList.size()));
        return runList;
    } // end getRunStatusList

    /**
     * Loads a List of {@link RunStatusModel} for use by the batch listing to determine if jobs are on schedule.
     *
     * @return list of RunStatusModel
     * @throws BaseException
     *         if an exception occurred
     */
    public List<RunStatusModel> getRunStatusListFromSchedule() throws BaseException {
        log.debug("Entering BatchAppInfo.getRunStatusList().");
        log.debug("This method is used to load a List of RunStatusModel for display.");
        List<RunStatusModel> runList = new ArrayList<RunStatusModel>();
        try{
            runList = ObjectConvertor.toRunStatusListBusinessFromObj(getBean().getRunStatusListFromSchedule());
        }catch(Exception e){
            log.error("An exception occurred while getting run status list entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Exiting BatchAppInfo.getRunStatusList runStatusList.size=" + (AppUtil.isEmpty(runList) ? "empty" : runList.size()));
        return runList;
    } // end getRunStatusListFromSchedule

    /**
     * This method is used to return a list of {@link RunStatusEntity}s for all smart george batch apps for all systems
     *
     * @return list of RunStatusModel
     * @throws BaseException
     *         if an exception occurred
     */
    public List<RunStatusModel> getCompletedRunStatusList() throws BaseException {
        log.debug("Entering BatchAppInfo.getCompletedRunStatusListBySystemAndSmarts().");
        log.debug("This method is used to return a list of {@link RunStatusEntity}s for all smart george batch apps within a system.");
        List<RunStatusModel> runList = new ArrayList<RunStatusModel>();
        try{
            runList = ObjectConvertor.toRunStatusListBusinessFromObj(getBean().getCompletedRunStatusList());
        }catch(Exception e){
            log.error("An exception occurred while getting run status list entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Exiting BatchAppInfo.getCompletedRunStatusListBySystemAndSmarts runStatusList.size=" + (AppUtil.isEmpty(runList) ? "empty" : runList.size()));
        return runList;
    } // end getRunStatusList

    /**
     * Loads a List of {@link BatchAppModel} for display.
     *
     * @return list of BatchAppModel
     * @throws BaseException
     *         if an exception occurred
     */

    public List<BatchAppModel> getBatchList() throws BaseException {
        log.debug("Entering BatchAppInfo.getBatchList().");
        log.debug("This method is used to load a List of BatchAppModels for display.");
        List<BatchAppModel> batchAppList = new ArrayList<BatchAppModel>();
        try{
            batchAppList = ObjectConvertor.convertForBatchAppList(getBean().getBatchList());
        }catch(Exception e){
            log.error("An exception occurred while getting batch app list entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
         // batchAppList = checkOnSchedule(batchAppList);
        log.debug("Exiting BatchAppInfo.getBatchList runStatusList.size=" + (AppUtil.isEmpty(batchAppList) ? "empty" : batchAppList.size()));
        return batchAppList;
    } // end getBatchList

    /**
     * Removes collection members from a List of {@link BatchAppModel} for display.
     *
     * @param batchList
     *        the list of batch apps to modify
     * @return list of BatchAppModel
     * @throws BaseException
     *         if an exception occurred
     */

    public List<BatchAppModel> removeCollectionMembers(List<BatchAppModel> batchList) throws BaseException {
        log.debug("Entering BatchAppInfo.removeCollectionMembers().");
        log.debug("Removes collection members from a List of BatchAppModels for display.");
        List<BatchAppModel> batchAppList = new ArrayList<BatchAppModel>();
        for(int i = 0;i < batchList.size();i++){
            if("N".equalsIgnoreCase(batchList.get(i).getPartOfCollection())){
                batchAppList.add(batchList.get(i));
            }// end if
        }// end for
        log.debug("Exiting BatchAppInfo.removeCollectionMembers batchAppList.size=" + (AppUtil.isEmpty(batchAppList) ? "empty" : batchAppList.size()));
        return batchAppList;
    } // end removeCollectionMembers

    /**
     * Loads a List of {@link BatchAppModel} for display. These are only batchApps and collections that have a valid schedule.
     *
     * @return list of BatchAppModel
     * @throws BaseException
     *         if an exception occurred
     */

    public List<BatchAppModel> getAllScheduledBatchAppsAndCollections() throws BaseException {
        log.debug("Entering BatchAppInfo.getAllScheduledBatchAppsAndCollections().");
        log.debug("This method is used to load a List of BatchAppModels for display. These are only batchApps and collections that have a valid schedule.");
        List<BatchAppModel> batchAppList = new ArrayList<BatchAppModel>();
        try{
            batchAppList = ObjectConvertor.convertForBatchAppList(getBean().getAllScheduledBatchAppsAndCollections());
        }catch(Exception e){
            log.error("An exception occurred while getting batch app list entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Exiting BatchAppInfo.getAllScheduledBatchAppsAndCollections runStatusList.size=" + (AppUtil.isEmpty(batchAppList) ? "empty" : batchAppList.size()));
        return batchAppList;
    } // end getAllScheduledBatchAppsAndCollections

    /**
     * This method gets a list of all batch jobs and batch collections and calculates all their run times within a specific date range.
     *
     * @param fromTs
     *        From Timestamp used in date range.
     * @param toTs
     *        To Timestamp used in date range.
     * @return Map of the apps and their run times
     * @throws BaseException
     *         if an exception occurred
     */
    public Map<Scheduleable, List<Timestamp>> getAllBatchRunTimes(Timestamp fromTs, Timestamp toTs) throws BaseException {
        log.debug("Entering BatchAppInfo.getAllBatchRunTimes().");
        log.debug("This method gets a list of all batch jobs and batch collections and calculates all their run times within a specific date range.");
        List<Timestamp> runTimes = new ArrayList<Timestamp>();
        Map<Scheduleable, List<Timestamp>> schedules = new HashMap<Scheduleable, List<Timestamp>>();
        try{
            // Get all the controllers. This will get all the batch jobs and batch collections as well.
            List<ControllerEntity> controllers = getControllerBean().getControllersAndChildren();
            // For each controller, pull out their batch jobs and collections.
            for(int i = 0, j = controllers.size();i < j;i++){
                ControllerEntity controller = controllers.get(i);
                JmsController jController = JmsObjectConvertor.toJms(controller);
                List<Scheduleable> apps = new ArrayList<Scheduleable>();
                // make sure the type is properly set for both standard batch jobs and collections.
                apps.addAll(setAllTypes(jController.getBatchApps(), "STD"));
                apps.addAll(setAllTypes(jController.getJmsBatchAppCollections(), "COL"));
                // loop through and load each batch job's run times.
                for(int k = 0, l = apps.size();k < l;k++){
                    if(!AppUtil.isEmpty(apps.get(k).getSchedule())){
                        schedules.put(apps.get(k), getBean().calculateRunTimes(apps.get(k), fromTs, toTs));
                    }// end if
                }// end for
            }// end for
        }catch(Exception e){
            log.error("An exception occurred while activating/deactivating all batch apps. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch

        log.debug("Exiting BatchAppInfo.getAllBatchRunTimes runTimes=" + (AppUtil.isEmpty(runTimes) ? "empty" : runTimes));
        return schedules;
    }// end getAllBatchRunTimes

    /**
     * This method loads the calendar data to a JSON array
     *
     * @param schedules
     *        a Map<Scheduleable, List<Timestamp>> of scheduled runs
     * @return JSONArray
     */
    public JSONArray getCalendarData(Map<Scheduleable, List<Timestamp>> schedules) {
        log.debug("Entering BatchAppInfo.getCalendarData().");
        log.debug("This method loads the calendar data to a JSON array.");
        JSONArray array = ObjectConvertor.convertToCalendarData(schedules);
        log.debug("Exiting BatchAppInfo.getCalendarData array=" + (AppUtil.isEmpty(array) ? "empty" : String.valueOf(array)));
        return array;
    }// end getCalendarData

    /**
     * This is a utility method to set all the types of a list of scheduleables.
     *
     * @param apps
     *        Scheduleables to change
     * @param type
     *        type to change to
     * @return list with the type changed
     */
    private List<Scheduleable> setAllTypes(List<?> apps, String type) {
        log.debug("Entering BatchAppInfo.setAllTypes().");
        log.debug("This is a utility method to set all the types of a list of scheduleables.");
        List<Scheduleable> returnApps = new ArrayList<Scheduleable>();
        for(int i = 0, j = apps.size();i < j;i++){
            Scheduleable scd = (Scheduleable) apps.get(i);
            scd.setType(type);
            returnApps.add(scd);
        }// end for
        log.debug("Exiting BatchAppInfo.setAllTypes array=" + (AppUtil.isEmpty(returnApps) ? "empty" : String.valueOf(returnApps)));
        return returnApps;
    }// end setAllTypes

    /**
     * Loads a List of {@link BatchAppModel} for display.
     *
     * @return list of BatchAppModel
     * @throws BaseException
     *         if an exception occurred
     */

    public List<BatchAppModel> getBatchCollectionList() throws BaseException {
        log.debug("Entering BatchAppInfo.getBatchList().");
        log.debug("This method is used to load a List of BatchAppModels for display.");
        List<BatchAppModel> batchAppList = new ArrayList<BatchAppModel>();
        try{
            batchAppList = ObjectConvertor.convertForBatchAppList(getBean().getBatchCollectionList());
        }catch(Exception e){
            log.error("An exception occurred while getting batch app collection list entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Exiting BatchAppInfo.getBatchCollectionList list.size=" + (AppUtil.isEmpty(batchAppList) ? "empty" : batchAppList.size()));
        return batchAppList;
    } // end getBatchList

    /**
     * Loads a {@link RunStatusModel} for display.
     *
     * @param refId
     *        the ref id of the batch
     * @return RunStatusModel
     * @throws BaseException
     *         if an exception occurred
     */
    public RunStatusModel getRunStatusResultDetail(Long refId) throws BaseException {
        log.debug("Entering BatchAppInfo.getRunStatusResultDetail(). Incoming parameter is runStatusRefId = " + String.valueOf(refId));
        log.debug("This method is used to load a RunStatusModel for display of result detail.");
        RunStatusEntity entity = null;
        RunStatusModel model = new RunStatusModel();
        try{
            entity = (RunStatusEntity) getBean().findRunStatusByRefId(refId);
        }catch(Exception e){
            log.error("An exception occurred while getting run status entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        try{
            if(null != entity){
                model = ObjectConvertor.toRunStatusBusiness(entity);
                model.setBatchApp(ObjectConvertor.toBatchAppBusiness(entity.getBatchApp()));
                model.setScheduleRefId(entity.getSchedule() == null ? 0L : entity.getSchedule().getScheduleRefId());
            }else{
                log.warn("The run status entity in the database is null or empty. An new instatnce of batch app model will be returned.");
            }// end if/else
        }catch(Exception e){
            log.error("An exception occurred while converting run status model. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Exiting BatchAppInfo.getRunStatusResultDetail model=" + String.valueOf(model));
        return model;
    } // end getRunStatusResultDetail

    /**
     * This method used to publish Information to JMS
     *
     * @param controllerRefId
     *        (required)
     * @throws BaseException
     *         if an exception occurred
     */
    private void publishControllerUpdate(Long controllerRefId) throws BaseException {
        log.debug("Entering BatchAppInfo.publishControllerUpdate");
        // Load all the controller's children
        ControllerInfo controllerInfo = ControllerInfo.getInstance();
        ControllerEntity controller = controllerInfo.getControllerEntity(controllerRefId);
        try{
            getJmsManager().resetControllerSchedules(JmsObjectConvertor.toJms(controller));
        }catch(Exception e){
            throw new BaseException(e.getMessage());
        }// end try/catch
        log.debug("Exiting BatchAppInfo.publishControllerUpdate");
    }// end publishControllerUpdate

    /**
     * This method used to start a batch app
     *
     * @param batchRefId
     *        (required)
     * @param model
     *        (required)
     * @param jobParameters
     *        (not required)
     * @throws BaseException
     *         if an exception occurred
     */
    public void startBatchApp(Long batchRefId, RunStatusModel model, String jobParameters) throws BaseException {
        log.debug("Entering BatchAppInfo.startBatchApp");
        log.trace("This method used to start a batch app");
        try{
            BatchAppEntity entity = getBatchEntity(batchRefId);
            String runNumberString = String.valueOf(System.currentTimeMillis());
            model.setRunNumber(Long.valueOf(runNumberString.substring(8)));
            model.setStatusCd(JmsRunStatus.STATUS_STARTING);
            model.setResultCd(JmsRunStatus.RESULTS_PENDING);
            model.setStart(ShamenUtil.getCurrentTimestampString());
            model.setStop(ShamenUtil.DEFAULT_TIMESTAMP);
            RunStatusEntity runStatusEntity = ObjectConvertor.toRunStatusPersist(model);
            runStatusEntity.setBatchApp(entity);
            getJmsManager().startBatchApp(runStatusEntity, entity.getBatchType().getBatchTypeCd(), null, jobParameters);
        }catch(Exception e){
            throw new BaseException(e.getMessage());
        }// end try/catch
        log.debug("Exiting BatchAppInfo.startBatchApp");
    }// end startBatchApp

    public List<ScheduleModel> removeScheduleInList(List<ScheduleModel> scheduleList, int listNumber) {
        log.debug("Entering BatchAppInfo.removeScheduleInList");
        for(int i = 0, j = scheduleList.size();i < j;i++){
            if(scheduleList.get(i).getScheduleRefId().equals(0L) && scheduleList.get(i).getListNumber() == listNumber){
                scheduleList.remove(i);
                break;
            }// end if
        }// end for
        log.debug("Exiting BatchAppInfo.removeScheduleInList");
        return scheduleList;
    }// end removeScheduleInList

    public List<ScheduleModel> updateScheduleInList(List<ScheduleModel> scheduleList, ScheduleModel model) throws BaseException {
        log.debug("Entering BatchAppInfo.updateScheduleInList");
        try{
            for(int i = 0, j = scheduleList.size();i < j;i++){
                if(scheduleList.get(i).getScheduleRefId().equals(model.getScheduleRefId())){
                    model = fillCodeDesc(model);
                    model.setLastRunStatus(new RunStatusModel("EDT"));
                    model.setEdit(true);
                    scheduleList.set(i, model);
                    break;
                }// end if
            }// end for
        }catch(Exception e){
            throw new BaseException(e.getMessage());
        }// end try/catch
        log.debug("Exiting BatchAppInfo.updateScheduleInList");
        return scheduleList;
    }// end updateScheduleInList

    /**
     * This method gets the descriptions for the associated codes
     * 
     * @param model
     *        the Schedule Model to update
     * @return ScheduleModel
     * @throws BaseException
     *         if an exception occurred
     */
    public ScheduleModel fillCodeDesc(ScheduleModel model) throws BaseException {
        log.debug("Entering BatchAppInfo.fillCodeDesc(). Incoming parameter is: ScheduleModel = " + String.valueOf(model));
        log.debug("This method gets the descriptions for the associated codes");
        try{
            if(!StringUtil.isNullOrEmpty(model.getFrequencyCd())){
                model.setFrequencyDesc(getCodeBean().findFrequencyCodeDescByCode(model.getFrequencyCd()));
            }// end if
            if(!StringUtil.isNullOrEmpty(model.getRepeatCd())){
                model.setRepeatDesc(getCodeBean().findRepeatCodeDescByCode(model.getRepeatCd()));
            }// end if
        }catch(Exception e){
            throw new BaseException(e.getMessage());
        }// end try/catch
        log.debug("Exiting BatchAppInfo.fillCodeDesc result=" + String.valueOf(model));
        return model;
    }// end fillCodeDesc

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
