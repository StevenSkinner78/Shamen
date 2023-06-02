package gov.doc.isu.shamen.jpa.entity;

import static gov.doc.isu.shamen.ejb.util.EJBConstants.DEFAULT_NEW_LINE;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Purpose: CommonModel used to separate the commonly used member variables.
 *
 * @author Steven L. Skinner
 */
@Embeddable
public class CommonEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @Column(name = "Create_User_Ref_Id", insertable = true, updatable = false)
    private Long createUserRefId;
    @Column(name = "Create_Ts", insertable = true, updatable = false)
    private Timestamp createTime;
    @Column(name = "Update_User_Ref_Id", insertable = false, updatable = true)
    private Long updateUserRefId;
    @Column(name = "Update_Ts", insertable = false, updatable = true)
    private Timestamp updateTime;
    @Column(name = "Delete_Ind")
    private String deleteIndicator;

    /**
     * Default Constructor
     */
    public CommonEntity() {
        super();
    }// end constructor

    /**
     * constructor with create and update parameters
     *
     * @param createUserRefId
     *        createUserRefId
     * @param createTime
     *        createTime
     * @param updateUserRefId
     *        updateUserRefId
     * @param updateTime
     *        updateTime
     */
    public CommonEntity(final Long createUserRefId, final Timestamp createTime, final Long updateUserRefId, final Timestamp updateTime) {
        this();
        this.setCreateUserRefId(createUserRefId);
        this.setCreateTime(createTime);
        this.setUpdateUserRefId(updateUserRefId);
        this.setUpdateTime(updateTime);
    }// end constructor

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
    public void setCreateUserRefId(final Long createUserRefId) {
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
    public void setCreateTime(final Timestamp createTime) {
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
    public void setUpdateUserRefId(final Long updateUserRefId) {
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
    public void setUpdateTime(final Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * @return the deleteIndicator
     */
    public String getDeleteIndicator() {
        return deleteIndicator;
    }

    /**
     * @param deleteIndicator
     *        the deleteIndicator to set
     */
    public void setDeleteIndicator(final String deleteIndicator) {
        this.deleteIndicator = deleteIndicator;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer builder = new StringBuffer();
        builder.append("CommonEntity [createUserRefId=");
        builder.append(createUserRefId).append(DEFAULT_NEW_LINE);
        builder.append(", createTime=");
        builder.append(createTime).append(DEFAULT_NEW_LINE);
        builder.append(", updateUserRefId=");
        builder.append(updateUserRefId).append(DEFAULT_NEW_LINE);
        builder.append(", updateTime=");
        builder.append(updateTime).append(DEFAULT_NEW_LINE);
        builder.append(", deleteIndicator=");
        builder.append(deleteIndicator);
        builder.append("]");
        return builder.toString();
    }
}// end class
