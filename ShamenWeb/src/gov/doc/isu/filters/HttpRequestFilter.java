package gov.doc.isu.filters;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import gov.doc.isu.models.SystemUser;

/**
 * Class: HttpRequestFilter.java Date: Jan 12, 2006 Description:
 * <p>
 * Filter to log performance issues.
 * </p>
 * <p>
 * <u>Modifications:</u>
 * <ul>
 * <li>03/06/2023 - removed reflection ({@code instanceof}), {@code StringBuilder} is taking Object (no primitives were being checked) no matter the Type because of Type erasure. Also removes possible {@code ClassCastException} for casting to String when no other type had been matched.</li>
 * <li>03/08/2023 - Added {@link #getUserAgentString(HttpServletRequest)} method.</li>
 * </ul>
 * 
 * @author Michael R. Dirks,Dwayne T. Walker
 * @author Andrew Fagre JCCC
 * @author Richard Salas JCCC
 * @author Modified: Leroy Norman JCCC
 * @author Modified: Michael Porter JCCC
 */
public class HttpRequestFilter implements Filter {
    private static Logger logger = Logger.getLogger("gov.doc.isu.filters.HttpRequestFilter");
    private FilterConfig filterConfig;

    /**
     * <p>
     * Called by the web container to indicate to a filter that it is being placed into service.
     * </p>
     * <p>
     * The servlet container calls the init method exactly once after instantiating the filter. The init method must complete successfully before the filter is asked to do any filtering work.
     * </p>
     * <p>
     * The web container cannot place the filter into service if the init method either
     * </p>
     * <ol>
     * <li>Throws a ServletException</li>
     * <li>Does not return within a time period defined by the web container</li>
     * </ol>
     *
     * @param config
     *        the filter configuration object used by the servlet container to pass information to a filter during initialization.
     * @throws ServletException
     *         general exception the servlet throws when it encounters difficulty.
     */
    public void init(FilterConfig config) throws ServletException {
        this.filterConfig = config;
        logger.debug("Exiting init.");
    }// end method

    /**
     * Performs the HTTP request filter action that displays the HTTP request information in the system output log.
     *
     * @param req
     *        HTTP Request Object
     * @param resp
     *        HTTP Response Object
     * @param chain
     *        filter chain
     * @throws javax.servlet.ServletException
     *         exeption that can be thrown by this method
     * @throws java.io.IOException
     *         exeption that can be thrown by this method
     */
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        logger.debug("Entering doFilter()");
        HttpServletRequest request = (HttpServletRequest) req;

        // Retrieve the session from the request object.
        HttpSession session = request.getSession();

        // Store the user ID of the logged on system user.
        String userId = "null";
        if(session != null){
            if(session.getAttribute(SystemUser.SESSION_SYSTEM_USER) != null){
                SystemUser user = (SystemUser) session.getAttribute(SystemUser.SESSION_SYSTEM_USER);
                userId = user.getSystemUserId();
            }else{
                logger.debug("SESSION_SYSTEM_USER key does not have a value.");
            }// end if
        }else{
            logger.warn("Session is null. Unable to retrieve the SESSION_SYSTEM_USER value.");
        }// end if

        // retrieving user-agent string from request header
        String userAgent = getUserAgentString(request);

        // getting the ip of the users machine
        String ipAddress = (null != request.getHeader("X-Forwarded-For") ? request.getHeader("X-Forwarded-For") : request.getRemoteAddr());

        // Store the system timestamp so that we have a fairly unique
        // identifier for requests that come in
        // that we can use for tracking in the log
        String systemTimestamp = new SimpleDateFormat("[MM/dd/yyyy hh:mm:ss.SSS]").format(new Date());

        // Store the system timestamp as a date value also so that we can
        // record how many milliseconds each transaction takes
        java.util.Date start = new java.util.Date();

        // Store the request URL so that we can associate that with the
        // response time to
        // easily see which transactions are responding poorly.
        String urlString = request.getRequestURL().toString().concat(getHttpRequestMethodParameter(request));

        // Write the details of the request to the output log
        try{
            // Allow the request to be processed by the web application
            chain.doFilter(req, resp);
        }catch(ServletException e){
            StringBuilder sb = new StringBuilder("A ServletException was caught in the HttpRequestFilter doFilter() method. Exception Message: ").append(e.getMessage()).append(". Some Details follow: ").append("[request=").append(request).append(", ipAddress=").append(ipAddress).append(", userId=").append(userId).append(", systemTimestamp=").append(systemTimestamp).append(", start=").append(start).append(", urlString=").append(urlString).append("]");
            logger.error(sb.toString(), e);
            throw e;
        }catch(IOException e){
            StringBuilder sb = new StringBuilder("An IOException was caught in the HttpRequestFilter doFilter() method. Exception Message: ").append(e.getMessage()).append(". Some Details follow: ").append("[request=").append(request).append(", ipAddress=").append(ipAddress).append(", userId=").append(userId).append(", systemTimestamp=").append(systemTimestamp).append(", start=").append(start).append(", urlString=").append(urlString).append("]");
            logger.error(sb.toString(), e);
            throw e;
        }finally{
            // stop the stop watch
            long milliseconds = new java.util.Date().getTime() - start.getTime();
            // Show that the request has completed processing
            StringBuilder logBuffer = new StringBuilder("Processing complete for HTTP request, ").append(systemTimestamp).append(", [").append(milliseconds).append(" ms], [").append(urlString).append("], [IP Address: ").append(ipAddress).append("], [UserId: ").append(userId).append("], [User-Agent: ").append(userAgent).append("]");
            logger.info(logBuffer.toString());
            logger.debug("Exiting doFilter()");
        }// end try...catch
    }// end method

    /**
     * Method that retrieves the user-agent string returned from the {@code HttpServletRequest}.
     * 
     * @param request
     *        the HttpServletRequest
     * @return String value of user-agent from request header
     */
    private String getUserAgentString(HttpServletRequest request) {
        logger.debug("Entering getUserAgentString()");
        String userAgent = "";
        if(request.getHeader("user-agent") != null){
            // retrieve the user-agent value from header
            userAgent = request.getHeader("user-agent");
            return userAgent;
        }// end if
        logger.debug("Exiting getUserAgentString()");
        return userAgent;
    }// end getUserAgentString

    /**
     * Used to extract the Method name from HttpRequest calls. This is used for Applications Utilizing DispatchAction.
     *
     * @param request
     *        the HttpServletRequest
     * @return String value of the method name called in this Request.
     */
    private String getHttpRequestMethodParameter(HttpServletRequest request) {
        logger.debug("Entering getHttpRequestMethodParameter()");
        StringBuilder result = new StringBuilder();
        if(request.getParameterNames() != null){
            for(Enumeration<String> parameters = request.getParameterNames();parameters.hasMoreElements();){
                String name = (String) parameters.nextElement();
                // Check if the parameter"name" == "method"
                if("method".equals(name)){
                    result.append(" ").append(name).append(": ");
                    result.append(collapseArray(request.getParameterValues(name)));
                } // end if
            } // end for
        }// end if
        logger.debug("Exiting getHttpRequestMethodParameter()");
        return result.toString();
    } // end method

    /**
     * Called by the web container to indicate to a filter that it is being taken out of service.
     * <p>
     * This method is only called once all threads within the filter's doFilter method have exited or after a timeout period has passed. After the web container calls this method, it will not call the doFilter method again on this instance of the filter.
     * </p>
     * <p>
     * This method gives the filter an opportunity to clean up any resources that are being held (for example, memory, file handles, threads) and make sure that any persistent state is synchronized with the filter's current state in memory.
     * </p>
     */
    public void destroy() {
        this.filterConfig = null;
    }// end method

    /**
     * <p>
     * Converts an array of Strings to a comma separated line
     *
     * @param obj
     *        The Object[] to convert
     * @return String object
     */
    private String collapseArray(Object[] obj) {
        String retStr = "";
        if(obj != null && obj.length != 0){
            StringBuilder sb = new StringBuilder();
            int lastCommaIndex = obj.length - 1;
            for(int i = 0;i < obj.length;i++){
                sb.append(obj[i]);
                if(i < lastCommaIndex){
                    sb.append(',');
                }// end if
            }// end for
            retStr = sb.toString();
        }// end if/else
        return retStr;
    }// end collapseArray
}// end class
