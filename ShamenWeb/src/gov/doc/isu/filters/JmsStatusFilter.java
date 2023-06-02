package gov.doc.isu.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import gov.doc.isu.dwarf.exception.BaseException;
import gov.doc.isu.shamen.business.JmsStatusInfo;

/**
 * Servlet Filter implementation class JmsStatusFilter
 */
public class JmsStatusFilter implements Filter {
    private static Logger logger = Logger.getLogger("gov.doc.isu.filters.JmsStatusFilter");
    private FilterConfig filterConfig;

    /**
     * Default constructor.
     */
    public JmsStatusFilter() {}// end constructor

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
    public void destroy() {}// end destroy

    /**
     * Performs injection of the jmsStatus info.
     *
     * @param req
     *        HTTP Request Object
     * @param response
     *        HTTP Response Object
     * @param chain
     *        filter chain
     * @throws javax.servlet.ServletException
     *         exeption that can be thrown by this method
     * @throws java.io.IOException
     *         exeption that can be thrown by this method
     */
    public void doFilter(ServletRequest req, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        logger.debug("Entering doFilter()");
        HttpServletRequest request = (HttpServletRequest) req;
        JmsStatusInfo jmsInfo = JmsStatusInfo.getInstance();
        try{
            request.getSession().setAttribute("JmsModel", jmsInfo.getStatus());
        }catch(BaseException e){
            logger.error("Exception occurred trying to load the JmsStatus info in the filter.", e);
        }// try-catch
         // pass the request along the filter chain
        chain.doFilter(request, response);
    }// end doFilter

}// end class
