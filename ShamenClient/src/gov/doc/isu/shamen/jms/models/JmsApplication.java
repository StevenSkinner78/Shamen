/**
 * @(#)JmsApplication.java 1.0.0 Copyright (c) 2018 Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit All Rights Reserved. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: -Redistribution of source code must retain the above copyright notice, this list of conditions and the following disclaimer. -Redistribution in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. Neither the name of Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit or the names of contributors may be used to endorse or promote products derived from this software without specific prior written permission. This software is provided "AS IS," without a warranty of any kind. ALL EXPRESS OR IMPLIED
 *                         CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. You
 *                         acknowledge that this software is not designed, licensed or intended for use in the design, construction, operation or maintenance of any nuclear facility.
 */
package gov.doc.isu.shamen.jms.models;

import gov.doc.isu.shamen.jms.ShamenCipher;

/**
 * Model object to hold the Application Record
 * 
 * @author <strong>Brian Hicks</strong> JCCC, Mar 1, 2016
 */
public class JmsApplication extends JmsBaseObject {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Long applicationRefId;
    private String applicationAddress;
    private String applicationName;
    private String applicationInstanceName;
    private String applicationType;
    private String status;
    private String requestedStatus;
    private String showAppNotification;
    private String appNotification;
    // Extra information that applications may provide.
    private String verifiedBranch;
    private String verifiedVersion;
    private String verifiedEar;
    private String verifiedAdditionalInfo;
    private Boolean confirmed;

    private ShamenCipher sc;

    /**
     * Default Constructor with no fields
     */
    public JmsApplication() {
        super();
    }

    /**
     * Constructor with fields
     * 
     * @param applicationName
     * @param applicationAddress
     */
    public JmsApplication(String applicationName, String applicationAddress) {
        super();
        this.applicationName = applicationName;
        this.applicationAddress = applicationAddress;
    }

    /**
     * Constructor with fields
     * 
     * @param applicationName
     * @param applicationAddress
     * @param status
     */
    public JmsApplication(String applicationName, String applicationAddress, String status) {
        super();
        this.applicationName = applicationName;
        this.applicationAddress = applicationAddress;
        this.status = status;
    }

    /**
     * Constructor with fields
     * 
     * @param applicationAddress
     * @param applicationName
     * @param status
     * @param sc
     */
    public JmsApplication(String applicationName, String applicationAddress, String status, ShamenCipher sc) {
        super();
        this.applicationName = applicationName;
        this.applicationAddress = applicationAddress;
        this.status = status;
        this.sc = sc;
    }

    /**
     * Return whether or not the handshake has been established for this application. If it has a password, then it has been established.
     * 
     * @return true or false
     */
    public Boolean isHandshakeEstablished() {
        Boolean established = false;
        if(sc != null){
            if(sc.getPassword() != null){
                established = true;
            }// end if
        }// end if
        return established;
    }// end isHandshakeEstablished

    /**
     * @return the applicationRefId
     */
    public Long getApplicationRefId() {
        return applicationRefId;
    }

    /**
     * @param applicationRefId
     *        the applicationRefId to set
     */
    public void setApplicationRefId(Long applicationRefId) {
        this.applicationRefId = applicationRefId;
    }

    /**
     * @return the applicationAddress
     */
    public String getApplicationAddress() {
        return applicationAddress;
    }

    /**
     * @param applicationAddress
     *        the applicationAddress to set
     */
    public void setApplicationAddress(String applicationAddress) {
        this.applicationAddress = applicationAddress;
    }

    /**
     * @return the applicationName
     */
    public String getApplicationName() {
        return applicationName;
    }

    /**
     * @param applicationName
     *        the applicationName to set
     */
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    /**
     * @return the applicationType
     */
    public String getApplicationType() {
        return applicationType;
    }

    /**
     * @param applicationType
     *        the applicationType to set
     */
    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
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

    /**
     * @return the sc
     */
    public ShamenCipher getSc() {
        return sc;
    }

    /**
     * @param sc
     *        the sc to set
     */
    public void setSc(ShamenCipher sc) {
        this.sc = sc;
    }

    /**
     * @return the requestedStatus
     */
    public String getRequestedStatus() {
        return requestedStatus;
    }

    /**
     * @param requestedStatus
     *        the requestedStatus to set
     */
    public void setRequestedStatus(String requestedStatus) {
        this.requestedStatus = requestedStatus;
    }

    /**
     * @return the showAppNotification
     */
    public String getShowAppNotification() {
        return showAppNotification;
    }

    /**
     * @param showAppNotification
     *        the showAppNotification to set
     */
    public void setShowAppNotification(String showAppNotification) {
        this.showAppNotification = showAppNotification;
    }

    /**
     * @return the appNotification
     */
    public String getAppNotification() {
        return appNotification;
    }

    /**
     * @param appNotification
     *        the appNotification to set
     */
    public void setAppNotification(String appNotification) {
        this.appNotification = appNotification;
    }

    /**
     * Get the application's key which is a combo of the application name and address.
     * 
     * @return key
     */
    public String getAppKey() {
        return trim(applicationName + applicationAddress);
    }

    /**
     * Get the application's key which is a formatted combo of the application name and address.
     * 
     * @return formatted key
     */
    public String getFormattedAppKey() {
        return String.valueOf(applicationName) + " - " + String.valueOf(applicationAddress);
    }

    /**
     * This method is a null safe trim utility method.
     * 
     * @param inString
     *        The String to be trimmed
     * @return trimmed value
     */
    public String trim(String inString) {
        String returnString = null;
        if(inString != null){
            returnString = inString.trim();
        }// end if
        return returnString;
    }// end trim

    /**
     * @return the verifiedBranch
     */
    public String getVerifiedBranch() {
        return verifiedBranch;
    }

    /**
     * @param verifiedBranch
     *        the verifiedBranch to set
     */
    public void setVerifiedBranch(String verifiedBranch) {
        this.verifiedBranch = verifiedBranch;
    }

    /**
     * @return the verifiedVersion
     */
    public String getVerifiedVersion() {
        return verifiedVersion;
    }

    /**
     * @param verifiedVersion
     *        the verifiedVersion to set
     */
    public void setVerifiedVersion(String verifiedVersion) {
        this.verifiedVersion = verifiedVersion;
    }

    /**
     * @return the verifiedEar
     */
    public String getVerifiedEar() {
        return verifiedEar;
    }

    /**
     * @param verifiedEar
     *        the verifiedEar to set
     */
    public void setVerifiedEar(String verifiedEar) {
        this.verifiedEar = verifiedEar;
    }

    /**
     * @return the verifiedAdditionalInfo
     */
    public String getVerifiedAdditionalInfo() {
        return verifiedAdditionalInfo;
    }

    /**
     * @param verifiedAdditionalInfo
     *        the verifiedAdditionalInfo to set
     */
    public void setVerifiedAdditionalInfo(String verifiedAdditionalInfo) {
        this.verifiedAdditionalInfo = verifiedAdditionalInfo;
    }

    /**
     * @return the confirmed
     */
    public Boolean getConfirmed() {
        return confirmed;
    }

    /**
     * @param confirmed
     *        the confirmed to set
     */
    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }

    /**
     * @return the applicationInstanceName
     */
    public String getApplicationInstanceName() {
        return applicationInstanceName;
    }

    /**
     * @param applicationInstanceName
     *        the applicationInstanceName to set
     */
    public void setApplicationInstanceName(String applicationInstanceName) {
        this.applicationInstanceName = applicationInstanceName;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer builder = new StringBuffer();
        builder.append("JmsApplication [applicationRefId=");
        builder.append(applicationRefId);
        builder.append(", applicationAddress=");
        builder.append(applicationAddress);
        builder.append(", applicationName=");
        builder.append(applicationName);
        builder.append(", applicationInstanceName=");
        builder.append(applicationInstanceName);
        builder.append(", applicationType=");
        builder.append(applicationType);
        builder.append(", status=");
        builder.append(status);
        builder.append(", requestedStatus=");
        builder.append(requestedStatus);
        builder.append(", showAppNotification=");
        builder.append(showAppNotification);
        builder.append(", showAppNotification=");
        builder.append(appNotification);
        builder.append(", verifiedBranch=");
        builder.append(verifiedBranch);
        builder.append(", verifiedVersion=");
        builder.append(verifiedVersion);
        builder.append(", verifiedEar=");
        builder.append(verifiedEar);
        builder.append(", verifiedAdditionalInfo=");
        builder.append(verifiedAdditionalInfo);
        builder.append(", confirmed=");
        builder.append(confirmed);
        builder.append(", sc=");
        builder.append(sc);
        builder.append("]");
        return builder.toString();
    }

}// end JmsApplication
