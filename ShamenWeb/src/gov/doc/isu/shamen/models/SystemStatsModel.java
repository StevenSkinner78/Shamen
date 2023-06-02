/**
 * @author <strong>Shane Duncan</strong> JCCC, July 17, 2017
 */
package gov.doc.isu.shamen.models;

import java.util.Map;
import java.util.TreeMap;

/**
 * s Model object to hold the statistic records for an entire system
 * 
 * @author <strong>Shane Duncan</strong> JCCC, July 17, 2017
 */
public class SystemStatsModel implements java.io.Serializable {

    private static final long serialVersionUID = -1222682569278763434L;
    private StatModel allStats;
    private StatModel batchStats;
    // This is used to hold the batch states since they are tough to determine
    private Map<Long, Boolean> batchStates = new TreeMap<Long, Boolean>();
    private StatModel webAppStats;

    /**
     * @return the allStats
     */
    public StatModel getAllStats() {
        return allStats;
    }

    /**
     * @param allStats
     *        the allStats to set
     */
    public void setAllStats(StatModel allStats) {
        this.allStats = allStats;
    }

    /**
     * @return the batchStats
     */
    public StatModel getBatchStats() {
        return batchStats;
    }

    /**
     * @param batchStats
     *        the batchStats to set
     */
    public void setBatchStats(StatModel batchStats) {
        this.batchStats = batchStats;
    }

    /**
     * @return the webAppStats
     */
    public StatModel getWebAppStats() {
        return webAppStats;
    }

    /**
     * @param webAppStats
     *        the webAppStats to set
     */
    public void setWebAppStats(StatModel webAppStats) {
        this.webAppStats = webAppStats;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer builder = new StringBuffer();
        builder.append("SystemStatsModel [allStats=");
        builder.append(allStats);
        builder.append(", batchStats=");
        builder.append(batchStats);
        builder.append(", webAppStats=");
        builder.append(webAppStats);
        builder.append("]");
        return builder.toString();
    }

    /**
     * @return the batchStates
     */
    public Map<Long, Boolean> getBatchStates() {
        return batchStates;
    }

    /**
     * @param batchStates
     *        the batchStates to set
     */
    public void setBatchStates(Map<Long, Boolean> batchStates) {
        this.batchStates = batchStates;
    }

}// end SystemStatsModel
