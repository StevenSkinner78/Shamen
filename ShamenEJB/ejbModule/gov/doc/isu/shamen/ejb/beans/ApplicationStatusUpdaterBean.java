/**
 *
 */
package gov.doc.isu.shamen.ejb.beans;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TemporaryQueue;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;

import gov.doc.isu.shamen.core.ShamenApplicationStatus;
import gov.doc.isu.shamen.ejb.beans.view.ApplicationBeanLocal;
import gov.doc.isu.shamen.ejb.beans.view.ApplicationStatusUpdaterBeanLocal;
import gov.doc.isu.shamen.ejb.beans.view.ApplicationStatusUpdaterBeanRemote;
import gov.doc.isu.shamen.ejb.util.ShamenEJBUtil;
import gov.doc.isu.shamen.jms.ShamenCipher;
import gov.doc.isu.shamen.jms.convertor.JmsObjectConvertor;
import gov.doc.isu.shamen.jms.models.JmsApplication;
import gov.doc.isu.shamen.jpa.entity.ApplicationEntity;
import gov.doc.isu.shamen.jpa.entity.ApplicationInstanceEntity;
import gov.doc.isu.shamen.jpa.entity.ApplicationStatusCodeEntity;

/**
 * This Bean confirms and updates an application's status. It performs all JMS operations and then updates the <code>ApplicationStatusBean</code>. This bean had to be created so prevent screen lag on the applications screen.
 *
 * @see ApplicationStatusBean
 * @author Shane Duncan - JCCC 3-16-2017
 */
@Stateless
@Local(ApplicationStatusUpdaterBeanLocal.class)
@Remote(ApplicationStatusUpdaterBeanRemote.class)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class ApplicationStatusUpdaterBean implements ApplicationStatusUpdaterBeanLocal, ApplicationStatusUpdaterBeanRemote {

    private static final long serialVersionUID = -2248129051203768991L;

    private Logger logger = Logger.getLogger("gov.doc.isu.shamen.ejb.beans.ApplicationStatusUpdaterBean");
    @PersistenceContext(unitName = "ShamenJPA")
    private EntityManager em;
    @EJB
    private ApplicationBeanLocal applicationBean;
    @Resource(name = "CO_SconT", type = ConnectionFactory.class)
    private ConnectionFactory factory;
    @Resource(name = "CO.WebApp.TOPIC", type = Destination.class)
    private Destination destination;

    /**
     * {@inheritDoc}
     */
    public List<ApplicationEntity> getApplicationList() throws Exception {
        logger.debug("Entering getApplicationList");
        logger.debug("This method is used to return a list of ApplicationEntitys.");
        List<ApplicationEntity> applicationList = applicationBean.getApplicationList();
        logger.debug("Return value is: applicationList.size=" + (ShamenEJBUtil.isEmpty(applicationList) ? "0" : applicationList.size()));
        logger.debug("Exiting getApplicationList");
        return applicationList;
    }// end getApplicationList

    /**
     * {@inheritDoc}
     */
    public void update(ApplicationEntity entity) throws Exception {
        logger.debug("Entering update");
        logger.debug("This method is used to update an instance of an object with the database.");
        logger.debug("Entry parameters are: entity=" + String.valueOf(entity));
        applicationBean.update(entity);
        logger.debug("Exiting update");
    }// end merge

    /**
     * {@inheritDoc}
     */
    @javax.ejb.Asynchronous
    public synchronized void retrieveAsyncStatusFromApplication(ApplicationEntity entity, ApplicationInstanceEntity instanceEntity) {
        logger.debug("Entering: retrieveAsyncStatusFromApplication");
        logger.debug("Parameter is: " + String.valueOf(instanceEntity));
        logger.debug("This method is run asynchronously and requests a status update from an application.");
        logger.debug("Reset communication with application instance: " + (instanceEntity != null ? String.valueOf(instanceEntity.getFormattedAppKey()) : "null"));
        JmsApplication jmsApp = null;
        Boolean goodShake = instanceEntity.isHandshakeEstablished();
        // if no handshake, try to establish one.
        if(!goodShake){
            logger.debug("No Handshake for application: " + instanceEntity.getFormattedAppKey() + ". Attempting to establish Handshake with application.");
            // generate encryption values for this application
            ShamenCipher outSc = new ShamenCipher(ShamenCipher.generatePassword());
            goodShake = establishHandshake(entity, instanceEntity, outSc, true);
            jmsApp = JmsObjectConvertor.toJms(entity, instanceEntity);
            jmsApp.setSc(outSc);
        }else{
            jmsApp = JmsObjectConvertor.toJms(entity, instanceEntity);
        }// end if
         // if shake is good and status not already confirmed, then get the status, otherwise set the app stuff to nulls.
        try{
            if(goodShake){
                logger.debug("Handshake for application: " + instanceEntity.getFormattedAppKey() + " is good. Updating status of application.");
                JmsApplication workApp = sendStatusUpdateToApplication(entity, instanceEntity, entity.getStatusComment(), true);
                // Attempt to send the status update to the client application
                if(workApp != null && workApp.getConfirmed()){
                    instanceEntity.setVerifiedAddtnlInfo(workApp.getVerifiedAdditionalInfo());
                    instanceEntity.setVerifiedBuildNm(workApp.getVerifiedBranch());
                    instanceEntity.setVerifiedEarNm(workApp.getVerifiedEar());
                    instanceEntity.setVerifiedVersionNm(workApp.getVerifiedVersion());
                    setSingleApplicationInstanceStatus(instanceEntity, entity.getApplicationStatus().getApplicationStatusCd(), jmsApp.getSc());
                }else{
                    logger.debug("Did not receive confirmation from application: " + instanceEntity.getFormattedAppKey());
                    setSingleApplicationInstanceStatus(instanceEntity, ApplicationStatusUpdaterBeanLocal.STATUS_UNRESPONSIVE, null);
                }// end if-else
            }else{
                logger.debug("Did not receive confirmation from application: " + instanceEntity.getFormattedAppKey());
                setSingleApplicationInstanceStatus(instanceEntity, ApplicationStatusUpdaterBeanLocal.STATUS_UNRESPONSIVE, null);
            }// end if-else
        }catch(Exception e){
            logger.error("Error occurred sending or setting the status to application: " + instanceEntity.getFormattedAppKey() + ". The error is: " + e);
        }// end try-catch
        logger.debug("Exiting: retrieveAsyncStatusFromApplication");
    }// end retrieveAsyncStatusFromApplication

    /**
     * {@inheritDoc}
     */
    public void verifyAppStatus(ApplicationEntity application, ApplicationInstanceEntity instance) {
        logger.debug("Entering: checkAppStatus");
        logger.debug(" This method requests a status update from an application.  It returns true for connected apps and false for unresponsive apps.");
        logger.debug("Verify the status for application: " + String.valueOf(application));
        HashMap<String, String> outgoingMsg = new HashMap<String, String>();
        JmsApplication app1 = JmsObjectConvertor.toJms(application, instance);
        Boolean connected = false;
        outgoingMsg.put(ShamenApplicationStatus.STATUS, "DUMMY");
        outgoingMsg.put(ShamenApplicationStatus.STATUS_REASON, "DUMMY");
        outgoingMsg.put(ShamenApplicationStatus.STATUS_TIME_SECONDS, String.valueOf(System.currentTimeMillis()));
        outgoingMsg.put(ShamenApplicationStatus.ACTION, ShamenApplicationStatus.ACTION_REPORT_STATUS);
        // encrypt the message
        outgoingMsg = app1.getSc().encryptApplicationMap(outgoingMsg);

        // Load the selectors so that the QB gets the message
        HashMap<String, String> selectors = loadSelectors(application, instance);
        JmsApplication workApp = null;
        if(!selectors.isEmpty()){
            workApp = publishEncryptedMessageToApplication(outgoingMsg, application, instance, selectors);
        }// end if

        try{
            if(workApp != null && workApp.getConfirmed()){
                logger.debug("Connection and status verified for: " + instance.getFormattedAppKey());
                connected = true;
                app1.setVerifiedAdditionalInfo(workApp.getVerifiedAdditionalInfo());
                app1.setVerifiedBranch(workApp.getVerifiedBranch());
                app1.setVerifiedEar(workApp.getVerifiedEar());
                app1.setVerifiedVersion(workApp.getVerifiedVersion());
                setSingleApplicationInstanceStatus(instance, app1.getStatus());
            }else{
                setSingleApplicationInstanceStatus(instance, ApplicationStatusUpdaterBeanLocal.STATUS_UNRESPONSIVE, null);
            }// end if
        }catch(Exception e){
            logger.error("Error occurred setting connection details for application: " + String.valueOf(application));
        }// end if
        logger.debug("Application " + app1.getAppKey() + " status was " + (connected ? "" : "not") + " confirmed");
        logger.debug("Exiting: checkAppStatus");
        // }
    }// end checkAppStatus

    /**
     * {@inheritDoc}
     */
    public synchronized JmsApplication sendStatusUpdateToApplication(ApplicationEntity app, ApplicationInstanceEntity instance, String statusComment, Boolean retry) {
        logger.debug("Entering: sendStatusUpdateToApplication with parameters, APP: " + String.valueOf(app) + " , STATUS COMMENT: " + String.valueOf(statusComment));
        Boolean confirmed = false;
        logger.debug("Send status update to application: " + instance.getFormattedAppKey());
        logger.debug("Creating JMS session.");
        logger.debug("Creating JMS producer.");
        HashMap<String, String> messageMap = new HashMap<String, String>();
        messageMap.put(ShamenApplicationStatus.ACTION, ShamenApplicationStatus.ACTION_CHANGE_YOUR_STATUS);
        messageMap.put(ShamenApplicationStatus.STATUS, app.getApplicationStatus().getApplicationStatusCd());
        messageMap.put(ShamenApplicationStatus.STATUS_REASON, statusComment);
        messageMap.put("STATUS_TIME_SECONDS", String.valueOf(new java.util.Date().getTime()));
        messageMap.put("REPLY_REQUIRED", "false");
        logger.debug("Encrypt the message.");
        JmsApplication jmsApp = JmsObjectConvertor.toJms(app, instance);
        messageMap = jmsApp.getSc().encryptApplicationMap(messageMap);
        // Load the selectors so that the QB gets the message
        HashMap<String, String> selectors = loadSelectors(app, instance);
        JmsApplication workApp = publishEncryptedMessageToApplication(messageMap, app, instance, selectors);

        if(workApp == null || !workApp.getConfirmed()){
            if(retry){
                logger.debug("Did not receive status confirmation from application: " + String.valueOf(app.getApplicationName()) + " - " + String.valueOf(app.getApplicationAddress()) + ". Will retry for confirmation in 2 seconds.");
                // Wait a couple of seconds before retry in case client application is in its reconnect stage.
                try{
                    TimeUnit.SECONDS.sleep(2);
                }catch(InterruptedException e){
                    logger.error("Exception occurred sleeping prior to re-attempting status verification from: " + String.valueOf(app.getApplicationName()) + " - " + String.valueOf(app.getApplicationAddress()));
                }// end try-catch
                workApp = sendStatusUpdateToApplication(app, instance, statusComment, false);
            }else{
                logger.debug("Did not receive status confirmation from application: " + String.valueOf(app.getApplicationName()) + " - " + String.valueOf(app.getApplicationAddress()));
                workApp = null;
            }// end if
        }else{
            logger.debug("Status update confirmation received from application: " + String.valueOf(app.getApplicationName()) + " - " + String.valueOf(app.getApplicationAddress()));
            workApp.setVerifiedAdditionalInfo(workApp.getVerifiedAdditionalInfo());
            workApp.setVerifiedBranch(workApp.getVerifiedBranch());
            workApp.setVerifiedEar(workApp.getVerifiedEar());
            workApp.setVerifiedVersion(workApp.getVerifiedVersion());
            workApp.setConfirmed(true);
        } // end if-else
        logger.debug("Send status update to application " + String.valueOf(app) + " with confirmed = " + String.valueOf(confirmed));
        logger.debug("Exiting: sendStatusUpdateToApplication");
        return workApp;
    }// end sendStatusUpdateToApplication

    /**
     * This method establishes the handshake with a particular application via a jms request message.
     *
     * @param applicationEntity
     *        applicationEntity
     * @param instanceEntity
     *        instanceEntity
     * @param outSc
     *        outSc
     * @param retry
     *        Should it retry if handshake was not established, true/false
     * @return handShakeStatusIsGood
     */
    private synchronized Boolean establishHandshake(ApplicationEntity applicationEntity, ApplicationInstanceEntity instanceEntity, ShamenCipher outSc, Boolean retry) {
        logger.debug("Entering: establishHandshake.");
        logger.debug("Parameter are: applicationEntity: " + applicationEntity + ", instanceEntity: " + String.valueOf(instanceEntity) + ", outSc: " + String.valueOf(outSc) + ", retry: " + String.valueOf(retry));
        logger.debug("This method establishes the handshake with a particular application via a jms request message.");
        logger.info("Establishing the handshake with: " + String.valueOf(instanceEntity.getFormattedAppKey()));
        Boolean handShakeEstablished = false;

        HashMap<String, String> outgoingMsg = new HashMap<String, String>();

        outgoingMsg.put(ShamenApplicationStatus.STATUS, "DUMMY");
        outgoingMsg.put(ShamenApplicationStatus.STATUS_REASON, "DUMMY");
        outgoingMsg.put(ShamenApplicationStatus.STATUS_TIME_SECONDS, String.valueOf(System.currentTimeMillis()));
        outgoingMsg.put(ShamenApplicationStatus.PASSWORD, outSc.getPassword());
        instanceEntity.setEncryptionKey(outSc.getPassword().getBytes());
        // use default encryption on the initial request
        try{
            outgoingMsg = new ShamenCipher().encryptApplicationMap(outgoingMsg);
            JmsApplication app = new JmsApplication();
            app.setApplicationName(applicationEntity.getApplicationName());
            app.setApplicationAddress(applicationEntity.getApplicationAddress());
            // this is the only value that is not encrypted in the initial request.
            outgoingMsg.put(ShamenApplicationStatus.ACTION, ShamenApplicationStatus.ACTION_ESTABLISH_HANDSHAKE);

            // Load the selectors so that the QB gets the message
            HashMap<String, String> selectors = loadSelectors(applicationEntity, instanceEntity);
            JmsApplication workApp = publishEncryptedMessageToApplication(outgoingMsg, applicationEntity, instanceEntity, selectors);

            if(workApp != null && workApp.getConfirmed()){
                app = workApp;
                logger.info("Successful handshake established with application: " + app.getFormattedAppKey());
                handShakeEstablished = true;
            }else{
                if(retry){
                    logger.info("UNSuccessful handshake attempted with application: " + app.getFormattedAppKey() + ". Will attempt 1 more time.");
                    // Wait a couple of seconds before retry in case client application is in its reconnect stage.
                    TimeUnit.SECONDS.sleep(2);
                    handShakeEstablished = establishHandshake(applicationEntity, instanceEntity, outSc, false);
                }else{
                    logger.info("UNSuccessful handshake attempted with application: " + app.getFormattedAppKey() + ". Connection for this app will be set to unresponsive.");
                }// end if
            }// end if
        }catch(Exception e){
            logger.error("Exception occurred during while trying to establish handshake with application: " + applicationEntity.getApplicationName() + " - " + applicationEntity.getApplicationAddress() + ". Exception is: " + e);
        }// end try-catch
        logger.debug("Exiting: establishHandshake");
        return handShakeEstablished;
    }// end establishHandshake

    /**
     * This method gets the QB for the application and loads the selectors map to insure that any message sent only goes to that instance.
     *
     * @param applicationEntity
     *        ApplicationEntity
     * @param instanceEntity
     *        ApplicationInstanceEntity
     * @return selectors
     */
    private HashMap<String, String> loadSelectors(ApplicationEntity applicationEntity, ApplicationInstanceEntity instanceEntity) {
        logger.debug("Entering: loadSelectors");
        logger.debug("Parameter are: " + String.valueOf(applicationEntity) + String.valueOf(instanceEntity));
        logger.debug("This method loads the selectors map to insure that any message sent only goes to the correct instance.");
        logger.debug("Load the selectors for application: " + String.valueOf(instanceEntity.getFormattedAppKey()));
        HashMap<String, String> selectors = null;
        // instance = applicationBean.getApplicationInstancePrimary(applicationEntity);
        selectors = new HashMap<String, String>();
        if(instanceEntity != null){
            // Load selectors for this message
            selectors.put(ShamenApplicationStatus.QUEUE_APPLICATION_SELECTOR_1, applicationEntity.getApplicationName());
            selectors.put(ShamenApplicationStatus.QUEUE_APPLICATION_SELECTOR_2, applicationEntity.getApplicationAddress());
            selectors.put(ShamenApplicationStatus.QUEUE_APPLICATION_SELECTOR_3, instanceEntity.getApplicationInstanceName());
        }// end if
        logger.debug("loadSelectors with: " + String.valueOf(selectors));
        logger.debug("Exiting: loadSelectors");
        return selectors;
    }// end loadSelectors

    /**
     * {@inheritDoc}
     */
    @javax.ejb.Asynchronous
    public void resetAllApplicationStatusesAsync() {
        logger.debug("Entering: resetAllApplicationStatusesAsync");
        logger.debug("This method refreshes connections with all applications");
        logger.info("Reset all the application statuses and handshakes.");
        List<ApplicationEntity> applicationList = null;
        try{
            applicationBean.deleteOldApplicationInstancesAll();

            requestRegistrationAll();
            logger.debug("Rest for 12 seconds to give the apps time to register their instances and reconnect, then set any unresponsive apps to UNR.");
            TimeUnit.SECONDS.sleep(12);
            // Must get a fresh application list
            applicationList = applicationBean.getApplicationList();
            // Run through all the apps and set their verified status.
            for(int i = 0, j = applicationList.size();i < j;i++){
                if(applicationList.get(i) != null){
                    List<ApplicationInstanceEntity> instances = applicationList.get(i).getApplicationInstances();
                    for(int l = 0, m = instances.size();l < m;l++){
                        instances.get(l).setEncryptionKey(null);
                        retrieveAsyncStatusFromApplication(applicationList.get(i), instances.get(l));
                    }// end for
                }// end if
            }// end for

        }catch(InterruptedException e){
            logger.error("Error occurred during the reset application status routine while trying to sleep. Error is : " + e);
        }catch(Exception e){
            logger.error("Exception encountered getting list of applications for the status update", e);
        }// end try-catch
        logger.debug("Exiting: resetAllApplicationStatusesAsync");
    }// end resetAllApplicationStatusesAsync

    /**
     * {@inheritDoc}
     */
    @javax.ejb.Asynchronous
    public void resetApplicationStatusAsync(ApplicationEntity application) {
        logger.debug("Entering: resetApplicationStatusAsync");
        logger.debug("This method refreshes the connection with an application.");
        logger.info("Refresh connection with application: " + application.getApplicationName() + "-" + application.getApplicationAddress());

        JmsApplication app = null;
        if(application != null){
            // delete any old instances for this app
            try{
                applicationBean.deleteOldApplicationInstances(application);
            }catch(Exception e){
                logger.error("Error occurred during the reset application status routine.  Was on application: " + String.valueOf(app) + "Error is : " + e);
            }// end try-catch
        }// end for
        requestRegistrationApplication(application);
        try{
            // Rest for 3 seconds to give the app time to register their instances
            TimeUnit.SECONDS.sleep(3);
            ApplicationEntity applicationFresh = applicationBean.findApplicationByRefId(application.getApplicationRefId());
            if(applicationFresh != null && applicationFresh.getApplicationInstances() != null && !applicationFresh.getApplicationInstances().isEmpty()){
                for(int i = 0, j = applicationFresh.getApplicationInstances().size();i < j;i++){
                    // set its verified status.
                    retrieveAsyncStatusFromApplication(application, applicationFresh.getApplicationInstances().get(i));
                }// end for
            }// end if
        }catch(InterruptedException e){
            logger.error("Error occurred during the reset application status routine while trying to sleep. Error is : " + e);
        }catch(Exception e){
            logger.error("Exception encountered getting list of applications for the status update", e);
        }// end try-catch
        logger.debug("Exiting: resetApplicationStatusAsync");
    }// end resetApplicationStatusAsync

    /**
     * {@inheritDoc}
     */
    @javax.ejb.Asynchronous
    public void resetApplicationStatusAsync(Long refId, String status, String comment) {
        logger.debug("Entering: resetApplicationStatusAsync");
        logger.debug("This method refreshes the connection with an application.");
        // logger.info("Refresh connection with application: " + application.getApplicationName() + "-" + application.getApplicationAddress());
        ApplicationEntity application = em.find(ApplicationEntity.class, refId);
        application.setApplicationStatus(new ApplicationStatusCodeEntity(status, "Suspended"));
        application.setStatusComment(comment);
        try{
            update(application);
        }catch(Exception e1){
            logger.error("Error occurred during the update of application.  Was on application: " + String.valueOf(application) + "Error is : " + e1);
            throw new IllegalStateException("Error occurred during the update of application.  Error is : " + e1.getMessage());
        }
        JmsApplication app = null;
        if(application != null){
            // delete any old instances for this app
            try{
                applicationBean.deleteOldApplicationInstances(application);
            }catch(Exception e){
                logger.error("Error occurred during the reset application status routine.  Was on application: " + String.valueOf(app) + "Error is : " + e);
                throw new IllegalStateException("Error occurred during the reset application status routine.  Error is : " + e.getMessage());
            }// end try-catch
        }// end for
        requestRegistrationApplication(application);
        try{
            // Rest for 3 seconds to give the app time to register their instances
            TimeUnit.SECONDS.sleep(3);
            ApplicationEntity applicationFresh = applicationBean.findApplicationByRefId(application.getApplicationRefId());
            if(applicationFresh != null && applicationFresh.getApplicationInstances() != null && !applicationFresh.getApplicationInstances().isEmpty()){
                for(int i = 0, j = applicationFresh.getApplicationInstances().size();i < j;i++){
                    // set its verified status.
                    retrieveAsyncStatusFromApplication(application, applicationFresh.getApplicationInstances().get(i));
                }// end for
            }// end if
        }catch(InterruptedException e){
            logger.error("InterruptedException occurred during the reset application status routine while trying to sleep. Error is : " + e);
            throw new IllegalStateException("InterruptedException occurred during the reset application status routine while trying to sleep. Error is : " + e.getMessage());
        }catch(Exception e){
            logger.error("Exception occurred during the reset application status routine while trying to sleep. Error is : ", e);
            throw new IllegalStateException("Exception occurred during the reset application status routine while trying to sleep. Error is : " + e.getMessage());
        }// end try-catch
        logger.debug("Exiting: resetApplicationStatusAsync");
    }// end resetApplicationStatusAsync

    /**
     * {@inheritDoc}
     */
    public void setSingleApplicationInstanceStatus(ApplicationInstanceEntity instance, String status, ShamenCipher sc) throws Exception {
        logger.debug("Entering setSingleApplicationInstanceStatus.");
        logger.debug("Parameters are: applicationName: " + String.valueOf(instance) + ", status: " + String.valueOf(status) + ", ShamenCipher: " + String.valueOf(sc));
        logger.debug("This method sets the the status for a single application and loads it into the DB.");
        logger.debug("Set the status for application: " + instance.getFormattedAppKey() + ". Status is: " + status);
        try{
            if(sc != null && sc.getPassword() != null){
                instance.setEncryptionKey(sc.getPassword().getBytes());
            }else{
                instance.setEncryptionKey(null);
            }// end if-else
            setSingleApplicationInstanceStatus(instance, status);
        }catch(Exception e){
            logger.debug("Exception occurred getting application entity: " + String.valueOf(instance), e);
            throw (e);
        }// end try-catch
        logger.debug("Exiting setSingleApplicationInstanceStatus");
    }// end setSingleApplicationInstanceStatus

    /**
     * {@inheritDoc}
     */
    public void setSingleApplicationInstanceStatus(ApplicationInstanceEntity instance, String status) throws Exception {
        logger.debug("Entering setSingleApplicationInstanceStatus.");
        logger.debug("Parameters are: ApplicationInstanceEntity: " + String.valueOf(instance) + ", status: " + String.valueOf(status));
        logger.debug("This method sets the the status for a single application and loads it into the DB.");
        logger.debug("Set the status for application: " + instance.getFormattedAppKey() + ". Status is: " + status);
        try{
            instance.setVerifiedApplicationStatusCd(status);
            applicationBean.update(instance);
        }catch(Exception e){
            logger.debug("Exception occurred getting application entity: " + String.valueOf(instance), e);
            throw (e);
        }// end try-catch
        logger.debug("Exiting setSingleApplicationInstanceStatus");
    }// end setSingleApplicationInstanceStatus

    /**
     * {@inheritDoc}
     */
    public void setSingleApplicationInstanceStatusAsync(ApplicationInstanceEntity instance, String status, ShamenCipher sc) throws Exception {
        logger.debug("Entering setSingleApplicationInstanceStatusAsync.");
        logger.debug("Parameters are: applicationName: " + String.valueOf(instance) + ", status: " + String.valueOf(status) + ", ShamenCipher: " + String.valueOf(sc));
        logger.debug("This method sets the the status for a single application and loads it into the DB.");
        logger.info("Set the status for application: " + instance.getFormattedAppKey() + ". Status is: " + status);
        try{
            instance.setEncryptionKey(sc.getPassword().getBytes());
            setSingleApplicationInstanceStatus(instance, status);
        }catch(Exception e){
            logger.debug("Exception occurred getting application entity: " + String.valueOf(instance), e);
            throw (e);
        }// end try-catch
        logger.debug("Exiting setSingleApplicationInstanceStatusAsync");
    }// end setSingleApplicationInstanceStatusAsync

    /**
     * {@inheritDoc}
     */
    public void setAllApplicationInstanceStatus(String status) {
        logger.debug("Entering setAllApplicationStatus.");
        logger.debug("Parameters are: status: " + String.valueOf(status));
        logger.debug("This method sets the the status for a all applications and loads it into the DB.");
        logger.info("Set all application instance statuses to: " + String.valueOf(status));
        List<ApplicationEntity> applicationList = null;
        try{
            applicationList = applicationBean.getApplicationList();
        }catch(Exception e){
            logger.error("Exception encountered getting list of applications for the status update", e);
        }// end try-catch
        JmsApplication app = null;
        if(applicationList != null){
            // loop through and set them all to awaiting response. This will insure that the page will show the proper data during refresh
            for(int i = 0, j = applicationList.size();i < j;i++){
                if(applicationList.get(i).getApplicationInstances() != null){
                    for(int k = 0, l = applicationList.get(i).getApplicationInstances().size();k < l;k++){
                        ApplicationInstanceEntity instance = applicationList.get(i).getApplicationInstances().get(k);
                        instance.setEncryptionKey(null);
                        try{
                            setSingleApplicationInstanceStatus(instance, status, null);
                        }catch(Exception e){
                            logger.error("Exception encountered while trying to set the application's verified status to AWR.  Application: " + String.valueOf(app), e);
                        }// end try-catch
                    }// end for
                }// end if
            }// end for
        }// end if
        logger.debug("Exiting setAllApplicationStatus");
    }// end setAllApplicationStatus

    /**
     * {@inheritDoc}
     */
    public void setAllInstanceStatusesForApplication(ApplicationEntity application, String status) {
        logger.debug("Entering setAllApplicationStatus.");
        logger.debug("Parameters are: status: " + String.valueOf(status));
        logger.debug("This method sets the the status for a all applications and loads it into the DB.");
        logger.info("Set all the instances of application: " + (application != null ? application.getApplicationName() + "-" + application.getApplicationAddress() : "null") + " to: " + String.valueOf(status));
        // loop through and set them all to awaiting response. This will insure that the page will show the proper data during refresh
        if(application.getApplicationInstances() != null){
            for(int k = 0, l = application.getApplicationInstances().size();k < l;k++){
                ApplicationInstanceEntity instance = application.getApplicationInstances().get(k);
                instance.setEncryptionKey(null);
                try{
                    setSingleApplicationInstanceStatus(instance, status, null);
                }catch(Exception e){
                    logger.error("Exception encountered while trying to set the application's verified status to AWR.  Application: " + String.valueOf(application), e);
                }// end try-catch
            }// end for
        }// end if
        logger.debug("Exiting setAllApplicationStatus");
    }// end setAllApplicationStatus

    /**
     * This method creates and then starts the connection
     *
     * @return Connection
     * @throws JMSException
     *         if an exception occurred
     */
    private Connection createAndStartConnection() throws JMSException {
        logger.debug("Entering: createAndStartConnection()");
        logger.debug("Creating connection through ConnectionFactory.");
        logger.debug("Create a new JMS connection and start it.");
        Connection connection = factory.createConnection();
        logger.debug("Starting connection.");
        connection.start();
        logger.debug("Exiting: createAndStartConnection()");
        return connection;
    }// end createAndStartConnection

    /**
     * This method will publish a message with the proper selector so that the correct controller will receive it. It does not encrypt.
     *
     * @param messageMap
     *        map containing the message
     * @param app
     *        app
     * @param instance
     *        instance
     * @param selectors
     *        All the message selectors
     * @return success
     */
    private synchronized JmsApplication publishEncryptedMessageToApplication(HashMap<String, String> messageMap, ApplicationEntity app, ApplicationInstanceEntity instance, HashMap<String, String> selectors) {
        logger.debug("Entering: publishEncryptedMessageToApplication.");
        logger.debug("This method will publish a message with the proper selector so that the correct controller will receive it.  It does not encrypt.");
        logger.info("Send a message to application: " + selectors);
        Connection connection = null;
        MessageConsumer consumer = null;
        Message inMessage = null;
        Boolean confirmed = false;
        HashMap<String, String> returnMessageMap;
        JmsApplication jmsApp = null;
        // Publish the message to the Topic
        try{
            connection = createAndStartConnection();
            logger.debug("Creating JMS session.");
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            logger.debug("Creating JMS producer.");
            MessageProducer producer = session.createProducer(destination);
            producer.setTimeToLive(6000);
            logger.debug("Create the temporary queue to be used in the response.");
            TemporaryQueue tempQ = session.createTemporaryQueue();
            String correlationId = String.valueOf(System.currentTimeMillis());
            consumer = session.createConsumer(tempQ);
            logger.debug("Creating Object Message");
            ObjectMessage message = session.createObjectMessage(messageMap);
            Set<String> keys = selectors.keySet();
            for(Iterator iterator = keys.iterator();iterator.hasNext();){
                String key = (String) iterator.next();
                message.setStringProperty(key, selectors.get(key));
            }// end for

            message.setJMSReplyTo(tempQ);
            message.setJMSCorrelationID(correlationId);
            logger.debug("Sending message: " + String.valueOf(messageMap));
            producer.send(message);
            logger.debug("Waiting for response on tempQ: " + String.valueOf(tempQ.getQueueName()) + " from " + selectors);
            inMessage = consumer.receive(5000);
            jmsApp = JmsObjectConvertor.toJms(app, instance);
            if(inMessage instanceof ObjectMessage){
                Object obj = ((ObjectMessage) inMessage).getObject();
                if(obj instanceof HashMap){
                    logger.debug("***********Received a message**************** On tempQ from: " + selectors);
                    returnMessageMap = (HashMap) obj;
                    // if the cipher is null, it's the initial handshake so don't try and get extra properties
                    if(jmsApp.getSc() != null){
                        returnMessageMap = jmsApp.getSc().decryptApplicationMap(returnMessageMap);
                        // returnMessageMap.putAll(messageMap);
                        jmsApp = JmsObjectConvertor.toJms(returnMessageMap, jmsApp.getSc());
                    }// end if
                    jmsApp.setConfirmed(true);
                    confirmed = true;
                }// end if
            }else{
                logger.debug("Did not receive confirmation from application: " + String.valueOf(app.getApplicationName()) + " - " + String.valueOf(app.getApplicationAddress()));
                jmsApp = null;
            }// end if-else
        }catch(JMSException e){
            logger.error("JMSException occurred while publishing encrypted message to an application.  Exception is: " + e.getMessage());
        }catch(Exception e){
            logger.error("Exception occurred while publishing encrypted message to an application.  Exception is: " + e.getMessage());
        }finally{
            closeConnection(connection);
        }// end try/catch
        logger.debug("Publish Encrypted Message To Application. Message was " + (confirmed ? "" : "NOT ") + "confirmed.");
        logger.debug("Exiting: publishEncryptedMessageToApplication");
        return jmsApp;
    }// end publishEncryptedMessageToApplication

    /**
     * {@inheritDoc}
     */
    public void requestRegistrationAll() {
        logger.debug("Entering: requestRegistrationAll");
        logger.debug("This method will send a message to all instances of all applications and force them to register themselves.");
        logger.info("Request registrations from all application instances.");
        HashMap<String, String> selectors = new HashMap<String, String>();
        selectors.put(ShamenApplicationStatus.QUEUE_APPLICATION_SELECTOR_1, "ALL");
        selectors.put(ShamenApplicationStatus.QUEUE_APPLICATION_SELECTOR_2, "ALL");
        selectors.put(ShamenApplicationStatus.QUEUE_APPLICATION_SELECTOR_3, "ALL");
        requestRegistration(selectors);
        logger.debug("Exiting: requestRegistrationAll");
    }// end requestRegistrationAll

    /**
     * {@inheritDoc}
     */
    public void requestRegistrationApplication(ApplicationEntity application) {
        logger.debug("Entering: requestRegistrationApplication");
        logger.debug("This method will send a message to all instances of an application and force them to register themselves.");
        logger.info("Request registration from all instances of application: " + (application != null ? application.getApplicationName() + "-" + application.getApplicationAddress() : "null"));
        if(application != null){
            logger.info("Request registrations from all instances of application: " + application.getApplicationName() + "-" + application.getApplicationAddress());
            HashMap<String, String> selectors = new HashMap<String, String>();
            selectors.put(ShamenApplicationStatus.QUEUE_APPLICATION_SELECTOR_1, application.getApplicationName());
            selectors.put(ShamenApplicationStatus.QUEUE_APPLICATION_SELECTOR_2, application.getApplicationAddress());
            selectors.put(ShamenApplicationStatus.QUEUE_APPLICATION_SELECTOR_3, "ALL");
            requestRegistration(selectors);
        }// end if
        logger.debug("Exiting: requestRegistrationApplication");
    }// end requestRegistrationApplication

    /**
     * This method will send a message to all instances of an application and force them to register themselves.
     *
     * @param selectors
     *        selectors
     */
    private void requestRegistration(HashMap<String, String> selectors) {
        logger.debug("Entering: requestRegistration");
        logger.debug("This method will send a message to all instances of an application and force them to register themselves.");
        logger.info("Request registrations from applications with subset: " + selectors);
        // Create JMS resources
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        try{
            connection = factory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            logger.debug("Create the JMS Producer.");
            producer = session.createProducer(destination);
            producer.setTimeToLive(20000);
            logger.debug("Create the message");
            HashMap<String, String> messageMap = new HashMap<String, String>();
            messageMap.put(ShamenApplicationStatus.ACTION, ShamenApplicationStatus.ACTION_REGISTER);
            messageMap.put(ShamenApplicationStatus.STATUS_TIME_SECONDS, String.valueOf(new java.util.Date().getTime()));
            messageMap.put(ShamenApplicationStatus.REPLY_REQUIRED, "true");

            ObjectMessage message = session.createObjectMessage(messageMap);

            Set<String> keys = selectors.keySet();
            for(Iterator iterator = keys.iterator();iterator.hasNext();){
                String key = (String) iterator.next();
                message.setStringProperty(key, selectors.get(key));
            }// end for
            logger.debug("Sending message: " + String.valueOf(messageMap));
            producer.send(message);
        }catch(JMSException e){
            logger.error("Error occurred sending message to request registration for subset: " + String.valueOf(selectors) + ". Error is: " + e);
        }finally{
            closeConnection(connection);
        }// end try-catch
        logger.debug("Exiting: requestRegistration");
    }// end requestRegistration

    /**
     * This method closes connections
     *
     * @param connection
     *        the connection to close
     */
    private void closeConnection(Connection connection) {
        logger.debug("Entering: closeConnection()");
        logger.debug("Close jms connection.");
        try{
            if(connection != null){
                logger.debug("Closing connection.");
                connection.close();
            }// end if
        }catch(JMSException e){
            logger.error("Exception occurred while trying to close connection.  Error is: " + e);
        }finally{
            connection = null;
        }// end try/catch
        logger.debug("Exiting: closeConnection()");
    }// end closeConnection

}// end class
