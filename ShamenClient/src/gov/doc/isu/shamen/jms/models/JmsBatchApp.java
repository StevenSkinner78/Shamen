/**
 * @(#)JmsBatchApp.java 1.0.0 Copyright (c) 2018 Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit All Rights Reserved. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: -Redistribution of source code must retain the above copyright notice, this list of conditions and the following disclaimer. -Redistribution in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. Neither the name of Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit or the names of contributors may be used to endorse or promote products derived from this software without specific prior written permission. This software is provided "AS IS," without a warranty of any kind. ALL EXPRESS OR IMPLIED CONDITIONS,
 *                      REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. You acknowledge that this
 *                      software is not designed, licensed or intended for use in the design, construction, operation or maintenance of any nuclear facility.
 */
package gov.doc.isu.shamen.jms.models;

import java.util.List;

import gov.doc.isu.shamen.interfaces.Scheduleable;

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
