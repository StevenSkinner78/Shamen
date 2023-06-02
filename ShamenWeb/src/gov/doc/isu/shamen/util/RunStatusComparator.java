package gov.doc.isu.shamen.util;

import java.util.Comparator;

import gov.doc.isu.shamen.models.RunStatusModel;

/**
 * This <code>RunStatusComparator</code> is used for sorting a lists of <code>RunStatusModel</code>'s. Currently this class is set up to sort by any of the following properties:
 * <ul>
 * <li>runStatus.start</li>
 * <li>runStatus.createTime</li>
 * </ul>
 * 
 * @author <strong>Steven Skinner</strong> JCCC, Sep 10, 2015
 */
public class RunStatusComparator implements Comparator<RunStatusModel> {

    /**
     * Default Constructor
     */
    public RunStatusComparator() {}

    /**
     * Sorts <code>RunStatusModel</code>'s by 1) start time
     * 
     * @param o1
     *        the <code>RunStatusModel</code> to compare
     * @param o2
     *        the <code>RunStatusModel</code> to compare
     * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
     */
    @Override
    public int compare(RunStatusModel o1, RunStatusModel o2) {
        int result = 0;
        if(o1.getStart().equalsIgnoreCase("") && o2.getStart().equalsIgnoreCase("")){
            result = compareCreateTime(o1, o2);
        }else if(o1.getStart().equalsIgnoreCase("")){
            result = -1;
        }else if(o2.getStart().equalsIgnoreCase("")){
            result = 1;
        }else if(!ShamenUtil.getSqlTimestamp(o1.getStart()).equals(ShamenUtil.getSqlTimestamp(o2.getStart()))){
            result = ShamenUtil.getSqlTimestamp(o1.getStart()).compareTo(ShamenUtil.getSqlTimestamp(o2.getStart()));
        }else{
            result = compareCreateTime(o1, o2);
        } // end if
        return result;
    }

    /**
     * Sorts <code>RunStatusModel</code>'s by 1) create time
     * 
     * @param o1
     *        the <code>RunStatusModel</code> to compare
     * @param o2
     *        the <code>RunStatusModel</code> to compare
     * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
     */
    private int compareCreateTime(RunStatusModel o1, RunStatusModel o2) {
        int result = 0;
        if(o1.getCreateTime() == null && o2.getCreateTime() == null){
            result = 0;
        }else if(o1.getCreateTime() == null){
            result = -1;
        }else if(o2.getCreateTime() == null){
            result = 1;
        }else if(!o1.getCreateTime().equals(o2.getCreateTime())){
            result = o1.getCreateTime().compareTo(o2.getCreateTime());
        } // end if
        return result;
    }

}
