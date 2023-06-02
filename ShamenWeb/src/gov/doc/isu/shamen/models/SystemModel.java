/**
 * @author <strong>Shane Duncan</strong> JCCC, July 17, 2017
 */
package gov.doc.isu.shamen.models;

import static gov.doc.isu.dwarf.resources.Constants.NEW_LINE;

import java.util.Iterator;
import java.util.List;

import gov.doc.isu.dwarf.model.CommonModel;
import gov.doc.isu.dwarf.util.AppUtil;

/**
 * Model object to hold the system record from the DB
 * 
 * @author <strong>Shane Duncan</strong> JCCC, July 17, 2017
 */
public class SystemModel extends CommonModel {

    private static final long serialVersionUID = -1222682569278763434L;
    private Long systemRefId;
    private String name;
    private String systemDesc;
    private SystemStatsModel stat;
    private List<BatchAppModel> batchApps;
    private List<ApplicationModel> applications;
    private int nbrBatchApps;
    private int nbrApplications;
    private int nbrTotal;
    private AuthorizedUserModel pointOfContact;

    /**
     * Default Constructor
     */
    public SystemModel() {
        super();
    }

    /**
     * Constructor
     * 
     * @param name
     *        The controller name
     */
    public SystemModel(String name) {
        super();
        this.name = name;
    }

    /**
     * Constructor
     * 
     * @param name
     *        The system name
     * @param systemDesc
     *        The system description
     */
    public SystemModel(String name, String systemDesc) {
        super();
        this.name = name;
        this.systemDesc = systemDesc;
    }

    /**
     * @param batchApp
     *        batchApp
     */
    public void addBatchApp(BatchAppModel batchApp) {
        getBatchApps().add(batchApp);
    }

    /**
     * @return the systemRefId
     */
    public Long getSystemRefId() {
        return systemRefId;
    }

    /**
     * @param systemRefId
     *        the systemRefId to set
     */
    public void setSystemRefId(Long systemRefId) {
        this.systemRefId = systemRefId;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *        the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the batchApps
     */
    public List<BatchAppModel> getBatchApps() {
        return batchApps;
    }

    /**
     * @param batchApps
     *        the batchApps to set
     */
    public void setBatchApps(List<BatchAppModel> batchApps) {
        this.batchApps = batchApps;
    }

    /**
     * @return the applications
     */
    public List<ApplicationModel> getApplications() {
        return applications;
    }

    /**
     * @param applications
     *        the applications to set
     */
    public void setApplications(List<ApplicationModel> applications) {
        this.applications = applications;
    }

    /**
     * @return the systemDesc
     */
    public String getSystemDesc() {
        return systemDesc;
    }

    /**
     * @param systemDesc
     *        the systemDesc to set
     */
    public void setSystemDesc(String systemDesc) {
        this.systemDesc = systemDesc;
    }

    /**
     * @return the stat
     */
    public SystemStatsModel getStat() {
        return stat;
    }

    /**
     * @param stat
     *        the stat to set
     */
    public void setStat(SystemStatsModel stat) {
        this.stat = stat;
    }

    /**
     * @return the nbrBatchApps
     */
    public int getNbrBatchApps() {
        return nbrBatchApps;
    }

    /**
     * @param nbrBatchApps
     *        the nbrBatchApps to set
     */
    public void setNbrBatchApps(int nbrBatchApps) {
        this.nbrBatchApps = nbrBatchApps;
    }

    /**
     * @return the nbrApplications
     */
    public int getNbrApplications() {
        return nbrApplications;
    }

    /**
     * @param nbrApplications
     *        the nbrApplications to set
     */
    public void setNbrApplications(int nbrApplications) {
        this.nbrApplications = nbrApplications;
    }

    /**
     * @return the nbrTotal
     */
    public int getNbrTotal() {
        return nbrTotal;
    }

    /**
     * @param nbrTotal
     *        the nbrTotal to set
     */
    public void setNbrTotal(int nbrTotal) {
        this.nbrTotal = nbrTotal;
    }

    /**
     * @return the pointOfContact
     */
    public AuthorizedUserModel getPointOfContact() {
        return pointOfContact;
    }

    /**
     * @param pointOfContact
     *        the pointOfContact to set
     */
    public void setPointOfContact(AuthorizedUserModel pointOfContact) {
        this.pointOfContact = pointOfContact;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer builder = new StringBuffer();
        builder.append("SystemModel [").append(NEW_LINE);
        builder.append("systemRefId=");
        builder.append(systemRefId).append(NEW_LINE);
        builder.append("name=");
        builder.append(name).append(NEW_LINE);
        builder.append("desc=");
        builder.append(systemDesc).append(NEW_LINE);
        builder.append("batchApps=");
        if(AppUtil.isEmpty(batchApps)){
            builder.append("none").append(NEW_LINE);
        }else{
            String batches = "{";
            for(Iterator<BatchAppModel> iter = batchApps.iterator();iter.hasNext();){
                batches += iter.next().getName();
                if(iter.hasNext()){
                    batches += ", ";
                }// end if
            }// end for
            batches += "}";
            builder.append(batches).append(NEW_LINE);
        }// end if-else
        builder.append(name).append(NEW_LINE);
        builder.append("applications=");
        if(AppUtil.isEmpty(applications)){
            builder.append("none").append(NEW_LINE);
        }else{
            String batches = "{";
            for(Iterator<BatchAppModel> iter = batchApps.iterator();iter.hasNext();){
                batches += iter.next().getName();
                if(iter.hasNext()){
                    batches += ", ";
                }// end if
            }// end for
            batches += "}";
            builder.append(batches).append(NEW_LINE);
        }// end if-else
        builder.append(super.toString());
        builder.append("]");
        return builder.toString();
    }// end toString
}// end SystemModel
