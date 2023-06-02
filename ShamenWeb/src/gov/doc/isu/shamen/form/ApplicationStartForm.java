package gov.doc.isu.shamen.form;

import static gov.doc.isu.dwarf.resources.Constants.EMPTY_STRING;
import static gov.doc.isu.dwarf.resources.Constants.NEW_LINE;

import gov.doc.isu.dwarf.core.AbstractForm;

/**
 * Form bean for the Application Sign On Information for the Data Utility application.
 * 
 * @author <strong>Steven L. Skinner</strong>
 */
public class ApplicationStartForm extends AbstractForm {

    /**
     * 
     */
    private static final long serialVersionUID = -1892791277847837278L;
    private String userId = EMPTY_STRING;
    private String password = EMPTY_STRING;

    /**
     * Default Constructor
     */
    public ApplicationStartForm() {

    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId.trim();
    }

    /**
     * @param userId
     *        the userId to set
     */
    public void setUserId(final String userId) {
        this.userId = userId;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password.trim();
    }

    /**
     * @param password
     *        the password to set
     */
    public void setPassword(final String password) {
        this.password = password;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer builder = new StringBuffer();
        builder.append("ApplicationStartForm [userId=");
        builder.append(userId).append(NEW_LINE);
        builder.append("password=");
        builder.append(password).append(NEW_LINE);
        builder.append(super.toString());
        builder.append("]");
        return builder.toString();
    }
}
