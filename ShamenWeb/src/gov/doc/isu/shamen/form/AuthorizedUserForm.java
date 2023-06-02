package gov.doc.isu.shamen.form;

import static gov.doc.isu.dwarf.resources.Constants.NEW_LINE;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

import gov.doc.isu.dwarf.core.AbstractForm;
import gov.doc.isu.dwarf.model.CodeModel;
import gov.doc.isu.shamen.models.AuthorizedUserModel;

/**
 * Form bean for the Authorized User Information for the Data Utility application.
 * 
 * @author <strong>Steven L. Skinner</strong>
 */
public class AuthorizedUserForm extends AbstractForm {

    /**
     * 
     */
    private static final long serialVersionUID = -1679203021836737734L;
    private AuthorizedUserModel authUser;
    private String caller;
    private List<CodeModel> emailIndList;
    private boolean admnHold;
    private List<CodeModel> authorityList;

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        super.reset(mapping, request);
        if(authUser != null){
            authUser.setEmailInd("N");
        }// end if
    }// end reset

    /**
     * @return the authUser
     */
    public AuthorizedUserModel getAuthUser() {
        return authUser;
    }

    /**
     * @param authUser
     *        the authUser to set
     */
    public void setAuthUser(final AuthorizedUserModel authUser) {
        this.authUser = authUser;
    }

    /**
     * @return the emailIndList
     */
    public List<CodeModel> getEmailIndList() {
        return emailIndList;
    }

    /**
     * @param emailIndList
     *        the emailIndList to set
     */
    public void setEmailIndList(List<CodeModel> emailIndList) {
        this.emailIndList = emailIndList;
    }

    /**
     * @return the admnHold
     */
    public boolean isAdmnHold() {
        return admnHold;
    }

    /**
     * @param admnHold
     *        the admnHold to set
     */
    public void setAdmnHold(boolean admnHold) {
        this.admnHold = admnHold;
    }

    /**
     * @return the authorityList
     */
    public List<CodeModel> getAuthorityList() {
        return authorityList;
    }

    /**
     * @param authorityList
     *        the authorityList to set
     */
    public void setAuthorityList(List<CodeModel> authorityList) {
        this.authorityList = authorityList;
    }

    /**
     * init method sets the admnJold variable so that there is a check to make sure at least one admninstrator is left in the database.
     */
    public void init() {
        if(authUser != null){
            if(authUser.getAuthority().equalsIgnoreCase("ADMN")){
                setAdmnHold(true);
            }else{
                setAdmnHold(false);
            }// end else
        }// end if
    }// end init

    /**
     * @return the caller
     */
    public String getCaller() {
        return caller;
    }

    /**
     * @param caller
     *        the caller to set
     */
    public void setCaller(String caller) {
        this.caller = caller;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer builder = new StringBuffer();
        builder.append("AuthorizedUserForm [authUser=");
        builder.append(authUser).append(NEW_LINE);
        builder.append(super.toString());
        builder.append("]");
        return builder.toString();
    }

}
