package gov.doc.isu.shamen.jpa.entity;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.ReadOnly;

/**
 * The persistent class for the Frequency_Codes database table.
 */
@ReadOnly
@Entity
@Table(name = "Frequency_Type_Codes", schema = "Code")
@NamedQuery(name = "FrequencyTypeCodeEntity.FIND_ALL_ACTIVE", query = "SELECT f FROM FrequencyTypeCodeEntity f where :currentDate between f.frequencyTypeStartDt and f.frequencyTypeStopDt order by f.frequencyTypeSortSeqNo")
public class FrequencyTypeCodeEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "Frequency_Type_Cd", insertable = false)
    private String frequencyTypeCd;

    @Column(name = "Frequency_Type_Desc", insertable = false, updatable = false)
    private String frequencyTypeDesc;

    @Column(name = "Frequency_Cd", insertable = false, updatable = false)
    private String frequencyCd;

    @Column(name = "Frequency_Type_Sort_Seq_No", insertable = false, updatable = false)
    private int frequencyTypeSortSeqNo;

    @Column(name = "Frequency_Type_Start_Dt", insertable = false, updatable = false)
    private Date frequencyTypeStartDt;

    @Column(name = "Frequency_Type_Stop_Dt", insertable = false, updatable = false)
    private Date frequencyTypeStopDt;

    @Column(name = "Create_User_Ref_Id", insertable = false, updatable = false)
    private Long createUserRefId;
    @Column(name = "Create_Ts", insertable = false, updatable = false)
    private Timestamp createTime;
    @Column(name = "Update_User_Ref_Id", insertable = false, updatable = false)
    private Long updateUserRefId;
    @Column(name = "Update_Ts", insertable = false, updatable = false)
    private Timestamp updateTime;

    /**
     * 
     */
    public FrequencyTypeCodeEntity() {
        super();
        this.frequencyTypeCd = "";
        this.frequencyTypeDesc = "";
    }

    /**
     * @param frequencyTypeCd
     *        frequencyTypeCd
     * @param frequencyTypeDesc
     *        frequencyTypeDesc
     */
    public FrequencyTypeCodeEntity(String frequencyTypeCd, String frequencyTypeDesc) {
        super();
        this.frequencyTypeCd = frequencyTypeCd;
        this.frequencyTypeDesc = frequencyTypeDesc;
    }

    /**
     * @return the frequencyTypeCd
     */
    public String getFrequencyTypeCd() {
        return frequencyTypeCd;
    }

    /**
     * @param frequencyTypeCd
     *        the frequencyTypeCd to set
     */
    public void setFrequencyTypeCd(String frequencyTypeCd) {
        this.frequencyTypeCd = frequencyTypeCd;
    }

    /**
     * @return the frequencyTypeDesc
     */
    public String getFrequencyTypeDesc() {
        return frequencyTypeDesc;
    }

    /**
     * @param frequencyTypeDesc
     *        the frequencyTypeDesc to set
     */
    public void setFrequencyTypeDesc(String frequencyTypeDesc) {
        this.frequencyTypeDesc = frequencyTypeDesc;
    }

    /**
     * @return the frequencyCd
     */
    public String getFrequencyCd() {
        return frequencyCd;
    }

    /**
     * @param frequencyCd
     *        the frequencyCd to set
     */
    public void setFrequencyCd(String frequencyCd) {
        this.frequencyCd = frequencyCd;
    }

    /**
     * @return the frequencyTypeSortSeqNo
     */
    public int getFrequencyTypeSortSeqNo() {
        return frequencyTypeSortSeqNo;
    }

    /**
     * @param frequencyTypeSortSeqNo
     *        the frequencyTypeSortSeqNo to set
     */
    public void setFrequencyTypeSortSeqNo(int frequencyTypeSortSeqNo) {
        this.frequencyTypeSortSeqNo = frequencyTypeSortSeqNo;
    }

    /**
     * @return the frequencyTypeStartDt
     */
    public Date getFrequencyTypeStartDt() {
        return frequencyTypeStartDt;
    }

    /**
     * @param frequencyTypeStartDt
     *        the frequencyTypeStartDt to set
     */
    public void setFrequencyTypeStartDt(Date frequencyTypeStartDt) {
        this.frequencyTypeStartDt = frequencyTypeStartDt;
    }

    /**
     * @return the frequencyTypeStopDt
     */
    public Date getFrequencyTypeStopDt() {
        return frequencyTypeStopDt;
    }

    /**
     * @param frequencyTypeStopDt
     *        the frequencyTypeStopDt to set
     */
    public void setFrequencyTypeStopDt(Date frequencyTypeStopDt) {
        this.frequencyTypeStopDt = frequencyTypeStopDt;
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
        return "FrequencyTypeCodeEntity [frequencyTypeCd=" + frequencyTypeCd + ", frequencyTypeDesc=" + frequencyTypeDesc + ", frequencyCd=" + frequencyCd + ", frequencyTypeSortSeqNo=" + frequencyTypeSortSeqNo + ", frequencyTypeStartDt=" + frequencyTypeStartDt + ", frequencyTypeStopDt=" + frequencyTypeStopDt + ", createUserRefId=" + createUserRefId + ", createTime=" + createTime + ", updateUserRefId=" + updateUserRefId + ", updateTime=" + updateTime + "]";
    }

}
