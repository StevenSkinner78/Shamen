/**
 *
 */
package gov.doc.isu.shamen.ejb.jms;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.persistence.NoResultException;

import org.apache.log4j.Logger;

import gov.doc.isu.shamen.core.ShamenApplicationStatus;
import gov.doc.isu.shamen.ejb.StartupBean;
import gov.doc.isu.shamen.ejb.beans.view.ApplicationBeanLocal;
import gov.doc.isu.shamen.ejb.beans.view.ApplicationStatusUpdaterBeanLocal;
import gov.doc.isu.shamen.ejb.beans.view.BatchAppBeanLocal;
import gov.doc.isu.shamen.ejb.convertor.EJBObjectConvertor;
import gov.doc.isu.shamen.ejb.util.EJBConstants;
import gov.doc.isu.shamen.ejb.util.ShamenEJBUtil;
import gov.doc.isu.shamen.jms.ShamenCipher;
import gov.doc.isu.shamen.jms.beans.view.JmsManagerBeanLocal;
import gov.doc.isu.shamen.jms.convertor.JmsObjectConvertor;
import gov.doc.isu.shamen.jms.models.JmsApplication;
import gov.doc.isu.shamen.jms.models.JmsBatchApp;
import gov.doc.isu.shamen.jms.models.JmsRunStatus;
import gov.doc.isu.shamen.jms.models.JmsRunStatusSummary;
import gov.doc.isu.shamen.jpa.entity.ApplicationEntity;
import gov.doc.isu.shamen.jpa.entity.ApplicationInstanceEntity;
import gov.doc.isu.shamen.jpa.entity.BatchAppEntity;
import gov.doc.isu.shamen.jpa.entity.CommonEntity;
import gov.doc.isu.shamen.jpa.entity.ResultCodeEntity;
import gov.doc.isu.shamen.jpa.entity.RunStatusEntity;
import gov.doc.isu.shamen.jpa.entity.StatusCodeEntity;
import gov.doc.isu.shamen.taglib.models.RunStatusModel;

/**
 * This class used to execute the logic of a message driven consumer bean for the application queue.
 *
 * @author Shane Duncan JCCC
 * @author Zac Lisle JCCC
 */

@SuppressWarnings({"unchecked", "rawtypes"})
@MessageDriven(activationConfig = {@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"), @ActivationConfigProperty(propertyName = "destination", propertyValue = "CO.ShamWeb.REQST"), @ActivationConfigProperty(propertyName = "useJNDI", propertyValue = "true")})
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class ShamenJmsApplicationConsumer implements MessageListener {

    private Logger log = Logger.getLogger("gov.doc.isu.shamen.ejb.jms.ShamenJmsApplicationConsumer");

    @EJB
    private ApplicationBeanLocal applicationBean;

    @EJB
    private BatchAppBeanLocal batchAppBean;

    @EJB
    private ApplicationStatusUpdaterBeanLocal applicationStatusBean;

    @EJB
    private JmsManagerBeanLocal jmsManagerBean;

    private static volatile AtomicBoolean keepGoingApp = new AtomicBoolean();

    /**
     * This default constructor must be used to instantiate this class.
     */
    public ShamenJmsApplicationConsumer() {
        super();
    }// end constructor

    /**
     * This method is used to launch both the JMS producer and consumer message driven beans.
     * <p>
     * These beans are scheduled to run concurrently and are used to activate the data migration logic for the application.
     *
     * @param message
     *        The message received from the queue.
     */
    @Override
    public void onMessage(Message message) {
        log.debug("Entering onMessage. This method is used to launch both the JMS producer and consumer message driven beans.");
        try{
            keepGoingApp.set(true);
            while(keepGoingApp.get()){
                log.debug("***********Received a message**************** On CO.ShamWeb.REQST");

                // only process messages after Shamen was started.
                if(message.getJMSTimestamp() > StartupBean.getTimeInMillis().get()){
                    if(message instanceof ObjectMessage){
                        Object obj;
                        obj = ((ObjectMessage) message).getObject();
                        if(obj instanceof HashMap){
                            log.debug("Message is an application message.");
                            HashMap<String, String> messageMap = (HashMap) obj;

                            if(messageMap != null){
                                if(messageMap.containsKey(ShamenApplicationStatus.ACTION) || messageMap.containsKey(ShamenApplicationStatus.APPLICATION_NAME)){
                                    if(ShamenApplicationStatus.ACTION_REGISTER_FROM_CLIENT.equals(messageMap.get(ShamenApplicationStatus.ACTION))){
                                        log.debug("Process the message as a registration which has originated from a client.");
                                        registerFromClient(messageMap, message);
                                    }else if(ShamenApplicationStatus.ACTION_REGISTER.equals(messageMap.get(ShamenApplicationStatus.ACTION))){
                                        log.debug("Process the message as a registration which has originated from a client.");
                                        register(messageMap, message);
                                    }else{
                                        // insure that handshake has already been established.
                                        ApplicationEntity application = getApplication(messageMap);
                                        ApplicationInstanceEntity instance = getApplicationInstance(messageMap, application);
                                        if(instance.isHandshakeEstablished()){
                                            JmsApplication app = JmsObjectConvertor.toJms(application, instance);
                                            log.debug("Process the message as an encrypted application message.");
                                            // hold onto the unencrypted values.(only application name and application environment)
                                            HashMap<String, String> holdMap = new HashMap<String, String>();
                                            holdMap.put(ShamenApplicationStatus.APPLICATION_NAME, messageMap.remove(ShamenApplicationStatus.APPLICATION_NAME));
                                            holdMap.put(ShamenApplicationStatus.APPLICATION_ENVIRONMENT, messageMap.remove(ShamenApplicationStatus.APPLICATION_ENVIRONMENT));
                                            holdMap.put(ShamenApplicationStatus.APPLICATION_INSTANCE, messageMap.remove(ShamenApplicationStatus.APPLICATION_INSTANCE));
                                            holdMap.put(ShamenApplicationStatus.ACTION, messageMap.remove(ShamenApplicationStatus.ACTION));
                                            messageMap = app.getSc().decryptApplicationMap(messageMap);
                                            // reload the omitted raw values.
                                            messageMap.putAll(holdMap);
                                            processHashMapMessage(messageMap, message);
                                        }else{
                                            log.error("WARNING! Message was received from application without a prior handshake established.  Message is: " + messageMap);
                                        }// end if
                                    }// end if
                                }// end if
                            }// end if
                        }// end if
                    }// end if
                }else{
                    String holdMsg = message.toString();
                    if(message instanceof ObjectMessage){
                        Object obj;
                        obj = ((ObjectMessage) message).getObject();
                        if(obj instanceof HashMap){
                            log.debug("Message is an application message.");
                        }// end if
                        log.debug("Stale application message received that is being discarded.  Message is: " + String.valueOf(holdMsg) + " Startup timestamp is: " + StartupBean.getTimeInMillis().get());
                    }// end if
                }// end if/else
                keepGoingApp.set(false);
                // }// end if-else
            }// end while
        }catch(JMSException e){
            log.error("JMSException occurred while receiving a PTP message.  Exception is: " + e);
        }catch(Exception e){
            log.error("Exception occurred while receiving a PTP message.  Exception is: " + e);
        }// end try-catch
        log.debug("Exiting onMessage");
    }// end onMessage

    /**
     * This method returns the ApplicationInstanceEntity from the message map that matches the Application Entity.
     *
     * @param messageMap
     *        the HashMap<String, String> message map
     * @param application
     *        the Application Entity
     * @return ApplicationInstanceEntity
     */
    private ApplicationInstanceEntity getApplicationInstance(HashMap<String, String> messageMap, ApplicationEntity application) {
        log.debug("Entering getApplicationInstance");
        log.debug("This method returns the ApplicationInstanceEntity from the message map that matches the Application Entity.");
        log.debug("Entry parameters are: messageMap=" + String.valueOf(messageMap) + ", application=" + String.valueOf(application));
        String applicationInstance = messageMap.get(ShamenApplicationStatus.APPLICATION_INSTANCE);
        ApplicationInstanceEntity returnInstance = null;
        if(application != null && application.getApplicationInstances() != null){
            for(int i = 0, j = application.getApplicationInstances().size();i < j;i++){
                ApplicationInstanceEntity applicationInstanceEntity = application.getApplicationInstances().get(i);
                if(applicationInstanceEntity != null && applicationInstanceEntity.getApplicationInstanceName().equals(applicationInstance)){
                    returnInstance = applicationInstanceEntity;
                    break;
                }// end if
            }// end for
        }// end if
        log.debug("Return value is: returnInstance=" + String.valueOf(returnInstance));
        log.debug("Exiting getApplicationInstance");
        return returnInstance;
    }// end getApplicationInstance

    /**
     * This method queries the db to get the most recent application and converts it to a JmsApplication.
     *
     * @param messageMap
     *        HashMap<String, String> message map
     * @return applicationEntity
     * @throws Exception
     *         if an exception occurred
     */
    private ApplicationEntity getApplication(HashMap<String, String> messageMap) throws Exception {
        log.debug("Entering getApplication. This method queries the db to get the most recent application and converts it to a JmsApplication.");
        ApplicationEntity applicationEntity;
        String applicationName = messageMap.get(ShamenApplicationStatus.APPLICATION_NAME);
        String applicationEnvironment = messageMap.get(ShamenApplicationStatus.APPLICATION_ENVIRONMENT);
        try{
            applicationEntity = applicationBean.findByNameAndAddress(applicationName, applicationEnvironment);
        }catch(Exception e){
            log.error("Exception occurred getting the application entity and converting it to a JMS object", e);
            throw (e);
        }// end try-catch
        log.debug("Exiting getApplication with: " + String.valueOf(applicationEntity));
        return applicationEntity;
    }// end getApplication

    /**
     * This method does all the processing for HashMap messages.
     *
     * @param messageMap
     *        HashMap<String, String> message map
     * @param message
     *        message
     */
    private void processHashMapMessage(HashMap<String, String> messageMap, Message message) {
        log.debug("Entering processHashMapMessage. This method does all the processing for HashMap messages.");
        try{
            // JmsManager jms = JmsManager.getInstance();
            if(messageMap != null){

                if(messageMap.containsKey(ShamenApplicationStatus.ACTION)){
                    String action = (String) messageMap.get(ShamenApplicationStatus.ACTION);
                    if(ShamenApplicationStatus.ACTION_REPORT_STATUS.equals(action)){
                        processActionGiveMeStatus(messageMap, message);
                    }else if(ShamenApplicationStatus.ACTION_GET_APP_BATCH_JOBS.equals(action)){
                        processActionGetAppsBatchJobs(messageMap, message);
                    }else if(ShamenApplicationStatus.ACTION_GIVE_ME_BATCH_JOB.equals(action)){
                        processActionGiveMeBatchJob(messageMap, message);
                    }else if(ShamenApplicationStatus.ACTION_GET_APP_NOTIFICATION.equals(action)){
                        processActionGiveMeAppNotification(messageMap, message);
                    }else if(ShamenApplicationStatus.ACTION_DEATH_NOTIFICATION.equals(action)){
                        processDeathNotification(messageMap, message);
                    }else if(ShamenApplicationStatus.ACTION_RUN_BATCH.equals(action)){
                        processRunBatch(messageMap, message);
                    }else if(ShamenApplicationStatus.ACTION_GET_RUN_STATUS_SUMMARIES.equals(action)){
                        processActionGetRunStatusSummaries(messageMap, message);
                    }else if(ShamenApplicationStatus.ACTION_GET_RUN_STATUS_INFO.equals(action)){
                        processActionGetRunStatusInfo(messageMap, message);
                    }// end if/else
                }// end if
            }// end if
        }catch(Exception e){
            log.error("Exception occurred while processing a PTP message in processHashMapMessage.  Exception is: " + e);
        }// end try-catch
        log.debug("Exiting processHashMapMessage");
    }// end processHashMapMessage

    /**
     * This method does all the processing for request of Give Me Status. This means that it will get the application's status from the DB and then return it via a tempQueue.
     *
     * @param messageMap
     *        HashMap<String, String> message map
     * @param message
     *        message
     * @throws Exception
     *         if an exception occurred
     */
    private void processDeathNotification(HashMap<String, String> messageMap, Message message) throws Exception {
        log.debug("Entering processActionGiveMeStatus. This method does all the processing for request of Give Me Status.  This means that it will get the application's status from the DB and then return it via a tempQueue.");
        if(messageMap.containsKey(ShamenApplicationStatus.APPLICATION_NAME)){
            log.debug("Get the application record for " + String.valueOf(messageMap.get(ShamenApplicationStatus.APPLICATION_NAME)) + " - " + String.valueOf(messageMap.get(ShamenApplicationStatus.APPLICATION_ENVIRONMENT)));
            ApplicationEntity application;
            ApplicationInstanceEntity instance;
            try{
                application = getApplication(messageMap);
                instance = getApplicationInstance(messageMap, application);
                log.info("Death notification received from: " + instance.getFormattedAppKey());

                applicationBean.deleteApplicationInstance(instance.getApplicationInstanceRefId(), 0L);
            }catch(NoResultException e){
                log.error("Unable to get the application.  Exception is: " + e);
                throw (e);
            }catch(Exception e){
                log.error("Exception encountered processing request of GIVE ME STATUS from application.", e);
                throw (e);
            }// end try-catch
        }// end if
        log.debug("Exiting processActionGiveMeStatus");
    }// end processActionGiveMeStatus

    /**
     * This method does all the processing for request of Give Me Status. This means that it will get the application's status from the DB and then return it via a tempQueue.
     *
     * @param messageMap
     *        HashMap<String, String> message map
     * @param message
     *        message
     * @throws Exception
     *         if an exception occurred
     */
    private void processActionGiveMeStatus(HashMap<String, String> messageMap, Message message) throws Exception {
        log.debug("Entering processActionGiveMeStatus. This method does all the processing for request of Give Me Status.  This means that it will get the application's status from the DB and then return it via a tempQueue.");
        if(messageMap.containsKey(ShamenApplicationStatus.APPLICATION_NAME)){
            log.debug("Get the application record for " + String.valueOf(messageMap.get(ShamenApplicationStatus.APPLICATION_NAME)) + " - " + String.valueOf(messageMap.get(ShamenApplicationStatus.APPLICATION_ENVIRONMENT)));
            ApplicationEntity application;
            try{
                application = getApplication(messageMap);
                log.debug("Request for application status received from: " + application.appInfo());

                HashMap<String, String> outgoingMsg = new HashMap<String, String>();
                outgoingMsg.put(ShamenApplicationStatus.ACTION, ShamenApplicationStatus.ACTION_CHANGE_YOUR_STATUS);
                outgoingMsg.put(ShamenApplicationStatus.STATUS, application.getApplicationStatus().getApplicationStatusCd());
                outgoingMsg.put(ShamenApplicationStatus.STATUS_REASON, application.getStatusComment());
                outgoingMsg.put(ShamenApplicationStatus.STATUS_TIME_SECONDS, String.valueOf(System.currentTimeMillis()));
                // Get the deployment information out of the message.
                ApplicationInstanceEntity instance = getApplicationInstance(messageMap, application);
                instance = loadDeploymentInfo(messageMap, instance);
                JmsApplication app = JmsObjectConvertor.toJms(application, instance);
                outgoingMsg = app.getSc().encryptApplicationMap(outgoingMsg);
                log.debug("Acknowledge the GIVE_ME_STATUS message");
                jmsManagerBean.acknowlegePTPMessageWithHashMap(message, outgoingMsg);
                applicationBean.update(application);
            }catch(NoResultException e){
                log.error("Unable to get the application.  Exception is: " + e);
                throw (e);
            }catch(Exception e){
                log.error("Exception encountered processing request of GIVE ME STATUS from application.", e);
                throw (e);
            }// end try-catch
        }// end if
        log.debug("Exiting processActionGiveMeStatus");
    }// end processActionGiveMeStatus

    /**
     * This method does all the processing for request of Give Me App Notification. This means that it will get the application's notification desc from the DB and then return it via a tempQueue.
     *
     * @param messageMap
     *        HashMap<String, String> message map
     * @param message
     *        message
     * @throws Exception
     *         if an exception occurred
     */
    private void processActionGiveMeAppNotification(HashMap<String, String> messageMap, Message message) throws Exception {
        log.debug("Entering processActionGiveMeAppNotification. This method does all the processing for request of Give Me App Notification. This means that it will get the application's notification desc from the DB and then return it via a tempQueue.");
        if(messageMap.containsKey(ShamenApplicationStatus.APPLICATION_NAME)){
            log.debug("Get the application record for " + String.valueOf(messageMap.get(ShamenApplicationStatus.APPLICATION_NAME)) + " - " + String.valueOf(messageMap.get(ShamenApplicationStatus.APPLICATION_ENVIRONMENT)));
            ApplicationEntity application;
            try{
                application = getApplication(messageMap);
                log.debug("Request for application notification received from: " + application.appInfo());

                HashMap<String, String> outgoingMsg = new HashMap<String, String>();
                outgoingMsg.put(ShamenApplicationStatus.ACTION, ShamenApplicationStatus.ACTION_GET_APP_NOTIFICATION);
                outgoingMsg.put(ShamenApplicationStatus.SHOW_APP_NOTIFICATION, application.getShowApplicationNotification());
                if("Y".equalsIgnoreCase(application.getShowApplicationNotification())){
                    outgoingMsg.put(ShamenApplicationStatus.APP_NOTIFICATION_DETAIL, application.getApplicationNotificationDesc());
                }else{
                    outgoingMsg.put(ShamenApplicationStatus.APP_NOTIFICATION_DETAIL, "");
                }// end if/else
                 // Get the deployment information out of the message.
                ApplicationInstanceEntity instance = getApplicationInstance(messageMap, application);
                instance = loadDeploymentInfo(messageMap, instance);
                JmsApplication app = JmsObjectConvertor.toJms(application, instance);
                outgoingMsg = app.getSc().encryptApplicationMap(outgoingMsg);
                log.debug("Acknowledge the GET_APP_NOTIFICATION message");
                jmsManagerBean.acknowlegePTPMessageWithHashMap(message, outgoingMsg);
            }catch(NoResultException e){
                log.error("Unable to get the application.  Exception is: " + e);
                throw (e);
            }catch(Exception e){
                log.error("Exception encountered processing request of GIVE ME NOTIFICATION from application.", e);
                throw (e);
            }// end try-catch
        }// end if
        log.debug("Exiting processActionGiveMeAppNotification");
    }// end processActionGiveMeAppNotification

    /**
     * This method loads the additional deployment information that is in the message. It puts it into the provided instance and returns it.
     *
     * @param messageMap
     *        HashMap<String, String> message map
     * @param instance
     *        instance
     * @return ApplicationInstanceEntity with all the deployment information added.
     */
    private ApplicationInstanceEntity loadDeploymentInfo(HashMap<String, String> messageMap, ApplicationInstanceEntity instance) {
        instance.setVerifiedAddtnlInfo(messageMap.get(ShamenApplicationStatus.APPLICATION_ADDNTL_INFO));
        instance.setVerifiedBuildNm(messageMap.get(ShamenApplicationStatus.APPLICATION_BRANCH));
        instance.setVerifiedEarNm(messageMap.get(ShamenApplicationStatus.APPLICATION_EAR));
        instance.setVerifiedVersionNm(messageMap.get(ShamenApplicationStatus.APPLICATION_VERSION));
        return instance;
    }// end loadDeploymentInfo

    /**
     * This method does all the processing for request of GET APPS BATCH JOBS. This means that it will get the application's batch jobs from the DB and then return them via a tempQueue.
     *
     * @param messageMap
     *        HashMap<String, String> message map
     * @param message
     *        message
     * @throws Exception
     *         if an exception occurred
     */
    private void processActionGetAppsBatchJobs(HashMap<String, String> messageMap, Message message) throws Exception {
        log.debug("Entering processActionGiveMeStatus. This method does all the processing for request of GET APPS BATCH JOBS. This means that it will get the application's batch jobs from the DB and then return them via a tempQueue.");
        if(messageMap.containsKey(ShamenApplicationStatus.APPLICATION_NAME)){
            log.debug("Get the application record for " + String.valueOf(messageMap.get(ShamenApplicationStatus.APPLICATION_NAME)) + " - " + String.valueOf(messageMap.get(ShamenApplicationStatus.APPLICATION_ENVIRONMENT)));
            ApplicationEntity application;
            ApplicationInstanceEntity applicationInstance;
            try{
                application = applicationBean.findByNameAndAddress((String) messageMap.get(ShamenApplicationStatus.APPLICATION_NAME), (String) messageMap.get(ShamenApplicationStatus.APPLICATION_ENVIRONMENT));
                applicationInstance = getApplicationInstance(messageMap, application);
                log.debug("Request to GET_APP_BATCH_JOBS received from: " + application);
                HashMap<String, Object> outgoingMsg = new HashMap<String, Object>();
                outgoingMsg.put(ShamenApplicationStatus.ACTION, ShamenApplicationStatus.ACTION_GET_APP_BATCH_JOBS);
                outgoingMsg.put(ShamenApplicationStatus.STATUS_TIME_SECONDS, String.valueOf(System.currentTimeMillis()));

                List<BatchAppEntity> batchList = batchAppBean.getAllBatchesAndCollectionsForApplication(application.getApplicationRefId());

                List<JmsBatchApp> batchAppList = JmsObjectConvertor.toBatchAppListBusiness(batchList);

                outgoingMsg.put(ShamenApplicationStatus.BATCH_APPS, batchAppList);
                outgoingMsg = JmsObjectConvertor.toJms(application, applicationInstance).getSc().encryptObjectApplicationMap(outgoingMsg);
                log.debug("Acknowledge the GET_APP_BATCH_JOBS message");
                jmsManagerBean.acknowlegePTPMessageWithObjectHashMap(message, outgoingMsg);

            }catch(NoResultException e){
                log.error("Unable to get the batch list.  Exception is: " + e);
                throw (e);
            }catch(Exception e){
                log.error("Exception encountered processing request of GET_APP_BATCH_JOBS from application.", e);
                throw (e);
            }// end try-catch
        }// end if
        log.debug("Exiting processActionGiveMeStatus");
    }// end processActionGetAppsBatchJobs

    /**
     * This method does all the processing for request of GET APPS BATCH JOBS. This means that it will get the application's batch jobs from the DB and then return them via a tempQueue.
     *
     * @param messageMap
     *        HashMap<String, String> message map
     * @param message
     *        message
     * @throws Exception
     *         if an exception occurred
     */
    private void processActionGiveMeBatchJob(HashMap<String, String> messageMap, Message message) throws Exception {
        log.debug("Entering processActionGiveMeBatchJob. This method does all the processing for request of GET ME BATCH JOB. This means that it will get the application's batch job from the DB and then return them via a tempQueue.");
        if(messageMap.containsKey(ShamenApplicationStatus.REF_ID)){
            log.debug("Get the batchApp record for " + String.valueOf(messageMap.get(ShamenApplicationStatus.REF_ID)));

            try{

                ApplicationEntity app = getApplication(messageMap);
                ApplicationInstanceEntity applicationInstance = getApplicationInstance(messageMap, app);
                log.debug("Request to GIVE_ME_BATCH_JOB from: " + app);
                HashMap<String, Object> outgoingMsg = new HashMap<String, Object>();
                outgoingMsg.put(ShamenApplicationStatus.ACTION, ShamenApplicationStatus.ACTION_GIVE_ME_BATCH_JOB);
                outgoingMsg.put(ShamenApplicationStatus.STATUS_TIME_SECONDS, String.valueOf(System.currentTimeMillis()));
                BatchAppEntity entity = batchAppBean.findBatchByRefId((Long.valueOf((String) messageMap.get(ShamenApplicationStatus.REF_ID))));
                if(!ShamenEJBUtil.isEmpty(entity.getSchedule())){
                    entity.setSchedule(batchAppBean.checkOnScheduleForBatchDetail(entity.getSchedule(), entity.getBatchAppRefId()));
                }// end if
                Integer count = 0;
                Long startRow = 0L;
                Long endRow = 20L;
                String code = null;
                List<RunStatusEntity> runStatusList = new ArrayList<RunStatusEntity>();
                if(!ShamenEJBUtil.isNullOrEmpty(messageMap.get(ShamenApplicationStatus.PAGING_START_ROW))){
                    startRow = Long.valueOf(messageMap.get(ShamenApplicationStatus.PAGING_START_ROW));
                }// end if
                if(!ShamenEJBUtil.isNullOrEmpty(messageMap.get(ShamenApplicationStatus.PAGING_END_ROW))){
                    endRow = Long.valueOf(messageMap.get(ShamenApplicationStatus.PAGING_END_ROW));
                }else if(!ShamenEJBUtil.isNullOrEmpty(messageMap.get(ShamenApplicationStatus.PAGE_SIZE))){
                    endRow = Long.valueOf(messageMap.get(ShamenApplicationStatus.PAGE_SIZE));
                }// end if/else
                if(!ShamenEJBUtil.isNullOrEmpty(messageMap.get(ShamenApplicationStatus.FILTER_RUN_STATUS))){
                    code = messageMap.get(ShamenApplicationStatus.FILTER_RUN_STATUS).toString();
                }// end if
                count = batchAppBean.countRunStatus(entity.getBatchAppRefId(), code);
                if("COL".equals(entity.getBatchType().getBatchTypeCd())){
                    runStatusList = EJBObjectConvertor.toRunStatusListBusinessFromObjForCollectionDetail(batchAppBean.getCollectionRunStatusListByPage(entity.getBatchAppRefId(), startRow, endRow, code));
                }else{
                    runStatusList = EJBObjectConvertor.toRunStatusListBusinessFromObjForDetail(batchAppBean.getRunStatusListByPageAndResult(entity.getBatchAppRefId(), startRow, endRow, code));
                }// end if-else
                entity.setRunStatuses(runStatusList);
                JmsBatchApp batchApp = JmsObjectConvertor.toJmsForClientApp(entity);

                outgoingMsg.put(ShamenApplicationStatus.BATCH_APPS, batchApp);
                outgoingMsg.put(ShamenApplicationStatus.RUN_STATUS_COUNT, count);
                outgoingMsg = JmsObjectConvertor.toJms(app, applicationInstance).getSc().encryptObjectApplicationMap(outgoingMsg);
                log.debug("Acknowledge the GIVE_ME_BATCH_JOB message");
                jmsManagerBean.acknowlegePTPMessageWithObjectHashMap(message, outgoingMsg);

            }catch(NoResultException e){
                log.error("Unable to get the batch job.  Exception is: " + e);
                throw (e);
            }catch(Exception e){
                log.error("Exception encountered processing request of GiveMeBatchJob from application.", e);
                throw (e);
            }// end try-catch
        }// end if
        log.debug("Exiting processActionGiveMeBatchJob");
    }// end processActionGiveMeBatchJob

    /**
     * This method processes a client application's request to run a batch job.
     *
     * @param messageMap
     *        HashMap<String, String> message map
     * @param message
     *        message
     * @throws Exception
     *         if an exception occurred
     */
    private void processRunBatch(HashMap<String, String> messageMap, Message message) throws Exception {
        log.debug("Entering processRunBatch. This method processes a client application's request to run a batch job.");
        if(messageMap.containsKey(ShamenApplicationStatus.REF_ID)){
            log.debug("Run the batchApp with batchAppRefId: " + String.valueOf(messageMap.get(ShamenApplicationStatus.REF_ID)));

            try{
                ApplicationEntity app = getApplication(messageMap);
                ApplicationInstanceEntity applicationInstance = getApplicationInstance(messageMap, app);
                log.info("Request to run batch application received from: " + app);
                HashMap<String, Object> outgoingMsg = new HashMap<String, Object>();
                outgoingMsg.put(ShamenApplicationStatus.ACTION, ShamenApplicationStatus.ACTION_RUN_BATCH);
                outgoingMsg.put(ShamenApplicationStatus.STATUS_TIME_SECONDS, String.valueOf(System.currentTimeMillis()));
                BatchAppEntity batchApp = batchAppBean.findBatchByRefId((Long.valueOf((String) messageMap.get(ShamenApplicationStatus.REF_ID))));
                String userId = messageMap.get(ShamenApplicationStatus.USER_REF_ID);
                String jobParameters = messageMap.get(ShamenApplicationStatus.JOB_PARAMETERS);
                String runNumberString = String.valueOf(System.currentTimeMillis());
                RunStatusEntity runStatusEntity = new RunStatusEntity();
                runStatusEntity.setRunNumber(Long.valueOf(runNumberString.substring(8)));
                runStatusEntity.setStatus(new StatusCodeEntity());
                runStatusEntity.getStatus().setStatusCd(JmsRunStatus.STATUS_STARTING);
                runStatusEntity.setResult(new ResultCodeEntity());
                runStatusEntity.getResult().setResultCd(JmsRunStatus.RESULTS_PENDING);
                runStatusEntity.setCommon(new CommonEntity());
                runStatusEntity.getCommon().setCreateUserRefId(Long.valueOf(EJBConstants.CLIENT_APP_USER_REF_ID));
                runStatusEntity.getCommon().setUpdateUserRefId(Long.valueOf(EJBConstants.CLIENT_APP_USER_REF_ID));
                runStatusEntity.setStartTime(ShamenEJBUtil.getCurrentTimestamp());
                runStatusEntity.setStopTime(ShamenEJBUtil.getSqlTimestamp(ShamenEJBUtil.DEFAULT_TIMESTAMP));

                runStatusEntity.setBatchApp(batchApp);
                jmsManagerBean.startBatchApp(runStatusEntity, batchApp.getBatchType().getBatchTypeCd(), userId, jobParameters);
                outgoingMsg.put(ShamenApplicationStatus.BATCH_APPS, batchApp);
                outgoingMsg = JmsObjectConvertor.toJms(app, applicationInstance).getSc().encryptObjectApplicationMap(outgoingMsg);
                log.debug("Acknowledge the ACTION_RUN_BATCH message");
                jmsManagerBean.acknowlegePTPMessageWithObjectHashMap(message, outgoingMsg);

            }catch(NoResultException e){
                log.error("Unable to get the batch job.  Exception is: " + e);
                throw (e);
            }catch(Exception e){
                log.error("Exception encountered processing request of batch job run from application.", e);
                throw (e);
            }// end try-catch
        }// end if
        log.debug("Exiting processRunBatch");
    }// end processRunBatch

    /**
     * This method does all the processing for registering a batch instance. this should be the process that was instigated by ShamenWeb
     *
     * @param messageMap
     *        HashMap<String, String> message map
     * @param message
     *        message
     * @return ApplicationInstanceEntity
     */
    private ApplicationInstanceEntity register(HashMap<String, String> messageMap, Message message) {
        log.debug("Entering registerFromClient. This method does all the processing for registering a batch instance.");
        ApplicationEntity application = null;
        ApplicationInstanceEntity applicationInstance = null;
        try{
            // decrypt the application information
            ShamenCipher sc = new ShamenCipher();
            // remove the non encrypted entry from the map
            messageMap.remove(ShamenApplicationStatus.ACTION);
            // decrypt
            messageMap = sc.decryptApplicationMap(messageMap);
            if(messageMap.containsKey(ShamenApplicationStatus.APPLICATION_NAME)){
                log.info("Registering application instance: " + messageMap.get(ShamenApplicationStatus.APPLICATION_NAME) + " - " + messageMap.get(ShamenApplicationStatus.APPLICATION_ENVIRONMENT) + " - " + messageMap.get(ShamenApplicationStatus.APPLICATION_INSTANCE));
                // retrieve this application
                application = getApplication(messageMap);
                if(application != null){
                    applicationInstance = getApplicationInstance(messageMap, application);
                    if(applicationInstance == null){
                        applicationInstance = new ApplicationInstanceEntity();
                        applicationInstance.setApplicationInstanceName(messageMap.get(ShamenApplicationStatus.APPLICATION_INSTANCE));
                        applicationInstance.setApplication(application);
                        applicationInstance.setVerifiedApplicationStatusCd(ApplicationStatusUpdaterBeanLocal.STATUS_AWAITING_RESPONSE);
                        applicationInstance.setEncryptionKey(null);
                        CommonEntity common = new CommonEntity();
                        common.setCreateTime(new Timestamp(System.currentTimeMillis()));
                        common.setCreateUserRefId(application.getCommon().getCreateUserRefId());
                        common.setDeleteIndicator("N");
                        applicationInstance.setCommon(common);
                        applicationInstance.setInstantiationTs(new Timestamp(System.currentTimeMillis()));
                        applicationInstance = loadDeploymentInfo(messageMap, applicationInstance);
                        applicationBean.create(applicationInstance);
                    }else{
                        applicationInstance.setInstantiationTs(new Timestamp(System.currentTimeMillis()));
                        applicationInstance.setVerifiedApplicationStatusCd(ApplicationStatusUpdaterBeanLocal.STATUS_AWAITING_RESPONSE);
                        applicationInstance.setEncryptionKey(null);
                        applicationBean.update(applicationInstance);
                    }// end if/else
                    HashMap<String, String> outgoingMsg = new HashMap<String, String>();
                    outgoingMsg.put(ShamenApplicationStatus.ACTION, ShamenApplicationStatus.ACTION_MESSAGE_CONFIRMED);
                    outgoingMsg.put(ShamenApplicationStatus.STATUS_TIME_SECONDS, String.valueOf(System.currentTimeMillis()));
                    // Get the deployment information out of the message.
                    application = getApplication(messageMap);
                    ApplicationInstanceEntity instance = getApplicationInstance(messageMap, application);
                    // instance = loadDeploymentInfo(messageMap, instance);
                    JmsApplication app = JmsObjectConvertor.toJms(application, instance);
                    outgoingMsg = app.getSc().encryptApplicationMap(outgoingMsg);
                    log.debug("Acknowledge the REGISTER message");
                    jmsManagerBean.acknowlegePTPMessageWithHashMap(message, outgoingMsg);
                }// end if
            }// end if
        }catch(NoResultException e){
            log.warn("Unable to get the application.  Exception is: " + e);
        }catch(ShamenJMSException e){
            log.error("ShamenJMSException occurred while processing a PTP message in register.  Exception is: " + e);
        }catch(Exception e){
            log.error("Exception occurred while processing a PTP message in register.  Exception is: " + e);
        }// end try-catch
        log.debug("Exiting register");
        return applicationInstance;
    }// end register

    /**
     * This method does all the processing for registering a batch instance and kicking off the handshake process.
     *
     * @param messageMap
     *        HashMap<String, String> message map
     * @param message
     *        message
     * @return ApplicationInstanceEntity
     */
    private ApplicationInstanceEntity registerFromClient(HashMap<String, String> messageMap, Message message) {
        log.debug("Entering registerFromClient. This method does all the processing for registering a batch instance.");
        ApplicationEntity application = null;
        ApplicationInstanceEntity applicationInstance = null;
        try{
            // decrypt the application information
            ShamenCipher sc = new ShamenCipher();
            // remove the non encrypted entry from the map
            messageMap.remove(ShamenApplicationStatus.ACTION);
            // decrypt
            messageMap = sc.decryptApplicationMap(messageMap);
            if(messageMap.containsKey(ShamenApplicationStatus.APPLICATION_NAME)){
                log.info("Registering application instance: " + messageMap.get(ShamenApplicationStatus.APPLICATION_NAME) + " - " + messageMap.get(ShamenApplicationStatus.APPLICATION_ENVIRONMENT) + " - " + messageMap.get(ShamenApplicationStatus.APPLICATION_INSTANCE));
                // retrieve this application
                application = getApplication(messageMap);
                if(application != null){
                    applicationInstance = getApplicationInstance(messageMap, application);
                    if(applicationInstance == null){
                        applicationInstance = new ApplicationInstanceEntity();
                        applicationInstance.setApplicationInstanceName(messageMap.get(ShamenApplicationStatus.APPLICATION_INSTANCE));
                        applicationInstance.setApplication(application);
                        applicationInstance.setVerifiedApplicationStatusCd(ApplicationStatusUpdaterBeanLocal.STATUS_AWAITING_RESPONSE);
                        applicationInstance.setEncryptionKey(null);
                        CommonEntity common = new CommonEntity();
                        common.setCreateTime(new Timestamp(System.currentTimeMillis()));
                        common.setCreateUserRefId(application.getCommon().getCreateUserRefId());
                        common.setDeleteIndicator("N");
                        applicationInstance.setCommon(common);
                        applicationInstance.setInstantiationTs(new Timestamp(System.currentTimeMillis()));
                        applicationBean.create(applicationInstance);
                    }else{
                        applicationInstance.setInstantiationTs(new Timestamp(System.currentTimeMillis()));
                        applicationInstance.setVerifiedApplicationStatusCd(ApplicationStatusUpdaterBeanLocal.STATUS_AWAITING_RESPONSE);
                        applicationInstance.setEncryptionKey(null);
                        applicationBean.update(applicationInstance);
                    }// end if/else
                    application = getApplication(messageMap);
                    applicationInstance = getApplicationInstance(messageMap, application);
                    applicationStatusBean.retrieveAsyncStatusFromApplication(application, applicationInstance);

                    HashMap outgoingMsg = new HashMap();
                    outgoingMsg.put(ShamenApplicationStatus.ACTION, ShamenApplicationStatus.ACTION_MESSAGE_CONFIRMED);
                    outgoingMsg.put(ShamenApplicationStatus.STATUS, "DUMMY");
                    outgoingMsg.put(ShamenApplicationStatus.STATUS_REASON, "DUMMY");
                    outgoingMsg.put(ShamenApplicationStatus.STATUS_TIME_SECONDS, String.valueOf(System.currentTimeMillis()));
                    log.debug("Acknowledge the message");
                    jmsManagerBean.acknowlegePTPMessageWithHashMap(message, outgoingMsg);
                    log.info("Application instance: " + messageMap.get(ShamenApplicationStatus.APPLICATION_NAME) + " - " + messageMap.get(ShamenApplicationStatus.APPLICATION_ENVIRONMENT) + " - " + messageMap.get(ShamenApplicationStatus.APPLICATION_INSTANCE) + " registration successfully acknowledged.");

                }// end if
            }// end if
        }catch(NoResultException e){
            log.warn("Unable to get the application.  Exception is: " + e);
        }catch(ShamenJMSException e){
            log.error("ShamenJMSException occurred while processing a PTP message in register.  Exception is: " + e);
        }catch(Exception e){
            log.error("Exception occurred while processing a PTP message in register.  Exception is: " + e);
        }// end try-catch
        log.debug("Exiting register");
        return applicationInstance;
    }// end registerFromClient

    /**
     * This method does all the processing for request of GET RUN STATUS SUMMARIES. This means that it will get the run status summaries for the provided batch application from the DB and then return them via a tempQueue.
     *
     * @param messageMap
     *        HashMap<String, String> message map
     * @param message
     *        message
     * @throws Exception
     *         if an exception occurred
     */
    private void processActionGetRunStatusSummaries(HashMap<String, String> messageMap, Message message) throws Exception {
        log.debug("Entering processActionGetRunStatusSummaries. This method does all the processing for request of GET RUN STATUS SUMMARIES. This means that it will get the run status summaries for the provided batch application from the DB and then return them via a tempQueue.");
        if(messageMap.containsKey(ShamenApplicationStatus.APPLICATION_NAME)){
            log.debug("Get the application record for " + String.valueOf(messageMap.get(ShamenApplicationStatus.APPLICATION_NAME)) + " - " + String.valueOf(messageMap.get(ShamenApplicationStatus.APPLICATION_ENVIRONMENT)));
            ApplicationEntity application;
            ApplicationInstanceEntity applicationInstance;
            try{
                application = applicationBean.findByNameAndAddress((String) messageMap.get(ShamenApplicationStatus.APPLICATION_NAME), (String) messageMap.get(ShamenApplicationStatus.APPLICATION_ENVIRONMENT));
                applicationInstance = getApplicationInstance(messageMap, application);
                String pastDate = messageMap.get(ShamenApplicationStatus.STATUS_PARAMETERS);
                String batchAppNm = messageMap.get(ShamenApplicationStatus.BATCH_APP_NM);
                log.debug("Request to ACTION_GET_RUN_STATUS_SUMMARIES received from: " + application);
                HashMap<String, Object> outgoingMsg = new HashMap<String, Object>();
                outgoingMsg.put(ShamenApplicationStatus.ACTION, ShamenApplicationStatus.ACTION_GET_RUN_STATUS_SUMMARIES);
                outgoingMsg.put(ShamenApplicationStatus.STATUS_TIME_SECONDS, String.valueOf(System.currentTimeMillis()));

                List<Object> summaryList = batchAppBean.getBatchAppRunStatusSummaries(batchAppNm, pastDate);

                List<JmsRunStatusSummary> jmsSummaryList = JmsObjectConvertor.toJmsStatusSummaries(summaryList);

                outgoingMsg.put(ShamenApplicationStatus.RUN_STATUS_SUMMARIES, jmsSummaryList);
                outgoingMsg = JmsObjectConvertor.toJms(application, applicationInstance).getSc().encryptObjectApplicationMap(outgoingMsg);
                log.debug("Acknowledge the GET_RUN_STATUS_SUMMARIES message");
                jmsManagerBean.acknowlegePTPMessageWithObjectHashMap(message, outgoingMsg);
            }catch(NoResultException e){
                log.error("Unable to get the Status Summary List.  Exception is: " + e);
                throw (e);
            }catch(Exception e){
                log.error("Exception encountered processing request of GET_RUN_STATUS_SUMMARIES from application.", e);
                throw (e);
            }// end try-catch
        }// end if
        log.debug("Exiting processActionGetRunStatusSummaries");
    }// end processActionGetRunStatusSummaries

    /**
     * This method does all the processing for request of GET RUN STATUS INFO. This means that it will get the run status for the provided run number from the DB and then return them via a tempQueue.
     *
     * @param messageMap
     *        HashMap<String, String> message map
     * @param message
     *        message
     * @throws Exception
     *         if an exception occurred
     */
    private void processActionGetRunStatusInfo(HashMap<String, String> messageMap, Message message) throws Exception {
        log.debug("Entering processActionGetRunStatusInfo. This method does all the processing for request of GET RUN STATUS SUMMARIES. This means that it will get the run status summaries for the provided batch application from the DB and then return them via a tempQueue.");
        if(messageMap.containsKey(ShamenApplicationStatus.APPLICATION_NAME)){
            log.debug("Get the application record for " + String.valueOf(messageMap.get(ShamenApplicationStatus.APPLICATION_NAME)) + " - " + String.valueOf(messageMap.get(ShamenApplicationStatus.APPLICATION_ENVIRONMENT)));
            ApplicationEntity application;
            ApplicationInstanceEntity applicationInstance;
            try{
                application = applicationBean.findByNameAndAddress((String) messageMap.get(ShamenApplicationStatus.APPLICATION_NAME), (String) messageMap.get(ShamenApplicationStatus.APPLICATION_ENVIRONMENT));
                applicationInstance = getApplicationInstance(messageMap, application);
                String batchAppRefId = messageMap.get(ShamenApplicationStatus.BATCH_APP_REF_ID); 
                String runNumber = messageMap.get(ShamenApplicationStatus.RUN_NUMBER);
                log.debug("Request to ACTION_GET_RUN_STATUS_INFO received from: " + application);
                HashMap<String, Object> outgoingMsg = new HashMap<String, Object>();
                outgoingMsg.put(ShamenApplicationStatus.ACTION, ShamenApplicationStatus.ACTION_GET_RUN_STATUS_INFO);
                outgoingMsg.put(ShamenApplicationStatus.STATUS_TIME_SECONDS, String.valueOf(System.currentTimeMillis()));

                List<Object> list = batchAppBean.getRunStatusListByRunNumber(Long.valueOf(batchAppRefId), Long.valueOf(runNumber));

                List<RunStatusEntity> modelStatusList = EJBObjectConvertor.toRunStatusListBusinessFromObjForDetail(list);

                List<JmsRunStatus> jmsStatusList = JmsObjectConvertor.toJmsRunStatusList(modelStatusList);

                outgoingMsg.put(ShamenApplicationStatus.RUN_STATUS_LIST, jmsStatusList);
                outgoingMsg = JmsObjectConvertor.toJms(application, applicationInstance).getSc().encryptObjectApplicationMap(outgoingMsg);
                log.debug("Acknowledge the GET_RUN_STATUS_INFO message");
                jmsManagerBean.acknowlegePTPMessageWithObjectHashMap(message, outgoingMsg);
            }catch(NoResultException e){
                log.error("Unable to get the Status Summary List.  Exception is: " + e);
                throw (e);
            }catch(Exception e){
                log.error("Exception encountered processing request of GET_RUN_STATUS_INFO from application.", e);
                throw (e);
            }// end try-catch
        }// end if
        log.debug("Exiting processActionGetRunStatusInfo");
    }// end processActionGetRunStatusInfo
}// end class
