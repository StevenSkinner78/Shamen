/**
 * @author <strong>Shane Duncan</strong> JCCC, July 17, 2017
 */
package gov.doc.isu.shamen.models;

/**
 * Model object to hold the statistic record from the DB
 * 
 * @author <strong>Shane Duncan</strong> JCCC, July 17, 2017
 */
public class StatModel implements java.io.Serializable {

    private static final long serialVersionUID = -1222682569278763434L;
    private int totalApps;
    private int niceApps;
    private int naughtyApps;
    private int suspendedApps;
    private int infoApps;
    private int activeApps;
    private int unreportableApps;
    private int pendingApps;
    private int runningApps;
    private int offScheduleApps;
    private int inActiveApps;
    private int noScheduleApps;

    /**
     * @return the totalApps
     */
    public int getTotalApps() {
        return totalApps;
    }

    /**
     * @param totalApps
     *        the totalApps to set
     */
    public void setTotalApps(int totalApps) {
        this.totalApps = totalApps;
    }

    /**
     * @return the naughtyApps
     */
    public int getNaughtyApps() {
        return naughtyApps;
    }

    /**
     * @return the percentage of naughty apps
     */
    public String getNaughtyAppPercentage() {
        Float percentage = 0F;
        String percent = "0";
        if(totalApps > 0){

            percentage = Float.valueOf(naughtyApps) / Float.valueOf(totalApps) * Float.valueOf("100");

            if(percentage > 0){
                percent = String.format("%.1f", percentage);
            }// end if
        }// end if
        return percent;
    }// end getNaughtyAppPercentage

    /**
     * @return the percentage of naughty apps formatted for pie chart display. This includes formatting marks for the jsp.
     */
    public String getNaughtyAppPercentageForPie() {
        String percentage = getNaughtyAppPercentage();
        if(percentage.equals("100.0")){
            percentage = "99.9";
        }
        return "-" + percentage + "s";
    }// end getNaughtyAppPercentageForPie

    /**
     * @return the percentage of nice apps formatted for pie screen display
     */
    public String getNiceAppPercentage() {
        Float percentage = 0F;
        if(niceApps != 0){
            percentage = Float.valueOf(niceApps) / Float.valueOf(totalApps) * Float.valueOf("100");
        }
        String percent = "0";
        percent = String.format("%.1f", percentage);

        return percent;
    }// end getNiceAppPercentage

    /**
     * @param naughtyApps
     *        the naughtyApps to set
     */
    public void setNaughtyApps(int naughtyApps) {
        this.naughtyApps = naughtyApps;
    }

   
    /**
     * @return the niceApps
     */
    public int getNiceApps() {
        return niceApps;
    }

    /**
     * @param niceApps
     *        the niceApps to set
     */
    public void setNiceApps(int niceApps) {
        this.niceApps = niceApps;
    }

    /**
     * @return the suspendedApps
     */
    public int getSuspendedApps() {
        return suspendedApps;
    }

    /**
     * @param suspendedApps the suspendedApps to set
     */
    public void setSuspendedApps(int suspendedApps) {
        this.suspendedApps = suspendedApps;
    }

    /**
     * @return the infoApps
     */
    public int getInfoApps() {
        return infoApps;
    }

    /**
     * @param infoApps the infoApps to set
     */
    public void setInfoApps(int infoApps) {
        this.infoApps = infoApps;
    }

    /**
     * @return the activeApps
     */
    public int getActiveApps() {
        return activeApps;
    }

    /**
     * @param activeApps the activeApps to set
     */
    public void setActiveApps(int activeApps) {
        this.activeApps = activeApps;
    }

    /**
     * @return the unreportableApps
     */
    public int getUnreportableApps() {
        return unreportableApps;
    }

    /**
     * @param unreportableApps the unreportableApps to set
     */
    public void setUnreportableApps(int unreportableApps) {
        this.unreportableApps = unreportableApps;
    }

    

    /**
     * @return the pendingApps
     */
    public int getPendingApps() {
        return pendingApps;
    }

    /**
     * @param pendingApps the pendingApps to set
     */
    public void setPendingApps(int pendingApps) {
        this.pendingApps = pendingApps;
    }

    /**
     * @return the runningApps
     */
    public int getRunningApps() {
        return runningApps;
    }

    /**
     * @param runningApps the runningApps to set
     */
    public void setRunningApps(int runningApps) {
        this.runningApps = runningApps;
    }

    /**
     * @return the offScheduleApps
     */
    public int getOffScheduleApps() {
        return offScheduleApps;
    }

    /**
     * @param offScheduleApps the offScheduleApps to set
     */
    public void setOffScheduleApps(int offScheduleApps) {
        this.offScheduleApps = offScheduleApps;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer builder = new StringBuffer();
        builder.append("StatModel [totalApps=");
        builder.append(totalApps);
        builder.append(", niceApps=");
        builder.append(niceApps);
        builder.append(", naughtyApps=");
        builder.append(naughtyApps);
        builder.append(", suspendedApps=");
        builder.append(suspendedApps);
        builder.append(", infoApps=");
        builder.append(infoApps);
        builder.append(", activeApps=");
        builder.append(activeApps);
        builder.append(", unreportableApps=");
        builder.append(unreportableApps);
        builder.append(", pendingApps=");
        builder.append(pendingApps);
        builder.append(", runningApps=");
        builder.append(runningApps);
        builder.append(", offScheduleApps=");
        builder.append(offScheduleApps);
        builder.append("]");
        return builder.toString();
    }

    /**
     * @return the inActiveApps
     */
    public int getInActiveApps() {
        return inActiveApps;
    }

    /**
     * @param inActiveApps the inActiveApps to set
     */
    public void setInActiveApps(int inActiveApps) {
        this.inActiveApps = inActiveApps;
    }

    /**
     * @return the noScheduleApps
     */
    public int getNoScheduleApps() {
        return noScheduleApps;
    }

    /**
     * @param noScheduleApps the noScheduleApps to set
     */
    public void setNoScheduleApps(int noScheduleApps) {
        this.noScheduleApps = noScheduleApps;
    }

}// end StatModel
