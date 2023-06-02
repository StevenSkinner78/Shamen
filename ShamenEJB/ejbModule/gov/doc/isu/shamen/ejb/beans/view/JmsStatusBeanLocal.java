/**
 *
 */
package gov.doc.isu.shamen.ejb.beans.view;

import java.io.Serializable;

import javax.ejb.Local;

/**
 * This is the local business interface for the {@link gov.doc.isu.shamen.ejb.beans.JmsStatusBeanLocal}.
 *
 * @author Joseph Burris JCCC
 */
@Local
public interface JmsStatusBeanLocal extends Serializable {

    /**
     * This method gets the status
     *
     * @return Boolean
     */
    public Boolean getStatus();

}// end interface
