package gov.doc.isu.shamen.jpa.entity;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.ReadOnly;

/**
 * The persistent class for the Batch_Type_Codes database table.
 */
/**
 * @author sls000is
 */
@ReadOnly
@Entity
@Table(name = "Batch_Type_Codes", schema = "Code")
@NamedQueries({@NamedQuery(name = "BatchTypeCodeEntity.FIND_ALL_ACTIVE", query = "SELECT b FROM BatchTypeCodeEntity b where :code <> b.batchTypeCd and :currentDate between b.batchTypeStartDt and b.batchTypeStopDt order by b.batchTypeSortSeqNo"), @NamedQuery(name = "BatchTypeCodeEntity.FIND_ALL_ACTIVE_WITH_COL", query = "SELECT b FROM BatchTypeCodeEntity b where :currentDate between b.batchTypeStartDt and b.batchTypeStopDt order by b.batchTypeSortSeqNo")})
public class BatchTypeCodeEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "Batch_Type_Cd", insertable = false)
    private String batchTypeCd;

    @Column(name = "Batch_Type_Desc", insertable = false, updatable = false)
    private String batchTypeDesc;

    @Column(name = "Batch_Type_Sort_Seq_No", insertable = false, updatable = false)
    private int batchTypeSortSeqNo;

    @Column(name = "Batch_Type_Start_Dt", insertable = false, updatable = false)
    private Date batchTypeStartDt;

    @Column(name = "Batch_Type_Stop_Dt", insertable = false, updatable = false)
    private Date batchTypeStopDt;

    @Column(name = "Create_User_Ref_Id", insertable = false, updatable = false)
    private Long createUserRefId;
    @Column(name = "Create_Ts", insertable = false, updatable = false)
    private Timestamp createTime;
    @Column(name = "Update_User_Ref_Id", insertable = false, updatable = false)
    private Long updateUserRefId;
    @Column(name = "Update_Ts", insertable = false, updatable = false)
    private Timestamp updateTime;

    /**
     * Default Contructor
     */
    public BatchTypeCodeEntity() {}

    /**
     * Constructor
     * 
     * @param batchTypeCd
     *        batchTypeCd
     * @param batchTypeDesc
     *        batchTypeDesc
     */
    public BatchTypeCodeEntity(String batchTypeCd, String batchTypeDesc) {
        super();
        this.batchTypeCd = batchTypeCd;
        this.batchTypeDesc = batchTypeDesc;
    }

    /**
     * @return the batchTypeCd
     */
    public String getBatchTypeCd() {
        return batchTypeCd;
    }

    /**
     * @param batchTypeCd
     *        the batchTypeCd to set
     */
    public void setBatchTypeCd(String batchTypeCd) {
        this.batchTypeCd = batchTypeCd;
    }

    /**
     * @return the batchTypeDesc
     */
    public String getBatchTypeDesc() {
        return batchTypeDesc;
    }

    /**
     * @param batchTypeDesc
     *        the batchTypeDesc to set
     */
    public void setBatchTypeDesc(String batchTypeDesc) {
        this.batchTypeDesc = batchTypeDesc;
    }

    /**
     * @return the batchTypeSortSeqNo
     */
    public int getBatchTypeSortSeqNo() {
        return batchTypeSortSeqNo;
    }

    /**
     * @param batchTypeSortSeqNo
     *        the batchTypeSortSeqNo to set
     */
    public void setBatchTypeSortSeqNo(int batchTypeSortSeqNo) {
        this.batchTypeSortSeqNo = batchTypeSortSeqNo;
    }

    /**
     * @return the batchTypeStartDt
     */
    public Date getBatchTypeStartDt() {
        return batchTypeStartDt;
    }

    /**
     * @param batchTypeStartDt
     *        the batchTypeStartDt to set
     */
    public void setBatchTypeStartDt(Date batchTypeStartDt) {
        this.batchTypeStartDt = batchTypeStartDt;
    }

    /**
     * @return the batchTypeStopDt
     */
    public Date getBatchTypeStopDt() {
        return batchTypeStopDt;
    }

    /**
     * @param batchTypeStopDt
     *        the batchTypeStopDt to set
     */
    public void setBatchTypeStopDt(Date batchTypeStopDt) {
        this.batchTypeStopDt = batchTypeStopDt;
    }

    /**
     * @return the createUserRefId
     */
    public Long getCreateUserRefId() {
        return createUserRefId;
    }

    /**
     * @param createUserRefId
     *        the createUserRefId to set
     */
    public void setCreateUserRefId(Long createUserRefId) {
        this.createUserRefId = createUserRefId;
    }

    /**
     * @return the createTime
     */
    public Timestamp getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime
     *        the createTime to set
     */
    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    /**
     * @return the updateUserRefId
     */
    public Long getUpdateUserRefId() {
        return updateUserRefId;
    }

    /**
     * @param updateUserRefId
     *        the updateUserRefId to set
     */
    public void setUpdateUserRefId(Long updateUserRefId) {
        this.updateUserRefId = updateUserRefId;
    }

    /**
     * @return the updateTime
     */
    public Timestamp getUpdateTime() {
        return updateTime;
    }

    /**
     * @param updateTime
     *        the updateTime to set
     */
    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "BatchTypeCodeEntity [batchTypeCd=" + batchTypeCd + ", batchTypeDesc=" + batchTypeDesc + ", batchTypeSortSeqNo=" + batchTypeSortSeqNo + ", batchTypeStartDt=" + batchTypeStartDt + ", batchTypeStopDt=" + batchTypeStopDt + ", createUserRefId=" + createUserRefId + ", createTime=" + createTime + ", updateUserRefId=" + updateUserRefId + ", updateTime=" + updateTime + "]";
    }

}
