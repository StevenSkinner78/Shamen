/**
 * @author <strong>Brian Hicks</strong> JCCC, Mar 3, 2016
 */
package gov.doc.isu.shamen.models;

import static gov.doc.isu.dwarf.resources.Constants.NEW_LINE;

import java.util.List;

import gov.doc.isu.dwarf.model.CommonModel;
import gov.doc.isu.dwarf.resources.Constants;
import gov.doc.isu.dwarf.util.AppUtil;
import gov.doc.isu.dwarf.util.StringUtil;

/**
 * Model object to hold an Application record from the DB
 *
 * @author <strong>Brian Hicks</strong> JCCC, Mar 3, 2016
 */
public class ApplicationModel extends CommonModel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Long applicationRefId;
    private String applicationAddress;
    private String applicationName;
    private String applicationType = "WEB";
    private String applicationTypeDesc;
    private String statusInd;
    private String statusIndDesc;
    private String requestStatusInd;
    private String requestStatusIndDesc;
    private String statusComment;
    private String showApplicationNotification;
    private String applicationNotificationDesc;
    private String encryptionKey;
    private SystemModel system;
    private List<BatchAppModel> batchApps;
    private String verifiedVersionNm;
    private String verifiedBuildNm;
    private String verifiedEarNm;
    private String verifiedAddtnlInfo;
    private List<ApplicationInstanceModel> appInstances;
    private AuthorizedUserModel pointOfContact;

    /**
     * default constructor
     */
    public ApplicationModel() {
        super();
    }// end constructor

    /**
     * @param name
     *        name
     */
    public ApplicationModel(String name) {
        super();
        this.applicationName = name;
    }// end constructor

    /**
     * @return the applicationRefId
     */
    public Long getApplicationRefId() {
        return applicationRefId;
    }// end getApplicationRefId

    /**
     * @param applicationRefId
     *        the applicationRefId to set
     */
    public void setApplicationRefId(Long applicationRefId) {
        this.applicationRefId = applicationRefId;
    }// end setApplicationRefId

    /**
     * @return the applicationAddress
     */
    public String getApplicationAddress() {
        return applicationAddress;
    }// end getApplicationAddress

    /**
     * @param applicationAddress
     *        the applicationAddress to set
     */
    public void setApplicationAddress(String applicationAddress) {
        this.applicationAddress = applicationAddress;
    }// end setApplicationAddress

    /**
     * @return the applicationName
     */
    public String getApplicationName() {
        return applicationName;
    }// end getApplicationName

    /**
     * @param applicationName
     *        the applicationName to set
     */
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }// end setApplicationName

    /**
     * @return the applicationType
     */
    public String getApplicationType() {
        return applicationType;
    }// end getApplicationType

    /**
     * @param applicationType
     *        the applicationType to set
     */
    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
    }// end setApplicationType

    /**
     * @return the applicationTypeDesc
     */
    public String getApplicationTypeDesc() {
        return applicationTypeDesc;
    }// end getApplicationTypeDesc

    /**
     * @param applicationTypeDesc
     *        the applicationTypeDesc to set
     */
    public void setApplicationTypeDesc(String applicationTypeDesc) {
        this.applicationTypeDesc = applicationTypeDesc;
    }// end setApplicationTypeDesc

    /**
     * @return the statusInd
     */
    public String getStatusInd() {
        return statusInd;
    }// end getStatusInd

    /**
     * @param statusInd
     *        the statusInd to set
     */
    public void setStatusInd(String statusInd) {
        this.statusInd = statusInd;
    }// end setStatusInd

    /**
     * @return the statusIndDesc
     */
    public String getStatusIndDesc() {
        return statusIndDesc;
    }// end getStatusIndDesc

    /**
     * @param statusIndDesc
     *        the statusIndDesc to set
     */
    public void setStatusIndDesc(String statusIndDesc) {
        this.statusIndDesc = statusIndDesc;
    }// end setStatusIndDesc

    /**
     * @return the requestStatusInd
     */
    public String getRequestStatusInd() {
        return requestStatusInd;
    }// end getRequestStatusInd

    /**
     * @param requestStatusInd
     *        the requestStatusInd to set
     */
    public void setRequestStatusInd(String requestStatusInd) {
        this.requestStatusInd = requestStatusInd;
    }// end setRequestStatusInd

    /**
     * @return the requestStatusIndDesc
     */
    public String getRequestStatusIndDesc() {
        return requestStatusIndDesc;
    }// end getRequestStatusIndDesc

    /**
     * @param requestStatusIndDesc
     *        the requestStatusIndDesc to set
     */
    public void setRequestStatusIndDesc(String requestStatusIndDesc) {
        this.requestStatusIndDesc = requestStatusIndDesc;
    }// end setRequestStatusIndDesc

    /**
     * @return the statusComment
     */
    public String getStatusComment() {
        return statusComment;
    }// end getStatusComment

    /**
     * @param statusComment
     *        the statusComment to set
     */
    public void setStatusComment(String statusComment) {
        this.statusComment = statusComment;
    }// end setStatusComment

    /**
     * @return the showApplicationNotification
     */
    public String getShowApplicationNotification() {
        return showApplicationNotification;
    }

    /**
     * @param showApplicationNotification
     *        the showApplicationNotification to set
     */
    public void setShowApplicationNotification(String showApplicationNotification) {
        this.showApplicationNotification = showApplicationNotification;
    }

    /**
     * @return the applicationNotificationDesc
     */
    public String getApplicationNotificationDesc() {
        return applicationNotificationDesc;
    }

    /**
     * @param applicationNotificationDesc
     *        the applicationNotificationDesc to set
     */
    public void setApplicationNotificationDesc(String applicationNotificationDesc) {
        this.applicationNotificationDesc = applicationNotificationDesc;
    }

    /**
     * @return the batchApps
     */
    public List<BatchAppModel> getBatchApps() {
        return batchApps;
    }// end getBatchApps

    /**
     * @param batchApps
     *        the batchApps to set
     */
    public void setBatchApps(List<BatchAppModel> batchApps) {
        this.batchApps = batchApps;
    }// end setBatchApps

    /**
     * @return the encryptionKey
     */
    public String getEncryptionKey() {
        return encryptionKey;
    }// end getEncryptionKey

    /**
     * @param encryptionKey
     *        the encryptionKey to set
     */
    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }// end setEncryptionKey

    /**
     * Return boolean value of handshake established
     *
     * @return Boolean
     */
    public Boolean isHandshakeEstablished() {
        return StringUtil.isNullOrEmpty(encryptionKey);
    }// end isHandshakeEstablished

    /**
     * @return the system
     */
    public SystemModel getSystem() {
        return system;
    }// end getSystem

    /**
     * @param system
     *        the system to set
     */
    public void setSystem(SystemModel system) {
        this.system = system;
    }// end setSystem

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

    /**
     * @return the appInstances
     */
    public List<ApplicationInstanceModel> getAppInstances() {
        return appInstances;
    }// end getAppInstances

    /**
     * @param appInstances
     *        the appInstances to set
     */
    public void setAppInstances(List<ApplicationInstanceModel> appInstances) {
        this.appInstances = appInstances;
    }// end setAppInstances

    /**
     * @return the pointOfContact
     */
    public AuthorizedUserModel getPointOfContact() {
        return pointOfContact;
    }// end getPointOfContact

    /**
     * @param pointOfContact
     *        the pointOfContact to set
     */
    public void setPointOfContact(AuthorizedUserModel pointOfContact) {
        this.pointOfContact = pointOfContact;
    }// end setPointOfContact

    /**
     * This method calculates the number of instances for the application
     *
     * @return String
     */
    public String getInstanceCount() {
        String count = "0";
        if(appInstances != null && !appInstances.isEmpty()){
            count = String.valueOf(appInstances.size());
        }// end if
        return count;
    }// end getInstanceCount

    /**
     * This method gets a display of instance names.
     *
     * @return String
     */
    public String getInstancesDisplay() {
        StringBuffer sb = new StringBuffer();
        if(appInstances != null && !appInstances.isEmpty()){
            for(int i = 0;i < appInstances.size();i++){
                sb.append(appInstances.get(i).getApplicationInstanceName());
                if(i < appInstances.size()){
                    sb.append(Constants.NEW_LINE);
                }// end if
            }// end for
        }// end if
        return sb.toString();
    }// end getInstancesDisplay
    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */

    @Override
    public String toString() {
        StringBuffer builder = new StringBuffer();
        builder.append("ApplicationModel [").append(NEW_LINE);
        builder.append("applicationRefId=");
        builder.append(applicationRefId).append(NEW_LINE);
        builder.append("applicationAddress=");
        builder.append(applicationAddress).append(NEW_LINE);
        builder.append("applicationName=");
        builder.append(applicationName).append(NEW_LINE);
        builder.append("applicationType=");
        builder.append(applicationTypeDesc).append(NEW_LINE);
        builder.append("statusInd=");
        builder.append(statusInd).append(NEW_LINE);
        builder.append("statusIndDesc=");
        builder.append(statusIndDesc).append(NEW_LINE);
        builder.append("requestStatusInd=");
        builder.append(requestStatusInd).append(NEW_LINE);
        builder.append("requestStatusIndDesc=");
        builder.append(requestStatusIndDesc).append(NEW_LINE);
        builder.append("showApplicationNotification=");
        builder.append(showApplicationNotification).append(NEW_LINE);
        builder.append("applicationNotificationDesc=");
        builder.append(applicationNotificationDesc).append(NEW_LINE);
        builder.append("batchApps.size()=");
        builder.append(AppUtil.isEmpty(batchApps) ? "empty" : batchApps.size()).append(NEW_LINE);
        builder.append(super.toString());
        builder.append("]");
        return builder.toString();
    }// end toString
}// end ApplicationModel
