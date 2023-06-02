package gov.doc.isu.shamen.jpa.entity;

import java.io.Serializable;
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

/**
 * The persistent class for the Applications database table.
 */
@Entity
@Table(name = "Applications", schema = "Trans")
@NamedQueries({@NamedQuery(name = "ApplicationEntity.findAll", query = "SELECT a FROM ApplicationEntity a ORDER BY a.applicationName"), @NamedQuery(name = "ApplicationEntity.findByNameAndAddress", query = "SELECT a FROM ApplicationEntity a WHERE a.applicationName = :applicationName AND a.applicationAddress = :applicationAddress"), @NamedQuery(name = "ApplicationEntity.FOR_USER", query = "SELECT c FROM ApplicationEntity c WHERE c.pointOfContact.userRefId = :userID ORDER BY c.applicationName"), @NamedQuery(name = "ApplicationEntity.FOR_SYSTEM", query = "SELECT a FROM ApplicationEntity a WHERE a.system.systemRefId = :systemRefId")})
@AdditionalCriteria("this.common.deleteIndicator = 'N'")
public class ApplicationEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Application_Ref_Id", unique = true, nullable = false)
    private Long applicationRefId;

    @Column(name = "Application_Addr")
    private String applicationAddress;

    @Column(name = "Application_Nm")
    private String applicationName;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Application_Type_Cd", columnDefinition = "IS NOT NULL")
    private ApplicationTypeCodeEntity applicationType;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Application_Status_Cd", columnDefinition = "IS NOT NULL")
    private ApplicationStatusCodeEntity applicationStatus;

    @Column(name = "Application_Status_Cmnt")
    private String statusComment;

    @Column(name = "Show_Application_Notification")
    private String showApplicationNotification;

    @Column(name = "Application_Notification_Desc")
    private String applicationNotificationDesc;

    // bi-directional many-to-one association to BatchAppEntity
    @OneToMany(mappedBy = "application", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @OrderBy("batchName asc")
    private List<BatchAppEntity> batchApps;

    // bi-directional many-to-one association to BatchAppEntity
    @OneToMany(mappedBy = "application", fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @OrderBy("applicationInstanceName asc")
    private List<ApplicationInstanceEntity> applicationInstances;

    // bi-directional many-to-one association to SystemEntity
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "System_Ref_Id")
    private SystemEntity system;

    @Embedded
    private CommonEntity common;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "Responsible_Staff_User_Ref_Id", columnDefinition = "IS NOT NULL")
    private AuthorizedUserEntity pointOfContact;

    /**
     * Default Constructor
     */
    public ApplicationEntity() {
        super();
    }

    /**
     * Constructor
     *
     * @param applicationRefId
     *        the applicationRefId
     */
    public ApplicationEntity(Long applicationRefId) {
        super();
        this.applicationRefId = applicationRefId;
        this.applicationAddress = "";
        this.applicationName = "";
    }

    /**
     * @return the applicationRefId
     */
    public Long getApplicationRefId() {
        return applicationRefId;
    }

    /**
     * @param applicationRefId
     *        the applicationRefId to set
     */
    public void setApplicationRefId(Long applicationRefId) {
        this.applicationRefId = applicationRefId;
    }

    /**
     * @return the applicationAddress
     */
    public String getApplicationAddress() {
        return applicationAddress;
    }

    /**
     * @param applicationAddress
     *        the applicationAddress to set
     */
    public void setApplicationAddress(String applicationAddress) {
        this.applicationAddress = applicationAddress;
    }

    /**
     * @return the applicationName
     */
    public String getApplicationName() {
        return applicationName;
    }

    /**
     * @param applicationName
     *        the applicationName to set
     */
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    /**
     * @return the applicationType
     */
    public ApplicationTypeCodeEntity getApplicationType() {
        return applicationType;
    }

    /**
     * @param applicationType
     *        the applicationType to set
     */
    public void setApplicationType(ApplicationTypeCodeEntity applicationType) {
        this.applicationType = applicationType;
    }

    /**
     * @return the applicationStatus
     */
    public ApplicationStatusCodeEntity getApplicationStatus() {
        return applicationStatus;
    }

    /**
     * @param applicationStatus
     *        the applicationStatus to set
     */
    public void setApplicationStatus(ApplicationStatusCodeEntity applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    /**
     * @return the statusComment
     */
    public String getStatusComment() {
        return statusComment;
    }

    /**
     * @param statusComment
     *        the statusComment to set
     */
    public void setStatusComment(String statusComment) {
        this.statusComment = statusComment;
    }

    /**
     * @return the showApplicationNotification
     */
    public String getShowApplicationNotification() {
        return showApplicationNotification;
    }

    /**
     * @param showApplicationNotification
     *        the showApplicationNotification to set
     */
    public void setShowApplicationNotification(String showApplicationNotification) {
        this.showApplicationNotification = showApplicationNotification;
    }

    /**
     * @return the applicationNotificationDesc
     */
    public String getApplicationNotificationDesc() {
        return applicationNotificationDesc;
    }

    /**
     * @param applicationNotificationDesc
     *        the applicationNotificationDesc to set
     */
    public void setApplicationNotificationDesc(String applicationNotificationDesc) {
        this.applicationNotificationDesc = applicationNotificationDesc;
    }

    /**
     * @return the batchApps
     */
    public List<BatchAppEntity> getBatchApps() {
        return batchApps;
    }

    /**
     * @param batchApps
     *        the batchApps to set
     */
    public void setBatchApps(List<BatchAppEntity> batchApps) {
        this.batchApps = batchApps;
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
     * @return the applicationInstances
     */
    public List<ApplicationInstanceEntity> getApplicationInstances() {
        return applicationInstances;
    }

    /**
     * @param applicationInstances
     *        the applicationInstances to set
     */
    public void setApplicationInstances(List<ApplicationInstanceEntity> applicationInstances) {
        this.applicationInstances = applicationInstances;
    }

    /**
     * Gets the appInfo
     * 
     * @return String app information
     */
    public String appInfo() {
        StringBuffer builder = new StringBuffer();
        builder.append("ApplicationEntity [applicationAddress=");
        builder.append(applicationAddress);
        builder.append(", applicationName=");
        builder.append(applicationName);
        builder.append(", applicationInstances=");
        builder.append(applicationInstances);
        builder.append("]");
        return builder.toString();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer builder = new StringBuffer();
        builder.append("ApplicationEntity [applicationRefId=");
        builder.append(applicationRefId);
        builder.append(", applicationAddress=");
        builder.append(applicationAddress);
        builder.append(", applicationName=");
        builder.append(applicationName);
        builder.append(", applicationType=");
        builder.append(applicationType);
        builder.append(", applicationStatus=");
        builder.append(applicationStatus);
        builder.append(", statusComment=");
        builder.append(statusComment);
        builder.append(", showApplicationNotification=");
        builder.append(showApplicationNotification);
        builder.append(", applicationNotificationDesc=");
        builder.append(applicationNotificationDesc);
        builder.append(", applicationInstances=");
        builder.append(applicationInstances);
        builder.append(", system=");
        builder.append(system);
        builder.append(", common=");
        builder.append(common);

        builder.append(", pointOfContact=");
        builder.append(pointOfContact);
        builder.append("]");
        return builder.toString();
    }

}
