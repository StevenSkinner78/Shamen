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
 * The persistent class for the Result_Codes database table.
 */
@ReadOnly
@Entity
@Table(name = "Result_Codes", schema = "Code")
@NamedQuery(name = "ResultCodeEntity.FIND_ALL_ACTIVE", query = "SELECT r FROM ResultCodeEntity r where :currentDate between r.resultStartDt and r.resultStopDt order by r.resultSortSeqNo")
public class ResultCodeEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "Result_Cd", insertable = false)
    private String resultCd;

    @Column(name = "Result_Desc", insertable = false, updatable = false)
    private String resultDesc;

    @Column(name = "Result_Sort_Seq_No", insertable = false, updatable = false)
    private int resultSortSeqNo;

    @Column(name = "Result_Start_Dt", insertable = false, updatable = false)
    private Date resultStartDt;

    @Column(name = "Result_Stop_Dt", insertable = false, updatable = false)
    private Date resultStopDt;

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
    public ResultCodeEntity() {}

    /**
     * @param resultCd
     *        resultCd
     * @param resultDesc
     *        resultDesc
     */
    public ResultCodeEntity(String resultCd, String resultDesc) {
        super();
        this.resultCd = resultCd;
        this.resultDesc = resultDesc;
    }

    /**
     * @return the resultCd
     */
    public String getResultCd() {
        return resultCd;
    }

    /**
     * @param resultCd
     *        the resultCd to set
     */
    public void setResultCd(String resultCd) {
        this.resultCd = resultCd;
    }

    /**
     * @return the resultDesc
     */
    public String getResultDesc() {
        return resultDesc;
    }

    /**
     * @param resultDesc
     *        the resultDesc to set
     */
    public void setResultDesc(String resultDesc) {
        this.resultDesc = resultDesc;
    }

    /**
     * @return the resultSortSeqNo
     */
    public int getResultSortSeqNo() {
        return resultSortSeqNo;
    }

    /**
     * @param resultSortSeqNo
     *        the resultSortSeqNo to set
     */
    public void setResultSortSeqNo(int resultSortSeqNo) {
        this.resultSortSeqNo = resultSortSeqNo;
    }

    /**
     * @return the resultStartDt
     */
    public Date getResultStartDt() {
        return resultStartDt;
    }

    /**
     * @param resultStartDt
     *        the resultStartDt to set
     */
    public void setResultStartDt(Date resultStartDt) {
        this.resultStartDt = resultStartDt;
    }

    /**
     * @return the resultStopDt
     */
    public Date getResultStopDt() {
        return resultStopDt;
    }

    /**
     * @param resultStopDt
     *        the resultStopDt to set
     */
    public void setResultStopDt(Date resultStopDt) {
        this.resultStopDt = resultStopDt;
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
        return "ResultCodeEntity [resultCd=" + resultCd + ", resultDesc=" + resultDesc + ", resultSortSeqNo=" + resultSortSeqNo + ", resultStartDt=" + resultStartDt + ", resultStopDt=" + resultStopDt + ", createUserRefId=" + createUserRefId + ", createTime=" + createTime + ", updateUserRefId=" + updateUserRefId + ", updateTime=" + updateTime + "]";
    }

}
