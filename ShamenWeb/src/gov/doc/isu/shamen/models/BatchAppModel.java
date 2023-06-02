/**
 * @author <strong>Steven Skinner</strong> JCCC, Sep 10, 2015
 */
package gov.doc.isu.shamen.models;

import static gov.doc.isu.dwarf.resources.Constants.EMPTY_STRING;
import static gov.doc.isu.dwarf.resources.Constants.NEW_LINE;

import java.util.ArrayList;
import java.util.List;

import gov.doc.isu.dwarf.model.CommonModel;
import gov.doc.isu.dwarf.util.AppUtil;
import gov.doc.isu.dwarf.util.StringUtil;

/**
 * Model object to hold the Batch Application Record
 *
 * @author <strong>Steven Skinner</strong> JCCC, Sep 10, 2015
 */
public class BatchAppModel extends CommonModel implements Cloneable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Long batchRefId;
    private String name;
    private String fileLocation;
    private String fileName;
    private String description;
    private String type;
    private String typeDesc;
    private String partOfCollection = "N";

    private ControllerModel controller;
    private ScheduleModel scheduleModel;
    private List<ScheduleModel> scheduleList;
    private List<RunStatusModel> runStatuses;
    private ApplicationModel application;
    private SystemModel system;
    private boolean assignedController = true;
    private Boolean appState = true;
    private List<BatchAppCollectionModel> batchAppCollection = new ArrayList<BatchAppCollectionModel>();
    private AuthorizedUserModel pointOfContact;
    private Long executionCount;
    private List<String> lastRunStatus = new ArrayList<String>();

    /**
     * Constructor
     *
     * @param batchRefId
     *        batchRefId
     * @author <strong>Shane Duncan</strong> JCCC, Oct 23, 2015
     */
    public BatchAppModel(Long batchRefId) {
        super();
        this.batchRefId = batchRefId;
    }// end constructor

    /**
     * Constructor
     *
     * @param type
     *        type
     * @author <strong>Steven Skinner</strong> JCCC, Aug 08,2017
     */
    public BatchAppModel(String type) {
        super();
        this.type = type;
        this.fileName = EMPTY_STRING;
        this.fileLocation = EMPTY_STRING;
        this.controller = new ControllerModel();
        this.application = new ApplicationModel();
        this.system = new SystemModel();
    }// end constructor

    /**
     * Constructor
     *
     * @param name
     *        name
     * @param type
     *        type
     * @author <strong>Steven Skinner</strong> JCCC, Oct 19,2017
     */
    public BatchAppModel(String name, String type) {
        super();
        this.name = name;
        this.type = type;

    }// end constructor

    /**
     * Default Constructor
     */
    public BatchAppModel() {
        super();

    }// end constructor

    /**
     * @return the batchRefId
     */
    public Long getBatchRefId() {
        return null != batchRefId ? batchRefId : 0L;
    }// end getBatchRefId

    /**
     * @param batchRefId
     *        the batchRefId to set
     */
    public void setBatchRefId(Long batchRefId) {
        this.batchRefId = batchRefId;
    }// end setBatchRefId

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }// end getName

    /**
     * @param name
     *        the name to set
     */
    public void setName(String name) {
        this.name = name;
    }// end setName

    /**
     * @return the fileLocation
     */
    public String getFileLocation() {
        return fileLocation;
    }// end getFileLocation

    /**
     * @param fileLocation
     *        the fileLocation to set
     */
    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }// end setFileLocation

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }// end getFileName

    /**
     * @param fileName
     *        the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }// end setFileName

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }// end getDescription

    /**
     * @param description
     *        the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }// end setDescription

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }// end getType

    /**
     * @param type
     *        the type to set
     */
    public void setType(String type) {
        this.type = type;
    }// end setType

    /**
     * @return the typeDesc
     */
    public String getTypeDesc() {
        return typeDesc;
    }// end getTypeDesc

    /**
     * @param typeDesc
     *        the typeDesc to set
     */
    public void setTypeDesc(String typeDesc) {
        this.typeDesc = typeDesc;
    }// end setTypeDesc

    /**
     * @return the controller
     */
    public ControllerModel getController() {
        return controller;
    }// end getController

    /**
     * @param controller
     *        the controller to set
     */
    public void setController(ControllerModel controller) {
        this.controller = controller;
    }// end setController

    /**
     * @return the scheduleModel
     */
    public ScheduleModel getScheduleModel() {
        return scheduleModel;
    }// end getScheduleModel

    /**
     * @param scheduleModel
     *        the scheduleModel to set
     */
    public void setScheduleModel(ScheduleModel scheduleModel) {
        this.scheduleModel = scheduleModel;
    }// end setScheduleModel

    /**
     * @return the scheduleList
     */
    public List<ScheduleModel> getScheduleList() {
        return scheduleList;
    }// end getScheduleList

    /**
     * @param scheduleList
     *        the scheduleList to set
     */
    public void setScheduleList(List<ScheduleModel> scheduleList) {
        this.scheduleList = scheduleList;
    }// end setScheduleList

    /**
     * @return the runStatuses
     */
    public List<RunStatusModel> getRunStatuses() {
        return runStatuses;
    }// end getRunStatuses

    /**
     * @param runStatuses
     *        the runStatuses to set
     */
    public void setRunStatuses(List<RunStatusModel> runStatuses) {
        this.runStatuses = runStatuses;
    }// end setRunStatuses

    /**
     * @return the application
     */
    public ApplicationModel getApplication() {
        return application;
    }// end getApplication

    /**
     * @param application
     *        the application to set
     */
    public void setApplication(ApplicationModel application) {
        this.application = application;
    }// end setApplication

    /**
     * @return the assignedController
     */
    public boolean isAssignedController() {
        return assignedController;
    }// end isAssignedController

    /**
     * @param assignedController
     *        the assignedController to set
     */
    public void setAssignedController(boolean assignedController) {
        this.assignedController = assignedController;
    }// end setAssignedController

    /**
     * @return the batchAppCollection
     */
    public List<BatchAppCollectionModel> getBatchAppCollection() {
        return batchAppCollection;
    }// end getBatchAppCollection

    /**
     * @param batchAppCollection
     *        the batchAppCollection to set
     */
    public void setBatchAppCollection(List<BatchAppCollectionModel> batchAppCollection) {
        this.batchAppCollection = batchAppCollection;
    }// end setBatchAppCollection

    /**
     * @return the system
     */
    public SystemModel getSystem() {
        return system;
    }// end getSystem

    /**
     * @param system
     *        the system to set
     */
    public void setSystem(SystemModel system) {
        this.system = system;
    }// end setSystem

    /**
     * @return the appState
     */
    public Boolean getAppState() {
        return appState;
    }// end getAppState

    /**
     * @param appState
     *        the appState to set
     */
    public void setAppState(Boolean appState) {
        this.appState = appState;
    }// end setAppState

    /**
     * @return the partOfCollection
     */
    public String getPartOfCollection() {
        return partOfCollection;
    }// end getPartOfCollection

    /**
     * @param partOfCollection
     *        the partOfCollection to set
     */
    public void setPartOfCollection(String partOfCollection) {
        this.partOfCollection = partOfCollection;
    }// end setPartOfCollection

    /**
     * @return the pointOfContact
     */
    public AuthorizedUserModel getPointOfContact() {
        return pointOfContact;
    }// end getPointOfContact

    /**
     * @param pointOfContact
     *        the pointOfContact to set
     */
    public void setPointOfContact(AuthorizedUserModel pointOfContact) {
        this.pointOfContact = pointOfContact;
    }// end setPointOfContact

    /**
     * @return the executionCount
     */
    public Long getExecutionCount() {
        return executionCount;
    }// end getExecutionCount

    /**
     * @param executionCount
     *        the executionCount to set
     */
    public void setExecutionCount(Long executionCount) {
        this.executionCount = executionCount;
    }// end setExecutionCount

    /**
     * This method returns a schedule object that probably has not been saved to database but exist in the schedule list.
     * 
     * @param listNumber
     *        the number to compare to
     * @return ScheduleModel
     */
    public ScheduleModel getScheduleByListNumber(int listNumber) {
        ScheduleModel schedule = null;
        for(ScheduleModel scheduleModel : scheduleList){
            if(scheduleModel.getScheduleRefId().equals(0L) && scheduleModel.getListNumber() == listNumber){
                schedule = scheduleModel;
            }// end if
        }// end for
        return schedule;
    }// end getScheduleByListNumber

    /**
     * @return the lastRunStatus
     */
    public List<String> getLastRunStatus() {
        return lastRunStatus;
    }// end getLastRunStatus

    /**
     * @param lastRunStatus
     *        the lastRunStatus to set
     */
    public void setLastRunStatus(List<String> lastRunStatus) {
        this.lastRunStatus = lastRunStatus;
    }// end setLastRunStatus

    /**
     * This method removes a schedule object that probably has not been saved to database but exist in the schedule list from the list.
     * 
     * @param listNumber
     *        the number to compare to
     */
    public void removeScheduleByListNumber(int listNumber) {
        for(ScheduleModel scheduleModel : scheduleList){
            if(scheduleModel.getScheduleRefId().equals(0L) && scheduleModel.getListNumber() == listNumber){
                getScheduleList().remove(scheduleModel);
            }// end if
        }// end for
    }// end removeScheduleByListNumber

    public void replaceSchedule(ScheduleModel scheduleModel) {
        for(ScheduleModel schedule : scheduleList){
            if(schedule.getScheduleRefId().equals(scheduleModel.getScheduleRefId())){
                schedule = scheduleModel;
            }// end if
        }// end for
    }// end replaceSchedule

    /**
     * This method is a utility check to see if any new or edited schedules are currently in the list.
     * 
     * @return Boolean
     */
    public Boolean isEdit() {
        Boolean result = false;
        if(!AppUtil.isEmpty(scheduleList)){
            for(int i = 0, j = scheduleList.size();i < j;i++){
                if(scheduleList.get(i).isEdit() || !AppUtil.isNotNullAndZero(scheduleList.get(i).getScheduleRefId())){
                    result = true;
                    break;
                }// end if
            }// end for
        }// end if
        return result;
    }// end checkNewSchedule

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((batchRefId == null) ? 0 : batchRefId.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }// end hashCode

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        }// end if
        if(obj == null){
            return false;
        }// end if
        if(!(obj instanceof BatchAppModel)){
            return false;
        }// end if
        BatchAppModel other = (BatchAppModel) obj;
        if(batchRefId == null){
            if(other.batchRefId != null){
                return false;
            }// end if
        }else if(!batchRefId.equals(other.batchRefId)){
            return false;
        }// end if/else
        if(name == null){
            if(other.name != null){
                return false;
            }// end if
        }else if(!name.equals(other.name)){
            return false;
        }// end if/else
        return true;
    }// end equals

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("BatchAppModel [").append(NEW_LINE);
        builder.append("batchRefId=");
        builder.append(batchRefId).append(NEW_LINE);
        builder.append("name=");
        builder.append(name).append(NEW_LINE);
        builder.append("fileLocation=");
        builder.append(fileLocation).append(NEW_LINE);
        builder.append("fileName=");
        builder.append(fileName).append(NEW_LINE);
        builder.append("description=");
        builder.append(description).append(NEW_LINE);
        builder.append("typeDesc=");
        builder.append(typeDesc).append(NEW_LINE);
        builder.append("controller=");
        builder.append(controller == null ? "null" : controller.getName()).append(NEW_LINE);
        builder.append("schedule=");
        builder.append(StringUtil.collapseList(scheduleList)).append(NEW_LINE);
        builder.append("runStatuses.size()=");
        builder.append(AppUtil.isEmpty(runStatuses) ? "empty" : runStatuses.size()).append(NEW_LINE);
        builder.append("application=");
        builder.append(application == null ? "null" : application.getApplicationName()).append(NEW_LINE);
        builder.append("partOfCollection=");
        builder.append(partOfCollection).append(NEW_LINE);
        builder.append("assignedController=");
        builder.append(assignedController).append(NEW_LINE);
        builder.append("appState=");
        builder.append(appState).append(NEW_LINE);
        builder.append("executionCount=");
        builder.append(executionCount).append(NEW_LINE);
        builder.append(super.toString());
        builder.append("]");
        return builder.toString();
    }// end toString

    /*
     * (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }// end clone

}// end class
