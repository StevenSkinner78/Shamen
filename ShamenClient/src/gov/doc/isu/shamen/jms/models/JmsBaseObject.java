/**
 * @(#)JmsBaseObject.java 1.0.0 Copyright (c) 2018 Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit All Rights Reserved. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: -Redistribution of source code must retain the above copyright notice, this list of conditions and the following disclaimer. -Redistribution in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. Neither the name of Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit or the names of contributors may be used to endorse or promote products derived from this software without specific prior written permission. This software is provided "AS IS," without a warranty of any kind. ALL EXPRESS OR IMPLIED CONDITIONS,
 *                        REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. You acknowledge that this
 *                        software is not designed, licensed or intended for use in the design, construction, operation or maintenance of any nuclear facility.
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
