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
@Table(name = "Controllers", schema = "Trans")
@NamedQueries({@NamedQuery(name = "ControllerEntity.LOAD_CONTROLLER_LIST", query = "SELECT c FROM ControllerEntity c ORDER BY c.controllerName"), @NamedQuery(name = "ControllerEntity.FIND_BY_ADDRESS", query = "SELECT c FROM ControllerEntity c WHERE c.controllerAddress = :address"), @NamedQuery(name = "ControllerEntity.LOAD_FOR_CODE", query = "SELECT c.controllerRefId, c.controllerName FROM ControllerEntity c ORDER BY c.controllerName")})
@AdditionalCriteria("this.common.deleteIndicator = 'N'")
public class ControllerEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Controller_Ref_Id", unique = true)
    private Long controllerRefId;

    @Column(name = "Controller_Address")
    private String controllerAddress;

    @Column(name = "Controller_Nm")
    private String controllerName;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Controller_Status_Cd", columnDefinition = "IS NOT NULL")
    private ControllerStatusCodeEntity controllerStatusCodeEntity;

    // bi-directional many-to-one association to BatchAppEntity
    @OneToMany(mappedBy = "controller", fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @OrderBy("batchName asc")
    private List<BatchAppEntity> batchApps;

    @Column(name = "Default_Address")
    private String defaultAddress;

    @Embedded
    private CommonEntity common;

    /**
     * Default Constructor
     */
    public ControllerEntity() {}

    /**
     * @param controllerRefId
     *        controllerRefId
     */
    public ControllerEntity(Long controllerRefId) {
        super();
        this.controllerRefId = controllerRefId;
    }

    /**
     * @return the controllerRefId
     */
    public Long getControllerRefId() {
        return controllerRefId;
    }

    /**
     * @param controllerRefId
     *        the controllerRefId to set
     */
    public void setControllerRefId(Long controllerRefId) {
        this.controllerRefId = controllerRefId;
    }

    /**
     * @return the controllerAddress
     */
    public String getControllerAddress() {
        return controllerAddress;
    }

    /**
     * @param controllerAddress
     *        the controllerAddress to set
     */
    public void setControllerAddress(String controllerAddress) {
        this.controllerAddress = controllerAddress;
    }

    /**
     * @return the controllerName
     */
    public String getControllerName() {
        return controllerName;
    }

    /**
     * @param controllerName
     *        the controllerName to set
     */
    public void setControllerName(String controllerName) {
        this.controllerName = controllerName;
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
     * @return the defaultAddress
     */
    public String getDefaultAddress() {
        return defaultAddress;
    }

    /**
     * @param defaultAddress
     *        the defaultAddress to set
     */
    public void setDefaultAddress(String defaultAddress) {
        this.defaultAddress = defaultAddress;
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
     * @param batchApp
     *        the batchApp to add
     * @return BatchAppEntity
     * @see java.util.List#add(java.lang.Object)
     */
    public BatchAppEntity addBatchApp(BatchAppEntity batchApp) {
        getBatchApps().add(batchApp);
        batchApp.setController(this);

        return batchApp;
    }

    /**
     * @param batchApp
     *        the batchApp to Remove
     * @return BatchAppEntity
     * @see java.util.List#remove(java.lang.Object)
     */
    public BatchAppEntity removeBatchApp(BatchAppEntity batchApp) {
        getBatchApps().remove(batchApp);
        batchApp.setController(null);

        return batchApp;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer builder = new StringBuffer();
        builder.append("ControllerEntity [controllerRefId=");
        builder.append(controllerRefId).append(DEFAULT_NEW_LINE);
        builder.append(", controllerAddress=");
        builder.append(controllerAddress).append(DEFAULT_NEW_LINE);
        builder.append(", controllerName=");
        builder.append(controllerName).append(DEFAULT_NEW_LINE);
        builder.append(", defaultAddress=");
        builder.append(defaultAddress).append(DEFAULT_NEW_LINE);
        builder.append(", batchApps=");
        builder.append(batchApps).append(DEFAULT_NEW_LINE);
        builder.append(", common=");
        builder.append(common);
        builder.append("]");
        return builder.toString();
    }

    /**
     * @return the controllerStatusCodeEntity
     */
    public ControllerStatusCodeEntity getControllerStatusCodeEntity() {
        return controllerStatusCodeEntity;
    }

    /**
     * @param controllerStatusCodeEntity
     *        the controllerStatusCodeEntity to set
     */
    public void setControllerStatusCodeEntity(ControllerStatusCodeEntity controllerStatusCodeEntity) {
        this.controllerStatusCodeEntity = controllerStatusCodeEntity;
    }

}
