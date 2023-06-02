package gov.doc.isu.shamen.business;

import static gov.doc.isu.dwarf.resources.Constants.DEFAULTNODESC;
import static gov.doc.isu.dwarf.resources.Constants.DEFAULTYESDESC;
import static gov.doc.isu.dwarf.resources.Constants.EMPTY_STRING;
import static gov.doc.isu.shamen.resources.AppConstants.SHAMEN_DEFAULT_LABEL;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import gov.doc.isu.dwarf.exception.BaseException;
import gov.doc.isu.dwarf.model.CodeModel;
import gov.doc.isu.dwarf.util.AppUtil;
import gov.doc.isu.dwarf.util.StringUtil;
import gov.doc.isu.shamen.convertor.ObjectConvertor;
import gov.doc.isu.shamen.ejb.beans.view.AuthorizedUserBeanLocal;
import gov.doc.isu.shamen.exception.UserAlreadyExistsException;
import gov.doc.isu.shamen.jpa.entity.ApplicationEntity;
import gov.doc.isu.shamen.jpa.entity.AuthorizedUserEntity;
import gov.doc.isu.shamen.jpa.entity.BatchAppEntity;
import gov.doc.isu.shamen.jpa.entity.SystemEntity;
import gov.doc.isu.shamen.models.AuthorizedUserModel;
import gov.doc.isu.shamen.models.SystemModel;
import net.sf.json.JSONArray;

/**
 * This is the processor class for AuthorizedUserInfo.
 * <p>
 * Modifications:
 * <ul>
 * <li>Enforced singleton pattern.</li>
 * <li>Removed reflection instances for getting class name.</li>
 * </ul>
 *
 * @author <strong>Steven L. Skinner</strong>
 */
public class AuthorizedUserInfo implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static Logger log = Logger.getLogger("gov.doc.isu.shamen.business.AuthorizedUserInfo");
    private static final AuthorizedUserInfo INSTANCE = new AuthorizedUserInfo();

    /**
     * This constructor made private to block instantiating this class.
     */
    private AuthorizedUserInfo() {
        super();
    }// end constructor

    /**
     * This method returns the private static final instance variable to enforce the Singleton pattern.
     *
     * @return An instance of this class.
     */
    public static AuthorizedUserInfo getInstance() {
        log.debug("Returning the static instance of the AuthorizedUserInfo class.");
        return INSTANCE;
    }// end getInstance

    /**
     * This method is called to use a JNDI lookup for accessing the {@link AuthorizedUserBeanLocal}.
     *
     * @return An instance of the <code>AuthorizedUserBeanLocal</code>.
     * @throws BaseException
     *         An exception if the <code>AuthorizedUserBeanLocal</code> class cannot be accessed.
     */
    public AuthorizedUserBeanLocal getBean() throws BaseException {
        log.debug("Entering AuthorizedUserInfo.getBean().");
        log.debug("This method is called to use a JNDI lookup for accessing the AuthorizedUserBeanLocal.");
        AuthorizedUserBeanLocal bean;
        try{
            Context context = new InitialContext();
            bean = (AuthorizedUserBeanLocal) context.lookup("java:global/ShamenEAR/ShamenEJB/AuthorizedUserBean!gov.doc.isu.shamen.ejb.beans.view.AuthorizedUserBeanLocal");
        }catch(NamingException e){
            log.error("An exception occurred while attempting to access the AuthorizedUserBean from the ShamenServiceBean. Message is: " + e.getMessage());
            throw new BaseException(e.getMessage());
        }// end try/catch
        log.debug("Exiting getBean. Return value is: " + String.valueOf(bean));
        return bean;
    }// end getBean

    /**
     * This method is used to search for a Authorized user record by userID to see if they are an Authorized User to this system.
     *
     * @param userSignOn
     *        the native sign on to search by
     * @return AuthorizedUserModel if user is valid or throws exception if not.
     * @throws BaseException
     *         to demonstrate user is not authorized.
     */
    public AuthorizedUserModel findAuthorizedUserByUserId(String userSignOn) throws BaseException {
        log.debug("Entering AuthorizedUserInfo.findAuthorizedUserByUserId(). Incoming parameter is userID = " + String.valueOf(userSignOn));
        log.debug("This method is used to search for a Authorized user record by userID to see if they are an Authorized User to this system.");
        AuthorizedUserEntity entity = null;
        try{
            entity = getBean().findAuthorizedUserByUserId(userSignOn);
        }catch(Exception e){
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        AuthorizedUserModel model = null;
        if(null != entity){
            model = ObjectConvertor.toAuthorizedUserBusiness(entity);
        }// end if
        log.debug("Exiting AuthorizedUserInfo.findAuthorizedUserByUserId model=" + StringUtil.isNull(model));
        return model;
    } // end findAuthorizedUserByUserId

    /**
     * This method is used to add an Authorized User.
     *
     * @param authorizedUserModel
     *        the record to add if one does not already exist.
     * @throws BaseException
     *         if an exception occurred
     */
    public void saveUser(AuthorizedUserModel authorizedUserModel) throws BaseException {
        log.debug("Entering AuthorizedUserInfo.saveUser. Incoming parameter is: " + (authorizedUserModel == null ? "null" : authorizedUserModel.toString()));
        log.debug("This method is used to add an Authorized User.");
        AuthorizedUserEntity entity = null;
        try{
            log.debug("checking to see if the user already exists with a user id of " + authorizedUserModel.getUserID());
            entity = getBean().findAuthorizedUserByUserId(authorizedUserModel.getUserID());
        }catch(Exception e){
            log.info("User ID does not exist. Adding to database");
        }// end try/catch
        if(entity != null){
            log.debug("user id already exists throwing an exception.");
            throw new UserAlreadyExistsException("a record with userId of " + authorizedUserModel.getUserID() + " already exists");
        } // end if
        log.debug("user does not exist in the db so inserting them");

        entity = ObjectConvertor.toAuthorizedUserPersist(authorizedUserModel);
        // Set the current timestamp to the createTs before merging.
        entity.getCommon().setCreateTime(AppUtil.getSQLTimestamp());
        entity.getCommon().setDeleteIndicator("N");

        try{
            getBean().merge(entity);
        }catch(Exception e){
            log.error("An exception occurred while merging an authorized user entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Exiting AuthorizedUserInfo.saveUser");
    } // end saveUser

    /**
     * This method is used to load a List of {@link AuthorizedUserModel} for display.
     *
     * @return list of users
     * @throws BaseException
     *         if an exception occurred
     */
    public List<AuthorizedUserModel> getUsersList() throws BaseException {
        log.debug("Entering AuthorizedUserInfo.getUsersList().");
        log.debug("This method is used to load a List of AuthorizedUserModel for display.");
        List<AuthorizedUserModel> list = new ArrayList<AuthorizedUserModel>();
        List<AuthorizedUserEntity> entities = null;
        try{
            entities = (List<AuthorizedUserEntity>) getBean().getUsersList();
        }catch(Exception e){
            log.error("An exception occurred while retrieving authorized user list. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        if(null != entities && !entities.isEmpty()){
            list = ObjectConvertor.toAuthorizedUserBusinessList(entities);
        }else{
            log.warn("The authorized user list in the database is null or empty. An empty list of authorized user models will be returned.");
        }// end if/else
        log.debug("Exiting AuthorizedUserInfo.getUsersList userList.size=" + (AppUtil.isEmpty(list) ? "empty" : list.size()));
        return list;
    } // end getUsersList

    /**
     * This method is used to load a JSONArray authorized user data for chart display.
     *
     * @param labels
     *        a String[] of labels
     * @return JSONArray
     * @throws BaseException
     *         if an exception occurred
     */
    public JSONArray getUsersListForChart(String[] labels) throws BaseException {
        log.debug("Entering AuthorizedUserInfo.getUsersList().");
        log.debug("This method is used to load a List of AuthorizedUserModel for display.");
        List<AuthorizedUserModel> list = new ArrayList<AuthorizedUserModel>();
        List<AuthorizedUserEntity> entities = null;
        List<SystemEntity> system = null;
        List<ApplicationEntity> webApp = null;
        JSONArray array = null;
        try{
            entities = (List<AuthorizedUserEntity>) getBean().getUsersList();
        }catch(Exception e){
            log.error("An exception occurred while retrieving authorized user list. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        if(null != entities && !entities.isEmpty()){
            AuthorizedUserModel model = null;
            for(int i = 0;i < entities.size();i++){
                model = new AuthorizedUserModel(entities.get(i).getUserRefId(), entities.get(i).getFirstName(), entities.get(i).getLastName());
                Integer count = 0;
                try{
                    system = getBean().getSystemListForUser(model.getUserRefId());
                    count = getBean().countBatchAppsAndCollectionsByUser(model.getUserRefId());
                    webApp = getBean().getWebAppListForUser(model.getUserRefId());
                    model.setNumOfBatchApps(count);
                    model.setNumOfSystems(system == null ? 0 : system.size());
                    model.setNumOfWebApps(webApp == null ? 0 : webApp.size());
                }catch(Exception e){
                    log.error("An exception occurred while retrieving authorized user list. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
                    throw new BaseException(e.getMessage(), e);
                }// end try/catch
                list.add(model);
            }
            array = ObjectConvertor.convertToUserData(list, labels);
        }else{
            log.warn("The authorized user list in the database is null or empty. An empty list of authorized user models will be returned.");
        }// end if/else
        log.debug("Exiting AuthorizedUserInfo.getUsersList userList.size=" + (AppUtil.isEmpty(list) ? "empty" : list.size()));
        return array;
    } // end getUsersList

    /**
     * This method is used to load a List of {@link CodeModel} for display.
     *
     * @return list of users as codes
     * @throws BaseException
     *         if an exception occurred
     */
    public List<CodeModel> getUsersListAsCode() throws BaseException {
        log.debug("Entering AuthorizedUserInfo.getUsersListAsCode().");
        log.debug("This method is used to load a List of CodeModel for display.");
        List<CodeModel> list = new ArrayList<CodeModel>();
        List<AuthorizedUserEntity> entities = null;
        try{
            entities = (List<AuthorizedUserEntity>) getBean().getUsersList();
        }catch(Exception e){
            log.error("An exception occurred while retrieving authorized user list. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        if(null != entities && !entities.isEmpty()){
            list = ObjectConvertor.toAuthorizedUserCodeList(entities);
        }else{
            log.warn("The authorized user list in the database is null or empty. An empty list of authorized user models will be returned.");
        }// end if/else
        log.debug("Exiting AuthorizedUserInfo.getUsersListAsCode list.size=" + (AppUtil.isEmpty(list) ? "empty" : list.size()));
        return list;
    } // end getUsersListAsCode

    /**
     * This method is used to load an Authorized User for edit.
     *
     * @param authorizedUserRefId
     *        the ref id to search by.
     * @return AuthorizedUserModel user
     * @throws BaseException
     *         if an exception occurred
     */
    public AuthorizedUserModel findUserByUserRefId(Long authorizedUserRefId) throws BaseException {
        log.debug("Entering AuthorizedUserInfo.findUserByUserRefId(). Incoming parameter is: " + String.valueOf(authorizedUserRefId));
        log.debug("This method is used to load an Authorized User for edit.");
        AuthorizedUserEntity entity = null;
        List<SystemEntity> system = null;
        List<BatchAppEntity> batch = null;
        List<ApplicationEntity> webApp = null;
        try{
            entity = getBean().findAuthorizedUserByUserRefId(authorizedUserRefId);
            system = getBean().getSystemListForUser(authorizedUserRefId);
            batch = getBean().getBatchListForUser(authorizedUserRefId);
            webApp = getBean().getWebAppListForUser(authorizedUserRefId);
        }catch(Exception e){
            log.error("An exception occurred while retrieving an authorized user entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        AuthorizedUserModel model = null;
        if(null != entity){
            model = ObjectConvertor.toAuthorizedUserBusiness(entity);
            model.setSystems(new ArrayList<SystemModel>());
            model.setBatchApps(ObjectConvertor.toBatchAppListBusiness(batch));
            model.setApps(ObjectConvertor.convertForApplicationList(webApp));

            if(system != null && !system.isEmpty()){
                for(int i = 0, j = system.size();i < j;i++){
                    model.getSystems().add(new SystemModel(system.get(i).getSystemName(), system.get(i).getSystemDesc()));
                }// end for
            }// end if
        }else{
            log.warn("No authorized user model was retrieved from the authorized user bean for user reference ID: " + String.valueOf(authorizedUserRefId));
        }// end if
        log.debug("Exiting AuthorizedUserInfo.findUserByUserRefId model=" + StringUtil.isNull(model));
        return model;
    } // end findUserByUserRefId

    /**
     * This method is used to Update an existing Authorized User record.
     *
     * @param authorizedUserModel
     *        the record to update.
     * @throws BaseException
     *         if an exception occurred
     */
    public void updateUser(AuthorizedUserModel authorizedUserModel) throws BaseException {
        log.debug("Entering AuthorizedUserInfo.updateUser(). Incoming parameter is: " + String.valueOf(authorizedUserModel));
        log.debug("This method is used to Update an existing Authorized User record.");
        AuthorizedUserEntity entity = ObjectConvertor.toAuthorizedUserPersist(authorizedUserModel);
        // Set the current timestamp to the updateTs before merging.
        entity.getCommon().setUpdateTime(AppUtil.getSQLTimestamp());
        try{
            getBean().merge(entity);
        }catch(Exception e){
            log.error("An exception occurred while updating an authorized user entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Exiting AuthorizedUserInfo.updateUser.");
    } // end updateUser

    /**
     * This method is used to delete an Authorized user record.
     *
     * @param userRefId
     *        the refId of the user to delete.
     * @param updateUserRefId
     *        the refId of user who deleted record
     * @throws BaseException
     *         if an exception occurred
     */
    public void deleteUser(Long userRefId, Long updateUserRefId) throws BaseException {
        log.debug("Entering AuthorizedUserInfo.deleteUser(). Incoming parameters are: userRefId=" + String.valueOf(userRefId) + ", updateUserRefId" + String.valueOf(updateUserRefId));
        log.debug("This method is used to delete an Authorized user record.");
        try{
            getBean().deleteAuthorizedUser(userRefId, updateUserRefId);
        }catch(Exception e){
            log.error("An exception occurred while deleting an authorized user entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Exiting AuthorizedUserInfo.deleteUser.");
    } // end deleteUser

    /**
     * This method is used to load a CodeModel list of email Ind values.
     *
     * @return List<CodeModel>
     */
    public List<CodeModel> getEmailIndList() {
        log.debug("Entering AuthorizedUserInfo.getEmailIndList().");
        log.debug("This method is used to load a CodeModel list of email Ind values.");
        List<CodeModel> emailIndList = new ArrayList<CodeModel>();
        emailIndList.add(new CodeModel(EMPTY_STRING, SHAMEN_DEFAULT_LABEL));
        emailIndList.add(new CodeModel(DEFAULTNODESC, DEFAULTNODESC));
        emailIndList.add(new CodeModel(DEFAULTYESDESC, DEFAULTYESDESC));
        log.debug("Exiting AuthorizedUserInfo.getEmailIndList.");
        return emailIndList;
    }// end getEmailIndList

    /**
     * This method is used to load a CodeModel list of authority values.
     *
     * @return List<CodeModel>
     * @throws Exception
     *         if an exception occured
     */
    public List<CodeModel> getAuthorityList() throws Exception {
        log.debug("Entering AuthorizedUserInfo.getAuthorityList().");
        log.debug("This method is used to load a CodeModel list of authority values.");
        List<CodeModel> authorityList = new ArrayList<CodeModel>();
        authorityList.add(new CodeModel(EMPTY_STRING, SHAMEN_DEFAULT_LABEL));
        authorityList.add(new CodeModel("User", "User"));
        authorityList.add(new CodeModel("Administrator", "Administrator"));
        log.debug("Exiting AuthorizedUserInfo.getAuthorityList().");
        return authorityList;
    }// end getAuthorityList

    /**
     * This method is used to replace the use of a compiler-generated <code>readResolve()</code> method which is called from the <code>readObject()</code> method within the serialization machinery.
     * <p>
     * The <code>readObject()</code> method will create a new instance of the serialized class and call the <code>readResolve()</code> thereafter. By providing this method, the singleton pattern will not be violated with an additional instance of the class since this method returns the singleton instance.
     *
     * @return The singleton instance of this class
     * @throws ObjectStreamException
     *         An exception if the serialization machinery fails on this method.
     */
    private Object readResolve() throws ObjectStreamException {
        return INSTANCE;
    }// end readResolve
} // end class
