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
@Table(name = "Controller_Status_Codes", schema = "Code")
@NamedQuery(name = "ControllerStatusCodeEntity.FIND_ALL_ACTIVE", query = "SELECT r FROM ControllerStatusCodeEntity r where :currentDate between r.controllerStatusStartDt and r.controllerStatusStopDt order by r.controllerStatusSortSeqNo")
public class ControllerStatusCodeEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "Controller_Status_Cd", insertable = false)
    private String controllerStatusCd;

    @Column(name = "Controller_Status_Desc", insertable = false, updatable = false)
    private String controllerStatusDesc;

    @Column(name = "Controller_Status_Sort_Seq_No", insertable = false, updatable = false)
    private int controllerStatusSortSeqNo;

    @Column(name = "Controller_Status_Start_Dt", insertable = false, updatable = false)
    private Date controllerStatusStartDt;

    @Column(name = "Controller_Status_Stop_Dt", insertable = false, updatable = false)
    private Date controllerStatusStopDt;

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
    public ControllerStatusCodeEntity() {}

    /**
     * @param controllerStatusCd
     *        controllerStatusCd
     * @param controllerStatusDesc
     *        controllerStatusDesc
     */
    public ControllerStatusCodeEntity(String controllerStatusCd, String controllerStatusDesc) {
        super();
        this.controllerStatusCd = controllerStatusCd;
        this.controllerStatusDesc = controllerStatusDesc;
    }

    /**
     * @return the controllerStatusCd
     */
    public String getControllerStatusCd() {
        return controllerStatusCd;
    }

    /**
     * @param controllerStatusCd
     *        the controllerStatusCd to set
     */
    public void setControllerStatusCd(String controllerStatusCd) {
        this.controllerStatusCd = controllerStatusCd;
    }

    /**
     * @return the controllerStatusDesc
     */
    public String getControllerStatusDesc() {
        return controllerStatusDesc;
    }

    /**
     * @param controllerStatusDesc
     *        the controllerStatusDesc to set
     */
    public void setControllerStatusDesc(String controllerStatusDesc) {
        this.controllerStatusDesc = controllerStatusDesc;
    }

    /**
     * @return the controllerStatusSortSeqNo
     */
    public int getControllerStatusSortSeqNo() {
        return controllerStatusSortSeqNo;
    }

    /**
     * @param controllerStatusSortSeqNo
     *        the controllerStatusSortSeqNo to set
     */
    public void setControllerStatusSortSeqNo(int controllerStatusSortSeqNo) {
        this.controllerStatusSortSeqNo = controllerStatusSortSeqNo;
    }

    /**
     * @return the controllerStatusStartDt
     */
    public Date getControllerStatusStartDt() {
        return controllerStatusStartDt;
    }

    /**
     * @param controllerStatusStartDt
     *        the controllerStatusStartDt to set
     */
    public void setControllerStatusStartDt(Date controllerStatusStartDt) {
        this.controllerStatusStartDt = controllerStatusStartDt;
    }

    /**
     * @return the controllerStatusStopDt
     */
    public Date getControllerStatusStopDt() {
        return controllerStatusStopDt;
    }

    /**
     * @param controllerStatusStopDt
     *        the controllerStatusStopDt to set
     */
    public void setControllerStatusStopDt(Date controllerStatusStopDt) {
        this.controllerStatusStopDt = controllerStatusStopDt;
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
        StringBuffer builder = new StringBuffer();
        builder.append("ControllerStatusCodeEntity [controllerStatusCd=");
        builder.append(controllerStatusCd);
        builder.append(", controllerStatusDesc=");
        builder.append(controllerStatusDesc);
        builder.append(", controllerStatusSortSeqNo=");
        builder.append(controllerStatusSortSeqNo);
        builder.append(", controllerStatusStartDt=");
        builder.append(controllerStatusStartDt);
        builder.append(", controllerStatusStopDt=");
        builder.append(controllerStatusStopDt);
        builder.append(", createUserRefId=");
        builder.append(createUserRefId);
        builder.append(", createTime=");
        builder.append(createTime);
        builder.append(", updateUserRefId=");
        builder.append(updateUserRefId);
        builder.append(", updateTime=");
        builder.append(updateTime);
        builder.append("]");
        return builder.toString();
    }

}
