/**
 * @author <strong>Shane Duncan</strong> JCCC, Oct 6, 2015
 */
package gov.doc.isu.shamen.models;

/**
 * @author <strong>Shane Duncan</strong> JCCC, Oct 6, 2015
 */
public class Properties {
    private String controllerName = null;
    private String controllerStayAlive = null;
    private String jmsStayAlive = null;
    private String scheduleRefresh = null;

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Oct 6, 2015
     * @return the controllerName
     */
    public String getControllerName() {
        return controllerName;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Oct 6, 2015
     * @param controllerName
     *        the controllerName to set
     */
    public void setControllerName(String controllerName) {
        this.controllerName = controllerName;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Oct 6, 2015
     * @return the controllerStayAlive
     */
    public String getControllerStayAlive() {
        return controllerStayAlive;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Oct 6, 2015
     * @param controllerStayAlive
     *        the controllerStayAlive to set
     */
    public void setControllerStayAlive(String controllerStayAlive) {
        this.controllerStayAlive = controllerStayAlive;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Oct 6, 2015
     * @return the jmsStayAlive
     */
    public String getJmsStayAlive() {
        return jmsStayAlive;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Oct 6, 2015
     * @param jmsStayAlive
     *        the jmsStayAlive to set
     */
    public void setJmsStayAlive(String jmsStayAlive) {
        this.jmsStayAlive = jmsStayAlive;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Oct 6, 2015
     * @return the scheduleRefresh
     */
    public String getScheduleRefresh() {
        return scheduleRefresh;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Oct 6, 2015
     * @param scheduleRefresh
     *        the scheduleRefresh to set
     */
    public void setScheduleRefresh(String scheduleRefresh) {
        this.scheduleRefresh = scheduleRefresh;
    }

    /**
     * This method returns the controllerStayAlive as a boolean.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Oct 6, 2015
     * @return Boolean
     */
    public Boolean isControllerStayAlive() {
        Boolean value = false;
        if(null != controllerStayAlive){
            if("true".equals(controllerStayAlive.trim())){
                value = true;
            }else{
                value = false;
            }// end else-if
        }// end if
        return value;
    }// end isControllerStayAlive

    /**
     * This method returns the jmsStayAlive as a boolean.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Oct 6, 2015
     * @return Boolean
     */
    public Boolean isJmsStayAlive() {
        Boolean value = false;
        if(null != jmsStayAlive){
            if("true".equals(jmsStayAlive.trim())){
                value = true;
            }else{
                value = false;
            }// end else-if
        }// end if
        return value;
    }// end isJmsStayAlive

    /**
     * This method returns the scheduleRefresh as a boolean.
     * 
     * @author <strong>Shane Duncan</strong> JCCC, Oct 6, 2015
     * @return Boolean
     */
    public Boolean isScheduleRefresh() {
        Boolean value = false;
        if(null != scheduleRefresh){
            if("true".equals(scheduleRefresh.trim())){
                value = true;
            }else{
                value = false;
            }// end else-if
        }// end if
        return value;
    }// end isScheduleRefresh

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer builder = new StringBuffer();
        builder.append("Properties [controllerName=");
        builder.append(controllerName);
        builder.append(", controllerStayAlive=");
        builder.append(controllerStayAlive);
        builder.append(", jmsStayAlive=");
        builder.append(jmsStayAlive);
        builder.append(", scheduleRefresh=");
        builder.append(scheduleRefresh);
        builder.append("]");
        return builder.toString();
    }
}
