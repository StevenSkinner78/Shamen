package gov.doc.isu.shamen.jms.convertor;

import static gov.doc.isu.shamen.ejb.util.EJBConstants.EMPTY_STRING;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import gov.doc.isu.shamen.core.ShamenApplicationStatus;
import gov.doc.isu.shamen.ejb.util.EJBConstants;
import gov.doc.isu.shamen.ejb.util.ShamenEJBUtil;
import gov.doc.isu.shamen.jms.ShamenCipher;
import gov.doc.isu.shamen.jms.models.JmsApplication;
import gov.doc.isu.shamen.jms.models.JmsBatchApp;
import gov.doc.isu.shamen.jms.models.JmsBatchAppCollection;
import gov.doc.isu.shamen.jms.models.JmsController;
import gov.doc.isu.shamen.jms.models.JmsRunStatus;
import gov.doc.isu.shamen.jms.models.JmsRunStatusSummary;
import gov.doc.isu.shamen.jms.models.JmsSchedule;
import gov.doc.isu.shamen.jpa.comparator.RunStatusEntityComparator;
import gov.doc.isu.shamen.jpa.entity.ApplicationEntity;
import gov.doc.isu.shamen.jpa.entity.ApplicationInstanceEntity;
import gov.doc.isu.shamen.jpa.entity.ApplicationStatusCodeEntity;
import gov.doc.isu.shamen.jpa.entity.BatchAppCollectionEntity;
import gov.doc.isu.shamen.jpa.entity.BatchAppEntity;
import gov.doc.isu.shamen.jpa.entity.CommonEntity;
import gov.doc.isu.shamen.jpa.entity.ControllerEntity;
import gov.doc.isu.shamen.jpa.entity.ResultCodeEntity;
import gov.doc.isu.shamen.jpa.entity.RunStatusEntity;
import gov.doc.isu.shamen.jpa.entity.ScheduleEntity;
import gov.doc.isu.shamen.jpa.entity.StatusCodeEntity;

/**
 * This class is used to convert Shamen models to Jms models and vice versa.
 *
 * @author <strong>Shane Duncan</strong> JCCC, Oct 5, 2015
 * @author <strong>Zachary Lisle</strong> JCCC, Sept 1, 2021
 */
public class JmsObjectConvertor {
    private static Logger log = Logger.getLogger("gov.doc.isu.shamen.jms.convertor.JmsObjectConvertor");
    private static final String SDF = "MM/dd/yyyy";

    /**
     * This method converts a <code>ControllerEntity</code> to <code>JmsController</code>.
     *
     * @author <strong>Shane Duncan</strong> JCCC, Nov 3, 2015
     * @param controllerEntity
     *        ControllerEntity to convert
     * @return JmsController
     */
    public static JmsController toJms(ControllerEntity controllerEntity) {
        log.debug("Entering JmsObjectConvertor.toJms.");
        log.debug("This method converts a ControllerEntity to JmsController. Entered with: " + String.valueOf(controllerEntity));
        if(controllerEntity == null){
            return null;
        }// end if
        JmsController j = new JmsController();
        j.setControllerAddress(controllerEntity.getControllerAddress() != null ? controllerEntity.getControllerAddress().toUpperCase() : "");
        j.setControllerName(controllerEntity.getControllerName());
        j.setControllerRefId(controllerEntity.getControllerRefId());
        j.setCreateTs(controllerEntity.getCommon().getCreateTime());
        j.setCreateUserRefId(controllerEntity.getCommon().getCreateUserRefId() == null ? 0L : controllerEntity.getCommon().getCreateUserRefId());
        j.setDeleteInd(controllerEntity.getCommon().getDeleteIndicator());
        j.setUpdateTs(controllerEntity.getCommon().getUpdateTime());
        j.setUpdateUserRefId(controllerEntity.getCommon().getUpdateUserRefId() == null ? 0L : controllerEntity.getCommon().getUpdateUserRefId());
        // convert the batchapps
        ArrayList<JmsBatchApp> jbaList = new ArrayList<JmsBatchApp>();
        ArrayList<JmsBatchAppCollection> jbaCollectionList = new ArrayList<JmsBatchAppCollection>();
        for(int i = 0, l = controllerEntity.getBatchApps().size();i < l;i++){
            if(controllerEntity.getBatchApps().get(i).getBatchCollection().size() == 0){
                jbaList.add(toJms(controllerEntity.getBatchApps().get(i)));
            }else{
                jbaCollectionList.add(toCollectionJms(controllerEntity.getBatchApps().get(i)));
            }// end if-else
        }// end for
        j.setBatchApps(jbaList);
        j.setJmsBatchAppCollections(jbaCollectionList);
        log.debug("Return JmsController is: " + String.valueOf(j));
        log.debug("Exiting JmsObjectConvertor.toJms.");
        return j;
    }// end toJms

    /**
     * This method converts a <code>BatchAppEntity</code> to <code>JmsBatchApp</code>.
     *
     * @author <strong>Shane Duncan</strong> JCCC, Nov 3, 2015
     * @param batchAppEntity
     *        BatchAppEntity to convert
     * @param runSeq
     *        run sequence of the collectionJob
     * @return JmsBatchApp
     */
    public static JmsBatchApp toJms(BatchAppEntity batchAppEntity, Long runSeq) {
        log.debug("Entering toJms");
        log.debug("This method converts a BatchAppEntity to JmsBatchApp.");
        log.debug("Entry parameters are: batchAppEntity=" + String.valueOf(batchAppEntity) + ", runSeq=" + String.valueOf(runSeq));
        if(batchAppEntity == null){
            log.debug("Return value is: null");
            log.debug("Exiting toJms");
            return null;
        }// end if
        JmsBatchApp j = toJms(batchAppEntity);
        j.setRunSequenceNbr(runSeq);
        log.debug("Return value is: JmsBatchApp=" + String.valueOf(j));
        log.debug("Exiting toJms");
        return j;
    }// end toJms

    /**
     * This method converts a <code>BatchAppEntity</code> to <code>JmsBatchApp</code>.
     *
     * @author <strong>Shane Duncan</strong> JCCC, Nov 3, 2015
     * @param batchAppEntity
     *        BatchAppEntity to convert
     * @return JmsBatchApp
     */
    public static JmsBatchApp toJms(BatchAppEntity batchAppEntity) {
        log.debug("Entering toJms");
        log.debug("This method converts a BatchAppEntity to JmsBatchApp.");
        log.debug("Entry parameters are: batchAppEntity=" + String.valueOf(batchAppEntity));
        JmsBatchApp jb = null;
        if(batchAppEntity != null){
            jb = new JmsBatchApp();
            jb.setBatchAppRefId(batchAppEntity.getBatchAppRefId());
            jb.setName(batchAppEntity.getBatchName());
            jb.setControllerRefId((batchAppEntity != null ? batchAppEntity.getController().getControllerRefId() : null));
            jb.setDescription(batchAppEntity.getDescription());
            jb.setFileLocation(batchAppEntity.getFileLocation());
            jb.setFileNm(batchAppEntity.getFileName());
            jb.setType(batchAppEntity.getBatchType().getBatchTypeCd());
            jb.setTypeDescription(batchAppEntity.getBatchType().getBatchTypeDesc());
            if(!ShamenEJBUtil.isEmpty(batchAppEntity.getSchedule()) && batchAppEntity.getSchedule().size() > 1){
                jb.setLastRunStatusCd("MUL");
            }else if(!ShamenEJBUtil.isEmpty(batchAppEntity.getSchedule()) && batchAppEntity.getSchedule().size() == 1){
                if(null != batchAppEntity.getSchedule().get(0).getLastRunStatus()){
                    jb.setLastRunStatusCd(batchAppEntity.getSchedule().get(0).getLastRunStatus().getResult().getResultCd());
                }else{
                    jb.setLastRunStatusCd("UNK");
                }// end if/else
            }else{
                jb.setLastRunStatusCd("UNK");
            }// end if/else
            jb.setExecutionCount(batchAppEntity.getExecutionCount());
            jb.setSchedule(toJms(batchAppEntity.getSchedule()));
            jb.setControllerStatus(batchAppEntity.getController().getControllerStatusCodeEntity().getControllerStatusCd());
            jb.setCreateTs(batchAppEntity.getCommon() != null ? batchAppEntity.getCommon().getCreateTime() : null);
            jb.setCreateUserRefId(batchAppEntity.getCommon() != null ? batchAppEntity.getCommon().getCreateUserRefId() == null ? 0L : batchAppEntity.getCommon().getCreateUserRefId() : 0L);
            jb.setDeleteInd(batchAppEntity.getCommon() != null ? batchAppEntity.getCommon().getDeleteIndicator() : null);
            jb.setUpdateTs(batchAppEntity.getCommon() != null ? batchAppEntity.getCommon().getUpdateTime() : null);
            jb.setUpdateUserRefId(batchAppEntity.getCommon() != null ? batchAppEntity.getCommon().getUpdateUserRefId() == null ? 0L : batchAppEntity.getCommon().getUpdateUserRefId() : 0L);
            jb.setRunStatusList(new ArrayList<JmsRunStatus>());
        }// end if
        log.debug("Return value is: JmsBatchApp=" + String.valueOf(jb));
        log.debug("Exiting toJms");
        return jb;
    }// end toJms

    /**
     * This method converts a <code>BatchAppEntity</code> to <code>JmsBatchApp</code>.
     *
     * @author <strong>Shane Duncan</strong> JCCC, Nov 3, 2015
     * @param batchAppEntity
     *        BatchAppEntity to convert
     * @return JmsBatchApp
     */
    public static JmsBatchApp toJmsForClientApp(BatchAppEntity batchAppEntity) {
        log.debug("Entering JmsObjectConvertor.toJms.");
        log.debug("This method converts a BatchAppEntity to JmsBatchApp. Entered with: " + batchAppEntity);
        if(batchAppEntity == null){
            return null;
        }// end if
        JmsBatchApp jb = new JmsBatchApp();
        jb.setBatchAppRefId(batchAppEntity.getBatchAppRefId());
        jb.setName(batchAppEntity.getBatchName());
        jb.setControllerRefId((batchAppEntity != null ? batchAppEntity.getController().getControllerRefId() : null));
        jb.setDescription(batchAppEntity.getDescription());
        jb.setFileLocation(batchAppEntity.getFileLocation());
        jb.setFileNm(batchAppEntity.getFileName());
        jb.setType(batchAppEntity.getBatchType().getBatchTypeCd());
        jb.setFromCollection("COL".equals(batchAppEntity.getBatchType().getBatchTypeCd()));
        jb.setTypeDescription(batchAppEntity.getBatchType().getBatchTypeDesc());
        if(!ShamenEJBUtil.isEmpty(batchAppEntity.getSchedule()) && batchAppEntity.getSchedule().size() > 1){
            jb.setLastRunStatusCd("MUL");
        }else if(!ShamenEJBUtil.isEmpty(batchAppEntity.getSchedule()) && batchAppEntity.getSchedule().size() == 1){
            if(null != batchAppEntity.getSchedule().get(0).getLastRunStatus()){
                jb.setLastRunStatusCd(batchAppEntity.getSchedule().get(0).getLastRunStatus().getResult().getResultCd());
            }else{
                jb.setLastRunStatusCd("UNK");
            }// end if/else
        }else{
            jb.setLastRunStatusCd("UNK");
        }// end if/else
        jb.setSchedule(toJms(batchAppEntity.getSchedule()));
        jb.setControllerStatus(batchAppEntity.getController().getControllerStatusCodeEntity().getControllerStatusCd());
        jb.setCreateTs(batchAppEntity.getCommon() != null ? batchAppEntity.getCommon().getCreateTime() : null);
        jb.setCreateUserRefId(batchAppEntity.getCommon() != null ? batchAppEntity.getCommon().getCreateUserRefId() == null ? 0L : batchAppEntity.getCommon().getCreateUserRefId() : 0L);
        jb.setDeleteInd(batchAppEntity.getCommon() != null ? batchAppEntity.getCommon().getDeleteIndicator() : null);
        jb.setUpdateTs(batchAppEntity.getCommon() != null ? batchAppEntity.getCommon().getUpdateTime() : null);
        jb.setUpdateUserRefId(batchAppEntity.getCommon() != null ? batchAppEntity.getCommon().getUpdateUserRefId() == null ? 0L : batchAppEntity.getCommon().getUpdateUserRefId() : 0L);
        jb.setRunStatusList(new ArrayList<JmsRunStatus>());
        // if run statuses are present, then convert them too, but only the most recent 10. This is to keep message size down.
        List<RunStatusEntity> runStatusList = batchAppEntity.getRunStatuses();
        if(runStatusList != null && !runStatusList.isEmpty()){
            Collections.sort(runStatusList, Collections.reverseOrder(new RunStatusEntityComparator()));
            for(int i = 0, j = runStatusList.size();i < j;i++){
                jb.getRunStatusList().add(JmsObjectConvertor.toJms(runStatusList.get(i)));
            }// end for
        }// end if
        log.debug("Exiting JmsObjectConvertor.toJms.");
        log.debug("Exiting JmsObjectConvertor.toJms with " + jb);
        return jb;
    }// end toJms

    /**
     * This method converts a <code>BatchAppCollectionEntity</code> to <code>JmsBatchAppCollection</code>. It also converts all the child <code>BatchAppEntity</code> objects.
     *
     * @author <strong>Shane Duncan</strong> JCCC, Nov 3, 2015
     * @param batchAppCollectionEntity
     *        BatchAppEntity to convert
     * @return JmsBatchApp
     */
    public static JmsBatchAppCollection toCollectionJms(BatchAppEntity batchAppCollectionEntity) {
        log.debug("Entering JmsObjectConvertor.toJms.");
        log.debug("This method converts a BatchAppCollectionEntity to JmsBatchAppCollection. Entered with: " + batchAppCollectionEntity);
        if(batchAppCollectionEntity == null){
            return null;
        }// end if
        JmsBatchAppCollection j = new JmsBatchAppCollection();
        j.setMainBatchApp(batchAppCollectionEntity.getBatchAppRefId());
        j.setName(batchAppCollectionEntity.getBatchName());
        // j.setControllerRefId((batchAppCollectionEntity != null ? batchAppCollectionEntity.getController().getControllerRefId() : null));
        j.setDescription(batchAppCollectionEntity.getDescription());
        // j.setFileLocation(batchAppCollectionEntity.getFileLocation());
        // j.setFileNm(batchAppCollectionEntity.getFileName());
        // j.setType(batchAppCollectionEntity.getBatchType().getBatchTypeCd());
        j.setSchedule(toJms(batchAppCollectionEntity.getSchedule()));
        j.setCreateTs(batchAppCollectionEntity.getCommon().getCreateTime());
        j.setCreateUserRefId(batchAppCollectionEntity.getCommon().getCreateUserRefId() == null ? 0L : batchAppCollectionEntity.getCommon().getCreateUserRefId());
        j.setDeleteInd(batchAppCollectionEntity.getCommon().getDeleteIndicator());
        j.setUpdateTs(batchAppCollectionEntity.getCommon().getUpdateTime());
        j.setUpdateUserRefId(batchAppCollectionEntity.getCommon().getUpdateUserRefId() == null ? 0L : batchAppCollectionEntity.getCommon().getUpdateUserRefId());
        j.setBatchApps(new ArrayList<JmsBatchApp>());
        // convert the child batch apps
        for(int i = 0, h = batchAppCollectionEntity.getBatchCollection().size();i < h;i++){
            BatchAppCollectionEntity col = batchAppCollectionEntity.getBatchCollection().get(i);
            JmsBatchApp jmsApp = toJms(batchAppCollectionEntity.getBatchCollection().get(i).getAssocBatchApp(), col.getRunSeq());
            if(jmsApp != null && !"Y".equals(jmsApp.getDeleteInd())){
                j.getBatchApps().add(jmsApp);
            }// end if
        }// end for
        log.debug("Exiting JmsObjectConvertor.toJms.");
        log.debug("Exiting JmsObjectConvertor.toJms with " + j);
        return j;
    }// end toJms

    /**
     * This method converts a <code>ApplicationEntity</code> to <code>JmsApplication</code>.
     *
     * @author <strong>Shane Duncan</strong> JCCC, Nov 3, 2015
     * @param applicationEntity
     *        ApplicationEntity to convert
     * @param instanceEntity
     *        needed for full conversion
     * @return JmsApplication
     */
    public static JmsApplication toJms(ApplicationEntity applicationEntity, ApplicationInstanceEntity instanceEntity) {
        log.debug("Entering JmsObjectConvertor.toJms.");
        log.debug("This method converts a applicationEntity to JmsApplication. Entered with: " + applicationEntity);
        if(applicationEntity == null){
            return null;
        }// end if
        JmsApplication j = new JmsApplication();
        j.setApplicationAddress(applicationEntity.getApplicationAddress());
        j.setApplicationName(applicationEntity.getApplicationName());
        j.setApplicationRefId(applicationEntity.getApplicationRefId());
        j.setApplicationType(applicationEntity.getApplicationType().getApplicationTypeCd());
        j.setRequestedStatus(applicationEntity.getApplicationStatus() != null ? applicationEntity.getApplicationStatus().getApplicationStatusCd() : null);
        j.setShowAppNotification(applicationEntity.getShowApplicationNotification() != null ? applicationEntity.getShowApplicationNotification() : "N");
        j.setAppNotification(applicationEntity.getApplicationNotificationDesc() != null ? applicationEntity.getApplicationNotificationDesc() : "");
        if(instanceEntity != null){
            j.setApplicationInstanceName(instanceEntity.getApplicationInstanceName());
            try{
                j.setSc(instanceEntity.getEncryptionKey() != null ? new ShamenCipher(new String(instanceEntity.getEncryptionKey())) : new ShamenCipher());
            }catch(Exception e){
                log.error("Exception occurred trying to convert the application entity into a jms object during the cipher creation.");
            }// end try-catch
            j.setStatus(instanceEntity.getVerifiedApplicationStatusCd());
        }// end if
        j.setCreateTs(applicationEntity.getCommon().getCreateTime());
        j.setCreateUserRefId(applicationEntity.getCommon().getCreateUserRefId() == null ? 0L : applicationEntity.getCommon().getCreateUserRefId());
        j.setDeleteInd(applicationEntity.getCommon().getDeleteIndicator());
        j.setUpdateTs(applicationEntity.getCommon().getUpdateTime());
        j.setUpdateUserRefId(applicationEntity.getCommon().getUpdateUserRefId() == null ? 0L : applicationEntity.getCommon().getUpdateUserRefId());
        log.debug("Exiting JmsObjectConvertor.toJms.");
        log.debug("Exiting JmsObjectConvertor.toJms with " + j);
        return j;
    }// end toJms

    /**
     * This method converts a decrypted message to a <code>JmsApplication</code>.
     *
     * @author <strong>Shane Duncan</strong> JCCC, Nov 3, 2016
     * @param messageMap
     *        message map to use
     * @param sc
     *        ShamenCipher to use
     * @return JmsApplication
     */
    public static JmsApplication toJms(HashMap<String, String> messageMap, ShamenCipher sc) {
        log.debug("Entering toJms");
        log.debug("This method converts a decrypted message to a JmsApplication.");
        log.debug("Entry parameters are: messageMap=" + String.valueOf(messageMap) + ", sc=" + String.valueOf(sc));
        JmsApplication j = null;
        if(messageMap != null){
            j = new JmsApplication();
            j.setApplicationAddress(messageMap.get(ShamenApplicationStatus.APPLICATION_ENVIRONMENT));
            j.setApplicationName(messageMap.get(ShamenApplicationStatus.APPLICATION_NAME));
            j.setVerifiedAdditionalInfo(messageMap.get(ShamenApplicationStatus.APPLICATION_ADDNTL_INFO));
            j.setVerifiedBranch(messageMap.get(ShamenApplicationStatus.APPLICATION_BRANCH));
            j.setVerifiedEar(messageMap.get(ShamenApplicationStatus.APPLICATION_EAR));
            j.setVerifiedVersion(messageMap.get(ShamenApplicationStatus.APPLICATION_VERSION));
            j.setSc(sc);
        }// end if
        log.debug("Return value is: JmsApplication=" + String.valueOf(j));
        log.debug("Exiting toJms");
        return j;
    }// end toJms

    /**
     * This method converts a <code>RunStatusEntity</code> to <code>JmsRunStatus</code>.
     *
     * @author <strong>Shane Duncan</strong> JCCC, Nov 3, 2015
     * @param runStatusEntity
     *        run status to convert
     * @return JmsRunStatus
     */
    public static JmsRunStatus toJms(RunStatusEntity runStatusEntity) {
        log.debug("Entering JmsObjectConvertor.toJms.");
        log.debug("This method converts a RunStatusEntity to JmsRunStatus. Entered with: " + runStatusEntity);
        if(runStatusEntity == null){
            return null;
        }// end if
        JmsRunStatus rs = new JmsRunStatus();
        JmsBatchApp batchApp = new JmsBatchApp();
        batchApp.setName(runStatusEntity.getBatchApp().getBatchName());
        rs.setBatchApp(batchApp);
        rs.setBatchAppRefId((runStatusEntity.getBatchApp() != null ? runStatusEntity.getBatchApp().getBatchAppRefId() : null));
        rs.setMainBatchAppRefId(runStatusEntity.getMainBatchAppRefId());
        rs.setRunNumber(runStatusEntity.getRunNumber());
        rs.setDescription(runStatusEntity.getDescription());
        rs.setResultCd(runStatusEntity.getResult().getResultCd());
        rs.setResultDesc(runStatusEntity.getResult().getResultDesc());
        rs.setResultDetail(runStatusEntity.getResultDetail());
        rs.setRunStatusRefId(runStatusEntity.getRunStatusRefId());
        rs.setScheduleRefId(runStatusEntity.getSchedule() != null ? runStatusEntity.getSchedule().getScheduleRefId() : null);
        rs.setStatusCd(runStatusEntity.getStatus().getStatusCd());
        rs.setStatusDesc(runStatusEntity.getStatus().getStatusDesc());
        rs.setStartTs(runStatusEntity.getStartTime() != null ? runStatusEntity.getStartTime() : ShamenEJBUtil.getDefaultTimeStamp());
        rs.setStopTs(runStatusEntity.getStopTime() != null ? runStatusEntity.getStopTime() : ShamenEJBUtil.getDefaultTimeStamp());
        rs.setCreateTs(runStatusEntity.getCommon().getCreateTime());
        rs.setCreateUserRefId(runStatusEntity.getCommon().getCreateUserRefId() == null ? 0L : runStatusEntity.getCommon().getCreateUserRefId());
        rs.setDeleteInd(runStatusEntity.getCommon().getDeleteIndicator());
        rs.setUpdateTs(runStatusEntity.getCommon().getUpdateTime());
        rs.setUpdateUserRefId(runStatusEntity.getCommon().getUpdateUserRefId() == null ? 0L : runStatusEntity.getCommon().getUpdateUserRefId());
        rs.setCollectionMembers(new ArrayList<JmsRunStatus>());
        // if the child list is not empty, then convert them too
        if(runStatusEntity.getCollectionMembers() != null && !runStatusEntity.getCollectionMembers().isEmpty()){
            for(int i = 0, j = runStatusEntity.getCollectionMembers().size();i < j;i++){
                rs.getCollectionMembers().add(toJmsCollectionMember(runStatusEntity.getCollectionMembers().get(i)));
            }// end for
        }// end if
        log.debug("Exiting JmsObjectConvertor.toJms.");
        log.debug("Exiting JmsObjectConvertor.toJms with " + rs);
        return rs;
    }// end toJms
    
    /**
     * This method converts a <code>RunStatusEntity</code> to <code>JmsRunStatus</code>.
     *
     * @author <strong>Shane Duncan</strong> JCCC, Nov 3, 2015
     * @param runStatusEntity
     *        run status to convert
     * @return JmsRunStatus
     */
    public static JmsRunStatus toJmsCollectionMember(RunStatusEntity runStatusEntity) {
        log.debug("Entering JmsObjectConvertor.toJms.");
        log.debug("This method converts a RunStatusEntity to JmsRunStatus. Entered with: " + runStatusEntity);
        if(runStatusEntity == null){
            return null;
        }// end if
        JmsRunStatus rs = new JmsRunStatus();
        JmsBatchApp batchApp = new JmsBatchApp();
        batchApp.setName(runStatusEntity.getBatchApp().getBatchName());
        rs.setBatchApp(batchApp);
        rs.setBatchAppRefId((runStatusEntity.getBatchApp() != null ? runStatusEntity.getBatchApp().getBatchAppRefId() : null));
        rs.setMainBatchAppRefId(runStatusEntity.getMainBatchAppRefId());
        rs.setRunNumber(runStatusEntity.getRunNumber());
        rs.setDescription(runStatusEntity.getDescription());
        rs.setResultCd(runStatusEntity.getResult().getResultCd());
        rs.setResultDesc(runStatusEntity.getResult().getResultDesc());
        rs.setResultDetail(runStatusEntity.getResultDetail());
        rs.setRunStatusRefId(runStatusEntity.getRunStatusRefId());
        rs.setScheduleRefId(runStatusEntity.getSchedule() != null ? runStatusEntity.getSchedule().getScheduleRefId() : null);
        rs.setStatusCd(runStatusEntity.getStatus().getStatusCd());
        rs.setStatusDesc(runStatusEntity.getStatus().getStatusDesc());
        rs.setStartTs(runStatusEntity.getStartTime() != null ? runStatusEntity.getStartTime() : ShamenEJBUtil.getDefaultTimeStamp());
        rs.setStopTs(runStatusEntity.getStopTime() != null ? runStatusEntity.getStopTime() : ShamenEJBUtil.getDefaultTimeStamp());
        rs.setCreateTs(runStatusEntity.getCommon().getCreateTime());
        rs.setCreateUserRefId(runStatusEntity.getCommon().getCreateUserRefId() == null ? 0L : runStatusEntity.getCommon().getCreateUserRefId());
        rs.setDeleteInd(runStatusEntity.getCommon().getDeleteIndicator());
        rs.setUpdateTs(runStatusEntity.getCommon().getUpdateTime());
        rs.setUpdateUserRefId(runStatusEntity.getCommon().getUpdateUserRefId() == null ? 0L : runStatusEntity.getCommon().getUpdateUserRefId());
        rs.setCollectionMembers(new ArrayList<JmsRunStatus>());
        // // if the child list is not empty, then convert them too
        // if(runStatusEntity.getCollectionMembers() != null && !runStatusEntity.getCollectionMembers().isEmpty()){
        // for(int i = 0, j = runStatusEntity.getCollectionMembers().size();i < j;i++){
        //// rs.getCollectionMembers().add(toJms(runStatusEntity.getCollectionMembers().get(i)));
        // }// end for
        // }// end if
        log.debug("Exiting JmsObjectConvertor.toJms.");
        log.debug("Exiting JmsObjectConvertor.toJms with " + rs);
        return rs;
    }// end toJms

    /**
     * This method converts a <code>ScheduleEntity</code> to <code>JmsSchedule</code>.
     *
     * @author <strong>Shane Duncan</strong> JCCC, Nov 3, 2015
     * @param scheduleEntity
     *        ScheduleEntity to convert
     * @return JmsSchedule
     */
    public static List<JmsSchedule> toJms(List<ScheduleEntity> scheduleList) {
        log.debug("Entering JmsObjectConvertor.toJms.");
        log.debug("This method converts a List<ScheduleEntity> to List<JmsSchedule>. Entered with: " + scheduleList);
        List<JmsSchedule> resultList = new ArrayList<JmsSchedule>();
        JmsSchedule j = null;
        StringBuilder details;
        if(!ShamenEJBUtil.isEmpty(scheduleList)){
            for(ScheduleEntity scheduleEntity : scheduleList){
                j = new JmsSchedule();
                details = new StringBuilder();
                j.setActive(scheduleEntity.getActiveInd());
                j.setBatchAppRefId((scheduleEntity.getBatchApp() != null ? scheduleEntity.getBatchApp().getBatchAppRefId() : null));
                j.setFrequencyCd(scheduleEntity.getFrequency().getFrequencyCd());
                j.setFrequencyCdDesc(scheduleEntity.getFrequency().getFrequencyDesc());
                j.setFrequencyTypeCd(scheduleEntity.getFrequencyType().getFrequencyTypeCd());
                j.setFrequencyTypeCdDesc(scheduleEntity.getFrequencyType().getFrequencyTypeDesc());
                // j.setLastRunTs(null);
                if(scheduleEntity.getLastRunStatus() != null){
                    j.setLastRunStatusCd(scheduleEntity.getLastRunStatus().getResult() != null ? scheduleEntity.getLastRunStatus().getResult().getResultCd() : "UNK");
                }else{
                    j.setLastRunStatusCd("UNK");
                }// end if/else
                j.setRecurNo(scheduleEntity.getRecurNo() != null ? String.valueOf(scheduleEntity.getRecurNo()) : "1");
                j.setRepeatCd(scheduleEntity.getRepeat().getRepeatCd());
                j.setRepeatCdDesc(scheduleEntity.getRepeat().getRepeatDesc());
                j.setScheduleRefId(scheduleEntity.getScheduleRefId());
                j.setScheduleStartDt(scheduleEntity.getScheduleStartDt());
                j.setStartTime(scheduleEntity.getStartTime());
                // DayNumber must be strung together as a common separated string.
                j.setDayNo(scheduleEntity.getDayNo());
                // Weekdays must be strung together as a common separated string.
                j.setWeekdays(scheduleEntity.getWeekdays());
                // Week number must be strung together as a common separated string.
                j.setWeekNo(scheduleEntity.getWeekNumber());
                j.setCreateTs(scheduleEntity.getCommon().getCreateTime());
                j.setCreateUserRefId(scheduleEntity.getCommon().getCreateUserRefId() == null ? 0L : scheduleEntity.getCommon().getCreateUserRefId());
                j.setDeleteInd(scheduleEntity.getCommon().getDeleteIndicator());
                j.setUpdateTs(scheduleEntity.getCommon().getUpdateTime());
                j.setUpdateUserRefId(scheduleEntity.getCommon().getUpdateUserRefId() == null ? 0L : scheduleEntity.getCommon().getUpdateUserRefId());
                if(j.getFrequencyCd().equalsIgnoreCase("ONT")){
                    details.append("Execute task at schedule start time on start date ");
                    if(!"BLA".equalsIgnoreCase(j.getRepeatCd())){
                        details.append(" and repeat every ").append(j.getRepeatCdDesc());
                    }// end if
                }else if(j.getFrequencyCd().equalsIgnoreCase("DLY")){
                    details.append("Execute task every ");
                    if(!"BLA".equalsIgnoreCase(j.getRepeatCd())){
                        details.append(j.getRepeatCdDesc()).append(" every ");
                    }// end if
                    if(Integer.valueOf(j.getRecurNo()) > 1){
                        details.append(j.getRecurNo()).append(" day(s)");
                    }else{
                        details.append("day");
                    }// end if/else
                }else if(j.getFrequencyCd().equalsIgnoreCase("WKY")){
                    details.append("Execute task every ").append(convertWeekdayString(j.getWeekdays()));
                }else if(j.getFrequencyCd().equalsIgnoreCase("MTY")){
                    if(j.getFrequencyTypeCd().equalsIgnoreCase("MWD")){
                        details.append("Execute task on the ").append(convertWeekNumberString(j.getWeekNo())).append(" ").append(convertWeekdayString(j.getWeekdays())).append(" of the month");
                    }else if(j.getFrequencyTypeCd().equalsIgnoreCase("DOM")){
                        details.append("Execute task on the ").append(getFullDayNumberDesc(j.getDayNo())).append(" day(s) of the month");
                    }// end if/else
                }// end if/else
                j.setScheduleDetails(details.toString());
                resultList.add(j);
            }// end for
        }// end if
        log.debug("Exiting JmsObjectConvertor.toJms.");
        log.debug("Exiting JmsObjectConvertor.toJms with " + resultList);
        return resultList;
    }// end toJms

    /**
     * This method converts a list of <code>Object</code> into a list of <code>JmsStatusSummary</code>.
     *
     * @author <strong>Zachary Lisle</strong> JCCC, Sept 1, 2021
     * @param summaries
     *        {@link JmsRunStatusSummary} to convert
     * @return List of {@link JmsRunStatusSummary}
     */
    public static List<JmsRunStatusSummary> toJmsStatusSummaries(List<Object> summaries) {
        log.debug("Entering JmsObjectConvertor.toJmsStatusSummaries.");
        log.debug(" This method converts a list of Object into a list of JmsStatusSummary. Entered with: " + String.valueOf(summaries.size()));
        List<JmsRunStatusSummary> resultList = new ArrayList<JmsRunStatusSummary>();
        if(!ShamenEJBUtil.isEmpty(summaries)){
            String start = null;
            String stop = null;
            JmsRunStatusSummary statusSummary = null;
            Object[] objAry = null;
            for(int i = 0, j = summaries.size();i < j;i++){
                objAry = (Object[]) summaries.get(i);
                statusSummary = new JmsRunStatusSummary();
                statusSummary.setRunStatusRefId(Long.valueOf(objAry[0].toString()));
                statusSummary.setBatchAppRefId(Long.valueOf(objAry[1].toString()));
                statusSummary.setBatchAppName((String) objAry[2]);
                start = ShamenEJBUtil.getFormattedDateTimeAsString((java.sql.Timestamp) objAry[3]);
                stop = ShamenEJBUtil.getFormattedDateTimeAsString((java.sql.Timestamp) objAry[4]);
                statusSummary.setStatus((String) objAry[5]);
                statusSummary.setDetails((String) objAry[6]);
                statusSummary.setRunNumber(Long.valueOf(objAry[7].toString()));
                statusSummary.setFrom((String) objAry[8]);
                //statusSummary.setBatchType((String) objAry[9]); //TODO sls000is uncomment to reflect update

                statusSummary.setStartTs(start);
                statusSummary.setStopTs(stop);
                statusSummary.setDuration(ShamenEJBUtil.getTimeDifference(start, stop));
                statusSummary.setDurationInMinutes(ShamenEJBUtil.getTimeDifferenceInMinutes(start, stop));

                resultList.add(statusSummary);
            }// end for
        }// end if
        log.debug("Exiting JmsObjectConvertor.toJmsStatusSummaries.");
        log.debug("Exiting JmsObjectConvertor.toJmsStatusSummaries with " + resultList);
        return resultList;
    }// end toJmsStatusSummaries

    /**
     * This method converts a list of {@link RunStatusEntity} into a list of <code>JmsStatusSummary</code>.
     *
     * @author <strong>Zachary Lisle</strong> JCCC, Sept 2, 2021
     * @param runStatusList
     *        {@link RunStatusEntity} to convert
     * @return List of {@link JmsRunStatus}
     */
    public static List<JmsRunStatus> toJmsRunStatusList(List<RunStatusEntity> runStatusList) {
        log.debug("Entering JmsObjectConvertor.toJmsRunStatusList.");
        log.debug(" This method converts a list of RunStatusEntity into a list of JmsRunStatus. Entered with: " + String.valueOf(runStatusList.size()));
        List<JmsRunStatus> resultList = new ArrayList<JmsRunStatus>();
        if(!ShamenEJBUtil.isEmpty(runStatusList)){
            RunStatusEntity runStatus = null;
            for(int i = 0, j = runStatusList.size();i < j;i++){
                runStatus = runStatusList.get(i);
                resultList.add(toJms(runStatus));
            }// end for
        }// end if
        log.debug("Exiting JmsObjectConvertor.toJmsRunStatusList.");
        log.debug("Exiting JmsObjectConvertor.toJmsRunStatusList with " + resultList);
        return resultList;
    }// end toJmsRunStatusList

    /**
     * Returns the Week Number Name as comma separated String
     * 
     * @return String
     */
    public static String getFullDayNumberDesc(String dayNumber) {
        if(dayNumber.contains("32")){
            dayNumber.replace("32", "Last");
        }// end if
        return dayNumber;
    }// end getFullDayNumberDesc

    /**
     * Returns the month name from a month int value.
     *
     * @param month
     *        month number
     * @return String
     */
    public static String getMonthFromInt(int month) {
        log.debug("Entering getMonthFromInt");
        log.debug("Returns the month name from a month int value.");
        log.debug("Entry parameters are: month=" + String.valueOf(month));
        String result = new DateFormatSymbols().getMonths()[month - 1];
        log.debug("Return value is: result=" + String.valueOf(result));
        log.debug("Exiting getMonthFromInt");
        return result;
    }// end getMonthFromInt

    /**
     * Converts a comma separated string of week numbers to a comma separated string of week number names
     *
     * @param weekNumber
     *        the weekNumber string
     * @return String
     */
    public static String convertWeekNumberString(String weekNumber) {
        log.debug("Entering convertWeekNumberString");
        log.debug("Converts a comma separated string of week numbers to a comma separated string of week number names");
        log.debug("Entry parameters are: weekNumber=" + String.valueOf(weekNumber));
        String returnResult = EMPTY_STRING;
        if(!ShamenEJBUtil.isNullOrEmpty(weekNumber)){
            String[] sarray = weekNumber.split(",");
            String[] result = new String[sarray.length];
            for(int i = 0;i < sarray.length;i++){
                if(Integer.parseInt(sarray[i]) == 1){
                    result[i] = "First";
                }else if(Integer.parseInt(sarray[i]) == 2){
                    result[i] = "Second";
                }else if(Integer.parseInt(sarray[i]) == 3){
                    result[i] = "Third";
                }else if(Integer.parseInt(sarray[i]) == 4){
                    result[i] = "Fourth";
                }else if(Integer.parseInt(sarray[i]) == 5){
                    result[i] = "Last";
                }// end if/else

            }// end for
            returnResult = convertArrayToCommaSeparatedString(result);
        }// end if
        log.debug("Return value is: returnResult=" + String.valueOf(returnResult));
        log.debug("Exiting convertWeekNumberString");
        return returnResult;
    }// end convertWeekNumber

    /**
     * Returns the week day name from a day int value.
     *
     * @param day
     *        int
     * @return String
     */
    public static String getDayFromInt(int day) {
        log.debug("Entering getDayFromInt");
        log.debug("Returns the week day name from a day int value.");
        log.debug("Entry parameters are: day=" + String.valueOf(day));
        String result = new DateFormatSymbols().getWeekdays()[day];
        log.debug("Return value is: result=" + String.valueOf(result));
        log.debug("Exiting getDayFromInt");
        return result;
    }// end getDayFromInt

    /**
     * Returns the week days in String []
     *
     * @return String []
     */
    public static String[] getWeekDays() {
        log.debug("Entering getWeekDays");
        log.debug("Returns the week days in String []");
        String[] result = new DateFormatSymbols().getWeekdays();
        log.debug("Return value is: result=" + Arrays.toString(result));
        log.debug("Exiting getWeekDays");
        return result;
    }// end getWeekDays

    /**
     * Converts a comma separated string of weekday numbers to a comma separated string of week day names
     *
     * @param weekdays
     *        the string of weekdays
     * @return String
     */
    public static String convertWeekdayString(String weekdays) {
        log.debug("Entering convertWeekdayString");
        log.debug("Converts a comma separated string of weekday numbers to a comma separated string of week day names");
        log.debug("Entry parameters are: weekdays=" + String.valueOf(weekdays));
        String returnResult = EMPTY_STRING;
        if(!ShamenEJBUtil.isNullOrEmpty(weekdays)){
            String[] sarray = weekdays.split(",");
            String[] result = new String[sarray.length];
            for(int i = 0;i < sarray.length;i++){
                result[i] = getDayFromInt(Integer.parseInt(sarray[i]));
            }// end for
            returnResult = convertArrayToCommaSeparatedString(result);
        }// end if
        log.debug("Return value is: returnResult=" + String.valueOf(returnResult));
        log.debug("Exiting convertWeekdayString");
        return returnResult;
    }// end convertWeekdayString

    /**
     * Converts a comma separated string of month numbers to a comma separated string of month names
     *
     * @param monthNumbers
     *        the string of monthNumbers
     * @return String
     */
    public static String convertMonthString(String monthNumbers) {
        log.debug("Entering convertMonthString");
        log.debug("Converts a comma separated string of month numbers to a comma separated string of month names");
        log.debug("Entry parameters are: sarray=" + String.valueOf(monthNumbers));
        String returnResult = EMPTY_STRING;
        if(!ShamenEJBUtil.isNullOrEmpty(monthNumbers)){
            String[] sarray = monthNumbers.split(",");
            String[] result = new String[sarray.length];
            for(int i = 0;i < sarray.length;i++){
                result[i] = getMonthFromInt(Integer.parseInt(sarray[i]));
            }// end for
            returnResult = convertArrayToCommaSeparatedString(result);
        }// end if
        log.debug("Return value is: returnResult=" + String.valueOf(returnResult));
        log.debug("Exiting convertMonthString");
        return returnResult;
    }// end convertMonthString

    /**
     * This method converts a string array into a comma separated string.
     *
     * @author <strong>Shane Duncan</strong> JCCC, Nov 3, 2015
     * @param inArray
     *        the array of comma separated strings
     * @return String
     */
    public static String convertArrayToCommaSeparatedString(String[] inArray) {
        log.debug("Entering JmsObjectConvertor.convertArrayToCommaSeparatedString.");
        log.debug("This method converts a string array into a comma separated string. Entered with: " + inArray);
        String returnString = null;
        if(inArray != null){
            StringBuffer sb = new StringBuffer();
            for(int i = 0, i2 = inArray.length;i < i2;i++){
                if(sb.length() == 0){
                    sb.append(inArray[i]);
                }else{
                    sb.append(",").append(inArray[i]);
                }// end else-if
            }// end for
            returnString = sb.toString();
        }// end if
        log.debug("Exiting JmsObjectConvertor.convertArrayToCommaSeparatedString.");
        log.debug("Exiting JmsObjectConvertor.convertArrayToCommaSeparatedString with " + returnString);
        return returnString;
    }// end convertArrayToCommaSeparatedString

    /**
     * This method converts a <code>JmsRunStatus</code> object to a <code>RunStatusEntity</code> object.
     *
     * @author <strong>Shane Duncan</strong> JCCC, Nov 3, 2015
     * @param j
     *        JmsRunStatus to convert
     * @return RunStatusEntity
     */
    public static RunStatusEntity toShamen(JmsRunStatus j) {
        log.debug("Entering JmsObjectConvertor.toShamen.");
        log.debug("This method converts a JmsRunStatus object to a RunStatusEntity object.. Entered with: " + j);
        if(j == null){
            return null;
        }// end if
        RunStatusEntity s = new RunStatusEntity();
        s.setRunStatusRefId(j.getRunStatusRefId());
        s.setMainBatchAppRefId(j.getMainBatchAppRefId());
        s.setRunNumber(j.getRunNumber());
        s.setBatchApp((j.getBatchAppRefId() != null && j.getBatchAppRefId() != 0L) ? new BatchAppEntity(j.getBatchAppRefId()) : null);
        s.setSchedule(j.getScheduleRefId() == null ? null : new ScheduleEntity(j.getScheduleRefId()));
        s.setStatus(j.getStatusCd() == null ? new StatusCodeEntity() : new StatusCodeEntity(j.getStatusCd(), ""));
        s.setResult(j.getResultCd() == null ? new ResultCodeEntity("BLA", "") : new ResultCodeEntity(j.getResultCd(), ""));
        s.setResultDetail(j.getResultDetail() == null ? "" : j.getResultDetail());
        s.setDescription(j.getDescription() == null ? "" : j.getDescription());
        s.setStartTime(j.getStartTs());
        s.setStopTime(j.getStopTs() != null ? j.getStopTs() : ShamenEJBUtil.getDefaultTimeStamp());

        s.setCommon(new CommonEntity());
        s.getCommon().setCreateTime(j.getCreateTs());
        s.getCommon().setCreateUserRefId(j.getCreateUserRefId() == null ? 0L : j.getCreateUserRefId());
        s.getCommon().setDeleteIndicator(j.getDeleteInd() == null || j.getDeleteInd().equalsIgnoreCase("") ? "N" : j.getDeleteInd());
        s.getCommon().setUpdateTime(j.getUpdateTs());
        s.getCommon().setUpdateTime(j.getUpdateTs() != null ? j.getUpdateTs() : ShamenEJBUtil.getDefaultTimeStamp());
        s.getCommon().setUpdateUserRefId(j.getUpdateUserRefId() == null ? 0L : j.getUpdateUserRefId());
        log.debug("Exiting JmsObjectConvertor.toShamen.");
        log.debug("Exiting JmsObjectConvertor.toShamen with " + s);
        return s;
    }// end toShamen

    /**
     * This method converts a <code>JmsApplication</code> object to a <code>ApplicationEntity</code> object.
     *
     * @author <strong>Shane Duncan</strong> JCCC, Nov 3, 2015
     * @param app
     *        JmsApplication to convert
     * @return ApplicationEntity
     */
    public static ApplicationEntity toShamen(JmsApplication app) {
        log.debug("Entering JmsObjectConvertor.toShamen.");
        log.debug("This method converts a JmsApplication object to a ApplicationEntity object. Entered with: " + app);
        if(app == null){
            return null;
        }// end if
        ApplicationEntity applicationEntity = new ApplicationEntity();
        applicationEntity.setApplicationAddress(app.getApplicationAddress());
        applicationEntity.setApplicationName(app.getApplicationName());
        applicationEntity.setApplicationRefId(app.getApplicationRefId());
        ApplicationStatusCodeEntity applicationStatusCodeEntity = new ApplicationStatusCodeEntity();
        applicationStatusCodeEntity.setApplicationStatusCd(app.getRequestedStatus());
        CommonEntity common = new CommonEntity();
        common.setCreateTime(app.getCreateTs());
        common.setCreateUserRefId(app.getCreateUserRefId());
        common.setUpdateUserRefId(app.getUpdateUserRefId());
        common.setUpdateTime(app.getUpdateTs());
        applicationEntity.setCommon(common);

        log.debug("Exiting JmsObjectConvertor.toShamen.");
        log.debug("Exiting JmsObjectConvertor.toShamen with " + applicationEntity);
        return applicationEntity;
    }// end toShamen

    /**
     * Formats the Timestamp
     *
     * @param sqlTime
     *        the passed in Timestamp
     * @return either the formatted Timestamp or null
     */
    public static String getFormattedDateTimeAsString(Timestamp sqlTime) {
        log.debug("Entering getFormattedDateTimeAsString(). The time to convert is: " + (sqlTime != null ? sqlTime.toString() : "null"));
        String result = null;
        if(sqlTime != null){
            result = new SimpleDateFormat(EJBConstants.SDFTF).format(sqlTime);
            if(EJBConstants.DEFAULT_TIMESTAMP.equalsIgnoreCase(result)){
                result = EJBConstants.EMPTY_STRING;
            }// end if
        }// end if
        log.debug("Exiting getFormattedTimeAsString(). The result is: " + (result != null ? result : "null"));
        return result;
    }// end getFormattedDateTimeAsString

    /**
     * Returns a java.sql.Date from a java.util.Date
     *
     * @param date
     *        date
     * @return java.sql.Date
     */
    public static java.sql.Date getSQLDate(java.util.Date date) {
        return new java.sql.Date(date.getTime());
    }// end getSQLDate

    /**
     * Converts the hh:mm a Time value to java.sql.Time
     *
     * @param v
     *        timesString
     * @return java.sql.Time
     */
    public static Time getSQLTime(String v) {
        log.debug("Entering ShamenUtil.getSQLTime.");
        log.debug("Parameters are: v=" + String.valueOf(v));

        Time result = null;
        if((v != null) && !v.equals("") && (v.indexOf(":") != -1)){
            StringBuffer sb = new StringBuffer();
            if(v.indexOf("p") != -1){
                int hold = Integer.valueOf(v.substring(0, v.indexOf(":")));
                hold = hold + 12;
                sb.append(hold).append(":").append(v.substring(v.indexOf(":") + 1, v.indexOf(":") + 3));
                v = sb.toString();
            }else if(v.indexOf("a") != -1 && v.substring(0, 2).equalsIgnoreCase("12")){
                sb.append("00").append(":").append(v.substring(v.indexOf(":") + 1, v.indexOf(":") + 3));
                v = sb.toString();
            }// end if/else
            try{
                result = new Time(new SimpleDateFormat(EJBConstants.STF).parse(v.trim()).getTime());
            }catch(ParseException e){
                log.info("Input String cannot be parsed in to time value.. Returning null");
            }// end catch
        }// end if
        log.debug("Exiting ShamenUtil.getSQLTime. result=" + String.valueOf(result));
        return result;
    }// end method

    /**
     * This method is used to convert an list of {@link BatchAppEntity} to an list of {@link JmsBatchApp}.
     *
     * @param list
     *        The list of entity object to convert.
     * @return The converted list.
     */
    public static List<JmsBatchApp> toBatchAppListBusiness(List<BatchAppEntity> list) {
        log.debug("Entering toBatchAppListBusiness. Incoming parameter size is: " + list == null ? "0" : list.size());
        log.debug("This method is used to convert an list of Object to an list of BatchAppModel.");
        List<JmsBatchApp> jmsBatchAppList = new ArrayList<JmsBatchApp>();
        if(list != null && !list.isEmpty()){
            for(int i = 0, j = list.size();i < j;i++){
                jmsBatchAppList.add(toJms(list.get(i)));
            }// end for
        }// end if

        log.debug("Exiting toBatchAppListBusiness. Return value is: " + String.valueOf(jmsBatchAppList));
        return jmsBatchAppList;
    }// end toBatchAppListBusiness

    /**
     * Returns the String representation of input date in MM/dd/yyyy format. Returns empty string if date is null.
     *
     * @param date
     *        java.sql.Date
     * @return date as string
     * @throws ParseException
     *         if an exception occurred
     */
    public static String getDateAsString(final Date date) throws ParseException {
        log.debug("Entering method getDateAsString");
        log.debug("Parameters: date=" + (date == null ? "null" : String.valueOf(date)));
        String result = "";
        if(date != null && !getDate("12/31/7799").equals(date)){
            result = new SimpleDateFormat(SDF).format(date);
        }// end if
        log.debug("Exiting method getDateAsString");
        return result;
    }// end method

    /**
     * Parses input date in "MM/dd/yyyy" format.
     *
     * @param date
     *        date
     * @return Date object for the input date
     */
    public static Date getDate(String date) {
        log.debug("Entering gov.doc.isu.dwarf.util.AppUtil - method getDate");
        log.debug("Parameters: date=" + (date == null ? "null" : String.valueOf(date)));
        Date result = null;
        if(date != null && date.trim().length() > 0){
            try{
                result = new SimpleDateFormat(SDF).parse(date);
            }catch(ParseException e){
                log.error("getDate- Exception parsing date: " + date);

            }// end try/catch
        }// end if
        log.debug("Exiting gov.doc.isu.dwarf.util.AppUtil - method getDate result=" + String.valueOf(result));
        return result;
    }// end getDate
}// end class
