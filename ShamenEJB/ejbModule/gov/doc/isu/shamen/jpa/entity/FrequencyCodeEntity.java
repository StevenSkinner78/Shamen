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
@Table(name = "Frequency_Codes", schema = "Code")
@NamedQuery(name = "FrequencyCodeEntity.FIND_ALL_ACTIVE", query = "SELECT f FROM FrequencyCodeEntity f where :currentDate between f.frequencyStartDt and f.frequencyStopDt order by f.frequencySortSeqNo")
public class FrequencyCodeEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "Frequency_Cd", insertable = false)
    private String frequencyCd;

    @Column(name = "Frequency_Desc", insertable = false, updatable = false)
    private String frequencyDesc;

    @Column(name = "Frequency_Sort_Seq_No", insertable = false, updatable = false)
    private int frequencySortSeqNo;

    @Column(name = "Frequency_Start_Dt", insertable = false, updatable = false)
    private Date frequencyStartDt;

    @Column(name = "Frequency_Stop_Dt", insertable = false, updatable = false)
    private Date frequencyStopDt;

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
    public FrequencyCodeEntity() {}

    /**
     * @param frequencyCd
     *        frequencyCd
     * @param frequencyDesc
     *        frequencyDesc
     */
    public FrequencyCodeEntity(String frequencyCd, String frequencyDesc) {
        super();
        this.frequencyCd = frequencyCd;
        this.frequencyDesc = frequencyDesc;
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
     * @return the frequencyDesc
     */
    public String getFrequencyDesc() {
        return frequencyDesc;
    }

    /**
     * @param frequencyDesc
     *        the frequencyDesc to set
     */
    public void setFrequencyDesc(String frequencyDesc) {
        this.frequencyDesc = frequencyDesc;
    }

    /**
     * @return the frequencySortSeqNo
     */
    public int getFrequencySortSeqNo() {
        return frequencySortSeqNo;
    }

    /**
     * @param frequencySortSeqNo
     *        the frequencySortSeqNo to set
     */
    public void setFrequencySortSeqNo(int frequencySortSeqNo) {
        this.frequencySortSeqNo = frequencySortSeqNo;
    }

    /**
     * @return the frequencyStartDt
     */
    public Date getFrequencyStartDt() {
        return frequencyStartDt;
    }

    /**
     * @param frequencyStartDt
     *        the frequencyStartDt to set
     */
    public void setFrequencyStartDt(Date frequencyStartDt) {
        this.frequencyStartDt = frequencyStartDt;
    }

    /**
     * @return the frequencyStopDt
     */
    public Date getFrequencyStopDt() {
        return frequencyStopDt;
    }

    /**
     * @param frequencyStopDt
     *        the frequencyStopDt to set
     */
    public void setFrequencyStopDt(Date frequencyStopDt) {
        this.frequencyStopDt = frequencyStopDt;
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
        return "FrequencyCodeEntity [frequencyCd=" + frequencyCd + ", frequencyDesc=" + frequencyDesc + ", frequencySortSeqNo=" + frequencySortSeqNo + ", frequencyStartDt=" + frequencyStartDt + ", frequencyStopDt=" + frequencyStopDt + ", createUserRefId=" + createUserRefId + ", createTime=" + createTime + ", updateUserRefId=" + updateUserRefId + ", updateTime=" + updateTime + "]";
    }

}
