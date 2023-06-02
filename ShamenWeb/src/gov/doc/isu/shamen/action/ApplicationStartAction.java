package gov.doc.isu.shamen.action;

import static gov.doc.isu.dwarf.resources.Constants.APPLICATION_USER;
import static gov.doc.isu.dwarf.resources.Constants.EMPTY_STRING;
import static gov.doc.isu.dwarf.resources.Constants.FAILURE;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import gov.doc.isu.dwarf.exception.BaseException;
import gov.doc.isu.dwarf.exception.DAOException;
import gov.doc.isu.exception.ActiveDirectoryValidationException;
import gov.doc.isu.models.SystemUser;
import gov.doc.isu.shamen.business.AuthorizedUserInfo;
import gov.doc.isu.shamen.form.ApplicationStartForm;
import gov.doc.isu.shamen.models.AuthorizedUserModel;
import gov.doc.isu.shamen.resources.AppConstants;
import gov.doc.isu.validation.ActiveDirectoryValidator;

/**
 * Action class used to log on and log off user.
 * 
 * @author <strong>Steven Skinner</strong>
 */
public class ApplicationStartAction extends ShamenDispatchAction {
    private static Logger log = Logger.getLogger("gov.doc.isu.shamen.action.ApplicationStartAction");

    /**
     * Verify user is authorized to this application.
     * 
     * @param mapping
     *        (REQUIRED)- Information that the controller, RequestProcessor, knows about mapping the request to the instance of this Action class.
     * @param form
     *        (REQUIRED) - A JavaBean associated with mapping. It will have its properties initialized from the request parameters. Subsequently, properties of this bean have been populated
     * @param request
     *        (REQUIRED) - Provides data including parameter name and values, attributes, and an input stream
     * @param response
     *        (OPTIONAL) - Assist in sending a response to the client
     * @return ActionForward - Describes where and how control should be forwarded
     * @throws Exception
     *         if an exception occured
     */
    public ActionForward logOn(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.debug("Entering ApplicationStartAction.logOn");
        ActionMessages errors = new ActionMessages();
        ActionForward forward = mapping.findForward("success");
        ApplicationStartForm theForm = (ApplicationStartForm) form;
        AuthorizedUserModel thisUser = null;
        AuthorizedUserInfo userInfo = AuthorizedUserInfo.getInstance();

        try{
            String userId = EMPTY_STRING;
            log.debug("validating input fields. userId=" + theForm.getUserId() + " ,password is protected.");
            // User id is set by the current user name and password passed in.
            errors = theForm.validate(mapping, request);
            if(!errors.isEmpty()){
                this.saveErrors(request, errors);
                forward = mapping.getInputForward();
            }else{
                log.debug("logging in user with user id=" + theForm.getUserId() + ", password is protected.");
                if(theForm.getUserId().equalsIgnoreCase(AppConstants.JMETER_USER_NAME)){
                    if(!theForm.getPassword().equalsIgnoreCase(AppConstants.JMETER_PASSWORD)){
                        String msg = "The request for a JMeter Load test was rejected because of a invalid Jmeter User password. Please notify the application administrator.";
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.signOn.error", msg));
                    }
                    userId = AppConstants.JMETER_USER_NAME;
                }else{
                    userId = ActiveDirectoryValidator.authenticateUser(theForm.getUserId(), theForm.getPassword());

                }
            }// end else
            if(errors.isEmpty()){
                log.debug("Authenticating application with userId=" + String.valueOf(userId));
                thisUser = userInfo.findAuthorizedUserByUserId(userId);

                if(thisUser != null){
                    request.getSession().setAttribute(APPLICATION_USER, thisUser);
                    request.getSession().setAttribute(SystemUser.SESSION_SYSTEM_USER, new SystemUser(String.valueOf(thisUser.getUpdateUserRefId()), thisUser.getUserID()));
                }else{
                    String msg = "User " + String.valueOf(userId) + " is not authorized for this application. Please notify the application administrator.";
                    errors.add("userId", new ActionMessage("errors.signOn.error", msg));
                }// end else
            }// end if
            setupTabs(request, null, true);
        }catch(ActiveDirectoryValidationException e){
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.signOn.error", e.getMessage()));
            log.error("ActiveDirectoryValidationException occurred in the ApplicationStartAction.logOn. e=" + e.getMessage());
        }catch(DAOException e){
            log.error("DAOException occurred in the ApplicationStartAction.logOn. e=" + e.getMessage());
            forward = mapping.findForward(FAILURE);
        }catch(BaseException e){
            log.error("Exception occurred in the ApplicationStartAction.logOn. e=" + e.getMessage());
            forward = mapping.findForward(FAILURE);
        }catch(Exception e){
            log.error("Exception occurred in the ApplicationStartAction.logOn. e=" + e.getMessage());
            forward = mapping.findForward(FAILURE);
        }catch(Throwable e){
            log.error("Exception occurred in the ApplicationStartAction.logOn. e=" + e.getMessage());
            forward = mapping.findForward(FAILURE);
        }finally{
            if(!errors.isEmpty()){
                this.saveErrors(request, errors);
                forward = mapping.getInputForward();
            }// end if
        }// end finally
        log.debug("Exiting ApplicationStartAction.logOn");
        return forward;
    }// end method

    /**
     * Cleans up session and logs user off.
     * 
     * @param mapping
     *        (REQUIRED)- Information that the controller, RequestProcessor, knows about mapping the request to the instance of this Action class.
     * @param form
     *        (REQUIRED) - A JavaBean associated with mapping. It will have its properties initialized from the request parameters. Subsequently, properties of this bean have been populated
     * @param request
     *        (REQUIRED) - Provides data including parameter name and values, attributes, and an input stream
     * @param response
     *        (OPTIONAL) - Assist in sending a response to the client
     * @return ActionForward - Describes where and how control should be forwarded
     */
    public ActionForward logOff(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering ApplicationStartAction.logOff");
        try{
            HttpSession session = request.getSession(false);
            // Check if user in session; log them off
            if(session != null){
                log.info("Invalidate Session Id: {}, Remote Addr: {}" + session.getId() + ", " + request.getRemoteAddr());
                log.debug("Session exists for the user");
                AuthorizedUserModel authorizedUserModel = null;
                if(session.getAttribute(APPLICATION_USER) != null){
                    authorizedUserModel = (AuthorizedUserModel) session.getAttribute(APPLICATION_USER);
                } // end if
                if(authorizedUserModel != null){
                    log.info(" User Logging Off");
                    log.debug("********************************************************");
                    log.debug(" Session attributes for AuthorizedUserModel... {} " + authorizedUserModel.getUserID());
                    log.debug(" Session Creation Time: {} " + new Date(session.getCreationTime()));
                    log.debug(" Session Last Accessed Time: {} " + new Date(session.getLastAccessedTime()));
                    log.debug(" Maximum Inactive Interval: {} seconds " + session.getMaxInactiveInterval());
                    log.debug(" Session Invalidated");
                    log.debug("********************************************************");
                }else{
                    log.debug("The Authorized User Was Not Found In Session");
                } // end else
                session.invalidate();
            }// end if
        }catch(Exception e){
            log.error("Exception caught in ApplicationStartAction method logoff", e);
            return mapping.findForward(FAILURE);
        } // end main try catch for entire method
        log.debug("Exiting ApplicationStartAction.logOff");
        return mapping.findForward("logoff");
    } // end method

}// end class
