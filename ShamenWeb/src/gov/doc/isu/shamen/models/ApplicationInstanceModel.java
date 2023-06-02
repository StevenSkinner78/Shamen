package gov.doc.isu.shamen.models;

import java.sql.Timestamp;

import gov.doc.isu.dwarf.model.CommonModel;

/**
 * The persistent class for the Application Instance database table.
 */
public class ApplicationInstanceModel extends CommonModel {
    private static final long serialVersionUID = 1L;

    private Long applicationInstanceRefId;
    private ApplicationModel application;
    private String applicationInstanceName;
    private String instantiationTs;
    private String primaryInd;
    private String status;
    private Timestamp timeOfDeathTs;
    private String verifiedApplicationStatusCd;
    private String verifiedVersionNm;
    private String verifiedBuildNm;
    private String verifiedEarNm;
    private String verifiedAddtnlInfo;

    /**
     * Default Constructor
     */
    public ApplicationInstanceModel() {
        super();
    }// end constructor

    /**
     * Contructor
     *
     * @param applicationInstanceName
     *        the instance name
     * @param instantiationTs
     *        the instatiated time
     * @param status
     *        the status
     */
    public ApplicationInstanceModel(String applicationInstanceName, String instantiationTs, String status) {
        super();
        this.applicationInstanceName = applicationInstanceName;
        this.instantiationTs = instantiationTs;
        this.status = status;
    }// end constructor

    /**
     * @return the applicationInstanceRefId
     */
    public Long getApplicationInstanceRefId() {
        return applicationInstanceRefId;
    }// end getApplicationInstanceRefId

    /**
     * @param applicationInstanceRefId
     *        the applicationInstanceRefId to set
     */
    public void setApplicationInstanceRefId(Long applicationInstanceRefId) {
        this.applicationInstanceRefId = applicationInstanceRefId;
    }// end setApplicationInstanceRefId

    /**
     * @return the application
     */
    public ApplicationModel getApplication() {
        return application;
    }// end getApplication

    /**
     * @param application
     *        the application to set
     */
    public void setApplication(ApplicationModel application) {
        this.application = application;
    }// end setApplication

    /**
     * @return the applicationInstanceName
     */
    public String getApplicationInstanceName() {
        return applicationInstanceName;
    }// end getApplicationInstanceName

    /**
     * @param applicationInstanceName
     *        the applicationInstanceName to set
     */
    public void setApplicationInstanceName(String applicationInstanceName) {
        this.applicationInstanceName = applicationInstanceName;
    }// end setApplicationInstanceName

    /**
     * @return the instantiationTs
     */
    public String getInstantiationTs() {
        return instantiationTs;
    }// end getInstantiationTs

    /**
     * @param instantiationTs
     *        the instantiationTs to set
     */
    public void setInstantiationTs(String instantiationTs) {
        this.instantiationTs = instantiationTs;
    }// end setInstantiationTs

    /**
     * @return the primaryInd
     */
    public String getPrimaryInd() {
        return primaryInd;
    }// end getPrimaryInd

    /**
     * @param primaryInd
     *        the primaryInd to set
     */
    public void setPrimaryInd(String primaryInd) {
        this.primaryInd = primaryInd;
    }// end setPrimaryInd

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }// end getStatus

    /**
     * @param status
     *        the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }// end setStatus

    /**
     * @return the timeOfDeathTs
     */
    public Timestamp getTimeOfDeathTs() {
        return timeOfDeathTs;
    }// end getTimeOfDeathTs

    /**
     * @param timeOfDeathTs
     *        the timeOfDeathTs to set
     */
    public void setTimeOfDeathTs(Timestamp timeOfDeathTs) {
        this.timeOfDeathTs = timeOfDeathTs;
    }// end setTimeOfDeathTs

    /**
     * @return the verifiedApplicationStatusCd
     */
    public String getVerifiedApplicationStatusCd() {
        return verifiedApplicationStatusCd;
    }// end getVerifiedApplicationStatusCd

    /**
     * @param verifiedApplicationStatusCd
     *        the verifiedApplicationStatusCd to set
     */
    public void setVerifiedApplicationStatusCd(String verifiedApplicationStatusCd) {
        this.verifiedApplicationStatusCd = verifiedApplicationStatusCd;
    }// end setVerifiedApplicationStatusCd

    /**
     * @return the verifiedVersionNm
     */
    public String getVerifiedVersionNm() {
        return verifiedVersionNm;
    }// end getVerifiedVersionNm

    /**
     * @param verifiedVersionNm
     *        the verifiedVersionNm to set
     */
    public void setVerifiedVersionNm(String verifiedVersionNm) {
        this.verifiedVersionNm = verifiedVersionNm;
    }// end setVerifiedVersionNm

    /**
     * @return the verifiedBuildNm
     */
    public String getVerifiedBuildNm() {
        return verifiedBuildNm;
    }// end getVerifiedBuildNm

    /**
     * @param verifiedBuildNm
     *        the verifiedBuildNm to set
     */
    public void setVerifiedBuildNm(String verifiedBuildNm) {
        this.verifiedBuildNm = verifiedBuildNm;
    }// end setVerifiedBuildNm

    /**
     * @return the verifiedEarNm
     */
    public String getVerifiedEarNm() {
        return verifiedEarNm;
    }// end getVerifiedEarNm

    /**
     * @param verifiedEarNm
     *        the verifiedEarNm to set
     */
    public void setVerifiedEarNm(String verifiedEarNm) {
        this.verifiedEarNm = verifiedEarNm;
    }// end setVerifiedEarNm

    /**
     * @return the verifiedAddtnlInfo
     */
    public String getVerifiedAddtnlInfo() {
        return verifiedAddtnlInfo;
    }// end getVerifiedAddtnlInfo

    /**
     * @param verifiedAddtnlInfo
     *        the verifiedAddtnlInfo to set
     */
    public void setVerifiedAddtnlInfo(String verifiedAddtnlInfo) {
        this.verifiedAddtnlInfo = verifiedAddtnlInfo;
    }// end setVerifiedAddtnlInfo

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer builder = new StringBuffer();
        builder.append("ApplicationInstanceModel [applicationInstanceRefId=");
        builder.append(applicationInstanceRefId);
        builder.append(", application=");
        builder.append(application);
        builder.append(", applicationInstanceName=");
        builder.append(applicationInstanceName);
        builder.append(", instantiationTs=");
        builder.append(instantiationTs);
        builder.append(", primaryInd=");
        builder.append(primaryInd);
        builder.append(", status=");
        builder.append(status);
        builder.append(", timeOfDeathTs=");
        builder.append(timeOfDeathTs);
        builder.append(", verifiedApplicationStatusCd=");
        builder.append(verifiedApplicationStatusCd);
        builder.append(", verifiedVersionNm=");
        builder.append(verifiedVersionNm);
        builder.append(", verifiedBuildNm=");
        builder.append(verifiedBuildNm);
        builder.append(", verifiedEarNm=");
        builder.append(verifiedEarNm);
        builder.append(", verifiedAddtnlInfo=");
        builder.append(verifiedAddtnlInfo);
        builder.append("]");
        return builder.toString();
    }// end toString

}// end class
