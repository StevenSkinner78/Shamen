package gov.doc.isu.sdk;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import gov.doc.isu.models.SystemUser;

/**
 * Class: JSPErrorHandler.java Date: Mar 30, 2006 Description:
 * <p>
 * Error handler to print the errors generated from a JSP
 * </p>
 * 
 * @author Michael R. Dirks
 * @author Joseph Burris JCCC - modification author
 */
public class JSPErrorHandler {
    private static Logger log = Logger.getLogger("gov.doc.isu.sdk.JSPErrorHandler");

    /**
     * Used to log errors.
     * 
     * @param request
     *        HttpServletRequest
     * @param e
     *        Throwable
     * @return String Current date
     */
    public static String error(HttpServletRequest request, Throwable e) {
        String dateTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss,SSS").format(new Date());
        String errMsg = buildErrorMessage(dateTime, request, e);
        log.error(errMsg);
        return dateTime;
    }// end error

    /**
     * Used to log in debug
     * 
     * @param request
     *        HttpServletRequest
     * @param e
     *        Throwable
     * @return String Current date
     */
    public static String debug(HttpServletRequest request, Throwable e) {
        String dateTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss,SSS").format(new Date());
        String errMsg = buildErrorMessage(dateTime, request, e);
        if(log.isDebugEnabled()){
            log.debug(errMsg);
        }// end if
        return dateTime;
    }// end debug

    /**
     * Used to see if debug is enabled.
     * 
     * @return boolean True if debug is enabled, false otherwise.
     */
    public static boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }// end isDebugEnabled

    /**
     * Used to build error messages.
     * 
     * @param dateTime
     *        The current date and time
     * @param request
     *        HttpServletRequest
     * @param e
     *        Throwable
     * @return String the error message to log
     */
    protected static String buildErrorMessage(String dateTime, HttpServletRequest request, Throwable e) {
        // Retrieve the session from the request object.
        HttpSession session = request.getSession();

        // Store the user ID of the logged on system user.
        String userId = "null";
        if(session != null){
            if(session.getAttribute(SystemUser.SESSION_SYSTEM_USER) != null){
                SystemUser user = (SystemUser) session.getAttribute(SystemUser.SESSION_SYSTEM_USER);
                userId = user.getSystemUserId();
            }else{
                log.debug("SESSION_SYSTEM_USER key does not have a value.");
            }// end if
        }else{
            log.warn("Session is null. Unable to retrieve the SESSION_SYSTEM_USER value.");
        }// end if

        // getting the ip of the users machine
        String ipAddress = (null != request.getHeader("X-Forwarded-For") ? request.getHeader("X-Forwarded-For") : request.getRemoteAddr());

        String eName = "null";
        if(null != e){
            if(null != e.getClass()){
                eName = e.getClass().toString();
                if(null != e.getClass().getName()){
                    eName = e.getClass().getName();
                }// end if
            }// end if
        }// end if
        String eMsg = "null";
        if(null != e){
            if(null != e.getMessage()){
                eMsg = e.getMessage();
            }// end if
        }// end if
        return "[JSP ERROR], [" + dateTime + "],  [Request URL: " + request.getRequestURL() + "], [IP Address: " + ipAddress + "], [user ID:" + userId + "], [error:" + eName + "], [message: " + eMsg + "]";
    }// end buildErrorMessage
}// end class
