package gov.doc.isu.shamen.jpa.entity;

import static gov.doc.isu.shamen.ejb.util.EJBConstants.DEFAULT_NEW_LINE;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.AdditionalCriteria;
import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;

import gov.doc.isu.shamen.jpa.comparator.RunStatusEntityComparator;

/**
 * The persistent class for the Batch_Apps database table.
 */
@Entity
@Table(name = "Batch_Apps", schema = "Trans")
@NamedQueries({@NamedQuery(name = "BatchAppEntity.LOAD_BATCH_LIST", query = "SELECT b FROM BatchAppEntity b"), @NamedQuery(name = "BatchAppEntity.FOR_USER", query = "SELECT c FROM BatchAppEntity c WHERE c.pointOfContact.userRefId = :userID ORDER BY c.batchName")})
@AdditionalCriteria("this.common.deleteIndicator = 'N'")
public class BatchAppEntity implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Batch_App_Ref_Id", unique = true, nullable = false)
    private Long batchAppRefId;

    @Column(name = "Batch_Nm")
    private String batchName;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Batch_Type_Cd", columnDefinition = "IS NOT NULL")
    private BatchTypeCodeEntity batchType;

    @Column(name = "Description")
    private String description;

    @Column(name = "File_Location")
    private String fileLocation;

    @Column(name = "File_Nm")
    private String fileName;

    // bi-directional many-to-one association to ControllerEntity
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "Controller_Ref_Id")
    private ControllerEntity controller;

    // bi-directional many-to-one association to SystemEntity
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "System_Ref_Id")
    private SystemEntity system;

    // bi-directional many-to-one association to RunStatusEntity
    // @OneToMany(mappedBy = "batchApp", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    private transient List<RunStatusEntity> runStatuses;

    // bi-directional one-to-one association to ScheduleEntity
    @OneToMany(mappedBy = "batchApp", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<ScheduleEntity> schedule;

    // bi-directional many-to-one association to BatchAppEntity
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "Application_Ref_Id")
    private ApplicationEntity application;

    // bi-directional many-to-one association to batchCollection
    @OneToMany(mappedBy = "mainBatchApp", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @BatchFetch(BatchFetchType.EXISTS)
    @OrderBy("runSeq")
    private List<BatchAppCollectionEntity> batchCollection;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "Responsible_Staff_User_Ref_Id", columnDefinition = "IS NOT NULL")
    private AuthorizedUserEntity pointOfContact;

    @Embedded
    private CommonEntity common;

    private transient String partOfCollection;
    private transient Long executionCount;

    /**
     * Default Constructor
     */
    public BatchAppEntity() {
        super();
    }

    /**
     * Constructor
     *
     * @param batchAppRefId
     *        the ref id of the batch app record
     */
    public BatchAppEntity(Long batchAppRefId) {
        super();
        this.batchAppRefId = batchAppRefId;
    }

    /**
     * @return the batchAppRefId
     */
    public Long getBatchAppRefId() {
        return batchAppRefId;
    }

    /**
     * @param batchAppRefId
     *        the batchAppRefId to set
     */
    public void setBatchAppRefId(Long batchAppRefId) {
        this.batchAppRefId = batchAppRefId;
    }

    /**
     * @return the batchName
     */
    public String getBatchName() {
        return batchName;
    }

    /**
     * @param batchName
     *        the batchName to set
     */
    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    /**
     * @return the batchType
     */
    public BatchTypeCodeEntity getBatchType() {
        return batchType;
    }

    /**
     * @param batchType
     *        the batchType to set
     */
    public void setBatchType(BatchTypeCodeEntity batchType) {
        this.batchType = batchType;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     *        the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the fileLocation
     */
    public String getFileLocation() {
        return fileLocation;
    }

    /**
     * @param fileLocation
     *        the fileLocation to set
     */
    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName
     *        the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return the controller
     */
    public ControllerEntity getController() {
        return controller;
    }

    /**
     * @param controller
     *        the controller to set
     */
    public void setController(ControllerEntity controller) {
        this.controller = controller;
    }

    /**
     * @return the schedule
     */
    public List<ScheduleEntity> getSchedule() {
        return schedule;
    }

    /**
     * @param schedule
     *        the schedule to set
     */
    public void setSchedule(List<ScheduleEntity> schedule) {
        this.schedule = schedule;
    }

    /**
     * @return the runStatuses
     */
    public List<RunStatusEntity> getRunStatuses() {
        return runStatuses;
    }

    /**
     * @param runStatuses
     *        the runStatuses to set
     */
    public void setRunStatuses(List<RunStatusEntity> runStatuses) {
        this.runStatuses = runStatuses;
    }

    /**
     * @param runStatus
     *        the runStatus to add
     * @return RunStatusEntity
     * @see java.util.List#add(java.lang.Object)
     */
    public RunStatusEntity addRunStatus(RunStatusEntity runStatus) {
        getRunStatuses().add(runStatus);
        runStatus.setBatchApp(this);

        return runStatus;
    }

    /**
     * @param runStatus
     *        the runStatus to Remove
     * @return RunStatusEntity
     * @see java.util.List#remove(java.lang.Object)
     */
    public RunStatusEntity removeRunStatus(RunStatusEntity runStatus) {
        getRunStatuses().remove(runStatus);
        runStatus.setBatchApp(null);

        return runStatus;
    }

    /**
     * @return the application
     */
    public ApplicationEntity getApplication() {
        return application;
    }

    /**
     * @param application
     *        the application to set
     */
    public void setApplication(ApplicationEntity application) {
        this.application = application;
    }

    /**
     * @return the common
     */
    public CommonEntity getCommon() {
        return common;
    }

    /**
     * @param common
     *        the common to set
     */
    public void setCommon(CommonEntity common) {
        this.common = common;
    }

    /**
     * Sorts the Run status List based on direction passed in
     *
     * @param sortDirection
     *        ASC for ascending, DESC for descending
     * @return first RunStatusEntity in list
     */
    public RunStatusEntity getRunStatusBySort(String sortDirection) {
        if(sortDirection.equalsIgnoreCase("ASC")){
            Collections.sort(runStatuses, new RunStatusEntityComparator());
        }else{
            Collections.sort(runStatuses, Collections.reverseOrder(new RunStatusEntityComparator()));
        }// end if..else
        return runStatuses.get(0);
    }// end method

    /**
     * Sorts the Run status List based on direction passed in
     *
     * @param sortDirection
     *        ASC for ascending, DESC for descending
     */
    public void sortRunStatusList(String sortDirection) {
        if(sortDirection.equalsIgnoreCase("ASC")){
            Collections.sort(runStatuses, new RunStatusEntityComparator());
        }else{
            Collections.sort(runStatuses, Collections.reverseOrder(new RunStatusEntityComparator()));
        }// end if..else
    }// end method

    /**
     * @return the batchCollection
     */
    public List<BatchAppCollectionEntity> getBatchCollection() {
        return batchCollection;
    }

    /**
     * @param batchCollection
     *        the batchCollection to set
     */
    public void setBatchCollection(List<BatchAppCollectionEntity> batchCollection) {
        this.batchCollection = batchCollection;
    }

    /**
     * @return the pointOfContact
     */
    public AuthorizedUserEntity getPointOfContact() {
        return pointOfContact;
    }

    /**
     * @param pointOfContact
     *        the pointOfContact to set
     */
    public void setPointOfContact(AuthorizedUserEntity pointOfContact) {
        this.pointOfContact = pointOfContact;
    }

    /**
     * @return the system
     */
    public SystemEntity getSystem() {
        return system;
    }

    /**
     * @param system
     *        the system to set
     */
    public void setSystem(SystemEntity system) {
        this.system = system;
    }

    /**
     * @return the partOfCollection
     */
    public String getPartOfCollection() {
        return partOfCollection;
    }

    /**
     * @param partOfCollection
     *        the partOfCollection to set
     */
    public void setPartOfCollection(String partOfCollection) {
        this.partOfCollection = partOfCollection;
    }

    /**
     * @return the executionCount
     */
    public Long getExecutionCount() {
        return executionCount;
    }

    /**
     * @param executionCount
     *        the executionCount to set
     */
    public void setExecutionCount(Long executionCount) {
        this.executionCount = executionCount;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer builder = new StringBuffer();
        builder.append("BatchAppEntity [batchAppRefId=");
        builder.append(batchAppRefId).append(DEFAULT_NEW_LINE);
        builder.append(", batchName=");
        builder.append(batchName).append(DEFAULT_NEW_LINE);
        builder.append(", batchType=");
        builder.append(batchType).append(DEFAULT_NEW_LINE);
        builder.append(", description=");
        builder.append(description).append(DEFAULT_NEW_LINE);
        builder.append(", fileLocation=");
        builder.append(fileLocation).append(DEFAULT_NEW_LINE);
        builder.append(", fileName=");
        builder.append(fileName).append(DEFAULT_NEW_LINE);
        // builder.append(", controller=");
        // builder.append(controller != null ? controller.getControllerRefId() : "null").append(DEFAULT_NEW_LINE);
        // builder.append(", runStatuses.size()=");
        // builder.append(runStatuses != null ? runStatuses.size() : "null").append(DEFAULT_NEW_LINE);
        builder.append(", schedule=");
        builder.append(schedule).append(DEFAULT_NEW_LINE);
        builder.append(", application=");
        builder.append(application != null ? application.getApplicationRefId() : "null").append(DEFAULT_NEW_LINE);
        builder.append(", common=");
        builder.append(common);
        builder.append("]");
        return builder.toString();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
