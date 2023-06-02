/**
 * 
 */
package gov.doc.isu.shamen.ejb.beans;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;

import gov.doc.isu.shamen.ejb.beans.view.CodeBeanLocal;
import gov.doc.isu.shamen.jpa.entity.ApplicationStatusCodeEntity;
import gov.doc.isu.shamen.jpa.entity.ApplicationTypeCodeEntity;
import gov.doc.isu.shamen.jpa.entity.BatchTypeCodeEntity;
import gov.doc.isu.shamen.jpa.entity.FrequencyCodeEntity;
import gov.doc.isu.shamen.jpa.entity.FrequencyTypeCodeEntity;
import gov.doc.isu.shamen.jpa.entity.RepeatCodeEntity;
import gov.doc.isu.shamen.jpa.entity.ResultCodeEntity;
import gov.doc.isu.shamen.jpa.entity.StatusCodeEntity;

/**
 * This class is used to access the database via an entity manager to perform CRUD operations with the code tables.
 * 
 * @author Steven Skinner JCCC
 */
@Stateless
@Local(CodeBeanLocal.class)
public class CodeBean implements CodeBeanLocal {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Logger logger = Logger.getLogger("gov.doc.isu.shamen.ejb.beans.CodeBean");
    @PersistenceContext(unitName = "ShamenJPA")
    private EntityManager em;

    /**
     * This default constructor must be used to instantiate this class.
     */
    public CodeBean() {
        super();
    }// end constructor

    /**
     * This method is called after the bean has been constructed.
     */
    @PostConstruct
    private void initialize() {
        logger.debug("Entering initialize. This method is called after the bean has been constructed.");
        if(em == null){
            logger.error("Entity manager has not been created in the post construct method. Check the server logs for deployment errors.");
        }// end if
        logger.debug("Exiting initialize");
    }// end initialize

    /**
     * {@inheritDoc}
     */
    @Override
    public BatchTypeCodeEntity findBatchTypeEntityeByCode(String code) throws Exception {
        logger.debug("Entering findBatchTypeEntityeByCode. This method is used to return an instance of an object for viewing or for update. Incoming parameter is: " + String.valueOf(code));
        BatchTypeCodeEntity result = em.find(BatchTypeCodeEntity.class, code);
        logger.debug("Exiting findBatchTypeEntityeByCode. Return value is: " + String.valueOf(result));
        return result;
    }// end findBatchTypeEntityeByCode

    /**
     * {@inheritDoc}
     */
    @Override
    public FrequencyTypeCodeEntity findFrequencyTypeEntityByCode(String code) throws Exception {
        logger.debug("Entering findFrequencyTypeEntityByCode. This method is used to return an instance of an object for viewing or for update. Incoming parameter is: " + String.valueOf(code));
        FrequencyTypeCodeEntity result = em.find(FrequencyTypeCodeEntity.class, code);
        logger.debug("Exiting findFrequencyTypeEntityByCode. Return value is: " + String.valueOf(result));
        return result;
    }// end findFrequencyTypeEntityByCode

    /**
     * {@inheritDoc}
     */
    @Override
    public FrequencyCodeEntity findFrequencyCodeEntityByCode(String code) throws Exception {
        logger.debug("Entering findFrequencyCodeEntityByCode. This method is used to return an instance of an object for viewing or for update. Incoming parameter is: " + String.valueOf(code));
        FrequencyCodeEntity result = em.find(FrequencyCodeEntity.class, code);
        logger.debug("Exiting findFrequencyCodeEntityByCode. Return value is: " + String.valueOf(result));
        return result;
    }// end findFrequencyCodeEntityByCode

    /**
     * {@inheritDoc}
     */
    @Override
    public RepeatCodeEntity findRepeatCodeEntityByCode(String code) throws Exception {
        logger.debug("Entering findRepeatCodeEntityByCode. This method is used to return an instance of an object for viewing or for update. Incoming parameter is: " + String.valueOf(code));
        RepeatCodeEntity result = em.find(RepeatCodeEntity.class, code);
        logger.debug("Exiting findRepeatCodeEntityByCode. Return value is: " + String.valueOf(result));
        return result;
    }// end findRepeatCodeEntityByCode

    /**
     * {@inheritDoc}
     */
    @Override
    public ResultCodeEntity findResultCodeEntityByCode(String code) throws Exception {
        logger.debug("Entering findResultCodeEntityByCode. This method is used to return an instance of an object for viewing or for update. Incoming parameter is: " + String.valueOf(code));
        ResultCodeEntity result = em.find(ResultCodeEntity.class, code);
        logger.debug("Exiting findResultCodeEntityByCode. Return value is: " + String.valueOf(result));
        return result;
    }// end findResultCodeEntityByCode

    /**
     * {@inheritDoc}
     */
    @Override
    public StatusCodeEntity findStatusCodeEntityByCode(String code) throws Exception {
        logger.debug("Entering findStatusCodeEntityByCode. This method is used to return an instance of an object for viewing or for update. Incoming parameter is: " + String.valueOf(code));
        StatusCodeEntity result = em.find(StatusCodeEntity.class, code);
        logger.debug("Exiting findStatusCodeEntityByCode. Return value is: " + String.valueOf(result));
        return result;
    }// end findStatusCodeEntityByCode

    /**
     * {@inheritDoc}
     */
    @Override
    public ApplicationTypeCodeEntity findApplicationTypeCodeEntityByCode(String code) throws Exception {
        logger.debug("Entering findApplicationTypeCodeEntityByCode. This method is used to return an instance of an object for viewing or for update. Incoming parameter is: " + String.valueOf(code));
        ApplicationTypeCodeEntity result = em.find(ApplicationTypeCodeEntity.class, code);
        logger.debug("Exiting findApplicationTypeCodeEntityByCode. Return value is: " + String.valueOf(result));
        return result;
    }// end findApplicationTypeCodeEntityByCode

    /**
     * {@inheritDoc}
     */
    @Override
    public ApplicationStatusCodeEntity findApplicationStatusCodeEntityByCode(String code) throws Exception {
        logger.debug("Entering findApplicationStatusCodeEntityByCode. This method is used to return an instance of an object for viewing or for update. Incoming parameter is: " + String.valueOf(code));
        ApplicationStatusCodeEntity result = em.find(ApplicationStatusCodeEntity.class, code);
        logger.debug("Exiting findApplicationStatusCodeEntityByCode. Return value is: " + String.valueOf(result));
        return result;
    }// end findApplicationStatusCodeEntityByCode

    /**
     * {@inheritDoc}
     */
    @Override
    public String findBatchTypeCodeDescByCode(String code) throws Exception {
        logger.debug("Entering findBatchTypeCodeDescByCode. This method is used to return an description of an object's code for viewing. Incoming parameter is: " + String.valueOf(code));
        String result = em.find(BatchTypeCodeEntity.class, code).getBatchTypeDesc();
        logger.debug("Exiting findBatchTypeCodeDescByCode. Return value is: " + String.valueOf(result));
        return result;
    }// end findBatchTypeCodeDescByCode

    /**
     * {@inheritDoc}
     */
    @Override
    public String findFrequencyTypeCodeDescByCode(String code) throws Exception {
        logger.debug("Entering findFrequencyTypeCodeDescByCode. This method is used to return an description of an object's code for viewing. Incoming parameter is: " + String.valueOf(code));
        String result = em.find(FrequencyTypeCodeEntity.class, code).getFrequencyTypeDesc();
        logger.debug("Exiting findFrequencyTypeCodeDescByCode. Return value is: " + String.valueOf(result));
        return result;
    }// end findBatchTypeCodeDescByCode

    /**
     * {@inheritDoc}
     */
    @Override
    public String findFrequencyCodeDescByCode(String code) throws Exception {
        logger.debug("Entering findFrequencyCodeDescByCode. This method is used to return an description of an object's code for viewing. Incoming parameter is: " + String.valueOf(code));
        String result = em.find(FrequencyCodeEntity.class, code).getFrequencyDesc();
        logger.debug("Exiting findFrequencyCodeDescByCode. Return value is: " + String.valueOf(result));
        return result;
    }// end findFrequencyCodeDescByCode

    /**
     * {@inheritDoc}
     */
    @Override
    public String findRepeatCodeDescByCode(String code) throws Exception {
        logger.debug("Entering findRepeatCodeDescByCode. This method is used to return an description of an object's code for viewing. Incoming parameter is: " + String.valueOf(code));
        String result = em.find(RepeatCodeEntity.class, code).getRepeatDesc();
        logger.debug("Exiting findRepeatCodeDescByCode. Return value is: " + String.valueOf(result));
        return result;
    }// end findRepeatCodeByCode

    /**
     * {@inheritDoc}
     */
    @Override
    public String findResultCodeDescByCode(String code) throws Exception {
        logger.debug("Entering findResultCodeDescByCode. This method is used to return an description of an object's code for viewing. Incoming parameter is: " + String.valueOf(code));
        String result = em.find(ResultCodeEntity.class, code).getResultDesc();
        logger.debug("Exiting findResultCodeDescByCode. Return value is: " + String.valueOf(result));
        return result;
    }// end findResultCodeDescByCode

    /**
     * {@inheritDoc}
     */
    @Override
    public String findStatusCodeDescByCode(String code) throws Exception {
        logger.debug("Entering findResultCodeDescByCode. This method is used to return an description of an object's code for viewing. Incoming parameter is: " + String.valueOf(code));
        String result = em.find(StatusCodeEntity.class, code).getStatusDesc();
        logger.debug("Exiting findStatusCodeDescByCode. Return value is: " + String.valueOf(result));
        return result;
    }// end findStatusCodeDescByCode

    /**
     * {@inheritDoc}
     */
    @Override
    public String findApplicationTypeCodeDescByCode(String code) throws Exception {
        logger.debug("Entering findApplicationTypeCodeDescByCode. This method is used to return an description of an object's code for viewing. Incoming parameter is: " + String.valueOf(code));
        String result = em.find(ApplicationTypeCodeEntity.class, code).getApplicationTypeDesc();
        logger.debug("Exiting findApplicationTypeCodeDescByCode. Return value is: " + String.valueOf(result));
        return result;
    }// end findApplicationTypeCodeDescByCode

    /**
     * {@inheritDoc}
     */
    @Override
    public String findApplicationStatusCodeDescByCode(String code) throws Exception {
        logger.debug("Entering findApplicationStatusCodeDescByCode. This method is used to return an description of an object's code for viewing. Incoming parameter is: " + String.valueOf(code));
        String result = em.find(ApplicationStatusCodeEntity.class, code).getApplicationStatusDesc();
        logger.debug("Exiting findApplicationStatusCodeDescByCode. Return value is: " + String.valueOf(result));
        return result;
    }// end findApplicationStatusCodeDescByCode

    /**
     * {@inheritDoc}
     */
    @Override
    public List<BatchTypeCodeEntity> getBatchTypeCodeList() throws Exception {
        logger.debug("Entering getBatchTypeCodeList. This method is used to return a list of BatchTypeCodeEntity that code is not equal to 'COL'.");
        List<BatchTypeCodeEntity> list = em.createNamedQuery("BatchTypeCodeEntity.FIND_ALL_ACTIVE", BatchTypeCodeEntity.class).setParameter("code", "COL").setParameter("currentDate", new java.sql.Date(System.currentTimeMillis())).getResultList();
        logger.debug("Exiting getBatchTypeCodeList. Return value is: " + String.valueOf(list));
        return list;
    }// end getBatchTypeCodeList

    /**
     * {@inheritDoc}
     */
    @Override
    public List<BatchTypeCodeEntity> getBatchTypeCodeListWithCol() throws Exception {
        logger.debug("Entering getBatchTypeCodeListWithCol. This method is used to return a list of all active BatchTypeCodeEntity.");
        List<BatchTypeCodeEntity> list = em.createNamedQuery("BatchTypeCodeEntity.FIND_ALL_ACTIVE_WITH_COL", BatchTypeCodeEntity.class).setParameter("currentDate", new java.sql.Date(System.currentTimeMillis())).getResultList();
        logger.debug("Exiting getBatchTypeCodeListWithCol. Return value is: " + String.valueOf(list));
        return list;
    }// end getBatchTypeCodeList

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FrequencyCodeEntity> getFrequencyCodeList() throws Exception {
        logger.debug("Entering getFrequencyCodeList. This method is used to return a list of FrequencyCodeEntity.");
        List<FrequencyCodeEntity> list = em.createNamedQuery("FrequencyCodeEntity.FIND_ALL_ACTIVE", FrequencyCodeEntity.class).setParameter("currentDate", new java.sql.Date(System.currentTimeMillis())).getResultList();
        logger.debug("Exiting getFrequencyCodeList. Return value is: " + String.valueOf(list));
        return list;
    }// end getFrequencyCodeList

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FrequencyTypeCodeEntity> getFrequencyTypeCodeList() throws Exception {
        logger.debug("Entering getFrequencyTypeCodeList. This method is used to return a list of FrequencyTypeCodeEntity.");
        List<FrequencyTypeCodeEntity> list = em.createNamedQuery("FrequencyTypeCodeEntity.FIND_ALL_ACTIVE", FrequencyTypeCodeEntity.class).setParameter("currentDate", new java.sql.Date(System.currentTimeMillis())).getResultList();
        logger.debug("Exiting getFrequencyTypeCodeList. Return value is: " + String.valueOf(list));
        return list;
    }// end getFrequencyTypeCodeList

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RepeatCodeEntity> getRepeatCodeList() throws Exception {
        logger.debug("Entering getRepeatCodeList. This method is used to return a list of RepeatCodeEntity.");
        List<RepeatCodeEntity> list = em.createNamedQuery("RepeatCodeEntity.FIND_ALL_ACTIVE", RepeatCodeEntity.class).setParameter("currentDate", new java.sql.Date(System.currentTimeMillis())).getResultList();
        logger.debug("Exiting getRepeatCodeList. Return value is: " + String.valueOf(list));
        return list;
    }// end getRepeatCodeList

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ResultCodeEntity> getResultCodeList() throws Exception {
        logger.debug("Entering getResultCodeList. This method is used to return a list of ResultCodeEntity.");
        List<ResultCodeEntity> list = em.createNamedQuery("ResultCodeEntity.FIND_ALL_ACTIVE", ResultCodeEntity.class).setParameter("currentDate", new java.sql.Date(System.currentTimeMillis())).getResultList();
        logger.debug("Exiting getResultCodeList. Return value is: " + String.valueOf(list));
        return list;
    }// end getResultCodeList

    /**
     * {@inheritDoc}
     */
    @Override
    public List<StatusCodeEntity> getStatusCodeList() throws Exception {
        logger.debug("Entering getStatusCodeList. This method is used to return a list of StatusCodeEntity.");
        List<StatusCodeEntity> list = em.createNamedQuery("StatusCodeEntity.FIND_ALL_ACTIVE", StatusCodeEntity.class).setParameter("currentDate", new java.sql.Date(System.currentTimeMillis())).getResultList();
        logger.debug("Exiting getStatusCodeList. Return value is: " + String.valueOf(list));
        return list;
    }// end getStatusCodeList

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ApplicationTypeCodeEntity> getApplicationTypeCodeList() throws Exception {
        logger.debug("Entering getApplicationTypeCodeList. This method is used to return a list of ApplicationTypeCodeEntity.");
        List<ApplicationTypeCodeEntity> list = em.createNamedQuery("ApplicationTypeCodeEntity.FIND_ALL_ACTIVE", ApplicationTypeCodeEntity.class).setParameter("currentDate", new java.sql.Date(System.currentTimeMillis())).getResultList();
        logger.debug("Exiting getApplicationTypeCodeList. Return value is: " + String.valueOf(list));
        return list;
    }// end getApplicationTypeCodeList

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ApplicationStatusCodeEntity> getApplicationStatusCodeList() throws Exception {
        logger.debug("Entering getApplicationStatusCodeList. This method is used to return a list of ApplicationStatusCodeEntity.");
        List<ApplicationStatusCodeEntity> list = em.createNamedQuery("ApplicationStatusCodeEntity.FIND_ALL_ACTIVE", ApplicationStatusCodeEntity.class).setParameter("currentDate", new java.sql.Date(System.currentTimeMillis())).getResultList();
        logger.debug("Exiting getApplicationStatusCodeList. Return value is: " + String.valueOf(list));
        return list;
    }// end getApplicationStatusCodeList

}// end class
