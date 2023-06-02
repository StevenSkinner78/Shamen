/**
 * 
 */
package gov.doc.isu.shamen.ejb.beans.view;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Local;

import gov.doc.isu.shamen.jpa.entity.ApplicationStatusCodeEntity;
import gov.doc.isu.shamen.jpa.entity.ApplicationTypeCodeEntity;
import gov.doc.isu.shamen.jpa.entity.BatchTypeCodeEntity;
import gov.doc.isu.shamen.jpa.entity.FrequencyCodeEntity;
import gov.doc.isu.shamen.jpa.entity.FrequencyTypeCodeEntity;
import gov.doc.isu.shamen.jpa.entity.RepeatCodeEntity;
import gov.doc.isu.shamen.jpa.entity.ResultCodeEntity;
import gov.doc.isu.shamen.jpa.entity.StatusCodeEntity;

/**
 * This is the local business interface for the {@link gov.doc.isu.shamen.ejb.beans.CodeBean}.
 * 
 * @author Steven Skinner JCCC
 */
@Local
public interface CodeBeanLocal extends Serializable {

    /**
     * This method is used to return an instance of an object for viewing or for update.
     * 
     * @param code
     *        The primary key of the object to retrieve.
     * @return An {@link BatchTypeCodeEntity}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public BatchTypeCodeEntity findBatchTypeEntityeByCode(String code) throws Exception;

    /**
     * This method is used to return an instance of an object for viewing or for update.
     * 
     * @param code
     *        The primary key of the object to retrieve.
     * @return An {@link FrequencyTypeCodeEntity}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public FrequencyTypeCodeEntity findFrequencyTypeEntityByCode(String code) throws Exception;

    /**
     * This method is used to return an instance of an object for viewing or for update.
     * 
     * @param code
     *        The primary key of the object to retrieve.
     * @return An {@link FrequencyCodeEntity}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public FrequencyCodeEntity findFrequencyCodeEntityByCode(String code) throws Exception;

    /**
     * This method is used to return an instance of an object for viewing or for update.
     * 
     * @param code
     *        The primary key of the object to retrieve.
     * @return An {@link RepeatCodeEntity}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public RepeatCodeEntity findRepeatCodeEntityByCode(String code) throws Exception;

    /**
     * This method is used to return an instance of an object for viewing or for update.
     * 
     * @param code
     *        The primary key of the object to retrieve.
     * @return An {@link ResultCodeEntity}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public ResultCodeEntity findResultCodeEntityByCode(String code) throws Exception;

    /**
     * This method is used to return an instance of an object for viewing or for update.
     * 
     * @param code
     *        The primary key of the object to retrieve.
     * @return An {@link StatusCodeEntity}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public StatusCodeEntity findStatusCodeEntityByCode(String code) throws Exception;

    /**
     * This method is used to return an instance of an object for viewing or for update.
     * 
     * @param code
     *        The primary key of the object to retrieve.
     * @return An {@link ApplicationTypeCodeEntity}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public ApplicationTypeCodeEntity findApplicationTypeCodeEntityByCode(String code) throws Exception;

    /**
     * This method is used to return an instance of an object for viewing or for update.
     * 
     * @param code
     *        The primary key of the object to retrieve.
     * @return An {@link ApplicationStatusCodeEntity}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public ApplicationStatusCodeEntity findApplicationStatusCodeEntityByCode(String code) throws Exception;

    /**
     * This method is used to return a list of {@link BatchTypeCodeEntity}s.
     * 
     * @return A {@link List} of {@link BatchTypeCodeEntity}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public List<BatchTypeCodeEntity> getBatchTypeCodeList() throws Exception;

    /**
     * This method is used to return a list of {@link BatchTypeCodeEntity}s.
     * 
     * @return A {@link List} of {@link BatchTypeCodeEntity}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public List<BatchTypeCodeEntity> getBatchTypeCodeListWithCol() throws Exception;

    /**
     * This method is used to return a list of {@link FrequencyTypeCodeEntity}s.
     * 
     * @return A {@link List} of {@link FrequencyTypeCodeEntity}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public List<FrequencyTypeCodeEntity> getFrequencyTypeCodeList() throws Exception;

    /**
     * This method is used to return a list of {@link FrequencyCodeEntity}s.
     * 
     * @return A {@link List} of {@link FrequencyCodeEntity}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public List<FrequencyCodeEntity> getFrequencyCodeList() throws Exception;

    /**
     * This method is used to return a list of {@link RepeatCodeEntity}s.
     * 
     * @return A {@link List} of {@link RepeatCodeEntity}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public List<RepeatCodeEntity> getRepeatCodeList() throws Exception;

    /**
     * This method is used to return a list of {@link ResultCodeEntity}s.
     * 
     * @return A {@link List} of {@link ResultCodeEntity}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public List<ResultCodeEntity> getResultCodeList() throws Exception;

    /**
     * This method is used to return a list of {@link StatusCodeEntity}s.
     * 
     * @return A {@link List} of {@link StatusCodeEntity}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public List<StatusCodeEntity> getStatusCodeList() throws Exception;

    /**
     * This method is used to return a list of {@link ApplicationTypeCodeEntity}s.
     * 
     * @return A {@link List} of {@link ApplicationTypeCodeEntity}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public List<ApplicationTypeCodeEntity> getApplicationTypeCodeList() throws Exception;

    /**
     * This method is used to return a list of {@link ApplicationStatusCodeEntity}s.
     * 
     * @return A {@link List} of {@link ApplicationStatusCodeEntity}.
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public List<ApplicationStatusCodeEntity> getApplicationStatusCodeList() throws Exception;

    /**
     * This method is used to return the description for a code.
     * 
     * @param code
     *        The primary key of the object to retrieve.
     * @return description for code
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public String findBatchTypeCodeDescByCode(String code) throws Exception;

    /**
     * This method is used to return the description for a code.
     * 
     * @param code
     *        The primary key of the object to retrieve.
     * @return description for code
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public String findFrequencyTypeCodeDescByCode(String code) throws Exception;

    /**
     * This method is used to return the description for a code.
     * 
     * @param code
     *        The primary key of the object to retrieve.
     * @return description for code
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public String findFrequencyCodeDescByCode(String code) throws Exception;

    /**
     * This method is used to return the description for a code.
     * 
     * @param code
     *        The primary key of the object to retrieve.
     * @return description for code
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public String findRepeatCodeDescByCode(String code) throws Exception;

    /**
     * This method is used to return the description for a code.
     * 
     * @param code
     *        The primary key of the object to retrieve.
     * @return description for code
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public String findResultCodeDescByCode(String code) throws Exception;

    /**
     * This method is used to return the description for a code.
     * 
     * @param code
     *        The primary key of the object to retrieve.
     * @return description for code
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public String findStatusCodeDescByCode(String code) throws Exception;

    /**
     * This method is used to return the description for a code.
     * 
     * @param code
     *        The primary key of the object to retrieve.
     * @return description for code
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public String findApplicationTypeCodeDescByCode(String code) throws Exception;

    /**
     * This method is used to return the description for a code.
     * 
     * @param code
     *        The primary key of the object to retrieve.
     * @return description for code
     * @throws Exception
     *         An exception if the method logic does not complete normally.
     */
    public String findApplicationStatusCodeDescByCode(String code) throws Exception;

}// end interface
