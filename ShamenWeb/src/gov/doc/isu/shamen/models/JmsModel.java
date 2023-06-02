/**
 * @author <strong>Steven Skinner</strong> JCCC, Sep 10, 2015
 */
package gov.doc.isu.shamen.models;

/**
 * Model object to hold the JMS status.
 * 
 * @author <strong>Shane Duncan</strong> JCCC, March 16, 2017
 */
public class JmsModel {

    private Boolean status;

    /**
     * @return the status
     */
    public Boolean getStatus() {
        return status;
    }

    /**
     * @param status
     *        the status to set
     */
    public void setStatus(Boolean status) {
        this.status = status;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer builder = new StringBuffer();
        builder.append("JmsModel [status=");
        builder.append(status);
        builder.append("]");
        return builder.toString();
    }

}
