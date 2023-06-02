/**
 * @(#)ShamenBaseFilter.java 1.0.0 Copyright (c) 2018 Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit All Rights Reserved. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: -Redistribution of source code must retain the above copyright notice, this list of conditions and the following disclaimer. -Redistribution in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. Neither the name of Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit or the names of contributors may be used to endorse or promote products derived from this software without specific prior written permission. This software is provided "AS IS," without a warranty of any kind. ALL EXPRESS OR IMPLIED
 *                           CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. You
 *                           acknowledge that this software is not designed, licensed or intended for use in the design, construction, operation or maintenance of any nuclear facility.
 */
package gov.doc.isu.shamen.filters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.doc.isu.shamen.core.ShamenConstants;

/**
 * Class: ShamenBaseFilter.java
 * <p>
 * Filter class to server as the super class for filters in Shamen Client
 * </p>
 * 
 * @author <strong>Shane Duncan</strong> JCCC, Mar 9, 2016
 */
public abstract class ShamenBaseFilter implements Filter {
    private static final String MY_CLASS_NAME = "gov.doc.isu.shamen.filters.ShamenBaseFilter";
    private static Log logger = LogFactory.getLog(MY_CLASS_NAME);
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
    }// end method

    /**
     * This method builds the key for this application to be used in checking values from the distributed map.
     * 
     * @return
     */
    private String getKey() {
        return ShamenConstants.CLIENT_SELECTOR_VALUE + ShamenConstants.CLIENT_SELECTOR_VALUE_2;
    }

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
     * This method gets an SVG image from a file and returns it as a String. It is intended for one image per file.
     * 
     * @param icon
     *        Path and name. If the icon is in the same package, just the name.
     * @return String SVG representation of the image.
     */
    public String getSVG(String icon) {
        logger.debug("Entering getSVG() for icon: " + String.valueOf(icon));
        logger.trace("This method gets an SVG image from a file and returns it as a String.  It is intended for one image per file.  It's a way to be able to keep the icons within the jar and also allow for any screen size.");
        InputStream stream = ShamenLoginInterceptFilter.class.getResourceAsStream(icon);
        StringBuffer iconGuts = new StringBuffer();
        if(stream != null){
            BufferedReader in = null;
            try{
                in = new BufferedReader(new InputStreamReader(stream));
                String line = null;
                while((line = in.readLine()) != null){
                    iconGuts.append(line);
                }// end while
            }catch(IOException e){
                logger.error("getSVG - Error while retrieving the svg: " + String.valueOf(icon), e);
            }finally{
                if(in != null){
                    try{
                        in.close();
                    }catch(IOException e){
                        logger.warn("Unable to close BufferedReader, may cause resource leak.  Exception is: " + e);
                    }// end try-catch
                }// end if
            } // end try-catch
        }
        logger.debug("Exiting getSVG()");
        return iconGuts.toString();
    }// end getSVG

    /**
     * This method builds the header with in-line style and icon adjustment JS so that the SVG header will resize to all screens and still look fantastic in all browsers.
     * 
     * @param icon
     *        Path and name. If the icon is in the same package, just the name.
     * @return String JS
     */
    public String getHeader() {
        logger.debug("Entering getHeader() ");
        logger.trace("This method builds the header with in-line style and icon adjustment JS so that the SVG header will resize to all screens and still look fantastic in all browsers.");
        InputStream stream = ShamenLoginInterceptFilter.class.getResourceAsStream(ShamenConstants.HEADER_DATA);
        StringBuffer htmlGuts = new StringBuffer();
        if(stream != null){
            BufferedReader in = null;
            try{
                in = new BufferedReader(new InputStreamReader(stream));
                String line = null;
                while((line = in.readLine()) != null){
                    htmlGuts.append(line);
                }// end while
            }catch(IOException e){
                logger.error("getSVG - Error while retrieving the svg: " + String.valueOf(ShamenConstants.HEADER_DATA), e);
            }finally{
                if(in != null){
                    try{
                        in.close();
                    }catch(IOException e){
                        logger.warn("Unable to close BufferedReader, may cause resource leak.  Exception is: " + e);
                    }// end try-catch
                }// end if
            } // end try-catch
        }// end if
        logger.debug("Exiting getHeader()");
        return htmlGuts.toString();
    }// end getHeader
}// end class
