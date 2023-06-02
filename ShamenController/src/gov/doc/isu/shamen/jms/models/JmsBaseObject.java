/**
 * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
 */
package gov.doc.isu.shamen.jms.models;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;

/**
 * Model object that holds data common to all models.
 * 
 * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
 */
public class JmsBaseObject implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Timestamp createTs;
    private Long createUserRefId;
    private Timestamp updateTs;
    private Long updateUserRefId;
    private String deleteInd;
    public static final String DEFAULT_NEW_LINE = System.getProperty("line.separator");
    // Comma
    public static final String COMMA = ",";

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
     * @return the createTs
     */
    public Timestamp getCreateTs() {
        return createTs;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
     * @param createTs
     *        the createTs to set
     */
    public void setCreateTs(Timestamp createTs) {
        this.createTs = createTs;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
     * @return the createUserRefId
     */
    public Long getCreateUserRefId() {
        return createUserRefId;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
     * @param createUserRefId
     *        the createUserRefId to set
     */
    public void setCreateUserRefId(Long createUserRefId) {
        this.createUserRefId = createUserRefId;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
     * @return the updateTs
     */
    public Timestamp getUpdateTs() {
        return updateTs;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
     * @param updateTs
     *        the updateTs to set
     */
    public void setUpdateTs(Timestamp updateTs) {
        this.updateTs = updateTs;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
     * @return the updateUserRefId
     */
    public Long getUpdateUserRefId() {
        return updateUserRefId;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
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
        StringBuffer sb = new StringBuffer();
        sb.append("createTs = ").append(createTs).append(DEFAULT_NEW_LINE);
        sb.append("createUserRefId = ").append(createUserRefId).append(DEFAULT_NEW_LINE);
        sb.append("updateTs = ").append(updateTs).append(DEFAULT_NEW_LINE);
        sb.append("updateUserRefId = ").append(updateUserRefId).append(DEFAULT_NEW_LINE);
        sb.append("deleteInd = ").append(deleteInd).append(DEFAULT_NEW_LINE);
        return sb.toString();
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
     * @return the deleteInd
     */
    public String getDeleteInd() {
        return deleteInd;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
     * @param deleteInd
     *        the deleteInd to set
     */
    public void setDeleteInd(String deleteInd) {
        this.deleteInd = deleteInd;
    }

    /**
     * This method is used to check for a <code>null</code> or empty collection.
     * 
     * @param col
     *        The <code>Collection&lt;?&gt;</code> to check
     * @return True if <code>null</code> or empty, false otherwise.
     */
    public static boolean isEmpty(Collection<?> col) {
        boolean result = (col == null || col.size() == 0) ? true : false;
        return result;
    }// end isEmpty
}
