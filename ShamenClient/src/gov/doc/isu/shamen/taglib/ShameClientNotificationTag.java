/**
 * @(#)ShameClientNotificationTag.java 1.0.0 Copyright (c) 2018 Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit All Rights Reserved. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: -Redistribution of source code must retain the above copyright notice, this list of conditions and the following disclaimer. -Redistribution in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. Neither the name of Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit or the names of contributors may be used to endorse or promote products derived from this software without specific prior written permission. This software is provided "AS IS," without a warranty of any kind. ALL EXPRESS OR
 *                                     IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH
 *                                     DAMAGES. You acknowledge that this software is not designed, licensed or intended for use in the design, construction, operation or maintenance of any nuclear facility.
 */
package gov.doc.isu.shamen.taglib;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.doc.isu.shamen.core.ShamenApplicationStatus;
import gov.doc.isu.shamen.core.ShamenConstants;
import gov.doc.isu.shamen.jms.ApplicationJmsManager;
import gov.doc.isu.shamen.util.ShamenClientUtil;

/**
 * This class builds the Shamen Web Client Notification tag
 * 
 * @author Steven L. Skinner JCCC 12/31/2019
 */
public class ShameClientNotificationTag extends TagSupport {

    private static final long serialVersionUID = 1L;
    private static Log log = LogFactory.getLog("gov.doc.isu.shamen.taglib.ShameClientNotificationTag");
    private static final String IMG_SVG_START = "<acronym class=\"header-icon\">";
    private static final String IMG_SVG_2_END = "</acronym>";
    private static final String BELL_ICON = "<svg xmlns=\"http://www.w3.org/2000/svg\"><path d=\"M0 0h24v24H0z\" fill=\"none\"/><path d=\"M7.58 4.08L6.15 2.65C3.75 4.48 2.17 7.3 2.03 10.5h2c.15-2.65 1.51-4.97 3.55-6.42zm12.39 6.42h2c-.15-3.2-1.73-6.02-4.12-7.85l-1.42 1.43c2.02 1.45 3.39 3.77 3.54 6.42zM18 11c0-3.07-1.64-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.68C7.63 5.36 6 7.92 6 11v5l-2 2v1h16v-1l-2-2v-5zm-6 11c.14 0 .27-.01.4-.04.65-.14 1.18-.58 1.44-1.18.1-.24.15-.5.15-.78h-4c.01 1.1.9 2 2.01 2z\"/></svg>";
    private static final String WARNING_ICON = "<svg xmlns=\"http://www.w3.org/2000/svg\"><path d=\"M0 0h24v24H0z\" fill=\"none\"/><path d=\"M11 15h2v2h-2zm0-8h2v6h-2zm.99-5C6.47 2 2 6.48 2 12s4.47 10 9.99 10C17.52 22 22 17.52 22 12S17.52 2 11.99 2zM12 20c-4.42 0-8-3.58-8-8s3.58-8 8-8 8 3.58 8 8-3.58 8-8 8z\"/></svg>";
    private static final String DEFAULT_HEADER_FONT_SIZE = "24px";
    private static final String DEFAULT_HEADER_FONT_WEIGHT = "700";
    private static final String DEFAULT_HEADER_TEXT_COLOR = "#FFF";
    private static final String DEFAULT_FONT_SIZE = "15px";
    private static final String DEFAULT_FONT_WEIGHT = "500";
    private static final String DEFAULT_BACKGROUND_COLOR = "#2F91E1";
    private static final String DEFAULT_TEXT_COLOR = "#FFF";
    private static final String DEFAULT_HEADER_TEXT = " ATTENTION ";
    private static final String WARNING_TEXT = "Failed to connect with a cooperating system therefore some functionality may be limited.<br>Please notify the System Administrator with this error code: <span class=\"error-code\">";
    private boolean showNotificationIcon = true;
    private String notificationIconColor;
    private String notificationIconSize;
    private boolean showHeaderText = true;
    private String headerText;
    private String headerTextColor;
    private String headerFontSize;
    private String headerFontWeight;
    private String backgroundColor;
    private String textColor;
    private String fontSize;
    private String fontWeight;

    /**
     * This tag creates application notification banner
     * 
     * @author Steven Skinner
     */
    public int doStartTag() throws JspException {

        StringBuffer sb = new StringBuffer();
        boolean errorOccured = false;
        if(ShamenConstants.USE_SHAMEN){
            try{
                if(ApplicationJmsManager.getInstance().isConnected()){
                    HashMap<String, Object> receivedMessageMap = getAppInfoFromShamen();
                    if(receivedMessageMap != null){
                        log.info("Received a response from ShamenWeb.");
                        receivedMessageMap = ShamenApplicationStatus.getInstance().getSc().decryptApplicationObjectMap(receivedMessageMap);
                        if("Y".equalsIgnoreCase(receivedMessageMap.get(ShamenApplicationStatus.SHOW_APP_NOTIFICATION).toString())){
                            sb = doInfo(receivedMessageMap.get(ShamenApplicationStatus.APP_NOTIFICATION_DETAIL));
                            pageContext.getOut().print(sb.toString());
                        }// end if
                    }// end if
                }// end if
            }catch(Exception e){
                String dateTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss,SSS").format(new Date());
                log.error("Error occurred while constructing the ShameClientNotificationTag. Exception is: " + e.getMessage(), e);
                errorOccured = true;
                sb = doWarning(dateTime);
            }finally{
                if(errorOccured){
                    try{
                        pageContext.getOut().print(sb.toString());
                    }catch(IOException e){
                        log.error("Error occurred while constructing the ShameClientNotificationTag. Exception is: " + e.getMessage(), e);
                    }// end try/catch
                }// end if
            }// end try/catch/finally
        }// end if
        return EVAL_PAGE;
    }// end doStartTag

    /**
     * This method gets the sends the jms message and waits for reply to load the message map.
     * 
     * @return HashMap<String, Object>
     */
    public HashMap<String, Object> getAppInfoFromShamen() {
        HashMap<String, String> messageMap = new HashMap<String, String>();
        HashMap<String, Object> receivedMessageMap = new HashMap<String, Object>();
        messageMap.put(ShamenApplicationStatus.STATUS, ShamenApplicationStatus.getInstance().getStatus());
        messageMap.put(ShamenApplicationStatus.REPLY_REQUIRED, "true");
        messageMap = ShamenApplicationStatus.getInstance().getSc().encryptApplicationMap(messageMap);
        messageMap.put(ShamenApplicationStatus.ACTION, ShamenApplicationStatus.ACTION_GET_APP_NOTIFICATION);
        messageMap.put(ShamenApplicationStatus.APPLICATION_NAME, ShamenConstants.CLIENT_SELECTOR_VALUE);
        messageMap.put(ShamenApplicationStatus.APPLICATION_ENVIRONMENT, ShamenConstants.CLIENT_SELECTOR_VALUE_2);
        messageMap.put(ShamenApplicationStatus.APPLICATION_INSTANCE, ShamenConstants.CLIENT_SELECTOR_VALUE_3);
        try{
            log.info("Attempt to get App Information from ShamenWeb.");
            receivedMessageMap = ApplicationJmsManager.getInstance().sendPTPWithAcknowledge(ShamenConstants.SHAMEN_REQUEST_Q, messageMap, 5000);

        }catch(Exception e){
            log.error("Exception occurred confirming status update to Shamen.  Exception is: " + e.getMessage(), e);
        }// end try-catch
        return receivedMessageMap;
    }// end getAppInfoFromShamen

    /**
     * This method formats information from Shamen Web into html, and returns the string.
     * 
     * @return
     * @throws JspException
     */
    public StringBuffer doInfo(Object information) throws JspException {
        StringBuffer sb = new StringBuffer();
        sb.append(loadStyleSheet(true));
        sb.append("<div class=\"shamen-app-info-container\" id=\"app-info-container\">");
        if(isShowHeaderText()){
            sb.append("<h1>");
            if(isShowNotificationIcon()){
                sb.append(IMG_SVG_START).append(BELL_ICON).append(IMG_SVG_2_END);
            }// end if
            sb.append(ShamenClientUtil.isNullOrEmpty(getHeaderText()) ? DEFAULT_HEADER_TEXT : getHeaderText());
            if(isShowNotificationIcon()){
                sb.append(IMG_SVG_START).append(BELL_ICON).append(IMG_SVG_2_END);
            }// end if
            sb.append("</h1>");
        }// end if
        sb.append("<p class=\"shamen-app-info\" id=\"app-info\">");
        sb.append(ShamenClientUtil.textBreak2HtmlBreak(information.toString()));
        sb.append("</p></div>");
        return sb;
    }// end

    /**
     * This method formats information from Shamen Web into html, and returns the string.
     * 
     * @return
     * @throws JspException
     */
    public StringBuffer doWarning(String dateTime) throws JspException {
        StringBuffer sb = new StringBuffer();
        sb.append(loadStyleSheet(false));
        sb.append("<div class=\"shamen-app-info-container\" id=\"app-info-container\">");
        sb.append("<h1>");
        sb.append(IMG_SVG_START).append(WARNING_ICON).append(IMG_SVG_2_END);
        sb.append(" WARNING ");
        sb.append(IMG_SVG_START).append(WARNING_ICON).append(IMG_SVG_2_END);
        sb.append("</h1>");
        sb.append("<p class=\"shamen-app-info\" id=\"app-info\">");
        sb.append(WARNING_TEXT).append(dateTime).append("</span>");
        sb.append("</p></div>");
        return sb;
    }// end

    /**
     * This method loads a default style sheet or an import statement for a custom one sent in by the tag parameters.
     *
     * @return styleSheet html
     */
    private String loadStyleSheet(boolean goodConnection) {
        StringBuffer sb = new StringBuffer();
        // add in-line style
        sb.append("<style>");
        if(goodConnection){
            sb.append(".shamen-app-info-container{padding: 12px 20px;margin-bottom: 16px;border-radius: 6px !important;");
            sb.append("background-color:");
            sb.append(ShamenClientUtil.isNullOrEmpty(getBackgroundColor()) ? DEFAULT_BACKGROUND_COLOR : getBackgroundColor()).append(";");
            sb.append("color:");
            sb.append(ShamenClientUtil.isNullOrEmpty(getTextColor()) ? DEFAULT_TEXT_COLOR : getTextColor()).append(";");
            sb.append("font-size:");
            sb.append(ShamenClientUtil.isNullOrEmpty(getFontSize()) ? DEFAULT_FONT_SIZE : getFontSize()).append(";");
            sb.append("font-weight:");
            sb.append(ShamenClientUtil.isNullOrEmpty(getFontWeight()) ? DEFAULT_FONT_WEIGHT : getFontWeight()).append(";");
            sb.append("}");
            sb.append(".shamen-app-info-container h1{text-align:center;");
            sb.append("font-size:");
            sb.append(ShamenClientUtil.isNullOrEmpty(getHeaderFontSize()) ? DEFAULT_HEADER_FONT_SIZE : getHeaderFontSize()).append(";");
            sb.append("line-height:");
            sb.append(ShamenClientUtil.isNullOrEmpty(getHeaderFontSize()) ? DEFAULT_HEADER_FONT_SIZE : getHeaderFontSize()).append(";");
            sb.append("font-weight:");
            sb.append(ShamenClientUtil.isNullOrEmpty(getHeaderFontWeight()) ? DEFAULT_HEADER_FONT_WEIGHT : getHeaderFontWeight()).append(";");
            sb.append("color:");
            sb.append(ShamenClientUtil.isNullOrEmpty(getHeaderTextColor()) ? DEFAULT_HEADER_TEXT_COLOR : getHeaderTextColor()).append(";");
            sb.append("}");
            sb.append(".header-icon{padding: 0 5px;}");
            sb.append(".header-icon svg{");
            sb.append("fill:");
            sb.append(ShamenClientUtil.isNullOrEmpty(getNotificationIconColor()) ? DEFAULT_HEADER_TEXT_COLOR : getNotificationIconColor()).append(";");
            sb.append("height:");
            sb.append(ShamenClientUtil.isNullOrEmpty(getNotificationIconSize()) ? DEFAULT_HEADER_FONT_SIZE : getNotificationIconSize()).append(";");
            sb.append("width:");
            sb.append(ShamenClientUtil.isNullOrEmpty(getNotificationIconSize()) ? DEFAULT_HEADER_FONT_SIZE : getNotificationIconSize()).append(";");
            sb.append("}");
        }else{
            sb.append(".shamen-app-info-container{padding: 12px 20px;margin-bottom: 16px;border-radius: 6px !important;");
            sb.append("background-color:#f0ad4e;");
            sb.append("color:#fffcf5;");
            sb.append("font-size:");
            sb.append(ShamenClientUtil.isNullOrEmpty(getFontSize()) ? DEFAULT_FONT_SIZE : getFontSize()).append(";");
            sb.append("font-weight:");
            sb.append(ShamenClientUtil.isNullOrEmpty(getFontWeight()) ? DEFAULT_FONT_WEIGHT : getFontWeight()).append(";");
            sb.append("}");
            sb.append(".shamen-app-info-container h1{text-align:center;");
            sb.append("font-size:");
            sb.append(ShamenClientUtil.isNullOrEmpty(getHeaderFontSize()) ? DEFAULT_HEADER_FONT_SIZE : getHeaderFontSize()).append(";");
            sb.append("line-height:");
            sb.append(ShamenClientUtil.isNullOrEmpty(getHeaderFontSize()) ? DEFAULT_HEADER_FONT_SIZE : getHeaderFontSize()).append(";");
            sb.append("font-weight:");
            sb.append(ShamenClientUtil.isNullOrEmpty(getHeaderFontWeight()) ? DEFAULT_HEADER_FONT_WEIGHT : getHeaderFontWeight()).append(";");
            sb.append("color:#fffcf5;");
            sb.append("}");
            sb.append(".header-icon{padding: 0 5px;}");
            sb.append(".header-icon svg{");
            sb.append("fill:#fffcf5;");
            sb.append("height:");
            sb.append(ShamenClientUtil.isNullOrEmpty(getNotificationIconSize()) ? DEFAULT_HEADER_FONT_SIZE : getNotificationIconSize()).append(";");
            sb.append("width:");
            sb.append(ShamenClientUtil.isNullOrEmpty(getNotificationIconSize()) ? DEFAULT_HEADER_FONT_SIZE : getNotificationIconSize()).append(";");
            sb.append("}");
            sb.append(".error-code{font-weight:").append(DEFAULT_HEADER_FONT_WEIGHT).append(";");
            sb.append("color:#C24642;");
            sb.append("}");
        }// end if/else
        sb.append(".shamen-app-info{margin-top: 0;margin-bottom: 0;}");
        sb.append("</style>");
        return sb.toString();
    }// end loadStyleSheet

    /**
     * @return the showNotificationIcon
     */
    public boolean isShowNotificationIcon() {
        return showNotificationIcon;
    }// end isShowNotificationIcon

    /**
     * @param showNotificationIcon
     *        the showNotificationIcon to set
     */
    public void setShowNotificationIcon(boolean showNotificationIcon) {
        this.showNotificationIcon = showNotificationIcon;
    }// end setShowNotificationIcon

    /**
     * @return the notificationIconColor
     */
    public String getNotificationIconColor() {
        return notificationIconColor;
    }// end getNotificationIconColor

    /**
     * @param notificationIconColor
     *        the notificationIconColor to set
     */
    public void setNotificationIconColor(String notificationIconColor) {
        this.notificationIconColor = notificationIconColor;
    }// end setNotificationIconColor

    /**
     * @return the notificationIconSize
     */
    public String getNotificationIconSize() {
        return notificationIconSize;
    }// end getNotificationIconSize

    /**
     * @param notificationIconSize
     *        the notificationIconSize to set
     */
    public void setNotificationIconSize(String notificationIconSize) {
        this.notificationIconSize = notificationIconSize;
    }// end setNotificationIconSize

    /**
     * @return the showHeaderText
     */
    public boolean isShowHeaderText() {
        return showHeaderText;
    }// end isShowHeaderText

    /**
     * @param showHeaderText
     *        the showHeaderText to set
     */
    public void setShowHeaderText(boolean showHeaderText) {
        this.showHeaderText = showHeaderText;
    }// end setShowHeaderText

    /**
     * @return the headerText
     */
    public String getHeaderText() {
        return headerText;
    }// end getHeaderText

    /**
     * @param headerText
     *        the headerText to set
     */
    public void setHeaderText(String headerText) {
        this.headerText = headerText;
    }// end setHeaderText

    /**
     * @return the headerTextColor
     */
    public String getHeaderTextColor() {
        return headerTextColor;
    }// end getHeaderTextColor

    /**
     * @param headerTextColor
     *        the headerTextColor to set
     */
    public void setHeaderTextColor(String headerTextColor) {
        this.headerTextColor = headerTextColor;
    }// end setHeaderTextColor

    /**
     * @return the headerFontSize
     */
    public String getHeaderFontSize() {
        return headerFontSize;
    }// end getHeaderFontSize

    /**
     * @param headerFontSize
     *        the headerFontSize to set
     */
    public void setHeaderFontSize(String headerFontSize) {
        this.headerFontSize = headerFontSize;
    }// end setHeaderFontSize

    /**
     * @return the headerFontWeight
     */
    public String getHeaderFontWeight() {
        return headerFontWeight;
    }// end getHeaderFontWeight

    /**
     * @param headerFontWeight
     *        the headerFontWeight to set
     */
    public void setHeaderFontWeight(String headerFontWeight) {
        this.headerFontWeight = headerFontWeight;
    }// end setHeaderFontWeight

    /**
     * @return the backgroundColor
     */
    public String getBackgroundColor() {
        return backgroundColor;
    }// end getBackgroundColor

    /**
     * @param backgroundColor
     *        the backgroundColor to set
     */
    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }// end setBackgroundColor

    /**
     * @return the textColor
     */
    public String getTextColor() {
        return textColor;
    }// end getTextColor

    /**
     * @param textColor
     *        the textColor to set
     */
    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }// end setTextColor

    /**
     * @return the fontSize
     */
    public String getFontSize() {
        return fontSize;
    }// end getFontSize

    /**
     * @param fontSize
     *        the fontSize to set
     */
    public void setFontSize(String fontSize) {
        this.fontSize = fontSize;
    }// end setFontSize

    /**
     * @return the fontWeight
     */
    public String getFontWeight() {
        return fontWeight;
    }// end getFontWeight

    /**
     * @param fontWeight
     *        the fontWeight to set
     */
    public void setFontWeight(String fontWeight) {
        this.fontWeight = fontWeight;
    }// end setFontWeight

}// end class
