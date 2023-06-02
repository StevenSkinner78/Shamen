package gov.doc.isu.shamen.jpa.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.AdditionalCriteria;

/**
 * The persistent class for the Application Instance database table.
 */
@Entity
@Table(name = "Application_Instances", schema = "Trans")
@NamedQueries({@NamedQuery(name = "ApplicationInstanceEntity.getInstanceByKey", query = "SELECT a FROM ApplicationInstanceEntity a where a.applicationInstanceName = :node and a.application.applicationRefId = :applicationRefId"), @NamedQuery(name = "ApplicationInstanceEntity.getInstanceByApplicationAddressName", query = "SELECT a FROM ApplicationInstanceEntity a where a.applicationInstanceName = :instanceName and a.application.applicationName = :applicationName and a.application.applicationAddress = :applicationAddress")})
@AdditionalCriteria("this.common.deleteIndicator = 'N'")
public class ApplicationInstanceEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Application_Instance_Ref_Id", unique = true, nullable = false)
    private Long applicationInstanceRefId;

    // bi-directional many-to-one association to BatchAppEntity
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "Application_Ref_Id", insertable = true)
    private ApplicationEntity application;

    @Column(name = "Application_Instance_Nm")
    private String applicationInstanceName;

    @Column(name = "Instantiation_Ts")
    private Timestamp instantiationTs;

    @Column(name = "Time_Of_Death_Ts")
    private Timestamp timeOfDeathTs;

    @Column(name = "Verify_Application_Status_Cd")
    private String verifiedApplicationStatusCd;

    @Column(name = "Verified_Version_Nm")
    private String verifiedVersionNm;

    @Column(name = "Verified_Build_Nm")
    private String verifiedBuildNm;

    @Column(name = "Verified_EAR_Nm")
    private String verifiedEarNm;

    @Column(name = "Verified_Addtnl_Info")
    private String verifiedAddtnlInfo;

    @Lob
    @Column(name = "Encryption_Password")
    private byte[] encryptionKey;

    @Embedded
    private CommonEntity common;

    /**
     * Default Constructor
     */
    public ApplicationInstanceEntity() {
        super();
    }

    /**
     * Constructor
     *
     * @param applicationInstanceRefId
     *        the applicationInstanceRefId
     */
    public ApplicationInstanceEntity(Long applicationInstanceRefId) {
        super();
        this.applicationInstanceRefId = applicationInstanceRefId;
    }

    /**
     * @return the applicationInstanceRefId
     */
    public Long getApplicationInstanceRefId() {
        return applicationInstanceRefId;
    }

    /**
     * @param applicationInstanceRefId
     *        the applicationInstanceRefId to set
     */
    public void setApplicationInstanceRefId(Long applicationInstanceRefId) {
        this.applicationInstanceRefId = applicationInstanceRefId;
    }

    /**
     * @return the applicationInstanceName
     */
    public String getApplicationInstanceName() {
        return applicationInstanceName;
    }

    /**
     * @param applicationInstanceName
     *        the applicationInstanceName to set
     */
    public void setApplicationInstanceName(String applicationInstanceName) {
        this.applicationInstanceName = applicationInstanceName;
    }

    /**
     * @return the instantiationTs
     */
    public Timestamp getInstantiationTs() {
        return instantiationTs;
    }

    /**
     * @param instantiationTs
     *        the instantiationTs to set
     */
    public void setInstantiationTs(Timestamp instantiationTs) {
        this.instantiationTs = instantiationTs;
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
     * @return the verifiedApplicationStatusCd
     */
    public String getVerifiedApplicationStatusCd() {
        return verifiedApplicationStatusCd;
    }

    /**
     * @param verifiedApplicationStatusCd
     *        the verifiedApplicationStatusCd to set
     */
    public void setVerifiedApplicationStatusCd(String verifiedApplicationStatusCd) {
        this.verifiedApplicationStatusCd = verifiedApplicationStatusCd;
    }

    /**
     * @return the encryptionKey
     */
    public byte[] getEncryptionKey() {
        return encryptionKey;
    }

    /**
     * @param encryptionKey
     *        the encryptionKey to set
     */
    public void setEncryptionKey(byte[] encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    /**
     * Return whether or not the handshake has been established for this application. If it has a password, then it has been established.
     *
     * @return true or false
     */
    public Boolean isHandshakeEstablished() {
        Boolean established = false;

        if(getEncryptionKey() != null){
            established = true;
        }// end if

        return established;
    }// end isHandshakeEstablished

    /**
     * Get the application's key which is a formatted combo of the application name and address.
     *
     * @return formatted key
     */
    public String getFormattedAppKey() {
        StringBuffer sb = new StringBuffer();
        if(application != null){
            sb.append(application.getApplicationName());
            sb.append(" - ");
            sb.append(application.getApplicationAddress());
            sb.append(" - ");
            sb.append(applicationInstanceName);
        }// end if
        return sb.toString();
    }// end getFormattedAppKey

    /**
     * @return the verifiedVersionNm
     */
    public String getVerifiedVersionNm() {
        return verifiedVersionNm;
    }

    /**
     * @param verifiedVersionNm
     *        the verifiedVersionNm to set
     */
    public void setVerifiedVersionNm(String verifiedVersionNm) {
        this.verifiedVersionNm = verifiedVersionNm;
    }

    /**
     * @return the verifiedBuildNm
     */
    public String getVerifiedBuildNm() {
        return verifiedBuildNm;
    }

    /**
     * @param verifiedBuildNm
     *        the verifiedBuildNm to set
     */
    public void setVerifiedBuildNm(String verifiedBuildNm) {
        this.verifiedBuildNm = verifiedBuildNm;
    }

    /**
     * @return the verifiedEarNm
     */
    public String getVerifiedEarNm() {
        return verifiedEarNm;
    }

    /**
     * @param verifiedEarNm
     *        the verifiedEarNm to set
     */
    public void setVerifiedEarNm(String verifiedEarNm) {
        this.verifiedEarNm = verifiedEarNm;
    }

    /**
     * @return the verifiedAddtnlInfo
     */
    public String getVerifiedAddtnlInfo() {
        return verifiedAddtnlInfo;
    }

    /**
     * @param verifiedAddtnlInfo
     *        the verifiedAddtnlInfo to set
     */
    public void setVerifiedAddtnlInfo(String verifiedAddtnlInfo) {
        this.verifiedAddtnlInfo = verifiedAddtnlInfo;
    }

    /**
     * @return the timeOfDeathTs
     */
    public Timestamp getTimeOfDeathTs() {
        return timeOfDeathTs;
    }

    /**
     * @param timeOfDeathTs
     *        the timeOfDeathTs to set
     */
    public void setTimeOfDeathTs(Timestamp timeOfDeathTs) {
        this.timeOfDeathTs = timeOfDeathTs;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer builder = new StringBuffer();
        builder.append("ApplicationInstanceEntity [applicationInstanceRefId=");
        builder.append(applicationInstanceRefId);
        builder.append(", applicationInstanceName=");
        builder.append(applicationInstanceName);
        builder.append(", instantiationTs=");
        builder.append(instantiationTs);
        builder.append(", timeOfDeathTs=");
        builder.append(timeOfDeathTs);
        builder.append(", verifiedApplicationStatusCd=");
        builder.append(verifiedApplicationStatusCd);
        builder.append(", verifiedVersionNm=");
        builder.append(verifiedVersionNm);
        builder.append(", verifiedBuildNm=");
        builder.append(verifiedBuildNm);
        builder.append(", verifiedEarNm=");
        builder.append(verifiedEarNm);
        builder.append(", verifiedAddtnlInfo=");
        builder.append(verifiedAddtnlInfo);
        builder.append(", common=");
        builder.append(common);
        builder.append("]");
        return builder.toString();
    }
}
