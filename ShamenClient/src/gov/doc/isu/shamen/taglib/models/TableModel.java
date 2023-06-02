/**
 * @(#)TableModel.java 1.0.0 Copyright (c) 2018 Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit All Rights Reserved. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: -Redistribution of source code must retain the above copyright notice, this list of conditions and the following disclaimer. -Redistribution in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. Neither the name of Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit or the names of contributors may be used to endorse or promote products derived from this software without specific prior written permission. This software is provided "AS IS," without a warranty of any kind. ALL EXPRESS OR IMPLIED CONDITIONS,
 *                     REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. You acknowledge that this
 *                     software is not designed, licensed or intended for use in the design, construction, operation or maintenance of any nuclear facility.
 */
package gov.doc.isu.shamen.taglib.models;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.PageContext;

import gov.doc.isu.shamen.jms.models.JmsBatchApp;
import gov.doc.isu.shamen.taglib.ShamenClientPaginatedList;
import gov.doc.isu.shamen.taglib.properties.TableProperties;
import gov.doc.isu.shamen.taglib.util.Href;

/**
 * This class is the backing model for the {@link gov.doc.isu.shamen.taglib.BatchAppHandlerTag} class.
 * 
 * @author Steven Skinner. JCCC
 */
public class TableModel {
    private String homeURL;
    private String webApp;
    private String environment;
    private String hasRunnableJobs;
    private String userId;
    private String tableId;
    private int listSize;
    private boolean listPage;

    private int pagesize;
    private int pageOffset;
    private final PageContext pageContext;
    private TableProperties properties;
    private Href href;
    private HtmlAttributeMap attributes;
    private JmsBatchApp jmsBatchApp;
    private List<JmsBatchApp> allBatchList;
    private List<TableRow> rowListFull;
    private List<TableRow> rowListPage;
    private ShamenClientPaginatedList paginatedList;

    /**
     * This overloaded constructor is used to instantiate this class.
     * 
     * @param webApp
     *        the associated web application name.
     * @param environment
     *        the associated web application environment.
     * @param tableId
     *        the table id.
     * @param listPage
     *        is this a list page.
     * @param properties
     *        the table properties.
     * @param pageContext
     *        the curent page context.
     */
    public TableModel(String webApp, String environment, String tableId, boolean listPage, TableProperties properties, PageContext pageContext) {
        super();
        this.webApp = webApp;
        this.environment = environment;
        this.tableId = tableId;
        this.listPage = listPage;
        this.properties = properties;
        this.pageContext = pageContext;
        this.rowListFull = new ArrayList<TableRow>(20);
        this.attributes = new HtmlAttributeMap();
        this.pagesize = 20;
    }// end constructor

    /**
     * This method is used to add a {@link TableRow} to the table model.
     *
     * @param row
     *        The instance of {@link TableRow} to add.
     */
    public void addRow(TableRow row) {
        rowListFull.add(row);
    }// end addRow

    /**
     * @return the pageOffset
     */
    public int getPageOffset() {
        return pageOffset;
    }// end getPageOffset

    /**
     * @param pageOffset
     *        the pageOffset to set
     */
    public void setPageOffset(int pageOffset) {
        this.pageOffset = pageOffset;
    }// end setPageOffset

    /**
     * @return the pageContext
     */
    public PageContext getPageContext() {
        return pageContext;
    }// end getPageContext

    /**
     * @return the homeURL
     */
    public String getHomeURL() {
        return homeURL;
    }// end getHomeURL

    /**
     * @param homeURL
     *        the homeURL to set
     */
    public void setHomeURL(String homeURL) {
        this.homeURL = homeURL;
    }// end setHomeURL

    /**
     * @return the webApp
     */
    public String getWebApp() {
        return webApp;
    }// end getWebApp

    /**
     * @param webApp
     *        the webApp to set
     */
    public void setWebApp(String webApp) {
        this.webApp = webApp;
    }// end setWebApp

    /**
     * @return the environment
     */
    public String getEnvironment() {
        return environment;
    }// end getEnvironment

    /**
     * @param environment
     *        the environment to set
     */
    public void setEnvironment(String environment) {
        this.environment = environment;
    }// end setEnvironment

    /**
     * @return the hasRunnableJobs
     */
    public String getHasRunnableJobs() {
        return hasRunnableJobs;
    }// end getHasRunnableJobs

    /**
     * @param hasRunnableJobs
     *        the hasRunnableJobs to set
     */
    public void setHasRunnableJobs(String hasRunnableJobs) {
        this.hasRunnableJobs = hasRunnableJobs;
    }// end setHasRunnableJobs

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }// end getUserId

    /**
     * @param userId
     *        the userId to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }// end setUserId

    /**
     * @return the tableId
     */
    public String getTableId() {
        return tableId;
    }// end getTableId

    /**
     * @param tableId
     *        the tableId to set
     */
    public void setTableId(String tableId) {
        this.tableId = tableId;
    }// end setTableId

    /**
     * @return the listSize
     */
    public int getListSize() {
        return listSize;
    }// end getListSize

    /**
     * @param listSize
     *        the listSize to set
     */
    public void setListSize(int listSize) {
        this.listSize = listSize;
    }// end setListSize

    /**
     * @return the listPage
     */
    public boolean isListPage() {
        return listPage;
    }// end isListPage

    /**
     * @param listPage
     *        the listPage to set
     */
    public void setListPage(boolean listPage) {
        this.listPage = listPage;
    }// end setListPage

    /**
     * @return the attributes
     */
    public HtmlAttributeMap getAttributes() {
        return attributes;
    }// end getAttributes

    /**
     * @param attributes
     *        the attributes to set
     */
    public void setAttributes(HtmlAttributeMap attributes) {
        this.attributes = attributes;
    }// end setAttributes

    /**
     * @return the allBatchList
     */
    public List<JmsBatchApp> getAllBatchList() {
        return allBatchList;
    }// end getAllBatchList

    /**
     * @param allBatchList
     *        the allBatchList to set
     */
    public void setAllBatchList(List<JmsBatchApp> allBatchList) {
        this.allBatchList = allBatchList;
    }// end setAllBatchList

    /**
     * @return the rowListFull
     */
    public List<TableRow> getRowListFull() {
        return rowListFull;
    }// end getRowListFull

    /**
     * @param rowListFull
     *        the rowListFull to set
     */
    public void setRowListFull(List<TableRow> rowListFull) {
        this.rowListFull = rowListFull;
    }// end setRowListFull

    /**
     * @return the rowListPage
     */
    public List<TableRow> getRowListPage() {
        return rowListPage;
    }// end getRowListPage

    /**
     * @param rowListPage
     *        the rowListPage to set
     */
    public void setRowListPage(List<TableRow> rowListPage) {
        this.rowListPage = rowListPage;
    }// end setRowListPage

    /**
     * @return the pagesize
     */
    public int getPagesize() {
        return pagesize;
    }// end getPagesize

    /**
     * @param pageSize
     *        the pagesize to set
     */
    public void setPagesize(int pagesize) {
        this.pagesize = pagesize;
    }// end setPagesize

    /**
     * @return the properties
     */
    public TableProperties getProperties() {
        return properties;
    }// end getProperties

    /**
     * @param properties
     *        the properties to set
     */
    public void setProperties(TableProperties properties) {
        this.properties = properties;
    }// end setProperties

    /**
     * @return the href
     */
    public Href getHref() {
        return href;
    }// end getHref

    /**
     * @param href
     *        the href to set
     */
    public void setHref(Href href) {
        this.href = href;
    }// end setHref

    /**
     * @return the jmsBatchApp
     */
    public JmsBatchApp getJmsBatchApp() {
        return jmsBatchApp;
    }// end getJmsBatchApp

    /**
     * @param jmsBatchApp
     *        the jmsBatchApp to set
     */
    public void setJmsBatchApp(JmsBatchApp jmsBatchApp) {
        this.jmsBatchApp = jmsBatchApp;
    }// end setJmsBatchApp

    /**
     * @return the paginatedList
     */
    public ShamenClientPaginatedList getPaginatedList() {
        return paginatedList;
    }// end getPaginatedList

    /**
     * @param paginatedList
     *        the paginatedList to set
     */
    public void setPaginatedList(ShamenClientPaginatedList paginatedList) {
        this.paginatedList = paginatedList;
    }// end setPaginatedList

    public JmsBatchApp getBatchForChildTag(String batchName) {
        JmsBatchApp result = null;
        for(JmsBatchApp batchApp : allBatchList){
            if(batchApp.getName().equalsIgnoreCase(batchName)){
                result = batchApp;
            }// end if
        }// end for
        return result;
    }// end getBatchForChildTag

}// end class
