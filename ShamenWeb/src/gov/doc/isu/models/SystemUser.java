/**
 * 
 */
package gov.doc.isu.models;

import java.io.Serializable;

/**
 * This class is used to model the basic properties of a logged in system user, including the system user reference ID and the system user ID. These values are set once the system user has been authenticated and authorized, and placed in session for use in the application.
 * <p>
 * This class may be retrieved from the session with key <code>SESSION_SYSTEM_USER</code>. Example:
 * 
 * <pre>
 * String userRefId = (String) request.getSession().getAttribute(SystemUser.SESSION_SYSTEM_USER));
 * </pre>
 * 
 * @author Joseph Burris JCCC
 */
public class SystemUser implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String systemUserRefId;
    private String systemUserId;
    public static final String SESSION_SYSTEM_USER = "sessionSystemUser";

    /**
     * This default constructor may be used to instantiate this class.
     */
    public SystemUser() {
        super();
    }// end constructor

    /**
     * This overridden constructor may be used to instantiate this class with the value of the system user ID.
     * 
     * @param systemUserId
     *        The system user ID used to log on to the application.
     */
    public SystemUser(String systemUserId) {
        this();
        this.systemUserId = systemUserId;
    }// end constructor

    /**
     * This overridden constructor may be used to instantiate this class with the value of the system user reference ID and the system user ID.
     * 
     * @param systemUserRefId
     *        The system user reference ID as maintained as the primary key in the database.
     * @param systemUserId
     *        The system user ID used to log on to the application.
     */
    public SystemUser(String systemUserRefId, String systemUserId) {
        this();
        this.systemUserRefId = systemUserRefId;
        this.systemUserId = systemUserId;
    }// end constructor

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "SystemUser [" + (systemUserRefId != null ? "systemUserRefId=" + systemUserRefId + ", " : "") + (systemUserId != null ? "systemUserId=" + systemUserId : "") + "]";
    }// end toString

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((systemUserId == null) ? 0 : systemUserId.hashCode());
        result = prime * result + ((systemUserRefId == null) ? 0 : systemUserRefId.hashCode());
        return result;
    }// end hashCode

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        }// end if
        if(obj == null){
            return false;
        }// end if
        if(!(obj instanceof SystemUser)){
            return false;
        }// end if
        SystemUser other = (SystemUser) obj;
        if(systemUserId == null){
            if(other.systemUserId != null){
                return false;
            }// end if
        }else if(!systemUserId.equals(other.systemUserId)){
            return false;
        }// end if
        if(systemUserRefId == null){
            if(other.systemUserRefId != null){
                return false;
            }// end if
        }else if(!systemUserRefId.equals(other.systemUserRefId)){
            return false;
        }// end if
        return true;
    }// end equals

    /**
     * @return the systemUserRefId
     */
    public String getSystemUserRefId() {
        return systemUserRefId;
    }// end getSystemUserRefId

    /**
     * @param systemUserRefId
     *        the systemUserRefId to set
     */
    public void setSystemUserRefId(String systemUserRefId) {
        this.systemUserRefId = null != systemUserRefId ? systemUserRefId.trim() : systemUserRefId;
    }// end setSystemUserRefId

    /**
     * @return the systemUserId
     */
    public String getSystemUserId() {
        return systemUserId;
    }// end getSystemUserId

    /**
     * @param systemUserId
     *        the systemUserId to set
     */
    public void setSystemUserId(String systemUserId) {
        this.systemUserId = null != systemUserId ? systemUserId.trim() : systemUserId;
    }// end setSystemUserId
}// end class
