/**
 * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
 */
package gov.doc.isu.shamen.jms.models;

import gov.doc.isu.shamen.interfaces.Scheduleable;

import java.util.List;

/**
 * Model object to hold the Batch Application Record
 * 
 * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
 */
public class JmsBatchApp extends Scheduleable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Long batchAppRefId;
    private Long controllerRefId;
    private String fileLocation;
    private String fileNm;
    private String type;
    private Long runSequenceNbr;
    private Boolean fromCollection;
    private Long executionCount;
    private List<JmsRunStatus> runStatusList;
    // Valid Batch App Types.
    public static final String TYPE_DUMB = "DUM";
    public static final String TYPE_GEORGE = "GE1";
    public static final String TYPE_SMART_GEORGE = "GE2";
    public static final String TYPE_SMART_ANT = "SMA";

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("batchAppRefId = ").append(batchAppRefId).append(DEFAULT_NEW_LINE);
        sb.append("controllerRefId = ").append(controllerRefId).append(DEFAULT_NEW_LINE);
        sb.append("fileLocation = ").append(fileLocation).append(DEFAULT_NEW_LINE);
        sb.append("fileNm = ").append(fileNm).append(DEFAULT_NEW_LINE);
        sb.append("type = ").append(type).append(DEFAULT_NEW_LINE);
        sb.append("runSequenceNbr = ").append(runSequenceNbr).append(DEFAULT_NEW_LINE);
        sb.append("fromCollection = ").append(fromCollection).append(DEFAULT_NEW_LINE);
        sb.append("executionCount = ").append(executionCount).append(DEFAULT_NEW_LINE);
        sb.append(super.toString());
        return sb.toString();
    }// end toString

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
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @return the controllerRefId
     */
    public Long getControllerRefId() {
        return controllerRefId;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param controllerRefId
     *        the controllerRefId to set
     */
    public void setControllerRefId(Long controllerRefId) {
        this.controllerRefId = controllerRefId;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @return the fileLocation
     */
    public String getFileLocation() {
        return fileLocation;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param fileLocation
     *        the fileLocation to set
     */
    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 11, 2015
     * @param type
     *        the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 17, 2015
     * @return the fileNm
     */
    public String getFileNm() {
        return fileNm;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 17, 2015
     * @param fileNm
     *        the fileNm to set
     */
    public void setFileNm(String fileNm) {
        this.fileNm = fileNm;
    }

    /**
     * @return the runSequenceNbr
     */
    public Long getRunSequenceNbr() {
        return runSequenceNbr;
    }

    /**
     * @param runSequenceNbr
     *        the runSequenceNbr to set
     */
    public void setRunSequenceNbr(Long runSequenceNbr) {
        this.runSequenceNbr = runSequenceNbr;
    }

    /**
     * @return the fromCollection
     */
    public Boolean getFromCollection() {
        return fromCollection;
    }

    /**
     * @param fromCollection
     *        the fromCollection to set
     */
    public void setFromCollection(Boolean fromCollection) {
        this.fromCollection = fromCollection;
    }

    /**
     * @return the executionCount
     */
    public Long getExecutionCount() {
        return executionCount;
    }

    /**
     * @param executionCount
     *        the executionCount to set
     */
    public void setExecutionCount(Long executionCount) {
        this.executionCount = executionCount;
    }

    /**
     * @return the runStatusList
     */
    public List<JmsRunStatus> getRunStatusList() {
        return runStatusList;
    }

    /**
     * @param runStatusList
     *        the runStatusList to set
     */
    public void setRunStatusList(List<JmsRunStatus> runStatusList) {
        this.runStatusList = runStatusList;
    }

}
