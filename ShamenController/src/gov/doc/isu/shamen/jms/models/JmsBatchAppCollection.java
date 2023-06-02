/**
 * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
 */
package gov.doc.isu.shamen.jms.models;

import gov.doc.isu.shamen.interfaces.Scheduleable;

import java.util.List;

/**
 * Model object to hold the Batch Application collection record. This is essentially a collection of batch jobs that run as a unit.
 * 
 * @author <strong>Shane Duncan</strong> JCCC, July 20, 2016
 */
public class JmsBatchAppCollection extends Scheduleable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Long mainBatchAppRefId;// This is the overall ref id
    private List<JmsBatchApp> batchApps;

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer builder = new StringBuffer();
        builder.append("JmsBatchAppCollection [mainBatchAppRefId=");
        builder.append(mainBatchAppRefId);
        builder.append(", batchApps=");
        builder.append(batchApps);
        builder.append("]");
        builder.append(super.toString());
        return builder.toString();
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Jul 20, 2016
     * @return the batchApps
     */
    public List<JmsBatchApp> getBatchApps() {
        return batchApps;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Jul 20, 2016
     * @param batchApps
     *        the batchApps to set
     */
    public void setBatchApps(List<JmsBatchApp> batchApps) {
        this.batchApps = batchApps;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Dec 8, 2016
     * @return the mainBatchApp
     */
    public Long getMainBatchAppRefId() {
        return mainBatchAppRefId;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Dec 8, 2016
     * @param mainBatchApp
     *        the mainBatchApp to set
     */
    public void setMainBatchApp(Long mainBatchAppRefId) {
        this.mainBatchAppRefId = mainBatchAppRefId;
    }
}
