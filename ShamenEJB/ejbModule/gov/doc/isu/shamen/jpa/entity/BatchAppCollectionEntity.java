package gov.doc.isu.shamen.jpa.entity;

import static gov.doc.isu.shamen.ejb.util.EJBConstants.DEFAULT_NEW_LINE;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.AdditionalCriteria;

/**
 * The persistent class for the Batch_Apps database table.
 */
@Entity
@Table(name = "Batch_Apps_Collection_Xref", schema = "Trans")
@NamedQuery(name = "BatchAppCollectionEntity.LOAD_BATCH_COLLECTION_LIST", query = "SELECT b FROM BatchAppCollectionEntity b")
@AdditionalCriteria("this.common.deleteIndicator = 'N'")
public class BatchAppCollectionEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Collection_Ref_Id", unique = true, nullable = false)
    private Long collectionRefId;

    // bi-directional one-to-one association to BatchAppEntity
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "Main_Batch_App_Ref_Id")
    private BatchAppEntity mainBatchApp;

    // bi-directional one-to-one association to BatchAppEntity
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "Assoc_Batch_App_Ref_Id", columnDefinition = "IS NOT NULL")
    private BatchAppEntity assocBatchApp;

    @Column(name = "Batch_App_Run_Seq")
    private Long runSeq;

    @Embedded
    private CommonEntity common;

    /**
     * Default Constructor
     */
    public BatchAppCollectionEntity() {
        super();
    }

    /**
     * Constructor with five parameters
     *
     * @param collectionRefId
     *        the collectionRefId
     * @param mainBatchApp
     *        the mainBatchApp
     * @param assocBatchApp
     *        the assocBatchApp
     * @param runSeq
     *        the runSeq
     * @param common
     *        the common entity
     */
    public BatchAppCollectionEntity(Long collectionRefId, BatchAppEntity mainBatchApp, BatchAppEntity assocBatchApp, Long runSeq, CommonEntity common) {
        super();
        this.collectionRefId = collectionRefId;
        this.mainBatchApp = mainBatchApp;
        this.assocBatchApp = assocBatchApp;
        this.runSeq = runSeq;
        this.common = common;
    }

    /**
     * @return the collectionRefId
     */
    public Long getCollectionRefId() {
        return collectionRefId;
    }

    /**
     * @param collectionRefId
     *        the collectionRefId to set
     */
    public void setCollectionRefId(Long collectionRefId) {
        this.collectionRefId = collectionRefId;
    }

    /**
     * @return the mainBatchApp
     */
    public BatchAppEntity getMainBatchApp() {
        return mainBatchApp;
    }

    /**
     * @param mainBatchApp
     *        the mainBatchApp to set
     */
    public void setMainBatchApp(BatchAppEntity mainBatchApp) {
        this.mainBatchApp = mainBatchApp;
    }

    /**
     * @return the assocBatchApp
     */
    public BatchAppEntity getAssocBatchApp() {
        return assocBatchApp;
    }

    /**
     * @param assocBatchApp
     *        the assocBatchApp to set
     */
    public void setAssocBatchApp(BatchAppEntity assocBatchApp) {
        this.assocBatchApp = assocBatchApp;
    }

    /**
     * @return the runSeq
     */
    public Long getRunSeq() {
        return runSeq;
    }

    /**
     * @param runSeq
     *        the runSeq to set
     */
    public void setRunSeq(Long runSeq) {
        this.runSeq = runSeq;
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
        builder.append("BatchAppCollectionEntity [collectionRefId=");
        builder.append(collectionRefId).append(DEFAULT_NEW_LINE);
        builder.append(", mainBatchApp=");
        builder.append(mainBatchApp).append(DEFAULT_NEW_LINE);
        builder.append(", assocBatchApp=");
        builder.append(assocBatchApp).append(DEFAULT_NEW_LINE);
        builder.append(", runSeq=");
        builder.append(runSeq).append(DEFAULT_NEW_LINE);
        builder.append(", common=");
        builder.append(common);
        builder.append("]");
        return builder.toString();
    }

}
