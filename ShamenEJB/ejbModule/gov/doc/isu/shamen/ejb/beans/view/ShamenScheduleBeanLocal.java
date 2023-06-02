package gov.doc.isu.shamen.ejb.beans.view;

import java.io.Serializable;

import javax.ejb.Local;

/***
 * This is the local business interface for the ShamenScheduleBean.
 *
 * @author Shane Duncan JCCC
 */
@Local
public interface ShamenScheduleBeanLocal extends Serializable {

    /**
     * This is scheduled to insure fresh application connection statuses.
     */
    public void resetAllApplicationStatuses();

    /**
     * This method used to establish all controller statuses.
     */
    public void establishAllControllerStatuses();

    /**
     * This method used to establish a single controller status.
     *
     * @param refId
     *        the ref id of the controller
     */
    public void establishSingleControllerStatus(Long refId);

    /**
     * This method used to delete all past run status records.
     */
    public void deletePastRunStatuses();
}
