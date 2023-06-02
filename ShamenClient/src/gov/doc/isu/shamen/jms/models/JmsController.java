/**
 * @(#)JmsController.java 1.0.0 Copyright (c) 2018 Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit All Rights Reserved. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: -Redistribution of source code must retain the above copyright notice, this list of conditions and the following disclaimer. -Redistribution in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. Neither the name of Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit or the names of contributors may be used to endorse or promote products derived from this software without specific prior written permission. This software is provided "AS IS," without a warranty of any kind. ALL EXPRESS OR IMPLIED CONDITIONS,
 *                        REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. You acknowledge that this
 *                        software is not designed, licensed or intended for use in the design, construction, operation or maintenance of any nuclear facility.
 */
package gov.doc.isu.shamen.jms.models;

import java.util.List;

/**
 * Model object to hold the controller record from the DB
 * 
 * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
 */
public class JmsController extends JmsBaseObject {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Long controllerRefId;
    private Long queueRefId;
    private String controllerAddress;
    private String controllerName;
    private String status;

    private List<JmsBatchApp> jmsBatchApps;
    private List<JmsBatchAppCollection> jmsBatchAppCollections = null;

    /**
     * Default Constructor
     */
    public JmsController() {
        super();
    }

    /**
     * Constructor using fields
     * 
     * @param controllerRefId
     *        the ref id of the controller
     */
    public JmsController(Long controllerRefId) {
        super();
        this.controllerRefId = controllerRefId;
    }

    /**
     * Constructor using fields
     * 
     * @param controllerRefId
     *        the ref id of the controller
     * @param status
     *        the status of the controller
     */
    public JmsController(Long controllerRefId, String status) {
        super();
        this.controllerRefId = controllerRefId;
        this.status = status;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
     * @return the controllerRefId
     */
    public Long getControllerRefId() {
        return controllerRefId;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
     * @param controllerRefId
     *        the controllerRefId to set
     */
    public void setControllerRefId(Long controllerRefId) {
        this.controllerRefId = controllerRefId;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
     * @return the queueRefId
     */
    public Long getQueueRefId() {
        return queueRefId;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
     * @param queueRefId
     *        the queueRefId to set
     */
    public void setQueueRefId(Long queueRefId) {
        this.queueRefId = queueRefId;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
     * @return the controllerAddress
     */
    public String getControllerAddress() {
        return controllerAddress;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
     * @param controllerAddress
     *        the controllerAddress to set
     */
    public void setControllerAddress(String controllerAddress) {
        this.controllerAddress = controllerAddress;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
     * @return the controllerName
     */
    public String getControllerName() {
        return controllerName;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
     * @param controllerName
     *        the controllerName to set
     */
    public void setControllerName(String controllerName) {
        this.controllerName = controllerName;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status
     *        the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("controllerRefId = ").append(controllerRefId).append(DEFAULT_NEW_LINE);
        sb.append("queueRefId = ").append(queueRefId).append(DEFAULT_NEW_LINE);
        sb.append("controllerAddress = ").append(controllerAddress).append(DEFAULT_NEW_LINE);
        sb.append("controllerName = ").append(controllerName).append(DEFAULT_NEW_LINE);
        sb.append("]").append(DEFAULT_NEW_LINE);
        sb.append(super.toString());
        return sb.toString();
    }// end toString

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Oct 2, 2015
     * @return the jmsBatchApps
     */
    public List<JmsBatchApp> getBatchApps() {
        return jmsBatchApps;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Oct 2, 2015
     * @param jmsBatchApps
     *        the jmsBatchApps to set
     */
    public void setBatchApps(List<JmsBatchApp> jmsBatchApps) {
        this.jmsBatchApps = jmsBatchApps;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Jul 20, 2016
     * @return the jmsBatchAppCollections
     */
    public List<JmsBatchAppCollection> getJmsBatchAppCollections() {
        return jmsBatchAppCollections;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Jul 20, 2016
     * @param jmsBatchAppCollections
     *        the jmsBatchAppCollections to set
     */
    public void setJmsBatchAppCollections(List<JmsBatchAppCollection> jmsBatchAppCollections) {
        this.jmsBatchAppCollections = jmsBatchAppCollections;
    }
}
