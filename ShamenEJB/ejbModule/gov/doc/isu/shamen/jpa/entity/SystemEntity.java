package gov.doc.isu.shamen.jpa.entity;

import static gov.doc.isu.shamen.ejb.util.EJBConstants.DEFAULT_NEW_LINE;

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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.AdditionalCriteria;

/**
 * The persistent class for the Controllers database table.
 */
@Entity
@Table(name = "Systems", schema = "Trans")
@NamedQueries({@NamedQuery(name = "SystemEntity.FOR_USER", query = "SELECT c FROM SystemEntity c WHERE c.pointOfContact.userRefId = :userID ORDER BY c.systemName"), @NamedQuery(name = "SystemEntity.LOAD_SYSTEM_LIST", query = "SELECT s FROM SystemEntity s ORDER BY s.systemName")})
@AdditionalCriteria("this.common.deleteIndicator = 'N'")
public class SystemEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "System_Ref_Id", unique = true)
    private Long systemRefId;

    @Column(name = "System_Nm")
    private String systemName;
    @Column(name = "System_Desc")
    private String systemDesc;

    // bi-directional many-to-one association to BatchAppEntity
    @OneToMany(mappedBy = "system", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @OrderBy("batchName asc")
    private List<BatchAppEntity> batchApps;

    // bi-directional many-to-one association to ApplicationEntity
    @OneToMany(mappedBy = "system", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @OrderBy("applicationName asc")
    private List<ApplicationEntity> apps;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "Responsible_Staff_User_Ref_Id", columnDefinition = "IS NOT NULL")
    private AuthorizedUserEntity pointOfContact;

    @Embedded
    private CommonEntity common;

    /**
     * Default Constructor
     */
    public SystemEntity() {}

    /**
     * @param systemRefId
     *        systemRefId
     */
    public SystemEntity(Long systemRefId) {
        super();
        this.systemRefId = systemRefId;
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

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer builder = new StringBuffer();
        builder.append("SystemEntity [systemRefId=");
        builder.append(systemRefId).append(DEFAULT_NEW_LINE);
        builder.append(", systemName=");
        builder.append(systemName).append(DEFAULT_NEW_LINE);
        builder.append(", common=");
        builder.append(common);
        builder.append("]");
        return builder.toString();
    }// end toString

    /**
     * @return the systemRefId
     */
    public Long getSystemRefId() {
        return systemRefId;
    }

    /**
     * @param systemRefId
     *        the systemRefId to set
     */
    public void setSystemRefId(Long systemRefId) {
        this.systemRefId = systemRefId;
    }

    /**
     * @return the systemName
     */
    public String getSystemName() {
        return systemName;
    }

    /**
     * @param systemName
     *        the systemName to set
     */
    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    /**
     * @return the systemDesc
     */
    public String getSystemDesc() {
        return systemDesc;
    }

    /**
     * @param systemDesc
     *        the systemDesc to set
     */
    public void setSystemDesc(String systemDesc) {
        this.systemDesc = systemDesc;
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
     * @return the apps
     */
    public List<ApplicationEntity> getApps() {
        return apps;
    }

    /**
     * @param apps
     *        the apps to set
     */
    public void setApps(List<ApplicationEntity> apps) {
        this.apps = apps;
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

}// end SystemEntity
