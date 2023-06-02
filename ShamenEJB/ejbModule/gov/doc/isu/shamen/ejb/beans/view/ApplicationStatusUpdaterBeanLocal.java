package gov.doc.isu.shamen.ejb.beans.view;

import java.io.Serializable;

import javax.ejb.Local;

import gov.doc.isu.shamen.jms.ShamenCipher;
import gov.doc.isu.shamen.jms.models.JmsApplication;
import gov.doc.isu.shamen.jpa.entity.ApplicationEntity;
import gov.doc.isu.shamen.jpa.entity.ApplicationInstanceEntity;

/***
 * This is the local business interface for the ApplicationStatusUpdateBean.
 *
 * @author Shane Duncan JCCC
 */
@Local
public interface ApplicationStatusUpdaterBeanLocal extends Serializable {
    public static final String STATUS_UNRESPONSIVE = "UNR";
    public static final String STATUS_SUSPENDED = "SUP";
    public static final String STATUS_ACTIVE = "ACT";
    public static final String STATUS_INACTIVE = "INA";
    public static final String STATUS_INFORMATION = "INF";
    public static final String STATUS_AWAITING_RESPONSE = "AWR";

    /**
     * Send the status to the application.
     *
     * @param app
     *        ApplicationEntity
     * @param instance
     *        ApplicationInstanceEntity
     * @param statusComment
     *        comment associated with the status
     * @param retry
     *        attempt again if it fails(true/false)
     * @return application Loaded with what is necessary or null if no contact
     */
    public JmsApplication sendStatusUpdateToApplication(ApplicationEntity app, ApplicationInstanceEntity instance, String statusComment, Boolean retry);

    /**
     * This method is run asynchronously and requests a status update from an application
     *
     * @param entity
     *        application
     * @param instanceEntity
     *        instance
     */
    public void retrieveAsyncStatusFromApplication(ApplicationEntity entity, ApplicationInstanceEntity instanceEntity);

    /**
     * This method requests a status update from an application
     *
     * @param application
     *        application
     * @param instance
     *        ApplicationInstanceEntity
     */
    public void verifyAppStatus(ApplicationEntity application, ApplicationInstanceEntity instance);

    /**
     * This method sets the the status for a single application instance and loads it into the DB.
     *
     * @param instance
     *        applicationInstance
     * @param status
     *        status
     * @param sc
     *        shamenCipher
     * @throws Exception
     *         if an exception occurred
     */
    public void setSingleApplicationInstanceStatus(ApplicationInstanceEntity instance, String status, ShamenCipher sc) throws Exception;

    /**
     * This method sets all the applications to the same status and loads it into the DB.
     *
     * @param status
     *        status
     */
    public void setAllApplicationInstanceStatus(String status);

    /**
     * This method sets all the applications to the same status and loads it into the DB.
     *
     * @param application
     *        ApplicationEntity
     * @param status
     *        status
     */
    public void setAllInstanceStatusesForApplication(ApplicationEntity application, String status);

    /**
     * This method will send a message to all instances of all applications and force them to register themselves.
     */
    public void requestRegistrationAll();

    /**
     * This method will send a message to all instances of an application and force them to register themselves.
     *
     * @param application
     *        fully loaded applicationEntity
     */
    public void requestRegistrationApplication(ApplicationEntity application);

    /**
     * This method sets the the status for a single application and loads it into the DB.
     *
     * @param instance
     *        applicationInstance
     * @param status
     *        status
     * @throws Exception
     *         if an exception occurred
     */
    public void setSingleApplicationInstanceStatus(ApplicationInstanceEntity instance, String status) throws Exception;

    /**
     * This method is used to refresh connections with all applications.
     */
    public void resetAllApplicationStatusesAsync();

    /**
     * This method is used to refresh connection with an application
     *
     * @param application
     *        fully loaded applicationEntity
     */
    public void resetApplicationStatusAsync(ApplicationEntity application);
}
