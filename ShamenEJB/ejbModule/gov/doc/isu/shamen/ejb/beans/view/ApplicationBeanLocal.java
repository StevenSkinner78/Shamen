/**
 *
 */
package gov.doc.isu.shamen.ejb.beans.view;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Local;

import gov.doc.isu.shamen.jpa.entity.ApplicationEntity;
import gov.doc.isu.shamen.jpa.entity.ApplicationInstanceEntity;
import gov.doc.isu.shamen.jpa.entity.ApplicationStatusCodeEntity;

/**
 * This is the local business interface for the {@link ApplicationEntity}.
 *
 * @author Steven Skinner JCCC
 */
@Local
public interface ApplicationBeanLocal extends Serializable {

    /**
     * This method is used to return a list of {@link ApplicationEntity}s.
     *
     * @return A {@link List} of {@link ApplicationEntity}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public List<ApplicationEntity> getApplicationList() throws Exception;

    /**
     * This method is used to return a list of {@link Object}s with application name and address.
     *
     * @return A {@link List} of {@link Object}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public List<Object> getApplicationListForCode() throws Exception;

    /**
     * This method is used to return a list of {@link Object}s with application name and address.
     *
     * @param systemRefId
     *        the system ref id
     * @return A {@link List} of {@link Object}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public List<Object> getApplicationListBySystemForCode(Long systemRefId) throws Exception;

    /**
     * This method is used to return an instance of an object for viewing or for update.
     *
     * @param applicationRefId
     *        The primary key of the object to retrieve.
     * @return An {@link ApplicationEntity}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public ApplicationEntity findApplicationByRefId(Long applicationRefId) throws Exception;

    /**
     * This method is used to return an instance of an object for viewing or for update.
     *
     * @param applicationInstanceRefId
     *        The primary key of the object to retrieve.
     * @return ApplicationInstanceEntity
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public ApplicationInstanceEntity findApplicationInstanceByRefId(Long applicationInstanceRefId) throws Exception;

    /**
     * This method is used to return an instance of an object for viewing or for update.
     *
     * @param applicationName
     *        The name of the object to retrieve.
     * @param applicationAddress
     *        The address or environment of the object to retrieve.
     * @return An {@link ApplicationEntity}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public ApplicationEntity findByNameAndAddress(String applicationName, String applicationAddress) throws Exception;

    /**
     * This method is used to return an instance of an object for viewing or for update.
     *
     * @param applicationName
     *        The name of the object to retrieve.
     * @param applicationAddress
     *        The address or environment of the object to retrieve.
     * @param instanceName
     *        The name of the application instance
     * @return An {@link ApplicationInstanceEntity}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public ApplicationInstanceEntity findInstanceByApplicationAddressName(String applicationName, String applicationAddress, String instanceName) throws Exception;

    /**
     * This method is used to update an instance of an object with the database.
     *
     * @param entity
     *        The object to update.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public void update(ApplicationEntity entity) throws Exception;

    /**
     * This method is used to update an instance of an object with the database.
     *
     * @param entity
     *        The object to update.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public void update(ApplicationInstanceEntity entity) throws Exception;

    /**
     * This method is used to create an instance of an object with the database.
     *
     * @param entity
     *        The object to create.
     * @return An {@link ApplicationEntity}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public ApplicationEntity create(ApplicationEntity entity) throws Exception;

    /**
     * This method is used to create an instance of an object with the database.
     *
     * @param entity
     *        The object to create. @return An {@link ApplicationInstanceEntity}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public ApplicationInstanceEntity create(ApplicationInstanceEntity entity) throws Exception;

    /**
     * This method is used to 'soft' delete an instance of an object in the database.
     *
     * @param applicationRefId
     *        The primary key of the record to delete.
     * @param updateUserRefId
     *        The primary key of the system user enacting the update.
     * @return Long
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public ApplicationEntity deleteApplication(Long applicationRefId, Long updateUserRefId) throws Exception;

    /**
     * This method is used to 'soft' delete an instance of an object in the database.
     *
     * @param applicationInstanceRefId
     *        The primary key of the record to delete.
     * @param updateUserRefId
     *        The primary key of the system user enacting the update.
     * @return ApplicationInstanceEntity
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public ApplicationInstanceEntity deleteApplicationInstance(Long applicationInstanceRefId, Long updateUserRefId) throws Exception;

    /**
     * Used to update all applicationEntities by setting stausInd to passed in status.
     *
     * @param statusInd
     *        the status indicator value
     * @param userRefId
     *        the refId of user updating records
     * @throws Exception
     *         if an exception occurred
     */
    public void updateAllApps(ApplicationStatusCodeEntity statusInd, Long userRefId) throws Exception;

    /**
     * Used to delete all application instances for a given application
     *
     * @param application
     *        The application that should have its stale instances deleted.
     * @throws Exception
     *         if an exception occurred
     */
    public void deleteOldApplicationInstances(ApplicationEntity application) throws Exception;

    /**
     * Used to delete all application instances for a all applications
     *
     * @throws Exception
     *         if an exception occurred
     */
    public void deleteOldApplicationInstancesAll() throws Exception;

    /**
     * This method gets the application instance for a given application by node.
     *
     * @param application
     *        The application entity
     * @param node
     *        the node of the server
     * @return primary applicationInstanceEntity
     * @throws Exception
     *         if an exception occurred
     */
    public ApplicationInstanceEntity getApplicationInstance(ApplicationEntity application, String node) throws Exception;

}// end interface
