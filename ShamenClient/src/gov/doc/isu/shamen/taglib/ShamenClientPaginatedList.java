/**
 * @(#)ShamenClientPaginatedList.java 1.0.0 Copyright (c) 2018 Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit All Rights Reserved. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: -Redistribution of source code must retain the above copyright notice, this list of conditions and the following disclaimer. -Redistribution in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. Neither the name of Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit or the names of contributors may be used to endorse or promote products derived from this software without specific prior written permission. This software is provided "AS IS," without a warranty of any kind. ALL EXPRESS OR IMPLIED
 *                                    CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *                                    You acknowledge that this software is not designed, licensed or intended for use in the design, construction, operation or maintenance of any nuclear facility.
 */
package gov.doc.isu.shamen.taglib;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.sql.rowset.CachedRowSet;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import gov.doc.isu.shamen.core.ShamenConstants;
import gov.doc.isu.shamen.taglib.pagination.PaginatedList;

/**
 * This class is the paginated list class for dispalying a table list
 * @author <strong>Steven Skinner</strong> JCCC
 */
public class ShamenClientPaginatedList implements PaginatedList {

    /** current page index, starts at 0 */
    private int index;
    /** total number of results (the total number of rows ) */
    private int fullListSize;

    /** list of results (rows found ) in the current page */
    @SuppressWarnings("rawtypes")
    private List list;

    /** number of results per page (number of rows per page to be displayed ) */
    private int pageSize;

    private int lastRowNum;

    private int firstRowNum;

    private int direction; //

    private boolean lastPageClicked;

    /** Set the default page size */
    private int DEFAULT_PAGE_SIZE = 20;

    /** Http servlet request **/
    private HttpServletRequest request;

    /**
     * 
     */
    public ShamenClientPaginatedList() {
        // TODO Auto-generated constructor stub
    }

    /**
     * Create <code>ShamenPaginatedList</code> instance using the <code>HttpServletRequest</code> object.
     * 
     * @param request
     *        <code>HttpServletRequest</code> object.
     * @param pagesize
     *        the page size - the total number of rows per page.
     */
    public ShamenClientPaginatedList(HttpServletRequest request, int pagesize) {

        pageSize = pagesize != 0 ? pagesize : DEFAULT_PAGE_SIZE;
        String page = request.getParameter(ShamenConstants.PARAMETER_PAGE);
        index = page == null ? 0 : Integer.parseInt(page) - 1;
    }

    /**
     * Create <code>ShamenPaginatedList</code> instance .
     * 
     * @param pagesize
     *        the page size - the total number of rows per page.
     * @return <code>IExtendedPaginatedList</code> instance.
     * @throws Exception
     *         - problem while creating paginatedlist object.
     */
    public PaginatedList getPaginatedListObject(int pagesize) throws Exception {

        if(request == null){
            throw new Exception("Cannot create paginated list. Depends upon HttpServletRequest.");
        }
        return new ShamenClientPaginatedList(request, pageSize);
    }

    /**
     * Set the non-null <code>HttpServletRequest</code> object.
     * 
     * @param request
     *        a <code>HttpServletRequest</code> object.
     */
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * {@inheritDoc}
     */
    public int getFirstRecordIndex() {
        return index * pageSize;
    }

    /**
     * {@inheritDoc}
     */
    public int getLastRecordIndex() {
        return getFirstRecordIndex() + pageSize;
    }

    /**
     * Returns the current page index (the index starts at 0).
     * 
     * @return a primitive integer
     */
    public int getIndex() {
        return index;
    }

    /**
     * {@inheritDoc}
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * {@inheritDoc}
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * {@inheritDoc}
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("rawtypes")
    public List getList() {
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("rawtypes")
    public void setList(List results) {
        this.list = results;
    }

    /**
     * {@inheritDoc}
     */
    public int getFullListSize() {
        return fullListSize;
    }

    /**
     * {@inheritDoc}
     */
    public void setTotalNumberOfRows(int total) {
        this.fullListSize = total;
    }

    /**
     * This method returns the total number of pages for the pagintated list.
     * 
     * @return a primitive integer
     */
    public int getTotalPages() {
        return (int) Math.ceil(((double) fullListSize) / pageSize);
    }

    /**
     * {@inheritDoc}
     */
    public int getObjectsPerPage() {
        return pageSize;
    }

    /**
     * {@inheritDoc}
     */
    public int getPageNumber() {
        return index + 1;
    }

    /**
     * {@inheritDoc}
     */
    public String getSearchId() {
        // Not implemented for now.
        // This is required, if we want the ID to be included in the paginated purpose.
        return null;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("super", super.toString()).toString();
    }

    /**
     * This method returns the last row number in the cached row set.
     * 
     * @return a primitive integer
     */
    public int getLastRowNum() {
        return lastRowNum;
    }

    /**
     * This method returns the first row number in the cached row set.
     * 
     * @return a primitive integer
     */
    public int getFirstRowNum() {
        return firstRowNum;
    }

    /**
     * <p>
     * This method returns the direction the pagination is going. This is used for navigating through the {@link CachedRowSet}. A table below explains the primitive integers that this method will return.
     * </p>
     * <table border="1" bordercolor="maroon" cellpadding="5" cellspacing="0">
     * <tr>
     * <th>Integer</th>
     * <th>What It Means</th>
     * </tr>
     * <tr>
     * <td style="text-align: center">-1</td>
     * <td>A value of -1 means that the application has navigated to the beginning of the {@link CachedRowSet} and will need to query the database for previous records.</td>
     * </tr>
     * <tr>
     * <td style="text-align: center">0</td>
     * <td>A value of 0 means that the application can still navigate through the current {@link CachedRowSet}.</td>
     * </tr>
     * <tr>
     * <td style="text-align: center">1</td>
     * <td>A value of 1 means that the application has navigated to the end of the {@link CachedRowSet} and will need to query the database for the next records from the database.</td>
     * </tr>
     * </table>
     * <br>
     * 
     * @return a primitive integer (-1, 0, or 1)
     */
    public int getDirection() {
        return direction;
    }

    /**
     * This method will set the lastPageClicked field.
     * 
     * @param lastPageClicked
     *        true/false value
     */
    public void setLastPageClicked(boolean lastPageClicked) {
        this.lastPageClicked = lastPageClicked;
    }

    /**
     * This method returns true/false value letting you know if the [Last] page button was clicked.
     * 
     * @return true/false
     */
    public boolean isLastPageClicked() {
        return lastPageClicked;
    }
}
