/**
 *
 */
package gov.doc.isu.shamen.ejb.beans.view;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.ejb.Local;

import gov.doc.isu.shamen.interfaces.Scheduleable;
import gov.doc.isu.shamen.jpa.entity.BatchAppEntity;
import gov.doc.isu.shamen.jpa.entity.RunStatusEntity;
import gov.doc.isu.shamen.jpa.entity.ScheduleEntity;

/**
 * This is the local business interface for the {@link gov.doc.isu.shamen.ejb.beans.BatchAppBean}.
 *
 * @author Joseph Burris JCCC
 * @author <strong>Steven Skinner</strong> JCCC
 */
@Local
public interface BatchAppBeanLocal extends Serializable {

    /**
     * This method is used to return a list of {@link BatchAppEntity}s loaded with everything needed for the batch app list screen.
     *
     * @return A {@link List} of {@link BatchAppEntity}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public List<BatchAppEntity> getBatchList() throws Exception;

    /**
     * This method is used to return a list of {@link BatchAppEntity}s loaded with everything needed for the batch app client tag.
     *
     * @param applicationRefId
     *        the Application ref id
     * @return A {@link List} of {@link BatchAppEntity}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public List<BatchAppEntity> getAllBatchesAndCollectionsForApplication(Long applicationRefId) throws Exception;

    /**
     * This method is used to return a list of {@link BatchAppEntity}s loaded with everything needed for a client web application.
     *
     * @param applicationName
     *        the application name
     * @param applicationEnvironment
     *        the environment of the application
     * @return A {@link List} of {@link BatchAppEntity}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public List<BatchAppEntity> getBatchList(String applicationName, String applicationEnvironment) throws Exception;

    /**
     * This method is used to return a list of {@link Object}s loaded with everything needed.
     *
     * @return A {@link List} of {@link BatchAppEntity}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public List<BatchAppEntity> getAllScheduledBatchAppsAndCollections() throws Exception;

    /**
     * This method is used to return a list of {@link Object}s loaded with everything needed for the batch app collection list screen.
     *
     * @return A {@link List} of {@link BatchAppEntity}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public List<BatchAppEntity> getBatchCollectionList() throws Exception;

    /**
     * This method is used to return a list of {@link RunStatusEntity}s.
     *
     * @return A {@link List} of {@link Object}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public List<Object> getRunStatusList() throws Exception;

    /**
     * Loads a List of Object for use by the batch listing to determine if jobs are on schedule.
     *
     * @return A {@link List} of {@link Object}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public List<Object> getRunStatusListFromSchedule() throws Exception;

    /**
     * This method is used to return a list of {@link RunStatusEntity}s for a given collection.
     *
     * @param batchAppRefId
     *        collection's batchAppRefId
     * @return A {@link List} of {@link Object}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public List<Object> getCollectionRunStatusList(Long batchAppRefId) throws Exception;

    /**
     * This method returns the list of {@link RunStatusEntity}s for a given collection batchApp for the associated rows.
     * 
     * @param batchAppRefId
     *        the batch app ref id
     * @param startRow
     *        the start row
     * @param endRow
     *        the end row
     * @param resultCode
     *        the result code to filter by
     * @return List<Object> run statuses
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public List<Object> getCollectionRunStatusListByPage(Long batchAppRefId, Long startRow, Long endRow, String resultCode) throws Exception;

    /**
     * This method returns the list of {@link RunStatusEntity}s for a given non-collection batchApp for the associated rows.
     * 
     * @param batchAppRefId
     *        the batch app ref id
     * @param startRow
     *        the start row
     * @param endRow
     *        the end row
     * @param resultCode
     *        the result code to filter by
     * @return List<Object> run statuses
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public List<Object> getRunStatusListByPageAndResult(Long batchAppRefId, Long startRow, Long endRow, String resultCode) throws Exception;

    /**
     * This method is used to return a list of {@link RunStatusEntity}s for a given non-collection batchApp by the provided run number.
     *
     * @param runNumber
     *        runNumber
     * @return A {@link List} of {@link Object}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public List<Object> getRunStatusListByRunNumber(Long batchAppRefId, Long runNumber) throws Exception;
    
    /**
     * This method is used to return a list of {@link RunStatusEntity}s for a given non-collection batchApp.
     *
     * @param batchAppRefId
     *        batchAppRefId
     * @return A {@link List} of {@link Object}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public List<Object> getRunStatusList(Long batchAppRefId) throws Exception;

    /**
     * This method is used to return a list of {@link RunStatusEntity}s for a given non-collection batchApp.
     *
     * @param batchAppRefId
     *        batchAppRefId
     * @return A {@link List} of {@link Object}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public List<Object> getRunStatusListForGraph(Long batchAppRefId) throws Exception;

    /**
     * This method is used to return a list of {@link RunStatusSummaryModel}s for a batchApp.
     *
     * @param batchAppNm
     *        batchAppNm
     * @param pastDate
     *        pastDate
     * @return A {@link List} of {@link Object}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public List<Object> getBatchAppRunStatusSummaries(String batchAppNm, String pastDate) throws Exception;

    /**
     * This method is used to return a the name of the Batch App for the given batchAppRefId.
     * 
     * @param batchAppRefId
     *        batchAppRefId
     * @return String
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public String getBatchAppName(Long batchAppRefId) throws Exception;

    /**
     * This method used to count the total number of runs for a given non-collection batchApp.
     * 
     * @param batchAppRefId
     *        batchAppRefId
     * @param resultCode
     *        the result code to filter by
     * @return Integer the count of runs
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public Integer countRunStatus(Long batchAppRefId, String resultCode) throws Exception;

    /**
     * This method is used to return a list of {@link RunStatusEntity}s for all smart george batch apps for all systems
     *
     * @return A {@link List} of {@link Object}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public List<Object> getCompletedRunStatusList() throws Exception;

    /**
     * This method is used to return an instance of an object for viewing or for update.
     *
     * @param batchAppRefId
     *        The primary key of the object to retrieve.
     * @return An {@link BatchAppEntity}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public BatchAppEntity findBatchByRefId(Long batchAppRefId) throws Exception;

    /**
     * This method is used to return an instance of an object for viewing or for update.
     *
     * @param scheduleRefId
     *        The primary key of the object to retrieve.
     * @return An {@link ScheduleEntity}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public ScheduleEntity findScheduleByRefId(Long scheduleRefId) throws Exception;

    /**
     * This method is used to 'soft' delete an instance of an object in the database.
     * 
     * @param scheduleRefId
     *        The primary key of the object to delete.
     * @param updateUserRefId
     *        The primary key of the system user enacting the update.
     * @return An {@link ScheduleEntity}
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public ScheduleEntity deleteSchedule(Long scheduleRefId, Long updateUserRefId) throws Exception;

    /**
     * This method is used to update an instance of an object with the database.
     *
     * @param entity
     *        The object to update.
     * @return An {@link BatchAppEntity}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public BatchAppEntity update(BatchAppEntity entity) throws Exception;

    /**
     * This method is used to create an instance of an object with the database.
     *
     * @param entity
     *        The object to create.
     * @return An {@link BatchAppEntity}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public BatchAppEntity create(BatchAppEntity entity) throws Exception;

    /**
     * This method is used to 'soft' delete an instance of an object in the database.
     *
     * @param batchRefId
     *        The primary key of the record to delete.
     * @param updateUserRefId
     *        The primary key of the system user enacting the update.
     * @return Long
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public Long deleteBatchApp(Long batchRefId, Long updateUserRefId) throws Exception;

    /**
     * This method is used to update all batch app objects in the database to Active or Inactive based on parameter.
     *
     * @param activeInd
     *        The value of avtiveInd to set
     * @param updateUserRefId
     *        The primary key of the system user enacting the update.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public void updateAllBatchApp(String activeInd, Long updateUserRefId) throws Exception;

    /**
     * This method is used to update all batch app collection objects in the database to Active or Inactive based on parameter.
     *
     * @param activeInd
     *        The value of avtiveInd to set
     * @param updateUserRefId
     *        The primary key of the system user enacting the update.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public void updateAllBatchAppCollections(String activeInd, Long updateUserRefId) throws Exception;

    /**
     * This method is used to update all batch app objects in the database to Active or Inactive based on parameter.
     *
     * @param activeInd
     *        The value of avtiveInd to set
     * @param controllerRefId
     *        The primary key of the controller associated with batchApp
     * @param updateUserRefId
     *        The primary key of the system user enacting the update.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public void updateAllControllerBatchApp(String activeInd, Long controllerRefId, Long updateUserRefId) throws Exception;

    /**
     * This method is checks if Run Status record already exist in database.
     *
     * @param entity
     *        The {@link RunStatusEntity} to compare.
     * @return boolean true|false
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public boolean findRunStatusByExample(RunStatusEntity entity) throws Exception;

    /**
     * This method is used to return an instance of an object for viewing or for update.
     *
     * @param refId
     *        The primary key of the object to retrieve.
     * @return An {@link RunStatusEntity}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public RunStatusEntity findRunStatusByRefId(Long refId) throws Exception;

    /**
     * This method is used to merge an instance of an object with the database.
     *
     * @param entity
     *        The object to merge.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public void mergeRunStatusFromMessage(RunStatusEntity entity) throws Exception;

    /**
     * This method is used to merge an instance of an object with the database. It will save a "Starting" run status, then send a message to the controller to start the batch job.
     *
     * @param entity
     *        The object to merge.
     * @param batchTypeCd
     *        batch type
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public void mergeRunStatusFromMessageForBatchStart(RunStatusEntity entity, String batchTypeCd) throws Exception;

    /**
     * This method deletes all Run Status records associated with a Batch App.
     *
     * @param batchAppRefId
     *        The ref id of the batch App Entity associated with the Run Status Entities to delete.
     * @param collection
     *        true/false indicator of whether it's a collection or not.
     * @return An {@link BatchAppEntity}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public BatchAppEntity deleteRunStatusByBatchAppRefId(Long batchAppRefId, Boolean collection) throws Exception;

    /**
     * This method updates the Active Ind on Batch App Entity.
     *
     * @param activeInd
     *        the value to change Actvie ind to
     * @param batchRefId
     *        the batch id of record to update
     * @param updateUserRefId
     *        the user id of user updating record
     * @return Long
     * @throws Exception
     *         if an exception occurred
     */
    public Long updateBatchAppActiveInd(String activeInd, Long batchRefId, Long updateUserRefId) throws Exception;

    /**
     * This method updates the Active Ind on Schedule Entity for a Batch App Entity.
     *
     * @param activeInd
     *        the value to change Actvie ind to
     * @param batchRefId
     *        the batch id of the schedule record
     * @param scheduleRefId
     *        the schedule id of record to update
     * @param updateUserRefId
     *        the user id of user updating record
     * @return Long
     * @throws Exception
     *         if an exception occurred
     */
    public Long updateBatchAppActiveInd(String activeInd, Long batchRefId, Long scheduleRefId, Long updateUserRefId) throws Exception;

    /**
     * This method hard deletes any run statuses that have already been soft deleted. This is done to keep an inordinate amount of records from jamming the table.
     *
     * @return numberOfRecordsDeleted
     * @throws Exception
     *         if an exception occurred
     */
    public Integer deleteOldRunStatus() throws Exception;

    /**
     * This method checks to see if the run status already exists.
     *
     * @param entity
     *        The RunStatusEntity to check.
     * @return exists
     * @throws Exception
     *         if an exception occurred
     */
    public Boolean checkDuplicateRunStatus(RunStatusEntity entity) throws Exception;

    /**
     * This method selects 'Y' if a record exist for the batch ref id equal to associated batch app ref id and 'N' if not.
     *
     * @param batchRefId
     *        the batch app ref id to compare to
     * @return String
     * @throws Exception
     *         if an exception occurred
     */
    public String partOfCollection(Long batchRefId) throws Exception;

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
    public List<Timestamp> calculateRunTimes(Scheduleable app, Timestamp fromTs, Timestamp toTs);

    /**
     * This method performs all the calculations and comparisons to determine if a job is on schedule. If so, it also determines its status.
     *
     * @param batchList
     *        (required)
     * @return List<BatchAppEntity>
     */
    public List<BatchAppEntity> checkOnSchedule(List<BatchAppEntity> batchList);

    /**
     * This method performs all the calculations and comparisons to determine if a job is on schedule. If so, it also determines its status.
     *
     * @param scheduleList
     *        (required)
     * @param batchAppRefId
     *        (required)
     * @return List<BatchAppEntity>
     */
    public List<ScheduleEntity> checkOnScheduleForBatchDetail(List<ScheduleEntity> scheduleList, Long batchAppRefId);
}// end interface
