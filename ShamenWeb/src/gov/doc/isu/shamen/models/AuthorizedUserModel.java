/**
 * @author <strong>Steven Skinner</strong> JCCC, Sep 10, 2015
 */
package gov.doc.isu.shamen.models;

import static gov.doc.isu.dwarf.resources.Constants.NEW_LINE;

import java.util.List;

import gov.doc.isu.dwarf.model.CommonModel;

/**
 * Model object to hold the Authorized Users record.
 *
 * @author <strong>Steven Skinner</strong> JCCC, Sep 10, 2015
 */
public class AuthorizedUserModel extends CommonModel {

    /**
     *
     */
    private static final long serialVersionUID = -8301528488601310789L;
    private Long userRefId;
    private String firstName;
    private String lastName;
    private String userID;
    private String emailInd;
    private String authority;
    private List<SystemModel> systems;
    private List<BatchAppModel> batchApps;
    private List<ApplicationModel> apps;
    private int numOfSystems;
    private int numOfBatchApps;
    private int numOfWebApps;

    /**
     * Default Constructor
     */
    public AuthorizedUserModel() {

    }

    /**
     * Constructor with ref id passed in
     *
     * @param refId
     *        refId
     */
    public AuthorizedUserModel(Long refId) {
        this.userRefId = refId;

    }

    /**
     * Constructor with name fields passed in
     *
     * @param firstName
     *        firstName
     * @param lastName
     *        lastName
     */
    public AuthorizedUserModel(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Constructor with name fields passed in
     *
     * @param refId
     *        refId
     * @param firstName
     *        firstName
     * @param lastName
     *        lastName
     */
    public AuthorizedUserModel(Long refId, String firstName, String lastName) {
        this.userRefId = refId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Constructor with all fields passed in
     *
     * @param refId
     *        refId
     * @param firstName
     *        firstName
     * @param lastName
     *        lastName
     * @param userId
     *        userId
     * @param authority
     *        authority
     */
    public AuthorizedUserModel(Long refId, String firstName, String lastName, String userId, String authority) {
        this.userRefId = refId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userID = userId;
        this.authority = authority;
    }

    /**
     * Gets the Volunteer full name as lastNm, firstNm
     *
     * @return String
     */
    public String getFullName() {
        StringBuffer sb = new StringBuffer();
        sb.append(getLastName().charAt(0));
        sb.append(getLastName().substring(1, getLastName().length()).toLowerCase().trim());
        if(getFirstName() != null && !getFirstName().equalsIgnoreCase("")){
            sb.append(", ");
            sb.append(getFirstName().toUpperCase().charAt(0));
            sb.append(getFirstName().substring(1, getFirstName().length()).toLowerCase().trim());
        }
        return sb.toString();
    }

    /**
     * Gets the Volunteer full name as lastNm, firstNm
     *
     * @return String
     */
    public String getFullNameAbbr() {
        StringBuffer sb = new StringBuffer();
        sb.append(lastName.charAt(0));
        sb.append(lastName.substring(1, lastName.length()).toLowerCase().trim());
        if(firstName != null && !firstName.equalsIgnoreCase("")){
            sb.append(", ");
            sb.append(firstName.toUpperCase().charAt(0));
            sb.append(".");
        }
        return sb.toString();
    }

    /**
     * @return the userRefId
     */
    public Long getUserRefId() {
        return userRefId;
    }

    /**
     * @param userRefId
     *        the userRefId to set
     */
    public void setUserRefId(Long userRefId) {
        this.userRefId = userRefId;
    }

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName
     *        the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName
     *        the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the userID
     */
    public String getUserID() {
        return userID;
    }

    /**
     * @param userID
     *        the userID to set
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }

    /**
     * @return the emailInd
     */
    public String getEmailInd() {
        return emailInd;
    }

    /**
     * @param emailInd
     *        the emailInd to set
     */
    public void setEmailInd(String emailInd) {
        this.emailInd = emailInd;
    }

    /**
     * @return the authority
     */
    public String getAuthority() {
        return authority;
    }

    /**
     * @param authority
     *        the authority to set
     */
    public void setAuthority(String authority) {
        this.authority = authority;
    }

    /**
     * @return the systems
     */
    public List<SystemModel> getSystems() {
        return systems;
    }

    /**
     * @param systems
     *        the systems to set
     */
    public void setSystems(List<SystemModel> systems) {
        this.systems = systems;
    }

    /**
     * @return the batchApps
     */
    public List<BatchAppModel> getBatchApps() {
        return batchApps;
    }

    /**
     * @param batchApps
     *        the batchApps to set
     */
    public void setBatchApps(List<BatchAppModel> batchApps) {
        this.batchApps = batchApps;
    }

    /**
     * @return the apps
     */
    public List<ApplicationModel> getApps() {
        return apps;
    }

    /**
     * @param apps
     *        the apps to set
     */
    public void setApps(List<ApplicationModel> apps) {
        this.apps = apps;
    }

    /**
     * @return the numOfSystems
     */
    public int getNumOfSystems() {
        return numOfSystems;
    }

    /**
     * @param numOfSystems
     *        the numOfSystems to set
     */
    public void setNumOfSystems(int numOfSystems) {
        this.numOfSystems = numOfSystems;
    }

    /**
     * @return the numOfBatchApps
     */
    public int getNumOfBatchApps() {
        return numOfBatchApps;
    }

    /**
     * @param numOfBatchApps
     *        the numOfBatchApps to set
     */
    public void setNumOfBatchApps(int numOfBatchApps) {
        this.numOfBatchApps = numOfBatchApps;
    }

    /**
     * @return the numOfWebApps
     */
    public int getNumOfWebApps() {
        return numOfWebApps;
    }

    /**
     * @param numOfWebApps
     *        the numOfWebApps to set
     */
    public void setNumOfWebApps(int numOfWebApps) {
        this.numOfWebApps = numOfWebApps;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer builder = new StringBuffer();
        builder.append("AuthorizedUserModel [userRefId=");
        builder.append(userRefId).append(NEW_LINE);
        builder.append("firstName=");
        builder.append(firstName).append(NEW_LINE);
        builder.append("lastName=");
        builder.append(lastName).append(NEW_LINE);
        builder.append("userID=");
        builder.append(userID).append(NEW_LINE);
        builder.append("emailInd=");
        builder.append(emailInd).append(NEW_LINE);
        builder.append(super.toString());
        builder.append("]");
        return builder.toString();
    }

}
