package gov.doc.isu.shamen.jms.beans.view;

import java.io.Serializable;

import javax.ejb.Remote;

/**
 * This is the local business interface for the {@link gov.doc.isu.shamen.jms.beans.JmsManagerBean}.
 * 
 * @author Steven Skinner JCCC
 */
@Remote
public interface JmsManagerBeanRemote extends Serializable {

    /**
     * This method will start a batch application from remote ejb call.
     *
     * @param batchRefId
     *        (required) This holds the batch ref id.
     * @param userID
     *        the users id
     * @param jobParameters
     *        job parameters if passed in
     * @author <strong>Steven Skinner</strong> JCCC, Oct 10, 2019
     * @throws Exception
     *         if an exception occurred
     */
    public void startBatchApp(Long batchRefId, String userID, String jobParameters) throws Exception;

    /**
     * This method is to verify that a remote EJB call is available.
     * 
     * @return Boolean
     * @throws Exception
     *         if an exception occurred
     */
    public Boolean testEJB() throws Exception;

}
