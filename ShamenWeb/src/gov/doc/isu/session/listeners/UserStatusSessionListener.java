/**
 * 
 */
package gov.doc.isu.session.listeners;

import java.sql.Time;

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;

import gov.doc.isu.shamen.models.AuthorizedUserModel;
import gov.doc.isu.shamen.resources.AppConstants;

/**
 * Class: UserStatusSessionListener.java Date: May 15, 2015 Description:
 * <p>
 * Monitor AppUser session attribute. Write to log file when user is added and removed.
 * </p>
 * 
 * @author Steven L. Skinner JCCC
 */
public class UserStatusSessionListener implements HttpSessionListener, HttpSessionAttributeListener {

    private static Logger logger = Logger.getLogger(UserStatusSessionListener.class);

    /**
     * Notification that an attribute has been added to a session. Called after the attribute is added.
     * <p>
     * Checks if the attribute is named AppUser and logs information associated with the user.
     * </p>
     * 
     * @param event
     *        {@link HttpSessionBindingEvent}
     */
    public void attributeAdded(HttpSessionBindingEvent event) {
        if(event != null){
            String currentObjectName = event.getName();
            if(currentObjectName.equalsIgnoreCase(AppConstants.APPLICATION_USER)){
                StringBuffer sb = new StringBuffer();
                AuthorizedUserModel user = (AuthorizedUserModel) event.getValue();
                sb.append("[");
                sb.append(user.getUserID());
                sb.append("], [");
                sb.append(user.getFullName());
                sb.append("], [Unavailable], [");
                sb.append(new Time(event.getSession().getCreationTime()));
                sb.append("]");
                logger.info("User Logged In: " + sb.toString());
            }
        }

    }

    /**
     * This method not used.
     * 
     * @see HttpSessionAttributeListener#attributeRemoved(HttpSessionBindingEvent)
     * @param arg0
     *        {@link HttpSessionBindingEvent}
     */
    public void attributeRemoved(HttpSessionBindingEvent arg0) {

    }

    /**
     * This method not used.
     * 
     * @see HttpSessionAttributeListener#attributeReplaced(HttpSessionBindingEvent)
     * @param arg0
     *        {@link HttpSessionBindingEvent}
     */
    public void attributeReplaced(HttpSessionBindingEvent arg0) {

    }

    /**
     ** This method not used.
     * 
     * @param event
     *        -the notification event
     * @see HttpSessionEvent
     */
    public void sessionCreated(HttpSessionEvent event) {}

    /**
     * Notification that a session is about to be invalidated.
     * <p>
     * Checks if the attribute named AppUser was added and logs information associated with the user.
     * </p>
     * 
     * @param event
     *        -the notification event
     * @see HttpSessionEvent
     */
    public void sessionDestroyed(HttpSessionEvent event) {
        if(event.getSession() != null && event.getSession().getAttribute(AppConstants.APPLICATION_USER) != null){
            StringBuffer sb = new StringBuffer();
            AuthorizedUserModel user = (AuthorizedUserModel) event.getSession().getAttribute(AppConstants.APPLICATION_USER);
            sb.append("[");
            sb.append(user.getUserID());
            sb.append("], [");
            sb.append(user.getFullName());
            sb.append("], [Unavailable], [");
            sb.append(new Time(event.getSession().getLastAccessedTime()));
            sb.append("]");
            logger.info("User Logged Out: " + sb.toString());
        }

    }

}
