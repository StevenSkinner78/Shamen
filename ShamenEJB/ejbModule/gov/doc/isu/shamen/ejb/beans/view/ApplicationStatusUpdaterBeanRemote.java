package gov.doc.isu.shamen.ejb.beans.view;

import java.io.Serializable;

import javax.ejb.Remote;

/***
 * This is the local business interface for the ApplicationStatusUpdateBean.
 *
 * @author Shane Duncan JCCC
 */
@Remote
public interface ApplicationStatusUpdaterBeanRemote extends Serializable {
    public static final String STATUS_UNRESPONSIVE = "UNR";
    public static final String STATUS_SUSPENDED = "SUP";
    public static final String STATUS_ACTIVE = "ACT";
    public static final String STATUS_INACTIVE = "INA";
    public static final String STATUS_INFORMATION = "INF";
    public static final String STATUS_AWAITING_RESPONSE = "AWR";

    /**
     * This method is used to reset connection with an application
     * 
     * @param applicationRefId
     *        the application ref id to retrieve
     * @param status
     *        the status to reset to
     * @param comment
     *        the comment sent from remote
     */
    public void resetApplicationStatusAsync(Long applicationRefId, String status, String comment);

}
