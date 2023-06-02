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
 * The persistent class for the Application_Status_Codes database table.
 */
@ReadOnly
@Entity
@Table(name = "Application_Status_Codes", schema = "Code")
@NamedQuery(name = "ApplicationStatusCodeEntity.FIND_ALL_ACTIVE", query = "SELECT a FROM ApplicationStatusCodeEntity a where :currentDate between a.applicationStatusStartDt and a.applicationStatusStopDt order by a.applicationStatusSortSeqNo")
public class ApplicationStatusCodeEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "Application_Status_Cd", insertable = false)
    private String applicationStatusCd;

    @Column(name = "Application_Status_Desc", insertable = false, updatable = false)
    private String applicationStatusDesc;

    @Column(name = "Application_Status_Sort_Seq_No", insertable = false, updatable = false)
    private int applicationStatusSortSeqNo;

    @Column(name = "Application_Status_Start_Dt", insertable = false, updatable = false)
    private Date applicationStatusStartDt;

    @Column(name = "Application_Status_Stop_Dt", insertable = false, updatable = false)
    private Date applicationStatusStopDt;

    @Column(name = "Create_Ts", insertable = false, updatable = false)
    private Timestamp createTime;

    @Column(name = "Create_User_Ref_Id", insertable = false, updatable = false)
    private Long createUserRefId;

    @Column(name = "Update_Ts", insertable = false, updatable = false)
    private Timestamp updateTime;

    @Column(name = "Update_User_Ref_Id", insertable = false, updatable = false)
    private Long updateUserRefId;

    /**
     * Default Constructor
     */
    public ApplicationStatusCodeEntity() {}

    /**
     * Constructor
     * 
     * @param applicationStatusCd
     *        applicationStatusDesc
     * @param applicationStatusDesc
     *        applicationStatusDesc
     */
    public ApplicationStatusCodeEntity(String applicationStatusCd, String applicationStatusDesc) {
        super();
        this.applicationStatusCd = applicationStatusCd;
        this.applicationStatusDesc = applicationStatusDesc;
    }

    /**
     * @return the applicationStatusCd
     */
    public String getApplicationStatusCd() {
        return applicationStatusCd;
    }

    /**
     * @param applicationStatusCd
     *        the applicationStatusCd to set
     */
    public void setApplicationStatusCd(String applicationStatusCd) {
        this.applicationStatusCd = applicationStatusCd;
    }

    /**
     * @return the applicationStatusDesc
     */
    public String getApplicationStatusDesc() {
        return applicationStatusDesc;
    }

    /**
     * @param applicationStatusDesc
     *        the applicationStatusDesc to set
     */
    public void setApplicationStatusDesc(String applicationStatusDesc) {
        this.applicationStatusDesc = applicationStatusDesc;
    }

    /**
     * @return the applicationStatusSortSeqNo
     */
    public int getApplicationStatusSortSeqNo() {
        return applicationStatusSortSeqNo;
    }

    /**
     * @param applicationStatusSortSeqNo
     *        the applicationStatusSortSeqNo to set
     */
    public void setApplicationStatusSortSeqNo(int applicationStatusSortSeqNo) {
        this.applicationStatusSortSeqNo = applicationStatusSortSeqNo;
    }

    /**
     * @return the applicationStatusStartDt
     */
    public Date getApplicationStatusStartDt() {
        return applicationStatusStartDt;
    }

    /**
     * @param applicationStatusStartDt
     *        the applicationStatusStartDt to set
     */
    public void setApplicationStatusStartDt(Date applicationStatusStartDt) {
        this.applicationStatusStartDt = applicationStatusStartDt;
    }

    /**
     * @return the applicationStatusStopDt
     */
    public Date getApplicationStatusStopDt() {
        return applicationStatusStopDt;
    }

    /**
     * @param applicationStatusStopDt
     *        the applicationStatusStopDt to set
     */
    public void setApplicationStatusStopDt(Date applicationStatusStopDt) {
        this.applicationStatusStopDt = applicationStatusStopDt;
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

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer builder = new StringBuffer();
        builder.append("ApplicationStatusCodeEntity [applicationStatusCd=");
        builder.append(applicationStatusCd);
        builder.append(", applicationStatusDesc=");
        builder.append(applicationStatusDesc);
        builder.append(", applicationStatusSortSeqNo=");
        builder.append(applicationStatusSortSeqNo);
        builder.append(", applicationStatusStartDt=");
        builder.append(applicationStatusStartDt);
        builder.append(", applicationStatusStopDt=");
        builder.append(applicationStatusStopDt);
        builder.append(", createTime=");
        builder.append(createTime);
        builder.append(", createUserRefId=");
        builder.append(createUserRefId);
        builder.append(", updateTime=");
        builder.append(updateTime);
        builder.append(", updateUserRefId=");
        builder.append(updateUserRefId);
        builder.append("]");
        return builder.toString();
    }

}
