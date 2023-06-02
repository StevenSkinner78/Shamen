/**
 *
 */
package gov.doc.isu.shamen.ejb.beans.view;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Local;

import gov.doc.isu.shamen.jpa.entity.ControllerEntity;

/**
 * This is the local business interface for the {@link ControllerEntity}.
 *
 * @author Steven Skinner JCCC
 */
@Local
public interface ControllerBeanLocal extends Serializable {

    /**
     * This method is used to return a list of {@link ControllerEntity}s.
     *
     * @return A {@link List} of {@link ControllerEntity}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public List<Object> getControllerList() throws Exception;

    /**
     * This method is used to return a list of {@link Object}s with controller name and refId.
     *
     * @return A {@link List} of {@link Object}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public List<Object> getControllerListForCode() throws Exception;

    /**
     * This method is used to load a controller entity by controller address.
     *
     * @param address
     *        the controller address
     * @return ControllerEntity
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public ControllerEntity findControllerAddress(String address) throws Exception;

    /**
     * This method is used to load a list of controllers with all their child associations
     *
     * @return list of ControllerEntity
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public List<ControllerEntity> getControllersAndChildren() throws Exception;

    /**
     * This method is used to return an instance of an object for viewing or for update.
     *
     * @param controllerRefId
     *        The primary key of the object to retrieve.
     * @return An {@link ControllerEntity}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public ControllerEntity findControllerByRefId(Long controllerRefId) throws Exception;

    /**
     * This method is used to update an instance of an object with the database.
     *
     * @param entity
     *        The object to update.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public void update(ControllerEntity entity) throws Exception;

    /**
     * This method is used to set a single controller's status to the database.
     *
     * @param controllerRefId
     *        controllerRefId
     * @param status
     *        status to set
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public void setSingleControllerStatus(Long controllerRefId, String status) throws Exception;

    /**
     * This method is used to create an instance of an object with the database.
     *
     * @param entity
     *        The object to create.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public void create(ControllerEntity entity) throws Exception;

    /**
     * This method is used to 'soft' delete an instance of an object in the database.
     *
     * @param controllerRefId
     *        The primary key of the record to delete.
     * @param updateUserRefId
     *        The primary key of the system user enacting the update.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public void deleteController(Long controllerRefId, Long updateUserRefId) throws Exception;

    /**
     * This method gets a list of controllers but with only the controllerRefId loaded. It's optimized for performance on controller status updates.
     *
     * @return List<ControllerEntity>
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public List<ControllerEntity> getControllerListForUpdater() throws Exception;

}// end interface
