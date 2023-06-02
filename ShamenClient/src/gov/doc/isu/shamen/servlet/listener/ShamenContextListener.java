/**
 * @(#)ShamenContextListener.java 1.0.0 Copyright (c) 2018 Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit All Rights Reserved. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: -Redistribution of source code must retain the above copyright notice, this list of conditions and the following disclaimer. -Redistribution in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. Neither the name of Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit or the names of contributors may be used to endorse or promote products derived from this software without specific prior written permission. This software is provided "AS IS," without a warranty of any kind. ALL EXPRESS OR IMPLIED
 *                                CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. You
 *                                acknowledge that this software is not designed, licensed or intended for use in the design, construction, operation or maintenance of any nuclear facility.
 */
package gov.doc.isu.shamen.servlet.listener;

import java.util.ResourceBundle;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import gov.doc.isu.shamen.core.ShamenAppSubscriptionSpawner;

/**
 * This ShamenContextListener class is for receiving notification events about ServletContext lifecycle changes.
 * <p>
 * This class handles 2 lifecycle changes. They are <b>context destroyed</b> which would occur on shutdown of the web application and <b>context initialized</b> which would occur on start up of the web application. This <code>ShamenContextListener</code> allows the application to release any system resources on shutdown or to obtain any resources needed on startup. In particular, any lingering JMS components.
 * </p>
 *
 * @author <strong>Shane Duncan</strong> JCCC July 13, 2016
 */
public class ShamenContextListener implements ServletContextListener {

    /**
     * Receives notification that the ServletContext is about to be shut down.
     * <p>
     * All servlets and filters will have been destroyed before any ServletContextListeners are notified of context destruction.
     * </p>
     * <p>
     * This method is utilized for releasing any system resources that may be holding locks on any JMS resources.
     * </p>
     * 
     * @param sce
     *        the ServletContextEvent containing the ServletContext that is being destroyed
     */
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Entering contextDestroyed");
        try{
            ResourceBundle rb = ResourceBundle.getBundle("ShamenApplicationResources");
            if(rb != null && Boolean.valueOf(rb.getString("useShamen"))){
                System.out.println("Attempting to destroy all JMS resources");
                ShamenAppSubscriptionSpawner.getInstance().killYourself();
            }else{
                System.out.println("JMS resources were successfully shutdown.");
            }// end if/else
        }catch(Exception e){
            System.err.println("Exception has occurred while trying to destroy JMS resources. Error message is: " + e.getMessage());
        }// end try...catch
        System.out.println("Exiting contextDestroyed");
    }// end method

    /**
     * Receives notification that the web application initialization process is starting.
     * <p>
     * All ServletContextListeners are notified of context initialization before any filters or servlets in the web application are initialized.
     * </p>
     * 
     * @param sce
     *        the ServletContextEvent containing the ServletContext that is being initialized
     */
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Entering contextInitialized");
        // nothing to initialize at this time.
        System.out.println("Exiting contextInitialized");
    }// end method

}// end class
