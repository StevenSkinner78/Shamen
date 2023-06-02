/**
 * @author <strong>Shane Duncan</strong> JCCC, Oct 6, 2015
 */
package gov.doc.isu.shamen.models;

import gov.doc.isu.shamen.interfaces.Scheduleable;

import java.sql.Timestamp;

/**
 * This class is a model used to store Messages from the DB.
 * 
 * @author <strong>Shane Duncan</strong> JCCC, Oct 6, 2015
 */
public class ScheduleEntry implements Comparable<ScheduleEntry>{
    private Scheduleable scheduleable;
    private Timestamp startTs;
    /**
     * @return the scheduleable
     */
    public Scheduleable getScheduleable() {
        return scheduleable;
    }
    /**
     * @param scheduleable the scheduleable to set
     */
    public void setScheduleable(Scheduleable scheduleable) {
        this.scheduleable = scheduleable;
    }
    /**
     * @return the startTs
     */
    public Timestamp getStartTs() {
        return startTs;
    }
    /**
     * @param startTs the startTs to set
     */
    public void setStartTs(Timestamp startTs) {
        this.startTs = startTs;
    }
    @Override
    public int compareTo(ScheduleEntry o) {        
        return startTs.compareTo(o.getStartTs());
    }

    
}
