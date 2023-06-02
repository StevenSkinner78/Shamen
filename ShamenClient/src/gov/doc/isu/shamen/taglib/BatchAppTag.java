/**
 * @(#)BatchAppTag.java 1.0.0 Copyright (c) 2018 Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit All Rights Reserved. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: -Redistribution of source code must retain the above copyright notice, this list of conditions and the following disclaimer. -Redistribution in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. Neither the name of Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit or the names of contributors may be used to endorse or promote products derived from this software without specific prior written permission. This software is provided "AS IS," without a warranty of any kind. ALL EXPRESS OR IMPLIED CONDITIONS,
 *                      REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. You acknowledge that this
 *                      software is not designed, licensed or intended for use in the design, construction, operation or maintenance of any nuclear facility.
 */
package gov.doc.isu.shamen.taglib;

import java.lang.reflect.InvocationTargetException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.doc.isu.shamen.core.ShamenConstants;
import gov.doc.isu.shamen.jms.models.JmsBatchApp;
import gov.doc.isu.shamen.taglib.models.BatchAppModel;
import gov.doc.isu.shamen.taglib.models.TableModel;

/**
 * This class adds a Batch Job to the SHAMEN Batch Application Admin batch list.
 * 
 * @author <strong>Steven Skinner</strong> JCCC, 10/01/2019
 */
public class BatchAppTag extends BodyTagSupport {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final Log log = LogFactory.getLog("gov.doc.isu.shamen.taglib.BatchAppTag");
    private String allowRun = "N";
    private String batchName;
    private String displayName;
    private String useParameterInput = "Y";
    private String defaultParameter;
    private String customMessage;
    private String showDefaultMessage = "Y";
    private static BatchAppHandlerTag parentTag;
    private static TableModel tableModel;

    /*
     * (non-Javadoc)
     * @see javax.servlet.jsp.tagext.BodyTagSupport#doStartTag()
     */
    public int doStartTag() throws JspException {
        log.trace("Entering doStartTag");
        parentTag = (BatchAppHandlerTag) findAncestorWithClass(this, BatchAppHandlerTag.class);
        if(parentTag == null){
            log.debug("Inside if condition with expression: parentTag == null");
            throw new JspException("Batch App Handler Tag is required.");
        }// end if
        tableModel = parentTag.getTableModel();
        log.trace("Exiting doStartTag");
        return 2;
    }// end doStartTag

    /**
     * This method adds the batch job to the list in tableModel in the parent BatchAppHandlerTag
     */
    public int doEndTag() throws JspException {
        log.trace("Entering doEndTag");
        if(parentTag == null){
            log.debug("Inside if condition with expression: parentTag == null");
            throw new JspException("Batch App Handler Tag is required.");
        }// end if
        if(ShamenConstants.USE_SHAMEN && parentTag.isListPage()){
            log.debug("Inside if condition with expression: USE_SHAMEN && parentTag.isListPage()");
            log.debug("Instantiating variable with identifier: batchJob");
            JmsBatchApp batchJob = tableModel.getBatchForChildTag(batchName);
            if(batchJob != null){
                log.debug("Inside if condition with expression: batchJob != null");
                log.debug("Method call to tableModel.setListSize(tableModel.getListSize() + 1)");
                tableModel.setListSize(tableModel.getListSize() + 1);
                try{
                    BatchAppModel batchModel = new BatchAppModel(batchJob);
                    batchModel.setDisplayName(displayName);
                    batchModel.setAllowRun(allowRun);
                    batchModel.setUseParameterInput(useParameterInput);
                    batchModel.setDefaultParameter(defaultParameter);
                    batchModel.setShowDefaultMessage(showDefaultMessage);
                    batchModel.setCustomMessage(customMessage);
                    if(null != bodyContent){
                        log.debug("Inside if condition with expression: null != bodyContent");
                        String inputBox = bodyContent.getString();
                        inputBox = inputBox.replace("job-parameters", "job-parameters" + batchJob.getBatchAppRefId());
                        batchModel.setBodyContent(inputBox);
                    }

                    tableModel.addRow(batchModel);

                }catch(Exception e){
                    log.error("Error While constructing the BatchApp. Exception is:" + e.getMessage(), e);
                    throw new JspException(e.getMessage());
                }// end try/catch
            }// end if
        }// end if
        release();
        log.trace("Exiting doEndTag");
        return EVAL_PAGE;
    }// end doStartTag

    /**
     * @return the allowRun
     */
    public String getAllowRun() {
        return allowRun;
    }// end getAllowRun

    /**
     * @param allowRun
     *        the allowRun to set
     */
    public void setAllowRun(String allowRun) {
        this.allowRun = allowRun;
    }// end setAllowRun

    /**
     * @return the batchName
     */
    public String getBatchName() {
        return batchName;
    }// end getBatchName

    /**
     * @param batchName
     *        the batchName to set
     */
    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }// end setBatchName

    /**
     * @return the displayName
     */
    public String getDisplayName() {
        return displayName;
    }// end getDisplayName

    /**
     * @param displayName
     *        the displayName to set
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }// end setDisplayName

    /**
     * @return the useParameterInput
     */
    public String getUseParameterInput() {
        return useParameterInput;
    }// end getUseParameterInput

    /**
     * @param useParameterInput
     *        the useParameterInput to set
     */
    public void setUseParameterInput(String useParameterInput) {
        this.useParameterInput = useParameterInput;
    }// end setUseParameterInput

    /**
     * @return the defaultParameter
     */
    public String getDefaultParameter() {
        return defaultParameter;
    }// end getDefaultParameter

    /**
     * @param defaultParameter
     *        the defaultParameter to set
     */
    public void setDefaultParameter(String defaultParameter) {
        if(defaultParameter.startsWith("pageScope.") || defaultParameter.startsWith("requestScope.") || defaultParameter.startsWith("sessionScope.") || defaultParameter.startsWith("applicationScope.")){
            defaultParameter = (String) evaluateExpression(defaultParameter);
        }// end if/else
        this.defaultParameter = defaultParameter;
    }// end setDefaultParameter

    /**
     * @return the customMessage
     */
    public String getCustomMessage() {
        return customMessage;
    }// end getCustomMessage

    /**
     * @param customMessage
     *        the customMessage to set
     */
    public void setCustomMessage(String customMessage) {
        this.customMessage = customMessage;
    }// end setCustomMessage

    /**
     * @return the showDefaultMessage
     */
    public String getShowDefaultMessage() {
        return showDefaultMessage;
    }// end getShowDefaultMessage

    /**
     * @param showDefaultMessage
     *        the showDefaultMessage to set
     */
    public void setShowDefaultMessage(String showDefaultMessage) {
        this.showDefaultMessage = showDefaultMessage;
    }// end setShowDefaultMessage

    /**
     * <p>
     * evaluate an expression in a way similar to LE in jstl.
     * </p>
     * <p>
     * the first token is supposed to be an object in the page scope (default scope) or one of the following:
     * </p>
     * <ul>
     * <li>pageScope</li>
     * <li>requestScope</li>
     * <li>sessionScope</li>
     * <li>applicationScope</li>
     * </ul>
     * <p>
     * Tokens after the object name are interpreted as javabean properties (accessed through getters), mapped or indexed properties, using the jakarta common-beans library
     * </p>
     * 
     * @param expression
     *        expression to evaluate
     * @return Object result
     * @throws ObjectLookupException
     *         if unable to get a bean using the given expression
     */
    protected Object evaluateExpression(String expression) {
        String expressionWithoutScope = expression;

        // default scope = request
        // this is for compatibility with the previous version, probably default should be PAGE
        int scope = PageContext.REQUEST_SCOPE;

        if(expression.startsWith("pageScope.")){
            scope = PageContext.PAGE_SCOPE;
            expressionWithoutScope = expressionWithoutScope.substring(expressionWithoutScope.indexOf('.') + 1);
        }else if(expression.startsWith("requestScope.")){
            scope = PageContext.REQUEST_SCOPE;
            expressionWithoutScope = expressionWithoutScope.substring(expressionWithoutScope.indexOf('.') + 1);

        }else if(expression.startsWith("sessionScope.")){
            scope = PageContext.SESSION_SCOPE;
            expressionWithoutScope = expressionWithoutScope.substring(expressionWithoutScope.indexOf('.') + 1);

        }else if(expression.startsWith("applicationScope.")){
            scope = PageContext.APPLICATION_SCOPE;
            expressionWithoutScope = expressionWithoutScope.substring(expressionWithoutScope.indexOf('.') + 1);

        }// end if/else
        if(expressionWithoutScope.indexOf('.') != -1){
            try{
                // complex: property from a bean
                String objectName = StringUtils.substringBefore(expressionWithoutScope, ".");
                String beanProperty = StringUtils.substringAfter(expressionWithoutScope, ".");
                Object beanObject;

                // get the bean
                beanObject = pageContext.getAttribute(objectName, scope);

                // if null return
                if(beanObject == null){
                    return "Unknown";
                }// end if

                return PropertyUtils.getSimpleProperty(beanObject, beanProperty);

            }catch(IllegalAccessException e){
                return "Unknown";
            }catch(InvocationTargetException e){
                return "Unknown";
            }catch(NoSuchMethodException e){
                return "Unknown";
            }// end try/catch
        }// end if

        return pageContext.getAttribute(expressionWithoutScope, scope);

    }// end evaluateExpression

}// end class
