package gov.doc.isu.shamen.form;

import static gov.doc.isu.dwarf.resources.Constants.NEW_LINE;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

import gov.doc.isu.dwarf.core.AbstractForm;
import gov.doc.isu.dwarf.model.CodeModel;
import gov.doc.isu.shamen.models.SystemModel;
import net.sf.json.JSONArray;

/**
 * Form bean for the Shamen System Information for the Shamen Web Interface application.
 * 
 * @author <strong>Shane Duncan</strong>
 */
public class SystemForm extends AbstractForm {

    private static final long serialVersionUID = 3772594624482339396L;
    private SystemModel system;
    private String caller;
    private String[] selectedBatches;
    private String[] selectedApplications;
    private List<CodeModel> pocList;
    private JSONArray systemData;

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        super.reset(mapping, request);
        selectedBatches = null;
        selectedApplications = null;
        systemData = null;
    }// end reset

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuffer builder = new StringBuffer();
        builder.append("SystemForm [system=");
        builder.append(system).append(NEW_LINE);
        builder.append("selectedBatches=");
        builder.append(Arrays.toString(selectedBatches)).append(NEW_LINE);
        builder.append("selectedApplications=");
        builder.append(Arrays.toString(selectedApplications)).append(NEW_LINE);
        builder.append(super.toString());
        builder.append("]");
        return builder.toString();
    }// end toString

    /**
     * @return the caller
     */
    public String getCaller() {
        return caller;
    }

    /**
     * @param caller
     *        the caller to set
     */
    public void setCaller(String caller) {
        this.caller = caller;
    }

    /**
     * @return the systemModel
     */
    public SystemModel getSystem() {
        return system;
    }

    /**
     * @param system
     *        the system to set
     */
    public void setSystem(SystemModel system) {
        this.system = system;
    }

    /**
     * @return the selectedBatches
     */
    public String[] getSelectedBatches() {
        return selectedBatches;
    }

    /**
     * @param selectedBatches
     *        the selectedBatches to set
     */
    public void setSelectedBatches(String[] selectedBatches) {
        this.selectedBatches = selectedBatches;
    }

    /**
     * @return the selectedApplications
     */
    public String[] getSelectedApplications() {
        return selectedApplications;
    }

    /**
     * @param selectedApplications
     *        the selectedApplications to set
     */
    public void setSelectedApplications(String[] selectedApplications) {
        this.selectedApplications = selectedApplications;
    }

    /**
     * @return the pocList
     */
    public List<CodeModel> getPocList() {
        return pocList;
    }

    /**
     * @param pocList
     *        the pocList to set
     */
    public void setPocList(List<CodeModel> pocList) {
        this.pocList = pocList;
    }

    /**
     * @return the systemData
     */
    public JSONArray getSystemData() {
        return systemData;
    }

    /**
     * @param systemData
     *        the systemData to set
     */
    public void setSystemData(JSONArray systemData) {
        this.systemData = systemData;
    }

}// end SystemForm
