/**
 * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
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
