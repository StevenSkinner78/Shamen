/**
 * @(#)BatchAppHandlerTagFilter.java 1.0.0 Copyright (c) 2018 Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit All Rights Reserved. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: -Redistribution of source code must retain the above copyright notice, this list of conditions and the following disclaimer. -Redistribution in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. Neither the name of Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit or the names of contributors may be used to endorse or promote products derived from this software without specific prior written permission. This software is provided "AS IS," without a warranty of any kind. ALL EXPRESS OR IMPLIED
 *                                   CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *                                   You acknowledge that this software is not designed, licensed or intended for use in the design, construction, operation or maintenance of any nuclear facility.
 */
package gov.doc.isu.shamen.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.doc.isu.shamen.core.ShamenConstants;

/**
 * Servlet Filter implementation class BatchAppHandlerTagFilter
 * 
 * @author <strong>Shane Duncan</strong> JCCC, 08/08/2017
 * @author <strong>Steven Skinner</strong> JCCC, 10/01/2019
 */
public class BatchAppHandlerTagFilter implements Filter {
    private static final String MY_CLASS_NAME = "gov.doc.isu.shamen.filters.BatchAppHandlerTagFilter";
    private static Log logger = LogFactory.getLog(MY_CLASS_NAME);
    private FilterConfig filterConfig;
    private static final String EQUAL = "=";

    /**
     * Default constructor.
     */
    public BatchAppHandlerTagFilter() {}

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
    }// end method

    /**
     * @see Filter#destroy()
     */
    public void destroy() {}

    /**
     * Performs return functionality for display of batch app information detail.
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
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        logger.debug("Entering doFilter()");
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        // Get the original url and all parameters.
        StringBuffer url = new StringBuffer(request.getParameter(ShamenConstants.TAG_PARENT_URL));
        String batchAppRefId = request.getParameter(ShamenConstants.TAG_BATCH_APP_REF_ID);
        if(batchAppRefId != null){
            url.append("&");
            url.append(ShamenConstants.TAG_BATCH_APP_REF_ID);
            url.append(EQUAL);
            url.append(batchAppRefId);
        }

        // pass the request back to the page containing the tag
        response.sendRedirect(url.toString());
    }// end doFilter

}
