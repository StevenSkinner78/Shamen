/**
 * @author <strong>Shane Duncan</strong> JCCC, Oct 6, 2015
 */
package gov.doc.isu.shamen.models;

import java.sql.Timestamp;

/**
 * This class is a model used to store Messages from the DB.
 * 
 * @author <strong>Shane Duncan</strong> JCCC, Oct 6, 2015
 */
public class Status {
    private String status;
    private Timestamp ts;
    private Boolean done = false;
    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }
    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }
    /**
     * @return the ts
     */
    public Timestamp getTs() {
        return ts;
    }
    /**
     * @param ts the ts to set
     */
    public void setTs(Timestamp ts) {
        this.ts = ts;
    }
    /**
     * @return the done
     */
    public Boolean getDone() {
        return done;
    }
    /**
     * @param done the done to set
     */
    public void setDone(Boolean done) {
        this.done = done;
    }    

    
}
