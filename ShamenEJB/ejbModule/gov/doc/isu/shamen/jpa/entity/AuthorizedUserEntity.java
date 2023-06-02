/**
 * 
 */
package gov.doc.isu.shamen.jpa.entity;

import static gov.doc.isu.shamen.ejb.util.EJBConstants.DEFAULT_NEW_LINE;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.AdditionalCriteria;

/**
 * This class is used to model an authorized user for the application.
 * <p>
 * This class contains annotations for JPA functionality.
 * 
 * @author Joseph Burris JCCC
 */
@Entity
@Table(name = "Authorized_Users", schema = "Trans")
@NamedQueries({@NamedQuery(name = "AuthorizedUserEntity.FIND_ALL", query = "SELECT au FROM AuthorizedUserEntity au ORDER BY au.lastName, au.firstName,au.userID"), @NamedQuery(name = "AuthorizedUserEntity.FIND_BY_USER_ID", query = "SELECT au FROM AuthorizedUserEntity au WHERE au.userID = :userID"), @NamedQuery(name = "AuthorizedUserEntity.FIND_FOR_EMAIL", query = "SELECT au.userID FROM AuthorizedUserEntity au WHERE au.emailInd = 'Y'")})
@AdditionalCriteria("this.common.deleteIndicator = 'N'")
public class AuthorizedUserEntity implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "User_Ref_Id", unique = true, columnDefinition = "INT NOT NULL")
    private Long userRefId;
    @Column(name = "User_First_Nm")
    private String firstName;
    @Column(name = "User_Last_Nm")
    private String lastName;
    @Column(name = "User_Id")
    private String userID;
    @Column(name = "Email_Ind")
    private String emailInd;
    @Column(name = "Authority")
    private String authority;
    @Embedded
    private CommonEntity common;

    /**
     * This constructor may be used to instantiate this class.
     */
    public AuthorizedUserEntity() {
        super();
    }// end constructor

    /**
     * This constructor may be used to instantiate this class.
     * 
     * @param userRefId
     *        userRefId
     */
    public AuthorizedUserEntity(Long userRefId) {
        super();
        this.userRefId = userRefId;
    }// end constructor

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer builder = new StringBuffer();
        builder.append("AuthorizedUserEntity [userRefId=");
        builder.append(userRefId).append(DEFAULT_NEW_LINE);
        builder.append(", firstName=");
        builder.append(firstName).append(DEFAULT_NEW_LINE);
        builder.append(", lastName=");
        builder.append(lastName).append(DEFAULT_NEW_LINE);
        builder.append(", userID=");
        builder.append(userID).append(DEFAULT_NEW_LINE);
        builder.append(", emailInd=");
        builder.append(emailInd).append(DEFAULT_NEW_LINE);
        builder.append(", authority=");
        builder.append(authority).append(DEFAULT_NEW_LINE);
        builder.append(", common=");
        builder.append(common);
        builder.append("]");
        return builder.toString();
    }

    /**
     * This method returns the formatted first and last name variables in the format: last, first
     * 
     * @return The formatted name.
     */
    public String getFullName() {
        return (null != lastName ? lastName.trim() : "") + (null != lastName && !"".equals(lastName) && null != firstName && !"".equals(firstName) ? ", " : "") + (null != firstName ? firstName.trim() : "");
    }// end getFullName

    /**
     * @return the userRefId
     */
    public Long getUserRefId() {
        return userRefId;
    }// end getUserRefId

    /**
     * @param userRefId
     *        the userRefId to set
     */
    public void setUserRefId(Long userRefId) {
        this.userRefId = userRefId;
    }// end setUserRefId

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }// end getFirstName

    /**
     * @param firstName
     *        the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }// end setFirstName

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }// end getLastName

    /**
     * @param lastName
     *        the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }// end setLastName

    /**
     * @return the userID
     */
    public String getUserID() {
        return userID;
    }// end getUserID

    /**
     * @param userID
     *        the userID to set
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }// end setUserID

    /**
     * @return the common
     */
    public CommonEntity getCommon() {
        return common;
    }// end getCommon

    /**
     * @param common
     *        the common to set
     */
    public void setCommon(CommonEntity common) {
        this.common = common;
    }// end setCommon

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

}// end class
