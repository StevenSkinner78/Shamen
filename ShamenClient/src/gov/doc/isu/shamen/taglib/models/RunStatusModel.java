/**
 * @(#)RunStatusModel.java 1.0.0 Copyright (c) 2018 Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit All Rights Reserved. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: -Redistribution of source code must retain the above copyright notice, this list of conditions and the following disclaimer. -Redistribution in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. Neither the name of Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit or the names of contributors may be used to endorse or promote products derived from this software without specific prior written permission. This software is provided "AS IS," without a warranty of any kind. ALL EXPRESS OR IMPLIED
 *                         CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. You
 *                         acknowledge that this software is not designed, licensed or intended for use in the design, construction, operation or maintenance of any nuclear facility.
 */
package gov.doc.isu.shamen.taglib.models;

import gov.doc.isu.shamen.jms.models.JmsBatchApp;
import gov.doc.isu.shamen.jms.models.JmsRunStatus;

/**
 * Object to model the Run Status record for a Batch Application
 * 
 * @author Steven Skinner, JCCC
 */
public class RunStatusModel extends TableRow {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private JmsRunStatus jmsRunStatus;
    private JmsBatchApp jmsBatchApp;

    /**
     * Default Constructor
     */
    public RunStatusModel() {}

    /**
     * @param jmsRunStatus
     */
    public RunStatusModel(JmsRunStatus jmsRunStatus) {
        this.jmsRunStatus = jmsRunStatus;
    }

    /**
     * @return the jmsRunStatus
     */
    public JmsRunStatus getJmsRunStatus() {
        return jmsRunStatus;
    }

    /**
     * @param jmsRunStatus
     *        the jmsRunStatus to set
     */
    public void setJmsRunStatus(JmsRunStatus jmsRunStatus) {
        this.jmsRunStatus = jmsRunStatus;
    }

    /**
     * @return the jmsBatchApp
     */
    public JmsBatchApp getJmsBatchApp() {
        return jmsBatchApp;
    }

    /**
     * @param jmsBatchApp
     *        the jmsBatchApp to set
     */
    public void setJmsBatchApp(JmsBatchApp jmsBatchApp) {
        this.jmsBatchApp = jmsBatchApp;
    }

}
