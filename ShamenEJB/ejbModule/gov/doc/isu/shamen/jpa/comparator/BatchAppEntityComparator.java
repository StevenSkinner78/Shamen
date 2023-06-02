package gov.doc.isu.shamen.jpa.comparator;

import java.util.Comparator;

import gov.doc.isu.shamen.ejb.util.ShamenEJBUtil;
import gov.doc.isu.shamen.jpa.entity.BatchAppEntity;
import gov.doc.isu.shamen.jpa.entity.RunStatusEntity;

/**
 * This <code>RunStatusEntityComparator</code> is used for sorting a lists of <code>RunStatusEntity</code>'s. Currently this class is set up to sort by any of the following properties:
 * <ul>
 * <li>batchAppEntity.batchName</li>
 * </ul>
 * 
 * @author Steven Skinner JCCC; Jan 06, 2021
 */
public class BatchAppEntityComparator implements Comparator<BatchAppEntity> {

    /**
     * Default Constructor
     */
    public BatchAppEntityComparator() {
        super();
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
    public int compare(BatchAppEntity o1, BatchAppEntity o2) {
        int result = 0;
        if(o1.getBatchName().equalsIgnoreCase(o2.getBatchName())){
            return result;
        }else{
            result = o1.getBatchName().compareToIgnoreCase(o2.getBatchName());
        } // end if
        return result;
    }

}
