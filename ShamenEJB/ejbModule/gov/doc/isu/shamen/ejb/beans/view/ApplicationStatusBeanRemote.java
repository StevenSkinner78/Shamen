package gov.doc.isu.shamen.ejb.beans.view;

import java.rmi.Remote;
import java.util.HashMap;

import gov.doc.isu.shamen.ejb.jms.ShamenJMSException;
import gov.doc.isu.shamen.jms.ShamenCipher;
import gov.doc.isu.shamen.jms.models.JmsApplication;

/***
 * This is the local business interface for the ApplicationStatusBean.
 *
 * @author Steven Skinner JCCC
 */
public interface ApplicationStatusBeanRemote extends Remote {

    /**
     * This method is used to query database for a list of ApplicationEntity's and load the Bean map of JmsApplication with a default status value.
     *
     * @throws Exception
     *         if an exception occurred
     */
    public void getStatuses() throws Exception;

    /**
     * This method is used to query database for a list of ApplicationEntity's and load the Bean map of JmsApplication with a default status value.
     *
     * @throws Exception
     *         if an exception occurred
     */
    public void setStatuses() throws Exception;

    /**
     * This method retrieves the status from the application by sending a request message to the application topic.
     *
     * @param applicationName
     *        The name of the application
     * @param applicationEnvironment
     *        The application environment
     * @return status
     * @author <strong>Shane Duncan</strong> JCCC, Aug 17, 2015
     */
    public String retrieveStatusFromApplication(String applicationName, String applicationEnvironment);

    /**
     * This method will publish a message with the proper selector so that the correct controller will receive it.
     *
     * @param messageMap
     *        HashMap
     * @param applicationName
     *        String
     * @param applicationEnvironment
     *        String
     * @author <strong>Shane Duncan</strong> JCCC, Sep 23, 2015
     * @throws ShamenJMSException
     *         if an exception occurred
     */
    public void publishMessageToApplication(HashMap<String, String> messageMap, String applicationName, String applicationEnvironment) throws ShamenJMSException;

    /**
     * This method will publish a message to all applications.
     *
     * @author <strong>Shane Duncan</strong> JCCC, Sep 23, 2015
     * @throws ShamenJMSException
     *         if an exception occurred
     */
    public void publishMessageToAllApplication() throws ShamenJMSException;

    /**
     * This method will publish a message with the proper selector so that the correct controller will receive it. It does not encrypt.
     *
     * @param messageMap
     *        HashMap
     * @param applicationName
     *        String
     * @param applicationEnvironment
     *        String
     * @author <strong>Shane Duncan</strong> JCCC, Sep 23, 2015
     * @return Boolean true/false
     * @throws ShamenJMSException
     *         if an exception occurred
     */
    public Boolean publishEncryptedMessageToApplication(HashMap<String, String> messageMap, String applicationName, String applicationEnvironment) throws ShamenJMSException;

    /**
     * This method sets the the status for a single application and loads it into the status map.
     *
     * @param applicationName
     *        name of the application
     * @param applicationEnvironment
     *        name of the application's environment
     * @param status
     *        status of the application
     */
    public void setSingleApplicationStatus(String applicationName, String applicationEnvironment, String status);

    /**
     * This method removes the single application from the apps map on delete.
     *
     * @param applicationName
     *        name of the application
     * @param applicationEnvironment
     *        name of the application's environment
     */
    public void removeApplicationFromMap(String applicationName, String applicationEnvironment);

    /**
     * This method gets the status of a unique application client.
     *
     * @param applicationName
     *        name of the application
     * @param applicationEnvironment
     *        name of the application's environment
     * @return status
     */
    public String getSingleApplicationStatus(String applicationName, String applicationEnvironment);

    /**
     * This method gets a unique application client. If one is not found, it returns null.
     *
     * @param applicationName
     *        name of the application
     * @param applicationEnvironment
     *        name of the application's environment
     * @return JmsApplication
     */
    public JmsApplication getApplication(String applicationName, String applicationEnvironment);

    /**
     * This method sets the the status for a single application and loads it into the application map.
     *
     * @param applicationName
     *        name of the application
     * @param applicationEnvironment
     *        name of the application's environment
     * @param status
     *        status of the application
     * @param sc
     *        ShamenCipher for the application
     */
    public void setSingleApplicationStatus(String applicationName, String applicationEnvironment, String status, ShamenCipher sc);

    /**
     * Return whether or not the handshake has been established for this application. If it has a password, then it has been established.
     *
     * @param applicationName
     *        name of application
     * @param applicationEnvironment
     *        environment of application
     * @return true or false
     */
    public Boolean isHandshakeEstablished(String applicationName, String applicationEnvironment);

    /**
     * Return the ShamenCipher for a given application.
     *
     * @param applicationName
     *        name of application
     * @param applicationEnvironment
     *        environment of application
     * @return sc shamenCipher
     */
    public ShamenCipher getShamenCipher(String applicationName, String applicationEnvironment);

    /**
     * This method establishes the handshake with a particular application via a jms request message.
     *
     * @param applicationName
     *        name of application
     * @param applicationEnvironment
     *        environment of application
     * @return true or false
     */
    public Boolean establishHandshake(String applicationName, String applicationEnvironment);

    /**
     * This method establishes the handshake for all loaded applications.
     *
     * @throws Exception
     *         if an exception occurred
     */
    public void establishAllHandShakes() throws Exception;

    /**
     * This method is a getter for the system start time.
     *
     * @return the start time
     */
    public long getStartTime();

    /**
     * This method gets the Jms Applications
     *
     * @return the apps
     */
    public HashMap<String, JmsApplication> getApps();

    /**
     * This method sets the JmsApplications to the HashMap
     *
     * @param apps
     *        the apps to set
     */
    public void setApps(HashMap<String, JmsApplication> apps);
}
