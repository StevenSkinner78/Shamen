package gov.doc.isu.shamen.action;

import static gov.doc.isu.dwarf.resources.Constants.FAILURE;
import static gov.doc.isu.dwarf.resources.Constants.LIST;
import static gov.doc.isu.dwarf.resources.Constants.REFRESH;
import static gov.doc.isu.shamen.resources.AppConstants.DEFAULT_PAGE_SIZE;
import static gov.doc.isu.shamen.resources.AppConstants.USER_MANAGEMENT_FIELDS;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import gov.doc.isu.dwarf.model.ColumnModel;
import gov.doc.isu.dwarf.resources.Constants;
import gov.doc.isu.dwarf.util.AppUtil;
import gov.doc.isu.dwarf.util.StringUtil;
import gov.doc.isu.shamen.business.AuthorizedUserInfo;
import gov.doc.isu.shamen.business.SystemInfo;
import gov.doc.isu.shamen.exception.UserAlreadyExistsException;
import gov.doc.isu.shamen.form.AuthorizedUserForm;
import gov.doc.isu.shamen.models.AuthorizedUserModel;
import gov.doc.isu.shamen.util.ShamenUtil;

/**
 * Action class that handles all request for the Authorized Users
 *
 * @author <strong>Steven Skinner</strong>
 */
public class AuthorizedUserAction extends ShamenDispatchAction {
    private static Logger log = Logger.getLogger("gov.doc.isu.shamen.action.AuthorizedUserAction");

    /**
     * This method gets the list of authorized users.
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
    public ActionForward list(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering AuthorizedUserAction.list");
        AuthorizedUserForm theForm = (AuthorizedUserForm) form;
        ActionForward forward = mapping.findForward(LIST);
        if(!(theForm.isAdministrator())){
            log.debug("User is not Administrator. Forwarding to error page.");
            forward = createErrorMessage(mapping, request, "error.generic.authorization");
        }else{
            AuthorizedUserInfo userInfo = AuthorizedUserInfo.getInstance();
            // This needed for export of PDF view
            List<AuthorizedUserModel> userList = new ArrayList<AuthorizedUserModel>();
            try{
                theForm.setEmailIndList(userInfo.getEmailIndList());
                theForm.setAuthorityList(userInfo.getAuthorityList());
                if(theForm.getFilterSearchObject() != null && !checkFilterSearchObject(theForm.getFilterSearchObject())){
                    theForm.setSessionList(userInfo.getUsersList());
                    return search(mapping, theForm, request, response);
                }// end if
                Constants.LIST_NAME = "AUTHORIZED USERS LIST";
                theForm.semiFlush();
                theForm.setNoRecords(false);
                log.debug("setting search object.");
                setFilterSearchObject(theForm, USER_MANAGEMENT_FIELDS);
                log.debug("loading the user list for display.");
                userList = userInfo.getUsersList();
                if(AppUtil.isEmpty(userList)){
                    userList = new ArrayList<AuthorizedUserModel>();
                    theForm.setNoRecords(true);
                    theForm.setGoButtonDisable("disabled");
                    AuthorizedUserModel t = new AuthorizedUserModel();
                    t.setColumnData(ShamenUtil.getNoRecordsFoundForDisplay(null, USER_MANAGEMENT_FIELDS));
                    userList.add(t);
                }// end if
                log.debug("setting userList. userList.size=" + (AppUtil.isEmpty(userList) ? "null/empty" : userList.size()));
                theForm.setDatalist(userList);
                theForm.setSessionList(userList);
                if(StringUtil.isNullOrEmpty(theForm.getPageSize())){
                    theForm.setPageSize(DEFAULT_PAGE_SIZE);
                }// end if
                theForm.setColumnInfo(userList.get(0).getColumnData());
                setupTabs(request, null, true);
            }catch(Exception e){
                log.error("Exception occurred in AuthorizedUserAction.list. ", e);
                forward = mapping.findForward(FAILURE);
            }// end try
        }// end if/else
        log.debug("Exiting AuthorizedUserAction.list");
        return forward;
    }// end method

    /**
     * Add view of a new record
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
    public ActionForward addUser(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering AuthorizedUserAction.addView");
        AuthorizedUserForm theForm = (AuthorizedUserForm) form;
        ActionForward forward = mapping.findForward("editUser");
        if(!(theForm.isAdministrator())){
            log.debug("User is not Administrator. Forwarding to error page.");
            forward = createErrorMessage(mapping, request, "error.generic.authorization");
        }else{
            try{
                theForm.setAuthUser(new AuthorizedUserModel());
                setUserRefIds(theForm.getAuthUser(), request);
                theForm.setEmailIndList(AuthorizedUserInfo.getInstance().getEmailIndList());
                theForm.setAuthorityList(AuthorizedUserInfo.getInstance().getAuthorityList());
                setupTabs(request, "AdminInfoLink", true);
            }catch(Exception e){
                log.error("Exception occured in AuthorizedUserAction.addView. ", e);
                forward = mapping.findForward(FAILURE);
            }// end try
        }// end if/else
        log.debug("Exiting AuthorizedUserAction.addView");
        return forward;
    }// end methof

    /**
     * Edit view of an existing record
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
    public ActionForward editView(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering AuthorizedUserAction.editView");
        AuthorizedUserForm theForm = (AuthorizedUserForm) form;
        ActionForward forward = mapping.findForward("editUser");
        if(!(theForm.isAdministrator())){
            log.debug("User is not Administrator. Forwarding to error page.");
            forward = createErrorMessage(mapping, request, "error.generic.authorization");
        }else{
            AuthorizedUserInfo userInfo = AuthorizedUserInfo.getInstance();
            try{
                setCaller(theForm, request);
                Long userRefId = 0L;
                if(!StringUtil.isNullOrEmpty(request.getParameter("userRefId"))){
                    log.debug("Retrieving uset ref id from request.");
                    userRefId = Long.valueOf(request.getParameter("userRefId"));
                }// end if
                log.debug("loading user by ref id. userRefId=" + userRefId);
                AuthorizedUserModel userTO = userInfo.findUserByUserRefId(userRefId);
                theForm.setAuthUser(userTO);
                theForm.init();
                if(userRefId == 99L){
                    theForm.setRestrict(true);
                }// end if
                setUserRefIds(userTO, request);
                theForm.setEmailIndList(userInfo.getEmailIndList());
                theForm.setAuthorityList(userInfo.getAuthorityList());
                request.getSession().setAttribute("sessionBatchList", theForm.getAuthUser().getBatchApps());
                request.getSession().setAttribute("sessionAppList", theForm.getAuthUser().getApps());
                setupTabs(request, "AdminInfoLink", true);
            }catch(Exception e){
                log.error("Exception occured in AuthorizedUserAction.editView. ", e);
                forward = mapping.findForward(FAILURE);
            }// end try
        }// end if/else
        log.debug("Exiting AuthorizedUserAction.editView");
        return forward;
    }// end method

    /**
     * Save a new or existing record
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
    public ActionForward addSave(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering AuthorizedUserAction.addSave");
        ActionMessages errors = new ActionMessages();
        AuthorizedUserForm theForm = (AuthorizedUserForm) form;
        ActionForward forward = mapping.findForward(REFRESH);
        if(!(theForm.isAdministrator())){
            log.debug("User is not Administrator. Forwarding to error page.");
            forward = createErrorMessage(mapping, request, "error.generic.authorization");
        }else{
            AuthorizedUserInfo userInfo = AuthorizedUserInfo.getInstance();
            if(theForm.getSubType() != null && theForm.getSubType().equalsIgnoreCase("Filter")){
                log.debug("Go button was clicked. Forward to search method.");
                return search(mapping, theForm, request, response);
            }// end if
            if(theForm.getCaller() != null){
                String path = mapping.findForward(theForm.getCaller()).getPath();
                forward = new ActionForward(path, true);
            }// end if
            if(isCancelled(request)){
                return forward;
            }// end if
            AuthorizedUserModel userTO = theForm.getAuthUser();

            try{
                log.debug("validating input fields. authorizedUser=" + StringUtil.isNull(userTO));
                errors = theForm.validate(mapping, request);
                if(!errors.isEmpty()){
                    this.saveErrors(request, errors);
                    forward = mapping.getInputForward();
                }else{
                    log.debug("adding user=" + StringUtil.isNull(userTO));
                    // String userId = ActiveDirectoryValidator.verifyUser(userTO.getUserID());
                    userTO.setUserID(userTO.getUserID());
                    if(!AppUtil.isNotNullAndZero(userTO.getUserRefId())){
                        userInfo.saveUser(userTO);
                    }else{
                        userInfo.updateUser(userTO);
                    }// end else
                    setupTabs(request, null, true);
                }// end if/else
            }catch(UserAlreadyExistsException e){
                errors.add("authUser.userID", new ActionMessage("errors.user.useridinuse", userTO.getUserID()));
                forward = mapping.getInputForward();
            }catch(Exception e){
                log.error("Exception occured in AuthorizedUserAction.addSave. ", e);
                forward = mapping.findForward(FAILURE);
            }finally{
                if(!errors.isEmpty()){
                    this.saveErrors(request, errors);
                    forward = mapping.getInputForward();
                    setupTabs(request, "AdminInfoLink", true);
                } // end if
            }// end try
        }// end if/else
        log.debug("Exiting AuthorizedUserAction.addSave");
        return forward;
    }// end method

    /**
     * Delete a authorized user record.
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
    public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering AuthorizedUserAction.delete");
        ActionMessages errors = new ActionMessages();
        ActionForward forward = mapping.findForward(REFRESH);
        AuthorizedUserInfo userInfo = AuthorizedUserInfo.getInstance();
        try{
            Long userRefId = 0L;

            if(!StringUtil.isNullOrEmpty(request.getParameter("userRefId"))){
                log.debug("Retrieving uset ref id from request.");
                userRefId = Long.valueOf(request.getParameter("userRefId"));
            }// end if
            log.debug("deleting Authorized User. userRefId=" + userRefId);
            if(SystemInfo.getInstance().isUnassignedUser(userRefId)){
                String msg = "User is the assigned Point of Contact for Unassigned System. Cannot delete user.";
                errors.add("", new ActionMessage("errors.authDelete.error", msg));
            }else if(userRefId.equals(getAppUser(request).getUserRefId())){
                String msg = "User not authorized to delete his/her own record.";
                errors.add("", new ActionMessage("errors.authDelete.error", msg));
            }else{
                userInfo.deleteUser(userRefId, getAppUser(request).getUserRefId());
            }// end else
        }catch(Exception e){
            log.error("an error occurred in AuthorizedUserAction.delete. ", e);
            forward = mapping.findForward(FAILURE);
        }finally{
            if(!errors.isEmpty()){
                this.saveErrors(request, errors);
                forward = mapping.getInputForward();
            }// end if
        }// end try
        setupTabs(request, null, true);
        log.debug("Exiting AuthorizedUserAction.delete");
        return forward;
    }// end method

    /**
     * This method uses the filter object to search sessionList on form for matching records.
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
    @SuppressWarnings("unchecked")
    public ActionForward search(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering AuthorizedUserAction.search");
        AuthorizedUserForm theForm = (AuthorizedUserForm) form;
        ActionForward forward = mapping.findForward(LIST);
        theForm.setNoRecords(false);
        try{
            ArrayList<AuthorizedUserModel> userList = new ArrayList<AuthorizedUserModel>();
            List<ColumnModel> fieldList = theForm.getFilterSearchObject();
            if(!AppUtil.isEmpty(fieldList)){
                List<AuthorizedUserModel> list = (ArrayList<AuthorizedUserModel>) theForm.getSessionList();
                for(int i = 0, j = list.size();i < j;i++){
                    if(ShamenUtil.filter(fieldList, list.get(i).getColumnData())){
                        userList.add(list.get(i));
                    }// end if
                }// end for
                theForm.setFilterSearchObject(fieldList);
            }// end if
            log.debug("setting userList based on search object. userList.size=" + (AppUtil.isEmpty(userList) ? "null/empty" : userList.size()));
            if(AppUtil.isEmpty(userList)){
                // check to see if all input boxes are empty. If so the reset list
                // to full list.
                if(checkFilterSearchObject(fieldList)){
                    userList = (ArrayList<AuthorizedUserModel>) theForm.getSessionList();
                }else{
                    theForm.setNoRecords(true);
                    AuthorizedUserModel t = new AuthorizedUserModel();
                    t.setColumnData(ShamenUtil.getNoRecordsFoundForDisplay(null, USER_MANAGEMENT_FIELDS));
                    userList.add(t);
                }// end else
            }// end if
            theForm.setDatalist(userList);
        }catch(Exception e){
            log.error("an error occurred in AuthorizedUserAction.search. ", e);
            forward = mapping.findForward(FAILURE);
        }// end try
        theForm.setSubType(null);
        setupTabs(request, null, true);
        log.debug("Exiting AuthorizedUserAction.search");
        return forward;
    }// end method

    /**
     * Set the caller parameter into the form
     *
     * @param theForm
     *        action form
     * @param request
     *        HTTP servlet request
     */
    private void setCaller(AuthorizedUserForm theForm, HttpServletRequest request) {
        log.debug("Entering setCaller");
        log.debug("Set the caller parameter into the form");
        if(request.getParameter("caller") != null){
            theForm.setCaller(request.getParameter("caller"));
        }// end if
        log.debug("Exiting setCaller");
    }// end setCaller
}// end class
