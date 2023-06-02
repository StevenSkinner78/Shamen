/**
 * @(#)ShamenAllInterceptFilter.java 1.0.0 Copyright (c) 2018 Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit All Rights Reserved. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: -Redistribution of source code must retain the above copyright notice, this list of conditions and the following disclaimer. -Redistribution in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. Neither the name of Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit or the names of contributors may be used to endorse or promote products derived from this software without specific prior written permission. This software is provided "AS IS," without a warranty of any kind. ALL EXPRESS OR IMPLIED
 *                                   CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *                                   You acknowledge that this software is not designed, licensed or intended for use in the design, construction, operation or maintenance of any nuclear facility.
 */
package gov.doc.isu.shamen.filters;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.doc.isu.shamen.core.ShamenApplicationStatus;
import gov.doc.isu.shamen.core.ShamenConstants;

/**
 * Class: ShamenAllInterceptFilter.java
 * <p>
 * Filter to check accessability of application through Shamen. This filter handles the suspension of the app.
 * </p>
 * 
 * @author <strong>Shane Duncan</strong> JCCC, Mar 9, 2016
 */
public class ShamenAllInterceptFilter extends ShamenBaseFilter {
    private static final String MY_CLASS_NAME = "gov.doc.isu.shamen.filters.ShamenAllInterceptFilter";
    private static Log logger = LogFactory.getLog(MY_CLASS_NAME);

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

        ShamenApplicationStatus status = ShamenApplicationStatus.getInstance();
        // Write the details of the request to the output log
        try{
            if(status.isSuspended()){
                logger.info("Application has been suspended from users accessing it.  Going to display styled page to users.");
                showSuspended(resp, request);
            }else{
                // Allow the request to be processed by the web application
                chain.doFilter(req, resp);
            }// end if-else
        }catch(ServletException e){
            StringBuffer sb = new StringBuffer("A ServletException was caught in the ShamenAllInterceptFilter doFilter() method. Exception Message: ").append(e.getMessage());
            logger.error(sb.toString(), e);
            throw e;
        }catch(IOException e){
            StringBuffer sb = new StringBuffer("An IOException was caught in the ShamenAllInterceptFilter doFilter() method. Exception Message: ").append(e.getMessage());
            logger.error(sb.toString(), e);
            throw e;
        }finally{
            // Show that the request has completed processing
            logger.debug("Processing complete for HTTP request");
        }// end try...catch
        logger.debug("Exiting doFilter()");
    }// end method

    /**
     * Create a suspended page and put it into the response.
     * 
     * @param response
     *        ServletResponse
     * @param request
     *        HttpServletRequest
     * @throws IOException
     *         exeption that can be thrown by this method
     * @author <strong>Shane Duncan</strong> JCCC, Mar 29, 2016
     */
    private void showSuspended(ServletResponse response, HttpServletRequest request) throws IOException {
        logger.debug("Entering showSuspended()");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        StringBuffer suspendedPage = new StringBuffer();
        suspendedPage.append("<!DOCTYPE HTML>\n");
        suspendedPage.append("<html>\n");
        suspendedPage.append(getHeader());
        suspendedPage.append("<body onload=\"adjustHeader()\" onresize=\"adjustHeader()\"> ");
        suspendedPage.append("<div>");
        suspendedPage.append("<img>");
        suspendedPage.append(getSVG(ShamenConstants.BACKGROUND_IMAGE));
        suspendedPage.append("</img>");
        suspendedPage.append("</div>\n");
        suspendedPage.append("<br><br>\n");
        suspendedPage.append("<div>");
        suspendedPage.append("<h1>");
        suspendedPage.append(ShamenConstants.CLIENT_SELECTOR_VALUE);
        suspendedPage.append(" has been suspended by ITSD</h1>");
        suspendedPage.append("</div>\n");
        suspendedPage.append("<br><br>\n");
        suspendedPage.append("<div class=\"info\">");
        suspendedPage.append("<center><p>Sorry, this application has been suspended for the following reason:</p></center>");
        suspendedPage.append("</div>\n");
        suspendedPage.append("<div class=\"reason\"><center><p>\n");
        suspendedPage.append(ShamenApplicationStatus.getInstance().getStatusReason());
        suspendedPage.append("\n</p></center></div>\n");

        suspendedPage.append("</body>\n</html>");
        out.println(suspendedPage);
        logger.debug("Exiting showSuspended()");
    }// end showSuspended

}// end class
