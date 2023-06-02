package gov.doc.isu.shamen.ejb.convertor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import gov.doc.isu.shamen.jms.models.JmsRunStatus;
import gov.doc.isu.shamen.jpa.comparator.BatchAppEntityComparator;
import gov.doc.isu.shamen.jpa.comparator.RunStatusEntityComparator;
import gov.doc.isu.shamen.jpa.entity.ApplicationEntity;
import gov.doc.isu.shamen.jpa.entity.AuthorizedUserEntity;
import gov.doc.isu.shamen.jpa.entity.BatchAppEntity;
import gov.doc.isu.shamen.jpa.entity.BatchTypeCodeEntity;
import gov.doc.isu.shamen.jpa.entity.CommonEntity;
import gov.doc.isu.shamen.jpa.entity.ControllerEntity;
import gov.doc.isu.shamen.jpa.entity.ControllerStatusCodeEntity;
import gov.doc.isu.shamen.jpa.entity.FrequencyCodeEntity;
import gov.doc.isu.shamen.jpa.entity.FrequencyTypeCodeEntity;
import gov.doc.isu.shamen.jpa.entity.RepeatCodeEntity;
import gov.doc.isu.shamen.jpa.entity.ResultCodeEntity;
import gov.doc.isu.shamen.jpa.entity.RunStatusEntity;
import gov.doc.isu.shamen.jpa.entity.ScheduleEntity;
import gov.doc.isu.shamen.jpa.entity.StatusCodeEntity;
import gov.doc.isu.shamen.jpa.entity.SystemEntity;

/**
 * Class used to convert Shamen Objects
 *
 * @author Shane Duncan JCCC
 */
public class EJBObjectConvertor {
    private static Logger log = Logger.getLogger("gov.doc.isu.shamen.ejb.convertor.EJBObjectConvertor");

    /**
     * This method is used to convert an list of {@link Object} to an list of {@link RunStatusEntity}.
     *
     * @param list
     *        The List<Object> to convert.
     * @return List<RunStatusEntity>
     */
    public static List<RunStatusEntity> toRunStatusEntityFromObject(List<Object> list) {
        log.debug("Entering toRunStatusEntityFromObject");
        log.debug("This method is used to convert an list of Object to an list of RunStatusEntity.");
        log.debug("Entry parameters are: list=" + String.valueOf(list));
        List<RunStatusEntity> modelList = new ArrayList<RunStatusEntity>();
        List<RunStatusEntity> finalList = new ArrayList<RunStatusEntity>();
        Map<Long, RunStatusEntity> collectionList = new TreeMap<Long, RunStatusEntity>();
        modelList = toRunStatusListBusiness(list);
        if(modelList != null && !modelList.isEmpty()){
            // consolidate the collections into one job
            for(int i = 0, j = modelList.size();i < j;i++){
                if(modelList.get(i).getMainBatchAppRefId() == null){
                    // hold this record to compare against children
                    collectionList.put(modelList.get(i).getBatchApp().getBatchAppRefId(), modelList.get(i));
                    finalList.add(modelList.get(i));
                }else{
                    // loop through and add this collection member to the right parent collection
                    for(int h = 0, k = finalList.size();h < k;h++){
                        if(finalList.get(h).getBatchApp().getBatchAppRefId().equals(modelList.get(i).getMainBatchAppRefId())){
                            // only show this child record if its parent collection's startTs is less than its own
                            RunStatusEntity runStatusEntity = collectionList.get(modelList.get(i).getMainBatchAppRefId());
                            Long runNbr = (runStatusEntity != null ? runStatusEntity.getRunNumber() : null);
                            if(runNbr.equals(modelList.get(i).getRunNumber())){
                                finalList.get(h).getCollectionMembers().add(modelList.get(i));
                            }// end if
                        }// end if
                    }// end for
                }// end if-else
            }// end for
            Collections.sort(finalList, Collections.reverseOrder(new RunStatusEntityComparator()));
        }// end if
        log.debug("Return value is: finalList=" + String.valueOf(finalList));
        log.debug("Exiting toRunStatusEntityFromObject");
        return finalList;
    }// end toEJB

    /**
     * This method is used to convert an list of {@link Object} to an list of {@link RunStatusEntity}.
     *
     * @param list
     *        list from query result set The list of entity object to convert.
     * @return List<RunStatusEntity>
     */
    public static List<RunStatusEntity> toRunStatusListBusiness(List<Object> list) {
        log.debug("Entering toRunStatusListBusiness");
        log.debug("This method is used to convert an list of Object to an list of RunStatusEntity.");
        log.debug("Entry parameters are: list=" + String.valueOf(list));
        List<RunStatusEntity> modelList = new ArrayList<RunStatusEntity>();
        RunStatusEntity model = null;
        if(list != null && !list.isEmpty()){
            for(int i = 0, j = list.size();i < j;i++){
                Object[] element = (Object[]) list.get(i);
                model = new RunStatusEntity();
                model.setRunStatusRefId(Long.valueOf(element[0].toString()));
                model.setStartTime((Timestamp) element[1]);
                model.setStopTime((Timestamp) element[2]);
                StatusCodeEntity statusCd = new StatusCodeEntity();
                statusCd.setStatusCd((String) (element[3] == null ? "" : element[3]));
                statusCd.setStatusDesc((String) (element[4] == null ? "" : element[4]));
                model.setStatus(statusCd);
                ResultCodeEntity result = new ResultCodeEntity();
                result.setResultDesc((String) (element[5] == null ? "" : element[5]));
                model.setResult(result);
                model.setBatchApp(new BatchAppEntity());
                model.getBatchApp().setBatchName((String) element[6]);
                model.setSchedule(new ScheduleEntity());
                model.getSchedule().setScheduleRefId((element[7] != null ? Long.valueOf((Integer) element[7]) : null));
                model.getBatchApp().setBatchAppRefId(Long.valueOf(element[8].toString()));
                model.setCommon(new CommonEntity());
                model.setUser(element[9] != null && element[10] != null ? String.valueOf(element[10]).trim() + ", " + String.valueOf(element[9]) : "");
                model.setMainBatchAppRefId(element[11] != null ? Long.valueOf(element[11].toString()) : null);
                model.setDescription((element[12] != null ? element[12].toString() : ""));
                model.setRunNumber((element[13] != null ? Long.valueOf(element[13].toString()) : null));
                model.getBatchApp().setBatchType(new BatchTypeCodeEntity());
                model.getBatchApp().getBatchType().setBatchTypeCd((String) (element[14] == null ? "" : element[14]));
                model.getBatchApp().setSystem(new SystemEntity());
                model.getBatchApp().getSystem().setSystemRefId((element[15] != null ? Long.valueOf(element[15].toString()) : null));
                model.getResult().setResultCd((String) (element[16] == null ? "" : element[16]));
                model.setResultDetail((String) (element[18] == null ? "" : element[18]));
                modelList.add(model);
            }// end for
        }// end if
        log.debug("Return value is: modelList=" + String.valueOf(modelList));
        log.debug("Exiting toRunStatusListBusiness");
        return modelList;
    }// end toRunStatusListBusiness

    /**
     * This method is used to convert an list of {@link Object} to an list of {@link BatchAppEntity}.
     *
     * @param list
     *        The list of entity object to convert.
     * @return List<BatchAppEntity>
     */
    public static List<BatchAppEntity> toBatchAppListEntityFromObj(List<Object> list) {
        log.debug("Entering toBatchAppListEntityFromObj");
        log.debug("This method is used to convert an list of Object to an list of BatchAppEntity.");
        log.debug("Entry parameters are: list=" + String.valueOf(list));
        List<BatchAppEntity> modelList = new ArrayList<BatchAppEntity>();
        Map<Long, BatchAppEntity> batchMap = new HashMap<Long, BatchAppEntity>();
        BatchAppEntity model = null;
        if(list != null && !list.isEmpty()){
            for(int i = 0, j = list.size();i < j;i++){
                Object[] element = (Object[]) list.get(i);
                model = batchMap.get(Long.valueOf(element[0].toString()));
                if(model == null){
                    model = new BatchAppEntity();
                    model.setBatchAppRefId(Long.valueOf(element[0].toString()));
                    model.setController(new ControllerEntity());
                    model.getController().setControllerName(element[1].toString());
                    model.getController().setControllerStatusCodeEntity(new ControllerStatusCodeEntity());
                    model.setBatchName(element[2].toString());
                    model.setSchedule(new ArrayList<ScheduleEntity>());
                    ScheduleEntity scheduleEntity = new ScheduleEntity();
                    scheduleEntity.setFrequency(new FrequencyCodeEntity());
                    scheduleEntity.setFrequencyType(new FrequencyTypeCodeEntity());
                    scheduleEntity.setRepeat(new RepeatCodeEntity());
                    scheduleEntity.setScheduleStartDt((element[3] == null ? null : (java.sql.Date) element[3]));
                    scheduleEntity.setStartTime(element[4] == null ? null : (java.sql.Time) element[4]);
                    scheduleEntity.setCommon(new CommonEntity());
                    model.setApplication(new ApplicationEntity());
                    model.getApplication().setApplicationName(element[5] == null ? "" : element[5].toString());
                    model.getApplication().setApplicationAddress(element[6] == null ? "" : element[6].toString());
                    model.setBatchType(new BatchTypeCodeEntity());
                    model.getBatchType().setBatchTypeDesc(element[7] == null ? "" : element[7].toString());
                    if("Collection".equalsIgnoreCase(model.getBatchType().getBatchTypeDesc())){
                        model.getBatchType().setBatchTypeCd("COL");
                    }// end if
                    scheduleEntity.setActiveInd(element[8] == null ? "N" : element[8].toString());
                    model.getController().getControllerStatusCodeEntity().setControllerStatusCd((element[9] == null ? "" : element[9].toString()));
                    scheduleEntity.getFrequency().setFrequencyDesc(element[10] == null ? "" : element[10].toString());
                    model.setSystem(new SystemEntity());
                    model.getSystem().setSystemName(element[11] == null ? "" : element[11].toString());
                    model.getController().setControllerRefId(element[12] == null ? 0L : Long.valueOf(element[12].toString()));
                    model.getController().getControllerStatusCodeEntity().setControllerStatusCd(element[13] == null ? "" : element[13].toString());
                    scheduleEntity.getFrequency().setFrequencyCd(element[14] == null ? "" : element[14].toString());
                    scheduleEntity.setRecurNo((element[15] == null ? 0L : Long.valueOf(element[15].toString())));
                    scheduleEntity.getRepeat().setRepeatCd((element[16] == null ? "" : element[16].toString()));
                    // weekNo
                    scheduleEntity.setWeekNumber(element[17] == null ? null : element[17].toString());
                    // weekdays
                    scheduleEntity.setWeekdays(element[18] == null ? null : element[18].toString());
                    // dayNo
                    scheduleEntity.setDayNo(element[19] == null ? null : element[19].toString());
                    scheduleEntity.getFrequencyType().setFrequencyTypeCd((element[20] == null ? "" : element[20].toString()));
                    model.getSystem().setSystemRefId(element[21] == null ? 0L : Long.valueOf(element[21].toString()));
                    if(element.length > 22){
                        model.setPartOfCollection(element[22] == null ? "" : element[22].toString());
                        scheduleEntity.setScheduleRefId((element[23] == null ? null : Long.valueOf(element[23].toString())));
                        model.setPointOfContact(new AuthorizedUserEntity());
                        model.getPointOfContact().setFirstName(element[24] == null ? "" : element[24].toString());
                        model.getPointOfContact().setLastName(element[25] == null ? "" : element[25].toString());
                        scheduleEntity.getCommon().setCreateTime((element[26] == null ? null : (Timestamp) element[26]));
                    }// end if
                    if(element.length == 28){
                        model.setExecutionCount(element[27] == null ? 0L : Long.valueOf(element[27].toString()));
                    }// end if
                    RunStatusEntity run = new RunStatusEntity();
                    run.setResult(new ResultCodeEntity());
                    run.getResult().setResultCd("UNK");
                    scheduleEntity.setLastRunStatus(run);
                    model.getSchedule().add(scheduleEntity);
                }else{
                    ScheduleEntity scheduleEntity = new ScheduleEntity();
                    scheduleEntity.setFrequency(new FrequencyCodeEntity());
                    scheduleEntity.setFrequencyType(new FrequencyTypeCodeEntity());
                    scheduleEntity.setRepeat(new RepeatCodeEntity());
                    scheduleEntity.setScheduleStartDt((element[3] == null ? null : (java.sql.Date) element[3]));
                    scheduleEntity.setStartTime(element[4] == null ? null : (java.sql.Time) element[4]);
                    scheduleEntity.setCommon(new CommonEntity());
                    scheduleEntity.setActiveInd(element[8] == null ? "N" : element[8].toString());
                    scheduleEntity.getFrequency().setFrequencyDesc(element[10] == null ? "" : element[10].toString());
                    scheduleEntity.getFrequency().setFrequencyCd(element[14] == null ? "" : element[14].toString());
                    scheduleEntity.setRecurNo((element[15] == null ? 0L : Long.valueOf(element[15].toString())));
                    scheduleEntity.getRepeat().setRepeatCd((element[16] == null ? "" : element[16].toString()));
                    // weekNo
                    scheduleEntity.setWeekNumber(element[17] == null ? null : element[17].toString());
                    // weekdays
                    scheduleEntity.setWeekdays(element[18] == null ? null : element[18].toString());
                    // dayNo
                    scheduleEntity.setDayNo(element[19] == null ? null : element[19].toString());
                    scheduleEntity.getFrequencyType().setFrequencyTypeCd((element[20] == null ? "" : element[20].toString()));
                    if(element.length > 22){
                        scheduleEntity.setScheduleRefId((element[23] == null ? null : Long.valueOf(element[23].toString())));
                        scheduleEntity.getCommon().setCreateTime((element[26] == null ? null : (Timestamp) element[26]));
                    }// end if
                    model.getSchedule().add(scheduleEntity);
                }
                batchMap.put(Long.valueOf(element[0].toString()), model);

            }// end for
            modelList.addAll(batchMap.values());
        }// end if
        Collections.sort(modelList, new BatchAppEntityComparator());
        log.debug("Return value is: modelList=" + String.valueOf(modelList));
        log.debug("Exiting toBatchAppListEntityFromObj");
        return modelList;
    }// end toBatchAppListBusinessFromObj

    /**
     * This method is used to convert a list of {@link Object} to a list of {@link RunStatusEntity}. It is used for single batch job display on the batch app detail.
     *
     * @param list
     *        The list of entity object to convert.
     * @return The converted list.
     */
    public static List<RunStatusEntity> toRunStatusListBusinessFromObjForDetail(List<Object> list) {
        log.debug("Entering toRunStatusListBusinessFromObjForDetail");
        log.debug("This method is used to convert an list of Object to an list of RunStatusEntity. It is used for single batch job display on the batch app detail.");
        log.debug("Entry parameters are: list=" + String.valueOf(list));
        List<RunStatusEntity> modelList = new ArrayList<RunStatusEntity>();
        List<RunStatusEntity> finalList = new ArrayList<RunStatusEntity>();
        Map<Long, RunStatusEntity> collectionList = new TreeMap<Long, RunStatusEntity>();
        modelList = toRunStatusListBusiness(list);
        if(modelList != null && !modelList.isEmpty()){
            // consolidate the statuses into one job
            for(int i = 0, j = modelList.size();i < j;i++){
                if(!collectionList.containsKey(modelList.get(i).getRunNumber())){
                    // hold this record to compare against children
                    collectionList.put(modelList.get(i).getRunNumber(), modelList.get(i));
                }else{
                    collectionList.get(modelList.get(i).getRunNumber()).getCollectionMembers().add(modelList.get(i));
                }// end if-else
            }// end for
            Iterator<RunStatusEntity> it = collectionList.values().iterator();
            while(it.hasNext()){
                finalList.add(it.next());
            }// end while
        }// end if
        Collections.sort(finalList, Collections.reverseOrder(new RunStatusEntityComparator()));
        log.debug("Return value is: finalList=" + String.valueOf(finalList));
        log.debug("Exiting toRunStatusListBusinessFromObjForDetail");
        return finalList;
    }// end toRunStatusListBusinessFromObj

    /**
     * This method is used to convert a list of {@link Object} to a list of {@link RunStatusEntity}. It is used for single batch job display on the batch app detail.
     *
     * @param list
     *        The list of entity object to convert.
     * @return The converted list.
     */
    public static List<RunStatusEntity> toRunStatusListBusinessFromObjForCollectionDetail(List<Object> list) {
        log.debug("Entering toRunStatusListBusinessFromObjForCollectionDetail");
        log.debug("This method is used to convert an list of Object to an list of RunStatusEntity. It is used for single batch job display on the batch app detail.");
        log.debug("Entry parameters are: list=" + String.valueOf(list));
        List<RunStatusEntity> modelList = new ArrayList<RunStatusEntity>();
        List<RunStatusEntity> finalList = new ArrayList<RunStatusEntity>();
        Map<Long, RunStatusEntity> collectionList = new TreeMap<Long, RunStatusEntity>();
        modelList = toRunStatusListBusiness(list);
        if(modelList != null && !modelList.isEmpty()){
            // consolidate the collections into one job
            Long holdRunNbr = 0L;
            for(int i = 0, j = modelList.size();i < j;i++){
                if(!modelList.get(i).getRunNumber().equals(holdRunNbr)){
                    holdRunNbr = modelList.get(i).getRunNumber();
                    // hold this record to compare against children
                    collectionList.put(modelList.get(i).getRunNumber(), modelList.get(i));

                    finalList.add(modelList.get(i));
                }else{
                    // loop through and add this collection member to the right parent collection
                    for(int h = 0, k = finalList.size();h < k;h++){
                        if(finalList.get(h).getRunNumber().equals(modelList.get(i).getRunNumber())){
                            // only show this child record if its parent collection's startTs is less than its own
                            RunStatusEntity runStatusModel = collectionList.get(modelList.get(i).getRunNumber());
                            Long runNbr = (runStatusModel != null ? runStatusModel.getRunNumber() : null);
                            if(runNbr.equals(modelList.get(i).getRunNumber())){
                                finalList.get(h).getCollectionMembers().add(modelList.get(i));
                            }// end if
                        }// end if
                    }// end for
                }// end if-else
            }// end for
        }// end if
         // Once the collections are split, properly order their children. This was done separately because the logic just gets too hairy. This was a complicated sort
         // specifically for the run statuses on the batch collection detail screen.
        for(int i = 0, j = finalList.size();i < j;i++){
            RunStatusEntity col = finalList.get(i);
            RunStatusEntity mbr;
            Long holdBatchAppRefId = 0L;
            ArrayList<RunStatusEntity> sortList = new ArrayList<RunStatusEntity>();
            ArrayList<RunStatusEntity> sortedList = new ArrayList<RunStatusEntity>();
            int holdIndex = 0;
            for(int k = 0, l = col.getCollectionMembers().size();k < l;k++){
                mbr = col.getCollectionMembers().get(k);
                if(!holdBatchAppRefId.equals(mbr.getBatchApp().getBatchAppRefId())){
                    holdBatchAppRefId = mbr.getBatchApp().getBatchAppRefId();
                    // if something was there, then load to output list.
                    if(!sortList.isEmpty()){
                        // add the done first.
                        if(JmsRunStatus.STATUS_DONE.equals(sortList.get(holdIndex - 1).getStatus().getStatusCd())){
                            RunStatusEntity doneRun = null;
                            try{
                                RunStatusEntity doneRunFirst = (RunStatusEntity) sortList.get(holdIndex - 1).clone();
                                doneRunFirst.setBatchApp((BatchAppEntity) doneRunFirst.getBatchApp().clone());
                                // doneRunFirst.getBatchApp().setName(new String(doneRunFirst.getBatchApp().getName().substring(2)));

                                sortedList.add(doneRunFirst);
                                doneRun = (RunStatusEntity) sortList.get(holdIndex - 1);
                                doneRun.setStartTime(doneRun.getStopTime());
                                doneRun.setStopTime(null);
                                // doneRun.setDuration(null);
                                // doneRun.setDurationInMinutes("");
                            }catch(Exception e){
                                log.warn("Unable to clone the runstatus.", e);
                            }// end try/catch
                        }// end if
                        holdIndex = 0;
                        // add the rest sorted ascending
                        sortedList.addAll(sortList);
                        sortList.clear();
                    }// end if
                }// end if
                holdIndex = holdIndex + 1;
                // Indent the children per JD
                // mbr.getBatchApp().setName("--" + mbr.getBatchApp().getName());
                sortList.add(mbr);
            }// end for
            if(!sortList.isEmpty()){
                // add the done first.
                if(JmsRunStatus.STATUS_DONE.equals(sortList.get(0).getResult().getResultCd())){
                    sortedList.add(sortList.get(0));
                }// end if
                sortedList.addAll(sortList);
            }// end if
             // if(sortedList.size() > 0){
             // finalList.set(i, sortedList.get(sortedList.size() - 1));
             // }
            finalList.get(i).setCollectionMembers(sortedList);
        }// end for

        Collections.sort(finalList, Collections.reverseOrder(new RunStatusEntityComparator()));
        log.debug("Return value is: finalList=" + String.valueOf(finalList));
        log.debug("Exiting toRunStatusListBusinessFromObjForCollectionDetail");
        return finalList;
    }// end toRunStatusListBusinessFromObj
}// end class
