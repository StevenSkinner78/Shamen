/**
 * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2015
 */
package gov.doc.isu.shamen.processors;

import gov.doc.isu.shamen.jms.models.JmsBatchApp;
import gov.doc.isu.shamen.jms.models.JmsBatchAppCollection;

/**
 * Contains all the object conversion methods for the Shamen Controller
 * 
 * @author <strong>Shane Duncan</strong> JCCC, Sep 10, 2016
 */
public class ShamenObjectConverter {
    private static final String MY_CLASS_NAME = "gov.doc.isu.shamen.processors.ShamenObjectConverter";

    public static JmsBatchApp collectionToMainBatch(JmsBatchAppCollection col) {
        if(col == null){
            return null;
        }// end if
        JmsBatchApp batchApp = new JmsBatchApp();
        batchApp.setBatchAppRefId(col.getMainBatchAppRefId());
        batchApp.setCreateTs(col.getCreateTs());
        batchApp.setCreateUserRefId(col.getCreateUserRefId());
        batchApp.setDeleteInd(col.getDeleteInd());
        batchApp.setDescription(col.getDescription());
        batchApp.setName(col.getName());
        batchApp.setSchedule(col.getSchedule());
        batchApp.setUpdateTs(col.getUpdateTs());
        batchApp.setUpdateUserRefId(col.getUpdateUserRefId());
        return batchApp;
    }// end collectionToMainBatch

}// end class
