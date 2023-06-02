package gov.doc.isu.shamen.form;

import static gov.doc.isu.dwarf.resources.Constants.NEW_LINE;

import java.util.List;

import gov.doc.isu.dwarf.core.AbstractForm;
import gov.doc.isu.shamen.models.RunStatusModel;

/**
 * Form bean for the Run Status Information for the Shamen Web application.
 * 
 * @author Steven L. Skinner
 */
public class RunStatusForm extends AbstractForm {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private List<RunStatusModel> runStatuses;
    private boolean showDetail;
    private String batchName;
    private String statusResultDetail;

    /**
     * @return the runStatuses
     */
    public List<RunStatusModel> getRunStatuses() {
        return runStatuses;
    }

    /**
     * @param runStatuses
     *        the runStatuses to set
     */
    public void setRunStatuses(List<RunStatusModel> runStatuses) {
        this.runStatuses = runStatuses;
    }

    /**
     * @return the showDetail
     */
    public boolean isShowDetail() {
        return showDetail;
    }

    /**
     * @param showDetail
     *        the showDetail to set
     */
    public void setShowDetail(boolean showDetail) {
        this.showDetail = showDetail;
    }

    /**
     * @return the batchName
     */
    public String getBatchName() {
        return batchName;
    }

    /**
     * @param batchName
     *        the batchName to set
     */
    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    /**
     * @return the statusResultDetail
     */
    public String getStatusResultDetail() {
        return statusResultDetail;
    }

    /**
     * @param statusResultDetail
     *        the statusResultDetail to set
     */
    public void setStatusResultDetail(String statusResultDetail) {
        this.statusResultDetail = statusResultDetail;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuffer builder = new StringBuffer();
        builder.append("RunStatusForm [runStatuses=");
        builder.append(runStatuses).append(NEW_LINE);
        builder.append(super.toString());
        builder.append("]");
        return builder.toString();
    }// end toString

}// end RunStatusForm
