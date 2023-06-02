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
 * The persistent class for the Status_Codes database table.
 */
@ReadOnly
@Entity
@Table(name = "Status_Codes", schema = "Code")
@NamedQuery(name = "StatusCodeEntity.FIND_ALL_ACTIVE", query = "SELECT s FROM StatusCodeEntity s where :currentDate between s.statusStartDt and s.statusStopDt order by s.statusSortSeqNo")
public class StatusCodeEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "Status_Cd", insertable = false)
    private String statusCd;

    @Column(name = "Status_Desc", insertable = false, updatable = false)
    private String statusDesc;

    @Column(name = "Status_Sort_Seq_No", insertable = false, updatable = false)
    private int statusSortSeqNo;

    @Column(name = "Status_Start_Dt", insertable = false, updatable = false)
    private Date statusStartDt;

    @Column(name = "Status_Stop_Dt", insertable = false, updatable = false)
    private Date statusStopDt;

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
    public StatusCodeEntity() {}

    /**
     * @param statusCd
     *        statusCd
     * @param statusDesc
     *        statusDesc
     */
    public StatusCodeEntity(String statusCd, String statusDesc) {
        super();
        this.statusCd = statusCd;
        this.statusDesc = statusDesc;
    }

    /**
     * @return the statusCd
     */
    public String getStatusCd() {
        return statusCd;
    }

    /**
     * @param statusCd
     *        the statusCd to set
     */
    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }

    /**
     * @return the statusDesc
     */
    public String getStatusDesc() {
        return statusDesc;
    }

    /**
     * @param statusDesc
     *        the statusDesc to set
     */
    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    /**
     * @return the statusSortSeqNo
     */
    public int getStatusSortSeqNo() {
        return statusSortSeqNo;
    }

    /**
     * @param statusSortSeqNo
     *        the statusSortSeqNo to set
     */
    public void setStatusSortSeqNo(int statusSortSeqNo) {
        this.statusSortSeqNo = statusSortSeqNo;
    }

    /**
     * @return the statusStartDt
     */
    public Date getStatusStartDt() {
        return statusStartDt;
    }

    /**
     * @param statusStartDt
     *        the statusStartDt to set
     */
    public void setStatusStartDt(Date statusStartDt) {
        this.statusStartDt = statusStartDt;
    }

    /**
     * @return the statusStopDt
     */
    public Date getStatusStopDt() {
        return statusStopDt;
    }

    /**
     * @param statusStopDt
     *        the statusStopDt to set
     */
    public void setStatusStopDt(Date statusStopDt) {
        this.statusStopDt = statusStopDt;
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
        return "StatusCodeEntity [statusCd=" + statusCd + ", statusDesc=" + statusDesc + ", statusSortSeqNo=" + statusSortSeqNo + ", statusStartDt=" + statusStartDt + ", statusStopDt=" + statusStopDt + ", createUserRefId=" + createUserRefId + ", createTime=" + createTime + ", updateUserRefId=" + updateUserRefId + ", updateTime=" + updateTime + "]";
    }
}
