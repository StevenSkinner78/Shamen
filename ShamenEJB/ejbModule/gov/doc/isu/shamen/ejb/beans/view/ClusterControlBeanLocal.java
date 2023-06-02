/**
 * 
 */
package gov.doc.isu.shamen.ejb.beans.view;

import java.io.Serializable;

import javax.ejb.Local;

import gov.doc.isu.shamen.jpa.entity.ClusterControlEntity;

/**
 * This is the local business interface for the {@link ClusterControlEntity}.
 * 
 * @author Shane Duncan JCCC
 */
@Local
public interface ClusterControlBeanLocal extends Serializable {

    /**
     * This method is used to return an instance of an object for viewing or for update.
     * 
     * @param controllerRefId
     *        The primary key of the object to retrieve.
     * @return An {@link ClusterControlEntity}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public ClusterControlEntity findControllerByRefId(Long controllerRefId) throws Exception;

    /**
     * This method is used to update an instance of an object with the database.
     * 
     * @param entity
     *        The object to update.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public void update(ClusterControlEntity entity) throws Exception;

    /**
     * This method is used to create an instance of an object with the database.
     * 
     * @param entity
     *        The object to create.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public void create(ClusterControlEntity entity) throws Exception;

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

}// end interface
