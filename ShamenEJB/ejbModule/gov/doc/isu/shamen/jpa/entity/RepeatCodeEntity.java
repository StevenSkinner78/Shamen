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
 * The persistent class for the Repeat_Codes database table.
 */
@ReadOnly
@Entity
@Table(name = "Repeat_Codes", schema = "Code")
@NamedQuery(name = "RepeatCodeEntity.FIND_ALL_ACTIVE", query = "SELECT r FROM RepeatCodeEntity r where :currentDate between r.repeatStartDt and r.repeatStopDt order by r.repeatSortSeqNo")
public class RepeatCodeEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "Repeat_Cd", insertable = false)
    private String repeatCd;

    @Column(name = "Repeat_Desc", insertable = false, updatable = false)
    private String repeatDesc;

    @Column(name = "Repeat_Sort_Seq_No", insertable = false, updatable = false)
    private int repeatSortSeqNo;

    @Column(name = "Repeat_Start_Dt", insertable = false, updatable = false)
    private Date repeatStartDt;

    @Column(name = "Repeat_Stop_Dt", insertable = false, updatable = false)
    private Date repeatStopDt;

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
    public RepeatCodeEntity() {
        super();
        this.repeatCd = "";
        this.repeatDesc = "";
    }

    /**
     * @param repeatCd
     *        repeatCd
     * @param repeatDesc
     *        repeatDesc
     */
    public RepeatCodeEntity(String repeatCd, String repeatDesc) {
        super();
        this.repeatCd = repeatCd;
        this.repeatDesc = repeatDesc;
    }

    /**
     * @return the repeatCd
     */
    public String getRepeatCd() {
        return repeatCd;
    }

    /**
     * @param repeatCd
     *        the repeatCd to set
     */
    public void setRepeatCd(String repeatCd) {
        this.repeatCd = repeatCd;
    }

    /**
     * @return the repeatDesc
     */
    public String getRepeatDesc() {
        return repeatDesc;
    }

    /**
     * @param repeatDesc
     *        the repeatDesc to set
     */
    public void setRepeatDesc(String repeatDesc) {
        this.repeatDesc = repeatDesc;
    }

    /**
     * @return the repeatSortSeqNo
     */
    public int getRepeatSortSeqNo() {
        return repeatSortSeqNo;
    }

    /**
     * @param repeatSortSeqNo
     *        the repeatSortSeqNo to set
     */
    public void setRepeatSortSeqNo(int repeatSortSeqNo) {
        this.repeatSortSeqNo = repeatSortSeqNo;
    }

    /**
     * @return the repeatStartDt
     */
    public Date getRepeatStartDt() {
        return repeatStartDt;
    }

    /**
     * @param repeatStartDt
     *        the repeatStartDt to set
     */
    public void setRepeatStartDt(Date repeatStartDt) {
        this.repeatStartDt = repeatStartDt;
    }

    /**
     * @return the repeatStopDt
     */
    public Date getRepeatStopDt() {
        return repeatStopDt;
    }

    /**
     * @param repeatStopDt
     *        the repeatStopDt to set
     */
    public void setRepeatStopDt(Date repeatStopDt) {
        this.repeatStopDt = repeatStopDt;
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
        return "RepeatCodeEntity [repeatCd=" + repeatCd + ", repeatDesc=" + repeatDesc + ", repeatSortSeqNo=" + repeatSortSeqNo + ", repeatStartDt=" + repeatStartDt + ", repeatStopDt=" + repeatStopDt + ", createUserRefId=" + createUserRefId + ", createTime=" + createTime + ", updateUserRefId=" + updateUserRefId + ", updateTime=" + updateTime + "]";
    }

}
