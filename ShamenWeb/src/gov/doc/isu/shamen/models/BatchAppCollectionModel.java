/**
 * @author <strong>Steven Skinner</strong> JCCC, Sep 10, 2015
 */
package gov.doc.isu.shamen.models;

import gov.doc.isu.dwarf.model.CommonModel;

/**
 * Model object to hold the Batch App Collection record.
 * 
 * @author <strong>Steven Skinner</strong> JCCC, Aug 15, 2016
 */
public class BatchAppCollectionModel extends CommonModel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Long collectionRefId;
    private BatchAppModel mainBatchApp;
    private BatchAppModel assocBatchApp;
    private Long runSeq;

    /**
     * Default Constructor
     */
    public BatchAppCollectionModel() {

    }

    /**
     * Four paramater constructor.
     * 
     * @param collectionRefId
     *        the primary ref id for the collection record
     * @param mainBatchApp
     *        the batch app ref id for the batch app collection
     * @param assocBatchApp
     *        the ref id for the batch app in the collection
     * @param runSeq
     *        the run sequence number that the assocBatchApp should be run in.
     */
    public BatchAppCollectionModel(Long collectionRefId, BatchAppModel mainBatchApp, BatchAppModel assocBatchApp, Long runSeq) {
        super();
        this.collectionRefId = collectionRefId;
        this.mainBatchApp = mainBatchApp;
        this.assocBatchApp = assocBatchApp;
        this.runSeq = runSeq;
    }

    /**
     * @return the collectionRefId
     */
    public Long getCollectionRefId() {
        return collectionRefId;
    }

    /**
     * @param collectionRefId
     *        the collectionRefId to set
     */
    public void setCollectionRefId(Long collectionRefId) {
        this.collectionRefId = collectionRefId;
    }

    /**
     * @return the mainBatchApp
     */
    public BatchAppModel getMainBatchApp() {
        return mainBatchApp;
    }

    /**
     * @param mainBatchApp
     *        the mainBatchApp to set
     */
    public void setMainBatchApp(BatchAppModel mainBatchApp) {
        this.mainBatchApp = mainBatchApp;
    }

    /**
     * @return the assocBatchApp
     */
    public BatchAppModel getAssocBatchApp() {
        return assocBatchApp;
    }

    /**
     * @param assocBatchApp
     *        the assocBatchApp to set
     */
    public void setAssocBatchApp(BatchAppModel assocBatchApp) {
        this.assocBatchApp = assocBatchApp;
    }

    /**
     * @return the runSeq
     */
    public Long getRunSeq() {
        return runSeq;
    }

    /**
     * @param runSeq
     *        the runSeq to set
     */
    public void setRunSeq(Long runSeq) {
        this.runSeq = runSeq;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer builder = new StringBuffer();
        builder.append("BatchAppCollectionModel [collectionRefId=");
        builder.append(collectionRefId);
        builder.append(", mainBatchApp=");
        builder.append(mainBatchApp);
        builder.append(", assocBatchApp=");
        builder.append(assocBatchApp);
        builder.append(", runSeq=");
        builder.append(runSeq);
        builder.append("]");
        return builder.toString();
    }

}
