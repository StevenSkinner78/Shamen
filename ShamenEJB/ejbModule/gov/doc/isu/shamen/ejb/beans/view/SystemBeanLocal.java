/**
 * @author sbd000is
 */
package gov.doc.isu.shamen.ejb.beans.view;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Local;

import gov.doc.isu.shamen.jpa.entity.SystemEntity;

/**
 * This is the local business interface for the {@link SystemEntity}.
 *
 * @author Shane Duncan JCCC
 */
@Local
public interface SystemBeanLocal extends Serializable {

    /**
     * This method is used to return a list of {@link SystemEntity}s.
     *
     * @return A {@link List} of {@link SystemEntity}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public List<Object> getSystemList() throws Exception;

    /**
     * This method is used to return an instance of an object for viewing or for update.
     *
     * @param systemRefId
     *        The primary key of the object to retrieve.
     * @return An {@link SytemEntity}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public SystemEntity findSystemByRefId(Long systemRefId) throws Exception;

    /**
     * This method is used to create an instance of an object with the database.
     *
     * @param entity
     *        The object to create.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public void create(SystemEntity entity) throws Exception;

    /**
     * This method is used to update an instance of an object with the database.
     *
     * @param entity
     *        The object to update.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public void update(SystemEntity entity) throws Exception;

    /**
     * This method is used to 'soft' delete an instance of an object in the database.
     *
     * @param systemRefId
     *        The primary key of the record to delete.
     * @param updateUserRefId
     *        The primary key of the system user enacting the update.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public void deleteSystem(Long systemRefId, Long updateUserRefId) throws Exception;

    /**
     * This method returns a fully loaded system list with all its children
     *
     * @return List<SystemEntity>
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public List<SystemEntity> getSystemListWithChildren() throws Exception;
}// end interface
