/**
 * 
 */
package gov.doc.isu.shamen.util;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import gov.doc.isu.dwarf.taglib.displaytag.pagination.PaginatedList;
import gov.doc.isu.dwarf.taglib.displaytag.properties.SortOrderEnum;

/**
 * @author sls000is
 */
public class ShamenPaginatedList implements PaginatedList {

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

    private boolean lastPageClicked;

    /** Set the default page size */
    private static int DEFAULT_PAGE_SIZE = 25;

    /** Http servlet request **/
    private HttpServletRequest request;

    private static String REQUEST_PARAMETERS_PAGE = "page";

    /**
     * 
     */
    public ShamenPaginatedList() {
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
    public ShamenPaginatedList(HttpServletRequest request, int pagesize) {

        pageSize = pagesize != 0 ? pagesize : DEFAULT_PAGE_SIZE;
        String page = request.getParameter(REQUEST_PARAMETERS_PAGE);
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
        return new ShamenPaginatedList(request, pageSize);
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

    @Override
    public String getSortCriterion() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SortOrderEnum getSortDirection() {
        // TODO Auto-generated method stub
        return null;
    }
}
