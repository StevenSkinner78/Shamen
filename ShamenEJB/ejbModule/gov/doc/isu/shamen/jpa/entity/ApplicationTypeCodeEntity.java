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
 * The persistent class for the Application_Type_Codes database table.
 */
@ReadOnly
@Entity
@Table(name = "Application_Type_Codes", schema = "Code")
@NamedQuery(name = "ApplicationTypeCodeEntity.FIND_ALL_ACTIVE", query = "SELECT a FROM ApplicationTypeCodeEntity a where :currentDate between a.applicationTypeStartDt and a.applicationTypeStopDt order by a.applicationTypeSortSeqNo")
public class ApplicationTypeCodeEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "Application_Type_Cd", insertable = false)
    private String applicationTypeCd;

    @Column(name = "Application_Type_Desc", insertable = false, updatable = false)
    private String applicationTypeDesc;

    @Column(name = "Application_Type_Sort_Seq_No", insertable = false, updatable = false)
    private int applicationTypeSortSeqNo;

    @Column(name = "Application_Type_Start_Dt", insertable = false, updatable = false)
    private Date applicationTypeStartDt;

    @Column(name = "Application_Type_Stop_Dt", insertable = false, updatable = false)
    private Date applicationTypeStopDt;

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
    public ApplicationTypeCodeEntity() {
        super();
        this.applicationTypeCd = "";
        this.applicationTypeDesc = "";
    }

    /**
     * @param applicationTypeCd
     *        applicationTypeCd
     * @param applicationTypeDesc
     *        applicationTypeDesc
     */
    public ApplicationTypeCodeEntity(String applicationTypeCd, String applicationTypeDesc) {
        super();
        this.applicationTypeCd = applicationTypeCd;
        this.applicationTypeDesc = applicationTypeDesc;
    }

    /**
     * @return the applicationTypeCd
     */
    public String getApplicationTypeCd() {
        return applicationTypeCd;
    }

    /**
     * @param applicationTypeCd
     *        the applicationTypeCd to set
     */
    public void setApplicationTypeCd(String applicationTypeCd) {
        this.applicationTypeCd = applicationTypeCd;
    }

    /**
     * @return the applicationTypeDesc
     */
    public String getApplicationTypeDesc() {
        return applicationTypeDesc;
    }

    /**
     * @param applicationTypeDesc
     *        the applicationTypeDesc to set
     */
    public void setApplicationTypeDesc(String applicationTypeDesc) {
        this.applicationTypeDesc = applicationTypeDesc;
    }

    /**
     * @return the applicationTypeSortSeqNo
     */
    public int getApplicationTypeSortSeqNo() {
        return applicationTypeSortSeqNo;
    }

    /**
     * @param applicationTypeSortSeqNo
     *        the applicationTypeSortSeqNo to set
     */
    public void setApplicationTypeSortSeqNo(int applicationTypeSortSeqNo) {
        this.applicationTypeSortSeqNo = applicationTypeSortSeqNo;
    }

    /**
     * @return the applicationTypeStartDt
     */
    public Date getApplicationTypeStartDt() {
        return applicationTypeStartDt;
    }

    /**
     * @param applicationTypeStartDt
     *        the applicationTypeStartDt to set
     */
    public void setApplicationTypeStartDt(Date applicationTypeStartDt) {
        this.applicationTypeStartDt = applicationTypeStartDt;
    }

    /**
     * @return the applicationTypeStopDt
     */
    public Date getApplicationTypeStopDt() {
        return applicationTypeStopDt;
    }

    /**
     * @param applicationTypeStopDt
     *        the applicationTypeStopDt to set
     */
    public void setApplicationTypeStopDt(Date applicationTypeStopDt) {
        this.applicationTypeStopDt = applicationTypeStopDt;
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
        builder.append("ApplicationTypeCodeEntity [applicationTypeCd=");
        builder.append(applicationTypeCd);
        builder.append(", applicationTypeDesc=");
        builder.append(applicationTypeDesc);
        builder.append(", applicationTypeSortSeqNo=");
        builder.append(applicationTypeSortSeqNo);
        builder.append(", applicationTypeStartDt=");
        builder.append(applicationTypeStartDt);
        builder.append(", applicationTypeStopDt=");
        builder.append(applicationTypeStopDt);
        builder.append("]");
        return builder.toString();
    }

}
