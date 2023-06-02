/**
 * @(#)JmsSetUp.java 1.0.0 Copyright (c) 2018 Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit All Rights Reserved. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: -Redistribution of source code must retain the above copyright notice, this list of conditions and the following disclaimer. -Redistribution in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. Neither the name of Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit or the names of contributors may be used to endorse or promote products derived from this software without specific prior written permission. This software is provided "AS IS," without a warranty of any kind. ALL EXPRESS OR IMPLIED CONDITIONS,
 *                   REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. You acknowledge that this
 *                   software is not designed, licensed or intended for use in the design, construction, operation or maintenance of any nuclear facility.
 */
package gov.doc.isu.shamen.servlet;

import java.util.ResourceBundle;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.doc.isu.shamen.core.ShamenAppSubscriptionSpawner;
import gov.doc.isu.shamen.core.ShamenConstants;

/**
 * Servlet implementation class JmsSetUp. This class checks the ShamenApplicaationResources properties file to see if useShamen property is set to true.
 * 
 * @author <strong>Shane Duncan</strong> JCCC, Aug 19, 2015
 * @author <strong>Steve Skinner</strong> JCCC, June 29, 2016
 */
public class JmsSetUp extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final String MY_CLASS_NAME = "gov.doc.isu.shamen.servlet.JmsSetUp";
    private static Log log = LogFactory.getLog(MY_CLASS_NAME);

    /**
     * @see HttpServlet#HttpServlet()
     */
    public JmsSetUp() {
        super();
        log.debug("Entering JmsSetUp constructor.");
        log.debug("Loading Shamen ApplicationResources file.");
        ResourceBundle rb = ResourceBundle.getBundle("ShamenApplicationResources");
        if(rb != null){
            if(Boolean.valueOf(rb.getString("useShamen"))){
                log.debug("useShamen property set to true. Initializing ShamenAppSubscriptionSpawner thread.");
                // Set the constants for this app based on the ShamenApplicationResources
                ShamenConstants.PROVIDER_URL = rb.getString(ShamenConstants.CLIENT_PROPERTY_PROVIDER_URL);
                ShamenConstants.CLIENT_SELECTOR_VALUE = rb.getString(ShamenConstants.CLIENT_PROPERTY_NAME);
                ShamenConstants.CLIENT_SELECTOR_VALUE_2 = rb.getString(ShamenConstants.CLIENT_PROPERTY_ENV);
                ShamenConstants.USE_SHAMEN = true;
                InitialContext ic;
                String nodeName = "unable to determine";
                try{
                    ic = new javax.naming.InitialContext();

                    ShamenConstants.CLIENT_ADDNTL_INFO = rb.getString(ShamenConstants.CLIENT_PROPERTY_ADDNTL_INFO);
                    ShamenConstants.CLIENT_EAR = rb.getString(ShamenConstants.CLIENT_PROPERTY_EAR);
                    ShamenConstants.CLIENT_BRANCH = rb.getString(ShamenConstants.CLIENT_PROPERTY_BRANCH);
                    ShamenConstants.CLIENT_VERSION = rb.getString(ShamenConstants.CLIENT_PROPERTY_VERSION);
                    ShamenConstants.MQ_USER_VALUE = rb.getString(ShamenConstants.CLIENT_PROPERTY_MQ_USER);
                    ShamenConstants.MQ_PASSWORD_VALUE = rb.getString(ShamenConstants.CLIENT_PROPERTY_MQ_PASSWORD);
                   
                    String serverName = ic.lookup("servername").toString();
                    String node = ic.lookup("thisNode/nodename").toString();
                    nodeName = serverName + "_" + node;
                    nodeName += "_" + ShamenConstants.CLIENT_EAR.split("_")[0]; 
                    ShamenConstants.CLIENT_SELECTOR_VALUE_3 = nodeName;
                    
                    // Set up the jms thread so that it is always running.
                    Thread jms = new Thread(ShamenAppSubscriptionSpawner.getInstance());
                    log.info("Starting ShamenAppSubscriptionSpawner thread.");
                    jms.start();
                }catch(NamingException e){
                    log.error("Unable to get instance identifier in the JNDI lookup. This instance will run as a standalone webApp.  Error is: " + e);
                    ShamenConstants.USE_SHAMEN = false;
                }catch(Exception e){
                    log.error("Unable to get instance identifier in the JNDI lookup. This instance will run as a standalone webApp.  Error is: " + e);
                    ShamenConstants.USE_SHAMEN = false;
                }

            }else{
                log.info("useShamen property set to false. Will not initialize ShamenAppSubscriptionSpawner thread.");
            }// end if-else
        }else{
            log.error("No ShamenApplicationResources found in the src folder.");
        }// end if-else
        log.debug("Exiting JmsSetUp constructor.");
    }// end constructor

    /**
     * This method builds the key for this application to be used in checking values from the distributed map.
     * 
     * @return
     */
    private String getKey() {
        return ShamenConstants.CLIENT_SELECTOR_VALUE + ShamenConstants.CLIENT_SELECTOR_VALUE_2;
    }
}// end JmsSetUp
