/**
 * @author <strong>Brian Hicks</strong> JCCC, Mar 3, 2016
 */
package gov.doc.isu.shamen.form;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.util.MessageResources;

import gov.doc.isu.dwarf.core.AbstractForm;
import gov.doc.isu.dwarf.model.CodeModel;
import gov.doc.isu.dwarf.util.AppUtil;
import gov.doc.isu.dwarf.util.StringUtil;
import gov.doc.isu.shamen.models.ApplicationModel;

/**
 * Form bean for the Application Information for the Shamen Web Interface application.
 *
 * @author <strong>Brian Hicks</strong> JCCC, Mar 3, 2016
 */
public class ApplicationForm extends AbstractForm {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static Logger log = Logger.getLogger("gov.doc.isu.shamen.form.ApplicationForm");
    private ApplicationModel application;
    private List<ApplicationModel> appList;
    // private List<CodeModel> appTypeList;
    private List<CodeModel> statusIndList;
    private List<CodeModel> confirmedStatusIndList;
    private List<CodeModel> systemList;
    private List<CodeModel> pocList;
    private String caller;

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        super.reset(mapping, request);
    }// end reset

    /**
     * @return the application
     */
    public ApplicationModel getApplication() {
        return application;
    }

    /**
     * @param application
     *        the application to set
     */
    public void setApplication(ApplicationModel application) {
        this.application = application;
    }

    /**
     * @return the appList
     */
    public List<ApplicationModel> getAppList() {
        return appList;
    }

    /**
     * @param appList
     *        the appList to set
     */
    public void setAppList(List<ApplicationModel> appList) {
        this.appList = appList;
    }

    // /**
    // * @return the appTypeList
    // */
    // public List<CodeModel> getAppTypeList() {
    // return appTypeList;
    // }
    //
    // /**
    // * @param appTypeList
    // * the appTypeList to set
    // */
    // public void setAppTypeList(List<CodeModel> appTypeList) {
    // this.appTypeList = appTypeList;
    // }

    /**
     * @return the statusIndList
     */
    public List<CodeModel> getStatusIndList() {
        return statusIndList;
    }

    /**
     * @param statusIndList
     *        the statusIndList to set
     */
    public void setStatusIndList(List<CodeModel> statusIndList) {
        this.statusIndList = statusIndList;
    }

    /**
     * @return the confirmedStatusIndList
     */
    public List<CodeModel> getConfirmedStatusIndList() {
        return confirmedStatusIndList;
    }

    /**
     * @param confirmedStatusIndList
     *        the confirmedStatusIndList to set
     */
    public void setConfirmedStatusIndList(List<CodeModel> confirmedStatusIndList) {
        this.confirmedStatusIndList = confirmedStatusIndList;
    }

    /**
     * Used to validate the fields on the applicationInfo.jsp.
     *
     * @param messageResources
     *        {@link MessageResources}
     * @return {@link ActionMessages}
     */
    public ActionMessages validateAppData(MessageResources messageResources) {
        log.debug("Entering ApplicationForm.validateAppData(). Parameter: MessageResources=" + (!String.valueOf(messageResources).equals(null) ? messageResources : null));
        log.debug("Used to validate the fields on the applicationInfo.jsp.");
        ActionMessages errors = new ActionMessages();
        if(StringUtil.isNullOrEmpty(application.getApplicationName())){
            errors.add("application.applicationName", new ActionMessage("errors.required", messageResources.getMessage("prompt.app.name")));
        }// end if
        if(!AppUtil.isNotNullAndZero(application.getPointOfContact().getUserRefId())){
            errors.add("application.pointOfContact.userRefId", new ActionMessage("errors.required", messageResources.getMessage("common.label.poc")));
        }// end if
        if(StringUtil.isNullOrEmpty(application.getApplicationAddress())){
            errors.add("application.applicationAddress", new ActionMessage("errors.required", messageResources.getMessage("prompt.app.location")));
        }// end if
        if(!StringUtil.isNullOrEmpty(application.getShowApplicationNotification()) && "Y".equalsIgnoreCase(application.getShowApplicationNotification())){
            if(StringUtil.isNullOrEmpty(application.getApplicationNotificationDesc())){
                errors.add("application.applicationNotificationDesc", new ActionMessage("errors.required", messageResources.getMessage("prompt.app.notificationDesc")));
            }
            if(!StringUtil.isNullOrEmpty(application.getApplicationNotificationDesc()) && application.getApplicationNotificationDesc().length() > 1000){
                errors.add("application.applicationNotificationDesc", new ActionMessage("errors.maxlength", new Object[]{messageResources.getMessage("prompt.app.notificationDesc"), "1000"}));
            }// end if
        }
        if(StringUtil.isNullOrEmpty(application.getStatusInd()) && StringUtil.isNullOrEmpty(application.getRequestStatusInd())){
            errors.add("application.requestStatusInd", new ActionMessage("errors.required", messageResources.getMessage("prompt.app.status.request")));
        }// end if
        if(!StringUtil.isNullOrEmpty(application.getRequestStatusInd()) && !application.getRequestStatusInd().equals(application.getStatusInd())){
            if(StringUtil.isNullOrEmpty(application.getStatusComment())){
                errors.add("application.statusComment", new ActionMessage("errors.required", messageResources.getMessage("prompt.app.status.comment")));
            }// end if
            if(!StringUtil.isNullOrEmpty(application.getStatusComment()) && application.getStatusComment().length() > 1000){
                errors.add("application.statusComment", new ActionMessage("errors.maxlength", new Object[]{messageResources.getMessage("prompt.app.status.comment"), "1000"}));
            }// end if
        }// end if
        log.debug("Entering ApplicationForm.validateAppData(). Return: ActionMessages=" + (!String.valueOf(errors).equals(null) ? errors : null));
        return errors;
    }// end validateAppData

    /**
     * @return the systemList
     */
    public List<CodeModel> getSystemList() {
        return systemList;
    }

    /**
     * @param systemList
     *        the systemList to set
     */
    public void setSystemList(List<CodeModel> systemList) {
        this.systemList = systemList;
    }

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

    /**
     * @return the pocList
     */
    public List<CodeModel> getPocList() {
        return pocList;
    }

    /**
     * @param pocList
     *        the pocList to set
     */
    public void setPocList(List<CodeModel> pocList) {
        this.pocList = pocList;
    }

}// end ApplicationForm
