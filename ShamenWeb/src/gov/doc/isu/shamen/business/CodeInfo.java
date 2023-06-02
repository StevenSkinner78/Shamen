package gov.doc.isu.shamen.business;

import static gov.doc.isu.shamen.resources.AppConstants.BLANK_CODE;
import static gov.doc.isu.shamen.resources.AppConstants.SHAMEN_DEFAULT_LABEL;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import gov.doc.isu.dwarf.exception.BaseException;
import gov.doc.isu.dwarf.model.CodeModel;
import gov.doc.isu.dwarf.resources.Constants;
import gov.doc.isu.dwarf.util.AppUtil;
import gov.doc.isu.dwarf.util.StringUtil;
import gov.doc.isu.shamen.ejb.beans.view.CodeBeanLocal;
import gov.doc.isu.shamen.jpa.entity.ApplicationStatusCodeEntity;
import gov.doc.isu.shamen.jpa.entity.ApplicationTypeCodeEntity;
import gov.doc.isu.shamen.jpa.entity.BatchTypeCodeEntity;
import gov.doc.isu.shamen.jpa.entity.FrequencyCodeEntity;
import gov.doc.isu.shamen.jpa.entity.RepeatCodeEntity;
import gov.doc.isu.shamen.jpa.entity.ResultCodeEntity;
import gov.doc.isu.shamen.jpa.entity.StatusCodeEntity;
import gov.doc.isu.shamen.models.RunStatusModel;
import gov.doc.isu.shamen.models.ScheduleModel;

/**
 * This is the processor class for Code Information.
 * <p>
 * Modifications:
 * <ul>
 * <li>Enforced singleton pattern.</li>
 * <li>Removed reflection instances for getting class name.</li>
 * </ul>
 * 
 * @author <strong>Steven L. Skinner</strong>
 */
public class CodeInfo implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static Logger log = Logger.getLogger("gov.doc.isu.shamen.business.CodeInfo");
    private static final CodeInfo INSTANCE = new CodeInfo();

    /**
     * This constructor made private to block instantiating this class.
     */
    private CodeInfo() {
        super();
    }// end constructor

    /**
     * This method returns the private static final instance variable to enforce the Singleton pattern.
     * 
     * @return An instance of this class.
     */
    public static CodeInfo getInstance() {
        log.debug("Returning the static instance of the CodeInfo class.");
        return INSTANCE;
    }// end getInstance

    /**
     * This method is called to use a JNDI lookup for accessing the {@link CodeBeanLocal}.
     * 
     * @return An instance of the <code>CodeBeanLocal</code>.
     * @throws BaseException
     *         An exception if the <code>CodeBeanLocal</code> class cannot be accessed.
     */
    public CodeBeanLocal getBean() throws BaseException {
        log.debug("Entering CodeInfo.getBean().");
        log.debug("This method is called to use a JNDI lookup for accessing the CodeBeanLocal.");
        CodeBeanLocal bean;
        try{
            Context context = new InitialContext();
            bean = (CodeBeanLocal) context.lookup("java:global/ShamenEAR/ShamenEJB/CodeBean!gov.doc.isu.shamen.ejb.beans.view.CodeBeanLocal");
        }catch(NamingException e){
            log.error("An exception occurred while attempting to access the CodeBean from the ShamenServiceBean. Message is: " + e.getMessage());
            throw new BaseException(e.getMessage());
        }// end try/catch
        log.debug("Exiting getBean. Return value is: " + String.valueOf(bean));
        return bean;
    }// end getBean

    /**
     * Loads a List of Results as CodeModel
     * 
     * @return list of results
     * @throws BaseException
     *         if an exception occurred
     */
    public List<CodeModel> getResultCodes() throws BaseException {
        log.debug("Entering getResultCodes().");
        log.debug("This method is used to load a List of Result Codes as CodeModel for display.");
        List<CodeModel> codeList = new ArrayList<CodeModel>();
        codeList.add(new CodeModel(BLANK_CODE, SHAMEN_DEFAULT_LABEL));
        List<ResultCodeEntity> entities = null;
        try{
            entities = (List<ResultCodeEntity>) getBean().getResultCodeList();
        }catch(Exception e){
            log.error("An exception occurred while retireving an result codes entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        if(null != entities && !entities.isEmpty()){
            for(int i = 1, j = entities.size();i < j;i++){
                codeList.add(new CodeModel(entities.get(i).getResultCd(), entities.get(i).getResultDesc()));
            }// end for
        }else{
            log.warn("The result codes list in the database is null or empty.");
        }// end if/else
        log.debug("Exiting getResultCodes codeList.size=" + (AppUtil.isEmpty(codeList) ? "empty" : codeList.size()));
        return codeList;
    } // end getResultCodes

    /**
     * Gets the code's description based on code passed in.
     * 
     * @param code
     *        the code of selected value
     * @return String description for code
     * @throws BaseException
     *         if an exception occurred
     */
    public String getResultDesc(String code) throws BaseException {
        log.debug("Entering CodeInfo.getResultDesc(). Incoming parameter is: code = " + String.valueOf(code));
        log.debug("This method is used to load an description for the code past in.");
        String result = "";
        try{
            result = getBean().findResultCodeDescByCode(code);
        }catch(Exception e){
            log.error("An exception occurred while retireving an result codes description. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Exiting CodeInfo.getResultDesc result=" + String.valueOf(result));
        return result;
    } // end getResultDesc

    /**
     * Loads a List of Statuses as CodesModel
     * 
     * @return list of Statuses
     * @throws BaseException
     *         if an exception occurred
     */
    public List<CodeModel> getStatusCodes() throws BaseException {
        log.debug("Entering CodeInfo.getStatusCodes");
        log.debug("This method is used to load a List of Status Codes as CodeModel for display.");
        List<CodeModel> codeList = new ArrayList<CodeModel>();
        codeList.add(new CodeModel(Constants.EMPTY_STRING, SHAMEN_DEFAULT_LABEL));
        List<StatusCodeEntity> entities = null;
        try{
            entities = (List<StatusCodeEntity>) getBean().getStatusCodeList();
        }catch(Exception e){
            log.error("An exception occurred while retireving an status codes entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        if(null != entities && !entities.isEmpty()){
            for(int i = 0, j = entities.size();i < j;i++){
                codeList.add(new CodeModel(entities.get(i).getStatusCd(), entities.get(i).getStatusDesc()));
            }// end for
        }else{
            log.warn("The status codes list in the database is null or empty.");
        }// end if/else
        log.debug("Exiting CodeInfo.getStatusCodes codeList.size=" + (AppUtil.isEmpty(codeList) ? "empty" : codeList.size()));
        return codeList;
    } // end getStatusCodes

    /**
     * Gets the code's description based on code passed in.
     * 
     * @param code
     *        the code of selected value
     * @return String description for code
     * @throws BaseException
     *         if an exception occurred
     */
    public String getStatusDesc(String code) throws BaseException {
        log.debug("Entering CodeInfo.getStatusDesc(). Incoming parameter is: code = " + String.valueOf(code));
        log.debug("This method is used to load an description for the code past in.");
        String result = "";
        try{
            result = getBean().findStatusCodeDescByCode(code);
        }catch(Exception e){
            log.error("An exception occurred while retireving an status codes description. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Exiting CodeInfo.getStatusDesc result=" + String.valueOf(result));
        return result;
    } // end getStatusDesc

    /**
     * Loads a List of Frequency as CodesModel
     * 
     * @param isListScreen
     *        boolean check for if this is a list screen. Will add default value for if this is a list screen.
     * @return list of Frequency
     * @throws BaseException
     *         if an exception occurred
     */
    public List<CodeModel> getFrequencyCodes(boolean isListScreen) throws BaseException {
        log.debug("Entering CodeInfo.getFrequencyCodes");
        log.debug("This method is used to load a List of Frequency Codes as CodeModel for display.");
        List<CodeModel> codeList = new ArrayList<CodeModel>();
        List<FrequencyCodeEntity> entities = null;
        try{
            entities = (List<FrequencyCodeEntity>) getBean().getFrequencyCodeList();
        }catch(Exception e){
            log.error("An exception occurred while retireving an frequency codes entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        if(null != entities && !entities.isEmpty()){
            if(isListScreen){
                codeList.add(new CodeModel(SHAMEN_DEFAULT_LABEL, SHAMEN_DEFAULT_LABEL));
            }// end if
            for(int i = 1, j = entities.size();i < j;i++){
                codeList.add(new CodeModel(entities.get(i).getFrequencyCd(), entities.get(i).getFrequencyDesc()));
            }// end for
        }else{
            log.warn("The frequency codes list in the database is null or empty.");
        }// end if/else
        log.debug("Exiting CodeInfo.getFrequencyCodes codeList.size=" + (AppUtil.isEmpty(codeList) ? "empty" : codeList.size()));
        return codeList;
    } // end getFrequencyCodes

    /**
     * Gets the code's description based on code passed in.
     * 
     * @param code
     *        the code of selected value
     * @return String description for code
     * @throws BaseException
     *         if an exception occurred
     */
    public String getFrequencyDesc(String code) throws BaseException {
        log.debug("Entering CodeInfo.getFrequencyDesc(). Incoming parameter is: code = " + String.valueOf(code));
        log.debug("This method is used to load an description for the code past in.");
        String result = "";
        try{
            result = getBean().findFrequencyCodeDescByCode(code);
        }catch(Exception e){
            log.error("An exception occurred while retireving an frequency codes description. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Exiting CodeInfo.getFrequencyDesc result=" + String.valueOf(result));
        return result;
    } // end getFrequencyDesc

    /**
     * Loads a List of Repeat as CodesModel
     * 
     * @return list of Frequency
     * @throws BaseException
     *         if an exception occurred
     */
    public List<CodeModel> getRepeatCodes() throws BaseException {
        log.debug("Entering CodeInfo.getRepeatCodes");
        log.debug("This method is used to load a List of Repeat Codes as CodeModel for display.");
        List<CodeModel> codeList = new ArrayList<CodeModel>();
        codeList.add(new CodeModel(BLANK_CODE, SHAMEN_DEFAULT_LABEL));
        List<RepeatCodeEntity> entities = null;
        try{
            entities = (List<RepeatCodeEntity>) getBean().getRepeatCodeList();
        }catch(Exception e){
            log.error("An exception occurred while retireving an repeat codes entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        if(null != entities && !entities.isEmpty()){
            for(int i = 1, j = entities.size();i < j;i++){
                codeList.add(new CodeModel(entities.get(i).getRepeatCd(), entities.get(i).getRepeatDesc()));
            }// end for
        }else{
            log.warn("The repeat codes list in the database is null or empty.");
        }// end if/else
        log.debug("Exiting CodeInfo.getRepeatCodes codeList.size=" + (AppUtil.isEmpty(codeList) ? "empty" : codeList.size()));
        return codeList;
    } // end getRepeatCodes

    /**
     * Gets the code's description based on code passed in.
     * 
     * @param code
     *        the code of selected value
     * @return String description for code
     * @throws BaseException
     *         if an exception occurred
     */
    public String getRepeatDesc(String code) throws BaseException {
        log.debug("Entering CodeInfo.getRepeatDesc(). Incoming parameter is: code = " + String.valueOf(code));
        log.debug("This method is used to load an description for the code past in.");
        String result = "";
        try{
            result = getBean().findRepeatCodeDescByCode(code);
        }catch(Exception e){
            log.error("An exception occurred while retireving an repeat codes description. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Exiting CodeInfo.getRepeatDesc result=" + String.valueOf(result));
        return result;
    } // end getRepeatDesc

    /**
     * Loads a List of Batch Type as CodesModel
     * 
     * @return list of Frequency
     * @throws BaseException
     *         if an exception occurred
     */
    public List<CodeModel> getBatchTypeCodes() throws BaseException {
        log.debug("Entering CodeInfo.getBatchTypeCodes");
        log.debug("This method is used to load a List of Batch Type Codes as CodeModel for display.");
        List<CodeModel> codeList = new ArrayList<CodeModel>();
        codeList.add(new CodeModel(Constants.EMPTY_STRING, SHAMEN_DEFAULT_LABEL));
        List<BatchTypeCodeEntity> entities = null;
        try{
            entities = (List<BatchTypeCodeEntity>) getBean().getBatchTypeCodeList();
        }catch(Exception e){
            log.error("An exception occurred while retireving an batch type codes entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        if(null != entities && !entities.isEmpty()){
            for(int i = 0, j = entities.size();i < j;i++){
                codeList.add(new CodeModel(entities.get(i).getBatchTypeCd(), entities.get(i).getBatchTypeDesc()));
            }// end for
        }else{
            log.warn("The batch type codes list in the database is null or empty.");
        }// end if/else
        log.debug("Exiting CodeInfo.getBatchTypeCodes codeList.size=" + (AppUtil.isEmpty(codeList) ? "empty" : codeList.size()));
        return codeList;
    } // end getBatchTypeCodes

    /**
     * Loads a List of Batch Type as CodesModel
     * 
     * @return list of Frequency
     * @throws BaseException
     *         if an exception occurred
     */
    public List<CodeModel> getBatchTypeCodesWithCollection() throws BaseException {
        log.debug("Entering CodeInfo.getBatchTypeCodesWithCollection");
        log.debug("This method is used to load a List of Batch Type Codes as CodeModel for display.");
        List<CodeModel> codeList = new ArrayList<CodeModel>();
        codeList.add(new CodeModel(Constants.EMPTY_STRING, SHAMEN_DEFAULT_LABEL));
        List<BatchTypeCodeEntity> entities = null;
        try{
            entities = (List<BatchTypeCodeEntity>) getBean().getBatchTypeCodeListWithCol();
        }catch(Exception e){
            log.error("An exception occurred while retireving an batch type codes entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        if(null != entities && !entities.isEmpty()){
            for(int i = 0, j = entities.size();i < j;i++){
                codeList.add(new CodeModel(entities.get(i).getBatchTypeCd(), entities.get(i).getBatchTypeDesc()));
            }// end for
        }else{
            log.warn("The batch type codes list in the database is null or empty.");
        }// end if/else
        log.debug("Exiting CodeInfo.getBatchTypeCodesWithCollection codeList.size=" + (AppUtil.isEmpty(codeList) ? "empty" : codeList.size()));
        return codeList;
    } // end getBatchTypeCodesWithCollection

    /**
     * Gets the code's description based on code passed in.
     * 
     * @param code
     *        the code of selected value
     * @return String description for code
     * @throws BaseException
     *         if an exception occurred
     */
    public String getBatchTypeDesc(String code) throws BaseException {
        log.debug("Entering CodeInfo.getBatchTypeDesc(). Incoming parameter is: code = " + String.valueOf(code));
        log.debug("This method is used to load an description for the code past in.");
        String result = "";
        try{
            result = getBean().findBatchTypeCodeDescByCode(code);
        }catch(Exception e){
            log.error("An exception occurred while retireving an batch type codes description. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Exiting CodeInfo.getBatchTypeDesc result=" + String.valueOf(result));
        return result;
    } // end getBatchTypeDesc

    /**
     * Loads a List of Application Type as CodesModel
     * 
     * @return list of Application Type
     * @throws BaseException
     *         if an exception occurred
     */
    public List<CodeModel> getApplicationTypeCodes() throws BaseException {
        log.debug("Entering CodeInfo.getApplicationTypeCodes");
        log.debug("This method is used to load a List of Application Type Codes as CodeModel for display.");
        List<CodeModel> codeList = new ArrayList<CodeModel>();
        codeList.add(new CodeModel(Constants.EMPTY_STRING, SHAMEN_DEFAULT_LABEL));
        List<ApplicationTypeCodeEntity> entities = null;
        try{
            entities = (List<ApplicationTypeCodeEntity>) getBean().getApplicationTypeCodeList();
        }catch(Exception e){
            log.error("An exception occurred while retireving an application type codes entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        if(null != entities && !entities.isEmpty()){
            for(int i = 0, j = entities.size();i < j;i++){
                codeList.add(new CodeModel(entities.get(i).getApplicationTypeCd(), entities.get(i).getApplicationTypeDesc()));
            }// end for
        }else{
            log.warn("The application type codes list in the database is null or empty.");
        }// end if/else
        log.debug("Exiting CodeInfo.getApplicationTypeCodes codeList.size=" + (AppUtil.isEmpty(codeList) ? "empty" : codeList.size()));
        return codeList;
    } // end getApplicationTypeCodes

    /**
     * Gets the code's description based on code passed in.
     * 
     * @param code
     *        the code of selected value
     * @return String description for code
     * @throws BaseException
     *         if an exception occurred
     */
    public String getApplicationTypeDesc(String code) throws BaseException {
        log.debug("Entering CodeInfo.getApplicationTypeDesc(). Incoming parameter is: code = " + String.valueOf(code));
        log.debug("This method is used to load an description for the code past in.");
        String result = "";
        try{
            result = getBean().findApplicationTypeCodeDescByCode(code);
        }catch(Exception e){
            log.error("An exception occurred while retireving an application type codes description. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Exiting CodeInfo.getApplicationTypeDesc result=" + String.valueOf(result));
        return result;
    } // end getApplicationTypeDesc

    /**
     * Loads a List of Application Status as CodesModel
     * 
     * @return list of Application Status
     * @throws BaseException
     *         if an exception occurred
     */
    public List<CodeModel> getApplicationStatusCodes() throws BaseException {
        log.debug("Entering CodeInfo.getApplicationStatusCodes");
        log.debug("This method is used to load a List of Application Status Codes as CodeModel for display.");
        List<CodeModel> codeList = new ArrayList<CodeModel>();
        codeList.add(new CodeModel(Constants.EMPTY_STRING, SHAMEN_DEFAULT_LABEL));
        List<ApplicationStatusCodeEntity> entities = null;
        try{
            entities = (List<ApplicationStatusCodeEntity>) getBean().getApplicationStatusCodeList();
        }catch(Exception e){
            log.error("An exception occurred while retireving an application status codes entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        if(null != entities && !entities.isEmpty()){
            for(int i = 0, j = entities.size();i < j;i++){
                codeList.add(new CodeModel(entities.get(i).getApplicationStatusCd(), entities.get(i).getApplicationStatusDesc()));
            }// end for
        }else{
            log.warn("The application status codes list in the database is null or empty.");
        }// end if/else
        log.debug("Exiting CodeInfo.getApplicationStatusCodes codeList.size=" + (AppUtil.isEmpty(codeList) ? "empty" : codeList.size()));
        return codeList;
    } // end getApplicationStatusCodes

    /**
     * Gets the code's description based on code passed in.
     * 
     * @param code
     *        the code of selected value
     * @return String description for code
     * @throws BaseException
     *         if an exception occurred
     */
    public String getApplicationStatusDesc(String code) throws BaseException {
        log.debug("Entering CodeInfo.getApplicationStatusDesc(). Incoming parameter is: code = " + String.valueOf(code));
        log.debug("This method is used to load an description for the code past in.");
        String result = "";
        try{
            result = getBean().findApplicationStatusCodeDescByCode(code);
        }catch(Exception e){
            log.error("An exception occurred while retireving an application type codes description. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Exiting CodeInfo.getApplicationStatusDesc result=" + String.valueOf(result));
        return result;
    } // end getApplicationStatusDesc

    /**
     * This method is used to load a ApplicationStatusCodeEntity for the code past in.
     * 
     * @param code
     *        the code of selected value
     * @return ApplicationStatusCodeEntity
     * @throws BaseException
     *         if an exception occurred
     */
    public ApplicationStatusCodeEntity getStatusEntityByCode(String code) throws BaseException {
        log.debug("Entering CodeInfo.getStatusEntityByCode(). Incoming parameter is: code = " + String.valueOf(code));
        log.debug("This method is used to load a ApplicationStatusCodeEntity for the code past in.");
        ApplicationStatusCodeEntity entity = null;
        try{
            entity = getBean().findApplicationStatusCodeEntityByCode(code);
        }catch(Exception e){
            log.error("An exception occurred while retireving an application status code entity. Throwing a BaseException with the original exception. Message is: " + e.getMessage(), e);
            throw new BaseException(e.getMessage(), e);
        }// end try/catch
        log.debug("Exiting CodeInfo.getStatusEntityByCode result=" + String.valueOf(entity));
        return entity;
    }// end getStatusEntityByCode

    /**
     * This method gets the descriptions for the associated codes
     * 
     * @param model
     *        the Schedule Model to update
     * @return ScheduleModel
     * @throws BaseException
     *         if an exception occurred
     */
    public ScheduleModel fillCodeDesc(ScheduleModel model) throws BaseException {
        log.debug("Entering CodeInfo.fillCodeDesc(). Incoming parameter is: ScheduleModel = " + String.valueOf(model));
        log.debug("This method gets the descriptions for the associated codes");
        if(!StringUtil.isNullOrEmpty(model.getFrequencyCd())){
            model.setFrequencyDesc(getFrequencyDesc(model.getFrequencyCd()));
        }// end if
        if(!StringUtil.isNullOrEmpty(model.getRepeatCd())){
            model.setRepeatDesc(getRepeatDesc(model.getRepeatCd()));
        }// end if

        log.debug("Exiting CodeInfo.fillCodeDesc result=" + String.valueOf(model));
        return model;
    }// end fillCodeDesc

    /**
     * This method is used to replace the use of a compiler-generated <code>readResolve()</code> method which is called from the <code>readObject()</code> method within the serialization machinery.
     * <p>
     * The <code>readObject()</code> method will create a new instance of the serialized class and call the <code>readResolve()</code> thereafter. By providing this method, the singleton pattern will not be violated with an additional instance of the class since this method returns the singleton instance.
     * 
     * @return The singleton instance of this class
     * @throws ObjectStreamException
     *         An exception if the serialization machinery fails on this method.
     */
    private Object readResolve() throws ObjectStreamException {
        return INSTANCE;
    }// end readResolve

} // end class
