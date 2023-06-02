package gov.doc.isu.shamen.ejb.beans.view;

import java.io.Serializable;

import javax.ejb.Local;

import gov.doc.isu.shamen.jpa.entity.ControllerEntity;

/***
 * This is the local business interface for the ControllerStatusUpdaterBean.
 *
 * @author Shane Duncan JCCC
 */
@Local
public interface ControllerStatusUpdaterBeanLocal extends Serializable {
    public static final String UNRESPONSIVE = "UNR";
    public static final String CONNECTED = "CON";
    public static final String AWAITING_RESPONSE = "AWR";

    /**
     * This method establishes a Controller status
     *
     * @param controller
     *        ControllerEntity
     */
    public void establishControllerStatus(ControllerEntity controller);

    /**
     * This method sets the the status for a single controller and loads it into the controller map.
     *
     * @param controller
     *        controller
     * @param status
     *        status
     */
    public void setSingleControllerStatus(ControllerEntity controller, String status);
}
