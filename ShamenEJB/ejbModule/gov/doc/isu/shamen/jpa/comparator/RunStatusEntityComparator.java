package gov.doc.isu.shamen.jpa.comparator;

import java.util.Comparator;

import gov.doc.isu.shamen.ejb.util.ShamenEJBUtil;
import gov.doc.isu.shamen.jpa.entity.RunStatusEntity;

/**
 * This <code>RunStatusEntityComparator</code> is used for sorting a lists of <code>RunStatusEntity</code>'s. Currently this class is set up to sort by any of the following properties:
 * <ul>
 * <li>runStatusEntity.startTime</li>
 * <li>runStatusEntity.common.createTime</li>
 * </ul>
 * 
 * @author Steven Skinner JCCC; November 11, 2015
 */
public class RunStatusEntityComparator implements Comparator<RunStatusEntity> {
    private static java.sql.Timestamp DEFAULT_TIMESTAMP;

    /**
     * Default Constructor
     */
    public RunStatusEntityComparator() {
        super();
        DEFAULT_TIMESTAMP = ShamenEJBUtil.getDefaultTimeStamp();
    }

    /**
     * Sorts <code>RunStatusEntity</code>'s by 1) start time
     * 
     * @param o1
     *        the <code>RunStatusEntity</code> to compare
     * @param o2
     *        the <code>RunStatusEntity</code> to compare
     * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
     */
    @Override
    public int compare(RunStatusEntity o1, RunStatusEntity o2) {
        int result = 0;
        if(o1.getStartTime().equals(DEFAULT_TIMESTAMP) && o2.getStartTime().equals(DEFAULT_TIMESTAMP)){
            result = compareCreateTime(o1, o2);
        }else if(o1.getStartTime().equals(DEFAULT_TIMESTAMP)){
            result = -1;
        }else if(o2.getStartTime().equals(DEFAULT_TIMESTAMP)){
            result = 1;
        }else if(!o1.getStartTime().equals(o2.getStartTime())){
            result = o1.getStartTime().compareTo(o2.getStartTime());
        }else{
            result = compareCreateTime(o1, o2);
        } // end if
        return result;
    }

    /**
     * Sorts <code>RunStatusEntity</code>'s by 1) create time
     * 
     * @param o1
     *        the <code>RunStatusEntity</code> to compare
     * @param o2
     *        the <code>RunStatusEntity</code> to compare
     * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
     */
    private int compareCreateTime(RunStatusEntity o1, RunStatusEntity o2) {
        int result = 0;
        if (o1.getCommon().getCreateTime() == null && o2.getCommon().getCreateTime()==null){
            return result;
        }
        if(o1.getCommon().getCreateTime().equals(DEFAULT_TIMESTAMP) && o2.getCommon().getCreateTime().equals(DEFAULT_TIMESTAMP)){
            result = 0;
        }else if(o1.getCommon().getCreateTime().equals(DEFAULT_TIMESTAMP)){
            result = -1;
        }else if(o2.getCommon().getCreateTime().equals(DEFAULT_TIMESTAMP)){
            result = 1;
        }else if(!o1.getCommon().getCreateTime().equals(o2.getCommon().getCreateTime())){
            result = o1.getCommon().getCreateTime().compareTo(o2.getCommon().getCreateTime());
        } // end if
        return result;
    }

}
