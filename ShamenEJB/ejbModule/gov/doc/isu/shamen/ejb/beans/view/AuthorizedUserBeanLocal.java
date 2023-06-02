/**
 *
 */
package gov.doc.isu.shamen.ejb.beans.view;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Local;

import gov.doc.isu.shamen.jpa.entity.ApplicationEntity;
import gov.doc.isu.shamen.jpa.entity.AuthorizedUserEntity;
import gov.doc.isu.shamen.jpa.entity.BatchAppEntity;
import gov.doc.isu.shamen.jpa.entity.SystemEntity;

/**
 * This is the local business interface for the {@link gov.doc.isu.shamen.ejb.beans.AuthorizedUserBean}.
 *
 * @author Joseph Burris JCCC
 */
@Local
public interface AuthorizedUserBeanLocal extends Serializable {

    /**
     * This method is used to count a list of batch apps for a user.
     *
     * @param userRefId
     *        the users ref id
     * @return Integer.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public Integer countBatchAppsAndCollectionsByUser(Long userRefId) throws Exception;

    /**
     * This method is used to return a list of userIds that have a email indicator equal to 'Y'.
     *
     * @return A {@link List} of {@link AuthorizedUserEntity.userID}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public List<String> getEmailList() throws Exception;

    /**
     * This method is used to return a list of {@link AuthorizedUserEntity}s.
     *
     * @return A {@link List} of {@link AuthorizedUserEntity}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public List<AuthorizedUserEntity> getUsersList() throws Exception;

    /**
     * This method is used to return a list of objects filled with batch app info for a responsible staff user ref id.
     *
     * @param userRefId
     *        responsible staff user ref id
     * @return List<BatchAppEntity>
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public List<BatchAppEntity> getBatchListForUser(Long userRefId) throws Exception;

    /**
     * This method is used to return a list of objects filled with web app info for a responsible staff user ref id.
     *
     * @param userRefId
     *        responsible staff user ref id
     * @return List<ApplicationEntity>
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public List<ApplicationEntity> getWebAppListForUser(Long userRefId) throws Exception;

    /**
     * This method is used to return a list of objects filled with system info for a responsible staff user ref id.
     *
     * @param userRefId
     *        responsible staff user ref id
     * @return List<SystemEntity>
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public List<SystemEntity> getSystemListForUser(Long userRefId) throws Exception;

    /**
     * This method is used to return an instance of an object for viewing or for update.
     *
     * @param authorizedUserRefId
     *        The primary key of the object to retrieve.
     * @return An {@link AuthorizedUserEntity}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public AuthorizedUserEntity findAuthorizedUserByUserRefId(Long authorizedUserRefId) throws Exception;

    /**
     * This method is used to return an instance of an object for viewing or for update.
     *
     * @param userSignOn
     *        The system user sign on of the object to retrieve.
     * @return An {@link AuthorizedUserEntity}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public AuthorizedUserEntity findAuthorizedUserByUserId(String userSignOn) throws Exception;

    /**
     * This method is used to merge an instance of an object with the database.
     *
     * @param entity
     *        The object to merge.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public void merge(AuthorizedUserEntity entity) throws Exception;

    /**
     * This method is used to 'soft' delete an instance of an object in the database.
     *
     * @param userRefId
     *        The primary key of the record to delete.
     * @param updateUserRefId
     *        The primary key of the system user enacting the update.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public void deleteAuthorizedUser(Long userRefId, Long updateUserRefId) throws Exception;

}// end interface
