/**
 * @author <strong>Steven Skinner</strong> JCCC, Sep 10, 2015
 */
package gov.doc.isu.shamen.models;

import static gov.doc.isu.dwarf.resources.Constants.NEW_LINE;

import java.util.Iterator;
import java.util.List;

import gov.doc.isu.dwarf.model.CodeModel;
import gov.doc.isu.dwarf.util.AppUtil;

/**
 * Model object to hold the controller record from the DB
 *
 * @author <strong>Steven Skinner</strong> JCCC, Sep 10, 2015
 */
public class ControllerModel extends CodeModel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Long controllerRefId;
    private String address;
    private String name;
    private String defaultAddress;
    private List<BatchAppModel> batchApps;
    private String status;
    private String statusCd;

    /**
     * Default Constructor
     */
    public ControllerModel() {
        super();
    }

    /**
     * Constructor
     *
     * @param name
     *        The controller name
     */
    public ControllerModel(String name) {
        super();
        this.name = name;
    }

    /**
     * Constructor for Controller Model as a Code Model
     *
     * @param code
     *        the controller ref id as a string
     * @param description
     *        the controller name
     */
    public ControllerModel(String code, String description) {
        super(code, description);
    }

    /**
     * @return the controllerRefId
     */
    public Long getControllerRefId() {
        return controllerRefId;
    }

    /**
     * @param controllerRefId
     *        the controllerRefId to set
     */
    public void setControllerRefId(Long controllerRefId) {
        this.controllerRefId = controllerRefId;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address
     *        the address to set
     */
    public void setAddress(String address) {
        this.address = address;
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
     * @return the defaultAddress
     */
    public String getDefaultAddress() {
        return defaultAddress;
    }

    /**
     * @param defaultAddress
     *        the defaultAddress to set
     */
    public void setDefaultAddress(String defaultAddress) {
        this.defaultAddress = defaultAddress;
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
     * @param batchApp
     *        batchApp
     */
    public void addBatchApp(BatchAppModel batchApp) {
        getBatchApps().add(batchApp);
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status
     *        the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the statusCd
     */
    public String getStatusCd() {
        return statusCd;
    }

    /**
     * @param statusCd
     *        the statusCd to set
     */
    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer builder = new StringBuffer();
        builder.append("ControllerModel [").append(NEW_LINE);
        builder.append("controllerRefId=");
        builder.append(controllerRefId).append(NEW_LINE);
        builder.append("address=");
        builder.append(address).append(NEW_LINE);
        builder.append("name=");
        builder.append(name).append(NEW_LINE);
        builder.append("defaultAddress=");
        builder.append(defaultAddress).append(NEW_LINE);
        builder.append("statusCd=");
        builder.append(statusCd).append(NEW_LINE);
        builder.append("status=");
        builder.append(status).append(NEW_LINE);
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
        }
        builder.append(super.toString());
        builder.append("]");
        return builder.toString();
    }
}
