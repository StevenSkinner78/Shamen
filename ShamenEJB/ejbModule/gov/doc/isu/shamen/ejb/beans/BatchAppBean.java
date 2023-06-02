/**
 *
 */
package gov.doc.isu.shamen.ejb.beans;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import gov.doc.isu.shamen.ejb.beans.view.BatchAppBeanLocal;
import gov.doc.isu.shamen.ejb.convertor.EJBObjectConvertor;
import gov.doc.isu.shamen.ejb.email.IEmailBeanLocal;
import gov.doc.isu.shamen.ejb.util.EJBConstants;
import gov.doc.isu.shamen.ejb.util.ShamenEJBUtil;
import gov.doc.isu.shamen.interfaces.Scheduleable;
import gov.doc.isu.shamen.jms.beans.view.JmsManagerBeanLocal;
import gov.doc.isu.shamen.jms.models.JmsSchedule;
import gov.doc.isu.shamen.jpa.entity.BatchAppEntity;
import gov.doc.isu.shamen.jpa.entity.ResultCodeEntity;
import gov.doc.isu.shamen.jpa.entity.RunStatusEntity;
import gov.doc.isu.shamen.jpa.entity.ScheduleEntity;

/**
 * This class is used to access the database via an entity manager to perform CRUD operations with the batch app table.
 *
 * @author <strong>Steven Skinner</strong> JCCC
 */
@Stateless
@Local(BatchAppBeanLocal.class)
public class BatchAppBean implements BatchAppBeanLocal {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Logger log = Logger.getLogger("gov.doc.isu.shamen.ejb.beans.BatchAppBean");
    @PersistenceContext(unitName = "ShamenJPA")
    private EntityManager em;
    @EJB(beanInterface = IEmailBeanLocal.class)
    private IEmailBeanLocal email;
    @EJB
    private JmsManagerBeanLocal jmsBean;

    /**
     * This default constructor must be used to instantiate this class.
     */
    public BatchAppBean() {
        super();
    }// end constructor

    /**
     * This method is called after the bean has been constructed.
     */
    @PostConstruct
    private void initialize() {
        log.debug("Entering initialize. This method is called after the bean has been constructed.");
        if(em == null){
            log.error("Entity manager has not been created in the post construct method. Check the server logs for deployment errors.");
        }// end if
        log.debug("Exiting initialize.");
    }// end initialize

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<BatchAppEntity> getBatchList() throws Exception {
        log.debug("Entering getBatchList. This method is used to return a list of objects filled with batch app info.");
        StringBuffer query = new StringBuffer();
        query.append("with sel as (select assoc_batch_app_ref_id from trans.Batch_Apps_Collection_Xref col where col.Delete_Ind = 'N' group by Assoc_Batch_App_Ref_Id)");
        query.append(" SELECT b.Batch_App_Ref_Id,c.Controller_Nm, b.Batch_Nm, s.Schedule_Start_Dt, s.Start_Time, a.Application_Nm, a.Application_Addr,bt.Batch_Type_Desc, s.Active_Ind,cc.Controller_Status_Desc,f.frequency_desc,sy.System_Nm,c.controller_ref_id,c.controller_status_cd,s.Frequency_Cd,s.Recur_No,s.Repeat_Cd,s.Week_No,s.Weekdays,s.Day_No,s.Frequency_Type_Cd, b.System_Ref_Id,CASE WHEN sel.Assoc_Batch_App_Ref_Id is null then 'N' else 'Y' end as partOfCollection,s.schedule_ref_id,us.User_First_Nm,us.User_Last_Nm,s.create_ts,(Select COUNT(*) FROM (SELECT COUNT(Run_Nbr) as RECORDS FROM trans.Run_Status r WHERE r.delete_ind = 'N' and r.Batch_App_Ref_Id = b.Batch_App_Ref_Id and r.Main_Batch_App_Ref_Id is null GROUP BY Run_Nbr) as number) as executionCount");
        query.append(" FROM Trans.Batch_Apps b left join trans.Schedules s on s.Batch_App_Ref_Id = b.Batch_App_Ref_Id and s.Delete_Ind = 'N' left join trans.Controllers c on c.Controller_Ref_Id = b.Controller_Ref_Id and c.Delete_Ind = 'N' left join trans.Applications a on a.Application_Ref_Id = b.Application_Ref_Id and a.Delete_Ind = 'N' left join code.Batch_Type_Codes bt on bt.Batch_Type_Cd = b.Batch_Type_Cd left join code.Controller_Status_Codes cc on cc.Controller_Status_Cd = c.Controller_Status_Cd left join code.Frequency_Codes f on f.Frequency_Cd = s.Frequency_Cd left join trans.Systems sy on sy.System_Ref_Id = b.System_Ref_Id and sy.Delete_Ind = 'N' left join trans.Authorized_Users us on us.User_Ref_Id = b.Responsible_Staff_User_Ref_Id and us.delete_ind = 'N' left join sel on sel.Assoc_Batch_App_Ref_Id = b.Batch_App_Ref_Id");
        query.append(" WHERE bt.batch_type_cd != 'COL' and b.Delete_Ind = 'N' order by b.Batch_Nm");
        List<Object> list = em.createNativeQuery(query.toString()).getResultList();
        List<BatchAppEntity> batchList = EJBObjectConvertor.toBatchAppListEntityFromObj(list);
        batchList = checkOnSchedule(batchList);
        log.debug("Return value is: list.size=" + (ShamenEJBUtil.isEmpty(list) ? "0" : list.size()));
        log.debug("Exiting getBatchList");
        return batchList;
    }// end getBatchList

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<BatchAppEntity> getBatchList(String applicationName, String applicationEnvironment) throws Exception {
        log.debug("Entering getBatchList. This method is used to return a list of objects filled with batch app info.");
        StringBuffer query = new StringBuffer();
        query.append("with sel as (select assoc_batch_app_ref_id from trans.Batch_Apps_Collection_Xref col where col.Delete_Ind = 'N' group by Assoc_Batch_App_Ref_Id)");
        query.append(" SELECT b.Batch_App_Ref_Id,c.Controller_Nm, b.Batch_Nm, s.Schedule_Start_Dt, s.Start_Time, a.Application_Nm, a.Application_Addr,bt.Batch_Type_Desc, s.Active_Ind,cc.Controller_Status_Desc,f.frequency_desc,sy.System_Nm,c.controller_ref_id,c.controller_status_cd,s.Frequency_Cd,s.Recur_No,s.Repeat_Cd,s.Week_No,s.Weekdays,s.Day_No,s.Frequency_Type_Cd, b.System_Ref_Id,CASE WHEN sel.Assoc_Batch_App_Ref_Id is null then 'N' else 'Y' end as partOfCollection,s.schedule_ref_id,us.User_First_Nm,us.User_Last_Nm,s.create_ts,0");
        query.append(" FROM Trans.Batch_Apps b left join trans.Schedules s on s.Batch_App_Ref_Id = b.Batch_App_Ref_Id and s.Delete_Ind = 'N'  left join trans.Controllers c on c.Controller_Ref_Id = b.Controller_Ref_Id and c.Delete_Ind = 'N' left join trans.Applications a on a.Application_Ref_Id = b.Application_Ref_Id and a.Delete_Ind = 'N' left join code.Batch_Type_Codes bt on bt.Batch_Type_Cd = b.Batch_Type_Cd left join code.Controller_Status_Codes cc on cc.Controller_Status_Cd = c.Controller_Status_Cd left join code.Frequency_Codes f on f.Frequency_Cd = s.Frequency_Cd left join trans.Systems sy on sy.System_Ref_Id = b.System_Ref_Id and sy.Delete_Ind = 'N' left join trans.Authorized_Users us on us.User_Ref_Id = b.Responsible_Staff_User_Ref_Id and us.delete_ind = 'N' left join sel on sel.Assoc_Batch_App_Ref_Id = b.Batch_App_Ref_Id");
        query.append(" WHERE bt.batch_type_cd != 'COL' and b.Delete_Ind = 'N' and a.Application_Nm = ? and a.Application_Addr = ?  order by b.Batch_Nm");
        List<Object> list = em.createNativeQuery(query.toString()).setParameter(1, applicationName).setParameter(2, applicationEnvironment).getResultList();
        List<BatchAppEntity> batchList = EJBObjectConvertor.toBatchAppListEntityFromObj(list);
        batchList = checkOnSchedule(batchList);
        log.debug("Return value is: list.size=" + (ShamenEJBUtil.isEmpty(list) ? "0" : list.size()));
        log.debug("Exiting getBatchList");
        return batchList;
    }// end getBatchList

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<BatchAppEntity> getAllBatchesAndCollectionsForApplication(Long applicationRefId) throws Exception {
        log.debug("Entering getAllBatchesAndCollectionsForApplication. This method is used to return a list of objects filled with batch app info.");
        StringBuffer query = new StringBuffer();
        query.append("with sel as (select assoc_batch_app_ref_id from trans.Batch_Apps_Collection_Xref col where col.Delete_Ind = 'N' group by Assoc_Batch_App_Ref_Id)");
        query.append(" SELECT b.Batch_App_Ref_Id,c.Controller_Nm, b.Batch_Nm, s.Schedule_Start_Dt, s.Start_Time, a.Application_Nm, a.Application_Addr,bt.Batch_Type_Desc, s.Active_Ind,cc.Controller_Status_Desc,f.frequency_desc,sy.System_Nm,c.controller_ref_id,c.controller_status_cd,s.Frequency_Cd,s.Recur_No,s.Repeat_Cd,s.Week_No,s.Weekdays,s.Day_No,s.Frequency_Type_Cd, b.System_Ref_Id,CASE WHEN sel.Assoc_Batch_App_Ref_Id is null then 'N' else 'Y' end as partOfCollection,s.schedule_ref_id,us.User_First_Nm,us.User_Last_Nm,s.create_ts,(Select COUNT(*) FROM (SELECT COUNT(Run_Nbr) as RECORDS FROM trans.Run_Status r WHERE r.delete_ind = 'N' and r.Batch_App_Ref_Id = b.Batch_App_Ref_Id and r.Main_Batch_App_Ref_Id is null GROUP BY Run_Nbr) as number) as executionCount");
        query.append(" FROM Trans.Batch_Apps b left join trans.Schedules s on s.Batch_App_Ref_Id = b.Batch_App_Ref_Id and s.Delete_Ind = 'N'  left join trans.Controllers c on c.Controller_Ref_Id = b.Controller_Ref_Id and c.Delete_Ind = 'N' left join trans.Applications a on a.Application_Ref_Id = b.Application_Ref_Id and a.Delete_Ind = 'N' left join code.Batch_Type_Codes bt on bt.Batch_Type_Cd = b.Batch_Type_Cd left join code.Controller_Status_Codes cc on cc.Controller_Status_Cd = c.Controller_Status_Cd left join code.Frequency_Codes f on f.Frequency_Cd = s.Frequency_Cd left join trans.Systems sy on sy.System_Ref_Id = b.System_Ref_Id and sy.Delete_Ind = 'N' left join trans.Authorized_Users us on us.User_Ref_Id = b.Responsible_Staff_User_Ref_Id and us.delete_ind = 'N' left join sel on sel.Assoc_Batch_App_Ref_Id = b.Batch_App_Ref_Id");
        query.append(" WHERE sel.Assoc_Batch_App_Ref_Id is null and b.Delete_Ind = 'N' and a.Application_ref_id = ?  order by b.Batch_Nm");
        List<Object> list = em.createNativeQuery(query.toString()).setParameter(1, applicationRefId).getResultList();
        List<BatchAppEntity> batchList = EJBObjectConvertor.toBatchAppListEntityFromObj(list);
        batchList = checkOnSchedule(batchList);
        log.debug("Return value is: list.size=" + (ShamenEJBUtil.isEmpty(list) ? "0" : list.size()));
        log.debug("Exiting getAllBatchesAndCollectionsForApplication");
        return batchList;
    }// end getAllBatchesAndCollectionsForApplication

    /**
     * This method performs all the calculations and comparisons to determine if a job is on schedule. If so, it also determines its status.
     *
     * @param batchList
     *        (required)
     * @return List<BatchAppEntity>
     */
    public List<BatchAppEntity> checkOnSchedule(List<BatchAppEntity> batchList) {
        log.debug("Entering checkOnSchedule().");
        log.debug("This method performs all the calculations and comparisons to determine if a job is on schedule. If so, it also determines its status.");
        BatchAppEntity bm = null;
        String result = null;
        // Get all the last run statuses to be used in the checking.
        List<RunStatusEntity> runStatuses;
        try{
            runStatuses = EJBObjectConvertor.toRunStatusEntityFromObject(getRunStatusListFromSchedule());

            for(int i = 0, j = batchList.size();i < j;i++){
                bm = batchList.get(i);
                if(ShamenEJBUtil.isEmpty(bm.getSchedule())){
                    result = EJBConstants.BATCH_STATUS_NO_SCHEDULE;
                }else{
                    for(int k = 0, l = bm.getSchedule().size();k < l;k++){
                        if(!"Y".equals(bm.getSchedule().get(k).getActiveInd())){
                            result = EJBConstants.BATCH_STATUS_INACTIVE;
                        }else if(JmsSchedule.FREQUENCY_ONE_TIME.equals(bm.getSchedule().get(k).getFrequency().getFrequencyCd())){
                            result = checkOneTimeRun(bm.getSchedule().get(k), filterRunStatusesByBatchApp(runStatuses, bm.getBatchAppRefId(), bm.getSchedule().get(k).getScheduleRefId()));
                        }else if(JmsSchedule.FREQUENCY_DAILY.equals(bm.getSchedule().get(k).getFrequency().getFrequencyCd())){
                            result = checkDailyRun(bm.getSchedule().get(k), filterRunStatusesByBatchApp(runStatuses, bm.getBatchAppRefId(), bm.getSchedule().get(k).getScheduleRefId()));
                        }else if(JmsSchedule.FREQUENCY_WEEKLY.equals(bm.getSchedule().get(k).getFrequency().getFrequencyCd())){
                            result = checkWeeklyRun(bm.getSchedule().get(k), filterRunStatusesByBatchApp(runStatuses, bm.getBatchAppRefId(), bm.getSchedule().get(k).getScheduleRefId()));
                        }else if(JmsSchedule.FREQUENCY_MONTHLY.equals(bm.getSchedule().get(k).getFrequency().getFrequencyCd())){
                            result = checkMonthlyRun(bm.getSchedule().get(k), filterRunStatusesByBatchApp(runStatuses, bm.getBatchAppRefId(), bm.getSchedule().get(k).getScheduleRefId()));
                        }// end if
                         // null check for if there are no runstatus records
                        if(null == bm.getSchedule().get(k).getLastRunStatus()){
                            bm.getSchedule().get(k).setLastRunStatus(new RunStatusEntity());
                            bm.getSchedule().get(k).getLastRunStatus().setResult(new ResultCodeEntity());
                        }// end if
                        bm.getSchedule().get(k).getLastRunStatus().getResult().setResultCd(result);
                    }// end for
                }// end if/else
            }// end for
        }catch(Exception e){
            log.error("Exception occurred trying to get the run status records for the batch list.  Exception is: " + e);
        }// end try/catch
        log.debug("Exiting checkOnSchedule");
        return batchList;
    }// end checkOnSchedule

    /**
     * This method performs all the calculations and comparisons to determine if a job is on schedule. If so, it also determines its status.
     *
     * @param batchList
     *        (required)
     * @return List<BatchAppEntity>
     */
    public List<ScheduleEntity> checkOnScheduleForBatchDetail(List<ScheduleEntity> scheduleList, Long batchAppRefId) {
        log.debug("Entering checkOnScheduleForBatchDetail()");
        log.debug("This method performs all the calculations and comparisons to determine if a job is on schedule. If so, it also determines its status.");
        String result = null;
        // Get all the last run statuses to be used in the checking.
        List<RunStatusEntity> runStatuses;
        try{
            runStatuses = EJBObjectConvertor.toRunStatusEntityFromObject(getRunStatusListFromSchedule());
            for(int k = 0, l = scheduleList.size();k < l;k++){
                if(!"Y".equals(scheduleList.get(k).getActiveInd())){
                    result = EJBConstants.BATCH_STATUS_INACTIVE;
                }else if(JmsSchedule.FREQUENCY_ONE_TIME.equals(scheduleList.get(k).getFrequency().getFrequencyCd())){
                    result = checkOneTimeRun(scheduleList.get(k), filterRunStatusesByBatchApp(runStatuses, batchAppRefId, scheduleList.get(k).getScheduleRefId()));
                }else if(JmsSchedule.FREQUENCY_DAILY.equals(scheduleList.get(k).getFrequency().getFrequencyCd())){
                    result = checkDailyRun(scheduleList.get(k), filterRunStatusesByBatchApp(runStatuses, batchAppRefId, scheduleList.get(k).getScheduleRefId()));
                }else if(JmsSchedule.FREQUENCY_WEEKLY.equals(scheduleList.get(k).getFrequency().getFrequencyCd())){
                    result = checkWeeklyRun(scheduleList.get(k), filterRunStatusesByBatchApp(runStatuses, batchAppRefId, scheduleList.get(k).getScheduleRefId()));
                }else if(JmsSchedule.FREQUENCY_MONTHLY.equals(scheduleList.get(k).getFrequency().getFrequencyCd())){
                    result = checkMonthlyRun(scheduleList.get(k), filterRunStatusesByBatchApp(runStatuses, batchAppRefId, scheduleList.get(k).getScheduleRefId()));
                }// end if
                 // null check for if there are no runstatus records
                if(null == scheduleList.get(k).getLastRunStatus()){
                    scheduleList.get(k).setLastRunStatus(new RunStatusEntity());
                    scheduleList.get(k).getLastRunStatus().setResult(new ResultCodeEntity());
                }// end if
                scheduleList.get(k).getLastRunStatus().getResult().setResultCd(result);
            }// end for
        }catch(Exception e){
            log.error("Exception occurred trying to get the run status records for the batch list.  Exception is: " + e);
        }// end try/catch
        log.debug("Exiting BatchAppInfo.checkOnSchedule");
        return scheduleList;
    }// end checkOnSchedule

    /**
     * This method filters down a runStatus list to only get the ones that are for a specific job. This is done as an alternative to going unnecessarily to the DB each time.
     *
     * @param runStatuses
     *        (required)
     * @param batchAppRefId
     *        (required)
     * @param scheduleRefId
     *        (required)
     * @return List<RunStatusEntity>
     */
    private List<RunStatusEntity> filterRunStatusesByBatchApp(List<RunStatusEntity> runStatuses, Long batchAppRefId, Long scheduleRefId) {
        log.debug("Entering BatchAppInfo.filterRunStatusesByBatchApp().");
        log.debug("This method filters down a runStatus list to only get the ones that are for a specific job.  This is done as an alternative to going unnecessarily to the DB each time.");
        ArrayList<RunStatusEntity> filteredList = new ArrayList<RunStatusEntity>();
        for(int i = 0, j = runStatuses.size();i < j;i++){
            if(runStatuses.get(i).getBatchApp() != null){
                if(batchAppRefId.equals(runStatuses.get(i).getBatchApp().getBatchAppRefId()) && scheduleRefId.equals(runStatuses.get(i).getSchedule().getScheduleRefId())){
                    filteredList.add(runStatuses.get(i));
                }// end if
            }// end if
        }// end for
        runStatuses = null;
        log.debug("Return value is: list.size=" + (ShamenEJBUtil.isEmpty(filteredList) ? "0" : filteredList.size()));
        log.debug("Exiting filterRunStatusesByBatchApp");
        return filteredList;
    }// end filterRunStatusesByBatchApp

    /**
     * This method performs calculations to determine when the job should have been run for the ONE TIME ONLY mode and returns the appropriate schedule status.
     *
     * @param schedule
     *        (required)
     * @param runStatuses
     *        (required)
     * @return String
     */
    private String checkOneTimeRun(ScheduleEntity schedule, List<RunStatusEntity> runStatuses) {
        log.debug("Entering BatchAppInfo.checkOneTimeRun().");
        log.debug("This method performs calculations to determine when the job should have been run for the ONE TIME ONLY mode and returns the appropriate schedule status.");
        String status = EJBConstants.BATCH_STATUS_UNKNOWN;
        Timestamp ts = null;
        // Load the run time
        ts = ShamenEJBUtil.getSqlTSFromDateTimeString(schedule.getScheduleStartDt(), schedule.getStartTime().toString(), EJBConstants.SDFT);
        if(ts.after(new Timestamp(System.currentTimeMillis()))){
            // For one time runs, if the timestamp is greater than current, then it is pending a run
            status = EJBConstants.BATCH_STATUS_PENDING;
        }else{
            // Check the repeat value and handle accordingly.
            ts = handleRepeats(schedule, ts);
            status = determineScheduleStatus(ts, runStatuses);
        }// end if
        log.debug("Return value is: status=" + status);
        log.debug("Exiting BatchAppInfo.checkOneTimeRun().");
        return status;
    }// end checkOneTimeRun

    /**
     * This method performs calculations to determine when the job should have been run for the DAILY mode and returns the appropriate schedule status.
     *
     * @param schedule
     *        (required)
     * @param runStatuses
     *        (required)
     * @return String
     */
    private String checkDailyRun(ScheduleEntity schedule, List<RunStatusEntity> runStatuses) {
        log.debug("Entering BatchAppInfo.checkDailyRun().");
        log.debug("This method performs calculations to determine when the job should have been run for the DAILY mode and returns the appropriate schedule status.");
        String status = EJBConstants.BATCH_STATUS_UNKNOWN;
        Timestamp ts = null;

        // Load the run time
        ts = ShamenEJBUtil.getSqlTSFromDateTimeString(schedule.getScheduleStartDt(), schedule.getStartTime().toString(), EJBConstants.SDFT);
        if(ts.after(new Timestamp(System.currentTimeMillis()))){
            // For one time runs, if the timestamp is greater than current, then it is pending a run
            status = EJBConstants.BATCH_STATUS_PENDING;
        }else{
            // Check the recur and handle next run.
            ts = handleRecurs(schedule, ts);
            ts = handleRepeats(schedule, ts);
            status = determineScheduleStatus(ts, runStatuses);
        }// end if
        log.debug("Return value is: status=" + status);
        log.debug("Exiting BatchAppInfo.checkDailyRun().");
        return status;
    }// end checkDailyRun

    /**
     * This method performs calculations to determine when the job should have been run for the WEEKLY mode and returns the appropriate schedule status.
     *
     * @param schedule
     *        (required)
     * @param runStatuses
     *        (required)
     * @return String
     */
    private String checkWeeklyRun(ScheduleEntity schedule, List<RunStatusEntity> runStatuses) {
        log.debug("Entering BatchAppInfo.checkWeeklyRun().");
        log.debug("This method performs calculations to determine when the job should have been run for the WEEKLY mode and returns the appropriate schedule status.");
        String status = EJBConstants.BATCH_STATUS_UNKNOWN;
        Timestamp ts = null;

        // Load the run time
        ts = ShamenEJBUtil.getSqlTSFromDateTimeString(schedule.getScheduleStartDt(), schedule.getStartTime().toString(), EJBConstants.SDFT);
        if(ts.after(new Timestamp(System.currentTimeMillis()))){
            // For one time runs, if the timestamp is greater than current, then it is pending a run
            status = EJBConstants.BATCH_STATUS_PENDING;
        }else{
            ts = ShamenEJBUtil.getSqlTSFromDateTimeString(determineWeekDayToUse(schedule), schedule.getStartTime().toString(), EJBConstants.SDFT);
            if(ts.after(new Timestamp(System.currentTimeMillis()))){
                // For one time runs, if the timestamp is greater than current, then it is pending a run
                status = EJBConstants.BATCH_STATUS_PENDING;
            }else{
                status = determineScheduleStatus(ts, runStatuses);
            }// end if
        }// end if
        log.debug("Return value is: status=" + status);
        log.debug("Exiting BatchAppInfo.checkWeeklyRun().");
        return status;
    }// end checkWeeklyRun

    /**
     * This method performs calculations to determine when the job should have been run for the MONTHLY mode and returns the appropriate schedule status.
     *
     * @param schedule
     *        (required)
     * @param runStatuses
     *        (required)
     * @return String
     */
    private String checkMonthlyRun(ScheduleEntity schedule, List<RunStatusEntity> runStatuses) {
        log.debug("Entering BatchAppInfo.checkMonthlyRun().");
        log.debug("This method performs calculations to determine when the job should have been run for the MONTHLY mode and returns the appropriate schedule status.");
        String status = EJBConstants.BATCH_STATUS_UNKNOWN;
        Timestamp ts = null;
        // Only perform fancy checks if the schedule is already active.
        ts = ShamenEJBUtil.getSqlTSFromDateTimeString(schedule.getScheduleStartDt(), schedule.getStartTime().toString(), EJBConstants.SDFT);
        if(ts.after(new Timestamp(System.currentTimeMillis()))){
            // For one time runs, if the timestamp is greater than current, then it is pending a run
            status = EJBConstants.BATCH_STATUS_PENDING;
        }else{
            // There are two monthly types, one is specific days of the month, the other is week number and week day based.
            if(JmsSchedule.FREQUENCY_TYPE_DAY_OF_MONTH.equals(schedule.getFrequencyType().getFrequencyTypeCd())){
                ts = ShamenEJBUtil.getSqlTSFromDateTimeString(determineMonthDayToUse(schedule), schedule.getStartTime().toString(), EJBConstants.SDFT);
            }else{
                ts = ShamenEJBUtil.getSqlTSFromDateTimeString(determineMonthWithWeekDayToUse(schedule), schedule.getStartTime().toString(), EJBConstants.SDFT);
            }// end if
            if(ts.after(new Timestamp(System.currentTimeMillis()))){
                // For one time runs, if the timestamp is greater than current, then it is pending a run
                status = EJBConstants.BATCH_STATUS_PENDING;
            }else{
                status = determineScheduleStatus(ts, runStatuses);
            }// end if
        }// end if
        log.debug("Return value is: status=" + status);
        log.debug("Exiting BatchAppInfo.checkMonthlyRun().");
        return status;
    }// end checkWeeklyRun

    /**
     * This method uses a job's schedule to determine which month day to check for a batch run. This is for the MONTHLY style schedule.
     *
     * @param schedule
     *        for job
     * @return date string
     */
    private String determineMonthDayToUse(ScheduleEntity schedule) {
        log.debug("Entering BatchAppInfo.determineMonthDayToUse().");
        log.debug("This method uses a job's schedule to determine which month day to check for a batch run.  This is for the MONTHLY style schedule.");
        String returnDate = null;
        String[] monthDays = schedule.getDayNumberArray();
        Calendar cal = Calendar.getInstance();
        Boolean today = false;
        // determine if the day is the same
        Integer curDay = cal.get(Calendar.DAY_OF_MONTH);
        Integer lastDayOfMonth = cal.getMaximum(Calendar.DAY_OF_MONTH);
        // if today is one of the days in the month or if last day of the month was chosen(day = 32), then check to see if today is the last
        // day of the month
        for(int i = 0, j = monthDays.length;i < j;i++){
            if((monthDays[i] != null && Integer.valueOf(monthDays[i]) == curDay) || ((monthDays[i] != null && Integer.valueOf(monthDays[i]) == 32)) && curDay.equals(lastDayOfMonth)){
                today = true;
                break;
            }// end if
        }// end for
         // if today is one of the days, then compare times. If job time is less than the current time, then use it. Otherwise, find day before.
        if(today){
            if(ShamenEJBUtil.getSqlTSFromDateTimeString(ShamenEJBUtil.getDate(), schedule.getStartTime().toString(), EJBConstants.SDFT).before(new Timestamp(System.currentTimeMillis()))){
                returnDate = ShamenEJBUtil.getDate();
            }else{
                // time is less, so previous day must be used
                returnDate = determineBestPreviousMonthDay(monthDays);
            }// end if/else
        }else{
            // day is not today so previous day must be used.
            returnDate = determineBestPreviousMonthDay(monthDays);
        }// end if/else
        log.debug("Return value is: date=" + String.valueOf(returnDate));
        log.debug("Exiting BatchAppInfo.determineMonthDayToUse().");
        return returnDate;
    }// end determineMonthDayToUse

    /**
     * This method uses a job's schedule to determine which week day to check for a batch run. This is for the MONTHLY style schedule.
     *
     * @param schedule
     *        for job
     * @return date string
     */
    private String determineMonthWithWeekDayToUse(ScheduleEntity schedule) {
        log.debug("Entering BatchAppInfo.determineMonthWithWeekDayToUse().");
        log.debug("This method uses a job's schedule to determine which week day to check for a batch run.  This is for the Monthly style schedule.");
        String returnDate = null;
        String[] weekNo = schedule.getWeekNumberArray();
        String[] weekdays = schedule.getWeekDaysArray();
        Calendar cal = Calendar.getInstance();
        Boolean today = false;
        // determine if the day is the same
        Integer curWeek = cal.get(Calendar.DAY_OF_WEEK_IN_MONTH);
        Integer lastWeekOfMonth = cal.getActualMaximum(Calendar.DAY_OF_WEEK_IN_MONTH);
        Integer curDay = cal.get(Calendar.DAY_OF_WEEK);
        // if today is in one of the weeks in the month or if last week of the month was chosen(week = 5), then check to see if today is the last
        // day of the month
        for(int i = 0, j = weekNo.length;i < j;i++){
            if((weekNo[i] != null && Integer.valueOf(weekNo[i]) == curWeek) || ((weekNo[i] != null && Integer.valueOf(weekNo[i]) == 5)) && curWeek.equals(lastWeekOfMonth)){

                for(int i2 = 0, j2 = weekdays.length;i2 < j2;i2++){
                    if(weekdays[i2] != null && Integer.valueOf(weekdays[i2]) == curDay){
                        log.debug("Current day is in the weekday list.");
                        today = true;
                    }// end if
                }// end for
            }// end if
        }// end for

        // if today is one of the days, then compare times. If job time is less than the current time, then use it. Otherwise, find day before.
        if(today){
            if(ShamenEJBUtil.getSqlTSFromDateTimeString(ShamenEJBUtil.getDate(), schedule.getStartTime().toString(), EJBConstants.SDFT).before(new Timestamp(System.currentTimeMillis()))){
                log.debug("Use today as the day to check the schedule.");
                returnDate = ShamenEJBUtil.getDate();
            }else{
                // time is less, so previous day must be used
                log.debug("Use a previous day as the day to check the schedule.");
                returnDate = determineBestPreviousWeekDay(weekdays);
            }// end if-else
        }else{
            log.debug("Use a previous day as the day to check the schedule.");
            returnDate = determineBestPreviousWeekDay(weekdays);
        }// end if-else
        log.debug("Return value is: date=" + String.valueOf(returnDate));
        log.debug("Exiting BatchAppInfo.determineMonthWithWeekDayToUse().");
        return returnDate;
    }// end determineMonthWithWeekDayToUse

    /**
     * This method uses a job's schedule to determine which week day to check for a batch run. This is for the WEEKLY style schedule.
     *
     * @param schedule
     *        for job
     * @return date string
     */
    private String determineWeekDayToUse(ScheduleEntity schedule) {
        log.debug("Entering BatchAppInfo.determineWeekDayToUse().");
        log.debug("This method uses a job's schedule to determine which week day to check for a batch run.  This is for the WEEKLY style schedule.");
        String returnDate = null;
        String[] weekdays = schedule.getWeekDaysArray();
        Calendar cal = Calendar.getInstance();
        Boolean today = false;
        // determine if the day is the same
        Integer curDay = cal.get(Calendar.DAY_OF_WEEK);
        for(int i = 0, j = weekdays.length;i < j;i++){
            if(weekdays[i] != null && Integer.valueOf(weekdays[i]) == curDay){
                log.debug("Current day is in the weekday list.");
                today = true;
            }// end if
        }// end for
         // if today is one of the days, then compare times. If job time is less than the current time, then use it. Otherwise, find day before.
        if(today){
            if(ShamenEJBUtil.getSqlTSFromDateTimeString(ShamenEJBUtil.getDate(), schedule.getStartTime().toString(), EJBConstants.SDFT).before(new Timestamp(System.currentTimeMillis()))){
                log.debug("Use today as the day to check the schedule.");
                returnDate = ShamenEJBUtil.getDate();
            }else{
                log.debug("Use a previous weekday as the day to check the schedule.");
                returnDate = determineBestPreviousWeekDay(weekdays);
            }// end if/else
        }else{
            log.debug("Use a previous weekday as the day to check the schedule.");
            returnDate = determineBestPreviousWeekDay(weekdays);
        }// end if/else
        log.debug("Return value is: date=" + String.valueOf(returnDate));
        log.debug("Exiting BatchAppInfo.determineWeekDayToUse().");
        return returnDate;
    }// end determineWeekDayToUse

    /**
     * This method will use an array of month days to find the one previous to today.
     *
     * @param monthDays
     *        array of monthDays
     * @return stringDate
     */
    private String determineBestPreviousMonthDay(String[] monthDays) {
        log.debug("Entering BatchAppInfo.determineBestPreviousMonthDay() with monthDays: " + String.valueOf(monthDays));
        log.debug("This method will use an array of month days to find the one previous to today.");
        String returnDate = ShamenEJBUtil.getDate();
        Calendar cal = Calendar.getInstance();
        ArrayList<Integer> dayList = new ArrayList<Integer>();
        // load into arrayList for easier processing
        for(int i = 0, j = monthDays.length;i < j;i++){
            if(monthDays[i] != null){
                dayList.add(Integer.valueOf(monthDays[i]));
            }// end if
        }// end for

        // subtract a day from the current date until one matches
        if(dayList != null && !dayList.isEmpty()){
            while(true){
                cal.add(Calendar.DAY_OF_MONTH, -1);
                Integer lastDayOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                if(dayList.contains(cal.get(Calendar.DAY_OF_MONTH)) || (dayList.contains(32) && cal.get(Calendar.DAY_OF_MONTH) == lastDayOfMonth)){
                    try{
                        returnDate = ShamenEJBUtil.getDateAsString(new java.util.Date(ShamenEJBUtil.clearCalendarTimeFields(cal)));
                    }catch(ParseException e){
                        log.error("An exception occurred while converting date. Date is: " + new java.util.Date(cal.getTimeInMillis()) + "Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
                        // throw new Exception(e.getMessage(), e);
                    }// end try-catch
                    break;
                }// end if
            }// end while
        }// end if
        log.debug("Return value is: date=" + String.valueOf(returnDate));
        log.debug("Exiting BatchAppInfo.determineBestPreviousMonthDay().");
        return returnDate;
    }// end determineBestPreviousMonthDay

    /**
     * This method will use an array of weekdays to find the one previous to today.
     *
     * @param weekdays
     *        array of weekdays
     * @return stringDate
     */
    private String determineBestPreviousWeekDay(String[] weekdays) {
        log.debug("Entering BatchAppInfo.determineBestPreviousMonthDay() with weekdays: " + String.valueOf(weekdays));
        log.debug("This method will use an array of weekdays to find the one previous to today.");
        String returnDate = ShamenEJBUtil.getDate();
        Calendar cal = Calendar.getInstance();
        ArrayList<Integer> dayList = new ArrayList<Integer>();
        // load into arrayList for easier processing
        for(int i = 0, j = weekdays.length;i < j;i++){
            if(weekdays[i] != null){
                dayList.add(Integer.valueOf(weekdays[i]));
            }// end if
        }// end for

        // subtract a day from the current date until one matches
        if(dayList != null && !dayList.isEmpty()){
            while(true){
                cal.add(Calendar.DAY_OF_WEEK, -1);
                if(dayList.contains(cal.get(Calendar.DAY_OF_WEEK))){
                    try{
                        returnDate = ShamenEJBUtil.getDateAsString(new java.util.Date(ShamenEJBUtil.clearCalendarTimeFields(cal)));
                    }catch(ParseException e){
                        log.error("An exception occurred while converting date. Date is: " + new java.util.Date(cal.getTimeInMillis()) + "Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
                        // throw new Exception(e.getMessage(), e);
                    }// end try-catch
                    break;
                }// end if
            }// end while
        }// end if
        log.debug("Return value is: date=" + String.valueOf(returnDate));
        log.debug("Exiting BatchAppInfo.determineBestPreviousWeekDay().");
        return returnDate;
    }// end determineBestPreviousWeekDay

    /**
     * This method takes the last run status and the time a job should have run and compares them to determine what the schedule status should be. Possible statuses are:
     * <ul>
     * <li>OFS = Off Schedule</li>
     * <li>PND = Pending(for one time only)</li>
     * <li>SUC = Successful</li>
     * <li>UNS = Unsuccessful</li>
     * <li>UNK = Unknown</li>
     * <li>BLA = Currently running</li>
     * </ul>
     *
     * @param shouldaRun
     *        (required)
     * @param runStatuses
     *        (required)
     * @return String
     */
    private String determineScheduleStatus(Timestamp shouldaRun, List<RunStatusEntity> runStatuses) {
        log.debug("Entering BatchAppInfo.determineScheduleStatus()");
        log.debug("This method takes the last run status and the time a job should have run and compares them to determine what the schedule status should be.  Possible statuses are: OFS = Off Schedule,PND = Pending(for daily only),SUC = Successful,UNS = Unsuccessful,UNK = Unknown,BLA = Currently running.");
        String status = EJBConstants.BATCH_STATUS_OFF_SCHEDULE;
        long difference = 0;
        if(runStatuses != null && !runStatuses.isEmpty()){
            RunStatusEntity rm = runStatuses.get(0);
            // if the job isn't currently running, then do the other checks, else return BLA
            if(!"PRO".equals(rm.getStatus().getStatusCd())){
                difference = shouldaRun.getTime() - rm.getStartTime().getTime();
                // If the time is within 30 seconds, then the batch job ran on schedule.
                if(difference > -30000 && difference < 30000){
                    if("DON".equals(rm.getStatus().getStatusCd())){
                        status = rm.getResult().getResultCd();
                    }else{
                        // it is currently running
                        status = EJBConstants.BATCH_STATUS_RUNNING;
                    }// end if
                }else{
                    // Only show the job as off-schedule if the time that it should have run is not within 30 seconds.
                    long currentTime = System.currentTimeMillis();
                    difference = currentTime - shouldaRun.getTime();
                    if(difference > -30000 && difference < 30000){
                        status = rm.getResult().getResultCd();
                    }else{
                        // batch job did not run when it was supposed to.
                        status = EJBConstants.BATCH_STATUS_OFF_SCHEDULE;
                    }// end if/else
                }// end if-else
            }else{
                // currently processing
                status = EJBConstants.BATCH_STATUS_RUNNING;
            }// end if-else
        }// end if
        log.debug("Return value is: status=" + String.valueOf(status));
        log.debug("Exiting BatchAppInfo.determineScheduleStatus().");
        return status;
    }// end determineScheduleStatus

    /**
     * This method determines if the next run time should be adjusted by checking the repeat value. If it does, then it will add the specified increment to it using the last run as its basis.
     *
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param schedule
     *        (required)
     * @param runTs
     *        (required)
     * @return timestamp
     */
    private Timestamp handleRepeats(ScheduleEntity schedule, Timestamp runTs) {
        log.debug("Entering BatchAppInfo.handleRepeats().");
        log.debug("This method determines if the next run time should be adjusted by checking the repeat value. If it does, then it will add the specified increment to it using the last run as its basis.");
        Timestamp repeatRun = runTs;
        // only perform calculations if necessary
        if(schedule.getRepeat().getRepeatCd() != null){
            Calendar currentCalendar = Calendar.getInstance();
            Timestamp currentTs = new Timestamp(System.currentTimeMillis());
            currentCalendar.setTimeInMillis(currentTs.getTime());
            // must get rid of the seconds in order to account for thread lag.
            currentCalendar.set(Calendar.SECOND, 0);
            currentCalendar.set(Calendar.MILLISECOND, 0);
            // Set up the time increments by the repeat code value.
            Calendar nextRunCalendar = Calendar.getInstance();
            Calendar holdRunCalendar = Calendar.getInstance();
            Calendar originalRunCalendar = Calendar.getInstance();
            originalRunCalendar.setTimeInMillis(runTs.getTime());
            nextRunCalendar.setTimeInMillis(runTs.getTime());
            Integer increment = 0;
            Integer incrementType = 0;
            if(JmsSchedule.REPEAT_TYPE_30_MINUTE.equals(schedule.getRepeat().getRepeatCd())){
                increment = 30;
                incrementType = Calendar.MINUTE;
            }else if(JmsSchedule.REPEAT_TYPE_15_MINUTE.equals(schedule.getRepeat().getRepeatCd())){
                increment = 15;
                incrementType = Calendar.MINUTE;
            }else if(JmsSchedule.REPEAT_TYPE_10_MINUTE.equals(schedule.getRepeat().getRepeatCd())){
                increment = 10;
                incrementType = Calendar.MINUTE;
            }else if(JmsSchedule.REPEAT_TYPE_05_MINUTE.equals(schedule.getRepeat().getRepeatCd())){
                increment = 05;
                incrementType = Calendar.MINUTE;
            }else if(JmsSchedule.REPEAT_TYPE_01_MINUTE.equals(schedule.getRepeat().getRepeatCd())){
                increment = 01;
                incrementType = Calendar.MINUTE;
            }else if(JmsSchedule.REPEAT_TYPE_1_HOUR.equals(schedule.getRepeat().getRepeatCd())){
                increment = 1;
                incrementType = Calendar.HOUR;
            }else if(JmsSchedule.REPEAT_TYPE_2_HOUR.equals(schedule.getRepeat().getRepeatCd())){
                increment = 2;
                incrementType = Calendar.HOUR;
            }else if(JmsSchedule.REPEAT_TYPE_3_HOUR.equals(schedule.getRepeat().getRepeatCd())){
                increment = 3;
                incrementType = Calendar.HOUR;
            }else if(JmsSchedule.REPEAT_TYPE_4_HOUR.equals(schedule.getRepeat().getRepeatCd())){
                increment = 4;
                incrementType = Calendar.HOUR;
            }// end else-if
             // Take the base start time and add the proper increments until the time is greater than current time or it moves the day that was set by the recur function.
            for(int i = 0;i < 500;i++){
                holdRunCalendar = (Calendar) nextRunCalendar.clone();
                nextRunCalendar.add(incrementType, increment);
                if(nextRunCalendar.after(currentCalendar) || nextRunCalendar.equals(currentCalendar) || nextRunCalendar.get(Calendar.DAY_OF_YEAR) > originalRunCalendar.get(Calendar.DAY_OF_YEAR)){
                    if(nextRunCalendar.after(currentCalendar) || nextRunCalendar.get(Calendar.DAY_OF_YEAR) > originalRunCalendar.get(Calendar.DAY_OF_YEAR)){
                        repeatRun.setTime(holdRunCalendar.getTimeInMillis());
                    }else{
                        repeatRun.setTime(nextRunCalendar.getTimeInMillis());
                    }// end if-else
                    break;
                }// end if
            }// end for
        }// end if
        log.debug("Return value is: date=" + String.valueOf(repeatRun));
        log.debug("Exiting BatchAppInfo.handleRepeats");
        return repeatRun;
    }// end handleRepeats

    /**
     * This method calculates all the jobs repeats for a given day starting at a certain time.
     *
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2017
     * @param schedule
     *        (required)
     * @param runTs
     *        (required)
     * @return List<Timestamp>
     */
    private List<Timestamp> handleMultipleRepeats(JmsSchedule schedule, Timestamp runTs) {
        log.debug("Entering BatchAppInfo.handleMultipleRepeats().");
        log.debug("This method calculates all the jobs repeats for a given day.");
        Timestamp repeatRun = runTs;
        List<Timestamp> runTimes = new ArrayList<Timestamp>();

        Calendar calculationDay = Calendar.getInstance();
        calculationDay.setTimeInMillis(runTs.getTime());
        // must get rid of the seconds in order to account for thread lag.
        calculationDay.set(Calendar.SECOND, 0);
        calculationDay.set(Calendar.MILLISECOND, 0);

        // Set up the time increments by the repeat code value.
        Calendar nextRunCalendar = Calendar.getInstance();
        nextRunCalendar.setTimeInMillis(runTs.getTime());
        // If the original fits, add the original time to it
        if(nextRunCalendar.after(calculationDay) || nextRunCalendar.equals(calculationDay)){
            runTimes.add((Timestamp) runTs.clone());
        }// end if
         // only perform calculations if necessary
        if(schedule.getRepeatCd() != null && !"BLA".equals(schedule.getRepeatCd())){
            Integer increment = 0;
            Integer incrementType = 0;
            if(JmsSchedule.REPEAT_TYPE_30_MINUTE.equals(schedule.getRepeatCd())){
                increment = 30;
                incrementType = Calendar.MINUTE;
            }else if(JmsSchedule.REPEAT_TYPE_15_MINUTE.equals(schedule.getRepeatCd())){
                increment = 15;
                incrementType = Calendar.MINUTE;
            }else if(JmsSchedule.REPEAT_TYPE_10_MINUTE.equals(schedule.getRepeatCd())){
                increment = 10;
                incrementType = Calendar.MINUTE;
            }else if(JmsSchedule.REPEAT_TYPE_05_MINUTE.equals(schedule.getRepeatCd())){
                increment = 05;
                incrementType = Calendar.MINUTE;
            }else if(JmsSchedule.REPEAT_TYPE_01_MINUTE.equals(schedule.getRepeatCd())){
                increment = 01;
                incrementType = Calendar.MINUTE;
            }else if(JmsSchedule.REPEAT_TYPE_1_HOUR.equals(schedule.getRepeatCd())){
                increment = 1;
                incrementType = Calendar.HOUR;
            }else if(JmsSchedule.REPEAT_TYPE_2_HOUR.equals(schedule.getRepeatCd())){
                increment = 2;
                incrementType = Calendar.HOUR;
            }else if(JmsSchedule.REPEAT_TYPE_3_HOUR.equals(schedule.getRepeatCd())){
                increment = 3;
                incrementType = Calendar.HOUR;
            }else if(JmsSchedule.REPEAT_TYPE_4_HOUR.equals(schedule.getRepeatCd())){
                increment = 4;
                incrementType = Calendar.HOUR;
            }// end else-if
             // Take the base start time and add the proper increments until the time is greater than current time or it moves the day that was set by the repeat function.
            int i = 0;
            while(true){
                nextRunCalendar.add(incrementType, increment);
                if(nextRunCalendar.get(Calendar.DAY_OF_YEAR) > calculationDay.get(Calendar.DAY_OF_YEAR)){
                    break;
                }// end if
                if(nextRunCalendar.after(calculationDay) || nextRunCalendar.equals(calculationDay)){
                    repeatRun.setTime(nextRunCalendar.getTimeInMillis());
                    runTimes.add((Timestamp) repeatRun.clone());
                }// end if
                i = i + 1;
            }// end while
        }// end if
        log.debug("Exiting BatchAppInfo.handleMultipleRepeats");
        return runTimes;
    }// end handleMultipleRepeats

    /**
     * This method determines if the next run time should be adjusted by checking the repeat value. If it does, then it will add the specified increment to it using the last run as its basis.
     *
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param schedule
     *        (required)
     * @param runTs
     *        (required)
     * @return timestamp
     */
    private Timestamp handleRecurs(ScheduleEntity schedule, Timestamp runTs) {
        log.debug("Entering BatchAppInfo.handleRecurs().");
        log.debug("This method determines if the next run time should be adjusted by checking the repeat value. If it does, then it will add the specified increment to it using the last run as its basis.");
        Timestamp recurRun = runTs;
        Date currentDateTest = ShamenEJBUtil.clearSQLTimeFields(new Date(System.currentTimeMillis()));
        Timestamp currentTs = new Timestamp(System.currentTimeMillis());
        Date startDate = ShamenEJBUtil.clearSQLTimeFields(schedule.getScheduleStartDt());
        // Insure that the recur is set and that the start date is not today's date.
        if(schedule.getRecurNo() != null && currentDateTest.after(startDate)){
            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.setTimeInMillis(currentTs.getTime());
            // must get rid of the seconds in order to account for thread lag.
            currentCalendar.set(Calendar.SECOND, 0);
            currentCalendar.set(Calendar.MILLISECOND, 0);
            if(!runTs.after(currentTs)){
                // Take the base start time and add the proper increments until the time is greater than current time.
                Calendar nextRunCalendar = Calendar.getInstance();
                nextRunCalendar.setTimeInMillis(runTs.getTime());
                Integer increment = Integer.valueOf(String.valueOf(schedule.getRecurNo()));
                Integer incrementType = null;
                if(JmsSchedule.FREQUENCY_DAILY.equals(schedule.getFrequency().getFrequencyCd())){
                    incrementType = Calendar.DAY_OF_YEAR;
                }else if(JmsSchedule.FREQUENCY_WEEKLY.equals(schedule.getFrequency().getFrequencyCd())){
                    incrementType = Calendar.WEEK_OF_MONTH;
                }// end if-else
                Date nextRunDate = new Date(nextRunCalendar.getTimeInMillis());
                Date currentDate = null;
                Calendar holdRunCalendar = Calendar.getInstance();
                if(increment != 0){
                    // add the proper increments until the time is future.
                    while(true){
                        holdRunCalendar = (Calendar) nextRunCalendar.clone();
                        // holdRunCalendar = nextRunCalendar;
                        nextRunCalendar.add(incrementType, increment);
                        nextRunDate = ShamenEJBUtil.clearSQLTimeFields(new Date(nextRunCalendar.getTimeInMillis()));
                        currentDate = ShamenEJBUtil.clearSQLTimeFields(new Date(currentCalendar.getTimeInMillis()));
                        // if calculated next run date is after current date or calculated next run date equals the current date, it's the one to
                        // use.
                        if(nextRunDate.after(currentDate) || nextRunDate.equals(currentDate)){
                            if(nextRunDate.after(currentDate)){
                                recurRun.setTime(holdRunCalendar.getTimeInMillis());
                            }else{
                                // if the dates are the same, also check the times
                                if(nextRunDate.equals(currentDate)){
                                    // holdStringDate = String.valueOf(nextRunCalendar.get(Calendar.MONTH) + 1) + "/" + String.valueOf(nextRunCalendar.get(Calendar.DAY_OF_MONTH)) + "/" + String.valueOf(nextRunCalendar.get(Calendar.YEAR));
                                    // if the run time is greater than current time, then use the previous recur date. Otherwise use the last one.
                                    if(ShamenEJBUtil.getSqlTSFromDateTimeString(ShamenEJBUtil.getDate(), schedule.getStartTime().toString(), EJBConstants.SDFT).after(new Timestamp(System.currentTimeMillis()))){
                                        recurRun.setTime(holdRunCalendar.getTimeInMillis());
                                    }else{
                                        recurRun.setTime(nextRunCalendar.getTimeInMillis());
                                    }// end if-else
                                }else{
                                    recurRun.setTime(nextRunCalendar.getTimeInMillis());
                                }// end if-else
                            }// end if-else
                            break;
                        }// end if
                    }// end while
                }// end if
            }// end if
        }// end if
        log.debug("Return value is: date=" + String.valueOf(recurRun));
        log.debug("Exiting BatchAppInfo.handleRecurs");
        return recurRun;
    }// end handleRecurs

    /**
     * This method loads a list of all the days this job should run within a specified range.
     *
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2017
     * @param schedule
     *        (required)
     * @param startTs
     *        schedule start (required)
     * @param fromTs
     *        from range (required)
     * @param toTs
     *        to range (required)
     * @return List<Timestamp>
     */
    private List<Timestamp> handleMultipleRecurs(JmsSchedule schedule, Timestamp startTs, Timestamp fromTs, Timestamp toTs) {
        log.debug("Entering BatchAppInfo.handleMultipleRecurs().");
        log.debug("This method loads a list of all the days this job should run within a specified range.");
        List<Timestamp> returnTimes = new ArrayList<Timestamp>();
        // Date startDate = ShamenEJBUtil.clearSQLTimeFields(ShamenEJBUtil.getSQLDate(schedule.getScheduleStartDt()));
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTimeInMillis(fromTs.getTime());
        // must get rid of the seconds for calculation purposes.
        fromCalendar.set(Calendar.SECOND, 0);
        fromCalendar.set(Calendar.MILLISECOND, 0);
        // Take the base start time and add the proper increments until the time is greater than current time.
        Calendar nextRunCalendar = Calendar.getInstance();
        nextRunCalendar.setTimeInMillis(startTs.getTime());
        // Insure that the recur is set
        if(schedule.getRecurNo() != null){
            // make sure that schedule is not set to start after the date range.
            if(!startTs.after(toTs)){
                Integer increment = Integer.valueOf(String.valueOf(schedule.getRecurNo()));
                Integer incrementType = null;
                if(JmsSchedule.FREQUENCY_DAILY.equals(schedule.getFrequencyCd())){
                    incrementType = Calendar.DAY_OF_YEAR;
                }else if(JmsSchedule.FREQUENCY_WEEKLY.equals(schedule.getFrequencyCd())){
                    incrementType = Calendar.WEEK_OF_MONTH;
                }// end if-else
                Date nextRunDate = new Date(nextRunCalendar.getTimeInMillis());
                Date currentDate = null;
                nextRunDate = ShamenEJBUtil.clearSQLTimeFields(new Date(nextRunCalendar.getTimeInMillis()));
                currentDate = ShamenEJBUtil.clearSQLTimeFields(new Date(fromCalendar.getTimeInMillis()));
                // if original date is in date range, use it.
                if((nextRunDate.after(currentDate) || nextRunDate.equals(currentDate)) && nextRunDate.before(toTs)){
                    returnTimes.add(new Timestamp(nextRunCalendar.getTimeInMillis()));
                }// end if-else
                 // add the proper increments until the time is not in range.
                if(increment != 0){
                    while(true){
                        nextRunCalendar.add(incrementType, increment);
                        nextRunDate = ShamenEJBUtil.clearSQLTimeFields(new Date(nextRunCalendar.getTimeInMillis()));
                        currentDate = ShamenEJBUtil.clearSQLTimeFields(new Date(fromCalendar.getTimeInMillis()));
                        // if calculated next run date is in date range, use it.
                        if((nextRunDate.after(currentDate) || nextRunDate.equals(currentDate)) && nextRunDate.before(toTs)){
                            returnTimes.add(new Timestamp(nextRunCalendar.getTimeInMillis()));
                        }// end if-else
                         // if the calculated next run date is past the date range, leave.
                        if(nextRunDate.after(toTs)){
                            break;
                        }// end if
                    }// end while
                }// end if
            }// end if
        }// end if
        log.debug("Exiting BatchAppInfo.handleMultipleRecurs");
        return returnTimes;
    }// end handleMultipleRecurs

    /**
     * This method loads a list of all the days this job should run within a specified range. This is for Weekly type jobs.
     *
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2017
     * @param schedule
     *        (required)
     * @param startTs
     *        schedule start (required)
     * @param fromTs
     *        from range (required)
     * @param toTs
     *        to range (required)
     * @return List<Timestamp>
     */
    private List<Timestamp> calculateMultipleWeekdays(JmsSchedule schedule, Timestamp startTs, Timestamp fromTs, Timestamp toTs) {
        log.debug("Entering BatchAppInfo.calculateMultipleWeekdays().");
        log.debug("This method loads a list of all the days this job should run within a specified range.  This is for Weekly type jobs.");
        List<Timestamp> returnTimes = new ArrayList<Timestamp>();
        // Insure that the weekdays has a value
        if(schedule.getWeekdays() != null){
            // make sure that schedule is not set to start after the date range.
            if(!startTs.after(toTs)){
                Calendar fromCalendar = Calendar.getInstance();
                fromCalendar.setTimeInMillis(fromTs.getTime());
                // must get rid of the seconds for calculation purposes.
                fromCalendar.set(Calendar.SECOND, 0);
                fromCalendar.set(Calendar.MILLISECOND, 0);
                // Take the base start time and add the proper increments until it's in range
                Calendar nextRunCalendar = Calendar.getInstance();
                nextRunCalendar.setTimeInMillis(startTs.getTime());
                Integer increment = 1;
                Integer incrementType = Calendar.DAY_OF_YEAR;
                Date nextRunDate = new Date(nextRunCalendar.getTimeInMillis());
                Date fromDate = ShamenEJBUtil.clearSQLTimeFields(new Date(fromCalendar.getTimeInMillis()));
                ArrayList<Integer> weekDayArray = schedule.getWeekdaysAsArrayList();
                // add the proper increments until the time is future.
                while(true){

                    Integer runDay = nextRunCalendar.get(Calendar.DAY_OF_WEEK);
                    nextRunDate = ShamenEJBUtil.clearSQLTimeFields(new Date(nextRunCalendar.getTimeInMillis()));
                    // if calculated next run date is in date range, use it.
                    if((nextRunDate.after(fromDate) || nextRunDate.equals(fromDate)) && nextRunDate.before(toTs)){
                        if(weekDayArray.contains(runDay)){
                            returnTimes.add(new Timestamp(nextRunCalendar.getTimeInMillis()));
                        }// end if
                    }// end if-else
                     // if the calculated next run date is past the date range, leave.
                    if(nextRunDate.after(toTs)){
                        break;
                    }// end if
                    nextRunCalendar.add(incrementType, increment);
                }// end while
            }// end if
        }// end if
        log.debug("Exiting BatchAppInfo.calculateMultipleWeekdays");
        return returnTimes;
    }// end calculateMultipleWeekdays

    /**
     * This method loads a list of all the days this job should run within a specified range. This is for Monthly type jobs.
     *
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2017
     * @param schedule
     *        (required)
     * @param startTs
     *        schedule start (required)
     * @param fromTs
     *        from range (required)
     * @param toTs
     *        to range (required)
     * @return List<Timestamp>
     */
    private List<Timestamp> calculateMultipleMonthdays(JmsSchedule schedule, Timestamp startTs, Timestamp fromTs, Timestamp toTs) {
        log.debug("Entering BatchAppInfo.calculateMultipleMonthdays().");
        log.debug("This method loads a list of all the days this job should run within a specified range.  This is for Monthly type jobs.");
        List<Timestamp> returnTimes = new ArrayList<Timestamp>();
        // make sure that schedule is not set to start after the date range.
        if(!startTs.after(toTs)){
            // set up calendar with from range
            Calendar fromCalendar = Calendar.getInstance();
            fromCalendar.setTimeInMillis(fromTs.getTime());
            fromCalendar = ShamenEJBUtil.clearCalendarTimeFieldsForCal(fromCalendar);
            // set up calendar with to range
            Calendar toCalendar = Calendar.getInstance();
            toCalendar.setTimeInMillis(toTs.getTime());
            toCalendar = ShamenEJBUtil.clearCalendarTimeFieldsForCal(toCalendar);
            Calendar calculationDate = Calendar.getInstance();
            calculationDate.setTimeInMillis(startTs.getTime());
            Integer increment = 1;
            Integer incrementType = Calendar.DAY_OF_YEAR;
            Integer calculationDay;
            Integer weekNumber;
            if(JmsSchedule.FREQUENCY_TYPE_MONTH_WITH_WEEKDAY.equals(schedule.getFrequencyTypeCd())){
                Integer lastWeekOfMonth;
                ArrayList<Integer> weekNumberArray = schedule.getWeekNumbersAsArrayList();
                ArrayList<Integer> weekDayArray = schedule.getWeekdaysAsArrayList();
                while(true){
                    calculationDay = calculationDate.get(Calendar.DAY_OF_WEEK);
                    weekNumber = calculationDate.get(Calendar.DAY_OF_WEEK_IN_MONTH);
                    lastWeekOfMonth = calculationDate.getActualMaximum(Calendar.DAY_OF_WEEK_IN_MONTH);
                    // first check to see if it fits the date range
                    if(calculationDate.after(fromCalendar) && calculationDate.before(toCalendar)){
                        // then check if it is the specified week of the month or it's last week of month and that was specified AND if it fits the date range then check for load
                        if(weekNumberArray.contains(weekNumber) || (weekNumber.equals(lastWeekOfMonth) && weekNumberArray.contains(Integer.valueOf(5)))){
                            // then check the day
                            if(weekDayArray.contains(calculationDay)){
                                returnTimes.add(new Timestamp(calculationDate.getTimeInMillis()));
                            }// end if
                        }// end if
                    }// end if
                    calculationDate.add(incrementType, increment);
                    // if the calculated next run date is past the date range, leave.
                    if(calculationDate.after(toCalendar)){
                        break;
                    }// end if
                }// end while
            }else if(JmsSchedule.FREQUENCY_TYPE_DAY_OF_MONTH.equals(schedule.getFrequencyTypeCd())){
                // This type of schedule is when the user picks certain days of of the month
                while(true){
                    calculationDay = calculationDate.get(Calendar.DAY_OF_MONTH);
                    Integer lastDayOfMonth = calculationDate.getMaximum(Calendar.DAY_OF_MONTH);
                    ArrayList<Integer> monthDayArray = schedule.getMonthDaysAsArrayList();
                    // first check to see if it fits the date range
                    if(calculationDate.after(fromCalendar) && calculationDate.before(toCalendar)){
                        // then check if it is one of the days the user chose
                        if(monthDayArray.contains(calculationDay) || (calculationDay.equals(lastDayOfMonth) && monthDayArray.contains(Integer.valueOf(32)))){
                            returnTimes.add(new Timestamp(calculationDate.getTimeInMillis()));
                        }// end if
                    }// end if
                    calculationDate.add(incrementType, increment);
                    // if the calculated next run date is past the date range, leave.
                    if(calculationDate.after(toCalendar)){
                        break;
                    }// end if
                }// end while
            }// end if-else
        }// end if
        log.debug("Exiting BatchAppInfo.calculateMultipleMonthdays");
        return returnTimes;
    }// end calculateMultipleMonthdays

    /**
     * This method calculates all the run times for a given app within a date range. The date range is mandatory.
     *
     * @param app
     *        The scheduled app
     * @param fromTs
     *        From Timestamp used in date range.
     * @param toTs
     *        To Timestamp used in date range.
     * @return list of run Timestamps
     */
    public List<Timestamp> calculateRunTimes(Scheduleable app, Timestamp fromTs, Timestamp toTs) {
        log.debug("Entering BatchAppInfo.calculateRunTimes().");
        log.debug("This method calculates all the run times for a given app within a date range. The date range is mandatory.");
        List<Timestamp> runTimes = new ArrayList<Timestamp>();
        for(JmsSchedule schedule : app.getSchedule()){
            if(schedule != null && "Y".equals(schedule.getActive())){
                if(JmsSchedule.FREQUENCY_ONE_TIME.equals(schedule.getFrequencyCd())){
                    runTimes.addAll(getOneTimeRunTimes(schedule, fromTs, toTs));
                }else if(JmsSchedule.FREQUENCY_DAILY.equals(schedule.getFrequencyCd())){
                    runTimes.addAll(getDailyRunTimes(schedule, fromTs, toTs));
                }else if(JmsSchedule.FREQUENCY_WEEKLY.equals(schedule.getFrequencyCd())){
                    runTimes.addAll(getWeeklyRunTimes(schedule, fromTs, toTs));
                }else if(JmsSchedule.FREQUENCY_MONTHLY.equals(schedule.getFrequencyCd())){
                    runTimes.addAll(getMonthlyRunTimes(schedule, fromTs, toTs));
                }// end if/else
            }// end if
        }// end for
        log.debug("Exiting BatchAppInfo.calculateRunTimes");
        return runTimes;
    }// end calculateRunTimes

    /**
     * This method gets the next run time for one-time jobs for a given range. If none are found, it will just return an empty list.
     *
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param schedule
     *        (required)
     * @param fromTs
     *        from range (required)
     * @param toTs
     *        to range (required)
     * @return List<Timestamp>
     */
    private List<Timestamp> getOneTimeRunTimes(JmsSchedule schedule, Timestamp fromTs, Timestamp toTs) {
        log.debug("Entering BatchAppInfo.getOneTimeRunTimes().");
        log.debug("This method gets the next run time for one-time jobs for a given range. If none are found, it will just return an empty list.");
        List<Timestamp> runTimes = new ArrayList<Timestamp>();
        Timestamp startTime = null;
        // Load the run time
        startTime = ShamenEJBUtil.getSqlTSFromDateTimeString(schedule.getScheduleStartDt(), schedule.getStartTime().toString(), EJBConstants.SDFTF);
        // for one time only job, if the one time is not in the date range, do not proceed.
        if(startTime.after(fromTs) && startTime.before(toTs)){
            // Check the repeat value and handle accordingly.
            runTimes = handleMultipleRepeats(schedule, startTime);
        }// end if
        log.debug("Exiting BatchAppInfo.getNextOneTimeRunTimes");
        return runTimes;
    }// end getOneTimeRunTimes

    /**
     * This method gets the next run time for daily jobs for a given range. If none are found, it will just return an empty list.
     *
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param schedule
     *        (required)
     * @param fromTs
     *        from range (required)
     * @param toTs
     *        to range (required)
     * @return List<Timestamp>
     */
    private List<Timestamp> getDailyRunTimes(JmsSchedule schedule, Timestamp fromTs, Timestamp toTs) {
        log.debug("Entering BatchAppInfo.getDailyRunTimes().");
        log.debug("This method gets the next run time for daily jobs for a given range. If none are found, it will just return an empty list.");
        List<Timestamp> runTimes = new ArrayList<Timestamp>();
        Timestamp startTime = null;
        // Load the run time
        startTime = ShamenEJBUtil.getSqlTSFromDateTimeString(schedule.getScheduleStartDt(), schedule.getStartTime().toString(), EJBConstants.SDFTF);
        // Get all the possible days to run within the specified range.
        List<Timestamp> runDays = handleMultipleRecurs(schedule, startTime, fromTs, toTs);
        // loop through all the possible days and find all the times for each one.
        for(int i = 0, j = runDays.size();i < j;i++){
            // Check the repeat value and handle accordingly.
            runTimes.addAll(handleMultipleRepeats(schedule, runDays.get(i)));
        }// end for
        log.debug("Exiting BatchAppInfo.getDailyRunTimes");
        return runTimes;
    }// end getDailyRunTimes

    /**
     * This method gets the next run time for weekly jobs for a given range. If none are found, it will just return an empty list.
     *
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param schedule
     *        (required)
     * @param fromTs
     *        from range (required)
     * @param toTs
     *        to range (required)
     * @return list of timestamps
     */
    private List<Timestamp> getWeeklyRunTimes(JmsSchedule schedule, Timestamp fromTs, Timestamp toTs) {
        log.debug("Entering BatchAppInfo.getWeeklyRunTimes().");
        log.debug("This method gets the next run time for weekly jobs for a given range. If none are found, it will just return an empty list.");
        List<Timestamp> runTimes = new ArrayList<Timestamp>();
        // Load the run time
        Timestamp startTs = ShamenEJBUtil.getSqlTSFromDateTimeString(schedule.getScheduleStartDt(), schedule.getStartTime().toString(), EJBConstants.SDFTF);
        // Get all the pertinent weekdays to use for calculations.
        List<Timestamp> runDays = calculateMultipleWeekdays(schedule, startTs, fromTs, toTs);
        // loop through all the possible days and find all the times for each one.
        for(int i = 0, j = runDays.size();i < j;i++){
            // Check the repeat value and handle accordingly.
            runTimes.addAll(handleMultipleRepeats(schedule, runDays.get(i)));
        }// end for
        log.debug("Exiting BatchAppInfo.getWeeklyRunTimes");
        return runTimes;
    }// end getWeeklyRunTimes

    /**
     * This method gets the next run time for monthly jobs for a given range. If none are found, it will just return an empty list.
     *
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param schedule
     *        (required)
     * @param fromTs
     *        from range (required)
     * @param toTs
     *        to range (required)
     * @return list of timestamps
     */
    private List<Timestamp> getMonthlyRunTimes(JmsSchedule schedule, Timestamp fromTs, Timestamp toTs) {
        log.debug("Entering BatchAppInfo.getWeeklyRunTimes().");
        log.debug("This method gets the next run time for weekly jobs for a given range. If none are found, it will just return an empty list.");
        List<Timestamp> runTimes = new ArrayList<Timestamp>();
        // Load the run time
        Timestamp startTs = ShamenEJBUtil.getSqlTSFromDateTimeString(schedule.getScheduleStartDt(), schedule.getStartTime().toString(), EJBConstants.SDFTF);
        // Get all the pertinent weekdays to use for calculations.
        List<Timestamp> runDays = calculateMultipleMonthdays(schedule, startTs, fromTs, toTs);
        // loop through all the possible days and find all the times for each one.
        for(int i = 0, j = runDays.size();i < j;i++){
            // Check the repeat value and handle accordingly.
            runTimes.addAll(handleMultipleRepeats(schedule, runDays.get(i)));
        }// end for
        log.debug("Exiting BatchAppInfo.getWeeklyRunTimes");
        return runTimes;
    }// end getWeeklyRunTimes

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<BatchAppEntity> getAllScheduledBatchAppsAndCollections() throws Exception {
        log.debug("Entering getAllScheduledBatchAppsAndCollections. ");
        log.debug("This method is used to return a list of objects filled with batch app info.");
        StringBuffer query = new StringBuffer();
        query.append("SELECT b.Batch_App_Ref_Id,c.Controller_Nm, b.Batch_Nm, s.Schedule_Start_Dt, s.Start_Time, a.Application_Nm, a.Application_Addr,bt.Batch_Type_Desc, s.Active_Ind,cc.Controller_Status_Desc,f.frequency_desc,sy.System_Nm,c.controller_ref_id,c.controller_status_cd,s.Frequency_Cd,s.Recur_No,s.Repeat_Cd,s.Week_No,s.Weekdays,s.Day_No,s.Frequency_Type_Cd, b.System_Ref_Id, 'N',s.schedule_ref_id,us.User_First_Nm,us.User_Last_Nm,s.create_ts,0");
        query.append(" FROM Trans.Batch_Apps b left join trans.Schedules s on s.Batch_App_Ref_Id = b.Batch_App_Ref_Id left join trans.Controllers c on c.Controller_Ref_Id = b.Controller_Ref_Id left join trans.Applications a on a.Application_Ref_Id = b.Application_Ref_Id left join code.Batch_Type_Codes bt on bt.Batch_Type_Cd = b.Batch_Type_Cd left join code.Controller_Status_Codes cc on cc.Controller_Status_Cd = c.Controller_Status_Cd left join code.Frequency_Codes f on f.Frequency_Cd = s.Frequency_Cd left join trans.Systems sy on sy.System_Ref_Id = b.System_Ref_Id left join trans.Authorized_Users us on us.User_Ref_Id = b.Responsible_Staff_User_Ref_Id and us.delete_ind = 'N'");
        query.append(" WHERE b.Delete_Ind = 'N' and Schedule_Start_Dt is not null and s.Delete_Ind = 'N' order by b.Batch_Nm");
        List<Object> list = em.createNativeQuery(query.toString()).getResultList();
        List<BatchAppEntity> batchList = EJBObjectConvertor.toBatchAppListEntityFromObj(list);
        log.debug("Return value is: list.size=" + (ShamenEJBUtil.isEmpty(batchList) ? "0" : batchList.size()));
        log.debug("Exiting getBatchList.");
        return checkOnSchedule(batchList);
    }// end getAllScheduledBatchAppsAndCollections

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<BatchAppEntity> getBatchCollectionList() throws Exception {
        log.debug("Entering getBatchCollectionList.");
        log.debug("This method is used to return a list of objects filled with batch app info.");
        StringBuffer query = new StringBuffer();
        query.append("SELECT b.Batch_App_Ref_Id,c.Controller_Nm, b.Batch_Nm, s.Schedule_Start_Dt, s.Start_Time, a.Application_Nm, a.Application_Addr,bt.Batch_Type_Desc, s.Active_Ind,cc.Controller_Status_Desc,f.frequency_desc,sy.System_Nm,c.controller_ref_id,c.controller_status_cd,s.Frequency_Cd,s.Recur_No,s.Repeat_Cd,s.Week_No,s.Weekdays,s.Day_No,s.Frequency_Type_Cd, b.System_Ref_Id,'N',s.schedule_ref_id,us.User_First_Nm,us.User_Last_Nm,s.create_ts,(Select COUNT(*) FROM (SELECT COUNT(Run_Nbr) as RECORDS FROM trans.Run_Status r WHERE r.delete_ind = 'N' and r.Batch_App_Ref_Id = b.Batch_App_Ref_Id and r.Main_Batch_App_Ref_Id is null GROUP BY Run_Nbr) as number) as executionCount");
        query.append(" FROM Trans.Batch_Apps b left join trans.Schedules s on s.Batch_App_Ref_Id = b.Batch_App_Ref_Id and s.Delete_Ind = 'N' left join trans.Controllers c on c.Controller_Ref_Id = b.Controller_Ref_Id left join trans.Applications a on a.Application_Ref_Id = b.Application_Ref_Id left join code.Batch_Type_Codes bt on bt.Batch_Type_Cd = b.Batch_Type_Cd left join code.Controller_Status_Codes cc on cc.Controller_Status_Cd = c.Controller_Status_Cd left join code.Frequency_Codes f on f.Frequency_Cd = s.Frequency_Cd left join trans.Systems sy on sy.System_Ref_Id = b.System_Ref_Id left join trans.Authorized_Users us on us.User_Ref_Id = b.Responsible_Staff_User_Ref_Id and us.delete_ind = 'N'");
        query.append(" WHERE bt.batch_type_cd = 'COL' and b.Delete_Ind = 'N' order by b.Batch_Nm");
        List<Object> list = em.createNativeQuery(query.toString()).getResultList();
        List<BatchAppEntity> batchList = EJBObjectConvertor.toBatchAppListEntityFromObj(list);
        log.debug("Return value is: list.size=" + (ShamenEJBUtil.isEmpty(batchList) ? "0" : batchList.size()));
        log.debug("Exiting getBatchCollectionList.");
        return checkOnSchedule(batchList);
    }// end getBatchList

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<Object> getRunStatusList() throws Exception {
        log.debug("Entering getRunStatusList.");
        log.debug("This method is used to return a list of RunStatusEntities.");
        StringBuffer query = new StringBuffer();
        query.append("with sel as (select Batch_App_Ref_Id, Main_Batch_App_Ref_Id,  MAX(create_ts) as create_ts from trans.Run_Status group by Batch_App_Ref_Id, Main_Batch_App_Ref_Id)");
        query.append(" SELECT r.Run_Status_Ref_Id,r.Start_Ts, r.Stop_Ts, r.Status_Cd,s.Status_Desc as statusDesc, res.Result_Desc, b.batch_nm, r.schedule_Ref_Id, r.batch_App_Ref_Id,a.User_First_Nm, a.User_Last_Nm, r.Main_Batch_App_Ref_Id,r.Description,r.Run_Nbr,b.Batch_Type_Cd,b.System_Ref_Id,res.Result_Cd, r.Create_User_Ref_Id, r.Result_Detail");
        query.append(" FROM trans.Run_Status r join Trans.Batch_Apps b  on b.Batch_App_Ref_Id = r.Batch_App_Ref_Id join code.Result_Codes res on res.Result_Cd = r.Result_Cd join Code.Status_Codes s on s.Status_Cd = r.Status_Cd left outer join Trans.Authorized_Users a on a.User_Ref_Id = r.Create_User_Ref_Id join sel on r.Batch_App_Ref_Id = sel.Batch_App_Ref_Id and sel.create_ts = r.create_ts and (sel.Main_Batch_App_Ref_Id = r.Main_Batch_App_Ref_Id or r.Main_Batch_App_Ref_Id is null)");
        query.append(" WHERE r.delete_ind = 'N' order by r.Main_Batch_App_Ref_Id,r.Start_Ts,r.batch_App_Ref_Id  desc, r.Stop_Ts");
        List<Object> list = em.createNativeQuery(query.toString()).getResultList();
        log.debug("Return value is: list.size=" + (ShamenEJBUtil.isEmpty(list) ? "0" : list.size()));
        log.debug("Exiting getRunStatusList.");
        return list;
    }// end getRunStatusList

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<Object> getRunStatusListFromSchedule() throws Exception {
        log.debug("Entering getRunStatusListFromSchedule.");
        log.debug("This method is used to return a list of RunStatusEntities.");
        StringBuffer query = new StringBuffer();
        query.append("with sel as (select Batch_App_Ref_Id, Main_Batch_App_Ref_Id, Schedule_Ref_Id, MAX(create_ts) as create_ts from trans.Run_Status where Schedule_Ref_Id is not null group by Batch_App_Ref_Id, Main_Batch_App_Ref_Id, Schedule_Ref_Id)");
        query.append(" SELECT r.Run_Status_Ref_Id,r.Start_Ts, r.Stop_Ts, r.Status_Cd,s.Status_Desc as statusDesc, res.Result_Desc, b.batch_nm, r.schedule_Ref_Id, r.batch_App_Ref_Id,a.User_First_Nm, a.User_Last_Nm, r.Main_Batch_App_Ref_Id,r.Description,r.Run_Nbr,b.Batch_Type_Cd,b.System_Ref_Id,res.Result_Cd, r.Create_User_Ref_Id, r.Result_Detail");
        query.append(" FROM trans.Run_Status r join Trans.Batch_Apps b  on b.Batch_App_Ref_Id = r.Batch_App_Ref_Id join code.Result_Codes res on res.Result_Cd = r.Result_Cd join Code.Status_Codes s on s.Status_Cd = r.Status_Cd left outer join Trans.Authorized_Users a on a.User_Ref_Id = r.Create_User_Ref_Id    join sel on r.Batch_App_Ref_Id = sel.Batch_App_Ref_Id and sel.create_ts = r.create_ts and (sel.Main_Batch_App_Ref_Id = r.Main_Batch_App_Ref_Id or r.Main_Batch_App_Ref_Id is null)");
        query.append(" WHERE r.delete_ind = 'N' order by r.Main_Batch_App_Ref_Id,r.Start_Ts,r.batch_App_Ref_Id  desc, r.Stop_Ts");
        List<Object> list = em.createNativeQuery(query.toString()).getResultList();
        log.debug("Return value is: list.size=" + (ShamenEJBUtil.isEmpty(list) ? "0" : list.size()));
        log.debug("Exiting getRunStatusListFromSchedule.");
        return list;
    }// end getRunStatusListFromSchedule

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<Object> getCollectionRunStatusList(Long batchAppRefId) throws Exception {
        log.debug("Entering getCollectionRunStatusList.");
        log.debug("This method is used to return a list of RunStatusEntities for a given collection.");
        StringBuffer query = new StringBuffer();
        query.append("SELECT r.Run_Status_Ref_Id,r.Start_Ts, r.Stop_Ts, r.Status_Cd,s.Status_Desc as statusDesc, res.Result_Desc, b.batch_nm, r.schedule_Ref_Id, r.batch_App_Ref_Id,a.User_First_Nm, a.User_Last_Nm, r.Main_Batch_App_Ref_Id,r.Description,r.Run_Nbr,b.Batch_Type_Cd,b.System_Ref_Id,res.Result_Cd, r.Create_User_Ref_Id, r.Result_Detail");
        query.append(" FROM trans.Run_Status r join Trans.Batch_Apps b  on b.Batch_App_Ref_Id = r.Batch_App_Ref_Id join code.Result_Codes res on res.Result_Cd = r.Result_Cd join Code.Status_Codes s on s.Status_Cd = r.Status_Cd left outer join Trans.Authorized_Users a on a.User_Ref_Id = r.Create_User_Ref_Id");
        query.append(" WHERE r.delete_ind = 'N' and r.Main_Batch_App_Ref_Id = ? or (r.Batch_App_Ref_Id = ? and r.Main_Batch_App_Ref_Id is null) order  by r.create_ts");
        List<Object> list = em.createNativeQuery(query.toString()).setParameter(1, batchAppRefId).setParameter(2, batchAppRefId).getResultList();
        log.debug("Return value is: list.size=" + (ShamenEJBUtil.isEmpty(list) ? "0" : list.size()));
        log.debug("Exiting getCollectionRunStatusList.");
        return list;
    }// end getCollectionRunStatusList

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Integer countRunStatus(Long batchAppRefId, String resultCode) throws Exception {
        log.debug("Entering countRunStatus.");
        log.debug("This method used to count the total number of runs for a given non-collection batchApp.");
        StringBuffer query = new StringBuffer();
        query.append("Select COUNT(*) FROM (SELECT COUNT(Run_Nbr) as RECORDS FROM trans.Run_Status r WHERE r.delete_ind = 'N' and r.Batch_App_Ref_Id = ? and r.Main_Batch_App_Ref_Id is null");
        if(null != resultCode){
            query.append(" and r.Result_Cd = '").append(resultCode).append("'");
        }// end if
        query.append(" GROUP BY Run_Nbr) as number");
        Integer result = (Integer) em.createNativeQuery(query.toString()).setParameter(1, batchAppRefId).getSingleResult();
        log.debug("Return result is: " + result);
        log.debug("Exiting countRunStatus.");
        return result;
    }// end countRunStatus

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<Object> getCollectionRunStatusListByPage(Long batchAppRefId, Long startRow, Long endRow, String resultCode) throws Exception {
        log.debug("Entering getCollectionRunStatusListByPage.");
        log.debug("This method is used to return a list of RunStatusEntities for a given collection batchApp for associated rows.");
        StringBuffer query = new StringBuffer();
        query.append("SELECT r.Run_Status_Ref_Id,r.Start_Ts, r.Stop_Ts, r.Status_Cd,s.Status_Desc as statusDesc, res.Result_Desc, b.batch_nm, r.schedule_Ref_Id, r.batch_App_Ref_Id,a.User_First_Nm, a.User_Last_Nm, r.Main_Batch_App_Ref_Id,r.Description,r.Run_Nbr,b.Batch_Type_Cd,b.System_Ref_Id,res.Result_Cd, r.Create_User_Ref_Id, r.Result_Detail");
        query.append(" FROM trans.Run_Status r join Trans.Batch_Apps b  on b.Batch_App_Ref_Id = r.Batch_App_Ref_Id join code.Result_Codes res on res.Result_Cd = r.Result_Cd join Code.Status_Codes s on s.Status_Cd = r.Status_Cd left outer join Trans.Authorized_Users a on a.User_Ref_Id = r.Create_User_Ref_Id");
        query.append(" WHERE r.delete_ind = 'N' and (r.Main_Batch_App_Ref_Id = ? or (r.Batch_App_Ref_Id = ? and r.Main_Batch_App_Ref_Id is null)) and r.Run_Nbr in (SELECT Run_Nbr FROM (SELECT ROW_NUMBER() OVER (ORDER BY run_nbr desc) AS Rownumber,Run_Nbr FROM trans.Run_Status rr WHERE rr.delete_ind = 'N' and rr.Batch_App_Ref_Id = ?");
        if(null != resultCode){
            query.append(" and rr.Result_Cd = '").append(resultCode).append("'");
        }// end if
        query.append(" group by Run_Nbr ) as t");
        query.append(" WHERE Rownumber BETWEEN ? and ?) order by r.create_ts desc");
        List<Object> list = em.createNativeQuery(query.toString()).setParameter(1, batchAppRefId).setParameter(2, batchAppRefId).setParameter(3, batchAppRefId).setParameter(4, startRow).setParameter(5, endRow).getResultList();
        log.debug("Return value is: list.size=" + (ShamenEJBUtil.isEmpty(list) ? "0" : list.size()));
        log.debug("Exiting getCollectionRunStatusListByPage.");
        return list;
    }// end getCollectionRunStatusListByPage

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<Object> getRunStatusListByPageAndResult(Long batchAppRefId, Long startRow, Long endRow, String resultCode) throws Exception {
        log.debug("Entering getRunStatusListByPageAndResult.");
        log.debug("This method is used to return a list of RunStatusEntities for a given non-collection batchApp for associated rows.");
        StringBuffer query = new StringBuffer();
        query.append("SELECT r.Run_Status_Ref_Id,r.Start_Ts, r.Stop_Ts, r.Status_Cd,s.Status_Desc as statusDesc, res.Result_Desc, b.batch_nm, r.schedule_Ref_Id, r.batch_App_Ref_Id,a.User_First_Nm, a.User_Last_Nm, r.Main_Batch_App_Ref_Id,r.Description,r.Run_Nbr,b.Batch_Type_Cd,b.System_Ref_Id,res.Result_Cd, r.Create_User_Ref_Id, r.Result_Detail");
        query.append(" FROM trans.Run_Status r join Trans.Batch_Apps b  on b.Batch_App_Ref_Id = r.Batch_App_Ref_Id join code.Result_Codes res on res.Result_Cd = r.Result_Cd join Code.Status_Codes s on s.Status_Cd = r.Status_Cd left outer join Trans.Authorized_Users a on a.User_Ref_Id = r.Create_User_Ref_Id");
        query.append(" WHERE r.delete_ind = 'N' and r.Batch_App_Ref_Id = ? and r.Main_Batch_App_Ref_Id is null and r.Run_Nbr in (SELECT Run_Nbr FROM (SELECT ROW_NUMBER() OVER (ORDER BY run_nbr desc) AS Rownumber,Run_Nbr FROM trans.Run_Status rr WHERE rr.delete_ind = 'N' and rr.Batch_App_Ref_Id = ? and rr.Main_Batch_App_Ref_Id is null");
        if(null != resultCode){
            query.append(" and rr.Result_Cd = '").append(resultCode).append("'");
        }// end if
        query.append(" group by Run_Nbr ) as t");
        query.append(" WHERE Rownumber BETWEEN ? and ?) order  by r.create_ts desc");
        List<Object> list = em.createNativeQuery(query.toString()).setParameter(1, batchAppRefId).setParameter(2, batchAppRefId).setParameter(3, startRow).setParameter(4, endRow).getResultList();
        log.debug("Return value is: list.size=" + (ShamenEJBUtil.isEmpty(list) ? "0" : list.size()));
        log.debug("Exiting getRunStatusListByPageAndResult.");
        return list;
    }// end getRunStatusListByPageAndResult

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<Object> getBatchAppRunStatusSummaries(String batchAppNm, String pastDate) throws Exception {
        log.debug("Entering getBatchAppRunStatusSummaries.");
        log.debug("This method is used to return a list of RunStatusEntities for a given non-collection batchApp.");
        StringBuffer query = new StringBuffer();
        query.append("with sel as (select Run_Nbr as Run_Nbr, MAX(create_ts) as create_ts, Batch_App_Ref_Id as batchRefId from trans.Run_Status");
        query.append(" where Batch_App_Ref_Id = (SELECT Batch_App_Ref_Id FROM Trans.Batch_Apps where Batch_Nm = ?) AND Create_Ts >= ? group by Run_Nbr, Batch_App_Ref_Id)");
        query.append(" SELECT DISTINCT TOP 50");
        query.append(" r.Run_Status_Ref_Id, r.batch_App_Ref_Id, b.Batch_Nm, r.Start_Ts, r.Stop_Ts, s.Status_Desc as Status, res.Result_Desc as Details, r.Run_Nbr,");
        query.append(" CASE WHEN r.Create_User_Ref_Id = 0 THEN 'Schedule' WHEN r.Create_User_Ref_Id = 99 THEN r.Result_Detail ELSE a.User_Id END as runFrom, b.Batch_Type_Cd");
        query.append(" from Trans.Run_Status r");
        query.append(" join Trans.Batch_Apps b  on b.Batch_App_Ref_Id = r.Batch_App_Ref_Id");
        query.append(" join code.Result_Codes res on res.Result_Cd = r.Result_Cd");
        query.append(" join Code.Status_Codes s on s.Status_Cd = r.Status_Cd");
        query.append(" left outer join Trans.Authorized_Users a on a.User_Ref_Id = r.Create_User_Ref_Id");
        query.append(" join sel on sel.Run_Nbr = r.Run_Nbr and sel.create_ts = r.create_ts and sel.batchRefId = r.Batch_App_Ref_Id");
        query.append(" order by r.Start_Ts desc");
        List<Object> list = em.createNativeQuery(query.toString()).setParameter(1, batchAppNm).setParameter(2, pastDate).getResultList();
        log.debug("Return value is: " + String.valueOf(list));
        log.debug("Exiting getBatchAppRunStatusSummaries.");
        return list;
    }// end getBatchAppRunStatusSummaries

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<Object> getRunStatusListByRunNumber(Long batchAppRefId, Long runNumber) throws Exception {
        log.debug("Entering getRunStatusList.");
        log.debug("This method is used to return a list of RunStatusEntities for a given non-collection batchApp.");
        StringBuffer query = new StringBuffer();
        query.append("SELECT r.Run_Status_Ref_Id, r.Start_Ts, r.Stop_Ts, r.Status_Cd, s.Status_Desc as statusDesc, res.Result_Desc, b.batch_nm, r.schedule_Ref_Id, r.batch_App_Ref_Id, a.User_First_Nm, a.User_Last_Nm, r.Main_Batch_App_Ref_Id, r.Description, r.Run_Nbr, b.Batch_Type_Cd, b.System_Ref_Id, res.Result_Cd, r.Create_User_Ref_Id,");
        query.append(" CASE WHEN r.Create_User_Ref_Id = 0 THEN 'Schedule' WHEN r.Create_User_Ref_Id = 99 THEN r.Result_Detail ELSE a.User_Id END as Result_Detail");
        query.append(" FROM trans.Run_Status r");
        query.append(" join Trans.Batch_Apps b on b.Batch_App_Ref_Id = r.Batch_App_Ref_Id");
        query.append(" join code.Result_Codes res on res.Result_Cd = r.Result_Cd");
        query.append(" join Code.Status_Codes s on s.Status_Cd = r.Status_Cd");
        query.append(" left outer join Trans.Authorized_Users a on a.User_Ref_Id = r.Create_User_Ref_Id");
        query.append(" WHERE (r.Main_Batch_App_Ref_Id = ? or (r.Batch_App_Ref_Id = ? or r.Main_Batch_App_Ref_Id = null))");
        query.append(" and r.Run_Nbr = ?");
        query.append(" order by r.create_ts desc");
        List<Object> list = em.createNativeQuery(query.toString()).setParameter(1, batchAppRefId).setParameter(2, batchAppRefId).setParameter(3, runNumber).getResultList();
        log.debug("Return value is: list.size=" + (ShamenEJBUtil.isEmpty(list) ? "0" : list.size()));
        log.debug("Exiting getRunStatusList.");
        return list;
    }// end getRunStatusList

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<Object> getRunStatusList(Long batchAppRefId) throws Exception {
        log.debug("Entering getRunStatusList.");
        log.debug("This method is used to return a list of RunStatusEntities for a given non-collection batchApp.");
        StringBuffer query = new StringBuffer();
        query.append("SELECT r.Run_Status_Ref_Id,r.Start_Ts, r.Stop_Ts, r.Status_Cd,s.Status_Desc as statusDesc, res.Result_Desc, b.batch_nm, r.schedule_Ref_Id, r.batch_App_Ref_Id,a.User_First_Nm, a.User_Last_Nm, r.Main_Batch_App_Ref_Id,r.Description,r.Run_Nbr,b.Batch_Type_Cd,b.System_Ref_Id,res.Result_Cd, r.Create_User_Ref_Id, r.Result_Detail");
        query.append(" FROM trans.Run_Status r join Trans.Batch_Apps b  on b.Batch_App_Ref_Id = r.Batch_App_Ref_Id join code.Result_Codes res on res.Result_Cd = r.Result_Cd join Code.Status_Codes s on s.Status_Cd = r.Status_Cd left outer join Trans.Authorized_Users a on a.User_Ref_Id = r.Create_User_Ref_Id");
        query.append(" WHERE r.delete_ind = 'N' and r.Batch_App_Ref_Id = ? and r.Main_Batch_App_Ref_Id is null order  by r.create_ts desc");
        List<Object> list = em.createNativeQuery(query.toString()).setParameter(1, batchAppRefId).getResultList();
        log.debug("Return value is: list.size=" + (ShamenEJBUtil.isEmpty(list) ? "0" : list.size()));
        log.debug("Exiting getRunStatusList.");
        return list;
    }// end getRunStatusList

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String getBatchAppName(Long batchAppRefId) throws Exception {
        log.debug("Entering getBatchAppName.");
        log.debug("This method is used to return a the name of the Batch App for the given batchAppRefId.");
        StringBuffer query = new StringBuffer();
        query.append("SELECT b.batch_nm");
        query.append(" FROM Trans.Batch_Apps b");
        query.append(" WHERE b.delete_ind = 'N' and b.Batch_App_Ref_Id = ?");
        Object result = em.createNativeQuery(query.toString()).setParameter(1, batchAppRefId).getSingleResult();
        log.debug("Return value is: " + String.valueOf(result));
        log.debug("Exiting getBatchAppName.");
        return String.valueOf(result);
    }// end getRunStatusList

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<Object> getRunStatusListForGraph(Long batchAppRefId) throws Exception {
        log.debug("Entering getRunStatusListForGraph.");
        log.debug("This method is used to return a list of RunStatusEntities for a given non-collection batchApp.");
        StringBuffer query = new StringBuffer();
        query.append("SELECT r.Run_Status_Ref_Id,r.Start_Ts, r.Stop_Ts, r.Status_Cd,r.Result_Cd,r.Run_Nbr");
        query.append(" FROM trans.Run_Status r");
        query.append(" WHERE r.delete_ind = 'N' and r.Batch_App_Ref_Id = ? and r.Main_Batch_App_Ref_Id is null and DATEDIFF(dd,r.Start_Ts,getDate()) < 91 order by r.create_ts desc");
        List<Object> list = em.createNativeQuery(query.toString()).setParameter(1, batchAppRefId).getResultList();
        log.debug("Return value is: " + String.valueOf(list));
        log.debug("Exiting getRunStatusListForGraph.");
        return list;
    }// end getRunStatusList

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<Object> getCompletedRunStatusList() throws Exception {
        log.debug("Entering getRunStatusList.");
        log.debug("This method is used to return a list of RunStatusEntities.");
        StringBuffer query = new StringBuffer();
        query.append("with sel as (select Batch_App_Ref_Id, Main_Batch_App_Ref_Id,  MAX(create_ts) as create_ts from trans.Run_Status group by Batch_App_Ref_Id, Main_Batch_App_Ref_Id)");
        query.append(" SELECT r.Run_Status_Ref_Id,r.Start_Ts, r.Stop_Ts, r.Status_Cd,s.Status_Desc as statusDesc, res.Result_Desc, b.batch_nm, r.schedule_Ref_Id, r.batch_App_Ref_Id,a.User_First_Nm, a.User_Last_Nm, r.Main_Batch_App_Ref_Id,r.Description,r.Run_Nbr,b.Batch_Type_Cd,b.System_Ref_Id,res.Result_Cd, r.Create_User_Ref_Id, r.Result_Detail");
        query.append(" FROM trans.Run_Status r join Trans.Batch_Apps b  on b.Batch_App_Ref_Id = r.Batch_App_Ref_Id join code.Result_Codes res on res.Result_Cd = r.Result_Cd join Code.Status_Codes s on s.Status_Cd = r.Status_Cd left outer join Trans.Authorized_Users a on a.User_Ref_Id = r.Create_User_Ref_Id join sel on r.Batch_App_Ref_Id = sel.Batch_App_Ref_Id and sel.create_ts = r.create_ts and (sel.Main_Batch_App_Ref_Id = r.Main_Batch_App_Ref_Id or r.Main_Batch_App_Ref_Id is null)");
        query.append(" WHERE r.delete_ind = 'N' and r.Status_Cd = 'DON' and r.Main_Batch_App_Ref_Id is null    order by r.Main_Batch_App_Ref_Id,r.Start_Ts,r.batch_App_Ref_Id  desc, r.Stop_Ts");
        List<Object> list = em.createNativeQuery(query.toString()).getResultList();
        log.debug("Return value is: " + String.valueOf(list));
        log.debug("Exiting getRunStatusList.");
        return list;
    }// end getRunStatusList

    /**
     * {@inheritDoc}
     */
    @Override
    public BatchAppEntity findBatchByRefId(Long batchRefId) throws Exception {
        log.debug("Entering findBatchByRefId.");
        log.debug("This method is used to return an instance of an object for viewing or for update. Incoming parameter is: " + String.valueOf(batchRefId));
        BatchAppEntity batchEntity = em.find(BatchAppEntity.class, batchRefId);
        // em.refresh(batchEntity);
        log.debug("Return value is: " + String.valueOf(batchEntity));
        log.debug("Exiting findBatchByRefId.");
        return batchEntity;
    }// end findBatchAppByRefId

    /**
     * This method gets the batch app with no refresh. It's optimized for performance.
     *
     * @param batchRefId
     *        the ref id of the Batch App Entity
     * @return BatchAppEntity
     * @throws Exception
     *         if an exception occurred
     */
    private BatchAppEntity findBatchByRefIdForMessage(Long batchRefId) throws Exception {
        log.debug("Entering findBatchByRefIdForMessage.");
        log.debug("This method gets the batch app with no refresh. It's optimized for performance. Incoming parameter is: " + String.valueOf(batchRefId));
        BatchAppEntity batchEntity = em.find(BatchAppEntity.class, batchRefId);
        log.debug("Return value is: " + String.valueOf(batchEntity));
        log.debug("Exiting findBatchByRefIdForMessage.");
        return batchEntity;
    }// end findBatchByRefIdForMessage

    /**
     * {@inheritDoc}
     */
    @Override
    public BatchAppEntity update(BatchAppEntity entity) throws Exception {
        log.debug("Entering update.");
        log.debug("This method is used to update an instance of an object with the database. Incoming parameter is: " + String.valueOf(entity));
        entity = em.merge(entity);
        log.debug("Exiting update.");
        return entity;
    }// end merge

    /**
     * {@inheritDoc}
     */
    @Override
    public BatchAppEntity create(BatchAppEntity entity) throws Exception {
        log.debug("Entering create.");
        log.debug("This method is used to create an instance of an object with the database. Incoming parameter is: " + String.valueOf(entity));
        entity = em.merge(entity);
        log.debug("Exiting create.");
        return entity;
    }// end create

    /**
     * {@inheritDoc}
     */
    @Override
    public Long deleteBatchApp(Long batchRefId, Long updateUserRefId) throws Exception {
        log.debug("Entering deleteBatchApp.");
        log.debug("This method is used to 'soft' delete an instance of an object in the database. Incoming parameters are: batchRefId=" + String.valueOf(batchRefId) + ", updateUserRefId=" + String.valueOf(updateUserRefId));
        BatchAppEntity batchEntity = findBatchByRefId(batchRefId);
        Long result = 0L;
        if(null != batchEntity){
            if(null != batchEntity.getCommon()){
                // Set the delete indicator and update user reference ID/timestamp before merging.
                Timestamp currentTime = new java.sql.Timestamp(System.currentTimeMillis());
                batchEntity.getCommon().setDeleteIndicator("Y");
                batchEntity.getCommon().setUpdateUserRefId(updateUserRefId);
                batchEntity.getCommon().setUpdateTime(currentTime);
                if(!ShamenEJBUtil.isEmpty(batchEntity.getSchedule())){
                    for(ScheduleEntity scheduleEntity : batchEntity.getSchedule()){
                        scheduleEntity.getCommon().setDeleteIndicator("Y");
                        scheduleEntity.getCommon().setUpdateUserRefId(updateUserRefId);
                        scheduleEntity.getCommon().setUpdateTime(currentTime);
                    }// end for
                }// end if
                em.merge(batchEntity);
                em.createNativeQuery("UPDATE trans.Batch_Apps_Collection_Xref SET Delete_Ind = ?, Update_User_Ref_Id = ?, Update_Ts = ? WHERE Main_Batch_App_Ref_Id = ? OR Assoc_Batch_App_Ref_Id = ?").setParameter(1, "Y").setParameter(2, updateUserRefId).setParameter(3, new java.sql.Timestamp(System.currentTimeMillis())).setParameter(4, batchRefId).setParameter(5, batchRefId).executeUpdate();
            }else{
                log.warn("The embedded common entity model is null in the batch app entity. The delete fails!");
            }// end if
        }else{
            log.warn("Unable to find the batch app model with the entity manager! Record will not be deleted. Record primary key is " + String.valueOf(batchRefId) + ", and the update user reference ID is " + String.valueOf(updateUserRefId));
        }// end if/else
        result = batchEntity.getController() != null ? batchEntity.getController().getControllerRefId() : 0L;
        log.debug("Return value is: " + String.valueOf(result));
        log.debug("Exiting deleteBatchApp.");
        return result;
    }// end deleteBatchApp

    /**
     * {@inheritDoc}
     */
    @Override
    public Long updateBatchAppActiveInd(String activeInd, Long batchRefId, Long updateUserRefId) throws Exception {
        log.debug("Entering updateBatchAppActiveInd.");
        log.debug("This method is used to update all batch app objects in the database to Active or Inactive based on parameter. Incoming parameters are: activeInd=" + String.valueOf(activeInd) + ", updateUserRefId=" + String.valueOf(updateUserRefId));
        em.createQuery("UPDATE ScheduleEntity s SET s.activeInd = ?1, s.common.updateUserRefId = ?2, s.common.updateTime = ?3 WHERE s.batchApp.batchAppRefId = ?4").setParameter(1, activeInd).setParameter(2, updateUserRefId).setParameter(3, new java.sql.Timestamp(System.currentTimeMillis())).setParameter(4, batchRefId).executeUpdate();
        BatchAppEntity found = findBatchByRefId(batchRefId);
        Long result = 0L;
        if(null != found){
            result = found.getController() != null ? found.getController().getControllerRefId() : 0L;
        }// end if
        log.debug("Return value is: " + String.valueOf(result));
        log.debug("Exiting updateBatchAppActiveInd.");
        return result;
    }// end updateBatchAppActiveInd

    /**
     * {@inheritDoc}
     */
    @Override
    public Long updateBatchAppActiveInd(String activeInd, Long batchRefId, Long scheduleRefId, Long updateUserRefId) throws Exception {
        log.debug("Entering updateBatchAppActiveInd.");
        log.debug("This method updates the Active Ind on Schedule Entity for a Batch App Entity. Incoming parameters are: activeInd=" + String.valueOf(activeInd) + ", updateUserRefId=" + String.valueOf(updateUserRefId));
        em.createQuery("UPDATE ScheduleEntity s SET s.activeInd = ?1, s.common.updateUserRefId = ?2, s.common.updateTime = ?3 WHERE s.scheduleRefId = ?4").setParameter(1, activeInd).setParameter(2, updateUserRefId).setParameter(3, new java.sql.Timestamp(System.currentTimeMillis())).setParameter(4, scheduleRefId).executeUpdate();
        BatchAppEntity found = findBatchByRefId(batchRefId);
        Long result = 0L;
        if(null != found){
            result = found.getController() != null ? found.getController().getControllerRefId() : 0L;
        }// end if
        log.debug("Return value is: " + String.valueOf(result));
        log.debug("Exiting updateBatchAppActiveInd.");
        return result;
    }// end updateBatchAppActiveInd

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateAllBatchApp(String activeInd, Long updateUserRefId) throws Exception {
        log.debug("Entering updateAllBatchApp.");
        log.debug("This method is used to update all batch app objects in the database to Active or Inactive based on parameter. Incoming parameters are: activeInd=" + String.valueOf(activeInd) + ", updateUserRefId=" + String.valueOf(updateUserRefId));
        em.createNativeQuery("UPDATE Trans.Schedules SET Active_Ind = ? , Update_User_Ref_Id = ? , Update_Ts = ?  WHERE Batch_App_Ref_Id IN (SELECT b.Batch_App_Ref_Id from trans.Batch_Apps b where b.Batch_Type_Cd != 'COL') AND Delete_Ind = 'N'").setParameter(1, activeInd).setParameter(2, updateUserRefId).setParameter(3, new java.sql.Timestamp(System.currentTimeMillis())).executeUpdate();
        log.debug("Exiting updateAllBatchApp.");
    }// end updateAllBatchApp

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateAllBatchAppCollections(String activeInd, Long updateUserRefId) throws Exception {
        log.debug("Entering updateAllBatchApp.");
        log.debug("This method is used to update all batch app objects in the database to Active or Inactive based on parameter. Incoming parameters are: activeInd=" + String.valueOf(activeInd) + ", updateUserRefId=" + String.valueOf(updateUserRefId));
        em.createNativeQuery("UPDATE Trans.Schedules SET Active_Ind = ? , Update_User_Ref_Id = ? , Update_Ts = ?  WHERE Batch_App_Ref_Id IN (SELECT b.Batch_App_Ref_Id from trans.Batch_Apps b where b.Batch_Type_Cd = 'COL') AND Delete_Ind = 'N'").setParameter(1, activeInd).setParameter(2, updateUserRefId).setParameter(3, new java.sql.Timestamp(System.currentTimeMillis())).executeUpdate();
        log.debug("Exiting updateAllBatchApp.");
    }// end updateAllBatchAppCollections

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateAllControllerBatchApp(String activeInd, Long controllerRefId, Long updateUserRefId) throws Exception {
        log.debug("Entering updateAllBatchApp.");
        log.debug("This method is used to update all batch app objects in the database to Active or Inactive based on parameter. Incoming parameters are: activeInd=" + String.valueOf(activeInd) + ", updateUserRefId=" + String.valueOf(updateUserRefId));
        em.createNativeQuery("UPDATE Trans.Schedules set Active_Ind = ?, Update_User_Ref_Id = ?, Update_Ts = ? WHERE Batch_App_Ref_Id IN (SELECT b.Batch_App_Ref_Id from Trans.Batch_Apps b WHERE b.Controller_Ref_Id = ?) AND Delete_Ind = 'N'").setParameter(1, activeInd).setParameter(2, updateUserRefId).setParameter(3, new java.sql.Timestamp(System.currentTimeMillis())).setParameter(4, controllerRefId).executeUpdate();
        log.debug("Exiting updateAllBatchApp.");
    }// end updateAllControllerBatchApp

    /**
     * {@inheritDoc}
     */
    @Override
    public RunStatusEntity findRunStatusByRefId(Long refId) throws Exception {
        log.debug("Entering findRunStatusByRefId.");
        log.debug("This method is used to return an instance of an object for viewing or for update. Incoming parameter is: " + String.valueOf(refId));
        RunStatusEntity runStatusEntity = em.find(RunStatusEntity.class, refId);
        log.debug("Return value is: runStatus=" + String.valueOf(runStatusEntity));
        log.debug("Exiting findRunStatusByRefId.");
        return runStatusEntity;
    }// end findBatchAppByRefId

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public boolean findRunStatusByExample(RunStatusEntity entity) {
        log.debug("Entering findRunStatusByExample.");
        log.debug("This method is used to return a boolean value of true|false whether Run Status record already exist in database. Incoming parameter is: " + String.valueOf(entity));
        boolean result = true;
        try{
            em.createNamedQuery("RunStatusEntity.FIND_BY_EXAMPLE", RunStatusEntity.class).setParameter("batchAppRefId", entity.getBatchApp().getBatchAppRefId()).setParameter("scheduleRefId", entity.getSchedule() == null ? null : entity.getSchedule().getScheduleRefId()).setParameter("startTime", entity.getStartTime()).setParameter("stopTime", entity.getStopTime()).setParameter("statusCd", entity.getStatus().getStatusCd()).setParameter("resultCd", entity.getResult().getResultCd()).setParameter("description", entity.getDescription()).setParameter("createUserRefId", entity.getCommon().getCreateUserRefId()).setParameter("createTime", entity.getCommon().getCreateTime()).getSingleResult();
        }catch(NoResultException e){
            log.debug("No result returned for query.");
            result = false;
        }catch(Exception e){
            log.debug("Exception caught while trying to check if a Run Status record already exist. e=" + e.getMessage());
            result = false;
        }// end try/catch
        log.debug("Return value is: " + String.valueOf(result));
        log.debug("Exiting findRunStatusByExample.");
        return result;
    }// end findBatchByExample

    /**
     * {@inheritDoc}
     */
    @Override
    public void mergeRunStatusFromMessage(RunStatusEntity entity) throws Exception {
        log.debug("Entering mergeRunStatusFromMessage.");
        log.debug("This method is used to merge an instance of an object with the database. Incoming parameter is: " + String.valueOf(entity));
        // first check to insure this run status has not been written already. This can happen when MQ server sends the message
        // multiple times due to internal errors.
        if(!checkDuplicateRunStatus(entity)){
            createRunStatusFromMessage(entity);
        }else{
            log.warn("The run status was already written to the DB.  This run status message will be discarded.  RunStatus is: " + String.valueOf(entity));
        }// end if-else
        log.debug("Exiting mergeRunStatusFromMessage.");
    }// end mergeRunStatusFromMessage

    /**
     * {@inheritDoc}
     */
    @Override
    public void mergeRunStatusFromMessageForBatchStart(RunStatusEntity entity, String batchTypeCd) throws Exception {
        log.debug("Entering mergeRunStatusFromMessageForBatchStart.");
        log.debug("This method is used to merge an instance of an object with the database. Incoming parameter are: runStatus= " + String.valueOf(entity) + ", batchTypeCode= " + String.valueOf(batchTypeCd));
        BatchAppEntity batch = findBatchByRefIdForMessage(entity.getBatchApp().getBatchAppRefId());
        batch.addRunStatus(entity);
        entity.setBatchApp(batch);
        em.merge(entity);
        jmsBean.startBatchApp(entity, batchTypeCd, null, " ");
    }// end mergeRunStatusFromMessageForBatchStart

    /**
     * {@inheritDoc}
     */
    @Override
    public BatchAppEntity deleteRunStatusByBatchAppRefId(Long refId, Boolean collection) throws Exception {
        log.debug("Entering deleteRunStatusByBatchAppRefId.");
        log.debug("This method deletes all Run Status records associated with a Batch App. Incoming parameter is: " + String.valueOf(refId));
        // if it is a collection, delete both the collection run status and the children for this collection only
        if(collection){
            em.createNativeQuery("DELETE FROM trans.Run_Status where batch_App_Ref_Id = ?1 or main_batch_app_ref_id = ?").setParameter(1, refId).setParameter(2, refId).executeUpdate();
        }else{
            // if not a collection, delete only this batch app's runStatuses that are not part of a collection
            em.createNativeQuery("DELETE FROM trans.Run_Status where batch_App_Ref_Id = ?1 and Main_Batch_App_Ref_Id is null").setParameter(1, refId).executeUpdate();
        }// end if-else
        BatchAppEntity batch = findBatchByRefId(refId);
        log.debug("Return value is: " + String.valueOf(batch));
        log.debug("Exiting deleteRunStatusByBatchAppRefId.");
        return batch;
    }// end mergeRunStatus

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer deleteOldRunStatus() throws Exception {
        log.debug("Entering deleteOldRunStatus.");
        log.debug("This method deletes all Run Status records from the database that are deleteIndicator = Y.");
        int result = em.createNativeQuery("delete from trans.Run_Status where run_nbr + Batch_App_Ref_Id not in (select Batch_App_Ref_Id + max(Run_nbr) from trans.Run_Status group by Batch_App_Ref_Id,Main_Batch_App_Ref_Id)").executeUpdate();
        // em.flush();
        log.debug("Return result is: " + String.valueOf(result));
        log.debug("Exiting deleteOldRunStatus.");
        return result;
    }// end deleteOldRunStatus

    /**
     * {@inheritDoc}
     */
    @Override
    public ScheduleEntity findScheduleByRefId(Long scheduleRefId) throws Exception {
        log.debug("Entering findScheduleByRefId.");
        log.debug("This method is used to return an instance of an object for viewing or for update. Incoming parameter is: " + String.valueOf(scheduleRefId));
        ScheduleEntity scheduleEntity = em.find(ScheduleEntity.class, scheduleRefId);
        // em.refresh(batchEntity);
        log.debug("Return schedule is: " + String.valueOf(scheduleEntity));
        log.debug("Exiting findScheduleByRefId.");
        return scheduleEntity;
    }// end findBatchAppByRefId

    /**
     * {@inheritDoc}
     */
    @Override
    public ScheduleEntity deleteSchedule(Long scheduleRefId, Long updateUserRefId) throws Exception {
        log.debug("Entering deleteSchedule");
        log.debug("This method is used to 'soft' delete an instance of an object in the database.");
        log.debug("Entry parameters are: scheduleRefId=" + String.valueOf(scheduleRefId) + ", updateUserRefId=" + String.valueOf(updateUserRefId));
        ScheduleEntity appEntity = findScheduleByRefId(scheduleRefId);
        if(null != appEntity){
            if(null != appEntity.getCommon()){
                // Set the delete indicator and update user reference ID/timestamp before merging.
                appEntity.getCommon().setDeleteIndicator("Y");
                appEntity.getCommon().setUpdateUserRefId(updateUserRefId);
                appEntity.getCommon().setUpdateTime(new java.sql.Timestamp(System.currentTimeMillis()));
                appEntity = em.merge(appEntity);
            }else{
                log.warn("The embedded common entity model is null in the application entity. The delete fails!");
            }// end if
        }else{
            log.warn("Unable to find the Schedule model with the entity manager! Record will not be deleted. Record primary key is " + String.valueOf(scheduleRefId) + ", and the update user reference ID is " + String.valueOf(updateUserRefId));
        }// end if/else
        log.debug("Return value is: appEntity=" + String.valueOf(appEntity));
        log.debug("Exiting deleteApplication");
        return appEntity;
    }// end deleteApplication

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean checkDuplicateRunStatus(RunStatusEntity entity) throws Exception {
        log.debug("Entering checkDuplicateRunStatus.");
        log.debug("This method checks to see if the run status already exists. ");
        Boolean found = true;
        try{
            StringBuilder sb = new StringBuilder();
            sb.append("Select run_status_Ref_Id from trans.run_status where");
            sb.append(" batch_app_ref_id = ? and start_ts = ? and stop_ts = ? and status_cd = ? and result_cd = ? and run_nbr = ? and description = ?");
            sb.append(entity.getSchedule() != null ? " and schedule_ref_id = ?" : " and schedule_ref_id IS NULL");
            sb.append(ShamenEJBUtil.isNotNullAndZero(entity.getMainBatchAppRefId()) ? " and main_batch_app_ref_id = ?" : " and main_batch_app_ref_id IS NULL");
            sb.append(" and Delete_Ind = 'N'");
            Query query = em.createNativeQuery(sb.toString());
            query.setParameter(1, entity.getBatchApp().getBatchAppRefId());
            query.setParameter(2, entity.getStartTime());
            query.setParameter(3, entity.getStopTime());
            query.setParameter(4, entity.getStatus().getStatusCd());
            query.setParameter(5, entity.getResult().getResultCd());
            query.setParameter(6, entity.getRunNumber());
            query.setParameter(7, entity.getDescription());
            if(entity.getSchedule() != null){
                query.setParameter(8, entity.getSchedule().getScheduleRefId());
                if(ShamenEJBUtil.isNotNullAndZero(entity.getMainBatchAppRefId())){
                    query.setParameter(9, entity.getMainBatchAppRefId());
                }// end if
            }else{
                if(ShamenEJBUtil.isNotNullAndZero(entity.getMainBatchAppRefId())){
                    query.setParameter(8, entity.getMainBatchAppRefId());
                }// end if
            }// end if/else
            query.getSingleResult();
        }catch(NoResultException e){
            found = false;
        }// end try-catch
        log.debug("Return result is: " + String.valueOf(found));
        log.debug("Exiting checkDuplicateRunStatus.");
        return found;
    }// end checkDuplicateRunStatus

    /**
     * This method inserts a record into the DB using native SQL.
     *
     * @param entity
     *        RunStatus to insert
     * @throws Exception
     *         if an exception occurred
     */
    private void createRunStatusFromMessage(RunStatusEntity entity) throws Exception {
        log.debug("Entering createRunStatusFromMessage.");
        log.debug("This method inserts a record into the DB using native SQL. ");
        em.createNativeQuery("INSERT INTO Trans.Run_Status (Batch_App_Ref_Id,Main_Batch_App_Ref_Id,Schedule_Ref_Id,Start_Ts,Stop_Ts,Status_Cd,Result_Cd,Result_Detail,Description,Run_Nbr,Create_User_Ref_Id,Create_Ts,Delete_Ind) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?) ").setParameter(1, entity.getBatchApp().getBatchAppRefId()).setParameter(2, entity.getMainBatchAppRefId()).setParameter(3, (entity.getSchedule() == null ? null : entity.getSchedule().getScheduleRefId())).setParameter(4, entity.getStartTime()).setParameter(5, entity.getStopTime()).setParameter(6, entity.getStatus().getStatusCd()).setParameter(7, entity.getResult().getResultCd()).setParameter(8, entity.getResultDetail()).setParameter(9, entity.getDescription()).setParameter(10, entity.getRunNumber()).setParameter(11, entity.getCommon().getCreateUserRefId()).setParameter(12, entity.getCommon().getCreateTime()).setParameter(13, "N").executeUpdate();
        log.debug("Exiting createRunStatusFromMessage.");
    }// end createRunStatusFromMessage

    /**
     * {@inheritDoc}
     */
    @Override
    public String partOfCollection(Long batchRefId) throws Exception {
        log.debug("Entering partOfCollection.");
        log.debug("This method selects 'Y' if a record exist for the batch ref id equal to associated batch app ref id and 'N' if not. Incoming parameter is batchRefId= " + String.valueOf(batchRefId));
        String result = "Y";
        List resultList = em.createNativeQuery("Select Collection_Ref_Id from trans.Batch_Apps_Collection_Xref where Assoc_Batch_App_Ref_Id = ? and Delete_Ind = 'N'").setParameter(1, batchRefId).getResultList();
        if(ShamenEJBUtil.isEmpty(resultList)){
            result = "N";
        }// end if
        log.debug("Return result is: " + String.valueOf(result));
        log.debug("Exiting partOfCollection.");
        return result;
    }// end partOfCollection
}// end class
