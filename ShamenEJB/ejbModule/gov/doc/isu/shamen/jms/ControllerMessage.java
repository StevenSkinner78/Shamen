/**
 * @author <strong>Shane Duncan</strong> JCCC, Aug 19, 2015
 */
package gov.doc.isu.shamen.jms;

import java.util.ArrayList;

import gov.doc.isu.shamen.jms.models.JmsBaseObject;
import gov.doc.isu.shamen.jms.models.JmsController;
import gov.doc.isu.shamen.jms.models.JmsRunStatus;

/**
 * This class is used as the primary message object for communication with Controllers.
 * 
 * @author <strong>Shane Duncan</strong> JCCC, Aug 19, 2015
 */
public class ControllerMessage extends JmsBaseObject {

    private static final long serialVersionUID = -3646547212690172382L;
    private String text;
    private String correlationID;
    private Long batchAppRefId;
    private Boolean collectionIndicator;
    private String selectorName;
    private String selector;
    private JmsController controller;
    private JmsRunStatus runStatus;
    private ArrayList<String> miscParameters;
    private String jobParameters;
    private Boolean replyTo = false;
    // Message Stuff
    // Property to be used to indicate receipt of message
    public static final String RECEIVED = "RECEIVED";
    // Property to be used to indicate no controller found in database for the address
    public static final String NO_SUCH_RECORD = "NO_SUCH_RECORD";

    // Orders for the JmsController (received by JMS)
    // Property to be used to tell controller to kill itself
    public static final String TERMINATE = "TERMINATE";
    // Property to be used to tell controller to run a batch job.
    public static final String RUN_BAT = "RUN BAT";
    // Property to be used to tell controller to verify it is alive
    public static final String ARE_YOU_ALIVE = "Alive?";
    // Property to be used to tell controller to update it's schedule
    public static final String REFRESH_SCHEDULE = "REFRESH_SCHEDULE";
    // Property to be used to tell Shamen to update a runstatus
    public static final String UPDATE_RUN_STATUS = "UPDATE_RUN_STATUS";
    // Property to be used to tell Shamen to return a the Controller record.
    public static final String GIVE_ME_CONTROLLER = "GIVE_ME_CONTROLLER";
    // Property to be used to tell Shamen to return a the Controller record.
    public static final String RESTART_THREADS = "RESTART_THREADS";

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Aug 19, 2015
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Aug 19, 2015
     * @param text
     *        the text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("Text = ").append(text).append(DEFAULT_NEW_LINE);
        sb.append("correlationID = ").append(correlationID).append(DEFAULT_NEW_LINE);
        sb.append("batchAppRefId = ").append(batchAppRefId).append(DEFAULT_NEW_LINE);
        sb.append("selectorName = ").append(selectorName).append(DEFAULT_NEW_LINE);
        sb.append("selector = ").append(selector).append(DEFAULT_NEW_LINE);
        sb.append("CONTROLLER = ").append(controller).append(DEFAULT_NEW_LINE);
        sb.append("RUNSTATUS = ").append(runStatus).append(DEFAULT_NEW_LINE);
        sb.append("replyTo = ").append(replyTo).append(DEFAULT_NEW_LINE);
        sb.append("collectionIndicator = ").append(collectionIndicator).append(DEFAULT_NEW_LINE);
        sb.append("miscParameters = [").append(DEFAULT_NEW_LINE);
        if(!isEmpty(miscParameters)){
            for(int i = 0, j = miscParameters.size();i < j;i++){
                if(i > 0){
                    sb.append(COMMA).append(DEFAULT_NEW_LINE);
                }// end if
                sb.append(miscParameters.get(i));
            }// end for
        }// end if
        sb.append("]").append(DEFAULT_NEW_LINE);
        return sb.toString();
    }// end toString

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Aug 21, 2015
     * @return the correlationID
     */
    public String getCorrelationID() {
        return correlationID;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Aug 21, 2015
     * @param correlationID
     *        the correlationID to set
     */
    public void setCorrelationID(String correlationID) {
        this.correlationID = correlationID;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 3, 2015
     * @return the miscParameters
     */
    public ArrayList<String> getMiscParameters() {
        return miscParameters;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 3, 2015
     * @param miscParameters
     *        the miscParameters to set
     */
    public void setMiscParameters(ArrayList<String> miscParameters) {
        this.miscParameters = miscParameters;
    }

    /**
     * @author <strong>Steve Skinner</strong> JCCC, Sep 17, 2019
     * @return the jobParameters
     */
    public String getJobParameters() {
        return jobParameters;
    }

    /**
     * @author <strong>Steve Skinner</strong> JCCC, Sep 17, 2019
     * @param jobParameters
     *        the jobParameters to set
     */
    public void setJobParameters(String jobParameters) {
        this.jobParameters = jobParameters;
    }
    
    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 8, 2015
     * @return the selectorName
     */
    public String getSelectorName() {
        return selectorName;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 8, 2015
     * @param selectorName
     *        the selectorName to set
     */
    public void setSelectorName(String selectorName) {
        this.selectorName = selectorName;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 8, 2015
     * @return the selector
     */
    public String getSelector() {
        return selector;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 8, 2015
     * @param selector
     *        the selector to set
     */
    public void setSelector(String selector) {
        this.selector = selector;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @return the batchAppRefId
     */
    public Long getBatchAppRefId() {
        return batchAppRefId;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param batchAppRefId
     *        the batchAppRefId to set
     */
    public void setBatchAppRefId(Long batchAppRefId) {
        this.batchAppRefId = batchAppRefId;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Oct 5, 2015
     * @return the controller
     */
    public JmsController getController() {
        return controller;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Oct 5, 2015
     * @param controller
     *        the controller to set
     */
    public void setController(JmsController controller) {
        this.controller = controller;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Oct 7, 2015
     * @return the runStatus
     */
    public JmsRunStatus getRunStatus() {
        return runStatus;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Oct 7, 2015
     * @param runStatus
     *        the runStatus to set
     */
    public void setRunStatus(JmsRunStatus runStatus) {
        this.runStatus = runStatus;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Oct 21, 2015
     * @return the replyTo
     */
    public Boolean getReplyTo() {
        return replyTo;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Oct 21, 2015
     * @param replyTo
     *        the replyTo to set
     */
    public void setReplyTo(Boolean replyTo) {
        this.replyTo = replyTo;
    }

    /**
     * @return the collectionIndicator
     */
    public Boolean getCollectionIndicator() {
        return collectionIndicator;
    }

    /**
     * @param collectionIndicator
     *        the collectionIndicator to set
     */
    public void setCollectionIndicator(Boolean collectionIndicator) {
        this.collectionIndicator = collectionIndicator;
    }

}
