/**
 * @(#)JmsRunStatusSummary.java 1.0.0 Copyright (c) 2018 Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit All Rights Reserved. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: -Redistribution of source code must retain the above copyright notice, this list of conditions and the following disclaimer. -Redistribution in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. Neither the name of Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit or the names of contributors may be used to endorse or promote products derived from this software without specific prior written permission. This software is provided "AS IS," without a warranty of any kind. ALL EXPRESS OR IMPLIED
 *                              CONDITIONS,
 */
package gov.doc.isu.shamen.jms.models;

/**
 * Model object to hold the run status summary records.
 * 
 * @author <strong>Zachary Lisle</strong> ITSD JCCC, Aug 27, 2021
 */
/**
 * @author Zachary Lisle, ITSD JCCC, Sep 1, 2021
 */
/**
 * @author Zachary Lisle, ITSD JCCC, Sep 1, 2021
 */
public class JmsRunStatusSummary extends JmsBaseObject {
    /**
     * 
     */
    private static final long serialVersionUID = 327055466881873804L;
    private Long runStatusRefId;
    /**
     * Unsure the need for either of these fields, providing for added flexibility for now.
     */
    private Long batchAppRefId; // batch app refId
    private String batchAppName; // Name of the batch app

    private String startTs; // the date and time the batch application run started.
    private String stopTs;// the date and time the batch application run stopped if available.
    /**
     * ShamenUtil has methods getTimeDifference and getTimeDifferenceInMinutes. Utilize them
     */
    private String duration; // how long the batch run took.
    private String durationInMinutes; //
    private String status;// the status of the batch run.
    private String details; // the details will be whether or not the job completed successful, is still running, or completed but was unsuccessful.
    private Long runNumber; // the run number for the particular run of the job.
    /**
     * This is retrieved based on the Create_User_Ref_Id column <br>
     * 1. If Ref Id > 0 and not equal to 99 then the value needs to be retrieved from the Auth User table <br>
     * 2. If the ref id == 99 then it was ran from the shamen client, need to retrieve from the Result_Detail column <br>
     * 3. If the refId == 0 then the value should be set to Schedule.. as it was ran from the schedule
     */
    private String from; // which will display who or what executed that run.

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("JmsRunStatusSummary [runStatusRefId=");
        buffer.append(runStatusRefId);
        buffer.append(", batchAppRefId=");
        buffer.append(batchAppRefId);
        buffer.append(", batchAppName=");
        buffer.append(batchAppName);
        buffer.append(", startTs=");
        buffer.append(startTs);
        buffer.append(", stopTs=");
        buffer.append(stopTs);
        buffer.append(", duration=");
        buffer.append(duration);
        buffer.append(", durationInMinutes=");
        buffer.append(durationInMinutes);
        buffer.append(", status=");
        buffer.append(status);
        buffer.append(", details=");
        buffer.append(details);
        buffer.append(", runNumber=");
        buffer.append(runNumber);
        buffer.append(", from=");
        buffer.append(from);
        buffer.append("]");
        return buffer.toString();
    }// end toString

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((batchAppName == null) ? 0 : batchAppName.hashCode());
        result = prime * result + ((batchAppRefId == null) ? 0 : batchAppRefId.hashCode());
        result = prime * result + ((details == null) ? 0 : details.hashCode());
        result = prime * result + ((duration == null) ? 0 : duration.hashCode());
        result = prime * result + ((durationInMinutes == null) ? 0 : durationInMinutes.hashCode());
        result = prime * result + ((from == null) ? 0 : from.hashCode());
        result = prime * result + ((runNumber == null) ? 0 : runNumber.hashCode());
        result = prime * result + ((runStatusRefId == null) ? 0 : runStatusRefId.hashCode());
        result = prime * result + ((startTs == null) ? 0 : startTs.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((stopTs == null) ? 0 : stopTs.hashCode());
        return result;
    }// end hashCode

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null) return false;
        if(getClass() != obj.getClass()) return false;
        JmsRunStatusSummary other = (JmsRunStatusSummary) obj;
        if(batchAppName == null){
            if(other.batchAppName != null) return false;
        }else if(!batchAppName.equals(other.batchAppName)) return false;// end if/else
        if(batchAppRefId == null){
            if(other.batchAppRefId != null) return false;
        }else if(!batchAppRefId.equals(other.batchAppRefId)) return false;// end if/else
        if(details == null){
            if(other.details != null) return false;
        }else if(!details.equals(other.details)) return false;// end if/else
        if(duration == null){
            if(other.duration != null) return false;
        }else if(!duration.equals(other.duration)) return false;// end if/else
        if(durationInMinutes == null){
            if(other.durationInMinutes != null) return false;
        }else if(!durationInMinutes.equals(other.durationInMinutes)) return false;// end if/else
        if(from == null){
            if(other.from != null) return false;
        }else if(!from.equals(other.from)) return false;// end if/else
        if(runNumber == null){
            if(other.runNumber != null) return false;
        }else if(!runNumber.equals(other.runNumber)) return false;// end if/else
        if(runStatusRefId == null){
            if(other.runStatusRefId != null) return false;
        }else if(!runStatusRefId.equals(other.runStatusRefId)) return false;// end if/else
        if(startTs == null){
            if(other.startTs != null) return false;
        }else if(!startTs.equals(other.startTs)) return false;// end if/else
        if(status == null){
            if(other.status != null) return false;
        }else if(!status.equals(other.status)) return false;// end if/else
        if(stopTs == null){
            if(other.stopTs != null) return false;
        }else if(!stopTs.equals(other.stopTs)) return false;// end if/else
        return true;
    }// end equals

    /**
     * @return runStatusRefId
     */
    public Long getRunStatusRefId() {
        return null != runStatusRefId ? runStatusRefId : 0L;
    }// end getRunStatusRefId

    /**
     * @param runStatusRefId
     */
    public void setRunStatusRefId(Long runStatusRefId) {
        this.runStatusRefId = runStatusRefId;
    }// end setRunStatusRefId

    /**
     * @return batchAppRefId
     */
    public Long getBatchAppRefId() {
        return null != batchAppRefId ? batchAppRefId : 0L;
    }// end getBatchAppRefId

    /**
     * @param batchAppRefId
     */
    public void setBatchAppRefId(Long batchAppRefId) {
        this.batchAppRefId = batchAppRefId;
    }// end setBatchAppRefId

    /**
     * @return batchAppName
     */
    public String getBatchAppName() {
        return null != batchAppName ? batchAppName : "";
    }// end getBatchAppName

    /**
     * @param batchAppName
     */
    public void setBatchAppName(String batchAppName) {
        this.batchAppName = batchAppName;
    }// end setBatchAppName

    /**
     * @return startTs
     */
    public String getStartTs() {
        return null != startTs ? startTs : "";
    }// end getStartTs

    /**
     * @param startTs
     */
    public void setStartTs(String startTs) {
        this.startTs = startTs;
    }// end setStartTs

    /**
     * @return stopTs
     */
    public String getStopTs() {
        return null != stopTs ? stopTs : "";
    }// end getStopTs

    /**
     * @param stopTs
     */
    public void setStopTs(String stopTs) {
        this.stopTs = stopTs;
    }// end setStopTs

    /**
     * @return duration
     */
    public String getDuration() {
        return null != duration ? duration : "";
    }// end getDuration

    /**
     * @param duration
     */
    public void setDuration(String duration) {
        this.duration = duration;
    }// end setDuration

    /**
     * @return durationInMinutes
     */
    public String getDurationInMinutes() {
        return null != durationInMinutes ? durationInMinutes : "";
    }// end getDurationInMinutes

    /**
     * @param durationInMinutes
     */
    public void setDurationInMinutes(String durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }// end setDurationInMinutes

    /**
     * @return status
     */
    public String getStatus() {
        return null != status ? status : "";
    }// end getStatus

    /**
     * @param status
     */
    public void setStatus(String status) {
        this.status = status;
    }// end setStatus

    /**
     * @return details
     */
    public String getDetails() {
        return null != details ? details : "";
    }// end getDetails

    /**
     * @param details
     */
    public void setDetails(String details) {
        this.details = details;
    }// end setDetails

    public Long getRunNumber() {
        return null != runNumber ? runNumber : 0L;
    }// end getRunNumber

    public void setRunNumber(Long runNumber) {
        this.runNumber = runNumber;
    }// end setRunNumber

    /**
     * @return from
     */
    public String getFrom() {
        return null != from ? from : "";
    }// end getFrom

    /**
     * @param from
     */
    public void setFrom(String from) {
        this.from = from;
    }// end setFrom

}// end class
