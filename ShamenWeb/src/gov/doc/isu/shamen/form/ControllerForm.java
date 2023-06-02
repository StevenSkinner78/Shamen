package gov.doc.isu.shamen.form;

import static gov.doc.isu.dwarf.resources.Constants.NEW_LINE;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import gov.doc.isu.dwarf.core.AbstractForm;
import gov.doc.isu.shamen.models.ControllerModel;

/**
 * Form bean for the Shamen Controller Information for the Shamen Web Interface application.
 *
 * @author <strong>Steven L. Skinner</strong>
 */
public class ControllerForm extends AbstractForm {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private ControllerModel controller;
    private String caller;
    private List<ControllerModel> controllerList;
    private String[] selectedBatches;

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        super.reset(mapping, request);
        selectedBatches = null;
    }// end reset

    /**
     * @return the controller
     */
    public ControllerModel getController() {
        return controller;
    }

    /**
     * @param controller
     *        the controller to set
     */
    public void setController(ControllerModel controller) {
        this.controller = controller;
    }

    /**
     * @return the controllerList
     */
    public List<ControllerModel> getControllerList() {
        return controllerList;
    }

    /**
     * @param controllerList
     *        the controllerList to set
     */
    public void setControllerList(List<ControllerModel> controllerList) {
        this.controllerList = controllerList;
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
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuffer builder = new StringBuffer();
        builder.append("ControllerForm [controller=");
        builder.append(controller).append(NEW_LINE);
        builder.append("controllerList=");
        builder.append(controllerList).append(NEW_LINE);
        builder.append("selectedBatches=");
        builder.append(Arrays.toString(selectedBatches)).append(NEW_LINE);
        builder.append("queueList=");
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

    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        return super.validate(mapping, request);
    }


}// end ControllerForm
